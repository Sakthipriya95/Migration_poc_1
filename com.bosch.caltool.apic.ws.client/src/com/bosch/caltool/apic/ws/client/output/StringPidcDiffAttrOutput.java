/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.output;

import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardChangedAttributeType;


/**
 * @author imi2si
 */
public class StringPidcDiffAttrOutput extends AbstractStringOutput {

  private final ProjectIdCardChangedAttributeType[] pidcs;

  public StringPidcDiffAttrOutput(final ProjectIdCardChangedAttributeType... pidcs) {
    super();
    this.pidcs = pidcs == null ? new ProjectIdCardChangedAttributeType[0] : pidcs;
  }

  @Override
  protected final boolean outputAvailable() {
    return this.pidcs.length > 0;
  }

  @Override
  protected final void createHeader() {
    append("Attribute Id");
    append("New Value ID");
    append("Old Value ID");
    append("New Value Clearing Status");
    append("Old Value Clearing Status");
    lineBreak();
  }

  @Override
  protected final void createRows() {
    for (ProjectIdCardChangedAttributeType pidc : this.pidcs) {
      append(pidc.getAttrID());
      append(pidc.isNewValueIDSpecified() ? pidc.getNewValueID() : null);
      append(pidc.isOldValueIDSpecified() ? pidc.getOldValueID() : null);
      append(pidc.getNewValueIdClearingStatus());
      append(pidc.getOldValueIdClearingStatus());
      lineBreak();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getOutput() {
    // TODO Auto-generated method stub
    return this.output.toString();
  }
}
