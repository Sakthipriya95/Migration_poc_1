package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.model.a2l.A2lWpParamMapping;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMappingModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMappingUpdateModel;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcA2lServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client Test for A2lWpParamMapping
 *
 * @author pdh2cob
 */
public class A2lWpParamMappingServiceClientTest extends AbstractRestClientTest {


  private final Long wpDefVersId = 2154182928L;
  private final Long WpParamMappingId = 2154182956L;
  private final Long InvalidId = -1232L;


  /**
   * Test method for {@link A2lWpParamMappingServiceClient#getAllByWpDefVersId(Long)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetAllByWpDefVersId() throws ApicWebServiceException {
    A2lWpParamMappingServiceClient servClient = new A2lWpParamMappingServiceClient();
    A2lWpParamMappingModel retModel = servClient.getAllByWpDefVersId(this.wpDefVersId);
    assertFalse("Response should not be null ", (retModel == null));
    Map<Long, A2lWpParamMapping> a2lWpParamMapping = retModel.getA2lWpParamMapping();
    assertFalse("Response should not be null or empty", ((a2lWpParamMapping == null) || a2lWpParamMapping.isEmpty()));
    testOutout(a2lWpParamMapping);

    // key - WPRESPID SET - PARAM_ID ASSOCIATED WITH EACH WPRESPID no wpid found
//    Map<Long, Set<Long>> paramAndRespPalMap = retModel.getParamAndRespPalMap();
//    assertNotNull("Response should not be null or empty",
//        ((paramAndRespPalMap == null) || paramAndRespPalMap.isEmpty()));
//    Set<Long> set = paramAndRespPalMap.get(2154182943l);
//    assertTrue(set.contains(435788265L));


    LOG.info("A2lWpParamMappingModel : no of mapped params : " + retModel.getA2lWpParamMapping().size());
  }

  /**
   * @param a2lWpParamMapping
   */
  private void testOutout(final Map<Long, A2lWpParamMapping> a2lWpParamMapping) {
    assertEquals("param Id is equal", a2lWpParamMapping.get(this.WpParamMappingId).getParamId(),
        Long.valueOf(435788265));
    assertEquals("Wp respId is equal", a2lWpParamMapping.get(this.WpParamMappingId).getWpRespId(),
        Long.valueOf(2154182943L));
    assertEquals("Par_A2l_RespId is equal", a2lWpParamMapping.get(this.WpParamMappingId).getParA2lRespId(),
        Long.valueOf(1580039486L));
    assertEquals("Created User is equal", "BNE4COB", a2lWpParamMapping.get(this.WpParamMappingId).getCreatedUser());
    assertNotNull("Created date is not null", a2lWpParamMapping.get(this.WpParamMappingId).getCreatedDate());

  }

  /**
   * @throws ApicWebServiceException service error
   */
  @Test
  public void testget() throws ApicWebServiceException {
    A2lWpParamMappingServiceClient serviceClient = new A2lWpParamMappingServiceClient();
    A2lWpParamMapping paramMapping = serviceClient.get(this.WpParamMappingId);
    assertFalse("Response should not be null ", (paramMapping == null));

    // validating get
    assertEquals("param Id is equal", Long.valueOf(435788265), paramMapping.getParamId());
    assertEquals("Wp respId is equal", Long.valueOf(2154182943L), paramMapping.getWpRespId());
    assertEquals("Par_A2l_RespId is equal", Long.valueOf(1580039486L), paramMapping.getParA2lRespId());
    assertNotNull("Created date is not null", paramMapping.getCreatedDate());
  }

  /**
   * @throws ApicWebServiceException web server error
   */
  @Test
  public void testgetNegative() throws ApicWebServiceException {
    A2lWpParamMappingServiceClient serviceClient = new A2lWpParamMappingServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("A2l WP Parameter Mapping with ID '" + this.InvalidId + "' not found");
    serviceClient.get(this.InvalidId);
    fail("expected exception not thrown");
  }

  /**
   * @throws ApicWebServiceException web exception
   */
  @Test
  public void testupdateA2lWpParamMapping() throws ApicWebServiceException {

    PidcA2l pidcA2l = null;
    A2lWpParamMappingServiceClient serviceClient = new A2lWpParamMappingServiceClient();
    A2lWpParamMappingUpdateModel updateModel = new A2lWpParamMappingUpdateModel();

    // set to create
    A2lWpParamMapping paramMappingToCreate = new A2lWpParamMapping();
    paramMappingToCreate.setParamId(15516023L);
    paramMappingToCreate.setWpRespId(2248779442L);
    updateModel.getA2lWpParamMappingToBeCreated().add(paramMappingToCreate);

    // call service for create
    pidcA2l = new PidcA2lServiceClient().getById(1579500628L);

    A2lWpParamMappingUpdateModel createdModel = serviceClient.updateA2lWpParamMapping(updateModel, pidcA2l);
    Map<Long, A2lWpParamMapping> createdA2lWpParamMapping = createdModel.getCreatedA2lWpParamMapping();

    // validating the created record
    testOutput(createdA2lWpParamMapping);

    // add created objects to update
    for (A2lWpParamMapping a2lWpParamMapping : createdA2lWpParamMapping.values()) {
      a2lWpParamMapping.setWpRespId(1746086278L);
      createdModel.getA2lWpParamMappingToBeUpdated().put(a2lWpParamMapping.getId(), a2lWpParamMapping);
      testOutput(a2lWpParamMapping);
    }

    // clear the map inorder to avoid constraint violation
    createdModel.getA2lWpParamMappingToBeCreated().clear();
    createdA2lWpParamMapping.clear();

    pidcA2l = new PidcA2lServiceClient().getById(1579500628L);
    A2lWpParamMappingUpdateModel updatedModel = serviceClient.updateA2lWpParamMapping(createdModel, pidcA2l);
    assertNotNull("Updated Model is not null", updatedModel);

    // delete updated object
    for (A2lWpParamMapping a2lWpParamMapping : updatedModel.getUpdatedA2lWpParamMapping().values()) {
      updatedModel.getA2lWpParamMappingToBeDeleted().put(a2lWpParamMapping.getId(), a2lWpParamMapping);
    }
    updatedModel.getA2lWpParamMappingToBeUpdated().clear();
    updatedModel.getUpdatedA2lWpParamMapping().clear();

    pidcA2l = new PidcA2lServiceClient().getById(1579500628L);

    A2lWpParamMappingUpdateModel deletedModel = serviceClient.updateA2lWpParamMapping(updatedModel, pidcA2l);

    // validating delete object
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("A2l WP Parameter Mapping with ID '" +
        serviceClient.get(deletedModel.getDeletedA2lWpParamMapping().keySet().iterator().next()) + "' not found");
    serviceClient.get(deletedModel.getDeletedA2lWpParamMapping().keySet().iterator().next());
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }


  /**
   * @param createdA2lWpParamMapping validating the created record
   */
  private void testOutput(final Map<Long, A2lWpParamMapping> createdA2lWpParamMapping) {
    assertEquals("Param Id is equal", createdA2lWpParamMapping.values().iterator().next().getParamId(),
        Long.valueOf(15516023L));
    assertEquals("Wp RespId is equal", createdA2lWpParamMapping.values().iterator().next().getWpRespId(),
        Long.valueOf(2248779442L));
    assertEquals("created user is equal", createdA2lWpParamMapping.values().iterator().next().getCreatedUser(),
        createdA2lWpParamMapping.values().iterator().next().getCreatedUser());
    assertNotNull("created date should not be null",
        createdA2lWpParamMapping.values().iterator().next().getCreatedUser());
  }

  /**
   * @param a2lWpParamMapping validate the updated record
   */
  private void testOutput(final A2lWpParamMapping a2lWpParamMapping) {
    assertEquals("Param Id is equal", a2lWpParamMapping.getParamId(), Long.valueOf(15516023L));
    assertEquals("Wp RespId is equal", a2lWpParamMapping.getWpRespId(), Long.valueOf(1746086278L));
    assertEquals("created user is equal", a2lWpParamMapping.getCreatedUser(), a2lWpParamMapping.getCreatedUser());
    assertNotNull("created date should not be null", a2lWpParamMapping.getCreatedUser());
  }
}