/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.qnaire;


/**
 * @author mkl2cob
 */
public final class QuestionnaireConstants {

  /**
   *
   */
  private static final String TBD_DISPLAY = "To be done";

  /**
   * Text for Result context menu option in questionnaire response
   */
  public static final String SET_TO_POSITIVE_ANS = "Set to positive answer";

  /**
   * Text for Result context menu option in questionnaire response
   */
  public static final String SET_TO_NEGATIVE_ANS = "Set to negative answer";

  /**
   * Series Maturity , Measures constants
   */
  public enum QUESTION_RESP_SERIES_MEASURE {
                                            /**
                                             * not defined
                                             */
                                            NOT_DEFINED("", "Not Defined"),
                                            /**
                                             * yes
                                             */
                                            YES("Y", "Yes"),
                                            /**
                                             * no
                                             */
                                            NO("N", "No");

    final String dbType;
    final String uiType;

    QUESTION_RESP_SERIES_MEASURE(final String dbType, final String uiType) {
      this.dbType = dbType;
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
    public final String getUiType() {
      return this.uiType;
    }

    /**
     * Return the type object for the given db type
     *
     * @param dbType db literal of type
     * @return the user type object
     */
    public static QUESTION_RESP_SERIES_MEASURE getType(final String dbType) {
      for (QUESTION_RESP_SERIES_MEASURE type : QUESTION_RESP_SERIES_MEASURE.values()) {
        if (type.dbType.equals(dbType)) {
          return type;
        }
      }
      return NOT_DEFINED;
    }

    /**
     * Return the type object for the given ui type
     *
     * @param uiType ui literal of type
     * @return the user type object
     */
    public static QUESTION_RESP_SERIES_MEASURE getTypeFromUI(final String uiType) {
      for (QUESTION_RESP_SERIES_MEASURE type : QUESTION_RESP_SERIES_MEASURE.values()) {
        if (type.uiType.equals(uiType)) {
          return type;
        }
      }
      return NOT_DEFINED;
    }
  }

  /**
   * Question response postivie - yes
   */
  public enum QUESTION_RESP_RESULT_YES {
                                        /**
                                         * Not finished
                                         */
                                        YES("P", "Yes", "Positive"),
                                        /**
                                         * Finished
                                         */
                                        NO("N", "No", "Negative"),
                                        /**
                                         * to be done
                                         */
                                        TO_BE_DONE("?", TBD_DISPLAY, null);


    final String dbType;
    final String uiType;

    final String responseType;

    QUESTION_RESP_RESULT_YES(final String dbType, final String uiType, final String responseType) {
      this.dbType = dbType;
      this.uiType = uiType;
      this.responseType = responseType;
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
    public final String getUiType() {
      return this.uiType;
    }


    /**
     * @return the responseType
     */
    public String getResponseType() {
      return this.responseType;
    }

    /**
     * Return the type object for the given db type
     *
     * @param dbType db literal of type
     * @return the user type object
     */
    public static QUESTION_RESP_RESULT_YES getType(final String dbType) {
      for (QUESTION_RESP_RESULT_YES type : QUESTION_RESP_RESULT_YES.values()) {
        if (type.dbType.equals(dbType)) {
          return type;
        }
      }
      return TO_BE_DONE;
    }


    /**
     * @param responseType "Postive" or negative
     * @return the enum value for the response type
     */
    public static QUESTION_RESP_RESULT_YES getTypeForResponse(final String responseType) {
      for (QUESTION_RESP_RESULT_YES type : QUESTION_RESP_RESULT_YES.values()) {
        if (type.dbType.equals(responseType)) {
          return type;
        }
      }
      return TO_BE_DONE;
    }

    /**
     * Return the type object for the given ui type
     *
     * @param uiType ui literal of type
     * @return the user type object
     */
    public static QUESTION_RESP_RESULT_YES getTypeFromUI(final String uiType) {
      for (QUESTION_RESP_RESULT_YES type : QUESTION_RESP_RESULT_YES.values()) {
        if (type.uiType.equals(uiType)) {
          return type;
        }
      }
      return TO_BE_DONE;
    }
  }

  /**
   * Question response postivie - no
   */
  public enum QUESTION_RESP_RESULT_NO {
                                       /**
                                        * Not finished
                                        */
                                       NO("P", "No"),
                                       /**
                                        * Finished
                                        */
                                       YES("N", "Yes"),
                                       /**
                                        * to be done
                                        */
                                       TO_BE_DONE("?", TBD_DISPLAY);

    final String dbType;
    final String uiType;

    QUESTION_RESP_RESULT_NO(final String dbType, final String uiType) {
      this.dbType = dbType;
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
    public final String getUiType() {
      return this.uiType;
    }

    /**
     * Return the type object for the given db type
     *
     * @param dbType db literal of type
     * @return the user type object
     */
    public static QUESTION_RESP_RESULT_NO getType(final String dbType) {
      for (QUESTION_RESP_RESULT_NO type : QUESTION_RESP_RESULT_NO.values()) {
        if (type.dbType.equals(dbType)) {
          return type;
        }
      }
      return TO_BE_DONE;
    }

    /**
     * Return the type object for the given ui type
     *
     * @param uiType ui literal of type
     * @return the user type object
     */
    public static QUESTION_RESP_RESULT_NO getTypeFromUI(final String uiType) {
      for (QUESTION_RESP_RESULT_NO type : QUESTION_RESP_RESULT_NO.values()) {
        if (type.uiType.equals(uiType)) {
          return type;
        }
      }
      return TO_BE_DONE;
    }
  }

  /**
   * Question response postivie - finished
   */
  public enum QUESTION_RESP_RESULT_FINISHED {
                                             /**
                                              * Not finished
                                              */
                                             FINISHED("P", "Finished"),
                                             /**
                                              * Finished
                                              */
                                             NOT_FINISHED("N", "Not Finished"),
                                             /**
                                              * to be done
                                              */
                                             TO_BE_DONE("?", "To be done");

    final String dbType;
    final String uiType;

    QUESTION_RESP_RESULT_FINISHED(final String dbType, final String uiType) {
      this.dbType = dbType;
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
    public final String getUiType() {
      return this.uiType;
    }

    /**
     * Return the type object for the given db type
     *
     * @param dbType db literal of type
     * @return the user type object
     */
    public static QUESTION_RESP_RESULT_FINISHED getType(final String dbType) {
      for (QUESTION_RESP_RESULT_FINISHED type : QUESTION_RESP_RESULT_FINISHED.values()) {
        if (type.dbType.equals(dbType)) {
          return type;
        }
      }
      return TO_BE_DONE;
    }

    /**
     * Return the type object for the given ui type
     *
     * @param uiType ui literal of type
     * @return the user type object
     */
    public static QUESTION_RESP_RESULT_FINISHED getTypeFromUI(final String uiType) {
      for (QUESTION_RESP_RESULT_FINISHED type : QUESTION_RESP_RESULT_FINISHED.values()) {
        if (type.uiType.equals(uiType)) {
          return type;
        }
      }
      return TO_BE_DONE;
    }
  }

  /**
   * CDReview Participant types
   */
  public enum QUESTION_RESP_RESULT {
                                    /**
                                     * Not finished
                                     */
                                    NOT_FINISHED("N", "Not Finished"),
                                    /**
                                     * Finished
                                     */
                                    FINISHED("Y", "Finished"),
                                    /**
                                     * to be done
                                     */
                                    TO_BE_DONE("?", "To be done");

    final String dbType;
    final String uiType;

    QUESTION_RESP_RESULT(final String dbType, final String uiType) {
      this.dbType = dbType;
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
    public final String getUiType() {
      return this.uiType;
    }

    /**
     * Return the type object for the given db type
     *
     * @param dbType db literal of type
     * @return the user type object
     */
    public static QUESTION_RESP_RESULT getType(final String dbType) {
      for (QUESTION_RESP_RESULT type : QUESTION_RESP_RESULT.values()) {
        if (type.dbType.equals(dbType)) {
          return type;
        }
      }
      return TO_BE_DONE;
    }

    /**
     * Return the type object for the given db type
     *
     * @param uiType ui literal of type
     * @return the user type object
     */
    public static QUESTION_RESP_RESULT getTypeFromUI(final String uiType) {
      for (QUESTION_RESP_RESULT type : QUESTION_RESP_RESULT.values()) {
        if (type.uiType.equals(uiType)) {
          return type;
        }
      }
      return TO_BE_DONE;
    }
  }

  /**
   * CDReview Participant types
   */
  public enum QUESTION_VERSION_STATUS {
                                       /**
                                        * IN WORK
                                        */
                                       IN_WORK("Y", "IN WORK"),
                                       /**
                                        * LOCKED
                                        */
                                       LOCKED("N", "LOCKED");


    final String dbType;
    final String uiType;

    QUESTION_VERSION_STATUS(final String dbType, final String uiType) {
      this.dbType = dbType;
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
    public final String getUiType() {
      return this.uiType;
    }

    /**
     * Return the type object for the given db type
     *
     * @param dbType db literal of type
     * @return the user type object
     */
    public static QUESTION_VERSION_STATUS getType(final String dbType) {
      for (QUESTION_VERSION_STATUS type : QUESTION_VERSION_STATUS.values()) {
        if (type.dbType.equals(dbType)) {
          return type;
        }
      }
      return null;
    }
  }

  /**
   * Private constructor for constant definition class
   */
  private QuestionnaireConstants() {
    // private constructor for constant definition class
  }
}
