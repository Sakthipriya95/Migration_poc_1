spool c:\temp\table_updates.log

--------------------------------------------------------
--  2014-11-21
-------------------------------------------------------- 

------------------------------------------
--  ICDM-1114 increase the length of the partnumber column in TabV_xxx_ATTR
------------------------------------------

ALTER TABLE tabv_project_attr MODIFY part_number           VARCHAR2(255);
ALTER TABLE tabv_variants_attr MODIFY part_number          VARCHAR2(255);
ALTER TABLE tabv_proj_sub_variants_attr MODIFY part_number VARCHAR2(255);
ALTER TABLE t_pidc_change_history MODIFY (old_part_number VARCHAR2(255), new_part_number VARCHAR2(255));

------------------------------------------
--  ICDM-1117 New Entry in the Common param table for new Pidc creation.
------------------------------------------
insert into tabv_common_params values('MAIL_NEW_PIDC','Mail subject for new PIDC','PIDC created',1);
commit;

------------------------------------------
--  ICDM-1113 Adding a new column in T_PARAMETER for German Long Name
------------------------------------------
ALTER TABLE T_PARAMETER ADD LONGNAME_GER VARCHAR2(1024);

------------------------------------------
--  ICDM-1134 New Entry in the Common param table for Values IDs to be excluded from search for Level 1 Attribute
------------------------------------------
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) 
    values ('PIDC_SEARCH_EXCLUDE_VAL_LEVEL1','Values IDs to be excluded from search for Level 1 Attribute during PIDC Search.(comma separated)','<Comma separated Value IDs>',1);

commit;


delete from TABV_COMMON_PARAMS WHERE PARAM_ID = 'iCDM_CLIENT_VERSION';
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('iCDM_CLIENT_VERSION','iCDM Client''s current version','1.16.0',1);
COMMIT;

spool off



