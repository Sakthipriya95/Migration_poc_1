/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.general;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVariant;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;
import com.bosch.caltool.icdm.model.general.ExternalLinkInfo;
import com.bosch.caltool.icdm.model.uc.UseCase;
import com.bosch.caltool.icdm.model.uc.UseCaseGroup;
import com.bosch.caltool.icdm.model.uc.UseCaseSection;
import com.bosch.caltool.icdm.model.uc.UsecaseFavorite;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.Matchers;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcA2lServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcSubVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CDRReviewResultServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RuleSetServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwQnaireRespVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwQnaireResponseServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.uc.UseCaseGroupServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.uc.UseCaseSectionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.uc.UseCaseServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.uc.UsecaseFavoriteServiceClient;


/**
 * @author bne4cob
 */
public class ExternalLinkServiceClientTest extends AbstractRestClientTest {

  /**
   *
   */
  private static final String PROJECT_LINK_INFO_NOT_NULL = "Project Link Info not null";
  // PIDC
  // X_Testcustomer->Diesel Engine->PC - Passenger Car->EDC17->X_Test_002_P866_EA288
  // PIDC URL - icdm:pidid,2747
  private static final Long PIDC_01 = 2747L;
  private static final String TPIDC_EXP_DISP_TXT =
      "PIDC Version: X_Testcustomer->Diesel Engine->PC - Passenger Car->EDC17->X_Test_002_P866_EA288 (Version 4)";
  private static final Long TPIDC_EXP_LIN_OBJ_ID = 773510915L;
  private static final String TPIDC_EXP_LINK_TYPE = "PIDC_VERSION";
  private static final String TPIDC_EXP_URL = "icdm:pidvid,773510915";


  // Active PIDC version
  // PIDC Version: Alfa Romeo->Gasoline Engine->PC - Passenger Car->ME(D)17->Alfa 1.8L, FamB, Gen2, US (Version 1)
  private static final Long PIDC_VERS_ID_01 = 773519265L;
  private static final String TP01_EXP_DISP_TXT =
      "PIDC Version: Alfa Romeo->Gasoline Engine->PC - Passenger Car->ME(D)17->Alfa 1.8L, FamB, Gen2, US (Version 1)";
  private static final Long TP01_EXP_LIN_OBJ_ID = 773519265L;
  private static final String TP01_EXP_LINK_TYPE = "PIDC_VERSION";
  private static final String TP01_EXP_URL = "icdm:pidvid,773519265";

  // Non active version
  // PIDC Version: X_Testcustomer->Diesel Engine->PC - Passenger Car->EDC17->X_Test_002_P866_EA288 (Version 2)
  private static final Long PIDC_VERS_ID_02 = 773510815L;
  private static final String TP02_EXP_DISP_TXT =
      "PIDC Version: X_Testcustomer->Diesel Engine->PC - Passenger Car->EDC17->X_Test_002_P866_EA288 (Version 2)";
  private static final Long TP02_EXP_LIN_OBJ_ID = 773510815L;
  private static final String TP02_EXP__LINK_TYPE = "PIDC_VERSION";
  private static final String TP02_EXP_URL = "icdm:pidvid,773510815";

  // PIDC Variant
  // PIDC Variant: Proton->Gasoline Engine->PC - Passenger Car->ME17-S0->Proton_NFE (Version 2)->NFE-EU5-16-82kW-MT+\r\n
  private static final Long PIDC_VARIANT = 768398006L;
  private static final String TPVAR_EXP_URL = "icdm:pidvarid,773513215-768398006";
  private static final Long TPVAR_EXP_LIN_OBJ_ID = 768398006L;
  private static final String TPVAR_EXP_LINK_TYPE = "PIDC_VARIANT";
  private static final String TPVAR_EXP_DISP_TXT =
      "PIDC Variant: Proton->Gasoline Engine->PC - Passenger Car->ME17-S0->Proton_NFE (Version 2)->NFE-EU5-16-82kW-MT+\r\n";


  // PidcSubVariant
  // Pidc => PIDC Version: Proton->Gasoline Engine->PC - Passenger Car->ME17-S0->Proton_NFE_2 (1)
  private static final Long PIDC_SUB_VARIANT = 9784775459L;
  private static final String TPSUB_VAR_EXP_URL = "icdm:pidcsubvarid,9784774681-9784775459";
  private static final Long TPSUB_VAR_EXP_LIN_OBJ_ID = 9784775459L;
  private static final String TPSUB_VAR_EXP_LINK_TYPE = "SUB_VARIANT";
  private static final String TPSUB_VAR_EXP_DISP_TXT =
      "PIDC Sub-variant: Proton->Gasoline Engine->PC - Passenger Car->ME17-S0->Proton_NFE_2 (1)->011_X260_Med_MT_EU5_2WD->High_Line_NFE_1.3l_CVT_EU3";


  // A2L File - PIDC A2L
  // A2L File: X_Testcustomer->Diesel Engine->PC - Passenger Car->EDC17->X_Test_002_P866_EA288 (Version
  // 4)->M1764VDAC866->DA805R4000000.A2L
  private static final Long PIDC_A2L = 773714115L;
  private static final String TPA2L_EXP_DISP_TXT =
      "A2L File: X_Testcustomer->Diesel Engine->PC - Passenger Car->EDC17->X_Test_002_P866_EA288 (Version 4)->M1764VDAC866->DA805R4000000.A2L";
  private static final Long TPA2L_EXP_LIN_OBJ_ID = 622995451L;
  private static final String TPA2L_EXP__LINK_TYPE = "A2L_FILE";
  private static final String TPA2L_EXP_URL = "icdm:a2lid,773714115-622995451";

  // Rule Set
  // Rule Set: Audi_V6Evo3_RB-Ladedruckregelung
  private static final Long RULE_SET = 1214155565L;
  private static final String TRS_EXP_DISP_TXT = "Rule Set: Audi_V6Evo3_RB-Ladedruckregelung";
  private static final Long TRS_EXP_LIN_OBJ_ID = 1214155565L;
  private static final String TRS_EXP_LINK_TYPE = "RULE_SET";
  private static final String TRS_EXP_URL = "icdm:rulesetid,1214155565";


  // Review Result
  // CDR Result: Alfa Romeo->Gasoline Engine->PC - Passenger Car->ME(D)17->Alfa 1.8L, FamB, Gen2, US (Version 1)->59)
  // Cylinder Imbalance AFIM - PS-EC (formerly DGS-EC)->2017-01-09 21:54 - 13A3 - MY18 AR4C AFIM Data Review
  private static final Long CDR_RESULT = 788934166L;
  private static final String TCR01_EXP_DISP_PATTERN =
      "CDR Result: Alfa Romeo->Gasoline Engine->PC - Passenger Car->ME\\(D\\)17->Alfa 1.8L, FamB, Gen2, US \\(Version 1\\)->59\\) Cylinder Imbalance AFIM - PS-EC \\(formerly DGS-EC\\)->.* - 13A3 - MY18 AR4C AFIM Data Review";
  private static final Long TCR01_EXP_LIN_OBJ_ID = 788934166L;
  private static final String TCR01_EXP_LINK_TYPE = "CDR_RESULT";
  private static final String TCR01_EXP_URL = "icdm:cdrid,788934166-773519265";

  // Review Result (with variant)
  // CDR Result: X_Testcustomer->Diesel Engine->PC - Passenger Car->EDC17->X_Test_002_P866_EA288 (Version
  // 4)->_0350_Sensoren_Lambda_Sonde->2016-03-21 21:56 - N86F - RFOJ - Test
  private static final Long CDR_RVW_VAR_ID = 782792165L;
  private static final String TCR02_EXP_DISP_PATTERN =
      "CDR Result: X_Testcustomer->Diesel Engine->PC - Passenger Car->EDC17->X_Test_002_P866_EA288 \\(Version 4\\)->_0350_Sensoren_Lambda_Sonde->.* - N86F - RFOJ - Test";
  private static final Long TCR02_EXP_LIN_OBJ_ID = 779968367L;
  private static final String TCR02_EXP_LINK_TYPE = "CDR_RESULT";
  private static final String TCR02_EXP_URL = "icdm:cdrid,779968367-773510915-771384735";

  // Review Result (variant - group)
  // CDR Result:Honda->Gasoline Engine->PC-Passenger Car->ME(D)17->HONDA_ME17956_2SV_4cyl_2.0l_PFI_FF (Version
  // 2)->_RESP__RBLA_Souza->2016-01-21 13:31 - CR800 - 2SV_MT - Souza_heater_config_MT
  private static final String TCR03_EXP_DISP_PATTERN =
      "CDR Result: Honda->Gasoline Engine->PC - Passenger Car->ME\\(D\\)17->HONDA_ME17956_2SV_4cyl_2.0l_PFI_FF \\(Version 2\\)->_RESP__RBLA_Souza->.* - CR800 - 2SV_MT - Souza_heater_config_MT";
  private static final Long TCR03_CDR_RVW_VAR_ID = 782753415L;
  private static final Long TCR03_EXP_LIN_OBJ_ID = 777642966L;
  private static final String TCR03_EXP_LINK_TYPE = "CDR_RESULT";
  private static final String TCR03_EXP_URL = "icdm:cdrid,777642966-774486867-774487163";

  // Review Result (non wp grouping)
  // CDR Result: Alfa Romeo->Gasoline Engine->PC - Passenger Car->ME(D)17->Alfa 1.8L, FamB, Gen2, US (Version
  // 1)->COMPLI_PARAM->2017-09-07 17:22 - 14A0 - only COMPLI
  private static final Long TCR04_CDR_RESULT = 949440467L;
  private static final String TCR04_EXP_DISP_PATTERN =
      "CDR Result: Alfa Romeo->Gasoline Engine->PC - Passenger Car->ME\\(D\\)17->Alfa 1.8L, FamB, Gen2, US \\(Version 1\\)->COMPLI_PARAM->.* - 14A0 - only COMPLI";
  private static final Long TCR04_EXP_LIN_OBJ_ID = 949440467L;
  private static final String TCR04_EXP_LINK_TYPE = "CDR_RESULT";
  private static final String TCR04_EXP_URL = "icdm:cdrid,949440467-773519265";

  // Review Result (Multiple WP)
  // CDR Result:AUDI->Gasoline Engine->PC->Passenger Car->MG1-C->Audi EA888-Gen3-BZyklus (Version 1)->MWP->2020-02-06
  // 20:29 - MY19D00 - MQB_1628_1SHRa_EU_OPF_Q2 - WP Hakan - Delta
  private static final Long TCR05_CDR_RESULT = 2247834449L;
  private static final String TCR05_EXP_DISP_PATTERN =
      "CDR Result: AUDI->Gasoline Engine->PC - Passenger Car->MG1-C->Audi EA888-Gen3-BZyklus \\(Version 1\\)->MWP->.* - MY19D00 - MQB_1628_1SHRa_EU_OPF_Q2 - WP Hakan - Delta";
  private static final Long TCR05_EXP_LIN_OBJ_ID = 2247834449L;
  private static final String TCR05_EXP_LINK_TYPE = "CDR_RESULT";
  private static final String TCR05_EXP_URL = "icdm:cdrid,2247834449-773510565";

  // Review Result (Single WP)
  // CDR Result: BMW->Diesel Engine->PC - Passenger Car->MD1-C->test_for_juint_cdrreviews \\(v1\\)->WP1->2020-02-04
  // 13:57 - 04D0 - 001B_ACC - TestJuit
  private static final Long TCR06_CDR_RESULT = 2222273520L;
  private static final String TCR06_EXP_DISP_PATTERN =
      "CDR Result: BMW->Diesel Engine->PC - Passenger Car->MD1-C->test_for_juint_cdrreviews \\(v1\\)->WP1->.* - 04D0 - 001B_ACC - TestJuit";
  private static final Long TCR06_EXP_LIN_OBJ_ID = 2222273520L;
  private static final String TCR06_EXP_LINK_TYPE = "CDR_RESULT";
  private static final String TCR06_EXP_URL = "icdm:cdrid,2222273520-2221593081";


  // Pidc Version - Use Case Group
  // Use Case Group: AUDI->Gasoline Engine->PC - Passenger Car->MG1-C->Audi EA888-Gen3-BZyklus (Version 1)->Diesel
  private static final String TPUC_GROUP_EXP_DISP_TXT =
      "PIDC Version (Use Case Group): AUDI->Gasoline Engine->PC - Passenger Car->MG1-C->Audi EA888-Gen3-BZyklus (Version 1)->Diesel";
  private static final Long TPUC_GROUP_EXP_LIN_OBJ_ID = 772259416L;
  private static final String TPUC_GROUP_EXP_LINK_TYPE = "USE_CASE_GROUP";
  private static final String TPUC_GROUP_EXP_URL = "icdm:pidvucid,773510565-772259416";

  // Pidc Version - Use Case
  // Use Case: AUDI->Gasoline Engine->PC - Passenger Car->MG1-C->PRO- Projct Cal_1
  private static final Long PIDC_VER = 773510565L;
  private static final String TPUC_EXP_DISP_TXT =
      "PIDC Version (Use Case): AUDI->Gasoline Engine->PC - Passenger Car->MG1-C->Audi EA888-Gen3-BZyklus (Version 1)->PRO- Projct Cal_1";
  private static final Long TPUC_EXP_LIN_OBJ_ID = 2176812342L;
  private static final String TPUC_EXP_LINK_TYPE = "USE_CASE";
  private static final String TPUC_EXP_URL = "icdm:pidvucid,773510565-2176812342";

//Pidc Version - Use Case Section
  // Use Case Section: AUDI->Gasoline Engine->PC - Passenger Car->MG1-C->Audi EA888-Gen3-BZyklus (Version 1)->ASMOD
  // AirMod
  private static final String TPUC_SECT_EXP_DISP_TXT =
      "PIDC Version (Use Case Section): AUDI->Gasoline Engine->PC - Passenger Car->MG1-C->Audi EA888-Gen3-BZyklus (Version 1)->ASMOD AirMod";
  private static final Long TPUC_SECT_EXP_LIN_OBJ_ID = 772273915L;
  private static final String TPUC_SECT_EXP_LINK_TYPE = "USE_CASE_SECT";
  private static final String TPUC_SECT_EXP_URL = "icdm:pidvucid,773510565-772273915";


  // Project Use Case Group
  // Project Use Case: Project Use Case Group: AUDI->Gasoline Engine->PC - Passenger Car->MG1-C->Audi EA888-Gen3-BZyklus
  // (Version 1)->CAL Work Packages DGS-EC/PA
  // pidc version ID = 773510565L
  private static final String TPROJ_UC_GROUP_EXP_DISP_TXT =
      "Project Use Case Group: AUDI->Gasoline Engine->PC - Passenger Car->MG1-C->Audi EA888-Gen3-BZyklus (Version 1)->CAL Work Packages DGS-EC/PA";
  private static final Long SELECTED_PROJ_UC_GROUP_ITEM_ID = 469537L;
  private static final String TPROJ_UC_GROUP_EXP_URL = "icdm:pidcprjucid,10343903779-773510565-469537";

  // Project Use Case
  // Project Use Case: AUDI->Gasoline Engine->PC - Passenger Car->MG1-C->Audi EA888-Gen3-BZyklus (Version 1)->BC - Base
  // Cal edit
  // pidc version ID = 773510565L
  private static final String TPROJ_UC_EXP_DISP_TXT =
      "Project Use Case: AUDI->Gasoline Engine->PC - Passenger Car->MG1-C->Audi EA888-Gen3-BZyklus (Version 1)->BC - Base Cal edit";
  private static final Long TPROJ_UC_EXP_LIN_OBJ_ID = 10343903779L;
  private static final Long SELECTED_PROJ_UC_ITEM_ID = 469544L;
  private static final String TPROJ_UC_EXP_LINK_TYPE = "PROJECT_USE_CASE";
  private static final String TPROJ_UC_EXP_URL = "icdm:pidcprjucid,10343903779-773510565-469544";

  // Project Use Case Section
  // Project Use Case: AUDI->Gasoline Engine->PC - Passenger Car->MG1-C->Audi EA888-Gen3-BZyklus (Version 1)->BC - Base
  // Cal edit
  // pidc version ID = 773510565L
  private static final String TPROJ_UC_SECT_EXP_DISP_TXT =
      "Project Use Case Section: AUDI->Gasoline Engine->PC - Passenger Car->MG1-C->Audi EA888-Gen3-BZyklus (Version 1)->BCAL";
  private static final Long SELECTED_PROJ_UC_SECT_ITEM_ID = 490725L;
  private static final String TPROJ_UC_SECT_EXP_URL = "icdm:pidcprjucid,10343903779-773510565-490725";

  /**
   * Test method for {@link ExternalLinkServiceClient#getLinkInfo( IModel) }. Test for pidc
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetLinkInfoPidc() throws ApicWebServiceException {
    Pidc model = new PidcServiceClient().getById(PIDC_01);
    assertNotNull("link info not null", new ExternalLinkServiceClient().getLinkInfo(model));
    testLinkInfo(model, TPIDC_EXP_DISP_TXT, TPIDC_EXP_LIN_OBJ_ID, TPIDC_EXP_LINK_TYPE, TPIDC_EXP_URL, false);
  }


  /**
   * Test method for {@link ExternalLinkServiceClient#getLinkInfo( IModel) }. Test for active pidc version
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetLinkInfoPidcVers01() throws ApicWebServiceException {
    PidcVersion model = new PidcVersionServiceClient().getById(PIDC_VERS_ID_01);
    assertNotNull("link info not null", new ExternalLinkServiceClient().getLinkInfo(model));
    testLinkInfo(model, TP01_EXP_DISP_TXT, TP01_EXP_LIN_OBJ_ID, TP01_EXP_LINK_TYPE, TP01_EXP_URL, false);
  }

  /**
   * Test method for {@link ExternalLinkServiceClient#getLinkInfo( IModel) }. Test for non active pidc version
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetLinkInfoPidcVers02() throws ApicWebServiceException {
    PidcVersion model = new PidcVersionServiceClient().getById(PIDC_VERS_ID_02);
    assertNotNull("link info not null", new ExternalLinkServiceClient().getLinkInfo(model));
    testLinkInfo(model, TP02_EXP_DISP_TXT, TP02_EXP_LIN_OBJ_ID, TP02_EXP__LINK_TYPE, TP02_EXP_URL, false);
  }


  /**
   * Test method for {@link ExternalLinkServiceClient#getLinkInfo( IModel) }. Test for PIDC variant
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetLinkInfoPidcVariant() throws ApicWebServiceException {
    PidcVariant model = new PidcVariantServiceClient().get(PIDC_VARIANT);
    assertNotNull("link info not null", new ExternalLinkServiceClient().getLinkInfo(model));
    testLinkInfo(model, TPVAR_EXP_DISP_TXT, TPVAR_EXP_LIN_OBJ_ID, TPVAR_EXP_LINK_TYPE, TPVAR_EXP_URL, false);
  }

  /**
   * Test method for {@link ExternalLinkServiceClient#getLinkInfo( IModel) }. Test for PIDC variant
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetLinkInfoPidcSubVariant() throws ApicWebServiceException {
    PidcSubVariant subVariant = new PidcSubVariantServiceClient().get(PIDC_SUB_VARIANT);
    ExternalLinkInfo linkInfo = new ExternalLinkServiceClient().getLinkInfo(subVariant);
    assertNotNull("link info not null", linkInfo);
    testLinkInfo(subVariant, TPSUB_VAR_EXP_DISP_TXT, TPSUB_VAR_EXP_LIN_OBJ_ID, TPSUB_VAR_EXP_LINK_TYPE,
        TPSUB_VAR_EXP_URL, false);
  }

  /**
   * Test method for {@link ExternalLinkServiceClient#getLinkInfo( IModel) }. Test for PIDC A2L
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetLinkInfoA2l() throws ApicWebServiceException {
    PidcA2l model = new PidcA2lServiceClient().getById(PIDC_A2L);

    assertNotNull("link info not null", new ExternalLinkServiceClient().getLinkInfo(model));
    testLinkInfo(model, TPA2L_EXP_DISP_TXT, TPA2L_EXP_LIN_OBJ_ID, TPA2L_EXP__LINK_TYPE, TPA2L_EXP_URL, false);
  }

  /**
   * Test method for {@link ExternalLinkServiceClient#getLinkInfo( IModel) }. Test for common/private USECASE Group
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetLinkInfoUsecaseGroup() throws ApicWebServiceException {

    UseCaseGroup model = new UseCaseGroupServiceClient().getById(TPUC_GROUP_EXP_LIN_OBJ_ID);

    Map<String, String> additionalDetails = new HashMap<>();
    additionalDetails.put(ApicConstants.PIDC_VERS_ID_STR, String.valueOf(PIDC_VER));
    additionalDetails.put(ApicConstants.SELECTED_UC_ITEM_ID_STR, String.valueOf(TPUC_GROUP_EXP_LIN_OBJ_ID));

    assertNotNull("link info not null", new ExternalLinkServiceClient().getLinkInfo(model, additionalDetails));
    testLinkInfo(model, TPUC_GROUP_EXP_DISP_TXT, TPUC_GROUP_EXP_LIN_OBJ_ID, TPUC_GROUP_EXP_LINK_TYPE,
        TPUC_GROUP_EXP_URL, false);
  }

  /**
   * Test method for {@link ExternalLinkServiceClient#getLinkInfo( IModel) }. Test for common/private USECASE
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetLinkInfoUsecase() throws ApicWebServiceException {

    UseCase model = new UseCaseServiceClient().getById(TPUC_EXP_LIN_OBJ_ID);

    Map<String, String> additionalDetails = new HashMap<>();
    additionalDetails.put(ApicConstants.PIDC_VERS_ID_STR, String.valueOf(PIDC_VER));
    additionalDetails.put(ApicConstants.SELECTED_UC_ITEM_ID_STR, String.valueOf(TPUC_EXP_LIN_OBJ_ID));

    assertNotNull("link info not null", new ExternalLinkServiceClient().getLinkInfo(model, additionalDetails));
    testLinkInfo(model, TPUC_EXP_DISP_TXT, TPUC_EXP_LIN_OBJ_ID, TPUC_EXP_LINK_TYPE, TPUC_EXP_URL, false);
  }

  /**
   * Test method for {@link ExternalLinkServiceClient#getLinkInfo( IModel) }. Test for private/common USECASE section
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetLinkInfoUsecaseSection() throws ApicWebServiceException {

    UseCaseSection model = new UseCaseSectionServiceClient().getById(TPUC_SECT_EXP_LIN_OBJ_ID);

    Map<String, String> additionalDetails = new HashMap<>();
    additionalDetails.put(ApicConstants.PIDC_VERS_ID_STR, String.valueOf(PIDC_VER));
    additionalDetails.put(ApicConstants.SELECTED_UC_ITEM_ID_STR, String.valueOf(TPUC_SECT_EXP_LIN_OBJ_ID));

    assertNotNull("link info not null", new ExternalLinkServiceClient().getLinkInfo(model, additionalDetails));
    testLinkInfo(model, TPUC_SECT_EXP_DISP_TXT, TPUC_SECT_EXP_LIN_OBJ_ID, TPUC_SECT_EXP_LINK_TYPE, TPUC_SECT_EXP_URL,
        false);
  }

  /**
   * Test method for {@link ExternalLinkServiceClient#getLinkInfo(IModel, Map) }. Test for Project Use Case Group
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetLinkInfoProjUsecaseGroup() throws ApicWebServiceException {

    UsecaseFavorite model = new UsecaseFavoriteServiceClient().getById(TPROJ_UC_EXP_LIN_OBJ_ID);

    Map<String, String> additionalDetails = new HashMap<>();
    additionalDetails.put(ApicConstants.PIDC_VERS_ID_STR, String.valueOf(PIDC_VER));
    additionalDetails.put(ApicConstants.SELECTED_UC_ITEM_ID_STR, String.valueOf(SELECTED_PROJ_UC_GROUP_ITEM_ID));

    ExternalLinkInfo projLinkInfo = new ExternalLinkServiceClient().getLinkInfo(model, additionalDetails);
    assertNotNull(PROJECT_LINK_INFO_NOT_NULL, projLinkInfo);
    testProjUCLinkInfo(projLinkInfo, TPROJ_UC_GROUP_EXP_DISP_TXT, SELECTED_PROJ_UC_GROUP_ITEM_ID,
        TPROJ_UC_EXP_LINK_TYPE, TPROJ_UC_GROUP_EXP_URL);
  }


  /**
   * Test method for {@link ExternalLinkServiceClient#getLinkInfo(IModel, Map) }. Test for Project Use Case
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetLinkInfoProjUsecase() throws ApicWebServiceException {

    UsecaseFavorite model = new UsecaseFavoriteServiceClient().getById(TPROJ_UC_EXP_LIN_OBJ_ID);

    Map<String, String> additionalDetails = new HashMap<>();
    additionalDetails.put(ApicConstants.PIDC_VERS_ID_STR, String.valueOf(PIDC_VER));
    additionalDetails.put(ApicConstants.SELECTED_UC_ITEM_ID_STR, String.valueOf(SELECTED_PROJ_UC_ITEM_ID));

    ExternalLinkInfo projLinkInfo = new ExternalLinkServiceClient().getLinkInfo(model, additionalDetails);
    assertNotNull(PROJECT_LINK_INFO_NOT_NULL, projLinkInfo);
    testProjUCLinkInfo(projLinkInfo, TPROJ_UC_EXP_DISP_TXT, SELECTED_PROJ_UC_ITEM_ID, TPROJ_UC_EXP_LINK_TYPE,
        TPROJ_UC_EXP_URL);
  }

  /**
   * Test method for {@link ExternalLinkServiceClient#getLinkInfo(IModel, Map) }. Test for Project Use Case Section
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetLinkInfoProjUsecaseSection() throws ApicWebServiceException {

    UsecaseFavorite model = new UsecaseFavoriteServiceClient().getById(TPROJ_UC_EXP_LIN_OBJ_ID);

    Map<String, String> additionalDetails = new HashMap<>();
    additionalDetails.put(ApicConstants.PIDC_VERS_ID_STR, String.valueOf(PIDC_VER));
    additionalDetails.put(ApicConstants.SELECTED_UC_ITEM_ID_STR, String.valueOf(SELECTED_PROJ_UC_SECT_ITEM_ID));

    ExternalLinkInfo projLinkInfo = new ExternalLinkServiceClient().getLinkInfo(model, additionalDetails);
    assertNotNull(PROJECT_LINK_INFO_NOT_NULL, projLinkInfo);
    testProjUCLinkInfo(projLinkInfo, TPROJ_UC_SECT_EXP_DISP_TXT, SELECTED_PROJ_UC_SECT_ITEM_ID, TPROJ_UC_EXP_LINK_TYPE,
        TPROJ_UC_SECT_EXP_URL);
  }

  /**
   * @param projLinkInfo
   * @param projUcExpDispTxt
   * @param projUcExpLinObjId
   * @param projUcExpLinkType
   * @param projUcExpUrl
   */
  private void testProjUCLinkInfo(final ExternalLinkInfo projLinkInfo, final String projUcExpDispTxt,
      final Long projUcExpLinObjId, final String projUcExpLinkType, final String projUcExpUrl) {

    assertEquals("Display Text is equal", projUcExpDispTxt, projLinkInfo.getDisplayText());
    assertEquals("Linkable Obj Id is equal", projUcExpLinObjId, projLinkInfo.getLinkableObjId());
    assertEquals("Link Type is equal", projUcExpLinkType, projLinkInfo.getLinkType());
    assertEquals("Url is equal", projUcExpUrl, projLinkInfo.getUrl());
  }


  /**
   * Test method for {@link ExternalLinkServiceClient#getLinkInfo( IModel) }. Test for rule set
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetLinkInfoRuleSet() throws ApicWebServiceException {
    RuleSet model = new RuleSetServiceClient().get(RULE_SET);
    assertNotNull("link info not null", new ExternalLinkServiceClient().getLinkInfo(model));
    testLinkInfo(model, TRS_EXP_DISP_TXT, TRS_EXP_LIN_OBJ_ID, TRS_EXP_LINK_TYPE, TRS_EXP_URL, false);
  }

  /**
   * Test method for {@link ExternalLinkServiceClient#getLinkInfo( IModel) }. Test for cdr result main variant
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetLinkInfoCdrResult01() throws ApicWebServiceException {
    CDRReviewResult model = new CDRReviewResultServiceClient().getById(CDR_RESULT);
    assertNotNull("link info not null", new ExternalLinkServiceClient().getLinkInfo(model));
    testLinkInfo(model, TCR01_EXP_DISP_PATTERN, TCR01_EXP_LIN_OBJ_ID, TCR01_EXP_LINK_TYPE, TCR01_EXP_URL, true);
  }

  /**
   * Test method for {@link ExternalLinkServiceClient#getLinkInfo( IModel) }. Test for cdr result added variant
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetLinkInfoCdrResult02() throws ApicWebServiceException {
    RvwVariant model = new RvwVariantServiceClient().getById(CDR_RVW_VAR_ID);
    assertNotNull("link info not null", new ExternalLinkServiceClient().getLinkInfo(model));
    testLinkInfo(model, TCR02_EXP_DISP_PATTERN, TCR02_EXP_LIN_OBJ_ID, TCR02_EXP_LINK_TYPE, TCR02_EXP_URL, true);
  }


  /**
   * Test method for {@link ExternalLinkServiceClient#getLinkInfo( IModel) }. Test for cdr result added variant - group
   * based a2l's WP review
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetLinkInfoCdrResult03() throws ApicWebServiceException {
    RvwVariant model = new RvwVariantServiceClient().getById(TCR03_CDR_RVW_VAR_ID);
    assertNotNull("link info not null", new ExternalLinkServiceClient().getLinkInfo(model));
    testLinkInfo(model, TCR03_EXP_DISP_PATTERN, TCR03_EXP_LIN_OBJ_ID, TCR03_EXP_LINK_TYPE, TCR03_EXP_URL, true);
  }

  /**
   * Test method for {@link ExternalLinkServiceClient#getLinkInfo( IModel) }. Test for cdr result compli params (non wp
   * grouping) based a2l's WP review
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetLinkInfoCdrResult04() throws ApicWebServiceException {
    CDRReviewResult model = new CDRReviewResultServiceClient().getById(TCR04_CDR_RESULT);
    assertNotNull("link info not null", new ExternalLinkServiceClient().getLinkInfo(model));
    testLinkInfo(model, TCR04_EXP_DISP_PATTERN, TCR04_EXP_LIN_OBJ_ID, TCR04_EXP_LINK_TYPE, TCR04_EXP_URL, true);
  }


  /**
   * Test method for {@link ExternalLinkServiceClient#getLinkInfo( IModel) }. Test for cdr result Multiple WP
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetLinkInfoCdrResult05() throws ApicWebServiceException {
    CDRReviewResult model = new CDRReviewResultServiceClient().getById(TCR05_CDR_RESULT);
    assertNotNull("link info not null", new ExternalLinkServiceClient().getLinkInfo(model));
    testLinkInfo(model, TCR05_EXP_DISP_PATTERN, TCR05_EXP_LIN_OBJ_ID, TCR05_EXP_LINK_TYPE, TCR05_EXP_URL, true);
  }

  /**
   * Test method for {@link ExternalLinkServiceClient#getLinkInfo( IModel) }. Test for cdr result single WP
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetLinkInfoCdrResult06() throws ApicWebServiceException {
    CDRReviewResult model = new CDRReviewResultServiceClient().getById(TCR06_CDR_RESULT);
    assertNotNull("link info not null", new ExternalLinkServiceClient().getLinkInfo(model));
    testLinkInfo(model, TCR06_EXP_DISP_PATTERN, TCR06_EXP_LIN_OBJ_ID, TCR06_EXP_LINK_TYPE, TCR06_EXP_URL, true);
  }
  
  /**
   * Test link creation for qnaire response without variant
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testGetLinkForQnaireResponseWithoutVariant() throws ApicWebServiceException {
    RvwQnaireResponse qNaireResponse = new RvwQnaireResponseServiceClient().getById(12798248279L);
    ExternalLinkInfo linkInfo = new ExternalLinkServiceClient().getLinkInfo(qNaireResponse);
    assertNotNull("link info not null", linkInfo);
  }
  
  /**
   * Test link creation for qnaire response with variant
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testGetLinkForQnaireResponseWithVariant() throws ApicWebServiceException {
    RvwQnaireRespVariant qNaireRespVar = new RvwQnaireRespVariantServiceClient().getRvwQnaireRespVariant(17913260828L);
    ExternalLinkInfo linkInfo = new ExternalLinkServiceClient().getLinkInfo(qNaireRespVar);
    assertNotNull("link info not null", linkInfo);
  }

  /**
   * @param model
   * @param expDisplayText
   * @param expLinkableObjId
   * @param expLinkType
   * @param expUrl
   * @throws ApicWebServiceException
   */
  private void testLinkInfo(final IModel model, final String expDisplayText, final Long expLinkableObjId,
      final String expLinkType, final String expUrl, final boolean pattern)
      throws ApicWebServiceException {

    ExternalLinkInfo linkInfo;
    // condition to check whether it is a use case group link
    if (TPUC_GROUP_EXP_LINK_TYPE.equals(expLinkType)) {
      Map<String, String> additionalDetails = new HashMap<>();
      additionalDetails.put(ApicConstants.PIDC_VERS_ID_STR, String.valueOf(PIDC_VER));
      additionalDetails.put(ApicConstants.SELECTED_UC_ITEM_ID_STR, String.valueOf(TPUC_GROUP_EXP_LIN_OBJ_ID));
      linkInfo = new ExternalLinkServiceClient().getLinkInfo(model, additionalDetails);
    }
    // condition to check whether it is a use case link
    else if (TPUC_EXP_LINK_TYPE.equals(expLinkType)) {
      Map<String, String> additionalDetails = new HashMap<>();
      additionalDetails.put(ApicConstants.PIDC_VERS_ID_STR, String.valueOf(PIDC_VER));
      additionalDetails.put(ApicConstants.SELECTED_UC_ITEM_ID_STR, String.valueOf(TPUC_EXP_LIN_OBJ_ID));
      linkInfo = new ExternalLinkServiceClient().getLinkInfo(model, additionalDetails);
    }
    // condition to check whether it is a use case section link
    else if (TPUC_SECT_EXP_LINK_TYPE.equals(expLinkType)) {
      Map<String, String> additionalDetails = new HashMap<>();
      additionalDetails.put(ApicConstants.PIDC_VERS_ID_STR, String.valueOf(PIDC_VER));
      additionalDetails.put(ApicConstants.SELECTED_UC_ITEM_ID_STR, String.valueOf(TPUC_SECT_EXP_LIN_OBJ_ID));
      linkInfo = new ExternalLinkServiceClient().getLinkInfo(model, additionalDetails);
    }
    else {
      linkInfo = new ExternalLinkServiceClient().getLinkInfo(model);
    }
    LOG.debug("External link : Display Text = {}", linkInfo.getDisplayText());
    LOG.debug("External link : Linkable Obj Id = {}", linkInfo.getLinkableObjId());
    LOG.debug("External link : Link Type = {}", linkInfo.getLinkType());
    LOG.debug("External link : url = {}", linkInfo.getUrl());

    if (pattern) {
      assertThat("Test Display Text", linkInfo.getDisplayText(), Matchers.matchesPattern(expDisplayText));
    }
    else {
      assertEquals("Test Display Text", expDisplayText, linkInfo.getDisplayText());
    }

    assertEquals("Test Linkable Obj Id", expLinkableObjId, linkInfo.getLinkableObjId());
    assertEquals("Test Link Type", expLinkType, linkInfo.getLinkType());
    assertEquals("Test Url", expUrl, linkInfo.getUrl());

  }

}
