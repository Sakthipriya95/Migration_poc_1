/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;


/**
 * @author say8cob
 */
public final class MonicaReviewErrorCode {


  /**
   * Constant Message for USER_NAME_CANNOT_BE_EMPTY
   */
  public static final String USER_NAME_EMPTY = "user name field cannot be left empty";

  /**
   * Constant Message for MONICA_PROTOCOL_CANNOT_BE_EMPTY
   */
  public static final String MONICA_REVIEW_EMPTY = "The protocol is empty. The review cannot be performed.";

  /**
   * Constant Message for PIDC_IS_LOCKED
   */
  public static final String PIDC_IS_LOCKED =
      "The PIDC Version is locked. You can upload Reviews only to active PIDC Versions.";

  /**
   * Constant Message for PIDC_IS_DELETED
   */
  public static final String PIDC_IS_DELETED =
      "The given PIDC is marked as deleted. Reviews can be created for non-deleted PIDCs";

  /**
   * Constant Message for PIDC_VARAINAT_IS_NULL
   */
  public static final String PIDC_VARIANT_IS_NULL =
      "Review could not be created. The given Variant id is not existing. Provide an existing Variant ID.";

  /**
   * Constant Message for PIDC_VARAINAT_IS_DELETED
   */
  public static final String PIDC_VARIANT_IS_DELETED =
      "The given Variant ID is marked as deleted. Reviews can be created for non-deleted variants.";

  /**
   * Constant Message for PIDC_HAS_A2L
   */
  public static final String PIDC_A2L_ABSENT = "The A2L File ID is not existing. Please provide a valid A2L-File-ID.";

  /**
   * Constant Message for PIDC_VERSION_MISMATCH
   */
  public static final String PIDC_VERSION_MISMATCH =
      "The PIDC Version of Variant ID and A2L ID are not matching. Both IDs must belong to the same PIDC Version.";

  /**
   * Constant Message for PIDC_HAS_VARIANT
   */
  public static final String PIDC_HAS_VARIANT =
      "Parameter 'Variant-ID' is mandatory for a PIDC that has variants. Add a variant ID to the request.";

  /**
   * Constant Message when the MoniCa protocol label is not available in DCM file
   */
  public static final String MONICA_PROTOCOL_LABEL_NOT_FOUND_DCMFILE =
      " cannot be found in DCM file but is part of MoniCa Protocol. The count of parameters in MoniCa Protocol and DCM file must be same.";

  /**
   * Constant Message when the MoniCa protocol label count is not same as in DCM file
   */
  public static final String MONICA_PROTOCOL_LABEL_COUNT_MISMATCH =
      "The count of parameters in MoniCa Protocol and DCM file are not equal.";

  /**
   * Constant Message when the DCM file have no calData information
   */
  public static final String NO_CAL_INFO_IN_DCMFILE = "No parameter available in the DCM file.";


  /**
   * Constant Message when the DCM file label is not available in MoniCa Protocol
   */
  public static final String DCM_LABEL_NOT_FOUND_MONICAPROTOCOL =
      " cannot be found in MoniCa Protocol but is part of DCM file. The count of parameters in DCM file and MoniCa Protocol must be same.";

  /**
   * Constant Message when the Label in MoniCa protocol is empty
   */
  public static final String MONICAPROTOCOL_CANNOT_EMPTY = "Provided MoniCa Protocol parameter is Empty";

  /**
   * Constant Message when the label status in MoniCa protocol is empty
   */
  public static final String MONICAPROTOCOL_STATUS_EMPTY = "Provided MoniCa Protocol parameter status is Empty";

  /**
   * constant Message when the MoniCa protocol label status is invalid
   */
  public static final String MONICAPROTOCOL_INVALID_STATUS = "Provided MoniCa Protocol parameter status is invalid";

  /**
   * Constant Message when the DCM file label is not available in A2l file
   */
  public static final String DCM_LABEL_NOT_FOUND_A2LFILE =
      " cannot be found in A2l file . Please check if the DCM file really belong to the A2l file. Upload only DCMs that contain the same parameter as the A2L.";

  public static final String PARAMETER = "Parameter ";

  public static final String FILE = "File ";

  public static final String FILE_NOT_EXIST = " is invalid, Provide a valid file.";

  public static final String CAL_DATA_ABSENT = " doesn't contain any caldata. Provide a valid file.";

  public static final String INVALID_DCM_FILE = "Invalid DCM File";

  /**
   * /** Private constructor
   */
  private MonicaReviewErrorCode() {
    // Private constructor
  }

}
