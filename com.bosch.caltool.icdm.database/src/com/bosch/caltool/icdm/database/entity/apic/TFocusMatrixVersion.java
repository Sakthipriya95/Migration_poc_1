package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.OptimisticLocking;
import org.eclipse.persistence.annotations.PrivateOwned;


/**
 * The persistent class for the T_FOCUS_MATRIX_VERSION database table.
 */
@Entity
@Table(name = "T_FOCUS_MATRIX_VERSION")
@OptimisticLocking(cascade = true)
public class TFocusMatrixVersion implements Serializable {


  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "FM_VERS_ID")
  private long fmVersId;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "\"LINK\"")
  private String link;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  private String name;

  private String remark;

  @Column(name = "REVIEWED_DATE")
  private Timestamp reviewedDate;

  @Column(name = "RVW_STATUS")
  private String rvwStatus;

  private String status;

  @Column(name = "REV_NUM")
  private long revNumber;

  @Column(name = "\"VERSION\"")
  @Version
  private long version;

  // bi-directional many-to-one association to TFocusMatrix
  @OneToMany(mappedBy = "tFocusMatrixVersion", fetch = FetchType.LAZY)
  @PrivateOwned
  private Set<TFocusMatrix> tFocusMatrixs;

  // bi-directional many-to-one association to TabvApicUser
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "REVIEWED_USER")
  private TabvApicUser reviewedUser;

  // bi-directional many-to-one association to TPidcVersion
  @ManyToOne
  @JoinColumn(name = "PIDC_VERS_ID")
  private TPidcVersion tPidcVersion;

  // bi-directional many-to-one association to TFocusMatrixVersionAttr
  @OneToMany(mappedBy = "tFocusMatrixVersion", fetch = FetchType.LAZY)
  private Set<TFocusMatrixVersionAttr> tFocusMatrixVersionAttrs;

  public TFocusMatrixVersion() {}

  public long getFmVersId() {
    return this.fmVersId;
  }

  public void setFmVersId(final long fmVersId) {
    this.fmVersId = fmVersId;
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

  public String getLink() {
    return this.link;
  }

  public void setLink(final String link) {
    this.link = link;
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

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getRemark() {
    return this.remark;
  }

  public void setRemark(final String remark) {
    this.remark = remark;
  }

  public Timestamp getReviewedDate() {
    return this.reviewedDate;
  }

  public void setReviewedDate(final Timestamp reviewedDate) {
    this.reviewedDate = reviewedDate;
  }

  public String getRvwStatus() {
    return this.rvwStatus;
  }

  public void setRvwStatus(final String rvwStatus) {
    this.rvwStatus = rvwStatus;
  }

  public String getStatus() {
    return this.status;
  }

  public void setStatus(final String status) {
    this.status = status;
  }

  public long getRevNumber() {
    return this.revNumber;
  }

  public void setRevNumber(final long revNumber) {
    this.revNumber = revNumber;
  }

  public long getVersion() {
    return this.version;
  }

  public void setVersion(final long version) {
    this.version = version;
  }

  public Set<TFocusMatrix> getTFocusMatrixs() {
    if (this.tFocusMatrixs == null) {
      this.tFocusMatrixs = new HashSet<>();
    }
    return this.tFocusMatrixs;
  }

  public void setTFocusMatrixs(final Set<TFocusMatrix> tFocusMatrixs) {
    this.tFocusMatrixs = tFocusMatrixs;
  }

  public TFocusMatrix addTFocusMatrix(final TFocusMatrix TFocusMatrix) {
    getTFocusMatrixs().add(TFocusMatrix);
    TFocusMatrix.setTFocusMatrixVersion(this);

    return TFocusMatrix;
  }

  public TFocusMatrix removeTFocusMatrix(final TFocusMatrix TFocusMatrix) {
    getTFocusMatrixs().remove(TFocusMatrix);
    TFocusMatrix.setTFocusMatrixVersion(null);

    return TFocusMatrix;
  }

  public TabvApicUser getReviewedUser() {
    return this.reviewedUser;
  }

  public void setReviewedUser(final TabvApicUser reviewedUser) {
    this.reviewedUser = reviewedUser;
  }

  public TPidcVersion getTPidcVersion() {
    return this.tPidcVersion;
  }

  public void setTPidcVersion(final TPidcVersion tPidcVersion) {
    this.tPidcVersion = tPidcVersion;
  }

  public Set<TFocusMatrixVersionAttr> getTFocusMatrixVersionAttrs() {
    if (this.tFocusMatrixVersionAttrs == null) {
      this.tFocusMatrixVersionAttrs = new HashSet<>();
    }
    return this.tFocusMatrixVersionAttrs;
  }

  public void setTFocusMatrixVersionAttrs(final Set<TFocusMatrixVersionAttr> TFocusMatrixVersionAttrs) {
    this.tFocusMatrixVersionAttrs = TFocusMatrixVersionAttrs;
  }

  public TFocusMatrixVersionAttr addTFocusMatrixVersionAttr(final TFocusMatrixVersionAttr TFocusMatrixVersionAttr) {
    getTFocusMatrixVersionAttrs().add(TFocusMatrixVersionAttr);
    TFocusMatrixVersionAttr.setTFocusMatrixVersion(this);

    return TFocusMatrixVersionAttr;
  }

  public TFocusMatrixVersionAttr removeTFocusMatrixVersionAttr(final TFocusMatrixVersionAttr TFocusMatrixVersionAttr) {
    getTFocusMatrixVersionAttrs().remove(TFocusMatrixVersionAttr);
    TFocusMatrixVersionAttr.setTFocusMatrixVersion(null);

    return TFocusMatrixVersionAttr;
  }

}