/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.dialogs.QuestionnaireNameSelDialog;
import com.bosch.caltool.cdr.ui.util.CdrUIConstants;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.wp.WorkPackageDivision;
import com.bosch.caltool.icdm.model.wp.WorkPkg;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeValueServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.IUtilityConstants;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.text.TextBoxContentDisplay;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * @author svj7cob
 */
// iCDM-1968
public class QuestionnaireDetailsSection {

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
   * desc length
   */
  private static final int DESC_LEN = 4000;
  /**
   * Button instance for cancel
   */
  Button cancelBtn;
  /**
   * Form instance
   */
  private Form form;
  /**
   * Text for adding comment when a value is edited
   */
  protected Text gerName;
  /**
   * eng name txt
   */
  private Text engNameText;


  /**
   * desc eng txt
   */
  private Text descEngTxt;
  /**
   * desc get text
   */
  private Text descGerTxt;
  /**
   * qnaire name browse button
   */
  private Button nameBrowse;
  /**
   * qnaire division selection combo
   */
  private Combo divsionCombo;

  /**
   * No of columns for quest. details section
   */
  private static final int NO_OF_COL = 3;


  /**
   * @return the divsionCombo
   */
  public Combo getDivsionCombo() {
    return this.divsionCombo;
  }

  /**
   * selected attr value
   */
  private AttributeValue selectedAttrVal;
  /**
   * Selected icdm wp
   */
  private WorkPkg selectedWp;
  /**
   * Key - division name , value - division attrVal obj
   */
  private final ConcurrentMap<String, AttributeValue> attrValues = new ConcurrentHashMap<>();

  private WorkPackageDivision selectedWpDiv;

  /**
   * the height size for the description field
   */
  private static final int HEIGHT_FOR_DESC_FIELD = 40;

  /**
   * if this check button is selected then General ques are not required
   */
  private Button equiGenQuesChkBtn;

  /**
   * if this check button is selected then negative answers in this questionnaire will not allow the WP to be finished
   */
  private Button noNegativeAnswersChkBox;


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
   * creates the composite
   *
   * @param parent the composite
   * @return the composite
   */
  public Composite createComposite(final Composite parent) {
    final GridData gridData = GridDataUtil.getInstance().getGridData();

    this.composite = getFormToolkit().createComposite(parent);
    this.composite.setLayout(new GridLayout());
    this.composite.setLayoutData(gridData);

    createQuestNaireDetailsSection();
    return this.composite;
  }


  /**
   * Initializes the section
   */
  private void createQuestNaireDetailsSection() {
    // section for Questuionnare detail
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "Enter the Questionnaire details");
    // create the form
    createForm();
    this.section.setClient(this.form);
    // iCDM-183
    this.section.getDescriptionControl().setEnabled(false);
  }

  /**
   * Initializes the form
   */
  private void createForm() {

    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = NO_OF_COL;
    this.form = getFormToolkit().createForm(this.section);
    this.form.getBody().setLayout(gridLayout);

    GridData commentGridData = getTextFieldGridData();

    getFormToolkit().createLabel(this.form.getBody(), "Division");
    this.divsionCombo = new Combo(this.form.getBody(), SWT.READ_ONLY);
    this.divsionCombo.setLayoutData(GridDataUtil.getInstance().getGridData());

    this.divsionCombo.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent evnt) {
        int index = QuestionnaireDetailsSection.this.divsionCombo.getSelectionIndex();
        String selVal = QuestionnaireDetailsSection.this.divsionCombo.getItem(index);
        // set the selected attr value based on the division chosen
        QuestionnaireDetailsSection.this.selectedAttrVal = QuestionnaireDetailsSection.this.attrValues.get(selVal);
        // only when division is selected the name browse button has to be enabled

        QuestionnaireDetailsSection.this.nameBrowse
            .setEnabled((null != QuestionnaireDetailsSection.this.selectedAttrVal) &&
                QuestionnaireDetailsSection.this.divsionCombo.isEnabled());
      }
    });

    // get the qnaire config attribute
    Long attrId;
    try {
      attrId = Long.valueOf(new CommonDataBO().getParameterValue(CommonParamKey.ICDM_QNAIRE_CONFIG_ATTR));
      // get the attribute values
      List<AttributeValue> attrValList = getAttributeValueList(attrId);
      // add the attribute values to the division selection combo
      for (AttributeValue attrVal : attrValList) {
        // add in the map for retrieveing the attrVal obj corresponsing to the division name selected
        this.attrValues.put(attrVal.getName(), attrVal);
        this.divsionCombo.add(attrVal.getName());
      }
    }
    catch (NumberFormatException | ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    getFormToolkit().createLabel(this.form.getBody(), "");

    getFormToolkit().createLabel(this.form.getBody(), "Name (English)");
    this.engNameText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.engNameText.setLayoutData(commentGridData);
    ControlDecoration txtValEngDec = new ControlDecoration(this.engNameText, SWT.LEFT | SWT.TOP);
    CdrUIConstants.DECORATOR.showReqdDecoration(txtValEngDec, IUtilityConstants.MANDATORY_MSG);
    // set the questionnaire name feild to non editable
    this.engNameText.setEditable(false);
    this.nameBrowse = new Button(this.form.getBody(), SWT.PUSH);
    this.nameBrowse.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.VARIANT_VALUE_16X16));
    this.nameBrowse.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
    this.nameBrowse.setEnabled(false);
    this.nameBrowse.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        QuestionnaireNameSelDialog dialog = new QuestionnaireNameSelDialog(Display.getCurrent().getActiveShell(),
            QuestionnaireDetailsSection.this, QuestionnaireDetailsSection.this.selectedAttrVal, null);
        dialog.open();
      }
    });


    getFormToolkit().createLabel(this.form.getBody(), "Name (German)");
    this.gerName = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.gerName.setLayoutData(commentGridData);
    this.gerName.setEditable(false);
    getFormToolkit().createLabel(this.form.getBody(), "");

    GridData descGridData = getDescGridData();
    getFormToolkit().createLabel(this.form.getBody(), "Description (English)");
    TextBoxContentDisplay textBoxObj1 = new TextBoxContentDisplay(this.form.getBody(),
        SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, DESC_LEN, descGridData);
    this.descEngTxt = textBoxObj1.getText();
    ControlDecoration descEngDec = new ControlDecoration(this.descEngTxt, SWT.LEFT | SWT.TOP);
    CdrUIConstants.DECORATOR.showReqdDecoration(descEngDec, IUtilityConstants.MANDATORY_MSG);
    getFormToolkit().createLabel(this.form.getBody(), "");

    getFormToolkit().createLabel(this.form.getBody(), "");
    getFormToolkit().createLabel(this.form.getBody(), "");


    getFormToolkit().createLabel(this.form.getBody(), "Description (German)");
    TextBoxContentDisplay textBoxObj2 = new TextBoxContentDisplay(this.form.getBody(),
        SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, DESC_LEN, descGridData);

    this.descGerTxt = textBoxObj2.getText();
    getFormToolkit().createLabel(this.form.getBody(), "");

    getFormToolkit().createLabel(this.form.getBody(), "");
    getFormToolkit().createLabel(this.form.getBody(), "");

    Composite comp = new Composite(this.form.getBody(), SWT.NONE);
    GridData gridData = new GridData();
    gridData.horizontalSpan = 3;
    comp.setLayoutData(gridData);
    comp.setLayout(new GridLayout(2, false));

    setEquiGenQuesChkBtn(new Button(comp, SWT.CHECK));
    getFormToolkit().createLabel(comp, "General Questionnaire not required when adding this questionnaire");
    createSecNoNegativeAnswersCheckBox(comp);

  }
  
  private void createSecNoNegativeAnswersCheckBox(Composite comp) {

    setNoNegativeAnswersChkBox(new Button(comp, SWT.CHECK));
    this.noNegativeAnswersChkBox.setEnabled(true);
    getFormToolkit().createLabel(comp, "No Negative answers allowed");

  }

  /**
   * @param attrId
   * @return
   */
  private List<AttributeValue> getAttributeValueList(final Long attrId) {
    List<AttributeValue> ret = new ArrayList<>();
    try {
      Map<Long, Map<Long, AttributeValue>> result = new AttributeValueServiceClient().getValuesByAttribute(attrId);
      if (result.containsKey(attrId)) {
        ret = result.get(attrId).values().stream().collect(Collectors.toCollection(ArrayList::new));
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e);
    }
    return ret;
  }

  /**
   * @return the text field grid data
   */
  private GridData getTextFieldGridData() {
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.verticalAlignment = GridData.CENTER;
    gridData.grabExcessVerticalSpace = true;
    return gridData;
  }

  /**
   * @return the desc text
   */
  private GridData getDescGridData() {
    GridData descGridData = getTextFieldGridData();
    descGridData.heightHint = HEIGHT_FOR_DESC_FIELD;
    return descGridData;
  }


  /**
   * @return the gerName
   */
  public Text getGerName() {
    return this.gerName;
  }


  /**
   * @return the engNameText
   */
  public Text getEngNameText() {
    return this.engNameText;
  }


  /**
   * @return the descEngTxt
   */
  public Text getDescEngTxt() {
    return this.descEngTxt;
  }


  /**
   * @return the descGerTxt
   */
  public Text getDescGerTxt() {
    return this.descGerTxt;
  }


  /**
   * @return the selectedAttrVal
   */
  public AttributeValue getSelectedAttrVal() {
    return this.selectedAttrVal;
  }


  /**
   * @param selectedAttrVal the selectedAttrVal to set
   */
  public void setSelectedAttrVal(final AttributeValue selectedAttrVal) {
    this.selectedAttrVal = selectedAttrVal;
  }


  /**
   * @return the selectedWp
   */
  public WorkPkg getSelectedWp() {
    return this.selectedWp;
  }


  /**
   * @param selectedWp the selectedWp to set
   */
  public void setSelectedWp(final WorkPkg selectedWp) {
    this.selectedWp = selectedWp;
  }


  /**
   * @param gerName the gerName to set
   */
  public void setGerName(final String gerName) {
    this.gerName.setText(gerName);
  }


  /**
   * @param engNameText the engNameText to set
   */
  public void setEngNameText(final String engNameText) {
    this.engNameText.setText(engNameText);
  }

  /**
   * @param selectedWpDiv WorkPackageDetails
   */
  public void setSelectedWpDiv(final WorkPackageDivision selectedWpDiv) {
    this.selectedWpDiv = selectedWpDiv;
  }

  /**
   * @return WorkPackageDetails
   */
  public WorkPackageDivision getSelectedWpDiv() {
    return this.selectedWpDiv;
  }

  /**
   * @return the equiGenQuesChkBtn
   */
  public Button getEquiGenQuesChkBtn() {
    return this.equiGenQuesChkBtn;
  }

  /**
   * @param equiGenQuesChkBtn the equiGenQuesChkBtn to set
   */
  public void setEquiGenQuesChkBtn(final Button equiGenQuesChkBtn) {
    this.equiGenQuesChkBtn = equiGenQuesChkBtn;
  }
  

  /**
   * @return the noNegativeAnswersChkBtn
   */
  public Button getNoNegativeAnswersChkBox() {
    return noNegativeAnswersChkBox;
  }

  
  /**
   * @param noNegativeAnswersChkBtn the noNegativeAnswersChkBtn to set
   */
  public void setNoNegativeAnswersChkBox(Button noNegativeAnswersChkBtn) {
    this.noNegativeAnswersChkBox = noNegativeAnswersChkBtn;
  }
}
