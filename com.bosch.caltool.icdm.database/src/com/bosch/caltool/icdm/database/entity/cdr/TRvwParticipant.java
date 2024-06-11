package com.bosch.caltool.icdm.database.entity.cdr;

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
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.annotations.OptimisticLocking;

import com.bosch.caltool.icdm.database.entity.apic.TabvApicUser;

/**
 * The persistent class for the T_RVW_PARTICIPANTS database table.
 */
@Entity
@OptimisticLocking(cascade = true)
@Table(name = "T_RVW_PARTICIPANTS")
public class TRvwParticipant implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CDR_SEQ_GENERATOR")
  @Column(name = "PARTICIPANT_ID")
  private long participantId;

  @Column(name = "ACTIVITY_TYPE")
  private String activityType;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "EDIT_FLAG")
  private String editFlag;

  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  private Long version;

  // bi-directional many-to-one association to TRvwResult
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "RESULT_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TRvwResult TRvwResult;

  // bi-directional many-to-one association to TabvApicUser
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "USER_ID")
  private TabvApicUser tabvApicUser;

  public TRvwParticipant() {}

  public long getParticipantId() {
    return this.participantId;
  }

  public void setParticipantId(final long participantId) {
    this.participantId = participantId;
  }

  public String getActivityType() {
    return this.activityType;
  }

  public void setActivityType(final String activityType) {
    this.activityType = activityType;
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

  public TRvwResult getTRvwResult() {
    return this.TRvwResult;
  }

  public void setTRvwResult(final TRvwResult TRvwResult) {
    this.TRvwResult = TRvwResult;
  }

  public TabvApicUser getTabvApicUser() {
    return this.tabvApicUser;
  }

  public void setTabvApicUser(final TabvApicUser tabvApicUser) {
    this.tabvApicUser = tabvApicUser;
  }


  /**
   * @return the editFlag
   */
  public String getEditFlag() {
    return this.editFlag;
  }


  /**
   * @param editFlag the editFlag to set
   */
  public void setEditFlag(final String editFlag) {
    this.editFlag = editFlag;
  }

}