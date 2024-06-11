spool c:\temp\11.pidcVersioning.log

-------------------------------------------------------------------------------------------------

--- Addition of PIDC_A2L_ID in T_RVW_RESULTS

-------------------------------------------------------------------------------------------------
ALTER TABLE T_RVW_RESULTS
ADD PIDC_A2L_ID NUMBER(15) CONSTRAINT T_RVW_RESULTS_FK_PIDC_A2L REFERENCES T_PIDC_A2L(PIDC_A2L_ID);
-------------------------------------------------------------------------------------------------

--- Deletion of INVALID Entries in T_RVW_RESULTS

-------------------------------------------------------------------------------------------------

--01.Query to find results with VARIANT_ID pointing to active(latest) revisions in PIDC
--select  pidc.PROJECT_ID,res.* from T_RVW_RESULTS res inner join TABV_PROJECT_VARIANTS var  on res.VARIANT_ID = var.VARIANT_ID
--                                   inner join T_PIDC_VERSION pver on var.PIDC_VERS_ID = pver.PIDC_VERS_ID 
--                                   inner join TABV_PROJECTIDCARD pidc on pver.PROJECT_ID = pidc.PROJECT_ID and pver.PRO_REV_ID = pidc.PRO_REV_ID;   

--02.Query to find results with VARIANT_ID pointing to non active(old) revisions in PIDC
--select res.* from T_RVW_RESULTS res inner join TABV_PROJECT_VARIANTS var  on res.VARIANT_ID = var.VARIANT_ID
--                                   inner join T_PIDC_VERSION pver on var.PIDC_VERS_ID = pver.PIDC_VERS_ID 
--                                   inner join TABV_PROJECTIDCARD pidc on pver.PROJECT_ID = pidc.PROJECT_ID and pver.PRO_REV_ID <> pidc.PRO_REV_ID;    

--03.Query to find results with VARIANT_ID as null
--select res.* from T_RVW_RESULTS res where res.VARIANT_ID is null;  

-- the below result id doesnt have a corresponding match in T_PIDC_A2L with for its project_id and A2l_ID 
--select * from T_RVW_RESULTS where RESULT_ID not in (select distinct(res.RESULT_ID) from T_RVW_RESULTS res,T_PIDC_A2L pa2l where res.PROJECT_ID = pa2l.PROJECT_ID and res.A2L_ID = pa2l.A2L_FILE_ID);

--760082067 534667  677266  697324096       6152450000  I       04-JUL-14 11.38.02.000000000 AM HEM3SI  13-AUG-14 10.22.11.000000000 AM DGS_ICDM    4   test    FC_WP   T   




/*
 * The following queries can be used to delete the invalid data, ONLY AFTER VERIFICATION !!!
 */

/*
delete from T_RVW_PARTICIPANTS where RESULT_ID not in (select res.RESULT_ID from T_RVW_RESULTS res ,T_PIDC_A2L pa2l where res.PROJECT_ID = pa2l.PROJECT_ID and res.A2L_ID = pa2l.A2L_FILE_ID);

delete from T_RVW_FILES where RESULT_ID not in (select res.RESULT_ID from T_RVW_RESULTS res ,T_PIDC_A2L pa2l where res.PROJECT_ID = pa2l.PROJECT_ID and res.A2L_ID = pa2l.A2L_FILE_ID);

delete from T_RVW_PARAMETERS where RESULT_ID not in (select res.RESULT_ID from T_RVW_RESULTS res ,T_PIDC_A2L pa2l where res.PROJECT_ID = pa2l.PROJECT_ID and res.A2L_ID = pa2l.A2L_FILE_ID);

delete from T_RVW_FUNCTIONS where RESULT_ID not in (select res.RESULT_ID from T_RVW_RESULTS res ,T_PIDC_A2L pa2l where res.PROJECT_ID = pa2l.PROJECT_ID and res.A2L_ID = pa2l.A2L_FILE_ID);

delete from T_RVW_ATTR_VALUES where RESULT_ID not in (select res.RESULT_ID from T_RVW_RESULTS res ,T_PIDC_A2L pa2l where res.PROJECT_ID = pa2l.PROJECT_ID and res.A2L_ID = pa2l.A2L_FILE_ID);

delete from T_RVW_RESULTS where ORG_RESULT_ID not in (select res.RESULT_ID from T_RVW_RESULTS res ,T_PIDC_A2L pa2l where res.PROJECT_ID = pa2l.PROJECT_ID and res.A2L_ID = pa2l.A2L_FILE_ID);

delete from T_RVW_RESULTS where RESULT_ID not in (select res.RESULT_ID from T_RVW_RESULTS res ,T_PIDC_A2L pa2l where res.PROJECT_ID = pa2l.PROJECT_ID and res.A2L_ID = pa2l.A2L_FILE_ID);

--Update PIDC_A2L_ID column in T_RVW_RESULTS

-- The results from query 2 pointing to non active(old) revisions in PIDC must be DELETED for migration purpose.
-- This deletion was done because data migration of T_PIDC_A2L to T_RVW_RESULTS is done on res.PROJECT_ID and res.A2L_ID columns which will results in 
-- inconsistent data with variant pointing to one PIDC Revision and PIDC_A2L_ID pointing to another revision
delete from T_RVW_PARTICIPANTS where RESULT_ID in (select res.RESULT_ID from T_RVW_RESULTS res inner join TABV_PROJECT_VARIANTS var  on res.VARIANT_ID = var.VARIANT_ID
                                   inner join T_PIDC_VERSION pver on var.PIDC_VERS_ID = pver.PIDC_VERS_ID 
                                   inner join TABV_PROJECTIDCARD pidc on pver.PROJECT_ID = pidc.PROJECT_ID and pver.PRO_REV_ID <> pidc.PRO_REV_ID);

delete from T_RVW_FILES where RESULT_ID in (select res.RESULT_ID from T_RVW_RESULTS res inner join TABV_PROJECT_VARIANTS var  on res.VARIANT_ID = var.VARIANT_ID
                                   inner join T_PIDC_VERSION pver on var.PIDC_VERS_ID = pver.PIDC_VERS_ID 
                                   inner join TABV_PROJECTIDCARD pidc on pver.PROJECT_ID = pidc.PROJECT_ID and pver.PRO_REV_ID <> pidc.PRO_REV_ID);
                                                                                                                                            
delete from T_RVW_PARAMETERS where RESULT_ID in (select res.RESULT_ID from T_RVW_RESULTS res inner join TABV_PROJECT_VARIANTS var  on res.VARIANT_ID = var.VARIANT_ID
                                   inner join T_PIDC_VERSION pver on var.PIDC_VERS_ID = pver.PIDC_VERS_ID 
                                   inner join TABV_PROJECTIDCARD pidc on pver.PROJECT_ID = pidc.PROJECT_ID and pver.PRO_REV_ID <> pidc.PRO_REV_ID);
                                   
delete from T_RVW_FUNCTIONS where RESULT_ID in (select res.RESULT_ID from T_RVW_RESULTS res inner join TABV_PROJECT_VARIANTS var  on res.VARIANT_ID = var.VARIANT_ID
                                   inner join T_PIDC_VERSION pver on var.PIDC_VERS_ID = pver.PIDC_VERS_ID 
                                   inner join TABV_PROJECTIDCARD pidc on pver.PROJECT_ID = pidc.PROJECT_ID and pver.PRO_REV_ID <> pidc.PRO_REV_ID);
                                                                    
delete from T_RVW_ATTR_VALUES where RESULT_ID in (select res.RESULT_ID from T_RVW_RESULTS res inner join TABV_PROJECT_VARIANTS var  on res.VARIANT_ID = var.VARIANT_ID
                                   inner join T_PIDC_VERSION pver on var.PIDC_VERS_ID = pver.PIDC_VERS_ID 
                                   inner join TABV_PROJECTIDCARD pidc on pver.PROJECT_ID = pidc.PROJECT_ID and pver.PRO_REV_ID <> pidc.PRO_REV_ID);
                                   
delete from T_RVW_RESULTS where ORG_RESULT_ID in (select res.RESULT_ID from T_RVW_RESULTS res inner join TABV_PROJECT_VARIANTS var  on res.VARIANT_ID = var.VARIANT_ID
                                   inner join T_PIDC_VERSION pver on var.PIDC_VERS_ID = pver.PIDC_VERS_ID 
                                   inner join TABV_PROJECTIDCARD pidc on pver.PROJECT_ID = pidc.PROJECT_ID and pver.PRO_REV_ID <> pidc.PRO_REV_ID);                                     
                                  
delete from T_RVW_RESULTS where RESULT_ID in (select res.RESULT_ID from T_RVW_RESULTS res inner join TABV_PROJECT_VARIANTS var  on res.VARIANT_ID = var.VARIANT_ID
                                   inner join T_PIDC_VERSION pver on var.PIDC_VERS_ID = pver.PIDC_VERS_ID 
                                   inner join TABV_PROJECTIDCARD pidc on pver.PROJECT_ID = pidc.PROJECT_ID and pver.PRO_REV_ID <> pidc.PRO_REV_ID);
--Rows in CDR Results from query 3 with null VARIANT_IDS are matched with A2L files in T_PIDC_A2L
*/

-------------------------------------------------------------------------------------------------

--- Population of data in PIDC_A2L_ID in T_RVW_RESULTS

-------------------------------------------------------------------------------------------------                                   
UPDATE T_RVW_RESULTS res SET(PIDC_A2L_ID) = (select pa2l.PIDC_A2L_ID from T_PIDC_A2L pa2l where res.PROJECT_ID = pa2l.PROJECT_ID and res.A2L_ID = pa2l.A2L_FILE_ID);

COMMIT;

spool off