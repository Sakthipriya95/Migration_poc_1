spool c:\temp\table_data.log


---------------------------------------------------------------

--  2015-09-10
---------------------------------------------------------------

---------------------------------------------------------------
----ICDM-1548  - additional changes regarding ICDM-1547
---- Removing the constant in TABV_COMMON_PARAMS
---------------------------------------------------------------
---------------------------------------------------------------
--TABV_COMMON_PARAMS
---------------------------------------------------------------
DELETE FROM TABV_COMMON_PARAMS WHERE PARAM_ID='WP_ROOT_VALUE_ID_1';
DELETE FROM TABV_COMMON_PARAMS WHERE PARAM_ID='WP_ROOT_VALUE_ID_2';

COMMIT;


--------------------------------------------------------
--  2015-09-28
-------------------------------------------------------- 

--------------------------------------------------------
--- iCDM-1521 - exception message to avoid illegal variant names in vCDM
--------------------------------------------------------

insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('ATTR_EDITOR','vCDM_VALID_NAME'
				,'The Variant Name contains invalid characters!\nOnly these characters are allowed: [a..z] | [A..Z] or numbers [0..9] or underscore [_]'
				,'Der Variantenname enthält ungültige Zeichen.\nAusschliesslich folgende Zeichen sind erlaubt: [a..z] | [A..Z] oder Ziffern [0..9] oder Unterstriche [_]')
;

commit;
--------------------------------------------------------
--iCDM-1611
--Update T_CHARACTERISTICS to set FOCUS_MATRIX_YN to 'N' for the attr classes 'Doc' & 'Sthr'
----------------------------------------------------------
update T_CHARACTERISTICS set FOCUS_MATRIX_YN = 'N' where char_name_eng in ('Doc','Sthr');

commit;

delete from TABV_COMMON_PARAMS WHERE PARAM_ID = 'iCDM_CLIENT_VERSION';
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('iCDM_CLIENT_VERSION','iCDM Client''s current version','1.21.0',1);
commit;

spool off