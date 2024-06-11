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
import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.common.ui.actions.CDMCommonActionSet;
import com.bosch.caltool.icdm.common.ui.dragdrop.CustomDragListener;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.providers.SelectionProviderMediator;
import com.bosch.caltool.icdm.common.ui.services.IA2lParamTable;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.OutlineViewPart;
import com.bosch.caltool.icdm.common.ui.wizards.PreCalDataExportWizard;
import com.bosch.caltool.icdm.common.ui.wizards.PreCalDataExportWizardDialog;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponentFunctions;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponents;
import com.bosch.caltool.icdm.ui.action.LabFunExportAction;
import com.bosch.caltool.icdm.ui.editors.A2LContentsEditor;
import com.bosch.caltool.icdm.ui.editors.A2LContentsEditorInput;
import com.bosch.caltool.icdm.ui.sorters.BCGridTabViewerSorter;
import com.bosch.caltool.icdm.ui.table.filters.BCTableFilters;
import com.bosch.caltool.icdm.ui.table.filters.OutlineFilter;
import com.bosch.caltool.icdm.ui.util.IMessageConstants;
import com.bosch.caltool.icdm.ui.util.Messages;
import com.bosch.caltool.icdm.ui.views.providers.BCFormPageTableLabelProvider;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;

/**
 * @author adn1cob
 */
public class A2LBCPage extends AbstractFormPage implements ISelectionListener {

  /**
   * Constant for page 3
   */
  private static final int PAGE_1 = 1;
  private FormToolkit formToolkit; // @jve:decl-index=0:visual-constraint=""
  private Composite composite;
  private Section section;
  private Form baseForm;

  private GridTableViewer bcTableViewer;
  private GridViewerColumn bcNameColumn;
  private GridViewerColumn versionColumn;
  private GridViewerColumn revColumn;
  private GridViewerColumn stateColumn;
  private GridViewerColumn longNameColumn;

  /**
   * Filter text instance
   */
  private Text filterTxt;
  /**
   * FCTableFilters instance
   */
  private BCTableFilters bcFilter;
  private BCGridTabViewerSorter bcTabSorter;

  /**
   * Editor instance
   */
  private final A2LContentsEditor editor;

  /**
   * Non scrollable form
   */
  private Form nonScrollableForm;
  /**
   * OutlineFilter instance
   */
  private OutlineFilter outlineFilter;

  /**
   * BC page constructor
   *
   * @param editor editor
   */
  public A2LBCPage(final FormEditor editor) {
    super(editor, "BC", Messages.getString("BCFormPage.label")); //$NON-NLS-1$ //$NON-NLS-2$
    this.editor = (A2LContentsEditor) editor;
  }

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

  @Override
  public Control getPartControl() {
    return this.nonScrollableForm;
  }

  /**
   *
   */
  @Override
  protected void createFormContent(final IManagedForm managedForm) {
    this.formToolkit = managedForm.getToolkit();
    createComposite(this.formToolkit);
    // add listeners
    getSite().getPage().addSelectionListener(this);
  }

  /**
   * @param managedForm
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
   * @param toolkit This method initializes section
   */
  private void createSection(final FormToolkit toolkit) {

    GridData gridData = getGridData();
    this.section = toolkit.createSection(this.composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.section.setText("Base Components");
    this.section.setExpanded(true);
    // ICDM-183
    this.section.getDescriptionControl().setEnabled(false);
    createForm(toolkit);

    this.section.setLayoutData(gridData);

    this.section.setClient(this.baseForm);
  }

  /**
   * @param toolkit This method initializes form
   */
  private void createForm(final FormToolkit toolkit) {

    GridData gridData = getGridData();
    this.baseForm = toolkit.createForm(this.section);
    this.filterTxt = toolkit.createText(this.baseForm.getBody(), null, SWT.SINGLE | SWT.BORDER);
    createFilterTxt();
    this.bcTabSorter = new BCGridTabViewerSorter();
    this.bcTableViewer = new GridTableViewer(this.baseForm.getBody(),
        SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
    this.bcTableViewer.getGrid().setLayoutData(gridData);

    this.bcTableViewer.getGrid().setLinesVisible(true);
    this.bcTableViewer.getGrid().setHeaderVisible(true);

    this.baseForm.getBody().setLayout(new GridLayout());
    createVersionsTabColumns();
    this.bcTableViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.bcTableViewer.setLabelProvider(new BCFormPageTableLabelProvider());

    SortedSet<A2LBaseComponents> listBC = ((A2LContentsEditorInput) getEditorInput()).getA2lFileInfoBO().getA2lBCInfo();
    this.bcTableViewer.setInput(listBC);
    this.bcTableViewer.getGrid().addFocusListener(new FocusListener() {

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
    invokeColumnSorter(this.bcTabSorter);
    addRightClickMenu();

    A2LContentsEditor a2lContentsEditor = (A2LContentsEditor) A2LBCPage.this.getEditor();
    SelectionProviderMediator selectionProviderMediator = a2lContentsEditor.getSelectionProviderMediator();
    selectionProviderMediator.addViewer(A2LBCPage.this.bcTableViewer);
    getSite().setSelectionProvider(selectionProviderMediator);
    // ICDM-1011
    Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };
    this.bcTableViewer.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE, transferTypes,
        new CustomDragListener(this.bcTableViewer));
  }

  /**
   * /** Method to set selection to properties view
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
   * input for status line
   *
   * @param outlineSelection flag set according to selection made in viewPart or editor.
   */
  // ICDM-343
  public void setStatusBarMessage(final boolean outlineSelection) {
    Collection<?> items = (Collection<?>) this.bcTableViewer.getInput();
    int totalItemCount = items.size();
    int filteredItemCount = this.bcTableViewer.getGrid().getItemCount();
    this.editor.updateStatusBar(outlineSelection, totalItemCount, filteredItemCount);
  }

  /**
   * Add sorter for the table columns
   *
   * @param bcTabSorter2
   */
  private void invokeColumnSorter(final BCGridTabViewerSorter sorter) {
    this.bcTableViewer.setComparator(sorter);
  }

  /**
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
   * Defines the columns of the TableViewer
   */
  private void createVersionsTabColumns() {

    this.bcNameColumn = new GridViewerColumn(this.bcTableViewer, SWT.NONE);
    this.bcNameColumn.getColumn().setText("BC Name");
    this.bcNameColumn.getColumn().setWidth(200);
    // Add column selection listener
    this.bcNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(this.bcNameColumn.getColumn(), 0, this.bcTabSorter, this.bcTableViewer));

    this.versionColumn = new GridViewerColumn(this.bcTableViewer, SWT.NONE);
    this.versionColumn.getColumn().setText("Version");
    this.versionColumn.getColumn().setWidth(80);
    // Add column selection listener
    this.versionColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(this.versionColumn.getColumn(), 1, this.bcTabSorter, this.bcTableViewer));

    this.revColumn = new GridViewerColumn(this.bcTableViewer, SWT.NONE);
    this.revColumn.getColumn().setText("Revision");
    this.revColumn.getColumn().setWidth(80);
    // Add column selection listener
    this.revColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(this.revColumn.getColumn(), 2, this.bcTabSorter, this.bcTableViewer));


    this.stateColumn = new GridViewerColumn(this.bcTableViewer, SWT.NONE);
    this.stateColumn.getColumn().setText("State");
    this.stateColumn.getColumn().setWidth(170);
    // Add column selection listener
    this.stateColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(this.stateColumn.getColumn(), 3, this.bcTabSorter, this.bcTableViewer));

    this.longNameColumn = new GridViewerColumn(this.bcTableViewer, SWT.NONE);
    this.longNameColumn.getColumn().setText("Long Name");
    this.longNameColumn.getColumn().setWidth(200);
    // Add column selection listener
    this.longNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(this.longNameColumn.getColumn(), 4, this.bcTabSorter, this.bcTableViewer));

  }

  /**
   * This method creates filter text
   */
  private void createFilterTxt() {
    GridData gridData = getFilterTxtGridData();
    this.filterTxt.setLayoutData(gridData);
    this.filterTxt.setMessage(Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.filterTxt.addModifyListener(event -> {
      String text = A2LBCPage.this.filterTxt.getText().trim();
      A2LBCPage.this.bcFilter.setFilterText(text);
      A2LBCPage.this.bcTableViewer.refresh();
      setStatusBarMessage(false);
    });
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
   * Add filters for the table
   */
  private void addFilters() {
    this.bcFilter = new BCTableFilters();
    // Add TableViewer filter
    this.bcTableViewer.addFilter(this.bcFilter);

    // ICDM-984 Add outline filters
    this.outlineFilter = new OutlineFilter(this.editor);
    this.bcTableViewer.addFilter(this.outlineFilter);

    addHelpAction((ToolBarManager) getToolBarManager());
    addResetAllFiltersAction();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setActive(final boolean active) {
    if (this.editor.getActivePage() == 1) {
      final IStructuredSelection selection = (IStructuredSelection) this.bcTableViewer.getSelection();
      if ((selection != null) && (!selection.isEmpty())) {
        setSelectedNodeAccesssPropertiesView(selection);
      }
    }
    super.setActive(active);
  }


  /**
   * Add reset filter button ICDM-1207
   */
  private void addResetAllFiltersAction() {
    getFilterTxtSet().add(this.filterTxt);
    getRefreshComponentSet().add(this.bcTableViewer);
    addResetFiltersAction();
  }

  /**
   * Selection listener implementation for selections on outlineFilter
   *
   * @param selection
   */
  private void selectionListener(final ISelection selection) {
    this.outlineFilter.a2lOutlineSelectionListener(selection);
    if (!this.bcTableViewer.getGrid().isDisposed()) {
      this.bcTableViewer.refresh();
      if (this.editor.getActivePage() == PAGE_1) {
        setStatusBarMessage(true);
      }
    }
  }

  /**
   * This method adds right click menu for tableviewer
   */
  private void addRightClickMenu() {
    final MenuManager menuMgr = new MenuManager();
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(mgr -> {
      IStructuredSelection selection = (IStructuredSelection) A2LBCPage.this.bcTableViewer.getSelection();
      final Object firstElement = selection.getFirstElement();
      if ((firstElement != null) && (selection.size() != 0)) {
        if (selection.size() == 1) {
          CDMCommonActionSet cdmCommonActionSet = new CDMCommonActionSet();
          cdmCommonActionSet.addFCBCUsageMenuAction(menuMgr, firstElement, true);
        }
        menuMgr.add(new Separator());
        getPreCalibrationData(menuMgr, selection);

        menuMgr.add(new Separator());
        addExportAsLabAction(menuMgr, selection);
        addExportAsFunAction(menuMgr, selection);
      }
    });

    Menu menu = menuMgr.createContextMenu(this.bcTableViewer.getControl());
    this.bcTableViewer.getControl().setMenu(menu);
    // Register menu for extension.
    getSite().registerContextMenu(menuMgr, this.bcTableViewer);
  }

  /**
   * @param menuMgr
   * @param selection
   */
  private void addExportAsLabAction(final MenuManager menuMgr, final IStructuredSelection selection) {
    final A2LContentsEditorInput a2lContentsEditorInput =
        (A2LContentsEditorInput) (A2LBCPage.this.editor.getEditorInput());
    List<A2LParameter> a2lParams = getParamsFromBCs(selection, a2lContentsEditorInput);

    menuMgr.add(new LabFunExportAction(
        a2lParams.stream().map(A2LParameter::getName).collect(Collectors.toCollection(HashSet::new)), null,
        OUTPUT_FILE_TYPE.LAB, null));
  }

  /**
   * @param menuMgr
   * @param selection
   */
  private void addExportAsFunAction(final MenuManager menuMgr, final IStructuredSelection selection) {
    menuMgr.add(new LabFunExportAction(null, getFCsFromBCs(selection), OUTPUT_FILE_TYPE.FUN, null));
  }

  // ICDM-1975
  /**
   * @param menuMgr manu Manager
   * @param paramList all the selected param Icdm-697 Action for opening the Export Wizard
   * @param a2lFile sel a2lfile
   * @param a2lContentsEditorInput
   * @param sortedSet set of sysconts
   */
  public void getPreCalibrationData(final MenuManager menuMgr, final IStructuredSelection selection) {
    final A2LContentsEditorInput a2lContentsEditorInput =
        (A2LContentsEditorInput) (A2LBCPage.this.editor.getEditorInput());
    final Action exportCdfxAction = new Action() {

      @Override
      public void run() {
        List<A2LParameter> a2lParams = getParamsFromBCs(selection, a2lContentsEditorInput);
        Shell parent = Display.getCurrent().getActiveShell();
        IA2lParamTable paramTable = A2LBCPage.this.editor.getPrmNatFormPage();
        PreCalDataExportWizard exportCDFWizard = new PreCalDataExportWizard(a2lParams,
            a2lContentsEditorInput.getA2lFileInfoBO(), a2lContentsEditorInput.getPidcA2lBO(), paramTable);
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
  private Set<String> getFCsFromBCs(final IStructuredSelection selection) {
    Set<String> a2lFCs = new HashSet<>();
    for (Object a2lBaseComponents : selection.toList()) {
      for (A2LBaseComponentFunctions a2lFc : ((A2LBaseComponents) a2lBaseComponents).getFunctionsList()) {
        a2lFCs.add(a2lFc.getName());
      }
    }
    return a2lFCs;
  }

  /**
   * @param selection
   * @param a2lContentsEditorInput
   * @return
   */
  private List<A2LParameter> getParamsFromBCs(final IStructuredSelection selection,
      final A2LContentsEditorInput a2lContentsEditorInput) {
    Collection<A2LParameter> paramInA2l = a2lContentsEditorInput.getA2lFileInfoBO().getA2lParamMap(null).values();
    List<A2LParameter> a2lParams = new ArrayList<>();
    for (Object a2lBaseComponents : selection.toList()) {
      for (A2LBaseComponentFunctions a2lFCs : ((A2LBaseComponents) a2lBaseComponents).getFunctionsList()) {
        getSelA2lParams(paramInA2l, a2lParams, a2lFCs);
      }
    }
    return a2lParams;
  }

  /**
   * @param paramInA2l
   * @param a2lParams
   * @param a2lFCs
   */
  private void getSelA2lParams(final Collection<A2LParameter> paramInA2l, final List<A2LParameter> a2lParams,
      final A2LBaseComponentFunctions a2lFCs) {
    for (A2LParameter a2lParameter : paramInA2l) {
      if ((null != a2lParameter.getDefFunction()) && a2lParameter.getDefFunction().getName().equals(a2lFCs.getName())) {
        a2lParams.add(a2lParameter);
      }
    }
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
   * {@inheritDoc}
   */
  @Override
  protected IToolBarManager getToolBarManager() {
    return this.nonScrollableForm.getToolBarManager();
  }
}