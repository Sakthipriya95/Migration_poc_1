/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.compli;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringJoiner;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.A2LFileInfoFetcher;
import com.bosch.caltool.icdm.bo.a2l.ParameterLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.cdr.CDRRuleUtil;
import com.bosch.caltool.icdm.bo.cdr.CheckSSDOutputData;
import com.bosch.caltool.icdm.bo.cdr.CheckSSDResultParam;
import com.bosch.caltool.icdm.bo.cdr.ComPkgBCModelLoader;
import com.bosch.caltool.icdm.bo.cdr.FeatureAttributeAdapterNew;
import com.bosch.caltool.icdm.bo.cdr.SSDServiceHandler;
import com.bosch.caltool.icdm.bo.cdr.review.RulesValidator;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.common.util.CaldataFileParserHandler;
import com.bosch.caltool.icdm.common.util.CaldataFileParserHandler.CALDATA_FILE_TYPE;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.common.util.DateUtil;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.logger.ParserLogger;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.COMPLI_RESULT_FLAG;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.QSSD_RESULT_FLAG;
import com.bosch.caltool.icdm.model.cdr.CompliCheckSSDInputModel;
import com.bosch.caltool.icdm.model.cdr.CompliReviewInputMetaData;
import com.bosch.caltool.icdm.model.cdr.CompliReviewOutputData;
import com.bosch.caltool.icdm.model.cdr.CompliReviewResponse;
import com.bosch.caltool.icdm.model.cdr.MappedBcsData;
import com.bosch.caltool.icdm.model.cdr.UnMappedBcsData;
import com.bosch.caltool.icdm.model.cdr.compli.CompliRvwHexParam;
import com.bosch.caltool.icdm.model.ssdfeature.Feature;
import com.bosch.checkssd.reports.summaryReport.CheckSSDBatchRun;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.ComPkgBcModel;
import com.bosch.ssd.icdm.model.FeaValModel;
import com.bosch.ssd.icdm.model.FeatureValueModel;
import com.bosch.ssd.icdm.model.SSDMessage;
import com.bosch.ssd.icdm.model.SSDMessageOptions;

/**
 * Business class to fetch the parse the a2l file byte array and fetch the compli-parameter from the a2l file model
 *
 * @author svj7cob
 */
// Task 263282
public class CompliDataReview extends AbstractSimpleBusinessObject {

  /**
   * server location
   */
  public static final String SERVER_PATH = Messages.getString("SERVICE_WORK_DIR") + "//COMPLI_REVIEWS//";

  /**
   * a2l file info object
   */
  private A2LFileInfo a2lFileInfo;
  /**
   * map key as Hex file name and value as Cal data map.
   */
  private final Map<String, Map<String, CalData>> hexFileCalDataMap = new ConcurrentHashMap<>();

  /**
   * map key as Hex file name and value as Warnings map.
   */
  private final Map<String, Map<String, List<String>>> parserWarningsMap = new ConcurrentHashMap<>();


  private final Map<Long, CompliReviewOutputData> compliRvwOutputMap = new ConcurrentHashMap<>();

  /**
   * Contains compli and qssd params
   */
  private Set<String> allParamSet;

  /**
   * the variable for all compliance labels
   */
  private Set<String> compliParamSet;

  /**
   * qssd param set
   */
  private Set<String> qssdParamSet;

  /**
   * the set of comp. pkg model
   */
  private Map<String, ComPkgBcModel> bcMap = new HashMap<>();

  private final Map<String, UnMappedBcsData> unmappedBcs = new HashMap<>();

  /**
   * Key - Mapped BC name, Value - {@link MappedBcsData}
   */
  private final Map<String, MappedBcsData> mappedBcsMap = new HashMap<>();

  /**
   * output folder path
   */
  private String folderPath;


  /**
   * Key - pidc element id - pidc version/pidc variant id Value - Set of AttributeValueModel containing attribute
   * dependencies
   */
  private final Map<Long, Set<AttributeValueModel>> attrValDepModelMap = new HashMap<>();

  private final Map<Long, String> pidcElementNameMap = new HashMap<>();
  private FeatureAttributeAdapterNew featAttrAdap;
  private String currentTimeStamp;

  private boolean isQSSDOnlyRelease;

  private final CompliCheckSSDInputModel checkSSDInputModel = new CompliCheckSSDInputModel();

  /**
   * Class Instance
   *
   * @param serviceData initialise the service transaction
   */
  public CompliDataReview(final ServiceData serviceData) {
    super(serviceData);
  }


  /**
   * @param inputMetaData Review input meta data
   * @param compliReviewHexParamResult {@link CompliReviewHexParamResult}
   * @param hexByteCheckSumMap input hex file byte map for calculating check sum
   * @param isNonSDOMBCRelease
   * @return {@link CompliReviewResponse}
   * @throws IcdmException IcdmException
   */
  public CompliReviewResponse performReview(final CompliReviewInputMetaData inputMetaData,
      final CompliReviewHexParamResult compliReviewHexParamResult, final Map<String, byte[]> hexByteCheckSumMap,
      final boolean isNonSDOMBCRelease)
      throws IcdmException {

    getLogger().info("Starting compliance data review process...");

    CompliReviewResponse resp;
    if (this.compliParamSet.isEmpty() && this.qssdParamSet.isEmpty()) {
      createOutputFolder();
      resp = createDummyOutput(inputMetaData);
    }
    else {
      this.isQSSDOnlyRelease = this.compliParamSet.isEmpty() && !this.qssdParamSet.isEmpty();

      if (!this.isQSSDOnlyRelease && CommonUtils.isNotEmpty(this.compliParamSet)) {
        // BC not required for QSSD only labels - default node is 'Quality Node'
        if (!isNonSDOMBCRelease) {
          fillBcInfo(inputMetaData);

        }
        if (CommonUtils.isNullOrEmpty(this.bcMap) && !isNonSDOMBCRelease) {
          throw new IcdmException("COMPLI_REVIEW.NO_BC_FOR_PVER");
        }
      }
      resp = new CompliReviewResponse();
      resp.setCompliParams(new ArrayList<>(this.compliParamSet));
      resp.setQssdParams(new ArrayList<>(this.qssdParamSet));

      getLogger().debug("Invoking SSD, CheckSSD started ...");

      invokeSsdAndCheckSsd(inputMetaData, resp, compliReviewHexParamResult, hexByteCheckSumMap, isNonSDOMBCRelease);

      getLogger().debug("Invoking SSD, CheckSSD completed.");

      resp.setHexFileOutput(this.compliRvwOutputMap);
      resp.setTimeStampUTC(DateUtil.getCurrentUtcTime(DateFormat.DATE_FORMAT_17));
    }

    getLogger().info("Compliance data review process finished. Compliance parameters = {}, webflowOutputMap size = {}",
        this.compliParamSet.size(), this.compliRvwOutputMap.size());

    return resp;

  }


  /**
   * @param a2lBytes input a2l file byte array
   * @param hexByteMap input hex file byte array
   * @param a2lFileName input a2l file name
   * @return input file folder path
   * @throws IcdmException Exception
   */
  public String createInputDataFolder(final byte[] a2lBytes, final Map<String, byte[]> hexByteMap,
      final String a2lFileName)
      throws IcdmException {
    // Check base directory, create if necessary
    File file = new File(CompliDataReview.SERVER_PATH);
    if (!file.exists()) {
      getLogger().debug("Base path does not exist. Creating directory {}", CompliDataReview.SERVER_PATH);
      file.mkdir();
      getLogger().debug("Base directory created");
    }
    this.currentTimeStamp = ApicUtil.getCurrentTime(DateFormat.DATE_FORMAT_20);
    file = new File(file.getAbsolutePath() + File.separator + this.currentTimeStamp);
    file.mkdir();
    String inputDataPath = file.getAbsolutePath();
    try {

      // Writing A2L file to disk
      getLogger().debug("Writing A2L file to disk ... ");
      String theA2lFileName = a2lFileName;

      byte[] temp = a2lBytes.clone();

      if (theA2lFileName.endsWith(".zip")) {
        theA2lFileName = theA2lFileName.substring(0, theA2lFileName.length() - 8) + ".a2l";
        Map<String, byte[]> unzipA2lMap = ZipUtils.unzipIfZipped(temp, theA2lFileName);
        // always a single A2l file.If the A2l file name is .A2L in caps creates NPE
        for (byte[] unzippedArray : unzipA2lMap.values()) {
          temp = unzippedArray;
        }
      }
      String a2lFilePath = inputDataPath + File.separator + theA2lFileName;

      CommonUtils.createFile(a2lFilePath, temp);

      getLogger().debug("A2L file created in path: {}", a2lFilePath);

      // Writing HEX file to disk
      getLogger().debug("Writing HEX files to disk ... ");
      for (Entry<String, byte[]> entry : hexByteMap.entrySet()) {
        String hexFilePath = inputDataPath + File.separator + entry.getKey();
        CommonUtils.createFile(hexFilePath, entry.getValue());
        getLogger().debug("HEX file created in path: {}", hexFilePath);
      }
    }
    catch (IOException exp) {
      throw new IcdmException("COMPLI_REVIEW.INPUT_FOLDER_FAILED", exp);
    }
    return inputDataPath;
  }

  /**
   * @param inputMetaData
   * @return
   * @throws DataException
   */
  private CompliReviewResponse createDummyOutput(final CompliReviewInputMetaData inputMetaData) throws DataException {

    getLogger().debug("Creating DUMMY output...");

    CompliReviewResponse resp = new CompliReviewResponse();
    resp.setUnmappedBcs(this.unmappedBcs);
    resp.setMappedBcsMap(this.mappedBcsMap);
    resp.setCompliParams(new ArrayList<>(this.compliParamSet));
    resp.setQssdParams(new ArrayList<>(this.qssdParamSet));
    Set<Entry<String, Map<String, CalData>>> hexFileEntrySet = this.hexFileCalDataMap.entrySet();
    for (Entry<String, Map<String, CalData>> entry : hexFileEntrySet) {
      Long hexFileIndex = getHexFileIndex(inputMetaData, entry.getKey());
      CompliReviewOutputData compliRvwOutputData = new CompliReviewOutputData();
      Long elementId = getPidcElementId(inputMetaData, hexFileIndex);
      // Even for the Dummy element fill the Pidc name map.
      fillHexPidcNameMap(elementId, hexFileIndex);
      compliRvwOutputData.setCompliCount(this.compliParamSet.size());
      compliRvwOutputData.setCompliPassCount(this.compliParamSet.size());
      compliRvwOutputData.setCssdFailCount(this.compliParamSet.size());
      compliRvwOutputData.setHexfileName(entry.getKey());
      compliRvwOutputData.setNoRuleCount(this.compliParamSet.size());
      compliRvwOutputData.setSsd2RvFailCount(this.compliParamSet.size());
      compliRvwOutputData.setSsdFileName("");
      compliRvwOutputData.setTotalFailCount(this.compliParamSet.size());
      compliRvwOutputData.setQssdCount(this.qssdParamSet.size());
      compliRvwOutputData.setQssdFailCount(this.qssdParamSet.size());
      compliRvwOutputData.setQssdNoRuleCount(this.qssdParamSet.size());
      compliRvwOutputData.setQssdPassCount(this.qssdParamSet.size());
      compliRvwOutputData.setQssdTotalFailCount(this.qssdParamSet.size());
      this.compliRvwOutputMap.put(hexFileIndex, compliRvwOutputData);
    }
    resp.setHexFileOutput(this.compliRvwOutputMap);

    getLogger().debug("DUMMY output creation completed");

    return resp;

  }


  /**
   * @param a2lInputStream A2L input stream
   * @param hexFileInputStreams input file streams
   * @param inputMetaData input meta data
   * @param a2lFileName A2L file's name
   * @param isNonSDOMBCRelease2
   * @throws IcdmException validation error
   */
  public void validateInputFiles(final InputStream a2lInputStream, final Map<String, InputStream> hexFileInputStreams,
      final CompliReviewInputMetaData inputMetaData, final String a2lFileName, final List<Boolean> isNonSDOMBCRelease)
      throws IcdmException {

    CompliReviewInputValidator validator = new CompliReviewInputValidator(getServiceData(), a2lInputStream,
        hexFileInputStreams, inputMetaData, a2lFileName);
    validator.validate(isNonSDOMBCRelease);
  }

  /**
   * @param a2lInputStream
   * @param hexFileInputStreams
   * @param inputMetaData
   * @param inputMetaData
   * @param resp
   * @param compliReviewHexParamResult
   * @param isNonSDOMBCRelease2
   * @param hexFileStreamMap
   * @throws IcdmException
   */
  private void invokeSsdAndCheckSsd(final CompliReviewInputMetaData inputMetaData, final CompliReviewResponse resp,
      final CompliReviewHexParamResult compliReviewHexParamResult, final Map<String, byte[]> hexByteMap,
      final boolean isNonSDOMBCRelease)
      throws IcdmException {

    int inputHexFileCount = 0;
    CheckSSDBatchRun.clearInstance();
    for (Entry<Long, String> entry : inputMetaData.getHexfileIdxMap().entrySet()) {
      Long hexFileIndex = entry.getKey();
      String hexFileName = entry.getValue();
      CheckSSDBatchRun.getInstance().setDataSetCnt(inputMetaData.getHexfileIdxMap().size());
      if (entry.getKey() == (inputMetaData.getHexfileIdxMap().size())) {
        CheckSSDBatchRun.getInstance().setLastDataset(true);
      }
      CheckSSDBatchRun.getInstance().setSheetIndex(inputHexFileCount + 1);
      inputHexFileCount++;
      // Param Result Map
      Map<String, CompliRvwHexParam> compliRvwParamMap = new HashMap<>();
      compliReviewHexParamResult.getCompliRvwHexParamMap().putIfAbsent(hexFileIndex, compliRvwParamMap);
      Map<String, CalData> calDataMap = this.hexFileCalDataMap.get(hexFileName);
      SSDServiceHandler ssdHandler = new SSDServiceHandler(getServiceData());
      Map<String, String> compliLabelUnitMap = createLabelUnitMap(calDataMap, this.allParamSet);

      try {
        Set<ComPkgBcModel> bcSet = new HashSet<>(this.bcMap.values());

        SSDMessageOptions ssdMessageOpts =
            ssdHandler.invokeComplianceRelease(compliLabelUnitMap, bcSet, this.isQSSDOnlyRelease, isNonSDOMBCRelease);

        // SSD Release Id
        Long proReleaseId = null != ssdHandler.getProReleaseId() ? ssdHandler.getProReleaseId().longValue() : null;

        Map<String, ComPkgBcModel> compPkgBcModelMap = new HashMap<>(this.bcMap);
        ssdMessageOpts.getNoNodeBcList().forEach(bc -> {
          String[] bcData = bc.split(",");
          fillNonMappedBc(bcData[0], bcData[1], resp);
          compPkgBcModelMap.remove(bcData[0]);
        });

        fillMappedBc(compPkgBcModelMap, resp);

        if (ssdMessageOpts.getSsdMessage() == SSDMessage.CONTINUERELEASE) {
          // Invoke SSD Release and Check SSD release

          Long elementId = getPidcElementId(inputMetaData, hexFileIndex);

          fillHexPidcNameMap(elementId, hexFileIndex);

          this.featAttrAdap = new FeatureAttributeAdapterNew(getServiceData());

          findAttrDependencies(ssdHandler, elementId);

          this.checkSSDInputModel.setDatafileoption(inputMetaData.getDatafileoption());
          this.checkSSDInputModel.setPredecessorCheck(inputMetaData.isPredecessorCheck());
          this.checkSSDInputModel.setHexFileName(hexFileName);
          this.checkSSDInputModel.setPidcA2l(inputMetaData.getPidcA2L());
          this.checkSSDInputModel.setVariantId(inputMetaData.getHexFilePidcElement().get(entry.getKey()));
          this.checkSSDInputModel.setHexFilePidcElement(inputMetaData.getHexFilePidcElement());

          CompliReviewOutputData compliRvwOutputData =
              fetchRulesAndCallCheckSsd(ssdHandler, elementId, compliLabelUnitMap,
                  compliReviewHexParamResult.getCompliRvwHexParamMap().get(hexFileIndex), isNonSDOMBCRelease);

          compliRvwOutputData.setReleaseId(proReleaseId);
          setCheckSum(hexByteMap, hexFileName, compliRvwOutputData);
          this.compliRvwOutputMap.put(hexFileIndex, compliRvwOutputData);

        }
        else {
          StringJoiner errorMsg = new StringJoiner("\n");
          errorMsg.add(ssdMessageOpts.getSsdMessage().getDescription());
          // if Mapping SSD node for BC version missing , add the bc list to error
          if (ssdMessageOpts.getSsdMessage() == SSDMessage.SSDNODEMISSING) {
            ssdMessageOpts.getNoNodeBcList().forEach(errorMsg::add);
          }
          throw new IcdmException(errorMsg.toString(), ssdMessageOpts.getSsdMessage().getCode());
        }
      }
      catch (IcdmException exp) {
        ssdHandler.cancelRelease();
        throw exp;
      }
      catch (Exception exp) {
        ssdHandler.cancelRelease();
        getLogger().error(exp.getLocalizedMessage(), exp);
        throw new IcdmException(exp.getMessage(), 1002, exp);
      }
    }
  }

  /**
   * @param unZippedHexStream
   * @param hexFileName
   * @param compliRvwOutputData
   * @throws IOException
   */
  private void setCheckSum(final Map<String, byte[]> hexByteMap, final String hexFileName,
      final CompliReviewOutputData compliRvwOutputData) {

    byte[] byteArray = hexByteMap.get(hexFileName.toLowerCase(Locale.getDefault()));
    Long checkSum = CommonUtils.calculateCheckSum(byteArray);
    if (null != checkSum) {
      compliRvwOutputData.setHexChecksum(Long.toHexString(checkSum).toUpperCase(Locale.getDefault()));
    }
  }


  /**
   * @param resp
   * @param compPkgBcModelMap
   * @param bcData
   * @param ssdMessageOpts
   * @param resp
   */
  private void fillMappedBc(final Map<String, ComPkgBcModel> compPkgBcModelMap, final CompliReviewResponse resp) {
    compPkgBcModelMap.forEach((bcName, bc) -> {
      MappedBcsData mappedBcsData = new MappedBcsData();
      mappedBcsData.setBcVersion(bc.getBvVersion());
      mappedBcsData.setNodeLevel(null != bc.getLevel() ? bc.getLevel().longValue() : null);
      this.mappedBcsMap.put(bcName, mappedBcsData);
    });
    resp.setMappedBcsMap(this.mappedBcsMap);
  }

  /**
   * @param ssdMessageOpts
   * @param resp
   */
  private void fillNonMappedBc(final String unMappedBcName, final String unMappedBcVersion,
      final CompliReviewResponse resp) {

    UnMappedBcsData unMappedBcsData = new UnMappedBcsData();
    unMappedBcsData.setBcVersion(unMappedBcVersion);
    BigDecimal level = this.bcMap.get(unMappedBcName).getLevel();
    unMappedBcsData.setNodeLevel(null != level ? level.longValue() : null);
    this.unmappedBcs.put(unMappedBcName, unMappedBcsData);
    resp.setUnmappedBcs(this.unmappedBcs);
  }

  /**
   * @param elementId
   * @param hexFileIndex
   * @throws DataException
   */
  private void fillHexPidcNameMap(final Long elementId, final Long hexFileIndex) throws DataException {
    if ((elementId != null) && (hexFileIndex != null)) {
      PidcVariantLoader variantLoader = new PidcVariantLoader(getServiceData());
      PidcVersionLoader versionLoader = new PidcVersionLoader(getServiceData());

      if (variantLoader.isValidId(elementId)) {
        PidcVariant projVariant = variantLoader.getDataObjectByID(elementId);
        PidcVersion pidcVer = versionLoader.getDataObjectByID(projVariant.getPidcVersionId());
        this.pidcElementNameMap.put(hexFileIndex, pidcVer.getName() + "/" + projVariant.getName());
      }
      else {
        PidcVersion pidcVer = versionLoader.getDataObjectByID(elementId);
        this.pidcElementNameMap.put(hexFileIndex, pidcVer.getName());
      }

    }

  }

  /**
   * @param inputMetaData
   * @param hexFileName
   * @return
   */
  private Long getHexFileIndex(final CompliReviewInputMetaData inputMetaData, final String hexFileName) {
    Map<Long, String> hexfileIdxMap = inputMetaData.getHexfileIdxMap();
    for (Entry<Long, String> hexIdMapEnrty : hexfileIdxMap.entrySet()) {
      if (hexIdMapEnrty.getValue().equals(hexFileName)) {
        return hexIdMapEnrty.getKey();
      }

    }
    return null;
  }

  /**
   * @param inputMetaData
   * @param hexFileName
   * @return
   */
  private Long getPidcElementId(final CompliReviewInputMetaData inputMetaData, final Long hexFileIndex) {
    Map<Long, String> hexfileIdxMap = inputMetaData.getHexfileIdxMap();
    for (Entry<Long, String> hexIdMapEnrty : hexfileIdxMap.entrySet()) {
      if (hexIdMapEnrty.getKey().equals(hexFileIndex)) {
        return inputMetaData.getHexFilePidcElement().get(hexIdMapEnrty.getKey());
      }
    }
    return null;
  }

  /**
   * @param calDataMap
   * @param ssdHandler
   * @param hexFileIdxName
   * @param hexFileName
   * @param elementId
   * @param compliLabelUnitMap
   * @param compliRvwHexParamMap
   * @param isNonSDOMBCRelease
   * @param checkSSDInputModel
   * @param dataFileOption
   * @param predecessorCheck
   * @return
   * @throws IcdmException
   * @throws IOException
   */
  private CompliReviewOutputData fetchRulesAndCallCheckSsd(final SSDServiceHandler ssdHandler, final Long elementId,
      final Map<String, String> compliLabelUnitMap, final Map<String, CompliRvwHexParam> compliRvwHexParamMap,
      final boolean isNonSDOMBCRelease)

      throws IcdmException {

    createOutputFolder();

    CompliSSDInvoker ssdInvoker = new CompliSSDInvoker();

    String ssdFilePath = invokeSSDRelease(ssdHandler, this.checkSSDInputModel.getHexFileName(), ssdInvoker);

    List<CDRRule> ruleList = ssdHandler.readRuleForCompliRelease();
    Map<String, List<CDRRule>> ssdRules = readCompliRules(ssdInvoker, ruleList);
    Map<String, List<CDRRule>> qssdRules = readQssdRules(ssdInvoker, ruleList);

    // check if sub variants are available
    if ((this.featAttrAdap.getSubVarAttrMap() != null) && !this.featAttrAdap.getSubVarAttrMap().isEmpty()) {
      Set<Long> subVarIdSet = new HashSet<>();
      subVarIdSet.add(this.featAttrAdap.getSubVarId());
      RulesValidator validator = new RulesValidator();

      // get the sub var map entry set
      Set<String> entrySet = this.featAttrAdap.getSubVarAttrMap().keySet();

      for (String subVarKey : entrySet) {
        String[] split = subVarKey.split("-");
        // get the sub var id
        Long subVarId = Long.valueOf(split[0]);

        // sub var id should not be same as the one in the feature adapter class, so that ssd release is not more than
        // once
        if (!subVarIdSet.contains(subVarId)) {
          this.featAttrAdap.setSubVarId(subVarId);
          // create release for each variant
          Set<ComPkgBcModel> bcSet = new HashSet<>(this.bcMap.values());
          ssdHandler.invokeComplianceRelease(compliLabelUnitMap, bcSet, this.isQSSDOnlyRelease, isNonSDOMBCRelease);
          findAttrDependencies(ssdHandler, elementId);
          ssdFilePath = invokeSSDRelease(ssdHandler, this.checkSSDInputModel.getHexFileName(), ssdInvoker);
          List<CDRRule> subVarRuleList = ssdHandler.readRuleForCompliRelease();
          Map<String, List<CDRRule>> ssdRulesSubVar = readCompliRules(ssdInvoker, subVarRuleList);
          boolean validateCompliRules = validator.validateRules(ssdRules, ssdRulesSubVar);

          Map<String, List<CDRRule>> qssdRulesSubVar = readQssdRules(ssdInvoker, subVarRuleList);
          boolean validateQssdRules = validator.validateRules(qssdRules, qssdRulesSubVar);

          if (!validateCompliRules || !validateQssdRules) {
            throw new IcdmException("Rules defined at Sub variant level are not same");
          }
          // add subvariants to set for which ssd release is completed. this is to avoid duplicates
          subVarIdSet.add(this.featAttrAdap.getSubVarId());
        }

      }
    }

    return callCheckSSD(ssdFilePath, ssdRules, qssdRules, compliRvwHexParamMap);
  }

  /**
   * @param calDataMap
   * @param hexFileIdxName
   * @param hexFileName
   * @param ssdFilePath
   * @param ssdRules
   * @param compliRvwHexParamMap
   * @param dataFileOption
   * @return
   * @throws IOException
   * @throws IcdmException
   * @throws CommandException
   */
  private CompliReviewOutputData callCheckSSD(final String ssdFilePath, final Map<String, List<CDRRule>> ssdRules,
      final Map<String, List<CDRRule>> qssdRules, final Map<String, CompliRvwHexParam> compliRvwHexParamMap)
      throws IcdmException {

    Map<String, CalData> calDataMap = this.hexFileCalDataMap.get(this.checkSSDInputModel.getHexFileName());
    Map<String, List<String>> warningsMap = this.parserWarningsMap.get(this.checkSSDInputModel.getHexFileName());

    CheckSSDOutputData checkSSDOut = invokeCheckSSD(calDataMap, warningsMap, ssdFilePath + ".ssd");

    // check Ssd out can be never null. Becasue it is initialized.
    if (checkSSDOut.isReviewHasExp() && (checkSSDOut.getReviewExceptionObj() != null)) {
      throw new IcdmException(checkSSDOut.getReviewExceptionObj().getMessage(), checkSSDOut.getReviewExceptionObj());

    }

    Map<String, String> compliResultMap = new HashMap<>();
    Map<String, String> qssdResultMap = new HashMap<>();

    getResult(checkSSDOut, ssdRules, qssdRules, compliRvwHexParamMap, calDataMap, compliResultMap, qssdResultMap);

    return constructWebFlowOutput(this.checkSSDInputModel.getHexFileName(), ssdFilePath, checkSSDOut, compliResultMap,
        qssdResultMap);
  }

  /**
   * create Output folder
   */
  private void createOutputFolder() {

    // create the file only for the first time
    if (this.folderPath == null) {

      getLogger().debug("Creating output directory");

      // Check base directory, create if necessary
      File file = new File(CompliDataReview.SERVER_PATH);
      if (!file.exists()) {
        getLogger().debug("Base path does not exist. Creating directory {}", CompliDataReview.SERVER_PATH);
        file.mkdir();
        getLogger().debug("Base directory created");
      }

      // Create output directory
      file = new File(file.getAbsoluteFile() + "\\CompliReview_" + this.currentTimeStamp);
      file.mkdir();
      this.folderPath = file.getAbsolutePath();

      getLogger().debug("Output directory created. Path = {}", this.folderPath);
    }

  }

  /**
   * Get compli and qssd results
   *
   * @param checkSSDOut
   * @param ssdRules
   * @param compliRvwHexParamMap
   * @param calDataMap
   * @param compliParamSet2
   * @throws CommandException
   * @throws DataException
   */
  private void getResult(final CheckSSDOutputData checkSSDOut, final Map<String, List<CDRRule>> ssdRules,
      final Map<String, List<CDRRule>> qssdRules, final Map<String, CompliRvwHexParam> compliRvwHexParamMap,
      final Map<String, CalData> calDataMap, final Map<String, String> compliResultMap,
      final Map<String, String> qssdResultMap)
      throws CommandException, DataException {

    Map<String, CheckSSDResultParam> checkSSDCompliParamMap = checkSSDOut.getCheckSSDCompliParamMap();
    Set<String> paramsInSSDFileCompliUsecase = new HashSet<>();
    Set<String> paramsInSSDFileQssdUsecase = new HashSet<>();

    checkSSDCompliParamMap.entrySet().forEach(checkSsdCompEntrySet -> {
      if (!this.compliParamSet.contains(checkSsdCompEntrySet.getKey()) &&
          (checkSsdCompEntrySet.getValue().getCompliReportModel() != null)) {
        paramsInSSDFileCompliUsecase.add(checkSsdCompEntrySet.getKey());
      }
      if (!this.qssdParamSet.contains(checkSsdCompEntrySet.getKey()) &&
          (checkSsdCompEntrySet.getValue().getQssdReportModel() != null)) {
        paramsInSSDFileQssdUsecase.add(checkSsdCompEntrySet.getKey());
      }
    });

    ParameterLoader paramLoader = new ParameterLoader(getServiceData());
    List<String> paramList = new ArrayList<>(checkSSDCompliParamMap.keySet());

    paramList.addAll(checkSSDOut.getLabelsInSsdMap().keySet());
    paramList.addAll(checkSSDOut.getLabelsWithoutRulesMap().keySet());
    paramList.addAll(checkSSDOut.getQssdLabelsWithoutRulesMap().keySet());

    Map<String, Parameter> paramMap = paramLoader.getParamMapByParamNames(paramList);

    for (Entry<String, CheckSSDResultParam> param : checkSSDCompliParamMap.entrySet()) {

      CheckSSDResultParam checkSSDParam = checkSSDCompliParamMap.get(param.getKey());

      CDRRule compliRule = CDRRuleUtil.assertSingleRule(ssdRules, param.getKey());
      CDRRule qSsdRule = CDRRuleUtil.assertSingleRule(qssdRules, param.getKey());

      if (this.qssdParamSet.contains(param.getKey()) || paramsInSSDFileQssdUsecase.contains(param.getKey())) {
        QSSD_RESULT_FLAG qssdResult = CompliResultUtil.getQssdResult(qSsdRule, checkSSDParam, false, param.getKey(),
            this.qssdParamSet.contains(param.getKey()));
        qssdResultMap.put(param.getKey(), qssdResult.getUiType());
        fillCompliRvwHexParam(compliRvwHexParamMap, param.getKey(), qSsdRule, qssdResult, calDataMap, paramMap);
      }
      if (this.compliParamSet.contains(param.getKey()) || paramsInSSDFileCompliUsecase.contains(param.getKey())) {
        COMPLI_RESULT_FLAG compliResult = CompliResultUtil.getCompliResult(compliRule, checkSSDParam, false,
            param.getKey(), this.compliParamSet.contains(param.getKey()), false);
        compliResultMap.put(param.getKey(), compliResult.getUiType());
        fillCompliRvwHexParam(compliRvwHexParamMap, param.getKey(), compliRule, compliResult, calDataMap, paramMap);
      }


    }

    // add skipped params directly
    compliResultMap.putAll(checkSSDOut.getLabelsInSsdMap());

    fillCompliRvwHexParam(checkSSDOut.getLabelsInSsdMap(), compliRvwHexParamMap, calDataMap, paramMap, false);

    // add params without rule directly
    compliResultMap.putAll(checkSSDOut.getLabelsWithoutRulesMap());

    fillCompliRvwHexParam(checkSSDOut.getLabelsWithoutRulesMap(), compliRvwHexParamMap, calDataMap, paramMap, false);

    // add qssd params without rule directly
    qssdResultMap.putAll(checkSSDOut.getQssdLabelsWithoutRulesMap());

    fillCompliRvwHexParam(checkSSDOut.getQssdLabelsWithoutRulesMap(), compliRvwHexParamMap, calDataMap, paramMap, true);

  }

  /**
   * @param compliRvwHexParamMap
   * @param param
   * @param rule
   * @param result
   * @param calDataMap
   * @param paramMap
   * @throws CommandException
   * @throws DataException
   */
  private <T> void fillCompliRvwHexParam(final Map<String, CompliRvwHexParam> compliRvwHexParamMap, final String param,
      final CDRRule rule, final T result, final Map<String, CalData> calDataMap, final Map<String, Parameter> paramMap)
      throws CommandException {

    CompliRvwHexParam compliRvwHexParam = new CompliRvwHexParam();

    if (null != rule) {
      compliRvwHexParam.setLabObjId(null == rule.getRuleId() ? null : rule.getRuleId().longValue());
      compliRvwHexParam.setRevId(null == rule.getRevId() ? null : rule.getRevId().longValue());
    }

    // Set the two flags to false. Make them true for current checks. Qssd or compli
    boolean qssdSet = false;
    boolean compliSet = false;
    if (result instanceof QSSD_RESULT_FLAG) {
      compliRvwHexParam.setQssdResult(((QSSD_RESULT_FLAG) result).getDbType());
      qssdSet = true;
    }
    else if (result instanceof COMPLI_RESULT_FLAG) {
      compliRvwHexParam.setCompliResult(((COMPLI_RESULT_FLAG) result).getDbType());
      compliSet = true;
    }

    compliRvwHexParam.setCheckValue(convertCalDataToZippedByteArr(calDataMap.get(param)));

    // set parameter id
    Parameter parameter = paramMap.get(param);
    compliRvwHexParam.setParamId(parameter == null ? null : parameter.getId());

    CompliRvwHexParam existingHexParam = compliRvwHexParamMap.get(param);
    if (existingHexParam != null) {
      if (qssdSet) {
        compliRvwHexParam.setCompliResult(existingHexParam.getCompliResult());
      }
      if (compliSet) {
        compliRvwHexParam.setQssdResult(existingHexParam.getQssdResult());
      }
    }
    compliRvwHexParamMap.put(param, compliRvwHexParam);
  }

  private void fillCompliRvwHexParam(final Map<String, String> resultsMap,
      final Map<String, CompliRvwHexParam> compliRvwHexParamMap, final Map<String, CalData> calDataMap,
      final Map<String, Parameter> paramMap, final boolean isQssd)
      throws CommandException {

    for (Entry<String, String> resultEntry : resultsMap.entrySet()) {
      CompliRvwHexParam compliRvwHexParam = compliRvwHexParamMap.get(resultEntry.getKey());
      if (null == compliRvwHexParam) {
        compliRvwHexParam = new CompliRvwHexParam();
      }
      compliRvwHexParam.setCheckValue(convertCalDataToZippedByteArr(calDataMap.get(resultEntry.getKey())));
      setResultToCompliRvwHexParam(isQssd, resultEntry.getValue(), compliRvwHexParam);
      Parameter parameter = paramMap.get(resultEntry.getKey());
      compliRvwHexParam.setParamId(parameter == null ? null : parameter.getId());
      compliRvwHexParamMap.put(resultEntry.getKey(), compliRvwHexParam);
    }
  }

  private void setResultToCompliRvwHexParam(final boolean isQssd, final String resultEntryVal,
      final CompliRvwHexParam compliRvwHexParam) {

    if (isQssd) {
      compliRvwHexParam.setQssdResult(QSSD_RESULT_FLAG.getTypeFromUiType(resultEntryVal).getDbType());
    }
    else {
      compliRvwHexParam.setCompliResult(COMPLI_RESULT_FLAG.getTypeFromUiType(resultEntryVal).getDbType());
    }
  }


  /**
   * @param ssdHandler
   * @param ssdInvoker
   * @return
   */
  private Map<String, List<CDRRule>> readCompliRules(final CompliSSDInvoker ssdInvoker, final List<CDRRule> ruleList) {
    if (null != ruleList) {
      return ssdInvoker.getRulesMap(ruleList);
    }
    return null;
  }

  /**
   * @param ssdInvoker
   * @param ruleList
   * @return
   */

  private Map<String, List<CDRRule>> readQssdRules(final CompliSSDInvoker ssdInvoker, final List<CDRRule> ruleList) {
    if (null != ruleList) {
      return ssdInvoker.getQssdRulesMap(ruleList);
    }
    return null;
  }

  /**
   * @param hexFileName
   * @param calDataMap
   * @param ssdFilePath
   * @param dataFileOption
   * @param ssdRules
   * @return
   * @throws IcdmException
   */
  private CheckSSDOutputData invokeCheckSSD(final Map<String, CalData> calDataMap,
      final Map<String, List<String>> hexParsingWarningsMap, final String ssdFilePath)
      throws IcdmException {

    CompliCheckSSDInput compiCheckSSDInp =
        CompliResultUtil.getInstance().createcompliInput(calDataMap, hexParsingWarningsMap, this.a2lFileInfo,
            ssdFilePath, this.folderPath, this.allParamSet, this.checkSSDInputModel);
    return CompliParamCheckSSDInvoker.getInstance().runCheckSSD(compiCheckSSDInp);
  }

  /**
   * @param ssdHandler
   * @param hexFileName
   * @param ssdInvoker
   * @return
   * @throws IcdmException
   */
  private String invokeSSDRelease(final SSDServiceHandler ssdHandler, final String hexFileName,
      final CompliSSDInvoker ssdInvoker)
      throws IcdmException {

    ssdInvoker.setHexFileName(hexFileName);
    return ssdInvoker.invokeSSDRelease(this.folderPath, ssdHandler);

  }

  /**
   * @param ssdFilePath
   * @param hexFileName
   * @param checkSSDOut
   * @param compliResults
   * @param hexFileName2
   * @return
   */
  private CompliReviewOutputData constructWebFlowOutput(final String hexFileName, final String ssdFilePath,
      final CheckSSDOutputData checkSSDOut, final Map<String, String> compliResultMap,
      final Map<String, String> qssdResultMap) {

    CompliReviewOutputData output = new CompliReviewOutputData();
    output.setHexfileName(hexFileName);
    output.setSsdFileName(FilenameUtils.getBaseName(ssdFilePath) + FilenameUtils.getExtension(ssdFilePath) + ".ssd");
    output.setCompliResult(compliResultMap);
    output.setQssdResult(qssdResultMap);
    List<String> checkSSDFileNames = new ArrayList<>();
    for (String filePath : checkSSDOut.getGeneratedCheckSSDFiles()) {
      if (filePath.endsWith(".xlsx")) {
        checkSSDFileNames.add(FilenameUtils.getBaseName(filePath) + ".xlsx");
      }
    }
    output.setCheckSSDfileNames(checkSSDFileNames);
    // qssd metrics
    CompliResultUtil.getInstance().fillQssdMetrics(output);
    // compli metrics
    CompliResultUtil.getInstance().fillCompliMetrics(output);

    return output;
  }

  /**
   * @param ssdHandler
   * @param elementId
   * @throws IcdmException
   */
  private void findAttrDependencies(final SSDServiceHandler ssdHandler, final Long elementId) throws IcdmException {

    Map<BigDecimal, FeaValModel> mapOfFeatures = ssdHandler.getFeaValueForSelection();
    if (CommonUtils.isNullOrEmpty(mapOfFeatures)) {
      return;
    }
    Set<Long> featureIds = new HashSet<>();
    for (BigDecimal fid : mapOfFeatures.keySet()) {
      featureIds.add(fid.longValue());
    }

    // Get map of attribute value model, Feature value model
    Map<Feature, FeatureValueModel> attrValFeatValMap = this.featAttrAdap.getFeatureValues(featureIds, elementId);

    // set attr value model for report
    Set<AttributeValueModel> attrValModelSet = null;
    if (CommonUtils.isNotEmpty(attrValFeatValMap)) {
      attrValModelSet = this.featAttrAdap.getAttrValSet(new ArrayList<>(attrValFeatValMap.values()), getServiceData());

      Set<AttributeValueModel> attrValSetForPidcElement = null;
      if (CommonUtils.isNotEmpty(this.attrValDepModelMap)) {
        attrValSetForPidcElement = this.attrValDepModelMap.get(elementId);

      }
      if ((attrValSetForPidcElement != null) && CommonUtils.isNotEmpty(attrValSetForPidcElement)) {
        attrValSetForPidcElement.addAll(attrValModelSet);
        this.attrValDepModelMap.put(elementId, attrValSetForPidcElement);
      }
      else {
        this.attrValDepModelMap.put(elementId, attrValModelSet);
      }

      // Set values in icdm
      if (!attrValFeatValMap.isEmpty()) {
        setValues(attrValFeatValMap, mapOfFeatures);
      }
    }
  }

  /**
   * @param attrValFeatValMap
   * @param mapOfFeatures
   * @throws IcdmException
   */
  private void setValues(final Map<Feature, FeatureValueModel> attrValFeatValMap,
      final Map<BigDecimal, FeaValModel> mapOfFeatures)
      throws IcdmException {

    SortedSet<String> invalidAttrSet = new TreeSet<>();
    for (Entry<Feature, FeatureValueModel> attrValModelEntry : attrValFeatValMap.entrySet()) {
      Long ssdValId = attrValModelEntry.getValue().getValueId().longValue();
      FeaValModel ssdFv = mapOfFeatures.get(attrValModelEntry.getValue().getFeatureId());
      boolean isSSDValueUsedByRule = ssdFv.setSelValueIdFromIcdm(BigDecimal.valueOf(ssdValId));
      if (!isSSDValueUsedByRule) {
        invalidAttrSet.add(attrValModelEntry.getKey().getAttrName());
      }
    }
    if (!invalidAttrSet.isEmpty()) {
      StringBuilder message =
          new StringBuilder("Rules not available for the selected value for the following attribute(s).");
      for (String attrName : invalidAttrSet) {
        message.append('\n').append(attrName);
      }
      throw new InvalidInputException(message.toString());
    }
  }

  /**
   * @param a2lInputStream A2L input stream
   * @param hexFileInputStreams HEX file input streams
   * @param a2lFileName A2L File's name
   * @throws IcdmException error while filling input data
   */
  public void fillInputData(final InputStream a2lInputStream, final Map<String, InputStream> hexFileInputStreams,
      final String a2lFileName)
      throws IcdmException {

    try {
      this.a2lFileInfo = getA2lFileInfo(a2lInputStream);
      this.a2lFileInfo.setFileName(a2lFileName);

      Map<String, Characteristic> allModulesLabelsMap = this.a2lFileInfo.getAllModulesLabels();
      getLogger().debug("A2L File : {}, Number of parameters = {}", a2lFileName, allModulesLabelsMap.size());

      fillCalDataMap(hexFileInputStreams);

      loadCompliQssdParams(allModulesLabelsMap);

      if (CommonUtils.isNotEmpty(this.compliParamSet) || CommonUtils.isNotEmpty(this.qssdParamSet)) {
        this.allParamSet = new TreeSet<>();
        this.allParamSet.addAll(this.compliParamSet);
        this.allParamSet.addAll(this.qssdParamSet);
      }

    }
    catch (IOException exp) {
      throw new InvalidInputException("COMPLI_REVIEW.A2L_INVALID", exp, exp.getMessage());
    }

  }

  /**
   * @param inputMetaData input Meta Data
   * @param a2lFileInfo a2lFileInfo
   * @throws InvalidInputException
   */
  private void fillBcInfo(final CompliReviewInputMetaData inputMetaData) throws IcdmException {
    ComPkgBCModelLoader bcMdlLoader = new ComPkgBCModelLoader(getServiceData(), this.a2lFileInfo, this.compliParamSet);
    this.bcMap = bcMdlLoader.getBCs(inputMetaData.getPverName(), inputMetaData.getPverVariant(),
        inputMetaData.getPverRevision());
  }


  private void loadCompliQssdParams(final Map<String, Characteristic> allA2lModulesLabelsMap) {
    ParameterLoader prmLdr = new ParameterLoader(getServiceData());
    this.compliParamSet = new TreeSet<>();
    this.qssdParamSet = new TreeSet<>();

    for (String paramName : allA2lModulesLabelsMap.keySet()) {
      if (prmLdr.isCompliParameter(paramName)) {
        this.compliParamSet.add(paramName);
      }
      if (prmLdr.isQssdParameter(paramName)) {
        this.qssdParamSet.add(paramName);
      }
    }

    getLogger().info("A2L file : {}, compli params = {}, Q-SSD params = {}", this.a2lFileInfo.getFileName(),
        this.compliParamSet.size(), this.qssdParamSet.size());

  }

  /**
   * @param hexFileInputStreams
   */
  private void fillCalDataMap(final Map<String, InputStream> hexFileInputStreams) throws IcdmException {
    // New method to get the Caldata Hex file map.
    for (Entry<String, InputStream> hexInputStream : hexFileInputStreams.entrySet()) {
      CALDATA_FILE_TYPE fileType = CALDATA_FILE_TYPE.getTypeFromFileName(hexInputStream.getKey());
      CaldataFileParserHandler handler = new CaldataFileParserHandler(ParserLogger.getInstance(), this.a2lFileInfo);
      ConcurrentMap<String, CalData> calDataMap =
          new ConcurrentHashMap<>(handler.getCalDataObjects(fileType, hexInputStream.getValue()));

      this.hexFileCalDataMap.put(hexInputStream.getKey(), calDataMap);
      this.parserWarningsMap.put(hexInputStream.getKey(), handler.getWarningsMap());
      // Perform review for each Cal data map.
    }
    Set<Entry<String, Map<String, CalData>>> hexFileEntrySet = this.hexFileCalDataMap.entrySet();
    for (Entry<String, Map<String, CalData>> entry : hexFileEntrySet) {
      getLogger().debug("Hex File : {}, Number of Caldata objects = {}", entry.getKey(), entry.getValue().size());
    }
  }

  /**
   * @param a2lInputStream
   * @return
   * @throws IOException
   * @throws InvalidInputException
   */
  private A2LFileInfo getA2lFileInfo(final InputStream a2lInputStream) throws IOException, InvalidInputException {
    byte[] byteArray = IOUtils.toByteArray(a2lInputStream);
    return A2LFileInfoFetcher.INSTANCE.getA2lFileInfo(byteArray, getLogger());
  }


  /**
   * Method moved to this class to be used by review with stand alone and web service.
   *
   * @param calDataMap calDataMap
   * @param compliParams compliParams
   * @return the label unit map
   */
  private Map<String, String> createLabelUnitMap(final Map<String, CalData> calDataMap,
      final Set<String> compliParams) {

    Map<String, String> labellist = new HashMap<>();
    for (String name : compliParams) {
      CalData calData = calDataMap.get(name);
      if (calData != null) {
        labellist.put(name, calData.getCalDataPhy().getUnit());
      }
    }

    return labellist;
  }

  /**
   * Convert the caldata object to a zipped byte array
   *
   * @param data caldata object
   * @return zipped byte array
   * @throws CommandException on any error during conversion
   */
  private byte[] convertCalDataToZippedByteArr(final CalData data) throws CommandException {
    if (data == null) {
      return new byte[0];
    }
    try (ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream outputStm = new ObjectOutputStream(out);) {

      outputStm.writeObject(data);
      ConcurrentMap<String, byte[]> dataMap = new ConcurrentHashMap<>();
      dataMap.put(CalDataUtil.KEY_CALDATA_ZIP, out.toByteArray());
      return ZipUtils.createZip(dataMap);

    }
    catch (IOException exp) {
      throw new CommandException("Error saving Cal data for parameter", exp);
    }
  }

  /**
   * @return the folderPath
   */
  public String getFolderPath() {
    return this.folderPath;
  }


  /**
   * @return the pidcElementNameMap
   */
  public Map<Long, String> getPidcElementNameMap() {
    return this.pidcElementNameMap;
  }


  /**
   * @return the attrValModelMap
   */
  public Map<Long, Set<AttributeValueModel>> getAttrValModelMap() {
    return this.attrValDepModelMap;
  }


  /**
   * @return the a2lFileInfo
   */
  public A2LFileInfo getA2lFileInfo() {
    return this.a2lFileInfo;
  }


  /**
   * @return the currentTime
   */
  public String getCurrentTimeStamp() {
    return this.currentTimeStamp;
  }

}

