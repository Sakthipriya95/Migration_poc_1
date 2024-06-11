package com.bosch.caltool.icdm.ws.rest.client.uc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.datamodel.core.cns.ChangeDataCreator;
import com.bosch.caltool.icdm.model.apic.cocwp.PIDCCocWpUpdationModel;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcSubVarCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVariantCocWp;
import com.bosch.caltool.icdm.model.uc.UseCaseFavData;
import com.bosch.caltool.icdm.model.uc.UsecaseFavorite;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.ChangeHandler;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapperChangeData;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.ModelParser;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for UsecaseFavorite
 *
 * @author dmo5cob
 */
public class UsecaseFavoriteServiceClient extends AbstractRestServiceClient {


  private static final IMapperChangeData USECASE_CREATE_AND_DELETE_MAPPER = data -> {

    Collection<ChangeData<IModel>> changeDataList = new HashSet<>();
    ChangeDataCreator<IModel> changeDataCreator = new ChangeDataCreator<>();
    UseCaseFavData ucFavData = (UseCaseFavData) data;

    UsecaseFavorite newlyCreatedUcFav = ucFavData.getNewlyCreatedUcFav();
    if (null != newlyCreatedUcFav) {
      changeDataList.add(changeDataCreator.createDataForCreate(0L, newlyCreatedUcFav));
    }
    else {
      changeDataList.add(changeDataCreator.createDataForDelete(0L, ucFavData.getDelPidcVersUcFav()));
    }

    PIDCCocWpUpdationModel updationModel = ucFavData.getPidcCoCWPUpdModel();

    // Change Data creation for PidcVersCocWp
    updationModel.getPidcVersCocWpCreationMap().entrySet().forEach(
        pidcVersCocWp -> changeDataList.add(changeDataCreator.createDataForCreate(0L, pidcVersCocWp.getValue())));

    updationModel.getPidcVersCocWpBeforeUpdate().entrySet().forEach(
        pidcVersCocWpEtry -> changeDataList.add(changeDataCreator.createDataForUpdate(0L, pidcVersCocWpEtry.getValue(),
            updationModel.getPidcVersCocWpAfterUpdate().get(pidcVersCocWpEtry.getValue().getWPDivId()))));

    // changedata creation for pidcVarCocWpDel map
    updationModel.getPidcVarCocWpDeletionMap().entrySet().forEach(pidcVarCocWpDelEtry -> {
      Map<Long, PidcVariantCocWp> pidcVarCocWPMap = pidcVarCocWpDelEtry.getValue();
      pidcVarCocWPMap.entrySet().forEach(pidcVarCocWpDelSubEtry -> changeDataList
          .add(changeDataCreator.createDataForDelete(0L, pidcVarCocWpDelSubEtry.getValue())));
    });

    // changedata creation for pidcSubVarCocWpDel map
    updationModel.getPidcSubVarCocWpDeletionMap().entrySet().forEach(pidcSubVarCocWpDelEtry -> {
      Map<Long, PidcSubVarCocWp> pidcSubVarCocWPMap = pidcSubVarCocWpDelEtry.getValue();
      pidcSubVarCocWPMap.entrySet().forEach(pidcSubVarCocWpDelSubEtry -> changeDataList
          .add(changeDataCreator.createDataForDelete(0L, pidcSubVarCocWpDelSubEtry.getValue())));
    });

    return changeDataList;
  };


  /**
   * Constructor
   */
  public UsecaseFavoriteServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_UC, WsCommonConstants.RWS_USECASEFAVORITE);
  }

  /**
   * Get UsecaseFavorite using its id
   *
   * @param objId object's id
   * @return UsecaseFavorite object
   * @throws ApicWebServiceException exception while invoking service
   */
  public UsecaseFavorite getById(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, UsecaseFavorite.class);
  }

  /**
   * Get UsecaseFavorite using user id
   *
   * @param userId object's id
   * @return UsecaseFavorite object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Map<Long, UsecaseFavorite> getFavoriteUseCases(final Long userId) throws ApicWebServiceException {

    LOGGER.debug("Get favorite use cases ");

    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_GET_ALL).queryParam(WsCommonConstants.RWS_USECASEFAVORITE, userId);

    GenericType<Map<Long, UsecaseFavorite>> type = new GenericType<Map<Long, UsecaseFavorite>>() {};
    Map<Long, UsecaseFavorite> retMap = get(wsTarget, type);

    LOGGER.debug("Favorite use cases fetched for user . Number of items = {}", retMap.size());

    return retMap;
  }

  /**
   * Get UsecaseFavorite using project id
   *
   * @param projectId object's id
   * @return UsecaseFavorite object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Map<Long, UsecaseFavorite> getProjectFavoriteUseCases(final Long projectId) throws ApicWebServiceException {

    LOGGER.debug("Get favorite project use cases ");

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_USECASE)
        .queryParam(WsCommonConstants.RWS_PROJUSECASEFAVORITE, projectId);

    GenericType<Map<Long, UsecaseFavorite>> type = new GenericType<Map<Long, UsecaseFavorite>>() {};
    Map<Long, UsecaseFavorite> retMap = get(wsTarget, type);

    LOGGER.debug("Project favorite use cases fetched for project. Number of items = {}", retMap.size());

    return retMap;
  }

  /**
   * Create a UsecaseFavorite record
   *
   * @param obj object to create
   * @return created UsecaseFavorite object
   * @throws ApicWebServiceException exception while invoking service
   */
  public UseCaseFavData create(final UsecaseFavorite obj) throws ApicWebServiceException {

    UseCaseFavData respModel = post(getWsBase(), obj, new GenericType<UseCaseFavData>() {});

    Collection<ChangeData<IModel>> useCaseFavData =
        ModelParser.getChangeData(respModel, USECASE_CREATE_AND_DELETE_MAPPER);

    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(useCaseFavData));

    return respModel;
  }

  /**
   * delete a UsecaseFavorite record
   *
   * @param obj object to delete
   * @throws ApicWebServiceException exception while invoking service
   */
  public void delete(final UsecaseFavorite obj) throws ApicWebServiceException {

    UseCaseFavData respModel = post(getWsBase().path(WsCommonConstants.RWS_DEL_UCFAV_AND_UPD_COC_WP), obj,
        new GenericType<UseCaseFavData>() {});
    respModel.setDelPidcVersUcFav(obj);

    Collection<ChangeData<IModel>> useCaseFavData =
        ModelParser.getChangeData(respModel, USECASE_CREATE_AND_DELETE_MAPPER);

    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(useCaseFavData));
  }
}
