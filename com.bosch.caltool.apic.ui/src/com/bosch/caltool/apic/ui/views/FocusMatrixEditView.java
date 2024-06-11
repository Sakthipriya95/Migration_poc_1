/**
 *
 */
package com.bosch.caltool.apic.ui.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.dialogs.AbstractFMEditComponent;
import com.bosch.caltool.apic.ui.dialogs.ColorSelectionDialogBtnGroup;
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
import com.bosch.caltool.icdm.common.ui.views.AbstractViewPart;
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

/**
 * @author adn1cob
 */
public class FocusMatrixEditView extends AbstractViewPart implements AbstractFMEditComponent {


  /**
   * ID of Focus matrix edit View
   */
  public static final String PART_ID = "com.bosch.caltool.apic.ui.views.FocusMatrixEditView";
  /**
   * Instance of form toolkit
   */
  private FormToolkit formToolkit;
  /**
   * Composite instance for base layout
   */
  private Composite top;
  /**
   * Composite instance
   */
  private Composite composite;
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
  private Map<FocusMatrixAttributeClientBO, List<FocusMatrixUseCaseItem>> fetchSelectedUcItems;

  /**
   * boolean to indicate that color code is different in multi update
   */
  private boolean colorCodeDiff;

  /**
   * boolean to indicate whether atleast one remarks exists for multi selection
   */
  private boolean atleastOneRemarkEmpty;


  /**
   * old link string
   */
  private String oldlinkStr;

  /**
   * new link string
   */
  private String newlinkStr;

  /**
   * decorator for link field
   */
  private ControlDecoration linkDecor;

  /**
   * decorator for remarks field
   */
  private ControlDecoration remarkDecor;

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
  private Group parentGrp;


  /**
   * Constructor
   *
   * @param map Map<FocusMatrixAttribute, List<FocusMatrixUseCaseItem>>
   * @param mappedRec Point
   * @param natTable CustomNATTable
   */
  public void setValuesInView(
      final Map<FocusMatrixAttributeClientBO, List<com.bosch.caltool.icdm.client.bo.fm.FocusMatrixUseCaseItem>> map,
      final Point mappedRec, final CustomNATTable natTable) {

    this.fetchSelectedUcItems = map;
    // set the existing values
    setExistingValuesForColor(this.parentGrp);
    // set the existing values for remarks
    setExistingValuesForRemarks();
    // set the existing values for link
    setExistingValuesForLink();


  }


  /*
   * (non-Javadoc)
   * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
   */
  @Override
  public void createPartControl(final Composite parent) {
    addHelpAction();
    this.formToolkit = new FormToolkit(parent.getDisplay());
    this.top = new Composite(parent, SWT.NONE);
    this.top.setLayout(new GridLayout());
    createComposite();

    setTitleToolTip("Focus Matrix Color Code, Comments , Link editing");


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
   *
   */
  private void createComposite() {

    GridData gridData = new GridData();
    gridData.horizontalAlignment = SWT.RIGHT;
    Action btn = new Action() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        page.hideView(page.findView(FocusMatrixEditView.PART_ID));
        // store the preference to preference store
        PlatformUI.getPreferenceStore().setValue(CommonUtils.FM_EDIT_VIEW, ApicConstants.CODE_NO);
      }
    };
    ImageManager.getInstance();
    btn.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.SHOW_VIEW));
    btn.setToolTipText("Show in Dialog");


    Action saveBtn = new Action() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {
        validateEntries();
        // ICDM-1626
        boolean noChanges = remarksNotChanged() && colorNotChanged() && linkNotChanged();

        if (canSaveChanges(noChanges)) {
          // save the changes only if there is a change and link & remarks are correct
          saveChanges();
        }
      }

    };
    ImageManager.getInstance();
    saveBtn.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.SAVE_28X30));
    saveBtn.setToolTipText("Save");

    IActionBars actionBars = getViewSite().getActionBars();
    IMenuManager dropDownMenu = actionBars.getMenuManager();
    IToolBarManager toolBar = actionBars.getToolBarManager();
    dropDownMenu.add(saveBtn);
    toolBar.add(saveBtn);

    dropDownMenu.add(btn);
    toolBar.add(btn);


    this.composite = getFormToolkit().createComposite(this.top);
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;// 3 columns
    this.composite.setLayout(gridLayout);
    getFormToolkit().createLabel(this.composite, "Color");
    this.parentGrp = new Group(this.composite, SWT.NONE);

    this.colorRadioBtnGrp.createRadioButtons(this.parentGrp);

    GridLayout gridLayoutGrp = new GridLayout();
    gridLayoutGrp.numColumns = 5;
    this.parentGrp.setLayout(gridLayoutGrp);

    getFormToolkit().createLabel(this.composite, "");

    // create remarks field
    GridData remarksGridLayout = GridDataUtil.getInstance().getGridData();
    remarksGridLayout.verticalSpan = 3;
    remarksGridLayout.heightHint = REMARKS_HEIGHT_HINT;

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

        FocusMatrixEditView.this.newRemarks = FocusMatrixEditView.this.remarksText.getText();
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
        FocusMatrixEditView.this.newlinkStr = FocusMatrixEditView.this.linkText.getText();
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
        commonActionSet.openLink(FocusMatrixEditView.this.linkText.getText());
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent selectionEvnt) {
        // Not needed
      }
    });

    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
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
    return ((this.linksDiff && CommonUtils.isEqual(this.newlinkStr, DIFFERENT_VALUE)) ||
        CommonUtils.isEqual(this.oldlinkStr, this.newlinkStr));
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
          FocusMatrixEditView.this.errorDialogDisplayed = false;
          return super.close();
        }
      };
      this.errorDialogDisplayed = true;
      errorDialog.open();

    }
    return true;
  }

  /**
   *
   */
  private void saveChanges() {
    try {
      if (FocusMatrixEditView.this.multiUpdate) {
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
   * this method validates link
   */
  private void validateLink() {
    Validator.getInstance().validateNDecorate(this.linkDecor, this.linkText, false, false);

    if (CommonUtils.isEqual(this.newlinkStr, DIFFERENT_VALUE) || CommonUtils.isEmptyString(this.newlinkStr)) {
      this.linkButton.setEnabled(false);
    }
    else if (linkNotCorrect()) {
      // disable link button and show error decoration
      this.linkButton.setEnabled(false);
      Decorators decorators = new Decorators();
      decorators.showErrDecoration(this.linkDecor, IUtilityConstants.INVALID_LINK, true);
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
    return !CommonUtils.isEmptyString(this.newlinkStr) && !CommonUtils.isValidHyperlinkFormat(this.newlinkStr) &&
        !CommonUtils.isEqual(this.newlinkStr, DIFFERENT_VALUE);
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

    // reset all the buttons
    this.colorRadioBtnGrp.clearAllButtons();

    this.multiUpdate = (this.fetchSelectedUcItems.size() > 1) ||
        (this.fetchSelectedUcItems.get(this.fetchSelectedUcItems.keySet().iterator().next()).size() > 1);

    if (this.multiUpdate) {
      // color code is different and all are not grey( this case will be true if some are grey and some are null)
      setExistingValuesForMultiUpdate(this.colorRadioBtnGrp.oldColorCode.getColor(), grp);
    }
    else {
      if (null != this.colorRadioBtnGrp.getDiffValBtn()) {
        this.colorRadioBtnGrp.getDiffValBtn().dispose();
      }

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
      gridLayoutGrp.numColumns = 6;

      // color code is different and all are not grey( this case will be true if some are grey and some are null)
      this.colorRadioBtnGrp.setDiffValBtn(getFormToolkit().createButton(grp, "<Diff Color>", SWT.RADIO));

      this.colorRadioBtnGrp.getDiffValBtn().addSelectionListener(new SelectionListener() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void widgetSelected(final SelectionEvent arg0) {
          FocusMatrixEditView.this.colorRadioBtnGrp.setDiffValBtnSelcted(true);
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


      this.colorRadioBtnGrp.getDiffValBtn().setSelection(true);
      this.colorRadioBtnGrp.setDiffValBtnSelcted(true);
    }
    else if ((null == colorCodeStr) || allGrey) {
      // if the color codes are not set for all selected items
      this.colorRadioBtnGrp.getWhiteBtn().setSelection(true);
    }
    else {
      setColorCodeRadioButton(FocusMatrixColorCode.getColor(colorCodeStr));
    }
    grp.setLayout(gridLayoutGrp);
    grp.pack(true);
  }

  /**
   * this method validates remarks
   */
  @Override
  public void validateRemarks() {
    Validator.getInstance().validateNDecorate(this.remarkDecor, this.remarksText, false, false);
    if (remarksNotCorrect()) {
      Decorators decorators = new Decorators();
      decorators.showReqdDecoration(this.remarkDecor, "Remark has to be entered!");
    }
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
  @Override
  public void setFocus() {
    // TODO Auto-generated method stub

  }


}
