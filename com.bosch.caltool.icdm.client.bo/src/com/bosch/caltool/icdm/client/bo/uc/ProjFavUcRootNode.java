/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.uc;

import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * This class represents the project favourite usecases root node
 *
 * @author dmo5cob
 */
public class ProjFavUcRootNode {

  /**
   * @return rootname
   */

  public String getName() {
    return ApicConstants.PROJ_USE_CASE_ROOT_NODE_NAME;
  }

  /**
   * @return description
   */

  public String getDescription() {
    return ApicConstants.PROJ_USECASE_ROOT_DESC;
  }


  /**
   * @return tooltip of this node
   */
  public String getToolTip() {
    return getDescription();
  }
}
