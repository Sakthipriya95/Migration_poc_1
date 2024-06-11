/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;


/**
 * @author svj7cob
 */
// Task 263282
public class A2lCompliParameterInput {

  /**
   * A2l file in byte array form
   */
  private byte[] a2lContent;

  /**
   * given web flow-ID
   */
  private Long webFlowId;

  /**
   * @param a2lContent the a2lContent to set
   */
  public void setA2lContent(final byte[] a2lContent) {
    if (a2lContent != null) {
      this.a2lContent = a2lContent.clone();
    }
  }

  /**
   * @return the a2lContent
   */
  public byte[] getA2lContent() {
    return this.a2lContent == null ? null : this.a2lContent.clone();
  }

  /**
   * @return the webFlowId
   */
  public Long getWebFlowId() {
    return this.webFlowId;
  }

  /**
   * @param webFlowId the webFlowId to set
   */
  public void setWebFlowId(final Long webFlowId) {
    this.webFlowId = webFlowId;
  }


}
