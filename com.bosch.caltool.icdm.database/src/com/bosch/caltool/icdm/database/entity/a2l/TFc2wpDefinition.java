package com.bosch.caltool.icdm.database.entity.a2l;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.bosch.caltool.dmframework.entity.IEntity;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;


/**
 * The persistent class for the T_FC2WP_DEFINITION database table.
 */
@Entity
@Table(name = "T_FC2WP_DEFINITION")
@NamedQueries(value = {
    @NamedQuery(name = TFc2wpDefinition.NQ_FIND_ALL, query = "SELECT t FROM TFc2wpDefinition t"),
    @NamedQuery(name = TFc2wpDefinition.NQ_GET_DEF_BY_VALUE_ID, query = "                                           " +
        "SELECT t FROM TFc2wpDefinition t " +
        "where t.fc2wpName=:nameValueId and t.tabvAttrValueDiv.valueId=:qnaireConfigValueId"),
    @NamedQuery(name = TFc2wpDefinition.NQ_FIND_QNAIRE_FC2WPDEF_BY_DIV_ID, query = "SELECT t FROM TFc2wpDefinition t " +
        "where t.tabvAttrValueDiv.valueId=:divValueId and t.relvForQnaireFlag = 'Y'"), })
public class TFc2wpDefinition implements IEntity, Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Named Query to find all FC2WP Definitions
   */
  public static final String NQ_FIND_ALL = "TFc2wpDefinition.findAll";

  /**
   * Named query to get FC2WP definition for the given name Value ID and Division Value ID
   */
  public static final String NQ_GET_DEF_BY_VALUE_ID = "TFc2wpDefinition.getDefByValueID";

  /**
   * Named query to get FC2WP definition for the given Division Value ID and relevancy for questionnaire
   */
  public static final String NQ_FIND_QNAIRE_FC2WPDEF_BY_DIV_ID = "TFc2wpDefinition.getQWPByValueID";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "FCWP_DEF_ID", unique = true, nullable = false)
  private long fcwpDefId;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 30)
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 30)
  private String modifiedUser;

  @Column(name = "RELV_FOR_QNAIRE_FLAG", length = 1)
  private String relvForQnaireFlag;

  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private long version;

  /*
   * // bi-directional many-to-one association to TabvAttrValue
   * @ManyToOne(fetch = FetchType.LAZY)
   * @JoinColumn(name = "NAME_VALUE_ID", nullable = false) private TabvAttrValue tabvAttrValueName;
   */
  // bi-directional many-to-one association to TabvAttrValue
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "DIV_VALUE_ID", nullable = false)
  private TabvAttrValue tabvAttrValueDiv;

  // bi-directional many-to-one association to TFc2wpDefVersion
  @OneToMany(mappedBy = "TFc2wpDefinition", fetch = FetchType.LAZY)
  private Set<TFc2wpDefVersion> TFc2wpDefVersions;

  // bi-directional many-to-one association to TFc2wpPtTypeRelv
  @OneToMany(mappedBy = "TFc2wpDefinition", fetch = FetchType.LAZY)
  private Set<TFc2wpPtTypeRelv> TFc2wpPtTypeRelvs;

  @Column(name = "DESC_ENG")
  private String fc2wpDescEng;

  @Column(name = "DESC_GER")
  private String fc2wpDescGer;

  @Column(name = "FC2WP_NAME")
  private String fc2wpName;


  public TFc2wpDefinition() {}

  public long getFcwpDefId() {
    return this.fcwpDefId;
  }

  public void setFcwpDefId(final long fcwpDefId) {
    this.fcwpDefId = fcwpDefId;
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

  public String getRelvForQnaireFlag() {
    return this.relvForQnaireFlag;
  }

  public void setRelvForQnaireFlag(final String relvForQnaireFlag) {
    this.relvForQnaireFlag = relvForQnaireFlag;
  }

  @Override
  public long getVersion() {
    return this.version;
  }

  public void setVersion(final long version) {
    this.version = version;
  }

  /*
   * public TabvAttrValue getTabvAttrValueName() { return this.tabvAttrValueName; } public void
   * setTabvAttrValueName(final TabvAttrValue tabvAttrValue) { this.tabvAttrValueName = tabvAttrValue; }
   */

  public TabvAttrValue getTabvAttrValueDiv() {
    return this.tabvAttrValueDiv;
  }

  public void setTabvAttrValueDiv(final TabvAttrValue tabvAttrValue) {
    this.tabvAttrValueDiv = tabvAttrValue;
  }

  public Set<TFc2wpDefVersion> getTFc2wpDefVersions() {
    return this.TFc2wpDefVersions;
  }

  public void setTFc2wpDefVersions(final Set<TFc2wpDefVersion> TFc2wpDefVersions) {
    this.TFc2wpDefVersions = TFc2wpDefVersions;
  }

  public TFc2wpDefVersion addTFc2wpDefVersion(final TFc2wpDefVersion TFc2wpDefVersion) {

    if (this.TFc2wpDefVersions == null) {
      this.TFc2wpDefVersions = new HashSet<>();
    }
    this.TFc2wpDefVersions.add(TFc2wpDefVersion);
    TFc2wpDefVersion.setTFc2wpDefinition(this);

    return TFc2wpDefVersion;
  }

  public TFc2wpDefVersion removeTFc2wpDefVersion(final TFc2wpDefVersion TFc2wpDefVersion) {
    if (this.TFc2wpDefVersions == null) {
      this.TFc2wpDefVersions = new HashSet<>();
    }
    this.TFc2wpDefVersions.remove(TFc2wpDefVersion);
    TFc2wpDefVersion.setTFc2wpDefinition(null);

    return TFc2wpDefVersion;
  }

  public Set<TFc2wpPtTypeRelv> getTFc2wpPtTypeRelvs() {
    return this.TFc2wpPtTypeRelvs;
  }

  public void setTFc2wpPtTypeRelvs(final Set<TFc2wpPtTypeRelv> TFc2wpPtTypeRelvs) {
    this.TFc2wpPtTypeRelvs = TFc2wpPtTypeRelvs;
  }

  public TFc2wpPtTypeRelv addTFc2wpPtTypeRelv(final TFc2wpPtTypeRelv TFc2wpPtTypeRelv) {
    if (this.TFc2wpPtTypeRelvs == null) {
      this.TFc2wpPtTypeRelvs = new HashSet<>();
    }
    getTFc2wpPtTypeRelvs().add(TFc2wpPtTypeRelv);
    TFc2wpPtTypeRelv.setTFc2wpDefinition(this);
    return TFc2wpPtTypeRelv;
  }

  public TFc2wpPtTypeRelv removeTFc2wpPtTypeRelv(final TFc2wpPtTypeRelv TFc2wpPtTypeRelv) {
    if (this.TFc2wpPtTypeRelvs == null) {
      this.TFc2wpPtTypeRelvs = new HashSet<>();
    }
    getTFc2wpPtTypeRelvs().remove(TFc2wpPtTypeRelv);
    return TFc2wpPtTypeRelv;
  }

  public String getFc2wpDescEng() {
    return this.fc2wpDescEng;
  }

  public void setFc2wpDescEng(final String fc2wpDescEng) {
    this.fc2wpDescEng = fc2wpDescEng;
  }

  public String getFc2wpDescGer() {
    return this.fc2wpDescGer;
  }

  public void setFc2wpDescGer(final String fc2wpDescGer) {
    this.fc2wpDescGer = fc2wpDescGer;
  }

  public String getFc2wpName() {
    return this.fc2wpName;
  }

  public void setFc2wpName(final String fc2wpName) {
    this.fc2wpName = fc2wpName;
  }


}