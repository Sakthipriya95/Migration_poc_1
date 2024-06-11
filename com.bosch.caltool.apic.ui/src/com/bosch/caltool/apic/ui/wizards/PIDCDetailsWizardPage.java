/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.wizards;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.dialogs.PIDCAliasDefSection;
import com.bosch.caltool.icdm.client.bo.apic.PidcCreationHandler;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.pidc.PidcCreationDetails;
import com.bosch.rcputils.IUtilityConstants;
import com.bosch.rcputils.decorators.Decorators;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.text.TextBoxContentDisplay;
import com.bosch.rcputils.ui.forms.SectionUtil;
import com.bosch.rcputils.ui.validators.Validator;


/**
 * ICDM-2362
 *
 * @author mkl2cob
 */
public class PIDCDetailsWizardPage extends WizardPage {


  private static final int MAX_TEXT_BOX_SIZE = 4000;
  /**
   * the vertical span of text field for comment
   */
  private static final int COMMENT_FIELD_VERTICAL_SPAN = 2;
  /**
   * the height of text field for comment
   */
  private static final int COMMENT_FIELD_HEIGHT_HINT = 40;
  private FormToolkit formToolkit;
  private Composite composite;
  private Section section;
  private Form form;
  private Text valueEngText;
  private Text valueDescEngText;
  private Text valueDescGerText;
  private PIDCAliasDefSection aliasSection;
  private ControlDecoration txtValDescEngDec;
  /**
   * Decorators instance
   */
  private final Decorators decorators = new Decorators();
  private ControlDecoration txtValEngDec;
  /**
   * ICDM-2437 boolean to indicate whether the pidc name exists
   */
  protected boolean nameExists;

  /**
   * pidc creation details
   */
  private PidcCreationDetails pidcCreationDetails;

  /**
   * @param pageName page Name
   */
  protected PIDCDetailsWizardPage(final String pageName) {
    super(pageName);
    setTitle("PIDC Details");
    setDescription("Enter the PIDC details");
    this.pidcCreationDetails = getPidCreationDetails();
    // disable Next button
    setPageComplete(false);
  }


  /**
   * @return
   */
  private PidcCreationDetails getPidCreationDetails() {
    PidcCreationHandler pidcCreationHandler = new PidcCreationHandler();
    return pidcCreationHandler.getPidcCreationDetails();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(final Composite parent) {
    initializeDialogUnits(parent);
    final Composite workArea = new Composite(parent, SWT.NONE);
    createComposite(workArea);
    final GridLayout layout = new GridLayout();
    workArea.setLayout(layout);
    workArea.setLayoutData(GridDataUtil.getInstance().createGridData());
    workArea.pack();
    setControl(workArea);
  }

  /**
   * This method initializes composite
   *
   * @param workArea
   */
  private void createComposite(final Composite workArea) {
    GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite = getFormToolkit().createComposite(workArea);
    this.composite.setLayout(new GridLayout());
    createSection();
    this.composite.setLayoutData(gridData);
    this.section.getDescriptionControl().setEnabled(false);
  }

  /**
   * This method initializes section
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "Enter the details");
    createForm();
    this.section.setClient(this.form);
    this.aliasSection =
        new PIDCAliasDefSection(this.composite, getFormToolkit(), null, this.pidcCreationDetails.getAliasDefMap());
    this.aliasSection.createSectionAliasDef();
  }

  /**
   * @return the text field grid data
   */
  private GridData getTextFieldGridData() {
    GridData gridData2 = new GridData();
    gridData2.grabExcessHorizontalSpace = true;
    gridData2.horizontalAlignment = GridData.FILL;
    gridData2.verticalAlignment = GridData.CENTER;
    gridData2.grabExcessVerticalSpace = true;
    return gridData2;
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
   * This method initializes form
   */
  private void createForm() {

    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;
    this.form = getFormToolkit().createForm(this.section);
    this.form.getBody().setLayout(gridLayout);

    GridData txtGrid = getTextFieldGridData();

    // Create Value English Label
    Label valueEngLabel = getFormToolkit().createLabel(this.form.getBody(), "");

    this.valueEngText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.valueEngText.setLayoutData(txtGrid);
    // ICDM-1268
    this.valueEngText.addKeyListener(new KeyListener() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void keyReleased(final KeyEvent keyEvent) {
        if (((keyEvent.stateMask & SWT.CTRL) == SWT.CTRL) && (keyEvent.keyCode == 'v')) {
          modifyClipBoard();
        }
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public void keyPressed(final KeyEvent keyEvent) {
        // Implementation in KeyReleased() method is sufficient
      }
    });
    MenuManager menuMgr = new MenuManager("popupmenu");
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(mgr -> {
      final Action pasteAction = new Action() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
          modifyClipBoard();
        }
      };
      pasteAction.setText("Paste");
      ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.PASTE_16X16);
      pasteAction.setImageDescriptor(imageDesc);
      pasteAction.setEnabled(true);
      mgr.add(pasteAction);

    });

    Menu menu = menuMgr.createContextMenu(this.valueEngText);
    this.valueEngText.setMenu(menu);
    this.valueEngText.setFocus();


    // Add modify listener to Value English.
    this.valueEngText.addModifyListener(event -> {
      if (PIDCDetailsWizardPage.this.valueEngText.getText().isEmpty()) {
        PIDCDetailsWizardPage.this.decorators.showReqdDecoration(PIDCDetailsWizardPage.this.txtValEngDec,
            IUtilityConstants.MANDATORY_MSG);
      }
      else {
        validateExistingPIDCName();
      }

      getContainer().updateButtons();
    });

    // ICDM-112
    this.txtValEngDec = new ControlDecoration(this.valueEngText, SWT.LEFT | SWT.TOP);
    this.decorators.showReqdDecoration(this.txtValEngDec, IUtilityConstants.MANDATORY_MSG);

    getFormToolkit().createLabel(this.form.getBody(), "");

    valueEngLabel.setText("Name:");

    getFormToolkit().createLabel(this.form.getBody(), "Description (English): ");

    // ICDM-2007 (Parent task : ICDM-1774)
    TextBoxContentDisplay textBoxContentDisplay = new TextBoxContentDisplay(this.form.getBody(),
        SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, 4000, getGridDataForText());
    this.valueDescEngText = textBoxContentDisplay.getText();

    getFormToolkit().createLabel(this.form.getBody(), "");
    getFormToolkit().createLabel(this.form.getBody(), "");
    getFormToolkit().createLabel(this.form.getBody(), "");


    // Add text modify listener to eng text.
    this.valueDescEngText.addModifyListener(event -> {
      Validator.getInstance().validateNDecorate(PIDCDetailsWizardPage.this.txtValDescEngDec,
          PIDCDetailsWizardPage.this.txtValEngDec, PIDCDetailsWizardPage.this.valueDescEngText,
          PIDCDetailsWizardPage.this.valueEngText, true);
      getContainer().updateButtons();
    });

    // ICDM-112
    this.txtValDescEngDec = new ControlDecoration(this.valueDescEngText, SWT.LEFT | SWT.TOP);
    this.decorators.showReqdDecoration(this.txtValDescEngDec, IUtilityConstants.MANDATORY_MSG);

    getFormToolkit().createLabel(this.form.getBody(), "Description (German): ");

    // ICDM-2007 (Parent task : ICDM-1774)
    TextBoxContentDisplay textContentCounterValueDescGer = new TextBoxContentDisplay(this.form.getBody(),
        SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, MAX_TEXT_BOX_SIZE, getGridDataForText());
    this.valueDescGerText = textContentCounterValueDescGer.getText();

    getFormToolkit().createLabel(this.form.getBody(), "");

    // ICDM-1397
    getFormToolkit().createLabel(this.form.getBody(), "");
    getFormToolkit().createLabel(this.form.getBody(), "");


  }

  /**
   * show error decoration if the pidc name is already existing
   */
  private void validateExistingPIDCName() {
    if (isNameAlreadyExisting()) {
      PIDCDetailsWizardPage.this.decorators.showErrDecoration(PIDCDetailsWizardPage.this.txtValEngDec,
          "PIDC name already exists", true);
      PIDCDetailsWizardPage.this.nameExists = true;
    }
    else {
      PIDCDetailsWizardPage.this.decorators.showErrDecoration(PIDCDetailsWizardPage.this.txtValEngDec,
          IUtilityConstants.EMPTY_STRING, false);
      PIDCDetailsWizardPage.this.nameExists = false;
    }
  }

  /**
   * check if pidc name is already existing
   */
  private boolean isNameAlreadyExisting() {
    return this.pidcCreationDetails.getExistingPidcNames().contains(this.valueEngText.getText());
  }

  /**
   * This method defines grid data for Text
   *
   * @return GridData
   */
  private GridData getGridDataForText() {
    GridData gridDataText = new GridData();
    gridDataText.grabExcessHorizontalSpace = true;
    gridDataText.horizontalAlignment = GridData.FILL;
    gridDataText.verticalAlignment = GridData.FILL;
    gridDataText.verticalSpan = COMMENT_FIELD_VERTICAL_SPAN;
    gridDataText.heightHint = COMMENT_FIELD_HEIGHT_HINT;
    gridDataText.grabExcessVerticalSpace = true;
    return gridDataText;
  }


  /**
   *
   */
  private void modifyClipBoard() {
    Clipboard clipboard = new Clipboard(Display.getCurrent());
    HTMLTransfer htmlTransfer = HTMLTransfer.getInstance();
    String htmlContents = (String) clipboard.getContents(htmlTransfer);
    String hrefContent = null;
    Pattern pattern = Pattern.compile("href=\'([^\"]*)\'", Pattern.DOTALL);
    if (htmlContents != null) {
      Matcher matcher = pattern.matcher(htmlContents);
      if (matcher.find()) {
        // Get all groups for this match
        for (int i = 0; i <= matcher.groupCount(); i++) {
          String groupStr = matcher.group(i);
          hrefContent = groupStr;
        }
      }
    }
    if ((hrefContent != null) && !hrefContent.isEmpty()) {
      this.valueEngText.setText(hrefContent);
      TextTransfer textTransfer = TextTransfer.getInstance();
      String textContents = (String) clipboard.getContents(textTransfer);
      this.valueDescEngText.setText(textContents);
    }
  }

  /**
   * on clicking next button, store the details
   */
  public void nextPressed() {
    PIDCCreationWizard wizard = (PIDCCreationWizard) getWizard();
    wizard.getWizardData().setNameEng(this.valueEngText.getText());
    wizard.getWizardData().setDescEng(this.valueDescEngText.getText());
    wizard.getWizardData().setDescGer(this.valueDescGerText.getText());
    wizard.getWizardData().setAliasDefinition(this.aliasSection.getSelAliasDef());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canFlipToNextPage() {
    boolean canProceed;
    if (CommonUtils.isEmptyString(this.valueEngText.getText()) ||
        CommonUtils.isEmptyString(this.valueDescEngText.getText()) || this.nameExists) {
      canProceed = false;
    }
    else {
      canProceed = true;
    }
    return canProceed;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isPageComplete() {
    return canFlipToNextPage();
  }


  /**
   * @return the pidcCreationDetails
   */
  public PidcCreationDetails getPidcCreationDetails() {
    return this.pidcCreationDetails;
  }


  /**
   * @param pidcCreationDetails the pidcCreationDetails to set
   */
  public void setPidcCreationDetails(final PidcCreationDetails pidcCreationDetails) {
    this.pidcCreationDetails = pidcCreationDetails;
  }


}
