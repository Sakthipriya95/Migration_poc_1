/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.pidc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.pidc.PIDCVersionReport;
import com.bosch.caltool.icdm.model.apic.pidc.SubVariantReport;
import com.bosch.caltool.icdm.model.apic.pidc.VariantReport;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author dja7cob
 */
public class PIDCServiceClientTest extends AbstractRestClientTest {

  private static final Long PIDC_ID = 773517365L;
  private final Long NegativeId = -100L;

  /**
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testsearchProject() throws ApicWebServiceException {
    PIDCServiceClient servClient = new PIDCServiceClient();
    PIDCVersionReport VersionReport = servClient.searchProject(PIDC_ID);

    // PIDCVersionReport has pidcAttrMap and VariantReport List
    Map<String, String> pidcAttrMap = VersionReport.getPidcAttrMap();
    assertFalse("Map should not be empty", ((pidcAttrMap == null) || pidcAttrMap.isEmpty()));

    // PIDCVersionReport has pidcAttrMap and VariantReport List
    List<VariantReport> variants = VersionReport.getVariants();
    assertFalse("List Should not be empty", ((variants.isEmpty())));

    // Validating VariantReport
    // List VariantReport contains SubVariantReport List and PidcVarAttrMap
    for (VariantReport variantReport : variants) {
      if (CommonUtils.isEqual(variantReport.getVariantID(), 768673297L)) {
        testOutput(variantReport);
      }
    }

    // validating SubVariantReport
    // List SubVariantReport contains PidcSubVarAttrMap
    List<SubVariantReport> subvariants = variants.iterator().next().getSubvariants();
    for (SubVariantReport subVariantReport : subvariants) {
      if (CommonUtils.isEqual(subVariantReport.getsVariantID(), 1503066615L)) {
        testOutput(subVariantReport);
      }
    }
    testOutput(VersionReport);

  }

  /**
   * @param subVariantReport
   */
  // validating SubVariantReport
  private void testOutput(final SubVariantReport subVariantReport) {
    assertEquals("Description is equal ", "02_A370_85kW_200Nm_DQ200_EU6AG_OPF", subVariantReport.getDescription());
    assertEquals("Name is equal ", "02_A370_85kW_200Nm_DQ200_EU6AG_OPF", subVariantReport.getName());
    assertEquals("Map value is equal ", "FlexFuel E85", subVariantReport.getPidcSubVarAttrMap().get("Fuel Type"));

  }

  /**
   * @param variantReport
   */
  // Validating VariantReport
  private void testOutput(final VariantReport variantReport) {
    assertEquals("Description is equal ", "2KR EU6B", variantReport.getDescription());
    assertEquals("Name is equal ", "2KR EU6B", variantReport.getName());
    assertEquals("Map value is equal ", "EOBD EU6+", variantReport.getPidcVarAttrMap().get("Diagnosis Legislation"));
  }

  /**
   * @param report
   */
  private void testOutput(final PIDCVersionReport report) {
    assertEquals("PidcID is equal", Long.valueOf(768673117L), report.getPidcID());
    assertEquals("PidcVersion is equal ", "Version 1", report.getPidcVersion());
    assertEquals("ProjectDescription is equal ", "Honda 2KR Variants", report.getProjectDescription());
    assertEquals("ProjectName is equal ", "HONDA XE1B 2KR", report.getProjectName());
    assertEquals("value of map is equal ", "Honda", report.getPidcAttrMap().get("Customer/Brand"));
  }

  /**
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testsearchProjectNegative() throws ApicWebServiceException {
    PIDCServiceClient servClient = new PIDCServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("PIDC Version with ID '" + this.NegativeId + "' not found");
    servClient.searchProject(this.NegativeId);
    fail("Expected exception is not thrown");
  }
}
