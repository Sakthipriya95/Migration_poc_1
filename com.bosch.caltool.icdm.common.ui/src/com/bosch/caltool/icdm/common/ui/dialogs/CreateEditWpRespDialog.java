/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.dialogs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.A2lWPValidationBO;
import com.bosch.caltool.icdm.common.bo.a2l.A2lResponsibilityCommon;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.actions.UpdateWpRespToolBarActionSet;
import com.bosch.caltool.icdm.common.ui.sorter.WPSorter;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.views.providers.ComboViewerContentPropsalProvider;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpResponsibilityServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * @author elm1cob
 */
public class CreateEditWpRespDialog extends AbstractDialog {

  /**
   * Top composite
   */
  private Composite top;
  /**
   * Composite instance for the dialog
   */
  private Composite composite;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;
  /**
   * Section instance
   */
  private Section workPackageSection;
  /**
   * Section instance
   */
  private ResponsibilitySection responsibilitySection;
  /**
   * Form instance
   */
  private Form workPkgForm;
  /**
   * Text field for Work Package
   */
  protected ComboViewer workPackageCombo;

  private UpdateWpRespToolBarActionSet toolBarActionSet;

  /**
   * save button
   */
  private Button saveButton;
  /**
   * A2lwpInfoBo object
   */
  private final A2LWPInfoBO a2lWpInfoBo;
  /**
   * A2lWpResponsibility object
   */
  private final A2lWpResponsibility respPalObj;
  private A2lWorkPackage selectedWP;

  private Map<Long, A2lWorkPackage> comoboViewerInput = new HashMap<>();
  /**
   * Workpackage Validation
   */
  private final A2lWPValidationBO validationBO;
  /**
   * Create or Edit mode dialog
   */
  private final boolean editFlagMode;
  private A2lResponsibility a2lResp;

  private A2lWpResponsibility new2lWpRespObj;

  /**
   * @param parentShell Shell
   * @param wPInfoBO A2lwpInfoBo
   * @param selRespPal A2lWpResponsibility
   * @param editModeFlag boolean
   */
  public CreateEditWpRespDialog(final Shell parentShell, final A2LWPInfoBO wPInfoBO,
      final A2lWpResponsibility selRespPal, final boolean editModeFlag) {
    super(parentShell);
    this.a2lWpInfoBo = wPInfoBO;
    this.respPalObj = selRespPal.clone();
    this.editFlagMode = editModeFlag;
    this.validationBO = new A2lWPValidationBO(this.a2lWpInfoBo);
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
    displayDialogMsg();
    return contents;
  }


  public A2LWPInfoBO gtA2lWoInfoBo() {
    return this.a2lWpInfoBo;
  }

  /**
   * Display dialog message
   */
  private void displayDialogMsg() {
    // Set the title
    if (this.editFlagMode) {
      setTitle("Edit Work package");
      setMessage("Edit work package details", IMessageProvider.INFORMATION);
    }
    else {
      setTitle("Create Work package");
      setMessage("Enter work package details", IMessageProvider.INFORMATION);
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Work package Responsibility");
    super.configureShell(newShell);
  }

  /**
   * {@inheritDoc}
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
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    createWorkPkgSection();
    createRespSection();
    this.composite.setLayoutData(gridData);
  }

  /**
   * This method initializes section
   */
  private void createWorkPkgSection() {
    this.workPackageSection = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "Work Package");
    this.workPackageSection
        .setDescription("Select Work Package from the drop down/use add button to add new Work Package");
    this.workPackageSection.getDescriptionControl().setEnabled(false);
    createWorkPkgForm();
    this.workPackageSection.setClient(this.workPkgForm);
  }

  /**
   * This method initializes form
   */
  private void createWorkPkgForm() {

    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    this.workPkgForm = getFormToolkit().createForm(this.workPackageSection);
    this.workPkgForm.getBody().setLayout(gridLayout);
    addToolBarActions();
    getFormToolkit().createLabel(this.workPkgForm.getBody(), "Name ");
    this.workPackageCombo = new ComboViewer(this.workPkgForm.getBody(), SWT.DROP_DOWN);
    this.workPackageCombo.getCombo().setLayoutData(GridDataUtil.getInstance().getTextGridData());
    fillComboViewer();
    loadWorkPkgDetails();
    addSelectionListener();
  }


  private void addToolBarActions() {
    this.toolBarActionSet = new UpdateWpRespToolBarActionSet(this);
    final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
    final ToolBar toolbar = toolBarManager.createControl(this.workPackageSection);
    this.toolBarActionSet.addCreateWpAction(toolBarManager);
    this.toolBarActionSet.addEditWpAction(toolBarManager);
    this.toolBarActionSet.getEditAction().setEnabled(this.editFlagMode);
    toolBarManager.update(true);
    this.workPackageSection.setTextClient(toolbar);
  }

  /**
   *
   */
  private void addSelectionListener() {
    this.workPackageCombo.addSelectionChangedListener(selectionChangedEvent -> {

      IStructuredSelection selection = (IStructuredSelection) selectionChangedEvent.getSelection();
      if (selection.getFirstElement() instanceof A2lWorkPackage) {
        A2lWorkPackage respPal = (A2lWorkPackage) selection.getFirstElement();
        CreateEditWpRespDialog.this.selectedWP = respPal;
        CreateEditWpRespDialog.this.respPalObj.setA2lWpId(respPal.getId());
        enableSave(true);
        CreateEditWpRespDialog.this.toolBarActionSet.getEditAction().setEnabled(true);
      }
    });

    this.workPackageCombo.getCombo().addKeyListener(new KeyListener() {

      @Override
      public void keyReleased(final KeyEvent keyevent) {
        enableSave(true);
      }

      @Override
      public void keyPressed(final KeyEvent keyevent) {
        // no need for implementation
      }

    });

  }


  /**
   * @return - the selected WP
   */
  public A2lWorkPackage getSelectedWp() {
    return this.selectedWP;
  }


  /**
   *
   */
  private void fillComboViewer() {
    this.workPackageCombo.setContentProvider(ArrayContentProvider.getInstance());
    this.workPackageCombo.setLabelProvider(new LabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof A2lWorkPackage) {
          A2lWorkPackage respPal = (A2lWorkPackage) element;
          return respPal.getName();
        }
        return "";
      }
    });
    removeAlreadyMappedWps();
    setInputForCombo();
  }

  /**
   */
  private void removeAlreadyMappedWps() {
    if (this.a2lWpInfoBo.getSelectedA2lVarGroup() != null) {
      this.comoboViewerInput = this.a2lWpInfoBo.getWpsForVarGrpLevel();
    }
    else {
      this.comoboViewerInput = this.a2lWpInfoBo.getWpsForDefaultLevel();
    }
  }

  /**
   */
  protected void setInputForCombo() {
    this.workPackageCombo.setComparator(new WPSorter());
    this.workPackageCombo.setInput(this.comoboViewerInput.values());
    // enable filtering WP when WP name is typed on the combo
    setWpComboContentProposal();
  }

  private void setWpComboContentProposal() {
    IContentProposalProvider provider =
        new ComboViewerContentPropsalProvider(this.workPackageCombo.getCombo().getItems());
    ContentProposalAdapter adapter =
        new ContentProposalAdapter(this.workPackageCombo.getCombo(), new ComboContentAdapter(), provider, null, null);
    adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
    adapter.setProposalPopupFocus();
    adapter.addContentProposalListener(arg0 -> {
      String selectedWPName = arg0.getContent();
      Collection<A2lWorkPackage> wps = CreateEditWpRespDialog.this.a2lWpInfoBo.getAllWpMapppedToPidcVers().values();
      for (A2lWorkPackage wp : wps) {
        if (wp.getName().equals(selectedWPName)) {
          CreateEditWpRespDialog.this.selectedWP = wp;
          CreateEditWpRespDialog.this.respPalObj.setA2lWpId(wp.getId());
          enableSave(true);
        }
      }
    });
  }

  /**
   * Enable save button method
   *
   * @param flag enable save
   */
  public void enableSave(final boolean flag) {

    if (this.a2lWpInfoBo.getAllWpMapppedToPidcVers().values().contains(this.selectedWP) && flag &&
        this.selectedWP.getName().equalsIgnoreCase(this.workPackageCombo.getCombo().getText())) {
      this.saveButton.setEnabled((!CommonUtils.isEmptyString(this.workPackageCombo.getCombo().getText())) &&
          this.responsibilitySection.isRespSelected());
    }
    else {
      this.saveButton.setEnabled(false);
    }
  }

  /**
   * This method initializes section
   */
  private void createRespSection() {
    this.responsibilitySection =
        new ResponsibilitySection(this.composite, getFormToolkit(), true, this.respPalObj, this.a2lWpInfoBo);
    this.responsibilitySection.createRespSection(getShell(), null);
    addRespButtonListeners();
    loadResponsibilityDetails();
  }

  /**
   * Radio button listeners for Responsibility section
   */
  private void addRespButtonListeners() {
    boschBtnListener();
    boschDeptBtnListener();
    customerBtnListener();
    otherBtnListener();
    respUserBtnListeners();
  }

  /**
   *
   */
  private void boschDeptBtnListener() {
    this.responsibilitySection.getBoschDeptButton().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        if (CreateEditWpRespDialog.this.responsibilitySection.getBoschDeptButton().getSelection()) {
          CreateEditWpRespDialog.this.responsibilitySection.getUserNameTextBox().setText("");
          CreateEditWpRespDialog.this.responsibilitySection.getClearUserButton().setEnabled(false);
          if (CreateEditWpRespDialog.this.editFlagMode && isBoschDeptRecord()) {
            CreateEditWpRespDialog.this.responsibilitySection.getUserNameTextBox()
                .setText(CreateEditWpRespDialog.this.a2lResp.getAliasName());
            CreateEditWpRespDialog.this.responsibilitySection.getClearUserButton().setEnabled(true);
          }
          CreateEditWpRespDialog.this.responsibilitySection.enableUserSearch(true);
          enableSave(true);
        }
      }
    });
  }

  /**
   * Open search user dialog based on the selection
   */
  private void respUserBtnListeners() {
    this.responsibilitySection.getSearchButton().addSelectionListener(new SelectionAdapter() {

      /**
       * re {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        CreateEditWpRespDialog.this.responsibilitySection.openUsersDialog();
        String newUserName = CreateEditWpRespDialog.this.responsibilitySection.getUserNameTextBox().getText();
        if (null != CreateEditWpRespDialog.this.a2lResp) {
          enableSave(!(CreateEditWpRespDialog.this.a2lResp.getAliasName().equals(newUserName) ||
              CreateEditWpRespDialog.this.a2lResp.getName().equals(newUserName)));
        }
        else {
          enableSave(true);
        }
        CreateEditWpRespDialog.this.responsibilitySection.getClearUserButton().setEnabled(!"".equals(newUserName));
      }
    });

    this.responsibilitySection.getClearUserButton().addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        CreateEditWpRespDialog.this.responsibilitySection.getUserNameTextBox().setText("");
        CreateEditWpRespDialog.this.responsibilitySection.getClearUserButton().setEnabled(false);
       // When creating new Work package, a2lResp obj will be null before saving
        if (CommonUtils.isNotNull(CreateEditWpRespDialog.this.a2lResp)) {
          enableSave(!isBoschDeptRecord());
        }
      }
    });
  }


  /**
   * @return the workPackageCombo
   */
  public ComboViewer getWorkPackageCombo() {
    return this.workPackageCombo;
  }

  /**
   * Others radio button listener
   */
  private void otherBtnListener() {
    this.responsibilitySection.getOthersButton().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        if (CreateEditWpRespDialog.this.responsibilitySection.getOthersButton().getSelection()) {
          CreateEditWpRespDialog.this.responsibilitySection.getUserNameTextBox().setText("");
          CreateEditWpRespDialog.this.responsibilitySection.getClearUserButton().setEnabled(false);
          if (CreateEditWpRespDialog.this.editFlagMode &&
              "O".equals(CreateEditWpRespDialog.this.a2lResp.getRespType())) {
            CreateEditWpRespDialog.this.responsibilitySection.getUserNameTextBox()
                .setText(CreateEditWpRespDialog.this.a2lWpInfoBo.getLFullName(CreateEditWpRespDialog.this.a2lResp));
            CreateEditWpRespDialog.this.responsibilitySection.getClearUserButton().setEnabled(true);
          }
          CreateEditWpRespDialog.this.responsibilitySection.enableUserSearch(true);
          enableSave(true);
        }
      }
    });
  }

  /**
   * Customer Radio button listener
   */
  private void customerBtnListener() {
    this.responsibilitySection.getCustomerButton().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        if (CreateEditWpRespDialog.this.responsibilitySection.getCustomerButton().getSelection()) {

          CreateEditWpRespDialog.this.responsibilitySection.getUserNameTextBox().setText("");
          CreateEditWpRespDialog.this.responsibilitySection.getClearUserButton().setEnabled(false);
          boolean a2lRespType = false;
          if (null != CreateEditWpRespDialog.this.a2lResp) {
            a2lRespType = "C".equals(CreateEditWpRespDialog.this.a2lResp.getRespType());
          }
          if (CreateEditWpRespDialog.this.editFlagMode && a2lRespType) {
            CreateEditWpRespDialog.this.responsibilitySection.getUserNameTextBox()
                .setText(CreateEditWpRespDialog.this.a2lWpInfoBo.getLFullName(CreateEditWpRespDialog.this.respPalObj));
            CreateEditWpRespDialog.this.responsibilitySection.getClearUserButton().setEnabled(true);
          }
          CreateEditWpRespDialog.this.responsibilitySection.enableUserSearch(true);
          enableSave(!(CreateEditWpRespDialog.this.editFlagMode && a2lRespType));

        }
      }
    });

  }

  /**
   * checks whether the selected A2lResponsibility object is a Bosch Dept Record
   */
  private boolean isBoschDeptRecord() {
    return "R".equals(this.a2lResp.getRespType()) && !isBoschUserRecord() && !isCustOrOtherRecord();
  }

  /**
   * checks whether the selected A2lResponsibility object is a Bosch User Record
   */
  private boolean isBoschUserRecord() {
    return (this.a2lResp.getUserId() != null) || isRespBosch() || isRespAndDeptBosch();
  }

  /**
   * @return
   */
  private boolean isRespBosch() {
    return (this.a2lResp.getLDepartment() == null) && !isCustOrOtherRecord();
  }

  /**
   * @return
   */
  private boolean isRespAndDeptBosch() {
    return "R".equals(this.a2lResp.getRespType()) &&
        ("RB".equals(this.a2lResp.getLDepartment()) || this.a2lResp.getLDepartment().contains("bosch.com"));
  }

  /**
   * checks whether the selected A2lResponsibility object is a Customer or Other Record
   */
  private boolean isCustOrOtherRecord() {
    return "C".equals(this.a2lResp.getRespType()) || "O".equals(this.a2lResp.getRespType());
  }

  /**
   * Bosch radio button listener
   */
  private void boschBtnListener() {
    this.responsibilitySection.getBoschButton().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        if (CreateEditWpRespDialog.this.responsibilitySection.getBoschButton().getSelection()) {
          CreateEditWpRespDialog.this.responsibilitySection.getUserNameTextBox().setText("");
          CreateEditWpRespDialog.this.responsibilitySection.getClearUserButton().setEnabled(false);
          if (CreateEditWpRespDialog.this.editFlagMode && isBoschUserRecord()) {
            if (null != CreateEditWpRespDialog.this.a2lResp.getUserId()) {
              User user = CreateEditWpRespDialog.this.a2lWpInfoBo.getA2lResponsibilityModel().getUserMap()
                  .get(CreateEditWpRespDialog.this.a2lResp.getUserId());
              // set the user
              CreateEditWpRespDialog.this.responsibilitySection.setBoschUser(user);
              CreateEditWpRespDialog.this.responsibilitySection.getUserNameTextBox().setText(user.getDescription());
            }
            else {
              CreateEditWpRespDialog.this.responsibilitySection.getUserNameTextBox()
                  .setText(CreateEditWpRespDialog.this.a2lResp.getName());
            }
            CreateEditWpRespDialog.this.responsibilitySection.getClearUserButton().setEnabled(true);
          }
          CreateEditWpRespDialog.this.responsibilitySection.enableUserSearch(true);
          enableSave(!(CreateEditWpRespDialog.this.editFlagMode && isBoschUserRecord()));
        }
      }
    });
  }

  /**
   * @param selectedA2lResp
   */
  private void setA2lRespToRespPal(final A2lResponsibility selectedA2lResp) {
    if (null != selectedA2lResp) {
      this.respPalObj.setA2lRespId(selectedA2lResp.getId());
    }
    enableSave(null != selectedA2lResp);
  }


  private void loadResponsibilityDetails() {
    this.responsibilitySection.resetRadioButtons();

    if (null != this.respPalObj.getA2lRespId()) {

      this.a2lResp =
          this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap().get(this.respPalObj.getA2lRespId());
      WpRespType respType = A2lResponsibilityCommon.getRespType(this.a2lResp);

      switch (respType) {
        case RB:
          setBoschOrBoschDeptRadioBtn();
          break;
        case CUSTOMER:
          setCustomerRadioButton(this.a2lResp);
          break;
        case OTHERS:
          setOtherRespRadioButton(this.a2lResp);
          break;
        default:
          break;
      }
    }
    else {
      // Default selection
      this.responsibilitySection.getBoschButton().setSelection(true);
    }
    this.responsibilitySection.getClearUserButton().setEnabled(this.editFlagMode);
    this.responsibilitySection.enableUserSearch(true);
    this.responsibilitySection.resizeDialog();
  }

  /**
   *
   */
  private void setBoschOrBoschDeptRadioBtn() {
    if (isBoschDeptRecord()) {
      setBoschDeptRadioBtn(this.a2lResp);
    }
    else {
      setBoschRadioButton(this.a2lResp);
    }
  }

  /**
   * @param a2lResp
   */
  private void setBoschDeptRadioBtn(final A2lResponsibility a2lResp) {
    this.responsibilitySection.getBoschDeptButton().setSelection(true);
    this.responsibilitySection.getUserNameTextBox().setText(a2lResp.getAliasName());
  }

  /**
   * Set Others responsible details
   *
   * @param a2lResp
   */
  private void setOtherRespRadioButton(final A2lResponsibility a2lResp) {
    this.responsibilitySection.getOthersButton().setSelection(true);
    this.responsibilitySection.getUserNameTextBox().setText(this.a2lWpInfoBo.getLFullName(a2lResp));
  }

  /**
   * Load work package details
   */
  public void loadWorkPkgDetails() {
    if (this.respPalObj != null) {
      if (this.a2lWpInfoBo.getAllWpMapppedToPidcVers().containsKey(this.respPalObj.getA2lWpId())) {
        this.selectedWP = this.a2lWpInfoBo.getAllWpMapppedToPidcVers().get(this.respPalObj.getA2lWpId());
        this.workPackageCombo.getCombo().setText(this.selectedWP.getName());
      }
      else {
        this.workPackageCombo.getCombo().setText("");
      }
    }
  }

  /**
   * Set Bosch responsible details
   *
   * @param a2lResp
   */
  private void setBoschRadioButton(final A2lResponsibility a2lResp) {
    if (null != a2lResp.getUserId()) {
      this.responsibilitySection.getBoschButton().setSelection(true);
      User user = this.a2lWpInfoBo.getA2lResponsibilityModel().getUserMap().get(a2lResp.getUserId());
      // set the user
      this.responsibilitySection.setBoschUser(user);
      this.responsibilitySection.getUserNameTextBox().setText(user.getDescription());
    }
    else {
      this.responsibilitySection.getBoschButton().setSelection(true);
      this.responsibilitySection.getUserNameTextBox().setText(a2lResp.getName());
    }
  }

  /**
   * Set Customer responsible details
   *
   * @param a2lResp
   */
  private void setCustomerRadioButton(final A2lResponsibility a2lResp) {
    if (a2lResp != null) {
      this.responsibilitySection.getCustomerButton().setSelection(true);
      this.responsibilitySection.getUserNameTextBox().setText(this.a2lWpInfoBo.getLFullName(this.respPalObj));
    }
  }


  /**
   * create method call for work package definition
   *
   * @return A2lWpResponsibility
   */
  private A2lWpResponsibility createNewWpResp(final A2lWpResponsibility a2lWpRespObj) {
    A2lWpResponsibility wpResponsibility = new A2lWpResponsibility();

    A2lWpResponsibilityServiceClient client = new A2lWpResponsibilityServiceClient();
    try {
      List<A2lWpResponsibility> a2lWpRespList = new ArrayList<>();
      a2lWpRespList.add(a2lWpRespObj);
      Set<A2lWpResponsibility> createA2lWpResponsibilities =
          client.create(a2lWpRespList, this.a2lWpInfoBo.getPidcA2lBo().getPidcA2l());
      wpResponsibility = createA2lWpResponsibilities.iterator().next();
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return wpResponsibility;
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
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.saveButton = createButton(parent, IDialogConstants.OK_ID, "Save", true);
    this.saveButton.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {

    String wpName = setWpDetailsToObj();
    boolean canProceed = setRespDetailsToObj();
    if (canProceed) {
      if (this.editFlagMode) {
        this.new2lWpRespObj = this.a2lWpInfoBo.updateA2lWpRespPal(this.respPalObj);
        super.okPressed();
      }
      else {
        if (this.validationBO.isA2lWpNameDuplicate(wpName)) {
          MessageDialogUtils.getInfoMessageDialog("Warning",
              "' " + wpName + " ' already exists. Please enter a new workpackage name");
        }
        else {
          // if variant group is selected, add that to resp pal object
          if (this.a2lWpInfoBo.getSelectedA2lVarGroup() != null) {
            this.respPalObj.setVariantGrpId(this.a2lWpInfoBo.getSelectedA2lVarGroup().getId());
          }
          this.new2lWpRespObj = createNewWpResp(this.respPalObj);
          super.okPressed();
        }
      }
    }
  }

  /**
   * set Responsibility with out user name
   *
   * @param userType
   */
  private void setRespWOUserName(final String userType) {
    A2lResponsibility a2lRespObj =
        CreateEditWpRespDialog.this.a2lWpInfoBo.getA2lResponsibilityModel().getDefaultA2lRespMap().get(userType);
    if (a2lRespObj != null) {
      this.respPalObj.setA2lRespId(a2lRespObj.getId());
    }
    else if (WpRespType.OTHERS.getCode().equals(userType)) {
      A2lResponsibility defaultOther = this.a2lWpInfoBo.createDefaultResponsible(WpRespType.OTHERS);
      this.respPalObj.setA2lRespId(defaultOther.getId());
    }
  }


  /**
   * Set Responsiblity details to object
   */
  private boolean setRespDetailsToObj() {
    boolean canProceed = true;
    if (CommonUtils.isEmptyString(this.responsibilitySection.getUserNameTextBox().getText())) {
      if (this.responsibilitySection.getBoschButton().getSelection() ||
          this.responsibilitySection.getBoschDeptButton().getSelection()) {
        setRespWOUserName(WpRespType.RB.getCode());
        return canProceed;
      }
      if (this.responsibilitySection.getOthersButton().getSelection()) {
        setRespWOUserName(WpRespType.OTHERS.getCode());
        return canProceed;
      }
      if (this.responsibilitySection.getCustomerButton().getSelection()) {
        setRespWOUserName(WpRespType.CUSTOMER.getCode());
        return canProceed;
      }
    }
    if (this.responsibilitySection.getBoschButton().getSelection() &&
        !WpRespType.RB.getDispName().equals(this.responsibilitySection.getUserNameTextBox().getText())) {
      A2lResponsibility a2lRespObj = this.a2lWpInfoBo.createBoschResponsible(this.responsibilitySection.getBoschUser());
      if (a2lRespObj == null) {
        canProceed = false;
      }
      else if (a2lRespObj.isDeleted()) {
        CDMLogger.getInstance().errorDialog(CommonUIConstants.DELETED_RESP_ERROR_MSG, Activator.PLUGIN_ID);
        open();
      }
      else {
        this.respPalObj.setA2lRespId(a2lRespObj.getId());
      }
    }
    else {
      setA2lRespToRespPal(this.responsibilitySection.getA2lResp());
    }
    return canProceed;
  }

  /**
   * Set Work package details to the object
   *
   * @return String
   */
  private String setWpDetailsToObj() {
    String wpName = "";
    this.respPalObj.setWpDefnVersId(this.a2lWpInfoBo.getA2lWpDefnModel().getSelectedWpDefnVersionId());
    this.respPalObj.setName(wpName);
    return wpName;
  }


  /**
   * @return the comoboViewerInput
   */
  public Map<Long, A2lWorkPackage> getComoboViewerInput() {
    return this.comoboViewerInput;
  }


  /**
   * @param comoboViewerInput the comoboViewerInput to set
   */
  public void setComoboViewerInput(final Map<Long, A2lWorkPackage> comoboViewerInput) {
    this.comoboViewerInput = comoboViewerInput;
  }


  /**
   * @param selectedWP the selectedWP to set
   */
  public void setSelectedWP(final A2lWorkPackage selectedWP) {
    this.selectedWP = selectedWP;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.DIALOG_TRIM);
  }


  /**
   * @return the new2lWpRespObj
   */
  public A2lWpResponsibility getNew2lWpRespObj() {
    return this.new2lWpRespObj;
  }


  /**
   * @param new2lWpRespObj the new2lWpRespObj to set
   */
  public void setNew2lWpRespObj(final A2lWpResponsibility new2lWpRespObj) {
    this.new2lWpRespObj = new2lWpRespObj;
  }
}
