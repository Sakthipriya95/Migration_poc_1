/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.shapereview;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.calcomp.catdata.catdatamodel.AnalysisRequest;
import com.bosch.calcomp.catdata.catdatamodel.AnalysisResult;
import com.bosch.calcomp.catdata.catdatamodel.ClassType;
import com.bosch.calcomp.catdata.catdatamodel.PVER;
import com.bosch.calcomp.shapeanalysis.VO.DatasetMarkerEntryVO;
import com.bosch.calcomp.shapeanalysis.VO.LabelValueAnalysisVO;
import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldataphy.CalDataPhy;
import com.bosch.caltool.apic.vcdminterface.VCDMInterface;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.A2LFileInfoLoader;
import com.bosch.caltool.icdm.bo.cdr.VcdmPstContentLoader;
import com.bosch.caltool.icdm.bo.cdr.VcdmPstLoader;
import com.bosch.caltool.icdm.bo.cdr.review.IReviewProcessResolver;
import com.bosch.caltool.icdm.bo.cdr.review.ReviewedInfo;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.logger.A2LLogger;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.logger.EASEELogger;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponentFunctions;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponents;
import com.bosch.caltool.icdm.model.cdr.VcdmPst;
import com.bosch.caltool.icdm.model.cdr.VcdmPstContent;

/**
 * @author bne4cob
 */
public class ShapeReview implements IReviewProcessResolver {

  /**
   * review result
   */
  private final ShapeReviewResult result = new ShapeReviewResult();


  private ShapeReviewInput rvwInput;

  private Set<String> paramsForReview;

  /**
   * Map holding parsed caldata objects
   */
  private final Map<String, CalData> calDataMap;
  private String catXmlPath;

  private VCDMInterface vcdmServiceHandler;
  ServiceData serviceData;
  final A2LFileInfo a2lFileContents;


  /**
   * Constructor
   *
   * @param serviceData common service data
   * @param input shape review input
   * @param apicDataProvider
   * @param serviceHandler
   * @param a2lFile2
   */
  public ShapeReview(final Map<String, CalData> calDataMap, final A2LFileInfo a2lFileContents,
      final ServiceData serviceData) {
    this.calDataMap = calDataMap;
    this.a2lFileContents = a2lFileContents;
    this.serviceData = serviceData;
  }


  /**
   * Gets the vcdm interface for super user.
   *
   * @return the vcdm interface for super user
   * @throws DataException
   */
  private VCDMInterface getVcdmInterfaceForSuperUser() throws DataException {
    VCDMInterface vCdmSuperUser = null;
    try {
      vCdmSuperUser = new VCDMInterface(EASEELogger.getInstance(), A2LLogger.getInstance());
    }
    catch (Exception exp) {
      throw new DataException("VCDM Webservice login failed. Opening A2L Files is not possible. " + exp.getMessage(),
          exp);
    }
    return vCdmSuperUser;
  }

  /**
   * Runs shape review
   */
  @Override
  public void performReview() throws DataException {
    this.vcdmServiceHandler = getVcdmInterfaceForSuperUser();
    A2LFileInfoLoader a2lfileInfoLoader = new A2LFileInfoLoader(this.serviceData);
    Map<String, A2LBaseComponents> a2lBcMap = a2lfileInfoLoader.getA2lBaseComponents(this.a2lFileContents.getId());
    SortedSet<A2LBaseComponents> bcSet = new TreeSet<A2LBaseComponents>(a2lBcMap.values());
    this.rvwInput = new ShapeReviewInput(bcSet, this.a2lFileContents);

    // Set all review params to the review input
    this.rvwInput.setDataReviewParamsSet(this.calDataMap.keySet());
    // set the caldata map to the review input
    this.rvwInput.setDataReviewInputDataMap(this.calDataMap);
    this.paramsForReview = this.rvwInput.getDataReviewParamsSet();
    // Get risky BCs from risk analyser
    RiskAnalysis riskAnalysis = new RiskAnalysis();
    if (!riskAnalysis.hasRisk()) {
      this.result.setStatus(SHAPE_STATUS.NO_RISKY_BCS);
      return;
    }

    // Download CAT xml file (from VCDM - a2l artifacts)
    boolean isFileAvailable = false;
    VcdmPstLoader pstLoader = new VcdmPstLoader(this.serviceData);
    VcdmPst vcdmPst = pstLoader.fetchvCDMPST(this.a2lFileContents.getId());
    VcdmPstContentLoader pstConstLoader = new VcdmPstContentLoader(this.serviceData);
    if (vcdmPst != null) {
      for (final VcdmPstContent cont : pstConstLoader.getPSTContents(vcdmPst.getId())) {
        if ("DALB".equalsIgnoreCase(cont.getVcdmClass())) {

          String catFileName = cont.getVcdmClass() + "_" + cont.getFileName();
          String tempDir = System.getProperty("java.io.tmpdir");
          this.catXmlPath = tempDir + catFileName;
          try {
            isFileAvailable =
                this.vcdmServiceHandler.getvCDMPSTArtifacts(catFileName, cont.getFileId().toString(), tempDir);
          }
          catch (Exception exp) {
            CDMLogger.getInstance().warn(exp.getMessage(), exp, Activator.PLUGIN_ID);
          }
          break;
        }
      }
    }
    if (isFileAvailable) {
      // Get the pver from CAT data xml file
      CatResultLoader catResLoader = new CatResultLoader();
      PVER catResultPVer = catResLoader.fetchCatResults(this.catXmlPath);

      // Risky Params for shape review
      Set<String> riskyParamsForShapeRev = new HashSet<>();

      // Find the parameters in the risky BCs
      Set<String> riskyBCsSet = riskAnalysis.findRiskyBCs();
      if ((null != riskyBCsSet) && !riskyBCsSet.isEmpty()) {
        Map<String, Set<String>> riskyBCParamsMap = getRiskyBcParams(riskyBCsSet, catResultPVer);
        for (Set<String> riskyParams : riskyBCParamsMap.values()) {
          riskyParamsForShapeRev.addAll(riskyParams);
        }
      }
      else {
        riskyParamsForShapeRev.addAll(findParamsForShapeReview(catResultPVer));
      }

      // Main method to execute shape review
      if (!riskyParamsForShapeRev.isEmpty()) {
        executeShapeReview(riskyParamsForShapeRev);
      }
      else {
        this.result.setStatus(SHAPE_STATUS.NO_RISKY_PARAMS);
      }
    }
    else {
      this.result.setStatus(SHAPE_STATUS.CAT_FILE_NOT_FOUND);
    }
  }


  /**
   * @param riskyBCsSet
   * @param catResultPVer
   * @return
   */
  private Map<String, Set<String>> getRiskyBcParams(final Set<String> riskyBCsSet, final PVER catResultPVer) {

    Map<String, Set<String>> riskyBCParamsMap = new HashMap<>();
    Set<String> riskyParamsIdentified = new HashSet<>();
    Map<String, Set<String>> bcFcMapFromCAT = getBcFCMapFromCATData(catResultPVer);
    Map<String, Map<String, Set<String>>> bcFcParamMapFromCAT = getFcParamMapFromCATData(catResultPVer, bcFcMapFromCAT);
    Set<String> riskyBCsNotInCAT = new HashSet<>();

    for (String riskyBc : riskyBCsSet) {
      if (null != bcFcParamMapFromCAT.get(riskyBc)) {
        Set<String> riskyParams = new HashSet<>();
        Map<String, Set<String>> fcParamMap = bcFcParamMapFromCAT.get(riskyBc);
        for (Set<String> paramSet : fcParamMap.values()) {
          Set<String> paramsInFc = paramSet;
          paramsInFc.retainAll(this.paramsForReview);
          riskyParams.addAll(paramsInFc);
          riskyParamsIdentified.addAll(paramsInFc);
        }
        riskyBCParamsMap.put(riskyBc, riskyParams);
      }
      else {
        riskyBCsNotInCAT.add(riskyBc);
      }
    }

    getFcParamMapFromA2l(riskyBCsNotInCAT, riskyParamsIdentified, riskyBCParamsMap);

    return riskyBCParamsMap;
  }

  /**
   * @param riskyBCsSet
   * @param riskyParamsIdentified
   * @param paramsForReview
   * @param riskyBCParamsMap
   * @return
   */
  private Map<String, Map<String, Set<String>>> getFcParamMapFromA2l(final Set<String> riskyBCsNotInCAT,
      final Set<String> riskyParamsIdentified, final Map<String, Set<String>> riskyBCParamsMap) {

    Map<String, Map<String, Set<String>>> bcFcParamMapFromA2l = new HashMap<String, Map<String, Set<String>>>();

    Map<String, Characteristic> paramMap = this.a2lFileContents.getAllModulesLabels();
    Map<String, Characteristic> riskyParamMap = new HashMap<>();
    this.paramsForReview.removeAll(riskyParamsIdentified);
    for (String param : this.paramsForReview) {
      riskyParamMap.put(param, paramMap.get(param));
    }

    for (String riskyBc : riskyBCsNotInCAT) {
      Map<String, Set<String>> fcParamMap = new HashMap<>();
      A2LBaseComponents a2lRiskyBc = null;
      for (A2LBaseComponents a2lBc : this.rvwInput.getA2lBcSet()) {
        if (a2lBc.getBcName().equalsIgnoreCase(riskyBc)) {
          a2lRiskyBc = a2lBc;
          break;
        }
      }

      if (null != a2lRiskyBc) {
        for (Characteristic param : riskyParamMap.values()) {
          String funcName = param.getDefFunction().getName();
          for (A2LBaseComponentFunctions a2lFunc : a2lRiskyBc.getFunctionsList()) {
            if (a2lFunc.getName().equalsIgnoreCase(funcName)) {
              if (null == fcParamMap.get(funcName)) {
                Set<String> paramSet = new HashSet<>();
                paramSet.add(param.getName());
                fcParamMap.put(funcName, paramSet);
              }
              else {
                fcParamMap.get(funcName).add(param.getName());
              }
            }
          }
        }
      }

      bcFcParamMapFromA2l.put(riskyBc, fcParamMap);

      Set<String> allFcParams = new HashSet<>();
      for (Set<String> paramSet : fcParamMap.values()) {
        allFcParams.addAll(paramSet);
      }
      riskyBCParamsMap.put(riskyBc, allFcParams);
    }
    return bcFcParamMapFromA2l;
  }

  /**
   * @param catResultPVer
   * @param bcFcMapFromCAT
   * @return
   */
  private Map<String, Map<String, Set<String>>> getFcParamMapFromCATData(final PVER catResultPVer,
      final Map<String, Set<String>> bcFcMapFromCAT) {

    Map<String, Map<String, Set<String>>> bcFcParamMap = new HashMap<String, Map<String, Set<String>>>();

    List<AnalysisResult> pverAnalysisResultList = catResultPVer.getAnalysisResults().getAnalysisResult();

    for (Entry<String, Set<String>> bcFc : bcFcMapFromCAT.entrySet()) {
      Map<String, Set<String>> fcParamMap = new HashMap<String, Set<String>>();
      for (String fc : bcFc.getValue()) {
        checkAnalysisResults(bcFcParamMap, pverAnalysisResultList, bcFc, fcParamMap, fc);
      }
    }
    return bcFcParamMap;
  }

  /**
   * @param bcFcParamMap
   * @param pverAnalysisResultList
   * @param bcFc
   * @param fcParamMap
   * @param fc
   */
  private void checkAnalysisResults(final Map<String, Map<String, Set<String>>> bcFcParamMap,
      final List<AnalysisResult> pverAnalysisResultList, final Entry<String, Set<String>> bcFc,
      final Map<String, Set<String>> fcParamMap, final String fc) {
    for (AnalysisResult analysisResult : pverAnalysisResultList) {
      if (isCurrBcFc(bcFc, fc, analysisResult)) {

        Set<String> paramSet = new HashSet<>();
        paramSet.addAll(analysisResult.getCharacteristics().getCharacteristic());
        fcParamMap.put(fc, paramSet);

        if (bcFcParamMap.keySet().contains(analysisResult.getContainerIdentifier().getName())) {
          bcFcParamMap.get(bcFc.getKey()).putAll(fcParamMap);
        }
        else {
          bcFcParamMap.put(bcFc.getKey(), fcParamMap);
        }
      }
    }
  }

  /**
   * @param bcFc
   * @param fc
   * @param analysisResult
   * @return
   */
  private boolean isCurrBcFc(final Entry<String, Set<String>> bcFc, final String fc,
      final AnalysisResult analysisResult) {
    return (null != analysisResult.getContainerIdentifier()) && checkClazzType(analysisResult) &&
        analysisResult.getElementIdentifier().getName().equalsIgnoreCase(fc) &&
        analysisResult.getContainerIdentifier().getName().equalsIgnoreCase(bcFc.getKey());
  }

  /**
   * @param analysisResult
   * @return
   */
  private boolean checkClazzType(final AnalysisResult analysisResult) {
    return analysisResult.getElementIdentifier().getClazz().equals(ClassType.FC) &&
        analysisResult.getContainerIdentifier().getClazz().equals(ClassType.BC);
  }

  /**
   * @param riskyBCParams
   * @param catResultPVer
   * @return
   */
  private Map<String, Set<String>> getBcFCMapFromCATData(final PVER catResultPVer) {

    Map<String, Set<String>> bcFcMap = new HashMap<String, Set<String>>();

    if ((null != catResultPVer.getAnalysisInputs()) && (null != catResultPVer.getAnalysisInputs().getAnalysisInput())) {
      List<AnalysisRequest> pverAnalysisInputList = catResultPVer.getAnalysisInputs().getAnalysisInput();

      for (AnalysisRequest analysisInput : pverAnalysisInputList) {
        if ((null != analysisInput.getContainerElement()) &&
            analysisInput.getInputElement().getElementClass().equals(ClassType.FC) &&
            analysisInput.getContainerElement().getContainerClass().equals(ClassType.BC)) {

          if (bcFcMap.keySet().contains(analysisInput.getContainerElement().getContainerName())) {
            bcFcMap.get(analysisInput.getContainerElement().getContainerName())
                .add(analysisInput.getInputElement().getElementName());
          }
          else {
            Set<String> fcList = new HashSet<>();
            fcList.add(analysisInput.getInputElement().getElementName());
            bcFcMap.put(analysisInput.getContainerElement().getContainerName(), fcList);
          }
        }
      }
    }
    else {
      CDMLogger.getInstance().debug("No Analysis inputs found in the CAT file", Activator.PLUGIN_ID);
    }
    return bcFcMap;
  }

  /**
   * @param riskyParamsForShapeRev
   */
  private void executeShapeReview(final Set<String> riskyParamsForShapeRev) {
    Map<String, ShapeReviewParamResult> paramResultMap = new ConcurrentHashMap<String, ShapeReviewParamResult>();
    // Invoke shape analysis tool
    for (String riskyParam : riskyParamsForShapeRev) {
      if (null != this.rvwInput.getDataReviewInputDataMap().get(riskyParam)) {
        // Get the caldataphy object of the parameter
        CalDataPhy paramCalDataPhy = this.rvwInput.getDataReviewInputDataMap().get(riskyParam).getCalDataPhy();
        LabelValueAnalysisVO labelValAnalysis = new LabelValueAnalysisVO(Long.valueOf(0), paramCalDataPhy);
        List<DatasetMarkerEntryVO> findingsList = labelValAnalysis.getShapeAnalysisFindings();

        ShapeReviewParamResult srParamRes = new ShapeReviewParamResult();
        srParamRes.setParamName(riskyParam);
        if (findingsList.isEmpty()) {
          // SHAPE RESULT: OK
          srParamRes.setResult(CommonUtilConstants.CODE_PASSED);
        }
        else {
          // SHAPE RESULT: ERROR
          srParamRes.setResult(CommonUtilConstants.CODE_FAILED);
          StringBuilder errDetails = new StringBuilder();
          for (DatasetMarkerEntryVO dataEntry : findingsList) {
            errDetails.append(dataEntry.getComment());
            errDetails.append(";");
            errDetails.append(dataEntry.getLongComment());
            errDetails.append("\n");
          }
          srParamRes.setErrorDetails(errDetails.toString());
        }

        paramResultMap.put(riskyParam, srParamRes);
      }
      else {
        CDMLogger.getInstance().debug("CalData not available for parameter:" + riskyParam, Activator.PLUGIN_ID);
      }

    }

    this.result.setParamResultMap(paramResultMap);
    if (this.result.getParamResultMap().isEmpty()) {
      this.result.setStatus(SHAPE_STATUS.FINISHED_NO_FINDINGS);
    }
  }

  /**
   * @param catResultPVer
   */
  private Set<String> findParamsForShapeReview(final PVER catResultPVer) {
    // Set of risky parameters for shape review
    Set<String> riskyParamsForRev = new HashSet<>();

    // Identify the paramters to be executed for shape review
    if (isAnalysisResultsValid(catResultPVer)) {
      List<AnalysisResult> pverAnalysisResultList = catResultPVer.getAnalysisResults().getAnalysisResult();
      for (AnalysisResult analysisResult : pverAnalysisResultList) {
        if (isCharListValid(analysisResult)) {
          List<String> charParams = analysisResult.getCharacteristics().getCharacteristic();
          charParams.retainAll(this.paramsForReview);
          riskyParamsForRev.addAll(charParams);
        }
      }
    }
    else {
      CDMLogger.getInstance().debug("No Analysis Results available from CAT tool", Activator.PLUGIN_ID);
    }
    return riskyParamsForRev;
  }

  /**
   * @param analysisResult
   * @return
   */
  private boolean isCharListValid(final AnalysisResult analysisResult) {
    return (null != analysisResult.getCharacteristics()) &&
        (null != analysisResult.getCharacteristics().getCharacteristic()) &&
        !analysisResult.getCharacteristics().getCharacteristic().isEmpty();
  }

  /**
   * @param catResultPVer
   * @return
   */
  private boolean isAnalysisResultsValid(final PVER catResultPVer) {
    return (null != catResultPVer.getAnalysisResults()) &&
        (null != catResultPVer.getAnalysisResults().getAnalysisResult()) &&
        !catResultPVer.getAnalysisResults().getAnalysisResult().isEmpty();
  }


  /**
   * Return the shape review results
   */
  public void getReviewResults() {
    // Change to appropriate return type
  }

  /**
   * @return the status
   */
  public SHAPE_STATUS getStatus() {
    return this.result.getStatus();
  }

  /**
   * @return the result
   */
  public ShapeReviewResult getResult() {
    return this.result;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public ReviewedInfo getReviewOutput() {
    // TODO Auto-generated method stub
    return null;
  }

}
