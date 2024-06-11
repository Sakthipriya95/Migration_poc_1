/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.output;

import com.bosch.caltool.apic.ws.client.APICStub.AttributeValue;
import com.bosch.caltool.apic.ws.client.APICStub.ValueList;

/**
 * @author imi2si
 */
public class StringAttrValOutput extends AbstractStringOutput {

  private final ValueList[] values;

  /**
   * @param values ValueList
   */
  public StringAttrValOutput(final ValueList... values) {
    super();
    this.values = values.clone();
  }

  @Override
  protected final boolean outputAvailable() {
    return this.values.length > 0;
  }

  @Override
  protected final void createHeader() {
    append("Attribute ID");
    append("Attribute Name");
    append("Value Name");
    append("Clearing Status");
    append("Is Cleared");
    lineBreak();
  }

  @Override
  protected final void createRows() {
    for (ValueList valueList : this.values) {
      for (AttributeValue value : valueList.getValues()) {
        append(valueList.getAttribute().getId());
        append(valueList.getAttribute().getNameE());
        append(value.getValueE());
        append(value.getClearingStatus());
        append(value.getIsCleared());
        lineBreak();
      }
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
