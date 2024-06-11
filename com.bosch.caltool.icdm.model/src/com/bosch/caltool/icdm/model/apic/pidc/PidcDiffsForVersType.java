/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.List;

/**
 * @author dmr1cob
 */
public class PidcDiffsForVersType extends PidcDiffsType {

  private Long pidcVersionId;

  private List<Long> attrId;

  private String language;


  /**
   * @return the pidcVersionId
   */
  public Long getPidcVersionId() {
    return this.pidcVersionId;
  }


  /**
   * @param pidcVersionId the pidcVersionId to set
   */
  public void setPidcVersionId(final Long pidcVersionId) {
    this.pidcVersionId = pidcVersionId;
  }


  /**
   * @return the attrId
   */
  public List<Long> getAttrId() {
    return this.attrId;
  }


  /**
   * @param attrId the attrId to set
   */
  public void setAttrId(final List<Long> attrId) {
    this.attrId = attrId;
  }


  /**
   * @return the language
   */
  public String getLanguage() {
    return this.language;
  }


  /**
   * @param language the language to set
   */
  public void setLanguage(final String language) {
    this.language = language;
  }


}
