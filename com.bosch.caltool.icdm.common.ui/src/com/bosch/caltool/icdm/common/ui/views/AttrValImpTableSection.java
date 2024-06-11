/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.views;

import java.util.SortedSet;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.dialogs.RuleAttrValueEditDialog;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.wizards.CalDataFileImportWizard;
import com.bosch.caltool.icdm.common.ui.wizards.pages.AttrValueImpWizardPage;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;


/**
 * Creates attr-val table in the second page of caldata wizard
 *
 * @author bru2cob
 */
public class AttrValImpTableSection {

  /**
   * Attribute col width
   */
  private static final int ATTR_COL_WIDTH = 200;
  /**
   * Value column width
   */
  private static final int VAL_COL_WIDTH = 200;
  /**
   * section
   */
  private Section sectionThree;
  /**
   * form
   */
  private Form formThree;

  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;

  /**
   * Table for attributes & dependencies
   */
  private GridTableViewer attrsTableViewer;
  /**
   * Instance of attr-val import page
   */
  private final AttrValueImpWizardPage attrValWizPg;

  /**
   * instance of wizard
   */
  private final CalDataFileImportWizard wizard;

  /**
   * value edit column width
   */
  private static final int VAL_EDIT_COL_WIDTH = 25;

  /**
   * Constructor
   *
   * @param attrValueImpWizardPage attrValueImpWizardPage
   */
  public AttrValImpTableSection(final AttrValueImpWizardPage attrValueImpWizardPage) {
    this.attrValWizPg = attrValueImpWizardPage;
    this.wizard = (CalDataFileImportWizard) this.attrValWizPg.getWizard();
  }

  /**
   * @return the attrsTableViewer
   */
  public GridTableViewer getAttrsTableViewer() {
    return this.attrsTableViewer;
  }

  /**
   * @param workArea Composite
   */
  public void createSectionAttr(final Composite workArea) {
    // create new section for dependent attributes
    this.sectionThree = getFormToolkit().createSection(workArea, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.sectionThree.setText("Dependent Attributes");
    this.sectionThree.setExpanded(true);
    this.sectionThree.getDescriptionControl().setEnabled(true);
    this.sectionThree
        .setDescription(
            "Displays attributes for selected " +
                this.wizard.getWizardData().getParamColDataProvider()
                    .getObjectTypeName(this.wizard.getWizardData().getImportObject()) +
                ". Select value(s) for each attribute from drop-down");
    // create form
    createFormThree();
    this.sectionThree.setLayoutData(GridDataUtil.getInstance().getGridData());
    // set client
    this.sectionThree.setClient(this.formThree);

  }

  /**
   * create form
   */
  private void createFormThree() {

    // create GridData instance
    final GridData gridDataFour = new GridData();
    gridDataFour.horizontalAlignment = GridData.FILL;
    gridDataFour.grabExcessHorizontalSpace = true;
    gridDataFour.verticalAlignment = GridData.FILL;
    gridDataFour.grabExcessVerticalSpace = true;
    // create form
    this.formThree = getFormToolkit().createForm(this.sectionThree);

    GridLayout layout = new GridLayout();
    this.formThree.getBody().setLayout(layout);
    // create attributes table
    createAttrsTable(gridDataFour);

  }

  /**
   * @param gridDataFour
   */
  private void createAttrsTable(final GridData gridDataFour) {
    // create attributes table
    this.attrsTableViewer =
        new GridTableViewer(this.formThree.getBody(), SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);


    this.attrsTableViewer.getGrid().setLayoutData(gridDataFour);

    this.attrsTableViewer.getGrid().setLinesVisible(true);
    this.attrsTableViewer.getGrid().setHeaderVisible(true);
    // set content provider
    this.attrsTableViewer.setContentProvider(ArrayContentProvider.getInstance());

    // create table columns
    createColumns();
    // create mouse listener
    addMouseDownListener();
  }

  /**
   * Add mouse down listener to the pidc attribute value edit column
   */
  private void addMouseDownListener() {
    this.attrsTableViewer.getGrid().addMouseListener(new MouseAdapter() {

      @Override
      public void mouseDown(final MouseEvent event) {
        final int columnIndex =
            GridTableViewerUtil.getInstance().getTabColIndex(event, AttrValImpTableSection.this.attrsTableViewer);


        if (columnIndex == 2) {
          final Point point = new Point(event.x, event.y);
          // Determine which row was selected
          final GridItem item = AttrValImpTableSection.this.attrsTableViewer.getGrid().getItem(point);
          if ((item != null) && !item.isDisposed()) {
            // Determine which column was selected
            for (int i = 0, n = AttrValImpTableSection.this.attrsTableViewer.getGrid().getColumnCount(); i < n; i++) {
              final Rectangle rect = item.getBounds(i);
              if (rect.contains(point)) {
                editTabItem(columnIndex, point);
                break;
              }
            }
          }
        }
      }
    });
  }

  /**
   * @param columnIndex deines gridviewer column index
   */
  private void editTabItem(final int columnIndex, final Point point) {
    final Attribute editableAttr = getSelectedPIDCAttr(point);
    if (columnIndex == 2) {
      final RuleAttrValueEditDialog dialog = new RuleAttrValueEditDialog(this.attrsTableViewer.getControl().getShell(),
          AttrValImpTableSection.this.attrValWizPg, editableAttr);
      dialog.open();
      this.attrValWizPg.setPageComplete(true);
      GridItem[] items = AttrValImpTableSection.this.attrsTableViewer.getGrid().getItems();
      for (GridItem gridItem : items) {
        if (ApicConstants.DEFAULT_COMBO_SELECT.equals(gridItem.getText(1)) || gridItem.getText(1).isEmpty()) {
          this.attrValWizPg.setPageComplete(false);
          this.attrValWizPg.getContainer().updateButtons();
        }
      }

    }
    else {
      CDMLogger.getInstance().info("EDIT not allowed !", Activator.PLUGIN_ID);
    }
  }

  private Attribute getSelectedPIDCAttr(final Point point) {
    Attribute pidcAttr = null;
    // Determine which row was selected
    AttrValImpTableSection.this.attrsTableViewer.getGrid().selectCell(point);
    final IStructuredSelection selection =
        (IStructuredSelection) AttrValImpTableSection.this.attrsTableViewer.getSelection();
    if ((selection != null) && (selection.size() != 0)) {
      final Object element = selection.getFirstElement();
      if (element instanceof Attribute) {
        pidcAttr = (Attribute) element;

      }
    }
    return pidcAttr;
  }

  /**
   * Defines the columns of the TableViewer
   */
  private void createColumns() {
    // create attr name col
    createNameCol();
    // create attr value col
    createValCol();
    // create attr value selection col
    createAttrValSelColViewer();
  }

  /**
   * This method creates PIDC attribute value edit column
   */
  private void createAttrValSelColViewer() {
    final GridViewerColumn attrValEditColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(AttrValImpTableSection.this.attrsTableViewer, VAL_EDIT_COL_WIDTH);
    // set label provider
    attrValEditColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * Defines attribute value edit image
       */
      private Image editImage;

      /**
       * Get the value edit image {@inheritDoc}
       */
      @Override
      public Image getImage(final Object element) {
        if (this.editImage == null) {
          // when edit image is null, create one
          this.editImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.VALUE_EDIT_28X30);
          return this.editImage;
        }
        return this.editImage;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        return "";
      }
    });
  }

  /**
   * Creates attr value col
   */
  private void createValCol() {
    final GridViewerColumn valueColumn = new GridViewerColumn(this.attrsTableViewer, SWT.NONE);
    valueColumn.getColumn().setText("Value");
    valueColumn.getColumn().setWidth(VAL_COL_WIDTH);
    // set label provider
    valueColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */

      @Override
      public String getText(final Object element) {
        Attribute paramAttr = (Attribute) element;
        if (!AttrValImpTableSection.this.attrValWizPg.getWizardData().getAttrVals().isEmpty()) {
          SortedSet<AttributeValue> selVals =
              AttrValImpTableSection.this.attrValWizPg.getWizardData().getAttrVals().get(paramAttr);
          if (selVals != null) {
            // consolidate the result in values
            StringBuilder values = new StringBuilder();
            for (AttributeValue selVal : selVals) {
              values.append(selVal.getName()).append(",");
            }
            return values.substring(0, values.length() - 1);
          }
        }
        return ApicConstants.DEFAULT_COMBO_SELECT;
      }


    });
  }

  /**
   * Creates attr column
   */
  private void createNameCol() {
    final GridViewerColumn attrNameColumn = new GridViewerColumn(this.attrsTableViewer, SWT.NONE);
    // set column text and width
    attrNameColumn.getColumn().setText("Attribute");
    attrNameColumn.getColumn().setWidth(ATTR_COL_WIDTH);
    // set the label providerS
    attrNameColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        if (element instanceof Attribute) {
          // get the name of the attribute
          Attribute attr = (Attribute) element;
          return attr.getName();
        }
        return "";
      }
    });
  }


  /**
   * This method initializes formToolkit
   *
   * @return org.eclipse.ui.forms.widgets.FormToolkit
   */
  private FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      // if formtoolkit is null, create new one
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }

  /**
   * @return the attrValWizPg
   */
  public AttrValueImpWizardPage getAttrValWizPg() {
    return this.attrValWizPg;
  }

  /**
   * Checks whether all the attrs are set value else disable the next button of the page
   */
  public void checkAllValsSet() {
    getAttrValWizPg().setPageComplete(true);
    GridItem[] items = getAttrsTableViewer().getGrid().getItems();
    for (GridItem gridItem : items) {
      if (gridItem.getText(1).equals(ApicConstants.DEFAULT_COMBO_SELECT) || gridItem.getText(1).isEmpty()) {
        getAttrValWizPg().setPageComplete(false);
      }
    }
  }
}
