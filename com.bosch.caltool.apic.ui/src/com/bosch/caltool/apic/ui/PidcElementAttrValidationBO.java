/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui;

import java.util.Map;
import java.util.SortedSet;

import com.bosch.caltool.apic.ui.util.PIDCPageEditUtil;
import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectObjectBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcDataHandler;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.SdomPVER;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author apj4cob
 */
public class PidcElementAttrValidationBO {

  private static final String ERR_MSG_DEFAULT = "";
  private final IProjectAttribute pidcAttribute;
  private final AbstractProjectAttributeBO projectAttrHandler;
  private final AbstractProjectObjectBO projObjBO;
  private final Map<IProjectAttribute, IProjectAttribute> predefGrpAttrMap;
  private String a2lMapError;

  /**
   * @param projectAttrHandler pidc element attribute BO
   * @param predefGrpAttrMap map of predefined attribute in a pidc
   */
  public PidcElementAttrValidationBO(final AbstractProjectAttributeBO projectAttrHandler,
      final Map<IProjectAttribute, IProjectAttribute> predefGrpAttrMap) {
    this.pidcAttribute = projectAttrHandler.getProjectAttr();
    this.projectAttrHandler = projectAttrHandler;
    this.projObjBO = projectAttrHandler.getProjectObjectBO();
    this.predefGrpAttrMap = predefGrpAttrMap;
  }

  /**
   * @return error message
   */
  public String validatePaste() {
    if (CommonUtils.isNotEmptyString(validateQuesConfigAttr())) {
      return validateQuesConfigAttr();
    }
    if (CommonUtils.isNotEmptyString(validatePverAttr())) {
      return validatePverAttr();
    }
    if (CommonUtils.isNotEmptyString(validateVcdmAprjAttr())) {
      return validateVcdmAprjAttr();
    }
    if (CommonUtils.isNotEmptyString(validateStructuredAttr())) {
      return validateStructuredAttr();
    }
    if (CommonUtils.isNotEmptyString(validateChildLevelAttr())) {
      return validateChildLevelAttr();
    }
    if (CommonUtils.isNotEmptyString(validateAttrVisibility())) {
      return validateAttrVisibility();
    }
    if (CommonUtils.isNotEmptyString(validatePredDefAttr())) {
      return validatePredDefAttr();
    }
    return PidcElementAttrValidationBO.ERR_MSG_DEFAULT;
  }

  private String validateStructuredAttr() {
    long attributeLevel =
        this.projObjBO.getPidcDataHandler().getAttributeMap().get(this.pidcAttribute.getAttrId()).getLevel();
    return (attributeLevel > 0 ? "Structure Attribute" : PidcElementAttrValidationBO.ERR_MSG_DEFAULT);
  }

  private String validateChildLevelAttr() {
    return (this.pidcAttribute.isAtChildLevel() ? "Target PIDC attribute at Child Level"
        : PidcElementAttrValidationBO.ERR_MSG_DEFAULT);
  }

  private String validateAttrVisibility() {
    return (!this.projectAttrHandler.isVisible() ? "Invisible Attribute" : PidcElementAttrValidationBO.ERR_MSG_DEFAULT);
  }

  private String validatePredDefAttr() {
    return (this.predefGrpAttrMap.containsKey(this.pidcAttribute) ? "Predefined Attribute"
        : PidcElementAttrValidationBO.ERR_MSG_DEFAULT);
  }

  private String validatePverAttr() {
    return (!isPverNameEditable(this.pidcAttribute, this.projObjBO) ? this.a2lMapError
        : PidcElementAttrValidationBO.ERR_MSG_DEFAULT);
  }

  private String validateVcdmAprjAttr() {
    if (this.projObjBO.getPidcDataHandler().getAttributeMap().get(this.pidcAttribute.getAttrId())
        .getLevel() == ApicConstants.VCDM_APRJ_NAME_ATTR) {
      PIDCPageEditUtil pidcPageEditUtil = new PIDCPageEditUtil(this.projObjBO);
      String aprjValue = this.pidcAttribute.getValue();

      if (pidcPageEditUtil.findAPRJID(aprjValue) != null) {
        return "vCDM transfer has already occured with another APRJ";
      }
    }
    return PidcElementAttrValidationBO.ERR_MSG_DEFAULT;
  }

  private String validateQuesConfigAttr() {
    try {
      if (CommonActionSet.isQnaireConfigNotModifiable(this.pidcAttribute, this.projObjBO)) {
        return " Review questionnaire(s) are mapped already";
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return PidcElementAttrValidationBO.ERR_MSG_DEFAULT;
  }

  /**
   * This method do validation for pver name to modify gor given pidc attribute<br>
   * 1. checks for pidc attribute has sdom level and the sdom pver name has not null value <br>
   * 2. checks the given IPIDCAttribute pidcAttribute for PIDC level <br>
   * 3. Get pver set and iterates each Pver name with old pver name <br>
   * 4. if pver have mapping to any a2l file throw error dialog <br>
   * 5. checks the given IPIDCAttribute pidcAttribute for Variant level 6. Get all variant and Iterate only active
   * variant <br>
   * 7. if other variants have the old pver name, then pver can be modified and return true <br>
   * 8. if other variants does not have the old pver name, then pver cannot be modified and return false
   *
   * @param pidcAttr
   * @return
   */
  // ICDM-1872
  private boolean isPverNameEditable(final IProjectAttribute pidcAttr, final AbstractProjectObjectBO currentProjObjBO) {
    boolean a2lFileAlreadyMappedFlag = false;

    PidcDataHandler pidcDataHandler = currentProjObjBO.getPidcDataHandler();
    if ((pidcDataHandler.getAttributeMap().get(pidcAttr.getAttrId()).getLevel()
        .equals(Long.valueOf(ApicConstants.SDOM_PROJECT_NAME_ATTR))) && (null != pidcAttr.getValueId())) {
      String oldPverName = pidcAttr.getValue();
      if (pidcAttr instanceof PidcVersionAttribute) {
        a2lFileAlreadyMappedFlag = checkIfA2LFileMapped(oldPverName, currentProjObjBO);
      }
      else if (pidcAttr instanceof PidcVariantAttribute) {

        Map<Long, PidcVariant> variantMap = pidcDataHandler.getVariantMap();

        boolean pverAvailableInOtherVariants = false;
        String variantNameInPage = variantMap.get(((PidcVariantAttribute) pidcAttr).getVariantId()).getName();
        // check if the PVER is available in other variants
        pverAvailableInOtherVariants =
            checkPverAvailableInOtherVariants(pidcAttr, pidcDataHandler, oldPverName, variantMap, variantNameInPage);
        // if pver name not available in other variants, then check for a2l file mapping
        if (!pverAvailableInOtherVariants) {
          a2lFileAlreadyMappedFlag = checkIfA2LFileMapped(oldPverName, currentProjObjBO);
        }
      }
    }
    // if a2l file not mapped, then allow to change pver name (means valid to edit the pver name)
    return !a2lFileAlreadyMappedFlag;
  }

  /**
   * @param pidcAttr
   * @param pidcDataHandler
   * @param oldPverName
   * @param variantMap
   * @param pverAvailableInOtherVariants
   * @param variantNameInPage
   * @return
   */
  private boolean checkPverAvailableInOtherVariants(final IProjectAttribute pidcAttr,
      final PidcDataHandler pidcDataHandler, final String oldPverName, final Map<Long, PidcVariant> variantMap,
      final String variantNameInPage) {
    for (PidcVariant pidcVariant : variantMap.values()) {
      if (!pidcVariant.isDeleted()) {
        String variantNameInSet = pidcVariant.getName();
        if (isPverAvailableInVariants(variantNameInSet, variantNameInPage, pidcAttr, pidcDataHandler, oldPverName,
            pidcVariant)) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean isPverAvailableInVariants(final String variantNameInSet, final String variantNameInPage,
      final IProjectAttribute pidcAttr, final PidcDataHandler pidcDataHandler, final String oldPverName,
      final PidcVariant pidcVariant) {
    // check with other variants whether this pver name availabe for
    // other variants and
    // --> if available, allow to change pver name
    // --> if not available, check for a2l file mapping with version
    if (!CommonUtils.isEqualIgnoreCase(variantNameInSet, variantNameInPage)) {
      Map<Long, PidcVariantAttribute> map = pidcDataHandler.getVariantAttributeMap().get(pidcVariant.getId());
      AttributeValue attributeValue = pidcDataHandler.getAttributeValue(map.get(pidcAttr.getAttrId()).getValueId());
      if (attributeValue != null) {
        String pverValue = attributeValue.getName();
        if (CommonUtils.isEqualIgnoreCase(pverValue, oldPverName)) {
          return true;

        }
      }
    }
    return false;
  }

  /**
   * This method used to check if any a2l files mapped to this pidc version and if available throw error dialog
   */
  private boolean checkIfA2LFileMapped(final String oldPverName, final AbstractProjectObjectBO handler) {

    SortedSet<SdomPVER> pverSet = handler.getPVerSet();
    for (SdomPVER pver : pverSet) {
      if (CommonUtils.isEqualIgnoreCase(pver.getPverName(), oldPverName) &&
          !handler.getMappedA2LFiles(pver.getPverName(), pver.getPidcVersion().getId()).isEmpty()) {
        this.a2lMapError = "";
        try {
          this.a2lMapError = new CommonDataBO().getMessage("PIDC_EDITOR", "A2L_MAP_ERROR");
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
        }
        return true;
      }
    }
    return false;
  }
}
