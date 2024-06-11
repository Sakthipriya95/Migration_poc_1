/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.wizards;

import java.util.Set;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * ICDM-1704
 *
 * @author mkl2cob
 */
public class CdrReportGenerationWizardData {


  /**
   * A2LFile instance
   */
  private PidcA2l pidcA2l;

  /**
   * PIDCVersion instance
   */
  private PidcVersion pidcVers;

  private Boolean variantsAvailable;

  /**
   * boolean to indicate whether the report is to be generated for last review or last n review
   */
  // initialised to true as the radio button will be checked for last review
  private boolean lastReview = true;


  /**
   * Number of reviews
   */
  // while opening
  private int numOfReviews = 1;
  /**
   * Flag to fetch checkvalue
   */
  private boolean fetchCheckVal;

  /**
   * pidc Varaint lists created
   */
  private Set<PidcVariant> pidcVaraints;

  /**
   * @return the fetchCheckVal
   */
  public boolean isFetchCheckVal() {
    return this.fetchCheckVal;
  }


  /**
   * @param fetchCheckVal the fetchCheckVal to set
   */
  public void setFetchCheckVal(final boolean fetchCheckVal) {
    this.fetchCheckVal = fetchCheckVal;
  }


  /**
   * @return the numOfReviews
   */
  public int getNumOfReviews() {
    return this.numOfReviews;
  }

  /**
   * @param numOfReviews the numOfReviews to set
   */
  public void setNumOfReviews(final int numOfReviews) {
    this.numOfReviews = numOfReviews;
  }

  /**
   * @return the lastReview
   */
  public boolean isLastReview() {
    return this.lastReview;
  }

  /**
   * @param lastReview the lastReview to set
   */
  public void setLastReview(final boolean lastReview) {
    this.lastReview = lastReview;
  }

  /**
   * @return the a2lFile
   */
  public PidcA2l getPidcA2l() {
    return this.pidcA2l;
  }

  /**
   * @param a2lFile the a2lFile to set
   */
  public void setPidcA2l(final PidcA2l a2lFile) {
    this.pidcA2l = a2lFile;
  }

  /**
   * @return true if variants are available
   */
  public boolean variantsAvailable() {
    if (this.variantsAvailable == null) {
      try {
        this.variantsAvailable = new PidcVariantServiceClient().hasVariant(this.pidcVers.getId(), false);
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }

    return (this.variantsAvailable != null) && this.variantsAvailable;
  }

  /**
   * @return the pidcVers
   */
  public PidcVersion getPidcVers() {
    return this.pidcVers;
  }

  /**
   * @param pidcVers the pidcVers to set
   */
  public void setPidcVers(final PidcVersion pidcVers) {
    this.pidcVers = pidcVers;
  }


  /**
   * @return the pidcVaraints
   */
  public Set<PidcVariant> getPidcVaraints() {
    return this.pidcVaraints;
  }


  /**
   * @param pidcVaraints the pidcVaraints to set
   */
  public void setPidcVaraints(final Set<PidcVariant> pidcVaraints) {
    this.pidcVaraints = pidcVaraints;
  }
}
