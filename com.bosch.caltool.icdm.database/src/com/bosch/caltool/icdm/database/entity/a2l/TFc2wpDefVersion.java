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


/**
 * The persistent class for the T_FC2WP_DEF_VERSION database table.
 */
@Entity
@Table(name = "T_FC2WP_DEF_VERSION")
@NamedQueries(value = {
    @NamedQuery(name = "TFc2wpDefVersion.findAll", query = "SELECT t FROM TFc2wpDefVersion t"),
    @NamedQuery(name = TFc2wpDefVersion.NQ_GET_FC2WP_VERS_ID_BY_PIDC_N_ATTR, query = "                                " +
        "select ver.fcwpVerId                                                                               " +
        "from TFc2wpDefinition def, TFc2wpDefVersion ver, TPidcVersion pidcver, TabvProjectAttr pidcattr    " +
        "where pidcattr.tPidcVersion = pidcver                                                              " +
        "  and ver.TFc2wpDefinition = def                                                                   " +
        "  and ver.activeFlag = 'Y'                                                                         " +
        "  and pidcattr.tabvAttribute.attrId = :attrId                                                      " +
        "  and pidcver.pidcVersId = :pidcVersId") })

public class TFc2wpDefVersion implements IEntity, Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Named query to get active version of FC2WP defnition, for the given PIDC version
   */
  public static final String NQ_GET_FC2WP_VERS_ID_BY_PIDC_N_ATTR = "TFc2wpDefVersion.findVersIdByPidcNAttrId";

  /**
   * Named query to get next new version of FC2WP defnition
   */
  public static final String NQ_GET_LATEST_FC2WP_VERS_BY_DEF_ID = "TFc2wpDefVersion.findLatestMajorMinorVersByDefId";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "FCWP_VER_ID", unique = true, nullable = false)
  private long fcwpVerId;

  @Column(name = "ACTIVE_FLAG", length = 1)
  private String activeFlag;

  @Column(name = "ARCH_RELEASE_SDOM", length = 100)
  private String archReleaseSdom;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 30)
  private String createdUser;

  @Column(name = "DESC_ENG", nullable = false, length = 4000)
  private String descEng;

  @Column(name = "DESC_GER", length = 4000)
  private String descGer;

  @Column(name = "IN_WORK_FLAG", length = 1)
  private String inWorkFlag;

  @Column(name = "MAJOR_VERSION_NUM", nullable = false)
  private long majorVersionNum;

  @Column(name = "MINOR_VERSION_NUM")
  private long minorVersionNum;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 30)
  private String modifiedUser;

  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private long version;

  // bi-directional many-to-one association to TFc2wpDefinition
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "FCWP_DEF_ID", nullable = false)
  private TFc2wpDefinition TFc2wpDefinition;

  // bi-directional many-to-one association to TFc2wpMapping
  @OneToMany(mappedBy = "TFc2wpDefVersion", fetch = FetchType.LAZY)
  private Set<TFc2wpMapping> TFc2wpMappings;

  public TFc2wpDefVersion() {}

  public long getFcwpVerId() {
    return this.fcwpVerId;
  }

  public void setFcwpVerId(final long fcwpVerId) {
    this.fcwpVerId = fcwpVerId;
  }

  public String getActiveFlag() {
    return this.activeFlag;
  }

  public void setActiveFlag(final String activeFlag) {
    this.activeFlag = activeFlag;
  }

  public String getArchReleaseSdom() {
    return this.archReleaseSdom;
  }

  public void setArchReleaseSdom(final String archReleaseSdom) {
    this.archReleaseSdom = archReleaseSdom;
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

  public String getDescEng() {
    return this.descEng;
  }

  public void setDescEng(final String descEng) {
    this.descEng = descEng;
  }

  public String getDescGer() {
    return this.descGer;
  }

  public void setDescGer(final String descGer) {
    this.descGer = descGer;
  }

  public String getInWorkFlag() {
    return this.inWorkFlag;
  }

  public void setInWorkFlag(final String inWorkFlag) {
    this.inWorkFlag = inWorkFlag;
  }

  public long getMajorVersionNum() {
    return this.majorVersionNum;
  }

  public void setMajorVersionNum(final long majorVersionNum) {
    this.majorVersionNum = majorVersionNum;
  }

  public long getMinorVersionNum() {
    return this.minorVersionNum;
  }

  public void setMinorVersionNum(final long minorVersionNum) {
    this.minorVersionNum = minorVersionNum;
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

  @Override
  public long getVersion() {
    return this.version;
  }

  public void setVersion(final long version) {
    this.version = version;
  }

  public TFc2wpDefinition getTFc2wpDefinition() {
    return this.TFc2wpDefinition;
  }

  public void setTFc2wpDefinition(final TFc2wpDefinition TFc2wpDefinition) {
    this.TFc2wpDefinition = TFc2wpDefinition;
  }

  public Set<TFc2wpMapping> getTFc2wpMappings() {
    return this.TFc2wpMappings;
  }

  public void setTFc2wpMappings(final Set<TFc2wpMapping> TFc2wpMappings) {
    this.TFc2wpMappings = TFc2wpMappings;
  }

  public TFc2wpMapping addTFc2wpMapping(final TFc2wpMapping TFc2wpMapping) {
    if (this.TFc2wpMappings == null) {
      this.TFc2wpMappings = new HashSet<>();
    }
    getTFc2wpMappings().add(TFc2wpMapping);
    TFc2wpMapping.setTFc2wpDefVersion(this);

    return TFc2wpMapping;
  }

  public TFc2wpMapping removeTFc2wpMapping(final TFc2wpMapping TFc2wpMapping) {
    if (this.TFc2wpMappings == null) {
      this.TFc2wpMappings = new HashSet<>();
    }
    getTFc2wpMappings().remove(TFc2wpMapping);
    TFc2wpMapping.setTFc2wpDefVersion(null);

    return TFc2wpMapping;
  }

}