/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.comparison;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.apic.jpa.bo.ApicObject;
import com.bosch.caltool.apic.jpa.bo.IPIDCAttribute;
import com.bosch.caltool.apic.jpa.bo.PIDCAttribute;
import com.bosch.caltool.apic.jpa.bo.PIDCVersion;
import com.bosch.caltool.comparator.AbstractComparator;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * PidcComparator.java - The comparator implementation of two PIDCard
 *
 * @author adn1cob
 */
public class PidcComparator extends AbstractComparator<ApicObject, PidcCompareResult> {

  /**
   * Attributes of first compare object
   */
  private Map<Long, PIDCAttribute> allAttrObj1;
  /**
   * Attributes of Second compare object
   */
  private Map<Long, PIDCAttribute> allAttrObj2;

  /**
   * Constructor for PidcComparator
   *
   * @param pidc1 ProjectIDCard1
   * @param pidc2 ProjectIDCard2
   */
  public PidcComparator(final PIDCVersion pidc1, final PIDCVersion pidc2) {
    super(pidc1, pidc2);
  }

  /**
   * Compares the selected PID cards
   */
  @Override
  public void compare() {
    initialize();
    constructPidcCompareResults(this.allAttrObj1, this.allAttrObj2);
  }

  /**
   * Initialize the local instances
   */
  private void initialize() {

    final PIDCVersion pidcVer1 = (PIDCVersion) getObject1();
    final PIDCVersion pidcVer2 = (PIDCVersion) getObject2();

    this.allAttrObj1 = pidcVer1.getAttributes();
    this.allAttrObj2 = pidcVer2.getAttributes();

  }

  /**
   * Constructs {@link PidcCompareResult} array
   *
   * @param obj1AttrMap
   * @param obj2AttrMap
   */
  private void constructPidcCompareResults(final Map<Long, PIDCAttribute> obj1AttrMap,
      final Map<Long, PIDCAttribute> obj2AttrMap) {
    final SortedSet<PidcCompareResult> pidcCompResults = new TreeSet<>();
    // To find the attributes map with more number of elements
    final Map<Long, PIDCAttribute> allAttrMap = (obj1AttrMap.size() >= obj2AttrMap.size()) ? obj1AttrMap : obj2AttrMap;

    // Construct One PidcCompareResult for each Attribute i.e One PidcCompareResult represents one row in excel sheet
    // Construct two compare columns for each PidcCompareResult
    for (Long attributeID : allAttrMap.keySet()) {
      // Compare Obj 1
      final PIDCAttribute attributePIDCard1 = obj1AttrMap.get(attributeID);
      final CompareColumns compareCol1 = constructCompareColumns(attributePIDCard1);
      // Compare Obj 2
      final PIDCAttribute attributePIDCard2 = obj2AttrMap.get(attributeID);
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
    // Set Part Num diff
    setDiffPartNumber(columnsCard1, columnsCard2, pidcCompareResult);

    // Set SpecLink diff
    setDiffSpecLink(columnsCard1, columnsCard2, pidcCompareResult);
    // Set Desc diff
    setDiffDesc(columnsCard1, columnsCard2, pidcCompareResult);

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
    if (isAttrValueDiffered(card1AttrValue, card2AttrValue)) {
      pidcCompareResult.setAttrValueDiff(true);
    }
  }

  /**
   * Method to compare the given string
   *
   * @param card1AttrValue
   * @param card2AttrValue
   * @return
   */
  private boolean isAttrValueDiffered(final String card1AttrValue, final String card2AttrValue) {
    boolean returnVal;
    if ((card1AttrValue == null) && (card2AttrValue == null)) {
      returnVal = false;
    }
    else if ((card2AttrValue == null) || (card1AttrValue == null)) {
      returnVal = true;
    }
    else {
      returnVal = !card1AttrValue.equals(card2AttrValue);
    }
    return returnVal;
  }

  /**
   * Method used to fill PartNumber field of {@link PidcCompareResult}
   *
   * @param columnsCard1
   * @param columnsCard2
   * @param pidcCompareResult
   */
  private void setDiffPartNumber(final CompareColumns columnsCard1, final CompareColumns columnsCard2,
      final PidcCompareResult pidcCompareResult) {
    final String card1PartNum = columnsCard1.getPartNumber();
    final String card2PartNum = columnsCard2.getPartNumber();
    if (isAttrValueDiffered(card1PartNum, card2PartNum)) {
      pidcCompareResult.setPartNumberDiff(true);
    }
  }

  /**
   * Method used to fill SpecLink field of {@link PidcCompareResult}
   *
   * @param columnsCard1
   * @param columnsCard2
   * @param pidcCompareResult
   */
  private void setDiffSpecLink(final CompareColumns columnsCard1, final CompareColumns columnsCard2,
      final PidcCompareResult pidcCompareResult) {
    final String card1SpecLink = columnsCard1.getSpecLink();
    final String card2SpecLink = columnsCard2.getSpecLink();
    if (isAttrValueDiffered(card1SpecLink, card2SpecLink)) {
      pidcCompareResult.setSpecLinkDiff(true);
    }
  }

  /**
   * Method used to fill Desc field of {@link PidcCompareResult}
   *
   * @param columnsCard1
   * @param columnsCard2
   * @param pidcCompareResult
   */
  private void setDiffDesc(final CompareColumns columnsCard1, final CompareColumns columnsCard2,
      final PidcCompareResult pidcCompareResult) {
    final String card1Desc = columnsCard1.getDesc();
    final String card2Desc = columnsCard2.getDesc();
    if (isAttrValueDiffered(card1Desc, card2Desc)) {
      pidcCompareResult.setDescDiff(true);
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
      strUsedFlagPID = ApicConstants.EMPTY_STRING;
      strValuePID = ApicConstants.EMPTY_STRING;
      compareColCard.setPartNumber(ApicConstants.EMPTY_STRING);
      compareColCard.setSpecLink(ApicConstants.EMPTY_STRING);
      compareColCard.setDesc(ApicConstants.EMPTY_STRING);
      compareColCard.setValueType(null);
    }
    else {
      strUsedFlagPID = attribute.getIsUsed();
      strValuePID = attribute.getDefaultValueDisplayName();
      compareColCard.setPartNumber(attribute.getPartNumber());
      compareColCard.setSpecLink(attribute.getSpecLink());
      compareColCard.setDesc(attribute.getAdditionalInfoDesc());
      compareColCard.setValueType(attribute.getAttribute().getValueType());
    }
    compareColCard.setUsedFlag(strUsedFlagPID);
    compareColCard.setAttrValue(strValuePID);
    return compareColCard;
  }

}
