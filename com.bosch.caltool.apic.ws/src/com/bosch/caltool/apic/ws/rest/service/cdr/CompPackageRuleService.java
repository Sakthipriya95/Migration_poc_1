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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.icdm.bo.cdr.FeatureAttributeAdapterNew;
import com.bosch.caltool.icdm.bo.cdr.ReviewRuleAdapter;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.cdr.ConfigBasedRuleInput;
import com.bosch.caltool.icdm.model.cdr.CreateCheckValRuleModel;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.ReviewRuleExt;
import com.bosch.caltool.icdm.model.cdr.ReviewRuleParamCol;
import com.bosch.caltool.icdm.model.comppkg.CompPackage;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.CDRRuleExt;
import com.bosch.ssd.icdm.model.FeatureValueModel;
import com.bosch.ssd.icdm.model.SSDMessage;

/**
 * Component Package Rule Services
 *
 * @author rgo7cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_CDR + "/" + WsCommonConstants.RWS_COMP_PKG)
public class CompPackageRuleService extends AbstractRuleService {


  /**
   * @param reviewRuleParamCol Comp Package object
   * @return Response sucess or failure
   * @throws IcdmException IcdmException
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response create(final ReviewRuleParamCol<CompPackage> reviewRuleParamCol) throws IcdmException {

    List<CDRRule> cdrRule = newRuleAdapter().convertReviewRule(reviewRuleParamCol.getReviewRuleList());

    SSDMessage ssdMessage = newRuleManager(reviewRuleParamCol).createMultipleRules(cdrRule);

    getLogger().info("Rule creation completed");

    return Response.ok(toSSDMessageWrapper(ssdMessage)).build();

  }


  /**
   * @param reviewRuleParamCol Comp Package object
   * @return Response with list of functions
   * @throws IcdmException IcdmException
   */
  @PUT
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response update(final ReviewRuleParamCol<CompPackage> reviewRuleParamCol) throws IcdmException {

    CDRRule cdrRule = newRuleAdapter().createCdrRule(reviewRuleParamCol.getReviewRule());

    SSDMessage ssdMessage = newRuleManager(reviewRuleParamCol).updateRule(cdrRule);

    return Response.ok(toSSDMessageWrapper(ssdMessage)).build();

  }


  /**
   * @param reviewRuleParamCol Comp Package object
   * @return Response with list of functions
   * @throws IcdmException IcdmException
   */
  @POST
  @Path("delete")
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response delete(final ReviewRuleParamCol<CompPackage> reviewRuleParamCol) throws IcdmException {
    List<CDRRule> cdrRules = newRuleAdapter().convertReviewRule(reviewRuleParamCol.getReviewRuleList());

    SSDMessage ssdMessage = newRuleManager(reviewRuleParamCol).deleteMultipleRules(cdrRules);
    getLogger().info("Rule deletion completed. SSD Return status = {}", ssdMessage);

    return Response.ok(toSSDMessageWrapper(ssdMessage)).build();
  }

  /**
   * @param reviewRuleParamCol Comp Package object
   * @return Response with list of functions
   * @throws IcdmException IcdmException
   */
  @POST
  @Path("getRulesForParam")
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response readRuleForParam(final ReviewRuleParamCol<CompPackage> reviewRuleParamCol) throws IcdmException {

    List<CDRRule> cdrRules =
        newRuleManager(reviewRuleParamCol).readRule(reviewRuleParamCol.getReviewRule().getParameterName());
    List<ReviewRule> reviewRuleList = newRuleAdapter().convertSSDRule(cdrRules);
    reviewRuleParamCol.setReviewRuleList(reviewRuleList);

    getLogger().info("Number of rules retrieved = {}", reviewRuleList.size());

    return Response.ok(reviewRuleParamCol).build();
  }

  /**
   * @param configInput config search rule input
   * @return Response with list of functions
   * @throws IcdmException IcdmException
   */
  @POST
  @Path("rulesearch")
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response searchRuleForDep(final ConfigBasedRuleInput<CompPackage> configInput) throws IcdmException {
    SortedSet<AttributeValueModel> attrValueModSet = configInput.getAttrValueModSet();
    FeatureAttributeAdapterNew adapter = new FeatureAttributeAdapterNew(getServiceData());
    Map<AttributeValueModel, FeatureValueModel> feaValModelList = adapter.createFeaValModel(attrValueModSet);
    Map<String, List<CDRRule>> cdrRuleMap = newRuleManager(configInput.getParamCol())
        .readRuleForDependency(configInput.getLabelNames(), new ArrayList<>(feaValModelList.values()));
    Map<String, List<ReviewRule>> retMap = newRuleAdapter().convertSSDRule(cdrRuleMap);

    getLogger().info("Number of parameters in response = {}", retMap.size());

    return Response.ok(retMap).build();

  }


  /**
   * @param reviewRuleParamCol Comp Package object
   * @return Response with list of rules
   * @throws IcdmException IcdmException
   */
  @POST
  @Path("ruleHistory")
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response getRuleHistory(final ReviewRuleParamCol<CompPackage> reviewRuleParamCol) throws IcdmException {
    ReviewRuleAdapter adapter = newRuleAdapter();
    List<CDRRuleExt> cdrRuleList = newRuleManager(reviewRuleParamCol).getRuleHistory(reviewRuleParamCol.getReviewRule());
    List<ReviewRuleExt> reviewRuleList = adapter.convertSSDRuleExt(cdrRuleList);
    getLogger().info("Number of rule history records = {}", reviewRuleList.size());
    return Response.ok(reviewRuleList).build();

  }

  /**
   * @param reviewRuleParamCol Comp Package object
   * @return Response with list of rules
   * @throws IcdmException IcdmException
   */
  @POST
  @Path("compliRuleHistory")
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response getCompliRuleHistory(final ReviewRuleParamCol<CompPackage> reviewRuleParamCol) throws IcdmException {
    ReviewRuleAdapter adapter = newRuleAdapter();
    List<CDRRuleExt> cdrRuleList = newRuleManager(reviewRuleParamCol).getRuleHistoryForNodeCompli(reviewRuleParamCol.getReviewRule());

    List<ReviewRuleExt> reviewRuleList = adapter.convertSSDRuleExt(cdrRuleList);
    getLogger().info("Number of rule history records = {}", reviewRuleList.size());
    return Response.ok(reviewRuleList).build();
  }

  /**
   * @param paramID param ID
   * @return Response with list of functions
   * @throws IcdmException IcdmException
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Path("checkValueRule")
  @CompressData
  public Response createCheclValueRule(final Long paramID) throws IcdmException {

    CreateCheckValRuleModel model = newRuleAdapter().createCheclValueRule(paramID, null);
    return Response.ok(model).build();
  }
}
