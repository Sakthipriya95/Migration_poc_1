/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.comphex;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.caldatautils.CalDataComparism;
import com.bosch.calcomp.caldatautils.CalDataComparism.CompareResult;
import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldataphy.CalDataPhy;
import com.bosch.caltool.icdm.bo.util.CalDataComparisonWrapper;

/**
 * @author IMI2SI
 */
public class HexWithCdfxComparison {


  private final A2LFileInfo a2lFileInfo;
  private final ConcurrentMap<String, CalData> calDataObjectsFromHex;
  private final Map<String, CalData> calDataObjectsFromCDFx;
  private final HexWithCdfxCompareResult compareList = new HexWithCdfxCompareResult();
  private final ILoggerAdapter logger;

  /**
   * @param a2lFileInfo
   * @param calDataObjectsFromHex
   * @param calDataObjectsFromCDFx
   * @param logger
   * @return
   */
  public static HexWithCdfxCompareResult compare(final A2LFileInfo a2lFileInfo,
      final ConcurrentMap<String, CalData> calDataObjectsFromHex, final Map<String, CalData> calDataObjectsFromCDFx,
      final ILoggerAdapter logger) {

    HexWithCdfxComparison comparison =
        new HexWithCdfxComparison(a2lFileInfo, calDataObjectsFromHex, calDataObjectsFromCDFx, logger);
    return comparison.compareLabelsQuantized();
  }

  /**
   * @param a2lFileInfo
   * @param calDataObjectsFromHex
   * @param calDataObjectsFromCDFx
   */
  public HexWithCdfxComparison(final A2LFileInfo a2lFileInfo,
      final ConcurrentMap<String, CalData> calDataObjectsFromHex, final Map<String, CalData> calDataObjectsFromCDFx,
      final ILoggerAdapter logger) {
    super();
    this.a2lFileInfo = a2lFileInfo;
    this.calDataObjectsFromHex = calDataObjectsFromHex;
    this.calDataObjectsFromCDFx = calDataObjectsFromCDFx;
    this.logger = logger;
  }

  private HexWithCdfxCompareResult compareLabelsQuantized() {

    for (Entry<String, Characteristic> a2lCharacteristicEntrySet : this.a2lFileInfo.getAllModulesLabels().entrySet()) {
      String labelName = a2lCharacteristicEntrySet.getKey();

      try {
        Characteristic a2lCharacteristic = a2lCharacteristicEntrySet.getValue();
        CalDataPhy hexCalDataPhy = getHexCalDataPhyForLabel(labelName);
        CalDataPhy cdfxCalDataPhy = getCdfxCalDataPhyForLabel(labelName);

        if (!a2lCharacteristic.isInCalMemory()) {
          continue;
        }

        CalDataComparism labelCompareResult =
            new CalDataComparisonWrapper(a2lCharacteristic).compare(hexCalDataPhy, cdfxCalDataPhy);
        addLabelCompResultToOverallResult(labelCompareResult, labelName);
      }
      catch (Exception e) {
        String error = String.format("HexWithCdfxComparison quantized: Error when comparing parameter %s", labelName);
        this.logger.error(error, e);
        throw new HexWithCDFXCompException(error, e);
      }
    }

    return this.compareList;
  }

  /**
   * @param labelCompareResult
   */
  private void addLabelCompResultToOverallResult(final CalDataComparism labelCompareResult, final String labelName) {
    this.compareList.addCompareResult(labelName, labelCompareResult);

    if (isCompareResultNotEqual(labelCompareResult)) {
      if (labelCompareResult.getCompareResult() == CompareResult.DIFF_UNITS) {
        this.logger.info("Diff Units at parameter: " + labelName);
      }
      if (labelCompareResult.getCompareResult() == CompareResult.DIFF_CHARACTERISTIC_TYPES) {
        this.logger.info("Diff Characteristics at parameter: " + labelName);
      }
      this.compareList.getUnequalPar().add(labelName);
    }
    else if (isCompareResultEqual(labelCompareResult)) {
      long equalParCount = this.compareList.getEqualParCount();
      equalParCount++;
      this.compareList.setEqualParCount(equalParCount);
    }
    else {
      throw new HexWithCDFXCompException(
          String.format("Quantized Comparison of parameter could not be done. Result from Comparison Operation: %s",
              labelCompareResult.toString()));
    }
  }

  private boolean isCompareResultEqual(final CalDataComparism labelCompareResult) {
    return labelCompareResult.getCompareResult() == CompareResult.EQUAL;
  }

  private boolean isCompareResultNotEqual(final CalDataComparism labelCompareResult) {
    return (labelCompareResult.getCompareResult() == CompareResult.NOT_EQUAL) ||
        (labelCompareResult.getCompareResult() == CompareResult.DIFF_UNITS) ||
        (labelCompareResult.getCompareResult() == CompareResult.DIFF_CHARACTERISTIC_TYPES);
  }

  private CalDataPhy getHexCalDataPhyForLabel(final String labelName) {
    if (this.calDataObjectsFromHex.get(labelName) == null) {
      return null;
    }
    else {
      return this.calDataObjectsFromHex.get(labelName).getCalDataPhy();
    }
  }

  private CalDataPhy getCdfxCalDataPhyForLabel(final String labelName) {
    if (this.calDataObjectsFromCDFx.get(labelName) == null) {
      return null;
    }
    else {
      return this.calDataObjectsFromCDFx.get(labelName).getCalDataPhy();
    }
  }
}
