/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.controller;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bosch.boot.ssd.api.entity.TSdomBcVariantSsdStatus;
import com.bosch.boot.ssd.api.exception.ParameterInvalidException;
import com.bosch.boot.ssd.api.exception.ResourceNotFoundException;
import com.bosch.boot.ssd.api.model.SSD2BCInfo;
import com.bosch.boot.ssd.api.model.io.RestResponse;
import com.bosch.boot.ssd.api.repository.BCVariantSSDStatusRepository;
import com.bosch.boot.ssd.api.service.SSD2BCService;
import com.bosch.boot.ssd.api.util.SSDAPIUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * This class is used to get the BC details, SSD Status and details of assigned nodes
 *
 * @author GDH9COB
 */
@RestController
@Tag(name = "SSD2BC API", description = "Enpoints for getting the SSD2BC information based on the input parameters")
@RequestMapping("/SSD2BCService")
public class SSD2BCController {

  private static final Logger logger = LoggerFactory.getLogger(SSD2BCController.class);

  @Autowired
  private BCVariantSSDStatusRepository bcVariantSSDStatusRepository;

  @Autowired
  private SSD2BCService ssd2BCService;

  @SuppressWarnings("javadoc")
  @Operation(summary = "Get the TSdomBcVariantSsdStatus", description = "Gets the SSD2BC details for the given Elnummer and variant")

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Success, returns the TSdomBcVariantSsdStatus", content = @Content(schema = @Schema(implementation = TSdomBcVariantSsdStatus.class))),
      @ApiResponse(responseCode = "400", description = "Input parameters elnummer and variant missing", content = @Content(schema = @Schema(implementation = RestResponse.class))),
      @ApiResponse(responseCode = "404", description = "Value not found for the given input parameters", content = @Content(schema = @Schema(implementation = RestResponse.class))) })
  @GetMapping("/bcvariantinfo")
  public TSdomBcVariantSsdStatus getSSD2BC(
      @Parameter(description = "Input elnummer", required = true) @RequestParam(value = "elnummer", required = true) final BigDecimal elnummer,
      @Parameter(description = "Input variant", required = true) @RequestParam("variant") final String variant)
      throws ResourceNotFoundException, ParameterInvalidException {

    // Validate the input parameters
    if ((elnummer == null) && !SSDAPIUtil.isValid(variant)) {
      throw new ParameterInvalidException();
    }

    return this.bcVariantSSDStatusRepository.findByElNummerAndVariant(elnummer, variant)
        .orElseThrow(ResourceNotFoundException::new);
  }

  @SuppressWarnings("javadoc")
  @Operation(summary = "Get the SSD2BC information by elnummer", description = "Gets the SSD2BC details for the given Elnummer and variant")

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Success, returns SSD2BC Information", content = @Content(schema = @Schema(implementation = SSD2BCInfo.class))),
      @ApiResponse(responseCode = "400", description = "Input parameters elnummer and variant missing", content = @Content(schema = @Schema(implementation = RestResponse.class))),
      @ApiResponse(responseCode = "404", description = "Value not found for the given input parameters", content = @Content(schema = @Schema(implementation = RestResponse.class))) })
  @GetMapping("/getSSD2BCInfoByNummer")
  public SSD2BCInfo getSSD2BCInfoByElementNumber(
      @Parameter(description = "Input elnummer", required = true) @RequestParam(value = "elnummer", required = true) final BigDecimal elnummer,
      @Parameter(description = "Input variant", required = true) @RequestParam("variant") final String variant)
      throws ParameterInvalidException, ResourceNotFoundException {

    // Validate the input parameters
    if ((elnummer == null) || !SSDAPIUtil.isValid(variant)) {
      throw new ParameterInvalidException();
    }

    return this.ssd2BCService.getSSD2BCInfoByBcNummerAndVariant(elnummer, variant);
  }


  @SuppressWarnings("javadoc")
  @Operation(summary = "Get the SSD2BC information by BC name", description = "Gets the SSD2BC details for the given BC name and variant")

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Success, returns SSD2BC Information", content = @Content(schema = @Schema(implementation = SSD2BCInfo.class))),
      @ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = RestResponse.class))),
      @ApiResponse(responseCode = "404", description = "Value not found for the given input parameters", content = @Content(schema = @Schema(implementation = RestResponse.class))) })
  @GetMapping("/getSSD2BCInfoByName")
  public SSD2BCInfo getSSD2BCInfoByBCName(
      @Parameter(description = "Input BC Name", required = true) @RequestParam(value = "bcname", required = true) final String bcName,
      @Parameter(description = "Input variant", required = true) @RequestParam(value = "variant", required = true) final String variant)
      throws ParameterInvalidException, ResourceNotFoundException {

    // Validate the input parameters
    if (!(SSDAPIUtil.isValid(bcName) && SSDAPIUtil.isValid(variant))) {
      throw new ParameterInvalidException();
    }
    return this.ssd2BCService.getSSD2BCInfoByBcNameAndVariant(bcName, variant);
  }

}
