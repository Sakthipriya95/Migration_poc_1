package com.bosch.caltool.icdm.database.entity.a2l;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * The persistent class for the T_A2L_WP_IMPORT_PROFILE database table.
 */
@Entity
@Table(name = "T_A2L_WP_IMPORT_PROFILE")
@NamedQuery(name = TA2lWpImportProfile.GET_ALL, query = "SELECT t FROM TA2lWpImportProfile t")
public class TA2lWpImportProfile implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Get all profiles
   */
  public static final String GET_ALL = "TA2lWpImportProfile.findAll";
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "PROFILE_ID", unique = true, nullable = false)
  private long profileId;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 30)
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 30)
  private String modifiedUser;

  @Column(name = "PROFILE_DETAILS", nullable = false)
  private String profileDetails;

  @Column(name = "PROFILE_NAME", nullable = false)
  private String profileName;

  @Column(name = "PROFILE_ORDER", nullable = false)
  private Long profileOrder;

  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private Long version;

  public TA2lWpImportProfile() {}

  public long getProfileId() {
    return this.profileId;
  }

  public void setProfileId(final long profileId) {
    this.profileId = profileId;
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

  public String getProfileDetails() {
    return this.profileDetails;
  }

  public void setProfileDetails(final String profileDetails) {
    this.profileDetails = profileDetails;
  }

  public String getProfileName() {
    return this.profileName;
  }

  public void setProfileName(final String profileName) {
    this.profileName = profileName;
  }

  public Long getProfileOrder() {
    return this.profileOrder;
  }

  public void setProfileOrder(final Long profileOrder) {
    this.profileOrder = profileOrder;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

}