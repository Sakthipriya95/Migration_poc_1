/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.qnaire;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.general.LinkCommand;
import com.bosch.caltool.icdm.bo.general.LinkLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireAnswer;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireAnswerOpl;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireRespVariant;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireResponse;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcQnaireInfo;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswerOpl;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;
import com.bosch.caltool.icdm.model.general.Link;

/**
 * @author apj4cob
 */
public class RvwQnaireResponseCommand extends AbstractCommand<RvwQnaireResponse, RvwQnaireResponseLoader> {


  /**
   * variable to store undeleted general questionnaire
   */
  private RvwQnaireResponse unDeletedGenQues;
  /**
   * variant or review result delete
   */
  private final boolean varAndRvwDelete;

  /**
   * Constructor
   *
   * @param serviceData service Data
   * @param inputData input Data
   * @param isUpdate is Update
   * @param isDelete is Delete
   * @param varAndRvwDelete set to true, if the command is invoked from variant/review result delete
   * @throws IcdmException as Exception
   */
  public RvwQnaireResponseCommand(final ServiceData serviceData, final RvwQnaireResponse inputData,
      final boolean isUpdate, final boolean isDelete, final boolean varAndRvwDelete) throws IcdmException {
    super(serviceData, inputData, new RvwQnaireResponseLoader(serviceData), resolveCommandModeA(isDelete, isUpdate));
    this.varAndRvwDelete = varAndRvwDelete;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TRvwQnaireResponse dbQnaireResp = new TRvwQnaireResponse();
    dbQnaireResp.setReviewedFlag(ApicConstants.CODE_NO);
    dbQnaireResp.setDeletedFlag(ApicConstants.CODE_NO);
    setUserDetails(COMMAND_MODE.CREATE, dbQnaireResp);
    persistEntity(dbQnaireResp);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    RvwQnaireResponseLoader rvwQnaireRespLoader = new RvwQnaireResponseLoader(getServiceData());
    TRvwQnaireResponse tRvwQnaireResponse = rvwQnaireRespLoader.getEntityObject(getInputData().getId());
    if (isObjectChanged(getInputData().isReviewed(), getOldData().isReviewed())) {
      tRvwQnaireResponse.setReviewedFlag(getInputData().isReviewed() ? ApicConstants.CODE_YES : ApicConstants.CODE_NO);
      tRvwQnaireResponse.setReviewedUser(getServiceData().getUsername());
      tRvwQnaireResponse.setReviewedDate(getCurrentTime());
    }
    // Start - adding the log message to track the deleting of general question issue
    // 629607 - General Questionnaires are deleted automatically without deleting review or variant.
    if (getInputData().isDeletedFlag()) {
      RvwQnaireResponse qnaireResponse = rvwQnaireRespLoader.getDataObjectByID(getInputData().getId());
      CDMLogger.getInstance().debug("Questionnaire Response  Name" + qnaireResponse.getName() + " Qnaire Resp Id " +
          qnaireResponse.getId() + " is soft deleted");
    }
    // End for logging

    tRvwQnaireResponse.setDeletedFlag(booleanToYorN(getInputData().isDeletedFlag()));
    // is gen ques required and gen ques is deleted , undelete gen questionnaire
    TRvwQnaireRespVariant primaryVariantEntity = rvwQnaireRespLoader.getPrimaryVariantEntity(tRvwQnaireResponse);
    long variantId = primaryVariantEntity.getTabvProjectVariant() == null ? ApicConstants.NO_VARIANT_ID
        : primaryVariantEntity.getTabvProjectVariant().getVariantId();
    long a2lWpId = primaryVariantEntity.gettA2lWorkPackage().getA2lWpId();
    long a2lRespId = primaryVariantEntity.gettA2lResponsibility().getA2lRespId();
    boolean genQuesReq = !rvwQnaireRespLoader.isGenQuesNotRequired(a2lWpId, a2lRespId, variantId);

    /*
     * If (a) genQuesReq = true (General questionnaire required) and (b) the current response is not that of General
     * questionnaire and (c) response is getting deleted (isDeletedFlag() = true) and (d) is not triggered from
     * variant/review result deletion, then un-delete the general questionnaire, if it is deleted
     */
    if (!this.varAndRvwDelete && getInputData().isDeletedFlag() &&
        (!getInputData().getName().contains(ApicConstants.GENERAL_QUESTIONS) ||
            !getInputData().getName().contains(ApicConstants.OBD_GENERAL_QUESTIONS)) &&
        genQuesReq) {
      // get the general questionnaire from pidc tree node
      PidcQnaireInfo qnaireInfo = rvwQnaireRespLoader.getPidcQnaireResponse(getInputData().getPidcVersId());

      Set<Long> qnaireRespIdSet = qnaireInfo.getVarRespWpQniareMap().get(variantId).get(a2lRespId).get(a2lWpId);
      // qnaireRespIdSet will have 'null' value due to simplified General Qnaireso remove it before applying filter
      qnaireRespIdSet.removeIf(Objects::isNull);

      Optional<Long> deletedGenQuesRespId = qnaireRespIdSet.stream()
          .filter(qnaireRespId -> CommonUtils.isNotEqual(ApicConstants.SIMP_QUES_RESP_ID, qnaireRespId))
          .filter(qnaireRespId -> (qnaireInfo.getRvwQnaireRespMap().get(qnaireRespId).getName()
              .contains(ApicConstants.OBD_GENERAL_QUESTIONS) ||
              qnaireInfo.getRvwQnaireRespMap().get(qnaireRespId).getName().contains(ApicConstants.GENERAL_QUESTIONS)) &&
              (qnaireInfo.getRvwQnaireRespMap().get(qnaireRespId).isDeletedFlag()))
          .findFirst();
      // To find if there is linked General Qnaire Resp
      Optional<Long> linkedGenQuesRespId = qnaireRespIdSet.stream()
          .filter(qnaireRespId -> CommonUtils.isNotEqual(ApicConstants.SIMP_QUES_RESP_ID, qnaireRespId))
          .filter(qnaireRespId -> (qnaireInfo.getRvwQnaireRespMap().get(qnaireRespId).getName()
              .contains(ApicConstants.OBD_GENERAL_QUESTIONS) ||
              qnaireInfo.getRvwQnaireRespMap().get(qnaireRespId).getName().contains(ApicConstants.GENERAL_QUESTIONS)) &&
              (!qnaireInfo.getRvwQnaireRespMap().get(qnaireRespId).isDeletedFlag()))
          .findFirst();
      // If there is linked General qnaire Resp do not undelete deleted General qnaire Resp
      if (deletedGenQuesRespId.isPresent() && !linkedGenQuesRespId.isPresent()) {
        // undelete general questionnaire
        RvwQnaireResponse deletedGenQuesResp = qnaireInfo.getRvwQnaireRespMap().get(deletedGenQuesRespId.get());
        deletedGenQuesResp.setDeletedFlag(false);
        RvwQnaireResponseCommand respCommand =
            new RvwQnaireResponseCommand(getServiceData(), deletedGenQuesResp, true, false, false);
        executeChildCommand(respCommand);
        setUnDeletedGenQues(respCommand.getNewData());
      }
    }

    setUserDetails(COMMAND_MODE.UPDATE, tRvwQnaireResponse);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    TRvwQnaireResponse tRvwQnaireResponse =
        new RvwQnaireResponseLoader(getServiceData()).getEntityObject(getInputData().getId());
    getEm().remove(tRvwQnaireResponse);
  }

  /**
   * @param inputDataSet set of review questionaire answer to be deleted
   * @throws IcdmException exception
   */
  public void delRvwQnaireAns(final SortedSet<RvwQnaireAnswer> inputDataSet) throws IcdmException {
    for (RvwQnaireAnswer inputData : inputDataSet) {
      executeChildCommand(new RvwQnaireAnswerCommand(getServiceData(), inputData, COMMAND_MODE.DELETE));
    }
  }

  private void deleteLinks(final TRvwQnaireAnswer ansEntity) throws IcdmException {
    Long qnaireAnsId = new RvwQnaireAnswerLoader(getServiceData()).createDataObject(ansEntity).getId();
    // store link objects to be deleted
    Map<Long, Link> links =
        new LinkLoader(getServiceData()).getLinksByNode(qnaireAnsId, ApicConstants.RVW_QNAIRE_ANS_NODE_TYPE);
    // delete the entity for link
    if (CommonUtils.isNotNull(links)) {
      for (Link delLink : links.values()) {
        executeChildCommand(new LinkCommand(getServiceData(), delLink, false));
      }
    }
  }

  /**
   * To delete open points list for a response
   *
   * @param tQnaireAnsOpenPoints
   * @throws IcdmException
   */
  private void deleteOpl(final Set<TRvwQnaireAnswerOpl> tQnaireAnsOpenPoints) throws IcdmException {
    // Set of opl object to be deleted
    SortedSet<RvwQnaireAnswerOpl> delOpl = new TreeSet<>();
    for (TRvwQnaireAnswerOpl oplEntity : tQnaireAnsOpenPoints) {
      delOpl.add(new RvwQnaireAnswerOplLoader(getServiceData()).createDataObject(oplEntity));
    }
    // delete the entity for opl
    for (RvwQnaireAnswerOpl oplObj : delOpl) {
      executeChildCommand(new RvwQnaireAnswerOplCommand(getServiceData(), oplObj, COMMAND_MODE.DELETE));
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    return isObjectChanged(getInputData().isDeletedFlag(), getOldData().isDeletedFlag()) ||
        isObjectChanged(getInputData().isReviewed(), getOldData().isReviewed()) ||
        isObjectChanged(getInputData().getA2lRespId(), getOldData().getA2lRespId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    // Implementation not provided
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // Implementation not provided
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }


  /**
   * @return the unDeletedGenQues
   */
  public RvwQnaireResponse getUnDeletedGenQues() {
    return this.unDeletedGenQues;
  }


  /**
   * @param unDeletedGenQues the unDeletedGenQues to set
   */
  public void setUnDeletedGenQues(final RvwQnaireResponse unDeletedGenQues) {
    this.unDeletedGenQues = unDeletedGenQues;
  }
}

