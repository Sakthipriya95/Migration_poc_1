package com.bosch.ssd.icdm.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;


/**
 * The persistent class for the TEMP_RELEASES_ERROR_LBLDTLS database table.
 */
@Entity
@Table(name = "TEMP_RELEASES_ERROR_LBLDTLS")
@NamedQuery(name = "TempReleasesErrorLblDtl.getTempErrorLblDtls", query = "select obj from TempReleasesErrorLbldtl obj where obj.uniId = :uniId order by lower(obj.gridLabelName)", hints = {
    @QueryHint(name = "eclipselink.jdbc.fetch-size", value = "1000"),
    @QueryHint(name = QueryHints.MAINTAIN_CACHE, value = HintValues.FALSE) })
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class TempReleasesErrorLbldtl implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Grid Err Number
   */
  @Column(name = "GRID_ERR_NUMBER")
  private BigDecimal gridErrNumber;

  /**
   * Grid Err Test
   */
  @Column(name = "GRID_ERR_TEST")
  private String gridErrTest;

  /**
   * Grid Label name
   */
  @Column(name = "GRID_LABEL_NAME")
  private String gridLabelName;

  /**
   * Grid Present Unit
   */
  @Column(name = "GRID_PRESENT_UNIT")
  private String gridPresentUnit;

  /**
   * Grid Scope
   */
  @Column(name = "GRID_SCOPE")
  private String gridScope;

  /**
   * GridUnit Labell List
   */
  @Column(name = "GRID_UNIT_LABELLIST")
  private String gridUnitLabellist;

  /**
   * Seq No
   */
  @Id
  @Column(name = "SEQ_NO")
  private BigDecimal seqNo;

  /**
   * Unique Idr
   */
  @Column(name = "UNI_ID")
  private String uniId;

  /**
   *
   */
  public TempReleasesErrorLbldtl() {
    // constructor
  }

  /**
   * @return gridErrNumber
   */
  public BigDecimal getGridErrNumber() {
    return this.gridErrNumber;
  }

  /**
   * @param gridErrNumber gridErrNumber
   */
  public void setGridErrNumber(final BigDecimal gridErrNumber) {
    this.gridErrNumber = gridErrNumber;
  }

  /**
   * @return getGridErrTest
   */
  public String getGridErrTest() {
    return this.gridErrTest;
  }

  /**
   * @param gridErrTest getGridErrTest
   */
  public void setGridErrTest(final String gridErrTest) {
    this.gridErrTest = gridErrTest;
  }

  /**
   * @return gridLabelName
   */
  public String getGridLabelName() {
    return this.gridLabelName;
  }

  /**
   * @param gridLabelName gridLabelName
   */
  public void setGridLabelName(final String gridLabelName) {
    this.gridLabelName = gridLabelName;
  }

  /**
   * @return gridPresentUnit
   */
  public String getGridPresentUnit() {
    return this.gridPresentUnit;
  }

  /**
   * @param gridPresentUnit gridPresentUnit
   */
  public void setGridPresentUnit(final String gridPresentUnit) {
    this.gridPresentUnit = gridPresentUnit;
  }

  /**
   * @return gridScope
   */
  public String getGridScope() {
    return this.gridScope;
  }

  /**
   * @param gridScope gridScope
   */
  public void setGridScope(final String gridScope) {
    this.gridScope = gridScope;
  }

  /**
   * @return gridUnitLabellist
   */
  public String getGridUnitLabellist() {
    return this.gridUnitLabellist;
  }

  /**
   * @param gridUnitLabellist gridUnitLabellist
   */
  public void setGridUnitLabellist(final String gridUnitLabellist) {
    this.gridUnitLabellist = gridUnitLabellist;
  }

  /**
   * @return sequence no
   */
  public BigDecimal getSeqNo() {
    return this.seqNo;
  }

  /**
   * @param seqNo seqNo
   */
  public void setSeqNo(final BigDecimal seqNo) {
    this.seqNo = seqNo;
  }

  /**
   * @return unique id
   */
  public String getUniId() {
    return this.uniId;
  }

  /**
   * @param uniId unique id
   */
  public void setUniId(final String uniId) {
    this.uniId = uniId;
  }

}