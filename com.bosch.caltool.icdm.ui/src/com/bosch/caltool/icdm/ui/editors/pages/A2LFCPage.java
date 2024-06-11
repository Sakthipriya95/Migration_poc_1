/**
 *
 */
package com.bosch.caltool.icdm.ui.editors.pages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheet;

import com.bosch.calcomp.labfunwriter.LabFunWriterConstants.OUTPUT_FILE_TYPE;
import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.common.ui.actions.CDMCommonActionSet;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.dragdrop.CustomDragListener;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.providers.SelectionProviderMediator;
import com.bosch.caltool.icdm.common.ui.services.IA2lParamTable;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.OutlineViewPart;
import com.bosch.caltool.icdm.common.ui.wizards.PreCalDataExportWizard;
import com.bosch.caltool.icdm.common.ui.wizards.PreCalDataExportWizardDialog;
import com.bosch.caltool.icdm.ruleseditor.actions.ReviewActionSet;
import com.bosch.caltool.icdm.ui.action.LabFunExportAction;
import com.bosch.caltool.icdm.ui.editors.A2LContentsEditor;
import com.bosch.caltool.icdm.ui.editors.A2LContentsEditorInput;
import com.bosch.caltool.icdm.ui.sorters.FCGridTabViewerSorter;
import com.bosch.caltool.icdm.ui.table.filters.FCTableFilters;
import com.bosch.caltool.icdm.ui.table.filters.OutlineFilter;
import com.bosch.caltool.icdm.ui.util.IMessageConstants;
import com.bosch.caltool.icdm.ui.util.Messages;
import com.bosch.caltool.icdm.ui.views.providers.FCFormPageTableLabelProvider;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;


/**
 * The Class FCFormPage.
 *
 * @author adn1cob
 */
public class A2LFCPage extends AbstractFormPage implements ISelectionListener {

  /** The form toolkit. */
  private FormToolkit formToolkit; // @jve:decl-index=0:visual-constraint=""

  /** The composite. */
  private Composite composite;

  /** The section. */
  private Section section;

  /** The base form. */
  private Form baseForm;

  /** The fc table viewer. */
  private GridTableViewer fcTableViewer;

  /** The fc name column. */
  private GridViewerColumn fcNameColumn;

  /** The version column. */
  private GridViewerColumn versionColumn;

  /** The long name column. */
  private GridViewerColumn longNameColumn;

  /** Filter text instance. */
  private Text filterTxt;

  /** FCTableFilters instance. */
  private FCTableFilters fcFilter;

  /** The fc tab sorter. */
  private FCGridTabViewerSorter fcTabSorter;

  /** OutlineFilter instance. */
  private OutlineFilter outlineFilter;

  /** Editor instance. */
  private final A2LContentsEditor editor;

  /** Non scrollable form. */
  private Form nonScrollableForm;

  /** The a 2 l contents editor input. */
  private final A2LContentsEditorInput a2lContentsEditorInput;

  /**
   * Instantiates a new FC form page.
   *
   * @param editor the editor
   */
  public A2LFCPage(final FormEditor editor) {
    super(editor, "FC", Messages.getString("FCFormPage.label")); //$NON-NLS-1$ //$NON-NLS-2$
    this.editor = (A2LContentsEditor) editor;
    this.a2lContentsEditorInput = (A2LContentsEditorInput) (editor.getEditorInput());
  }

  /**
   * {@inheritDoc}
   */
  // ICDM-249
  @Override
  public void createPartControl(final Composite parent) {

    // ICDM-249
    // Create an ordinary non scrollable form on which widgets are built
    this.nonScrollableForm = this.editor.getToolkit().createForm(parent);
    // instead of editor.getToolkit().createScrolledForm(parent); in superclass
    // formToolkit is obtained from managed form to create form within section
    ManagedForm mform = new ManagedForm(parent);
    createFormContent(mform);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Control getPartControl() {
    return this.nonScrollableForm;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createFormContent(final IManagedForm managedForm) {
    this.formToolkit = managedForm.getToolkit();
    createComposite(this.formToolkit);
    // add listeners
    getSite().getPage().addSelectionListener(this);

  }

  /**
   * fset focus to property sheet.
   *
   * @param toolkit This method initializes composite
   */
  private void createComposite(final FormToolkit toolkit) {

    GridData gridData = getGridData();
    this.composite = this.nonScrollableForm.getBody();
    this.composite.setLayout(new GridLayout());
    createSection(toolkit);
    this.composite.setLayoutData(gridData);
  }

  /**
   * Creates the section.
   *
   * @param toolkit This method initializes section
   */
  private void createSection(final FormToolkit toolkit) {

    GridData gridData = getGridData();
    this.section = toolkit.createSection(this.composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.section.setText("Function Components");
    this.section.setExpanded(true);
    // ICDM-183
    this.section.getDescriptionControl().setEnabled(false);
    createForm(toolkit);
    this.section.setLayoutData(gridData);
    this.section.setClient(this.baseForm);
  }

  /**
   * Creates the form.
   *
   * @param toolkit This method initializes form
   */
  private void createForm(final FormToolkit toolkit) {

    GridData gridData = getGridData();
    this.baseForm = toolkit.createForm(this.section);
    this.filterTxt = toolkit.createText(this.baseForm.getBody(), null, SWT.SINGLE | SWT.BORDER);

    createFilterTxt();
    this.fcTabSorter = new FCGridTabViewerSorter();
    this.fcTableViewer = new GridTableViewer(this.baseForm.getBody(),
        SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);

    this.fcTableViewer.getGrid().setLayoutData(gridData);

    this.fcTableViewer.getGrid().setLinesVisible(true);
    this.fcTableViewer.getGrid().setHeaderVisible(true);

    this.baseForm.getBody().setLayout(new GridLayout());
    createTabColumns();
    this.fcTableViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.fcTableViewer.setLabelProvider(new FCFormPageTableLabelProvider());

    SortedSet<Function> finalfuncList = this.a2lContentsEditorInput.getA2lFileInfoBO().getFunctionsOfLabelType(null);
    this.fcTableViewer.setInput(finalfuncList);
    // // ICDM-56
    this.fcTableViewer.getGrid().addFocusListener(new FocusListener() {

      @Override
      public void focusLost(final FocusEvent fLost) {
        // TO-DO
      }

      @Override
      public void focusGained(final FocusEvent fGained) {
        setStatusBarMessage(false);
      }
    });

    // Add filters to the TableViewer
    addFilters();

    // Invoke TableViewer Column sorters
    invokeColumnSorter();

    addRightClickMenu();

    A2LContentsEditor a2lContentsEditor = (A2LContentsEditor) A2LFCPage.this.getEditor();
    SelectionProviderMediator selectionProviderMediator = a2lContentsEditor.getSelectionProviderMediator();
    selectionProviderMediator.addViewer(A2LFCPage.this.fcTableViewer);
    getSite().setSelectionProvider(selectionProviderMediator);

    Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };
    this.fcTableViewer.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE, transferTypes,
        new CustomDragListener(this.fcTableViewer));

  }

  /**
   * Method to set selection to properties view
   *
   * @param selection from nattable
   */
  private void setSelectedNodeAccesssPropertiesView(final IStructuredSelection selection) {
    IViewPart viewPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
        .findView(com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants.PROPERTIES_VIEW);
    if (viewPart != null) {
      PropertySheet propertySheet = (PropertySheet) viewPart;
      IPropertySheetPage page = (IPropertySheetPage) propertySheet.getCurrentPage();
      if (page != null) {
        page.selectionChanged(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor(),
            selection);
      }
    }
  }

  /**
   * Gets the grid data.
   *
   * @return This method defines GridData
   */
  private GridData getGridData() {

    GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    return gridData;
  }

  /**
   * Defines the columns of the TableViewer.
   */
  private void createTabColumns() {

    this.fcNameColumn = new GridViewerColumn(this.fcTableViewer, SWT.NONE);
    this.fcNameColumn.getColumn().setText("FC Name");
    this.fcNameColumn.getColumn().setWidth(200);
    // Add column selection listener
    this.fcNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(this.fcNameColumn.getColumn(), 0, this.fcTabSorter, this.fcTableViewer));


    this.versionColumn = new GridViewerColumn(this.fcTableViewer, SWT.NONE);
    this.versionColumn.getColumn().setText("Version");
    this.versionColumn.getColumn().setWidth(200);
    // Add column selection listener
    this.versionColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(this.versionColumn.getColumn(), 1, this.fcTabSorter, this.fcTableViewer));


    this.longNameColumn = new GridViewerColumn(this.fcTableViewer, SWT.NONE);
    this.longNameColumn.getColumn().setText("Long Name");
    this.longNameColumn.getColumn().setWidth(300);
    // Add column selection listener
    this.longNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(this.longNameColumn.getColumn(), 2, this.fcTabSorter, this.fcTableViewer));


  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setActive(final boolean active) {
    if (A2LFCPage.this.editor.getActivePage() == 4) {
      final IStructuredSelection selection = (IStructuredSelection) this.fcTableViewer.getSelection();
      if ((selection != null) && (!selection.isEmpty())) {
        setSelectedNodeAccesssPropertiesView(selection);
      }
    }
    super.setActive(active);
  }

  /**
   * This method creates filter text.
   */
  private void createFilterTxt() {
    GridData gridData = getFilterTxtGridData();
    this.filterTxt.setLayoutData(gridData);
    this.filterTxt.setMessage(Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.filterTxt.addModifyListener(event -> {
      String text = A2LFCPage.this.filterTxt.getText().trim();
      A2LFCPage.this.fcFilter.setFilterText(text);
      A2LFCPage.this.fcTableViewer.refresh();
      setStatusBarMessage(false);
    });
  }

  /**
   * This method returns filter text GridData object.
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
   * Add filters for the table.
   */
  private void addFilters() {
    this.fcFilter = new FCTableFilters();
    // Add TableViewer filter
    this.fcTableViewer.addFilter(this.fcFilter);

    // Add outline filters
    this.outlineFilter = new OutlineFilter(this.editor);
    this.outlineFilter.setWpType(this.a2lContentsEditorInput.getA2lFileInfoBO().getMappingSourceID());
    this.outlineFilter.setNotAssignedFC(this.a2lContentsEditorInput.getA2lFileInfoBO().getUnassignedParams());
    this.fcTableViewer.addFilter(this.outlineFilter);

    addHelpAction((ToolBarManager) getToolBarManager());
    addResetAllFiltersAction();
  }

  /**
   * Add reset filter button ICDM-1207.
   */
  private void addResetAllFiltersAction() {
    getFilterTxtSet().add(this.filterTxt);
    getRefreshComponentSet().add(this.fcTableViewer);
    addResetFiltersAction();
  }

  /**
   * Add sorter for the table columns.
   */
  private void invokeColumnSorter() {
    this.fcTableViewer.setComparator(this.fcTabSorter);
  }


  /**
   * This method adds right click menu for tableviewer.
   */
  private void addRightClickMenu() {
    final MenuManager menuMgr = new MenuManager();
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(mgr -> {
      IStructuredSelection selection = (IStructuredSelection) A2LFCPage.this.fcTableViewer.getSelection();
      final Object firstElement = selection.getFirstElement();
      if ((firstElement != null) && (selection.size() != 0)) {
        if (selection.size() == 1) {
          new CommonActionSet().setAddToScrachPadAction(menuMgr, firstElement);
          menuMgr.add(new Separator());

          new CDMCommonActionSet().addFCBCUsageMenuAction(menuMgr, firstElement, true);

          // Icdm 440 Passing the function list from the editor input
          SortedSet<Function> functionList =
              A2LFCPage.this.a2lContentsEditorInput.getA2lFileInfoBO().getAllSortedFunctions();
          // Icdm-521 New Action Set For Review Param
          new ReviewActionSet().addReviewParamEditor(menuMgr, firstElement, functionList, null, false);

          menuMgr.add(new Separator());
        }
        getPreCalibrationData(menuMgr, selection);

        menuMgr.add(new Separator());
        addExportAsLabAction(menuMgr, selection);
        addExportAsFunAction(menuMgr, selection);
      }
    });

    Menu menu = menuMgr.createContextMenu(this.fcTableViewer.getControl());
    this.fcTableViewer.getControl().setMenu(menu);
    // Register menu for extension.
    getSite().registerContextMenu(menuMgr, this.fcTableViewer);
  }

  /**
   * @param menuMgr
   * @param selection
   */
  private void addExportAsFunAction(final MenuManager menuMgr, final IStructuredSelection selection) {

    Set<String> functions = new HashSet<>();
    for (Object func : selection.toList()) {
      functions.add(((Function) func).getName());
    }
    menuMgr.add(new LabFunExportAction(null, functions, OUTPUT_FILE_TYPE.FUN,null));
  }

  /**
   * @param menuMgr
   * @param selection
   */
  private void addExportAsLabAction(final MenuManager menuMgr, final IStructuredSelection selection) {
    List<A2LParameter> a2lParams = getParamsFromFCs(selection);
    menuMgr.add(new LabFunExportAction(
        a2lParams.stream().map(A2LParameter::getName).collect(Collectors.toCollection(HashSet::new)), null,
        OUTPUT_FILE_TYPE.LAB,null));
  }

  // ICDM-1975
  /**
   * Gets the pre calibration data.
   *
   * @param menuMgr manu Manager
   * @param selection the selection
   */
  public void getPreCalibrationData(final MenuManager menuMgr, final IStructuredSelection selection) {
    final Action exportCdfxAction = new Action() {

      @Override
      public void run() {

        List<A2LParameter> a2lParams = getParamsFromFCs(selection);
        // Icdm-976 getting the Default shell
        Shell parent = Display.getCurrent().getActiveShell();
        IA2lParamTable paramTable = A2LFCPage.this.editor.getPrmNatFormPage();
        PreCalDataExportWizard exportCDFWizard =
            new PreCalDataExportWizard(a2lParams, A2LFCPage.this.a2lContentsEditorInput.getA2lFileInfoBO(),
                A2LFCPage.this.a2lContentsEditorInput.getPidcA2lBO(), paramTable);
        PreCalDataExportWizardDialog exportCDFWizardDialog = new PreCalDataExportWizardDialog(parent, exportCDFWizard);
        exportCDFWizardDialog.create();
        exportCDFWizardDialog.open();
      }
    };
    // iCDM-902
    exportCdfxAction.setText("Get Pre-Calibration Data");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.EXPORT_DATA_16X16);
    exportCdfxAction.setImageDescriptor(imageDesc);
    menuMgr.add(exportCdfxAction);
  }

  /**
   * @param selection
   * @return
   */
  private List<A2LParameter> getParamsFromFCs(final IStructuredSelection selection) {
    Collection<A2LParameter> paramInA2l =
        A2LFCPage.this.a2lContentsEditorInput.getA2lFileInfoBO().getA2lParamMap(null).values();
    List<A2LParameter> a2lParams = new ArrayList<>();

    for (Object a2lFCs : selection.toList()) {
      for (A2LParameter a2lParameter : paramInA2l) {
        if ((null != a2lParameter.getDefFunction()) &&
            a2lParameter.getDefFunction().getName().equals(((Function) a2lFCs).getName())) {
          a2lParams.add(a2lParameter);
        }
      }
    }
    return a2lParams;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
    if ((getSite().getPage().getActiveEditor() == getEditor()) && (part instanceof OutlineViewPart)) {
      selectionListener(selection);
    }
  }

  /**
   * input for status line.
   *
   * @param outlineSelection flag set according to selection made in viewPart or editor.
   */
  // ICDM-343
  public void setStatusBarMessage(final boolean outlineSelection) {
    if (this.fcTableViewer != null) {
      Collection<?> items = (Collection<?>) this.fcTableViewer.getInput();
      int totalItemCount = items.size();
      int filteredItemCount = this.fcTableViewer.getGrid().getItemCount();
      this.editor.updateStatusBar(outlineSelection, totalItemCount, filteredItemCount);
    }
  }

  /**
   * Selection listener implementation for selections on outlineFilter.
   *
   * @param selection the selection
   */
  private void selectionListener(final ISelection selection) {
    this.outlineFilter.a2lOutlineSelectionListener(selection);
    if (!this.fcTableViewer.getGrid().isDisposed()) {
      this.fcTableViewer.refresh();
      if (this.editor.getActivePage() == 4) {
        setStatusBarMessage(true);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFocus() {
    this.fcTableViewer.getControl().setFocus();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IToolBarManager getToolBarManager() {
    return this.nonScrollableForm.getToolBarManager();
  }

}
