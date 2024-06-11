/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.CopyA2lWpRespResponse;
import com.bosch.caltool.icdm.model.a2l.CopyPar2WpFromA2lInput;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcTakeOverA2lWrapper;

/**
 * @author mkl2cob
 */
public class PidcA2LPar2WPCommand extends AbstractSimpleCommand {

  /**
   * input object
   */
  private final PidcTakeOverA2lWrapper pidcTakeOverA2lWrapper;
  /**
   * list of copy PAR2WP commands
   */
  private List<AbstractSimpleCommand> copyPar2WpCmdList;

  /**
   * @return the copyPar2WpCmdList
   */
  public List<AbstractSimpleCommand> getCopyPar2WpCmdList() {
    return new ArrayList<>(this.copyPar2WpCmdList);
  }


  /**
   * @return the pidcA2lSet
   */
  public Set<PidcA2l> getPidcA2lSet() {
    return new HashSet<>(this.pidcA2lSet);
  }

  /**
   * Set<PidcA2l>
   */
  private Set<PidcA2l> pidcA2lSet;

  /**
   * @param serviceData ServiceData
   * @param pidcTakeOverA2lWrapper PidcTakeOverA2lWrapper
   * @throws IcdmException exception during command execution
   */
  public PidcA2LPar2WPCommand(final ServiceData serviceData, final PidcTakeOverA2lWrapper pidcTakeOverA2lWrapper)
      throws IcdmException {
    super(serviceData);
    this.pidcTakeOverA2lWrapper = pidcTakeOverA2lWrapper;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {
    List<AbstractSimpleCommand> pidcA2lCmdList = new ArrayList<>();

    for (PidcA2l pidcA2l : this.pidcTakeOverA2lWrapper.getPidcA2lsToCreate()) {
      // iterate over pidc a2l's to be created
      PidcA2lCommand cmd = new PidcA2lCommand(getServiceData(), pidcA2l, true);
      // call command for insertion
      pidcA2lCmdList.add(cmd);
      executeChildCommand(cmd);
    }
    // flush should be done to refresh the entities
    getEm().flush();
    this.pidcA2lSet = new HashSet<>();
    for (AbstractSimpleCommand cmd : pidcA2lCmdList) {
      // add pidc a2l's created
      this.pidcA2lSet.add(((PidcA2lCommand) cmd).getNewData());
    }

    List<CopyPar2WpFromA2lInput> inputDataList = new ArrayList<>();
    for (PidcA2l pidcA2l : this.pidcA2lSet) {
      // got all pidc a2l created , create copy par2wp inputs
      CopyPar2WpFromA2lInput inputData = new CopyPar2WpFromA2lInput();
      inputData.setDescPidcA2lId(pidcA2l.getId());
      inputData.setSourceWpDefVersId(this.pidcTakeOverA2lWrapper.getSourceWpDefVersId());
      inputData.setDerivateFromFunc(this.pidcTakeOverA2lWrapper.isDeriveFromFunc());
      inputDataList.add(inputData);
    }

    CopyA2lWpRespResponse response = new CopyA2lWpRespResponse();
    response.setPidcA2lSet(this.pidcA2lSet);
    this.copyPar2WpCmdList = new ArrayList<>();
    for (CopyPar2WpFromA2lInput inputData : inputDataList) {
      // create CopyPar2WpFromA2lCommand for all the inputs
      CopyPar2WpFromA2lCommand copyCmd = new CopyPar2WpFromA2lCommand(getServiceData(), inputData);
      this.copyPar2WpCmdList.add(copyCmd);
      executeChildCommand(copyCmd);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    if (CommonUtils.isNotEmpty(this.copyPar2WpCmdList)) {
      for (AbstractSimpleCommand abstractSimpleCommand : this.copyPar2WpCmdList) {
        CopyPar2WpFromA2lCommand copyPar2WpFromA2lCommand = (CopyPar2WpFromA2lCommand) abstractSimpleCommand;
        copyPar2WpFromA2lCommand.refreshEntity();
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }

}
