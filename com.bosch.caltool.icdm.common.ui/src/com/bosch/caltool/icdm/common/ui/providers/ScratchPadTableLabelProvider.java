package com.bosch.caltool.icdm.common.ui.providers;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.bosch.calmodel.caldataphy.CalDataPhyAscii;
import com.bosch.calmodel.caldataphy.CalDataPhyAxisPts;
import com.bosch.calmodel.caldataphy.CalDataPhyBoolean;
import com.bosch.calmodel.caldataphy.CalDataPhyCurve;
import com.bosch.calmodel.caldataphy.CalDataPhyMap;
import com.bosch.calmodel.caldataphy.CalDataPhyValBlk;
import com.bosch.calmodel.caldataphy.CalDataPhyValue;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.ParameterType;


/**
 * @author dmo5cob
 */
public class ScratchPadTableLabelProvider implements ITableLabelProvider {

  /**
   *
   */
  private static final String STR_COLON = " :: ";


  @Override
  public void addListener(final ILabelProviderListener listener) {
    // TO-DO
  }

  @Override
  public void dispose() {
    // TO-DO
  }

  @Override
  public boolean isLabelProperty(final Object element, final String property) {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeListener(final ILabelProviderListener listener) {
    // TO-DO
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getColumnText(final Object element, final int columnIndex) {

    String result = "";
    if (element instanceof ScratchPadDataFetcher) {
      ScratchPadDataFetcher data = (ScratchPadDataFetcher) element;

      switch (columnIndex) {

        case 1:
          result = caseColOne(result, data);
          break;
        case 0:
        default:
          result = "";
          break;
      }
    }
    return result;

  }

  /**
   * @param result
   * @param data
   * @return
   */
  private String caseColOne(String result, final ScratchPadDataFetcher data) {
    if (CommonUtils.isNotNull(data.getParameter())) {
      return data.getParameter().getName();
    }
    if (CommonUtils.isNotNull(data.getPidcA2l())) {
      return data.getPidcA2l().getName();
    }
    else if (CommonUtils.isNotNull(data.getFunction())) {
      return data.getFunction().getName();
    }
    // ICDM-226
    else if (CommonUtils.isNotNull(data.getSeriesStatsInfo())) {
      String dataSetName = data.getSeriesStatsInfo().getDataSetName();

      result = CommonUtils.concatenate(result, data.getSeriesStatsInfo().getCalData().getShortName(), STR_COLON,
          data.getSeriesStatsInfo().getCalDataPhyValType().getLabel(), STR_COLON,
          data.getSeriesStatsInfo().getCalData().getCalDataPhy().getSimpleDisplayValue());
      if (!CommonUtils.isEmptyString(dataSetName)) {
        result = CommonUtils.concatenate(result, STR_COLON, "(", dataSetName, ")");
      }
    }
    else if (CommonUtils.isNotNull(data.getPidcVersion())) {
      return data.getPidcVersion().getName();
    }
    else if (CommonUtils.isNotNull(data.getPidcVariant())) {
      return data.getPidcVariant().getName() + STR_COLON + data.getPidcVariant().getName();
    }
    else if (CommonUtils.isNotNull(data.getPidcSubVariant())) {
      return data.getPidcSubVariant().getName() + STR_COLON + data.getPidcSubVariant().getName() + STR_COLON +
          data.getPidcSubVariant().getName();

    } // ICDM-254
    else if (CommonUtils.isNotNull(data.getAttrVal())) {
      return data.getAttrVal().getName();
    }
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Image getColumnImage(final Object element, final int columnIndex) {
    Image image = null;
    if (element instanceof ScratchPadDataFetcher) {
      final ScratchPadDataFetcher data = (ScratchPadDataFetcher) element;

      if (CommonUtils.isNotNull(data.getPidcA2l())) {
        image = CommonUiUtils.getInstance().getImageForA2lFile(data.getPidcA2l());
      }
      if (CommonUtils.isNotNull(data.getParameter())) {
        image = getImageForFuncParam(data.getParameter());
      }
      else if (CommonUtils.isNotNull(data.getFunction())) {
        image = ImageManager.getInstance().getRegisteredImage(ImageKeys.FUNCTION_28X30);
      }
      // ICDM-226
      else if ((CommonUtils.isNotNull(data.getSeriesStatsInfo())) &&
          (data.getSeriesStatsInfo().getCalData().getCalDataPhy() instanceof CalDataPhyValue)) {
        image = ImageManager.getInstance().getRegisteredImage(ImageKeys.VALUE_16X16);
      }
      else if ((CommonUtils.isNotNull(data.getSeriesStatsInfo())) &&
          (data.getSeriesStatsInfo().getCalData().getCalDataPhy() instanceof CalDataPhyValBlk)) {
        image = ImageManager.getInstance().getRegisteredImage(ImageKeys.VALBLK_16X16);
      }
      else if ((CommonUtils.isNotNull(data.getSeriesStatsInfo())) &&
          (data.getSeriesStatsInfo().getCalData().getCalDataPhy() instanceof CalDataPhyAscii)) {
        image = ImageManager.getInstance().getRegisteredImage(ImageKeys.ASCII_16X16);
      }
      else if ((CommonUtils.isNotNull(data.getSeriesStatsInfo())) &&
          (data.getSeriesStatsInfo().getCalData().getCalDataPhy() instanceof CalDataPhyAxisPts)) {
        image = ImageManager.getInstance().getRegisteredImage(ImageKeys.AXIS_16X16);
      }
      else if ((CommonUtils.isNotNull(data.getSeriesStatsInfo())) &&
          (data.getSeriesStatsInfo().getCalData().getCalDataPhy() instanceof CalDataPhyCurve)) {
        image = ImageManager.getInstance().getRegisteredImage(ImageKeys.CURVE_16X16);
      }
      else if ((CommonUtils.isNotNull(data.getSeriesStatsInfo())) &&
          (data.getSeriesStatsInfo().getCalData().getCalDataPhy() instanceof CalDataPhyMap)) {
        image = ImageManager.getInstance().getRegisteredImage(ImageKeys.MAP_16X16);
      }
      else if ((CommonUtils.isNotNull(data.getSeriesStatsInfo())) &&
          (data.getSeriesStatsInfo().getCalData().getCalDataPhy() instanceof CalDataPhyBoolean)) {
        image = ImageManager.getInstance().getRegisteredImage(ImageKeys.BOOLEAN_16X16);
      }
      else if (CommonUtils.isNotNull(data.getPidcVersion())) {
        image = ImageManager.getInstance().getRegisteredImage(ImageKeys.PIDC_16X16);
      }
      else if (CommonUtils.isNotNull(data.getPidcVariant())) {
        image = ImageManager.getInstance().getRegisteredImage(ImageKeys.VARIANT_28X30);
      }
      else if (CommonUtils.isNotNull(data.getPidcSubVariant())) {
        image = ImageManager.getInstance().getRegisteredImage(ImageKeys.SUBVAR_28X30);
      }
      else if (CommonUtils.isNotNull(data.getAttrVal())) {
        image = ImageManager.getInstance().getRegisteredImage(ImageKeys.ATTRVAL_16X16);
      }

    }

    switch (columnIndex) {
      case 0:
        return image;
      case 1:
      default:
        return null;
    }
  }

  /**
   * @param cdrFuncParameter
   * @return
   */
  private Image getImageForFuncParam(final Parameter cdrFuncParameter) {
    Image image = null;

    // VAL_BLK
    // CURVE
    // AXIS_PTS
    // VALUE
    // ASCII
    // MAP
    if (cdrFuncParameter.getType().equals(ParameterType.VAL_BLK.getText())) {
      image = ImageManager.getInstance().getRegisteredImage(ImageKeys.VALBLK_16X16);
    }
    else if (cdrFuncParameter.getType().equals(ParameterType.CURVE.getText())) {
      image = ImageManager.getInstance().getRegisteredImage(ImageKeys.CURVE_16X16);
    }
    else if (cdrFuncParameter.getType().equals(ParameterType.AXIS_PTS.getText())) {
      image = ImageManager.getInstance().getRegisteredImage(ImageKeys.AXIS_16X16);
    }
    else if (cdrFuncParameter.getType().equals(ParameterType.VALUE.getText())) {
      image = ImageManager.getInstance().getRegisteredImage(ImageKeys.VALUE_16X16);
    }
    else if (cdrFuncParameter.getType().equals(ParameterType.ASCII.getText())) {
      image = ImageManager.getInstance().getRegisteredImage(ImageKeys.ASCII_16X16);
    }
    else if (cdrFuncParameter.getType().equals(ParameterType.MAP.getText())) {
      image = ImageManager.getInstance().getRegisteredImage(ImageKeys.MAP_16X16);
    }

    return image;
  }
}
