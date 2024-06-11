package com.bosch.caltool.icdm.database.entity.a2l;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.bosch.caltool.dmframework.entity.IEntity;


/**
 * The persistent class for the T_WP_RESP database table.
 */
@Entity
@Table(name = "T_WP_RESP")
@NamedQuery(name = TWpResp.GET_ALL, query = "SELECT t FROM TWpResp t")
// New named native Query

public class TWpResp implements IEntity, Serializable {

  /**
   * Unique serialization id
   */
  private static final long serialVersionUID = 8704899555499801238L;

  /**
   * Get all A2L Resp
   */
  public static final String GET_ALL = "TWpResp.findAll";

  @Id
  @Column(name = "RESP_ID", unique = true, nullable = false)
  private long respId;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 30)
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 30)
  private String modifiedUser;

  @Column(name = "RESP_NAME", nullable = false, length = 100)
  private String respName;

  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private Long version;

  // bi-directional many-to-one association to TA2lWpResp
  @OneToMany(mappedBy = "TWpResp")
  private List<TA2lWpResp> TA2lWpResps;

  public TWpResp() {
    //
  }

  public long getRespId() {
    return this.respId;
  }

  public void setRespId(final long respId) {
    this.respId = respId;
  }

  @Override
  public Timestamp getCreatedDate() {
    return this.createdDate;
  }

  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
  }

  @Override
  public String getCreatedUser() {
    return this.createdUser;
  }

  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  @Override
  public Timestamp getModifiedDate() {
    return this.modifiedDate;
  }

  public void setModifiedDate(final Timestamp modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  @Override
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  public String getRespName() {
    return this.respName;
  }

  public void setRespName(final String respName) {
    this.respName = respName;
  }

  @Override
  public long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public List<TA2lWpResp> getTA2lWpResps() {
    return this.TA2lWpResps;
  }

  public void setTA2lWpResps(final List<TA2lWpResp> TA2lWpResps) {
    this.TA2lWpResps = TA2lWpResps;
  }

  public TA2lWpResp addTA2lWpResp(final TA2lWpResp TA2lWpResp) {
    getTA2lWpResps().add(TA2lWpResp);
    TA2lWpResp.setTWpResp(this);

    return TA2lWpResp;
  }

  public TA2lWpResp removeTA2lWpResp(final TA2lWpResp TA2lWpResp) {
    getTA2lWpResps().remove(TA2lWpResp);
    TA2lWpResp.setTWpResp(null);

    return TA2lWpResp;
  }

}