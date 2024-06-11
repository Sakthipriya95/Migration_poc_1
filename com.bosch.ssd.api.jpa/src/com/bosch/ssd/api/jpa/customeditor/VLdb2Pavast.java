package com.bosch.ssd.api.jpa.customeditor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;


/**
 * The persistent class for the V_LDB2_PAVAST database table.
 */
@Entity
@Table(name = "V_LDB2_PAVAST")
@NamedQueries({
    @NamedQuery(name = "VLdb2Pavast.labelExistsData", query = "select r from VLdb2Pavast r where r.upperLabel IN (select UPPER(t.labelName) from TempLabelsForCategory t)"),
    @NamedQuery(name = "VLdb2Pavast.deleteTempValues", query = "delete from TempLabelsForCategory"), })
@Converter(name = "dateToString", converterClass = DateConverter.class)
public class VLdb2Pavast implements Serializable {

  private static final long serialVersionUID = 1L;

  @Column(name = "ACTIVE")
  private String active;

  @Column(name = "CATEGORY")
  private String category;

  @Convert("dateToString")
  // @Temporal(TemporalType.DATE)
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
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_LDB2_ALL")
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

  public VLdb2Pavast() {}

  public String getActive() {
    return this.active;
  }

  public void setActive(final String active) {
    this.active = active;
  }

  public String getCategory() {
    return this.category;
  }

  public void setCategory(final String category) {
    this.category = category;
  }

  public String getCreDate() {
    return this.creDate;
  }

  public void setCreDate(final String creDate) {
    this.creDate = creDate;
  }

  public String getDgsLabel() {
    return this.dgsLabel;
  }

  public void setDgsLabel(final String selectedCheckBox_data) {
    this.dgsLabel = selectedCheckBox_data;
  }

  public String getEdc16Label() {
    return this.edc16Label;
  }

  public void setEdc16Label(final String edc16Label) {
    this.edc16Label = edc16Label;
  }

  public String getFileName() {
    return this.fileName;
  }

  public void setFileName(final String fileName) {
    this.fileName = fileName;
  }

  public String getFunction() {
    return this.function;
  }

  public void setFunction(final String function) {
    this.function = function;
  }

  public BigDecimal getLabId() {
    return this.labId;
  }

  public void setLabId(final BigDecimal labId) {
    this.labId = labId;
  }

  public String getLabel() {
    return this.label;
  }

  public void setLabel(final String label) {
    this.label = label;
  }

  public String getLongnameE() {
    return this.longnameE;
  }

  public void setLongnameE(final String longnameE) {
    this.longnameE = longnameE;
  }

  public String getLongnameF() {
    return this.longnameF;
  }

  public void setLongnameF(final String longnameF) {
    this.longnameF = longnameF;
  }

  public String getLongnameG() {
    return this.longnameG;
  }

  public void setLongnameG(final String longnameG) {
    this.longnameG = longnameG;
  }

  public String getLongnameI() {
    return this.longnameI;
  }

  public void setLongnameI(final String longnameI) {
    this.longnameI = longnameI;
  }

  public Date getModClassDate() {
    return this.modClassDate;
  }

  public void setModClassDate(final Date modClassDate) {
    this.modClassDate = modClassDate;
  }

  public String getNewEdc17Label() {
    return this.newEdc17Label;
  }

  public void setNewEdc17Label(final String newEdc17Label) {
    this.newEdc17Label = newEdc17Label;
  }

  public String getOldEdc17Label() {
    return this.oldEdc17Label;
  }

  public void setOldEdc17Label(final String oldEdc17Label) {
    this.oldEdc17Label = oldEdc17Label;
  }

  public String getParamterType() {
    return this.paramterType;
  }

  public void setParamterType(final String paramterType) {
    this.paramterType = paramterType;
  }

  public BigDecimal getRefId() {
    return this.refId;
  }

  public void setRefId(final BigDecimal refId) {
    this.refId = refId;
  }

  public String getRemarks() {
    return this.remarks;
  }

  public void setRemarks(final String remarks) {
    this.remarks = remarks;
  }

  public String getRevDescr() {
    return this.revDescr;
  }

  public void setRevDescr(final String revDescr) {
    this.revDescr = revDescr;
  }

  public String getSsdClass() {
    return this.ssdClass;
  }

  public void setSsdClass(final String ssdClass) {
    this.ssdClass = ssdClass;
  }

  public String getState() {
    return this.state;
  }

  public void setState(final String state) {
    this.state = state;
  }

  public String getSubtyp() {
    return this.subtyp;
  }

  public void setSubtyp(final String subtyp) {
    this.subtyp = subtyp;
  }

  public String getTyp() {
    return this.typ;
  }

  public void setTyp(final String typ) {
    this.typ = typ;
  }

  public String getUpperLabel() {
    return this.upperLabel;
  }

  public void setUpperLabel(final String upperLabel) {
    this.upperLabel = upperLabel;
  }

}