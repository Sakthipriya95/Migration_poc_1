/*
 * nt * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic.pidc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.apic.ApicDataBO;
import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.client.bo.apic.AttributeValueWithDetails;
import com.bosch.caltool.icdm.client.bo.apic.PidcDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueDummy;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValueAndValidtyModel;
import com.bosch.caltool.icdm.model.apic.attr.ProjectAttributesUpdationModel;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.ws.rest.client.apic.PredefinedAttrValueServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.ProjectAttributesUpdationServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author pdh2cob
 */
public class PidcAttrValueEditBO {

  private final PidcDataHandler pidcDataHandler;

  private final PidcVersionBO pidcVersionBO;

  private final IModel apicObject;

  private Attribute attribute;

  /**
   * @param pidcVersionBO - pidc version bo
   * @param apicObject apicObject
   */
  public PidcAttrValueEditBO(final PidcVersionBO pidcVersionBO, final IModel apicObject) {
    this.pidcVersionBO = pidcVersionBO;
    this.pidcDataHandler = pidcVersionBO.getPidcDataHandler();
    this.apicObject = apicObject;
    this.attribute = getAttributeFromApicObject();
  }


  /**
   * @param valueId value id
   * @return PredefinedAttrValueAndValidtyModel
   */
  public PredefinedAttrValueAndValidtyModel getPredefinedAttrValueAndValidtyModel(final Long valueId) {

    PredefinedAttrValueAndValidtyModel model = new PredefinedAttrValueAndValidtyModel();
    Set<Long> valueIdSet = new HashSet<>();
    valueIdSet.add(valueId);
    try {
      // service to fetch predefined attribute values and validity for value ids
      model = new PredefinedAttrValueServiceClient().getPredefinedAttrValuesAndValidityForValueSet(valueIdSet);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return model;
  }

  /**
   * @param predefinedAttrValueMap predefined attribute value
   * @param attr attribute
   */
  public void saveGrpAttrValueWithoutPredefAttr(final Map<Long, Map<Long, PredefinedAttrValue>> predefinedAttrValueMap,
      final Attribute attr) {
    if (attr.isGroupedAttr() && CommonUtils.isNullOrEmpty(predefinedAttrValueMap)) {
      ProjectAttributesUpdationModel updationModel = new ProjectAttributesUpdationModel();
      updationModel.setPidcVersion(this.pidcVersionBO.getPidcVersion());
      Long prevGrpdAttrValId = fetchPrevAttrValId();
      // prevGrpdAttrValId can be null if we are setting grpd attr value for first time
      if (hasPredefAttr(prevGrpdAttrValId)) {
        resetExistingPredAttrVal(updationModel, prevGrpdAttrValId, this.apicObject);
        saveAttributes(updationModel);
      }
    }
  }


  /**
   * @param prevGrpdAttrValId Long
   * @return boolean
   */
  public boolean hasPredefAttr(final Long prevGrpdAttrValId) {
    return (prevGrpdAttrValId != null) &&
        ((null != this.pidcDataHandler.getPreDefAttrValMap().get(prevGrpdAttrValId)) &&
            !this.pidcDataHandler.getPreDefAttrValMap().get(prevGrpdAttrValId).isEmpty());
  }


  /**
   * @param attributeClientBO
   * @return
   */
  private AttributeValueWithDetails getAttrValueDetailsForPidcAttr(final boolean isCompareEditor) {
    ConcurrentMap<Long, IProjectAttribute> projAttrToCheckMap = new ConcurrentHashMap<>();
    if (isCompareEditor) {
      // TODO
    }
    else {
      projAttrToCheckMap.putAll(this.pidcDataHandler.getPidcVersAttrMap());
    }
    return new AttributeClientBO(this.attribute).getValidAttrValuesWithDetails(projAttrToCheckMap,
        this.pidcDataHandler);
  }


  /**
   * @param attributeClientBO
   * @return
   */
  private AttributeValueWithDetails getAttrValueDetailsForVarAttr(final boolean isCompareEditor) {
    ConcurrentMap<Long, IProjectAttribute> attrMapToValidateDepns = new ConcurrentHashMap<>();
    PidcVariantAttribute selVarAttr = (PidcVariantAttribute) this.apicObject;
    // Add Variant Attributes and PIDC attributes if PIDCAttributeVar
    if (isCompareEditor) {
      // TODO
    }
    else {
      // Add the verstion attributes also here
      attrMapToValidateDepns.putAll(this.pidcDataHandler.getPidcVersAttrMap());
      attrMapToValidateDepns.putAll(this.pidcDataHandler.getVariantAttributeMap().get(selVarAttr.getVariantId()));
      for (Entry<Long, PidcVariantAttribute> entry : this.pidcDataHandler.getVariantAttributeMap()
          .get(selVarAttr.getVariantId()).entrySet()) {
        attrMapToValidateDepns.put(entry.getKey(), entry.getValue());
      }
    }

    return new AttributeClientBO(this.attribute).getValidAttrValuesWithDetails(attrMapToValidateDepns,
        this.pidcDataHandler);
  }

  /**
   * @param attributeClientBO
   * @return
   */
  private AttributeValueWithDetails getAttrValueDetailsForSubvarAttr(final boolean isCompareEditor) {

    ConcurrentMap<Long, IProjectAttribute> attrMapToValidateDepns = new ConcurrentHashMap<>();
    // Add Sub Variant Attributes,Variant Attributes and PIDC attributes if PIDCAttributeVar
    PidcSubVariantAttribute selSubVarAttr = (PidcSubVariantAttribute) this.apicObject;
    if (isCompareEditor) {
      // TODO
    }
    else {

      // Add the verstion attributes also here
      attrMapToValidateDepns.putAll(this.pidcDataHandler.getPidcVersAttrMap());
      // add also varaint level attributes here
      attrMapToValidateDepns.putAll(this.pidcDataHandler.getVariantAttributeMap().get(selSubVarAttr.getVariantId()));

      attrMapToValidateDepns
          .putAll(this.pidcDataHandler.getSubVariantAttributeMap().get(selSubVarAttr.getSubVariantId()));
      for (Entry<Long, PidcVariantAttribute> entry : this.pidcDataHandler.getVariantAttributeMap()
          .get(selSubVarAttr.getVariantId()).entrySet()) {
        attrMapToValidateDepns.put(entry.getKey(), entry.getValue());
      }
      for (Entry<Long, PidcSubVariantAttribute> entry : this.pidcDataHandler.getSubVariantAttributeMap()
          .get(selSubVarAttr.getSubVariantId()).entrySet()) {
        attrMapToValidateDepns.put(entry.getKey(), entry.getValue());
      }
    }

    return new AttributeClientBO(this.attribute).getValidAttrValuesWithDetails(attrMapToValidateDepns,
        this.pidcDataHandler);
  }

  /**
   * This method returns list of attribute values with one dummy attribute value if the includeDummyValue is true, which
   * is used while editing pidc/variant/sub-variant
   *
   * @param isCompareEditor bool val
   * @return AttributeValueWithDetails object
   */
  public AttributeValueWithDetails getAttributeValueWithDetails(final boolean isCompareEditor) {

    AttributeValueWithDetails attrValueWithDetails = new AttributeValueWithDetails();

    // Get pidc or variant attribute
    // ICDM-196 for checking the dependency in PIDC, Variant and Sub Variant Attribute level
    if (this.apicObject instanceof PidcVersionAttribute) {
      attrValueWithDetails = getAttrValueDetailsForPidcAttr(isCompareEditor);
    }
    else if (this.apicObject instanceof PidcVariantAttribute) {
      attrValueWithDetails = getAttrValueDetailsForVarAttr(isCompareEditor);
    }
    else if (this.apicObject instanceof PidcSubVariantAttribute) {
      attrValueWithDetails = getAttrValueDetailsForSubvarAttr(isCompareEditor);
    }

    return attrValueWithDetails;

  }


  /**
   * This method returns Attribute
   *
   * @return Attribute instance
   */
  private com.bosch.caltool.icdm.model.apic.attr.Attribute getAttributeFromApicObject() {
    Attribute attr = null;
    ApicDataBO apicDataBO = new ApicDataBO();
    if (this.apicObject instanceof IProjectAttribute) {
      attr = this.pidcDataHandler.getAttributeMap().get(((IProjectAttribute) this.apicObject).getAttrId());
    }
    else if (this.apicObject instanceof com.bosch.caltool.icdm.model.apic.attr.Attribute) {
      attr = (com.bosch.caltool.icdm.model.apic.attr.Attribute) this.apicObject;
    }
    else if (this.apicObject instanceof PidcVersion) {
      attr = apicDataBO.getAllLvlAttrByLevel().get((long) ApicConstants.PROJECT_NAME_ATTR);
    }
    else if (this.apicObject instanceof PidcVariant) {
      attr = apicDataBO.getAllLvlAttrByLevel().get((long) ApicConstants.VARIANT_CODE_ATTR);
    }
    else if (this.apicObject instanceof PidcSubVariant) {
      attr = apicDataBO.getAllLvlAttrByLevel().get((long) ApicConstants.SUB_VARIANT_CODE_ATTR);
    }
    return attr;
  }


  /**
   * @param validAttrValues valid attribute value
   */
  public void addDummyValue(final AttributeValueWithDetails validAttrValues) {
    boolean addDummy = true;
    // avoid adding dummy attr val twice, if an attrval is created using add button
    for (AttributeValue val : validAttrValues.getAttrValset()) {
      if (val.getAttributeId() == null) {
        addDummy = false;
        break;
      }
    }
    if (addDummy) {
      validAttrValues.getAttrValset().add(new AttributeValueDummy(this.attribute));
    }
  }


  /**
   * This method return list of pidc or variant or sub-variant attribute values for the selected
   * pidc/variant/sub-variant attribute. Incase of while creating a PIDC it will returns list of unused pidc names or
   * while creating variant it will returns list of unused pidc variant names or while creating variant it will returns
   * list of unused sub-variant names
   *
   * @param isNotCompareOrMultiEdit boolean val
   * @param validAttrValues valid attribute values
   * @return List<AttributeValue> defines the selected pidc attribute values
   */
  public Set<AttributeValue> getValidAttrValueSet(final boolean isNotCompareOrMultiEdit,
      final AttributeValueWithDetails validAttrValues) {
    if (isNotCompareOrMultiEdit) {
      if ((this.attribute.getLevel() == ApicConstants.VARIANT_CODE_ATTR) ||
          (this.attribute.getLevel() == ApicConstants.SUB_VARIANT_CODE_ATTR)) {
        return new AttributeClientBO(this.pidcDataHandler.getAttributeMap().get(this.attribute.getId()))
            .getAttrValues();
      }
      return getUnusedPidcNames();
    }
    if (this.apicObject instanceof IProjectAttribute) { // ICDM-1411
      // if boolean , make the includeDummyFlag argument as false
      IProjectAttribute pidcAttr = (IProjectAttribute) this.apicObject;
      Attribute attr = this.pidcDataHandler.getAttributeMap().get(pidcAttr.getAttrId());
      if (attr.getValueType().equals(AttributeValueType.BOOLEAN.getDisplayText())) {
        return validAttrValues.getAttrValset();
      }
    }
    addDummyValue(validAttrValues);
    return validAttrValues.getAttrValset();
  }


  /**
   * @param readOnlyMode boolean value
   * @return true if action can be enabled
   */
  public boolean canEnableAddNewValAction(final boolean readOnlyMode) {

    boolean enable = false;

    NodeAccess curUserAccRight;
    try {
      curUserAccRight = new CurrentUserBO().getNodeAccessRight(this.pidcVersionBO.getPidcVersion().getPidcId());

      // the attribute can be modified if the user can modify the PIDC
      if ((curUserAccRight != null) && curUserAccRight.isWrite()) {
        // structure attributes can not be modified
        boolean isModifiable = !((this.pidcDataHandler.getAttributeMap()
            .get(((IProjectAttribute) this.apicObject).getAttrId()).getLevel() > 0) || this.pidcVersionBO.isDeleted());

        enable = isModifiable && !readOnlyMode;
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }

    return enable;

  }

  /**
   * @param updationModel model to save project attributes
   */
  public void saveAttributes(final ProjectAttributesUpdationModel updationModel) {
    ProjectAttributesUpdationServiceClient upClient = new ProjectAttributesUpdationServiceClient();
    try {
      upClient.updatePidcAttrs(updationModel);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * @return Long value
   */
  public Long fetchPrevAttrValId() {
    if (this.apicObject instanceof PidcVersionAttribute) {
      return ((PidcVersionAttribute) this.apicObject).getValueId();
    }
    if (this.apicObject instanceof PidcVariantAttribute) {
      return ((PidcVariantAttribute) this.apicObject).getValueId();

    }
    if (this.apicObject instanceof PidcSubVariantAttribute) {
      return ((PidcSubVariantAttribute) this.apicObject).getValueId();
    }
    return null;
  }


  /**
   * When set value action is performed on multiple variants and if existing grouped attribute value has more number of
   * predefined attributes than newly set grouped attribute value then reset irrelevant predefined attribute
   *
   * @param irrelevantPredefAttr irrelevant predefined attribute value set
   * @param var variant
   * @param updateModel ProjectAttributesUpdationModel
   */
  public void resetDiffPredefAttrForVar(final Set<PredefinedAttrValue> irrelevantPredefAttr, final PidcVariant var,
      final ProjectAttributesUpdationModel updateModel) {
    for (PredefinedAttrValue predefAttrVal : irrelevantPredefAttr) {
      PidcVariantAttribute varAttr =
          this.pidcDataHandler.getVariantAttributeMap().get(var.getId()).get(predefAttrVal.getPredefinedAttrId());
      if (varAttr != null) {
        varAttr.setUsedFlag(ApicConstants.NOT_DEFINED);
        varAttr.setValue(null);
        varAttr.setValueId(null);

        Map<Long, PidcVariantAttribute> varAttrMap;
        if (updateModel.getPidcVarAttrsToBeUpdated().get(varAttr.getVariantId()) == null) {
          varAttrMap = new HashMap<>();
          updateModel.getPidcVarAttrsToBeUpdated().put(varAttr.getVariantId(), varAttrMap);
        }
        else {
          varAttrMap = updateModel.getPidcVarAttrsToBeUpdated().get(varAttr.getVariantId());
        }
        varAttrMap.put(varAttr.getAttrId(), varAttr);
      }

    }

  }

  /**
   * When set value action is performed on multiple subvariants and if existing grouped attribute value has more number
   * of predefined attributes than newly set grouped attribute value then reset irrelevant predefined attribute
   *
   * @param difference difference in predefined attribute value set
   * @param subVar pidc subvariant
   * @param updateModel ProjectAttributesUpdationModel
   */
  public void resetDiffPredefAttrForSubVar(final Set<PredefinedAttrValue> difference, final PidcSubVariant subVar,
      final ProjectAttributesUpdationModel updateModel) {
    for (PredefinedAttrValue predefAttrVal : difference) {
      PidcSubVariantAttribute subVarAttr =
          this.pidcDataHandler.getSubVariantAttributeMap().get(subVar.getId()).get(predefAttrVal.getPredefinedAttrId());
      if (subVarAttr != null) {
        subVarAttr.setUsedFlag(ApicConstants.NOT_DEFINED);
        subVarAttr.setValue(null);
        subVarAttr.setValueId(null);

        Map<Long, PidcSubVariantAttribute> subVarAttrMap;
        if (updateModel.getPidcSubVarAttrsToBeUpdated().get(subVarAttr.getVariantId()) == null) {
          subVarAttrMap = new HashMap<>();
          updateModel.getPidcSubVarAttrsToBeUpdated().put(subVarAttr.getVariantId(), subVarAttrMap);
        }
        else {
          subVarAttrMap = updateModel.getPidcSubVarAttrsToBeUpdated().get(subVarAttr.getVariantId());
        }
        subVarAttrMap.put(subVarAttr.getAttrId(), subVarAttr);
      }

    }

  }

  /**
   * @param pidcAttrMap pidc attribute map
   */
  public void fillAttrMap(final Map<Long, IProjectAttribute> pidcAttrMap) {
    if (this.apicObject instanceof PidcVersionAttribute) {
      pidcAttrMap.putAll(this.pidcVersionBO.getAttributesAll());
    }
    else if (this.apicObject instanceof PidcVariantAttribute) {
      pidcAttrMap.putAll(this.pidcVersionBO.getAttributesAll());
      pidcAttrMap.putAll(
          this.pidcDataHandler.getVariantAttributeMap().get(((PidcVariantAttribute) this.apicObject).getVariantId()));
    }
    else if (this.apicObject instanceof PidcSubVariantAttribute) {
      pidcAttrMap.putAll(this.pidcVersionBO.getAttributesAll());
      PidcSubVariantAttribute subVarAttr = (PidcSubVariantAttribute) this.apicObject;

      pidcAttrMap.putAll(this.pidcDataHandler.getVariantAttributeMap().get(subVarAttr.getVariantId()));

      pidcAttrMap.putAll(this.pidcDataHandler.getSubVariantAttributeMap()
          .get(((PidcSubVariantAttribute) this.apicObject).getSubVariantId()));
    }
  }


  /**
   * @return the list of unused PIDC name attribute values
   */
  public final synchronized Set<AttributeValue> getUnusedPidcNames() {
    final Set<AttributeValue> retList = new HashSet<>();

    ApicDataBO apicDataBO = new ApicDataBO();
    Attribute attr = apicDataBO.getAllLvlAttrByLevel().get(Long.valueOf(ApicConstants.PROJECT_NAME_ATTR));

    Set<AttributeValue> attrVals = new AttributeClientBO(attr).getAttrValues();
    retList.addAll(attrVals);

    return retList;
  }

  /**
   * @param currPredefinedAttrValueMap predefined attribute val map
   * @param grpdAttr grouped attribute
   * @param attributeValue AttributeValue
   * @return true if there is difference between previous predef attr value and current predef attribute value
   */
  public boolean diffWithCurrPredAttr(final Map<Long, Map<Long, PredefinedAttrValue>> currPredefinedAttrValueMap,
      final IProjectAttribute grpdAttr, final AttributeValue attributeValue) {

    Map<Long, PredefinedAttrValue> currPreDefinedAttrValues = currPredefinedAttrValueMap.get(attributeValue.getId());
    Map<Long, PredefinedAttrValue> prevPredefAttrVal =
        this.pidcDataHandler.getPreDefAttrValMap().get(grpdAttr.getValueId());
    // prev
    return (!CommonUtils.getDifference(prevPredefAttrVal, currPreDefinedAttrValues).isEmpty());
  }


  /**
   * @param updationModel ProjectAttributesUpdationModel
   * @param prevGrpAttrValueId previous grouped attriute value id
   * @param grpAttr grouped attribute
   */
  public void resetExistingPredAttrVal(final ProjectAttributesUpdationModel updationModel,
      final Long prevGrpAttrValueId, final IModel grpAttr) {
    for (Entry<Long, PredefinedAttrValue> predefAttrValEntry : this.pidcDataHandler.getPreDefAttrValMap()
        .get(prevGrpAttrValueId).entrySet()) {
      if (!this.pidcDataHandler.getPidcVersInvisibleAttrSet()
          .contains(predefAttrValEntry.getValue().getPredefinedAttrId())) {
        if (grpAttr instanceof PidcVersionAttribute) {
          updatePidcAtrr(updationModel, predefAttrValEntry);
        }
        if (grpAttr instanceof PidcVariantAttribute) {
          setValueForVarLevelPredefAttr(grpAttr, updationModel, predefAttrValEntry);
        }
        if (grpAttr instanceof PidcSubVariantAttribute) {
          setValueForSubVarLevelPredefAttr(grpAttr, updationModel, predefAttrValEntry);
        }
      }
    }

  }

  /**
   * @param updateModel ProjectAttributesUpdationModel
   * @param predefAttrValEntry predefined attribute value map entry
   */
  public void updatePidcAtrr(final ProjectAttributesUpdationModel updateModel,
      final Entry<Long, PredefinedAttrValue> predefAttrValEntry) {
    PidcVersionAttribute versAttr =
        this.pidcDataHandler.getPidcVersAttrMap().get(predefAttrValEntry.getValue().getPredefinedAttrId());
    versAttr.setUsedFlag(ApicConstants.NOT_DEFINED);
    versAttr.setValue(null);
    versAttr.setValueId(null);
    updateModel.getPidcAttrsToBeUpdated().put(versAttr.getAttrId(), versAttr);
  }

  /**
   * @param grpAttr grouped attribute
   * @param updateModel ProjectAttributesUpdationModel
   * @param predefAttrValEntry predefined attribute value map entry
   */
  public void setValueForVarLevelPredefAttr(final IModel grpAttr, final ProjectAttributesUpdationModel updateModel,
      final Entry<Long, PredefinedAttrValue> predefAttrValEntry) {

    PidcVariantAttribute varAttr = this.pidcDataHandler.getVariantAttributeMap()
        .get(((PidcVariantAttribute) grpAttr).getVariantId()).get(predefAttrValEntry.getValue().getPredefinedAttrId());
    if (varAttr != null) {
      varAttr.setUsedFlag(ApicConstants.NOT_DEFINED);
      varAttr.setValue(null);
      varAttr.setValueId(null);

      Map<Long, PidcVariantAttribute> varAttrMap;
      if (updateModel.getPidcVarAttrsToBeUpdated().get(varAttr.getVariantId()) == null) {
        varAttrMap = new HashMap<>();
        updateModel.getPidcVarAttrsToBeUpdated().put(varAttr.getVariantId(), varAttrMap);
      }
      else {
        varAttrMap = updateModel.getPidcVarAttrsToBeUpdated().get(varAttr.getVariantId());
      }
      varAttrMap.put(varAttr.getAttrId(), varAttr);
    }

  }

  /**
   * @param grpAttr grouped attribute
   * @param updateModel ProjectAttributesUpdationModel
   * @param predefAttrValEntry predefined attribute value map entry
   */
  public void setValueForSubVarLevelPredefAttr(final IModel grpAttr, final ProjectAttributesUpdationModel updateModel,
      final Entry<Long, PredefinedAttrValue> predefAttrValEntry) {

    PidcSubVariantAttribute subvarAttr =
        this.pidcDataHandler.getSubVariantAttributeMap().get(((PidcSubVariantAttribute) grpAttr).getSubVariantId())
            .get(predefAttrValEntry.getValue().getPredefinedAttrId());

    if (subvarAttr != null) {
      subvarAttr.setUsedFlag(ApicConstants.NOT_DEFINED);
      subvarAttr.setValue(null);
      subvarAttr.setValueId(null);

      Map<Long, PidcSubVariantAttribute> subvarAttrMap;
      if (updateModel.getPidcSubVarAttrsToBeUpdated().get(subvarAttr.getSubVariantId()) == null) {
        subvarAttrMap = new HashMap<>();
        updateModel.getPidcSubVarAttrsToBeUpdated().put(subvarAttr.getSubVariantId(), subvarAttrMap);
      }
      else {
        subvarAttrMap = updateModel.getPidcSubVarAttrsToBeUpdated().get(subvarAttr.getSubVariantId());
      }
      subvarAttrMap.put(subvarAttr.getAttrId(), subvarAttr);
    }
  }


  /**
   * @return the attribute
   */
  public Attribute getAttribute() {
    if (this.attribute == null) {
      this.attribute = getAttributeFromApicObject();
    }
    return this.attribute;
  }


}
