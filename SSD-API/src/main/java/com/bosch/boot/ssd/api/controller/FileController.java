/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.controller;

import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bosch.boot.ssd.api.exception.FileChecksumException;
import com.bosch.boot.ssd.api.exception.ParameterInvalidException;
import com.bosch.boot.ssd.api.exception.ResourceNotFoundException;
import com.bosch.boot.ssd.api.model.io.RestResponse;
import com.bosch.boot.ssd.api.service.FileService;
import com.bosch.boot.ssd.api.util.MessageConstants;
import com.bosch.boot.ssd.api.util.SSDAPIUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @author GDH9COB
 */
@RestController
@Tag(name = "File Service API", description = "Enpoints for checking and downloading latest Syntax documents, User Guides and OSS Reports of Tools")
@RequestMapping("/fileservice")
public class FileController {

  private static final Logger logger = LoggerFactory.getLogger(FileController.class);

  @Autowired
  private FileService fileStorageService;

  @SuppressWarnings("javadoc")
  @Operation(summary = "Downloads Syntax documents, User Guides and OSS Reports of Tools", description = "Downloads the latest files." +
      "If checksum provided by user then checks if checksum is same. " +
      "If same returns error else downloads the latest file")

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Success, returns the downloaded files", content = @Content(schema = @Schema(implementation = byte.class))),
      @ApiResponse(responseCode = "204", description = "File Checksum same so no need to download file", content = @Content(schema = @Schema(implementation = RestResponse.class))),
      @ApiResponse(responseCode = "400", description = "Required input parameters missing", content = @Content(schema = @Schema(implementation = RestResponse.class))),
      @ApiResponse(responseCode = "404", description = "Requested file not found", content = @Content(schema = @Schema(implementation = RestResponse.class))) })
  @GetMapping("/downloadFile")
  public ResponseEntity<Object> getFile(
      @Parameter(description = "File name with extension (ex:file.pdf)", required = true) @RequestParam(value = MessageConstants.STR_FILE_NAME_WITH_EXTN, required = true) final String fileNameWithExtn,
      @Parameter(description = "Tool name that requests the file (ex: CheckSSD, SSD)", required = true) @RequestParam(value = MessageConstants.STR_TOOL_NAME, required = true) final String toolName,
      @Parameter(description = "Type of file (ex:Syntax, OSS)", required = true) @RequestParam(value = MessageConstants.STR_FILE_CATEGORY, required = true) final String fileCategory,
      @Parameter(description = "File checksum", required = false) @RequestParam(value = MessageConstants.STR_FILE_CHECK_SUM, required = false, defaultValue = "0") final Long fileCheckSum)
      throws ParameterInvalidException, FileChecksumException, ResourceNotFoundException {

    // Validate the input parameters
    if (!(SSDAPIUtil.isValid(fileNameWithExtn) && SSDAPIUtil.isValid(toolName) && SSDAPIUtil.isValid(fileCategory))) {
      throw new ParameterInvalidException(MessageConstants.REQUIRED_PARAMETERS_MISSING);
    }

    try {

      // Construct the file path and get the resource
      Resource resource = this.fileStorageService.getResource(fileNameWithExtn, toolName, fileCategory, logger);

      // Check for checksum if provided by the user with the file available in server
      boolean isIdenticalFiles = this.fileStorageService.checkIfIdenticalFiles(fileCheckSum, resource.getFile());
      if (isIdenticalFiles) {
        logger.error(MessageConstants.STR_FILE_IDENTICAL_MSG);
        return ResponseEntity.badRequest().build();
      }

      logger.info("File downloaded successfully -{}", resource.getFile().toPath());
      return ResponseEntity.status(HttpStatus.OK)
          .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(resource.getFile().toPath()))
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
          .body(resource);
    }
    catch (Exception ex) {
      logger.error(MessageConstants.FILE_NOT_FOUND, ex);
      return ResponseEntity.notFound().build();
    }
  }

}
