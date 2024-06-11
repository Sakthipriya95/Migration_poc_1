/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.ParameterAttributeLoader;
import com.bosch.caltool.icdm.bo.a2l.ParameterLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.database.entity.cdr.TParamAttr;
import com.bosch.caltool.icdm.database.entity.cdr.TParameter;
import com.bosch.caltool.icdm.model.a2l.ParameterAttribute;

/**
 * @author rgo7cob
 */
public class ParameterAttrCommand extends AbstractCommand<ParameterAttribute, ParameterAttributeLoader> {

  /**
   * @param serviceData
   * @param inputData
   * @param busObj
   * @param mode
   * @throws IcdmException
   */
  public ParameterAttrCommand(final ServiceData serviceData, final ParameterAttribute inputData,
      final ParameterAttributeLoader busObj, final COMMAND_MODE mode) throws IcdmException {
    super(serviceData, inputData, busObj, mode);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TParamAttr entity = new TParamAttr();
    setValuesToEntity(entity);
    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }

  /**
   * @param entity
   */
  private void setValuesToEntity(final TParamAttr entity) {
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    TabvAttribute tabvAttr = attrLoader.getEntityObject(getInputData().getAttrId());
    entity.setTabvAttribute(tabvAttr);
    TParameter tparam = new ParameterLoader(getServiceData()).getEntityObject(getInputData().getParamId());
    entity.setTParameter(tparam);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    ParameterAttributeLoader loader = new ParameterAttributeLoader(getServiceData());
    TParamAttr dbParamAttr = loader.getEntityObject(getInputData().getId());
    getEm().remove(dbParamAttr);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    // TODO Auto-generated method stub

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
