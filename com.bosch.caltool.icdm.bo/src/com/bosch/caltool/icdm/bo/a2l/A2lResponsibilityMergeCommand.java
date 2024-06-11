/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibilityMergeModel;

/**
 * @author dmr1cob
 */
public class A2lResponsibilityMergeCommand extends AbstractSimpleCommand {

  private final A2lResponsibilityMergeModel mergeModel;

  private final Set<A2lResponsibility> a2lResponsibilityDeleteSet = new HashSet<>();

  /**
   * @param serviceData Service data
   * @param mergeModel Merge Model
   * @throws IcdmException Exception in webservice
   */
  public A2lResponsibilityMergeCommand(final ServiceData serviceData, final A2lResponsibilityMergeModel mergeModel)
      throws IcdmException {
    super(serviceData);
    this.mergeModel = mergeModel;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {

    getLogger().info("Merge A2l Responsibility in CDFX delivery");
    A2lRespMergeCdfxDlvryCommand a2lRespMergeCdfxDlvryCommand =
        new A2lRespMergeCdfxDlvryCommand(getServiceData(), this.mergeModel);
    executeChildCommand(a2lRespMergeCdfxDlvryCommand);

    getLogger().info("Merge A2l Responsibility in Review Result");
    A2lRespMergeRvwResultCommand a2lRespMergeRvwResultCommand =
        new A2lRespMergeRvwResultCommand(getServiceData(), this.mergeModel);
    executeChildCommand(a2lRespMergeRvwResultCommand);

    getLogger().info("Merge A2l Responsibility in Qnaire Response");
    A2lRespMergeQnaireRespCommand a2lRespMergeQnaireRespCommand =
        new A2lRespMergeQnaireRespCommand(getServiceData(), this.mergeModel);
    executeChildCommand(a2lRespMergeQnaireRespCommand);

    getLogger().info("Merge A2l Responsibility in Wp Param mapping");
    A2lRespMergeWpParamMappingCommand a2lRespMergeWpParamMappingCommand =
        new A2lRespMergeWpParamMappingCommand(getServiceData(), this.mergeModel);
    executeChildCommand(a2lRespMergeWpParamMappingCommand);

    getLogger().info("Delete Src A2l responsibilities");
    for (Long a2lResId : this.mergeModel.getA2lRespMergeFromIdSet()) {
      A2lResponsibility a2lResp = new A2lResponsibilityLoader(getServiceData()).getDataObjectByID(a2lResId);
      A2lResponsibilityCommand a2lResponsibilityCommand =
          new A2lResponsibilityCommand(getServiceData(), a2lResp, false, true);
      executeChildCommand(a2lResponsibilityCommand);
      this.a2lResponsibilityDeleteSet.add(a2lResp);
    }
    this.mergeModel.setA2lResponsibilityDeleteSet(this.a2lResponsibilityDeleteSet);
    getLogger().info("Source A2l Responsibility Delete count - {}", this.a2lResponsibilityDeleteSet.size());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // No Implementation
  }


  public A2lResponsibilityMergeModel getMergeModel() {
    return this.mergeModel;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }

}
