package com.bosch.caltool.icdm.bo.cdr.qnaire;

import java.text.ParseException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.general.LinkCommand;
import com.bosch.caltool.icdm.bo.general.LinkLoader;
import com.bosch.caltool.icdm.common.bo.qnaire.QnaireRespVersDataResolver;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionnaireVersion;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireAnswer;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireAnswerOpl;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireRespVersion;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireResponse;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswerOpl;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponseModel;
import com.bosch.caltool.icdm.model.general.Link;


/**
 * Command class for RvwQnaireRespVersion
 *
 * @author say8cob
 */
public class RvwQnaireRespVersionCommand extends AbstractCommand<RvwQnaireRespVersion, RvwQnaireRespVersionLoader> {

  private final boolean isToCreateOnQnaireRespCopy;
  private final boolean isToCreateBaselineOfWorkingSet;

  /**
   * Constructor to create a Qnaire Response Version
   *
   * @param input input data
   * @param serviceData service Data
   * @param isUpdate true if service call is to update
   * @param isDelete delete flag
   * @param isToCreateOnQnaireRespCopy true if service call is to create on Qnaire Resp copy
   * @param isToCreateBaselineOfWorkingSet true if service call is to create baseline of working set after paste
   * @throws IcdmException error when initializing
   */
  public RvwQnaireRespVersionCommand(final ServiceData serviceData, final RvwQnaireRespVersion input,
      final boolean isUpdate, final boolean isDelete, final boolean isToCreateOnQnaireRespCopy,
      final boolean isToCreateBaselineOfWorkingSet) throws IcdmException {

    super(serviceData, input, new RvwQnaireRespVersionLoader(serviceData), computeCommandMode(isUpdate, isDelete));

    this.isToCreateOnQnaireRespCopy = isToCreateOnQnaireRespCopy;
    this.isToCreateBaselineOfWorkingSet = isToCreateBaselineOfWorkingSet;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TRvwQnaireRespVersion entity = new TRvwQnaireRespVersion();

    entity.setName(getInputData().getVersionName());
    entity.setDescription(getInputData().getDescription());

    TRvwQnaireResponse qnaireResponse =
        new RvwQnaireResponseLoader(getServiceData()).getEntityObject(getInputData().getQnaireRespId());

    /*
     * 1)For copied WS, the rev num should be 0 - So checking whether the rev num is 0L but WS in Target is also 0L so
     * added one more check - checking the version name is not "Baseline of working set before paste". 2) If the
     * destination does not have same QuesResponse, then replica of source will be created in destination - in that case
     * isToCreateOnQnaireRespCopy = true and isToCreateBaselineOfWorkingSet = false. Else if already available in the
     * destination,then baselines in source should be copied to destination - in that case isToCreateOnQnaireRespCopy =
     * true and isToCreateBaselineOfWorkingSet = true. If any one of the above 2 cases passed, then same rev num as
     * input else New Revision number will be calculated in server side
     */
    Long newRevNumber = (CommonUtils.isEqual(getInputData().getRevNum(), CDRConstants.WORKING_SET_REV_NUM) &&
        (CommonUtils.isNotEqual(getInputData().getVersionName(), CommonUtilConstants.BASELINE_VERS_NAME_BFR_PASTE) &&
            CommonUtils.isNotEqual(getInputData().getVersionName(),
                CommonUtilConstants.BASELINE_VERS_NAME_BFR_QNAIRE_UPD) &&
            CommonUtils.isNotEqual(getInputData().getVersionName(),
                CommonUtilConstants.BASELINE_VERS_NAME_CRE_FROM_DATA_ASSMNT))) ||
        (this.isToCreateOnQnaireRespCopy && !this.isToCreateBaselineOfWorkingSet) ? getInputData().getRevNum()
            : getNewRevNumber(qnaireResponse);

    entity.setRevNum(newRevNumber);

    entity.setQnaireVersStatus(getInputData().getQnaireRespVersStatus());

    qnaireResponse.addTRvwQnaireRespVersion(entity);

    TQuestionnaireVersion tQuestionnaireVersion =
        new QuestionnaireVersionLoader(getServiceData()).getEntityObject(getInputData().getQnaireVersionId());
    entity.setTQuestionnaireVersion(tQuestionnaireVersion);

    try {
      entity.setReviewedDate(string2timestamp(getInputData().getReviewedDate()));
    }
    catch (ParseException e) {
      throw new IcdmException("Error while parsing reviewed date string");
    }
    entity.setReviewedUser(getInputData().getReviewedUser());
    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
    if ((newRevNumber != 0L) && !this.isToCreateOnQnaireRespCopy) {
      // create ques answers displayed in the UI
      createQuesAnswersEntities(entity);
    }
  }

  /**
   * @param entity
   * @throws IcdmException
   */
  private void createQuesAnswersEntities(final TRvwQnaireRespVersion entity) throws IcdmException {
    for (RvwQnaireAnswer existingQnaireAnswer : getAllQAnswers(entity).values()) {
      Question ques = new QuestionLoader(getServiceData()).getDataObjectByID(existingQnaireAnswer.getQuestionId());
      // To avoid Dummy Review Qnaire Answer object
      if (CommonUtils.isNotNull(existingQnaireAnswer.getQuestionId()) && !ques.getDeletedFlag()) {
        RvwQnaireAnswer qnaireAnswer = existingQnaireAnswer.clone();
        // iterate through the list of answers of ques response
        qnaireAnswer.setQnaireRespVersId(entity.getQnaireRespVersId());

        RvwQnaireAnswerCommand qAnsCommand =
            new RvwQnaireAnswerCommand(getServiceData(), qnaireAnswer, COMMAND_MODE.CREATE);
        executeChildCommand(qAnsCommand);
        TRvwQnaireAnswer existingTRvwQuesAns =
            new RvwQnaireAnswerLoader(getServiceData()).getEntityObject(qnaireAnswer.getId());
        Long createdAnswerId = qAnsCommand.getNewData().getId();

        // create DB entries for OPL
        if ((null != existingTRvwQuesAns) && (null != existingTRvwQuesAns.getTQnaireAnsOpenPoints())) {
          RvwQnaireAnswerOplLoader oplLoader = new RvwQnaireAnswerOplLoader(getServiceData());
          // collect ids of opl from dbRvwQuesAns
          Set<Long> oplIdSet = existingTRvwQuesAns.getTQnaireAnsOpenPoints().stream()
              .map(TRvwQnaireAnswerOpl::getOpenPointsId).collect(Collectors.toSet());
          // get all the opl linked to qnaire response answer
          Map<Long, RvwQnaireAnswerOpl> oplMap = oplLoader.getDataObjectByID(oplIdSet);
          for (RvwQnaireAnswerOpl opl : oplMap.values()) {
            opl.setRvwAnswerId(createdAnswerId);
            RvwQnaireAnswerOplCommand command =
                new RvwQnaireAnswerOplCommand(getServiceData(), opl, COMMAND_MODE.CREATE);
            executeChildCommand(command);
          }
        }

        // create DB entries for links associated with the answer
        Map<Long, Link> linksByNode = new LinkLoader(getServiceData()).getLinksByNode(existingQnaireAnswer.getId(),
            ApicConstants.RVW_QNAIRE_ANS_NODE_TYPE);
        for (Link existingLink : linksByNode.values()) {
          // iterate through the links attached to the rvw answer
          Link linkCopy = existingLink.clone();
          linkCopy.setNodeId(createdAnswerId);
          LinkCommand linkCommand = new LinkCommand(getServiceData(), linkCopy);
          executeChildCommand(linkCommand);
        }
      }
    }
  }

  /**
   * @return SortedSet<RvwQnaireAnswer>
   * @throws IcdmException
   */
  private Map<Long, RvwQnaireAnswer> getAllQAnswers(final TRvwQnaireRespVersion entity) throws IcdmException {
    RvwQnaireRespVersion qnaireRespVersWorkingSet = new RvwQnaireRespVersionLoader(getServiceData())
        .getQnaireRespVersWorkingSet(entity.getTRvwQnaireResponse().getQnaireRespId());
    RvwQnaireResponseModel qnaireRespModel =
        new RvwQnaireResponseLoader(getServiceData()).getQnaireResponseModel(qnaireRespVersWorkingSet.getId());
    QnaireRespVersDataResolver quesRespEvaluator = new QnaireRespVersDataResolver(
        new QuesRespDataProviderServer(getServiceData(), getInputData().getQnaireVersionId(), qnaireRespModel));
    quesRespEvaluator.loadMainQuestions(qnaireRespModel);
    return quesRespEvaluator.getAllQAMap();
  }

  /**
   * @param qnaireResponse
   * @return
   */
  private Long getNewRevNumber(final TRvwQnaireResponse qnaireResponse) {
    Long lastRevNum = 0L;

    if (CommonUtils.isNotEmpty(qnaireResponse.getTRvwQnaireRespVersions())) {
      for (TRvwQnaireRespVersion tRvwQnaireRespVersion : qnaireResponse.getTRvwQnaireRespVersions()) {
        if (tRvwQnaireRespVersion.getRevNum() > lastRevNum) {
          lastRevNum = tRvwQnaireRespVersion.getRevNum();
        }
      }
      lastRevNum += 1L;
    }
    return lastRevNum;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    TRvwQnaireRespVersion tRvwQnaireRespVersion =
        new RvwQnaireRespVersionLoader(getServiceData()).getEntityObject(getInputData().getId());

    // Updating Version Status
    tRvwQnaireRespVersion.setQnaireVersStatus(getInputData().getQnaireRespVersStatus());
    setUserDetails(COMMAND_MODE.UPDATE, tRvwQnaireRespVersion);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    TRvwQnaireRespVersion entity =
        new RvwQnaireRespVersionLoader(getServiceData()).getEntityObject(getInputData().getId());

    // delete all the review Qnaire answer mapped to the review Qnaire Resp version to be deleted
    delRvwQnaireAns(entity.getTRvwQnaireAnswers());
    // delete Qnaire resp version from set of rvw qnaire Version of rvwQnaireResp
    entity.getTQuestionnaireVersion().getTRvwQnaireRespVersions().remove(entity);
    entity.getTRvwQnaireResponse().getTRvwQnaireRespVersions().remove(entity);


    getEm().remove(entity);
  }

  /**
   * @param trvwQnaireAnswerSet set of review questionaire answer to be deleted
   * @throws IcdmException exception
   */
  public void delRvwQnaireAns(final Set<TRvwQnaireAnswer> trvwQnaireAnswerSet) throws IcdmException {

    for (TRvwQnaireAnswer trvwQnaireAnswer : trvwQnaireAnswerSet) {
      RvwQnaireAnswer rvwQnaireAnswer = new RvwQnaireAnswerLoader(getServiceData()).createDataObject(trvwQnaireAnswer);
      // delete links mapped to this answer
      deleteLinks(rvwQnaireAnswer.getId());
      // delete open points mapped to this answer
      for (Long trvwQnaireAnswerOplId : rvwQnaireAnswer.getOplId()) {
        RvwQnaireAnswerOpl rvwQnaireAnswerOpl =
            new RvwQnaireAnswerOplLoader(getServiceData()).getDataObjectByID(trvwQnaireAnswerOplId);
        RvwQnaireAnswerOplCommand rvwQnaireAnswerOplCommand =
            new RvwQnaireAnswerOplCommand(getServiceData(), rvwQnaireAnswerOpl, COMMAND_MODE.DELETE);
        executeChildCommand(rvwQnaireAnswerOplCommand);
      }
      executeChildCommand(new RvwQnaireAnswerCommand(getServiceData(), rvwQnaireAnswer, COMMAND_MODE.DELETE));
    }
  }

  private void deleteLinks(final Long qnaireAnsId) throws IcdmException {
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
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // Not Applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    // Not Applicable
  }

  private static COMMAND_MODE computeCommandMode(final boolean isUpdate, final boolean isDelete) {
    if (isDelete) {
      return COMMAND_MODE.DELETE;
    }
    return isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE;
  }
}
