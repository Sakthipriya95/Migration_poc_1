spool c:\temp\32_QnaireMig_Step1B-Validations.log


-------------------------------------------------
-- Step 1 Validation Scripts
-------------------------------------------------
-------------------------------------------------
-- Checks whether the new questionnaire definition version with both general and exisiting is created 
--   for all the qnaire versions used in questionnaire response
--
-- Expecteted result : no qnaire response present without 'Migrated version'
-------------------------------------------------
set serveroutput on size unlimited
DECLARE 
	v_count number;
	v_general_ques_id number;
begin

    DBMS_OUTPUT.PUT_LINE(to_char( SYSDATE, 'YYYY-MM-DD HH24:MI:SS' ) || ' - Start validation');

	select param_value into v_general_ques_id from tabv_common_params where param_id = 'GENERAL_QNAIRE_ID';
	FOR v_qnaire_version in (select * from T_QUESTIONNAIRE_VERSION where qnaire_vers_id in (select distinct qnaire_vers_id from t_rvw_qnaire_response ) ) 
	LOOP
		if v_general_ques_id <> v_qnaire_version.qnaire_id then
			select count(1) into v_count from T_QUESTIONNAIRE_VERSION where qnaire_id = v_qnaire_version.qnaire_id and DESC_ENG like '%Migrated version%';
			IF v_count = 0 THEN     
				dbms_output.put_line('Migrated version for Qnaire Version not found : '|| v_qnaire_version.qnaire_vers_id );
			END IF;
		end if;
	END LOOP;

    DBMS_OUTPUT.PUT_LINE(to_char( SYSDATE, 'YYYY-MM-DD HH24:MI:SS' ) || ' - End validation');

END;
/

spool off
