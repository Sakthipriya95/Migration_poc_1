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


/**
 * The persistent class for the TABV_PID_FAVORITES database table.
 */
@Entity
@Table(name = "TABV_PID_FAVORITES")
@NamedQueries(value = { @NamedQuery(name = TabvPidFavorite.NQ_GET_ALL, query = "SELECT uf FROM TabvPidFavorite uf ") })
public class TabvPidFavorite implements Serializable {

  /**
   * Named query to fetch all favourites.
   */
  public static final String NQ_GET_ALL = "TabvPidFavorite.getAll";


  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "FAV_ID", unique = true, nullable = false, precision = 15)
  private long favId;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 100)
  private String createdUser;


  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 100)
  private String modifiedUser;

  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  private Long version;


  // bi-directional many-to-one association to TabvApicUser
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "USER_ID", nullable = false)
  private TabvApicUser tabvApicUser;


  // bi-directional many-to-one association to TabvProjectidcard
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PROJECT_ID", nullable = false)
  private TabvProjectidcard tabvProjectidcard;

  public TabvPidFavorite() {}

  public long getFavId() {
    return this.favId;
  }

  public void setFavId(final long favId) {
    this.favId = favId;
  }

  public Timestamp getCreatedDate() {
    return this.createdDate;
  }

  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
  }


  public TabvProjectidcard getTabvProjectidcard() {
    return this.tabvProjectidcard;
  }

  public void setTabvProjectidcard(final TabvProjectidcard tabvProjectidcard) {
    this.tabvProjectidcard = tabvProjectidcard;
  }

  /**
   * @return the tabvApicUser
   */
  public TabvApicUser getTabvApicUser() {
    return this.tabvApicUser;
  }

  /**
   * @param tabvApicUser the tabvApicUser to set
   */
  public void setTabvApicUser(final TabvApicUser tabvApicUser) {
    this.tabvApicUser = tabvApicUser;
  }

  /**
   * @return the createdUser
   */
  public String getCreatedUser() {
    return this.createdUser;
  }


  /**
   * @param createdUser the createdUser to set
   */
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }


  /**
   * @return the modifiedDate
   */
  public Timestamp getModifiedDate() {
    return this.modifiedDate;
  }


  /**
   * @param modifiedDate the modifiedDate to set
   */
  public void setModifiedDate(final Timestamp modifiedDate) {
    this.modifiedDate = modifiedDate;
  }


  /**
   * @return the modifiedUser
   */
  public String getModifiedUser() {
    return this.modifiedUser;
  }


  /**
   * @param modifiedUser the modifiedUser to set
   */
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  /**
   * @return the version
   */
  public Long getVersion() {
    return this.version;
  }


  /**
   * @param version the version to set
   */
  public void setVersion(final Long version) {
    this.version = version;
  }
}