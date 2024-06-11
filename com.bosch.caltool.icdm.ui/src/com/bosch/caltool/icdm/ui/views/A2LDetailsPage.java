/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.icdm.client.bo.a2l.A2LEditorDataHandler;
import com.bosch.caltool.icdm.client.bo.a2l.A2lDetailsHandler;
import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.dragdrop.CustomDragListener;
import com.bosch.caltool.icdm.common.ui.services.CommandState;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.AbstractPage;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.A2LDetailsStructureModel;
import com.bosch.caltool.icdm.model.a2l.A2lVarGrpMapCmdModel;
import com.bosch.caltool.icdm.model.a2l.A2lVarGrpVariantMapping;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.ui.action.A2lExportWithWpAction;
import com.bosch.caltool.icdm.ui.dialogs.A2lWPDefnVersionListDialog;
import com.bosch.caltool.icdm.ui.dialogs.AddVariantGroupDialog;
import com.bosch.caltool.icdm.ui.dialogs.CompareA2lDialog;
import com.bosch.caltool.icdm.ui.dialogs.EditVariantGroupDialog;
import com.bosch.caltool.icdm.ui.editors.pages.A2lWPDefinitionPage;
import com.bosch.caltool.icdm.ui.editors.pages.WPLabelAssignNatPage;
import com.bosch.caltool.icdm.ui.sorters.A2lWpDefVersionSorter;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lVarGrpVarMappingServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author bru2cob
 */
public class A2LDetailsPage extends AbstractPage {

  /**
   * Declare UI components and controls
   */
  private Composite top;
  /**
   * Main composite
   */
  private Composite composite;
  /**
   * TreeViewer instance
   */
  private TreeViewer viewer;

  private final A2LEditorDataHandler dataHandler;


  private ComboViewer wpDefnVersComboViewer;
  private Button editVersionButton;

  private A2lWPDefinitionPage a2lWPDefinitionPage;
  private WPLabelAssignNatPage wpLabelAssignNatPage;

  private Button addVarGrpButton;
  private Button deletFileButton;
  private Button editVarGrpButton;
  private A2lWpDefnVersion selectedA2lWpDefVers;

  private A2LDetailsStructureModel detailsStrucModel;

  CommandState expReportService = new CommandState();
  private Button compWPDefVersButton;

  /**
   * @param dataHandler dataHandler
   */
  public A2LDetailsPage(final A2LEditorDataHandler dataHandler) {
    this.dataHandler = dataHandler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshTreeViewer(final boolean deselectAll) {
    // NA
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(final Composite parent) {
    // Configure standard layout
    this.top = new Composite(parent, SWT.NONE);
    this.top.setLayout(new GridLayout());
    addHelpAction();
    // Build the UI
    createComposite();
    addDragDropListeners();
    hookContextMenu();
    // add selection provider
    getSite().setSelectionProvider(this.viewer);
  }

  /**
   * Build contect menu on tree
   */
  private void hookContextMenu() {
    // Create the menu manager and fill context menu
    MenuManager menuMgr = new MenuManager("popupmenu");
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(A2LDetailsPage.this::fillContextMenu);
    Menu menu = menuMgr.createContextMenu(this.viewer.getControl());
    this.viewer.getControl().setMenu(menu);
    getSite().registerContextMenu("", menuMgr, this.viewer);
  }

  /**
   * Fills the context menu
   *
   * @param manager
   */
  private void fillContextMenu(final IMenuManager manager) {

    // Get the current selection and add actions to it
    IStructuredSelection selection = (IStructuredSelection) this.viewer.getSelection();
    if ((selection != null) && (selection.getFirstElement() != null)) {
      Object firstElement = selection.getFirstElement();
      if (firstElement instanceof String) {
        manager.add(new A2lExportWithWpAction(this.dataHandler.getA2lWpInfoBo().getPidcA2lBo().getPidcA2l(),
            this.selectedA2lWpDefVers.getId(), null));
        manager.add(new Separator());
      }
      if (firstElement instanceof A2lVariantGroup) {
        // Set Right click for the selected item
        A2lVariantGroup a2lVarGrpNode = (A2lVariantGroup) firstElement;
        manager.add(new A2lExportWithWpAction(this.dataHandler.getA2lWpInfoBo().getPidcA2lBo().getPidcA2l(),
            this.selectedA2lWpDefVers.getId(), a2lVarGrpNode.getId()));
        manager.add(new Separator());
      }
    }
  }

  /**
   * ICDM-1031 Adds drag & drop listener to the tree view control
   */
  private void addDragDropListeners() {

    // this line is to handle the drag& drop support
    Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };
    // add drag listener
    this.viewer.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE, transferTypes, new CustomDragListener(this.viewer));

    // add drop listener
    final DropTarget target =
        new DropTarget(this.viewer.getControl(), DND.DROP_DEFAULT | DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK);
    target.setTransfer(transferTypes);
    target.addDropListener(new DropTargetAdapter() {

      @Override
      public void dragOver(final DropTargetEvent event) {
        if ((null != event.item) &&
            ((event.item.getData() instanceof A2lVariantGroup) || (event.item.getData() instanceof String))) {
          event.detail = DND.DROP_DEFAULT;
        }
        else {
          event.detail = DND.DROP_NONE;
        }
        // this line is to enable automatic scroll in the tree viewer during drag
        event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
      }


      @Override
      public void dragEnter(final DropTargetEvent event) {
        if (event.detail == DND.DROP_DEFAULT) {
          event.detail = DND.DROP_COPY;
        }
      }

      @Override
      public void dragOperationChanged(final DropTargetEvent event) {
        if (event.detail == DND.DROP_DEFAULT) {
          event.detail = DND.DROP_COPY;
        }
      }

      @Override
      public void drop(final DropTargetEvent event) {
        if (event.data == null) {
          // if no data, drop detail is set to none
          event.detail = DND.DROP_NONE;
          return;
        }

        // display message when the user does not have owner rights or WP Definition version is not a working set
        if (!(A2LDetailsPage.this.dataHandler.getA2lWpInfoBo().isWPInfoModifiable() && A2LDetailsPage.this.dataHandler
            .getA2lWpInfoBo().isWorkingSet(A2LDetailsPage.this.dataHandler.getWpVersionId()))) {
          CDMLogger.getInstance().infoDialog(
              "Not Enough Privileges!\nNote:One should have Pidc Owner Access to perform this action.Only a working set can be modified",
              Activator.PLUGIN_ID);
          return;
        }

        // Pidc Variant object
        final Object dragData = event.data;
        // selected Pidc Variant
        final IStructuredSelection strucSelec = (StructuredSelection) dragData;
        if ((strucSelec.getFirstElement() instanceof A2lVariantGroup) ||
            (strucSelec.getFirstElement() instanceof String)) {
          CDMLogger.getInstance().infoDialog("Only Variants can be moved", Activator.PLUGIN_ID);
          return;
        }
        List<Long> selPidcVarIdList = getSelVariantId(strucSelec);
        A2lVarGrpVarMappingServiceClient a2lVarGrpMapClient = new A2lVarGrpVarMappingServiceClient();
        // To move variants from variant group level to default level
        if (event.item.getData() instanceof String) {
          moveToDefaultLevel(selPidcVarIdList, a2lVarGrpMapClient);
        }

        // To move variants between variant group or from default level to variant group
        else if (event.item.getData() instanceof A2lVariantGroup) {
          A2lVarGrpMapCmdModel model = new A2lVarGrpMapCmdModel();
          A2lVariantGroup varGrpToDrop = (A2lVariantGroup) event.item.getData();
          // drop PidcVariant to chosen A2lVariantGroup
          boolean isNotAlreadyMapped = true;
          for (A2lVariantGroup a2lVarGrp : A2LDetailsPage.this.dataHandler.getDetailsStrucModel().getA2lVariantGrpMap()
              .values()) {
            // check if variant is already mapped to the target variant group
            if (a2lVarGrp.equals(varGrpToDrop)) {
              List<PidcVariant> mappedPidcVarList =
                  A2LDetailsPage.this.dataHandler.getDetailsStrucModel().getMappedVariantsMap().get(a2lVarGrp.getId());
              if ((null != mappedPidcVarList) && isAlreadyMapped(mappedPidcVarList, strucSelec)) {
                /**
                 * flag to check if the selected variant is already mapped to the target variant group(to which variant
                 * is being dropped)
                 */
                isNotAlreadyMapped = false;
              }
            }
            // Unmap Pidc Variant Group if it is already mapped to other variant group
            if (!a2lVarGrp.equals(varGrpToDrop)) {
              unmapVariant(selPidcVarIdList, model, a2lVarGrp);
            }
          }
          if (isNotAlreadyMapped) {
            // map PidcVariant to chosen A2lVariantGroup
            for (Long varId : selPidcVarIdList) {
              A2lVarGrpVariantMapping mapping = new A2lVarGrpVariantMapping();
              mapping.setA2lVarGroupId(varGrpToDrop.getId());
              mapping.setVariantId(varId);
              model.getMappingTobeCreated().add(mapping);
            }
          }
          try {
            if (!CommonUtils.isNullOrEmpty(model.getMappingTobeDeleted()) ||
                !CommonUtils.isNullOrEmpty(model.getMappingTobeCreated())) {
              a2lVarGrpMapClient.updateVariantMappings(model,
                  A2LDetailsPage.this.dataHandler.getA2lWpInfoBo().getPidcA2lBo().getPidcA2l());
            }
          }
          catch (ApicWebServiceException exp) {
            CDMLogger.getInstance().error("Error when mapping/unmapping Variants to Variant group", exp,
                Activator.PLUGIN_ID);
          }
        }
      }

      public boolean isAlreadyMapped(final List<PidcVariant> mappedPidcVarList, final IStructuredSelection strucSelec) {
        for (Long id : getSelVariantId(strucSelec)) {
          if (getListOfVarId(mappedPidcVarList).contains(id)) {
            return true;
          }
        }
        return false;
      }

      private List<Long> getListOfVarId(final List<PidcVariant> mappedPidcVarList) {
        List<Long> pidcVarId = new ArrayList<>();
        for (PidcVariant pidcVariant : mappedPidcVarList) {
          pidcVarId.add(pidcVariant.getId());
        }
        return pidcVarId;
      }


      /**
       * @param selPidcVarIdList list of selected PidcVariant Id
       * @param a2lVarGrpMapClient A2lVarGrpVarMappingServiceClient
       */
      public void moveToDefaultLevel(final List<Long> selPidcVarIdList,
          final A2lVarGrpVarMappingServiceClient a2lVarGrpMapClient) {
        A2lVarGrpMapCmdModel model = new A2lVarGrpMapCmdModel();
        for (A2lVariantGroup a2lVarGrp : A2LDetailsPage.this.dataHandler.getDetailsStrucModel().getA2lVariantGrpMap()
            .values()) {
          unmapVariant(selPidcVarIdList, model, a2lVarGrp);
        }
        try {
          if (!CommonUtils.isNullOrEmpty(model.getMappingTobeDeleted())) {
            a2lVarGrpMapClient.updateVariantMappings(model,
                A2LDetailsPage.this.dataHandler.getA2lWpInfoBo().getPidcA2lBo().getPidcA2l());
          }
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error("Error when mapping/unmapping Variants to Variant group", exp,
              Activator.PLUGIN_ID);
        }
      }


      private void unmapVariant(final List<Long> selVariantIdList, final A2lVarGrpMapCmdModel model,
          final A2lVariantGroup a2lVarGrp) {
        List<PidcVariant> mappedPidcVarList =
            A2LDetailsPage.this.dataHandler.getDetailsStrucModel().getMappedVariantsMap().get(a2lVarGrp.getId());
        if (!CommonUtils.isNullOrEmpty(mappedPidcVarList)) {
          for (PidcVariant mappedPidcVar : mappedPidcVarList) {
            A2lVarGrpVariantMapping a2lVarGrpVarMapping =
                A2LDetailsPage.this.dataHandler.getDetailsStrucModel().getGroupMappingMap().get(mappedPidcVar.getId());
            if (selVariantIdList.contains(mappedPidcVar.getId())) {
              model.getMappingTobeDeleted().add(a2lVarGrpVarMapping);

            }
          }
        }
      }


      private List<Long> getSelVariantId(final IStructuredSelection strucSelec) {
        Iterator<PidcVariant> it = strucSelec.iterator();
        List<Long> pidcVarList = new ArrayList<>();
        while (it.hasNext()) {
          pidcVarList.add(it.next().getId());
        }
        return pidcVarList;

      }
    });
  }

  /**
   *
   */
  private void createComposite() {

    final GridData gridData = new GridData();

    // Apply grid data styles
    applyGridDataStyles(gridData);

    createEditWpVersionSection();

    Composite treeComposite = new Composite(this.composite, SWT.NONE);

    this.composite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));


    treeComposite.setLayoutData(gridData);
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;

    // gridLayout.


    treeComposite.setLayout(gridLayout);

    Composite treeviewerComp = new Composite(treeComposite, SWT.NONE);
    treeviewerComp.setLayout(new GridLayout());


    treeviewerComp.setLayoutData(gridData);

    // Create filters
    final PatternFilter filter = new PatternFilter();
    final FilteredTree tree = new FilteredTree(treeviewerComp, SWT.BORDER | SWT.MULTI, filter, true);
    // Get viewer and set styled layout for tree
    this.viewer = tree.getViewer();


    this.viewer.getTree().setLayoutData(gridData);
    // set auto expand level
    this.viewer.setAutoExpandLevel(CommonUIConstants.DEF_TREE_COLLAPSE_LEVEL);

    this.viewer.addSelectionChangedListener(event -> {
      // ICDM-865
      this.expReportService = (CommandState) CommonUiUtils.getInstance().getSourceProvider();
      A2LDetailsPage.this.expReportService.setExportService(false);
      final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      Object selected = selection.getFirstElement();
      // if default node is selected , set the default level to true, else- false
      this.dataHandler.getA2lWpInfoBo().setDefaultLevel(false);
      if (selected instanceof A2lVariantGroup) {
        this.dataHandler.getA2lWpInfoBo().setSelectedA2lVarGroup((A2lVariantGroup) selected);
        this.dataHandler.getA2lWpInfoBo().setNotAssignedVarGrp(false);
        boolean editable = this.dataHandler.getA2lWpInfoBo().isWPInfoModifiable() &&
            this.dataHandler.getA2lWpInfoBo().isWorkingSet(this.dataHandler.getWpVersionId());
        A2LDetailsPage.this.editVarGrpButton.setEnabled(editable);
        A2LDetailsPage.this.deletFileButton.setEnabled(editable);
        if ((this.wpLabelAssignNatPage != null) && this.wpLabelAssignNatPage.isActive()) {
          A2LDetailsPage.this.expReportService.setExportService(true);
        }
      }
      else if (selected instanceof PidcVariant) {
        A2lVariantGroup selA2lVariantGroup = null;

        PidcVariant pidcVariant = (PidcVariant) selected;
        for (Entry<Long, List<PidcVariant>> entrySet : this.detailsStrucModel.getMappedVariantsMap().entrySet()) {
          for (PidcVariant pidcVar : entrySet.getValue()) {
            if (pidcVar.equals(pidcVariant)) {
              Long varGrpId = entrySet.getKey();
              selA2lVariantGroup = this.detailsStrucModel.getA2lVariantGrpMap().get(varGrpId);
            }
          }
        }
        this.dataHandler.getA2lWpInfoBo().setSelectedA2lVarGroup(selA2lVariantGroup);
        this.dataHandler.getA2lWpInfoBo().setNotAssignedVarGrp(selA2lVariantGroup == null);

        A2LDetailsPage.this.editVarGrpButton.setEnabled(false);
        A2LDetailsPage.this.deletFileButton.setEnabled(false);
        if ((this.wpLabelAssignNatPage != null) && this.wpLabelAssignNatPage.isActive()) {
          A2LDetailsPage.this.expReportService.setExportService(true);
        }
      }
      else if (selected instanceof String) {
        if (((String) selected).equals(ApicUiConstants.STRUCTURE_VIEW_DEFAULT_NODE)) {
          A2LDetailsPage.this.editVarGrpButton.setEnabled(false);
          A2LDetailsPage.this.deletFileButton.setEnabled(false);
          this.dataHandler.getA2lWpInfoBo().setSelectedA2lVarGroup(null);
          this.dataHandler.getA2lWpInfoBo().setNotAssignedVarGrp(true);
          this.dataHandler.getA2lWpInfoBo().setDefaultLevel(true);
          if ((this.wpLabelAssignNatPage != null) && this.wpLabelAssignNatPage.isActive()) {
            A2LDetailsPage.this.expReportService.setExportService(true);
          }
        }
      }
      else if (selected instanceof A2lWpDefnVersion) {
        A2LDetailsPage.this.editVarGrpButton.setEnabled(false);
        A2LDetailsPage.this.deletFileButton.setEnabled(false);
        this.dataHandler.getA2lWpInfoBo().setSelectedA2lVarGroup(null);
        this.dataHandler.getA2lWpInfoBo().setNotAssignedVarGrp(false);
      }
    });

    ColumnViewerToolTipSupport.enableFor(this.viewer);


    // fill the value from Working set or selected A2l wp version id

    Long wpVersionId = this.dataHandler.getA2lWpInfoBo().getA2lWpDefnModel().getSelectedWpDefnVersionId() == null
        ? this.dataHandler.getA2lWpInfoBo().getWorkingSetId()
        : this.dataHandler.getA2lWpInfoBo().getA2lWpDefnModel().getSelectedWpDefnVersionId();

    createButtons(treeComposite);

    if (wpVersionId != null) {
      populateData(wpVersionId);
    }
  }

  /**
   * @param treeComposite
   * @param gridData
   */
  private void createButtons(final Composite treeComposite) {
    Composite comp = new Composite(treeComposite, SWT.NONE);
    GridData gridData1 = new GridData();
    gridData1.verticalAlignment = GridData.FILL;
    gridData1.grabExcessVerticalSpace = true;
    gridData1.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
    comp.setLayoutData(gridData1);
    comp.setLayout(new GridLayout());
    this.addVarGrpButton = new Button(comp, SWT.PUSH);
    this.addVarGrpButton.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.ADD_16X16));
    this.addVarGrpButton.setToolTipText("Add Variant Group");
    boolean editable = this.dataHandler.getA2lWpInfoBo().isWPInfoModifiable() &&
        this.dataHandler.getA2lWpInfoBo().isWorkingSet(this.dataHandler.getWpVersionId());
    this.addVarGrpButton.setEnabled(editable);
    this.addVarGrpButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final AddVariantGroupDialog vrsnDialog =
            new AddVariantGroupDialog(Display.getDefault().getActiveShell(), A2LDetailsPage.this.dataHandler);
        vrsnDialog.open();
      }
    });


    this.editVarGrpButton = new Button(comp, SWT.PUSH);
    this.editVarGrpButton.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.EDIT_16X16));
    this.editVarGrpButton.setToolTipText("Edit Variant Group");
    this.editVarGrpButton.setEnabled(false);
    this.editVarGrpButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final EditVariantGroupDialog vrsnDialog =
            new EditVariantGroupDialog(Display.getDefault().getActiveShell(), A2LDetailsPage.this.dataHandler);
        vrsnDialog.open();
      }
    });


    this.deletFileButton = new Button(comp, SWT.PUSH);
    this.deletFileButton.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.DELETE_16X16));
    this.deletFileButton.setToolTipText("Delete Variant Group");
    this.deletFileButton.setEnabled(false);

    this.deletFileButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {

        if (wpDefDelConfirmation()) {
          new A2lDetailsHandler().deleteA2lVarGroup(
              A2LDetailsPage.this.dataHandler.getA2lWpInfoBo().getSelectedA2lVarGroup(),
              A2LDetailsPage.this.dataHandler.getA2lWpInfoBo().getPidcA2lBo().getPidcA2l());
        }
      }
    });
  }

  /**
   * @return
   */
  private boolean wpDefDelConfirmation() {
    boolean canDelete;
    canDelete = MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Confirmation",
        "Variant group along with the varaint mapping will be deleted.Click OK to delete");
    return canDelete;
  }

  /**
   * create wp Divison combo
   */
  private void createEditWpVersionSection() {
    Composite comboBoxComp = new Composite(this.composite, SWT.NONE);


    GridData gridData1 = new GridData();
    gridData1.verticalAlignment = GridData.FILL;
    gridData1.grabExcessHorizontalSpace = true;

    gridData1.horizontalAlignment = GridData.FILL;
    comboBoxComp.setLayoutData(gridData1);
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 4;
    Label label = new Label(comboBoxComp, SWT.BOLD);
    label.setText("WP Definition:");
    this.wpDefnVersComboViewer = new ComboViewer(comboBoxComp, SWT.READ_ONLY);
    Combo combo = this.wpDefnVersComboViewer.getCombo();
    GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
    combo.setLayoutData(gridData);
    fillWPDefnVersionCombo();
    this.editVersionButton = new Button(comboBoxComp, SWT.PUSH);
    // set icon for button
    this.editVersionButton.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.EDIT_16X16));
    this.editVersionButton.setToolTipText("Edit Versions");
    this.editVersionButton.setEnabled(true);

    this.editVersionButton.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent arg0) {
        A2lWPDefnVersionListDialog dialog =
            new A2lWPDefnVersionListDialog(A2LDetailsPage.this.editVersionButton.getShell(),
                A2LDetailsPage.this.dataHandler.getA2lWpInfoBo(), A2LDetailsPage.this, false);
        dialog.open();

      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // NA

      }
    });
    this.compWPDefVersButton = new Button(comboBoxComp, SWT.PUSH);
    // set icon for button
    this.compWPDefVersButton.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.COMPARE_EDITOR_16X16));
    this.compWPDefVersButton.setToolTipText("Compare WP Version");
    this.compWPDefVersButton.setEnabled(true);
    comboBoxComp.setLayout(gridLayout);
    this.compWPDefVersButton.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent arg0) {
        CompareA2lDialog compA2lDialog =
            new CompareA2lDialog(null, A2LDetailsPage.this.dataHandler.getA2lWpInfoBo().getA2lWpDefnVersMap(),
                Display.getCurrent().getActiveShell());
        compA2lDialog.open();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // NA

      }
    });

  }

  /**
   * @param wpDefnVers
   * @param a2lWpDefinitionVersion
   * @return
   */
  private String getWpDefnVersName(final A2lWpDefnVersion a2lWpDefinitionVersion) {
    StringBuilder wpDefnVersName = new StringBuilder();
    if (a2lWpDefinitionVersion.isActive()) {
      wpDefnVersName.append("(Active) ");
    }
    wpDefnVersName.append(a2lWpDefinitionVersion.getName());
    return wpDefnVersName.toString();
  }


  private void fillWPDefnVersionCombo() {


    this.wpDefnVersComboViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.wpDefnVersComboViewer.setLabelProvider(new LabelProvider() {

      @Override
      public String getText(final Object element) {
        StringBuilder wpDefnVers = new StringBuilder();
        if (element instanceof A2lWpDefnVersion) {
          A2lWpDefnVersion a2lWpDefinitionVersion = (A2lWpDefnVersion) element;
          wpDefnVers.append(getWpDefnVersName(a2lWpDefinitionVersion));
        }
        return wpDefnVers.toString();
      }

    });

    this.wpDefnVersComboViewer.addSelectionChangedListener(selectionChangedEvent -> {
      // added this flag to avoid calling of labelassignment nattable refresh for version changing in a2l structures
      boolean selectionChangedFlag = false;
      IStructuredSelection selection = (IStructuredSelection) selectionChangedEvent.getSelection();
      // load a2l wp defn model for selected wp defn version
      A2lWpDefnVersion currentSelection = (A2lWpDefnVersion) selection.getFirstElement();
      if (!currentSelection.equals(this.selectedA2lWpDefVers)) {
        A2LDetailsPage.this.selectedA2lWpDefVers = currentSelection;
        selectionChangedFlag = true;
      }
      Long a2lWpVersId = A2LDetailsPage.this.selectedA2lWpDefVers.getId();
      A2LDetailsPage.this.dataHandler.getA2lWpInfoBo().initializeModelBasedOnWpDefVers(a2lWpVersId, false, false);
      if (A2LDetailsPage.this.a2lWPDefinitionPage != null) {
        A2LDetailsPage.this.a2lWPDefinitionPage.refreshUIElements();
      }

      if ((A2LDetailsPage.this.wpLabelAssignNatPage != null) && selectionChangedFlag) {
        A2LDetailsPage.this.wpLabelAssignNatPage.refreshUIElements();
      }
      boolean editable = A2LDetailsPage.this.dataHandler.getA2lWpInfoBo().isWPInfoModifiable() &&
          A2LDetailsPage.this.dataHandler.getA2lWpInfoBo().isWorkingSet(a2lWpVersId);
      if (A2LDetailsPage.this.addVarGrpButton != null) {
        A2LDetailsPage.this.addVarGrpButton.setEnabled(editable);

      }

      populateData(a2lWpVersId);

      // Outline view refresh
      CommonActionSet act = new CommonActionSet();
      act.refreshOutlinePages(true);

      A2LDetailsPage.this.wpDefnVersComboViewer.getControl()
          .setToolTipText(getWpDefnVersName(this.selectedA2lWpDefVers));
    });

    A2lWpDefVersionSorter sorter = new A2lWpDefVersionSorter();


    List<A2lWpDefnVersion> values = new ArrayList<>(this.dataHandler.getA2lWpInfoBo().getA2lWpDefnVersMap().values());
    Collections.sort(values, sorter);
    this.wpDefnVersComboViewer.setInput(values);
    if (this.dataHandler.getA2lWpInfoBo().getWorkingSet() != null) {
      final ISelection selection = new StructuredSelection(this.dataHandler.getA2lWpInfoBo().getWorkingSet());
      this.wpDefnVersComboViewer.setSelection(selection);
    }

  }

  private void refreshUIElements() {
    if (this.wpDefnVersComboViewer != null) {
      this.wpDefnVersComboViewer.refresh();
    }

  }

  /**
   * @param wpVersionId wpVersionId
   */
  public void populateData(final Long wpVersionId) {

    this.dataHandler.setWpVersionId(wpVersionId);
    // set the Wp Def version id in the Model class while opening , refersh and Combo Value change

    this.detailsStrucModel = this.dataHandler.getA2lDetailsModel(this.dataHandler.getWpVersionId());
    if (this.viewer != null) {
      this.viewer.setContentProvider(new A2LDetailsViewContentProvider(this.detailsStrucModel));
      // Set Label provider for the tree
      this.viewer.setLabelProvider(new A2LDetailsViewLabelProvider(this.detailsStrucModel));
      // create root node in Content provider
      this.viewer.setInput(this.dataHandler.getWpVersionId());
      // to maintain vg selection in the tree view
      A2lVariantGroup selectedA2lVarGroup = this.dataHandler.getA2lWpInfoBo().getSelectedA2lVarGroup();
      if ((null != selectedA2lVarGroup) &&
          selectedA2lVarGroup.getWpDefnVersId().equals(this.dataHandler.getWpVersionId())) {
        final ISelection selection = new StructuredSelection(selectedA2lVarGroup);
        this.dataHandler.getA2lWpInfoBo().setDefaultLevel(false);
        this.viewer.setSelection(selection);
      }
      else {
        final ISelection selection = new StructuredSelection("<DEFAULT>");
        this.dataHandler.getA2lWpInfoBo().setDefaultLevel(true);
        this.viewer.setSelection(selection);
      }

      ColumnViewerToolTipSupport.enableFor(this.viewer, ToolTip.NO_RECREATE);
    }
    // Set Content provider for the tree
  }

  /**
   * Applies styles to GridData
   *
   * @param gridData
   */
  private void applyGridDataStyles(final GridData gridData) {
    // Apply the standard styles
    gridData.verticalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    // Create GridLayout
    final GridLayout gridLayout = new GridLayout();

    this.composite = new Composite(this.top, SWT.NONE);
    this.composite.setLayoutData(gridData);
    this.composite.setLayout(gridLayout);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Control getControl() {
    return this.top;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFocus() {
    this.viewer.getControl().setFocus();
  }

  /**
   *
   */
  public void refreshPage() {
    //NA

  }


  /**
   * @return viewer
   */
  public TreeViewer getViewer() {
    return this.viewer;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    if (dce != null) {
      Map<IModelType, Map<Long, ChangeData<?>>> consChangeData = dce.getConsChangeData();

      for (IModelType modelType : consChangeData.keySet()) {

        if (modelType == MODEL_TYPE.A2L_WP_DEFN_VERSION) {
          ISelection selection = this.wpDefnVersComboViewer.getSelection();
          A2lWpDefVersionSorter sorter = new A2lWpDefVersionSorter();
          List<A2lWpDefnVersion> values =
              new ArrayList<>(this.dataHandler.getA2lWpInfoBo().getA2lWpDefnVersMap().values());
          Collections.sort(values, sorter);
          this.wpDefnVersComboViewer.setInput(values);
          this.wpDefnVersComboViewer.setSelection(selection);
        }
        if ((modelType == MODEL_TYPE.A2L_VARIANT_GROUP) || (modelType == MODEL_TYPE.VARIANT) ||
            (modelType == MODEL_TYPE.A2L_VAR_GRP_VAR_MAPPING)) {
          populateData(this.dataHandler.getWpVersionId());
          Map<Long, A2lVariantGroup> a2lVariantGrpMap = this.detailsStrucModel.getA2lVariantGrpMap();
          Map<Long, A2lVariantGroup> defModelA2lVarGrpMap =
              this.dataHandler.getA2lWpInfoBo().getA2lWpDefnModel().getA2lVariantGroupMap();

          for (Entry<Long, A2lVariantGroup> a2lVarGrpEntry : a2lVariantGrpMap.entrySet()) {
            if (defModelA2lVarGrpMap.get(a2lVarGrpEntry.getKey()) == null) {
              defModelA2lVarGrpMap.put(a2lVarGrpEntry.getKey(), a2lVarGrpEntry.getValue());
            }

          }
          List<Long> varGrpsDeleted = new ArrayList<>();
          for (Entry<Long, A2lVariantGroup> defmodEntrySet : defModelA2lVarGrpMap.entrySet()) {

            if (a2lVariantGrpMap.get(defmodEntrySet.getKey()) == null) {
              varGrpsDeleted.add(defmodEntrySet.getKey());
            }
          }

          for (Long delVarGrpIds : varGrpsDeleted) {
            defModelA2lVarGrpMap.remove(delVarGrpIds);
          }


          refreshUIElements();

          this.dataHandler.getA2lWpInfoBo().formVirtualRecords();
          break;
        }

      }
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public IClientDataHandler getDataHandler() {
    return this.dataHandler;
  }


  /**
   * @param a2lWPDefinitionPage a2lWPDefinitionPage
   */
  public void setWpDefPage(final A2lWPDefinitionPage a2lWPDefinitionPage) {
    this.a2lWPDefinitionPage = a2lWPDefinitionPage;

  }

  /**
   * @param wpLabelAssignNatPage wpLabelAssignNatPage
   */
  public void setWpLabelAssignPage(final WPLabelAssignNatPage wpLabelAssignNatPage) {
    this.wpLabelAssignNatPage = wpLabelAssignNatPage;

  }


  /**
   * @return the wpDefnVersComboViewer
   */
  public ComboViewer getWpDefnVersComboViewer() {
    return this.wpDefnVersComboViewer;
  }


  /**
   * @return the selectedA2lWpDefVers
   */
  public A2lWpDefnVersion getSelectedA2lWpDefVers() {
    return this.selectedA2lWpDefVers;
  }


  /**
   * @return the addVarGrpButton
   */
  public Button getAddVarGrpButton() {
    return this.addVarGrpButton;
  }


}
