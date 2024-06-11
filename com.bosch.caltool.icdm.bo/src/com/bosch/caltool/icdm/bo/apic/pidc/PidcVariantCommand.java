package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.ArrayList;
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
import com.bosch.caltool.icdm.bo.apic.cocwp.PidcVariantCocWpCommand;
import com.bosch.caltool.icdm.bo.apic.cocwp.PidcVariantCocWpLoader;
import com.bosch.caltool.icdm.bo.cdr.RvwVariantCommand;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireRespVariantCommand;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireRespVariantLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireResponseCommand;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireResponseLoader;
import com.bosch.caltool.icdm.bo.rm.PidcRmDefinitionLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TPidcA2l;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireRespVariant;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireResponse;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvVariantsAttr;
import com.bosch.caltool.icdm.database.entity.apic.cocwp.TPidcVariantCocWp;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.cocwp.CoCWPUsedFlag;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVariantCocWp;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantData;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVariant;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;


/**
 * Command class for Project variant
 *
 * @author mkl2cob
 */
public class PidcVariantCommand extends AbstractCommand<PidcVariant, PidcVariantLoader> {


  /**
   * PidcVariantCreationData
   */
  private final PidcVariantData pidcVaraintData;


  /**
   * Constructor
   *
   * @param pidcVaraintData input data
   * @param isUpdate if true, update, else create
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public PidcVariantCommand(final ServiceData serviceData, final PidcVariantData pidcVaraintData,
      final boolean isUpdate) throws IcdmException {
    super(serviceData,
        (pidcVaraintData.isUndoDeleteNUpdateVar() ? pidcVaraintData.getDestPidcVar() : pidcVaraintData.getSrcPidcVar()),
        new PidcVariantLoader(serviceData), (isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE));
    this.pidcVaraintData = pidcVaraintData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TabvProjectVariant entity = new TabvProjectVariant();

    // check to move qnaire response from No_Variant to first created Variant
    boolean hasVariant =
        new PidcVariantLoader(getServiceData()).hasVariants(this.pidcVaraintData.getPidcVersion().getId(), false);

    // set attribute value for variant name
    AttributeValueLoader attrValueLoader = new AttributeValueLoader(getServiceData());
    AttributeValue varNameAttrValue = this.pidcVaraintData.getVarNameAttrValue();
    if (null == varNameAttrValue.getId()) {
      varNameAttrValue = createNameValue(this.pidcVaraintData.getVarNameAttrValue());
    }

    // when the attribute value is already available
    TabvAttrValue tabvAttrValue = attrValueLoader.getEntityObject(varNameAttrValue.getId());
    entity.setTabvAttrValue(tabvAttrValue);
    // if creating new revision from existing version from versions page, deleted flag must be set as per the parent
    if (this.pidcVaraintData.isNewRevision()) {
      entity.setDeletedFlag(
          this.pidcVaraintData.getDestPidcVar().isDeleted() ? ApicConstants.CODE_YES : ApicConstants.CODE_NO);
    }
    else {
      entity.setDeletedFlag(ApicConstants.CODE_NO);
    }
    if (null != this.pidcVaraintData.getRiskDefId()) {
      entity.setTPidcRmDefinition(
          new PidcRmDefinitionLoader(getServiceData()).getEntityObject(this.pidcVaraintData.getRiskDefId()));
    }
    // set pidc version
    PidcVersionLoader pidcVersionLoader = new PidcVersionLoader(getServiceData());
    TPidcVersion tpidcVersion = pidcVersionLoader.getEntityObject(this.pidcVaraintData.getPidcVersion().getId());
    entity.setTPidcVersion(tpidcVersion);
    List<TabvProjectVariant> tabvProjectVariants = tpidcVersion.getTabvProjectVariants();
    if (null == tabvProjectVariants) {
      tabvProjectVariants = new ArrayList<>();
      tpidcVersion.setTabvProjectVariants(tabvProjectVariants);
    }
    tabvProjectVariants.add(entity);

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
    // create project variant attributes for the newly created variant
    if (!this.pidcVaraintData.isNewRevision()) {
      createProjVariantAttr(tpidcVersion, entity);
    }
    // Method to move Qnaire respone from No variant to first available Variant.
    moveQnaireNoVarToVar(entity, hasVariant, tpidcVersion);

    // Method to move review result from No variant to first available Variant.
    moveRvwResultNoVarToVar(entity, hasVariant, tpidcVersion);
    // copy pidc variant coc wp
    if (!this.pidcVaraintData.isNewRevision()) {
      createPidcVariantCocWp(entity, tpidcVersion);
    }
  }

  /**
   * @param entity
   * @param hasVariant
   * @param tpidcVersion
   * @throws IcdmException
   */
  private void moveQnaireNoVarToVar(final TabvProjectVariant entity, final boolean hasVariant,
      final TPidcVersion tpidcVersion)
      throws IcdmException {
    RvwQnaireRespVariantLoader rvwQnaireRespVariantLoader = new RvwQnaireRespVariantLoader(getServiceData());
    Set<TRvwQnaireRespVariant> tRvwQnaireRespVariantsSet = tpidcVersion.getTRvwQnaireRespVariants();
    RvwQnaireRespVariantCommand respVariantCommand;
    if (!hasVariant && CommonUtils.isNotEmpty(tRvwQnaireRespVariantsSet)) {
      for (TRvwQnaireRespVariant tRvwQnaireRespVariant : tRvwQnaireRespVariantsSet) {
        RvwQnaireRespVariant qnaireRespVar =
            rvwQnaireRespVariantLoader.getDataObjectByID(tRvwQnaireRespVariant.getQnaireRespVarId());
        qnaireRespVar.setVariantId(entity.getVariantId());
        respVariantCommand = new RvwQnaireRespVariantCommand(getServiceData(), qnaireRespVar, true, false);
        executeChildCommand(respVariantCommand);
      }
    }
  }

  /**
   * @param entity
   * @param hasVariant
   * @param tpidcVersion
   * @throws IcdmException
   */
  private void moveRvwResultNoVarToVar(final TabvProjectVariant entity, final boolean hasVariant,
      final TPidcVersion tpidcVersion)
      throws IcdmException {
    if (CommonUtils.isNotEmpty(tpidcVersion.getTabvPidcA2ls())) {
      for (TPidcA2l tPidcA2l : tpidcVersion.getTabvPidcA2ls()) {
        if (!hasVariant && CommonUtils.isNotEmpty(tPidcA2l.getTRvwResults())) {
          for (TRvwResult rvwResult : tPidcA2l.getTRvwResults()) {
            createRvwVariant(entity, rvwResult);
          }
        }
      }
    }
  }

  /**
   * @param entity
   * @param rvwResult
   * @throws IcdmException
   */
  private void createRvwVariant(final TabvProjectVariant entity, final TRvwResult rvwResult) throws IcdmException {
    if (rvwResult.getTRvwVariants().isEmpty()) {
      RvwVariant rvwVariant = new RvwVariant();
      rvwVariant.setResultId(rvwResult.getResultId());
      rvwVariant.setVariantId(entity.getVariantId());
      RvwVariantCommand rvwvariantcommond = new RvwVariantCommand(getServiceData(), rvwVariant, false, false);
      executeChildCommand(rvwvariantcommond);
    }
  }

  /**
   * @param tabvProjectVariant as tabvProjectVariant
   * @param tpidcVersion as tPidcVersion
   * @throws IcdmException
   */
  private void createPidcVariantCocWp(final TabvProjectVariant tabvProjectVariant, final TPidcVersion tpidcVersion)
      throws IcdmException {
    List<TPidcVariantCocWp> pidcVariantCocWP = getPidcVariantCocWP(tabvProjectVariant, tpidcVersion);
    for (TPidcVariantCocWp tPidcVariantCocWp : pidcVariantCocWP) {
      PidcVariantCocWp variantCocWp =
          new PidcVariantCocWpLoader(getServiceData()).getDataObjectByID(tPidcVariantCocWp.getVarCocWpId());

      PidcVariantCocWp variantCocWpNew = new PidcVariantCocWp();
      variantCocWpNew.setPidcVariantId(tabvProjectVariant.getVariantId());
      variantCocWpNew.setWPDivId(variantCocWp.getWPDivId());
      variantCocWpNew.setUsedFlag(CoCWPUsedFlag.YES.getDbType());
      variantCocWpNew.setAtChildLevel(false);

      PidcVariantCocWpCommand pidcVariantCocWpCommand =
          new PidcVariantCocWpCommand(getServiceData(), variantCocWpNew, false, false);
      executeChildCommand(pidcVariantCocWpCommand);
      PidcVariantCocWp pidcVarCocWP = pidcVariantCocWpCommand.getNewData();
      new PidcCocWpUpdationBO(getServiceData()).updatePidcVarsCocWpReferenceEntity(pidcVarCocWP,
          pidcVariantCocWpCommand.getCmdMode());
    }
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
   * create project variant attributes
   *
   * @param entity
   * @param tpidcVersion
   * @throws IcdmException
   */
  private void createProjVariantAttr(final TPidcVersion tpidcVersion, final TabvProjectVariant entity)
      throws IcdmException {
    // Check if PID has atleast one VARIANT
    if (tpidcVersion.getTabvProjectVariants().size() > 1) {
      final List<TabvVariantsAttr> pidcVarAttrs = getVariantAttrs(entity, tpidcVersion);
      // Create default attributes for the new variant
      if (pidcVarAttrs != null) {
        Long valueToSet = null;
        PidcVariantAttributeLoader pidcVarAttrLoader = new PidcVariantAttributeLoader(getServiceData());
        for (TabvVariantsAttr pidcVarAttr : pidcVarAttrs) {
          PidcVariantAttribute varAttr = pidcVarAttrLoader.getDataObjectByID(pidcVarAttr.getVarAttrId());
          PidcVariantAttribute newPIDCVarAttr = new PidcVariantAttribute();


          // creation of variant from PIDCDetailsNode
          if (this.pidcVaraintData.getStructAttrValueMap() != null) {
            valueToSet = this.pidcVaraintData.getStructAttrValueMap().get(pidcVarAttr.getTabvAttribute().getAttrId());
          }
          // ICDM-406
          if (CommonUtils.isNotNull(valueToSet)) {
            newPIDCVarAttr.setValueId(valueToSet);
            newPIDCVarAttr.setUsedFlag(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType());
          }
          else {
            newPIDCVarAttr.setValueId(null);
            newPIDCVarAttr.setUsedFlag(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType());
          }
          // ICDM-372
          newPIDCVarAttr.setPartNumber("");
          newPIDCVarAttr.setSpecLink("");
          newPIDCVarAttr.setAdditionalInfoDesc("");
          newPIDCVarAttr.setAttrId(varAttr.getAttrId());
          newPIDCVarAttr.setVariantId(entity.getVariantId());
          newPIDCVarAttr.setPidcVersionId(tpidcVersion.getPidcVersId());

          PidcVariantAttributeCommand pidcVarAttrCommand =
              new PidcVariantAttributeCommand(getServiceData(), newPIDCVarAttr, false, false);
          executeChildCommand(pidcVarAttrCommand);

        }
      }
    }
  }

  /**
   * @param entity
   * @param tpidcVersion
   * @return
   */
  private List<TabvVariantsAttr> getVariantAttrs(final TabvProjectVariant entity, final TPidcVersion tpidcVersion) {
    // Get VARIANT attributes list from any one of the VARIANT
    List<TabvVariantsAttr> pidcVarAttrs = null;
    for (TabvProjectVariant pidVar : tpidcVersion.getTabvProjectVariants()) {
      if (entity.getVariantId() == pidVar.getVariantId()) {
        continue;
      }
      // ICDM-1360
      pidcVarAttrs = pidVar.getTabvVariantsAttrs();
      // any one variant attributes list is sufficient, hence break
      break;
    }


    return pidcVarAttrs;
  }

  /**
   * @param tabvProjectVariant as TabvProjectVariant
   * @param tpidcVersion tpidcVersion
   * @return
   */
  private List<TPidcVariantCocWp> getPidcVariantCocWP(final TabvProjectVariant tabvProjectVariant,
      final TPidcVersion tpidcVersion) {
    // Get TPidcVariantCocWp list from any one of the VARIANT
    List<TPidcVariantCocWp> tpidcVarCocWpList = new ArrayList<>();
    for (TabvProjectVariant pidVar : tpidcVersion.getTabvProjectVariants()) {
      if (tabvProjectVariant.getVariantId() == pidVar.getVariantId()) {
        continue;
      }
      // ICDM-1360
      tpidcVarCocWpList = pidVar.gettPidcVarCocWp();
      // any one TPidcVariantCocWp list is sufficient, hence break
      break;
    }
    return tpidcVarCocWpList;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    PidcVariantLoader loader = new PidcVariantLoader(getServiceData());
    PidcVariant pidcVar = this.pidcVaraintData.isUndoDeleteNUpdateVar() ? this.pidcVaraintData.getDestPidcVar()
        : this.pidcVaraintData.getSrcPidcVar();
    TabvProjectVariant entity = loader.getEntityObject(pidcVar.getId());

    AttributeValueLoader attrValueLoader = new AttributeValueLoader(getServiceData());
    TabvAttrValue tabvAttrValue = attrValueLoader.getEntityObject(pidcVar.getNameValueId());

    checkForVariantNameUpdate(pidcVar, entity, attrValueLoader, tabvAttrValue);

    String inputVarDeleted = getInputData().isDeleted() ? CommonUtilConstants.CODE_YES : CommonUtilConstants.CODE_NO;

    if (!entity.getDeletedFlag().equals(inputVarDeleted)) {
      for (TRvwQnaireRespVariant rvwQnaireRespVariant : entity.getTRvwQnaireRespVariants()) {
        TRvwQnaireResponse tRvwQnaireResponse = rvwQnaireRespVariant.getTRvwQnaireResponse();
        // tRvwQnaireResponse is Null for Simplified Qnaire
        if (CommonUtils.isNotNull(tRvwQnaireResponse)) {
          RvwQnaireResponse qnaireResponse =
              new RvwQnaireResponseLoader(getServiceData()).getDataObjectByID(tRvwQnaireResponse.getQnaireRespId());
          qnaireResponse.setDeletedFlag(getInputData().isDeleted());

          RvwQnaireResponseCommand qnaireResponseCommand =
              new RvwQnaireResponseCommand(getServiceData(), qnaireResponse, true, false, true);
          executeChildCommand(qnaireResponseCommand);
        }
      }
    }

    entity.setDeletedFlag(getInputData().isDeleted() ? ApicConstants.CODE_YES : ApicConstants.CODE_NO);

    // set pidc version
    PidcVersionLoader pidcVersionLoader = new PidcVersionLoader(getServiceData());
    entity.setTPidcVersion(pidcVersionLoader.getEntityObject(getInputData().getPidcVersionId()));

    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * @param pidcVar
   * @param entity
   * @param attrValueLoader
   * @param tabvAttrValue
   * @throws DataException
   * @throws IcdmException
   */
  private void checkForVariantNameUpdate(final PidcVariant pidcVar, final TabvProjectVariant entity,
      final AttributeValueLoader attrValueLoader, final TabvAttrValue tabvAttrValue)
      throws IcdmException {
    // Check if data changed
    if (isNameUpdated()) {
      // ICDM-767
      final Map<Long, AttributeValue> varList =
          attrValueLoader.getAttrValues(tabvAttrValue.getTabvAttribute().getAttrId());

      boolean varNameAlreadyExists = false;
      for (AttributeValue var : varList.values()) {
        if (CommonUtils.isEqual(this.pidcVaraintData.getVarNameAttrValue().getTextValueEng(), var.getTextValueEng())) {
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
        if (isVarNameUsed(pidcVar)) {
          // create new attr value
          AttributeValue newAttrValue = createNameValue(this.pidcVaraintData.getVarNameAttrValue());
          final TabvAttrValue newDbVarName = attrValueLoader.getEntityObject(newAttrValue.getId());
          entity.setTabvAttrValue(newDbVarName);

        }
        else {
          // if it is not used in any other pidc
          updateName(entity.getTabvAttrValue().getValueId());
        }
      }
    }
  }

  /**
   * @param pidcVar
   * @return
   */
  private boolean isVarNameUsed(final PidcVariant pidcVar) {


    final String query = "SELECT var.variantId from TabvProjectVariant var where var.tabvAttrValue.valueId = '" +
        pidcVar.getNameValueId() + "' and var.variantId != '" + pidcVar.getId() + "'";

    final EntityManager entMgr = getEm();
    final TypedQuery<TabvProjectVariant> typeQuery = entMgr.createQuery(query, TabvProjectVariant.class);
    final List<TabvProjectVariant> varList = typeQuery.setMaxResults(1).getResultList();

    return !varList.isEmpty();


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
    modifyAttrValue.setTextValueEng(this.pidcVaraintData.getVarNameAttrValue().getTextValueEng());
    modifyAttrValue.setTextValueGer(this.pidcVaraintData.getVarNameAttrValue().getTextValueGer());
    modifyAttrValue.setDescriptionEng(this.pidcVaraintData.getVarNameAttrValue().getDescriptionEng());
    modifyAttrValue.setDescriptionGer(this.pidcVaraintData.getVarNameAttrValue().getDescriptionGer());
    AttrValueCommand attrValCommand = new AttrValueCommand(getServiceData(), modifyAttrValue, true, false);
    executeChildCommand(attrValCommand);

  }

  /**
   * @return
   */
  private boolean isNameUpdated() {
    return this.pidcVaraintData.isNameUpdated();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    // not applicable
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
