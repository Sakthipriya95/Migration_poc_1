/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;


/**
 * @author dja7cob
 */
public class AttachRvwResultInput {

  private RvwVariant rvwVariant;

  private boolean isLinkExistingQnaire;


  /**
   * @return the rvwVariant
   */
  public RvwVariant getRvwVariant() {
    return this.rvwVariant;
  }


  /**
   * @param rvwVariant the rvwVariant to set
   */
  public void setRvwVariant(final RvwVariant rvwVariant) {
    this.rvwVariant = rvwVariant;
  }


  /**
   * @return the isLinkExistingQnaire
   */
  public boolean isLinkExistingQnaire() {
    return this.isLinkExistingQnaire;
  }


  /**
   * @param isLinkExistingQnaire the isLinkExistingQnaire to set
   */
  public void setLinkExistingQnaire(final boolean isLinkExistingQnaire) {
    this.isLinkExistingQnaire = isLinkExistingQnaire;
  }

}
