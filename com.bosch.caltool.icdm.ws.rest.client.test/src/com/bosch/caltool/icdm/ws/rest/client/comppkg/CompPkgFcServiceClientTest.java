package com.bosch.caltool.icdm.ws.rest.client.comppkg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.SortedSet;

import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.bosch.caltool.icdm.model.comppkg.CompPkgFc;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;


/**
 * Service Client Test for TCompPkgBcFc
 *
 * @author say8cob
 */
public class CompPkgFcServiceClientTest extends AbstractRestClientTest {

  private final static Long COMPPKGFC_ID = (long) 772090569;
  private final static Long COMP_BC_ID = (long) 772090568;
  /**
   * Expected exception
   */
  public final ExpectedException thrown = ExpectedException.none();

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.CompPkgFcServiceClientTest.client.comppkg.CompPkgBcFcServiceClientTest#getById()}
   */
  @Test
  public void testGetById() {
    LOG.info("=======================================================================================================");
    LOG.info(
        "TestGetById==============================================================================================");
    LOG.info("=======================================================================================================");
    CompPkgFcServiceClient servClient = new CompPkgFcServiceClient();
    try {
      CompPkgFc ret = servClient.getById(COMPPKGFC_ID);
      assertNotNull("CompPkg FC not null", ret);
      LOG.info("CompPkg FC Name " + ret.getName());
      assertEquals("CompPkg FC Name check", "App_ActrEner", ret.getName());
    }
    catch (Exception excep) {
      assertNull("Error in WS call", excep);
    }
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.CompPkgFcServiceClientTest.client.comppkg.CompPkgBcFcServiceClientTest#getByCompBcId()}
   */
  @Test
  public void testGetByCompBcId() {
    LOG.info("=======================================================================================================");
    LOG.info(
        " TestGetByCompBcId==============================================================================================");
    LOG.info("=======================================================================================================");
    CompPkgFcServiceClient servClient = new CompPkgFcServiceClient();
    try {
      SortedSet<CompPkgFc> retMap = servClient.getByCompBcId(COMP_BC_ID);
      assertFalse("CompPkg FC list not null or empty", (retMap == null) || retMap.isEmpty());
      LOG.info("CompPkg FC list size = " + retMap.size());
      LOG.info("First CompPkg FC = " + retMap.iterator().next().getFcName());
    }
    catch (Exception excep) {
      assertNull("Error in WS call", excep);
    }
  }

  /**
   * Common method to call Create ,Update and Delete testcases to avoid duplicate mapping of Fc to Bc's
   */
  @Test
  public void testCaseCreateUpdateDelete() {
    try {
      Long comppkgFcid = testCreate();
      if (comppkgFcid != null) {
        // testUpdate(comppkgFcid);
        testDelete(comppkgFcid);
      }
    }
    catch (Exception excep) {
      assertNull("Error in WS call", excep);
    }

  }

  private Long testCreate() {
    LOG.info("=======================================================================================================");
    LOG.info(
        "TestCreate==============================================================================================");
    LOG.info("=======================================================================================================");
    Long compPkgFcId = null;
    CompPkgFcServiceClient servClient = new CompPkgFcServiceClient();
    try {
      CompPkgFc obj = new CompPkgFc();
      obj.setCompBcId(COMP_BC_ID);
      obj.setFcName("ACCECU_Eng");
      obj.setVersion((long) 1);

      // Invoke create method
      CompPkgFc createdObj = servClient.create(obj);
      compPkgFcId = createdObj.getId();

      assertFalse("Response should not be null ", (createdObj == null));
      LOG.info("CompPkgFc - id = {}, name = {}", createdObj.getId(), createdObj.getName());
      assertNotNull("CompPkgFc id not empty", createdObj.getId());
    }
    catch (Exception excep) {
      assertNull("Error in WS call", excep);
    }
    return compPkgFcId;
  }

  private void testUpdate(final Long ComppkgFcId) {
    LOG.info("=======================================================================================================");
    LOG.info(
        "TestUpdate==============================================================================================");
    LOG.info("=======================================================================================================");
    CompPkgFcServiceClient servClient = new CompPkgFcServiceClient();
    try {
      CompPkgFc obj = new CompPkgFc();

      obj.setCompBcId(COMP_BC_ID);
      obj.setId((long) ComppkgFcId);
      obj.setFcName("ACCECU_Flex");
      obj.setVersion((long) 2);
      // Invoke update method
      CompPkgFc updatedObj = servClient.update(obj);
      LOG.info("updated fc id " + updatedObj.getId());
      assertFalse("Response should not be null ", (updatedObj == null));
      assertNotNull("CompPkgFc id not empty", updatedObj.getId());
    }
    catch (Exception excep) {
      assertNull("Error in WS call", excep);
    }
  }

  private void testDelete(final Long ComppkgFcId) {
    LOG.info("=======================================================================================================");
    LOG.info(
        "TestDelete==============================================================================================");
    LOG.info("=======================================================================================================");
    CompPkgFcServiceClient servClient = new CompPkgFcServiceClient();
    try {
      // Invoke delete method
      servClient.delete(servClient.getById(ComppkgFcId));
      LOG.info("deleted fc id " + ComppkgFcId);
      // If the previous delete method is successful, then getById call will throw exception
      // CompPkgFc deletedObj = servClient.getById(ComppkgFcId);
      // this.thrown.expect(ApicWebServiceException.class);
    }
    catch (Exception excep) {
      assertNull("Error in WS call", excep);
    }
  }


}
