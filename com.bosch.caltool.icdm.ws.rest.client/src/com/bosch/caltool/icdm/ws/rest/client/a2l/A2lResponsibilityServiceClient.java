package com.bosch.caltool.icdm.ws.rest.client.a2l;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.datamodel.core.cns.ChangeDataCreator;
import com.bosch.caltool.icdm.model.a2l.A2lRespMergeData;
import com.bosch.caltool.icdm.model.a2l.A2lRespMaintenanceData;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibilityMergeModel;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibilityModel;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.ChangeHandler;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapperChangeData;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.ModelParser;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for A2lResponsibility
 *
 * @author pdh2cob
 */
public class A2lResponsibilityServiceClient extends AbstractRestServiceClient {

  private static final IMapperChangeData A2L_RESP_MERGE = data -> {

    Collection<ChangeData<IModel>> changeDataList = new HashSet<>();
    ChangeDataCreator<IModel> changeDataCreator = new ChangeDataCreator<>();
    A2lResponsibilityMergeModel mergeModel = (A2lResponsibilityMergeModel) data;

    mergeModel.getCdfxDlvryWpRespOld().forEach(cdfxDlvryWpResp -> changeDataList.add(changeDataCreator
        .createDataForUpdate(0L, cdfxDlvryWpResp, mergeModel.getCdfxDelvryParamUpdate().get(cdfxDlvryWpResp.getId()))));
    mergeModel.getCdfxDlvryWpRespDelete()
        .forEach(cdfxDlvryWpResp -> changeDataList.add(changeDataCreator.createDataForDelete(0L, cdfxDlvryWpResp)));
    mergeModel.getCdfxDelvryParamOld().forEach(cdfxDlvryParam -> changeDataList.add(changeDataCreator
        .createDataForUpdate(0L, cdfxDlvryParam, mergeModel.getCdfxDelvryParamUpdate().get(cdfxDlvryParam.getId()))));
    mergeModel.getRvwWpRespOld().forEach(rvwWpResp -> changeDataList.add(
        changeDataCreator.createDataForUpdate(0L, rvwWpResp, mergeModel.getRvwWpRespUpdate().get(rvwWpResp.getId()))));
    mergeModel.getRvwWpRespDelete()
        .forEach(rvwWpResp -> changeDataList.add(changeDataCreator.createDataForDelete(0L, rvwWpResp)));
    mergeModel.getCdrResultParameterOld()
        .forEach(cdrResultParam -> changeDataList.add(changeDataCreator.createDataForUpdate(0L, cdrResultParam,
            mergeModel.getCdrResultParameterUpdate().get(cdrResultParam.getId()))));
    mergeModel.getRvwQnaireResponseOld().forEach(rvwQnaireResp -> changeDataList.add(changeDataCreator
        .createDataForUpdate(0L, rvwQnaireResp, mergeModel.getRvwQnaireResponseUpdate().get(rvwQnaireResp.getId()))));
    mergeModel.getRvwQnaireResponseDelete()
        .forEach(rvwQnaireResp -> changeDataList.add(changeDataCreator.createDataForDelete(0L, rvwQnaireResp)));
    mergeModel.getRvwQnaireRespVariantDelete()
        .forEach(rvwQnaireRespVar -> changeDataList.add(changeDataCreator.createDataForDelete(0L, rvwQnaireRespVar)));
    mergeModel.getRvwQnaireAnswerDelete()
        .forEach(rvwQnaireAns -> changeDataList.add(changeDataCreator.createDataForDelete(0L, rvwQnaireAns)));
    mergeModel.getRvwQnaireAnswerOplDelete()
        .forEach(rvwQnaireAnsOpl -> changeDataList.add(changeDataCreator.createDataForDelete(0L, rvwQnaireAnsOpl)));
    mergeModel.getRvwQnaireRespVersionDelete().forEach(
        rvwQnaireRespVersion -> changeDataList.add(changeDataCreator.createDataForDelete(0L, rvwQnaireRespVersion)));
    mergeModel.getA2lWpResponsibilityOld().forEach(a2lWpResp -> changeDataList.add(changeDataCreator
        .createDataForUpdate(0L, a2lWpResp, mergeModel.getA2lWpResponsibilityUpdate().get(a2lWpResp.getId()))));
    mergeModel.getA2lWpParamMappingOld()
        .forEach(a2lRespParamMapping -> changeDataList.add(changeDataCreator.createDataForUpdate(0L,
            a2lRespParamMapping, mergeModel.getA2lWpParamMappingUpdate().get(a2lRespParamMapping.getId()))));
    mergeModel.getA2lResponsibilityDeleteSet()
        .forEach(a2lResp -> changeDataList.add(changeDataCreator.createDataForDelete(0L, a2lResp)));
    return changeDataList;
  };


  private static final IMapperChangeData A2L_RESP_CREATE_UPDATE_MAPPER = data -> {
    Collection<ChangeData<IModel>> changeDataList = new HashSet<>();
    ChangeDataCreator<IModel> changeDataCreator = new ChangeDataCreator<>();
    A2lRespMaintenanceData a2lRespMaintenanceData = (A2lRespMaintenanceData) data;

    if (a2lRespMaintenanceData.getA2lRespUpdated() != null) {
      changeDataList.add(changeDataCreator.createDataForUpdate(0L, a2lRespMaintenanceData.getA2lRespToUpdate(),
          a2lRespMaintenanceData.getA2lRespUpdated()));
    }
    else {
      changeDataList.add(changeDataCreator.createDataForCreate(0L, a2lRespMaintenanceData.getA2lRespToCreate()));
    }

    a2lRespMaintenanceData.getBoschUsrsCreationList()
        .forEach(boschUser -> changeDataList.add(changeDataCreator.createDataForCreate(0L, boschUser)));

    a2lRespMaintenanceData.getBoschUsrsDeletionList()
        .forEach(boschUser -> changeDataList.add(changeDataCreator.createDataForDelete(0L, boschUser)));

    return changeDataList;
  };

  /**
   * Constructor
   */
  public A2lResponsibilityServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_A2L_RESPONSIBILITY);
  }


  /**
   * Get A2lResponsibility using its id
   *
   * @param objId object's id
   * @return A2lResponsibility object
   * @throws ApicWebServiceException exception while invoking service
   */
  public A2lResponsibility get(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, A2lResponsibility.class);
  }

  /**
   * Create a A2lResponsibility record
   *
   * @param obj object to create
   * @return created A2lResponsibility object
   * @throws ApicWebServiceException exception while invoking service
   */
  public A2lResponsibility create(final A2lResponsibility obj) throws ApicWebServiceException {
    return create(getWsBase(), obj);
  }

  /**
   * Update a A2lResponsibility record
   *
   * @param obj object to update
   * @return updated A2lResponsibility object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Map<Long, A2lResponsibility> update(final List<A2lResponsibility> obj) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase();
    GenericType<Map<Long, A2lResponsibility>> type = new GenericType<Map<Long, A2lResponsibility>>() {};
    return update(wsTarget, obj, type);
  }


  /**
   * @param pidcId - id
   * @return Map -> key - A2lResponsibility id, value A2lResponsibility
   * @throws ApicWebServiceException exception while invoking service
   */
  public A2lResponsibilityModel getByPidc(final Long pidcId) throws ApicWebServiceException {
    LOGGER.info("Get A2lResponsibilityModel for pidc id {}", pidcId);

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_A2L_RESPONSIBILITY_BY_PIDC)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_ID, pidcId);
    GenericType<A2lResponsibilityModel> type = new GenericType<A2lResponsibilityModel>() {};

    A2lResponsibilityModel ret = get(wsTarget, type);

    LOGGER.info("A2lResponsibilityModel loaded. Count : {}, users = {}", ret.getA2lResponsibilityMap().size(),
        ret.getUserMap().size());

    return ret;
  }

  /**
   * Merge A2l responsibility
   *
   * @param mergeModel object to create
   * @return Rest response, {@link A2lResponsibilityMergeModel}obj
   * @throws ApicWebServiceException exception while invoking service
   */
  public A2lResponsibilityMergeModel mergeResponsibility(final A2lResponsibilityMergeModel mergeModel)
      throws ApicWebServiceException {

    LOGGER.info("Merge A2L responsibility as resp id - ", mergeModel.getA2lRespMergeToId());

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_MERGE_A2L_RESPONSIBILITY);
    A2lResponsibilityMergeModel mergeModelResponse = post(wsTarget, mergeModel, A2lResponsibilityMergeModel.class);

    Collection<ChangeData<IModel>> newDataModelSet = ModelParser.getChangeData(mergeModelResponse, A2L_RESP_MERGE);

    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(newDataModelSet));

    displayMessage("Merging A2L responsibility is completed");

    LOGGER.info("Merging A2L responsibility is completed");
    return mergeModelResponse;
  }


  /**
   * Merge A2l responsibility
   *
   * @param a2lRespMaintenanceData object to create/update
   * @return Rest response, {@link A2lRespMaintenanceData}obj
   * @throws ApicWebServiceException exception while invoking service
   */
  public A2lRespMaintenanceData maintainA2lResp(final A2lRespMaintenanceData a2lRespMaintenanceData)
      throws ApicWebServiceException {

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_A2L_RESPONSIBILITY_MAINTENANCE);
    A2lRespMaintenanceData a2lRespMaintenanceDataResponse = post(wsTarget, a2lRespMaintenanceData, A2lRespMaintenanceData.class);

    Collection<ChangeData<IModel>> newDataModelSet =
        ModelParser.getChangeData(a2lRespMaintenanceDataResponse, A2L_RESP_CREATE_UPDATE_MAPPER);

    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(newDataModelSet));

    LOGGER.info("A2L responsibility is modified succesfully.");
    return a2lRespMaintenanceDataResponse;
  }

  /**
   * Selected a2l response statistic data is stored in server as a JSON file and time stamp is returned as output. This
   * timestamp will be used by hotline team to load the data by parsing the json from server
   *
   * @param a2lRespMergeData selected a2l resp details set
   * @return Execution ID
   * @throws ApicWebServiceException exception in web service
   */
  public String parseSelectedA2lRespStatToJson(final A2lRespMergeData a2lRespMergeData) throws ApicWebServiceException {

    LOGGER.info("Create JSON for selected Qnaire resp details set");

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_PARSE_A2L_RESP_DETAILS_TO_JSON);
    String executionId = post(wsTarget, a2lRespMergeData, String.class);

    LOGGER.info("Selected qnaire resp details is parsed to JSON succesfully. Excecution ID is - ", executionId);
    return executionId;
  }

  /**
   * @param executionId id to fetch previously saved input data from the server
   * @return {@link A2lRespMergeData}
   * @throws ApicWebServiceException Exception in ws call
   */
  public A2lRespMergeData fetchA2lRespMergeInputData(final String executionId) throws ApicWebServiceException {

    LOGGER.info("Parse Json from server and import the data");

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_A2L_RESP_MERGE_INPUT_FETCH)
        .queryParam(WsCommonConstants.RWS_QP_EXECUTION_ID, executionId);
    GenericType<A2lRespMergeData> type = new GenericType<A2lRespMergeData>() {};

    return get(wsTarget, type);
  }
}
