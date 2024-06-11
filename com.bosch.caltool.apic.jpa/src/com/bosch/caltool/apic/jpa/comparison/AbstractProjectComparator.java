/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.comparison;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.apic.jpa.bo.ApicObject;
import com.bosch.caltool.apic.jpa.bo.AttributeValue;
import com.bosch.caltool.apic.jpa.bo.IPIDCAttribute;
import com.bosch.caltool.comparator.AbstractComparator;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * @author jvi6cob
 */
public abstract class AbstractProjectComparator extends AbstractComparator<ApicObject, PidcCompareResult> {


  /**
   * Set to true to indicate that the compared objects are PID Cards
   */
  private boolean isPIDCard;
  /**
   * Attributes of first compare object
   */
  protected Map<Long, IPIDCAttribute> allAttrObj1;
  /**
   * Attributes of Second compare object
   */
  protected Map<Long, IPIDCAttribute> allAttrObj2;

  /**
   * Constructor for comparison of objects
   *
   * @param object1 ApicObject to compare
   * @param object2 ApicObject to compare
   */
  public AbstractProjectComparator(final ApicObject object1, final ApicObject object2) {
    super(object1, object2);
  }

  @Override
  public void compare() {
    initialize();
    constructPidcCompareResults(this.allAttrObj1, this.allAttrObj2);
  }


  /**
   *
   */
  protected abstract void initialize();

  /**
   * Constructs {@link PidcCompareResult} array
   *
   * @param allAttrObj1
   * @param allAttrObj2
   */
  private void constructPidcCompareResults(final Map<Long, IPIDCAttribute> allAttrObj1,
      final Map<Long, IPIDCAttribute> allAttrObj2) {
    final SortedSet<PidcCompareResult> pidcCompResults = new TreeSet<PidcCompareResult>();
    // To find the attributes map with more number of elements
    final Map<Long, IPIDCAttribute> allAttrMap = (allAttrObj1.size() >= allAttrObj2.size()) ? allAttrObj1 : allAttrObj2;

    // Construct One PidcCompareResult for each Attribute i.e One PidcCompareResult represents one row in excel sheet
    // Construct two compare columns for each PidcCompareResult
    for (Long attributeID : allAttrMap.keySet()) {
      // Compare Obj 1
      final IPIDCAttribute attributePIDCard1 = allAttrObj1.get(attributeID);
      final CompareColumns compareCol1 = constructCompareColumns(attributePIDCard1);
      // Compare Obj 2
      final IPIDCAttribute attributePIDCard2 = allAttrObj2.get(attributeID);
      final CompareColumns compareCol2 = constructCompareColumns(attributePIDCard2);

      // Construct PidcCompareResult
      final PidcCompareResult pidcCompareResult =
          getPidcCompareResult(attributePIDCard1, compareCol1, attributePIDCard2, compareCol2);
      pidcCompResults.add(pidcCompareResult);
    }
    setResult(pidcCompResults);
  }

  /**
   * Creates PidcCompareResult
   *
   * @param attribute1
   * @param columnsCard1
   * @param attribute2
   * @param columnsCard2
   * @return
   */
  private PidcCompareResult getPidcCompareResult(final IPIDCAttribute attribute1, final CompareColumns columnsCard1,
      final IPIDCAttribute attribute2, final CompareColumns columnsCard2) {
    final PidcCompareResult pidcCompareResult = new PidcCompareResult();
    // If attribute is not existing in one card use the other card's attribute
    // Attribute should exist in one of the PID cards
    if (attribute1 == null) {
      pidcCompareResult.setAttribute(attribute2.getAttribute());
    }
    else {
      pidcCompareResult.setAttribute(attribute1.getAttribute());
    }
    pidcCompareResult.setFirstColumnSet(columnsCard1);
    pidcCompareResult.setSecondColumnSet(columnsCard2);

    // Set AttrValueDiff
    setDiffAttrValue(columnsCard1, columnsCard2, pidcCompareResult);

    // Set AttrUsedFlagDiff
    setDiffUsedFlag(columnsCard1, columnsCard2, pidcCompareResult);
    return pidcCompareResult;
  }

  /**
   * Method used to fill attrUsedFlagDif field of {@link PidcCompareResult}
   *
   * @param columnsCard1
   * @param columnsCard2
   * @param pidcCompareResult
   */
  private void setDiffUsedFlag(final CompareColumns columnsCard1, final CompareColumns columnsCard2,
      final PidcCompareResult pidcCompareResult) {
    final String card1UsedFlag = columnsCard1.getUsedFlag();
    final String card2UsedFlag = columnsCard2.getUsedFlag();
    if (!card1UsedFlag.equals(card2UsedFlag)) {
      pidcCompareResult.setAttrUsedFlagDiff(true);
    }
  }

  /**
   * Method used to fill attrValueDiff field of {@link PidcCompareResult}
   *
   * @param columnsCard1
   * @param columnsCard2
   * @param pidcCompareResult
   */
  private void setDiffAttrValue(final CompareColumns columnsCard1, final CompareColumns columnsCard2,
      final PidcCompareResult pidcCompareResult) {
    final String card1AttrValue = columnsCard1.getAttrValue();
    final String card2AttrValue = columnsCard2.getAttrValue();
    boolean isDiffers;
    if ((card1AttrValue == null) && (card2AttrValue == null)) {
      isDiffers = false;
    }
    else if ((card1AttrValue == null) || (card2AttrValue == null)) {
      isDiffers = true;
    }
    else {
      isDiffers = !card1AttrValue.equals(card2AttrValue);
    }
    if (isDiffers) {
      pidcCompareResult.setAttrValueDiff(true);
    }
  }

  /**
   * Creates {@link CompareColumns} from the {@link IPIDCAttribute} parameter
   *
   * @param attribute IPIDCAttribute
   * @return compareColumnsCard CompareColumns
   */
  private CompareColumns constructCompareColumns(final IPIDCAttribute attribute) {
    final CompareColumns compareColCard = new CompareColumns();
    String strUsedFlagPID;
    String strValuePID;
    if (attribute == null) {
      compareColCard.setUsedFlag("");
      compareColCard.setAttrValue("");
      compareColCard.setValueType(null);
    }
    else {
      strUsedFlagPID = attribute.getIsUsed();
      compareColCard.setUsedFlag(strUsedFlagPID);
      final AttributeValue attributeVal = attribute.getAttributeValue();
      if (attributeVal == null) {
        if (attribute.isVariant()) { // For subvariant isVariant is false always
          if (this.isPIDCard) {// for PIDCard
            strValuePID = ApicConstants.VARIANT_ATTR_DISPLAY_NAME;
          }
          else {// for Variant
            strValuePID = ApicConstants.SUB_VARIANT_ATTR_DISPLAY_NAME;
          }
        }
        else {
          strValuePID = null; // NOPMD by jvi6cob on 6/26/13 11:08 AM
        }
      }
      else {
        strValuePID = attributeVal.getValue();
      }
      compareColCard.setAttrValue(strValuePID);
      compareColCard.setValueType(attribute.getAttribute().getValueType());
    }
    return compareColCard;
  }


}
