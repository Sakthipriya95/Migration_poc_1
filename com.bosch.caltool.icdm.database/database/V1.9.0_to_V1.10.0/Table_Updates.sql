spool c:\temp\table_updates.log

--------------------------------------------------------
--  May-16-2014
-------------------------------------------------------- 

------------------------------------------
-- New table T_Links to maintain all usecase and usecase section links
-- ICDM-763
------------------------------------------

CREATE TABLE T_LINKS
(
  LINK_ID NUMBER PRIMARY KEY,
  NODE_ID NUMBER NOT NULL,
  NODE_TYPE VARCHAR2(5) NOT NULL,
  LINK_URL VARCHAR2(1000) NOT NULL,
  DESC_ENG VARCHAR2(2000) NOT NULL,
  DESC_GER VARCHAR2(2000),
  CREATED_USER VARCHAR2(30) NOT NULL,
  CREATED_DATE TIMESTAMP WITH TIME ZONE NOT NULL,
  MODIFIED_USER VARCHAR2(30),
  MODIFIED_DATE TIMESTAMP WITH TIME ZONE,
  VERSION NUMBER  NOT NULL,

  CONSTRAINT "T_LINKS_UK1" UNIQUE ("NODE_ID", "NODE_TYPE", "DESC_ENG")
);


----------------------------------------------------------
-- DB updates in T_RVW_RESULTS table source type for existing reviews
-- ICDM-729
--------------------------------------------------------

-- update for Group mapping
update T_RVW_RESULTS set source_type='GRP' where source_type is null  and grp_work_pkg is not null and grp_work_pkg <> '<CUSTOM_REVIEW>';

-- update for Custom Review
update T_RVW_RESULTS set source_type='CUSTOM' where source_type is null   and grp_work_pkg = '<CUSTOM_REVIEW>';

-- update for Fc to Wp Mapping
update T_RVW_RESULTS set source_type='FC_WP' where source_type is null and grp_work_pkg is null and fc2wp_id is not null;

-- update for No definition
update T_RVW_RESULTS set source_type='NOT_DEF'  where source_type is null and grp_work_pkg is null and fc2wp_id is  null;

--------------------------------------------------------
--  Version update script
--------------------------------------------------------      
delete from TABV_COMMON_PARAMS WHERE PARAM_ID = 'iCDM_CLIENT_VERSION';
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('iCDM_CLIENT_VERSION','iCDM Client''s current version','1.10.0',1);

commit;  

spool off