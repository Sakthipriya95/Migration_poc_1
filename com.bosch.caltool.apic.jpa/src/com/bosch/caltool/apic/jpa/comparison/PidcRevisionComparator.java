/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.comparison;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.apic.jpa.bo.ApicObject;
import com.bosch.caltool.apic.jpa.bo.IPIDCAttribute;
import com.bosch.caltool.apic.jpa.bo.PIDCAttribute;
import com.bosch.caltool.apic.jpa.bo.PIDCAttributeSubVar;
import com.bosch.caltool.apic.jpa.bo.PIDCAttributeVar;
import com.bosch.caltool.apic.jpa.bo.PIDCSubVariant;
import com.bosch.caltool.apic.jpa.bo.PIDCVariant;
import com.bosch.caltool.apic.jpa.bo.PIDCVersion;
import com.bosch.caltool.comparator.AbstractComparator;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * @author jvi6cob
 */
public class PidcRevisionComparator extends AbstractComparator<ApicObject, PidcRevisionCompareResult> {

  /**
   * First Variant to Compare
   */
  private PIDCVersion pidcVer1;
  /**
   * Second Variant to Compare
   */
  private PIDCVersion pidcVer2;

  /**
   * Attributes of first compare object
   */
  private Map<Long, PIDCAttribute> allAttrObj1;
  /**
   * Attributes of Second compare object
   */
  private Map<Long, PIDCAttribute> allAttrObj2;

  private final Map<PIDCVariant, Map<Long, PIDCAttributeVar>> allVariantsAttrMap = new ConcurrentHashMap<>();
  private final Map<PIDCSubVariant, Map<Long, PIDCAttributeSubVar>> allSubVariantsAttrMap = new ConcurrentHashMap<>();

  // Populate varSubVariant Map two pid cards
  private final Map<PIDCVariant, SortedSet<PIDCSubVariant>> varSubvarMap1 = new ConcurrentHashMap<>();
  private final Map<PIDCVariant, SortedSet<PIDCSubVariant>> varSubvarMap2 = new ConcurrentHashMap<>();

  private boolean attrLevelDiffFlag;
  private boolean varLevelDiffFlag;

  /**
   * @param pidcVer1 PIDCVariant
   * @param pidcVer2 PIDCVariant
   */
  public PidcRevisionComparator(final PIDCVersion pidcVer1, final PIDCVersion pidcVer2) {
    super(pidcVer1, pidcVer2);
  }

  /**
   * Initialize the local instances
   */
  private void initialize() {

    this.pidcVer1 = (PIDCVersion) getObject1();
    this.pidcVer2 = (PIDCVersion) getObject2();

    this.allAttrObj1 = this.pidcVer1.getAttributes();
    this.allAttrObj2 = this.pidcVer2.getAttributes();

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

    final SortedSet<PidcRevisionCompareResult> pidcRevCmpResults = new TreeSet<PidcRevisionCompareResult>();

    // To find the attributes map with more number of elements
    final Map<Long, IPIDCAttribute> allAttrMap = new ConcurrentHashMap<Long, IPIDCAttribute>();
    if (this.allAttrObj1.size() >= this.allAttrObj2.size()) {
      allAttrMap.putAll(this.allAttrObj1);
    }
    else {
      allAttrMap.putAll(this.allAttrObj2);
    }

    final SortedSet<PIDCVariant> variants1 = this.pidcVer1.getVariantsSet();
    final SortedSet<PIDCVariant> variants2 = this.pidcVer2.getVariantsSet();

    for (PIDCVariant pidcVariant : variants1) {
      SortedSet<PIDCSubVariant> subVariantsSet = pidcVariant.getSubVariantsSet();
      this.varSubvarMap1.put(pidcVariant, subVariantsSet);
      for (PIDCSubVariant pidcSubVariant : subVariantsSet) {
        // Fill All sub variant attributes map
        this.allSubVariantsAttrMap.put(pidcSubVariant, pidcSubVariant.getAttributes());
      }
      // Fill All variant attributes map
      this.allVariantsAttrMap.put(pidcVariant, pidcVariant.getAttributes());
    }

    for (PIDCVariant pidcVariant : variants2) {
      SortedSet<PIDCSubVariant> subVariantsSet = pidcVariant.getSubVariantsSet();
      this.varSubvarMap2.put(pidcVariant, subVariantsSet);
      for (PIDCSubVariant pidcSubVariant : subVariantsSet) {
        // Fill All sub variant attributes map
        this.allSubVariantsAttrMap.put(pidcSubVariant, pidcSubVariant.getAttributes());
      }
      // Fill All variant attributes map
      this.allVariantsAttrMap.put(pidcVariant, pidcVariant.getAttributes());
    }

    // Construct One PidcCompareResult for each Attribute i.e One
    // PidcCompareResult represents one row in excel sheet
    // Construct two compare columns for each PidcCompareResult
    for (Long attributeID : allAttrMap.keySet()) {

      // Compare Obj 1
      final IPIDCAttribute attributePIDCard1 = this.allAttrObj1.get(attributeID);
      final CompareColumns compareCol1 = constructCompareColumns(attributePIDCard1);
      // Compare Obj 2
      final IPIDCAttribute attributePIDCard2 = this.allAttrObj2.get(attributeID);
      final CompareColumns compareCol2 = constructCompareColumns(attributePIDCard2);

      final PidcRevisionCompareResult pidcRevCompResult = constructPIDCRevisionCompareResult(attributeID,
          attributePIDCard1, compareCol1, attributePIDCard2, compareCol2);
      pidcRevCmpResults.add(pidcRevCompResult);
    }

    setResult(pidcRevCmpResults);

  }

  private PidcRevisionCompareResult constructPIDCRevisionCompareResult(final Long attributeID,
      final IPIDCAttribute attributePIDCard1, final CompareColumns compareCol1, final IPIDCAttribute attributePIDCard2,
      final CompareColumns compareCol2) {
    final PidcRevisionCompareResult pidcRevCompResult =
        getPidcRevisionCompareResult(attributePIDCard1, compareCol1, attributePIDCard2, compareCol2);

    // Skip Variant Comparison if both attributes are not variants
    if ((attributePIDCard1 != null) && (attributePIDCard2 != null) && !attributePIDCard1.isVariant() &&
        !attributePIDCard2.isVariant()) {
      return pidcRevCompResult;
    }

    this.attrLevelDiffFlag = false;

    List<PidcRevisionVariantCompareResult> pidcRevVarCmpResults = new ArrayList<PidcRevisionVariantCompareResult>();

    final Map<PIDCVariant, SortedSet<PIDCSubVariant>> varRemovedSubvarMap2 =
        new ConcurrentHashMap<PIDCVariant, SortedSet<PIDCSubVariant>>();
    varRemovedSubvarMap2.putAll(this.varSubvarMap2);

    if ((attributePIDCard1 != null) && attributePIDCard1.isVariant()) {
      // Fill attribute of Variants and Subvariants of PID cards
      for (PIDCVariant pidcVariant1 : this.varSubvarMap1.keySet()) {
        if (pidcVariant1.isDeleted()) {
          continue;
        }
        Map<Long, PIDCAttributeVar> variant1AttrMap = this.allVariantsAttrMap.get(pidcVariant1);
        IPIDCAttribute variant1Attribute = variant1AttrMap.get(attributeID);
        // Compare Variants of Two PidCards
        PIDCVariant pidcVariant2 = null;
        for (PIDCVariant pidcVariant : this.varSubvarMap2.keySet()) {
          if (pidcVariant.getVariantName().equals(pidcVariant1.getVariantName())) {
            pidcVariant2 = pidcVariant;
            // To avoid parsing already compared variant entries
            varRemovedSubvarMap2.remove(pidcVariant);
            break;
          }
        }

        Map<Long, PIDCAttributeVar> variant2AttrMap = this.allVariantsAttrMap.get(pidcVariant2);
        IPIDCAttribute variant2Attribute = null;
        if ((variant2AttrMap != null) && !variant2AttrMap.isEmpty()) {
          variant2Attribute = variant2AttrMap.get(attributeID);
        }

        // If attribute not present in both variants continue with next
        // set of variants
        if ((variant1Attribute == null) && (variant2Attribute == null)) {
          continue;
        }

        // Compare Obj 1
        CompareColumns compareColVar1;
        if (CommonUtils.isNull(variant1Attribute)) {
          compareColVar1 = constructEmptyCompareColumn();
        }
        else {
          compareColVar1 = constructCompareColumns(variant1Attribute);
        }
        // Compare Obj 2
        CompareColumns compareColVar2;
        if (CommonUtils.isNull(variant2Attribute)) {
          compareColVar2 = constructEmptyCompareColumn();
        }
        else {
          compareColVar2 = constructCompareColumns(variant2Attribute);
        }

        // Construct PidcCompareResult
        final PidcRevisionVariantCompareResult pidcRevVarCompResult =
            constructPIDCRevisionVariantCompareResult(pidcVariant1, pidcVariant2, attributeID, variant1Attribute,
                compareColVar1, variant2Attribute, compareColVar2);
        pidcRevVarCmpResults.add(pidcRevVarCompResult);
      }
    }
    if ((attributePIDCard2 != null) && attributePIDCard2.isVariant()) {
      for (PIDCVariant pidcVariant2 : varRemovedSubvarMap2.keySet()) {
        if (pidcVariant2.isDeleted()) {
          continue;
        }
        Map<Long, PIDCAttributeVar> variant2AttrMap = this.allVariantsAttrMap.get(pidcVariant2);
        IPIDCAttribute variant2Attribute = variant2AttrMap.get(attributeID);
        if (variant2Attribute == null) {
          continue;
        }
        CompareColumns compareColVar1 = constructEmptyCompareColumn();
        CompareColumns compareColVar2 = constructCompareColumns(variant2Attribute);
        // Construct PidcCompareResult
        final PidcRevisionVariantCompareResult pidcRevVarCompResult = constructPIDCRevisionVariantCompareResult(null,
            pidcVariant2, attributeID, null, compareColVar1, variant2Attribute, compareColVar2);
        pidcRevVarCmpResults.add(pidcRevVarCompResult);
      }
    }
    Collections.sort(pidcRevVarCmpResults, new VarNameComparator());
    pidcRevCompResult.setPidcRevisionVariantCompareResults(pidcRevVarCmpResults);
    if ((attributePIDCard1 != null) && (attributePIDCard2 != null) && attributePIDCard1.isVariant() &&
        attributePIDCard2.isVariant() && this.attrLevelDiffFlag) {
      pidcRevCompResult.setAttrValueDiff(true);
    }

    return pidcRevCompResult;
  }


  private static class VarNameComparator implements Comparator<PidcRevisionVariantCompareResult> {

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(final PidcRevisionVariantCompareResult arg0, final PidcRevisionVariantCompareResult arg1) {
      return arg0.getPidcVariant().getVariantName().compareTo(arg1.getPidcVariant().getVariantName());
    }

  }

  private PidcRevisionVariantCompareResult constructPIDCRevisionVariantCompareResult(final PIDCVariant pidcVariant1,
      final PIDCVariant pidcVariant2, final Long attributeID, final IPIDCAttribute attributeVar1,
      final CompareColumns compareColVar1, final IPIDCAttribute attributeVar2, final CompareColumns compareColVar2) {

    // pidcVariant1 can be null
    // pidcVariant2 can be null
    // both pidcVariants cant be null or maybe...
    // attributeVar1 can be null
    // attributeVar2 can be null

    //
    Map<Long, PIDCSubVariant> subVariantsMap1 = new ConcurrentHashMap<Long, PIDCSubVariant>();
    Map<Long, PIDCSubVariant> subVariantsMap2 = new ConcurrentHashMap<Long, PIDCSubVariant>();

    if (pidcVariant1 != null) {
      subVariantsMap1 = pidcVariant1.getSubVariantsMap();
    }

    if (pidcVariant2 != null) {
      subVariantsMap2 = pidcVariant2.getSubVariantsMap();
    }

    this.varLevelDiffFlag = false;

    final PidcRevisionVariantCompareResult pidcRevVarCompResult =
        getPidcRevisionVariantCompareResult(attributeVar1, compareColVar1, attributeVar2, compareColVar2);

    final Map<PIDCSubVariant, PidcCompareResult> subVarCmpResults = new TreeMap<PIDCSubVariant, PidcCompareResult>();

    final Map<Long, PIDCSubVariant> subVariantsRemovedMap1 = new ConcurrentHashMap<Long, PIDCSubVariant>();
    subVariantsRemovedMap1.putAll(subVariantsMap1);

    final Map<Long, PIDCSubVariant> subVariantsRemovedMap2 = new ConcurrentHashMap<Long, PIDCSubVariant>();
    subVariantsRemovedMap2.putAll(subVariantsMap2);
    // Fill attribute of Sub Variants of Var1
    for (PIDCSubVariant subVariant : subVariantsMap1.values()) {
      final IPIDCAttribute attributeSubvar1 = subVariant.getAttributes().get(attributeID);
      if (attributeSubvar1 != null) {
        final CompareColumns compareColSubVar1 = constructCompareColumns(attributeSubvar1);
        CompareColumns compareColSubVar2 = constructEmptyCompareColumn();
        IPIDCAttribute attributeSubvar2 = null;
        if (!subVariantsMap2.isEmpty()) {
          for (PIDCSubVariant subVariant2 : subVariantsMap2.values()) {
            if (subVariant2.getSubVariantName().equals(subVariant.getSubVariantName())) {
              attributeSubvar2 = subVariant2.getAttributes().get(attributeID);
              compareColSubVar2 = constructCompareColumns(attributeSubvar2);
              // To avoid parsing already compared subvariant entries
              subVariantsRemovedMap2.remove(subVariant2.getSubVariantID());
              break;
            }
          }
        }
        final PidcCompareResult cmpSubVarResult =
            getPidcCompareSubVarResult(attributeSubvar1, compareColSubVar1, attributeSubvar2, compareColSubVar2);
        subVarCmpResults.put(subVariant, cmpSubVarResult);
      }
    }

    // Fill attribute of Sub Variants of Var2
    for (PIDCSubVariant subVariant : subVariantsRemovedMap2.values()) {
      final IPIDCAttribute attributeSubvar2 = subVariant.getAttributes().get(attributeID);
      CompareColumns compareColSubVar1 = constructEmptyCompareColumn();
      final CompareColumns compareColSubVar2 = constructCompareColumns(attributeSubvar2);
      if (attributeSubvar2 != null) {
        final PidcCompareResult cmpSubVarResult =
            getPidcCompareSubVarResult(null, compareColSubVar1, attributeSubvar2, compareColSubVar2);
        subVarCmpResults.put(subVariant, cmpSubVarResult);
      }
    }

    if (((pidcVariant1 != null) && (pidcVariant2 == null)) || ((pidcVariant1 != null) && (pidcVariant2 != null))) {
      pidcRevVarCompResult.setPidcVariant(pidcVariant1);
    }
    else if ((pidcVariant1 == null) && (pidcVariant2 != null)) {
      pidcRevVarCompResult.setPidcVariant(pidcVariant2);
    }
    pidcRevVarCompResult.setSubVariantMap(subVarCmpResults);
    if ((attributeVar1 != null) && (attributeVar2 != null) && attributeVar1.isVariant() && attributeVar2.isVariant() &&
        this.varLevelDiffFlag) {
      this.attrLevelDiffFlag = true;
      pidcRevVarCompResult.setAttrValueDiff(true);
    }
    return pidcRevVarCompResult;
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
  private PidcRevisionCompareResult getPidcRevisionCompareResult(final IPIDCAttribute attribute1,
      final CompareColumns columnsCard1, final IPIDCAttribute attribute2, final CompareColumns columnsCard2) {
    final PidcRevisionCompareResult pidcRevCmpResult = new PidcRevisionCompareResult();
    // If attribute is not existing in one card use the other card's
    // attribute
    // Attribute should exist in one of the PID cards
    if (attribute1 == null) {
      pidcRevCmpResult.setAttribute(attribute2.getAttribute());
    }
    else {
      pidcRevCmpResult.setAttribute(attribute1.getAttribute());
    }
    pidcRevCmpResult.setFirstColumnSet(columnsCard1);
    pidcRevCmpResult.setSecondColumnSet(columnsCard2);

    // Set AttrValueDiff
    setDiffAttrValue(columnsCard1, columnsCard2, pidcRevCmpResult);

    // Set AttrUsedFlagDiff
    setDiffUsedFlag(columnsCard1, columnsCard2, pidcRevCmpResult);
    // Set Part Num diff
    setDiffPartNumber(columnsCard1, columnsCard2, pidcRevCmpResult);

    // Set SpecLink diff
    setDiffSpecLink(columnsCard1, columnsCard2, pidcRevCmpResult);
    // Set Desc diff
    setDiffDesc(columnsCard1, columnsCard2, pidcRevCmpResult);


    return pidcRevCmpResult;
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
    // If attribute is not existing in one card use the other card's
    // attribute
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
      this.varLevelDiffFlag = true;
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
      this.varLevelDiffFlag = true;
    }
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
  private PidcRevisionVariantCompareResult getPidcRevisionVariantCompareResult(final IPIDCAttribute attribute1,
      final CompareColumns columnsCard1, final IPIDCAttribute attribute2, final CompareColumns columnsCard2) {
    final PidcRevisionVariantCompareResult pidcRevVarCmpResult = new PidcRevisionVariantCompareResult();
    // If attribute is not existing in one card use the other card's
    // attribute
    // Attribute should exist in one of the PID cards

    if ((attribute1 == null) && (attribute2 == null)) {
      pidcRevVarCmpResult.setAttribute(null);
    }
    else if (attribute1 == null) {
      pidcRevVarCmpResult.setAttribute(attribute2.getAttribute());
    }
    else {
      pidcRevVarCmpResult.setAttribute(attribute1.getAttribute());
    }
    pidcRevVarCmpResult.setFirstColumnSet(columnsCard1);
    pidcRevVarCmpResult.setSecondColumnSet(columnsCard2);

    // Set AttrValueDiff
    setDiffAttrValue(columnsCard1, columnsCard2, pidcRevVarCmpResult);
    // Set AttrUsedFlagDiff
    setDiffUsedFlag(columnsCard1, columnsCard2, pidcRevVarCmpResult);
    // Set PartNumber Diff
    setDiffPartNumber(columnsCard1, columnsCard2, pidcRevVarCmpResult);
    // Set SpecLink Diff
    setDiffSpecLink(columnsCard1, columnsCard2, pidcRevVarCmpResult);
    // Set Desc Diff
    setDiffDesc(columnsCard1, columnsCard2, pidcRevVarCmpResult);

    return pidcRevVarCmpResult;
  }

  /**
   * Method used to fill attrUsedFlagDif field of {@link PidcCompareResult}
   *
   * @param columnsCard1
   * @param columnsCard2
   * @param pidcRevCmpResult
   */
  private void setDiffUsedFlag(final CompareColumns columnsCard1, final CompareColumns columnsCard2,
      final PidcRevisionCompareResult pidcRevCmpResult) {
    final String card1UsedFlag = columnsCard1.getUsedFlag();
    final String card2UsedFlag = columnsCard2.getUsedFlag();
    if (!card1UsedFlag.equals(card2UsedFlag)) {
      pidcRevCmpResult.setAttrUsedFlagDiff(true);
    }
  }

  /**
   * Method used to fill attrValueDiff field of {@link PidcCompareResult}
   *
   * @param columnsCard1
   * @param columnsCard2
   * @param pidcRevCmpResult
   */
  private void setDiffAttrValue(final CompareColumns columnsCard1, final CompareColumns columnsCard2,
      final PidcRevisionCompareResult pidcRevCmpResult) {
    final String card1AttrValue = columnsCard1.getAttrValue();
    final String card2AttrValue = columnsCard2.getAttrValue();
    if (isAttrValueDiffered(card1AttrValue, card2AttrValue)) {
      pidcRevCmpResult.setAttrValueDiff(true);
    }
  }

  /**
   * Method used to fill PartNumber field of {@link PidcCompareResult}
   *
   * @param columnsCard1
   * @param columnsCard2
   * @param pidcRevCmpResult
   */
  private void setDiffPartNumber(final CompareColumns columnsCard1, final CompareColumns columnsCard2,
      final PidcRevisionCompareResult pidcRevCmpResult) {
    final String card1PartNum = columnsCard1.getPartNumber();
    final String card2PartNum = columnsCard2.getPartNumber();
    if (isAttrValueDiffered(card1PartNum, card2PartNum)) {
      pidcRevCmpResult.setPartNumberDiff(true);
    }
  }

  /**
   * Method used to fill SpecLink field of {@link PidcCompareResult}
   *
   * @param columnsCard1
   * @param columnsCard2
   * @param pidcRevCmpResult
   */
  private void setDiffSpecLink(final CompareColumns columnsCard1, final CompareColumns columnsCard2,
      final PidcRevisionCompareResult pidcRevCmpResult) {
    final String card1SpecLink = columnsCard1.getSpecLink();
    final String card2SpecLink = columnsCard2.getSpecLink();
    if (isAttrValueDiffered(card1SpecLink, card2SpecLink)) {
      pidcRevCmpResult.setSpecLinkDiff(true);
    }
  }


  /**
   * Method used to fill Desc field of {@link PidcCompareResult}
   *
   * @param columnsCard1
   * @param columnsCard2
   * @param pidcRevCmpResult
   */
  private void setDiffDesc(final CompareColumns columnsCard1, final CompareColumns columnsCard2,
      final PidcRevisionCompareResult pidcRevCmpResult) {
    final String card1Desc = columnsCard1.getDesc();
    final String card2Desc = columnsCard2.getDesc();
    if (isAttrValueDiffered(card1Desc, card2Desc)) {
      pidcRevCmpResult.setDescDiff(true);
    }
  }

  /**
   * Method to compare the given string
   *
   * @param card1Desc
   * @param card2Desc
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
   * Method used to fill attrUsedFlagDif field of {@link PidcCompareResult}
   *
   * @param columnsCard1
   * @param columnsCard2
   * @param pidcRevVarCmpResult
   */
  private void setDiffUsedFlag(final CompareColumns columnsCard1, final CompareColumns columnsCard2,
      final PidcRevisionVariantCompareResult pidcRevVarCmpResult) {
    final String card1UsedFlag = columnsCard1.getUsedFlag();
    final String card2UsedFlag = columnsCard2.getUsedFlag();
    if (!card1UsedFlag.equals(card2UsedFlag)) {
      pidcRevVarCmpResult.setAttrUsedFlagDiff(true);
      this.attrLevelDiffFlag = true;
    }
  }

  /**
   * Method used to fill attrValueDiff field of {@link PidcCompareResult}
   *
   * @param columnsCard1
   * @param columnsCard2
   * @param pidcRevVarCmpResult
   */
  private void setDiffAttrValue(final CompareColumns columnsCard1, final CompareColumns columnsCard2,
      final PidcRevisionVariantCompareResult pidcRevVarCmpResult) {
    final String card1AttrValue = columnsCard1.getAttrValue();
    final String card2AttrValue = columnsCard2.getAttrValue();
    if (isAttrValueDiffered(card1AttrValue, card2AttrValue)) {
      pidcRevVarCmpResult.setAttrValueDiff(true);
      this.attrLevelDiffFlag = true;
    }
  }

  /**
   * Method used to fill PartNumber field of {@link PidcCompareResult}
   *
   * @param columnsCard1
   * @param columnsCard2
   * @param pidcCompareResult
   */
  private void setDiffPartNumber(final CompareColumns columnsCard1, final CompareColumns columnsCard2,
      final PidcRevisionVariantCompareResult pidcRevVarCmpResult) {
    final String card1PartNum = columnsCard1.getPartNumber();
    final String card2PartNum = columnsCard2.getPartNumber();
    if (isAttrValueDiffered(card1PartNum, card2PartNum)) {
      pidcRevVarCmpResult.setPartNumberDiff(true);
      this.attrLevelDiffFlag = true;
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
      final PidcRevisionVariantCompareResult pidcRevVarCmpResult) {
    final String card1SpecLink = columnsCard1.getSpecLink();
    final String card2SpecLink = columnsCard2.getSpecLink();
    if (isAttrValueDiffered(card1SpecLink, card2SpecLink)) {
      pidcRevVarCmpResult.setSpecLinkDiff(true);
      this.attrLevelDiffFlag = true;
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
      final PidcRevisionVariantCompareResult pidcRevVarCmpResult) {
    final String card1Desc = columnsCard1.getDesc();
    final String card2Desc = columnsCard2.getDesc();
    if (isAttrValueDiffered(card1Desc, card2Desc)) {
      pidcRevVarCmpResult.setDescDiff(true);
      this.attrLevelDiffFlag = true;
    }
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
      this.varLevelDiffFlag = true;
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
      this.varLevelDiffFlag = true;
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
      this.varLevelDiffFlag = true;
    }
  }
}
