package com.bosch.caltool.apic.ui.editors.pages;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.actions.RiskEvalPageToolBarActionSet;
import com.bosch.caltool.apic.ui.editors.PIDCEditor;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.apic.ui.views.providers.RiskEvalTreeViewContentProvider;
import com.bosch.caltool.apic.ui.views.providers.RiskEvalTreeViewLabelProvider;
import com.bosch.caltool.icdm.client.bo.apic.PidcRiskResultHandler;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.editors.AbstractGroupByNatFormPage;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.rm.PidcRmDefinition;
import com.bosch.caltool.icdm.model.rm.RmMetaData;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcRmDefClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.RmMetaDataClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;

/**
 * @author adn1cob
 */
public class RiskEvaluationPage extends AbstractGroupByNatFormPage implements ISelectionListener {

  /**
   * String constant for risk evaluation
   */
  private static final String RISK_EVALUATION = "Risk Evaluation";

  /** The editor. */
  private final PIDCEditor editor;

  /** The pidc version. */
  private PidcVersion pidcVersion;

  /** The non scrollable form. */
  private Form nonScrollableForm;

  /** The main composite. */
  private SashForm mainComposite;

  /** The form toolkit. */
  private FormToolkit formToolkit;

  /** The nat table section. */
  private RiskEvalNatTableSection natTableSection;

  /** The section left. */
  private Section sectionLeft;

  /** The left form. */
  private ScrolledForm leftForm;

  /** The rm tree viewer. */
  private TreeViewer rmTreeViewer;

  private PidcRiskResultHandler riskResultHandler;

  private List<PidcRmDefinition> riskDefList = new ArrayList<>();

  private ToolBarManager leftSectiontoolBarManager;

  private RiskEvalPageToolBarActionSet toolBarActionSet;

  /**
   * PidcRmDefinition
   */
  private PidcRmDefinition selPidcRmDef;

  /** Main page columns. */
  private static final int SASHFORM_COLS = 2;

  /** Composite weight. */
  private static final int BTM_COMP_WEIGHT2 = 5;

  /** Composite weight. */
  private static final int BTM_COMP_WEIGHT1 = 1;

  private static final String TITLE_TEXT = "TITLE_TEXT";

  /**
   * @param editor instance
   * @param pidVersion instance
   */
  public RiskEvaluationPage(final FormEditor editor, final PidcVersion pidVersion) {
    super(editor, RISK_EVALUATION, RISK_EVALUATION);
    this.editor = (PIDCEditor) editor;
    this.pidcVersion = pidVersion;
  }

  @Override
  public void createPartControl(final Composite parent) {
    this.nonScrollableForm = this.editor.getToolkit().createForm(parent);
    final GridData gridData = GridDataUtil.getInstance().getGridData();

    this.nonScrollableForm.getBody().setLayout(new GridLayout());
    this.nonScrollableForm.getBody().setLayoutData(gridData);
    setPageTitleText();
    createTitleDescription();
    addHelpAction((ToolBarManager) this.nonScrollableForm.getToolBarManager());

    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = SASHFORM_COLS;
    final GridData gridData1 = new GridData();
    gridData1.horizontalAlignment = GridData.FILL;
    gridData1.grabExcessHorizontalSpace = true;

    // create the main composite
    this.mainComposite = new SashForm(this.nonScrollableForm.getBody(), SWT.HORIZONTAL);
    this.mainComposite.setLayout(gridLayout);
    this.mainComposite.setLayoutData(gridData);
    final ManagedForm mform = new ManagedForm(parent);
    createFormContent(mform);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createFormContent(final IManagedForm managedForm) {
    // initiate the form tool kit
    this.formToolkit = managedForm.getToolkit();

    createMainComposite();

    // add listeners
    getSite().getPage().addSelectionListener(this);
  }

  /**
   * Creates the left composite.
   */
  private void createLeftComposite() {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    SashForm compositeOne = new SashForm(this.mainComposite, SWT.VERTICAL);
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 1;
    createLeftTreeSection(this.formToolkit, gridLayout, compositeOne);
    compositeOne.setLayout(gridLayout);
    compositeOne.setLayoutData(gridData);
  }

  /**
   *
   */
  private List<PidcRmDefinition> getPidcDefinitionWS() {
    PidcRmDefClient client = new PidcRmDefClient();
    List<PidcRmDefinition> outputList = null;
    try {
      outputList = client.getPidRmDefList(this.pidcVersion.getId());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return outputList;
  }


  /**
   * @return
   */
  private void getRiskEvalMetaDataWS() {
    RmMetaDataClient client = new RmMetaDataClient();
    try {
      RmMetaData metaData = client.getMetaData();
      this.riskResultHandler = new PidcRiskResultHandler(metaData);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    catch (Exception e) {
      CDMLogger.getInstance().error("Error occurred in loading : Risk MetaData", e, Activator.PLUGIN_ID);
    }
  }

  /**
   * Creates the left tree section.
   *
   * @param formToolkit the form toolkit
   * @param gridLayout the grid layout
   * @param compositeOne the composite one
   */
  private void createLeftTreeSection(final FormToolkit formToolkit, final GridLayout gridLayout,
      final SashForm compositeOne) {

    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.sectionLeft = formToolkit.createSection(compositeOne, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.sectionLeft.setText("Project Scope");
    this.sectionLeft.setExpanded(true);
    this.sectionLeft.getDescriptionControl().setEnabled(false);
    createLeftForm(formToolkit, gridLayout, this.sectionLeft);
    this.sectionLeft.setLayoutData(gridData);
    this.sectionLeft.setClient(this.leftForm);
  }

  /**
   * Creates the bottom composites.
   */
  private void createMainComposite() {

    // Tree Viewer
    createLeftComposite();

    // Nattable
    this.natTableSection = new RiskEvalNatTableSection(this.pidcVersion, this.riskResultHandler, this);
    this.natTableSection.createRightComposite();

    // Default tree selection
    if (CommonUtils.isNotEmpty(this.riskDefList)) {
      this.rmTreeViewer.setSelection(new StructuredSelection(this.riskDefList.get(0)), true);
      setSelPidcRmDef(this.riskDefList.get(0));
    }

    this.mainComposite.setWeights(new int[] { BTM_COMP_WEIGHT1, BTM_COMP_WEIGHT2 });
    setStatusBarMsg(false);
  }

  /**
   * Creates Title description
   */
  private void createTitleDescription() {
    String introText;
    try {
      introText = new CommonDataBO().getMessage(ApicConstants.RISK_EVALUATION, TITLE_TEXT);
      Label pageDescLbl = LabelUtil.getInstance().createLabel(this.nonScrollableForm.getBody(), introText);
      pageDescLbl.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
      Font boldFont =
          new Font(pageDescLbl.getDisplay(), new FontData(GUIHelper.DEFAULT_FONT.toString(), 10, SWT.LINE_SOLID));
      pageDescLbl.setFont(boldFont);
    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
    }

  }

  /**
   * Creates the left form.
   *
   * @param toolkit This method initializes form
   * @param gridLayout the grid layout
   * @param parent the parent
   */
  private void createLeftForm(final FormToolkit toolkit, final GridLayout gridLayout, final Section parent) {

    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.leftForm = toolkit.createScrolledForm(parent);
    this.leftForm.getBody().setLayoutData(gridData);
    this.leftForm.getBody().setLayout(gridLayout);

    final PatternFilter filter = new PatternFilter();
    final FilteredTree tree = new FilteredTree(this.leftForm.getBody(), SWT.BORDER, filter, true);
    // Get viewer and set styled layout for tree
    this.rmTreeViewer = tree.getViewer();
    this.rmTreeViewer.getTree().setLayoutData(gridData);
    // set auto expand level
    this.rmTreeViewer.setAutoExpandLevel(2);

    // Load MetaData
    getRiskEvalMetaDataWS();

    // Risk Definition Web Service
    this.riskDefList = getPidcDefinitionWS();
    // Set Content provider for the tree
    RiskEvalTreeViewContentProvider provider = new RiskEvalTreeViewContentProvider();
    this.rmTreeViewer.setContentProvider(provider);
    this.rmTreeViewer.setLabelProvider(new RiskEvalTreeViewLabelProvider());
    this.rmTreeViewer.setInput(this.riskDefList);
    this.rmTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

      @Override
      public void selectionChanged(final SelectionChangedEvent event) {
        final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        Object selected = selection.getFirstElement();
        if (selected instanceof PidcRmDefinition) {
          PidcRmDefinition riskDefn = (PidcRmDefinition) selected;
          RiskEvaluationPage.this.setSelPidcRmDef(riskDefn);
          RiskEvaluationPage.this.natTableSection.getProjCharMappingWS(riskDefn.getId(), false);
          RiskEvaluationPage.this.natTableSection.updateOverallProjStatus();
        }
      }
    });
    createToolBarAction();
  }

  /**
   *
   */
  private void createToolBarAction() {
    this.leftSectiontoolBarManager = new ToolBarManager(SWT.FLAT);
    this.toolBarActionSet = new RiskEvalPageToolBarActionSet();

    this.toolBarActionSet.addNewRiskEvalAction(this.leftSectiontoolBarManager);
    this.toolBarActionSet.riskEvalSettingsAction(this.leftSectiontoolBarManager);

    this.leftSectiontoolBarManager.update(true);
    Composite toolbarComposite = this.editor.getToolkit().createComposite(this.sectionLeft);
    toolbarComposite.setBackground(null);
    this.leftSectiontoolBarManager.createControl(toolbarComposite);
    this.sectionLeft.setTextClient(toolbarComposite);
  }

  /**
   * Sets the page title text.
   */
  private void setPageTitleText() {
    this.nonScrollableForm
        .setText(CommonUtils.concatenate(this.editor.getPartName().replaceAll("&", "&&"), " - ", RISK_EVALUATION));
  }

  @Override
  public Control getPartControl() {
    return this.nonScrollableForm;
  }


  /**
   * Sets the page title
   *
   * @param title String
   */
  public void setTitleText(final String title) {
    if (this.nonScrollableForm != null) {
      this.nonScrollableForm.setText(title);
    }
  }


  // this method is added to prevent
  // "java.lang.RuntimeException: WARNING: Prevented recursive attempt to activate part
  // org.eclipse.ui.views.PropertySheet while still in the middle of activating part"
  @Override
  public void setFocus() {
    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setFocus();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected IToolBarManager getToolBarManager() {
    return this.nonScrollableForm.getToolBarManager();
  }


  /**
   * @return the pidcVersion
   */
  public PidcVersion getPidcVersion() {
    return this.pidcVersion;
  }


  /**
   * @param pidcVersion the pidcVersion to set
   */
  public void setPidcVersion(final PidcVersion pidcVersion) {
    this.pidcVersion = pidcVersion;
  }

  /**
   * @return
   */
  public Composite getMainComposite() {
    return this.mainComposite;
  }

  /**
   * @return
   */
  public FormToolkit getFormToolkit() {
    return this.formToolkit;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void selectionChanged(final IWorkbenchPart arg0, final ISelection arg1) {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateStatusBar(final boolean outlineSelection) {
    super.updateStatusBar(outlineSelection);
    setStatusBarMsg(outlineSelection);
  }

  /**
   * @param outlineSelection
   */
  void setStatusBarMsg(final boolean outlineSelection) {
    if ((this.natTableSection != null) && (this.natTableSection.getCustomFilterGridLayer() != null)) {
      int totalItemCount = this.natTableSection.getTotalRowCount();
      int filteredItemCount =
          this.natTableSection.getCustomFilterGridLayer().getRowHeaderLayer().getPreferredRowCount();
      final StringBuilder buf = new StringBuilder(40);
      buf.append("Displaying : ").append(filteredItemCount).append(" out of ").append(totalItemCount)
          .append(" records ");
      IStatusLineManager statusLine;
      // Updation of status based on selection in view part
      if (outlineSelection) {
        // in case of outline selection
        final IViewSite viewPartSite = (IViewSite) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .findView(ApicUiConstants.OUTLINE_TREE_VIEW).getSite();
        // get the status line manager from the outline
        statusLine = viewPartSite.getActionBars().getStatusLineManager();
      }
      else {
        // get the status line manager from the editor
        statusLine = this.editor.getEditorSite().getActionBars().getStatusLineManager();
      }
      statusLine.setMessage(buf.toString());
      statusLine.update(true);
    }
  }

  /**
   * @return the selPidcRmDef
   */
  public PidcRmDefinition getSelPidcRmDef() {
    return this.selPidcRmDef;
  }

  /**
   * @param selPidcRmDef the selPidcRmDef to set
   */
  public void setSelPidcRmDef(final PidcRmDefinition selPidcRmDef) {
    this.selPidcRmDef = selPidcRmDef;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setStatusBarMessage(final boolean outlineSelction) {
    setStatusBarMsg(outlineSelction);

  }
}