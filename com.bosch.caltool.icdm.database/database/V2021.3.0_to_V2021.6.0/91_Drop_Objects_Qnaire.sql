spool C:\Temp\90_Drop_Objects_Qnaire.log

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 567145: Create tables and Constrains based on ER diagram for questionnaire enhancements
------------------------------------------------------------------------------------------------------------------

----------------------------
--------t_rvw_qnaire_answer
----------------------------

-- DROP CONSTRAINTS  
        
ALTER TABLE t_rvw_qnaire_answer DROP CONSTRAINT T_RVW_QNAIRE_ANSWERS__FK_1;

ALTER TABLE t_rvw_qnaire_answer DROP CONSTRAINT T_RVW_QNAIRE_ANSWERS__FK_2; 

ALTER TABLE t_rvw_qnaire_answer DROP CONSTRAINT T_RVW_QNAIRE_ANSWERS__FK_3;  

-- executed in BTEST, but was not found in PRO
ALTER TABLE t_rvw_qnaire_answer DROP CONSTRAINT T_RVW_QNAIRE_ANSWERS_FK_5; 

-- DROP COLUMNS
ALTER TABLE t_rvw_qnaire_answer DROP COLUMN VARIANT_ID;
  
ALTER TABLE t_rvw_qnaire_answer DROP COLUMN PIDC_VERS_ID;
  
ALTER TABLE t_rvw_qnaire_answer DROP COLUMN QNAIRE_VERS_ID;

ALTER TABLE t_rvw_qnaire_answer DROP COLUMN QNAIRE_RESP_ID;

    
-------------------------------
--------t_rvw_qnaire_response
-------------------------------

-- DROP CONSTRAINTS

--drop fk constraint, index on PIDC_VERS_ID
ALTER TABLE T_RVW_QNAIRE_RESPONSE DROP CONSTRAINT T_RVW_QNAIRE_RESP_FK1;
drop index T_RVW_QNAIRE_RESP_IDX1;

--drop fk constraint, index on  VARIANT_ID
ALTER TABLE T_RVW_QNAIRE_RESPONSE DROP CONSTRAINT T_RVW_QNAIRE_RESP_FK2;
drop index T_RVW_QNAIRE_RESP_IDX2;
    
--drop fk constraint QNAIRE_VERS_ID
ALTER TABLE T_RVW_QNAIRE_RESPONSE DROP CONSTRAINT T_RVW_QNAIRE_RESP_FK3;
    

--DROP COLUMNS
ALTER TABLE T_RVW_QNAIRE_RESPONSE DROP COLUMN PIDC_VERS_ID;
  
ALTER TABLE T_RVW_QNAIRE_RESPONSE DROP COLUMN VARIANT_ID;  
  
ALTER TABLE T_RVW_QNAIRE_RESPONSE DROP COLUMN QNAIRE_VERS_ID;


--Drop columns created for migration
ALTER TABLE T_RVW_QNAIRE_RESPONSE DROP COLUMN MIG_STATE;
ALTER TABLE T_RVW_QNAIRE_RESP_VERSIONS DROP COLUMN MIG_STATE;
ALTER TABLE T_RVW_QNAIRE_RESPONSE DROP COLUMN MIG_STATE;

ALTER TABLE T_RVW_QNAIRE_RESPONSE DROP COLUMN COPIED_FROM_QNAIRE_RESP_ID;

drop table tmp_pidc_vers_qnaire;
drop table t_temp_rvwwpresp_wp_assignment; 
drop table t_temp_qnaireresp_wpresp;


---------------------------------------------------------------
--Remove the table 
--DGS_ICDM.T_RVW_QNAIRE_RESULTS
---------------------------------------------------------------
--Drop triggers
DROP TRIGGER TRG_T_RVW_QNAIRE_UPDT; 
DROP TRIGGER TRG_T_RVW_QNAIRE_INS; 

--Drop the table
DROP TABLE T_RVW_QNAIRE_RESULTS; 

spool off
