package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixVersionAttr;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client Test for Focus Matrix Version Attribute
 *
 * @author MKL2COB
 */
public class FocusMatrixVersionAttrServiceClientTest extends AbstractRestClientTest {

  /**
   *
   */
  private static final long FOCUSMATRIXVERSION_ID_CRUD = 1055530072L;

  /**
   * CONSTANT FOR FOCUS MATRIX VERSION ID
   */
  private static final Long FOCUSMATRIX_VERSION_ID = 1503185335L;

  private static final Long FOCUSMATRIXVERSIONATTR_ID = 1503185336L;

  private static final Long ATTR_ID = 1265L;


  /**
   * Expected exception
   */
  public final ExpectedException thrown = ExpectedException.none();


  /**
   * Test method for {@link FocusMatrixVersionAttrServiceClient#getById(Long)}
   */
  @Test
  public void testGetById() {
    LOG.info("=======================================================================================================");
    LOG.info(
        " TestGetById==============================================================================================");
    LOG.info("=======================================================================================================");
    FocusMatrixVersionAttrServiceClient servClient = new FocusMatrixVersionAttrServiceClient();
    try {
      FocusMatrixVersionAttr ret = servClient.getById(FOCUSMATRIXVERSIONATTR_ID);
      assertFalse("Response should not be null", (ret == null));
      // testOutput(ret);
    }
    catch (Exception excep) {
      LOG.error("Error in WS call", excep);
      assertNull("Error in WS call", excep);
    }
  }

  /**
   * Test method for fetching version attributes for a focus matrix version
   */
  @Test
  public void testVersionAttrsForVersion() {
    LOG.info("=======================================================================================================");
    LOG.info(
        " TestVersionAttrForFMVersion==============================================================================================");
    LOG.info("=======================================================================================================");
    FocusMatrixVersionAttrServiceClient servClient = new FocusMatrixVersionAttrServiceClient();
    try {
      Map<Long, FocusMatrixVersionAttr> focusMatrixAttrForVersion =
          servClient.getFocusMatrixAttrForVersion(FOCUSMATRIX_VERSION_ID);
      assertFalse("Response should not be null or empty",
          ((focusMatrixAttrForVersion == null) || focusMatrixAttrForVersion.isEmpty()));
    }
    catch (Exception excep) {
      LOG.error("Error in WS call", excep);
      assertNull("Error in WS call", excep);
    }
  }

  /**
   * This test casen is to check the Create, Delete and update operation
   */
  @Test
  public void testCreateUpdateDelete() {
    LOG.info("=======================================================================================================");
    LOG.info(
        " testCreateUpdateDelete==============================================================================================");
    LOG.info("=======================================================================================================");
    FocusMatrixVersionAttrServiceClient servClient = new FocusMatrixVersionAttrServiceClient();
    Map<Long, FocusMatrixVersionAttr> existingRecord;
    boolean isRecordExist = false;
    Map<Long, FocusMatrixVersionAttr> collect = new HashMap<>();
    try {
      existingRecord = servClient.getFocusMatrixAttrForVersion(FOCUSMATRIXVERSION_ID_CRUD);
      collect = existingRecord.entrySet().stream()
          .filter(fmVersAttr -> fmVersAttr.getValue().getFmVersId().equals(1055530072L) &&
              fmVersAttr.getValue().getAttrId().equals(ATTR_ID))
          .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
      if (CommonUtils.isNotEmpty(collect)) {
        isRecordExist = true;
      }

    }


    catch (ApicWebServiceException e) {
      LOG.debug(e.getMessage(), e);
    }

    try {
      FocusMatrixVersionAttr obj = new FocusMatrixVersionAttr();
      obj.setFmVersId(FOCUSMATRIXVERSION_ID_CRUD);
      obj.setAttrId(ATTR_ID);
      obj.setVariantId(null);
      obj.setSubVariantId(null);
      obj.setUsed("Y");
      obj.setValueId(null);
      Date date = Calendar.getInstance().getTime();
      DateFormat dateFormat = new SimpleDateFormat("dd-mmm-yy hh:mm:ss");
      String strDate = dateFormat.format(date);
      obj.setCreatedDate(strDate);
      obj.setModifiedDate(null);
      obj.setModifiedUser(null);
      obj.setVersion(1L);

      // check for existing record. if yes then delete it.
      if (isRecordExist) {
        for (Entry<Long, FocusMatrixVersionAttr> entry : collect.entrySet()) {
          Long objectID = entry.getValue().getId();
          servClient.delete(objectID);
        }
      }
      // Invoke create method
      FocusMatrixVersionAttr createdObj = servClient.create(obj);
      assertNotNull("object not null", createdObj);
      testOutput(createdObj);

      // update
      obj.setId(createdObj.getId());
      obj.setModifiedDate(strDate);
      FocusMatrixVersionAttr updatedObj = servClient.update(obj);
      assertNotNull("object not null", updatedObj);
      testOutput(updatedObj);

      // delete
      servClient.delete(updatedObj.getId());
    }
    catch (Exception excep) {
      LOG.error("Error in WS call", excep);
      assertNull("Error in WS call", excep);
    }
  }

  /**
   * test output data
   */
  private void testOutput(final FocusMatrixVersionAttr obj) {
    assertEquals("FmVersId is equal", obj.getFmVersId().toString(), "1055530072");
    assertEquals("Used is equal", obj.getUsed(), "Y");
    assertNotNull("CreatedDate is not null", obj.getCreatedDate());
    assertEquals("CreatedUser is equal", obj.getCreatedUser(), "BNE4COB");
    assertEquals("AttrId is equal", obj.getAttrId().toString(), "1265");

  }

}
