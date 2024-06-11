/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.qnaire;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import com.bosch.caltool.datamodel.core.cns.CHANGE_OPERATION;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.framework.AbstractClientDataHandler;
import com.bosch.caltool.icdm.client.bo.framework.ChangeDataInfo;
import com.bosch.caltool.icdm.client.bo.framework.CnsUtils;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireVersionModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireVersion;
import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroupNodeAccess;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.ws.rest.client.cdr.QnaireVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.QuestionnaireServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author apj4cob
 */
public class QnaireDefEditorDataHandler extends AbstractClientDataHandler {

  private final QnaireDefBO qnaireBo;

  /**
   * @param qnaireBo QnaireDefEditorDataHandler
   */
  public QnaireDefEditorDataHandler(final QnaireDefBO qnaireBo) {
    this.qnaireBo = qnaireBo;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void registerForCns() {
    registerCnsChecker(MODEL_TYPE.QUESTIONNAIRE_VERSION, this::isQnaireVersionChanged);
    registerCnsAction(this::refreshQVersionDataMap, MODEL_TYPE.QUESTIONNAIRE_VERSION);
    registerCnsChecker(MODEL_TYPE.QUESTION, this::isQuestionChanged);
    // For local changes
    registerCnsActionLocal(this::refreshQMapForLocal, MODEL_TYPE.QUESTION);
    // For remote changes
    registerCnsAction(this::refreshQMapForRemoteChng, MODEL_TYPE.QUESTION);
    registerNodeAccessChange();
  }


  private boolean isQuestionChanged(final ChangeData<?> chData) {
    return this.qnaireBo.isQuestionChanged(chData);
  }

  private boolean isQnaireVersionChanged(final ChangeData<?> chData) {
    return this.qnaireBo.isQnaireVersionChanged(chData);
  }

  /**
   * @param chDataInfoMap Map<Long, ChangeDataInfo>
   */
  public void refreshQVersionDataMap(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      if (data.getChangeType().equals(CHANGE_OPERATION.UPDATE) ||
          data.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
        updateQVersionMap();
        // update Questionnaire Version available from Bo in case there is any change from
        // QuestionnaireDetailsDialog;which is possible only for working set as only working set is editable
        updateQnaireVerInBo(data);
      }
    }
  }

  /**
   * @param data ChangeDataInfo
   */
  public void updateQnaireVerInBo(final ChangeDataInfo data) {
    if (data.getObjId() == this.qnaireBo.getQnaireVersion().getId().longValue()) {
      for (QuestionnaireVersion qVersion : this.qnaireBo.getAllVersions()) {
        if (this.qnaireBo.isWorkingSet(qVersion)) {
          this.qnaireBo.getQnaireDefModel().setQuestionnaireVersion(qVersion);
          break;
        }
      }
    }
  }

  /**
  *
  */
  private void registerNodeAccessChange() {
    registerCnsChecker(MODEL_TYPE.NODE_ACCESS, chData -> {
      Long nodeId = ((NodeAccess) CnsUtils.getModel(chData)).getNodeId();
      return nodeId.equals(this.qnaireBo.getQuestionnaire().getId());
    });
    registerCnsChecker(MODEL_TYPE.ACTIVE_DIRECTORY_GROUP_NODE_ACCES, chData -> {
      Long nodeId = ((ActiveDirectoryGroupNodeAccess) CnsUtils.getModel(chData)).getNodeId();
      return nodeId.equals(this.qnaireBo.getQuestionnaire().getId());
    });
  }

  /**
   * @param data ChangeDataInfo
   */
  private void updateQVersionMap() {
    try {
      SortedSet<QuestionnaireVersion> qnaireVesionSet =
          new QuestionnaireServiceClient().getAllVersions(this.qnaireBo.getQuestionnaire().getId());
      this.qnaireBo.getAllVersions().clear();
      this.qnaireBo.getAllVersions().addAll(qnaireVesionSet);
      // update Questionnaire available fro QnaireDefBo
      this.qnaireBo.getQnaireDefModel()
          .setQuestionnaire(new QuestionnaireServiceClient().get(this.qnaireBo.getQuestionnaire().getId()));
    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().error(ex.getMessage(), ex, Activator.PLUGIN_ID);
    }

  }

  /**
   * @param chData ChangeData
   */
  private void refreshQMapForLocal(final ChangeData<?> chData) {
    if (chData.getChangeType().equals(CHANGE_OPERATION.UPDATE)) {
      updateDataMapForEdit(chData);
    }
    if (chData.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
      updateDataMapForCreate(chData);
    }
  }

  /**
   * @param objId
   */
  private void updateDataMapForCreate(final ChangeData<?> chData) {
    QnaireVersionModel model;
    Question createdQues = (Question) chData.getNewData();
    // In case of level 1 questions do complete refresh
    if (CommonUtils.isNull(createdQues.getParentQId())) {
      try {
        this.qnaireBo.setQnaireDefModel(
            new QnaireVersionServiceClient().getQnaireVersionWithDetails(createdQues.getQnaireVersId()));
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }
    // For level 2 and 3 questions refresh entire parent child hierarchy for the corresponding super parent
    else {
      try {
        model = new QnaireVersionServiceClient().getQnaireVModelByQuesId(createdQues.getParentQId());
        this.qnaireBo.getQnaireDefModel().getQuestionMap().put(createdQues.getParentQId(),
            model.getQuestionMap().get(createdQues.getParentQId()));
        if (CommonUtils.isNotNull(model.getQuestionConfigMap().get(createdQues.getParentQId()))) {
          this.qnaireBo.getQnaireDefModel().getQuestionConfigMap().put(createdQues.getParentQId(),
              model.getQuestionConfigMap().get(createdQues.getParentQId()));
        }
        if (model.getQuesWithQuestionResultOptionsMap().get(createdQues.getId()) != null) {
          this.qnaireBo.getQnaireDefModel().getQuesWithQuestionResultOptionsMap().put(createdQues.getId(),
              model.getQuesWithQuestionResultOptionsMap().get(createdQues.getId()));
        }
        addToChildMap(model);
      }

      catch (ApicWebServiceException ex) {
        CDMLogger.getInstance().error(ex.getMessage(), ex, Activator.PLUGIN_ID);
      }
    }
  }


  /**
   * For local refresh
   *
   * @param chInfoMap Map<Long, ChangeDataInfo>
   */
  private void updateDataMapForEdit(final ChangeData<?> chData) {
    Long editedQuesId = chData.getObjId();
    Question editedQuest = (Question) chData.getNewData();
    // In case of level 1 questions do complete refresh
    if (CommonUtils.isNull(editedQuest.getParentQId()) ||
        CommonUtils.isNull(((Question) chData.getOldData()).getParentQId())) {
      try {
        this.qnaireBo.setQnaireDefModel(
            new QnaireVersionServiceClient().getQnaireVersionWithDetails(editedQuest.getQnaireVersId()));
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }
    // For level 2 and 3 questions refresh entire parent child hierarchy for the corresponding super parent
    else {
      QnaireVersionModel model = new QnaireVersionModel();
      try {
        Question quest = this.qnaireBo.getQnaireDefModel().getQuestionMap().get(editedQuesId);
        // Get super parent for refresh
        while (CommonUtils.isNotNull(this.qnaireBo.getQnaireDefModel().getQuestionMap().get(quest.getParentQId()))) {
          quest = this.qnaireBo.getQnaireDefModel().getQuestionMap().get(quest.getParentQId());
        }
        model = new QnaireVersionServiceClient().getQnaireVModelByQuesId(quest.getId());
        this.qnaireBo.getQnaireDefModel().getQuestionMap().put(quest.getId(),
            model.getQuestionMap().get(quest.getId()));
        if (CommonUtils.isNotNull(model.getQuestionConfigMap().get(quest.getId()))) {
          this.qnaireBo.getQnaireDefModel().getQuestionConfigMap().put(quest.getId(),
              model.getQuestionConfigMap().get(quest.getId()));
        }
        if (model.getQuesWithQuestionResultOptionsMap().get(editedQuesId) != null) {
          this.qnaireBo.getQnaireDefModel().getQuesWithQuestionResultOptionsMap().put(editedQuesId,
              model.getQuesWithQuestionResultOptionsMap().get(editedQuesId));
        }
        addToChildMap(model);
      }
      catch (ApicWebServiceException ex) {
        CDMLogger.getInstance().error(ex.getMessage(), ex, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * Adds the to child map.
   *
   * @param updatedQuestion the updated question
   */
  private void addToChildMap(final QnaireVersionModel updatedQuestionModel) {
    for (Map.Entry<Long, List<Long>> entry : updatedQuestionModel.getChildQuestionIdMap().entrySet()) {
      List<Long> childQuesIdList = updatedQuestionModel.getChildQuestionIdMap().get(entry.getKey());
      this.qnaireBo.getQnaireDefModel().getChildQuestionIdMap().put(entry.getKey(), childQuesIdList);
      // Update question map and question config map for each entry in child map
      for (Long childQuesId : childQuesIdList) {
        Question quest = updatedQuestionModel.getQuestionMap().get(childQuesId);
        this.qnaireBo.getQnaireDefModel().getQuestionMap().put(childQuesId, quest);
        this.qnaireBo.getQnaireDefModel().getQuestionConfigMap().put(childQuesId,
            updatedQuestionModel.getQuestionConfigMap().get(childQuesId));
        if (updatedQuestionModel.getQuesWithQuestionResultOptionsMap().get(childQuesId) != null) {
          this.qnaireBo.getQnaireDefModel().getQuesWithQuestionResultOptionsMap().put(childQuesId,
              updatedQuestionModel.getQuesWithQuestionResultOptionsMap().get(childQuesId));
        }
      }
    }
  }


  /**
   * @param chDataInfoMap Map<Long, ChangeDataInfo>
   */
  private void refreshQMapForRemoteChng(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    // Do complete refresh for remote change
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      if (data.getChangeType().equals(CHANGE_OPERATION.UPDATE) ||
          data.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
        try {
          this.qnaireBo.setQnaireDefModel(
              new QnaireVersionServiceClient().getQnaireVersionWithDetails(this.qnaireBo.getQnaireVersion().getId()));
        }
        catch (ApicWebServiceException ex) {
          CDMLogger.getInstance().error(ex.getMessage(), ex, Activator.PLUGIN_ID);
        }
      }
    }
  }
}
