package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.ArrayList;
import java.util.List;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjSubVariantsAttr;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectSubVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;


/**
 * Command class for PidcSubVariantAttribute
 *
 * @author dmo5cob
 */
public class PidcSubVariantAttributeCommand
    extends AbstractCommand<PidcSubVariantAttribute, PidcSubVariantAttributeLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public PidcSubVariantAttributeCommand(final ServiceData serviceData, final PidcSubVariantAttribute input,
      final boolean isUpdate, final boolean isDelete) throws IcdmException {
    super(serviceData, input, new PidcSubVariantAttributeLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : (isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TabvProjSubVariantsAttr entity = new TabvProjSubVariantsAttr();

    setDetails(entity);

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }

  /**
   * @param entity
   */
  private void setDetails(final TabvProjSubVariantsAttr entity) {
    PidcSubVariantAttribute newSubVarAttr = getInputData();

    PidcSubVariantLoader svLoader = new PidcSubVariantLoader(getServiceData());
    TabvProjectSubVariant subvar = svLoader.getEntityObject(newSubVarAttr.getSubVariantId());
    entity.setTabvProjectSubVariant(subvar);

    List<TabvProjSubVariantsAttr> tabvSubVariantsAttrs = subvar.getTabvProjSubVariantsAttrs();
    if (null == tabvSubVariantsAttrs) {
      tabvSubVariantsAttrs = new ArrayList<>();
      subvar.setTabvProjSubVariantsAttrs(tabvSubVariantsAttrs);
    }
    if (!tabvSubVariantsAttrs.contains(entity)) {
      tabvSubVariantsAttrs.add(entity);
    }

    TabvProjectVariant varentityObject = subvar.getTabvProjectVariant();
    entity.setTabvProjectVariant(varentityObject);

    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    TabvAttribute attribute = attrLoader.getEntityObject(newSubVarAttr.getAttrId());
    entity.setTabvAttribute(attribute);

    AttributeValueLoader attrvalLoader = new AttributeValueLoader(getServiceData());
    entity.setTabvAttrValue(attrvalLoader.getEntityObject(newSubVarAttr.getValueId()));

    setUsedFlag(entity, newSubVarAttr, attribute);

    entity.setPartNumber(newSubVarAttr.getPartNumber());
    entity.setSpecLink(newSubVarAttr.getSpecLink());
    entity.setDescription(newSubVarAttr.getAdditionalInfoDesc());
    PidcVersionLoader versLoader = new PidcVersionLoader(getServiceData());
    entity.setTPidcVersion(versLoader.getEntityObject(subvar.getTPidcVersion().getPidcVersId()));
  }

  /**
   * @param entity
   * @param newPidcVerAtt
   * @param attr
   */
  private void setUsedFlag(final TabvProjSubVariantsAttr entity, final PidcSubVariantAttribute newPidcSubVarAttr,
      final TabvAttribute attr) {
    // ICDM-92 If the used flag is YES and a value is defined and the user sets the used flag to NO or ???, the value
    // needs to be cleared (set to NULL)
    if ((newPidcSubVarAttr.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType())) ||
        (newPidcSubVarAttr.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType()))) {
      entity.setTabvAttrValue(null);
    }
    // ICDM-92 If the used flag is changed to YES and the attribute has the datatype BOOLEAN, the value TRUE needs to be
    // set automatically
    else if (newPidcSubVarAttr.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType())) {

      if (AttributeValueType.BOOLEAN == AttributeValueType.getType(attr.getTabvAttrValueType().getValueTypeId())) {
        entity.setTabvAttrValue(getBooleanTrueValue(attr));
      }
    }
    entity.setUsed(newPidcSubVarAttr.getUsedFlag());
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
    PidcSubVariantAttributeLoader loader = new PidcSubVariantAttributeLoader(getServiceData());
    TabvProjSubVariantsAttr entity = loader.getEntityObject(getInputData().getId());

    setDetails(entity);

    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    PidcSubVariantAttributeLoader loader = new PidcSubVariantAttributeLoader(getServiceData());
    TabvProjSubVariantsAttr entity = loader.getEntityObject(getInputData().getId());
    entity.getTabvProjectSubVariant().getTabvProjSubVariantsAttrs().remove(entity);
    getEm().remove(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {

    PidcSubVariantLoader varLoader = new PidcSubVariantLoader(getServiceData());
    TabvProjectSubVariant var = varLoader.getEntityObject(getInputData().getSubVariantId());
    PidcSubVariantAttributeLoader loader = new PidcSubVariantAttributeLoader(getServiceData());
    TabvProjSubVariantsAttr entity = loader.getEntityObject(getInputData().getId());
    if (var.getTabvProjSubVariantsAttrs().contains(entity)) {
      var.getTabvProjSubVariantsAttrs().remove(entity);
    }
    var.getTabvProjSubVariantsAttrs().add(entity);
    var.getTPidcVersion().getTabvProjSubVariantsAttrs().add(entity);


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
