/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.usecase.output;

import com.bosch.caltool.apic.ws.client.APICStub.UseCaseType;
import com.bosch.caltool.apic.ws.client.output.AbstractStringOutput;


/**
 * @author imi2si
 */
public class StringUseCaseOutput extends AbstractStringOutput {

  private final UseCaseType[] usecases;

  public StringUseCaseOutput(final UseCaseType... usecases) {
    super();
    this.usecases = usecases;
  }

  @Override
  protected final boolean outputAvailable() {
    return this.usecases.length > 0;
  }

  @Override
  protected final void createHeader() {
    append("Use Case ID");
    append("Use Case Name");
    append("Use Case Description");
    append("Use Case Group");
    append("Is Deleted");
    append("Created Date");
    append("Created User");
    append("Modify Date");
    append("Modify User");
    lineBreak();
  }

  @Override
  protected final void createRows() {
    for (UseCaseType entry : this.usecases) {
      append(entry.getUseCaseID());
      append(entry.getNameE());
      append(entry.getDescrE());
      append(entry.getUseCaseGroup());
      append(entry.getIsDeleted());
      append(entry.getCreateDate());
      append(entry.getCreateUser());
      append(entry.getModifyDate());
      append(entry.getModifyUser());
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
