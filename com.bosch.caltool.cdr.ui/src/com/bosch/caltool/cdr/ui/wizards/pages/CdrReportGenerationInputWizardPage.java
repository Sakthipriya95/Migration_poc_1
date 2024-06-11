/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.wizards.pages;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.cdr.ui.dialogs.PidcVariantSelectionDialog;
import com.bosch.caltool.cdr.ui.wizards.CdrReportGenerationWizard;
import com.bosch.caltool.cdr.ui.wizards.CdrReportGenerationWizardData;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * ICDM-1704
 *
 * @author mkl2cob
 */
public class CdrReportGenerationInputWizardPage extends WizardPage {

  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;

  /**
   * Composite instance
   */
  private Composite composite;

  /**
   * Section instance
   */
  private Section section;
  /**
   * code word checkbox instance
   */
  private Button checkValChkBox;
  /**
   * Form instance
   */
  private Form form;

  private final boolean isToCreateRprtForWPRespNode;

  private final PidcVariant selPidcVariant;

  /**
   * @param pageName page title
   * @param selPidcVar selected Pidc Variant
   * @param isToCreateRprtForWPRespNode true if the action is called to generate data review report for WP/Resp Node
   */
  public CdrReportGenerationInputWizardPage(final String pageName, final PidcVariant selPidcVar,
      final boolean isToCreateRprtForWPRespNode) {
    super(pageName);
    setTitle(pageName);
    setDescription("");
    this.selPidcVariant = selPidcVar;
    this.isToCreateRprtForWPRespNode = isToCreateRprtForWPRespNode;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(final Composite parent) {
    initializeDialogUnits(parent);
    GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.heightHint = 350;
    parent.setLayoutData(gridData);
    this.composite = getFormToolkit().createComposite(parent);
    createSection();
    this.composite.setLayout(new GridLayout());
    this.composite.setLayoutData(gridData);
    setControl(this.composite);
  }

  /**
   * create section
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "Data Review Report");
    this.section.setLayout(new GridLayout());
    this.section.getDescriptionControl().setEnabled(false);
    // create form
    createForm();
    // set the client
    this.section.setClient(this.form);
  }

  /**
   * create the form
   */
  private void createForm() {
    this.form = getFormToolkit().createForm(this.section);

    // create the controls for variant selection
    createVariantControls();
    // create the controls for choosing number of reviews
    createNumOfRvwControls();
    LabelUtil.getInstance().createLabel(this.form.getBody(), "");
    // create the control to choose checkvalue or not
    createCheckValControl();
    final GridLayout gridLayout = new GridLayout();
    // 3 columns for the layout
    gridLayout.numColumns = 3;
    gridLayout.verticalSpacing = 15;
    this.form.getBody().setLayout(gridLayout);
    this.form.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());

  }

  /**
   * create check value ui controls
   */
  private void createCheckValControl() {
    LabelUtil.getInstance().createLabel(this.form.getBody(), "Load Check Values ");
    this.checkValChkBox = new Button(this.form.getBody(), SWT.CHECK);
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    this.checkValChkBox.setLayoutData(gridData);
    // add the listener
    this.checkValChkBox.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        CdrReportGenerationWizardData wizardData = ((CdrReportGenerationWizard) getWizard()).getWizardData();
        wizardData.setFetchCheckVal(CdrReportGenerationInputWizardPage.this.checkValChkBox.getSelection());
      }
    });
  }

  /**
   * create the controls for selecting the number of reviews needed for the report
   */
  private void createNumOfRvwControls() {
    LabelUtil.getInstance().createLabel(this.form.getBody(), "Number of reviews ");
    // composite for displaying radio buttons
    Composite lastNRvwsComp = getFormToolkit().createComposite(this.form.getBody());
    GridLayout gridLayoutForComp = new GridLayout();
    gridLayoutForComp.numColumns = 2;

    lastNRvwsComp.setLayout(gridLayoutForComp);
    GridData gridDataForComp = GridDataUtil.getInstance().getGridData();
    gridDataForComp.verticalAlignment = GridData.CENTER;
    lastNRvwsComp.setLayoutData(gridDataForComp);

    // radio button for last review
    Button btnLastReview = new Button(lastNRvwsComp, SWT.RADIO);
    btnLastReview.setText("Last Review");
    btnLastReview.setSelection(true);

    LabelUtil.getInstance().createEmptyLabel(lastNRvwsComp);
    // radio button for last n reviews
    Button btnLastNReviews = new Button(lastNRvwsComp, SWT.RADIO);
    btnLastNReviews.setText("Last n reviews");
    // keep default number as 5
    final Text numOfRvwsTxt = TextUtil.getInstance().createText(lastNRvwsComp, true, "5");
    GridData gridData = new GridData();
    gridData.horizontalAlignment = SWT.RIGHT;
    gridData.widthHint = 20;
    numOfRvwsTxt.setLayoutData(gridData);
    numOfRvwsTxt.setEditable(false);
    btnLastReview.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        // get the wizard data
        CdrReportGenerationWizardData wizardData = ((CdrReportGenerationWizard) getWizard()).getWizardData();
        wizardData.setNumOfReviews(1);// set the number of reviews to one
        wizardData.setLastReview(true);
        numOfRvwsTxt.setEditable(false);
        // enable disable the finish button
        getWizard().getContainer().updateButtons();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent event) {
        // NA

      }
    });
    // add selection listener
    btnLastNReviews.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent selectionEvnt) {
        // get the wizard data
        CdrReportGenerationWizardData wizardData = ((CdrReportGenerationWizard) getWizard()).getWizardData();
        // check if the text can be converted to Integer
        checkifTextEnteredIsInt(numOfRvwsTxt, wizardData);
        wizardData.setLastReview(false);
        numOfRvwsTxt.setEditable(true);
        // enable disable the finish button
        getWizard().getContainer().updateButtons();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent selectionEvent) {
        // NA

      }
    });
    // add modify listener
    numOfRvwsTxt.addModifyListener((final ModifyEvent modifyEvnt) -> {
      // get the wizard data
      CdrReportGenerationWizardData wizardData = ((CdrReportGenerationWizard) getWizard()).getWizardData();
      // check if the text can be converted to Integer
      checkifTextEnteredIsInt(numOfRvwsTxt, wizardData);
    });
  }

  /**
   * create variant controls
   */
  private void createVariantControls() {
    LabelUtil.getInstance().createLabel(this.form.getBody(), "Variant");
    final Text varText = TextUtil.getInstance().createText(this.form.getBody(), false, "");
    GridData txtGridData = new GridData();
    txtGridData.grabExcessHorizontalSpace = true;
    txtGridData.horizontalAlignment = GridData.FILL;
    varText.setLayoutData(txtGridData);
    varText.setEditable(false);

    Button btnVariant = new Button(this.form.getBody(), SWT.NONE);
    btnVariant.setImage(ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON));// image for browse
                                                                                                // button
    final CdrReportGenerationWizardData wizardData = ((CdrReportGenerationWizard) getWizard()).getWizardData();
    btnVariant.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent selectionEvent) {
        // open dialog for selecting variant
        final PidcVariantSelectionDialog variantSelDialog =
            new PidcVariantSelectionDialog(Display.getCurrent().getActiveShell(), wizardData.getPidcA2l().getId());
        variantSelDialog.setMultiSel(true);

        variantSelDialog.open();
        final PidcVariant selectedVariant = variantSelDialog.getSelectedVariant();// variant selected from the dialog
        Set<PidcVariant> selectedPidcVariants = variantSelDialog.getSelectedPidcVariants();
        if (!CommonUtils.isNullOrEmpty(selectedPidcVariants)) {
          // set the variant text and update the generate btn
          StringBuilder builder = new StringBuilder();

          for (PidcVariant pidcVariants : selectedPidcVariants) {
            builder.append(pidcVariants.getName());
            builder.append(",");
          }
          String variantStr = builder.toString();
          varText.setText(variantStr.substring(0, variantStr.length() - 1));
          wizardData.setPidcVaraints(selectedPidcVariants);
          getWizard().getContainer().updateButtons();
        }
        else if (selectedVariant != null) {
          selectedPidcVariants = new HashSet<>();
          varText.setText(selectedVariant.getName());
          selectedPidcVariants.add(selectedVariant);
          wizardData.setPidcVaraints(selectedPidcVariants);
          getWizard().getContainer().updateButtons();
        }
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent selectionEvent) {
        // NA

      }
    });

    btnVariant.setEnabled(false);
    if (CommonUtils.isNotNull(this.selPidcVariant) || this.isToCreateRprtForWPRespNode) {
      // disable the button and the text field if 'Generate Data Review Report' is called from WP node / Resp Node
      varText.setText(this.selPidcVariant.getName());

      Set<PidcVariant> selectedPidcVariants = new HashSet<>();
      selectedPidcVariants.add(this.selPidcVariant);
      wizardData.setPidcVaraints(selectedPidcVariants);
    }
    else if (wizardData.variantsAvailable()) {
      // disable the button and the text field if the variants are not available
      btnVariant.setEnabled(true);
      varText.setEnabled(true);
    }

  }

  /**
   * Validation for int
   *
   * @param numOfRvwsTxt Text
   * @param wizardData GenerateRvwReportWizardData
   */
  private void checkifTextEnteredIsInt(final Text numOfRvwsTxt, final CdrReportGenerationWizardData wizardData) {
    try {
      // try to parse the integer
      int parsedInteger = Integer.parseInt(numOfRvwsTxt.getText().trim());
      wizardData.setNumOfReviews(parsedInteger);
    }
    catch (NumberFormatException numException) {
      // set the number of reviews as 0
      wizardData.setNumOfReviews(0);
    }
    getWizard().getContainer().updateButtons();
  }

  /**
   * This method initializes formToolkit
   *
   * @return org.eclipse.ui.forms.widgets.FormToolkit
   */
  private FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      // create the form toolkit if its not available
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }


}
