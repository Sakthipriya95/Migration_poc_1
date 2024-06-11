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
import com.bosch.caltool.icdm.model.a2l.A2lWpRespStatusUpdationModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibilityStatus;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.ChangeHandler;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapperChangeData;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.ModelParser;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for A2LWPResponsibilityStatus
 *
 * @author UKT1COB
 */
public class A2lWpResponsibilityStatusServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public A2lWpResponsibilityStatusServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_A2LWPRESPONSIBILITYSTATUS);
  }

  private static final IMapperChangeData A2L_WP_RESP_UPDATION_MAPPER = data -> {
    Collection<ChangeData<IModel>> changeDataList = new HashSet<>();
    ChangeDataCreator<IModel> changeDataCreator = new ChangeDataCreator<>();
    A2lWpRespStatusUpdationModel updationModel = (A2lWpRespStatusUpdationModel) data;

    List<A2lWpResponsibilityStatus> listOfNewlyCreatedA2lWPRespStatus =
        updationModel.getListOfNewlyCreatedA2lWpRespStatus();
    if ((null != listOfNewlyCreatedA2lWPRespStatus) && !listOfNewlyCreatedA2lWPRespStatus.isEmpty()) {
      listOfNewlyCreatedA2lWPRespStatus.stream()
          .forEach(a2lWpRespStatus -> changeDataList.add(changeDataCreator.createDataForCreate(0L, a2lWpRespStatus)));
    }

    Map<Long, A2lWpResponsibilityStatus> a2lWPRespStatusMapBeforeUpd = updationModel.getA2lWpRespStatusToBeUpdatedMap();
    if ((null != a2lWPRespStatusMapBeforeUpd) && !a2lWPRespStatusMapBeforeUpd.isEmpty()) {
      a2lWPRespStatusMapBeforeUpd.entrySet()
          .forEach(a2lWpRespStatusEntrySet -> changeDataList
              .add(changeDataCreator.createDataForUpdate(0L, a2lWpRespStatusEntrySet.getValue(),
                  updationModel.getA2lWpRespStatusMapAfterUpdate().get(a2lWpRespStatusEntrySet.getKey()))));
    }

    return changeDataList;
  };

  /**
   * Get A2lWpResponsibilityStatus using its id
   *
   * @param A2lWpResponsibilityStatusId object's id
   * @return A2lWpResponsibilityStatus object
   * @throws ApicWebServiceException exception while invoking service
   */
  public A2lWpResponsibilityStatus get(final Long A2lWpResponsibilityStatusId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, A2lWpResponsibilityStatusId);
    return get(wsTarget, A2lWpResponsibilityStatus.class);
  }

  /**
   * Get A2lWpResponsibilityStatus Wp definition version Id and Pidc Variant ID
   *
   * @param variantId , Pidc Variant ID
   * @param activeWpDefnVersId , Wp definition version Id
   * @return Map<Long, Map<Long, A2lWpResponsibilityStatus>> - Key : WPID, Value:[ Key : RESPID, Value:
   *         A2lWpResponsibilityStatus]
   * @throws ApicWebServiceException exception while invoking service
   */
  public Map<Long, Map<Long, A2lWpResponsibilityStatus>> getWpStatusByVarAndWpDefnVersId(final Long variantId,
      final Long activeWpDefnVersId)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_WORKPACKAGE_STATUS)
        .queryParam(WsCommonConstants.RWS_QP_VARIANT_ID, variantId)
        .queryParam(WsCommonConstants.RWS_QP_WP_DEFN_VERS_ID, activeWpDefnVersId);
    GenericType<Map<Long, Map<Long, A2lWpResponsibilityStatus>>> type =
        new GenericType<Map<Long, Map<Long, A2lWpResponsibilityStatus>>>() {};
    return get(wsTarget, type);
  }

  /**
   * Update a A2lWpResponsibilityStatus record
   *
   * @param a2lWPRespUpdModel - Model with data to be created or update
   * @return Rest response, with updated Model
   * @throws ApicWebServiceException exception while invoking service
   */
  public A2lWpRespStatusUpdationModel updateWpFinStatus(final A2lWpRespStatusUpdationModel a2lWPRespUpdModel)
      throws ApicWebServiceException {

    A2lWpRespStatusUpdationModel respModel =
        post(getWsBase().path(WsCommonConstants.RWS_UPDATE_A2L_WP_RESPONSIBILITY_STATUS), a2lWPRespUpdModel,
            new GenericType<A2lWpRespStatusUpdationModel>() {});

    Collection<ChangeData<IModel>> a2lWpespChangeDataModel =
        ModelParser.getChangeData(respModel, A2L_WP_RESP_UPDATION_MAPPER);

    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(a2lWpespChangeDataModel));

    displayMessage("A2lWpResponsibility WP Finished status created/updated successfully");

    return respModel;
  }


}
