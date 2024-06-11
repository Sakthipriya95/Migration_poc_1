/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.List;

/**
 * @author dmr1cob
 */
public class PidcVariantTypeV2 {

  private PidcVariantInfoTypeV2 pidcVariantInfoTypeV2;

  private List<AttributeWithValueTypeV2> attributeWithValueTypeV2List;

  private List<PidcVariantTypeV2> pidcSubVarTypeV2List;


  /**
   * @return the pidcVariantInfoTypeV2
   */
  public PidcVariantInfoTypeV2 getPidcVariantInfoTypeV2() {
    return this.pidcVariantInfoTypeV2;
  }


  /**
   * @param pidcVariantInfoTypeV2 the pidcVariantInfoTypeV2 to set
   */
  public void setPidcVariantInfoTypeV2(final PidcVariantInfoTypeV2 pidcVariantInfoTypeV2) {
    this.pidcVariantInfoTypeV2 = pidcVariantInfoTypeV2;
  }


  /**
   * @return the attributeWithValueTypeV2List
   */
  public List<AttributeWithValueTypeV2> getAttributeWithValueTypeV2List() {
    return this.attributeWithValueTypeV2List;
  }


  /**
   * @param attributeWithValueTypeV2List the attributeWithValueTypeV2List to set
   */
  public void setAttributeWithValueTypeV2List(final List<AttributeWithValueTypeV2> attributeWithValueTypeV2List) {
    this.attributeWithValueTypeV2List = attributeWithValueTypeV2List;
  }


  /**
   * @return the pidcVarTypeV2List
   */
  public List<PidcVariantTypeV2> getPidcSubVarTypeV2List() {
    return this.pidcSubVarTypeV2List;
  }


  /**
   * @param pidcVarTypeV2List the pidcVarTypeV2List to set
   */
  public void setPidcSubVarTypeV2List(final List<PidcVariantTypeV2> pidcVarTypeV2List) {
    this.pidcSubVarTypeV2List = pidcVarTypeV2List;
  }


}