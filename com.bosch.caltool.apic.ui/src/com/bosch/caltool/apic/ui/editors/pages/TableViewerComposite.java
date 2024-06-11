/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages;


import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.CustomGridTableViewer;


/**
 * @author dmo5cob
 */
public class TableViewerComposite {

  /**
   * Composite instance
   */
  private Composite composite;
  /**
   * Section instance
   */
  private Section section;
  /**
   * Form instance
   */
  private Form form;
  /**
   * Text instance
   */
  private Text filterTxt;
  /**
   * GridTableViewer instance
   */
  private GridTableViewer tableViewer;

  /**
   * Special Access text to be display if the attributes are owned by Specialist
   */
  private Label specialAccessTextLbl;


  /**
   * This method initializes a composite
   *
   * @param formToolkit ToolKit instance
   * @param parent composite instance
   */
  public void createComposite(final FormToolkit formToolkit, final Composite parent) {

    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite = formToolkit.createComposite(parent);
    this.composite.setLayout(new GridLayout());
    createSection(formToolkit);
    this.composite.setLayoutData(gridData);
  }

  /**
   * @param formToolkit ToolKit instance
   */
  private void createSection(final FormToolkit formToolkit) {
    final GridData gridData5 = GridDataUtil.getInstance().getGridData();
    this.section = formToolkit.createSection(this.composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.section.setText("");
    this.section.setExpanded(true);
    createForm(formToolkit);
    this.section.setLayoutData(gridData5);
    this.section.setClient(this.form);
    this.section.getDescriptionControl().setEnabled(false);
  }


  /**
   * This method initializes form1
   *
   * @param formToolkit
   */
  private void createForm(final FormToolkit formToolkit) {
    this.form = formToolkit.createForm(this.section);
    this.form.getBody().setLayout(new GridLayout());

    this.specialAccessTextLbl = new Label(this.form.getBody(), SWT.NONE);
    this.specialAccessTextLbl.setText("special access rights!");
    this.specialAccessTextLbl.setLayoutData(new GridData());
    this.specialAccessTextLbl.setForeground(this.composite.getDisplay().getSystemColor(SWT.COLOR_RED));
    this.specialAccessTextLbl.setVisible(false);
    // set font style
    FontData fontData = this.specialAccessTextLbl.getFont().getFontData()[0];
    Font font =
        new Font(this.form.getBody().getDisplay(), new FontData(fontData.getName(), fontData.getHeight(), SWT.BOLD));
    this.specialAccessTextLbl.setFont(font);

    // filter textbox
    createFilterTxt(formToolkit);

    final GridData gridData = getTableViewerGridData();
    // add tableviewer
    this.tableViewer = new CustomGridTableViewer(this.form.getBody(),
        SWT.FILL | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.MULTI,
        new int[] { ApicUiConstants.COLUMN_INDEX_1 }/* ICDM-451 */);

    this.tableViewer.getGrid().setLayoutData(gridData);
    this.tableViewer.getGrid().setLinesVisible(true);
    this.tableViewer.getGrid().setHeaderVisible(true);
    this.tableViewer.getGrid().setToolTipText("");
    // set the content provider
    this.tableViewer.setContentProvider(ArrayContentProvider.getInstance());
    // set tooltip
    final GridItem[] items = this.tableViewer.getGrid().getItems();
    for (GridItem gridItem : items) {
      gridItem.setToolTipText(ApicUiConstants.COLUMN_INDEX_1,
          com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants.EMPTY_STRING);
    }
  }

  /**
   * This method creates filter text for Attributes
   */
  private void createFilterTxt(final FormToolkit formToolkit2) {
    this.filterTxt = formToolkit2.createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);

    GridData gridData = getFilterTxtGridData();
    this.filterTxt.setLayoutData(gridData);
    this.filterTxt.setMessage(Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
  }

  /**
   * @return the filterTxt
   */
  public Text getFilterTxt() {
    return this.filterTxt;
  }

  /**
   * @return GridData
   */
  private GridData getTableViewerGridData() {
    GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    gridData.heightHint = 250;
    return gridData;
  }

  /**
   * This method returns filter text GridData object
   *
   * @return GridData
   */
  private GridData getFilterTxtGridData() {
    GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.verticalAlignment = GridData.CENTER;
    return gridData;
  }

  /**
   * @return the composite
   */
  public Composite getComposite() {
    return this.composite;
  }

  /**
   * @return the section
   */
  public Section getSection() {
    return this.section;
  }

  /**
   * @return the tableViewer
   */
  public GridTableViewer getTableViewer() {
    return this.tableViewer;
  }

  /**
   * @return the form
   */
  public Form getForm() {
    return this.form;
  }


  /**
   * @return the exportToLbl
   */
  public Label getSpecialAccessTextLbl() {
    return this.specialAccessTextLbl;
  }
}
