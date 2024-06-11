package com.bosch.ssd.icdm.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;
import org.eclipse.persistence.annotations.Direction;
import org.eclipse.persistence.annotations.NamedStoredProcedureQueries;
import org.eclipse.persistence.annotations.NamedStoredProcedureQuery;
import org.eclipse.persistence.annotations.StoredProcedureParameter;

/**
 * The persistent class for the K5ESK_LDB2.TEMP_LABELLIST_IF database table.
 */
@Entity
@Table(name = "K5ESK_LDB2.TEMP_LABELLIST_IF")
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
@NamedNativeQueries({
    @NamedNativeQuery(name = "TempLabellistInterface.deleteTempValues", query = "delete from K5ESK_LDB2.TEMP_LABELLIST_IF "),
    @NamedNativeQuery(name = "TempLabellistInterface.findAll", query = "select * from K5ESK_LDB2.TEMP_LABELLIST_IF "),
    @NamedNativeQuery(name = "TempLabellistInterface.deleteEntry", query = "delete from K5ESK_LDB2.TEMP_LABELLIST_IF where UPPER_LABEL = ? ") })
@NamedStoredProcedureQueries({
    /**
     * Named Stored Procedure Query for Get SSD File
     *
     * @author SSN9COB
     */
    @NamedStoredProcedureQuery(name = "TempLabellistInterface.getSSDFile", procedureName = "SSD_FileData_icdm", parameters = {

        @StoredProcedureParameter(queryParameter = "pc_node_id", name = "pc_node_id", type = BigDecimal.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "commentPb", name = "commentPb", type = String.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "userId", name = "userId", type = String.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "outputFlag", name = "outputFlag", type = String.class, direction = Direction.OUT) }),
    /**
     * Named Stored Procedure Query for Ger SSD File Dependency
     *
     * @author SSN9COB
     */
    @NamedStoredProcedureQuery(name = "TempLabellistInterface.getSSDFileDependency", procedureName = "SSD_FileData_icdm_dependency", parameters = {

        @StoredProcedureParameter(queryParameter = "pc_node_id", name = "pc_node_id", type = BigDecimal.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "commentPb", name = "commentPb", type = String.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "userId", name = "userId", type = String.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "outputFlag", name = "outputFlag", type = String.class, direction = Direction.OUT) }) })
//@NamedQuery(name = "TempLabellistInterface.findAll", query = "SELECT t FROM TempLabellistInterface t")
public class TempLabellistInterface implements Serializable {

  private static final long serialVersionUID = 1L;
  /**
   * Seq Number
   */
  @Id
  @Column(name = "SEQ_NO")
  private BigDecimal seqNo;

  /**
   * Node ID
   */
  @Column(name = "NODE_ID")
  private BigDecimal nodeId;

  /**
   * Label
   */
  @Column(name = "\"LABEL\"")
  private String label;

  /**
   * Upper Label
   */
  @Column(name = "\"UPPER_LABEL\"")
  private String upperLabel;

  /**
   * Default Rule
   */
  @Column(name = "\"DEFAULT_RULE\"")
  private String defaultRule = "N";

  /**
   *
   */
  public TempLabellistInterface() {
    // constructor
  }

  /**
   * @return the nodeId
   */
  public BigDecimal getNodeId() {
    return this.nodeId;
  }

  /**
   * @param nodeId the nodeId to set
   */
  public void setNodeId(final BigDecimal nodeId) {
    this.nodeId = nodeId;
  }

  /**
   * @return the upperLabel
   */
  public String getUpperLabel() {
    return this.upperLabel;
  }


  /**
   * @param upperLabel the upperLabel to set
   */
  public void setUpperLabel(final String upperLabel) {
    this.upperLabel = upperLabel;
  }


  /**
   * @return the defaultRule
   */
  public String getDefaultRule() {
    return this.defaultRule;
  }


  /**
   * @param defaultRule the defaultRule to set
   */
  public void setDefaultRule(final String defaultRule) {
    this.defaultRule = defaultRule;
  }

  /**
   * @return sequence no
   */
  public BigDecimal getSeqNo() {
    return this.seqNo;
  }

  /**
   * @param seqNo sequence number
   */
  public void setSeqNo(final BigDecimal seqNo) {
    this.seqNo = seqNo;
  }

  /**
   * @return label
   */
  public String getLabel() {
    return this.label;
  }

  /**
   * @param label label
   */
  public void setLabel(final String label) {
    this.label = label;
  }

}