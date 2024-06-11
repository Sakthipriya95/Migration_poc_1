/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.differences.adapter;

import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangeHistory;


/**
 * @author imi2si
 */
public interface PIDCChangedHistAdapter {

  public static final String STRING_NO_MODIFICATION = "Not Modified";
  public static final String STRING_NOT_DEFINED = "Not Defined";
  public static final long LONG_NO_MODIFICATION = java.lang.Long.MIN_VALUE;

  public PidcChangeHistory getHistEntryOld();

  public boolean isModified();

  public void removeEquals() throws DataException;
}
