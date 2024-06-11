/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * Vcdm Pst Model class
 *
 * @author bru2cob
 */
public class VcdmPst implements Comparable<VcdmPst>, IModel {

  /**
   * Serial UID
   */
  private final static long serialVersionUID = 42384536363298L;
  /**
   * Pst Id
   */
  private Long pstId;
  /**
   * Pst Name
   */
  private String pstName;
  /**
   * Pst Variant
   */
  private String pstVariant;
  /**
   * Pst Revision
   */
  private Long pstRevision;
  /**
   * Pver Name
   */
  private String pverName;
  /**
   * Pver Variante
   */
  private String pverVariante;
  /**
   * Pver Revision
   */
  private Long pverRevision;
  /**
   * A2l Info Id
   */
  private Long a2lInfoId;
  /**
   * Cre Date
   */
  private String creDate;
  /**
   * Cre User
   */
  private String creUser;
  /**
   * Mod Date
   */
  private String modDate;
  /**
   * Mod User
   */
  private String modUser;
  /**
   * Vcdm Cre Date
   */
  private String vcdmCreDate;
  /**
   * Pst For Icdm
   */
  private String pstForIcdm;


  /**
   * @return pstName
   */
  public String getPstName() {
    return this.pstName;
  }

  /**
   * @param pstName set pstName
   */
  public void setPstName(final String pstName) {
    this.pstName = pstName;
  }

  /**
   * @return pstVariant
   */
  public String getPstVariant() {
    return this.pstVariant;
  }

  /**
   * @param pstVariant set pstVariant
   */
  public void setPstVariant(final String pstVariant) {
    this.pstVariant = pstVariant;
  }

  /**
   * @return pstRevision
   */
  public Long getPstRevision() {
    return this.pstRevision;
  }

  /**
   * @param pstRevision set pstRevision
   */
  public void setPstRevision(final Long pstRevision) {
    this.pstRevision = pstRevision;
  }

  /**
   * @return pverName
   */
  public String getPverName() {
    return this.pverName;
  }

  /**
   * @param pverName set pverName
   */
  public void setPverName(final String pverName) {
    this.pverName = pverName;
  }

  /**
   * @return pverVariante
   */
  public String getPverVariante() {
    return this.pverVariante;
  }

  /**
   * @param pverVariante set pverVariante
   */
  public void setPverVariante(final String pverVariante) {
    this.pverVariante = pverVariante;
  }

  /**
   * @return pverRevision
   */
  public Long getPverRevision() {
    return this.pverRevision;
  }

  /**
   * @param pverRevision set pverRevision
   */
  public void setPverRevision(final Long pverRevision) {
    this.pverRevision = pverRevision;
  }

  /**
   * @return a2lInfoId
   */
  public Long getA2lInfoId() {
    return this.a2lInfoId;
  }

  /**
   * @param a2lInfoId set a2lInfoId
   */
  public void setA2lInfoId(final Long a2lInfoId) {
    this.a2lInfoId = a2lInfoId;
  }

  /**
   * @return creDate
   */
  public String getCreDate() {
    return this.creDate;
  }

  /**
   * @param creDate set creDate
   */
  public void setCreDate(final String creDate) {
    this.creDate = creDate;
  }

  /**
   * @return creUser
   */
  public String getCreUser() {
    return this.creUser;
  }

  /**
   * @param creUser set creUser
   */
  public void setCreUser(final String creUser) {
    this.creUser = creUser;
  }

  /**
   * @return modDate
   */
  public String getModDate() {
    return this.modDate;
  }

  /**
   * @param modDate set modDate
   */
  public void setModDate(final String modDate) {
    this.modDate = modDate;
  }

  /**
   * @return modUser
   */
  public String getModUser() {
    return this.modUser;
  }

  /**
   * @param modUser set modUser
   */
  public void setModUser(final String modUser) {
    this.modUser = modUser;
  }

  /**
   * @return vcdmCreDate
   */
  public String getVcdmCreDate() {
    return this.vcdmCreDate;
  }

  /**
   * @param vcdmCreDate set vcdmCreDate
   */
  public void setVcdmCreDate(final String vcdmCreDate) {
    this.vcdmCreDate = vcdmCreDate;
  }

  /**
   * @return pstForIcdm
   */
  public String getPstForIcdm() {
    return this.pstForIcdm;
  }

  /**
   * @param pstForIcdm set pstForIcdm
   */
  public void setPstForIcdm(final String pstForIcdm) {
    this.pstForIcdm = pstForIcdm;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final VcdmPst object) {
    return ModelUtil.compare(getId(), object.getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {

    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((VcdmPst) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return this.pstId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long objId) {
    this.pstId = objId;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setVersion(final Long version) {
    // TODO Auto-generated method stub

  }

}
