/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.output;

import com.bosch.caltool.apic.ws.client.APICStub.GetPidcDiffsResponseType;


/**
 * @author imi2si
 */
public class StringPidcDiffOutput extends AbstractStringOutput {

  private final GetPidcDiffsResponseType[] pidcs;

  public StringPidcDiffOutput(final GetPidcDiffsResponseType... pidcs) {
    super();
    this.pidcs = pidcs;
  }

  @Override
  protected final boolean outputAvailable() {
    return this.pidcs.length > 0;
  }

  @Override
  protected final void createHeader() {
    append("PIDC ID");
    lineBreak();
  }

  @Override
  protected final void createRows() {
    for (GetPidcDiffsResponseType pidc : this.pidcs) {
      append(pidc.getPidcID());
      lineBreak();
      append(new StringPidcDiffAttrOutput(pidc.getChangedAttributes()));
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
