/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.pidcsearch;

import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.apic.ws.client.APICStub.PidcSearchConditionType;


/**
 * @author imi2si
 */
public class PidcSearchCondition extends PidcSearchConditionType {

  /**
   * Value set
   */
  private final Set<Long> valueSet = new HashSet<>();

  /**
   * @param attr Attribute
   */
  public PidcSearchCondition(final long attrId) {
    super.setAttributeId(attrId);
  }

  /**
   * @return the attribute
   */
  public long getAttribute() {
    return super.getAttributeId();
  }


  /**
   * @return the valueSet
   */
  public Set<Long> getValueSet() {
    return this.valueSet;
  }

  /**
   * @param value AttributeValue
   */
  public void addValue(final long value) {
    this.valueSet.add(value);
    super.setAttributeValueIds(valueSetToArray());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("PidcSearchCondition [attribute=").append(super.getAttributeId()).append(", valueSet=")
        .append(this.valueSet).append(", usedFlag=").append(super.getUsedFlag()).append("]");
    return builder.toString();
  }

  private long[] valueSetToArray() {
    long values[] = new long[this.valueSet.size()];
    int index = 0;

    for (Long value : this.valueSet) {
      values[index] = value.longValue();
      index++;
    }

    return values;
  }
}
