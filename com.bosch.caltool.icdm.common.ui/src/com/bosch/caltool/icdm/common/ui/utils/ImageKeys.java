/*
 * ' * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.utils;

import java.io.File;


/**
 * ICDM-948
 *
 * @author bru2cob
 */
public enum ImageKeys {


                       /**
                        * Connection enabled<br>
                        * Source - https://materialdesignicons.com/
                        */
                       CONNECTED_16X16("connected.png"),
                       /**
                        * Connection not available
                        */
                       DISCONNECTED_16X16("disconnected.gif"),
                       /**
                        * Reconnecting
                        */
                       RECONNECTING_16X16("reconnecting.gif"),
                       /**
                        * ICDM Logo small size
                        */

                       ICDM_LOGO_16X16("icdm-logo-16x16.gif"),
                       /**
                        * ICDM Link small size
                        */
                       ICDM_LINK_16X16("icdm-link-16x16.png"),
                       /**
                        * Support DashBoard link<br>
                        * source : Created internally
                        */
                       SUPPORT_DASHBOARD_16X16("Support_Dashboard_link.png"),
                       /**
                        * ICDM OSS Document. source : https://materialdesignicons.com
                        */
                       ICDM_OSS_16X16("file-certificate.png"),
                       /**
                        * Login
                        */
                       LOG_IN_16X16("sign-in.png"),
                       /**
                        * Checkbox yes
                        */
                       CHECKBOX_YES_16X16("checkbox_yes.png"),
                       /**
                        * Checkbox no
                        */
                       CHECKBOX_NO_16X16("checkbox_no.png"),
                       /**
                        * Checkbox readonly
                        */
                       CHECKBOX_READONLY_16X16("checkbox-readonly.png"),
                       /**
                        * Icon for pver
                        */
                       PVER_16X16("pver.gif"),
                       /**
                        * Icon for Pidc
                        */
                       PIDC_16X16("pidc_obj.gif"),
                       /**
                        * Icon for compare editor
                        */
                       COMPARE_EDITOR_16X16("compare_editor.png"),
                       /**
                        * Icon for Robert Bosch Resp Type
                        */
                       ROBERT_BOSCH_RESP_16X16("robertBosch16x16.png"),
                       /**
                        * Icon for Customer Resp Type
                        */
                       CUSTOMER_RESP_16X16("customer16x16.png"),
                       /**
                        * Icon for Others Resp Type
                        */
                       OTHER_RESP_16X16("other16x16.png"),
                       /**
                        * Icon for Add to compare editor
                        */
                       COMPARE_EDITOR_ADD_16X16("compare_editor_add.png"),
                       /**
                        * Icon for CalDataPhyValue
                        */
                       VALUE_16X16("Value.gif"),
                       /**
                        * Icon for CalDataPhyValBlk
                        */
                       VALBLK_16X16("ValBlk.gif"),
                       /**
                        * Icon for CalDataPhyAscii
                        */
                       ASCII_16X16("ASCII.gif"),
                       /**
                        * Icon for CalDataPhyAxisPts
                        */
                       AXIS_16X16("AxisPts.gif"),
                       /**
                        * Icon for CalDataPhyCurve
                        */
                       CURVE_16X16("Curve.gif"),
                       /**
                        * Icdm-1073 Icon for CalDataPhyCurve
                        */
                       TICK_16X16("TickMark.png"),
                       /**
                        * Icon for qnaire response answer<br>
                        * Source - https://materialdesignicons.com/
                        */
                       NEUTRAL_ICON_16X16("neutral_icon.png"),
                       /**
                        * Icon for qnaire response answer<br>
                        * Source - https://materialdesignicons.com/
                        */
                       NEGATIVE_ICON_16X16("negative_icon.png"),
                       // ICDM-2439
                       /**
                        * Icon for Compliance parameter<br>
                        * Source- Created using GIMP
                        */
                       PARAM_TYPE_COMPLIANCE_16X16("OrangeExclamation.png"),
                       // ICDM-2439
                       /**
                        * Icon for Non Compliance parameter<br>
                        * Source- Created using GIMP
                        */
                       PARAM_TYPE_NON_COMPLIANCE_16X16("OrangeExclamationCross.png"),
                       /**
                        * Icon for CalDataPhyMap
                        */
                       MAP_16X16("Map.gif"),
                       /**
                        * Icon for CalDataPhyBoolean
                        */
                       BOOLEAN_16X16("Boolean.gif"),
                       /**
                        * Icon for A2lFile
                        */
                       A2LFILE_16X16("a2lfile.gif"),
                       /**
                        * Icon for Function
                        */
                       FUNCTION_28X30("function.gif"),
                       /**
                        * Icon for variant
                        */
                       VARIANT_28X30("variant.gif"),
                       /**
                        * Icon for sub-variant
                        */
                       SUBVAR_28X30("sub_variant.gif"),
                       /**
                        * Icon for attribute value
                        */
                       ATTRVAL_16X16("att_value_obj.gif"),
                       /**
                        * Icon for link decorator
                        */
                       LINK_DECORATOR_12X12("hyperLink.png"),

                       /**
                        * ICon for PIDC root
                        */
                       PIDC_ROOT_28X30("attr_root_obj.gif"),
                       /**
                        * Icon for PIDC node
                        */
                       PIDC_NODE_28X30("pidc_node.gif"),
                       /**
                        * Icon for PIDC deleted<br>
                        * Source - https://materialdesignicons.com/
                        */
                       PIDC_DEL_8X8("pidc_obj_deleted.png"),
                       /**
                        * Icon for Review result<br>
                        * Source - https://materialdesignicons.com/
                        */
                       RELEASE_28X30("Release.png"),
                       /**
                        * Icon for workPackage
                        */
                       WP_28X30("work_package.gif"),
                       /**
                        * Icon for review result closed
                        */
                       RVW_RES_CLOSED_16X16("review_result_green.png"),
                       /**
                        * Icon for review result closed
                        */
                       RVW_RES_LINK_VAR_16X16("review_result_linkvar.png"),
                       /**
                        * Icon for main review result
                        */
                       RVW_RES_MAIN_16X16("review_result_open.png"),
                       /**
                        * Icon for BC
                        */
                       BC_16X16("bc_icon.png"),
                       /**
                        * Icon for WP group
                        */
                       WP_GROUP_28X30("work_package_group.gif"),
                       /**
                        * Icon for comp pkg
                        */
                       CMP_PKG_16X16("component_pkgs.png"),

                       /**
                        * Icon for comp pkg<br>
                        * Source- http://eclipse-icons.i24.cc/ovr16/stckframe_obj.gif
                        */
                       RULE_SET_16X16("rule_set.png"),
                       /**
                        * Icon for common
                        */
                       COMMON_28X30("common.gif"),
                       /**
                        * Icon for WP root
                        */
                       WP_ROOT_28X30("wp_root.gif"),
                       /**
                        * Icon for param
                        */
                       PARAM_28X30("Parameter.gif"),
                       /**
                        * Icon for vCDM
                        */
                       VCDM_16X16("vCDM.png"),
                       /**
                        * Icon for list file<br>
                        * Source- http://eclipse-icons.i24.cc/eclipse-icons-02.html
                        */
                       LIST_FILE_28X30("list_file.gif"),
                       /**
                        * Icon for delete
                        */
                       DELETE_16X16("delete.gif"),
                       /**
                        * Icon for fc-bc
                        */
                       FC_BC_USAGE_16X16("fc_bc_usage.gif"),
                       /**
                        * Icon for hyper_link
                        */
                       HYPER_LINK_16X16("hyperLink_16x16.png"),
                       /**
                        * Icon for stracth pad
                        */
                       SCRATCH_PAD_16X16("scratch_pad.png"),
                       /**
                        * Icon for review rules
                        */
                       REVIEW_RULES_16X16("review_rules.png"),
                       /**
                        * Icon for default review rules
                        */
                       DEFAULT_REVIEW_RULES_16X16("default_review_rules.png"),
                       /**
                        * Icon for series statistics
                        */
                       SERIES_STATISTICS_16X16("series_obj.gif"),
                       /**
                        * Icon for tree collapse
                        */
                       TREE_COLLAPSE_16X16("btn_collapse.gif"),
                       /**
                        * Icon for tree expand
                        */
                       TREE_EXPAND_16X16("expandall.gif"),
                       /**
                        * Icon for deleting from stracth pad
                        */
                       REMOVE_16X16("remove_obj.gif"),
                       /**
                        * Icon for not baselined qnaire response
                        */
                       NOT_BASELINED_16X16("Not_Baselined.png"),
                       /**
                        * Icon for exporting cdfx,cmp pkg<br>
                        * Source- http://eclipse-icons.i24.cc/eclipse-icons-02.html
                        */
                       EXPORT_DATA_16X16("exportpref_obj.gif"),

                       /**
                        * Icon for data assessment option in the A2L context menu
                        */
                       DATA_ASSESSMENT_16X16("data_assessment.png"),

                       /**
                        * Icon for data assessment report overview page - check
                        */
                       DATA_ASSESSMENT_CHECK("check.png"),

                       /**
                        * Icon for data assessment report overview page - times
                        */
                       DATA_ASSESSMENT_TIMES("times.png"),

                       /**
                        * Icon for help<br>
                        * Source - https://materialdesignicons.com/
                        */
                       HELP_24X24("help.png"),

                       /**
                        * Icon for help source: https://git.eclipse.org/c/platform/eclipse.platform.images.git/
                        */
                       HELP_16X16("help_16x16.png"),

                       /**
                        * Icon for compare report
                        */
                       COMPARE_REPORT_16X16("compare.gif"),
                       /**
                        * Icon for copy
                        */
                       COPY_16X16("copy_edit.gif"),
                       /**
                        * Icon for paste
                        */
                       PASTE_16X16("paste_edit.gif"),

                       /**
                        * Icon for super group folder
                        */
                       SUP_GRP_28X30("supergroup_folder.png"),
                       /**
                        * Icon for group green
                        */
                       GROUP_GREEN_28X30("group_green.gif"),
                       /**
                        * Icon for usecase group deleted
                        */
                       UC_GRP_DEL_28X30("usecase_group_deleted.gif"),
                       /**
                        * Icon for use case group
                        */
                       UC_GRP_28X30("usecase_group.gif"),
                       /**
                        * Icon for use case deleted
                        */
                       UC_DEL_28X30("usecase_deleted.gif"),
                       /**
                        * Icon for usecase
                        */
                       UC_28X30("usecase.png"),

                       /**
                        * Icon for non-usecase toolbar filter Source - https://materialdesignicons.com/
                        */
                       NON_UC_16X16("NON_UC_16X16.png"),

                       /**
                        * Icon for usecase toolbar filter Source - https://materialdesignicons.com/
                        */
                       UC_16X16("UC_16X16.png"),
                       /**
                        * Icon for usecase toolbar filter<br>
                        * Source - https://materialdesignicons.com/
                        */
                       UC_14X14("14X14-uc-icon.png"),
                       /**
                        * Icon for usecase section
                        */
                       UCS_28X30("usecase_section.gif"),
                       /**
                        * Icon for usecase section deleted
                        */
                       UCS_DEL_28X30("usecase_section_deleted.gif"),
                       /**
                        * Icon for previous serach results of series statistics
                        */
                       SERIES_STAT_RESULT_16X16("series_stat_result.gif"),
                       /**
                        * Warning icon<br>
                        * Source -https://materialdesignicons.com/
                        */
                       WARNING_16X16("warning.png"),
                       /**
                        * Icon for variant value selection in comp pkg wizard<br>
                        * Source- http://eclipse-icons.i24.cc/ovr16/goto_input.gif
                        */
                       VARIANT_VALUE_16X16("select_value.png"),
                       /**
                        * Icon for DCM upload<br>
                        * Source- http://eclipse-icons.i24.cc/eclipse-icons-02.html
                        */
                       DCM_UPLOAD_28X30("dcm_upload.gif"),
                       /**
                        * Icon for function folder
                        */
                       FUNCTION_FOLDER_28X30("function_folder.gif"),
                       /**
                        * Icon for function search
                        */
                       FUNC_SEARCH_28X30("Search.gif"),
                       /**
                        * Icon for add<br>
                        * Source - https://materialdesignicons.com/
                        */
                       ADD_16X16("add.png"),
                       /**
                        * Icon for edit<br>
                        * Source - https://materialdesignicons.com/
                        */
                       EDIT_16X16("edit.png"),
                       /**
                        * Icon for edit
                        */
                       EDIT_BLACK_16X16("edit_black_16X16.png"),
                       /**
                        * Icon for edit<br>
                        * Source - https://materialdesignicons.com/
                        */
                       EDIT_12X12("edit_12X12.png"),
                       /**
                        * Icon for dowmload<br>
                        * Source - https://materialdesignicons.com/
                        */
                       DOWNLOAD_16X16("Download.png"),
                       /**
                        * Icon to start cdr review
                        */
                       START_CDRREVIEW_16X16("start_cdreview.gif"),
                       /**
                        * Icon for delta review
                        */
                       DELTA_REVIEW_28X30("delta_review_16.gif"),
                       /**
                        * Icon to clear all checkbox
                        */
                       CLEAR_CHECKBOX_20X18("clear_checkbox.png"),
                       /**
                        * Icon for review data
                        */
                       REVIEW_DATA_16X16("review_data.jpg"),
                       /**
                        * Icon for Hide similar in compare editor
                        */
                       HIDE_SIMILAR_16X16("hide_similar.png"),
                       /**
                        * Icon for history view
                        */
                       HISTORY_VIEW_16X16("history_view.gif"),
                       /**
                        * Icon for mark<br>
                        * Source- http://eclipse-icons.i24.cc/eclipse-icons-02.html
                        */
                       MARK_16X16("mark.png"),
                       /**
                        * Icon for unmark<br>
                        * Source- http://eclipse-icons.i24.cc/ovr16/disabled_co.gif
                        */
                       UNMARK_16X16("unmark.png"),
                       /**
                        * Icon for comment
                        */
                       COMMENT_16X16("comment.png"),
                       /**
                        * Icon for screw
                        */
                       SCREW_28X30("screw.gif"),
                       /**
                        * Icon for nail
                        */
                       NAIL_28X30("nail.gif"),
                       /**
                        * Icon for rivet
                        */
                       RIVET_28X30("rivet.gif"),
                       /**
                        * Icon for undefined - source - Bosch image library
                        */
                       UNDEFINED_16X16("Question.png"),
                       /**
                        * Icon for yes
                        */
                       YES_28X30("yes-icon.gif"),
                       /**
                        * Icon for no<br>
                        * Source - https://materialdesignicons.com/
                        */
                       NO_28X30("no-icon.png"),
                       /**
                        * Icon for manual
                        */
                       MANUAL_16X16("manual.jpg"),
                       /**
                        * Icon for automatic
                        */
                       AUTOMATIC_16X16("automatic.jpg"),
                       /**
                        * Icon for rule exists
                        */
                       RULE_EXIST_16X16("RuleExists.jpg"),
                       /**
                        * Icon for no rules exists
                        */
                       NO_RULES_EXIST_16X16("NoRuleExists.jpg"),
                       /**
                        * Icon for rule complete
                        */
                       RULE_COMPLETE_16X16("rulecomplete.png"),
                       /**
                        * Icon for no rule incomplete
                        */
                       RULE_INCOMPLETE_16X16("ruleincomplete.png"),
                       /**
                        * Icon for save <br>
                        * Source- http://eclipse-icons.i24.cc/ovr16/save_edit.gif
                        */
                       SAVE_28X30("Save.gif"),
                       /**
                        * Icon for cdr wizard page1
                        */
                       CDR_WIZARD_PG1_67X57("cdrwizard_page1.jpg"),
                       /**
                        * Icon for cdr wizard page2
                        */
                       CDR_WIZARD_PG2_67X57("cdrwizard_page2.jpg"),
                       /**
                        * Icon for cdr wizard page3
                        */
                       CDR_WIZARD_PG3_67X57("cdrwizard_page3.jpg"),
                       /**
                        * Icon for cdr wizard page4
                        */
                       CDR_WIZARD_PG4_67X57("cdrwizard_page4.jpg"),
                       /**
                        * Icon for reviewed
                        */
                       REVIEWED_28X30("reviewed.gif"),
                       /**
                        * Icon for not reviewed
                        */
                       NOT_REVIEWED_28X30("not-reviewed.gif"),
                       /**
                        * Icon for COMPLI result
                        */
                       RESULT_COMPLI_16X16("compliResult.png"),
                       /**
                        * Icon for SHAPE check
                        */
                       SHAPE_CHECK_16X16("shapeCheck.png"),
                       /**
                        * Icon for SHAPE check accept
                        */
                       SHAPE_CHECK_ACCEPT_16X16("shapeCheck_accept.png"),
                       /**
                        * Icon for SHAPE check decline
                        */
                       SHAPE_CHECK_DECLINE_16X16("shapeCheck_decline.png"),
                       /**
                        * Icon for ok<br>
                        * Source -https://materialdesignicons.com/
                        */
                       OK_16X16("ok.png"),
                       /**
                        * Icon for not ok
                        */
                       NOT_OK_16X16("notOk.png"),
                       /**
                        * Icon for change marker<br>
                        * Source - https://materialdesignicons.com/
                        */
                       CHANGE_MARKER_16X16("change_marker.png"),
                       /**
                        * Icon for no change marker
                        */
                       NO_CHANGE_MARKER_16X16("no_change_marker.png"),
                       /**
                        * Icon for PIDC details defined value node in tree
                        */
                       PDT_VAL_NOD_28X30("vir_node.gif"),
                       /**
                        * Icon for PIDC details UN-defined value node in tree<br>
                        * Source - Created using GIMP
                        */
                       PDT_VAL_UDEF_28X30("undefined_node.gif"),
                       /**
                        * Icon for deleted variant node node in tree
                        */
                       VARIANT_DEL_28X30("variant_deleted.gif"),
                       /**
                        * Icon for DELETED sub variant node in tree
                        */
                       SVAR_DEL_28X30("sub_variant_deleted.gif"),
                       /**
                        * Icon for value edit
                        */
                       VALUE_EDIT_28X30("value_edit.gif"),
                       /**
                        * Icon for mandatory colum
                        */
                       RED_BALL_16X16("Red-Ball-icon.png"),
                       // ICDM-2625
                       /**
                        * Icon for predefined attrs column<br>
                        * Source - https://materialdesignicons.com/
                        */
                       BLUE_BALL_GRPD_16X16("blue_ball_16X16.png"),
                       /**
                        * Icon for predefined attrs column
                        */
                       BLUE_BALL_PREDEF_16X16("blue_ball_predef_16X16.png"),
                       /**
                        * Icon for deppendency attr<br>
                        * Source - https://materialdesignicons.com/
                        */
                       DEPN_ATTR_28X30("dependency_attr.png"),
                       /**
                        * Icon for undelete
                        */
                       UNDELETE_16X16("undelete.png"),
                       /**
                        * Icon for inclearing<br>
                        * Source- Created using GIMP
                        */
                       IN_CLEARING_16X16("for_clearing.png"),
                       /**
                        * Icon for clear<br>
                        * Source- Created using GIMP
                        */
                       CLEAR_16X16("clear.png"),
                       /**
                        * Icon for warning 12x12<br>
                        * Source -https://materialdesignicons.com/
                        */
                       WARNING_12X12("warning12x12.png"),
                       /**
                        * Icon for clear filter<br>
                        * Source- Created using GIMP
                        */
                       CLEAR_FILTER_16X16("clear_filter.png"),
                       /**
                        * Icon for rejected value <br>
                        * Source- Created using GIMP
                        */
                       REJECTED_VALUE_16X16("clear_rejected.png"),
                       /**
                        * Icon to add to favourites - source - https://materialdesignicons.com/
                        */
                       FAV_ADD_28X30("favorites_add.png"),
                       /**
                        * Icon to import pidc<br>
                        * Source- http://eclipse-icons.i24.cc/eclipse-icons-02.html
                        */
                       IMPORT_PID_28X30("import.gif"),
                       /**
                        * Icon to remove from favourites
                        */
                       FAV_DEL_28X30("favorites_del.gif"),
                       /**
                        * Icon to export PIDC
                        */
                       EXPORT_16X16("export.png"),
                       /**
                        * Icon to 100% export excel - source - https://materialdesignicons.com/
                        */
                       EXCEL_FILE_16X16("file-excel.png"),
                       /**
                        * Icon for delte virtual node<br>
                        * Source - Created using GIMP
                        */
                       VIR_NODE_DEL_16X16("deleteNode.png"),
                       /**
                        * Icon for add virtual node <br>
                        * Source - Created using GIMP
                        */
                       VIR_NODE_ADD_16X16("addNode.png"),
                       /**
                        * Icon for mandatory attr<br>
                        * Source - https://materialdesignicons.com
                        */
                       ATTR_MANDATORY_16X16("Red-Ball-icon.png"),
                       /**
                        * Icon for attr non dependent
                        */
                       ATTR_NONDEP_16X16("attr_nondep.png"),
                       /**
                        * Icon for non-mandatory<br>
                        * Source -https://materialdesignicons.com/
                        */
                       NON_MANDATORY_28X30("non_mandatory.png"),
                       /**
                        * Icon to show variants<br>
                        * Source- Created using GIMP
                        */
                       SHOW_VARIANTS_28X30("show_variants.gif"),
                       /**
                        * Icon to show defined atttrs<br>
                        * Source- http://eclipse-icons.i24.cc/ovr16/importpref_obj.gif
                        */
                       SHOW_DEFINED_28X30("show_defined.gif"),
                       /**
                        * Icon for virtual nodes
                        */
                       VIR_NODE_16X16("vir_node.gif"),
                       /**
                        * Icon for non virtual node<br>
                        * Source- Created using GIMP
                        */
                       NON_VIR_NODE_16X16("non_virtual.png"),
                       /**
                        * Icon for all attributes including invisible
                        */
                       ALL_ATTR_16X16("all_attrs.png"),
                       /**
                        * Icon for calendar
                        */
                       CALENDAR_16X16("calendar.png"),
                       /**
                        * Icon for non variant<br>
                        * Source- Created using GIMP
                        */
                       NON_VARIANT_28X30("nonVariant.gif"),
                       /**
                        * Icon for not defined<br>
                        * Source- Created using GIMP
                        */
                       NON_DEFINED_28X30("notDefined.gif"),
                       /**
                        * Icon for used
                        */
                       USED_16X16("Used.gif"),
                       /**
                        * Icon for not used
                        */
                       NOT_USED_16X16("NotUsed.gif"),
                       /**
                        * Icon for question
                        */
                       QUESTION_16X16("Question.gif"),
                       /**
                        * Icon for lock<br>
                        * Source- http://eclipse-icons.i24.cc/eclipse-icons-02.html
                        */
                       LOCK_16X16("lock.png"),
                       /**
                        * Icon for Unlock
                        */
                       UNLOCK_16X16("Unlock.png"),

                       /**
                        * Icon for new version
                        */
                       NEW_VERSION_16X16("newVersion.png"),
                       /**
                        * Icon for copy edit
                        */
                       COPY_EDIT_16X16("copy_edit.gif"),
                       /**
                        * Icon for rename
                        */
                       RENAME_16X16("rename.png"),
                       /**
                        * Icon for undo delete<br>
                        * Source - https://materialdesignicons.com/
                        */
                       UNDO_DELETE_16X16("undo_delete.png"),
                       /**
                        * Icon for pidc requester<br>
                        * Source - https://materialdesignicons.com/
                        */
                       PIDC_REQUESTOR_16X16("pidc_requestor.png"),
                       /**
                        * Icon for all filter action <br>
                        * Source - https://materialdesignicons.com/
                        */
                       ALL_16X16("All.png"),
                       /**
                        * Icon for all filter action <br>
                        * Source - https://materialdesignicons.com/
                        */
                       ALL_8X8("All_8X8.png"),
                       /**
                        * Icon for all filter action <br>
                        * Source - https://materialdesignicons.com/
                        */
                       ALL_YELLOW_8X8("all_yellow_8X8.png"),
                       /**
                        * Icon for none action<br>
                        * Source - https://materialdesignicons.com/
                        */
                       NONE_16X16("None.png"),
                       /**
                        * Icon for All action in UC Editor<br>
                        * Source - https://materialdesignicons.com/
                        */
                       CHECKBOX_ALL_16X16("checkbox_all_marked.png"),
                       /**
                        * Icon for Any action in UC Editor<br>
                        * Source - https://materialdesignicons.com/
                        */
                       CHECKBOX_ANY_16X16("checkbox_any_marked.png"),
                       /**
                        * Icon for None action in UC Editor<br>
                        * Source - https://materialdesignicons.com/
                        */
                       CHECKBOX_NONE_16X16("checkbox_none_marked.png"),
                       /**
                        * Icon for usecase root node
                        */
                       UC_ROOT_28X30("usecase_root.gif"),
                       /**
                        * Icon for uploading
                        */
                       UPLOAD_16X16("Upload.png"),
                       /**
                        * Icon for with status filter<br>
                        * Source- https://materialdesignicons.com/
                        */
                       WITH_STATUS_16X16("withStatus.png"),
                       /**
                        * Icon for no status filter<br>
                        * Source -https://materialdesignicons.com/
                        */
                       NO_STATUS_16X16("noStatus.png"),
                       /**
                        * Icon to show caldata value
                        */
                       SHOW_CALDATA_VAL_14X14("show_caldata_value.png"),
                       /**
                        * Icon for no caldata value
                        */
                       NO_CALDATA_VAL_14X14("no_caldata_value.png"),
                       /**
                        * Icon to filter lab files<br>
                        * Source- https://materialdesignicons.com/
                        */
                       LAB_16X16("filterLAB.png"),
                       /**
                        * Icon to MoniCa files
                        */
                       // ICDM-2138
                       MONICA_REPORT_16X16("monicaReport.png"),
                       /**
                        * Icon to remove lab files filter<br>
                        * Source- https://materialdesignicons.com/
                        */
                       LAB_DEL_16X16("removeLAB.png"),
                       /**
                        * Icon for table and graph
                        */
                       TABLE_GRAPH_16X16("table_graph.png"),
                       /**
                        * Icon for table and graph<br>
                        * Source - https://materialdesignicons.com/
                        */
                       COLLAPSE_NAT_16X16("collapseTree.png"),
                       /**
                        * Icon for importing param data<br>
                        * Source- http://eclipse-icons.i24.cc/eclipse-icons-02.html
                        */
                       IMPORT_PARAM_16X16("ImportParamData.gif"),
                       /**
                        * Icon for upload LAB file
                        */
                       UPLOAD_LAB_16X16("uploadFile.png"),
                       /**
                        * Icon to clear param data
                        */
                       CLEAR_PARAM_16X16("clearParamData.png"),
                       /**
                        * Icon to clear lab file
                        */
                       CLEAR_LAB_16X16("clearFile.png"),
                       /**
                        * Icon for not used filter in pid<br>
                        * Source - https://materialdesignicons.com/
                        */
                       FILTER_QUESTION_16X16("Question.png"),
                       /**
                        * Icon for review complete
                        */
                       GREEN_SMILEY_16X16("green_smiley.png"),
                       /**
                        * Icon for param rules
                        */
                       PARAM_RULES_16X16("param_rules.png"),

                       /**
                        * Icon for virtual favorite nodes<br>
                        * Source- http://eclipse-icons.i24.cc/ovr16/warning_co.gif
                        */
                       VIRTUAL_DECORATOR_12X12("virtual_indicator.gif"),

                       /**
                        * Icon for virtual records Source - https://materialdesignicons.com/
                        */
                       VIRTUAL_RECORD_8X8("label_variant_8X8.png"),

                       /**
                        * Icon for virtual records Source - https://materialdesignicons.com/
                        */
                       VIRTUAL_RECORD_16X16("label_variant_16X16.png"),


                       /**
                        * Icon for virtual records Source - https://materialdesignicons.com/
                        */
                       VIRTUAL_RECORD_DARK_8X8("label_variant_darkOrange8X8.png"),

                       /**
                        * Icon for favorite node indicator<br>
                        * Source - Source -https://materialdesignicons.com/ & edited with GIMP
                        */
                       FAV_NODE_DECORATOR_12X12("uc_fav_indicator1.png"),

                       /**
                        * Link for PIDC
                        */
                       PIDC_LINK_12X12("pidc_link.jpg"),

                       /**
                        * Icon for tick mark
                        */
                       LINK_VAR_MARK_8X8("Orange.png"),
                       /**
                        * Icon for tick mark<br>
                        * Source -https://materialdesignicons.com/
                        */
                       TICK_COLUMN_16X16("tick.png"),
                       /**
                        * Icon for letter Text value type
                        */
                       VALUE_TEXT_16X16("Text-icon.png"),
                       /**
                        * Icon for letter boolean value type
                        */
                       VALUE_BOOL_16X16("binary.png"),
                       /**
                        * Icon for letter number value type
                        */
                       VALUE_NUMBER_16X16("Numbers-icon.png"),
                       /**
                        * Icon for decimal
                        */
                       DECIMAL_16X16("decimal16x16.png"),
                       /**
                        * Icon for include param filetr icon
                        */
                       INCLUDE_PARAM_16X16("include_param_16x16.png"),
                       /**
                        * Icon for not included param filetr icon
                        */
                       NOT_INCLUDED_PARAM_16X16("include_no_param_16x16.png"),
                       /**
                        * Icon for param with dependencies
                        */
                       PARAM_DEPN_16X16("param_with_depn.png"),
                       /**
                        * Icon for param without dependencies
                        */
                       PARAM_NO_DEPN_16X16("param_with_no_depn.png"),
                       /**
                        * copy existing rule to new configuration<br>
                        * Source - http://eclipse-icons.i24.cc/ovr16/cpyqual_menu.gif
                        */
                       RULE_COPY_16X16("rule_copy.png"),
                       /**
                        * Image for Pidc Statistics
                        */
                       PIDC_STATISTICS("statistics.png"),
                       /**
                        * iCDM-1198 ; Icon for clear history (series stat)<br>
                        * Source- Created using GIMP
                        */
                       CLEAR_HIST_16X16("clear_history.png"),
                       /**
                        * Show param history<br>
                        * Source- http://eclipse-icons.i24.cc/ovr16/history_view.gif
                        */
                       SHOW_PARAM_HIST_16X16("show_history.png"),
                       /**
                        * Dont show param history<br>
                        * Source- https://materialdesignicons.com/
                        */
                       SHOW_NO_PARAM_HIST_16X16("no_history.png"),
                       /**
                        * Clear all filters
                        */
                       RESET_FILTERS_16X16("clear_all_filters.png"),
                       /**
                        * Link with editor action ( //iCDM-1241)
                        */
                       LINK_EDITOR_16X16("link_editor.gif"),
                       /**
                        * A2l file icon with hyperlink (iCDM-1249)
                        */
                       A2LFILE_LINK_16X16("a2lfile_link.gif"),
                       /**
                        * CDR result icon with hyperlink (iCDM-1249)
                        */
                       CDR_RES_LINK_16X16("review_result_link.gif"),

                       /**
                        * pidc search icon(iCDM-1291)
                        */
                       PIDC_SEARCH_16X16("Search.gif"),
                       /**
                        * Invisible attrs in pidc (iCDM-1345)
                        */
                       GREY_BALL_16X16("grey_ball.png"),
                       // ICDM-1232
                       /**
                        * Send Mail<br>
                        * Source- http://eclipse-icons.i24.cc/ovr16/sample3.gif
                        */
                       SEND_MAIL_16X16("send_mail.gif"),
                       /**
                        * Pidc history icon
                        */
                       PIDC_HISTORY_16X16("clock.png"),
                       // iCDM-2614
                       /**
                        * Focus matrix history icon
                        */
                       FOCUS_MATRIX_HISTORY_16X16("clock.png"),
                       /**
                        * Pidc history link levels icon<br>
                        * Source- http://eclipse-icons.i24.cc/eclipse-icons-02.html
                        */
                       PIDC_HISTORY_LINK_LEVELS_16X16("level_link.png"),
                       /**
                        * Attributes editor
                        */
                       ATTRIBUTES_EDITOR_16X16("attr_editor.png"),

                       /**
                        * Attributes editor
                        */
                       ALIAS_16X16("alias.png"),
                       /**
                        * iCDM Community (iCDM-1306)
                        */
                       ICDM_COMMUNITY_16X16("icdm_community.png"),
                       /**
                        * iCDM Wiki (iCDM-1306)
                        */
                       ICDM_WIKI_16X16("icdm_wiki.png"),
                       /**
                        * iCDM Contacts (iCDM-1306)
                        */
                       ICDM_CONTACTS_16X16("icdm_contacts.png"),
                       /**
                        * refresh icon
                        */
                       REFRESH_16X16("refresh.gif"),
                       /**
                        * Function edit
                        */
                       FUNCTION_EDIT_16X16("function_edit.png"),
                       /**
                        * Rule set editor
                        */
                       RULE_SET_EDIT_16X16("rule_set_edit.png"),

                       /**
                        * Component package rules editor
                        */
                       COMP_PKG_RULE_EDIT_16X16("cp_edit.png"),

                       /**
                        * Image for 'Other versions' string in pidc tree<br>
                        * Source- https://materialdesignicons.com/
                        */
                       OTHER_VERSIONS_16X16("other_versions.png"),
                       /**
                        * Image for 'active pidc version' string in pidc tree
                        */
                       ACTIVE_PIDC_16X16("pidc_active_obj.gif"),

                       /**
                        * Marker for comments<br>
                        * Source - https://materialdesignicons.com/
                        */
                       COMMENTS_TAG_16X16("comments_tag_16x16.png"),

                       /**
                        * ICDM-1697 CDR Project report<br>
                        * Source-http://eclipse-icons.i24.cc/ovr16/new_untitled_text_file.gif
                        */
                       CDR_PROJECT_REPORT_16X16("report_16x16.png"),


                       /**
                        * Image for pdf report export of hex comparison with latest review result Image source :
                        * https://materialdesignicons.com/
                        */
                       PDF_EXPORT_16X16("file-pdf.png"),

                       /**
                        * Bosch responsibility icon
                        */
                       BOSCH_RESPONSIBLE_ICON_16X16("responsibility_16x16.png"),

                       /**
                        * Customer responsilbility icon
                        */
                       CUST_RESPONSIBLE_ICON_16X16("non_responsibility_16x16.png"),
                       /**
                        * Others responsibility icon
                        */
                       OTHERS_RESPONSIBLE_ICON_16X16("cust_name_no.png"),
                       /**
                        * Tick with circle icon<br>
                        * Source - https://materialdesignicons.com/
                        */
                       TICK_CIRCLE_16X16("Circle_tickmark.png"),
                  /**
                        * transfer icon<br>
                        * Source - https://materialdesignicons.com/
                        */
                       TRANSFER_16X16("transfer.png"),
                       /**
                        * Tick with delete icon
                        */
                       TICK_DELETE_16X16("tick-delete.png"),
                       /**
                        * lock icon 8X8
                        */
                       LOCK_8X8("lock_blue_8x8.png"),
                       /**
                        * hide icon 16x16<br>
                        * Source -https://materialdesignicons.com/
                        */
                       HIDE_16X16("hide.png"),

                       /**
                        * Review result icon for start phase
                        */
                       RVW_RESULT_START_16X16("review_result_start.png"),
                       /**
                        * red icon 8X8
                        */
                       RED_MARK_8X8("red_mark.png"),
                       /**
                        * green icon 8X8
                        */
                       GREEN_MARK_8X8("green_mark.png"),
                       /**
                        * green icon 8X8
                        */
                       QR_GREEN_MARK_8X8("QR_green_mark.png"),
                       /**
                        * yellow icon 8X8
                        */
                       YELLOW_MARK_8X8("yellow_mark.png"),
                       /**
                        * grey icon 8X8
                        */
                       GREY_MARK_8X8("grey_mark.png"),
                       /**
                        * reload 16x16 <br>
                        * Source -https://materialdesignicons.com/
                        */
                       RELOAD_16X16("reload.png"),
                       /**
                        * Review result icon for start phase
                        */
                       RVW_RESULT_TEST_16X16("review_result_test.png"),
                       /**
                        * Questinaire icon
                        */
                       QUESTIONARE_ICON_16X16("Questinaire-icon.png"),

                       /**
                        * Questinaire icon with link
                        */
                       QUESTIONNAIRE_ICON_WITH_LINK_16X16("Questionnaire_icon_with_link_16x16.png"),
                       /**
                        * Question response icon
                        */
                       QUESTION_RESPONSE_ICON_16X16("ques_response.png"),
                       /**
                        * Q icon
                        */
                       QUESTION_ICON_16X16("Q-icon.png"),
                       /**
                        * Questinaire icon
                        */
                       HEADING_ICON_16X16("H-icon.png"),
                       /**
                        * Questinaire icon
                        */
                       EXCLAMATION_ICON_16X16("exclamation_mark.png"),
                       /**
                        * exclamation Questinaire icon
                        */
                       ORANGE_EXCLAMATION_ICON_16X16("exclamation.png"),
                       /**
                        * deleted items icon - Source - https://materialdesignicons.com/
                        */
                       DELETED_ITEMS_ICON_16X16("deleted.png"),
                       /**
                        * active questions icon
                        */
                       ACTIVE_ITEMS_ICON_16X16("active_questions.png"),
                       /**
                        * use case proposal colored ball icon<br>
                        * Source - https://materialdesignicons.com/
                        */
                       UC_PROPOSAL_ICON_16X16("16x16-uc-icon.png"),
                       /**
                        * use case proposal cum comment icon<br>
                        * Source - https://materialdesignicons.com/ & edited using GIMP
                        */
                       // ICDM-2427
                       UC_PROPOSAL_COMMENT_ICON_32X16("bag-comments-icon.png"),
                       /**
                        * use case proposal colored ball icon<br>
                        * Source -https://materialdesignicons.com/ & edited with GIMP
                        */
                       RED_CROSS_ICON_12X12("wrong_12X12.png"),
                       /**
                        * Browse icon
                        */
                       BROWSE_BUTTON_ICON("browse.gif"),
                       /**
                        * Folder icon - source - https://materialdesignicons.com/
                        */
                       FOLDER_ICON("folder.png"),
                       /**
                        * Folder disabled icon
                        */
                       FOLDER_DIS_ICON("folder_disabled.png"),
                       /**
                        * Mapped focus matrix icon
                        */
                       MAPPED_FOCUS_MATRIX_ICON_16X16("mapped-focus-matrix.jpg"),
                       /**
                        * Un-mapped focus matrix icon
                        */
                       UNMAPPED_FOCUS_MATRIX_ICON_16X16("unmapped-focus-matrix.jpg"),
                       /**
                        * Mapped focus matrix overlay icon
                        */
                       MAPPED_FOCUS_MATRIX_ICON_OVERLAY("mapped-focus-matrix-overlay.jpg"),
                       // ICDM-2408
                       /**
                        * Icon for web flow
                        */
                       WEBFLOW_16X16("webflow.png"),
                       /**
                        * Icon for back arrow
                        */
                       BACKWARD_16X16("backward.gif"),
                       /**
                        * Icon for forward arrow
                        */
                       FORWARD_16X16("forward.gif"),
                       /**
                        * Icon for graph compare;
                        */
                       COMPARE_GRAPH_16X16("graph_compare.gif"),
                       /**
                        * Icon for Up arrow;<br>
                        * Source - https://materialdesignicons.com/
                        */
                       UP_BUTTON_ICON_16X16("Up.png"),
                       /**
                        * Icon for down arrow;<br>
                        * Source - https://materialdesignicons.com/
                        */
                       DOWN_BUTTON_ICON_16X16("down_arrow.png"),
                       // ICDM-2625
                       /**
                        * Icon for grouped attributes;
                        */
                       GROUPED_ATTR_16X16("grouped_attr.png"),
                       // ICDM-2625
                       /**
                        * Icon for not grouped attributes;
                        */
                       NON_GROUPED_ATTR_16X16("not_grouped_attr.png"),
                       // ICDM-2585 (Parent Task ICDM-2412)
                       /**
                        * official icon
                        */
                       OFFICIAL_ICON_16X16("official-rvw.png"),

                       // iCDM-2614
                       /**
                        * Focus Matrix filter icon
                        */
                       FOCUS_MATRIX_16X16("focus-matrix.png"),

                       // FC2WP
                       /**
                        * Icon for FC2WP settings<br>
                        * Source - https://materialdesignicons.com
                        */
                       SETTINGS_16X16("setting.png"),

                       /**
                        * Icon for FC2WP isNotInICDMA2L<br>
                        * Source - https://materialdesignicons.com
                        */
                       ICON_NOT_FOUND_16X16("notFound.png"),

                       /**
                        * Icon for FC2WP isInICDMA2L<br>
                        * Source - https://materialdesignicons.com
                        */
                       ICON_IS_FOUND_16X16("isFound.png"),
                       /**
                        * Icon for FC2WP WorkPackage Assigned<br>
                        * Source - https://materialdesignicons.com
                        */
                       ICON_WP_ASSGND_16X16("wp_assigned.png"),

                       /**
                        * Icon for WorkPackage Not Assigned<br>
                        * Source - https://materialdesignicons.com
                        */
                       ICON_WP_NOT_ASSGND_16X16("wp_notAssigned.png"),

                       /**
                        * Icon for FC2WP deleted<br>
                        * Source - https://materialdesignicons.com
                        */
                       ICON_WP_DELETED_16X16("deleted.png"),

                       /**
                        * Icon for WorkPackage Not deleted<br>
                        * Source - https://materialdesignicons.com
                        */
                       ICON_WP_NOT_DELETED_16X16("notDeleted.png"),

                       /**
                        * Icon for FC2WP relevant PT-type<br>
                        * Source - Eclipse Repository (cheatsheet_item_obj.png)
                        */
                       ICON_PT_RELEVANT_16X16("relevant.png"),

                       /**
                        * Icon for FC2WP not relevant PT-type<br>
                        * Source - Eclipse Repository (cheatsheet_unsupported_obj.png)
                        */
                       ICON_PT_NOT_RELEVANT_16X16("notRelevant.png"),
                       /**
                        * Icon for Showing consolidated secondary review result in review result editor Source -
                        * https://materialdesignicons.com/
                        */
                       // Task 231287
                       ICDON_SECONDARY_RESULT("secondary-result-16X16.png"),
                       /**
                        * Icon for Showing secondary review result ok filter in review result editor, created using GIMP
                        */
                       // Task 236307
                       ICDON_SECONDARY_OK_RESULT("secondary-ok-result-16X16.png"),
                       /**
                        * Icon for Showing secondary review result Not-ok filter in review result editor ,created using
                        * GIMP
                        */
                       /// Task 236307
                       ICDON_SECONDARY_NOT_OK_RESULT("secondary-not-ok-result-16X16.png"),
                       /**
                        * Icon for Showing secondary review result Not-Applicable filter in review result editor source
                        * - Bosch image library
                        */
                       // Task 236307
                       ICDON_NOT_APPLICABLE_16X16("Question.png"),
                       /**
                        * Icon for assign varnames
                        */
                       ASSIGN_VARNAMES_16X16("assign.png"),
                       /**
                        * Icon for file <br>
                        * Source - Eclipse Repository
                        */
                       FILE_16X16("file.gif"),
                       /**
                        * Icon for fc2wp defn 16X16<br>
                        * Source - https://materialdesignicons.com
                        */
                       FC2WP_DEFN_16X16("fc2wp_16X16.png"),
                       // Task 236308
                       /**
                        * Icon for marking CHECKED 16X16<br>
                        * Source - https://www.iconfinder.com
                        */
                       CHECKED_16X16("checked-icon-16X16.png"),
                       // Task 236308
                       /**
                        * Icon for marking RESET 16X16<br>
                        * Source - https://www.iconfinder.com
                        */
                       RESET_16X16("reset-icon-16X16.png"),
                       /**
                        * Icon for fc2wp defn 32X32<br>
                        * Source - https://materialdesignicons.com
                        */
                       FC2WP_DEFN_32X32("fc2wp_32X32.png"),

                       /**
                        * Icon for bosch logo in pdf report
                        */
                       BOSCH_LOGO_159_76("boschlogo_icon.png"),
                       /**
                        * Icon for caldata analyzer editor<br>
                        * Source - https://materialdesignicons.com
                        */
                       CDA_EDITOR_16X16("caldata_analyzer.png"),

                       /**
                        * overlay icon Source - https://materialdesignicons.com
                        */
                       COMPLI_MARK_8X8("compli-mark.png"),
                       /**
                        * Icon for radio box - checked Source - https://materialdesignicons.com
                        */
                       RADIO_CHECK("radio_check_16x16.png"),
                       /**
                        * Icon for radio box - unchecked Source - https://materialdesignicons.com
                        */
                       RADIO_UNCHECK("radio_uncheck_16x16.png"),
                       /**
                        * Icon for expired usecase - Source - https://materialdesignicons.com
                        */
                       OUTDATED("outdated.png"),
                       /**
                        * Icon for expired usecase - Source - https://materialdesignicons.com
                        */
                       BIG_TICK_36X36("big_tick.png"),
                       /**
                        * Icon for expired usecase - Source - https://materialdesignicons.com
                        */
                       ERROR_36X36("big_error.png"),
                       /**
                        * Checkssd icon for the external customer report
                        */
                       DISCLOSURE("disclosure.jpg"),

                       /**
                        * Icon for loading filter criteria - Source - https://materialdesignicons.com
                        */
                       FILE_IMPORT_16X16("file-import.png"),
                       /**
                        * Icon for compliance review of hex
                        */
                       COMPLI_HEX_16X16("hex-file_16x16.png"),

                       /**
                        * Image for read only toolbar action<br>
                        * Source -https://materialdesignicons.com/
                        */
                       READ_ONLY_16X16("readonly16x16.png"),

                       /**
                        * Image for not read only toolbar action<br>
                        * Source -https://materialdesignicons.com/
                        */
                       NOT_READ_ONLY_16X16("notreadonly16x16.png"),

                       /**
                        * Image key for user column in wp defn nattable in a2l wp definition page Source -
                        * https://materialdesignicons.com
                        */
                       WP_DEFN_USER_RESPONSIBLE("user_resp.png"),
                       /**
                        * Image key for user column in Resp type for outline page - https://materialdesignicons.com
                        */
                       WP_DEFN_USER_RESP_TYPE("user_resp_type.png"),
                       /**
                        * Image key for predefined toolbar filter in a2l wp definition page Source -
                        * https://materialdesignicons.com
                        */
                       WP_DEFN_USER_RESPONSIBLE_DISABLED("disable_user_responsible.png"),
                       /**
                        * Image key for predefined toolbar filter in a2l wp definition page Source -
                        * https://materialdesignicons.com
                        */
                       WP_DEFN_USER_RESPONSIBLE_NA("resp_not_assigned.png"),
                       /**
                        * Image key for predefined toolbar filter in a2l wp label assignment page Source -
                        * https://materialdesignicons.com
                        */
                       WP_LABEL_RESP_INHERITED("wp_resp_inherited.png"),
                       /**
                        * Image key for predefined toolbar filter in a2l wp label assignment page Source -
                        * https://materialdesignicons.com
                        */
                       CUSTOMER_NAME_INHERITED("cust_name_inherited.png"),
                       /**
                        * Image key for predefined toolbar filter in a2l wp label assignment page Source -
                        * https://materialdesignicons.com
                        */
                       CUSTOMER_NAME_SET("cust_name_yes.png"),
                       /**
                        * Image key for predefined toolbar filter in a2l wp label assignment page Source -
                        * https://materialdesignicons.com
                        */
                       CUSTOMER_NAME_NOT_SET("cust_name_no.png"),
                       /**
                        * Image key for showing view from dialog Source - http://eclipse-icons.i24.cc/index.html
                        */
                       SHOW_VIEW("exportapp_wiz.gif"),
                       /**
                        * Image key for black list labels https://materialdesignicons.com
                        */
                       BLACK_LIST_LABEL("black_list.png"),
                       /**
                        * Image key for black list labels https://materialdesignicons.com
                        */
                       NON_BLACK_LIST_LABEL("black_list_cross.png"),
                       /**
                        * Image key for QSSD labels https://materialdesignicons.com
                        */
                       QSSD_LABEL("qssd_16X16.png"),
                       /**
                        * Image key for Non QSSD labels Source- Created using GIMP
                        */
                       NON_QSSD_LABEL("non_qssd_16X16.png"),

                       /**
                        * Image Key for Fc In Sdom
                        */
                       FC_IN_SDOM_16X16("InSdom.gif"),
                       /**
                        * Image Key for Fc Not In Sdom
                        */
                       FC_NOT_IN_SDOM_16X16("NotInSdom.gif"),

                       /**
                        * Image key for quotation flag Source : https://materialdesignicons.com
                        */
                       QUOTATION_FLAG("quotation_16x16.png"),

                       /**
                        * Image key for quotation not relevant flag Source : https://materialdesignicons.com
                        */
                       QUOTATION_NOT_RELEVANT_FLAG("quotation_not_relevant_16x16.png"),

                       /**
                        * Image key for listing details<br>
                        * Source : https://materialdesignicons.com
                        */
                       LIST_DETAILS("format-list-checkbox.png"),

                       /**
                        * Admin icon Source - https://materialdesignicons.com/
                        */
                       ADMIN_16X16("admin.png"),
                       /**
                        * Image key for FC With Params<br>
                        * Source : https://brandguide.bosch.com/
                        */
                       FC_WITH_PARAMS("fc_with_params.png"),
                       /**
                        * Image key for FC Without Params<br>
                        * Source : https://brandguide.bosch.com/
                        */
                       FC_WITH_PARAMS_NO("fc_without_params.png"),

                       /**
                        * Icon for active A2L File
                        */
                       A2LFILE_ACTIVE("active_a2lfile.png"),

                       /**
                        * Icon to indicate Questionnaire status as 'Ok' when there are no Questionnaires for the
                        * label<br>
                        * Source: https://materialdesignicons.com
                        */
                       NO_QNAIRE("minus-circle_16X16.png"),

                       /**
                        * Icon for qnaire version update
                        */
                       QNAIRE_VERSION_UPDATE("file-sync-outline-custom.png"),

                       /**
                        * Icon to indicate oss component information option Source: https://materialdesignicons.com
                        */
                       OSS_INFO("OSSInfo.png"),

                       /**
                        * Icon for Admin reveiw Result deletion
                        */
                       RVW_RES_ADMIN_DEL("review-result-admin-delete.png");

  /**
   * image name
   */
  private final String fileName;
  /**
   * Constant for icons folder
   */
  private static final String ICON_FOLDER = "icons";

  ImageKeys(final String name) {
    this.fileName = name;
  }

  /**
   * @return key as string
   */
  protected String getKeyAsString() {
    return toString();
  }

  /**
   * path of the image
   *
   * @return img path
   */
  public String getPath() {
    StringBuilder path = new StringBuilder();
    path.append(ICON_FOLDER).append(File.separator).append(this.fileName);
    return path.toString();
  }
}
