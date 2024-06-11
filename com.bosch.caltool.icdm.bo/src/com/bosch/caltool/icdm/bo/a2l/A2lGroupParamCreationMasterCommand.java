/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractCommand.COMMAND_MODE;
import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2lResp;
import com.bosch.caltool.icdm.model.a2l.A2lWpResp;
import com.bosch.caltool.icdm.model.a2l.IcdmA2lGroup;

/**
 * @author apj4cob
 */
public class A2lGroupParamCreationMasterCommand extends AbstractSimpleCommand {

  private final A2lGroupInput a2lGrpInput;
  private A2lResp a2lResp;
  private Set<IcdmA2lGroup> groupSet = new HashSet<>();
  private Map<String, Long> dbA2lGrpIdMap;

  /**
   * @param serviceData ServiceData
   * @param a2lGrpInput Input Data
   * @throws IcdmException Exception
   */
  public A2lGroupParamCreationMasterCommand(final ServiceData serviceData, final A2lGroupInput a2lGrpInput)
      throws IcdmException {
    super(serviceData);
    this.a2lGrpInput = a2lGrpInput;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {
    // Fetch A2l Group from Db based on A2l ID and WP Root ID(Attribute Value ID)
    this.groupSet = new IcdmA2lGroupLoader(getServiceData()).getA2lGroupByA2lIdAndRootId(this.a2lGrpInput.getA2lID(),
        this.a2lGrpInput.getRootGrpID());
    // Check whether records exists in Ta2lGroup table for the given A2lId and WP Root ID(Attribute Value ID)
    if (CommonUtils.isNullOrEmpty(this.groupSet)) {
      // Insert records into Ta2lGroup - all groups in a2l file
      createA2lGroup();
      // refresh the groupSet after creating A2l Groups in Db
      this.groupSet = new IcdmA2lGroupLoader(getServiceData()).getA2lGroupByA2lIdAndRootId(this.a2lGrpInput.getA2lID(),
          this.a2lGrpInput.getRootGrpID());
      // Create records in temp table GttParameter - all parameters of a2l file and
      // Insert records into Ta2lGrpParam - use select insert from TParameter, GttParameter
      createA2lGrpParams(this.a2lGrpInput.getGrpCharParamMap());
    }
    // Insert one record to Ta2lResp
    createA2lResp();
    // Insert records in TA2lWpResp - one record for each group, mapping to record created in Ta2lResp
    createA2LWPRespForGrps();
  }


  /**
   * Create A2lResp entity in Db
   *
   * @throws IcdmException
   */
  private void createA2lResp() throws IcdmException {
    A2lResp inputData = new A2lResp();
    inputData.setPidcA2lId(this.a2lGrpInput.getPidcA2LId());
    inputData.setWpRootId(this.a2lGrpInput.getRootGrpID());
    inputData.setWpTypeId(this.a2lGrpInput.getTypeId());
    A2lRespCommand createCmd = new A2lRespCommand(getServiceData(), inputData, COMMAND_MODE.CREATE);
    executeChildCommand(createCmd);
    this.a2lResp = createCmd.getNewData();
  }

  /**
   * Creates the A2l WP resp for grps in Db.
   *
   * @param a2lResp the a 2 l resp
   * @param groupSet the group set
   * @throws IcdmException the icdm exception
   */
  private void createA2LWPRespForGrps() throws IcdmException {
    getLogger().debug("Inserting responsibilities for groups ....");
    for (IcdmA2lGroup group : this.groupSet) {
      A2lWpResp inputData = new A2lWpResp();
      inputData.setA2lGroupId(group.getId());
      inputData.setA2lRespId(this.a2lResp.getId());
      inputData.setWpRespId(this.a2lGrpInput.getDefaultWPRespId());
      A2lWpRespCommand createCmd = new A2lWpRespCommand(getServiceData(), inputData, COMMAND_MODE.CREATE);
      executeChildCommand(createCmd);
    }
  }

  /**
   * @throws IcdmException
   */
  private void createA2lGroup() throws IcdmException {
    getLogger().debug("Create groups in T_A2L_GROUP table...");
    this.dbA2lGrpIdMap = new HashMap<>();
    for (Entry<String, String> group : this.a2lGrpInput.getA2lGrpMap().entrySet()) {
      IcdmA2lGroup inputData = new IcdmA2lGroup();
      inputData.setGrpName(group.getKey());
      inputData.setGrpLongName(group.getValue());
      inputData.setA2lId(this.a2lGrpInput.getA2lID());
      inputData.setWpRootId(this.a2lGrpInput.getRootGrpID());
      IcdmA2lGroupCommand createCmd = new IcdmA2lGroupCommand(getServiceData(), inputData);
      executeChildCommand(createCmd);
      // Map Key- A2l Grp Name Value-A2l Grp Id.This map will be used to populate temp table,to resolve a2l group id
      this.dbA2lGrpIdMap.put(createCmd.getNewData().getGrpName(), createCmd.getNewData().getId());
    }
  }

  /**
   * @param a2lGrpMap
   * @param grpCharMap
   * @throws IcdmException
   */
  private void createA2lGrpParams(final Map<String, Set<String>> grpCharMap) throws IcdmException {
    GrpParamCreatorCommand grpParamCreatorCmd =
        new GrpParamCreatorCommand(getServiceData(), grpCharMap, this.dbA2lGrpIdMap);
    executeChildCommand(grpParamCreatorCmd);
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
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }

}
