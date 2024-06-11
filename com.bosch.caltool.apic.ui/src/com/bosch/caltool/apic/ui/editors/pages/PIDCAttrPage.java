/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages;


import static org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes.CELL_PAINTER;
import static org.eclipse.nebula.widgets.nattable.grid.GridRegion.FILTER_ROW;
import static org.eclipse.nebula.widgets.nattable.style.DisplayMode.NORMAL;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.command.StructuralRefreshCommand;
import org.eclipse.nebula.widgets.nattable.command.VisualRefreshCommand;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.config.NullComparator;
import org.eclipse.nebula.widgets.nattable.data.IColumnAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultBooleanDisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDoubleDisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.ComboBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.edit.editor.ICellEditor;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterIconPainter;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowDataLayer;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowPainter;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.CheckBoxPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ImagePainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.CellPainterDecorator;
import org.eclipse.nebula.widgets.nattable.persistence.command.DisplayPersistenceDialogCommandHandler;
import org.eclipse.nebula.widgets.nattable.selection.RowSelectionProvider;
import org.eclipse.nebula.widgets.nattable.sort.SortConfigAttributes;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseClickAction;
import org.eclipse.nebula.widgets.nattable.ui.util.CellEdgeEnum;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
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

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.actions.PIDCActionSet;
import com.bosch.caltool.apic.ui.actions.PIDCGroupedAttrActionSet;
import com.bosch.caltool.apic.ui.actions.PIDCGrpAttrInfoAction;
import com.bosch.caltool.apic.ui.actions.PIDCPageToolBarActionSet;
import com.bosch.caltool.apic.ui.actions.PIDCSessionLockAction;
import com.bosch.caltool.apic.ui.actions.PIDCUpToDateAction;
import com.bosch.caltool.apic.ui.dialogs.PIDCAttrValueEditDialog;
import com.bosch.caltool.apic.ui.dialogs.PIDCGrpdAttrChangesDialog;
import com.bosch.caltool.apic.ui.editors.PIDCEditor;
import com.bosch.caltool.apic.ui.editors.PIDCEditorInput;
import com.bosch.caltool.apic.ui.editors.PIDCNatToolTip;
import com.bosch.caltool.apic.ui.editors.compare.PIDCCompareNatTableCheckBoxCellEditor;
import com.bosch.caltool.apic.ui.editors.compare.PidcColumnDataMapper;
import com.bosch.caltool.apic.ui.editors.compare.PidcColumnFilterMatcher;
import com.bosch.caltool.apic.ui.editors.compare.PidcEditConfiguration;
import com.bosch.caltool.apic.ui.editors.compare.PidcNattableLabelAccumulator;
import com.bosch.caltool.apic.ui.editors.compare.PidcNattableRowObject;
import com.bosch.caltool.apic.ui.table.filters.PIDCHistoryToolBarFilters;
import com.bosch.caltool.apic.ui.table.filters.PIDCOutlineFilter;
import com.bosch.caltool.apic.ui.table.filters.PIDCPageToolBarFilters;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.apic.ui.util.PIDCPageConstants;
import com.bosch.caltool.apic.ui.util.PIDCPageEditUtil;
import com.bosch.caltool.apic.ui.views.PIDCHistoryViewPart;
import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectObjectBO;
import com.bosch.caltool.icdm.client.bo.apic.ApicDataBO;
import com.bosch.caltool.icdm.client.bo.apic.AttrRootNode;
import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.client.bo.apic.AttributeValueClientBO;
import com.bosch.caltool.icdm.client.bo.apic.PIDCDetailsNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcEditorDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcSubVariantAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcSubVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionStatus;
import com.bosch.caltool.icdm.client.bo.apic.ProjectObjectStatistics;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.client.bo.uc.FavUseCaseItemNode;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.ProjFavUcRootNode;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseGroupClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseRootNode;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseSectionClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UserFavUcRootNode;
import com.bosch.caltool.icdm.common.ui.actions.CDMCommonActionSet;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.editors.AbstractNatFormPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.OutlineViewPart;
import com.bosch.caltool.icdm.common.ui.views.PIDCDetailsViewPart;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedValidity;
import com.bosch.caltool.icdm.model.apic.pidc.GroupdAttrPredefAttrModel;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectObject;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.general.Link;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomNATTable;
import com.bosch.caltool.nattable.configurations.CustomNatTableStyleConfiguration;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.text.TextUtil;

/**
 * Main Page of PIDC Editor, showing project attributes
 *
 * @author pdh2cob
 */
public class PIDCAttrPage extends AbstractNatFormPage implements ISelectionListener {

  /**
   * message if pidc version is up to date
   */
  private static final String PIDC_VERSION_UP_TO_DATE_MSG = "PIDC Version is up to date";
  /**
   * message if pidc version is not up to date
   */
  private static final String PIDC_VERSION_NOT_UP_TO_DATE_MSG = "PIDC Version is NOT up to date";
  /**
   * Title Separator
   */
  private static final String STR_ARROW = " >> ";
  /**
   * Composite instance for base layout
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
   * Filter text instance
   */
  private Text filterTxt;

  /**
   * PIDCPageToolBarFilters instance
   */
  // ICDM-107
  private PIDCPageToolBarFilters toolBarFilters;
  /**
   * OutlineFilter instance
   */
  private PIDCOutlineFilter outLineFilter;

  /**
   * @deprecated
   */
  /**
   * GridTableViewer instance for PIDC attribute
   */
  @Deprecated
  private GridTableViewer pidcAttrTabViewer;

  private PidcVariant selectedPidcVariant;

  private PidcVersion selectedPidcVersion;

  /**
   * Selected PIDCSubVariant instance
   */
  // ICDM-121
  private PidcSubVariant selectedPidcSubVariant;
  /**
   * Defines PIDC details variant tree node is selected or not
   */
  private boolean isVarNodeSelected;

  /**
   * Defines PIDC details sub-variant tree node is selected or not
   */
  // ICDM-121
  private boolean isSubVarNodeSelected;
  /**
   * PIDCPageToolBarActionSet instance
   */
  private PIDCPageToolBarActionSet toolBarActionSet;

  /**
   * the column index for the Value column if the used column is expanded
   */
  private static final int COLUMN_INDEX_VALUE_IF_EXPANDED = 6;

  private RowSelectionProvider<PidcNattableRowObject> selectionProvider;
  /**
   * Editor instance
   */
  private final PIDCEditor editor;
  /**
   * Non scrollable form
   */
  private Form nonScrollableForm;
  /**
   * PIDC Attribute Used info Grid Column group instance
   */

  /**
  *
  */
  private static final int YCOORDINATE_TEN = 10;
  /**
  *
  */
  private static final int XCOORDINATE_TEN = 10;

  /**
   * last selection object
   */
  private Object lastSelection;

  /**
   * PIDCHistoryViewPart instance
   */
  protected PIDCHistoryViewPart historyView;
  /**
   * Constant for Sdom pver name attr
   */
  private static final String SDOM_PVER_ATTR_NAME = "PVER name in SDOM";
  /**
   * Constant for Sdom pver Warning String
   */
  private static final String SDOM_PVER_WARN_STR =
      "You are about to Change the SDOM pver Configuration. It may result in some inconsistencies in PIDC. Do you Really want to change the Value?";

  private static final String CUSTOM_COMPARATOR_LABEL = "customComparatorLabel";

  /**
   * SB size of warning message
   */
  private static final int SB_WARNMSG_SIZE = 80;
  private String selCellValue;

  // ICDM-2354
  /**
   *
   */
  private PIDCSessionLockAction pidcLockAction;

  // ICDM-2625
  /**
  *
  */
  private PIDCGrpAttrInfoAction pidcGrpAttrInfoAction;

  /**
   * Label to display pidc statistics
   */
  private StyledText pidcStatisticsLabel;
  private Section sectionStatistics;
  private ToolBarManager toolbarManager;

  private PIDCUpToDateAction pidcNotUpToDateAction;

  private AbstractProjectObjectBO projObjBO;

  private final CurrentUserBO currentUser = new CurrentUserBO();

  // Nattable related declarations

  /** The property to label map. */
  private Map<Integer, String> propertyToLabelMap;

  private int totTableRowCount;

  /*
   * NAT table grid layer
   */
  private CustomFilterGridLayer pidcFilterGridLayer;

  /*
   * NAT table instance
   */
  private CustomNATTable natTable;


  private static final int COLUMN_NUM_BALL = 0;

  private static final int COLUMN_NUM_ATTRIBUTE = 1;

  private static final int COLUMN_NUM_DESCRIPTION = 2;

  private static final int COLUMN_NUM_USED_QUES = 3;

  private static final int COLUMN_NUM_USED_NO = 4;

  private static final int COLUMN_NUM_USED_YES = 5;

  private static final int COLUMN_NUM_VALUE = 6;

  private static final int COLUMN_NUM_VALUE_ICON = 7;

  private static final int COLUMN_NUM_VCDM = 8;

  private static final int COLUMN_NUM_FM = 9;

  private static final int COLUMN_NUM_PART_NUMBER = 10;

  private static final int COLUMN_NUM_SPECIFICATION = 11;

  private static final int COLUMN_NUM_COMMENT = 12;

  private static final int COLUMN_NUM_MODIFIED_DATE = 13;

  private static final int COLUMN_NUM_ATTRIBUTE_CLASS = 14;

  private static final int COLUMN_NUM_VALUE_CLASS = 15;

  private static final int COLUMN_NUM_ATTRIBUTE_ON_DATE = 16;

  private static final int COLUMN_NUM_GROUP = 17;

  private static final int COLUMN_NUM_SUPERGROUP = 18;

  private PidcColumnFilterMatcher<PidcNattableRowObject> allColumnFilterMatcher;

  private SortedSet<PidcNattableRowObject> compareRowObjects;


  private AbstractProjectAttributeBO projectAttrHandler;

  private final PidcDataHandler pidcDataHandler;

  private final PidcVersionBO pidcVersionBO;

  /**
   * If filter option is selected in warning dialog for undefined mand/uc attrs, flag is set to true
   */
  private boolean filterUndefinedAttr;

  /**
   * Check box columns in nattable
   */
  private static final int[] checkBoxColumns = { 3, 4, 5, 8, 9 };

  private static final String CHECK_BOX_CONFIG_LABEL = "checkBox";
  private static final String CHECK_BOX_EDITOR_CNG_LBL = "checkBoxEditor";
  /**
   *
   */
  protected static final int LEFT_MOUSE_CLICK = 1;

  /**
   * Map to hold the filter action text and its changed state (checked - true or false)
   */
  private final Map<String, Boolean> toolBarFilterStateMap = new ConcurrentHashMap<>();
  /**
   * showFilterDialog flag to indicate whether to display warning dialog,do not show warning dialog when opened via
   * usecase link
   */
  private final boolean isNotShowWarnDialog;
  private PIDCUpToDateAction pidcUpToDateAction;
  private boolean isPidcLocked;

  /**
   * field to store the value of the click event
   */
  private int clickEventVal;
  // to hold outline view selection
  private UsecaseClientBO selectedUsecaseClientBo;
  private UseCaseSectionClientBO selectedUsecaseSectionClientBo;
  private UseCaseGroupClientBO selectedUsecaseGroupClientBo;

  private IUseCaseItemClientBO iUseCaseItemClientBO;

  /**
   * The Parameterized constructor
   *
   * @param editor              instance of FormEditor
   * @param pidcDataHandler     data handler for pidc
   * @param isNotShowWarnDialog flag to indicate whether warn message dialog is to be shown or not,do not show this
   *                              dialog in case pidc editor is opened using usecase link
   */
  public PIDCAttrPage(final FormEditor editor, final PidcDataHandler pidcDataHandler,
      final boolean isNotShowWarnDialog) {
    super(editor, Messages.getString(IMessageConstants.PIDC_LABEL),
        Messages.getString(IMessageConstants.PIDC_PAGE_LABEL));
    this.editor = (PIDCEditor) editor;
    this.selectedPidcVersion = ((PIDCEditorInput) editor.getEditorInput()).getSelectedPidcVersion();

    this.projObjBO = ((PIDCEditorInput) editor.getEditorInput()).getPidcVersionBO();
    this.pidcVersionBO = (PidcVersionBO) this.projObjBO;
    this.pidcDataHandler = this.pidcVersionBO.getPidcDataHandler();
    this.isNotShowWarnDialog = isNotShowWarnDialog;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public PIDCEditorInput getEditorInput() {
    return (PIDCEditorInput) super.getEditorInput();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createPartControl(final Composite parent) {
    // ICDM-26/ICDM-168
    // Super class implementation creates a scrolled form by default
    // The scrolled form is used to create a managed form

    // Overrode this by creating an ordinary form without scrollable behaviour and a managed form instantiated without a
    // scrolled form

    // Create an ordinary non scrollable form on which widgets are built
    this.nonScrollableForm = this.editor.getToolkit().createForm(parent);
    this.nonScrollableForm.getBody().setLayout(new GridLayout());
    this.nonScrollableForm.getBody().setLayoutData(new GridData());
    // ICDM-208
    this.nonScrollableForm.setText(setTitle().replace("&", "&&"));

    if (isUpToDate()) {
      this.nonScrollableForm.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.ALL_16X16));
      this.nonScrollableForm.setMessage("[ " + PIDC_VERSION_UP_TO_DATE_MSG + " ]");
    }
    else {
      this.nonScrollableForm.setMessage("[ " + PIDC_VERSION_NOT_UP_TO_DATE_MSG + " ]", 3);

    }
    ManagedForm mform = new ManagedForm(parent);
    createFormContent(mform);

  }


  /**
   * @return the pidcLockAction
   */
  public PIDCSessionLockAction getPidcLockAction() {
    return this.pidcLockAction;
  }


  /**
   * @return the pidcGrpAttrInfoAction
   */
  public PIDCGrpAttrInfoAction getPidcGrpAttrInfoAction() {
    return this.pidcGrpAttrInfoAction;
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
    FormToolkit formToolkit = managedForm.getToolkit();
    // create composite
    createComposite(formToolkit);

    // add listeners
    getSite().getPage().addPostSelectionListener(this);
  }

  /**
   * This method initializes composite
   */
  private void createComposite(final FormToolkit toolkit) {
    this.composite = this.nonScrollableForm.getBody();
    this.composite.setLayout(new GridLayout());
    createStatisticsInfoControl(toolkit);

    // create section
    createSection(toolkit);

    // ICDM-891 display warning message for non mandate and Uncleared and deleted
    if (this.projObjBO.isModifiable()) {
      // Icdm-1001 only for users having Write access on PIDC enable this.
      displayWarnMessageForUndefinedAttr();
    }

    // Task 242053
    try {
      if (this.currentUser.hasNodeOwnerAccess(this.selectedPidcVersion.getPidcId()) && !isUpToDate() &&
          !this.isPidcLocked && (!this.editor.getEditorInput().isNewlyCreatedPIDC())) {
        displayInfoMessageForNotUpToDate();
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    // set composite layout data
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
  }

  /**
   * @return the selectionProvider
   */
  public RowSelectionProvider<PidcNattableRowObject> getSelectionProvider() {
    return this.selectionProvider;
  }

  /**
   * check if it is Up to date or not
   *
   * @return boolean
   */
  public boolean isUpToDate() {
    return this.selectedPidcVersion.isUpToDate();
  }

  /**
   * @param toolkit
   */
  private void createStatisticsInfoControl(final FormToolkit toolkit) {
    this.sectionStatistics =
        toolkit.createSection(this.composite, ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);

    this.sectionStatistics.setText("Project Statistics");

    Form formStatistics = toolkit.createForm(this.sectionStatistics);
    formStatistics.getBody().setLayout(new GridLayout());
    this.pidcStatisticsLabel = new StyledText(formStatistics.getBody(), SWT.NONE);
    this.sectionStatistics.setLayoutData(GridDataUtil.getInstance().getTextGridData());

    this.pidcStatisticsLabel.setLayoutData(GridDataUtil.getInstance().getTextGridData());

    this.sectionStatistics.setClient(formStatistics);
  }

  /**
   * This method initializes section
   */
  private void createSection(final FormToolkit toolkit) {
    this.section = toolkit.createSection(this.composite, ExpandableComposite.TITLE_BAR);
    this.section.setExpanded(true);
    this.section.setText(Messages.getString(IMessageConstants.ATTRIBUTES_INFO_LABEL));
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());

    this.toolBarActionSet = new PIDCPageToolBarActionSet(this.pidcFilterGridLayer, this);
    final ToolBarManager toolBarManager = (ToolBarManager) this.nonScrollableForm.getToolBarManager();
    // add help action
    addHelpAction(toolBarManager);

    this.pidcLockAction = new PIDCSessionLockAction(this.selectedPidcVersion);

    this.isPidcLocked = !(new ApicDataBO()).isPidcUnlockedInSession(getSelectedPidcVersion());

    this.pidcUpToDateAction = new PIDCUpToDateAction(true, this);
    toolBarManager.add(this.pidcUpToDateAction);
    this.pidcNotUpToDateAction = new PIDCUpToDateAction(false, this);
    toolBarManager.add(this.pidcNotUpToDateAction);
    toolBarManager.add(new Separator());

    toolBarManager.add(this.pidcLockAction);

    toolBarManager.add(new Separator());

    this.pidcGrpAttrInfoAction = new PIDCGrpAttrInfoAction(this, this.pidcVersionBO.getPidcVersion());


    toolBarManager.add(this.pidcGrpAttrInfoAction);

    toolBarManager.add(new Separator());
    // ICDM-2354

    this.toolBarActionSet.createPidcHistoryAction(toolBarManager, this);
    toolBarManager.add(new Separator());
    this.toolBarActionSet.pidcRequestorAction(toolBarManager, false);
    toolBarManager.update(true);


    createForm(toolkit);

    this.section.setClient(this.form);


    Map<Long, PidcVersionAttribute> allPIDCAttrMap = this.pidcVersionBO.getAttributesAll();

    PIDCGroupedAttrActionSet actionSet =
        new PIDCGroupedAttrActionSet(this.pidcVersionBO.getPidcDataHandler(), this.pidcVersionBO);
    List<GroupdAttrPredefAttrModel> grpAttrValList =
        actionSet.getAllPidcGrpAttrVal(this.selectedPidcVersion, allPIDCAttrMap, null);

    if ((null != grpAttrValList) && !grpAttrValList.isEmpty()) {
      PIDCGrpdAttrChangesDialog dialog = new PIDCGrpdAttrChangesDialog(Display.getDefault().getActiveShell(), this,
          null, null, this.selectedPidcVersion, true, allPIDCAttrMap, null, grpAttrValList, null);
      dialog.open();
    }


  }

  /**
   * Task 242053 show info dialog if the PIDC version is not up to date
   */
  private void displayInfoMessageForNotUpToDate() {

    boolean confirmUpToDate = MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
        "Confirm PIDC Version Up To Date", PIDC_VERSION_NOT_UP_TO_DATE_MSG + "\n Confirm if it is up to date.");
    if (confirmUpToDate) {
      confirmUpToDate(false);
    }
  }


  /**
   * create command and confirm the update date
   */
  private void confirmUpToDate(final boolean notUpToDate) {

    if (notUpToDate) {
      this.selectedPidcVersion.setLastConfirmationDate(null);
    }
    else {
      this.selectedPidcVersion
          .setLastConfirmationDate(ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_15, Calendar.getInstance()));
    }
    getPidcVersionBO().upToDate(this.selectedPidcVersion);

    if (notUpToDate) {
      getNonScrollableForm().setMessage(PIDC_VERSION_NOT_UP_TO_DATE_MSG, 3);
      getPidcNotUpToDateAction().setEnabled(false);
    }
    else {
      getNonScrollableForm().setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.ALL_16X16));
      getNonScrollableForm().setMessage(PIDC_VERSION_UP_TO_DATE_MSG);
      getPidcUpToDateAction().setEnabled(false);
      getPidcNotUpToDateAction().setEnabled(true);
    }
  }

  /**
   * Info about Attributes
   *
   * @return info
   */
  // Icdm-342
  public String constructStatisticalInfoForAttr() {
    return constructStatisticalInfo(this.selectedPidcVersion);
  }


  /**
   * Info about Attributes
   *
   * @param varID variant id
   * @return String
   */
  public String constructStatisticalInfoForVarAttr(final Long varID) {
    PidcVariant pidcVar = this.projObjBO.getPidcDataHandler().getVariantMap().get(varID);
    return constructStatisticalInfo(pidcVar);
  }

  /**
   * Info about Attributes
   *
   * @param subVarId subVarId
   * @return string
   */
  public String constructStatisticalInfoForSubVarAttr(final Long subVarId) {
    PidcSubVariant pidcSubVar = this.projObjBO.getPidcDataHandler().getSubVariantMap().get(subVarId);
    return constructStatisticalInfo(pidcSubVar);
  }

  /**
   * Find statistical info. Set total number of items to attribute grid table viewer
   */
  private String constructStatisticalInfo(final IProjectObject projObj) {
    if (projObj == null) {
      return "";
    }
    ProjectObjectStatistics<?> statistics = this.projObjBO.getProjectStatistics(
        getEditorInput().getOutlineDataHandler().getUcDataHandler(), getEditorInput().getFmDataHandler());

    setStatiscticsSectionColor(statistics);
    return statistics.getStatisticsAsString();
  }

  /**
   * @param statistics
   */
  private void setStatiscticsSectionColor(final ProjectObjectStatistics<?> statistics) {
    if (!statistics.validateStatistics()) {
      this.sectionStatistics.setTitleBarBackground(new Color(Display.getCurrent(), 255, 189, 145));
    }
  }

  /**
   * @return the natTable
   */
  public CustomNATTable getNatTable() {
    return this.natTable;
  }

  /**
   * This method initializes scrolledForm
   */
  private void createForm(final FormToolkit toolkit) {
    // ICDM-2625


    this.form = toolkit.createForm(this.section);

    this.form.getBody().setLayout(new GridLayout());


    this.filterTxt = TextUtil.getInstance().createFilterText(toolkit, this.form.getBody(),
        GridDataUtil.getInstance().getTextGridData(), Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    addModifyListenerForFilterTxt();
    createTable();
  }


  /**
   *
   */
  private void createTable() {

    createPidcNattable();

    createToolBarAction();

    addMouseListener();

    updateOverAllRiskDetails();

  }


  private void createToolBarAction() {


    this.toolbarManager = new ToolBarManager(SWT.FLAT);

    this.toolBarActionSet = new PIDCPageToolBarActionSet(this.pidcFilterGridLayer, this);

    final Separator separator = new Separator();

    // show all the attributes including the invisible attributes
    this.toolBarActionSet.showAllAttributesAction(this.toolbarManager, this.toolBarFilters);

    this.toolbarManager.add(separator);
    this.toolBarActionSet.pidcUsecaseAndAttrMandatoryFilterAction(this.toolbarManager, this.toolBarFilters);

    this.toolBarActionSet.pidcNonUsecaseAndAttrMandatoryFilterAction(this.toolbarManager, this.toolBarFilters);
    this.toolbarManager.add(separator);
    this.toolBarActionSet.createQuotationRelevantFilterAction(this.toolbarManager, this.toolBarFilters);

    this.toolBarActionSet.createQuotationNotRelevantFilterAction(this.toolbarManager, this.toolBarFilters);


    this.toolbarManager.add(separator);
    // ICDM-2625
    this.toolBarActionSet.showAllGroupedAttrAction(this.toolbarManager, this.toolBarFilters);
    this.toolBarActionSet.showAllNotGrpAttrAction(this.toolbarManager, this.toolBarFilters);
    this.toolbarManager.add(separator);

    // filter for dependent attributes
    this.toolBarActionSet.pidcAttrDepenFilterAction(this.toolbarManager, this.toolBarFilters);

    // filter for non dependent attributes
    this.toolBarActionSet.pidcAttrNotDepenFilterAction(this.toolbarManager, this.toolBarFilters);

    this.toolbarManager.add(separator);

    // filter for mandatory attributes
    this.toolBarActionSet.pidcAttrMandatoryFilterAction(this.toolbarManager, this.toolBarFilters);

    // filter for non mandatory attributes
    this.toolBarActionSet.pidcAttrNonMandatoryFilterAction(this.toolbarManager, this.toolBarFilters);

    this.toolbarManager.add(separator);

    // filter for new attributes
    this.toolBarActionSet.newAttributesFilter(this.toolbarManager, this.toolBarFilters);

    // filter for used but not defined flag
    this.toolBarActionSet.pidcAttrNotKnownFilterAction(this.toolbarManager, this.toolBarFilters);

    // filter for not used flag
    this.toolBarActionSet.pidcAttrNotUsedFilterAction(this.toolbarManager, this.toolBarFilters);

    // filter for used flag
    this.toolBarActionSet.pidcAttrUsedFilterAction(this.toolbarManager, this.toolBarFilters);

    this.toolbarManager.add(separator);

    // filter for defined attributes
    this.toolBarActionSet.definedFilterAction(this.toolbarManager, this.toolBarFilters);

    // filter for not defined attributes
    this.toolBarActionSet.notDefinedFilterAction(this.toolbarManager, this.toolBarFilters);

    this.toolbarManager.add(separator);
    // filter for variant attributes
    this.toolBarActionSet.variantFilterAction(this.toolbarManager, this.toolBarFilters);

    // filter for non variant attributes
    this.toolBarActionSet.nonVariantFilterAction(this.toolbarManager, this.toolBarFilters);

    this.toolbarManager.add(separator);

    // filter for un cleared attributes
    this.toolBarActionSet.createNotClearFilterAction(this.toolbarManager, this.toolBarFilters);

    // filter for cleared attributes
    this.toolBarActionSet.createClearFilterAction(this.toolbarManager, this.toolBarFilters);

    this.toolbarManager.add(separator);

    // filter for structured attributes
    this.toolBarActionSet.structFilterAction(this.toolbarManager, this.toolBarFilters);

    // filter for non structured attributes
    this.toolBarActionSet.notstructFilterAction(this.toolbarManager, this.toolBarFilters);

    this.toolbarManager.add(separator);


    this.toolbarManager.update(true);

    Composite toolbarComposite = this.editor.getToolkit().createComposite(this.section);
    toolbarComposite.setBackground(null);
    this.toolbarManager.createControl(toolbarComposite);
    this.section.setTextClient(toolbarComposite);
    addResetAllFiltersAction();


  }

  /**
   * Add reset filter button
   */
  private void addResetAllFiltersAction() {
    getFilterTxtSet().add(this.filterTxt);
    getRefreshComponentSet().add(this.pidcFilterGridLayer);
    addResetFiltersAction();
  }

  /**
   * Method to create pidc nattable
   */
  private void createPidcNattable() {


    // creating nattable header columns
    Map<Integer, Integer> columnWidthMap = createNatTableColumns();


    IConfigRegistry configRegistry = new ConfigRegistry();

    // fill attributes information for table
    fillNattableRowObjects();

    PidcNatInputToColumnConverter natInputToColumnConverter = new PidcNatInputToColumnConverter();

    this.pidcFilterGridLayer = new CustomFilterGridLayer<PidcNattableRowObject>(configRegistry, this.compareRowObjects,
        columnWidthMap, new CustomPIDCCompareColumnPropertyAccessor<>(), new CustomPIDCCompareHeaderDataProvider(),
        getComparePIDCComparator(0), natInputToColumnConverter, this, new ValueEditNatMouseClickAction(), false, false,
        true);


    this.allColumnFilterMatcher = new PidcColumnFilterMatcher<>();
    this.pidcFilterGridLayer.getFilterStrategy().setAllColumnFilterMatcher(this.allColumnFilterMatcher);


    this.natTable = new CustomNATTable(
        this.form.getBody(), SWT.FULL_SELECTION | SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED |
            SWT.BORDER | SWT.VIRTUAL | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI,
        this.pidcFilterGridLayer, false, getClass().getSimpleName());

    // check whether the focus matrix is disabled
    // If disabled hide the FM column
    if (!this.editor.isFocusmatrixEnabled()) {
      this.pidcFilterGridLayer.getBodyLayer().getColumnHideShowLayer()
          .hideColumnPositions(Arrays.asList(COLUMN_NUM_FM));
    }

    try {
      this.natTable.setProductVersion(new CommonDataBO().getIcdmVersion());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    IProjectObject projObj;
    if (this.isVarNodeSelected) {
      projObj = this.selectedPidcVariant;
    }
    else if (this.isSubVarNodeSelected) {
      projObj = this.selectedPidcSubVariant;
    }
    else {
      projObj = this.selectedPidcVersion;
    }
    this.toolBarFilters = new PIDCPageToolBarFilters(projObj, this);
    this.pidcFilterGridLayer.getFilterStrategy().setToolBarFilterMatcher(this.toolBarFilters.getToolBarMatcher());
    this.natTable.setConfigRegistry(configRegistry);
    this.natTable.setLayoutData(getGridData());
    this.natTable.addConfiguration(new CustomNatTableStyleConfiguration());


    this.natTable.addConfiguration(new FilterRowCustomConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry confRegistry) {
        super.configureRegistry(confRegistry); // Shade the row to be slightly darker than the blue background. final
        Style rowStyle = new Style();
        rowStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.getColor(197, 212, 231));
        confRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, rowStyle, NORMAL, FILTER_ROW);
      }
    });

    // sorting configuration
    this.natTable.addConfiguration(new SingleClickSortConfiguration());
    this.natTable
        .addConfiguration(getCustomComparatorConfiguration(this.pidcFilterGridLayer.getColumnHeaderDataLayer()));

    this.natTable.addConfiguration(new AbstractRegistryConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry confRegistry) {
        registerConfigLabelsOnColumns((ColumnOverrideLabelAccumulator) PIDCAttrPage.this.pidcFilterGridLayer
            .getDummyDataLayer().getConfigLabelAccumulator());
        registerCheckBoxEditor(confRegistry);
        registerEditableRules(confRegistry);
        registerValueEditIcon(confRegistry);
      }
    });


    this.natTable.addConfiguration(new PidcEditConfiguration(this.pidcVersionBO));


    DataLayer bodyDataLayer = this.pidcFilterGridLayer.getDummyDataLayer();
    IRowDataProvider<PidcNattableRowObject> bodyDataProvider =
        (IRowDataProvider<PidcNattableRowObject>) bodyDataLayer.getDataProvider();


    final PidcNattableLabelAccumulator compPIDCLabelAccumulator =
        new PidcNattableLabelAccumulator(bodyDataLayer, bodyDataProvider);
    bodyDataLayer.setConfigLabelAccumulator(compPIDCLabelAccumulator);

    this.outLineFilter = new PIDCOutlineFilter(getPidcDataHandler(), getEditorInput().getOutlineDataHandler());
    this.pidcFilterGridLayer.getFilterStrategy().setOutlineNatFilterMatcher(this.outLineFilter.getUcOutlineMatcher());
    this.natTable.configure();

    this.pidcFilterGridLayer.registerCommandHandler(new DisplayPersistenceDialogCommandHandler(this.natTable));


    this.selectionProvider = new RowSelectionProvider<>(this.pidcFilterGridLayer.getBodyLayer().getSelectionLayer(),
        this.pidcFilterGridLayer.getBodyDataProvider(), false);

    addSelectionListener();
    addRightClickOption();

    groupColumns();

    // The below method is required to enable tootltip only for cells which contain not fully visible content
    attachToolTip(this.natTable);

  }

  /**
   * @return boolean
   */
  public boolean isUcItemSelected() {
    return this.outLineFilter.isUcItemSelected();
  }


  /**
   * @return
   */
  public IUseCaseItemClientBO getUcItem() {
    return this.iUseCaseItemClientBO;
  }

  /**
   * @return
   */
  public UseCaseSectionClientBO getUseCaseSectionClientBo() {
    return this.selectedUsecaseSectionClientBo;
  }

  /**
   * @return
   */
  public UsecaseClientBO getUseCaseClientBo() {
    return this.selectedUsecaseClientBo;
  }

  /**
   * @return
   */
  public UseCaseGroupClientBO getUseCaseGroupClientBo() {
    return this.selectedUsecaseGroupClientBo;
  }

  /**
   *
   */
  private void addSelectionListener() {
    this.selectionProvider.addSelectionChangedListener(event -> {
      final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      Object selected = selection.getFirstElement();
      if (selected instanceof PidcNattableRowObject) {
        final PidcNattableRowObject compareRowObject = (PidcNattableRowObject) selected;
        IProjectAttribute selectedPIDCAttr = compareRowObject.getProjectAttributeHandler().getProjectAttr();
        setSelectedAttrHistoryView(selectedPIDCAttr);
        setSelectedAttrPropertiesView(selection);
      }
    });
  }


  /**
   * Method to set selection to properties view
   *
   * @param selection from nattable
   */
  private void setSelectedAttrPropertiesView(final IStructuredSelection selection) {
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


  private IConfiguration getCustomComparatorConfiguration(final AbstractLayer columnHeaderDataLayer) {

    return new AbstractRegistryConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry configRegistry) {
        // Add label accumulator
        ColumnOverrideLabelAccumulator labelAccumulator = new ColumnOverrideLabelAccumulator(columnHeaderDataLayer);
        columnHeaderDataLayer.setConfigLabelAccumulator(labelAccumulator);

        // Register labels
        labelAccumulator.registerColumnOverrides(0, CUSTOM_COMPARATOR_LABEL + 0);

        labelAccumulator.registerColumnOverrides(1, CUSTOM_COMPARATOR_LABEL + 1);

        labelAccumulator.registerColumnOverrides(2, CUSTOM_COMPARATOR_LABEL + 2);

        labelAccumulator.registerColumnOverrides(3, CUSTOM_COMPARATOR_LABEL + 3);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR, new NullComparator(), NORMAL,
            CUSTOM_COMPARATOR_LABEL + 0);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR, getComparePIDCComparator(1),
            NORMAL, CUSTOM_COMPARATOR_LABEL + 1);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR, getComparePIDCComparator(2),
            NORMAL, CUSTOM_COMPARATOR_LABEL + 2);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR, getComparePIDCComparator(3),
            NORMAL, CUSTOM_COMPARATOR_LABEL + 3);


        for (int i = 4; i < (PIDCAttrPage.this.propertyToLabelMap.size()); i++) {
          String colHeader = PIDCAttrPage.this.propertyToLabelMap.get(i);
          if (CommonUIConstants.EMPTY_STRING.equals(colHeader) ||
              colHeader.equalsIgnoreCase(Messages.getString(IMessageConstants.QUESTION_LABEL)) ||
              colHeader.equalsIgnoreCase(Messages.getString(IMessageConstants.NO_LABEL)) ||
              colHeader.equalsIgnoreCase(Messages.getString(IMessageConstants.YES_LABEL))) {
            continue;
          }
          labelAccumulator.registerColumnOverrides(i, CUSTOM_COMPARATOR_LABEL + i);

          configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR, getComparePIDCComparator(i),
              NORMAL, CUSTOM_COMPARATOR_LABEL + i);
        }

        // Register null comparator to disable sort
      }
    };
  }

  /**
   * Enables tootltip only for cells which contain not fully visible content
   *
   * @param natTable
   */
  private void attachToolTip(final NatTable natTbl) {
    // Icdm-1208- Custom tool tip for Nat table.
    DefaultToolTip toolTip = new PIDCNatToolTip(natTbl, new String[0], this.projObjBO, this.pidcVersionBO);
    toolTip.setPopupDelay(0);
    toolTip.activate();
    toolTip.setShift(new Point(XCOORDINATE_TEN, YCOORDINATE_TEN));
  }

  /**
   *
   */
  private void addRightClickOption() {
    final MenuManager menuMgr = new MenuManager();
    menuMgr.setRemoveAllWhenShown(true);
    final PIDCActionSet actionSet = new PIDCActionSet();
    final CommonActionSet cmnActionSet = new CommonActionSet();
    menuMgr.addMenuListener(mgr -> {
      final IStructuredSelection selection = (IStructuredSelection) PIDCAttrPage.this.selectionProvider.getSelection();

      if (selection != null) {
        final Object firstElement = selection.getFirstElement();

        if (firstElement instanceof PidcNattableRowObject) {

          PidcNattableRowObject compareRowObject = (PidcNattableRowObject) firstElement;
          IProjectAttribute ipidcAttribute = compareRowObject.getProjectAttributeHandler().getProjectAttr();

          addRightClickOptionBasedOnSelection(menuMgr, actionSet, cmnActionSet, selection, compareRowObject,
              ipidcAttribute);
        }
      }
    });

    final Menu menu = menuMgr.createContextMenu(this.natTable.getShell());
    this.natTable.setMenu(menu);
    // Register menu for extension.
    getSite().registerContextMenu(menuMgr, this.selectionProvider);
  }


  /**
   * @param menuMgr
   * @param actionSet
   * @param cmnActionSet
   * @param selection
   * @param compareRowObject
   * @param ipidcAttribute
   */
  private void addRightClickOptionBasedOnSelection(final MenuManager menuMgr, final PIDCActionSet actionSet,
      final CommonActionSet cmnActionSet, final IStructuredSelection selection,
      final PidcNattableRowObject compareRowObject, final IProjectAttribute ipidcAttribute) {
    if (!selection.isEmpty()) {

      if (selection.size() == CommonUIConstants.SINGLE_SELECTION) {
        // ICDM-1291
        List<IProjectAttribute> listOfElements = new ArrayList<>();
        listOfElements.add(compareRowObject.getProjectAttributeHandler().getProjectAttr());

        actionSet.addPidcSearchOption(menuMgr, listOfElements, PIDCAttrPage.this.selectedPidcVersion,
            PIDCAttrPage.this.getPidcDataHandler());
        menuMgr.add(new Separator());


        if (ipidcAttribute instanceof PidcVersionAttribute) {

          singlePidcAttrMenu(menuMgr, actionSet, cmnActionSet, ipidcAttribute);
        }
        else if (ipidcAttribute instanceof PidcVariantAttribute) {
          singleVarAttrMenu(menuMgr, actionSet, cmnActionSet, ipidcAttribute);
        }
        // ICDM-123
        else if (ipidcAttribute instanceof PidcSubVariantAttribute) {
          singleSubVarAttrMenu(menuMgr, actionSet, cmnActionSet, ipidcAttribute);
        }

        if (!CommonUtils.isEmptyString(getSelCellValue())) {
          actionSet.copyAttrValueMenu(menuMgr, getSelCellValue());
        }
      }
      else {
        addMulAttrOptions(menuMgr, actionSet, selection, ipidcAttribute);
      }
    }
  }

  /**
   * @param editableAttrVar
   * @param menuMgr
   * @param cmnActionSet
   */
  private void addValLinkAction(final PidcVariantAttribute editableAttrVar, final MenuManager menuMgr,
      final CommonActionSet cmnActionSet) {
    if (null != getPidcDataHandler().getAttributeValue(editableAttrVar.getValueId())) {
      AttributeValueClientBO attrValClntBO =
          new AttributeValueClientBO(getPidcDataHandler().getAttributeValue(editableAttrVar.getValueId()));
      SortedSet<Link> links = attrValClntBO.getLinks();
      if (!editableAttrVar.isAtChildLevel() && (null != links) && !links.isEmpty()) {
        menuMgr.add(new Separator());
        cmnActionSet.addValLinkAction(menuMgr, links);
      }
    }
  }

  /**
   * @param menuMgr
   * @param actionSet
   * @param selection
   * @param ipidcAttribute
   */
  private void addMulAttrOptions(final MenuManager menuMgr, final PIDCActionSet actionSet,
      final IStructuredSelection selection, final IProjectAttribute ipidcAttribute) {

    List<PidcNattableRowObject> list = selection.toList();
    List<IProjectAttribute> listOfElements = new ArrayList<>();

    for (PidcNattableRowObject compareRowObject : list) {
      listOfElements.add(compareRowObject.getProjectAttributeHandler().getProjectAttr());
    }
    // ICDM-1291
    actionSet.addPidcSearchOption(menuMgr, listOfElements, this.selectedPidcVersion,
        PIDCAttrPage.this.getPidcDataHandler());


    // ICDM-1402
    if (ipidcAttribute instanceof PidcVersionAttribute) {
      setPidcAttrOptions(menuMgr, actionSet, selection);
    }
    else if (ipidcAttribute instanceof PidcVariantAttribute) {
      setVarAttrOptions(menuMgr, actionSet, selection);
    }
    else if (ipidcAttribute instanceof PidcSubVariantAttribute) {
      setSubVarAttrOptions(menuMgr, actionSet, selection);
    }
  }

  /**
   * @param menuMgr
   * @param actionSet
   * @param selection
   */
  private void setSubVarAttrOptions(final MenuManager menuMgr, final PIDCActionSet actionSet,
      final IStructuredSelection selection) {
    final PidcNattableRowObject rowObj = (PidcNattableRowObject) selection.getFirstElement();
    IProjectAttribute editableAttrSubVar = rowObj.getProjectAttributeHandler().getProjectAttr();
    PidcSubVariantAttributeBO attrHan = new PidcSubVariantAttributeBO((PidcSubVariantAttribute) editableAttrSubVar,
        new PidcSubVariantBO(this.selectedPidcVersion, this.selectedPidcSubVariant, getPidcDataHandler()));

    if (attrHan.isVisible() &&
        !(this.selectedPidcVersion.getPidStatus().equals(PidcVersionStatus.LOCKED.getDbStatus())) &&
        getPidcVersionBO().isModifiable()) {
      actionSet.addMoveMultAttrToVariantMenu(menuMgr, selection, this);
    }
  }


  /**
   * @param menuMgr
   * @param actionSet
   * @param selection
   */
  private void setVarAttrOptions(final MenuManager menuMgr, final PIDCActionSet actionSet,
      final IStructuredSelection selection) {
    PidcNattableRowObject rowObj = (PidcNattableRowObject) selection.getFirstElement();
    final PidcVariantAttribute editableAttrVar =
        (PidcVariantAttribute) rowObj.getProjectAttributeHandler().getProjectAttr();
    PidcVariantAttributeBO varandler = new PidcVariantAttributeBO(editableAttrVar,
        new PidcVariantBO(this.selectedPidcVersion, getSelectedPidcVariant(), this.pidcDataHandler));
    if (varandler.isVisible() && varandler.isModifiable() && getPidcVersionBO().isModifiable()) {
      if (checkAllVarAttrs(selection)) {
        actionSet.addMoveMulAttrToCommonMenu(menuMgr, selection, getSelectedPidcVariant(), this);
        actionSet.addMoveMulAttrToSubVarMenu(menuMgr, selection, this);
      }
    } // only if all the selected attrs are in sub-variant level , provide this context menu option
    else if (varandler.isVisible() &&
        !(this.selectedPidcVersion.getPidStatus().equals(PidcVersionStatus.LOCKED.getDbStatus())) &&
        editableAttrVar.isAtChildLevel() && getPidcVersionBO().isModifiable() && checkAllSubVarAttrs(selection)) {
      actionSet.addMoveMultAttrToVariantMenu(menuMgr, selection, this);
    }
  }


  /**
   * @param menuMgr
   * @param actionSet
   * @param selection
   */
  private void setPidcAttrOptions(final MenuManager menuMgr, final PIDCActionSet actionSet,
      final IStructuredSelection selection) {

    if (!getPidcVersionBO().isModifiable() ||
        CommonUtils.isEqual(this.selectedPidcVersion.getPidStatus(), PidcVersionStatus.LOCKED.getDbStatus())) {
      return;
    }

    boolean canMoveTovariant = true;
    boolean canMoveToCommon = true;

    // Verify whether all attributes are modfiable and are defined at project level
    for (Object obj : selection.toList()) {

      PidcNattableRowObject compareRowObject = (PidcNattableRowObject) obj;

      AbstractProjectAttributeBO handler = compareRowObject.getProjectAttributeHandler();

      if (!handler.isVisible() || !handler.isModifiable() || !handler.canMoveDown() ||
          compareRowObject.getProjectAttributeHandler().getProjectAttr().isAtChildLevel()) {
        canMoveTovariant = false;
        break;
      }
    }

    // Verify whether all attributes are defined at variant level
    for (Object obj : selection.toList()) {
      PidcNattableRowObject compareRowObject = (PidcNattableRowObject) obj;
      IProjectAttribute projAttr = compareRowObject.getProjectAttributeHandler().getProjectAttr();
      AbstractProjectAttributeBO handler = compareRowObject.getProjectAttributeHandler();
      if (!handler.isVisible() || !projAttr.isAtChildLevel()) {
        canMoveToCommon = false;
        break;
      }
    }

    if (canMoveTovariant) {
      actionSet.addMoveMulAttrToVariantMenu(menuMgr, selection, this.selectedPidcVersion, this);
    }
    // Only one of the context menus is possible at a time
    else if (canMoveToCommon) {
      actionSet.addMoveMulAttrToCommonMenu(menuMgr, selection, getSelectedPidcVariant(), this);
    }


  }


  /**
   * @param menuMgr
   * @param actionSet
   * @param cmnActionSet
   * @param firstElement
   */
  private void singleSubVarAttrMenu(final MenuManager menuMgr, final PIDCActionSet actionSet,
      final CommonActionSet cmnActionSet, final Object firstElement) {

    final PidcSubVariantAttribute editableAttrSubVar = (PidcSubVariantAttribute) firstElement;
    PidcSubVariantAttributeBO attrHan = new PidcSubVariantAttributeBO(editableAttrSubVar,
        new PidcSubVariantBO(this.selectedPidcVersion, this.selectedPidcSubVariant, getPidcDataHandler()));

    if (attrHan.isVisible() && getPidcVersionBO().isModifiable()) {
      actionSet.addMoveToVariantMenu(menuMgr, editableAttrSubVar, this.selectedPidcSubVariant, this);
    }

    // ICDM-452
    menuMgr.add(new Separator());
    if (!attrHan.isHiddenToUser()) {
      actionSet.addShowAttrValsOption(menuMgr, PIDCAttrPage.this, editableAttrSubVar);
    }
    menuMgr.add(new Separator());

    AttributeValueClientBO attrValClntBO =
        new AttributeValueClientBO(getPidcDataHandler().getAttributeValue(editableAttrSubVar.getValueId()));
    AttributeClientBO attrClntBO =
        new AttributeClientBO(getPidcDataHandler().getAttribute(editableAttrSubVar.getAttrId()));

    cmnActionSet.addLinkAction(menuMgr, attrClntBO.getLinks());
    if (null != getPidcDataHandler().getAttributeValue(editableAttrSubVar.getValueId())) {
      SortedSet<Link> links = attrValClntBO.getLinks();
      if ((null != links) && !links.isEmpty()) {
        menuMgr.add(new Separator());
        cmnActionSet.addValLinkAction(menuMgr, links);
      }
    }
  }

  /**
   * @param menuMgr      MenuManager
   * @param actionSet    PIDCActionSet
   * @param cmnActionSet CommonActionSet
   * @param firstElement Object
   */
  private void singleVarAttrMenu(final MenuManager menuMgr, final PIDCActionSet actionSet,
      final CommonActionSet cmnActionSet, final Object firstElement) {

    final PidcVariantAttribute editableAttrVar = (PidcVariantAttribute) firstElement;
    PidcVariantAttributeBO varandler = new PidcVariantAttributeBO(editableAttrVar,
        new PidcVariantBO(this.selectedPidcVersion, getSelectedPidcVariant(), this.pidcDataHandler));
    addMoveToSubVariantAndCommonMenu(menuMgr, actionSet, editableAttrVar, varandler);
    actionSet.addMoveToVariantMenu(menuMgr, actionSet, editableAttrVar, this);
    menuMgr.add(new Separator());
    if (!varandler.isHiddenToUser()) {
      actionSet.addShowAttrValsOption(menuMgr, PIDCAttrPage.this, editableAttrVar);
    }
    menuMgr.add(new Separator());
    AttributeClientBO attrClntBO =
        new AttributeClientBO(getPidcDataHandler().getAttribute(editableAttrVar.getAttrId()));
    cmnActionSet.addLinkAction(menuMgr, attrClntBO.getLinks());
    addValLinkAction(editableAttrVar, menuMgr, cmnActionSet);
  }

  /**
   * @param menuMgr
   * @param actionSet
   * @param editableAttrVar
   * @param varandler
   */
  private void addMoveToSubVariantAndCommonMenu(final MenuManager menuMgr, final PIDCActionSet actionSet,
      final PidcVariantAttribute editableAttrVar, final PidcVariantAttributeBO varandler) {

    if (varandler.isVisible() && getPidcVersionBO().isModifiable()) {
      actionSet.addMoveToCommonMenu(menuMgr, editableAttrVar, this.selectedPidcVersion, this.selectedPidcVariant, this);
      // ICDM-123
      actionSet.addMoveToSubVariantMenu(menuMgr, editableAttrVar, this);
    }
  }


  private void registerEditableRules(final IConfigRegistry configRegistry) {

    // For checkbox columns - used flag, vcdm , fm

    for (final int i : PIDCAttrPage.checkBoxColumns) {

      configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE,
          DisplayMode.EDIT, CHECK_BOX_CONFIG_LABEL + "_" + i);
      ((ColumnOverrideLabelAccumulator) this.pidcFilterGridLayer.getColumnHeaderDataLayer().getConfigLabelAccumulator())
          .registerColumnOverrides(i, CUSTOM_COMPARATOR_LABEL + i);
      configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR, getCheckBoxColumnComparator(i),
          NORMAL, CUSTOM_COMPARATOR_LABEL + i);

    }
  }


  /**
   * Comparator for checkbox columns - for sorting
   *
   * @param i
   * @return
   */
  private static Comparator<PidcNattableRowObject> getCheckBoxColumnComparator(final int columnIndex) {
    return (final PidcNattableRowObject pidcNattableRowObject1, final PidcNattableRowObject pidcNattableRowObject2) -> {

      Boolean isPIDCAttr1Checked = (Boolean) pidcNattableRowObject1.getColumnDataMapper().getColumnData(columnIndex);
      Boolean isPIDCAttr2Checked = (Boolean) pidcNattableRowObject2.getColumnDataMapper().getColumnData(columnIndex);

      int ret = isPIDCAttr1Checked.compareTo(isPIDCAttr2Checked);
      if (ret == 0) {
        ret = pidcNattableRowObject1.getAttribute().compareTo(pidcNattableRowObject2.getAttribute(),
            ApicConstants.SORT_ATTRNAME);
      }
      return ret;
    };
  }

  private SortedSet<com.bosch.caltool.icdm.model.apic.attr.Attribute> fillNattableRowObjects() {
    SortedSet<com.bosch.caltool.icdm.model.apic.attr.Attribute> attributesInput = new TreeSet<>();
    Map<Long, IProjectAttribute> pidcAttrs = this.projObjBO.getAttributesAll();
    if (null != pidcAttrs) {
      for (IProjectAttribute projAttr : pidcAttrs.values()) {
        if (this.pidcDataHandler.getAttributeMap().get(projAttr.getAttrId()) != null) {

          attributesInput.add(this.pidcDataHandler.getAttributeMap().get(projAttr.getAttrId()));
        }
      }
    }

    this.compareRowObjects = new TreeSet<>();
    this.totTableRowCount = fillNattableRowObjForVisibleAttrs(attributesInput, pidcAttrs);
    return attributesInput;
  }


  /**
   * @param attributesInput
   * @param visibileAttrCount
   * @param pidcAttrs
   * @return
   */
  private int fillNattableRowObjForVisibleAttrs(
      final SortedSet<com.bosch.caltool.icdm.model.apic.attr.Attribute> attributesInput,
      final Map<Long, IProjectAttribute> pidcAttrs) {
    int visibileAttrCount = 0;
    for (Attribute attribute : attributesInput) {
      // If check added to prevent invalid rows for Not required attributes
      if ((CommonUtils.isNotNull(attribute) && attribute.isDeleted()) || checkAttrLvl(attribute)) {
        continue;
      }
      PidcNattableRowObject compareRowObj = createRowObject(attribute, pidcAttrs);
      this.compareRowObjects.add(compareRowObj);
      if (compareRowObj.getProjectAttributeHandler().isVisible()) {
        visibileAttrCount++;
      }
    }
    return visibileAttrCount;
  }


  /**
   * @param attribute
   * @return
   */
  private boolean checkAttrLvl(final Attribute attribute) {
    return (attribute.getLevel() == ApicConstants.PROJECT_NAME_ATTR) ||
        (attribute.getLevel() == ApicConstants.VARIANT_CODE_ATTR) ||
        (attribute.getLevel() == ApicConstants.SUB_VARIANT_CODE_ATTR);
  }

  private PidcNattableRowObject createRowObject(final Attribute attribute,
      final Map<Long, IProjectAttribute> pidcAttrs) {
    IProjectAttribute projAttribute = null;
    if (this.projObjBO instanceof PidcVersionBO) {
      projAttribute = pidcAttrs.get(attribute.getId());
      this.projectAttrHandler =
          new PidcVersionAttributeBO((PidcVersionAttribute) projAttribute, (PidcVersionBO) this.projObjBO);
    }
    else if (this.projObjBO instanceof PidcVariantBO) {
      projAttribute =
          this.pidcDataHandler.getVariantAttributeMap().get(this.selectedPidcVariant.getId()).get(attribute.getId());
      this.projectAttrHandler =
          new PidcVariantAttributeBO((PidcVariantAttribute) projAttribute, (PidcVariantBO) this.projObjBO);
    }
    else if (this.projObjBO instanceof PidcSubVariantBO) {
      projAttribute = this.pidcDataHandler.getSubVariantAttributeMap().get(this.selectedPidcSubVariant.getId())
          .get(attribute.getId());
      this.projectAttrHandler =
          new PidcSubVariantAttributeBO((PidcSubVariantAttribute) projAttribute, (PidcSubVariantBO) this.projObjBO);
    }
    PidcNattableRowObject compareRowObject = new PidcNattableRowObject(this.projectAttrHandler, this.pidcDataHandler);
    compareRowObject.setAttribute(attribute);
    compareRowObject.addProjectAttribute(projAttribute);

    return compareRowObject;
  }


  /**
   * Method to add checkboxes to columns
   *
   * @param columnLabelAccumulator
   */
  private void registerConfigLabelsOnColumns(final ColumnOverrideLabelAccumulator columnLabelAccumulator) {

    for (int checkBoxColumn : PIDCAttrPage.checkBoxColumns) {
      columnLabelAccumulator.registerColumnOverrides(checkBoxColumn, CHECK_BOX_EDITOR_CNG_LBL + "_" + checkBoxColumn,
          CHECK_BOX_CONFIG_LABEL + "_" + checkBoxColumn);
    }
  }

  /**
   * Registering value edit icon
   *
   * @param configRegistry
   */
  private void registerValueEditIcon(final IConfigRegistry configRegistry) {
    Image editImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.EDIT_12X12);
    ICellPainter bgImagePainter =
        new CellPainterDecorator(new TextPainter(), CellEdgeEnum.BOTTOM, new ImagePainter(editImage));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, bgImagePainter, NORMAL,
        ApicConstants.CONFIG_LABEL_VALUE_EDIT);
  }


  /**
   * Registering check box editor
   *
   * @param configRegistry
   */
  private void registerCheckBoxEditor(final IConfigRegistry configRegistry) {


    for (final int columnIndex : PIDCAttrPage.checkBoxColumns) {

      Style cellStyleUC = new Style();
      cellStyleUC.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
      configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyleUC, NORMAL,
          CHECK_BOX_CONFIG_LABEL + "_" + columnIndex);
      configRegistry.registerConfigAttribute(CELL_PAINTER, new CheckBoxPainter(), NORMAL,
          CHECK_BOX_CONFIG_LABEL + "_" + columnIndex);
      configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER,
          new DefaultBooleanDisplayConverter(), NORMAL, CHECK_BOX_CONFIG_LABEL + "_" + columnIndex);
      PIDCCompareNatTableCheckBoxCellEditor checkBoxCellEditor = new PIDCCompareNatTableCheckBoxCellEditor() {

        /**
         * {@inheritDoc}
         */
        @Override
        protected Control activateCell(final Composite parent, final Object originalCanonicalValue) {
          IStructuredSelection selection = (IStructuredSelection) PIDCAttrPage.this.selectionProvider.getSelection();
          Object firstElement = selection.getFirstElement();
          if (null != firstElement) {
            boolean varSubVar = false;
            final PidcNattableRowObject compareRowObject = (PidcNattableRowObject) selection.getFirstElement();
            final PidcColumnDataMapper columnDataMapper = compareRowObject.getColumnDataMapper();

            final IProjectAttribute ipidcAttribute =
                (null == columnDataMapper.getColumnIndexPIDCAttrMap().get(columnIndex))
                    ? compareRowObject.getProjectAttributeHandler().getProjectAttr()
                    : columnDataMapper.getColumnIndexPIDCAttrMap().get(columnIndex);
            AbstractProjectAttributeBO handler = null;
            if (ipidcAttribute instanceof PidcVersionAttribute) {
              handler = new PidcVersionAttributeBO((PidcVersionAttribute) ipidcAttribute,
                  (PidcVersionBO) PIDCAttrPage.this.projObjBO);
            }
            else if (ipidcAttribute instanceof PidcVariantAttribute) {
              handler = new PidcVariantAttributeBO((PidcVariantAttribute) ipidcAttribute,
                  (PidcVariantBO) PIDCAttrPage.this.projObjBO);
              varSubVar = true;
            }
            else {
              handler = new PidcSubVariantAttributeBO((PidcSubVariantAttribute) ipidcAttribute,
                  (PidcSubVariantBO) PIDCAttrPage.this.projObjBO);
              varSubVar = true;
            }

            CurrentUserBO currUser = new CurrentUserBO();
            ApicDataBO apicBo = new ApicDataBO();
            try {
              if (isPidcLockedInSessionIsUserWrite(currUser, apicBo) &&
                  !isFmVcdmAtVarSubvarLvl(columnIndex, varSubVar) &&
                  (PIDCAttrPage.this.clickEventVal == LEFT_MOUSE_CLICK)) {
                final PIDCActionSet pidcActionSet = new PIDCActionSet();
                // clickEventValue is set to 0 in order to prevent the message dialog to appear again due to mouse
                // up/down event
                PIDCAttrPage.this.clickEventVal = 0;
                pidcActionSet.showUnlockPidcDialog(PIDCAttrPage.this.selectedPidcVersion);
              }
            }
            catch (ApicWebServiceException exp) {
              CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
            }
            if (apicBo.isPidcUnlockedInSession(PIDCAttrPage.this.selectedPidcVersion)) {
              if (!handler.isModifiable() || !handler.isVisible()) {
                CDMLogger.getInstance().errorDialog("Cannot edit this attribute", Activator.PLUGIN_ID);
                return super.createEditorControlWithoutInversion(parent);
              }

              final String flagName = columnDataMapper.getColumnIndexFlagMap().get(columnIndex);


              final PIDCPageEditUtil pidcPageEditUtil = new PIDCPageEditUtil(PIDCAttrPage.this.projObjBO);
              Runnable busyRunnable =
                  () -> callCheckBoxEditor(columnIndex, columnDataMapper, flagName, ipidcAttribute, pidcPageEditUtil);

              BusyIndicator.showWhile(PlatformUI.getWorkbench().getDisplay(), busyRunnable);
            }
          }
          return super.activateCell(parent, originalCanonicalValue);
        }
      };
      configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, checkBoxCellEditor, DisplayMode.EDIT,
          CHECK_BOX_EDITOR_CNG_LBL + "_" + columnIndex);
    }
  }


  /**
   * @param columnIndex
   * @param varSubVar
   * @return
   */
  private boolean isFmVcdmAtVarSubvarLvl(final int columnIndex, final boolean varSubVar) {
    return (varSubVar && ((COLUMN_NUM_FM == columnIndex) || (COLUMN_NUM_VCDM == columnIndex)));
  }

  /**
   * @param currUser
   * @param apicBo
   * @return
   * @throws ApicWebServiceException
   */
  private boolean isPidcLockedInSessionIsUserWrite(final CurrentUserBO currUser, final ApicDataBO apicBo)
      throws ApicWebServiceException {
    return !apicBo.isPidcUnlockedInSession(PIDCAttrPage.this.selectedPidcVersion) &&
        currUser.hasNodeWriteAccess(PIDCAttrPage.this.selectedPidcVersion.getPidcId());
  }

  /**
   * When an insert happens in db the IPIDCAttribute used by nattable is replaced with another IPIDCAttribute with the
   * new pidcAttrID,so this new pidcattribute needs to be updated in the nattable data
   *
   * @param columnIndex
   * @param columnDataMapper
   * @param flagName
   * @param ipidcAttribute
   * @param pidcPageEditUtil
   */
  private void callCheckBoxEditor(final Integer columnIndex, final PidcColumnDataMapper columnDataMapper,
      final String flagName, final IProjectAttribute ipidcAttribute, final PIDCPageEditUtil pidcPageEditUtil) {
    if (flagName.equals(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType())) {
      pidcPageEditUtil.editProjectAttributeNotDefinedFlag(ipidcAttribute);
    }
    else if (flagName.equals(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType())) {
      boolean newSelectedUsedValue = !(boolean) columnDataMapper.getColumnData(columnIndex);
      pidcPageEditUtil.editProjectAttributeNotUsedInfo(ipidcAttribute, Boolean.toString(newSelectedUsedValue));
    }
    else if (flagName.equals(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType())) {
      boolean newSelectedUsedValue = !(boolean) columnDataMapper.getColumnData(columnIndex);
      pidcPageEditUtil.editProjectAttributeUsedInfo(ipidcAttribute, Boolean.toString(newSelectedUsedValue));

    }
    // column is editable only at pidc version level
    else if (flagName.equals(ApicConstants.LABEL_VCDM_FLAG) && (ipidcAttribute instanceof PidcVersionAttribute)) {
      boolean newSelectedvCDMValue = !(boolean) columnDataMapper.getColumnData(columnIndex);
      pidcPageEditUtil.editTransferToVcdmFlag(ipidcAttribute, newSelectedvCDMValue);

    }
    // column is editable only at pidc version level
    else if (flagName.equals(ApicConstants.LABEL_FM_RELEVANT_FLAG) &&
        (ipidcAttribute instanceof PidcVersionAttribute)) {
      boolean newSelectedFMValue = !(boolean) columnDataMapper.getColumnData(columnIndex);
      pidcPageEditUtil.editFMRelevantFlag(ipidcAttribute, newSelectedFMValue, getEditorInput().getFmDataHandler());

    }
  }


  private static class FilterRowCustomConfiguration extends AbstractRegistryConfiguration {

    final DefaultDoubleDisplayConverter doubleDisplayConverter = new DefaultDoubleDisplayConverter();

    @Override
    public void configureRegistry(final IConfigRegistry configRegistry) {
      // override the default filter row configuration for painter
      configRegistry.registerConfigAttribute(CELL_PAINTER,
          new FilterRowPainter(new FilterIconPainter(GUIHelper.getImage("filter"))), NORMAL, FILTER_ROW);

      List<String> comboList = Arrays.asList("True", "False");
      // register a combo box cell editor for the Diff column in the filter row
      // the label is set automatically to the value of
      ICellEditor comboBoxCellEditor = new ComboBoxCellEditor(comboList);
      configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, comboBoxCellEditor, NORMAL,
          FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 4);
      configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, comboBoxCellEditor, NORMAL,
          FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 5);
      configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, comboBoxCellEditor, NORMAL,
          FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 3);
      configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, comboBoxCellEditor, NORMAL,
          FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 8);
      configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, comboBoxCellEditor, NORMAL,
          FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 9);
    }
  }


  /**
   * Method to group nattable columns
   */
  private void groupColumns() {

    int[] addInfoColumns = new int[] { 10, 11, 12, 13, 14, 15, 16 };


    ColumnGroupModel columnGroupModel = this.pidcFilterGridLayer.getColumnGroupModel();


    String addInfoCol = "Additional Info";

    columnGroupModel.addColumnsIndexesToGroup(addInfoCol, addInfoColumns);
    columnGroupModel.setColumnGroupCollapseable(addInfoColumns[0], true);
    columnGroupModel.setStaticColumnIndexesByGroup(addInfoCol, new int[] { addInfoColumns[addInfoColumns.length - 5] });
    this.pidcFilterGridLayer.getColumnGroupHeaderLayer().setGroupAsCollapsed(addInfoColumns[0]);


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
   * action class value editing by mouse click
   */
  public class ValueEditNatMouseClickAction implements IMouseClickAction {

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(final NatTable natTbl, final MouseEvent event) {
      // NA
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isExclusive() {
      // NA
      return false;
    }
  }

  class CustomPIDCCompareHeaderDataProvider implements IDataProvider {


    /**
     * @param columnIndex int
     * @return String
     */
    public String getColumnHeaderLabel(final int columnIndex) {
      String columnHeaderLabel = PIDCAttrPage.this.propertyToLabelMap.get(columnIndex);

      return columnHeaderLabel == null ? "" : columnHeaderLabel;
    }

    @Override
    public int getColumnCount() {
      return PIDCAttrPage.this.propertyToLabelMap.size();
    }

    @Override
    public int getRowCount() {
      return 1;
    }

    /**
     * This class does not support multiple rows in the column header layer.
     */
    @Override
    public Object getDataValue(final int columnIndex, final int rowIndex) {
      return getColumnHeaderLabel(columnIndex);
    }

    @Override
    public void setDataValue(final int columnIndex, final int rowIndex, final Object newValue) {
      throw new UnsupportedOperationException();
    }

  }

  /**
   * @author pdh2cob
   */
  public class PidcNatInputToColumnConverter extends AbstractNatInputToColumnConverter {

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getColumnValue(final Object evaluateObj, final int colIndex) {
      Object result = null;
      if (evaluateObj instanceof PidcNattableRowObject) {
        result = getAttributeData((PidcNattableRowObject) evaluateObj, colIndex);
      }
      return result;
    }


    /**
     * @param evaluateObj
     * @param colIndex
     * @return
     * @throws ParseException
     */
    private Object getAttributeData(final PidcNattableRowObject compareRowObject, final int colIndex) {
      Object result = null;
      switch (colIndex) {
        case 0:
          break;
        case 1:
          result = compareRowObject.getAttribute().getName();
          break;
        case 2:
          result = compareRowObject.getAttribute().getDescription();
          break;
        default:
          result = compareRowObject.getColumnDataMapper().getColumnData(colIndex);
          break;
      }
      return result;
    }
  }

  class CustomPIDCCompareColumnPropertyAccessor<T> implements IColumnAccessor<T> {

    /**
     * This method has been overridden so that it returns the passed row object. The above behavior is required for use
     * of custom comparators for sorting which requires the Row object to be passed without converting to a particular
     * column String value {@inheritDoc}
     */
    @Override
    public Object getDataValue(final T compareRowObject, final int columnIndex) {
      return compareRowObject;
    }


    @Override
    public void setDataValue(final T sysConstNatModel, final int columnIndex, final Object newValue) {
      // NA
    }

    @Override
    public int getColumnCount() {
      return PIDCAttrPage.this.propertyToLabelMap.size();
    }


  }


  /**
   * @return
   */
  private Comparator<PidcNattableRowObject> getComparePIDCComparator(final int columnNum) {

    return (final PidcNattableRowObject cmpRowObj1, final PidcNattableRowObject cmpRowObj2) -> {
      int ret = 0;
      switch (columnNum) {
        case 0:
          // No compare for ball image column
          break;
        case 1:
          ret = cmpRowObj1.getAttribute().compareTo(cmpRowObj2.getAttribute(), ApicConstants.SORT_ATTRNAME);
          break;
        case 2:
          ret = cmpRowObj1.getAttribute().compareTo(cmpRowObj2.getAttribute(), ApicConstants.SORT_ATTRDESCR);
          break;
        default:
          ret = comparePIDCAttribute(cmpRowObj1, cmpRowObj2, columnNum);
      }
      return ret;
    };
  }

  private int comparePIDCAttribute(final PidcNattableRowObject cmpRowObj1, final PidcNattableRowObject cmpRowObj2,
      final int col) {
    IProjectAttribute ipidcAttribute1 = cmpRowObj1.getColumnDataMapper().getColumnIndexPIDCAttrMap().get(col);
    IProjectAttribute ipidcAttribute2 = cmpRowObj2.getColumnDataMapper().getColumnIndexPIDCAttrMap().get(col);
    String columnHeader = cmpRowObj1.getColumnDataMapper().getColumnIndexFlagMap().get(col);
    int returnValue;
    switch (columnHeader) {

      case ApicConstants.VALUE_TEXT:
        returnValue = this.projObjBO.compare(ipidcAttribute1, ipidcAttribute2, ApicConstants.SORT_VALUE);
        break;
      case ApicConstants.CHARACTERISTIC:
        returnValue = this.projObjBO.compare(ipidcAttribute1, ipidcAttribute2, ApicConstants.SORT_CHAR);
        break;
      case ApicConstants.PART_NUMBER:
        returnValue = this.projObjBO.compare(ipidcAttribute1, ipidcAttribute2, ApicConstants.SORT_PART_NUMBER);
        break;
      case ApicConstants.SPECIFICATION:
        returnValue = this.projObjBO.compare(ipidcAttribute1, ipidcAttribute2, ApicConstants.SORT_SPEC_LINK);
        break;
      case ApicConstants.COMMENT:
        returnValue = this.projObjBO.compare(ipidcAttribute1, ipidcAttribute2, ApicConstants.SORT_DESC);
        break;
      case ApicConstants.MODIFIED_DATE:
        returnValue = this.projObjBO.compare(ipidcAttribute1, ipidcAttribute2, ApicConstants.SORT_MODIFIED_DATE);
        break;
      case ApicConstants.CHARVAL:
        returnValue = this.projObjBO.compare(ipidcAttribute1, ipidcAttribute2, ApicConstants.SORT_CHAR_VAL);
        break;
      default:
        returnValue = getDefaultValue(cmpRowObj1, cmpRowObj2, col);
    }
    return returnValue;
  }

  /**
   * @param cmpRowObj1
   * @param cmpRowObj2
   * @param col
   * @param returnValue
   * @return
   * @throws ParseException
   */
  private static int getDefaultValue(final PidcNattableRowObject cmpRowObj1, final PidcNattableRowObject cmpRowObj2,
      final int col) {
    int returnValue = 0;
    Object columnValue1 = cmpRowObj1.getColumnDataMapper().getColumnData(col);
    Object columnValue2 = cmpRowObj2.getColumnDataMapper().getColumnData(col);
    columnValue1 = columnValue1 == null ? "" : columnValue1;
    columnValue2 = columnValue2 == null ? "" : columnValue2;
    if ((columnValue1 instanceof String) && (columnValue2 instanceof String)) {
      returnValue = ((String) columnValue1).compareTo((String) columnValue2);
    }
    return returnValue;
  }


  private Map<Integer, Integer> createNatTableColumns() {

    this.propertyToLabelMap = new HashMap<>();

    Map<Integer, Integer> columnWidthMap = new HashMap<>();

    return configureColumnsNATTable(columnWidthMap);
  }

  /**
   * Configure columns NAT table.
   *
   * @param propertyToLabelMap2 the property to label map 2
   * @param columnWidthMap      the column width map
   */
  private Map<Integer, Integer> configureColumnsNATTable(final Map<Integer, Integer> columnWidthMap) {

    this.propertyToLabelMap.put(COLUMN_NUM_BALL, "");
    this.propertyToLabelMap.put(COLUMN_NUM_ATTRIBUTE, "Attribute");
    this.propertyToLabelMap.put(COLUMN_NUM_DESCRIPTION, "Description");
    this.propertyToLabelMap.put(COLUMN_NUM_USED_QUES, "???");
    this.propertyToLabelMap.put(COLUMN_NUM_USED_YES, "Yes");
    this.propertyToLabelMap.put(COLUMN_NUM_USED_NO, "No");
    this.propertyToLabelMap.put(COLUMN_NUM_VALUE, "Value");
    this.propertyToLabelMap.put(COLUMN_NUM_VALUE_ICON, "");
    this.propertyToLabelMap.put(COLUMN_NUM_VCDM, "vCDM");
    this.propertyToLabelMap.put(COLUMN_NUM_FM, "FM");
    this.propertyToLabelMap.put(COLUMN_NUM_PART_NUMBER, "Part Number");
    this.propertyToLabelMap.put(COLUMN_NUM_SPECIFICATION, "Specification");
    this.propertyToLabelMap.put(COLUMN_NUM_COMMENT, "Comment");
    this.propertyToLabelMap.put(COLUMN_NUM_MODIFIED_DATE, "Modified Date");
    this.propertyToLabelMap.put(COLUMN_NUM_ATTRIBUTE_CLASS, "Attribute Class");
    this.propertyToLabelMap.put(COLUMN_NUM_VALUE_CLASS, "Value Class");
    this.propertyToLabelMap.put(COLUMN_NUM_ATTRIBUTE_ON_DATE, "Attribute On Date");
    this.propertyToLabelMap.put(COLUMN_NUM_GROUP, "Group");
    this.propertyToLabelMap.put(COLUMN_NUM_SUPERGROUP, "Super Group");


    columnWidthMap.put(COLUMN_NUM_BALL, 30);
    columnWidthMap.put(COLUMN_NUM_ATTRIBUTE, 175);
    columnWidthMap.put(COLUMN_NUM_DESCRIPTION, 175);
    columnWidthMap.put(COLUMN_NUM_USED_QUES, 30);
    columnWidthMap.put(COLUMN_NUM_USED_YES, 30);
    columnWidthMap.put(COLUMN_NUM_USED_NO, 30);
    columnWidthMap.put(COLUMN_NUM_VALUE, 100);
    columnWidthMap.put(COLUMN_NUM_VALUE_ICON, 30);
    columnWidthMap.put(COLUMN_NUM_VCDM, 45);
    columnWidthMap.put(COLUMN_NUM_FM, 35);
    columnWidthMap.put(COLUMN_NUM_PART_NUMBER, 100);
    columnWidthMap.put(COLUMN_NUM_SPECIFICATION, 150);
    columnWidthMap.put(COLUMN_NUM_COMMENT, 150);
    columnWidthMap.put(COLUMN_NUM_MODIFIED_DATE, 150);
    columnWidthMap.put(COLUMN_NUM_ATTRIBUTE_CLASS, 150);
    columnWidthMap.put(COLUMN_NUM_VALUE_CLASS, 150);
    columnWidthMap.put(COLUMN_NUM_ATTRIBUTE_ON_DATE, 150);
    columnWidthMap.put(COLUMN_NUM_GROUP, 100);
    columnWidthMap.put(COLUMN_NUM_SUPERGROUP, 100);


    return columnWidthMap;


  }

  /**
   */
  public void updateOverAllRiskDetails() {
    if (this.isVarNodeSelected) {
      this.pidcStatisticsLabel.setText(constructStatisticalInfoForVarAttr(this.selectedPidcVariant.getId()));
    }
    else if (this.isSubVarNodeSelected) {
      this.pidcStatisticsLabel.setText(constructStatisticalInfoForSubVarAttr(this.selectedPidcSubVariant.getId()));
    }
    else {
      this.pidcStatisticsLabel.setText(constructStatisticalInfoForAttr());
    }
    this.pidcStatisticsLabel.redraw();
    this.pidcStatisticsLabel.setEditable(false);
  }


  /**
   * display warning message for non mandate and Uncleared and deleted
   */
  private void displayWarnMessageForUndefinedAttr() {
    StringBuilder warnDialogMsg = new StringBuilder(SB_WARNMSG_SIZE);
    warnDialogMsg.append(this.selectedPidcVersion.getName());

    // check if quotation related warning dialog is required
    boolean projectStatusQuotation = isProjectStatusQuotation();
    boolean allQuotationAttrDefined = projectStatusQuotation && this.pidcVersionBO.isAllQuotationAttrDefined();
    boolean isWarningDialogForQuotReq = projectStatusQuotation && !allQuotationAttrDefined;

    // check if mandatory attr related warning dialog is required
    boolean allMandatoryAttrDefined = this.pidcVersionBO.isAllMandatoryAttrDefined();
    boolean isWarningDialogForMandAttrReq =
        projectStatusQuotation && allQuotationAttrDefined && !allMandatoryAttrDefined;

    // check if mandatory or uc attr related warning dialog is required
    boolean isMandOrUcAttrUndefined = !allMandatoryAttrDefined || this.pidcVersionBO.isProjUseCaseAttrNotDefined();
    boolean isUcAndMandAttrWarningReq = !projectStatusQuotation && isMandOrUcAttrUndefined;

    // check for deleted/ uncleared attr values
    boolean hasInvalidAttrValues = this.pidcVersionBO.hasInvalidAttrValues();

    this.pidcVersionBO.getPidcDataHandler().setQuotAttrWarningReq(isWarningDialogForQuotReq);
    this.pidcVersionBO.getPidcDataHandler().setMandAttrWarningReq(isWarningDialogForMandAttrReq);
    this.pidcVersionBO.getPidcDataHandler().setMandAndUcAttrWarningReq(isUcAndMandAttrWarningReq);

    if (isWarningDialogForQuotReq || isWarningDialogForMandAttrReq || isUcAndMandAttrWarningReq ||
        hasInvalidAttrValues) {

      if (isWarningDialogForQuotReq) {
        warnDialogMsg.append(" - Not all quotation relevant attributes are defined");
      }
      else if (isWarningDialogForMandAttrReq) {
        warnDialogMsg.append(" - Not all mandatory attributes are defined");
      }
      else if (isUcAndMandAttrWarningReq) {
        warnDialogMsg.append(" - Not all mandatory and/or project use case attributes are defined");
      }
      warnDialogMsg.append("\n");

      if (hasInvalidAttrValues) {
        warnDialogMsg.append("Project ID Card has Deleted values");
      }
      warnDialogMsg.append("\nNote: An attribute is considered as defined if")
          .append("\n1. it is used (Used = Yes) and a value is set").append("\n2. it is not set (Used = No)");
      if (!warnDialogMsg.toString().isEmpty()) {
        displayWarningMessage(warnDialogMsg.toString(), isWarningDialogForQuotReq, isWarningDialogForMandAttrReq,
            isUcAndMandAttrWarningReq);
      }
    }
  }

  private void displayWarningMessage(final String warnDialogMsg, final boolean isQuotAttrWarningReq,
      final boolean isMandAttrWarningReq, final boolean isUcAndMandAttrWarningReq) {

    PIDCEditorInput pidcEditorInput = this.editor.getEditorInput();
    if (pidcEditorInput.isInvokedFromURIClient()) {
      CDMLogger.getInstance().warn(warnDialogMsg, Activator.PLUGIN_ID);
      CommonUiUtils.forceActive(this.form.getShell());
    }
    if (!this.isNotShowWarnDialog) {
      MessageDialog warningMessageDialog = new MessageDialog(Display.getCurrent().getActiveShell(), "iCDM Warning",
          null, warnDialogMsg.replace("&", "&&"), MessageDialog.WARNING, new String[] { "No Filter", "Filter" }, 1);
      int returnCode = warningMessageDialog.open();
      if (returnCode != 0) {
        this.filterUndefinedAttr = true;
        setFilterActions(isQuotAttrWarningReq, isMandAttrWarningReq, isUcAndMandAttrWarningReq);

      }
      else {
        this.filterUndefinedAttr = false;
      }
    }

  }

  private void setFilterActions(final boolean isQuotAttrWarningReq, final boolean isMandAttrWarningReq,
      final boolean isUcAndMandAttrWarningReq) {

    if (isQuotAttrWarningReq) {
      this.toolBarActionSet.getQuotNotRelevantAction().setChecked(false);
      this.toolBarActionSet.getQuotNotRelevantAction().run();
    }
    else if (isMandAttrWarningReq) {
      this.toolBarActionSet.getAttrNonMandtoryAction().setChecked(false);
      this.toolBarActionSet.getAttrNonMandtoryAction().run();
    }
    else if (isUcAndMandAttrWarningReq) {
      this.toolBarActionSet.getNonUsecaseAndMandatoryAttrAction().setChecked(false);
      this.toolBarActionSet.getNonUsecaseAndMandatoryAttrAction().run();
    }
    this.toolBarActionSet.getAttrNotUsedAction().setChecked(false);
    this.toolBarActionSet.getAttrNotUsedAction().run();
    this.toolBarActionSet.getDefinedAction().setChecked(false);
    this.toolBarActionSet.getDefinedAction().run();
  }

  private boolean isProjectStatusQuotation() {
    boolean isProjectStatusQuot = false;
    CommonDataBO commonDataBO = new CommonDataBO();
    try {
      String quotationAttrString = commonDataBO.getParameterValue(CommonParamKey.QUOT_ATTR_ID);
      if (CommonUtils.isNotEmptyString(quotationAttrString)) {
        PidcVersionAttribute quotPidcAttr =
            this.pidcDataHandler.getPidcVersAttrMap().get(Long.valueOf(quotationAttrString));
        if (quotPidcAttr != null) {
          String quotValueIdStr = commonDataBO.getParameterValue(CommonParamKey.QUOT_VALUE_IDS);

          List<Long> quotValueIdList = Arrays.asList((quotValueIdStr.trim()).split(",")).stream()
              .mapToLong(Long::parseLong).boxed().collect(Collectors.toList());

          if (quotPidcAttr.getValueId() != null) {
            isProjectStatusQuot = quotValueIdList.contains(quotPidcAttr.getValueId());
          }
        }
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return isProjectStatusQuot;
  }


  /**
   * /** Set the selected attribtue for history view
   *
   * @param pidcAttr IProjectAttribute
   */
  public void setSelectedAttrHistoryView(final IProjectAttribute pidcAttr) {
    if (null != PIDCAttrPage.this.historyView) {
      PIDCAttrPage.this.historyView.setSelectedPidcAttr(pidcAttr);

      for (ViewerFilter filter : PIDCAttrPage.this.historyView.getHistoryTableViewer().getFilters()) {
        if (filter instanceof PIDCHistoryToolBarFilters) {
          PIDCHistoryToolBarFilters pidcHistoryToolBarFilter = (PIDCHistoryToolBarFilters) filter;
          if (pidcHistoryToolBarFilter.isAttrSyncFlag()) {
            pidcHistoryToolBarFilter.setAttrSyncFlag(true);
            PIDCAttrPage.this.historyView.getHistoryTableViewer().refresh();
            PIDCAttrPage.this.historyView.setTitleDescription();
          }
        }
      }
    }
  }


  /**
   * This method returns selected PIDC attribute from PIDCAttribute GridTableViewer
   *
   * @param point selected row dimension point
   * @return IPIDCAttribute
   */
  // ICDM-322
  public IProjectAttribute getSelectedPIDCAttr(final Point point) {
    IProjectAttribute pidcAttr = null;
    // Determine which row was selected
    PIDCAttrPage.this.pidcAttrTabViewer.getGrid().selectCell(point);
    final IStructuredSelection selection = (IStructuredSelection) this.pidcAttrTabViewer.getSelection();
    if ((selection != null) && (!selection.isEmpty())) {
      final Object element = selection.getFirstElement();
      if (element instanceof IProjectAttribute) {
        pidcAttr = (IProjectAttribute) element;

      }
    }
    return pidcAttr;
  }


  /**
   * This method creates filter text
   */
  private void addModifyListenerForFilterTxt() {
    this.filterTxt.addModifyListener(event -> {
      final String text = PIDCAttrPage.this.filterTxt.getText().trim();
      PIDCAttrPage.this.allColumnFilterMatcher.setFilterText(text, true);
      PIDCAttrPage.this.pidcFilterGridLayer.getFilterStrategy().applyFilterInAllColumns(text);
      PIDCAttrPage.this.pidcFilterGridLayer.getSortableColumnHeaderLayer()
          .fireLayerEvent(new FilterAppliedEvent(PIDCAttrPage.this.pidcFilterGridLayer.getSortableColumnHeaderLayer()));
      setStatusBarMessage(false, false);
    });
  }

  /**
   * input for status line
   *
   * @param outlineSelection    flag set according to selection made in viewPart or editor.
   * @param pidcDetailSelection flag set according to selection made in viewPart
   */
  public void setStatusBarMessage(final boolean outlineSelection, final boolean pidcDetailSelection) {

    if (this.editor.isAttributePage()) {
      this.editor.updateStatusBar(outlineSelection, pidcDetailSelection, this.totTableRowCount,
          this.pidcFilterGridLayer.getRowHeaderLayer().getPreferredRowCount());
    }

    else if (this.editor.isCocWpPage()) {
      this.editor.getCocWpPage().setStatusBarMessage(false);
    }

  }

  @Override
  public void updateStatusBar(final boolean outlineSelection) {
    super.updateStatusBar(outlineSelection);
    setStatusBarMessage(outlineSelection, false);
  }

  /**
   * Selection changed listener {@inheritDoc}
   */
  @Override
  public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
    if (CommonUtils.isEqual(getSite().getPage().getActiveEditor(), getEditor())) {
      if (part instanceof PIDCDetailsViewPart) {
        pidcDetailsTreeViewerSelListener(selection);
      }
      else if ((part instanceof OutlineViewPart) && (getEditor().getActivePage() == 0)) {
        outLineSelectionListener(selection);
      }
      setStatusBarMessage(true, false);
    }
  }

  /**
   * pidc details tree listener
   *
   * @param selection
   */
  private void pidcDetailsTreeViewerSelListener(final ISelection selection) {
    if ((selection instanceof IStructuredSelection) && !selection.isEmpty()) {
      if (((IStructuredSelection) selection).size() == CommonUIConstants.SINGLE_SELECTION) {
        final Object first = ((IStructuredSelection) selection).getFirstElement();
        pidcDetNodeSelection(first);
      }
    }
    else if (this.pidcVersionBO.hasVirtualStructure()) {
      this.toolBarActionSet.getStructFilAction().setEnabled(true);
      this.toolBarActionSet.getNotStructFilAction().setEnabled(true);
    }

  }

  /**
   * @param first node object
   */
  public void pidcDetNodeSelection(final Object first) {
    if (!CommonUtils.isEqual(this.lastSelection, first)) {
      Long editorPidcVersId =
          ((PIDCEditorInput) getSite().getPage().getActiveEditor().getEditorInput()).getSelectedPidcVersion().getId();
      if ((first instanceof PidcVersion) && (editorPidcVersId.equals(((PidcVersion) first).getId()))) {
        // ICDM 529
        this.toolBarActionSet.setOldPIDSelection(true);

        setPIDCInfo(first);

      }
      else if ((first instanceof PidcVariant) ||
          ((first instanceof PIDCDetailsNode) && ((PIDCDetailsNode) first).isVariantNode())) {
        // ICDM 529
        this.toolBarActionSet.setOldPIDSelection(false);
        setVariantInfo(first);
      }
      // ICDM-121
      else if ((first instanceof PidcSubVariant) &&
          (editorPidcVersId.equals(((PidcSubVariant) first).getPidcVersionId()))) {
        setSubVariantInfo(first);
      }
      else {
        this.toolBarActionSet.getStructFilAction().setEnabled(false);
        this.toolBarActionSet.getNotStructFilAction().setEnabled(false);
      }
    }

    setSelectedNodeInHistoryView(first);
    this.lastSelection = first;


    this.section.layout();
    setStatusBarMessage(false, true);
  }


  /**
   * Set selected node for history view
   *
   * @param first
   */
  private void setSelectedNodeInHistoryView(final Object first) {

    if (null != this.historyView) {
      if ((first instanceof PidcVariant) ||
          ((first instanceof PIDCDetailsNode) && ((PIDCDetailsNode) first).isVariantNode())) {
        this.historyView.setVaraintNodeSelected(true);
        this.historyView.setSubVaraintNodeSelected(false);
        this.historyView.setSelectedPIDCVariant(getSelectedPidcVariant());
      }
      else if (first instanceof PidcSubVariant) {
        this.historyView.setVaraintNodeSelected(false);
        this.historyView.setSubVaraintNodeSelected(true);
        this.historyView.setSelectedPIDCSubVariant(getSelectedPidcSubVariant());
      }
      for (ViewerFilter filter : PIDCAttrPage.this.historyView.getHistoryTableViewer().getFilters()) {
        if (filter instanceof PIDCHistoryToolBarFilters) {
          PIDCHistoryToolBarFilters pidcHistoryToolBarFilter = (PIDCHistoryToolBarFilters) filter;
          if (pidcHistoryToolBarFilter.isLevelSyncFlag()) {
            pidcHistoryToolBarFilter.setLevelSyncFlag(true);
            PIDCAttrPage.this.historyView.getHistoryTableViewer().refresh();
            PIDCAttrPage.this.historyView.setTitleDescription();
          }
        }
      }
    }

  }

  /**
  *
  */
  public void applyOutlineFilter() {
    if (null != this.pidcFilterGridLayer) {
      this.pidcFilterGridLayer.getFilterStrategy().applyOutlineFilterInAllColumns(false);
      this.pidcFilterGridLayer.getSortableColumnHeaderLayer()
          .fireLayerEvent(new FilterAppliedEvent(this.pidcFilterGridLayer.getSortableColumnHeaderLayer()));
    }
  }

  /**
   * This method invokes on the selection of Outline Tree node
   *
   * @param selection
   */
  private void outLineSelectionListener(final ISelection selection) {
    if ((selection == null) || selection.isEmpty()) {// ICDM-2270
      // for empty selection , show all parameters
      this.outLineFilter.setFilterText("");
    }
    if ((selection != null) && !selection.isEmpty() && (selection instanceof IStructuredSelection)) {

      final Object first = ((IStructuredSelection) selection).getFirstElement();
      // Check if selection is SuperGroup
      if (first instanceof AttrSuperGroup) {
        this.outLineFilter.setGroup(false);
        this.outLineFilter.setSuperGroup(true);
        this.outLineFilter.setCommon(false);
        final AttrSuperGroup attrSuperGroup = (AttrSuperGroup) first;
        this.outLineFilter.setFilterText(attrSuperGroup.getName());
        applyOutlineFilter();


        // Check if selection is Group
      }
      else if (first instanceof AttrGroup) {
        this.outLineFilter.setGroup(true);
        this.outLineFilter.setSuperGroup(false);
        final AttrGroup attrGroup = (AttrGroup) first;

        this.outLineFilter.setFilterText(attrGroup.getName());
        applyOutlineFilter();

      }
      // Icdm-296,Icdm-1030
      else if ((first instanceof com.bosch.caltool.icdm.client.bo.apic.AttrRootNode) ||
          (first instanceof UseCaseRootNode) ||
          (first instanceof com.bosch.caltool.icdm.client.bo.uc.ProjFavUcRootNode) ||
          (first instanceof com.bosch.caltool.icdm.client.bo.uc.UserFavUcRootNode)) {
        this.outLineFilter.setGroup(false);
        this.outLineFilter.setSuperGroup(false);
        this.outLineFilter.setCommon(true);

        outlineFilterSelectionForRootNode(first);

      }
      // Icdm-296
      else if (first instanceof IUseCaseItemClientBO) {

        this.iUseCaseItemClientBO = (IUseCaseItemClientBO) first;
        setUsecaseItemOutlineSelection();
        this.outLineFilter.setUseCaseItem(this.iUseCaseItemClientBO);
        applyOutlineFilter();
      }
      // ICDM-1030
      else if (first instanceof FavUseCaseItemNode) {
        this.outLineFilter.setFavUseCaseItem((FavUseCaseItemNode) first);
        applyOutlineFilter();
      }
    }
    if (!this.natTable.isDisposed()) {
      this.natTable.refresh();
    }
  }


  /**
   * @param first
   */
  private void outlineFilterSelectionForRootNode(final Object first) {
    // ICDM-1865
    if (first instanceof AttrRootNode) {
      this.outLineFilter.setFilterText(ApicUiConstants.SYS_VIEW_ROOT_NODE);
      applyOutlineFilter();
    }
    else if (first instanceof UseCaseRootNode) {
      this.outLineFilter.setFilterText(ApicUiConstants.UC_ROOT_NODE);
      applyOutlineFilter();
    }
    else if (first instanceof ProjFavUcRootNode) {
      this.outLineFilter.setFilterText(ApicUiConstants.PROJECT_UC_ROOT_NODE);
      applyOutlineFilter();
    }
    else if (first instanceof UserFavUcRootNode) {
      this.outLineFilter.setFilterText(ApicUiConstants.PRIVATE_UC_ROOT_NODE);
      applyOutlineFilter();
    }
  }


  /**
   *
   */
  private void setUsecaseItemOutlineSelection() {
    if (this.iUseCaseItemClientBO instanceof UsecaseClientBO) {
      this.selectedUsecaseClientBo = (UsecaseClientBO) this.iUseCaseItemClientBO;
      this.selectedUsecaseSectionClientBo = null;
      this.selectedUsecaseGroupClientBo = null;
    }
    else if (this.iUseCaseItemClientBO instanceof UseCaseSectionClientBO) {
      this.selectedUsecaseSectionClientBo = (UseCaseSectionClientBO) this.iUseCaseItemClientBO;
      this.selectedUsecaseClientBo = null;
      this.selectedUsecaseGroupClientBo = null;
    }
    else if (this.iUseCaseItemClientBO instanceof UseCaseGroupClientBO) {
      this.selectedUsecaseGroupClientBo = (UseCaseGroupClientBO) this.iUseCaseItemClientBO;
      this.selectedUsecaseSectionClientBo = null;
      this.selectedUsecaseClientBo = null;
    }
  }

  /**
  *
  */
  public void reconstructNatTable() {

    this.natTable.dispose();
    this.propertyToLabelMap.clear();

    this.pidcFilterGridLayer = null;
    if (this.toolbarManager != null) {
      this.toolbarManager.removeAll();
    }
    if (this.form.getToolBarManager() != null) {
      this.form.getToolBarManager().removeAll();
    }
    createTable();
    // First the form's body is repacked and then the section is repacked
    // Packing in the below manner prevents the disappearance of Filter Field and refreshes the natTable

    if (!this.filterTxt.getText().isEmpty()) {
      this.filterTxt.setText(this.filterTxt.getText());
    }

    if (this.natTable != null) {
      this.natTable.doCommand(new StructuralRefreshCommand());
      this.natTable.doCommand(new VisualRefreshCommand());
      this.natTable.refresh();
    }
    this.form.getBody().pack();
    this.section.layout();
    setStatusBarMessage(false, false);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent dce) {
    this.selectedPidcVersion = this.projObjBO.getPidcVersion();
    // if variant is selected, update selected variant with latest from model
    if (this.isVarNodeSelected) {
      this.selectedPidcVariant =
          this.projObjBO.getPidcDataHandler().getVariantMap().get(this.selectedPidcVariant.getId());
    }
    // if sub variant is selected, update sub selected variant with latest from model
    else if (this.isSubVarNodeSelected) {
      this.selectedPidcSubVariant =
          this.projObjBO.getPidcDataHandler().getSubVariantMap().get(this.selectedPidcSubVariant.getId());
    }

    this.editor.setEditorPartName(this.projObjBO.getPidcVersion().getName());
    this.nonScrollableForm.setText(setTitle());
    this.editor.setFocus();
    // to refresh enabling /disabling of buttons based on access rights change
    this.pidcNotUpToDateAction.enableDisableUpToDateNotUpToDateBtns();
    this.pidcUpToDateAction.enableDisableUpToDateNotUpToDateBtns();
    getPidcLockAction().setActionProperties();
    refreshNatTable();
  }

  /**
   * refresh the table
   */
  private void refreshNatTable() {
    if (this.natTable != null) {
      this.pidcFilterGridLayer.getEventList().clear();

      fillNattableRowObjects();

      this.pidcFilterGridLayer.getEventList().addAll(this.compareRowObjects);
      this.natTable.doCommand(new StructuralRefreshCommand());
      this.natTable.doCommand(new VisualRefreshCommand());
      this.natTable.refresh();
    }
    updateOverAllRiskDetails();
  }

  /**
   * This method sets PIDC information on selection PIDC details treeviewer
   *
   * @param first
   */
  private void setPIDCInfo(final Object first) {
    final PidcVersion selPidcVer = (PidcVersion) first;
    setSelectedPidcVersion(selPidcVer);

    setVaraintNodeSelected(false);
    setSubVaraintNodeSelected(false);
    this.nonScrollableForm.setText(setTitle());
    if (null != this.historyView) {
      this.historyView.setVaraintNodeSelected(false);
      this.historyView.setSubVaraintNodeSelected(false);
    }
    this.toolBarActionSet.getStructFilAction().setEnabled(false);
    this.toolBarActionSet.getNotStructFilAction().setEnabled(false);
    this.toolBarActionSet.getVariantAction().setEnabled(true);
    this.toolBarActionSet.getNonVariantAction().setEnabled(true);


    controlVariantAndNonVariantFiltersPIDC();

    this.projObjBO = this.pidcVersionBO;


    this.toolBarFilters.setProjectBO(this.projObjBO);
    refreshNatTable();

  }

  /**
   * This method sets PIDC Variant information on selection PIDC details treeviewer
   *
   * @param first
   */
  private void setVariantInfo(final Object first) {


    PidcVariant selPIDCVariant = null;
    if (first instanceof PidcVariant) {
      selPIDCVariant = (PidcVariant) first;
    }
    else if ((first instanceof PIDCDetailsNode) && ((PIDCDetailsNode) first).isVariantNode()) {
      PIDCDetailsNode node = (PIDCDetailsNode) first;
      selPIDCVariant = node.getPidcVariant();
    }
    if (selPIDCVariant != null) {
      PidcVersion pidcVer = this.selectedPidcVersion;

      String titleText = pidcVer.getName() + " " + STR_ARROW + selPIDCVariant.getName();
      this.nonScrollableForm.setText(titleText);
      setSelectedPidcVariant(selPIDCVariant);
      setVaraintNodeSelected(true);
      setSubVaraintNodeSelected(false);

      this.toolBarActionSet.getVariantAction().setEnabled(true);
      if (this.pidcVersionBO.hasVirtualStructure()) {
        this.toolBarActionSet.getStructFilAction().setEnabled(true);
        this.toolBarActionSet.getNotStructFilAction().setEnabled(true);
      }
      this.toolBarActionSet.getNonVariantAction().setEnabled(true);
      controlVariantAndNonVariantFiltersVariant();

      this.projObjBO = new PidcVariantBO(pidcVer, selPIDCVariant, this.pidcDataHandler);

      this.toolBarFilters.setProjectBO(this.projObjBO);

      refreshNatTable();
    }
  }

  /**
   * This method controls variant and non variant toolbar filters information while setting PIDC info
   */
  // ICDM-278
  private void controlVariantAndNonVariantFiltersPIDC() {
    // ICDM 529
    this.toolBarActionSet.getVariantAction().setChecked(this.toolBarActionSet.isPidcVarFlag());
    this.toolBarActionSet.getVariantAction().run();
    this.toolBarActionSet.getNonVariantAction().setChecked(this.toolBarActionSet.isPidcNonVarFlag());
    this.toolBarActionSet.getNonVariantAction().run();
  }

  /**
   * This method controls variant and non variant toolbar filters information while setting Variant info
   */
  // ICDM-529
  private void controlVariantAndNonVariantFiltersVariant() {
    this.toolBarActionSet.getVariantAction().setChecked(this.toolBarActionSet.isVarSubvarFlag());
    this.toolBarActionSet.getVariantAction().run();
    this.toolBarActionSet.getNonVariantAction().setChecked(this.toolBarActionSet.isVarNonSubvarFlag());
    this.toolBarActionSet.getNonVariantAction().run();
  }

  /**
   * This method sets PIDC Sub-Variant information on selection PIDC details treeviewer
   *
   * @param first
   */
  // ICDM-121
  private void setSubVariantInfo(final Object first) {
    final PidcSubVariant selPIDCSubVariant = (PidcSubVariant) first;
    PidcVariant pidcVariant = this.pidcDataHandler.getVariantMap().get(selPIDCSubVariant.getPidcVariantId());
    setSelectedPidcVariant(pidcVariant);
    PidcVersion pidcVer = this.selectedPidcVersion;

    String titleText =
        pidcVer.getName() + " " + STR_ARROW + pidcVariant.getName() + STR_ARROW + selPIDCSubVariant.getName();
    this.nonScrollableForm.setText(titleText);
    setVaraintNodeSelected(false);
    setSelectedPidcSubVariant(selPIDCSubVariant);
    setSubVaraintNodeSelected(true);

    // ICDM-278
    this.toolBarActionSet.getVariantAction().setEnabled(false);
    this.toolBarActionSet.getNonVariantAction().setEnabled(false);
    if (this.pidcVersionBO.hasVirtualStructure()) {
      this.toolBarActionSet.getStructFilAction().setEnabled(true);
      this.toolBarActionSet.getNotStructFilAction().setEnabled(true);
    }
    if (!this.toolBarFilters.isNonVariantSel()) {
      this.toolBarFilters.setNonVariantSel(true);
    }
    if (!this.toolBarFilters.isVariantSel()) {
      this.toolBarFilters.setVariantSel(true);
    }

    this.projObjBO = new PidcSubVariantBO(pidcVer, selPIDCSubVariant, this.pidcDataHandler);
    this.toolBarFilters.setProjectBO(this.projObjBO);
    refreshNatTable();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    getSite().getPage().removeSelectionListener(this);
    super.dispose();
  }

  /**
   * @param isVaraintNodeSelected defines variant node is selected or not
   */
  public void setVaraintNodeSelected(final boolean isVaraintNodeSelected) {
    this.isVarNodeSelected = isVaraintNodeSelected;
  }

  /**
   * @return boolean
   */
  public boolean isVaraintNodeSelected() {
    return this.isVarNodeSelected;
  }

  /**
   * @return the isSubVaraintNodeSelected
   */
  // ICDM-121
  public boolean isSubVaraintNodeSelected() {
    return this.isSubVarNodeSelected;
  }

  /**
   * @param isSubVaraintNodeSelected the isSubVaraintNodeSelected to set
   */
  // ICDM-121
  public void setSubVaraintNodeSelected(final boolean isSubVaraintNodeSelected) {
    this.isSubVarNodeSelected = isSubVaraintNodeSelected;
  }


  /**
   * @return the selectedPidcVariant
   */
  public PidcVariant getSelectedPidcVariant() {
    if ((null == this.selectedPidcVariant) && !CommonUtils.isNullOrEmpty(getPidcVersionBO().getVariantsSet(false))) {
      return getPidcVersionBO().getVariantsSet(false).first();
    }
    return this.selectedPidcVariant;
  }


  /**
   * @param selectedPidcVariant the selectedPidcVariant to set
   */
  public void setSelectedPidcVariant(final PidcVariant selectedPidcVariant) {
    this.selectedPidcVariant = selectedPidcVariant;
  }


  /**
   * @return the selectedPidcSubVariant
   */
  public PidcSubVariant getSelectedPidcSubVariant() {
    return this.selectedPidcSubVariant;
  }


  /**
   * @param selectedPidcSubVariant the selectedPidcSubVariant to set
   */
  public void setSelectedPidcSubVariant(final PidcSubVariant selectedPidcSubVariant) {
    this.selectedPidcSubVariant = selectedPidcSubVariant;
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

  /**
   * Resets variant and subvariant selections
   */
  public void resetNodeSelections() {
    setVaraintNodeSelected(false);
    setSubVaraintNodeSelected(false);
    this.selectedPidcVariant = null;
    this.selectedPidcSubVariant = null;
  }

  /**
   * @return Title of the page
   */
  private String setTitle() {

    PidcVersion pidcVers = this.projObjBO.getPidcVersion();
    String strTitle = pidcVers.getName();
    if (this.isVarNodeSelected) {
      strTitle = pidcVers.getName() + " " + STR_ARROW + this.selectedPidcVariant.getName();
    }
    if (this.isSubVarNodeSelected) {
      strTitle = pidcVers.getName() + " " + STR_ARROW + this.selectedPidcVariant.getName() + STR_ARROW +
          this.selectedPidcSubVariant.getName();
    }

    return strTitle;
  }

  // this method is added to prevent
  // "java lang RuntimeException: WARNING: Prevented recursive attempt to activate part
  // org eclipse ui views PropertySheet while still in the middle of activating part"
  /**
   * {@inheritDoc}
   */
  @Override
  public void setFocus() {
    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setFocus();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setActive(final boolean active) {
    final IStructuredSelection selection = (IStructuredSelection) getSelectionProvider().getSelection();
    if ((selection != null) && (!selection.isEmpty())) {
      setSelectedAttrPropertiesView(selection);
    }
    super.setActive(active);
  }

  /**
   * @return the outLineFilter
   */
  public PIDCOutlineFilter getOutLineFilter() {
    return this.outLineFilter;
  }


  /**
   * @return the toolBarActionSet
   */
  public PIDCPageToolBarActionSet getToolBarActionSet() {
    return this.toolBarActionSet;
  }

  /**
   * @deprecated
   */
  /**
   * @return the pidcAttrTabViewer
   */
  @Deprecated
  public GridTableViewer getPidcAttrTabViewer() {
    return this.pidcAttrTabViewer;
  }

  /**
   * @return the section
   */
  public Section getSection() {
    return this.section;
  }

  /**
   * @return set of attribute IDs visible in the nat table
   */
  public Set<Long> getFilteredAttributeIds() {
    Set<Long> retSet = new HashSet<>();
    for (Object row : this.pidcFilterGridLayer.getBodyDataProvider().getList()) {
      retSet.add(((PidcNattableRowObject) row).getAttribute().getId());
    }
    return retSet;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected IToolBarManager getToolBarManager() {
    return this.toolbarManager;
  }

  // ICdm-1357
  /**
   * New Warning message if the user tries to modify the SDOM PVER.
   *
   * @param arg0 arg0
   * @return boolean false, if user select NO for save changes
   */
  public boolean showWarningForSdomPverChange(final Object arg0) {
    IProjectAttribute ipidAttr = (IProjectAttribute) arg0;
    Attribute attr = getPidcVersionBO().getPidcDataHandler().getAttribute(ipidAttr.getAttrId());
    PIDCActionSet aSet = new PIDCActionSet();
    if ((attr != null) && attr.getName().equals(SDOM_PVER_ATTR_NAME) &&
        (aSet.hasCdrResults(this.selectedPidcVersion.getId()))) {

      MessageBox dialog = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
      dialog.setText("Save Changes");

      dialog.setMessage(SDOM_PVER_WARN_STR);
      if (dialog.open() == SWT.NO) {
        return false;
      }


    }
    return true;
  }


  /**
   * @return the historyView
   */
  public PIDCHistoryViewPart getHistoryView() {
    return this.historyView;
  }


  /**
   * @param historyView the historyView to set
   */
  public void setHistoryView(final PIDCHistoryViewPart historyView) {
    this.historyView = historyView;
  }


  /**
   * @param selection
   * @return
   */
  private boolean checkAllVarAttrs(final IStructuredSelection selection) {
    boolean flag = true;
    for (Object obj : selection.toList()) {
      PidcNattableRowObject rowObj = (PidcNattableRowObject) obj;
      IProjectAttribute attr = rowObj.getProjectAttributeHandler().getProjectAttr();
      if (((PidcVariantAttribute) attr).isAtChildLevel()) {
        flag = false;
        break;
      }
    }
    return flag;
  }

  /**
   * @param selection
   * @return
   */
  private boolean checkAllSubVarAttrs(final IStructuredSelection selection) {
    boolean flag = true;
    for (Object obj : selection.toList()) {
      PidcNattableRowObject rowObj = (PidcNattableRowObject) obj;
      IProjectAttribute attr = rowObj.getProjectAttributeHandler().getProjectAttr();
      if (!((PidcVariantAttribute) attr).isAtChildLevel()) {
        flag = false;
        break;
      }
    }
    return flag;
  }

  /**
   * @return the selCellValue
   */
  public String getSelCellValue() {
    return this.selCellValue;
  }

  /**
   * @param selCellValue the selCellValue to set
   */
  public void setSelCellValue(final String selCellValue) {
    this.selCellValue = selCellValue;
  }


  /**
   * @return the pidcStatisticsLabel
   */
  public StyledText getPidcStatisticsLabel() {
    return this.pidcStatisticsLabel;
  }


  /**
   * @return the nonScrollableForm
   */
  public Form getNonScrollableForm() {
    return this.nonScrollableForm;
  }


  /**
   * @return the pidcNotUpToDateAction
   */
  public PIDCUpToDateAction getPidcNotUpToDateAction() {
    return this.pidcNotUpToDateAction;
  }


  /**
   * @return the Project Object BO
   */
  public AbstractProjectObjectBO getProjectObjectBO() {
    return this.projObjBO;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PidcEditorDataHandler getDataHandler() {
    return getEditorInput().getDataHandler();
  }

  /**
   * @return the pidcDataHandler
   */
  public PidcDataHandler getPidcDataHandler() {
    return this.pidcDataHandler;
  }


  /**
   * @return the selectedPidcVersion
   */
  public PidcVersion getSelectedPidcVersion() {
    return this.selectedPidcVersion;
  }


  /**
   * @param selectedPidcVersion the selectedPidcVersion to set
   */
  public void setSelectedPidcVersion(final PidcVersion selectedPidcVersion) {
    this.selectedPidcVersion = selectedPidcVersion;
  }

  /**
   * Mouse listener to the table viewer
   */
  private void addMouseListener() {
    this.natTable.addMouseListener(new MouseListener() {

      @Override
      public void mouseUp(final MouseEvent mouseEvent) {
        // NA
      }

      @Override
      public void mouseDown(final MouseEvent mouseEvent) {
        if (mouseEvent.button == LEFT_MOUSE_CLICK) {
          leftMouseClickAction(mouseEvent);
        }
      }

      @Override
      public void mouseDoubleClick(final MouseEvent mouseEvent) {
        // NA
      }

    });

  }

  /**
   * @param mouseEvent
   */
  private void leftMouseClickAction(final MouseEvent mouseEvent) {
    // store the event
    PIDCAttrPage.this.clickEventVal = LEFT_MOUSE_CLICK;

    ILayerCell cell =
        PIDCAttrPage.this.natTable.getCellByPosition(PIDCAttrPage.this.natTable.getColumnPositionByX(mouseEvent.x),
            PIDCAttrPage.this.natTable.getRowPositionByY(mouseEvent.y));
    if (cell != null) {// cell is null when clicking empty area in nattable
      LabelStack configLabels = cell.getConfigLabels();
      if (isConfigLblHasEditableLbl(configLabels) || isConfigLblHasUsedFlgLbl(configLabels)) {
        // open dialog to edit

        final int col = LayerUtil.convertColumnPosition(PIDCAttrPage.this.natTable,
            PIDCAttrPage.this.natTable.getColumnPositionByX(mouseEvent.x),
            ((CustomFilterGridLayer<PidcNattableRowObject>) PIDCAttrPage.this.natTable.getLayer()).getDummyDataLayer());

        int row = LayerUtil.convertRowPosition(PIDCAttrPage.this.natTable,
            PIDCAttrPage.this.natTable.getRowPositionByY(mouseEvent.y),
            ((CustomFilterGridLayer<PidcNattableRowObject>) PIDCAttrPage.this.natTable.getLayer()).getDummyDataLayer());
        Object rowObject = PIDCAttrPage.this.pidcFilterGridLayer.getBodyDataProvider().getRowObject(row);
        if (rowObject instanceof PidcNattableRowObject) {
          final PidcNattableRowObject compareRowObject = (PidcNattableRowObject) rowObject;
          IProjectAttribute selectedPIDCAttr = compareRowObject.getProjectAttributeHandler().getProjectAttr();
          IProjectAttribute projAttribute = getUpdatedPrjAttr(selectedPIDCAttr);
          Map<IProjectAttribute, IProjectAttribute> predefGrpAttrMap = getPidcVersionBO().getPredefAttrGrpAttrMap();

          openEditableTable(configLabels, col, projAttribute, predefGrpAttrMap);
        }
      }
    }
  }


  /**
   * @param configLabels
   * @param col
   * @param projAttribute
   * @param predefGrpAttrMap
   */
  private void openEditableTable(final LabelStack configLabels, final int col, final IProjectAttribute projAttribute,
      final Map<IProjectAttribute, IProjectAttribute> predefGrpAttrMap) {
    if (CommonUtils.isNotNull(projAttribute)) {
      // check if the projAttribute is a predefined attribute & also check if the predefined attribute is
      // valid for this project
      if (predefGrpAttrMap.containsKey(projAttribute) &&
          isPredefAttrValidForPidcVersion(predefGrpAttrMap.get(projAttribute).getValueId())) {
        CDMLogger.getInstance().infoDialog("Predefined Attribute can not be edited", Activator.PLUGIN_ID);
      }
      else if (isConfigLblHasEditableLbl(configLabels)) {
        editTabItem(col, projAttribute);
      }
    }
  }


  private boolean isConfigLblHasEditableLbl(final LabelStack configLabels) {
    boolean isConfigLblHasEditableLbl = true;
    return configLabels.hasLabel(ApicConstants.CONFIG_LABEL_VALUE_EDIT) ||
        configLabels.hasLabel(ApicConstants.CONFIG_LABEL_HYPERLINK) ? isConfigLblHasEditableLbl
            : !isConfigLblHasEditableLbl;
  }

  private boolean isConfigLblHasUsedFlgLbl(final LabelStack configLabels) {
    boolean isConfigLblHasUsedFlgLbl = true;
    return configLabels.hasLabel(ApicConstants.CONFIG_LABEL_CHECK_BOX3) ||
        configLabels.hasLabel(ApicConstants.CONFIG_LABEL_CHECK_BOX4) ||
        configLabels.hasLabel(ApicConstants.CONFIG_LABEL_CHECK_BOX5) ? isConfigLblHasUsedFlgLbl
            : !isConfigLblHasUsedFlgLbl;
  }

  /**
   * @param pidcVerWithDetails
   */
  private boolean isPredefAttrValidForPidcVersion(final Long groupAttrValId) {
    boolean isPredefAttrValidForPidcVersion = true;
    for (Entry<Long, PredefinedValidity> predefValMapEntry : PIDCAttrPage.this.pidcDataHandler
        .getPredefinedValidityMap().entrySet()) {
      PredefinedValidity preDefVal = predefValMapEntry.getValue();
      if (groupAttrValId.equals(preDefVal.getGrpAttrValId())) {
        isPredefAttrValidForPidcVersion = false;
        PidcVersionAttribute pidcVersAttr =
            PIDCAttrPage.this.pidcDataHandler.getPidcVersAttrMap().get(preDefVal.getValidityAttrId());
        // more than one PredefValidity model will have same grpAttrValId. So should not directly return the result
        if (preDefVal.getValidityValueId().equals(pidcVersAttr.getValueId())) {
          return true;
        }
      }
    }
    return isPredefAttrValidForPidcVersion;
  }

  /**
   * @param selectedPIDCAttr
   * @param projAttribute
   * @return
   */
  private IProjectAttribute getUpdatedPrjAttr(final IProjectAttribute selectedPIDCAttr) {
    IProjectAttribute projAttribute = null;

    if (selectedPIDCAttr instanceof PidcVersionAttribute) {
      projAttribute = PIDCAttrPage.this.pidcVersionBO.getAttributesAll().get(selectedPIDCAttr.getAttrId());

    }
    else if (selectedPIDCAttr instanceof PidcVariantAttribute) {
      projAttribute = PIDCAttrPage.this.pidcDataHandler.getVariantAttributeMap()
          .get(((PidcVariantAttribute) selectedPIDCAttr).getVariantId()).get(selectedPIDCAttr.getAttrId());

    }
    else if (selectedPIDCAttr instanceof PidcSubVariantAttribute) {
      projAttribute = PIDCAttrPage.this.pidcDataHandler.getSubVariantAttributeMap()
          .get(((PidcSubVariantAttribute) selectedPIDCAttr).getSubVariantId()).get(selectedPIDCAttr.getAttrId());

    }
    return projAttribute;
  }

  /**
   * @param columnIndex deines gridviewer column index
   */
  private void editTabItem(final int columnIndex, final IProjectAttribute selectedPIDCAttr) {
    // Edit PIDC attribute value column index is 8
    // ICDM-179
    final IProjectAttribute editableAttr = selectedPIDCAttr;
    AbstractProjectAttributeBO handler = null;
    if (editableAttr instanceof PidcVersionAttribute) {
      handler =
          new PidcVersionAttributeBO((PidcVersionAttribute) editableAttr, (PidcVersionBO) PIDCAttrPage.this.projObjBO);
    }
    else if (editableAttr instanceof PidcVariantAttribute) {
      handler =
          new PidcVariantAttributeBO((PidcVariantAttribute) editableAttr, (PidcVariantBO) PIDCAttrPage.this.projObjBO);
    }
    else {
      handler = new PidcSubVariantAttributeBO((PidcSubVariantAttribute) editableAttr,
          (PidcSubVariantBO) PIDCAttrPage.this.projObjBO);
    }
    if (handler.isHiddenToUser()) {
      CDMLogger.getInstance().infoDialog("Hidden attribute values cannot be viewed without PIDC rights",
          Activator.PLUGIN_ID);
      return;
    }

    CommonActionSet actionSet = new CommonActionSet();
    if ((columnIndex == COLUMN_NUM_VALUE_ICON) && PIDCAttrPage.this.projObjBO.isModifiable()) {

      // ICDM-2487 P1.27.101
      if ((editableAttr != null) && !handler.isModifiable()) {

        CurrentUserBO currUser = new CurrentUserBO();
        ApicDataBO apicBo = new ApicDataBO();
        try {
          if (!apicBo.isPidcUnlockedInSession(getPidcVersionBO().getPidcVersion()) &&
              currUser.hasNodeWriteAccess(getPidcVersionBO().getPidcVersion().getPidcId())) {
            final PIDCActionSet pidcActionSet = new PIDCActionSet();
            boolean resultFlag = pidcActionSet.showUnlockPidcDialog(getPidcVersionBO().getPidcVersion());

            if (!resultFlag) {
              openValueEditDlgInReadOnlyMode(editableAttr);
            }
          }
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }

      // ICDM-2493
      try {
        if (((editableAttr != null) &&
            Long.valueOf(new CommonDataBO().getParameterValue(CommonParamKey.ICDM_QNAIRE_CONFIG_ATTR))
                .equals(editableAttr.getAttrId())) &&
            CommonActionSet.isQnaireConfigModifyErrorMessageShown(editableAttr, getPidcVersionBO())) {
          return;
        }
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }

      // ICDM-479 only visible attr can be edited
      if ((editableAttr != null) && handler.isModifiable() && handler.isVisible() && !editableAttr.isAtChildLevel()) {

        // ICDM-94
        if (!showWarningForSdomPverChange(editableAttr)) {
          return;
        }
        boolean isPverNameValidToModify;
        // ICDM-1482 , Disable SDOM PVER modification, if a2l file mapping is present for the version
        PIDCActionSet aSet = new PIDCActionSet();
        // check if sdom pver name is set and valid
        isPverNameValidToModify = aSet.isPverNameEditable(editableAttr, PIDCAttrPage.this.projObjBO);


        if (validateCustomerNameinCDMAttr(editableAttr) && isPverNameValidToModify) {

          final PIDCAttrValueEditDialog dialog =
              new PIDCAttrValueEditDialog(PIDCAttrPage.this.natTable.getShell(), PIDCAttrPage.this, editableAttr, null);
          dialog.setReadOnlyMode(false);
          dialog.open();
        }
      }
      else {

        CDMLogger.getInstance().info(ApicUiConstants.EDIT_NOT_ALLOWED, Activator.PLUGIN_ID);
        if (!PIDCAttrPage.this.projObjBO.isModifiable()) {
          CDMLogger.getInstance().info(
              ApicUiConstants.NO_WRITE_ACCESS + PIDCAttrPage.this.projObjBO.getPidcVersion().getName(),
              Activator.PLUGIN_ID);
        }
      }
    }
    /*
     * If used column group is expanded the value column index is 6 else if user clicks to show the summary column then
     * value column index is 7
     */
    // ICDM-322
    else if (columnIndex == COLUMN_INDEX_VALUE_IF_EXPANDED) /*
                                                             * || (columnIndex == COLUMN_INDEX_VALUE_IF_EXPANDED)
                                                             */ {
      // ICDM-2529
      actionSet.openLink(editableAttr.getValue());
    }
    else if (columnIndex == PIDCPageConstants.COLUMN_INDEX_SPEC_LINK) {
      if ((null != editableAttr.getSpecLink()) && !editableAttr.getSpecLink().equals(ApicConstants.HIDDEN_VALUE)) {
        // ICDM-2529
        actionSet.openLink(editableAttr.getSpecLink());
      }

    }
    else {
      openValueEditDlgInReadOnlyMode(editableAttr);
      CurrentUserBO currUser = new CurrentUserBO();
      ApicDataBO apicBo = new ApicDataBO();
      CDMLogger.getInstance().info(ApicUiConstants.EDIT_NOT_ALLOWED, Activator.PLUGIN_ID);
      try {
        if (!apicBo.isPidcUnlockedInSession(getPidcVersionBO().getPidcVersion()) &&
            currUser.hasNodeWriteAccess(getPidcVersionBO().getPidcVersion().getPidcId())) {
          final PIDCActionSet pidcActionSet = new PIDCActionSet();
          pidcActionSet.showUnlockPidcDialog(getPidcVersionBO().getPidcVersion());
        }
        else if (!currUser.hasNodeWriteAccess(getPidcVersionBO().getPidcVersion().getPidcId())) {
          CDMLogger.getInstance().info(
              ApicUiConstants.NO_WRITE_ACCESS +
                  getPidcVersionBO().getPidcDataHandler().getPidcVersionInfo().getPidc().getName(),
              Activator.PLUGIN_ID);
        }
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * @param editableAttr
   */
  private boolean validateCustomerNameinCDMAttr(final IProjectAttribute editableAttr) {
    try {
      if (editableAttr.getAttrId().toString()
          .equals(new CommonDataBO().getParameterValue(CommonParamKey.VARIANT_IN_CUST_CDMS_ATTR_ID)) &&
          (editableAttr instanceof PidcVersionAttribute)) {
        boolean yesFlag = MessageDialogUtils.getQuestionMessageDialog("Edit not allowed",
            "This attribute can be edited on variant level only'. Do you want to move it to variant level now? ");
        if (yesFlag) {
          final PIDCActionSet pidcActionSet = new PIDCActionSet();
          pidcActionSet.moveToVar((PidcVersionAttribute) editableAttr, this.selectedPidcVersion, this.pidcVersionBO);
        }
        return false;
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    return true;
  }

  /**
   * @param editableAttr
   */
  private void openValueEditDlgInReadOnlyMode(final IProjectAttribute editableAttr) {
    final PIDCAttrValueEditDialog dialog =
        new PIDCAttrValueEditDialog(this.natTable.getShell(), this, editableAttr, null);
    dialog.setReadOnlyMode(true);
    dialog.open();
  }

  /**
   * @param menuMgr      MenuManager
   * @param actionSet    PIDCActionSet
   * @param editableAttr PIDCAttribute
   * @param handler      PIDCVersionAttributeHandler
   */
  private void addMoveToCommonMenuIfApplicable(final MenuManager menuMgr, final PIDCActionSet actionSet,
      final PidcVersionAttribute editableAttr, final PidcVersionAttributeBO handler) {
    // icdm-138

    if (handler.isVisible() && editableAttr.isAtChildLevel() && getPidcVersionBO().isModifiable() &&
        (CommonUtils.isNotEqual(this.selectedPidcVersion.getPidStatus(), PidcVersionStatus.LOCKED.getDbStatus()))) {
      SortedSet<PidcVariant> varList = getPidcVersionBO().getVariantsSet();
      if (!varList.isEmpty()) {
        PidcVariant pidcVarObj = varList.first();
        PidcVariantAttribute editableAttrVar =
            getPidcDataHandler().getVariantAttributeMap().get(pidcVarObj.getId()).get(editableAttr.getAttrId());

        actionSet.addMoveToCommonMenu(menuMgr, editableAttrVar, this.selectedPidcVersion, pidcVarObj,
            PIDCAttrPage.this);
      }
    }
  }

  /**
   * @param menuMgr                MenuManager
   * @param editableAttr           PIDCAttribute
   * @param PidcVersionAttributeBO handler
   */
  private void addAPRJMenuActionIfApplicable(final MenuManager menuMgr, final PidcVersionAttribute editableAttr,
      final PidcVersionAttributeBO handler) {

    // ICDM-858
    if (handler.isVisible() &&
        (this.pidcDataHandler.getAttribute(editableAttr.getAttrId()).getLevel() == ApicConstants.VCDM_APRJ_NAME_ATTR)) {
      CDMCommonActionSet cdmCommonActionSet = new CDMCommonActionSet();
      cdmCommonActionSet.addAPRJMenuAction(menuMgr,
          (null == this.pidcDataHandler.getAttributeValue(editableAttr.getValueId())) ? ""
              : this.pidcDataHandler.getAttributeValue(editableAttr.getValueId()).getName(),
          null);
    }
  }

  /**
   * @return the pidcVersionBO
   */
  public PidcVersionBO getPidcVersionBO() {
    return this.pidcVersionBO;
  }

  /**
   * @param menuMgr      MenuManager
   * @param actionSet    PIDCActionSet
   * @param cmnActionSet CommonActionSet
   * @param firstElement IProjectAttribute
   */
  private void singlePidcAttrMenu(final MenuManager menuMgr, final PIDCActionSet actionSet,
      final CommonActionSet cmnActionSet, final IProjectAttribute firstElement) {

    final PidcVersionAttribute editableAttr = (PidcVersionAttribute) firstElement;

    PidcVersionAttributeBO handler = new PidcVersionAttributeBO(editableAttr, this.pidcVersionBO);
    if (!handler.isHiddenToUser()) {
      actionSet.addShowAttrValsOption(menuMgr, PIDCAttrPage.this, editableAttr);
    }
    menuMgr.add(new Separator());

    actionSet.addMoveToVariantMenu(menuMgr, editableAttr, this.selectedPidcVersion, this);

    // ICDM-2379
    try {
      Long qnaireConfigAttrId =
          Long.valueOf(new CommonDataBO().getParameterValue(CommonParamKey.ICDM_QNAIRE_CONFIG_ATTR));
      if (this.currentUser.hasApicWriteAccess() && !CommonUtils.isEmptyString(editableAttr.getName()) &&
          !(getPidcDataHandler().getAllLevelAttrMap()
              .containsKey(this.pidcDataHandler.getAttribute(editableAttr.getAttrId()).getLevel())) &&
          !editableAttr.getAttrId().equals(qnaireConfigAttrId)) {
        actionSet.markAttrHiddenMenu(menuMgr, editableAttr, getPidcDataHandler());
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

    addAPRJMenuActionIfApplicable(menuMgr, editableAttr, handler);

    addMoveToCommonMenuIfApplicable(menuMgr, actionSet, editableAttr, handler);

    // ICDM-452
    AttributeClientBO attrClntBO = new AttributeClientBO(getPidcDataHandler().getAttribute(editableAttr.getAttrId()));
    AttributeValueClientBO attrValClntBO =
        new AttributeValueClientBO(getPidcDataHandler().getAttributeValue(editableAttr.getValueId()));
    cmnActionSet.addLinkAction(menuMgr, attrClntBO.getLinks());
    if (null != getPidcDataHandler().getAttributeValue(editableAttr.getValueId())) {
      SortedSet<Link> links = attrValClntBO.getLinks();
      if (!editableAttr.isAtChildLevel() && (null != links) && !links.isEmpty()) {
        menuMgr.add(new Separator());
        cmnActionSet.addValLinkAction(menuMgr, links);
      }
    }
  }


  /**
   * @return the toolBarFilterStateMap
   */
  public Map<String, Boolean> getToolBarFilterStateMap() {
    return this.toolBarFilterStateMap;
  }


  /**
   * @return the pidcUpToDateAction
   */
  public PIDCUpToDateAction getPidcUpToDateAction() {
    return this.pidcUpToDateAction;
  }

  /**
   * @return the filterUndefinedAttr
   */
  public boolean isFilterUndefinedAttr() {
    return this.filterUndefinedAttr;
  }


  /**
   * @return the isPidcLocked
   */
  public boolean isPidcLocked() {
    return this.isPidcLocked;
  }


}
