/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.Map;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.bo.uc.UsecaseFavoriteLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectAttr;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.uc.UsecaseFavorite;

/**
 * @author dja7cob
 */
public class PidcVersionAttributeCommand extends AbstractCommand<PidcVersionAttribute, PidcVersionAttributeLoader> {

  /**
   * @param serviceData
   * @param input
   * @param isUpdate
   * @param isDelete
   * @throws IcdmException
   */
  public PidcVersionAttributeCommand(final ServiceData serviceData, final PidcVersionAttribute input,
      final boolean isUpdate, final boolean isDelete) throws IcdmException {
    super(serviceData, input, new PidcVersionAttributeLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : (isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TabvProjectAttr entity = new TabvProjectAttr();

    setValuesToEntity(entity);

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);


  }

  /**
   * @param entity
   * @throws IcdmException
   */
  private void setValuesToEntity(final TabvProjectAttr entity) throws IcdmException {
    PidcVersionAttribute newPidcVerAtt = getInputData();

    PidcVersionLoader versLoader = new PidcVersionLoader(getServiceData());
    TPidcVersion tpidcVersion = versLoader.getEntityObject(newPidcVerAtt.getPidcVersId());
    entity.setTPidcVersion(tpidcVersion);

    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    TabvAttribute attr = attrLoader.getEntityObject(newPidcVerAtt.getAttrId());
    entity.setTabvAttribute(attr);

    AttributeValueLoader attrvalLoader = new AttributeValueLoader(getServiceData());
    entity.setTabvAttrValue(attrvalLoader.getEntityObject(newPidcVerAtt.getValueId()));


    setUsedFlag(entity, newPidcVerAtt, attr);

    if (newPidcVerAtt.isAtChildLevel()) {
      entity.setIsVariant(ApicConstants.CODE_YES);
    }
    else {
      entity.setIsVariant(ApicConstants.CODE_NO);
    }
    if (newPidcVerAtt.isAttrHidden()) {
      entity.setAttrHiddenFlag(ApicConstants.CODE_YES);
    }
    else {
      entity.setAttrHiddenFlag(ApicConstants.CODE_NO);
    }
    entity.setTrnsfrVcdmFlag(newPidcVerAtt.isTransferToVcdm() ? ApicConstants.CODE_YES : ApicConstants.CODE_NO);

    if (newPidcVerAtt.isFocusMatrixApplicable()) {
      updateFMEntities(entity, newPidcVerAtt);
    }
    entity.setFocusMatrixYn(newPidcVerAtt.isFocusMatrixApplicable() ? ApicConstants.CODE_YES : ApicConstants.CODE_NO);

    entity.setPartNumber(newPidcVerAtt.getPartNumber());

    entity.setSpecLink(newPidcVerAtt.getSpecLink());

    entity.setDescription(newPidcVerAtt.getAdditionalInfoDesc());

    entity.setFmAttrRemark(newPidcVerAtt.getFmAttrRemark());


  }

  /**
   * @param entity
   * @param newPidcVerAtt
   * @throws IcdmException
   */
  private void updateFMEntities(final TabvProjectAttr entity, final PidcVersionAttribute newPidcVerAtt)
      throws IcdmException {
    // If FM flag is enabled for an attribute in PidcAttrPage , that attribute should be enabled with FM relevant flag
    // in focus matrix page for the usecase nodes in which that attribute is present
    if (((null == entity.getFocusMatrixYn()) || entity.getFocusMatrixYn().equals(ApicConstants.CODE_NO)) &&
        newPidcVerAtt.isFocusMatrixApplicable()) {
      UsecaseFavoriteLoader usecaseFavLoader = new UsecaseFavoriteLoader(getServiceData());
      // Load the project usecases
      Map<Long, UsecaseFavorite> projFavoriteUseCases =
          usecaseFavLoader.getProjFavoriteUseCases(entity.getTPidcVersion().getTabvProjectidcard().getProjectId());
      MultipleFocusMatrixCommand cmd =
          new MultipleFocusMatrixCommand(getServiceData(), projFavoriteUseCases, entity);
      cmd.execute();
    }
  }

  /**
   * @param entity
   * @param newPidcVerAtt
   * @param attr
   */
  private void setUsedFlag(final TabvProjectAttr entity, final PidcVersionAttribute newPidcVerAtt,
      final TabvAttribute attr) {
    // ICDM-92 If the used flag is YES and a value is defined and the user sets the used flag to NO or ???, the value
    // needs to be cleared (set to NULL)
    if ((newPidcVerAtt.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType())) ||
        (newPidcVerAtt.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType()))) {
      entity.setTabvAttrValue(null);
    }
    // ICDM-92 If the used flag is changed to YES and the attribute has the datatype BOOLEAN, the value TRUE needs to be
    // set automatically
    else if (newPidcVerAtt.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType())) {

      if (AttributeValueType.BOOLEAN == AttributeValueType.getType(attr.getTabvAttrValueType().getValueTypeId())) {
        entity.setTabvAttrValue(getBooleanTrueValue(attr));
      }
    }
    entity.setUsed(newPidcVerAtt.getUsedFlag());
  }

  /**
   * Gets the 'true' value of this attribute, if it is of type boolean. If true value is not defined for this attribute,
   * null is returned. <br>
   * Note : Only boolean type attributes should be passed to this method.
   *
   * @param attribute the attribute
   * @return the true value
   */
  private TabvAttrValue getBooleanTrueValue(final TabvAttribute attribute) { // ICDM-92
    for (TabvAttrValue value : attribute.getTabvAttrValues()) {
      if (ApicConstants.BOOLEAN_TRUE_DB_STRING.equals(value.getBoolvalue())) {
        return value;
      }
    }
    return null;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    PidcVersionAttributeLoader loader = new PidcVersionAttributeLoader(getServiceData());
    TabvProjectAttr entity = loader.getEntityObject(getInputData().getId());

    setValuesToEntity(entity);

    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
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
  protected void validateInput() throws IcdmException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    PidcVersionLoader versLoader = new PidcVersionLoader(getServiceData());
    TPidcVersion tpidcVersion = versLoader.getEntityObject(getInputData().getPidcVersId());
    PidcVersionAttributeLoader loader = new PidcVersionAttributeLoader(getServiceData());
    TabvProjectAttr entity = loader.getEntityObject(getInputData().getId());
    if (tpidcVersion.getTabvProjectAttrs().contains(entity)) {
      tpidcVersion.getTabvProjectAttrs().remove(entity);
    }
    tpidcVersion.getTabvProjectAttrs().add(entity);


  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }

}
