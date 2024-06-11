/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.uc;

import com.bosch.caltool.icdm.model.uc.IUseCaseItem;
import com.bosch.caltool.icdm.model.uc.UseCase;
import com.bosch.caltool.icdm.model.uc.UseCaseGroup;
import com.bosch.caltool.icdm.model.uc.UseCaseSection;
import com.bosch.caltool.icdm.model.uc.UsecaseFavorite;
import com.bosch.caltool.icdm.model.uc.UsecaseModel;

// ICDM-1027
/**
 * This class provides all the properties of TUsecaseFavorite entity
 *
 * @author mkl2cob
 */
public class FavUseCaseItem {

  // selected use case fav
  private final UsecaseFavorite useCaseFav;

  /**
   * @return the useCaseFav
   */
  public UsecaseFavorite getUseCaseFav() {
    return this.useCaseFav;
  }

  // instance of data handler
  private final UseCaseDataHandler ucHandler;

  /**
   * Constructor
   *
   * @param useCaseFav UsecaseFavorite
   */
  public FavUseCaseItem(final UsecaseFavorite useCaseFav, final UseCaseDataHandler ucHandler) {
    this.useCaseFav = useCaseFav;
    this.ucHandler = ucHandler;
  }

  /**
   * @return id
   */
  public Long getID() {
    return this.useCaseFav.getId();
  }

  /**
   * Returns the name of the use case item
   * <p>
   */

  public String getName() {
    return getUseCaseItem(this.ucHandler).getName();
  }


  /**
   * Returns the use case item object - Usecase/Usecase group /Usecase Section
   *
   * @param useCaseDataHandler
   * @return AbstractUseCaseItem
   */
  public IUseCaseItemClientBO getUseCaseItem(final UseCaseDataHandler useCaseDataHandler) {
    if (this.useCaseFav.getGroupId() != null) {
      // get the usecase group from data cache
      UseCaseGroup ucg =
          useCaseDataHandler.getUseCaseDetailsModel().getUseCaseGroupMap().get(this.useCaseFav.getGroupId());
      UseCaseGroup parentucg =
          useCaseDataHandler.getUseCaseDetailsModel().getUseCaseGroupMap().get(ucg.getParentGroupId());
      UseCaseGroupClientBO parentUcgClientBO = null;
      if (null != parentucg) {
        parentUcgClientBO = new UseCaseGroupClientBO(parentucg, useCaseDataHandler.getUseCaseDetailsModel(),
            (UseCaseGroupClientBO) getParentUseCaseItem(useCaseDataHandler, parentucg));
      }
      return new UseCaseGroupClientBO(ucg, useCaseDataHandler.getUseCaseDetailsModel(), parentUcgClientBO);
    }
    else if (this.useCaseFav.getSectionId() != null) {

      UseCaseSection ucs =
          useCaseDataHandler.getUseCaseDetailsModel().getUcSectionMap().get(this.useCaseFav.getSectionId());
      UseCase uc = useCaseDataHandler.getUseCaseDetailsModel().getUsecaseMap().get(ucs.getUseCaseId());
      UseCaseGroup ucg = useCaseDataHandler.getUseCaseDetailsModel().getUseCaseGroupMap().get(uc.getGroupId());
      UseCaseGroupClientBO ucgClientBO = new UseCaseGroupClientBO(ucg, useCaseDataHandler.getUseCaseDetailsModel(),
          (UseCaseGroupClientBO) getParentUseCaseItem(useCaseDataHandler, ucg));
      UsecaseModel usecaseModel =
          useCaseDataHandler.getUseCaseDetailsModel().getUsecaseDetailsModelMap().get(ucs.getUseCaseId());

      return new UseCaseSectionClientBO(usecaseModel, ucs, new UsecaseClientBO(uc, ucgClientBO));
    }
    else if (this.useCaseFav.getUseCaseId() != null)

    {
      // get the usecase from data cache
      UseCase uc = useCaseDataHandler.getUseCaseDetailsModel().getUsecaseMap().get(this.useCaseFav.getUseCaseId());
      return new UsecaseClientBO(uc, (UseCaseGroupClientBO) getParentUseCaseItem(useCaseDataHandler, uc));
    }
    return null;
  }

  /**
   * Returns the use case item object - Usecase/Usecase group /Usecase Section
   *
   * @param useCaseDataHandler
   * @return AbstractUseCaseItem
   */
  public IUseCaseItemClientBO getParentUseCaseItem(final UseCaseDataHandler useCaseDataHandler,
      final IUseCaseItem ucClientBo) {
    if (ucClientBo instanceof UseCaseGroup) {
      // get the usecase group from data cache
      UseCaseGroup ucg = useCaseDataHandler.getUseCaseDetailsModel().getUseCaseGroupMap().get(ucClientBo.getId());
      UseCaseGroup parentucg =
          useCaseDataHandler.getUseCaseDetailsModel().getUseCaseGroupMap().get(ucg.getParentGroupId());
      UseCaseGroupClientBO parentUcgClientBO = null;
      if (null != parentucg) {
        parentUcgClientBO = new UseCaseGroupClientBO(parentucg, useCaseDataHandler.getUseCaseDetailsModel(),
            (UseCaseGroupClientBO) getParentUseCaseItem(useCaseDataHandler, parentucg));
      }
      return parentUcgClientBO;
    }
    else if (ucClientBo instanceof UseCaseSection) {
      // Not needed
    }
    else if (ucClientBo instanceof UseCase) {
      // get the usecase from data cache
      UseCase uc = useCaseDataHandler.getUseCaseDetailsModel().getUsecaseMap().get(ucClientBo.getId());
      UseCaseGroup ucg = useCaseDataHandler.getUseCaseDetailsModel().getUseCaseGroupMap().get(uc.getGroupId());
      UseCaseGroupClientBO ucgClientBO = new UseCaseGroupClientBO(ucg, useCaseDataHandler.getUseCaseDetailsModel(),
          (UseCaseGroupClientBO) getParentUseCaseItem(useCaseDataHandler, ucg));

      return ucgClientBO;
    }
    return null;
  }

  /**
   * @return true if the usecase item is project favourite usecase item
   */
  public boolean isProjectFavUCItem() {
    return this.useCaseFav.getProjectId() != null;

  }

  /**
   *
   */

  public String getCreatedUser() {
    return this.useCaseFav.getCreatedUser();
  }

  /**
   *
   */
  public String getModifiedUser() {
    return this.useCaseFav.getModifiedUser();
  }

  /**
   * {}
   */
  public String getCreatedDate() {
    return this.useCaseFav.getCreatedDate();
  }


  public String getModifiedDate() {
    return this.useCaseFav.getModifiedDate();
  }
}
