/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.utils;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.BackgroundPainter;
import org.eclipse.nebula.widgets.nattable.style.CellStyleUtil;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

/**
 * Paints one or more images in a given cell based on the labels. Since label check happens only after this painter has
 * been invoked, it is required that this painter be registered always on label. Then this paint will decide which image
 * to paint based on other configured labels. Below is an example of this usage.
 *
 * <pre>
 * CustomNatMultiImagePainter painter = new CustomNatMultiImagePainter(4, true);
 *
 * //Replace ... below with appropriate images
 * painter.add("BLACK_LIST_LABEL", ...);
 * painter.add("COMPLI_LABEL", ...);
 * painter.add("READ_ONLY_LABEL", ...);
 * painter.add("QSSDLABEL", ...);
 * painter.add("VIRTUAL_RECORD", ...);
 *
 * // Here MULTI_IMAGE_PAINTER is label that needs to be added by data provider whenever this painter needs to be invoked
 * configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, painter, DisplayMode.NORMAL, "MULTI_IMAGE_PAINTER");
 * </pre>
 */
public class CustomNatMultiImagePainter extends BackgroundPainter {

  private final List<LabelImagePair> labelImages = new ArrayList<>();
  private final boolean paintBg;
  private final int spacing;

  /**
   * Creates a multi image painter
   *
   * @param spacing number of pixels of spacing between each image. It can be -ve, in that case images will overlap
   * @param paintBg paint the background or not
   */
  public CustomNatMultiImagePainter(final int spacing, final boolean paintBg) {
    this.spacing = spacing;
    this.paintBg = paintBg;
  }

  /**
   * Adds a given label and image combination
   */
  public CustomNatMultiImagePainter add(final String label, final Image image) {
    this.labelImages.add(new LabelImagePair(label, image));
    return this;
  }

  @Override
  public int getPreferredWidth(final ILayerCell cell, final GC gc, final IConfigRegistry configRegistry) {
    int width = 0;
    for (Image image : getImages(cell)) {
      if (width != 0) {
        width += this.spacing;
      }
      width += image.getBounds().width;
    }

    return width;
  }

  protected List<Image> getImages(final ILayerCell cell) {
    List<Image> images = new ArrayList<>();
    for (LabelImagePair labelImage : this.labelImages) {
      if (cell.getConfigLabels().hasLabel(labelImage.label)) {
        images.add(labelImage.image);
      }
    }

    return images;
  }

  @Override
  public int getPreferredHeight(final ILayerCell cell, final GC gc, final IConfigRegistry configRegistry) {
    int height = 0;
    for (Image image : getImages(cell)) {
      height = Math.max(height, image.getBounds().height);
    }

    return height;
  }

  @Override
  public void paintCell(final ILayerCell cell, final GC gc, final Rectangle bounds,
      final IConfigRegistry configRegistry) {
    if (this.paintBg) {
      super.paintCell(cell, gc, bounds, configRegistry);
    }

    IStyle cellStyle = CellStyleUtil.getCellStyle(cell, configRegistry);

    List<Image> images = getImages(cell);
    int boundX = bounds.x +
        CellStyleUtil.getHorizontalAlignmentPadding(cellStyle, bounds, getPreferredWidth(cell, gc, configRegistry));
    int paddingY =
        CellStyleUtil.getVerticalAlignmentPadding(cellStyle, bounds, getPreferredHeight(cell, gc, configRegistry));

    for (Image image : images) {
      Rectangle imageBounds = image.getBounds();
      gc.drawImage(image, boundX, bounds.y + paddingY);
      boundX += imageBounds.width + this.spacing;
    }

  }
}

class LabelImagePair {

  public final String label;
  public final Image image;

  public LabelImagePair(final String label, final Image image) {
    this.label = label;
    this.image = image;
  }
}
