/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author nip4cob
 */
public class AttrValcombinationCalculator {

  private final Queue<Attribute> paramAttrQueue = new LinkedList<>();

  /**
   * Stores paramAttr and its selected values
   */
  private Map<Attribute, SortedSet<AttributeValue>> selectedAttrAttrValues = new HashMap<>();

  /**
   * Map with key as one combination of all attribute values and value as a map of Attribute,AttributeValue
   */
  private final ConcurrentMap<Integer, Map<Attribute, AttributeValue>> attrAttrValCombinationMap =
      new ConcurrentHashMap<>();

  private int key = 1;

  /**
   * Constructor which accepts the selectedAttrAttrValues from UI
   *
   * @param attrVals
   */
  public AttrValcombinationCalculator(final Map<Attribute, SortedSet<AttributeValue>> selectedAttrAttrValues) {
    super();
    this.selectedAttrAttrValues = selectedAttrAttrValues;
    this.paramAttrQueue.addAll(selectedAttrAttrValues.keySet());
  }


  /**
   * @return
   */
  public Map<Integer, Map<Attribute, AttributeValue>> constructAttrValCombi() {
    constructAllCom(this.paramAttrQueue.poll(), null);
    return this.attrAttrValCombinationMap;
  }

  /**
   * @param paramAttrQueue
   */
  private void constructAllCom(final Attribute paramAttr, final AttributeValue... attrValPasd) {
    Attribute poppedParam = this.paramAttrQueue.poll();
    AttributeServiceClient client = new AttributeServiceClient();
    if (paramAttr != null) {

      for (AttributeValue attrVal : this.selectedAttrAttrValues.get(paramAttr)) {
        if (null == poppedParam) {
          // base case since there is no call to constructAllCon after this line
          HashMap<Attribute, AttributeValue> attrValMap = new HashMap<>();
          if (attrValPasd == null) {
            // When only one Attribute is present as dependency
            // construct map with attribute and its value
            try {
              attrValMap.put(client.get(attrVal.getAttributeId()), attrVal);
            }
            catch (ApicWebServiceException e) {
              CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
            }
            this.attrAttrValCombinationMap.put(this.key, attrValMap);
            this.key++;
          }
          else {
            if (attrValPasd.length != 0) {
              // Attribute values to be passed
              AttributeValue[] attributeValuesToBePassed = Arrays.copyOf(attrValPasd, attrValPasd.length + 1);
              attributeValuesToBePassed[attributeValuesToBePassed.length - 1] = attrVal;
              for (AttributeValue attributeVal : attributeValuesToBePassed) {
                try {
                  attrValMap.put(client.get(attributeVal.getAttributeId()), attributeVal);
                }
                catch (ApicWebServiceException e) {

                  CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
                }
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

}
