/**
 *
 */
package com.bosch.caltool.apic.ui.util;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * ApicUiConstants Defines constants used in APIC UI objects - View, Editors, Dialogs
 *
 * @author adn1cob
 */
public final class ApicUiConstants {

  /**
   * Constant defining the pidc view part id
   */
  public static final String PID_TREE_VIEW = "com.bosch.caltool.icdm.common.ui.views.PIDTreeViewPart";
  /**
   * Constant defining the pidc details view part id
   */
  public static final String PID_DETAILS_TREE_VIEW = "com.bosch.caltool.icdm.common.ui.views.PIDCDetailsViewPart";

  /**
   * Constant defining the favorites view part id
   */
  public static final String FAVOURITES_TREE_VIEW = "com.bosch.caltool.icdm.common.ui.views.FavoritesViewPart";

  /**
   * Constant defining the outline view part id
   */
  public static final String OUTLINE_TREE_VIEW = "com.bosch.caltool.icdm.common.ui.views.OutlineViewPart";

  /**
   * Constant defining the favorites view part id
   */
  public static final String SCRATCH_PAD_VIEW = "com.bosch.caltool.icdm.common.ui.views.ScratchPadViewPart";
  /**
   * PIDC History View ID
   */
  public static final String PIDC_HISTORY_VIEW_ID = "com.bosch.caltool.apic.ui.views.PIDCHistoryViewPart";

  /**
   * The constant for DEFAULT level in structure view
   */
  public static final String STRUCTURE_VIEW_DEFAULT_NODE = "<DEFAULT>";

  /**
   * Constant defining the Add value
   */
  public static final String DESC_ADD_NEW_ATTR_VAL = "This is to add a new attribute value";
  /**
   * Constant defining the Add value
   */
  public static final String ADD_VAL = "Add Value";
  /**
   * Constant defining the Add a PIDC
   */
  public static final String ADD_A_PROJ_ID_CARD = "Add a Project ID Card";
  /**
   * Constant defining the Add a PIDC Variant
   */
  public static final String ADD_A_VARIANT = "Add a Variant";
  /**
   * Constant defining the Add a Sub-Variant
   */
  // ICDM-121
  public static final String ADD_A_SUB_VARIANT = "Add a Sub-Variant";
  /**
   * Constant defining the new pidc attribute value
   */
  public static final String DEFINE_NEW_PIDC_ATTR_VAL = "Define New PIDC Attribute Value";
  /**
   * Constant defining pidc with a new name
   */
  // ICDM-94
  public static final String DESC_CREAT_PIDC_NEW_NAME = "Create a PIDC with a new name";
  /**
   * Constant defining variant with a new name
   */
  public static final String CREATE_VAR_NEW_NAME = "Create a Variant with a new name";
  /**
   * Constant defining sub-variant with a new name
   */
  public static final String CREATE_SVAR_NEW_NAME = "Create a Sub-Variant with a new name";
  /**
   * Constant defining pidc with a new name for message in dialog
   */
  public static final String DESC_CREATE_PID_NEW_NAME = "This is to create a PIDC with a new name";
  /**
   * Constant defining variant with a new name for message in dialog
   */
  public static final String DESC_CREATE_VAR_NEW_NAME = "This is to create a Variant with a new name";
  /**
   * Constant defining sub-variant with a new name for message in dialog
   */
  // ICDM-121
  public static final String DESC_CRETE_SVAR_NEW_NAME = "This is to create a Sub-Variant with a new name";
  /**
   * Constant defining the PIDC
   */
  public static final String DEFINE_NEW_PIDC = "Define New Project ID Card";
  /**
   * Constant defining the attribute values
   */
  public static final String ATTR_VALS = "Attribute Values";
  /**
   * Constant defining the pidc names
   */
  public static final String PIDC_NAMES = "PIDC Names";
  /**
   * Constant defining the variant names
   */
  public static final String VARIANT_NAMES = "Variant Names";
  /**
   * Constant defining the sub-variant names
   */
  // ICDM-121
  public static final String SUB_VARIANT_NAMES = "Sub-Variant Names";
  /**
   * Constant defining the description
   */
  public static final String DESCRIPTION = "Description";
  /**
   * Constant defining to add a pidc
   */
  public static final String ADD_A_PIDC = "Add a Project ID Card";

  /**
   * Constant defining to edit pidc attribute value
   */
  public static final String EDIT_PIDC_ATT_VAL = "Edit PIDC Attribute Value";

  /**
   * Constant defining to show the list of pidc attribute values
   */
  public static final String PIDC_ATTR_VALUE_LIST = "List of Attribute Values";
  /**
   * Constant defining the value
   */
  public static final String VALUE = "Value";
  /**
   * Constant defining the name
   */
  public static final String NAME = "Name";
  /**
   * Constant defining the attribute name
   */
  public static final String ATTR_NAME = "Attribute Name : ";
  /**
   * Constant defining the Add PIDC
   */
  public static final String ADD_PRO_ID_CARD = "Add Project ID Card";

  /**
   * Constant defining the delete action
   */
  public static final String DELETE_ACTION = "Delete";
  /**
   * Constant defining the delete action
   */
  public static final String UN_DELETE_ACTION = "Undo Delete";
  /**
   * Constant defining the rename action
   */
  public static final String RENAME_ACTION = "Rename";
  /**
   * Constant edit not allowed
   */
  public static final String EDIT_NOT_ALLOWED = "EDIT not allowed !";
  /**
   * Constant edit not allowed
   */
  public static final String EDIT_NOT_ALWD_MAND_ATR =
      "The Used flag cannot be updated since attribute is defined as mandatory !";
  /**
   * Constant defining add sub-variant
   */
  public static final String ADD_SUB_VARIANT = "Add Sub-Variant";
  // ICDM-2408
  /**
   * Constant defining web flow job
   */
  public static final String START_WEB_FLOW_JOB = "Start webFLOW Job";/* ICDM-2567 - Label change */

  // ICDM-2408
  /**
   * Constant defining Webflow replacable string (element Id)
   */
  public static final String WEB_FLOW_JOB_ELEMENT_ID = "<elementId>";
  // ICDM-158
  /**
   * Constant defining the Copy name
   */
  public static final String COPY = "Copy";
  /**
   * Constant defining the rename Sub-Variant name
   */
  // ICDM-121
  public static final String RENAME_SUB_VARIANT = "Rename Sub-Variant";
  // ICDM-123
  /**
   * Constant defining the move to Sub-Variant name
   */
  public static final String MOVE_TO_SUB_VARIANT = "Move To Sub-Variant";
  /**
   * Constant defining the Sub-Variant already moved name
   */
  public static final String SUB_VARIANT_ALREADY_MOVED = "Sub-Variant already moved";
  /**
   * Constant defining the move to Variant name
   */
  public static final String MOVE_TO_VARIANT = "Move To Variant";

  /**
   * Constant defining the move to Common
   */
  public static final String MOVE_TO_COMMON = "Move To Common";
  /**
   * Constant defining set used flag to yes
   */
  public static final String SET_TO_YES = "Set to \"Yes\"";
  /**
   * Constant defining set used flag to no
   */
  public static final String SET_TO_NO = "Set to \"No\"";
  /**
   * Constant defining set used flag to ???
   */
  public static final String SET_TO_NOT_DEFINED = "Set to \"???\"";
  /**
   * Constant defining the paste name
   */
  public static final String PASTE = "Paste";
  /**
   * Constant defining the paste pidc name
   */
  // ICDM-150
  public static final String PASTE_PIDC = "Paste PIDC";
  /**
   * Constant defining the paste variant name
   */
  public static final String PASTE_VARIANT = "Paste Variant";
  /**
   * Constant defining the paste sub-variant name
   */
  public static final String PASTE_SUB_VARIANT = "Paste Sub-Variant";
  /**
   * Constant defining the paste as new name
   */
  public static final String PASTE_AS_NEW = "Paste As New";
  /**
   * Constant defining the export action
   */
  public static final String EXPORT_ACTION = "Excel Report";
  /**
   * Constant defining the add new user name for message in add new dialog
   */
  public static final String THIS_IS_TO_ADD_NEW_USER = "This is to add new user";
  /**
   * Constant defining the add new user name
   */
  public static final String ADD_NEW_USER = "Add New User";
  /**
   * Constant defining the paste as new value for message in add new dialog
   */
  public static final String DESC_PASTE_NEW_VALUE = "This is to paste new value";
  /**
   * Defines GridViewerColumn index 15
   */
  public static final int COLUMN_INDEX_15 = 15;
  /**
   * Defines GridViewerColumn index 14
   */
  public static final int COLUMN_INDEX_14 = 14;
  /**
   * Defines GridViewerColumn index 13
   */
  public static final int COLUMN_INDEX_13 = 13;
  /**
   * Defines GridViewerColumn index 12
   */
  public static final int COLUMN_INDEX_12 = 12;
  /**
   * Defines GridViewerColumn index 11
   */
  public static final int COLUMN_INDEX_11 = 11;
  // ICDM-179
  /**
   * Defines GridViewerColumn index 10
   */
  public static final int COLUMN_INDEX_10 = 10;
  /**
   * Defines GridViewerColumn index 9
   */
  public static final int COLUMN_INDEX_9 = 9;
  /**
   * Defines GridViewerColumn index 7
   */
  public static final int COLUMN_INDEX_7 = 7;
  /**
   * Defines GridViewerColumn index 6
   */
  public static final int COLUMN_INDEX_6 = 6;
  /**
   * Defines GridViewerColumn index 5
   */
  public static final int COLUMN_INDEX_5 = 5;
  /**
   * Defines GridViewerColumn index 4
   */
  public static final int COLUMN_INDEX_4 = 4;
  /**
   * Defines GridViewerColumn index 3
   */
  public static final int COLUMN_INDEX_3 = 3;
  /**
   * Defines GridViewerColumn index 2
   */
  public static final int COLUMN_INDEX_2 = 2;
  /**
   * Defines GridViewerColumn index 1
   */
  public static final int COLUMN_INDEX_1 = 1;
  /**
   * Defines GridViewerColumn index 0
   */
  public static final int COLUMN_INDEX_0 = 0;

  /**
   * Color for Orange
   */
  public static final Color NOT_CLEARED_VAL_COLOR = new Color(Display.getCurrent(), 255, 128, 0);
  /**
   * Defines GridViewerColumn index 16
   */
  public static final int COLUMN_INDEX_16 = 16;
  /**
   * Defines GridViewerColumn index 17
   */
  public static final int COLUMN_INDEX_17 = 17;
  /**
   * Defines GridViewerColumn index 8
   */
  public static final int COLUMN_INDEX_8 = 8;
  /**
   * pidc history title constant
   */
  public static final String PIDC_HISTORY_TITLE = "PIDC History";

  /**
   * the focus matrix
   */
  // ICDM-2614
  public static final String FOCUS_MATRIX_HISTORY_TITLE = "Focus Matrix History";
  /**
   * PIDC History sub title
   */
  public static final String CHANGE_HISTORY_FOR_PIDC = "Change history of PIDC";

  // iCDM-911
  /**
   * Constant for storing PIDC Details view menu
   */
  public static final String PIDC_DETAILS_SHOW_DELETED = "pidcDetailsShowDeleted";
  // ICDM-2190
  /**
   * Constant for storing PIDC Details view menu
   */
  public static final String PIDC_DETAILS_SHOW_UNCLEARED = "pidcDetailsShowUncleared";
  /**
   * Constant for storing SynchronizePIDC levels action in History view part
   */
  public static final String SYNCHRONIZE_PIDC_LEVELS = "Synchronize PIDC levels";
  /**
   * Constant for storing SynchronizePIDC levels action in History view part
   */
  public static final String REFRESH = "Refresh";

  /**
   * Constant for storing Synchronize attr action in History view part
   */
  public static final String SYNCHRONIZE_ATTRIBUTE = "Synchronize attribute";

  /**
   * iCDM-1345 Group name from PIDC EDITOR graoup in t_messages
   */
  public static final String PIDC_EDITOR_GROUP = ApicConstants.MSGGRP_PIDC_EDITOR;

  /**
   * ICDM-1444 Un delete pidc action string constant
   */
  public static final String UN_DELETE_PIDC_ACTION = "Undo Delete PIDC";

  /**
   * ICDM-1444 delete pidc action string constant
   */
  public static final String DELETE_PIDC_ACTION = "Delete PIDC";


  /**
   * ICDM-1444 rename pidc action string constant
   */
  public static final String RENAME_PIDC_ACTION = "Rename PIDC";
  /**
   * Set Pidc version as Active
   */
  public static final String SET_ACTIVE_VER = "Set as Active";
  /**
   * Edit
   */
  public static final String EDIT = "Edit";
  /**
   * column index constant for 18
   */
  public static final int COLUMN_INDEX_18 = 18;
  /**
   * column index constant for 19
   */
  public static final int COLUMN_INDEX_19 = 19;

  // ICDM-2354
  /**
   *
   */
  public static final String UNLOCK_PIDC_FOR_EDITING = "Unlock PIDC for editing!";

  /**
  *
  */
  public static final String NO_WRITE_ACCESS = "User does not have write access for PIDC: ";

  /**
   * comma constant
   */
  // ICDM-2394
  public static final String COMMA = ", ";

  /**
   * colon constant
   */
  // ICDM-2394
  public static final String COLON = " : ";

  // ICDM-1865
  /**
   * Usecase Node in PIDC Outline page
   */
  public static final String UC_ROOT_NODE = "Use Cases";

  // ICDM-1865
  /**
   * Project Usecase Node in PIDC Outline page
   */
  public static final String PROJECT_UC_ROOT_NODE = "Project Use Cases";

  // ICDM-1865
  /**
   * Private Usecase Node in PIDC Outline page
   */
  public static final String PRIVATE_UC_ROOT_NODE = "Private Use Cases";

  // ICDM-1865
  /**
   * System View Node in PIDC Outline page
   */
  public static final String SYS_VIEW_ROOT_NODE = "System View";
  /**
   *
   */
  public static final String DEFINE_RISK_PIDC_ACTION = "Define Risk";
  /**
   *
   */
  public static final String MONETORY_PRE_SOP_RISK = "Monetary Risk Pre-SOP";
  /**
   *
   */
  public static final String MONETORY_POST_SOP_RISK = "Monetary Risk Post-SOP";
  /**
   *
   */
  public static final String RISK_LAW = "Risk Law";
  /**
   *
   */
  public static final String RISK_REPUTATION = "Risk Reputation";
  /**
   *
   */
  public static final String RISK_SAFETY = "Risk Safety";

  /**
   * Work Packages Nattable config label for user responsible column
   */
  public static final String CONFIG_LABEL_WP_NAME = "WORK_PKG_NAME";
  /**
   * Name at Customer Nattable config label for user responsible column
   */
  public static final String CONFIG_LABEL_WP_NAME_CUST = "CUSTOMER_NAME";
  /**
   * Description Nattable config label for user responsible column
   */
  public static final String CONFIG_LABEL_DESC = "DESCRIPTION";
  /**
   * Gray out Label for disabled NAT table entries
   */
  public static final String CONFIG_LABEL_DISABLE = "NAT_CELL_DISABLE";

  /**
   * Label for Delete column - Non Deletable cell
   */
  public static final String CONFIG_LABEL_NOT_DELETABLE = "NOT_DELETABLE_CELL";

  /**
   * Label for checkBox Editor
   */
  public static final String CHECK_BOX_EDITOR_CNG_LBL = "checkBoxEditor";
  /**
   * Label for checkBox
   */
  public static final String CHECK_BOX_CONFIG_LABEL = "checkBox";
  /**
   * Label to hide chcekbox
   */
  public static final String EMPTY_LABEL = "empty label";

  /**
   * PIDCEditor : Emission Robustness : EMR_ASSIGN_VARIANT
   */
  public static final String EMR_ASSIGN_VARIANT = "Assign Variant";

  /**
   * PIDCEditor : Emission Robustness : EMR_MODIFY_DESC
   */
  public static final String EMR_MODIFY_DESC = "Modify Description";

  /**
   * PIDCEditor : Emission Robustness : EMR_OPEN_EXCEL
   */
  public static final String EMR_OPEN_EXCEL = "Open Excel File";

  /**
   * PIDCEditor : Emission Robustness : EMR_CHK_UPLOAD_PROTOCOL
   */
  public static final String EMR_CHK_UPLOAD_PROTOCOL = "Check Upload Protocol";

  /**
   * Constant defining whether attribute values can be added by pidc users
   */
  public static final String VALUE_CAN_BE_ADDED_BY_USERS = "Values can be created by users";
  /**
   * Tooltip text for Project Attribute Value edit Icon
   */
  public static final String TOOLTIP_PROJ_ATTR_EDIT =
      "Edit project attribute. Set the attribute value, additional info";

  /**
   * Filter extensions for Zip file
   */
  public static final String[] ZIP_FILE_FILTER_EXTN = new String[] { "*.zip" };

  /**
   * message that score cannot be set
   */
  public static final String MSG_CANNOT_CHANGE_SCORE =
      "Comments are mandatory to set score 9 for COMPLI/Q-SSD failure/Unfulfilled rules parameters.";

  /**
   * message that score cannot be set
   */
  public static final String MSG_CANNOT_CHANGE_COMMENT =
      "Comment cannot be set to empty.\nFor COMPLI/Q-SSD failure/Unfulfilled rules parameters with score 9, comments are mandatory.";

  /**
   * Config label for COC WP header
   */
  public static final String COC_WP_HEADER_LABEL = "COC_WP_HEADER";

  /**
   * Config label for deleted coc wp
   */
  public static final String DELETED_COC_WP_LABEL = "DELETED_COC_WP";
  /**
   * Filter Extension for Cdfx File
   */
  public static final String[] CDFX_FILE_FILTER_EXTN = new String[] { "*.cdfx" };
  /**
   * Cdfx File Extension
   */
  public static final Object CDFX_FILE_EXT = ".cdfx";

  /**
   * Private constructor
   */
  private ApicUiConstants() {
    // private constructor
  }

}
