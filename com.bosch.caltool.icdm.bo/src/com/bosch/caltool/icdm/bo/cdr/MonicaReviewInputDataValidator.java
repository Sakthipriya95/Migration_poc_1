/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.calmodel.a2ldata.ref.concrete.DefCharacteristic;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.authentication.ldap.LdapException;
import com.bosch.caltool.authentication.ldap.UserInfo;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.A2lWpResponsibilityLoader;
import com.bosch.caltool.icdm.bo.a2l.ParameterLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantAttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.user.UserCommand;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.bo.user.LdapAuthenticationWrapper;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.a2l.WpRespLabelResponse;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.MonicaInputData;
import com.bosch.caltool.icdm.model.cdr.MonicaInputModel;
import com.bosch.caltool.icdm.model.cdr.MonicaReviewData;
import com.bosch.caltool.icdm.model.cdr.MonicaReviewFileData;
import com.bosch.caltool.icdm.model.cdr.MonicaReviewInputData;
import com.bosch.caltool.icdm.model.cdr.RvwWpAndRespModel;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.monicareportparser.data.ParameterInfo;

/**
 * @author say8cob
 */
public class MonicaReviewInputDataValidator {

  private static final int NO_OF_LABELS_TOBE_DISPLAYED = 15;

  private static final String PIDC_LOCKED = "L";

  private static final String OWNER = "Owner";

  private static final String CAL_ENGINEER = "Calibration Engineer";

  private static final String AUDITOR = "Auditor";

  private final String dcmFileName;

  private final String monicaFileName;

  private final byte[] monicaByteArray;

  private final Map<String, CalData> dcmCalDataMap;

  private final byte[] dcmByteArray;

  private UserLoader userLoader;

  private PidcA2lLoader a2lloader;

  private PidcVersionLoader pidcVersionloader;

  private PidcVariantLoader variantLoader;

  private PidcVariantAttributeLoader variantAttrLoader;

  private PidcVersionAttributeLoader projectVerAttrLoader;

  private final ServiceData serviceData;

  private static final String EXCEL_FILE_EXT1 = ".xlsx";

  private static final String EXCEL_FILE_EXT2 = ".xlx";

  private static final String DCM_FILE_EXT = ".dcm";

  private PidcA2l pidcA2l;

  private Map<String, Characteristic> allModulesLabels;

  private String labelsMissingInExcel;

  private String labelsMissingInDcm;

  private SortedSet<String> labelsToRemove;


  // local varaible

  private final Long pidcA2lId;

  private final String ownUserName;

  private final String audUserName;

  private final String calUserName;

  private final List<MonicaReviewData> monicaObjectList;

  private final Long variantId;


  /**
   * Error message
   */
  private static final String SAV_CALDATA_ERR = "Error saving Cal data for parameter";

  /**
   * @param monicaReviewInputData
   * @param dcmFileName
   * @param monicaFileName
   * @param dcmByteArray
   * @param monicaByteArray
   * @param serviceData
   * @param calDataMap
   */
  public MonicaReviewInputDataValidator(final MonicaReviewInputData monicaReviewInputData, final String dcmFileName,
      final String monicaFileName, final byte[] dcmByteArray, final byte[] monicaByteArray,
      final ServiceData serviceData, final Map<String, CalData> calDataMap) {
    this.pidcA2lId = monicaReviewInputData.getPidcA2lId();
    this.ownUserName = monicaReviewInputData.getOwnUserName();
    this.audUserName = monicaReviewInputData.getAudUserName();
    this.calUserName = monicaReviewInputData.getCalEngUserName();
    this.monicaObjectList = monicaReviewInputData.getMonicaObject();
    this.variantId = monicaReviewInputData.getVariantId();
    this.dcmFileName = dcmFileName;
    this.monicaFileName = monicaFileName;
    this.serviceData = serviceData;
    this.dcmByteArray = null == dcmByteArray ? null : dcmByteArray.clone();
    this.monicaByteArray = null == monicaByteArray ? null : monicaByteArray.clone();
    this.dcmCalDataMap = calDataMap;
  }


  /**
   * Constructor to handle internal monica service
   *
   * @param monicaReviewInputData
   * @param dcmFileName
   * @param monicaFileName
   * @param dcmByteArray
   * @param monicaByteArray
   * @param serviceData
   * @param calDataMap
   */
  public MonicaReviewInputDataValidator(final MonicaInputData monicaInputData, final MonicaInputModel monicaInputModel,
      final String dcmFileName, final String monicaFileName, final byte[] dcmByteArray, final byte[] monicaByteArray,
      final ServiceData serviceData, final Map<String, CalData> calDataMap) {
    this.pidcA2lId = monicaInputModel.getPidcA2lId();
    this.ownUserName = monicaInputModel.getOwnUserName();
    this.audUserName = monicaInputModel.getAudUserName();
    this.calUserName = monicaInputModel.getCalEngUserName();
    this.monicaObjectList = monicaInputData.getMonicaObject();
    this.variantId = monicaInputData.getVariantId();
    this.dcmFileName = dcmFileName;
    this.monicaFileName = monicaFileName;
    this.serviceData = serviceData;
    this.dcmByteArray = null == dcmByteArray ? null : dcmByteArray.clone();
    this.monicaByteArray = null == monicaByteArray ? null : monicaByteArray.clone();
    this.dcmCalDataMap = calDataMap;
  }

  /**
   * @return
   * @throws DataException
   * @throws IcdmException
   */
  public void validate() throws IcdmException {

    // Loader invocation
    this.a2lloader = new PidcA2lLoader(this.serviceData);
    this.variantLoader = new PidcVariantLoader(this.serviceData);
    this.variantAttrLoader = new PidcVariantAttributeLoader(this.serviceData);
    this.projectVerAttrLoader = new PidcVersionAttributeLoader(this.serviceData);
    this.pidcVersionloader = new PidcVersionLoader(this.serviceData);
    this.userLoader = new UserLoader(this.serviceData);
    PIDCA2lFileInfoProvider a2lDataProvider = new PIDCA2lFileInfoProvider(this.serviceData, this.pidcA2lId);
    // to get the values for PIDCA2L model
    A2LFileInfo a2lFileInfo = a2lDataProvider.getA2lFileInfo();
    this.pidcA2l = a2lDataProvider.getPidcA2l();
    this.allModulesLabels = a2lFileInfo.getAllModulesLabels();
    // code for user validation
    validateUsers();
    // code for Input File DCM/MoniCa validation
    validateInputFiles();
    // code for Input DCM/MoniCa byte[] validation
    validateInputByteArray();
    // Code for validation of label in MoniCa protocol and dcm file
    validateMonicaLabels();
    // Code for PIDC varaint,A2l and SDOM pver validation
    validatePidcAndA2l();

  }

  /**
   * @param monicaReviewInputData
   * @param dcmCalDataMap
   * @param dcmFileName
   * @param dcmByteArray
   * @param moniCaFileName
   * @param monicaByteArray
   * @param inputDataValidator
   * @return
   * @throws DataException
   * @throws UnAuthorizedAccessException
   * @throws IcdmException
   */
  public MonicaReviewFileData getMonicaReviewFileData(final MonicaReviewInputData monicaReviewInputData,
      final Map<String, CalData> dcmCalDataMap, final String dcmFileName, final byte[] dcmByteArray,
      final StringBuilder moniCaFileName, final byte[] monicaByteArray)
      throws IcdmException {
    // to get the a2l functions that matches the parameters in MoniCa object
    Set<String> a2lFunctionList;
    a2lFunctionList =
        getA2lFunctionListUsingDefChar(monicaReviewInputData.getPidcA2lId(), monicaReviewInputData.getMonicaObject());
    // to get the caldata that matches the parameter in MoniCa object
    Map<String, byte[]> calDataByteArrayMap =
        getCalDataByteArrayMap(monicaReviewInputData.getMonicaObject(), dcmCalDataMap);

    List<String> paramNameList = new ArrayList<>();
    monicaReviewInputData.getMonicaObject().forEach(labelObj -> paramNameList.add(labelObj.getLabel()));
    // Find the param id for the labels in monica input file
    List<Long> monicaObjParamIdsList = new ParameterLoader(this.serviceData).getParamObjListByParamName(paramNameList)
        .stream().map(Parameter::getId).collect(Collectors.toList());

    // get the wp resp label response for the a2l file
    List<WpRespLabelResponse> wpRespLabResponse = new A2lWpResponsibilityLoader(this.serviceData)
        .getWpResp(monicaReviewInputData.getPidcA2lId(), monicaReviewInputData.getVariantId());


    Map<Long, RvwWpAndRespModel> rvwMonicaParamAndWpRespModelMap = new HashMap<>();
    // creating RvwWpAndRespModel for the monica review
    Set<RvwWpAndRespModel> rvwMonicaWpAndRespModelSet = new HashSet<>();
    for (WpRespLabelResponse wpRespLabelResponse : wpRespLabResponse) {
      if (monicaObjParamIdsList.contains(wpRespLabelResponse.getParamId())) {
        RvwWpAndRespModel wpAndRespModel = new RvwWpAndRespModel();
        wpAndRespModel.setA2lWpId(wpRespLabelResponse.getWpRespModel().getA2lWpId());
        wpAndRespModel.setA2lRespId(wpRespLabelResponse.getWpRespModel().getA2lResponsibility().getId());
        rvwMonicaWpAndRespModelSet.add(wpAndRespModel);
        rvwMonicaParamAndWpRespModelMap.put(wpRespLabelResponse.getParamId(), wpAndRespModel);
      }
    }


    MonicaReviewFileDataCreater monicaReviewFileDataCreater = new MonicaReviewFileDataCreater();
    MonicaReviewFileData monicaReviewFileData = monicaReviewFileDataCreater.createMonicaFileData(monicaReviewInputData,
        dcmFileName, dcmByteArray, moniCaFileName.toString(), monicaByteArray, a2lFunctionList, calDataByteArrayMap);
    // setting the wp resp data to monica review file data for further processing
    monicaReviewFileData.setRvwMonicaParamAndWpRespModelMap(rvwMonicaParamAndWpRespModelMap);
    monicaReviewFileData.setRvwMonicaWpAndRespModelSet(rvwMonicaWpAndRespModelSet);

    return monicaReviewFileData;
  }



  /**
   * @param monicaInputData
   * @param monicaInputModel
   * @param dcmCalDataMap
   * @param dcmFileName
   * @param dcmByteArray
   * @param moniCaFileName
   * @param monicaByteArray
   * @param inputDataValidator
   * @return
   * @throws IcdmException
   * @throws CommandException
   * @throws DataException
   * @throws UnAuthorizedAccessException
   */
  public MonicaReviewFileData getMonicaReviewFileDataInternal(final MonicaInputData monicaInputData,
      final MonicaInputModel monicaInputModel, final Map<String, CalData> dcmCalDataMap, final String dcmFileName,
      final byte[] dcmByteArray, final StringBuilder moniCaFileName, final byte[] monicaByteArray)
      throws IcdmException {
    // to get the a2l functions that matches the parameters in MoniCa object
    Set<String> a2lFunctionList;
    a2lFunctionList =
        getA2lFunctionListUsingDefChar(monicaInputModel.getPidcA2lId(), monicaInputData.getMonicaObject());
    // to get the caldata that matches the parameter in MoniCa object
    Map<String, byte[]> calDataByteArrayMap = getCalDataByteArrayMap(monicaInputData.getMonicaObject(), dcmCalDataMap);

    List<String> paramNameList = new ArrayList<>();
    monicaInputData.getMonicaObject().forEach(labelObj -> paramNameList.add(labelObj.getLabel()));
    // Find the param id for the labels in monica input file
    List<Long> monicaObjParamIdsList = new ParameterLoader(this.serviceData).getParamObjListByParamName(paramNameList)
        .stream().map(Parameter::getId).collect(Collectors.toList());

    // get the wp resp label response for the a2l file
    List<WpRespLabelResponse> wpRespLabResponse = new A2lWpResponsibilityLoader(this.serviceData)
        .getWpResp(monicaInputModel.getPidcA2lId(), monicaInputData.getVariantId());


    Map<Long, RvwWpAndRespModel> rvwMonicaParamAndWpRespModelMap = new HashMap<>();
    // creating RvwWpAndRespModel for the monica review
    Set<RvwWpAndRespModel> rvwMonicaWpAndRespModelSet = new HashSet<>();
    for (WpRespLabelResponse wpRespLabelResponse : wpRespLabResponse) {
      if (monicaObjParamIdsList.contains(wpRespLabelResponse.getParamId())) {
        RvwWpAndRespModel wpAndRespModel = new RvwWpAndRespModel();
        wpAndRespModel.setA2lWpId(wpRespLabelResponse.getWpRespModel().getA2lWpId());
        wpAndRespModel.setA2lRespId(wpRespLabelResponse.getWpRespModel().getA2lResponsibility().getId());
        rvwMonicaWpAndRespModelSet.add(wpAndRespModel);
        rvwMonicaParamAndWpRespModelMap.put(wpRespLabelResponse.getParamId(), wpAndRespModel);
      }
    }


    MonicaReviewInputData monicaReviewInputData = createMonicaReviewInput(monicaInputData, monicaInputModel);

    MonicaReviewFileDataCreater monicaReviewFileDataCreater = new MonicaReviewFileDataCreater();
    MonicaReviewFileData monicaReviewFileData = monicaReviewFileDataCreater.createMonicaFileData(monicaReviewInputData,
        dcmFileName, dcmByteArray, moniCaFileName.toString(), monicaByteArray, a2lFunctionList, calDataByteArrayMap);
    // setting the wp resp data to monica review file data for further processing
    monicaReviewFileData.setRvwMonicaParamAndWpRespModelMap(rvwMonicaParamAndWpRespModelMap);
    monicaReviewFileData.setRvwMonicaWpAndRespModelSet(rvwMonicaWpAndRespModelSet);


    return monicaReviewFileData;
  }

  /**
   * @param monicaReviewInputData
   * @param dcmCalDataMap
   * @return
   * @throws CommandException
   */
  private Map<String, byte[]> getCalDataByteArrayMap(final List<MonicaReviewData> monicaObjects,
      final Map<String, CalData> dcmCalDataMap)
      throws CommandException {
    Map<String, byte[]> calDataByteArrayMap = new HashMap<>();
    for (MonicaReviewData monicaReviewData : monicaObjects) {
      if (dcmCalDataMap.containsKey(monicaReviewData.getLabel())) {
        byte[] convertCalDataToZippedByteArr =
            convertCalDataToZippedByteArr(dcmCalDataMap.get(monicaReviewData.getLabel()));
        calDataByteArrayMap.put(monicaReviewData.getLabel(), convertCalDataToZippedByteArr);
      }
    }
    return calDataByteArrayMap;
  }

  /**
   * Convert the caldata object to a zipped byte array
   *
   * @param data caldata object
   * @return zipped byte array
   * @throws CommandException on any error during conversion
   */
  // ICDM-2069
  private byte[] convertCalDataToZippedByteArr(final CalData data) throws CommandException {
    if (data == null) {
      return null;
    }
    try (ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream outputStm = new ObjectOutputStream(out);) {

      outputStm.writeObject(data);
      ConcurrentMap<String, byte[]> dataMap = new ConcurrentHashMap<>();
      dataMap.put(CalDataUtil.KEY_CALDATA_ZIP, out.toByteArray());
      return ZipUtils.createZip(dataMap);

    }
    catch (IOException e) {
      throw new CommandException(SAV_CALDATA_ERR, e);
    }
  }


  /**
   * @param monicaReviewInputData
   * @param a2lFunctionList
   * @throws DataException
   */
  private Set<String> getA2lFunctionListUsingDefChar(final Long pidcA2lId, final List<MonicaReviewData> monicaObjects)
      throws IcdmException {
    Set<String> a2lFunctionSet = new HashSet<>();
    A2LFileInfo a2lFileInfo = new PIDCA2lFileInfoProvider(this.serviceData, pidcA2lId).getA2lFileInfo();

    for (MonicaReviewData monicaReviewData : monicaObjects) {
      for (Function function : a2lFileInfo.getAllSortedFunctions()) {
        if (function.getDefCharRefList() != null) {
          for (DefCharacteristic defCharacteristic : function.getDefCharRefList()) {
            if (defCharacteristic.getIdentifier().equals(monicaReviewData.getLabel())) {
              a2lFunctionSet.add(function.getName());
            }
          }
        }
      }
    }
    return a2lFunctionSet;
  }


  /**
   * @param monicaInputData
   * @param monicaInputModel
   * @return
   */
  private MonicaReviewInputData createMonicaReviewInput(final MonicaInputData monicaInputData,
      final MonicaInputModel monicaInputModel) {
    MonicaReviewInputData monicaReviewInputData = new MonicaReviewInputData();

    monicaReviewInputData.setAudUserName(monicaInputModel.getAudUserName());
    monicaReviewInputData.setCalEngUserName(monicaInputModel.getCalEngUserName());
    monicaReviewInputData.setDeltaReview(monicaInputData.isDeltaReview());
    monicaReviewInputData.setDeltaReviewType(monicaInputData.getDeltaReviewType());
    monicaReviewInputData.setDescription(monicaInputModel.getDescription());
    monicaReviewInputData.setMonicaObject(monicaInputData.getMonicaObject());
    monicaReviewInputData.setOrgResultId(monicaInputData.getOrgResultId());
    monicaReviewInputData.setOwnUserName(monicaInputModel.getOwnUserName());
    monicaReviewInputData.setPidcA2lId(monicaInputModel.getPidcA2lId());
    monicaReviewInputData.setReviewParticipants(monicaInputModel.getReviewParticipants());
    monicaReviewInputData.setSelMoniCaSheet(monicaInputData.getSelMoniCaSheet());
    monicaReviewInputData.setVariantId(monicaInputData.getVariantId());
    return monicaReviewInputData;
  }

  /**
   * @param pidcVersionId
   * @param pidcVariantId
   * @return
   * @throws DataException
   */
  private String getPidcSDOMPverName(final Long pidcVersionId, final Long pidcVariantId) throws DataException {
    String pverName = "";

    boolean childLevel = this.projectVerAttrLoader.getSDOMPverAttribute(pidcVersionId).isAtChildLevel();
    if (childLevel) {
      // No need to convert a Long into string and check for empty
      if ((this.variantId != null)) {
        pverName = this.variantAttrLoader.getSDOMPverName(pidcVariantId);
      }
    }
    else {
      pverName = this.projectVerAttrLoader.getSDOMPverName(pidcVersionId);
    }
    return pverName;

  }

  private void validateMonicaLabels() throws DataException {
    List<String> monicaLabelList = new ArrayList<>();

    for (MonicaReviewData monicaReviewData : this.monicaObjectList) {
      monicaLabelList.add(monicaReviewData.getLabel().trim());
    }

    // For Validating the input MoniCa protocol data

    for (MonicaReviewData monicaReviewData : this.monicaObjectList) {

      if (CommonUtils.isEmptyString(monicaReviewData.getLabel())) {
        throw new InvalidInputException(MonicaReviewErrorCode.MONICAPROTOCOL_CANNOT_EMPTY);
      }
      else if (CommonUtils.isEmptyString(monicaReviewData.getStatus())) {
        throw new InvalidInputException(MonicaReviewErrorCode.MONICAPROTOCOL_STATUS_EMPTY);
      }
      else if (ParameterInfo.MONICA_REVIEW_STATUS.valueOf(monicaReviewData.getStatus().toUpperCase()) == null) {
        throw new InvalidInputException(MonicaReviewErrorCode.MONICAPROTOCOL_INVALID_STATUS);
      }
    }

    // Condition to validate the labels both in MoniCa protocol and DCM file

    if (!CommonUtils.isNullOrEmpty(this.dcmCalDataMap)) {
      checkMonicaLabelsInDCMFile(monicaLabelList);
      checkDCMlabelsInMonicaProtocol(monicaLabelList);
    }
    else {
      throw new InvalidInputException(MonicaReviewErrorCode.NO_CAL_INFO_IN_DCMFILE);
    }
    List<MonicaReviewData> toRemoveList = new ArrayList<>();
    if (!this.labelsToRemove.isEmpty()) {
      for (MonicaReviewData monicaReviewData : this.monicaObjectList) {
        if (this.labelsToRemove.contains(monicaReviewData.getLabel().trim())) {
          toRemoveList.add(monicaReviewData);
        }
      }
    }
    // labels not in excel/dcm are removed from the final list
    this.monicaObjectList.removeAll(toRemoveList);
    // Ciondition to check the DCM labels available in A2l File
    checkDCMLabelsInA2lFile(monicaLabelList);

  }


  /**
   * @param monicaLabelList
   * @throws InvalidInputException
   */
  private void checkDCMLabelsInA2lFile(final List<String> monicaLabelList) throws InvalidInputException {
    for (String label : monicaLabelList) {
      Characteristic characteristic = this.allModulesLabels.get(label);
      if (characteristic == null) {
        throw new InvalidInputException(
            MonicaReviewErrorCode.PARAMETER + label + MonicaReviewErrorCode.DCM_LABEL_NOT_FOUND_A2LFILE);
      }
    }
  }


  /**
   * @param monicaLabelList
   * @throws InvalidInputException
   */
  private void checkDCMlabelsInMonicaProtocol(final List<String> monicaLabelList) {
    StringBuilder labelsMissingInExcelBuilder = new StringBuilder();
    SortedSet<String> missingLabelsListInExcel = new TreeSet<>();
    for (String dcmLabel : this.dcmCalDataMap.keySet()) {
      if (!monicaLabelList.contains(dcmLabel)) {
        missingLabelsListInExcel.add(dcmLabel);
      }
    }
    if (!missingLabelsListInExcel.isEmpty()) {
      int count = 0;
      for (String labels : missingLabelsListInExcel) {
        if (count <= MonicaReviewInputDataValidator.NO_OF_LABELS_TOBE_DISPLAYED) {
          labelsMissingInExcelBuilder.append(labels).append(",");
          count++;
        }
        else {
          labelsMissingInExcelBuilder.append(
              " " + (missingLabelsListInExcel.size() - MonicaReviewInputDataValidator.NO_OF_LABELS_TOBE_DISPLAYED) +
                  " more parameters....");
          break;
        }
      }
    }
    if (labelsMissingInExcelBuilder.length() > 0) {
      this.labelsMissingInExcel =
          labelsMissingInExcelBuilder.toString().substring(0, labelsMissingInExcelBuilder.length() - 1);
    }
  }


  /**
   * @param monicaLabelList
   * @throws InvalidInputException
   */
  private void checkMonicaLabelsInDCMFile(final List<String> monicaLabelList) {
    this.labelsToRemove = new TreeSet<>();
    SortedSet<String> missingLabelsListInDCM = new TreeSet<>();
    StringBuilder labelsMissingInDcmBuilder = new StringBuilder();
    for (String monicaLabel : monicaLabelList) {
      CalData calData = this.dcmCalDataMap.get(monicaLabel);
      if (calData == null) {
        this.labelsToRemove.add(monicaLabel);
        missingLabelsListInDCM.add(monicaLabel);
      }
    }
    if (!missingLabelsListInDCM.isEmpty()) {
      int count = 0;
      for (String labels : missingLabelsListInDCM) {
        if (count <= MonicaReviewInputDataValidator.NO_OF_LABELS_TOBE_DISPLAYED) {
          labelsMissingInDcmBuilder.append(labels).append(",");
          count++;
        }
        else {
          labelsMissingInDcmBuilder.append(
              " " + (missingLabelsListInDCM.size() - MonicaReviewInputDataValidator.NO_OF_LABELS_TOBE_DISPLAYED) +
                  " more parameters....");
          break;
        }
      }
    }
    if (labelsMissingInDcmBuilder.length() > 0) {
      this.labelsMissingInDcm =
          labelsMissingInDcmBuilder.toString().substring(0, labelsMissingInDcmBuilder.length() - 1);
    }
  }

  /**
   * @return
   * @throws DataException
   */
  private void validatePidcAndA2l() throws DataException {


    // condition to check if there are any pidc variant present for the given pidc
    if (this.variantLoader.hasVariants(this.pidcA2l.getPidcVersId(), false) &&
        ((this.variantId == null) || this.variantId.toString().isEmpty())) {
      throw new InvalidInputException(MonicaReviewErrorCode.PIDC_HAS_VARIANT);
    }
    PidcVariant pidcVariant = null;
    if (this.variantId != null) {
      // to get the values for PidcVariant model
      try {
        pidcVariant = this.variantLoader.getDataObjectByID(this.variantId);
      }
      catch (DataException e) {
        // condition to check whether the variant id is existing for any pidcr
        throw new InvalidInputException(MonicaReviewErrorCode.PIDC_VARIANT_IS_NULL, e);
      }
    }
    // condition to check whether the MoniCa protocol is empty
    if (this.monicaObjectList.isEmpty()) {
      throw new InvalidInputException(MonicaReviewErrorCode.MONICA_REVIEW_EMPTY);
    }
    // to identify whether the PIDC is in LOCKED status.
    PidcVersion pidcVersion = this.pidcVersionloader.getDataObjectByID(this.pidcA2l.getPidcVersId());
    // condition to check whether the pidc is in LOCKED Status
    if (pidcVersion.getPidStatus().equals(PIDC_LOCKED)) {
      throw new InvalidInputException(MonicaReviewErrorCode.PIDC_IS_LOCKED);
    }
    // condition to check whether the pidc is deleted
    if (this.a2lloader.isPidcVerDeleted(this.pidcA2l.getPidcVersId())) {
      throw new InvalidInputException(MonicaReviewErrorCode.PIDC_IS_DELETED);
    }
    // condition to check whether the variant id is deleted
    if ((pidcVariant != null) && pidcVariant.isDeleted()) {
      throw new InvalidInputException(MonicaReviewErrorCode.PIDC_VARIANT_IS_DELETED);
    }
    // condition to check whether the a2l id is existing
    if (this.a2lloader.getPidcA2l(this.pidcA2l.getA2lFileId(), this.pidcA2l.getProjectId()).getId() == null) {
      throw new InvalidInputException(MonicaReviewErrorCode.PIDC_A2L_ABSENT);
    }
    // condition to check whether the a2l id is from a different pidc version than the variant id
    if ((pidcVariant != null) && ((this.pidcA2l.getPidcVersId() == null) ||
        !(CommonUtils.isEqual(this.pidcA2l.getPidcVersId(), pidcVariant.getPidcVersionId())))) {
      throw new InvalidInputException(MonicaReviewErrorCode.PIDC_VERSION_MISMATCH);
    }


    String pverName = getPidcSDOMPverName(this.pidcA2l.getPidcVersId(), this.variantId);

    // Condition to check if the a2l id is from another pver which is not assigned to the variant
    if (!pverName.equals(this.pidcA2l.getSdomPverName())) {
      throw new InvalidInputException("A2L-ID " + this.pidcA2l.getA2lFileId() + " is from PVER " +
          this.pidcA2l.getSdomPverName() + ". Only PVER " + pverName + " is allowed for this variant.");
    }

  }

  /**
   * @return
   * @throws IcdmException
   */
  private void validateUsers() throws IcdmException {

    validateUserName(this.ownUserName, OWNER);

    // Validating Calibration Engineer User Id
    validateUserName(this.calUserName, CAL_ENGINEER);

    // Validating auditor User Id
    validateUserName(this.audUserName, AUDITOR);

  }

  /**
   * @return
   * @throws InvalidInputException
   */
  private void validateInputFiles() throws InvalidInputException {

    // condition to check whether the MoniCa file is valid
    if (!(this.monicaFileName.toLowerCase(Locale.getDefault()).endsWith(EXCEL_FILE_EXT1) ||
        this.monicaFileName.toLowerCase(Locale.getDefault()).endsWith(EXCEL_FILE_EXT2))) {
      throw new InvalidInputException(
          MonicaReviewErrorCode.FILE + this.monicaFileName + MonicaReviewErrorCode.FILE_NOT_EXIST);
    }
    // condition to checke whether the dcm file is valid
    if (!(this.dcmFileName.toLowerCase(Locale.getDefault()).endsWith(DCM_FILE_EXT))) {
      throw new InvalidInputException(
          MonicaReviewErrorCode.FILE + this.dcmFileName + MonicaReviewErrorCode.FILE_NOT_EXIST);
    }

  }

  private void validateInputByteArray() throws InvalidInputException {

    if ((this.dcmByteArray == null) || (this.dcmByteArray.length == 0)) {
      throw new InvalidInputException(
          MonicaReviewErrorCode.FILE + this.dcmFileName + MonicaReviewErrorCode.CAL_DATA_ABSENT);
    }
    if ((this.monicaByteArray == null) || (this.monicaByteArray.length == 0)) {
      throw new InvalidInputException(
          MonicaReviewErrorCode.FILE + this.monicaFileName + MonicaReviewErrorCode.FILE_NOT_EXIST);
    }
  }

  /**
   * @param userName
   * @param userType
   * @return
   * @throws IcdmException
   */
  private void validateUserName(final String userName, final String userType) throws IcdmException {
    if ((userName == null) || (userName.isEmpty())) {
      throw new InvalidInputException(userType + MonicaReviewErrorCode.USER_NAME_EMPTY);
    }
    // for user validataion
    Long userId = this.userLoader.getUserIdByUserName(userName);
    if (null == userId) {
      UserInfo userInfo = getUserFromLdap(userName);
      createNewICDMUser(userInfo);
    }

  }


  /**
   * @param userName
   * @param userInfo
   * @return
   * @throws InvalidInputException
   */
  private UserInfo getUserFromLdap(final String userName) throws InvalidInputException {
    UserInfo userInfo;
    try {
      userInfo = new LdapAuthenticationWrapper().getUserDetails(userName);
    }
    catch (LdapException e) {
      throw new InvalidInputException("The user name " + userName + " is invalid.", e);
    }
    return userInfo;
  }

  /**
   * @param userName
   * @throws IcdmException
   */
  private void createNewICDMUser(final UserInfo userInfo) throws IcdmException {
    User user = new User();
    user.setName(userInfo.getUserName());
    user.setDepartment(userInfo.getDepartment());
    user.setFirstName(userInfo.getGivenName());
    user.setLastName(userInfo.getSurName());
    UserCommand command = new UserCommand(this.serviceData, user);
    command.create();
  }


  /**
   * @return the labelsMissingInExcel
   */
  public String getLabelsMissingInExcel() {
    return this.labelsMissingInExcel;
  }


  /**
   * @return the labelsMissingInDcm
   */
  public String getLabelsMissingInDcm() {
    return this.labelsMissingInDcm;
  }


}
