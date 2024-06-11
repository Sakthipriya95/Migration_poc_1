/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.general;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TLink;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.general.Link;

/**
 * @author NIP4COB
 */
public class LinkCommand extends AbstractCommand<Link, LinkLoader> {

  /**
   * @param serviceData serviceData
   * @param inputData
   * @param busObj
   * @param mode
   * @throws IcdmException
   */
  protected LinkCommand(final ServiceData serviceData, final Link inputData, final LinkLoader busObj,
      final COMMAND_MODE mode) throws IcdmException {
    super(serviceData, inputData, busObj, mode);
  }

  /**
   * @param serviceData serviceData
   * @param inputData Link
   * @throws IcdmException
   */
  public LinkCommand(final ServiceData serviceData, final Link inputData) throws IcdmException {
    super(serviceData, inputData, new LinkLoader(serviceData), COMMAND_MODE.CREATE);
  }


  /**
   * @param serviceData serviceData
   * @param inputData Link
   * @param isUpdate - flag to identify whether the operation is update or delete
   * @throws IcdmException
   */
  public LinkCommand(final ServiceData serviceData, final Link inputData, final boolean isUpdate) throws IcdmException {
    super(serviceData, inputData, new LinkLoader(serviceData), isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.DELETE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    final TLink dbLink = new TLink();
    dbLink.setNodeId(getInputData().getNodeId());
    dbLink.setNodeType(getInputData().getNodeType());
    dbLink.setLinkUrl(getInputData().getLinkUrl());
    dbLink.setDescEng(getInputData().getDescriptionEng());
    dbLink.setDescGer(getInputData().getDescriptionGer());
    setUserDetails(COMMAND_MODE.CREATE, dbLink);
    if (dbLink.getNodeType().equals(ApicConstants.ATTRIB_VALUE)) {
      TabvAttrValue dbValue =
          new AttributeValueLoader(getServiceData()).getEntityObject(getInputData().getAttributeValueId());
      dbLink.setTabvAttrValue(dbValue);
      dbValue.gettLinks().add(dbLink);
    }
    persistEntity(dbLink);
  }

  /**
   * updates the db entity with new values
   *
   * @param modifiedLink
   */
  private void setNewValues(final TLink modifiedLink) {
    if (!CommonUtils.isEqual(getOldData().getLinkUrl(), getInputData().getLinkUrl())) {
      modifiedLink.setLinkUrl(getInputData().getLinkUrl());
    }
    if (!CommonUtils.isEqual(getOldData().getDescriptionEng(), getInputData().getDescriptionEng())) {
      modifiedLink.setDescEng(getInputData().getDescriptionEng());
    }
    if (!CommonUtils.isEqual(getOldData().getDescriptionGer(), getInputData().getDescriptionGer())) {
      modifiedLink.setDescGer(getInputData().getDescriptionGer());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    final TLink modifiedLink = new LinkLoader(getServiceData()).getEntityObject(getInputData().getId());
    setNewValues(modifiedLink);
    setUserDetails(COMMAND_MODE.UPDATE, modifiedLink);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    TLink entity = new LinkLoader(getServiceData()).getEntityObject(getInputData().getId());
    if (entity.getTabvAttrValue() != null) {
      (new AttributeValueLoader(getServiceData()).getEntityObject(entity.getTabvAttrValue().getValueId()))
          .removeTLink(entity);
    }
    getEm().remove(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    return isObjectChanged(getInputData().getLinkUrl(), getOldData().getLinkUrl()) ||
        isObjectChanged(getInputData().getDescriptionEng(), getOldData().getDescriptionEng()) ||
        isObjectChanged(getInputData().getDescriptionGer(), getOldData().getDescriptionGer());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }

}
