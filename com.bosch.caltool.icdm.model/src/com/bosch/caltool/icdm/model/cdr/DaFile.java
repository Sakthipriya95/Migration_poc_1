package com.bosch.caltool.icdm.model.cdr;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * DaFile Model class
 *
 * @author say8cob
 */
public class DaFile implements Cloneable, Comparable<DaFile>, IModel {

  /**
   * Serial UID
   */
  private final static long serialVersionUID = 117987227927196L;
  /**
   * Da File Id
   */
  private Long id;
  /**
   * Data Assessment Id
   */
  private Long dataAssessmentId;
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
   * @return dataAssessmentId
   */
  public Long getDataAssessmentId() {
    return this.dataAssessmentId;
  }

  /**
   * @param dataAssessmentId set dataAssessmentId
   */
  public void setDataAssessmentId(final Long dataAssessmentId) {
    this.dataAssessmentId = dataAssessmentId;
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
   * @return createdData
   */
  public String getCreatedDate() {
    return this.createdDate;
  }

  /**
   * @param createdData set createdData
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
  public DaFile clone() {
    try {
      return (DaFile) super.clone();
    }
    catch (CloneNotSupportedException excep) {
      // TODO
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final DaFile object) {
    return ModelUtil.compare(getId(), object.getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return ModelUtil.isEqual(getId(), ((DaFile) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }

}
