/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.actions.AssignVarNamesToolBarActionSet;
import com.bosch.caltool.apic.ui.actions.PIDCActionSet;
import com.bosch.caltool.apic.ui.sorter.AssignVariantNamesTabSorter;
import com.bosch.caltool.apic.ui.table.filters.AssignVarNameToolBarFilters;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.icdm.client.bo.apic.ApicDataBO;
import com.bosch.caltool.icdm.client.bo.apic.AssignCustomerCDMHandler;
import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.client.bo.apic.pidc.ProjectAttributesMovementModel;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.dialogs.FileDialogHandler;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.CretaFteFilesParserHandler;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.logger.ParserLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * Task 233015
 *
 * @author dmo5cob
 */
public class AssignCustVarsToICDMVarsDialog extends AbstractDialog {

  /**
   * String constant for Assigned
   */
  private static final String ASSIGNED = "Assigned";

  /**
   * Dialog title
   */
  private static final String DIALOG_TITLE = "Assign variant names";

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
   * Form instance
   */
  private Form form;
  /**
   * Table viewer to display all the var names from file
   */
  private GridTableViewer varNameListFromFileTableViewer;
  /**
   * Table viewer to display all the var names from icdm
   */
  private GridTableViewer varNameListFromiCDMTableViewer;

  /**
   * Table Sorter for allAttrListTableViewer
   */
  private AssignVariantNamesTabSorter btmTabSorter;
  /**
   * Text instance
   */
  private Text filterTxt;
  /**
   * Button instance
   */
  private Button assignBtn;
  /**
   * table filter
   */
  private ViewerFilter leftTableFilter;
  private ViewerFilter rightTableFilter;
  private ViewerFilter bottomTableFilter;

  private Section btmSection;
  private Form btmForm;
  private GridTableViewer existingAssgnmtsTableViewer;
  private Text rghtfilterTxt;
  private SashForm sashForm;
  private Action addAction;
  /**
   * File selected in File selection dialog
   */
  protected String fileSelected[];
  private final PidcVersionBO pidcVersionBO;
  private Text leftSelectedText;
  private Text rightSelectedText;
  private final TreeViewer pidcDetailsTreeViewer;
  private static final String fileNames[] =
      new String[] { "All Files FTE/Creta(*.csv,*.fte)", "CRETA files(*.csv)", "FTE Files (*.fte)" };
  /**
   * String[] having extn of files
   */
  private static final String fileExtensions[] = new String[] { "*.csv;*.fte", "*.csv", "*.fte" };
  /**
   * selected variant name in left table viewer
   */
  protected String selCDMVarName;
  /**
   * selected variant name in right table viewer
   */
  protected PidcVariant seliCDMVarName;
  private Text filterTxtExistingAssgnmts;
  private AssignVariantNamesTabSorter rightTabSorter;
  private AssignVariantNamesTabSorter leftTabSorter;

  private AssignVarNamesToolBarActionSet toolBarActionSet;
  private final AssignCustomerCDMHandler cdmHandler;

  /**
   * @param parentShell instance
   * @param pidcVersion pidcversion object
   * @param pidcDetailsTreeViewer Treeviewer instance from PIDCDetailsViewPart
   */
  public AssignCustVarsToICDMVarsDialog(final Shell parentShell, final PidcVersionBO pidcVersionBO,
      final TreeViewer pidcDetailsTreeViewer) {
    super(parentShell);
    this.pidcVersionBO = pidcVersionBO;
    this.pidcDetailsTreeViewer = pidcDetailsTreeViewer;
    this.cdmHandler = new AssignCustomerCDMHandler(pidcVersionBO);

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
    setMessage("Assignment between Creta/FTE and iCDM variant", IMessageProvider.INFORMATION);

    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // Set the dialog title
    newShell.setText(DIALOG_TITLE);

    super.configureShell(newShell);
  }

  /**
   * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse .swt.widgets.Composite)
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {

    createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, false);
    parent.redraw();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void buttonPressed(final int buttonId) {
    if (buttonId == IDialogConstants.CLOSE_ID) {
      close();
    }
    super.buttonPressed(buttonId);
  }

  /**
   * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets .Composite)
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    createComposite();

    parent.layout(false, true);
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
    this.composite.setLayout(new GridLayout());
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    this.composite.setLayoutData(gridData);

    createTableSection();
    createBottomSection();

    // set the width of two section
    this.sashForm.setWeights(new int[] { 1, 1 });

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(SWT.CLOSE | SWT.BORDER | SWT.RESIZE | SWT.TITLE | SWT.MAX | SWT.MIN);
  }

  /**
   *
   */
  private void createBottomSection() {
    GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);

    this.btmSection =
        SectionUtil.getInstance().createSection(this.sashForm, this.formToolkit, "All existing assignments");
    this.btmSection.setLayoutData(gridData);

    createBottomForm();

    this.btmSection.setClient(this.btmForm);
  }

  /**
   *
   */
  private void createBottomForm() {
    this.btmForm = this.formToolkit.createForm(this.btmSection);
    this.btmForm.getBody().setLayout(new GridLayout());

    this.toolBarActionSet = new AssignVarNamesToolBarActionSet();
    AssignVarNameToolBarFilters toolBarFilters = new AssignVarNameToolBarFilters(this.pidcVersionBO);
    final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);

    final ToolBar toolbar = toolBarManager.createControl(this.btmSection);


    // create filter text for the table
    createFilterTxtExistingAssgnmtsTable();

    // Create table to display the list of var names from file
    createBottomTable();


    this.existingAssgnmtsTableViewer.addFilter(toolBarFilters);


    this.toolBarActionSet.createInFileFilterAction(toolBarManager, toolBarFilters, this.existingAssgnmtsTableViewer);
    this.toolBarActionSet.createNotInFileFilterAction(toolBarManager, toolBarFilters, this.existingAssgnmtsTableViewer);
    toolBarFilters.setTableViewer(this.varNameListFromFileTableViewer);

    toolBarManager.update(true);
    this.btmSection.setTextClient(toolbar);
  }


  /**
   *
   */
  private void createBottomTable() {

    this.existingAssgnmtsTableViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.btmForm.getBody(),
        SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER | SWT.V_SCROLL,
        GridDataUtil.getInstance().getHeightHintGridData(250));
    this.existingAssgnmtsTableViewer.setContentProvider(new ArrayContentProvider());

    // Create table sorter
    this.btmTabSorter = new AssignVariantNamesTabSorter(this.pidcVersionBO);
    this.existingAssgnmtsTableViewer.setComparator(this.btmTabSorter);
    this.btmTabSorter.setTableViewer(this.varNameListFromFileTableViewer);
    // Create GridViewerColumns
    createVarNameCustColumn();
    createInFileColumn();
    createVarNameinICDMColumn();
    createDeleteColumn();
    addMouseDownListener();

    setInputExistingAssignmnts();
    addFilterBottomTable();
  }

  /**
   *
   */
  private void addMouseDownListener() {
    this.existingAssgnmtsTableViewer.getGrid().addMouseListener(new MouseAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void mouseDown(final MouseEvent event) {
        final int columnIndex = GridTableViewerUtil.getInstance().getTabColIndex(event,
            AssignCustVarsToICDMVarsDialog.this.existingAssgnmtsTableViewer);
        if (columnIndex == CommonUIConstants.COLUMN_INDEX_3) {
          final Point point = new Point(event.x, event.y);
          final GridItem item =
              AssignCustVarsToICDMVarsDialog.this.existingAssgnmtsTableViewer.getGrid().getItem(point);
          final Object data = item.getData();
          PidcVariant variant = (PidcVariant) data;
          PidcVariantAttribute varCustCDMAttr = getVariantNameAssgnmtAttr(variant);
          // delete assignment
          AssignCustVarsToICDMVarsDialog.this.cdmHandler.invokeUpdateCommand(varCustCDMAttr);
          setInputExistingAssignmnts();
          AssignCustVarsToICDMVarsDialog.this.varNameListFromiCDMTableViewer.refresh();
          AssignCustVarsToICDMVarsDialog.this.varNameListFromFileTableViewer.refresh();
        }
      }


    });

  }


  /**
   * {@inheritDoc}
   */
  public boolean isModifiable(final PidcVersion pidcVersion) {

    CurrentUserBO currUser = new CurrentUserBO();


    try {
      if ((null != currUser.getNodeAccessRight(this.pidcVersionBO.getPidcVersion().getPidcId())) &&
          currUser.hasNodeWriteAccess(this.pidcVersionBO.getPidcVersion().getPidcId())) {
        return !this.pidcVersionBO.getPidcDataHandler().getPidcVersionInfo().getPidc().isDeleted();
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }

    return false;
  }

  /**
   *
   */
  private void createDeleteColumn() {
    final GridViewerColumn delColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.existingAssgnmtsTableViewer, "", 50);

    delColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc} getText
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
        return ImageManager.INSTANCE.getRegisteredImage(ImageKeys.DELETED_ITEMS_ICON_16X16);
      }
    });
  }

  /**
   * @param existingAssgmts
   * @param varCustAttrIdStr
   */
  private void setInputExistingAssignmnts() {
    SortedSet<PidcVariant> existingAssgmts = new TreeSet<PidcVariant>();
    for (PidcVariant var : this.pidcVersionBO.getPidcDataHandler().getVariantMap().values()) {
      if ((null != getVariantNameAssgnmtAttr(var)) && (getVariantNameAssgnmtAttr(var).getValue() != null)) {
        existingAssgmts.add(var);
      }
    }
    this.existingAssgnmtsTableViewer.setInput(existingAssgmts);
  }

  private void createVarNameinICDMColumn() {
    final GridViewerColumn attrNameColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.existingAssgnmtsTableViewer, "Variant in ICDM", 300);
    // Add column selection listener
    attrNameColumn.getColumn()
        .addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(attrNameColumn.getColumn(),
            ApicUiConstants.COLUMN_INDEX_2, this.btmTabSorter, this.existingAssgnmtsTableViewer));
    attrNameColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc} getText
       */
      @Override
      public String getText(final Object element) {
        return ((PidcVariant) element).getName();
      }

      @Override
      public Color getForeground(final Object element) {
        final PidcVariant var = (PidcVariant) element;
        if (var.isDeleted()) {
          return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
        }
        return Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
      }
    });
  }

  private void createInFileColumn() {

    GridColumn gridCol = new GridColumn(this.existingAssgnmtsTableViewer.getGrid(), SWT.CHECK | SWT.LEFT);
    gridCol.setWidth(35);
    gridCol.setText("In cur. file");
    gridCol.setSummary(false);

    final GridViewerColumn inCurrentFileColumn = new GridViewerColumn(this.existingAssgnmtsTableViewer, gridCol);
    inCurrentFileColumn.getColumn().setText("In cur. file");
    inCurrentFileColumn.getColumn().setWidth(70);

    // Add column selection listener
    inCurrentFileColumn.getColumn()
        .addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(inCurrentFileColumn.getColumn(),
            ApicUiConstants.COLUMN_INDEX_1, this.btmTabSorter, this.existingAssgnmtsTableViewer));
    inCurrentFileColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public void update(final ViewerCell cell) {
        updateCellVal(cell);
      }
    });
  }

  /**
   * @param cell
   */
  private void updateCellVal(final ViewerCell cell) {
    Object element = cell.getElement();
    GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
    final PidcVariant var = (PidcVariant) element;
    boolean flag = false;
        GridItem[] itemsFromFile =
            AssignCustVarsToICDMVarsDialog.this.varNameListFromFileTableViewer.getGrid().getItems();

    PidcVariantAttribute varCustCDMAttr = getVariantNameAssgnmtAttr(var);
    PidcVariantBO varHandler = new PidcVariantBO(AssignCustVarsToICDMVarsDialog.this.pidcVersionBO.getPidcVersion(),
        var, AssignCustVarsToICDMVarsDialog.this.pidcVersionBO.getPidcDataHandler());
    PidcVariantAttributeBO attHandler = new PidcVariantAttributeBO(varCustCDMAttr, varHandler);
    String varNameInCust = attHandler.getDefaultValueDisplayName(true);
    for (GridItem item : itemsFromFile) {
      if (item.getData().equals(varNameInCust)) {
        flag = true;
        break;
      }
    }
    gridItem.setChecked(cell.getVisualIndex(), flag);
    gridItem.setCheckable(cell.getVisualIndex(), false);
  }

  /**
   *
   */
  private void createVarNameCustColumn() {
    final GridViewerColumn attrNameColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.existingAssgnmtsTableViewer, "Variants in Cust. CDM system", 300);
    // Add column selection listener
    attrNameColumn.getColumn()
        .addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(attrNameColumn.getColumn(),
            ApicUiConstants.COLUMN_INDEX_0, this.btmTabSorter, this.existingAssgnmtsTableViewer));
    attrNameColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc} getText
       */
      @Override
      public String getText(final Object element) {
        final PidcVariant variant = (PidcVariant) element;
        PidcVariantAttribute varCustCDMAttr = getVariantNameAssgnmtAttr(variant);
        PidcVariantBO varHandler = new PidcVariantBO(AssignCustVarsToICDMVarsDialog.this.pidcVersionBO.getPidcVersion(),
            variant, AssignCustVarsToICDMVarsDialog.this.pidcVersionBO.getPidcDataHandler());
        PidcVariantAttributeBO attHandler = new PidcVariantAttributeBO(varCustCDMAttr, varHandler);
        return attHandler.getDefaultValueDisplayName(true);
      }
    });
  }

  /**
   * ok pressed
   */
  @Override
  protected void okPressed() {
    // Do nothing
  }

  /**
   *
   */
  private void createTableSection() {
    GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    this.sashForm = new SashForm(this.composite, SWT.VERTICAL);
    this.sashForm.setLayout(new GridLayout());
    this.sashForm.setLayoutData(gridData);

    this.section = SectionUtil.getInstance().createSection(this.sashForm, this.formToolkit,
        "Assign customer variants to iCDM variant");
    this.section.setLayoutData(gridData);

    createTableForm();
    this.section.setClient(this.form);
  }


  /**
   *
   */
  private void createTableForm() {
    this.form = this.formToolkit.createForm(this.section);

    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;
    this.form.getBody().setLayout(gridLayout);

    // Create table to display the list of var names from file
    createLeftTable();

    getFormToolkit().createLabel(this.form.getBody(), "");

    // Create table to display the list of var names from iCDM
    createRightTable();
    getFormToolkit().createLabel(this.form.getBody(), ApicConstants.EMPTY_STRING);
    getFormToolkit().createLabel(this.form.getBody(), "");
    this.assignBtn = createButton(this.form.getBody(), IDialogConstants.OK_ID, "", true);
    this.assignBtn.setImage(ImageManager.INSTANCE.getRegisteredImage(ImageKeys.DOWN_BUTTON_ICON_16X16));
    this.assignBtn.setSize(10, 10);
    this.assignBtn.setEnabled(true);

    this.assignBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        assignCustVarsToICDMVars();
      }
    });
  }

  /**
  *
  */
  private void assignCustVarsToICDMVars() {
    if ((null != AssignCustVarsToICDMVarsDialog.this.selCDMVarName) &&
        (null == AssignCustVarsToICDMVarsDialog.this.seliCDMVarName)) {
      createVariantInICDMAndAssignTheVal();
      refreshControls();
    }
    else if ((null != AssignCustVarsToICDMVarsDialog.this.selCDMVarName) &&
        (null != AssignCustVarsToICDMVarsDialog.this.seliCDMVarName)) {
      String leftSelectedVarName = AssignCustVarsToICDMVarsDialog.this.selCDMVarName;
      String rightSelectedVarName = AssignCustVarsToICDMVarsDialog.this.seliCDMVarName.getName();
      Collection<PidcVariant> varCollection =
          AssignCustVarsToICDMVarsDialog.this.pidcVersionBO.getVariantsMap().values();
      PidcVariant selPidcVariant = null;
      for (PidcVariant pidcVariant : varCollection) {
        if (pidcVariant.getName().equals(rightSelectedVarName)) {
          selPidcVariant = pidcVariant;
          break;
        }
      }
      if (null != selPidcVariant) {
        // Get the Variant attribute in customer CDM system attribute
        PidcVariantAttribute varCustCDMAttr = getVariantNameAssgnmtAttr(selPidcVariant);
            AssignCustVarsToICDMVarsDialog.this.cdmHandler.setValueForVarCustCDMAttr(selPidcVariant,
                leftSelectedVarName, leftSelectedVarName, varCustCDMAttr);
      }
      setInputExistingAssignmnts();
      refreshControls();
    }
    else {
      MessageDialogUtils.getErrorMessageDialog("Invalid Action ",
          "Please select a customer variant and optionally an iCDM variant to create an assignment or create a new variant in iCDM");
    }
  }

  /**
  *
  */
  private void refreshControls() {
    AssignCustVarsToICDMVarsDialog.this.leftSelectedText.setText("");
    AssignCustVarsToICDMVarsDialog.this.rightSelectedText.setText("");
    AssignCustVarsToICDMVarsDialog.this.varNameListFromFileTableViewer.refresh();
    AssignCustVarsToICDMVarsDialog.this.varNameListFromiCDMTableViewer.refresh();
    setInputExistingAssignmnts();
    AssignCustVarsToICDMVarsDialog.this.existingAssgnmtsTableViewer.refresh();
  }

  /**
   *
   */
  private void createRightTable() {
    final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);

    final ToolBar toolbar = toolBarManager.createControl(this.section);

    createAddVariantAction(toolBarManager);

    toolBarManager.update(true);
    this.section.setTextClient(toolbar);
    Group rightGrp = new Group(this.form.getBody(), SWT.BORDER_DOT);
    rightGrp.setLayout(new GridLayout());
    rightGrp.setLayoutData(GridDataUtil.getInstance().getGridData());
    createRightFilterTxt(rightGrp);
    this.varNameListFromiCDMTableViewer = GridTableViewerUtil.getInstance().createGridTableViewer(rightGrp,
        SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER | SWT.V_SCROLL,
        GridDataUtil.getInstance().getHeightHintGridData(150));
    this.varNameListFromiCDMTableViewer.setContentProvider(new ArrayContentProvider());

    // Create table sorter
    this.rightTabSorter = new AssignVariantNamesTabSorter(this.pidcVersionBO);
    this.varNameListFromiCDMTableViewer.setComparator(this.rightTabSorter);
    // Create GridViewerColumns
    createVarNameICDMColumn();
    createAssignedICDMColumn();


    Group gr = new Group(rightGrp, SWT.NONE);
    GridLayout gridLayout1 = new GridLayout();
    gridLayout1.numColumns = 2;
    gr.setLayout(gridLayout1);
    gr.setLayoutData(GridDataUtil.getInstance().getGridData());

    this.rightSelectedText = TextUtil.getInstance().createFilterText(this.formToolkit, gr,
        GridDataUtil.getInstance().getTextGridData(), "Selected Variant");
    this.rightSelectedText.setEditable(false);

    Button deleteRightSelectionBtn = new Button(gr, SWT.PUSH);
    deleteRightSelectionBtn.setImage(ImageManager.INSTANCE.getRegisteredImage(ImageKeys.DELETE_16X16));
    deleteRightSelectionBtn.setFont(JFaceResources.getDialogFont());

    deleteRightSelectionBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        AssignCustVarsToICDMVarsDialog.this.rightSelectedText.setText(ApicConstants.EMPTY_STRING);
        AssignCustVarsToICDMVarsDialog.this.varNameListFromiCDMTableViewer.setSelection(StructuredSelection.EMPTY);
        AssignCustVarsToICDMVarsDialog.this.seliCDMVarName = null;
      }
    });

    this.varNameListFromiCDMTableViewer.setInput(this.pidcVersionBO.getVariantsSet(false));

    this.varNameListFromiCDMTableViewer.addSelectionChangedListener((final SelectionChangedEvent event) -> {
      final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      Object selected = selection.getFirstElement();
      if (selected instanceof PidcVariant) {
        PidcVariant var = (PidcVariant) selected;
        AssignCustVarsToICDMVarsDialog.this.seliCDMVarName = var;
        AssignCustVarsToICDMVarsDialog.this.rightSelectedText.setText(var.getName());
      }
    });
    // add filter for the table viewer
    addFilterRightTable();

  }

  /**
   * @param toolBarManager
   * @param toolBarFilters2
   * @param resultTable2
   */
  private void createAddVariantAction(final ToolBarManager toolBarManager) {
    this.addAction = new Action("Add Variant") {

      @Override
      public void run() {
        createVariantInICDMAndAssignTheVal();
      }
    };
    // Image for add action
    this.addAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));
    toolBarManager.add(this.addAction);
  }

  /**
   *
   */
  private void createAssignedICDMColumn() {

    GridColumn gridCol = new GridColumn(this.varNameListFromiCDMTableViewer.getGrid(), SWT.CHECK | SWT.LEFT);
    gridCol.setWidth(35);
    gridCol.setText(ASSIGNED);
    gridCol.setSummary(false);

    final GridViewerColumn assignedColumn = new GridViewerColumn(this.varNameListFromiCDMTableViewer, gridCol);
    assignedColumn.getColumn().setText(ASSIGNED);
    assignedColumn.getColumn().setWidth(70);

    // Add column selection listener
    assignedColumn.getColumn()
        .addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(assignedColumn.getColumn(),
            ApicUiConstants.COLUMN_INDEX_3, this.rightTabSorter, this.varNameListFromiCDMTableViewer));
    assignedColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public void update(final ViewerCell cell) {
        Object element = cell.getElement();
        final PidcVariant var = (PidcVariant) element;
        PidcVariantAttribute varCustCDMAttr = getVariantNameAssgnmtAttr(var);
        GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
        if (null != varCustCDMAttr) {
          gridItem.setChecked(cell.getVisualIndex(), varCustCDMAttr.getValue() != null);
          gridItem.setCheckable(cell.getVisualIndex(), false);
        }
      }
    });
  }

  /**
  *
  */
  private void createVariantInICDMAndAssignTheVal() {
    // Create the variant in ICDM
    final Attribute varNameAttr = new ApicDataBO().getAllLvlAttrByLevel().get((long) ApicConstants.VARIANT_CODE_ATTR);
    Set<String> attrValSet = new HashSet<>();
    new AttributeClientBO(varNameAttr).getAttrValues().forEach(attrval -> attrValSet.add(attrval.getNameRaw()));
    final AddValueDialog addValueDialog = new AddValueDialog(Display.getDefault().getActiveShell(), varNameAttr,
        AssignCustVarsToICDMVarsDialog.this.pidcDetailsTreeViewer, AssignCustVarsToICDMVarsDialog.this.pidcVersionBO,
        attrValSet);
    addValueDialog.setValueEngText(AssignCustVarsToICDMVarsDialog.this.selCDMVarName);
    addValueDialog.setValueGerText("");
    addValueDialog.setDescripEng(AssignCustVarsToICDMVarsDialog.this.selCDMVarName);
    addValueDialog.setFromVarNameAssgnmtDialog(true);
    addValueDialog.open();

    SortedSet<PidcVariant> variantsSet = AssignCustVarsToICDMVarsDialog.this.pidcVersionBO.getVariantsSet();

    long newVarId = addValueDialog.getNewVariantID();
    // assign the Variant name in customer cdm system attr
    PidcVariant newlyCreatedVar = null;
    for (PidcVariant pidcVariant : variantsSet) {
      if (pidcVariant.getId().equals(newVarId)) {
        newlyCreatedVar = pidcVariant;
        break;
      }
    }
    if (null != newlyCreatedVar) {
      // Get the Variant attribute in customer CDM system attribute
      PidcVariantAttribute varCustCDMAttr = getVariantNameAssgnmtAttr(newlyCreatedVar);
      AssignCustVarsToICDMVarsDialog.this.cdmHandler.setValueForVarCustCDMAttr(newlyCreatedVar,
          AssignCustVarsToICDMVarsDialog.this.selCDMVarName, AssignCustVarsToICDMVarsDialog.this.selCDMVarName,
          varCustCDMAttr);

      // refresh UI
      AssignCustVarsToICDMVarsDialog.this.varNameListFromiCDMTableViewer.setInput(variantsSet);
      AssignCustVarsToICDMVarsDialog.this.varNameListFromiCDMTableViewer.refresh();
    }
  }


  /**
   *
   */
  private void createVarNameICDMColumn() {
    final GridViewerColumn attrNameColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.varNameListFromiCDMTableViewer, "Variants in iCDM", 200);
    // Add column selection listener
    attrNameColumn.getColumn()
        .addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(attrNameColumn.getColumn(),
            ApicUiConstants.COLUMN_INDEX_2, this.rightTabSorter, this.varNameListFromiCDMTableViewer));
    attrNameColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc} getText
       */
      @Override
      public String getText(final Object element) {
        final PidcVariant var = (PidcVariant) element;
        return var.getName();
      }
    });
  }

  /**
   * Create filter text for table
   *
   * @param leftGrp
   */
  private void createFilterTxt(final Group leftGrp) {
    this.filterTxt = TextUtil.getInstance().createFilterText(this.formToolkit, leftGrp,
        GridDataUtil.getInstance().getTextGridData(), Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.filterTxt.addModifyListener((final ModifyEvent event) -> {
      final String text = AssignCustVarsToICDMVarsDialog.this.filterTxt.getText().trim();
      // set the entered text as filter text and invoke the table viewer refresh
      ((AbstractViewerFilter) AssignCustVarsToICDMVarsDialog.this.leftTableFilter).setFilterText(text);
      AssignCustVarsToICDMVarsDialog.this.varNameListFromFileTableViewer.refresh();
    });
    this.filterTxt.setFocus();

  }

  /**
   * Create filter text for table
   *
   * @param compsite
   */
  private void createFilterTxtExistingAssgnmtsTable() {
    this.filterTxtExistingAssgnmts = TextUtil.getInstance().createFilterText(this.formToolkit, this.btmForm.getBody(),
        GridDataUtil.getInstance().getTextGridData(), Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.filterTxtExistingAssgnmts.addModifyListener((final ModifyEvent event) -> {
      final String text = AssignCustVarsToICDMVarsDialog.this.filterTxtExistingAssgnmts.getText().trim();
      // set the entered text as filter text and invoke the table viewer refresh
      ((AbstractViewerFilter) AssignCustVarsToICDMVarsDialog.this.bottomTableFilter).setFilterText(text);
      AssignCustVarsToICDMVarsDialog.this.existingAssgnmtsTableViewer.refresh();
    });
    this.filterTxtExistingAssgnmts.setFocus();
  }

  /**
   * Create filter text for table
   *
   * @param rightGrp
   */
  private void createRightFilterTxt(final Group rightGrp) {
    this.rghtfilterTxt = TextUtil.getInstance().createFilterText(this.formToolkit, rightGrp,
        GridDataUtil.getInstance().getTextGridData(), Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.rghtfilterTxt.addModifyListener((final ModifyEvent event) -> {
      final String text = AssignCustVarsToICDMVarsDialog.this.rghtfilterTxt.getText().trim();
      // set the entered text as filter text and invoke the table viewer refresh
      ((AbstractViewerFilter) AssignCustVarsToICDMVarsDialog.this.rightTableFilter).setFilterText(text);
      AssignCustVarsToICDMVarsDialog.this.varNameListFromiCDMTableViewer.refresh();
    });
    this.rghtfilterTxt.setFocus();
  }

  /**
   * Create table to display the attribute value list
   */
  private void createLeftTable() {

    final GridData gridData = GridDataUtil.getInstance().getGridData();
    Button loadBtn = new Button(this.form.getBody(), SWT.PUSH);
    loadBtn.setLayoutData(gridData);
    loadBtn.setText("Load from customer CDM system");
    loadBtn.setFont(JFaceResources.getDialogFont());

    loadBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {

        loadFromCusCDMSystem();
      }
    });
    getFormToolkit().createLabel(this.form.getBody(), "");
    getFormToolkit().createLabel(this.form.getBody(), "");
    getFormToolkit().createLabel(this.form.getBody(), "");


    Group leftGrp = new Group(this.form.getBody(), SWT.BORDER_DOT);
    GridLayout gridLayout = new GridLayout();
    leftGrp.setLayout(gridLayout);
    leftGrp.setLayoutData(GridDataUtil.getInstance().getGridData());


    // create filter text for the table
    createFilterTxt(leftGrp);
    this.varNameListFromFileTableViewer = GridTableViewerUtil.getInstance().createGridTableViewer(leftGrp,
        SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER | SWT.V_SCROLL,
        GridDataUtil.getInstance().getHeightHintGridData(150));
    this.varNameListFromFileTableViewer.setContentProvider(new ArrayContentProvider());

    // Create table sorter
    this.leftTabSorter = new AssignVariantNamesTabSorter(this.pidcVersionBO);
    this.varNameListFromFileTableViewer.setComparator(this.leftTabSorter);
    // Create GridViewerColumns
    createVarNameColumn();
    createAssignedColumn();

    Group gr = new Group(leftGrp, SWT.NONE);
    GridLayout gridLayout1 = new GridLayout();
    gridLayout1.numColumns = 2;
    gr.setLayout(gridLayout1);
    gr.setLayoutData(GridDataUtil.getInstance().getGridData());

    this.leftSelectedText = TextUtil.getInstance().createFilterText(this.formToolkit, gr,
        GridDataUtil.getInstance().getTextGridData(), "Selected Variant");
    this.leftSelectedText.setEditable(false);
    Button deleteLeftSelectionBtn = new Button(gr, SWT.PUSH);
    deleteLeftSelectionBtn.setImage(ImageManager.INSTANCE.getRegisteredImage(ImageKeys.DELETE_16X16));
    deleteLeftSelectionBtn.setFont(JFaceResources.getDialogFont());

    deleteLeftSelectionBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        AssignCustVarsToICDMVarsDialog.this.leftSelectedText.setText(ApicConstants.EMPTY_STRING);
        AssignCustVarsToICDMVarsDialog.this.varNameListFromFileTableViewer.setSelection(StructuredSelection.EMPTY);
        AssignCustVarsToICDMVarsDialog.this.selCDMVarName = null;
      }
    });

    this.varNameListFromFileTableViewer.addSelectionChangedListener((final SelectionChangedEvent event) -> {
      final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      Object selected = selection.getFirstElement();
      if (selected instanceof String) {
        AssignCustVarsToICDMVarsDialog.this.selCDMVarName = (String) selected;
        AssignCustVarsToICDMVarsDialog.this.leftSelectedText.setText(AssignCustVarsToICDMVarsDialog.this.selCDMVarName);
      }
    });
    this.varNameListFromFileTableViewer.setInput(new ArrayList<>(Arrays.asList("", "", "", "", "", "", "")));
    // add filter for the table viewer
    addFilterLeftTable();

  }

  /**
  *
  */
  private void loadFromCusCDMSystem() {
    FileDialogHandler fileDialog = new FileDialogHandler(Display.getCurrent().getActiveShell(), SWT.OPEN | SWT.MULTI);
    fileDialog.setText("Import CRETA/FTE files");


    // set filter names
    fileDialog.setFilterNames(fileNames);
    // set the extensions order based on user previous selection
    fileDialog.setFilterExtensions(fileExtensions);
    fileDialog.open();
    String[] filenames = fileDialog.getFileNames();
    String[] fileNamesWithPath = new String[filenames.length];
    int iCounter = 0;
    for (String string : filenames) {
      fileNamesWithPath[iCounter] = fileDialog.getFilterPath() + "\\" + string;
      iCounter++;
    }
    // selected file names
    AssignCustVarsToICDMVarsDialog.this.fileSelected = fileNamesWithPath;
    if (AssignCustVarsToICDMVarsDialog.this.fileSelected == null) {
      return;
    }
    SortedSet<String> varNamesFromAllFiles = new TreeSet<>();
    for (String filePath : fileNamesWithPath) {

      CretaFteFilesParserHandler parsrHandler = new CretaFteFilesParserHandler(ParserLogger.getInstance());
      SortedSet<String> varNames = parsrHandler.getVariantNames(filePath);
      varNamesFromAllFiles.addAll(varNames);
    }
    if (CommonUtils.isNotEmpty(varNamesFromAllFiles)) {
      AssignCustVarsToICDMVarsDialog.this.varNameListFromFileTableViewer.setInput(varNamesFromAllFiles);
      AssignCustVarsToICDMVarsDialog.this.existingAssgnmtsTableViewer.refresh();
    }
  }

  /**
   * Create column for attribute description
   */
  private void createAssignedColumn() {
    GridColumn gridCol = new GridColumn(this.varNameListFromFileTableViewer.getGrid(), SWT.CHECK | SWT.LEFT);
    gridCol.setWidth(35);
    gridCol.setText(ASSIGNED);
    gridCol.setSummary(false);
    final GridViewerColumn assignedColumn = new GridViewerColumn(this.varNameListFromFileTableViewer, gridCol);
    assignedColumn.getColumn().setText(ASSIGNED);
    assignedColumn.getColumn().setWidth(70);

    // Add column selection listener
    assignedColumn.getColumn()
        .addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(assignedColumn.getColumn(),
            ApicUiConstants.COLUMN_INDEX_1, this.leftTabSorter, this.varNameListFromFileTableViewer));
    assignedColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public void update(final ViewerCell cell) {
        Object element = cell.getElement();
        GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
        final String varName = (String) element;
        boolean flag = false;
        for (PidcVariant var : AssignCustVarsToICDMVarsDialog.this.pidcVersionBO.getVariantsMap().values()) {

          PidcVariantAttribute varCustCDMAttr = getVariantNameAssgnmtAttr(var);
          if ((null != varCustCDMAttr) && (null != varCustCDMAttr.getValue()) &&
              varCustCDMAttr.getValue().equals(varName)) {
            flag = true;
            break;
          }
        }
        gridItem.setChecked(cell.getVisualIndex(), flag);
        gridItem.setCheckable(cell.getVisualIndex(), false);
      }
    });
  }

  /**
   * Create column for attribute name
   */
  private void createVarNameColumn() {
    final GridViewerColumn attrNameColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.varNameListFromFileTableViewer, "Variants from Cust. CDM System", 200);
    // Add column selection listener
    attrNameColumn.getColumn()
        .addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(attrNameColumn.getColumn(),
            ApicUiConstants.COLUMN_INDEX_0, this.leftTabSorter, this.varNameListFromFileTableViewer));
    attrNameColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc} getText
       */
      @Override
      public String getText(final Object element) {
        return (String) element;
      }
    });
  }

  /**
   * add filter for the table view
   */
  private void addFilterLeftTable() {
    this.varNameListFromFileTableViewer.addFilter(new AbstractViewerFilter() {

      @Override
      protected boolean selectElement(final Object element) {
        if (element instanceof String) {
          final String varName = (String) element;
          // match the entered text with variant name
          if (matchText(varName)) {
            return true;
          }
        }
        return false;
      }
    });
    final ViewerFilter[] filters = this.varNameListFromFileTableViewer.getFilters();
    this.leftTableFilter = filters[0];

  }

  /**
   * add filter for the table view
   */
  private void addFilterBottomTable() {
    this.existingAssgnmtsTableViewer.addFilter(new AbstractViewerFilter() {

      @Override
      protected boolean selectElement(final Object element) {
        if (element instanceof PidcVariant) {

          final PidcVariant var = (PidcVariant) element;
          PidcVariantAttribute varCustCDMAttr = getVariantNameAssgnmtAttr(var);

          // match the entered text with variant name
          if (matchText(var.getName()) || matchText(varCustCDMAttr.getValue())) {
            return true;
          }
        }
        return false;
      }
    });
    final ViewerFilter[] filters = this.existingAssgnmtsTableViewer.getFilters();
    this.bottomTableFilter = filters[0];

  }

  /**
   * @param variant
   * @return
   * @throws ApicWebServiceException
   */
  private PidcVariantAttribute getVariantNameAssgnmtAttr(final PidcVariant variant) {
    CommonDataBO commonDataBo = new CommonDataBO();

    String varCustAttrIdStr = null;
    try {
      varCustAttrIdStr = commonDataBo.getParameterValue(CommonParamKey.VARIANT_IN_CUST_CDMS_ATTR_ID);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    PidcVariantBO varHandler = new PidcVariantBO(AssignCustVarsToICDMVarsDialog.this.pidcVersionBO.getPidcVersion(),
        variant, AssignCustVarsToICDMVarsDialog.this.pidcVersionBO.getPidcDataHandler());

    if (null == varHandler.getAttributesAll().get(Long.parseLong(varCustAttrIdStr))) {
      // Attr to be found from pidc level and moved to variant level
      PidcVersionAttribute attrAtPIDCLevel = this.pidcVersionBO.getAttributes().get(Long.parseLong(varCustAttrIdStr));

      if (attrAtPIDCLevel.isCanMoveDown()) {
        PidcVersionAttributeBO handler = new PidcVersionAttributeBO(attrAtPIDCLevel, this.pidcVersionBO);

        if (handler.isVisible() && handler.isModifiable() && isModifiable(this.pidcVersionBO.getPidcVersion())) {

          // prepare move model
          ProjectAttributesMovementModel moveModel = new ProjectAttributesMovementModel(this.pidcVersionBO);
          moveModel.setPidcVersion(this.pidcVersionBO.getPidcVersion());
          moveModel.getPidcAttrsToBeMovedDown().put(attrAtPIDCLevel.getAttrId(), attrAtPIDCLevel);

          // move to variant
          new PIDCActionSet().moveToVar(attrAtPIDCLevel, this.pidcVersionBO.getPidcVersion(), this.pidcVersionBO);

        }
      }
    }
    return varHandler.getAttributesAll().get(Long.parseLong(varCustAttrIdStr));
  }


  /**
   * add filter for the table view
   */
  private void addFilterRightTable() {
    this.varNameListFromiCDMTableViewer.addFilter(new AbstractViewerFilter() {

      @Override
      protected boolean selectElement(final Object element) {
        if (element instanceof PidcVariant) {

          final PidcVariant varName = (PidcVariant) element;
          // match the entered text with variant name
          if (matchText(varName.getName())) {
            return true;
          }
        }
        return false;
      }
    });
    final ViewerFilter[] filters = this.varNameListFromiCDMTableViewer.getFilters();
    this.rightTableFilter = filters[0];
  }

  @Override
  protected boolean isResizable() {
    return true;
  }
}
