package com.bosch.caltool.icdm.ws.rest.client.a2l;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.datamodel.core.cns.ChangeDataCreator;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMapping;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMappingModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMappingUpdateModel;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.ws.rest.client.AbstractA2lWpRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.ChangeHandler;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapperChangeData;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.ModelParser;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for A2lWpParamMapping
 *
 * @author pdh2cob
 */
public class A2lWpParamMappingServiceClient extends AbstractA2lWpRestServiceClient {


  /**
   * Mapper to create change data for A2lwpParamMapping create/update/delete operations
   */
  private static final IMapperChangeData PARAM_MAPPING_UPDATE_MAPPER = data -> {
    Collection<ChangeData<IModel>> changeDataList = new HashSet<>();
    ChangeDataCreator<IModel> changeDataCreator = new ChangeDataCreator<>();
    A2lWpParamMappingUpdateModel updationModel = (A2lWpParamMappingUpdateModel) data;

    // create change data for new mappings
    updationModel.getCreatedA2lWpParamMapping().values()
        .forEach(mapping -> changeDataList.add(changeDataCreator.createDataForCreate(0L, mapping)));

    // create change data for updated mappings
    updationModel.getUpdatedA2lWpParamMapping().values().forEach(mapping -> changeDataList.add(changeDataCreator
        .createDataForUpdate(0L, updationModel.getA2lWpParamMappingToBeUpdated().get(mapping.getId()), mapping)));

    // create change data for deleted mappings
    updationModel.getDeletedA2lWpParamMapping().values()
        .forEach(mapping -> changeDataList.add(changeDataCreator.createDataForDelete(0L, mapping)));

    return changeDataList;
  };

  /**
   * Constructor
   */
  public A2lWpParamMappingServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_A2L_WP_PARAM_MAPPING);
  }

  /**
   * Get all A2lWpParamMapping records in system
   *
   * @param wpDefVersId A2lWpDefVersion id
   * @return Map. Key - id, Value - A2lWpParamMapping object
   * @throws ApicWebServiceException exception while invoking service
   */
  public A2lWpParamMappingModel getAllByWpDefVersId(final Long wpDefVersId) throws ApicWebServiceException {
    LOGGER.debug("Get A2lWpParamMapping records based on WpDefVersId ");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_WP_PARAM_MAPPING_BY_WP_DEFN_VERS_ID)
        .queryParam(WsCommonConstants.RWS_QP_WP_DEFN_VERS_ID, wpDefVersId);

    A2lWpParamMappingModel model = get(wsTarget, A2lWpParamMappingModel.class);
    LOGGER.debug("A2lWpParamMapping.getAllByWpDefVersId records loaded count = {}",
        model.getA2lWpParamMapping().size());
    return model;
  }

  /**
   * Get A2lWpParamMapping using its id
   *
   * @param objId object's id
   * @return A2lWpParamMapping object
   * @throws ApicWebServiceException exception while invoking service
   */
  public A2lWpParamMapping get(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, A2lWpParamMapping.class);
  }

  /**
   * @param a2lWpParamMappingUpdateModel - A2lWpParamMappingUpdateModel model
   * @param pidcA2l - Pidc A2l object to update
   * @return updated model
   * @throws ApicWebServiceException - exception
   */
  public A2lWpParamMappingUpdateModel updateA2lWpParamMapping(
      final A2lWpParamMappingUpdateModel a2lWpParamMappingUpdateModel, final PidcA2l pidcA2l)
      throws ApicWebServiceException {
    GenericType<A2lWpParamMappingUpdateModel> type = new GenericType<A2lWpParamMappingUpdateModel>() {};
    A2lWpParamMappingUpdateModel ret = updateModel(getWsBase().path(WsCommonConstants.RWS_UPDATE_A2L_WP_PARAM_MAPPING),
        a2lWpParamMappingUpdateModel, type, pidcA2l);

    Collection<ChangeData<IModel>> newDataModelSet = ModelParser.getChangeData(ret, PARAM_MAPPING_UPDATE_MAPPER);

    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(newDataModelSet));

    displayMessage("A2l WP Parameter mappings updated !");

    return ret;
  }


}
