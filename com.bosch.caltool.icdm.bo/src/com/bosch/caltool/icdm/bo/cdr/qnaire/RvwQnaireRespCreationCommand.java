/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.qnaire;

import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespCreationModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVariant;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;

/**
 * @author say8cob
 */
public class RvwQnaireRespCreationCommand extends AbstractSimpleCommand {

  private final QnaireRespCreationModel inputData;

  private RvwQnaireResponse rvwQnaireResponse;

  private String qnaireRespStatus;


  /**
   * @param serviceData service Data
   * @param inputData as QnaireRespCreationModel
   * @param qnaireStatus as questionnaire response status
   * @throws IcdmException as exception
   */
  public RvwQnaireRespCreationCommand(final ServiceData serviceData, final QnaireRespCreationModel inputData,
      final String qnaireStatus) throws IcdmException {
    super(serviceData);
    this.inputData = inputData;
    this.qnaireRespStatus = qnaireStatus;
  }


  /**
   * Method to create Review Questionnaire Response
   *
   * @param pidcVersId as pidc Version id
   * @param qnaireVersId as questionnaire version id
   * @param variantId as pidc variant id
   * @param a2lRespId as a2l responsiblity id
   * @param a2lWpId as a2l workpackage id
   * @throws IcdmException as exception
   */
  private void createRvwQnaireResponse(final Long pidcVersId, final Long qnaireVersId, final Long variantId,
      final Long a2lRespId, final Long a2lWpId)
      throws IcdmException {
    Long qnaireRespId = null;
    // QnaireVersId is null for Simplified Qnaire & for Default WP
    if (CommonUtils.isNotNull(qnaireVersId)) {
      // create questionnaire response for the questionnaire
      RvwQnaireResponse qsResp = new RvwQnaireResponse();
      qsResp.setA2lRespId(a2lRespId);
      qsResp.setA2lWpId(a2lWpId);
      RvwQnaireResponseCommand qnaireRespCmd =
          new RvwQnaireResponseCommand(getServiceData(), qsResp, false, false, false);
      executeChildCommand(qnaireRespCmd);
      qnaireRespId = qnaireRespCmd.getObjId();

      RvwQnaireRespVersion rvwQnaireRespVersion = new RvwQnaireRespVersion();
      rvwQnaireRespVersion.setQnaireRespId(qnaireRespId);
      rvwQnaireRespVersion.setQnaireVersionId(qnaireVersId);
      rvwQnaireRespVersion.setVersionName("Working Set");

      RvwQnaireResponseStatusHandler rvwQnaireResponseStatusHandler =
          new RvwQnaireResponseStatusHandler(getServiceData());

      if (CommonUtils.isNull(this.qnaireRespStatus)) {
        this.qnaireRespStatus = rvwQnaireResponseStatusHandler.getQnaireRespStatus(pidcVersId, variantId, qnaireVersId);
      }
      // Calculate the status for 'Working Set' Questionnaire version
      rvwQnaireRespVersion.setQnaireRespVersStatus(this.qnaireRespStatus);

      // Create Qnaire Resp Verion - update, delete flags are 'false'
      RvwQnaireRespVersionCommand rvwQnaireRespVersionCommand =
          new RvwQnaireRespVersionCommand(getServiceData(), rvwQnaireRespVersion, false, false, false, false);
      executeChildCommand(rvwQnaireRespVersionCommand);

      this.rvwQnaireResponse = qnaireRespCmd.getNewData();
    }

    RvwQnaireRespVariant rvwQnaireRespVariant = new RvwQnaireRespVariant();
    rvwQnaireRespVariant.setPidcVersId(pidcVersId);
    rvwQnaireRespVariant.setQnaireRespId(qnaireRespId);
    rvwQnaireRespVariant.setVariantId(variantId);
    rvwQnaireRespVariant.setA2lRespId(a2lRespId);
    rvwQnaireRespVariant.setA2lWpId(a2lWpId);

    RvwQnaireRespVariantCommand rvwQnaireRespVariantCommand =
        new RvwQnaireRespVariantCommand(getServiceData(), rvwQnaireRespVariant, false, false);
    executeChildCommand(rvwQnaireRespVariantCommand);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {
    createRvwQnaireResponse(this.inputData.getPidcVersionId(), this.inputData.getQnaireVersId(),
        this.inputData.getPidcVariantId(), this.inputData.getSelRespId(), this.inputData.getSelWpId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // Implementation not required

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return false;
  }


  /**
   * @return the rvwQnaireResponse
   */
  public RvwQnaireResponse getRvwQnaireResponse() {
    return this.rvwQnaireResponse;
  }

}
