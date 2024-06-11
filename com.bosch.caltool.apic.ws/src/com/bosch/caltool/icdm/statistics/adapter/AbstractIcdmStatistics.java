/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.statistics.adapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.bosch.caltool.apic.ws.PIDCVersionStatResponseType;


/**
 * A statistical representation of a PIDC
 *
 * @author imi2si
 */
public abstract class AbstractIcdmStatistics {

  /**
   * General date formater for this class and dubclasses
   */
  protected SimpleDateFormat dateFromater = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()); // NOPMD

  /**
   * ENUM of the possible levels of a PIDC
   */
  public enum PidcLevel {
                         /**
                          * Level marker for PIDC-Level
                          */
                         PIDC,

                         /**
                          * Level marker for Variant-Level
                          */
                         VARIANT,

                         /**
                          * Level marker for Sub-Variant-Level
                          */
                         SUB_VARIANT
  }

  /**
   * Creates a new format for date fields
   *
   * @param format the format String for date fields, e.g.: "dd.MM.yyyy"
   */
  public final void setDateFormat(final String format) {
    this.dateFromater = new SimpleDateFormat(format, Locale.getDefault());
  }


  /**
   * Returns the PIDC name
   *
   * @return the PIDC name
   */
  public abstract String getPidcName();

  /**
   * Returns the number of attributes that describe the PIDC (PIDC-Tree)
   *
   * @return the number of PIDC-Meta-Data levels
   */
  public abstract int getPidcNoOfTreeAttributes();

  /**
   * Returns the value of the of attributes that describe the PIDC on the passed level (PIDC-Tree)
   *
   * @param level the level, for which the attribute value should be returned
   * @return the value of the PIDC level
   */
  public abstract String getPidcTreeAttribute(int level);

  /**
   * Returns the creation date of the PIDC
   *
   * @return the Date of creation of the PIDC
   */
  public abstract Date getPidcCreateDate();

  /**
   * Returns the creation date of the PIDC as String, formated with the format passed with method
   * {@link #setDateFormat(String) setDateFormat} . By default, the format "dd.MM.yyyy" is used.
   *
   * @return the String representation of the Date of creation of the PIDC
   */
  public abstract String getPidcCreateDateString();

  /**
   * Returns the mail addresses of the owners of the PIDC
   *
   * @return the owners of the PIDC
   */
  public abstract String getPidcOwner();

  /**
   * Returns the modification date of the PIDC
   *
   * @return the Date of modification of the PIDC
   */
  public abstract Date getPidcModifyDate();

  /**
   * Returns the modification date of the PIDC as String, formated with the format passed with method
   * {@link #setDateFormat(String) setDateFormat}. By default, the format "dd.MM.yyyy" is used.
   *
   * @return the String representation of the Date of modification of the PIDC
   */
  public abstract String getPidcModifyDateString();


  /**
   * Returns the overall number of attributes of the PIDC
   *
   * @return the number of attributes of the PIDC
   */
  public abstract int getPidcNoOfAttributes();

  /**
   * Returns the number of mandatory attributes with value of the PIDC
   *
   * @return the number of madatory attributes with value of the PIDC
   */
  public abstract int getPidcNoOfMandAttr();

  /**
   * Returns the max number of mandatory attributes with value of the PIDC
   *
   * @return the max number of madatory attributes
   */
  public abstract int getPidcNoOfMandMax();

  /**
   * Returns the maintanence degree in percent of the mandatory attributes. Example:<br>
   * <ul>
   * <li>An attribute is set to level variant</li>
   * <li>There are four variants</li>
   * <li>Within three variants, a value is set. One variant has no value</li>
   * <li>The result would be a maintanence degree of 75%</li>
   * </ul>
   *
   * @return the number of madatory attributes with value of the PIDC
   */
  public abstract double getPidcPercMandAttr();

  /**
   * Returns the overall number of variants of the PIDC
   *
   * @return the number of variants of the PIDC
   */
  public abstract int getPidcNoOfVariants();

  /**
   * Returns the overall number of sub-variants of the PIDC
   *
   * @return the number of sub-variants of the PIDC
   */
  public abstract int getPidcNoOfSubVariants();

  /**
   * Returns the Usage Flag of the attribute Codex - EM relevant CAL
   *
   * @return the number of sub-variants of the PIDC
   */
  public abstract String getUsageOfCodexEmAttr();

  /**
   * Returns the Usage Flag of the attribute Codex - Dia relevant CAL
   *
   * @return the number of sub-variants of the PIDC
   */
  public abstract String getUsageOfCodexDiaAttr();

  /**
   * Returns the value of the attribut SAP NUmber
   *
   * @return the number of sub-variants of the PIDC
   */
  public abstract String getSAPNumber();

  /**
   * Returns the value of the attribut SPjM CAL
   *
   * @return the number of sub-variants of the PIDC
   */
  public abstract String getSpjmCal();

  /**
   * Adds a level statistic object to the PIDC. Multiple Objects for the same level can be passed, because one PIDC can
   * have several Variants.
   *
   * @param level the level for which statistics should be added.One of the types part of the enum PidcLevel.
   * @param statistics An instance of AbstractIcdmLevelStatistics representing the level to add.
   */
  public abstract void addLevelStatistics(PidcLevel level, AbstractIcdmLevelStatistics statistics);

  /**
   * Returns statistics for the given level.
   *
   * @param level the level for which statistics should be returned
   * @return the statistics for the passed level
   */
  public abstract AbstractIcdmLevelStatistics getLevelStatistics(PidcLevel level);


  /**
   * @return
   */
  public abstract int getCoverageMandAttr();

  public abstract int getCoverageMandAttrMax();

  public abstract double getCoverageMandPercentage();

  public abstract PIDCVersionStatResponseType getPidcHeaderCoverageReport();


  /**
   * @return
   */
  public abstract String getCalProjectOrga();


  /**
   * @return
   */
  public abstract double getUnratedFocusMatrix();


  /**
   * @return
   */
  public abstract double getCoveragePidcPercentage();


  /**
   * @return
   */
  public abstract int getCoveragePidcMax();


  /**
   * @return
   */
  public abstract int getCoveragePidc();


  /**
   * @return
   */
  public abstract double getProjectUseCases();


  /**
   * @return
   */
  public abstract double getNewAttributes();


  /**
   * @return
   */
  public abstract double getNotDefinedAttributes();

  /**
   * @return
   */
  public abstract double getUsedAttributes();

  /**
   * @return
   */
  public abstract double getNotUsedAttributes();

  /**
   * @return
   */
  public abstract double getFocusMatrixApplicabeAttributes();

  /**
   * @return
   */
  public abstract Calendar getLastConfirmationDate();

  /**
   * @return
   */
  public abstract String getLastConfirmationDateAsString();
}
