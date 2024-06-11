package com.bosch.caltool.icdm.ws.rest.client.a2l;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.datamodel.core.cns.ChangeDataCreator;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefinitionModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpRespDeleteModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpRespResetModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.a2l.WpRespLabelResponse;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.ws.rest.client.AbstractA2lWpRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.ChangeHandler;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapperChangeData;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.ModelParser;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for A2lWpResponsibility
 *
 * @author pdh2cob
 */
public class A2lWpResponsibilityServiceClient extends AbstractA2lWpRestServiceClient {

  /**
   * Mapper to create change data for A2lWpResponsibility delete operation
   */
  private static final IMapperChangeData A2L_WP_RESP_MAPPER = data -> {
    Collection<ChangeData<IModel>> changeDataList = new HashSet<>();
    ChangeDataCreator<IModel> changeDataCreator = new ChangeDataCreator<>();
    A2lWpRespDeleteModel deleteModel = (A2lWpRespDeleteModel) data;

    // create change data for deleted A2lWpResponsibilitys
    deleteModel.getDeletedA2lWpResponsibilitySet()
        .forEach(wpResp -> changeDataList.add(changeDataCreator.createDataForDelete(0L, wpResp)));

    // create change data for updated mappings
    deleteModel.getUpdatedA2lWpParamMapping().values().forEach(mapping -> changeDataList.add(changeDataCreator
        .createDataForUpdate(0L, deleteModel.getA2lWpParamMappingToBeUpdated().get(mapping.getId()), mapping)));

    // create change data for deleted mappings
    deleteModel.getDeletedA2lWpParamMapping().values()
        .forEach(mapping -> changeDataList.add(changeDataCreator.createDataForDelete(0L, mapping)));

    return changeDataList;
  };

  /**
   * Mapper to create change data for A2lWpResponsibility delete operation
   */
  private static final IMapperChangeData A2L_WP_RESP_WORKSPLIT_RESET_MAPPER = data -> {
    Collection<ChangeData<IModel>> changeDataList = new HashSet<>();
    ChangeDataCreator<IModel> changeDataCreator = new ChangeDataCreator<>();
    A2lWpRespResetModel resetModel = (A2lWpRespResetModel) data;

    A2lWpRespDeleteModel deleteModel = resetModel.getA2lWpRespDeleteModel();

    // create change data for deleted A2lWpResponsibilitys
    deleteModel.getDeletedA2lWpResponsibilitySet()
        .forEach(wpResp -> changeDataList.add(changeDataCreator.createDataForDelete(0L, wpResp)));

    // create change data for updated mappings
    deleteModel.getUpdatedA2lWpParamMapping().values().forEach(mapping -> changeDataList.add(changeDataCreator
        .createDataForUpdate(0L, deleteModel.getA2lWpParamMappingToBeUpdated().get(mapping.getId()), mapping)));

    // create change data for deleted mappings
    deleteModel.getDeletedA2lWpParamMapping().values()
        .forEach(mapping -> changeDataList.add(changeDataCreator.createDataForDelete(0L, mapping)));

    // Create change data to delete variant groups
    resetModel.getDeletedA2lVariantGroup().forEach(varGroup -> changeDataCreator.createDataForDelete(0L, varGroup));
    // Create change data to refresh new a2l wp definition version
    changeDataList.add(changeDataCreator.createDataForCreate(0L, resetModel.getNewA2lWpDefnVersion()));

    return changeDataList;
  };

  /**
   * Constructor
   */
  public A2lWpResponsibilityServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_A2L_WP_RESPONSIBILITY);
  }


  /**
   * @param wpDefnVersId - id
   * @return Map. Key - id, Value - A2lWpResponsibility object
   * @throws ApicWebServiceException exception while invoking service
   */
  public A2lWpDefinitionModel getA2lWpRespForWpDefnVers(final Long wpDefnVersId) throws ApicWebServiceException {
    LOGGER.debug("Get A2lWpDefinitionModel for given WP defn version id");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_WP_RESP_BY_WP_DEFN_VERS_ID)
        .queryParam(WsCommonConstants.RWS_QP_WP_DEFN_VERS_ID, wpDefnVersId);
    GenericType<A2lWpDefinitionModel> type = new GenericType<A2lWpDefinitionModel>() {};
    return get(wsTarget, type);
  }

  /**
   * Get A2lWpResponsibility using its id
   *
   * @param objId object's id
   * @return A2lWpResponsibility object
   * @throws ApicWebServiceException exception while invoking service
   */
  public A2lWpResponsibility get(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, A2lWpResponsibility.class);
  }

  /**
   * Create a A2lWpResponsibility record
   *
   * @param objs List of objects to create
   * @param pidcA2l PidcA2l
   * @return created A2lWpResponsibility object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Set<A2lWpResponsibility> create(final List<A2lWpResponsibility> objs, final PidcA2l pidcA2l)
      throws ApicWebServiceException {
    GenericType<Set<A2lWpResponsibility>> type = new GenericType<Set<A2lWpResponsibility>>() {};
    return create(getWsBase(), objs, type, pidcA2l);
  }


  /**
   * Update a A2lWpResponsibility record
   *
   * @param objs List of objects to update
   * @param pidcA2l PidcA2l
   * @return updated A2lWpResponsibility object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Set<A2lWpResponsibility> update(final List<A2lWpResponsibility> objs, final PidcA2l pidcA2l)
      throws ApicWebServiceException {
    GenericType<Set<A2lWpResponsibility>> type = new GenericType<Set<A2lWpResponsibility>>() {};
    return update(getWsBase(), objs, type, pidcA2l);
  }

  /**
   * Delete A2lWpResponsibility objects
   *
   * @param a2lWpRespIds A2lWpResponsibility ids to delete
   * @param pidcA2l pidcA2l to update
   * @throws ApicWebServiceException exception while invoking service
   */
  public void delete(final Set<Long> a2lWpRespIds, final PidcA2l pidcA2l) throws ApicWebServiceException {
    GenericType<A2lWpRespDeleteModel> type = new GenericType<A2lWpRespDeleteModel>() {};
    A2lWpRespDeleteModel ret =
        delete(getWsBase().path(WsCommonConstants.RWS_DELETE_A2L_WP_RESPONSIBILITY), a2lWpRespIds, type, pidcA2l);

    Collection<ChangeData<IModel>> newDataModelSet = ModelParser.getChangeData(ret, A2L_WP_RESP_MAPPER);

    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(newDataModelSet));

    displayMessage("A2l WP Responsibility(s) deleted!");
  }


  /**
   * @param pidcA2lId
   * @param varId
   * @throws ApicWebServiceException
   */
  public List<WpRespLabelResponse> getWpResp(final Long pidcA2lId, final Long varId) throws ApicWebServiceException {
    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_WP_RESP).queryParam(WsCommonConstants.RWS_QP_PIDC_A2L_ID, pidcA2lId)
            .queryParam(WsCommonConstants.RWS_QP_VARIANT_ID, varId);

    GenericType<List<WpRespLabelResponse>> type = new GenericType<List<WpRespLabelResponse>>() {};

    return get(wsTarget, type);
  }


  /**
   * @param pidcA2lId pidcA2lId
   * @throws ApicWebServiceException exception
   */
  public void resetWorkSplit(final Long pidcA2lId) throws ApicWebServiceException {
    GenericType<A2lWpRespResetModel> type = new GenericType<A2lWpRespResetModel>() {};

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_RESET_A2L_WP_PARAMS)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_A2L_ID, pidcA2lId);

    A2lWpRespResetModel ret = post(wsTarget, pidcA2lId, type);

    Collection<ChangeData<IModel>> newDataModelSet = ModelParser.getChangeData(ret, A2L_WP_RESP_WORKSPLIT_RESET_MAPPER);

    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(newDataModelSet));

    displayMessage("Reset work split is completed successfully!");
  }
}
