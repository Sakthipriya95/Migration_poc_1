/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.rcputils.griddata;

import org.eclipse.swt.layout.GridData;


/**
 * @author mga1cob
 */
public final class GridDataUtil {

  /**
   * GridDataUtil instance
   */
  private static GridDataUtil gridDataUtil;

  /**
   * The default constructor
   */
  private GridDataUtil() {
    // This is private constructor
  }

  /**
   * This method returns GridDataUtil instance
   * 
   * @return GridDataUtil
   */
  public static GridDataUtil getInstance() {
    if (gridDataUtil == null) {
      gridDataUtil = new GridDataUtil();
    }
    return gridDataUtil;
  }


  /**
   * This method returns GridData object
   * 
   * @return GridData
   */
  public GridData getGridData() {
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    return gridData;
  }

  /**
   * This method returns text GridData object It will applys the style with horizontally stretching the text filed and
   * aligning the it vertically center
   * 
   * @return GridData
   */
  public GridData getTextGridData() {
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.verticalAlignment = GridData.CENTER;
    return gridData;
  }

  /**
   * @param noOfSpans defines number of spans
   * @return GridData instance
   */
  public GridData getHorizontalSpanGridData(final int noOfSpans) {
    final GridData gridData = getGridData();
    gridData.horizontalSpan = noOfSpans;
    return gridData;
  }


  /**
   * @param noOfSpans defines number of spans
   * @return GridData instance
   */
  public GridData getHoriSpanVAlignCenterGridData(final int noOfSpans) {
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalSpan = noOfSpans;
    gridData.verticalAlignment = GridData.CENTER;
    return gridData;
  }

  /**
   * @param hAlignment defines horizontal alignment
   * @param hSpace defines to grab excess horizontal space
   * @param vSpace defines to grab excess vertical space
   * @param hSpan defines number of horizontal spans
   * @param vAlignment defines vertical alignment
   * @return GridData
   */
  public GridData createGridData(final int hAlignment, final boolean hSpace, final boolean vSpace, final int hSpan,
      final int vAlignment) {
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = hAlignment;
    gridData.grabExcessHorizontalSpace = hSpace;
    gridData.grabExcessVerticalSpace = vSpace;
    gridData.horizontalSpan = hSpan;
    gridData.verticalAlignment = vAlignment;
    return gridData;
  }

  /**
   * @param hAlignment defines horizontal alignment
   * @param hSpace defines to grab excess horizontal space
   * @param vSpace defines to grab excess vertical space
   * @param hSpan defines number of horizontal spans
   * @param vAlignment defines vertical alignment
   * @param vSpan defines vertical span
   * @return GridData
   */
  public GridData createGridData(final int hAlignment, final boolean hSpace, final boolean vSpace, final int hSpan,
      final int vAlignment, final int vSpan) {
    final GridData gridData = new GridData();
    gridData.horizontalSpan = hSpan;
    gridData.horizontalAlignment = hAlignment;
    gridData.verticalAlignment = vAlignment;
    gridData.grabExcessHorizontalSpace = hSpace;
    gridData.grabExcessVerticalSpace = vSpace;
    gridData.verticalSpan = vSpan;
    return gridData;
  }

  /**
   * @param hAlignment defines horizontal alignment
   * @param hSpace defines to grab excess horizontal space
   * @param vSpace defines to grab excess vertical space
   * @param hSpan defines number of horizontal spans
   * @param heightHint defines heightHint
   * @param vSpan defines vertical span
   * @return GridData
   */
  public GridData createHeightHintGridData(final int hAlignment, final boolean hSpace, final boolean vSpace,
      final int hSpan, final int heightHint, final int vSpan) {
    final GridData gridData = new GridData();
    gridData.horizontalSpan = hSpan;
    gridData.horizontalAlignment = hAlignment;
    gridData.heightHint = heightHint;
    gridData.grabExcessHorizontalSpace = hSpace;
    gridData.grabExcessVerticalSpace = vSpace;
    gridData.verticalSpan = vSpan;
    return gridData;
  }

  /**
   * @param heightHint defines SWT control height
   * @return GridData instance
   */
  public GridData getHeightHintGridData(final int heightHint) {
    final GridData gridData = getGridData();
    gridData.heightHint = heightHint;
    return gridData;
  }

  /**
   * @param widthHint defines SWT control width
   * @return GridData instance
   */
  public GridData getWidthHintGridData(final int widthHint) {
    final GridData gridData = new GridData();
    gridData.widthHint = widthHint;
    return gridData;
  }

  /**
   * This method returns GridData instance
   * 
   * @param widthHint defines width
   * @param heightHint defines height
   * @return GridData instance
   */
  public GridData createGridData(final int widthHint, final int heightHint) {
    final GridData grpGridDtata = new GridData();
    grpGridDtata.widthHint = widthHint;
    grpGridDtata.heightHint = heightHint;
    return grpGridDtata;
  }

  /**
   * This method returns GridData instance
   * 
   * @param widthHint defines width
   * @param hAlign defines horizontal alignment
   * @param vAlign defines vertical alignment
   * @return GridData instance
   */
  public GridData createGridData(final int widthHint, final int hAlign, final int vAlign) {
    final GridData layoutData = new GridData();
    layoutData.widthHint = widthHint;
    layoutData.horizontalAlignment = hAlign;
    layoutData.verticalAlignment = vAlign;
    return layoutData;
  }

  /**
   * This method returns GridData instance
   * 
   * @param widthHint defines width
   * @param hAlign defines horizontal alignment
   * @param hSpan defines
   * @param vAlign defines vertical alignment
   * @return GridData instance
   */
  public GridData createGridData(final int widthHint, final int hSpan, final int hAlign, final int vAlign) {
    final GridData layoutData = new GridData();
    layoutData.widthHint = widthHint;
    layoutData.horizontalSpan = hSpan;
    layoutData.horizontalAlignment = hAlign;
    layoutData.verticalAlignment = vAlign;
    return layoutData;
  }

  /**
   * @return GridData instance
   */
  public GridData createGridData() {
    final GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.verticalAlignment = GridData.CENTER;
    gridData.grabExcessVerticalSpace = true;
    return gridData;
  }


}
