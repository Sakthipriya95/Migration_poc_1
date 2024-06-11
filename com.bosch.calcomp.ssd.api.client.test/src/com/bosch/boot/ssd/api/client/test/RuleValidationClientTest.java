/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.client.test;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.bosch.calcomp.ssd.api.client.model.RuleValidationInputModel;
import com.bosch.calcomp.ssd.api.client.model.RuleValidationOutputModel;
import com.bosch.calcomp.ssd.api.service.client.RuleValidationClient;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author TAB1JA
 */
public class RuleValidationClientTest {


  // Server
  private final String targetUrl = "https://si-cdm01.de.bosch.com:8743/ssdapiservice/rulevalidationservice";

  private static final String ruleText =
      "#usecase SSD2Rv\r\n#contact \"CR-Customer-Team-Hydraulic\"\r\n    InjVlv_numBnkTDC_CA,    OKMSG, " +
          "\"This is a VBLK Msg\"\r\n#endcontact \r\n#endusecase \r\n asdasdasdasdasdasd\r\n #usecase " +
          "C-SSD\r\n#contact \"CR-Customer-Team-Hydraulic\" asdasd\r\n    TrbCh_Pwm.rPerUn_C, OkMSg," +
          " \"This is a Value  Msg\"\r\n#endcontact \r\n#endusecase\r\nKONSTANTE name_of_constant value " +
          "asdasdasd TESTING\r\n #usecase ssd2rv \r\n#contact \"CR-Customer-Team-Hydraulic\"\r\n    " +
          "LSU_tRi_CUR,    okmsg, \"This is a Curve Msg\"\r\n#endcontact \r\n#endusecase\r\nMATRIX " +
          "name_of_matrix X?number, Y?number, matrix_of_values \r\n#usecase C-SSD\r\n#contact" +
          " \"CR-Customer-Team-Hydraulic\"\r\n    CoEOM_stOpModeDL_MAP,   Okmsg, \"asd\"\r\n#endcontact" +
          " \r\n#endusecase\r\n \r\n#usecase C-SSD \r\n#contact \"CR-Customer-Team-Hydraulic\"\r\n " +
          "   Rail_pSetPointMax_MAP, ERRMSG, \"This is a Map Err Msg\"\r\n#endcontact \r\n#endusecase\r" +
          "\n#usecase             \r\n#contact \"Truck Platform-Team\"\r\n#rule 3532696099:0004\r\n  " +
          "  #IF (EXISTS(InjVlv_pMin_C))\r\n        AccPed_swtBrkPrio_C, >=0, <=2\r\n    #ELSE\r\n    " +
          "#IF (EXISTS(Rail_pMinInjRlsLoLim_C))\r\n        KONSTANTE konstantepminc Rail_pMinInjRlsLoLim_C, " +
          "111 asdasd1aqsdasd1\r\n    #ELSE\r\n        KONSTANTE konstantepminc1 xxx111 10,10 10.0 <=0 >=0 <10 >10\r\n " +
          "   #ENDIF\r\n#ENDIF\r\n#endcontact \r\n#endusecase";


  /**
   * @throws Exception json exception
   */
  @Test
  public void testRuleValidation() throws Exception {

    String ruleTextJson;

    try {
      ObjectMapper objectMapper = new ObjectMapper();
      RuleValidationInputModel input = new RuleValidationInputModel(ruleText);
      ruleTextJson = objectMapper.writeValueAsString(input);
    }
    catch (Exception e) {
      throw new Exception("Error while buiding input json for rule validation : " + e.getMessage());
    }

    RuleValidationClient client = new RuleValidationClient();
    List<RuleValidationOutputModel> resultList = client.getRuleValidation(ruleTextJson, this.targetUrl);
    assertNotNull(resultList.size() > 0);
    assertNotNull(resultList.get(0).getMessage());


  }


}
