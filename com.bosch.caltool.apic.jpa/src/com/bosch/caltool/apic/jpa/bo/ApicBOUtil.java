/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.FeatureValueModel;


/**
 * Utility class for APIC BO
 *
 * @author adn1cob
 */
public final class ApicBOUtil {

  /**
   * To remove semicolon as the end of attr-val string
   */
  private static final int SEMICOLON_SIZE = 4;

  /**
   * Returns the equivalent display string for the boolean value
   *
   * @param value value
   * @return value as display text (Yes/No)
   */
  public static String getDisplayString(final boolean value) {
    return value ? ApicConstants.USED_YES_DISPLAY : ApicConstants.USED_NO_DISPLAY;
  }

  /**
   * Create attr-val pairs for given rule
   *
   * @param rule rule
   * @param dataProvider ApicDataProvider
   * @return attr-val pairs
   */
  public static String createRulesAttrVal(final CDRRule rule, final ApicDataProvider dataProvider) {
    String result = "";

    List<FeatureValueModel> features = rule.getDependencyList();
    SortedSet<AttributeValueModel> attrValSet = new TreeSet<AttributeValueModel>();
    Set<FeatureValueModel> featureSet = new HashSet<FeatureValueModel>(features);
    FeatureAttributeAdapter adapter = new FeatureAttributeAdapter(dataProvider);
    try {
      attrValSet.addAll(adapter.createAttrValModel(featureSet).values());
    }
    catch (IcdmException exception) {
      ObjectStore.getInstance().getLogger().error(exception.getLocalizedMessage(), exception);
      return result;
    }
    result = getAttrValString(attrValSet);
    // iCDM-713
    rule.setDependenciesForDisplay(result);
    return result;
  }


  /**
   * @param attrValSet
   * @return
   */
  public static String getAttrValString(final SortedSet<AttributeValueModel> attrValSet) {
    String result = "";
    StringBuilder depen = new StringBuilder();
    for (AttributeValueModel attrVal : attrValSet) {
      // iCDM-1317
      depen.append(attrVal.getAttribute().getName()).append("  --> ").append(attrVal.getAttrValue().getName())
          .append("  ;  ");
    }
    if (!CommonUtils.isEmptyString(depen.toString())) {
      result = depen.substring(0, depen.length() - SEMICOLON_SIZE).trim();
    }
    return result;
  }

  /**
   * @param result
   * @param attrValSet
   * @param depen
   * @return
   * @deprecated use the method in common.bo.ApicBOUtil
   */
  @Deprecated
  public static String getAttrValueString(
      final SortedSet<com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel> attrValSet) {
    String result = "";
    StringBuilder depen = new StringBuilder();
    for (com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel attrVal : attrValSet) {
      // iCDM-1317
      depen.append(attrVal.getAttr().getName()).append("  --> ").append(attrVal.getValue().getName()).append("  ;  ");
    }
    if (!CommonUtils.isEmptyString(depen.toString())) {
      result = depen.substring(0, depen.length() - SEMICOLON_SIZE).trim();
    }
    return result;
  }


  public static SortedSet<AttributeValueModel> getAttrValModel(final CDRRule rule,
      final ApicDataProvider dataProvider) {
    List<FeatureValueModel> features = rule.getDependencyList();
    SortedSet<AttributeValueModel> attrValSet = new TreeSet<AttributeValueModel>();
    Set<FeatureValueModel> featureSet = new HashSet<FeatureValueModel>(features);
    FeatureAttributeAdapter adapter = new FeatureAttributeAdapter(dataProvider);
    try {
      attrValSet.addAll(adapter.createAttrValModel(featureSet).values());
    }
    catch (IcdmException exception) {
      ObjectStore.getInstance().getLogger().error(exception.getLocalizedMessage(), exception);
    }
    return attrValSet;
  }

  public static String createAttrVal(final List<FeatureValueModel> features, final ApicDataProvider dataProvider) {
    String result = "";
    SortedSet<AttributeValueModel> attrValSet = new TreeSet<AttributeValueModel>();
    Set<FeatureValueModel> featureSet = new HashSet<FeatureValueModel>(features);
    FeatureAttributeAdapter adapter = new FeatureAttributeAdapter(dataProvider);
    try {
      attrValSet.addAll(adapter.createAttrValModel(featureSet).values());
    }
    catch (IcdmException exception) {
      ObjectStore.getInstance().getLogger().error(exception.getLocalizedMessage(), exception);
      return result;
    }
    result = getAttrValString(attrValSet);
    return result;
  }

  /**
   * Get the attribute values of the given parameter, having feature value mappings
   *
   * @param dataProvider ApicDataProvider
   * @param attr Attribute
   * @return Sorted set of attribute values
   */
  public static SortedSet<AttributeValue> getFeatureMappedAttrValues(final ApicDataProvider dataProvider,
      final Attribute attr) {
    // Get the Feature mapped to the attrcannot be null since only mapped Attrs are added as dep
    Feature feature = dataProvider.getDataCache().getAttrFeaMap().get(attr.getID());
    // Get the Feature Values associated to the Feature with Attr Val id as key
    Map<Long, FeatureValue> featureValues = feature.getFeatureValAttrMap();
    SortedSet<AttributeValue> retValSet = new TreeSet<>();
    // Check if the Feature Value is not null
    if (CommonUtils.isNotNull(featureValues)) {
      // Iterate through all Attr Values of the atte
      for (AttributeValue attrVal : attr.getAttrValues(false)) {
        // If the Feature Val has apic Val id same as Attr Val id add it
        if (CommonUtils.isNotNull(featureValues.get(attrVal.getValueID()))) {
          retValSet.add(attrVal);
        }
      }
      // iCDM-1316,1317
      // Add AttrVal models of used and not used
      if (CommonUtils.isNotNull(featureValues.get(ApicConstants.ATTR_VAL_USED_VALUE_ID))) {
        retValSet.add(new AttributeValueUsed(dataProvider, attr));
      }
      if (CommonUtils.isNotNull(featureValues.get(ApicConstants.ATTR_VAL_NOT_USED_VALUE_ID))) {
        retValSet.add(new AttributeValueNotUsed(dataProvider, attr));
      }
    }
    return retValSet;
  }
}
