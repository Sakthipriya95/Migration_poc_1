/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comppkg.ui.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.comppkg.ui.Activator;
import com.bosch.caltool.comppkg.ui.editors.pages.ComponentDetailsPage;
import com.bosch.caltool.comppkg.ui.sorters.BCFCSorter;
import com.bosch.caltool.comppkg.ui.table.filters.BCFCTableFilter;
import com.bosch.caltool.icdm.client.bo.comppkg.ComponentPackageEditorDataHandler;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.bc.SdomBc;
import com.bosch.caltool.icdm.model.bc.SdomFc;
import com.bosch.caltool.icdm.model.comppkg.CompPkgBc;
import com.bosch.caltool.icdm.model.comppkg.CompPkgFc;
import com.bosch.caltool.icdm.ws.rest.client.bc.SdomFcServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * ICDM-748
 *
 * @author bru2cob
 */
public class AddBcFcDialog extends AbstractDialog {

  /**
   * title for BC
   */
  private static final String TITLE_BC = "Select Base Component";
  /**
   * title for FC
   */
  private static final String TITLE_FC = "Select Function";

  /**
   * filter text
   */
  private static final String TYPE_FILTER_LABEL = "type filter text";

  /**
   * to add new BC
   */
  private final boolean isBcFlag;

  /**
   * to add new FC
   */
  private final boolean isFcFlag;

  /**
   * Composite instance
   */
  private Composite composite;
  /**
   * Composite instance
   */
  private Composite top;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;

  /**
   * Section instance
   */
  private Section section;
  /**
   * Add new user button instance
   */
  private Button okBtn;

  /**
   * Form instance
   */
  private Form form;
  /**
   * Filter text instance
   */
  private Text filterTxt;
  /**
   * GridTableViewer instance for selection of variant or workpackage
   */
  private GridTableViewer bcFcTbleViewer;
  /**
   * sdomBC instance
   */
  protected SdomBc sdomBC;
  /**
   * detailsPage instance
   */
  private final ComponentDetailsPage detailsPage;
  /**
   * sdomBCFC instance
   */
  private SdomFc sdomBCFC;

  private ArrayList<SdomFc> selectedFCs = new ArrayList<>();

  // new Filter for the Bc Fc Contents

  private BCFCTableFilter bcFcFilter;
  // new sorter for the Bc Fc Contents
  private BCFCSorter tabSorter;

  private final ComponentPackageEditorDataHandler pkgBO;

  private final Long newBCSeqNo;

  /**
   * @param parentShell shell
   * @param isBcFlag flag for BC
   * @param isFcFlags flag for FC
   * @param detailsPage page instance
   */
  public AddBcFcDialog(final Shell parentShell, final boolean isBcFlag, final boolean isFcFlags,
      final ComponentDetailsPage detailsPage) {
    super(parentShell);
    this.isBcFlag = isBcFlag;
    this.isFcFlag = isFcFlags;
    this.detailsPage = detailsPage;
    this.pkgBO = detailsPage.getDataHandler();
    this.newBCSeqNo = 0L;
  }

  /**
   * Creates the dialog's contents
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set the title and message
    if (this.isBcFlag) {
      setTitle(AddBcFcDialog.TITLE_BC);
      setMessage(AddBcFcDialog.TITLE_BC, IMessageProvider.INFORMATION);
    }
    else if (this.isFcFlag) {
      setTitle(AddBcFcDialog.TITLE_FC);
      setMessage(AddBcFcDialog.TITLE_FC, IMessageProvider.INFORMATION);
    }
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    if (this.isBcFlag) {
      newShell.setText(AddBcFcDialog.TITLE_BC);
    }
    else if (this.isFcFlag) {
      newShell.setText(AddBcFcDialog.TITLE_FC);
    }

    super.configureShell(newShell);
    super.setHelpAvailable(true);
  }

  /**
   * Creates the gray area
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());

    createComposite();
    return this.top;
  }

  /**
   * This method initializes composite
   */
  private void createComposite() {
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    createSection();
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
  }

  /**
   * This method initializes formToolkit
   *
   * @return org.eclipse.ui.forms.widgets.FormToolkit
   */
  private FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }

  /**
   * This method initializes section
   */
  private void createSection() {
    if (this.isBcFlag) {
      this.section =
          SectionUtil.getInstance().createSection(this.composite, getFormToolkit(), "Available Base Components");

    }
    else if (this.isFcFlag) {
      this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(), "Available Functions");
    }

    createForm();
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());

    this.section.setClient(this.form);
  }

  @Override
  protected void createButtonsForButtonBar(final Composite parent) {

    this.okBtn = createButton(parent, IDialogConstants.OK_ID, "Select", true);
    this.okBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  /**
   * This method initializes form
   */
  private void createForm() {
    this.form = getFormToolkit().createForm(this.section);
    // Create Filter text
    createFilterTxt();
    // Create new users grid tableviewer
    createbcFcGridTabViewer();

    // Add selection listener to the addNewUserTableViewer
    addTableSelectionListener();


    // Adds double click selection listener to the variantTableViewer
    addDoubleClickListener();

    // Create column sorter
    invokeColumnSorter();

    // add coulmn filters
    addFilters();

    this.form.getBody().setLayout(new GridLayout());

  }


  /**
   * This method creates filter text
   */
  private void createFilterTxt() {
    final GridData gridData = GridDataUtil.getInstance().createGridData();
    this.filterTxt = TextUtil.getInstance().createFilterText(getFormToolkit(), this.form.getBody(), gridData,
        AddBcFcDialog.TYPE_FILTER_LABEL);

    this.filterTxt.addModifyListener(event -> {
      final String text = AddBcFcDialog.this.filterTxt.getText().trim();
      AddBcFcDialog.this.bcFcFilter.setFilterText(text);
      AddBcFcDialog.this.bcFcTbleViewer.refresh();
    });

    this.filterTxt.setFocus();

  }

  /**
   * This method adds the filter instance to bcFcTableViewer
   */
  private void addFilters() {
    this.bcFcFilter = new BCFCTableFilter();
    this.bcFcTbleViewer.addFilter(this.bcFcFilter);

  }

  /**
   * This method adds selection listener to the addNewUserTableViewer
   */
  private void addTableSelectionListener() {
    this.bcFcTbleViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        IStructuredSelection selection = (IStructuredSelection) AddBcFcDialog.this.bcFcTbleViewer.getSelection();
        if ((selection != null) && (selection.size() != 0)) {
          if (AddBcFcDialog.this.isBcFlag) {
            final Object element = selection.getFirstElement();
            if (element instanceof SdomBc) {
              AddBcFcDialog.this.sdomBC = (SdomBc) element;
              AddBcFcDialog.this.okBtn.setEnabled(true);

            }
          }
          else if (AddBcFcDialog.this.isFcFlag) {
            AddBcFcDialog.this.selectedFCs = new ArrayList<>();
            List<?> selectedElements = selection.toList();
            if ((null != selectedElements) && !selectedElements.isEmpty()) {
              for (Object element : selectedElements) {
                if (element instanceof SdomFc) {
                  AddBcFcDialog.this.sdomBCFC = (SdomFc) element;
                  AddBcFcDialog.this.okBtn.setEnabled(true);
                  AddBcFcDialog.this.selectedFCs.add(AddBcFcDialog.this.sdomBCFC);
                }
              }

            }
          }
        }
      }
    });


  }

  /**
   * This method creates the variantTableViewer
   *
   * @param gridData
   */
  private void createbcFcGridTabViewer() {
    final GridData gridData = GridDataUtil.getInstance().createGridData();
    gridData.heightHint = 150;
    if (this.isBcFlag) {
      this.bcFcTbleViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(),
          SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL, gridData);
    }
    else if (this.isFcFlag) {
      this.bcFcTbleViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(),
          SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI, gridData);
    }
    this.bcFcTbleViewer.setContentProvider(new ArrayContentProvider());

    // Create GridViewerColumns
    createGridViewerColumns();

    if (this.isBcFlag) {
      this.bcFcTbleViewer.setInput(this.detailsPage.getDataHandler().getSdomBcSet());
    }
    else if (this.isFcFlag) {

      SdomFcServiceClient fc = new SdomFcServiceClient();
      try {
        List<String> fcList = fc.getSDOMFcByBCName(this.detailsPage.getSelCompPkgBC().getBcName());
        Collections.sort(fcList);
        SortedSet<SdomFc> retSet = new TreeSet<>();
        for (String dbFC : fcList) {
          retSet.add(new SdomFc(dbFC, dbFC));
        }
        this.bcFcTbleViewer.setInput(retSet);
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * This method adds Columns to the variantTableViewer
   *
   * @param attrMap
   */
  private void createGridViewerColumns() {
    createNameColumn();
    createDescriptionColumn();


  }

  /**
   * Description colunm
   */
  private void createDescriptionColumn() {
    GridViewerColumn descColumn;
    descColumn = GridViewerColumnUtil.getInstance().createGridViewerColumn(this.bcFcTbleViewer, "Long Name ", 300);

    descColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof SdomFc) {
          return ((SdomFc) element).getName();
        }
        else if (element instanceof SdomBc) {
          return ((SdomBc) element).getDescription();
        }
        return "";
      }
    });
    // Add column selection listener
    descColumn.getColumn().addSelectionListener(getSelectionAdapter(descColumn.getColumn(), 1, this.bcFcTbleViewer));

  }

  /**
   * This method adds name column to the bcFcTableViewer
   *
   * @param attrMap
   */
  private void createNameColumn() {

    GridViewerColumn nameColumn;
    String name = ApicConstants.EMPTY_STRING;

    if (this.isBcFlag) {
      name = "Base Component Name";
    }
    else if (this.isFcFlag) {
      name = "Function Name";
    }

    nameColumn = GridViewerColumnUtil.getInstance().createGridViewerColumn(this.bcFcTbleViewer, name, 150);
    nameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof SdomFc) {
          return ((SdomFc) element).getName();
        }
        else if (element instanceof SdomBc) {
          return ((SdomBc) element).getName();
        }
        return "";
      }
    });
    // Add column selection listener
    nameColumn.getColumn().addSelectionListener(getSelectionAdapter(nameColumn.getColumn(), 0, this.bcFcTbleViewer));
  }


  /**
   * This method returns SelectionAdapter instance
   *
   * @param column
   * @param index
   * @return SelectionAdapter
   */
  private SelectionAdapter getSelectionAdapter(final GridColumn column, final int index,
      final GridTableViewer gridTable) {

    return new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        AddBcFcDialog.this.tabSorter.setColumn(index);
        int direction = AddBcFcDialog.this.tabSorter.getDirection();
        for (int i = 0; i < gridTable.getGrid().getColumnCount(); i++) {
          if (i == index) {
            if (direction == 0) {
              column.setSort(SWT.DOWN);
            }
            else if (direction == 1) {
              column.setSort(SWT.UP);
            }
          }
          if (i != index) {
            gridTable.getGrid().getColumn(i).setSort(SWT.NONE);
          }
        }
        gridTable.refresh();
      }
    };
  }

  /**
   * Add sorter for the table columns
   */
  private void invokeColumnSorter() {
    this.tabSorter = new BCFCSorter();
    this.bcFcTbleViewer.setComparator(this.tabSorter);
  }


  /**
   * This method defines the activities to be performed when double clicked on the table
   *
   * @param functionListTableViewer2
   */
  private void addDoubleClickListener() {
    this.bcFcTbleViewer.addDoubleClickListener(event -> Display.getDefault().asyncExec(AddBcFcDialog.this::okPressed));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    if (this.isBcFlag) {
      CompPkgBc pkgBc = new CompPkgBc();
      pkgBc.setBcName(this.sdomBC.getName());
      pkgBc.setCompPkgId(this.detailsPage.getSelectedCmpPkg().getId());
      Long seqNo = null;
      if ((null != this.detailsPage.getPkgBcFc().getBcSet()) && this.detailsPage.getPkgBcFc().getBcSet().isEmpty()) {
        seqNo = this.newBCSeqNo + 1;
      }
      else if ((null != this.detailsPage.getPkgBcFc().getBcSet()) &&
          !this.detailsPage.getPkgBcFc().getBcSet().isEmpty()) {
        final SortedSet<CompPkgBc> setCmpPkgBcs = new TreeSet<>(this.detailsPage.getPkgBcFc().getBcSet());
        seqNo = setCmpPkgBcs.last().getBcSeqNo() + 1;
      }
      pkgBc.setBcSeqNo(seqNo);
      try {
        this.pkgBO.insertBC(pkgBc);
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
    }
    else if (this.isFcFlag) {
      for (SdomFc sdomFC : this.selectedFCs) {

        CompPkgFc compPkgFc = new CompPkgFc();
        compPkgFc.setFcName(sdomFC.getName());
        compPkgFc.setCompBcId(this.detailsPage.getSelCompPkgBC().getId());
        try {
          this.pkgBO.insertFC(compPkgFc);
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
        }
      }
    }
    super.okPressed();

  }
}
