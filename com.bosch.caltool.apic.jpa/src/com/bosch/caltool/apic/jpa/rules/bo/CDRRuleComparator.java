package com.bosch.caltool.apic.jpa.rules.bo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.apic.jpa.bo.Attribute;
import com.bosch.caltool.apic.jpa.bo.AttributeValue;
import com.bosch.caltool.apic.jpa.bo.AttributeValueModel;
import com.bosch.caltool.apic.jpa.bo.FeatureAttributeAdapter;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.FeatureValueModel;

/**
 * A Comparator class for sorting CDRRules The above comparison is used when displaying CDRRules in ParametersRulePage
 * in RuleEditor
 *
 * @author jvi6cob
 */
class CDRRuleComparator implements Comparator<CDRRule> {

  private final FeatureAttributeAdapter fAdapter;

  /**
   * Constructor
   *
   * @param fAdapter FeatureAttributeAdapter
   */
  public CDRRuleComparator(final FeatureAttributeAdapter fAdapter) {
    this.fAdapter = fAdapter;
  }


  @Override
  public int compare(final CDRRule cdrRule1, final CDRRule cdrRule2) {

    Map<Attribute, AttributeValue> attrAttrValMap1 = createAttrAttrValMap(cdrRule1);

    Map<Attribute, AttributeValue> attrAttrValMap2 = createAttrAttrValMap(cdrRule2);

    // Need to fill the Missing attribute keys with null for sorting to work correctly
    // The above condition occurs when there are rules which has no attribute mapping which occurs when an attribute
    // dependency is added when there are rules existing already

    // If size of both CDR rule maps are not equal
    label: if (attrAttrValMap1.size() != attrAttrValMap2.size()) {
      if (attrAttrValMap1.size() > attrAttrValMap2.size()) {
        for (Attribute attr : attrAttrValMap1.keySet()) {
          if (attrAttrValMap2.get(attr) == null) {
            attrAttrValMap2.put(attr, null);
            break label;
          }
        }
      }
      // If size of CDR rule2 mapis bigger
      if (attrAttrValMap2.size() > attrAttrValMap1.size()) {
        for (Attribute attr : attrAttrValMap2.keySet()) {
          if (attrAttrValMap1.get(attr) == null) {
            attrAttrValMap1.put(attr, null);
            break label;
          }
        }
      }
    }

    // Iterate over the attribute, value map
    for (Entry<Attribute, AttributeValue> map1Entry : attrAttrValMap1.entrySet()) {
      // Get the attribute values
      AttributeValue attrVal1 = map1Entry.getValue();
      AttributeValue attrVal2 = attrAttrValMap2.get(map1Entry.getKey());
      String name1 = attrVal1 == null ? "" : CommonUtils.checkNull(attrVal1.getValue());
      String name2 = attrVal2 == null ? "" : CommonUtils.checkNull(attrVal2.getValue());

      // Compare the results
      int compareResult = name1.compareTo(name2);

      // If names are same
      if (compareResult == 0) {
        continue;
      }
      return compareResult;
    }

    // Should be unreachable since two CDRRules can never have the same attribute value combinations
    return 0;
  }

  /**
   * @param cdrRule
   * @param feaAdapter FeatureAttributeAdapter
   * @return Map<Attribute, AttributeValue>
   */
  private Map<Attribute, AttributeValue> createAttrAttrValMap(final CDRRule cdrRule) {
    List<FeatureValueModel> fvModelList = cdrRule.getDependencyList();
    Map<FeatureValueModel, AttributeValueModel> mapOfAttrFeature;
    Map<Attribute, AttributeValue> attrAttrValMap = new TreeMap<>();
    // Check for the dependency of rule
    if ((fvModelList != null) && !fvModelList.isEmpty()) {
      try {
        // create a map of feature, attribute value
        mapOfAttrFeature = this.fAdapter.createAttrValModel(new HashSet<FeatureValueModel>(fvModelList));
        List<AttributeValueModel> attValModList1 = new ArrayList<>(mapOfAttrFeature.values());
        for (AttributeValueModel attributeValueModel : attValModList1) {
          attrAttrValMap.put(attributeValueModel.getAttribute(), attributeValueModel.getAttrValue());
        }
      }
      catch (IcdmException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }
    return attrAttrValMap;
  }
}