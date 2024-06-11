package com.bosch.ssd.icdm.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;
import org.eclipse.persistence.annotations.Direction;
import org.eclipse.persistence.annotations.NamedStoredProcedureQuery;
import org.eclipse.persistence.annotations.StoredProcedureParameter;

import com.bosch.ssd.icdm.constants.JPAQueryConstants;


/**
 * The persistent class for the TEMP_LABELS database table.
 */
@Entity
/**
 * Create Updated Labellist
 *
 * @author SSN9COB
 */
@NamedStoredProcedureQuery(name = JPAQueryConstants.CREATE_LABEL_LIST, procedureName = "CREATE_UPDATED_LBLLIST", parameters = {
    @StoredProcedureParameter(queryParameter = "p_node_id", name = "p_node_id", type = BigDecimal.class, direction = Direction.IN),
    @StoredProcedureParameter(queryParameter = "P_USERNAME", name = "P_USERNAME", type = String.class, direction = Direction.IN),
    @StoredProcedureParameter(queryParameter = "P_RESULT", name = "P_RESULT", type = Integer.class, direction = Direction.OUT),
    @StoredProcedureParameter(queryParameter = "P_ERROR", name = "P_ERROR", type = String.class, direction = Direction.OUT) })

@Table(name = "TEMP_LABELS")
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
@NamedNativeQuery(name = "TempLabel.deleteAll", query = "delete from TEMP_LABELS")
@NamedQuery(name = "TempLabel.findAll", query = "SELECT t FROM TempLabel t")
public class TempLabel implements Serializable {

  private static final long serialVersionUID = 1L;
  /**
   * Label
   */
  @Column(name = "\"LABEL\"")
  private String label;

  /**
   * Node ID
   */
  @Column(name = "NODE_ID")
  private BigDecimal nodeId;

  /**
   * Sequence ID
   */
  @Id
  @Column(name = "SEQ_ID")
  private BigDecimal seqId;

  /**
   * Unit
   */
  private String unit;

  /**
   * Upper Label
   */
  @Column(name = "UPPER_LABEL")
  private String upperLabel;

  /**
   *
   */
  public TempLabel() {
    // constructor
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

  /**
   * @return node id
   */
  public BigDecimal getNodeId() {
    return this.nodeId;
  }

  /**
   * @param nodeId node id
   */
  public void setNodeId(final BigDecimal nodeId) {
    this.nodeId = nodeId;
  }

  /**
   * @return sequence id
   */
  public BigDecimal getSeqId() {
    return this.seqId;
  }

  /**
   * @param seqId sequence id
   */
  public void setSeqId(final BigDecimal seqId) {
    this.seqId = seqId;
  }

  /**
   * @return unit
   */
  public String getUnit() {
    return this.unit;
  }

  /**
   * @param unit unit
   */
  public void setUnit(final String unit) {
    this.unit = unit;
  }

  /**
   * @return upperLabel
   */
  public String getUpperLabel() {
    return this.upperLabel;
  }

  /**
   * @param upperLabel label
   */
  public void setUpperLabel(final String upperLabel) {
    this.upperLabel = upperLabel;
  }

}