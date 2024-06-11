/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.junit.Test;

import com.bosch.caltool.icdm.model.cdr.AttachRvwResultInput;
import com.bosch.caltool.icdm.model.cdr.AttachRvwResultResponse;
import com.bosch.caltool.icdm.model.cdr.ReviewVariantModel;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author say8cob
 */
public class RvwVariantServiceClientTest extends AbstractRestClientTest {

  // CDR Result: X_Testcustomer->Diesel Engine->PC - Passenger Car->EDC17->X_Test_002_P866_EA288 (Version
  // 4)->_0350_Sensoren_Lambda_Sonde->2016-03-21 21:56 - N86F - RFOJ - Test
  private static final Long CDR_RVW_VAR_ID = 782792165L;

  /**
   * Test method for {@link RvwVariantServiceClient#getById(Long) }.
   *
   * @throws ApicWebServiceException error while invoking service
   */
  @Test
  public void testGetById() throws ApicWebServiceException {
    RvwVariantServiceClient client = new RvwVariantServiceClient();
    RvwVariant rvwVariant = client.getById(CDR_RVW_VAR_ID);

    assertNotNull("Response item" + ": object not null", rvwVariant);
    assertNotNull("Response item" + ": Name not empty", rvwVariant.getName());

  }

  /**
   * Test method for {@link RvwVariantServiceClient#getById(Long) }.Negative test
   *
   * @throws ApicWebServiceException error while invoking service
   */
  @Test
  public void testGetByIdNegative() throws ApicWebServiceException {
    RvwVariantServiceClient client = new RvwVariantServiceClient();
    this.thrown.expectMessage("Review Result Variant with ID '" + -8L + "' not found");
    client.getById(-8L);
    fail("Expected exception not thrown");
  }

  /**
   * Test method for {@link RvwVariantServiceClient#getReviewVariantListByPidcVersion(Long)}
   *
   * @throws ApicWebServiceException error in test
   */
  @Test
  @Deprecated
  public void testGetReviewVariantListByPidcVersion() throws ApicWebServiceException {

    RvwVariantServiceClient servClient = new RvwVariantServiceClient();
    Map<String, SortedSet<ReviewVariantModel>> retMap = servClient.getReviewVariantListByPidcVersion(775479170L);
    testResponseMap(retMap);
  }

  /**
   * Test method for {@link RvwVariantServiceClient#getReviewVariantListByPidcVersion(Long)}.Negative test
   *
   * @throws ApicWebServiceException error in test
   */
  @Test
  @Deprecated
  public void testGetReviewVariantListByPidcVersionNegative() throws ApicWebServiceException {

    RvwVariantServiceClient servClient = new RvwVariantServiceClient();
    Map<String, SortedSet<ReviewVariantModel>> retMap = servClient.getReviewVariantListByPidcVersion(-1L);
    assertTrue("review variant set should be empty", (retMap == null) || retMap.isEmpty());
  }

  private void testResponseMap(final Map<String, SortedSet<ReviewVariantModel>> retMap) {
    assertFalse("review variant map not empty", (retMap == null) || retMap.isEmpty());

    SortedSet<ReviewVariantModel> reviewVariantSet = retMap.values().iterator().next();

    assertFalse("review variant set not empty", (reviewVariantSet == null) || reviewVariantSet.isEmpty());

    ReviewVariantModel rvwVarMdl = reviewVariantSet.iterator().next();

    assertNotNull("Model - variant available", rvwVarMdl.getRvwVariant());
    assertNotNull("Model - review data available", rvwVarMdl.getReviewResultData());

    LOG.debug("Variant name = " + rvwVarMdl.getRvwVariant().getName());
  }


  /**
   * Test method for {@link RvwVariantServiceClient#getReviewVariantMapWithWPName(Long, Long)}
   *
   * @throws Exception error in test
   */
  @Test
  @Deprecated
  public void testGetReviewVariantMapWithWPName() throws Exception {

    RvwVariantServiceClient servClient = new RvwVariantServiceClient();
    Map<String, SortedSet<ReviewVariantModel>> retMap =
        servClient.getReviewVariantMapWithWPName(776019367L, 773521665L);
    testResponseMap(retMap);
  }

  /**
   * Test method for {@link RvwVariantServiceClient#getReviewVariantMapWithWPName(Long, Long)}.Negative test
   *
   * @throws Exception error in test
   */
  @Test
  @Deprecated
  public void testGetReviewVariantMapWithWPNameNegative() throws Exception {

    RvwVariantServiceClient servClient = new RvwVariantServiceClient();
    this.thrown.expectMessage("PIDC Variant with ID '" + -1l + "' not found");
    servClient.getReviewVariantMapWithWPName(-1l, -2L);
    fail("Expected exception not thrown");
  }

  /**
   * Test method for {@link RvwVariantServiceClient#getReviewVarMap(Long)}
   *
   * @throws ApicWebServiceException error in test
   */
  @Test
  public void testGetReviewVarMap() throws ApicWebServiceException {

    RvwVariantServiceClient servClient = new RvwVariantServiceClient();
    Map<Long, ReviewVariantModel> retMap = servClient.getReviewVarMap(777334727L);
    ReviewVariantModel variantModel = retMap.get(782763515l);
    // validate
    assertNotNull("Review variant model is not null", variantModel);
    assertEquals("variant id  is equal", Long.valueOf(774487180l), variantModel.getRvwVariant().getVariantId());
    assertEquals("Result id is equal", Long.valueOf(777334727l), variantModel.getRvwVariant().getResultId());
  }

  /**
   * Test method for {@link RvwVariantServiceClient#getReviewVarMap(Long)}.Negative test
   *
   * @throws ApicWebServiceException error in test
   */
  @Test
  public void testGetReviewVarMapNegative() throws ApicWebServiceException {
    RvwVariantServiceClient servClient = new RvwVariantServiceClient();
    this.thrown.expectMessage("ID '" + -1l + "' is invalid for Data Review Result");
    servClient.getReviewVarMap(-1L);
    fail("Expected exception not thrown");
  }

  /**
   * Test method for {@link RvwVariantServiceClient#getReviewVariantListByPidcVariant(Long,Long)}
   *
   * @throws ApicWebServiceException error in test
   */
  @Test
  @Deprecated
  public void testGetReviewVariantListByPidcVariant() throws ApicWebServiceException {

    RvwVariantServiceClient servClient = new RvwVariantServiceClient();
    Set<ReviewVariantModel> retSet = servClient.getReviewVariantListByPidcVariant(776019367L, 773521665L);
    for (ReviewVariantModel variantModel : retSet) {
      if (variantModel.getRvwVariant().getId() == 782762915l) {
        assertEquals("variant id  is equal", Long.valueOf(776019367L), variantModel.getRvwVariant().getVariantId());
        assertEquals("Result id is equal", Long.valueOf(777123016), variantModel.getRvwVariant().getResultId());
      }
    }
  }

  /**
   * Test method for {@link RvwVariantServiceClient#getReviewVariantListByPidcVariant(Long,Long)}.Negative test
   *
   * @throws ApicWebServiceException error in test
   */
  @Test
  @Deprecated
  public void testGetReviewVariantListByPidcVariantNegative() throws ApicWebServiceException {

    RvwVariantServiceClient servClient = new RvwVariantServiceClient();
    this.thrown.expectMessage("PIDC Variant with ID '" + -7l + "' not found");
    servClient.getReviewVariantListByPidcVariant(-7L, 773521665L);
    fail("Expected exception not thrown");

  }

  /**
   * Test method for {@link RvwVariantServiceClient#getRvwVariantByResultNVarId(long, long)}
   *
   * @throws ApicWebServiceException error in test
   */
  @Test
  public void testGetRvwVariantByResultNVarId() throws ApicWebServiceException {
    // CDR Result: X_Testcustomer->Diesel Engine->PC - Passenger Car->EDC17->X_Test_002_P866_EA288 (Version
    // 4)->_0350_Sensoren_Lambda_Sonde->2016-03-21 21:56 - N86F - RFOJ - Test
    // icdm:cdrid,779968367-773510915-771384735
    RvwVariant ret = new RvwVariantServiceClient().getRvwVariantByResultNVarId(779968367L, 771384735L);
    assertNotNull("Review variant retrieved using result ID and variant ID" + ": object not null", ret);
    assertNotNull("Review variant retrieved using result ID and variant ID" + ": Name not empty", ret.getName());
  }

  /**
   * Test method for {@link RvwVariantServiceClient#getRvwVariantByResultNVarId(long, long)}.Negative test
   *
   * @throws ApicWebServiceException error in test
   */
  @Test
  public void testGetRvwVariantByResultNVarIdNegative() throws ApicWebServiceException {
    this.thrown.expectMessage("ID '" + -1l + "' is invalid for Data Review Result");
    new RvwVariantServiceClient().getRvwVariantByResultNVarId(-1L, -1L);
    fail("Expected exception not thrown");
  }


  /**
   * Test method for {@link RvwVariantServiceClient#create(RvwVariant)}
   *
   * @throws ApicWebServiceException error in test
   */
  @Test
  public void testCreateDelete() throws ApicWebServiceException {
    RvwVariantServiceClient servClient = new RvwVariantServiceClient();
    RvwVariant rvwVariant = new RvwVariant();
    rvwVariant.setResultId(1756295318l);
    rvwVariant.setVariantId(6134l);
    RvwVariant ret = servClient.create(rvwVariant);
    assertNotNull("Review variant retrieved using result ID and variant ID" + ": object not null", ret);
    assertNotNull("Review variant retrieved using result ID and variant ID" + ": Name not empty", ret.getName());
    servClient.delete(ret);

  }

  /**
   * Test method for {@link RvwVariantServiceClient#getReviewVariantModelSet(Set)}}
   *
   * @throws ApicWebServiceException error in test
   */
  @Test
  public void testReviewVariantModelSet() throws ApicWebServiceException {
    // For Pidc icdm:pidvid,2176862581
    // Test case for test_monica (v1) >> v1_mon >> Work Packages (_DEFAULT_WP_)
    Set<Long> cdrResultIdSet = new HashSet<>();
    cdrResultIdSet.add(2310088178l);
    cdrResultIdSet.add(2181465228l);
    cdrResultIdSet.add(2181476906l);
    cdrResultIdSet.add(2181461928l);

    Set<ReviewVariantModel> reviewVariantModelSet =
        new RvwVariantServiceClient().getReviewVariantModelSet(cdrResultIdSet);
    assertFalse("review variant set not empty", (reviewVariantModelSet == null) || reviewVariantModelSet.isEmpty());

    ReviewVariantModel rvwVarMdl = reviewVariantModelSet.iterator().next();

    assertNotNull("Model - variant available", rvwVarMdl.getRvwVariant());
    assertNotNull("Model - review data available", rvwVarMdl.getReviewResultData());

    LOG.debug("Variant name = " + rvwVarMdl.getRvwVariant().getName());

  }


  /**
   * Test method for {@link RvwVariantServiceClient#attachRvwResWithQnaire(AttachRvwResultInput)}}
   *
   * @throws ApicWebServiceException error in test
   */
  @Test
  public void testAttachRvwResWithQnaire() throws ApicWebServiceException {
    AttachRvwResultInput attachRvwResultInput = new AttachRvwResultInput();
    RvwVariant rvwVariant = new RvwVariant();
    rvwVariant.setVariantId(766323468L);
    rvwVariant.setResultId(779066258L);
    attachRvwResultInput.setRvwVariant(rvwVariant);
    attachRvwResultInput.setLinkExistingQnaire(true);
    RvwVariantServiceClient servClient = new RvwVariantServiceClient();
    AttachRvwResultResponse attachRvwResultResponse = servClient.attachRvwResWithQnaire(attachRvwResultInput);
    assertNotNull(attachRvwResultResponse);
    servClient.delete(attachRvwResultResponse.getRvwVariant());
  }


  /**
   * Test method for {@link RvwVariantServiceClient#attachRvwResWithQnaire(AttachRvwResultInput)}.Negative test}
   *
   * @throws ApicWebServiceException error in test
   */
  @Test
  public void testAttachRvwResWithQnaireNegative() throws ApicWebServiceException {
    AttachRvwResultInput attachRvwResultInput = new AttachRvwResultInput();
    RvwVariant rvwVariant = new RvwVariant();
    rvwVariant.setVariantId(null);
    rvwVariant.setResultId(null);
    attachRvwResultInput.setRvwVariant(rvwVariant);
    attachRvwResultInput.setLinkExistingQnaire(true);
    RvwVariantServiceClient servClient = new RvwVariantServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    AttachRvwResultResponse attachRvwResultResponse = servClient.attachRvwResWithQnaire(attachRvwResultInput);
    fail("Expected exception not thrown");
    assertNull(attachRvwResultResponse);
  }

}
