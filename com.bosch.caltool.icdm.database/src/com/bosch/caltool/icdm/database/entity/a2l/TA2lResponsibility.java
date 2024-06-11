package com.bosch.caltool.icdm.database.entity.a2l;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;

import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireRespVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicUser;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectidcard;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwWpResp;
import com.bosch.caltool.icdm.database.entity.cdr.cdfx.TCDFxDelvryWpResp;


/**
 * The persistent class for the T_A2L_RESPONSIBILITY database table.
 */
@Entity
@Table(name = "T_A2L_RESPONSIBILITY")
@NamedQuery(name = "TA2lResponsibility.findAll", query = "SELECT t FROM TA2lResponsibility t")
public class TA2lResponsibility implements Serializable, com.bosch.caltool.dmframework.entity.IEntity {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "A2L_RESP_ID")
  private long a2lRespId;

  @Column(name = "ALIAS_NAME")
  private String aliasName;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "L_DEPARTMENT")
  private String lDepartment;

  @Column(name = "L_FIRST_NAME")
  private String lFirstName;

  @Column(name = "L_LAST_NAME")
  private String lLastName;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  @Column(name = "DELETED_FLAG")
  private String deletedFlag;

  @ManyToOne
  @JoinColumn(name = "PROJECT_ID")
  private TabvProjectidcard tabvProjectidcard;

  @Column(name = "RESP_TYPE")
  private String respType;

  @ManyToOne
  @JoinColumn(name = "USER_ID")
  private TabvApicUser tabvApicUser;

  @Version
  @Column(name = "\"VERSION\"")
  private long version;

  @OneToMany(mappedBy = "a2lResponsibility", fetch = FetchType.LAZY)
  private List<TA2lWpResponsibility> wpRespPalList;

  @OneToMany(mappedBy = "tA2lResponsibility", fetch = FetchType.LAZY)
  private List<TA2lWpParamMapping> wpParamMappingList;

  // bi-directional many-to-one association to TRvwWpResp
  @OneToMany(mappedBy = "tA2lResponsibility", fetch = FetchType.LAZY)
  private Set<TRvwWpResp> tRvwWpResps;

  // bi-directional many-to-one association to TCDFxDelWpResp
  @OneToMany(mappedBy = "resp", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private List<TCDFxDelvryWpResp> tCDFxDelWpRespList;

  // bi-directional many-to-one association to TRvwQnaireRespVariant
  @OneToMany(mappedBy = "tA2lResponsibility", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private List<TRvwQnaireRespVariant> tRvwQnaireRespVariant;

  @OneToMany(mappedBy = "tA2lResponsibility", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private List<TA2lResponsiblityBshgrpUsr> ta2lResponsiblityBshgrpUsrList;

  // bi-directional one-to-many association to TA2lWpResponsibilityStatus
  @OneToMany(mappedBy = "tA2lResp", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private List<TA2lWpResponsibilityStatus> tA2lWPRespStatus;

  public TA2lResponsibility() {
    //
  }


  /**
   * @param ta2lResponsiblityBshgrpUsr
   * @return modified TA2lResponsiblityBshgrpUsr
   */
  public TA2lResponsiblityBshgrpUsr addBoschGrpUser(final TA2lResponsiblityBshgrpUsr ta2lResponsiblityBshgrpUsr) {
    if (getTa2lResponsiblityBshgrpUsrList() == null) {
      setTa2lResponsiblityBshgrpUsrList(new ArrayList<>());
    }
    getTa2lResponsiblityBshgrpUsrList().add(ta2lResponsiblityBshgrpUsr);
    ta2lResponsiblityBshgrpUsr.setTA2lResponsibility(this);
    return ta2lResponsiblityBshgrpUsr;
  }

  /**
   * @param ta2lResponsiblityBshgrpUsr
   * @return modified TA2lResponsiblityBshgrpUsr
   */
  public TA2lResponsiblityBshgrpUsr removeBoschGrpUser(final TA2lResponsiblityBshgrpUsr ta2lResponsiblityBshgrpUsr) {
    if (getTa2lResponsiblityBshgrpUsrList() != null) {
      getTa2lResponsiblityBshgrpUsrList().remove(ta2lResponsiblityBshgrpUsr);
    }
    return ta2lResponsiblityBshgrpUsr;
  }


  /**
   * @return the ta2lResponsiblityBshgrpUsrList
   */
  public List<TA2lResponsiblityBshgrpUsr> getTa2lResponsiblityBshgrpUsrList() {
    return this.ta2lResponsiblityBshgrpUsrList;
  }


  /**
   * @param ta2lResponsiblityBshgrpUsrList the ta2lResponsiblityBshgrpUsrList to set
   */
  public void setTa2lResponsiblityBshgrpUsrList(final List<TA2lResponsiblityBshgrpUsr> ta2lResponsiblityBshgrpUsrList) {
    this.ta2lResponsiblityBshgrpUsrList = ta2lResponsiblityBshgrpUsrList;
  }


  /**
   * @return the wpRespPalList
   */
  public List<TA2lWpResponsibility> getWpRespPalList() {
    return this.wpRespPalList;
  }


  /**
   * @param wpRespPalList the wpRespPalList to set
   */
  public void setWpRespPalList(final List<TA2lWpResponsibility> wpRespPalList) {
    this.wpRespPalList = wpRespPalList;
  }


  public long getA2lRespId() {
    return this.a2lRespId;
  }

  public void setA2lRespId(final long a2lRespId) {
    this.a2lRespId = a2lRespId;
  }

  public String getAliasName() {
    return this.aliasName;
  }

  public void setAliasName(final String aliasName) {
    this.aliasName = aliasName;
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

  public String getLDepartment() {
    return this.lDepartment;
  }

  public void setLDepartment(final String department) {
    this.lDepartment = department;
  }

  public String getLFirstName() {
    return this.lFirstName;
  }

  public void setLFirstName(final String firstName) {
    this.lFirstName = firstName;
  }

  public String getLLastName() {
    return this.lLastName;
  }

  public void setLLastName(final String lastName) {
    this.lLastName = lastName;
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
   * @return the tabvProjectidcard
   */
  public TabvProjectidcard getTabvProjectidcard() {
    return this.tabvProjectidcard;
  }


  /**
   * @param tabvProjectidcard the pidc to set
   */
  public void setTabvProjectidcard(final TabvProjectidcard tabvProjectidcard) {
    this.tabvProjectidcard = tabvProjectidcard;
  }

  /**
   * @return the respTyp
   */
  public String getRespType() {
    return this.respType;
  }


  /**
   * @param respTyp the respTyp to set
   */
  public void setRespType(final String respTyp) {
    this.respType = respTyp;
  }


  /**
   * @return the tA2lWPRespStatus
   */
  public List<TA2lWpResponsibilityStatus> gettA2lWPRespStatus() {
    return this.tA2lWPRespStatus;
  }


  /**
   * @param tA2lWPRespStatus the tA2lWPRespStatus to set
   */
  public void settA2lWPRespStatus(final List<TA2lWpResponsibilityStatus> tA2lWPRespStatus) {
    this.tA2lWPRespStatus = tA2lWPRespStatus;
  }


  /**
   * @return the tabvApicUser
   */
  public TabvApicUser getTabvApicUser() {
    return this.tabvApicUser;
  }


  /**
   * @param tabvApicUser the tabvApicUser to set
   */
  public void setTabvApicUser(final TabvApicUser tabvApicUser) {
    this.tabvApicUser = tabvApicUser;
  }


  /**
   * @return the version
   */
  public long getVersion() {
    return this.version;
  }


  /**
   * @param version the version to set
   */
  public void setVersion(final long version) {
    this.version = version;
  }


  /**
   * @return the wpParamMappingList
   */
  public List<TA2lWpParamMapping> getWpParamMappingList() {
    return this.wpParamMappingList;
  }


  /**
   * @param wpParamMappingList the wpParamMappingList to set
   */
  public void setWpParamMappingList(final List<TA2lWpParamMapping> wpParamMappingList) {
    this.wpParamMappingList = wpParamMappingList;
  }


  /**
   * @return the tRvwWpResps
   */
  public Set<TRvwWpResp> gettRvwWpResps() {
    return this.tRvwWpResps;
  }


  /**
   * @param tRvwWpResps the tRvwWpResps to set
   */
  public void settRvwWpResps(final Set<TRvwWpResp> tRvwWpResps) {
    this.tRvwWpResps = tRvwWpResps;
  }


  /**
   * @return the deletedFlag
   */
  public String getDeletedFlag() {
    return this.deletedFlag;
  }


  /**
   * @param deletedFlag the deletedFlag to set
   */
  public void setDeletedFlag(final String deletedFlag) {
    this.deletedFlag = deletedFlag;
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
    tRvwQnaireRespVariants.settA2lResponsibility(this);

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