/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TQuestion;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * @author jvi6cob
 */
public class QuestionnaireVersion extends AbstractCdrObject implements Comparable<QuestionnaireVersion> {

  /**
   * Sort columns for questionnaire versions
   *
   * @author mkl2cob
   */
  public enum SortColumns {
                           /**
                            * Version name
                            */
                           SORT_VERSION_NAME,
                           /**
                            * description
                            */
                           SORT_DESC,
                           /**
                            * version status
                            */
                           SORT_VERSION_STATUS,
                           /**
                            * active
                            */
                           SORT_ACTIVE,
                           /**
                            * created date
                            */
                           SORT_CREATED_DATE,
                           /**
                            * created user
                            */
                           SORT_CREATED_USER
  }

  public static final String FLD_ACTIVE = "ACTIVE_FLAG";

  public static final String FLD_DESC_ENG = "DESC_ENG";

  public static final String FLD_DESC_GER = "DESC_GER";

  private static final String FLD_MEASUREMNT_HIDDEN_FLAG = "MEASUREMNT_HIDDEN_FLAG";

  private static final String FLD_MEASUREMNT_REL_FLAG = "MEASUREMNT_REL_FLAG";

  private static final String FLD_SERIES_HIDDEN_FLAG = "SERIES_HIDDEN_FLAG";

  private static final String FLD_SERIES_REL_FLAG = "SERIES_REL_FLAG";

  private static final String FLD_LINK_HIDDEN_FLAG = "LINK_HIDDEN_FLAG";

  private static final String FLD_LINK_REL_FLAG = "LINK_REL_FLAG";

  private static final String FLD_RMRK_HIDDEN_FLAG = "RMRK_HIDDEN_FLAG";

  private static final String FLD_RMRK_REL_FLAG = "RMRK_REL_FLAG";

  private static final String FLD_OPN_HIDDEN_FLAG = "OPN_HIDDEN_FLAG";

  private static final String FLD_OPN_REL_FLAG = "OPN_REL_FLAG";

  private static final String FLD_RESULT_HIDDEN_FLAG = "RESULT_HIDDEN_FLAG";

  private static final String FLD_RESULT_REL_FLAG = "RESULT_REL_FLAG";

  private final SortedSet<Question> questionSet;

  /**
   * @param cdrDataProvider ApicDataProvider
   * @param qNaireVersID Long
   */
  public QuestionnaireVersion(final CDRDataProvider cdrDataProvider, final Long qNaireVersID) {
    super(cdrDataProvider, qNaireVersID);
    getDataCache().getAllQuestionnaireVersionMap().put(qNaireVersID, this);
    this.questionSet = new TreeSet<Question>();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbQuestionnaireVersion(getID()).getCreatedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbQuestionnaireVersion(getID()).getModifiedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbQuestionnaireVersion(getID()).getCreatedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbQuestionnaireVersion(getID()).getModifiedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return CDREntityType.QUESTIONNAIRE_VERSION;
  }

  /**
   * @return Major version number
   */
  public Long getMajorVersionNum() {
    return getEntityProvider().getDbQuestionnaireVersion(getID()).getMajorVersionNum();
  }


  /**
   * @return Major version number
   */
  public Long getMinorVersionNum() {
    return getEntityProvider().getDbQuestionnaireVersion(getID()).getMinorVersionNum();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return getQuestionnaire().getName() + " (" + getVersionName() + ")";
  }

  /**
   * @return version name
   */
  public String getVersionName() {
    Long majorVersionNum = getEntityProvider().getDbQuestionnaireVersion(getID()).getMajorVersionNum();
    Long minorVersionNum = getEntityProvider().getDbQuestionnaireVersion(getID()).getMinorVersionNum();

    if (CommonUtils.isEqual(majorVersionNum, 0L) &&
        (CommonUtils.isEqual(minorVersionNum, 0L) || CommonUtils.isEqual(minorVersionNum, null))) {
      // if major version num and minor version num is 0
      return "Working Set";
    }
    StringBuilder versionName = new StringBuilder("Version ");
    versionName.append(majorVersionNum);
    if (minorVersionNum != null) {
      versionName.append("." + minorVersionNum);
    }
    else {
      versionName.append("." + 0);// TODO: Check Is 0 the only major version ending?
    }
    return versionName.toString();
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
   * Returns the QuestionnaireVersion Description in ENGLISH
   *
   * @return QuestionnaireVersion English description in String
   */
  public String getDescEng() {
    String returnValue = getEntityProvider().getDbQuestionnaireVersion(getID()).getDescEng();
    if (returnValue == null) {
      returnValue = "";
    }
    return returnValue;
  }

  /**
   * Returns the QuestionnaireVersion Description in GERMAN
   *
   * @return QuestionnaireVersion German Description in String
   */
  public String getDescGer() {
    String returnValue = getEntityProvider().getDbQuestionnaireVersion(getID()).getDescGer();
    if (returnValue == null) {
      returnValue = "";
    }
    return returnValue;
  }

  /**
   * @return Questionnaire of this QuestionnaireVersion
   */
  public Questionnaire getQuestionnaire() {
    long qnaireId = getEntityProvider().getDbQuestionnaireVersion(getID()).getTQuestionnaire().getQnaireId();
    Questionnaire questionnaire = getDataCache().getQuestionnaire(qnaireId);
    if (questionnaire == null) {
      questionnaire = new Questionnaire(getDataProvider(), qnaireId);
    }
    return questionnaire;
  }

  /**
   * @return true if this is the active version of the Questionnaire
   */
  public boolean isActiveVersion() {
    return ApicConstants.YES.equals(getEntityProvider().getDbQuestionnaireVersion(getID()).getActiveFlag());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final QuestionnaireVersion qNaireVerToCmp) {
    int compResult = ApicUtil.compare(getVersion(), qNaireVerToCmp.getVersion());
    if (compResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      return ApicUtil.compare(getID(), qNaireVerToCmp.getID());
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
   * @return All the questions under the version
   */
  public SortedSet<Question> getAllQuestions() {
    if (this.questionSet.isEmpty()) {
      getLogger().debug("Loading Questions of QuestionnaireVersion : " + getID());
      Set<TQuestion> questionEntities = getEntityProvider().getDbQuestionnaireVersion(getID()).getTQuestions();
      List<Question> qBos = new ArrayList<>();
      Question questionBO;
      if (null != questionEntities) {
        for (TQuestion question : questionEntities) {
          questionBO = getDataCache().getQuestion(question.getQId());
          if (questionBO == null) {
            questionBO = new Question(getDataProvider(), question.getQId());
          }
          qBos.add(questionBO);
        }
      }
      this.questionSet.addAll(qBos);
      getLogger().debug("Total Questions count = " + this.questionSet.size());
    }

    return this.questionSet;
  }


  /**
   * @return the set of questions which are headings and at the first level .
   */
  public SortedSet<Question> getFirstLevelQuestions() {
    SortedSet<Question> firstLevel = new TreeSet<>();
    getLogger().debug("Loading first level questions of QuestionnaireVersion : " + getID());
    for (Question questionBO : getAllQuestions()) {
      if (null == questionBO.getParentQuestion()) {
        firstLevel.add(questionBO);
      }
    }
    getLogger().debug("Total first level questions count = " + firstLevel.size());


    return firstLevel;
  }

  /**
   * @param includeHeadings includeHeadings
   * @return All the questions under the version - if includeHeadings is true or return only questions which are not
   *         headings if the includeHeadings is false
   */
  public SortedSet<Question> getAllQuestions(final boolean includeHeadings) {
    SortedSet<Question> allQnsSet = getAllQuestions();
    SortedSet<Question> retSet = new TreeSet<>(allQnsSet);

    if (!includeHeadings) {
      for (Question qsn : allQnsSet) {
        if (qsn.isHeading()) {
          retSet.remove(qsn);
        }
      }
    }

    return retSet;
  }

  /**
   * @return true if the version is in work
   */
  public String isInWork() {
    return getEntityProvider().getDbQuestionnaireVersion(getID()).getInworkFlag();
  }

  /**
   * @return true if this version is a working set
   */
  public boolean isWorkingSet() {
    Long majorVersionNum = getEntityProvider().getDbQuestionnaireVersion(getID()).getMajorVersionNum();
    Long minorVersionNum = getEntityProvider().getDbQuestionnaireVersion(getID()).getMinorVersionNum();
    if (CommonUtils.isEqual(majorVersionNum, 0L) && (CommonUtils.isEqual(minorVersionNum, null))) {
      // if major version num and minor version num is 0
      return true;
    }
    return false;
  }

  public int compareTo(final QuestionnaireVersion quesVers2, final SortColumns sortColumn) {
    int compareResult;

    switch (sortColumn) {
      case SORT_VERSION_NAME:
        compareResult = ApicUtil.compare(getVersionName(), quesVers2.getVersionName());
        break;

      case SORT_DESC:
        compareResult = ApicUtil.compare(getDescription(), quesVers2.getDescription());
        break;

      case SORT_ACTIVE:
        compareResult = ApicUtil.compareBoolean(isActiveVersion(), quesVers2.isActiveVersion());
        break;

      case SORT_VERSION_STATUS:
        compareResult = ApicUtil.compare(isInWork(), quesVers2.isInWork());
        break;

      case SORT_CREATED_DATE:
        compareResult = ApicUtil.compare(getCreatedDate().getTime(), quesVers2.getCreatedDate().getTime());
        break;

      case SORT_CREATED_USER:
        compareResult = ApicUtil.compare(getCreatedUserDisplayName(), quesVers2.getCreatedUserDisplayName());
        break;

      default:
        compareResult = 0;
        break;
    }

    if (compareResult == 0) {
      // compare result is equal, compare the version name
      compareResult = ApicUtil.compare(getVersionName(), quesVers2.getVersionName());

    }

    return compareResult;
  }

  /**
   * @return the inwork flag
   */
  public String getInworkFlag() {
    return getEntityProvider().getDbQuestionnaireVersion(getID()).getInworkFlag();
  }

  /**
   * @return the inwork flag
   */
  public String getLinkHiddenFlag() {
    return getEntityProvider().getDbQuestionnaireVersion(getID()).getLinkHiddenFlag();
  }

  /**
   * @return the inwork flag
   */
  public String getLinkRelevantFlag() {
    return getEntityProvider().getDbQuestionnaireVersion(getID()).getLinkRelevantFlag();
  }

  /**
   * @return the inwork flag
   */
  public String getMeasurementHiddenFlag() {
    return getEntityProvider().getDbQuestionnaireVersion(getID()).getMeasurementHiddenFlag();
  }

  /**
   * @return the inwork flag
   */
  public String getMeasurementRelevantFlag() {
    return getEntityProvider().getDbQuestionnaireVersion(getID()).getMeasurementRelevantFlag();
  }

  /**
   * @return the inwork flag
   */
  public String getOpenPointsHiddenFlag() {
    return getEntityProvider().getDbQuestionnaireVersion(getID()).getOpenPointsHiddenFlag();
  }

  /**
   * @return the inwork flag
   */
  public String getOpenPointsRelevantFlag() {
    return getEntityProvider().getDbQuestionnaireVersion(getID()).getOpenPointsRelevantFlag();
  }

  /**
   * @return the inwork flag
   */
  public String getRemarkRelevantFlag() {
    return getEntityProvider().getDbQuestionnaireVersion(getID()).getRemarkRelevantFlag();
  }

  /**
   * @return the inwork flag
   */
  public String getRemarksHiddenFlag() {
    return getEntityProvider().getDbQuestionnaireVersion(getID()).getRemarksHiddenFlag();
  }


  /**
   * @return the inwork flag
   */
  public String getResultHiddenFlag() {
    return getEntityProvider().getDbQuestionnaireVersion(getID()).getResultHiddenFlag();
  }

  /**
   * @return the inwork flag
   */
  public String getResultRelevantFlag() {
    return getEntityProvider().getDbQuestionnaireVersion(getID()).getResultRelevantFlag();
  }

  /**
   * @return the inwork flag
   */
  public String getSeriesHiddenFlag() {
    return getEntityProvider().getDbQuestionnaireVersion(getID()).getSeriesHiddenFlag();
  }

  /**
   * @return the inwork flag
   */
  public String getSeriesRelevantFlag() {
    return getEntityProvider().getDbQuestionnaireVersion(getID()).getSeriesRelevantFlag();
  }

  /**
   * @return the active flag
   */
  public String getActiveFlag() {
    return getEntityProvider().getDbQuestionnaireVersion(getID()).getActiveFlag();
  }

  /**
   * @return MeasureHiddenFlag
   */
  public String getMeasureHiddenFlag() {
    return getEntityProvider().getDbQuestionnaireVersion(getID()).getMeasureHiddenFlag();
  }

  /**
   * @return MeasureRelaventFlag
   */
  public String getMeasureRelaventFlag() {
    return getEntityProvider().getDbQuestionnaireVersion(getID()).getMeasureRelaventFlag();
  }

  /**
   * @return ResponsibleRelaventFlag
   */
  public String getResponsibleRelaventFlag() {
    return getEntityProvider().getDbQuestionnaireVersion(getID()).getResponsibleRelaventFlag();
  }

  /**
   * @return ResponsibleHiddenFlag
   */
  public String getResponsibleHiddenFlag() {
    return getEntityProvider().getDbQuestionnaireVersion(getID()).getResponsibleHiddenFlag();
  }

  /**
   * @return CompletionDateHiddenFlag
   */
  public String getCompletionDateHiddenFlag() {
    return getEntityProvider().getDbQuestionnaireVersion(getID()).getCompletionDateHiddenFlag();
  }

  /**
   * @return CompletionDateRelaventFlag
   */
  public String getCompletionDateRelaventFlag() {
    return getEntityProvider().getDbQuestionnaireVersion(getID()).getCompletionDateRelaventFlag();
  }


  /**
   * Check, if the object can be modified by the current user
   *
   * @return TRUE if the object can be modified
   */
  @Override
  public boolean isModifiable() {
    if (null != getQuestionnaire().getCurrentUserAccessRights()) {
      return getQuestionnaire().getCurrentUserAccessRights().hasWriteAccess();
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, String> getObjectDetails() {

    final ConcurrentMap<String, String> objDetails = new ConcurrentHashMap<String, String>();

    objDetails.put(FLD_ACTIVE, isActiveVersion() ? ApicConstants.YES : ApicConstants.CODE_NO);
    objDetails.put(FLD_DESC_ENG, getDescEng());
    objDetails.put(FLD_DESC_GER, getDescGer());

    objDetails.put(FLD_MEASUREMNT_HIDDEN_FLAG, getMeasurementHiddenFlag());
    objDetails.put(FLD_MEASUREMNT_REL_FLAG, getMeasurementRelevantFlag());

    objDetails.put(FLD_SERIES_HIDDEN_FLAG, getMeasurementHiddenFlag());
    objDetails.put(FLD_SERIES_REL_FLAG, getMeasurementRelevantFlag());

    objDetails.put(FLD_LINK_HIDDEN_FLAG, getLinkHiddenFlag());
    objDetails.put(FLD_LINK_REL_FLAG, getLinkRelevantFlag());

    objDetails.put(FLD_RMRK_HIDDEN_FLAG, getRemarksHiddenFlag());
    objDetails.put(FLD_RMRK_REL_FLAG, getRemarkRelevantFlag());

    objDetails.put(FLD_OPN_HIDDEN_FLAG, getRemarksHiddenFlag());
    objDetails.put(FLD_OPN_REL_FLAG, getRemarkRelevantFlag());

    objDetails.put(FLD_RESULT_HIDDEN_FLAG, getResultHiddenFlag());
    objDetails.put(FLD_RESULT_REL_FLAG, getResultRelevantFlag());

    return objDetails;

  }

  /**
   * Checks whether the questionnaire is 'General' type
   *
   * @return true, if this is a general questionnaire's version
   */
  // ICDM-2155
  public boolean isGeneralType() {
    return getQuestionnaire().isGeneralType();
  }
}
