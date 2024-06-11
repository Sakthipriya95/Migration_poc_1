package com.bosch.caltool.icdm.database.entity.comppkg;

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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.annotations.OptimisticLocking;
import org.eclipse.persistence.annotations.PrivateOwned;


/**
 * The persistent class for the T_COMP_PKG_BC database table.
 */
@Entity
@OptimisticLocking(cascade = true)
@Table(name = "T_COMP_PKG_BC")
@NamedQueries({
    @NamedQuery(name = "TCompPkgBc.findAll", query = "SELECT t FROM TCompPkgBc t"),
    @NamedQuery(name = "TCompPkgBc.findByCompId", query = "SELECT t FROM TCompPkgBc t where t.TCompPkg.compPkgId =:compPkgId") })
public class TCompPkgBc implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMP_SEQ_GENERATOR")
  @Column(name = "COMP_BC_ID")
  private long compBcId;

  @Column(name = "BC_NAME")
  private String bcName;

  @Column(name = "BC_SEQ_NO")
  private Long bcSeqNo;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  private Long version;

  // bi-directional many-to-one association to TCompPkg
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "COMP_PKG_ID")
  private TCompPkg TCompPkg;

  // bi-directional many-to-one association to TCompPkgBcFc
  @OneToMany(mappedBy = "TCompPkgBc", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  @PrivateOwned
  private List<TCompPkgBcFc> TCompPkgBcFcs;

  public TCompPkgBc() {}

  public long getCompBcId() {
    return this.compBcId;
  }

  public void setCompBcId(final long compBcId) {
    this.compBcId = compBcId;
  }

  public String getBcName() {
    return this.bcName;
  }

  public void setBcName(final String bcName) {
    this.bcName = bcName;
  }

  public Long getBcSeqNo() {
    return this.bcSeqNo;
  }

  public void setBcSeqNo(final Long bcSeqNo) {
    this.bcSeqNo = bcSeqNo;
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

  public TCompPkg getTCompPkg() {
    return this.TCompPkg;
  }

  public void setTCompPkg(final TCompPkg TCompPkg) {
    this.TCompPkg = TCompPkg;
  }

  public List<TCompPkgBcFc> getTCompPkgBcFcs() {
    return this.TCompPkgBcFcs;
  }

  public void setTCompPkgBcFcs(final List<TCompPkgBcFc> TCompPkgBcFcs) {
    this.TCompPkgBcFcs = TCompPkgBcFcs;
  }

  /**
   * @param fcEntity entity to remove
   */
  public void removeTCompPkgBcFc(final TCompPkgBcFc fcEntity) {
    if (this.TCompPkgBcFcs != null) {
      this.TCompPkgBcFcs.remove(fcEntity);
    }
  }

  /**
   * @param entity entity to add
   */
  public void addTCompPkgBcFc(final TCompPkgBcFc entity) {
    if (this.TCompPkgBcFcs == null) {
      this.TCompPkgBcFcs = new ArrayList<>();
    }
    this.TCompPkgBcFcs.add(entity);

  }

}