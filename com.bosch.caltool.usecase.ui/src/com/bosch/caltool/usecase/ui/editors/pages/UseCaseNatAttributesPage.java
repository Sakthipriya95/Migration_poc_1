/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.usecase.ui.editors.pages;

import static org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes.CELL_PAINTER;
import static org.eclipse.nebula.widgets.nattable.grid.GridRegion.FILTER_ROW;
import static org.eclipse.nebula.widgets.nattable.style.DisplayMode.NORMAL;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.columnChooser.ColumnChooserUtils;
import org.eclipse.nebula.widgets.nattable.columnChooser.ColumnEntry;
import org.eclipse.nebula.widgets.nattable.command.StructuralRefreshCommand;
import org.eclipse.nebula.widgets.nattable.command.VisualRefreshCommand;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.config.NullComparator;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.data.IColumnAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultBooleanDisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDoubleDisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterIconPainter;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowDataLayer;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowPainter;
import org.eclipse.nebula.widgets.nattable.filterrow.TextMatchingMode;
import org.eclipse.nebula.widgets.nattable.filterrow.config.FilterRowConfigAttributes;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.nebula.widgets.nattable.freeze.command.FreezeSelectionCommand;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.layer.cell.AggregateConfigLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.layer.event.ColumnInsertEvent;
import org.eclipse.nebula.widgets.nattable.painter.cell.CheckBoxPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ImagePainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.CellPainterDecorator;
import org.eclipse.nebula.widgets.nattable.persistence.command.DisplayPersistenceDialogCommandHandler;
import org.eclipse.nebula.widgets.nattable.reorder.command.MultiColumnReorderCommand;
import org.eclipse.nebula.widgets.nattable.selection.RowSelectionProvider;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.sort.SortConfigAttributes;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.CellStyleUtil;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.ui.menu.HeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuBuilder;
import org.eclipse.nebula.widgets.nattable.ui.util.CellEdgeEnum;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.icdm.client.bo.apic.AttrRootNode;
import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.client.bo.uc.FavUseCaseItemNode;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseEditorRowAttr;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseRootNode;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseSectionClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UserFavUcRootNode;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.ui.editors.AbstractNatFormPage;
import com.bosch.caltool.icdm.common.ui.table.filters.OutlineUCNatFilter;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.OutlineViewPart;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.uc.UcpAttr;
import com.bosch.caltool.icdm.model.uc.UseCase;
import com.bosch.caltool.icdm.model.uc.UseCaseSection;
import com.bosch.caltool.icdm.model.uc.UsecaseEditorModel;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.uc.UseCaseServiceClient;
import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomNATTable;
import com.bosch.caltool.nattable.configurations.CustomNatTableStyleConfiguration;
import com.bosch.caltool.usecase.ui.Activator;
import com.bosch.caltool.usecase.ui.actions.UseCaseActionSet;
import com.bosch.caltool.usecase.ui.actions.UsecaseNatAttributesPageToolBarActionSet;
import com.bosch.caltool.usecase.ui.actions.UsecaseUpToDateAction;
import com.bosch.caltool.usecase.ui.editors.UseCaseEditor;
import com.bosch.caltool.usecase.ui.editors.UseCaseEditorInput;
import com.bosch.caltool.usecase.ui.table.filters.AllUCColumnFilterMatcher;
import com.bosch.caltool.usecase.ui.table.filters.UseCaseAttrPageNatToolBarFilters;
import com.bosch.caltool.usecase.ui.util.UseCaseNatToolTip;
import com.bosch.caltool.usecase.ui.views.AttributeValuesViewPart;
import com.bosch.caltool.usecase.ui.views.UseCaseDetailsViewPart;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;
import com.bosch.rcputils.wbutils.WorkbenchUtils;


/**
 * @author jvi6cob
 */
public class UseCaseNatAttributesPage extends AbstractNatFormPage implements ISelectionListener {

  /**
   *
   */
  private static final String UNDERSCORE = "_";
  /**
   *
   */
  private static final String TICK_UTF_ENCODED = "\u2714";
  /**
   *
   */
  private static final String MULTIPLE_ITEMS = "(Multiple Items)";
  /**
   * message if usecase is up to date
   */
  public static final String UC_UP_TO_DATE_MSG = "Use case is up to date";
  /**
   * message if usecase is not up to date
   */
  public static final String UC_NOT_UP_TO_DATE_MSG = "Use case is NOT up to date";
  /**
   * static column index
   */
  public static final int STATIC_COLUMN_INDEX = 8;
  /**
   * Constant string
   */
  private static final String USE_CASE_ITEMS = "Use Case Items";
  /**
   *
   */
  private static final int COLUMN_NUM_BALL_WIDTH = 40;
  /**
   *
   */
  private static final int ATTRIBUTE_COLUMN_WIDTH = 120;
  /**
   *
   */
  private static final int DESCRIPTION_COLUMN_WIDTH = 120;
  /**
   *
   */
  private static final int ATTR_CREATION_DATE_WIDTH = 100;
  /**
   *
   */
  private static final int ATTR_CLASS_COLUMN_WIDTH = 90;
  /**
   *
   */
  private static final int ALL_COLUMN_WIDTH = 50;
  /**
   *
   */
  private static final int ANY_COLUMN_WIDTH = 50;
  /**
   *
   */
  private static final int NONE_COLUMN_WIDTH = 50;
  /**
   *
   */
  private static final int YCOORDINATE_TEN = 10;
  /**
   *
   */
  private static final int XCOORDINATE_TEN = 10;
  /**
   * Constant defining the type filter text
   */
  private static final String TYPE_FILTER_TEXT = "type filter text";
  // ICDM-336
  /**
   * Title Separator
   */
  private static final String STR_ARROW = " >> ";

  private UsecaseUpToDateAction ucNotUpToDateAction;

  /**
   * Constant defining the Usecase - Attributes
   */
  private static final String USECASE_ATTRIBUTES = "Usecase - Attributes";

  /**
   * constant value for left mouse click button event
   */
  protected static final int LEFT_MOUSE_CLICK = 1;

  private static final int RIGHT_MOUSE_CLICK = 3;

  /**
   *
   */
  public static final int COLUMN_NUM_BALL = 0;

  /**
   *
   */
  public static final int ATTR_COLUMN_IDX = 1;

  /**
   *
   */
  public static final int DESC_COLUMN_IDX = 2;

  /**
   *
   */
  public static final int CREATION_DATE_COLUMN_IDX = 3;

  /**
   *
   */
  public static final int ATTR_CLASS_COLUMN_IDX = 4;

  /**
   *
   */
  public static final int ALL_CHECKBOX_COLUMN_IDX = 6;

  /**
   *
   */
  public static final int NONE_CHECKBOX_COLUMN_IDX = 7;


  /**
   *
   */
  public static final int ANY_COLUMN_IDX = 5;


  private CellPainterDecorator quotPainterDecorator;

  /**
   * Editor instance
   */
  private final UseCaseEditor editor;

  /**
   * Non-scrollable Form instance
   */
  // ICDM-288
  private Form nonScrollableForm;

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
   * Filter text instance
   */
  private Text filterTxt;
  /**
   * UseCaseAttrPageToolBarFilters instance
   */
  private UseCaseAttrPageNatToolBarFilters toolBarFilters;

  /**
   * Initial Column Count when NatTable is initialised
   */
  private int initialcolumnCount;

  /**
   * selected use case section
   */
  private UseCaseSectionClientBO selectedUcSection;
  /**
   * Mappable UC Items
   */
  private SortedSet<IUseCaseItemClientBO> mappableUcItems;

  private CustomFilterGridLayer ucFilterGridLayer;
  private CustomNATTable natTable;
  private RowSelectionProvider<UseCaseEditorRowAttr> selectionProvider;
  private Map<Integer, String> propertyToLabelMap;
  private final Map<Integer, IUseCaseItemClientBO> columnUseCaseItemMapping = new HashMap<>();
  private SortedSet<UseCaseEditorRowAttr> useCaseNatInputs;
  private List<IUseCaseItemClientBO> cachedUseCaseItems;
  private AllUCColumnFilterMatcher<UseCaseEditorRowAttr> allColumnFilterMatcher;
  private int totTableRowCount;
  private OutlineUCNatFilter outLineNatFilter;
  private UseCaseNatInputToColumnConverter natInputToColumnConverter;
  private ToolBarManager toolBarManager;
  private UsecaseUpToDateAction ucUpToDateAction;
  private int updatedCachedUCItemsSize;
  private Set<UseCaseSectionClientBO> selUCSSet = new HashSet<>();
  /**
   * field to store the left mouse click event
   */
  protected int clickEventVal;
  private static final String CHECK_BOX_CONFIG_LABEL = "checkBox";
  private static final String CHECK_BOX_EDITOR_CNG_LBL = "checkBoxEditor";
  private static final String CUSTOM_COMPARATOR_LABEL = "customComparatorLabel";
  /**
   * Quotation config label
   */
  public static final String QUOTATION_LABEL = "quotation";
  private boolean canShowQuotRelevantContextMenu = false;
  private boolean canShowQuotNotRelevantContextMenu = false;
  private boolean canShowMapUnmapContextMenu = false;
  /**
   * Key - ucpAttrId, value - UcpAttr
   */
  private final Map<Long, UcpAttr> selectedUcpAttrMap = new HashMap<>();

  /**
   * list of UcpAttr in which attribute mappings can be created
   */
  private List<UcpAttr> mapAllAttrList;

  /**
   * set of UcpAttr whose mappings are to be deleted
   */
  private Set<UcpAttr> unMapAllAttrsSet;
  /**
   * change in access rights
   */
  private boolean changeInAccessRights;


  /**
   * @param editor instance
   * @param useCase instance
   */
  public UseCaseNatAttributesPage(final FormEditor editor, final UsecaseClientBO useCase) {
    super(editor, "UseCase.label", "Attributes");
    this.editor = (UseCaseEditor) editor;
  }

  /**
   * // icdm-296 changes {@inheritDoc}
   */
  @Override
  public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
    if (CommonUtils.isEqual(getSite().getPage().getActiveEditor(), getEditor())) {
      // ICDM-288
      if (part instanceof UseCaseDetailsViewPart) {
        usecaseDetailsSelectionListener(selection);
        this.ucFilterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        this.ucFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(this.ucFilterGridLayer.getSortableColumnHeaderLayer()));
        if (this.editor.getActivePage() == 0) {
          setStatusBarMessage(false, true);
        }
      }
      else if (part instanceof OutlineViewPart) {
        outLineSelectionListener(selection);
        if (this.editor.getActivePage() == 0) {
          setStatusBarMessage(true, false);
        }
      }
    }
  }

  /**
   * This method will listen to selection change of usecase details view
   *
   * @param selection
   */
  private void usecaseDetailsSelectionListener(final ISelection selection) {
    if ((selection != null) && !selection.isEmpty() && (selection instanceof IStructuredSelection)) {
      final Object first = ((IStructuredSelection) selection).getFirstElement();
      if (first instanceof UsecaseClientBO) {
        this.selUCSSet = null;
        reconstructNatTable();
        if (this.mappableUcItems != null) {
          this.mappableUcItems.clear();
        }
        this.mappableUcItems = ((IUseCaseItemClientBO) first).getMappableItems();

        // ICDM-289
        this.toolBarFilters.setUcItemsList(this.mappableUcItems);
        setUseCaseInfo(first);
        consolidateColumns();
        this.natTable.doCommand(new StructuralRefreshCommand());
      }
      // ICDM-336
      else if (first instanceof UseCaseSectionClientBO) {
        reloadMappableItems(selection);
        this.updatedCachedUCItemsSize = this.cachedUseCaseItems.size();
        reconstructNatTable();
        // ICDM-289
        this.toolBarFilters.setUcItemsList(this.mappableUcItems);

        boolean newColumnAdded = false;
        List<IUseCaseItemClientBO> ucItemsToRemove = new ArrayList<>();
        List<IUseCaseItemClientBO> ucItemsToAdd = new ArrayList<>();
        for (IUseCaseItemClientBO IUseCaseItem : this.mappableUcItems) {
          if (!this.cachedUseCaseItems.contains(IUseCaseItem)) {
            if ((IUseCaseItem.getMappableItems() == null) || IUseCaseItem.getMappableItems().isEmpty() ||
                ((IUseCaseItem.getMappableItems().size() == 1) &&
                    IUseCaseItem.getMappableItems().contains(IUseCaseItem))) {
              // Missing UseCaseItem is the last child
              if (IUseCaseItem.getParent().getMappableItems().size() == 1) {
                // Missing UseCaseItem is the only child
                if (this.cachedUseCaseItems.contains(IUseCaseItem.getParent())) {
                  // Missing UseCaseItem's parent entry already existing and missing UseCaseItem is the only child,so
                  // need to add extra columns
                  ucItemsToRemove.add(IUseCaseItem.getParent());
                  ucItemsToAdd.add(IUseCaseItem);
                  continue;
                }
              }
              // Missing UseCaseItem's parent entry has more than one child
              newColumnAdded = true;
              ucItemsToAdd.add(IUseCaseItem);
            }
          }
        }
        this.cachedUseCaseItems.removeAll(ucItemsToRemove);
        this.cachedUseCaseItems.addAll(ucItemsToAdd);
        setUseCaseSectionInfo(first);
        if (newColumnAdded || (this.ucFilterGridLayer.getBodyLayer().getColumnReorderLayer()
            .getColumnCount() < (this.columnUseCaseItemMapping.size() + STATIC_COLUMN_INDEX))) {
          registerConfigLabelsOnColumns(UseCaseNatAttributesPage.this.ucFilterGridLayer.getBodyLabelAccumulator(),
              true);
          registerCheckBoxEditor(this.natTable.getConfigRegistry(), true);
          registerEditableRules(this.natTable.getConfigRegistry(), true);
          ILayer bodyDataLayer = this.ucFilterGridLayer.getDummyDataLayer();
          // The below firelayer event prevents creation of an empty column when adding new UseCaseItems
          int colCount = this.ucFilterGridLayer.getBodyLayer().getColumnHideShowLayer().getColumnCount();
          bodyDataLayer.fireLayerEvent(new ColumnInsertEvent(bodyDataLayer, colCount - 1));
          consolidateColumns();
          consolidateUseCaseColumnGroupingForNewColumn();
        }
        if (!newColumnAdded) {
          consolidateColumns();
        }
        this.natTable.doCommand(new StructuralRefreshCommand());

      }
    }

  }

  /**
   * @param selection
   */
  private void reloadMappableItems(final ISelection selection) {
    if (null != selection) {
      this.mappableUcItems.clear();
      this.selUCSSet = new HashSet<>();
      Iterator<?> iterator = ((IStructuredSelection) selection).iterator();
      while (iterator.hasNext()) {
        Object selElement = iterator.next();
        if (selElement instanceof UseCaseSectionClientBO) {
          this.mappableUcItems.addAll(((IUseCaseItemClientBO) selElement).getMappableItems());
          this.selUCSSet.add((UseCaseSectionClientBO) selElement);
        }
      }
    }

  }

  /**
   * reconstruct nat table
   */
  private void reconstructNatTable() {
    if ((this.initialcolumnCount < this.updatedCachedUCItemsSize) || this.changeInAccessRights) {
      this.natTable.dispose();
      this.propertyToLabelMap.clear();
      this.columnUseCaseItemMapping.clear();
      this.cachedUseCaseItems = null;
      this.ucFilterGridLayer = null;
      if (this.toolBarManager != null) {
        this.toolBarManager.removeAll();
      }
      if (this.nonScrollableForm.getToolBarManager() != null) {
        this.nonScrollableForm.getToolBarManager().removeAll();
      }
      // TODO:The reconstruction of NatTable needs to be optimised
      createUsecaseAttrGridTable();
      createToolBarAction();
      // First the form's body is repacked and then the section is repacked
      // Packing in the below manner prevents the disappearance of Filter Field and refreshes the natTable
      this.form.getBody().pack();
      this.section.pack();
      this.changeInAccessRights = false;
    }
  }


  // ICDM-336
  /**
   * This method sets UseCase information on selection UseCase details treeviewer
   *
   * @param first
   */
  private void setUseCaseInfo(final Object first) {
    final UsecaseClientBO selUseCase = (UsecaseClientBO) first;
    this.nonScrollableForm.setText(selUseCase.getName());
  }

  /**
   * This method sets UseCase section information on selection UseCase details treeviewer
   *
   * @param first
   */
  private void setUseCaseSectionInfo(final Object first) {
    UseCaseSectionClientBO selectedUcSection = (UseCaseSectionClientBO) first;
    String titleText = selectedUcSection.getName();
    // selected usecase section will be used during cns refresh
    // Icdm-358 get Parent made Generic
    UseCaseSectionClientBO parentUcSectionBO = null;
    if ((selectedUcSection.getParent() instanceof UseCaseSectionClientBO)) {
      parentUcSectionBO = (UseCaseSectionClientBO) selectedUcSection.getParent();
    }
    IUseCaseItemClientBO ucItem = parentUcSectionBO;
    // if ucSection is null then no parent
    while (ucItem instanceof UseCaseSectionClientBO) {
      if (!ucItem.getName().equals(selectedUcSection.getName())) {
        titleText = ucItem.getName() + STR_ARROW + titleText;
      }
      ucItem = ucItem.getParent();
    }
    titleText = getUseCase().getName() + STR_ARROW + titleText;
    if ((null != this.selUCSSet) && (this.selUCSSet.size() != 1)) {
      titleText = getUseCase().getName() + STR_ARROW + MULTIPLE_ITEMS;
      this.nonScrollableForm.setText(titleText);
    }
    else {
      this.nonScrollableForm.setText(titleText);

    }
  }

  /**
   * @return selected usecase
   */
  public UsecaseClientBO getUseCase() {
    return this.editor.getEditorInput().getSelectedUseCase();
  }

  /**
   * icdm-296 This method invokes on the selection of Outline Tree node
   *
   * @param selection
   */
  private void outLineSelectionListener(final ISelection selection) {
    if ((selection != null) && !selection.isEmpty() && (selection instanceof IStructuredSelection)) {
      final Object first = ((IStructuredSelection) selection).getFirstElement();
      // Check if selection is SuperGroup
      if (first instanceof AttrSuperGroup) {
        this.outLineNatFilter.setSuperGroup(true);
        final AttrSuperGroup attrSuperGroup = (AttrSuperGroup) first;
        this.outLineNatFilter.setSelectedNode(attrSuperGroup.getName());
        this.ucFilterGridLayer.getFilterStrategy().applyOutlineFilterInAllColumns(false);
        this.ucFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(this.ucFilterGridLayer.getSortableColumnHeaderLayer()));
      }
      // Check if selection is Group
      else if (first instanceof AttrGroup) {
        this.outLineNatFilter.setGroup(true);
        final AttrGroup attrGroup = (AttrGroup) first;
        this.outLineNatFilter.setSelectedNode(attrGroup.getName());
        this.ucFilterGridLayer.getFilterStrategy().applyOutlineFilterInAllColumns(false);
        this.ucFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(this.ucFilterGridLayer.getSortableColumnHeaderLayer()));
      }
      // Check if selection is COMMON
      else if ((first instanceof AttrRootNode) || (first instanceof UseCaseRootNode) ||
          (first instanceof UserFavUcRootNode)) {
        this.outLineNatFilter.setCommon(true);
        this.outLineNatFilter.setSelectedNode("");
        this.ucFilterGridLayer.getFilterStrategy().applyOutlineFilterInAllColumns(true);
        this.ucFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(this.ucFilterGridLayer.getSortableColumnHeaderLayer()));
      }
      else if (first instanceof IUseCaseItemClientBO) {
        this.outLineNatFilter.setUseCaseItem((IUseCaseItemClientBO) first);
        this.ucFilterGridLayer.getFilterStrategy().applyOutlineFilterInAllColumns(false);
        this.ucFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(this.ucFilterGridLayer.getSortableColumnHeaderLayer()));
      }
      // ICDM-1035
      else if (first instanceof FavUseCaseItemNode) {
        this.outLineNatFilter.setFavUseCaseItem((FavUseCaseItemNode) first);
        this.ucFilterGridLayer.getFilterStrategy().applyOutlineFilterInAllColumns(false);
        this.ucFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(this.ucFilterGridLayer.getSortableColumnHeaderLayer()));
      }
    }
  }


  @Override
  // ICDM-288
  public void createPartControl(final Composite parent) {
    this.nonScrollableForm = this.editor.getToolkit().createForm(parent);
    this.nonScrollableForm.setText(getUseCase().getName());


    if (getUseCase().isUpToDate()) {
      this.nonScrollableForm.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.ALL_16X16));
      this.nonScrollableForm.setMessage("[ " + UC_UP_TO_DATE_MSG + " ]");
    }
    else {
      this.nonScrollableForm.setMessage("[ " + UC_NOT_UP_TO_DATE_MSG + " ]", 3);
    }

    final ManagedForm mform = new ManagedForm(parent);
    createFormContent(mform);
  }

  @Override
  public Control getPartControl() {
    return this.nonScrollableForm;
  }

  @Override
  protected void createFormContent(final IManagedForm managedForm) {
    final FormToolkit formToolkit = managedForm.getToolkit();

    createComposite(formToolkit);

    // add listeners
    getSite().getPage().addSelectionListener(this);
  }

  /**
   * This method initializes composite
   *
   * @throws IcdmException
   */
  private void createComposite(final FormToolkit toolkit) {
    this.composite = this.nonScrollableForm.getBody();
    this.composite.setLayout(new GridLayout());

    createSection(toolkit);
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());

    CurrentUserBO currentUser = new CurrentUserBO();
    boolean isOwner;
    try {
      isOwner = currentUser.hasNodeOwnerAccess(getUseCase().getUseCase().getId());
      if (isOwner && !getUseCase().isUpToDate()) {
        displayInfoMessageForNotUpToDate();
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

  }

  /**
   *
   */
  private void displayInfoMessageForNotUpToDate() {

    boolean confirmUpToDate = MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
        "Confirm Use Case Up To Date", UC_NOT_UP_TO_DATE_MSG + "\n Confirm if it is up to date.");
    if (confirmUpToDate) {
      confirmUpToDate(false);
    }
  }

  /**
   * @param notUpToDate
   */
  private void confirmUpToDate(final boolean notUpToDate) {

    UseCaseServiceClient ucServiceClient = new UseCaseServiceClient();
    UseCase useCase = getUseCase().getUseCase();

    try {
      ucServiceClient.changeUpToDateStatus(useCase, !notUpToDate);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }


  }

  /**
   * This method initializes section
   */
  private void createSection(final FormToolkit toolkit) {
    this.section = SectionUtil.getInstance().createSection(this.composite, toolkit, USECASE_ATTRIBUTES);
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.getDescriptionControl().setEnabled(true);
    this.section.setDescription(
        "To set an attribute as 'Quotation Relevant', right click on any cell in UseCase Items column  and choose 'Mark as quotation relevant'. Right click on 'ALL' column to set for all usecase items.");
    createForm(toolkit);
    this.section.setClient(this.form);
  }

  /**
   * This method initializes scrolledForm
   */
  private void createForm(final FormToolkit toolkit) {
    this.form = toolkit.createForm(this.section);
    this.filterTxt = TextUtil.getInstance().createFilterText(toolkit, this.form.getBody(),
        GridDataUtil.getInstance().getTextGridData(), TYPE_FILTER_TEXT);
    this.form.getBody().setLayout(new GridLayout());
    addModifyListenerForFilterTxt();
    // ICDM-288
    createUsecaseAttrTableViewer();
    // ICDM-289
    createToolBarAction();
  }

  /**
   * This method creates usecase GridTableViewer
   */
  private void createUsecaseAttrTableViewer() {
    createUsecaseAttrGridTable();
  }

  /**
   *
   */
  private void createUsecaseAttrGridTable() {

    UseCaseEditorInput useCaseEditorInput = (UseCaseEditorInput) getEditorInput();

    this.mappableUcItems = getUseCase().getMappableItems();
    final SortedSet<AttributeClientBO> attributesInput = fillRowObjects(useCaseEditorInput);

    fillPropertyToLabelMap();

    // The below map is used by NatTable to Map Columns with their respective widths
    // Width is based on pixels
    Map<Integer, Integer> columnWidthMap = new HashMap<>();
    fillColumnWidthMap(columnWidthMap);

    consolidateColumns();

    this.natInputToColumnConverter = new UseCaseNatInputToColumnConverter();
    IConfigRegistry configRegistry = new ConfigRegistry();

    // A Custom Filter Grid Layer is constructed
    this.ucFilterGridLayer = new CustomFilterGridLayer<UseCaseEditorRowAttr>(configRegistry, this.useCaseNatInputs,
        columnWidthMap, new CustomUseCaseColumnPropertyAccessor<>(), new CustomUseCaseColumnHeaderDataProvider(),
        getUCComparator(0), this.natInputToColumnConverter, this, null, true, true, true);

    this.outLineNatFilter =
        new OutlineUCNatFilter(true, ((UseCaseEditorInput) getEditorInput()).getOutlineDataHandler());
    this.ucFilterGridLayer.getFilterStrategy().setOutlineNatFilterMatcher(this.outLineNatFilter.getUcOutlineMatcher());

    this.toolBarFilters = new UseCaseAttrPageNatToolBarFilters(this.mappableUcItems);
    this.ucFilterGridLayer.getFilterStrategy().setToolBarFilterMatcher(this.toolBarFilters.getToolBarMatcher());

    this.allColumnFilterMatcher = new AllUCColumnFilterMatcher<>();
    this.ucFilterGridLayer.getFilterStrategy().setAllColumnFilterMatcher(this.allColumnFilterMatcher);

    this.natTable = new CustomNATTable(
        this.form.getBody(), SWT.FULL_SELECTION | SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED |
            SWT.BORDER | SWT.VIRTUAL | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI,
        this.ucFilterGridLayer, false, getClass().getSimpleName());


    natTableConfig(configRegistry);

    // Group columns programmatically
    groupColumns();

    consolidateUseCaseColumnGrouping();
    this.natTable.addConfiguration(new AbstractRegistryConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry configRegistry) {
        registerConfigLabelsOnColumns(UseCaseNatAttributesPage.this.ucFilterGridLayer.getBodyLabelAccumulator(), false);
        registerCheckBoxEditor(configRegistry, false);
        registerEditableRules(configRegistry, false);


      }


    });

    this.natTable.addConfiguration(new AbstractRegistryConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry iConfigRegistry) {
        // configure the validation error style
        // Style for cells under column with BALL icon
        IStyle validationErrorStyle = new Style();
        validationErrorStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT,
            HorizontalAlignmentEnum.CENTER);
        validationErrorStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR,
            GUIHelper.COLOR_WIDGET_LIGHT_SHADOW);
        // Get image for column
        ImagePainter imgPainter = new ImagePainter() {

          /**
           * Returns the image for a cell.{@inheritDoc}
           */
          @Override
          protected Image getImage(final ILayerCell cell, final IConfigRegistry iCfgRegistry) {
            // get the selected row object
            UseCaseEditorRowAttr useCaseNatInput =
                (UseCaseEditorRowAttr) UseCaseNatAttributesPage.this.ucFilterGridLayer.getBodyDataProvider()
                    .getRowObject(cell.getRowIndex());
            // get the attribute object
            Attribute attribute = useCaseNatInput.getAttributeBO().getAttribute();
            Image image = null;

            // IF MANDATORY
            if (attribute.isMandatory()) {
              image = getLinkOverLayedImage(ImageKeys.RED_BALL_16X16, ImageKeys.LINK_DECORATOR_12X12, attribute);
            }
            // IF DEPENDENT ATTRIBUTE
            else if (CommonUtils.isNotEmpty(UseCaseNatAttributesPage.this.editor.getEditorInput()
                .getUseCaseEditorModel().getAttrRefDependenciesMap().get(attribute.getId())) &&
                hasAttrDependencies(attribute)) {
              image = ImageManager.getInstance().getRegisteredImage(ImageKeys.DEPN_ATTR_28X30);
            }
            else if (UseCaseNatAttributesPage.this.editor.getEditorInput().getUseCaseEditorModel().getLinkSet()
                .contains(attribute.getId())) {
              image = ImageManager.getDecoratedImage(null, ImageKeys.LINK_DECORATOR_12X12, IDecoration.TOP_RIGHT);
            }
            return image;

          }

        };
        iConfigRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter, DisplayMode.NORMAL,
            "BALL");
        iConfigRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle,
            DisplayMode.NORMAL, "BALL");

        // Style for DATE_ATTRCLASS
        IStyle dateNAttrClassStyle = new Style();
        dateNAttrClassStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
        dateNAttrClassStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WHITE);
        configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, dateNAttrClassStyle, DisplayMode.NORMAL,
            "DATE_ATTRCLASS");
      }


    });

    DataLayer bodyDataLayer = this.ucFilterGridLayer.getDummyDataLayer();
    IRowDataProvider<UseCaseEditorRowAttr> bodyDataProvider =
        (IRowDataProvider<UseCaseEditorRowAttr>) bodyDataLayer.getDataProvider();

    UseCaseNatAttrPageLabelAccumulator labelAcc =
        new UseCaseNatAttrPageLabelAccumulator(bodyDataLayer, bodyDataProvider, this);

    // aggregate config label accumulator to add multiple label accumulators
    AggregateConfigLabelAccumulator aggLabelAcc = new AggregateConfigLabelAccumulator();

    aggLabelAcc.add(labelAcc);
    aggLabelAcc.add(UseCaseNatAttributesPage.this.ucFilterGridLayer.getBodyLabelAccumulator());

    bodyDataLayer.setConfigLabelAccumulator(aggLabelAcc);

    this.ucFilterGridLayer.setConfigLabelAccumulator(labelAcc);
    this.natTable.configure();


    // get the reference to the SelectionLayer
    SelectionLayer selectionLayer = this.ucFilterGridLayer.getBodyLayer().getSelectionLayer();
    // select cell with column position 2 and row position 0
    selectionLayer.setSelectedCell(STATIC_COLUMN_INDEX, 0);
    // freeze the first two columns
    this.natTable.doCommand(new FreezeSelectionCommand());
    this.ucFilterGridLayer.registerCommandHandler(new DisplayPersistenceDialogCommandHandler(this.natTable));
    this.selectionProvider = new RowSelectionProvider<>(this.ucFilterGridLayer.getBodyLayer().getSelectionLayer(),
        this.ucFilterGridLayer.getBodyDataProvider(), false);


    this.selectionProvider.addSelectionChangedListener(selectionChangeListener());
    this.selectionProvider.setAddSelectionOnSet(true);

    // The below method is required to enable tootltip only for cells which contain not fully visible content
    attachToolTip(this, attributesInput);

    getSite().setSelectionProvider(this.selectionProvider);

    addMouseListener();

    addRightClickOption();

    this.initialcolumnCount = this.cachedUseCaseItems.size();
  }


  private Image getLinkOverLayedImage(final ImageKeys baseImgKey, final ImageKeys overlayKey,
      final Attribute attribute) {
    if (!UseCaseNatAttributesPage.this.editor.getEditorInput().getUseCaseEditorModel().getLinkSet()
        .contains(attribute.getId())) {
      return ImageManager.getInstance().getRegisteredImage(baseImgKey);
    }
    return ImageManager.getDecoratedImage(baseImgKey, overlayKey, IDecoration.TOP_RIGHT);
  }


  private boolean hasAttrDependencies(final Attribute attribute) {
    // Check for attr dependencies
    Set<AttrNValueDependency> depenAttr = UseCaseNatAttributesPage.this.editor.getEditorInput().getUseCaseEditorModel()
        .getAttrRefDependenciesMap().get(attribute.getId());
    for (AttrNValueDependency attrDependency : depenAttr) {
      if ((attrDependency != null) && (attrDependency.getAttributeId() != null)) {
        return true;
      }
    }
    return false;
  }


  private void registerImagePainterConfig() {
    // Get image for column
    Image registeredImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.QUOTATION_FLAG);

    this.quotPainterDecorator =
        new CellPainterDecorator(new ImagePainter(), CellEdgeEnum.TOP_RIGHT, 2, new ImagePainter(registeredImage) {

          @Override
          protected Image getImage(final ILayerCell cell, final IConfigRegistry configRegistry) {
            List<String> labels = cell.getConfigLabels().getLabels();
            if (labels.contains(QUOTATION_LABEL)) {
              return registeredImage;
            }
            return CellStyleUtil.getCellStyle(cell, configRegistry).getAttributeValue(CellStyleAttributes.IMAGE);
          }
        }, true);
  }

  private boolean isNotRelevantColIndexForContextMenus(final int colIndex) {
    return ((colIndex == COLUMN_NUM_BALL) || (colIndex == DESC_COLUMN_IDX) || (colIndex == CREATION_DATE_COLUMN_IDX) ||
        (colIndex == ANY_COLUMN_IDX));
  }

  private void setFlagForContextMenu(final int colIndex) {
    UseCaseNatAttributesPage.this.canShowQuotRelevantContextMenu = false;
    UseCaseNatAttributesPage.this.canShowQuotNotRelevantContextMenu = false;
    if (!CommonUtils.isNullOrEmpty(UseCaseNatAttributesPage.this.selectedUcpAttrMap)) {
      if (colIndex == ALL_CHECKBOX_COLUMN_IDX) {
        UseCaseNatAttributesPage.this.canShowQuotRelevantContextMenu = true;
        UseCaseNatAttributesPage.this.canShowQuotNotRelevantContextMenu = false;
      }
      else if (colIndex == NONE_CHECKBOX_COLUMN_IDX) {
        UseCaseNatAttributesPage.this.canShowQuotRelevantContextMenu = false;
        UseCaseNatAttributesPage.this.canShowQuotNotRelevantContextMenu = true;
      }
      else {
        UseCaseNatAttributesPage.this.canShowQuotRelevantContextMenu = true;
        UseCaseNatAttributesPage.this.canShowQuotNotRelevantContextMenu = true;
      }
    }
  }


  private void addMouseListener() {

    this.natTable.addMouseListener(new MouseListener() {

      @Override
      public void mouseDoubleClick(final MouseEvent arg0) {
        // NA

      }

      @Override
      public void mouseDown(final MouseEvent mouseEvent) {


        if (mouseEvent.button == LEFT_MOUSE_CLICK) {
          UseCaseNatAttributesPage.this.clickEventVal = LEFT_MOUSE_CLICK;
        }

        if (mouseEvent.button == RIGHT_MOUSE_CLICK) {

          UseCaseNatAttributesPage.this.canShowMapUnmapContextMenu = false;
          UseCaseNatAttributesPage.this.canShowQuotNotRelevantContextMenu = false;
          UseCaseNatAttributesPage.this.canShowQuotRelevantContextMenu = false;

          int columnPositionByX = UseCaseNatAttributesPage.this.natTable.getColumnPositionByX(mouseEvent.x);

          int rowPositionByY = UseCaseNatAttributesPage.this.natTable.getRowPositionByY(mouseEvent.y);
          int rightClickRowIndex = LayerUtil.convertRowPosition(UseCaseNatAttributesPage.this.natTable, rowPositionByY,
              UseCaseNatAttributesPage.this.ucFilterGridLayer.getBodyLayer().getSelectionLayer());

          if (!shouldRetainSelection(rightClickRowIndex)) {
            // get the row object from the row number
            UseCaseEditorRowAttr rowObj = (UseCaseEditorRowAttr) UseCaseNatAttributesPage.this.ucFilterGridLayer
                .getBodyDataProvider().getRowObject(rightClickRowIndex);
            // first set the selection to empty selection to avoid multi-selection
            UseCaseNatAttributesPage.this.selectionProvider.setSelection(new StructuredSelection());

            UseCaseNatAttributesPage.this.selectionProvider.setSelection(new StructuredSelection(rowObj));
          }

          final int rightClickColIndex =
              LayerUtil.convertColumnPosition(UseCaseNatAttributesPage.this.natTable, columnPositionByX,
                  ((CustomFilterGridLayer<UseCaseEditorRowAttr>) UseCaseNatAttributesPage.this.natTable.getLayer())
                      .getDummyDataLayer());

          if ((columnPositionByX != 0) && isNotRelevantColIndexForContextMenus(rightClickColIndex)) {
            return;
          }

          final IStructuredSelection selection =
              (IStructuredSelection) UseCaseNatAttributesPage.this.selectionProvider.getSelection();

          if ((null != selection) && CommonUtils.isNotEmpty(selection.toList())) {
            createMapUnMapAttrsCollections(rightClickColIndex, columnPositionByX, selection.toList());
            setSelectedUcpAttrs(rightClickColIndex, columnPositionByX);
          }

          // to enable or disable context menu
          setFlagForContextMenu(rightClickColIndex);
        }

      }

      @Override
      public void mouseUp(final MouseEvent arg0) {
        // NA

      }

    });
  }


  /**
   * @param rightClickRowIndex
   * @return
   */
  private boolean shouldRetainSelection(final int rightClickRowIndex) {
    // get the row numbers of selected rows in case of multi-selection
    Set<Range> selectedRowPositions =
        UseCaseNatAttributesPage.this.ucFilterGridLayer.getBodyLayer().getSelectionLayer().getSelectedRowPositions();
    for (Range range : selectedRowPositions) {
      // check if the right click is performed in any of the selected row numbers
      if ((rightClickRowIndex >= range.start) && (rightClickRowIndex < range.end)) {
        return true;
      }
    }
    return false;
  }

  private void setSelectedUcpAttrs(final int rightClickcolIndex, final int columnPositionByX) {

    UseCaseNatAttributesPage.this.selectedUcpAttrMap.clear();
    Long ucItemId = null;
    Map<Integer, List<Integer>> selectionMap = getSelectionMap(rightClickcolIndex, columnPositionByX);
    for (Entry<Integer, List<Integer>> coordinateEntry : selectionMap.entrySet()) {

      Integer rowIndex = coordinateEntry.getKey();

      UseCaseEditorRowAttr useCaseNatInput = (UseCaseEditorRowAttr) UseCaseNatAttributesPage.this.ucFilterGridLayer
          .getBodyDataProvider().getRowObject(rowIndex);

      for (Integer colIndex : coordinateEntry.getValue()) {
        IUseCaseItemClientBO ucItemClientBo = UseCaseNatAttributesPage.this.columnUseCaseItemMapping.get(colIndex);
        if (ucItemClientBo instanceof UseCaseSectionClientBO) {
          ucItemId = ((UseCaseSectionClientBO) ucItemClientBo).getUseCaseSection().getId();
        }
        else if (ucItemClientBo instanceof UsecaseClientBO) {
          ucItemId = ((UsecaseClientBO) ucItemClientBo).getUseCase().getId();
        }
        if (ucItemId != null) {
          Map<Long, Long> ucItemUcpAttrMap =
              UseCaseNatAttributesPage.this.editor.getEditorInput().getUseCaseEditorModel().getAttrToUcpAttrMap()
                  .get(useCaseNatInput.getAttributeBO().getAttribute().getId());

          if (CommonUtils.isNotEmpty(ucItemUcpAttrMap) && ucItemUcpAttrMap.keySet().contains(ucItemId)) {
            UcpAttr ucpAttr = UseCaseNatAttributesPage.this.editor.getEditorInput().getUseCaseEditorModel().getUcpAttr()
                .get(ucItemUcpAttrMap.get(ucItemId)).clone();
            UseCaseNatAttributesPage.this.selectedUcpAttrMap.put(ucpAttr.getId(), ucpAttr);
          }
        }
      }


    }
  }

  /**
   * @param rightClickColIndex
   * @param columnPositionByX
   * @param selectedRowObjects
   */
  private void createMapUnMapAttrsCollections(final int rightClickColIndex, final int columnPositionByX,
      final List<UseCaseEditorRowAttr> selectedRowObjects) {
    if (((rightClickColIndex == 0) || (rightClickColIndex == 1)) && (columnPositionByX != 1)) {
      this.mapAllAttrList = new ArrayList<>();
      this.unMapAllAttrsSet = new HashSet<>();
      final SortedSet<IUseCaseItemClientBO> notDeletedUCItems = getNotDeleledUCItems();

      for (UseCaseEditorRowAttr useCaseNatInput : selectedRowObjects) {

        final AttributeClientBO attributeBO = useCaseNatInput.getAttributeBO();

        ucpAttrsToBeCreated(notDeletedUCItems, attributeBO);
        ucpAttrsToBeUnMapped(notDeletedUCItems, attributeBO);

      }
      this.canShowMapUnmapContextMenu = true;
    }
  }


  /**
   * @param notDeletedUCItems
   * @param attributeBO
   */
  private void ucpAttrsToBeCreated(final SortedSet<IUseCaseItemClientBO> notDeletedUCItems,
      final AttributeClientBO attributeBO) {

    if (!notDeletedUCItems.isEmpty()) {
      UsecaseClientBO useCaseBO = getUseCase();
      for (IUseCaseItemClientBO iUseCaseItemClientBO : notDeletedUCItems) {
        // if attribute is mapped, no need to add in the list
        if (iUseCaseItemClientBO.isMapped(attributeBO.getAttribute())) {
          continue;
        }

        UcpAttr ucpAttr = new UcpAttr();
        ucpAttr.setAttrId(attributeBO.getAttribute().getId());
        ucpAttr.setUseCaseId(useCaseBO.getID());
        // for use case section, set the section ID
        if (iUseCaseItemClientBO instanceof UseCaseSectionClientBO) {
          ucpAttr.setSectionId(iUseCaseItemClientBO.getID());
        }

        this.mapAllAttrList.add(ucpAttr);
      }
    }
  }

  /**
   * @param notDeletedUCItems
   * @param attributeBO
   */
  private void ucpAttrsToBeUnMapped(final SortedSet<IUseCaseItemClientBO> notDeletedUCItems,
      final AttributeClientBO attributeBO) {
    if (!notDeletedUCItems.isEmpty()) {
      UsecaseEditorModel useCaseEditorModel =
          UseCaseNatAttributesPage.this.editor.getEditorInput().getUseCaseEditorModel();
      Map<Long, Long> attrToUcpMap = useCaseEditorModel.getAttrToUcpAttrMap().get(attributeBO.getAttribute().getId());
      if (CommonUtils.isNotEmpty(attrToUcpMap)) {
        for (IUseCaseItemClientBO ucItem : this.mappableUcItems) {
          Long attrIdToBeUnMapped = attrToUcpMap.get(ucItem.getID());
          if (null != attrIdToBeUnMapped) {
            UcpAttr ucpAttr = useCaseEditorModel.getUcpAttr().get(attrIdToBeUnMapped);
            this.unMapAllAttrsSet.add(ucpAttr.clone());
          }
        }
      }
    }
  }


  private void addToSelectionMap(final Map<Integer, List<Integer>> selectionMap, final int colIndex,
      final int rowIndex) {
    if (selectionMap.containsKey(rowIndex)) {
      selectionMap.get(rowIndex).add(colIndex);
    }
    else {
      List<Integer> colIndexList = new ArrayList<>();
      colIndexList.add(colIndex);
      selectionMap.put(rowIndex, colIndexList);
    }
  }

  private Map<Integer, List<Integer>> getSelectionMap(final int colIndex, final int columnPositionByX) {

    SelectionLayer selectionLayer = UseCaseNatAttributesPage.this.ucFilterGridLayer.getBodyLayer().getSelectionLayer();

    // key- rowIndex, value- list of colIndex
    Map<Integer, List<Integer>> selectionMap = new HashMap<>();

    for (PositionCoordinate positionCoordinate : selectionLayer.getSelectedCellPositions()) {

      if ((colIndex == ALL_CHECKBOX_COLUMN_IDX) || (colIndex == NONE_CHECKBOX_COLUMN_IDX) || (columnPositionByX == 0)) {
        int numOfDynamicCol = (this.mappableUcItems != null) ? this.mappableUcItems.size() : 0;
        for (int col = STATIC_COLUMN_INDEX; col < (STATIC_COLUMN_INDEX + numOfDynamicCol); col++) {
          addToSelectionMap(selectionMap, col, positionCoordinate.rowPosition);
        }
      }
      else {
        addToSelectionMap(selectionMap, positionCoordinate.columnPosition, positionCoordinate.rowPosition);
      }
    }
    return selectionMap;
  }

  private void addRightClickOption() {
    MenuManager menuMgr = new MenuManager();
    menuMgr.setRemoveAllWhenShown(true);
    final UseCaseActionSet actionSet = new UseCaseActionSet();
    menuMgr.addMenuListener(mgr -> {
      // add the Map All/ Un-Map All context menu options
      if (getUseCase().isMappingModifiable() && UseCaseNatAttributesPage.this.canShowMapUnmapContextMenu) {
        actionSet.setMapAllAction(menuMgr, UseCaseNatAttributesPage.this.mapAllAttrList);
        actionSet.setUnmapAllAction(menuMgr, UseCaseNatAttributesPage.this.unMapAllAttrsSet);
        menuMgr.add(new Separator());
      }
      // add the Quotation Relevant/Not Relevant Conetext Menu options
      if (getUseCase().isMappingModifiable() && (UseCaseNatAttributesPage.this.canShowQuotRelevantContextMenu ||
          UseCaseNatAttributesPage.this.canShowQuotNotRelevantContextMenu)) {
        if (UseCaseNatAttributesPage.this.canShowQuotRelevantContextMenu) {
          actionSet.setQuotationRelevantAction(menuMgr, UseCaseNatAttributesPage.this.selectedUcpAttrMap);
        }
        if (UseCaseNatAttributesPage.this.canShowQuotNotRelevantContextMenu) {
          actionSet.setQuotationNotRelevantAction(menuMgr, UseCaseNatAttributesPage.this.selectedUcpAttrMap);
        }
      }
    });
    final Menu menu = menuMgr.createContextMenu(this.natTable.getShell());
    this.natTable.setMenu(menu);
    // Register menu for extension.
    getSite().registerContextMenu(menuMgr, this.selectionProvider);


  }

  /**
   * @param configRegistry
   */
  private void natTableConfig(final IConfigRegistry configRegistry) {
    this.natTable.setConfigRegistry(configRegistry);
    this.natTable.setLayoutData(getGridData());
    this.natTable.addConfiguration(new CustomNatTableStyleConfiguration());
    this.natTable.addConfiguration(new FilterRowCustomConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry configRegistry) {
        super.configureRegistry(configRegistry);

        // Shade the row to be slightly darker than the blue background.
        final Style rowStyle = new Style();
        rowStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.getColor(197, 212, 231));
        configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, rowStyle, NORMAL, FILTER_ROW);
      }
    });
    this.natTable.addConfiguration(new SingleClickSortConfiguration());
    this.natTable.addConfiguration(getCustomComparatorConfiguration(this.ucFilterGridLayer.getColumnHeaderDataLayer()));
    this.natTable.addConfiguration(new HeaderMenuConfiguration(this.natTable) {

      /**
       * {@inheritDoc}
       */
      @Override
      protected PopupMenuBuilder createRowHeaderMenu(final NatTable natTable) {
        return new PopupMenuBuilder(natTable);
      }

      @Override
      protected PopupMenuBuilder createColumnHeaderMenu(final NatTable natTable) {
        return super.createColumnHeaderMenu(natTable).withStateManagerMenuItemProvider();
      }
    });
  }

  /**
   * @return
   */
  private ISelectionChangedListener selectionChangeListener() {
    return new ISelectionChangedListener() {

      @Override
      public void selectionChanged(final SelectionChangedEvent event) {

        IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        // TODO: Check if properties view can be activated for nattable without this selection listener approach
        if (selection.getFirstElement() instanceof UseCaseEditorRowAttr) {
          UseCaseEditorRowAttr useCaseNatInput = (UseCaseEditorRowAttr) selection.getFirstElement();
          final IWorkbenchPart valuesViewPart = WorkbenchUtils.getView(AttributeValuesViewPart.PART_ID, true);
          // Icdm-292
          if (valuesViewPart != null) {
            ((AttributeValuesViewPart) valuesViewPart).setSelectedAttr(useCaseNatInput.getAttributeBO());
          }


        }
      }

    };
  }

  /**
   * @param columnWidthMap
   */
  private void fillColumnWidthMap(final Map<Integer, Integer> columnWidthMap) {
    columnWidthMap.put(COLUMN_NUM_BALL, COLUMN_NUM_BALL_WIDTH);
    columnWidthMap.put(ATTR_COLUMN_IDX, ATTRIBUTE_COLUMN_WIDTH);
    columnWidthMap.put(DESC_COLUMN_IDX, DESCRIPTION_COLUMN_WIDTH);
    columnWidthMap.put(CREATION_DATE_COLUMN_IDX, ATTR_CREATION_DATE_WIDTH);
    columnWidthMap.put(ATTR_CLASS_COLUMN_IDX, ATTR_CLASS_COLUMN_WIDTH);
    columnWidthMap.put(ANY_COLUMN_IDX, ANY_COLUMN_WIDTH);
    columnWidthMap.put(ALL_CHECKBOX_COLUMN_IDX, ALL_COLUMN_WIDTH);
    columnWidthMap.put(NONE_CHECKBOX_COLUMN_IDX, NONE_COLUMN_WIDTH);
  }

  /**
   *
   */
  private void fillPropertyToLabelMap() {
    this.propertyToLabelMap = new HashMap<>();
    this.propertyToLabelMap.put(COLUMN_NUM_BALL, "");
    this.propertyToLabelMap.put(ATTR_COLUMN_IDX, "Attribute");
    this.propertyToLabelMap.put(DESC_COLUMN_IDX, "Description");
    this.propertyToLabelMap.put(CREATION_DATE_COLUMN_IDX, "Attribute Creation Date");
    this.propertyToLabelMap.put(ATTR_CLASS_COLUMN_IDX, "Attribute Class");
    this.propertyToLabelMap.put(ANY_COLUMN_IDX, "ANY");
    this.propertyToLabelMap.put(ALL_CHECKBOX_COLUMN_IDX, "ALL");
    this.propertyToLabelMap.put(NONE_CHECKBOX_COLUMN_IDX, "NONE");
  }

  /**
   * @param useCaseEditorInput
   * @return
   */
  private SortedSet<AttributeClientBO> fillRowObjects(final UseCaseEditorInput useCaseEditorInput) {

    final SortedSet<AttributeClientBO> attributesInput = useCaseEditorInput.getSortedAttributes();
    this.useCaseNatInputs = new TreeSet<>();
    for (AttributeClientBO attribute : attributesInput) {
      UseCaseEditorRowAttr useCaseNatInput =
          new UseCaseEditorRowAttr(this.editor.getEditorInput().getUseCaseEditorModel());
      useCaseNatInput.setAttributeBO(attribute);
      this.useCaseNatInputs.add(useCaseNatInput);
    }
    this.totTableRowCount = this.useCaseNatInputs.size();
    return attributesInput;
  }


  /**
   * Enables tootltip only for cells which contain not fully visible content
   *
   * @param useCaseNatAttributesPage
   * @param attributesInput
   */
  private void attachToolTip(final UseCaseNatAttributesPage useCaseNatAttributesPage,
      final SortedSet<AttributeClientBO> attributesInput) {
    UseCaseEditorInput useCaseEditorInput = (UseCaseEditorInput) getEditorInput();

    // Icdm-1208- Custom tool tip for Nat table.
    DefaultToolTip toolTip =
        new UseCaseNatToolTip(useCaseNatAttributesPage, new String[0], attributesInput, useCaseEditorInput);
    toolTip.setPopupDelay(0);
    toolTip.activate();
    toolTip.setShift(new Point(XCOORDINATE_TEN, YCOORDINATE_TEN));
  }

  /**
   * handleGroupColumnsCommand method in ColumnGroupsCommandHandler class
   */
  private void groupColumns() {
    List<Integer> selectedPositions = new ArrayList<>();
    selectedPositions.add(ALL_CHECKBOX_COLUMN_IDX);
    selectedPositions.add(NONE_CHECKBOX_COLUMN_IDX);
    int[] fullySelectedColumns = new int[] { ALL_CHECKBOX_COLUMN_IDX, NONE_CHECKBOX_COLUMN_IDX };
    ColumnGroupModel columnGroupModel = this.ucFilterGridLayer.getColumnGroupModel();
    columnGroupModel.addColumnsIndexesToGroup("Select", fullySelectedColumns);
    columnGroupModel.setColumnGroupCollapseable(ALL_CHECKBOX_COLUMN_IDX, false);
    SelectionLayer selectionLayer = this.ucFilterGridLayer.getBodyLayer().getSelectionLayer();
    selectionLayer.doCommand(
        new MultiColumnReorderCommand(selectionLayer, selectedPositions, selectedPositions.get(0).intValue()));
    selectionLayer.clear();


  }

  /**
   *
   */
  private void consolidateUseCaseColumnGrouping() {
    if (this.cachedUseCaseItems == null) {
      this.cachedUseCaseItems = new ArrayList<>(this.columnUseCaseItemMapping.values());
    }
    List<Integer> selectedPositions = new ArrayList<>();
    for (int i = STATIC_COLUMN_INDEX; i < this.propertyToLabelMap.size(); i++) {
      selectedPositions.add(i);
    }
    int[] fullySelectedColumns = new int[selectedPositions.size()];
    for (int i = 0; i < selectedPositions.size(); i++) {
      fullySelectedColumns[i] = selectedPositions.get(i);
    }
    ColumnGroupModel columnGroupModel = this.ucFilterGridLayer.getColumnGroupModel();
    columnGroupModel.addColumnsIndexesToGroup(USE_CASE_ITEMS, fullySelectedColumns);
    SelectionLayer selectionLayer = this.ucFilterGridLayer.getBodyLayer().getSelectionLayer();
    if (!selectedPositions.isEmpty()) {
      selectionLayer.doCommand(
          new MultiColumnReorderCommand(selectionLayer, selectedPositions, selectedPositions.get(0).intValue()));
      selectionLayer.clear();
    }

  }

  /**
   *
   */
  private void consolidateUseCaseColumnGroupingForNewColumn() {
    List<Integer> selectedPositions = new ArrayList<>();
    for (int i = 0; i < (this.cachedUseCaseItems.size()); i++) {
      selectedPositions.add(i + STATIC_COLUMN_INDEX);
    }
    int[] fullySelectedColumns = new int[selectedPositions.size()];
    for (int i = 0; i < selectedPositions.size(); i++) {
      fullySelectedColumns[i] = selectedPositions.get(i);
    }
    ColumnGroupModel columnGroupModel = this.ucFilterGridLayer.getColumnGroupModel();
    columnGroupModel.removeColumnGroup(columnGroupModel.getColumnGroupByName(USE_CASE_ITEMS));
    columnGroupModel.addColumnsIndexesToGroup(USE_CASE_ITEMS, fullySelectedColumns);
    SelectionLayer selectionLayer = this.ucFilterGridLayer.getBodyLayer().getSelectionLayer();
    selectionLayer.doCommand(
        new MultiColumnReorderCommand(selectionLayer, selectedPositions, selectedPositions.get(0).intValue()));
    selectionLayer.clear();
  }

  /**
   *
   */
  private void consolidateColumns() {
    List<Integer> ucColumnNums = new ArrayList<>();
    if ((this.natTable != null) && (this.ucFilterGridLayer != null)) {
      for (int i = STATIC_COLUMN_INDEX; i < this.ucFilterGridLayer.getBodyLayer().getColumnReorderLayer()
          .getColumnCount(); i++) {
        ucColumnNums.add(i);
      }
    }
    updatePropertyToLabelMap();

    if (this.ucFilterGridLayer == null) {
      return;
    }

    List<ColumnEntry> hiddenColumnEntries =
        ColumnChooserUtils.getHiddenColumnEntries(this.ucFilterGridLayer.getBodyLayer().getColumnHideShowLayer(),
            this.ucFilterGridLayer.getColumnHeaderLayer(), this.ucFilterGridLayer.getColumnHeaderDataLayer());
    List<ColumnEntry> visibleColumnsEntries =
        ColumnChooserUtils.getVisibleColumnsEntries(this.ucFilterGridLayer.getBodyLayer().getColumnHideShowLayer(),
            this.ucFilterGridLayer.getColumnHeaderLayer(), this.ucFilterGridLayer.getColumnHeaderDataLayer());

    List<ColumnEntry> allColumnEntries = new ArrayList<>();
    allColumnEntries.addAll(visibleColumnsEntries);
    allColumnEntries.addAll(hiddenColumnEntries);
    List<ColumnEntry> columnEntryToHide = new ArrayList<>();
    List<ColumnEntry> columnEntryToShow = new ArrayList<>();

    for (ColumnEntry colEntry : allColumnEntries) {
      if (colEntry.getIndex() < STATIC_COLUMN_INDEX) {
        continue;
      }
      if (this.columnUseCaseItemMapping.get(colEntry.getIndex()) == null) {
        if (!hiddenColumnEntries.contains(colEntry)) {
          columnEntryToHide.add(colEntry);
        }
      }
      else {
        columnEntryToShow.add(colEntry);
      }
    }
    if (!columnEntryToHide.isEmpty()) {
      ColumnChooserUtils.hideColumnEntries(columnEntryToHide,
          this.ucFilterGridLayer.getBodyLayer().getColumnHideShowLayer());
    }
    if (!columnEntryToShow.isEmpty()) {
      ColumnChooserUtils.showColumnEntries(columnEntryToShow,
          this.ucFilterGridLayer.getBodyLayer().getColumnHideShowLayer());
    }

    if (this.natTable != null) {
      consolidateSorting();
    }

  }

  /**
   *
   */
  private void updatePropertyToLabelMap() {
    int size = this.propertyToLabelMap.size();
    this.columnUseCaseItemMapping.clear();
    for (int i = STATIC_COLUMN_INDEX; i < size; i++) {
      this.propertyToLabelMap.remove(i);
    }

    for (IUseCaseItemClientBO useCaseItem : this.mappableUcItems) {
      this.columnUseCaseItemMapping.put(this.propertyToLabelMap.size(), useCaseItem);
      this.propertyToLabelMap.put(this.propertyToLabelMap.size(), useCaseItem.getName());
    }
  }

  private void registerConfigLabelsOnColumns(final ColumnOverrideLabelAccumulator columnLabelAccumulator,
      final boolean onlyUseCase) {

    if (!onlyUseCase) {
      columnLabelAccumulator.registerColumnOverrides(ALL_CHECKBOX_COLUMN_IDX,
          CHECK_BOX_EDITOR_CNG_LBL + UNDERSCORE + ALL_CHECKBOX_COLUMN_IDX,
          CHECK_BOX_CONFIG_LABEL + UNDERSCORE + ALL_CHECKBOX_COLUMN_IDX);
      columnLabelAccumulator.registerColumnOverrides(NONE_CHECKBOX_COLUMN_IDX,
          CHECK_BOX_EDITOR_CNG_LBL + UNDERSCORE + NONE_CHECKBOX_COLUMN_IDX,
          CHECK_BOX_CONFIG_LABEL + UNDERSCORE + NONE_CHECKBOX_COLUMN_IDX);
      columnLabelAccumulator.registerColumnOverrides(ANY_COLUMN_IDX,
          CHECK_BOX_EDITOR_CNG_LBL + UNDERSCORE + ANY_COLUMN_IDX, CHECK_BOX_CONFIG_LABEL + UNDERSCORE + ANY_COLUMN_IDX);
    }
    for (int i = STATIC_COLUMN_INDEX; i < (this.cachedUseCaseItems.size() + STATIC_COLUMN_INDEX); i++) {
      columnLabelAccumulator.registerColumnOverrides(i, CHECK_BOX_EDITOR_CNG_LBL + UNDERSCORE + i,
          CHECK_BOX_CONFIG_LABEL + UNDERSCORE + i);
    }


  }

  private void registerCheckBoxEditor(final IConfigRegistry configRegistry, final boolean onlyUseCase) {
    if (!onlyUseCase) {
      // All column
      Style cellStyleAll = new Style();
      cellStyleAll.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
      configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyleAll, NORMAL,
          CHECK_BOX_CONFIG_LABEL + UNDERSCORE + ALL_CHECKBOX_COLUMN_IDX);

      configRegistry.registerConfigAttribute(CELL_PAINTER, new CheckBoxPainter(), NORMAL,
          CHECK_BOX_CONFIG_LABEL + UNDERSCORE + ALL_CHECKBOX_COLUMN_IDX);
      configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER,
          new DefaultBooleanDisplayConverter(), NORMAL, CHECK_BOX_CONFIG_LABEL + UNDERSCORE + ALL_CHECKBOX_COLUMN_IDX);

      UcNatAttrTableCheckBoxCellEditor checkBoxCellEditorAll = getCheckBoxCellEditorAll();

      configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, checkBoxCellEditorAll, NORMAL,
          CHECK_BOX_EDITOR_CNG_LBL + UNDERSCORE + ALL_CHECKBOX_COLUMN_IDX);

      // None column
      Style cellStyleNone = new Style();
      cellStyleNone.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
      configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyleNone, NORMAL,
          CHECK_BOX_CONFIG_LABEL + UNDERSCORE + NONE_CHECKBOX_COLUMN_IDX);

      configRegistry.registerConfigAttribute(CELL_PAINTER, new CheckBoxPainter(), NORMAL,
          CHECK_BOX_CONFIG_LABEL + UNDERSCORE + NONE_CHECKBOX_COLUMN_IDX);
      configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER,
          new DefaultBooleanDisplayConverter(), NORMAL, CHECK_BOX_CONFIG_LABEL + UNDERSCORE + NONE_CHECKBOX_COLUMN_IDX);

      UcNatAttrTableCheckBoxCellEditor checkBoxCellEditorNone = getCheckBoxCellEditorNone();

      configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, checkBoxCellEditorNone, NORMAL,
          CHECK_BOX_EDITOR_CNG_LBL + UNDERSCORE + NONE_CHECKBOX_COLUMN_IDX);

      // Any column
      Style cellStyleAny = new Style();
      cellStyleAny.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
      configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyleAny, NORMAL,
          CHECK_BOX_CONFIG_LABEL + UNDERSCORE + ANY_COLUMN_IDX);
    }


    // image for quotation flag
    registerImagePainterConfig();

    for (int i = STATIC_COLUMN_INDEX; i < (this.cachedUseCaseItems.size() + STATIC_COLUMN_INDEX); i++) {
      Style cellStyleUC = new Style();
      cellStyleUC.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
      configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyleUC, NORMAL,
          CHECK_BOX_CONFIG_LABEL + UNDERSCORE + i);

      // create decorator on top of quotation image painter decorator to have both checkbox and image in same cell
      CellPainterDecorator checkboxDecorator =
          new CellPainterDecorator(this.quotPainterDecorator, CellEdgeEnum.TOP, new CheckBoxPainter(), false);

      configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, checkboxDecorator, DisplayMode.NORMAL,
          CHECK_BOX_CONFIG_LABEL + UNDERSCORE + i);

      configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER,
          new DefaultBooleanDisplayConverter(), NORMAL, CHECK_BOX_CONFIG_LABEL + UNDERSCORE + i);
      final Integer columnIndex = i;
      UcNatAttrTableCheckBoxCellEditor checkBoxCellEditor = getCheckBoxCellEditor(columnIndex);
      configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, checkBoxCellEditor, NORMAL,
          CHECK_BOX_EDITOR_CNG_LBL + UNDERSCORE + i);


    }


  }

  /**
   * @param columnIndex
   * @return
   */
  private UcNatAttrTableCheckBoxCellEditor getCheckBoxCellEditor(final Integer columnIndex) {
    return new UcNatAttrTableCheckBoxCellEditor() {

      /**
       * {@inheritDoc}
       */
      @Override
      protected Control activateCell(final Composite parent, final Object originalCanonicalValue) {
        Control control = super.activateCell(parent, originalCanonicalValue);


        BusyIndicator.showWhile(Display.getDefault().getActiveShell().getDisplay(), new Runnable() {

          @Override
          public void run() {
            IStructuredSelection selection =
                (IStructuredSelection) UseCaseNatAttributesPage.this.selectionProvider.getSelection();
            if ((null != selection) && (null != selection.getFirstElement())) {
              final AttributeClientBO attribute = ((UseCaseEditorRowAttr) selection.getFirstElement()).getAttributeBO();
              IUseCaseItemClientBO ucItem = UseCaseNatAttributesPage.this.columnUseCaseItemMapping.get(columnIndex);
              if (!ucItem.isDeleted()) {
                insertOrDeleteUCPAttr(attribute, ucItem, UseCaseNatAttributesPage.this,
                    UseCaseNatAttributesPage.this.editor.getEditorInput().getUseCaseEditorModel());
              }
            }
          }

        });


        return control;

      }

    };
  }

  /**
   * @return
   */
  private UcNatAttrTableCheckBoxCellEditor getCheckBoxCellEditorNone() {
    return new UcNatAttrTableCheckBoxCellEditor() {

      /**
       * {@inheritDoc}
       */
      @Override
      protected Control activateCell(final Composite parent, final Object originalCanonicalValue) {
        Control control = super.activateCell(parent, originalCanonicalValue);
        Runnable busyRunnable = new Runnable() {

          @Override
          public void run() {
            if (getEditorValue()) {
              IStructuredSelection selection =
                  (IStructuredSelection) UseCaseNatAttributesPage.this.selectionProvider.getSelection();
              final AttributeClientBO attribute = ((UseCaseEditorRowAttr) selection.getFirstElement()).getAttributeBO();
              final SortedSet<IUseCaseItemClientBO> notDeletedUCItems = getNotDeleledUCItems();
              // Changes For Select all None icdm-301
              deleteAllAttrMappings(attribute, notDeletedUCItems, UseCaseNatAttributesPage.this);
            }
          }


        };
        BusyIndicator.showWhile(PlatformUI.getWorkbench().getDisplay(), busyRunnable);
        return control;

        // Get ApicDataProvider instance
      }

    };
  }

  /**
   * @return
   */
  private UcNatAttrTableCheckBoxCellEditor getCheckBoxCellEditorAll() {
    return new UcNatAttrTableCheckBoxCellEditor() {

      /**
       * {@inheritDoc}
       */
      @Override
      protected Control activateCell(final Composite parent, final Object originalCanonicalValue) {
        Control control = super.activateCell(parent, originalCanonicalValue);
        Runnable busyRunnable = () -> {
          if (getEditorValue() && (UseCaseNatAttributesPage.this.clickEventVal == LEFT_MOUSE_CLICK)) {
            // Get UsecaseItem attribute
            IStructuredSelection selection =
                (IStructuredSelection) UseCaseNatAttributesPage.this.selectionProvider.getSelection();
            final AttributeClientBO attributeBO = ((UseCaseEditorRowAttr) selection.getFirstElement()).getAttributeBO();
            final SortedSet<IUseCaseItemClientBO> notDeletedUCItems = getNotDeleledUCItems();
            // Changes For Select all None icdm-301
            // Code Refactoring
            createUcpAttrForAll(attributeBO, notDeletedUCItems, UseCaseNatAttributesPage.this);
            // reset the clickEventVal to avoid creation of Ucp Attrs again which in turn causes db constraint violation
            UseCaseNatAttributesPage.this.clickEventVal = 0;
          }


        };
        BusyIndicator.showWhile(PlatformUI.getWorkbench().getDisplay(), busyRunnable);
        return control;
      }

    };
  }


  private void registerEditableRules(final IConfigRegistry configRegistry, final boolean onlyUseCase) {

    // ICDM-2610
    // Editing logic of Select groups [All, None, Any] group columns
    if (!onlyUseCase && getUseCase().isMappingModifiable()) {
      configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE,
          DisplayMode.EDIT, CHECK_BOX_CONFIG_LABEL + UNDERSCORE + ALL_CHECKBOX_COLUMN_IDX);
      configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE,
          DisplayMode.EDIT, CHECK_BOX_CONFIG_LABEL + UNDERSCORE + NONE_CHECKBOX_COLUMN_IDX);
    }

    // Sorting logic of Select groups [All, None, Any] group columns
    ((ColumnOverrideLabelAccumulator) this.ucFilterGridLayer.getColumnHeaderDataLayer().getConfigLabelAccumulator())
        .registerColumnOverrides(ALL_CHECKBOX_COLUMN_IDX, CUSTOM_COMPARATOR_LABEL + ALL_CHECKBOX_COLUMN_IDX);
    configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
        getUCStatColumnComparator(this, ALL_CHECKBOX_COLUMN_IDX), NORMAL,
        CUSTOM_COMPARATOR_LABEL + ALL_CHECKBOX_COLUMN_IDX);

    ((ColumnOverrideLabelAccumulator) this.ucFilterGridLayer.getColumnHeaderDataLayer().getConfigLabelAccumulator())
        .registerColumnOverrides(NONE_CHECKBOX_COLUMN_IDX, CUSTOM_COMPARATOR_LABEL + NONE_CHECKBOX_COLUMN_IDX);
    configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
        getUCStatColumnComparator(this, NONE_CHECKBOX_COLUMN_IDX), NORMAL,
        CUSTOM_COMPARATOR_LABEL + NONE_CHECKBOX_COLUMN_IDX);

    ((ColumnOverrideLabelAccumulator) this.ucFilterGridLayer.getColumnHeaderDataLayer().getConfigLabelAccumulator())
        .registerColumnOverrides(ANY_COLUMN_IDX, CUSTOM_COMPARATOR_LABEL + ANY_COLUMN_IDX);
    configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
        getUCStatColumnComparator(this, ANY_COLUMN_IDX), NORMAL, CUSTOM_COMPARATOR_LABEL + ANY_COLUMN_IDX);

    for (int i = STATIC_COLUMN_INDEX; i < (this.cachedUseCaseItems.size() + STATIC_COLUMN_INDEX); i++) {
      IUseCaseItemClientBO useCaseItem;
      if (onlyUseCase) {
        // When onlyUseCase is true it means the last element is the newly added element
        useCaseItem = this.cachedUseCaseItems.get(this.cachedUseCaseItems.size() - 1);
      }
      else {
        useCaseItem = this.columnUseCaseItemMapping.get(i);
      }

      // ICDM-2610
      // Editing logic of Use-Case-Item columns
      if ((useCaseItem != null) && useCaseItem.isModifyCellAllowed()) {
        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE,
            DisplayMode.EDIT, CHECK_BOX_CONFIG_LABEL + UNDERSCORE + i);
      }

      // Sorting logic of Use-Case-Item columns
      ((ColumnOverrideLabelAccumulator) this.ucFilterGridLayer.getColumnHeaderDataLayer().getConfigLabelAccumulator())
          .registerColumnOverrides(i, CUSTOM_COMPARATOR_LABEL + i);
      configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR, getUCItemComparator(useCaseItem),
          NORMAL, CUSTOM_COMPARATOR_LABEL + i);
      if (onlyUseCase) {
        // break if onlyUseCase is true since at a time only one column can be added
        break;
      }
    }
  }

  private void consolidateSorting() {
    for (Entry<Integer, IUseCaseItemClientBO> colUseCaseItmMappingEntry : this.columnUseCaseItemMapping.entrySet()) {
      ((ColumnOverrideLabelAccumulator) this.ucFilterGridLayer.getColumnHeaderDataLayer().getConfigLabelAccumulator())
          .registerColumnOverrides(colUseCaseItmMappingEntry.getKey(),
              CUSTOM_COMPARATOR_LABEL + colUseCaseItmMappingEntry.getKey());
      this.natTable.getConfigRegistry().registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
          getUCItemComparator(colUseCaseItmMappingEntry.getValue()), NORMAL,
          CUSTOM_COMPARATOR_LABEL + colUseCaseItmMappingEntry.getKey());
    }
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
   * @return
   */
  private static Comparator getUCComparator(final int columnNum) {

    return new Comparator<UseCaseEditorRowAttr>() {

      public int compare(final UseCaseEditorRowAttr useCaseNatInput1, final UseCaseEditorRowAttr useCaseNatInput2) {
        int ret = 0;
        switch (columnNum) {
          case COLUMN_NUM_BALL:
            // No compare for ball image column
            break;
          case ATTR_COLUMN_IDX:
            ret = useCaseNatInput1.getAttributeBO().compareTo(useCaseNatInput2.getAttributeBO(),
                ApicConstants.SORT_ATTRNAME);
            break;
          case DESC_COLUMN_IDX:
            ret = useCaseNatInput1.getAttributeBO().compareTo(useCaseNatInput2.getAttributeBO(),
                ApicConstants.SORT_ATTRDESCR);
            break;
          case CREATION_DATE_COLUMN_IDX:
            ret = useCaseNatInput1.getAttributeBO().compareTo(useCaseNatInput2.getAttributeBO(),
                ApicConstants.SORT_ATTR_CREATED_DATE_PIDC);
            break;
          case ATTR_CLASS_COLUMN_IDX:
            ret =
                useCaseNatInput1.getAttributeBO().compareTo(useCaseNatInput2.getAttributeBO(), ApicConstants.SORT_CHAR);
            break;
          default:
            ret = 0;
        }
        return ret;
      }
    };
  }

  /**
   * @param useCaseEditorModel2
   * @return
   */
  private static Comparator getUCItemComparator(final IUseCaseItemClientBO useCaseItem) {

    return new Comparator<UseCaseEditorRowAttr>() {

      public int compare(final UseCaseEditorRowAttr useCaseNatInput1, final UseCaseEditorRowAttr useCaseNatInput2) {
        int ret = 0;

        Boolean isP2Mapped = useCaseItem.isMapped(useCaseNatInput2.getAttributeBO().getAttribute());
        Boolean isP1Mapped = useCaseItem.isMapped(useCaseNatInput1.getAttributeBO().getAttribute());
        ret = isP2Mapped.compareTo(isP1Mapped);
        if (ret == 0) {
          ret = useCaseNatInput1.getAttributeBO().compareTo(useCaseNatInput2.getAttributeBO(),
              ApicConstants.SORT_ATTRNAME);
        }
        return ret;
      }
    };
  }

  /**
   * 0 - All Column 1 - None Column 2 - Any Column
   *
   * @return
   */
  private static Comparator getUCStatColumnComparator(final UseCaseNatAttributesPage useCaseNatAttributesPage,
      final int columnNum) {

    return new Comparator<UseCaseEditorRowAttr>() {

      public int compare(final UseCaseEditorRowAttr useCaseNatInput1, final UseCaseEditorRowAttr useCaseNatInput2) {
        int resultComparison;
        Object attributeData1 =
            useCaseNatAttributesPage.getNatInputToColumnConverter().getAttributeData(useCaseNatInput1, columnNum);
        Object attributeData2 =
            useCaseNatAttributesPage.getNatInputToColumnConverter().getAttributeData(useCaseNatInput2, columnNum);

        Boolean isP1Mapped = TICK_UTF_ENCODED.equals(attributeData1) ? TICK_UTF_ENCODED.equals(attributeData1)
            : checkForEmptyString(attributeData1);
        Boolean isP2Mapped = TICK_UTF_ENCODED.equals(attributeData2) ? TICK_UTF_ENCODED.equals(attributeData2)
            : checkForEmptyString(attributeData2);
        resultComparison = isP2Mapped.compareTo(isP1Mapped);
        if (resultComparison == 0) {
          resultComparison = useCaseNatInput1.getAttributeBO().compareTo(useCaseNatInput2.getAttributeBO(),
              ApicConstants.SORT_ATTRNAME);
        }
        return resultComparison;
      }

      /**
       * method will be invoked only when the attributeData value is "" or boolean value(ALL, NONE check box status)
       *
       * @param attributeData
       * @return
       */
      private boolean checkForEmptyString(final Object attributeData) {
        if (!"".equals(attributeData)) {
          return (Boolean) attributeData;
        }
        return false;
      }
    };
  }

  /**
   * This method creates filter text
   */
  private void addModifyListenerForFilterTxt() {
    this.filterTxt.addModifyListener(event -> {
      final String text = UseCaseNatAttributesPage.this.filterTxt.getText().trim();
      UseCaseNatAttributesPage.this.allColumnFilterMatcher.setFilterText(text, true);
      UseCaseNatAttributesPage.this.ucFilterGridLayer.getFilterStrategy().applyFilterInAllColumns(text);
      UseCaseNatAttributesPage.this.ucFilterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
          new FilterAppliedEvent(UseCaseNatAttributesPage.this.ucFilterGridLayer.getSortableColumnHeaderLayer()));
      setStatusBarMessage(false, false);
    });
  }

  /**
   * @param outlineSelection boolean
   * @param detailsViewSelection boolean
   */
  public void setStatusBarMessage(final boolean outlineSelection, final boolean detailsViewSelection) {
    this.editor.updateStatusBar(outlineSelection, detailsViewSelection, this.totTableRowCount,
        this.ucFilterGridLayer.getRowHeaderLayer().getPreferredRowCount());
  }

  @Override
  public void dispose() {
    getSite().getPage().removeSelectionListener(this);
    IViewPart viewPart = null;
    if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage() != null) {
      viewPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
          .findView(AttributeValuesViewPart.PART_ID);
    }
    // Icdm-292
    if (viewPart != null) {
      ((AttributeValuesViewPart) viewPart).setSelectedAttr(null);
    }
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
   * This method returns not deleted usecase items
   *
   * @return SortedSet<IUseCaseItemClientBO>
   */
  private SortedSet<IUseCaseItemClientBO> getNotDeleledUCItems() {
    // Get not-deleted mappble usecase items
    final SortedSet<IUseCaseItemClientBO> notDeletedUCItems = new TreeSet<>();

    for (IUseCaseItemClientBO ucItem : UseCaseNatAttributesPage.this.mappableUcItems) {
      if (!ucItem.isDeleted()) {
        notDeletedUCItems.add(ucItem);
      }
    }
    return notDeletedUCItems;
  }

  private boolean setNoneState(final Attribute attribute) {
    boolean isChecked;
    boolean isAllUCItemsUnMapped = true;
    for (IUseCaseItemClientBO ucItem : UseCaseNatAttributesPage.this.mappableUcItems) {
      // Skip the deleted Items
      if (!attribute.isDeleted() && !ucItem.isDeleted() && ucItem.isMapped(attribute)) {
        isAllUCItemsUnMapped = false;
        break;
      }
    }
    if (isAllUCItemsUnMapped) {
      isChecked = true;
    }
    else {
      isChecked = false;
    }
    return isChecked;
  }

  // ICDM-289
  /**
   * This method creates Section ToolBar actions
   */
  private void createToolBarAction() {
    this.toolBarManager = new ToolBarManager(SWT.FLAT);


    final ToolBar toolbar = this.toolBarManager.createControl(this.section);

    final Separator separator = new Separator();

    UsecaseNatAttributesPageToolBarActionSet toolBarActionSet =
        new UsecaseNatAttributesPageToolBarActionSet(this, this.ucFilterGridLayer);

    // filter for quotation relevant attributes
    toolBarActionSet.createQuotationRelevantFilterAction(this.toolBarManager, this.toolBarFilters);

    // filter for non quotation relevant attributes
    toolBarActionSet.createQuotationNotRelevantFilterAction(this.toolBarManager, this.toolBarFilters);

    this.toolBarManager.add(separator);
    // filter for mandatory attributes
    toolBarActionSet.createAttrMandatoryFilterAction(this.toolBarManager, this.toolBarFilters);

    // filter for non mandatory attributes
    toolBarActionSet.createAttrNonMandatoryFilterAction(this.toolBarManager, this.toolBarFilters);

    this.toolBarManager.add(separator);
    // filter for dependent attributes
    toolBarActionSet.createAttrDepenFilterAction(this.toolBarManager, this.toolBarFilters);

    // filter for non dependent attributes
    toolBarActionSet.createAttrNonDepenFilterAction(this.toolBarManager, this.toolBarFilters);

    this.toolBarManager.add(separator);
    toolBarActionSet.createUseCaseAttrAllFilterAction(this.toolBarManager, this.toolBarFilters);

    toolBarActionSet.createUseCaseAttrNoneFilterAction(this.toolBarManager, this.toolBarFilters);

    toolBarActionSet.createUseCaseAttrAnyFilterAction(this.toolBarManager, this.toolBarFilters);

    this.toolBarManager.update(true);
    addResetAllFiltersAction();
    this.section.setTextClient(toolbar);

    // Action for usecase up to date confirmation
    final ToolBarManager titleBarManager = (ToolBarManager) this.nonScrollableForm.getToolBarManager();
    addHelpAction(titleBarManager);
    this.ucUpToDateAction = new UsecaseUpToDateAction(true, getUseCase());
    titleBarManager.add(this.ucUpToDateAction);
    this.ucNotUpToDateAction = new UsecaseUpToDateAction(false, getUseCase());
    titleBarManager.add(this.ucNotUpToDateAction);
    titleBarManager.update(true);

  }


  // this method is added to prevent
  // "java.lang.RuntimeException: WARNING: Prevented recursive attempt to activate part
  // org.eclipse.ui.views.PropertySheet while still in the middle of activating part"
  @Override
  public void setFocus() {
    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setFocus();
  }


  /**
   * Add reset filter button ICDM-1207
   */
  private void addResetAllFiltersAction() {

    getFilterTxtSet().add(this.filterTxt);
    getRefreshComponentSet().add(this.ucFilterGridLayer);
    addResetFiltersAction();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateStatusBar(final boolean outlineSelection) {
    if (this.editor.getActivePage() == 0) {
      super.updateStatusBar(outlineSelection);
      // TODO: Need to check condition for DetailsViewPart Flag
      setStatusBarMessage(outlineSelection, false);
    }

  }

  private IConfiguration getCustomComparatorConfiguration(final AbstractLayer columnHeaderDataLayer) {

    return new AbstractRegistryConfiguration() {

      public void configureRegistry(final IConfigRegistry configRegistry) {
        // Add label accumulator
        ColumnOverrideLabelAccumulator labelAccumulator = new ColumnOverrideLabelAccumulator(columnHeaderDataLayer);
        columnHeaderDataLayer.setConfigLabelAccumulator(labelAccumulator);

        // Register labels
        labelAccumulator.registerColumnOverrides(COLUMN_NUM_BALL, CUSTOM_COMPARATOR_LABEL + COLUMN_NUM_BALL);

        labelAccumulator.registerColumnOverrides(ATTR_COLUMN_IDX, CUSTOM_COMPARATOR_LABEL + ATTR_COLUMN_IDX);

        labelAccumulator.registerColumnOverrides(DESC_COLUMN_IDX, CUSTOM_COMPARATOR_LABEL + DESC_COLUMN_IDX);

        labelAccumulator.registerColumnOverrides(CREATION_DATE_COLUMN_IDX,
            CUSTOM_COMPARATOR_LABEL + CREATION_DATE_COLUMN_IDX);

        labelAccumulator.registerColumnOverrides(ATTR_CLASS_COLUMN_IDX,
            CUSTOM_COMPARATOR_LABEL + ATTR_CLASS_COLUMN_IDX);


        // Register null comparator to disable sort
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR, new NullComparator(), NORMAL,
            CUSTOM_COMPARATOR_LABEL + COLUMN_NUM_BALL);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR, getUCComparator(ATTR_COLUMN_IDX),
            NORMAL, CUSTOM_COMPARATOR_LABEL + ATTR_COLUMN_IDX);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR, getUCComparator(DESC_COLUMN_IDX),
            NORMAL, CUSTOM_COMPARATOR_LABEL + DESC_COLUMN_IDX);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getUCComparator(CREATION_DATE_COLUMN_IDX), NORMAL, CUSTOM_COMPARATOR_LABEL + CREATION_DATE_COLUMN_IDX);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getUCComparator(ATTR_CLASS_COLUMN_IDX), NORMAL, CUSTOM_COMPARATOR_LABEL + ATTR_CLASS_COLUMN_IDX);

      }
    };
  }


  /**
   *
   */
  public void refreshToolBarFilters() {
    UseCaseNatAttributesPage.this.ucFilterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
    UseCaseNatAttributesPage.this.ucFilterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
        new FilterAppliedEvent(UseCaseNatAttributesPage.this.ucFilterGridLayer.getSortableColumnHeaderLayer()));
  }


  private static class FilterRowCustomConfiguration extends AbstractRegistryConfiguration {

    final DefaultDoubleDisplayConverter doubleDisplayConverter = new DefaultDoubleDisplayConverter();

    public void configureRegistry(final IConfigRegistry configRegistry) {
      // override the default filter row configuration for painter
      configRegistry.registerConfigAttribute(CELL_PAINTER,
          new FilterRowPainter(new FilterIconPainter(GUIHelper.getImage("filter"))), NORMAL, FILTER_ROW);


      // TODO: Below four lines can be removed. To be checked
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_COMPARATOR, getIgnorecaseComparator(),
          NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 1);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_COMPARATOR, getIgnorecaseComparator(),
          NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 2);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_COMPARATOR, getIgnorecaseComparator(),
          NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 3);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_COMPARATOR, getIgnorecaseComparator(),
          NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 4);

      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE,
          TextMatchingMode.REGULAR_EXPRESSION, NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 1);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE,
          TextMatchingMode.REGULAR_EXPRESSION, NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 2);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE,
          TextMatchingMode.REGULAR_EXPRESSION, NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 3);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE,
          TextMatchingMode.REGULAR_EXPRESSION, NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 4);

    }
  }

  private static Comparator<?> getIgnorecaseComparator() {
    return new Comparator<String>() {

      public int compare(final String str1, final String str2) {
        return str1.compareToIgnoreCase(str2);
      }
    };
  }

  public class UseCaseNatInputToColumnConverter extends AbstractNatInputToColumnConverter {

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getColumnValue(final Object evaluateObj, final int colIndex) {
      Object result = null;
      if (evaluateObj instanceof UseCaseEditorRowAttr) {
        result = getAttributeData((UseCaseEditorRowAttr) evaluateObj, colIndex);
      }
      return result;
    }

    /**
     * @param evaluateObj
     * @param colIndex
     * @return
     * @throws ParseException
     */
    private Object getAttributeData(final UseCaseEditorRowAttr useCaseNatInput, final int colIndex) {
      Object result;
      switch (colIndex) {
        case ATTR_COLUMN_IDX:
          result = useCaseNatInput.getAttrName();
          break;
        case DESC_COLUMN_IDX:
          result = useCaseNatInput.getAttrDescription();
          break;
        case CREATION_DATE_COLUMN_IDX:
          result = useCaseNatInput.getAttrCreatedDateFormatted();
          break;
        case ATTR_CLASS_COLUMN_IDX:
          result = useCaseNatInput.getAttrClassName();
          break;
        case ALL_CHECKBOX_COLUMN_IDX:
          result = useCaseNatInput.isMappedToAll(UseCaseNatAttributesPage.this.mappableUcItems);
          break;
        case NONE_CHECKBOX_COLUMN_IDX:
          result = useCaseNatInput.isMappedToNone(UseCaseNatAttributesPage.this.mappableUcItems);
          break;
        case ANY_COLUMN_IDX:
          result = useCaseNatInput.isMappedToAny(UseCaseNatAttributesPage.this.mappableUcItems) ? TICK_UTF_ENCODED : "";
          break;

        default:
          // removed null initialization to result.
          IUseCaseItemClientBO ucItem = UseCaseNatAttributesPage.this.columnUseCaseItemMapping.get(colIndex);
          result = useCaseNatInput.isUCItemMapped(ucItem);
          break;
      }
      return result;
    }
  }


  class CustomUseCaseColumnPropertyAccessor<T> implements IColumnAccessor<T> {

    private int columnCount;

    /**
     * Constructor
     *
     * @param columnCount int
     */
    public CustomUseCaseColumnPropertyAccessor() {
      this.columnCount = UseCaseNatAttributesPage.this.propertyToLabelMap.size();
    }

    /**
     * This method has been overridden so that it returns the passed row object. The above behavior is required for use
     * of custom comparators for sorting which requires the Row object to be passed without converting to a particular
     * column String value {@inheritDoc}
     */
    @Override
    public Object getDataValue(final T type, final int columnIndex) {
      return type;
    }


    @Override
    public void setDataValue(final T sysConstNatModel, final int columnIndex, final Object newValue) {
      // TODO:
    }

    @Override
    public int getColumnCount() {
      int cacheSize;
      if (UseCaseNatAttributesPage.this.cachedUseCaseItems != null) {
        cacheSize = UseCaseNatAttributesPage.this.cachedUseCaseItems.size();
        this.columnCount = cacheSize + STATIC_COLUMN_INDEX;
      }
      return this.columnCount;
    }


  }

  class CustomUseCaseColumnHeaderDataProvider implements IDataProvider {


    /**
     * @param columnIndex int
     * @return String
     */
    public String getColumnHeaderLabel(final int columnIndex) {
      String string = UseCaseNatAttributesPage.this.propertyToLabelMap.get(columnIndex);

      return string == null ? "" : string;
    }

    @Override
    public int getColumnCount() {
      if (UseCaseNatAttributesPage.this.cachedUseCaseItems != null) {
        int cacheSize = UseCaseNatAttributesPage.this.cachedUseCaseItems.size();
        return cacheSize + STATIC_COLUMN_INDEX;
      }
      return UseCaseNatAttributesPage.this.propertyToLabelMap.size();
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
   * @return the natInputToColumnConverter
   */
  public UseCaseNatInputToColumnConverter getNatInputToColumnConverter() {
    return this.natInputToColumnConverter;
  }


  /**
   * @return the ucFilterGridLayer
   */
  public CustomFilterGridLayer getUcFilterGridLayer() {
    return this.ucFilterGridLayer;
  }


  /**
   * @return the natTable
   */
  public NatTable getNatTable() {
    return this.natTable;
  }


  /**
   * @return the outLineNatFilter
   */
  public OutlineUCNatFilter getOutLineNatFilter() {
    return this.outLineNatFilter;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IToolBarManager getToolBarManager() {
    return this.toolBarManager;
  }

  /**
   * @return the nonScrollableForm
   */
  public Form getNonScrollableForm() {
    return this.nonScrollableForm;
  }

  /**
   * @param nonScrollableForm the nonScrollableForm to set
   */
  public void setNonScrollableForm(final Form nonScrollableForm) {
    this.nonScrollableForm = nonScrollableForm;
  }

  /**
   * @return the ucUpToDateAction
   */
  public UsecaseUpToDateAction getUcUpToDateAction() {
    return this.ucUpToDateAction;
  }

  /**
   * @param ucUpToDateAction the ucUpToDateAction to set
   */
  public void setUcUpToDateAction(final UsecaseUpToDateAction ucUpToDateAction) {
    this.ucUpToDateAction = ucUpToDateAction;
  }


  /**
   * @return the ucNotUpToDateAction
   */
  public UsecaseUpToDateAction getUcNotUpToDateAction() {
    return this.ucNotUpToDateAction;
  }

  /**
   * @param ucNotUpToDateAction the ucNotUpToDateAction to set
   */
  public void setUcNotUpToDateAction(final UsecaseUpToDateAction ucNotUpToDateAction) {
    this.ucNotUpToDateAction = ucNotUpToDateAction;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IClientDataHandler getDataHandler() {
    return this.editor.getEditorInput();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent dce) {
    for (Entry<IModelType, Map<Long, ChangeData<?>>> changeData : dce.getConsChangeData().entrySet()) {
      if ((changeData.getKey() == MODEL_TYPE.UCP_ATTR) || (changeData.getKey() == MODEL_TYPE.ATTRIBUTE)) {
        // on changes with ucp attr
        this.ucFilterGridLayer.getEventList().clear();
        UseCaseEditorInput useCaseEditorInput = (UseCaseEditorInput) getEditorInput();
        if (!CommonUtils.isNullOrEmpty(this.selUCSSet)) {
          constructUpdatedMappableUCItemsSet();
          fillRowObjects(useCaseEditorInput);
          this.ucFilterGridLayer.getEventList().addAll(this.useCaseNatInputs);
          onUCSSelectionRefresh();
        }
        else {
          // if selection is usecase
          this.mappableUcItems = getUseCase().getMappableItems();
          fillRowObjects(useCaseEditorInput);
          this.ucFilterGridLayer.getEventList().addAll(this.useCaseNatInputs);
          consolidateColumns();
          this.cachedUseCaseItems = new ArrayList<>(this.columnUseCaseItemMapping.values());
        }

        this.natTable.doCommand(new StructuralRefreshCommand());
        this.natTable.doCommand(new VisualRefreshCommand());
        this.natTable.refresh();
      }
      else {
        this.nonScrollableForm.setText(getUseCase().getName());
        if (getUseCase().isUpToDate()) {
          this.nonScrollableForm.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.ALL_16X16));
          this.nonScrollableForm.setMessage("[ " + UC_UP_TO_DATE_MSG + " ]");
        }
        else {
          this.nonScrollableForm.setMessage("[ " + UC_NOT_UP_TO_DATE_MSG + " ]", 3);
        }
        this.ucUpToDateAction.enableDisableButton();
        this.ucNotUpToDateAction.enableDisableButton();
      }
      if (changeData.getKey() == MODEL_TYPE.NODE_ACCESS) {
        // indicator to know if the nat table need to be refreshed
        this.changeInAccessRights = true;
      }
    }
    refreshNatTable();

    refreshToolBarFilters();
  }

  /**
   *
   */
  private void constructUpdatedMappableUCItemsSet() {

    Set<IUseCaseItemClientBO> existingMappableUCItemsSet = new HashSet<>(this.mappableUcItems);
    this.mappableUcItems.clear();
    UsecaseEditorModel useCaseEditorModel = this.editor.getEditorInput().getUseCaseEditorModel();

    // only construct the ucs items present in the existinMappableUCItemsSet
    for (UseCaseSection ucSection : useCaseEditorModel.getUcSectionMap().values()) {
      for (IUseCaseItemClientBO iUseCaseItemClientBO : existingMappableUCItemsSet) {
        if (ucSection.getId().equals(iUseCaseItemClientBO.getId())) {
          UseCaseSectionClientBO newUCS = new UseCaseSectionClientBO(useCaseEditorModel, ucSection, getUseCase());
          this.mappableUcItems.add(newUCS);
        }
      }
    }

  }

  /**
   *
   */
  private void refreshNatTable() {
    // ICDM-336
    if (!CommonUtils.isNullOrEmpty(this.selUCSSet)) {
      updateSelUCSSet();
      this.updatedCachedUCItemsSize = (getUseCase()).getMappableItems().size();
      reconstructNatTable();
      onUCSSelectionRefresh();
      this.natTable.doCommand(new StructuralRefreshCommand());
    }
    else {
      if (this.mappableUcItems != null) {
        this.mappableUcItems.clear();
      }
      this.mappableUcItems = (getUseCase()).getMappableItems();
      updatePropertyToLabelMap();
      this.cachedUseCaseItems = new ArrayList<>(this.columnUseCaseItemMapping.values());
      this.updatedCachedUCItemsSize = this.cachedUseCaseItems.size();
      reconstructNatTable();
      // ICDM-289
      this.toolBarFilters.setUcItemsList(this.mappableUcItems);
      setUseCaseInfo(getUseCase());
      consolidateColumns();
      this.natTable.doCommand(new StructuralRefreshCommand());
    }
  }

  /**
   *
   */
  private void updateSelUCSSet() {
    if (this.selUCSSet.size() == 1) {
      // initialize updatedSelUCSClientBO with the old data from the set
      UseCaseSectionClientBO updatedSelUCSClientBO = this.selUCSSet.iterator().next();
      UsecaseEditorModel useCaseEditorModel = this.editor.getEditorInput().getUseCaseEditorModel();
      for (UseCaseSection ucSection : useCaseEditorModel.getUcSectionMap().values()) {
        if (ucSection.getId().equals(updatedSelUCSClientBO.getId())) {
          updatedSelUCSClientBO = new UseCaseSectionClientBO(useCaseEditorModel, ucSection, getUseCase());
          this.selUCSSet.clear();
          this.selUCSSet.add(updatedSelUCSClientBO);
          break;
        }
      }
    }
  }

  /**
   *
   */
  private void onUCSSelectionRefresh() {

    if ((null != this.selUCSSet) && (this.selUCSSet.size() == 1)) {
      if (this.mappableUcItems != null) {
        this.mappableUcItems.clear();
      }
      UseCaseSectionClientBO selectedUCSItem = this.selUCSSet.iterator().next();
      selectedUCSItem.getAttributes().clear();
      this.mappableUcItems = selectedUCSItem.getMappableItems();
    }
    // ICDM-289
    this.toolBarFilters.setUcItemsList(this.mappableUcItems);

    boolean newColumnAdded = false;
    List<IUseCaseItemClientBO> ucItemsToRemove = new ArrayList<>();
    List<IUseCaseItemClientBO> ucItemsToAdd = new ArrayList<>();
    for (IUseCaseItemClientBO IUseCaseItem : this.mappableUcItems) {
      if (!this.cachedUseCaseItems.contains(IUseCaseItem)) {
        if ((IUseCaseItem.getMappableItems() == null) || IUseCaseItem.getMappableItems().isEmpty() ||
            ((IUseCaseItem.getMappableItems().size() == 1) && IUseCaseItem.getMappableItems().contains(IUseCaseItem))) {
          // Missing UseCaseItem is the last child
          if (IUseCaseItem.getParent().getMappableItems().size() == 1) {
            // Missing UseCaseItem is the only child
            if (this.cachedUseCaseItems.contains(IUseCaseItem.getParent())) {
              // Missing UseCaseItem's parent entry already existing and missing UseCaseItem is the only child,so
              // need to add extra columns
              ucItemsToRemove.add(IUseCaseItem.getParent());
              ucItemsToAdd.add(IUseCaseItem);
              continue;
            }
          }
          // Missing UseCaseItem's parent entry has more than one child
          newColumnAdded = true;
          ucItemsToAdd.add(IUseCaseItem);
        }
      }
    }
    this.cachedUseCaseItems.removeAll(ucItemsToRemove);
    this.cachedUseCaseItems.addAll(ucItemsToAdd);
    setUseCaseSectionInfo(this.selUCSSet.iterator().next());
    if (newColumnAdded || (this.ucFilterGridLayer.getBodyLayer().getColumnReorderLayer()
        .getColumnCount() < (this.columnUseCaseItemMapping.size() + STATIC_COLUMN_INDEX))) {
      registerConfigLabelsOnColumns(UseCaseNatAttributesPage.this.ucFilterGridLayer.getBodyLabelAccumulator(), true);
      registerCheckBoxEditor(this.natTable.getConfigRegistry(), true);
      registerEditableRules(this.natTable.getConfigRegistry(), true);
      ILayer bodyDataLayer = this.ucFilterGridLayer.getDummyDataLayer();
      // The below firelayer event prevents creation of an empty column when adding new UseCaseItems
      int colCount = this.ucFilterGridLayer.getBodyLayer().getColumnHideShowLayer().getColumnCount();
      bodyDataLayer.fireLayerEvent(new ColumnInsertEvent(bodyDataLayer, colCount - 1));
      consolidateColumns();
      consolidateUseCaseColumnGroupingForNewColumn();
    }
    if (!newColumnAdded) {
      consolidateColumns();
    }
  }


  /**
   * @return the selectionProvider
   */
  public RowSelectionProvider<UseCaseEditorRowAttr> getSelectionProvider() {
    return this.selectionProvider;
  }


  /**
   * @return the columnUseCaseItemMapping
   */
  public Map<Integer, IUseCaseItemClientBO> getColumnUseCaseItemMapping() {
    return this.columnUseCaseItemMapping;
  }

  /**
   * @return the UseCaseEditor instance
   */
  public UseCaseEditor getUCEditor() {
    return this.editor;
  }

  /**
   * @return SortedSet<IUseCaseItemClientBO>
   */
  public SortedSet<IUseCaseItemClientBO> getMappableUCItems() {
    return this.mappableUcItems;
  }
}
