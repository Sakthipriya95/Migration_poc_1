package com.bosch.caltool.icdm.database.entity.a2l;

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
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;

import com.bosch.caltool.dmframework.entity.IEntity;


/**
 * The persistent class for the T_FCWP_MAP_PT_TYPES database table.
 */
@Entity
@Table(name = "T_FC2WP_MAP_PT_TYPES")
@NamedQuery(name = "TFc2wpMapPtType.findAll", query = "SELECT t FROM TFc2wpMapPtType t")
public class TFc2wpMapPtType implements IEntity, Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "FCWP_MAP_PT_TYPE_ID", unique = true, nullable = false)
  private long fcwpMapPtTypeId;

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
  private long version;

  // bi-directional many-to-one association to TFc2wpMapping
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "FCWP_MAP_ID", nullable = false)
  private TFc2wpMapping TFc2wpMapping;

  // bi-directional many-to-one association to TPowerTrainType
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PT_TYPE_ID", nullable = false)
  @BatchFetch(value = BatchFetchType.JOIN)
  private TPowerTrainType TPowerTrainType;

  public TFc2wpMapPtType() {}

  public long getFcwpMapPtTypeId() {
    return this.fcwpMapPtTypeId;
  }

  public void setFcwpMapPtTypeId(final long fcwpMapPtTypeId) {
    this.fcwpMapPtTypeId = fcwpMapPtTypeId;
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

  @Override
  public long getVersion() {
    return this.version;
  }

  public void setVersion(final long version) {
    this.version = version;
  }

  public TFc2wpMapping getTFc2wpMapping() {
    return this.TFc2wpMapping;
  }

  public void setTFc2wpMapping(final TFc2wpMapping TFc2wpMapping) {
    this.TFc2wpMapping = TFc2wpMapping;
  }

  public TPowerTrainType getTPowerTrainType() {
    return this.TPowerTrainType;
  }

  public void setTPowerTrainType(final TPowerTrainType TPowerTrainType) {
    this.TPowerTrainType = TPowerTrainType;
  }

}