package com.bosch.boot.ssd.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the V_LDB2_PAVAST database table.
 */
@Entity
@Table(name = "V_LDB2_PAVAST")
public class VLdb2Pavast implements Serializable {

  private static final long serialVersionUID = 1L;
  
  @Id
  @Column(name = "LAB_ID")
  private BigDecimal labId;
  
  @Column(name = "LABEL")
  private String label;
  
  @Column(name = "TYP")
  private String typ;
  
  @Column(name = "SUBTYP")
  private String subtyp;
  
  @Column(name = "CATEGORY")
  private String category;
  
  @Column(name = "PARAMTER_TYPE")
  private String paramterType;
  
  @Column(name = "FUNCTION")
  private String function;
  
  @Column(name = "SSD_CLASS")
  private String ssdClass;
  
  @Column(name = "EDC16_LABEL")
  private String edc16Label;
  
  @Column(name = "FILE_NAME")
  private String fileName;
  
  @Column(name = "REMARKS")
  private String remarks;

  @Column(name = "LONGNAME_E")
  private String longnameE;

  @Column(name = "LONGNAME_F")
  private String longnameF;

  @Column(name = "LONGNAME_G")
  private String longnameG;

  @Column(name = "LONGNAME_I")
  private String longnameI;

  @Column(name = "ACTIVE")
  private String active;
  
  @Column(name = "NEW_EDC17_LABEL")
  private String newEdc17Label;

  @Column(name = "OLD_EDC17_LABEL")
  private String oldEdc17Label;

  //@Temporal(TemporalType.DATE)
  @Column(name = "CRE_DATE")
  private String creDate;
  
  @Column(name = "UPPER_LABEL")
  private String upperLabel;

  @Temporal(TemporalType.DATE)
  @Column(name = "MOD_CLASS_DATE")
  private Date modClassDate;

  @Column(name = "REV_DESCR")
  private String revDescr;
  
  @Column(name = "STATE")
  private String state;
  
  @Column(name = "REF_ID")
  private BigDecimal refId;
  
  @Column(name = "DGS_LABEL")
  private String dgsLabel;
  
  /**
   * @return the labId
   */
  public BigDecimal getLabId() {
    return labId;
  }

  
  /**
   * @param labId the labId to set
   */
  public void setLabId(BigDecimal labId) {
    this.labId = labId;
  }

  
  /**
   * @return the label
   */
  public String getLabel() {
    return label;
  }

  
  /**
   * @param label the label to set
   */
  public void setLabel(String label) {
    this.label = label;
  }

  
  /**
   * @return the typ
   */
  public String getTyp() {
    return typ;
  }

  
  /**
   * @param typ the typ to set
   */
  public void setTyp(String typ) {
    this.typ = typ;
  }

  
  /**
   * @return the subtyp
   */
  public String getSubtyp() {
    return subtyp;
  }

  
  /**
   * @param subtyp the subtyp to set
   */
  public void setSubtyp(String subtyp) {
    this.subtyp = subtyp;
  }

  
  /**
   * @return the category
   */
  public String getCategory() {
    return category;
  }

  
  /**
   * @param category the category to set
   */
  public void setCategory(String category) {
    this.category = category;
  }

  
  /**
   * @return the paramterType
   */
  public String getParamterType() {
    return paramterType;
  }

  
  /**
   * @param paramterType the paramterType to set
   */
  public void setParamterType(String paramterType) {
    this.paramterType = paramterType;
  }

  
  /**
   * @return the function
   */
  public String getFunction() {
    return function;
  }

  
  /**
   * @param function the function to set
   */
  public void setFunction(String function) {
    this.function = function;
  }

  
  /**
   * @return the ssdClass
   */
  public String getSsdClass() {
    return ssdClass;
  }

  
  /**
   * @param ssdClass the ssdClass to set
   */
  public void setSsdClass(String ssdClass) {
    this.ssdClass = ssdClass;
  }

  
  /**
   * @return the edc16Label
   */
  public String getEdc16Label() {
    return edc16Label;
  }

  
  /**
   * @param edc16Label the edc16Label to set
   */
  public void setEdc16Label(String edc16Label) {
    this.edc16Label = edc16Label;
  }

  
  /**
   * @return the fileName
   */
  public String getFileName() {
    return fileName;
  }

  
  /**
   * @param fileName the fileName to set
   */
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  
  /**
   * @return the remarks
   */
  public String getRemarks() {
    return remarks;
  }

  
  /**
   * @param remarks the remarks to set
   */
  public void setRemarks(String remarks) {
    this.remarks = remarks;
  }

  
  /**
   * @return the longnameE
   */
  public String getLongnameE() {
    return longnameE;
  }

  
  /**
   * @param longnameE the longnameE to set
   */
  public void setLongnameE(String longnameE) {
    this.longnameE = longnameE;
  }

  
  /**
   * @return the longnameF
   */
  public String getLongnameF() {
    return longnameF;
  }

  
  /**
   * @param longnameF the longnameF to set
   */
  public void setLongnameF(String longnameF) {
    this.longnameF = longnameF;
  }

  
  /**
   * @return the longnameG
   */
  public String getLongnameG() {
    return longnameG;
  }

  
  /**
   * @param longnameG the longnameG to set
   */
  public void setLongnameG(String longnameG) {
    this.longnameG = longnameG;
  }

  
  /**
   * @return the longnameI
   */
  public String getLongnameI() {
    return longnameI;
  }

  
  /**
   * @param longnameI the longnameI to set
   */
  public void setLongnameI(String longnameI) {
    this.longnameI = longnameI;
  }

  
  /**
   * @return the active
   */
  public String getActive() {
    return active;
  }

  
  /**
   * @param active the active to set
   */
  public void setActive(String active) {
    this.active = active;
  }

  
  /**
   * @return the newEdc17Label
   */
  public String getNewEdc17Label() {
    return newEdc17Label;
  }

  
  /**
   * @param newEdc17Label the newEdc17Label to set
   */
  public void setNewEdc17Label(String newEdc17Label) {
    this.newEdc17Label = newEdc17Label;
  }

  
  /**
   * @return the oldEdc17Label
   */
  public String getOldEdc17Label() {
    return oldEdc17Label;
  }

  
  /**
   * @param oldEdc17Label the oldEdc17Label to set
   */
  public void setOldEdc17Label(String oldEdc17Label) {
    this.oldEdc17Label = oldEdc17Label;
  }

  
  /**
   * @return the creDate
   */
  public String getCreDate() {
    return creDate;
  }

  
  /**
   * @param creDate the creDate to set
   */
  public void setCreDate(String creDate) {
    this.creDate = creDate;
  }

  
  /**
   * @return the upperLabel
   */
  public String getUpperLabel() {
    return upperLabel;
  }

  
  /**
   * @param upperLabel the upperLabel to set
   */
  public void setUpperLabel(String upperLabel) {
    this.upperLabel = upperLabel;
  }

  
  /**
   * @return the modClassDate
   */
  public Date getModClassDate() {
    return modClassDate;
  }

  
  /**
   * @param modClassDate the modClassDate to set
   */
  public void setModClassDate(Date modClassDate) {
    this.modClassDate = modClassDate;
  }

  
  /**
   * @return the revDescr
   */
  public String getRevDescr() {
    return revDescr;
  }

  
  /**
   * @param revDescr the revDescr to set
   */
  public void setRevDescr(String revDescr) {
    this.revDescr = revDescr;
  }

  
  /**
   * @return the state
   */
  public String getState() {
    return state;
  }

  
  /**
   * @param state the state to set
   */
  public void setState(String state) {
    this.state = state;
  }

  
  /**
   * @return the refId
   */
  public BigDecimal getRefId() {
    return refId;
  }

  
  /**
   * @param refId the refId to set
   */
  public void setRefId(BigDecimal refId) {
    this.refId = refId;
  }

  
  /**
   * @return the dgsLabel
   */
  public String getDgsLabel() {
    return dgsLabel;
  }

  
  /**
   * @param dgsLabel the dgsLabel to set
   */
  public void setDgsLabel(String dgsLabel) {
    this.dgsLabel = dgsLabel;
  }

}