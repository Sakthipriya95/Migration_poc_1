/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.icdm.model.cdr.qnaire.Questionnaire;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireVersion;
import com.bosch.caltool.icdm.model.wp.WorkPkg;

/**
 * @author say8cob
 */
public class RvwQnaireWPTypesModel {

  private final Set<WorkPkg> existngWpList = new HashSet<>();
  private final Set<WorkPkg> allDivWpsWithQn = new HashSet<>();
  private final Set<WorkPkg> divWpsWithoutQn = new HashSet<>();
  private final SortedSet<QuestionnaireVersion> qsWithActiveVersions = new TreeSet<>();
  private final SortedSet<Questionnaire> qsWithoutActiveVersions = new TreeSet<>();
  private final List<WorkPkg> qsToBeCreated = new ArrayList<>();
  private final List<Long> wpDivsWithQs = new ArrayList<>();
  private final SortedSet<WorkPkg> reviewQnaireList = new TreeSet<>();
  private SortedSet<WorkPkg> questionnaireWps = new TreeSet<>();
  private Long divisionId;


  /**
   * @return the divisionId
   */
  public Long getDivisionId() {
    return this.divisionId;
  }


  /**
   * @param divisionId the divisionId to set
   */
  public void setDivisionId(final Long divisionId) {
    this.divisionId = divisionId;
  }


  /**
   * @return the reviewQnaireList
   */
  public SortedSet<WorkPkg> getQuestionnairesInUse() {
    return this.reviewQnaireList;
  }


  /**
   * @return the questionnaireWps
   */
  public SortedSet<WorkPkg> getQuestionnaireWps() {
    return this.questionnaireWps;
  }


  /**
   * @param questionnaireWps the questionnaireWps to set
   */
  public void setQuestionnaireWps(final SortedSet<WorkPkg> questionnaireWps) {
    this.questionnaireWps = questionnaireWps == null ? new TreeSet<>() : new TreeSet<>(questionnaireWps);
  }

  /**
   * @return the existngWpList
   */
  public Set<WorkPkg> getExistngWpList() {
    return this.existngWpList;
  }

  /**
   * @return the allDivWpsWithQn
   */
  public Set<WorkPkg> getAllDivWpsWithQn() {
    return this.allDivWpsWithQn;
  }

  /**
   * @return the divWpsWithoutQn
   */
  public Set<WorkPkg> getDivWpsWithoutQn() {
    return this.divWpsWithoutQn;
  }

  /**
   * @return the qsWithActiveVersions
   */
  public SortedSet<QuestionnaireVersion> getQsWithActiveVersions() {
    return this.qsWithActiveVersions;
  }

  /**
   * @return the qsWithoutActiveVersions
   */
  public SortedSet<Questionnaire> getQsWithoutActiveVersions() {
    return this.qsWithoutActiveVersions;
  }

  /**
   * @return the qsToBeCreated
   */
  public List<WorkPkg> getQsToBeCreated() {
    return this.qsToBeCreated;
  }

  /**
   * @return the wpDivsWithQs
   */
  public List<Long> getWpDivsWithQs() {
    return this.wpDivsWithQs;
  }


}
