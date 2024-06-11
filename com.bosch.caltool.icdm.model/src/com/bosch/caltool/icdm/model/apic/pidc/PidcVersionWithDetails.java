/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import com.bosch.caltool.icdm.model.uc.ProjectUsecaseModel;

/**
 * @author pdh2cob
 */
/**
 * @author pdh2cob
 */
public class PidcVersionWithDetails {


  PidcVersionInfo pidcVersionInfo;

  private AliasDefinitionModel aliasDefnModel;

  /**
   * Key - variant id, value - variant
   */
  Map<Long, PidcVariant> pidcVariantMap = new HashMap<>();

  /**
   * Key - sub variant id, value - sub variant
   */
  Map<Long, PidcSubVariant> pidcSubVariantMap = new HashMap<>();

  /**
   * Key - attribute id, value - PidcVersionAttribute
   */
  Map<Long, PidcVersionAttribute> pidcVersionAttributeMap = new HashMap<>();

  /**
   * Key - attribute id, value - PidcVersionAttribute
   */
  Map<Long, PidcVersionAttribute> pidcVersionAttributeDefinedMap = new HashMap<>();

  /**
   * Key - variant id, value - PidcVariantAttribute map - (key- attribute id, value - PidcVariantAttribute)
   */
  Map<Long, Map<Long, PidcVariantAttribute>> pidcVariantAttributeMap = new HashMap<>();

  /**
   * Key - subvariant id, value - PidcSubVariantAttribute map (key-attribute id, value - PidcSubVariantAttribute)
   */
  Map<Long, Map<Long, PidcSubVariantAttribute>> pidcSubVariantAttributeMap = new HashMap<>();

  /**
   * key - attribute id, value - attribute object
   */
  Map<Long, Attribute> allAttributeMap = new HashMap<>();

  /**
   * key - attribute value id, value - attribute value object
   */
  Map<Long, AttributeValue> attributeValueMap = new HashMap<>();

  /**
   * key - attribute group id, value - attribute group object
   */
  Map<Long, AttrGroup> allAttributeGroupMap = new HashMap<>();

  /**
   * key - pidc details structure id, value - pidc details structure object
   */
  Map<Long, PidcDetStructure> pidcDetailsStructmap = new HashMap<>();

  /**
   * Let of link ids
   */
  Set<Long> linkSet = new HashSet<>();

  /**
   * All attribute values id having icdm links
   */
  Set<Long> attValLinks = new HashSet<>();

  /**
   * key - predefinedvalidity id, predefined validity object
   */
  Map<Long, PredefinedValidity> predefinedValidityMap = new HashMap<>();

  /**
   * key - super group id, value - attribute super group
   */
  Map<Long, AttrSuperGroup> allAttributeSuperGroup = new HashMap<>();

  /**
   * key - characteristic value id, characterisitcValue object
   */
  Map<Long, CharacteristicValue> allCharactersiticValueMap = new HashMap<>();

  /**
   * Key - characteristic id, value - characteristic object
   */
  Map<Long, Characteristic> allCharacteristicMap = new HashMap<>();

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
   * Set of Invisible attributes for each sub variant at subvariant level.<br>
   * Key - Sub Variant ID<br>
   * Value - Set of 'attribute' ID
   */
  private Map<Long, Set<Long>> subVariantInvisbleAttributeMap = new HashMap<>();

  /**
   * Map witn depedent attribute value id and its attributeValue Object
   */
  private Map<Long, AttributeValue> attrDepValMap = new HashMap<>();

  /**
   * Map with attributes and its dependencies set Key - attr id, value - dependencies set
   */
  private Map<Long, Set<AttrNValueDependency>> attrDependenciesMap = new HashMap<>();
  /**
   * Map with key -attribute id and its dependencies set mapped to attribute value Key - attribute value id, value -
   * dependencies set
   */
  private Map<Long, Map<Long, Set<AttrNValueDependency>>> attrDependenciesMapForAllAttr = new HashMap<>();
  /**
   * Map with attributes and its dependencies set Key - attr id, value - dependencies set
   */
  private Map<Long, Set<AttrNValueDependency>> attrRefDependenciesMap = new HashMap<>();


  /**
   * key - attribute value id, value - map of mandatory attributes(key - mandattr id, value- mand attr)
   */
  private Map<Long, Map<Long, MandatoryAttr>> mandatoryAttrMap = new HashMap<>();
  /**
   * Key - level, Value - level attribute in project
   */
  private Map<Long, PidcVersionAttribute> allLevelAttrMap = new HashMap<>();


  /**
   * Key- PredefinedAttributeValue id, value - PredefinedAttrValue
   */
  protected Map<Long, Map<Long, PredefinedAttrValue>> preDefAttrValMap;


  private ProjectUsecaseModel projectUsecaseModel = new ProjectUsecaseModel();


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
   * @return the attrDepValMap
   */
  public Map<Long, AttributeValue> getAttrDepValMap() {
    return this.attrDepValMap;
  }

  /**
   * @param attrDepValMap the attrDepValMap to set
   */
  public void setAttrDepValMap(final Map<Long, AttributeValue> attrDepValMap) {
    this.attrDepValMap = attrDepValMap;
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
   * @return the allCharactersiticValueMap
   */
  public Map<Long, CharacteristicValue> getAllCharactersiticValueMap() {
    return this.allCharactersiticValueMap;
  }


  /**
   * @param allCharactersiticValueMap the allCharactersiticValueMap to set
   */
  public void setAllCharactersiticValueMap(final Map<Long, CharacteristicValue> allCharactersiticValueMap) {
    this.allCharactersiticValueMap = allCharactersiticValueMap;
  }


  /**
   * @return the allAttributeSuperGroup
   */
  public Map<Long, AttrSuperGroup> getAllAttributeSuperGroup() {
    return this.allAttributeSuperGroup;
  }


  /**
   * @param allAttributeSuperGroup the allAttributeSuperGroup to set
   */
  public void setAllAttributeSuperGroup(final Map<Long, AttrSuperGroup> allAttributeSuperGroup) {
    this.allAttributeSuperGroup = allAttributeSuperGroup;
  }


  /**
   * @return the pidcVariantMap
   */
  public Map<Long, PidcVariant> getPidcVariantMap() {
    return this.pidcVariantMap;
  }


  /**
   * @param pidcVariantMap the pidcVariantMap to set
   */
  public void setPidcVariantMap(final Map<Long, PidcVariant> pidcVariantMap) {
    this.pidcVariantMap = pidcVariantMap;
  }


  /**
   * @return the pidcSubVariantMap
   */
  public Map<Long, PidcSubVariant> getPidcSubVariantMap() {
    return this.pidcSubVariantMap;
  }


  /**
   * @param pidcSubVariantMap the pidcSubVariantMap to set
   */
  public void setPidcSubVariantMap(final Map<Long, PidcSubVariant> pidcSubVariantMap) {
    this.pidcSubVariantMap = pidcSubVariantMap;
  }


  /**
   * @return the pidcVersionAttributeMap
   */
  public Map<Long, PidcVersionAttribute> getPidcVersionAttributeMap() {
    return this.pidcVersionAttributeMap;
  }


  /**
   * @param pidcVersionAttributeMap the pidcVersionAttributeMap to set
   */
  public void setPidcVersionAttributeMap(final Map<Long, PidcVersionAttribute> pidcVersionAttributeMap) {
    this.pidcVersionAttributeMap = pidcVersionAttributeMap;
  }


  /**
   * @return the pidcVariantAttributeMap
   */
  public Map<Long, Map<Long, PidcVariantAttribute>> getPidcVariantAttributeMap() {
    return this.pidcVariantAttributeMap;
  }


  /**
   * @param pidcVariantAttributeMap the pidcVariantAttributeMap to set
   */
  public void setPidcVariantAttributeMap(final Map<Long, Map<Long, PidcVariantAttribute>> pidcVariantAttributeMap) {
    this.pidcVariantAttributeMap = pidcVariantAttributeMap;
  }


  /**
   * @return the pidcSubVariantAttributeMap
   */
  public Map<Long, Map<Long, PidcSubVariantAttribute>> getPidcSubVariantAttributeMap() {
    return this.pidcSubVariantAttributeMap;
  }


  /**
   * @param pidcSubVariantAttributeMap the pidcSubVariantAttributeMap to set
   */
  public void setPidcSubVariantAttributeMap(
      final Map<Long, Map<Long, PidcSubVariantAttribute>> pidcSubVariantAttributeMap) {
    this.pidcSubVariantAttributeMap = pidcSubVariantAttributeMap;
  }


  /**
   * @return the allAttributeMap
   */
  public Map<Long, Attribute> getAllAttributeMap() {
    return this.allAttributeMap;
  }


  /**
   * @param allAttributeMap the allAttributeMap to set
   */
  public void setAllAttributeMap(final Map<Long, Attribute> allAttributeMap) {
    this.allAttributeMap = allAttributeMap;
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
   * @return the allAttributeGroupMap
   */
  public Map<Long, AttrGroup> getAllAttributeGroupMap() {
    return this.allAttributeGroupMap;
  }


  /**
   * @param allAttributeGroupMap the allAttributeGroupMap to set
   */
  public void setAllAttributeGroupMap(final Map<Long, AttrGroup> allAttributeGroupMap) {
    this.allAttributeGroupMap = allAttributeGroupMap;
  }


  /**
   * @return the pidcDetailsStructmap
   */
  public Map<Long, PidcDetStructure> getPidcDetailsStructmap() {
    return this.pidcDetailsStructmap;
  }


  /**
   * @param pidcDetailsStructmap the pidcDetailsStructmap to set
   */
  public void setPidcDetailsStructmap(final Map<Long, PidcDetStructure> pidcDetailsStructmap) {
    this.pidcDetailsStructmap = pidcDetailsStructmap;
  }


  /**
   * @return the linkSet
   */
  public Set<Long> getLinkSet() {
    return this.linkSet;
  }


  /**
   * @param linkSet the linkSet to set
   */
  public void setLinkSet(final Set<Long> linkSet) {
    this.linkSet = linkSet;
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
   * @return the allCharacteristicMap
   */
  public Map<Long, Characteristic> getAllCharacteristicMap() {
    return this.allCharacteristicMap;
  }


  /**
   * @param allCharacteristicMap the allCharacteristicMap to set
   */
  public void setAllCharacteristicMap(final Map<Long, Characteristic> allCharacteristicMap) {
    this.allCharacteristicMap = allCharacteristicMap;
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
   * @return the pidcVersionAttributeDefinedMap
   */
  public Map<Long, PidcVersionAttribute> getPidcVersionAttributeDefinedMap() {
    return this.pidcVersionAttributeDefinedMap;
  }


  /**
   * @param pidcVersionAttributeDefinedMap the pidcVersionAttributeDefinedMap to set
   */
  public void setPidcVersionAttributeDefinedMap(final Map<Long, PidcVersionAttribute> pidcVersionAttributeDefinedMap) {
    this.pidcVersionAttributeDefinedMap = pidcVersionAttributeDefinedMap;
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
   * @return the attrDependenciesMapForAllAttr
   */
  public Map<Long, Map<Long, Set<AttrNValueDependency>>> getAttrDependenciesMapForAllAttr() {
    return this.attrDependenciesMapForAllAttr;
  }

  /**
   * @param attrDependenciesMapForAllAttr the attrDependenciesMapForAllAttr to set
   */
  public void setAttrDependenciesMapForAllAttr(
      final Map<Long, Map<Long, Set<AttrNValueDependency>>> attrDependenciesMapForAllAttr) {
    this.attrDependenciesMapForAllAttr = attrDependenciesMapForAllAttr;
  }


  /**
   * @return the aliasDefnModel
   */
  public AliasDefinitionModel getAliasDefnModel() {
    return this.aliasDefnModel;
  }


  /**
   * @param aliasDefnModel the aliasDefnModel to set
   */
  public void setAliasDefnModel(final AliasDefinitionModel aliasDefnModel) {
    this.aliasDefnModel = aliasDefnModel;
  }


}
