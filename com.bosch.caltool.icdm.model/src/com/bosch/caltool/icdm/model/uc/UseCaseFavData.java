/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.uc;

import com.bosch.caltool.icdm.model.apic.cocwp.PIDCCocWpUpdationModel;

/**
 * @author UKT1COB
 */
public class UseCaseFavData {

  private PIDCCocWpUpdationModel pidcCoCWPUpdModel;

  private UsecaseFavorite newlyCreatedUcFav;

  private UsecaseFavorite delPidcVersUcFav;


  /**
   * @return the pidcCoCWPUpdModel
   */
  public PIDCCocWpUpdationModel getPidcCoCWPUpdModel() {
    return this.pidcCoCWPUpdModel;
  }


  /**
   * @param pidcCoCWPUpdModel the pidcCoCWPUpdModel to set
   */
  public void setPidcCoCWPUpdModel(final PIDCCocWpUpdationModel pidcCoCWPUpdModel) {
    this.pidcCoCWPUpdModel = pidcCoCWPUpdModel;
  }

  /**
   * @return the newlyCreatedUcFav
   */
  public UsecaseFavorite getNewlyCreatedUcFav() {
    return this.newlyCreatedUcFav;
  }


  /**
   * @param newlyCreatedUcFav the newlyCreatedUcFav to set
   */
  public void setNewlyCreatedUcFav(final UsecaseFavorite newlyCreatedUcFav) {
    this.newlyCreatedUcFav = newlyCreatedUcFav;
  }


  /**
   * @return the delPidcVersUcFav
   */
  public UsecaseFavorite getDelPidcVersUcFav() {
    return this.delPidcVersUcFav;
  }


  /**
   * @param delPidcVersUcFav the delPidcVersUcFav to set
   */
  public void setDelPidcVersUcFav(final UsecaseFavorite delPidcVersUcFav) {
    this.delPidcVersUcFav = delPidcVersUcFav;
  }


}
