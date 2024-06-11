/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
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

import com.bosch.caltool.apic.ui.editors.pages.RiskCategoryGroup;
import com.bosch.caltool.apic.ui.editors.pages.RiskEvalNatTableSection;
import com.bosch.caltool.apic.ui.editors.pages.RiskMeasureGroupSection;
import com.bosch.caltool.icdm.client.bo.apic.PidcRMCharacterMapping;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.model.rm.PidcRmDefinition;
import com.bosch.caltool.icdm.model.rm.RmCategoryMeasures;
import com.bosch.rcputils.griddata.GridDataUtil;

/**
 * Dialog to display the detailed risk evaluation of project char This dialog will open when a risk level is selected in
 * the Risk evaluation NAT table
 *
 * @author dja7cob
 */
public class RiskEvaluationPopupDialog extends AbstractDialog {

  /**
   * Risk Evaluation Dialog Title
   */
  private static final String DIALOG_TITLE = "Risk Evaluation and Measures";
  /**
   * Composite instance
   */
  private Composite composite;
  /**
   * Composite instance
   */
  private Composite top;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;
  /**
   * Button instance
   */
  private Button okBtn;
  /**
   * Monetary Risk Pre-SOP section
   */
  private Section section;

  /**
   * Risk Safety form
   */
  private Form form;
  /**
   * selected pidc char mapping
   */
  private final PidcRMCharacterMapping selPidcChar;
  /**
   * nattable section instance
   */
  private final RiskEvalNatTableSection riskEvalNattableSection;
  /**
   * rm category measures map
   */
  private final Map<String, RmCategoryMeasures> rmCatMeasureMap;
  /**
   * selected rm definition
   */
  private final PidcRmDefinition selPidcRmDefinition;

  private final Map<String, RiskCategoryGroup> categoryGroupMap = new HashMap<>();

  /**
   * Constructor
   *
   * @param parentShell Shell
   * @param riskEvalNattableSection Nattable Section
   * @param selPidcChar selected project character
   * @param colIndexMeasureMap risk category map
   * @param selPidcRmDefinition selected PidcRmDefinition in tree viewer
   */
  public RiskEvaluationPopupDialog(final Shell parentShell, final RiskEvalNatTableSection riskEvalNattableSection,
      final PidcRMCharacterMapping selPidcChar, final Map<String, RmCategoryMeasures> colIndexMeasureMap,
      final PidcRmDefinition selPidcRmDefinition) {
    super(parentShell);
    this.riskEvalNattableSection = riskEvalNattableSection;
    this.selPidcChar = selPidcChar;
    this.rmCatMeasureMap = colIndexMeasureMap;
    this.selPidcRmDefinition = selPidcRmDefinition;
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
    setTitle("Risk Evaluation of Project character");
    setMessage(this.selPidcChar.getProjectCharacter());
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText(DIALOG_TITLE + " : " + this.selPidcRmDefinition.getName() + " - PIDC Level");
    newShell.layout(true, true);
    super.configureShell(newShell);
  }

  /**
   * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse .swt.widgets.Composite)
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.okBtn = createButton(parent, IDialogConstants.OK_ID, "Close", true);
    this.okBtn.setEnabled(true);
  }

  /**
   * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets .Composite)
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    // create composite
    createComposite();
    return this.top;
  }

  @Override
  protected boolean isResizable() {
    return true;
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

    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.widthHint = 800;
    gridData.heightHint = 300;
    this.composite = getFormToolkit().createComposite(this.top);
    GridLayout layout = new GridLayout();
    this.composite.setLayout(layout);
    this.composite.setLayoutData(gridData);

    createSection();
  }

  /**
   * @param compositeOverall
   */
  private void createSection() {
    this.section = getFormToolkit().createSection(this.composite,
        Section.DESCRIPTION | ExpandableComposite.TITLE_BAR | ExpandableComposite.CLIENT_INDENT);
    this.section.setText(DIALOG_TITLE);
    GridLayout overallLayout = new GridLayout();
    this.section.setLayout(overallLayout);
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.getDescriptionControl().setEnabled(false);
    createForm();
    this.section.setClient(this.form);
  }

  /**
   *
   */
  private void createForm() {
    this.form = getFormToolkit().createForm(this.section);
    GridLayout overallLayout = new GridLayout();
    this.section.setLayout(overallLayout);
    overallLayout.numColumns = 3;
    overallLayout.makeColumnsEqualWidth = true;
    this.form.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());
    this.form.getBody().setLayout(overallLayout);

    RiskMeasureGroupSection rmGrpcomponent =
        new RiskMeasureGroupSection(this.form, this.rmCatMeasureMap, this.riskEvalNattableSection);
    rmGrpcomponent.createRiskGroups(this.categoryGroupMap);
  }

  /**
   * @return the riskEvalNattableSection
   */
  public RiskEvalNatTableSection getRiskEvalNattableSection() {
    return this.riskEvalNattableSection;
  }

  /**
   * @return the rmCatMeasureMap
   */
  public Map<String, RmCategoryMeasures> getRmCatMeasureMap() {
    return this.rmCatMeasureMap;
  }
}
