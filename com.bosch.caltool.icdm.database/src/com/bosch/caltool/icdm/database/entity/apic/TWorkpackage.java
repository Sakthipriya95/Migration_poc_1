package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;

import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpResp;


/**
 * The persistent class for the T_WORKPACKAGE database table.
 */
@Entity
@Table(name = "T_WORKPACKAGE")
@NamedQueries({
    @NamedQuery(name = TWorkpackage.NQ_FIND_ALL, query = "SELECT t FROM TWorkpackage t"),
    @NamedQuery(name = TWorkpackage.NQ_FIND_BY_WP_ID, query = "SELECT t FROM TWorkpackage t " +
        "       where t.wpId=:wpId") })
public class TWorkpackage implements Serializable {

  private static final long serialVersionUID = 1L;
  /**
   * Named query - Find all work packages
   */
  public static final String NQ_FIND_ALL = "TWorkpackage.findAll";
  /**
   *
   */
  public static final String NQ_FIND_BY_WP_ID = "TWorkpackageDivision.findByWpId";;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "WP_ID", unique = true, nullable = false, precision = 15)
  private long wpId;
  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;
  @Column(name = "CREATED_USER", length = 100)
  private String createdUser;
  @Column(name = "DELETE_FLAG", length = 1)
  private String deleteFlag;
  @Column(name = "DESC_ENG", length = 4000)
  private String descEng;
  @Column(name = "DESC_GER", length = 4000)
  private String descGer;
  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;
  @Column(name = "MODIFIED_USER", length = 100)
  private String modifiedUser;
  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  private Long version;
  @Column(name = "WP_NAME_E", length = 255)
  private String wpNameE;
  @Column(name = "WP_NAME_G", length = 255)
  private String wpNameG;

  // bi-directional one-to-one association to TWpmlWpMasterlist
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "WP_MASTERLIST_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TWpmlWpMasterlist tWpmlWpMasterList;

  // bi-directional many-to-one association to TWorkpackageDivision
  @OneToMany(mappedBy = "tWorkpackage", fetch = FetchType.LAZY)
  private List<TWorkpackageDivision> TWorkpackageDivisions;

  // bi-directional many-to-one association to TA2lWpResp
  @OneToMany(mappedBy = "TWorkpackage")
  private List<TA2lWpResp> tA2lWpResps;

  public TWorkpackage() {}

  public long getWpId() {
    return this.wpId;
  }

  public void setWpId(final long wpId) {
    this.wpId = wpId;
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

  public String getDeleteFlag() {
    return this.deleteFlag;
  }

  public void setDeleteFlag(final String deleteFlag) {
    this.deleteFlag = deleteFlag;
  }

  public String getDescEng() {
    return this.descEng;
  }

  public void setDescEng(final String descEng) {
    this.descEng = descEng;
  }

  public String getDescGer() {
    return this.descGer;
  }

  public void setDescGer(final String descGer) {
    this.descGer = descGer;
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

  public String getWpNameE() {
    return this.wpNameE;
  }

  public void setWpNameE(final String wpNameE) {
    this.wpNameE = wpNameE;
  }

  public String getWpNameG() {
    return this.wpNameG;
  }

  public void setWpNameG(final String wpNameG) {
    this.wpNameG = wpNameG;
  }

  public List<TWorkpackageDivision> getTWorkpackageDivisions() {
    return this.TWorkpackageDivisions;
  }

  public void setTWorkpackageDivisions(final List<TWorkpackageDivision> TWorkpackageDivisions) {
    this.TWorkpackageDivisions = TWorkpackageDivisions;
  }

  public List<TA2lWpResp> getTA2lWpResps() {
    return this.tA2lWpResps;
  }

  public void setTA2lWpResps(final List<TA2lWpResp> TA2lWpResps) {
    this.tA2lWpResps = TA2lWpResps;
  }


  /**
   * @return the tWpmlWpMasterList
   */
  public TWpmlWpMasterlist gettWpmlWpMasterList() {
    return this.tWpmlWpMasterList;
  }


  /**
   * @param tWpmlWpMasterList the tWpmlWpMasterList to set
   */
  public void settWpmlWpMasterList(final TWpmlWpMasterlist tWpmlWpMasterList) {
    this.tWpmlWpMasterList = tWpmlWpMasterList;
  }

  public TWorkpackageDivision addTWorkpackageDivisions(final TWorkpackageDivision TWorkpackageDivision) {
    if (this.TWorkpackageDivisions == null) {
      this.TWorkpackageDivisions = new ArrayList<>();
    }
    getTWorkpackageDivisions().add(TWorkpackageDivision);
    TWorkpackageDivision.setTWorkpackage(this);

    return TWorkpackageDivision;
  }

  public TWorkpackageDivision removeTWorkpackageDivisions(final TWorkpackageDivision TWorkpackageDivision) {
    if (this.TWorkpackageDivisions == null) {
      this.TWorkpackageDivisions = new ArrayList<>();
    }
    getTWorkpackageDivisions().remove(TWorkpackageDivision);
    TWorkpackageDivision.setTWorkpackage(null);

    return TWorkpackageDivision;
  }


}
