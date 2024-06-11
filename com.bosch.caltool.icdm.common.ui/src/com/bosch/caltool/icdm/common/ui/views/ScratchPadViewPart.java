/*
 * \ * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.views;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParamInfo;
import com.bosch.caltool.icdm.client.bo.apic.PIDCDetailsNode;
import com.bosch.caltool.icdm.client.bo.apic.PIDCScoutResult;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode.PIDC_TREE_NODE_TYPE;
import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.client.bo.ss.CalDataType;
import com.bosch.caltool.icdm.client.bo.ss.SeriesStatisticsInfo;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.actions.IPIDCTreeViewActionSet;
import com.bosch.caltool.icdm.common.ui.dialogs.CalDataViewerDialog;
import com.bosch.caltool.icdm.common.ui.dragdrop.CustomDragListener;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.ui.jobs.CDFFileExportJob;
import com.bosch.caltool.icdm.common.ui.jobs.rules.MutexRule;
import com.bosch.caltool.icdm.common.ui.providers.ScratchPadDataFetcher;
import com.bosch.caltool.icdm.common.ui.providers.ScratchPadTableLabelProvider;
import com.bosch.caltool.icdm.common.ui.services.CommandState;
import com.bosch.caltool.icdm.common.ui.sorters.ScratchPadSorter;
import com.bosch.caltool.icdm.common.ui.table.filters.ScratchPadFilter;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CaldataFileParserHandler;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.logger.ParserLogger;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectObject;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ws.rest.client.a2l.FunctionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * @author NIP4COB
 */
public class ScratchPadViewPart extends AbstractViewPart {

  /**
   * ID of Scratch Pad View
   */
  public static final String PART_ID = "com.bosch.caltool.icdm.common.ui.views.ScratchPadViewPart";

  /**
   *
   */
  private static final String CLASS = "class";
  /**
   * Composite instance for base layout
   */
  private Composite top;
  /**
   * Composite instance
   */
  private Composite composite;

  /**
   * TableViewer instance
   */
  private TableViewer tableViewer;

  private final List<ScratchPadDataFetcher> nodeList = new ArrayList<>();

  /**
   * Delete action instance
   */
  private IAction createTBarDelAction;

  /**
   * The width of each column for properties table.
   */
  private static int[] ColWidths = { 20, 500 };

  /**
   * Rule to run the Export as CSV file jobs in sequence.
   */
  // ICDM-242
  private final MutexRule exportCDFRule = new MutexRule();

  /**
   * CommandState instance
   */
  CommandState expReportService = new CommandState();


  /**
   * Get the eclipse preference store
   */
  private transient final IPreferenceStore preference = PlatformUI.getPreferenceStore();
  private FormToolkit toolkit;
  private Text filterTxt;
  private Form form;
  private Section section;
  protected AbstractViewerFilter scratchPadFilter;

  private boolean enableFlag = true;

  private List<IProjectObject> validObjsToCompare;

  /**
   * @return
   */
  public List<ScratchPadDataFetcher> getNodeList() {
    return this.nodeList;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void createPartControl(final Composite parent) {
    addHelpAction();
    this.toolkit = new FormToolkit(parent.getDisplay());
    this.top = new Composite(parent, SWT.NONE);
    this.top.setLayout(new GridLayout());
    createComposite();

    // Context menu actions
    hookContextMenu();

    // iCDM-530
    setTitleToolTip(" Storage for further processing, E.g. Compare, Export..");

  }


  private void hookContextMenu() {
    // Create the menu manager and fill context menu
    MenuManager menuMgr = new MenuManager("popupmenu");
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener() {

      @Override
      public void menuAboutToShow(final IMenuManager manager) {
        openComparePidc(manager);
        // ICDM-242
        createExportCDFContextMenu(manager);
        // ICDM-1125
        createImportCalDataFile(manager);
        manager.add(new Separator());
        // ICDM-254
        createCopyContextMenu(manager, (IStructuredSelection) ScratchPadViewPart.this.tableViewer.getSelection());
        createCopyFromClipBoard(manager);
        manager.add(new Separator());
        compareCalDataContextMenu(manager, (IStructuredSelection) ScratchPadViewPart.this.tableViewer.getSelection());
        manager.add(new Separator());
        showSeriesStat(manager, (IStructuredSelection) ScratchPadViewPart.this.tableViewer.getSelection());
        showReviewData(manager, (IStructuredSelection) ScratchPadViewPart.this.tableViewer.getSelection());
        showFunctions(manager, (IStructuredSelection) ScratchPadViewPart.this.tableViewer.getSelection());
      }


    });
    Menu menu = menuMgr.createContextMenu(this.tableViewer.getControl());
    this.tableViewer.getControl().setMenu(menu);
    getSite().registerContextMenu(menuMgr, this.tableViewer);

  }

  /**
   * @return
   */
  private IConfigurationElement[] getConfigurationElements() {
    return Platform.getExtensionRegistry()
        .getConfigurationElementsFor("com.bosch.caltool.icdm.common.ui.PIDCTreeViewExtnPt");
  }

  private void openComparePidc(final IMenuManager manager) {
    final IStructuredSelection iStructSelection =
        (IStructuredSelection) ScratchPadViewPart.this.tableViewer.getSelection();
    final Action createComparePIDCAction = new Action() {

      @Override
      public void run() {
        IConfigurationElement[] configs = getConfigurationElements();
        for (IConfigurationElement config : configs) {
          try {
            IPIDCTreeViewActionSet extension = (IPIDCTreeViewActionSet) config.createExecutableExtension(CLASS);
            final List<IProjectObject> pidcVersionsUpdated = ScratchPadViewPart.this.validObjsToCompare;
            if (null != pidcVersionsUpdated) {
              extension.openComparePidcEditor(manager, pidcVersionsUpdated);
            }
          }
          catch (Exception exp) {
            CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
          }
        }
      }
    };

    if (iStructSelection.getFirstElement() != null) {
      if (((ScratchPadDataFetcher) iStructSelection.getFirstElement()).getPidcVariant() != null) {
        createComparePIDCAction.setText("Project Variant Compare");
      }
      else if (((ScratchPadDataFetcher) iStructSelection.getFirstElement()).getPidcSubVariant() != null) {
        createComparePIDCAction.setText("Project Sub-Variant Compare");
      }
      else {
        createComparePIDCAction.setText("Compare PIDC");
      }

      ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.COMPARE_EDITOR_16X16);
      createComparePIDCAction.setImageDescriptor(imageDesc);
      this.validObjsToCompare = getValidSelection(iStructSelection.toList());
      if ((this.validObjsToCompare.size() >= 2) && this.enableFlag) {
        manager.add(createComparePIDCAction);
      }

    }
  }


  /**
   * @param selObjList List<?>
   * @return List<IProjectObject>
   */
  public List<IProjectObject> getValidSelection(final List<?> selObjList) {
    this.enableFlag = false;
    List<IProjectObject> projObjList = new ArrayList<>();
    if (isListOfIProjectObj(selObjList) && checkForSimilarType(selObjList)) {
      projObjList = addToCompareList(selObjList);
    }
    return projObjList;
  }


  /**
   * @param selObjList List<IProjectObject>
   * @return List<IProjectObject>
   */
  public List<IProjectObject> addToCompareList(final List<?> selObjList) {
    Iterator<?> validObjList = selObjList.iterator();
    ScratchPadDataFetcher firstEle = (ScratchPadDataFetcher) validObjList.next();
    List<IProjectObject> compareObjList = new ArrayList<>();

    if ((firstEle.getPidcVariant() != null) && checkValidPidcVariant(validObjList, firstEle)) {
      Iterator<?> objList = selObjList.iterator();
      while (objList.hasNext()) {
        ScratchPadDataFetcher scratchPadDataFetcher = (ScratchPadDataFetcher) objList.next();
        compareObjList.add(scratchPadDataFetcher.getPidcVariant());
      }
      this.enableFlag = true;
    }
    else if ((firstEle.getPidcSubVariant() != null) && checkValidPidcSubVar(validObjList, firstEle)) {
      Iterator<?> objList = selObjList.iterator();
      while (objList.hasNext()) {
        ScratchPadDataFetcher scratchPadDataFetcher = (ScratchPadDataFetcher) objList.next();
        compareObjList.add(scratchPadDataFetcher.getPidcSubVariant());
      }
      this.enableFlag = true;
    }
    else if (firstEle.getPidcVersion() != null) {
      compareObjList.add(firstEle.getPidcVersion());
      while (validObjList.hasNext()) {
        ScratchPadDataFetcher scratchPadDataFetcher = (ScratchPadDataFetcher) validObjList.next();
        compareObjList.add(scratchPadDataFetcher.getPidcVersion());
      }
      this.enableFlag = true;
    }
    return compareObjList;
  }

  // To check whether selected objects in scratch pad view are of type IProjectObject
  /**
   * @param selObjList
   */
  private boolean isListOfIProjectObj(final List<?> selObjList) {
    Iterator<?> validObjList = selObjList.iterator();
    while (validObjList.hasNext()) {
      ScratchPadDataFetcher dataFetcher = (ScratchPadDataFetcher) validObjList.next();
      if (CommonUtils.isNull(dataFetcher.getPidcSubVariant()) && CommonUtils.isNull(dataFetcher.getPidcVariant()) &&
          CommonUtils.isNull(dataFetcher.getPidcVersion())) {
        return false;
      }
    }
    return true;
  }


  // check whether the selected objects from scratch pad view are of similar type
  /**
   * @param selObjList List<?>
   * @return boolean
   */
  public boolean checkForSimilarType(final List<?> selObjList) {
    Iterator<?> validObjList = selObjList.iterator();
    ScratchPadDataFetcher firstEle = (ScratchPadDataFetcher) validObjList.next();
    while (validObjList.hasNext()) {
      ScratchPadDataFetcher scratchDataFetcher = (ScratchPadDataFetcher) validObjList.next();
      if (((firstEle.getPidcVersion() != null) && (null != scratchDataFetcher.getPidcVersion())) ||
          (((firstEle.getPidcVariant() != null) && (null != scratchDataFetcher.getPidcVariant())) ||
              ((firstEle.getPidcSubVariant() != null) && (null != scratchDataFetcher.getPidcSubVariant())))) {
        continue;
      }
      return false;
    }
    return true;
  }


  /**
   * @param validObjList Iterator<?>
   * @param firstEle ScratchPadDataFetcher
   * @return boolean
   */
  public boolean checkValidPidcSubVar(final Iterator<?> validObjList, final ScratchPadDataFetcher firstEle) {
    while (validObjList.hasNext()) {
      ScratchPadDataFetcher scratchDataFetcher = (ScratchPadDataFetcher) validObjList.next();
      if (firstEle.getPidcSubVariant().getPidcVariantId()
          .equals(scratchDataFetcher.getPidcSubVariant().getPidcVariantId())) {
        continue;
      }
      return false;
    }
    return true;
  }


  /**
   * @param validObjList Iterator<?>
   * @param firstEle ScratchPadDataFetcher
   * @return boolean
   */
  public boolean checkValidPidcVariant(final Iterator<?> validObjList, final ScratchPadDataFetcher firstEle) {
    while (validObjList.hasNext()) {
      ScratchPadDataFetcher scratchDataFetcher = (ScratchPadDataFetcher) validObjList.next();
      if (firstEle.getPidcVariant().getPidcVersionId().equals(scratchDataFetcher.getPidcVariant().getPidcVersionId())) {
        continue;
      }
      return false;
    }
    return true;
  }


  /**
   * @param scratchDataFetcher ScratchPadDataFetcher
   * @return List<IProjectObject>
   */
  public List<IProjectObject> addFirstObjToCompare(final ScratchPadDataFetcher scratchDataFetcher) {
    List<IProjectObject> projObjList = new ArrayList<>();
    if (null != scratchDataFetcher.getPidcVersion()) {
      projObjList.add(scratchDataFetcher.getPidcVersion());
    }
    else if (null != scratchDataFetcher.getPidcVariant()) {
      projObjList.add(scratchDataFetcher.getPidcVariant());
    }
    else if (null != scratchDataFetcher.getPidcSubVariant()) {
      projObjList.add(scratchDataFetcher.getPidcSubVariant());
    }
    return projObjList;
  }

  /**
   * @param manager
   * @param selection
   */
  protected void showFunctions(final IMenuManager manager, final IStructuredSelection selection) {
    if (isAllElementsCalData(selection.toList()) && (selection.size() == 1)) {
      final Action openReviewRule = new Action() {

        String paramName;
        SortedSet<com.bosch.caltool.icdm.model.a2l.Function> fetchFunctions;

        @Override
        public void run() {
          List<ScratchPadDataFetcher> dataList = selection.toList();

          if (com.bosch.caltool.icdm.common.util.CommonUtils.isNotNull(dataList.get(0).getSeriesStatsInfo())) {
            this.paramName = dataList.get(0).getSeriesStatsInfo().getCalData().getShortName();
          }
          else {
            this.paramName = dataList.get(0).getParameter().getName();
          }

          final ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
          try {
            dialog.run(true, true, new IRunnableWithProgress() {

              @Override
              public void run(final IProgressMonitor monitor) {
                monitor.beginTask("Fetching Functions...", IProgressMonitor.UNKNOWN);

                try {
                  fetchFunctions = new FunctionServiceClient().getFunctionsByParamName(paramName);
                }
                catch (ApicWebServiceException e) {
                  CDMLogger.getInstance().errorDialog("Error in loading Functions", e, Activator.PLUGIN_ID);
                  e.printStackTrace();
                }
              }


            });
          }
          catch (InvocationTargetException | InterruptedException e) {
            CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
          }

          Display.getDefault().syncExec(new Runnable() {

            @Override
            public void run() {

              if (hasBigFunctions(fetchFunctions)) {
                return;
              }
              CommonActionSet cmnActionSet = new CommonActionSet();
              // to be done

              // Open the function selection dialog
              cmnActionSet.openFunctionSelectionDialog(new ArrayList<>(fetchFunctions), paramName);
            }

          });
        }
      };

      openReviewRule.setText("Open Review Rule");

      final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.REVIEW_RULES_16X16);
      // Set imange/icon to the export as CDF
      openReviewRule.setImageDescriptor(imageDesc);
      // Add export as CDF action to Menu manager
      manager.add(openReviewRule);
    }
  }

  /**
   * @param manager manager
   */
  protected void createCopyFromClipBoard(final IMenuManager manager) {
    CommonActionSet cmnActionSet = new CommonActionSet();
    cmnActionSet.copyFromClipBoard(manager, this);
  }


  /**
   * @param manager
   * @param selection
   */
  protected void showSeriesStat(final IMenuManager manager, final IStructuredSelection selection) {

    List<Object> dataList = selection.toList();

    com.bosch.caltool.icdm.common.ui.actions.CDMCommonActionSet actionSet =
        new com.bosch.caltool.icdm.common.ui.actions.CDMCommonActionSet();
    actionSet.addShowSeriesStatisticsMenuAction(manager, dataList, isAllElementsCalData(dataList));


  }

  /**
   * @param manager manager
   * @param selection selection
   */
  protected void showReviewData(final IMenuManager manager, final IStructuredSelection selection) {
    Object data;
    List<Object> dataList = selection.toList();
    if (isAllElementsCalData(dataList) && !dataList.isEmpty()) {
      if (com.bosch.caltool.icdm.common.util.CommonUtils
          .isNotNull(((ScratchPadDataFetcher) dataList.get(0)).getSeriesStatsInfo())) {
        data = ((ScratchPadDataFetcher) dataList.get(0)).getSeriesStatsInfo().getCalData().getShortName();
      }
      else {
        data = ((ScratchPadDataFetcher) dataList.get(0)).getParameter().getName();
      }

      com.bosch.caltool.icdm.common.ui.actions.CDMCommonActionSet actionSet =
          new com.bosch.caltool.icdm.common.ui.actions.CDMCommonActionSet();
      actionSet.addShowReviewDataMenuAction(manager, data, true, null);
    }

  }


  /**
   * @param funcList
   * @return
   */
  private boolean hasBigFunctions(final SortedSet<com.bosch.caltool.icdm.model.a2l.Function> funcList) {
    for (com.bosch.caltool.icdm.model.a2l.Function cdrFunction : funcList) {
      if (new ParamCollectionDataProvider().isBigFunction(cdrFunction)) {
        CDMLogger.getInstance().errorDialog("The function is big and cannot be opened without a function version",
            Activator.PLUGIN_ID);
        return true;
      }
    }
    return false;
  }

  /**
   * Import caldata files
   *
   * @param manager
   */
  private void createImportCalDataFile(final IMenuManager manager) {
    final Action importCalDataAction = new Action() {

      @Override
      public void run() {
        final FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
        fileDialog.setText("Import from File");
        com.bosch.caltool.icdm.common.util.CommonUtils.swapArrayElement(CommonUIConstants.FILE_EXTENSIONS,
            ScratchPadViewPart.this.preference.getString(CommonUIConstants.IMPORT_TO_SCRATCH_PAD_EXTN), 0);

        com.bosch.caltool.icdm.common.util.CommonUtils.swapArrayElement(CommonUIConstants.FILE_NAMES,
            ScratchPadViewPart.this.preference.getString(CommonUIConstants.IMPORT_TO_SCRATCH_PAD_NAME), 0);
        // ICDM-1131
        fileDialog.setFilterNames(CommonUIConstants.FILE_NAMES);
        fileDialog.setFilterExtensions(CommonUIConstants.FILE_EXTENSIONS);

        final String selectedFile = fileDialog.open();
        String fileName = fileDialog.getFileName();
        // store preferences
        ScratchPadViewPart.this.preference.setValue(CommonUIConstants.IMPORT_TO_SCRATCH_PAD_EXTN,
            CommonUIConstants.FILE_EXTENSIONS[fileDialog.getFilterIndex()]);

        ScratchPadViewPart.this.preference.setValue(CommonUIConstants.IMPORT_TO_SCRATCH_PAD_NAME,
            CommonUIConstants.FILE_NAMES[fileDialog.getFilterIndex()]);
        if (selectedFile != null) {
          try {
            final CaldataFileParserHandler parserHandler =
                new CaldataFileParserHandler(ParserLogger.getInstance(), null);
            // Parsing the input files selected
            final Map<String, CalData> calDataMap = parserHandler.getCalDataObjects(selectedFile);
            SortedSet<String> keys = new TreeSet<String>(calDataMap.keySet());
            for (String key : keys) {
              CalData calData = calDataMap.get(key);
              ScratchPadDataFetcher dataProvider = new ScratchPadDataFetcher();
              CalDataType type = null;
              // ICDM-1247
              String[] fileNameExtn = fileName.split("\\.");
              if (com.bosch.caltool.icdm.common.util.CommonUtils
                  .isEqualIgnoreCase(fileNameExtn[fileNameExtn.length - 1], CommonUIConstants.DCM_EXTN)) {
                type = CalDataType.IMPORT_DCM;
              }
              else if (com.bosch.caltool.icdm.common.util.CommonUtils
                  .isEqualIgnoreCase(fileNameExtn[fileNameExtn.length - 1], CommonUIConstants.CDFX_EXTN)) {
                type = CalDataType.IMPORT_CDFX;
              }
              else if (com.bosch.caltool.icdm.common.util.CommonUtils
                  .isEqualIgnoreCase(fileNameExtn[fileNameExtn.length - 1], CommonUIConstants.PACO_EXTN)) {
                type = CalDataType.IMPORT_PACO;
              }
              final SeriesStatisticsInfo seriesStatisticsInfo = new SeriesStatisticsInfo(calData, type);
              dataProvider.setSeriesStatsInfo(seriesStatisticsInfo);
              checkCalDataExists(calData);
              ScratchPadViewPart.this.nodeList.add(dataProvider);
            }
            // Icdm-707 sorter for Scrach pad
            Collections.sort(ScratchPadViewPart.this.nodeList, new ScratchPadSorter());
            ScratchPadViewPart.this.tableViewer.setInput(ScratchPadViewPart.this.nodeList);
          }
          catch (IcdmException exp) {
            CDMLogger.getInstance().errorDialog(
                "Error occured while parsing the files to be imported !" + exp.getMessage(), exp, Activator.PLUGIN_ID);
          }
        }
      }
    };
    importCalDataAction.setText("Import CalData files");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.DCM_UPLOAD_28X30);
    importCalDataAction.setImageDescriptor(imageDesc);
    importCalDataAction.setEnabled(true);
    manager.add(importCalDataAction);

  }

  /**
   * @param manager
   */
  // ICDM-242
  private void createExportCDFContextMenu(final IMenuManager manager) {
    final IStructuredSelection iStructSelection =
        (IStructuredSelection) ScratchPadViewPart.this.tableViewer.getSelection();

    final Action exportCDFAction = new Action() {

      @Override
      public void run() {
        List<ScratchPadDataFetcher> selectionList = iStructSelection.toList();
        if (hasDuplicateParams(selectionList)) {
          return;
        }
        // Create cdfCalDataObject
        Map<String, CalData> cdfCalDataObjects = new ConcurrentHashMap<String, CalData>();
        for (ScratchPadDataFetcher scratchPadDataFetcher : selectionList) {
          CalData calDataObj = scratchPadDataFetcher.getSeriesStatsInfo().getCalData();
          String calDataName = calDataObj.getShortName();
          // Icdm-797 null values for the Unit in caldataphy Obj
          if ((calDataObj.getCalDataPhy() != null) &&
              ((calDataObj.getCalDataPhy().getUnit() == null) || "null".equals(calDataObj.getCalDataPhy().getUnit()))) {
            calDataObj.getCalDataPhy().setUnit("");
          }
          cdfCalDataObjects.put(calDataName, calDataObj);
        }
        if (cdfCalDataObjects.size() > 0) {
          String fileName = "CDFX_Export_Report";
          fileName = fileName.replaceAll("[^a-zA-Z0-9]+", "_");
          final FileDialog saveFileDialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.SAVE);
          saveFileDialog.setText("Save");
          // Icdm-932 change the default file format to cdfx
          String[] filterExt = { "*.cdfx", "*.cdf", "*.*" };
          saveFileDialog.setFilterExtensions(filterExt);
          saveFileDialog.setFilterIndex(0);
          saveFileDialog.setFileName(fileName);
          saveFileDialog.setOverwrite(true);
          String fileSelected = saveFileDialog.open();
          if (fileSelected != null) {
            Job compareJob = new CDFFileExportJob(ScratchPadViewPart.this.exportCDFRule, cdfCalDataObjects,
                fileSelected, filterExt[0], true);
            CommonUiUtils.getInstance().showView(CommonUIConstants.PROGRESS_VIEW);
            compareJob.schedule();
          }
        }
      }
    };
    // Icdm-932 change the name to CDFX
    exportCDFAction.setText("Export as CDFX");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.EXPORT_DATA_16X16);
    // Set imange/icon to the export as CDF
    exportCDFAction.setImageDescriptor(imageDesc);
    boolean isEnabled = isExportCDFActionEnabled(iStructSelection);
    exportCDFAction.setEnabled(isEnabled);
    // Add export as CDF action to Menu manager
    manager.add(exportCDFAction);
  }

  /**
   * @param selectionList selectionList
   * @return true if there are duplicate entries.
   */
  protected boolean hasDuplicateParams(final List<ScratchPadDataFetcher> selectionList) {

    Set<String> allParamNames = new HashSet<>();
    for (ScratchPadDataFetcher scratchPadDataFetcher : selectionList) {
      String scratchPadEntryName = scratchPadDataFetcher.getSeriesStatsInfo().getCalData().getShortName();
      if (!allParamNames.add(scratchPadEntryName)) {
        CDMLogger.getInstance().errorDialog("You selected the parameter '" + scratchPadEntryName +
            "' several times.The parameter name must be unique in a file.", Activator.PLUGIN_ID);
        return true;
      }
    }

    return false;
  }


  /**
   * @param manager
   * @param iStructuredSelection
   */
  // ICDM-254
  private void createCopyContextMenu(final IMenuManager manager, final IStructuredSelection iStructuredSelection) {

    CommonActionSet cmnActionSet = new CommonActionSet();

    List<ScratchPadDataFetcher> selectionList = iStructuredSelection.toList();
    // ICDM-1348
    boolean copyActionEnabled = isCopyActionEnabled(iStructuredSelection);
    if (copyActionEnabled && (selectionList.get(0).getSeriesStatsInfo() != null)) {// ICDM-1348
      cmnActionSet.setCopyAction(manager, "Copy Parameter Name to Clipboard", this.tableViewer, copyActionEnabled);
    }
    else {
      cmnActionSet.setCopyAction(manager, "Copy", this.tableViewer, copyActionEnabled);
    }
  }

  /**
   * @param manager
   * @param iStructuredSelection
   */
  // ICDM-254
  private void compareCalDataContextMenu(final IMenuManager manager, final IStructuredSelection iStructuredSelection) {

    final Action compareCaldataAction = new Action() {

      @Override
      public void run() {
        List<ScratchPadDataFetcher> dataList = iStructuredSelection.toList();
        if (dataList.get(0).getSeriesStatsInfo() == null) {
          CDMLogger.getInstance().errorDialog(
              "The Parameter name selected is copied from the clipboard.Table/Graph viewer cannot be shown.",
              Activator.PLUGIN_ID);
          return;
        }
        String calDataPhyValType = dataList.get(0).getSeriesStatsInfo().getCalData().getCalDataPhy().getType();
        if (isCaldatasSameType(dataList, calDataPhyValType)) {
          showTableGraph(dataList);
        }
        else {
          return;
        }
      }
    };

    compareCaldataAction.setText("Show in Table/Graph viewer");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.TABLE_GRAPH_16X16);
    // Set imange/icon to the export as CDF
    compareCaldataAction.setImageDescriptor(imageDesc);
    compareCaldataAction
        .setEnabled(isAllElementsCalData(iStructuredSelection.toList()) && !iStructuredSelection.isEmpty());
    // Add export as CDF action to Menu manager
    manager.add(compareCaldataAction);

  }


  /**
   * @param dataList dataList
   */
  protected void showTableGraph(final List<ScratchPadDataFetcher> dataList) {
    Map<String, CalData> calDataMap = new ConcurrentHashMap<String, CalData>();
    for (ScratchPadDataFetcher data : dataList) {
      String key = data.getSeriesStatsInfo().getCalData().getShortName() + "::" +
          data.getSeriesStatsInfo().getCalDataPhyValType().getLabel() + "::" +
          data.getSeriesStatsInfo().getCalData().getCalDataPhy().getSimpleDisplayValue() +
          data.getSeriesStatsInfo().getDataSetName();
      calDataMap.put(key, data.getSeriesStatsInfo().getCalData());
    }
    // ICDM-2304
    CalDataViewerDialog calDataViewerDialog = new CalDataViewerDialog(Display.getCurrent().getActiveShell(), calDataMap,
        "CalData Comparison", "Table/Graph Representation", false, false);
    calDataViewerDialog.open();
  }

  /**
   * @param list
   * @return true if all the objects are Cal data.
   */
  private boolean isAllElementsCalData(final List<Object> list) {

    for (Object dataProvider : list) {
      if ((((ScratchPadDataFetcher) dataProvider).getSeriesStatsInfo() == null) &&
          (((ScratchPadDataFetcher) dataProvider).getParameter() == null)) {
        return false;
      }
    }
    return true;
  }


  /**
   * Checks whether two objects are selected for compareAction
   *
   * @return boolean
   */
  private boolean isExportCDFActionEnabled(final IStructuredSelection iStructSelection) {
    List<ScratchPadDataFetcher> selectionList = iStructSelection.toList();
    if ((selectionList == null) || selectionList.isEmpty()) {
      return false;
    }
    for (ScratchPadDataFetcher scratchPadDataFetcher : selectionList) {
      if (scratchPadDataFetcher.getSeriesStatsInfo() == null) {
        return false;
      }
    }
    return true;
  }

  /**
   * Checks whether objects are selected for copy Action
   *
   * @return boolean
   */
  private boolean isCopyActionEnabled(final IStructuredSelection iStructSelection) {
    boolean isEnabled = false;
    List<ScratchPadDataFetcher> selectionList = iStructSelection.toList();
    if ((selectionList != null) && (selectionList.size() == 1) && (selectionList.get(0) != null) &&
        (selectionList.get(0).getSeriesStatsInfo() != null)) { // ICDM-1348
      return true;
    }
    else if ((selectionList != null) && (selectionList.size() > 0)) {
      for (ScratchPadDataFetcher scratchPadDataFetcher : selectionList) {
        if (scratchPadDataFetcher.getAttrVal() == null) {
          isEnabled = false;
          break;
        }
        isEnabled = true;
      }
    }
    return isEnabled;
  }

  /**
   * Checks whether two objects are selected for compareAction
   *
   * @return
   */
  private boolean isCompareActionEnabled(final IStructuredSelection iStructSelection) {
    boolean isEnabled = false;
    List selectionList = iStructSelection.toList();
    if ((selectionList != null) && (selectionList.size() == 2)) {
      // disable subvariant selection.Need to remove check after subvariant code implementation
      ScratchPadDataFetcher selectedObject1 = (ScratchPadDataFetcher) selectionList.get(0);
      ScratchPadDataFetcher selectedObject2 = (ScratchPadDataFetcher) selectionList.get(1);
      if ((selectedObject1.getPidcSubVariant() != null) && (selectedObject2.getPidcSubVariant() != null)) {
        isEnabled = false;
      }
      // ICDM-242
      else if (((selectedObject1.getPidcVersion() != null) && (selectedObject2.getPidcVersion() != null)) ||
          ((selectedObject1.getPidcVariant() != null) && (selectedObject2.getPidcVariant() != null))) {
        isEnabled = true;
      }
    }
    return isEnabled;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFocus() {
    this.expReportService = (CommandState) CommonUiUtils.getInstance().getSourceProvider();
    this.expReportService.setExportService(false);
    this.tableViewer.getControl().setFocus();
  }

  /**
   * This method initializes composite
   */
  private void createComposite() {

    ScrolledComposite scrollComp = new ScrolledComposite(this.top, SWT.H_SCROLL | SWT.V_SCROLL);
    scrollComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
    GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite = new Composite(scrollComp, SWT.NONE);
    this.composite.setLayout(new GridLayout());
    this.composite.setLayoutData(gridData);
    scrollComp.setContent(this.composite);
    scrollComp.setExpandHorizontal(true);
    scrollComp.setExpandVertical(true);
    scrollComp.setMinSize(this.composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    createGroup();
    createToolBar();
  }

  /**
   * Creates an actions on view tool bar
   */
  private void createToolBar() {

    IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
    Separator separator = new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
    mgr.add(separator);
    separator = new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
    mgr.add(separator);

    this.createTBarDelAction = createDelAction();
    separator = new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
    mgr.add(separator);
    mgr.add(this.createTBarDelAction);
    this.createTBarDelAction.setEnabled(false);

  }

  /**
   * This method initializes group
   */
  private void createGroup() {

    GridData tvGridData = new GridData();
    tvGridData.horizontalAlignment = GridData.FILL;
    tvGridData.grabExcessHorizontalSpace = true;
    tvGridData.grabExcessVerticalSpace = true;
    tvGridData.verticalAlignment = GridData.FILL;

    // Icdm-1356- Create Filter for Scratch pad View part.
    createFilterTxtSection();
    GridData txtGridData = new GridData();
    txtGridData.horizontalAlignment = GridData.FILL;
    txtGridData.grabExcessHorizontalSpace = true;
    txtGridData.verticalAlignment = GridData.CENTER;
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    Group group = new Group(this.form.getBody(), SWT.NONE);
    group.setLayoutData(gridData);
    group.setLayout(gridLayout);
    this.tableViewer = new TableViewer(group, SWT.FULL_SELECTION | SWT.VIRTUAL | SWT.MULTI);
    this.tableViewer.setUseHashlookup(true);

    // ICDM-589
    Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };
    DropTarget target = new DropTarget(this.tableViewer.getControl(), DND.DROP_COPY | DND.DROP_MOVE);
    target.setTransfer(transferTypes);
    target.addDropListener(new DropTargetAdapter() {

      @Override
      public void drop(final DropTargetEvent event) {

        if (event.data == null) {
          event.detail = DND.DROP_NONE;
          return;
        }
        boolean validDrag = false;
        StringBuilder invalidParams = new StringBuilder();
        StructuredSelection structuredSelection = (StructuredSelection) event.data;
        ScratchPadDataFetcher scratchPadDataFetcher = null;
        for (Object selectedElement : structuredSelection.toList()) {
          boolean checkAdd = new CommonActionSet().checkIfAlreadyAdded(selectedElement, ScratchPadViewPart.this);
          scratchPadDataFetcher = new ScratchPadDataFetcher();

          if (!checkAdd && (selectedElement instanceof PidcTreeNode)) {
            PidcTreeNode pidctreeNodeElement = ((PidcTreeNode) selectedElement);

            PIDC_TREE_NODE_TYPE nodeType = pidctreeNodeElement.getNodeType();

            switch (nodeType) {

              case PIDC_A2L:
                validDrag = true;
                scratchPadDataFetcher.setPidcA2l(pidctreeNodeElement.getPidcA2l());
                break;

              case ACTIVE_PIDC_VERSION:
              case OTHER_PIDC_VERSION:
                validDrag = true;
                scratchPadDataFetcher.setPidcVersion(pidctreeNodeElement.getPidcVersion());
                break;

              default:
                CDMLogger.getInstance().errorDialog("The selected element cannot be moved to scratch pad.",
                    Activator.PLUGIN_ID);

            }
          }
          if (!checkAdd && (selectedElement instanceof Function)) {
            validDrag = true;
            scratchPadDataFetcher.setFunction((Function) selectedElement);
          }
          // Check for instance of A2LParameter not required since this is applicable only in case of adding to
          // scratchPad via right click context menu
          // During drag A2lParameter is converted to A2lParamInfo to satisfy drag and drop to "export to CDF" wizard
          if (!checkAdd && (selectedElement instanceof A2LParamInfo)) {
            validDrag = true;
            A2LParamInfo a2lParamInfo = (A2LParamInfo) selectedElement;
            if ((a2lParamInfo.getA2lParam().getCalData() == null) ||
                (a2lParamInfo.getA2lParam().getCalData().getCalDataPhy() == null)) {
              invalidParams.append(a2lParamInfo.getA2lParam().getName() + ", ");
              continue;
            }
            for (int i = 0; i < ScratchPadViewPart.this.nodeList.size(); i++) {
              final ScratchPadDataFetcher nodeCheck = ScratchPadViewPart.this.nodeList.get(i);
              if ((nodeCheck.getSeriesStatsInfo() != null) && (nodeCheck.getSeriesStatsInfo().getCalData() != null)) {
                if ((nodeCheck.getSeriesStatsInfo().getCalData().getCalDataPhy() != null) &&
                    nodeCheck.getSeriesStatsInfo().getCalData().getCalDataPhy().getName()
                        .equals(a2lParamInfo.getA2lParam().getCalData().getCalDataPhy().getName()) &&
                    (nodeCheck.getSeriesStatsInfo().getCalData().getCalDataPhy()
                        .equals(a2lParamInfo.getA2lParam().getCalData().getCalDataPhy()))) {
                  ScratchPadViewPart.this.nodeList.remove(nodeCheck);
                  break;
                }
              }
            }
            final CalData calDataObj = a2lParamInfo.getA2lParam().getCalData();

            final SeriesStatisticsInfo seriesStatisticsInfo =
                new SeriesStatisticsInfo(calDataObj, CalDataType.DSTVALUE);
            seriesStatisticsInfo.setDataSetName(a2lParamInfo.getA2lFileName());
            scratchPadDataFetcher.setSeriesStatsInfo(seriesStatisticsInfo);
          }
          // ICDM-226
          if (!checkAdd && (selectedElement instanceof SeriesStatisticsInfo)) {
            validDrag = true;
            scratchPadDataFetcher.setSeriesStatsInfo((SeriesStatisticsInfo) selectedElement);
            checkCalDataExists(((SeriesStatisticsInfo) selectedElement).getCalData());
          }
          if (!checkAdd && (selectedElement instanceof PidcVersion)) {
            validDrag = true;
            PidcVersion pidcVer = (PidcVersion) selectedElement;
            scratchPadDataFetcher.setPidcVersion(pidcVer);
          }
          if (!checkAdd && (selectedElement instanceof PIDCScoutResult)) {
            validDrag = true;
            PIDCScoutResult scoutResult = (PIDCScoutResult) selectedElement;
            scratchPadDataFetcher.setPidcVersion(scoutResult.getPidcVersion());
          }
          else if (!checkAdd && ((selectedElement instanceof PidcVariant) ||
              ((selectedElement instanceof PIDCDetailsNode) && ((PIDCDetailsNode) selectedElement).isVariantNode()))) {
            validDrag = true;
            scratchPadDataFetcher.setPidcVariant(CommonUiUtils.getInstance().getPidcVariantObject(selectedElement));
          }
          else if (!checkAdd && (selectedElement instanceof PidcSubVariant)) {
            validDrag = true;
            scratchPadDataFetcher.setPidcSubVariant((PidcSubVariant) selectedElement);
          }
          else if (!checkAdd && (selectedElement instanceof AttributeValue)) {
            validDrag = true;
            scratchPadDataFetcher.setAttrVal((AttributeValue) selectedElement);
          }
          if (validDrag) {
            ScratchPadViewPart.this.nodeList.add(scratchPadDataFetcher);
          }
        }
        if (validDrag) {
          // Icdm-707 sorter for Scrach pad
          Collections.sort(ScratchPadViewPart.this.nodeList, new ScratchPadSorter());
          ScratchPadViewPart.this.tableViewer.setInput(ScratchPadViewPart.this.nodeList);
          ScratchPadViewPart.this.tableViewer.setSelection(new StructuredSelection(scratchPadDataFetcher), true);
        }
        if (invalidParams.length() > 0) {
          String message = invalidParams.substring(0, invalidParams.length() - 2);
          MessageDialogUtils.getInfoMessageDialog("Info",
              message + " not added to ScratchPad since no value is present");
        }
      }
    });


    this.tableViewer.getTable().setLayoutData(tvGridData);
    this.tableViewer.getTable().setLinesVisible(false);
    this.tableViewer.getTable().setHeaderVisible(false);

    new TableViewerColumn(this.tableViewer, SWT.LEFT);
    new TableViewerColumn(this.tableViewer, SWT.LEFT);


    for (int i = 0; i < this.tableViewer.getTable().getColumns().length; i++) {
      TableColumn[] columns = this.tableViewer.getTable().getColumns();
      columns[i].setWidth(ColWidths[i]);
    }

    this.tableViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.tableViewer.setLabelProvider(new ScratchPadTableLabelProvider());

    this.tableViewer.getTable().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        IStructuredSelection selection = (IStructuredSelection) ScratchPadViewPart.this.tableViewer.getSelection();
        if ((selection != null) && (selection.size() != 0)) {
          ScratchPadViewPart.this.createTBarDelAction.setEnabled(true);
        }
      }
    });
    this.tableViewer.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE, transferTypes,
        new CustomDragListener(this.tableViewer));

    addFilter();
    addModifyListenerForFilterTxt();
    addKeyListener();
    this.section.setClient(this.form);
  }


  /**
   * ICDM-1348 adding key listener for ctrl+c
   */
  private void addKeyListener() {
    this.tableViewer.getControl().addKeyListener(new KeyListener() {

      @Override
      public void keyReleased(final KeyEvent keyevent) {
        // Not applicable
      }

      @Override
      public void keyPressed(final KeyEvent keyevent) {
        IStructuredSelection sel = (IStructuredSelection) ScratchPadViewPart.this.tableViewer.getSelection();
        if ((sel != null) && !sel.isEmpty() && (sel.size() == 1)) {
          createCopyToClipboard(keyevent, sel);
        }
        if ((keyevent.stateMask == CommonUIConstants.KEY_CTRL) && (keyevent.keyCode == CommonUIConstants.KEY_PASTE)) {
          CommonActionSet actionSet = new CommonActionSet();
          actionSet.addcopiedItemsToScratchPad(ScratchPadViewPart.this);
        }
      }
    });
  }

  /**
   * @param keyevent
   * @param sel
   */
  private void createCopyToClipboard(final KeyEvent keyevent, final IStructuredSelection sel) {
    Object firstElement = sel.getFirstElement();
    if (firstElement instanceof ScratchPadDataFetcher) {
      SeriesStatisticsInfo seriesStatsInfo = ((ScratchPadDataFetcher) firstElement).getSeriesStatsInfo();
      if (CommonUtils.isNotNull(seriesStatsInfo) && (keyevent.stateMask == CommonUIConstants.KEY_CTRL) &&
          (keyevent.keyCode == CommonUIConstants.KEY_COPY)) {
        CommonUiUtils.setTextContentsToClipboard(seriesStatsInfo.getCalData().getShortName());
      }

    }
  }

  private void createFilterTxtSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, this.toolkit, "");
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    // ICDM-183
    this.section.getDescriptionControl().setEnabled(false);

    this.form = this.toolkit.createForm(this.section);
    this.filterTxt = TextUtil.getInstance().createFilterText(this.toolkit, this.form.getBody(),
        GridDataUtil.getInstance().getTextGridData(), "type filter text");
    this.form.getBody().setLayout(new GridLayout());
  }


  /**
   *
   */
  private void addFilter() {
    this.scratchPadFilter = new ScratchPadFilter();
    this.tableViewer.addFilter(this.scratchPadFilter);

  }


  /**
   * This method deletes a selected row in scratchpad tableviewer
   *
   * @return IAction
   */
  public IAction createDelAction() {

    IAction deleteAction = new Action("Delete") {

      @Override
      public void run() {
        IStructuredSelection sel = (IStructuredSelection) ScratchPadViewPart.this.tableViewer.getSelection();
        if ((sel != null) && !sel.isEmpty()) {
          ScratchPadViewPart.this.nodeList.removeAll(sel.toList());
          ScratchPadViewPart.this.tableViewer.refresh();
          ScratchPadViewPart.this.createTBarDelAction.setEnabled(false);
        }
      }
    };
    // Set image for delete action
    deleteAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.REMOVE_16X16));

    return deleteAction;
  }

  /**
   * Checks whether caldata dropped in scratchpad already exists,if so it will be removed
   *
   * @param selectedElement
   * @param seriesStatisticsType
   */
  private void checkCalDataExists(final CalData selectedElement) {
    for (int i = 0; i < ScratchPadViewPart.this.nodeList.size(); i++) {
      final ScratchPadDataFetcher nodeCheck = ScratchPadViewPart.this.nodeList.get(i);
      if ((nodeCheck.getSeriesStatsInfo() != null) && (nodeCheck.getSeriesStatsInfo().getCalData() != null)) {
        if ((nodeCheck.getSeriesStatsInfo().getCalData().getCalDataPhy() != null) &&
            nodeCheck.getSeriesStatsInfo().getCalData().getCalDataPhy().getName()
                .equals(selectedElement.getCalDataPhy().getName()) &&
            (nodeCheck.getSeriesStatsInfo().getCalData().getCalDataPhy().equals(selectedElement.getCalDataPhy()))) {

          ScratchPadViewPart.this.nodeList.remove(nodeCheck);
          break;
        }
      }
    }
  }


  /**
   * This method creates filter text
   */
  private void addModifyListenerForFilterTxt() {
    this.filterTxt.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        final String text = ScratchPadViewPart.this.filterTxt.getText().trim();
        ScratchPadViewPart.this.scratchPadFilter.setFilterText(text);
        ScratchPadViewPart.this.tableViewer.refresh();
      }
    });
  }

  /**
   * @param dataList
   * @param calDataPhyValType
   * @return true if all the Cal data objects are of same type
   */
  private boolean isCaldatasSameType(final List<ScratchPadDataFetcher> dataList, final String calDataPhyValType) {
    for (ScratchPadDataFetcher data : dataList) {
      if (ApicUtil.compare(calDataPhyValType, data.getSeriesStatsInfo().getCalData().getCalDataPhy().getType()) != 0) {
        CDMLogger.getInstance().errorDialog("Only parameters of the same type can be compared", Activator.PLUGIN_ID);
        return false;
      }
    }
    return true;
  }


  /**
   * @return the tableViewer
   */
  public TableViewer getTableViewer() {
    return this.tableViewer;
  }

}
