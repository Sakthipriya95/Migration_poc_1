/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lWpMapping;
import com.bosch.caltool.icdm.model.a2l.CompliParamOutput;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.a2l.WpRespLabelResponse;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.CDRWizardUIModel;
import com.bosch.caltool.icdm.model.cdr.CombinedReviewResultExcelExportData;
import com.bosch.caltool.icdm.model.cdr.CombinedRvwExportInputModel;
import com.bosch.caltool.icdm.model.cdr.ReviewResultEditorData;
import com.bosch.caltool.icdm.model.cdr.ReviewVariantWrapper;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireCreationModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.Questionnaire;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireVersion;
import com.bosch.caltool.icdm.model.cdr.review.ReviewInput;
import com.bosch.caltool.icdm.model.cdr.review.ReviewOutput;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.ssd.FeatureValueICDMModel;
import com.bosch.caltool.icdm.model.ssd.SSDFeatureICDMAttrModel;
import com.bosch.caltool.icdm.model.ssd.SSDReleaseIcdmModel;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.model.wp.WorkPkg;
import com.bosch.caltool.icdm.model.wp.WorkPkgInput;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpResponsibilityServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.FunctionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.ParameterServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.WorkPackageServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.compli.CompliParamServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeValueServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcA2lServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionAttributeServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.SdomPverServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CDRReviewResultServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.QnaireVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.QuestionnaireServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.ReviewResultCombinedExportDataServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.ReviewServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RuleSetServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.UserServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.ssd.SSDReleaseServiceClient;

/**
 * @author say8cob
 */
public class CDRHandler {

  private final CDRReviewResultServiceClient cdrReviewResultServiceClient = new CDRReviewResultServiceClient();
  private final PidcVariantServiceClient pidcVariantServiceClient = new PidcVariantServiceClient();
  private final UserServiceClient userServiceClient = new UserServiceClient();
  private final PidcA2lServiceClient pidcA2lServiceClient = new PidcA2lServiceClient();
  private final CompliParamServiceClient compliParamServiceClient = new CompliParamServiceClient();
  private final RuleSetServiceClient ruleSetServiceClient = new RuleSetServiceClient();
  private final ReviewServiceClient reviewFileServiceClient = new ReviewServiceClient();
  private final SSDReleaseServiceClient ssdReleaseServiceClient = new SSDReleaseServiceClient();
  private final AttributeValueServiceClient attributeValueServiceClient = new AttributeValueServiceClient();
  private final CDRReviewResultServiceClient reviewResultServiceClient = new CDRReviewResultServiceClient();
  private final FunctionServiceClient functionServiceClient = new FunctionServiceClient();
  private final PidcVersionServiceClient pidcVersionServiceClient = new PidcVersionServiceClient();
  private final PidcVersionAttributeServiceClient pidcVersionAttributeServiceClient =
      new PidcVersionAttributeServiceClient();
  private final WorkPackageServiceClient workPackageServiceClient = new WorkPackageServiceClient();
  private final QuestionnaireServiceClient questionnaireServiceClient = new QuestionnaireServiceClient();
  private final QnaireVersionServiceClient qnaireVersionServiceClient = new QnaireVersionServiceClient();
  private final ParameterServiceClient parameterServiceClient = new ParameterServiceClient();
  private final SdomPverServiceClient sdomPverServiceClient = new SdomPverServiceClient();
  private final A2lWpResponsibilityServiceClient a2lWpRespPalServiceClient = new A2lWpResponsibilityServiceClient();

  public CDRReviewResult getCdrReviewResult(final Long resultId) throws ApicWebServiceException {
    return this.cdrReviewResultServiceClient.getById(resultId);
  }

  /**
   * @param pidcA2lId
   * @return
   * @throws ApicWebServiceException
   */
  public Map<Long, PidcVariant> getA2lMappedVariants(final Long pidcA2lId) throws ApicWebServiceException {
    return this.pidcVariantServiceClient.getA2lMappedVariants(pidcA2lId);
  }

  /**
   * To get the Apic User with username as input
   *
   * @param userName
   * @return
   * @throws ApicWebServiceException
   */
  public Map<String, User> getApicUsersMap(final List<String> userNameList) throws ApicWebServiceException {
    return this.userServiceClient.getApicUserByUsername(userNameList);
  }


  /**
   * To get the map of param ids and param obj
   *
   * @param paramIdSet set of param ids
   * @return map of param id and param object
   * @throws ApicWebServiceException as exception
   */
  public Map<Long, Parameter> getParamMapUsingParamIdSet(final Set<Long> paramIdSet) throws ApicWebServiceException {
    return this.parameterServiceClient.getParamsUsingIds(paramIdSet);
  }

  /**
   * To get the Workpackage Resp with PidcA2lId and Variant id
   *
   * @param pidcA2lId as input
   * @param variantId as input
   * @return list of Wp Resp Label Response
   * @throws ApicWebServiceException as exception
   */
  public List<WpRespLabelResponse> getWorkPackageRespBasedOnPidcA2lIdAndVarId(final Long pidcA2lId,
      final Long variantId)
      throws ApicWebServiceException {
    return this.a2lWpRespPalServiceClient.getWpResp(pidcA2lId, variantId);
  }

  /**
   * @return true - if the PIDC Division attribute value is mapped to COMMON PARAM - DIV_WITH_OBD_OPTION
   */
  public boolean isDivIdAppForOBDOption(final Long pidcVersionId) {

    CommonDataBO dataBO = new CommonDataBO();
    Long pidcDivValId = 0L;
    boolean isOBDOptionConfigAvl = false;

    try {
      pidcDivValId = getQnaireConfigAttribute(pidcVersionId).getValueId();
      String divIdListStr = dataBO.getParameterValue(CommonParamKey.DIVISIONS_WITH_OBD_OPTION);
      if (CommonUtils.isNotNull(divIdListStr)) {
        // comma seperated users
        String[] divIdsArray = divIdListStr.split(",");
        isOBDOptionConfigAvl = Arrays.asList(divIdsArray).contains(pidcDivValId.toString());
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return isOBDOptionConfigAvl;
  }

  /**
   * @param cdrHandler
   * @param pidcTreeNode
   * @throws ApicWebServiceException
   * @throws NumberFormatException
   */
  public boolean getPidcDivisionAttributes(final Long pidcVersionId) throws ApicWebServiceException {
    return isReviewAttributeSet(getQnaireConfigAttribute(pidcVersionId));
  }

  /**
   * @param pidcVersAttr PidcVersionAttribute
   * @return boolean value , true if pidcVersAttr is not null
   */
  private boolean isReviewAttributeSet(final PidcVersionAttribute pidcVersAttr) {
    return pidcVersAttr != null;
  }

  /**
   * @return
   * @throws ApicWebServiceException
   */
  public User getCurrentApicUser() throws ApicWebServiceException {
    return new CurrentUserBO().getUser();
  }

  /**
   * To check whether the PIDC version is having any variant
   *
   * @param pidcVersId
   * @param includeDeleted
   * @return
   * @throws ApicWebServiceException
   */
  public boolean hasVariant(final Long pidcVersId, final boolean includeDeleted) throws ApicWebServiceException {

    return this.pidcVariantServiceClient.hasVariant(pidcVersId, includeDeleted);
  }


  /**
   * To get A2lMapping records
   *
   * @param pidcA2lId
   * @return
   * @throws ApicWebServiceException
   * @deprecated not used
   */
  @Deprecated
  public A2lWpMapping getA2lMapping(final Long pidcA2lId) throws ApicWebServiceException {
    return this.pidcA2lServiceClient.getA2lWorkpackageMapping(pidcA2lId);
  }

  /**
   * @return
   * @throws ApicWebServiceException
   */
  public CompliParamOutput getCompliParamValues() throws ApicWebServiceException {
    return this.compliParamServiceClient.getCompliParams();
  }

  /**
   * @return
   * @throws ApicWebServiceException
   */
  public SortedSet<RuleSet> getAllRuleSet() throws ApicWebServiceException {
    SortedSet<RuleSet> allRuleSets = this.ruleSetServiceClient.getAllRuleSets();


    Iterator<RuleSet> ruleSetIterator = allRuleSets.iterator();
    while (ruleSetIterator.hasNext()) {
      RuleSet ruleSet = ruleSetIterator.next();
      if (ruleSet.isDeleted()) {
        ruleSetIterator.remove();
      }
    }

    return allRuleSets;
  }


  /**
   * Method to call the perform review service
   *
   * @param reviewData contains the data for review
   * @return
   * @throws ApicWebServiceException
   */
  public ReviewOutput performReview(final ReviewInput reviewData) throws ApicWebServiceException {
    return this.reviewFileServiceClient.performReview(reviewData);
  }

  /**
   * Method to call the service to get the SSD Release
   *
   * @param swVersionId
   * @return
   * @throws ApicWebServiceException
   */
  public List<SSDReleaseIcdmModel> getSSDReleases(final Long swVersionId) throws ApicWebServiceException {
    return this.ssdReleaseServiceClient.getSSDReleaseList(swVersionId);
  }

  /**
   * @param dependencyList
   * @return
   * @throws ApicWebServiceException
   */
  public List<SSDFeatureICDMAttrModel> getSSDFeatureICDMAttrModelList(final List<FeatureValueICDMModel> dependencyList)
      throws ApicWebServiceException {
    return this.ssdReleaseServiceClient.getSSDFeatureICDMAttrModelList(dependencyList);
  }

  /**
   * Method to get the attribute value
   *
   * @param pidcVersId
   * @param attrId
   * @return
   * @throws ApicWebServiceException
   */
  public AttributeValue getValuesByPidcVersAttribute(final Long pidcVersId, final Long attrId)
      throws ApicWebServiceException {
    return this.attributeValueServiceClient.getValueByAttribute(pidcVersId, attrId);
  }

  /**
   * @param objId
   * @return
   * @throws ApicWebServiceException
   */
  public AttributeValue getAttributeValue(final Long objId) throws ApicWebServiceException {
    return this.attributeValueServiceClient.getById(objId);
  }

  /**
   * Method to delete the review result asyn
   *
   * @param reviewResultId
   * @throws ApicWebServiceException
   */
  public void deleteReviewResult(final CDRReviewResult cdrReviewResult) throws ApicWebServiceException {
    this.reviewResultServiceClient.deleteReviewResult(cdrReviewResult, true);
  }

  /**
   * @param functionNames
   * @return
   * @throws ApicWebServiceException
   */
  public Map<String, Function> getFunctionByName(final SortedSet<String> functionNames) throws ApicWebServiceException {
    return this.functionServiceClient.getFunctionsByName(new ArrayList<String>(functionNames));
  }


  /**
   * Method to invoke save cancelled service
   *
   * @param reviewInput object as input
   * @return cdrreview result
   * @throws ApicWebServiceException
   */
  public ReviewVariantWrapper saveCancelledResult(final ReviewInput reviewInput) throws ApicWebServiceException {
    return this.reviewFileServiceClient.saveCancelledResult(reviewInput);
  }


  /**
   * Method to fetch data for Delte Review
   *
   * @param resultId
   * @return
   * @throws ApicWebServiceException
   */
  public CDRWizardUIModel getReviewResultForDeltaReview(final long resultId) throws ApicWebServiceException {
    return this.cdrReviewResultServiceClient.getReviewResultForDeltaReview(resultId,
        CommonUtils.getICDMTmpFileDirectoryPath());
  }

  /**
   * @param pidcVersionId
   * @param sdomPverName
   * @return
   * @throws ApicWebServiceException
   */
  public Map<Long, PidcA2l> getA2LFileBySdom(final Long pidcVersionId, final String sdomPverName)
      throws ApicWebServiceException {
    return this.pidcA2lServiceClient.getA2LFileBySdom(pidcVersionId, sdomPverName);
  }


  /**
   * @param pidcVersionId
   * @return
   * @throws ApicWebServiceException
   */
  public PidcVersion getPidcVersion(final long pidcVersionId) throws ApicWebServiceException {
    return this.pidcVersionServiceClient.getById(pidcVersionId);
  }

  /**
   * @param pidcVersionId
   * @return
   * @throws ApicWebServiceException
   */
  public PidcVersionAttribute getQnaireConfigAttribute(final long pidcVersionId) throws ApicWebServiceException {
    return this.pidcVersionAttributeServiceClient.getQnaireConfigAttribute(pidcVersionId);
  }

  /**
   * @param pidcVersionId
   * @param pidcVariantId
   * @return
   * @throws ApicWebServiceException
   */
  public String getSDOMPverName(final Long pidcVersionId, final Long pidcVariantId) throws ApicWebServiceException {
    return this.sdomPverServiceClient.getSDOMPverName(pidcVersionId, pidcVariantId);
  }

  /**
   * @param workPkgInput
   * @return
   * @throws ApicWebServiceException
   */
  public Map<Long, String> getWorkPackageResponseMap(final WorkPkgInput workPkgInput) throws ApicWebServiceException {
    return this.workPackageServiceClient.getWorkRespMap(workPkgInput);
  }

  /**
   * @param qnaireCmdData
   * @return
   * @throws ApicWebServiceException
   */
  public Questionnaire createQnaireAndQnaireVersionCommand(final QnaireCreationModel qnaireCmdData)
      throws ApicWebServiceException {
    return this.questionnaireServiceClient.createQnaireAndVersion(qnaireCmdData).getDataCreated();
  }

  /**
   * @param questionnaireVersion
   * @return
   * @throws ApicWebServiceException
   */
  public Long createQnaireVersCommand(final QuestionnaireVersion questionnaireVersion) throws ApicWebServiceException {
    return this.qnaireVersionServiceClient.create(questionnaireVersion).getId();
  }

  /**
   * @param qnaireId
   * @return
   * @throws ApicWebServiceException
   */
  public WorkPkg getWorkPkgbyQnaireID(final Long qnaireId) throws ApicWebServiceException {
    return this.workPackageServiceClient.getWorkPkgbyQnaireID(qnaireId);
  }


  /**
   * @param qnaireIdSet as input
   * @return as Set of workpkg name
   * @throws ApicWebServiceException as exception
   */
  public Map<Long, String> getWorkPkgNameUsingQnaireIDSet(final Set<Long> qnaireIdSet) throws ApicWebServiceException {
    return this.workPackageServiceClient.getWorkPkgNameUsingQnaireIDSet(qnaireIdSet);
  }

  /**
   * @param labelSet
   * @param a2lLabelset
   * @return
   * @throws ApicWebServiceException
   */
  public Set<String> getMissingParams(final Set<String> labelSet, final Set<String> a2lLabelset)
      throws ApicWebServiceException {
    return this.parameterServiceClient.getMissingLabels(labelSet, a2lLabelset);
  }

  /**
   * Method to return the count of param for the fucntion set
   *
   * @param funcNameSet as input
   * @return count of params
   * @throws ApicWebServiceException exception
   */
  public Long getParamCountForFuncNameSet(final Set<String> funcNameSet) throws ApicWebServiceException {
    return this.parameterServiceClient.getParamCountbyFunctionNameSet(funcNameSet);
  }

  /**
   * Fetch review result data from server
   *
   * @param rvwResultId , the review result id
   * @return ReviewResultClientBO
   */
  public ReviewResultClientBO getRevResultClientBo(final Long rvwResultId) {
    try {
      ReviewResultEditorData response = new CDRReviewResultServiceClient().getRvwResultEditorData(rvwResultId, null);
      return new ReviewResultClientBO(response, null);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return null;
  }


  /**
   * Method do a service call to get the models for combined rvw report
   *
   * @param exportInputModel as input
   * @return CombinedReviewResultExcelExportData as output
   */
  public CombinedReviewResultExcelExportData getCombinedExportData(final CombinedRvwExportInputModel exportInputModel) {
    try {
      return new ReviewResultCombinedExportDataServiceClient().getCombinedReviewAndQnaireExcelExport(exportInputModel);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return null;
  }
}
