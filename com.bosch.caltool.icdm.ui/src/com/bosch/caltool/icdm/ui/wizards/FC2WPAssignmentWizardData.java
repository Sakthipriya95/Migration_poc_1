/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.wizards;

import java.util.Set;

import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPDef;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMappingWithDetails;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPVersion;

/**
 * @author bru2cob
 */
public class FC2WPAssignmentWizardData {


  private boolean createWP;

  private Set<FC2WPDef> fc2wpDefSet;

  private AttributeValue selDivision;

  private String fc2wpName;

  private boolean createNewFC2WP;

  private FC2WPDef existingFC2WP;

  private FC2WPDef openExistingFC2WP;

  private FC2WPDef newDef;
  private FC2WPVersion newActiveVers;
  private FC2WPMappingWithDetails newFc2wpMapping;

  /**
   * @return the createWP
   */
  public boolean isCreateWP() {
    return this.createWP;
  }


  /**
   * @param createWP the createWP to set
   */
  public void setCreateWP(final boolean createWP) {
    this.createWP = createWP;
  }


  /**
   * @return the fc2wpDefSet
   */
  public Set<FC2WPDef> getFc2wpDefSet() {
    return this.fc2wpDefSet;
  }


  /**
   * @param fc2wpDefSet the fc2wpDefSet to set
   */
  public void setFc2wpDefSet(final Set<FC2WPDef> fc2wpDefSet) {
    this.fc2wpDefSet = fc2wpDefSet;
  }


  /**
   * @return the selDivision
   */
  public AttributeValue getSelDivision() {
    return this.selDivision;
  }


  /**
   * @param attributeValue the selDivision to set
   */
  public void setSelDivision(final AttributeValue attributeValue) {
    this.selDivision = attributeValue;
  }


  /**
   * @return the fc2wpName
   */
  public String getFc2wpName() {
    return this.fc2wpName;
  }


  /**
   * @param fc2wpName the fc2wpName to set
   */
  public void setFc2wpName(final String fc2wpName) {
    this.fc2wpName = fc2wpName;
  }


  /**
   * @return the createNewFC2WP
   */
  public boolean isCreateNewFC2WP() {
    return this.createNewFC2WP;
  }


  /**
   * @param createNewFC2WP the createNewFC2WP to set
   */
  public void setCreateNewFC2WP(final boolean createNewFC2WP) {
    this.createNewFC2WP = createNewFC2WP;
  }


  /**
   * @return the existingFC2WP
   */
  public FC2WPDef getExistingFC2WP() {
    return this.existingFC2WP;
  }


  /**
   * @param existingFC2WP the existingFC2WP to set
   */
  public void setExistingFC2WP(final FC2WPDef existingFC2WP) {
    this.existingFC2WP = existingFC2WP;
  }


  /**
   * @return the openExistingFC2WP
   */
  public FC2WPDef getOpenExistingFC2WP() {
    return this.openExistingFC2WP;
  }


  /**
   * @param openExistingFC2WP the openExistingFC2WP to set
   */
  public void setOpenExistingFC2WP(final FC2WPDef openExistingFC2WP) {
    this.openExistingFC2WP = openExistingFC2WP;
  }


  /**
   * @return the newDef
   */
  public FC2WPDef getNewDef() {
    return this.newDef;
  }


  /**
   * @param newDef the newDef to set
   */
  public void setNewDef(final FC2WPDef newDef) {
    this.newDef = newDef;
  }


  /**
   * @return the newActiveVers
   */
  public FC2WPVersion getNewActiveVers() {
    return this.newActiveVers;
  }


  /**
   * @param newActiveVers the newActiveVers to set
   */
  public void setNewActiveVers(final FC2WPVersion newActiveVers) {
    this.newActiveVers = newActiveVers;
  }


  /**
   * @return the newFc2wpMapping
   */
  public FC2WPMappingWithDetails getNewFc2wpMapping() {
    return this.newFc2wpMapping;
  }


  /**
   * @param newFc2wpMapping the newFc2wpMapping to set
   */
  public void setNewFc2wpMapping(final FC2WPMappingWithDetails newFc2wpMapping) {
    this.newFc2wpMapping = newFc2wpMapping;
  }


}
