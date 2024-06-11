/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.nattable.filters;

import java.util.Map;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.ruleseditor.pages.ParametersRulePage;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;


/**
 * @author jvi6cob
 */
public class AttrValueCombChooserDialog extends AbstractDialog {

  /**
   *
   */
  private static final String EXISTING_ATTRIBUTE_TITLE = "Existing Attribute Value Combinations";

  private static final int INDEX_40 = 40;

  private static final int INDEX_200 = 200;

  private GridTableViewer tableViewer;

  private final Map<Attribute, String> combiMap;

  private final ParametersRulePage paramRulesPage;

  /**
   * Constructor
   *
   * @param parentShell
   * @param paramRulesPage
   */
  public AttrValueCombChooserDialog(final Shell parentShell, final Map<Attribute, String> combiMap,
      final ParametersRulePage paramRulesPage) {
    super(parentShell);
    this.combiMap = combiMap;
    this.paramRulesPage = paramRulesPage;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle(EXISTING_ATTRIBUTE_TITLE);

    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void create() {
    super.create();
    setTitle(EXISTING_ATTRIBUTE_TITLE);
    setMessage("Filter Attribute Value");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    Composite area = (Composite) super.createDialogArea(parent);
    area.setLayout(new GridLayout());
    createAttrListControl(area);
    return area;
  }

  /**
   * Create project control
   */
  private void createAttrListControl(final Composite workArea) {

    this.tableViewer = GridTableViewerUtil.getInstance().createGridTableViewer(workArea,
        SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);

    this.tableViewer.getGrid().setLayoutData(GridDataUtil.getInstance().getGridData());
    this.tableViewer.getGrid().setLinesVisible(true);
    this.tableViewer.getGrid().setHeaderVisible(true);
    workArea.setLayout(new GridLayout());
    createTabColumns();
    this.tableViewer.setContentProvider(ArrayContentProvider.getInstance());
    addMouseDownListener();

    this.tableViewer.setInput(this.combiMap.keySet());
  }

  /**
   * Add mouse down listener to the pidc attribute value edit column
   */
  private void addMouseDownListener() {
    this.tableViewer.getGrid().addMouseListener(new MouseAdapter() {

      @Override
      public void mouseDown(final MouseEvent mouseEvent) {
        final int columnIndex =
            GridTableViewerUtil.getInstance().getTabColIndex(mouseEvent, AttrValueCombChooserDialog.this.tableViewer);
        final int colIndex = CommonUIConstants.COLUMN_INDEX_2;

        if (columnIndex == colIndex) {
          final Point point = new Point(mouseEvent.x, mouseEvent.y);
          // Determine which row was selected
          final GridItem item = AttrValueCombChooserDialog.this.tableViewer.getGrid().getItem(point);
          if ((item != null) && !item.isDisposed()) {
            final Object data = item.getData();
            final Attribute attr = (Attribute) data;
            // Determine which column was selected
            for (int i = 0, n = AttrValueCombChooserDialog.this.tableViewer.getGrid().getColumnCount(); i < n; i++) {
              final Rectangle rect = item.getBounds(i);
              if (rect.contains(point)) {
                SelectValueDialog dialog = new SelectValueDialog(Display.getCurrent().getActiveShell(), attr,
                    AttrValueCombChooserDialog.this, AttrValueCombChooserDialog.this.paramRulesPage);
                dialog.open();
                break;
              }
            }
          }
        }
      }
    });
  }

  /**
   * Defines the columns of the TableViewer
   */
  private void createTabColumns() {

    // activate the tooltip support for the viewer
    ColumnViewerToolTipSupport.enableFor(this.tableViewer, ToolTip.NO_RECREATE);

    createAttrCol();

    createValueCol();

    createEditCol();
  }

  /**
   * Creates the Value Column
   */
  private void createValueCol() {
    final GridViewerColumn valueColumn = new GridViewerColumn(this.tableViewer, SWT.NONE);
    valueColumn.getColumn().setText("Value");
    valueColumn.getColumn().setWidth(INDEX_200);
    valueColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        final Attribute item = (Attribute) element;
        return AttrValueCombChooserDialog.this.combiMap.get(item);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getToolTipText(final Object element) {
        final Attribute item = (Attribute) element;
        return AttrValueCombChooserDialog.this.combiMap.get(item);
      }
    });
  }

  /**
   * Creates the Edit column
   */
  private void createEditCol() {
    final GridViewerColumn varColumn = new GridViewerColumn(this.tableViewer, SWT.NONE);
    varColumn.getColumn().setText("Edit");
    varColumn.getColumn().setWidth(INDEX_40);
    varColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        return "";
      }


      /**
       * {@inheritDoc}
       */
      @Override
      public Image getImage(final Object element) {
        return ImageManager.getInstance().getRegisteredImage(ImageKeys.VARIANT_VALUE_16X16);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getToolTipText(final Object element) {
        return "Select a value from the list";
      }
    });
  }

  /**
   * Creates the attribute column
   */
  private void createAttrCol() {
    final GridViewerColumn attrColumn = new GridViewerColumn(this.tableViewer, SWT.NONE);
    attrColumn.getColumn().setText("Attribute");
    attrColumn.getColumn().setWidth(INDEX_200);
    attrColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        final Attribute item = (Attribute) element;
        return item.getName();
      }
    });
  }


  /**
   * Returns the TableViewer used in the {@link AttrValueCombChooserDialog}
   *
   * @return the tableViewer
   */
  public GridTableViewer getTableViewer() {
    return this.tableViewer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(SWT.CLOSE | SWT.BORDER | SWT.RESIZE | SWT.TITLE);
    setBlockOnOpen(false);
  }


  /**
   * Returns the combimap used in the {@link AttrValueCombChooserDialog}
   *
   * @return the combiMap
   */
  public Map<Attribute, String> getCombiMap() {
    return this.combiMap;
  }

}
