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

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;

import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;


/**
 * ICdm-1214 -creation of a new Table for storing the Review Attr Values The persistent class for the T_RVW_ATTR_VALUES
 * database table.
 */
@Entity
@Table(name = "T_RVW_ATTR_VALUES")
public class TRvwAttrValue implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CDR_SEQ_GENERATOR")
  @Column(name = "RVW_ATTRVAL_ID")
  private long rvwAttrvalId;

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

  @Column(nullable = true, length = 1)
  private String used;

  // bi-directional many-to-one association to TabvAttrValue batch fetch for tabv attr Value
  @ManyToOne
  @JoinColumn(name = "VALUE_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TabvAttrValue tabvAttrValue;

  // bi-directional many-to-one association to TabvAttributes batch fetch for tabv attr
  @ManyToOne
  @JoinColumn(name = "ATTR_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TabvAttribute tabvAttribute;

  // bi-directional many-to-one association to TRvwResult
  @ManyToOne
  @JoinColumn(name = "RESULT_ID")
  private TRvwResult TRvwResult;

  public TRvwAttrValue() {}

  public long getRvwAttrvalId() {
    return this.rvwAttrvalId;
  }

  public void setRvwAttrvalId(final long rvwAttrvalId) {
    this.rvwAttrvalId = rvwAttrvalId;
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

  public TabvAttrValue getTabvAttrValue() {
    return this.tabvAttrValue;
  }

  public void setTabvAttrValue(final TabvAttrValue tabvAttrValue) {
    this.tabvAttrValue = tabvAttrValue;
  }

  public TRvwResult getTRvwResult() {
    return this.TRvwResult;
  }

  public void setTRvwResult(final TRvwResult TRvwResult) {
    this.TRvwResult = TRvwResult;
  }

  /**
   * @return the tabvAttribute
   */
  public TabvAttribute getTabvAttribute() {
    return this.tabvAttribute;
  }

  /**
   * @param tabvAttribute the tabvAttribute to set
   */
  public void setTabvAttribute(final TabvAttribute tabvAttribute) {
    this.tabvAttribute = tabvAttribute;
  }

  /**
   * @return Y if used else N if not used
   */
  public String getUsed() {
    return this.used;
  }

  /**
   * @param used
   */
  public void setUsed(final String used) {
    this.used = used;
  }

}