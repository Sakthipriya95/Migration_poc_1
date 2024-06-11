/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * A node in the PIDC hierarchy
 */
public class PidcTreeNodeChildren {

  private boolean isOtherPidcVerPresent;

  private boolean isSdomPversPresent;

  private boolean isCdrPresent;

  private boolean isQuestionnairesPresent;

  private Set<SdomPVER> pidcSdomPverSet;

  private Set<PidcVariant> pidcVariants;

  /**
   * @return the isOtherPidcVerPresent
   */
  public boolean isOtherPidcVerPresent() {
    return this.isOtherPidcVerPresent;
  }


  /**
   * @param isOtherPidcVerPresent the isOtherPidcVerPresent to set
   */
  public void setOtherPidcVerPresent(final boolean isOtherPidcVerPresent) {
    this.isOtherPidcVerPresent = isOtherPidcVerPresent;
  }


  /**
   * @return the isSdomPversPresent
   */
  public boolean isSdomPversPresent() {
    return this.isSdomPversPresent;
  }


  /**
   * @param isSdomPversPresent the isSdomPversPresent to set
   */
  public void setSdomPversPresent(final boolean isSdomPversPresent) {
    this.isSdomPversPresent = isSdomPversPresent;
  }


  /**
   * @return the isCdrPresent
   */
  public boolean isCdrPresent() {
    return this.isCdrPresent;
  }


  /**
   * @param isCdrPresent the isCdrPresent to set
   */
  public void setCdrPresent(final boolean isCdrPresent) {
    this.isCdrPresent = isCdrPresent;
  }


  /**
   * @return the isQuestionnairesPresent
   */
  public boolean isQuestionnairesPresent() {
    return this.isQuestionnairesPresent;
  }


  /**
   * @param isQuestionnairesPresent the isQuestionnairesPresent to set
   */
  public void setQuestionnairesPresent(final boolean isQuestionnairesPresent) {
    this.isQuestionnairesPresent = isQuestionnairesPresent;
  }


  /**
   * @return the pidcSdomPverSet
   */
  public Set<SdomPVER> getPidcSdomPverSet() {
    return this.pidcSdomPverSet;
  }


  /**
   * @param pidcSdomPverSet the pidcSdomPverSet to set
   */
  public void setPidcSdomPverSet(final Set<SdomPVER> pidcSdomPverSet) {
    this.pidcSdomPverSet = pidcSdomPverSet == null ? null : new TreeSet<>(pidcSdomPverSet);
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
    this.pidcVariants = pidcVariants == null ? null : new HashSet<>(pidcVariants);
  }

}