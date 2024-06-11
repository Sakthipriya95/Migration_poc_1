/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.qnaire;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.user.NodeAccessLoader;
import com.bosch.caltool.icdm.bo.wp.WorkPackageDivisionLoader;
import com.bosch.caltool.icdm.bo.wp.WorkPkgLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.cdr.RvwQnaireWPTypesModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireCreationModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespCreationModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespUpdationModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespVarRespWpLink;
import com.bosch.caltool.icdm.model.cdr.qnaire.Questionnaire;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVariant;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;
import com.bosch.caltool.icdm.model.general.DataCreationModel;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.wp.WorkPackageDivision;
import com.bosch.caltool.icdm.model.wp.WorkPkg;

/**
 * @author dmr1cob
 */
public class RvwQnaireRespUpdationCommand extends AbstractSimpleCommand {

  private final QnaireRespUpdationModel inputData;

  /**
   * @param serviceData service data
   * @param inputData {@link QnaireRespUpdationModel}
   * @throws IcdmException exception in service call
   */
  public RvwQnaireRespUpdationCommand(final ServiceData serviceData, final QnaireRespUpdationModel inputData)
      throws IcdmException {
    super(serviceData);
    this.inputData = inputData;
  }

  /**
   * {@inheritDoc}
   */
  private void createRvwQnaireRespFormWorkpackage() throws IcdmException {

    // Update existing qnaire response
    for (RvwQnaireResponse qnaireResp : this.inputData.getOldQnaireRespSet()) {
      RvwQnaireResponseCommand qnaireRespCmd =
          new RvwQnaireResponseCommand(getServiceData(), qnaireResp, true, false, false);
      executeChildCommand(qnaireRespCmd);
      this.inputData.getUpdatedQnaireRespMap().put(qnaireRespCmd.getNewData().getId(), qnaireRespCmd.getNewData());
    }

    // Create Qnaire response from selected work packages
    if (!this.inputData.getWorkPkgSet().isEmpty()) {
      Set<Long> qnaireIdFromWpId = getQnaireIdFromWpId();
      Long pidcVersId = this.inputData.getPidcVersionId();
      for (Long qnaireId : qnaireIdFromWpId) {
        QuestionnaireVersion activeQnnaireVersion =
            new QuestionnaireVersionLoader(getServiceData()).getActiveQnnaireVersion(qnaireId);

        QnaireRespCreationModel qnaireRespCreationModel = new QnaireRespCreationModel();
        qnaireRespCreationModel.setPidcVersionId(pidcVersId);
        qnaireRespCreationModel.setPidcVariantId(this.inputData.getPidcVariantId());
        qnaireRespCreationModel.setQnaireVersId(activeQnnaireVersion.getId());
        qnaireRespCreationModel.setSelRespId(this.inputData.getSelRespId());
        qnaireRespCreationModel.setSelWpId(this.inputData.getSelWpId());
        RvwQnaireRespCreationCommand rvwQnaireRespCreationCommand =
            new RvwQnaireRespCreationCommand(getServiceData(), qnaireRespCreationModel, null);
        executeChildCommand(rvwQnaireRespCreationCommand);

        if (ApicConstants.CODE_YES.equals(activeQnnaireVersion.getGenQuesEquivalent())) {
          // add the questionnaire names to the set if the questionnaire version is General Qustnaire not required type
          this.inputData.getGenQuesNotReqQues().add(activeQnnaireVersion.getName());
        }
        this.inputData.getNewQnaireRespSet().add(rvwQnaireRespCreationCommand.getRvwQnaireResponse());
      }
    }

    // Create/delete variant linking
    if (!this.inputData.getQnaireRespVarLinkSet().isEmpty()) {
      createAndDeleteQnaireRespLinks();
    }
  }

  /**
   * @throws IcdmException
   */
  private void createAndDeleteQnaireRespLinks() throws IcdmException {
    for (QnaireRespVarRespWpLink qnaireRespVariantLink : this.inputData.getQnaireRespVarLinkSet()) {
      // returns qnaire response if there are qnaire response with same name during linking or else it will returns
      // null
      RvwQnaireResponseLoader rvwQnaireResponseLoader = new RvwQnaireResponseLoader(getServiceData());
      RvwQnaireResponse qnaireResponse = null;
      Long qnaireRespVarIdToDel = null;
      List<BigDecimal> qnaireRespIds = rvwQnaireResponseLoader.checkForQnaireWithSameNameWhenVarLinking(
          qnaireRespVariantLink.getPidcVariant(), qnaireRespVariantLink.getSelQnaireRespId(),
          qnaireRespVariantLink.getA2lResponsibility().getId(), qnaireRespVariantLink.getA2lWorkPackage().getId());
      if (CommonUtils.isNotEmpty(qnaireRespIds)) {
        if (qnaireRespIds.size() == 1) {
          qnaireResponse = rvwQnaireResponseLoader.getDataObjectByID((qnaireRespIds.get(0)).longValue());
        }
        // If size > 0, there is a Qnaire Response already linked
        // Get the already linked Qnaire Response Variant Id to delete
        else {
          qnaireRespVarIdToDel = rvwQnaireResponseLoader.getQnaireRespVarIdForUnlink((qnaireRespIds.get(0)).longValue(),
              (qnaireRespIds.get(1)).longValue(), qnaireRespVariantLink.getPidcVariant(),
              qnaireRespVariantLink.getA2lResponsibility().getId(), qnaireRespVariantLink.getA2lWorkPackage().getId());
        }
      }
      if (qnaireRespVariantLink.isLinked()) {
        if (CommonUtils.isNotNull(qnaireRespVarIdToDel)) {
          // If already Qnaire Response is Linked, unlink the previously linked Qnaire Response
          // And link the current combination
          deleteQnaireRespVariant(qnaireRespVarIdToDel);
          createQnaireRespVariant(qnaireRespVariantLink);
        }
        else {
          createQnaireRespVariant(qnaireRespVariantLink);
          // to mark the idetified similar name qnaire response as deleted during linking
          updateQnaireRespDeletedFlag(qnaireResponse, true);
        }
      }
      else {
        deleteQnaireRespVariant(qnaireRespVariantLink.getQnaireRespVarIdToDel());
        // to mark the idetified similar name qnaire response as un deleted during un-linking
        updateQnaireRespDeletedFlag(qnaireResponse, false);
      }
    }
  }

  /**
   * @param qnaireResponse
   * @throws IcdmException
   */
  private void updateQnaireRespDeletedFlag(final RvwQnaireResponse qnaireResponse, final boolean deletedFlag)
      throws IcdmException {
    if (qnaireResponse != null) {
      this.inputData.getOldQnaireRespSet().add(qnaireResponse);
      qnaireResponse.setDeletedFlag(deletedFlag);
      RvwQnaireResponseCommand rvwQnaireResponseCommand =
          new RvwQnaireResponseCommand(getServiceData(), qnaireResponse, true, false, false);
      executeChildCommand(rvwQnaireResponseCommand);
      this.inputData.getUpdatedQnaireRespMap().put(rvwQnaireResponseCommand.getNewData().getId(),
          rvwQnaireResponseCommand.getNewData());
    }
  }

  /**
   * @throws IcdmException exception in ws call
   */
  private void deleteQnaireRespVariant(final Long qnaireRespVarIdToDel) throws IcdmException {
    RvwQnaireRespVariant qnaireRespVar =
        new RvwQnaireRespVariantLoader(getServiceData()).getDataObjectByID(qnaireRespVarIdToDel);
    RvwQnaireRespVariantCommand rvwQnaireRespVariantCommand =
        new RvwQnaireRespVariantCommand(getServiceData(), qnaireRespVar, false, true);
    executeChildCommand(rvwQnaireRespVariantCommand);
    this.inputData.getDeletedQnaireRespVariant().add(qnaireRespVar);
  }

  /**
   * @param qnaireRespVariantLink
   * @throws IcdmException
   * @throws DataException
   */
  private void createQnaireRespVariant(final QnaireRespVarRespWpLink qnaireRespVariantLink) throws IcdmException {
    RvwQnaireRespVariant rvwQnaireRespVariant = new RvwQnaireRespVariant();

    rvwQnaireRespVariant.setPidcVersId(this.inputData.getPidcVersionId());
    rvwQnaireRespVariant.setVariantId(qnaireRespVariantLink.getPidcVariant().getId());
    rvwQnaireRespVariant.setQnaireRespId(qnaireRespVariantLink.getSelQnaireRespId());
    rvwQnaireRespVariant.setA2lRespId(qnaireRespVariantLink.getA2lResponsibility().getId());
    rvwQnaireRespVariant.setA2lWpId(qnaireRespVariantLink.getA2lWorkPackage().getId());

    RvwQnaireRespVariantCommand rvwQnaireRespVariantCommand =
        new RvwQnaireRespVariantCommand(getServiceData(), rvwQnaireRespVariant, false, false);
    executeChildCommand(rvwQnaireRespVariantCommand);
    this.inputData.getCreatedQnaireRespVariantSet().add(rvwQnaireRespVariantCommand.getNewData());
  }


  /**
   * @return
   * @throws IcdmException
   * @throws ApicWebServiceException Exception while invoking service
   */
  private Set<Long> getQnaireIdFromWpId() throws IcdmException {

    RvwQnaireLoader rvwQnaireLoader = new RvwQnaireLoader(getServiceData());

    SortedSet<WorkPkg> selWrkpkg = this.inputData.getWorkPkgSet();

    RvwQnaireWPTypesModel rvwQnaireWPTypesModel = rvwQnaireLoader.loadWpTypes(selWrkpkg, this.inputData.getDivId());
    Set<Long> selectedQNaireId = new HashSet<>();
    if (null != rvwQnaireWPTypesModel) {

      // Throw error if Questionaire is not available for selected work package
      if (CommonUtils.isNotEmpty(rvwQnaireWPTypesModel.getQsToBeCreated())) {
        Set<String> wpNameSet = new HashSet<>();
        for (WorkPkg WorkPkg : rvwQnaireWPTypesModel.getQsToBeCreated()) {
          wpNameSet.add(WorkPkg.getName());
        }
        throw new IcdmException("QNAIRE_DEF.WP_WITHOUT_QNAIRE", String.join(", ", wpNameSet));
      }

      fetchSelectedQuestionnaireId(rvwQnaireWPTypesModel, selWrkpkg, selectedQNaireId);
    }
    return selectedQNaireId;
  }


  private void fetchSelectedQuestionnaireId(final RvwQnaireWPTypesModel rvwQnaireWPTypesModel,
      final SortedSet<WorkPkg> selWrkpkg, final Set<Long> selectedQNaireId)
      throws IcdmException {
    Set<Long> qnaireIdsWithActiveVer = new HashSet<>();
    Set<Long> qnaireIdsWithoutActiveVer = new HashSet<>();
    Map<Long, String> workPkgNameWithQnaireActiveVer =
        getWrkPkgNameWithActiveVer(rvwQnaireWPTypesModel, qnaireIdsWithActiveVer);
    Map<Long, String> workPkgNameWithoutQnaireActiveVer =
        getWrkPkgNameWithoutActiveVer(rvwQnaireWPTypesModel, qnaireIdsWithoutActiveVer);

    for (WorkPkg icdmWp : selWrkpkg) {
      fetchQnaireWithActiveVers(rvwQnaireWPTypesModel, selectedQNaireId, workPkgNameWithQnaireActiveVer, icdmWp);
      createNFetchQnaireWithoutActiveVers(rvwQnaireWPTypesModel, selectedQNaireId, workPkgNameWithoutQnaireActiveVer,
          icdmWp);
    }
  }

  /**
   * @param rvwQnaireWPTypesModel
   * @param selectedQNaireId
   * @param workPkgNameWithoutQnaireActiveVer
   * @param icdmWp
   * @throws IcdmException
   * @throws ApicWebServiceException
   */
  private void createNFetchQnaireWithoutActiveVers(final RvwQnaireWPTypesModel rvwQnaireWPTypesModel,
      final Set<Long> selectedQNaireId, final Map<Long, String> workPkgNameWithoutQnaireActiveVer, final WorkPkg icdmWp)
      throws IcdmException {
    for (Questionnaire ques : rvwQnaireWPTypesModel.getQsWithoutActiveVersions()) {
      String workPkgName = workPkgNameWithoutQnaireActiveVer.get(ques.getId());
      if (workPkgName.equals(icdmWp.getName())) {
        createQnaireVersionCommand(ques);
        selectedQNaireId.add(ques.getId());
      }
    }
  }

  /**
   * @param rvwQnaireWPTypesModel
   * @param selectedQNaireId
   * @param workPkgNameWithQnaireActiveVer
   * @param icdmWp
   */
  private void fetchQnaireWithActiveVers(final RvwQnaireWPTypesModel rvwQnaireWPTypesModel,
      final Set<Long> selectedQNaireId, final Map<Long, String> workPkgNameWithQnaireActiveVer, final WorkPkg icdmWp) {
    for (QuestionnaireVersion quesVer : rvwQnaireWPTypesModel.getQsWithActiveVersions()) {
      String workPkgName = workPkgNameWithQnaireActiveVer.get(quesVer.getQnaireId());
      if (workPkgName.equals(icdmWp.getName())) {
        selectedQNaireId.add(quesVer.getQnaireId());

      }
    }
  }


  /**
   * @param rvwQnaireWPTypesModel
   * @param qnaireIdsWithActiveVer
   * @param workPkgNameWithQnaireActiveVer
   * @return
   * @throws ApicWebServiceException
   */
  private Map<Long, String> getWrkPkgNameWithActiveVer(final RvwQnaireWPTypesModel rvwQnaireWPTypesModel,
      final Set<Long> qnaireIdsWithActiveVer) {
    Map<Long, String> workPkgNameWithQnaireActiveVer = new HashMap<>();
    rvwQnaireWPTypesModel.getQsWithActiveVersions()
        .forEach(qnaireVers -> qnaireIdsWithActiveVer.add(qnaireVers.getQnaireId()));

    if (!qnaireIdsWithActiveVer.isEmpty()) {
      WorkPkgLoader loader = new WorkPkgLoader(getServiceData());

      for (Long qnaireId : qnaireIdsWithActiveVer) {
        WorkPkg workPkg = loader.getWorkPkgbyQnaireID(qnaireId);
        workPkgNameWithQnaireActiveVer.put(qnaireId, workPkg.getName());
      }
    }
    return workPkgNameWithQnaireActiveVer;
  }


  /**
   * @param rvwQnaireWPTypesModel
   * @param qnaireIdsWithoutActiveVer
   * @return
   * @throws ApicWebServiceException
   */
  private Map<Long, String> getWrkPkgNameWithoutActiveVer(final RvwQnaireWPTypesModel rvwQnaireWPTypesModel,
      final Set<Long> qnaireIdsWithoutActiveVer) {
    Map<Long, String> workPkgNameWithoutQnaireActiveVer = new HashMap<>();
    rvwQnaireWPTypesModel.getQsWithoutActiveVersions().forEach(qnaire -> qnaireIdsWithoutActiveVer.add(qnaire.getId()));

    if (!qnaireIdsWithoutActiveVer.isEmpty()) {
      WorkPkgLoader loader = new WorkPkgLoader(getServiceData());

      for (Long qnaireId : qnaireIdsWithoutActiveVer) {
        WorkPkg workPkg = loader.getWorkPkgbyQnaireID(qnaireId);
        workPkgNameWithoutQnaireActiveVer.put(qnaireId, workPkg.getName());
      }
    }
    return workPkgNameWithoutQnaireActiveVer;
  }

  /**
   * @param division
   * @param icdmWp
   * @throws IcdmException
   */
  private Questionnaire createQnaireAndVersionCommand(final AttributeValue division, final WorkPkg icdmWp)
      throws IcdmException {
    QnaireCreationModel qnaireCreateModelData = new QnaireCreationModel();
    Questionnaire questionnaire = new Questionnaire();
    questionnaire.setDescEng("Created during review");
    questionnaire.setDescGer(null);
    questionnaire.setDivName(division.getName());
    qnaireCreateModelData.setQnaire(questionnaire);

    WorkPackageDivision wpDiv = new WorkPackageDivision();
    wpDiv.setDivAttrValId(division.getId());
    wpDiv.setDivName(division.getName());
    wpDiv.setWpId(icdmWp.getId());
    qnaireCreateModelData.setWpDiv(wpDiv);

    QuestionnaireVersion qnaireVersion = new QuestionnaireVersion();
    qnaireVersion.setMajorVersionNum(0L);
    qnaireVersion.setMinorVersionNum(null);
    qnaireVersion.setDescEng("Working Set");
    qnaireVersion.setDescGer(null);
    qnaireCreateModelData.setQnaireVersion(qnaireVersion);

    NodeAccess nodeAccess = new NodeAccess();
    nodeAccess.setGrant(true);
    nodeAccess.setOwner(true);
    nodeAccess.setWrite(true);
    nodeAccess.setRead(true);
    nodeAccess.setNodeType(MODEL_TYPE.QUESTIONNAIRE.getTypeCode());
    qnaireCreateModelData.setNodeAccess(nodeAccess);


    WorkPackageDivisionLoader loader = new WorkPackageDivisionLoader(getServiceData());

    // Fetch all WorkPackageDivsions of the given divId
    Set<WorkPackageDivision> wpDivisionsByByDivID = loader.getWorkPackageDivByDivID(division.getId(), false, null);
    Long wpDivId = null;
    for (WorkPackageDivision workPackageDetails : wpDivisionsByByDivID) {
      if ((workPackageDetails.getDivAttrValId().equals(division.getId())) &&
          (workPackageDetails.getWpId().equals(icdmWp.getId()))) {
        // Getting Wp Div Id
        wpDivId = workPackageDetails.getId();
        // Setting Wp Details for the Questionnaire Creation Model
        qnaireCreateModelData.setWpDiv(workPackageDetails);
        break;
      }
    }
    questionnaire.setWpDivId(wpDivId);

    nodeAccess.setUserId(getServiceData().getUserId());
    qnaireCreateModelData.setNodeAccess(nodeAccess);

    QnaireCreationCommand cmd = new QnaireCreationCommand(getServiceData(), qnaireCreateModelData);
    executeChildCommand(cmd);
    Questionnaire qnaire =
        new QuestionnaireLoader(getServiceData()).getDataObjectByID(qnaireCreateModelData.getQnaire().getId());
    // DataCreationModel is used as response object so that both Questionnaire and NodeAccess object can be available in
    // response object
    DataCreationModel<Questionnaire> ret = new DataCreationModel<>();
    ret.setDataCreated(qnaire);
    // Use NodeAccessLoader to get the created node access object for the user who has created questionnaire
    ret.setNodeAccess(
        new NodeAccessLoader(getServiceData()).getDataObjectByID(qnaireCreateModelData.getNodeAccess().getId()));


    return ret.getDataCreated();
  }

  /**
   * @param cdrHandler
   * @param ques
   * @return
   * @throws IcdmException
   * @throws ApicWebServiceException
   */
  private Long createQnaireVersionCommand(final Questionnaire ques) throws IcdmException {
    QuestionnaireVersion questionnaireVersion = new QuestionnaireVersion();
    questionnaireVersion.setMajorVersionNum(1L);
    questionnaireVersion.setMinorVersionNum(0L);
    questionnaireVersion.setDescEng("Active version");
    questionnaireVersion.setDescGer(null);
    questionnaireVersion.setActiveFlag(ApicConstants.CODE_YES);
    questionnaireVersion.setQnaireId(ques.getId());
    // create active version and then add

    QuestionnaireVersionCommand cmd = new QuestionnaireVersionCommand(getServiceData(), questionnaireVersion);
    executeChildCommand(cmd);
    QuestionnaireVersion ret = cmd.getNewData();
    return ret.getId();
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
    // NA
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {
    createRvwQnaireRespFormWorkpackage();
  }

}
