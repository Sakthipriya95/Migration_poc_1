package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.ArrayList;
import java.util.List;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvVariantsAttr;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;


/**
 * Command class for PidcVariantAttribute
 *
 * @author dmo5cob
 */
public class PidcVariantAttributeCommand extends AbstractCommand<PidcVariantAttribute, PidcVariantAttributeLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public PidcVariantAttributeCommand(final ServiceData serviceData, final PidcVariantAttribute input,
      final boolean isUpdate, final boolean isDelete) throws IcdmException {
    super(serviceData, input, new PidcVariantAttributeLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : (isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TabvVariantsAttr entity = new TabvVariantsAttr();

    setDetails(entity);


    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }

  /**
   * @param entity
   */
  private void setDetails(final TabvVariantsAttr entity) {
    PidcVariantAttribute newVarAttr = getInputData();

    PidcVariantLoader vLoader = new PidcVariantLoader(getServiceData());
    TabvProjectVariant tabvVar = vLoader.getEntityObject(newVarAttr.getVariantId());
    entity.setTabvProjectVariant(tabvVar);

    List<TabvVariantsAttr> tabvVariantsAttrs = tabvVar.getTabvVariantsAttrs();
    if (null == tabvVariantsAttrs) {
      tabvVariantsAttrs = new ArrayList<>();
      tabvVar.setTabvVariantsAttrs(tabvVariantsAttrs);
    }
    if (!tabvVariantsAttrs.contains(entity)) {
      tabvVariantsAttrs.add(entity);
    }

    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    TabvAttribute attribute = attrLoader.getEntityObject(newVarAttr.getAttrId());
    entity.setTabvAttribute(attribute);

    AttributeValueLoader attrvalLoader = new AttributeValueLoader(getServiceData());
    entity.setTabvAttrValue(attrvalLoader.getEntityObject(newVarAttr.getValueId()));

    setUsedFlag(entity, newVarAttr, attribute);

    entity.setIsSubVariant(newVarAttr.isAtChildLevel() ? ApicConstants.CODE_YES : ApicConstants.CODE_NO);
    entity.setPartNumber(newVarAttr.getPartNumber());
    entity.setSpecLink(newVarAttr.getSpecLink());
    entity.setDescription(newVarAttr.getAdditionalInfoDesc());
    PidcVersionLoader versLoader = new PidcVersionLoader(getServiceData());
    entity.setTPidcVersion(versLoader.getEntityObject(newVarAttr.getPidcVersionId()));
  }

  /**
   * @param entity
   * @param newPidcVerAtt
   * @param attr
   */
  private void setUsedFlag(final TabvVariantsAttr entity, final PidcVariantAttribute newPidcVarAttr,
      final TabvAttribute attr) {
    // ICDM-92 If the used flag is YES and a value is defined and the user sets the used flag to NO or ???, the value
    // needs to be cleared (set to NULL)
    if ((newPidcVarAttr.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType())) ||
        (newPidcVarAttr.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType()))) {
      entity.setTabvAttrValue(null);
    }
    // ICDM-92 If the used flag is changed to YES and the attribute has the datatype BOOLEAN, the value TRUE needs to be
    // set automatically
    else if (newPidcVarAttr.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType())) {

      if (AttributeValueType.BOOLEAN == AttributeValueType.getType(attr.getTabvAttrValueType().getValueTypeId())) {
        entity.setTabvAttrValue(getBooleanTrueValue(attr));
      }
    }
    entity.setUsed(newPidcVarAttr.getUsedFlag());
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
    PidcVariantAttributeLoader loader = new PidcVariantAttributeLoader(getServiceData());
    TabvVariantsAttr entity = loader.getEntityObject(getInputData().getId());

    setDetails(entity);

    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    PidcVariantAttributeLoader loader = new PidcVariantAttributeLoader(getServiceData());
    TabvVariantsAttr entity = loader.getEntityObject(getInputData().getId());
    entity.getTabvProjectVariant().getTabvVariantsAttrs().remove(entity);
    getEm().remove(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    PidcVariantLoader varLoader = new PidcVariantLoader(getServiceData());
    TabvProjectVariant var = varLoader.getEntityObject(getInputData().getVariantId());
    PidcVariantAttributeLoader loader = new PidcVariantAttributeLoader(getServiceData());
    TabvVariantsAttr entity = loader.getEntityObject(getInputData().getId());
    if (var.getTabvVariantsAttrs().contains(entity)) {
      var.getTabvVariantsAttrs().remove(entity);
    }
    var.getTabvVariantsAttrs().add(entity);
    PidcVersionLoader versLoader = new PidcVersionLoader(getServiceData());
    TPidcVersion entityObject = versLoader.getEntityObject(getInputData().getPidcVersionId());

    entityObject.getTabvVariantsAttrs().add(entity);
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
