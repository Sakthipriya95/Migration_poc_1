/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc.projcons;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.bo.apic.pidc.projcons.data.PidcConsInfo;
import com.bosch.caltool.icdm.bo.apic.pidc.projcons.data.PidcVersionConsInfo;
import com.bosch.caltool.icdm.bo.apic.pidc.projcons.data.ProjectSubvariantConsInfo;
import com.bosch.caltool.icdm.bo.apic.pidc.projcons.data.ProjectVariantConsInfo;


/**
 * Create report of evaluation details to the logger
 *
 * @author bne4cob
 */
class ProjConsEvalLoggerReport {

  /**
   * Index incrementor for message creation
   */
  private static final int MSG_INDX_INCR = 2;
  /**
   * Position - PIDC ID
   */
  private static final int POS_PIDC_ID = 0;
  /**
   * Position - PIDC Name
   */
  private static final int POS_PIDC_NAME = 1;
  /**
   * Position - version ID
   */
  private static final int POS_PIDC_VERS_ID = 2;
  /**
   * Position - version name
   */
  private static final int POS_PIDC_VERS_NAME = 3;
  /**
   * Position - variant ID
   */
  private static final int POS_VAR_ID = 4;
  /**
   * Position - variant name
   */
  private static final int POS_VAR_NAME = 5;
  /**
   * Position - Sub variant ID
   */
  private static final int POS_SVAR_ID = 6;
  /**
   * Position - Sub variant name
   */
  private static final int POS_SVAR_NAME = 7;
  /**
   * Position - attribute ID
   */
  private static final int POS_ATTR_ID = 8;
  /**
   * Position - attribute name
   */
  private static final int POS_ATTR_NAME = 9;
  /**
   * Position - error type
   */
  private static final int POS_ERROR_TYPE = 10;
  /**
   * Position - Error level
   */
  private static final int POS_ERR_LEVEL = 11;
  /**
   * Position - Created user
   */
  private static final int POS_CRE_USR = 12;
  /**
   * Position - Created date
   */
  private static final int POS_CRE_DATE = 13;
  /**
   * Position - Last modified user
   */
  private static final int POS_MOD_USR = 14;
  /**
   * Position - Last modified date
   */
  private static final int POS_MOD_DATE = 15;
  /**
   * Position - Created user of parent
   */
  private static final int POS_CRE_USR_PAR = 16;
  /**
   * Position - Created date of parent
   */
  private static final int POS_CRE_DATE_PAR = 17;
  /**
   * Position - Last modified user of parent
   */
  private static final int POS_MOD_USR_PAR = 18;
  /**
   * Position - Last modified date of parent
   */
  private static final int POS_MOD_DATE_PAR = 19;
  /**
   * Maximum columns
   */
  private static final int MAX_COL = 20;

  /**
   * Output format
   */
  private static final String OUTPUT_FORMAT = "\t{" + POS_PIDC_ID + "}" + '\t' + "{" + POS_PIDC_NAME + "}" + '\t' +
      "{" + POS_PIDC_VERS_ID + "}" + '\t' + "{" + POS_PIDC_VERS_NAME + "}" + '\t' + "{" + POS_VAR_ID + "}" + '\t' +
      "{" + POS_VAR_NAME + "}" + '\t' + "{" + POS_SVAR_ID + "}" + '\t' + "{" + POS_SVAR_NAME + "}" + '\t' + "{" +
      POS_ATTR_ID + "}" + '\t' + "{" + POS_ATTR_NAME + "}" + '\t' + "{" + POS_ERROR_TYPE + "}" + '\t' + "{" +
      POS_ERR_LEVEL + "}" + '\t' + "{" + POS_CRE_USR + "}" + '\t' + "{" + POS_CRE_DATE + "}" + '\t' + "{" +
      POS_MOD_USR + "}" + '\t' + "{" + POS_MOD_DATE + "}" + '\t' + "{" + POS_CRE_USR_PAR + "}" + '\t' + "{" +
      POS_CRE_DATE_PAR + "}" + '\t' + "{" + POS_MOD_USR_PAR + "}" + '\t' + "{" + POS_MOD_DATE_PAR + "}";

  /**
   * Logger
   */
  private final ILoggerAdapter logger;

  /**
   * Validation results
   */
  private final SortedSet<IValidationResult> valResultSet;

  /**
   * Constructor
   *
   * @param logger logger
   * @param valResultSet validation results
   */
  ProjConsEvalLoggerReport(final ILoggerAdapter logger, final SortedSet<IValidationResult> valResultSet) {
    this.logger = logger;
    this.valResultSet = valResultSet;
  }

  /**
   * Log the results to the Logger. This will align the error inputs and messages to separate columns for easy
   * readability, copying to excel etc. The output is tab separated.
   */
  public void createReport() {
    ProjectSubvariantConsInfo svarInfo;
    ProjectVariantConsInfo varInfo;
    PidcVersionConsInfo projVersInfo;
    PidcConsInfo pidcInfo;
    String message;

    // Title Row, required only if error records are present
    if ((this.valResultSet != null) && !this.valResultSet.isEmpty()) {
      this.logger.info(createMessage(POS_PIDC_ID, "PIDC ID", POS_PIDC_NAME, "Project Name", POS_PIDC_VERS_ID,
          "PIDC Version ID", POS_PIDC_VERS_NAME, "PIDC Version Name", POS_VAR_ID, "Variant ID", POS_VAR_NAME,
          "Variant Name", POS_SVAR_ID, "Sub-variant ID", POS_SVAR_NAME, "Sub-variant Name", POS_ATTR_ID, "Attribute ID",
          POS_ATTR_NAME, "Attribute Name", POS_ERROR_TYPE, "Error Type", POS_ERR_LEVEL, "Error Level", POS_CRE_USR,
          "Created User", POS_CRE_DATE, "Created Date", POS_MOD_USR, "Last Modified User", POS_MOD_DATE,
          "Last Modified Date", POS_CRE_USR_PAR, "Created User - Parent", POS_CRE_DATE_PAR, "Created Date - Parent",
          POS_MOD_USR_PAR, "Last Modified User - Parent", POS_MOD_DATE_PAR, "Last Modified Date - Parent"));
    }
    for (IValidationResult result : this.valResultSet) {

      // instanceof is checked in descending hierarchy of object inheritence
      if (result instanceof ProjectSubvariantConsInfo) {
        svarInfo = (ProjectSubvariantConsInfo) result;
        message = createMessage(POS_PIDC_ID, svarInfo.getProjectID(), POS_PIDC_NAME, svarInfo.getProjectName(),
            POS_PIDC_VERS_ID, svarInfo.getPidcVersID(), POS_PIDC_VERS_NAME, svarInfo.getPidcVersName(), POS_VAR_ID,
            svarInfo.getVariantID(), POS_VAR_NAME, svarInfo.getVariantName(), POS_SVAR_ID, svarInfo.getSubvariantID(),
            POS_SVAR_NAME, svarInfo.getSubvariantName(), POS_ATTR_ID, svarInfo.getAttrID(), POS_ATTR_NAME,
            svarInfo.getAttrName(), POS_ERROR_TYPE, svarInfo.getErrorType(), POS_ERR_LEVEL, svarInfo.getErrorLevel(),
            POS_CRE_USR, svarInfo.getCreatedUser(), POS_CRE_DATE, svarInfo.getCreatedDate(), POS_MOD_USR,
            svarInfo.getLastModifiedUser(), POS_MOD_DATE, svarInfo.getLastModifiedDate(), POS_CRE_USR_PAR,
            svarInfo.getCreatedUserParent(), POS_CRE_DATE_PAR, svarInfo.getCreatedDateParent(), POS_MOD_USR_PAR,
            svarInfo.getLastModifiedUserParent(), POS_MOD_DATE_PAR, svarInfo.getLastModifiedDateParent());
      }
      else if (result instanceof ProjectVariantConsInfo) {
        varInfo = (ProjectVariantConsInfo) result;
        message = createMessage(POS_PIDC_ID, varInfo.getProjectID(), POS_PIDC_NAME, varInfo.getProjectName(),
            POS_PIDC_VERS_ID, varInfo.getPidcVersID(), POS_PIDC_VERS_NAME, varInfo.getPidcVersName(), POS_VAR_ID,
            varInfo.getVariantID(), POS_VAR_NAME, varInfo.getVariantName(), POS_ATTR_ID, varInfo.getAttrID(),
            POS_ATTR_NAME, varInfo.getAttrName(), POS_ERROR_TYPE, varInfo.getErrorType(), POS_ERR_LEVEL,
            varInfo.getErrorLevel(), POS_CRE_USR, varInfo.getCreatedUser(), POS_CRE_DATE, varInfo.getCreatedDate(),
            POS_MOD_USR, varInfo.getLastModifiedUser(), POS_MOD_DATE, varInfo.getLastModifiedDate(), POS_CRE_USR_PAR,
            varInfo.getCreatedUserParent(), POS_CRE_DATE_PAR, varInfo.getCreatedDateParent(), POS_MOD_USR_PAR,
            varInfo.getLastModifiedUserParent(), POS_MOD_DATE_PAR, varInfo.getLastModifiedDateParent());
      }
      else if (result instanceof PidcVersionConsInfo) {
        projVersInfo = (PidcVersionConsInfo) result;
        message = createMessage(POS_PIDC_ID, projVersInfo.getProjectID(), POS_PIDC_NAME, projVersInfo.getProjectName(),
            POS_PIDC_VERS_ID, projVersInfo.getPidcVersID(), POS_PIDC_VERS_NAME, projVersInfo.getPidcVersName(),
            POS_ATTR_ID, projVersInfo.getAttrID(), POS_ATTR_NAME, projVersInfo.getAttrName(), POS_ERROR_TYPE,
            projVersInfo.getErrorType(), POS_ERR_LEVEL, projVersInfo.getErrorLevel(), POS_CRE_USR,
            projVersInfo.getCreatedUser(), POS_CRE_DATE, projVersInfo.getCreatedDate(), POS_MOD_USR,
            projVersInfo.getLastModifiedUser(), POS_MOD_DATE, projVersInfo.getLastModifiedDate(), POS_CRE_USR_PAR,
            projVersInfo.getCreatedUserParent(), POS_CRE_DATE_PAR, projVersInfo.getCreatedDateParent(), POS_MOD_USR_PAR,
            projVersInfo.getLastModifiedUserParent(), POS_MOD_DATE_PAR, projVersInfo.getLastModifiedDateParent());
      }
      else if (result instanceof PidcConsInfo) {
        pidcInfo = (PidcConsInfo) result;
        message = createMessage(POS_PIDC_ID, pidcInfo.getProjectID(), POS_PIDC_NAME, pidcInfo.getProjectName(),
            POS_ERROR_TYPE, pidcInfo.getErrorType(), POS_ERR_LEVEL, pidcInfo.getErrorLevel(), POS_CRE_USR,
            pidcInfo.getCreatedUser(), POS_CRE_DATE, pidcInfo.getCreatedDate(), POS_MOD_USR,
            pidcInfo.getLastModifiedUser(), POS_MOD_DATE, pidcInfo.getLastModifiedDate(), POS_CRE_USR_PAR,
            pidcInfo.getCreatedUserParent(), POS_CRE_DATE_PAR, pidcInfo.getCreatedDateParent(), POS_MOD_USR_PAR,
            pidcInfo.getLastModifiedUserParent(), POS_MOD_DATE_PAR, pidcInfo.getLastModifiedDateParent());
      }
      else {
        // Generic messages
        message = createMessage(POS_ERROR_TYPE, result.getErrorType(), POS_ERR_LEVEL, "ROOT");
      }

      // Log the inconsistant records as warnings
      this.logger.warn(message);
    }

  }

  /**
   * Create error message
   *
   * @param inputs inputs
   * @return formatted message
   */
  private String createMessage(final Object... inputs) {
    final ConcurrentMap<Integer, Object> inputMap = new ConcurrentHashMap<>();
    // Find the inputs to be set to the message and their index.
    for (int arrIndx = 0; arrIndx < inputs.length; arrIndx = arrIndx + MSG_INDX_INCR) {
      int key = Integer.parseInt(inputs[arrIndx].toString());
      Object val = inputs[arrIndx + 1];
      if (val == null) {
        val = "";
      }
      else if (val instanceof Calendar) {
        val = ((Calendar) val).getTime().toString();
      }
      inputMap.put(key, val);
    }

    // Arrange the inputs in the correct order to an array for MessageFormat
    Object[] formatInput = new Object[MAX_COL];
    for (int pos = 0; pos < MAX_COL; pos++) {
      Object item = inputMap.get(pos);
      if (item == null) {
        item = "";
      }
      formatInput[pos] = item.toString();
    }


    return MessageFormat.format(OUTPUT_FORMAT, formatInput);
  }
}
