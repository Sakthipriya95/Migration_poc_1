/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswerOpl;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author apj4cob
 */
public class RvwQnaireAnswerOplServiceClientTest extends AbstractRestClientTest {

  private static final Long RWS_QP_OBJ_ID = 1460193989L;

  /**
   * Testcase create.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCreateUpdateDelete() throws ApicWebServiceException {

    RvwQnaireAnswerOplServiceClient servClient = new RvwQnaireAnswerOplServiceClient();
    RvwQnaireAnswerOpl obj = new RvwQnaireAnswerOpl();
    obj.setMeasure("Junit");
    obj.setResult("J");
    obj.setCompletionDate(null);
    obj.setOpenPoints("Junit");
    obj.setResponsible(1214625116l);
    obj.setRvwAnswerId(1453288880l);
    List<RvwQnaireAnswerOpl> listObj = new ArrayList<>();
    listObj.add(obj);
    // create
    List<RvwQnaireAnswerOpl> createdList = servClient.create(listObj);
    assertNotNull("object not null", createdList);
    RvwQnaireAnswerOpl createdObj = createdList.iterator().next();
    // validate
    assertEquals("Measure is equal", "Junit", createdObj.getMeasure());
    assertEquals("Result is equal", "J", createdObj.getResult());
    assertNull("Completion date not null", createdObj.getCompletionDate());
    assertEquals("Open points is equal", "Junit", createdObj.getOpenPoints());
    assertEquals("Responsible is equal", Long.valueOf(1214625116l), createdObj.getResponsible());
    assertEquals("Rvw answer id is equal", Long.valueOf(1453288880l), createdObj.getRvwAnswerId());
    assertEquals("Version is equal", Long.valueOf(1), createdObj.getVersion());
    // update
    createdList.remove(createdObj);
    createdObj.setMeasure("Junit_Updated");
    createdObj.setOpenPoints("Junit_Updated");
    createdList.add(createdObj);
    List<RvwQnaireAnswerOpl> updatedList = servClient.update(createdList);
    assertNotNull("object not null", updatedList);
    // validate
    RvwQnaireAnswerOpl updatedObject = updatedList.iterator().next();
    assertEquals("Measure is equal", "Junit_Updated", updatedObject.getMeasure());
    assertEquals("Open points is equal", "Junit_Updated", updatedObject.getOpenPoints());
    assertEquals("Version is equal", Long.valueOf(createdObj.getVersion() + 1), updatedObject.getVersion());
    List<Long> toBeDeleted = new ArrayList<>();
    toBeDeleted.add(updatedObject.getId());
    // delete
    servClient.delete(toBeDeleted);

  }

  /**
   * Testcase getById.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetById() throws ApicWebServiceException {
    RvwQnaireAnswerOplServiceClient servClient = new RvwQnaireAnswerOplServiceClient();
    RvwQnaireAnswerOpl rvwQnaireAnswerOpl = servClient.getById(RWS_QP_OBJ_ID);
    assertNotNull(rvwQnaireAnswerOpl);
  }


  /**
   * Testcase getByIdInvalid.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetByIdInvalid() throws ApicWebServiceException {
    RvwQnaireAnswerOplServiceClient servClient = new RvwQnaireAnswerOplServiceClient();
    this.thrown.expectMessage("Review Questionnaire Answer OPL with ID '" + 14 + "' not found");
    servClient.getById(14l);
    fail("Expected exception not thrown");
  }


  /**
   * Testcase getByIdNull.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetByIdNull() throws ApicWebServiceException {
    RvwQnaireAnswerOplServiceClient servClient = new RvwQnaireAnswerOplServiceClient();
    this.thrown.expectMessage("Review Questionnaire Answer OPL with ID '" + null + "' not found");
    servClient.getById(null);
    fail("Expected exception not thrown");
  }


}
