/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Query;

import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.datamodel.core.ModelTypeRegistry;
import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.user.NodeAccessCommand;
import com.bosch.caltool.icdm.bo.user.NodeAccessLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.RuleSetInputData;
import com.bosch.caltool.icdm.model.cdr.RuleSetOutputData;
import com.bosch.caltool.icdm.model.user.NodeAccess;

/**
 * @author say8cob
 */
public class RuleSetCreationCommand extends AbstractSimpleCommand {

  private RuleSetInputData ruleSetModel = new RuleSetInputData();

  private RuleSetOutputData ruleSetOutputData = new RuleSetOutputData();

  /**
   * @param serviceData as ServiceData
   * @param ruleSetModel as input
   * @throws IcdmException as exception
   */
  public RuleSetCreationCommand(final ServiceData serviceData, final RuleSetInputData ruleSetModel)
      throws IcdmException {
    super(serviceData);
    this.ruleSetModel = ruleSetModel;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {
    createRuleSet();
  }


  /**
   * @throws IcdmException as exception
   */
  private void createRuleSet() throws IcdmException {
    // to check whether the current user have special admin access to create a ruleset
    if (!new NodeAccessLoader(getServiceData()).hasSpecialAdminAccess()) {
      throw new IcdmException("The user don't have special admin access rights to create rule set.");
    }
    if (CommonUtils.isEmptyString(this.ruleSetModel.getRuleSet().getName())) {
      throw new IcdmException("The Rule Set name cannot be null or empty.");
    }
    if (CommonUtils.isNullOrEmpty(this.ruleSetModel.getRuleSetOwnerIds())) {
      throw new IcdmException("The Rule Set Owners cannot be empty.");
    }

    ParamCollection paramCollection = findRuleSet(this.ruleSetModel.getRuleSet().getName());
    if (CommonUtils.isNotNull(paramCollection)) {
      throw new IcdmException(
          "The Rule Set with the name '" + this.ruleSetModel.getRuleSet().getName() + "' already exists.");
    }

    // calling ruleset creation package
    final Query query = getEm().createNativeQuery("call PK_UTILS.create_rule_set(?1, ?2)");
    query.setParameter(1, this.ruleSetModel.getRuleSet().getName());
    query.setParameter(2, this.ruleSetModel.getRuleSet().getDescription());
    query.executeUpdate();

    Set<NodeAccess> addedNodeAccess = new HashSet<>();
    // fetching the newly created ruleset
    paramCollection = findRuleSet(this.ruleSetModel.getRuleSet().getName());
    if (paramCollection == null) {
      throw new IcdmException("Rule Set creation failed.");
    }

    IModelType nodeType = ModelTypeRegistry.INSTANCE.getTypeOfModel(paramCollection);
    for (Long userId : this.ruleSetModel.getRuleSetOwnerIds()) {
      NodeAccess access = new NodeAccess();
      access.setNodeId(paramCollection.getId());
      access.setNodeType(nodeType.getTypeCode());
      access.setUserId(userId);
      access.setOwner(true);

      NodeAccessCommand cmd = new NodeAccessCommand(getServiceData(), access);
      executeChildCommand(cmd);
      addedNodeAccess.add(cmd.getNewData());
    }

    this.ruleSetOutputData.setRuleSet(new RuleSetLoader(getServiceData()).getDataObjectByID(paramCollection.getId()));
    this.ruleSetOutputData.setRuleSetOwnerNodeAccess(addedNodeAccess);
  }

  private ParamCollection findRuleSet(final String ruleSetName) throws DataException {
    for (ParamCollection paramColl : new RuleSetLoader(getServiceData()).getAllRuleSets()) {
      if (paramColl.getName().trim().equalsIgnoreCase(ruleSetName.trim())) {
        return paramColl;
      }
    }
    return null;
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


  /**
   * @return the ruleSetOutputData
   */
  public RuleSetOutputData getRuleSetOutputData() {
    return this.ruleSetOutputData;
  }


  /**
   * @param ruleSetOutputData the ruleSetOutputData to set
   */
  public void setRuleSetOutputData(final RuleSetOutputData ruleSetOutputData) {
    this.ruleSetOutputData = ruleSetOutputData;
  }


}
