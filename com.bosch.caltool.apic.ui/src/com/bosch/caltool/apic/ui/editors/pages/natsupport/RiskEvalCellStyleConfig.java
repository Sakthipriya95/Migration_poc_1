/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages.natsupport;

import static org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes.CELL_PAINTER;
import static org.eclipse.nebula.widgets.nattable.style.DisplayMode.NORMAL;

import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultBooleanDisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.painter.cell.CheckBoxPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ComboBoxPainter;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.apic.ui.editors.pages.RiskEvalNatTableSection;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.model.rm.RmRiskLevel.RISK_LEVEL_CONFIG;


/**
 * Review score combo box edit configuration
 *
 * @author bru2cob
 */
public class RiskEvalCellStyleConfig extends AbstractRegistryConfiguration {

  /** The radio icon 0. */
  private final Image radioIcon_0;

  /** The radio icon 1. */
  private final Image radioIcon_1;

  /**
   * Color code for Risk level Medium
   */
  private Color colorMedium;

  /**
   * Color code for Risk level Low
   */
  private Color colorLow;

  /**
   * Color code for Risk level High
   */
  private Color colorHigh;
  /**
   * RB_SHARE_COMBO label
   */
  public static final String RB_SHARE_COMBO = "RB_SHARE_COMBO";
  /**
   * RB_INPUT_DATA_COMBO label
   */
  public static final String RB_INPUT_DATA_COMBO = "RB_INPUT_DATA_COMBO";

  static final String ROW_GREYED = "ROW_GREYED";

  /** Code : CHECK_BOX_CONFIG_LABEL. */
  public static final String CHECK_BOX_CONFIG_LABEL = "checkBox";

  /** Code : CHECK_BOX_EDITOR_CNG_LBL. */
  public static final String CHECK_BOX_EDITOR_CNG_LBL = "checkBoxEditor";

  /** Code : CUSTOM_COMPARATOR_LABEL. */
  public static final String CUSTOM_COMPARATOR_LABEL = "customComparatorLabel";

  /** code : DISABLED **/
  public final static String DISABLED = "_DISABLED";

  /**
   * @param riskHandler
   */
  public RiskEvalCellStyleConfig() {
    // Button style
    this.radioIcon_0 = ImageManager.getInstance().getRegisteredImage(ImageKeys.RADIO_UNCHECK);
    this.radioIcon_1 = ImageManager.getInstance().getRegisteredImage(ImageKeys.RADIO_CHECK);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void configureRegistry(final IConfigRegistry configRegistry) {

    // set the style for read only
    Style styleReadOnly = new Style();
    styleReadOnly.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.getColor(204, 198, 176));
    styleReadOnly.setAttributeValue(CellStyleAttributes.FONT, GUIHelper.DEFAULT_FONT);

    // risk level background
    FontData newFontData = new FontData("Segoe UI", 9, SWT.BOLD);
    Font selectedFont = GUIHelper.getFont(newFontData);

    // risk - high
    IStyle colorRed = new Style();
    Device device = Display.getCurrent();
    this.colorHigh = new Color(device, RISK_LEVEL_CONFIG.RISK_LVL_HIGH.getRed(),
        RISK_LEVEL_CONFIG.RISK_LVL_HIGH.getGreen(), RISK_LEVEL_CONFIG.RISK_LVL_HIGH.getBlue());
    colorRed.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, this.colorHigh);
    colorRed.setAttributeValue(CellStyleAttributes.FONT, selectedFont);
    colorRed.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, colorRed, DisplayMode.NORMAL,
        RISK_LEVEL_CONFIG.RISK_LVL_HIGH.getCode());

    // risk - medium
    IStyle colorYellow = new Style();
    this.colorMedium = new Color(device, RISK_LEVEL_CONFIG.RISK_LVL_MEDIUM.getRed(),
        RISK_LEVEL_CONFIG.RISK_LVL_MEDIUM.getGreen(), RISK_LEVEL_CONFIG.RISK_LVL_MEDIUM.getBlue());
    colorYellow.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, this.colorMedium);
    colorYellow.setAttributeValue(CellStyleAttributes.FONT, selectedFont);
    colorYellow.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, colorYellow, DisplayMode.NORMAL,
        RISK_LEVEL_CONFIG.RISK_LVL_MEDIUM.getCode());

    // risk - low
    IStyle colorGreen = new Style();
    this.colorLow = new Color(device, RISK_LEVEL_CONFIG.RISK_LVL_LOW.getRed(),
        RISK_LEVEL_CONFIG.RISK_LVL_LOW.getGreen(), RISK_LEVEL_CONFIG.RISK_LVL_LOW.getBlue());
    colorGreen.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, this.colorLow);
    colorGreen.setAttributeValue(CellStyleAttributes.FONT, selectedFont);
    colorGreen.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, colorGreen, DisplayMode.NORMAL,
        RISK_LEVEL_CONFIG.RISK_LVL_LOW.getCode());

    // risk - na
    IStyle colorNA = new Style();
    colorNA.setAttributeValue(CellStyleAttributes.FONT, selectedFont);
    colorNA.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, colorNA, DisplayMode.NORMAL,
        RISK_LEVEL_CONFIG.RISK_LVL_NA.getCode());

    // Grey Out
    Style greyStyle = new Style();
    greyStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_GRAY);
    greyStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, greyStyle, DisplayMode.NORMAL, ROW_GREYED);

    // register checkbox enabled state
    registerCheckBoxInEnabledState(configRegistry);

    // register checkbox disabled state
    registerCheckBoxInDisabledState(configRegistry);

    // register comboxbox style
    registerComboBoxStyleRBInputData(configRegistry);
    // register comboxbox style
    registerComboBoxStyle(configRegistry);
  }

  /**
   * @param configRegistry
   */
  private void registerComboBoxStyleRBInputData(final IConfigRegistry configRegistry) {

    String label = RB_INPUT_DATA_COMBO;

    // default font style
    Style style = new Style();
    style.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WHITE);
    style.setAttributeValue(CellStyleAttributes.FONT, GUIHelper.DEFAULT_FONT);
    style.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);

    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, style, DisplayMode.NORMAL, label);

    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, new ComboBoxPainter(), DisplayMode.NORMAL,
        label);
    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE,
        DisplayMode.NORMAL, label);
  }

  /**
   * @param configRegistry
   */
  private void registerComboBoxStyle(final IConfigRegistry configRegistry) {

    String label = RB_SHARE_COMBO;

    // default font style
    Style style = new Style();
    style.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WHITE);
    style.setAttributeValue(CellStyleAttributes.FONT, GUIHelper.DEFAULT_FONT);
    style.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);

    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, style, DisplayMode.NORMAL, label);

    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, new ComboBoxPainter(), DisplayMode.NORMAL,
        label);
    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE,
        DisplayMode.NORMAL, label);
  }

  /**
   * Register check box in disabled state.
   *
   * @param configRegistry the config registry
   */
  private void registerCheckBoxInDisabledState(final IConfigRegistry configRegistry) {

    // YES column
    Style cellStyleYesDisabled = new Style();
    cellStyleYesDisabled.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
    cellStyleYesDisabled.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_GRAY);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyleYesDisabled, NORMAL,
        RM_STYLE_CODE.RELEVANT_YES_DISABLED.styleCode);
    configRegistry.registerConfigAttribute(CELL_PAINTER, new CheckBoxPainter(this.radioIcon_1, this.radioIcon_0),
        NORMAL, RM_STYLE_CODE.RELEVANT_YES_DISABLED.styleCode);
    configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new DefaultBooleanDisplayConverter(),
        NORMAL, RM_STYLE_CODE.RELEVANT_YES_DISABLED.styleCode);
    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.NEVER_EDITABLE,
        DisplayMode.NORMAL, RM_STYLE_CODE.RELEVANT_YES_DISABLED.styleCode);

    // NO column
    Style cellStyleNoDisabled = new Style();
    cellStyleNoDisabled.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
    cellStyleNoDisabled.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_GRAY);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyleNoDisabled, NORMAL,
        RM_STYLE_CODE.RELEVANT_NO_DISABLED.styleCode);
    configRegistry.registerConfigAttribute(CELL_PAINTER, new CheckBoxPainter(this.radioIcon_1, this.radioIcon_0),
        NORMAL, RM_STYLE_CODE.RELEVANT_NO_DISABLED.styleCode);
    configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new DefaultBooleanDisplayConverter(),
        NORMAL, RM_STYLE_CODE.RELEVANT_NO_DISABLED.styleCode);


    // NA column
    Style cellStyleNADisabled = new Style();
    cellStyleNADisabled.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
    cellStyleNADisabled.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_GRAY);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyleNADisabled, NORMAL,
        RM_STYLE_CODE.RELEVANT_NA_DISABLED.styleCode);
    configRegistry.registerConfigAttribute(CELL_PAINTER, new CheckBoxPainter(this.radioIcon_1, this.radioIcon_0),
        NORMAL, RM_STYLE_CODE.RELEVANT_NA_DISABLED.styleCode);
    configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new DefaultBooleanDisplayConverter(),
        NORMAL, RM_STYLE_CODE.RELEVANT_NA_DISABLED.styleCode);

  }

  /**
   * @param configRegistry
   */
  public void registerCheckBoxInEnabledState(final IConfigRegistry configRegistry) {

    // YES column
    Style cellStyleYes = new Style();
    cellStyleYes.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyleYes, NORMAL,
        RM_STYLE_CODE.RELEVANT_YES.styleCode);
    configRegistry.registerConfigAttribute(CELL_PAINTER, new CheckBoxPainter(this.radioIcon_1, this.radioIcon_0),
        NORMAL, RM_STYLE_CODE.RELEVANT_YES.styleCode);
    configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new DefaultBooleanDisplayConverter(),
        NORMAL, RM_STYLE_CODE.RELEVANT_YES.styleCode);

    // NO column
    Style cellStyleNo = new Style();
    cellStyleNo.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyleNo, NORMAL,
        RM_STYLE_CODE.RELEVANT_NO.styleCode);
    configRegistry.registerConfigAttribute(CELL_PAINTER, new CheckBoxPainter(this.radioIcon_1, this.radioIcon_0),
        NORMAL, RM_STYLE_CODE.RELEVANT_NO.styleCode);
    configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new DefaultBooleanDisplayConverter(),
        NORMAL, RM_STYLE_CODE.RELEVANT_NO.styleCode);

    // NA column
    Style cellStyleNA = new Style();
    cellStyleNA.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
    cellStyleNA.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WIDGET_LIGHT_SHADOW);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyleNA, NORMAL,
        RM_STYLE_CODE.RELEVANT_NA.styleCode);
    configRegistry.registerConfigAttribute(CELL_PAINTER, new CheckBoxPainter(this.radioIcon_1, this.radioIcon_0),
        NORMAL, RM_STYLE_CODE.RELEVANT_NA.styleCode);
    configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new DefaultBooleanDisplayConverter(),
        NORMAL, RM_STYLE_CODE.RELEVANT_NA.styleCode);


  }

  /**
   * @author gge6cob
   */
  public static enum RM_STYLE_CODE {
                                    /**
                                     * YES Style
                                     */
                                    RELEVANT_YES(CHECK_BOX_CONFIG_LABEL + RiskEvalNatTableSection.RELEVANT_YES_COLNUM, CHECK_BOX_EDITOR_CNG_LBL + RiskEvalNatTableSection.RELEVANT_YES_COLNUM),
                                    /**
                                     * NO Style
                                     */
                                    RELEVANT_NO(CHECK_BOX_CONFIG_LABEL + RiskEvalNatTableSection.RELEVANT_NO_COLNUM, CHECK_BOX_EDITOR_CNG_LBL + RiskEvalNatTableSection.RELEVANT_NO_COLNUM),
                                    /**
                                     * NA Style
                                     */
                                    RELEVANT_NA(CHECK_BOX_CONFIG_LABEL + RiskEvalNatTableSection.RELEVANT_NA_COLNUM, CHECK_BOX_EDITOR_CNG_LBL + RiskEvalNatTableSection.RELEVANT_NA_COLNUM),

                                    /**
                                     * RB_SOFTWARE_SHARE StylE
                                     */

                                    /**
                                     * YES Style - Disabled
                                     */
                                    RELEVANT_YES_DISABLED(RiskEvalNatTableSection.RELEVANT_YES_COLNUM + DISABLED, RiskEvalNatTableSection.RELEVANT_YES_COLNUM + DISABLED),
                                    /**
                                     * NO Style - Disabled
                                     */
                                    RELEVANT_NO_DISABLED(RiskEvalNatTableSection.RELEVANT_NO_COLNUM + DISABLED, RiskEvalNatTableSection.RELEVANT_NO_COLNUM + DISABLED),
                                    /**
                                     * NA Style - Disabled
                                     */
                                    RELEVANT_NA_DISABLED(RiskEvalNatTableSection.RELEVANT_NA_COLNUM + DISABLED, RiskEvalNatTableSection.RELEVANT_NA_COLNUM + DISABLED);



    /**
     * value in database column
     */
    private final String styleCode;
    /**
     * Display value in UI
     */
    private final String editorCode;

    RM_STYLE_CODE(final String styleCode, final String editorCode) {
      this.styleCode = styleCode;
      this.editorCode = editorCode;
    }

    /**
     * @return DB type literal
     */
    public final String getStyleCode() {
      return this.styleCode;
    }

    /**
     * @return UI Type string
     */
    public final String getEditorCode() {
      return this.editorCode;
    }
  }


  /**
   * @return the colorMedium
   */
  public Color getColorMedium() {
    return this.colorMedium;
  }


  /**
   * @param colorMedium the colorMedium to set
   */
  public void setColorMedium(final Color colorMedium) {
    this.colorMedium = colorMedium;
  }


  /**
   * @return the colorLow
   */
  public Color getColorLow() {
    return this.colorLow;
  }


  /**
   * @param colorLow the colorLow to set
   */
  public void setColorLow(final Color colorLow) {
    this.colorLow = colorLow;
  }


  /**
   * @return the colorHigh
   */
  public Color getColorHigh() {
    return this.colorHigh;
  }


  /**
   * @param colorHigh the colorHigh to set
   */
  public void setColorHigh(final Color colorHigh) {
    this.colorHigh = colorHigh;
  }
}
