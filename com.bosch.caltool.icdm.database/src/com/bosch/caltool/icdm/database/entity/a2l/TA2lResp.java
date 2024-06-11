package com.bosch.caltool.icdm.database.entity.a2l;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.bosch.caltool.icdm.database.entity.apic.TPidcA2l;


/**
 * The persistent class for the T_A2L_RESP database table.
 */
@Entity
@Table(name = "T_A2L_RESP")
@NamedQueries(value = {
    @NamedQuery(name = TA2lResp.GET_ALL, query = "SELECT t FROM TA2lResp t"),
    @NamedQuery(name = TA2lResp.FIND_QUERY_WITH_ROOT, query = "Select t FROM TA2lResp t where t.TPidcA2l.pidcA2lId = :pidcA2lId and t.wpTypeId = :wpTypeId  and t.wpRootId= :wpRootId"),
    @NamedQuery(name = TA2lResp.FIND_QUERY_WITHOUT_ROOT, query = "Select t FROM TA2lResp t where t.TPidcA2l.pidcA2lId = :pidcA2lId and t.wpTypeId = :wpTypeId") })
// New named native Query
@NamedNativeQuery(name = TA2lResp.INSERT_A2L_RESP, query = "Insert into T_A2L_RESP(PIDC_A2L_ID,WP_TYPE_ID,WP_ROOT_ID,CREATED_USER) values(?,?,?,?")

public class TA2lResp implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Find query with Root id
   */
  public static final String FIND_QUERY_WITH_ROOT = "TA2lResp.findWithRoot";

  /**
   * Find query without root id
   */
  public static final String FIND_QUERY_WITHOUT_ROOT = "TA2lResp.findWithoutRoot";

  /**
   * INSERTING A2L RESP
   */
  public static final String INSERT_A2L_RESP = "TA2LResp.insertwithoutVar";
  /**
   * Get all A2L Resp
   */
  public static final String GET_ALL = "TA2lResp.findAll";
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "A2L_RESP_ID", unique = true, nullable = false)
  private long a2lRespId;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 30)
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 30)
  private String modifiedUser;

  @Column(name = "RESP_VAR_ID")
  private Long respVarId;

  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private Long version;

  @Column(name = "WP_ROOT_ID")
  private Long wpRootId;

  @Column(name = "WP_TYPE_ID", nullable = false)
  private Long wpTypeId;

  // bi-directional many-to-one association to TPidcA2l
  @ManyToOne
  @JoinColumn(name = "PIDC_A2L_ID", nullable = false)
  private TPidcA2l TPidcA2l;

  // bi-directional many-to-one association to TA2lWpResp
  @OneToMany(mappedBy = "TA2lResp")
  private List<TA2lWpResp> TA2lWpResps;

  public TA2lResp() {}

  public long getA2lRespId() {
    return this.a2lRespId;
  }

  public void setA2lRespId(final long a2lRespId) {
    this.a2lRespId = a2lRespId;
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

  public Long getRespVarId() {
    return this.respVarId;
  }

  public void setRespVarId(final Long respVarId) {
    this.respVarId = respVarId;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public Long getWpRootId() {
    return this.wpRootId;
  }

  public void setWpRootId(final Long wpRootId) {
    this.wpRootId = wpRootId;
  }

  public Long getWpTypeId() {
    return this.wpTypeId;
  }

  public void setWpTypeId(final Long wpTypeId) {
    this.wpTypeId = wpTypeId;
  }

  public TPidcA2l getTPidcA2l() {
    return this.TPidcA2l;
  }

  public void setTPidcA2l(final TPidcA2l TPidcA2l) {
    this.TPidcA2l = TPidcA2l;
  }

  public List<TA2lWpResp> getTA2lWpResps() {
    return this.TA2lWpResps;
  }

  public void setTA2lWpResps(final List<TA2lWpResp> TA2lWpResps) {
    this.TA2lWpResps = TA2lWpResps;
  }

  public TA2lWpResp addTA2lWpResp(final TA2lWpResp TA2lWpResp) {
    getTA2lWpResps().add(TA2lWpResp);
    TA2lWpResp.setTA2lResp(this);

    return TA2lWpResp;
  }

  public TA2lWpResp removeTA2lWpResp(final TA2lWpResp TA2lWpResp) {
    getTA2lWpResps().remove(TA2lWpResp);
    TA2lWpResp.setTA2lResp(null);

    return TA2lWpResp;
  }

}