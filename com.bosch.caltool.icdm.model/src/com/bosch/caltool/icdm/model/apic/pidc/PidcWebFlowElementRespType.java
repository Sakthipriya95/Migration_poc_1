/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.Set;

import com.bosch.caltool.icdm.model.apic.WebFlowAttribute;

/**
 * @author dmr1cob
 */
public class PidcWebFlowElementRespType {


  private PidcWebFlowElementSelectionType selectionType;

  private Set<PidcWebFlowElementDetailsType> detailsTypeSet;

  private Set<WebFlowAttribute> webflowAttrSet;


  /**
   * @return the slectionType
   */
  public PidcWebFlowElementSelectionType getSelectionType() {
    return this.selectionType;
  }


  /**
   * @param slectionType the slectionType to set
   */
  public void setSelectionType(final PidcWebFlowElementSelectionType slectionType) {
    this.selectionType = slectionType;
  }


  /**
   * @return the detailsTypeSet
   */
  public Set<PidcWebFlowElementDetailsType> getDetailsTypeSet() {
    return this.detailsTypeSet;
  }


  /**
   * @param detailsTypeSet the detailsTypeSet to set
   */
  public void setDetailsTypeSet(final Set<PidcWebFlowElementDetailsType> detailsTypeSet) {
    this.detailsTypeSet = detailsTypeSet;
  }


  /**
   * @return the webflowAttrSet
   */
  public Set<WebFlowAttribute> getWebflowAttrSet() {
    return this.webflowAttrSet;
  }


  /**
   * @param webflowAttrSet the webflowAttrSet to set
   */
  public void setWebflowAttrSet(final Set<WebFlowAttribute> webflowAttrSet) {
    this.webflowAttrSet = webflowAttrSet;
  }

}
