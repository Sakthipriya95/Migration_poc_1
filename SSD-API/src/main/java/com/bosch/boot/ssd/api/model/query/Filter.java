/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.model.query;

import java.util.List;

/**
 * @author GDH9COB
 *
 */
public class Filter {
  
  private List<String> labelName;

  
  /**
   * @return the labelName
   */
  public List<String> getLabelName() {
    return labelName;
  }

  
  /**
   * @param labelName the labelName to set
   */
  public void setLabelName(List<String> labelName) {
    this.labelName = labelName;
  }
  

}
