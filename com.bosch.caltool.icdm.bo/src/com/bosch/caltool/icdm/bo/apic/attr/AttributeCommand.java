package com.bosch.caltool.icdm.bo.apic.attr;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrGroup;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValueType;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;


/**
 * Command class for Attribute
 *
 * @author dmo5cob
 */
public class AttributeCommand extends AbstractCommand<Attribute, AttributeLoader> {


  private TabvAttribute entity;

  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public AttributeCommand(final ServiceData serviceData, final Attribute input, final boolean isUpdate,
      final boolean isDelete) throws IcdmException {
    super(serviceData, input, new AttributeLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : (isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    this.entity = new TabvAttribute();

    setValuesToEntity(this.entity);

    setUserDetails(COMMAND_MODE.CREATE, this.entity);

    persistEntity(this.entity);
  }

  /**
   * @param entity
   * @throws IcdmException
   */
  private void setValuesToEntity(final TabvAttribute entity) throws IcdmException {
    entity.setAttrNameEng(getInputData().getNameEng());
    entity.setAttrNameGer(getInputData().getNameGer());
    entity.setAttrDescEng(getInputData().getDescriptionEng());
    entity.setAttrDescGer(getInputData().getDescriptionGer());

    TabvAttrGroup dbAttrGrp = new AttrGroupLoader(getServiceData()).getEntityObject(getInputData().getAttrGrpId());
    dbAttrGrp.addTabvAttribute(entity);

    TabvAttrValueType dbValueType =
        new AttrValueTypeLoader(getServiceData()).getEntityObject(getInputData().getValueTypeId());
    entity.setTabvAttrValueType(dbValueType);

    entity.setNormalizedFlag(booleanToYorN(getInputData().isNormalized()));
    entity.setDeletedFlag(booleanToYorN(getInputData().isDeleted()));
    entity.setAttrLevel(BigDecimal.valueOf(getInputData().getLevel()));
    entity.setMandatory(booleanToYorN(getInputData().isMandatory()));
    if (AttributeValueType.getType(getInputData().getValueTypeId()) == AttributeValueType.NUMBER) {
      entity.setUnits(getInputData().getUnit());
    }
    else {
      entity.setUnits("-");
    }
    entity.setFormat(getInputData().getFormat());
    entity.setPartNumberFlag(booleanToYorN(getInputData().isWithPartNumber()));
    entity.setSpecLinkFlag(booleanToYorN(getInputData().isWithSpecLink()));
    entity.setAttrSecurity(getInputData().isExternal() ? ApicConstants.EXTERNAL : ApicConstants.INTERNAL);
    entity.setValueSecurity(getInputData().isExternalValue() ? ApicConstants.EXTERNAL : ApicConstants.INTERNAL);

    entity.settCharacteristic(
        new CharacteristicLoader(getServiceData()).getEntityObject(getInputData().getCharacteristicId()));
    entity.setChangeComment(getInputData().getChangeComment());
    entity.setEadmName(getInputData().getEadmName());
    entity.setGroupFlag(booleanToYorN(getInputData().isGroupedAttr()));
    entity.setAddValuesByUsersFlag(booleanToYorN(getInputData().isAddValByUserFlag()));
    if (getInputData().isDelCharFlag()) {
      createCharValDelCommand(entity);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    AttributeLoader loader = new AttributeLoader(getServiceData());
    TabvAttribute entity = loader.getEntityObject(getInputData().getId());

    setValuesToEntity(entity);

    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    AttributeLoader loader = new AttributeLoader(getServiceData());
    TabvAttribute entity = loader.getEntityObject(getInputData().getId());

    getEm().remove(entity);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    if (this.entity != null) {
      getEm().refresh(this.entity);
    }
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

  /**
   * ICdm-955 Delete the mapping of Char Value with the Char of attr is changed
   *
   * @param dbAttr
   * @throws IcdmException
   */
  private void createCharValDelCommand(final TabvAttribute dbAttr) throws IcdmException {


    if (hasCharValue(dbAttr)) {
      List<TabvAttrValue> attrValsWithChar = getValuesWithCharVal(dbAttr);
      for (TabvAttrValue attrVal : attrValsWithChar) {
        AttributeValue attrValue = new AttributeValueLoader(getServiceData()).getDataObjectByID(attrVal.getValueId());
        attrValue.setCharacteristicValueId(null);
        AttrValueCommand valCmd = new AttrValueCommand(getServiceData(), attrValue, true, false);
        executeChildCommand(valCmd);
      }
    }
  }

  /**
   * @return a list of Attribute Values with Char Values
   */
  public List<TabvAttrValue> getValuesWithCharVal(final TabvAttribute dbAttr) {
    final List<TabvAttrValue> vallWithCharVal = new ArrayList<TabvAttrValue>();
    for (TabvAttrValue attrVal : dbAttr.getTabvAttrValues()) {
      if (null != attrVal.gettCharacteristicValue()) {
        vallWithCharVal.add(attrVal);
      }
    }
    return vallWithCharVal;
  }

  /**
   * @param dbAttr
   */
  private boolean hasCharValue(final TabvAttribute dbAttr) {
    for (TabvAttrValue attrValue : dbAttr.getTabvAttrValues()) {
      if (attrValue.gettCharacteristicValue() != null) {
        return true;
      }
    }
    return false;
  }
}
