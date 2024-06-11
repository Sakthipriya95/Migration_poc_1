package com.bosch.ssd.icdm.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;
import org.eclipse.persistence.annotations.Index;

import com.bosch.ssd.icdm.entity.keys.VLdb2ProjectLabelsPK;


/**
 * The persistent class for the V_LDB2_PROJECT_LABELS database table.
 *
 * @author SSN9COB
 */
@Entity
@IdClass(VLdb2ProjectLabelsPK.class)
@Table(name = "V_LDB2_PROJECT_LABELS")
@NamedQueries({ @NamedQuery(name = "VLdb2ProjectLabel.findAll", query = "SELECT v FROM VLdb2ProjectLabel v")

})
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class VLdb2ProjectLabel implements Serializable {

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
  @Column(name = "\"LABEL\"")
  private String label;

  /**
   * Pro Rev Id
   */
  @Id
  @Column(name = "PRO_REV_ID")
  private BigDecimal proRevId;

  /**
   * Unit
   */
  private String unit;

  /**
   * Upper Label
   */
  @Index
  @Column(name = "UPPER_LABEL")
  private String upperLabel;

  /**
   * Constructor
   */
  public VLdb2ProjectLabel() {
    // constructor
  }

  /**
   * @return ctrlFld
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
   * @return rev id
   */
  public BigDecimal getProRevId() {
    return this.proRevId;
  }

  /**
   * @param proRevId rev id
   */
  public void setProRevId(final BigDecimal proRevId) {
    this.proRevId = proRevId;
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
   * @return label
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