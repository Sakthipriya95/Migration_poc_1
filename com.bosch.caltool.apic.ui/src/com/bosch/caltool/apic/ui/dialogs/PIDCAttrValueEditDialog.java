/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import com.bosch.caltool.apic.ui.actions.PIDCActionSet;
import com.bosch.caltool.apic.ui.actions.PIDCAttrValueEditToolBarActionSet;
import com.bosch.caltool.apic.ui.actions.PIDCGroupedAttrActionSet;
import com.bosch.caltool.apic.ui.editors.PIDCCompareEditorInput;
import com.bosch.caltool.apic.ui.editors.compare.ColumnDataMapper;
import com.bosch.caltool.apic.ui.editors.compare.ComparePIDCPage;
import com.bosch.caltool.apic.ui.editors.pages.PIDCAttrPage;
import com.bosch.caltool.apic.ui.sorter.PIDCAttrValGridTabViewerSorter;
import com.bosch.caltool.apic.ui.table.filters.PIDCAttrValTabFilter;
import com.bosch.caltool.apic.ui.table.filters.PIDCAttrValueEditToolBarFilters;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.apic.ui.util.PIDCPageEditUtil;
import com.bosch.caltool.apic.ui.views.providers.PIDCAttrValLabelProvider;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectObjectBO;
import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.client.bo.apic.AttributeValueClientBO;
import com.bosch.caltool.icdm.client.bo.apic.AttributeValueWithDetails;
import com.bosch.caltool.icdm.client.bo.apic.PidcDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.client.bo.apic.ProjectAttributeUtil;
import com.bosch.caltool.icdm.client.bo.apic.pidc.PidcAttrValueEditBO;
import com.bosch.caltool.icdm.client.bo.general.NodeAccessPageDataHandler;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueDummy;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValueAndValidtyModel;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedValidity;
import com.bosch.caltool.icdm.model.apic.attr.ProjectAttributesUpdationModel;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.rcputils.IUtilityConstants;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;
import com.bosch.rcputils.text.TextBoxContentDisplay;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * @author mga1cob
 */
public class PIDCAttrValueEditDialog extends AbstractDialog {

  /**
   * String constant for invalid value info
   */
  private static final String INVALID_VALUE_INFO = "Invalid Value Info:";

  /**
   * the maximum length for the text field
   */
  private static final int MAX_TEXT_BOX_SIZE = 4000;

  /**
   * assigning width for the column
   */
  private static final int COL_WIDTH_1 = 200;

  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;

  /**
   * Composite instance for base layout
   */
  private Composite top;

  /**
   * Composite instance
   */
  private Composite composite;

  /**
   * Section instance
   */
  private Section section;

  /**
   * Form instance
   */
  private Form form;

  /**
   * Defines PIDC editable attribute
   */
  private final IModel apicObject;

  /**
   * Ok Button instance
   */
  private Button okBtn;

  /**
   * Filter text instance
   */
  private Text filterTxt;

  /**
   * PIDC attribute value GridTableViewer instance
   */
  private GridTableViewer valTableViewer;

  /**
   * Defines AbstractViewerSorter - PIDC attribute value GridTableViewer sorter
   */
  private AbstractViewerSorter valTabSorter;

  /**
   * PIDCAttrValTabFilter PIDC attribute value GridTableViewer filter instance
   */
  private PIDCAttrValTabFilter valTabFilter;

  /**
   * PIDCPage instance
   */
  private PIDCAttrPage pidcPage;

  /**
   * ColumnViewer instance
   */
  private final ColumnViewer viewer;

  /**
   * Section instance
   */
  private Section sectionOne;

  /**
   * Form instance
   */
  private Form formOne;

  /**
   * Part number Text instance
   */
  private Text partNumText;

  /**
   * spec link Text instance
   */
  private Text specLinkText;

  /**
   * Description text instance
   */
  private Text descText;
  // ICDM-2580
  /**
   * Action for adding new attribute value
   */
  private Action addNewValAction;

  /**
   * multipleVariant flag
   */
  private boolean multipleVariant;

  /**
   * multipleSubVariant flag
   */
  private boolean multipleSubVariant;

  /**
   * Selected pidc version
   */
  PidcVersion pidcVer;

  /**
   * ControlDecoration for text field version name
   */
  protected ControlDecoration txtVrsnNameDec;

  /**
   * PIDCVariantValueDialog instance
   */
  private PIDCVariantValueDialog pidcVariantValueDialog;

  /**
   * PIDCVariant instance
   */
  private PidcVariant variant;

  /**
   * PIDC Version Details Section
   */
  private PIDCVersionDetailsSection versionSection;

  private ColumnDataMapper columnDataMapper;


  private boolean isCompareEditor;

  /**
   * display String of the attr Value to see if it is actual value or Use current Value
   */
  private String dispStr;

  /**
   * List of varaints for value Change
   */
  private PidcVariant[] variants;
  /**
   * List of Sub-varaints for value Change
   */
  private PidcSubVariant[] subVariants;

  private final ComparePIDCPage comparePIDCPage;


  private boolean readOnlyMode = false;

  private PIDCAttrValueEditToolBarFilters toolBarFilters;

  /**
   * PidcDataHandler
   */
  protected PidcDataHandler pidcDataHandler;

  /**
   * PidcVersionHandler
   */
  protected final PidcVersionBO pidcVersionBO;
  /**
   * Not cleared action
   */
  private Action actionNotCleared;

  private AttributeValueWithDetails validAttrValues;

  private final PidcAttrValueEditBO valueEditBO;


  /**
   * The parameterized constructor
   *
   * @param parentShell instance
   * @param pidcPage instance
   * @param apicObject attribute instance
   * @param viewer instance
   */
  // ICDM-94
  public PIDCAttrValueEditDialog(final Shell parentShell, final PIDCAttrPage pidcPage, final IModel apicObject,
      final ColumnViewer viewer) {
    super(parentShell);
    this.pidcPage = pidcPage;
    this.pidcDataHandler = pidcPage.getPidcDataHandler();
    this.pidcVer = pidcPage.getSelectedPidcVersion();
    this.pidcVersionBO = pidcPage.getPidcVersionBO();
    this.apicObject = apicObject;
    this.viewer = viewer;
    this.comparePIDCPage = null;
    this.valueEditBO = new PidcAttrValueEditBO(this.pidcVersionBO, apicObject);
    this.validAttrValues = this.valueEditBO.getAttributeValueWithDetails(false);
  }

  /**
   * @param apicObject attribute instance
   * @param multipleVariant flag for mutiple varaint selection
   * @param pidcVariantValueDialog instance
   * @param dispStr dispStr
   * @param pidcPage PIDC Attribute Page
   */
  public PIDCAttrValueEditDialog(final IModel apicObject, final boolean multipleVariant,
      final PIDCVariantValueDialog pidcVariantValueDialog, final String dispStr, final PIDCAttrPage pidcPage) {
    super(pidcVariantValueDialog.getPidcAttrTabViewer().getControl().getShell());
    this.pidcPage = pidcPage;
    this.apicObject = apicObject;
    this.multipleVariant = multipleVariant;
    this.multipleSubVariant = !multipleVariant;
    this.pidcVariantValueDialog = pidcVariantValueDialog;
    this.viewer = this.pidcVariantValueDialog.getPidcAttrTabViewer();
    this.pidcVer = pidcVariantValueDialog.getSelPidcVer();
    this.variant = pidcVariantValueDialog.getVariant();
    this.variants = null == pidcVariantValueDialog.getVariants() ? null : pidcVariantValueDialog.getVariants().clone();
    this.subVariants =
        null == pidcVariantValueDialog.getSubVariants() ? null : pidcVariantValueDialog.getSubVariants().clone();
    this.dispStr = dispStr;
    this.comparePIDCPage = null;
    this.pidcVersionBO = this.pidcPage.getPidcVersionBO();
    this.pidcDataHandler = this.pidcPage.getPidcVersionBO().getPidcDataHandler();
    this.valueEditBO = new PidcAttrValueEditBO(this.pidcVersionBO, apicObject);
    this.validAttrValues = this.valueEditBO.getAttributeValueWithDetails(false);
  }


  /**
   * ICDM-767
   *
   * @param parentShell parent shell
   * @param projObjBO PIDCPage
   * @param apicObject2 ApicObject
   * @param viewer2 TreeViewer
   * @param selPidcVer PIDCard version
   */
  public PIDCAttrValueEditDialog(final Shell parentShell, final AbstractProjectObjectBO projObjBO,
      final IModel apicObject2, final TreeViewer viewer2, final PidcVersion selPidcVer) {
    super(parentShell);
    this.pidcDataHandler = projObjBO.getPidcDataHandler();
    this.pidcVersionBO = (PidcVersionBO) projObjBO;
    this.apicObject = apicObject2;
    this.viewer = viewer2;
    this.pidcVer = selPidcVer;
    this.comparePIDCPage = null;
    this.valueEditBO = new PidcAttrValueEditBO(this.pidcVersionBO, this.apicObject);
  }


  /**
   * @param activeShell parent shell
   * @param comparePIDCPage Value edit from compoare PIDC page
   * @param pidcAttribute attr
   * @param columnDataMapper columdata mapper
   * @param isCompareEditor iscompare editor
   * @param variant pidc variant of the selected attribute
   * @param projObjBO Project Handler
   */
  public PIDCAttrValueEditDialog(final Shell activeShell, final ComparePIDCPage comparePIDCPage,
      final IProjectAttribute pidcAttribute, final ColumnDataMapper columnDataMapper, final boolean isCompareEditor,
      final PidcVariant variant, final AbstractProjectObjectBO projObjBO) {
    super(activeShell);
    this.pidcPage = null;
    this.pidcVer = projObjBO.getPidcVersion();
    this.apicObject = pidcAttribute;
    this.viewer = null;
    this.columnDataMapper = columnDataMapper;
    this.isCompareEditor = isCompareEditor;
    this.variant = variant;
    this.comparePIDCPage = comparePIDCPage;
    this.pidcVersionBO =
        ((PIDCCompareEditorInput) comparePIDCPage.getEditorInput()).getComparePidcHandler().getPidcVersionBO();
    this.pidcDataHandler = projObjBO.getPidcDataHandler();
    this.valueEditBO = new PidcAttrValueEditBO(this.pidcVersionBO, this.apicObject);
    this.validAttrValues = this.valueEditBO.getAttributeValueWithDetails(true);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle(getTitle());

    // Set the message
    setMessage(ApicUiConstants.ATTR_NAME + this.valueEditBO.getAttribute().getName(), IMessageProvider.INFORMATION);

    return contents;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // Set shell name
    newShell.setText(getTitle());
    super.configureShell(newShell);
    // ICDM-153
    super.setHelpAvailable(true);
  }


  /**
   * This method returns title for dialog
   */
  private String getTitle() {
    String title;
    if ((this.pidcPage == null) && (this.pidcVariantValueDialog == null) && (!this.isCompareEditor)) {
      if (this.valueEditBO.getAttribute().getLevel() == ApicConstants.SUB_VARIANT_CODE_ATTR) {
        title = ApicUiConstants.ADD_A_SUB_VARIANT;
      }
      else if (this.valueEditBO.getAttribute().getLevel() == ApicConstants.VARIANT_CODE_ATTR) {
        title = ApicUiConstants.ADD_A_VARIANT;
      }
      else {
        title = ApicUiConstants.ADD_A_PIDC;
      }
    }
    else {
      title = this.readOnlyMode ? ApicUiConstants.PIDC_ATTR_VALUE_LIST : ApicUiConstants.EDIT_PIDC_ATT_VAL;
    }
    return title;
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
   * This method creates the composite
   */
  private void createComposite() {
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
    createSection();
    // Only for non-level attributes, partnumber and specLink is applicable
    if ((this.valueEditBO.getAttribute().getLevel() == 0) &&
        (this.valueEditBO.getAttribute().isWithPartNumber() || this.valueEditBO.getAttribute().isWithSpecLink())) {
      createAdditionalInfoSection();
    }
  }

  /**
   * This method creates a section
   */
  private void createSection() {
    final String sectionName = getSectionName();
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), sectionName);

    createForm();

    createToolBarAction();

    this.section.setClient(this.form);
  }

  /**
   * This method creates a section
   */
  private void createAdditionalInfoSection() {
    final String sectionName = "Additional Info:";
    this.sectionOne = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), sectionName);


    createFormOne();

    this.sectionOne.setClient(this.formOne);
  }

  /**
   * create form one
   */
  private void createFormOne() {

    this.formOne = getFormToolkit().createForm(this.sectionOne);
    this.formOne.getBody().setLayout(new GridLayout());
    AbstractProjectObjectBO handler;
    if (this.comparePIDCPage != null) {
      handler = ((PIDCCompareEditorInput) this.comparePIDCPage.getEditorInput()).getComparePidcHandler()
          .getCompareObjectsHandlerMap().get(new ProjectAttributeUtil().getID((IProjectAttribute) this.apicObject));
    }
    else {
      handler = this.pidcVersionBO;
    }
    PIDCPageEditUtil pageEditUtil = new PIDCPageEditUtil(handler);

    Group group = new Group(this.formOne.getBody(), SWT.NONE);
    group.setLayoutData(GridDataUtil.getInstance().getGridData());
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    group.setLayout(gridLayout);
    getFormToolkit().createLabel(group, "Part Number");
    createPartNumControl(pageEditUtil, group);
    createSpecLinkControl(pageEditUtil, group);
    createCommentControl(pageEditUtil, group);
  }

  /**
   * @param pageEditUtil
   * @param group
   */
  private void createCommentControl(final PIDCPageEditUtil pageEditUtil, final Group group) {
    // ICDM-908 Change the Label Description to Comment
    getFormToolkit().createLabel(group, "Comment");
    TextBoxContentDisplay boxContentDisplay = new TextBoxContentDisplay(group,
        SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, MAX_TEXT_BOX_SIZE, getTextAreaGridData());
    this.descText = boxContentDisplay.getText();

    if (!(this.multipleSubVariant || this.multipleVariant)) {
      this.descText.setText((((IProjectAttribute) this.apicObject).getAdditionalInfoDesc() == null) ? ""
          : ((IProjectAttribute) this.apicObject).getAdditionalInfoDesc());
    }
    else {
      this.descText.setEnabled(true);
      this.descText.setText(pageEditUtil.getTextForComments(this.apicObject, this.variants, this.subVariants));
    }
    this.descText.setEnabled(!isReadOnlyMode());
    this.descText.addModifyListener(event -> validateOkBtn());
  }

  /**
   * @param pageEditUtil
   * @param group
   */
  private void createSpecLinkControl(final PIDCPageEditUtil pageEditUtil, final Group group) {
    // ICDM-908 Change the Label Specification to Specification (Link)
    getFormToolkit().createLabel(group, "Specification (Link)");
    this.specLinkText = getFormToolkit().createText(group, null, SWT.SINGLE | SWT.BORDER);
    this.specLinkText.setLayoutData(getTextFieldGridData());
    if (!(this.multipleSubVariant || this.multipleVariant)) {
      this.specLinkText.setText((((IProjectAttribute) this.apicObject).getSpecLink() == null) ? ""
          : ((IProjectAttribute) this.apicObject).getSpecLink());
    }
    else {
      this.specLinkText.setEnabled(true);
      this.specLinkText.setText(pageEditUtil.getTextForSpecLink(this.apicObject, this.variants, this.subVariants));
    }
    if (!this.valueEditBO.getAttribute().isWithSpecLink()) {
      this.specLinkText.setEnabled(false);
    }
    this.specLinkText.setEnabled(!isReadOnlyMode());
    this.specLinkText.addModifyListener(event -> validateOkBtn());
  }

  /**
   * @param pageEditUtil
   * @param group
   */
  private void createPartNumControl(final PIDCPageEditUtil pageEditUtil, final Group group) {
    this.partNumText = getFormToolkit().createText(group, null, SWT.SINGLE | SWT.BORDER);
    this.partNumText.setLayoutData(getTextFieldGridData());
    if (!(this.multipleSubVariant || this.multipleVariant)) {
      this.partNumText.setText((((IProjectAttribute) this.apicObject).getPartNumber() == null) ? ""
          : ((IProjectAttribute) this.apicObject).getPartNumber());
    }
    else {

      this.partNumText.setText(pageEditUtil.getTextForPartNum(this.apicObject, this.variants, this.subVariants));
      this.partNumText.setEnabled(true);
    }
    if (!this.valueEditBO.getAttribute().isWithPartNumber()) {
      this.partNumText.setEnabled(true);
    }
    this.partNumText.setEnabled(!isReadOnlyMode());

    this.partNumText.addModifyListener(event -> validateOkBtn());
  }


  /**
   * ICDM 451 returns the layout needed for text area
   *
   * @return GridData
   */
  protected GridData getTextAreaGridData() {
    GridData gridData2 = new GridData();
    gridData2.grabExcessHorizontalSpace = true;
    gridData2.horizontalAlignment = GridData.FILL;
    gridData2.verticalAlignment = GridData.FILL;
    gridData2.verticalSpan = 2;
    gridData2.heightHint = 40;
    gridData2.grabExcessVerticalSpace = true;
    return gridData2;
  }

  /**
   * Thsi method returns section name
   *
   * @return String
   */
  // ICDM-150
  protected String getSectionName() {
    String sectionName;
    if ((this.pidcPage == null) && (null == this.pidcVariantValueDialog) && (!this.isCompareEditor)) {
      // ICDM-121
      if (this.valueEditBO.getAttribute().getLevel().intValue() == ApicConstants.SUB_VARIANT_CODE_ATTR) {
        sectionName = ApicUiConstants.SUB_VARIANT_NAMES;
      }
      else if (this.valueEditBO.getAttribute().getLevel().intValue() == ApicConstants.VARIANT_CODE_ATTR) {
        sectionName = ApicUiConstants.VARIANT_NAMES;
      }
      else {
        sectionName = ApicUiConstants.PIDC_NAMES;
      }
    }
    else {
      sectionName = ApicUiConstants.ATTR_VALS;
    }
    return sectionName;
  }

  /**
   * This method creates a form on the section
   */
  private void createForm() {
    this.valTabSorter = new PIDCAttrValGridTabViewerSorter();

    this.form = getFormToolkit().createForm(this.section);
    // Create Filter text
    createFilterTxt();

    // Create new users grid tableviewer
    createValGridTabViewer();

    // Set ContentProvider and LabelProvider to addNewUserTableViewer
    setTabViewerProviders();

    // Set input to the addNewUserTableViewer
    setTabViewerInput(null);
    if (!isReadOnlyMode()) {
      // Add selection listener to the addNewUserTableViewer
      addTableSelectionListener();

      // Adds double click selection listener to the addNewUserTableViewer
      addDoubleClickListener();


    }
    else {
      this.valTableViewer.getGrid()
          .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
    }
    // Add filters to the TableViewer
    addFilters();

    // Invokde GridColumnViewer sorter
    invokeColumnSorter();
    this.form.getBody().setLayout(new GridLayout());
  }

  /**
   * ICDM 574-This method defines the activities to be performed when double clicked on the table
   *
   * @param functionListTableViewer2
   */
  private void addDoubleClickListener() {
    this.valTableViewer.addDoubleClickListener(event -> Display.getDefault().asyncExec(() -> {
      boolean isEnable = false;

      if ((PIDCAttrValueEditDialog.this.apicObject instanceof IProjectAttribute) ||
          (PIDCAttrValueEditDialog.this.apicObject instanceof Attribute)) {
        isEnable = true;
      }
      if (isEnable) {
        if ((null != PIDCAttrValueEditDialog.this.versionSection) &&
            CommonUtils.isEmptyString(PIDCAttrValueEditDialog.this.versionSection.getVersionNameTxt().getText())) {
          MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "Version Details",
              "PIDC Version name to be provided!");
        }
        else {
          okPressed();
        }
      }
    })

    );
  }

  /**
   * This method creates Section ToolBar actions
   */
  private void createToolBarAction() {

    final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
    PIDCAttrValueEditToolBarActionSet actionSet = new PIDCAttrValueEditToolBarActionSet();
    final ToolBar toolbar = toolBarManager.createControl(this.section);
    final Separator separator = new Separator();
    actionSet.createDepnFilterAction(toolBarManager, this.toolBarFilters, this.valTableViewer);
    actionSet.createNotDepnFilterAction(toolBarManager, this.toolBarFilters, this.valTableViewer);

    toolBarManager.add(separator);
    actionSet.createDeletedFilterAction(toolBarManager, this.toolBarFilters, this.valTableViewer);
    actionSet.createNotDeletedFilterAction(toolBarManager, this.toolBarFilters, this.valTableViewer);


    toolBarManager.add(separator);
    actionSet.createClearFilterAction(toolBarManager, this.toolBarFilters, this.valTableViewer);
    this.actionNotCleared =
        actionSet.createNotClearFilterAction(toolBarManager, this.toolBarFilters, this.valTableViewer);
    actionSet.createRejectedFilterAction(toolBarManager, this.toolBarFilters, this.valTableViewer);


    toolBarManager.add(separator);
    addNewPIDCAttrValAction(toolBarManager);


    toolBarManager.update(true);

    this.section.setTextClient(toolbar);
  }


  /**
   * @param toolBarManager
   */
  private void addNewPIDCAttrValAction(final ToolBarManager toolBarManager) {
    final String actionName = getActionName();
    // Create an action to add new attribute value
    // ICDM-2580
    this.addNewValAction = new Action(actionName, SWT.NONE) {

      @Override
      public void run() {
        addNewAttrVal();
      }
    };
    // Set the image for add user action
    this.addNewValAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));

    toolBarManager.add(this.addNewValAction);

    enableAddNewValAction(this.addNewValAction);
  }

  /**
   *
   */
  private void addNewAttrVal() {
    Attribute attr = PIDCAttrValueEditDialog.this.valueEditBO.getAttribute();
    AttributeClientBO attrBO = new AttributeClientBO(attr);
    // condition to check other users without access to attribute for which values cannot be entered by all users
    if (!attrBO.canModifyValues() && !attr.isAddValByUserFlag()) {
      NodeAccessPageDataHandler nodeAccesHandler = new NodeAccessPageDataHandler(attr);
      SortedSet<NodeAccess> nodeAccessSet = nodeAccesHandler.getNodeAccess();
      // only domain expert should be contacted
      StringBuilder msg =
          new StringBuilder("Only the attribute owners can create values for this attribute.Please contact\n");
      for (NodeAccess nodeAccess : nodeAccessSet) {
        msg.append(nodeAccesHandler.getUserFullName(nodeAccess.getId())).append("\n");
      }
      CDMLogger.getInstance().infoDialog(msg.toString(), Activator.PLUGIN_ID);
    }
    else {
      // ICDM-94
      if ((PIDCAttrValueEditDialog.this.pidcPage == null) && !PIDCAttrValueEditDialog.this.multipleSubVariant &&
          !PIDCAttrValueEditDialog.this.multipleVariant && !PIDCAttrValueEditDialog.this.isCompareEditor) {
        invokeAddNewAttrValDialog();
      }
      else {
        // ICDM-108
        invokeNewPIDCAttrValDialog();
      }
    }
  }

  /**
   * This method validates to enable new value action should be enable or not
   *
   * @param action
   * @param attribute
   */
  private void enableAddNewValAction(final Action action) {
    if ((this.apicObject instanceof IProjectAttribute)) {
      action.setEnabled(this.valueEditBO.canEnableAddNewValAction(this.readOnlyMode));
    }
  }

  /**
   * This method returns action name
   *
   * @return String
   */
  private String getActionName() {
    String actionName;
    if (this.pidcPage == null) {
      actionName = ApicUiConstants.DESC_CREAT_PIDC_NEW_NAME;
    }
    else {
      actionName = ApicUiConstants.DEFINE_NEW_PIDC_ATTR_VAL;
    }
    return actionName;
  }

  /**
   * This method invokes to set the comparator to pidcAttrValGridTableViewer
   */
  private void invokeColumnSorter() {
    this.valTableViewer.setComparator(this.valTabSorter);
  }

  /**
   * This method add selection listener to valTableViewer
   */
  private void addTableSelectionListener() {
    this.valTableViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        getSelAttrValFromTabViewer();
        validateOkBtn();
      }
    });
  }

  /**
   * This method sets input to the pidc attribute GridTableViewer
   *
   * @param attrVal
   */
  private void setTabViewerInput(final AttributeValue attrVal) {
    final Set<AttributeValue> tempAttrValues = this.valueEditBO
        .getValidAttrValueSet(!isMultipleVarOrSubVarSelected() && (!this.isCompareEditor), this.validAttrValues);

    // Add the "<Use Current Value>" value if the values are different
    if (CommonUIConstants.DISP_TEXT_USE_CUR_VAL.equals(this.dispStr)) {
      IProjectAttribute pidcAttr = (IProjectAttribute) this.apicObject;

      AttributeValueDummy dummyAttr =
          new AttributeValueDummy(this.pidcDataHandler.getAttributeMap().get(pidcAttr.getAttrId()));
      dummyAttr.setValue(CommonUIConstants.DISP_TEXT_USE_CUR_VAL);
      dummyAttr.setId(0L);
      tempAttrValues.add(dummyAttr);
      this.valTableViewer.setInput(tempAttrValues);
      setSelection(this.valTableViewer, dummyAttr);
    }
    else {
      this.valTableViewer.setInput(tempAttrValues);


      // To set selection on the value which is set to pidc attribute
      if ((this.apicObject instanceof IProjectAttribute) && (attrVal == null)) {
        AttributeValue attributeValue =
            this.pidcDataHandler.getAttributeValueMap().get(((IProjectAttribute) this.apicObject).getValueId());
        if ((attributeValue != null) ||
            ApicConstants.CODE_YES.equals(((IProjectAttribute) this.apicObject).getUsedFlag())) {
          GridTableViewerUtil.getInstance().setSelection(this.valTableViewer, attributeValue);
        }
      } // To set selection on the value which is inserted newly
      else if ((this.apicObject instanceof IProjectAttribute) && (attrVal != null)) {
        setSelection(this.valTableViewer, attrVal);
        validateOkBtn();
      }
    }
  }


  /**
   * iCDM-1099 Check if multiple variant subvariants selected
   *
   * @return true if mutiple selected
   */
  private boolean isMultipleVarOrSubVarSelected() {
    return (!((this.pidcPage == null) && !this.multipleVariant && !this.multipleSubVariant));

  }


  /**
   * This method sets the providers to attribute values grid table viewer
   */
  private void setTabViewerProviders() {
    // Set content provider
    this.valTableViewer.setContentProvider(ArrayContentProvider.getInstance());
    // Set label provider
    if (this.validAttrValues != null) {
      this.valTableViewer.setLabelProvider(new PIDCAttrValLabelProvider(this.validAttrValues.getValidValMap()));
    }
    else {
      this.valTableViewer.setLabelProvider(new PIDCAttrValLabelProvider(null));
    }

  }

  /**
   * This method adds the filter instance to pidc/variant/sub-variant attribute value
   */
  private void addFilters() {
    this.valTabFilter = new PIDCAttrValTabFilter();
    // Add PIDC Attribute TableViewer filter
    this.valTableViewer.addFilter(this.valTabFilter);

    this.toolBarFilters = new PIDCAttrValueEditToolBarFilters(this.validAttrValues);
    this.valTableViewer.addFilter(this.toolBarFilters);

  }

  /**
   * This method creates the pidc/variant/sub-variant attribute value gridtableviewer
   *
   * @param gridData
   */
  private void createValGridTabViewer() {
    this.valTableViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(),
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL,
        GridDataUtil.getInstance().getHeightHintGridData(200));
    // Create GridViewerColumns
    createValGridViewerColumns();

  }

  /**
   * This method adds Columns to the gridtableviewer
   */
  private void createValGridViewerColumns() {
    // Creates PIDC attribute value column
    createPIDCAttrValColumn();
    // Creates PIDC attribute value description column
    createPIDCAttrValDescColumn();
    // create PIDC PIDC attribute value status column
    createPIDCAttrValClrStatColumn();

    // create PIDC attribute Char Val Column
    createPIDCAttrCharValColumn();
  }

  /**
   * Icdm-956 Create a new Column for Attr Char Value
   */
  private void createPIDCAttrCharValColumn() {
    final GridViewerColumn attrCharColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.valTableViewer, ApicConstants.CHARVAL, COL_WIDTH_1);
    // Add column selection listener
    attrCharColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        attrCharColumn.getColumn(), ApicUiConstants.COLUMN_INDEX_3, this.valTabSorter, this.valTableViewer));

  }


  /**
   * This method creates PIDCAttrVal Clr Stat Column
   */
  private void createPIDCAttrValClrStatColumn() {
    final GridViewerColumn clrStatusColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.valTableViewer, "Clearing status", 150);
    // Add column selection listener
    clrStatusColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(clrStatusColumn.getColumn(), 2, this.valTabSorter, this.valTableViewer));
  }


  /**
   * This method adds pidc attribute value column to the gridtableviewer
   */
  private void createPIDCAttrValColumn() {
    final String colName = getColumnName();
    final GridViewerColumn attrValColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.valTableViewer, colName, 200);
    // Add column selection listener
    attrValColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(attrValColumn.getColumn(), 0, this.valTabSorter, this.valTableViewer));
  }

  /**
   * This returns column name
   *
   * @return String
   */
  private String getColumnName() {
    String colName;
    // To create new PIDC/variant/sub-variant
    if (this.pidcPage == null) {
      colName = ApicUiConstants.NAME;
    }
    else {
      // To create PIDC attrubte value
      colName = ApicUiConstants.VALUE;
    }
    return colName;
  }

  /**
   * This method adds pidc attribute value description column to the gridtableviewer
   */
  private void createPIDCAttrValDescColumn() {
    final GridViewerColumn valDescColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.valTableViewer, ApicUiConstants.DESCRIPTION, 250);
    // Add column selection listener
    valDescColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(valDescColumn.getColumn(), 1, this.valTabSorter, this.valTableViewer));

  }

  /**
   * This method creates filter text
   */
  private void createFilterTxt() {
    this.filterTxt = TextUtil.getInstance().createFilterText(getFormToolkit(), this.form.getBody(),
        GridDataUtil.getInstance().getTextGridData(), Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.filterTxt.addModifyListener(event -> {
      final String text = PIDCAttrValueEditDialog.this.filterTxt.getText().trim();
      PIDCAttrValueEditDialog.this.valTabFilter.setFilterText(text);
      PIDCAttrValueEditDialog.this.valTableViewer.refresh();

    });
    // ICDM-183
    this.filterTxt.setFocus();
  }

  /**
   * create button for Button Bar
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.okBtn = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
    this.okBtn.setEnabled(false);
    if (null != this.versionSection) {
      this.versionSection.setOkBtn(this.okBtn);
    }
    // creating cancel button
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
  }


  /**
   * After pressing ok button
   */
  @Override
  protected void okPressed() {
    // Get the selected attribute value
    final AttributeValue attrValue = getSelAttrValFromTabViewer();
    AbstractFormPage formPage = getFormPage();

    // if not set value dialog
    if (CommonUtils.isNull(this.pidcVariantValueDialog)) {

      if ((null != attrValue) && CommonUtils.isNotEqual(attrValue.getId(), AttributeValueDummy.VALUE_ID) &&
          (null != attrValue.getId())) {
        PredefinedAttrValueAndValidtyModel model =
            this.valueEditBO.getPredefinedAttrValueAndValidtyModel(attrValue.getId());
        Map<Long, Map<Long, PredefinedValidity>> predefinedValidityMap = model.getPredefinedValidityMap();
        Map<Long, Map<Long, PredefinedAttrValue>> predefinedAttrValueMap = model.getPredefinedAttrValueMap();
        // if grouped attribute value does not have any related predefined attribute then reset old grpd attr value's
        // predefined attr
        this.valueEditBO.saveGrpAttrValueWithoutPredefAttr(predefinedAttrValueMap,
            this.pidcDataHandler.getAttributeMap().get(attrValue.getAttributeId()));

        if (this.pidcDataHandler.getAttributeMap().get(attrValue.getAttributeId()).isGroupedAttr() &&
            this.pidcVersionBO.checkGroupedAttrValueValidity(attrValue, predefinedValidityMap) &&
            CommonUtils.isNotEmpty(predefinedAttrValueMap)) {

          saveGrpAttrValueWithPredefAttr(attrValue, formPage, predefinedAttrValueMap);
        }
        else {
          saveValue(attrValue);
        }
      }
      else {
        saveValue(attrValue);
      }
    }
    else {
      saveValue(attrValue);
    }
  }


  /**
   * @param formPage
   */
  private AbstractFormPage getFormPage() {
    AbstractFormPage formPage = null;
    if (CommonUtils.isNotNull(this.pidcPage)) {
      formPage = this.pidcPage;
    }
    else if (CommonUtils.isNotNull(this.comparePIDCPage)) {
      formPage = this.comparePIDCPage;
    }
    return formPage;
  }

  /**
   * @param attrValue
   * @param formPage
   * @param predefinedAttrValueMap
   */
  private void saveGrpAttrValueWithPredefAttr(final AttributeValue attrValue, final AbstractFormPage formPage,
      final Map<Long, Map<Long, PredefinedAttrValue>> predefinedAttrValueMap) {
    boolean flag = false;
    PIDCGroupedAttrActionSet actionSet = new PIDCGroupedAttrActionSet(this.pidcDataHandler, this.pidcVersionBO);
    Map<Long, PidcVersionAttribute> allPIDCAttrMap = this.pidcDataHandler.getPidcVersAttrMap();
    Set<PredefinedAttrValue> preDefinedAttrValueSet =
        actionSet.getPredefinedAttrValSet(attrValue, predefinedAttrValueMap);

    flag = actionSet.isallPredefinedAttrInSetInvisible(allPIDCAttrMap, preDefinedAttrValueSet) ? flag
        : actionSet.checkAllPredefAttrVal(attrValue, allPIDCAttrMap,
            (IProjectAttribute) PIDCAttrValueEditDialog.this.apicObject, predefinedAttrValueMap) ||
            this.valueEditBO.diffWithCurrPredAttr(predefinedAttrValueMap,
                (IProjectAttribute) PIDCAttrValueEditDialog.this.apicObject, attrValue);
    if (flag) {
      PIDCGrpdAttrChangesDialog dialog = new PIDCGrpdAttrChangesDialog(Display.getDefault().getActiveShell(), formPage,
          attrValue, (IProjectAttribute) PIDCAttrValueEditDialog.this.apicObject, this.pidcVer, true,
          this.pidcDataHandler.getPidcVersAttrMap(),
          new HashSet<>(predefinedAttrValueMap.get(attrValue.getId()).values()), null, null);
      dialog.open();
      if (dialog.isSaveSuccess()) {
        saveValue(attrValue);
      }
    }
    else {
      saveValue(attrValue);
    }
  }

  /**
   * @param attrValue attribute value
   * @param valDep attribute and value dependency
   * @param pidcAttrMap pidc attribute map
   * @return boolean value
   */
  public boolean validateAttrValue(final AttributeValue attrValue, final Set<AttrNValueDependency> valDep,
      final Map<Long, IProjectAttribute> pidcAttrMap) {

    boolean warningMsgShown = true;

    if (!new AttributeValueClientBO(attrValue).isValidValue(pidcAttrMap, this.pidcDataHandler, valDep)) {
      MessageDialog.openWarning(Display.getCurrent().getActiveShell(), INVALID_VALUE_INFO,
          " This value cannot be set due to missing dependency !");
      warningMsgShown = false;
    }
    else if (attrValue.isDeleted()) {
      MessageDialog.openWarning(Display.getCurrent().getActiveShell(), INVALID_VALUE_INFO,
          " This value cannot be set since it is deleted !");
      warningMsgShown = false;
    }
    else if ((PIDCAttrValueEditDialog.this.apicObject instanceof IProjectAttribute) &&
        isValueAlreadySet(attrValue, PIDCAttrValueEditDialog.this.apicObject)) {
      MessageDialog.openWarning(Display.getCurrent().getActiveShell(), INVALID_VALUE_INFO,
          " This value cannot be set since it is already set !");
      warningMsgShown = false;
    }
    return warningMsgShown;
  }

  private boolean validateHyperLink(final String specLink) {
    if (!specLink.isEmpty() && !CommonUIConstants.DISP_TEXT_USE_CUR_VAL.equals(specLink) &&
        (!CommonUtils.isValidHyperlinkFormat(specLink))) {
      MessageDialog.openWarning(Display.getCurrent().getActiveShell(), INVALID_VALUE_INFO,
          "Valid hyperlink value to be provided!");
      return false;
    }
    return true;
  }

  /**
   * @param attrValue
   */
  private void saveValue(final AttributeValue attributeValue) {

    Set<AttrNValueDependency> valDep = new HashSet<>();
    AttributeValue attrValue =
        (attributeValue == null) ? new AttributeValueDummy(this.valueEditBO.getAttribute()) : attributeValue;

    Map<Long, IProjectAttribute> pidcAttrMap = new HashMap<>();
    this.valueEditBO.fillAttrMap(pidcAttrMap);

    if (validateAttrValue(attrValue, valDep, pidcAttrMap)) {
      String partNumber = "";
      String desc = "";
      if (null != this.partNumText) {
        partNumber = this.partNumText.getText();
      }
      if (null != this.descText) {
        desc = this.descText.getText();
      }
      String specLink = getSpecLink();
      if (validateHyperLink(specLink)) {
        if (CommonUtils.isNotNull(this.pidcVariantValueDialog)) {

          saveValueForSetValueDialog(attributeValue, attrValue, partNumber, desc, specLink);
        }

        else if (this.pidcPage != null) {
          saveValueForPidcEditor(attrValue, partNumber, desc, specLink);

        }
        else if (this.isCompareEditor) {
          saveValueForCompareEditor(attrValue, partNumber, desc, specLink);
        }
      }
      super.okPressed();
    }

  }

  /**
   * @param attributeValue
   * @param attrValue
   * @param partNumber
   * @param desc
   * @param specLink
   */
  private void saveValueForSetValueDialog(final AttributeValue attributeValue, final AttributeValue attrValue,
      final String partNumber, final String desc, final String specLink) {
    if (this.multipleVariant) {
      saveValueForMultipleVariants(attributeValue, attrValue, partNumber, desc, specLink);
    }
    else if (this.multipleSubVariant) {
      saveValueForMultipleSubvariants(attributeValue, attrValue, partNumber, desc, specLink);
    }
  }

  /**
   * @param attrValue
   * @param partNumber
   * @param desc
   * @param specLink
   */
  private void saveValueForPidcEditor(final AttributeValue attrValue, final String partNumber, final String desc,
      final String specLink) {
    // ICDM-87
    final PIDCPageEditUtil pageEditUtil = new PIDCPageEditUtil(this.pidcPage.getProjectObjectBO());
    // To edit variant attribute value
    if (this.pidcPage.isVaraintNodeSelected() || this.pidcPage.isSubVaraintNodeSelected()) {
      pageEditUtil.editValue(attrValue, this.apicObject, partNumber, specLink, desc);
    }
    else {
      // To edit project id card attribute value
      pageEditUtil.editPIDCAttrValue(attrValue, (PidcVersionAttribute) this.apicObject, partNumber, specLink, desc);
    }
  }

  /**
   * @param attrValue
   * @param partNumber
   * @param desc
   * @param specLink
   */
  private void saveValueForCompareEditor(final AttributeValue attrValue, final String partNumber, final String desc,
      final String specLink) {
    final PIDCPageEditUtil pageEditUtil = new PIDCPageEditUtil(getColumnDataMapper(),
        ((PIDCCompareEditorInput) PIDCAttrValueEditDialog.this.comparePIDCPage.getEditorInput()).getComparePidcHandler()
            .getCompareObjectsHandlerMap().get(new ProjectAttributeUtil().getID((IProjectAttribute) this.apicObject)));
    if (this.apicObject instanceof PidcVersionAttribute) {
      pageEditUtil.editPIDCAttrValue(attrValue, (IProjectAttribute) this.apicObject, partNumber, specLink, desc);
    }
    else if ((this.apicObject instanceof PidcVariantAttribute) ||
        (this.apicObject instanceof PidcSubVariantAttribute)) {
      pageEditUtil.editValue(attrValue, this.apicObject, partNumber, specLink, desc);
    }
  }

  /**
   * @param attributeValue
   * @param attrValue
   * @param partNumber
   * @param desc
   * @param specLink
   */
  private void saveValueForMultipleSubvariants(final AttributeValue attributeValue, final AttributeValue attrValue,
      final String partNumber, final String desc, final String specLink) {
    // predefValCheck - boolean variable to check if checkAllPredefAttrVal is executed.. if its executed, its
    // not
    // required to be executed again

    boolean isPredefinedValCheckDone = false;
    boolean predefinedValCheckResult = false;
    ProjectAttributesUpdationModel updationModel = new ProjectAttributesUpdationModel();
    boolean isResetReqd = false;
    updationModel.setPidcVersion(this.pidcVer);
    for (PidcSubVariant pidcSubVar : this.subVariants) {
      Map<Long, PidcSubVariantAttribute> subVariantAttrMap =
          this.pidcDataHandler.getSubVariantAttributeMap().get(pidcSubVar.getId());
      Map<Long, PredefinedAttrValue> prevPredefAttrValMap = this.pidcDataHandler.getPreDefAttrValMap()
          .get(subVariantAttrMap.get(attrValue.getAttributeId()).getValueId());
      Set<PredefinedAttrValue> prevPredefValSet =
          null == prevPredefAttrValMap ? new HashSet<>() : new HashSet<>(prevPredefAttrValMap.values());

      AttributeValueClientBO valBO = new AttributeValueClientBO(attributeValue);
      Set<PredefinedAttrValue> newPreDefinedAttrValueSet = valBO.getPreDefinedAttrValueSet();
      // if previously set grouped attribute has predefined attribute which is not relevant for new grouped
      // attribute value
      if (!prevPredefValSet.isEmpty()) {
        this.valueEditBO.resetDiffPredefAttrForSubVar(
            CommonUtils.getDifference(prevPredefValSet, newPreDefinedAttrValueSet), pidcSubVar, updationModel);
      }

      if (!isPredefinedValCheckDone) {
        predefinedValCheckResult = checkPredefinedValue(attrValue);
        if (!prevPredefValSet.isEmpty() &&
            !CommonUtils.getDifference(prevPredefValSet, newPreDefinedAttrValueSet).isEmpty()) {
          isResetReqd = true;
        }
        isPredefinedValCheckDone = true;
      }

      Collection<PidcSubVariantAttribute> allAttrs =
          this.pidcDataHandler.getSubVariantAttributeMap().get(pidcSubVar.getId()).values();
      setSelSubAttrInfo(attrValue, partNumber, specLink, desc, allAttrs, this.apicObject, predefinedValCheckResult);

    }
    if (isResetReqd && !predefinedValCheckResult) {
      this.valueEditBO.saveAttributes(updationModel);
    }
  }

  /**
   * @param attributeValue
   * @param attrValue
   * @param partNumber
   * @param desc
   * @param specLink
   */
  private void saveValueForMultipleVariants(final AttributeValue attributeValue, final AttributeValue attrValue,
      final String partNumber, final String desc, final String specLink) {
    // predefValCheck - boolean variable to check if checkAllPredefAttrVal is executed.. if its executed, its
    // not
    // required to be executed again

    boolean isPredefinedValCheckDone = false;
    boolean predefinedValCheckResult = false;
    ProjectAttributesUpdationModel updationModel = new ProjectAttributesUpdationModel();
    boolean isResetReqd = false;
    updationModel.setPidcVersion(this.pidcVer);
    for (PidcVariant pidcVar : this.variants) {
      Map<Long, PidcVariantAttribute> variantAttrMap =
          this.pidcDataHandler.getVariantAttributeMap().get(pidcVar.getId());
      Map<Long, PredefinedAttrValue> prevPredefAttrValMap =
          this.pidcDataHandler.getPreDefAttrValMap().get(variantAttrMap.get(attrValue.getAttributeId()).getValueId());
      Set<PredefinedAttrValue> prevPredefValSet =
          null == prevPredefAttrValMap ? new HashSet<>() : new HashSet<>(prevPredefAttrValMap.values());
      AttributeValueClientBO valBO = new AttributeValueClientBO(attributeValue);
      Set<PredefinedAttrValue> newPreDefinedAttrValueSet = valBO.getPreDefinedAttrValueSet();
      if (!prevPredefValSet.isEmpty()) {
        this.valueEditBO.resetDiffPredefAttrForVar(
            CommonUtils.getDifference(prevPredefValSet, newPreDefinedAttrValueSet), pidcVar, updationModel);
      }
      if (!isPredefinedValCheckDone) {
        predefinedValCheckResult = checkPredefinedValue(attrValue);
        if (!prevPredefValSet.isEmpty() &&
            !CommonUtils.getDifference(prevPredefValSet, newPreDefinedAttrValueSet).isEmpty()) {
          isResetReqd = true;
        }
        isPredefinedValCheckDone = true;
      }

      Collection<PidcVariantAttribute> allAttrs =
          this.pidcDataHandler.getVariantAttributeMap().get(pidcVar.getId()).values();
      setSelVarAttrInfo(attrValue, partNumber, specLink, desc, allAttrs, this.apicObject, predefinedValCheckResult);

    }
    if (isResetReqd && !predefinedValCheckResult) {
      this.valueEditBO.saveAttributes(updationModel);
    }
  }


  /**
   * @param attrValue
   * @param partNumber
   * @param specLink
   * @param desc
   * @param allAttrs
   */
  private void setSelVarAttrInfo(final AttributeValue attrValue, final String partNumber, final String specLink,
      final String desc, final Collection<PidcVariantAttribute> allAttrs, final IModel selAttr,
      final boolean predefinedValCheckResult) {

    for (PidcVariantAttribute varAttr : allAttrs) {
      if (varAttr.compareTo((IProjectAttribute) selAttr) == 0) {

        Attribute attr = this.pidcDataHandler.getAttributeMap().get(varAttr.getAttrId());
        AttributeClientBO clintBO = new AttributeClientBO(attr);
        if (clintBO.isGrouped() && predefinedValCheckResult) {
          this.pidcVariantValueDialog.getGrpAttrEditList().add(varAttr);
        }

        this.pidcVariantValueDialog.setVarValMap(varAttr, attrValue, partNumber, specLink, desc);
        this.pidcVariantValueDialog.setPartNum(partNumber);
        this.pidcVariantValueDialog.setSpecLink(specLink);
        this.pidcVariantValueDialog.setDesc(desc);
        this.pidcVariantValueDialog.getPidcAttrTabViewer().refresh();

        break;
      }

    }
  }

  /**
   * @param attrValue
   * @param partNumber
   * @param specLink
   * @param desc
   * @param allAttrs
   */
  private void setSelSubAttrInfo(final AttributeValue attrValue, final String partNumber, final String specLink,
      final String desc, final Collection<PidcSubVariantAttribute> allAttrs, final IModel selAttr,
      final boolean predefinedValCheckResult) {

    for (PidcSubVariantAttribute subvarAttr : allAttrs) {
      if (subvarAttr.compareTo((IProjectAttribute) selAttr) == 0) {

        Attribute attr = this.pidcDataHandler.getAttributeMap().get(subvarAttr.getAttrId());
        AttributeClientBO clintBO = new AttributeClientBO(attr);
        if (clintBO.isGrouped() && predefinedValCheckResult) {
          this.pidcVariantValueDialog.getGrpAttrEditList().add(subvarAttr);
        }

        this.pidcVariantValueDialog.setSubVarValMap(subvarAttr, attrValue, partNumber, specLink, desc);
        this.pidcVariantValueDialog.setPartNum(partNumber);
        this.pidcVariantValueDialog.setSpecLink(specLink);
        this.pidcVariantValueDialog.setDesc(desc);
        this.pidcVariantValueDialog.getPidcAttrTabViewer().refresh();

        break;
      }

    }
  }

  /**
   * Method to check if attribute value has valid predefined attribute values
   *
   * @param attrValue
   * @return true or false
   */
  private boolean checkPredefinedValue(final AttributeValue attrValue) {
    PIDCGroupedAttrActionSet actionSet = new PIDCGroupedAttrActionSet(this.pidcDataHandler, this.pidcVersionBO);
    return actionSet.checkAllPredefAttrVal(attrValue, this.pidcVersionBO.getAttributesAll(),
        (IProjectAttribute) PIDCAttrValueEditDialog.this.apicObject, null);
  }


  /**
   * This method returns boolean value, it will checks for whether user is selecting same attribute or not
   *
   * @param selAttrVal selected attribute value instance from the GridTableViewer in the dialog
   * @param editAttr defines editable pidc attribute
   * @return boolean defines whether ok button should be enable or not
   */
  private boolean isValueAlreadySet(final AttributeValue selAttrVal, final IModel editAttr) {

    boolean valueAlreadySet = false;
    if (!(this.multipleSubVariant || this.multipleVariant)) {
      // Get selected pidc/variant/sub-variant attribute value from tablewviewer
      final String value1 = selAttrVal.getName();
      String partNumber = "";
      String desc = "";
      if (null != this.partNumText) {
        partNumber = this.partNumText.getText();
      }
      if (null != this.descText) {
        desc = this.descText.getText();
      }
      String specLink = getSpecLink();
      String value2 = IUtilityConstants.EMPTY_STRING;
      final String attrValue = ((IProjectAttribute) editAttr).getValue();
      if (attrValue != null) {
        value2 = attrValue;
      }
      final String partNumber2 = ((IProjectAttribute) editAttr).getPartNumber();
      final String specLink2 = ((IProjectAttribute) editAttr).getSpecLink();
      final String desc2 = CommonUtils.checkNull(((IProjectAttribute) editAttr).getAdditionalInfoDesc());

      if (value1.equalsIgnoreCase(value2) && partNumber.equalsIgnoreCase(partNumber2) &&
          specLink.equalsIgnoreCase(specLink2) && desc2.equalsIgnoreCase(desc)) {
        valueAlreadySet = true;
      }
    }
    return valueAlreadySet;
  }

  /**
   * @param specLink
   * @return
   */
  private String getSpecLink() {
    String specLink = "";
    if (null != this.specLinkText) {
      specLink = this.specLinkText.getText().trim();
      if (CommonUtils.isValidURLFormat(specLink)) {
        specLink = CommonUtils.formatUrl(this.specLinkText.getText().trim());
      }
    }
    return specLink;
  }


  private Set<String> getValueStringsForValidation() {
    Set<String> valueStringSet = new HashSet<>();
    if (this.valTableViewer.getInput() != null) {
      Set<AttributeValue> attrValueStringSet = (Set<AttributeValue>) this.valTableViewer.getInput();
      for (AttributeValue attributeValue : attrValueStringSet) {
        if (attributeValue.getBoolvalue() != null) {
          valueStringSet.add(attributeValue.getBoolvalue());
        }
        valueStringSet.add(attributeValue.getNameRaw());
      }
    }
    return valueStringSet;
  }


  private AttributeValue callAddUserDialog() {
    AddUserAsValueDialog addUserDialog = new AddUserAsValueDialog(
        Display.getCurrent().getActiveShell(), false, this.valueEditBO
            .getValidAttrValueSet(!isMultipleVarOrSubVarSelected() && (!this.isCompareEditor), this.validAttrValues),
        this.valueEditBO.getAttribute());
    addUserDialog.open();
    return addUserDialog.getCreatedValue();
  }

  /**
   * To method invokes the dialog to add new pidc attribute value
   */
  private void invokeNewPIDCAttrValDialog() {
    AddValueDialog addValueDialog = null;
    AttributeValue selectedValue = null;
    if (this.valueEditBO.getAttribute().getValueType().equals(AttributeValueType.ICDM_USER.toString())) {
      selectedValue = callAddUserDialog();
      if (!(this.valueEditBO.getAttribute().isWithPartNumber() || this.valueEditBO.getAttribute().isWithSpecLink())) {
        createProjectAttr(selectedValue);
      }
    }
    else {
      addValueDialog = openAddValueDialog();
    }
    int returnValue = 1;
    if (null != selectedValue) {
      returnValue = 0;
    }
    if (null != addValueDialog) {
      returnValue = addValueDialog.open();
    }
    if (returnValue == 0) {

      this.actionNotCleared.setChecked(true);
      this.toolBarFilters.setNotClearSel(true);
      AttributeValue attrVal;
      if (addValueDialog != null) {
        attrVal = addValueDialog.getNewAttrValue();
      }
      else {
        attrVal = selectedValue;
      }
      if (attrVal != null) {
        this.validAttrValues.getAttrValset().add(attrVal);
      }
      setNewAttrValue(attrVal);
    }
  }

  /**
   * @param attrVal
   */
  private void setNewAttrValue(final AttributeValue attrVal) {
    if (!this.valueEditBO.getAttribute().isWithPartNumber() && !this.valueEditBO.getAttribute().isWithSpecLink()) {
      this.valTableViewer.setInput(null);
      this.valTableViewer.getGrid().removeAll();
      this.valTableViewer.refresh();
      this.valTableViewer.setSelection(null);
      setTabViewerInput(attrVal);
      if (null == this.pidcPage) {
        okPressed();
      }
      PIDCAttrValueEditDialog.this.close();
    }
    else if (this.valueEditBO.getAttribute().isWithPartNumber() || this.valueEditBO.getAttribute().isWithSpecLink()) {
      this.valTableViewer.setInput(null);
      this.valTableViewer.getGrid().removeAll();
      this.valTableViewer.refresh();
      this.valTableViewer.setSelection(null);
      setTabViewerInput(attrVal);
    }
    else {
      if (this.multipleSubVariant || this.multipleVariant) {
        this.valTableViewer.setInput(null);
        this.valTableViewer.getGrid().removeAll();
        this.valTableViewer.refresh();
        this.valTableViewer.setSelection(null);
        if (attrVal != null) {
          setTabViewerInput(attrVal);
          setSelection(this.valTableViewer, attrVal);
          validateOkBtn();
        }
        okPressed();
      }
      PIDCAttrValueEditDialog.this.close();
    }
  }

  /**
   * @return
   */
  private AddValueDialog openAddValueDialog() {
    AddValueDialog addValueDialog;
    if (this.multipleSubVariant) {
      addValueDialog =
          new AddValueDialog(Display.getCurrent().getActiveShell(), PIDCAttrValueEditDialog.this.apicObject,

              PIDCAttrValueEditDialog.this.pidcVariantValueDialog.getPidcAttrTabViewer(), false, true,
              PIDCAttrValueEditDialog.this, false, getValueStringsForValidation());
    }
    else if (this.multipleVariant) {
      addValueDialog =
          new AddValueDialog(Display.getCurrent().getActiveShell(), PIDCAttrValueEditDialog.this.apicObject,

              PIDCAttrValueEditDialog.this.pidcVariantValueDialog.getPidcAttrTabViewer(), true, false,
              PIDCAttrValueEditDialog.this, false, getValueStringsForValidation());
    }
    else if (this.isCompareEditor) {
      addValueDialog =
          new AddValueDialog(Display.getCurrent().getActiveShell(), PIDCAttrValueEditDialog.this.apicObject,

              null, false, false, PIDCAttrValueEditDialog.this, false, getValueStringsForValidation());
    }
    else {
      // ICDM-2580
      addValueDialog =
          new AddValueDialog(Display.getCurrent().getActiveShell(), PIDCAttrValueEditDialog.this.apicObject, this.// ICDM-150
                                                                                                                  // Paste
                                                                                                                  // action
                                                                                                                  // should
                                                                                                                  // be
                                                                                                                  // false
              valTableViewer, PIDCAttrValueEditDialog.this.pidcPage, false, this, getValueStringsForValidation());
    }
    return addValueDialog;
  }

  /**
   * @param selectedValue
   */
  private void createProjectAttr(final AttributeValue selectedValue) {
    if (!this.isCompareEditor) {
      PIDCPageEditUtil pageEditUtil;
      if (this.pidcPage == null) {
        pageEditUtil = new PIDCPageEditUtil(getPidcVariantValueDialog(), this.pidcVersionBO);
      }
      else {
        pageEditUtil = new PIDCPageEditUtil(this.pidcPage.getProjectObjectBO());
      }
      // Add a new value to selected variant attribute
      if (((this.pidcPage != null) && this.pidcPage.isVaraintNodeSelected()) || this.multipleVariant) {
        pageEditUtil.editValue(selectedValue, this.apicObject, "", "", "");
      } // ICDM-122
      else if (((this.pidcPage != null) && this.pidcPage.isSubVaraintNodeSelected()) || this.multipleSubVariant) {
        pageEditUtil.editValue(selectedValue, this.apicObject, "", "", "");
      }
      else {
        // Add a new value to selected pidc attribute
        pageEditUtil.editPIDCAttrValue(selectedValue, (IProjectAttribute) this.apicObject, "", "", "");
      }
      this.valTableViewer.refresh();
    }
  }

  /**
   * To method invokes the dialog to add new pidc/variant/sub-variant
   */
  private void invokeAddNewAttrValDialog() {
    AddValueDialog addValueDialog;
    if ((this.apicObject instanceof PidcVersion) || (this.apicObject instanceof PidcVariant) ||
        (this.apicObject instanceof PidcSubVariant)) {
      addValueDialog = new AddValueDialog(Display.getCurrent().getActiveShell(), this.valueEditBO.getAttribute(),
          PIDCAttrValueEditDialog.this.viewer, null, true /* Paste action */, null, getValueStringsForValidation());

    }

    else {
      // ICDM-2580
      addValueDialog = new AddValueDialog(Display.getCurrent().getActiveShell(), this.valueEditBO.getAttribute(),
          PIDCAttrValueEditDialog.this.viewer, null, false /* Paste action */, this, getValueStringsForValidation());


    }

    final int returnValue = addValueDialog.open();
    if (returnValue == 0) {
      PIDCAttrValueEditDialog.this.close();
    }
  }

  /**
   * This method incase while editing pidc/variant/sub-variant attribute value it returns selected attribute value from
   * the pidc attribute values otherwise it returns selected PIDC name
   *
   * @return AttributeValue
   */
  protected AttributeValue getSelAttrValFromTabViewer() {
    AttributeValue attributeValue = null;
    final IStructuredSelection selection = (IStructuredSelection) this.valTableViewer.getSelection();
    if ((selection != null) && (selection.size() != 0)) {
      final Object element = selection.getFirstElement();
      if (element instanceof AttributeValue) {
        attributeValue = (AttributeValue) element;
      }
    }
    return attributeValue;
  }

  /**
   * This method is responsible to enable/disable Ok button
   *
   * @param attributeValue
   */
  private void validateOkBtn() {
    if (PIDCAttrValueEditDialog.this.apicObject instanceof IProjectAttribute) {
      PIDCAttrValueEditDialog.this.okBtn.setEnabled(true);
    }
    else {
      boolean isEnable = true;
      if (null != this.versionSection) {
        isEnable = this.versionSection.validateFields();
      }
      PIDCAttrValueEditDialog.this.okBtn.setEnabled(isEnable);
    }
  }

  /**
   * gets text field for the grid data
   *
   * @return gridData
   */
  private GridData getTextFieldGridData() {
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.verticalAlignment = GridData.CENTER;
    gridData.grabExcessVerticalSpace = true;
    return gridData;
  }

  /**
   * This method provides the selection to, the item should be selected in the GridTableViewer
   *
   * @param tableViewer instance
   * @param elementToSelect instance
   */
  public void setSelection(final GridTableViewer tableViewer, final AttributeValue elementToSelect) {
    final GridItem[] gridItems = tableViewer.getGrid().getItems();
    int index = 0;

    for (GridItem gridItem : gridItems) {
      final Object data = gridItem.getData();
      if (data instanceof AttributeValue) {
        AttributeValue attrVal = (AttributeValue) data;
        if (attrVal instanceof AttributeValueDummy) {
          attrVal.setId(0L);
        }
        if (attrVal.getId().longValue() == elementToSelect.getId().longValue()) {
          // Get the index of grid item
          index = tableViewer.getGrid().getIndexOfItem(gridItem);
          break;
        }
      }
    }
    tableViewer.getGrid().setSelection(index);
    tableViewer.getGrid().showSelection();

  }

  /**
   * Checks if the variant is used in set of all variants
   *
   * @param attributeValue AttributeValue
   * @param variantsSet SortedSet of SortedSet
   * @return true, if variant is used
   */
  // iCDM-1155
  protected boolean checkVariantIsUsed(final AttributeValue attributeValue, final SortedSet<PidcVariant> variantsSet) {
    boolean usedVariant = false;
    // report error, if variant is already used.
    // un-delete if variant is used and marked as deleted
    for (PidcVariant pidcVariant : variantsSet) {
      if (pidcVariant.getName().equals(attributeValue.getName())) {
        usedVariant = true;
        if (pidcVariant.isDeleted()) {
          final PIDCActionSet actionset = new PIDCActionSet();
          actionset.deleteVarAction(pidcVariant, ApicUiConstants.UN_DELETE_ACTION, this.pidcVer);
        }
        else {
          CDMLogger.getInstance().errorDialog(
              "Selected variant is already used in this Project ID card. Kindly select un-used variants",
              Activator.PLUGIN_ID);
        }
        break;
      }
    }
    return usedVariant;
  }


  /**
   * Checks if the sub-variant is used in set of all sub-variants
   *
   * @param attributeValue AttributeValue
   * @param subVariantsSet subVariants Set
   * @return true if value is used as sub variant's name
   */
  // iCDM-1155
  protected boolean checkSubVariantIsUsed(final AttributeValue attributeValue,
      final SortedSet<PidcSubVariant> subVariantsSet) {
    boolean usedSubVariant = false;
    // report error, if sub-variant is already used.
    // un-delete if sub-variant is used and marked as deleted
    for (PidcSubVariant pidcSubVariant : subVariantsSet) {
      if (pidcSubVariant.getName().equals(attributeValue.getName())) {
        usedSubVariant = true;
        if (pidcSubVariant.isDeleted()) {
          final PIDCActionSet actionset = new PIDCActionSet();
          actionset.deleteSubVarAction(pidcSubVariant, ApicUiConstants.UN_DELETE_ACTION);
        }
        else {
          CDMLogger.getInstance().errorDialog(
              "Selected sub-variant is already used in this Variant. Kindly select un-used sub-variants",
              Activator.PLUGIN_ID);
        }
        break;
      }
    }
    return usedSubVariant;
  }


  /**
   * @return the addNewValAction
   */
  public Action getAddNewValAction() {
    return this.addNewValAction;
  }

  /**
   * @return the filterTxt
   */
  public Text getFilterTxt() {
    return this.filterTxt;
  }


  /**
   * @return the readOnlyMode
   */
  public boolean isReadOnlyMode() {
    return this.readOnlyMode;
  }


  /**
   * @param readOnlyMode the readOnlyMode to set
   */
  public void setReadOnlyMode(final boolean readOnlyMode) {
    this.readOnlyMode = readOnlyMode;
  }


  /**
   * @return the pidcPage
   */
  public PIDCAttrPage getPidcPage() {
    return this.pidcPage;
  }


  /**
   * @return the pidcVersionBO
   */
  public PidcVersionBO getPidcVersionBO() {
    return this.pidcVersionBO;
  }


  /**
   * @return the columnDataMapper
   */
  public ColumnDataMapper getColumnDataMapper() {
    return this.columnDataMapper;
  }

  /**
   * @return the variant
   */
  public PidcVariant getVariant() {
    return this.variant;
  }

  /**
   * @return the pidcVariantValueDialog
   */
  public PIDCVariantValueDialog getPidcVariantValueDialog() {
    return this.pidcVariantValueDialog;
  }


  /**
   * @return the valueEditBO
   */
  public PidcAttrValueEditBO getValueEditBO() {
    return this.valueEditBO;
  }


}
