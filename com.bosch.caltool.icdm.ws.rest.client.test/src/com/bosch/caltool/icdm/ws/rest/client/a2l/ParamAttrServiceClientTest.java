/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.bosch.caltool.icdm.model.a2l.ParameterAttribute;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Test class for {@link ParamAttrServiceClient}
 *
 * @author HNU1COB
 */
public class ParamAttrServiceClientTest extends AbstractRestClientTest {

  private static final Long ATTR_ID = 2225L;
  private static final Long PARAM_ID = 403905665L;


  /**
   * Test method for {@link ParamAttrServiceClient#create(ParameterAttribute)}. and
   * {@link ParamAttrServiceClient#delete(ParameterAttribute)}.
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testCreateParameterAttribute() throws ApicWebServiceException {

    ParamAttrServiceClient serviceClient = new ParamAttrServiceClient();
    ParameterAttribute paramAttr = new ParameterAttribute();
    paramAttr.setAttrId(ATTR_ID);
    paramAttr.setParamId(PARAM_ID);
    paramAttr.setVersion(1L);
    // create
    ParameterAttribute createdObj = serviceClient.create(paramAttr);
    // validate
    assertNotNull("Created Object not null", createdObj);
    assertEquals("AttrId is equal", ATTR_ID, createdObj.getAttrId());
    assertEquals("Param ID is equal", PARAM_ID, createdObj.getParamId());
    assertEquals("Version is equal", Long.valueOf(1L), createdObj.getVersion());
    LOG.info("PramAttr Id : {} ,Atttr Id : {} ,Param Id : {},Version : {}", createdObj.getId(), createdObj.getAttrId(),
        createdObj.getParamId(), createdObj.getVersion());
    // delete
    serviceClient.delete(createdObj);


  }

}
