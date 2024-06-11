/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.icdm.model.apic.AliasDefinitionModel;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.Characteristic;
import com.bosch.caltool.icdm.model.apic.attr.CharacteristicValue;
import com.bosch.caltool.icdm.model.apic.attr.MandatoryAttr;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedValidity;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDetStructure;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionInfo;
import com.bosch.caltool.icdm.model.uc.ProjectUsecaseModel;

/**
 * Model class to hold all common data for pidc editor
 *
 * @author pdh2cob
 */
public class PidcDataHandler {

  /**
   * PIDC Version level project attributes <br>
   * Key - Attribute ID; Value - project attribute
   */
  private Map<Long, PidcVersionAttribute> pidcVersAttrMap = new HashMap<>();

  private Map<Long, PidcVersionAttribute> pidcVersAttrMapDefined = new HashMap<>();

  private PidcVersionInfo pidcVersionInfo;

  /**
   * Map of pidc details structure Key - PidcDetStructure id, Value - PidcDetStructure object
   */
  private Map<Long, PidcDetStructure> pidcDetStructureMap = new HashMap<>();

  /**
   * Set of grouped pidc version attributes
   */
  protected Set<PidcVersionAttribute> groupedPidcVersionAttrSet;

  /**
   * Key - PIDC ID Value - Map of Node ID, Node Object
   */
  private ConcurrentMap<String, PIDCDetailsNode> pidDetNodeMap = new ConcurrentHashMap<>();

  /**
   * key - attribute value id, value - map of mandatory attributes(key - mandattr id, value- mand attr)
   */
  Map<Long, Map<Long, MandatoryAttr>> mandatoryAttrMap = new ConcurrentHashMap<>();

  /**
   * Key - attribute id, value - attribute object
   */
  private Map<Long, Attribute> attributeMap = new HashMap<>();

  /**
   * key - attribute group id, value - attribute group object
   */
  private Map<Long, AttrGroup> attributeGroupMap = new HashMap<>();

  /**
   * key - attribute super group id, value - attribute super group object
   */
  private Map<Long, AttrSuperGroup> attributeSuperGroupMap = new HashMap<>();


  /**
   * key - characteristicId, value - Characteristic object
   */
  private Map<Long, Characteristic> characteristicMap = new HashMap<>();

  /**
   * key - characteristic value id, value - characteristic value object
   */
  Map<Long, CharacteristicValue> characteristicValueMap = new HashMap<>();

  /**
   * key - attribute value id, value - attribute value object
   */
  private Map<Long, AttributeValue> attributeValueMap = new HashMap<>();


  /**
   * Key- PredefinedAttributeValue id, value - PredefinedAttrValue
   */
  protected Map<Long, Map<Long, PredefinedAttrValue>> preDefAttrValMap;

  /**
   * all nodeIds having iCDM links
   */
  private Set<Long> links = new HashSet<>();

  /**
   * All attribute values id having icdm links
   */
  private Set<Long> attValLinks = new HashSet<>();

  /**
   * Map with predefined validity
   */
  private Map<Long, PredefinedValidity> predefinedValidityMap = new HashMap<>();

  /**
   * key - var id, value - PidcVariant
   */

  protected Map<Long, PidcVariant> variantMap = new HashMap<>();


  /**
   * key - sub var id, value - PidcSubVariant
   */
  protected Map<Long, PidcSubVariant> subVariantMap = new HashMap<>();


  /**
   * Key - variant id, value - PidcVariantAttribute map - (key- attribute id, value - PidcVariantAttribute)
   */
  protected Map<Long, Map<Long, PidcVariantAttribute>> variantAttributeMap = new HashMap<>();


  /**
   * Key - subvariant id, value - PidcSubVariantAttribute map (key-attribute id, value - PidcSubVariantAttribute)
   */
  protected Map<Long, Map<Long, PidcSubVariantAttribute>> subVariantAttributeMap = new HashMap<>();

  /**
   * Set of Invisible attributes at PIDC Version Level.<br>
   * Note : Attribute IDs are stored here
   */
  private Set<Long> pidcVersInvisibleAttrSet = new HashSet<>();

  /**
   * Set of Invisible attributes for each variant at variant level.<br>
   * Key - Variant ID<br>
   * Value - Set of 'attribute' ID
   */
  private Map<Long, Set<Long>> variantInvisbleAttributeMap = new HashMap<>();
  /**
   * Key - level, Value - level attribute in project
   */
  private Map<Long, PidcVersionAttribute> allLevelAttrMap = new HashMap<>();


  /**
   * Set of Invisible attributes for each sub variant at subvariant level.<br>
   * Key - Sub Variant ID<br>
   * Value - Set of 'attribute' ID
   */
  private Map<Long, Set<Long>> subVariantInvisbleAttributeMap = new HashMap<>();


  /**
   * Map with attributes and its dependencies set Key - attr id, value - dependencies set
   */
  private Map<Long, Set<AttrNValueDependency>> attrDependenciesMap = new HashMap<>();

  /**
   * Map with attributes and its dependencies set Key - attr id, value - dependencies set
   */
  private Map<Long, Set<AttrNValueDependency>> attrRefDependenciesMap = new HashMap<>();
  /**
   * Map with attributes and its dependencies set Key - attr id, value -map of dependencies set mapped attribute value
   * id
   */
  private Map<Long, Map<Long, Set<AttrNValueDependency>>> attrDependenciesMapForAllAttr = new HashMap<>();
  /**
   * Map with dependent attribute value id and its AttributeValue object
   */
  private Map<Long, AttributeValue> attributeDepValueMap = new HashMap<>();


  /**
   * AliasDefinitionModel
   */
  private AliasDefinitionModel aliasDefModel;

  private ProjectUsecaseModel projectUsecaseModel;

  /**
   * Flag to indicate project status attribute value for filtering quotation relevant attributes
   */
  private boolean quotAttrWarningReq;

  private boolean mandAttrWarningReq;

  private boolean mandAndUcAttrWarningReq;


  /**
   * @return the quotAttrWarningReq
   */
  public boolean isQuotAttrWarningReq() {
    return this.quotAttrWarningReq;
  }


  /**
   * @param quotAttrWarningReq the quotAttrWarningReq to set
   */
  public void setQuotAttrWarningReq(final boolean quotAttrWarningReq) {
    this.quotAttrWarningReq = quotAttrWarningReq;
  }


  /**
   * @return the mandAttrWarningReq
   */
  public boolean isMandAttrWarningReq() {
    return this.mandAttrWarningReq;
  }


  /**
   * @param mandAttrWarningReq the mandAttrWarningReq to set
   */
  public void setMandAttrWarningReq(final boolean mandAttrWarningReq) {
    this.mandAttrWarningReq = mandAttrWarningReq;
  }


  /**
   * @return the mandAndUcAttrWarningReq
   */
  public boolean isMandAndUcAttrWarningReq() {
    return this.mandAndUcAttrWarningReq;
  }


  /**
   * @param mandAndUcAttrWarningReq the mandAndUcAttrWarningReq to set
   */
  public void setMandAndUcAttrWarningReq(final boolean mandAndUcAttrWarningReq) {
    this.mandAndUcAttrWarningReq = mandAndUcAttrWarningReq;
  }


  /**
   * @return the projectUsecaseModel
   */
  public ProjectUsecaseModel getProjectUsecaseModel() {
    return this.projectUsecaseModel;
  }


  /**
   * @param projectUsecaseModel the projectUsecaseModel to set
   */
  public void setProjectUsecaseModel(final ProjectUsecaseModel projectUsecaseModel) {
    this.projectUsecaseModel = projectUsecaseModel;
  }


  /**
   * @return the attrRefDependenciesMap
   */
  public Map<Long, Set<AttrNValueDependency>> getAttrRefDependenciesMap() {
    return this.attrRefDependenciesMap;
  }


  /**
   * @param attrRefDependenciesMap the attrRefDependenciesMap to set
   */
  public void setAttrRefDependenciesMap(final Map<Long, Set<AttrNValueDependency>> attrRefDependenciesMap) {
    this.attrRefDependenciesMap = attrRefDependenciesMap;
  }


  /**
   * Get the virtual nodes for the given PIDC
   *
   * @return map of virtual nodes, with key=node id, value=node object
   */
  public ConcurrentMap<String, PIDCDetailsNode> getPidcDetNodes() {
    return this.pidDetNodeMap;
  }


  /**
   * @return the pidcVersInvisibleAttrSet
   */
  public Set<Long> getPidcVersInvisibleAttrSet() {
    return this.pidcVersInvisibleAttrSet;
  }


  /**
   * @param pidcVersInvisibleAttrSet the pidcVersInvisibleAttrSet to set
   */
  public void setPidcVersInvisibleAttrSet(final Set<Long> pidcVersInvisibleAttrSet) {
    this.pidcVersInvisibleAttrSet = pidcVersInvisibleAttrSet;
  }

  /**
   * @return the attributeDepValueMap
   */
  public Map<Long, AttributeValue> getAttributeDepValueMap() {
    return this.attributeDepValueMap;
  }

  /**
   * @param attributeDepValueMap the attributeDepValueMap to set
   */
  public void setAttributeDepValueMap(final Map<Long, AttributeValue> attributeDepValueMap) {
    this.attributeDepValueMap = attributeDepValueMap;
  }

  /**
   * @return the variantInvisbleAttributeMap
   */
  public Map<Long, Set<Long>> getVariantInvisbleAttributeMap() {
    return this.variantInvisbleAttributeMap;
  }


  /**
   * @param variantInvisbleAttributeMap the variantInvisbleAttributeMap to set
   */
  public void setVariantInvisbleAttributeMap(final Map<Long, Set<Long>> variantInvisbleAttributeMap) {
    this.variantInvisbleAttributeMap = variantInvisbleAttributeMap;
  }


  /**
   * @return the subVariantInvisbleAttributeMap
   */
  public Map<Long, Set<Long>> getSubVariantInvisbleAttributeMap() {
    return this.subVariantInvisbleAttributeMap;
  }


  /**
   * @param subVariantInvisbleAttributeMap the subVariantInvisbleAttributeMap to set
   */
  public void setSubVariantInvisbleAttributeMap(final Map<Long, Set<Long>> subVariantInvisbleAttributeMap) {
    this.subVariantInvisbleAttributeMap = subVariantInvisbleAttributeMap;
  }


  /**
   * @return the variantAttributeMap
   */
  public Map<Long, Map<Long, PidcVariantAttribute>> getVariantAttributeMap() {
    return this.variantAttributeMap;
  }


  /**
   * @param variantAttributeMap the variantAttributeMap to set
   */
  public void setVariantAttributeMap(final Map<Long, Map<Long, PidcVariantAttribute>> variantAttributeMap) {
    this.variantAttributeMap = variantAttributeMap;
  }


  /**
   * @return the subVariantAttributeMap
   */
  public Map<Long, Map<Long, PidcSubVariantAttribute>> getSubVariantAttributeMap() {
    return this.subVariantAttributeMap;
  }


  /**
   * @param subVariantAttributeMap the subVariantAttributeMap to set
   */
  public void setSubVariantAttributeMap(final Map<Long, Map<Long, PidcSubVariantAttribute>> subVariantAttributeMap) {
    this.subVariantAttributeMap = subVariantAttributeMap;
  }


  /**
   * @return the preDefAttrValMap
   */
  public Map<Long, Map<Long, PredefinedAttrValue>> getPreDefAttrValMap() {
    return this.preDefAttrValMap;
  }


  /**
   * @param preDefAttrValMap the preDefAttrValMap to set
   */
  public void setPreDefAttrValMap(final Map<Long, Map<Long, PredefinedAttrValue>> preDefAttrValMap) {
    this.preDefAttrValMap = preDefAttrValMap;
  }

  /**
   * @return the pidcVersAttrMap
   */
  public Map<Long, PidcVersionAttribute> getPidcVersAttrMap() {
    return this.pidcVersAttrMap;
  }


  /**
   * @param pidcVersAttrMap the pidcVersAttrMap to set
   */
  public void setPidcVersAttrMap(final Map<Long, PidcVersionAttribute> pidcVersAttrMap) {
    this.pidcVersAttrMap = pidcVersAttrMap;
  }

  /**
   * @return the attributeMap
   */
  public Map<Long, Attribute> getAttributeMap() {
    return this.attributeMap;
  }


  /**
   * @param attributeMap the attributeMap to set
   */
  public void setAttributeMap(final Map<Long, Attribute> attributeMap) {
    this.attributeMap = attributeMap;
  }


  /**
   * @return the attributeValueMap
   */
  public Map<Long, AttributeValue> getAttributeValueMap() {
    return this.attributeValueMap;
  }


  /**
   * @param attributeValueMap the attributeValueMap to set
   */
  public void setAttributeValueMap(final Map<Long, AttributeValue> attributeValueMap) {
    this.attributeValueMap = attributeValueMap;
  }

  /**
   * @return the attributeGroupMap
   */
  public Map<Long, AttrGroup> getAttributeGroupMap() {
    return this.attributeGroupMap;
  }


  /**
   * @param attributeGroupMap the attributeGroupMap to set
   */
  public void setAttributeGroupMap(final Map<Long, AttrGroup> attributeGroupMap) {
    this.attributeGroupMap = attributeGroupMap;
  }


  /**
   * @return the attributeSuperGroupMap
   */
  public Map<Long, AttrSuperGroup> getAttributeSuperGroupMap() {
    return this.attributeSuperGroupMap;
  }


  /**
   * @param attributeSuperGroupMap the attributeSuperGroupMap to set
   */
  public void setAttributeSuperGroupMap(final Map<Long, AttrSuperGroup> attributeSuperGroupMap) {
    this.attributeSuperGroupMap = attributeSuperGroupMap;
  }


  /**
   * @return the pidcDetStructureMap
   */
  public Map<Long, PidcDetStructure> getPidcDetStructureMap() {
    return this.pidcDetStructureMap;
  }


  /**
   * @param pidcDetStructureMap the pidcDetStructureMap to set
   */
  public void setPidcDetStructureMap(final Map<Long, PidcDetStructure> pidcDetStructureMap) {
    this.pidcDetStructureMap = pidcDetStructureMap;
  }


  /**
   * @return the pidDetNodeMap
   */
  public ConcurrentMap<String, PIDCDetailsNode> getPidDetNodeMap() {
    return this.pidDetNodeMap;
  }


  /**
   * @param pidDetNodeMap the pidDetNodeMap to set
   */
  public void setPidDetNodeMap(final ConcurrentMap<String, PIDCDetailsNode> pidDetNodeMap) {
    this.pidDetNodeMap = pidDetNodeMap;
  }


  /**
   * @return the links
   */
  public Set<Long> getLinks() {
    return this.links;
  }


  /**
   * @param links the links to set
   */
  public void setLinks(final Set<Long> links) {
    this.links = links;
  }


  /**
   * @return the predefinedValidityMap
   */
  public Map<Long, PredefinedValidity> getPredefinedValidityMap() {
    return this.predefinedValidityMap;
  }


  /**
   * @param predefinedValidityMap the predefinedValidityMap to set
   */
  public void setPredefinedValidityMap(final Map<Long, PredefinedValidity> predefinedValidityMap) {
    this.predefinedValidityMap = predefinedValidityMap;
  }


  /**
   * @return the variantMap
   */
  public Map<Long, PidcVariant> getVariantMap() {
    return this.variantMap;
  }


  /**
   * @param variantMap the variantMap to set
   */
  public void setVariantMap(final Map<Long, PidcVariant> variantMap) {
    this.variantMap = variantMap;
  }


  /**
   * @return the subVariantMap
   */
  public Map<Long, PidcSubVariant> getSubVariantMap() {
    return this.subVariantMap;
  }


  /**
   * @param subVariantMap the subVariantMap to set
   */
  public void setSubVariantMap(final Map<Long, PidcSubVariant> subVariantMap) {
    this.subVariantMap = subVariantMap;
  }


  /**
   * @return the mandatoryAttrMap
   */
  public Map<Long, Map<Long, MandatoryAttr>> getMandatoryAttrMap() {
    return this.mandatoryAttrMap;
  }


  /**
   * @param mandatoryAttrMap the mandatoryAttrMap to set
   */
  public void setMandatoryAttrMap(final Map<Long, Map<Long, MandatoryAttr>> mandatoryAttrMap) {
    this.mandatoryAttrMap = mandatoryAttrMap;
  }


  /**
   * @return the groupedPidcVersionAttrSet
   */
  public Set<PidcVersionAttribute> getGroupedPidcVersionAttrSet() {
    return this.groupedPidcVersionAttrSet;
  }


  /**
   * @param groupedPidcVersionAttrSet the groupedPidcVersionAttrSet to set
   */
  public void setGroupedPidcVersionAttrSet(final Set<PidcVersionAttribute> groupedPidcVersionAttrSet) {
    this.groupedPidcVersionAttrSet = groupedPidcVersionAttrSet;
  }


  /**
   * @return the characteristicValueMap
   */
  public Map<Long, CharacteristicValue> getCharacteristicValueMap() {
    return this.characteristicValueMap;
  }


  /**
   * @param characteristicValueMap the characteristicValueMap to set
   */
  public void setCharacteristicValueMap(final Map<Long, CharacteristicValue> characteristicValueMap) {
    this.characteristicValueMap = characteristicValueMap;
  }


  /**
   * @return the characteristicMap
   */
  public Map<Long, Characteristic> getCharacteristicMap() {
    return this.characteristicMap;
  }


  /**
   * @param characteristicMap the characteristicMap to set
   */
  public void setCharacteristicMap(final Map<Long, Characteristic> characteristicMap) {
    this.characteristicMap = characteristicMap;
  }


  /**
   * @return the attrDependenciesMap
   */
  public Map<Long, Set<AttrNValueDependency>> getAttrDependenciesMap() {
    return this.attrDependenciesMap;
  }


  /**
   * @param attrDependenciesMap the attrDependenciesMap to set
   */
  public void setAttrDependenciesMap(final Map<Long, Set<AttrNValueDependency>> attrDependenciesMap) {
    this.attrDependenciesMap = attrDependenciesMap;
  }


  /**
   * @return the attrRefDependenciesMapForAllAttr
   */
  public Map<Long, Map<Long, Set<AttrNValueDependency>>> getAttrDependenciesMapForAllAttr() {
    return this.attrDependenciesMapForAllAttr;
  }

  /**
   * @param attrDependenciesMapForAllAttr the attrDependenciesMap to set
   */
  public void setAttrDependenciesMapForAllAttr(
      final Map<Long, Map<Long, Set<AttrNValueDependency>>> attrDependenciesMapForAllAttr) {
    this.attrDependenciesMapForAllAttr = attrDependenciesMapForAllAttr;
  }

  /**
   * @param deleteNeeded
   * @return
   */
  public Map<Long, PidcVariant> getVariantsMap(final boolean deleteNeeded) {
    Map<Long, PidcVariant> pidcVariantsMapIterate = getVariantMap();
    final Map<Long, PidcVariant> pidcVariantsMap = new ConcurrentHashMap<Long, PidcVariant>();
    for (PidcVariant pidcVariant : pidcVariantsMapIterate.values()) {
      checkIfDeletedNeeded(pidcVariantsMap, pidcVariant);
      if (deleteNeeded && pidcVariant.isDeleted()) {
        pidcVariantsMap.put(pidcVariant.getId(), pidcVariant);
      }
    }
    return pidcVariantsMap;
  }

  /**
   * @param pidcVariantsMap
   * @param pidcVariant
   */
  private void checkIfDeletedNeeded(final Map<Long, PidcVariant> pidcVariantsMap, final PidcVariant pidcVariant) {
    // Check whether the variant is not deleted, else deleted variants will get added irrespective of the 'show deleetd'
    // flag
    if (!pidcVariant.isDeleted()) {
      pidcVariantsMap.put(pidcVariant.getId(), pidcVariant);
    }
  }


  /**
   * @return the pidcVersionInfo
   */
  public PidcVersionInfo getPidcVersionInfo() {
    return this.pidcVersionInfo;
  }


  /**
   * @param pidcVersionInfo the pidcVersionInfo to set
   */
  public void setPidcVersionInfo(final PidcVersionInfo pidcVersionInfo) {
    this.pidcVersionInfo = pidcVersionInfo;
  }


  /**
   * @return the pidcVersAttrMapDefined
   */
  public Map<Long, PidcVersionAttribute> getPidcVersAttrMapDefined() {
    return this.pidcVersAttrMapDefined;
  }


  /**
   * @param pidcVersAttrMapDefined the pidcVersAttrMapDefined to set
   */
  public void setPidcVersAttrMapDefined(final Map<Long, PidcVersionAttribute> pidcVersAttrMapDefined) {
    this.pidcVersAttrMapDefined = pidcVersAttrMapDefined;
  }


  /**
   * @return the allLevelAttrMap
   */
  public Map<Long, PidcVersionAttribute> getAllLevelAttrMap() {
    return this.allLevelAttrMap;
  }


  /**
   * @param allLevelAttrMap the allLevelAttrMap to set
   */
  public void setAllLevelAttrMap(final Map<Long, PidcVersionAttribute> allLevelAttrMap) {
    this.allLevelAttrMap = allLevelAttrMap;
  }

  /**
   * @param attrId attribute id
   * @return attribute
   */
  public Attribute getAttribute(final Long attrId) {
    return getAttributeMap().get(attrId);
  }


  /**
   * @param valueId value Id
   * @return Attribute Value
   */
  public AttributeValue getAttributeValue(final Long valueId) {
    return getAttributeValueMap().get(valueId);
  }

  /**
   * @param superGroupId Attribute Super Group ID
   * @return Attribute Super Group
   */
  public AttrSuperGroup getAttrSuperGroup(final Long superGroupId) {
    return getAttributeSuperGroupMap().get(superGroupId);
  }

  /**
   * @param groupId Attribute Group ID
   * @return Attribute Group
   */
  public AttrGroup getAttrGroup(final Long groupId) {
    return getAttributeGroupMap().get(groupId);
  }


  /**
   * @param chrValId characteristic value ID
   * @return characteristic value
   */
  public CharacteristicValue getCharacteristicValue(final Long chrValId) {
    return getCharacteristicValueMap().get(chrValId);
  }


  /**
   * @return the attValLinks
   */
  public Set<Long> getAttValLinks() {
    return this.attValLinks;
  }


  /**
   * @param attValLinks the attValLinks to set
   */
  public void setAttValLinks(final Set<Long> attValLinks) {
    this.attValLinks = attValLinks;
  }


  /**
   * @return the aliasDefModel
   */
  public AliasDefinitionModel getAliasDefModel() {
    return this.aliasDefModel;
  }


  /**
   * @param aliasDefModel the aliasDefModel to set
   */
  public void setAliasDefModel(final AliasDefinitionModel aliasDefModel) {
    this.aliasDefModel = aliasDefModel;
  }


}
