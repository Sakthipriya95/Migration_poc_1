/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.caldataimport;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;


/**
 * Utility class used to calculate all possible Attribute-->AttributeValueCombinations
 *
 * @author jvi6cob
 */
public class CombinationCalculator {

  private final Queue<Attribute> paramAttrQueue = new LinkedList<>();

  /**
   * Stores paramAttr and its selected values
   */
  private final Map<Attribute, SortedSet<AttributeValue>> selectedAttrAttrValues;

  /**
   * Map with key as one combination of all attribute values and value as a map of Attribute,AttributeValue
   */
  private final ConcurrentMap<Integer, Map<Long, AttributeValue>> attrAttrValCombinationMap = new ConcurrentHashMap<>();

  private int key = 1;

  /**
   * Constructor which accepts the selectedAttrAttrValues from UI
   *
   * @param attrVals
   */
  public CombinationCalculator(final Map<Attribute, SortedSet<AttributeValue>> selectedAttrAttrValues) {
    super();
    this.selectedAttrAttrValues = selectedAttrAttrValues;
    this.paramAttrQueue.addAll(selectedAttrAttrValues.keySet());
  }


  /**
   * @return
   */
  public Map<Integer, Map<Long, AttributeValue>> constructAttrValCombi() {
    constructAllCom(this.paramAttrQueue.poll(), null);
    return this.attrAttrValCombinationMap;
  }

  /**
   * @param paramAttrQueue
   */
  private void constructAllCom(final Attribute attribute, final AttributeValue... attrValPasd) {
    Attribute poppedParam = this.paramAttrQueue.poll();
    for (AttributeValue attrVal : this.selectedAttrAttrValues.get(attribute)) {
      if (null == poppedParam) {
        // base case since there is no call to constructAllCon after this line
        HashMap<Long, AttributeValue> attrValMap = new HashMap<>();
        if (attrValPasd == null) {
          // When only one Attribute is present as dependency
          // construct map with attribute and its value
          attrValMap.put(attrVal.getAttributeId(), attrVal);
          this.attrAttrValCombinationMap.put(this.key, attrValMap);
          this.key++;
        }
        else {
          if (attrValPasd.length != 0) {
            // Attribute values to be passed
            AttributeValue[] attributeValuesToBePassed = Arrays.copyOf(attrValPasd, attrValPasd.length + 1);
            attributeValuesToBePassed[attributeValuesToBePassed.length - 1] = attrVal;
            for (AttributeValue attributeVal : attributeValuesToBePassed) {
              attrValMap.put(attributeVal.getAttributeId(), attributeVal);
            }
            this.attrAttrValCombinationMap.put(this.key, attrValMap);
            this.key++;
          }

        }
      }
      else {
        if (null == attrValPasd) {
          // Construct attribute value pair with the parameter from queue
          constructAllCom(poppedParam, attrVal);
        }
        else {
          if (attrValPasd.length == 0) {
            // first recursive call
            // Construct attribute value pair with the parameter from queue
            constructAllCom(poppedParam, new AttributeValue[] { attrVal });
          }
          else {
            AttributeValue[] attributeValuesToBePassed = Arrays.copyOf(attrValPasd, attrValPasd.length + 1);
            attributeValuesToBePassed[attributeValuesToBePassed.length - 1] = attrVal;
            // Construct attribute value pair with the parameter from queue
            constructAllCom(poppedParam, attributeValuesToBePassed);
          }

        }

      }
    }
    if (poppedParam != null) {
      // Add parameter to the queue
      this.paramAttrQueue.add(poppedParam);
    }
  }

}
