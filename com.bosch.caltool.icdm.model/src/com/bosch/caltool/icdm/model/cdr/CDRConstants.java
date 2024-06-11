/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.BitSet;

import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * @author adn1cob
 */
public final class CDRConstants {

  /**
   * string constant for "<FUNCTION>"
   */
  public static final String FUN_STR = "<FUNCTION>";

  /**
   * Text displayed when no variant exists for PIDC
   */
  public static final String NO_VARIANT = "NO-VARIANT";


  /**
   * Text displayed for variant name when cdfx export is performed from Resp/Wp nodes when there is NO VARIANT
   */
  public static final String NO_VARIANT_NAME = "<NO-VARIANT>";


  /**
   * CDFx File Name for export
   */
  public static final String CDFX_FILE_NAME = "cdfx_export";
  /**
   * Node ID for ICDM files from CDR
   */
  public static final long CDR_FILE_NODE_ID = 0L;
  // ICDM-2026
  /**
   * Multiple group
   */
  public static final String MUL_GRP = "<MGRP>";
  /**
   * Multiple fc2wp
   */
  public static final String MUL_FC2WP = "<MFC2WP>";
  /**
   * FC2WP type
   */
  public static final String FC2WP = "<FC2WP>";

  /**
   * WP Resp
   */
  public static final String MUL_WP = "<MWP>";

  /**
   * Group name of the review type tooltip description in T_MESSAGES table
   */
  public static final String REVIEW_TYPE_GROUP_NAME = "REVIEW_TYPE";

  /**
   * warning message asked to fill unfilled qnaire response before locking for official review
   */
  public static final String WARN_MSG_TO_FILL_UNFILLED_QNAIRE_RESP =
      "Please fill out all questionnaires before you lock a review that contains labels with score 8 or 9.";
  /**
   * warning message asked to fill unfilled qnaire response before locking for Start review
   */
  public static final String WARN_MSG_TO_FILL_UNFILLED_QNAIRE_RESP_FOR_START_REVIEW =
      "Please fill out all questionnaires before you lock a review that contains labels with score 7 or 8.";

  /**
   * Open Braces
   */
  public static final String OPEN_BRACES = "(";

  /**
   * Close Braces
   */
  public static final String CLOSE_BRACES = ")";

  /**
   * Compli Result
   */
  public static final String COMPLI_RESULT = "Compli Result";

  /**
   * Compli Result
   */
  public static final String NOT_BASELINED = "Not Baselined Questionnaire Responses";

  /**
   * PVER name in SDOM
   */
  public static final String PVER_NAME_IN_SDOM = "PVER name in SDOM";

  /**
   * Key for User decimal limit preference
   */
  public static final String DECIMAL_PREF_LIMIT_KEY = "DECIMAL_PREF_LIMIT";

  /**
   * No Limit value for User decimal limit preference
   */
  public static final String DECIMAL_PREF_NO_LIMIT = "<NO_LIMIT>";

  /**
   * Display name for Robert Bosch Resp Type
   */
  public static final String RB_RESP_TYPE_DISP_NAME = "Robert Bosch";

  /**
   * Display name for Not Applicable in CDR Report Service
   */
  public static final String NOT_APPLICABLE_DISP_NAME = "N/A";


  /**
   * enum for Cdr Exception types
   *
   * @author rgo7cob
   * @deprecated not used
   */
  @Deprecated
  public enum CDR_EXCEPTION_TYPE {

                                  /**
                                   * Parser Exception
                                   */
                                  PARSER_EXCEPTION("Error occured while parsing the files to be reviewed !"),
                                  /**
                                   * Parser Exception
                                   */
                                  HIDDEN_EXCEPTION(
                                                   "Review cannot be done since there are hidden attribute(s) in the  project id card . "),
                                  /**
                                   * No param Selected Exception
                                   */
                                  NO_PARAM_SELECTED("None of the paramaters found in input file(s) selected!"),
                                  /**
                                   * Param Repeats Exception
                                   */
                                  PARAM_REPEATS(
                                                "Parameters are repeated in multiple files! \nDo you want an excel report?"),
                                  /**
                                   * No params available in A2l.
                                   */
                                  PARAMS_NOT_AVAILABLE(
                                                       "None of the parameters selected for review are present in the A2l File and review cannot be done."),
                                  /**
                                   * Feature Value model Exception
                                   */
                                  FEA_VALUE_EXCEPTION(""),


                                  /**
                                   * Exception when downloading the input files
                                   */
                                  DOWNLOAD_INP_FILE_EXCEPTION("Exception in downloading the input files"),
                                  /**
                                   * Check SSD exception
                                   */
                                  CHECK_SSD_EXCEPTION(""),

                                  /**
                                   * Check SSD exception
                                   */
                                  VECTOR_CHANNEL_SSD_EXCEPTION(
                                                               "Exception while removeing channel and vector from SSD file"),

                                  /**
                                   * A2l file mismatch exception
                                   */
                                  A2L_FILE_MONICA_MISMATCH(
                                                           "The a2l file in the MoniCa report excel is different from the selected excel"),
                                  /**
                                   * Feature Value model Exception
                                   */
                                  RULE_SET_EXCEPION("None of the parameter selected is available in Rule set"),

                                  /**
                                   * ERROR during creation of SSD file of compliance parameters
                                   */
                                  SSD_COMPLIANCE_EXCEPTION(
                                                           "Error during creation of SSD file for compliance parameters");

    final String exceptionStr;

    CDR_EXCEPTION_TYPE(final String exceptionStr) {
      this.exceptionStr = exceptionStr;
    }

    /**
     * @return ParticipantType
     */
    public final String getExceptionStr() {
      return this.exceptionStr;
    }

  }

  /**
   * CDReview Participant types
   */
  public enum REVIEW_USER_TYPE {

                                /**
                                 * CalibrationEngineer
                                 */
                                CAL_ENGINEER("C"),
                                /**
                                 * Auditor
                                 */
                                AUDITOR("A"),
                                /**
                                 * AdditionalParticipants
                                 */
                                ADDL_PARTICIPANT("P");

    final String dbType;

    REVIEW_USER_TYPE(final String dbType) {
      this.dbType = dbType;
    }

    /**
     * @return ParticipantType
     */
    public final String getDbType() {
      return this.dbType;
    }

    /**
     * Return the type object for the given db type
     *
     * @param dbType db literal of type
     * @return the user type object
     */
    public static REVIEW_USER_TYPE getType(final String dbType) {
      for (REVIEW_USER_TYPE type : REVIEW_USER_TYPE.values()) {
        if (type.dbType.equals(dbType)) {
          return type;
        }
      }
      return null;
    }
  }

  /**
   * Delta review types
   */
  public enum DELTA_REVIEW_TYPE {

                                 /**
                                  * delta review against a project
                                  */
                                 DELTA_REVIEW("D"),
                                 /**
                                  * delta review against project data
                                  */
                                 PROJECT_DELTA_REVIEW("P");

    final String dbType;

    DELTA_REVIEW_TYPE(final String dbType) {
      this.dbType = dbType;
    }

    /**
     * @return ParticipantType
     */
    public final String getDbType() {
      return this.dbType;
    }

    /**
     * Return the type object for the given db type
     *
     * @param dbType db literal of type
     * @return the user type object
     */
    public static DELTA_REVIEW_TYPE getType(final String dbType) {
      for (DELTA_REVIEW_TYPE type : DELTA_REVIEW_TYPE.values()) {
        if (type.dbType.equals(dbType)) {
          return type;
        }
      }
      return null;
    }
  }

  /**
   * @author rgo7cob Enum For the Source Type Icdm-729 New Enum for CDR Source Type
   */
  public enum CDR_SOURCE_TYPE {

                               /**
                                * Lab file
                                */
                               LAB_FILE("LAB", "<LAB>", "<LAB>"),
                               /**
                                * Fun File
                                */
                               FUN_FILE("FUN", FUN_STR, FUN_STR),

                               /**
                                * Fc to WP Mapping
                                */
                               WORK_PACKAGE("FC_WP", "", "<WORK_PACKAGE>"),

                               /**
                                * WP Resp
                                */
                               WP("WP", "", "<WP>"),

                               /**
                                * Group Mapping
                                */
                               GROUP("GRP", "", "<GROUP>"),

                               /**
                                * Review File
                                */
                               REVIEW_FILE("RF", "<REVIEWED_FILE>", "<REVIEWED_FILE>"),

                               /**
                                * Not defined
                                */
                               NOT_DEFINED("NOT_DEF", "<NOT_DEFINED>", "<NOT_DEFINED>"),
                               /**
                                * a2l file
                                */
                               A2L_FILE("A2L", FUN_STR, FUN_STR),

                               /**
                                * a2l file
                                */
                               MONICA_FILE("MONICA", "<MONICA_REPORT>", "<MONICA_REPORT>"),

                               /**
                                * Compliance parameters
                                */
                               COMPLI_PARAM(CDRConstants.COMPLI, "<COMPLI_PARAM>", "<COMPLI_PARAM>");


    final String dbType;
    final String treeDispText;
    final String uiType;

    CDR_SOURCE_TYPE(final String dbType, final String treeDispStr, final String uiType) {
      this.dbType = dbType;
      this.treeDispText = treeDispStr;
      this.uiType = uiType;
    }

    /**
     * @return ParticipantType
     */
    public final String getDbType() {
      return this.dbType;
    }

    /**
     * @return ParticipantType
     */
    public final String getUIType() {
      return this.uiType;
    }

    /**
     * Icdm-729
     *
     * @return the Tree Display Type
     */
    public String getTreeDispName() {
      return this.treeDispText;
    }

    /**
     * Return the type object for the given db type
     *
     * @param dbType db literal of type
     * @return the user type object
     */
    public static CDR_SOURCE_TYPE getType(final String dbType) {
      for (CDR_SOURCE_TYPE type : CDR_SOURCE_TYPE.values()) {
        if (type.dbType.equals(dbType)) {
          return type;
        }
      }
      return null;
    }

  }

  /**
   * CDReview File types
   */
  public enum REVIEW_FILE_TYPE {

                                /**
                                 * Input file
                                 */
                                INPUT("I"),
                                /**
                                 * Output file
                                 */
                                OUTPUT("O"),
                                /**
                                 * Output file
                                 */
                                RULE("R"),
                                /**
                                 * Additional Files
                                 */
                                RVW_ADDL_FILE("RA"),
                                /**
                                 * Parameter Additional Files
                                 */
                                RVW_PRM_ADDL_FILE("PA"),

                                /**
                                 * Function File
                                 */
                                FUNCTION_FILE("FUN"),
                                /**
                                 * Lab File
                                 */
                                LAB_FILE("LAB"),

                                /**
                                 * MoniCa File type
                                 */
                                MONICA_FILE("M");


    final String dbType;

    REVIEW_FILE_TYPE(final String dbType) {
      this.dbType = dbType;
    }

    /**
     * @return fileType
     */
    public final String getDbType() {
      return this.dbType;
    }

    /**
     * Return the file type object for the given db type
     *
     * @param dbType db literal of type
     * @return the file type object
     */
    public static REVIEW_FILE_TYPE getType(final String dbType) {
      for (REVIEW_FILE_TYPE type : REVIEW_FILE_TYPE.values()) {
        if (type.dbType.equals(dbType)) {
          return type;
        }
      }
      return null;
    }
  }

  /**
   * CDReview Reviewed flag
   */
  public enum REVIEW_FLAG {

                           /**
                            * Reviewed
                            */
                           YES(ApicConstants.CODE_YES),
                           /**
                            * Not reviewed
                            */
                           NO(ApicConstants.CODE_NO);

    final String dbType;

    REVIEW_FLAG(final String dbType) {
      this.dbType = dbType;
    }

    /**
     * @return ReviwedType
     */
    public final String getDbType() {
      return this.dbType;
    }
  }

  /**
   * @author rgo7cob
   */
  public enum RULE_SOURCE {

                           /**
                            * Common rules
                            */
                           COMMON_RULES("Common Rules", "C"),
                           /**
                            * Rule set
                            */
                           RULE_SET("Rule Set", "R"),
                           /**
                            * ssd release
                            */
                           SSD_RELEASE("SSD Release", "S"),

                           /**
                            * ssd file
                            */
                           SSD_FILE("SSD File", "F");

    private final String uiVal;

    /**
     * @return the uiVal
     */
    public String getUiVal() {
      return this.uiVal;
    }


    /**
     * @return the dbVal
     */
    public String getDbVal() {
      return this.dbVal;
    }

    private final String dbVal;

    RULE_SOURCE(final String uiVal, final String dbVal) {
      this.uiVal = uiVal;
      this.dbVal = dbVal;
    }

    /**
     * @param dbvalue dbvalue
     * @return the db val
     */
    public static RULE_SOURCE getSource(final String dbvalue) {
      for (RULE_SOURCE source : RULE_SOURCE.values()) {
        if (source.dbVal.equals(dbvalue)) {
          return source;
        }
      }
      return null;
    }

  }


  /**
   * CDReview Result value (ok, high, low)
   */
  public enum RESULT_FLAG {

                           /**
                            * Ok
                            */
                           OK("O", "OK"),
                           /**
                            * Not Ok
                            */
                           NOT_OK("N", "Not OK"),
                           /**
                            * Low
                            */
                           LOW("L", "Low"),
                           /**
                            * High
                            */
                           HIGH("H", "High"),
                           /**
                            * Not reviewed
                            */
                           NOT_REVIEWED("", "???"),
                           /**
                            * Shape
                            */
                           SHAPE("S", "SHAPE"),

                           /**
                            * checked value update purpose
                            */
                           CHECKED("P", "CHECKED");

    private final String dbType;
    private final String uiType;

    RESULT_FLAG(final String dbType, final String uiType) {
      this.dbType = dbType;
      this.uiType = uiType;
    }

    /**
     * @return Result Value db literal
     */
    public final String getDbType() {
      return this.dbType;
    }

    /**
     * @return UI type
     */
    public final String getUiType() {
      return this.uiType;
    }

    /**
     * Return the review flag object for the given db type
     *
     * @param dbType db literal of flag
     * @return the flag object
     */
    public static RESULT_FLAG getType(final String dbType) {
      for (RESULT_FLAG type : RESULT_FLAG.values()) {
        if (type.dbType.equals(dbType)) {
          return type;
        }
      }
      return NOT_REVIEWED;
    }

  }

  /**
   * compli result flag values
   */
  public enum COMPLI_RESULT_FLAG {

                                  /**
                                   * Ok
                                   */
                                  OK("O", "OK"),
                                  /**
                                   * No Rule
                                   */
                                  NO_RULE("N", "NO RULE"),
                                  /**
                                   * Compli Failed - usecase type cssd
                                   */
                                  CSSD("C", "C-SSD"),
                                  /**
                                   * Compli Failed - usecase type ssd2rv
                                   */
                                  SSD2RV("S", "SSD2Rv");

    private final String dbType;
    private final String uiType;

    COMPLI_RESULT_FLAG(final String dbType, final String uiType) {
      this.dbType = dbType;
      this.uiType = uiType;
    }

    /**
     * @return Result Value db literal
     */
    public final String getDbType() {
      return this.dbType;
    }

    /**
     * @return UI type
     */
    public final String getUiType() {
      return this.uiType;
    }

    /**
     * Return the review flag object for the given db type
     *
     * @param dbType db literal of flag
     * @return the flag object
     */
    public static COMPLI_RESULT_FLAG getType(final String dbType) {
      for (COMPLI_RESULT_FLAG type : COMPLI_RESULT_FLAG.values()) {
        if (type.dbType.equals(dbType)) {
          return type;
        }
      }
      return null;
    }

    /**
     * Return the review flag object for the given db type
     *
     * @param uiType UI literal of flag
     * @return the flag object
     */
    public static COMPLI_RESULT_FLAG getTypeFromUiType(final String uiType) {
      for (COMPLI_RESULT_FLAG type : COMPLI_RESULT_FLAG.values()) {
        if (type.uiType.equals(uiType)) {
          return type;
        }
      }
      return null;
    }

  }


  /**
   * compli result flag values
   */
  public enum QSSD_RESULT_FLAG {

                                /**
                                 * Ok
                                 */
                                OK("O", "OK"),
                                /**
                                 * No Rule
                                 */
                                NO_RULE("N", "NO RULE"),
                                /**
                                 * Qssd type
                                 */
                                QSSD("Q", "Q-SSD");

    private final String dbType;
    private final String uiType;

    QSSD_RESULT_FLAG(final String dbType, final String uiType) {
      this.dbType = dbType;
      this.uiType = uiType;
    }

    /**
     * @return Result Value db literal
     */
    public final String getDbType() {
      return this.dbType;
    }

    /**
     * @return UI type
     */
    public final String getUiType() {
      return this.uiType;
    }

    /**
     * Return the review flag object for the given db type
     *
     * @param dbType db literal of flag
     * @return the flag object
     */
    public static QSSD_RESULT_FLAG getType(final String dbType) {
      for (QSSD_RESULT_FLAG type : QSSD_RESULT_FLAG.values()) {
        if (type.dbType.equals(dbType)) {
          return type;
        }
      }
      return null;
    }

    /**
     * Return the review flag object for the given db type
     *
     * @param uiType UI literal of flag
     * @return the flag object
     */
    public static QSSD_RESULT_FLAG getTypeFromUiType(final String uiType) {
      for (QSSD_RESULT_FLAG type : QSSD_RESULT_FLAG.values()) {
        if (type.uiType.equals(uiType)) {
          return type;
        }
      }
      return null;
    }

  }

  /**
   * CD Review Type (S - Start, O-Official, T-Test)
   */
  public enum REVIEW_TYPE {

                           /**
                            * Test review
                            */
                           TEST("T", "Test"),
                           /**
                            * Official Review
                            */
                           OFFICIAL("O", "Official"),
                           /**
                            * Start
                            */
                           START("S", "Start");

    private final String dbType;
    private final String uiType;

    REVIEW_TYPE(final String dbType, final String uiType) {
      this.dbType = dbType;
      this.uiType = uiType;
    }

    /**
     * @return Result Value db literal
     */
    public final String getDbType() {
      return this.dbType;
    }

    /**
     * @return UI type
     */
    public final String getUiType() {
      return this.uiType;
    }

    /**
     * Return the review type object for the given db type
     *
     * @param dbType db literal of flag
     * @return the flag object
     */
    public static REVIEW_TYPE getType(final String dbType) {
      if (OFFICIAL.getDbType().equals(dbType)) {
        return OFFICIAL;
      }
      else if (START.getDbType().equals(dbType)) {
        return START;
      }
      return TEST;
    }
  }


  /**
   * Review Lock Status.
   */
  public enum REVIEW_LOCK_STATUS {

                                  /**
                                   * Review Is locked
                                   */
                                  YES(ApicConstants.CODE_YES, "Locked"),
                                  /**
                                   * Review is not locked
                                   */
                                  NO(ApicConstants.CODE_NO, "Not Locked");

    private final String dbType;
    private final String uiType;

    REVIEW_LOCK_STATUS(final String dbType, final String uiType) {
      this.dbType = dbType;
      this.uiType = uiType;
    }

    /**
     * @return Result Value db literal
     */
    public final String getDbType() {
      return this.dbType;
    }

    /**
     * @return UI type
     */
    public final String getUiType() {
      return this.uiType;
    }

    /**
     * Return the review status object for the given db type
     *
     * @param dbType db literal of flag
     * @return the flag object
     */
    public static REVIEW_LOCK_STATUS getType(final String dbType) {
      for (REVIEW_LOCK_STATUS type : REVIEW_LOCK_STATUS.values()) {
        if (type.dbType.equals(dbType)) {
          return type;
        }
      }
      // By default not locked
      return NO;
    }
  }

  /**
   * CDReview Status (Open, In-Progress, Closed) //iCDM-665
   */
  public enum REVIEW_STATUS {

                             /**
                              * Open
                              */
                             OPEN("O", "Open"),
                             /**
                              * In progress
                              */
                             IN_PROGRESS("I", "In Progress"),
                             /**
                              * Closed
                              */
                             CLOSED("C", "Closed");

    private final String dbType;
    private final String uiType;

    REVIEW_STATUS(final String dbType, final String uiType) {
      this.dbType = dbType;
      this.uiType = uiType;
    }

    /**
     * @return Result Value db literal
     */
    public final String getDbType() {
      return this.dbType;
    }

    /**
     * @return UI type
     */
    public final String getUiType() {
      return this.uiType;
    }

    /**
     * Return the review status object for the given db type
     *
     * @param dbType db literal of flag
     * @return the flag object
     */
    public static REVIEW_STATUS getType(final String dbType) {
      for (REVIEW_STATUS type : REVIEW_STATUS.values()) {
        if (type.dbType.equals(dbType)) {
          return type;
        }
      }
      // In progress, by default
      return IN_PROGRESS;
    }

  }

  /**
   * Question Result Type
   */
  public enum QS_RESULT_TYPE {

                              /**
                               * Test review
                               */
                              YES("Y", "Yes"),
                              /**
                               * Official Review
                               */
                              NO("N", "No"),
                              /**
                               * Start
                               */
                              FINISHED("F", CDRConstants.FINISHED_MARKER);

    private final String dbType;
    private final String uiType;

    QS_RESULT_TYPE(final String dbType, final String uiType) {
      this.dbType = dbType;
      this.uiType = uiType;
    }

    /**
     * @return Result Value db literal
     */
    public final String getDbType() {
      return this.dbType;
    }

    /**
     * @return UI type
     */
    public final String getUiType() {
      return this.uiType;
    }

    /**
     * Return the Question Result Type for the given db type
     *
     * @param dbType db literal of flag
     * @return the flag object
     */
    public static QS_RESULT_TYPE getTypeByDbCode(final String dbType) {
      // FINISHED, by default
      QS_RESULT_TYPE ret = FINISHED;

      for (QS_RESULT_TYPE type : QS_RESULT_TYPE.values()) {
        if (type.dbType.equals(dbType)) {
          ret = type;
          break;
        }
      }

      return ret;
    }

    /**
     * Return the Question Result Type for the given ui type
     *
     * @param uiType ui literal of flag
     * @return the flag object
     */
    public static QS_RESULT_TYPE getTypeByUiText(final String uiType) {
      // FINISHED, by default
      QS_RESULT_TYPE ret = FINISHED;

      for (QS_RESULT_TYPE type : QS_RESULT_TYPE.values()) {
        if (type.uiType.equals(uiType)) {
          ret = type;
          break;
        }
      }

      return ret;
    }

  }

  /**
   * Assesment type for Questionnaire
   */
  public enum QS_ASSESMENT_TYPE {

                                 /**
                                  * For positive result
                                  */
                                 POSITIVE("P", "Positive"),
                                 /**
                                  * For negative result
                                  */
                                 NEGATIVE("N", "Negative"),
                                 /**
                                  * For the result neither positive nor negative
                                  */
                                 NEUTRAL("-", "Neutral");

    private final String dbType;
    private final String uiType;

    QS_ASSESMENT_TYPE(final String dbType, final String uiType) {
      this.dbType = dbType;
      this.uiType = uiType;
    }

    /**
     * @return the dbType
     */
    public String getDbType() {
      return this.dbType;
    }

    /**
     * @return the uiType
     */
    public String getUiType() {
      return this.uiType;
    }

    /**
     * Return the Question Result Type for the given db type
     *
     * @param dbType db literal of flag
     * @return the flag object
     */
    public static QS_ASSESMENT_TYPE getTypeByDbCode(final String dbType) {

      QS_ASSESMENT_TYPE ret = null;

      for (QS_ASSESMENT_TYPE type : QS_ASSESMENT_TYPE.values()) {
        if (type.dbType.equals(dbType)) {
          ret = type;
          break;
        }
      }

      return ret;
    }

    /**
     * Return the Question Result Type for the given ui type
     *
     * @param uiType ui literal of flag
     * @return the flag object
     */
    public static QS_ASSESMENT_TYPE getTypeByUiText(final String uiType) {
      QS_ASSESMENT_TYPE ret = null;

      for (QS_ASSESMENT_TYPE type : QS_ASSESMENT_TYPE.values()) {
        if (type.uiType.equals(uiType)) {
          ret = type;
          break;
        }
      }

      return ret;
    }

  }

  /**
   * Workpackage Responsibilty Status for Finished / Not Finished
   *
   * @author say8cob
   */
  public enum WP_RESP_STATUS_TYPE {

                                   /**
                                    * For Finished Workpackage Responsibility
                                    */
                                   FINISHED("F", "Workpackage Responsibility finished."),
                                   /**
                                    * For Not Finished Workpackage Responsibility
                                    */
                                   NOT_FINISHED("N", "Workpackage Responsibilty not finished.");

    private final String dbType;
    private final String uiType;

    WP_RESP_STATUS_TYPE(final String dbType, final String uiType) {
      this.dbType = dbType;
      this.uiType = uiType;
    }

    /**
     * @return the dbType
     */
    public String getDbType() {
      return this.dbType;
    }

    /**
     * @return the uiType
     */
    public String getUiType() {
      return this.uiType;
    }

    /**
     * Return the Question Result Type for the given db type
     *
     * @param dbType db literal of flag
     * @return the flag object
     */
    public static WP_RESP_STATUS_TYPE getTypeByDbCode(final String dbType) {

      WP_RESP_STATUS_TYPE ret = null;

      for (WP_RESP_STATUS_TYPE type : WP_RESP_STATUS_TYPE.values()) {
        if (type.dbType.equals(dbType)) {
          ret = type;
          break;
        }
      }

      return ret;
    }

    /**
     * Return the Question Result Type for the given ui type
     *
     * @param uiType ui literal of flag
     * @return the flag object
     */
    public static WP_RESP_STATUS_TYPE getTypeByUiText(final String uiType) {
      WP_RESP_STATUS_TYPE ret = null;

      for (WP_RESP_STATUS_TYPE type : WP_RESP_STATUS_TYPE.values()) {
        if (type.uiType.equals(uiType)) {
          ret = type;
          break;
        }
      }

      return ret;
    }
  }

  /**
   * Questionnaire response Status type<br>
   * Note: Whenever a new Type is introduced, add that type to {DA_QS_STATUS_TYPE}
   */
  public enum QS_STATUS_TYPE {

                              /**
                               * For positive result
                               */
                              ALL_POSITIVE("P", "All questions are answered. No negative answers."),
                              /**
                               * For negative result
                               */
                              NOT_ALL_POSITIVE("N", "All answered, but with negative answers."),
                              /**
                               * For Answers that doesnt allow to finish WP
                               */
                              NOT_ALLOWED_FINISHED_WP(
                                                      "F",
                                                      "All answered, but with answers that doesn't allow to finish WP"),
                              /**
                               * For the result neither positive nor negative
                               */
                              NOT_ANSWERED("-", "Not all answered."),

                              /**
                               * No negative answered in questionnaire
                               */
                              NOT_ALLOW_NEGATIVE(
                                                 "Q",
                                                 "All answered, but with negative answers - This will NOT allow to mark Work Package as Finished.");


    private final String dbType;
    private final String uiType;

    QS_STATUS_TYPE(final String dbType, final String uiType) {
      this.dbType = dbType;
      this.uiType = uiType;
    }

    /**
     * @return the dbType
     */
    public String getDbType() {
      return this.dbType;
    }

    /**
     * @return the uiType
     */
    public String getUiType() {
      return this.uiType;
    }

    /**
     * Return the Question Result Type for the given db type
     *
     * @param dbType db literal of flag
     * @return the flag object
     */
    public static QS_STATUS_TYPE getTypeByDbCode(final String dbType) {

      QS_STATUS_TYPE ret = null;

      for (QS_STATUS_TYPE type : QS_STATUS_TYPE.values()) {
        if (type.dbType.equals(dbType)) {
          ret = type;
          break;
        }
      }

      return ret;
    }

    /**
     * Return the Question Result Type for the given ui type
     *
     * @param uiType ui literal of flag
     * @return the flag object
     */
    public static QS_STATUS_TYPE getTypeByUiText(final String uiType) {
      QS_STATUS_TYPE ret = null;

      for (QS_STATUS_TYPE type : QS_STATUS_TYPE.values()) {
        if (type.uiType.equals(uiType)) {
          ret = type;
          break;
        }
      }

      return ret;
    }

  }

  /**
   * Questionnaire response Status type
   */
  public enum DA_QS_STATUS_TYPE {

                                 /**
                                  * For positive result
                                  */
                                 ALL_POSITIVE("P", "All questions are answered. No negative answers."),
                                 /**
                                  * For negative result
                                  */
                                 NOT_ALL_POSITIVE("N", "All answered, but with negative answers."),
                                 /**
                                  * For Answers that doesnt allow to finish WP
                                  */
                                 NOT_ALLOWED_FINISHED_WP(
                                                         "F",
                                                         "All answered, but with answers that doesn't allow to finish WP"),
                                 /**
                                  * For the result neither positive nor negative
                                  */
                                 NOT_ANSWERED("-", "Not all answered."),

                                 /**
                                  * Review Questionnaire is Not Baselined
                                  */
                                 NO_BASELINE("B", "No baseline existing for this work package."),

                                 /**
                                  * Questionnaire status is N/A, if the parameter is not reviewed
                                  */
                                 N_A("A", RVW_QNAIRE_STATUS_N_A_TOOLTIP),

                                 /**
                                  * No questionnaire available
                                  */
                                 NO_QNAIRE("?", NO_QNAIRE_STATUS),
                                 /**
                                  * No negative answered in questionnaire
                                  */
                                 NOT_ALLOW_NEGATIVE(
                                                    "Q",
                                                    "All answered, but with negative answers - This will NOT allow to mark wp as finish.");

    private final String dbType;
    private final String uiType;

    DA_QS_STATUS_TYPE(final String dbType, final String uiType) {
      this.dbType = dbType;
      this.uiType = uiType;
    }

    /**
     * @return the dbType
     */
    public String getDbType() {
      return this.dbType;
    }

    /**
     * @return the uiType
     */
    public String getUiType() {
      return this.uiType;
    }

    /**
     * Return the Question Result Type for the given db type
     *
     * @param dbType db literal of flag
     * @return the flag object
     */
    public static DA_QS_STATUS_TYPE getTypeByDbCode(final String dbType) {

      DA_QS_STATUS_TYPE ret = null;

      for (DA_QS_STATUS_TYPE type : DA_QS_STATUS_TYPE.values()) {
        if (type.dbType.equals(dbType)) {
          ret = type;
          break;
        }
      }

      return ret;
    }

    /**
     * Return the Question Result Type for the given ui type
     *
     * @param uiType ui literal of flag
     * @return the flag object
     */
    public static DA_QS_STATUS_TYPE getTypeByUiText(final String uiType) {
      DA_QS_STATUS_TYPE ret = null;

      for (DA_QS_STATUS_TYPE type : DA_QS_STATUS_TYPE.values()) {
        if (type.uiType.equals(uiType)) {
          ret = type;
          break;
        }
      }

      return ret;
    }

  }

  /**
   * Review Data Status (Equal, Not-Equal)
   */
  public enum RESULT_STATUS {

                             /**
                              * Equal
                              */
                             EQUAL("Equal"),
                             /**
                              * NOT-Equal
                              */
                             NOT_EQUAL("Not Equal"),
                             /**
                              * NOT-Equal
                              */
                             NA("Not Applicable");

    private final String text;


    RESULT_STATUS(final String text) {
      this.text = text;

    }

    /**
     * @return String
     */
    public String getText() {
      return this.text;
    }
  }

  /**
   * Compliance Type Status (Compliance, Non-Compliance)
   */
  public enum COMPLI_TYPE {

                           /**
                            * Compliance
                            */
                           COMPLIANCE("Compliance"),
                           /**
                            * Non-Compliance
                            */
                           NON_COMPLIANCE("Non Compliance");

    private final String text;


    COMPLI_TYPE(final String text) {
      this.text = text;

    }

    /**
     * @return String
     */
    public String getText() {
      return this.text;
    }
  }

  /**
   * Type of parameter
   */
  public enum ParameterType {

                             /**
                              * Map
                              */
                             MAP("MAP"),
                             /**
                              * CURVE
                              */
                             CURVE("CURVE"),
                             /**
                              * VALUE
                              */
                             VALUE("VALUE"),
                             /**
                              * VAL_BLK
                              */
                             VAL_BLK("VAL_BLK"),
                             /**
                              * ASCII
                              */
                             ASCII("ASCII"),
                             /**
                              * AXIS_PTS
                              */
                             AXIS_PTS("AXIS_PTS");

    private final String text;


    ParameterType(final String text) {
      this.text = text;

    }

    /**
     * @return String
     */
    public String getText() {
      return this.text;
    }
  }

  /**
   * CDR Shape Review Result flag
   */
  public enum SR_RESULT {

                         /**
                          * Passed
                          */
                         PASS(ApicConstants.CODE_PASSED, "Passed"),
                         /**
                          * Failed
                          */
                         FAIL(ApicConstants.CODE_FAILED, "Failed");

    private final String dbType;
    private final String uiType;

    SR_RESULT(final String dbType, final String uiType) {
      this.dbType = dbType;
      this.uiType = uiType;
    }

    /**
     * @return the uiType
     */
    public String getUiType() {
      return this.uiType;
    }

    /**
     * @return the dbType
     */
    public String getDbType() {
      return this.dbType;
    }

    /**
     * @param dbType DB type
     * @return result enum
     */
    public static SR_RESULT getType(final String dbType) {
      for (SR_RESULT type : SR_RESULT.values()) {
        if (type.dbType.equals(dbType)) {
          return type;
        }
      }
      return null;
    }
  }

  /**
   * CDR Shape Review Accepted flag
   */
  public enum SR_ACCEPTED_FLAG {

                                /**
                                 * Passed
                                 */
                                YES(ApicConstants.CODE_YES, "Yes"),
                                /**
                                 * Failed
                                 */
                                NO(ApicConstants.CODE_NO, "No");

    private final String dbType;
    private final String uiType;

    SR_ACCEPTED_FLAG(final String dbType, final String uiType) {
      this.dbType = dbType;
      this.uiType = uiType;
    }

    /**
     * @return the uiType
     */
    public String getUiType() {
      return this.uiType;
    }

    /**
     * @return the dbType
     */
    public String getDbType() {
      return this.dbType;
    }

    /**
     * @param dbType DB type
     * @return accepted flag enum
     */
    public static SR_ACCEPTED_FLAG getType(final String dbType) {
      for (SR_ACCEPTED_FLAG type : SR_ACCEPTED_FLAG.values()) {
        if (type.dbType.equals(dbType)) {
          return type;
        }
      }
      return null;
    }
  }

  /**
   * Flag for Type of Assessment
   */
  public enum TYPE_OF_ASSESSMENT {

                                  /**
                                   * Series Release
                                   */
                                  SERIES_RELEASE("S", "Assessment for series release"),
                                  /**
                                   * Development
                                   */
                                  DEVELOPMENT("D", "Assessment for development");


    private final String dbType;
    private final String uiType;

    TYPE_OF_ASSESSMENT(final String dbType, final String uiType) {
      this.dbType = dbType;
      this.uiType = uiType;
    }

    /**
     * @return type of assessment db literal
     */
    public final String getDbType() {
      return this.dbType;
    }

    /**
     * @return UI type
     */
    public final String getUiType() {
      return this.uiType;
    }

    /**
     * Return the type of assessment object for the given db type
     *
     * @param dbType db literal of flag
     * @return the flag object
     */
    public static TYPE_OF_ASSESSMENT getType(final String dbType) {
      for (TYPE_OF_ASSESSMENT type : TYPE_OF_ASSESSMENT.values()) {
        if (type.dbType.equals(dbType)) {
          return type;
        }
      }
      // Development, by default
      return DEVELOPMENT;
    }

  }

  /**
   * File Archival status
   */
  public enum FILE_ARCHIVAL_STATUS {

                                    /**
                                     * In progress
                                     */
                                    IN_PROGRESS("I", "In Progress"),
                                    /**
                                     * Completed
                                     */
                                    COMPLETED("C", "Completed"),
                                    /**
                                     * Failed - Completed with errors
                                     */
                                    FAILED("F", "Failed"),
                                    /**
                                     * NOT available - Completed with errors
                                     */
                                    NOT_AVAILABLE("N", "Not Available");

    private final String dbType;
    private final String uiType;

    FILE_ARCHIVAL_STATUS(final String dbType, final String uiType) {
      this.dbType = dbType;
      this.uiType = uiType;
    }

    /**
     * @return the uiType
     */
    public String getUiType() {
      return this.uiType;
    }

    /**
     * @return the dbType
     */
    public String getDbType() {
      return this.dbType;
    }

    /**
     * @param dbType DB type
     * @return result enum
     */
    public static FILE_ARCHIVAL_STATUS getTypeByDbCode(final String dbType) {
      for (FILE_ARCHIVAL_STATUS type : FILE_ARCHIVAL_STATUS.values()) {
        if (type.dbType.equals(dbType)) {
          return type;
        }
      }
      return null;
    }


    /**
     * @param uiType UI type
     * @return enum
     */
    public static FILE_ARCHIVAL_STATUS getTypeByUiCode(final String uiType) {
      for (FILE_ARCHIVAL_STATUS type : FILE_ARCHIVAL_STATUS.values()) {
        if (type.uiType.equals(uiType)) {
          return type;
        }
      }
      return null;
    }
  }

  /**
   * Score value Change Marker Flag index for score value change in CDR result parameter during delta review
   */
  public static final int COMPLI_RESULT_CHG_FLAG_INX = 8;
  /**
   * Score value Change Marker Flag index for score value change in CDR result parameter during delta review
   */
  public static final int SCORE_VAL_CHG_FLAG_INX = 7;

  /**
   * Bitwise value Change Marker Flag index for Bitwise value change in CDR result parameter during delta review
   */
  static final int BITWISE_LMT_CHG_FLAG_INX = 6;
  /**
   * Bitwise Change Marker Flag index for bitwise flag change in CDR result parameter during delta review
   */
  private static final int BITWISE_CHG_FLAG_INX = 5;
  /**
   * Icdm-945 Ref Value Change Marker Flag index for Ref Value change in CDR result parameter during delta review
   */
  private static final int REF_VAL_CHG_FLAG_INX = 4;

  /**
   * Icdm-945 Removed Hex Value and made it as normal int Flag for lower limit change in CDR result parameter during
   * delta review
   */
  private static final int LOW_LMT_CHG_FLAG_INX = 3;

  /**
   * Icdm-945 Removed Hex Value and made it as normal int Flag for upper limit change in CDR result parameter during
   * delta review
   */
  public static final int HIGH_LMT_CHG_FLAG_INX = 2;

  /**
   * Icdm-945 Removed Hex Value and made it as normal int Flag for result change in CDR result parameter during delta
   * review
   */
  public static final int RESULT_CHG_FLAG_INX = 1;

  /**
   * Icdm-945 Removed Hex Value and made it as normal int Flag for checked result change in CDR result parameter during
   * delta review
   */
  public static final int CHK_VAL_CHG_FLAG_INX = 0;

  /**
   * secondary Result change index.
   */
  public static final int SEC_RESULT_CHG_FLAG_INX = 9;
  /**
   * qssd Result change index.
   */
  public static final int QSSD_RESULT_CHG_FLAG_INX = 10;
  /**
   * ready for series flag index.
   */
  public static final int READY_FOR_SERIES_FLAG_INX = 11;

  /**
   * exact match index
   */
  public static final int EXACT_MATCH_FLAG_INX = 12;


  /**
   * Flags for parameter change types in delta review
   *
   * @author bne4cob
   */
  public enum PARAM_CHANGE_FLAG {

                                 /**
                                  * Lower limit
                                  */
                                 LOWER_LIMT(LOW_LMT_CHG_FLAG_INX),

                                 /**
                                  * Upper limit
                                  */
                                 UPPER_LIMIT(HIGH_LMT_CHG_FLAG_INX),

                                 /**
                                  * Result
                                  */
                                 RESULT(RESULT_CHG_FLAG_INX),

                                 /**
                                  * Checked value
                                  */
                                 CHECK_VALUE(CHK_VAL_CHG_FLAG_INX),
                                 /**
                                  * Checked value
                                  */
                                 BITWISE_FLAG(BITWISE_CHG_FLAG_INX),
                                 /**
                                  * Checked value
                                  */
                                 BITWISE_LIMIT(BITWISE_LMT_CHG_FLAG_INX),

                                 /**
                                  * Icdm-945 new enum value for Ref value Reference Value
                                  */
                                 REF_VALUE(REF_VAL_CHG_FLAG_INX),

                                 /**
                                  * Score value
                                  */
                                 SCORE_VALUE(SCORE_VAL_CHG_FLAG_INX),
                                 /**
                                  * compliance result
                                  */
                                 COMPLI_RESULT(COMPLI_RESULT_CHG_FLAG_INX),

                                 /**
                                  * secondary Result
                                  */
                                 SECONDARY_RESULT(SEC_RESULT_CHG_FLAG_INX),
                                 /**
                                  * compliance result
                                  */
                                 QSSD_RESULT(QSSD_RESULT_CHG_FLAG_INX),
                                 /**
                                  * ready for series-.rewviw method in cdr result parameter flag
                                  */
                                 READY_FOR_SERIES(READY_FOR_SERIES_FLAG_INX),
                                 /**
                                  * Exact match ->Match Ref Flag in cdr result parameter
                                  */
                                 EXACT_MATCH(EXACT_MATCH_FLAG_INX);


    private final int index;

    PARAM_CHANGE_FLAG(final int bitIndex) {
      this.index = bitIndex;
    }

    /**
     * Sets this flag to the given input
     *
     * @param input input
     * @return modified input after flag is set
     */
    public int setFlag(final int input) {
      BitSet bitSet = BitSet.valueOf(new long[] { input });
      bitSet.set(this.index);
      long[] bitArray = bitSet.toLongArray();
      if (bitArray.length == 0) {
        return 0;
      }
      return (int) bitArray[0];
    }

    /**
     * Sets this flag to the given input
     *
     * @param input input
     * @return modified input after flag is set
     */
    public int removeFlag(final int input) {
      BitSet bitSet = BitSet.valueOf(new long[] { input });
      bitSet.clear(this.index);
      long[] bitArray = bitSet.toLongArray();
      if (bitArray.length == 0) {
        return 0;
      }
      return (int) bitArray[0];
    }

    /**
     * Checks whether this flag is set in the input
     *
     * @param input input
     * @return true/false
     */
    public boolean isSet(final int input) {
      BitSet bitSet = BitSet.valueOf(new long[] { input });
      return bitSet.get(this.index);
    }
  }

  /**
   * Question fields config type
   */
  /**
   * @author dmo5cob
   */
  public enum QUESTION_CONFIG_TYPE {

                                    /**
                                     * Optional
                                     */
                                    OPTIONAL("O", "Optional"),
                                    /**
                                     * Mandatory
                                     */
                                    MANDATORY("M", "Mandatory"),
                                    /**
                                     * Not Relevant
                                     */
                                    NOT_RELEVANT("N", "Not Relevant"),

                                    /**
                                     * For question of type heading , the type would be none
                                     */
                                    NONE("", "");

    private final String dbType;
    private final String uiType;

    QUESTION_CONFIG_TYPE(final String dbType, final String uiType) {
      this.dbType = dbType;
      this.uiType = uiType;
    }

    /**
     * @return Result Value db literal
     */
    public final String getDbType() {
      return this.dbType;
    }

    /**
     * @return UI type
     */
    public final String getUiType() {
      return this.uiType;
    }

    /**
     * Return the type object for the given db type
     *
     * @param dbType db literal of flag
     * @return the flag object
     */
    public static QUESTION_CONFIG_TYPE getType(final String dbType) {
      if (dbType == null) {
        return NONE;
      }
      for (QUESTION_CONFIG_TYPE type : QUESTION_CONFIG_TYPE.values()) {
        if (type.dbType.equals(dbType)) {
          return type;
        }
      }
      // optional, by default
      return NONE;
    }

    /**
     * Return the type object for the given db type
     *
     * @param uiType db literal of flag
     * @return the flag object
     */
    public static QUESTION_CONFIG_TYPE getDbType(final String uiType) {
      for (QUESTION_CONFIG_TYPE type : QUESTION_CONFIG_TYPE.values()) {
        if (type.uiType.equals(uiType)) {
          return type;
        }
      }
      // optional, by default
      return NONE;
    }
  }

  /**
   * @author rgo7cob
   * @deprecated Use {@link WpRespType} instead
   */
  @Deprecated
  public enum WPResponsibilityEnum {

                                    /**
                                     * Robert Bosch
                                     */
                                    RB("Robert Bosch", "R"),
                                    /**
                                     * Customer
                                     */
                                    CUSTOMER("Customer", "C"),
                                    /**
                                     * Others
                                     */
                                    OTHERS("Others", "O");

    /**
     * display name
     */
    private final String dispName;


    /**
     * db name
     */
    private final String dbVal;


    WPResponsibilityEnum(final String dispName, final String dbVal) {
      this.dispName = dispName;
      this.dbVal = dbVal;
    }

    /**
     * @return the dbVal
     */
    public String getDbVal() {
      return this.dbVal;
    }

    /**
     * @return the dispName
     */
    public String getDispName() {
      return this.dispName;
    }

    /**
     * Return the type object for the given db type
     *
     * @param dbVal db literal of type
     * @return the user type object
     */
    public static WPResponsibilityEnum getType(final String dbVal) {
      for (WPResponsibilityEnum type : WPResponsibilityEnum.values()) {
        if (type.dbVal.equals(dbVal)) {
          return type;
        }
      }
      return null;
    }


  }

  /**
   * Node type indicator for workpackages in the PIDC and Favorites trees
   */
  public static final String NODE_TYPE_WRK_PKG = "WRK_PKG";
  // ICDM-1880
  /**
   * Node type indicator for variants used in review in the PIDC and Favorites trees
   */
  public static final String NODE_TYPE_VARIANTS = "VARIANTS";

  /**
   * Node type indicator for variants used in review questionnaire in the PIDC and Favorites trees
   */
  public static final String NODE_TYPE_QNAIRE_VARIANTS = "Q_VARIANTS";


  /**
   * all the params Icdm-729
   */
  public static final String ALL_PARAMETRES = "ALL_PARAMETRES";


  /**
   * Icdm-729 Selecting the functions
   */
  public static final String FUNCTION = "FUNCTION";

  /**
   * Icdm-729 Selecting the lab file
   */
  public static final String LAB = "LAB";

  /**
   * constant for paramter type
   */
  public static final String PARAM_TYPE = "TYPE";
  /**
   * constant for TICK in focus matrix
   */
  public static final String TICK = "TICK";

  /**
   * constant for review flag no
   */
  public static final String REVIEW_FLAG_NO = "REVIEW_FLAG_NO";


  /**
   * constant for review flag no
   */
  public static final String REVIEW_NO_BACKGROUND = "REVIEW_NO_BACKGROUND";
  // ICDM-1880
  /**
   * constant for No-Variant node type
   */
  public static final Long NO_VARIANT_NODE_ID = 1l;
  /**
   * text for exact match check box
   */
  public static final String EXACT_MATCH_TO_REFERENCE_VALUE = "Exact match to reference value";
  /**
   * constant to display review results image
   */
  public static final String REVIEW_RESULTS_IMAGE = "RVW_RSLTS";
  /**
   * constant to display wp archival image
   */
  public static final String WP_ARCHIVALS_IMAGE = "WP_ARCHIVALS";
  /**
   * constant for review questionnaires
   */
  public static final String NODE_TYPE_RVW_QUESTIONNAIRES = "Review Questionnaires";

  /**
   * RVW_QNAIRE_RESP_STATUS_ALL
   */
  public static final String RVW_QNAIRE_RESP_STATUS_ALL = "RVW_QNAIRE_RESP_STATUS_ALL";
  /**
   * RVW_QNAIRE_RESP_STATUS_NA
   */
  public static final String RVW_QNAIRE_RESP_STATUS_NA = "RVW_QNAIRE_RESP_STATUS_NA";

  /**
   * RVW_QNAIRE_RESP_STATUS_EXCLAMATION
   */
  public static final String RVW_QNAIRE_RESP_STATUS_EXCLAMATION = "RVW_QNAIRE_RESP_STATUS_EXCLAMATION";

  /**
   * RVW_QNAIRE_RESP_STATUS_REMOVE
   */
  public static final String RVW_QNAIRE_RESP_STATUS_REMOVE = "RVW_QNAIRE_RESP_STATUS_REMOVE";

  /**
   * RVW_QNAIRE_RESP_STATUS_OK
   */
  public static final String RVW_QNAIRE_RESP_STATUS_OK = "RVW_QNAIRE_RESP_STATUS_OK";

  /**
   * review maturity Complete
   */
  public static final String SCORE_RATING_75 = "SCORE_RATING_75";

  /**
   * review maturity None
   */
  public static final String SCORE_RATING_0 = "SCORE_RATING_0";

  /**
   * review maturity Prelim
   */
  public static final String SCORE_RATING_50 = "SCORE_RATING_50";

  /**
   * review maturity start
   */
  public static final String SCORE_RATING_25 = "SCORE_RATING_25";


  /**
   * cdfx export dialog group
   */
  public static final String CDFX_EXPORT_DIALOG_GROUP = "CDFX_EXPORT_DIALOG";
  /**
   * tooltip msg
   */
  public static final String BLACK_LIST_TOOLTIP = "BLACK_LIST_TOOLTIP";

  /**
   * Tooltip messsage for virtual records
   */
  public static final String VIRTUAL_WP_PARAM_MAPPING_REC_TOOLTIP = "VIRTUAL_WP_PARAM_MAPPING_REC_TOOLTIP";
  /**
   * info for single param score update
   */
  public static final String BLACK_LIST_INFO_SINLGE = "BLACK_LIST_INFO_SINGLE";
  /**
   * info for multiple param score update
   */
  public static final String BLACK_LIST_INFO_MULTIPLE = "BLACK_LIST_INFO_MULTIPLE";

  /**
   *
   */
  public static final String PARAM = "PARAM";

  // ICDM-2439
  /**
   * Compliance Parameter
   */
  public static final String COMPLIANCE_LABEL = "COMPLIANCE";
  /**
   * Compliance Parameter
   */
  public static final String SHAPE_CHECK_LABEL = "SHAPE_CHECK";
  /**
   * Parameter not in CalMemory
   */
  public static final String NOT_IN_CALMEMORY_LABEL = "NOT_IN_CALMEMORY";


  /**
   * Group name for hex compare functionality
   */
  public static final String HEX_COMPARE = "HEX_COMPARE";

  // ICDM-2439
  /**
   * Compliance Parameter
   */
  public static final int DEFAULT_COMBO_SEL_INDEX = 0;
  /**
   *
   */
  public static final String FUNC_VERS_DIFF = "Function Version Different";

  /**
   *
   */
  public static final String MAIN_REVIEW = "MainReview";
  /**
   *
   */
  public static final String SSD_FILE_PATH_SEPARATOR = "__";
  /**
   *
   */
  public static final String SECONDARY_REVIEW = "SecondaryReview";
  /**
   *
   */
  public static final String COMPLI_REVIEW = "CompliReview";
  /**
   *
   */
  public static final String SSD_RULES = "SSDRules";
  /**
   *
   */
  public static final String COMMON_RULES = "CommonRules";
  /**
   *
   */
  public static final String SSD_FILE_EXT = ".ssd";
  /**
   *
   */
  public static final String CHECK_SSD = "CheckSSD";

  /**
   *
   */
  public static final String PDF_REPORT_BOSCH_LOGO_IMAGE = "boschlogo_icon.png";

  /**
   *
   */
  public static final String REPORT_LOGO_NODE_TYPE = "REPORT_LOGO";
  /**
  *
  */
  public static final String CHECKSSD_LOGO_NODE_TYPE = "CHECKSSD_LOGO";
  /**
  *
  */
  public static final String CHECKSSD_LOGO_IMAGE = "disclosure.jpg";

  /**
  *
  */
  public static final String INFORMATION_DIALOG = "Information Dialog";

  /**
  *
  */
  public static final String REVIEW_DISCARD_MESSAGE = "The Review process will be discarded";

  // Cal data importer constants
  /**
   * Key for function name in parameter properties map
   */
  public static final String CDIKEY_FUNCTION_NAME = "function";
  /**
   * Key for parameter name in parameter properties map
   */
  public static final String CDIKEY_PARAM_NAME = "name";
  /**
   * Key for parameter class in parameter properties map
   */
  public static final String CDIKEY_PARAM_CLASS = "pclass";
  /**
   * Key to indicate param props have changed
   */
  public static final String CDIKEY_PROP_CHANGED = "change";
  /**
   * Key for parameter iscodeword in parameter properties map
   */
  public static final String CDIKEY_CODE_WORD = "codeword";
  /**
   * Key for parameter long name in parameter properties map
   */
  public static final String CDIKEY_LONG_NAME = "longname";
  /**
   * Key for parameter iscodeword in parameter properties map
   */
  public static final String CDIKEY_CAL_HINT = "calhint";
  /**
   * Key for use check box of class
   */
  public static final String CDIKEY_USE_CLASS = "usenewclass";
  /**
   * Key for parameter iscodeword in parameter properties map
   */
  public static final String CDIKEY_BIT_WISE = "bitwise";

  /**
   * Read Only Parameter
   */
  public static final String READ_ONLY = "READ_ONLY";

  /**
   * QSSD LABEL
   */
  public static final String QSSD_LABEL = "QSSD_LABEL";


  /**
   * Added this to hold the sheet name and file name seperator
   */
  public static final String FILENAME_SEPERATOR = ":";

  /**
   *
   */
  public static final String MONICA_SHEETNAME_POSTFIX_VAL = "iCDM_Check";

  /**
   *
   */
  public static final String BLACK_LIST_LABEL = "BLACK_LIST_LABEL";

  /**
   *
   */
  public static final String COMPLI_BLACK_LIST_LABEL = "COMPLI_BLACK_LIST_LABEL";

  /**
   *
   */
  public static final String READ_ONLY_BLACK_LIST_LABEL = "READ_ONLY_BLACK_LIST_LABEL";

  /**
   *
   */
  public static final String COMPLI_READ_ONLY_BLACK_LIST_LABEL = "COMPLI_READ_ONLY_BLACK_LIST_LABEL";

  /**
   *
   */
  public static final String COMPLI_READ_ONLY = "COMPLI_READ_ONLY";

  /**
   *
   */
  public static final String COMPLI = "COMPLI";

  /**
  *
  */
  public static final String SHARE_POINT_UPLOAD = "SHARE_POINT_UPLOAD";

  /**
  *
  */
  public static final String BASELINE_HYPERLINK = "BASELINE_HYPERLINK";

  /**
  *
  */
  public static final String FILE_HYPERLINK = "FILE_HYPERLINK";

  /**
   *
   */
  public static final String VIRTUAL_INDICATOR = "VIRTUAL_INDICATOR";
  /**
  *
  */
  public static final String MULTI_IMAGE_PAINTER = "MULTI_IMAGE_PAINTER";

  /**
   * Constant to show the message when there are not secondary results for a parameter
   */
  public static final String NO_SECONDARY_RESULT = "No secondary results available for this parameter.";

  /**
   * Constant to display 'N/A' as questionnaire status in case of never reviewed
   */
  public static final String RVW_QNAIRE_STATUS_N_A = "N/A";

  /**
   * Constant for Not Baselined questionnaire Response
   */
  public static final String NOT_BASELINED_QNAIRE_RESP = "NOT_BASELINED";

  /**
   * Constant for tooltip 'N/A' as questionnaire status in case of never reviewed
   */
  public static final String RVW_QNAIRE_STATUS_N_A_TOOLTIP =
      "Questionnaire status can only be determined after a label has been reviewed.";

  /**
   *
   */
  public static final Long WORKING_SET_REV_NUM = 0L;

  /**
   * Colon to split revision and version name
   */
  public static final String REV_COL_COLON = " : ";
  /**
   * Colon to split revision and version name
   */
  public static final String FINISHED_MARKER = "Finished";
  /**
   * Colon to split revision and version name
   */
  public static final String NOT_FINISHED_MARKER = "Not Finished";
  /**
   *
   */
  public static final String NO_REVIEW_RESULT_FOUND = "No Review Result found";

  public static final String LINK_COL_NAME = "Link";

  /**
   *
   */
  public static final String ANSWER_COL_NAME = "Answer";

  /**
   * Dependent characteristic
   */
  public static final String DEPENDENT_CHARACTERISTICS = "Dependent_Characteristic";

  /**
   * Workpackage Responsibility MSG for Completed Questionnaire Response
   */
  public static final String QNAIRE_RESP_STATUS_IS_COMPLETED_MSG = "Questionnaire Response Status is completed";

  /**
   * Workpackage Responsibility MSG for failed if all the a2l Workpackage Responsibility parameters are not Reviewed
   */
  public static final String RVW_PARAM_WPRESP_NOT_REVIEWED =
      "All the Parameter are not reviewed between the following Review result and A2l's active WorkPackageDefinition Version for the below combinations(s):";
  /**
   * Workpackage Responsibility MSG for failed if all the a2l Workpackage Responsibility review on parameters are not
   * Official
   */
  public static final String RVW_PARAM_WPRESP_NOT_OFFICIAL =
      "Review for the below Workpackage and Responsibility combinations(s) are not official:";
  /**
   * Workpackage Responsibility MSG for failed if all the a2l Workpackage Responsibility review on parameters are not
   * locked
   */
  public static final String RVW_PARAM_WPRESP_NOT_LOCKED =
      "Review for the below Workpackage and Responsibility combinations(s) are not locked:";

  /**
   * Workpackage Responsibility MSG for failed Review Parameters
   */
  public static final String RVW_PARAM_WPRESP_FAIL_MSG =
      "Please check the Review Score for parameters under the below Workpackage Responsibility combination(s):";

  /**
   * Workpackage Responsibility MSG for failed Questionnaire Response
   */
  public static final String QNAIRE_RESP_STATUS_FAIL_MSG =
      "Please check the Questionnaire Response status for the below Workpackage Responsibility combination(s):";

  /**
   * Workpackage Responsibility MSG for negative answers not allowed
   */
  public static final String QNAIRE_NEGATIVE_ANSWERS_FAIL_MSG =
      " - Questionnaire Response has negative answers (No negative answers in this questionnaire are allowed to finish this WP according to the questionnaire's definition) and this Workpackage cannot be finished :";

  /**
   * Workpackage Responsibility MSG for negative answers not allowed
   */
  public static final String QNAIRE_NOT_BASELINED_MSG =
      "Questionnaire Response without baseline existing for the below Workpackage and Responsibility combination(s):";

  /**
   * Workpackage Responsibility MSG for Completed Questionnaire Response and Review parameters
   */
  public static final String COMPLETED_WPRESP_MSG =
      "Both Review Parameter(s) and Questionnaire Response(s) are completed for the below Workpackage Responsibility combination(s):";

  /**
   * Parameter Mismatch message between the Review WPDefinitionVersion and A2l Active WPDefinitionVersion
   */
  public static final String PARAMETER_MISMATCH_MSG =
      "Parameter mismatch between the following Review WorkPackageDefinition and A2l's active WorkPackageDefinition Version:";

  /**
   * RVW_WORKPACKAGE_RESP_MSG to display if the Workpackage and Responsibilty not found in active WpDefVersion
   */
  public static final String RVW_WORKPACKAGE_RESP_MSG =
      "Review's Workpackage and Responsibilty not found in active A2l WorkPackageDefinition Version";

  /**
   * Workpackage Responsibility MSG for Successfully Archived WP Resp
   */
  public static final String WP_ARCHIVAL_COMPLETED_WPRESP_MSG =
      "WP Archival is completed for the below Workpackage Responsibility combination(s):";

  /**
   * Workpackage Responsibility MSG for Archival Failed for WP Resp
   */
  public static final String WP_ARCHIVAL_FAILED_WPRESP_MSG =
      "WP Archival has failed for the below Workpackage Responsibility combination(s):";

  /**
   * Workpackage Responsibility MSG for Archival Failed for WP Resp
   */
  public static final String WP_ARCHIVAL_INPROGRESS_MSG =
      "WP Archival creation is In-Progress. You will be notified once the process is completed.";

  /**
   * Constant for WP Archival Folder
   */
  public static final String WP_ARCHIVAL_FOLDER = "WP_ARCHIVES";

  /**
   * Text for <don't care> attributes
   */
  public static final String DONT_CARE_ATTR_VALUE_TEXT = "<don't care>";

  /**
   * Constant for Questionnaire Status - When there are no questionnaires for Workpackage, for Monica review
   */
  public static final String NO_QNAIRE_STATUS = "No questionnaire available";

  /**
   * Constant for Questionnaire Status ToolTip - When no Questionnaire Response Baseline exists for WP Resp
   */
  public static final String QNAIRE_RESP_NOT_BASELINED_TOOLTIP =
      "No baseline existing for this work package.\n\tOpen the questionnaire and create a baseline.";

  /**
   * Constant for Data assessment - Group name in T_MESSAGES
   */
  public static final String DATA_ASSESSMENT = "DATA_ASSESSMENT";

  /**
   * Constant for Baseline creation - Name in T_MESSAGES
   */
  public static final String BASELINE_CREATION = "BASELINE_CREATION";

  /**
   * Constant for File archival status IN_PROGRESS - Name in T_MESSAGES
   */
  public static final String FILE_STATUS_IN_PROGRESS = "FILE_STATUS_IN_PROGRESS";

  /**
   * Constant for File archival status FAILED - Name in T_MESSAGES
   */
  public static final String FILE_STATUS_FAILED = "FILE_STATUS_FAILED";

  /**
   * Constant for File archival status NOT_AVAILABLE - Name in T_MESSAGES
   */
  public static final String FILE_STATUS_NOT_AVAILABLE = "FILE_STATUS_NOT_AVAILABLE";

  /**
   * enum for Data assessment Questionnaire Status Types for a WP RESP
   */
  public enum DA_QNAIRE_STATUS_FOR_WPRESP {

                                           /**
                                            * if all questionnaires are answered and baselined
                                            */
                                           YES("Y", "YES"),
                                           /**
                                            * if all questionnaires are not answered and baselined
                                            */
                                           NO("N", "NO"),

                                           /**
                                            * N/A, if questionnaires are not available for WP RESP
                                            */
                                           N_A("-", "N/A");


    private final String dbType;
    private final String uiType;


    /**
     *
     */
    private DA_QNAIRE_STATUS_FOR_WPRESP(final String dbType, final String uiType) {
      this.dbType = dbType;
      this.uiType = uiType;
    }

    /**
     * @return the dbType
     */
    public String getDbType() {
      return this.dbType;
    }

    /**
     * @return the uiType
     */
    public String getUiType() {
      return this.uiType;
    }

    /**
     * Return the Question Status Type of WP RESP for the given db type
     *
     * @param dbType db literal of flag
     * @return the flag object
     */
    public static DA_QNAIRE_STATUS_FOR_WPRESP getTypeByDbCode(final String dbType) {

      DA_QNAIRE_STATUS_FOR_WPRESP ret = null;

      for (DA_QNAIRE_STATUS_FOR_WPRESP type : DA_QNAIRE_STATUS_FOR_WPRESP.values()) {
        if (type.dbType.equals(dbType)) {
          ret = type;
          break;
        }
      }

      return ret;
    }

    /**
     * Return the Question Status Type of WP RESP for the given ui type
     *
     * @param uiType ui literal of flag
     * @return the flag object
     */
    public static DA_QNAIRE_STATUS_FOR_WPRESP getTypeByUiText(final String uiType) {
      DA_QNAIRE_STATUS_FOR_WPRESP ret = null;

      for (DA_QNAIRE_STATUS_FOR_WPRESP type : DA_QNAIRE_STATUS_FOR_WPRESP.values()) {
        if (type.uiType.equals(uiType)) {
          ret = type;
          break;
        }
      }

      return ret;
    }
  }

  /**
   * OBD General Questionnaire Option
   */
  public enum OBD_OPTION {

                          /**
                           * For Only OBD Labels - Only OBD general Qnaire
                           */
                          ONLY_OBD_LABELS("O", "Only OBD Labels."),
                          /**
                           * For NO-OBD Labels - Only Simplified Qnaire Option
                           */
                          NO_OBD_LABELS("N", "NO-OBD Labels."),
                          /**
                           * For Both OBD and Simplified Qnaire Option
                           */
                          BOTH_OBD_AND_NON_OBD_LABELS("B", "Both OBD and non-OBD Labels.");


    private final String dbType;
    private final String uiType;

    OBD_OPTION(final String dbType, final String uiType) {
      this.dbType = dbType;
      this.uiType = uiType;
    }

    /**
     * @return the dbType
     */
    public String getDbType() {
      return this.dbType;
    }

    /**
     * @return the uiType
     */
    public String getUiType() {
      return this.uiType;
    }

    /**
     * Return the OBD_OPTION for the given db type
     *
     * @param dbType db literal of flag
     * @return the flag object
     */
    public static OBD_OPTION getTypeByDbCode(final String dbType) {

      OBD_OPTION ret = null;

      for (OBD_OPTION type : OBD_OPTION.values()) {
        if (type.dbType.equals(dbType)) {
          ret = type;
          break;
        }
      }

      return ret;
    }

    /**
     * Return the OBD_OPTION for the given ui type
     *
     * @param uiType ui literal of flag
     * @return the flag object
     */
    public static OBD_OPTION getTypeByUiText(final String uiType) {
      OBD_OPTION ret = null;

      for (OBD_OPTION type : OBD_OPTION.values()) {
        if (type.uiType.equals(uiType)) {
          ret = type;
          break;
        }
      }

      return ret;
    }
  }

}
