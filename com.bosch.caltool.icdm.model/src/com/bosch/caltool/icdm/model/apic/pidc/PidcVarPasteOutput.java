/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.List;
import java.util.Map;

/**
 * @author mkl2cob
 */
public class PidcVarPasteOutput {

  private List<PidcVariantAttribute> valAlreadyExists;

  private PidcVariant pastedVariant;

  private PidcVersion pidcVersion;

  private Map<String, List<PidcSubVariantAttribute>> valAlreadyExistsForSubVarMap;

  /**
   * @return the valAlreadyExists
   */
  public List<PidcVariantAttribute> getValAlreadyExists() {
    return this.valAlreadyExists;
  }


  /**
   * @param valAlreadyExists the valAlreadyExists to set
   */
  public void setValAlreadyExists(final List<PidcVariantAttribute> valAlreadyExists) {
    this.valAlreadyExists = valAlreadyExists;
  }


  /**
   * @return the pastedVariant
   */
  public PidcVariant getPastedVariant() {
    return this.pastedVariant;
  }


  /**
   * @param pastedVariant the pastedVariant to set
   */
  public void setPastedVariant(final PidcVariant pastedVariant) {
    this.pastedVariant = pastedVariant;
  }


  /**
   * @return the pidcVersion
   */
  public PidcVersion getPidcVersion() {
    return this.pidcVersion;
  }


  /**
   * @param pidcVersion the pidcVersion to set
   */
  public void setPidcVersion(final PidcVersion pidcVersion) {
    this.pidcVersion = pidcVersion;
  }


  /**
   * @return the valAlreadyExistsForSubVarMap
   */
  public Map<String, List<PidcSubVariantAttribute>> getValAlreadyExistsForSubVarMap() {
    return this.valAlreadyExistsForSubVarMap;
  }


  /**
   * @param valAlreadyExistsForSubVarMap the valAlreadyExistsForSubVarMap to set
   */
  public void setValAlreadyExistsForSubVarMap(
      final Map<String, List<PidcSubVariantAttribute>> valAlreadyExistsForSubVarMap) {
    this.valAlreadyExistsForSubVarMap = valAlreadyExistsForSubVarMap;
  }
}
