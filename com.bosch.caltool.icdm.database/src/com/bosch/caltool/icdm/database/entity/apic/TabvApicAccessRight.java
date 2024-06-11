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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * The persistent class for the TABV_APIC_ACCESS_RIGHTS database table.
 */
@NamedQueries(value = {
    @NamedQuery(name = TabvApicAccessRight.NQ_GET_ALL_APIC_WRITE_USERS, query = "select apicRight from TabvApicAccessRight apicRight where apicRight.accessRight='APIC_WRITE'")})
@Entity
@Table(name = "TABV_APIC_ACCESS_RIGHTS")
public class TabvApicAccessRight implements Serializable {

  private static final long serialVersionUID = 1L;
  private long accessrightId;
  private String accessRight;
  private Timestamp createdDate;
  private String createdUser;
  private Timestamp modifiedDate;
  private String modifiedUser;
  private String moduleName;
  private Long version;
  private TabvApicUser tabvApicUser;

  public TabvApicAccessRight() {}

  /**
   * Named query to fetch APIC WRITE users.
   */
  public static final String NQ_GET_ALL_APIC_WRITE_USERS = "TabvApicAccessRight.getAllApicWriteUsers";

  @Id
  @SequenceGenerator(name = "SEQV_ATTRIBUTES_GENERATOR", sequenceName = "SEQV_ATTRIBUTES", allocationSize = 50)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "ACCESSRIGHT_ID", unique = true, nullable = false)
  public long getAccessrightId() {
    return this.accessrightId;
  }

  public void setAccessrightId(final long accessrightId) {
    this.accessrightId = accessrightId;
  }


  @Column(name = "ACCESS_RIGHT", nullable = false, length = 30)
  public String getAccessRight() {
    return this.accessRight;
  }

  public void setAccessRight(final String accessRight) {
    this.accessRight = accessRight;
  }

  @Column(name = "CREATED_DATE", nullable = false)
  public Timestamp getCreatedDate() {
    return this.createdDate;
  }

  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
  }


  @Column(name = "CREATED_USER", nullable = false, length = 30)
  public String getCreatedUser() {
    return this.createdUser;
  }

  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }


  @Column(name = "MODIFIED_DATE")
  public Timestamp getModifiedDate() {
    return this.modifiedDate;
  }

  public void setModifiedDate(final Timestamp modifiedDate) {
    this.modifiedDate = modifiedDate;
  }


  @Column(name = "MODIFIED_USER", length = 30)
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }


  @Column(name = "MODULE_NAME", nullable = false, length = 30)
  public String getModuleName() {
    return this.moduleName;
  }

  public void setModuleName(final String moduleName) {
    this.moduleName = moduleName;
  }


  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }


  // bi-directional many-to-one association to TabvApicUser
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "USER_ID")
  public TabvApicUser getTabvApicUser() {
    return this.tabvApicUser;
  }

  public void setTabvApicUser(final TabvApicUser tabvApicUser) {
    this.tabvApicUser = tabvApicUser;
  }

}