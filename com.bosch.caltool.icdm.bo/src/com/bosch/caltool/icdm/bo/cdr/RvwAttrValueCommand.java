/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwAttrValue;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.model.cdr.RvwAttrValue;


/**
 * Command class for Review Attribute Value
 *
 * @author bru2cob
 */
public class RvwAttrValueCommand extends AbstractCommand<RvwAttrValue, RvwAttrValueLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public RvwAttrValueCommand(final ServiceData serviceData, final RvwAttrValue input, final boolean isUpdate,
      final boolean isDelete) throws IcdmException {
    super(serviceData, input, new RvwAttrValueLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : (isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TRvwAttrValue entity = new TRvwAttrValue();

    TRvwResult resultEntity = new CDRReviewResultLoader(getServiceData()).getEntityObject(getInputData().getResultId());
    entity.setTRvwResult(resultEntity);

    // Add the Review Attr Value to the Result.
    Set<TRvwAttrValue> tRvwValSet = resultEntity.getTRvwAttrValue();
    if (tRvwValSet == null) {
      tRvwValSet = new HashSet<TRvwAttrValue>();
    }
    tRvwValSet.add(entity);
    resultEntity.setTRvwAttrValue(tRvwValSet);
    entity.setTabvAttribute(new AttributeLoader(getServiceData()).getEntityObject(getInputData().getAttrId()));
    entity.setTabvAttrValue(getInputData().getValueId() == null ? null
        : new AttributeValueLoader(getServiceData()).getEntityObject(getInputData().getValueId()));
    entity.setUsed(getInputData().getUsed());

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
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
    RvwAttrValueLoader attrLoader = new RvwAttrValueLoader(getServiceData());
    TRvwAttrValue entity = attrLoader.getEntityObject(getInputData().getId());
    entity.getTRvwResult().getTRvwAttrValue().remove(entity);
    getEm().remove(entity);
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
  protected boolean dataChanged() throws IcdmException {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    // TODO Auto-generated method stub
  }

}
