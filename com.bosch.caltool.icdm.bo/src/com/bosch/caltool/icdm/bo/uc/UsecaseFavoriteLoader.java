package com.bosch.caltool.icdm.bo.uc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.database.entity.apic.TUsecaseFavorite;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicUser;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectidcard;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.uc.UseCase;
import com.bosch.caltool.icdm.model.uc.UseCaseGroup;
import com.bosch.caltool.icdm.model.uc.UseCaseSection;
import com.bosch.caltool.icdm.model.uc.UsecaseFavorite;
import com.bosch.caltool.icdm.model.user.User;


/**
 * Loader class for UsecaseFavorite
 *
 * @author dmo5cob
 */
public class UsecaseFavoriteLoader extends AbstractBusinessObject<UsecaseFavorite, TUsecaseFavorite> {


  /**
   *
   */
  public static final String PROJECT_USE_CASE_GROUP_STR = "Project Use Case Group";
  /**
   *
   */
  public static final String PROJECT_USE_CASE_STR = "Project Use Case";
  /**
   *
   */
  public static final String PROJECT_USE_CASE_SECT_STR = "Project Use Case Section";

  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public UsecaseFavoriteLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.UC_FAV, TUsecaseFavorite.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UsecaseFavorite createDataObject(final TUsecaseFavorite entity) throws DataException {
    UsecaseFavorite object = new UsecaseFavorite();

    setCommonFields(object, entity);
    if (null != entity.getTabvUseCaseGroup()) {
      UseCaseGroup ucGrp =
          new UseCaseGroupLoader(getServiceData()).getDataObjectByID(entity.getTabvUseCaseGroup().getGroupId());
      object.setGroupId(ucGrp.getId());
    }
    if (null != entity.getTabvUseCas()) {
      UseCase uc = new UseCaseLoader(getServiceData()).getDataObjectByID(entity.getTabvUseCas().getUseCaseId());
      object.setUseCaseId(uc.getId());
    }
    if (null != entity.getTabvUseCaseSection()) {
      UseCaseSection ucs =
          new UseCaseSectionLoader(getServiceData()).getDataObjectByID(entity.getTabvUseCaseSection().getSectionId());
      object.setSectionId(ucs.getId());
    }
    if (null != entity.getTabvApicUser()) {
      User user = new UserLoader(getServiceData()).getDataObjectByID(entity.getTabvApicUser().getUserId());
      object.setUserId(user.getId());
    }
    if (null != entity.getTabvProjectidcard()) {
      Pidc project = new PidcLoader(getServiceData()).getDataObjectByID(entity.getTabvProjectidcard().getProjectId());
      object.setProjectId(project.getId());
    }
    return object;
  }

  /**
   * @param userId user id
   * @return key - UsecaseFav ID, value - attUsecaseFavoriteribute
   * @throws DataException if record not be found
   */
  public Map<Long, UsecaseFavorite> getFavoriteUseCases(final Long userId) throws DataException {
    Map<Long, UsecaseFavorite> retMap = new HashMap<>();
    TabvApicUser user = new UserLoader(getServiceData()).getEntityObject(userId);
    Set<TUsecaseFavorite> favSet = user.getTUsecaseFavorites();
    for (TUsecaseFavorite ucf : favSet) {
      UsecaseFavorite ucFav = getDataObjectByID(ucf.getUcFavId());
      retMap.put(ucf.getUcFavId(), ucFav);
    }
    return retMap;
  }

  /**
   * @param projectId project id
   * @return key - UsecaseFav ID, value - attUsecaseFavoriteribute
   * @throws DataException if record not be found
   */
  public Map<Long, UsecaseFavorite> getProjFavoriteUseCases(final Long projectId) throws DataException {
    Map<Long, UsecaseFavorite> retMap = new HashMap<>();
    TabvProjectidcard project = new PidcLoader(getServiceData()).getEntityObject(projectId);
    if (null != project) {
      Set<TUsecaseFavorite> favSet = project.getTUsecaseFavorites();
      for (TUsecaseFavorite ucf : favSet) {
        UsecaseFavorite ucFav = getDataObjectByID(ucf.getUcFavId());
        retMap.put(ucf.getUcFavId(), ucFav);
      }
    }
    return retMap;
  }

  /**
   * @param ucFavId
   * @param pidcVersId
   * @param ucItemId
   * @return
   * @throws DataException
   */
  public String getExtendedName(final Long pidcVersId, final Long ucItemId) throws IcdmException {
    String ucItemName = null;
    String prefixForDispText = null;

    if (new UseCaseGroupLoader(getServiceData()).isValidId(ucItemId)) {
      ucItemName = (new UseCaseGroupLoader(getServiceData())).getDataObjectByID(ucItemId).getName();
      prefixForDispText = PROJECT_USE_CASE_GROUP_STR;
    }
    else if (new UseCaseLoader(getServiceData()).isValidId(ucItemId)) {
      ucItemName = (new UseCaseLoader(getServiceData())).getDataObjectByID(ucItemId).getName();
      prefixForDispText = PROJECT_USE_CASE_STR;
    }
    else if (new UseCaseSectionLoader(getServiceData()).isValidId(ucItemId)) {
      ucItemName = (new UseCaseSectionLoader(getServiceData())).getDataObjectByID(ucItemId).getName();
      prefixForDispText = PROJECT_USE_CASE_SECT_STR;
    }
    else {
      throw new InvalidInputException("Invalid Use Case Item ID " + ucItemId);
    }
    PidcVersionLoader pidcVersionLoader = new PidcVersionLoader(getServiceData());
    PidcVersion pidcVersion = pidcVersionLoader.getDataObjectByID(pidcVersId);
    return prefixForDispText + ": " + pidcVersionLoader.getPidcTreePath(pidcVersId) + pidcVersion.getName() + "->" +
        ucItemName;

  }
}
