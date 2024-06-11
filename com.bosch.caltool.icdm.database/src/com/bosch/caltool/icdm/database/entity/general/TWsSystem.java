package com.bosch.caltool.icdm.database.entity.general;

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
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * The persistent class for the T_WS_SYSTEMS database table.
 */
@Entity
@Table(name = "T_WS_SYSTEMS")
@NamedQuery(name = TWsSystem.ALL_T_WS_SYSTEM, query = "SELECT t FROM TWsSystem t")
public class TWsSystem implements Serializable {

  /**
   * all ws syatems
   */
  public static final String ALL_T_WS_SYSTEM = "TWsSystem.findAll";

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "SYSTEM_ID", unique = true, nullable = false, precision = 15)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  private long systemId;

  @Column(name = "SYSTEM_TOKEN", nullable = false, length = 200)
  private String systemToken;

  @Column(name = "SYSTEM_TYPE", nullable = false, length = 100)
  private String systemType;

  @Column(name = "SERV_ACCESS_TYPE", nullable = false, length = 1)
  private String servAccessType;

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

  @OneToMany(mappedBy = "tWsSystem", fetch = FetchType.LAZY)
  private List<TWsSystemService> tWsSystemServices;

  /**
   * constrcutor empty
   */
  public TWsSystem() {
    // empty default created by framework.
  }

  /**
   * @return systemId
   */
  public long getSystemId() {
    return this.systemId;
  }

  /**
   * @param systemId systemId
   */
  public void setSystemId(final long systemId) {
    this.systemId = systemId;
  }

  /**
   * @return systemToken
   */
  public String getSystemToken() {
    return this.systemToken;
  }

  /**
   * @param systemToken systemToken
   */
  public void setSystemToken(final String systemToken) {
    this.systemToken = systemToken;
  }

  /**
   * @return systemType
   */
  public String getSystemType() {
    return this.systemType;
  }

  /**
   * @param systemType systemType
   */
  public void setSystemType(final String systemType) {
    this.systemType = systemType;
  }


  /**
   * @return the servAccessType
   */
  public String getServAccessType() {
    return this.servAccessType;
  }


  /**
   * @param servAccessType the servAccessType to set
   */
  public void setServAccessType(final String servAccessType) {
    this.servAccessType = servAccessType;
  }


  /**
   * @return the createdDate
   */
  public Timestamp getCreatedDate() {
    return this.createdDate;
  }


  /**
   * @param createdDate the createdDate to set
   */
  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
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
  public long getVersion() {
    return this.version;
  }

  /**
   * @param version the version to set
   */
  public void setVersion(final long version) {
    this.version = version;
  }


  /**
   * @return the tWsSystemServices
   */
  public List<TWsSystemService> getTWsSystemServices() {
    return this.tWsSystemServices;
  }


  /**
   * @param tWsSystemServices the tWsSystemServices to set
   */
  public void setTWsSystemServices(final List<TWsSystemService> tWsSystemServices) {
    this.tWsSystemServices = tWsSystemServices;
  }

  /**
   * @param tWsSystemService TWsSystemService to add
   * @return TWsSystemService
   */
  public TWsSystemService addTWsSystemService(final TWsSystemService tWsSystemService) {
    if (getTWsSystemServices() == null) {
      setTWsSystemServices(new ArrayList<>());
    }

    getTWsSystemServices().add(tWsSystemService);
    tWsSystemService.setTWsSystem(this);

    return tWsSystemService;
  }

  /**
   * @param tWsSystemService TWsSystemService to remove
   */
  public void removeTWsSystemService(final TWsSystemService tWsSystemService) {
    if (getTWsSystemServices() != null) {
      getTWsSystemServices().remove(tWsSystemService);
    }
  }

}