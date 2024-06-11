/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.bo.uc;

import com.bosch.caltool.icdm.model.uc.IUseCaseItem;
import com.bosch.caltool.icdm.model.uc.UseCase;
import com.bosch.caltool.icdm.model.uc.UseCaseGroup;
import com.bosch.caltool.icdm.model.uc.UseCaseSection;
import com.bosch.caltool.icdm.model.uc.UsecaseFavorite;
import com.bosch.caltool.icdm.model.uc.UsecaseModel;

/**
 * @author dmr1cob
 */
public class FavUseCaseItemCommon {

  private final UsecaseFavorite useCaseFav;

  /**
   * @return the useCaseFav
   */
  public UsecaseFavorite getUseCaseFav() {
    return this.useCaseFav;
  }


  private final UseCaseCommonDataHandler ucHandler;

  /**
   * Constructor
   *
   * @param useCaseFav UsecaseFavorite
   */
  public FavUseCaseItemCommon(final UsecaseFavorite useCaseFav, final UseCaseCommonDataHandler ucHandler) {
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
  public IUsecaseItemCommonBO getUseCaseItem(final UseCaseCommonDataHandler useCaseDataHandler) {
    if (this.useCaseFav.getGroupId() != null) {
      // get the usecase group from data cache
      UseCaseGroup ucg =
          useCaseDataHandler.getUseCaseDetailsModel().getUseCaseGroupMap().get(this.useCaseFav.getGroupId());
      UseCaseGroup parentucg =
          useCaseDataHandler.getUseCaseDetailsModel().getUseCaseGroupMap().get(ucg.getParentGroupId());
      UseCaseGroupCommonBO parentUcgCommonBO = null;
      if (null != parentucg) {
        parentUcgCommonBO = new UseCaseGroupCommonBO(parentucg, useCaseDataHandler.getUseCaseDetailsModel(),
            (UseCaseGroupCommonBO) getParentUseCaseItem(useCaseDataHandler, parentucg));
      }
      return new UseCaseGroupCommonBO(ucg, useCaseDataHandler.getUseCaseDetailsModel(), parentUcgCommonBO);
    }
    else if (this.useCaseFav.getSectionId() != null) {

      UseCaseSection ucs =
          useCaseDataHandler.getUseCaseDetailsModel().getUcSectionMap().get(this.useCaseFav.getSectionId());
      UseCase uc = useCaseDataHandler.getUseCaseDetailsModel().getUsecaseMap().get(ucs.getUseCaseId());
      UseCaseGroup ucg = useCaseDataHandler.getUseCaseDetailsModel().getUseCaseGroupMap().get(uc.getGroupId());
      UseCaseGroupCommonBO ucgCommonBO = new UseCaseGroupCommonBO(ucg, useCaseDataHandler.getUseCaseDetailsModel(),
          (UseCaseGroupCommonBO) getParentUseCaseItem(useCaseDataHandler, ucg));
      UsecaseModel usecaseModel =
          useCaseDataHandler.getUseCaseDetailsModel().getUsecaseDetailsModelMap().get(ucs.getUseCaseId());

      UsecaseSectionCommonBO usecaseSectionCommonBO =
          new UsecaseSectionCommonBO(ucs, usecaseModel, new UsecaseCommonBO(uc, ucgCommonBO));
      return usecaseSectionCommonBO;
    }
    else if (this.useCaseFav.getUseCaseId() != null)

    {
      // get the usecase from data cache
      UseCase uc = useCaseDataHandler.getUseCaseDetailsModel().getUsecaseMap().get(this.useCaseFav.getUseCaseId());
      return new UsecaseCommonBO(uc, (UseCaseGroupCommonBO) getParentUseCaseItem(useCaseDataHandler, uc));
    }
    return null;
  }

  /**
   * Returns the use case item object - Usecase/Usecase group /Usecase Section
   *
   * @param useCaseCommonDataHandler {@link UseCaseCommonDataHandler}
   * @param ucCommonBo {@link IUseCaseItem}
   * @return AbstractUseCaseItem
   */
  public IUsecaseItemCommonBO getParentUseCaseItem(final UseCaseCommonDataHandler useCaseCommonDataHandler,
      final IUseCaseItem ucCommonBo) {
    if (ucCommonBo instanceof UseCaseGroup) {
      // get the usecase group from data cache
      UseCaseGroup ucg = useCaseCommonDataHandler.getUseCaseDetailsModel().getUseCaseGroupMap().get(ucCommonBo.getId());
      UseCaseGroup parentucg =
          useCaseCommonDataHandler.getUseCaseDetailsModel().getUseCaseGroupMap().get(ucg.getParentGroupId());
      UseCaseGroupCommonBO parentUcgCommonBO = null;
      if (null != parentucg) {
        parentUcgCommonBO = new UseCaseGroupCommonBO(parentucg, useCaseCommonDataHandler.getUseCaseDetailsModel(),
            (UseCaseGroupCommonBO) getParentUseCaseItem(useCaseCommonDataHandler, parentucg));
      }
      return parentUcgCommonBO;
    }
    else if (ucCommonBo instanceof UseCaseSection) {
      // Not needed
    }
    else if (ucCommonBo instanceof UseCase) {
      // get the usecase from data cache
      UseCase uc = useCaseCommonDataHandler.getUseCaseDetailsModel().getUsecaseMap().get(ucCommonBo.getId());
      UseCaseGroup ucg = useCaseCommonDataHandler.getUseCaseDetailsModel().getUseCaseGroupMap().get(uc.getGroupId());
      UseCaseGroupCommonBO ucgCommonBO =
          new UseCaseGroupCommonBO(ucg, useCaseCommonDataHandler.getUseCaseDetailsModel(),
              (UseCaseGroupCommonBO) getParentUseCaseItem(useCaseCommonDataHandler, ucg));

      return ucgCommonBO;
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
