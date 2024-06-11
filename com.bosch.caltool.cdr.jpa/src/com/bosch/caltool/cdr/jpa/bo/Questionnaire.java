/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.apic.jpa.bo.NodeAccessRight;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionnaireVersion;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * ICDM-2005
 *
 * @author jvi6cob
 */
public class Questionnaire extends AbstractCdrObject implements Comparable<Questionnaire> {

  /**
   *
   */
  private static final int VERSION_SIZE_1 = 1;
  private static final String FLD_NAME_ENG = "NAME_ENG";
  private static final String FLD_NAME_GER = "NAME_GER";
  private static final String FLD_DESC_ENG = "DESC_ENG";
  private static final String FLD_DESC_GER = "DESC_GER";
  /**
   * Set of All QuestionnaireVersions of this Questionnaire
   */
  private final SortedSet<QuestionnaireVersion> allQnaireVersions;
  /**
   * Active version
   */
  private QuestionnaireVersion activeQnaireVersion;

  /**
   * @param activeQnaireVersion the activeQnaireVersion to set
   */
  public void setActiveQnaireVersion(final QuestionnaireVersion activeQnaireVersion) {
    this.activeQnaireVersion = activeQnaireVersion;
  }

  /**
   * if true, versions are loaded atleast once.
   */
  private boolean versionsLoaded;


  /**
   * @param cdrDataProvider ApicDataProvider
   * @param qNaireID Long
   */
  public Questionnaire(final CDRDataProvider cdrDataProvider, final Long qNaireID) {

    super(cdrDataProvider, qNaireID);
    getDataCache().getQuestionnaireMap().put(qNaireID, this);
    this.allQnaireVersions = Collections.synchronizedSortedSet(new TreeSet<QuestionnaireVersion>());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbQuestionnaire(getID()).getCreatedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbQuestionnaire(getID()).getModifiedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbQuestionnaire(getID()).getCreatedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbQuestionnaire(getID()).getModifiedDate());
  }

  /**
   * @return Work Package division, if available
   */
  public WorkPackageDivision getWorkPackageDivision() {
    if (null == getEntityProvider().getDbQuestionnaire(getID()).getTWorkpackageDivision()) {
      return null;
    }
    return getDataProvider()
        .getWorkPackageDivision(getEntityProvider().getDbQuestionnaire(getID()).getTWorkpackageDivision().getWpDivId());

  }

  /**
   * @return wp division name
   */
  public String getWPDivisionName() {
    if (null == getWorkPackageDivision()) {
      return "";
    }
    return getWorkPackageDivision().getDivisionName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return CDREntityType.QUESTIONNAIRE;
  }

  /**
   * Returns the name of the questionnaire. Format WP name + WP division name
   * <p>
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return ApicUtil.getLangSpecTxt(getDataCache().getLanguage(), getNameEng(), getNameGer(), "");
  }

  /**
   * @return name of the questionnaire, without division
   */
  public String getNameSimple() {

    return ApicUtil.getLangSpecTxt(getDataCache().getLanguage(), getNameSimpleEng(), getNameGer(),
        ApicConstants.EMPTY_STRING);
  }

  /**
   * Returns the Questionnaire Name in ENGLISH
   *
   * @return Questionnaire English Name in String
   */
  public String getNameSimpleEng() {
    String returnValue = getEntityProvider().getDbQuestionnaire(getID()).getNameEng();

    if (CommonUtils.isEmptyString(returnValue)) {
      returnValue = getWorkPackageDivision().getWPNameEng();
    }

    return returnValue;
  }

  /**
   * Returns the Questionnaire Name in ENGLISH
   *
   * @return Questionnaire English Name in String
   */
  public String getNameEng() {
    String returnValue = null;
    if (null != getEntityProvider().getDbQuestionnaire(getID())) {
      returnValue = getEntityProvider().getDbQuestionnaire(getID()).getNameEng();
    }
    if (CommonUtils.isEmptyString(returnValue)) {
      returnValue = getWorkPackageDivision().getNameEng();
    }
    if (returnValue == null) {
      returnValue = "";
    }
    return returnValue;
  }


  /**
   * Returns the Questionnaire Name in GERMAN
   *
   * @return Questionnaire German Name in String
   */
  public String getNameSimpleGer() {
    String returnValue = getEntityProvider().getDbQuestionnaire(getID()).getNameGer();
    if (CommonUtils.isEmptyString(returnValue)) {
      returnValue = getWorkPackageDivision().getWPNameGerman();
    }
    return returnValue;
  }

  /**
   * Returns the Questionnaire Name in GERMAN
   *
   * @return Questionnaire German Name in String
   */
  public String getNameGer() {
    String returnValue = getEntityProvider().getDbQuestionnaire(getID()).getNameGer();
    if (CommonUtils.isEmptyString(returnValue)) {
      WorkPackageDivision wpDiv = getWorkPackageDivision();
      returnValue = wpDiv == null ? "" : wpDiv.getNameGer();
    }
    return returnValue;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {

    return ApicUtil.getLangSpecTxt(getDataCache().getLanguage(), getDescEng(), getDescGer(),
        ApicConstants.EMPTY_STRING);
  }

  /**
   * Returns the Questionnaire Description in ENGLISH
   *
   * @return Questionnaire English description in String
   */
  public String getDescEng() {
    String returnValue = getEntityProvider().getDbQuestionnaire(getID()).getDescEng();
    if (returnValue == null) {
      returnValue = "";
    }
    return returnValue;
  }

  /**
   * Returns the Questionnaire Description in GERMAN
   *
   * @return Questionnaire German Description in String
   */
  public String getDescGer() {
    String returnValue = getEntityProvider().getDbQuestionnaire(getID()).getDescGer();
    if (returnValue == null) {
      returnValue = "";
    }
    return returnValue;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final Questionnaire qNaireToCmp) {
    int compResult = ApicUtil.compare(getName(), qNaireToCmp.getName());
    if (compResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      return ApicUtil.compare(getID(), qNaireToCmp.getID());
    }
    return compResult;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return super.equals(obj);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * @return true, if versions are loaded atleast once
   */
  boolean isVersionsLoaded() {
    return this.versionsLoaded;
  }

  /**
   * reload all versions of the Questionnaire
   */
  public void reloadVersions() {
    this.versionsLoaded = false;
    getAllVersions();
  }

  /**
   * @return all versions of this PIDC
   */
  public SortedSet<QuestionnaireVersion> getAllVersions() {
    synchronized (this) {
      if (!this.versionsLoaded) {
        getLogger().debug("Loading all versions of Questionnaire : " + getID());
        Set<TQuestionnaireVersion> qNaireVersions =
            getEntityProvider().getDbQuestionnaire(getID()).getTQuestionnaireVersions();
        QuestionnaireVersion qNaireVerBO;
        for (TQuestionnaireVersion qNaireVersion : qNaireVersions) {
          qNaireVerBO = getDataCache().getQuestionnaireVersion(qNaireVersion.getQnaireVersId());
          if (qNaireVerBO == null) {
            qNaireVerBO = new QuestionnaireVersion(getDataProvider(), qNaireVersion.getQnaireVersId());
          }
          this.allQnaireVersions.add(qNaireVerBO);
        }
        this.versionsLoaded = true;
        getLogger().debug("Total Questionnaire versions count = " + this.allQnaireVersions.size());
      }
    }
    return this.allQnaireVersions;

  }

  /**
   * TODO: check whether active pidc version needs to be searched each time or can be stored locally
   *
   * @return the active version of the pidc
   */
  public QuestionnaireVersion getActiveVersion() {
    synchronized (this) {
      if (this.activeQnaireVersion == null) {
        getLogger().debug("Finding active version of Questionnaire : " + getID());
        SortedSet<QuestionnaireVersion> allVersions = getAllVersions();
        for (QuestionnaireVersion qNaireVersion : allVersions) {
          if (qNaireVersion.isActiveVersion()) {
            this.activeQnaireVersion = qNaireVersion;
            break;
          }
        }
        Long activeVersId = this.activeQnaireVersion == null ? null : this.activeQnaireVersion.getID();
        getLogger().debug("Active Questionnaire Version is : " + activeVersId);
      }
    }
    return this.activeQnaireVersion;
  }

  /**
   * @return isDeleted
   */
  public boolean isDeleted() {
    return getEntityProvider().getDbQuestionnaire(getID()).getDeletedFlag().equals(ApicConstants.YES);
  }

  // ICDM-1965
  /**
   * Get a sorted set of the PIDCs AccessRights
   *
   * @return access rights
   */
  public SortedSet<NodeAccessRight> getAccessRights() {
    return getApicDataProvider().getNodeAccessRights(getID());
  }

  /**
   * Get the current users access right on this PIDC
   *
   * @return The NodeAccessRight of the current user If the user has no special access rights return NULL
   */
  public NodeAccessRight getCurrentUserAccessRights() {
    return getApicDataProvider().getNodeAccRight(getID());
  }

  /**
   * Returns whether the logged in user has privilege to modify access rights to this Project ID Card.
   *
   * @return <code>true</code> if current user can modify the access rights.
   */
  public boolean canModifyAccessRights() {
    if (getApicDataProvider().getCurrentUser().hasApicWriteAccess()) {
      return true;
    }
    NodeAccessRight curUserAccRight = getCurrentUserAccessRights();
    if ((curUserAccRight != null) && curUserAccRight.hasGrantOption()) {
      return true;
    }
    return false;
  }

  /**
   * @return boolean if the user has the access to change the owner flag
   */
  public boolean canModifyOwnerRights() {
    final NodeAccessRight curUserAccRight = getCurrentUserAccessRights();
    if (((curUserAccRight != null) && curUserAccRight.isOwner()) ||
        getApicDataProvider().getCurrentUser().hasApicWriteAccess()) {
      return true;
    }

    return false;
  }

  /**
   * @return latest version
   */
  public QuestionnaireVersion getLatestVersion() {
    QuestionnaireVersion latestVersion = getWorkingSet();
    synchronized (this) {

      getLogger().debug("Finding latest version of Questionnaire : " + getID());
      SortedSet<QuestionnaireVersion> allVersions = getAllVersions();
      int numOfVers = allVersions.size();
      Long higheshMajor = 0L;
      Long highestMinor = 0L;
      // iterate and find out the highest major number
      for (QuestionnaireVersion qNaireVersion : allVersions) {
        if (ApicUtil.compareLong(qNaireVersion.getMajorVersionNum(), higheshMajor) > 0) {
          higheshMajor = qNaireVersion.getMajorVersionNum();
        }
      }
      // iterate and find out the highest major for the identified highest major number
      for (QuestionnaireVersion qNaireVersion : allVersions) {
        if (numOfVers == VERSION_SIZE_1) {
          latestVersion = qNaireVersion;
          break;
        }
        if (CommonUtils.isNull(qNaireVersion.getMinorVersionNum())) {
          continue;
        }
        else if (CommonUtils.isEqual(qNaireVersion.getMajorVersionNum(), higheshMajor) &&
            (ApicUtil.compareLong(qNaireVersion.getMinorVersionNum(), highestMinor) >= 0)) {
          highestMinor = qNaireVersion.getMinorVersionNum();
          latestVersion = qNaireVersion;
        }
      }

      getLogger().debug("Latest Questionnaire Version is : " + latestVersion.getID());
    }

    return latestVersion;
  }

  /**
   * @return the working set of a Questionnaire
   */
  public QuestionnaireVersion getWorkingSet() {
    SortedSet<QuestionnaireVersion> allVersions = getAllVersions();
    for (QuestionnaireVersion version : allVersions) {
      if ((version.getMajorVersionNum() == 0L) && (version.getMinorVersionNum() == null)) {
        // if major version is zero and minor version is null
        return version;
      }
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isModifiable() {
    if (getCurrentUserAccessRights() != null) {
      return getCurrentUserAccessRights().hasWriteAccess();
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, String> getObjectDetails() {
    final Map<String, String> objDetails = new HashMap<String, String>();
    objDetails.put(FLD_NAME_ENG, getNameEng());
    objDetails.put(FLD_NAME_GER, getNameGer());
    objDetails.put(FLD_DESC_ENG, getDescEng());
    objDetails.put(FLD_DESC_GER, getDescGer());

    return objDetails;
  }

  /**
   * Checks whether the questionnaire is 'General' type
   *
   * @return true, if this is a general questionnaire
   */
  // ICDM-2155
  public boolean isGeneralType() {
    // ICDM-2404
    return CommonUtils.isEqual(this, getDataCache().getGeneralQuestionnaire());
  }

}
