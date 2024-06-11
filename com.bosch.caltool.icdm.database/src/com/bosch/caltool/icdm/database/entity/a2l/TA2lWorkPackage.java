package com.bosch.caltool.icdm.database.entity.a2l;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;

import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireRespVariant;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwWpResp;
import com.bosch.caltool.icdm.database.entity.cdr.cdfx.TCDFxDelvryWpResp;


/**
 * The persistent class for the T_A2L_WORK_PACKAGES database table.
 */
@Entity
@Table(name = "T_A2L_WORK_PACKAGES")
public class TA2lWorkPackage implements Serializable, com.bosch.caltool.dmframework.entity.IEntity {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "A2L_WP_ID")
  private long a2lWpId;

  @Column(name = "WP_DESC")
  private String wpDesc;

  @Column(name = "WP_NAME")
  private String wpName;

  @Column(name = "WP_NAME_CUST")
  private String wpNameCust;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PIDC_VERS_ID")
  private TPidcVersion pidcVersion;

  @Column(name = "\"VERSION\"")
  @Version
  private long version;

  // bi-directional many-to-one association to TA2lWorkPackage
  @OneToMany(mappedBy = "a2lWp", fetch = FetchType.LAZY)
  private List<TA2lWpResponsibility> a2lWpRespPalList;


  // bi-directional many-to-one association to TRvwWpResp
  @OneToMany(mappedBy = "tA2lWorkPackage", fetch = FetchType.LAZY)
  private List<TRvwWpResp> tRvwWpResps;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PARENT_A2L_WP_ID")
  private TA2lWorkPackage parentTA2lWorkPackage;

  // TODO change this to one-to-many
  @OneToOne(mappedBy = "parentTA2lWorkPackage", fetch = FetchType.LAZY)
  private TA2lWorkPackage childTA2lWorkPackage;

  // bi-directional many-to-one association to TCDFxDelWpResp
  @OneToMany(mappedBy = "wp", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private List<TCDFxDelvryWpResp> tCDFxDelWpRespList;

  // bi-directional many-to-one association to TRvwQnaireRespVariant
  @OneToMany(mappedBy = "tA2lWorkPackage", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private List<TRvwQnaireRespVariant> tRvwQnaireRespVariant;

  public TA2lWorkPackage() {
    // Default Constructor
  }

  public long getA2lWpId() {
    return this.a2lWpId;
  }

  public void setA2lWpId(final long a2lWpId) {
    this.a2lWpId = a2lWpId;
  }


  public String getCreatedUser() {
    return this.createdUser;
  }

  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  public String getModifiedUser() {
    return this.modifiedUser;
  }

  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }


  public String getWpDesc() {
    return this.wpDesc;
  }

  public void setWpDesc(final String wpDesc) {
    this.wpDesc = wpDesc;
  }


  /**
   * @return the pidcVersion
   */
  public TPidcVersion getPidcVersion() {
    return this.pidcVersion;
  }


  /**
   * @param pidcVersion the pidcVersion to set
   */
  public void setPidcVersion(final TPidcVersion pidcVersion) {
    this.pidcVersion = pidcVersion;
  }


  /**
   * @param createdDate the createdDate to set
   */
  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
  }


  /**
   * @param modifiedDate the modifiedDate to set
   */
  public void setModifiedDate(final Timestamp modifiedDate) {
    this.modifiedDate = modifiedDate;
  }


  /**
   * @param version the version to set
   */
  public void setVersion(final long version) {
    this.version = version;
  }

  public String getWpName() {
    return this.wpName;
  }

  public void setWpName(final String wpName) {
    this.wpName = wpName;
  }

  public String getWpNameCust() {
    return this.wpNameCust;
  }

  public void setWpNameCust(final String wpNameCust) {
    this.wpNameCust = wpNameCust;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Timestamp getCreatedDate() {
    return this.createdDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Timestamp getModifiedDate() {
    return this.modifiedDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long getVersion() {
    return this.version;
  }


  /**
   * @return the a2lWpRespPalList
   */
  public List<TA2lWpResponsibility> getA2lWpRespPalList() {
    return this.a2lWpRespPalList;
  }


  /**
   * @param a2lWpRespPalList the a2lWpRespPalList to set
   */
  public void setA2lWpRespPalList(final List<TA2lWpResponsibility> a2lWpRespPalList) {
    this.a2lWpRespPalList = a2lWpRespPalList;
  }


  /**
   * @return the tRvwWpResps
   */
  public List<TRvwWpResp> gettRvwWpResps() {
    return this.tRvwWpResps;
  }


  /**
   * @param tRvwWpResps the tRvwWpResps to set
   */
  public void settRvwWpResps(final List<TRvwWpResp> tRvwWpResps) {
    this.tRvwWpResps = tRvwWpResps;
  }


  /**
   * @return the parentTA2lWorkPackage
   */
  public TA2lWorkPackage getParentTA2lWorkPackage() {
    return this.parentTA2lWorkPackage;
  }


  /**
   * @param parentTA2lWorkPackage the parentTA2lWorkPackage to set
   */
  public void setParentTA2lWorkPackage(final TA2lWorkPackage parentTA2lWorkPackage) {
    this.parentTA2lWorkPackage = parentTA2lWorkPackage;
  }


  /**
   * @return the childTA2lWorkPackage
   */
  public TA2lWorkPackage getChildTA2lWorkPackage() {
    return this.childTA2lWorkPackage;
  }


  /**
   * @param childTA2lWorkPackage the childTA2lWorkPackage to set
   */
  public void setChildTA2lWorkPackage(final TA2lWorkPackage childTA2lWorkPackage) {
    this.childTA2lWorkPackage = childTA2lWorkPackage;
  }


  /**
   * @return the tCDFxDelWpRespList
   */
  public List<TCDFxDelvryWpResp> gettCDFxDelWpRespList() {
    return this.tCDFxDelWpRespList;
  }


  /**
   * @param tCDFxDelWpRespList the tCDFxDelWpRespList to set
   */
  public void settCDFxDelWpRespList(final List<TCDFxDelvryWpResp> tCDFxDelWpRespList) {
    this.tCDFxDelWpRespList = tCDFxDelWpRespList;
  }


  /**
   * @return the tRvwQnaireRespVariant
   */
  public List<TRvwQnaireRespVariant> gettRvwQnaireRespVariant() {
    return this.tRvwQnaireRespVariant;
  }


  /**
   * @param tRvwQnaireRespVariant the tRvwQnaireRespVariant to set
   */
  public void settRvwQnaireRespVariant(final List<TRvwQnaireRespVariant> tRvwQnaireRespVariant) {
    this.tRvwQnaireRespVariant = tRvwQnaireRespVariant;
  }

  /**
   * @param tRvwQnaireRespVariants as input
   * @return tRvwQnaireRespVariants
   */
  public TRvwQnaireRespVariant addTRvwQnaireRespVariants(final TRvwQnaireRespVariant tRvwQnaireRespVariants) {
    if (gettRvwQnaireRespVariant() == null) {
      settRvwQnaireRespVariant(new ArrayList<>());
    }
    gettRvwQnaireRespVariant().add(tRvwQnaireRespVariants);
    tRvwQnaireRespVariants.settA2lWorkPackage(this);

    return tRvwQnaireRespVariants;
  }

  /**
   * @param tRvwQnaireRespVariants as input
   * @return tRvwQnaireRespVariants
   */
  public TRvwQnaireRespVariant removeTRvwQnaireRespVariants(final TRvwQnaireRespVariant tRvwQnaireRespVariants) {
    gettRvwQnaireRespVariant().remove(tRvwQnaireRespVariants);
    return tRvwQnaireRespVariants;
  }

}