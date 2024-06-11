spool c:\temp\13_Table_Data2.log

---------------------------------------------------------------
--ICDM-233015
--Insert new Value in TABV_COMMON_PARAMS for Variant in Customer Calibration Data Management System Attribute ID
---------------------------------------------------------------

declare
    v_attr_id number;
    v_attr_grp_id number;
begin
    select seqv_attributes.nextval into v_attr_id from dual;   
    SELECT GROUP_ID INTO v_attr_grp_id FROM TABV_ATTR_GROUPS where GROUP_NAME_ENG = 'Project Information';
    
 INSERT INTO TABV_ATTRIBUTES(
   ATTR_ID, ATTR_NAME_ENG, ATTR_NAME_GER, 
   ATTR_DESC_ENG, ATTR_DESC_GER, GROUP_ID, 
   VALUE_TYPE_ID, CREATED_DATE, MODIFIED_DATE, 
   CREATED_USER, MODIFIED_USER, NORMALIZED_FLAG, 
   DELETED_FLAG, ATTR_LEVEL, MANDATORY, 
   UNITS, FORMAT,  
   PART_NUMBER_FLAG, SPEC_LINK_FLAG, ATTR_SECURITY, 
   VALUE_SECURITY, CHAR_ID, CHANGE_COMMENT, 
   EADM_NAME, GROUP_ATTR_FLAG, MOVE_DOWN_YN) 
 VALUES (v_attr_id,
  'Variant Name in customer CDM system','Variant Name in customer CDM system', 'The name of the variant in the customers calibration data management system (e.G.: Creta).','Name der Variante im Kunden Applikations-Management-System (z.B.: Creta).',
  v_attr_grp_id,1,sys_extract_utc(systimestamp),null, USER, null, 'N', 'N',null, 'N', '-', null,  'N','N','E','E', null,' ', 'Variant_Name_in_customer_CDM_system',null, null);
    
 INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) 
    values ('VARIANT_IN_CUST_CDMS_ATTR_ID','Variant in Customer Calibration Data Management System Attribute ID', TO_CHAR(v_attr_id),1);
 
 commit;
 
end;
/

    
    
-----------------------------------------------------------------------------------------------
--Task 234241 - getPidcVersionStatistics should return also the mandatory attributes
-----------------------------------------------------------------------------------------------
UPDATE T_MESSAGES SET MESSAGE_TEXT = 'Total attributes : {0}\t\t Used attributes : {1}\t\t Not Used attributes : {2}\t\t Undefined attributes : {3}\t\tNew attributes : {4}\t\tLast modified date : {5}\nProject use case items : {6}\t\tCoverage PIDC : {7} of {8} attributes\t\tCoverage Mandatory Attributes : {9} of {10}\t\tUnrated Focus Matrix : {11} of {12}' 
WHERE GROUP_NAME='PIDC_EDITOR' AND NAME='PROJECT_STATISTICS_PIDCVERS';


---------------------------------------------------------------
--236525
--Product version update script
---------------------------------------------------------------

DELETE FROM TABV_COMMON_PARAMS WHERE PARAM_ID = 'iCDM_CLIENT_VERSION';
INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) 
    values ('iCDM_CLIENT_VERSION','iCDM Client''s current version','2.0.0',1);

    
-----------------------------------------------------------------------------------------------
--Task 238117 - DB changes for mandatory rule set for a PIDC
-----------------------------------------------------------------------------------------------

 
   
declare
    v_attr_id number;
    v_attr_val_id number;
    v_attr_grp_id number;
    
   CURSOR c_rulesets IS 
   SELECT RSET_ID,RSET_NAME, DESC_ENG, DESC_GER,DELETED_FLAG FROM T_RULE_SET;
   
   ruleset_row c_rulesets%ROWTYPE;
begin
	
 --Generate attribute ID
  SELECT seqv_attributes.nextval INTO v_attr_id FROM dual;   
  SELECT GROUP_ID INTO v_attr_grp_id FROM TABV_ATTR_GROUPS where GROUP_NAME_ENG = 'Project Information';
    
   
 -- INSERTING into TABV_ATTRIBUTES
   INSERT INTO TABV_ATTRIBUTES(
   ATTR_ID, ATTR_NAME_ENG, ATTR_NAME_GER, 
   ATTR_DESC_ENG, ATTR_DESC_GER, GROUP_ID, 
   VALUE_TYPE_ID, CREATED_DATE, MODIFIED_DATE, 
   CREATED_USER, MODIFIED_USER, NORMALIZED_FLAG, 
   DELETED_FLAG, ATTR_LEVEL, MANDATORY, 
   UNITS, FORMAT,  
   PART_NUMBER_FLAG, SPEC_LINK_FLAG, ATTR_SECURITY, 
   VALUE_SECURITY, CHAR_ID, CHANGE_COMMENT, 
   EADM_NAME, GROUP_ATTR_FLAG, MOVE_DOWN_YN) 
   VALUES (v_attr_id,
   'Mandatory Rule Set','Verpflichtendes Rule Set', 'The chosen Rule Set will be part of every review 
   that is performed in the current PIDC version as secondary source of rules. Use a mandatory rule set when you’ve labels in your project that must match project specific
   rules (e.G. on customer demand)',
   'Das gewählte Rule Set wird als zweite Quelle für Review Regeln in jedem Review der PIDC Version benutzt.Wählen Sie ein verpflichtendes Rule Set wenn es
   (beispielsweise auf Kundenwunsch) Labels gibt, die immer mit demselben Wert appliziert sein müssen',
   v_attr_grp_id,1,sys_extract_utc(systimestamp),null, USER, null, 'N', 'N',null, 'Y', '-', ' ',  'N','N','N','N', null,'Created during release', 'MandatoryRuleSet','N', 'N');
   
  --INSERTING into TABV_COMMON_PARAMS
  INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) 
  values ('CDR_MANDATORY_RULESET_ATTR_ID','Mandatory Ruleset Attribute ID', TO_CHAR(v_attr_id),1);
 
       
   -- INSERTING into TABV_ATTR_VALUES(various rulesets) with cursor
   OPEN c_rulesets; 
   LOOP 
   FETCH c_rulesets into ruleset_row; 
      EXIT WHEN c_rulesets%notfound; 
      
	   --Generate attribute value ID
	  SELECT seqv_attributes.nextval INTO v_attr_val_id FROM dual;
	  
	  INSERT into TABV_ATTR_VALUES (VALUE_ID,ATTR_ID,VALUE_DESC_ENG,VALUE_DESC_GER,TEXTVALUE_ENG,DELETED_FLAG,CLEARING_STATUS) 
	  values (v_attr_val_id,v_attr_id,ruleset_row.DESC_ENG,ruleset_row.DESC_GER,ruleset_row.RSET_NAME,ruleset_row.DELETED_FLAG,'Y');
	  
	  UPDATE T_RULE_SET SET ATTR_VALUE_ID = v_attr_val_id WHERE RSET_ID=ruleset_row.RSET_ID;
      
   END LOOP; 
   CLOSE c_customers; 
    
   commit;
 
end;
/

declare
    v_attr_val_id number;
begin
select value_id into v_attr_val_id from tabv_attr_values where TEXTVALUE_ENG='GROUP in A2L file';

Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) 
    values ('GROUP_MAPPING_ID','A2L Group Mapping ID',TO_CHAR(v_attr_val_id),1);
    
 commit;
 
end;
/

commit;


spool off