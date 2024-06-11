/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.io.IOException;
import java.util.Calendar;
import java.util.Map;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.model.MODEL_TYPE;


/**
 * @author bne4cob
 */
public class IcdmFile extends ApicObject implements Comparable<IcdmFile> {

  /**
   * Node types for File
   *
   * @author bne4cob
   * @deprecated Use {@link MODEL_TYPE} instead
   */
  @Deprecated
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
    private String dbNodeType;

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
   * Create a new instance of ICDM file object. The created object is also added to the data cache against the specific
   * node ID.
   *
   * @param apicDataProvider data provider
   * @param fileID file ID
   */
  protected IcdmFile(final ApicDataProvider apicDataProvider, final Long fileID) {
    super(apicDataProvider, fileID);

    getDataCache().getIcdmFilesOfNode(getEntityProvider().getDbIcdmFile(getID()).getNodeId()).put(fileID, this);
  }

  /**
   * @return node ID of this file
   */
  public final Long getNodeID() {
    return getEntityProvider().getDbIcdmFile(getID()).getNodeId();
  }

  /**
   * @return Node type of this file
   */
  public final NodeType getNodeType() {
    return NodeType.getNodeType(getEntityProvider().getDbIcdmFile(getID()).getNodeType());
  }

  /**
   * Returns the number of files represented by this object
   *
   * @return number of files
   */
  protected final long getFileCount() {
    return getEntityProvider().getDbIcdmFile(getID()).getFileCount();
  }

  /**
   * Returns whether this object represents a single file or a group of files
   *
   * @return true/false
   */
  public final boolean isSingleFile() {
    return getFileCount() == 1;
  }

  /**
   * Provides the name of the file if there is only one file in this object. If the object represents more than one
   * file, the method returns <code>null</code>. This can be checked by executing <code>isSingleFile()</code> method of
   * this object.
   *
   * @return file name
   */
  public final String getFileName() {
    if (isSingleFile()) {
      return getEntityProvider().getDbIcdmFile(getID()).getFileName();
    }
    return null;
  }

  /**
   * Gets the file contents as a <code>byte</code> array
   *
   * @return file contents
   */
  protected final byte[] getZippedFileData() {
    return getEntityProvider().getDbIcdmFile(getID()).getTabvIcdmFileData().getFileData();
  }

  /**
   * The method returns the unzipped files as a map, with key as the file name including the relative path, value as the
   * file as byte array.
   *
   * @return the unzipped files as a map
   * @throws IOException for any exceptions during unzipping
   */
  public final Map<String, byte[]> getFiles() throws IOException {
    final byte[] zippedFile = getEntityProvider().getDbIcdmFile(getID()).getTabvIcdmFileData().getFileData();
    return ZipUtils.unzip(zippedFile);
  }

  /**
   * @return created date of the attribute value
   */
  @Override
  public final Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbIcdmFile(getID()).getCreatedDate());
  }

  /**
   * @return created user of the attribute value
   */
  @Override
  public final String getCreatedUser() {
    return getEntityProvider().getDbIcdmFile(getID()).getCreatedUser();
  }

  /**
   * @return modified date of the attribute value
   */
  @Override
  public final Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbIcdmFile(getID()).getModifiedDate());
  }

  /**
   * @return modified user of the attribute value
   */
  @Override
  public final String getModifiedUser() {
    return getEntityProvider().getDbIcdmFile(getID()).getModifiedUser();
  }

  /**
   * Icdm-543 {@inheritDoc}
   */
  @Override
  public int compareTo(final IcdmFile arg0) {
    int compareResult = ApicUtil.compare(getFileName(), arg0.getFileName());
    if (compareResult == 0) {
      compareResult = ApicUtil.compareLong(getID(), arg0.getID());
    }
    return compareResult;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return super.equals(obj);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return getFileName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    // Not applicable
    return null;
  }
}
