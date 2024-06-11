/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.wizards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.apic.ui.table.filters.PidcVarListTextFilter;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.emr.EmrFile;
import com.bosch.caltool.icdm.model.emr.EmrFileEmsVariantMapping;
import com.bosch.caltool.icdm.model.emr.EmrPidcVariant;
import com.bosch.caltool.icdm.model.util.ModelUtil;
import com.bosch.caltool.icdm.ws.rest.client.apic.emr.EmrFileServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.emr.EmrFileVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * @author mkl2cob
 */
public class EMRAssignVariantComposite {

  /**
   *
   */
  private static final int PIDC_VAR_LIST_HEIGHT = 75;

  /**
   * constant for showing number of rows
   */
  private static final int MIN_ROW_COUNT = 7;

  private static final String DELIMITER = " / ";

  private static final String SPACE = " ";

  /**
   * Composite
   */
  private Composite composite;
  /**
   * FormToolkit
   */
  private FormToolkit formToolkit;
  /**
   * create assignments section
   */
  private Section sectionOne;
  /**
   * Table sorter
   */
  private EmrVariantTableSorter variantGridSorter;
  /**
   * Form
   */
  private Form formOne;
  /**
   * current assignments Section
   */
  private Section sectionTwo;
  /**
   * Form
   */
  private Form formTwo;

  /**
   * list box for sheet names
   */
  private List fileEmsList;

  private List variantsList;

  private final boolean isUpdateCmd;

  private Button assignVariantBtn;

  private EmrFile selectedEmrFile;

  private final Set<Long> emrFileIds;

  private Action deleteAssignmentAction;

  private EmrFileEmsVariantMapping wsOutput;

  /**
   * table viewer
   */
  private GridTableViewer mappedDataTabViewer;

  private TreeSet<EmrGridEntryData> gridTableDataSet;

  private final ArrayList<String> alreadyMappedList = new ArrayList<>();

  private final Map<String, EmrPidcVariant> possibleNewMapping = new HashMap<>();
  // Set of pidc variants to be set as input to list viewer
  private final Set<String> varSet = new HashSet<>();
  private Text filterTxt;


  /**
   * @param emrFileIds fileIds
   */
  public EMRAssignVariantComposite(final Set<Long> emrFileIds, final boolean isEdit) {
    this.isUpdateCmd = isEdit;
    this.emrFileIds = new HashSet<>(emrFileIds);
  }

  /**
   * @return the fileEmsList
   */
  public List getfileEmsList() {
    return this.fileEmsList;
  }

  /**
   * This method initializes composite
   *
   * @param workArea Composite
   * @param formToolkit1 FormToolkit
   * @param pidcVersion PIDCVersion
   * @return Composite
   */
  public Composite createComposite(final Composite workArea, final FormToolkit formToolkit1,
      final PidcVersion pidcVersion) {
    this.formToolkit = formToolkit1;
    GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite = formToolkit1.createComposite(workArea);
    this.composite.setLayout(new GridLayout());
    createFirstSection();
    createSecondSection();
    this.composite.setLayoutData(gridData);
    return this.composite;
  }

  /**
   * This method initializes section
   *
   * @param pidcVersion
   */
  private void createFirstSection() {
    this.sectionOne = SectionUtil.getInstance().createSection(this.composite, this.formToolkit,
        GridDataUtil.getInstance().getGridData(), "Create assignments to PIDC/Variants");
    this.sectionOne.setDescription("Select which Sheets/Emission standard you want to assign to a PIDC");
    createFormOne();
    this.sectionOne.setClient(this.formOne);
  }


  /**
   * This method initializes section
   */
  private void createSecondSection() {
    this.sectionTwo = SectionUtil.getInstance().createSection(this.composite, this.formToolkit,
        GridDataUtil.getInstance().getGridData(), "Current assignments in PIDC");
    this.sectionTwo.setDescription("Mark the variants for which the sheets are relevant");
    createFormTwo();
    this.sectionTwo.setClient(this.formTwo);
  }

  /**
   * This method initializes Top form
   *
   * @param pidcVersion
   */
  private void createFormOne() {
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    this.formOne = this.formToolkit.createForm(this.sectionOne);
    this.formOne.getBody().setLayout(gridLayout);

    // sheet name
    LabelUtil.getInstance().createLabel(this.formOne.getBody(), "Sheet Name(s):");
    LabelUtil.getInstance().createEmptyLabel(this.formOne.getBody());


    this.fileEmsList = new List(this.formOne.getBody(), SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
    GridData fileListGridData = GridDataUtil.getInstance().getGridData();
    fileListGridData.horizontalSpan = 2;
    this.fileEmsList.setLayoutData(fileListGridData);
    this.fileEmsList.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        String[] itemsSelected = EMRAssignVariantComposite.this.fileEmsList.getSelection();
        for (String element : itemsSelected) {
          EMRAssignVariantComposite.this.fileEmsList.setToolTipText(element);
        }
        toggleMappingButtonBasedOnSelection();
      }
    });
    // pidc variants
    LabelUtil.getInstance().createLabel(this.formOne.getBody(), "PIDC Variant(s):");
    // create filter text
    this.filterTxt = this.formToolkit.createText(this.formOne.getBody(), null, SWT.SINGLE | SWT.BORDER);
    GridData filterTxtGridData = GridDataUtil.getInstance().getGridData();
    filterTxtGridData.horizontalSpan = 2;
    this.filterTxt.setLayoutData(filterTxtGridData);
    // pidc variants list
    this.variantsList = new List(this.formOne.getBody(), SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
    GridData varListGridData = GridDataUtil.getInstance().getGridData();
    varListGridData.grabExcessHorizontalSpace = true;
    varListGridData.grabExcessVerticalSpace = true;
    varListGridData.verticalAlignment = GridData.FILL;
    varListGridData.widthHint = getStringWidthHint(15, this.variantsList);
    varListGridData.heightHint = PIDC_VAR_LIST_HEIGHT;
    this.variantsList.setLayoutData(varListGridData);
    ListViewer listViewer = new ListViewer(this.variantsList);
    listViewer.getList().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    listViewer.setContentProvider(new ArrayContentProvider());
    final PidcVarListTextFilter filter = new PidcVarListTextFilter();
    this.filterTxt.setMessage(Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.filterTxt.addModifyListener(event -> {
      final String text = EMRAssignVariantComposite.this.filterTxt.getText().trim();
      filter.setFilterText(text);
      listViewer.refresh();
    });
    listViewer.setInput(this.varSet);
    listViewer.addFilter(filter);
    listViewer.getList().setLayoutData(varListGridData);
    this.variantsList.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        String[] itemsSelected = EMRAssignVariantComposite.this.variantsList.getSelection();
        for (String element : itemsSelected) {
          EMRAssignVariantComposite.this.variantsList.setToolTipText(element);
        }
        toggleMappingButtonBasedOnSelection();
      }
    });

    GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.horizontalSpan = 3;
    gridData.horizontalAlignment = SWT.CENTER;

    Composite btnComp = new Composite(this.formOne.getBody(), SWT.NONE);
    btnComp.setLayoutData(gridData);
    btnComp.setLayout(new GridLayout());

    this.assignVariantBtn = new Button(btnComp, SWT.CENTER);
    GridData btnGridData = new GridData();
    btnGridData.widthHint = 70;
    this.assignVariantBtn.setLayoutData(btnGridData);
    this.assignVariantBtn.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.DOWN_BUTTON_ICON_16X16));

    // Call WS to get mapped/unmapped data
    loadFieldData();


    this.assignVariantBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        String[] selectedEms = EMRAssignVariantComposite.this.fileEmsList.getSelection();
        String[] selectedVariant = EMRAssignVariantComposite.this.variantsList.getSelection();
        Set<EmrPidcVariant> emrPidcEmsSet = new HashSet<>();
        for (String element : selectedEms) {
          EmrPidcVariant dummyNewMapping = EMRAssignVariantComposite.this.possibleNewMapping.get(element);
          for (String varaintElement : selectedVariant) {
            PidcVariant variant = (PidcVariant) EMRAssignVariantComposite.this.variantsList.getData(varaintElement);
            dummyNewMapping.setPidcVariantId(variant.getId());
            if (!EMRAssignVariantComposite.this.alreadyMappedList
                .contains(getUniqueMappingId(dummyNewMapping.getEmrFileId(), dummyNewMapping.getEmissionStdId(),
                    variant.getId(), dummyNewMapping.getEmrVariant()))) {
              emrPidcEmsSet.add(new EmrPidcVariant(dummyNewMapping));
            }
          }
        }

        // Call WS to save the selected mapping & refresh tables
        if (CommonUtils.isNotEmpty(emrPidcEmsSet)) {
          saveEmrFileEmsMapping(emrPidcEmsSet);
          loadFieldData();
          setTableViewerInput();
          // refresh the mapping button
          toggleMappingButtonBasedOnSelection();
        }
      }
    });

  }

  /*
   * The selected variants and File Ems are passed ,the method checks if any of the selected Ems and variant combination
   * is already mapped. If any combination is already mapped , the assign variant button need to be disabled
   */
  private void toggleMappingButtonBasedOnSelection() {
    if (this.selectedEmrFile == null) {
      // Assign variant dialog is not applicable
      return;
    }
    String[] selectedFileEms = EMRAssignVariantComposite.this.fileEmsList.getSelection();
    String[] selectedVariants = EMRAssignVariantComposite.this.variantsList.getSelection();
    // The selection is not valid
    if (!(this.selectedEmrFile.getIsVariant()) || CommonUtils.isNullOrEmpty(selectedFileEms) ||
        CommonUtils.isNullOrEmpty(selectedVariants)) {
      EMRAssignVariantComposite.this.assignVariantBtn.setEnabled(false);
      return;
    }
    // There are no existing mappings available
    if (CommonUtils.isNullOrEmpty(this.alreadyMappedList)) {
      EMRAssignVariantComposite.this.assignVariantBtn.setEnabled(true);
      return;
    }
    for (String variantName : selectedVariants) {
      for (String fileEmsName : selectedFileEms) {
        PidcVariant variant = (PidcVariant) EMRAssignVariantComposite.this.variantsList.getData(variantName);
        EmrPidcVariant emrPidcVariant = EMRAssignVariantComposite.this.possibleNewMapping.get(fileEmsName);
        if (this.alreadyMappedList.contains(getUniqueMappingId(emrPidcVariant.getEmrFileId(),
            emrPidcVariant.getEmissionStdId(), variant.getId(), emrPidcVariant.getEmrVariant()))) {
          EMRAssignVariantComposite.this.assignVariantBtn.setEnabled(false);
          return;
        }
      }
    }
    EMRAssignVariantComposite.this.assignVariantBtn.setEnabled(true);
  }

  /**
   * Get height hint even if item size is large
   *
   * @param nChars to be visible
   * @param control component
   * @return width hint
   */
  public static int getStringWidthHint(final int nChars, final Control control) {
    GC gc = new GC(control);
    gc.setFont(control.getFont());
    FontMetrics fontMetrics = gc.getFontMetrics();
    gc.dispose();
    return nChars * fontMetrics.getAverageCharWidth();
  }

  /**
   * @param emrPidcEmsSet object to be mapped
   */
  protected void saveEmrFileEmsMapping(final Set<EmrPidcVariant> emrPidcEmsSet) {
    EmrFileVariantServiceClient client = new EmrFileVariantServiceClient();
    try {
      client.saveEmrFileEmsVariantMapping(emrPidcEmsSet);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   *
   */
  private void loadFieldData() {
    // Clear data
    clearData();

    // WS call to get latest mapping
    getEmrFileVariantMappingWS();

    // Assigned items - Botton - Grid table
    this.gridTableDataSet = new TreeSet<>();
    this.wsOutput.getEmrFileEmsVariantMap().values().forEach(elementSet -> elementSet.forEach(element -> {
      long emrFileId = element.getEmrFileId();
      long emsId = element.getEmissionStdId();
      long variantId = element.getPidcVariantId();
      long emrVariant = element.getEmrVariant();

      String sheetName = EMRAssignVariantComposite.this.wsOutput.getEmrFilesMap().get(emrFileId).getName() + DELIMITER +
          EMRAssignVariantComposite.this.wsOutput.getEmissionStandard().get(emsId).getEmissionStandardName();
      if (CommonUtils.isNotEmpty(EMRAssignVariantComposite.this.wsOutput.getEmrVariantInfoMap()) &&
          EMRAssignVariantComposite.this.wsOutput.getEmrVariantInfoMap().containsKey(emsId) &&
          EMRAssignVariantComposite.this.wsOutput.getEmrVariantInfoMap().get(emsId).containsKey(emrVariant)) {
        sheetName =
            sheetName + EMRAssignVariantComposite.this.wsOutput.getEmrVariantInfoMap().get(emsId).get(emrVariant);
      }

      String variant = EMRAssignVariantComposite.this.wsOutput.getPidcVariants().get(variantId).getName();
      this.gridTableDataSet.add(new EmrGridEntryData(sheetName, variant, element));
      this.alreadyMappedList.add(getUniqueMappingId(emrFileId, emsId, variantId, emrVariant));

    }));

    Set<String> fileEmsSetInternal = new TreeSet<>();
    Set<String> variantSetInternal = new TreeSet<>();

    // Load all variants - (and remove if mapped)
    for (PidcVariant variant : this.wsOutput.getPidcVariants().values()) {
      variantSetInternal.add(variant.getName());
    }

    // SheetName List
    Map<Long, Set<Long>> emsFileIdMap = this.wsOutput.getEmrFileEmsMap();
    createFileVariantMappingList(emsFileIdMap, fileEmsSetInternal);

    /*
     * PidcVariants List : Right side
     */
    if (!fileEmsSetInternal.isEmpty()) {
      this.wsOutput.getPidcVariants().values().stream().sorted().forEach(variant -> {
        // If no EMS standard-sheets are in the excel-file, no need to show any variants
        if (variantSetInternal.contains(variant.getName())) {
          this.variantsList.add(variant.getName());
          this.variantsList.setData(variant.getName(), variant);
          this.varSet.add(variant.getName());
        }
      });

      // SheetName-file-Ems list : Left side
      fileEmsSetInternal.forEach(element -> this.fileEmsList.add(element));
    }

    // set State of action button
    if (!CommonUtils.isNotEmpty(this.fileEmsList.getItems()) || !CommonUtils.isNotEmpty(this.variantsList.getItems())) {
      EMRAssignVariantComposite.this.assignVariantBtn.setEnabled(false);
    }
    else {
      // default selection
      this.fileEmsList.setSelection(0);
      this.variantsList.setSelection(0);
      EMRAssignVariantComposite.this.assignVariantBtn.setEnabled(true);
    }
  }

  /**
   * Creates the file variant mapping list.
   *
   * @param emsFileIdMap the ems file id map
   * @param fileEmsSetInternal the file ems set internal
   */
  private void createFileVariantMappingList(final Map<Long, Set<Long>> emsFileIdMap,
      final Set<String> fileEmsSetInternal) {

    for (Long emrFileId : this.emrFileIds) {
      if (emsFileIdMap.containsKey(emrFileId)) {
        for (Long emsId : emsFileIdMap.get(emrFileId)) {
          String fileName = this.wsOutput.getEmrFilesMap().get(emrFileId).getName();

          // To get the list of sheetnames that would be displayed. Sheet names/Emission standard would be displayed
          // based on the EMR variants
          Map<String, Long> sheetsWithVariantMap = getSheetNamesWithVariantInfo(emsId, fileName);

          // During edit, only one emrfile will be selected for assigning variants
          this.selectedEmrFile = this.wsOutput.getEmrFilesMap().get(emrFileId);

          // Dummy mapping to be done for each of the sheet
          sheetsWithVariantMap.forEach((sheetName, emrVariant) -> {
            // Dummy object without variantId - Upon selection, variantId will be filled and saved
            EmrPidcVariant dummyNewMapping = new EmrPidcVariant();
            dummyNewMapping.setEmrFileId(emrFileId);
            dummyNewMapping.setEmissionStdId(emsId);
            dummyNewMapping.setEmrVariant(emrVariant);

            EMRAssignVariantComposite.this.possibleNewMapping.put(sheetName, dummyNewMapping);

            // If there is existing variant assignment
            addIfNoExisitngMapping(fileEmsSetInternal, emrFileId, emsId, sheetName, emrVariant);
          });
        }
      }
    }
  }

  /**
   * For each of the Emission standard procedure from EMR file, multiple rows would be displayed differentiated by their
   * Variants
   *
   * @param emsId Emission standard procedure ID
   * @param fileName Name of the input EMR File
   * @return Map of Sheetnames that would be displayed in UI along with their EMR variants For each of the Emission
   *         standard procedure from EMR file
   */
  private Map<String, Long> getSheetNamesWithVariantInfo(final Long emsId, final String fileName) {
    Map<String, Long> sheetsWithVariantMap = new HashMap<>();
    if (CommonUtils.isNotEmpty(this.wsOutput.getEmrVariantInfoMap()) &&
        this.wsOutput.getEmrVariantInfoMap().containsKey(emsId)) {
      this.wsOutput.getEmrVariantInfoMap().get(emsId).forEach((emrVar, emrVarInfo) -> {
        if (CommonUtils.isNotEmptyString(emrVarInfo)) {
          StringBuilder sheetNameBuilder = new StringBuilder(fileName);
          sheetNameBuilder.append(DELIMITER)
              .append(this.wsOutput.getEmissionStandard().get(emsId).getEmissionStandardName());
          sheetNameBuilder.append(SPACE).append(emrVarInfo);
          sheetsWithVariantMap.put(sheetNameBuilder.toString(), emrVar);
        }
      });
    }
    else {
      StringBuilder sheetNameBuilder = new StringBuilder(fileName);
      sheetNameBuilder.append(DELIMITER)
          .append(this.wsOutput.getEmissionStandard().get(emsId).getEmissionStandardName());
      sheetsWithVariantMap.put(sheetNameBuilder.toString(), 0L);
    }
    return sheetsWithVariantMap;
  }

  /**
   * @param fileEmsSetInternal
   * @param emrFileId
   * @param emsId
   * @param uiString
   */
  private void addIfNoExisitngMapping(final Set<String> fileEmsSetInternal, final Long emrFileId, final Long emsId,
      final String sheetName, final long emrVariant) {
    if (CommonUtils.isNotEmpty(this.alreadyMappedList)) {
      // Check if already mapped
      for (PidcVariant variant : this.wsOutput.getPidcVariants().values()) {
        if (!this.alreadyMappedList.contains(getUniqueMappingId(emrFileId, emsId, variant.getId(), emrVariant))) {
          // If not mapped, consider file/ems for possible assignment in fileEmsList for selection
          fileEmsSetInternal.add(sheetName);
        }
      }
    }
    else {
      fileEmsSetInternal.add(sheetName);
    }
  }

  /**
   *
   */
  private void clearData() {
    // Clear existing data
    this.fileEmsList.removeAll();
    this.variantsList.removeAll();
    this.alreadyMappedList.clear();
    this.possibleNewMapping.clear();
  }

  private void getEmrFileVariantMappingWS() {
    EmrFileVariantServiceClient client = new EmrFileVariantServiceClient();
    try {
      this.wsOutput = client.getPidcEmrFileEmsVariantMapping(this.emrFileIds);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * This method initializes form
   */
  private void createFormTwo() {
    GridLayout gridLayout = new GridLayout();
    this.formTwo = this.formToolkit.createForm(this.sectionTwo);
    this.formTwo.getBody().setLayout(gridLayout);

    GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.heightHint = 100;

    if (this.isUpdateCmd) {
      createValidForPidcFlag();
    }

    // Grid Table
    this.mappedDataTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.formTwo.getBody(),
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI, gridData);

    this.mappedDataTabViewer.setItemCount(MIN_ROW_COUNT);

    // Actions
    createTableToolbarAction();

    // Table columns
    createGridTableColumns();

    // Content PRovider
    this.mappedDataTabViewer.setContentProvider((IStructuredContentProvider) inputElement -> {
      if (inputElement instanceof TreeSet<?>) {
        return ((TreeSet<?>) inputElement).toArray();
      }
      return new Object[0];
    });

    // Selection Listener
    this.mappedDataTabViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent selectionevent) {
        EMRAssignVariantComposite.this.deleteAssignmentAction.setEnabled(true);
      }
    });

    // TableViewer Input data
    setTableViewerInput();

    // Editable state
    if (this.isUpdateCmd && (this.selectedEmrFile != null)) {
      setEditStateByFlag(this.selectedEmrFile.getIsVariant());
    }

    // Invoke TableViewer Column sorters
    this.mappedDataTabViewer.setComparator(this.variantGridSorter);

    // Enable or Disable mapping button based on selected EMR Variants
    toggleMappingButtonBasedOnSelection();
  }

  /**
   * Create checkbox flag for 'valid for Pidc'
   */
  private void createValidForPidcFlag() {
    Button checkForPIDC = new Button(this.formTwo.getBody(), SWT.CHECK);
    checkForPIDC.setText("Valid for complete PIDC for all variants.");
    checkForPIDC.pack();

    // Initial State
    for (Long emrFileId : this.emrFileIds) {
      if (this.wsOutput.getEmrFilesMap().containsKey(emrFileId)) {
        boolean isValidForPidc = !this.wsOutput.getEmrFilesMap().get(emrFileId).getIsVariant();
        checkForPIDC.setSelection(isValidForPidc);
      }
    }

    // On Selection
    checkForPIDC.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent selectionevent) {
        boolean isValidForPidc = ((Button) selectionevent.getSource()).getSelection();
        EmrFileServiceClient client = new EmrFileServiceClient();
        EmrFile newData = EMRAssignVariantComposite.this.selectedEmrFile.clone();
        newData.setIsVariant(!isValidForPidc);
        try {
          EMRAssignVariantComposite.this.selectedEmrFile = client.updateEmrFileDetails(newData);
          setEditStateByFlag(EMRAssignVariantComposite.this.selectedEmrFile.getIsVariant());
          if (!isValidForPidc) {
            // if valid for all variants is unchecked , refresh the button
            toggleMappingButtonBasedOnSelection();
          }
          else {
            // If the valid for pidc check box is checked, remove the selected mappings and disable the delete
            EMRAssignVariantComposite.this.mappedDataTabViewer.setSelection(StructuredSelection.EMPTY);
            EMRAssignVariantComposite.this.deleteAssignmentAction.setEnabled(false);
          }
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
        }
      }
    });
  }

  /**
   * @param isValidForPidc flag
   */
  public void setEditStateByFlag(final boolean isValidForPidc) {
    if (CommonUtils.isNotEmpty(this.gridTableDataSet)) {
      EMRAssignVariantComposite.this.mappedDataTabViewer.getGrid().setEnabled(isValidForPidc);
    }
    EMRAssignVariantComposite.this.assignVariantBtn.setEnabled(isValidForPidc);
  }

  /**
   * This method creates Section ToolBar actions
   */
  private void createTableToolbarAction() {
    final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
    final ToolBar toolbar = toolBarManager.createControl(this.sectionTwo);
    // Delete button
    adddeleteAssignmentAction(toolBarManager);
    toolBarManager.update(true);
    this.sectionTwo.setTextClient(toolbar);
  }


  /**
   * To elimate duplicate entries for assigning mapping
   *
   * @param emrFileId
   * @param emsId
   * @param variantId
   * @return
   */
  private String getUniqueMappingId(final long emrFileId, final long emsId, final long variantId,
      final long emrVariantId) {
    return emrFileId + DELIMITER + emsId + DELIMITER + variantId + DELIMITER + emrVariantId;
  }

  /**
   * create columns for table
   */
  private void createGridTableColumns() {
    this.variantGridSorter = new EmrVariantTableSorter();
    final GridViewerColumn sheetNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.mappedDataTabViewer, "Sheet Name", 650);

    sheetNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        sheetNameColumn.getColumn(), ApicUiConstants.COLUMN_INDEX_0, this.variantGridSorter, this.mappedDataTabViewer));

    sheetNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        final EmrGridEntryData var = (EmrGridEntryData) element;
        return var.getSheetName();
      }
    });

    final GridViewerColumn variantColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.mappedDataTabViewer, "Variant", 300);

    variantColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        variantColumn.getColumn(), ApicUiConstants.COLUMN_INDEX_1, this.variantGridSorter, this.mappedDataTabViewer));

    variantColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        final EmrGridEntryData var = (EmrGridEntryData) element;
        return var.getVariant();
      }
    });
  }

  /**
   * @param toolBarManager
   */
  private void adddeleteAssignmentAction(final ToolBarManager toolBarManager) {

    this.deleteAssignmentAction = new Action("Delete Assignments", SWT.NONE) {

      @Override
      public void run() {
        Set<EmrPidcVariant> fileVariantEms = new HashSet<>();
        EmrFileVariantServiceClient client = new EmrFileVariantServiceClient();
        try {
          IStructuredSelection tableSelection =
              EMRAssignVariantComposite.this.mappedDataTabViewer.getStructuredSelection();
          for (Object selected : tableSelection.toArray()) {
            if (selected instanceof EmrGridEntryData) {
              EmrGridEntryData selectedData = (EmrGridEntryData) selected;
              fileVariantEms.add(selectedData.getMappingData());
            }
          }
          // Delete mapping WS
          client.deleteEmrFileEmsVariantMapping(fileVariantEms);
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
        }
        // Refresh field data
        loadFieldData();
        setTableViewerInput();

        // Disable the delete button after deleting an assignment
        EMRAssignVariantComposite.this.deleteAssignmentAction.setEnabled(false);
        // Refresh the Mapping button after delete
        toggleMappingButtonBasedOnSelection();
      }
    };
    this.deleteAssignmentAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));
    this.deleteAssignmentAction.setEnabled(false);
    toolBarManager.add(this.deleteAssignmentAction);
  }


  /**
   * Refresh Grid Table
   */
  private void setTableViewerInput() {
    this.mappedDataTabViewer.setInput(this.gridTableDataSet);
    this.mappedDataTabViewer.refresh();
  }

  /**
   * @return list
   */
  public List getSheetNamesList() {
    return this.fileEmsList;
  }


  /**
   * @return the emrFileIds
   */
  public Set<Long> getEmrFileIds() {
    return this.emrFileIds;
  }

  /*
   * Grid Table row object
   */
  private class EmrGridEntryData implements Comparable<EmrGridEntryData> {

    private final String sheetName;
    private final String variant;
    private final EmrPidcVariant mappingData;

    EmrGridEntryData(final String sheetName, final String variant, final EmrPidcVariant mappingData) {
      this.sheetName = sheetName;
      this.variant = variant;
      this.mappingData = mappingData;
    }

    /**
     * @return the sheetName
     */
    public String getSheetName() {
      return this.sheetName;
    }

    /**
     * @return the variant
     */
    public String getVariant() {
      return this.variant;
    }

    /**
     * @return the mappingData
     */
    public EmrPidcVariant getMappingData() {
      return this.mappingData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final EmrGridEntryData obj) {
      return ApicUtil.compare(this.sheetName + this.variant, obj.sheetName + obj.variant);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
      if (obj == this) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      return (obj.getClass() == this.getClass()) &&
          ModelUtil.isEqual(getSheetName(), ((EmrGridEntryData) obj).getSheetName()) &&
          ModelUtil.isEqual(getVariant(), ((EmrGridEntryData) obj).getVariant());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
      return ModelUtil.generateHashCode(getSheetName(), getVariant());
    }
  }

  /**
   * Refresh composite
   */
  public void refreshData() {
    loadFieldData();
  }

  /*
   * TableSorter
   */
  class EmrVariantTableSorter extends AbstractViewerSorter {

    /**
     * Index
     */
    private int index;
    /**
     * DESCENDING
     */
    private static final int DESCENDING = 1;
    /**
     * ASCENDING
     */
    private static final int ASCENDING = 0;
    /**
     * direction
     */
    private int direction = DESCENDING;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setColumn(final int index) {
      // set the direction of sorting
      if (index == this.index) {
        this.direction = 1 - this.direction;
      }
      // Ascending order
      else {
        this.index = index;
        this.direction = ASCENDING;
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDirection() {
      return this.direction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(final Viewer viewer, final Object object1, final Object object2) {

      EmrGridEntryData mapping1 = (EmrGridEntryData) object1;
      EmrGridEntryData mapping2 = (EmrGridEntryData) object2;

      int compareResult;

      switch (this.index) {
        case CommonUIConstants.COLUMN_INDEX_0:
          // fileName/Ems
          compareResult = ApicUtil.compare(mapping1.getSheetName(), mapping2.getSheetName());
          break;
        case CommonUIConstants.COLUMN_INDEX_1:
          // pidcVariant
          compareResult = ApicUtil.compare(mapping1.getVariant(), mapping2.getVariant());
          break;
        default:
          compareResult = CommonUIConstants.COLUMN_INDEX_0;
      }
      // If descending order, flip the direction
      if (this.direction == DESCENDING) {
        compareResult = -compareResult;
      }
      return compareResult;
    }
  }
}
