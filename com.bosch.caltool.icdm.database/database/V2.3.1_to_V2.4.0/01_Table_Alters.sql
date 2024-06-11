spool c:\temp\01_Table_Alters.log

-------------------------------------------------------------
--  ALM TaskId : 333276
--  Create Query for T_REGION, T_WORKPACKAGE_DIVISION_CDL
-------------------------------------------------------------

CREATE TABLE t_region (
    region_id       NUMBER NOT NULL,
    region_code     VARCHAR2(4) NOT NULL,
    region_name_eng   VARCHAR2(50) NOT NULL,
    region_name_ger   VARCHAR2(50),
    CREATED_DATE TIMESTAMP (6) NOT NULL ENABLE, 
	CREATED_USER VARCHAR2(30 BYTE) NOT NULL ENABLE, 
	MODIFIED_DATE TIMESTAMP (6), 
	MODIFIED_USER VARCHAR2(30 BYTE), 
	VERSION NUMBER NOT NULL ENABLE,
	
    CONSTRAINT t_region_pk PRIMARY KEY ( region_id ),
    CONSTRAINT t_region_code_un UNIQUE ( region_code ),
	CONSTRAINT t_region_name_un UNIQUE ( region_name_eng )
);

CREATE TABLE t_workpackage_division_cdl (
    wp_div_cdl_id   NUMBER NOT NULL,
    region_id       NUMBER NOT NULL,
    wp_div_id       NUMBER NOT NULL,
    user_id         NUMBER NOT NULL,
    CREATED_DATE TIMESTAMP (6) NOT NULL ENABLE, 
	CREATED_USER VARCHAR2(30 BYTE) NOT NULL ENABLE, 
	MODIFIED_DATE TIMESTAMP (6), 
	MODIFIED_USER VARCHAR2(30 BYTE), 
	VERSION NUMBER NOT NULL ENABLE,
	CONSTRAINT t_wp_div_cdl_pk PRIMARY KEY ( wp_div_cdl_id ),
    CONSTRAINT t_wp_div_cdl_un UNIQUE ( wp_div_id, region_id ),
	CONSTRAINT t_wp_div_cdl_region_id_fk FOREIGN KEY ( region_id ) REFERENCES t_region ( region_id ),	
	CONSTRAINT t_wp_div_cdl_wp_div_id_fk FOREIGN KEY ( wp_div_id ) REFERENCES t_workpackage_division ( wp_div_id ),
	CONSTRAINT t_wp_div_cdl_user_id_fk FOREIGN KEY ( user_id ) REFERENCES tabv_apic_users ( user_id )
);


---------------------------------------------------------------------
--  ALM TaskId : 333282
--  Alter Query for T_WORKPACKAGE_DIVISION
---------------------------------------------------------------------  
ALTER TABLE t_workpackage_division ADD (wpd_comment   VARCHAR2(4000 CHAR));

---------------------------------------------------------------------
--  ALM Story : 307426
--------------------------------------------------------------------- 
CREATE TABLE T_WEBFLOW_ELEMENT
( 
  WEBFLOW_ID NUMBER NOT NULL,
  ELEMENT_ID NUMBER(15,0)  NOT NULL ,
  VARIANT_ID NUMBER(15,0)  NOT NULL ,
  IS_DELETED VARCHAR2(1 BYTE) DEFAULT 'N' NOT NULL, 
  CREATED_DATE TIMESTAMP (6) DEFAULT sysdate NOT NULL ENABLE, 
  MODIFIED_DATE TIMESTAMP (6), 
  CREATED_USER VARCHAR2(100 BYTE) DEFAULT user NOT NULL ENABLE, 
  MODIFIED_USER VARCHAR2(100 BYTE), 
  VERSION NUMBER NOT NULL ENABLE, 
  
  CONSTRAINT WEBFLOWID_PK PRIMARY KEY (WEBFLOW_ID)
);

--Making varinat id as foreign key
ALTER TABLE T_WEBFLOW_ELEMENT ADD CONSTRAINT T_WEBFLOW_ELEM_FK_VAR_ID 
  FOREIGN KEY (VARIANT_ID)
  REFERENCES TABV_PROJECT_VARIANTS (VARIANT_ID);

---------------------------------------------------------------------
--ALM Story : 340189
---------------------------------------------------------------------
ALTER TABLE tabv_use_cases modify (desc_ger varchar2(4000), desc_eng varchar2(4000));
ALTER TABLE tabv_use_case_sections modify (desc_ger varchar2(4000), desc_eng varchar2(4000));
ALTER TABLE tabv_use_case_groups modify (desc_ger varchar2(4000), desc_eng varchar2(4000));

---------------------------------------------------------------------
--ALM Story : 345500
---------------------------------------------------------------------

ALTER TABLE t_questionnaire MODIFY WP_DIV_ID NUMBER NOT NULL;

spool off