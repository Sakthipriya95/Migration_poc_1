/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import javax.persistence.Query;

import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.PidcA2lCommand;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TPidcA2l;
import com.bosch.caltool.icdm.model.a2l.UnmapA2LDeletionResponse;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;


/**
 * @author hnu1cob
 */
public class UnmapA2lCommand extends AbstractSimpleCommand {

  private final Long pidcA2LId;

  UnmapA2LDeletionResponse delResponse;


  /**
   * @return the delResponse
   */
  public UnmapA2LDeletionResponse getDelResponse() {
    return this.delResponse;
  }

  /**
   * @param serviceData ,the service data
   * @throws IcdmException ,exception
   */
  public UnmapA2lCommand(final ServiceData serviceData, final Long pidcA2LId) throws IcdmException {
    super(serviceData);
    this.pidcA2LId = pidcA2LId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {
    this.delResponse = new UnmapA2LDeletionResponse();
    Query query = getEm().createNativeQuery("call PK_UNMAP_A2L.P_UNMAP_A2L(?pidcA2lId)");
    query.setParameter("pidcA2lId", this.pidcA2LId);
    query.executeUpdate();
    getEm().flush();
    updatePidcA2l();
  }

  /**
   * Update the pidc a2l flag is new mappings are created
   */
  private void updatePidcA2l() throws IcdmException {
    PidcA2lLoader pidcA2lLoader = new PidcA2lLoader(getServiceData());
    PidcA2l pidcA2l = pidcA2lLoader.getDataObjectByID(this.pidcA2LId);
    pidcA2l.setPidcVersId(null);
    pidcA2l.setActive(false);
    pidcA2l.setWpParamPresentFlag(false);
    pidcA2l.setActiveWpParamPresentFlag(false);
    pidcA2l.setWorkingSetModified(false);
    PidcA2lCommand pidcA2lCmd = new PidcA2lCommand(getServiceData(), pidcA2l, true, false, true);
    executeChildCommand(pidcA2lCmd);
    this.delResponse.setPidcA2l(pidcA2lCmd.getNewData());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    PidcA2lLoader pidcA2lLoader = new PidcA2lLoader(getServiceData());
    TPidcA2l tPidcA2l = pidcA2lLoader.getEntityObject(this.pidcA2LId);
    tPidcA2l.gettA2lWpDefnVersions();
    tPidcA2l.getTRvwResults();
    tPidcA2l.getTabvProjectidcard();
    getEm().refresh(tPidcA2l);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }

}
