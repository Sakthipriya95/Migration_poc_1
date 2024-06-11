/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;

/**
 * @author SMN6KOR
 *
 */
@Entity
@Table(name = "K5ESK_LDB2.TEMP_ICDM_NODE_TABLE")
@NamedQueries({
    @NamedQuery(name = "TempNonSDOMNodeList.findAllNodes", query = "SELECT t from TempNonSDOMNodeList t where t.primaryId =:primaryId"),
    @NamedQuery(name = "TempNonSDOMNodeList.deleteEntries", query = "delete from TempNonSDOMNodeList where primaryId =:primaryId") })
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
@IdClass(com.bosch.ssd.icdm.entity.keys.TempNonSDOMNodeListPK.class)
public class TempNonSDOMNodeList implements Serializable {
  private static final long serialVersionUID = 1L;

 
  @Id
  @Column(name = "PRIMARY_ID")
  private String primaryId;


  @Id
  @Column(name = "SEQ_NO")
  private String seqNo;


  @Column(name = "NODE_SCOPE")
  private String nodeScope;
  
  @Column(name ="NODE_ID")
  private BigDecimal nodeId;
  
  @Column(name ="PRO_REV_ID")
  private BigDecimal proRevID;

  
  /**
   * @return the primaryId
   */
  public String getPrimaryId() {
    return primaryId;
  }

  
  /**
   * @param primaryId the primaryId to set
   */
  public void setPrimaryId(String primaryId) {
    this.primaryId = primaryId;
  }

  
  /**
   * @return the seqNo
   */
  public String getSeqNo() {
    return seqNo;
  }

  
  /**
   * @param seqNo the seqNo to set
   */
  public void setSeqNo(String seqNo) {
    this.seqNo = seqNo;
  }

  
  /**
   * @return the nodeScope
   */
  public String getNodeScope() {
    return nodeScope;
  }

  
  /**
   * @param nodeScope the nodeScope to set
   */
  public void setNodeScope(String nodeScope) {
    this.nodeScope = nodeScope;
  }

  
  /**
   * @return the nodeId
   */
  public BigDecimal getNodeId() {
    return nodeId;
  }

  
  /**
   * @param nodeId the nodeId to set
   */
  public void setNodeId(BigDecimal nodeId) {
    this.nodeId = nodeId;
  }

  
  /**
   * @return the proRevID
   */
  public BigDecimal getProRevID() {
    return proRevID;
  }

  
  /**
   * @param proRevID the proRevID to set
   */
  public void setProRevID(BigDecimal proRevID) {
    this.proRevID = proRevID;
  }



  
}
