package com.bosch.ssd.icdm.entity;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

import com.bosch.ssd.icdm.entity.keys.VLdb2ProjectLabelsListPK;


/**
 * The persistent class for the V_LDB2_PROJECT_LABELS_LIST database table.
 *
 * @author SSN9COB
 */
@IdClass(VLdb2ProjectLabelsListPK.class)
@Entity
@Table(name = "V_LDB2_PROJECT_LABELS_LIST")
@NamedQueries({
    /**
     * Named Query - Find Labels
     *
     * @author SSN9COB
     */
    @NamedQuery(name = "VLdb2ProjectLabelsList.findLabels", query = "SELECT l  " + "FROM VLdb2ProjectLabelsList l " +
        "WHERE l.proRevId = :revId " +
        "ORDER BY l.label", hints = { @QueryHint(name = QueryHints.READ_ONLY, value = HintValues.TRUE) }),
    /**
     * Named Query - FInd count of labels
     *
     * @author SSN9COB
     */
    @NamedQuery(name = "VLdb2ProjectLabelsList.findCountOfLabels", query = "SELECT count(l)  " +
        "FROM VLdb2ProjectLabelsList l " +
        "WHERE l.proRevId = :revId ", hints = { @QueryHint(name = QueryHints.READ_ONLY, value = HintValues.TRUE) }) }

)
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class VLdb2ProjectLabelsList implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Ctrl FLD
   */
  @Column(name = "CTRL_FLD")
  private String ctrlFld;

  /**
   * Label
   */
  @Id
  private String label;

  /**
   * Not Defined
   */
  @Column(name = "NOT_DEFINED")
  private String notDefined;

  /**
   * Pro_rev ID
   */
  @Column(name = "PRO_REV_ID")
  @Id
  private BigInteger proRevId;

  /**
   * SSD Class
   */
  @Column(name = "SSD_CLASS")
  private String ssdClass;

  /**
   * Unit
   */
  private String unit;

  /**
   * Constructor
   */
  public VLdb2ProjectLabelsList() {
    // constructor
  }

  /**
   * @param label label
   * @param unit unit
   * @param ssdClass ssdClass
   * @param notDefined defined
   */
  public VLdb2ProjectLabelsList(final String label, final String unit, final String ssdClass, final String notDefined) {
    this.label = label;
    this.unit = unit;
    this.ssdClass = ssdClass;
    this.notDefined = notDefined;
  }

  /**
   * @return field
   */
  public String getCtrlFld() {
    return this.ctrlFld;
  }

  /**
   * @param ctrlFld field
   */
  public void setCtrlFld(final String ctrlFld) {
    this.ctrlFld = ctrlFld;
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
   * @return notDefined
   */
  public String getNotDefined() {
    return this.notDefined;
  }

  /**
   * @param notDefined notdefined
   */
  public void setNotDefined(final String notDefined) {
    this.notDefined = notDefined;
  }

  /**
   * @return rev id
   */
  public BigInteger getProRevId() {
    return this.proRevId;
  }

  /**
   * @param proRevId rev id
   */
  public void setProRevId(final BigInteger proRevId) {
    this.proRevId = proRevId;
  }

  /**
   * @return ssd class
   */
  public String getSsdClass() {
    return this.ssdClass;
  }

  /**
   * @param ssdClass class
   */
  public void setSsdClass(final String ssdClass) {
    this.ssdClass = ssdClass;
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


}