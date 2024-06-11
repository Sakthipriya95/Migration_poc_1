/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.HashMap;
import java.util.Map;

import com.bosch.caltool.icdm.model.a2l.A2lWPRespModel;

/**
 * Input model to calculate status of selected WP Responsibility from PIDC/Favorites view
 *
 * @author apj4cob
 */
public class TreeViewSelectnRespWP {


  // a2l under which selected wp resp node is available
  private Long pidcA2lID;

  // variant under which selected wp resp node is available
  private Long variantID;

  /**
   * Map of a2l responsibilities and it's workpackages Key-A2l Responsibility ID Value-Map of Workapackage ID and A2l WP
   * Resp Model
   */
  private Map<Long, Map<Long, A2lWPRespModel>> respWpA2lWpRespModelMap = new HashMap<>();


  /**
   * @return the pidcA2lID
   */
  public Long getPidcA2lID() {
    return this.pidcA2lID;
  }


  /**
   * @param pidcA2lID the pidcA2lID to set
   */
  public void setPidcA2lID(final Long pidcA2lID) {
    this.pidcA2lID = pidcA2lID;
  }


  /**
   * @return the variantID
   */
  public Long getVariantID() {
    return this.variantID;
  }


  /**
   * @param variantID the variantID to set
   */
  public void setVariantID(final Long variantID) {
    this.variantID = variantID;
  }


  /**
   * @return the respWpA2lWpRespModelMap
   */
  public Map<Long, Map<Long, A2lWPRespModel>> getRespWpA2lWpRespModelMap() {
    return this.respWpA2lWpRespModelMap;
  }


  /**
   * @param respWpA2lWpRespModelMap the respWpA2lWpRespModelMap to set
   */
  public void setRespWpA2lWpRespModelMap(final Map<Long, Map<Long, A2lWPRespModel>> respWpA2lWpRespModelMap) {
    this.respWpA2lWpRespModelMap = respWpA2lWpRespModelMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "Selected WP Responsibility Status ";
  }
}
