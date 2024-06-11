/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.util;


/**
 * @author TUD1COB
 */
import org.junit.Assert;
import org.junit.Test;

/**
 * @author TUD1COB
 */
public class MessageConstantsTest {

  /**
   *
   */
  @Test
  public void testReviewStatusUpdatedSuccessfully() {
    Assert.assertEquals("Review status is updated successfully", MessageConstants.REVIEW_STATUS_UPDATED_SUCCESSFULLY);
  }

  /**
   *
   */
  @Test
  public void testErrorUpdatingReview() {
    Assert.assertEquals("Error updating the review", MessageConstants.ERROR_UPDATING_REVIEW);
  }

  /**
   *
   */
  @Test
  public void testNoReviewAvailableForReviewId() {
    Assert.assertEquals("No review is available for the review id", MessageConstants.NO_REVIEW_AVAILABLE_FOR_REVIEW_ID);
  }

  /**
   *
   */
  @Test
  public void testDecisionShouldBeSet() {
    Assert.assertEquals("Decision should be set if status is set to 5", MessageConstants.DECISION_SHOULD_BE_SET);
  }

  /**
   *
   */
  @Test
  public void testStrWorkProduct() {
    Assert.assertEquals("WorkProduct", MessageConstants.STR_WORKPRODUCT);
  }

  /**
   *
   */
  @Test
  public void testReviewId() {
    Assert.assertEquals("rvwID", MessageConstants.REVIEW_ID);
  }

  /**
   *
   */
  @Test
  public void testStatus() {
    Assert.assertEquals("status", MessageConstants.STATUS);
  }

  /**
   *
   */
  @Test
  public void testDecision() {
    Assert.assertEquals("decision", MessageConstants.DECISION);
  }

  /**
   *
   */
  @Test
  public void testStrFileNameWithExtn() {
    Assert.assertEquals("fileNameWithExtn", MessageConstants.STR_FILE_NAME_WITH_EXTN);
  }

  /**
   *
   */
  @Test
  public void testStrFileCategory() {
    Assert.assertEquals("fileCategory", MessageConstants.STR_FILE_CATEGORY);
  }

  /**
   *
   */
  @Test
  public void testStrToolName() {
    Assert.assertEquals("toolName", MessageConstants.STR_TOOL_NAME);
  }

  /**
   *
   */
  @Test
  public void testStrFileCheckSum() {
    Assert.assertEquals("fileCheckSum", MessageConstants.STR_FILE_CHECK_SUM);
  }

  /**
   *
   */
  @Test
  public void testStrFileIdenticalMsg() {
    Assert.assertEquals("File is identical. Same file can be used", MessageConstants.STR_FILE_IDENTICAL_MSG);
  }

  /**
   *
   */
  @Test
  public void testMsgDownloadingFile() {
    Assert.assertEquals("Downloading file...", MessageConstants.MSG_DOWNLOADING_FILE);
  }

  /**
   *
   */
  @Test
  public void testErrorDownloadingFile() {
    Assert.assertEquals("Error downloading the file", MessageConstants.ERROR_DOWNLOADING_FILE);
  }

  /**
   *
   */
  @Test
  public void testFileNotFound() {
    Assert.assertEquals("File Not found", MessageConstants.FILE_NOT_FOUND);
  }

  /**
   *
   */
  @Test
  public void testRequiredParametersMissing() {
    Assert.assertEquals("Required parameters missing", MessageConstants.REQUIRED_PARAMETERS_MISSING);
  }

  /**
   *
   */
  @Test
  public void testStrMasterFileDir() {
    Assert.assertEquals("MASTER_FILE_DIR", MessageConstants.STR_MASTER_FILE_DIR);
  }

  /**
   *
   */
  @Test
  public void testStrUploadFileDir() {
    Assert.assertEquals("FILE_LOC", MessageConstants.STR_UPLOAD_FILE_DIR);
  }

  /**
   *
   */
  @Test
  public void testChecklistNames() {
    Assert.assertEquals("SSD - Compliance Rules", MessageConstants.CHECKLIST_NAMES);
  }

  /**
   *
   */
  @Test
  public void testNoUsersForGivenRoleId() {
    Assert.assertEquals("No users for the given role ID.", MessageConstants.NO_USERS_FOR_GIVEN_ROLE);
  }

  /**
   *
   */
  @Test
  public void testErrorFetchingUsers() {
    Assert.assertEquals("Error fetching users from IDM", MessageConstants.ERROR_FETCHING_USERS);
  }

}
