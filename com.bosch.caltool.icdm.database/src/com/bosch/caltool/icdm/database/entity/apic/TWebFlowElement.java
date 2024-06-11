package com.bosch.caltool.icdm.database.entity.apic;

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
 * The persistent class for the T_WEBFLOW_ELEMENT database table.
 */
@Entity
@Table(name = "T_WEBFLOW_ELEMENT")
@NamedQuery(name = TWebFlowElement.NQ_GET_VAR_BY_ELE_ID, query = "SELECT t FROM TWebFlowElement t where t.elementID = :elementId")
public class TWebFlowElement implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  /**
   *
   */
  public static final String NQ_GET_VAR_BY_ELE_ID = "TWebFlowElement.getVarByEleId";

  private long webFlowId;
  private long elementID;
  private long variantID;
  private String isDeleted;
  private Timestamp createdDate;
  private String createdUser;
  private Timestamp modifiedDate;
  private String modifiedUser;
  private Long version;

  /**
   * @return the webFlowId
   */
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "WEBFLOW_ID", unique = true, nullable = false)
  public long getWebFlowId() {
    return this.webFlowId;
  }


  /**
   * @param webFlowId the webFlowId to set
   */
  public void setWebFlowId(final long webFlowId) {
    this.webFlowId = webFlowId;
  }

  /**
   * @return isDeleted
   */
  @Column(name = "IS_DELETED", nullable = false, length = 1)
  public String getIsDeleted() {
    return this.isDeleted;
  }

  /**
   * @param isDeleted the isDeleted to set
   */
  public void setIsDeleted(final String isDeleted) {
    this.isDeleted = isDeleted;
  }


  /**
   * @return the elementID
   */
  @Column(name = "ELEMENT_ID", nullable = false)
  public long getElementID() {
    return this.elementID;
  }


  /**
   * @param elementID the elementID to set
   */
  public void setElementID(final long elementID) {
    this.elementID = elementID;
  }

  /**
   * @return the variantID
   */
  @Column(name = "VARIANT_ID", nullable = false)
  public long getVariantID() {
    return this.variantID;
  }


  /**
   * @param variantID the variantID to set
   */
  public void setVariantID(final long variantID) {
    this.variantID = variantID;
  }


  /**
   * @return the createdDate
   */
  @Column(name = "CREATED_DATE", nullable = false)
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
  @Column(name = "CREATED_USER", nullable = false, length = 100)
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
  @Column(name = "MODIFIED_DATE")
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
  @Column(name = "MODIFIED_USER", length = 100)
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
  @Column(name = "\"VERSION\"", nullable = false)
  @Version
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