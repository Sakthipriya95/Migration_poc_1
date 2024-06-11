spool c:\temp\table_data.log

---------------------------------------------------------------

--  2016-2-05
---------------------------------------------------------------

-------------------------------------------------
-- ICDM-1339
-- Set IsBitwise = 'N' to avoid change markers in delta reviews
----------------------------------------------------------
update T_RVW_PARAMETERS
   set isBitwise = 'N'
 where isBitwise is null
; 

---------------------------------------------------------------
----ICDM-1836
---------------------------------------------------------------
---------------------------------------------------------------
-- TABV_COMMON_PARAMS
---------------------------------------------------------------

--Insert new Value in TABV_COMMON_PARAMS for defining level attribute that defines the mandatory attribute

Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('MANDATORY_LEVEL_ATTR','Level attribute that defines mandatory attr list',<level attr id that defines mandatory attributes> ,1);
commit;

--
-- fill unique function versions
--
insert into T_FUNCVERS_UNIQUE
(ID, FUNCNAME, FUNCVERSION, CREATED_DATE)
select ID 
     , FUNCNAME
     , FUNCVERSION
     , CREATED_DATE
  from T_FUNCVERS_UNIQUE@dgspro.world@k5esk_villa_ro
;

---------------------------------------------------------------
----ICDM-1910
---------------------------------------------------------------
---------------------------------------------------------------
-- TABV_COMMON_PARAMS
---------------------------------------------------------------

--Insert new Value in TABV_COMMON_PARAMS for ICDM v1.23.0

delete from TABV_COMMON_PARAMS WHERE PARAM_ID = 'iCDM_CLIENT_VERSION';
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('iCDM_CLIENT_VERSION','iCDM Client''s current version','1.23.0',1);
commit;
spool off