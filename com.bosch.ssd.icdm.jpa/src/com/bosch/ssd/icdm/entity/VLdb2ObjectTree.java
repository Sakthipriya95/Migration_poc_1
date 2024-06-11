package com.bosch.ssd.icdm.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;

import com.bosch.ssd.icdm.entity.keys.VLdb2ObjectTreePK;

/**
 * The persistent class for the V_LDB2_OBJECT_TREE database table.
 */
@IdClass(VLdb2ObjectTreePK.class)
@Entity
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
@SqlResultSetMappings({
    @SqlResultSetMapping(name = "objectTree", entities = { @EntityResult(entityClass = VLdb2ObjectTreePK.class) }),
    @SqlResultSetMapping(name = "getNodeID", entities = { @EntityResult(entityClass = VLdb2ObjectTreePK.class) }) })
@NamedQueries({
    @NamedQuery(name = "VLdb2ObjectTree.revisionNodeIdDetail", query = "Select  obj1.nodeId from VLdb2ObjectTree obj1  where obj1.parentId=" +
        "(Select obj1.nodeId from VLdb2ObjectTree obj1  where obj1.parentId=" +
        "(Select  obj1.nodeId from VLdb2ObjectTree obj1  where  obj1.description = (:description) and  obj1.scope =5) and  obj1.scope =7) and  obj1.scope =8"),


})
@Table(name = "V_LDB2_OBJECT_TREE")


public class VLdb2ObjectTree implements Serializable {

  private static final long serialVersionUID = 1L;

  @Temporal(TemporalType.DATE)
  @Column(name = "CRE_DATE")
  private Date creDate;

  @Column(name = "CRE_USER", length = 30)
  private String creUser;

  @Column(name = "DESCRIPTION", length = 1000)
  private String description;

  @Column(nullable = false, length = 1)
  private String hide;

  @Temporal(TemporalType.DATE)
  @Column(name = "MOD_DATE")
  private Date modDate;

  @Column(name = "MOD_USER", length = 30)
  private String modUser;

  @Id
  @Column(name = "NODE_ID", nullable = false, precision = 15)
  private BigDecimal nodeId;

  @Column(name = "OBJECT_ID", precision = 15)
  private BigDecimal objectId;

  @Column(name = "PARENT_ID", precision = 15)
  private BigDecimal parentId;

  @Column(name = "PRJ_TYP", length = 32)
  private String prjTyp;

  @Column(length = 1)
  private String production;

  @Column(length = 1)
  private String component;
//SSD-238 Series flag for buggy
  @Column(length = 1)
  private String series;

  @Column(name = "SCOPE", precision = 1)
  private BigDecimal scope;

  /**
   *
   */
  public VLdb2ObjectTree() {
    /*
     * constructor
     */
  }

  /**
   * @return creDate
   */
  public Date getCreDate() {
    return (Date) this.creDate.clone();
  }

  /**
   * @param creDate creDate
   */
  public void setCreDate(final Date creDate) {
    this.creDate = (Date) creDate.clone();
  }

  /**
   * @return creUser
   */
  public String getCreUser() {
    return this.creUser;
  }

  /**
   * @param creUser creUser
   */
  public void setCreUser(final String creUser) {
    this.creUser = creUser;
  }

  /**
   * @return description
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * @param description description
   */
  public void setDescription(final String description) {
    this.description = description;
  }

  /**
   * @return hide
   */
  public String getHide() {
    return this.hide;
  }

  /**
   * @param hide hide
   */
  public void setHide(final String hide) {
    this.hide = hide;
  }

  /**
   * @return modDate
   */
  public Date getModDate() {
    return (Date) this.modDate.clone();
  }

  /**
   * @param modDate modDate
   */
  public void setModDate(final Date modDate) {
    this.modDate = (Date) modDate.clone();
  }

  /**
   * @return modUser
   */
  public String getModUser() {
    return this.modUser;
  }

  /**
   * @param modUser modUser
   */
  public void setModUser(final String modUser) {
    this.modUser = modUser;
  }

  /**
   * @return nodeId
   */
  public BigDecimal getNodeId() {
    return this.nodeId;
  }

  /**
   * @param nodeId nodeId
   */
  public void setNodeId(final BigDecimal nodeId) {
    this.nodeId = nodeId;
  }

  /**
   * @return objectId
   */
  public BigDecimal getObjectId() {
    return this.objectId;
  }

  /**
   * @param objectId objectId
   */
  public void setObjectId(final BigDecimal objectId) {
    this.objectId = objectId;
  }

  /**
   * @return parentId
   */
  public BigDecimal getParentId() {
    return this.parentId;
  }

  /**
   * @param parentId parentId
   */
  public void setParentId(final BigDecimal parentId) {
    this.parentId = parentId;
  }

  /**
   * @return prjTyp
   */
  public String getPrjTyp() {
    return this.prjTyp;
  }

  /**
   * @param prjTyp prjTyp
   */
  public void setPrjTyp(final String prjTyp) {
    this.prjTyp = prjTyp;
  }

  /**
   * @return production
   */
  public String getProduction() {
    return this.production;
  }

  /**
   * @param production production
   */
  public void setProduction(final String production) {
    this.production = production;
  }

  /**
   * @return component
   */
  public String getComponent() {
    return this.component;
  }

  /**
   * @param component component
   */
  public void setComponent(final String component) {
    this.production = component;
  }

  /**
   * @return scope
   */
  public BigDecimal getScope() {
    return this.scope;
  }

  /**
   * @param scope scope
   */
  public void setScope(final BigDecimal scope) {
    this.scope = scope;
  }

  /**
   * @return the series
   */
  public String getSeries() {
    return this.series;
  }

  /**
   * @param series the series to set
   */
  public void setSeries(final String series) {
    this.series = series;
  }

}