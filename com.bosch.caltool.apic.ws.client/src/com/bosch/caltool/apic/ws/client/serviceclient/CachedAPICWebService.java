/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.serviceclient;

import java.util.HashMap;
import java.util.Map;

import com.bosch.caltool.apic.ws.client.APICStub.Attribute;
import com.bosch.caltool.apic.ws.client.APICStub.AttributeValue;
import com.bosch.caltool.apic.ws.client.APICStub.ValueList;


/**
 * @author imi2si
 */
public class CachedAPICWebService extends APICWebServiceClient {

  Map<Long, Attribute> allAttributes = new HashMap<>();
  Map<Long, Map<Long, AttributeValue>> allAttributeValues = new HashMap<>();

  /**
   * @param apicWsServer
   */
  public CachedAPICWebService(final APICWsServer apicWsServer) {
    super(apicWsServer);
  }

  public void flushCash() {
    this.allAttributes.clear();
    this.allAttributeValues.clear();
  }

  @Override
  public Attribute getAttributeByID(final long attributeId) throws Exception {
    getAllAttributes();

    return this.allAttributes.get(attributeId);
  }

  @Override
  public AttributeValue getAttributeValueByID(final long attributeId, final long valueId) throws Exception {
    if (this.allAttributeValues.isEmpty()) {


      for (Attribute attribute : getAllAttributes()) {

        Map<Long, AttributeValue> attrValues = new HashMap<>();

        for (ValueList valueList : getAttrValues(new long[] { attribute.getId() })) {
          for (final AttributeValue value : valueList.getValues()) {
            // Inserts the Attr-ID as Key, and a Hashmap with Key Value-ID and Value AttributeValue
            attrValues.put(value.getValueID(), value);
          }
        }
        this.allAttributeValues.put(attribute.getId(), attrValues);
      }
    }

    return this.allAttributeValues.get(attributeId).get(valueId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Attribute[] getAllAttributes() throws Exception {

    if (this.allAttributes.isEmpty()) {
      Attribute[] attributes = super.getAllAttributes();

      for (Attribute attribute : attributes) {
        this.allAttributes.put(attribute.getId(), attribute);
      }
    }

    return this.allAttributes.values().toArray(new Attribute[0]);
  }
}
