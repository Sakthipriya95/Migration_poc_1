/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.qnairemigration;

import javax.persistence.Query;

import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;

/**
 * @author bne4cob
 */
public class RvwQnaireRespVrsionFlagMigUpdateCommand extends AbstractSimpleCommand {

  private final Long qnaireRespVersId;
  private final String newState;

  /**
   * @param serviceData ServiceData
   * @param qnaireRespVersId qnaireRespVersId
   * @param newState new state
   * @throws IcdmException error
   */
  public RvwQnaireRespVrsionFlagMigUpdateCommand(final ServiceData serviceData, final Long qnaireRespVersId,
      final String newState) throws IcdmException {
    super(serviceData);
    this.qnaireRespVersId = qnaireRespVersId;
    this.newState = newState;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {
    // A native query is used to avoid need to add column in entitiy, model etc.
    String updStmt = "update T_RVW_QNAIRE_RESP_VERSIONS set MIG_STATE = '" + this.newState +
        "' where QNAIRE_RESP_VERS_ID = " + this.qnaireRespVersId;

    Query statusUpdQry = getEm().createNativeQuery(updStmt);
    int rowsUpdated = statusUpdQry.executeUpdate();

    getLogger().debug("Migration status updated on rows = {}", rowsUpdated);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // N/A
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }

}
