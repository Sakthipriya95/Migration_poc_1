package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.annotations.OptimisticLocking;

import com.bosch.caltool.icdm.database.entity.a2l.TA2lResponsibility;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lResponsiblityBshgrpUsr;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwUserCmntHistory;


/**
 * The persistent class for the TABV_APIC_USERS database table.
 */
@NamedQueries(value = {
    @NamedQuery(name = TabvApicUser.NQ_GET_ALL_APIC_USERS, query = "select usr from TabvApicUser usr "),
    @NamedQuery(name = TabvApicUser.NQ_GET_APIC_USER_BY_NAME, query = "select usr from TabvApicUser usr where usr.username = :username"),
    @NamedQuery(name = TabvApicUser.NQ_GET_APIC_USER_BY_NAMES_LIST, query = "select usr from TabvApicUser usr where usr.username in :usernamecoll") })
@Entity
@OptimisticLocking(cascade = true)
@Table(name = "TABV_APIC_USERS")
public class TabvApicUser implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Named query to get all users from database
   *
   * @return list of TabvApicUser
   */
  public static final String NQ_GET_ALL_APIC_USERS = "TabvApicUser.getAllApicUsers";

  /**
   * Named query to get the user with the given user name(NT ID)
   *
   * @param username user name(NT ID)
   * @return list of TabvApicUser
   */
  public static final String NQ_GET_APIC_USER_BY_NAME = "TabvApicUser.getApicUserByName";

  /**
   * Named query to get the users with the given set of user names(NT ID)
   *
   * @param usernamecoll set of user names(NT ID)
   * @return list of TabvApicUser
   */
  public static final String NQ_GET_APIC_USER_BY_NAMES_LIST = "TabvApicUser.getApicUserByNamesList";


  private long userId;
  private Timestamp createdDate;
  private String createdUser;
  private String department;
  private String firstname;
  private String lastname;
  private Timestamp modifiedDate;
  private String modifiedUser;
  private String username;
  private Long version;
  private Set<TabvApicAccessRight> tabvApicAccessRights;
  private Set<TabvApicNodeAccess> tabvApicNodeAccesses;
  private Set<TUsecaseFavorite> tUsecaseFavorites;
  private Timestamp disclmrAcceptedDate;
  private List<TWorkpackageDivisionCdl> tWorkpackageDivisionCdls;
  private Set<TabvPidFavorite> tabvPidFavorites;


  private List<TA2lResponsibility> tA2lResponsibilityList;

  private List<TRvwUserCmntHistory> tRvwUserCmntHistoryList;

  private List<TA2lResponsiblityBshgrpUsr> ta2lResponsiblityBshgrpUsrList;

  private Set<TUserPreference> tUserPreferences;


  public TabvApicUser() {
    //
  }

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "USER_ID", unique = true, nullable = false)
  public long getUserId() {
    return this.userId;
  }

  public void setUserId(final long userId) {
    this.userId = userId;
  }


  @Column(name = "CREATED_DATE", nullable = false)
  public Timestamp getCreatedDate() {
    return this.createdDate;
  }

  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
  }


  @Column(name = "CREATED_USER", nullable = false, length = 30)
  public String getCreatedUser() {
    return this.createdUser;
  }

  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }


  @Column(length = 50)
  public String getDepartment() {
    return this.department;
  }

  public void setDepartment(final String department) {
    this.department = department;
  }


  @Column(length = 50)
  public String getFirstname() {
    return this.firstname;
  }

  public void setFirstname(final String firstname) {
    this.firstname = firstname;
  }


  @Column(length = 50)
  public String getLastname() {
    return this.lastname;
  }

  public void setLastname(final String lastname) {
    this.lastname = lastname;
  }


  @Column(name = "MODIFIED_DATE")
  public Timestamp getModifiedDate() {
    return this.modifiedDate;
  }

  public void setModifiedDate(final Timestamp modifiedDate) {
    this.modifiedDate = modifiedDate;
  }


  @Column(name = "MODIFIED_USER", length = 30)
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }


  @Column(nullable = false, length = 30)
  public String getUsername() {
    return this.username;
  }

  public void setUsername(final String username) {
    this.username = username;
  }


  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }


  // bi-directional many-to-one association to TabvApicAccessRight
  @OneToMany(mappedBy = "tabvApicUser")
  @BatchFetch(value = BatchFetchType.JOIN)
  public Set<TabvApicAccessRight> getTabvApicAccessRights() {
    return this.tabvApicAccessRights;
  }

  public void setTabvApicAccessRights(final Set<TabvApicAccessRight> tabvApicAccessRights) {
    this.tabvApicAccessRights = tabvApicAccessRights;
  }


  // bi-directional many-to-one association to TabvApicNodeAccess
  @OneToMany(mappedBy = "tabvApicUser")
  public Set<TabvApicNodeAccess> getTabvApicNodeAccesses() {
    return this.tabvApicNodeAccesses;
  }

  public void setTabvApicNodeAccesses(final Set<TabvApicNodeAccess> tabvApicNodeAccesses) {
    this.tabvApicNodeAccesses = tabvApicNodeAccesses;
  }

  // bi-directional many-to-one association to TUsecaseFavorite
  @OneToMany(mappedBy = "tabvApicUser")
  public Set<TUsecaseFavorite> getTUsecaseFavorites() {
    return this.tUsecaseFavorites;
  }

  public void setTUsecaseFavorites(final Set<TUsecaseFavorite> tUsecaseFavorites) {
    this.tUsecaseFavorites = tUsecaseFavorites;
  }


  /**
   * @return the disclaimerAcceptnceDate
   */
  @Column(name = "DISCLMR_ACCEPTED_DATE")
  public Timestamp getDisclaimerAcceptnceDate() {
    return this.disclmrAcceptedDate;
  }

  /**
   * @param disclaimerAcceptnceDate the disclaimerAcceptnceDate to set
   */
  public void setDisclaimerAcceptnceDate(final Timestamp disclaimerAcceptnceDate) {
    this.disclmrAcceptedDate = disclaimerAcceptnceDate;
  }

  // bi-directional many-to-one association to TWorkpackageDivisionCdl
  @OneToMany(mappedBy = "tabvApicUser", fetch = FetchType.LAZY)
  public List<TWorkpackageDivisionCdl> getTWorkpackageDivisionCdls() {
    return this.tWorkpackageDivisionCdls;
  }

  public void setTWorkpackageDivisionCdls(final List<TWorkpackageDivisionCdl> tWorkpackageDivisionCdls) {
    this.tWorkpackageDivisionCdls = tWorkpackageDivisionCdls;
  }

  public TWorkpackageDivisionCdl addTWorkpackageDivisionCdl(final TWorkpackageDivisionCdl tWorkpackageDivisionCdl) {
    getTWorkpackageDivisionCdls().add(tWorkpackageDivisionCdl);
    tWorkpackageDivisionCdl.setTabvApicUser(this);

    return tWorkpackageDivisionCdl;
  }

  public TWorkpackageDivisionCdl removeTWorkpackageDivisionCdl(final TWorkpackageDivisionCdl tWorkpackageDivisionCdl) {
    getTWorkpackageDivisionCdls().remove(tWorkpackageDivisionCdl);
    return tWorkpackageDivisionCdl;
  }

  public TA2lResponsiblityBshgrpUsr addBoschGrpUser(final TA2lResponsiblityBshgrpUsr ta2lResponsiblityBshgrpUsr) {
    getTa2lResponsiblityBshgrpUsrList().add(ta2lResponsiblityBshgrpUsr);
    ta2lResponsiblityBshgrpUsr.setTabvApicUser(this);
    return ta2lResponsiblityBshgrpUsr;
  }

  public TA2lResponsiblityBshgrpUsr removeBoschGrpUser(final TA2lResponsiblityBshgrpUsr ta2lResponsiblityBshgrpUsr) {
    getTa2lResponsiblityBshgrpUsrList().remove(ta2lResponsiblityBshgrpUsr);
    return ta2lResponsiblityBshgrpUsr;
  }

  /**
   * @param nodeAccessEntity node access entity to remove
   */
  public void removeTabvApicNodeAccess(final TabvApicNodeAccess nodeAccessEntity) {
    if (this.tabvApicNodeAccesses != null) {
      this.tabvApicNodeAccesses.remove(nodeAccessEntity);
    }
  }

  /**
   * @param entity entity to add
   */
  public void addTabvApicNodeAccess(final TabvApicNodeAccess entity) {
    if (this.tabvApicNodeAccesses == null) {
      this.tabvApicNodeAccesses = new HashSet<>();
    }
    this.tabvApicNodeAccesses.add(entity);

  }

  /**
   * @param TRvwUserCmntHistory node access entity to remove
   */
  public void removeTRvwUserCmntHistory(final TRvwUserCmntHistory tRvwUserCmntHistory) {
    if (this.tRvwUserCmntHistoryList != null) {
      this.tRvwUserCmntHistoryList.remove(tRvwUserCmntHistory);
    }
  }

  /**
   * @param entity entity to add
   */
  public void addTRvwUserCmntHistory(final TRvwUserCmntHistory tRvwUserCmntHistory) {
    if (this.tRvwUserCmntHistoryList == null) {
      this.tRvwUserCmntHistoryList = new ArrayList<>();
    }
    this.tRvwUserCmntHistoryList.add(tRvwUserCmntHistory);

  }


  /**
   * @return the tabvPidFavorite
   */
  @OneToMany(mappedBy = "tabvApicUser", fetch = FetchType.LAZY)
  // bi-directional many-to-one association to TUsecaseFavorite
  public Set<TabvPidFavorite> getTabvPidFavorites() {
    return this.tabvPidFavorites;
  }

  /**
   * @param tabvPidFavorite the tabvPidFavorite to set
   */
  public void setTabvPidFavorites(final Set<TabvPidFavorite> tabvPidFavorite) {
    this.tabvPidFavorites = tabvPidFavorite;
  }


  /**
   * @return the tA2lResponsibilityList
   */
  @OneToMany(mappedBy = "tabvApicUser", fetch = FetchType.LAZY)
  public List<TA2lResponsibility> getTA2lResponsibilityList() {
    return this.tA2lResponsibilityList;
  }


  /**
   * @param tA2lResponsibilityList the tA2lResponsibilityList to set
   */
  public void setTA2lResponsibilityList(final List<TA2lResponsibility> tA2lResponsibilityList) {
    this.tA2lResponsibilityList = tA2lResponsibilityList;
  }


  /**
   * @return the tRvwUserCmntHistoryList
   */
  @OneToMany(mappedBy = "rvwCmntUser", fetch = FetchType.LAZY)
  public List<TRvwUserCmntHistory> gettRvwUserCmntHistoryList() {
    return this.tRvwUserCmntHistoryList;
  }


  /**
   * @param tRvwUserCmntHistoryList the tRvwUserCmntHistoryList to set
   */
  public void settRvwUserCmntHistoryList(final List<TRvwUserCmntHistory> tRvwUserCmntHistoryList) {
    this.tRvwUserCmntHistoryList = tRvwUserCmntHistoryList;
  }


  /**
   * @return the ta2lResponsiblityBshgrpUsrList
   */
  @OneToMany(mappedBy = "tabvApicUser", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  public List<TA2lResponsiblityBshgrpUsr> getTa2lResponsiblityBshgrpUsrList() {
    return this.ta2lResponsiblityBshgrpUsrList;
  }


  /**
   * @param ta2lResponsiblityBshgrpUsrList the ta2lResponsiblityBshgrpUsrList to set
   */
  public void setTa2lResponsiblityBshgrpUsrList(final List<TA2lResponsiblityBshgrpUsr> ta2lResponsiblityBshgrpUsrList) {
    this.ta2lResponsiblityBshgrpUsrList = ta2lResponsiblityBshgrpUsrList;
  }

  // bi-directional many-to-one association to TUserPreferences
  @OneToMany(mappedBy = "tabvApicUser", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  public Set<TUserPreference> getTUserPreferences() {
    return this.tUserPreferences;
  }

  /**
   * @param tUserPreferences the tUserPreferences to set
   */
  public void setTUserPreferences(final Set<TUserPreference> tUserPreferences) {
    this.tUserPreferences = tUserPreferences;
  }

}