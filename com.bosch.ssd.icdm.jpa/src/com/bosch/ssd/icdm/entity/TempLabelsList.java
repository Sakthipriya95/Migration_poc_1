package com.bosch.ssd.icdm.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;
import org.eclipse.persistence.annotations.Direction;
import org.eclipse.persistence.annotations.NamedStoredProcedureQueries;
import org.eclipse.persistence.annotations.NamedStoredProcedureQuery;
import org.eclipse.persistence.annotations.StoredProcedureParameter;

/**
 * The persistent class for the TEMP_TEST_LABEL_FUNCTION database table.
 */
@Entity
@Table(name = "temp_ldb2_labels_list")
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
/**
 * Get Error Report for label
 *
 * @author SSN9COB
 */
@NamedNativeQuery(name = "TempLabelsList.getErrorReport", query = "select a.label,a.error_nr , b.error_text,a.LAB_REF_CLASS " +
    "from temp_ldb2_labels_list a, T_ldb2_BULK_ERROR b where a.ERROR_NR =b.ERROR_NR and a.error_nr != 0  and a.label=? " +
    " order by a.label,a.error_nr ", hints = { @QueryHint(name = "eclipselink.jdbc.fetch-size", value = "1000") })
/**
 * List of Stored procedure queries for insert, delete, validate
 *
 * @author SSN9COB
 */
@NamedStoredProcedureQueries(value = {
    @NamedStoredProcedureQuery(name = "TempLabelsList.insertInTemp", procedureName = "ICDM_LABEL_INSERT_TEMP"),
    @NamedStoredProcedureQuery(name = "TempLabelsList.deleteTempTables", procedureName = "DELETE_TEMP_TABLES"),
    @NamedStoredProcedureQuery(name = "TempLabelsList.validateLabelsNew", procedureName = "VALIDATE_LABELS_ICDM"),
    @NamedStoredProcedureQuery(name = "TempLabelsList.insertLabels", procedureName = "checkInsertP", parameters = {
        @StoredProcedureParameter(queryParameter = "p_tempno", name = "p_tempno", type = BigDecimal.class, direction = Direction.IN), }), })
public class TempLabelsList implements Serializable {

  private static final long serialVersionUID = 1L;
  /**
   * Upper Label
   */
  @Id
  @Column(name = "UPPER_LABEL")
  private String upperLabel;

  /**
   * Active
   */
  private String active;

  /**
   * Category
   */
  private String category;

  /**
   * DGS_Label
   */
  @Column(name = "DGS_LABEL")
  private String dgsLabel;

  /**
   * ErrorNr
   */
  @Column(name = "ERROR_NR")
  private BigDecimal errorNr;

  /**
   * Fucntion
   */
  private String function;

  /**
   * SSD Class
   */
  @Column(name = "SSD_CLASS")
  private String ssdClass;

  /**
   * Label Ref class
   */
  @Column(name = "LAB_REF_CLASS")
  private String labRefClass;

  /**
   * Label
   */
  private String label;

  /**
   * Label Present
   */
  @Column(name = "LABEL_PRESENT")
  private String labelPresent;

  @Column(name = "LONGNAME_E")
  private String longnameE;

  @Column(name = "LONGNAME_F")
  private String longnameF;

  @Column(name = "LONGNAME_G")
  private String longnameG;

  @Column(name = "LONGNAME_I")
  private String longnameI;

  @Column(name = "PARAMTER_TYPE")
  private String paramterType;

  @Column(name = "REF_ID")
  private BigDecimal refId;

  private String remarks;

  @Column(name = "SUB_TYP")
  private String subTyp;

  private BigDecimal tempno;

  private String typ;

  /**
   *
   */
  public TempLabelsList() {
    // Empty Constructor
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
   * @return dgsLabel
   */
  public String getDgsLabel() {
    return this.dgsLabel;
  }

  /**
   * @param dgsLabel dgsLabel
   */
  public void setDgsLabel(final String dgsLabel) {
    this.dgsLabel = dgsLabel;
  }

  /**
   * @return errorNr
   */
  public BigDecimal getErrorNr() {
    return this.errorNr;
  }

  /**
   * @param errorNr errorNr
   */
  public void setErrorNr(final BigDecimal errorNr) {
    this.errorNr = errorNr;
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
   * @return labRefClass
   */
  public String getLabRefClass() {
    return this.labRefClass;
  }

  /**
   * @param labRefClass labRefClass
   */
  public void setLabRefClass(final String labRefClass) {
    this.labRefClass = labRefClass;
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
   * @return labelPresent
   */
  public String getLabelPresent() {
    return this.labelPresent;
  }

  /**
   * @param labelPresent labelPresent
   */
  public void setLabelPresent(final String labelPresent) {
    this.labelPresent = labelPresent;
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
   * @return subTyp
   */
  public String getSubTyp() {
    return this.subTyp;
  }

  /**
   * @param subTyp subTyp
   */
  public void setSubTyp(final String subTyp) {
    this.subTyp = subTyp;
  }

  /**
   * @return typ
   */
  public String getTyp() {
    return this.typ;
  }

  /**
   * @param typ setTyp
   */
  public void setTyp(final String typ) {
    this.typ = typ;
  }

  /**
   * @param ssdClass setSsdClass
   */
  public void setSsdClass(final String ssdClass) {
    this.ssdClass = ssdClass;
  }

  /**
   * @return getSsdClass
   */
  public String getSsdClass() {
    return this.ssdClass;
  }

  /**
   * @param tempno setTempno
   */
  public void setTempno(final BigDecimal tempno) {
    this.tempno = tempno;
  }

  /**
   * @return getTempno
   */
  public BigDecimal getTempno() {
    return this.tempno;
  }

}