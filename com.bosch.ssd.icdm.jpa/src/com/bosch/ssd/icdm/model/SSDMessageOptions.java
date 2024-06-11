/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.model;

import java.util.ArrayList;
import java.util.List;


/**
 * @author mrf5cob
 */
//ALM-294077-All inputs w.r.to SSDMessageOptions are related to this ticket
public class SSDMessageOptions {

  private SSDMessage ssdMessage;

  private List<String> noNodeBcList = new ArrayList<>();


  /**
   * @return the ssdMessage
   */
  public SSDMessage getSsdMessage() {
    return this.ssdMessage;
  }


  /**
   * @param ssdMessage the ssdMessage to set
   */
  public void setSsdMessage(final SSDMessage ssdMessage) {
    this.ssdMessage = ssdMessage;
  }


  /**
   * @return the noNodeBcList
   */
  public List<String> getNoNodeBcList() {
    return new ArrayList<>(this.noNodeBcList);
  }


  /**
   * @param noNodeBcList the noNodeBcList to set
   */
  public void setNoNodeBcList(final List<String> noNodeBcList) {
    this.noNodeBcList = new ArrayList<>(noNodeBcList);
  }

}
