spool c:\temp\table_alters.log

--------------------------------------------------------
--  2015-02-19
-------------------------------------------------------- 

--------------------------------------------------------
--- ICDM-1292 - Data model changes in iCDM related to Component package data import
--------------------------------------------------------
----------------------------------------------------------------------
--T_COMP_PKG--
----------------------------------------------------------------------
alter TABLE T_COMP_PKG add 
    (
        COMP_PKG_TYPE   VARCHAR2 (5)  DEFAULT 'N' NOT NULL ,
        SSD_NODE_ID     NUMBER ,
        SSD_PARAM_CLASS VARCHAR2 (25) ,
        SSD_USECASE     VARCHAR2 (25)
    ) ;

----------------------------------------------------------------------
--T_CP_RULE_ATTRS--
----------------------------------------------------------------------
CREATE
    TABLE T_CP_RULE_ATTRS
    (
        CP_RULE_ATTR_ID NUMBER NOT NULL ,
        COMP_PKG_ID     NUMBER NOT NULL ,
        ATTR_ID         NUMBER (15) NOT NULL ,
        CREATED_DATE    TIMESTAMP NOT NULL ,
        CREATED_USER    VARCHAR2 (30) NOT NULL ,
        MODIFIED_DATE   TIMESTAMP WITH TIME ZONE ,
        MODIFIED_USER   VARCHAR2 (30) ,
        VERSION         NUMBER NOT NULL
    ) ;
CREATE
    INDEX T_CP_RULE_ATTRS__IDX1 ON T_CP_RULE_ATTRS
    (
        COMP_PKG_ID ASC
    ) ;
ALTER TABLE T_CP_RULE_ATTRS ADD CONSTRAINT T_CP_ATTRS_PK PRIMARY KEY (
CP_RULE_ATTR_ID ) ;
ALTER TABLE T_CP_RULE_ATTRS ADD CONSTRAINT T_CP_RULE_ATTRS__UK1 UNIQUE (
COMP_PKG_ID , ATTR_ID ) ;
ALTER TABLE T_CP_RULE_ATTRS ADD CONSTRAINT T_CP_RULE_ATTRS_FK1 FOREIGN KEY (
COMP_PKG_ID ) REFERENCES T_COMP_PKG ( COMP_PKG_ID ) ;
ALTER TABLE T_CP_RULE_ATTRS ADD CONSTRAINT T_CP_RULE_ATTRS_FK2 FOREIGN KEY (
ATTR_ID ) REFERENCES TABV_ATTRIBUTES ( ATTR_ID ) ;


--------------------------------------------------------
--  2015-02-20
-------------------------------------------------------- 

--------------------------------------------------------
--- ICDM-1272 - Data model changes in iCDM related to Storing of tool tips
--------------------------------------------------------

create table T_MESSAGES (
MESSAGE_ID number primary key,
GROUP_NAME varchar(200) not null,
NAME varchar(200) not null,
MESSAGE_TEXT varchar(4000) not null,
MESSAGE_TEXT_GER  varchar2(4000),
VERSION       NUMBER not null,
CONSTRAINT T_NAME_UNIQ UNIQUE (GROUP_NAME,NAME)
);

---------------------------------------------
-- 10-Mar-2015
-- iCDM-1317
-- Alter T_RVW_ATTR_VALUES to add used flag 
---------------------------------------------
      
ALTER TABLE T_RVW_ATTR_VALUES
ADD USED VARCHAR2(1);
  
--Update existing recods to N (NOT_USED) where apic value_id is not available 
update T_RVW_ATTR_VALUES set used = 'N' where (value_id is null and used is null);
commit;  
 
-------------------------------------------------
--11-Mar-2015
--iCDM-1326
--Alter t_comp_pkg to add ssd_vers_node_id for creation of labellist for ssd release
----------------------------------------------------------
alter table T_COMP_PKG
add SSD_VERS_NODE_ID number;


------------------------------------------------------------
spool off



