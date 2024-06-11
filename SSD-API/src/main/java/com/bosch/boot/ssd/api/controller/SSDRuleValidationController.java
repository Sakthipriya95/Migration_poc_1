/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bosch.boot.ssd.api.exception.ParameterInvalidException;
import com.bosch.boot.ssd.api.model.RuleValidationOutputMessage;
import com.bosch.boot.ssd.api.model.SSDRuleInputModel;
import com.bosch.boot.ssd.api.model.io.RestResponse;
import com.bosch.boot.ssd.api.service.ValidateRuleInvokerService;
import com.bosch.boot.ssd.api.util.SSDAPIUtil;
import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @author TAB1JA this controller will be used for SSD rule text validation
 */
@RestController
@Tag(name = "SSD Rule validation API", description = "Enpoints for validating SSD Rule text")
@RequestMapping("/rulevalidationservice")
public class SSDRuleValidationController {
  
  @Autowired
  private ValidateRuleInvokerService validateRuleInvokerService;
  
  private final ILoggerAdapter logger = new Log4JLoggerAdapterImpl(LogManager.getLogger(SSDRuleValidationController.class));



  @SuppressWarnings({ "javadoc" })
  @Operation(summary = "Get SSD rule validated", description = "Validates rule text")

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Success, returns the RuleValidationOutputMessage", content = @Content(schema = @Schema(implementation = RuleValidationOutputMessage.class))),
      @ApiResponse(responseCode = "400", description = "Input parameters ssd file is missing", content = @Content(schema = @Schema(implementation = RestResponse.class))),
      @ApiResponse(responseCode = "404", description = "could not validate ssd rule text for given input file", content = @Content(schema = @Schema(implementation = RestResponse.class))) })
  @PostMapping(value= "/validaterule", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<RuleValidationOutputMessage>> getRuleValidation(@RequestBody final SSDRuleInputModel input)
      throws ParameterInvalidException {
    List<RuleValidationOutputMessage> validationMessageList = new ArrayList<>();
    String errorMessage;
    
    if ((input == null)) {
      throw new ParameterInvalidException();
    } 
      String filePath = SSDAPIUtil.getAppDataFolderPath();
      filePath += "SSDValidate_temp.SSD";
  
      try {
        
        SSDAPIUtil.writeToFile(input.getRuleText(),filePath);
        
        errorMessage =  this.validateRuleInvokerService.validateRules(filePath,logger);
           
        File file = new File(filePath);
        
         if (file.delete()) {
           logger.info("Deleted temp SSD file used for validation ");
         }
        
         if ((errorMessage != null) && !errorMessage.isEmpty()) {
           formResponseMessageList(this.validateRuleInvokerService.getLineNoAndError(),validationMessageList);
           } 
           else {
             return new ResponseEntity<>(validationMessageList, HttpStatus.OK);
           }
        return new ResponseEntity<>(validationMessageList, HttpStatus.OK);
        
    }
    catch (Exception e) {
      logger.error("Error while SSD rule validation from SSD API");
      return new ResponseEntity<>(validationMessageList, HttpStatus.INTERNAL_SERVER_ERROR);
    }

  }
  
  /**
   * @param lineNoAndError
   * @param validationMessageList
   */
  private void formResponseMessageList(Map<Integer, String> lineNoAndError,
      List<RuleValidationOutputMessage> validationMessageList) {
    
    for (Entry<Integer, String> entry : lineNoAndError.entrySet()) {
      RuleValidationOutputMessage validationMessage= new RuleValidationOutputMessage();
      validationMessage.setLineNo(entry.getKey());
      validationMessage.setMessage(entry.getValue());
      validationMessageList.add(validationMessage);
    }
    
  }


  
 

}
