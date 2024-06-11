/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.apic.ui.views.FocusMatrixEditView;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixAttributeClientBO;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixColorCode;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixDetails;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixUseCaseItem;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseSectionClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrix;
import com.bosch.caltool.icdm.ws.rest.client.apic.FocusMatrixServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.CustomNATTable;
import com.bosch.rcputils.IUtilityConstants;
import com.bosch.rcputils.decorators.Decorators;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.text.TextBoxContentDisplay;
import com.bosch.rcputils.ui.validators.Validator;
import com.bosch.rcputils.wbutils.WorkbenchUtils;


/**
 * This class is for the pop up dialog that appears during single click of focus matrix usecase item
 *
 * @author mkl2cob
 */
public class FocusMatrixEditDialog extends PopupDialog implements AbstractFMEditComponent {


  /**
   * next level composite
   */
  Composite composite;

  /**
   * FormToolkit
   */
  private FormToolkit formToolkit;

  /**
   * remarks text
   */
  private Text remarksText;

  /**
   * link text
   */
  private Text linkText;

  /**
   * constant for different value string display
   */
  private static final String DIFFERENT_VALUE = "<Diff Value>";

  /**
   * ColorSelectionDialogBtnGroup instance
   */
  private final ColorSelectionDialogBtnGroup colorRadioBtnGrp = new ColorSelectionDialogBtnGroup(this);

  /**
   * old remarks string
   */
  private String oldRemarks;


  /**
   * new remarks string
   */
  private String newRemarks;

  /**
   * boolean for multiple update
   */
  private boolean multiUpdate;
  /**
   * boolean to store difference in remarks
   */
  private boolean remarksDiff;

  /**
   * map for selected uc items
   */
  private final Map<FocusMatrixAttributeClientBO, List<FocusMatrixUseCaseItem>> fetchSelectedUcItems;

  /**
   * boolean to indicate that color code is different in multi update
   */
  private boolean colorCodeDiff;

  /**
   * boolean to indicate whether atleast one remarks exists for multi selection
   */
  private boolean atleastOneRemarkEmpty;


  /*
   * The listener installed in order to close the popup.
   */
  private PopupCloserListener popupCloser;

  private final Point sourcePoint;

  /**
   * old link string
   */
  private String oldlinkStr;

  /**
   * new link string
   */
  private String newlinkStr;

  /**
   * CustomNATTable instance
   */
  private final CustomNATTable natTable;


  /**
   * decorator for link field
   */
  private ControlDecoration linkDecor;

  /**
   * decorator for remarks field
   */
  private ControlDecoration remarkDecor;

  /*
   * The pixel offset of the popup from the bottom corner of the control.
   */
  private static final int POPUP_OFFSET = 27;

  /**
   * Button instance
   */
  private Button linkButton;

  private boolean linksDiff;

  /**
   * boolean to indicate whether
   */
  private boolean errorDialogDisplayed;

  /**
   * boolean to indicate that a command is executed
   */
  private boolean commandExecuted;

  /**
   * the remarks height hint
   */
  private static final int REMARKS_HEIGHT_HINT = 60;
  /**
   * the maximum length for the text field
   */
  private static final int MAX_TEXT_BOX_SIZE = 4000;

  /**
   * Constructor
   *
   * @param map Map<FocusMatrixAttribute, List<FocusMatrixUseCaseItem>>
   * @param mappedRec Point
   * @param natTable CustomNATTable
   */
  public FocusMatrixEditDialog(
      final Map<FocusMatrixAttributeClientBO, List<com.bosch.caltool.icdm.client.bo.fm.FocusMatrixUseCaseItem>> map,
      final Point mappedRec, final CustomNATTable natTable) {
    // create a resizable pop up dialog
    super(new Shell(), SWT.RESIZE | SWT.ON_TOP | SWT.TITLE, false, false, false, false, false, "Edit Focus matrix",
        null);
    this.fetchSelectedUcItems = map;
    this.sourcePoint = mappedRec;
    this.natTable = natTable;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createTitleMenuArea(final Composite parent) {
    GridData gridData = new GridData();
    gridData.horizontalAlignment = SWT.RIGHT;
    Button btn = new Button(parent, SWT.PUSH);
    btn.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.SHOW_VIEW));
    btn.setToolTipText("Show in View");
    btn.setLayoutData(gridData);
    btn.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {

        try {
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
              .showView("com.bosch.caltool.apic.ui.views.FocusMatrixEditView");
          FocusMatrixEditView fmEditview = (FocusMatrixEditView) WorkbenchUtils.getView(FocusMatrixEditView.PART_ID);
          fmEditview.setValuesInView(FocusMatrixEditDialog.this.fetchSelectedUcItems,
              FocusMatrixEditDialog.this.sourcePoint, FocusMatrixEditDialog.this.natTable);
          close();
          // store the preference to preference store
          PlatformUI.getPreferenceStore().setValue(CommonUtils.FM_EDIT_VIEW, ApicConstants.CODE_YES);
        }
        catch (PartInitException exp) {
          CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }
    });


    return super.createTitleMenuArea(parent);
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
    // set the back ground for buttons
    this.colorRadioBtnGrp.setBackgroundColorForButtons();
    return contents;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected Color getBackground() {
    // grey background for the pop up dialog
    return Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    Composite top = new Composite(parent, SWT.NONE);
    createComposite(top);
    top.setLayout(new GridLayout());
    top.setLayoutData(GridDataUtil.getInstance().getGridData());
    return top;
  }

  /**
   * create composite for dialog
   *
   * @param top Composite
   */
  private void createComposite(final Composite top) {
    this.composite = getFormToolkit().createComposite(top);

    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;// 3 columns
    this.composite.setLayout(gridLayout);
    getFormToolkit().createLabel(this.composite, "Color");
    Group grp = new Group(this.composite, SWT.NONE);

    this.colorRadioBtnGrp.createRadioButtons(grp);

    // set the existing values
    setExistingValuesForColor(grp);

    getFormToolkit().createLabel(this.composite, "");

    // create remarks field
    GridData remarksGridLayout = GridDataUtil.getInstance().getGridData();
    remarksGridLayout.verticalSpan = 3;
    remarksGridLayout.heightHint = FocusMatrixEditDialog.REMARKS_HEIGHT_HINT;

    getFormToolkit().createLabel(this.composite, "Remarks");

    TextBoxContentDisplay textBoxContentDisplay = new TextBoxContentDisplay(this.composite,
        SWT.BORDER | SWT.V_SCROLL | SWT.MULTI, MAX_TEXT_BOX_SIZE, remarksGridLayout);
    this.remarksText = textBoxContentDisplay.getText();

    this.remarksText.addModifyListener(new ModifyListener() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void modifyText(final ModifyEvent event) {
        FocusMatrixEditDialog.this.newRemarks = FocusMatrixEditDialog.this.remarksText.getText();
        validateRemarks();
      }
    });
    this.remarkDecor = new ControlDecoration(this.remarksText, SWT.LEFT | SWT.TOP);

    // create dummy labels
    getFormToolkit().createLabel(this.composite, "");
    getFormToolkit().createLabel(this.composite, "");
    getFormToolkit().createLabel(this.composite, "");
    // create link fields
    getFormToolkit().createLabel(this.composite, "Link");
    this.linkText = getFormToolkit().createText(this.composite, "", SWT.BORDER);
    GridData linkLayoutData = new GridData();
    linkLayoutData.grabExcessHorizontalSpace = true;
    linkLayoutData.horizontalAlignment = GridData.FILL;
    this.linkText.setLayoutData(linkLayoutData);
    this.linkText.addModifyListener(new ModifyListener() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void modifyText(final ModifyEvent event) {
        FocusMatrixEditDialog.this.newlinkStr = FocusMatrixEditDialog.this.linkText.getText();
        validateLink();
      }
    });
    // creating decorator for the link text field
    this.linkDecor = new ControlDecoration(this.linkText, SWT.LEFT | SWT.TOP);
    // creating button to open link
    this.linkButton = getFormToolkit().createButton(this.composite, "", SWT.NONE);
    this.linkButton.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.LINK_DECORATOR_12X12));
    this.linkButton.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent selectionEvnt) {

        CommonActionSet commonActionSet = new CommonActionSet();
        // ICDM-2529
        commonActionSet.openLink(FocusMatrixEditDialog.this.linkText.getText());
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent selectionEvnt) {
        // Not needed
      }
    });

    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
    // set the existing values for remarks
    setExistingValuesForRemarks();
    // set the existing values for link
    setExistingValuesForLink();
  }

  /**
   * this method validates remarks
   */
  @Override
  public void validateRemarks() {
    Validator.getInstance().validateNDecorate(FocusMatrixEditDialog.this.remarkDecor,
        FocusMatrixEditDialog.this.remarksText, false, false);
    if (remarksNotCorrect()) {
      Decorators decorators = new Decorators();
      decorators.showReqdDecoration(FocusMatrixEditDialog.this.remarkDecor, "Remark has to be entered!");
    }
  }

  /**
   * ICDM-1641 set values for link
   */
  private void setExistingValuesForLink() {

    if (this.multiUpdate && this.linksDiff) {
      // in case of multi update and different values
      this.linkText.setText(DIFFERENT_VALUE);
    }
    else {
      this.linkText.setText(this.oldlinkStr);
    }

  }

  /**
   * set existing value for remarks text area
   */
  private void setExistingValuesForRemarks() {

    if (this.multiUpdate) {
      // set the remarks for multiple update
      if (this.remarksDiff) {
        this.remarksText.setText(DIFFERENT_VALUE);
      }
      else {
        if (null != this.oldRemarks) {
          this.remarksText.setText(this.oldRemarks);
        }
      }
    }
    else {
      // set the remarks for single update
      if (null != this.oldRemarks) {
        this.remarksText.setText(this.oldRemarks);
      }
    }

  }

  /**
   * @return true is remark is not changed
   */
  private boolean remarksNotChanged() {
    if (this.multiUpdate) {
      // checking for multiple update
      if (this.remarksDiff) {
        return CommonUtils.isEqual(this.newRemarks, DIFFERENT_VALUE);
      }
      return CommonUtils.isEqual(this.colorRadioBtnGrp.oldColorCode, this.colorRadioBtnGrp.newColorCode);// TODO
    }
    // checking for single update
    return CommonUtils.isEqual(this.newRemarks, this.oldRemarks);
  }

  /**
   * @return true if color is not changed
   */
  private boolean colorNotChanged() {
    if (this.multiUpdate) {
      // checking for multiple update
      if (this.colorCodeDiff) {
        return CommonUtils.isEqual(DIFFERENT_VALUE, this.colorRadioBtnGrp.newColorCode);
      }
      return CommonUtils.isEqual(this.colorRadioBtnGrp.oldColorCode, this.colorRadioBtnGrp.newColorCode);
    }
    // checking for single update
    return CommonUtils.isEqual(this.colorRadioBtnGrp.oldColorCode, this.colorRadioBtnGrp.newColorCode);

  }

  /**
   * ICDM-1626
   *
   * @return true if remark is not entered in case of choosing a color other than white
   */
  private boolean remarksNotCorrect() {
    if (colorRadioBtnSelected()) {
      if (this.atleastOneRemarkEmpty && CommonUtils.isEqual(this.newRemarks, DIFFERENT_VALUE)) {
        // if atleast one remark is empty when color radio button is chosen, then return remarks are not correct
        return true;
      }
      // some remark should be entered if a color is choosen
      return CommonUtils.isEmptyString(this.newRemarks);
    }
    if (this.colorCodeDiff && CommonUtils.isEqual(DIFFERENT_VALUE, this.colorRadioBtnGrp.newColorCode)) {
      return CommonUtils.isEmptyString(this.newRemarks);
    }
    return false;
  }


  /**
   * @return true if any color radio button is selected
   */
  private boolean colorRadioBtnSelected() {
    return CommonUtils.isEqual(FocusMatrixColorCode.GREEN, this.colorRadioBtnGrp.newColorCode) ||
        CommonUtils.isEqual(FocusMatrixColorCode.RED, this.colorRadioBtnGrp.newColorCode) ||
        CommonUtils.isEqual(FocusMatrixColorCode.YELLOW, this.colorRadioBtnGrp.newColorCode) ||
        CommonUtils.isEqual(FocusMatrixColorCode.ORANGE, this.colorRadioBtnGrp.newColorCode);
  }

  /**
   * set existing color and comments
   *
   * @param grp Group
   */
  private void setExistingValuesForColor(final Group grp) {
    // get the row
    FocusMatrixAttributeClientBO fmAttribute = this.fetchSelectedUcItems.keySet().iterator().next();
    // get the col
    List<FocusMatrixUseCaseItem> focusMatrixItem = this.fetchSelectedUcItems.get(fmAttribute);
    // get the color of the cell
    this.colorRadioBtnGrp.oldColorCode = focusMatrixItem.get(0).getColorCode(fmAttribute.getAttribute());
    this.colorRadioBtnGrp.newColorCode = this.colorRadioBtnGrp.oldColorCode;
    // get the remarks of the cell
    this.oldRemarks = focusMatrixItem.get(0).getComments(fmAttribute.getAttribute());
    this.newRemarks = this.oldRemarks;

    // get the link string of the cell
    this.oldlinkStr = focusMatrixItem.get(0).getLink(fmAttribute.getAttribute());
    this.newlinkStr = this.oldlinkStr;


    this.oldlinkStr = focusMatrixItem.get(0).getLink(fmAttribute.getAttribute());
    this.oldlinkStr = this.oldlinkStr == null ? "" : this.oldlinkStr;
    this.newlinkStr = this.oldlinkStr;

    this.multiUpdate = (this.fetchSelectedUcItems.size() > 1) ||
        (this.fetchSelectedUcItems.get(this.fetchSelectedUcItems.keySet().iterator().next()).size() > 1);

    if (this.multiUpdate) {
      setExistingValuesForMultiUpdate(this.colorRadioBtnGrp.oldColorCode.getColor(), grp);
    }
    else {

      // Set the values for single selection
      if (null == this.colorRadioBtnGrp.oldColorCode) {
        this.colorRadioBtnGrp.getWhiteBtn().setSelection(true);
      }
      else {
        setColorCodeRadioButton(this.colorRadioBtnGrp.oldColorCode);
      }

      GridLayout gridLayoutGrp = new GridLayout();
      gridLayoutGrp.numColumns = 5;
      grp.setLayout(gridLayoutGrp);
    }


  }

  /**
   * // ICDM-1592 Multiple update
   *
   * @param colorCodeStr String
   * @param grp Group
   */
  private void setExistingValuesForMultiUpdate(final String colorCodeStr, final Group grp) {

    this.colorCodeDiff = false;
    boolean allGrey = true;
    this.remarksDiff = false;
    this.linksDiff = false;

    this.atleastOneRemarkEmpty = false;

    for (FocusMatrixAttributeClientBO focusMatrixAttribute : this.fetchSelectedUcItems.keySet()) {
      for (FocusMatrixUseCaseItem focusMatrixUseCaseItem : this.fetchSelectedUcItems.get(focusMatrixAttribute)) {
        String colorCodeTemp = focusMatrixUseCaseItem.getColorCode(focusMatrixAttribute.getAttribute()).getColor();

        if (isAtleastOneColored(colorCodeTemp)) {
          // if any one is color, all grey is false
          allGrey = false;
        }

        if (!this.colorCodeDiff && !CommonUtils.isEqual(colorCodeStr, colorCodeTemp)) {
          // set diff color code flag to true if there is a change
          this.colorCodeDiff = true;
        }

        String remarksTemp = focusMatrixUseCaseItem.getComments(focusMatrixAttribute.getAttribute());
        if (!this.atleastOneRemarkEmpty && CommonUtils.isEmptyString(remarksTemp)) {
          // set the flag to know if atleast one remark is empty
          this.atleastOneRemarkEmpty = true;
        }
        setRemarksLinkDiffFlag(focusMatrixAttribute, focusMatrixUseCaseItem, remarksTemp);

      }
    }

    GridLayout gridLayoutGrp = new GridLayout();
    gridLayoutGrp.numColumns = 5;
    setValueInRadioButton(colorCodeStr, grp, this.colorCodeDiff, allGrey, gridLayoutGrp);
  }


  /**
   * @param focusMatrixAttribute FocusMatrixAttribute
   * @param focusMatrixUseCaseItem FocusMatrixUseCaseItem
   * @param remarksTemp String
   */
  void setRemarksLinkDiffFlag(final FocusMatrixAttributeClientBO focusMatrixAttribute,
      final FocusMatrixUseCaseItem focusMatrixUseCaseItem, final String remarksTemp) {
    if (!this.remarksDiff && !checkEqualconsideringNull(remarksTemp, this.oldRemarks)) {
      // set diff remarks flag to true if there is a change
      this.remarksDiff = true;
    }
    String linkTemp = focusMatrixUseCaseItem.getLink(focusMatrixAttribute.getAttribute());
    if (!this.linksDiff && !(checkEqualconsideringNull(linkTemp, this.oldlinkStr))) {
      this.linksDiff = true;
    }
  }


  /**
   * @param colorCodeTemp String
   * @return true if atleast one selection is colored
   */
  boolean isAtleastOneColored(final String colorCodeTemp) {
    return CommonUtils.isEqual(FocusMatrixColorCode.GREEN.getColor(), colorCodeTemp) ||
        CommonUtils.isEqual(FocusMatrixColorCode.YELLOW.getColor(), colorCodeTemp) ||
        CommonUtils.isEqual(FocusMatrixColorCode.RED.getColor(), colorCodeTemp) ||
        CommonUtils.isEqual(FocusMatrixColorCode.ORANGE.getColor(), colorCodeTemp);
  }


  /**
   * @param remarksTemp String
   * @param oldStr String
   * @return true if the strings are equal (empty string and null are considered equal)
   */
  private boolean checkEqualconsideringNull(final String remarksTemp, final String oldStr) {
    if (CommonUtils.isEmptyString(oldStr) && CommonUtils.isEmptyString(remarksTemp)) {
      return true;
    }
    return CommonUtils.isEqual(oldStr, remarksTemp);
  }

  /**
   * @param colorCodeStr String
   * @param grp Group
   * @param colorCodeDiff boolean
   * @param allGrey boolean
   * @param gridLayoutGrp GridLayout
   */
  private void setValueInRadioButton(final String colorCodeStr, final Group grp, final boolean colorCodeDiff,
      final boolean allGrey, final GridLayout gridLayoutGrp) {
    if (colorCodeDiff && !allGrey) {
      // color code is different and all are not grey( this case will be true if some are grey and some are null)
      this.colorRadioBtnGrp.setDiffValBtn(getFormToolkit().createButton(grp, "<Diff Color>", SWT.RADIO));

      this.colorRadioBtnGrp.getDiffValBtn().addSelectionListener(new SelectionListener() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void widgetSelected(final SelectionEvent arg0) {
          FocusMatrixEditDialog.this.colorRadioBtnGrp.setDiffValBtnSelcted(true);
          validateRemarks();
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public void widgetDefaultSelected(final SelectionEvent arg0) {
          // TODO Auto-generated method stub

        }
      });

      gridLayoutGrp.numColumns = 6;
      this.colorRadioBtnGrp.getDiffValBtn().setSelection(true);
      FocusMatrixEditDialog.this.colorRadioBtnGrp.setDiffValBtnSelcted(true);
    }
    else if ((null == colorCodeStr) || allGrey) {
      // if the color codes are not set for all selected items
      this.colorRadioBtnGrp.getWhiteBtn().setSelection(true);
    }
    else {
      setColorCodeRadioButton(FocusMatrixColorCode.getColor(colorCodeStr));
    }
    grp.setLayout(gridLayoutGrp);
  }

  /**
   * @param oldColorCodeStr
   */
  private void setColorCodeRadioButton(final FocusMatrixColorCode oldColorCodeStr) {

    switch (oldColorCodeStr) {
      case GREEN:
        this.colorRadioBtnGrp.getGreenBtn().setSelection(true);
        break;
      case YELLOW:
        this.colorRadioBtnGrp.getYellowBtn().setSelection(true);
        break;
      case RED:
        this.colorRadioBtnGrp.getRedBtn().setSelection(true);
        break;
      case ORANGE:
        this.colorRadioBtnGrp.getOrngBtn().setSelection(true);
        break;
      default:
        this.colorRadioBtnGrp.getWhiteBtn().setSelection(true);
    }

  }

  /**
   * This method initializes formToolkit
   *
   * @return org.eclipse.ui.forms.widgets.FormToolkit
   */
  @Override
  public FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }


  /**
   * {@inheritDoc}
   */
  private void saveChanges() {

    try {
      if (this.multiUpdate) {
        multipleModification();
      }
      else {
        singleCellModification();
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }


  }


  /**
   * multiple modification of focus matrix records
   *
   * @throws ApicWebServiceException
   */
  private void multipleModification() throws ApicWebServiceException {

    Set<FocusMatrixAttributeClientBO> fmAttributeSet = this.fetchSelectedUcItems.keySet();
    Map<Long, FocusMatrixDetails> attrFcsMatrxMap;
    IUseCaseItemClientBO useCaseItem;
    Long ucItemID;

    FocusMatrixDetails existingFocusMatrix;
    FocusMatrixServiceClient fmClient = new FocusMatrixServiceClient();

    List<FocusMatrix> insertList = new ArrayList<FocusMatrix>();
    List<FocusMatrix> updateList = new ArrayList<FocusMatrix>();

    for (FocusMatrixAttributeClientBO fmAttribute : fmAttributeSet) {
      for (FocusMatrixUseCaseItem fmUcItem : this.fetchSelectedUcItems.get(fmAttribute)) {

        attrFcsMatrxMap =
            fmUcItem.getFocusMatrixVersion().getFocusMatrixItemMap().get(fmAttribute.getAttribute().getId());
        useCaseItem = fmUcItem.getUseCaseItem();
        ucItemID = useCaseItem.getID();

        existingFocusMatrix = attrFcsMatrxMap == null ? null : attrFcsMatrxMap.get(ucItemID);

        FocusMatrix fcusMatrix;

        if (null == existingFocusMatrix) {
          fcusMatrix = newFcsMatrxCreate(fmAttribute, fmUcItem, useCaseItem, ucItemID);
          insertList.add(fcusMatrix);
        }
        else {
          fcusMatrix = existingFocusMatrix.getFocusMatrix();
          FocusMatrix fcusMatrixClone = fcusMatrix.clone();
          if (!CommonUtils.isEqual(DIFFERENT_VALUE, this.newRemarks)) {
            fcusMatrixClone.setComments(this.newRemarks);
          }
          if (!CommonUtils.isEqual(DIFFERENT_VALUE, this.newlinkStr)) {
            fcusMatrixClone.setLink(this.newlinkStr);
          }
          fcusMatrixClone.setIsDeleted(false);
          if (!this.colorRadioBtnGrp.isDiffValBtnSelcted()) {
            fcusMatrixClone.setColorCode(this.colorRadioBtnGrp.newColorCode.getColor());
          }
          updateList.add(fcusMatrixClone);
        }
      }
    }

    fmClient.multipleCreate(insertList);
    fmClient.multipleUpdate(updateList);
  }


  /**
   * singleCellModification
   *
   * @throws ApicWebServiceException
   */
  private void singleCellModification() throws ApicWebServiceException {

    FocusMatrixAttributeClientBO fmAttribute = this.fetchSelectedUcItems.keySet().iterator().next();

    List<FocusMatrixUseCaseItem> focusMatrixItem = this.fetchSelectedUcItems.get(fmAttribute);

    FocusMatrixUseCaseItem focusMatrixUseCaseItem = focusMatrixItem.get(0);

    Map<Long, FocusMatrixDetails> attrFmMap =
        focusMatrixUseCaseItem.getFocusMatrixVersion().getFocusMatrixItemMap().get(fmAttribute.getAttribute().getId());

    IUseCaseItemClientBO useCaseItem = focusMatrixUseCaseItem.getUseCaseItem();

    Long ucItemID = useCaseItem.getID();

    FocusMatrixDetails existingFocusMatrix = attrFmMap == null ? null : attrFmMap.get(ucItemID);

    FocusMatrixServiceClient fmClient = new FocusMatrixServiceClient();

    FocusMatrix focusMatrix;

    if (null == existingFocusMatrix) {
      focusMatrix = newFcsMatrxCreate(fmAttribute, focusMatrixUseCaseItem, useCaseItem, ucItemID);
      fmClient.create(focusMatrix);
    }
    else {
      focusMatrix = existingFocusMatrix.getFocusMatrix();
      FocusMatrix fcsMatrixClone = focusMatrix.clone();
      fcsMatrixClone.setLink(this.newlinkStr);
      fcsMatrixClone.setColorCode(this.colorRadioBtnGrp.newColorCode.getColor());
      fcsMatrixClone.setComments(this.newRemarks);
      fmClient.update(fcsMatrixClone);
    }


  }

  /**
   * single insert of focus matrix record
   *
   * @param fmAttribute
   * @param focusMatrixUseCaseItem
   * @param useCaseItem
   * @param ucItemID
   * @param fmClient
   * @throws ApicWebServiceException
   */
  private FocusMatrix newFcsMatrxCreate(final FocusMatrixAttributeClientBO fmAttribute,
      final FocusMatrixUseCaseItem focusMatrixUseCaseItem, final IUseCaseItemClientBO useCaseItem, final Long ucItemID)
      throws ApicWebServiceException {

    FocusMatrix focusMatrix = new FocusMatrix();

    if (!CommonUtils.isEqual(DIFFERENT_VALUE, this.newlinkStr)) {
      focusMatrix.setLink(this.newlinkStr);
    }
    focusMatrix.setColorCode(this.colorRadioBtnGrp.newColorCode.getColor());
    if (!CommonUtils.isEqual(DIFFERENT_VALUE, this.newRemarks)) {
      focusMatrix.setComments(this.newRemarks);
    }
    focusMatrix.setFmVersId(focusMatrixUseCaseItem.getFocusMatrixVersion().getFmVersion().getId());
    focusMatrix.setIsDeleted(false);
    Long ucpaId = focusMatrixUseCaseItem.getAttributeMapping().get(fmAttribute.getAttribute());

    if (ucpaId == null) {
      focusMatrix.setAttrId(fmAttribute.getAttribute().getId());
      if (useCaseItem instanceof UseCaseSectionClientBO) {
        focusMatrix.setSectionId(ucItemID);
      }
      if (useCaseItem instanceof UsecaseClientBO) {
        focusMatrix.setUseCaseId(ucItemID);
      }
    }
    else {
      focusMatrix.setUcpaId(ucpaId);
    }

    return focusMatrix;
  }

  /**
   * Opens this ContentProposalPopup. This method is extended in order to add the control listener when the popup is
   * opened and to invoke the secondary popup if applicable.
   *
   * @return the return code
   * @see org.eclipse.jface.window.Window#open()
   */
  @Override
  public int open() {
    int value = super.open();
    if (this.popupCloser == null) {
      this.popupCloser = new PopupCloserListener(this);
    }
    this.popupCloser.installListeners();
    // provide a hook for adjusting the bounds. This is only
    // necessary when there is content driven sizing that must be
    // adjusted each time the dialog is opened.
    adjustBounds();
    return value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void adjustBounds() {
    // Get our control's location in display coordinates.
    int initialX = this.sourcePoint.x + POPUP_OFFSET;
    int initialY = (this.sourcePoint.y + this.composite.getSize().y) - 2;

    // set location for the pop up
    getShell().setLocation(initialX, initialY);
  }

  /**
   * Closes this popup. This method is extended to remove the control listener.
   *
   * @return <code>true</code> if the window is (or was already) closed, and <code>false</code> if it is still open
   */
  @Override
  public boolean close() {
    if ((getShell() == null) || getShell().isDisposed()) {
      return true;
    }
    validateEntries();
    // ICDM-1626
    boolean noChanges = remarksNotChanged() && colorNotChanged() && linkNotChanged();

    if (canSaveChanges(noChanges)) {
      // save the changes only if there is a change and link & remarks are correct
      saveChanges();
    }

    if (this.popupCloser != null) {
      this.popupCloser.removeListeners();
    }
    boolean ret = super.close();

    return ret;
  }


  /**
   * @param noChanges boolean
   * @return true if changes can be saved
   */
  private boolean canSaveChanges(final boolean noChanges) {
    return !noChanges && !linkNotCorrect() && !remarksNotCorrect() && !this.commandExecuted;
  }

  /**
   * @return true if link is not changed
   */
  private boolean linkNotChanged() {
    // multi update //single update
    return (this.linksDiff && CommonUtils.isEqual(this.newlinkStr, DIFFERENT_VALUE)) ||
        CommonUtils.isEqual(this.oldlinkStr, this.newlinkStr);
  }

  /**
   * this method validates link
   */
  private void validateLink() {
    Validator.getInstance().validateNDecorate(FocusMatrixEditDialog.this.linkDecor, FocusMatrixEditDialog.this.linkText,
        false, false);

    if (CommonUtils.isEqual(this.newlinkStr, DIFFERENT_VALUE) ||
        CommonUtils.isEmptyString(FocusMatrixEditDialog.this.newlinkStr)) {
      this.linkButton.setEnabled(false);
    }
    else if (linkNotCorrect()) {
      // disable link button and show error decoration
      this.linkButton.setEnabled(false);
      Decorators decorators = new Decorators();
      decorators.showErrDecoration(FocusMatrixEditDialog.this.linkDecor, IUtilityConstants.INVALID_LINK, true);
    }
    else {
      // enable link button
      this.linkButton.setEnabled(true);
    }

  }

  /**
   * @return true if link is not correct
   */
  private boolean linkNotCorrect() {
    return !CommonUtils.isEmptyString(FocusMatrixEditDialog.this.newlinkStr) &&
        !CommonUtils.isValidHyperlinkFormat(FocusMatrixEditDialog.this.newlinkStr) &&
        !CommonUtils.isEqual(this.newlinkStr, DIFFERENT_VALUE);
  }

  /**
   * @return true if the dialog can be closed
   */
  private boolean validateEntries() {
    // validate entries and throw error dialog in case the values are not correct
    if ((remarksNotCorrect() || linkNotCorrect()) && !this.errorDialogDisplayed) {// ICDM-1641
      MessageDialog errorDialog = new MessageDialog(new Shell(), "iCDM", null,
          "Error has occurred while saving focus matrix details! Enter valid remarks and link!", MessageDialog.ERROR,
          new String[] { IDialogConstants.OK_LABEL }, 0) {

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean close() {
          FocusMatrixEditDialog.this.errorDialogDisplayed = false;
          return super.close();
        }
      };
      this.errorDialogDisplayed = true;
      errorDialog.open();

    }
    return true;
  }

  /**
   * @return boolean errorDialogDisplayed
   */
  public boolean getErrorDialogDisplayed() {
    return this.errorDialogDisplayed;
  }

  /**
   * @return the natTable
   */
  public CustomNATTable getNatTable() {
    return this.natTable;
  }

}
