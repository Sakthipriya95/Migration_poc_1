package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Test;

import com.bosch.caltool.icdm.model.a2l.A2lVarGrpMapCmdModel;
import com.bosch.caltool.icdm.model.a2l.A2lVarGrpVariantMapping;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcA2lServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client Test for A2lVarGrpVarMapping
 *
 * @author pdh2cob
 */
public class A2lVarGrpVarMappingServiceClientTest extends AbstractRestClientTest {

  private static final String EXPECTED_EXCEPTION_NOT_THROWN = "Expected exception not thrown";


  /**
   * Test method for {@link A2lVarGrpVarMappingServiceClient#create(A2lVarGrpVariantMapping, PidcA2l)}
   *
   * @throws ApicWebServiceException error from server
   */
  @Test
  public void testcreate() throws ApicWebServiceException {
    A2lVarGrpVariantMapping createMapping = createA2lVarGrpMapping();

    assertNotNull(createMapping);
    assertNotNull(createMapping.getId());

  }

  /**
   * @return
   * @throws ApicWebServiceException
   */
  private A2lVarGrpVariantMapping createA2lVarGrpMapping() throws ApicWebServiceException {
    A2lVarGrpVarMappingServiceClient servClient = new A2lVarGrpVarMappingServiceClient();

    A2lVarGrpVariantMapping mapping = new A2lVarGrpVariantMapping();


    mapping.setA2lVarGroupId(createA2lVarGroup().getId());
    mapping.setVariantId(1013490549l);
    PidcA2lServiceClient client = new PidcA2lServiceClient();
    return servClient.create(mapping, client.getById(1577924978L));
  }

  /**
   * @return
   * @throws ApicWebServiceException
   */
  private A2lVariantGroup createA2lVarGroup() throws ApicWebServiceException {
    A2lVariantGroupServiceClient servClient = new A2lVariantGroupServiceClient();
    A2lVariantGroup a2lVarGroup = new A2lVariantGroup();
    String runId = String.valueOf(System.currentTimeMillis());
    a2lVarGroup.setDescription("Variant group Desc");
    a2lVarGroup.setName("JunitVG_" + runId);
    a2lVarGroup.setWpDefnVersId(1534834927l);
    PidcA2lServiceClient client = new PidcA2lServiceClient();

    return servClient.create(a2lVarGroup, client.getById(1577924978L));
  }

  /**
   * Test method for {@link A2lVarGrpVarMappingServiceClient#delete(A2lVarGrpVariantMapping, PidcA2l)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testDelete() throws ApicWebServiceException {
    A2lVarGrpVarMappingServiceClient servClient = new A2lVarGrpVarMappingServiceClient();
    A2lVarGrpVariantMapping createMapping = createA2lVarGrpMapping();
    assertNotNull(createMapping);
    PidcA2lServiceClient client = new PidcA2lServiceClient();
    servClient.delete(createMapping, client.getById(1577924978L));

  }

  /**
   * Test method for {@link A2lVarGrpVarMappingServiceClient#updateVariantMappings(A2lVarGrpMapCmdModel, PidcA2l)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testUpdateVariantMappings() throws ApicWebServiceException {
    A2lVarGrpVarMappingServiceClient servClient = new A2lVarGrpVarMappingServiceClient();

    A2lVarGrpVariantMapping mapping = new A2lVarGrpVariantMapping();
    mapping.setA2lVarGroupId(1539940485l);
    mapping.setVariantId(1013490549l);

    PidcA2l pidcA2l = new PidcA2lServiceClient().getById(1205578266L);

    // create mapping
    A2lVarGrpVariantMapping createMapping = servClient.create(mapping, pidcA2l);
    assertNotNull(createMapping);
    // delete mapping
    A2lVarGrpMapCmdModel model = new A2lVarGrpMapCmdModel();
    model.setMappingTobeDeleted(Arrays.asList(createMapping));
    servClient.updateVariantMappings(model, pidcA2l);
  }

  /**
   * Negative Test method for {@link A2lVarGrpVarMappingServiceClient#get(Long)}
   *
   * @throws ApicWebServiceException
   */
  @Test
  public void testGetInvalidVariantGroupId() throws ApicWebServiceException {

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("A2l Variant Group Variant Mapping with ID '1' not found");
    new A2lVarGrpVarMappingServiceClient().get(1L);
    fail(EXPECTED_EXCEPTION_NOT_THROWN);
  }

  /**
   * Test method for {@link A2lVarGrpVarMappingServiceClient#get(Long)} expected: VariantId: 356019L, VarGroupId:
   * 1578116693L,VersionId: 1
   *
   * @throws ApicWebServiceException
   */
  @Test
  public void testGetValidVariantGroupId() throws ApicWebServiceException {

    A2lVarGrpVariantMapping a2lVarGrpVariantMapping = new A2lVarGrpVarMappingServiceClient().get(1578116694L);


    assertNotNull("Response should not be null", a2lVarGrpVariantMapping);
    assertNotNull("VariantId should not be null", a2lVarGrpVariantMapping.getVariantId());
    assertNotNull("A2lVarGroupId should not be null", a2lVarGrpVariantMapping.getA2lVarGroupId());
    assertNotNull("Version should not be null", a2lVarGrpVariantMapping.getVersion());


    assertEquals("VariantId should be equal to ", (Long) 356019L, (a2lVarGrpVariantMapping.getVariantId()));

    assertEquals("Variant Group Id should be equal to ", (Long) 1578116693L,
        (a2lVarGrpVariantMapping.getA2lVarGroupId()));

    assertEquals("Version should be equal to ", (Long) 1L, (a2lVarGrpVariantMapping.getVersion()));

  }

}
