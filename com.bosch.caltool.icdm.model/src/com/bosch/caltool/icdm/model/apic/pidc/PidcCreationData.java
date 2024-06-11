/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.icdm.model.uc.UsecaseFavorite;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.user.User;

/**
 * @author dja7cob
 */
public class PidcCreationData {

  private Pidc pidc;

  private Set<PidcVersion> allVersionSet = new HashSet<>();

  private List<UsecaseFavorite> selUcFav = new ArrayList<>();

  private final Map<Long, Long> structAttrValMap = new HashMap<>();

  private User owner;

  private PidcVersion copiedPidcVer;

  private NodeAccess createdNodeAccess;

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
   * @return the structAttrValMap
   */
  public Map<Long, Long> getStructAttrValMap() {
    return this.structAttrValMap;
  }


  /**
   * @return the allVersionSet
   */
  public Set<PidcVersion> getAllVersionSet() {
    return this.allVersionSet;
  }


  /**
   * @param allVersionSet the allVersionSet to set
   */
  public void setAllVersionSet(final Set<PidcVersion> allVersionSet) {
    this.allVersionSet = allVersionSet == null ? null : new HashSet<>(allVersionSet);
  }


  /**
   * @return the owner
   */
  public User getOwner() {
    return this.owner;
  }


  /**
   * @param owner the owner to set
   */
  public void setOwner(final User owner) {
    this.owner = owner;
  }


  /**
   * @return the selUcFav
   */
  public List<UsecaseFavorite> getSelUcFav() {
    return this.selUcFav;
  }


  /**
   * @param selUcFav the selUcFav to set
   */
  public void setSelUcFav(final List<UsecaseFavorite> selUcFav) {
    this.selUcFav = selUcFav == null ? null : new ArrayList<>(selUcFav);
  }


  /**
   * @return the copiedPidcVer
   */
  public PidcVersion getCopiedPidcVer() {
    return this.copiedPidcVer;
  }


  /**
   * @param copiedPidcVer the copiedPidcVer to set
   */
  public void setCopiedPidcVer(final PidcVersion copiedPidcVer) {
    this.copiedPidcVer = copiedPidcVer;
  }


  /**
   * @return the createdNodeAccess
   */
  public NodeAccess getCreatedNodeAccess() {
    return this.createdNodeAccess;
  }


  /**
   * @param createdNodeAccess the createdNodeAccess to set
   */
  public void setCreatedNodeAccess(final NodeAccess createdNodeAccess) {
    this.createdNodeAccess = createdNodeAccess;
  }
}
