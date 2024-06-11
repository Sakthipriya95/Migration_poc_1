package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValueAndValidtyModel;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValuesCreationModel;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client Test for PredefinedAttrValue
 *
 * @author HNU1COB
 */
public class PredefinedAttrValueServiceClientTest extends AbstractRestClientTest {


  private final static Long PREDEFINED_ATTR_ID = 1420158427L;
  private final static Long PREDEFINED_GRP_ATTRVALUE_ID = 787914015L;
  private final static Long INVALID_PREDEFINED_ATTR_ID = -1L;
  private final static Long INVALID_PREDEFINED_GRP_ATTRVALUE_ID = -787914015L;
  private static Long GRP_ATTR_VALUE[] = { 787914015L, 787913865L };
  private final static Set<Long> GRP_ATTR_VALUE_SET = new HashSet<>(Arrays.asList(GRP_ATTR_VALUE));
  private static Long INVALID_GRP_ATTR_VALUE[] = { -1L };
  private final static Set<Long> INVALID_GRP_ATTR_VALUE_SET = new HashSet<>(Arrays.asList(INVALID_GRP_ATTR_VALUE));

  /**
   * Test method for {@link PredefinedAttrValueServiceClient#getByValueId(java.lang.Long)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetByValueId() throws ApicWebServiceException {
    PredefinedAttrValueServiceClient servClient = new PredefinedAttrValueServiceClient();
    Map<Long, PredefinedAttrValue> retMap = servClient.getByValueId(PREDEFINED_GRP_ATTRVALUE_ID);
    assertFalse("Response should not be null or empty", ((retMap == null) || retMap.isEmpty()));
    PredefinedAttrValue ret = retMap.get(PREDEFINED_ATTR_ID);
    testOutput(ret);
  }

  /**
   * Test method for {@link PredefinedAttrValueServiceClient#getById(java.lang.Long)}.
   *
   * @throws ApicWebServiceException exe
   */
  @Test
  public void testGetById() throws ApicWebServiceException {
    PredefinedAttrValueServiceClient servClient = new PredefinedAttrValueServiceClient();
    PredefinedAttrValue ret = servClient.getById(PREDEFINED_ATTR_ID);
    assertNotNull("Response should not be null", ret);
    testOutput(ret);
  }

  /**
   * Test method for
   * {@link PredefinedAttrValueServiceClient#getPredefinedAttrValuesAndValidityForValueSet(java.util.Set)}.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetPredefinedAttrValuesAndValidityForValueSet() throws ApicWebServiceException {
    PredefinedAttrValueServiceClient servClient = new PredefinedAttrValueServiceClient();
    PredefinedAttrValueAndValidtyModel retModel =
        servClient.getPredefinedAttrValuesAndValidityForValueSet(GRP_ATTR_VALUE_SET);
    assertNotNull("Response should not be null or empty", retModel);
    Map<Long, Map<Long, PredefinedAttrValue>> retAttrValMap = retModel.getPredefinedAttrValueMap();
    Map<Long, PredefinedAttrValue> retMap = retAttrValMap.get(787914015L);
    PredefinedAttrValue ret = retMap.get(PREDEFINED_ATTR_ID);
    testOutput(ret);
  }

  /**
   * Test method for {@link PredefinedAttrValueServiceClient#getByValueId(java.lang.Long)}.Negative test
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetByValueIdNegative() throws ApicWebServiceException {
    PredefinedAttrValueServiceClient servClient = new PredefinedAttrValueServiceClient();
    Map<Long, PredefinedAttrValue> retMap = servClient.getByValueId(INVALID_PREDEFINED_GRP_ATTRVALUE_ID);
    assertTrue("Response should be null or empty", ((retMap == null) || retMap.isEmpty()));
  }

  /**
   * Test method for {@link PredefinedAttrValueServiceClient#getById(java.lang.Long)}.Negative Test
   *
   * @throws ApicWebServiceException 'Predefined Attribute Value with ID not found'
   */
  @Test
  public void testGetByIdNegative() throws ApicWebServiceException {
    PredefinedAttrValueServiceClient servClient = new PredefinedAttrValueServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Predefined Attribute Value with ID '" + INVALID_PREDEFINED_ATTR_ID + "' not found");
    PredefinedAttrValue ret = servClient.getById(INVALID_PREDEFINED_ATTR_ID);
    fail("Expected Exception not thrown");
  }

  /**
   * Test method for
   * {@link PredefinedAttrValueServiceClient#getPredefinedAttrValuesAndValidityForValueSet(java.util.Set)}.Negative Test
   *
   * @throws ApicWebServiceException 'iCDM error'
   */
  @Test
  public void testGetPredefinedAttrValuesAndValidityForValueSetNegative() throws ApicWebServiceException {
    PredefinedAttrValueServiceClient servClient = new PredefinedAttrValueServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    servClient.getPredefinedAttrValuesAndValidityForValueSet(INVALID_GRP_ATTR_VALUE_SET);
    fail("Expected Exception not thrown");
  }

  /**
   * test output data
   *
   * @param obj
   */
  private void testOutput(final PredefinedAttrValue obj) {
    assertEquals("AttrValId is equal", Long.valueOf(1420158427), obj.getId());
    assertEquals("GrpAttrValId is equal", Long.valueOf(787914015), obj.getGrpAttrValId());
    assertEquals("PredefinedValueId is equal", Long.valueOf(1420138777), obj.getPredefinedValueId());
    assertEquals("CreatedUser is equal", "DGS_ICDM", obj.getCreatedUser());
    assertNotNull("CreatedDate is not null", obj.getCreatedDate());
    assertEquals("PredefinedAttrId is equal", Long.valueOf(1420134671), obj.getPredefinedAttrId());
  }

  /**
   * Test method for {@link PredefinedAttrValueServiceClient#createPredefinedValues(PredefinedAttrValuesCreationModel)}.
   *
   * @throws ApicWebServiceException exe
   */
  @Test
  public void testCreatePredefinedValues() throws ApicWebServiceException {

    PredefinedAttrValue obj = new PredefinedAttrValue();
    obj.setGrpAttrValId(787913565l);
    obj.setPredefinedAttrId(1420134666l);
    obj.setPredefinedValueId(1420134647l);
    Set<PredefinedAttrValue> attrValuesToBeCreated = new HashSet<>();
    attrValuesToBeCreated.add(obj);
    PredefinedAttrValuesCreationModel model = new PredefinedAttrValuesCreationModel();
    model.setValuesToBeCreated(attrValuesToBeCreated);
    PredefinedAttrValueServiceClient servClient = new PredefinedAttrValueServiceClient();
    Set<PredefinedAttrValue> attrValues = servClient.createPredefinedValues(model);
    // validate
    assertNotNull("Response should not be null", attrValues);
    for (PredefinedAttrValue value : attrValues) {
      assertEquals("GrpAttrValId is equal", Long.valueOf(787913565), value.getGrpAttrValId());
      assertEquals("PredefinedValueId is equal", Long.valueOf(1420134647), value.getPredefinedValueId());
      assertEquals("PredefinedAttrId is equal", Long.valueOf(1420134666), value.getPredefinedAttrId());
    }

  }
}
