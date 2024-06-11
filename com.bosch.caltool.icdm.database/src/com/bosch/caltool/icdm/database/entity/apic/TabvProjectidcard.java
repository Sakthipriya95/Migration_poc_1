package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.sql.Timestamp;
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
import org.eclipse.persistence.annotations.OptimisticLocking;
import org.eclipse.persistence.annotations.PrivateOwned;

import com.bosch.caltool.icdm.database.entity.a2l.TA2lResponsibility;


/**
 * The persistent class for the TABV_PROJECTIDCARD database table.
 */
@NamedQueries(value = {
    @NamedQuery(name = TabvProjectidcard.NQ_GET_ALL_PIDCS, query = "select pidc from TabvProjectidcard pidc"),
    @NamedQuery(name = TabvProjectidcard.NQ_GET_PIDC_BY_VALID, query = "select pidc from TabvProjectidcard pidc where pidc.tabvAttrValue.valueId=:valId"),
    @NamedQuery(name = TabvProjectidcard.NQ_GET_PIDC_ID_BY_NAME, query = "select pidc.projectId from TabvProjectidcard pidc where upper(pidc.tabvAttrValue.textvalueEng) like upper(:pidcName)") })
@Entity
// Icdm-230
@OptimisticLocking(cascade = true)
@Table(name = "TABV_PROJECTIDCARD")
public class TabvProjectidcard implements Serializable {

  private static final long serialVersionUID = 1L;


  /**
   * Named query to get all PID Cards from database
   *
   * @return list of TabvProjectidcard
   */
  public static final String NQ_GET_ALL_PIDCS = "TabvProjectidcard.getAllPidcs";

  /**
   * Name query to get pidc by its name value id
   */
  public static final String NQ_GET_PIDC_BY_VALID = "TabvProjectidcard.getPidcByValId";

  /**
   * Name query to get pidc ids by its name
   */
  public static final String NQ_GET_PIDC_ID_BY_NAME = "TabvProjectidcard.getPidcIdByName";

  private long projectId;
  private Timestamp createdDate;
  private String createdUser;
  private Timestamp modifiedDate;
  private String modifiedUser;
  private Long proRevId;
  private Long version;
  private Set<TabvPidFavorite> tabvPidFavorites;
  private TabvAttrValue tabvAttrValue;
  private Set<TUsecaseFavorite> TUsecaseFavorites;
  private Long aprjId;
  private String vcdmTransferUser;
  private Timestamp vcdmTransferDate;
  private Set<TPidcVersion> tPidcVersions;
  private Set<TPidcA2l> tabvPidcA2ls;
  private TAliasDefinition taliasDefinition;

  private List<TA2lResponsibility> tA2lResponsibilityList;

  private String inclRvwOfOldVers;

  public TabvProjectidcard() {
    // dummy constructor
  }


  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "PROJECT_ID", unique = true, nullable = false, precision = 15)
  public long getProjectId() {
    return this.projectId;
  }

  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public void setProjectId(final long projectId) {
    this.projectId = projectId;
  }


  @Column(name = "CREATED_DATE", nullable = false)
  public Timestamp getCreatedDate() {
    return this.createdDate;
  }

  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
  }


  @Column(name = "CREATED_USER", nullable = false, length = 100)
  public String getCreatedUser() {
    return this.createdUser;
  }

  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }


  @Column(name = "MODIFIED_DATE")
  public Timestamp getModifiedDate() {
    return this.modifiedDate;
  }

  public void setModifiedDate(final Timestamp modifiedDate) {
    this.modifiedDate = modifiedDate;
  }


  @Column(name = "MODIFIED_USER", length = 100)
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }


  @Column(name = "PRO_REV_ID", nullable = false, precision = 15)
  public Long getProRevId() {
    return this.proRevId;
  }

  public void setProRevId(final Long proRevId) {
    this.proRevId = proRevId;
  }

  // bi-directional many-to-one association to TAliasDefinition
  @ManyToOne
  @JoinColumn(name = "AD_ID")
  public TAliasDefinition getTaliasDefinition() {
    return this.taliasDefinition;
  }

  public void setTaliasDefinition(final TAliasDefinition taliasDefinition) {
    this.taliasDefinition = taliasDefinition;
  }

  // bi-directional many-to-one association to TabvPidFavorite
  @OneToMany(mappedBy = "tabvProjectidcard")
  public Set<TabvPidFavorite> getTabvPidFavorites() {
    return this.tabvPidFavorites;
  }

  public void setTabvPidFavorites(final Set<TabvPidFavorite> tabvPidFavorites) {
    this.tabvPidFavorites = tabvPidFavorites;
  }

  // bi-directional many-to-one association to TPidcVersion
  @OneToMany(mappedBy = "tabvProjectidcard")
  @PrivateOwned
  @BatchFetch(value = BatchFetchType.JOIN)
  public Set<TPidcVersion> getTPidcVersions() {
    return this.tPidcVersions;
  }

  public void setTPidcVersions(final Set<TPidcVersion> tPidcVersions) {
    this.tPidcVersions = tPidcVersions;
  }


  // bi-directional many-to-one association to TabvAttrValue
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "VALUE_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  public TabvAttrValue getTabvAttrValue() {
    return this.tabvAttrValue;
  }

  public void setTabvAttrValue(final TabvAttrValue tabvAttrValue) {
    this.tabvAttrValue = tabvAttrValue;
  }

  // bi-directional many-to-one association to TUsecaseFavorite
  @OneToMany(mappedBy = "tabvProjectidcard", fetch = FetchType.LAZY)
  public Set<TUsecaseFavorite> getTUsecaseFavorites() {
    return this.TUsecaseFavorites;
  }

  public void setTUsecaseFavorites(final Set<TUsecaseFavorite> TUsecaseFavorites) {
    this.TUsecaseFavorites = TUsecaseFavorites;
  }

  @Column(name = "APRJ_ID")
  public Long getAprjId() {
    return this.aprjId;
  }

  public void setAprjId(final Long aprjId) {
    this.aprjId = aprjId;
  }

  @Column(name = "VCDM_TRANSFER_USER")
  public String getVcdmTransferUser() {
    return this.vcdmTransferUser;
  }

  public void setVcdmTransferUser(final String vcdmTransferUser) {
    this.vcdmTransferUser = vcdmTransferUser;
  }


  /**
   * @return the inclRvwOfOldVers
   */
  @Column(name = "INCL_RVW_OF_OLD_VERS", length = 1)
  public String getInclRvwOfOldVers() {
    return this.inclRvwOfOldVers;
  }


  /**
   * @param inclRvwOfOldVers the inclRvwOfOldVers to set
   */
  public void setInclRvwOfOldVers(final String inclRvwOfOldVers) {
    this.inclRvwOfOldVers = inclRvwOfOldVers;
  }


  @Column(name = "VCDM_TRANSFER_DATE")
  public Timestamp getVcdmTransferDate() {
    return this.vcdmTransferDate;
  }

  public void setVcdmTransferDate(final Timestamp vcdmTransferDate) {
    this.vcdmTransferDate = vcdmTransferDate;
  }

  // bi-directional many-to-one association to TabvPidcA2l
  @OneToMany(mappedBy = "tabvProjectidcard")
  @PrivateOwned
  public Set<TPidcA2l> getTabvPidcA2ls() {
    return this.tabvPidcA2ls;
  }

  public void setTabvPidcA2ls(final Set<TPidcA2l> tabvPidcA2ls) {
    this.tabvPidcA2ls = tabvPidcA2ls;
  }


  /**
   * @return the tA2lResponsibilityList
   */
  @OneToMany(mappedBy = "tabvProjectidcard", fetch = FetchType.LAZY)
  public List<TA2lResponsibility> getTA2lResponsibilityList() {
    return this.tA2lResponsibilityList;
  }


  /**
   * @param tA2lResponsibilityList the tA2lResponsibilityList to set
   */
  public void setTA2lResponsibilityList(final List<TA2lResponsibility> tA2lResponsibilityList) {
    this.tA2lResponsibilityList = tA2lResponsibilityList;
  }


}