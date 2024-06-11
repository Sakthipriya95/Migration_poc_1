/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.nebula.jface.gridviewer.CheckEditingSupport;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode.PIDC_TREE_NODE_TYPE;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.bo.qnaire.QnaireRespVarRespWpLinkBo;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcQnaireInfo;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.DefineQnaireRespInputData;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespUpdationModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespVarRespWpLink;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVariant;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;
import com.bosch.caltool.icdm.model.wp.WorkPkg;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwQnaireRespVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwQnaireResponseServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.text.TextUtil;

/**
 * @author dmr1cob
 */
public class DefineQuestionnaireDialog extends AbstractDialog {

  /**
   *
   */
  private static final String REVIEW_QUESTIONNAIRES = "REVIEW_QUESTIONNAIRES";

  /**
   * form tool kit
   */
  private FormToolkit formToolkit;

  /**
   * Composite composite
   */
  private Composite composite;

  /**
   * Instance of wp resp name textbox
   */
  private Text wpRespName;


  /**
   * Define table colums
   */
  private GridTableViewer qNaireTableViewer;

  /**
   * Define table colums
   */
  private GridTableViewer qNaireLinkTableViewer;

  /**
   * Define table colums For WP/RESP Link
   */
  private GridTableViewer qNaireWpLinkTableViewer;


  /**
   * Delete qnaire button
   */
  private Button delQnaireButton;

  /**
   * Filter text instances
   */
  private Text filterTxt;

  /**
   * Filter text instances
   */
  private Text wpFilterTxt;

  /**
   * Pidc Variant of selected node
   */
  private PidcVariant pidcVariant;

  /**
   * A2l Responsibility of selected node
   */
  private A2lResponsibility a2lResp;

  /**
   * A2l Work package of selected node
   */
  private A2lWorkPackage a2lWp;

  /**
   * Pidc version of selected node
   */
  private PidcVersion pidcVersion;

  /**
   * Selected pidc tree node object
   */
  private final PidcTreeNode pidcTreeNode;

  /**
   * Qnaire info for the selected pidc tree node
   */
  private PidcQnaireInfo qnaireInfo;

  /**
   * key - Wp Resp name Value - Qnaire resp id set
   */
  private final Map<String, Set<Long>> qnaireRespMap = new HashMap<>();

  /**
   * key - Wp resp name Value - responsibility id
   */
  private final Map<String, Long> selRespIdMap = new HashMap<>();

  /**
   * key - Wp resp name Value - workpackage id
   */
  private final Map<String, Long> selWpIdMap = new HashMap<>();

  /**
   * All qnaire response set in qnaire response table
   */
  private SortedSet<RvwQnaireResponse> allQnaireResponseSet = new TreeSet<>();

  /**
   * Already available qnaire response set
   */
  private final SortedSet<RvwQnaireResponse> oldQnaireResponseSet = new TreeSet<>();

  /**
   * Already available qnaire response set
   */
  private final SortedSet<RvwQnaireResponse> qnaireResponseUpdateSet = new TreeSet<>();

  /**
   * Newly added qnaire response set
   */
  private SortedSet<RvwQnaireResponse> selQnaireResp;

  /**
   * Newly selected work package
   */
  private final SortedSet<WorkPkg> workPkgSet = new TreeSet<>();

  /**
   * div id for selected pidc version
   */
  private Long divId;

  /**
   * key - {@link RvwQnaireResponse} object Value - Corresponding {@link WorkPkg} obj
   */
  private final Map<RvwQnaireResponse, WorkPkg> qnaireRespWpMap = new HashMap<>();

  /**
   * Selected wp resp name in combo
   */
  private String selWpRespName;

  /**
   * Add button to add work packages
   */
  private Button addQnaireButton;

  /*
   * All variants in the selected pidc version
   */
  private Map<Long, PidcVariant> allVariantMap = new HashMap<>();

  /*
   * Variant linking model
   */
  private final SortedSet<QnaireRespVarRespWpLink> qnaireRespVarLinkSet = new TreeSet<>();

  private final Set<QnaireRespVarRespWpLink> modifiedQnaireRespVarLinkSet = new HashSet<>();

  /*
   * WP/Resp linking model
   */
  private final SortedSet<QnaireRespVarRespWpLink> qnaireRespWpLinkSet = new TreeSet<>();

  private GridViewerColumn linkColumn;

  private GridViewerColumn detailCol;

  private GridViewerColumn wpLinkColumn;

  private GridViewerColumn wpDetailCol;

  /*
   * Qnaire resp variant map Key - Rvw qnaire resp id Value - Qnaire resp variant
   */
  private Map<Long, List<RvwQnaireRespVariant>> qnaireRespVarMap = new HashMap<>();

  /**
   * Width of dialog
   */
  private static final int WIDTH_OF_DIALOG = 800;
  /**
   * Height of dialog
   */
  private static final int HEIGHT_OF_DIALOG = 1100;

  private static final String EDIT_WARNING_TEXT = "Please use the browse button to modify the text";

  private int frameX;
  private int frameY;

  /**
   * Selected qnaire resp object in qnaire table
   */
  protected RvwQnaireResponse selectedQnaireResp;

  /**
   * Qnaire var link filter
   */
  private VariantLinkingFilter varLinkFilter;

  /**
   * Qnaire WP/Resp link filter
   */
  private WpLinkingFilter wpLinkFilter;

  private boolean isAccessAvailable;

  private Button okPressed;

  GridViewerColumn delFlagViewer;

  private boolean hasMultipleVariants;

  private boolean hasSingleVariantMultipleWp;

  /**
   * @param parentShell parent shell
   * @param pidcTreeNode selected pidc tree node
   */
  public DefineQuestionnaireDialog(final Shell parentShell, final PidcTreeNode pidcTreeNode) {
    super(parentShell);
    this.pidcTreeNode = pidcTreeNode;
    fillInputData();
  }

  /**
   * Fill {@link PidcVariant}, {@link PidcVersion} and {@link PidcQnaireInfo} information
   */
  private void fillInputData() {
    this.pidcVariant = this.pidcTreeNode.getPidcVariant();
    this.pidcVersion = this.pidcTreeNode.getPidcVersion();
    if (this.pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_QNAIRE_WP_NODE)) {
      this.a2lResp = this.pidcTreeNode.getA2lResponsibility();
      this.a2lWp = this.pidcTreeNode.getA2lWorkpackage();
    }
    // If User Right clicks on the Qnaire Response and chooses Add/Edit Questionnaires,
    // Get Resp and WP from Parent Node because if it is linked Qnaire, the Qnaire Resp contains original Resp and Wp
    else if (this.pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.QNAIRE_RESP_NODE)) {
      this.a2lResp = this.pidcTreeNode.getParentNode().getA2lResponsibility();
      this.a2lWp = this.pidcTreeNode.getParentNode().getA2lWorkpackage();
    }
    try {
      // Single service call to get input data to improve performance
      DefineQnaireRespInputData defineQnaireRespInputData =
          new RvwQnaireRespVariantServiceClient().getDefineQnaireRespInputData(this.pidcVersion.getId());
      this.allVariantMap = defineQnaireRespInputData.getAllVariantMap();
      this.qnaireRespVarMap = defineQnaireRespInputData.getQnaireRespVarMap();
      this.qnaireInfo = defineQnaireRespInputData.getQnaireInfo();
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isResizable() {
    return true;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Define Questionnaires");
    this.frameX = newShell.getSize().x - newShell.getClientArea().width;
    this.frameY = newShell.getSize().y - newShell.getClientArea().height;
    newShell.setSize(WIDTH_OF_DIALOG + this.frameX, HEIGHT_OF_DIALOG + this.frameY);
    super.configureShell(newShell);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Point getInitialSize() {
    return new Point(WIDTH_OF_DIALOG + this.frameX, HEIGHT_OF_DIALOG + this.frameY);
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


  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle("Define Questionnaires");

    return contents;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.composite = getFormToolkit().createComposite(parent);
    this.composite.setLayout(new GridLayout());
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
    createQNaireGroup();
    // Disable Qnaire link table when only one variant is present
    this.hasMultipleVariants = CommonUtils.isNotEmpty(this.allVariantMap) &&
        ((this.allVariantMap.size() > 1) || (this.pidcVariant.getId().equals(ApicConstants.NO_VARIANT_ID)));

    if (this.hasMultipleVariants) {
      createQNaireLinkGroup();
    }
    // Condition to check if the Review Qnaires has single variant but multiple WP or Resps
    else if (CommonUtils.isNotEmpty(this.qnaireInfo.getA2lWpMap()) && (this.qnaireInfo.getA2lWpMap().size() > 1)) {
      this.hasSingleVariantMultipleWp = true;
      createQNaireLinkGroup();
    }
    return this.composite;
  }


  /**
   * Create group
   */
  private void createQNaireGroup() {
    final Group qNaireGroup = new Group(this.composite, SWT.NONE);
    qNaireGroup.setLayout(new GridLayout());
    GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    qNaireGroup.setLayoutData(gridData);
    qNaireGroup.setText("Assign Questionnaires to your Work Packages");
    StringBuilder labelName = new StringBuilder();
    labelName.append("Par2Wp Work Packages and Responsibility");
    if (!this.pidcVariant.getId().equals(ApicConstants.NO_VARIANT_ID)) {
      labelName.append(" for Variant - " + this.pidcVariant.getName());
    }
    LabelUtil.getInstance().createLabel(createRowComposite(qNaireGroup), labelName.toString());

    final Composite firstRowComposite = createRowComposite(qNaireGroup);
    createWpRespControl(firstRowComposite);
    gridData.heightHint = 250;

    final Composite tableComp = new Composite(qNaireGroup, SWT.NONE);
    tableComp.setLayout(new GridLayout(2, false));
    tableComp.setLayoutData(GridDataUtil.getInstance().getGridData());

    createQNaireTable(tableComp);

    // composite for aligining the selection and delete buttons
    final Composite qnaireBtComp = new Composite(tableComp, SWT.NONE);
    final GridLayout layoutQnaireComp = new GridLayout();
    layoutQnaireComp.numColumns = 1;
    layoutQnaireComp.makeColumnsEqualWidth = false;
    layoutQnaireComp.marginWidth = 0;
    layoutQnaireComp.marginTop = 20;
    qnaireBtComp.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
    qnaireBtComp.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
    qnaireBtComp.setLayout(layoutQnaireComp);

    createAddButton(qnaireBtComp);
    createDeleteButton(qnaireBtComp);
  }

  /**
   * @param descGroup composite
   * @return composite
   */
  private Composite createRowComposite(final Composite comp) {
    final Composite firstRowComposite = new Composite(comp, SWT.NONE);
    firstRowComposite.setLayout(new GridLayout(3, false));
    firstRowComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    return firstRowComposite;
  }

  /**
   * create wp resp combo viewer
   *
   * @param firstRowComposite group composite
   */
  private void createWpRespControl(final Composite firstRowComposite) {
    new Label(firstRowComposite, SWT.NONE).setText("Work Package and Responsibility : ");
    this.wpRespName = new Text(firstRowComposite, SWT.BORDER);
    this.wpRespName.addKeyListener(new KeyListener() {

      @Override
      public void keyReleased(final KeyEvent keyEvent) {
        if (!(((keyEvent.stateMask & SWT.CTRL) == SWT.CTRL) && (keyEvent.keyCode == 262144))) {
          MessageDialogUtils.getWarningMessageDialog("Select Wp Resp", EDIT_WARNING_TEXT);
        }

      }

      @Override
      public void keyPressed(final KeyEvent keyEvent) {
        // Implementation in KeyReleased() method is sufficient
      }
    });

    final GridData wpRespData = new GridData(SWT.FILL, SWT.NONE, true, false);
    wpRespData.widthHint = new PixelConverter(this.wpRespName).convertWidthInCharsToPixels(25);
    this.wpRespName.setLayoutData(wpRespData);

    SortedSet<String> wpRespList = fillWpRespCombo();

    Button wpRespBrowse = new Button(firstRowComposite, SWT.PUSH);
    wpRespBrowse.setImage(ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON));
    wpRespBrowse.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
    wpRespBrowse.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        WpRespSelectionDialog wpRespSelectionDialog =
            new WpRespSelectionDialog(Display.getCurrent().getActiveShell(), wpRespList);
        wpRespSelectionDialog.open();
        if (CommonUtils.isNotNull(wpRespSelectionDialog.getSelectedWpResp())) {
          DefineQuestionnaireDialog.this.selWpRespName = wpRespSelectionDialog.getSelectedWpResp();
          DefineQuestionnaireDialog.this.wpRespName.setText(wpRespSelectionDialog.getSelectedWpResp());
        }
      }
    });
    addWpRespTextModifyListener();
  }

  /**
   * @param pidcTreeNode
   * @param tempQnaireRespActions
   */
  private boolean qnaireAccessValidation() {
    boolean accessFlag = false;
    Long pidcId = this.pidcTreeNode.getPidcVersion().getPidcId();
    Long a2lRespId = this.selRespIdMap.get(this.selWpRespName);

    try {
      accessFlag = new RvwQnaireResponseServiceClient().validateQnaireAccess(pidcId, a2lRespId);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    return accessFlag;
  }


  /**
   * create selection listener for the wp resp combo. It will get triggered when changing the value in combo viewer
   */
  private void addWpRespTextModifyListener() {
    this.wpRespName.addModifyListener(event -> {
      DefineQuestionnaireDialog.this.allQnaireResponseSet.clear();
      if (CommonUtils.isNotNull(DefineQuestionnaireDialog.this.selWpRespName)) {
        Set<Long> qnaireRespSet =
            DefineQuestionnaireDialog.this.qnaireRespMap.get(DefineQuestionnaireDialog.this.selWpRespName);
        qnaireRespSet.forEach(qnaireRespId -> {
          // QnaireRespId will be null if there is simplified Qnaire
          if (CommonUtils.isNotNull(qnaireRespId) &&
              CommonUtils.isNotEqual(qnaireRespId, ApicConstants.SIMP_QUES_RESP_ID)) {
            DefineQuestionnaireDialog.this.allQnaireResponseSet
                .add(DefineQuestionnaireDialog.this.qnaireInfo.getRvwQnaireRespMap().get(qnaireRespId));
            DefineQuestionnaireDialog.this.oldQnaireResponseSet
                .add(DefineQuestionnaireDialog.this.qnaireInfo.getRvwQnaireRespMap().get(qnaireRespId));
          }
        });
        // Calling the method to Reload the Wp/Resp Link Table on Changing the Work Package - Resp
        // 1st calling 'fillQnaireRespWpLinkSet' method which will reset a2lResp and a2lWp
        fillQnaireRespWpLinkSet();
        fillQnaireRespVarLinkSet();
        if (null != DefineQuestionnaireDialog.this.qNaireTableViewer) {
          DefineQuestionnaireDialog.this.qNaireTableViewer
              .setInput(DefineQuestionnaireDialog.this.allQnaireResponseSet);
        }

        DefineQuestionnaireDialog.this.isAccessAvailable = qnaireAccessValidation();

        if (!DefineQuestionnaireDialog.this.isAccessAvailable) {
          CDMLogger.getInstance().errorDialog("You don't have access for the selected workpackage and responsible \n" +
              DefineQuestionnaireDialog.this.selWpRespName, com.bosch.caltool.cdr.ui.Activator.PLUGIN_ID);
        }
        enableQnaireTabViewerUiWidget();
      }
      if (null != DefineQuestionnaireDialog.this.qNaireLinkTableViewer) {
        DefineQuestionnaireDialog.this.linkColumn.getColumn().setVisible(false);
        DefineQuestionnaireDialog.this.detailCol.getColumn().setVisible(false);
      }
    });
  }

  /**
   *
   */
  private void enableQnaireTabViewerUiWidget() {
    if ((null != DefineQuestionnaireDialog.this.addQnaireButton) &&
        (null != DefineQuestionnaireDialog.this.okPressed) && (null != DefineQuestionnaireDialog.this.delFlagViewer)) {
      DefineQuestionnaireDialog.this.addQnaireButton.setEnabled(DefineQuestionnaireDialog.this.isAccessAvailable);
      DefineQuestionnaireDialog.this.okPressed.setEnabled(validate());
      DefineQuestionnaireDialog.this.delFlagViewer.getColumn()
          .setCheckable(DefineQuestionnaireDialog.this.isAccessAvailable);
    }
    if ((null != DefineQuestionnaireDialog.this.linkColumn)) {
      DefineQuestionnaireDialog.this.linkColumn.getColumn()
          .setCheckable(DefineQuestionnaireDialog.this.isAccessAvailable);
    }
  }

  /**
   * @return concatenated wp resp name
   */
  private SortedSet<String> fillWpRespCombo() {
    Map<Long, Map<Long, Set<Long>>> respWpMap = this.qnaireInfo.getVarRespWpQniareMap().get(getPidcVariant().getId());
    SortedSet<String> wpRespSet = new TreeSet();
    if (!respWpMap.isEmpty()) {
      respWpMap.entrySet().forEach(respWpEntry -> {
        String respName = this.qnaireInfo.getA2lRespMap().get(respWpEntry.getKey()).getName();
        Map<Long, Set<Long>> wpMap = respWpEntry.getValue();
        if (!wpMap.isEmpty()) {
          wpMap.entrySet().forEach(wpEntry -> {
            String wpName = this.qnaireInfo.getA2lWpMap().get(wpEntry.getKey()).getName();
            String workPackageRespName = wpName + ", " + respName;
            wpRespSet.add(workPackageRespName);
            this.qnaireRespMap.put(workPackageRespName, wpEntry.getValue());
            this.selRespIdMap.put(workPackageRespName, respWpEntry.getKey());
            this.selWpIdMap.put(workPackageRespName, wpEntry.getKey());
          });
        }
      });
    }
    return wpRespSet;
  }

  /**
   * Create qnaire resp table to show the qnaire response
   */
  private void createQNaireTable(final Composite comp) {
    this.qNaireTableViewer = GridTableViewerUtil.getInstance().createGridTableViewer(comp,
        SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, GridDataUtil.getInstance().getGridData());
    this.qNaireTableViewer.setContentProvider(new ArrayContentProvider());
    this.qNaireTableViewer.getGrid().setLinesVisible(true);
    this.qNaireTableViewer.getGrid().setHeaderVisible(true);
    createQnaireColumn();
    createDeleteFlagColumn();

    wpRespSetSelection();
    addQnaireTableSelectionListener();

    addQnaireTableSelectionListenerForWpResp();

  }

  /**
   * when define questionnaire dialog is opened from workpackage node or qnaire response node pre set the value in combo
   * viewer with corresponding wp resp combination
   */
  private void wpRespSetSelection() {
    if (this.pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.QNAIRE_RESP_NODE) ||
        this.pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_QNAIRE_RESP_NODE)) {
      PidcTreeNode wpNode = this.pidcTreeNode.getParentNode();
      PidcTreeNode respNode = wpNode.getParentNode();
      String wpRespNameSel = wpNode.getName() + ", " + respNode.getName();
      this.selWpRespName = wpRespNameSel;
      this.wpRespName.setText(wpRespNameSel);
    }
    else if (this.pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_QNAIRE_WP_NODE) ||
        this.pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_WP_NODE)) {
      PidcTreeNode respNode = this.pidcTreeNode.getParentNode();
      String wpRespNameSel = this.pidcTreeNode.getName() + ", " + respNode.getName();
      this.selWpRespName = wpRespNameSel;
      this.wpRespName.setText(wpRespNameSel);
    }
  }

  /**
   * Create selection listener for table viewer
   */
  private void addQnaireTableSelectionListener() {

    this.qNaireTableViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        DefineQuestionnaireDialog.this.modifiedQnaireRespVarLinkSet.clear();
        IStructuredSelection selection =
            (IStructuredSelection) DefineQuestionnaireDialog.this.qNaireTableViewer.getSelection();
        if (!selection.isEmpty()) {
          DefineQuestionnaireDialog.this.selectedQnaireResp = (RvwQnaireResponse) selection.getFirstElement();
          DefineQuestionnaireDialog.this.selQnaireResp = new TreeSet<>();
          if (null == DefineQuestionnaireDialog.this.selectedQnaireResp.getId()) {
            DefineQuestionnaireDialog.this.selQnaireResp.add(DefineQuestionnaireDialog.this.selectedQnaireResp);
            DefineQuestionnaireDialog.this.delQnaireButton.setEnabled(true);
          }
          else {
            DefineQuestionnaireDialog.this.delQnaireButton.setEnabled(false);
          }
        }

        boolean isVisible = false;
        if ((null != DefineQuestionnaireDialog.this.selectedQnaireResp) &&
            (null != DefineQuestionnaireDialog.this.selectedQnaireResp.getId()) &&
            !DefineQuestionnaireDialog.this.selectedQnaireResp.isDeletedFlag()) {
          QnaireRespVarRespWpLinkBo variantLinkBo = new QnaireRespVarRespWpLinkBo(
              DefineQuestionnaireDialog.this.qnaireInfo, DefineQuestionnaireDialog.this.selectedQnaireResp,
              DefineQuestionnaireDialog.this.qnaireRespVarLinkSet, getUserInfoMessages());
          variantLinkBo.updateDetails(false,
              isQnaireRespAlreadylinked(DefineQuestionnaireDialog.this.selectedQnaireResp));
          if (DefineQuestionnaireDialog.this.hasMultipleVariants) {
            DefineQuestionnaireDialog.this.qNaireLinkTableViewer
                .setInput(DefineQuestionnaireDialog.this.qnaireRespVarLinkSet);
          }
          isVisible = true;
        }
        if (DefineQuestionnaireDialog.this.hasMultipleVariants) {
          DefineQuestionnaireDialog.this.linkColumn.getColumn().setVisible(isVisible);
          DefineQuestionnaireDialog.this.detailCol.getColumn().setVisible(isVisible);
        }
        DefineQuestionnaireDialog.this.okPressed.setEnabled(validate());
      }


    });
  }

  private Map<String, String> getUserInfoMessages() {
    Map<String, String> messagesMap = new HashMap<>();
    try {
      CommonDataBO commomDataBo = new CommonDataBO();
      messagesMap.put("QNAIRE_RESP_ALREADY_LINKED",
          commomDataBo.getMessage(REVIEW_QUESTIONNAIRES, "QNAIRE_RESP_ALREADY_LINKED"));
      messagesMap.put("NO_WP_IN_VARIANT", commomDataBo.getMessage(REVIEW_QUESTIONNAIRES, "NO_WP_IN_VARIANT"));
      messagesMap.put("UNLINK_PRIMARY_LINK_NOT_POSSIBLE",
          commomDataBo.getMessage(REVIEW_QUESTIONNAIRES, "UNLINK_PRIMARY_LINK_NOT_POSSIBLE"));
      messagesMap.put("NO_DEFAULT_WP_LINK", commomDataBo.getMessage(REVIEW_QUESTIONNAIRES, "NO_DEFAULT_WP_LINK"));
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return messagesMap;
  }

  /**
   * Create selection listener for table viewer
   */
  private void addQnaireTableSelectionListenerForWpResp() {
    this.qNaireTableViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        DefineQuestionnaireDialog.this.modifiedQnaireRespVarLinkSet.clear();
        IStructuredSelection selection =
            (IStructuredSelection) DefineQuestionnaireDialog.this.qNaireTableViewer.getSelection();
        if (!selection.isEmpty()) {
          DefineQuestionnaireDialog.this.selectedQnaireResp = (RvwQnaireResponse) selection.getFirstElement();
          DefineQuestionnaireDialog.this.selQnaireResp = new TreeSet<>();
          if (null == DefineQuestionnaireDialog.this.selectedQnaireResp.getId()) {
            DefineQuestionnaireDialog.this.selQnaireResp.add(DefineQuestionnaireDialog.this.selectedQnaireResp);
            DefineQuestionnaireDialog.this.delQnaireButton.setEnabled(true);
          }
          else {
            DefineQuestionnaireDialog.this.delQnaireButton.setEnabled(false);
          }
        }

        boolean isVisible = false;
        if ((null != DefineQuestionnaireDialog.this.selectedQnaireResp) &&
            (null != DefineQuestionnaireDialog.this.selectedQnaireResp.getId()) &&
            !DefineQuestionnaireDialog.this.selectedQnaireResp.isDeletedFlag()) {
          QnaireRespVarRespWpLinkBo variantLinkBo = new QnaireRespVarRespWpLinkBo(
              DefineQuestionnaireDialog.this.qnaireInfo, DefineQuestionnaireDialog.this.selectedQnaireResp,
              DefineQuestionnaireDialog.this.qnaireRespWpLinkSet, getUserInfoMessages());
          variantLinkBo.updateDetails(true,
              isQnaireRespAlreadylinked(DefineQuestionnaireDialog.this.selectedQnaireResp));

          DefineQuestionnaireDialog.this.qNaireWpLinkTableViewer
              .setInput(DefineQuestionnaireDialog.this.qnaireRespWpLinkSet);

          isVisible = true;
        }

        DefineQuestionnaireDialog.this.wpLinkColumn.getColumn().setVisible(isVisible);
        DefineQuestionnaireDialog.this.wpDetailCol.getColumn().setVisible(isVisible);

        DefineQuestionnaireDialog.this.okPressed.setEnabled(validate());
      }


    });
  }

  private boolean isQnaireRespAlreadylinked(final RvwQnaireResponse rvwQnaireResponse) {
    return CommonUtils.isNotEqual(rvwQnaireResponse.getVariantId(), this.pidcVariant.getId()) ||
        CommonUtils.isNotEqual(rvwQnaireResponse.getA2lRespId(), this.a2lResp.getId()) ||
        CommonUtils.isNotEqual(rvwQnaireResponse.getA2lWpId(), this.a2lWp.getId());
  }


  /**
   * column creation for qnaire table
   */
  private void createQnaireColumn() {
    final GridViewerColumn qNaireCol = new GridViewerColumn(this.qNaireTableViewer, SWT.NONE);
    qNaireCol.getColumn().setText("Questionnaire");
    qNaireCol.getColumn().setWidth(400);
    setQnaireColLabelProvider(qNaireCol);
  }

  /**
   * @param qNaireCol
   */
  private void setQnaireColLabelProvider(final GridViewerColumn qNaireCol) {
    qNaireCol.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        return qNaireColGetText(element);
      }

      @Override
      public Color getForeground(final Object element) {
        return qNaireColGetForeground(element);
      }

      /**
       * @param element
       * @return
       */
      private Color qNaireColGetForeground(final Object element) {
        if (element instanceof RvwQnaireResponse) {
          RvwQnaireResponse qnaireResp = (RvwQnaireResponse) element;
          if (null == qnaireResp.getId()) {
            return getqNaireTableViewer().getGrid().getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE);
          }
          if ((null != qnaireResp.getId()) && qnaireResp.isDeletedFlag()) {
            return getqNaireTableViewer().getGrid().getDisplay().getSystemColor(SWT.COLOR_RED);
          }
          if ((null != qnaireResp.getId()) && (qnaireResp.getName().startsWith(ApicConstants.GENERAL_QUESTIONS) ||
              qnaireResp.getName().startsWith(ApicConstants.OBD_GENERAL_QUESTIONS))) {
            return getqNaireTableViewer().getGrid().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
          }
        }
        return null;
      }
    });
  }

  /**
   * Delete flag column creation
   */
  private void createDeleteFlagColumn() {
    GridColumn delFlagCol = new GridColumn(this.qNaireTableViewer.getGrid(), SWT.CHECK | SWT.CENTER);
    delFlagCol.setWidth(50);
    delFlagCol.setSummary(false);
    this.delFlagViewer = new GridViewerColumn(this.qNaireTableViewer, delFlagCol);
    this.delFlagViewer.getColumn().setText("Deleted");
    this.delFlagViewer.getColumn().setWidth(100);
    setDeleteFlagColLabelProvider(this.delFlagViewer);

    this.delFlagViewer.setEditingSupport(new CheckEditingSupport(this.delFlagViewer.getViewer()) {

      @Override
      public void setValue(final Object arg0, final Object arg1) {
        RvwQnaireResponse qnaireResp = (RvwQnaireResponse) arg0;
        deleteQuesRespInSet((Boolean) arg1, qnaireResp);
        DefineQuestionnaireDialog.this.okPressed.setEnabled(validate());
      }
    });
  }

  /**
   * @param deletedFlag
   * @param qnaireResp
   */
  private void deleteQuesRespInSet(final Boolean deletedFlag, final RvwQnaireResponse qnaireResp) {
    qnaireResp.setDeletedFlag(deletedFlag);
    DefineQuestionnaireDialog.this.oldQnaireResponseSet.add(qnaireResp);
    // Remove if already updated the delete flag
    if (!DefineQuestionnaireDialog.this.qnaireResponseUpdateSet.remove(qnaireResp)) {
      DefineQuestionnaireDialog.this.qnaireResponseUpdateSet.add(qnaireResp);
    }
  }

  /**
   * @param delFlagViewer
   */
  private void setDeleteFlagColLabelProvider(final GridViewerColumn delFlagViewer) {
    delFlagViewer.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public void update(final ViewerCell cell) {
        final Object element = cell.getElement();
        if (element instanceof RvwQnaireResponse) {
          RvwQnaireResponse qnaireResp = (RvwQnaireResponse) element;
          final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
          gridItem.setChecked(cell.getVisualIndex(), qnaireResp.isDeletedFlag());
          boolean isGenearlQuestion =
              (null != qnaireResp.getId()) && (!qnaireResp.getName().startsWith(ApicConstants.OBD_GENERAL_QUESTIONS) ||
                  !qnaireResp.getName().startsWith(ApicConstants.GENERAL_QUESTIONS));
          gridItem.setCheckable(cell.getVisualIndex(), isGenearlQuestion);
          gridItem.setGrayed(cell.getVisualIndex(), !isGenearlQuestion);
        }
      }


      @Override
      public Color getForeground(final Object element) {
        if (element instanceof RvwQnaireResponse) {
          RvwQnaireResponse qnaireResp = (RvwQnaireResponse) element;
          if (null == qnaireResp.getId()) {
            return getqNaireTableViewer().getGrid().getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE);
          }
          if ((null != qnaireResp.getId()) && qnaireResp.isDeletedFlag()) {
            return getqNaireTableViewer().getGrid().getDisplay().getSystemColor(SWT.COLOR_RED);
          }
        }
        return null;
      }
    });
  }

  /**
   * Add work package button creation
   */
  private void createAddButton(final Composite comp) {
    this.addQnaireButton = new Button(comp, SWT.PUSH);
    this.addQnaireButton.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.ADD_16X16));
    this.addQnaireButton.setToolTipText("Add Questionnaire");
    this.addQnaireButton.setEnabled(CommonUtils.isNotNull(this.selWpRespName));
    this.addQnaireButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        QuestionnaireNameSelDialog dialog = new QuestionnaireNameSelDialog(Display.getCurrent().getActiveShell(), null,
            null, DefineQuestionnaireDialog.this);
        dialog.open();
        DefineQuestionnaireDialog.this.okPressed.setEnabled(validate());
      }
    });
    this.addQnaireButton.setToolTipText("Add new questionnaire to create questionnaire response");
  }

  /**
   * Remove newly added questionnaire response button creation
   */
  private void createDeleteButton(final Composite comp) {
    final Image deleteButtonImage = ImageManager.INSTANCE.getRegisteredImage(ImageKeys.DELETE_16X16);
    this.delQnaireButton = new Button(comp, SWT.PUSH);
    this.delQnaireButton.setImage(deleteButtonImage);
    this.delQnaireButton.setEnabled(false);
    this.delQnaireButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        if (CommonUtils.isNotNull(DefineQuestionnaireDialog.this.selQnaireResp)) {
          for (RvwQnaireResponse qnaireResp : DefineQuestionnaireDialog.this.selQnaireResp) {
            DefineQuestionnaireDialog.this.allQnaireResponseSet.remove(qnaireResp);
            DefineQuestionnaireDialog.this.workPkgSet
                .remove(DefineQuestionnaireDialog.this.qnaireRespWpMap.get(qnaireResp));
            DefineQuestionnaireDialog.this.qnaireRespWpMap.remove(qnaireResp);
          }
          DefineQuestionnaireDialog.this.qNaireTableViewer
              .setInput(DefineQuestionnaireDialog.this.allQnaireResponseSet);
          DefineQuestionnaireDialog.this.delQnaireButton.setEnabled(false);
        }
        DefineQuestionnaireDialog.this.okPressed.setEnabled(validate());
      }
    });

    this.delQnaireButton.setToolTipText("Only newly added questionnaire (in blue) can be deleted");
  }

  private void createQNaireLinkGroup() {

    TabFolder qNaireLinkFolder = new TabFolder(this.composite, SWT.NONE);

    qNaireLinkFolder.setLayout(new GridLayout());
    GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    qNaireLinkFolder.setLayoutData(gridData);

    // Check if it has single variant and multiple WP/Resps
    // If it has single variant not displaying the Link to Variant tab
    if (!this.hasSingleVariantMultipleWp) {
      TabItem varLinkTab = new TabItem(qNaireLinkFolder, SWT.NONE);
      varLinkTab.setText("Link to Variant");

      Composite varLinkComposite = new Composite(qNaireLinkFolder, SWT.NONE);
      varLinkComposite.setLayout(new GridLayout(1, false));
      varLinkTab.setControl(varLinkComposite);


      LabelUtil.getInstance().createLabel(createRowComposite(varLinkComposite), "");
      gridData.heightHint = 250;

      this.varLinkFilter = new VariantLinkingFilter();
      createFilterTxt(varLinkComposite);
      final Composite tableComp = new Composite(varLinkComposite, SWT.NONE);
      tableComp.setLayout(new GridLayout(1, false));
      tableComp.setLayoutData(GridDataUtil.getInstance().getGridData());
      createVariantQNaireLinkTable(tableComp);
    }

    // For WP/Resp Linking Tab
    TabItem wpLinkTab = new TabItem(qNaireLinkFolder, SWT.NONE);
    wpLinkTab.setText("Link to another WP/Resp");

    Composite wpLinkComposite = new Composite(qNaireLinkFolder, SWT.NONE);
    wpLinkComposite.setLayout(new GridLayout(1, false));
    wpLinkTab.setControl(wpLinkComposite);
    LabelUtil.getInstance().createLabel(createRowComposite(wpLinkComposite), "");

    this.wpLinkFilter = new WpLinkingFilter();
    createFilterTxtForWpLink(wpLinkComposite);

    final Composite tableCompWp = new Composite(wpLinkComposite, SWT.NONE);
    tableCompWp.setLayout(new GridLayout(1, false));
    tableCompWp.setLayoutData(GridDataUtil.getInstance().getGridData());
    createWpQNaireLinkTable(tableCompWp);
  }

  /**
   * This method creates filter text for Attributes
   */
  private void createFilterTxt(final Composite qNaireLinkComposite) {
    this.filterTxt = TextUtil.getInstance().createFilterText(this.formToolkit, qNaireLinkComposite,
        GridDataUtil.getInstance().getTextGridData(), Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.filterTxt.addModifyListener(event -> {
      final String text = DefineQuestionnaireDialog.this.filterTxt.getText().trim();
      DefineQuestionnaireDialog.this.varLinkFilter.setFilterText(text);
      DefineQuestionnaireDialog.this.qNaireLinkTableViewer.refresh();
    });
  }

  /**
   * This method creates filter text for Attributes of Wp/Resp Link Table
   */
  private void createFilterTxtForWpLink(final Composite qNaireWpLinkComposite) {
    this.wpFilterTxt = TextUtil.getInstance().createFilterText(this.formToolkit, qNaireWpLinkComposite,
        GridDataUtil.getInstance().getTextGridData(), Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.wpFilterTxt.addModifyListener(event -> {
      final String text = DefineQuestionnaireDialog.this.wpFilterTxt.getText().trim();
      DefineQuestionnaireDialog.this.wpLinkFilter.setFilterText(text);
      DefineQuestionnaireDialog.this.qNaireWpLinkTableViewer.refresh();
    });
  }

  /**
   * @param tableComp
   */
  private void createVariantQNaireLinkTable(final Composite tableComp) {
    this.qNaireLinkTableViewer = GridTableViewerUtil.getInstance().createGridTableViewer(tableComp,
        SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, GridDataUtil.getInstance().getGridData());
    this.qNaireLinkTableViewer.setContentProvider(new ArrayContentProvider());
    this.qNaireLinkTableViewer.getGrid().setLinesVisible(true);
    this.qNaireLinkTableViewer.getGrid().setHeaderVisible(true);

    createVariantColumn();
    createLinkColumn();
    createDetailsColumn();

    fillQnaireRespVarLinkSet();
    this.qNaireLinkTableViewer.addFilter(this.varLinkFilter);
  }

  /**
   */
  private void fillQnaireRespVarLinkSet() {

    this.qnaireRespVarLinkSet.clear();

    for (PidcVariant pidcVar : this.allVariantMap.values()) {
      if (!pidcVar.getId().equals(this.pidcVariant.getId())) {
        QnaireRespVarRespWpLink qnaireRespVarLink = new QnaireRespVarRespWpLink();
        qnaireRespVarLink.setPidcVariant(pidcVar);
        qnaireRespVarLink.setA2lResponsibility(this.a2lResp);
        qnaireRespVarLink.setA2lWorkPackage(this.a2lWp);
        this.qnaireRespVarLinkSet.add(qnaireRespVarLink);
      }
    }

    if (null != this.qNaireLinkTableViewer) {
      this.qNaireLinkTableViewer.setInput(this.qnaireRespVarLinkSet);
    }
  }

  /**
   *
   */
  private void createVariantColumn() {
    final GridViewerColumn varCol = new GridViewerColumn(this.qNaireLinkTableViewer, SWT.NONE);
    varCol.getColumn().setText("Variant");
    varCol.getColumn().setWidth(300);
    setVariantColLabelProvider(varCol);
  }

  /**
   * @param varCol
   */
  private void setVariantColLabelProvider(final GridViewerColumn varCol) {
    varCol.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        QnaireRespVarRespWpLink qnaireRespVarLink = (QnaireRespVarRespWpLink) element;
        return qnaireRespVarLink.getPidcVariant().getName();
      }

      @Override
      public Color getForeground(final Object element) {
        if (element instanceof QnaireRespVarRespWpLink) {
          QnaireRespVarRespWpLink qnaireRespVarLink = (QnaireRespVarRespWpLink) element;
          if (qnaireRespVarLink.isDisabled()) {
            return getqNaireLinkTableViewer().getGrid().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
          }
        }
        return null;
      }
    });
  }

  /**
   *
   */
  private void createLinkColumn() {
    GridColumn linkGridCol = new GridColumn(this.qNaireLinkTableViewer.getGrid(), SWT.CHECK | SWT.CENTER);
    linkGridCol.setWidth(50);
    linkGridCol.setSummary(false);
    this.linkColumn = new GridViewerColumn(this.qNaireLinkTableViewer, linkGridCol);
    this.linkColumn.getColumn().setText("Linked");
    this.linkColumn.getColumn().setWidth(100);
    setLinkcolLabelProvider();

    addLinkColEditSupport();
    this.linkColumn.getColumn().setVisible(false);
  }

  /**
   *
   */
  private void addLinkColEditSupport() {
    this.linkColumn.setEditingSupport(new CheckEditingSupport(this.linkColumn.getViewer()) {

      @Override
      public void setValue(final Object arg0, final Object arg1) {
        QnaireRespVarRespWpLink qnaireRespVarLink = (QnaireRespVarRespWpLink) arg0;
        boolean isLinked = (Boolean) arg1;
        if (isLinked) {
          variantLinking(qnaireRespVarLink, isLinked);
        }
        else {
          variantUnlinking(qnaireRespVarLink, isLinked);
        }
        DefineQuestionnaireDialog.this.qnaireRespVarLinkSet.add(qnaireRespVarLink);
        DefineQuestionnaireDialog.this.okPressed.setEnabled(validate());
      }
    });
  }

  /**
   * Create Wp/Resp Linking Table
   *
   * @param tableComp
   */
  private void createWpQNaireLinkTable(final Composite tableComp) {
    this.qNaireWpLinkTableViewer = GridTableViewerUtil.getInstance().createGridTableViewer(tableComp,
        SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, GridDataUtil.getInstance().getGridData());
    this.qNaireWpLinkTableViewer.setContentProvider(new ArrayContentProvider());
    this.qNaireWpLinkTableViewer.getGrid().setLinesVisible(true);
    this.qNaireWpLinkTableViewer.getGrid().setHeaderVisible(true);
    // Create the columns for Table
    createVariantColumnForWpLink();
    createRespColumn();
    createWpColumn();
    createLinkColumnForWpLink();
    createDetailsColumnForWpLink();
    // Set the Data for the Table
    fillQnaireRespWpLinkSet();
    this.qNaireWpLinkTableViewer.addFilter(this.wpLinkFilter);
  }

  /**
   * @param selectedA2lResp
   * @param selectedA2lWp
   */
  private void fillQnaireRespWpLinkSet() {
    A2lResponsibility selectedA2lResponsibility;
    A2lWorkPackage selectedA2lWp;
    // Get the selected WP and Responsibility
    // If WP and Resp is not selected assign null to the fields
    if (this.qnaireInfo.getA2lRespMap().containsKey(this.selRespIdMap.get(this.selWpRespName))) {
      selectedA2lResponsibility = this.qnaireInfo.getA2lRespMap().get(this.selRespIdMap.get(this.selWpRespName));
    }
    else {
      selectedA2lResponsibility = null;
    }
    if (this.qnaireInfo.getA2lWpMap().containsKey(this.selWpIdMap.get(this.selWpRespName))) {
      selectedA2lWp = this.qnaireInfo.getA2lWpMap().get(this.selWpIdMap.get(this.selWpRespName));
    }
    else {
      selectedA2lWp = null;
    }

    this.a2lResp = selectedA2lResponsibility;
    this.a2lWp = selectedA2lWp;

    this.qnaireRespWpLinkSet.clear();

    // qnaireInfo.varRespWpQniareMap contains the mapping of Variant - Responsibility - WP - QnaireResp
    // Iterate over the map to get the Variants
    this.qnaireInfo.getVarRespWpQniareMap().entrySet().stream().forEach(entry -> {
      // Check if the variant is available or if it is No - Variant
      if (this.allVariantMap.containsKey(entry.getKey()) || (entry.getKey().equals(ApicConstants.NO_VARIANT_ID))) {
        PidcVariant pidcVariantObj;
        if (this.allVariantMap.containsKey(entry.getKey())) {
          pidcVariantObj = this.allVariantMap.get(entry.getKey());
        }
        else {
          // If it is No variant, create a PIDC object for no variant
          pidcVariantObj = new PidcVariant();
          pidcVariantObj.setId(ApicConstants.NO_VARIANT_ID);
          pidcVariantObj.setName(ApicConstants.DUMMY_VAR_NODE_NOVAR);
        }
        // Iterate over the map values to get all the Responsibilities
        entry.getValue().entrySet().forEach(
            respWpMap -> createWpRespLinkTab(selectedA2lResponsibility, selectedA2lWp, pidcVariantObj, respWpMap));
      }
    });
    if (null != this.qNaireWpLinkTableViewer) {
      this.qNaireWpLinkTableViewer.setInput(this.qnaireRespWpLinkSet);
    }
  }

  /**
   * @param selectedA2lResp
   * @param selectedA2lWp
   * @param pidcVariantObj
   * @param respWpMap
   */
  private void createWpRespLinkTab(final A2lResponsibility selectedA2lResp, final A2lWorkPackage selectedA2lWp,
      final PidcVariant pidcVariantObj, final Entry<Long, Map<Long, Set<Long>>> respWpMap) {
    if (this.qnaireInfo.getA2lRespMap().containsKey(respWpMap.getKey())) {
      A2lResponsibility a2lResponsibility = this.qnaireInfo.getA2lRespMap().get(respWpMap.getKey());
      // Iterate over the map to get all the WPs
      respWpMap.getValue().entrySet().forEach(wpQnaireRespMap -> {
        Long wpId = wpQnaireRespMap.getKey();
        if (this.qnaireInfo.getA2lWpMap().containsKey(wpId)) {
          A2lWorkPackage a2lWorkPackage = this.qnaireInfo.getA2lWpMap().get(wpId);

          // Do not display the selected WP and Resp in Linking table
          if (!(pidcVariantObj.getId().equals(this.pidcVariant.getId()) && a2lResponsibility.equals(selectedA2lResp) &&
              a2lWorkPackage.equals(selectedA2lWp))) {
            QnaireRespVarRespWpLink qnaireWpVarLink = new QnaireRespVarRespWpLink();
            qnaireWpVarLink.setPidcVariant(pidcVariantObj);
            qnaireWpVarLink.setA2lResponsibility(a2lResponsibility);
            qnaireWpVarLink.setA2lWorkPackage(a2lWorkPackage);
            this.qnaireRespWpLinkSet.add(qnaireWpVarLink);
          }
        }
      });
    }
  }

  /**
  *
  */
  private void createVariantColumnForWpLink() {
    final GridViewerColumn varCol = new GridViewerColumn(this.qNaireWpLinkTableViewer, SWT.NONE);
    varCol.getColumn().setText("Variant");
    varCol.getColumn().setWidth(200);
    setVariantColForWpLinkLabelProvider(varCol);
  }

  /**
   * @param varCol
   */
  private void setVariantColForWpLinkLabelProvider(final GridViewerColumn varCol) {
    varCol.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        QnaireRespVarRespWpLink qnaireRespWpLink = (QnaireRespVarRespWpLink) element;
        return qnaireRespWpLink.getPidcVariant().getName();
      }

      @Override
      public Color getForeground(final Object element) {
        if (element instanceof QnaireRespVarRespWpLink) {
          QnaireRespVarRespWpLink qnaireRespWpLink = (QnaireRespVarRespWpLink) element;
          if (qnaireRespWpLink.isDisabled()) {
            return getqNaireWpLinkTableViewer().getGrid().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
          }
        }
        return null;
      }
    });
  }

  /**
  *
  */
  private void createRespColumn() {
    final GridViewerColumn varCol = new GridViewerColumn(this.qNaireWpLinkTableViewer, SWT.NONE);
    varCol.getColumn().setText("Responsibility");
    varCol.getColumn().setWidth(200);
    setRespColLabelProvider(varCol);
  }

  /**
   * @param varCol
   */
  private void setRespColLabelProvider(final GridViewerColumn varCol) {
    varCol.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        QnaireRespVarRespWpLink qnaireRespWpLink = (QnaireRespVarRespWpLink) element;
        return qnaireRespWpLink.getA2lResponsibility().getName();
      }

      @Override
      public Color getForeground(final Object element) {
        if (element instanceof QnaireRespVarRespWpLink) {
          QnaireRespVarRespWpLink qnaireRespWpLink = (QnaireRespVarRespWpLink) element;
          if (qnaireRespWpLink.isDisabled()) {
            return getqNaireWpLinkTableViewer().getGrid().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
          }
        }
        return null;
      }
    });
  }

  /**
  *
  */
  private void createWpColumn() {
    final GridViewerColumn varCol = new GridViewerColumn(this.qNaireWpLinkTableViewer, SWT.NONE);
    varCol.getColumn().setText("Work Package");
    varCol.getColumn().setWidth(200);
    setWpColLabelProvider(varCol);
  }

  /**
   * @param varCol
   */
  private void setWpColLabelProvider(final GridViewerColumn varCol) {
    varCol.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        QnaireRespVarRespWpLink qnaireRespWpLink = (QnaireRespVarRespWpLink) element;
        return qnaireRespWpLink.getA2lWorkPackage().getName();
      }

      @Override
      public Color getForeground(final Object element) {
        if (element instanceof QnaireRespVarRespWpLink) {
          QnaireRespVarRespWpLink qnaireRespWpLink = (QnaireRespVarRespWpLink) element;
          if (qnaireRespWpLink.isDisabled()) {
            return getqNaireWpLinkTableViewer().getGrid().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
          }
        }
        return null;
      }
    });
  }

  /**
  *
  */
  private void createLinkColumnForWpLink() {
    GridColumn linkGridCol = new GridColumn(this.qNaireWpLinkTableViewer.getGrid(), SWT.CHECK | SWT.CENTER);
    linkGridCol.setWidth(50);
    linkGridCol.setSummary(false);
    this.wpLinkColumn = new GridViewerColumn(this.qNaireWpLinkTableViewer, linkGridCol);
    this.wpLinkColumn.getColumn().setText("Linked");
    this.wpLinkColumn.getColumn().setWidth(100);
    setLinkcolLabelProviderForWpLink();
    addWpLinkColEditSupport();
    this.wpLinkColumn.getColumn().setVisible(false);
  }

  /**
  *
  */
  private void addWpLinkColEditSupport() {
    this.wpLinkColumn.setEditingSupport(new CheckEditingSupport(this.wpLinkColumn.getViewer()) {

      @Override
      public void setValue(final Object arg0, final Object arg1) {
        QnaireRespVarRespWpLink qnaireRespVarLink = (QnaireRespVarRespWpLink) arg0;
        boolean isLinked = (Boolean) arg1;
        if (isLinked) {
          variantLinking(qnaireRespVarLink, isLinked);
        }
        else {
          varWpRespUnlinking(qnaireRespVarLink, isLinked);
        }
        DefineQuestionnaireDialog.this.qnaireRespWpLinkSet.add(qnaireRespVarLink);
        DefineQuestionnaireDialog.this.okPressed.setEnabled(validate());
      }
    });
  }


  /**
  *
  */
  private void setLinkcolLabelProviderForWpLink() {
    this.wpLinkColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public void update(final ViewerCell cell) {
        final Object element = cell.getElement();
        if (element instanceof QnaireRespVarRespWpLink) {
          QnaireRespVarRespWpLink qnaireRespWpLink = (QnaireRespVarRespWpLink) element;
          final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
          gridItem.setChecked(cell.getVisualIndex(), qnaireRespWpLink.isLinked());

          boolean isCheckable = qnaireRespWpLink.isDisabled();
          gridItem.setCheckable(cell.getVisualIndex(), !isCheckable);
          gridItem.setGrayed(cell.getVisualIndex(), isCheckable);
        }
      }


      @Override
      public Color getForeground(final Object element) {
        if (element instanceof QnaireRespVarRespWpLink) {
          QnaireRespVarRespWpLink qnaireRespWpLink = (QnaireRespVarRespWpLink) element;
          if (qnaireRespWpLink.isDisabled()) {
            return getqNaireWpLinkTableViewer().getGrid().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
          }
        }
        return null;
      }
    });
  }

  /**
   *
   */
  private void createDetailsColumnForWpLink() {
    this.wpDetailCol = new GridViewerColumn(this.qNaireWpLinkTableViewer, SWT.NONE);
    this.wpDetailCol.getColumn().setText("Details");
    this.wpDetailCol.getColumn().setWidth(500);
    setDetailscolLabelProviderForWpLink(this.wpDetailCol);
    this.wpDetailCol.getColumn().setVisible(false);
  }

  /**
   * @param detailCol
   */
  private void setDetailscolLabelProviderForWpLink(final GridViewerColumn wpDetailCol) {
    wpDetailCol.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        QnaireRespVarRespWpLink qnaireRespWpLink = (QnaireRespVarRespWpLink) element;
        return qnaireRespWpLink.getDetails();
      }

      @Override
      public Color getForeground(final Object element) {
        if (element instanceof QnaireRespVarRespWpLink) {
          QnaireRespVarRespWpLink qnaireRespWpLink = (QnaireRespVarRespWpLink) element;
          if (qnaireRespWpLink.isDisabled()) {
            return getqNaireWpLinkTableViewer().getGrid().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
          }
        }
        return null;
      }
    });
  }

  /**
   * @param qnaireRespVarLink
   * @param isLinked
   */
  private void variantLinking(final QnaireRespVarRespWpLink qnaireRespVarLink, final boolean isLinked) {
    qnaireRespVarLink.setLinked(isLinked);
    if (!DefineQuestionnaireDialog.this.modifiedQnaireRespVarLinkSet.remove(qnaireRespVarLink)) {
      DefineQuestionnaireDialog.this.modifiedQnaireRespVarLinkSet.add(qnaireRespVarLink);
    }
  }

  /**
   * @param qnaireRespVarLink
   * @param isLinked
   */
  private void variantUnlinking(final QnaireRespVarRespWpLink qnaireRespVarLink, final boolean isLinked) {
    if (!DefineQuestionnaireDialog.this.modifiedQnaireRespVarLinkSet.remove(qnaireRespVarLink)) {
      for (RvwQnaireRespVariant rvwQnaireRespVariant : DefineQuestionnaireDialog.this.qnaireRespVarMap
          .get(DefineQuestionnaireDialog.this.selectedQnaireResp.getId())) {
        if (DefineQuestionnaireDialog.this.selectedQnaireResp.getSecondaryQnaireLinkMap().keySet()
            .contains(rvwQnaireRespVariant.getVariantId()) &&
            CommonUtils.isEqual(qnaireRespVarLink.getPidcVariant().getId(), rvwQnaireRespVariant.getVariantId())) {
          qnaireRespVarLink.setQnaireRespVarIdToDel(rvwQnaireRespVariant.getId());
        }
      }
      DefineQuestionnaireDialog.this.modifiedQnaireRespVarLinkSet.add(qnaireRespVarLink);
    }
    qnaireRespVarLink.setLinked(isLinked);
  }

  /**
   * @param qnaireRespVarLink
   * @param isLinked
   */
  private void varWpRespUnlinking(final QnaireRespVarRespWpLink qnaireRespVarLink, final boolean isLinked) {
    if (!DefineQuestionnaireDialog.this.modifiedQnaireRespVarLinkSet.remove(qnaireRespVarLink)) {
      for (RvwQnaireRespVariant rvwQnaireRespVariant : DefineQuestionnaireDialog.this.qnaireRespVarMap
          .get(DefineQuestionnaireDialog.this.selectedQnaireResp.getId())) {
        if (isQnaireRespVarLinkHasSameVariant(qnaireRespVarLink.getPidcVariant().getId(),
            rvwQnaireRespVariant.getVariantId()) &&
            CommonUtils.isEqual(qnaireRespVarLink.getA2lResponsibility().getId(),
                rvwQnaireRespVariant.getA2lRespId()) &&
            CommonUtils.isEqual(qnaireRespVarLink.getA2lWorkPackage().getId(), rvwQnaireRespVariant.getA2lWpId())) {
          qnaireRespVarLink.setQnaireRespVarIdToDel(rvwQnaireRespVariant.getId());
        }
      }
      DefineQuestionnaireDialog.this.modifiedQnaireRespVarLinkSet.add(qnaireRespVarLink);
    }
    qnaireRespVarLink.setLinked(isLinked);
  }


  private boolean isQnaireRespVarLinkHasSameVariant(final Long varIdFromLink, final Long varId) {
    if (CommonUtils.isNull(varId) && CommonUtils.isEqual(ApicConstants.NO_VARIANT_ID, varIdFromLink)) {
      return true;
    }
    return CommonUtils.isEqual(varIdFromLink, varId);
  }


  /**
   *
   */
  private void setLinkcolLabelProvider() {
    this.linkColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public void update(final ViewerCell cell) {
        final Object element = cell.getElement();
        if (element instanceof QnaireRespVarRespWpLink) {
          QnaireRespVarRespWpLink qnaireRespVarLink = (QnaireRespVarRespWpLink) element;
          final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
          gridItem.setChecked(cell.getVisualIndex(), qnaireRespVarLink.isLinked());

          boolean isCheckable = qnaireRespVarLink.isDisabled();
          gridItem.setCheckable(cell.getVisualIndex(), !isCheckable);
          gridItem.setGrayed(cell.getVisualIndex(), isCheckable);
        }
      }


      @Override
      public Color getForeground(final Object element) {
        if (element instanceof QnaireRespVarRespWpLink) {
          QnaireRespVarRespWpLink qnaireRespVarLink = (QnaireRespVarRespWpLink) element;
          if (qnaireRespVarLink.isDisabled()) {
            return getqNaireLinkTableViewer().getGrid().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
          }
        }
        return null;
      }
    });
  }

  /**
   *
   */
  private void createDetailsColumn() {
    this.detailCol = new GridViewerColumn(this.qNaireLinkTableViewer, SWT.NONE);
    this.detailCol.getColumn().setText("Details");
    this.detailCol.getColumn().setWidth(500);
    setDetailscolLabelProvider(this.detailCol);
    this.detailCol.getColumn().setVisible(false);
  }

  /**
   * @param detailCol
   */
  private void setDetailscolLabelProvider(final GridViewerColumn detailCol) {
    detailCol.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        QnaireRespVarRespWpLink qnaireRespVarLink = (QnaireRespVarRespWpLink) element;
        return qnaireRespVarLink.getDetails();
      }

      @Override
      public Color getForeground(final Object element) {
        if (element instanceof QnaireRespVarRespWpLink) {
          QnaireRespVarRespWpLink qnaireRespVarLink = (QnaireRespVarRespWpLink) element;
          if (qnaireRespVarLink.isDisabled()) {
            return getqNaireLinkTableViewer().getGrid().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
          }
        }
        return null;
      }
    });
  }


  /**
   * @return the qNaireTableViewer
   */
  public GridTableViewer getqNaireTableViewer() {
    return this.qNaireTableViewer;
  }


  /**
   * @param qNaireTableViewer the qNaireTableViewer to set
   */
  public void setqNaireTableViewer(final GridTableViewer qNaireTableViewer) {
    this.qNaireTableViewer = qNaireTableViewer;
  }


  /**
   * @return the pidcVariant
   */
  public PidcVariant getPidcVariant() {
    return this.pidcVariant;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.okPressed = createButton(parent, IDialogConstants.OK_ID, "OK", true);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    DefineQuestionnaireDialog.this.okPressed.setEnabled(validate());
  }

  /**
   * {@inheritDoc}
   */
  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {


    try {
      QnaireRespUpdationModel qnaireRespInputData = new QnaireRespUpdationModel();
      qnaireRespInputData.setPidcVersionId(this.pidcVersion.getId());

      // For No variant dont set Pidc variant id
      if (this.pidcVariant.getId() > 0) {
        qnaireRespInputData.setPidcVariantId(this.pidcVariant.getId());
      }
      qnaireRespInputData.setDivId(getDivId());
      qnaireRespInputData.setSelRespId(this.selRespIdMap.get(this.selWpRespName));
      qnaireRespInputData.setSelWpId(this.selWpIdMap.get(this.selWpRespName));
      qnaireRespInputData.setOldQnaireRespSet(getOldQnaireResponseSet());
      qnaireRespInputData.setWorkPkgSet(getWorkPkgSet());
      qnaireRespInputData.setQnaireRespVarLinkSet(this.modifiedQnaireRespVarLinkSet);
      RvwQnaireResponseServiceClient client = new RvwQnaireResponseServiceClient();
      QnaireRespUpdationModel updationModel = client.createQnaireResp(qnaireRespInputData);
      confirmAndDeleteGenQues(updationModel.getGenQuesNotReqQues());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    super.okPressed();
  }


  /**
   * @param genQuesNotReqQues
   */
  private void confirmAndDeleteGenQues(final Set<String> genQuesNotReqQues) {
    // validations for deletion of general questionnaires
    StringBuilder builder = new StringBuilder();
    builder.append("General Questionnaire will not be required as following questionnaires are added.\n");
    if (CommonUtils.isNotEmpty(genQuesNotReqQues)) {
      for (String quesName : genQuesNotReqQues) {
        builder.append(quesName).append(",");
      }
      builder.deleteCharAt(builder.length() - 1);
      builder.append("\nDo you want to remove the general questionnaire?");

      Optional<RvwQnaireResponse> findGenQues = this.allQnaireResponseSet.stream()
          .filter(qnaireResp -> (qnaireResp.getName().startsWith(ApicConstants.GENERAL_QUESTIONS)) ||
              qnaireResp.getName().startsWith(ApicConstants.OBD_GENERAL_QUESTIONS))
          .findFirst();
      // Show confirmation to delete the 'General Questionnaire'
      if (findGenQues.isPresent() &&
          MessageDialogUtils.getConfirmMessageDialogWithYesNo("Delete General Questionnaire", builder.toString())) {
        // call delete service
        RvwQnaireResponseServiceClient client = new RvwQnaireResponseServiceClient();
        try {
          RvwQnaireResponse genQuesResp = findGenQues.get();
          genQuesResp.setDeletedFlag(true);
          client.update(genQuesResp);
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
        }
      }
    }
  }

  private boolean validate() {
    return this.isAccessAvailable && (!this.qnaireResponseUpdateSet.isEmpty() || !this.workPkgSet.isEmpty() ||
        !this.modifiedQnaireRespVarLinkSet.isEmpty());
  }


  /**
   * @return the qnaireResponseset
   */
  public SortedSet<RvwQnaireResponse> getAllQnaireResponseSet() {
    return this.allQnaireResponseSet;
  }


  /**
   * @param qnaireResponseSet the qnaireResponseset to set
   */
  public void setAllQnaireResponseList(final SortedSet<RvwQnaireResponse> qnaireResponseSet) {
    this.allQnaireResponseSet = qnaireResponseSet;
  }


  /**
   * @return the divId
   */
  public Long getDivId() {
    return this.divId;
  }


  /**
   * @param divId the divId to set
   */
  public void setDivId(final Long divId) {
    this.divId = divId;
  }


  /**
   * @return the workPkgSet
   */
  public SortedSet<WorkPkg> getWorkPkgSet() {
    return this.workPkgSet;
  }

  /**
   * @return the qnaireRespWpMap
   */
  public Map<RvwQnaireResponse, WorkPkg> getQnaireRespWpMap() {
    return this.qnaireRespWpMap;
  }


  /**
   * @return the oldQnaireResponseSet
   */
  public SortedSet<RvwQnaireResponse> getOldQnaireResponseSet() {
    return this.oldQnaireResponseSet;
  }


  /**
   * @return the pidcVersion
   */
  public PidcVersion getPidcVersion() {
    return this.pidcVersion;
  }


  /**
   * @return the selWpRespName
   */
  public String getSelWpRespName() {
    return this.selWpRespName;
  }

  /**
   * @return the qnaireRespMap
   */
  public Map<String, Set<Long>> getQnaireRespMap() {
    return this.qnaireRespMap;
  }


  /**
   * @return the qNaireLinkTableViewer
   */
  public GridTableViewer getqNaireLinkTableViewer() {
    return this.qNaireLinkTableViewer;
  }

  /**
   * @return the qNaireLinkTableViewer
   */
  public GridTableViewer getqNaireWpLinkTableViewer() {
    return this.qNaireWpLinkTableViewer;
  }


  /**
   * @param element
   * @return
   */
  private String qNaireColGetText(final Object element) {
    RvwQnaireResponse qnaireResp = (RvwQnaireResponse) element;
    StringBuilder resultName = new StringBuilder(qnaireResp.getName());
    if (isDifferentQnaireVariant(qnaireResp) || isDifferentQnaireWp(qnaireResp) || isDifferentQnaireResp(qnaireResp)) {
      resultName.append(" - ");
      resultName.append(qnaireResp.getPrimaryVarRespWpName());
    }
    return resultName.toString();
  }

  private boolean isDifferentQnaireVariant(final RvwQnaireResponse qnaireResp) {
    return CommonUtils.isNotEqual(qnaireResp.getVariantId(), DefineQuestionnaireDialog.this.pidcVariant.getId());
  }

  private boolean isDifferentQnaireWp(final RvwQnaireResponse qnaireResp) {
    return CommonUtils.isNotEqual(qnaireResp.getA2lWpId(), DefineQuestionnaireDialog.this.a2lWp.getId());
  }

  private boolean isDifferentQnaireResp(final RvwQnaireResponse qnaireResp) {
    return CommonUtils.isNotEqual(qnaireResp.getA2lRespId(), DefineQuestionnaireDialog.this.a2lResp.getId());
  }


  /**
   * @return the a2lRespId
   */
  public A2lResponsibility getA2lResp() {
    return this.a2lResp;
  }


  /**
   * @return the a2lWpId
   */
  public A2lWorkPackage getA2lWp() {
    return this.a2lWp;
  }


}
