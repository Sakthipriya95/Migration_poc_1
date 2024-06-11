package com.bosch.caltool.icdm.model.cdr;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.CloneNotSupportedRuntimeException;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * Review Files Model class
 *
 * @author bru2cob
 */
public class RvwFile implements Cloneable, Comparable<RvwFile>, IDataObject {

  /**
   *
   */
  private static final long serialVersionUID = 3268498373009052340L;


  /**
   * Node types for File
   *
   * @author bne4cob
   */
  public enum NodeType {
                        /**
                         * Node type for Review data
                         */
                        REVIEW_DATA("REVIEW_DATA"),
                        /**
                         * Node type for Review results
                         */
                        REVIEW_RESULT("REVIEW_RESULT"),
                        /**
                         * Node type for Emr file
                         */
                        EMR_FILE("EMR_FILE");

    /**
     * Node type value in the DB
     */
    private final String dbNodeType;

    /**
     * Enum Constructor
     *
     * @param dbNodeType Node type value in the DB
     */
    NodeType(final String dbNodeType) {
      this.dbNodeType = dbNodeType;
    }

    /**
     * @return the dbNodeType
     */
    public String getDbNodeType() {
      return this.dbNodeType;
    }

    /**
     * Gets the Node type equivalent to the db node type literal.
     *
     * @param type db node type
     * @return node type enumeration value
     */
    public static NodeType getNodeType(final String type) {
      for (NodeType nType : NodeType.values()) {
        if (nType.dbNodeType.equals(type)) {
          return nType;
        }
      }
      return null;
    }

  }

  /**
   * Rvw File Id
   */
  private Long id;
  /**
   * Result Id
   */
  private Long resultId;
  /**
   * File Id
   */
  private Long fileId;
  /**
   * Rvw Param Id
   */
  private Long rvwParamId;
  /**
   * File Type
   */
  private String fileType;
  /**
   * Created User
   */
  private String createdUser;
  /**
   * Created Date
   */
  private String createdDate;
  /**
   * Modified User
   */
  private String modifiedUser;
  /**
   * Modified Date
   */
  private String modifiedDate;
  /**
   * Version
   */
  private Long version;

  /**
   * File name
   */
  private String fileName;
  /**
   * Node type
   */
  private String nodeType;
  /**
   * file path
   */
  private String filePath;


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
   * @return resultId
   */
  public Long getResultId() {
    return this.resultId;
  }

  /**
   * @param resultId set resultId
   */
  public void setResultId(final Long resultId) {
    this.resultId = resultId;
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
   * @return rvwParamId
   */
  public Long getRvwParamId() {
    return this.rvwParamId;
  }

  /**
   * @param rvwParamId set rvwParamId
   */
  public void setRvwParamId(final Long rvwParamId) {
    this.rvwParamId = rvwParamId;
  }

  /**
   * @return fileType
   */
  public String getFileType() {
    return this.fileType;
  }

  /**
   * @param fileType set fileType
   */
  public void setFileType(final String fileType) {
    this.fileType = fileType;
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
  public RvwFile clone() {

    RvwFile file = null;
    try {
      file = (RvwFile) super.clone();
    }
    catch (CloneNotSupportedException excep) {
      throw new CloneNotSupportedRuntimeException(excep);
    }
    return file;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final RvwFile object) {
    int ret = ModelUtil.compare(this.fileName, object.fileName);
    return ret == 0 ? ModelUtil.compare(getId(), object.getId()) : ret;
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

    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((RvwFile) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }


  /**
   * @return the fileName
   */
  @Override
  public String getName() {
    return this.fileName;
  }


  /**
   * @param fileName the fileName to set
   */
  @Override
  public void setName(final String fileName) {
    this.fileName = fileName;
  }


  /**
   * @return the nodeType
   */
  public String getNodeType() {
    return this.nodeType;
  }


  /**
   * @param nodeType the nodeType to set
   */
  public void setNodeType(final String nodeType) {
    this.nodeType = nodeType;
  }


  /**
   * @return the filePath
   */
  public String getFilePath() {
    return this.filePath;
  }


  /**
   * @param filePath the filePath to set
   */
  public void setFilePath(final String filePath) {
    this.filePath = filePath;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    // Not Applicable
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDescription(final String description) {
    // Not Applicable

  }


}
