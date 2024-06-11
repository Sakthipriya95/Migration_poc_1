/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantData;
import com.bosch.caltool.icdm.model.emr.EmrEmissionStandard;
import com.bosch.caltool.icdm.model.emr.EmrFile;
import com.bosch.caltool.icdm.model.emr.EmrFileEmsVariantMapping;
import com.bosch.caltool.icdm.model.emr.EmrPidcVariant;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.apic.emr.EmrFileServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.emr.EmrFileVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author IKI1COB
 */
public class EmrFileVariantServiceClientTest extends AbstractRestClientTest {

  /**
   *
   */
  private static final long EMR_FILE_ID_1 = 6437603729L;
  private final static Long VARIANT_ID = 6437597523L;
  private final static Long INVALID_VARIANT_ID = -1L;
  private final static Long EMS_ID = 1334412165L;
  private final static Long EMR_FILE_ID = 1453992280L;
  private final static Long EMR_PV_ID = 1453992280L;

  /**
   * Test method for {@link EmrFileServiceClient#getPidcEmrFileMapping(Long)}
   *
   * @throws ApicWebServiceException Webservice Error
   */
  @Test
  public void testGetPidcEmrFileEmsVariantMapping() throws ApicWebServiceException {
    EmrFileVariantServiceClient servClient = new EmrFileVariantServiceClient();

    Set<Long> set = new HashSet<>();
    set.add(EMR_FILE_ID_1);

    EmrFileEmsVariantMapping ret = servClient.getPidcEmrFileEmsVariantMapping(set);
    String message = "Response should not be null or empty";
    assertNotNull(message, ret);
    Map<Long, PidcVariant> pidcVariants = ret.getPidcVariants();
    assertNotNull(message, pidcVariants);
    PidcVariant pidcVariant = pidcVariants.get(VARIANT_ID);
    assertNotNull(message, pidcVariant);
    testPidcVariant(pidcVariant);
    Map<Long, EmrEmissionStandard> emissionStandards = ret.getEmissionStandard();
    assertNotNull(message, emissionStandards);
    EmrEmissionStandard emissionStandard = emissionStandards.get(5909501277L);
    assertNotNull(message, emissionStandard);
    testEmrEmissionStandard(emissionStandard);
    Map<Long, EmrFile> emrFilesMap = ret.getEmrFilesMap();
    assertNotNull(message, emrFilesMap);
    EmrFile emrFileMap = emrFilesMap.get(EMR_FILE_ID_1);
    assertNotNull(message, emrFileMap);
    testEmrFile(emrFileMap);
    Map<Long, Set<EmrPidcVariant>> emrFileEmsVariantMap = ret.getEmrFileEmsVariantMap();
    assertNotNull(message, emrFileEmsVariantMap);
    Set<EmrPidcVariant> emrPidcVariant = emrFileEmsVariantMap.get(EMR_FILE_ID_1);
    assertNotNull(message, emrPidcVariant);

    for (EmrPidcVariant emrPidcVar : emrPidcVariant) {
      if (emrPidcVar.getId().equals(EMR_PV_ID)) {
        testEmrPidcVariant(emrPidcVar);
        break;
      }
    }

  }

  /**
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testsavedeleteEmrFileEmsVariantMapping() throws ApicWebServiceException {
    EmrFileVariantServiceClient servClient = new EmrFileVariantServiceClient();
    EmrPidcVariant emrPidcVariant = new EmrPidcVariant();
    PidcVariantServiceClient serviceClient = new PidcVariantServiceClient();
    PidcVariantData pidcVariantData = new PidcVariantData();
    AttributeValue attributeValue = new AttributeValue();
    attributeValue.setDescriptionEng("Junit_PidcVariant_" + getRunId());
    attributeValue.setAttributeId(126l);
    attributeValue.setTextValueEng("Junit_PidcVariant_" + getRunId());
    attributeValue.setDeleted(false);
    attributeValue.setChangeComment(null);
    attributeValue.setCharacteristicValueId(null);
    attributeValue.setClearingStatus("R");


    PidcVersionServiceClient versionServiceClient = new PidcVersionServiceClient();
    pidcVariantData.setPidcVersion(versionServiceClient.getById(773515365l));
    pidcVariantData.setVarNameAttrValue(attributeValue);
    // Invoke create method
    PidcVariantData createdObj = serviceClient.create(pidcVariantData);
    emrPidcVariant.setPidcVariantId(createdObj.getDestPidcVar().getId());
    emrPidcVariant.setEmrFileId(EMR_FILE_ID);
    emrPidcVariant.setEmissionStdId(EMS_ID);
    // invoke save EmrFiles
    Set<EmrPidcVariant> emrPidcVariantSet = new HashSet<>();
    emrPidcVariantSet.add(emrPidcVariant);

    Map<Long, EmrPidcVariant> retMap = servClient.saveEmrFileEmsVariantMapping(emrPidcVariantSet);
    assertNotNull("Map should not be null or empty", retMap);

    Set<Long> fileIdSet = new HashSet<>();
    fileIdSet.add(EMR_FILE_ID);

    EmrFileEmsVariantMapping emrFileEmsVariantMapping = servClient.getPidcEmrFileEmsVariantMapping(fileIdSet);
    Map<Long, Set<EmrPidcVariant>> ret = emrFileEmsVariantMapping.getEmrFileEmsVariantMap();
    Set<EmrPidcVariant> setEmrPidcVariant = ret.get(EMR_FILE_ID);

    // compare the unique values and retrieve emrPidcVariantIds to delete
    EmrPidcVariant emrPidcVarToDelete = null;
    for (EmrPidcVariant emrPidcVariant2 : setEmrPidcVariant) {
      if (CommonUtils.isEqual(emrPidcVariant2.getPidcVariantId(), emrPidcVariant.getPidcVariantId()) &&
          CommonUtils.isEqual(emrPidcVariant2.getEmissionStdId(), EMS_ID) &&
          CommonUtils.isEqual(emrPidcVariant2.getEmrFileId(), EMR_FILE_ID)) {
        emrPidcVarToDelete = emrPidcVariant2;
        break;
      }
    }
    // invoke delete method
    Set<EmrPidcVariant> setDelete = new HashSet<>();
    if (emrPidcVarToDelete != null) {
      setDelete.add(emrPidcVarToDelete);
      servClient.deleteEmrFileEmsVariantMapping(setDelete);
    }
  }


  /**
   * Test method for {@link EmrFileServiceClient#getPidcEmrFileMappingNegative(Long)}
   *
   * @throws ApicWebServiceException Webservice Error
   */
  @Test
  public void testGetPidcEmrFileEmsVariantMappingNegative() throws ApicWebServiceException {
    EmrFileVariantServiceClient servClient = new EmrFileVariantServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    Set<Long> set = new HashSet<>();

    set.add(-1L);
    set.add(-2L);
    set.add(-3L);

    EmrFileEmsVariantMapping ret = servClient.getPidcEmrFileEmsVariantMapping(set);
    this.thrown.expect(ApicWebServiceException.class);
    Map<Long, PidcVariant> pidcVariants = ret.getPidcVariants();
    pidcVariants.get(INVALID_VARIANT_ID);
    fail("Exception not thrown");

  }

  /**
   * test PidcVariant data
   */
  private void testPidcVariant(final PidcVariant pidcVariant) {
    assertEquals("Created user is equal", "APJ4COB", pidcVariant.getCreatedUser());
    assertEquals("Deleted flag is equal", false, pidcVariant.isDeleted());
    assertEquals("Name is equal", "00003B57_G11_B57D30O0_AWD_EU6_ATT", pidcVariant.getName());
  }

  /**
   * @param emrPidcVar data
   */
  private void testEmrPidcVariant(final EmrPidcVariant emrPidcVar) {
    assertEquals("EmissionStd Id must be equal", Long.valueOf("1334412165"), emrPidcVar.getEmissionStdId());
    assertEquals("Variant Id must be equal", Long.valueOf("766198280"), emrPidcVar.getEmissionStdId());
  }

  /**
   * @param emrFilesMap data
   */
  private void testEmrFile(final EmrFile emrFileMap) {
    assertEquals("Id must be equal", Long.valueOf(EMR_FILE_ID_1), emrFileMap.getId());
    assertFalse("Deleted Flag must be false", emrFileMap.getDeletedFlag());
    assertNull("Desription must be null", emrFileMap.getDescription());
    assertTrue("Is Variant must be true", emrFileMap.getIsVariant());
    assertFalse("LoadedWithoutErrors Flag must be false", emrFileMap.getLoadedWithoutErrorsFlag());
    assertEquals("iCDMFileId must be equal", Long.valueOf("6437603677"), emrFileMap.getIcdmFileId());
    assertEquals("Pidc Version Id must be equal", Long.valueOf("6437597515"), emrFileMap.getPidcVersId());
  }

  /**
   * @param emissionStandard data
   */
  private void testEmrEmissionStandard(final EmrEmissionStandard emissionStandard) {
    assertFalse("EmrEmissionStandardFlag must be false", emissionStandard.getEmissionStandardFlag());
    assertFalse("MeasuresFlag must be false", emissionStandard.getMeasuresFlag());
    assertFalse("TestCaseFlag must be false", emissionStandard.getTestcaseFlag());
    assertEquals("Version must be equal", "Calibration Release Program", emissionStandard.getEmissionStandardName());
    assertNull("ParentId must be null", emissionStandard.getParentId());
  }

}
