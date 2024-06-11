package com.bosch.ssd.icdm.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;


/**
 * The persistent class for the V_SDOM_SSDNODE_VARIANTE database table.
 * 
 * @author SSN9COB
 */
@Entity
@Table(name = "V_SDOM_SSDNODE_VARIANTE")
@NamedQuery(name = "VSdomSsdnodeVariante.findAll", query = "select e.nodeId,e.ssdNode from VSdomSsdnodeVariante e where e.elName=:elName " +
    " and e.variante=:variante and e.deleted = 'N' order by e.nodeId ")
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class VSdomSsdnodeVariante implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Created By
   */
  @Column(name = "CREATED_BY")
  private String createdBy;

  /**
   * Comments
   */
  @Lob()
  private String comments;

  /**
   * Created Date
   */
  @Temporal(TemporalType.DATE)
  @Column(name = "CREATED_DATE")
  private Date createdDate;

  /**
   * Deleted
   */
  private String deleted;

  /**
   * Deleted By
   */
  @Column(name = "DELETED_BY")
  private String deletedBy;

  /**
   * Deleted Date
   */
  @Temporal(TemporalType.DATE)
  @Column(name = "DELETED_DATE")
  private Date deletedDate;

  /**
   * El Name
   */
  @Column(name = "EL_NAME")
  private String elName;

  /**
   * El Name Original
   */
  @Column(name = "EL_NAME_ORIG")
  private String elNameOrig;

  /**
   * El NUmmber
   */
  @Column(name = "EL_NUMMER")
  private BigDecimal elNummer;

  /**
   * ID
   */
  @SequenceGenerator(name = "seq_ldb2_sdom", sequenceName = "SEQ_LDB2_ALL", allocationSize = 1)
  @GeneratedValue(generator = "seq_ldb2_sdom")
  @Id
  private BigDecimal id;

  /**
   * Class
   */
  private String klasse;

  /**
   * Node ID
   */
  @Column(name = "NODE_ID")
  private BigDecimal nodeId;

  /**
   * SSD Node
   */
  @Column(name = "SSD_NODE")
  private String ssdNode;

  /**
   * Variant
   */
  private String variante;

  /**
   * Constructor
   */
  public VSdomSsdnodeVariante() {
    // constructor
  }


  /**
   * @return the createdBy
   */
  public String getCreatedBy() {
    return this.createdBy;
  }


  /**
   * @param createdBy the createdBy to set
   */
  public void setCreatedBy(final String createdBy) {
    this.createdBy = createdBy;
  }


  /**
   * @return the comments
   */
  public String getComments() {
    return this.comments;
  }


  /**
   * @param comments the comments to set
   */
  public void setComments(final String comments) {
    this.comments = comments;
  }


  /**
   * @return the createdDate
   */
  public Date getCreatedDate() {
    return (Date) this.createdDate.clone();
  }


  /**
   * @param createdDate the createdDate to set
   */
  public void setCreatedDate(final Date createdDate) {
    this.createdDate = (Date) createdDate.clone();
  }


  /**
   * @return the deleted
   */
  public String getDeleted() {
    return this.deleted;
  }


  /**
   * @param deleted the deleted to set
   */
  public void setDeleted(final String deleted) {
    this.deleted = deleted;
  }


  /**
   * @return the deletedBy
   */
  public String getDeletedBy() {
    return this.deletedBy;
  }


  /**
   * @param deletedBy the deletedBy to set
   */
  public void setDeletedBy(final String deletedBy) {
    this.deletedBy = deletedBy;
  }


  /**
   * @return the deletedDate
   */
  public Date getDeletedDate() {
    return (Date) this.deletedDate.clone();
  }


  /**
   * @param deletedDate the deletedDate to set
   */
  public void setDeletedDate(final Date deletedDate) {
    this.deletedDate = (Date) deletedDate.clone();
  }


  /**
   * @return the elName
   */
  public String getElName() {
    return this.elName;
  }


  /**
   * @param elName the elName to set
   */
  public void setElName(final String elName) {
    this.elName = elName;
  }


  /**
   * @return the elNameOrig
   */
  public String getElNameOrig() {
    return this.elNameOrig;
  }


  /**
   * @param elNameOrig the elNameOrig to set
   */
  public void setElNameOrig(final String elNameOrig) {
    this.elNameOrig = elNameOrig;
  }


  /**
   * @return the elNummer
   */
  public BigDecimal getElNummer() {
    return this.elNummer;
  }


  /**
   * @param elNummer the elNummer to set
   */
  public void setElNummer(final BigDecimal elNummer) {
    this.elNummer = elNummer;
  }


  /**
   * @return the id
   */
  public BigDecimal getId() {
    return this.id;
  }


  /**
   * @param id the id to set
   */
  public void setId(final BigDecimal id) {
    this.id = id;
  }


  /**
   * @return the klasse
   */
  public String getKlasse() {
    return this.klasse;
  }


  /**
   * @param klasse the klasse to set
   */
  public void setKlasse(final String klasse) {
    this.klasse = klasse;
  }


  /**
   * @return the nodeId
   */
  public BigDecimal getNodeId() {
    return this.nodeId;
  }


  /**
   * @param nodeId the nodeId to set
   */
  public void setNodeId(final BigDecimal nodeId) {
    this.nodeId = nodeId;
  }


  /**
   * @return the ssdNode
   */
  public String getSsdNode() {
    return this.ssdNode;
  }


  /**
   * @param ssdNode the ssdNode to set
   */
  public void setSsdNode(final String ssdNode) {
    this.ssdNode = ssdNode;
  }


  /**
   * @return the variante
   */
  public String getVariante() {
    return this.variante;
  }


  /**
   * @param variante the variante to set
   */
  public void setVariante(final String variante) {
    this.variante = variante;
  }


  /**
   * @return the serialversionuid
   */
  public static long getSerialversionuid() {
    return serialVersionUID;
  }


}