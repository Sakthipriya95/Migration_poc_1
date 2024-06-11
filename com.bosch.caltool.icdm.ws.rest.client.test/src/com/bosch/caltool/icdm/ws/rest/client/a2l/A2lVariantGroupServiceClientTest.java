package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.bosch.caltool.icdm.model.a2l.A2LDetailsStructureModel;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcA2lServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client Test for A2lVariantGroup
 *
 * @author pdh2cob
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class A2lVariantGroupServiceClientTest extends AbstractRestClientTest {


  /**
   * Test method for {@link com.bosch.caltool.icdm.ws.rest.client.a2l.A2lVariantGroupServiceClientTest#testGet()}
   *
   * @throws ApicWebServiceException server exception
   */
  @Test
  public void testGet() throws ApicWebServiceException {
    A2lVariantGroupServiceClient servClient = new A2lVariantGroupServiceClient();

    A2lVariantGroup a2lVarGroup = createA2lVarGroup();

    a2lVarGroup = servClient.get(a2lVarGroup.getId());
    assertNotNull(a2lVarGroup);


  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.ws.rest.client.a2l.A2lVariantGroupServiceClientTest#testCreate()}
   *
   * @throws ApicWebServiceException exception from Db
   */
  @Test
  public void testCreate() throws ApicWebServiceException {


    A2lVariantGroup a2lVarGroup = createA2lVarGroup();


    assertNotNull(a2lVarGroup);
    assertNotNull(a2lVarGroup.getId());

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
    A2lVariantGroup a2lVarGroupMod = servClient.create(a2lVarGroup, client.getById(1205578266L));

    return a2lVarGroupMod;
  }


  /**
   * Test method for {@link com.bosch.caltool.icdm.ws.rest.client.a2l.A2lVariantGroupServiceClientTest#testUpdate()}
   *
   * @throws ApicWebServiceException exception from server
   */
  @Test
  public void testUpdate() throws ApicWebServiceException {
    A2lVariantGroupServiceClient servClient = new A2lVariantGroupServiceClient();

    A2lVariantGroup a2lVarGroup = createA2lVarGroup();

    String runId = String.valueOf(System.currentTimeMillis());
    String description = "JunitVGMODIFIEDDec_" + runId;
    String name = "JunitVGMODIFIEDName_" + runId;
    a2lVarGroup.setDescription(description);

    a2lVarGroup.setName(name);
    a2lVarGroup.setWpDefnVersId(1534834927l);
    PidcA2lServiceClient client = new PidcA2lServiceClient();
    servClient.update(a2lVarGroup, client.getById(1205578266L));
    assertEquals(a2lVarGroup.getName(), name);
    assertEquals(a2lVarGroup.getDescription(), description);

  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.ws.rest.client.a2l.A2lVariantGroupServiceClientTest#testDelete()}
   *
   * @throws ApicWebServiceException exception from server
   */
  @Test
  public void testDelete() throws ApicWebServiceException {
    A2lVariantGroupServiceClient servClient = new A2lVariantGroupServiceClient();

    A2lVariantGroup a2lVarGroup = createA2lVarGroup();
    assertNotNull(a2lVarGroup);
    PidcA2lServiceClient client = new PidcA2lServiceClient();
    servClient.delete(a2lVarGroup, client.getById(1205578266L));
    this.thrown.expect(ApicWebServiceException.class);
    servClient.get(a2lVarGroup.getId());

  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.ws.rest.client.a2l.A2lVariantGroupServiceClientTest#testGetByWpDefVerId()}
   *
   * @throws ApicWebServiceException exception from server
   */
  @Test
  public void testGetByWpDefVerId() throws ApicWebServiceException {
    A2lVariantGroupServiceClient servClient = new A2lVariantGroupServiceClient();

    A2LDetailsStructureModel a2lDetailsStructureData = servClient.getA2lDetailsStructureData(1534834927L);

    assertNotNull(a2lDetailsStructureData);
    assertNotNull(a2lDetailsStructureData.getA2lMappedVariantsMap());
    assertNotNull(a2lDetailsStructureData.getA2lVariantGrpMap());
    assertNotNull(a2lDetailsStructureData.getMappedVariantsMap());
    assertNotNull(a2lDetailsStructureData.getWpDefVersion());
    assertNotNull(a2lDetailsStructureData.getUnmappedVariants());

    LOG.debug("Unmapped variants :");
    for (PidcVariant pidcVar : a2lDetailsStructureData.getUnmappedVariants()) {
      LOG.debug(pidcVar.getName());
    }

    System.out.println("all variants :");
    for (PidcVariant pidcVar : a2lDetailsStructureData.getA2lMappedVariantsMap().values()) {
      LOG.debug(pidcVar.getName());
    }


    LOG.debug("mapped variants :");
    for (Long a2lGrpid : a2lDetailsStructureData.getMappedVariantsMap().keySet()) {
      System.out.println("A2l group - " + a2lDetailsStructureData.getA2lVariantGrpMap().get(a2lGrpid).getName());
      for (PidcVariant pidcVar : a2lDetailsStructureData.getMappedVariantsMap().get(a2lGrpid)) {
        LOG.debug("variant - " + pidcVar.getName());
      }
    }

  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.ws.rest.client.a2l.A2lVariantGroupServiceClientTest#testGetByWpDefVerId()}
   *
   * @throws ApicWebServiceException exception from server
   */
  @Test
  public void testGetVarGrpByWpDefVerId() throws ApicWebServiceException {
    A2lVariantGroupServiceClient servClient = new A2lVariantGroupServiceClient();

    Map<Long, A2lVariantGroup> a2lVariantGrpMap = servClient.getVarGrpForWpDefVer(1534834927L);

    assertNotNull(a2lVariantGrpMap);


  }


}
