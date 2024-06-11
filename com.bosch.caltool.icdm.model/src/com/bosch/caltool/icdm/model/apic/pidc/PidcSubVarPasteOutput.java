/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mkl2cob
 */
public class PidcSubVarPasteOutput {

  private List<PidcSubVariantAttribute> valAlreadyExists;

  private PidcVersion pidcVersBeforeUpdate;

  private PidcVersion pidcVersAfterUpdate;

  private PidcVariant pidcVarBeforeUpdate;

  private PidcVariant pidcVarAfterUpdate;

  private PidcSubVariant pidcSubVarBeforeUpdate;


  private PidcSubVariant pastedSubVariant;

  /**
   * @return the valAlreadyExists
   */
  public List<PidcSubVariantAttribute> getValAlreadyExists() {
    return this.valAlreadyExists;
  }

  /**
   * @param valAlreadyExists the valAlreadyExists to set
   */
  public void setValAlreadyExists(final List<PidcSubVariantAttribute> valAlreadyExists) {
    this.valAlreadyExists = valAlreadyExists == null ? null : new ArrayList<>(valAlreadyExists);
  }

  /**
   * @return the pastedSubVariant
   */
  public PidcSubVariant getPastedSubVariant() {
    return this.pastedSubVariant;
  }

  /**
   * @param pastedSubVariant the pastedSubVariant to set
   */
  public void setPastedSubVariant(final PidcSubVariant pastedSubVariant) {
    this.pastedSubVariant = pastedSubVariant;
  }


  /**
   * @return the pidcVersBeforeUpdate
   */
  public PidcVersion getPidcVersBeforeUpdate() {
    return this.pidcVersBeforeUpdate;
  }


  /**
   * @param pidcVersBeforeUpdate the pidcVersBeforeUpdate to set
   */
  public void setPidcVersBeforeUpdate(final PidcVersion pidcVersBeforeUpdate) {
    this.pidcVersBeforeUpdate = pidcVersBeforeUpdate;
  }


  /**
   * @return the pidcVersAfterUpdate
   */
  public PidcVersion getPidcVersAfterUpdate() {
    return this.pidcVersAfterUpdate;
  }


  /**
   * @param pidcVersAfterUpdate the pidcVersAfterUpdate to set
   */
  public void setPidcVersAfterUpdate(final PidcVersion pidcVersAfterUpdate) {
    this.pidcVersAfterUpdate = pidcVersAfterUpdate;
  }


  /**
   * @return the pidcVarBeforeUpdate
   */
  public PidcVariant getPidcVarBeforeUpdate() {
    return this.pidcVarBeforeUpdate;
  }


  /**
   * @param pidcVarBeforeUpdate the pidcVarBeforeUpdate to set
   */
  public void setPidcVarBeforeUpdate(final PidcVariant pidcVarBeforeUpdate) {
    this.pidcVarBeforeUpdate = pidcVarBeforeUpdate;
  }


  /**
   * @return the pidcVarAfterUpdate
   */
  public PidcVariant getPidcVarAfterUpdate() {
    return this.pidcVarAfterUpdate;
  }


  /**
   * @param pidcVarAfterUpdate the pidcVarAfterUpdate to set
   */
  public void setPidcVarAfterUpdate(final PidcVariant pidcVarAfterUpdate) {
    this.pidcVarAfterUpdate = pidcVarAfterUpdate;
  }


  /**
   * @return the pidcSubVarBeforeUpdate
   */
  public PidcSubVariant getPidcSubVarBeforeUpdate() {
    return this.pidcSubVarBeforeUpdate;
  }


  /**
   * @param pidcSubVarBeforeUpdate the pidcSubVarBeforeUpdate to set
   */
  public void setPidcSubVarBeforeUpdate(final PidcSubVariant pidcSubVarBeforeUpdate) {
    this.pidcSubVarBeforeUpdate = pidcSubVarBeforeUpdate;
  }
}
