/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.cocwp;

import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author UKT1COB
 */
/**
 * CoC WP Used Flag types.
 */
public enum CoCWPUsedFlag {

                           /** Used flag YES. */
                           YES("Y", ApicConstants.USED_YES_DISPLAY),

                           /** Used flag NO. */
                           NO("N", ApicConstants.USED_NO_DISPLAY),

                           /** Not defined. */
                           NOT_DEFINED("?", ApicConstants.USED_NOTDEF_DISPLAY),

                           /** New COC WP. */
                           NEW_COCWP("", "NEW");

  /** value in database column. */
  private final String dbType;

  /** Display value in UI. */
  private final String uiType;

  /**
   * Instantiates a new CoC WP used flag.
   *
   * @param dbType the db type
   * @param uiType the ui type
   */
  CoCWPUsedFlag(final String dbType, final String uiType) {
    this.dbType = dbType;
    this.uiType = uiType;
  }

  /**
   * Gets the db type.
   *
   * @return DB type literal
   */
  public final String getDbType() {
    return this.dbType;
  }

  /**
   * Gets the ui type.
   *
   * @return UI Type string
   */
  public final String getUiType() {
    return this.uiType;
  }

  /**
   * Return the coc wp used flag enum value for the given db type.
   *
   * @param dbType db literal of type
   * @return the file type object
   */
  public static CoCWPUsedFlag getType(final String dbType) {
    String typToCheck = ModelUtil.checkNull(dbType);
    for (CoCWPUsedFlag type : CoCWPUsedFlag.values()) {
      if (type.dbType.equals(typToCheck)) {
        return type;
      }
    }
    return null;
  }

  /**
   * Return the coc wp used flag db string for ui string.
   *
   * @param uiType ui literal of type
   * @return the file type object
   */
  public static String getDbType(final String uiType) {
    for (CoCWPUsedFlag type : CoCWPUsedFlag.values()) {
      if (type.uiType.equals(uiType)) {
        return type.dbType;
      }
    }
    return "";
  }
}

