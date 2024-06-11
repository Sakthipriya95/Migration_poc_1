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


/**
 * ICDM-763 The persistent class for the T_LINKS database table.
 */
@Entity
@Table(name = "T_LINKS")
@NamedQueries(value = {
    @NamedQuery(name = TLink.GET_ALL, query = "SELECT t FROM TLink t "),
    @NamedQuery(name = TLink.NQ_GET_BY_NODE, query = "SELECT link from TLink link where link.nodeId = :nodeId and upper(link.nodeType) = :nodeType"),
    @NamedQuery(name = TLink.NQ_GET_NODES_WITH_LINKS_BY_TYPE, query = "SELECT distinct link.nodeId from TLink link where upper(link.nodeType) = :nodeType"),
    @NamedQuery(name = TLink.NQ_GET_HELP_LINKS, query = "SELECT link from TLink link where link.nodeId = -100") })
public class TLink implements Serializable {

  private static final long serialVersionUID = 1L;
  /**
   * Get all Link
   */
  public static final String GET_ALL = "TLink.findAll";

  /**
   * Get all Link
   */
  public static final String NQ_GET_HELP_LINKS = "TLink.getHelpLinks";

  /**
   * Named query to fetch links by type.
   */
  public static final String NQ_GET_BY_NODE = "TLink.getByNode";

  /**
   * Named query to fetch nodes with links of given type
   */
  public static final String NQ_GET_NODES_WITH_LINKS_BY_TYPE = "TLink.getNodesWithLinksByType";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "LINK_ID", unique = true, nullable = false)
  private long linkId;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 30)
  private String createdUser;

  @Column(name = "DESC_ENG", nullable = false, length = 2000)
  private String descEng;

  @Column(name = "DESC_GER", length = 2000)
  private String descGer;

  @Column(name = "LINK_URL", nullable = false, length = 1000)
  private String linkUrl;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 30)
  private String modifiedUser;

  @Column(name = "NODE_ID", nullable = false)
  private Long nodeId;

  @Column(name = "NODE_TYPE", nullable = false, length = 100)
  private String nodeType;

  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  private Long version;

  // bi-directional many-to-one association to TabvAttrValue
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ATTR_VALUE_ID", nullable = false)
  private TabvAttrValue tabvAttrValue;

  public TLink() {}

  public long getLinkId() {
    return this.linkId;
  }

  public void setLinkId(final long linkId) {
    this.linkId = linkId;
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

  public String getDescEng() {
    return this.descEng;
  }

  public void setDescEng(final String descEng) {
    this.descEng = descEng;
  }

  public String getDescGer() {
    return this.descGer;
  }

  public void setDescGer(final String descGer) {
    this.descGer = descGer;
  }

  public String getLinkUrl() {
    return this.linkUrl;
  }

  public void setLinkUrl(final String linkUrl) {
    this.linkUrl = linkUrl;
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

  public Long getNodeId() {
    return this.nodeId;
  }

  public void setNodeId(final Long nodeId) {
    this.nodeId = nodeId;
  }

  public String getNodeType() {
    return this.nodeType;
  }

  public void setNodeType(final String nodeType) {
    this.nodeType = nodeType;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }


  /**
   * @return the tabvAttrValue
   */
  public TabvAttrValue getTabvAttrValue() {
    return this.tabvAttrValue;
  }


  /**
   * @param tabvAttrValue the tabvAttrValue to set
   */
  public void setTabvAttrValue(final TabvAttrValue tabvAttrValue) {
    this.tabvAttrValue = tabvAttrValue;
  }

}