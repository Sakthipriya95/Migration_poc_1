/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.shapereview;


/**
 * @author bne4cob
 */
public enum SHAPE_STATUS {
                          /**
                           * Not started
                           */
                          NOT_STARTED,
                          /**
                           * No BCs present in the PIDC that are risky
                           */
                          NO_RISKY_BCS,
                          /**
                           * CAT file not found in vCDM
                           */
                          CAT_FILE_NOT_FOUND,
                          /**
                           * No parameters present in review that are risky
                           */
                          NO_RISKY_PARAMS,
                          /**
                           * Shape analysis finished. No findings
                           */
                          FINISHED_NO_FINDINGS,
                          /**
                           * Shape analysis finished. Findings present
                           */
                          FINISHED_WITH_FINDINGS;
}
