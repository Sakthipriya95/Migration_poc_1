/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.actions;

import com.bosch.caltool.icdm.product.util.ICDMConstants;


/**
 * @author jvi6cob
 */
public class PIDCPerspectiveIntroAction extends AbstractPerspectiveIntroAction {


  @Override
  public String getPerspectiveId() {
    // Id of PIDC perspective
    return ICDMConstants.ID_PERSP_PIDC;
  }
}