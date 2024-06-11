/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bosch.boot.ssd.api.exception.ParameterInvalidException;
import com.bosch.boot.ssd.api.exception.ResourceNotFoundException;
import com.bosch.boot.ssd.api.model.SSD2BCInfo;
import com.bosch.boot.ssd.api.model.io.RestResponse;
import com.bosch.boot.ssd.api.model.query.Filter;
import com.bosch.boot.ssd.api.service.SSDFileEditorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * This Controller is used to provide information for the SSDFileEditor
 *
 * @author GDH9COB
 */
@RestController
@Tag(name = "SSD File Editor API", description = "Enpoints for getting the labels with category")
@RequestMapping("/customeditorservice")
public class SSDFileEditorController {

  @Autowired
  private SSDFileEditorService ssdFileEditorService;

  @SuppressWarnings("javadoc")
  @Operation(summary = "Lists the labels with category", description = "For the given list of labels, the api returns the labels with category ")

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Success, returns labels with category", content = @Content(schema = @Schema(implementation = SSD2BCInfo.class))),
      @ApiResponse(responseCode = "400", description = "Input label list is missing", content = @Content(schema = @Schema(implementation = RestResponse.class))),
      @ApiResponse(responseCode = "404", description = "Value not found for the given input parameters", content = @Content(schema = @Schema(implementation = RestResponse.class))) })
  @PostMapping(value = "/getLabelCategory")
  public ResponseEntity<Map<String, String>> getLabelCategory(
      @Parameter(description = "Label list", required = true) @RequestBody final List<String> labelList)
      throws ParameterInvalidException, ResourceNotFoundException {

    // Validate the input parameter
    if ((labelList == null) || labelList.isEmpty()) {
      throw new ParameterInvalidException();
    }

    Map<String, String> labelWithCategory = new HashMap<>();
    int listSize = 1000;
    AtomicInteger counter = new AtomicInteger();

    // Partition the list with size of 1000
    Collection<List<String>> partitions =
        labelList.stream().collect(Collectors.groupingBy(index -> counter.getAndDecrement() / listSize)).values();

    // iterate the sublists and get the category for the labels
    for (List<String> subList : partitions) {
      Filter filter = new Filter();
      filter.setLabelName(subList);

      labelWithCategory.putAll(this.ssdFileEditorService.getLabelByType(filter));
    }


    if (!labelWithCategory.isEmpty()) {
      return ResponseEntity.status(HttpStatus.OK).body(labelWithCategory);
    }
    throw new ResourceNotFoundException();
  }

}
