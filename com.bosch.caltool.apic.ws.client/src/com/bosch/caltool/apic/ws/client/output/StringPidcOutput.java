/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.output;

import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardInfoType;


/**
 * @author imi2si
 */
public class StringPidcOutput extends AbstractStringOutput {

  private final ProjectIdCardInfoType[] pidcs;

  /**
   * @param pidcs ProjectIdCardInfoType
   */
  public StringPidcOutput(final ProjectIdCardInfoType... pidcs) {
    super();
    this.pidcs = pidcs.clone();
  }

  @Override
  protected final boolean outputAvailable() {
    return this.pidcs.length > 0;
  }

  @Override
  protected final void createHeader() {
    append("PIDC ID");
    append("PIDC Name");
    append("Clearing Status");
    append("Is Cleared");
    append("Create Date");
    append("Modify Date");
    lineBreak();
  }

  @Override
  protected final void createRows() {
    int rowCount = this.pidcs.length;
    if ((getNoOfRowsToInclude() >= 0) && (getNoOfRowsToInclude() < rowCount)) {
      rowCount = getNoOfRowsToInclude();
    }
    ProjectIdCardInfoType pidc;
    for (int idx = 0; idx < rowCount; idx++) {
      pidc = this.pidcs[idx];

      append(pidc.getId());
      append(pidc.getName());
      append(pidc.getClearingStatus());
      append(pidc.getIsCleared());
      append(pidc.getCreateDate());
      append(pidc.getModifyDate());
      lineBreak();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getOutput() {
    return this.output.toString();
  }
}
