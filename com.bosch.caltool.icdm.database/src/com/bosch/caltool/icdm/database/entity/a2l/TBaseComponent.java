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


/**
 * The persistent class for the T_BASE_COMPONENTS database table.
 */
@Entity
@Table(name = "T_BASE_COMPONENTS")
@NamedQuery(name = "TBaseComponent.findAll", query = "SELECT t FROM TBaseComponent t")
public class TBaseComponent implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "BC_ID", unique = true, nullable = false)
  private long bcId;

  @Column(name = "BC_DESC", length = 4000)
  private String bcDesc;

  @Column(name = "BC_NAME", nullable = false, length = 255)
  private String bcName;

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
  @OneToMany(mappedBy = "TBaseComponent", fetch = FetchType.LAZY)
  private Set<TFc2wpMapping> TFc2wpMappings;

  public TBaseComponent() {}

  public long getBcId() {
    return this.bcId;
  }

  public void setBcId(final long bcId) {
    this.bcId = bcId;
  }

  public String getBcDesc() {
    return this.bcDesc;
  }

  public void setBcDesc(final String bcDesc) {
    this.bcDesc = bcDesc;
  }

  public String getBcName() {
    return this.bcName;
  }

  public void setBcName(final String bcName) {
    this.bcName = bcName;
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

  public long getVersion() {
    return this.version;
  }

  public void setVersion(final long version) {
    this.version = version;
  }

  public Set<TFc2wpMapping> getTFc2wpMappings() {
    return this.TFc2wpMappings;
  }

  public void setTFc2wpMappings(final Set<TFc2wpMapping> TFc2wpMappings) {
    this.TFc2wpMappings = TFc2wpMappings;
  }

  public TFc2wpMapping addTFc2wpMapping(final TFc2wpMapping TFc2wpMapping) {
    getTFc2wpMappings().add(TFc2wpMapping);
    TFc2wpMapping.setTBaseComponent(this);

    return TFc2wpMapping;
  }

  public TFc2wpMapping removeTFc2wpMapping(final TFc2wpMapping TFc2wpMapping) {
    getTFc2wpMappings().remove(TFc2wpMapping);
    TFc2wpMapping.setTBaseComponent(null);

    return TFc2wpMapping;
  }

}