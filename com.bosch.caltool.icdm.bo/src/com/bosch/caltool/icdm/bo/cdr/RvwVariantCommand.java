/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireRespCreationCommand;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireRespVariantCommand;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireRespVariantLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireResponseLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionnaireVersion;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireResponse;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwVariant;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespCreationModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVariant;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponseLink;

/**
 * @author say8cob
 */
public class RvwVariantCommand extends AbstractCommand<RvwVariant, RvwVariantLoader> {

  private boolean isLinkExistingQnaire = false;

  private boolean isAttachRvwQnaire = false;

  private final Set<RvwQnaireResponse> qnaireRespSkipped = new HashSet<>();

  /**
   * @param serviceData the service Data
   * @param input the input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @throws IcdmException error
   */
  public RvwVariantCommand(final ServiceData serviceData, final RvwVariant input, final boolean isUpdate,
      final boolean isDelete) throws IcdmException {
    super(serviceData, input, new RvwVariantLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : isUpdateCmd(isUpdate));
  }

  /**
   * @param serviceData the service Data
   * @param input the input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param isAttachRvwQnaire if true, review questionnaire to be attached
   * @param isLinkExistingQnaire if true, existing questionnaire to be linked
   * @throws IcdmException error
   */
  public RvwVariantCommand(final ServiceData serviceData, final RvwVariant input, final boolean isUpdate,
      final boolean isDelete, final boolean isAttachRvwQnaire, final boolean isLinkExistingQnaire)
      throws IcdmException {
    super(serviceData, input, new RvwVariantLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : isUpdateCmd(isUpdate));
    this.isAttachRvwQnaire = isAttachRvwQnaire;
    this.isLinkExistingQnaire = isLinkExistingQnaire;
  }

  /**
   * @param isUpdate
   * @return
   */
  private static COMMAND_MODE isUpdateCmd(final boolean isUpdate) {
    return isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TRvwVariant entity = new TRvwVariant();
    TRvwResult resultEntity = new CDRReviewResultLoader(getServiceData()).getEntityObject(getInputData().getResultId());
    entity.setTRvwResult(resultEntity);
    Set<TRvwVariant> tRvwVarSet = resultEntity.getTRvwVariants();
    if (tRvwVarSet == null) {
      tRvwVarSet = new HashSet<>();
    }
    tRvwVarSet.add(entity);
    resultEntity.setTRvwVariants(tRvwVarSet);
    entity
        .setTabvProjectVariant(new PidcVariantLoader(getServiceData()).getEntityObject(getInputData().getVariantId()));
    setUserDetails(COMMAND_MODE.CREATE, entity);
    persistEntity(entity);

    if (this.isAttachRvwQnaire &&
        !resultEntity.getReviewType().equalsIgnoreCase(CDRConstants.REVIEW_TYPE.TEST.getDbType())) {
      // Review questionnaires are applicable for start and official reviews only
      createRvwQnaireRespIfApplicable(entity);
    }
  }

  /**
   * @param entity
   * @throws DataException
   * @throws IcdmException
   */
  private void createRvwQnaireRespIfApplicable(final TRvwVariant entity) throws IcdmException {
    RvwVariant newObj = new RvwVariantLoader(getServiceData()).getDataObjectByID(entity.getRvwVarId());
    Set<RvwQnaireRespVariant> qnaireRespVarSetInSrc =
        new RvwQnaireResponseLoader(getServiceData()).getQnaireRespVarInSrcWpResp(newObj);

    if (CommonUtils.isNotEmpty(qnaireRespVarSetInSrc)) {
      RvwQnaireResponseLink qnairesInTarget =
          new RvwQnaireResponseLoader(getServiceData()).getQnaireInTargetWpResp(newObj);

      if (this.isLinkExistingQnaire) {
        linkToExistingQnaireResponses(newObj, qnaireRespVarSetInSrc, qnairesInTarget);
      }
      else {
        createNewQnaireResponses(entity, newObj, qnaireRespVarSetInSrc, qnairesInTarget);
      }
    }
  }

  /**
   * @param newObj
   * @param qnaireRespVarSetInSrc
   * @param qnairesInTarget
   * @throws DataException
   * @throws IcdmException
   */
  private void linkToExistingQnaireResponses(final RvwVariant newObj,
      final Set<RvwQnaireRespVariant> qnaireRespVarSetInSrc, final RvwQnaireResponseLink qnairesInTarget)
      throws IcdmException {

    for (RvwQnaireRespVariant qnaireRespVar : qnaireRespVarSetInSrc) {
      TRvwQnaireResponse tRvwQnaireResponse = new RvwQnaireRespVariantLoader(getServiceData())
          .getEntityObject(qnaireRespVar.getId()).getTRvwQnaireResponse();

      // tRvwQnaireResponse is Null for Simplified Qnaire
      if (CommonUtils.isNotNull(tRvwQnaireResponse)) {
        Long qnaireId = tRvwQnaireResponse.getTRvwQnaireRespVersions().iterator().next().getTQuestionnaireVersion()
            .getTQuestionnaire().getQnaireId();

        RvwQnaireResponse qnaireResp =
            new RvwQnaireResponseLoader(getServiceData()).getDataObjectByID(tRvwQnaireResponse.getQnaireRespId());

        if (qnairesInTarget.getSecondaryQnaireWpRespLinkMap().containsKey(qnaireRespVar.getA2lWpId()) &&
            qnairesInTarget.getSecondaryQnaireWpRespLinkMap().get(qnaireRespVar.getA2lWpId())
                .containsKey(qnaireRespVar.getA2lRespId()) &&
            qnairesInTarget.getSecondaryQnaireWpRespLinkMap().get(qnaireRespVar.getA2lWpId())
                .get(qnaireRespVar.getA2lRespId()).contains(qnaireId)) {
          this.qnaireRespSkipped.add(qnaireResp);
        }
        else {
          invokeLinkQnaireCmd(newObj, qnaireRespVar);
        }
      }
    }
  }

  /**
   * @param newObj
   * @param qnaireRespVar
   * @throws IcdmException
   */
  private void invokeLinkQnaireCmd(final RvwVariant newObj, final RvwQnaireRespVariant qnaireRespVar)
      throws IcdmException {

    qnaireRespVar.setVariantId(newObj.getVariantId());
    RvwQnaireRespVariantCommand respVariantCommand =
        new RvwQnaireRespVariantCommand(getServiceData(), qnaireRespVar, false, false);

    executeChildCommand(respVariantCommand);
  }


  /**
   * @return the qnaireRespWithVarLinking
   */
  public Set<RvwQnaireResponse> getQnaireRespSkipped() {
    return this.qnaireRespSkipped;
  }

  /**
   * @param entity
   * @param newObj
   * @param qnaireRespVarSetInSrc
   * @param qnairesInTarget
   * @throws IcdmException
   */
  private void createNewQnaireResponses(final TRvwVariant entity, final RvwVariant newObj,
      final Set<RvwQnaireRespVariant> qnaireRespVarSetInSrc, final RvwQnaireResponseLink qnairesInTarget)
      throws IcdmException {
    if (CommonUtils.isNotEmpty(qnaireRespVarSetInSrc)) {
      for (RvwQnaireRespVariant rvwQnaireVarSrc : qnaireRespVarSetInSrc) {

        TRvwQnaireResponse tRvwQnaireResponse = new RvwQnaireRespVariantLoader(getServiceData())
            .getEntityObject(rvwQnaireVarSrc.getId()).getTRvwQnaireResponse();

        // tRvwQnaireResp is Null for Simplified Gnrl Qnaire resp - Empty WP/Resp Structure
        if (CommonUtils.isNotNull(tRvwQnaireResponse)) {
          Long qnaireId = tRvwQnaireResponse.getTRvwQnaireRespVersions().iterator().next().getTQuestionnaireVersion()
              .getTQuestionnaire().getQnaireId();
          Long qnaireRespId = rvwQnaireVarSrc.getQnaireRespId();
          // qnaireRespId is Null for Simplified Qnaire
          if (CommonUtils.isNotNull(qnaireRespId)) {
            RvwQnaireResponse qnaireResp =
                new RvwQnaireResponseLoader(getServiceData()).getDataObjectByID(qnaireRespId);

            if (qnairesInTarget.getPrimaryQnaireWpRespLinkMap().containsKey(rvwQnaireVarSrc.getA2lWpId()) &&
                qnairesInTarget.getPrimaryQnaireWpRespLinkMap().get(rvwQnaireVarSrc.getA2lWpId())
                    .containsKey(rvwQnaireVarSrc.getA2lRespId()) &&
                qnairesInTarget.getPrimaryQnaireWpRespLinkMap().get(rvwQnaireVarSrc.getA2lWpId())
                    .get(rvwQnaireVarSrc.getA2lRespId()).contains(qnaireId)) {
              this.qnaireRespSkipped.add(qnaireResp);
            }
            else {
              invokeCreateQnaireRespCmd(entity, newObj, rvwQnaireVarSrc);
            }
          }
        }
      }
    }
  }

  /**
   * @param entity
   * @param newObj
   * @param qnaireRespVar
   * @throws IcdmException
   */
  private void invokeCreateQnaireRespCmd(final TRvwVariant entity, final RvwVariant newObj,
      final RvwQnaireRespVariant qnaireRespVar)
      throws IcdmException {
    Long qnaireRespId = qnaireRespVar.getQnaireRespId();
    Long activeQnnaireVersionId = null;

    // qnaireRespId is Null for Simplified Qnaire
    if (CommonUtils.isNotNull(qnaireRespId)) {
      TRvwQnaireResponse tQnaireResp = new RvwQnaireResponseLoader(getServiceData()).getEntityObject(qnaireRespId);
      Set<TQuestionnaireVersion> tQuestionnaireVersions = tQnaireResp.getTRvwQnaireRespVersions().iterator().next()
          .getTQuestionnaireVersion().getTQuestionnaire().getTQuestionnaireVersions();

      for (TQuestionnaireVersion qnaireVers : tQuestionnaireVersions) {
        if ((null != qnaireVers.getActiveFlag()) && qnaireVers.getActiveFlag().equals(ApicConstants.CODE_YES)) {
          activeQnnaireVersionId = qnaireVers.getQnaireVersId();
          break;
        }
      }
    }

    QnaireRespCreationModel qnaireRespCreationModel = new QnaireRespCreationModel();
    qnaireRespCreationModel.setPidcVersionId(entity.getTabvProjectVariant().getTPidcVersion().getPidcVersId());
    qnaireRespCreationModel.setPidcVariantId(newObj.getVariantId());
    qnaireRespCreationModel.setQnaireVersId(activeQnnaireVersionId);
    qnaireRespCreationModel.setSelRespId(qnaireRespVar.getA2lRespId());
    qnaireRespCreationModel.setSelWpId(qnaireRespVar.getA2lWpId());

    RvwQnaireRespCreationCommand qnaireRespCmd =
        new RvwQnaireRespCreationCommand(getServiceData(), qnaireRespCreationModel, null);

    executeChildCommand(qnaireRespCmd);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    TRvwVariant tRvwVariant = new RvwVariantLoader(getServiceData()).getEntityObject(getInputData().getId());
    tRvwVariant
        .setTabvProjectVariant(new PidcVariantLoader(getServiceData()).getEntityObject(getInputData().getVariantId()));
    setUserDetails(COMMAND_MODE.UPDATE, tRvwVariant);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    RvwVariantLoader loader = new RvwVariantLoader(getServiceData());
    TRvwVariant entity = loader.getEntityObject(getInputData().getId());
    entity.getTRvwResult().getTRvwVariants().remove(entity);
    getEm().remove(entity);
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

}
