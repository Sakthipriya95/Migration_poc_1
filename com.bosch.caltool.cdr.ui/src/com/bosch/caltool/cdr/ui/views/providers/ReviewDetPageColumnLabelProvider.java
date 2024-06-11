/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.views.providers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.ParameterType;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;

/**
 * @author say8cob
 */
public class ReviewDetPageColumnLabelProvider extends ColumnLabelProvider {

  /**
   * Q-SSD constant
   */
  private static final String QSSD_STR = "Qssd";

  /**
   * blacklist constant
   */
  private static final String BLACKLIST_STR = "Blacklist";

  /**
   * read only parameter constant
   */
  private static final String READ_ONLY_STR = "Read-only";

  /**
   * dependent parameter constant
   */
  private static final String DEP_PARAM_STR = "Dependent";

  /**
   * compli constant
   */
  private static final String COMPLI_STR = "Compli";

  final ReviewResultClientBO resultData;

  /**
   * Column Index
   */
  private final int columnIndex;

  /**
   * key - string combination for the multiple image, value - Image
   */
  private final Map<String, Image> multiImageMap = new ConcurrentHashMap<>();

  /**
   * @param resultData BO
   * @param columnIndex Columnindex
   */
  public ReviewDetPageColumnLabelProvider(final ReviewResultClientBO resultData, final int columnIndex) {
    this.resultData = resultData;
    this.columnIndex = columnIndex;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Image getImage(final Object element) {
    Image img = null;
    // ICDM-2439
    if ((element instanceof CDRResultParameter) && (this.columnIndex == 1)) {
      final CDRResultParameter funcParam = (CDRResultParameter) element;
      if (ParameterType.MAP.getText().equalsIgnoreCase(funcParam.getpType())) {
        img = ImageManager.getInstance().getRegisteredImage(ImageKeys.MAP_16X16);
      }
      else if (ParameterType.CURVE.getText().equalsIgnoreCase(funcParam.getpType())) {
        img = ImageManager.getInstance().getRegisteredImage(ImageKeys.CURVE_16X16);
      }
      else if (ParameterType.VALUE.getText().equalsIgnoreCase(funcParam.getpType())) {
        img = ImageManager.getInstance().getRegisteredImage(ImageKeys.VALUE_16X16);
      }
      else if (ParameterType.ASCII.getText().equalsIgnoreCase(funcParam.getpType())) {
        img = ImageManager.getInstance().getRegisteredImage(ImageKeys.ASCII_16X16);
      }
      else if (ParameterType.VAL_BLK.getText().equalsIgnoreCase(funcParam.getpType())) {
        img = ImageManager.getInstance().getRegisteredImage(ImageKeys.VALBLK_16X16);
      }
      else if (ParameterType.AXIS_PTS.getText().equalsIgnoreCase(funcParam.getpType())) {
        img = ImageManager.getInstance().getRegisteredImage(ImageKeys.AXIS_16X16);
      }

    }

    // ICDM-2439
    if ((element instanceof CDRResultParameter) && (this.columnIndex == 0)) {
      return getImageForZerothColumnIndex(element);
    }
    return img;


  }


  /**
   * @param element
   * @return
   */
  private Image getImageForZerothColumnIndex(final Object element) {
    final CDRResultParameter param = (CDRResultParameter) element;
    String keyString = "";
    if (this.resultData.isComplianceParameter(param)) {
      keyString = keyString.concat(COMPLI_STR);
    }
    if (this.resultData.isReadOnly(param)) {
      keyString = keyString.concat(READ_ONLY_STR);
    }
    if (this.resultData.isBlackList(param)) {
      keyString = keyString.concat(BLACKLIST_STR);
    }
    if (this.resultData.isQssdParameter(param)) {
      keyString = keyString.concat(QSSD_STR);
    }
    if (this.resultData.isDependentParam(param)) {
      keyString = keyString.concat(DEP_PARAM_STR);
    }
    Image imageFromMap = this.multiImageMap.get(keyString);
    if (null == imageFromMap) {
      imageFromMap = createImage(param);
    }
    return imageFromMap;
  }


  /**
   * @param param
   * @return
   */
  private Image createImage(final CDRResultParameter param) {
    Image compositeImg = new Image(Display.getCurrent(), 48, 16);
    String keyString = "";
    int iconWidth = 0;
    GC gc = new GC(compositeImg);
    if (this.resultData.isComplianceParameter(param)) {
      keyString = keyString.concat(COMPLI_STR);
      gc.drawImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.PARAM_TYPE_COMPLIANCE_16X16), iconWidth, 0);
    }
    if (this.resultData.isReadOnly(param)) {
      keyString = keyString.concat(READ_ONLY_STR);
      iconWidth = iconWidth + 16;
      gc.drawImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.READ_ONLY_16X16), iconWidth, 0);
    }
    if (this.resultData.isBlackList(param)) {
      keyString = keyString.concat(BLACKLIST_STR);
      iconWidth = iconWidth + 16;
      gc.drawImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.BLACK_LIST_LABEL), iconWidth, 0);
    }
    if (this.resultData.isQssdParameter(param)) {
      keyString = keyString.concat(QSSD_STR);
      iconWidth = iconWidth + 16;
      gc.drawImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.QSSD_LABEL), iconWidth, 0);
    }
    if (this.resultData.isDependentParam(param)) {
      keyString = keyString.concat(DEP_PARAM_STR);
      iconWidth = iconWidth + 16;
      gc.drawImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.PARAM_DEPN_16X16), iconWidth, 0);
    }
    this.multiImageMap.put(keyString, compositeImg);
    return compositeImg;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getText(final Object element) {
    String result = "";
    if ((element instanceof CDRResultParameter) && (CommonUIConstants.COLUMN_INDEX_2 == this.columnIndex)) {
      result = this.resultData.getFunctionParameter((CDRResultParameter) element).getName();
    }
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Color getForeground(final Object element) {
    if ((element instanceof CDRResultParameter) && (CommonUIConstants.COLUMN_INDEX_2 == this.columnIndex)) {
      CDRResultParameter resParam = (CDRResultParameter) element;
      if (this.resultData.isReviewed(resParam)) {
        return Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN);
      }
      else if ((ApicUtil.compare(this.resultData.getResult(resParam), CDRConstants.RESULT_FLAG.OK.getUiType()) != 0)) {
        return Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED);
      }
    }
    return super.getForeground(element);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    super.dispose();
    for (Image image : this.multiImageMap.values()) {
      if (null != image) {
        image.dispose();
      }
    }
  }
}
