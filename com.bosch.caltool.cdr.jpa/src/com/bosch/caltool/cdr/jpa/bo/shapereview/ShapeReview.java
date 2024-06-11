/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo.shapereview;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.calcomp.catdata.catdatamodel.AnalysisRequest;
import com.bosch.calcomp.catdata.catdatamodel.AnalysisResult;
import com.bosch.calcomp.catdata.catdatamodel.ClassType;
import com.bosch.calcomp.catdata.catdatamodel.PVER;
import com.bosch.calcomp.shapeanalysis.VO.DatasetMarkerEntryVO;
import com.bosch.calcomp.shapeanalysis.VO.LabelValueAnalysisVO;
import com.bosch.calmodel.caldataphy.CalDataPhy;
import com.bosch.caltool.a2l.jpa.A2LEditorDataProvider;
import com.bosch.caltool.a2l.jpa.bo.A2LBaseComponentFunctions;
import com.bosch.caltool.a2l.jpa.bo.A2LBaseComponents;
import com.bosch.caltool.a2l.jpa.bo.A2LParameter;
import com.bosch.caltool.apic.jpa.bo.A2LFile;
import com.bosch.caltool.apic.jpa.bo.ApicDataProvider;
import com.bosch.caltool.apic.jpa.bo.VCDMPST;
import com.bosch.caltool.apic.jpa.bo.VCDMPSTContents;
import com.bosch.caltool.apic.vcdminterface.VCDMInterface;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.logger.CDMLogger;

/**
 * @author bne4cob
 */
public class ShapeReview {

  /**
   * Stores the input and output data related to shape analysis
   */
  private final ShapeReviewData data;

  /**
   * review result
   */
  private final ShapeReviewResult result = new ShapeReviewResult();

  private final A2LFile a2lFile;

  private final ReviewInput rvwInput;

  private final Set<String> paramsForReview;

  final ApicDataProvider apicDataProvider;

  private String catXmlPath;

  private final VCDMInterface vcdmServiceHandler;

  /**
   * Constructor
   *
   * @param serviceData common service data
   * @param input shape review input
   * @param apicDataProvider
   * @param serviceHandler
   * @param a2lFile2
   */
  public ShapeReview(final ServiceData serviceData, final ReviewInput input, final ApicDataProvider apicDataProvider,
      final A2LFile a2lFile, final VCDMInterface serviceHandler) {
    this.data = new ShapeReviewData(serviceData, input);
    this.rvwInput = input;
    this.paramsForReview = input.getDataReviewParamsSet();
    this.apicDataProvider = apicDataProvider;
    this.a2lFile = a2lFile;
    this.vcdmServiceHandler = serviceHandler;
  }

  /**
   * Runs shape review
   */
  public void run() {

    // Get risky BCs from risk analyser
    RiskAnalysis riskAnalysis = new RiskAnalysis(this.data);
    if (!riskAnalysis.hasRisk()) {
      this.result.setStatus(SHAPE_STATUS.NO_RISKY_BCS);
      return;
    }

    // Download CAT xml file (from VCDM - a2l artifacts)
    boolean isFileAvailable = false;
    VCDMPST vcdmPst = this.apicDataProvider.fetchVcdmPSTs(this.a2lFile);
    if (vcdmPst != null) {
      for (final VCDMPSTContents cont : vcdmPst.getPSTContents()) {
        if (cont.getVCDMClass().equalsIgnoreCase("DALB")) {

          String catFileName = cont.getVCDMClass() + "_" + cont.getFileName();
          String tempDir = System.getProperty("java.io.tmpdir");
          this.catXmlPath = tempDir + catFileName;
          try {
            isFileAvailable =
                this.vcdmServiceHandler.getvCDMPSTArtifacts(catFileName, cont.getFileId().toString(), null, tempDir);
          }
          catch (Exception exp) {
            CDMLogger.getInstance().warn(exp.getMessage(), Activator.PLUGIN_ID);
          }
          break;
        }
      }
    }
    if (isFileAvailable) {
      // Get the pver from CAT data xml file
      CatResultLoader catResLoader = new CatResultLoader(this.data);
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
      CDMLogger.getInstance().warn("CAT file not available to perform shape review", Activator.PLUGIN_ID);
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

    getFcParamMapFromA2l(riskyBCsNotInCAT, bcFcParamMapFromCAT, riskyParamsIdentified, riskyBCParamsMap);

    return riskyBCParamsMap;
  }

  /**
   * @param riskyBCsSet
   * @param bcFcParamMapFromCAT
   * @param riskyParamsIdentified
   * @param paramsForReview
   * @param riskyBCParamsMap
   * @return
   */
  private Map<String, Map<String, Set<String>>> getFcParamMapFromA2l(final Set<String> riskyBCsNotInCAT,
      final Map<String, Map<String, Set<String>>> bcFcParamMapFromCAT, final Set<String> riskyParamsIdentified,
      final Map<String, Set<String>> riskyBCParamsMap) {

    Map<String, Map<String, Set<String>>> bcFcParamMapFromA2l = new HashMap<String, Map<String, Set<String>>>();
    A2LEditorDataProvider a2lEditorDatProvider = new A2LEditorDataProvider(this.rvwInput.getA2lFileInfo());
    Map<String, A2LParameter> paramMap = a2lEditorDatProvider.getParamMap();
    Map<String, A2LParameter> riskyParamMap = new HashMap<>();

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
        for (A2LParameter param : riskyParamMap.values()) {
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

}
