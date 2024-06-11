spool c:\temp\03_Table_Data.log


---------------------------------------------------------------
----ICDM-2430
---------------------------------------------------------------
--For all the level attributes except the attribute with level=-30 , the MOVE_DOWN_YN is updated to N (since it should not be possible to move to var/subvar levels
UPDATE tabv_attributes SET MOVE_DOWN_YN='N' WHERE attr_level in (1,2,3,4,-1,-2,-3,-20,-10);
commit;

--For the attr_id configured in  tabv_common_params with id WP_TYPE_ATTR_ID, the MOVE_DOWN_YN is updated to N 
UPDATE tabv_attributes
SET MOVE_DOWN_YN='N'
WHERE attr_id= ( select param_value from tabv_common_params where param_id='WP_TYPE_ATTR_ID');
commit;

--For the attr_id configured in  tabv_common_params with id WP_ROOT_GROUP_ATTR_ID, the MOVE_DOWN_YN is updated to N 
UPDATE tabv_attributes
SET MOVE_DOWN_YN='N'
WHERE attr_id= ( select param_value from tabv_common_params where param_id='WP_ROOT_GROUP_ATTR_ID');
commit;

--For the attr_id configured in  tabv_common_params with id QUOT_ATTR_ID, the MOVE_DOWN_YN is updated to N 
UPDATE tabv_attributes
SET MOVE_DOWN_YN='N'
WHERE attr_id= ( select param_value from tabv_common_params where param_id='QUOT_ATTR_ID');
commit;

--For the attr_id configured in  tabv_common_params with id PIDC_DIVISION_ATTR, the MOVE_DOWN_YN is updated to N 
UPDATE tabv_attributes
SET MOVE_DOWN_YN='N'
WHERE attr_id= ( select param_value from tabv_common_params where param_id='PIDC_DIVISION_ATTR');
commit;

--For the Top level entity User.
Insert into TABV_TOP_LEVEL_ENTITIES
   (ENT_ID, ENTITY_NAME, LAST_MOD_DATE, VERSION)
 Values
   (5, 'APIC_USER', sysdate, 1);
   
commit;

    
insert into t_functions (ID, NAME, LONGNAME,ISCUSTFUNC,created_date,created_user,modifed_date,modifed_user,big_function,relevant_name)  
    VALUES (-1,'<NOT-ASSIGNED>',null,'N',sysdate,'DGS_ICDM',null,null,'N','N');

commit;

---------------------------------------------------------------
--ICDM-2584
---------------------------------------------------------------

INSERT INTO T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT)
VALUES 
('CDFX_EXPORT_DIALOG', 'REVIWED_PARAM_STATUS_SCORE_WITH_LOCK', 'Reviewed Parameter(score 8 and 9; review locked) with status from review score');

INSERT INTO T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT)
VALUES 
('CDFX_EXPORT_DIALOG', 'REVIWED_PARAM_STATUS_100_WITH_LOCK', 'Reviewed Parameter (score 8 and 9; review locked) with status 100%');


COMMIT;

---------------------------------------------------------------
----ICDM-2579
---------------------------------------------------------------
-- Insert into t_messages table , message while saving delta review result as normal review result
INSERT INTO T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) VALUES('REVIEW_MESSAGE','DELTA_TO_NORMAL_RVW','Could not find parent reviews for any of the parameters reviewed. Saved the review as a Normal review rather than Delta review.');
commit;

---------------------------------------------------------------
----ICDM-2578
---------------------------------------------------------------
-- Insert into t_messages table , message when there are invalid characters entered in open iCDM link dialog
INSERT INTO T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) VALUES('OPEN_ICDM_LINK','INVALID_CHARACTERS','The ID you entered contains invalid characters. Please ensure that you copied the right link. Kindly contact iCDM hotline if the problem continues.');
commit;

---------------------------------------------------------------
----ICDM-2600
---- Insert data into T_WP_RESP table
---------------------------------------------------------------
INSERT INTO T_WP_RESP(RESP_NAME) VALUES('R');
INSERT INTO T_WP_RESP(RESP_NAME) VALUES('C');
INSERT INTO T_WP_RESP(RESP_NAME) VALUES('O');
commit;


---------------------------------------------------------------
----ICDM-2626
---------------------------------------------------------------
-- Insert into t_messages table , message when there are predefined attrs of a grouped attr in a PIDC Version
INSERT INTO T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) VALUES('PIDC_EDITOR','PREDEFINED_ATTRS','\nThis attribute is a predefined attribute of grouped attribute :');
commit;

---------------------------------------------------------------
----ICDM-2646
---------------------------------------------------------------
-- Script for Data migration of workpackages and division (DGS) (which are not already present in T_WORKPACKAGE_DIVISION table)into T_WORKPACKAGE_DIVISION table from T_FC2WP view
-- Data migration for other FC2WP types (BEG,DS) needs to be done

DECLARE
    v_wp_division_id NUMBER;
    v_div_attr_id NUMBER;
    v_value_id NUMBER;

BEGIN
  --Generate workpackage division ID
  SELECT seqv_attributes.nextval INTO v_wp_division_id FROM dual;
  
  --Get attribute ID for Division attribute
  SELECT ATTR_ID INTO v_div_attr_id FROM TABV_ATTRIBUTES where ATTR_NAME_ENG = 'Division';
  
  --Get attribute value ID for Division attribute "DGS" (div for FC2WP2 type)
  SELECT VALUE_ID INTO v_value_id FROM TABV_ATTR_VALUES where TEXTVALUE_ENG = 'DGS' AND ATTR_ID=v_div_attr_id;
  
  --INSERTING into T_WORKPAGE_DIVISION_TEST
  INSERT INTO T_WORKPACKAGE_DIVISION(VALUE_ID,CREATED_USER,CREATED_DATE,VERSION,WP_ID)
    SELECT v_value_id,user,sys_extract_utc(systimestamp),1,wp.WP_ID FROM T_WORKPACKAGE wp WHERE NOT EXISTS (SELECT wp_div.WP_ID FROM T_WORKPACKAGE_DIVISION wp_div WHERE wp.WP_ID =wp_div.WP_ID);

  COMMIT;  
END;
/

---------------------------------------------------------------
----ICDM-2646
---------------------------------------------------------------
-- Insert the division and fc2wp type mapping in T_DIV_FC2WP_TYPE table
DECLARE
v_div_attr_id NUMBER;
v_dgs_value_id NUMBER;
v_beg_value_id NUMBER;
v_ds_value_id NUMBER;

BEGIN
  
  --Get attribute ID for Division attribute
  SELECT ATTR_ID INTO v_div_attr_id FROM TABV_ATTRIBUTES where ATTR_NAME_ENG = 'Division';
  
  --Get attribute value ID for Division attribute "DGS" (div for FC2WP2 type)
  SELECT VALUE_ID INTO v_dgs_value_id FROM TABV_ATTR_VALUES where TEXTVALUE_ENG = 'DGS' AND ATTR_ID=v_div_attr_id;
  
   --Get attribute value ID for Division attribute "BEG" (div for FC2WP_BEG2 type)
  SELECT VALUE_ID INTO v_beg_value_id FROM TABV_ATTR_VALUES where TEXTVALUE_ENG = 'BEG' AND ATTR_ID=v_div_attr_id;
  
   --Get attribute value ID for Division attribute "DS" (type not defined yet)
  SELECT VALUE_ID INTO v_ds_value_id FROM TABV_ATTR_VALUES where TEXTVALUE_ENG = 'DS' AND ATTR_ID=v_div_attr_id;
  
  --INSERTING into T_WORKPAGE_DIVISION_TEST
  INSERT ALL 
    INTO T_DIV_FC2WP_TYPE (VALUE_ID,DIVISION_NAME,FC2WP_TYPE) VALUES (v_dgs_value_id,'DGS','FC2WP2')
    INTO T_DIV_FC2WP_TYPE (VALUE_ID,DIVISION_NAME,FC2WP_TYPE) VALUES  (v_beg_value_id,'BEG','FC2WP_BEG2')
    INTO T_DIV_FC2WP_TYPE (VALUE_ID,DIVISION_NAME,FC2WP_TYPE) VALUES (v_ds_value_id,'DS','')
  SELECT * FROM dual;
  
  COMMIT;  
  
END;
/

---------------------------------------------------------------
--ICDM-2634
--Product version update script
---------------------------------------------------------------

DELETE FROM TABV_COMMON_PARAMS WHERE PARAM_ID = 'iCDM_CLIENT_VERSION';
INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) 
    values ('iCDM_CLIENT_VERSION','iCDM Client''s current version','1.29.0',1);
    
commit;

spool off