/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.output;

import com.bosch.caltool.apic.ws.client.APICStub.Attribute;

/**
 * @author imi2si
 */
public class StringAttributesOutput extends AbstractStringOutput {

  private final Attribute[] attributes;

  /**
   * @param attributes Attribute
   */
  public StringAttributesOutput(final Attribute... attributes) {
    super();
    this.attributes = attributes.clone();
  }

  @Override
  protected final boolean outputAvailable() {
    return this.attributes.length > 0;
  }

  @Override
  protected final void createHeader() {
    append("Attribute ID");
    append("Name");
    append("Description");
    append("Unit");
    append("Deleted");
    lineBreak();
  }

  @Override
  protected final void createRows() {
    int rowCount = this.attributes.length;
    if ((getNoOfRowsToInclude() >= 0) && (getNoOfRowsToInclude() < this.attributes.length)) {
      rowCount = getNoOfRowsToInclude();
    }
    Attribute attribute;
    for (int idx = 0; idx < rowCount; idx++) {
      attribute = this.attributes[idx];

      append(attribute.getId());
      append(attribute.getNameE());
      append(attribute.getDescrE());
      append(attribute.getUnit());
      append(attribute.getIsDeleted());
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
