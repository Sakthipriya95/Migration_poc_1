/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;


import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.editors.pages.AttributesPage;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.apic.ui.views.AttributesOutlinePage;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.listeners.LinkTableSelectionListener;
import com.bosch.caltool.icdm.common.ui.sorter.LinkTableSorter;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.views.providers.LinkTableLabelProvider;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroupModel;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.model.apic.attr.Characteristic;
import com.bosch.caltool.icdm.ws.rest.client.apic.CharacteristicServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.IUtilityConstants;
import com.bosch.rcputils.decorators.Decorators;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;
import com.bosch.rcputils.text.TextBoxContentDisplay;
import com.bosch.rcputils.ui.forms.SectionUtil;
import com.bosch.rcputils.ui.validators.Validator;

/**
 * This class provides a dialog to add a new attribute
 */
/**
 * @author dja7cob
 */
public class AttributeDialog extends AbstractDialog {

  /**
   * constant for link tab viewer height
   */
  private static final int HEIGHT_HINT_FOR_LINK_TABLE = 50;
  /**
   * constant for showing number of rows
   */
  private static final int MIN_ROW_COUNT = 5;
  /**
   * Defines number of horizontal span
   */
  private static final int H_SPAN = 2;
  /**
   * bottom section column count. Two cols if link section is to be added.
   */
  private static final int BOTTOM_SEC_COL = 2;
  /**
   * AttributesPage instance
   */
  private final AttributesPage attributesPage;

  /**
   * Save button
   */
  protected Button saveBtn;

  /**
   * Cancel button
   */
  protected Button cancelBtn;

  /**
   * Top composite
   */
  private Composite top;
  /**
   * Form toolkit
   */
  private FormToolkit formToolkit; // @jve:decl-index=0:visual-constraint=""
  private Composite composite;
  /**
   * 491081 - In Attribute editor while creating new attribute , group info section is not visible
   */
  private ScrolledComposite scrollComp;
  private SashForm mainComposite;
  /**
   * Top composite
   */
  private Composite topComposite;
  private Composite composite2;
  /**
   * Left section
   */
  private Section leftSection;
  /**
   * Right section
   */
  private Section rightSection;
  /**
   * Left form
   */
  private Form leftForm;
  /**
   * Right form
   */
  private Form rightForm;
  private Section section;
  private Form form;
  /**
   * English name text box
   */
  protected Text nameEngText;
  /**
   * German name text box
   */
  protected Text nameGermText;
  /**
   * English description text box
   */
  protected Text descEngText;
  /**
   * German description text box
   */
  protected Text descGermText;
  /**
   * Value type combo
   */
  protected Combo comboValType;
  /**
   * Label - date format
   */
  private Label dateFormatLabel;
  /**
   * Date format combobox
   */
  protected Combo comboDateFormat;
  /**
   * Label - number format
   */
  private Label numberFrmtLabel;
  /**
   * Number format text box
   */
  protected Text numberFrmtText;
  /**
   * Label - unit
   */
  private Label unitLabel;
  /**
   * Unit - text box
   */
  protected Text unitText;
  /**
   * Normalized flag - combo box
   */
  protected Combo comboNormFlag;
  /**
   * Mandatory - combo box
   */
  protected Combo comboMandatory;
  /**
   * Attribute Super group - combo box
   */
  protected Combo comboSuperGrp;
  /**
   * Attribute Group - combo
   */
  protected Combo comboGrp;
  /**
   * Sorted set of attribute super groups
   */
  private SortedSet<AttrSuperGroup> superGrp = new TreeSet<>();
  /**
   * Selected attribute group
   */
  protected AttrGroup selectedAttrGroup;
  /**
   * Sorted set of attribute groups
   */
  protected SortedSet<AttrGroup> groups = new TreeSet<>();
  /**
   * Decorators instance
   */
  private final Decorators decorators = new Decorators();
  /**
   * ControlDecoration instance for numeric
   */
  protected ControlDecoration txtNumFormatDec;
  // ICDM-112
  /**
   * Control decoration - name english
   */
  protected ControlDecoration txtNameEngDec;
  /**
   * Control decoration - value type
   */
  protected ControlDecoration comboValTypeDec;
  /**
   * Control decoration - normalized flag
   */
  protected ControlDecoration comboNormFlagDec;
  /**
   * Control decoration - mandatory flag
   */
  protected ControlDecoration comboMandatoryDec;
  /**
   * Control decoration - description english
   */
  protected ControlDecoration txtDescEngDec;
  /**
   * Control decoration - unit
   */
  protected ControlDecoration txtUnitDec;
  /**
   * Control decoration - date format
   */
  protected ControlDecoration dateformatDec;
  /**
   * Control decoration - super group
   */
  protected ControlDecoration comboSuperGrpDec;
  /**
   * Control decoration - attribute group
   */
  protected ControlDecoration comboGrpDec;
  /**
   * Check box - part number flag
   */
  protected Button chkBoxPartNumFlag;
  /**
   * Check box - specification link required flag
   */
  protected Button chkBoxSpecLinkFlag;
  /**
   * Bottom composite
   */
  private Composite bottomComposite;
  /**
   * Bottom section
   */
  private Section btmSection;
  /**
   * Bottom form
   */
  private Form btmform;
  /**
   * Left composite
   */
  private Composite leftComposite;
  /**
   * Section for links
   */
  private Section sectionLink;
  /**
   * Form instance for links
   */
  private Form formLink;
  /**
   * Sorter instance
   */
  private LinkTableSorter linksTabSorter;

  /**
   * GridTableViewer for links
   */
  protected GridTableViewer linksTabViewer;
  /**
   * Defines new link action in toolbar
   */
  protected Action newLinkAction;

  /**
   * Defines edit link action in toolbar
   */
  protected Action editLinkAction;

  /**
   * Defines delete link action in toolbar
   */
  protected Action deleteLinkAction;

  /**
   * boolean that tells whether it is edit dialog or add dialog
   */
  private final boolean isEdit;
  /**
   * Combo to add Value Flag
   */
  protected Combo comboAddValFlag;

  /**
   * Icdm-480 Combo for Attr Security
   */
  protected Combo comboAttrSecure;


  /**
   * Icdm-480 Combo for Attr Security
   */
  protected Combo comboAttrValSecurity;
  /**
   * ICDM-2590 Combo for Attr Security
   */
  protected Combo comboGrpdAttr;
  /**
   * Combo For Characteristics
   */
  protected Combo comboChar;
  /**
   * Attribute characteristics
   */
  protected SortedSet<Characteristic> characteristics;

  /**
   * Text for adding comment when a value is edited
   */
  protected Text comment;

  /**
   * ok or cancel pressed indicator
   */
  protected boolean okCancelPress;

  /**
   * boolean to indicate link section is changed
   */
  protected boolean linksChanged;
  // ICDM-1560
  /**
   * Text for adding eadm name
   */
  protected Text eadmNameText;
  /**
   * Control decoration - EADM name
   */
  protected ControlDecoration txtEadmNameDec;
  // iCDM-2035
  /**
   * the field to make the control of Content display of text box and the message label by the user
   */
  protected TextBoxContentDisplay textBoxContentDisplay;
  /**
   * boolean flag to indicate if predefined attrs are added to the grouped attr
   */
  protected boolean preDefndValuesPresent;
  /**
   * the maximum length for the comment
   */
  private static final int MAX_LENGTH = 4000;

  /**
   * @param parentShell instance
   * @param attributesPage instance
   * @param isEdit edit allowed
   */
  public AttributeDialog(final Shell parentShell, final AttributesPage attributesPage, final boolean isEdit) {
    super(parentShell);
    this.attributesPage = attributesPage;
    this.isEdit = isEdit;
  }

  /**
   * @return the saveBtn
   */
  public Button getSaveBtn() {
    return this.saveBtn;
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
    setTitle("");
    // Set the message
    setMessage("");

    return contents;
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
    this.mainComposite = new SashForm(this.top, SWT.HORIZONTAL);
    this.mainComposite.setLayout(new GridLayout());
    this.mainComposite.setLayoutData(GridDataUtil.getInstance().getGridData());
    createComposite();
    parent.layout(true, true);
    return this.top;
  }

  // Icdm-326
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    // Not required
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
   * This method initializes composite
   */
  private void createComposite() {
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    gridLayout.makeColumnsEqualWidth = true;

    // 491081 - In Attribute editor while creating new attribute , group info section is not visible
    this.scrollComp = new ScrolledComposite(this.mainComposite, SWT.H_SCROLL | SWT.V_SCROLL);
    this.scrollComp.setLayout(new GridLayout());
    this.composite = getFormToolkit().createComposite(this.scrollComp, SWT.NONE);
    this.scrollComp.setContent(this.composite);
    this.scrollComp.setExpandHorizontal(true);
    this.scrollComp.setExpandVertical(true);
    this.scrollComp.setDragDetect(true);
    this.scrollComp.addControlListener(new ControlAdapter() {

      @Override
      public void controlResized(final ControlEvent event) {
        // ICDM-895
        Rectangle rect = AttributeDialog.this.scrollComp.getClientArea();
        AttributeDialog.this.scrollComp.setMinSize(AttributeDialog.this.composite.computeSize(rect.width, SWT.DEFAULT));
      }
    });
    createTopComposite();
    createLeftComposite();
    this.composite.setLayout(gridLayout);
    createRightComposite();
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
    createBottomComposite();
  }

  private void createTopComposite() {
    this.topComposite = getFormToolkit().createComposite(this.composite);
    this.topComposite.setLayout(new GridLayout());
    createSection();
    GridData createGridData = GridDataUtil.getInstance().createGridData(GridData.FILL, true, false, 2, GridData.FILL);
    createGridData.grabExcessVerticalSpace = false;
    createGridData.grabExcessHorizontalSpace = true;
    this.topComposite.setLayoutData(createGridData);
  }

  private void createBottomComposite() {
    this.bottomComposite = getFormToolkit().createComposite(this.composite);
    GridLayout gridLayout = new GridLayout();
    // ICDM-452
    if (this.isEdit) {
      gridLayout.numColumns = BOTTOM_SEC_COL;
    }
    this.bottomComposite.setLayout(gridLayout);
    createBottomSection();
    // ICDM-452
    if (this.isEdit) {
      createLinkSection();
      GridData gridData = GridDataUtil.getInstance().getGridData();
      gridData.grabExcessVerticalSpace = false;
      this.bottomComposite.setLayoutData(gridData);
    }
    else {
      this.bottomComposite
          .setLayoutData(GridDataUtil.getInstance().createGridData(GridData.FILL, true, false, H_SPAN, GridData.FILL));
    }
  }

  /**
   * create link section
   */
  private void createLinkSection() {
    // Create section for link
    this.sectionLink = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "Link Details");
    createLinkForm();
    this.sectionLink.setClient(this.formLink);

  }

  /**
   * // ICDM-452 create link form
   */
  private void createLinkForm() {
    this.linksTabSorter = new LinkTableSorter();

    this.formLink = getFormToolkit().createForm(this.sectionLink);
    this.formLink.getBody().setLayout(new GridLayout());

    GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.heightHint = HEIGHT_HINT_FOR_LINK_TABLE; // ICDM-1781
    this.linksTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.formLink.getBody(),
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL, gridData);

    this.linksTabViewer.setItemCount(MIN_ROW_COUNT); // ICDM-1781
    createToolBarAction();
    createTabColumns();

    this.linksTabViewer.setContentProvider(new IStructuredContentProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void dispose() {
        // Not applicable
      }

      /**
       * Enable/disable save button when input changes.
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        // Selection listener for ok/Cancel
        if (!AttributeDialog.this.okCancelPress) {
          AttributeDialog.this.linksChanged = true;
          checkSaveBtnEnable();
        }
      }

      /**
       * Sorted elements
       * <p>
       * {@inheritDoc}
       */
      @Override
      public Object[] getElements(final Object inputElement) {
        if (inputElement instanceof SortedSet<?>) {
          return ((SortedSet<?>) inputElement).toArray();
        }
        return null;
      }

    });

    this.linksTabViewer.setLabelProvider(new LinkTableLabelProvider());

    // Invoke TableViewer Column sorters
    invokeColumnSorter(this.linksTabSorter);

  }


  /**
   * Add sorter for the table columns
   */
  private void invokeColumnSorter(final AbstractViewerSorter sorter) {
    this.linksTabViewer.setComparator(sorter);
  }

  /**
   * ICDM-452 creates the columns of access rights table viewer
   */
  private void createTabColumns() {
    createLinkColumn();

    createDescEngColumn();

    createDescGerColumn();

    this.linksTabViewer.getGrid().addSelectionListener(
        new LinkTableSelectionListener(this.linksTabViewer, this.editLinkAction, this.deleteLinkAction));
  }

  /**
   * ICDM-452 create link column
   */
  private void createLinkColumn() {
    final GridViewerColumn linkColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.linksTabViewer, "Link", 100);

    linkColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(linkColumn.getColumn(), 0, this.linksTabSorter, this.linksTabViewer));
  }

  /**
   * ICDM-452 creates desc Eng column
   */
  private void createDescEngColumn() {
    final GridViewerColumn descEngColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.linksTabViewer, "Description(Eng)", 120);

    descEngColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(descEngColumn.getColumn(), 1, this.linksTabSorter, this.linksTabViewer));
  }

  /**
   * ICDM-452 creates desc Ger column
   */
  private void createDescGerColumn() {
    final GridViewerColumn descGerColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.linksTabViewer, "Description(Ger)", 74);

    descGerColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(descGerColumn.getColumn(), 2, this.linksTabSorter, this.linksTabViewer));
  }

  /**
   * ICDM-452 This method creates Section ToolBar actions
   */
  private void createToolBarAction() {

    final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);

    final ToolBar toolbar = toolBarManager.createControl(this.sectionLink);

    addNewLinkAction(toolBarManager);

    addEditLinkAction(toolBarManager);

    addDeleteLinkActionToSection(toolBarManager);


    toolBarManager.update(true);

    this.sectionLink.setTextClient(toolbar);
  }

  /**
   * ICDM-452 creates add new link icon in the toolbar and handles the action
   *
   * @param toolBarManager ToolBarManager
   */
  protected void addNewLinkAction(final ToolBarManager toolBarManager) {
    CommonActionSet cmnActionSet = new CommonActionSet();

    // Create an action to add new link
    this.newLinkAction = cmnActionSet.addNewLinkAction(this.linksTabViewer);
    toolBarManager.add(this.newLinkAction);
  }

  /**
   * ICDM-452 creates delete link icon in the toolbar and handles the action
   *
   * @param toolBarManager ToolBarManager
   */
  protected void addDeleteLinkActionToSection(final ToolBarManager toolBarManager) {
    CommonActionSet cmnActionSet = new CommonActionSet();
    // Create an action to delete the link
    this.deleteLinkAction =
        cmnActionSet.addDeleteLinkActionToSection(toolBarManager, this.linksTabViewer, this.editLinkAction);
  }

  /**
   * ICDM-452 creates edit link action in the toolbar and handles the action
   *
   * @param toolBarManager ToolBarManager
   */
  protected void addEditLinkAction(final ToolBarManager toolBarManager) {

    CommonActionSet cmnActionSet = new CommonActionSet(); // Create an action to add new link this.editLinkAction =
    this.editLinkAction = cmnActionSet.addEditLinkAction(toolBarManager, this.linksTabViewer);
  }


  /**
   * This method initializes composite1
   */
  private void createLeftComposite() {
    this.leftComposite = getFormToolkit().createComposite(this.composite);
    this.leftComposite.setLayout(new GridLayout());
    createLeftSection();
    GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.grabExcessVerticalSpace = false;
    this.leftComposite.setLayoutData(gridData);
  }

  /**
   * This method initializes section
   */
  private void createSection() {
    this.section =
        getFormToolkit().createSection(this.topComposite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.section.setLayoutData(new GridData());
    this.section.setExpanded(true);
    this.section.setText("Group Info:");
    createForm();
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.setClient(this.form);
  }

  /**
   * This method initializes section
   */
  private void createBottomSection() {
    this.btmSection =
        getFormToolkit().createSection(this.bottomComposite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.btmSection.setExpanded(true);
    this.btmSection.setText("Additional Info:");
    createBottomForm();
    this.btmSection.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.btmSection.setClient(this.btmform);
  }

  /**
   * This method initializes section
   */


  private void createForm() {
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 5;
    this.form = getFormToolkit().createForm(this.section);
    this.form.getBody().setLayout(gridLayout);

    getFormToolkit().createLabel(this.form.getBody(), "Super Group");
    createComboSuperGroup();
    getFormToolkit().createLabel(this.form.getBody(), "Group");
    createComboGroup();
  }

  private void createBottomForm() {
    GridLayout gridLayout = new GridLayout();
    this.btmform = getFormToolkit().createForm(this.btmSection);
    this.btmform.getBody().setLayout(gridLayout);
    GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.grabExcessVerticalSpace = false;
    Group btmGroup = new Group(this.btmform.getBody(), SWT.NONE);
    btmGroup.setLayoutData(gridData);
    btmGroup.setLayout(gridLayout);

    this.chkBoxPartNumFlag = new Button(btmGroup, SWT.CHECK);
    this.chkBoxPartNumFlag.setText(" Allow Part Number Information ");
    this.chkBoxPartNumFlag.setLayoutData(gridData);
    this.chkBoxPartNumFlag.addSelectionListener(new SelectionAdapter() {

      /**
       * Enable/disable save button
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent evnt) {
        checkSaveBtnEnable();
      }
    });
    this.chkBoxSpecLinkFlag = new Button(btmGroup, SWT.CHECK);
    this.chkBoxSpecLinkFlag.setText(" Allow Specification Link Information ");


    this.chkBoxSpecLinkFlag.setLayoutData(gridData);
    this.chkBoxSpecLinkFlag.addSelectionListener(new SelectionAdapter() {

      /**
       * Enable/disable save button
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent evnt) {
        checkSaveBtnEnable();
      }
    });
    // ICDM-908 set the Spec link flag true by default
    if (this instanceof AddAttributeDialog) {
      this.chkBoxSpecLinkFlag.setSelection(true);
    }
    // ICDM-1397
    Composite commentComp = getFormToolkit().createComposite(this.btmform.getBody());
    GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    commentComp.setLayout(layout);
    commentComp.setLayoutData(GridDataUtil.getInstance().getGridData());
    getFormToolkit().createLabel(commentComp, "Comment");
    GridData commentGridData = GridDataUtil.getInstance().getGridData();
    commentGridData.grabExcessVerticalSpace = false;
    commentGridData.heightHint = 40;
    this.textBoxContentDisplay = new TextBoxContentDisplay(commentComp,
        SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, commentGridData, true, AttributeDialog.MAX_LENGTH);
    this.comment = this.textBoxContentDisplay.getText();
    this.comment.addModifyListener(evnt -> checkSaveBtnEnable());

  }

  /**
   * This method initializes composite2
   */
  private void createRightComposite() {
    GridData gridData2 = GridDataUtil.getInstance().getGridData();
    gridData2.grabExcessVerticalSpace = false;
    this.composite2 = getFormToolkit().createComposite(this.composite);
    this.composite2.setLayout(new GridLayout());
    createRightSection();
    this.composite2.setLayoutData(gridData2);
  }

  /**
   * This method initializes section1
   */
  private void createLeftSection() {
    GridData gridData3 = GridDataUtil.getInstance().getGridData();
    this.leftSection =
        getFormToolkit().createSection(this.leftComposite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.leftSection.setExpanded(true);
    this.leftSection.setText("General Info:");
    createLeftForm();
    this.leftSection.setLayoutData(gridData3);
    this.leftSection.setClient(this.leftForm);
  }

  /**
   * This method initializes section2
   */
  private void createRightSection() {
    GridData gridData4 = GridDataUtil.getInstance().getGridData();
    this.rightSection =
        getFormToolkit().createSection(this.composite2, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.rightSection.setExpanded(true);
    createRightForm();
    this.rightSection.setLayoutData(gridData4);
    this.rightSection.setClient(this.rightForm);
  }

  /**
   * This method initializes form1
   */
  private void createLeftForm() {
    GridData gridDataTextField = getTextFieldGridData();
    GridData gridDataTextArea = getTextAreaGridData();
    GridLayout gridLayout1 = new GridLayout();
    gridLayout1.numColumns = 2;
    this.leftForm = getFormToolkit().createForm(this.leftSection);
    this.leftForm.getBody().setLayout(gridLayout1);

    createNameEngField(gridDataTextField);
    // ICDM-112
    this.txtNameEngDec = new ControlDecoration(this.nameEngText, SWT.LEFT | SWT.TOP);
    this.decorators.showReqdDecoration(this.txtNameEngDec, "This field is mandatory.");

    createNameGermField(gridDataTextField);
    createEADMField(gridDataTextField);
    createDescEngField(gridDataTextArea);
    createDescGermanField(gridDataTextArea);
    getFormToolkit().createLabel(this.leftForm.getBody(), "");
    getFormToolkit().createLabel(this.leftForm.getBody(), "Mandatory(Default)");
    createComboMandatory();
    getFormToolkit().createLabel(this.leftForm.getBody(), "Is external ?");
    createComboAttrSecure();
    getFormToolkit().createLabel(this.leftForm.getBody(), ApicUiConstants.VALUE_CAN_BE_ADDED_BY_USERS);
    createComboAddValFlag();
  }

  /**
   *
   */
  public void disableValAddFlag() {
    this.comboAddValFlag.setText(ApicConstants.USED_YES_DISPLAY);
    this.comboAddValFlag.setEnabled(false);
  }

  /**
   *
   */

  private void createComboAddValFlag() {
    GridData gridData = getTextFieldGridData();
    this.comboAddValFlag = new Combo(this.leftForm.getBody(), SWT.READ_ONLY);
    this.comboAddValFlag.setLayoutData(gridData);
    this.comboAddValFlag.add(ApicConstants.USED_YES_DISPLAY);
    this.comboAddValFlag.add(ApicConstants.USED_NO_DISPLAY);
    this.comboAddValFlag.select(0);
    // ICDM-112
    this.comboAddValFlag.addSelectionListener(new SelectionAdapter() {

      /**
       * Enable/disable save button
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        int selectionIndex = AttributeDialog.this.comboAddValFlag.getSelectionIndex();
        if (selectionIndex == -1) {
          return;

        }
        checkSaveBtnEnable();
      }

    });
  }

  /**
   * @param gridDataTextArea
   */
  private void createDescGermanField(final GridData gridDataTextArea) {
    getFormToolkit().createLabel(this.leftForm.getBody(), "Description(German)");
    this.descGermText =
        getFormToolkit().createText(this.leftForm.getBody(), null, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
    this.descGermText.setLayoutData(gridDataTextArea);
    this.descGermText.addModifyListener(evnt -> checkSaveBtnEnable());
  }

  /**
   * @param gridDataTextArea
   */
  private void createDescEngField(final GridData gridDataTextArea) {
    getFormToolkit().createLabel(this.leftForm.getBody(), "Description(English)");
    this.descEngText =
        getFormToolkit().createText(this.leftForm.getBody(), null, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
    getFormToolkit().createLabel(this.leftForm.getBody(), "");
    this.descEngText.setLayoutData(gridDataTextArea);
    this.descEngText.addModifyListener(evnt -> {
      // ICDM-112
      Validator.getInstance().validateNDecorate(AttributeDialog.this.txtNameEngDec, AttributeDialog.this.txtEadmNameDec,
          AttributeDialog.this.txtDescEngDec, AttributeDialog.this.comboValTypeDec,
          AttributeDialog.this.comboNormFlagDec, AttributeDialog.this.comboMandatoryDec,
          AttributeDialog.this.nameEngText, AttributeDialog.this.eadmNameText, AttributeDialog.this.descEngText,
          AttributeDialog.this.comboValType, AttributeDialog.this.comboNormFlag, AttributeDialog.this.comboMandatory,
          true);

      checkSaveBtnEnable();
    });
    // ICDM-112
    this.txtDescEngDec = new ControlDecoration(this.descEngText, SWT.LEFT | SWT.TOP);
    this.decorators.showReqdDecoration(this.txtDescEngDec, IUtilityConstants.MANDATORY_MSG);
  }

  /**
   * @param gridDataTextField
   */
  private void createEADMField(final GridData gridDataTextField) {
    // ICDM-1560
    getFormToolkit().createLabel(this.leftForm.getBody(), "EADM Name:");
    this.eadmNameText = getFormToolkit().createText(this.leftForm.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.eadmNameText.setLayoutData(gridDataTextField);
    this.txtEadmNameDec = new ControlDecoration(this.eadmNameText, SWT.LEFT | SWT.TOP);
    this.decorators.showReqdDecoration(this.txtEadmNameDec, "This field is mandatory.");
    this.eadmNameText.addModifyListener(evnt -> {
      Validator.getInstance().validateNDecorate(AttributeDialog.this.txtNameEngDec, AttributeDialog.this.txtEadmNameDec,
          AttributeDialog.this.txtDescEngDec, AttributeDialog.this.comboValTypeDec,
          AttributeDialog.this.comboNormFlagDec, AttributeDialog.this.comboMandatoryDec,
          AttributeDialog.this.nameEngText, AttributeDialog.this.eadmNameText, AttributeDialog.this.descEngText,
          AttributeDialog.this.comboValType, AttributeDialog.this.comboNormFlag, AttributeDialog.this.comboMandatory,
          true);
      checkSaveBtnEnable();
    });
    this.eadmNameText.addFocusListener(new FocusListener() {

      /**
       * EADM name validation. Checks for invalid characters
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void focusLost(final FocusEvent event) {
        String eadmName = AttributeDialog.this.eadmNameText.getText();
        if (!eadmName.isEmpty() && !CommonUtils.isValidEadmName(eadmName)) {
          AttributeDialog.this.eadmNameText.setText(CommonUtils.formatEadmName(eadmName));
          CDMLogger.getInstance().infoDialog(
              "White space(s) and special character(s) : ÄÖÜäöü/ß in EADM name are replaced by '_'",
              Activator.PLUGIN_ID);
        }

      }

      /**
       * Not applicable
       */
      @Override
      public void focusGained(final FocusEvent event) {
        // No action required
      }
    });
  }

  /**
   * @param gridDataTextField
   */
  private void createNameGermField(final GridData gridDataTextField) {
    getFormToolkit().createLabel(this.leftForm.getBody(), "Name(German)");
    this.nameGermText = getFormToolkit().createText(this.leftForm.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.nameGermText.setLayoutData(gridDataTextField);
    this.nameGermText.addModifyListener(evnt -> checkSaveBtnEnable());
  }

  /**
   * @param gridDataTextField
   */
  private void createNameEngField(final GridData gridDataTextField) {
    getFormToolkit().createLabel(this.leftForm.getBody(), "Name(English)");
    this.nameEngText = getFormToolkit().createText(this.leftForm.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.nameEngText.setLayoutData(gridDataTextField);
    // ICDM-183
    this.nameEngText.setFocus();

    this.nameEngText.addModifyListener(evnt -> {
      // ICDM-112
      Validator.getInstance().validateNDecorate(AttributeDialog.this.txtNameEngDec, AttributeDialog.this.txtEadmNameDec,
          AttributeDialog.this.txtDescEngDec, AttributeDialog.this.comboValTypeDec,
          AttributeDialog.this.comboNormFlagDec, AttributeDialog.this.comboMandatoryDec,
          AttributeDialog.this.nameEngText, AttributeDialog.this.eadmNameText, AttributeDialog.this.descEngText,
          AttributeDialog.this.comboValType, AttributeDialog.this.comboNormFlag, AttributeDialog.this.comboMandatory,
          true);
      checkSaveBtnEnable();
    });
    this.nameEngText.addFocusListener(new FocusListener() {

      /**
       * Set formatted English name as default EADM name
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void focusLost(final FocusEvent event) {
        if ((!AttributeDialog.this.nameEngText.getText().isEmpty()) &&
            (AttributeDialog.this.eadmNameText.getText().isEmpty())) {
          AttributeDialog.this.eadmNameText
              .setText(CommonUtils.formatEadmName(AttributeDialog.this.nameEngText.getText()));
        }

      }

      /**
       * Not applicable
       */
      @Override
      public void focusGained(final FocusEvent event) {
        // No action required
      }
    });
  }

  /**
   * Icdm-480 create combo for Attr Security
   */
  private void createComboAttrSecure() {
    GridData gridData = getTextFieldGridData();
    this.comboAttrSecure = new Combo(this.leftForm.getBody(), SWT.READ_ONLY);
    this.comboAttrSecure.setLayoutData(gridData);
    this.comboAttrSecure.add(ApicConstants.USED_YES_DISPLAY);
    this.comboAttrSecure.add(ApicConstants.USED_NO_DISPLAY);
    this.comboAttrSecure.select(0);
    // ICDM-112
    this.comboAttrSecure.addSelectionListener(new SelectionAdapter() {

      /**
       * Enable/disable save button
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        int selectionIndex = AttributeDialog.this.comboAttrSecure.getSelectionIndex();
        if (selectionIndex == -1) {
          return;

        }
        checkSaveBtnEnable();
      }

    });
  }


  /**
   * ICDM 451 returns the layout needed for text area
   *
   * @return GridData
   */
  private GridData getTextAreaGridData() {
    GridData gridData2 = new GridData();
    gridData2.grabExcessHorizontalSpace = true;
    gridData2.horizontalAlignment = GridData.FILL;
    gridData2.verticalSpan = 2;
    gridData2.heightHint = 35;
    gridData2.minimumWidth = 160;
    gridData2.grabExcessVerticalSpace = true;
    return gridData2;
  }

  /**
   * This method initializes rightForm
   */
  private void createRightForm() {

    GridData gridDataTextField = getTextFieldGridData();
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    this.rightForm = getFormToolkit().createForm(this.rightSection);
    this.rightForm.getBody().setLayout(gridLayout);
    getFormToolkit().createLabel(this.rightForm.getBody(), "Value Type");
    createComboValType();
    this.dateFormatLabel = getFormToolkit().createLabel(this.rightForm.getBody(), "Date Format");
    createComboDateFrmt();
    createNumberFormatField(gridDataTextField);
    createUnitField(gridDataTextField);
    // ICDM-112
    this.txtUnitDec = new ControlDecoration(this.unitText, SWT.LEFT | SWT.TOP);
    getFormToolkit().createLabel(this.rightForm.getBody(), "Normalised Flag");
    createComboNormFlag();
    /**
     * Icdm-480 Combo for Attr Value Security
     */
    getFormToolkit().createLabel(this.rightForm.getBody(), ApicConstants.CHARACTERISTIC);
    createComboChar();
    getFormToolkit().createLabel(this.rightForm.getBody(), "Is value external ?");
    createComboAttrValFlag();

    getFormToolkit().createLabel(this.rightForm.getBody(), "Is grouped attribute ?");
    createComboGrpdAttrFlag();

  }

  /**
   * ICDM-2590
   */
  private void createComboGrpdAttrFlag() {

    GridData gridData = getTextFieldGridData();
    this.comboGrpdAttr = new Combo(this.rightForm.getBody(), SWT.READ_ONLY);
    this.comboGrpdAttr.setLayoutData(gridData);
    this.comboGrpdAttr.add(ApicConstants.USED_NO_DISPLAY);
    this.comboGrpdAttr.add(ApicConstants.USED_YES_DISPLAY);
    this.comboGrpdAttr.select(0);

    this.comboGrpdAttr.addSelectionListener(new SelectionAdapter() {


      /**
       * Enable/disable save button
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        // TODO - Deepthi
      }
    });

  }

  /**
   * @param gridDataTextField
   */
  private void createUnitField(final GridData gridDataTextField) {
    this.unitLabel = getFormToolkit().createLabel(this.rightForm.getBody(), "Unit");
    this.unitText = getFormToolkit().createText(this.rightForm.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.unitText.setLayoutData(gridDataTextField);
    this.unitText.setEnabled(false);
    this.unitText.addModifyListener(evnt -> {
      // ICDM-112
      Validator.getInstance().validateNDecorate(AttributeDialog.this.txtUnitDec, AttributeDialog.this.txtEadmNameDec,
          AttributeDialog.this.txtDescEngDec, AttributeDialog.this.comboValTypeDec,
          AttributeDialog.this.comboNormFlagDec, AttributeDialog.this.comboMandatoryDec, AttributeDialog.this.unitText,
          AttributeDialog.this.nameEngText, AttributeDialog.this.descEngText, AttributeDialog.this.comboValType,
          AttributeDialog.this.comboNormFlag, AttributeDialog.this.comboMandatory, true);

      if ("".equals(AttributeDialog.this.unitText.getText().trim())) {
        AttributeDialog.this.decorators.showReqdDecoration(AttributeDialog.this.txtUnitDec,
            IUtilityConstants.MANDATORY_MSG);
      }
      else {
        AttributeDialog.this.decorators.showErrDecoration(AttributeDialog.this.txtUnitDec, "", false);
      }
      checkSaveBtnEnable();

    });
  }

  /**
   * @param gridDataTextField
   */
  private void createNumberFormatField(final GridData gridDataTextField) {
    this.numberFrmtLabel = getFormToolkit().createLabel(this.rightForm.getBody(), "Number Format");
    this.numberFrmtText = getFormToolkit().createText(this.rightForm.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.numberFrmtText.setEnabled(false);
    this.numberFrmtText.setLayoutData(gridDataTextField);

    this.numberFrmtText.addModifyListener(evnt -> {
      // ICDM-112
      if (CommonUiUtils.validateNumberFormat(AttributeDialog.this.numberFrmtText.getText().trim())) {
        AttributeDialog.this.decorators.showErrDecoration(AttributeDialog.this.txtNumFormatDec, "", false);
      }
      else {
        AttributeDialog.this.decorators.showErrDecoration(AttributeDialog.this.txtNumFormatDec,
            "This field contains invalid input", true);
      }
      CommonUiUtils.validateNumFormatStyle(AttributeDialog.this.numberFrmtText.getText().trim(),
          AttributeDialog.this.decorators, AttributeDialog.this.txtNumFormatDec);
      checkSaveBtnEnable();
    });

    // ICDM-112
    this.numberFrmtText.addKeyListener(new KeyAdapter() {

      /**
       * Validate number format definition
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void keyPressed(final KeyEvent evnt) {
        int index = AttributeDialog.this.comboValType.getSelectionIndex();
        String valType = AttributeDialog.this.comboValType.getItem(index);
        if (!"".equals(valType) && valType.equalsIgnoreCase(AttributeValueType.NUMBER.getDisplayText())) {
          char key = evnt.character;
          if (!isValidKeyPress(key)) {
            evnt.doit = false;
          }
        }
      }

    });
    this.txtNumFormatDec = new ControlDecoration(this.numberFrmtText, SWT.LEFT | SWT.TOP);
  }

  /**
   * @param key char
   * @return boolean
   */
  private boolean isValidKeyPress(final char key) {
    return Character.isDigit(key) || checkPunct(key) || checkOper(key) || checkKey(key);
  }

  /**
   * @param key
   * @return
   */
  private boolean checkKey(final char key) {
    return (key == SWT.DEL) || (key == SWT.BS) || (key == SWT.ARROW_RIGHT) || (key == SWT.ARROW_LEFT);
  }

  /**
   * @param key
   * @return
   */
  private boolean checkOper(final char key) {
    return (key == '+') || (key == '-');
  }

  /**
   * @param key
   * @return
   */
  private boolean checkPunct(final char key) {
    return (key == '.') || (key == ',');
  }

  /**
   * ICdm-955 new Combo for Characteristic
   */
  private void createComboChar() {

    GridData gridData = getTextFieldGridData();
    this.comboChar = new Combo(this.rightForm.getBody(), SWT.READ_ONLY);
    this.comboChar.setLayoutData(gridData);
    this.comboChar.add("<SELECT>");
    this.characteristics = getAllCharSet();
    for (Characteristic attributeCharacteristic : this.characteristics) {
      this.comboChar.add(attributeCharacteristic.getName());
    }
    this.comboChar.select(0);
    this.comboChar.addSelectionListener(new SelectionAdapter() {

      /**
       * Enable/disable save button
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        checkSaveBtnEnable();
      }
    });
  }

  /**
   * @return
   */
  private SortedSet<Characteristic> getAllCharSet() {
    CharacteristicServiceClient client = new CharacteristicServiceClient();
    SortedSet<Characteristic> charSet = new TreeSet<>();
    try {
      charSet.addAll(client.getAll());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
    }
    return charSet;
  }

  /**
   * @return
   */
  private GridData getTextFieldGridData() {
    GridData gridData2 = new GridData();
    gridData2.grabExcessHorizontalSpace = true;
    gridData2.horizontalAlignment = GridData.FILL;
    gridData2.verticalAlignment = GridData.CENTER;
    gridData2.grabExcessVerticalSpace = true;
    return gridData2;
  }

  /**
   * This method initializes comboValType
   */
  private void createComboValType() {

    GridData gridData = getTextFieldGridData();
    this.comboValType = new Combo(this.rightForm.getBody(), SWT.READ_ONLY);
    this.comboValType.setLayoutData(gridData);
    this.comboValType.add(ApicConstants.DEFAULT_COMBO_SELECT);
    this.comboValType.add(AttributeValueType.BOOLEAN.getDisplayText());
    this.comboValType.add(AttributeValueType.DATE.getDisplayText());
    this.comboValType.add(AttributeValueType.HYPERLINK.getDisplayText());
    this.comboValType.add(AttributeValueType.NUMBER.getDisplayText());
    this.comboValType.add(AttributeValueType.TEXT.getDisplayText());
    this.comboValType.add(AttributeValueType.ICDM_USER.getDisplayText());
    this.comboValType.select(0);
    this.comboValType.addSelectionListener(new SelectionAdapter() {

      /**
       * Enable/disable save button
       */
      @Override
      public void widgetSelected(final SelectionEvent evnt) {
        // ICDM-112
        Validator.getInstance().validateNDecorate(AttributeDialog.this.txtNameEngDec,
            AttributeDialog.this.txtEadmNameDec, AttributeDialog.this.txtDescEngDec,
            AttributeDialog.this.comboValTypeDec, AttributeDialog.this.comboNormFlagDec,
            AttributeDialog.this.comboMandatoryDec, AttributeDialog.this.nameEngText, AttributeDialog.this.eadmNameText,
            AttributeDialog.this.descEngText, AttributeDialog.this.comboValType, AttributeDialog.this.comboNormFlag,
            AttributeDialog.this.comboMandatory, true);
        checkSaveBtnEnable();
        openAttrDialog();
      }
    });
    // ICDM-112
    this.comboValTypeDec = new ControlDecoration(this.comboValType, SWT.LEFT | SWT.TOP);
    this.decorators.showReqdDecoration(this.comboValTypeDec, IUtilityConstants.MANDATORY_MSG);
  }

  /**
   * @param item
   */
  private void openAttrDialog() {
    String attributeValType =
        AttributeDialog.this.comboValType.getItem(AttributeDialog.this.comboValType.getSelectionIndex());
    if (attributeValType.equalsIgnoreCase(AttributeValueType.NUMBER.getDisplayText())) {
      openAttrDialogForNumDataType();
    }
    else if (attributeValType.equalsIgnoreCase(AttributeValueType.DATE.getDisplayText())) {
      openAttrDialogForDateDataType();
    }
    else {
      openAttrDialogForOtherDataType();
    }
  }

  /**
   *
   */
  private void openAttrDialogForNumDataType() {
    AttributeDialog.this.unitLabel.setEnabled(true);
    AttributeDialog.this.unitText.setEnabled(true);
    AttributeDialog.this.dateFormatLabel.setEnabled(false);
    AttributeDialog.this.numberFrmtLabel.setEnabled(true);
    AttributeDialog.this.numberFrmtText.setEnabled(true);
    AttributeDialog.this.comboDateFormat.setEnabled(false);
    AttributeDialog.this.comboDateFormat.removeAll();
    // ICDM-112
    AttributeDialog.this.decorators.showReqdDecoration(AttributeDialog.this.txtUnitDec,
        IUtilityConstants.MANDATORY_MSG);
    AttributeDialog.this.decorators.showReqdDecoration(AttributeDialog.this.txtNumFormatDec,
        IUtilityConstants.MANDATORY_MSG);
  }

  /**
   *
   */
  private void openAttrDialogForOtherDataType() {
    AttributeDialog.this.unitLabel.setEnabled(false);
    AttributeDialog.this.unitText.setEnabled(false);
    AttributeDialog.this.dateFormatLabel.setEnabled(false);
    AttributeDialog.this.unitText.setText("");
    AttributeDialog.this.numberFrmtLabel.setEnabled(false);
    AttributeDialog.this.numberFrmtText.setEnabled(false);
    AttributeDialog.this.numberFrmtText.setText("");
    AttributeDialog.this.comboDateFormat.setEnabled(false);
    AttributeDialog.this.comboDateFormat.removeAll();
    // ICDM-112
    AttributeDialog.this.decorators.showErrDecoration(AttributeDialog.this.txtUnitDec, "", false);
    AttributeDialog.this.comboDateFormat.setEnabled(false);
    AttributeDialog.this.comboDateFormat.removeAll();
    AttributeDialog.this.decorators.showErrDecoration(AttributeDialog.this.dateformatDec, "", false);
    AttributeDialog.this.decorators.showErrDecoration(AttributeDialog.this.txtNumFormatDec, "", false);
  }

  /**
   *
   */
  private void openAttrDialogForDateDataType() {
    AttributeDialog.this.dateFormatLabel.setEnabled(true);
    AttributeDialog.this.comboDateFormat.setEnabled(true);
    SortedSet<String> dateFormats = CommonUiUtils.getDateFormats();
    for (String string : dateFormats) {
      AttributeDialog.this.comboDateFormat.add(string);
    }
    AttributeDialog.this.comboDateFormat.select(0);
    AttributeDialog.this.numberFrmtLabel.setEnabled(false);
    AttributeDialog.this.numberFrmtText.setEnabled(false);
    AttributeDialog.this.numberFrmtText.setText("");
    // ICDM-112
    AttributeDialog.this.unitLabel.setEnabled(false);
    AttributeDialog.this.unitText.setEnabled(false);
    AttributeDialog.this.unitText.setText("");

    AttributeDialog.this.decorators.showErrDecoration(AttributeDialog.this.dateformatDec, "", false);
    AttributeDialog.this.decorators.showErrDecoration(AttributeDialog.this.txtNumFormatDec, "", false);
    AttributeDialog.this.decorators.showErrDecoration(AttributeDialog.this.txtUnitDec, "", false);
  }


  /**
   * This method initializes comboSuperGrp
   */
  private void createComboSuperGroup() {

    GridData gridData = getTextFieldGridData();
    this.comboSuperGrp = new Combo(this.form.getBody(), SWT.READ_ONLY);
    this.comboSuperGrp.setLayoutData(gridData);
    getSuperGroups();

    this.comboSuperGrp.add(ApicConstants.DEFAULT_COMBO_SELECT);
    Object outlineCurrentSelection = AttributesOutlinePage.getCurrentSelection();
    AttrSuperGroup supGrpCurOutlineSel = null;
    AttrGroup attrGrpCurOutlineSel = null;

    for (AttrSuperGroup attrSuperGroup : this.superGrp) {
      this.comboSuperGrp.add(attrSuperGroup.getName());
    }
    if (outlineCurrentSelection != null) {
      if (outlineCurrentSelection instanceof AttrSuperGroup) {
        supGrpCurOutlineSel = (AttrSuperGroup) outlineCurrentSelection;
      }
      if (outlineCurrentSelection instanceof AttrGroup) {
        attrGrpCurOutlineSel = (AttrGroup) outlineCurrentSelection;
      }
    }

    selectValueInCombo(supGrpCurOutlineSel, attrGrpCurOutlineSel);

    this.comboSuperGrp.addSelectionListener(new SelectionAdapter() {

      /**
       * Populate Groups combobox. Enable/disable save button.
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent evnt) {
        Validator.getInstance().validateNDecorate(AttributeDialog.this.comboSuperGrpDec,
            AttributeDialog.this.comboSuperGrp, true);
        checkSaveBtnEnable();

        int index = AttributeDialog.this.comboSuperGrp.getSelectionIndex();
        String selItem = AttributeDialog.this.comboSuperGrp.getItem(index);
        if (selItem.equals(ApicConstants.DEFAULT_COMBO_SELECT)) {
          SortedSet<AttrGroup> emptyGroups = new TreeSet<>();
          setGroups(emptyGroups, null);
          return;
        }

        addGroupsOfSuperGrp(selItem);

        // attrGroupCurrentOutlineSelection
        setGroups(AttributeDialog.this.groups, null);

      }


    });
    // Add Decorators
    this.comboSuperGrpDec = new ControlDecoration(this.comboSuperGrp, SWT.LEFT | SWT.TOP);
    this.decorators.showReqdDecoration(this.comboSuperGrpDec, IUtilityConstants.MANDATORY_MSG);
    Validator.getInstance().validateNDecorate(AttributeDialog.this.comboSuperGrpDec, AttributeDialog.this.comboSuperGrp,
        true);
  }

  /**
   *
   */
  private void getSuperGroups() {
    this.superGrp = new TreeSet<>(this.attributesPage.getAttrGroupModel().getAllSuperGroupMap().values());
  }

  /**
   * @param supGrpCurOutlineSel
   * @param attrGrpCurOutlineSel
   */
  private void selectValueInCombo(final AttrSuperGroup supGrpCurOutlineSel, final AttrGroup attrGrpCurOutlineSel) {
    if (supGrpCurOutlineSel != null) {
      this.comboSuperGrp.select(this.comboSuperGrp.indexOf(supGrpCurOutlineSel.getName()));
    }
    else if (attrGrpCurOutlineSel != null) {

      this.comboSuperGrp.select(this.comboSuperGrp.indexOf(this.attributesPage.getAttrGroupModel().getAllSuperGroupMap()
          .get(attrGrpCurOutlineSel.getSuperGrpId()).getName()));
    }
    else {
      this.comboSuperGrp.select(0);
    }
  }

  /**
   * This method initializes comboGrp
   */
  private void createComboGroup() {

    GridData gridData = getTextFieldGridData();
    this.comboGrp = new Combo(this.form.getBody(), SWT.READ_ONLY);
    this.comboGrp.setLayoutData(gridData);

    this.comboGrpDec = new ControlDecoration(this.comboGrp, SWT.LEFT | SWT.TOP);
    this.decorators.showReqdDecoration(this.comboGrpDec, IUtilityConstants.MANDATORY_MSG);

    Object outlineCurrentSelection = AttributesOutlinePage.getCurrentSelection();
    AttrGroup attrGrpCurOutlineSel = null;

    if (outlineCurrentSelection instanceof AttrGroup) {
      attrGrpCurOutlineSel = (AttrGroup) outlineCurrentSelection;
    }
    String selItem = this.comboSuperGrp.getItem(this.comboSuperGrp.getSelectionIndex());
    addGroupsOfSuperGrp(selItem);

    if (selItem.equals(ApicConstants.DEFAULT_COMBO_SELECT)) {
      this.comboGrp.add(ApicConstants.NO_GROUP_COMBO_SELECT);
      this.comboGrp.select(0);
    }
    else {
      setGroups(this.groups, attrGrpCurOutlineSel);
    }
    this.comboGrp.addSelectionListener(new SelectionAdapter() {

      /**
       * Enable/disable save button
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent evnt) {
        Validator.getInstance().validateNDecorate(AttributeDialog.this.comboGrpDec, AttributeDialog.this.comboGrp,
            true);
        checkSaveBtnEnable();

      }
    });

    Validator.getInstance().validateNDecorate(AttributeDialog.this.comboGrpDec, AttributeDialog.this.comboGrp, true);
  }

  /**
   * This method initializes comboValType
   */
  private void createComboDateFrmt() {

    GridData gridData = getTextFieldGridData();
    this.comboDateFormat = new Combo(this.rightForm.getBody(), SWT.READ_ONLY);
    this.comboDateFormat.setLayoutData(gridData);
    this.comboDateFormat.setEnabled(false);

    this.dateformatDec = new ControlDecoration(this.comboDateFormat, SWT.LEFT | SWT.TOP);

  }

  /**
   * This method initializes comboNormFlag
   */
  private void createComboNormFlag() {

    GridData gridData = getTextFieldGridData();
    this.comboNormFlag = new Combo(this.rightForm.getBody(), SWT.READ_ONLY);
    this.comboNormFlag.setLayoutData(gridData);
    this.comboNormFlag.add(ApicConstants.DEFAULT_COMBO_SELECT);
    this.comboNormFlag.add(ApicConstants.USED_YES_DISPLAY);
    this.comboNormFlag.add(ApicConstants.USED_NO_DISPLAY);
    this.comboNormFlag.select(0);

    this.comboNormFlag.addSelectionListener(new SelectionAdapter() {

      /**
       * Enable/disable save button
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        Validator.getInstance().validateNDecorate(AttributeDialog.this.comboNormFlagDec,
            AttributeDialog.this.comboNormFlag, true);
        checkSaveBtnEnable();
      }
    });

    // ICDM-112
    this.comboNormFlagDec = new ControlDecoration(this.comboNormFlag, SWT.LEFT | SWT.TOP);
    this.decorators.showReqdDecoration(this.comboNormFlagDec, IUtilityConstants.MANDATORY_MSG);

  }


  /**
   * Icdm-480 This method initializes ComboAttrValFlaglag
   */
  private void createComboAttrValFlag() {

    GridData gridData = getTextFieldGridData();
    this.comboAttrValSecurity = new Combo(this.rightForm.getBody(), SWT.READ_ONLY);
    this.comboAttrValSecurity.setLayoutData(gridData);
    this.comboAttrValSecurity.add(ApicConstants.USED_YES_DISPLAY);
    this.comboAttrValSecurity.add(ApicConstants.USED_NO_DISPLAY);
    this.comboAttrValSecurity.select(0);

    this.comboAttrValSecurity.addSelectionListener(new SelectionAdapter() {

      /**
       * Enable/disable save button
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        checkSaveBtnEnable();
      }
    });

  }

  /**
   * This method initializes createComboMandatory APIC-62
   */
  private void createComboMandatory() {

    GridData gridData = getTextFieldGridData();
    this.comboMandatory = new Combo(this.leftForm.getBody(), SWT.READ_ONLY);
    this.comboMandatory.setLayoutData(gridData);
    this.comboMandatory.add(ApicConstants.DEFAULT_COMBO_SELECT);
    this.comboMandatory.add(ApicConstants.USED_YES_DISPLAY);
    this.comboMandatory.add(ApicConstants.USED_NO_DISPLAY);
    this.comboMandatory.select(0);
    // ICDM-112
    this.comboMandatoryDec = new ControlDecoration(this.comboMandatory, SWT.LEFT | SWT.TOP);

    this.comboMandatory.addSelectionListener(new SelectionAdapter() {

      /**
       * Enable/disable save button
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent evnt) {
        int selectionIndex = AttributeDialog.this.comboMandatory.getSelectionIndex();
        if (selectionIndex == -1) {
          return;
        }
        Validator.getInstance().validateNDecorate(AttributeDialog.this.comboMandatoryDec,
            AttributeDialog.this.comboMandatory, true);
        checkSaveBtnEnable();
      }

    });
    this.decorators.showReqdDecoration(this.comboMandatoryDec, IUtilityConstants.MANDATORY_MSG);
  }

  /**
   * This method validates text and combo fields
   *
   * @return boolean
   */
  protected boolean validateFields() {
    // Validations for save button
    String nameEng = this.nameEngText.getText();
    int supGrpIndex = this.comboSuperGrp.getSelectionIndex();
    String supGrpItem = this.comboSuperGrp.getItem(supGrpIndex).trim();
    int grpIndex = this.comboGrp.getSelectionIndex();
    String grpItem = this.comboGrp.getItem(grpIndex).trim();
    int norIndex = this.comboNormFlag.getSelectionIndex();
    String norItem = this.comboNormFlag.getItem(norIndex).trim();
    int valTypeIndex = this.comboValType.getSelectionIndex();
    String valTypeItem = this.comboValType.getItem(valTypeIndex).trim();
    String format = this.numberFrmtText.getText().trim();
    String descEng = this.descEngText.getText();
    int valMandatoryIndex = this.comboMandatory.getSelectionIndex();
    String valmanItem = this.comboMandatory.getItem(valMandatoryIndex).trim();
    String eadmName = this.eadmNameText.getText();
    if (valTypeItem.equalsIgnoreCase(AttributeValueType.NUMBER.getDisplayText())) {
      if (checkGrpSupGrp(supGrpItem, grpItem) && checkNameDesVal(nameEng, descEng, eadmName, norItem, valTypeItem) &&
          isValid(norItem, valTypeItem, format, valmanItem)) {
        return true;
      }
    }
    else if (valTypeItem.equalsIgnoreCase(AttributeValueType.DATE.getDisplayText())) {
      int index = this.comboDateFormat.getSelectionIndex();
      if (index > -1) {
        String dateFormat = this.comboDateFormat.getItem(index);
        if (checkGrpSupGrp(supGrpItem, grpItem) && checkNameDesVal(nameEng, descEng, eadmName, norItem, valTypeItem) &&
            !"Date formats are not defined".equalsIgnoreCase(dateFormat) &&
            !ApicConstants.DEFAULT_COMBO_SELECT.equals(valmanItem)) {
          return true;
        }
      }
    }
    else {
      if (checkGrpSupGrp(supGrpItem, grpItem) && checkNameDesVal(nameEng, descEng, eadmName, norItem, valTypeItem) &&
          !ApicConstants.DEFAULT_COMBO_SELECT.equals(valmanItem)) {
        return true;
      }
    }
    return false;

  }

  /**
   * @param supGrpItem
   * @param grpItem
   * @return
   */
  private boolean checkGrpSupGrp(final String supGrpItem, final String grpItem) {
    return checkSupGrp(supGrpItem) && checkGrp(grpItem);
  }

  /**
   * @param nameEng
   * @param descEng
   * @param eadmName
   * @param norItem
   * @param valTypeItem
   * @return
   */
  private boolean checkNameDesVal(final String nameEng, final String descEng, final String eadmName,
      final String norItem, final String valTypeItem) {
    return checkNameDes(nameEng, descEng, eadmName) && checkNorValType(norItem, valTypeItem);
  }

  /**
   * @param norItem
   * @param valTypeItem
   * @param format
   * @param valmanItem
   * @return
   */
  private boolean isValid(final String norItem, final String valTypeItem, final String format,
      final String valmanItem) {
    return checkNorValType(norItem, valTypeItem) && !"".equals(this.unitText.getText()) && checkFormat(format) &&
        !ApicConstants.DEFAULT_COMBO_SELECT.equals(valmanItem);
  }

  /**
   * @param format
   * @return
   */
  private boolean checkFormat(final String format) {
    return CommonUiUtils.validateNumberFormat(format) && CommonUiUtils.validateFormat(format);
  }

  /**
   * @param norItem
   * @param valTypeItem
   * @return
   */
  private boolean checkNorValType(final String norItem, final String valTypeItem) {
    return !"".equals(norItem) && !ApicConstants.DEFAULT_COMBO_SELECT.equals(norItem) && !"".equals(valTypeItem) &&
        !ApicConstants.DEFAULT_COMBO_SELECT.equals(valTypeItem);
  }

  /**
   * @param eadmName
   * @param descEng
   * @param supGrpItem
   * @return
   */
  private boolean checkNameDes(final String nameEng, final String descEng, final String eadmName) {
    return !"".equals(nameEng.trim()) && !"".equals(eadmName.trim()) && !"".equals(descEng.trim());
  }

  /**
   * @param supGrpItem
   * @return
   */
  private boolean checkSupGrp(final String supGrpItem) {
    return !"".equals(supGrpItem) && !ApicConstants.DEFAULT_COMBO_SELECT.equals(supGrpItem);
  }

  /**
   * @param grpItem
   * @return
   */
  private boolean checkGrp(final String grpItem) {
    return !"".equals(grpItem) && !ApicConstants.NO_GROUP_COMBO_SELECT.equalsIgnoreCase(grpItem) &&
        !ApicConstants.DEFAULT_COMBO_SELECT.equals(grpItem);
  }

  /**
   * Validates save button enable or disable
   */
  protected void checkSaveBtnEnable() {
    // Perform validations for save button
    if (validateFields() && (this.saveBtn != null)) {
      this.saveBtn.setEnabled(true);
    }
    else if (this.saveBtn != null) {
      this.saveBtn.setEnabled(false);
    }

  }

  /**
   * This method adds all groups to the combobox
   *
   * @param groups defines Sorted group set
   * @param attrGrpCurOutlineSel selected group
   */
  protected void setGroups(final SortedSet<AttrGroup> groups, final AttrGroup attrGrpCurOutlineSel) {
    String[] grpItems = this.comboGrp.getItems();
    if (grpItems.length > 0) {
      this.comboGrp.removeAll();
    }

    if (groups.isEmpty()) {
      this.comboGrp.add(ApicConstants.NO_GROUP_COMBO_SELECT);
    }
    else {
      this.comboGrp.add(ApicConstants.DEFAULT_COMBO_SELECT);
      for (AttrGroup grp : this.groups) {
        this.comboGrp.add(grp.getName());
      }
    }

    // attrGroupCurrentOutlineSelection is null indicates that a edit by user is happening
    // attrGroupCurrentOutlineSelection not null indicates group combobox is filled based on selection in Outline view
    if (attrGrpCurOutlineSel == null) {
      this.comboGrp.select(0);
    }
    else {
      this.comboGrp.select(this.comboGrp.indexOf(attrGrpCurOutlineSel.getName()));
    }

    // Added when user selects valid group and selects No Groups Available from another superGroup to remove decorator
    // Null check added to prevent NullPointerException when Dialog is initialised
    if (checkWidgetsForNull()) {
      Validator.getInstance().validateNDecorate(AttributeDialog.this.comboGrpDec, AttributeDialog.this.comboGrp, true);
      checkSaveBtnEnable();
    }
  }

  /**
   * @return
   */
  private boolean checkWidgetsForNull() {
    return isNameDescNotNull() && isComboNotNull() && (this.numberFrmtText != null) && (this.comboMandatory != null);
  }

  /**
   * @return
   */
  private boolean isNameDescNotNull() {
    return (this.nameEngText != null) && (this.descEngText != null);
  }

  /**
   * @return
   */
  private boolean isComboNotNull() {
    return (this.comboSuperGrp != null) && (this.comboGrp != null) && (this.comboNormFlag != null) &&
        (this.comboValType != null);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.DIALOG_TRIM);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean close() {
    this.okCancelPress = true;
    return super.close();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void cancelPressed() {
    this.okCancelPress = true;
    super.cancelPressed();
  }

  /**
   * @param client
   * @param selItem
   * @throws ApicWebServiceException
   */
  private void addGroupsOfSuperGrp(final String selItem) {
    for (AttrSuperGroup attrSuperGroup : AttributeDialog.this.superGrp) {
      if (attrSuperGroup.getName().equalsIgnoreCase(selItem)) {
        AttrGroupModel attrGroupModel = this.attributesPage.getAttrGroupModel();
        Map<Long, AttrGroup> allGroupMap = attrGroupModel.getAllGroupMap();
        Set<Long> groupIds;
        groupIds = attrGroupModel.getGroupBySuperGroupMap().get(attrSuperGroup.getId());

        for (Long id : groupIds) {
          AttributeDialog.this.groups.add(allGroupMap.get(id));
        }
        break;
      }
    }
  }
}