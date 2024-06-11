/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CollectionUtil;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSearchCondition;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSearchInput;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSearchResult;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * JUnit test cases for the PIDC Search web service call
 *
 * @author BNE4COB
 */
// ICDM-2326
public class PidcSearchRestTest extends AbstractRestClientTest {

  /**
   *
   */
  private static final long ATTR_ID = 2459L;
  /**
   * Stores the conditions for one test case
   */
  private final Set<PidcSearchCondition> conditions = new HashSet<>();

  /**
   * This test case shows how to get the data to have an object for further usage. <br>
   * Passed: Attribute ID 1621 (), Value ID 696416 () <br>
   * Excepted: PIDC IDs will be shown as console output.
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void createPIDCSearchObject() throws ApicWebServiceException {
    // The map contains all single conditions. Will be passed to the web service.
    // The Set must be cleared before each web service call, if it is used multiple times.
    Set<PidcSearchCondition> searchConditions = new HashSet<>();

    // Example for a condition. Pass the attribute ID in the constructor.
    PidcSearchCondition condition = new PidcSearchCondition();
    condition.setAttributeId(1621L);
    // Pass the value ID. The method van be called several times with different values.
    // All passed values will be considered for the Search
    condition.getAttributeValueIds().add(696416L);

    // Add the condition to the map
    searchConditions.add(condition);
    PidcSearchInput searchInput = new PidcSearchInput();
    searchInput.getSearchConditions().add(condition);

    // Call the webservice. The matching PIDC-IDs for the Serach condition will be returned in a long array.
    Set<PidcSearchResult> pidcScoutResp = new PidcScoutServiceClient().searchProjects(searchInput);

    assertTrue(pidcScoutResp.size() > 0);
    for (PidcSearchResult result : pidcScoutResp) {
      LOG.info("ID: {}", result.getPidcVersion().getId());
    }

  }

  /**
   * Shows the result for a condition with one used flag filter.<br>
   * Passed: Attribute ID 2801L [ SOP (2.) ], Used = YES<br>
   * Excepted: 18 PIDCs should be returned.
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void getResultWithUsedFlag() throws ApicWebServiceException {
    // Attribute ID 2801L [ SOP (2.) ] , Used = YES
    this.conditions.add(getSearchCondTypeUsed(2801L, "YES"));

    assertResult(this.conditions, "Alfa 1.8L, FamB, Gen2", "Audi EA888-Gen2-B8-laengs", "Alfa 1.8L, FamB, Gen2, US",
        "P160/P200", "VW EA888-Gen3-BZ", "KoVOMo V8T", "M276LA", "Suzuki_KC_MY15_4cyl_TGDI",
        "EA211 1,5l TSI (MG1CS011)", "HONDA XE1B 2SV-R", "Hyundai Nu Improved, 1.8L, MPI, MDG1", "Fire 1.4L ME17.3.0",
        "EA211 1,0l R3 TSI EU6", "EA211 TSI ZD C6", "GM-A_EU5_1,8l_VVT", "Iran_Khodro_EF7_AT_NA", "M278", "M256 Hybrid",
        "SAIPA_X200_MT", "GM-A_EU5(EU6)_1,8l_VVT-A_Copy-EC8", "293-UAZ_ZMZ40911.10_2.7l", "M256-a", "Scania_CNG_5Zyl",
        "Iran_Khodro_EF7_AT_TC", "EA398 6,0l TSI", "HN PIDC vCDM Transfer", "Suzuki_KC_MY16_3cyl_TGDI", "M281",
        "EA211 1,2l 1,4l TSI EU", "EA211 1,0l R3 TSI EU6W", "003_PK095_VW370p_MoPf_INVCON23", "Scania_CNG_6Zyl",
        "EDC17C69_JK_EU6_Bridge", "Fire 1.4L ME17.3.0 NextGen", "Audi V8Gen3", "827F", "EA211 1,4l MLBevo",
        "KoVOMo V6T", "054_JLR_I4_AJ200_Calibration", "Audi EA825 C8RS_D5_OPF", "AUDI_TEST_COPY_V1",
        "EA398 W12 6.0l TSI (MG1CS163)", "HN PIDC Demonstrator 5");
  }

  /**
   * Shows the result for a condition with one used flag filter.<br>
   * Passed: Attribute ID 2801L [ SOP (2.) ], Used = YES<br>
   * Excepted: 18 PIDCs should be returned.
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void getResultWithUsedFlagLCase() throws ApicWebServiceException {
    // Attribute ID 2801L [ SOP (2.) ] , Used = YES
    this.conditions.add(getSearchCondTypeUsed(2801L, "yes"));

    assertResult(this.conditions, "Alfa 1.8L, FamB, Gen2", "Audi EA888-Gen2-B8-laengs", "Alfa 1.8L, FamB, Gen2, US",
        "P160/P200", "VW EA888-Gen3-BZ", "KoVOMo V8T", "M276LA", "Suzuki_KC_MY15_4cyl_TGDI",
        "EA211 1,5l TSI (MG1CS011)", "HONDA XE1B 2SV-R", "Hyundai Nu Improved, 1.8L, MPI, MDG1", "Fire 1.4L ME17.3.0",
        "EA211 1,0l R3 TSI EU6", "EA211 TSI ZD C6", "GM-A_EU5_1,8l_VVT", "Iran_Khodro_EF7_AT_NA", "M278", "M256 Hybrid",
        "SAIPA_X200_MT", "GM-A_EU5(EU6)_1,8l_VVT-A_Copy-EC8", "293-UAZ_ZMZ40911.10_2.7l", "M256-a", "Scania_CNG_5Zyl",
        "Iran_Khodro_EF7_AT_TC", "EA398 6,0l TSI", "HN PIDC vCDM Transfer", "Suzuki_KC_MY16_3cyl_TGDI", "M281",
        "EA211 1,2l 1,4l TSI EU", "EA211 1,0l R3 TSI EU6W", "003_PK095_VW370p_MoPf_INVCON23", "Scania_CNG_6Zyl",
        "EDC17C69_JK_EU6_Bridge", "Fire 1.4L ME17.3.0 NextGen", "Audi V8Gen3", "827F", "EA211 1,4l MLBevo",
        "KoVOMo V6T", "054_JLR_I4_AJ200_Calibration", "Audi EA825 C8RS_D5_OPF", "AUDI_TEST_COPY_V1",
        "EA398 W12 6.0l TSI (MG1CS163)", "HN PIDC Demonstrator 5");
  }

  /**
   * If no condition has been passed, no error should appear.<br>
   * Passed: - <br>
   * Excepted: No Error (ResultSet will include all PIDC-IDs)
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void getResultWithEmptyRequest() throws ApicWebServiceException {
    Set<PidcSearchResult> result = getWsResult(this.conditions);
    assertFalse("Result is available", (result == null) || result.isEmpty());
    LOG.info("Result count for empty input= {}", result.size());
  }

  /**
   * Show the result for a PIDC with an attribut which is defined in some variants with Used = Yes, and in some Variants
   * with sued = No<br>
   * Passed: Attribute ID 1193L (CC), Used = YES<br>
   * Excepted: PIDC 386283 054_JLR_I4_AJ200_Calibration defined this attribut in one of the Variants as Used = Yes, in
   * the others as Used = No Should appear in both cases, if used = yes and used = No is searched
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void resultWithAttributInVariantUsedYesNoShouldAppear() throws ApicWebServiceException {
    // Attribute ID 1193L (CC), Used = YES
    this.conditions.add(getSearchCondTypeUsed(1193L, "YES"));

    assertTrue("Not in Search Result, Used = Yes", isPidcInResult(getWsResult(this.conditions), "Honda XE1B 2LZ"));

    // Attribute ID 1193L (CC), Used = NO
    this.conditions.add(getSearchCondTypeUsed(1193L, "NO"));

    assertTrue("Not in Search Result, Used = No",
        isPidcInResult(getWsResult(this.conditions), "FIRE 1.4L ME17.3.0 EU6d / CN 6"));
  }

  /**
   * Show the result for a PIDC with an attribut which is defined as Used = "Yes" on PIDC Level, with Value = "Variant".
   * On Variant Level it is defined as Used = "No" in every Variant.<br>
   * Passed: Attribute ID 765930367 (VMig-Attr01), Used = YES<br>
   * Excepted: PIDC should appear in Search Result for PIDC ID 699017 (Fire 1.4L ME17.3.0) Used = Yes (on PIDC Level)
   * and Used = No (on Variant Level)
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void resultWithAttributInPidcUsedYesInVariantUsedNo() throws ApicWebServiceException {
    // Attribute ID 2801L [ SOP (2.) ], Used = YES//765930367L
    this.conditions.add(getSearchCondTypeUsed(2801L, "YES"));

    assertTrue("Not in Search Result, Used = Yes", isPidcInResult(getWsResult(this.conditions), "M256 Hybrid"));

    // Attribute ID 67 (Customer name or brand), Used = NO
    this.conditions.add(getSearchCondTypeUsed(67L, "NO"));

    assertTrue("Not in Search Result, Used = No", isPidcInResult(getWsResult(this.conditions), "Fire 1.4L ME17.3.0"));
  }

  /**
   * Shows the result for a condition with one attribute filter.<br>
   * Passed: Attribute ID 2801L [ SOP (2.) ], Value IDs 373018 (06.17)<br>
   * Excepted: PIDC M256 should be returned.
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void oneAttributeOneCondition() throws ApicWebServiceException {
    // Attribute Name: 2801L [ SOP (2.) ], Date-Values 06.17
    this.conditions.add(getSearchCondType(2801L, 373018));

    assertResult(this.conditions, "M256 Hybrid", "M256-a", "EA211 1,0l R3 TSI EU6");
  }

  /**
   * Shows the result for a condition with one attribute filters.<br>
   * Passed: Attribute ID 2801L [ SOP (2.) ], Value IDs 373018 (06.17), 485538 (01.17), 5543 (05.16) <br>
   * Excepted: PIDCs M256, 827F, Honda XE1B should be returned.
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void oneAttributeTwoConditions() throws ApicWebServiceException {
    // Attribute Name: 1. SOP, Date-Values 06.17, 01.17, 05.16
    this.conditions.add(getSearchCondType(2801L, 373018, 485538, 5543));

    assertResult(this.conditions, "AUDI_TEST_COPY_V1", "M256-a", "827F", "Audi EA825 C8RS_D5_OPF", "HONDA XE1B 2SV-R",
        "Suzuki_KC_MY16_3cyl_TGDI", "M256 Hybrid", "EA211 1,0l R3 TSI EU6", "KoVOMo V8T", "KoVOMo V6T");
  }

  /**
   * Shows the result for a condition with one attribute filter for two values with two search conditions. This way an
   * attribute with the same filter IDs should NOT be handled.<br>
   * Passed: Attribute ID 2801L [ SOP (2.) ], Value IDs 373018 (06.17), 485538 (01.17), 5543 (05.16) <br>
   * Excepted: PIDCs M256, 827F, Honda XE1B should be returned.
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void oneAttributeTwoSearchCondObjects() throws ApicWebServiceException {
    // Attribute Name: 2801L [ SOP (2.) ], Date-Values 06.17
    this.conditions.add(getSearchCondType(2801L, 373018));

    // Attribute Name: 2801L [ SOP (2.) ], Date-Values 01.17
    this.conditions.add(getSearchCondType(2801L, 485538));

    assertResult(this.conditions);
  }

  /**
   * Shows the result for a condition with two attribute filters.<br>
   * Passed: Attribute ID 36 (Customer name or brand) Value ID 39(VW) <br>
   * Attribute ID 2225 (ECU used for powertrain component) , Value ID 2228(Electric Machine) <br>
   * Excepted: PIDC M256 should be returned.
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void twoAttributesOneResult() throws ApicWebServiceException {
    // Attribute Name: 1. SOP, Date-Value 06.17
    this.conditions.add(getSearchCondType(36L, 39L));
    // Attribute Name: AFR Sensor 1, Value LSU 4.9 TSP
    this.conditions.add(getSearchCondType(2225L, 2228L));

    assertResult(this.conditions, "003_PK088_VW370p_MQB_INVCON33", "003_PK095_VW120e_MoPf_INVCON23",
        "003_PK122_VW_eCrafter_Gen3", "003_PK095_VW370p_MoPf_INVCON23", "003_PK106_VW_MEB_INV_Gen3Evo",
        "003_PK117_VW_MQB_Gen3_MoPf", "003_PK071_VW370e_INVCON23", "003_PK124_VW_120e_Gen3", "003_PK097_VW_INV3xEvo",
        "003_PK048_VW361h_INVCON23", "003_PK119_VW_MQB_Gen3_CHN_JV", "003_PK074_VW370p_INVCON23",
        "003_PK125_VW_37w_Gen3", "003_PK077_VW481p_INVCON33", "003_PK097_VW_INVCON3XEvo", "003_PK036_VW120e_INVCON23",
        "003_PK105_VW370e_MQB_INVCON33", "003_PK095_VW370e_MoPf_INVCON23", "003_PK020_VW_PL72h_INVCON22");
  }

  /**
   * Shows the result for a condition with two attribute filters, that returns an empty resultset. In this case, no
   * error message but an empty array of PID-IDs is returned.<br>
   * Passed: Attribute ID 2801L [ SOP (2.) ], Value ID 373018 (06.17) <br>
   * Attribute ID 1526 (AFR Sensor 1), Value ID 5018 (LSU 4.9)<br>
   * Excepted: Empty long array.
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void twoAttributesNoResult() throws ApicWebServiceException {
    // Attribute Name: 2801L [ SOP (2.) ], Date-Value 06.17
    this.conditions.add(getSearchCondType(2801L, 373018));
    // Attribute Name: AFR Sensor 1, Value LSU 4.9
    this.conditions.add(getSearchCondType(1526L, 5018));

    assertResult(this.conditions);
  }

  /**
   * Shows the result for a condition one used flag (???) and two attribute filters.<br>
   * Passed: Attribute ID 1261 (ACCF2S), Used Flag ??? <br>
   * Attribute ID 2742L , Value IDs 759686666L, 6349L, 2743L 759174175 (all not deleted values of this attribute) <br>
   * Attribute ID 126L , Value IDs 809L, 2669L, 3131L (all not deleted values of this attribute) <br>
   * Excepted: PIDC Audi EA888-Gen2-B8-laengs should be returned.
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void severalAttributesOneResult() throws ApicWebServiceException {
    // Attribute Name: ACCF2S
    this.conditions.add(getSearchCondTypeUsed(1261L, "???"));
    // Attribute Name: AC Current effective Value - maximum Peak, all not deleted values
    this.conditions.add(getSearchCondType(2742L, 759686666L, 6349L, 2743L));
    // Attribute Name: AC Current Sensors, all not deleted values
    this.conditions.add(getSearchCondType(126L, 809L, 2669L, 3131L));

    assertResult(this.conditions, "Audi EA888-Gen2-B8-laengs");
  }

  /**
   * Shows the result for a condition with three different used flags (???, NO, YES) and no additional attribute. <br>
   * Passed: Attribute ID 249268 (Alternator), Used Flag ??? <br>
   * Attribute ID 2802L [ SOP (3.) ], Used Flag NO <br>
   * Attribute ID 2801L [ SOP (2.) ], Used Flag Yes <br>
   * Excepted: PIDCs EA211 1,0l R3 TSI EU6, EA211 1,4l MLBevo, VW EA888-Gen3-BZ, EA211 1,5l TSI (MG1CS011), HONDA XE1B
   * 2SV-R, EA398 6,0l TSI, Audi EA825 C8RS_D5_OPF, AUDI_TEST_COPY_V1, Fire 1.4L ME17.3.0 NextGen, Fire 1.4L ME17.3.0,
   * KoVOMo V6T, KoVOMo V8T, EA398 W12 6.0l TSI (MG1CS163) should be returned.
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void differentUsedFlags() throws ApicWebServiceException {
    // Attribute Name: Alternator
    this.conditions.add(getSearchCondTypeUsed(249268L, "???"));
    // Attribute Name: 2802L [ SOP (3.) ]
    this.conditions.add(getSearchCondTypeUsed(2802L, "NO"));
    // Attribute Name: [ SOP (2.) ]
    this.conditions.add(getSearchCondTypeUsed(2801L, "YES"));

    assertResult(this.conditions, "EA211 1,0l R3 TSI EU6", "EA211 1,4l MLBevo", "VW EA888-Gen3-BZ",
        "EA211 1,5l TSI (MG1CS011)", "HONDA XE1B 2SV-R", "EA398 6,0l TSI", "Audi EA825 C8RS_D5_OPF",
        "AUDI_TEST_COPY_V1", "Fire 1.4L ME17.3.0 NextGen", "Fire 1.4L ME17.3.0", "KoVOMo V6T", "KoVOMo V8T",
        "EA398 W12 6.0l TSI (MG1CS163)");
  }

  /**
   * Shows the result for a condition with three different used flags (???, NO, YES) and one attribute. <br>
   * Passed: Attribute ID 249268 (Alternator), Used Flag ??? <br>
   * Attribute ID 2802L [ SOP (3.) ], Used Flag NO <br>
   * Attribute ID 2801L [ SOP (2.) ], Used Flag Yes <br>
   * Attribute ID 1265 (ABS1), Value ID 1267 (TRUE) <br>
   * Excepted: PIDCs EA398 W12 6.0l TSI (MG1CS163), EA211 1,0l R3 TSI EU6, EA211 1,4l MLBevo,KoVOMo V8T,VW
   * EA888-Gen3-BZ,EA211 1,5l TSI (MG1CS011),KoVOMo V6T,AUDI_TEST_COPY_V1,Fire 1.4L ME17.3.0,HONDA XE1B 2SV-R
   * NextGen,Fire 1.4L ME17.3.0,Audi EA825 C8RS_D5_OPF should be returned.
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void differentUsedFlagsAndAttribute() throws ApicWebServiceException {
    // Attribute Name: Alternator
    this.conditions.add(getSearchCondTypeUsed(249268L, "???"));
    // Attribute Name: 2802L [ SOP (3.) ]
    this.conditions.add(getSearchCondTypeUsed(2802L, "NO"));
    // Attribute Name: 2801L [ SOP (2.) ]
    this.conditions.add(getSearchCondTypeUsed(2801L, "YES"));
    // Attribute Name: ABS1, Value: TRUE
    this.conditions.add(getSearchCondType(1265L, 1267));

    assertResult(this.conditions, "EA398 W12 6.0l TSI (MG1CS163)", "EA211 1,0l R3 TSI EU6", "EA211 1,4l MLBevo",
        "KoVOMo V8T", "VW EA888-Gen3-BZ", "EA211 1,5l TSI (MG1CS011)", "KoVOMo V6T", "AUDI_TEST_COPY_V1",
        "Fire 1.4L ME17.3.0 NextGen", "HONDA XE1B 2SV-R", "Fire 1.4L ME17.3.0", "Audi EA825 C8RS_D5_OPF");
  }

  /**
   * Shows the result for the attribute Exhaust Gas Valv with value text_ger_test<br>
   * Passed: Attribute ID 766 (Exhaust Gas Valv), Value ID 765895618 (text_ger_test) <br>
   * Excepted: BN-TEMP-02 should be returned.
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void filterExhaustGasValve() throws ApicWebServiceException {
    // Attribute Name: Exhaust Gas Valve, Value: text_ger_test
    this.conditions.add(getSearchCondType(766, 768659018));

    assertResult(this.conditions, "KTM R2", "2.0 GME T4 MultiAir High Output, EMEA", "KTM_2Cyl_Inline_MY19_EU4",
        "FCA Alfa-Maserati 2.0 GME T4 MultiAir", "X_Test_HENZE_1788_1", "Fire 1.4L ME17.3.0",
        "BE-045724_GM_V8_EMS_TurnKey");
  }

  /**
   * Shows the result for the attribute Exhaust Gas Valv with value text_ger_test<br>
   * Passed: Attribute ID 766 (Exhaust Gas Valve), Value ID 765895618 (text_ger_test) <br>
   * Excepted: BN-TEMP-02 should be returned.
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void filterAcUsedFlag() throws ApicWebServiceException {
    // Attribute Name: Exhaust Gas Valve, Value: text_ger_test
    this.conditions.add(getSearchCondTypeUsed(391, "NO"));

    assertResult(this.conditions, "Audi V6Evo33", "293-UAZ_ZMZ40911.10_2.7l", "Fire 1.4L ME17.3.0",
        "KTM 1-cyl MY17 MTB", "TCD3.6", "HATZ_4H50_3H50_DPF_soot_load_detection_StageV", "999-RBPF_MDG1_2WP",
        "BE-049307 MFTBC FUSO Canter E-Cell", "MAN_E18", "P1537_SDF_KE_ECU", "Inno Mule 2 (VCU for VMC)", "Lorem Ipsum",
        "KTM_2Cyl_Inline_MY19_EU4", "242-FP_SMC_FamB_EDC17C69_E6d", "TCD5.0 DNE-A", "BMW BMS-O", "MD1CC878_P1712",
        "BeAST - demoengine", "KTM R2", "BE-044113_Suzuki_Turbo", "242-FP_FamB_EDC17C69_E6d", "KTM_1Cyl_MY20_MTB_EU5",
        "div pidc 11-a rename", "P1546_SDF_KE_DCU");
  }

  /**
   * Tests if the A2L flag retuns the right values. PIDCs in the Testcustomer are not considered.<br>
   * Passed: PIDC 'Mon_Card', 'UEGO_Diesel_LSU51' <br>
   * Excepted: 'Mon_Card' has no A2Ls, false is excepted, 'UEGO_Diesel_LSU51' has A2Ls, true is excepted.
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void hasA2LFlag() throws ApicWebServiceException {
    // For PIDC 002_Ford_DV_Neo_EU6_2
    assertA2lFlag(1411067, false);

    // For PIDC 293-UAZ_ZMZ40911.10_2.7l
    assertA2lFlag(765594767, true);
  }

  /**
   * Tests if the A2L flag retuns the right values. PIDCs in the Testcustomer are not considered.<br>
   * Passed: PIDC 'Mon_Card', 'UEGO_Diesel_LSU51' <br>
   * Excepted: 'Mon_Card' has no A2Ls, false is excepted, 'UEGO_Diesel_LSU51' has A2Ls, true is excepted.
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void hasReviewFlag() throws ApicWebServiceException {
    // For PIDC 293-UAZ_ZMZ40911.10_2.7l
    assertReviewFlag(765594767L, true);

    // For PIDC F1_DCU_P1869_Daily_OM
    assertReviewFlag(1213855917L, false);
  }

  /**
   * PIDC Condition filled in two ways. Length must be the same.<br>
   * Passed: Attribute ID 766 (Exhaust Gas Valve), Value ID 765895618 (text_ger_test) <br>
   * Excepted: BN-TEMP-02 should be returned.
   */
  @Test
  public void searchCondTypeMustEqual() {
    // Test with one attribute ID each
    assertList(getSearchCondType(766, 765895618), getSearchCondTypeAddMethod(766, 765895618));

    // Test with many attribute IDs each
    assertList(getSearchCondType(758693115L, 760281461, 759243274, 759151122, 759174175),
        getSearchCondTypeAddMethod(758693115L, 760281461, 759243274, 759151122, 759174175));
  }

  /**
   * This test case to test service by giving 1600 value id's. <br>
   * Passed: Attribute ID 2459 () <br>
   * Excepted: PIDC IDs will be shown as console output.
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void createPIDCSearchObjectWithLargeData() throws ApicWebServiceException {
    Map<Long, Map<Long, AttributeValue>> retMap = new AttributeValueServiceClient().getValuesByAttribute(ATTR_ID);

    // Create a search condition for an attribute, with large number of values (add all values of the attribute)
    PidcSearchCondition condition = new PidcSearchCondition();
    condition.setAttributeId(ATTR_ID);
    condition.setAttributeValueIds(new HashSet<>(retMap.get(ATTR_ID).keySet()));

    Set<PidcSearchCondition> searchConditions = new HashSet<>();
    searchConditions.add(condition);

    PidcSearchInput searchInput = new PidcSearchInput();
    searchInput.getSearchConditions().add(condition);
    searchInput.setSearchA2lFiles(true);
    searchInput.setSearchFocusMatrix(true);
    searchInput.setSearchReviews(true);

    // Call the webservice. The matching PIDC-IDs for the Serach condition will be returned in a long array.
    Set<PidcSearchResult> resultSet = new PidcScoutServiceClient().searchProjects(searchInput);

    assertFalse("Results not empty", resultSet.isEmpty());

    Map<Long, PidcSearchResult> resultMap = CollectionUtil.toMap(resultSet, r -> r.getPidcVersion().getId());

    // Check result contains Audi EA888-Gen3-BZyklus (Version 1)
    assertTrue("Result has PIDC Version 'Audi EA888-Gen3-BZyklus (Version 1)'", resultMap.containsKey(773510565L));
    // Check result contains Audi EA888-Gen2-B8-laengs (Version 1)
    assertTrue("Result has PIDC Version 'Audi EA888-Gen2-B8-laengs (Version 1)'", resultMap.containsKey(773510715L));

    // Log first 5 results
    int counter = 0;
    LOG.info("First 5 PIDC Version IDs :");
    for (PidcSearchResult result : resultSet) {
      if (counter >= 5) {
        break;
      }
      LOG.info("   PIDC Version ID: {}", result.getPidcVersion().getId());
      counter++;
    }

  }

  /**
   * Checks the length of two Lists for equality. There's a method which puts all Attribute-IDs into the COndition and
   * another one that puts an attribute value one by one into the condition. Both must return the same value.
   */
  private void assertList(final PidcSearchCondition searchCondition1, final PidcSearchCondition searchCondition2) {

    int lengthCond1 = searchCondition1.getAttributeValueIds().size();
    int lengthCond2 = searchCondition2.getAttributeValueIds().size();

    assertTrue("PidcSearchCondition is not equal. Length: " + lengthCond1 + "," + lengthCond2,
        lengthCond1 == lengthCond2);
  }

  private void assertA2lFlag(final long pidcId, final boolean shouldHaveA2L) throws ApicWebServiceException {
    boolean pidcFound = false;
    Set<PidcSearchResult> pidcScoutResp = getWsResult(this.conditions);

    for (PidcSearchResult result : pidcScoutResp) {

      // PIDC Test
      if (result.getPidc().getId() == pidcId) {
        LOG.debug("Checking Has A2L for ID: {}, Name: {}, Should have A2L/ Has A2L: {}/{}", pidcId,
            result.getPidc().getName(), shouldHaveA2L, result.isA2lFilesMapped());

        assertTrue(
            "Checking Has A2L for ID: " + pidcId + ", Name: " + result.getPidc().getName() +
                ", Should have A2L/ Has A2L: " + shouldHaveA2L + "/" + result.isA2lFilesMapped(),
            shouldHaveA2L == result.isA2lFilesMapped());

        pidcFound = true;
        break;
      }
    }

    assertTrue("ID: " + pidcId + ", Name: " + getPidcNameFromId(pidcId) + " not found.", pidcFound);
  }

  private void assertReviewFlag(final long pidcId, final boolean shouldHaveReview) throws ApicWebServiceException {
    boolean pidcFound = false;
    Set<PidcSearchResult> pidcScoutResp = getWsResult(this.conditions);

    for (PidcSearchResult pidc : pidcScoutResp) {

      // PIDC Test
      if (pidc.getPidc().getId() == pidcId) {
        LOG.debug("Checking Has Review for ID: {}, Name: {}, Should have Review/ Has Review: {}/{}", pidcId,
            pidc.getPidc().getName(), shouldHaveReview, pidc.isReviewResultsFound());

        assertTrue(
            "Checking Has Review for ID: " + pidcId + ", Name: " + pidc.getPidc().getName() +
                ", Should have Review/ Has Review: " + shouldHaveReview + "/" + pidc.isReviewResultsFound(),
            shouldHaveReview == pidc.isReviewResultsFound());

        pidcFound = true;
        break;
      }
    }

    assertTrue("ID: " + pidcId + ", Name: " + getPidcNameFromId(pidcId) + " not found.", pidcFound);
  }

  /**
   * Checks a Junit test for correctness. The Conditions are passed to the webservice. The result is validated against
   * the passed PIDC names. The number of passed PIDCs and the name of the returned PIDCs must equal each other.
   *
   * @param searchConditions a set of search conditions
   * @param exceptedPidcs a variable argument list of PIDC names that are excepted as result from the search
   * @throws ApicWebServiceException service error
   */
  private void assertResult(final Set<PidcSearchCondition> searchConditions, final String... exceptedPidcs)
      throws ApicWebServiceException {

    LOG.info("Search condition : {}", searchConditions);

    Set<PidcSearchResult> pidcScoutResp = getWsResult(searchConditions);

    LOG.info("Search Results count : {}", pidcScoutResp.size());

    for (PidcSearchResult pidc : pidcScoutResp) {
      LOG.info("PIDC ID = {}, PIDC Version ID: {}, Name: {}", pidc.getPidc().getId(), pidc.getPidcVersion().getId(),
          pidc.getPidcVersion().getName());
    }

    for (String pidc : exceptedPidcs) {
      assertTrue("PIDC " + pidc + " excepted in Result Set", isPidcInResult(pidcScoutResp, pidc));
    }

    assertTrue(exceptedPidcs.length + " PIDCS excepted, got: " + pidcScoutResp.size(),
        pidcScoutResp.size() >= exceptedPidcs.length);

  }

  private Set<PidcSearchResult> getWsResult(final Set<PidcSearchCondition> searchConditions)
      throws ApicWebServiceException {

    PidcSearchInput input = new PidcSearchInput();
    input.getSearchConditions().addAll(searchConditions);

    return new PidcScoutServiceClient().searchProjects(input);
  }


  /**
   * Checks if a passed PIDC name is in the result array of PIDC-IDs.
   *
   * @param pidcScoutResp the long array of PIDC-IDs returned by the web service.
   * @param pidcName the PIDC-Name checked for existence in the array of IDs.
   */
  private boolean isPidcInResult(final Set<PidcSearchResult> pidcScoutResp, final String pidcName) {
    for (PidcSearchResult result : pidcScoutResp) {
      if (result.getPidc().getName().equalsIgnoreCase(pidcName)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Factory method for creating a PidcSearchCondition with an attribute ID and a various number of value IDs
   *
   * @param attributeId the attribute ID.
   * @param attributeValueIds the value IDs. 0..n value IDs can be passed.
   * @return a PidcSearchCondition object.
   */
  private PidcSearchCondition getSearchCondType(final long attributeId, final long... attributeValueIds) {
    PidcSearchCondition pidcSearchCond = new PidcSearchCondition();

    pidcSearchCond.setAttributeId(attributeId);
    for (long valID : attributeValueIds) {
      pidcSearchCond.getAttributeValueIds().add(valID);
    }

    return pidcSearchCond;
  }

  /**
   * Factory method for creating a PidcSearchCondition with an attribute ID and a various number of value IDs using the
   * addValue method. With this method it is possible to add one value to the existing list. The setAttributeValueIds
   * method on the other hand lets you store a whole array at once. The result must be the same
   *
   * @param attributeId the attribute ID.
   * @param attributeValueIds the value IDs. 0..n value IDs can be passed.
   * @return a PidcSearchCondition object.
   */
  private PidcSearchCondition getSearchCondTypeAddMethod(final long attributeId, final long... attributeValueIds) {
    PidcSearchCondition pidcSearchCond = new PidcSearchCondition();

    pidcSearchCond.setAttributeId(attributeId);
    for (long valueId : attributeValueIds) {
      pidcSearchCond.getAttributeValueIds().add(valueId);
    }

    return pidcSearchCond;
  }

  /**
   * Factory method for creating a PidcSearchCondition with an attribute ID and a used flag
   *
   * @param attributeId the attribute ID.
   * @param usedFlag the used flag. One of the values ???, YES, NO.
   * @return a PidcSearchCondition object.
   */
  private PidcSearchCondition getSearchCondTypeUsed(final long attributeId, final String usedFlag) {
    PidcSearchCondition pidcSearchCond = new PidcSearchCondition();
    pidcSearchCond.setAttributeId(attributeId);
    pidcSearchCond.setUsedFlag(usedFlag);

    return pidcSearchCond;
  }

  /**
   * Returns the name of a passed PIDC-ID.
   *
   * @param pidcId the PIDC-ID.
   * @return the name of the PIDC.
   * @throws ApicWebServiceException service error
   */
  private String getPidcNameFromId(final Long pidcId) throws ApicWebServiceException {
    return (new PidcServiceClient().getById(pidcId)).getName();
  }
}
