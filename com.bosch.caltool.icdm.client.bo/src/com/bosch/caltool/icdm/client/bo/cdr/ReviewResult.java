/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import java.io.IOException;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldataphy.CalDataAxis;
import com.bosch.calmodel.caldataphy.CalDataPhy;
import com.bosch.calmodel.caldataphy.CalDataPhyCurve;
import com.bosch.calmodel.caldataphy.CalDataPhyMap;
import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.ReviewResultType;


/**
 * @author rgo7cob
 */
public class ReviewResult implements Comparable<ReviewResult> {

  private final ReviewResultType wsResponse;
  private CalData checkedValue = null;
  private final Set<ReviewDetail> reviewDetails = new HashSet<>();

  public ReviewResult(final ReviewResultType type) {
    this.wsResponse = type;
    extractCalData();
    addReviewDetail(type);
  }


  public void addReviewDetail(final ReviewResultType wsResponse) {
    this.reviewDetails.add(new ReviewDetail(wsResponse, this.wsResponse.getParamName()));
  }

  public void addReviewDetail(final ReviewDetail result) {
    this.reviewDetails.add(result);
  }

  public void addReviewDetails(final Set<ReviewDetail> reviewDetails) {
    this.reviewDetails.addAll(reviewDetails);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final ReviewResult result) {
    return getNumberOfRecords() - result.getNumberOfRecords();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }

    ReviewResult other = (ReviewResult) obj;
    if ((this.checkedValue == null) && (other.getCheckedValue() == null)) {
      return true;
    }

    if ((this.checkedValue == null) || (other.getCheckedValue() == null)) {
      return false;
    }

    // This and the other object have to be checked in case
    // two parameters that have not the same type will be compared
    if ((this.checkedValue.getCalDataPhy() instanceof CalDataPhyCurve) &&
        (other.checkedValue.getCalDataPhy() instanceof CalDataPhyCurve)) {
      return equalsCurve((CalDataPhyCurve) this.checkedValue.getCalDataPhy(),
          (CalDataPhyCurve) other.checkedValue.getCalDataPhy());
    }

    if ((this.checkedValue.getCalDataPhy() instanceof CalDataPhyMap) &&
        (other.checkedValue.getCalDataPhy() instanceof CalDataPhyMap)) {
      return equalsMap((CalDataPhyMap) this.checkedValue.getCalDataPhy(),
          (CalDataPhyMap) other.checkedValue.getCalDataPhy());
    }

    return this.checkedValue.getCalDataPhy().equals(other.getCheckedValue().getCalDataPhy());
  }

  private boolean equalsMap(final CalDataPhyMap map1, final CalDataPhyMap map2) {
    BitSet result = map1.equalsExt(map2);
    if (validateBits(result) || result.get(CalDataPhy.atomicValuePhyBit)) {
      return false;
    }

    // Check the axis separate. All axis characteristics but the axis type are checked
    return equalsAxis(map1.getCalDataAxisX(), map1.getCalDataAxisX()) &&
        equalsAxis(map1.getCalDataAxisY(), map1.getCalDataAxisY());
  }


  /**
   * @param result
   * @return
   */
  private boolean validateBits(BitSet result) {
    return result.get(CalDataPhy.unitBit) || result.get(CalDataPhy.typeBit) || result.get(CalDataPhy.nameBit) ||
            result.get(CalDataPhy.isTextBit);
  }

  private boolean equalsCurve(final CalDataPhyCurve curve1, final CalDataPhyCurve curve2) {
    BitSet result = curve1.equalsExt(curve2);
    if (validateBits(result) || result.get(CalDataPhy.atomicValuePhyBit)) {
      return false;
    }

    // Check the axis separate. All axis characteristics but the axis type are checked
    return equalsAxis(curve1.getCalDataAxisX(), curve2.getCalDataAxisX());
  }

  private boolean equalsAxis(final CalDataAxis axis1, final CalDataAxis axis2) {
    // The equalsExt method fails, if the axsPtsRef is null.
    // That's why a dummy must be assigned if the axisPtsRef value is null
    String axisPtsRef = new String();

    if (axis1.getAxisPtsRef() == null) {
      axis1.setAxisPtsRef(axisPtsRef);
    }

    if (axis2.getAxisPtsRef() == null) {
      axis2.setAxisPtsRef(axisPtsRef);
    }

    BitSet result = axis1.equalsExt(axis2);

    // The Bit CalDataPhy.axisType
    if (result.get(CalDataPhy.unitBit) || result.get(CalDataPhy.isTextBit) || result.get(CalDataPhy.noOfAxisPtsBit) ||
        result.get(CalDataPhy.atomicValuePhyBit)) {
      return false;
    }

    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  private void extractCalData() {
    byte[] checkValue = this.wsResponse.getCheckValue();

    try {
      if (checkValue != null) {
        this.checkedValue = CalDataUtil.getCalDataObj(checkValue);

      }
    }
    catch (IOException | ClassNotFoundException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }


  }

  public CalData getCheckedValue() {
    return this.checkedValue;
  }

  public String getCheckedValueString() {
    return this.wsResponse.getCheckValStr();
  }

  public String getUnit() {
    return this.wsResponse.getUnit() == null ? "" : this.wsResponse.getUnit();
  }

  public int getNumberOfRecords() {
    return this.reviewDetails.size();
  }

  public Set<ReviewDetail> getReviewDetails() {
    return this.reviewDetails;
  }

}
