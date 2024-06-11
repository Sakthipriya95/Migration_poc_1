package com.bosch.caltool.icdm.ws.rest.client.uc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.datamodel.core.cns.CHANGE_OPERATION;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.datamodel.core.cns.ChangeDataCreator;
import com.bosch.caltool.icdm.model.general.DataCreationModel;
import com.bosch.caltool.icdm.model.uc.UseCase;
import com.bosch.caltool.icdm.model.uc.UsecaseCreationData;
import com.bosch.caltool.icdm.model.uc.UsecaseEditorModel;
import com.bosch.caltool.icdm.model.uc.UsecaseType;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.ChangeHandler;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapper;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for Usecase
 *
 * @author MKL2COB
 */
public class UseCaseServiceClient extends AbstractRestServiceClient {


  private static final IMapper USECASE_CREATION_MAPPER = obj -> new HashSet<>(Arrays
      .asList(((DataCreationModel<UseCase>) obj).getDataCreated(), ((DataCreationModel<UseCase>) obj).getNodeAccess()));

  /**
   * Constructor
   */
  public UseCaseServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_UC, WsCommonConstants.RWS_USECASE);
  }

  /**
   * Get all Usecase records in system
   *
   * @return Map. Key - id, Value - UseCase object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Map<Long, UseCase> getAll() throws ApicWebServiceException {
    LOGGER.debug("Get all Usecase records ");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL);
    GenericType<Map<Long, UseCase>> type = new GenericType<Map<Long, UseCase>>() {};
    Map<Long, UseCase> retMap = get(wsTarget, type);
    LOGGER.debug("Usecase records loaded count = {}", retMap.size());
    return retMap;
  }

  /**
   * Get Usecase using its id
   *
   * @param objId object's id
   * @return UseCase object
   * @throws ApicWebServiceException exception while invoking service
   */
  public UseCase getById(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, UseCase.class);
  }

  /**
   * Create a Usecase record
   *
   * @param ucCreationData object to create
   * @return created UseCase object
   * @throws ApicWebServiceException exception while invoking service
   */
  public DataCreationModel<UseCase> create(final UsecaseCreationData ucCreationData) throws ApicWebServiceException {
    return create(getWsBase(), ucCreationData, new GenericType<DataCreationModel<UseCase>>() {},
        UseCaseServiceClient.USECASE_CREATION_MAPPER);
  }

  /**
   * Update a Usecase record
   *
   * @param obj object to update
   * @return updated UseCase object
   * @throws ApicWebServiceException exception while invoking service
   */
  public UseCase update(final UseCase obj) throws ApicWebServiceException {
    return update(getWsBase(), obj);
  }

  /**
   * Update a Usecase record
   *
   * @param useCase Long
   * @param isUpToDate Boolean
   * @return updated UseCase object
   * @throws ApicWebServiceException exception while invoking service
   */
  public UseCase changeUpToDateStatus(final UseCase useCase, final Boolean isUpToDate) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_UPDATE_CONFRM_DATE)
        .queryParam(WsCommonConstants.RWS_QP_OBJ_ID, useCase.getId());

    UseCase updatedUseCase = put(wsTarget, isUpToDate, UseCase.class);
    ChangeData<UseCase> chgData = (new ChangeDataCreator<UseCase>()).createDataForUpdate(0L, useCase, updatedUseCase);
    (new ChangeHandler()).triggerLocalChangeEvent(Arrays.asList(chgData));

    displayMessage(CHANGE_OPERATION.UPDATE, updatedUseCase);
    return updatedUseCase;
  }

  /**
   * @param useCaseId Long
   * @return UsecaseTreeGroupModel
   * @throws ApicWebServiceException exception from servers
   */
  public UsecaseEditorModel getUseCaseEditorData(final Long useCaseId) throws ApicWebServiceException {
    LOGGER.debug("Get Usecase editor Data");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_UC_EDITOR_DATA)
        .queryParam(WsCommonConstants.RWS_QP_OBJ_ID, useCaseId);
    return get(wsTarget, UsecaseEditorModel.class);
  }


  /**
   * @param usecaseIds Set<Long>
   * @return Map<Long, UsecaseEditorModel>
   * @throws ApicWebServiceException
   */
  public Map<Long, UsecaseEditorModel> getUsecaseEditorModels(final Set<Long> usecaseIds)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_UC_EXPORT_DATA);
    GenericType<Map<Long, UsecaseEditorModel>> type = new GenericType<Map<Long, UsecaseEditorModel>>() {};
    for (Long usecaseId : usecaseIds) {
      wsTarget = wsTarget.queryParam(WsCommonConstants.RWS_QP_OBJ_ID, usecaseId);
    }
    Map<Long, UsecaseEditorModel> ucModelMap = get(wsTarget, type);
    return ucModelMap;
  }

  /**
   * @param usecaseIds
   * @return
   * @throws ApicWebServiceException
   */
  public Set<UsecaseType> getUseCases(final Set<Long> usecaseIds) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_USECASES);
    GenericType<Set<UsecaseType>> type = new GenericType<Set<UsecaseType>>() {};
    for (Long usecaseId : usecaseIds) {
      wsTarget = wsTarget.queryParam(WsCommonConstants.RWS_QP_OBJ_ID, usecaseId);
    }
    Set<UsecaseType> ucTypeSet = get(wsTarget, type);
    return ucTypeSet;
  }

  /**
   * @param usecaseIds
   * @return
   * @throws ApicWebServiceException
   */
  public Set<UsecaseType> getUseCaseWithSectionTree(final Set<Long> usecaseIds) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_USECASE_WITH_SECTION);
    GenericType<Set<UsecaseType>> type = new GenericType<Set<UsecaseType>>() {};
    for (Long usecaseId : usecaseIds) {
      wsTarget = wsTarget.queryParam(WsCommonConstants.RWS_QP_OBJ_ID, usecaseId);
    }
    Set<UsecaseType> ucTypeSet = get(wsTarget, type);
    return ucTypeSet;
  }

}
