/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.ssd.icdm.model.FeatureValueModel;


/**
 * New Class for Conversion of FV to AV model and reverse
 *
 * @author rgo7cob
 */
public class FeatureAttributeAdapter {


  private final ApicDataProvider apicDataProvider;

  /**
   * set the valu not set attr
   */
  private final SortedSet<AttributeValueModel> valueNotSetAttr = new TreeSet<>();

  /**
   * ICDM-2019 set the value not set attr
   */
  private final SortedSet<AttributeValueModel> attrMissingInFeatureMapping = new TreeSet<>();

  private boolean unUsedAttrAvail;

  /**
   * ICDM-2019 VALUE not DEFINED error message constant
   */
  public static final String VALUE_NOT_DEFINED = "VALUE NOT DEFINED => please define a value in the PIDC";

  /**
   * ICDM-2019 ATTRIBUTE not MAPPED error message constant
   */
  public static final String ATTRIBUTE_NOT_MAPPED = "ATTRIBUTE NOT MAPPED => please contact the iCDM Hotline";

  /**
   * ICDM-2019 VALUE not MAPPED error message constant
   */
  public static final String VALUE_NOT_MAPPED = "VALUE NOT MAPPED => please contact the iCDM Hotline";


  /**
   * @return the valueNotSetAttr
   */
  public SortedSet<AttributeValueModel> getValueNotSetAttr() {
    return this.valueNotSetAttr;
  }


  /**
   * the one and only constructor
   *
   * @param apicDataProvider data provider
   */
  public FeatureAttributeAdapter(final ApicDataProvider apicDataProvider) {
    this.apicDataProvider = apicDataProvider;
  }


  /**
   * Converts the attribute value model to feature value model
   *
   * @param attrValModSet set of AttrVal Model
   * @return the Feature Value Map, key - attribute value model; value - associated feature value model
   * @throws IcdmException IcdmException if Feature-Attribute or Value-Attribute value mapping is missing
   */
  public Map<AttributeValueModel, FeatureValueModel> createFeaValModel(final Set<AttributeValueModel> attrValModSet)
      throws IcdmException {
    this.attrMissingInFeatureMapping.clear();
    // Map for Attribute Val Model
    Map<AttributeValueModel, FeatureValueModel> feaValModelMap = new ConcurrentHashMap<>();
    for (AttributeValueModel attrValueModel : attrValModSet) {
      FeatureValueModel feaValModel;
      try {
        feaValModel = createFeaValModel(attrValueModel);
      }
      catch (IcdmException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
        continue;
      }
      feaValModelMap.put(attrValueModel, feaValModel);
    }
    if (!this.valueNotSetAttr.isEmpty() || !this.attrMissingInFeatureMapping.isEmpty()) {
      throw new IcdmException("Feature Attribute Mapping missing");
    }
    return feaValModelMap;
  }


  /**
   * Converts the attribute value model to feature value model which also includes values which are not set
   *
   * @param attrValModSet set of AttrVal Model
   * @return the Feature Value Map, key - attribute value model; value - associated feature value model
   * @throws IcdmException IcdmException if Feature-Attribute or Value-Attribute value mapping is missing
   */
  public Map<AttributeValueModel, FeatureValueModel> createAllFeaValModel(final Set<AttributeValueModel> attrValModSet)
      throws IcdmException {
    this.attrMissingInFeatureMapping.clear();
    // Map for Attribute Val Model
    Map<AttributeValueModel, FeatureValueModel> feaValModelMap = new ConcurrentHashMap<>();
    for (AttributeValueModel attrValueModel : attrValModSet) {
      FeatureValueModel feaValModel;
      try {
        feaValModel = createFeaValModel(attrValueModel);
      }
      catch (IcdmException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
        continue;
      }
      feaValModelMap.put(attrValueModel, feaValModel);
    }
    return feaValModelMap;
  }

  /**
   * @param attrValueModel AttributeValueModel
   * @return FeatureValueModel
   */
  private FeatureValueModel createFeaValModel(final AttributeValueModel attrValueModel) throws IcdmException {

    Feature feature =
        this.apicDataProvider.getFeaturesWithAttrKey().get(attrValueModel.getAttribute().getAttributeID());
    // Check for Feature attr mapping
    if (CommonUtils.isNull(feature)) {
      String errorMsg = ATTRIBUTE_NOT_MAPPED;
      boolean contains = this.valueNotSetAttr.contains(attrValueModel);
      if (contains) {
        for (AttributeValueModel attrValmodel : this.valueNotSetAttr) {
          if (attrValueModel.equals(attrValmodel)) {
            String existingErrorMsg = attrValmodel.getErrorMsg();
            attrValmodel.setErrorMsg(existingErrorMsg + ";" + errorMsg);
            break;
          }
        }
      }
      else {
        attrValueModel.setErrorMsg(errorMsg);
      }
      this.attrMissingInFeatureMapping.add(attrValueModel);
      this.valueNotSetAttr.add(attrValueModel);
      throw new IcdmException(CommonUtils.concatenate("The attribute '", attrValueModel.getAttribute().getName(),
          "' is not associated with a feature"));
    }
    // ICDM-2019
    // If attr val model not present throw exception
    if (this.valueNotSetAttr.contains(attrValueModel) && !this.unUsedAttrAvail) {
      throw new IcdmException(attrValueModel.getErrorMsg());
    }
    FeatureValue feaValue = null;
    if (this.unUsedAttrAvail) {
      this.valueNotSetAttr.remove(attrValueModel);
      feaValue = feature.getFeatureValAttrMap().get(ApicConstants.ATTR_VAL_NOT_SET_VALUE_ID);
      FeatureValueModel feaValModel = new FeatureValueModel();
      feaValModel.setFeatureId(BigDecimal.valueOf(feature.getFeatureID()));
      feaValModel.setFeatureText(feature.getFeatureText());
      feaValModel.setValueId(BigDecimal.valueOf(feaValue.getID()));
      feaValModel.setValueText(feaValue.getValueText());

      return feaValModel;
    }

    feaValue = feature.getFeatureValAttrMap().get(attrValueModel.getAttrValue().getID());


    // if feaValue null here means, value is not mapped, in this case check if used flag mapping is available and set
    // the value_id to indicate used flag
    if (CommonUtils.isNull(feaValue) && feature.isUsedFlagMapped()) {
      feaValue = feature.getUsedFeatureValue();
    }

    // If the attr's is used/not used ,such attrvalue is stored as an instance of AttributeValueUsed, NotUSed
    if (CommonUtils.isEqual(attrValueModel.getAttrValue().getID(), ApicConstants.ATTR_VAL_USED_VALUE_ID)) {
      feaValue = feature.getUsedFeatureValue();
    }
    else if (CommonUtils.isEqual(attrValueModel.getAttrValue().getID(), ApicConstants.ATTR_VAL_NOT_USED_VALUE_ID)) {
      feaValue = feature.getNotUsedFeatureValue();
    }
    // If NULL here, neither a rule for the selected valueid nor USED flag mapping rule nor default rule is available
    if (CommonUtils.isNull(feaValue)) {
      this.valueNotSetAttr.add(attrValueModel);
      String errorMsg = CommonUtils.concatenate("Value '", attrValueModel.getAttrValue().getValue(), "' of attribute '",
          attrValueModel.getAttribute().getName(), "' is not associated to a feature value or <USED> value!");
      attrValueModel.setErrorMsg(VALUE_NOT_MAPPED);
      throw new IcdmException(errorMsg, 1);
    }

    // Create the model object and set the details
    FeatureValueModel feaValModel = new FeatureValueModel();
    feaValModel.setFeatureId(BigDecimal.valueOf(feature.getFeatureID()));
    feaValModel.setFeatureText(feature.getFeatureText());
    feaValModel.setValueId(BigDecimal.valueOf(feaValue.getID()));
    feaValModel.setValueText(feaValue.getValueText());

    return feaValModel;
  }


  /**
   * Converts the feature value model to attribute value model
   *
   * @param featureValModSet featureValModSet a set of Feature Value Model classes
   * @return the Map<FeatureValueModel, AttributeValueModel> with Key as FeatureValueModel Obj
   * @throws IcdmException for Invalid Conditions
   */
  public Map<FeatureValueModel, AttributeValueModel> createAttrValModel(final Set<FeatureValueModel> featureValModSet)
      throws IcdmException {
    // Map for Attribute Val Model
    Map<FeatureValueModel, AttributeValueModel> attrValModelMap = new ConcurrentHashMap<>();
    for (FeatureValueModel featureValueModel : featureValModSet) {
      // Call the Method to get the Attr Val Model Obj
      AttributeValueModel attrValModel = createAttrValModel(featureValueModel);
      // Put the Value to the Map.
      attrValModelMap.put(featureValueModel, attrValModel);
    }
    return attrValModelMap;
  }

  /**
   * @param featureValueModel FeatureValueModel
   * @return AttributeValueModel
   */
  public AttributeValueModel createAttrValModel(final FeatureValueModel featureValueModel) throws IcdmException {

    Feature feature = this.apicDataProvider.getAllFeatures().get(featureValueModel.getFeatureId().longValue());
    if (CommonUtils.isNull(feature)) {
      throw new IcdmException(CommonUtils.concatenate("Invalid feature ID - '", featureValueModel.getFeatureId()));
    }
    // Check for Feature attr mapping
    if (CommonUtils.isNull(feature.getAttributeID())) {
      throw new IcdmException(
          CommonUtils.concatenate("Feature '", feature.getFeatureText(), "' is not associated to any attribute"));
    }

    Attribute selAttr = this.apicDataProvider.getAllAttributes().get(feature.getAttributeID());
    // Check for Valid Attr-feature mapping
    if (CommonUtils.isNull(selAttr)) {
      throw new IcdmException(
          CommonUtils.concatenate("Feature '", feature.getFeatureText(), "'  is mapped to an Invalid attribute"));
    }

    // Get the Feature Value from the Feature Value model
    FeatureValue featureValue = feature.getFeatureValues().get(featureValueModel.getValueId().longValue());

    // If the features whose used flag=N,Y ,such attrvalue is stored as an instance of AttributeValueNotUsed,
    // AttributeValueUsed
    if (CommonUtils.isNull(featureValue.getApicValueId()) && (!CommonUtils.isNull(featureValue.getUsedFlag()))) {
      if (featureValue.getUsedFlag().equals(ApicConstants.CODE_NO)) {
        return new AttributeValueModel(selAttr, new AttributeValueNotUsed(this.apicDataProvider, selAttr));
      }
      else if (featureValue.getUsedFlag().equals(ApicConstants.YES)) {
        return new AttributeValueModel(selAttr, new AttributeValueUsed(this.apicDataProvider, selAttr));
      }

      else if (featureValue.getUsedFlag().equals("?")) {
        return new AttributeValueModel(selAttr, null);
      }
      else {
        throw new IcdmException(
            CommonUtils.concatenate("Invalid feature value ID - '", featureValueModel.getValueId()));
      }
    }
    else if (CommonUtils.isNull(featureValue.getApicValueId())) { // if here, valueId is null and used flag also not
      // Special case for Don't care feature Values
      if (featureValue.getValueID() < 0) {
        return new AttributeValueModel(selAttr,
            new AttributeValueDontCare(this.apicDataProvider, selAttr, featureValue.getValueID()));
      }
      // defined in ssd
      // Check for Attr Value-feature value mapping
      throw new IcdmException(CommonUtils.concatenate("Value '", featureValue.getValueText(), "' of feature '",
          feature.getFeatureText(), "' is not associated to any attribute value"));
    }

    AttributeValue selattrVal = this.apicDataProvider.getAttrValue(featureValue.getApicValueId());
    // Check for Valid Attr Value-feature value mapping
    if (CommonUtils.isNull(selattrVal)) {
      throw new IcdmException(CommonUtils.concatenate("Value \"", featureValue.getValueText(), "\" of feature \"",
          feature.getFeatureText(), "\" is mapped to an invalid attribute value"));
    }

    // Create and return a new Attr Val model Obj
    return new AttributeValueModel(selAttr, selattrVal);
  }

  /**
   * create the Feature Value Model for the given attributes, from the Project ID Card
   *
   * @param amoSet set of IAttributeMappedObject
   * @param pidcVer PIDC version
   * @return the FeatureValueModel Map Key - Attribute Value model; value-associated feature value model
   * @throws IcdmException IcdmException if Feature-Attribute or Value-Attribute value mapping is missing
   */
  public Set<FeatureValueModel> createAllFeaValModel(final Set<IAttributeMappedObject> amoSet,
      final PIDCVersion pidcVer)
      throws IcdmException {
    // Create the Attr Val Model Set with Pidcard,Param attr. PIdc Variant not available
    Map<Long, AttributeValueModel> attrValModMap = createAttrValModSet(pidcVer, null, null, amoSet, null);
    // Create the Feature Val Model Set
    return new HashSet<FeatureValueModel>(
        createAllFeaValModel(new HashSet<AttributeValueModel>(attrValModMap.values())).values());
  }

  /**
   * create the Feature Value Model for the given attributes, from the Project Variant
   *
   * @param amoSet set of IAttributeMappedObject
   * @param pidcVariant pidcVariant
   * @param labelNames
   * @param paramsToRemove
   * @return the FeatureValueModel Map
   * @throws IcdmException IcdmException
   */
  public Set<FeatureValueModel> createAllFeaValModel(final Set<IAttributeMappedObject> amoSet,
      final PIDCVariant pidcVariant, final Set<String> labelNames, final List<IAttributeMappedObject> paramsToRemove)
      throws IcdmException {
    // Create the Attr Val Model Set with Pidcard,Param attr and PIdc Variant
    Map<Long, AttributeValueModel> attrValModMap =
        createAttrValModSet(pidcVariant.getPidcVersion(), pidcVariant, labelNames, amoSet, paramsToRemove);
    // Create the Feature Val Model Set
    return new HashSet<FeatureValueModel>(
        createAllFeaValModel(new HashSet<AttributeValueModel>(attrValModMap.values())).values());
  }


  /**
   * @param feaValModel feaValModel
   * @param pidcVariant pidcVariant
   * @param pidcVersion pidcVersion
   * @return
   * @throws IcdmException IcdmException
   */
  public AttributeValueModel getAttrValModel(final FeatureValueModel feaValModel, final PIDCVariant pidcVariant,
      final PIDCVersion pidcVersion)
      throws IcdmException {
    Feature feature = this.apicDataProvider.getAllFeatures().get(feaValModel.getFeatureId().longValue());
    if (CommonUtils.isNull(feature) || CommonUtils.isNull(feature.getAttributeID())) {
      return null;
    }

    if (pidcVariant == null) {
      return createAttrValModelForPidc(feaValModel, feature,
          pidcVersion.getAttributes(false).get(feature.getAttributeID()));

    }

    IPIDCAttribute pidcAttribute = pidcVariant.getAttributes(false).get(feature.getAttributeID());
    if (pidcAttribute == null) {
      pidcAttribute = pidcVersion.getAttributes(false).get(feature.getAttributeID());
    }
    return createAttrValModelForPidc(feaValModel, feature, pidcAttribute);


  }


  /**
   * @param feaValModel
   * @param feature
   * @param pidcAttribute
   * @return
   * @throws IcdmException
   */
  private AttributeValueModel createAttrValModelForPidc(final FeatureValueModel feaValModel, final Feature feature,
      final IPIDCAttribute pidcAttribute)
      throws IcdmException {
    if (CommonUtils.isNull(pidcAttribute)) {
      return null;
    }
    Attribute selAttr = pidcAttribute.getAttribute();

    FeatureValue featureValue = feature.getFeatureValues().get(feaValModel.getValueId().longValue());

    if (CommonUtils.isNull(featureValue.getApicValueId()) && (!CommonUtils.isNull(featureValue.getUsedFlag()))) {
      if (featureValue.getUsedFlag().equals(ApicConstants.CODE_NO)) {
        return new AttributeValueModel(selAttr, new AttributeValueNotUsed(this.apicDataProvider, selAttr));
      }
      else if (featureValue.getUsedFlag().equals(ApicConstants.YES)) {
        return new AttributeValueModel(selAttr, new AttributeValueUsed(this.apicDataProvider, selAttr));
      }
      else {
        throw new IcdmException(CommonUtils.concatenate("Invalid feature value ID - '", feaValModel.getValueId()));
      }
    }
    else if (CommonUtils.isNull(featureValue.getApicValueId())) { // if here, valueId is null and used flag also not
      // defined in ssd
      // Check for Attr Value-feature value mapping
      return null;
    }

    AttributeValue selattrVal = this.apicDataProvider.getAttrValue(featureValue.getApicValueId());

    if (CommonUtils.isNull(selattrVal)) {
      throw new IcdmException(CommonUtils.concatenate("Value \"", featureValue.getValueText(), "\" of feature \"",
          feature.getFeatureText(), "\" is mapped to an invalid attribute value"));
    }

    AttributeValue attributeValue = pidcAttribute.getAttributeValue();
    if (attributeValue == null) {
      return null;
    }
    if (selattrVal.equals(attributeValue)) {
      return new AttributeValueModel(pidcAttribute.getAttribute(), attributeValue);
    }
    else {
      throw new IcdmException(CommonUtils.concatenate("Value \"", featureValue.getValueText(), "\" of feature \"",
          feature.getFeatureText(), "\" value is mismatch"));
    }


  }


  /**
   * @param attrValModSet
   * @param selPidcVer
   * @param labelNames
   * @param amoSet
   * @param paramsToRemove
   * @param cdrFuncParameter
   * @return the Attr Val Model Map. key - Attribute ID, value - attrvaluemodel
   */
  private Map<Long, AttributeValueModel> createAttrValModSet(final PIDCVersion selPidcVer, final PIDCVariant pidVar,
      final Set<String> labelNames, final Set<IAttributeMappedObject> amoSet,
      final List<IAttributeMappedObject> paramsToRemove)
      throws IcdmException {

    // Refresh the attributes map of pidc and variant once and then disable the refresh during the iterations
    selPidcVer.getAttributes();
    if (CommonUtils.isNotNull(pidVar)) {
      pidVar.getAttributes();
    }

    Map<Long, AttributeValueModel> attrValModMap = new ConcurrentHashMap<>();
    for (IAttributeMappedObject amo : amoSet) {
      for (Attribute attr : amo.getAttributes()) {
        createAttrVal(selPidcVer, pidVar, labelNames, paramsToRemove, attrValModMap, amo, attr);
      }
    }
    if (CommonUtils.isNotEmpty(this.valueNotSetAttr) && !this.unUsedAttrAvail) {
      String errorMsg = CommonUtils.concatenate("Value is not set for the attributes '",
          "' in the selected project. Review cannot be done.");
      throw new IcdmException(errorMsg);
      // ICDM-2019
      // Cannot throw exception here as Feature/value to attribute/value mapping wont be checked if exception is thrown
      // for (AttributeValueModel attrValueModel : this.valueNotSetAttr) {
      // attrValModMap.put(attrValueModel.getAttribute().getID(), attrValueModel);
      // }
    }
    return attrValModMap;
  }


  /**
   * @param selPidcVer
   * @param pidVar
   * @param labelNames
   * @param paramsToRemove
   * @param attrValModMap
   * @param amo
   * @param attr
   * @throws IcdmException
   */
  private void createAttrVal(final PIDCVersion selPidcVer, final PIDCVariant pidVar, final Set<String> labelNames,
      final List<IAttributeMappedObject> paramsToRemove, final Map<Long, AttributeValueModel> attrValModMap,
      final IAttributeMappedObject amo, final Attribute attr)
      throws IcdmException {
    AttributeValueModel attrValModel;
    // Create the Attr Model If the Variant is selected
    if (CommonUtils.isNotNull(pidVar)) {
      attrValModel = createModelForVarAttr(attr, pidVar, amo, labelNames);
    }
    // Create the Attr Model If No Variant is present
    else {
      attrValModel = createModelForPIDCAttr(attr, selPidcVer);
    }


    if (null == attrValModel) {
      if (null != paramsToRemove) {
        paramsToRemove.add(amo);
      }
    }
    else {
      attrValModMap.put(attr.getID(), attrValModel);
    }
  }

  /**
   * @param paramAttr
   * @param pidcAttr
   * @return the AttributeValueModel from the PIDC attr
   */
  private AttributeValueModel createModelForPIDCAttr(final Attribute attr, final PIDCVersion pidcVer)
      throws IcdmException {
    PIDCAttribute pidcAttr = pidcVer.getAttributes(false).get(attr.getAttributeID());

    assertAttrNotNull(pidcAttr, attr);

    if (pidcAttr == null) {
      AttributeValueModel atttrUnused = getAtttrUnused(attr);
      if (atttrUnused != null) {
        return atttrUnused;
      }

      String errorMsg = CommonUtils.concatenate("Value is not set for the attribute '", attr.getName(),
          "' in the selected project. Review cannot be done.");
      throw new IcdmException(errorMsg);
    }
    assertValueNotNull(pidcAttr.getAttributeValue(), attr, pidcAttr.getIsUsed());
    // If the features whose used flag=N,Y ,such attrvalue is stored as an instance of
    // AttributeValueUsed, AttributeValueNotUsed
    if (CommonUtils.isNull(pidcAttr.getAttributeValue())) {
      if (ApicConstants.USED_NO_DISPLAY.equals(pidcAttr.getIsUsed())) {
        return new AttributeValueModel(attr, new AttributeValueNotUsed(this.apicDataProvider, attr));
      }
      else if (ApicConstants.PROJ_ATTR_USED_FLAG.YES.getUiType().equals(pidcAttr.getIsUsed())) {
        return new AttributeValueModel(attr, new AttributeValueUsed(this.apicDataProvider, attr));
      }
    }
    return new AttributeValueModel(attr, pidcAttr.getAttributeValue());
  }

  /**
   * @param amo
   * @param amoSet
   * @param labelNames
   * @param paramAttr
   * @param pidcAttr
   * @return the AttributeValueModel from the PIDC attr
   * @throws IcdmException
   */
  private AttributeValueModel createModelForVarAttr(final Attribute attr, final PIDCVariant pidVariant,
      final IAttributeMappedObject amo, final Set<String> labelNames)
      throws IcdmException {

    // If the attribute is varaint coded attribute
    if (attr.getAttrLevel() == ApicConstants.VARIANT_CODE_ATTR) {
      return new AttributeValueModel(attr, pidVariant.getNameValue());
    }
    // first get the project attributes
    // Refresh of attributes disabled, since refresh is done in createAttrValModSet() before iterations
    IPIDCAttribute projAttr = pidVariant.getPidcVersion().getAttributes(false).get(attr.getAttributeID());


    assertAttrNotNull(projAttr, attr);

    if (projAttr == null) {
      AttributeValueModel atttrUnused = getAtttrUnused(attr);
      if (atttrUnused != null) {
        return atttrUnused;
      }

      String errorMsg = CommonUtils.concatenate("Value is not set for the attribute '", attr.getName(),
          "' in the selected project. Review cannot be done.");
      throw new IcdmException(errorMsg);

    }
    // Check if variant
    if (projAttr.isVariant()) {
      // Refresh of attributes disabled, since refresh is done in createAttrValModSet() before iterations
      projAttr = pidVariant.getAttributes(false, false).get(attr.getAttributeID());
      assertAttrNotNull(projAttr, attr);
      // check if sub-variant
      if (projAttr.isVariant()) {
        labelNames.remove(amo.getName());
        return null;
      }
    }
    // check attr value is null
    assertValueNotNull(projAttr.getAttributeValue(), attr, projAttr.getIsUsed());

    if (CommonUtils.isNull(projAttr.getAttributeValue())) {
      if (ApicConstants.USED_NO_DISPLAY.equals(projAttr.getIsUsed())) {
        return new AttributeValueModel(attr, new AttributeValueNotUsed(this.apicDataProvider, attr));
      }
      else if (ApicConstants.PROJ_ATTR_USED_FLAG.YES.getUiType().equals(projAttr.getIsUsed())) {
        return new AttributeValueModel(attr, new AttributeValueUsed(this.apicDataProvider, attr));
      }
    }

    return new AttributeValueModel(attr, projAttr.getAttributeValue());
  }


  /**
   * @param attr
   */
  private AttributeValueModel getAtttrUnused(final Attribute attr) {
    Feature feature = this.apicDataProvider.getFeaturesWithAttrKey().get(attr.getAttributeID());
    if (feature != null) {
      Collection<FeatureValue> values = feature.getFeatureValAttrMap().values();

      for (FeatureValue featureValue : values) {

        if ((featureValue.getUsedFlag() != null) && featureValue.getUsedFlag().equals("?")) {
          this.unUsedAttrAvail = true;
          return new AttributeValueModel(attr, null);
        }

      }

    }

    return null;
  }


  /**
   * Assert value not null
   *
   * @param value attribute value
   * @param attr attribute
   * @param usedFlag
   * @throws IcdmException if value is null
   */
  private void assertValueNotNull(final AttributeValue value, final Attribute attr, final String usedFlag)
      throws IcdmException {
    if (CommonUtils.isNull(value) && (usedFlag.equals(ApicConstants.PROJ_ATTR_USED_FLAG.NEW_ATTR.getUiType()) ||
        usedFlag.equals(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType()))) {

      AttributeValueModel attrValModel = new AttributeValueModel(attr, null);
      String errorMsg = CommonUtils.concatenate("Value is not set for the attribute '", attr.getName(),
          "' in the selected project. Review cannot be done.");
      attrValModel.setErrorMsg(VALUE_NOT_DEFINED);
      this.valueNotSetAttr.add(attrValModel);
      // throw new IcdmException(errorMsg);
    }
  }

  /**
   * Assert projAttr not null
   *
   * @param projAttr IPIDCAttribute
   * @param attr attribute
   * @throws IcdmException if value is null
   */
  private void assertAttrNotNull(final IPIDCAttribute projAttr, final Attribute attr) throws IcdmException {
    if (CommonUtils.isNull(projAttr)) {
      AttributeValueModel attrValModel = new AttributeValueModel(attr, null);
      String errorMsg = CommonUtils.concatenate("Value is not set for the attribute '", attr.getName(),
          "' in the selected project. Review cannot be done.");
      attrValModel.setErrorMsg(VALUE_NOT_DEFINED);
      this.valueNotSetAttr.add(attrValModel);
      // throw new IcdmException(errorMsg);
    }
  }
}
