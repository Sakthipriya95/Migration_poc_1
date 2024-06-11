package com.bosch.caltool.icdm.database.entity.cdr;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.bosch.caltool.icdm.database.entity.apic.TabvApicUser;


/**
 * The persistent class for the T_RVW_USER_CMNT_HISTORY database table.
 */
@Entity
@Table(name = "T_RVW_USER_CMNT_HISTORY")
public class TRvwUserCmntHistory implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "RVW_USER_CMNT_HISTORY_ID")
  private long rvwCmntUserHistoryId;

  // bi-directional many-to-one association to TPidcA2l
  @ManyToOne
  @JoinColumn(name = "RVW_CMNT_USER_ID")
  private TabvApicUser rvwCmntUser;

  @Column(name = "RVW_COMMENT")
  private String rvwComment;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "\"VERSION\"")
  private Long version;

  public TRvwUserCmntHistory() {}


  /**
   * @return the rvwCmntUserHistoryId
   */
  public long getRvwCmntUserHistoryId() {
    return this.rvwCmntUserHistoryId;
  }


  /**
   * @param rvwCmntUserHistoryId the rvwCmntUserHistoryId to set
   */
  public void setRvwCmntUserHistoryId(final long rvwCmntUserHistoryId) {
    this.rvwCmntUserHistoryId = rvwCmntUserHistoryId;
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


  /**
   * @return the rvwCmntUser
   */
  public TabvApicUser getRvwCmntUser() {
    return this.rvwCmntUser;
  }


  /**
   * @param rvwCmntUser the rvwCmntUser to set
   */
  public void setRvwCmntUser(final TabvApicUser rvwCmntUser) {
    this.rvwCmntUser = rvwCmntUser;
  }


  public String getRvwComment() {
    return this.rvwComment;
  }

  public void setRvwComment(final String rvwComment) {
    this.rvwComment = rvwComment;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

}