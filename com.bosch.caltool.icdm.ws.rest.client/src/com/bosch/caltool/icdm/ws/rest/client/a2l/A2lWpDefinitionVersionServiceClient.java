package com.bosch.caltool.icdm.ws.rest.client.a2l;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.datamodel.core.cns.ChangeDataCreator;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.a2l.A2lWpRespStatusUpdationModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibilityStatus;
import com.bosch.caltool.icdm.model.a2l.CopyA2lWpRespResponse;
import com.bosch.caltool.icdm.model.a2l.CopyPar2WpFromA2lInput;
import com.bosch.caltool.icdm.model.a2l.WpImportFromFuncInput;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcTakeOverA2lWrapper;
import com.bosch.caltool.icdm.ws.rest.client.AbstractA2lWpRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.Activator;
import com.bosch.caltool.icdm.ws.rest.client.cns.ChangeHandler;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapper;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapperChangeData;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.ModelParser;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for A2lWpDefinitionVersion
 *
 * @author pdh2cob
 */
public class A2lWpDefinitionVersionServiceClient extends AbstractA2lWpRestServiceClient {

  private static final IMapper WP_RESP_IMPORT_MAPPER = obj -> {
    Collection<IModel> changeDataList = new HashSet<>();
    changeDataList.addAll(((CopyA2lWpRespResponse) obj).getWpDefSet());
    changeDataList.addAll(((CopyA2lWpRespResponse) obj).getPidcA2lSet());
    changeDataList.addAll(((CopyA2lWpRespResponse) obj).getWpRespPalSet());
    changeDataList.addAll(((CopyA2lWpRespResponse) obj).getA2lWpParamMappingSet());
    changeDataList.addAll(((CopyA2lWpRespResponse) obj).getVarGrpSet());
    return changeDataList;
  };
  private static final IMapperChangeData A2l_WP_RESPONSIBILITY_STATUS = data -> {

    Collection<ChangeData<IModel>> changeDataList = new HashSet<>();
    ChangeDataCreator<IModel> changeDataCreator = new ChangeDataCreator<>();
    A2lWpRespStatusUpdationModel a2lWpResp = (A2lWpRespStatusUpdationModel) data;

    Map<Long, A2lWpResponsibilityStatus> wpRespMap = a2lWpResp.getA2lWpRespStatusMapAfterUpdate();

    if ((wpRespMap != null)) {
      wpRespMap.forEach((a2lWpResponsibilityStatusId, a2lWpResponsibilityStatus) -> changeDataList
          .add(changeDataCreator.createDataForCreate(0L, wpRespMap.get(a2lWpResponsibilityStatusId))));
    }

    return changeDataList;
  };

  /**
   * Constructor
   */
  public A2lWpDefinitionVersionServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_A2L_WP_DEFINITION_VERSION);
  }


  /**
   * Get A2lWpDefinitionVersion using its id
   *
   * @param objId object's id
   * @return A2lWpDefinitionVersion object
   * @throws ApicWebServiceException exception while invoking service
   */
  public A2lWpDefnVersion get(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, A2lWpDefnVersion.class);
  }

  /**
   * Get all A2lWpDefinitionVersion records in system
   *
   * @param pidcA2lId - pidc a2l id
   * @return Map. Key - id, Value - A2lWpDefinitionVersion object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Map<Long, A2lWpDefnVersion> getWPDefnVersForPidcA2l(final Long pidcA2lId) throws ApicWebServiceException {
    LOGGER.debug("Get all A2lWpDefinitionVersion records for given pidcA2l id");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_WP_DEFN_VERS_BY_PIDC_A2L_ID)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_A2L_ID, pidcA2lId);
    GenericType<Map<Long, A2lWpDefnVersion>> type = new GenericType<Map<Long, A2lWpDefnVersion>>() {};
    Map<Long, A2lWpDefnVersion> retMap = get(wsTarget, type);
    LOGGER.debug("A2lWpDefinitionVersion records loaded count = {}", retMap.size());
    return retMap;
  }


  /**
   * Create a A2lWpDefinitionVersion record
   *
   * @param obj object to create
   * @param pidcA2l
   * @return created A2lWpDefinitionVersion object
   * @throws ApicWebServiceException exception while invoking service
   */
  public A2lWpDefnVersion create(final A2lWpDefnVersion obj, final PidcA2l pidcA2l) throws ApicWebServiceException {
    return create(getWsBase(), obj, pidcA2l);
  }

  /**
   * Update a list of A2lWpDefinitionVersion records
   *
   * @param wpDefnList A2lWpDefinitionVersion list to update
   * @return updated A2lWpDefinitionVersion object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Set<A2lWpDefnVersion> update(final List<A2lWpDefnVersion> wpDefnList) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase();
    GenericType<Set<A2lWpDefnVersion>> type = new GenericType<Set<A2lWpDefnVersion>>() {};
    Set<A2lWpDefnVersion> retSet = update(wsTarget, wpDefnList, type);
    LOGGER.debug("A2lWpDefinitionVersion objects have been updated. No of A2lWpDefinitionVersion updated : {}",
        retSet.size());
    return retSet;

  }

  /**
   * copy par2wp mappings from one a2l file to another
   *
   * @param inputDataList CopyPar2WpFromA2lInput
   * @throws ApicWebServiceException
   */
  public void copyA2lWpResp(final List<CopyPar2WpFromA2lInput> inputDataList) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_COPY_PAR2WP);
    create(wsTarget, inputDataList, CopyA2lWpRespResponse.class, WP_RESP_IMPORT_MAPPER);
    LOGGER.debug("Copy a2l mappings Completed");
  }


  /**
   * @param pidcTakeOverA2lWrapper as input
   * @return CopyA2lWpRespResponse
   * @throws ApicWebServiceException as exception
   */
  public final CopyA2lWpRespResponse createPidcA2landTakeOverFromA2l(
      final PidcTakeOverA2lWrapper pidcTakeOverA2lWrapper)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_TAKE_OVER_A2L);
    CopyA2lWpRespResponse copyA2lWp =
        create(wsTarget, pidcTakeOverA2lWrapper, CopyA2lWpRespResponse.class, WP_RESP_IMPORT_MAPPER);
    LOGGER.debug("Create PidcA2l and Copy a2l mappings Completed");
    return copyA2lWp;
  }

  /**
   * @param pidcA2lId
   * @returns true if the pic a2l has active version
   * @throws ApicWebServiceException
   */
  public boolean hasActiveVersion(final Long pidcA2lId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_ACTIVE_WP_DEFN_VERS_BY_PIDC_A2L_ID)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_A2L_ID, pidcA2lId);
    return get(wsTarget, boolean.class);
  }

  /**
   * @param pidcA2lId pidc a2l id
   * @return isDefaultWpRespLabelAssignmentExist
   * @throws ApicWebServiceException exception in ws call
   */
  public boolean isDefaultWpRespLabelAssignmentExist(final Long pidcA2lId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_DEFAULT_WP_RESP_ASSIGN_EXIST)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_A2L_ID, pidcA2lId);
    return get(wsTarget, boolean.class);
  }

  /**
   * @param inputData WpImportFromFuncInput
   * @return CopyA2lWpRespResponse
   * @throws ApicWebServiceException as exception
   */
  public final CopyA2lWpRespResponse importWpFromFunctions(final WpImportFromFuncInput inputData)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_WP_IMPORT_FROM_FUNC);
    CopyA2lWpRespResponse copyA2lWp = create(wsTarget, inputData, CopyA2lWpRespResponse.class, WP_RESP_IMPORT_MAPPER);

    LOGGER.debug("Create WP from Functions Completed");

    return copyA2lWp;
  }


  /**
   * Service client to Update Workpackage Responsibility Status for new Wp def active version
   *
   * @param prevWpDefActiveVers prevWpDefActiveVers
   * @param newWpDefActiveVers newWpDefActiveVers
   */
  public void updateWorkpackageStatus(final long prevWpDefActiveVers, final A2lWpDefnVersion newWpDefActiveVers) {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_UPDATE_WP_STATUS_FOR_WP_DEF_VER)
        .queryParam(WsCommonConstants.RWS_PREVIOUS_WP_DEF_ACTIVE_VERS_ID, prevWpDefActiveVers)
        .queryParam(WsCommonConstants.RWS_NEW_WP_DEF_ACTIVE_VERS_ID, newWpDefActiveVers.getId());
    new Thread(() -> {
      try {
        A2lWpRespStatusUpdationModel responseA2LWpRespStatus =
            getResponseAsync(createFutureUpdate(wsTarget, newWpDefActiveVers), A2lWpRespStatusUpdationModel.class);
        Collection<ChangeData<IModel>> newDataModelSet =
            ModelParser.getChangeData(responseA2LWpRespStatus, A2l_WP_RESPONSIBILITY_STATUS);
        (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(newDataModelSet));
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
      }

    }).start();
  }

}
