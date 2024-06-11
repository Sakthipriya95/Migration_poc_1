/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dja7cob
 */
public class PidcImportCompareData {

  /**
   * Map of key - attr id, value - version level Import attr
   */
  private Map<Long, ProjectImportAttr<PidcVersionAttribute>> verAttrImportData = new HashMap<>();
  /**
   * Map of key - attr id, value - variant level Import attr
   */
  private Map<Long, Map<Long, ProjectImportAttr<PidcVariantAttribute>>> varAttrImportData = new HashMap<>();
  /**
   * Map of key - variant id, value - map of <key - attr id, value - subvariant level Import attr>
   */
  private Map<Long, Map<Long, ProjectImportAttr<PidcSubVariantAttribute>>> subvarAttrImportData = new HashMap<>();


  /**
   * @return the verAttrImportData
   */
  public Map<Long, ProjectImportAttr<PidcVersionAttribute>> getVerAttrImportData() {
    return this.verAttrImportData;
  }


  /**
   * @param verAttrImportData the verAttrImportData to set
   */
  public void setVerAttrImportData(final Map<Long, ProjectImportAttr<PidcVersionAttribute>> verAttrImportData) {
    this.verAttrImportData = verAttrImportData;
  }


  /**
   * @return the varAttrImportData
   */
  public Map<Long, Map<Long, ProjectImportAttr<PidcVariantAttribute>>> getVarAttrImportData() {
    return this.varAttrImportData;
  }


  /**
   * @param varAttrImportData the varAttrImportData to set
   */
  public void setVarAttrImportData(
      final Map<Long, Map<Long, ProjectImportAttr<PidcVariantAttribute>>> varAttrImportData) {
    this.varAttrImportData = varAttrImportData;
  }


  /**
   * @return the subvarAttrImportData
   */
  public Map<Long, Map<Long, ProjectImportAttr<PidcSubVariantAttribute>>> getSubvarAttrImportData() {
    return this.subvarAttrImportData;
  }


  /**
   * @param subvarAttrImportData the subvarAttrImportData to set
   */
  public void setSubvarAttrImportData(
      final Map<Long, Map<Long, ProjectImportAttr<PidcSubVariantAttribute>>> subvarAttrImportData) {
    this.subvarAttrImportData = subvarAttrImportData;
  }
}
