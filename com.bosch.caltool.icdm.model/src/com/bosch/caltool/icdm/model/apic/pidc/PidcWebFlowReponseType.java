/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.Set;
import java.util.TreeSet;

import com.bosch.caltool.icdm.model.apic.WebFlowAttribute;

/**
 * @author dmr1cob
 */
public class PidcWebFlowReponseType {

  private PidcDetailsType pidcDetailsType;

  private Set<WebFlowAttribute> webFlowAttr = new TreeSet<>();


  /**
   * @return the pidcDetailsType
   */
  public PidcDetailsType getPidcDetailsType() {
    return this.pidcDetailsType;
  }


  /**
   * @param pidcDetailsType the pidcDetailsType to set
   */
  public void setPidcDetailsType(final PidcDetailsType pidcDetailsType) {
    this.pidcDetailsType = pidcDetailsType;
  }


  /**
   * @return the webFlowAttr
   */
  public Set<WebFlowAttribute> getWebFlowAttr() {
    return this.webFlowAttr;
  }


  /**
   * @param webFlowAttr the webFlowAttr to set
   */
  public void setWebFlowAttr(final Set<WebFlowAttribute> webFlowAttr) {
    this.webFlowAttr = webFlowAttr;
  }

}
