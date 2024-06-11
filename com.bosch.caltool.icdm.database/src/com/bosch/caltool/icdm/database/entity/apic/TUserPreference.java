package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the T_USER_PREFERENCES database table.
 */
@Entity
@Table(name = "T_USER_PREFERENCES")
@NamedQuery(name = TUserPreference.NQ_FIND_BY_USER_ID, query = "SELECT t FROM TUserPreference t where t.tabvApicUser.userId = :userId")
public class TUserPreference implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Named query - Find user preference by user id
   */
  public static final String NQ_FIND_BY_USER_ID = "TUserPreference.findByUserId";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "USER_PREF_ID", unique = true, nullable = false)
  private long userPrefId;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false)
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "USER_PREF_KEY", nullable = false)
  private String userPrefKey;

  @Column(name = "USER_PREF_VAL", nullable = false)
  private String userPrefVal;

  @Column(name = "\"VERSION\"", nullable = false)
  private BigDecimal version;

  @ManyToOne
  @JoinColumn(name = "USER_ID")
  private TabvApicUser tabvApicUser;

  public TUserPreference() {
    // not applicable
  }

  public long getUserPrefId() {
    return this.userPrefId;
  }

  public void setUserPrefId(final long userPrefId) {
    this.userPrefId = userPrefId;
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


  public String getUserPrefKey() {
    return this.userPrefKey;
  }

  public void setUserPrefKey(final String userPrefKey) {
    this.userPrefKey = userPrefKey;
  }

  public String getUserPrefVal() {
    return this.userPrefVal;
  }

  public void setUserPrefVal(final String userPrefVal) {
    this.userPrefVal = userPrefVal;
  }

  public BigDecimal getVersion() {
    return this.version;
  }

  public void setVersion(final BigDecimal version) {
    this.version = version;
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

}
