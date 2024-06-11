package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.OptimisticLocking;
import org.eclipse.persistence.annotations.PrivateOwned;


/**
 * The persistent class for the T_ALIAS_DEFINITION database table.
 */
@Entity
@Table(name = "T_ALIAS_DEFINITION")
@OptimisticLocking(cascade = true)
@NamedQuery(name = TAliasDefinition.GET_ALL, query = "SELECT t FROM TAliasDefinition t")
public class TAliasDefinition implements Serializable {

  /**
   * Named query - get all
   */
  public static final String GET_ALL = "TAliasDefinition.findAll";

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "AD_ID")
  private long adId;

  @Column(name = "AD_NAME")
  private String adName;

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

  // bi-directional many-to-one association to TabvProjectidcard
  @OneToMany(mappedBy = "taliasDefinition")
  private List<TabvProjectidcard> tabvProjectidcards;

  // bi-directional many-to-one association to TAliasDetail
  @OneToMany(mappedBy = "tAliasDefinition")
  @PrivateOwned
  private List<TAliasDetail> tAliasDetails;

  public TAliasDefinition() {}

  public long getAdId() {
    return this.adId;
  }

  public void setAdId(final long adId) {
    this.adId = adId;
  }

  public String getAdName() {
    return this.adName;
  }

  public void setAdName(final String adName) {
    this.adName = adName;
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

  public List<TabvProjectidcard> getTabvProjectidcards() {
    return this.tabvProjectidcards;
  }

  public void setTabvProjectidcards(final List<TabvProjectidcard> tabvProjectidcards) {
    this.tabvProjectidcards = tabvProjectidcards;
  }

  public List<TAliasDetail> getTAliasDetails() {
    return this.tAliasDetails;
  }

  public void setTAliasDetails(final List<TAliasDetail> TAliasDetails) {
    this.tAliasDetails = TAliasDetails;
  }

  public void addTAliasDetail(final TAliasDetail tAliasDetail) {
    List<TAliasDetail> tAliasDetailList = getTAliasDetails();
    if (tAliasDetailList == null) {
      tAliasDetailList = new ArrayList<>();
      setTAliasDetails(tAliasDetailList);
    }
    tAliasDetailList.add(tAliasDetail);

    tAliasDetail.setTAliasDefinition(this);
  }

  public void removeTAliasDetail(final TAliasDetail tAliasDetail) {
    List<TAliasDetail> tAliasDetailList = getTAliasDetails();
    if (tAliasDetailList != null) {
      tAliasDetailList.remove(tAliasDetail);
    }
  }

}