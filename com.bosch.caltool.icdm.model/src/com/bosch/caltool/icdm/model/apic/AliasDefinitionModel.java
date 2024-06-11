/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic;

import java.util.HashMap;
import java.util.Map;

import com.bosch.caltool.icdm.model.apic.AliasDef;
import com.bosch.caltool.icdm.model.apic.AliasDetail;

/**
 * Model for Alias Definition and details
 * @author pdh2cob
 */
public class AliasDefinitionModel {

  private AliasDef aliasDefnition;

  /**
   * Long - AliasDetail ID, AliasDetail object
   */
  private Map<Long, AliasDetail> aliasDetailsMap = new HashMap<>();


  /**
   * @return the aliasDefnition
   */
  public AliasDef getAliasDefnition() {
    return this.aliasDefnition;
  }


  /**
   * @param aliasDefnition the aliasDefnition to set
   */
  public void setAliasDefnition(final AliasDef aliasDefnition) {
    this.aliasDefnition = aliasDefnition;
  }


  /**
   * @return the aliasDetailsMap
   */
  public Map<Long, AliasDetail> getAliasDetailsMap() {
    return this.aliasDetailsMap;
  }


  /**
   * @param aliasDetailsMap the aliasDetailsMap to set
   */
  public void setAliasDetailsMap(final Map<Long, AliasDetail> aliasDetailsMap) {
    this.aliasDetailsMap = aliasDetailsMap;
  }


}
