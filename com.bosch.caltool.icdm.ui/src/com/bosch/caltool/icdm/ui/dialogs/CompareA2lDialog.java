/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2LDetailsStructureModel;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ui.editors.a2lcomparison.A2LCompareJob;
import com.bosch.caltool.icdm.ui.editors.a2lcomparison.A2lParamComparePage;
import com.bosch.caltool.icdm.ui.sorters.A2lWpDefVersionSorter;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lVariantGroupServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpDefinitionVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;

/**
 * @author apj4cob
 */
public class CompareA2lDialog extends AbstractDialog {

  /** Top composite. */
  private Composite top;

  /** FormToolkit instance. */
  private FormToolkit formToolkit;

  /** Composite instance for the dialog. */
  private Composite a2lComposite;

  /** Section instance. */
  private Section a2lSection;

  /** Form instance. */
  private Form a2lForm;

  /**
   * Compare A2L Button
   */
  private Button compareA2lBtn;
  /**
   * pidc A2l list selected from structure view
   */
  private final List<PidcA2l> selPidcA2lList;
  /**
   * Map of WP Definition Version corresponding to A2l opened in the editor,used to populate wp version combo box for wp
   * version comparision
   */
  private Map<Long, A2lWpDefnVersion> a2lWpDefnVersionMap;

  /**
   * constant
   */
  private static final String DEFAULT = "<DEFAULT>";
  // to maintain selection count for displaying in the title
  private int count;
  private Form a2lForm1;
  // Map of wp version combo box and var group box
  private final Map<ComboViewer, ComboViewer> wpDefnVersnVarComboViewerMap = new HashMap<>();
  // Map of wp version combo box and selected wp definition version
  private final Map<ComboViewer, A2lWpDefnVersion> wpDefVersnComboDataMap = new HashMap<>();

  private A2lParamComparePage a2lParamCompPage;

  /**
   * @param pidcA2l Pidc A2l object
   * @param a2lWpDefnVersMap selected wp
   * @param parentShell Shell
   */
  public CompareA2lDialog(final List<PidcA2l> pidcA2l, final Map<Long, A2lWpDefnVersion> a2lWpDefnVersMap,
      final Shell parentShell) {
    super(parentShell);
    this.selPidcA2lList = pidcA2l;
    this.a2lWpDefnVersionMap = a2lWpDefnVersMap;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Compare A2l File");
    super.configureShell(newShell);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.compareA2lBtn = createButton(parent, IDialogConstants.OK_ID, "Compare", true);
    this.compareA2lBtn.setEnabled(validateInputFields());
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }


  /**
   * Creates the dialog's contents
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set the title
    setTitle("Compare A2l File");
    // Set the description
    setMessage("Compare A2L File with Work Package information", IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    // create composite
    createComposite();
    parent.layout(true, true);
    return this.top;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(SWT.CLOSE | SWT.APPLICATION_MODAL | SWT.BORDER | SWT.TITLE | SWT.RESIZE | SWT.MAX);
  }


  /**
   *
   */
  private void createComposite() {
    GridLayout gridLayout = new GridLayout();
    gridLayout.makeColumnsEqualWidth = true;
    this.a2lComposite = getFormToolkit().createComposite(this.top);
    this.a2lComposite.setLayout(gridLayout);
    createSection();
    this.a2lComposite.setLayoutData(GridDataUtil.getInstance().getGridData());
  }


  /**
   *
   */
  private void createSection() {
    this.a2lSection =
        getFormToolkit().createSection(this.a2lComposite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.a2lSection.setExpanded(true);
    this.a2lSection.setText("Input Data to compare A2L File");
    this.a2lSection.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.a2lSection.getDescriptionControl().setEnabled(false);
    createForm();
    this.a2lSection.setClient(this.a2lForm);

  }

  /**
  *
  */
  private Section createSectionOne(final PidcA2l pidcA2l) {
    Section a2lSection1 =
        getFormToolkit().createSection(this.a2lComposite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    a2lSection1.setExpanded(true);
    if (CommonUtils.isNotNull(pidcA2l)) {
      a2lSection1.setText("Selection " + this.count + "-" + pidcA2l.getName());
    }
    else {
      a2lSection1.setText("Selection " + this.count);
    }
    a2lSection1.setLayoutData(GridDataUtil.getInstance().getGridData());
    a2lSection1.getDescriptionControl().setEnabled(false);
    a2lSection1.setClient(createFormOne(a2lSection1));
    return a2lSection1;
  }

  /**
   *
   */
  private Form createFormOne(final Section section) {
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;
    this.a2lForm1 = getFormToolkit().createForm(section);
    this.a2lForm1.getBody().setLayout(gridLayout);
    final GridData txtGrid = GridDataUtil.getInstance().getTextGridData();
    txtGrid.widthHint = 480;
    return this.a2lForm1;
  }


  /**
   *
   */
  private void createForm() {
    GridLayout gridLayout = new GridLayout();
    this.a2lForm = getFormToolkit().createForm(this.a2lSection);
    this.a2lForm.getBody().setLayout(gridLayout);
    // Combo boxes
    if (CommonUtils.isNotNull(this.selPidcA2lList)) {
      this.count = 0;
      for (PidcA2l pidcA2l : this.selPidcA2lList) {
        this.count++;
        createSectionOne(pidcA2l);
        createVarVersCombo(pidcA2l);
      }
    }
    else {
      this.count = 1;
      createSectionOne(null);
      createVarVersCombo(null);
      this.count = 2;
      createSectionOne(null);
      createVarVersCombo(null);
    }

  }


  private Map<Long, A2lWpDefnVersion> doGetWpDefnVersionMap(final PidcA2l pidcA2l) {
    try {
      this.a2lWpDefnVersionMap = new A2lWpDefinitionVersionServiceClient().getWPDefnVersForPidcA2l(pidcA2l.getId());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    return this.a2lWpDefnVersionMap;
  }


  /**
   *
   */
  protected void enableDisableOKBtn() {
    if (null != this.compareA2lBtn) {
      this.compareA2lBtn.setEnabled(validateInputFields());
    }
  }

  /**
   * @return
   */
  private boolean validateInputFields() {
    // loop through combo viewer widgets and check if the combo viewers are filled
    boolean validateFlag = true;
    for (Entry<ComboViewer, ComboViewer> entry : this.wpDefnVersnVarComboViewerMap.entrySet()) {
      ComboViewer wpDefVersnComboViewer = entry.getKey();
      ComboViewer varGrpComboViewer = entry.getValue();
      if (varGrpComboViewer.getCombo().getText().isEmpty() || wpDefVersnComboViewer.getCombo().getText().isEmpty()) {
        validateFlag = false;
        return validateFlag;
      }
    }
    return validateFlag;
  }


  private void createVarVersCombo(final PidcA2l pidcA2l) {

    Composite formBody = this.a2lForm1.getBody();

    getFormToolkit().createLabel(formBody, "WP Definition Version : ");
    ComboViewer wpDefnVersComboViewer = new ComboViewer(formBody, SWT.READ_ONLY);
    GridData gridData1 = new GridData();
    gridData1.horizontalAlignment = GridData.FILL;
    gridData1.grabExcessHorizontalSpace = true;
    wpDefnVersComboViewer.getCombo().setLayoutData(gridData1);
    getFormToolkit().createLabel(this.a2lForm1.getBody(), "");

    getFormToolkit().createLabel(formBody, "Variant Group : ");
    ComboViewer variantGrpComboViewer = new ComboViewer(formBody, SWT.READ_ONLY);
    GridData gridData2 = new GridData();
    gridData2.horizontalAlignment = GridData.FILL;
    gridData2.grabExcessHorizontalSpace = true;
    variantGrpComboViewer.getCombo().setLayoutData(gridData2);
    getFormToolkit().createLabel(this.a2lForm1.getBody(), "");
    this.wpDefnVersnVarComboViewerMap.put(wpDefnVersComboViewer, variantGrpComboViewer);
    fillWPDefnVersionCombo(pidcA2l, wpDefnVersComboViewer, variantGrpComboViewer);
  }

  /**
   *
   */
  private void addPidcLevelA2lVariantGroup(final ComboViewer variantGrpComboViewer) {
    A2lVariantGroup defaultVarGrp = new A2lVariantGroup();
    defaultVarGrp.setId(null);
    defaultVarGrp.setName(DEFAULT);
    variantGrpComboViewer.getCombo().add(defaultVarGrp.getName(), 0);
    variantGrpComboViewer.getCombo().setData(Integer.toString(0), defaultVarGrp);
    variantGrpComboViewer.getCombo().select(0);

  }


  /**
   *
   */
  private void fillWPDefnVersionCombo(final PidcA2l pidcA2l, final ComboViewer wpDefnVersComboViewer,
      final ComboViewer variantGrpComboViewer) {
    wpDefnVersComboViewer.setContentProvider(ArrayContentProvider.getInstance());
    wpDefnVersComboViewer.setLabelProvider(new LabelProvider() {

      @Override
      public String getText(final Object element) {
        String wpDefnVers = "";
        if (element instanceof A2lWpDefnVersion) {
          A2lWpDefnVersion a2lWpDefinitionVersion = (A2lWpDefnVersion) element;
          if (a2lWpDefinitionVersion.isActive()) {
            wpDefnVers = "(Active) ";
          }
          wpDefnVers += a2lWpDefinitionVersion.getName();
        }
        return wpDefnVers;
      }

    });
    Map<Long, A2lWpDefnVersion> doGetWpDefnVersionMap;
    if (pidcA2l != null) {
      doGetWpDefnVersionMap = doGetWpDefnVersionMap(pidcA2l);
    }
    else {
      doGetWpDefnVersionMap = this.a2lWpDefnVersionMap;
    }
    A2lWpDefVersionSorter sorter = new A2lWpDefVersionSorter();
    List<A2lWpDefnVersion> a2lwpDefVerList = new ArrayList<>();
    if (doGetWpDefnVersionMap != null) {
      a2lwpDefVerList.addAll(doGetWpDefnVersionMap.values());
      Collections.sort(a2lwpDefVerList, sorter);
      wpDefnVersComboViewer.setInput(a2lwpDefVerList);
    }

    if (null != this.selPidcA2lList) {
      for (A2lWpDefnVersion a2lWpDefnVersion : a2lwpDefVerList) {
        if (a2lWpDefnVersion.isActive()) {
          setDefnversSelection(a2lWpDefnVersion, wpDefnVersComboViewer, variantGrpComboViewer);
          this.wpDefVersnComboDataMap.put(wpDefnVersComboViewer, a2lWpDefnVersion);
          break;
        }
      }
    }
    addWpDefnVersSelectionListener(wpDefnVersComboViewer, variantGrpComboViewer);
  }


  /**
   * @param a2lWpDefnVersion
   */
  private void setDefnversSelection(final A2lWpDefnVersion a2lWpDefnVersion, final ComboViewer wpDefnVersComboViewer,
      final ComboViewer variantGrpComboViewer) {
    final ISelection selection = new StructuredSelection(a2lWpDefnVersion);
    wpDefnVersComboViewer.setSelection(selection);
    fillVarGrpCombo(a2lWpDefnVersion, variantGrpComboViewer);
  }


  /**
   * @param a2lWpDefnVersion
   */
  private void fillVarGrpCombo(final A2lWpDefnVersion a2lWpDefnVersion, final ComboViewer variantGrpComboViewer) {
    variantGrpComboViewer.getCombo().removeAll();
    addPidcLevelA2lVariantGroup(variantGrpComboViewer);
    try {
      A2LDetailsStructureModel a2lDetailsStructureModel =
          new A2lVariantGroupServiceClient().getA2lDetailsStructureData(a2lWpDefnVersion.getId());
      Map<Long, A2lVariantGroup> a2lVariantGroupMap = a2lDetailsStructureModel.getA2lVariantGrpMap();
      int index = 1;
      for (Entry<Long, A2lVariantGroup> varGrpMap : a2lVariantGroupMap.entrySet()) {
        variantGrpComboViewer.getCombo().add(varGrpMap.getValue().getName(), index);
        variantGrpComboViewer.getCombo().setData(Integer.toString(index), varGrpMap.getValue());
        index++;
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    addVarGrpSelectionListener(variantGrpComboViewer);
  }


  /**
   *
   */
  private void addVarGrpSelectionListener(final ComboViewer variantGrpComboViewer) {
    variantGrpComboViewer.addSelectionChangedListener(selectionChangedEvent -> {
      enableDisableOKBtn();
    });
  }


  /**
   *
   */
  private void addWpDefnVersSelectionListener(final ComboViewer wpDefnVersComboViewer,
      final ComboViewer variantGrpComboViewer) {
    wpDefnVersComboViewer.addSelectionChangedListener(selectionChangedEvent -> {
      IStructuredSelection selection = (IStructuredSelection) selectionChangedEvent.getSelection();
      A2lWpDefnVersion currentSelection = (A2lWpDefnVersion) selection.getFirstElement();
      fillVarGrpCombo(currentSelection, variantGrpComboViewer);
      this.wpDefVersnComboDataMap.put(wpDefnVersComboViewer, currentSelection);
      enableDisableOKBtn();
    });
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    Map<A2lWpDefnVersion, Set<A2lVariantGroup>> selWPDefVersnVarSetMap = new HashMap<>();
    for (Entry<ComboViewer, ComboViewer> entry : this.wpDefnVersnVarComboViewerMap.entrySet()) {
      Set<A2lVariantGroup> selA2lVariantGrpSet;
      ComboViewer wpDefVersnComboViewer = entry.getKey();
      ComboViewer varGrpComboViewer = entry.getValue();
      A2lWpDefnVersion a2lWpDefVersn = this.wpDefVersnComboDataMap.get(wpDefVersnComboViewer);
      A2lVariantGroup a2lVarGrp = (A2lVariantGroup) varGrpComboViewer.getCombo()
          .getData(Integer.toString(varGrpComboViewer.getCombo().getSelectionIndex()));
      if (selWPDefVersnVarSetMap.containsKey(a2lWpDefVersn)) {
        selA2lVariantGrpSet = selWPDefVersnVarSetMap.get(a2lWpDefVersn);
        if (selA2lVariantGrpSet.contains(a2lVarGrp)) {
          CDMLogger.getInstance().warnDialog(
              "Comparison with same Workpackage Definition version/Variant group is not possible", Activator.PLUGIN_ID);
          return;

        }
      }
      else {
        selA2lVariantGrpSet = new HashSet<>();
      }

      if (a2lVarGrp.getName().equals(DEFAULT)) {
        a2lVarGrp = null;
      }
      selA2lVariantGrpSet.add(a2lVarGrp);

      selWPDefVersnVarSetMap.put(a2lWpDefVersn, selA2lVariantGrpSet);
    }
    if (CommonUtils.isNull(this.a2lParamCompPage)) {
      Job job = new A2LCompareJob(selWPDefVersnVarSetMap);
      job.schedule();
    }
    else {
      this.a2lParamCompPage.addToExistingCompareEditor(selWPDefVersnVarSetMap);
    }
    super.okPressed();
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
   * @param a2lParamCompPage A2lParamComparePage
   */
  public void setA2lParamComparePage(final A2lParamComparePage a2lParamCompPage) {
    this.a2lParamCompPage = a2lParamCompPage;

  }
}

