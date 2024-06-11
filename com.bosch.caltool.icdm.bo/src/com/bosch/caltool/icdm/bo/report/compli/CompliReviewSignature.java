/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.report.compli;

/**
 * @author hnu1cob
 */
public enum CompliReviewSignature {

                                   /**
                                    * Unique Instance for Single class implementation
                                    */
                                   INSTANCE;

  /**
                                   *
                                   */
  private static final String SIGNATURE_BR = "Signature<br>";

  /**
                                   *
                                   */
  private static final String SIGNATURE_1_BR = "Signature 1<br>";

  /**
                                   *
                                   */
  private static final String SIGNATURE_2_BR = "Signature 2<br>";

  /**
                                   *
                                   */
  private static final String PROJECT_RESPONSIBLE_BR = "Project Responsible<br>";

  /**
                                   *
                                   */
  private static final String EXPERT_PROJECT_BR = "Expert Project<br>";

  /**
                                   *
                                   */
  private static final String EXPERT_CO_C_CAL_BR = "Expert CoC Cal.<br>";

  /**
                                   *
                                   */
  private static final String CAL_EXPERT_BR = "CAL expert<br>";

  /**
                                         *
                                         */
  private static final String DIGITALLY_SIGNED = "(digitally signed)";

  /**
   * @return the unique instance of this class
   */
  public static CompliReviewSignature getInstance() {
    return INSTANCE;
  }

  private static final String CSSD_APPROVAL1_BEG = "Approval for release - Not Active";

  private static final String CSSD_APPROVAL2_BEG = "Approval for release - Active";

  private static final String CSSD_APPROVAL1_OTHERS = "Approval for reason - Not Active";

  private static final String CSSD_APPROVAL2_OTHERS = "Approval for reason - Active";

  private static final String CSSD_SIGN1_BEG =
      SIGNATURE_1_BR + PROJECT_RESPONSIBLE_BR + "Engineer [Role: PM or PCE]<br>" + DIGITALLY_SIGNED;

  private static final String CSSD_SIGN2_BEG =
      SIGNATURE_2_BR + "CAL expert BEG/EAx-SSD2Rv<br>" + "Team<br>" + DIGITALLY_SIGNED;

  private static final String CSSD_SIGN3_BEG =
      SIGNATURE_BR + PROJECT_RESPONSIBLE_BR + "Engineer [Role: PM or PCE]<br>" + "see (3)<br>" + DIGITALLY_SIGNED;

  private static final String CSSD_SIGN1_OTHERS =
      SIGNATURE_1_BR + EXPERT_PROJECT_BR + "Responsible Engineer [Role: PA]<br>" + DIGITALLY_SIGNED;


  private static final String CSSD_SIGN2_OTHERS = SIGNATURE_2_BR + "Expert CoC Calibration<br>" + DIGITALLY_SIGNED;

  private static final String CSSD_SIGN3_OTHERS =
      SIGNATURE_BR + EXPERT_PROJECT_BR + "Responsible Engineer [Role: PA]<br>" + "see (3)<br>" + DIGITALLY_SIGNED;

  private static final String QSSD_APPROVAL1_BEG = "Confirmation in case of BEG Responsibility";

  private static final String QSSD_APPROVAL2_BEG = "Confirmation in case of Customer or Third party";

  private static final String QSSD_APPROVAL1_OTHERS = "Confirmation in case of Bosch Responsibility";

  private static final String QSSD_APPROVAL2_OTHERS = "Confirmation in case of Customer or Third party";


  private static final String QSSD_SIGN1_BEG =
      SIGNATURE_1_BR + CAL_EXPERT_BR + "BEG/EAx-SSD2Rv Team<br>" + DIGITALLY_SIGNED;

  private static final String QSSD_SIGN2_BEG =
      SIGNATURE_2_BR + CAL_EXPERT_BR + "BEG/EAx-SSD2Rv Team<br>" + DIGITALLY_SIGNED;

  private static final String QSSD_SIGN3_BEG =
      "Signature 3<br>" + "(only in case of escalation)<br>" + "Management<br>" + DIGITALLY_SIGNED;

  private static final String QSSD_SIGN4_BEG = SIGNATURE_BR + PROJECT_RESPONSIBLE_BR + "Engineer<br>" +
      "[Role: PM or PCE] - see (5)<br>" + DIGITALLY_SIGNED;


  private static final String QSSD_SIGN1_OTHERS = SIGNATURE_1_BR + EXPERT_CO_C_CAL_BR + DIGITALLY_SIGNED;

  private static final String QSSD_SIGN2_OTHERS = SIGNATURE_2_BR + "DH of Expert CoC Cal.<br>" + DIGITALLY_SIGNED;

  private static final String QSSD_SIGN3_OTHERS =
      "Signature 3<br>" + "SH <br>" + "Problem solving <br>" + "experts <br>" + DIGITALLY_SIGNED;

  private static final String QSSD_SIGN4_OTHERS = "Signature <br>" + "Expert <br>" + "Project responsible<br>" +
      "engineer<br>" + "[Role: PA] - see (5)<br>" + DIGITALLY_SIGNED;


  private static final String SSD2RV_STD_RELEASE_BEG = "Standard release by CAL expert BEG/EAx-SSD2Rv Team";

  private static final String SSD2RV_CODEX_CASE_BEG = "Release on concern by Codex Case";

  private static final String SSD2RV_STD_RELEASE_OTHERS = "Standard release by CAL Expert (CoC)";

  private static final String SSD2RV_CODEX_CASE_OTHERS = "Release on concern by Codex Case";


  private static final String SSD2RV_SIGN1_BEG = SIGNATURE_1_BR + CAL_EXPERT_BR + DIGITALLY_SIGNED;

  private static final String SSD2RV_SIGN2_BEG = SIGNATURE_2_BR + CAL_EXPERT_BR + DIGITALLY_SIGNED;

  private static final String SSD2RV_SIGN3_BEG =
      "Codex Case<br>" + "for exactly this dataset<br>" + "release <br>" + "(ALM) ID";

  private static final String SSD2RV_SIGN4_BEG = SIGNATURE_BR + PROJECT_RESPONSIBLE_BR + "Engineer<br>" +
      "[Role: PM or PCE]<br> - see (4)<br>" + DIGITALLY_SIGNED;


  private static final String SSD2RV_SIGN1_OTHERS = SIGNATURE_1_BR + EXPERT_CO_C_CAL_BR + DIGITALLY_SIGNED;

  private static final String SSD2RV_SIGN2_OTHERS = SIGNATURE_2_BR + EXPERT_CO_C_CAL_BR + DIGITALLY_SIGNED;

  private static final String SSD2RV_SIGN3_OTHERS = "Codex Case<br>" + "(ALM) ID";

  private static final String SSD2RV_SIGN4_OTHERS = "Signature <br>" + EXPERT_PROJECT_BR +
      "Responsible Engineer<br>" + "[Role: PA] see (4)<br>" + DIGITALLY_SIGNED;

  /**
   * @param projectOrgBEG
   * @return
   */
  public String[] getColumnHeadersForCSSDSignature(final boolean projectOrgBEG) {
    String[] columnHeadersForCssd = new String[5];
    if (projectOrgBEG) {
      columnHeadersForCssd[0] = CSSD_SIGN1_BEG;
      columnHeadersForCssd[1] = CSSD_SIGN2_BEG;
      columnHeadersForCssd[2] = CSSD_SIGN3_BEG;
      columnHeadersForCssd[3] = CSSD_APPROVAL1_BEG;
      columnHeadersForCssd[4] = CSSD_APPROVAL2_BEG;
    }
    else {
      columnHeadersForCssd[0] = CSSD_SIGN1_OTHERS;
      columnHeadersForCssd[1] = CSSD_SIGN2_OTHERS;
      columnHeadersForCssd[2] = CSSD_SIGN3_OTHERS;
      columnHeadersForCssd[3] = CSSD_APPROVAL1_OTHERS;
      columnHeadersForCssd[4] = CSSD_APPROVAL2_OTHERS;
    }
    return columnHeadersForCssd;
  }

  /**
   * @param projectOrgBEG
   * @return
   */
  public String[] getColumnHeadersForQSSDSignature(final boolean projectOrgBEG) {
    String[] columnHeadersForQssd = new String[6];
    if (projectOrgBEG) {
      columnHeadersForQssd[0] = QSSD_SIGN1_BEG;
      columnHeadersForQssd[1] = QSSD_SIGN2_BEG;
      columnHeadersForQssd[2] = QSSD_SIGN3_BEG;
      columnHeadersForQssd[3] = QSSD_SIGN4_BEG;
      columnHeadersForQssd[4] = QSSD_APPROVAL1_BEG;
      columnHeadersForQssd[5] = QSSD_APPROVAL2_BEG;
    }
    else {
      columnHeadersForQssd[0] = QSSD_SIGN1_OTHERS;
      columnHeadersForQssd[1] = QSSD_SIGN2_OTHERS;
      columnHeadersForQssd[2] = QSSD_SIGN3_OTHERS;
      columnHeadersForQssd[3] = QSSD_SIGN4_OTHERS;
      columnHeadersForQssd[4] = QSSD_APPROVAL1_OTHERS;
      columnHeadersForQssd[5] = QSSD_APPROVAL2_OTHERS;
    }
    return columnHeadersForQssd;
  }

  /**
   * @param projectOrgBEG
   * @return
   */
  public String[] getColumnHeadersForSSD2RvSignature(final boolean projectOrgBEG) {
    String[] columnHeadersForSsd2Rv = new String[6];
    if (projectOrgBEG) {
      columnHeadersForSsd2Rv[0] = SSD2RV_SIGN1_BEG;
      columnHeadersForSsd2Rv[1] = SSD2RV_SIGN2_BEG;
      columnHeadersForSsd2Rv[2] = SSD2RV_SIGN3_BEG;
      columnHeadersForSsd2Rv[3] = SSD2RV_SIGN4_BEG;
      columnHeadersForSsd2Rv[4] = SSD2RV_STD_RELEASE_BEG;
      columnHeadersForSsd2Rv[5] = SSD2RV_CODEX_CASE_BEG;
    }
    else {
      columnHeadersForSsd2Rv[0] = SSD2RV_SIGN1_OTHERS;
      columnHeadersForSsd2Rv[1] = SSD2RV_SIGN2_OTHERS;
      columnHeadersForSsd2Rv[2] = SSD2RV_SIGN3_OTHERS;
      columnHeadersForSsd2Rv[3] = SSD2RV_SIGN4_OTHERS;
      columnHeadersForSsd2Rv[4] = SSD2RV_STD_RELEASE_OTHERS;
      columnHeadersForSsd2Rv[5] = SSD2RV_CODEX_CASE_OTHERS;
    }
    return columnHeadersForSsd2Rv;
  }
}
