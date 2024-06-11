/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.actions;

import com.bosch.caltool.icdm.product.util.ICDMConstants;


/**
 * @author bru2cob
 */
public class UseCasePerspectiveIntroAction extends AbstractPerspectiveIntroAction {


  @Override
  public String getPerspectiveId() {
    // Id of Usecase perspective
    return ICDMConstants.ID_PERSP_USECASE;
  }

}
