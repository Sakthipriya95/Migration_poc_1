/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.wizards;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.WpRespModel;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.cdfx.CdfxExportOutput;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author say8cob
 */
public class CDFXExportWizardData {

  private A2LFileInfo a2lFileInfo;

  /**
   * A2LFile instance
   */
  private PidcA2l pidcA2l;

  /**
   * PIDCVersion instance
   */
  private PidcVersion pidcVers;

  /**
   * PIDC instance
   */
  private Pidc pidc;

  /**
   * pidc Varaint
   */
  private PidcVariant pidcVariant;


  private PidcVariant defaultPidcVariant;

  private Set<PidcVariant> pidcVariants = new HashSet<>();


  /**
   * Is pidc Varaint available
   */
  private Boolean variantsAvailable;

  private boolean containPreSelectedWP;

  private boolean boschScope;

  private boolean otherScope;

  private boolean customerScope;

  private boolean wpRespScope;

  private List<WpRespModel> wpRespModels = new ArrayList<>();

  private String outputDirecPath;

  private CdfxExportOutput cdfxExportOutput;

  private String cdfxReadinessConditionStr;

  /**
   * @return the pidcA2l
   */
  public PidcA2l getPidcA2l() {
    return this.pidcA2l;
  }


  /**
   * @param pidcA2l the pidcA2l to set
   */
  public void setPidcA2l(final PidcA2l pidcA2l) {
    this.pidcA2l = pidcA2l;
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
  public PidcVariant getPidcVariant() {
    return this.pidcVariant;
  }


  /**
   * @param pidcVariant the pidcVariant to set
   */
  public void setPidcVariant(final PidcVariant pidcVariant) {
    this.pidcVariant = pidcVariant;
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
   * @return the variantsAvailable
   */
  public Boolean isVariantsAvailable() {
    return this.variantsAvailable;
  }


  /**
   * @param variantsAvailable the variantsAvailable to set
   */
  public void setVariantsAvailable(final Boolean variantsAvailable) {
    this.variantsAvailable = variantsAvailable;
  }


  /**
   * @return the a2lFileInfo
   */
  public A2LFileInfo getA2lFileInfo() {
    return this.a2lFileInfo;
  }


  /**
   * @param a2lFileInfo the a2lFileInfo to set
   */
  public void setA2lFileInfo(final A2LFileInfo a2lFileInfo) {
    this.a2lFileInfo = a2lFileInfo;
  }


  /**
   * @return the boschScope
   */
  public boolean isBoschScope() {
    return this.boschScope;
  }


  /**
   * @param boschScope the boschScope to set
   */
  public void setBoschScope(final boolean boschScope) {
    this.boschScope = boschScope;
  }


  /**
   * @return the otherScope
   */
  public boolean isOtherScope() {
    return this.otherScope;
  }


  /**
   * @param otherScope the otherScope to set
   */
  public void setOtherScope(final boolean otherScope) {
    this.otherScope = otherScope;
  }


  /**
   * @return the customerScope
   */
  public boolean isCustomerScope() {
    return this.customerScope;
  }


  /**
   * @param customerScope the customerScope to set
   */
  public void setCustomerScope(final boolean customerScope) {
    this.customerScope = customerScope;
  }


  /**
   * @return the wpRespScope
   */
  public boolean isWpRespScope() {
    return this.wpRespScope;
  }


  /**
   * @param wpRespScope the wpRespScope to set
   */
  public void setWpRespScope(final boolean wpRespScope) {
    this.wpRespScope = wpRespScope;
  }


  /**
   * @return the wpRespModels
   */
  public List<WpRespModel> getWpRespModels() {
    return this.wpRespModels;
  }


  /**
   * @param wpRespModels the wpRespModels to set
   */
  public void setWpRespModels(final List<WpRespModel> wpRespModels) {
    this.wpRespModels = wpRespModels;
  }


  /**
   * @return the outputDirecPath
   */
  public String getOutputDirecPath() {
    return this.outputDirecPath;
  }


  /**
   * @param outputDirecPath the outputDirecPath to set
   */
  public void setOutputDirecPath(final String outputDirecPath) {
    this.outputDirecPath = outputDirecPath;
  }

  /**
   * @return CdfxExportOutputModel
   */
  public CdfxExportOutput getCdfxExportOutput() {
    return this.cdfxExportOutput;
  }

  /**
   * @param cdfxExportOutput CdfxExportOutput
   */

  public void setCdfxExportOutputModel(final CdfxExportOutput cdfxExportOutput) {
    this.cdfxExportOutput = cdfxExportOutput;
  }


  /**
   * @return the pidc
   */
  public Pidc getPidc() {
    return this.pidc;
  }


  /**
   * @param pidc the pidc to set
   */
  public void setPidc(final Pidc pidc) {
    this.pidc = pidc;
  }


  /**
   * @return the cdfxReadinessConditionStr
   */
  public String getCdfxReadinessConditionStr() {
    return this.cdfxReadinessConditionStr;
  }

  /**
   * @param cdfxReadinessConditionStr the cdfxReadinessConditionStr to set
   */
  public void setCdfxReadinessConditionStr(final String cdfxReadinessConditionStr) {
    this.cdfxReadinessConditionStr = cdfxReadinessConditionStr;
  }


  /**
   * @return the defaultPidcVariant
   */
  public PidcVariant getDefaultPidcVariant() {
    return this.defaultPidcVariant;
  }


  /**
   * @param defaultPidcVariant the defaultPidcVariant to set
   */
  public void setDefaultPidcVariant(final PidcVariant defaultPidcVariant) {
    this.defaultPidcVariant = defaultPidcVariant;
  }


  /**
   * @return the containPreSelectedWP
   */
  public boolean isContainPreSelectedWP() {
    return this.containPreSelectedWP;
  }


  /**
   * @param containPreSelectedWP the containPreSelectedWP to set
   */
  public void setContainPreSelectedWP(final boolean containPreSelectedWP) {
    this.containPreSelectedWP = containPreSelectedWP;
  }

  /**
   * @return the pidcVariants
   */
  public Set<PidcVariant> getPidcVariants() {
    return this.pidcVariants;
  }


  /**
   * @param pidcVariants the pidcVariants to set
   */
  public void setPidcVariants(final Set<PidcVariant> pidcVariants) {
    this.pidcVariants = pidcVariants;
  }

}
