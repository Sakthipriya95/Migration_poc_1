/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bosch.boot.ssd.api.service.CreateSSDUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * @author TUD1COB
 *
 */
@RestController
@RequestMapping("/ssdService")
public class CreateSSDUserController {
  
  
  @Autowired
  private CreateSSDUserService createSSDUserService;
  

  @SuppressWarnings("javadoc")
  @Operation(summary = "schedule the procedure to create SSD users", description = "Schedules the procedure PR_IDM_UPDATE_USER_ACCESS")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Success"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error") })
  @GetMapping("/createUser")
  @Scheduled(cron = "${interval-in-cron-createuserssd}")
  public void createSSDUser()
  {
    this.createSSDUserService.doCreateUser();
  }

}
