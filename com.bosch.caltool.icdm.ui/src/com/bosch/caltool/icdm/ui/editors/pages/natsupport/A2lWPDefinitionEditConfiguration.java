package com.bosch.caltool.icdm.ui.editors.pages.natsupport;

import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ImagePainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.BeveledBorderDecorator;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.CellPainterDecorator;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.CellStyleUtil;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.style.VerticalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.ui.util.CellEdgeEnum;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;

import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.ui.util.IUIConstants;


/**
 * NAT page Style configuration class
 *
 * @author pdh2cob
 */
public class A2lWPDefinitionEditConfiguration extends AbstractRegistryConfiguration {

  @Override
  public void configureRegistry(final IConfigRegistry configRegistry) {

    // configure the validation error style
    IStyle validationErrorStyle = new Style();
    validationErrorStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WIDGET_BACKGROUND);
    validationErrorStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_WIDGET_FOREGROUND);
    validationErrorStyle.setAttributeValue(CellStyleAttributes.GRADIENT_BACKGROUND_COLOR, GUIHelper.COLOR_WHITE);
    validationErrorStyle.setAttributeValue(CellStyleAttributes.GRADIENT_FOREGROUND_COLOR,
        GUIHelper.getColor(136, 212, 215));
    validationErrorStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
    validationErrorStyle.setAttributeValue(CellStyleAttributes.VERTICAL_ALIGNMENT, VerticalAlignmentEnum.MIDDLE);
    validationErrorStyle.setAttributeValue(CellStyleAttributes.BORDER_STYLE, null);
    validationErrorStyle.setAttributeValue(CellStyleAttributes.FONT,
        GUIHelper.getFont(new FontData("Segoe UI", 9, SWT.NONE)));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.NORMAL,
        "DUMMY_HEADER");


    // configuration for selection in a2l details view
    IStyle varGrpSelectionStyle = new Style();
    varGrpSelectionStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
    varGrpSelectionStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_DARK_GRAY);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, varGrpSelectionStyle, DisplayMode.NORMAL,
        IUIConstants.CONFIG_LABEL_VAR_GRP_NOT_MAPPED);

    // configuration for default workpackage in a2l view
    IStyle defaultWpStyle = new Style();
    defaultWpStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
    defaultWpStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_DARK_GRAY);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, defaultWpStyle, DisplayMode.NORMAL,
        IUIConstants.CONFIG_LABEL_DEFAULT_WORK_PACKAGE_DISABLED);


    // Style for deleted responsibles
    IStyle deletedRespStyle = new Style();
    deletedRespStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
    deletedRespStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_RED);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, deletedRespStyle, DisplayMode.NORMAL,
        IUIConstants.CONFIG_LABEL_DEL_WP_RESPONSIBLE);


    ICellPainter valueHeaderCellPainter = new BeveledBorderDecorator(new TextPainter());

    // configure the painter
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, valueHeaderCellPainter,
        DisplayMode.NORMAL, "DUMMY_HEADER");

    final Image linkImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.WP_DEFN_USER_RESPONSIBLE);

    final Image disableRespEditImage =
        ImageManager.getInstance().getRegisteredImage(ImageKeys.WP_DEFN_USER_RESPONSIBLE_DISABLED);

    CellPainterDecorator decorator =
        new CellPainterDecorator(new TextPainter(), CellEdgeEnum.TOP, 2, new ImagePainter(linkImage) {

          @Override
          protected Image getImage(final ILayerCell cell, final IConfigRegistry configRegistry2) {
            LabelStack configLabels = cell.getConfigLabels();
            if (configLabels.getLabels().contains(IUIConstants.CONFIG_LABEL_SET_RESPONSIBLE_DISABLED)) {
              return disableRespEditImage;
            }
            if (configLabels.getLabels().contains(IUIConstants.CONFIG_LABEL_SET_RESPONSIBLE)) {
              return linkImage;
            }

            return CellStyleUtil.getCellStyle(cell, configRegistry2).getAttributeValue(CellStyleAttributes.IMAGE);
          }

        }, false);

    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, decorator, DisplayMode.NORMAL,
        IUIConstants.CONFIG_LABEL_SET_RESPONSIBLE);

  }

}
