/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic;


/**
 * @author jvi6cob
 */
public interface IImportConstants {

  // Column Index zero-inclusive
  public static final int ATTRIBUTE_USED_FLAG_COLUMN_NUM = 4;
  public static final int ATTRIBUTE_VALUE_COLUMN_NUM = 5;
  public static final int ATTR_PARTNUM_COL = 7;
  public static final int ATTRIBUTE_SPECLINK_COLUMN_NUM = 8;
  public static final int ATTRIBUTE_DESC_COLUMN_NUM = 9;
  public static final int ATTRIBUTE_ID_COL_NUM = 11;
  public static final short LAST_COLUMN_INDEX = 12;

  // Invalid Messages
  public static final String INVALID_VALUE_DEPENDENCY = "Value Dependency not met for ";
  public static final String INVALID_ATTRIBUTE_DEPENDENCY = "Attribute Dependency not met for ";
  public static final String INVALID_ATTRIBUTE_VALUE_TYPE = "Value-Type found invalid for Attribute";
  public static final String INVALID_COMBINATION = "Invalid Used-Flag,Value Combination";
  public static final String ATTR_NOT_FOUND = "Attribute not found in Existing PIDCard";
  public static final String INVALID_MOD_EDIT_NOT_ALLOWED = "Invalid Modification(Edit not Allowed)";
  public static final String INVALID_SPEC_EDIT_NOT_ALLOWED = "Invalid Modification(Spec Edit not Allowed)";
  // New Attribute
  public static final String NEW_ATTRIBUTE_FOUND = "New Attribute found(existing)";

  // Modified messages
  public static final String MODIFIED_COMMENT = "Modified with existing values";
  public static final String ADDING_VALUE_TO_EXISTING = "Added new value to existing values";
  public static final String ADDING_VALUE_FIRST_TIME = "Added new value";
  public static final String FLAG_ONLY_CHANGED = "Modified used flag";
  public static final String ADDINFO_ONLY_CHANGED = "Additional Info Changed";

  // Input validation messages
  public static final String PID_CARD_ID_MISMATCH_ERROR_MSG = "Invalid PIDCard Import: Wrong PIDC input file";
  public static final String PIDC_ID_MISMATCH = "Invalid PIDCard Import: Wrong PIDC"; // TODO: replace
                                                                                      // PID_CARD_ID_MISMATCH_ERROR_MSG
  public static final String WRONG_IMPORT_FILE_ERROR_MSG =
      "Invalid PIDCard Import: Wrong input format. Kindly Contact iCDM Hotline for further details.";
  public static final String LANGUAGE_MISMATCH_ERROR_MSG = "Invalid PIDCard Import: Language Mismatch";

  public static final String NO_ATTRS_FOUND = "NO ATTRIBUTES FOUND IN EXISTING PID CARD";

}
