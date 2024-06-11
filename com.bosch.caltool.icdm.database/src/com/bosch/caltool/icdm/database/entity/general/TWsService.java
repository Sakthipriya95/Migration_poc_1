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

import com.bosch.caltool.dmframework.entity.IEntity;


/**
 * The persistent class for the T_WS_SYSTEMS database table.
 */
@Entity
@Table(name = "T_WS_SERVICES")
@NamedQuery(name = TWsService.NQ_ALL_T_WS_SERVICE, query = "SELECT t FROM TWsService t")
public class TWsService implements Serializable, IEntity {

  /**
   * all ws syatems
   */
  public static final String NQ_ALL_T_WS_SERVICE = "TWsService.findAll";

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "WS_SERV_ID", unique = true, nullable = false, precision = 15)
  private long wsServId;

  @Column(name = "SERV_METHOD", nullable = false, length = 10)
  private String servMethod;

  @Column(name = "SERV_URI", nullable = false, length = 200)
  private String servUri;

  @Column(name = "MODULE", nullable = false, length = 10)
  private String module;

  @Column(name = "SERV_DESC", length = 2000)
  private String servDesc;

  @Column(name = "SERVICE_SCOPE", nullable = false, length = 1)
  private String serviceScope;

  @Column(name = "DELETE_FLAG", length = 1)
  private String deleteFlag;

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

  @OneToMany(mappedBy = "tWsService", fetch = FetchType.LAZY)
  private List<TWsSystemService> tWsSystemServices;

  /**
   * constrcutor empty
   */
  public TWsService() {
    // empty default created by framework.
  }


  /**
   * @return the wsServId
   */
  public long getWsServId() {
    return this.wsServId;
  }


  /**
   * @param wsServId the wsServId to set
   */
  public void setWsServId(final long wsServId) {
    this.wsServId = wsServId;
  }


  /**
   * @return the servMethod
   */
  public String getServMethod() {
    return this.servMethod;
  }


  /**
   * @param servMethod the servMethod to set
   */
  public void setServMethod(final String servMethod) {
    this.servMethod = servMethod;
  }


  /**
   * @return the servUri
   */
  public String getServUri() {
    return this.servUri;
  }


  /**
   * @param servUri the servUri to set
   */
  public void setServUri(final String servUri) {
    this.servUri = servUri;
  }


  /**
   * @return the module
   */
  public String getModule() {
    return this.module;
  }


  /**
   * @param module the module to set
   */
  public void setModule(final String module) {
    this.module = module;
  }


  /**
   * @return the servDesc
   */
  public String getServDesc() {
    return this.servDesc;
  }


  /**
   * @param servDesc the servDesc to set
   */
  public void setServDesc(final String servDesc) {
    this.servDesc = servDesc;
  }


  /**
   * @return the serviceScope
   */
  public String getServiceScope() {
    return this.serviceScope;
  }


  /**
   * @param serviceScope the serviceScope to set
   */
  public void setServiceScope(final String serviceScope) {
    this.serviceScope = serviceScope;
  }


  /**
   * @return the deleteFlag
   */
  public String getDeleteFlag() {
    return this.deleteFlag;
  }


  /**
   * @param deleteFlag the deleteFlag to set
   */
  public void setDeleteFlag(final String deleteFlag) {
    this.deleteFlag = deleteFlag;
  }


  /**
   * @return the createdDate
   */
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
    tWsSystemService.setTWsService(this);

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