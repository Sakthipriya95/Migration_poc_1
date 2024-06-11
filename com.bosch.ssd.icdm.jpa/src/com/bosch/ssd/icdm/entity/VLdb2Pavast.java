package com.bosch.ssd.icdm.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

/**
 * The persistent class for the V_LDB2_PAVAST database table.
 */
@Entity

@NamedQueries({
    @NamedQuery(name = "VLdb2Pavast.checkLabel", query = "select e from VLdb2Pavast e where e.upperLabel= :label "),
    @NamedQuery(name = "VLdb2Pavast.getLabId", query = "select e.labId from VLdb2Pavast e where e.upperLabel= :label ", hints = {
        @QueryHint(name = QueryHints.REFRESH, value = HintValues.TRUE)

    }) })
@Table(name = "V_LDB2_PAVAST")
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class VLdb2Pavast implements Serializable {

  private static final long serialVersionUID = 1L;

  @Column(name = "ACTIVE")
  private String active;

  @Column(name = "CATEGORY")
  private String category;


  @Column(name = "CRE_DATE")
  private String creDate;

  @Column(name = "DGS_LABEL")
  private String dgsLabel;

  @Column(name = "EDC16_LABEL")
  private String edc16Label;

  @Column(name = "FILE_NAME")
  private String fileName;

  @Column(name = "FUNCTION")
  private String function;

  @Id
  @Column(name = "LAB_ID")
  private BigDecimal labId;

  @Column(name = "LABEL")
  private String label;

  @Column(name = "LONGNAME_E")
  private String longnameE;

  @Column(name = "LONGNAME_F")
  private String longnameF;

  @Column(name = "LONGNAME_G")
  private String longnameG;

  @Column(name = "LONGNAME_I")
  private String longnameI;

  @Temporal(TemporalType.DATE)
  @Column(name = "MOD_CLASS_DATE")
  private Date modClassDate;

  @Column(name = "NEW_EDC17_LABEL")
  private String newEdc17Label;

  @Column(name = "OLD_EDC17_LABEL")
  private String oldEdc17Label;

  @Column(name = "PARAMTER_TYPE")
  private String paramterType;

  @Column(name = "REF_ID")
  private BigDecimal refId;

  @Column(name = "REMARKS")
  private String remarks;

  @Column(name = "REV_DESCR")
  private String revDescr;

  @Column(name = "SSD_CLASS")
  private String ssdClass;

  private String state;

  @Column(name = "SUBTYP")
  private String subtyp;

  @Column(name = "TYP")
  private String typ;

  @Column(name = "UPPER_LABEL")
  private String upperLabel;

  /**
   *
   */
  public VLdb2Pavast() {
    // constructor
  }

  /**
   * @return active
   */
  public String getActive() {
    return this.active;
  }

  /**
   * @param active active
   */
  public void setActive(final String active) {
    this.active = active;
  }

  /**
   * @return category
   */
  public String getCategory() {
    return this.category;
  }

  /**
   * @param category category
   */
  public void setCategory(final String category) {
    this.category = category;
  }

  /**
   * @return creDate
   */
  public String getCreDate() {
    return this.creDate;
  }

  /**
   * @param creDate creDate
   */
  public void setCreDate(final String creDate) {
    this.creDate = creDate;
  }

  /**
   * @return dgsLabel
   */
  public String getDgsLabel() {
    return this.dgsLabel;
  }

  /**
   * @param selectedCheckBox_data dgsLabel
   */
  public void setDgsLabel(final String selectedCheckBox_data) {
    this.dgsLabel = selectedCheckBox_data;
  }

  /**
   * @return edc16Label
   */
  public String getEdc16Label() {
    return this.edc16Label;
  }

  /**
   * @param edc16Label edc16Label
   */
  public void setEdc16Label(final String edc16Label) {
    this.edc16Label = edc16Label;
  }

  /**
   * @return fileName
   */
  public String getFileName() {
    return this.fileName;
  }

  /**
   * @param fileName fileName
   */
  public void setFileName(final String fileName) {
    this.fileName = fileName;
  }

  /**
   * @return function
   */
  public String getFunction() {
    return this.function;
  }

  /**
   * @param function function
   */
  public void setFunction(final String function) {
    this.function = function;
  }

  /**
   * @return labId
   */
  public BigDecimal getLabId() {
    return this.labId;
  }

  /**
   * @param labId labId
   */
  public void setLabId(final BigDecimal labId) {
    this.labId = labId;
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
   * @return longnameE
   */
  public String getLongnameE() {
    return this.longnameE;
  }

  /**
   * @param longnameE longnameE
   */
  public void setLongnameE(final String longnameE) {
    this.longnameE = longnameE;
  }

  /**
   * @return longnameF
   */
  public String getLongnameF() {
    return this.longnameF;
  }

  /**
   * @param longnameF longnameF
   */
  public void setLongnameF(final String longnameF) {
    this.longnameF = longnameF;
  }

  /**
   * @return longnameG
   */
  public String getLongnameG() {
    return this.longnameG;
  }

  /**
   * @param longnameG longnameG
   */
  public void setLongnameG(final String longnameG) {
    this.longnameG = longnameG;
  }

  /**
   * @return longnameI
   */
  public String getLongnameI() {
    return this.longnameI;
  }

  /**
   * @param longnameI longnameI
   */
  public void setLongnameI(final String longnameI) {
    this.longnameI = longnameI;
  }

  /**
   * @return modClassDate
   */
  public Date getModClassDate() {
    return (Date) this.modClassDate.clone();
  }

  /**
   * @param modClassDate modClassDate
   */
  public void setModClassDate(final Date modClassDate) {
    this.modClassDate = (Date) modClassDate.clone();
  }

  /**
   * @return newEdc17Label
   */
  public String getNewEdc17Label() {
    return this.newEdc17Label;
  }

  /**
   * @param newEdc17Label newEdc17Label
   */
  public void setNewEdc17Label(final String newEdc17Label) {
    this.newEdc17Label = newEdc17Label;
  }

  /**
   * @return oldEdc17Label
   */
  public String getOldEdc17Label() {
    return this.oldEdc17Label;
  }

  /**
   * @param oldEdc17Label oldEdc17Label
   */
  public void setOldEdc17Label(final String oldEdc17Label) {
    this.oldEdc17Label = oldEdc17Label;
  }

  /**
   * @return paramterType
   */
  public String getParamterType() {
    return this.paramterType;
  }

  /**
   * @param paramterType paramterType
   */
  public void setParamterType(final String paramterType) {
    this.paramterType = paramterType;
  }

  /**
   * @return refId
   */
  public BigDecimal getRefId() {
    return this.refId;
  }

  /**
   * @param refId refId
   */
  public void setRefId(final BigDecimal refId) {
    this.refId = refId;
  }

  /**
   * @return remarks
   */
  public String getRemarks() {
    return this.remarks;
  }

  /**
   * @param remarks remarks
   */
  public void setRemarks(final String remarks) {
    this.remarks = remarks;
  }

  /**
   * @return revDescr
   */
  public String getRevDescr() {
    return this.revDescr;
  }

  /**
   * @param revDescr revDescr
   */
  public void setRevDescr(final String revDescr) {
    this.revDescr = revDescr;
  }

  /**
   * @return ssdClass
   */
  public String getSsdClass() {
    return this.ssdClass;
  }

  /**
   * @param ssdClass ssdClass
   */
  public void setSsdClass(final String ssdClass) {
    this.ssdClass = ssdClass;
  }

  /**
   * @return state
   */
  public String getState() {
    return this.state;
  }

  /**
   * @param state state
   */
  public void setState(final String state) {
    this.state = state;
  }

  /**
   * @return subtyp
   */
  public String getSubtyp() {
    return this.subtyp;
  }

  /**
   * @param subtyp subtyp
   */
  public void setSubtyp(final String subtyp) {
    this.subtyp = subtyp;
  }

  /**
   * @return typ
   */
  public String getTyp() {
    return this.typ;
  }

  /**
   * @param typ typ
   */
  public void setTyp(final String typ) {
    this.typ = typ;
  }

  /**
   * @return upperLabel
   */
  public String getUpperLabel() {
    return this.upperLabel;
  }

  /**
   * @param upperLabel upperLabel
   */
  public void setUpperLabel(final String upperLabel) {
    this.upperLabel = upperLabel;
  }

}