/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.qnaire;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.wp.WorkPackageDivisionLoader;
import com.bosch.caltool.icdm.bo.wp.WorkPkgLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TQuestion;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionnaire;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionnaireVersion;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.qnaire.Questionnaire;
import com.bosch.caltool.icdm.model.wp.WorkPackageDivision;
import com.bosch.caltool.icdm.model.wp.WorkPkg;


/**
 * Loader class for Questionnaire
 *
 * @author bru2cob
 */
public class QuestionnaireLoader extends AbstractBusinessObject<Questionnaire, TQuestionnaire> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public QuestionnaireLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.QUESTIONNAIRE, TQuestionnaire.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Questionnaire createDataObject(final TQuestionnaire entity) throws DataException {
    Questionnaire object = new Questionnaire();

    setCommonFields(object, entity);

    String localWpNameEng = null;
    WorkPackageDivision wpDiv = null;
    if (entity.getTWorkpackageDivision() != null) {
      object.setWpDivId(entity.getTWorkpackageDivision().getWpDivId());
      wpDiv = new WorkPackageDivisionLoader(getServiceData()).getDataObjectByID(object.getWpDivId());
      object.setDivName(wpDiv.getDivName());
    }
    String localNameEng = entity.getNameEng();
    if (CommonUtils.isEmptyString(localNameEng) && (wpDiv != null)) {
      object.setName(wpDiv.getName());
      object.setNameSimple(wpDiv.getWpName());
      object.setDescription(wpDiv.getDescription());

      WorkPkg wrkPkg = new WorkPkgLoader(getServiceData())
          .getDataObjectByID(entity.getTWorkpackageDivision().getTWorkpackage().getWpId());
      localWpNameEng = wrkPkg.getWpNameEng();
      object.setNameEng(localWpNameEng);
      object.setNameGer(wrkPkg.getWpNameGer());
      object.setDescEng(wrkPkg.getWpDescEng());
      object.setDescGer(wrkPkg.getWpDescGer());
    }
    else {
      object.setName(getLangSpecTxt(entity.getNameEng(), entity.getNameGer()));
      object.setNameSimple(object.getName());
      object.setNameEng(entity.getNameEng());
      object.setNameGer(entity.getNameGer());

      object.setDescription(getLangSpecTxt(entity.getDescEng(), entity.getDescGer()));
      object.setDescEng(entity.getDescEng());
      object.setDescGer(entity.getDescGer());
    }

    if ((object.getDescEng() == null) && (entity.getDescEng() != null)) {
      object.setDescEng(entity.getDescEng());
    }
    else if ((entity.getDescEng() == null) && (localWpNameEng != null)) {
      object.setDescEng(localWpNameEng);
    }

    object.setDeleted(yOrNToBoolean(entity.getDeletedFlag()));

    return object;
  }

  /**
   * Get all Questionnaire records in system
   *
   * @param includeDeleted if true, deleted records are also returned
   * @param includeQnaireWithoutQues includeQnaireWithoutQuestions
   * @return Map. Key - id, Value - Questionnaire object
   * @throws DataException error while retrieving data
   */
  public Map<Long, Questionnaire> getAll(final boolean includeDeleted, final boolean includeQnaireWithoutQues)
      throws DataException {
    Map<Long, Questionnaire> objMap = new ConcurrentHashMap<>();
    TypedQuery<TQuestionnaire> tQuery = getEntMgr().createNamedQuery(TQuestionnaire.GET_ALL, TQuestionnaire.class);
    List<TQuestionnaire> dbObj = tQuery.getResultList();
    for (TQuestionnaire entity : dbObj) {
      Questionnaire qnaire = createDataObject(entity);
      if ((includeDeleted || !qnaire.getDeleted()) && (includeQnaireWithoutQues || isQuestionAvailable(entity))) {
        objMap.put(entity.getQnaireId(), qnaire);
      }
    }
    return objMap;
  }

  /**
   * return true if active qnaire version has atleast one question
   */
  private boolean isQuestionAvailable(final TQuestionnaire entity) {
    boolean isQuesAvailable = false;
    Set<TQuestionnaireVersion> tQuestionnaireVersionSet = entity.getTQuestionnaireVersions();
    for (TQuestionnaireVersion tQuestionnaireVersion : tQuestionnaireVersionSet) {
      if (ApicConstants.CODE_YES.equals(tQuestionnaireVersion.getActiveFlag())) {
        Set<TQuestion> tQuestSet = tQuestionnaireVersion.getTQuestions();
        if (CommonUtils.isNotEmpty(tQuestSet)) {
          isQuesAvailable = true;
          break;
        }
      }
    }
    return isQuesAvailable;
  }
}
