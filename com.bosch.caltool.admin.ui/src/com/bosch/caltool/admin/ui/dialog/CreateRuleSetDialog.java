/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.admin.ui.dialog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.caltool.admin.ui.Activator;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.dialogs.UserSelectionDialog;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.cdr.RuleSetInputData;
import com.bosch.caltool.icdm.model.cdr.RuleSetOutputData;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ruleseditor.actions.ReviewActionSet;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditorInput;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RuleSetServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.text.TextUtil;

/**
 * @author say8cob
 */
public class CreateRuleSetDialog extends TitleAreaDialog {

  /**
   * * CONSTANT FOR "null,null"
   */
  public static final String STR_NULL_NULL = "null,null";

  /**
   * Dialog title
   */
  private static final String DIALOG_TITLE = "Create Rule Set";

  /**
   * mandotary field decorator
   */
  private static final String DESC_TXT_MANDATORY = "This field is mandatory.";

  /**
   * Create button Constant
   */
  public static final String CREATE_BUTTON_CONSTANT = "Create";

  /**
   * Minimum width of the dialog
   */
  private static final int DIALOG_MIN_WIDTH = 100;

  /**
   * Minimum height of the dialog
   */
  private static final int DIALOG_MIN_HEIGHT = 50;

  /**
  *
  */
  private static final int LIST_ROW_COUNT = 7;

  private FormToolkit formToolkit;

  private Text ruleSetName;

  private Text ruleSetDesc;

  /**
   * create ruleset button
   */
  private Button createBtn;

  /**
   * List of other ruleSetOwner
   */
  private List ruleSetOwnerList;

  private Button ownerDelButton;

  private Button addOwnersBtn;

  /**
   * Array of selected ruleSetOwner to be removed
   */
  private String[] selOwnerToRemove;

  /**
   * Sorted set of selected RuleSet Owners
   */
  private final SortedSet<User> selRuleSetOwners = new TreeSet<>();

  private Button openRuleEditorChkBox;

  private Button addCurrentUserAsOwner;

  /**
   * @param parentShell as input
   */
  public CreateRuleSetDialog(final Shell parentShell) {
    super(parentShell);
  }

  /**
   * Configures the shell {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText(DIALOG_TITLE);
    super.configureShell(newShell);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);

    // Set title
    setTitle(DIALOG_TITLE);
    // Set the message
    setMessage("Fill the Rule Set name, description and owners");

    return contents;
  }

  /**
   * Allow resizing
   * <p>
   * {@inheritDoc}
   */
  @Override
  protected boolean isResizable() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
//   set grid for the dialog
    final Composite composite = (Composite) super.createDialogArea(parent);
    composite.setLayout(new GridLayout());

    final GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.minimumWidth = DIALOG_MIN_WIDTH;
    gridData.minimumHeight = DIALOG_MIN_HEIGHT;
    composite.setLayoutData(gridData);

    createMainComposite(composite);

    return composite;
  }

  /**
   * Create the main composite and its contents
   *
   * @param composite
   */
  private void createMainComposite(final Composite composite) {
    final Composite mainComposite = getFormToolkit().createComposite(composite);
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;
    mainComposite.setLayout(gridLayout);
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    mainComposite.setLayoutData(gridData);
    // Create text field for RuleSet Name
    createRuleSetNameTextField(mainComposite);
    // Create text field for RuleSet Desc
    createRuleSetDescTextField(mainComposite);
    // Create selection area for RuleSet owners
    createOwnersControl(mainComposite);
    // create add cureent user field
    createAddCurrentUserAsOwnerField(mainComposite);
    // create open rule editor fields
    createOpenRuleEditorField(mainComposite);


  }

  /**
   * @param composite
   */
  private void createAddCurrentUserAsOwnerField(final Composite composite) {
    createLabelControl(composite, "");
    this.addCurrentUserAsOwner = new Button(composite, SWT.CHECK);
    this.addCurrentUserAsOwner.setText("Add current user as Owner");
    this.addCurrentUserAsOwner.setSelection(false);
    this.addCurrentUserAsOwner.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    this.addCurrentUserAsOwner.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        try {
          User currentUser = new CurrentUserBO().getUser();
          String currentUserName = currentUser.getDescription();
          if (CreateRuleSetDialog.this.addCurrentUserAsOwner.getSelection()) {
            if ((CreateRuleSetDialog.this.ruleSetOwnerList.indexOf(currentUserName) == -1) &&
                (!STR_NULL_NULL.equalsIgnoreCase(currentUserName))) {
              CreateRuleSetDialog.this.selRuleSetOwners.add(currentUser);
              CreateRuleSetDialog.this.ruleSetOwnerList.add(currentUserName);
            }
          }
          else {
            CreateRuleSetDialog.this.selRuleSetOwners.remove(currentUser);
            CreateRuleSetDialog.this.ruleSetOwnerList.remove(currentUserName);
          }
          enableOkButton();
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
        }
      }
    });

    createLabelControl(composite, "");
  }

  /**
   * @param mainComposite
   */
  private void createOpenRuleEditorField(final Composite comp) {
    createLabelControl(comp, "");
    this.openRuleEditorChkBox = new Button(comp, SWT.CHECK);
    this.openRuleEditorChkBox.setText("Open Rule Set in Editor");
    this.openRuleEditorChkBox.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    this.openRuleEditorChkBox.setSelection(true);
    createLabelControl(comp, "");
  }

  /**
   * Creates a label
   *
   * @param compparent composite
   * @param lblName label text
   */
  private void createLabelControl(final Composite comp, final String lblName) {
//    form field label alignment
    final GridData gridData = new GridData();
    gridData.verticalAlignment = SWT.TOP;
    LabelUtil.getInstance().createLabel(this.formToolkit, comp, lblName);
  }

  /**
   * Creates a text field
   *
   * @param comp parent composite
   * @return Text the new field
   */
  private Text createTextField(final Composite comp) {
//    text field and its alignment
    final Text text = TextUtil.getInstance().createEditableText(this.formToolkit, comp, false, "");
    final GridData widthHintGridData = new GridData();
    widthHintGridData.horizontalAlignment = GridData.FILL;
    widthHintGridData.grabExcessHorizontalSpace = true;
    text.setLayoutData(widthHintGridData);
    return text;
  }

  /**
   * Create Execution Id text field
   *
   * @param comp parent composite
   */
  private void createRuleSetNameTextField(final Composite comp) {
    createLabelControl(comp, "Rule Set Name");
    this.ruleSetName = createTextField(comp);
    this.ruleSetName.setEnabled(true);
    this.ruleSetName.setEditable(true);
    this.ruleSetName.addModifyListener(e -> enableOkButton());

    ControlDecoration decorator = new ControlDecoration(this.ruleSetName, SWT.LEFT | SWT.TOP);
    decorator.setDescriptionText(DESC_TXT_MANDATORY);
    FieldDecoration fieldDecoration =
        FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_REQUIRED);
    decorator.setImage(fieldDecoration.getImage());
    decorator.show();
    createLabelControl(comp, "");
  }

  /**
   * Create Execution Id text field
   *
   * @param comp parent composite
   */
  private void createRuleSetDescTextField(final Composite comp) {
    createLabelControl(comp, "Description");
    this.ruleSetDesc = createTextField(comp);
    this.ruleSetDesc.setEnabled(true);
    this.ruleSetDesc.setEditable(true);
    this.ruleSetDesc.addModifyListener(e -> enableOkButton());

    ControlDecoration decorator = new ControlDecoration(this.ruleSetDesc, SWT.LEFT | SWT.TOP);
    decorator.setDescriptionText(DESC_TXT_MANDATORY);
    FieldDecoration fieldDecoration =
        FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_REQUIRED);
    decorator.setImage(fieldDecoration.getImage());
    decorator.show();
    createLabelControl(comp, "");
  }

  /**
   * @param workArea parent Composite
   */
  private void createOwnersControl(final Composite comp) {
    createLabelControl(comp, "Owners");
    this.ruleSetOwnerList = new List(comp, SWT.V_SCROLL | SWT.MULTI | SWT.BORDER);
    final GridData data = new GridData(GridData.FILL_HORIZONTAL);
    data.heightHint = LIST_ROW_COUNT * this.ruleSetOwnerList.getItemHeight();
    this.ruleSetOwnerList.setLayoutData(data);

    // participantBtComposite for aligining the participants selection and delete buttons
    final Composite participantBtComp = new Composite(comp, SWT.NONE);
    final GridLayout layoutPartComp = new GridLayout();
    layoutPartComp.numColumns = 1;
    layoutPartComp.makeColumnsEqualWidth = false;
    layoutPartComp.marginWidth = 0;
    layoutPartComp.marginTop = 0;
    participantBtComp.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
    participantBtComp.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
    participantBtComp.setLayout(layoutPartComp);

    ControlDecoration decorator = new ControlDecoration(this.ruleSetOwnerList, SWT.LEFT | SWT.TOP);
    decorator.setDescriptionText(DESC_TXT_MANDATORY);
    FieldDecoration fieldDecoration =
        FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_REQUIRED);
    decorator.setImage(fieldDecoration.getImage());
    decorator.show();
    // add and delete participants button
    createOwnerButtons(participantBtComp);
    createRuleSetOwnerListeners();
  }

  /**
   * @param ownerBtComp child composite
   */
  private void createOwnerButtons(final Composite ownerBtComp) {
    addButton(ownerBtComp);
    delButton(ownerBtComp);
  }

  private void createRuleSetOwnerListeners() {
    ruleSetOwnerListListener();
    ownerDelBtnListener();
    addOwnerBtnListener();
  }

  /**
   *
   */
  private void addOwnerBtnListener() {
    this.addOwnersBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final UserSelectionDialog partsDialog = new UserSelectionDialog(Display.getCurrent().getActiveShell(),
            "Add User", "Add user", "Add user", "Add", true, false);
        partsDialog.setSelectedMultipleUser(null);
        partsDialog.open();
        ArrayList<User> selectedUsers = (ArrayList<User>) partsDialog.getSelectedMultipleUser();
        if ((selectedUsers != null) && !selectedUsers.isEmpty()) {
          final Iterator<User> users = selectedUsers.iterator();
          while (users.hasNext()) {
            final User selectedUser = users.next();
            final String selUserName = selectedUser.getDescription();
            if ((CreateRuleSetDialog.this.ruleSetOwnerList.indexOf(selUserName) == -1) &&
                (!STR_NULL_NULL.equalsIgnoreCase(selUserName))) {
              CreateRuleSetDialog.this.selRuleSetOwners.add(selectedUser);
              CreateRuleSetDialog.this.ruleSetOwnerList.add(selUserName);
              checkIfCurrentUser(selUserName, true);
            }
          }
          // sorting of list items
          final String[] items = CreateRuleSetDialog.this.ruleSetOwnerList.getItems();
          java.util.Arrays.sort(items);
          CreateRuleSetDialog.this.ruleSetOwnerList.setItems(items);
        }
        enableOkButton();
      }
    });
  }

  /**
   *
   */
  private void ownerDelBtnListener() {
    this.ownerDelButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {

        if ((getSelOwnerToRemove() != null) && (getSelOwnerToRemove().length != 0)) {
          for (String partiName : getSelOwnerToRemove()) {
            final Iterator<User> participants = CreateRuleSetDialog.this.selRuleSetOwners.iterator();
            CreateRuleSetDialog.this.ruleSetOwnerList.remove(partiName);
            while (participants.hasNext()) {
              final User user = participants.next();
              final String userName = user.getDescription();
              if (userName.equalsIgnoreCase(partiName)) {
                checkIfCurrentUser(userName, false);
                participants.remove();
                break;
              }
            }
          }
        }
        clearRuleSetOwnerList();
      }
    });
  }

  /**
   * @param userName user name
   * @param checkBoxSelection true/false
   */
  private void checkIfCurrentUser(final String userName, final boolean checkBoxSelection) {
    String crtUserName = getCurrentUser().getDescription();
    if ((crtUserName != null) && crtUserName.equals(userName)) {
      CreateRuleSetDialog.this.addCurrentUserAsOwner.setSelection(checkBoxSelection);
    }
  }

  private User getCurrentUser() {
    User user = new User();
    try {
      user = new CurrentUserBO().getUser();
      return user;
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    return user;
  }

  /**
   *
   */
  private void clearRuleSetOwnerList() {
    if (CreateRuleSetDialog.this.ruleSetOwnerList.getItemCount() == 0) {
      CreateRuleSetDialog.this.selRuleSetOwners.clear();
    }
    CreateRuleSetDialog.this.ownerDelButton.setEnabled(false);
    enableOkButton();
  }

  /**
   *
   */
  private void ruleSetOwnerListListener() {
    if (this.ruleSetOwnerList != null) {

      ruleSetOwnerListSelectionListener();

      ruleSetOwnerListKeyListener();

    }
  }

  /**
   *
   */
  private void ruleSetOwnerListKeyListener() {
    this.ruleSetOwnerList.addKeyListener(new KeyAdapter() {

      @Override
      public void keyPressed(final KeyEvent event) {

        final char character = event.character;
        if (character == SWT.DEL) {
          if ((getSelOwnerToRemove() != null) && (getSelOwnerToRemove().length != 0)) {
            for (String partiName : getSelOwnerToRemove()) {
              CreateRuleSetDialog.this.ruleSetOwnerList.remove(partiName);
              final Iterator<User> othParticipants = CreateRuleSetDialog.this.selRuleSetOwners.iterator();
              removeRuleSetOwnersList(partiName, othParticipants);
            }
          }
          clearRuleSetOwnerList();
        }

      }

      /**
       * @param partiName
       * @param othParticipants
       */
      private void removeRuleSetOwnersList(final String partiName, final Iterator<User> othParticipants) {
        while (othParticipants.hasNext()) {
          final User user = othParticipants.next();
          final String userName = user.getDescription();
          if (userName.equalsIgnoreCase(partiName)) {
            othParticipants.remove();
            break;
          }
        }
      }
    });
  }

  /**
   *
   */
  private void ruleSetOwnerListSelectionListener() {
    this.ruleSetOwnerList.addListener(SWT.Selection, event -> {
      CreateRuleSetDialog.this.ownerDelButton.setEnabled(true);
      setSelOwnerToRemove(CreateRuleSetDialog.this.ruleSetOwnerList.getSelection());
    });
  }

  /**
   * @param ownerBtComp
   */
  private void delButton(final Composite ownerBtComp) {
    final Image deleteButtonImage = ImageManager.INSTANCE.getRegisteredImage(ImageKeys.DELETE_16X16);
    this.ownerDelButton = new Button(ownerBtComp, SWT.PUSH);
    this.ownerDelButton.setImage(deleteButtonImage);
    this.ownerDelButton.setEnabled(false);

  }


  /**
   * @param ownerBtComp
   * @param addButtonImage
   */
  private void addButton(final Composite ownerBtComp) {
    final Image addButtonImage = ImageManager.INSTANCE.getRegisteredImage(ImageKeys.ADD_16X16);
    this.addOwnersBtn = new Button(ownerBtComp, SWT.PUSH);
    this.addOwnersBtn.setImage(addButtonImage);
  }

  /**
   * @return FormToolkit
   */
  private FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }

  /**
   * Enable OK button only if execution id is provided
   */
  private void enableOkButton() {
    if (null != this.createBtn) {
      this.createBtn.setEnabled(CommonUtils.isNotEmptyString(this.ruleSetName.getText()) &&
          CommonUtils.isNotEmptyString(this.ruleSetDesc.getText()) && CommonUtils.isNotEmpty(getSelRuleSetOwners()));
    }
  }

  /**
   * creating import button {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.createBtn = createButton(parent, IDialogConstants.OK_ID, CREATE_BUTTON_CONSTANT, false);
    this.createBtn.setEnabled(CommonUtils.isNotEmptyString(this.ruleSetName.getText()) &&
        CommonUtils.isNotEmptyString(this.ruleSetDesc.getText()) && CommonUtils.isNotEmpty(getSelRuleSetOwners()));
    createButton(parent, IDialogConstants.CANCEL_ID, "Cancel", false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    try {
      // dummy ruleset creation
      RuleSet inputRuleSet = new RuleSet();
      inputRuleSet.setName(getRuleSetName().getText().trim());
      inputRuleSet.setDescription(getRuleSetDesc().getText().trim());

      RuleSetInputData ruleSetModel = new RuleSetInputData();
      ruleSetModel.setRuleSetOwnerIds(getSelRuleSetOwnerIds());
      ruleSetModel.setRuleSet(inputRuleSet);

      RuleSetOutputData createdRuleSet = new RuleSetServiceClient().create(ruleSetModel);

      if (CommonUtils.isNotNull(createdRuleSet.getRuleSet())) {
        StringBuilder infoMsg = new StringBuilder();
        infoMsg.append("Rule Set is created and Owner access is given for the following users");
        for (User user : getSelRuleSetOwners()) {
          infoMsg.append("\n");
          infoMsg.append(user.getDescription());
        }
        // displaying info message
        CDMLogger.getInstance().infoDialog(infoMsg.toString(), Activator.PLUGIN_ID);
        // opening ruleset editor
        if (this.openRuleEditorChkBox.getSelection()) {
          super.okPressed();
          new ReviewActionSet().openRulesEditor(new ReviewParamEditorInput(createdRuleSet.getRuleSet()), null);
        }
        super.okPressed();
      }
    }
    catch (ApicWebServiceException | PartInitException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }

  private Set<Long> getSelRuleSetOwnerIds() {
    SortedSet<Long> selRuleSetOwnerIds = new TreeSet<>();
    for (User user : getSelRuleSetOwners()) {
      selRuleSetOwnerIds.add(user.getId());
    }
    return selRuleSetOwnerIds;
  }


  /**
   * @return the selOwnerToRemove
   */
  public String[] getSelOwnerToRemove() {
    return this.selOwnerToRemove;
  }


  /**
   * @param selOwnerToRemove the selOwnerToRemove to set
   */
  public void setSelOwnerToRemove(final String[] selOwnerToRemove) {
    this.selOwnerToRemove = selOwnerToRemove;
  }


  /**
   * @return the ruleSetName
   */
  public Text getRuleSetName() {
    return this.ruleSetName;
  }


  /**
   * @param ruleSetName the ruleSetName to set
   */
  public void setRuleSetName(final Text ruleSetName) {
    this.ruleSetName = ruleSetName;
  }


  /**
   * @return the ruleSetDesc
   */
  public Text getRuleSetDesc() {
    return this.ruleSetDesc;
  }


  /**
   * @param ruleSetDesc the ruleSetDesc to set
   */
  public void setRuleSetDesc(final Text ruleSetDesc) {
    this.ruleSetDesc = ruleSetDesc;
  }


  /**
   * @return the selRuleSetOwners
   */
  public SortedSet<User> getSelRuleSetOwners() {
    return this.selRuleSetOwners;
  }

}
