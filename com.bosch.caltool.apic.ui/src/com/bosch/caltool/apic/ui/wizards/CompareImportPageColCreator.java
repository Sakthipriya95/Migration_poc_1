/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.wizards;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridColumnGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.apic.ui.sorter.PIDCImportTabViewerSorter;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.ProjectImportAttr;
import com.bosch.rcputils.nebula.gridviewer.GridColumnUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;

/**
 * @author dja7cob
 */
public class CompareImportPageColCreator {

  private final GridTableViewer pidcAttrTabViewer;

  private final PIDCImportTabViewerSorter pidcAttrTabSorter;

  /**
   * @param pidcAttrTabViewer
   * @param pidcAttrTabSorter
   */
  public CompareImportPageColCreator(final GridTableViewer pidcAttrTabViewer,
      final PIDCImportTabViewerSorter pidcAttrTabSorter) {
    this.pidcAttrTabViewer = pidcAttrTabViewer;
    this.pidcAttrTabSorter = pidcAttrTabSorter;
  }

  /**
   *
   */
  public void createPIDCAttrTabViewerColumns() {

    createVariantNameColViewer();
    createSubVariantNameColViewer();
    // Create PIDC Attribute name Viewer Column
    createPIDCAttrNameColViewer();
    // Create PIDC Attribute description Viewer Column
    createPIDCAttrDescColViewer();
    // Create PIDC Attribute used Viewer Column
    createPIDCAttrUsedColViewer();
    createPIDCAttrStatusColViewer();
    createPIDCAttrCommentsColViewer();
  }

  /**
  *
  */
  private void createSubVariantNameColViewer() {
    final GridViewerColumn attrSubVarNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.pidcAttrTabViewer, "Sub-Variant", 100, SWT.NONE);
    attrSubVarNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof ProjectImportAttr) {
          ProjectImportAttr<?> item = (ProjectImportAttr<?>) element;
          if (item.getPidcAttr() instanceof PidcSubVariantAttribute) {
            return ((PidcSubVariantAttribute) item.getPidcAttr()).getSubVariantName();

          }
        }
        return "";
      }

      @Override
      public Color getForeground(final Object element) {

        Color red = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
        Color black = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);

        if (element instanceof ProjectImportAttr) {
          ProjectImportAttr<?> item = (ProjectImportAttr<?>) element;
          if (!item.isValidImport()) {
            return red;
          }
          return black;
        }
        return black;
      }
    });
    // Add column selection listener
    attrSubVarNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(attrSubVarNameColumn.getColumn(), 1, this.pidcAttrTabSorter, this.pidcAttrTabViewer));

  }

  /**
  *
  */
  private void createVariantNameColViewer() {
    final GridViewerColumn attrVariantNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.pidcAttrTabViewer, "Variant", 100, SWT.NONE);
    attrVariantNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof ProjectImportAttr) {
          ProjectImportAttr<?> item = (ProjectImportAttr<?>) element;

          if (item.getPidcAttr() instanceof PidcVariantAttribute) {
            return ((PidcVariantAttribute) item.getPidcAttr()).getVariantName();
          }
          else if (item.getPidcAttr() instanceof PidcSubVariantAttribute) {
            return ((PidcSubVariantAttribute) item.getPidcAttr()).getVariantName();
          }
        }
        return "";
      }

      @Override
      public Color getForeground(final Object element) {

        Color red = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
        Color black = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);

        if (element instanceof ProjectImportAttr) {
          ProjectImportAttr<?> item = (ProjectImportAttr<?>) element;
          if (!item.isValidImport()) {
            return red;
          }
          return black;
        }
        return black;
      }
    });
    // Add column selection listener
    attrVariantNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(attrVariantNameColumn.getColumn(), 0, this.pidcAttrTabSorter, this.pidcAttrTabViewer));

  }

  /**
  *
  */
  private void createPIDCAttrNameColViewer() {
    final GridViewerColumn attrNameColumn = GridViewerColumnUtil.getInstance().createGridViewerColumn(
        this.pidcAttrTabViewer, Messages.getString(IMessageConstants.ATTRIBUTE_LABEL), 175, SWT.NONE);
    attrNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof ProjectImportAttr) {
          ProjectImportAttr<?> item = (ProjectImportAttr<?>) element;
          return (item.getPidcAttr()).getName();
        }
        return "";
      }

      @Override
      public Color getForeground(final Object element) {

        Color red = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
        Color black = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);

        if (element instanceof ProjectImportAttr) {
          ProjectImportAttr<?> item = (ProjectImportAttr<?>) element;
          if (!item.isValidImport()) {
            return red;
          }

          return black;
        }
        return black;
      }

    });
    // Add column selection listener
    attrNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(attrNameColumn.getColumn(), 2, this.pidcAttrTabSorter, this.pidcAttrTabViewer));
  }

  /**
  *
  */
  private void createExistingPIDCAttrValColViewer(final GridColumnGroup usedColumnGroup) {

    final GridColumn attrValQColumn = GridColumnUtil.getInstance().createGridColumn(usedColumnGroup, SWT.NONE, 35,
        Messages.getString(IMessageConstants.VALUE_LABEL), false);

    final GridViewerColumn attrValColumn = new GridViewerColumn(this.pidcAttrTabViewer, attrValQColumn);
    attrValQColumn.setWidth(100);

    attrValColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof ProjectImportAttr) {
          ProjectImportAttr<?> item = (ProjectImportAttr<?>) element;
          return item.getPidcAttr().getValue();
        }
        return "";
      }

      /**
       * {@inheritDoc} ICDM-140
       */
      @Override
      public Color getForeground(final Object element) {

        Color red = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
        Color black = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);

        if (element instanceof ProjectImportAttr) {
          ProjectImportAttr<?> item = (ProjectImportAttr<?>) element;
          if (!item.isValidImport()) {
            return red;
          }

          return black;
        }
        return black;
      }
    });


    attrValColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(attrValColumn.getColumn(), 5, this.pidcAttrTabSorter, this.pidcAttrTabViewer));
  }

  /**
  *
  */
  private void createExistingPIDCAttrPartNumColViewer(final GridColumnGroup usedColumnGroup) {

    final GridColumn attrPartQColumn =
        GridColumnUtil.getInstance().createGridColumn(usedColumnGroup, SWT.NONE, 35, "Part Number", false, true);

    final GridViewerColumn attrPartNumColumn = new GridViewerColumn(this.pidcAttrTabViewer, attrPartQColumn);
    attrPartQColumn.setWidth(100);

    attrPartNumColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof ProjectImportAttr) {
          ProjectImportAttr<?> item = (ProjectImportAttr<?>) element;
          return item.getPidcAttr().getPartNumber();
        }
        return "";
      }

      /**
       * {@inheritDoc} ICDM-140
       */
      @Override
      public Color getForeground(final Object element) {

        Color red = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
        Color black = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);

        if (element instanceof ProjectImportAttr) {
          ProjectImportAttr<?> item = (ProjectImportAttr<?>) element;
          if (!item.isValidImport()) {
            return red;
          }

          return black;
        }
        return black;
      }
    });


    attrPartNumColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(attrPartNumColumn.getColumn(), 6, this.pidcAttrTabSorter, this.pidcAttrTabViewer));
  }

  /**
  *
  */
  private void createExistingPIDCAttrSpecLinkColViewer(final GridColumnGroup usedColumnGroup) {

    final GridColumn attrSpecQColumn =
        GridColumnUtil.getInstance().createGridColumn(usedColumnGroup, SWT.NONE, 35, "Spec Link", false, true);

    final GridViewerColumn attrSpecLinkColumn = new GridViewerColumn(this.pidcAttrTabViewer, attrSpecQColumn);
    attrSpecQColumn.setWidth(100);

    attrSpecLinkColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof ProjectImportAttr) {
          ProjectImportAttr<?> item = (ProjectImportAttr<?>) element;
          item.getPidcAttr().getSpecLink();
        }
        return "";
      }

      /**
       * {@inheritDoc} ICDM-140
       */
      @Override
      public Color getForeground(final Object element) {

        Color red = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
        Color black = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);

        if (element instanceof ProjectImportAttr) {
          ProjectImportAttr<?> item = (ProjectImportAttr<?>) element;
          if (!item.isValidImport()) {
            return red;
          }

          return black;
        }
        return black;
      }
    });


    attrSpecLinkColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(attrSpecLinkColumn.getColumn(), 7, this.pidcAttrTabSorter, this.pidcAttrTabViewer));
  }

  /**
  *
  */
  private void createExistingPIDCAttrDescColViewer(final GridColumnGroup usedColumnGroup) {

    final GridColumn attrDescQColumn =
        GridColumnUtil.getInstance().createGridColumn(usedColumnGroup, SWT.NONE, 35, "Desc", false, true);

    final GridViewerColumn attrDescColumn = new GridViewerColumn(this.pidcAttrTabViewer, attrDescQColumn);
    attrDescQColumn.setWidth(100);

    attrDescColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof ProjectImportAttr) {
          ProjectImportAttr<?> item = (ProjectImportAttr<?>) element;
          return item.getPidcAttr().getAdditionalInfoDesc();
        }
        return "";
      }

      /**
       * {@inheritDoc} ICDM-140
       */
      @Override
      public Color getForeground(final Object element) {

        Color red = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
        Color black = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);

        if (element instanceof ProjectImportAttr) {
          ProjectImportAttr<?> item = (ProjectImportAttr<?>) element;

          if (!item.isValidImport()) {
            return red;
          }

          return black;
        }
        return black;
      }
    });


    attrDescColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(attrDescColumn.getColumn(), 8, this.pidcAttrTabSorter, this.pidcAttrTabViewer));
  }

  /**
  *
  */
  private void createImportedPIDCAttrValColViewer(final GridColumnGroup usedColumnGroup) {

    final GridColumn attrValQColumn = GridColumnUtil.getInstance().createGridColumn(usedColumnGroup, SWT.NONE, 35,
        Messages.getString(IMessageConstants.VALUE_LABEL), false);

    final GridViewerColumn attrValColumn = new GridViewerColumn(this.pidcAttrTabViewer, attrValQColumn);
    attrValQColumn.setWidth(100);

    attrValColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof ProjectImportAttr) {
          ProjectImportAttr<?> item = (ProjectImportAttr<?>) element;
          return item.getExcelAttr().getValue();
        }
        return "";
      }

      /**
       * {@inheritDoc} ICDM-140
       */
      @Override
      public Color getForeground(final Object element) {

        Color red = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
        Color black = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
        Color orange = new Color(Display.getCurrent(), 255, 128, 0);

        if (element instanceof ProjectImportAttr) {
          ProjectImportAttr<?> item = (ProjectImportAttr<?>) element;
          if (!item.isValidImport()) {
            return red;
          }
          if (!item.isCleared()) {
            return orange;
          }

          return black;
        }
        return black;
      }
    });


    attrValColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(attrValColumn.getColumn(), 10, this.pidcAttrTabSorter, this.pidcAttrTabViewer));
  }

  /**
  *
  */
  private void createImportedPIDCAttrPartNumColViewer(final GridColumnGroup usedColumnGroup) {

    final GridColumn attrPartQColumn =
        GridColumnUtil.getInstance().createGridColumn(usedColumnGroup, SWT.NONE, 35, "Part Number", false, true);

    final GridViewerColumn attrPartColumn = new GridViewerColumn(this.pidcAttrTabViewer, attrPartQColumn);
    attrPartQColumn.setWidth(100);

    attrPartColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof ProjectImportAttr) {
          ProjectImportAttr<?> item = (ProjectImportAttr<?>) element;
          return item.getExcelAttr().getPartNumber();
        }
        return "";
      }

      /**
       * {@inheritDoc} ICDM-140
       */
      @Override
      public Color getForeground(final Object element) {

        Color red = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
        Color black = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);

        if (element instanceof ProjectImportAttr) {
          ProjectImportAttr<?> item = (ProjectImportAttr<?>) element;

          if (!item.isValidImport()) {
            return red;
          }

          return black;
        }
        return black;
      }
    });


    attrPartColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(attrPartColumn.getColumn(), 11, this.pidcAttrTabSorter, this.pidcAttrTabViewer));
  }

  /**
  *
  */
  private void createImportedPIDCAttrSpecLinkColViewer(final GridColumnGroup usedColumnGroup) {

    final GridColumn attrSpecQColumn =
        GridColumnUtil.getInstance().createGridColumn(usedColumnGroup, SWT.NONE, 35, "Spec Link", false, true);

    final GridViewerColumn attrSpecColumn = new GridViewerColumn(this.pidcAttrTabViewer, attrSpecQColumn);
    attrSpecQColumn.setWidth(100);

    attrSpecColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof ProjectImportAttr) {
          ProjectImportAttr<?> item = (ProjectImportAttr<?>) element;
          return item.getExcelAttr().getSpecLink();
        }
        return "";
      }

      /**
       * {@inheritDoc} ICDM-140
       */
      @Override
      public Color getForeground(final Object element) {

        Color red = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
        Color black = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);

        if (element instanceof ProjectImportAttr) {
          ProjectImportAttr<?> item = (ProjectImportAttr<?>) element;
          if (!item.isValidImport()) {
            return red;
          }

          return black;
        }
        return black;
      }
    });


    attrSpecColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(attrSpecColumn.getColumn(), 12, this.pidcAttrTabSorter, this.pidcAttrTabViewer));
  }

  /**
  *
  */
  private void createImportedPIDCAttrDescColViewer(final GridColumnGroup usedColumnGroup) {

    final GridColumn attrDescQColumn =
        GridColumnUtil.getInstance().createGridColumn(usedColumnGroup, SWT.NONE, 35, "Desc", false, true);

    final GridViewerColumn attrDescColumn = new GridViewerColumn(this.pidcAttrTabViewer, attrDescQColumn);
    attrDescQColumn.setWidth(100);

    attrDescColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof ProjectImportAttr) {
          ProjectImportAttr<?> item = (ProjectImportAttr<?>) element;
          return item.getExcelAttr().getAdditionalInfoDesc();
        }
        return "";
      }

      /**
       * {@inheritDoc} ICDM-140
       */
      @Override
      public Color getForeground(final Object element) {

        Color red = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
        Color black = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);

        if (element instanceof ProjectImportAttr) {
          ProjectImportAttr<?> item = (ProjectImportAttr<?>) element;

          if (!item.isValidImport()) {
            return red;
          }

          return black;
        }
        return black;
      }
    });


    attrDescColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(attrDescColumn.getColumn(), 13, this.pidcAttrTabSorter, this.pidcAttrTabViewer));
  }

  /**
   * This method creates PIDC attribute used table viewer column
   */
  private void createPIDCAttrUsedColViewer() {
    final GridColumnGroup usedColumnGroupExisting = new GridColumnGroup(this.pidcAttrTabViewer.getGrid(), SWT.TOGGLE);
    usedColumnGroupExisting.setText("Existing");
    createExistingAttrUsedSummaryViewerColumn(usedColumnGroupExisting);
    createExistingPIDCAttrValColViewer(usedColumnGroupExisting);
    createExistingPIDCAttrPartNumColViewer(usedColumnGroupExisting);
    createExistingPIDCAttrSpecLinkColViewer(usedColumnGroupExisting);
    createExistingPIDCAttrDescColViewer(usedColumnGroupExisting);
    usedColumnGroupExisting.setExpanded(true);

    final GridColumnGroup usedColumnGroupImported = new GridColumnGroup(this.pidcAttrTabViewer.getGrid(), SWT.TOGGLE);
    usedColumnGroupImported.setText("Import");
    createImportedAttrUsedSummaryViewerColumn(usedColumnGroupImported);
    createImportedPIDCAttrValColViewer(usedColumnGroupImported);
    createImportedPIDCAttrPartNumColViewer(usedColumnGroupImported);
    createImportedPIDCAttrSpecLinkColViewer(usedColumnGroupImported);
    createImportedPIDCAttrDescColViewer(usedColumnGroupImported);
    usedColumnGroupImported.setExpanded(true);
  }

  /**
  *
  */
  private void createExistingAttrUsedSummaryViewerColumn(final GridColumnGroup usedColumnGroup) {
    final GridColumn attrUsedSummaryColumn = GridColumnUtil.getInstance().createGridColumn(usedColumnGroup, SWT.NONE,
        20, Messages.getString(IMessageConstants.USED_LABEL), false);
    attrUsedSummaryColumn.setWidth(50);

    GridViewerColumn attrUnKnownInfoViewerCol = new GridViewerColumn(this.pidcAttrTabViewer, attrUsedSummaryColumn);
    attrUnKnownInfoViewerCol.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof ProjectImportAttr) {
          ProjectImportAttr<?> item = (ProjectImportAttr<?>) element;
          return item.getPidcAttr().getUsedFlag();
        }
        return "";
      }

      @Override
      public Color getForeground(final Object element) {

        Color red = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
        Color black = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);

        if (element instanceof ProjectImportAttr) {
          ProjectImportAttr<?> item = (ProjectImportAttr<?>) element;
          if (!item.isValidImport()) {
            return red;
          }

          return black;
        }
        return black;
      }
    });
    attrUsedSummaryColumn.addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(attrUsedSummaryColumn, 4, this.pidcAttrTabSorter, this.pidcAttrTabViewer));

  }

  /**
  *
  */
  private void createImportedAttrUsedSummaryViewerColumn(final GridColumnGroup usedColumnGroup) {
    final GridColumn attrUsedSummaryColumn = GridColumnUtil.getInstance().createGridColumn(usedColumnGroup, SWT.NONE,
        20, Messages.getString(IMessageConstants.USED_LABEL), false);
    attrUsedSummaryColumn.setWidth(50);

    GridViewerColumn attrUnKnownInfoViewerCol = new GridViewerColumn(this.pidcAttrTabViewer, attrUsedSummaryColumn);
    attrUnKnownInfoViewerCol.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof ProjectImportAttr) {
          ProjectImportAttr<?> item = (ProjectImportAttr<?>) element;
          return item.getExcelAttr().getUsedFlag();
        }
        return "";
      }

      @Override
      public Color getForeground(final Object element) {

        Color red = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
        Color black = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);

        if (element instanceof ProjectImportAttr) {
          ProjectImportAttr<?> item = (ProjectImportAttr<?>) element;

          if (!item.isValidImport()) {
            return red;
          }

          return black;
        }
        return black;
      }
    });
    attrUsedSummaryColumn.addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(attrUsedSummaryColumn, 9, this.pidcAttrTabSorter, this.pidcAttrTabViewer));

  }

  /**
  *
  */
  private void createPIDCAttrStatusColViewer() {
    final GridViewerColumn attrNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.pidcAttrTabViewer, "Status", 70, SWT.NONE);
    attrNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof ProjectImportAttr) {
          ProjectImportAttr<?> item = (ProjectImportAttr<?>) element;
          if (item.isNewlyAddedVal()) {
            return "NEW VALUE";
          }
          return "MODIFIED";
        }
        return "";
      }

      @Override
      public Color getForeground(final Object element) {

        Color red = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
        Color black = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);

        if (element instanceof ProjectImportAttr) {
          ProjectImportAttr<?> item = (ProjectImportAttr<?>) element;

          if (!item.isValidImport()) {
            return red;
          }

          return black;
        }
        return black;
      }
    });
    // Add column selection listener
    attrNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(attrNameColumn.getColumn(), 14, this.pidcAttrTabSorter, this.pidcAttrTabViewer));
  }

  private void createPIDCAttrCommentsColViewer() {
    final GridViewerColumn attrNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.pidcAttrTabViewer, "Comments", 200, SWT.NONE);
    attrNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof ProjectImportAttr) {
          ProjectImportAttr<?> item = (ProjectImportAttr<?>) element;
          return item.getComment();
        }
        return "";
      }

      @Override
      public Color getForeground(final Object element) {

        Color red = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
        Color black = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);

        if (element instanceof ProjectImportAttr) {
          ProjectImportAttr<?> item = (ProjectImportAttr<?>) element;
          if (!item.isValidImport()) {
            return red;
          }

          return black;
        }
        return black;
      }
    });
    // Add column selection listener
    attrNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(attrNameColumn.getColumn(), 15, this.pidcAttrTabSorter, this.pidcAttrTabViewer));
  }

  /**
  *
  */
  private void createPIDCAttrDescColViewer() {
    final GridViewerColumn attrDescColumn = GridViewerColumnUtil.getInstance().createGridViewerColumn(
        this.pidcAttrTabViewer, Messages.getString(IMessageConstants.DESCRIPTION_LABEL), 175, SWT.NONE);
    attrDescColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof ProjectImportAttr) {
          ProjectImportAttr<?> item = (ProjectImportAttr<?>) element;
          return (item.getAttr()).getDescription();
        }
        return "";

      }

      @Override
      public Color getForeground(final Object element) {

        Color red = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
        Color black = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);

        if (element instanceof ProjectImportAttr) {
          ProjectImportAttr<?> item = (ProjectImportAttr<?>) element;
          if (!item.isValidImport()) {
            return red;
          }

          return black;
        }
        return black;
      }
    });
    // Add column selection listener
    attrDescColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(attrDescColumn.getColumn(), 3, this.pidcAttrTabSorter, this.pidcAttrTabViewer));
  }


}
