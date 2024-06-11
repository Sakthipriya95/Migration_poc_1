package com.bosch.caltool.apic.ui.views.providers;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.ImagePainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.CellPainterDecorator;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.CellStyleUtil;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.ui.util.CellEdgeEnum;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.graphics.Image;

import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixColorCode;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;

/**
 * TODO:// Extract methods
 *
 * @author dmo5cob
 */
public class FocusMatrixEditConfiguration extends AbstractRegistryConfiguration {


  /**
   * String constant for mapped from focus matrix
   */
  private static final String MAPPED_FROM_FOCUS_MATRIX = "MAPPED_FROM_FOCUS_MATRIX";
  /**
   * String constant for usecase proposal
   */
  private static final String USECASE_PROPOSAL = "USECASE_PROPOSAL";

  @Override
  public void configureRegistry(final IConfigRegistry configRegistry) {

    // configure the validation error style
    IStyle validationErrorStyle;
    setCellStyleForBgColor(configRegistry);

    validationErrorStyle = new Style();
    validationErrorStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR,
        com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils.getFocusMatrixColor(FocusMatrixColorCode.NOT_DEFINED));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.NORMAL,
        "NONE");

    // ICDM-1629
    validationErrorStyle = new Style();
    validationErrorStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_GRAY);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.NORMAL,
        "INVISIBLE");

    validationErrorStyle = new Style();
    validationErrorStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_DARK_GRAY);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.NORMAL,
        "NOT_FM_RELEVANT");


    validationErrorStyle = new Style();
    validationErrorStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);


    final Image commentImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.COMMENTS_TAG_16X16);
    final Image bagImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.UC_PROPOSAL_ICON_16X16);

    final CellPainterDecorator cellCommentDecorator =
        new CellPainterDecorator(new TextPainter(), CellEdgeEnum.TOP_RIGHT, 2, new ImagePainter(commentImage) {

          @Override
          protected Image getImage(final ILayerCell cell, final IConfigRegistry configRegistry2) {
            List<String> labels = cell.getConfigLabels().getLabels();
            if (labels.contains("HIDDEN")) {
              final Image hiddenImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.RED_CROSS_ICON_12X12);
              return hiddenImage;
            }
            else if (labels.contains("COMMENT")) {
              // ICDM-2427
              if (labels.contains(USECASE_PROPOSAL)) {
                final Image bagCommentDecoratorImage =
                    ImageManager.getInstance().getRegisteredImage(ImageKeys.UC_PROPOSAL_COMMENT_ICON_32X16);
                return bagCommentDecoratorImage;
              }
              return commentImage;
            }
            else if (labels.contains(MAPPED_FROM_FOCUS_MATRIX) && labels.contains(USECASE_PROPOSAL)) {
              // ICDM-2427
              return bagImage;
            }
            return CellStyleUtil.getCellStyle(cell, configRegistry2).getAttributeValue(CellStyleAttributes.IMAGE);
          }

        }, true);


    final Image linkImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.LINK_DECORATOR_12X12);
    final CellPainterDecorator cellLinkDecorator =
        new CellPainterDecorator(cellCommentDecorator, CellEdgeEnum.TOP_LEFT, 2, new ImagePainter(linkImage) {

          @Override
          protected Image getImage(final ILayerCell cell, final IConfigRegistry configRegistry2) {
            LabelStack configLabels = cell.getConfigLabels();
            if (configLabels.getLabels().contains("LINK")) {
              return linkImage;
            }
            return CellStyleUtil.getCellStyle(cell, configRegistry2).getAttributeValue(CellStyleAttributes.IMAGE);
          }

        }, false);


    CellPainterDecorator bagDecorator =
        new CellPainterDecorator(cellLinkDecorator, CellEdgeEnum.TOP, new ImagePainter(bagImage), false);

    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, bagDecorator, DisplayMode.NORMAL,
        USECASE_PROPOSAL);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.NORMAL,
        USECASE_PROPOSAL);


    Image remarksImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.COMMENTS_TAG_16X16);
    CellPainterDecorator remarkDecorator =
        new CellPainterDecorator(cellLinkDecorator, CellEdgeEnum.TOP, new ImagePainter(remarksImage), false);

    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, remarkDecorator, DisplayMode.NORMAL,
        "ATTR_REMARKS");
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.NORMAL,
        "ATTR_REMARKS");

    final Image tickImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.TICK_COLUMN_16X16);

    CellPainterDecorator tickDecorator =
        new CellPainterDecorator(cellLinkDecorator, CellEdgeEnum.TOP, new ImagePainter(tickImage), false);

    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, tickDecorator, DisplayMode.NORMAL,
        MAPPED_FROM_FOCUS_MATRIX);

    validationErrorStyle = new Style();
    validationErrorStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.NORMAL,
        MAPPED_FROM_FOCUS_MATRIX);

  }

  /**
   * @param configRegistry IConfigRegistry
   * @param validationErrorStyle IStyle
   */
  private void setCellStyleForBgColor(final IConfigRegistry configRegistry) {
    IStyle validationErrorStyle = new Style();
    validationErrorStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR,
        com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils.getFocusMatrixColor(FocusMatrixColorCode.RED));// red
    validationErrorStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_BLACK);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.NORMAL,
        "RED");
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.SELECT,
        "RED");

    validationErrorStyle = new Style();
    validationErrorStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR,
        com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils.getFocusMatrixColor(FocusMatrixColorCode.GREEN));// green
    validationErrorStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_BLACK);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.NORMAL,
        "GRN");
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.SELECT,
        "GRN");

    validationErrorStyle = new Style();
    validationErrorStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR,
        com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils.getFocusMatrixColor(FocusMatrixColorCode.ORANGE));// orange
    validationErrorStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_BLACK);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.NORMAL,
        "ORG");// icdm-1670
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.SELECT,
        "ORG");

    validationErrorStyle = new Style();
    validationErrorStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR,
        com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils.getFocusMatrixColor(FocusMatrixColorCode.YELLOW));// yellow
    validationErrorStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_BLACK);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.NORMAL,
        "YLW");
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.SELECT,
        "YLW");
  }
}