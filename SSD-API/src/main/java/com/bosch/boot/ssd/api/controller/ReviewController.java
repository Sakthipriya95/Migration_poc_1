/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bosch.boot.ssd.api.entity.TLdb2SsdCrwReview;
import com.bosch.boot.ssd.api.exception.FileStorageException;
import com.bosch.boot.ssd.api.exception.ParameterInvalidException;
import com.bosch.boot.ssd.api.exception.ResourceNotFoundException;
import com.bosch.boot.ssd.api.model.InputReviewDetails;
import com.bosch.boot.ssd.api.model.ReviewResult;
import com.bosch.boot.ssd.api.model.io.RestResponse;
import com.bosch.boot.ssd.api.repository.SSDReviewRepository;
import com.bosch.boot.ssd.api.service.ReviewService;
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
@Tag(name = "Review Service API", description = "Enpoints for updating review status and upload review pdf")
@RequestMapping("/reviewservice")
public class ReviewController {

  private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

  /**
   * Key to retrieve shared file path URL
   */
  @Value("${ssd.file_Upload_Path}")
  private String fileUploadPath;

  @Autowired
  private SSDReviewRepository ssdReviewRepository;

  @Autowired
  private ReviewService reviewService;

  @Value("${review.status}")
  private BigDecimal status;

  @SuppressWarnings("javadoc")
  @Operation(summary = "Updates the Review status and decision(optionally)", description = "Updates the review status and the decision and closed date (if provided)" +
      "Decision and review closed data will be updated only if the status is 5")

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Success, review status updated for the given review id", content = @Content(schema = @Schema(implementation = byte.class))),
      @ApiResponse(responseCode = "400", description = "Decision not set due to status is not 5", content = @Content(schema = @Schema(implementation = RestResponse.class))),
      @ApiResponse(responseCode = "404", description = "No data found for the given Review ID", content = @Content(schema = @Schema(implementation = RestResponse.class))) })
  @PostMapping("/updateReviewStatus")
  public ResponseEntity<ReviewResult> updateReviewStatus(
      @Parameter(description = "Review id of the review to be updated", required = true) @RequestParam(value = MessageConstants.REVIEW_ID, required = true) final String reviewId,
      @Parameter(description = "Status to be updated", required = true) @RequestParam(value = MessageConstants.STATUS, required = true) final BigDecimal status,
      @Parameter(description = "Decision to be updated", required = true) @RequestParam(value = MessageConstants.DECISION, required = false) final BigDecimal decision)
      throws ResourceNotFoundException {

    // check if review is available for the provided review id
    Optional<TLdb2SsdCrwReview> oReview = this.ssdReviewRepository.findByReviewId(reviewId);
    ReviewResult reviewResult = new ReviewResult();

    if (oReview.isPresent()) {
      try {
        TLdb2SsdCrwReview review = oReview.get();
        review.setStatus(status);

        // Decision and closed date is set only when status is 5 (Closed state)
        if (status.intValue() == 5) {
          if (decision != null) {
            review.setDecision(decision);
            // Set review closed date only when status is 5
            review.setReviewClosedDate(LocalDateTime.now());
          }
          else {
            // Throw error if decison is not available for status 5
            logger.error(MessageConstants.DECISION_SHOULD_BE_SET);
            throw new ParameterInvalidException(MessageConstants.DECISION_SHOULD_BE_SET);
          }
        }
        // Save the updated review

        this.ssdReviewRepository.save(review);
        logger.info(MessageConstants.REVIEW_STATUS_UPDATED_SUCCESSFULLY);
        reviewResult.setReturnMsg(MessageConstants.REVIEW_STATUS_UPDATED_SUCCESSFULLY);
        return new ResponseEntity<>(reviewResult, HttpStatus.OK);
      }
      catch (Exception e) {
        reviewResult.setReturnMsg(MessageConstants.ERROR_UPDATING_REVIEW);
        logger.error(MessageConstants.ERROR_UPDATING_REVIEW);
        if (!(e instanceof ResourceNotFoundException)) {
          return new ResponseEntity<>(reviewResult, HttpStatus.INTERNAL_SERVER_ERROR);
        }
      }
    }
    // Error due to review not available
    logger.error(MessageConstants.NO_REVIEW_AVAILABLE_FOR_REVIEW_ID);
    throw new ResourceNotFoundException(MessageConstants.NO_REVIEW_AVAILABLE_FOR_REVIEW_ID);
  }

  @SuppressWarnings("javadoc")
  @Operation(summary = "Uploads the review file to review team shared link", description = "Upload the review file under the folder created with ssdrv id in the review team link")

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Success, file uploaded in review team link", content = @Content(schema = @Schema(implementation = byte.class))),
      @ApiResponse(responseCode = "422", description = "File upload failed", content = @Content(schema = @Schema(implementation = String.class))) })
  @PostMapping("/uploadfile")
  public RestResponse uploadFile(
      @Parameter(description = "File created with SSDRvId in the review team shared link", required = true) @RequestParam("ssdrvid") final long ssdRvId,
      @Parameter(description = "Input review file", required = true) @RequestParam("file") final MultipartFile uploadFile)
      throws FileStorageException {

    // Create file location
    String fileLocation = SSDAPIUtil.joinString(File.separator, this.fileUploadPath, String.valueOf(ssdRvId),
        MessageConstants.STR_WORKPRODUCT, uploadFile.getOriginalFilename());
    logger.info("{} -Upload File path", fileLocation);

    try {
      // Upload the file to the file location
      writeToFile(uploadFile.getInputStream(), fileLocation);
      String successMsg = "File uploaded to: " + fileLocation;
      logger.info(successMsg);
      return new RestResponse(true, successMsg);
    }
    catch (IOException e) {
      String errorMsg = "Error uploading the file in " + fileLocation;
      throw new FileStorageException(errorMsg, e);
    }
  }

  @SuppressWarnings("javadoc")
  @Operation(summary = "Create Review", description = "Invokes a client service from Compliance Review which creates a new review with the input provided.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = ReviewResult.class))),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class))) })
  @PostMapping(value = "/createReviewSSD")
  public ReviewResult createAndUpdateReview(@RequestBody final InputReviewDetails input)
      throws ResourceNotFoundException {

    ReviewResult reviewResult = this.reviewService.callCreateReviewService(input).getBody();

    if (reviewResult != null) {

      Optional<TLdb2SsdCrwReview> crwReview = this.ssdReviewRepository.findById(new BigDecimal(input.getReviewId()));

      if (crwReview.isPresent()) {
        TLdb2SsdCrwReview review = crwReview.get();

        if ((reviewResult.getReviewIDStr() == null) && (reviewResult.getReturnMsg() != null)) {
          review.setErrorDescription(reviewResult.getReturnMsg());
          review.setStatus(BigDecimal.valueOf(-1));// creation failed
        }
        else if (reviewResult.getReviewIDStr() != null) {

          review.setStatus(this.status);// in progress
          review.setReviewLink(reviewResult.getDirectLink());
          review.setReviewId(reviewResult.getReviewIDStr());
        }
        this.ssdReviewRepository.save(review);
        logger.info(MessageConstants.REVIEW_STATUS_UPDATED_SUCCESSFULLY);
      }
      else {

        logger.error(MessageConstants.NO_REVIEW_AVAILABLE_FOR_REVIEW_ID);
        throw new ResourceNotFoundException(MessageConstants.NO_REVIEW_AVAILABLE_FOR_REVIEW_ID);
      }
    }

    return reviewResult;
  }

  /**
   * @param uploadInputStream
   * @param fileLocation
   * @throws IOException
   */
  private void writeToFile(final InputStream uploadInputStream, final String fileLocation) throws IOException {

    File file = SSDAPIUtil.createDirectory(fileLocation);

    try (OutputStream out = new FileOutputStream(file)) {
      int read = 0;
      byte[] bytes = new byte[1024];

      while ((read = uploadInputStream.read(bytes)) != -1) {
        out.write(bytes, 0, read);
      }
      out.flush();
    }
  }
}
