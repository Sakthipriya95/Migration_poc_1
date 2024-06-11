/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.qnaire;

import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.user.NodeAccessCommand;
import com.bosch.caltool.icdm.bo.wp.WorkPackageDivisionCommand;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireCreationModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.Questionnaire;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireVersion;
import com.bosch.caltool.icdm.model.user.NodeAccess;

/**
 * @author apj4cob
 */
public class QnaireCreationCommand extends AbstractSimpleCommand {

  private final QnaireCreationModel inputData;

  /**
   * @param serviceData ServiceData
   * @param inputData QuestionnaireCreationCommand
   * @throws IcdmException Exception
   */
  public QnaireCreationCommand(final ServiceData serviceData, final QnaireCreationModel inputData)
      throws IcdmException {
    super(serviceData);
    this.inputData = inputData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {
    // Create Questionnaire , questionnaire version and the associated access rights
    Questionnaire qnaire = createQnaire();
    this.inputData.getQnaire().setId(qnaire.getId());
    createQVersion(qnaire);
    createAccessRights(qnaire);
  }

  /**
   * @param qnaire
   * @throws IcdmException
   */
  private void createAccessRights(final Questionnaire qnaire) throws IcdmException {
    NodeAccess nodeAccess = this.inputData.getNodeAccess();
    nodeAccess.setNodeId(qnaire.getId());
    NodeAccessCommand nodeAccessCmd = new NodeAccessCommand(getServiceData(), nodeAccess);
    executeChildCommand(nodeAccessCmd);
    this.inputData.getNodeAccess().setId(nodeAccessCmd.getNewData().getId());
  }

  /**
   * @param qnaire
   * @throws IcdmException
   */
  private void createQVersion(final Questionnaire qnaire) throws IcdmException {
    QuestionnaireVersion qVersion = this.inputData.getQnaireVersion();
    qVersion.setQnaireId(qnaire.getId());
    QuestionnaireVersionCommand qVersionCommand = new QuestionnaireVersionCommand(getServiceData(), qVersion);
    executeChildCommand(qVersionCommand);
  }

  /**
   * @throws IcdmException
   */
  private Questionnaire createQnaire() throws IcdmException {
    Questionnaire newQnaire = this.inputData.getQnaire();
    if (this.inputData.getQnaire().getWpDivId() == null) {
      // Create wp div
      WorkPackageDivisionCommand wpDivCmd = new WorkPackageDivisionCommand(getServiceData(), this.inputData.getWpDiv());
      executeChildCommand(wpDivCmd);
      newQnaire.setWpDivId(wpDivCmd.getNewData().getId());
    }
    else {
      newQnaire.setWpDivId(this.inputData.getWpDiv().getId());
    }
    QuestionnaireCommand qnaireCmd = new QuestionnaireCommand(getServiceData(), newQnaire, false, false);
    executeChildCommand(qnaireCmd);
    return qnaireCmd.getNewData();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }

}
