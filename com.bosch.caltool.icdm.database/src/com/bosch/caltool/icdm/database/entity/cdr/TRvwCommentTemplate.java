package com.bosch.caltool.icdm.database.entity.cdr;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the T_RVW_COMMENT_TEMPLATES database table.
 */
@Entity
@Table(name = "T_RVW_COMMENT_TEMPLATES")
@NamedQuery(name = TRvwCommentTemplate.GET_ALL, query = "SELECT t FROM TRvwCommentTemplate t")
public class TRvwCommentTemplate implements Serializable {

  private static final long serialVersionUID = 1L;

  /** The Constant GET_ALL. */
  public static final String GET_ALL = "TRvwCommentTemplate.findAll";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CDR_SEQ_GENERATOR")
  @Column(name = "COMMENT_ID")
  private long commentId;

  @Column(name = "COMMENT_DESC")
  private String commentDesc;

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

  public TRvwCommentTemplate() {}

  public long getCommentId() {
    return this.commentId;
  }

  public void setCommentId(final long commentId) {
    this.commentId = commentId;
  }

  public String getCommentDesc() {
    return this.commentDesc;
  }

  public void setCommentDesc(final String commentDesc) {
    this.commentDesc = commentDesc;
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

}
