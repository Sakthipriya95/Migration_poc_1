/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.wizards;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.actions.EMRReloadBtnAction;
import com.bosch.caltool.apic.ui.sorter.EMRErrorTableSorter;
import com.bosch.caltool.apic.ui.table.filters.EMRErrorFilter;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.emr.EMRFileUploadResponse;
import com.bosch.caltool.icdm.model.emr.EmrFile;
import com.bosch.caltool.icdm.model.emr.EmrUploadError;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * @author mkl2cob
 */
public class EMRResultComposite {

  /**
   * upload with errors image
   */
  private static final ImageKeys UPLOAD_FAILURE_IMAGE = ImageKeys.ERROR_36X36;
  /**
   * upload success image
   */
  private static final ImageKeys UPLOAD_SUCCESS_IMAGE = ImageKeys.BIG_TICK_36X36;
  /**
   * upload success text
   */
  private static final String UPLOAD_SUCCESS_TEXT = "The files have been added successfully";
  /**
   * upload with errors text
   */
  private static final String UPLOAD_WITH_ERRORS_TEXT =
      "There has been errors during upload.The file has been saved in database nevertheless.\n Mail has been sent to iCDM Hotline.We will take care of this and inform you as soon as possible.";
  /**
   * Composite
   */
  private Composite composite;
  /**
   * FormToolkit
   */
  private FormToolkit formToolkit;
  /**
   * import result section
   */
  private Section sectionOne;

  /**
   * Form
   */
  private Form formOne;

  /**
   * Filter text instance
   */
  private Text filterTxt;
  /**
   * true if result is success
   */
  private final boolean resultSuccess;
  /**
   * errors table viewer
   */
  private GridTableViewer errorsTabViewer;

  /**
   * result image label
   */
  private Label resultImage;
  /**
   * result text label
   */
  private Label resultText;
  /**
   * true if the composite is used for reload
   */
  private final boolean reload;
  /**
   * reload excel sheet button
   */
  private Button reloadButton;
  /**
   * EMRErrorTableSorter
   */
  private EMRErrorTableSorter errorTableSorter;

  /**
   * EMRErrorFilter
   */
  private EMRErrorFilter errorFilter;
  /**
   * emr file id
   */
  private final Long emrFileId;


  /**
   * Constructor
   *
   * @param result boolean
   * @param reload boolean
   * @param emrFileId Long
   */
  public EMRResultComposite(final boolean result, final boolean reload, final Long emrFileId) {
    this.resultSuccess = result;
    this.reload = reload;
    this.emrFileId = emrFileId;
  }

  /**
   * @param workArea Composite
   * @param formToolkit FormToolkit
   */
  public void createComposite(final Composite workArea, final FormToolkit formToolkit) {
    this.formToolkit = formToolkit;
    GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite = formToolkit.createComposite(workArea);
    this.composite.setLayout(new GridLayout());
    this.composite.setLayoutData(gridData);

    createFirstSection();
    this.composite.layout();
  }

  /**
   * Add sorter for the table columns
   */
  private void invokeColumnSorter(final AbstractViewerSorter sorter) {
    this.errorsTabViewer.setComparator(sorter);
  }

  /**
   * This method initializes section
   */
  private void createFirstSection() {
    this.sectionOne = SectionUtil.getInstance().createSection(this.composite, this.formToolkit,
        GridDataUtil.getInstance().getGridData(), "Result of Excel Upload Operation to Database");
    this.sectionOne.getDescriptionControl().setEnabled(false);
    createFormOne();
    this.sectionOne.setClient(this.formOne);
  }

  /**
   * This method initializes form
   */
  private void createFormOne() {


    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    this.formOne = this.formToolkit.createForm(this.sectionOne);
    this.formOne.getBody().setLayout(gridLayout);


    if (this.resultSuccess) {
      // if the result is success (without errors)
      this.resultImage = LabelUtil.getInstance().createLabel(this.formOne.getBody(), "");
      this.resultImage.setImage(ImageManager.getInstance().getRegisteredImage(UPLOAD_SUCCESS_IMAGE));
      this.resultText = LabelUtil.getInstance().createLabel(this.formOne.getBody(), UPLOAD_SUCCESS_TEXT);
    }
    else {
      // In case of errors
      this.resultImage = LabelUtil.getInstance().createLabel(this.formOne.getBody(), "");
      this.resultImage.setImage(ImageManager.getInstance().getRegisteredImage(UPLOAD_FAILURE_IMAGE));
      this.resultText = LabelUtil.getInstance().createLabel(this.formOne.getBody(), UPLOAD_WITH_ERRORS_TEXT);
      this.errorFilter = new EMRErrorFilter();
      // fiter text
      GridData textGridData = GridDataUtil.getInstance().getTextGridData();
      textGridData.horizontalSpan = 2;
      this.filterTxt = TextUtil.getInstance().createFilterText(this.formToolkit, this.formOne.getBody(), textGridData,
          Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
      createErrorsTable();
      // Add error table type filter
      this.errorsTabViewer.addFilter(this.errorFilter);
      addModifyListenerForFilterTxt();

      if (this.reload) {
        // if there is reload functionality
        EMRReloadBtnAction reloadButtonAction = new EMRReloadBtnAction(this.formOne.getBody(), SWT.NONE, this);
        this.reloadButton = reloadButtonAction.getButton();
      }
    }
  }

  /**
   * @param reloadEmrFile EMRFileUploadResponse
   */
  public void changeStatus(final EMRFileUploadResponse reloadEmrFile) {
    if (CommonUtils.isNotEmpty(reloadEmrFile.getEmrFileErrorMap())) {
      // if there are errors
      setErrorsInput(reloadEmrFile);
    }
    else {
      // if there are no errors
      showSuccessControls();
    }
  }

  /**
   * This method creates filter text
   */
  private void addModifyListenerForFilterTxt() {
    this.filterTxt.addModifyListener(new ModifyListener() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void modifyText(final ModifyEvent event) {
        final String text = EMRResultComposite.this.filterTxt.getText().trim();
        EMRResultComposite.this.errorFilter.setFilterText(text);
        EMRResultComposite.this.errorsTabViewer.refresh();
      }
    });
  }

  /**
   * create error table viewer
   */
  private void createErrorsTable() {

    GridData gridData = GridDataUtil.getInstance().getGridData();

    gridData.horizontalSpan = 2;
    gridData.heightHint = 300;

    this.errorTableSorter = new EMRErrorTableSorter();
    this.errorsTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.formOne.getBody(),
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL, gridData);


    createTabColumns();

    this.errorsTabViewer.setContentProvider(new IStructuredContentProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void dispose() {
        // Not applicable
      }


      /**
       * Sorted elements
       * <p>
       * {@inheritDoc}
       */
      @Override
      public Object[] getElements(final Object inputElement) {
        if (inputElement instanceof EMRFileUploadResponse) {
          // get response object
          EMRFileUploadResponse responseObj = (EMRFileUploadResponse) inputElement;
          Map<Long, EmrFile> emrFileMap = responseObj.getEmrFileMap();
          List<Entry<EmrFile, EmrUploadError>> inputList = new ArrayList<Map.Entry<EmrFile, EmrUploadError>>();
          for (Entry<Long, List<EmrUploadError>> iterable_element : responseObj.getEmrFileErrorMap().entrySet()) {
            for (EmrUploadError entry : iterable_element.getValue()) {
              inputList.add(new EMRErrorTableEntry(emrFileMap.get(iterable_element.getKey()), entry));
            }
          }
          return inputList.toArray();
        }
        return null;
      }

    });


  }

  /**
   * create table columns
   */
  private void createTabColumns() {
    createFileNameCol();
    createRowCol();
    createCategoryCol();
    createErrorDataCol();
    createErrorMsgCol();

  }

  /**
   * create error data column
   */
  private void createErrorDataCol() {
    final GridViewerColumn errorColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.errorsTabViewer, "Error Data", 300);

    errorColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {

        String result = "";
        if (element instanceof Entry<?, ?>) {
          Entry<EmrFile, EmrUploadError> errorEntry = (Entry<EmrFile, EmrUploadError>) element;
          result = errorEntry.getValue().getErrorData();
        }
        return result;
      }


    });

    // Add column selection listener
    errorColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(errorColumn.getColumn(), 3, this.errorTableSorter, this.errorsTabViewer));

  }

  /**
   * create error message column
   */
  private void createErrorMsgCol() {

    final GridViewerColumn errorColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.errorsTabViewer, "Error Message", 300);

    errorColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {

        String result = "";
        if (element instanceof Entry<?, ?>) {
          Entry<EmrFile, EmrUploadError> errorEntry = (Entry<EmrFile, EmrUploadError>) element;
          result = errorEntry.getValue().getErrorMessage();
        }
        return result;
      }


    });

    // Add column selection listener
    errorColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(errorColumn.getColumn(), 4, this.errorTableSorter, this.errorsTabViewer));

  }

  /**
   * create category column
   */
  private void createCategoryCol() {

    final GridViewerColumn errorColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.errorsTabViewer, "Category", 100);

    errorColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        String result = "";
        if (element instanceof Entry<?, ?>) {
          Entry<EmrFile, EmrUploadError> errorEntry = (Entry<EmrFile, EmrUploadError>) element;
          result = errorEntry.getValue().getErrorCategory();
        }
        return result;
      }


    });

    // Add column selection listener
    errorColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(errorColumn.getColumn(), 2, this.errorTableSorter, this.errorsTabViewer));
  }

  /**
   * create row column
   */
  private void createRowCol() {
    final GridViewerColumn rowColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.errorsTabViewer, "Row", 60);

    rowColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        String result = "";
        if (element instanceof Entry<?, ?>) {
          Entry<EmrFile, EmrUploadError> errorEntry = (Entry<EmrFile, EmrUploadError>) element;
          result = errorEntry.getValue().getRowNumber().toString();
        }
        return result;
      }


    });

    // Add column selection listener
    rowColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(rowColumn.getColumn(), 1, this.errorTableSorter, this.errorsTabViewer));
  }

  /**
   * create file name column
   */
  private void createFileNameCol() {
    final GridViewerColumn fileNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.errorsTabViewer, "File Name", 500);

    fileNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        String result = "";
        if (element instanceof Entry<?, ?>) {
          Entry<EmrFile, EmrUploadError> errorEntry = (Entry<EmrFile, EmrUploadError>) element;
          result = errorEntry.getKey().getName();
        }
        return result;
      }


    });

    // Add column selection listener
    fileNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(fileNameColumn.getColumn(), 0, this.errorTableSorter, this.errorsTabViewer));
  }

  /**
   * @return GridTableViewer
   */
  public GridTableViewer getErrorTable() {
    return this.errorsTabViewer;
  }

  /**
   * @param uploadResponse Map<String, List<EmrUploadError>
   */
  public void setErrorsInput(final EMRFileUploadResponse uploadResponse) {

    this.errorsTabViewer.setInput(uploadResponse);
    // Invoke TableViewer Column sorters
    invokeColumnSorter(this.errorTableSorter);
    this.errorsTabViewer.refresh();

  }


  /**
   * show only success label
   */
  public void showSuccessControls() {
    this.resultImage.setImage(ImageManager.getInstance().getRegisteredImage(UPLOAD_SUCCESS_IMAGE));
    this.resultText.setText(UPLOAD_SUCCESS_TEXT);
    if (null != this.errorsTabViewer) {
      this.errorsTabViewer.getControl().setVisible(false);
    }
    if (null != this.filterTxt) {
      this.filterTxt.setVisible(false);
    }
    if (null != this.reloadButton) {
      // When its for reload dialog
      this.reloadButton.setVisible(false);
    }

  }

  private class EMRErrorTableEntry<K, V> implements Entry<K, V> {

    private final K key;
    private V value;

    /**
     * Constructor
     *
     * @param key
     * @param value
     */
    public EMRErrorTableEntry(final K key, final V value) {
      this.key = key;
      this.value = value;
    }

    @Override
    public K getKey() {
      return this.key;
    }

    @Override
    public V getValue() {
      return this.value;
    }

    @Override
    public V setValue(final V value) {
      V old = this.value;
      this.value = value;
      return old;
    }
  }

  /**
   * @return Long
   */
  public Long getEMRFileId() {

    return this.emrFileId;
  }


}
