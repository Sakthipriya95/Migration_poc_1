package com.bosch.caltool.icdm.model.cdr;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * WpFile Model class
 *
 * @author msp5cob
 */
public class WpFiles implements Comparable<WpFiles>, IModel {


  /**
   * Generated Serial Version UID
   */
  private static final long serialVersionUID = 560173667096061077L;
  /**
   * Wp File Id
   */
  private Long id;
  /**
   * Wp Archival Id
   */
  private Long wpArchivalId;
  /**
   * File Name
   */
  private String fileName;
  /**
   * File Data
   */
  private byte[] fileData;
  /**
   * Created Data
   */
  private String createdDate;
  /**
   * Created User
   */
  private String createdUser;
  /**
   * Modified Date
   */
  private String modifiedDate;
  /**
   * Modified User
   */
  private String modifiedUser;
  /**
   * Version
   */
  private Long version;

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
   * @return wpArchivalId
   */
  public Long getWpArchivalId() {
    return this.wpArchivalId;
  }

  /**
   * @param wpArchivalId set wpArchivalId
   */
  public void setWpArchivalId(final Long wpArchivalId) {
    this.wpArchivalId = wpArchivalId;
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
   * @return fileData
   */
  public byte[] getFileData() {
    return this.fileData;
  }

  /**
   * @param fileData set fileData
   */
  public void setFileData(final byte[] fileData) {
    this.fileData = fileData;
  }

  /**
   * @return createdDate
   */
  public String getCreatedDate() {
    return this.createdDate;
  }

  /**
   * @param createdDate set createdDate
   */
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * @return createdUser
   */
  public String getCreatedUser() {
    return this.createdUser;
  }

  /**
   * @param createdUser set createdUser
   */
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  /**
   * @return modifiedDate
   */
  public String getModifiedDate() {
    return this.modifiedDate;
  }

  /**
   * @param modifiedDate set modifiedDate
   */
  public void setModifiedDate(final String modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  /**
   * @return modifiedUser
   */
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  /**
   * @param modifiedUser set modifiedUser
   */
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return this.version;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setVersion(final Long version) {
    this.version = version;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final WpFiles object) {
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
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((WpFiles) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }

}
