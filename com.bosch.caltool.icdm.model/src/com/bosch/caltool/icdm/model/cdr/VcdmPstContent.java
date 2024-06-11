/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * Vcdm Pst Contents Model class
 *
 * @author bru2cob
 */
public class VcdmPstContent implements Comparable<VcdmPstContent>, IModel {

  /**
   * Serial UID
   */
  private final static long serialVersionUID = 42448447543295L;
  /**
   * Id
   */
  private Long id;
  /**
   * Pst Id
   */
  private Long pstId;
  /**
   * File Id
   */
  private Long fileId;
  /**
   * Vcdm Class
   */
  private String vcdmClass;
  /**
   * Vcdm Name
   */
  private String vcdmName;
  /**
   * Vcdm Variant
   */
  private String vcdmVariant;
  /**
   * Vcdm Revision
   */
  private Long vcdmRevision;
  /**
   * File Name
   */
  private String fileName;

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return this.id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long id) {
    this.id = id;
  }

  /**
   * @return pstId
   */
  public Long getPstId() {
    return this.pstId;
  }

  /**
   * @param pstId set pstId
   */
  public void setPstId(final Long pstId) {
    this.pstId = pstId;
  }

  /**
   * @return fileId
   */
  public Long getFileId() {
    return this.fileId;
  }

  /**
   * @param fileId set fileId
   */
  public void setFileId(final Long fileId) {
    this.fileId = fileId;
  }

  /**
   * @return vcdmClass
   */
  public String getVcdmClass() {
    return this.vcdmClass;
  }

  /**
   * @param vcdmClass set vcdmClass
   */
  public void setVcdmClass(final String vcdmClass) {
    this.vcdmClass = vcdmClass;
  }

  /**
   * @return vcdmName
   */
  public String getVcdmName() {
    return this.vcdmName;
  }

  /**
   * @param vcdmName set vcdmName
   */
  public void setVcdmName(final String vcdmName) {
    this.vcdmName = vcdmName;
  }

  /**
   * @return vcdmVariant
   */
  public String getVcdmVariant() {
    return this.vcdmVariant;
  }

  /**
   * @param vcdmVariant set vcdmVariant
   */
  public void setVcdmVariant(final String vcdmVariant) {
    this.vcdmVariant = vcdmVariant;
  }

  /**
   * @return vcdmRevision
   */
  public Long getVcdmRevision() {
    return this.vcdmRevision;
  }

  /**
   * @param vcdmRevision set vcdmRevision
   */
  public void setVcdmRevision(final Long vcdmRevision) {
    this.vcdmRevision = vcdmRevision;
  }

  /**
   * @return fileName
   */
  public String getFileName() {
    return this.fileName;
  }

  /**
   * @param fileName set fileName
   */
  public void setFileName(final String fileName) {
    this.fileName = fileName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final VcdmPstContent object) {
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
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((VcdmPstContent) obj).getId());
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
