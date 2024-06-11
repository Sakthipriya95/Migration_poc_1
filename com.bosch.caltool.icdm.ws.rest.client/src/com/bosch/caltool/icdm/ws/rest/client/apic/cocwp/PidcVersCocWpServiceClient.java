package com.bosch.caltool.icdm.ws.rest.client.apic.cocwp;

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
import com.bosch.caltool.icdm.model.apic.cocwp.PIDCCocWpUpdationInputModel;
import com.bosch.caltool.icdm.model.apic.cocwp.PIDCCocWpUpdationModel;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcCocWpExternalAPIData;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcSubVarCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVariantCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVersCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVersCocWpData;
import com.bosch.caltool.icdm.model.uc.UsecaseFavorite;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.ChangeHandler;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapperChangeData;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.ModelParser;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for PidcVersCocWp
 *
 * @author UKT1COB
 */
public class PidcVersCocWpServiceClient extends AbstractRestServiceClient {


  private static final IMapperChangeData COC_WP_CREATE_AND_UPDATE_MAPPER = data -> {

    Collection<ChangeData<IModel>> changeDataList = new HashSet<>();
    ChangeDataCreator<IModel> changeDataCreator = new ChangeDataCreator<>();
    PIDCCocWpUpdationModel updationModel = (PIDCCocWpUpdationModel) data;

    // Change Data creation for PidcVersCocWp
    updationModel.getPidcVersCocWpCreationMap().entrySet().forEach(
        pidcVersCocWp -> changeDataList.add(changeDataCreator.createDataForCreate(0L, pidcVersCocWp.getValue())));
    updationModel.getPidcVersCocWpBeforeUpdate().entrySet().forEach(
        pidcVersCocWpEtry -> changeDataList.add(changeDataCreator.createDataForUpdate(0L, pidcVersCocWpEtry.getValue(),
            updationModel.getPidcVersCocWpAfterUpdate().get(pidcVersCocWpEtry.getValue().getWPDivId()))));
    // Change Data creation for PidcVarsCocWp
    updationModel.getPidcVarCocWpCreationMap().entrySet().forEach(pidcVarCocWpEtry -> {
      Map<Long, PidcVariantCocWp> pidcVarCocWPMap = pidcVarCocWpEtry.getValue();
      pidcVarCocWPMap.entrySet().forEach(pidcVarCocWpSubEntry -> changeDataList
          .add(changeDataCreator.createDataForCreate(0L, pidcVarCocWpSubEntry.getValue())));
    });
    // changedata updation for pidcVarCocWpDel map
    updationModel.getPidcVarCocWpMapBeforeUpdate().entrySet().forEach(pidcVarCocWpBfrEtry -> {
      Map<Long, PidcVariantCocWp> pidcVarCocWPMap = pidcVarCocWpBfrEtry.getValue();
      pidcVarCocWPMap.entrySet().forEach(pidcVarCocWpBfrSubEntry -> {
        PidcVariantCocWp pidcVarCocBfrWp = pidcVarCocWpBfrSubEntry.getValue();
        PidcVariantCocWp pidcVarCocAfrWp = updationModel.getPidcVarCocWpMapAfterUpdate()
            .get(pidcVarCocBfrWp.getPidcVariantId()).get(pidcVarCocBfrWp.getWPDivId());
        changeDataList.add(changeDataCreator.createDataForUpdate(0L, pidcVarCocBfrWp, pidcVarCocAfrWp));
      });
    });
    // changedata creation for pidcVarCocWpDel map
    updationModel.getPidcVarCocWpDeletionMap().entrySet().forEach(pidcVarCocWpDelEtry -> {
      Map<Long, PidcVariantCocWp> pidcVarCocWPMap = pidcVarCocWpDelEtry.getValue();
      pidcVarCocWPMap.entrySet().forEach(pidcVarCocWpDelSubEtry -> changeDataList
          .add(changeDataCreator.createDataForDelete(0L, pidcVarCocWpDelSubEtry.getValue())));
    });
    // Change Data creation for PidcSubVarCocWp
    updationModel.getPidcSubVarCocWpCreationMap().entrySet().forEach(pidcSubVarCocWpEntry -> {
      Map<Long, PidcSubVarCocWp> pidcsubVarCocWpMap = pidcSubVarCocWpEntry.getValue();
      pidcsubVarCocWpMap.entrySet().forEach(pidcsubVarCocWpIntEntry -> changeDataList
          .add(changeDataCreator.createDataForCreate(0L, pidcsubVarCocWpIntEntry.getValue())));
    });
    // changedata updation for pidcSubVarCocWpDel map
    updationModel.getPidcSubVarCocWpBeforeUpdateMap().entrySet().forEach(pidcSubVarCocWpBfrEtry -> {
      Map<Long, PidcSubVarCocWp> pidcSubVarCocWPMap = pidcSubVarCocWpBfrEtry.getValue();
      pidcSubVarCocWPMap.entrySet().forEach(pidcVarCocWpBfrSubEntry -> {
        PidcSubVarCocWp pidcSubVarCocBfrWp = pidcVarCocWpBfrSubEntry.getValue();
        PidcSubVarCocWp pidcSubVarCocAfrWp = updationModel.getPidcSubVarCocWpAfterUpdateMap()
            .get(pidcSubVarCocBfrWp.getPidcSubVarId()).get(pidcSubVarCocBfrWp.getWPDivId());
        changeDataList.add(changeDataCreator.createDataForUpdate(0L, pidcSubVarCocBfrWp, pidcSubVarCocAfrWp));
      });
    });

    // changedata creation for pidcSubVarCocWpDel map
    updationModel.getPidcSubVarCocWpDeletionMap().entrySet().forEach(pidcSubVarCocWpDelEtry -> {
      Map<Long, PidcSubVarCocWp> pidcSubVarCocWPMap = pidcSubVarCocWpDelEtry.getValue();
      pidcSubVarCocWPMap.entrySet().forEach(pidcSubVarCocWpDelSubEtry -> changeDataList
          .add(changeDataCreator.createDataForDelete(0L, pidcSubVarCocWpDelSubEtry.getValue())));
    });

    updationModel.getPidcVariantMap().entrySet().forEach(pidcVarEntry -> changeDataList
        .add(changeDataCreator.createDataForUpdate(0L, pidcVarEntry.getValue(), pidcVarEntry.getValue())));

    updationModel.getPidcSubVariantMap().entrySet().forEach(pidcSubVarEntry -> changeDataList
        .add(changeDataCreator.createDataForUpdate(0L, pidcSubVarEntry.getValue(), pidcSubVarEntry.getValue())));

    if (updationModel.getPidcVersion() != null) {
      changeDataList.add(
          changeDataCreator.createDataForUpdate(0L, updationModel.getOldPidcVersion(), updationModel.getPidcVersion()));
    }

    List<UsecaseFavorite> listOfnewlyCreatedUcFav = updationModel.getListOfNewlyCreatedUcFav();
    if ((null != listOfnewlyCreatedUcFav) && !listOfnewlyCreatedUcFav.isEmpty()) {
      listOfnewlyCreatedUcFav.stream()
          .forEach(createdUCFav -> changeDataList.add(changeDataCreator.createDataForCreate(0L, createdUCFav)));
    }

    List<UsecaseFavorite> listOfDelUcFav = updationModel.getListOfDelUcFav();
    if ((null != listOfDelUcFav) && !listOfDelUcFav.isEmpty()) {
      listOfDelUcFav.stream()
          .forEach(delUCFav -> changeDataList.add(changeDataCreator.createDataForDelete(0L, delUCFav)));
    }

    return changeDataList;
  };

  /**
   * Constructor
   */
  public PidcVersCocWpServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_PIDCVERSCOCWP);
  }

  /**
   * Get PidcVersCocWp using its id
   *
   * @param PidcVersCocWpId object's id
   * @return PidcVersCocWp object
   * @throws ApicWebServiceException exception while invoking service
   */
  public PidcVersCocWp get(final Long PidcVersCocWpId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, PidcVersCocWpId);
    return get(wsTarget, PidcVersCocWp.class);
  }

  /**
   * Get all PidcVersCocWp records in system
   *
   * @param pidcVersionId PIDC Vers Id
   * @return Map. Key - id, Value - PidcVersCocWp object
   * @throws ApicWebServiceException exception while invoking service
   */
  public PidcVersCocWpData getAllCocWpByPidcVersId(final Long pidcVersionId) throws ApicWebServiceException {
    LOGGER.debug("Get all PidcVersCocWp records ");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL_COC_WP_BY_PIDC_VERS)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_VERSION_ID, pidcVersionId);
    GenericType<PidcVersCocWpData> type = new GenericType<PidcVersCocWpData>() {};
    PidcVersCocWpData retMap = get(wsTarget, type);
    LOGGER.debug("PidcVersCocWp records loaded count = {}", retMap.getPidcVersCocWpMap().size());
    return retMap;
  }


  /**
   * Get all PidcVersCocWp records in system
   *
   * @param pidcVersionId PIDC Vers Id
   * @return Map. Key - id, Value - PidcVersCocWp object
   * @throws ApicWebServiceException exception while invoking service
   */
  public PidcCocWpExternalAPIData getAllCocWpExternal(final Long pidcVersionId) throws ApicWebServiceException {
    LOGGER.debug("Get all PidcVersCocWp records ");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL_COC_WP_BY_PIDC_VERS)
        .path(WsCommonConstants.RWS_EXTERNAL).queryParam(WsCommonConstants.RWS_QP_PIDC_VERSION_ID, pidcVersionId);
    GenericType<PidcCocWpExternalAPIData> type = new GenericType<PidcCocWpExternalAPIData>() {};
    PidcCocWpExternalAPIData retMap = get(wsTarget, type);
    LOGGER.debug("PidcVersCocWp records loaded count = {}", retMap.getPidcVersCocWpMap().size());
    return retMap;
  }


  /**
   * Update a Coc WP
   *
   * @param pidcCocWpUpdationInputModel model with maps to perform actions
   * @return updated pidcCocWpUpdationModel object
   * @throws ApicWebServiceException exception while invoking service
   */
  public PIDCCocWpUpdationModel updatePidcCocWPs(final PIDCCocWpUpdationInputModel pidcCocWpUpdationInputModel)
      throws ApicWebServiceException {

    PIDCCocWpUpdationModel respModel = post(getWsBase().path(WsCommonConstants.RWS_UPDATE_COC_WP),
        pidcCocWpUpdationInputModel, new GenericType<PIDCCocWpUpdationModel>() {});

    respModel.setOldPidcVersion(pidcCocWpUpdationInputModel.getPidcVersionOld());
    Collection<ChangeData<IModel>> pidcCocWpUpdatedModel =
        ModelParser.getChangeData(respModel, COC_WP_CREATE_AND_UPDATE_MAPPER);

    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(pidcCocWpUpdatedModel));

    displayMessage("PIDC CoC WP updated successfully!");

    return respModel;
  }

}
