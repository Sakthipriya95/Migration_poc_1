package com.bosch.caltool.icdm.database.entity.apic;

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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.a2l.TFc2wpMapping;
import com.bosch.caltool.icdm.database.entity.a2l.TWpResource;
import com.bosch.caltool.icdm.database.entity.apic.cocwp.TPidcSubVarCocWp;
import com.bosch.caltool.icdm.database.entity.apic.cocwp.TPidcVariantCocWp;
import com.bosch.caltool.icdm.database.entity.apic.cocwp.TPidcVersCocWp;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;


/**
 * The persistent class for the T_WORKPACKAGE_DIVISION database table.
 */
@Entity
@Table(name = "T_WORKPACKAGE_DIVISION")
@NamedQueries({
    @NamedQuery(name = "TWorkpackageDivision.findAll", query = "SELECT t FROM TWorkpackageDivision t"),
    @NamedQuery(name = TWorkpackageDivision.NQ_FIND_BY_DIV_ID, query = "SELECT t FROM TWorkpackageDivision t " +
        "       where (t.tabvAttrValue.valueId=:divValueId) AND coalesce(t.deleteFlag,'N') !='Y' AND coalesce(t.tWorkpackage.deleteFlag,'N') != 'Y'"),
    @NamedQuery(name = TWorkpackageDivision.NQ_FIND_BY_DIV_ID_WITH_ICC_RELEVANT, query = "SELECT t FROM TWorkpackageDivision t " +
        "       where (t.tabvAttrValue.valueId=:divValueId) AND coalesce(t.deleteFlag,'N') !='Y' AND coalesce(t.tWorkpackage.deleteFlag,'N') != 'Y'" +
        "AND t.iccRelevantFlag = 'Y'"),
    @NamedQuery(name = TWorkpackageDivision.NQ_FIND_BY_DIV_ID_WITHOUT_ICC_RELEVANT, query = "SELECT t FROM TWorkpackageDivision t " +
        "       where (t.tabvAttrValue.valueId=:divValueId) AND coalesce(t.deleteFlag,'N') !='Y' AND coalesce(t.tWorkpackage.deleteFlag,'N') != 'Y'" +
        "AND (t.iccRelevantFlag = 'N' or t.iccRelevantFlag is null)"),
    @NamedQuery(name = TWorkpackageDivision.NQ_FIND_ALL_BY_DIV_ID, query = "SELECT t FROM TWorkpackageDivision t " +
        "       where (t.tabvAttrValue.valueId=:divValueId)"),
    @NamedQuery(name = TWorkpackageDivision.NQ_FIND_ALL_BY_DIV_ID_WITH_ICC_RELEVANT, query = "SELECT t FROM TWorkpackageDivision t " +
        "       where (t.tabvAttrValue.valueId=:divValueId)" + "AND t.iccRelevantFlag = 'Y'"),
    @NamedQuery(name = TWorkpackageDivision.NQ_FIND_ALL_BY_DIV_ID_WITHOUT_ICC_RELEVANT, query = "SELECT t FROM TWorkpackageDivision t " +
        "       where (t.tabvAttrValue.valueId=:divValueId) " +
        "AND (t.iccRelevantFlag = 'N' or t.iccRelevantFlag is null)"),
    @NamedQuery(name = TWorkpackageDivision.NQ_FIND_RES_BY_DIV_ID, query = "SELECT t FROM TWorkpackageDivision t " +
        "       where (t.tabvAttrValue.valueId=:divValueId AND t.tWorkpackage.wpId=:wpId)"),
    @NamedQuery(name = TWorkpackageDivision.NQ_FIND_BY_DIV_AND_MASTERLIST_ID, query = "SELECT t FROM TWorkpackageDivision t" +
        "       where (t.tabvAttrValue.valueId=:divValueId) and (t.tWorkpackage.tWpmlWpMasterList.id IS NOT NULL)") })
public class TWorkpackageDivision implements Serializable {

  private static final long serialVersionUID = 1L;
  /**
   * Named query to get wp divisions for the given div Id
   */
  public static final String NQ_FIND_BY_DIV_ID = "TWorkpackageDivision.findByDivId";
  /**
   * Named query to get wp divisions for the given div Id including deleted Wp
   */
  public static final String NQ_FIND_ALL_BY_DIV_ID = "TWorkpackageDivision.findAllByDivId";
  /**
   * Named query to get wp divisions for the given div Id and wpmasterlist id excluding deleted Wp
   */
  public static final String NQ_FIND_BY_DIV_AND_MASTERLIST_ID = "TWorkpackageDivision.findByDivAndMasterListId";
  /**
   * Named query to get wp resource for the given div Id
   */
  public static final String NQ_FIND_RES_BY_DIV_ID = "TWorkpackageDivision.findResByDivId";
  /**
   * Named query to get wp resource for the given div Id with icc relevant flag
   */
  public static final String NQ_FIND_BY_DIV_ID_WITH_ICC_RELEVANT = "TWorkpackageDivision.findwithiccrelevantflagY";
  /**
   * Named query to get wp resource for the given div Id with icc relevant flag
   */
  public static final String NQ_FIND_BY_DIV_ID_WITHOUT_ICC_RELEVANT = "TWorkpackageDivision.findwithiccrelevantflagN";
  /**
   * Named query to get wp divisions for the given div Id including deleted Wp
   */
  public static final String NQ_FIND_ALL_BY_DIV_ID_WITH_ICC_RELEVANT = "TWorkpackageDivision.findallwithiccflagY";
  /**
   * Named query to get wp divisions for the given div Id including deleted Wp
   */
  public static final String NQ_FIND_ALL_BY_DIV_ID_WITHOUT_ICC_RELEVANT = "TWorkpackageDivision.findallwithiccflagN";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "WP_DIV_ID", unique = true, nullable = false, precision = 15)
  private long wpDivId;
  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;
  @Column(name = "CREATED_USER", length = 100)
  private String createdUser;
  @Column(name = "DELETE_FLAG", length = 1)
  private String deleteFlag;
  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;
  @Column(name = "MODIFIED_USER", length = 100)
  private String modifiedUser;
  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  private Long version;
  @Column(name = "WP_ID_MCR", length = 50)
  private String wpIdMcr;
  @Column(name = "WPD_COMMENT", length = 4000)
  private String wpdComment;
  @Column(name = "ICC_RELEVANT_FLAG", length = 1)
  private String iccRelevantFlag;
  @Column(name = "CRP_OBD_RELEVANT_FLAG", length = 1)
  private String crpObdRelevantFlag;
  @Column(name = "CRP_OBD_COMMENT", length = 4000)
  private String crpObdComment;
  @Column(name = "CRP_EMISSION_RELEVANT_FLAG", length = 1)
  private String crpEmissionRelevantFlag;
  @Column(name = "CRP_EMISSION_COMMENT", length = 4000)
  private String crpEmissionComment;
  @Column(name = "CRP_SOUND_RELEVANT_FLAG", length = 1)
  private String crpSoundRelevantFlag;
  @Column(name = "CRP_SOUND_COMMENT", length = 4000)
  private String crpSoundComment;


  // bi-directional many-to-one association to TFc2wpMapping
  @OneToMany(mappedBy = "TWorkpackageDivision", fetch = FetchType.LAZY)
  private Set<TFc2wpMapping> tFc2wpMappings;

  // bi-directional many-to-one association to TabvApicUser
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "CONTACT_USER_ID")
  private TabvApicUser tabvApicContactUser;

  // bi-directional many-to-one association to TabvApicUser
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "SECOND_CONTACT_USER_ID")
  private TabvApicUser tabvApicSecondContactUser;

  // bi-directional many-to-one association to TabvAttrValue
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "DIV_VALUE_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TabvAttrValue tabvAttrValue;

  // bi-directional many-to-one association to TWorkpackage
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "WP_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TWorkpackage tWorkpackage;

  // bi-directional many-to-one association to TWpResource
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "WP_RES_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TWpResource tWpResource;

  // bi-directional many-to-one association to TQuestionnaire
  @OneToMany(mappedBy = "tWorkpackageDivision", fetch = FetchType.LAZY)
  private List<TQuestionnaire> tQuestionnaires;

  // bi-directional many-to-one association to TRvwResult
  @OneToMany(mappedBy = "TWorkpackageDivision")
  private Set<TRvwResult> tRvwResults;

  // bi-directional many-to-one association to TWorkpackageDivisionCdl
  @OneToMany(mappedBy = "tWorkpackageDivision", fetch = FetchType.LAZY)
  private List<TWorkpackageDivisionCdl> tWorkpackageDivisionCdls;

  // bi-directional one-to-many association to TPidcVersCocWp
  @OneToMany(mappedBy = "twrkpkgdiv", fetch = FetchType.LAZY)
  private List<TPidcVersCocWp> tPidcVersCocWp;

  // bi-directional one-to-many association to TPidcVariantCocWp
  @OneToMany(mappedBy = "twrkpkgdiv", fetch = FetchType.LAZY)
  private List<TPidcVariantCocWp> tPidcVariantCocWp;

  // bi-directional one-to-many association to TPidcSubVarCocWp
  @OneToMany(mappedBy = "twrkpkgdiv", fetch = FetchType.LAZY)
  private List<TPidcSubVarCocWp> tPidcSubVarCocWp;


  public TWorkpackageDivision() {
//    public constructor
  }

  public long getWpDivId() {
    return this.wpDivId;
  }

  public void setWpDivId(final long wpDivId) {
    this.wpDivId = wpDivId;
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

  public String getDeleteFlag() {
    return this.deleteFlag;
  }

  public void setDeleteFlag(final String deleteFlag) {
    this.deleteFlag = deleteFlag;
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

  public String getWpIdMcr() {
    return this.wpIdMcr;
  }

  public void setWpIdMcr(final String wpIdMcr) {
    this.wpIdMcr = wpIdMcr;
  }

  public String getWpdComment() {
    return this.wpdComment;
  }

  public void setWpdComment(final String wpdComment) {
    this.wpdComment = wpdComment;
  }

  public Set<TFc2wpMapping> getTFc2wpMappings() {
    return this.tFc2wpMappings;
  }

  public void setTFc2wpMappings(final Set<TFc2wpMapping> tFc2wpMappings) {
    this.tFc2wpMappings = tFc2wpMappings;
  }

  public TFc2wpMapping addTFc2wpMapping(final TFc2wpMapping tFc2wpMapping) {
    getTFc2wpMappings().add(tFc2wpMapping);
    tFc2wpMapping.setTWorkpackageDivision(this);

    return tFc2wpMapping;
  }

  public TFc2wpMapping removeTFc2wpMapping(final TFc2wpMapping tFc2wpMapping) {
    getTFc2wpMappings().remove(tFc2wpMapping);
    tFc2wpMapping.setTWorkpackageDivision(null);

    return tFc2wpMapping;
  }

  public TabvApicUser getTabvApicContactUser() {
    return this.tabvApicContactUser;
  }

  public void setTabvApicContactUser(final TabvApicUser tabvApicUser) {
    this.tabvApicContactUser = tabvApicUser;
  }

  public TabvApicUser getTabvApicSecondContactUser() {
    return this.tabvApicSecondContactUser;
  }

  public void setTabvApicSecondContactUser(final TabvApicUser tabvApicUser) {
    this.tabvApicSecondContactUser = tabvApicUser;
  }

  public TabvAttrValue getTabvAttrValue() {
    return this.tabvAttrValue;
  }

  public void setTabvAttrValue(final TabvAttrValue tabvAttrValue) {
    this.tabvAttrValue = tabvAttrValue;
  }

  public TWorkpackage getTWorkpackage() {
    return this.tWorkpackage;
  }

  public void setTWorkpackage(final TWorkpackage tWorkpackage) {
    this.tWorkpackage = tWorkpackage;
  }

  public TWpResource getTWpResource() {
    return this.tWpResource;
  }

  public void setTWpResource(final TWpResource tWpResource) {
    this.tWpResource = tWpResource;
  }

  public List<TQuestionnaire> getTQuestionnaires() {
    return this.tQuestionnaires;
  }

  public void setTQuestionnaires(final List<TQuestionnaire> tQuestionnaires) {
    this.tQuestionnaires = tQuestionnaires;
  }

  public Set<TRvwResult> getTRvwResults() {
    return this.tRvwResults;
  }

  public void setTRvwResults(final Set<TRvwResult> tRvwResults) {
    this.tRvwResults = tRvwResults;
  }

  public List<TWorkpackageDivisionCdl> getTWorkpackageDivisionCdls() {
    return this.tWorkpackageDivisionCdls;
  }

  public void setTWorkpackageDivisionCdls(final List<TWorkpackageDivisionCdl> tWorkpackageDivisionCdls) {
    this.tWorkpackageDivisionCdls = tWorkpackageDivisionCdls;
  }


  /**
   * @return the iccRelevantFlag
   */
  public String getIccRelevantFlag() {
    return this.iccRelevantFlag;
  }


  /**
   * @param iccRelevantFlag the iccRelevantFlag to set
   */
  public void setIccRelevantFlag(final String iccRelevantFlag) {
    this.iccRelevantFlag = iccRelevantFlag;
  }


  /**
   * @return the crpObdRelevantFlag
   */
  public String getCrpObdRelevantFlag() {
    return this.crpObdRelevantFlag;
  }


  /**
   * @param crpObdRelevantFlag the crpObdRelevantFlag to set
   */
  public void setCrpObdRelevantFlag(final String crpObdRelevantFlag) {
    this.crpObdRelevantFlag = crpObdRelevantFlag;
  }


  /**
   * @return the crpObdComment
   */
  public String getCrpObdComment() {
    return this.crpObdComment;
  }


  /**
   * @param crpObdComment the crpObdComment to set
   */
  public void setCrpObdComment(final String crpObdComment) {
    this.crpObdComment = crpObdComment;
  }


  /**
   * @return the crpEmissionRelevantFlag
   */
  public String getCrpEmissionRelevantFlag() {
    return this.crpEmissionRelevantFlag;
  }


  /**
   * @param crpEmissionRelevantFlag the crpEmissionRelevantFlag to set
   */
  public void setCrpEmissionRelevantFlag(final String crpEmissionRelevantFlag) {
    this.crpEmissionRelevantFlag = crpEmissionRelevantFlag;
  }


  /**
   * @return the crpEmissionComment
   */
  public String getCrpEmissionComment() {
    return this.crpEmissionComment;
  }


  /**
   * @param crpEmissionComment the crpEmissionComment to set
   */
  public void setCrpEmissionComment(final String crpEmissionComment) {
    this.crpEmissionComment = crpEmissionComment;
  }


  /**
   * @return the crpSoundRelevantFlag
   */
  public String getCrpSoundRelevantFlag() {
    return this.crpSoundRelevantFlag;
  }


  /**
   * @param crpSoundRelevantFlag the crpSoundRelevantFlag to set
   */
  public void setCrpSoundRelevantFlag(final String crpSoundRelevantFlag) {
    this.crpSoundRelevantFlag = crpSoundRelevantFlag;
  }


  /**
   * @return the crpSoundComment
   */
  public String getCrpSoundComment() {
    return this.crpSoundComment;
  }


  /**
   * @param crpSoundComment the crpSoundComment to set
   */
  public void setCrpSoundComment(final String crpSoundComment) {
    this.crpSoundComment = crpSoundComment;
  }

  public TWorkpackageDivisionCdl addTWorkpackageDivisionCdl(final TWorkpackageDivisionCdl tWorkpackageDivisionCdl) {
    getTWorkpackageDivisionCdls().add(tWorkpackageDivisionCdl);
    tWorkpackageDivisionCdl.setTWorkpackageDivision(this);

    return tWorkpackageDivisionCdl;
  }

  /**
   * @param tWorkpackageDivisionCdl TWorkpackageDivisionCdl
   * @return TWorkpackageDivisionCdl
   */
  public TWorkpackageDivisionCdl removeTWorkpackageDivisionCdl(final TWorkpackageDivisionCdl tWorkpackageDivisionCdl) {
    getTWorkpackageDivisionCdls().remove(tWorkpackageDivisionCdl);
    return tWorkpackageDivisionCdl;
  }


  /**
   * @return the tPidcVersCocWp
   */
  public List<TPidcVersCocWp> gettPidcVersCocWp() {
    return this.tPidcVersCocWp;
  }


  /**
   * @param tPidcVersCocWp the tPidcVersCocWp to set
   */
  public void settPidcVersCocWp(final List<TPidcVersCocWp> tPidcVersCocWp) {
    this.tPidcVersCocWp = tPidcVersCocWp;
  }


  /**
   * @return the tPidcVariantCocWp
   */
  public List<TPidcVariantCocWp> gettPidcVariantCocWp() {
    if (CommonUtils.isNull(this.tPidcVariantCocWp)) {
      this.tPidcVariantCocWp = new ArrayList<>();
    }
    return this.tPidcVariantCocWp;
  }


  /**
   * @param tPidcVariantCocWp the tPidcVariantCocWp to set
   */
  public void settPidcVariantCocWp(final List<TPidcVariantCocWp> tPidcVariantCocWp) {
    this.tPidcVariantCocWp = tPidcVariantCocWp;
  }


  /**
   * @return the tPidcSubVarCocWp
   */
  public List<TPidcSubVarCocWp> gettPidcSubVarCocWp() {
    if (CommonUtils.isNull(this.tPidcSubVarCocWp)) {
      this.tPidcSubVarCocWp = new ArrayList<>();
    }
    return this.tPidcSubVarCocWp;
  }


  /**
   * @param tPidcSubVarCocWp the tPidcSubVarCocWp to set
   */
  public void settPidcSubVarCocWp(final List<TPidcSubVarCocWp> tPidcSubVarCocWp) {
    this.tPidcSubVarCocWp = tPidcSubVarCocWp;
  }


}
