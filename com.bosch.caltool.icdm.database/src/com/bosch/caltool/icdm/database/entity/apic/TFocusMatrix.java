package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.annotations.OptimisticLocking;


/**
 * The persistent class for the T_FOCUS_MATRIX database table.
 */
@Entity
@Table(name = "T_FOCUS_MATRIX")
@OptimisticLocking(cascade = true)
@NamedQueries(value = {
    @NamedQuery(name = TFocusMatrix.GET_ALL_FOCUS_MATRIX, query = "SELECT fm FROM TFocusMatrix fm") })
public class TFocusMatrix implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Named query to get all attribute super groups
   */
  public static final String GET_ALL_FOCUS_MATRIX = "TFocusMatrix.GET_ALL_FOCUS_MATIRX";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "FM_ID")
  private long fmId;

  @Column(name = "COLOR_CODE", length = 3)
  private String colorCode;

  @Column(length = 4000)
  private String comments;

  @Column(length = 1000)
  private String link;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 30)
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 30)
  private String modifiedUser;
  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private Long version;

  // bi-directional many-to-one association to TabvUcpAttr
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "UCPA_ID", nullable = true)
  @BatchFetch(value = BatchFetchType.JOIN)
  private TabvUcpAttr tabvUcpAttr;

  // bi-directional many-to-one association to TFocusMatrixVersion
  @ManyToOne
  @JoinColumn(name = "FM_VERS_ID", nullable = false)
  private TFocusMatrixVersion tFocusMatrixVersion;

  // bi-directional many-to-one association to TabvAttribute
  @ManyToOne
  @JoinColumn(name = "ATTR_ID", nullable = false)
  private TabvAttribute tabvAttribute;

  // bi-directional many-to-one association to TabvUseCase
  @ManyToOne
  @JoinColumn(name = "USE_CASE_ID", nullable = false)
  private TabvUseCase tabvUseCas;

  // bi-directional many-to-one association to TabvUseCaseSection
  @ManyToOne
  @JoinColumn(name = "SECTION_ID")
  private TabvUseCaseSection tabvUseCaseSection;

  @Column(name = "IS_DELETED", nullable = false, length = 1)
  private String deletedFlag;

  public TFocusMatrix() {}

  public long getFmId() {
    return this.fmId;
  }

  public void setFmId(final long fmId) {
    this.fmId = fmId;
  }

  public String getColorCode() {
    return this.colorCode;
  }

  public void setColorCode(final String colorCode) {
    this.colorCode = colorCode;
  }

  public String getComments() {
    return this.comments;
  }

  public void setComments(final String comments) {
    this.comments = comments;
  }

  public Timestamp getCreatedDate() {
    return this.createdDate;
  }

  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
  }

  public String getCreatedUser() {
    return this.createdUser;
  }

  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  public Timestamp getModifiedDate() {
    return this.modifiedDate;
  }

  public void setModifiedDate(final Timestamp modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  public String getModifiedUser() {
    return this.modifiedUser;
  }

  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public TabvUcpAttr getTabvUcpAttr() {
    return this.tabvUcpAttr;
  }

  public void setTabvUcpAttr(final TabvUcpAttr tabvUcpAttr) {
    this.tabvUcpAttr = tabvUcpAttr;
  }

  public TFocusMatrixVersion getTFocusMatrixVersion() {
    return this.tFocusMatrixVersion;
  }

  public void setTFocusMatrixVersion(final TFocusMatrixVersion tFocusMatrixVersion) {
    this.tFocusMatrixVersion = tFocusMatrixVersion;
  }

  public String getLink() {
    return this.link;
  }

  public void setLink(final String link) {
    this.link = link;
  }

  public TabvAttribute getTabvAttribute() {
    return this.tabvAttribute;
  }

  public void setTabvAttribute(final TabvAttribute tabvAttribute) {
    this.tabvAttribute = tabvAttribute;
  }

  public TabvUseCase getTabvUseCas() {
    return this.tabvUseCas;
  }

  public void setTabvUseCas(final TabvUseCase tabvUseCas) {
    this.tabvUseCas = tabvUseCas;
  }

  public TabvUseCaseSection getTabvUseCaseSection() {
    return this.tabvUseCaseSection;
  }

  public void setTabvUseCaseSection(final TabvUseCaseSection tabvUseCaseSection) {
    this.tabvUseCaseSection = tabvUseCaseSection;
  }


  /**
   * @return the deletedFlag
   */
  public String getDeletedFlag() {
    return this.deletedFlag;
  }


  /**
   * @param deletedFlag the deletedFlag to set
   */
  public void setDeletedFlag(final String deletedFlag) {
    this.deletedFlag = deletedFlag;
  }
}