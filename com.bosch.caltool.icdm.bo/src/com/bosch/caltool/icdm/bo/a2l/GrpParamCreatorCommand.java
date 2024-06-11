/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lGrpParam;
import com.bosch.caltool.icdm.database.entity.apic.GttParameter;

/**
 * @author rgo7cob
 */
class GrpParamCreatorCommand extends AbstractSimpleCommand {

  private final Map<String, Set<String>> characterMap;
  private final Map<String, Long> groupMap;

  /**
   * @param serviceData ServiceData
   * @param charMap charMap
   * @param dbGrpMap fetchDbGroupId
   * @throws IcdmException Exception
   */
  public GrpParamCreatorCommand(final ServiceData serviceData, final Map<String, Set<String>> charMap,
      final Map<String, Long> dbGrpMap) throws IcdmException {
    super(serviceData);
    this.characterMap = charMap;
    this.groupMap = dbGrpMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {
    Long recID = 0L;
    // Create records in temp table GttParameter - all parameters of a2l file
    for (Entry<String, Long> groupName : this.groupMap.entrySet()) {
      recID = populateTempTable(groupName.getKey(), groupName.getValue(), this.characterMap, recID);
    }
    // Insert records into Ta2lGrpParam - use select insert from TParameter, GttParameter
    createA2LGrpParams();
  }

  /**
   * @param groupName
   * @param groupId
   * @param entMgrToUse
   * @param charMap
   * @param entMgr
   * @throws IcdmException
   */
  private Long populateTempTable(final String groupName, final long groupId, final Map<String, Set<String>> charMap,
      Long recID)
      throws IcdmException {

    getLogger().debug("Populating temp table");

    GttParameter tempParam;
    // Create entities for all the parameters
    for (String param : charMap.get(groupName)) {
      recID = recID + 1;
      String[] split = param.split(":");
      tempParam = new GttParameter();
      tempParam.setId(recID);
      tempParam.setParamName(split[0]);
      tempParam.setType(split[1]);
      tempParam.setGroupId(groupId);
      GttParameterCreateCommand createTempCmd;
      createTempCmd = new GttParameterCreateCommand(getServiceData(), tempParam);
      executeChildCommand(createTempCmd);

    }
    return recID;
  }

  /**
   * create A2LGroupParams for this ICDMA2lGroupParam
   *
   * @param entMgrToUse
   * @throws CommandException
   */
  private void createA2LGrpParams() {
    getLogger().debug("Insert parameters from temp table...");
    TypedQuery<Object[]> a2lGrpParamQuery = getEm().createNamedQuery(TA2lGrpParam.INSERT_GRP_PARAM, Object[].class);
    int recInserted = a2lGrpParamQuery.executeUpdate();
    getLogger().debug("Number of records inserted in TA2lGrpParam", recInserted);

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
