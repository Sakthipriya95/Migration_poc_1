/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.serviceclient;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.bosch.caltool.apic.ws.client.APICStub.Attribute;
import com.bosch.caltool.apic.ws.client.APICStub.AttributeValue;
import com.bosch.caltool.apic.ws.client.APICStub.ValueList;
import com.bosch.caltool.apic.ws.client.output.AbstractStringOutput;
import com.bosch.caltool.apic.ws.client.output.StringAttrValOutput;


/**
 * @author ELM1COB
 */
public class TestAttrValues extends AbstractSoapClientTest {

  private final APICWebServiceClient stub = new APICWebServiceClient();


  /**
   * Test Text attribute value service
   *
   * @throws Exception service exception
   */
  @Test
  public void testGetAttrValuesText() throws Exception {

    long[] attrIDs = new long[1];

    // text attribute : SCR - DENOX Version (Type)
    attrIDs[0] = 1601L;

    ValueList[] valRetArr = this.stub.getAttrValues(attrIDs);
    assertNotNull("TEXT attribute ret not null", valRetArr);
    assertTrue("Check TEXT value response", (valRetArr.length == 1) && (valRetArr[0].getValues().length > 0));

    AbstractStringOutput output = new StringAttrValOutput(valRetArr);
    showOutput(output);

  }

  /**
   * Test Number attribute value service
   *
   * @throws Exception service exception
   */
  @Test
  public void testGetAttrValuesNumber() throws Exception {
    long[] attrIDs = new long[1];

    // number attribute : Balancer Shafts (No.)
    attrIDs[0] = 726;

    ValueList[] valRetArr = this.stub.getAttrValues(attrIDs);
    assertNotNull("NUMBER Attribute Ret not null", valRetArr);
    assertTrue("Check NUMBER value response", (valRetArr.length == 1) && (valRetArr[0].getValues().length > 0));
    ValueList firstRetItem = valRetArr[0];
    LOG.info("NUMBER Attribute : {} - First value : {}", firstRetItem.getAttribute().getNameE(),
        firstRetItem.getValues()[0].getValueE());
  }

  /**
   * Test Date attribute value service
   *
   * @throws Exception service exception
   */
  @Test
  public void testGetAttrValuesDate() throws Exception {
    long[] attrIDs = new long[1];

    // date attribute : SOP (1.)
    attrIDs[0] = 399;

    ValueList[] valRetArr = this.stub.getAttrValues(attrIDs);
    assertNotNull("DATE Attribute Ret not null", valRetArr);
    assertTrue("Check DATE value response", (valRetArr.length == 1) && (valRetArr[0].getValues().length > 0));
    ValueList firstRetItem = valRetArr[0];
    LOG.info("DATE Attribute : {} - First value : {}", firstRetItem.getAttribute().getNameE(),
        firstRetItem.getValues()[0].getValueE());
  }

  /**
   * Test Boolean attribute value service
   *
   * @throws Exception service exception
   */
  @Test
  public void testGetAttrValuesBoolean() throws Exception {
    long[] attrIDs = new long[1];

    // boolean attribute : Knock Sensor - used
    attrIDs[0] = 1935;

    ValueList[] valRetArr = this.stub.getAttrValues(attrIDs);
    assertNotNull("BOOLEAN Attribute Ret not null", valRetArr);
    assertTrue("Check BOOLEAN value response", (valRetArr.length == 1) && (valRetArr[0].getValues().length > 0));
    ValueList firstRetItem = valRetArr[0];
    LOG.info("BOOLEAN Attribute : {} - First value : {}", firstRetItem.getAttribute().getNameE(),
        firstRetItem.getValues()[0].getValueE());

  }


  /**
   * Get all values
   *
   * @throws Exception error from service
   */
  @Test
  public void testAttrValuesAll() throws Exception {


    Attribute[] attrs = this.stub.getAllAttributes();

    assertNotNull(attrs);

    long[] ids = new long[attrs.length];

    for (int i = 0; i < attrs.length; i++) {
      ids[i] = attrs[i].getId();
    }


    ValueList[] attvals = this.stub.getAttrValues(ids);
    assertNotNull(attvals);

    int counter1 = 0;
    for (ValueList valueList : attvals) {
      if (counter1 > 10) {
        // Print only first few items
        break;
      }
      LOG.info("Attribute name : {}", valueList.getAttribute().getNameE());

      int counter2 = 0;
      for (AttributeValue attrVal : valueList.getValues()) {
        if (counter2 > 2) {
          // Print only first few items
          break;
        }
        LOG.info("  {} --- {}", attrVal.getAttrID(), attrVal.getValueE());
        counter2++;
      }


      counter1++;

    }

  }

}
