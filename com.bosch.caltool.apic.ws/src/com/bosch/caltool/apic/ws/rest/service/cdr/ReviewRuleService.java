/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.cdr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.icdm.bo.cdr.FeatureAttributeAdapterNew;
import com.bosch.caltool.icdm.bo.cdr.IRuleManager;
import com.bosch.caltool.icdm.bo.cdr.ReviewRuleAdapter;
import com.bosch.caltool.icdm.bo.cdr.RuleRemarkHandler;
import com.bosch.caltool.icdm.bo.cdr.RuleSetHandler;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.cdr.ConfigBasedRuleInput;
import com.bosch.caltool.icdm.model.cdr.CreateCheckValRuleModel;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.ReviewRuleExt;
import com.bosch.caltool.icdm.model.cdr.ReviewRuleParamCol;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.CDRRuleExt;
import com.bosch.ssd.icdm.model.FeatureValueModel;
import com.bosch.ssd.icdm.model.SSDMessage;


/**
 * Services for maintenance and retrieval of review rules
 *
 * @author rgo7cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_CDR + "/" + WsCommonConstants.RWS_REVIEW_RULE)
public class ReviewRuleService extends AbstractRuleService {


  /**
   * @param reviewRuleParamCol reviewRuleParamCol
   * @return Response sucess or failure
   * @throws IcdmException IcdmException
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response create(final ReviewRuleParamCol<Function> reviewRuleParamCol) throws IcdmException {

    Map<CDRRule, ReviewRule> inputCdrRuleMap =
        newRuleAdapter().convertReviewRules(reviewRuleParamCol.getReviewRuleList());

    IRuleManager ruleManager = newRuleManager(reviewRuleParamCol);

    List<CDRRule> cdrRuleList = new ArrayList<>(inputCdrRuleMap.keySet());
    validateComplexRules(cdrRuleList);
    SSDMessage ssdMessage = ruleManager.createMultipleRules(cdrRuleList);
    getLogger().info("Rule creation finished. SSD Message : {}", ssdMessage);

    // Create Unicode Remarks and Links For the Rule created
    Map<CDRRule, ReviewRule> unicodeRmrksAndLinksForCreate =
        collectUnicodeRmrksAndLinksForCreate(inputCdrRuleMap, ruleManager.readRule(getParamNames(cdrRuleList)));
    new RuleRemarkHandler(getServiceData()).createUpdDelRmrks(unicodeRmrksAndLinksForCreate);
    // Create Links
    new RuleSetHandler(getServiceData()).createUpdDelLinks(unicodeRmrksAndLinksForCreate);

    getLogger().info("Unicode and Links creation finished.");

    return Response.ok(toSSDMessageWrapper(ssdMessage)).build();
  }

  /**
   * @param reviewRuleParamCol param collection with rules, input
   * @return Response with list of functions
   * @throws IcdmException IcdmException
   */
  @PUT
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response update(final ReviewRuleParamCol<Function> reviewRuleParamCol) throws IcdmException {
    ReviewRule inputReviewRule = reviewRuleParamCol.getReviewRule();
    CDRRule inputCdrRule = newRuleAdapter().createCdrRule(inputReviewRule);
    validateComplexRule(inputCdrRule);
    IRuleManager ruleManager = newRuleManager(reviewRuleParamCol);

    SSDMessage ssdMessage = SSDMessage.SUCCESS;

    boolean ruleUpdateRequired = isRuleUpdateRequired(inputCdrRule, ruleManager);
    //Update the Input Review Rule
    new RuleSetHandler(getServiceData()).getUpdInputRvwRule(inputReviewRule, ruleUpdateRequired);

    if (ruleUpdateRequired) {
      ssdMessage = ruleManager.updateRule(inputCdrRule);
    }

    getLogger().info("Rule Updation finished. SSD Message : {}", ssdMessage);

    // Update Unicode Remarks and Links For the Rule updated
    Map<CDRRule, ReviewRule> unicodeRmrksAndLinksForUpd =
        collectUnicodeRmrksAndLinksForUpd(inputCdrRule, inputReviewRule, ruleManager, ruleUpdateRequired);
    // Update or delete Unicode Remarks
    new RuleRemarkHandler(getServiceData()).createUpdDelRmrks(unicodeRmrksAndLinksForUpd);
    // Update or delete Links
    new RuleSetHandler(getServiceData()).createUpdDelLinks(unicodeRmrksAndLinksForUpd);
    getLogger().info("Unicode and Links Updation finished.");

    return Response.ok(toSSDMessageWrapper(ssdMessage)).build();
  }


  /**
   * @param reviewRuleParamCol param collection with rules, input
   * @return Response with list of functions
   * @throws IcdmException IcdmException
   */
  @POST
  @Path(WsCommonConstants.RWS_DELETE)
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response delete(final ReviewRuleParamCol<Function> reviewRuleParamCol) throws IcdmException {
    List<CDRRule> cdrRules = newRuleAdapter().convertReviewRule(reviewRuleParamCol.getReviewRuleList());

    SSDMessage ssdMessage = newRuleManager(reviewRuleParamCol).deleteMultipleRules(cdrRules);
    getLogger().info("Rule deletion finished. SSD Message : {}", ssdMessage);

    return Response.ok(toSSDMessageWrapper(ssdMessage)).build();
  }

  /**
   * @param reviewRuleParamCol param collection with rules, input
   * @return Response with list of functions
   * @throws IcdmException IcdmException
   */
  @POST
  @Path(WsCommonConstants.RWS_RULES_FOR_PARAM)
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response readRuleForParam(final ReviewRuleParamCol<Function> reviewRuleParamCol) throws IcdmException {

    List<CDRRule> cdrRules =
        newRuleManager(reviewRuleParamCol).readRule(reviewRuleParamCol.getReviewRule().getParameterName());
    List<ReviewRule> reviewRuleList = newRuleAdapter().convertSSDRule(cdrRules);
    reviewRuleParamCol.setReviewRuleList(reviewRuleList);

    getLogger().info("Number of rules retrieved = {}", reviewRuleList.size());

    return Response.ok(reviewRuleParamCol).build();
  }

  /**
   * @param configInput search input
   * @return Response with list of functions
   * @throws IcdmException IcdmException
   */
  @POST
  @Path(WsCommonConstants.RWS_RULES_FOR_PARAM_WITH_DEP)
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response readRuleForParamWithDep(final ConfigBasedRuleInput<Function> configInput) throws IcdmException {

    FeatureAttributeAdapterNew featureAttributeAdapter = new FeatureAttributeAdapterNew(getServiceData());
    List<FeatureValueModel> feaValModelList = new ArrayList<>();
    feaValModelList.addAll(featureAttributeAdapter.createFeaValModel(configInput.getAttrValueModSet()).values());
    Map<String, List<CDRRule>> cdrRules =
        newRuleManager(configInput.getParamCol()).readRuleForDependency(configInput.getLabelNames(), feaValModelList);
    Map<String, List<ReviewRule>> retMap = newRuleAdapter().convertSSDRule(cdrRules);

    getLogger().info("Number of parameters in response = {}", retMap.size());

    return Response.ok(retMap).build();
  }

  /**
   * @param configInput search input
   * @return Response with list of functions
   * @throws IcdmException IcdmException
   */
  @POST
  @Path(WsCommonConstants.RWS_SEARCH)
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response searchRuleForDep(final ConfigBasedRuleInput<Function> configInput) throws IcdmException {
    SortedSet<AttributeValueModel> attrValueModSet = configInput.getAttrValueModSet();
    FeatureAttributeAdapterNew adapter = new FeatureAttributeAdapterNew(getServiceData());
    List<FeatureValueModel> feaValModelList = adapter.getFeaValModelList(attrValueModSet);
    Map<String, List<CDRRule>> cdrRuleMap =
        newRuleManager(configInput.getParamCol()).readRuleForDependency(configInput.getLabelNames(), feaValModelList);
    Map<String, List<ReviewRule>> retMap = newRuleAdapter().convertSSDRule(cdrRuleMap);

    getLogger().info("Number of parameters in response = {}", retMap.size());

    return Response.ok(retMap).build();

  }


  /**
   * @param reviewRuleParamCol param collection with rules, input
   * @return Response with list of rules
   * @throws IcdmException IcdmException
   */
  @POST
  @Path(WsCommonConstants.RWS_RULE_HISTORY)
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response getRuleHistory(final ReviewRuleParamCol<Function> reviewRuleParamCol) throws IcdmException {
    ReviewRuleAdapter adapter = newRuleAdapter();
    List<CDRRuleExt> cdrRuleList =
        newRuleManager(reviewRuleParamCol).getRuleHistory(reviewRuleParamCol.getReviewRule());
    List<ReviewRuleExt> reviewRuleList = adapter.convertSSDRuleExt(cdrRuleList);

    getLogger().info("Number of rule history records = {}", reviewRuleList.size());

    return Response.ok(reviewRuleList).build();
  }

  /**
   * @param reviewRuleParamCol param collection with rules, input
   * @return Response with list of rules
   * @throws IcdmException IcdmException
   */
  @POST
  @Path(WsCommonConstants.RWS_COMPLI_RULE_HISTORY)
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response getCompliRuleHistory(final ReviewRuleParamCol<Function> reviewRuleParamCol) throws IcdmException {
    ReviewRuleAdapter adapter = newRuleAdapter();
    List<CDRRuleExt> cdrRuleList =
        newRuleManager(reviewRuleParamCol).getRuleHistoryForNodeCompli(reviewRuleParamCol.getReviewRule());
    List<ReviewRuleExt> reviewRuleList = adapter.convertSSDRuleExt(cdrRuleList);

    getLogger().info("Number of rule history records = {}", reviewRuleList.size());

    return Response.ok(reviewRuleList).build();

  }

  /**
   * @param paramID paramID
   * @param ruleSetId ruleSetId
   * @return Response with list of functions
   * @throws IcdmException IcdmException
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_CHECK_VALUE_RULE)
  @CompressData
  public Response createCheclValueRule(final Long paramID,
      @QueryParam(value = WsCommonConstants.RWS_QP_RULESET_ID) final Long ruleSetId)
      throws IcdmException {

    CreateCheckValRuleModel model = newRuleAdapter().createCheclValueRule(paramID, ruleSetId);
    return Response.ok(model).build();
  }

}
