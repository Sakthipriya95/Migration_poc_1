/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.UnmapA2LResponse;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2lFileExt;
import com.bosch.caltool.icdm.ws.rest.client.a2l.UnmapA2LServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;

/**
 * @author hnu1cob
 */
public class UnmapA2lDetailsComposite extends Composite {

  Composite parentComp;
  FormToolkit formToolkit;
  private PidcA2lFileExt selA2lFile;
  private GridTableViewer revDetailsTableViewer;
  private GridTableViewer a2lDefnTableViewer;
  private Text a2lNameTxt;
  private Text pidcVersTxt;

  private final boolean adminPage;

  private UnmapA2LResponse unmapA2LResponse;


  /**
   * @param parent , the parent composite
   * @param formToolkit , FormToolkit
   * @param selA2LFile2 ,PidcA2lFileExt of selected A2l file
   * @param adminPage true if composite is created from Admin page
   * @param unmapA2lResponse UnmapA2LResponse
   */
  public UnmapA2lDetailsComposite(final Composite parent, final FormToolkit formToolkit,
      final PidcA2lFileExt selA2LFile2, final boolean adminPage, final UnmapA2LResponse unmapA2lResponse) {
    super(parent, SWT.NONE);
    this.parentComp = parent;
    this.formToolkit = formToolkit;
    this.adminPage = adminPage;
    this.unmapA2LResponse = unmapA2lResponse;
    setSelA2lFile(selA2LFile2 != null ? selA2LFile2 : null);
    // Create section to display A2L Details
    createA2lDetailsSection();
    // Create Tables to display attached/related items to A2L
    createTableSection();
  }

  /**
   * @param parent
   */
  private void createA2lDetailsSection() {
    GridData gridData = GridDataUtil.getInstance().getGridData();

    Section a2lDetailsSection =
        this.formToolkit.createSection(this.parentComp, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    a2lDetailsSection.setText("A2L File Details");
    a2lDetailsSection.setExpanded(true);
    a2lDetailsSection.getDescriptionControl().setEnabled(false);
    a2lDetailsSection.setLayout(new GridLayout());
    a2lDetailsSection.setLayoutData(gridData);

    Composite a2lDetailsComp = this.formToolkit.createComposite(a2lDetailsSection);
    a2lDetailsComp.setLayout(new GridLayout(2, false));
    a2lDetailsComp.setLayoutData(gridData);

    GridData textGridData = GridDataUtil.getInstance().getTextGridData();
    LabelUtil.getInstance().createLabel(a2lDetailsComp, "A2L File");

    this.a2lNameTxt = new Text(a2lDetailsComp, SWT.BORDER);
    this.a2lNameTxt.setText(getSelA2lFile() != null ? getSelA2lFile().getA2lFile().getFilename() : "");
    this.a2lNameTxt.setLayoutData(textGridData);
    this.a2lNameTxt.setEditable(false);

    LabelUtil.getInstance().createLabel(a2lDetailsComp, "Unmap from PIDC Version");

    this.pidcVersTxt = new Text(a2lDetailsComp, SWT.BORDER);
    this.pidcVersTxt.setText(getSelA2lFile() != null ? getSelA2lFile().getPidcVersion().getName() : "");
    this.pidcVersTxt.setLayoutData(textGridData);
    this.pidcVersTxt.setEditable(false);

    a2lDetailsSection.setClient(a2lDetailsComp);

  }

  /**
  *
  */
  private void createTableSection() {
    GridData gridData = GridDataUtil.getInstance().getGridData();
    GridLayout gridLayout = new GridLayout();

    Section section =
        this.formToolkit.createSection(this.parentComp, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    section.setText("Attached items/Related items");
    section.getDescriptionControl().setEnabled(false);
    section.setLayout(gridLayout);
    section.setExpanded(true);
    section.setLayoutData(gridData);

    Composite tableComp = this.formToolkit.createComposite(section);
    tableComp.setLayout(gridLayout);
    tableComp.setLayoutData(gridData);

    createReviewAndCdfxTable(tableComp);
    createA2lDefnTable(tableComp);

    try {
      fetchAndSetInputForTables();
    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), Activator.PLUGIN_ID);
    }
    section.setClient(tableComp);
  }

  /**
   * Fetch the input for table from service and set input
   *
   * @throws ApicWebServiceException Exception while fetching data
   */
  public void fetchAndSetInputForTables() throws ApicWebServiceException {
    if (getSelA2lFile() != null) {
      if (this.adminPage) {
        this.unmapA2LResponse = new UnmapA2LServiceClient().getRelatedDbEntries(getSelA2lFile().getPidcA2l().getId());
      }
      this.a2lNameTxt.setText(this.unmapA2LResponse.getA2lFileName());
      this.pidcVersTxt.setText(this.unmapA2LResponse.getPidcVersName());
      // create Map of Review related items and its count
      Map<String, Integer> revItemsAndCountMap = new LinkedHashMap<>();
      revItemsAndCountMap.put(MODEL_TYPE.CDR_RESULT.getTypeName(), this.unmapA2LResponse.getRvwResCount());
      // revItemsAndCountMap.put("Questionnaire responses", this.unmapA2LResponse.getQuesRespCount());

      // set the input for the table
      this.revDetailsTableViewer.setInput(revItemsAndCountMap.entrySet());
      this.revDetailsTableViewer.refresh();
      // create Map of A2L Definition related items and its count
      Map<String, Integer> a2lDefItemsAndCountMap = new LinkedHashMap<>();
      a2lDefItemsAndCountMap.put(MODEL_TYPE.A2L_WP_DEFN_VERSION.getTypeName(), this.unmapA2LResponse.getDefVersCount());
      a2lDefItemsAndCountMap.put(MODEL_TYPE.A2L_VARIANT_GROUP.getTypeName(), this.unmapA2LResponse.getVarGrpCount());
      a2lDefItemsAndCountMap.put(MODEL_TYPE.A2L_WP_RESPONSIBILITY.getTypeName(),
          this.unmapA2LResponse.getWpRespCombinationsCount());
      a2lDefItemsAndCountMap.put(MODEL_TYPE.A2L_WP_PARAM_MAPPING.getTypeName(),
          this.unmapA2LResponse.getParamMappingCount());
//        a2lDefItemsAndCountMap.put("A2L workpackages ( belonging only to this A2L file )",
//            this.unmapA2LResponse.getA2lWrkPckgCount());
//        a2lDefItemsAndCountMap.put("A2L responsibilities ( belonging only to this A2L file )",
//            this.unmapA2LResponse.getA2lRespCount());

      // set the input for table viewer
      this.a2lDefnTableViewer.setInput(a2lDefItemsAndCountMap.entrySet());
      this.a2lDefnTableViewer.refresh();

    }
  }

  /**
   * clear all fields
   */
  public void clearAllFields() {
    this.a2lNameTxt.setText("");
    this.pidcVersTxt.setText("");
    this.revDetailsTableViewer.setInput(null);
    this.revDetailsTableViewer.refresh();
    this.a2lDefnTableViewer.setInput(null);
    this.revDetailsTableViewer.refresh();
  }

  /**
   * @param tableComp
   */
  private void createA2lDefnTable(final Composite tableComp) {

    this.a2lDefnTableViewer = GridTableViewerUtil.getInstance().createGridTableViewer(tableComp,
        SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, GridDataUtil.getInstance().getGridData());
    // set the content provider for the table
    this.a2lDefnTableViewer.setContentProvider(ArrayContentProvider.getInstance());

    GridData gridData = GridDataUtil.getInstance().createGridData();
    gridData.minimumHeight = 120;
    this.a2lDefnTableViewer.getGrid().setLayoutData(gridData);

    final GridViewerColumn itemsColumn = new GridViewerColumn(this.a2lDefnTableViewer, SWT.NONE);
    itemsColumn.getColumn().setText("A2L Definitions");
    itemsColumn.getColumn().setWidth(350);
    itemsColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof Entry<?, ?>) {
          Entry<String, Integer> entry = (Entry<String, Integer>) element;
          return entry.getKey();
        }
        return "";
      }
    });

    final GridViewerColumn countColumn = new GridViewerColumn(this.a2lDefnTableViewer, SWT.NONE);
    countColumn.getColumn().setText("Count");
    countColumn.getColumn().setWidth(100);
    countColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof Entry<?, ?>) {
          Entry<String, Integer> entry = (Entry<String, Integer>) element;
          return entry.getValue().toString();
        }
        return "";
      }
    });

  }

  /**
   * @param tableComp
   */
  private void createReviewAndCdfxTable(final Composite tableComp) {
    this.revDetailsTableViewer = GridTableViewerUtil.getInstance().createGridTableViewer(tableComp,
        SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, GridDataUtil.getInstance().getGridData());
    // set the content provider for the table
    this.revDetailsTableViewer.setContentProvider(ArrayContentProvider.getInstance());

    GridData gridData = GridDataUtil.getInstance().createGridData();
    gridData.minimumHeight = 50;
    this.revDetailsTableViewer.getGrid().setLayoutData(gridData);

    final GridViewerColumn itemsColumn = new GridViewerColumn(this.revDetailsTableViewer, SWT.NONE);
    itemsColumn.getColumn().setText("Review Result");
    itemsColumn.getColumn().setWidth(350);
    itemsColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        if (element instanceof Entry<?, ?>) {
          Entry<String, Integer> entry = (Entry<String, Integer>) element;
          return entry.getKey();
        }
        return "";
      }
    });

    final GridViewerColumn countColumn = new GridViewerColumn(this.revDetailsTableViewer, SWT.NONE);
    countColumn.getColumn().setText("Count");
    countColumn.getColumn().setWidth(100);
    countColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof Entry<?, ?>) {
          Entry<String, Integer> entry = (Entry<String, Integer>) element;
          return entry.getValue().toString();
        }
        return "";
      }
    });
  }

  /**
   * @return the selA2lFile
   */
  public PidcA2lFileExt getSelA2lFile() {
    return this.selA2lFile;
  }

  /**
   * @param selA2lFile the selA2lFile to set
   */
  public void setSelA2lFile(final PidcA2lFileExt selA2lFile) {
    this.selA2lFile = selA2lFile;
  }


}
