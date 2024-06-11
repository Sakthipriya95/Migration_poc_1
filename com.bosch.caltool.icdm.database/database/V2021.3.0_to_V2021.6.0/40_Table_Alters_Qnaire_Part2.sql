spool c:\temp\40_Table_Alters_Qnaire_Part2.log

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 567145: Create tables and Constrains based on ER diagram for questionnaire enhancements
------------------------------------------------------------------------------------------------------------------
        
ALTER TABLE t_rvw_qnaire_answer MODIFY (QNAIRE_RESP_VERS_ID NOT NULL);

ALTER TABLE t_rvw_qnaire_response MODIFY (A2L_WP_ID NOT NULL);
    
ALTER TABLE t_rvw_qnaire_response MODIFY (A2L_RESP_ID NOT NULL);

-------------------------------------------------------
-- T_RVW_QNAIRE_ANSWER
-- create Unique Constraint : QNAIRE_RESP_VERS_ID, Q_ID
-------------------------------------------------------
ALTER TABLE T_RVW_QNAIRE_ANSWER
    ADD CONSTRAINT T_RVW_QNAIRE_ANS_UK_VERS_Q 
    UNIQUE (QNAIRE_RESP_VERS_ID, Q_ID);
    
  
----------------------------------------------------
--T_RVW_QNAIRE_RESP_VERSIONS
--create Unique Constraint (QNAIRE_RESP_ID, REV_NUM)
----------------------------------------------------
ALTER TABLE T_RVW_QNAIRE_RESP_VERSIONS
    ADD CONSTRAINT T_RVW_QNAIRE_RESP_VERS_UK1
    UNIQUE (QNAIRE_RESP_ID, REV_NUM);
    
---------------------------------------------------------------------
--T_RVW_QNAIRE_RESP_VARIANTS
--create Unique Constraint (PIDC_VERS_ID, VARIANT_ID, QNAIRE_RESP_ID)
---------------------------------------------------------------------
ALTER TABLE T_RVW_QNAIRE_RESP_VARIANTS
    ADD CONSTRAINT T_RVW_QNAIRE_RESP_VAR_UK1
    UNIQUE (PIDC_VERS_ID, VARIANT_ID, QNAIRE_RESP_ID);

---------------------------------------------------------------
-- Mark the obsolete columns as nullable. They would be deleted
--    in the final drop script
---------------------------------------------------------------
ALTER TABLE T_RVW_QNAIRE_RESPONSE MODIFY (PIDC_VERS_ID NULL);
ALTER TABLE T_RVW_QNAIRE_RESPONSE MODIFY (QNAIRE_VERS_ID NULL);


ALTER TABLE T_RVW_QNAIRE_ANSWER MODIFY (QNAIRE_RESP_ID NULL);
-- NOTE : (if the above statement doesn't work)
-- A 'not-null' constraint exists for T_RVW_QNAIRE_ANSWER.QNAIRE_RESP_ID
-- This should be removed manually by identifing the constraint name
-- e.g. (from IT env)
--     ALTER TABLE T_RVW_QNAIRE_ANSWER DROP CONSTRAINT SYS_C00121431;



spool off