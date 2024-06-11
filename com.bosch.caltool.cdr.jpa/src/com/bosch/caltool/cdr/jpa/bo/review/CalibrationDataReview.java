/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo.review;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.EntityManager;

import org.apache.commons.io.FilenameUtils;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.calmodel.a2ldata.ref.concrete.DefCharacteristic;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.a2l.jpa.A2LEditorDataProvider;
import com.bosch.caltool.a2l.jpa.bo.A2LBaseComponents;
import com.bosch.caltool.a2l.jpa.bo.A2LGroup;
import com.bosch.caltool.a2l.jpa.bo.A2LParameter;
import com.bosch.caltool.apic.jpa.bo.ApicDataProvider;
import com.bosch.caltool.apic.jpa.bo.AttributeValue;
import com.bosch.caltool.apic.jpa.bo.AttributeValueModel;
import com.bosch.caltool.apic.jpa.bo.FeatureAttributeAdapter;
import com.bosch.caltool.apic.jpa.bo.IAttributeMappedObject;
import com.bosch.caltool.apic.jpa.bo.IcdmFile;
import com.bosch.caltool.apic.jpa.bo.PIDCAttribute;
import com.bosch.caltool.cdr.jpa.CDRJpaActivator;
import com.bosch.caltool.cdr.jpa.CompliParamFetcher;
import com.bosch.caltool.cdr.jpa.bo.AbstractParameter;
import com.bosch.caltool.cdr.jpa.bo.CDRDataProvider;
import com.bosch.caltool.cdr.jpa.bo.CDRFuncParameter;
import com.bosch.caltool.cdr.jpa.bo.CDRFunction;
import com.bosch.caltool.cdr.jpa.bo.CDRSecondaryResult;
import com.bosch.caltool.cdr.jpa.bo.CdrReviewDataLoader;
import com.bosch.caltool.cdr.jpa.bo.CheckSSDResultParam;
import com.bosch.caltool.cdr.jpa.bo.CmdModCDRResult;
import com.bosch.caltool.cdr.jpa.bo.IcdmWorkPackage;
import com.bosch.caltool.cdr.jpa.bo.Questionnaire;
import com.bosch.caltool.cdr.jpa.bo.QuestionnaireVersion;
import com.bosch.caltool.cdr.jpa.bo.RuleSet;
import com.bosch.caltool.cdr.jpa.bo.RuleSetParameter;
import com.bosch.caltool.cdr.jpa.bo.WorkPackageDivision;
import com.bosch.caltool.cdr.jpa.bo.shapereview.ShapeReviewResult;
import com.bosch.caltool.dmframework.bo.ICommandManager;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.bo.compli.CompliReviewData;
import com.bosch.caltool.icdm.bo.compli.CompliReviewSummary;
import com.bosch.caltool.icdm.bo.compli.ComplianceParamReview;
import com.bosch.caltool.icdm.common.exception.DataRuntimeException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.SsdInterfaceException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CaldataFileParserHandler;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DelimiterJoinerCustomArrayList;
import com.bosch.caltool.icdm.database.entity.cdr.TParameter;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.logger.ParserLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.CDR_EXCEPTION_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.CDR_SOURCE_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.DELTA_REVIEW_TYPE;
import com.bosch.checkssd.CheckSSDCoreInstances;
import com.bosch.checkssd.CheckSSDInfo;
import com.bosch.checkssd.exception.CheckSSDException;
import com.bosch.checkssd.reports.CheckSSDReport;
import com.bosch.checkssd.reports.reportMessage.ReportMessages;
import com.bosch.checkssd.reports.reportmodel.FormtdRptValModel;
import com.bosch.checkssd.reports.reportmodel.ReportModel;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.CDRRulesWithFile;
import com.bosch.ssd.icdm.model.FeatureValueModel;
import com.bosch.ssd.icdm.model.SSDRelease;


/**
 * @author rgo7cob
 */
public class CalibrationDataReview {


  private final CDRData cdrData = new CDRData();

  private final ICommandManager commandManager;

  private final CDRDataProvider cdrDataProvider;

  private final ApicDataProvider apicDataProvider;


  private static final String HEX_EXTENSION = ".hex";
  /**
   *
   */
  private static final int CHKSSD_GENERATE_ALL_TYPES = 2;

  /**
   * Single param in input.
   */
  private static final int SINGLE_PARAM_COUNT = 1;
  /**
   * Param repeat count.
   */
  private static final int PARAM_REPEAT_COUNT = 2;


  private final CaldataReviewSummary cdrSummary = new CaldataReviewSummary(getCdrData());
  private SortedSet<QuestionnaireVersion> qsWithActiveVersions = new TreeSet<QuestionnaireVersion>();
  private final SortedSet<Questionnaire> qsWithoutActiveVersions = new TreeSet<Questionnaire>();
  List<IcdmWorkPackage> qsToBeCreated = new ArrayList<IcdmWorkPackage>();

  List<Long> wpDivsWithQs = new ArrayList<Long>();

  /*
   * For Compli check
   */
  private CompliReviewData compliRvwData;

  private SortedSet<A2LBaseComponents> bCInfo;

  private ComplianceParamReview compliParamRvw;

  private String dataFileName;

  public static final String[] unwantedOutputFiles = { ".wrn", ".lab", ".err", ".log" };


  /**
   * @return the compliRvw
   */
  public ComplianceParamReview getCompliRvw() {
    return this.compliParamRvw;
  }


  /**
   * @param compliRvw the compliRvw to set
   */
  public void setCompliRvwData(final ComplianceParamReview compliRvw) {
    this.compliParamRvw = compliRvw;
  }

  /**
   * @param compliRvwData the compliRvwData to set
   */
  public void setCompliRvw(final CompliReviewData compliRvwData) {
    this.compliRvwData = compliRvwData;
  }

  /**
   * @param commandManager commandManager
   * @param cdrDataProvider cdrDataProvider
   * @param apicDataProvider apicDataProvider
   * @param checkSSDLoggr check SSD Loggr
   */
  public CalibrationDataReview(final ICommandManager commandManager, final CDRDataProvider cdrDataProvider,
      final ApicDataProvider apicDataProvider, final ILoggerAdapter checkSSDLoggr) {
    super();
    this.commandManager = commandManager;
    this.cdrDataProvider = cdrDataProvider;
    this.apicDataProvider = apicDataProvider;
    this.cdrData.setCheckSSDLogger(checkSSDLoggr);
  }


  /**
   * @return the cdrSummary
   */
  public CaldataReviewSummary getCdrSummary() {
    return this.cdrSummary;
  }


  public void setSecondaryRuleSets() {
    MandateRuleSetResolver resolver = new MandateRuleSetResolver(this.apicDataProvider, this.cdrDataProvider);

    resolver.addMandateRuleSet(this.cdrData);
  }


  /**
   * @return the reviewData
   */
  public CDRData getCdrData() {
    return this.cdrData;
  }


  /**
   * @param labFunSelection labFunSelection
   * @param allParamsSelection allParamsSelection
   * @return reviewMap
   * @throws SsdInterfaceException
   */
  public Map<String, CalData> fetchCalDataMap(final boolean labFunSelection, final boolean allParamsSelection)
      throws SsdInterfaceException {


    Map<String, Map<String, CalData>> fileCalDataMap = new HashMap<String, Map<String, CalData>>();

    Map<String, CalData> consolidatedCaldataMap = new ConcurrentHashMap<>();

    CaldataFileParserHandler parserHandler =
        new CaldataFileParserHandler(ParserLogger.getInstance(), this.cdrData.getA2lFileContents());
    boolean isExceptionOccur = false;
    @SuppressWarnings("unchecked")
    List<String> listOfSelectedFiles = new DelimiterJoinerCustomArrayList(true, "\n");
    // Parsing all the input files selected
    for (String filePath : this.cdrData.getSelFilesPath()) {
      try {
        // ICDM-1312
        Map<String, CalData> calDataMap = new ConcurrentHashMap<>(parserHandler.getCalDataObjects(filePath));
        fileCalDataMap.put(filePath, calDataMap); // ICDM 636
        consolidatedCaldataMap.putAll(calDataMap);
      }
      // catch Exception instead of CalDataFileException beacuse many exception types are thrown
      catch (Exception exp) {
        this.cdrSummary.setExceptioninReview(true);
        this.cdrSummary.setReviewExceptionObj(exp);
        this.cdrSummary.setCdrException(CDR_EXCEPTION_TYPE.PARSER_EXCEPTION);
        // return the empty map incase of any error
        isExceptionOccur = true;
        listOfSelectedFiles.add(FilenameUtils.getName(filePath));
        continue;
      }
    }
    if (isExceptionOccur) {
      this.cdrSummary.setListOfSelectedFiles(listOfSelectedFiles);
      return consolidatedCaldataMap;
    }
    // Check if the parameters repeat in any of the multiple input files to be reviewed.If not then get the map of
    // caldata objects to be reviewed
    checkForAmbiguousParamaters(fileCalDataMap, labFunSelection, allParamsSelection);
    return consolidatedCaldataMap;

  }

  /**
   * @param fileCalDataMap Map of file name and Caldata Map
   * @param labFunSelection
   * @param allParamsSelection
   * @return calDataReviewMap boolean added for checking the Radio Button selection
   * @throws SsdInterfaceException
   */
  private Map<String, CalData> checkForAmbiguousParamaters(final Map<String, Map<String, CalData>> fileCalDataMap,
      final boolean labFunSelection, final boolean allParamsSelection)
      throws SsdInterfaceException {

    A2LEditorDataProvider a2lEditorDP = this.cdrData.getA2lEditorDP();
    SortedSet<Function> allFuncsList = a2lEditorDP.getAllFunctions();
    SortedSet<Function> reviewFuncsList = new TreeSet<Function>();
    Map<String, CalData> calDataReviewMap = new HashMap<String, CalData>();
    List<ParamRepeatExcelData> paramsRepeated = new ArrayList<ParamRepeatExcelData>();

    // Getting the Function objects from the function name
    for (String funcName : this.cdrData.getSelReviewFuncs()) {
      for (Function function : allFuncsList) {
        if (function.getName().equals(funcName)) {
          reviewFuncsList.add(function);
        }
      }
    }

    // Checking whether the same parameter repeats in multiple files
    boolean paramRepeats;

    Set<String> labelSet = new HashSet<>();

    if (!this.cdrData.getUnassParaminReview().isEmpty()) {
      labelSet.addAll(this.cdrData.getUnassParaminReview());
    }

    // Group is selected
    if ((CommonUtils.isNullOrEmpty(this.cdrData.getSelReviewFuncs())) && (this.cdrData.getA2lGroup() != null)) {

      A2LGroup group = this.cdrData.getA2lGroup();
      Collection<String> labelList = group.getLabelMap().values();
      labelSet.addAll(labelList);
      paramRepeats = fillCalDataRevMap(fileCalDataMap, calDataReviewMap, paramsRepeated, null, labelSet);
    }
    else if (reviewFuncsList.isEmpty() && !this.cdrData.getLabelList().isEmpty() && !labFunSelection &&
        (this.cdrData.getSelA2LGroup() == null) && (this.cdrData.getSelWorkPackage() == null)) {
      labelSet.addAll(this.cdrData.getLabelList());
      paramRepeats = fillCalDataRevMap(fileCalDataMap, calDataReviewMap, paramsRepeated, null, labelSet);

    }

    // Selecting the Labels from the Lab/Fun File
    else if (reviewFuncsList.isEmpty() && !this.cdrData.getLabelList().isEmpty() && labFunSelection) {
      Collection<String> labelList = this.cdrData.getLabelList();
      labelSet.addAll(labelList);
      paramRepeats = fillCalDataRevMap(fileCalDataMap, calDataReviewMap, paramsRepeated, null, labelSet);
    }
    // Selecting the Labels from the DCM, Hex, CDFX files etc
    else if (allParamsSelection) {
      final boolean hexFileAvailable = checkForHexFile(this.cdrData.getSelFilesPath());
      if (hexFileAvailable) {
        // Remove the check for huge number of params
        // Icdm-1751- New Check made for Rule sets. Only params of the rule must be taken for the review.
        if (CommonUtils.isNotNull(this.cdrData.getPrimaryReviewRuleSetData().getRuleSet())) {
          Map<String, RuleSetParameter> paramsInRuleSetMap =
              this.cdrData.getPrimaryReviewRuleSetData().getRuleSet().getAllParameters(false);
          Map<String, Characteristic> allModulesLabels = a2lEditorDP.getA2lFileInfo().getAllModulesLabels();
          for (RuleSetParameter ruleSetParam : paramsInRuleSetMap.values()) {
            if (allModulesLabels.containsKey(ruleSetParam.getName())) {
              this.cdrData.getMultiFilelabels().add(ruleSetParam.getName());
            }
          }
        }
        else {
          SortedSet<Characteristic> characteristics = a2lEditorDP.getCharacteristics();
          for (Characteristic characteristic : characteristics) {
            this.cdrData.getMultiFilelabels().add(characteristic.getName());

          }
        }
      }
      else {
        this.cdrData.setMultiFilelabels(getLabelsFromExtFiles(fileCalDataMap));
      }
      if ((this.cdrData.getMultiFilelabels() != null) && !this.cdrData.getMultiFilelabels().isEmpty()) {
        // Add all the labels to the label list in Wizard
        this.cdrData.getLabelList().clear();
        this.cdrData.getLabelList().addAll(this.cdrData.getMultiFilelabels());
      }
      labelSet = new HashSet<>(this.cdrData.getMultiFilelabels());
      paramRepeats = fillCalDataRevMap(fileCalDataMap, calDataReviewMap, paramsRepeated, null, labelSet);
    }
    // Workpackage selected
    else {

      ConcurrentMap<String, String> labelFunMap = new ConcurrentHashMap<>();
      for (Function function : reviewFuncsList) {

        List<DefCharacteristic> paramList = function.getDefCharRefList();
        if ((paramList != null) && (paramList.size() > 0)) {
          for (DefCharacteristic defCharacteristic : paramList) {
            labelFunMap.put(defCharacteristic.getName(), function.getName());
          }
        }

      }
      Set<String> keySet = new HashSet<>(labelFunMap.keySet());
      keySet.addAll(labelSet);

      paramRepeats = fillCalDataRevMap(fileCalDataMap, calDataReviewMap, paramsRepeated, labelFunMap, keySet);
    }
    if (paramRepeats) {
      this.cdrSummary.setCdrException(CDRConstants.CDR_EXCEPTION_TYPE.PARAM_REPEATS);
      this.cdrData.setParamsRepeated(paramsRepeated);
      this.cdrSummary.setExceptioninReview(true);
    }
    if ((CommonUtils.isNullOrEmpty(calDataReviewMap.values())) && !paramRepeats) {
      this.cdrSummary.setCdrException(CDRConstants.CDR_EXCEPTION_TYPE.NO_PARAM_SELECTED);
      this.cdrSummary.setExceptioninReview(true);
    }


    return calDataReviewMap;
  }


  /**
   * @param fileCalDataMap
   * @param calDataReviewMap
   * @param paramsRepeated
   * @param labelFunMap
   * @param labelList
   * @return New mehod refractor
   */
  private boolean fillCalDataRevMap(final Map<String, Map<String, CalData>> fileCalDataMap,
      final Map<String, CalData> calDataReviewMap, final List<ParamRepeatExcelData> paramsRepeated,
      final Map<String, String> labelFunMap, final Collection<String> labelList) {

    int count;
    boolean isRepeat = false;
    // ICDM-1720
    ConcurrentMap<String, String> paramFilesMap = new ConcurrentHashMap<String, String>();
    for (String label : labelList) {
      String functionName = labelFunMap == null ? "" : labelFunMap.get(label);
      // iteretion for labels
      count = 0;
      CalData calDataObj = null;
      String fileName1 = "";
      for (Entry<String, Map<String, CalData>> mapEntry : fileCalDataMap.entrySet()) {
        // iterations for caldata in files
        Map<String, CalData> calDataMap = mapEntry.getValue();
        if (calDataMap.containsKey(label)) {
          count++;
          calDataObj = calDataMap.get(label);
          if (count > SINGLE_PARAM_COUNT) {
            isRepeat = true;
            // ICDM 636
            // If the count is true , create excel data for the first file where the label was found
            if (count == PARAM_REPEAT_COUNT) {
              ParamRepeatExcelData excelData = new ParamRepeatExcelData(functionName, label, fileName1);
              paramsRepeated.add(excelData);
            }
            // create excel data with empty function name
            ParamRepeatExcelData excelData = new ParamRepeatExcelData(functionName, label, mapEntry.getKey());
            paramsRepeated.add(excelData);
          }
          fileName1 = mapEntry.getKey();
        }
      }
      if ((count == 1) && (calDataObj != null)) {
        // ICDM-1720
        // add to the map for review
        paramFilesMap.put(label, fileName1);
        // put into map to be reviewed
        calDataReviewMap.put(label, calDataObj);
      }
    }
    // ICDM-1720
    this.cdrData.setParamFilesMap(paramFilesMap);
    return isRepeat;
  }

  /**
   * Icdm-729 Check if there is a hex file aviable in the Selceted file path
   *
   * @param selFilesPath
   * @return
   */
  public boolean checkForHexFile(final Set<String> selFilesPath) {
    for (String string : selFilesPath) {
      if (string.toLowerCase(Locale.ENGLISH).contains(HEX_EXTENSION)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Chevk if the hex file is already selected
   *
   * @param fileCalDataMap
   * @return
   */
  private List<String> getLabelsFromExtFiles(final Map<String, Map<String, CalData>> fileCalDataMap) {

    for (Entry<String, Map<String, CalData>> mapEntry : fileCalDataMap.entrySet()) {
      Map<String, CalData> calDataMap = mapEntry.getValue();
      for (String calData : calDataMap.keySet()) {
        this.cdrData.getMultiFilelabels().add(calData);
      }
    }
    return this.cdrData.getMultiFilelabels();
  }

  /**
   * @param filePath filePath
   * @param parentFile parentFile
   */
  public void writeFileAndSetPath(final String filePath, final IcdmFile parentFile) {
    // Download
    try (FileOutputStream fos = new FileOutputStream(System.getProperty("java.io.tmpdir") + filePath)) {
      fos.write(parentFile.getFiles().get(filePath));
    }
    catch (IOException e) {
      this.cdrSummary.setExceptioninReview(true);
      this.cdrSummary.setReviewExceptionObj(e);
      this.cdrSummary.setCdrException(CDR_EXCEPTION_TYPE.PARSER_EXCEPTION);
    }
  }


  /**
   * set the Func params if a list of Function names are available
   */
  public void setFuncParamsForFun() {

    final SortedSet<Function> allFuncsList = this.cdrData.getA2lEditorDP().getAllFunctions();
    Set<Function> functions = new HashSet<Function>();
    Set<Characteristic> charSet = new HashSet<Characteristic>();

    Map<Long, Set<CDRFuncParameter>> reviewFuncParamMap = new HashMap<>();
    Set<String> labelNames = new HashSet<String>();
    Map<String, Set<Characteristic>> funcCharMap = new HashMap<>();
    final CdrReviewDataLoader dataLoader = new CdrReviewDataLoader(this.cdrDataProvider);


    addFunctions(allFuncsList, functions);


    // Get the CDRFunction set from the T_FUnctions table
    SortedSet<CDRFunction> reviewFuncsSet =
        dataLoader.fetchFunctions(this.cdrData.getSelReviewFuncs(), this.cdrData.getUnassParaminReview().size() > 0);
    // Iterate the function list and store the store the char list
    fillCharSet(functions, charSet, labelNames, funcCharMap);
    // Fetch the Func Param from tParam table
    Map<String, CDRFuncParameter> fetchFuncParams = this.cdrDataProvider.fetchFuncParamMap(charSet);

    // Set of Cdr Functions for the Review. -reviewFuncsSet
    populateFuncParam(reviewFuncsSet, reviewFuncParamMap, funcCharMap, fetchFuncParams);

    // Fetch the func params for the char set

    if (!reviewFuncsSet.isEmpty()) {
      this.cdrData.setCdrFunctionsList(reviewFuncsSet);
    }
    Set<AbstractParameter<?>> modParamSet = null;
    if (CommonUtils.isNotEmpty(reviewFuncParamMap.values())) {

      SortedSet<CDRFuncParameter> paramSet = new TreeSet<>();

      for (Set<CDRFuncParameter> cdrFuncParameterSet : reviewFuncParamMap.values()) {
        if (cdrFuncParameterSet != null) {
          for (CDRFuncParameter cdrFuncParameter : cdrFuncParameterSet) {
            if (null != cdrFuncParameter) {
              paramSet.add(cdrFuncParameter);
            }
          }
        }

      }
      modParamSet = fetchDepAndRules(labelNames, dataLoader, paramSet);
      if (this.cdrSummary.getCdrException() == null) {
        this.cdrData.setReviewFuncParamMap(reviewFuncParamMap);
        this.cdrData.setCdrFuncParams(modParamSet);
      }
    }
    setErrorForEmptyParamSet(reviewFuncParamMap, modParamSet);

  }


  /**
   * @param reviewFuncParamMap
   * @param modParamSet
   */
  private void setErrorForEmptyParamSet(final Map<Long, Set<CDRFuncParameter>> reviewFuncParamMap,
      final Set<AbstractParameter<?>> modParamSet) {
    if ((this.cdrSummary.getCdrException() == null) && (this.cdrData.getUnassParaminReview().isEmpty()) &&
        (reviewFuncParamMap.isEmpty() || (!CommonUtils.isNotEmpty(modParamSet)))) {
      this.cdrSummary.setCdrException(CDRConstants.CDR_EXCEPTION_TYPE.PARAMS_NOT_AVAILABLE);
      this.cdrSummary.setExceptioninReview(true);
    }
  }


  /**
   * @param allFuncsList
   * @param functions
   */
  private void addFunctions(final SortedSet<Function> allFuncsList, final Set<Function> functions) {
    // Iterate the all function list of the a2l file and filter the required Functions and also fill the rules
    for (String funcName : this.cdrData.getSelReviewFuncs()) {
      for (Function function : allFuncsList) {
        if (function.getName().equals(funcName)) {
          functions.add(function);
        }
      }
    }
  }


  /**
   * @param reviewFuncsSet
   * @param reviewFuncParamMap
   * @param funcCharMap
   * @param fetchFuncParams
   */
  private void populateFuncParam(final SortedSet<CDRFunction> reviewFuncsSet,
      final Map<Long, Set<CDRFuncParameter>> reviewFuncParamMap, final Map<String, Set<Characteristic>> funcCharMap,
      final Map<String, CDRFuncParameter> fetchFuncParams) {
    for (CDRFunction cdrFun : reviewFuncsSet) {
      // funcCharMap contains key Function name and Value set of Char From A2l File.
      Set<Characteristic> set = funcCharMap.get(cdrFun.getName());
      if (set == null) {
        continue;
      }
      for (Characteristic charac : set) {
        // fetchFuncParams is the Cdr Func Params from DB- From tParameter table Map contaning values
        CDRFuncParameter funcParFromMap = fetchFuncParams.get(charac.getName() + charac.getType());
        // get the func Param set from the review Func Param map
        Set<CDRFuncParameter> funcParSet = reviewFuncParamMap.get(cdrFun.getID());
        if (funcParSet == null) {
          funcParSet = new HashSet<>();
        }
        if (funcParFromMap != null) {
          funcParSet.add(funcParFromMap);
        }
        reviewFuncParamMap.put(cdrFun.getID(), funcParSet);

      }
    }
  }

  /**
   * @param labelNames
   * @param dataLoader
   * @param paramSet
   * @return
   */
  private Set<AbstractParameter<?>> fetchDepAndRules(final Set<String> labelNames, final CdrReviewDataLoader dataLoader,
      final SortedSet<CDRFuncParameter> paramSet) {
    Set<AbstractParameter<?>> reviewParamSet = null;
    FeatureAttributeAdapter faAdapter = new FeatureAttributeAdapter(this.apicDataProvider);
    try {
      if (this.cdrData.getUnassParaminReview() != null) {
        labelNames.addAll(this.cdrData.getUnassParaminReview());
      }
      // ICDM-2477
      Set<String> paramNotInRuleset = new TreeSet<String>();
      reviewParamSet = getReviewFuncParamSet(paramSet, labelNames, paramNotInRuleset);
      this.cdrSummary.getParamNotInRuleset().clear();
      this.cdrSummary.getParamNotInRuleset().addAll(paramNotInRuleset);

      if (CommonUtils.isEmptyString(this.cdrData.getMonicaFilePath())) {
        // create SSD file for compliance parameters
        Set<String> compliParamSet = identifyCompliParams(paramSet);
        this.compliRvwData = null;

        if ((compliParamSet != null) && (compliParamSet.size() > 0)) {
          // this.compliParamRvw =
          // new ComplianceParamReview(this.compliRvwData, compliParamSet, faAdapter, this.apicDataProvider);
          // this.compliParamRvw.invokeSSDReleaseForCompli();
        }
        List<IAttributeMappedObject> paramsToRemove = new ArrayList<IAttributeMappedObject>();
        Set<FeatureValueModel> featureValModSet = getFeatureValModel(
            new HashSet<IAttributeMappedObject>(reviewParamSet), faAdapter, labelNames, paramsToRemove);
        this.cdrData.setAttrWithoutMapping(faAdapter.getValueNotSetAttr());
        Set<AttributeValueModel> attrValModel = getAttrValModel(featureValModSet);
        boolean setAttrs = true;
        for (AttributeValueModel attributeValueModel : attrValModel) {
          PIDCAttribute pidcAttr =
              getCdrData().getSelPidcVersion().getAttributes().get(attributeValueModel.getAttribute().getID());
          // Special case for Variant code attr. Pidc Attr can be null only if
          if ((pidcAttr != null) && !pidcAttr.isReadable() && pidcAttr.isHidden()) {
            setAttrs = false;
            this.cdrData.setValueNotSetAttr(faAdapter.getValueNotSetAttr());
            this.cdrSummary.setCdrException(CDRConstants.CDR_EXCEPTION_TYPE.HIDDEN_EXCEPTION);
            this.cdrSummary.setExceptioninReview(true);
            break;
          }
        }
        for (IAttributeMappedObject obj : paramsToRemove) {
          reviewParamSet.remove(obj);
        }
        if (setAttrs) {
          createSSDRulesFileWithDep(labelNames, featureValModSet, dataLoader, reviewParamSet);
          this.cdrData.setAttrValModel(attrValModel);
        }
        for (IAttributeMappedObject obj : paramsToRemove) {
          reviewParamSet.add((AbstractParameter<?>) obj);
        }
      }
    }

    catch (IcdmException exception) {
      if (CommonUtils.isNotEmptyString(exception.getErrorCode()) && exception.getErrorCode().equals("1002")) {
        this.cdrSummary.setCdrException(CDRConstants.CDR_EXCEPTION_TYPE.SSD_COMPLIANCE_EXCEPTION);
      }
      else {
        this.cdrData.setValueNotSetAttr(faAdapter.getValueNotSetAttr());
        this.cdrSummary.setCdrException(CDRConstants.CDR_EXCEPTION_TYPE.FEA_VALUE_EXCEPTION);
      }
      this.cdrSummary.setExceptioninReview(true);
      this.cdrSummary.setReviewExceptionObj(exception);
      paramSet.clear();
      if (null != this.cdrData.getReviewFuncParamMap()) {
        this.cdrData.getReviewFuncParamMap().clear();
      }
    }
    return reviewParamSet;
  }

  /**
   * In case of A2L Group mapping, this methods resolves the functions of the parameter for the selected Group
   *
   * @param a2lGroup a2l group
   */
  public void resolveA2LGroupParams(final A2LGroup a2lGroup) {
    List<String> labelList = new ArrayList<String>();
    for (String label : a2lGroup.getLabelMap().values()) {
      Characteristic charObj = this.cdrData.getA2lCharMap().get(label);
      Function func = charObj.getDefFunction();
      if (func == null) {
        // In general, this would not happen
        continue;
      }
      labelList.add(charObj.getName());
    }
    setFuncParamsForLab(labelList);
  }


  /**
   * Setting the selected functions to be reviewed into the CDRReviewWizardData
   *
   * @throws SsdInterfaceException
   */
  public void setCDRFunctions() throws SsdInterfaceException {

    SortedSet<CDRFunction> reviewFuncsList = new TreeSet<CDRFunction>();
    for (String funcName : this.cdrData.getSelReviewFuncs()) {
      CDRFunction cdrFunc = this.cdrDataProvider.getCDRFunction(funcName);
      // Code change made if function is not present in t_functions table then do not do anything.
      if (CommonUtils.isNotNull(cdrFunc)) {
        cdrFunc.getAllParameters(true);
        reviewFuncsList.add(cdrFunc);
      }
    }
    if (CommonUtils.isNotEmpty(reviewFuncsList)) {
      this.cdrData.setCdrFunctionsList(reviewFuncsList);
    }

  }

  /**
   * Icdm-870 new method for fetching the CDRFunction and CDRParameter for a list of labels
   *
   * @param labelList create the CDRFUNction and CDRFUncParam Obj
   */
  public void setFuncParamsForLab(final List<String> labelList) {

    // Get the editor Provider
    Map<Long, Set<CDRFuncParameter>> rvwFunParamMap = new HashMap<>();
    SortedSet<CDRFunction> reviewFuncsSet;
    Set<String> funNames = new HashSet<String>();
    Set<String> labelNames = new HashSet<String>();
    Set<Characteristic> charSet = new HashSet<Characteristic>();
    Map<String, Set<Characteristic>> funcCharMap = new HashMap<>();
    final CdrReviewDataLoader dataLoader = new CdrReviewDataLoader(this.cdrDataProvider);
    // Fill the Fun names in a list and also the Character objects set
    for (String label : labelList) {
      fillFunLabNames(this.cdrData.getA2lCharMap(), funNames, label, charSet, labelNames, funcCharMap);
    }
    if (this.cdrData.getSourceType() == CDR_SOURCE_TYPE.LAB_FILE) {
      // Fetch the CDR functions from the T_Function table
      reviewFuncsSet =
          dataLoader.fetchFunctions(this.cdrData.getSelReviewFuncs(), this.cdrData.getUnassParaminReview().size() > 0);
    }
    else {
      reviewFuncsSet = dataLoader.fetchFunctions(funNames, this.cdrData.getUnassParaminReview().size() > 0);
    }
    // Set the value to the CDR function list
    if (!reviewFuncsSet.isEmpty()) {
      this.cdrData.setCdrFunctionsList(reviewFuncsSet);
    }

    // Fetch the Func Param from tParam table
    Map<String, CDRFuncParameter> fetchFuncParams = this.cdrDataProvider.fetchFuncParamMap(charSet);

    // Set of Cdr Functions for the Review. -reviewFuncsSet
    populateFuncParam(reviewFuncsSet, rvwFunParamMap, funcCharMap, fetchFuncParams);

    // Load the CDR func parameter object
    Set<AbstractParameter<?>> modParamSet = null;
    // Set the value to the wizard
    if (!rvwFunParamMap.isEmpty()) {
      this.cdrData.setReviewFuncParamMap(rvwFunParamMap);
      SortedSet<CDRFuncParameter> treeSet = new TreeSet<>();

      for (Set<CDRFuncParameter> cdrFuncParameterSet : rvwFunParamMap.values()) {
        if (cdrFuncParameterSet != null) {
          for (CDRFuncParameter cdrFuncParameter : cdrFuncParameterSet) {
            if (null != cdrFuncParameter) {
              treeSet.add(cdrFuncParameter);
            }
          }
        }

      }
      modParamSet = fetchDepAndRules(labelNames, dataLoader, treeSet);

      this.cdrData.setCdrFuncParams(modParamSet);

    }
    setErrorForEmptyParamSet(rvwFunParamMap, modParamSet);

  }

  /**
   * Icdm-870 fill the char set
   *
   * @param functions
   * @param charSet
   * @param labelNames
   */
  private void fillCharSet(final Set<Function> functions, final Set<Characteristic> charSet,
      final Set<String> labelNames, final Map<String, Set<Characteristic>> funcCharMap) {
    for (Function function : functions) {
      List<DefCharacteristic> defCharRefList = function.getDefCharRefList();
      Map<String, String> labelMapForGrp = getLabelMapForGrp();
      Set<Characteristic> funCharSet = new HashSet<>();
      if (defCharRefList != null) {
        for (DefCharacteristic defCharacteristic : defCharRefList) {
          if (labelMapForGrp.isEmpty() || labelMapForGrp.containsKey(defCharacteristic.getObj().getName())) {
            charSet.add(defCharacteristic.getObj());
            funCharSet.add(defCharacteristic.getObj());
            labelNames.add(defCharacteristic.getObj().getName());
            funcCharMap.put(function.getName(), funCharSet);
          }
        }
      }
    }
    A2LEditorDataProvider a2lEditorDP = this.cdrData.getA2lEditorDP();
    SortedSet<Characteristic> characteristics = a2lEditorDP.getCharacteristics();

    Set<Characteristic> funCharSet = new HashSet<>();
    for (Characteristic characteristic : characteristics) {
      for (String paramName : this.cdrData.getUnassParaminReview()) {
        if (characteristic.getName().equals(paramName)) {
          charSet.add(characteristic);
          funCharSet.add(characteristic);

        }
      }
    }
    funcCharMap.put(ApicConstants.NOT_ASSIGNED, funCharSet);

  }


  /**
   * @param labelMap
   * @return
   */
  private Map<String, String> getLabelMapForGrp() {
    Map<String, String> labelMap = new HashMap<>();
    A2LGroup grp = this.cdrData.getSelA2LGroup();
    if (CommonUtils.isNotNull(grp)) {
      labelMap = grp.getLabelMap();
    }
    // new check for Multi Groups
    else if (CommonUtils.isNotEmpty(this.cdrData.getMultiGrp())) {
      for (A2LGroup a2lGrp : this.cdrData.getMultiGrp()) {
        labelMap.putAll(a2lGrp.getLabelMap());
      }
    }
    return labelMap;
  }

  /**
   * Icdm-1215 - UI Changes for Review Attr Val
   *
   * @param featureValModel
   * @return
   */
  private Set<AttributeValueModel> getAttrValModel(final Set<FeatureValueModel> featureValModel) throws IcdmException {
    final FeatureAttributeAdapter faAdapter = new FeatureAttributeAdapter(this.apicDataProvider);

    HashSet<AttributeValueModel> hashSet =
        new HashSet<AttributeValueModel>(faAdapter.createAttrValModel(featureValModel).values());
    this.cdrData.setValueNotSetAttr(faAdapter.getValueNotSetAttr());
    return hashSet;


  }


  /**
   * @param labelSet
   * @param featureValModel
   * @param dataLoader
   * @param paramSet
   * @throws SsdInterfaceException
   */
  @SuppressWarnings("null")
  private void createSSDRulesFileWithDep(final Set<String> labelSet, final Set<FeatureValueModel> featureValModelSet,
      final CdrReviewDataLoader dataLoader, final Set<AbstractParameter<?>> paramSet)
      throws SsdInterfaceException {
    if (CommonUtils.isEmptyString(this.cdrData.getMonicaFilePath())) {
      // Icdm-1255-Varaint coded params
      Map<String, String> baseParamMap = ApicUtil.getBaseParamMap(labelSet);
      Set<String> paramsWithDepencies = this.cdrDataProvider.fetchParamsWithDepen(new HashSet(baseParamMap.values()));
      Set<String> modifiedLabelList = ApicUtil.removeBaseParamWithDep(labelSet, paramsWithDepencies);
      if (!this.cdrSummary.isReviewHasExp()) {
        CDRRulesWithFile rulesWithFilePath;

        RuleSet ruleSet = this.cdrData.getPrimaryReviewRuleSetData().getRuleSet();
        // Task 231284
        if (CommonUtils.isNotNull(ruleSet)) {
          // if rule set is selected, then fetch rules from ruleset for primary rules
          rulesWithFilePath = dataLoader.createSSDRulesFileWithDepForRuleSet(modifiedLabelList, featureValModelSet,
              ruleSet.getSsdNodeID());
          fetchRulesForGivenSSDFile(paramSet, rulesWithFilePath, true, ruleSet,
              this.cdrData.getPrimaryReviewRuleSetData());
        }
        // fetching secondary rule sets
        for (ReviewRuleSetData ruleSetData : this.cdrData.getRuleSetDataList()) {
          RuleSet secondaryRuleSet = ruleSetData.getRuleSet();
          if (secondaryRuleSet != null) {
            rulesWithFilePath = dataLoader.createSSDRulesFileWithDepForRuleSet(modifiedLabelList, featureValModelSet,
                secondaryRuleSet.getSsdNodeID());
            fetchRulesForGivenSSDFile(paramSet, rulesWithFilePath, false, secondaryRuleSet, ruleSetData);
          }
        }
        // fetch the common rules if it is for secondary or Common Rules is for primary
        if (this.cdrData.isCommonRulesNeeded() || this.cdrData.isCommonRulesPrimary()) {
          // fetching common rules if it is needed
          rulesWithFilePath = dataLoader.createSSDRulesFileWithDep(modifiedLabelList, featureValModelSet);
          fetchRulesForGivenSSDFile(paramSet, rulesWithFilePath, this.cdrData.isCommonRulesPrimary(), null, null);
        }


      }

    }

    // ICDM-2477
    Set<String> paramWithoutRule = new TreeSet<String>();
    for (AbstractParameter<?> param : paramSet) {
      checkRuleAvailability(param.getName(), paramWithoutRule);
      this.cdrSummary.getParamWithoutRule().clear();
      this.cdrSummary.getParamWithoutRule().addAll(paramWithoutRule);
    }
  }


  /**
   * @param paramSet
   * @param ssdRulesWithPath
   * @param isPrimaryRule
   * @param ruleSet
   * @param reviewRuleSetData
   * @throws SsdInterfaceException
   */
  private void fetchRulesForGivenSSDFile(final Set<AbstractParameter<?>> paramSet,
      final CDRRulesWithFile ssdRulesWithPath, final boolean isPrimaryRule, final RuleSet ruleSet,
      ReviewRuleSetData reviewRuleSetData)
      throws SsdInterfaceException {
    if ((null == reviewRuleSetData) && !isPrimaryRule) {
      // if it is not initialised (in case of common rules)
      reviewRuleSetData = new ReviewRuleSetData();
      // add it to the secondary rules list
      this.cdrData.getRuleSetDataList().add(reviewRuleSetData);
    }
    if (ssdRulesWithPath.getSsdFilePath() == null) {
      // if there are no rules found from SSD
      if (isPrimaryRule) {
        // set the error flag only in case of primary rules
        this.cdrData.setNoValidRuleFlag(true);
      }
      return;
    }
    if (isPrimaryRule) {
      // for primary rules
      this.cdrData.setPrimarySsdRules(ssdRulesWithPath.getCdrRules());

      String mainRevFileName;
      String currentDate = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
      if (null != ruleSet) {
        mainRevFileName = CDRConstants.MAIN_REVIEW + CDRConstants.SSD_FILE_PATH_SEPARATOR + ruleSet.getName() +
            CDRConstants.SSD_FILE_PATH_SEPARATOR + currentDate + CDRConstants.SSD_FILE_EXT;
      }
      else {
        mainRevFileName = CDRConstants.MAIN_REVIEW + CDRConstants.SSD_FILE_PATH_SEPARATOR + CDRConstants.COMMON_RULES +
            CDRConstants.SSD_FILE_PATH_SEPARATOR + currentDate + CDRConstants.SSD_FILE_EXT;
      }

      String[] filePathsplit = ssdRulesWithPath.getSsdFilePath().split("\\\\");
      String fileName = filePathsplit[(filePathsplit.length) - 1];
      File oldSSDFile = new File(ssdRulesWithPath.getSsdFilePath());
      File newSSDFile = new File(ssdRulesWithPath.getSsdFilePath().replace(fileName, mainRevFileName));
      if (newSSDFile.exists()) {
        newSSDFile.delete();
      }
      // rename the file generated by SSD
      newSSDFile = new File(ssdRulesWithPath.getSsdFilePath().replace(fileName, mainRevFileName));
      oldSSDFile.renameTo(newSSDFile);
      this.cdrData.setPrimarySsdFilePath(newSSDFile.getPath());
    }
    else {

      String secRevFileName;
      String currentDate = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
      if (null != ruleSet) {
        secRevFileName = CDRConstants.SECONDARY_REVIEW + CDRConstants.SSD_FILE_PATH_SEPARATOR + ruleSet.getName() +
            CDRConstants.SSD_FILE_PATH_SEPARATOR + currentDate + CDRConstants.SSD_FILE_EXT;
      }
      else if ((null != reviewRuleSetData.getSource()) &&
          reviewRuleSetData.getSource().equals(CDRSecondaryResult.RULE_SOURCE.SSD_RELEASE)) {
        secRevFileName = CDRConstants.MAIN_REVIEW + CDRConstants.SSD_FILE_PATH_SEPARATOR + CDRConstants.SSD_RULES +
            CDRConstants.SSD_FILE_PATH_SEPARATOR + currentDate + CDRConstants.SSD_FILE_EXT;
      }
      else {
        secRevFileName = CDRConstants.SECONDARY_REVIEW + CDRConstants.SSD_FILE_PATH_SEPARATOR +
            CDRConstants.COMMON_RULES + CDRConstants.SSD_FILE_PATH_SEPARATOR + currentDate + CDRConstants.SSD_FILE_EXT;
      }
      String[] filePathsplit = ssdRulesWithPath.getSsdFilePath().split("\\\\");
      String fileName = filePathsplit[(filePathsplit.length) - 1];
      File newSSDFile = new File(ssdRulesWithPath.getSsdFilePath().replace(fileName, secRevFileName));
      File oldSSDFile = new File(ssdRulesWithPath.getSsdFilePath());
      if (newSSDFile.exists()) {
        newSSDFile.delete();
      }

      newSSDFile = new File(ssdRulesWithPath.getSsdFilePath().replace(fileName, secRevFileName));
      oldSSDFile.renameTo(newSSDFile);
      reviewRuleSetData.setSSDRules(ssdRulesWithPath.getCdrRules());
      reviewRuleSetData.setSSDFilePath(newSSDFile.getPath());
    }

    Map<String, List<CDRRule>> cdrRules = ssdRulesWithPath.getCdrRules();
    validateMultiAndIncompleteRules(paramSet, cdrRules);
  }


  /**
   * @param paramSet
   * @param cdrRules
   * @throws SsdInterfaceException
   */
  private void validateMultiAndIncompleteRules(final Set<AbstractParameter<?>> paramSet,
      final Map<String, List<CDRRule>> cdrRules)
      throws SsdInterfaceException {
    List<String> paramNamesWithDepn = new ArrayList<String>();
    for (AbstractParameter<?> param : paramSet) { // icdm-1199
      List<CDRRule> ruleList = cdrRules.get(param.getName());
      if ((ruleList != null) && (ruleList.size() > 1)) {
        this.cdrSummary.setExceptioninReview(true);
        throw new DataRuntimeException("Multiple rules given for the parameter " + param.getName());
      }
      if (param.hasDependencies()) {
        // identifying and adding parameter names that has dependencies
        paramNamesWithDepn.add(param.getName());
      }
    }
    // ICDM-1199
    Map<String, List<CDRRule>> allRuleForParams =
        this.apicDataProvider.getSsdServiceHandler().readReviewRule(new ArrayList<>(paramNamesWithDepn));
    List<String> paramWithIncompleteAttrDef = new ArrayList<String>();

    for (List<CDRRule> ruleList : allRuleForParams.values()) {
      findIfParamRulesIncomplete(paramWithIncompleteAttrDef, ruleList);
    }
    if (!paramWithIncompleteAttrDef.isEmpty()) {
      this.cdrSummary.setExceptioninReview(true);
      throw new DataRuntimeException("Rule is incomplete for \n" + paramWithIncompleteAttrDef);
    }
  }


  /**
   * set the secondary results map for the result param.
   */
  public void setResultsToParam() {

    Map<String, CDRConstants.RESULT_FLAG> secondaryResMap = new ConcurrentHashMap<>();

    List<ReviewRuleSetData> ruleSetDataList = this.cdrData.getRuleSetDataList();


    for (ReviewRuleSetData reviewRuleSetData : ruleSetDataList) {

      CDRConstants.RESULT_FLAG result = CDRConstants.RESULT_FLAG.NOT_REVIEWED;

      if (reviewRuleSetData.getCheckSSDResParamMap() != null) {
        for (Entry<String, CheckSSDResultParam> checkSSDResultParam : reviewRuleSetData.getCheckSSDResParamMap()
            .entrySet()) {

          ReportModel reportModel = checkSSDResultParam.getValue().getReportModel();
          if (reportModel != null) {
            if ((reportModel.getMessType() == ReportMessages.LOG_MSG) ||
                (reportModel.getMessType() == ReportMessages.WRN_MSG)) {
              result = CDRConstants.RESULT_FLAG.OK;
            }
            else {
              result = getResultFromResultModel(reportModel);
            }
          }
          else {
            Map<String, List<CDRRule>> rulesMap = reviewRuleSetData.getCdrRules();
            if (rulesMap != null) {
              List<CDRRule> ruleList = rulesMap.get(checkSSDResultParam.getKey());
              if (CommonUtils.isNotEmpty(ruleList)) {
                CDRRule cdrRule = ruleList.get(0);
                if ((cdrRule != null) && cdrRule.isRuleComplete()) {
                  result = CDRConstants.RESULT_FLAG.NOT_OK;
                }
              }
            }
          }
          setSecondResMap(secondaryResMap, result, checkSSDResultParam);
          result = CDRConstants.RESULT_FLAG.NOT_REVIEWED;
        }
      }

    }

    this.cdrData.getSecResultMap().putAll(secondaryResMap);

  }


  /**
   * @param reportModel ReportModel
   * @return CDRConstants.RESULT_FLAG
   */
  private CDRConstants.RESULT_FLAG getResultFromResultModel(final ReportModel reportModel) {
    CDRConstants.RESULT_FLAG result;
    if (reportModel instanceof FormtdRptValModel) {
      FormtdRptValModel formtdRptValModel = (FormtdRptValModel) reportModel;
      if ((formtdRptValModel.getValGE() != null) && !formtdRptValModel.getValGE().isEmpty()) {
        result = CDRConstants.RESULT_FLAG.HIGH;
      }
      else if ((formtdRptValModel.getValLE() != null) && !formtdRptValModel.getValLE().isEmpty()) {
        result = CDRConstants.RESULT_FLAG.LOW;
      }
      else {
        result = CDRConstants.RESULT_FLAG.NOT_OK;
      }
    }
    else {
      result = CDRConstants.RESULT_FLAG.NOT_OK;
    }
    return result;
  }

  /**
   * @param secondaryResMap
   * @param currentRes
   * @param checkSSDResultParam
   */
  private void setSecondResMap(final Map<String, CDRConstants.RESULT_FLAG> secondaryResMap,
      final CDRConstants.RESULT_FLAG currentRes, final Entry<String, CheckSSDResultParam> checkSSDResultParam) {

    if (secondaryResMap.get(checkSSDResultParam.getKey()) == null) {
      whenSecResIsNull(secondaryResMap, currentRes, checkSSDResultParam);
    }
    else if ((currentRes == CDRConstants.RESULT_FLAG.NOT_OK) || (currentRes == CDRConstants.RESULT_FLAG.HIGH) ||
        (currentRes == CDRConstants.RESULT_FLAG.LOW)) {
      secondaryResMap.put(checkSSDResultParam.getKey(), CDRConstants.RESULT_FLAG.NOT_OK);
    }

    else if ((secondaryResMap.get(checkSSDResultParam.getKey()) == CDRConstants.RESULT_FLAG.OK) &&
        (currentRes != CDRConstants.RESULT_FLAG.NOT_REVIEWED)) {
      secondaryResMap.put(checkSSDResultParam.getKey(), currentRes);
    }
    // 236207
    else if ((currentRes == CDRConstants.RESULT_FLAG.OK) &&
        (secondaryResMap.get(checkSSDResultParam.getKey()) == CDRConstants.RESULT_FLAG.NOT_REVIEWED)) {
      secondaryResMap.put(checkSSDResultParam.getKey(), CDRConstants.RESULT_FLAG.OK);
    }
  }


  /**
   * @param secondaryResMap
   * @param currentRes
   * @param checkSSDResultParam
   */
  private void whenSecResIsNull(final Map<String, CDRConstants.RESULT_FLAG> secondaryResMap,
      final CDRConstants.RESULT_FLAG currentRes, final Entry<String, CheckSSDResultParam> checkSSDResultParam) {
    if ((currentRes == CDRConstants.RESULT_FLAG.NOT_OK) || (currentRes == CDRConstants.RESULT_FLAG.HIGH) ||
        (currentRes == CDRConstants.RESULT_FLAG.LOW)) {
      secondaryResMap.put(checkSSDResultParam.getKey(), CDRConstants.RESULT_FLAG.NOT_OK);
    }
    else {
      secondaryResMap.put(checkSSDResultParam.getKey(), currentRes);
    }
  }

  // call this method from the next pressed of the compli label selection in work package sel page.
  public List<String> getCompliLabels() {
    CompliParamFetcher fetcher = new CompliParamFetcher();
    List<String> labelNames = new ArrayList<>();
    EntityManager entMgrToUse = ObjectStore.getInstance().getEntityManagerFactory().createEntityManager();
    List<TParameter> compliParamList = fetcher.getCompliParamList(entMgrToUse);

    for (TParameter tParameter : compliParamList) {
      labelNames.add(tParameter.getName());
    }

    return labelNames;

  }


  /**
   * If the Rule returned by the SSD has more or less Dep than the dep list of Parameter then praram rule is incomplete.
   *
   * @param paramWithIncompleteAttrDef List of parameter names with incomplete attr definition
   * @param attrDefForRuleIncomplete true if attr definition is incomplete atleast for a parameter
   * @param ruleList List of rules for the review
   * @return true if the parameters have incomplete rules
   */
  private void findIfParamRulesIncomplete(final List<String> paramWithIncompleteAttrDef, final List<CDRRule> ruleList) {
    if (CommonUtils.isNotEmpty(ruleList)) {
      for (CDRRule rule : ruleList) {
        List<FeatureValueModel> dependencyList = rule.getDependencyList();
        CDRFuncParameter cdrFuncParameter =
            this.cdrDataProvider.getCDRFuncParameter(rule.getParameterName(), rule.getValueType());

        // Icdm-1289- If the Parameter type or the parameter lower upper case is different in ICDM and SSD the CDR func
        // parameter object can be null. So null check is made for CDR func Parameter

        if (CommonUtils.isNotEmpty(dependencyList) && CommonUtils.isNotNull(cdrFuncParameter) &&
            CommonUtils.isNotNull(cdrFuncParameter.getParamAttrs()) &&
            !CommonUtils.isEqual(dependencyList.size(), cdrFuncParameter.getParamAttrs().size())) {
          // avoid checking // the condition for default rule
          paramWithIncompleteAttrDef.add(rule.getParameterName());
          break;

        }
      }
    }

  }

  /**
   * @param treeSet
   * @param labelNames
   * @param paramsToRemove
   * @return
   */
  private Set<FeatureValueModel> getFeatureValModel(final Set<IAttributeMappedObject> treeSet,
      final FeatureAttributeAdapter faAdapter, final Set<String> labelNames,
      final List<IAttributeMappedObject> paramsToRemove)
      throws IcdmException {


    Set<FeatureValueModel> feaValModelSet;

    if (CommonUtils.isNotNull(this.cdrData.getSelPIDCVariant())) {
      feaValModelSet =
          faAdapter.createAllFeaValModel(treeSet, this.cdrData.getSelPIDCVariant(), labelNames, paramsToRemove);
    }
    else {
      feaValModelSet = faAdapter.createAllFeaValModel(treeSet, this.cdrData.getSelPidcVersion());
    }

    return feaValModelSet;
  }

  /**
   * @param a2lCharacterMap
   * @param funNames
   * @param label
   * @param charSet
   * @param dataLoader
   * @param labelNames
   * @param funcParamMap
   */
  private void fillFunLabNames(final Map<String, Characteristic> a2lCharacterMap, final Set<String> funNames,
      final String label, final Set<Characteristic> charSet, final Set<String> labelNames,
      final Map<String, Set<Characteristic>> funcParamMap) {
    Characteristic charObj = a2lCharacterMap.get(label);
    if (charObj != null) {
      Function func = charObj.getDefFunction();
      String funcName;
      if (func == null) {
        funNames.add(ApicConstants.NOT_ASSIGNED);
        funcName = ApicConstants.NOT_ASSIGNED;
        this.cdrData.getUnassParaminReview().add(charObj.getName());
      }
      else {
        funcName = func.getName();
      }
      funNames.add(funcName);
      labelNames.add(label);
      charSet.add(charObj);
      Set<Characteristic> set = funcParamMap.get(funcName);
      if (set == null) {
        set = new HashSet<>();
      }
      set.add(charObj);
      funcParamMap.put(funcName, set);
    }

  }

  /**
   * @param treeSet
   * @param labelNames
   * @param labelNames
   * @param paramNotInRuleset2
   * @return the parameters which are available only in the Cal data map.
   * @throws SsdInterfaceException
   */
  private Set<AbstractParameter<?>> getReviewFuncParamSet(final SortedSet<CDRFuncParameter> treeSet,
      final Set<String> labelNames, final Set<String> paramNotInRuleset)
      throws SsdInterfaceException {

    Set<AbstractParameter<?>> modifiedTreeSet = new TreeSet<>();
    Map<String, CalData> calDataMap = this.cdrData.getCalDataMap();
    RuleSet ruleSet = this.cdrData.getPrimaryReviewRuleSetData().getRuleSet();

    if (CommonUtils.isNotNull(ruleSet)) {
      Map<String, RuleSetParameter> allParameters = ruleSet.getAllParameters(false);
      for (AbstractParameter cdrFuncParameter : treeSet) {
        String paramName = cdrFuncParameter.getName();

        // ICDM-2477
        if (!(allParameters.containsKey(paramName))) {
          paramNotInRuleset.add(paramName);
        }

        if (calDataMap.containsKey(paramName) && (allParameters.containsKey(paramName))) {
          modifiedTreeSet.add(allParameters.get(paramName));
        }
        else if (ApicUtil.isVariantCoded(paramName) && calDataMap.containsKey(paramName) &&
            (allParameters.containsKey(ApicUtil.getBaseParamName(paramName)))) {
          modifiedTreeSet.add(allParameters.get(ApicUtil.getBaseParamName(paramName)));
        }

      }
      if (modifiedTreeSet.isEmpty()) {
        this.cdrSummary.setCdrException(CDRConstants.CDR_EXCEPTION_TYPE.RULE_SET_EXCEPION);
        this.cdrSummary.setExceptioninReview(true);
      }
    }

    else {
      Set<CDRFuncParameter> validFuncParamSet = new HashSet<>();
      for (CDRFuncParameter cdrFuncParameter : treeSet) {

        if (calDataMap.containsKey(cdrFuncParameter.getName())) {
          modifiedTreeSet.add(cdrFuncParameter);
          validFuncParamSet.add(cdrFuncParameter);
        }
        else {
          labelNames.remove(cdrFuncParameter.getName());
        }
      }
      this.cdrDataProvider.fetchParameterAttrDepn(validFuncParamSet);
    }
    return modifiedTreeSet;
  }

  // ICDM-2477
  /**
   * @param allParameters
   * @param paramName
   * @param paramWithoutRule
   */
  private void checkRuleAvailability(final String paramName, final Set<String> paramWithoutRule) {

    if (null != this.cdrData.getPrimarySsdRules()) {
      List<CDRRule> paramRulesList = this.cdrData.getPrimarySsdRules().get(paramName);
      if ((null != paramRulesList) && !paramRulesList.isEmpty()) {
        for (CDRRule paramRule : paramRulesList) {
          if (!paramRule.isRuleComplete()) {
            paramWithoutRule.add(paramName);
          }
        }
      }
      else {
        paramWithoutRule.add(paramName);
      }
    }
    else {
      paramWithoutRule.add(paramName);
    }
  }

  /**
   * invoke check SSD.
   */
  public void invokeCheckSSD() {
    Map<String, CalData> calDataMap = this.cdrData.getCalDataMap();
    A2LFileInfo a2lFileContents = this.cdrData.getA2lFileContents();

    String primarySsdFilePath = this.cdrData.getPrimarySsdFilePath();
    // Task 231284
    // invoke check ssd for primary SSD file
    if (primarySsdFilePath != null) {
      checkSSDCallForSSDFile(calDataMap, a2lFileContents, primarySsdFilePath, true, null);
    }
    // invoke check ssd for secondary SSD files
    for (ReviewRuleSetData ruleSetData : this.cdrData.getRuleSetDataList()) {
      if (ruleSetData.getSsdFilePath() != null) {
        checkSSDCallForSSDFile(calDataMap, a2lFileContents, ruleSetData.getSsdFilePath(), false, ruleSetData);
      }
    }
  }


  /**
   * @param calDataMap
   * @param a2lFileContents
   * @param ssdFilePath
   * @param isPrimaryRule
   * @param ruleSetData
   * @param checkSSDResultParamMap
   */
  private void checkSSDCallForSSDFile(final Map<String, CalData> calDataMap, final A2LFileInfo a2lFileContents,
      final String ssdFilePath, final boolean isPrimaryRule, final ReviewRuleSetData ruleSetData) {
    Map<String, CheckSSDResultParam> checkSSDResultParamMap = new HashMap<String, CheckSSDResultParam>();
    File ssdFile = new File(ssdFilePath);

    if (ssdFile.exists() && (calDataMap != null) && !calDataMap.isEmpty() && (a2lFileContents != null)) {

      CheckSSDInfo checkSSDInfo = new CheckSSDInfo(ssdFilePath);
      checkSSDInfo.setLogger(this.cdrData.getCheckSSDLogger());
      String suffix = "CommonRules";
      if (!this.cdrData.isCommonRulesPrimary()) {
        RuleSet ruleSet = this.cdrData.getPrimaryReviewRuleSetData().getRuleSet();
        suffix = ruleSet.getName();
      }
      if (ruleSetData != null) {
        if ((ruleSetData.getSsdReleaseID() != null) || ruleSetData.isSSDFileReview()) {
          suffix = CDRConstants.SSD_RULES;
        }
        else {
          suffix = ruleSetData.getRuleSet() == null ? "CommonRules" : ruleSetData.getRuleSet().getName();
        }
      }
      Map<String, CalData> clonedCalDataMap = CommonUtils.cloneCalData(calDataMap);

      String currentDate = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
      String reviewName;
      if (isPrimaryRule) {
        reviewName = CDRConstants.MAIN_REVIEW;
      }
      else {
        reviewName = CDRConstants.SECONDARY_REVIEW;
      }
      this.cdrData.getSelFilesPath().forEach(filePath -> this.dataFileName = new File(filePath).getName());
      File dir = new File(ssdFile.getParent() + File.separator + reviewName + CDRConstants.SSD_FILE_PATH_SEPARATOR +
          suffix + CDRConstants.SSD_FILE_PATH_SEPARATOR + currentDate);
      if (!dir.exists()) {
        dir.mkdir();
      }
      checkSSDInfo.setOptFileLocForRpts(dir.getPath());
      checkSSDInfo.setCalDataMap(clonedCalDataMap);
      checkSSDInfo.setA2lFileInfo(a2lFileContents);
      checkSSDInfo.setRptTypeFlag(CHKSSD_GENERATE_ALL_TYPES);
      checkSSDInfo.setA2lFilePath(a2lFileContents.getFileName());
      checkSSDInfo.setHexFilePath(this.dataFileName);
      CheckSSDCoreInstances cssdCoreInstances = new CheckSSDCoreInstances();
      checkSSDInfo.setCssdCoreInstances(cssdCoreInstances);
      try {
        checkSSDInfo.runCheckSSD();
        changeCheckSSDFileNames(checkSSDInfo,
            reviewName + CDRConstants.SSD_FILE_PATH_SEPARATOR + suffix + CDRConstants.SSD_FILE_PATH_SEPARATOR +
                CDRConstants.CHECK_SSD + CDRConstants.SSD_FILE_PATH_SEPARATOR + currentDate,
            checkSSDInfo.getOutPutFilesPath());

        // Task 231284
        if (this.cdrData.getGeneratedCheckSSDFiles() == null) {
          final Set<String> outputFiles = new HashSet<>();
          // log, warn, lab and error file to be not set in the output.
          checkSSDInfo.getOutPutFilesPath().forEach(outputFile -> {
            if (!CommonUtils.containsString(outputFile, unwantedOutputFiles)) {
              outputFiles.add(outputFile);
            }

          });
          this.cdrData.setGeneratedCheckSSDFiles(outputFiles);
        }
        else {
          this.cdrData.getGeneratedCheckSSDFiles().addAll(checkSSDInfo.getOutPutFilesPath());
        }

      }
      catch (CheckSSDException exception) {

        this.cdrSummary.setCdrException(CDRConstants.CDR_EXCEPTION_TYPE.CHECK_SSD_EXCEPTION);
        this.cdrSummary.setExceptioninReview(true);
        this.cdrSummary.setReviewExceptionObj(exception);
      }

      // Report
      CheckSSDReport checkSSDReport = checkSSDInfo.getRptInstance();
      Map<String, ReportModel> reportModelMap = new HashMap<>();
      List<ReportModel> allModels = new ArrayList<>();
      // Value Type
      allModels.addAll(checkSSDReport.getValueModelList());
      allModels.addAll(checkSSDReport.getWildCardValLblList());
      // MapCurve Type
      allModels.addAll(checkSSDReport.getMcvModelList());
      allModels.addAll(checkSSDReport.getMcVblkModelList());
      // ValueBlock Type
      allModels.addAll(checkSSDReport.getVblkModelList());
      allModels.addAll(checkSSDReport.getWildCardVblkLblList());

      for (ReportModel reportModel : allModels) {
        String labelName = reportModel.getLabelName();

        /*
         * ICDM-1460 - If any of the result is not ok , then add it to map , otherwise do not add [eg. AXIS_PTS[4] will
         * have one reportmodel for each of the 4 values.If any of these values are NOT OK then result should be
         * summarised as NOT OK
         */
        ReportModel existingRepModel = reportModelMap.get(labelName);
        if ((existingRepModel == null) || checkIfErrorOccured(reportModel)) {
          reportModelMap.put(labelName, reportModel);
        }
      }

      Map<String, CheckSSDResultParam> checkSSDMap =
          setCheckSSDMap(clonedCalDataMap, checkSSDResultParamMap, reportModelMap);

      if (isPrimaryRule) {
        this.cdrData.setPrimaryCheckSSDResultParamMap(checkSSDMap);
      }
      else if (ruleSetData != null) {
        ruleSetData.setCheckSSDResParamMap(checkSSDMap);
      }

    }
  }


  /**
   * @param checkSSDInfo
   * @param strTobeAdded
   * @param outputStrSet
   */
  private void changeCheckSSDFileNames(final CheckSSDInfo checkSSDInfo, final String newFileName,
      final Set<String> outputStrSet) {
    for (String filePath : checkSSDInfo.getOutPutFilesPath()) {
      if (filePath.endsWith(".xlsx")) {
        File checkSSDFile = new File(filePath);
        try {
          Path newFilePath = Files.move(checkSSDFile.toPath(),
              new File(filePath.replace(checkSSDFile.getName(), newFileName + ".xlsx")).toPath(),
              java.nio.file.StandardCopyOption.REPLACE_EXISTING);
          outputStrSet.remove(checkSSDFile.toString());
          outputStrSet.add(newFilePath.toString());
        }
        catch (IOException ex) {
          this.cdrSummary.setCdrException(CDRConstants.CDR_EXCEPTION_TYPE.CHECK_SSD_EXCEPTION);
          this.cdrSummary.setExceptioninReview(true);
          this.cdrSummary.setReviewExceptionObj(ex);
        }
        break;
      }
    }
  }

  /**
   * ICDM-2440 Review for Compliance Parameters
   *
   * @throws IcdmException
   */
  public void invokeCheckSSDForCompliParam() throws IcdmException {

    // Invoke CheckSSD here
    if (this.compliRvwData.getCompliSSDFilePath() != null) {
      this.cdrData.getSelFilesPath().forEach(filePath -> this.dataFileName = new File(filePath).getName());
      CompliReviewSummary compliRvwSummary = null;
      // CompliReviewSummary compliRvwSummary = this.compliParamRvw.invokeCheckSSD(this.cdrData.getA2lFileContents(),
      // this.compliRvwData.getCompliSSDFilePath(), this.cdrData.getCheckSSDLogger(), this.dataFileName);

      // Result - to be set into SetCommonFields()
      if (compliRvwSummary != null) {
        // Errors
        this.cdrSummary.setCdrException(compliRvwSummary.getCdrException());
        if (compliRvwSummary.getCheckSSDOutData() != null) {
          this.cdrSummary.setExceptioninReview(compliRvwSummary.getCheckSSDOutData().isReviewHasExp());
        }
        this.cdrSummary.setReviewExceptionObj(compliRvwSummary.getReviewExceptionObj());


        if ((compliRvwSummary.getCheckSSDOutData() != null) &&
            !compliRvwSummary.getCheckSSDOutData().isReviewHasExp()) {
          this.compliRvwData
              .setCheckSSDCompliParamMap(compliRvwSummary.getCheckSSDOutData().getCheckSSDCompliParamMap());
          this.compliRvwData
              .setCompliCheckSSDOutputFiles(compliRvwSummary.getCheckSSDOutData().getGeneratedCheckSSDFiles());
          this.compliRvwData.setErrorInSSDfile(compliRvwSummary.getCheckSSDOutData().getErrorinSSDFile());
        }
      }
    }
  }


  /**
   * @param filePath
   * @throws IOException
   */
  private void removeVectorChannel(final String filePath) throws IOException {

    File originalFile = new File(filePath);
    String modifiedFileName = "compliSSD_modified";


    String ssdFilePath = CommonUtils.getSystemUserTempDirPath() + File.separator;
    File modifiedFile = new File(ssdFilePath + modifiedFileName);

    modifiedFile.createNewFile();

    try (BufferedReader reader = new BufferedReader(new FileReader(originalFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(modifiedFile))) {

      String vector = "Vector";

      String channel = "Channel";
      String currentLine;

      while ((currentLine = reader.readLine()) != null) {
        // trim newline when comparing with lineToRemove
        String trimmedLine = currentLine.trim();
        if (trimmedLine.contains(vector) || trimmedLine.contains(channel)) {
          continue;
        }
        writer.write(currentLine + System.getProperty("line.separator"));
      }
    }
    catch (Exception ex) {
      throw ex;
    }

    originalFile.delete();

    File newFiletobeCreated = new File(filePath);
    modifiedFile.renameTo(newFiletobeCreated);

  }


  /**
   * @param calDataMap
   * @param checkSSDResultParamMap
   * @param reportModelMap
   * @return
   */
  public Map<String, CheckSSDResultParam> setCheckSSDMap(final Map<String, CalData> calDataMap,
      final Map<String, CheckSSDResultParam> checkSSDResultParamMap, final Map<String, ReportModel> reportModelMap) {
    for (String labelName : calDataMap.keySet()) {
      CalData calData = calDataMap.get(labelName);
      ReportModel reportModel = null;
      if (reportModelMap != null) {
        reportModel = reportModelMap.get(labelName);
      }
      CheckSSDResultParam checkSSDResultParam = new CheckSSDResultParam(labelName, calData, "", reportModel);
      checkSSDResultParamMap.put(labelName, checkSSDResultParam);
    }
    return checkSSDResultParamMap;
  }

  /**
   * @param reportModel
   * @param existingRepModel
   * @return
   */
  private boolean checkIfErrorOccured(final ReportModel reportModel) {
    // This checks if result is OK
    if ((reportModel.getMessType() == ReportMessages.LOG_MSG) ||
        (reportModel.getMessType() == ReportMessages.WRN_MSG)) {
      return false;
    } // else result not ok
    return true;
  }


  /**
   * @param status
   * @param sourceType
   * @param shapeReviewResult
   * @param isFinish ICDM-2579 true if it is a cancel operation
   * @return
   */
  public CmdModCDRResult saveResults(final CDRConstants.REVIEW_STATUS status, final CDR_SOURCE_TYPE sourceType,
      final DELTA_REVIEW_TYPE deltaReviewType, final ShapeReviewResult shapeReviewResult, final boolean isFinish) {

    final CmdModCDRResult cmdResult;
    if (CommonUtils.isNotNull(this.cdrData.getCanceledCDRResult())) {
      cmdResult = createUpdateResultCommand(status, sourceType, deltaReviewType, shapeReviewResult, isFinish);
    }
    else {
      cmdResult = createResultCommand(status, sourceType, deltaReviewType, shapeReviewResult, isFinish);
    }

    // ICDM-2579
    if (isFinish && (null != deltaReviewType) && !cmdResult.isDeltaReviewValid()) {
      String msgToDisplay = this.apicDataProvider.getMessage("REVIEW_MESSAGE", "DELTA_TO_NORMAL_RVW");
      CDMLogger.getInstance().infoDialog(msgToDisplay, CDRJpaActivator.PLUGIN_ID);
    }
    return cmdResult;
  }

  /**
   * Creates a newe CDR result command
   *
   * @param sourceType
   * @param deltaReviewType
   * @param shapeReviewResult
   * @param isFinish TODO
   * @return the result command
   */
  private CmdModCDRResult createUpdateResultCommand(final CDRConstants.REVIEW_STATUS status,
      final CDR_SOURCE_TYPE sourceType, final DELTA_REVIEW_TYPE deltaReviewType,
      final ShapeReviewResult shapeReviewResult, final boolean isFinish) {
    CmdModCDRResult cmdModCDRResult = new CmdModCDRResult(this.cdrDataProvider, this.cdrData.getCanceledCDRResult(),
        this.cdrData.getParentCdrResult(), deltaReviewType, status, sourceType, false, shapeReviewResult);
    cmdModCDRResult.setA2lFile(this.cdrData.getSelA2LFile());
    // set a2leditor data provider to get the functions list
    cmdModCDRResult.setA2LEditorDp(this.cdrData.getA2lEditorDP());
    cmdModCDRResult.setFunctionSet(this.cdrData.getCdrFunctionsList());

    final Long mappingSource = this.cdrData.getMappingSource();
    if (sourceType == null) {
      cmdModCDRResult.setGrpWorkPkg(CDRConstants.CDR_SOURCE_TYPE.NOT_DEFINED.getTreeDispName());
    }
    else if (mappingSource == null) {
      cmdModCDRResult.setGrpWorkPkg(CDR_SOURCE_TYPE.FUN_FILE.getUIType());
    }
    else {
      setGrpWPForMapped(sourceType, cmdModCDRResult);
    }
    // Icdm-874 set the value of Review Type
    setCommandFields(cmdModCDRResult, deltaReviewType, isFinish);

    this.commandManager.addCommand(cmdModCDRResult, CDRJpaActivator.PLUGIN_ID);
    return cmdModCDRResult;
  }


  /**
   * @param cmdModCDRResult
   * @param deltaReviewType
   * @param isFinish TODO
   */
  private void setCommandFields(final CmdModCDRResult cmdModCDRResult, final DELTA_REVIEW_TYPE deltaReviewType,
      final boolean isFinish) {
    cmdModCDRResult.setReviewType(this.cdrData.getReviewType());
    // Set input files
    cmdModCDRResult.setInputFiles(this.cdrData.getSelFilesPath());

    if (null != this.cdrData.getSelSSDRelease()) {
      cmdModCDRResult.setSsdRelID(this.cdrData.getSelSSDRelease().getReleaseId().longValue());
    }
    // Set the Lab Fun file path
    cmdModCDRResult.setFunLabFile(this.cdrData.getFunLabFilePath());

    // Set rule file
    cmdModCDRResult.setRuleFile(this.cdrData.getPrimarySsdFilePath());
    Set<String> outputFiles = new HashSet<>();


    if ((this.compliRvwData != null) && (this.compliRvwData.getSsdErrorPath() != null)) {
      outputFiles.add(this.compliRvwData.getSsdErrorPath());
    }

    if (this.cdrData.getGeneratedCheckSSDFiles() != null) {
      outputFiles.addAll(this.cdrData.getGeneratedCheckSSDFiles());
    }
    // Set output file
    cmdModCDRResult.setOutputFiles(outputFiles);


    cmdModCDRResult.setPidcVersion(this.cdrData.getSelPidcVersion());

    cmdModCDRResult.setPidcVariant(this.cdrData.getSelPIDCVariant());

    if (deltaReviewType == DELTA_REVIEW_TYPE.PROJECT_DELTA_REVIEW) {
      cmdModCDRResult.setSourcePIDCVariant(this.cdrData.getSourcePIDCVariant());
      cmdModCDRResult.setSourcePidcVer(this.cdrData.getSourcePidcVer());
    }

    cmdModCDRResult.setDescription(this.cdrData.getDescription());

    cmdModCDRResult.setCheckSSDResultParamMap(this.cdrData.getPrimaryCheckSSDResultParamMap());

    // Task 231283
    cmdModCDRResult.setSecondaryRuleSetList(this.cdrData.getRuleSetDataList());

    cmdModCDRResult.setSecResultMap(this.cdrData.getSecResultMap());

    cmdModCDRResult.setSelCalEngineer(this.cdrData.getSelCalEngineer());

    // Set additional participants
    cmdModCDRResult.setSelParticipants(this.cdrData.getSelParticipants());

    cmdModCDRResult.setSelAuditor(this.cdrData.getSelAuditor());

    cmdModCDRResult.setReviewFuncParamMap(this.cdrData.getReviewFuncParamMap());
    cmdModCDRResult.setSsdRuleMap(this.cdrData.getPrimarySsdRules());

    // Icdm-1215 Set the Attr Val Mod Set
    cmdModCDRResult.setAttrValModSet(this.cdrData.getAttrValModel());
    cmdModCDRResult.setRuleSet(this.cdrData.getPrimaryReviewRuleSetData().getRuleSet());

    cmdModCDRResult.setMonicaFilePath(this.cdrData.getMonicaFilePath());

    cmdModCDRResult.setMonicaOutput(this.cdrData.getMonicaOutput());
    // ICDM-1720
    // Set the parameters to be reviewed and their corresponding file
    cmdModCDRResult.setParamFilesMap(this.cdrData.getParamFilesMap());
    // ICDM-2063
    cmdModCDRResult.setQuestionnaireVersions(this.cdrData.getQuestionnaireVersions());
    cmdModCDRResult.setCaldataMap(this.cdrData.getCalDataMap());

    // ICDM-1785
    cmdModCDRResult.setCharacteristicsMap(this.cdrData.getA2lCharMap());

    cmdModCDRResult.setOffReviewType(this.cdrData.isOffParentReviewType());
    cmdModCDRResult.setStartReviewType(this.cdrData.isStartParentReviewType());
    cmdModCDRResult.setOnlyLockedOffReview(this.cdrData.isOnlyLockedOffReview());
    cmdModCDRResult.setOnlyLockedStartResults(this.cdrData.isOnlyLockedStartReview());
    // ICDM-2579
    cmdModCDRResult.setIsFinishOperation(isFinish);

    /*
     * COMPLI related data
     */
    if (this.compliRvwData != null) {
      cmdModCDRResult.setCompliRuleFilePath(this.compliRvwData.getCompliSSDFilePath());
      cmdModCDRResult.setParamsWithNoCompliRules(this.compliRvwData.getParamsWithNoRules());
      cmdModCDRResult.setSSDRuleForCompliParam(this.compliRvwData.getSsdRulesForCompliance());
      // cmdModCDRResult.setCheckSSDCompliParamMap(this.compliRvwData.getCheckSSDCompliParamMap());

      cmdModCDRResult.setErrorinSSDFile(this.compliRvwData.isErrorinSSDFile());
      cmdModCDRResult.setOutputCompliFiles(this.compliRvwData.getCompliCheckSSDOutputFiles());


      // cmdModCDRResult.addAttrValModel(this.compliRvwData.getAttrValueModSet());
    }
  }

  /**
   * Creates a newe CDR result command
   *
   * @param sourceType
   * @param shapeReviewResult
   * @param isFinish
   * @return the result command
   */
  private CmdModCDRResult createResultCommand(final CDRConstants.REVIEW_STATUS status, final CDR_SOURCE_TYPE sourceType,
      final DELTA_REVIEW_TYPE deltaReviewType, final ShapeReviewResult shapeReviewResult, final boolean isFinish) {
    CmdModCDRResult cmdModCDRResult;
    if (this.cdrData.isDeltaReview()) {
      cmdModCDRResult = new CmdModCDRResult(this.cdrDataProvider, this.cdrData.getParentCdrResult(), deltaReviewType,
          status, sourceType, shapeReviewResult);
    }
    else {
      cmdModCDRResult = new CmdModCDRResult(this.cdrDataProvider, status, sourceType, shapeReviewResult);
    }

    cmdModCDRResult.setA2lFile(this.cdrData.getSelA2LFile());
    // ICDM-1720
    // set a2leditor data provider to get the functions list
    cmdModCDRResult.setA2LEditorDp(this.cdrData.getA2lEditorDP());
    cmdModCDRResult.setFunctionSet(this.cdrData.getCdrFunctionsList());

    final Long mappingSource = this.cdrData.getMappingSource();

    if (mappingSource == null) {
      // ICDM-2032
      cmdModCDRResult.setGrpWorkPkg(CDR_SOURCE_TYPE.FUN_FILE.getUIType());
    }
    else {
      setGrpWPForMapped(sourceType, cmdModCDRResult);
    }

    setCommandFields(cmdModCDRResult, deltaReviewType, isFinish);
    this.commandManager.addCommand(cmdModCDRResult, CDRJpaActivator.PLUGIN_ID);
    return cmdModCDRResult;
  }


  /**
   * @param sourceType
   * @param cmdModCDRResult
   */
  private void setGrpWPForMapped(final CDR_SOURCE_TYPE sourceType, final CmdModCDRResult cmdModCDRResult) {
    if (sourceType == CDR_SOURCE_TYPE.WORK_PACKAGE) {
      setFieldsForWPSrc(cmdModCDRResult);
    }
    else if (sourceType == CDR_SOURCE_TYPE.GROUP) {
      setFieldsForGrpSrc(cmdModCDRResult);
    }
    // ICDM-2026
    else if (sourceType == CDR_SOURCE_TYPE.LAB_FILE) {
      cmdModCDRResult.setGrpWorkPkg(CDR_SOURCE_TYPE.LAB_FILE.getUIType());
    }
    else if ((sourceType == CDR_SOURCE_TYPE.FUN_FILE) || (sourceType == CDR_SOURCE_TYPE.A2L_FILE)) {
      cmdModCDRResult.setGrpWorkPkg(CDR_SOURCE_TYPE.FUN_FILE.getUIType());
    }
    else if (sourceType == CDR_SOURCE_TYPE.REVIEW_FILE) {
      cmdModCDRResult.setGrpWorkPkg(CDR_SOURCE_TYPE.REVIEW_FILE.getUIType());
    }
    else if (sourceType == CDR_SOURCE_TYPE.MONICA_FILE) {
      cmdModCDRResult.setGrpWorkPkg(CDR_SOURCE_TYPE.MONICA_FILE.getUIType());
    }
    else if (sourceType == CDR_SOURCE_TYPE.COMPLI_PARAM) {// Task 237135
      cmdModCDRResult.setGrpWorkPkg(CDR_SOURCE_TYPE.COMPLI_PARAM.getUIType());
    }
  }


  /**
   * @param cmdModCDRResult
   */
  private void setFieldsForGrpSrc(final CmdModCDRResult cmdModCDRResult) {
    if (this.cdrData.getSelA2LGroup() == null) {
      cmdModCDRResult.setGrpWorkPkg(CDRConstants.MUL_GRP);
    }
    else {
      cmdModCDRResult.setGrpWorkPkg(this.cdrData.getSelA2LGroup().getGroupName());
    }
  }


  /**
   * @param cmdModCDRResult
   */
  private void setFieldsForWPSrc(final CmdModCDRResult cmdModCDRResult) {
    if (this.cdrData.getSelWorkPackage() == null) {
      cmdModCDRResult.setGrpWorkPkg(CDRConstants.MUL_FC2WP);
    }
    else {
      cmdModCDRResult
          .setFcToWp(this.cdrDataProvider.getWorkPackageDivision(this.cdrData.getSelWorkPackage().getFc2wp().getId()));
      cmdModCDRResult.setGrpWorkPkg(CDRConstants.FC2WP);
    }
  }

  ILoggerAdapter getLogger() {
    return ObjectStore.getInstance().getLogger();
  }

  /**
   * @param a2lGroup a2lGroup
   * @return the set of Label names
   */
  public Set<String> resolveLabelsForA2lGrp(final A2LGroup a2lGroup) {
    this.cdrData.setA2lCharMap(this.cdrData.getA2lEditorDP().getCharacteristicsMap());
    HashSet<String> labelNames = new HashSet<>();

    labelNames.addAll(a2lGroup.getLabelMap().values());
    return labelNames;

  }

  /**
   * @param selectedElement list of a2l Groups
   * @return the function list
   */
  public SortedSet<Function> resolveFunsForA2lGrp(final List selectedElement) {

    A2LGroup a2lGroup = (A2LGroup) selectedElement.get(0);
    return getFuncsForGrp(a2lGroup);
  }


  /**
   * @param a2lGroup a2lGroup object
   * @return the functions list for a group
   */
  public SortedSet<Function> getFuncsForGrp(final A2LGroup a2lGroup) {
    this.cdrData.setA2lCharMap(this.cdrData.getA2lEditorDP().getCharacteristicsMap());
    Set<String> funcNameStr = new HashSet<>();
    SortedSet<Function> funList = new TreeSet<Function>();
    for (String label : a2lGroup.getLabelMap().values()) {
      Characteristic charObj = this.cdrData.getA2lCharMap().get(label);
      Function func = charObj.getDefFunction();

      if (func == null) {
        // In general, this would not happen
        continue;
      }
      if (!funcNameStr.contains(func.getName())) {
        funcNameStr.add(func.getName());
        funList.add(func);
      }

    }
    return funList;
  }


  /**
   * @param selectedFileName selectedFileName
   * @return true if the dcm file name is same as the onw in MoniCa report
   */
  public boolean isValidDcm(final String selectedFileName) {
    String dcmFilePath = this.cdrData.getMonicaOutput().getDcmFilePath();
    dcmFilePath = dcmFilePath.substring(dcmFilePath.lastIndexOf('\\') + 1);
    if (CommonUtils.isEqual(dcmFilePath, selectedFileName)) {
      return true;
    }
    return false;
  }


  /**
   * @return the qsWithActiveVersions
   */
  public SortedSet<QuestionnaireVersion> getQsWithActiveVersions() {
    return this.qsWithActiveVersions;
  }


  /**
   * @return the qsWithoutActiveVersions
   */
  public SortedSet<Questionnaire> getQsWithoutActiveVersions() {
    return this.qsWithoutActiveVersions;
  }


  /**
   * @return the qsToBeCreated
   */
  public List<IcdmWorkPackage> getQsToBeCreated() {
    return this.qsToBeCreated;
  }


  /**
   * @return the wpDivsWithQs
   */
  public List<Long> getWpDivsWithQs() {
    return this.wpDivsWithQs;
  }

  /**
   * Find the workpackages which a. does not have questionnaire - will be stored in 'qsToBeCreated' list b. which has
   * questionnaire and no active version - will be stored in 'qsWithoutActiveVersions' set c. which has questionnaire
   * and active version - will be stored in 'qsWithActiveVersions' set
   */
  public void fillWpTypes(final SortedSet<IcdmWorkPackage> icdmWps) {
    AttributeValue division = this.cdrData.getDivision();
    boolean toBeCreated;
    if (this.cdrDataProvider.getMappedWrkPkgMap().containsKey(division)) {

      // ICDM-2646
      SortedSet<IcdmWorkPackage> wpSet =
          new TreeSet<IcdmWorkPackage>(this.cdrDataProvider.getIcdmWorkPackageMapForDiv(division).values());
      HashSet<IcdmWorkPackage> existngWpList = new HashSet<>();

      // Set to hold the work packages with selected FC2WP type (division) & which are present in the
      // T_WORKPACKAGE_DIVISION table and have questionaire in
      // the T_QUESTIONNAIRE table
      HashSet<IcdmWorkPackage> allDivWpsWithQn = new HashSet<>();

      // Set to hold the work packages with selected FC2WP type (division) & which are present in the
      // T_WORKPACKAGE_DIVISION table and no questionaire in the
      // T_QUESTIONNAIRE table
      HashSet<IcdmWorkPackage> divWpsWithoutQn = new HashSet<>();

      for (Questionnaire quest : this.cdrDataProvider.getQuestionnaireMap().values()) {
        if (quest.getWorkPackageDivision().getDivision().getID().equals(division.getID())) {
          if (wpSet.contains(quest.getWorkPackageDivision().getWorkPackage())) {
            existngWpList.add(quest.getWorkPackageDivision().getWorkPackage());
          }
          // Add all the wp with quest with the selected division to the set
          quest.getWorkPackageDivision().getWorkPackage().getName();

        }
      }

      for (Questionnaire quest : this.cdrDataProvider.getAllQuestionares(true)) {
        if (quest.getWorkPackageDivision().getDivision().getID().equals(division.getID())) {
          allDivWpsWithQn.add(quest.getWorkPackageDivision().getWorkPackage());
        }
      }

      for (IcdmWorkPackage wp : wpSet) {
        if (!allDivWpsWithQn.contains(wp)) {
          divWpsWithoutQn.add(wp);
        }
      }

      Collection<WorkPackageDivision> wpList = this.cdrDataProvider.getWrkPkgDivisionMap().values();
      for (IcdmWorkPackage icdmWp : icdmWps) {
        toBeCreated = true;
        for (WorkPackageDivision wpDiv : wpList) {
          if (wpDiv.getWorkPackage().getID().equals(icdmWp.getID()) &&
              wpDiv.getDivision().getID().equals(division.getID())) {
            // get the questionnaire corresponding to this mapping
            this.wpDivsWithQs.add(wpDiv.getID());
            toBeCreated = false;
            break;
          }
        }

        if (divWpsWithoutQn.contains(icdmWp) || (!existngWpList.contains(icdmWp) && toBeCreated)) {
          // If the wp is not having a questionaire with the selected division, create an entry in the questionnaire
          // table
          this.qsToBeCreated.add(icdmWp);
        }
      }
    }
    else {
      this.qsToBeCreated.addAll(icdmWps);
    }
    if (!this.wpDivsWithQs.isEmpty()) {
      // get the questionnaires with or without active version
      this.qsWithActiveVersions =
          this.cdrDataProvider.getQuestionnaireVersions(this.wpDivsWithQs, this.qsWithoutActiveVersions);
    }
  }

  /**
   * @param reviewFuncsSet reviewFuncsSet
   */
  public void setUnassignedParamsInReview(final SortedSet<String> reviewFuncsSet) {
    for (String funcName : reviewFuncsSet) {
      if (CommonUtils.isEqual(funcName, ApicConstants.NOT_ASSIGNED)) {
        this.cdrData.getUnassParaminReview().addAll(getUnassignedParamNames());
      }
    }
  }


  /**
   * @return the unassigned param names
   */
  public List<String> getUnassignedParamNames() {
    A2LEditorDataProvider a2lEditorDP = this.cdrData.getA2lEditorDP();
    List<String> unsassParamNames = new ArrayList<>();
    for (A2LParameter parameter : a2lEditorDP.getUnassignedParams()) {
      unsassParamNames.add(parameter.getName());
    }
    return unsassParamNames;
  }

  /**
   * Identify compli params.
   *
   * @param paramSet the param set
   * @return
   */
  private Set<String> identifyCompliParams(final SortedSet<CDRFuncParameter> paramSet) {
    // iterate through the param set
    Set<String> compliParamSet = new HashSet<String>();
    for (CDRFuncParameter cdrFuncParameter : paramSet) {
      if (cdrFuncParameter.isComplianceParameter()) {
        // if the parameter is complaince parameter
        compliParamSet.add(cdrFuncParameter.getName());
      }
    }
    if (compliParamSet.size() > 0) {
      this.cdrData.setCompliParamsPresent(true);
    }
    return compliParamSet;
  }


  /**
   * Sets the A2L base components.
   *
   * @param bcInfo the new a2l base components
   */
  public void setA2LBaseComponents(final SortedSet<A2LBaseComponents> bcInfo) {
    this.bCInfo = bcInfo;
  }


  /**
   * @return the Ssd software releases
   */
  public List<SSDRelease> getSSDRelesesBySwVersionId() {

    Long ssdSoftwareVersionID = this.cdrData.getSelA2LFile().getPidcA2l().getSsdSoftwareVersionID();
    if (ssdSoftwareVersionID != null) {
      try {
        final CdrReviewDataLoader dataLoader = new CdrReviewDataLoader(this.cdrDataProvider);
        return dataLoader.getSSDRelesesBySwVersionId(ssdSoftwareVersionID);
      }
      catch (Exception exp) {

      }
    }
    return null;
  }

  /**
   * @param releaseId
   * @return the release id.
   * @throws Exception releaseId get the rules based on release id.
   */
  public List<CDRRule> readRuleForRelease(final BigDecimal releaseId) throws Exception {
    final CdrReviewDataLoader dataLoader = new CdrReviewDataLoader(this.cdrDataProvider);
    return dataLoader.readRuleForRelease(releaseId);
  }

  /**
   * @param path patyh of the Temp Folder
   * @param releaseId releaseId
   * @return the path of the SSD file
   * @throws Exception
   */
  public String getReleaseReportsByReleaseId(final String path, final BigDecimal releaseId) throws Exception {
    final CdrReviewDataLoader dataLoader = new CdrReviewDataLoader(this.cdrDataProvider);
    return dataLoader.getReleaseReportsByReleaseId(path, releaseId);
  }
}
