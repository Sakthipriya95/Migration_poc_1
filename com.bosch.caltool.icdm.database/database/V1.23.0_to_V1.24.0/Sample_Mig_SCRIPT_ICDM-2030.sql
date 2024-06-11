/*******************************************************************************
* 
* Sample migration script to update review results based on CDR restructuring
* ICDM-2030
*  
********************************************************************************/



/*
  Step 1 : Create required database objects
  a) create a function to get main variant of review result
  b) Create a temporary table with existing review results for analysis
*/

create or replace function temp_get_first_rvw_variant (res_id in number)
RETURN NUMBER
is
    var_id number;
begin
    select variant_id into var_id 
    from (
          select VARIANT_ID, RESULT_ID from t_rvw_variants where result_id = res_id order by created_date asc
         ) 
    where rownum  = 1;
return (var_id);
end;
/

create table t_rvw_results_temp  as(
select results.SOURCE_TYPE, results.source_type as source_type_new, results.RVW_STATUS, results.GRP_WORK_PKG, results.GRP_WORK_PKG as GRP_WORK_PKG_new, results.FC2WP_ID, LABFUN_FILE, ATTRVAL_COUNT, FILE_COUNT, FUNCTION_COUNT, PARAM_COUNT, PARTICIPANT_COUNT, results.REVIEW_TYPE, results.ORG_RESULT_ID, results.RESULT_ID, temp_get_first_rvw_variant(results.result_id) as "VARIANT_ID", results.CREATED_DATE, results.CREATED_USER, results.MODIFIED_DATE, results.MODIFIED_USER, results.VERSION, results.DESCRIPTION, results.RSET_ID, results.PIDC_A2L_ID, results.LOCK_STATUS
from t_rvw_results results
left outer join                                                                                                  
        (select rvw.result_id, listagg(files.file_name,',') WITHIN GROUP (order by rvw.result_id) labfun_file         
            from t_rvw_files rvw                                                                                     
             join tabv_icdm_files files on (files.file_id = rvw.file_id)                                             
            where rvw.file_type in ('LAB', 'FUN')                                                                                
            group by rvw.result_id ) files                                                                           
         on (files.result_id = results.result_id)  
left outer join
    (select result_id, count(1) attrval_count from T_RVW_ATTR_VALUES group by result_id) rvwattrval
    on rvwattrval.result_id = results.result_id
left outer join
    (select result_id, count(1) file_count from T_RVW_FILES group by result_id) rvwfile
    on rvwfile.result_id = results.result_id
left outer join
    (select result_id, count(1) function_count from t_rvw_functions group by result_id) rvwfun
    on rvwfun.result_id = results.result_id
left outer join
    (select result_id, count(1) param_count from t_rvw_parameters group by result_id) rvwparam
    on rvwparam.result_id = results.result_id
left outer join
    (select result_id, count(1) participant_count from T_RVW_PARTICIPANTS group by result_id) rvwparticipant
    on rvwparticipant.result_id = results.result_id
);

alter table t_rvw_results_temp add (new_type varchar2(50), new_type varchar2(50), temp_remarks varchar2(4000));

/*

Step 2 : 

verify the records in the temp table and set the field NEW_TYPE
Classify values as :

No Change   - No changes required
A2L Review  - Custom Review in PRO database
FC2WP       - FC2WP type workpackage based review
MFC2WP      - Multiple FC2WP type workpackage
LAB         - LAB file based review
FUN         - FUN file based review
GRP         - Group type workpackage
MGRP        - Multiple Group selection
MONICA      - Not applicable for PRO database
RF          - Review file based reviews
NOTDEF      - Grouping not defined yet

The activity could be done by export to excel, update the field value, create update script for T_RVW_RESULTS_TEMP 
Refer ICDM-2030__CDR_RestructuringMigration__Approach.xlsx attached to JIRA Issue

*/

----------
--Update scripts based on analysis, to identify the new type.
----------

--Current source type 'RF'
update t_rvw_results_temp set new_type = 'RF' where SOURCE_TYPE = 'RF';

--Current source type 'LAB'
update t_rvw_results_temp set new_type = 'LAB' where SOURCE_TYPE = 'LAB' and lower(LABFUN_FILE) like '%lab';
update t_rvw_results_temp set new_type = 'A2L Review' where SOURCE_TYPE = 'LAB' AND LABFUN_FILE IS NULL AND GRP_WORK_PKG = '<CUSTOM_REVIEW>';
update t_rvw_results_temp set new_type= 'MGRP' where SOURCE_TYPE = 'LAB' AND LABFUN_FILE IS NULL AND GRP_WORK_PKG IS NULL;

--Current source type 'FUN'
update t_rvw_results_temp set new_type = 'FUN' where SOURCE_TYPE = 'FUN' AND lower(LABFUN_FILE) like '%fun';
update t_rvw_results_temp set new_type = 'A2L Review' where SOURCE_TYPE = 'FUN' AND LABFUN_FILE IS NULL AND GRP_WORK_PKG = '<CUSTOM_REVIEW>';
update t_rvw_results_temp set new_type = 'MFC2WP' where SOURCE_TYPE = 'FUN' AND LABFUN_FILE IS NULL AND GRP_WORK_PKG IS NULL;

--Current source type 'FC_WP'
update t_rvw_results_temp set new_type = 'FC2WP' where SOURCE_TYPE = 'FC_WP';

--Current source type 'GRP'
update t_rvw_results_temp set new_type = 'No Change' where SOURCE_TYPE = 'GRP' AND GRP_WORK_PKG is not null;

--Current source type 'RF'
update t_rvw_results_temp set new_type = 'A2L Review' where SOURCE_TYPE = 'NOT_DEF' AND GRP_WORK_PKG = '<CUSTOM_REVIEW>';
update t_rvw_results_temp set new_type = 'NOTDEF' where SOURCE_TYPE = 'NOT_DEF' AND GRP_WORK_PKG is null and nvl(ATTRVAL_COUNT, 0)  = 0 and nvl(FILE_COUNT, 0)  = 0 and nvl(FUNCTION_COUNT, 0)  = 0 and nvl(PARAM_COUNT, 0)  = 0 and nvl(PARTICIPANT_COUNT, 0)  = 0 ;


/*
* Step 2.1 Analyze all rows in T_RVW_RESULTS_TEMP where new_type is null. Manually update the type.
*/



/*
  Step 3 : update the SOURCE_TYPE and GRP_WORK_PKG columns in the temp table for type new type
*/

UPDATE T_RVW_RESULTS_TEMP SET source_type_new = 'A2L', grp_work_pkg_new = '<FUNCTION>' WHERE NEW_TYPE = 'A2L Review';
UPDATE T_RVW_RESULTS_TEMP SET source_type_new = 'FC_WP', grp_work_pkg_new = '<FC2WP>' WHERE NEW_TYPE = 'FC2WP';
UPDATE T_RVW_RESULTS_TEMP SET source_type_new = 'FUN', grp_work_pkg_new = '<FUNCTION>' WHERE NEW_TYPE = 'FUN';
UPDATE T_RVW_RESULTS_TEMP SET source_type_new = 'LAB', grp_work_pkg_new = '<LAB>' WHERE NEW_TYPE = 'LAB';
UPDATE T_RVW_RESULTS_TEMP SET source_type_new = 'FC_WP', grp_work_pkg_new = '<MFC2WP>' WHERE NEW_TYPE = 'MFC2WP';
UPDATE T_RVW_RESULTS_TEMP SET source_type_new = 'GRP', grp_work_pkg_new = '<MGRP>' WHERE NEW_TYPE = 'MGRP';
UPDATE T_RVW_RESULTS_TEMP SET source_type_new = 'MONICA', grp_work_pkg_new = '<MONICA_REPORT>' WHERE NEW_TYPE = 'MONICA';
UPDATE T_RVW_RESULTS_TEMP set source_type_new = 'RF', grp_work_pkg_new = '<REVIEWED_FILE>' WHERE NEW_TYPE = 'RF';
UPDATE T_RVW_RESULTS_TEMP set source_type_new = 'NOT_DEF', grp_work_pkg_new = '<NOT_DEFINED>' WHERE NEW_TYPE = 'NOTDEF';


COMMIT;

/*
* Step 4 & 5 : update the t_rvw_results with data from T_RVW_RESULTS_TEMP
*/


/*
* Step 4 : Generate the update scripts for T_RVW_RESULTS using the below query
*/

select 'update T_RVW_RESULTS set SOURCE_TYPE = '''|| source_type_new || ''', GRP_WORK_PKG = '''|| grp_work_pkg_new || ''' WHERE RESULT_ID = '|| result_id || ';' from T_RVW_RESULTS_TEMP
where new_type not in ('No Change', 'Reference') and new_type is not null;

/*
* Step 5 : execute the above scripts.
*/



/*
* Step 6 : drop the temporary database objects, when migration is complete. 
* To excecute, Uncomment the below DDL statements and and execute them. 
*/
--drop table T_RVW_RESULTS_TEMP cascade constraints;
--drop function temp_get_first_rvw_variant;
