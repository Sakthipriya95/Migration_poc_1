package com.bosch.caltool.icdm.database.entity.a2l;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.bosch.caltool.dmframework.entity.IEntity;
import com.bosch.caltool.icdm.database.entity.apic.TWorkpackageDivision;


/**
 * The persistent class for the T_WP_RESOURCE database table.
 */
@Entity
@Table(name = "T_WP_RESOURCE")
@NamedQuery(name = TWpResource.NQ_FIND_ALL, query = "SELECT t FROM TWpResource t")
public class TWpResource implements IEntity, Serializable {

  private static final long serialVersionUID = 1L;
  /**
   * Named query - Find all work package resources
   */
  public static final String NQ_FIND_ALL = "TWpResource.findAll";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "WP_RES_ID", unique = true, nullable = false)
  private long wpResId;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 30)
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 30)
  private String modifiedUser;

  @Column(name = "RESOURCE_CODE", nullable = false, length = 40)
  private String resourceCode;

  @Column(name = "RESOURCE_DESC", length = 4000)
  private String resourceDesc;

  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private long version;

  // bi-directional many-to-one association to TWorkpackageDivision
  @OneToMany(mappedBy = "tWpResource", fetch = FetchType.LAZY)
  private Set<TWorkpackageDivision> TWorkpackageDivisions;

  public TWpResource() {}

  public long getWpResId() {
    return this.wpResId;
  }

  public void setWpResId(final long wpResId) {
    this.wpResId = wpResId;
  }

  @Override
  public Timestamp getCreatedDate() {
    return this.createdDate;
  }

  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
  }

  @Override
  public String getCreatedUser() {
    return this.createdUser;
  }

  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  @Override
  public Timestamp getModifiedDate() {
    return this.modifiedDate;
  }

  public void setModifiedDate(final Timestamp modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  @Override
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  public String getResourceCode() {
    return this.resourceCode;
  }

  public void setResourceCode(final String resourceCode) {
    this.resourceCode = resourceCode;
  }

  public String getResourceDesc() {
    return this.resourceDesc;
  }

  public void setResourceDesc(final String resourceDesc) {
    this.resourceDesc = resourceDesc;
  }

  @Override
  public long getVersion() {
    return this.version;
  }

  public void setVersion(final long version) {
    this.version = version;
  }

  public Set<TWorkpackageDivision> getTWorkpackageDivisions() {
    return this.TWorkpackageDivisions;
  }

  public void setTWorkpackageDivisions(final Set<TWorkpackageDivision> TWorkpackageDivisions) {
    this.TWorkpackageDivisions = TWorkpackageDivisions;
  }

  public TWorkpackageDivision addTWorkpackageDivision(final TWorkpackageDivision TWorkpackageDivision) {
    getTWorkpackageDivisions().add(TWorkpackageDivision);
    TWorkpackageDivision.setTWpResource(this);

    return TWorkpackageDivision;
  }

  public TWorkpackageDivision removeTWorkpackageDivision(final TWorkpackageDivision TWorkpackageDivision) {
    getTWorkpackageDivisions().remove(TWorkpackageDivision);
    TWorkpackageDivision.setTWpResource(null);

    return TWorkpackageDivision;
  }

}