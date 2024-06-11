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

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;

/**
 * The persistent class for the TABV_APIC_NODE_ACCESS database table.
 */
@Entity
@Table(name = "TABV_APIC_NODE_ACCESS")
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.INVALIDATE)
@NamedQueries(value = {
    @NamedQuery(name = TabvApicNodeAccess.NQ_GET_ALL_BY_TYPE, query = "SELECT acc FROM TabvApicNodeAccess acc where acc.nodeType= :nodeType"),
    @NamedQuery(name = TabvApicNodeAccess.NQ_GET_ALL_BY_TYPE_N_NODES, query = "SELECT acc FROM TabvApicNodeAccess acc where acc.nodeType= :nodeType and acc.nodeId in :nodeIdSet"),
    @NamedQuery(name = TabvApicNodeAccess.NQ_GET_ALL_SPECIAL_NODES, query = "SELECT DISTINCT acc.nodeType,acc.nodeId FROM TabvApicNodeAccess acc where acc.nodeId < 0") })
public class TabvApicNodeAccess implements Serializable {

  /**
   * Named query to fetch TabvApicNodeAccess with Special node type.
   */
  public static final String NQ_GET_ALL_SPECIAL_NODES = "TabvApicNodeAccess.getAllBySpecialNode";

  /**
   * Named query to fetch TabvApicNodeAccess with node type.
   */
  public static final String NQ_GET_ALL_BY_TYPE = "TabvApicNodeAccess.getAllByType";

  /**
   * Named query to fetch TabvApicNodeAccess with node type and node Id set
   */
  public static final String NQ_GET_ALL_BY_TYPE_N_NODES = "TabvApicNodeAccess.getAllByTypeNNode";


  private static final long serialVersionUID = 1L;
  private long nodeaccessId;
  private Timestamp createdDate;
  private String createdUser;
  private String grantright;
  private Timestamp modifiedDate;
  private String modifiedUser;
  private Long nodeId;
  private String nodeType;
  private String owner;
  private String readright;
  private Long version;
  private String writeright;
  private TabvApicUser tabvApicUser;

  public TabvApicNodeAccess() {}


  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "NODEACCESS_ID", unique = true, nullable = false)
  public long getNodeaccessId() {
    return this.nodeaccessId;
  }

  public void setNodeaccessId(final long nodeaccessId) {
    this.nodeaccessId = nodeaccessId;
  }


  @Column(name = "CREATED_DATE", nullable = false)
  public Timestamp getCreatedDate() {
    return this.createdDate;
  }

  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
  }


  @Column(name = "CREATED_USER", nullable = false, length = 15)
  public String getCreatedUser() {
    return this.createdUser;
  }

  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }


  @Column(length = 1)
  public String getGrantright() {
    return this.grantright;
  }

  public void setGrantright(final String grantright) {
    this.grantright = grantright;
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


  @Column(name = "NODE_ID", nullable = false, precision = 15)
  public Long getNodeId() {
    return this.nodeId;
  }

  public void setNodeId(final Long nodeId) {
    this.nodeId = nodeId;
  }


  @Column(name = "NODE_TYPE", nullable = false, length = 15)
  public String getNodeType() {
    return this.nodeType;
  }

  public void setNodeType(final String nodeType) {
    this.nodeType = nodeType;
  }


  @Column(length = 1)
  public String getOwner() {
    return this.owner;
  }

  public void setOwner(final String owner) {
    this.owner = owner;
  }


  @Column(length = 1)
  public String getReadright() {
    return this.readright;
  }

  public void setReadright(final String readright) {
    this.readright = readright;
  }


  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }


  @Column(length = 1)
  public String getWriteright() {
    return this.writeright;
  }

  public void setWriteright(final String writeright) {
    this.writeright = writeright;
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