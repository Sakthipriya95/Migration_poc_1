/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.comparison;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.bosch.caltool.apic.jpa.bo.ApicObject;
import com.bosch.caltool.apic.jpa.bo.IPIDCAttribute;
import com.bosch.caltool.apic.jpa.bo.PIDCAttributeVar;
import com.bosch.caltool.apic.jpa.bo.PIDCSubVariant;
import com.bosch.caltool.apic.jpa.bo.PIDCVariant;
import com.bosch.caltool.comparator.AbstractComparator;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * @author jvi6cob
 */
public class PidcVariantComparator extends AbstractComparator<ApicObject, PidcVariantCompareResult> {


  /**
   * First Variant to Compare
   */
  private PIDCVariant variant1;
  /**
   * Second Variant to Compare
   */
  private PIDCVariant variant2;

  /**
   * Attributes of first compare object
   */
  private Map<Long, PIDCAttributeVar> allAttrObj1;
  /**
   * Attributes of Second compare object
   */
  private Map<Long, PIDCAttributeVar> allAttrObj2;

  /**
   * @param variant1 PIDCVariant
   * @param variant2 PIDCVariant
   */
  public PidcVariantComparator(final PIDCVariant variant1, final PIDCVariant variant2) {
    super(variant1, variant2);
  }

  /**
   * Initialize the local instances
   */
  private void initialize() {

    this.variant1 = (PIDCVariant) getObject1();
    this.variant2 = (PIDCVariant) getObject2();

    this.allAttrObj1 = this.variant1.getAttributes();
    this.allAttrObj2 = this.variant2.getAttributes();

  }


  /**
   * Compares the selected Variants
   */
  @Override
  public void compare() {

    initialize();

    constructPidcCompareResults();

  }


  /**
   * Constructs {@link PidcCompareResult} array
   *
   * @param allAttrObj1
   * @param allAttrObj2
   */
  private void constructPidcCompareResults() {

    final SortedSet<PidcVariantCompareResult> pidcVarCmpResults = new TreeSet<PidcVariantCompareResult>();


    // To find the attributes map with more number of elements
    final Map<Long, PIDCAttributeVar> allAttrMap = new HashMap<>();
    if (this.allAttrObj1.size() >= this.allAttrObj2.size()) {
      allAttrMap.putAll(this.allAttrObj1);
    }
    else {
      allAttrMap.putAll(this.allAttrObj2);
    }

    final Map<Long, PIDCSubVariant> subVariantsMap1 = this.variant1.getSubVariantsMap();
    final Map<Long, PIDCSubVariant> subVariantsMap2 = this.variant2.getSubVariantsMap();

    // Construct One PidcCompareResult for each Attribute i.e One PidcCompareResult represents one row in excel sheet
    // Construct two compare columns for each PidcCompareResult
    for (Long attributeID : allAttrMap.keySet()) {

      // Compare Obj 1
      final IPIDCAttribute attributePIDCard1 = this.allAttrObj1.get(attributeID);
      final CompareColumns compareCol1 = constructCompareColumns(attributePIDCard1);
      // Compare Obj 2
      final IPIDCAttribute attributePIDCard2 = this.allAttrObj2.get(attributeID);
      final CompareColumns compareCol2 = constructCompareColumns(attributePIDCard2);


      // Construct PidcCompareResult
      final PidcVariantCompareResult pidcVarCompResult = constructPIDCCompareResult(subVariantsMap1, subVariantsMap2,
          attributeID, attributePIDCard1, compareCol1, attributePIDCard2, compareCol2);
      pidcVarCmpResults.add(pidcVarCompResult);
    }

    setResult(pidcVarCmpResults);

  }

  /**
   * @param subVariantsMap1
   * @param subVariantsMap2
   * @param attributeID
   * @param attributePIDCard1
   * @param compareCol1
   * @param attributePIDCard2
   * @param compareCol2
   * @return
   */
  public PidcVariantCompareResult constructPIDCCompareResult(final Map<Long, PIDCSubVariant> subVariantsMap1,
      final Map<Long, PIDCSubVariant> subVariantsMap2, final Long attributeID, final IPIDCAttribute attributePIDCard1,
      final CompareColumns compareCol1, final IPIDCAttribute attributePIDCard2, final CompareColumns compareCol2) {
    final PidcVariantCompareResult pidcVarCompResult =
        getPidcVariantCompareResult(attributePIDCard1, compareCol1, attributePIDCard2, compareCol2);

    final Map<PIDCVariant, Map<PIDCSubVariant, PidcCompareResult>> varSubVarMap =
        new TreeMap<PIDCVariant, Map<PIDCSubVariant, PidcCompareResult>>();

    final Map<PIDCSubVariant, PidcCompareResult> subVarCmpResults1 = new TreeMap<PIDCSubVariant, PidcCompareResult>();
    // Fill attribute of Sub Variants of Var1
    for (PIDCSubVariant subVariant : subVariantsMap1.values()) {
      final IPIDCAttribute attributeSubvar = subVariant.getAttributes().get(attributeID);
      if (attributeSubvar != null) {
        final CompareColumns compareColSubVar = constructCompareColumns(attributeSubvar);
        final CompareColumns emptyColSubVar = constructEmptyCompareColumn();
        final PidcCompareResult cmpSubVarResult =
            getPidcCompareSubVarResult(attributeSubvar, compareColSubVar, null, emptyColSubVar);
        subVarCmpResults1.put(subVariant, cmpSubVarResult);
      }
    }

    varSubVarMap.put(this.variant1, subVarCmpResults1);

    final Map<PIDCSubVariant, PidcCompareResult> subVarCmpResults2 = new TreeMap<PIDCSubVariant, PidcCompareResult>();
    // Fill attribute of Sub Variants of Var1
    for (PIDCSubVariant subVariant : subVariantsMap2.values()) {
      final IPIDCAttribute attributeSubvar = subVariant.getAttributes().get(attributeID);
      if (attributeSubvar != null) {
        final CompareColumns emptyCmpColSubVar = constructEmptyCompareColumn();
        final CompareColumns compareColSubVar = constructCompareColumns(attributeSubvar);
        final PidcCompareResult cmpSubVarResult =
            getPidcCompareSubVarResult(attributeSubvar, emptyCmpColSubVar, null, compareColSubVar);
        subVarCmpResults2.put(subVariant, cmpSubVarResult);
      }
    }
    varSubVarMap.put(this.variant2, subVarCmpResults2);

    pidcVarCompResult.setVariantSubVariantMap(varSubVarMap);
    return pidcVarCompResult;
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
  private PidcVariantCompareResult getPidcVariantCompareResult(final IPIDCAttribute attribute1,
      final CompareColumns columnsCard1, final IPIDCAttribute attribute2, final CompareColumns columnsCard2) {
    final PidcVariantCompareResult pidcVarCmpResult = new PidcVariantCompareResult();
    // If attribute is not existing in one card use the other card's attribute
    // Attribute should exist in one of the PID cards
    if (attribute1 == null) {
      pidcVarCmpResult.setAttribute(attribute2.getAttribute());
    }
    else {
      pidcVarCmpResult.setAttribute(attribute1.getAttribute());
    }
    pidcVarCmpResult.setFirstColumnSet(columnsCard1);
    pidcVarCmpResult.setSecondColumnSet(columnsCard2);

    // Set AttrValueDiff
    setDiffAttrValue(columnsCard1, columnsCard2, pidcVarCmpResult);

    // Set AttrUsedFlagDiff
    setDiffUsedFlag(columnsCard1, columnsCard2, pidcVarCmpResult);
    // Set Part Num diff
    setDiffPartNumber(columnsCard1, columnsCard2, pidcVarCmpResult);

    // Set SpecLink diff
    setDiffSpecLink(columnsCard1, columnsCard2, pidcVarCmpResult);
    // Set Desc diff
    setDiffDesc(columnsCard1, columnsCard2, pidcVarCmpResult);

    return pidcVarCmpResult;
  }

  /**
   * @param attributeSubvar
   * @param compareColSubVar
   * @param object
   * @param emptyCompareColSubVar
   */
  private PidcCompareResult getPidcCompareSubVarResult(final IPIDCAttribute attribute1,
      final CompareColumns columnsCard1, final IPIDCAttribute attribute2, final CompareColumns columnsCard2) {
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

    pidcCompareResult.setDiff(false);
    pidcCompareResult.setAttrValueDiff(false);

    return pidcCompareResult;

  }


  /**
   * Method used to fill attrUsedFlagDif field of {@link PidcCompareResult}
   *
   * @param columnsCard1
   * @param columnsCard2
   * @param pidcVarCmpResult
   */
  private void setDiffUsedFlag(final CompareColumns columnsCard1, final CompareColumns columnsCard2,
      final PidcVariantCompareResult pidcVarCmpResult) {
    final String card1UsedFlag = columnsCard1.getUsedFlag();
    final String card2UsedFlag = columnsCard2.getUsedFlag();
    if (!card1UsedFlag.equals(card2UsedFlag)) {
      pidcVarCmpResult.setAttrUsedFlagDiff(true);
    }
  }

  /**
   * Method used to fill attrValueDiff field of {@link PidcCompareResult}
   *
   * @param columnsCard1
   * @param columnsCard2
   * @param pidcVarCmpResult
   */
  private void setDiffAttrValue(final CompareColumns columnsCard1, final CompareColumns columnsCard2,
      final PidcVariantCompareResult pidcVarCmpResult) {
    final String card1AttrValue = columnsCard1.getAttrValue();
    final String card2AttrValue = columnsCard2.getAttrValue();
    if (isAttrValueDiffers(card1AttrValue, card2AttrValue)) {
      pidcVarCmpResult.setAttrValueDiff(true);
    }
  }

  /**
   * Method to compare the given string
   *
   * @param card1AttrValue
   * @param card2AttrValue
   * @return
   */
  private boolean isAttrValueDiffers(final String card1AttrValue, final String card2AttrValue) {
    return ((card1AttrValue == null) && (card2AttrValue != null)) ||
        ((card2AttrValue == null) && (card1AttrValue != null)) ||
        ((card1AttrValue != null) && (card2AttrValue != null) && !card1AttrValue.equals(card2AttrValue));
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


  private CompareColumns constructEmptyCompareColumn() {
    final CompareColumns compareColCard = new CompareColumns();
    compareColCard.setUsedFlag("");
    compareColCard.setAttrValue("");
    compareColCard.setPartNumber("");
    compareColCard.setSpecLink("");
    compareColCard.setDesc("");
    compareColCard.setValueType(null);
    return compareColCard;
  }

  /**
   * Method used to fill PartNumber field of {@link PidcCompareResult}
   *
   * @param columnsCard1
   * @param columnsCard2
   * @param pidcCompareResult
   */
  private void setDiffPartNumber(final CompareColumns columnsCard1, final CompareColumns columnsCard2,
      final PidcVariantCompareResult pidcCompareResult) {
    final String card1PartNum = columnsCard1.getPartNumber();
    final String card2PartNum = columnsCard2.getPartNumber();
    if (isAttrValueDiffers(card1PartNum, card2PartNum)) {
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
      final PidcVariantCompareResult pidcCompareResult) {
    final String card1SpecLink = columnsCard1.getSpecLink();
    final String card2SpecLink = columnsCard2.getSpecLink();
    if (isAttrValueDiffers(card1SpecLink, card2SpecLink)) {
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
      final PidcVariantCompareResult pidcCompareResult) {
    final String card1Desc = columnsCard1.getDesc();
    final String card2Desc = columnsCard2.getDesc();
    if (isAttrValueDiffers(card1Desc, card2Desc)) {
      pidcCompareResult.setDescDiff(true);
    }
  }
}
