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

import org.eclipse.persistence.annotations.OptimisticLocking;


/**
 * The persistent class for the T_PIDC_RM_PROJECT_CHARACTER database table.
 */
@OptimisticLocking(cascade = true)
@Entity
@Table(name = "T_PIDC_RM_PROJECT_CHARACTER")
@NamedQueries({
    @NamedQuery(name = TPidcRmProjectCharacter.NQ_GET_PID_CHAR_RM_ID, query = "SELECT t FROM TPidcRmProjectCharacter t where  t.TPidcRmDefinition.pidcRmId =:pidrmId") })
public class TPidcRmProjectCharacter implements Serializable {

  private static final long serialVersionUID = 1L;

  public static final String NQ_GET_PID_CHAR_RM_ID = "Get pidc proj char";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "PIDC_PC_ID", unique = true, nullable = false)
  private long pidcPcId;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "RELEVANT_FLAG")
  private String relevantFlag;

  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private long version;

  // bi-directional many-to-one association to TPidcRmDefinition
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PIDC_RM_ID")
  private TPidcRmDefinition TPidcRmDefinition;

  // bi-directional many-to-one association to TRmCategory
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "RB_SHARE")
  private TRmCategory rbShare;

  // bi-directional many-to-one association to TRmCategory
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "RB_DATA")
  private TRmCategory rbData;

  // bi-directional many-to-one association to TRmProjectCharacter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PRJ_CHARACTER_ID")
  private TRmProjectCharacter TRmProjectCharacter;

  public TPidcRmProjectCharacter() {}

  public long getPidcPcId() {
    return this.pidcPcId;
  }

  public void setPidcPcId(final long pidcPcId) {
    this.pidcPcId = pidcPcId;
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

  public String getRelevantFlag() {
    return this.relevantFlag;
  }

  public void setRelevantFlag(final String relevantFlag) {
    this.relevantFlag = relevantFlag;
  }

  public long getVersion() {
    return this.version;
  }

  public void setVersion(final long version) {
    this.version = version;
  }

  public TPidcRmDefinition getTPidcRmDefinition() {
    return this.TPidcRmDefinition;
  }

  public void setTPidcRmDefinition(final TPidcRmDefinition TPidcRmDefinition) {
    this.TPidcRmDefinition = TPidcRmDefinition;
  }


  public TRmProjectCharacter getTRmProjectCharacter() {
    return this.TRmProjectCharacter;
  }

  public void setTRmProjectCharacter(final TRmProjectCharacter TRmProjectCharacter) {
    this.TRmProjectCharacter = TRmProjectCharacter;
  }


  /**
   * @return the rbShare
   */
  public TRmCategory getRbShare() {
    return this.rbShare;
  }


  /**
   * @param rbShare the rbShare to set
   */
  public void setRbShare(final TRmCategory rbShare) {
    this.rbShare = rbShare;
  }


  /**
   * @return the rbData
   */
  public TRmCategory getRbData() {
    return this.rbData;
  }


  /**
   * @param rbData the rbData to set
   */
  public void setRbData(final TRmCategory rbData) {
    this.rbData = rbData;
  }

}