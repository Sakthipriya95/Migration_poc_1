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
import com.bosch.caltool.icdm.model.a2l.A2lVarGrpMapCmdModel;
import com.bosch.caltool.icdm.model.a2l.A2lVarGrpVariantMapping;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.ws.rest.client.AbstractA2lWpRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.ChangeHandler;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapperChangeData;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.ModelParser;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for A2lVarGrpVarMapping
 *
 * @author pdh2cob
 */
public class A2lVarGrpVarMappingServiceClient extends AbstractA2lWpRestServiceClient {

  private static final IMapperChangeData A2L_VAR_GRP_MAPPING = obj -> {
    Collection<ChangeData<IModel>> changeDataList = new HashSet<>();
    ChangeDataCreator<IModel> changeDataCreator = new ChangeDataCreator<>();

    A2lVarGrpMapCmdModel varChangeModel = (A2lVarGrpMapCmdModel) obj;


    varChangeModel.getMappingCreated()
        .forEach(var -> changeDataList.add(changeDataCreator.createDataForCreate(0L, var)));

    for (A2lVarGrpVariantMapping entry : varChangeModel.getMappingTobeDeleted()) {

      changeDataList.add(changeDataCreator.createDataForDelete(0L, entry));

    }


    return changeDataList;
  };


  /**
   * Constructor
   */
  public A2lVarGrpVarMappingServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_A2L_VAR_GRP_VAR_MAPPING);
  }


  /**
   * Get A2lVarGrpVarMapping using its id
   *
   * @param objId object's id
   * @return A2lVarGrpVarMapping object
   * @throws ApicWebServiceException exception while invoking service
   */
  public A2lVarGrpVariantMapping get(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, A2lVarGrpVariantMapping.class);
  }

  /**
   * Create a A2lVarGrpVarMapping record
   *
   * @param obj object to create
   * @param pidcA2l PIDC A2L object available
   * @return created A2lVarGrpVarMapping object
   * @throws ApicWebServiceException exception while invoking service
   */
  public A2lVarGrpVariantMapping create(final A2lVarGrpVariantMapping obj, final PidcA2l pidcA2l)
      throws ApicWebServiceException {
    return create(getWsBase(), obj, pidcA2l);
  }


  /**
   * @param a2lVarGroupMapping mapping
   * @param pidcA2l PIDC A2L object available
   * @throws ApicWebServiceException exception while invoking service
   */
  public void delete(final A2lVarGrpVariantMapping a2lVarGroupMapping, final PidcA2l pidcA2l)
      throws ApicWebServiceException {
    delete(getWsBase(), a2lVarGroupMapping, pidcA2l);
  }


  /**
   * map /unmap Variants
   *
   * @param model cmdObjList
   * @param pidcA2l PIDC A2L object available
   * @throws ApicWebServiceException ApicWebServiceException map /unmap Variants
   */
  public void updateVariantMappings(final A2lVarGrpMapCmdModel model, final PidcA2l pidcA2l)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_MAP_UNMAP);
    GenericType<A2lVarGrpMapCmdModel> type = new GenericType<A2lVarGrpMapCmdModel>() {};
    A2lVarGrpMapCmdModel a2lVarGrpModel = post(wsTarget, model, type);

    Collection<ChangeData<IModel>> newDataModelSet = ModelParser.getChangeData(a2lVarGrpModel, A2L_VAR_GRP_MAPPING);
    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(newDataModelSet));

    triggerPidcA2lChangeAsync(pidcA2l);

  }


}
