spool c:\temp\table_alters.log



-------------------------------------------------
--iCDM-1560
--Alter tabv_atribtues to add EADM when an attribute is created/edited
----------------------------------------------------------
alter table TABV_ATTRIBUTES
add EADM_NAME varchar2(100);

------------------------------------------------------------

ALTER TABLE TABV_ATTRIBUTES ADD CONSTRAINT UK_ATTR UNIQUE ( EADM_NAME ) ;


----------------------------------------------------------------------------------------------------
--ICDM-1641
--Alter T_FOCUS_MATRIX to add LINK column
----------------------------------------------------------------------------------------------------
ALTER TABLE T_FOCUS_MATRIX add LINK varchar2(1000);


----------------------------------------------------------------------------------------------------
--ICDM-1625
--Alter T_FOCUS_MATRIX to add USE_CASE_ID,SECTION_ID,ATTR_ID,IS_DELETED  columns
----------------------------------------------------------------------------------------------------
ALTER TABLE T_FOCUS_MATRIX ADD(
  USE_CASE_ID  NUMBER  ,
  SECTION_ID   NUMBER,
  ATTR_ID        NUMBER ,
  IS_DELETED   VARCHAR2(1 BYTE)
  )   
;  
----------------------------------------------------------------------------------------------------
--Update data into the newly added columns in  T_FOCUS_MATRIX ( USE_CASE_ID,SECTION_ID,ATTR_ID,IS_DELETED  columns)
----------------------------------------------------------------------------------------------------
UPDATE T_FOCUS_MATRIX mat
SET (USE_CASE_ID,SECTION_ID,ATTR_ID )= (SELECT USE_CASE_ID,SECTION_ID,ATTR_ID
               FROM TABV_UCP_ATTRS ucp
              WHERE ucp.UCPA_ID=mat.UCPA_ID)
where USE_CASE_ID is null  and ATTR_ID is null
;
----------------------------------------------------------------------------------------------------
--Update the T_FOCUS_MATRIX to make the  USE_CASE_ID, ATTR_ID columns as NOT NULL )
----------------------------------------------------------------------------------------------------

ALTER TABLE T_FOCUS_MATRIX MODIFY  (USE_CASE_ID   NOT  NULL,ATTR_ID  NOT NULL)
;

ALTER TABLE T_FOCUS_MATRIX MODIFY  (UCPA_ID  NULL)
;

ALTER TABLE T_FOCUS_MATRIX DROP CONSTRAINT T_FOCUS_MATRIX_UK1
;

ALTER TABLE T_FOCUS_MATRIX ADD CONSTRAINT  T_FOCUS_MATRIX_UK_01 UNIQUE (USE_CASE_ID,SECTION_ID,ATTR_ID,PIDC_VERS_ID)
;

-----------------------------------------------------------------------------------------------------
commit;


---------------------------------------------------------------

--  2015-10-29
---------------------------------------------------------------

---------------------------------------------------------------
----ICDM-1691  - 
----Prepare data script to fill values in new column EADM_NAME in TABV_ATTRIBUTES table
---------------------------------------------------------------
---------------------------------------------------------------
--  TABV_ATTRIBUTES
---------------------------------------------------------------

UPDATE TABV_ATTRIBUTES set EADM_NAME=regexp_replace(ATTR_NAME_ENG,'[ÄÖÜäöü/ß ]','_') where EADM_NAME IS NULL
;

ALTER TABLE TABV_ATTRIBUTES MODIFY (EADM_NAME NOT NULL)
;

COMMIT
;

spool off



