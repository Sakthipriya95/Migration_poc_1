/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;


/**
 * @author RDP2COB
 */
public class TreeViewFlagValueProvider {


  boolean loadA2l = false;
  boolean loadCdr = false;
  boolean loadVariants = false;
  boolean loadQnaires = false;
  boolean includeDelChildren = false;
  boolean includeInactiveA2lFiles = false;
  boolean isTreeViewPart = false;

  /**
   * @param loadA2l
   * @param loadCdr
   * @param loadVariants
   * @param loadQnaires
   * @param includeDelChildren
   * @param includeInactiveA2lFiles
   * @param isTreeViewPart
   */
  public TreeViewFlagValueProvider(final boolean loadA2l, final boolean loadCdr, final boolean loadVariants,
      final boolean loadQnaires, final boolean includeDelChildren, final boolean includeInactiveA2lFiles,
      final boolean isTreeViewPart) {
    super();
    this.loadA2l = loadA2l;
    this.loadCdr = loadCdr;
    this.loadVariants = loadVariants;
    this.loadQnaires = loadQnaires;
    this.includeDelChildren = includeDelChildren;
    this.includeInactiveA2lFiles = includeInactiveA2lFiles;
    this.isTreeViewPart = isTreeViewPart;
  }


  /**
   * @return the loadA2l
   */
  public boolean isLoadA2l() {
    return this.loadA2l;
  }

  /**
   * @param loadA2l the loadA2l to set
   */
  public void setLoadA2l(final boolean loadA2l) {
    this.loadA2l = loadA2l;
  }

  /**
   * @return the loadCdr
   */
  public boolean isLoadCdr() {
    return this.loadCdr;
  }

  /**
   * @param loadCdr the loadCdr to set
   */
  public void setLoadCdr(final boolean loadCdr) {
    this.loadCdr = loadCdr;
  }

  /**
   * @return the loadVariants
   */
  public boolean isLoadVariants() {
    return this.loadVariants;
  }

  /**
   * @param loadVariants the loadVariants to set
   */
  public void setLoadVariants(final boolean loadVariants) {
    this.loadVariants = loadVariants;
  }

  /**
   * @return the loadQnaires
   */
  public boolean isLoadQnaires() {
    return this.loadQnaires;
  }

  /**
   * @param loadQnaires the loadQnaires to set
   */
  public void setLoadQnaires(final boolean loadQnaires) {
    this.loadQnaires = loadQnaires;
  }

  /**
   * @return the includeDelChildren
   */
  public boolean isIncludeDelChildren() {
    return this.includeDelChildren;
  }

  /**
   * @param includeDelChildren the includeDelChildren to set
   */
  public void setIncludeDelChildren(final boolean includeDelChildren) {
    this.includeDelChildren = includeDelChildren;
  }

  /**
   * @return the includeInactiveA2lFiles
   */
  public boolean isIncludeInactiveA2lFiles() {
    return this.includeInactiveA2lFiles;
  }

  /**
   * @param includeInactiveA2lFiles the includeInactiveA2lFiles to set
   */
  public void setIncludeInactiveA2lFiles(final boolean includeInactiveA2lFiles) {
    this.includeInactiveA2lFiles = includeInactiveA2lFiles;
  }

  /**
   * @return the isTreeViewPart
   */
  public boolean isTreeViewPart() {
    return this.isTreeViewPart;
  }

  /**
   * @param isTreeViewPart the isTreeViewPart to set
   */
  public void setTreeViewPart(final boolean isTreeViewPart) {
    this.isTreeViewPart = isTreeViewPart;
  }
}
