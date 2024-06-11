/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc.projcons;


/**
 * PIDC validation error levels
 *
 * @author bne4cob
 */
public enum ErrorLevel implements IErrorLevel {
                                               /**
                                                * PIDC
                                                */
                                               PIDC,
                                               /**
                                                * Version
                                                */
                                               VERSION,
                                               /**
                                                * pidcc Version level attribute
                                                */
                                               PIDC_ATTR,
                                               /**
                                                * Variant
                                                */
                                               VARIANT,
                                               /**
                                                * Variant Attribute
                                                */
                                               VARIANT_ATTR,
                                               /**
                                                * Sub variant
                                                */
                                               SUB_VARIANT,
                                               /**
                                                * Sub variant attribute
                                                */
                                               SUB_VARIANT_ATTR,
                                               /**
                                                * Focus Matrix Version
                                                */
                                               FM_VERSION;

  /**
   * {@inheritDoc}
   */
  @Override
  public String getLevel() {
    return toString();
  }

}
