/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.util;

/**
 * @author GDH9COB
 */
@SuppressWarnings("javadoc")
public class MessageConstants {

  private MessageConstants() {}


  public static final String REVIEW_STATUS_UPDATED_SUCCESSFULLY = "Review status is updated successfully";
  public static final String ERROR_UPDATING_REVIEW = "Error updating the review";
  public static final String NO_REVIEW_AVAILABLE_FOR_REVIEW_ID = "No review is available for the review id";
  public static final String DECISION_SHOULD_BE_SET = "Decision should be set if status is set to 5";
  public static final String STR_WORKPRODUCT = "WorkProduct";
  public static final String REVIEW_ID = "rvwID";
  public static final String STATUS = "status";
  public static final String DECISION = "decision";
  public static final String STR_FILE_NAME_WITH_EXTN = "fileNameWithExtn";
  public static final String STR_FILE_CATEGORY = "fileCategory";
  public static final String STR_TOOL_NAME = "toolName";
  public static final String STR_FILE_CHECK_SUM = "fileCheckSum";
  public static final String STR_FILE_IDENTICAL_MSG = "File is identical. Same file can be used";

  public static final String MSG_DOWNLOADING_FILE = "Downloading file...";
  public static final String ERROR_DOWNLOADING_FILE = "Error downloading the file";
  public static final String FILE_NOT_FOUND = "File Not found";
  public static final String REQUIRED_PARAMETERS_MISSING = "Required parameters missing";
  public static final String STR_MASTER_FILE_DIR = "MASTER_FILE_DIR";
  public static final String STR_UPLOAD_FILE_DIR = "FILE_LOC";
  public static final String CHECKLIST_NAMES = "SSD - Compliance Rules";
  
  public static final String NO_USERS_FOR_GIVEN_ROLE = "No users for the given role ID.";
  public static final String ERROR_FETCHING_USERS = "Error fetching users from IDM";
}
