/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.qnaire;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.bosch.caltool.dmframework.bo.AbstractCommand.COMMAND_MODE;
import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.A2lResponsibilityLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWorkPackageLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.general.LinkCommand;
import com.bosch.caltool.icdm.bo.general.LinkLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireResponse;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespActionData;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespCopyData;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswerOpl;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVariant;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;
import com.bosch.caltool.icdm.model.general.Link;

/**
 * @author UKT1COB
 */
public class RvwQnaireRespCopyCommand extends AbstractSimpleCommand {

  private final QnaireRespCopyData qnaireRespCopyData;

  private QnaireRespActionData qnaireRespVersionUpdateData;

  private RvwQnaireResponse pastedQnaireResp;

  private final boolean isToCreate;

  private RvwQnaireResponse destGeneralQnaireRespAfterUpdate;

  private RvwQnaireRespVersion baselinedRvwQnaireRespVersion;

  private RvwQnaireRespVersion deletedRvwQnaireRespWSVersion;

  private final List<RvwQnaireRespVersion> copiedRvwQnaireRespVersionList = new ArrayList<>();

  private final List<RvwQnaireAnswer> copiedRvwQnaireAnswerList = new ArrayList<>();

  private final List<RvwQnaireAnswerOpl> copiedRvwQnaireAnswerOplList = new ArrayList<>();

  private final List<Link> copiedRvwQnaireAnswerLinkList = new ArrayList<>();

  private boolean isQnaireVersUpdate = false;

  private String currentQnaireVersionName;

  private String activeQnaireVersionName;

  private QuestionnaireVersion activeQnnaireVersion;

  private static final int MAX_LENGTH = 4000;

  /**
   * @param serviceData serviceData
   * @param qnaireRespActionData data needed for pasting
   * @param isToCreate true if sevice call is to create else update
   * @throws IcdmException exception
   */
  public RvwQnaireRespCopyCommand(final ServiceData serviceData, final QnaireRespCopyData qnaireRespActionData,
      final boolean isToCreate) throws IcdmException {
    super(serviceData);
    this.qnaireRespCopyData = qnaireRespActionData;
    this.isToCreate = isToCreate;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {
    if (!this.isQnaireVersUpdate) {
      executeCopyPasteAction();
    }
    else {
      updateQnaireRespToActiveVersion();
    }
  }

  /**
   * @throws IcdmException
   * @throws DataException
   */
  private void executeCopyPasteAction() throws IcdmException {

    if (this.isToCreate) {
      createReplicaOfCopiedQnaireRespInDest();
    }
    else {
      updateExistingQnaireRespWithCopiedQnaireResp();
    }

    // Handle 'General Questionnaire Equivalent' case
    RvwQnaireResponse destGeneralQuesResp = this.qnaireRespCopyData.getDestGeneralQuesResp();
    if (CommonUtils.isNotNull(destGeneralQuesResp)) {
      // soft delete the general questionnaire in the destination after copying questionnaire equivalent to general
      // questionnaire
      destGeneralQuesResp.setDeletedFlag(true);
      RvwQnaireResponseCommand cmd =
          new RvwQnaireResponseCommand(getServiceData(), destGeneralQuesResp, true, false, false);
      executeChildCommand(cmd);

      setDestGeneralQnaireRespAfterUpdate(cmd.getNewData());
    }
  }


  /**
   * Method to create replica of copied qnaire resp in destination resp-wp combination
   */
  private void createReplicaOfCopiedQnaireRespInDest() throws IcdmException {

    // copy properties of source Qnaire Resp and create new questionnaire response
    RvwQnaireResponse srcQniareResp = this.qnaireRespCopyData.getCopiedQnaireResp();
    RvwQnaireResponse destRvwQnaireResp = new RvwQnaireResponse();
    CommonUtils.shallowCopy(destRvwQnaireResp, srcQniareResp);
    destRvwQnaireResp.setA2lRespId(this.qnaireRespCopyData.getTargetRespId());
    destRvwQnaireResp.setA2lWpId(this.qnaireRespCopyData.getTargetWpId());
    destRvwQnaireResp.setPidcVersId(this.qnaireRespCopyData.getTargetPidcVersion().getId());

    // Handling NO-Variant case
    PidcVariant destPidcVar = this.qnaireRespCopyData.getTargetPidcVariant();
    Long destVariantId = CommonUtils.isNull(destPidcVar) ? 0L : destPidcVar.getId();
    if (destVariantId > 0) {
      destRvwQnaireResp.setVariantId(destVariantId);
    }
    RvwQnaireResponseCommand qnaireRespCmd =
        new RvwQnaireResponseCommand(getServiceData(), destRvwQnaireResp, false, false, false);
    executeChildCommand(qnaireRespCmd);

    Long createdQnaireRespId = qnaireRespCmd.getObjId();
    RvwQnaireRespVariant rvwQnaireRespVariant = new RvwQnaireRespVariant();
    rvwQnaireRespVariant.setPidcVersId(this.qnaireRespCopyData.getTargetPidcVersion().getId());
    rvwQnaireRespVariant.setQnaireRespId(createdQnaireRespId);
    rvwQnaireRespVariant.setVariantId(CommonUtils.isNotNull(this.qnaireRespCopyData.getTargetPidcVariant())
        ? this.qnaireRespCopyData.getTargetPidcVariant().getId() : null);

    rvwQnaireRespVariant.setA2lRespId(this.qnaireRespCopyData.getTargetRespId());
    rvwQnaireRespVariant.setA2lWpId(this.qnaireRespCopyData.getTargetWpId());

    RvwQnaireRespVariantCommand rvwQnaireRespVariantCommand =
        new RvwQnaireRespVariantCommand(getServiceData(), rvwQnaireRespVariant, false, false);
    executeChildCommand(rvwQnaireRespVariantCommand);

    copyQnaireRespVersions(srcQniareResp, createdQnaireRespId, false);
    setPastedQnaireResp(qnaireRespCmd.getNewData());
  }

  /**
   * @throws IcdmException
   */
  private void updateExistingQnaireRespWithCopiedQnaireResp() throws IcdmException {

    RvwQnaireResponse existingQnaireResp = this.qnaireRespCopyData.getExistingTargetQnaireResp();
    // undo delete
    if (this.qnaireRespCopyData.isUndoDelete()) {
      RvwQnaireResponseCommand respCommand =
          new RvwQnaireResponseCommand(getServiceData(), existingQnaireResp, true, false, false);
      executeChildCommand(respCommand);
    }
    // Creating Base line for working set
    RvwQnaireRespVersion existingRvwQnaireRespWkngSetVers =
        createBaselineofExistQnaireRespWS(this.qnaireRespCopyData.getExistingTargetQnaireResp());

    // Deleting the existing Questionnaire working set response version
    deleteExistingQnaireRespWS(existingRvwQnaireRespWkngSetVers);
    // forcing the execution because the dest working set should be deleted before copying working set of source
    getEm().flush();

    // Copy the copied qnaire resp Working set and baselines based on user preference
    copyQnaireRespVersions(this.qnaireRespCopyData.getCopiedQnaireResp(), existingQnaireResp.getId(), true);
    setPastedQnaireResp(existingQnaireResp);
  }

  /**
   * @param srcQniareResp
   * @param destQnaireRespId
   * @param isToCreateBaselineOfWorkingSet
   * @throws DataException
   * @throws IcdmException
   */
  private void copyQnaireRespVersions(final RvwQnaireResponse srcQniareResp, final Long destQnaireRespId,
      final boolean isToCreateBaselineOfWorkingSet)
      throws IcdmException {
    RvwQnaireRespVersionLoader rvwQnaireRespVersLoader = new RvwQnaireRespVersionLoader(getServiceData());
    // if copy only working set
    if (this.qnaireRespCopyData.isCopyOnlyWorkingSet()) {
      RvwQnaireRespVersion srcRvwQnaireRespVersion =
          rvwQnaireRespVersLoader.getQnaireRespVersWorkingSet(srcQniareResp.getId());
      copyQnaireRespVersAlongWithAnswerFromSrcToDest(destQnaireRespId, srcRvwQnaireRespVersion,
          isToCreateBaselineOfWorkingSet);
    }
    else {
      // if copy both working set and baselined
      List<RvwQnaireRespVersion> rvwQnaireRespVersList = rvwQnaireRespVersLoader
          .getQnaireRespVersionsByRespId(srcQniareResp.getId()).values().stream().collect(Collectors.toList());
      // sort in asc order
      rvwQnaireRespVersList.sort(Comparator.comparing(RvwQnaireRespVersion::getRevNum));

      for (RvwQnaireRespVersion srcRvwQnaireRespVersion : rvwQnaireRespVersList) {
        copyQnaireRespVersAlongWithAnswerFromSrcToDest(destQnaireRespId, srcRvwQnaireRespVersion,
            isToCreateBaselineOfWorkingSet);
      }
    }
  }

  /**
   * Method to copy properties from src to dest and create new RvwQnaire Resp Version along with Rvw Qnaire Answer
   *
   * @param destQnaireRespId
   * @param destRvwQnaireRespVersion
   * @param srcRvwQnaireRespVersion
   * @throws IcdmException
   */
  private void copyQnaireRespVersAlongWithAnswerFromSrcToDest(final Long destQnaireRespId,
      final RvwQnaireRespVersion srcRvwQnaireRespVersion, final boolean isToCreateBaselineOfWorkingSet)
      throws IcdmException {
    RvwQnaireRespVersion destRvwQnaireRespVersion = new RvwQnaireRespVersion();
    CommonUtils.shallowCopy(destRvwQnaireRespVersion, srcRvwQnaireRespVersion);
    destRvwQnaireRespVersion.setQnaireRespId(destQnaireRespId);

    String remarks = CommonUtils.isEqual(CDRConstants.WORKING_SET_REV_NUM, srcRvwQnaireRespVersion.getRevNum())
        ? "Working Set " : "Baseline ";
    String srcDescription = srcRvwQnaireRespVersion.getDescription();
    String oldRemark = CommonUtils.isNotNull(srcDescription) ? srcDescription : "";
    destRvwQnaireRespVersion.setDescription(CommonUtils.truncateTextToMaxLen(
        remarks + "copied from " + getSourceLocation() + " " + oldRemark, RvwQnaireRespCopyCommand.MAX_LENGTH));

    // Create Mode - update flag = false, delete flag = false
    RvwQnaireRespVersionCommand rvwQnaireRespVersionCommand = new RvwQnaireRespVersionCommand(getServiceData(),
        destRvwQnaireRespVersion, false, false, true, isToCreateBaselineOfWorkingSet);
    executeChildCommand(rvwQnaireRespVersionCommand);
    this.copiedRvwQnaireRespVersionList.add(rvwQnaireRespVersionCommand.getNewData());

    List<RvwQnaireAnswer> srcRvwQnaireAnsList = new RvwQnaireAnswerLoader(getServiceData()).getAll().values().stream()
        .filter(
            rvwQnaireAns -> CommonUtils.isEqual(srcRvwQnaireRespVersion.getId(), rvwQnaireAns.getQnaireRespVersId()))
        .collect(Collectors.toList());

    for (RvwQnaireAnswer srcRvwQnaireAns : srcRvwQnaireAnsList) {
      RvwQnaireAnswer destRvwQnaireAns = new RvwQnaireAnswer();
      CommonUtils.shallowCopy(destRvwQnaireAns, srcRvwQnaireAns);
      destRvwQnaireAns.setQnaireRespVersId(rvwQnaireRespVersionCommand.getObjId());

      RvwQnaireAnswerCommand rvwQnaireAnswerCommand =
          new RvwQnaireAnswerCommand(getServiceData(), destRvwQnaireAns, COMMAND_MODE.CREATE);
      executeChildCommand(rvwQnaireAnswerCommand);
      this.copiedRvwQnaireAnswerList.add(rvwQnaireAnswerCommand.getNewData());

      // open points
      List<RvwQnaireAnswerOpl> srcRvwQnaireAnswerOplList =
          new RvwQnaireAnswerOplLoader(getServiceData()).getAll().values().stream()
              .filter(rvwQnaireAnsOpl -> CommonUtils.isEqual(srcRvwQnaireAns.getId(), rvwQnaireAnsOpl.getRvwAnswerId()))
              .collect(Collectors.toList());
      for (RvwQnaireAnswerOpl srcRvwQnaireAnswerOpl : srcRvwQnaireAnswerOplList) {
        RvwQnaireAnswerOpl destRvwQnaireAnswerOpl = new RvwQnaireAnswerOpl();
        CommonUtils.shallowCopy(destRvwQnaireAnswerOpl, srcRvwQnaireAnswerOpl);
        destRvwQnaireAnswerOpl.setRvwAnswerId(rvwQnaireAnswerCommand.getObjId());

        RvwQnaireAnswerOplCommand rvwQnaireAnswerOplCommand =
            new RvwQnaireAnswerOplCommand(getServiceData(), destRvwQnaireAnswerOpl, COMMAND_MODE.CREATE);
        executeChildCommand(rvwQnaireAnswerOplCommand);
        this.copiedRvwQnaireAnswerOplList.add(rvwQnaireAnswerOplCommand.getNewData());
      }

      // Links
      List<Link> srcRvwAnsLinkList = new LinkLoader(getServiceData())
          .getLinksByNode(srcRvwQnaireAns.getId(), ApicConstants.RVW_QNAIRE_ANS_NODE_TYPE).values().stream()
          .collect(Collectors.toList());

      for (Link srcRvwAnsLink : srcRvwAnsLinkList) {
        Link destRvwAnsLink = new Link();
        CommonUtils.shallowCopy(destRvwAnsLink, srcRvwAnsLink);
        destRvwAnsLink.setNodeId(rvwQnaireAnswerCommand.getObjId());

        LinkCommand rvwAnsLinkCmd = new LinkCommand(getServiceData(), destRvwAnsLink);
        executeChildCommand(rvwAnsLinkCmd);
        this.copiedRvwQnaireAnswerLinkList.add(rvwAnsLinkCmd.getNewData());
      }
    }
  }


  /**
   * @param wsQnaireRespVersion
   * @throws DataException
   */
  private String getSourceLocation() throws DataException {
    String pidcVersName = new PidcVersionLoader(getServiceData())
        .getDataObjectByID(this.qnaireRespCopyData.getCopiedQnaireResp().getPidcVersId()).getName();
    Long variantId = this.qnaireRespCopyData.getCopiedQnaireResp().getVariantId();
    String pidcVarName = CommonUtils.isNotNull(variantId) && (variantId > 0)
        ? new PidcVariantLoader(getServiceData()).getDataObjectByID(variantId).getName() : "<NO-VARIANT>";
    String wpName = new A2lWorkPackageLoader(getServiceData())
        .getDataObjectByID(this.qnaireRespCopyData.getCopiedQnaireResp().getA2lWpId()).getName();
    String respName = new A2lResponsibilityLoader(getServiceData())
        .getDataObjectByID(this.qnaireRespCopyData.getCopiedQnaireResp().getA2lRespId()).getName();
    return "<" + pidcVersName + "/" + pidcVarName + "/" + respName + "/" + wpName + ">. ";
  }

  /**
   * @throws IcdmException
   */
  private void updateQnaireRespToActiveVersion() throws IcdmException {

    // Creating Base line for working set
    RvwQnaireRespVersion existingRvwQnaireRespWkngSetVers =
        createBaselineofExistQnaireRespWS(this.qnaireRespVersionUpdateData.getExistingTargetQnaireResp());

    // Take copy of existingRvwQnaireRespWkngSetVers before deleting
    RvwQnaireRespVersion updRvwQnaireRespVersion = new RvwQnaireRespVersion();
    CommonUtils.shallowCopy(updRvwQnaireRespVersion, existingRvwQnaireRespWkngSetVers);
    updRvwQnaireRespVersion.setQnaireVersionId(this.activeQnnaireVersion.getId());
    updRvwQnaireRespVersion.setDescription("Working Set created after updating to active qnaire version");

    // Deleting the existing Questionnaire working set response version
    deleteExistingQnaireRespWS(existingRvwQnaireRespWkngSetVers);

    // forcing the execution because the working set should be deleted before creating new working set with active
    // qnaire version
    getEm().flush();

    // Update Questionnaire resp with active version
    createWSWithActiveQnaireVersion(updRvwQnaireRespVersion);
  }

  /**
   * @return
   * @throws IcdmException
   */
  private RvwQnaireRespVersion createBaselineofExistQnaireRespWS(final RvwQnaireResponse existingTargetQnaireResp)
      throws IcdmException {
    // Create baseline of the existing qnaire resp working set before pasting
    RvwQnaireRespVersion existingRvwQnaireRespWkngSetVers =
        new RvwQnaireRespVersionLoader(getServiceData()).getQnaireRespVersWorkingSet(existingTargetQnaireResp.getId());

    if (this.isQnaireVersUpdate) {
      setActiveQnaireVersionName(existingRvwQnaireRespWkngSetVers);
      setCurrentQnaireRespVersName();
    }

    RvwQnaireRespVersion wsQnaireRespVersion = new RvwQnaireRespVersion();
    CommonUtils.shallowCopy(wsQnaireRespVersion, existingRvwQnaireRespWkngSetVers);

    wsQnaireRespVersion.setVersionName(this.isQnaireVersUpdate ? "Baseline of working set before qnaire update"
        : "Baseline of working set before paste");
    wsQnaireRespVersion.setDescription(this.isQnaireVersUpdate
        ? "Baseline has been created automatically before updating Questionnaire response version from: " +
            this.currentQnaireVersionName + " to active Qnaire version: " + this.activeQnaireVersionName
        : "Baseline has been created automatically before pasting contents of " + getSourceLocation());
    wsQnaireRespVersion.setReviewedUser("DGS_ICDM");
    wsQnaireRespVersion.setReviewedDate(DateFormat.formatDateToString(new Date(), DateFormat.DATE_FORMAT_15));

    RvwQnaireRespVersionCommand qnaireRespVersionCommand =
        new RvwQnaireRespVersionCommand(getServiceData(), wsQnaireRespVersion, false, false, false, true);
    executeChildCommand(qnaireRespVersionCommand);
    setBaselinedRvwQnaireRespVersion(qnaireRespVersionCommand.getNewData());
    return existingRvwQnaireRespWkngSetVers;
  }

  /**
   *
   */
  private void setCurrentQnaireRespVersName() {

    RvwQnaireResponseLoader rvwQnaireResponseLoader = new RvwQnaireResponseLoader(getServiceData());
    TRvwQnaireResponse tRvwQnaireResponse =
        rvwQnaireResponseLoader.getEntityObject(getQnaireRespVersionUpdateData().getExistingTargetQnaireResp().getId());
    // Fetching current qnaire version name
    this.currentQnaireVersionName = rvwQnaireResponseLoader.getQnaireRespName(tRvwQnaireResponse);
  }


  /**
   * @throws DataException
   */
  private void setActiveQnaireVersionName(final RvwQnaireRespVersion existingRvwQnaireRespWkngSetVers)
      throws DataException {

    // fetching active qnaire id
    Long qnaireId =
        new RvwQnaireRespVersionLoader(getServiceData()).getEntityObject(existingRvwQnaireRespWkngSetVers.getId())
            .getTQuestionnaireVersion().getTQuestionnaire().getQnaireId();
    this.activeQnnaireVersion = new QuestionnaireVersionLoader(getServiceData()).getActiveQnnaireVersion(qnaireId);
    // Fetching active qnaire version name
    this.activeQnaireVersionName = getActiveQnaireVersionName(this.activeQnnaireVersion);
  }

  /**
   * @param existingRvwQnaireRespWkngSetVers
   * @throws IcdmException
   */
  private void deleteExistingQnaireRespWS(final RvwQnaireRespVersion existingRvwQnaireRespWkngSetVers)
      throws IcdmException {
    // After creating baseline from the Working set, delete that working set
    RvwQnaireRespVersionCommand rvwQnaireRespVersionCommand =
        new RvwQnaireRespVersionCommand(getServiceData(), existingRvwQnaireRespWkngSetVers, false, true, false, false);
    executeChildCommand(rvwQnaireRespVersionCommand);
    setDeletedRvwQnaireRespWSVersion(existingRvwQnaireRespWkngSetVers);
  }

  /**
   * @param activeQnaireVersion QuestionnaireVersion
   * @return qnaireVersionName
   */
  private String getActiveQnaireVersionName(final QuestionnaireVersion activeQnaireVersion) {
    StringBuilder qnaireVersionName = new StringBuilder();
    qnaireVersionName.append(" (Version ");

    qnaireVersionName.append(activeQnaireVersion.getMajorVersionNum());
    if (CommonUtils.isNotNull(activeQnaireVersion.getMinorVersionNum())) {
      qnaireVersionName.append(".");
      qnaireVersionName.append(activeQnaireVersion.getMinorVersionNum());
    }
    qnaireVersionName.append(")");

    return qnaireVersionName.toString();

  }

  /**
   * Create Questionnaire response with active version
   *
   * @param existingRvwQnaireRespWkngSetVers
   * @throws IcdmException IcdmException
   */
  private void createWSWithActiveQnaireVersion(final RvwQnaireRespVersion updRvwQnaireRespVersion)
      throws IcdmException {

    // Create Mode - update flag = false, delete flag = false
    RvwQnaireRespVersionCommand rvwQnaireRespVersionCommand =
        new RvwQnaireRespVersionCommand(getServiceData(), updRvwQnaireRespVersion, false, false, true, true);
    executeChildCommand(rvwQnaireRespVersionCommand);
    this.copiedRvwQnaireRespVersionList.add(rvwQnaireRespVersionCommand.getNewData());
  }

  /**
   * @return the pastedQnaireResp
   */
  public RvwQnaireResponse getPastedQnaireResp() {
    return this.pastedQnaireResp;
  }


  /**
   * @param pastedQnaireResp the pastedQnaireResp to set
   */
  public void setPastedQnaireResp(final RvwQnaireResponse pastedQnaireResp) {
    this.pastedQnaireResp = pastedQnaireResp;
  }


  /**
   * @return the destGeneralQnaireRespAfterUpdate
   */
  public RvwQnaireResponse getDestGeneralQnaireRespAfterUpdate() {
    return this.destGeneralQnaireRespAfterUpdate;
  }


  /**
   * @param destGeneralQnaireRespAfterUpdate the destGeneralQnaireRespAfterUpdate to set
   */
  public void setDestGeneralQnaireRespAfterUpdate(final RvwQnaireResponse destGeneralQnaireRespAfterUpdate) {
    this.destGeneralQnaireRespAfterUpdate = destGeneralQnaireRespAfterUpdate;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // NA
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }


  /**
   * @return the baselinedRvwQnaireRespVersion
   */
  public RvwQnaireRespVersion getBaselinedRvwQnaireRespVersion() {
    return this.baselinedRvwQnaireRespVersion;
  }


  /**
   * @param baselinedRvwQnaireRespVersion the baselinedRvwQnaireRespVersion to set
   */
  public void setBaselinedRvwQnaireRespVersion(final RvwQnaireRespVersion baselinedRvwQnaireRespVersion) {
    this.baselinedRvwQnaireRespVersion = baselinedRvwQnaireRespVersion;
  }


  /**
   * @return the deletedRvwQnaireRespWSVersion
   */
  public RvwQnaireRespVersion getDeletedRvwQnaireRespWSVersion() {
    return this.deletedRvwQnaireRespWSVersion;
  }


  /**
   * @param deletedRvwQnaireRespWSVersion the deletedRvwQnaireRespWSVersion to set
   */
  public void setDeletedRvwQnaireRespWSVersion(final RvwQnaireRespVersion deletedRvwQnaireRespWSVersion) {
    this.deletedRvwQnaireRespWSVersion = deletedRvwQnaireRespWSVersion;
  }


  /**
   * @return the copiedRvwQnaireRespVersionList
   */
  public List<RvwQnaireRespVersion> getCopiedRvwQnaireRespVersionList() {
    return this.copiedRvwQnaireRespVersionList;
  }


  /**
   * @return the copiedRvwQnaireAnswerList
   */
  public List<RvwQnaireAnswer> getCopiedRvwQnaireAnswerList() {
    return this.copiedRvwQnaireAnswerList;
  }


  /**
   * @return the copiedRvwQnaireAnswerOplList
   */
  public List<RvwQnaireAnswerOpl> getCopiedRvwQnaireAnswerOplList() {
    return this.copiedRvwQnaireAnswerOplList;
  }


  /**
   * @return the copiedRvwQnaireAnswerLinkList
   */
  public List<Link> getCopiedRvwQnaireAnswerLinkList() {
    return this.copiedRvwQnaireAnswerLinkList;
  }


  /**
   * @return the isQuesUpdate
   */
  public boolean isQuesUpdate() {
    return this.isQnaireVersUpdate;
  }


  /**
   * @param isQuesUpdate the isQuesUpdate to set
   */
  public void setQuesUpdate(final boolean isQuesUpdate) {
    this.isQnaireVersUpdate = isQuesUpdate;
  }


  /**
   * @return the qnaireRespVersionUpdateData
   */
  public QnaireRespActionData getQnaireRespVersionUpdateData() {
    return this.qnaireRespVersionUpdateData;
  }


  /**
   * @param qnaireRespVersionUpdateData the qnaireRespVersionUpdateData to set
   */
  public void setQnaireRespVersionUpdateData(final QnaireRespActionData qnaireRespVersionUpdateData) {
    this.qnaireRespVersionUpdateData = qnaireRespVersionUpdateData;
  }

}