package com.bosch.caltool.icdm.ws.rest.client.comppkg;

import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.bosch.caltool.icdm.model.comppkg.CompPkgBc;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client Test for TCompPkgBc
 *
 * @author say8cob
 */
public class CompPkgBcServiceClientTest extends AbstractRestClientTest {

  private final static Long COMPPKGBC_ID = (long) 772090568;
  /**
   * Expected exception
   */
  public final ExpectedException thrown = ExpectedException.none();

  // /**
  // * Test method for {@link com.bosch.caltool.icdm.rest.client.comppkg.CompPkgBcServiceClientTest#getAll()}
  // *
  // */
  // @Test
  // public void testGetAll() {
  // LOG.info("=======================================================================================================");
  // LOG.info("
  // TestGetAll==============================================================================================");
  // LOG.info("=======================================================================================================");
  // CompPkgBcServiceClient servClient = new CompPkgBcServiceClient();
  // try {
  // Map<Long,CompPkgBc> retMap = servClient.getAll();
  // for (CompPkgBc comp : retMap.values()) {
  // LOG.info("ComponentBC Ids = " + comp.getCompPkgId() +" "+comp.getBcName());
  // }
  //
  // // assertFalse("Response should not be null or empty", (retMap == null || retMap.isEmpty()));
  // // testOutput((retMap.values().iterator().next()));
  // } catch (Exception excep) {
  // LOG.error("Error in WS call", excep);
  // // assertNull("Error in WS call", excep);
  // }
  // }

  // /**
  // * Test method for {@link com.bosch.caltool.icdm.rest.client.comppkg.CompPkgBcServiceClientTest#getById()}
  // *
  // */
  // @Test
  // public void testGetById() {
  // LOG.info("=======================================================================================================");
  // LOG.info("
  // TestGetById==============================================================================================");
  // LOG.info("=======================================================================================================");
  // CompPkgBcServiceClient servClient = new CompPkgBcServiceClient();
  // try {
  // SortedSet<CompPkgBc> ret = servClient.getById(COMPPKGBC_ID);
  // for (CompPkgBc comp : ret) {
  // LOG.info("ComponentBC Ids = " + comp.getCompPkgId() +" "+comp.getBcName());
  // }
  // //testOutput(ret);
  // } catch (Exception excep) {
  // LOG.error("Error in WS call", excep);
  // }
  // }

  // /**
  // * Test method for {@link com.bosch.caltool.icdm.rest.client.comppkg.CompPkgBcServiceClientTest#getById()}
  // *
  // */
  // @Test
  // public void testGetBCFCById() {
  // LOG.info("=======================================================================================================");
  // LOG.info("
  // TestGetById==============================================================================================");
  // LOG.info("=======================================================================================================");
  // CompPkgBcServiceClient servClient = new CompPkgBcServiceClient();
  // try {
  // CompPkgBcFc ret = servClient.getBCFCById(COMPPKGBC_ID);
  // for (CompPkgBc bcSet : ret.getBcSet()) {
  // LOG.info("BC Info " + bcSet.getBcName());
  //
  // for (CompPkgFc fcObj : ret.getFcMap().get(bcSet.getId())) {
  //
  // LOG.info("FC Name Info " +fcObj.getFcName());
  //
  // }
  // }
  //
  // } catch (Exception excep) {
  // LOG.error("Error in WS call", excep);
  // }
  // }

  // /**
  // * Test method for {@link com.bosch.caltool.icdm.rest.client.comppkg.CompPkgBcServiceClientTest#getById()}
  // *
  // */
  // @Test
  // public void testGetByIdasMap() {
  // LOG.info("=======================================================================================================");
  // LOG.info("
  // TestGetById==============================================================================================");
  // LOG.info("=======================================================================================================");
  // CompPkgBcServiceClient servClient = new CompPkgBcServiceClient();
  // try {
  // Map<CompPkgBc, SortedSet<CompPkgFc>> ret = servClient.getBCFCMapById(COMPPKGBC_ID);
  // for (CompPkgBc comp : ret.keySet()) {
  // LOG.info("ComponentBC Ids = " + comp.getCompPkgId() +" "+comp.getBcName());
  // for (CompPkgFc compFC : ret.get(comp)) {
  // LOG.info("ComponentFC Ids = " + compFC.getFcName() +" "+compFC.getId()+" "+compFC.getCompBcId());
  // }
  // }
  // //testOutput(ret);
  // } catch (Exception excep) {
  // LOG.error("Error in WS call", excep);
  // }
  // }
  //
  // /**
  // * Test method for {@link com.bosch.caltool.icdm.rest.client.comppkg.CompPkgBcServiceClientTest#create()}
  // *
  // */
  // @Test
  // public void testCreate() {
  // LOG.info("=======================================================================================================");
  // LOG.info("
  // TestCreate==============================================================================================");
  // LOG.info("=======================================================================================================");
  // CompPkgBcServiceClient servClient = new CompPkgBcServiceClient();
  // try {
  // CompPkgBc obj = new CompPkgBc();
  // obj.setCompPkgId(COMPPKGBC_ID);
  // obj.setBcName("GEVCtl");
  // obj.setBcSeqNo((long)6);
  // obj.setCreatedUser("SAY8COB");
  // obj.setCreatedDate(ApicUtil.getCurrentTime().getCalendarType());
  // obj.setModifiedUser("SAY8COB");
  // obj.setModifiedDate(ApicUtil.getCurrentTime().getCalendarType());
  // obj.setVersion((long)1);
  //
  // //Invoke create method
  // CompPkgBc createdObj = servClient.create(obj);
  // LOG.debug(createdObj.getId().toString());
  //// assertNotNull("object not null", createdObj);
  //// testOutput(createdObj);
  // } catch (Exception excep) {
  // LOG.error("Error in WS call", excep);
  //// assertNull("Error in WS call", excep);
  // }
  // }

  /**
   * Test method for {@link com.bosch.caltool.icdm.rest.client.comppkg.CompPkgBcServiceClientTest#update()}
   */
  // @Test
  // public void testUpdate() {
  // LOG.info("=======================================================================================================");
  // LOG.info("
  // TestUpdate==============================================================================================");
  // LOG.info("=======================================================================================================");
  // CompPkgBcServiceClient servClient = new CompPkgBcServiceClient();
  // try {
  // CompPkgBc obj = new CompPkgBc();
  // obj.setId((long)897683168);
  // obj.setCompPkgId(COMPPKGBC_ID);
  // obj.setBcName("GEVCtl");
  // obj.setBcSeqNo((long)8);
  // obj.setVersion((long)3);
  //
  // //Invoke update method
  // CompPkgBc updatedObj = servClient.update(obj);
  // LOG.debug(updatedObj.getId().toString());
  // } catch (Exception excep) {
  // LOG.error("Error in WS call", excep);
  // }
  // }
  //
  /**
   * Test method for {@link com.bosch.caltool.icdm.rest.client.comppkg.CompPkgBcServiceClientTest#delete()}
   */
  @Test
  public void testDelete() {
    LOG.info("=======================================================================================================");
    LOG.info(
        " TestDelete==============================================================================================");
    LOG.info("=======================================================================================================");
    CompPkgBcServiceClient servClient = new CompPkgBcServiceClient();
    try {
      CompPkgBc obj = new CompPkgBc();
      obj.setCompPkgId(772090566l);
      obj.setId(COMPPKGBC_ID);
      obj.setBcName("HEGO");
      obj.setBcSeqNo((long) 6);
      obj.setVersion((long) 1);

      CompPkgBc createdObj = servClient.create(obj);

      // Invoke delete method
      servClient.delete(createdObj);

      // If the previous delete method is successful, then getById call will throw exception
      this.thrown.expect(ApicWebServiceException.class);
      // thrown.expectMessage(containsString("not found"));
      servClient.getBCByCompId(createdObj.getId());
    }
    catch (Exception excep) {
      LOG.error("Error in WS call", excep);
      assertNull("Error in WS call", excep);
    }
  }
  //
  // /**
  // * test output data
  // *
  // */
  // private void testOutput(final CompPkgBc obj) {
  //// assertEquals("CompPkgId is equal", obj.getCompPkgId(), <Enter your input here>);
  //// assertEquals("BcName is equal", obj.getBcName(), <Enter your input here>);
  //// assertEquals("BcSeqNo is equal", obj.getBcSeqNo(), <Enter your input here>);
  //// assertEquals("CreatedUser is equal", obj.getCreatedUser(), <Enter your input here>);
  //// assertNotNull("CreatedDate is not null", obj.getCreatedDate());
  // }

}
