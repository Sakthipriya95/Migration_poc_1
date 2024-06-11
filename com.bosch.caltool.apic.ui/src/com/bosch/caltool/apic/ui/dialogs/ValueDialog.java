/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.views.providers.LinkTableContentProvider;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.client.bo.apic.AttributeValueClientBO.CLEARING_STATUS;
import com.bosch.caltool.icdm.client.bo.apic.PidcCreationHandler;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.dialogs.CalendarDialog;
import com.bosch.caltool.icdm.common.ui.listeners.LinkTableSelectionListener;
import com.bosch.caltool.icdm.common.ui.sorter.LinkTableSorter;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.providers.LinkTableLabelProvider;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateAndNumValidator;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.model.apic.attr.CharacteristicValue;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.CharacteristicValueServiceClient;
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
 * This class provides UI to add value
 */
public class ValueDialog extends AbstractDialog {

  /**
   * string constant for Value
   */
  private static final String STR_VALUE = "Value";
  /**
   * Button instance for save
   */
  protected Button saveBtn;

  /**
   * Button instance for cancel
   */
  Button cancelBtn;

  /**
   * ok or cancel pressed indicator
   */
  protected boolean okCancelPress;


  /**
   * Composite instance for the dialog
   */
  private Composite composite;
  /**
   * Value type Text box.
   */
  protected Text valueTypeText;
  /**
   * Attr Value
   */
  protected String attrValue;
  /**
   * Attr Value germany
   */
  protected String attrValueGer;
  /**
   * Desc Germany
   */
  protected String descGer;
  /**
   * desc english
   */
  protected String descEng;
  /**
   * Clearing status Inclearing,Cleared or Not cleared.
   */
  protected String clearingStatus;
  /**
   * Decorators instance
   */
  private final Decorators decorators = new Decorators();

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
   * the top of form
   */
  private Composite top;

  /**
   * Defines ApicObject
   */
  // ICDM-108
  private final IModel apicObject;

  /**
   * format text col
   */
  protected Text formatText;
  /**
   * Unit Value text
   */
  protected Text unitText;

  /**
   * Value in english Text
   */
  protected Text valueEngText;

  /**
   * Combo box
   */
  protected Combo combo;

  /**
   * German Label
   */
  private Label valueGerLabel;
  /**
   * German Value
   */
  protected Text valueGerText;
  /**
   * Decorator for the Combo.
   */
  private ControlDecoration comboDec;

  /**
   * Value desc english
   */
  protected Text valueDescEngText;

  /**
   * Value Desc german.
   */
  protected Text valueDescGerText;

  /**
   * Attribute instance
   */
  // ICDM-108
  protected com.bosch.caltool.icdm.model.apic.attr.Attribute attribute;
  // ICDM-112
  /**
   * English Val decorator
   */
  private ControlDecoration txtValEngDec;
  /**
   * Eng desc Decorator
   */
  private ControlDecoration txtValDescEngDec;

  /**
   * Combo for Value class
   */
  protected Combo comboCharVal;

  /**
   * set of Attribute Value class.
   */
  protected SortedSet<CharacteristicValue> attrCharValues;
  /**
   * Text for adding comment when a value is edited
   */
  protected Text comment;

  /**
   * changeComment
   */
  protected String changeComment = "";

  /**
   * whether to include PIDC Version Details Section or not
   */
  boolean includeVersionSection;
  /**
   * PIDC Version Details Section
   */
  private PIDCVersionDetailsSection versionSection;
  // Alias section
  private PIDCAliasDefSection aliasSection;

  // ICDM-2580
  private final PIDCAttrValueEditDialog pidcAttrValDialog;
  // English value
  private String valueEng;
  // German value
  private String valueGer;
  // English desciption
  private String descripEng;
  // section link
  private Section sectionLink;
  /**
   * Form instance for links
   */
  private Form formLink;
  // Table sorter
  private LinkTableSorter linksTabSorter;
  /**
   *
   */
  // Table viewer
  protected GridTableViewer linksTabViewer;
  // Action to delete link
  private Action deleteLinkAction;
  // Action to edit link
  private Action editLinkAction;
  /**
   * boolean to indicate link section is changed
   */
  protected boolean linksChanged;

  // is edit valid
  private final boolean isEdit;
  // attr client bo instance
  private AttributeClientBO attrClientBO;

  /**
   * the maximum length for the text field
   */
  private static final int MAX_TEXT_BOX_SIZE = 4000;
  /**
   * the vertical span of text field for comment
   */
  private static final int COMMENT_FIELD_VERTICAL_SPAN = 2;
  /**
   * the height of text field for comment
   */
  private static final int COMMENT_FIELD_HEIGHT_HINT = 40;
  /**
   * constant for link tab viewer height
   */
  private static final int HEIGHT_HINT_FOR_LINK_TABLE = 100;
  /**
   * constant for showing number of rows
   */
  private static final int MIN_ROW_COUNT = 5;

  /**
   *
   */
  protected Text attrNameText;
  private SashForm mainComposite;


  /**
   * The Parameterized Constructor
   *
   * @param parentShell instance
   * @param apicObject defines ApicObject
   * @param pidcAttrValueEditDialog PIDCAttrValueEditDialog
   * @param isEdit flag
   */
  // ICDM-108
  public ValueDialog(final Shell parentShell, final IModel apicObject,
      final PIDCAttrValueEditDialog pidcAttrValueEditDialog, final boolean isEdit) {
    super(parentShell);
    this.apicObject = apicObject;
    // ICDM-2580
    this.pidcAttrValDialog = pidcAttrValueEditDialog;
    this.isEdit = isEdit;
  }

  /**
   * @return the okCancelPress
   */
  public boolean isOkCancelPress() {
    return this.okCancelPress;
  }


  /**
   * @param okCancelPress the okCancelPress to set
   */
  public void setOkCancelPress(final boolean okCancelPress) {
    this.okCancelPress = okCancelPress;
  }

  /**
   * @return the linksChanged
   */
  public boolean isLinksChanged() {
    return this.linksChanged;
  }


  /**
   * @param linksChanged the linksChanged to set
   */
  public void setLinksChanged(final boolean linksChanged) {
    this.linksChanged = linksChanged;
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
   * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse .swt.widgets.Composite)
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    // NA
  }

  /**
   * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets .Composite)
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
    ScrolledComposite scrollComp = new ScrolledComposite(this.mainComposite, SWT.H_SCROLL | SWT.V_SCROLL);
    scrollComp.setLayout(new GridLayout());
    this.composite = getFormToolkit().createComposite(scrollComp, SWT.NONE);
    this.composite.setLayout(new GridLayout());
    scrollComp.setContent(this.composite);
    scrollComp.setExpandHorizontal(true);
    scrollComp.setExpandVertical(true);
    scrollComp.setDragDetect(true);
    scrollComp.addControlListener(new ControlAdapter() {

      @Override
      public void controlResized(final ControlEvent event) {
        // ICDM-895
        Rectangle rect = scrollComp.getClientArea();
        scrollComp.setMinSize(ValueDialog.this.composite.computeSize(rect.width, SWT.DEFAULT));
      }
    });
    createSection();
    // Create link section
    if (this.isEdit) {
      createLinkSection();
    }
    this.composite.setLayoutData(gridData);
    // ICDM-183
    this.section.getDescriptionControl().setEnabled(false);

    if (this.includeVersionSection) {
      this.versionSection = new PIDCVersionDetailsSection(this.composite, getFormToolkit(), false, this);
      this.versionSection.createSectionPidcVersion();
      PidcCreationHandler pidcCreationHandler = new PidcCreationHandler();
      this.aliasSection = new PIDCAliasDefSection(this.composite, getFormToolkit(), this,
          pidcCreationHandler.getPidcCreationDetails().getAliasDefMap());
      this.aliasSection.createSectionAliasDef();
    }
  }

  /**
   * create link section
   */
  private void createLinkSection() {
    // Create section for link
    this.sectionLink = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "Link Details");
    // create form
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
    // Create toolbar actions
    createToolBarAction();
    // craete table columns
    createTabColumns();

    this.linksTabViewer.setContentProvider(new LinkTableContentProvider(this));
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
    // create link column
    createLinkColumn();
    // Create english description column
    createDescEngColumn();
    // craete german description column
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

    // Add actions
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
    toolBarManager.add(cmnActionSet.addNewLinkAction(this.linksTabViewer));
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

    CommonActionSet cmnActionSet = new CommonActionSet();
    // Create an action to add new link
    this.editLinkAction = cmnActionSet.addEditLinkAction(toolBarManager, this.linksTabViewer);


  }

  /**
   * This method initializes section
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "Enter the details");
    createForm();
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form
   */
  private void createForm() {

    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;
    this.form = getFormToolkit().createForm(this.section);
    this.form.getBody().setLayout(gridLayout);
    getFormToolkit().createLabel(this.form.getBody(), "Attribute Name:");
    this.attrNameText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    // ICDM-108
    setSelectedAttr();
    if (this.attribute != null) {
      this.attrNameText.setText(this.attribute.getName());
      this.attrNameText.setEnabled(false);
      GridData txtGrid = getTextFieldGridData();
      this.attrNameText.setLayoutData(txtGrid);
      getFormToolkit().createLabel(this.form.getBody(), "");
      getFormToolkit().createLabel(this.form.getBody(), "Value Type");
      this.valueTypeText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
      this.valueTypeText.setLayoutData(txtGrid);

      // ICDM-108
      final String valueType = this.attribute.getValueType();
      this.valueTypeText.setText(valueType);
      this.valueTypeText.setEnabled(false);
      getFormToolkit().createLabel(this.form.getBody(), "");
      final String format = this.attribute.getFormat();
      // Set the Fomat- Number or Date.
      if ((valueType.equals(AttributeValueType.NUMBER.toString())) ||
          (valueType.equals(AttributeValueType.DATE.toString()))) {
        // Create Format label
        Label formatLabel = getFormToolkit().createLabel(this.form.getBody(), "");
        if (valueType.equals(AttributeValueType.NUMBER.toString())) {
          formatLabel.setText("Number Format");
        }
        else if (valueType.equals(AttributeValueType.DATE.toString())) {
          formatLabel.setText("Date Format");
        }
        this.formatText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
        this.formatText.setLayoutData(txtGrid);
        this.formatText.setEnabled(false);

        if (format != null) {
          this.formatText.setText(format);
        }
        getFormToolkit().createLabel(this.form.getBody(), "");
      }
      else {
        this.formatText = null;
      }
      // Set the Unit Text for number Value type.
      if (valueType.equals(AttributeValueType.NUMBER.toString())) {
        // Create Unit label
        getFormToolkit().createLabel(this.form.getBody(), "Unit");
        this.unitText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
        this.unitText.setLayoutData(txtGrid);
        this.unitText.setEnabled(false);

        getFormToolkit().createLabel(this.form.getBody(), "");

        String attrUnit = this.attribute.getUnit();
        this.unitText.setText(CommonUtils.checkNull(attrUnit));

      }
      else {
        this.unitText = null;
      }
      // Create Value English Label
      Label valueEngLabel = getFormToolkit().createLabel(this.form.getBody(), "");
      // ICDM-183
      this.valueEngText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
      this.valueEngText.setLayoutData(txtGrid);
      // ICDM-1268
      this.valueEngText.addKeyListener(new KeyListener() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void keyReleased(final KeyEvent keyEvent) {
          if (((keyEvent.stateMask & SWT.CTRL) == SWT.CTRL) &&
              ((keyEvent.keyCode == 'v') && (keyEvent.keyCode == SWT.CTRL))) {
            modifyClipBoard();
          }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void keyPressed(final KeyEvent keyEvent) {
          // Implementation in KeyReleased() method is sufficient
        }
      });
      MenuManager menuMgr = new MenuManager("popupmenu");
      menuMgr.setRemoveAllWhenShown(true);
      menuMgr.addMenuListener(manager -> {
        final Action pasteAction = new Action() {

          /**
           * {@inheritDoc}
           */
          @Override
          public void run() {
            modifyClipBoard();
          }
        };
        pasteAction.setText("Paste");
        ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.PASTE_16X16);
        pasteAction.setImageDescriptor(imageDesc);
        pasteAction.setEnabled(true);
        manager.add(pasteAction);
      });

      Menu menu = menuMgr.createContextMenu(this.valueEngText);
      this.valueEngText.setMenu(menu);
      this.valueEngText.setFocus();
      if (this.valueTypeText.getText().trim().equalsIgnoreCase(AttributeValueType.DATE.getDisplayText())) {
        this.valueEngText.setEnabled(true);
        this.valueEngText.setEditable(false);
      }
      else if (this.valueTypeText.getText().trim().equalsIgnoreCase(AttributeValueType.NUMBER.getDisplayText())) {
        if ((format != null) && !"".equals(format)) {
          this.valueEngText.setMessage(format);
        }
      }

      // ICDM-2580
      if ((null != this.pidcAttrValDialog) && (this.pidcAttrValDialog.getAddNewValAction().isEnabled())) {
        this.valueEngText.setText(this.pidcAttrValDialog.getFilterTxt().getText().toString());
      }
      // Add modify listener to Value English.
      this.valueEngText.addModifyListener(event -> {
        // ICDM-112
        if (ValueDialog.this.valueTypeText.getText().trim()
            .equalsIgnoreCase(AttributeValueType.NUMBER.getDisplayText())) {
          Validator.getInstance().validateNDecorate(ValueDialog.this.txtValEngDec, ValueDialog.this.valueEngText,
              ValueDialog.this.valueDescEngText, true, true, format);
        }
        else {
          // ICDM-112
          Validator.getInstance().validateNDecorate(ValueDialog.this.txtValEngDec, ValueDialog.this.txtValDescEngDec,
              ValueDialog.this.valueEngText, ValueDialog.this.valueDescEngText, true);
        }
        if (ValueDialog.this.valueTypeText.getText().trim()
            .equalsIgnoreCase(AttributeValueType.DATE.getDisplayText())) {
          if ((format != null) && !"".equals(format)) {
            DateAndNumValidator.getInstance().dateFormatValidator(format,
                ValueDialog.this.valueEngText.getText().trim());
          }
        }
        checkSaveBtnEnable();

      });
      // ICDM-112
      this.txtValEngDec = new ControlDecoration(this.valueEngText, SWT.LEFT | SWT.TOP);
      if (CommonUtils.isEmptyString(this.valueEngText.getText())) {
        this.decorators.showReqdDecoration(this.txtValEngDec, IUtilityConstants.MANDATORY_MSG);
      }

      // Check for Valur type text
      if (this.valueTypeText.getText().trim().equalsIgnoreCase(AttributeValueType.TEXT.getDisplayText())) {
        // ICDM-768
        if ((getAttribute().getLevel() == ApicConstants.VARIANT_CODE_ATTR) &&
            (getAttribute().getLevel() == ApicConstants.SUB_VARIANT_CODE_ATTR)) {
          if (this.valueGerLabel != null) {
            this.valueGerLabel.dispose();
          }

          if (this.valueGerText != null) {
            this.valueGerText.dispose();
          }

          if (this.combo != null) {
            this.combo.dispose();
          }
          valueEngLabel.setText(STR_VALUE);
        }
        else {
          getFormToolkit().createLabel(this.form.getBody(), "");

          if (this.combo != null) {
            this.combo.dispose();

          }
          valueEngLabel.setText("Value (English):");

          // create Value Germany Label
          this.valueGerLabel = getFormToolkit().createLabel(this.form.getBody(), "Value (German):");
          this.valueGerText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
          this.valueGerText.setLayoutData(txtGrid);
        }
      }
      // Check for Value type Boolean
      else if (this.valueTypeText.getText().trim().equalsIgnoreCase(AttributeValueType.BOOLEAN.getDisplayText())) {
        if (this.valueGerLabel != null) {
          this.valueGerLabel.dispose();
        }

        if (this.valueGerText != null) {
          this.valueGerText.dispose();
        }

        if (this.valueEngText != null) {
          this.valueEngText.dispose();
        }

        valueEngLabel.setText(STR_VALUE);
        createCombo();
      }
      else {
        if (this.valueGerLabel != null) {
          this.valueGerLabel.dispose();
        }

        if (this.valueGerText != null) {
          this.valueGerText.dispose();
        }

        if (this.combo != null) {
          this.combo.dispose();
        }
        valueEngLabel.setText(STR_VALUE);

      }
      Button timeStampButton = getFormToolkit().createButton(this.form.getBody(), "", SWT.PUSH);
      if (this.valueTypeText.getText().trim().equalsIgnoreCase(AttributeValueType.DATE.getDisplayText())) {
        timeStampButton.setVisible(true);
        timeStampButton.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.CALENDAR_16X16));
      }
      else {
        timeStampButton.setVisible(false);
      }

      // Add Selection listnerlto Time stamp button.
      timeStampButton.addSelectionListener(new SelectionAdapter() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void widgetSelected(final SelectionEvent event) {

          final String attrFrmat = ValueDialog.this.attribute.getFormat();
          CalendarDialog calDailog = new CalendarDialog();
          calDailog.addCalendarDialog(ValueDialog.this.form.getBody(), ValueDialog.this.valueEngText, attrFrmat);
        }
      });
      getFormToolkit().createLabel(this.form.getBody(), "Description (English): ");

      // ICDM-2007 (Parent task : ICDM-1774)
      TextBoxContentDisplay textBoxContentDisplay = new TextBoxContentDisplay(this.form.getBody(),
          SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, MAX_TEXT_BOX_SIZE, getGridDataForText());
      this.valueDescEngText = textBoxContentDisplay.getText();

      getFormToolkit().createLabel(this.form.getBody(), "");
      getFormToolkit().createLabel(this.form.getBody(), "");
      getFormToolkit().createLabel(this.form.getBody(), "");

      // ICDM-135
      if (ValueDialog.this.valueTypeText.getText().equalsIgnoreCase(AttributeValueType.DATE.getDisplayText())) {
        this.valueDescEngText.setEnabled(false);
        this.valueDescEngText.setText("-");
      }
      else {
        this.valueDescEngText.setEnabled(true);
      }
      // Add text modify listener to eng text.
      this.valueDescEngText.addModifyListener(event -> {
        // ICDM-112
        if (ValueDialog.this.valueTypeText.getText().equalsIgnoreCase(AttributeValueType.BOOLEAN.getDisplayText())) {
          Validator.getInstance().validateNDecorate(ValueDialog.this.comboDec, ValueDialog.this.txtValDescEngDec,
              ValueDialog.this.combo, ValueDialog.this.valueDescEngText, true);
        }
        else {
          // ICDM-112
          Validator.getInstance().validateNDecorate(ValueDialog.this.txtValDescEngDec,
              ValueDialog.this.valueDescEngText, true, false);
        }
        checkSaveBtnEnable();
      });

      // ICDM-112
      this.txtValDescEngDec = new ControlDecoration(this.valueDescEngText, SWT.LEFT | SWT.TOP);
      if (!valueType.equals(AttributeValueType.DATE.toString())) {
        this.decorators.showReqdDecoration(this.txtValDescEngDec, IUtilityConstants.MANDATORY_MSG);
      }

      getFormToolkit().createLabel(this.form.getBody(), "Description (German): ");

      // ICDM-2007 (Parent task : ICDM-1774)
      TextBoxContentDisplay textContentCounterValueDescGer = new TextBoxContentDisplay(this.form.getBody(),
          SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, MAX_TEXT_BOX_SIZE, getGridDataForText());
      this.valueDescGerText = textContentCounterValueDescGer.getText();

      // ICDM-135
      this.valueDescGerText.setEnabled(
          !ValueDialog.this.valueTypeText.getText().equalsIgnoreCase(AttributeValueType.DATE.getDisplayText()));
      createCharValArea();
      getFormToolkit().createLabel(this.form.getBody(), "");
    }

    // ICDM-1397
    getFormToolkit().createLabel(this.form.getBody(), "");
    getFormToolkit().createLabel(this.form.getBody(), "");

    getFormToolkit().createLabel(this.form.getBody(), "Comment");

    // ICDM-2007 (Parent task : ICDM-1774)
    TextBoxContentDisplay textContentCounterComment = new TextBoxContentDisplay(this.form.getBody(),
        SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, MAX_TEXT_BOX_SIZE, getGridDataForText());
    this.comment = textContentCounterComment.getText();
    this.comment.addModifyListener(evt -> checkSaveBtnEnable());
  }

  /**
   * This method defines grid data for Text
   *
   * @return GridData
   */
  private GridData getGridDataForText() {
    GridData gridDataText = new GridData();
    gridDataText.grabExcessHorizontalSpace = true;
    gridDataText.horizontalAlignment = GridData.FILL;
    gridDataText.verticalAlignment = GridData.FILL;
    gridDataText.verticalSpan = COMMENT_FIELD_VERTICAL_SPAN;
    gridDataText.heightHint = COMMENT_FIELD_HEIGHT_HINT;
    gridDataText.grabExcessVerticalSpace = true;
    return gridDataText;
  }

  /**
   * Create the new Char value Area Icdm-955
   */
  private void createCharValArea() {
    if ((null != this.attrClientBO) && this.attrClientBO.hasCharacteristic()) {
      getFormToolkit().createLabel(this.form.getBody(), "");
      getFormToolkit().createLabel(this.form.getBody(), "");
      getFormToolkit().createLabel(this.form.getBody(), "");
      getFormToolkit().createLabel(this.form.getBody(), ApicConstants.CHARVAL);
      createComboCharVal();
      getFormToolkit().createLabel(this.form.getBody(), "");
    }
  }


  /**
   * create Combo Char Value
   */
  private void createComboCharVal() {

    GridData gridData = getTextFieldGridData();
    this.comboCharVal = new Combo(this.form.getBody(), SWT.READ_ONLY);
    this.comboCharVal.setLayoutData(gridData);

    this.comboCharVal.add("<SELECT>");
    // get the Value class of the Attribute
    this.attrCharValues = new TreeSet<>(getCharacteristicVals(this.attribute.getCharacteristicId()).values());

    for (CharacteristicValue attrCharVal : this.attrCharValues) {
      this.comboCharVal.add(attrCharVal.getName());
    }
    // make the default selection to the <SELECT>
    this.comboCharVal.select(0);
    // Add selection listener
    this.comboCharVal.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        checkSaveBtnEnable();
      }
    });
  }

  /**
   * @param charID
   * @return
   */
  private Map<Long, CharacteristicValue> getCharacteristicVals(final Long charID) {
    try {
      CharacteristicValueServiceClient servClient = new CharacteristicValueServiceClient();
      return servClient.getValuesByCharacteristic(charID);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    return new HashMap<>();
  }


  /**
   * This method returns Attribute
   *
   * @return Attribute instance
   */
  protected Attribute getAttribute() {
    Attribute attrObj = null;
    if ((this.apicObject != null) && (this.apicObject instanceof IProjectAttribute)) {
      if (null != this.pidcAttrValDialog) {
        attrObj = this.pidcAttrValDialog.getPidcVersionBO().getPidcDataHandler().getAttributeMap()
            .get(((IProjectAttribute) this.apicObject).getAttrId());
      }
    }
    else if ((this.apicObject != null) && (this.apicObject instanceof Attribute)) {
      attrObj = (Attribute) this.apicObject;
    }
    return attrObj;
  }


  /**
   * Check for the save button enabling
   */
  // ICDM-112
  public void checkSaveBtnEnable() {
    // Perform validations for save button
    if (validateFields() && (this.saveBtn != null)) {
      this.saveBtn.setEnabled(true);
    }
    else if (this.saveBtn != null) {
      this.saveBtn.setEnabled(false);
    }
  }

  /**
   * This method validates text & combo fields
   *
   * @return boolean
   */
  // ICDM-112, //Task 237910
  protected boolean validateFields() {
    String value;
    if (this.valueEngText.isDisposed()) {
      // validate combo for boolean
      int index = this.combo.getSelectionIndex();
      value = this.combo.getItem(index);
    }
    else {
      value = this.valueEngText.getText().trim();

      String valueType = this.valueTypeText.getText().trim();
      // Value type empty
      if ("".equalsIgnoreCase(valueType) || valueType.equalsIgnoreCase(AttributeValueType.NUMBER.getDisplayText())) {
        this.valueEngText.setEnabled(true);
        if (!ApicUtil.isNumber(value)) {
          return false;
        }
      }
      // Value type date
      else if (valueType.equalsIgnoreCase(AttributeValueType.DATE.getDisplayText())) {
        this.valueEngText.setEnabled(true);
        this.valueEngText.setEditable(false);
      }
    }
    if ((null != this.versionSection) && CommonUtils.isEmptyString(this.versionSection.getVersionNameTxt().getText())) {
      return false;
    }
    return (!CommonUtils.isEmptyString(value.trim()) && !CommonUtils.isEmptyString(this.valueDescEngText.getText()) &&
        !checkIfAnyDecoratorSet());
  }

  /**
   * @return true if any of the decorator is visible
   */
  private boolean checkIfAnyDecoratorSet() {
    return checkComboDec() || this.txtValEngDec.isVisible() || this.txtValDescEngDec.isVisible() || checkVerSection();
  }

  /**
   * @return
   */
  private boolean checkVerSection() {
    return (this.versionSection != null) && (this.versionSection.getTxtVrsnNameDec() != null) &&
        this.versionSection.getTxtVrsnNameDec().isVisible();
  }

  /**
   * @return
   */
  private boolean checkComboDec() {
    return (this.comboDec != null) && this.comboDec.isVisible();
  }

  /**
   * The sets the selected attribute
   */
  // ICDM-108
  private void setSelectedAttr() {
    try {
      if (this.apicObject instanceof Attribute) {
        this.attribute = (Attribute) this.apicObject;
      }
      else if (this.apicObject instanceof PidcVersionAttribute) {
        Long attrId = ((PidcVersionAttribute) this.apicObject).getAttrId();
        this.attribute = new AttributeServiceClient().get(attrId);
      }
      else if (this.apicObject instanceof PidcVariantAttribute) {
        Long attrId = ((PidcVariantAttribute) this.apicObject).getAttrId();
        this.attribute = new AttributeServiceClient().get(attrId);
      } // ICDM-122
      else if (this.apicObject instanceof PidcSubVariantAttribute) {
        Long attrId = ((PidcSubVariantAttribute) this.apicObject).getAttrId();
        this.attribute = new AttributeServiceClient().get(attrId);
      }
      this.attrClientBO = new AttributeClientBO(this.attribute);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * @return the text field grid data
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
   * This method initializes combo for boolean
   */
  private void createCombo() {
    GridData gridData = getTextFieldGridData();
    this.combo = new Combo(this.form.getBody(), SWT.READ_ONLY);
    this.combo.setLayoutData(gridData);
    this.combo.add("FALSE");
    this.combo.add("TRUE");
    this.combo.select(0);
    // Add selection listener for the combo
    this.combo.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        // ICDM-112
        Validator.getInstance().validateNDecorate(ValueDialog.this.comboDec, ValueDialog.this.txtValDescEngDec,
            ValueDialog.this.combo, ValueDialog.this.valueDescEngText, true);
        checkSaveBtnEnable();
      }
    });
    this.comboDec = new ControlDecoration(this.combo, SWT.LEFT | SWT.TOP);
    this.decorators.showReqdDecoration(this.comboDec, IUtilityConstants.MANDATORY_MSG);
  }


  /**
   * This method invokes on ok button pressed
   */
  protected void onOkPressed() {
    this.changeComment = CommonUtils.checkNull(this.comment, "", this.comment.getText());

    // ICDM-108
    String valType = CommonUtils.checkNull(this.attribute, null, this.attribute.getValueType());

    if (valType != null) {
      // Icdm-830 Data Model changes for New Column Clearing status
      this.clearingStatus = CLEARING_STATUS.CLEARED.getDBText();
      // Set the attr value for boolean type
      if (valType.equals(AttributeValueType.BOOLEAN.toString())) {
        int index = this.combo.getSelectionIndex();
        this.attrValue = this.combo.getItem(index);

        this.descEng = this.valueDescEngText.getText();
        this.descGer = this.valueDescGerText.getText();
      }
      // Set the attr value for Text type
      else if (valType.equals(AttributeValueType.TEXT.toString())) {
        this.attrValue = this.valueEngText.getText().trim();
        if (this.valueGerText != null) {
          this.attrValueGer = this.valueGerText.getText().trim();
        }
        this.descEng = this.valueDescEngText.getText().trim();
        this.descGer = this.valueDescGerText.getText().trim();

      }
      // // Set the attr value for Number type
      else if (valType.equals(AttributeValueType.NUMBER.toString())) {
        this.attrValue = this.valueEngText.getText().trim();
        this.attrValue = this.attrValue.replaceAll(",", ".").trim();
        this.descEng = this.valueDescEngText.getText().trim();
        this.descGer = this.valueDescGerText.getText();
      }
      // Defualt Text
      else {
        this.attrValue = this.valueEngText.getText();
        this.descEng = this.valueDescEngText.getText().trim();
        this.descGer = this.valueDescGerText.getText();
      }
    }
    // ICDM-1267
    if (this.attrValue != null) {
      this.attrValue = this.attrValue.trim();
    }
    if (this.attrValueGer != null) {
      this.attrValueGer = this.attrValueGer.trim();
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.DIALOG_TRIM);
  }


  /**
   * Method to set the updated Char Val name
   *
   * @param charValStr charValStr
   * @return the Char Val Object for the Char Val Name
   */
  protected CharacteristicValue setUpdatedCharVal(final String charValStr) {
    CharacteristicValue attrCharValUpd = null;
    for (CharacteristicValue attrCharVal : this.attrCharValues) {
      if (attrCharVal.getName().equals(charValStr)) {
        attrCharValUpd = attrCharVal;
        break;
      }
    }
    return attrCharValUpd;
  }

  /**
   *
   */
  private void modifyClipBoard() {
    Clipboard clipboard = new Clipboard(Display.getCurrent());
    TextTransfer textTransfer = TextTransfer.getInstance();
    String textContents = (String) clipboard.getContents(textTransfer);
    if (!this.valueTypeText.getText().equals(AttributeValueType.HYPERLINK.toString())) {
      setValueNDescText(textContents);
    }
    else {
      pasteHyperlink(clipboard, textContents);
    }
  }

  /**
   * @param clipboard
   * @param textContents
   */
  private void pasteHyperlink(final Clipboard clipboard, final String textContents) {
    HTMLTransfer htmlTransfer = HTMLTransfer.getInstance();
    // if the contents in clipboard is available in html format,this was implemented to get correct vcdm link
    String htmlContents = (String) clipboard.getContents(htmlTransfer);
    String hrefContent = null;
    Pattern pattern = Pattern.compile("href=\'([^\"]*)\'", Pattern.DOTALL);

    if (htmlContents != null) {
      Matcher matcher = pattern.matcher(htmlContents);
      if (matcher.find()) {
        // Get all groups for this match
        for (int i = 0; i <= matcher.groupCount(); i++) {
          String groupStr = matcher.group(i);
          hrefContent = groupStr;
        }
      }
    }
    if ((hrefContent != null) && !hrefContent.isEmpty()) {
      ValueDialog.this.valueEngText.setText(hrefContent);
      ValueDialog.this.valueDescEngText.setText(textContents);
    }
    else {
      if (CommonUtils.isValidHyperlinkFormat(textContents)) {
        setValueNDescText(textContents);
      }
      // show error decoration if not a valid link
      else {
        this.decorators.showErrDecoration(ValueDialog.this.txtValEngDec, IUtilityConstants.EMPTY_STRING, true);
      }
    }
  }

  /**
   * @param textContents
   */
  private void setValueNDescText(final String textContents) {
    ValueDialog.this.valueEngText.setText(textContents);
    ValueDialog.this.valueDescEngText.setText("");
  }


  /**
   * @return the includeVersionSection
   */
  public boolean isIncludeVersionSection() {
    return this.includeVersionSection;
  }


  /**
   * @param includeVersionSection the includeVersionSection to set
   */
  public void setIncludeVersionSection(final boolean includeVersionSection) {
    this.includeVersionSection = includeVersionSection;
  }


  /**
   * @return the versionSection
   */
  public PIDCVersionDetailsSection getVersionSection() {
    return this.versionSection;
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
   * @return the saveBtn
   */
  public Button getSaveBtn() {
    return this.saveBtn;
  }


  /**
   * @param valueEngText the valueEngText to set
   */
  public void setValueEngText(final String valueEngText) {
    this.valueEng = valueEngText;
  }


  /**
   * @param valueGerText the valueGerText to set
   */
  public void setValueGerText(final String valueGerText) {
    this.valueGer = valueGerText;
  }


  /**
   * @return the valueEngText
   */
  public String getValueEngText() {
    return this.valueEng;
  }


  /**
   * @return the valueGerText
   */
  public String getValueGerText() {
    return this.valueGer;
  }


  /**
   * @return the descripEng
   */
  public String getDescripEng() {
    return this.descripEng;
  }


  /**
   * @param descripEng the descripEng to set
   */
  public void setDescripEng(final String descripEng) {
    this.descripEng = descripEng;
  }
}
