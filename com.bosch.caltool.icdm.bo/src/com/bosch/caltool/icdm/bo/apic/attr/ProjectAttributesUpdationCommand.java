package com.bosch.caltool.icdm.bo.apic.attr;

import java.math.BigDecimal;
import java.util.Map;

import com.bosch.caltool.authentication.ldap.LdapException;
import com.bosch.caltool.authentication.ldap.UserInfo;
import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcSubVariantAttributeCommand;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcSubVariantAttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcSubVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantAttributeCommand;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantAttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeCommand;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.user.UserCommand;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.bo.user.LdapAuthenticationWrapper;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjSubVariantsAttr;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectAttr;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectSubVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvVariantsAttr;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.model.apic.attr.ProjectAttributesUpdationModel;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.user.User;


/**
 * Command class for updating project attributes
 *
 * @author dmo5cob
 */
public class ProjectAttributesUpdationCommand extends AbstractSimpleCommand {


  /**
   * Input model
   */
  private final ProjectAttributesUpdationModel inputData;

  /**
   * Constructor
   *
   * @param input input data
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public ProjectAttributesUpdationCommand(final ServiceData serviceData, final ProjectAttributesUpdationModel input)
      throws IcdmException {
    super(serviceData);
    this.inputData = input;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {
    if (!this.inputData.getPidcAttrsToBeCreated().isEmpty()) {
      for (PidcVersionAttribute value : this.inputData.getPidcAttrsToBeCreated().values()) {
        PidcVersionAttributeCommand command = new PidcVersionAttributeCommand(getServiceData(), value, false, false);
        executeChildCommand(command);
        addNewEntityToPidcVersion(value, command.getObjId());
      }

    }
    if (!this.inputData.getPidcAttrsToBeCreatedwithNewVal().isEmpty()) {
      for (PidcVersionAttribute value : this.inputData.getPidcAttrsToBeCreatedwithNewVal().values()) {
        PidcVersionAttributeCommand command = new PidcVersionAttributeCommand(getServiceData(), value, false, false);
        executeChildCommand(command);
        PidcVersionAttribute verAttr = command.getNewData();
        verAttr.setValue(value.getValue());
        AttrValueCommand attrValCmd =
            new AttrValueCommand(getServiceData(), createAttrValObj(verAttr, verAttr.getValueType()), false, false);
        executeChildCommand(attrValCmd);
        verAttr.setValue(attrValCmd.getNewData().getNameRaw());
        verAttr.setValueId(attrValCmd.getNewData().getId());
        addAttrValEntityToPidcAttr(attrValCmd.getNewData(), verAttr);
        addNewEntityToPidcVersion(value, command.getObjId());
      }
    }
    if (!this.inputData.getPidcAttrsToBeUpdated().isEmpty()) {
      for (PidcVersionAttribute val : this.inputData.getPidcAttrsToBeUpdated().values()) {
        PidcVersionAttributeCommand command = new PidcVersionAttributeCommand(getServiceData(), val, true, false);
        executeChildCommand(command);
        addNewEntityToPidcVersion(val, val.getId());
      }
    }
    if (!this.inputData.getPidcAttrsToBeUpdatedwithNewVal().isEmpty()) {
      for (PidcVersionAttribute verAttr : this.inputData.getPidcAttrsToBeUpdatedwithNewVal().values()) {
        AttrValueCommand attrValCmd =
            new AttrValueCommand(getServiceData(), createAttrValObj(verAttr, verAttr.getValueType()), false, false);
        executeChildCommand(attrValCmd);
        verAttr.setValue(attrValCmd.getNewData().getName());
        verAttr.setValueId(attrValCmd.getNewData().getId());
        PidcVersionAttributeCommand command = new PidcVersionAttributeCommand(getServiceData(), verAttr, true, false);
        executeChildCommand(command);
        addAttrValEntityToPidcAttr(attrValCmd.getNewData(), verAttr);
        addNewEntityToPidcVersion(verAttr, verAttr.getId());
      }
    }
    if (!this.inputData.getPidcVarAttrsToBeCreated().isEmpty()) {
      for (Map<Long, PidcVariantAttribute> varAttrMap : this.inputData.getPidcVarAttrsToBeCreated().values()) {
        for (PidcVariantAttribute value : varAttrMap.values()) {
          PidcVariantAttributeCommand command = new PidcVariantAttributeCommand(getServiceData(), value, false, false);
          executeChildCommand(command);
          addNewEntityToVariant(value, command.getObjId());
        }
      }

    }
    if (!this.inputData.getPidcVarAttrsToBeCreatedWithNewVal().isEmpty()) {
      for (Map<Long, PidcVariantAttribute> varAttrMap : this.inputData.getPidcVarAttrsToBeCreatedWithNewVal()
          .values()) {
        for (PidcVariantAttribute value : varAttrMap.values()) {
          PidcVariantAttributeCommand command = new PidcVariantAttributeCommand(getServiceData(), value, false, false);
          executeChildCommand(command);
          PidcVariantAttribute varAttr = command.getNewData();
          AttrValueCommand attrValCmd =
              new AttrValueCommand(getServiceData(), createAttrValObj(varAttr, varAttr.getValueType()), false, false);
          executeChildCommand(attrValCmd);
          varAttr.setValue(attrValCmd.getNewData().getName());
          varAttr.setValueId(attrValCmd.getNewData().getId());
          addAttrValEntityToVarAttr(attrValCmd.getNewData(), varAttr);
          addNewEntityToVariant(value, command.getObjId());
        }
      }

    }
    if (!this.inputData.getPidcVarAttrsToBeUpdatedWithNewVal().isEmpty()) {
      for (Map<Long, PidcVariantAttribute> varAttrMap : this.inputData.getPidcVarAttrsToBeUpdatedWithNewVal()
          .values()) {
        for (PidcVariantAttribute value : varAttrMap.values()) {
          AttrValueCommand attrValCmd =
              new AttrValueCommand(getServiceData(), createAttrValObj(value, value.getValueType()), false, false);
          executeChildCommand(attrValCmd);
          value.setValue(attrValCmd.getNewData().getName());
          value.setValueId(attrValCmd.getNewData().getId());
          PidcVariantAttributeCommand command = new PidcVariantAttributeCommand(getServiceData(), value, true, false);
          executeChildCommand(command);
          addAttrValEntityToVarAttr(attrValCmd.getNewData(), value);
          addNewEntityToVariant(value, value.getId());
        }
      }

    }
    if (!this.inputData.getPidcVarAttrsToBeUpdated().isEmpty()) {
      for (Map<Long, PidcVariantAttribute> varAttrMap : this.inputData.getPidcVarAttrsToBeUpdated().values()) {
        for (PidcVariantAttribute value : varAttrMap.values()) {
          PidcVariantAttributeCommand command = new PidcVariantAttributeCommand(getServiceData(), value, true, false);
          executeChildCommand(command);
          addNewEntityToVariant(value, value.getId());
        }
      }

    }

    if (!this.inputData.getPidcSubVarAttrsToBeCreatedWithNewVal().isEmpty()) {
      for (Map<Long, PidcSubVariantAttribute> varAttrMap : this.inputData.getPidcSubVarAttrsToBeCreatedWithNewVal()
          .values()) {
        for (PidcSubVariantAttribute value : varAttrMap.values()) {
          PidcSubVariantAttributeCommand command =
              new PidcSubVariantAttributeCommand(getServiceData(), value, false, false);
          executeChildCommand(command);
          PidcSubVariantAttribute subvarAttr = command.getNewData();
          AttrValueCommand attrValCmd = new AttrValueCommand(getServiceData(),
              createAttrValObj(subvarAttr, subvarAttr.getValueType()), false, false);
          executeChildCommand(attrValCmd);
          subvarAttr.setValue(attrValCmd.getNewData().getName());
          subvarAttr.setValueId(attrValCmd.getNewData().getId());
          addAttrValEntityToSubvarAttr(attrValCmd.getNewData(), subvarAttr);
          addNewEntityToSubVariant(value, command.getObjId());
        }
      }

    }
    if (!this.inputData.getPidcSubVarAttrsToBeCreated().isEmpty()) {
      for (Map<Long, PidcSubVariantAttribute> varAttrMap : this.inputData.getPidcSubVarAttrsToBeCreated().values()) {
        for (PidcSubVariantAttribute value : varAttrMap.values()) {
          PidcSubVariantAttributeCommand command =
              new PidcSubVariantAttributeCommand(getServiceData(), value, false, false);
          executeChildCommand(command);
          addNewEntityToSubVariant(value, command.getObjId());
        }
      }

    }
    if (!this.inputData.getPidcSubVarAttrsToBeUpdatedWithNewVal().isEmpty()) {
      for (Map<Long, PidcSubVariantAttribute> varAttrMap : this.inputData.getPidcSubVarAttrsToBeUpdatedWithNewVal()
          .values()) {
        for (PidcSubVariantAttribute value : varAttrMap.values()) {
          AttrValueCommand attrValCmd =
              new AttrValueCommand(getServiceData(), createAttrValObj(value, value.getValueType()), false, false);
          executeChildCommand(attrValCmd);
          value.setValue(attrValCmd.getNewData().getName());
          value.setValueId(attrValCmd.getNewData().getId());
          PidcSubVariantAttributeCommand command =
              new PidcSubVariantAttributeCommand(getServiceData(), value, true, false);
          executeChildCommand(command);
          addAttrValEntityToSubvarAttr(attrValCmd.getNewData(), value);
          addNewEntityToSubVariant(value, value.getId());
        }
      }

    }
    if (!this.inputData.getPidcSubVarAttrsToBeUpdated().isEmpty()) {
      for (Map<Long, PidcSubVariantAttribute> varAttrMap : this.inputData.getPidcSubVarAttrsToBeUpdated().values()) {
        for (PidcSubVariantAttribute value : varAttrMap.values()) {
          PidcSubVariantAttributeCommand command =
              new PidcSubVariantAttributeCommand(getServiceData(), value, true, false);
          executeChildCommand(command);
          addNewEntityToSubVariant(value, value.getId());
        }
      }

    }
    if (!this.inputData.getPidcVariantAttributeDeletedMap().isEmpty()) {
      for (Map<Long, PidcVariantAttribute> varAttrMap : this.inputData.getPidcVariantAttributeDeletedMap().values()) {
        for (PidcVariantAttribute value : varAttrMap.values()) {
          PidcVariantAttributeCommand command = new PidcVariantAttributeCommand(getServiceData(), value, false, true);
          executeChildCommand(command);
          removeEntityFromVariant(value, command.getObjId());
        }
      }

    }
    if (!this.inputData.getPidcSubVariantAttributeDeletedMap().isEmpty()) {
      for (Map<Long, PidcSubVariantAttribute> varAttrMap : this.inputData.getPidcSubVariantAttributeDeletedMap()
          .values()) {
        for (PidcSubVariantAttribute value : varAttrMap.values()) {
          PidcSubVariantAttributeCommand command =
              new PidcSubVariantAttributeCommand(getServiceData(), value, false, true);
          executeChildCommand(command);
          removeEntityFromSubVariant(value, command.getObjId());
        }
      }

    }
  }

  /**
   * @param newData
   * @param value
   */
  private void addAttrValEntityToSubvarAttr(final AttributeValue newAttrVal, final PidcSubVariantAttribute subvarAttr) {
    PidcSubVariantAttributeLoader verAttrLoader = new PidcSubVariantAttributeLoader(getServiceData());
    TabvProjSubVariantsAttr tabvSubvarAttr = verAttrLoader.getEntityObject(subvarAttr.getId());
    AttributeValueLoader attrValLoader = new AttributeValueLoader(getServiceData());
    TabvAttrValue tabvAttrVal = attrValLoader.getEntityObject(newAttrVal.getId());
    tabvSubvarAttr.setTabvAttrValue(tabvAttrVal);
  }

  /**
   * @param newAttrVal
   * @param varAttr
   */
  private void addAttrValEntityToVarAttr(final AttributeValue newAttrVal, final PidcVariantAttribute varAttr) {
    PidcVariantAttributeLoader verAttrLoader = new PidcVariantAttributeLoader(getServiceData());
    TabvVariantsAttr tabvVarAttr = verAttrLoader.getEntityObject(varAttr.getId());
    AttributeValueLoader attrValLoader = new AttributeValueLoader(getServiceData());
    TabvAttrValue tabvAttrVal = attrValLoader.getEntityObject(newAttrVal.getId());
    tabvVarAttr.setTabvAttrValue(tabvAttrVal);
  }

  /**
   * @param newAttrVal
   * @param verAttr
   */
  private void addAttrValEntityToPidcAttr(final AttributeValue newAttrVal, final PidcVersionAttribute verAttr) {
    PidcVersionAttributeLoader verAttrLoader = new PidcVersionAttributeLoader(getServiceData());
    TabvProjectAttr tabvProjAttr = verAttrLoader.getEntityObject(verAttr.getId());
    AttributeValueLoader attrValLoader = new AttributeValueLoader(getServiceData());
    TabvAttrValue tabvAttrVal = attrValLoader.getEntityObject(newAttrVal.getId());
    tabvProjAttr.setTabvAttrValue(tabvAttrVal);
  }

  /**
   * @param string
   * @param value
   * @param valueType
   * @return
   * @throws IcdmException
   */
  private AttributeValue createAttrValObj(final IProjectAttribute verAttr, final String valType) throws IcdmException {
    AttributeValue attrVal = new AttributeValue();
    String val = verAttr.getValue();
    if (null != valType) {
      attrVal.setAttributeId(verAttr.getAttrId());
      String attrValue = verAttr.getValue();

      // Icdm-830 Data Model changes for New Column Clearing status
      attrVal.setClearingStatus(ApicConstants.CODE_YES);

      // Set the attr value for boolean type
      if (valType.equals(AttributeValueType.BOOLEAN.toString())) {
        if (ApicConstants.BOOLEAN_TRUE_STRING.equalsIgnoreCase(val)) {
          attrValue = ApicConstants.BOOLEAN_TRUE_DB_STRING;
        }
        else {
          attrValue = ApicConstants.BOOLEAN_FALSE_DB_STRING;
        }
        attrVal.setBoolvalue(attrValue);
      }
      // // Set the attr value for Number type
      else if (valType.equals(AttributeValueType.NUMBER.toString())) {
        attrValue = val.replaceAll(",", ".").trim();
        attrVal.setNumValue(new BigDecimal(attrValue));
      }
      else if (valType.equals(AttributeValueType.TEXT.toString()) ||
          valType.equals(AttributeValueType.HYPERLINK.toString())) {
        attrVal.setTextValueEng(attrValue);
        attrVal.setTextValueGer(attrValue);
      }
      else if (valType.equals(AttributeValueType.DATE.toString())) {
        attrVal.setDateValue(attrValue);
      }
      else if (valType.equals(AttributeValueType.ICDM_USER.toString())) {
        try {
          attrVal.setUserId(getUserObj(attrValue).getId());
        }
        catch (LdapException e) {
          getLogger().error(e.getMessage(), e);
          throw new IcdmException(e.getMessage(), e);
        }
      }
      // Defualt Text
      attrVal.setName(attrValue);
      attrVal.setValueType(valType);
      attrVal.setDescriptionEng(val);
      attrVal.setDescriptionGer(val);
      attrVal.setDescription(val);
    }
    return attrVal;
  }

  private User getUserObj(final String attrValue) throws IcdmException, LdapException {
    String attrValTocreate = attrValue.replaceAll(",", "").trim();
    UserLoader userLoader = new UserLoader(getServiceData());
    for (User user : userLoader.getAllApicUsers(false)) {
      if (user.getDescription().replaceAll(",", "").trim().equals(attrValTocreate)) {
        return user;
      }
    }

    UserInfo userinfo = new LdapAuthenticationWrapper().getUserDetailsForFullName(attrValTocreate);

    UserCommand usercmd = new UserCommand(getServiceData(), createUserobj(userinfo));
    usercmd.create();
    return usercmd.getNewData();
  }

  /**
   * @param userinfo
   * @return
   */
  private User createUserobj(final UserInfo userinfo) {
    User user = new User();
    user.setName(userinfo.getUserName());
    user.setFirstName(userinfo.getGivenName());
    user.setLastName(userinfo.getSurName());
    user.setDepartment(userinfo.getDepartment());
    return user;
  }

  /**
   * @param value
   * @param id
   */
  private void addNewEntityToSubVariant(final PidcSubVariantAttribute value, final Long id) {
    PidcSubVariantLoader varLoader = new PidcSubVariantLoader(getServiceData());
    TabvProjectSubVariant var = varLoader.getEntityObject(value.getSubVariantId());
    PidcSubVariantAttributeLoader loader = new PidcSubVariantAttributeLoader(getServiceData());
    TabvProjSubVariantsAttr entity = loader.getEntityObject(id);
    if (var.getTabvProjSubVariantsAttrs().contains(entity)) {
      var.getTabvProjSubVariantsAttrs().remove(entity);
    }
    var.getTabvProjSubVariantsAttrs().add(entity);
    PidcVersionLoader versLoader = new PidcVersionLoader(getServiceData());
    TPidcVersion entityObject = versLoader.getEntityObject(var.getTPidcVersion().getPidcVersId());
    entity.setTPidcVersion(entityObject);
    entityObject.getTabvProjectSubVariants().add(var);
    entityObject.getTabvProjSubVariantsAttrs().add(entity);
  }

  /**
   * @param value
   * @param id
   */
  private void removeEntityFromSubVariant(final PidcSubVariantAttribute value, final Long id) {
    PidcSubVariantLoader varLoader = new PidcSubVariantLoader(getServiceData());
    TabvProjectSubVariant var = varLoader.getEntityObject(value.getSubVariantId());
    PidcSubVariantAttributeLoader loader = new PidcSubVariantAttributeLoader(getServiceData());
    TabvProjSubVariantsAttr entity = loader.getEntityObject(id);
    if (var.getTabvProjSubVariantsAttrs().contains(entity)) {
      var.getTabvProjSubVariantsAttrs().remove(entity);
    }

    PidcVersionLoader versLoader = new PidcVersionLoader(getServiceData());
    TPidcVersion entityObject = versLoader.getEntityObject(var.getTPidcVersion().getPidcVersId());
    entityObject.getTabvProjectSubVariants().add(var);
    if (entityObject.getTabvProjSubVariantsAttrs().contains(entity)) {
      entityObject.getTabvProjSubVariantsAttrs().remove(entity);
    }
  }

  /**
   * @param value
   * @param id
   */
  private void addNewEntityToVariant(final PidcVariantAttribute value, final Long id) {
    PidcVariantLoader varLoader = new PidcVariantLoader(getServiceData());
    TabvProjectVariant var = varLoader.getEntityObject(value.getVariantId());
    PidcVariantAttributeLoader loader = new PidcVariantAttributeLoader(getServiceData());
    TabvVariantsAttr entity = loader.getEntityObject(id);
    if (var.getTabvVariantsAttrs().contains(entity)) {
      var.getTabvVariantsAttrs().remove(entity);
    }
    var.getTabvVariantsAttrs().add(entity);
    PidcVersionLoader versLoader = new PidcVersionLoader(getServiceData());
    TPidcVersion entityObject = versLoader.getEntityObject(var.getTPidcVersion().getPidcVersId());
    entity.setTPidcVersion(entityObject);
    entityObject.getTabvProjectVariants().add(var);
    entityObject.getTabvVariantsAttrs().add(entity);
  }

  /**
   * @param value
   * @param id
   */
  private void removeEntityFromVariant(final PidcVariantAttribute value, final Long id) {
    PidcVariantLoader varLoader = new PidcVariantLoader(getServiceData());
    TabvProjectVariant var = varLoader.getEntityObject(value.getVariantId());
    PidcVariantAttributeLoader loader = new PidcVariantAttributeLoader(getServiceData());
    TabvVariantsAttr entity = loader.getEntityObject(id);
    if (var.getTabvVariantsAttrs().contains(entity)) {
      var.getTabvVariantsAttrs().remove(entity);
    }

    PidcVersionLoader versLoader = new PidcVersionLoader(getServiceData());
    TPidcVersion entityObject = versLoader.getEntityObject(var.getTPidcVersion().getPidcVersId());

    if (entityObject.getTabvVariantsAttrs().contains(entity)) {
      entityObject.getTabvVariantsAttrs().remove(entity);
    }
  }

  /**
   * @param val
   * @param long1
   */
  private void addNewEntityToPidcVersion(final PidcVersionAttribute val, final Long id) {
    PidcVersionLoader versLoader = new PidcVersionLoader(getServiceData());
    TPidcVersion tpidcVersion = versLoader.getEntityObject(val.getPidcVersId());
    PidcVersionAttributeLoader loader = new PidcVersionAttributeLoader(getServiceData());
    TabvProjectAttr entity = loader.getEntityObject(id);
    if (tpidcVersion.getTabvProjectAttrs().contains(entity)) {
      tpidcVersion.getTabvProjectAttrs().remove(entity);
    }
    tpidcVersion.getTabvProjectAttrs().add(entity);
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
