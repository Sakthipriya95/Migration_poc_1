/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.rcputils.action;


import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.StatusLineLayoutData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;


/**
 * Status line contribution for the current user information.
 * 
 * @author bne4cob
 */
public class TableInfoStatusLineContribution extends ContributionItem {

  /**
   * Contribution's ID
   */
  public static final String OBJ_ID = "com.bosch.rcputils.action.TableInfoStatusLineContribution";

  /**
   * Tooltip start length in string builder
   */
  private static final int SB_START_LEN = 60;

  /**
   * Width
   */
  private static final int LBL_WIDTH_CHRS = 20;

  /**
   * Label
   */
  private CLabel label;

  /**
   * Visible records count
   */
  private int visibleRecords;

  /**
   * Total records count
   */
  private int totalRecords;

  /**
   * whether info is set
   */
  private boolean infoSet;

  /**
   * Error color
   */
  private Color colorErr;

  /**
   * Info color
   */
  private Color colorInfo;


  /**
   * Constructor
   */
  public TableInfoStatusLineContribution() {
    super(OBJ_ID);
    infoSet = false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void fill(final Composite parent) {

    // Creates a separator to the parent
    new Label(parent, SWT.SEPARATOR);

    label = new CLabel(parent, SWT.SHADOW_NONE);

    GC graphics = new GC(parent);
    graphics.setFont(parent.getFont());
    FontMetrics fontMtric = graphics.getFontMetrics();

    StatusLineLayoutData statusLineLayoutData = new StatusLineLayoutData();

    statusLineLayoutData.widthHint = fontMtric.getAverageCharWidth() * LBL_WIDTH_CHRS;
    statusLineLayoutData.heightHint = fontMtric.getHeight();
    label.setLayoutData(statusLineLayoutData);

    graphics.dispose();

    setLabelProps();

    label.addDisposeListener(new DisposeListener() {

      /**
       * Dispose the color objects
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void widgetDisposed(final DisposeEvent event) {
        colorErr.dispose();
        colorInfo.dispose();
      }
    });

  }

  /**
   * Set the record count information
   * 
   * @param visibleRecords visibleRecords
   * @param totalRecords totalRecords
   */
  public void setInfo(final int visibleRecords, final int totalRecords) {
    this.visibleRecords = visibleRecords;
    this.totalRecords = totalRecords;
    this.infoSet = true;
  }

  /**
   * @return whether the message is info or error
   */
  private boolean isInfoMsg() {
    return this.totalRecords == this.visibleRecords;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isDirty() {
    return this.infoSet || super.isDirty();
  }

  /**
   * @return the text to be displayed
   */
  private String getText() {
    StringBuilder text = new StringBuilder(SB_START_LEN);
    if (infoSet) {
      text.append(visibleRecords).append(" / ").append(totalRecords);
    }
    return text.toString();
  }

  /**
   * @return the tooltip to be displayed
   */
  private String getToolTip() {
    final StringBuilder tooltip = new StringBuilder();
    if (infoSet) {
      tooltip.append("Displaying ").append(this.visibleRecords).append(" out of ").append(this.totalRecords)
          .append(" records");
    }

    return tooltip.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void update() {
    setLabelProps();
    this.label.redraw();
  }

  /**
   * Set the label properties
   */
  private void setLabelProps() {

    if (colorErr == null || colorErr.isDisposed()) {
      colorErr = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
    }
    if (colorInfo == null || colorInfo.isDisposed()) {
      colorInfo = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
    }

    if (isInfoMsg()) {
      this.label.setForeground(this.colorInfo);
    }
    else {
      this.label.setForeground(this.colorErr);
    }

    label.setText(getText());
    label.setToolTipText(getToolTip());
  }

}
