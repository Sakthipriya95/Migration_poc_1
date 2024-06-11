package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttrValueCommand;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.bo.apic.cocwp.PidcCocWpUpdationBO;
import com.bosch.caltool.icdm.bo.apic.cocwp.PidcSubVarCocWpCommand;
import com.bosch.caltool.icdm.bo.apic.cocwp.PidcSubVarCocWpLoader;
import com.bosch.caltool.icdm.bo.apic.cocwp.PidcVariantCocWpLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjSubVariantsAttr;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectSubVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvVariantsAttr;
import com.bosch.caltool.icdm.database.entity.apic.cocwp.TPidcSubVarCocWp;
import com.bosch.caltool.icdm.database.entity.apic.cocwp.TPidcVariantCocWp;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.cocwp.CoCWPUsedFlag;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcSubVarCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVariantCocWp;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;


/**
 * Command class for Pidc Sub Variant
 *
 * @author mkl2cob
 */
public class PidcSubVariantCommand extends AbstractCommand<PidcSubVariant, PidcSubVariantLoader> {


  private final PidcSubVariantData pidcSubVarData;

  /**
   * Constructor
   *
   * @param isUpdate if true, update, else create
   * @param serviceData service Data
   * @param pidcSubVarData pidc sub variant creation data
   * @throws IcdmException error when initializing
   */
  public PidcSubVariantCommand(final ServiceData serviceData, final PidcSubVariantData pidcSubVarData,
      final boolean isUpdate) throws IcdmException {
    super(serviceData,
        (pidcSubVarData.isUndoDeleteNUpdateSubVar() ? pidcSubVarData.getDestPidcSubVar()
            : pidcSubVarData.getSrcPidcSubVar()),
        new PidcSubVariantLoader(serviceData), (isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE));
    this.pidcSubVarData = pidcSubVarData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TabvProjectSubVariant entity = new TabvProjectSubVariant();

    PidcVariantLoader pidcVarLoader = new PidcVariantLoader(getServiceData());

    TabvProjectVariant tabvVar = pidcVarLoader.getEntityObject(this.pidcSubVarData.getPidcVariantId());
    // getting tPidcVersion from TabvProjectVariant
    TPidcVersion tPidcVersion = tabvVar.getTPidcVersion();

    entity.setTabvProjectVariant(tabvVar);
    List<TabvProjectSubVariant> tabvProjectSubVariants = tabvVar.getTabvProjectSubVariants();
    if (null == tabvProjectSubVariants) {
      tabvProjectSubVariants = new ArrayList<>();
      tabvVar.setTabvProjectSubVariants(tabvProjectSubVariants);
    }
    tabvProjectSubVariants.add(entity);
    // Adding newly created Sub variant to tPidcVersion entity
    List<TabvProjectSubVariant> tabvProjectSubVariantsFromVersion = tPidcVersion.getTabvProjectSubVariants();
    if (null == tabvProjectSubVariantsFromVersion) {
      tabvProjectSubVariantsFromVersion = new ArrayList<>();
      tPidcVersion.setTabvProjectSubVariants(tabvProjectSubVariantsFromVersion);
    }
    tabvProjectSubVariantsFromVersion.add(entity);

    // set attribute value for variant name
    AttributeValueLoader attrValueLoader = new AttributeValueLoader(getServiceData());
    AttributeValue varNameAttrValue = this.pidcSubVarData.getSubvarNameAttrValue();
    if (null == varNameAttrValue.getId()) {
      varNameAttrValue = createNameValue(this.pidcSubVarData.getSubvarNameAttrValue());
    }
    // when the attribute value is already available
    TabvAttrValue tabvAttrValue = attrValueLoader.getEntityObject(varNameAttrValue.getId());
    entity.setTabvAttrValue(tabvAttrValue);
    // if creating new revision from existing version from versions page, deleted flag must be set as per the parent
    if (this.pidcSubVarData.isNewRevison()) {
      entity.setDeletedFlag(
          this.pidcSubVarData.getDestPidcSubVar().isDeleted() ? ApicConstants.CODE_YES : ApicConstants.CODE_NO);
    }
    else {
      entity.setDeletedFlag(ApicConstants.CODE_NO);
    }

    entity.setTPidcVersion(tabvVar.getTPidcVersion());

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
    if (!this.pidcSubVarData.isNewRevison()) {
      createSubVariantAttributes(entity, tabvVar);
    }
    // copy pidcSubVariantCocWp to new SubVaiant
    if (!this.pidcSubVarData.isNewRevison()) {
      createPidcSubVarCocWp(entity, tabvVar);
      getPidcVarCocWPForUpdate(tabvVar);
    }
  }

  /**
   * @param tabvProjectVar as input
   * @throws DataException
   */
  private void getPidcVarCocWPForUpdate(final TabvProjectVariant tabvProjectVar) throws DataException {
    Set<PidcVariantCocWp> pidcVarCocWpSet = new HashSet<>();
    for (TPidcVariantCocWp tPidcVariantCocWp : tabvProjectVar.gettPidcVarCocWp()) {
      pidcVarCocWpSet
          .add(new PidcVariantCocWpLoader(getServiceData()).getDataObjectByID(tPidcVariantCocWp.getVarCocWpId()));
    }
    this.pidcSubVarData.setPidcVarCocWpSet(pidcVarCocWpSet);
  }

  /**
   * @param entity
   * @param tabvVar
   * @throws DataException
   * @throws IcdmException
   */
  private void createPidcSubVarCocWp(final TabvProjectSubVariant entity, final TabvProjectVariant tabvVar)
      throws IcdmException {
    for (TPidcSubVarCocWp tPidcSubVarCocWp : getPidcSubVariantCocWP(entity, tabvVar)) {
      PidcSubVarCocWp subVarCocWp =
          new PidcSubVarCocWpLoader(getServiceData()).getDataObjectByID(tPidcSubVarCocWp.getSubVarCocWpId());

      PidcSubVarCocWp subVarCocWpNew = new PidcSubVarCocWp();
      subVarCocWpNew.setAtChildLevel(false);
      subVarCocWpNew.setPidcSubVarId(entity.getSubVariantId());
      subVarCocWpNew.setUsedFlag(CoCWPUsedFlag.YES.getDbType());
      subVarCocWpNew.setWPDivId(subVarCocWp.getWPDivId());

      PidcSubVarCocWpCommand pidcSubVarCocWpCommand =
          new PidcSubVarCocWpCommand(getServiceData(), subVarCocWpNew, false, false);
      executeChildCommand(pidcSubVarCocWpCommand);
      PidcSubVarCocWp pidcSubVarCocWp = pidcSubVarCocWpCommand.getNewData();
      // to update the entity relations for pidcSubVarCocWp
      new PidcCocWpUpdationBO(getServiceData()).updatePidcSubVarsCocWpReferenceEntity(pidcSubVarCocWp,
          pidcSubVarCocWpCommand.getCmdMode());
    }
  }

  /**
   * @param entity TabvProjectVariant
   * @param tpidcVersion tpidcVersion
   * @return
   */
  private List<TPidcSubVarCocWp> getPidcSubVariantCocWP(final TabvProjectSubVariant entity,
      final TabvProjectVariant tabvProjectVariant) {
    // Get TPidcSubVarCocWp list from any one of the VARIANT
    List<TPidcSubVarCocWp> tpidcSubVarCocWpList = new ArrayList<>();
    for (TabvProjectSubVariant pidSubVar : tabvProjectVariant.getTabvProjectSubVariants()) {
      if (entity.getSubVariantId() == pidSubVar.getSubVariantId()) {
        continue;
      }
      // ICDM-1360
      tpidcSubVarCocWpList = pidSubVar.gettPidcSubVarCocWp();
      // any one TPidcSubVarCocWp list is sufficient, hence break
      break;
    }
    return tpidcSubVarCocWpList;
  }

  /**
   * @param varNameAttrValue AttributeValue
   * @return
   * @throws IcdmException
   */
  private AttributeValue createNameValue(final AttributeValue varNameAttrValue) throws IcdmException {
    AttrValueCommand attrValueCommand = new AttrValueCommand(getServiceData(), varNameAttrValue, false, false);
    executeChildCommand(attrValueCommand);
    return attrValueCommand.getNewData();
  }

  /**
   * @param tabvVar
   * @param entity
   * @throws IcdmException
   */
  private void createSubVariantAttributes(final TabvProjectSubVariant entity, final TabvProjectVariant tabvVar)
      throws IcdmException {
    List<PidcSubVariantAttribute> pidcSubVarAttrs;
    TabvProjectVariant srcTabVar = tabvVar;

    if (this.pidcSubVarData.isSubVarCopiedAlongWithVariant()) {
      PidcVariantLoader pidcVarLoader = new PidcVariantLoader(getServiceData());
      srcTabVar = pidcVarLoader.getEntityObject(this.pidcSubVarData.getSrcPidcSubVar().getPidcVariantId());
    }

    if (!CommonUtils.isNullOrEmpty(this.pidcSubVarData.getStructAttrValueMap()) &&
        (tabvVar.getTabvProjectSubVariants().size() == 1)) {
      // when subvar is created from virtual node
      pidcSubVarAttrs = copyVarAttrToSubVarAttr(entity, tabvVar);
    }
    else {
      // Check if PID has atleast one sub VARIANT
      pidcSubVarAttrs = getSubVariantAttrs(entity, srcTabVar);

    }
    // Create default attributes for the new variant
    Long valueToSet = null;
    for (PidcSubVariantAttribute pidcSubVarAttr : pidcSubVarAttrs) {
      PidcSubVariantAttribute newPIDCSubVarAttr = new PidcSubVariantAttribute();


      // creation of sub variant from PIDCDetailsNode

      if (this.pidcSubVarData.getStructAttrValueMap() != null) {
        valueToSet = this.pidcSubVarData.getStructAttrValueMap().get(pidcSubVarAttr.getAttrId());
        if (tabvVar.getTabvProjectSubVariants().size() == 1) {
          moveVarAttrToSubVarLevel();
        }
      }
      // ICDM-406
      if (CommonUtils.isNotNull(valueToSet)) {
        newPIDCSubVarAttr.setValueId(valueToSet);
        newPIDCSubVarAttr.setUsedFlag(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType());
      }
      else {
        newPIDCSubVarAttr.setValue(null);
        newPIDCSubVarAttr.setUsedFlag(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType());
      }
      // ICDM-372
      newPIDCSubVarAttr.setPartNumber("");
      newPIDCSubVarAttr.setSpecLink("");
      newPIDCSubVarAttr.setAdditionalInfoDesc("");
      newPIDCSubVarAttr.setAttrId(pidcSubVarAttr.getAttrId());
      newPIDCSubVarAttr.setVariantId(tabvVar.getVariantId());
      newPIDCSubVarAttr.setSubVariantId(entity.getSubVariantId());
      newPIDCSubVarAttr.setPidcVersionId(pidcSubVarAttr.getPidcVersionId());

      PidcSubVariantAttributeCommand pidcVarAttrCommand =
          new PidcSubVariantAttributeCommand(getServiceData(), newPIDCSubVarAttr, false, false);
      executeChildCommand(pidcVarAttrCommand);
    }
  }


  /**
   * @param tabvVar
   * @throws IcdmException
   */
  private void moveVarAttrToSubVarLevel() throws IcdmException {

    // applicable while creating subvariant from a virtual node
    // if the attribute on which the virtual structure is created is in variant level , it should be moved down

    PidcVariantLoader varLoader = new PidcVariantLoader(getServiceData());
    PidcVariantAttributeLoader varAttrLoader = new PidcVariantAttributeLoader(getServiceData());
    for (Long attrId : this.pidcSubVarData.getStructAttrValueMap().keySet()) {
      TabvProjectVariant varEntity = varLoader.getEntityObject(this.pidcSubVarData.getPidcVariantId());
      if (!CommonUtils.isNullOrEmpty(varEntity.getTabvProjectSubVariants())) {
        // the attribute should be moved down to subvariant level if its in variant level
        Long varAttrId = null;
        List<TabvVariantsAttr> tabvVariantsAttrs = varEntity.getTabvVariantsAttrs();
        for (TabvVariantsAttr tabvVariantAttr : tabvVariantsAttrs) {
          if (attrId.equals(tabvVariantAttr.getTabvAttribute().getAttrId())) {
            varAttrId = tabvVariantAttr.getVarAttrId();
            break;
          }
        }
        PidcVariantAttribute varAttr = varAttrLoader.getDataObjectByID(varAttrId);
        if (!varAttr.isAtChildLevel()) {
          varAttr.setAtChildLevel(true);
          PidcVariantAttributeCommand pidcVarAttrCommand =
              new PidcVariantAttributeCommand(getServiceData(), varAttr, true, false);
          executeChildCommand(pidcVarAttrCommand);
        }
      }
    }
  }


  /**
   * @param entity
   * @param tabvVar
   * @return
   * @throws DataException
   */
  private List<PidcSubVariantAttribute> getSubVariantAttrs(final TabvProjectSubVariant entity,
      final TabvProjectVariant tabvVar)
      throws DataException {
    PidcSubVariantAttributeLoader loader = new PidcSubVariantAttributeLoader(getServiceData());
    // Get VARIANT attributes list from any one of the VARIANT
    List<PidcSubVariantAttribute> pidcVarAttrs = new ArrayList<>();
    for (TabvProjectSubVariant pidSubVar : tabvVar.getTabvProjectSubVariants()) {
      if (entity.getSubVariantId() == pidSubVar.getSubVariantId()) {
        continue;
      }
      for (TabvProjSubVariantsAttr tabVSubVarAttr : pidSubVar.getTabvProjSubVariantsAttrs()) {
        pidcVarAttrs.add(loader.getDataObjectByID(tabVSubVarAttr.getSubVarAttrId()));
      }

      // ICDM-1360
      // any one variant attributes list is sufficient, hence break
      break;
    }
    return pidcVarAttrs;
  }

  private List<PidcSubVariantAttribute> copyVarAttrToSubVarAttr(final TabvProjectSubVariant entity,
      final TabvProjectVariant tabvVar) {

    // applicable while creating subvariant from a virtual node
    // if the attribute on which the virtual structure is created is in variant level , it should be moved down

    List<PidcSubVariantAttribute> retList = new ArrayList<>();
    for (Long attrId : this.pidcSubVarData.getStructAttrValueMap().keySet()) {
      Long varAttrId = null;
      List<TabvVariantsAttr> tabvVariantsAttrs = tabvVar.getTabvVariantsAttrs();
      for (TabvVariantsAttr tabvVariantAttr : tabvVariantsAttrs) {
        if (attrId.equals(tabvVariantAttr.getTabvAttribute().getAttrId())) {
          varAttrId = tabvVariantAttr.getVarAttrId();
          break;
        }
      }
      TabvVariantsAttr entityObject = new PidcVariantAttributeLoader(getServiceData()).getEntityObject(varAttrId);
      PidcSubVariantAttribute subVarAttr = new PidcSubVariantAttribute();
      subVarAttr.setAtChildLevel(true);
      subVarAttr.setAttrId(attrId);
      subVarAttr.setValueId(this.pidcSubVarData.getStructAttrValueMap().get(attrId));
      subVarAttr.setPidcVersionId(entityObject.getTPidcVersion().getPidcVersId());
      subVarAttr.setVariantId(this.pidcSubVarData.getPidcVariantId());
      subVarAttr.setSubVariantId(entity.getSubVariantId());
      retList.add(subVarAttr);
    }
    return retList;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {

    PidcSubVariant srcPidcSubVar = this.pidcSubVarData.isUndoDeleteNUpdateSubVar()
        ? this.pidcSubVarData.getDestPidcSubVar() : this.pidcSubVarData.getSrcPidcSubVar();
    PidcSubVariantLoader loader = new PidcSubVariantLoader(getServiceData());
    TabvProjectSubVariant entity = loader.getEntityObject(srcPidcSubVar.getId());

    PidcVariantLoader pidcVarLoader = new PidcVariantLoader(getServiceData());
    entity.setTabvProjectVariant(pidcVarLoader.getEntityObject(srcPidcSubVar.getPidcVariantId()));

    AttributeValueLoader attrValueLoader = new AttributeValueLoader(getServiceData());
    TabvAttrValue tabvAttrValue = attrValueLoader.getEntityObject(srcPidcSubVar.getNameValueId());
    entity.setTabvAttrValue(tabvAttrValue);

    // Check if data changed
    if (isNameUpdated()) {
      // ICDM-767
      final Map<Long, AttributeValue> subVarList =
          attrValueLoader.getAttrValues(tabvAttrValue.getTabvAttribute().getAttrId());

      boolean varNameAlreadyExists = false;
      for (AttributeValue var : subVarList.values()) {
        if (CommonUtils.isEqual(this.pidcSubVarData.getSubvarNameAttrValue().getTextValueEng(),
            var.getTextValueEng())) {
          // if new variant name already exists, change the mapping of attr value
          final TabvAttrValue newDbVarName = attrValueLoader.getEntityObject(var.getId());
          entity.setTabvAttrValue(newDbVarName);
          varNameAlreadyExists = true;
          break;
        }
      }
      if (varNameAlreadyExists) {
        // this is to update description
        // if it is not used in any other pidc
        updateName(entity.getTabvAttrValue().getValueId());
      }
      else {
        // if the new variant name doesnot exist
        if (isSubVarNameUsed(srcPidcSubVar)) {
          // create new attr value
          AttributeValue newAttrValue = createNameValue(this.pidcSubVarData.getSubvarNameAttrValue());
          final TabvAttrValue newDbVarName = attrValueLoader.getEntityObject(newAttrValue.getId());
          entity.setTabvAttrValue(newDbVarName);

        }
        else {
          // if it is not used in any other pidc
          updateName(entity.getTabvAttrValue().getValueId());
        }
      }


    }

    entity.setDeletedFlag(srcPidcSubVar.isDeleted() ? ApicConstants.CODE_YES : ApicConstants.CODE_NO);

    PidcVersionLoader pidcVersionLoader = new PidcVersionLoader(getServiceData());
    entity.setTPidcVersion(pidcVersionLoader.getEntityObject(srcPidcSubVar.getPidcVersionId()));

    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * @param subVariant
   * @return
   */
  private boolean isSubVarNameUsed(final PidcSubVariant subVariant) {
    final String query = "SELECT var.subVariantId from TabvProjectSubVariant var where var.tabvAttrValue.valueId = '" +
        subVariant.getNameValueId() + "' and var.subVariantId != '" + subVariant.getId() + "'";

    final EntityManager entMgr = getEm();
    final TypedQuery<TabvProjectSubVariant> typeQuery = entMgr.createQuery(query, TabvProjectSubVariant.class);
    final List<TabvProjectSubVariant> subVarList = typeQuery.setMaxResults(1).getResultList();

    return !subVarList.isEmpty();
  }

  /**
   * Update the name of this PIDC.
   *
   * @param valueID value ID
   * @throws IcdmException
   */
  protected final void updateName(final Long valueID) throws IcdmException {

    AttributeValueLoader attrValueLoader = new AttributeValueLoader(getServiceData());
    final AttributeValue modifyAttrValue = attrValueLoader.getDataObjectByID(valueID);
    modifyAttrValue.setTextValueEng(this.pidcSubVarData.getSubvarNameAttrValue().getTextValueEng());
    modifyAttrValue.setTextValueGer(this.pidcSubVarData.getSubvarNameAttrValue().getTextValueGer());
    modifyAttrValue.setDescriptionEng(this.pidcSubVarData.getSubvarNameAttrValue().getDescriptionEng());
    modifyAttrValue.setDescriptionGer(this.pidcSubVarData.getSubvarNameAttrValue().getDescriptionGer());
    AttrValueCommand attrValCommand = new AttrValueCommand(getServiceData(), modifyAttrValue, true, false);
    executeChildCommand(attrValCommand);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    // not applicable
  }

  /**
   * @return
   */
  private boolean isNameUpdated() {
    return this.pidcSubVarData.isNameUpdated();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // NA
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
    // NA
  }

}
