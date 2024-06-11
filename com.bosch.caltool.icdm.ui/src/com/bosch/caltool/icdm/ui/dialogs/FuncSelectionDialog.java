/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.dialogs;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.ws.rest.client.a2l.FunctionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * @author dja7cob
 */
public class FuncSelectionDialog extends AbstractDialog {

  /**
   * AddValidityAttrValDialog Title
   */
  private static final String DIALOG_TITLE = "Select Function";
  /**
   * AddValidityAttrValDialog width
   */
  private static final int DIALOG_WIDTH = 600;
  /**
   * AddValidityAttrValDialog height
   */
  private static final int DIALOG_NORM_HEIGHT = 650;
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
   * Button instance
   */
  private Button saveBtn;
  /**
   * Button instance
   */
  private Button cancelBtn;
  /**
   * Section instance
   */
  private Section section;
  private Form form;
  private Text searchTxt;
  private String selectedText;
  private Button searchButton;
  private GridTableViewer funcListTabViewer;
  private boolean isFc2WP;
  private Set<String> existingFuncs;
  private EditFC2WPMappingDialog editFC2WPMappingDialog;
  private final Set<Function> selFunctions = new HashSet<>();

  /**
   * @param parentShell
   */
  public FuncSelectionDialog(final Shell parentShell) {
    super(parentShell);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param activeShell
   * @param isFc2WP
   * @param set
   * @param editFC2WPMappingDialog
   */
  public FuncSelectionDialog(final Shell activeShell, final boolean isFc2WP, final Set<String> set,
      final EditFC2WPMappingDialog editFC2WPMappingDialog) {
    super(activeShell);
    this.isFc2WP = isFc2WP;
    this.existingFuncs = set;
    this.editFC2WPMappingDialog = editFC2WPMappingDialog;
  }

  /**
   * Creates the dialog's contents
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createContents(final Composite parent) {
    Control contents = super.createContents(parent);

    // Set the title
    setTitle(DIALOG_TITLE);
    // Set the message
    setMessage("Select Functions starting with");
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText(DIALOG_TITLE);

    // Constants for the Width and height
    final Point newSize = newShell.computeSize(DIALOG_WIDTH, DIALOG_NORM_HEIGHT, true);
    newShell.setSize(newSize);
    newShell.layout(true, true);
    super.configureShell(newShell);
  }

  /**
   * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse .swt.widgets.Composite)
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    // NA
  }

  /**
   * ok pressed
   */
  @Override
  protected void okPressed() {
    // On ok pressed
    if (FuncSelectionDialog.this.isFc2WP) {
      StringBuilder funcNameSb = new StringBuilder();
      int selFuncCount = FuncSelectionDialog.this.selFunctions.size();
      for (Function func : FuncSelectionDialog.this.selFunctions) {
        FuncSelectionDialog.this.editFC2WPMappingDialog.getNewFuncDetailsSet().add(func);
        funcNameSb.append(func.getName());
        if (selFuncCount > 1) {
          funcNameSb.append("; ");
        }
      }
      FuncSelectionDialog.this.editFC2WPMappingDialog.getFuncText().setText(funcNameSb.toString());
    }
    super.okPressed();
  }

  /**
   * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets .Composite)
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    // create composite
    createComposite();
    // create buttons
    createButtons(this.top);
    parent.layout(true, true);
    return this.top;
  }

  /**
   * This method initializes formToolkit
   *
   * @return org.eclipse.ui.forms.widgets.FormToolkit
   */
  protected FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }

  /**
   * This method initializes composite
   */
  private void createComposite() {
    GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite = getFormToolkit().createComposite(this.top);
    GridLayout layout = new GridLayout();
    this.composite.setLayout(layout);
    this.composite.setLayoutData(gridData);
    createTableSection();
  }

  /**
  *
  */
  private void createTableSection() {

    GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    this.section = SectionUtil.getInstance().createSection(this.composite, this.formToolkit, "Select Function");
    this.section.getDescriptionControl().setEnabled(true);
    this.section
        .setDescription("Enter a part of a function name or the complete function name and press the search icon");
    this.section.setLayoutData(gridData);
    // create table form
    createTableForm();
    this.section.setClient(this.form);
  }

  /**
   *
   */
  private void createTableForm() {
    this.form = this.formToolkit.createForm(this.section);
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    this.form.getBody().setLayout(gridLayout);
    // create filter text for the table
    createSearchTxt();
    createSearchButton();
    // craete table to display the list of functions
    createFuncListTable();
    // add filter to the table

    addTableSelectionListener();
    addDoubleClickListener();
  }

  /**
   * This method add doubleclick listener to TableViewer
   */
  private void addDoubleClickListener() {
    this.funcListTabViewer.addDoubleClickListener(new IDoubleClickListener() {

      @Override
      public void doubleClick(final DoubleClickEvent doubleclickevent) {
        Display.getDefault().asyncExec(new Runnable() {

          @Override
          public void run() {
            getSelFuncFromTabViewer();
            validateSave();
            okPressed();
          }
        });
      }

    });
  }

  /**
   * This method add selection listener to TableViewer
   */
  private void addTableSelectionListener() {
    this.funcListTabViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * Table selection
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        getSelFuncFromTabViewer();
        validateSave();
      }
    });
  }

  private void validateSave() {
    this.saveBtn.setEnabled(CommonUtils.isNotEmpty(this.selFunctions));
  }

  private void getSelFuncFromTabViewer() {
    this.selFunctions.clear();
    final IStructuredSelection selection = (IStructuredSelection) this.funcListTabViewer.getSelection();
    if ((selection != null) && (selection.size() != 0)) {
      final List<IStructuredSelection> elementList = selection.toList();
      if (elementList.get(0) instanceof Function) {
        this.selFunctions.addAll((Collection<? extends Function>) elementList);
      }
    }
  }

  /**
   *
   */
  private void createSearchButton() {
    this.searchButton = new Button(this.form.getBody(), SWT.PUSH);
    this.searchButton.setToolTipText("Search");
    this.searchButton.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.FUNC_SEARCH_28X30));
    this.searchButton.setEnabled(false);
    this.searchButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final String searchText = FuncSelectionDialog.this.selectedText;
        FuncSelectionDialog.this.funcListTabViewer.getGrid().removeAll();
        if (FuncSelectionDialog.this.isFc2WP) {
          FunctionServiceClient service = new FunctionServiceClient();

          Set<Function> funcList = new HashSet<>();
          try {
            funcList = service.getSearchFunctions(searchText);
          }
          catch (ApicWebServiceException e) {
            CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
          }
          if (funcList.isEmpty()) {
            MessageDialog.openWarning(Display.getCurrent().getActiveShell(), " Info:", "No Results found !");

          }
          else {
            Set<Function> tableInput = new HashSet<>();
            for (Function func : funcList) {
              if (!FuncSelectionDialog.this.existingFuncs.contains(func.getName())) {
                tableInput.add(func);
              }
            }
            if (!tableInput.isEmpty()) {
              FuncSelectionDialog.this.funcListTabViewer.setInput(tableInput);
            }
            else {
              MessageDialog.openWarning(Display.getCurrent().getActiveShell(), " Info:", "No Results found !");
            }
          }
        }
      }
    });

  }

  /**
  *
  */
  private void createSearchTxt() {
    this.searchTxt = TextUtil.getInstance().createFilterText(this.formToolkit, this.form.getBody(),
        GridDataUtil.getInstance().getTextGridData(), "Type Filter Text");
    this.searchTxt.addModifyListener(new ModifyListener() {

      /**
       *
       */
      @Override
      public void modifyText(final ModifyEvent event) {
        FuncSelectionDialog.this.selectedText = FuncSelectionDialog.this.searchTxt.getText().trim();
        FuncSelectionDialog.this.funcListTabViewer.refresh();
        FuncSelectionDialog.this.searchButton.setEnabled(!FuncSelectionDialog.this.selectedText.isEmpty());
      }
    });
    this.searchTxt.addKeyListener(new KeyAdapter() {

      @Override
      public void keyPressed(final KeyEvent arg0) {
        if (arg0.character == SWT.CR) {
          FuncSelectionDialog.this.searchButton.notifyListeners(SWT.Selection, new Event());
        }
      }
    });
    this.searchTxt.setFocus();
  }

  /**
   * Create validity value table
   */
  private void createFuncListTable() {
    this.funcListTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(),
        SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER | SWT.V_SCROLL,
        GridDataUtil.getInstance().getHeightHintGridData(350));
    this.funcListTabViewer.getGrid().setLayoutData(GridDataUtil.getInstance().getGridData());
    this.funcListTabViewer.getGrid().setLinesVisible(true);
    this.funcListTabViewer.getGrid().setHeaderVisible(true);
    // Create sorter for the table

    this.funcListTabViewer.setContentProvider(new ArrayContentProvider());
    // Create GridViewerColumns
    createFuncListColumn();
    // Set input to the table
  }

  /**
   * Create column for validity value description
   */
  private void createFuncListColumn() {
    final GridViewerColumn funcColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.funcListTabViewer, "Function", 250);
    // Add column selection listener
    // Label provider for validity value description column
    funcColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof Function) {
          final Function func = (Function) element;
          return func.getName();
        }
        return null;
      }
    });
  }

  /**
   * ICDM 574-This method defines the activities to be performed when double clicked on the table
   *
   * @param tableviewer tableviewer
   */
  protected void addDoubleClickListener(final GridTableViewer tableviewer) {
    tableviewer.addDoubleClickListener(new IDoubleClickListener() {

      @Override
      public void doubleClick(final DoubleClickEvent doubleclickevent) {
        Display.getDefault().asyncExec(new Runnable() {

          @Override
          public void run() {
            okPressed();
          }
        });
      }

    });
  }

  /**
   * Create common Buttons
   *
   * @param comp parent composite
   */
  private void createButtons(final Composite comp) {
    final Composite buttonGroup = new Composite(comp, SWT.NONE);
    final GridLayout layout = new GridLayout();
    layout.numColumns = 3;
    layout.makeColumnsEqualWidth = false;
    layout.marginWidth = 0;

    buttonGroup.setLayout(layout);
    final GridData gridData1 = new GridData();
    gridData1.grabExcessHorizontalSpace = true;
    gridData1.horizontalAlignment = SWT.END;

    buttonGroup.setLayoutData(gridData1);

    final GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
    gridData.widthHint = 92;
    this.saveBtn = new Button(buttonGroup, SWT.PUSH);
    this.saveBtn.setLayoutData(gridData);
    this.saveBtn.setText("OK");
    this.saveBtn.setFont(JFaceResources.getDialogFont());
    this.saveBtn.addSelectionListener(new SelectionAdapter() {

      /**
       *
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {

        okPressed();
      }
    });
    this.saveBtn.setEnabled(false);

    this.cancelBtn = new Button(buttonGroup, SWT.PUSH);
    this.cancelBtn.setLayoutData(gridData);
    this.cancelBtn.setText("Cancel");
    this.cancelBtn.setFont(JFaceResources.getDialogFont());
    this.cancelBtn.addSelectionListener(new SelectionAdapter() {

      /**
       *
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        cancelPressed();
      }
    });
  }
}
