package com.bosch.caltool.icdm.ws.rest.client.uc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.UCP_ATTR_MAPPING_FLAGS;
import com.bosch.caltool.icdm.model.uc.UcpAttr;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client Test for Ucp Attr
 *
 * @author EMS4KOR
 */
public class UcpAttrServiceClientTest extends AbstractRestClientTest {

  private final static long UCPATTR_ID = 654030;
  private final static long INVALID_UCPATTR_ID = -1L;


  /**
   * Test method for {@link UcpAttrServiceClient#getAll()}
   *
   * @throws ApicWebServiceException Webservice Error
   */

  @Test
  public void testGetAll() throws ApicWebServiceException {

    UcpAttrServiceClient servClient = new UcpAttrServiceClient();
    Map<Long, UcpAttr> retMap = servClient.getAll();
    assertFalse("Response should not be null or empty", ((retMap == null) || retMap.isEmpty()));
    UcpAttr ucpAttr = retMap.get(UCPATTR_ID);
    testOutput(ucpAttr);
  }

  /**
   * Test method for {@link UcpAttrServiceClient#getById(Long)}
   *
   * @throws ApicWebServiceException Webservice Error
   */
  @Test
  public void testGetById() throws ApicWebServiceException {

    UcpAttrServiceClient servClient = new UcpAttrServiceClient();
    UcpAttr ret = servClient.getById(UCPATTR_ID);
    assertNotNull("Response should not be null", ret);
    testOutput(ret);

  }

  /**
   * Test method for {@link UcpAttrServiceClient#getById(Long)} Negative Test Case
   *
   * @throws ApicWebServiceException Webservice Error
   */

  @Test
  public void testGetByIdNegative() throws ApicWebServiceException {
    UcpAttrServiceClient servClient = new UcpAttrServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Use Case Attribute with ID" + " '" + INVALID_UCPATTR_ID + "'" + " not found");
    UcpAttr ret = servClient.getById(INVALID_UCPATTR_ID);
    fail("Expected Exception not thrown");
    testOutput(ret);
  }


  /**
   * Test method for {@link UcpAttrServiceClient#create(List)}
   */
  @Test
  public void testCreateUpdateDelete() throws ApicWebServiceException {
    UcpAttrServiceClient servClient = new UcpAttrServiceClient();
    UcpAttr ucpAttr1 = new UcpAttr();
    ucpAttr1.setUseCaseId(772496716L);
    ucpAttr1.setSectionId(709550L);
    ucpAttr1.setAttrId(249272L);
    UcpAttr ucpAttr2 = new UcpAttr();
    ucpAttr2.setUseCaseId(772259515L);
    ucpAttr2.setSectionId(1530900642L);
    ucpAttr2.setAttrId(1495497768L);
    List<UcpAttr> ucpAttrList = new ArrayList<>();
    ucpAttrList.add(ucpAttr1);
    ucpAttrList.add(ucpAttr2);
    // Invoke create method
    Map<Long, UcpAttr> createdObjMap = servClient.create(ucpAttrList);
    // Validate
    assertNotNull("Object should not be null", createdObjMap);
    for (UcpAttr ucpAttr : createdObjMap.values()) {

      if (ucpAttr.getAttrId().equals(1495497768L)) {
        assertEquals("Value must be equal", Long.valueOf(1495497768), ucpAttr.getAttrId());
        break;
      }

    }
    Map<Long, UcpAttr> ucpAttrMapToUpdate = new HashMap<>();

    createdObjMap.values().stream().forEach(ucpAttr -> ucpAttrMapToUpdate.put(ucpAttr.getId(), ucpAttr));

    Long quotRelFlag = 0L;

    quotRelFlag = UCP_ATTR_MAPPING_FLAGS.QUOTATION_RELEVANT.setFlag(quotRelFlag);

    for (UcpAttr ucpAttr : ucpAttrMapToUpdate.values()) {
      ucpAttr.setMappingFlags(Long.parseLong(String.valueOf(quotRelFlag)));
    }

    Map<Long, UcpAttr> updatedMap = servClient.update(ucpAttrMapToUpdate);

    assertNotNull(updatedMap);

    for (UcpAttr attr : updatedMap.values()) {
      assertTrue(UCP_ATTR_MAPPING_FLAGS.QUOTATION_RELEVANT.isSet(attr.getMappingFlags()));
    }

    servClient.delete(new HashSet<>(updatedMap.values()));
  }


  // LOG.info("=======================================================================================================");
  // LOG.info("
  // TestCreate==============================================================================================");
  // LOG.info("=======================================================================================================");
  // UcpAttrServiceClient servClient = new UcpAttrServiceClient();
  // try {
  // UcpAttr obj = new UcpAttr();
  // obj.setId(<Enter your input here>);
  // obj.setUseCaseId(<Enter your input here>);
  // obj.setSectionId(<Enter your input here>);
  // obj.setAttrId(<Enter your input here>);
  // obj.setCreatedUser(<Enter your input here>);
  // obj.setCreatedDate(<Enter your input here>);
  // obj.setModifiedDate(<Enter your input here>);
  // obj.setModifiedUser(<Enter your input here>);
  // obj.setVersion(<Enter your input here>);
  //
  // //Invoke create method
  // UcpAttr createdObj = servClient.create(obj);
  // assertNotNull("object not null", createdObj);
  // testOutput(createdObj);
  // } catch (Exception excep) {
  // LOG.error("Error in WS call", excep);
  // assertNull("Error in WS call", excep);
  // }
  // }
  //
  // /**
  // * Test method for {@link com.bosch.caltool.icdm.rest.client.uc.UcpAttrServiceClientTest#update()}
  // *
  // */
  // @Test
//   public void testUpdate() {
  // LOG.info("=======================================================================================================");
  // LOG.info("
  // TestUpdate==============================================================================================");
  // LOG.info("=======================================================================================================");
//   UcpAttrServiceClient servClient = new UcpAttrServiceClient();
//   try {
//   UcpAttr obj = new UcpAttr();
//   obj.setId(<Enter your input here>);
//   obj.setUseCaseId(<Enter your input here>);
//   obj.setSectionId(<Enter your input here>);
//   obj.setAttrId(<Enter your input here>);
//   obj.setCreatedUser(<Enter your input here>);
//   obj.setCreatedDate(<Enter your input here>);
//   obj.setModifiedDate(<Enter your input here>);
//   obj.setModifiedUser(<Enter your input here>);
//   obj.setVersion(<Enter your input here>);
//
//   //Invoke update method
//   UcpAttr updatedObj = servClient.update(obj);
//   assertNotNull("object not null", updatedObj);
//   testOutput(updatedObj);
//   } catch (Exception excep) {
//   LOG.error("Error in WS call", excep);
//   assertNull("Error in WS call", excep);
//   }
//   }
  //
  // /**
  // * Test method for {@link com.bosch.caltool.icdm.rest.client.uc.UcpAttrServiceClientTest#delete()}
  // *
  // */
  // @Test
  // public void testDelete() {
  // LOG.info("=======================================================================================================");
  // LOG.info("
  // TestDelete==============================================================================================");
  // LOG.info("=======================================================================================================");
  // UcpAttrServiceClient servClient = new UcpAttrServiceClient();
  // try {
  // UcpAttr obj = new UcpAttr();
  // obj.setId(<Enter your input here>);
  // obj.setUseCaseId(<Enter your input here>);
  // obj.setSectionId(<Enter your input here>);
  // obj.setAttrId(<Enter your input here>);
  // obj.setCreatedUser(<Enter your input here>);
  // obj.setCreatedDate(<Enter your input here>);
  // obj.setModifiedDate(<Enter your input here>);
  // obj.setModifiedUser(<Enter your input here>);
  // obj.setVersion(<Enter your input here>);
  // UcpAttr createdObj = servClient.create(obj);
  //
  // //Invoke delete method
  // servClient.delete(createdObj.getId());
  //
  // //If the previous delete method is successful, then getById call will throw exception
  // thrown.expect(DataNotFoundException.class);
  // thrown.expectMessage(containsString("not found"));
  // servClient.getById(createdObj.getId());
  // } catch (Exception excep) {
  // LOG.error("Error in WS call", excep);
  // assertNull("Error in WS call", excep);
  // }
  // }

  /**
   * test output data
   */
  private void testOutput(final UcpAttr obj) {
    assertEquals("UseCaseId is equal", Long.valueOf(772496716), Long.valueOf(obj.getUseCaseId()));
    assertEquals("SectionId is equal", Long.valueOf(654018), Long.valueOf(obj.getSectionId()));
    assertEquals("AttrId is equal", Long.valueOf(249294), Long.valueOf(obj.getAttrId()));
    assertEquals("CreatedUser is equal", "GUK2SI", obj.getCreatedUser().toString());
    assertNotNull("CreatedDate is not null", obj.getCreatedDate());
  }

}
