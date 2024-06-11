package com.bosch.caltool.icdm.database.entity.a2l;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.bosch.caltool.icdm.database.entity.apic.TWorkpackage;


/**
 * The persistent class for the T_A2L_WP_RESP database table.
 */
@Entity
@Table(name = "T_A2L_WP_RESP")
@NamedQueries(value = {
    @NamedQuery(name = "TA2lWpResp.findByA2LRespId", query = "SELECT t FROM TA2lWpResp t where t.TA2lResp.a2lRespId = :a2lRespId") })
public class TA2lWpResp implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * find query to get
   */
  public static final String GET_A2LWPRESP = "TA2lWpResp.findByA2LRespId";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "A2L_WP_RESP_ID", unique = true, nullable = false)
  private long a2lWpRespId;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 30)
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 30)
  private String modifiedUser;

  @Column(name = "\"VERSION\"", nullable = false)
  private Long version;

  // bi-directional many-to-one association to TA2lGroup
  @ManyToOne
  @JoinColumn(name = "A2L_GROUP_ID")
  private TA2lGroup TA2lGroup;

  // bi-directional many-to-one association to TA2lResp
  @ManyToOne
  @JoinColumn(name = "A2L_RESP_ID", nullable = false)
  private TA2lResp TA2lResp;

  // bi-directional many-to-one association to TWorkpackage
  @ManyToOne
  @JoinColumn(name = "WP_ID")
  private TWorkpackage TWorkpackage;

  // bi-directional many-to-one association to TWpResp
  @ManyToOne
  @JoinColumn(name = "RESP_ID", nullable = false)
  private TWpResp TWpResp;

  public TA2lWpResp() {}

  public long getA2lWpRespId() {
    return this.a2lWpRespId;
  }

  public void setA2lWpRespId(final long a2lWpRespId) {
    this.a2lWpRespId = a2lWpRespId;
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

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public TA2lGroup getTA2lGroup() {
    return this.TA2lGroup;
  }

  public void setTA2lGroup(final TA2lGroup TA2lGroup) {
    this.TA2lGroup = TA2lGroup;
  }

  public TA2lResp getTA2lResp() {
    return this.TA2lResp;
  }

  public void setTA2lResp(final TA2lResp TA2lResp) {
    this.TA2lResp = TA2lResp;
  }

  public TWorkpackage getTWorkpackage() {
    return this.TWorkpackage;
  }

  public void setTWorkpackage(final TWorkpackage TWorkpackage) {
    this.TWorkpackage = TWorkpackage;
  }

  public TWpResp getTWpResp() {
    return this.TWpResp;
  }

  public void setTWpResp(final TWpResp TWpResp) {
    this.TWpResp = TWpResp;
  }

}