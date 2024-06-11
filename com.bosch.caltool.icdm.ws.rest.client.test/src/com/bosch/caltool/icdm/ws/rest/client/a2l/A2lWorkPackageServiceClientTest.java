/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author hnu1cob
 */
public class A2lWorkPackageServiceClientTest extends AbstractRestClientTest {

  /**
   * Test method for {@link A2lWorkPackageServiceClient#create(A2lWorkPackage)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCreateUpdateA2lWp() throws ApicWebServiceException {
    A2lWorkPackage wpPalToCreate = new A2lWorkPackage();
    wpPalToCreate.setName("Junit_WP_" + getRunId());
    wpPalToCreate.setNameCustomer("Junit_WP_" + getRunId());
    wpPalToCreate.setPidcVersId(1507932231L);
    // create
    A2lWorkPackage createdWp = new A2lWorkPackageServiceClient().create(wpPalToCreate);
    // validate create
    assertNotNull("Created Object not null", createdWp);
    assertEquals("Workpackage name is equal", "Junit_WP_" + getRunId(), createdWp.getName());
    assertEquals("Workpackage name at customer is equal", "Junit_WP_" + getRunId(), createdWp.getNameCustomer());
    assertEquals("Pidc version id is equal", Long.valueOf(1507932231), createdWp.getPidcVersId());
    assertEquals("Version is equal", Long.valueOf(1), createdWp.getVersion());
    // update
    createdWp.setName("Junit_WP_Updated" + getRunId());
    A2lWorkPackage updatedWp = new A2lWorkPackageServiceClient().update(createdWp);
    // validate update
    assertNotNull("Created Object not null", updatedWp);
    assertEquals("Workpackage name is equal", "Junit_WP_Updated" + getRunId(), updatedWp.getName());
  }

  /**
   * @throws ApicWebServiceException - Error during webservice call
   */
  @Test
  public void testGetWpByPidcVers() throws ApicWebServiceException {
    A2lWorkPackageServiceClient servClient = new A2lWorkPackageServiceClient();
    Map<Long, A2lWorkPackage> wpsMappedToPidcVers = servClient.getWpByPidcVers(1507932231L);
    assertNotNull("Map of WP is not null", wpsMappedToPidcVers);
  }

  /**
   * @throws ApicWebServiceException - Error during webservice call
   */
  @Test
  public void testGetById() throws ApicWebServiceException {
    A2lWorkPackageServiceClient servClient = new A2lWorkPackageServiceClient();
    // Work Package Id : Überwachung Grundanpassung (Komponentenüberwachung) - BEG
    A2lWorkPackage a2lWorkPackage = servClient.getById(1748481377L);
    assertNotNull("WP is not null", a2lWorkPackage);
  }

  /**
   * Test method for {@link A2lWorkPackageServiceClient#create(A2lWorkPackage)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCreateNegative() throws ApicWebServiceException {
    String wpName = "DIUMPR_ct$%^Gen###[0]" + getRunId();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("The given work package name '" + wpName + "' does not comply with A2L specification");
    A2lWorkPackage wpPalToCreate = new A2lWorkPackage();
    wpPalToCreate.setName(wpName);
    wpPalToCreate.setNameCustomer("Junit_WP_" + getRunId());
    wpPalToCreate.setPidcVersId(1507932231L);
    // create
    new A2lWorkPackageServiceClient().create(wpPalToCreate);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test method for {@link A2lWorkPackageServiceClient#create(A2lWorkPackage)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCreateUpdateNegative() throws ApicWebServiceException {
    A2lWorkPackage wpPalToCreate = new A2lWorkPackage();
    wpPalToCreate.setName("Junit_WP_" + getRunId());
    wpPalToCreate.setNameCustomer("Junit_WP_" + getRunId());
    wpPalToCreate.setPidcVersId(1507932231L);
    // create
    A2lWorkPackage createdWp = new A2lWorkPackageServiceClient().create(wpPalToCreate);
    // validate create
    assertNotNull("Created Object not null", createdWp);
    assertEquals("Workpackage name is equal", "Junit_WP_" + getRunId(), createdWp.getName());
    assertEquals("Workpackage name at customer is equal", "Junit_WP_" + getRunId(), createdWp.getNameCustomer());
    assertEquals("Pidc version id is equal", Long.valueOf(1507932231), createdWp.getPidcVersId());
    assertEquals("Version is equal", Long.valueOf(1), createdWp.getVersion());
    // update
    String wpUpdateName = "DIUMPR_ctGen[ACBD]@.12geddf" + getRunId();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown
        .expectMessage("The given work package name '" + wpUpdateName + "' does not comply with A2L specification");
    createdWp.setName(wpUpdateName);
    new A2lWorkPackageServiceClient().update(createdWp);
  }
}
