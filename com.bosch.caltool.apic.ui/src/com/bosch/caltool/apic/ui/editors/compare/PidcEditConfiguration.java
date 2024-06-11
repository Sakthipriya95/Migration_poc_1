/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.compare;

import java.util.Set;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.validate.IDataValidator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ImagePainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.CellPainterDecorator;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.style.TextDecorationEnum;
import org.eclipse.nebula.widgets.nattable.ui.util.CellEdgeEnum;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcSubVariantAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcSubVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.nattable.CustomFilterGridLayer;


/**
 * Registry Configuration class for compare editor which assigns style based on the config labels registered on the
 * cells
 *
 * @author jvi6cob
 */
public class PidcEditConfiguration extends AbstractRegistryConfiguration {

  /**
   * Instance of pidc version bo
   */
  private final PidcVersionBO pidcVersionBO;

  /**
   * @param pidcVersionBO Pidc Version BO
   */
  public PidcEditConfiguration(final PidcVersionBO pidcVersionBO) {
    super();
    this.pidcVersionBO = pidcVersionBO;
  }


  /**
   * Configure NatTable's {@link IConfigRegistry} upon receiving this call back. A mechanism to plug-in custom
   * {@link ICellPainter}, {@link IDataValidator} etc.
   */
  @Override
  public void configureRegistry(final IConfigRegistry configRegistry) {

    // configure the validation error style
    // Style for cells under column with BALL icon
    IStyle validationErrorStyle = new Style();
    validationErrorStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
    validationErrorStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WIDGET_LIGHT_SHADOW);
    // Get image for column
    ImagePainter imgPainter = new ImagePainter() {

      /**
       * Returns the image for a cell.{@inheritDoc}
       */
      @Override
      protected Image getImage(final ILayerCell cell, final IConfigRegistry configRegistry) {
        CustomFilterGridLayer<PidcNattableRowObject> customFilterGridLayer =
            (CustomFilterGridLayer<PidcNattableRowObject>) cell.getLayer();
        // get the selected row object
        PidcNattableRowObject compareRowObject =
            customFilterGridLayer.getBodyDataProvider().getRowObject(cell.getRowIndex());
        // get the attribute object
        Attribute attribute = compareRowObject.getAttribute();
        // attribute client bo
        AttributeClientBO attrClientBo = new AttributeClientBO(attribute);


        Image image = null;
        // Invisible attrs

        // Check for invisible pidc attr
        // Partially Invisible || All invisible
        if (isPIDCAttrInvisible(compareRowObject.getProjectAttributeHandler().getProjectAttr())) {
          image = getLinkOverLayedImage(ImageKeys.GREY_BALL_16X16, ImageKeys.LINK_DECORATOR_12X12, attribute);
        }
        // IF MANDATORY
        else if (compareRowObject.getProjectAttributeHandler().isMandatory()) {
          // ICDM-931
          image = getLinkOverLayedImage(ImageKeys.RED_BALL_16X16, ImageKeys.LINK_DECORATOR_12X12, attribute);
        }
        // IF DEPENDENT ATTRIBUTE
        // need to check
        else if (CommonUtils.isNotEmpty(PidcEditConfiguration.this.pidcVersionBO.getPidcDataHandler()
            .getAttrRefDependenciesMap().get(attribute.getId()))) {
          // ICDM-931
          if (hasAttrDependencies(attribute)) {
            // attr with dependencies
            image = getLinkOverLayedImage(ImageKeys.DEPN_ATTR_28X30, ImageKeys.LINK_DECORATOR_12X12, attribute);
          }
        }
        else if (attrClientBo.isGrouped()) {
          // ICDM-2636
          // grouped attr
          image = getLinkOverLayedImage(ImageKeys.BLUE_BALL_GRPD_16X16, ImageKeys.LINK_DECORATOR_12X12, attribute);
        }
        else {

          // predef attr available

          if (isAttrPreDefnd(compareRowObject.getProjectAttributeHandler().getProjectAttr())) {
            // ICDM-2636
            image = getLinkOverLayedImage(ImageKeys.BLUE_BALL_PREDEF_16X16, ImageKeys.LINK_DECORATOR_12X12, attribute);
          }
          else {
            image = getLinkOverLayedImage(null, ImageKeys.LINK_DECORATOR_12X12, attribute);
          }

        }
        return image;
      }


    };

    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter, DisplayMode.NORMAL, "BALL");
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.NORMAL,
        "BALL");

    // Added to display the hyperlink icon on attribute values column
    TextPainter textPainter = new TextPainter(false, true);
    CellPainterDecorator cellPainterDecorator = new CellPainterDecorator(textPainter, CellEdgeEnum.LEFT,
        new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.LINK_DECORATOR_12X12)));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, cellPainterDecorator, DisplayMode.NORMAL,
        "VALUE");

    IStyle validationErrorStyle3 = new Style();
    validationErrorStyle3.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
    validationErrorStyle3.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WHITE);
    validationErrorStyle3.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_BLUE);
    validationErrorStyle3.setAttributeValue(CellStyleAttributes.TEXT_DECORATION, TextDecorationEnum.UNDERLINE);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle3, DisplayMode.NORMAL,
        "HYPERLINK");


    // Style for INVISIBLE attributes
    validationErrorStyle = new Style();
    validationErrorStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
    validationErrorStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WIDGET_LIGHT_SHADOW);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.NORMAL,
        "INVISIBLE");

    // Style for VISIBLE attributes
    IStyle validationErrorStyle1 = new Style();
    validationErrorStyle1.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
    validationErrorStyle1.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WHITE);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle1, DisplayMode.NORMAL,
        "VISIBLE");

    // Style for HIDDEN attributes
    IStyle validationHiddenStyle = new Style();
    validationHiddenStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
    validationHiddenStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR,
        Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationHiddenStyle, DisplayMode.NORMAL,
        "HIDDEN");

    // Style for deleted attribute Values
    IStyle validationErrorStyle4 = new Style();
    validationErrorStyle4.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
    validationErrorStyle4.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_RED);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle4, DisplayMode.NORMAL,
        "DELVALUE");

    // Style for attribute Values if the value is not cleared
    IStyle validationErrorStyle5 = new Style();
    validationErrorStyle5.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
    validationErrorStyle5.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR,
        new Color(Display.getCurrent(), 255, 128, 0));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle5, DisplayMode.NORMAL,
        "CLEAREDVAL");
    // Style for attribute Values if the value is not visible due to attribute dependencies
    IStyle validationErrorStyle6 = new Style();
    validationErrorStyle6.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
    validationErrorStyle6.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR,
        new Color(Display.getCurrent(), 242, 0, 230));

    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle6, DisplayMode.NORMAL,
        "DEPATTRVAL");

  }


  /**
   * ICDM-931 Gives the deocrated image for objects which has links
   *
   * @param baseImgKey base image key
   * @param overlayKey overlay image key
   * @param element object
   * @return baseimage/decoratedImage
   */
  public Image getLinkOverLayedImage(final ImageKeys baseImgKey, final ImageKeys overlayKey, final Object element) {
    Attribute attr = (Attribute) element;

    if (!this.pidcVersionBO.getPidcDataHandler().getLinks().contains(attr.getId())) {
      if (baseImgKey != null) {
        return ImageManager.getInstance().getRegisteredImage(baseImgKey);
      }
      return null;
    }

    return ImageManager.getDecoratedImage(baseImgKey, overlayKey, IDecoration.TOP_RIGHT);
  }


  /**
   * @param ipidcAttrs
   * @param isAnyIPIDCAttrPredefined
   * @return
   */
  private boolean isAttrPreDefnd(final IProjectAttribute pidcAttr) {
    // return true if pidcAttr is a predefined attribute
    return this.pidcVersionBO.getPredefAttrGrpAttrMap().containsKey(pidcAttr);
  }

  /**
   * Check if pidc attribute invisible
   * 
   * @param ipidcAttrs
   * @param isAnyIPIDCAttrInvisible
   * @return
   */
  private boolean isPIDCAttrInvisible(final IProjectAttribute ipidcAttribute) {
    // check whether ipidcAttribute is an isvisible attribute
    boolean isAnyIPIDCAttrInvisible = false;
    AbstractProjectAttributeBO handler = null;
    // check the type of attribute and define the handler according to it
    if (ipidcAttribute instanceof PidcVersionAttribute) {
      handler = new PidcVersionAttributeBO((PidcVersionAttribute) ipidcAttribute, this.pidcVersionBO);
    }
    else if (ipidcAttribute instanceof PidcVariantAttribute) {
      PidcVariantBO varHandler =
          new PidcVariantBO(this.pidcVersionBO.getPidcVersion(), this.pidcVersionBO.getPidcDataHandler().getVariantMap()
              .get(((PidcVariantAttribute) ipidcAttribute).getVariantId()), this.pidcVersionBO.getPidcDataHandler());
      handler = new PidcVariantAttributeBO((PidcVariantAttribute) ipidcAttribute, varHandler);
    }
    else if (ipidcAttribute instanceof PidcSubVariantAttribute) {
      PidcSubVariantBO subVarHandler =
          new PidcSubVariantBO(this.pidcVersionBO.getPidcVersion(),
              this.pidcVersionBO.getPidcDataHandler().getSubVariantMap()
                  .get(((PidcSubVariantAttribute) ipidcAttribute).getSubVariantId()),
              this.pidcVersionBO.getPidcDataHandler());
      handler = new PidcSubVariantAttributeBO((PidcSubVariantAttribute) ipidcAttribute, subVarHandler);
    }

    if ((handler != null) && !handler.isVisible()) {
      isAnyIPIDCAttrInvisible = true;
    }

    return isAnyIPIDCAttrInvisible;
  }

  /**
   * Method which checks whether the passed attribute has attribute dependencies
   *
   * @param attribute Attribute
   * @return boolean
   */
  private boolean hasAttrDependencies(final Attribute attribute) {
    // Check for attr dependencies
    Set<AttrNValueDependency> depenAttr =
        this.pidcVersionBO.getPidcDataHandler().getAttrRefDependenciesMap().get(attribute.getId());
    for (AttrNValueDependency attrDependency : depenAttr) {
      if (attrDependency != null) {
        if (attrDependency.getAttributeId() != null) {
          return true;
        }
      }
    }
    return false;
  }
}
