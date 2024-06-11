/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dmr1cob
 */
public class PidcTypeV2 {

  private PidcInfoTypeV2 pidcInfoTypeV2;

  private List<AttributeWithValueTypeV2> attributeWithValueTypeV2List;

  private List<PidcVariantTypeV2> pidcVariantTypeV2List;


  /**
   * @return the pidcInfoTypeV2
   */
  public PidcInfoTypeV2 getPidcInfoTypeV2() {
    return this.pidcInfoTypeV2;
  }


  /**
   * @param pidcInfoTypeV2 the pidcInfoTypeV2 to set
   */
  public void setPidcInfoTypeV2(final PidcInfoTypeV2 pidcInfoTypeV2) {
    this.pidcInfoTypeV2 = pidcInfoTypeV2;
  }


  /**
   * @return the attributeWithValueTypeV2List
   */
  public List<AttributeWithValueTypeV2> getAttributeWithValueTypeV2List() {
    return this.attributeWithValueTypeV2List == null ? null : new ArrayList<>(this.attributeWithValueTypeV2List);
  }


  /**
   * @param attributeWithValueTypeV2List the attributeWithValueTypeV2List to set
   */
  public void setAttributeWithValueTypeV2List(final List<AttributeWithValueTypeV2> attributeWithValueTypeV2List) {
    this.attributeWithValueTypeV2List =
        attributeWithValueTypeV2List == null ? null : new ArrayList<>(attributeWithValueTypeV2List);
  }


  /**
   * @return the pidcVariantTypeV2List
   */
  public List<PidcVariantTypeV2> getPidcVariantTypeV2List() {
    return this.pidcVariantTypeV2List;
  }


  /**
   * @param pidcVariantTypeV2List the pidcVariantTypeV2List to set
   */
  public void setPidcVariantTypeV2List(final List<PidcVariantTypeV2> pidcVariantTypeV2List) {
    this.pidcVariantTypeV2List = pidcVariantTypeV2List;
  }

}
