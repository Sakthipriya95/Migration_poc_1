spool c:\temp\04_Pidc_Questionnaire_Link_Changes.log

---------------------------------------------------------------------------------------
-- Questionnaire response restructuring. 
-- Introduce table T_RVW_QNAIRE_RESPONSE and related data migration
-- ICDM-2404
---------------------------------------------------------------------------------------
CREATE TABLE T_RVW_QNAIRE_RESPONSE (    
    QNAIRE_RESP_ID NUMBER NOT NULL, 
    PIDC_VERS_ID NUMBER NOT NULL, 
    VARIANT_ID NUMBER, 
    QNAIRE_VERS_ID NUMBER NOT NULL, 
    CREATED_DATE TIMESTAMP (6) NOT NULL, 
    CREATED_USER VARCHAR2(30) NOT NULL, 
    MODIFIED_DATE TIMESTAMP (6), 
    MODIFIED_USER VARCHAR2(30), 
    VERSION NUMBER NOT NULL,
    
    CONSTRAINT T_RVW_QNAIRE_RESP_PK PRIMARY KEY (QNAIRE_RESP_ID), 
    
    CONSTRAINT T_RVW_QNAIRE_RESP_FK1 FOREIGN KEY (PIDC_VERS_ID)
        REFERENCES T_PIDC_VERSION (PIDC_VERS_ID), 
    CONSTRAINT T_RVW_QNAIRE_RESP_FK2 FOREIGN KEY (VARIANT_ID)
        REFERENCES TABV_PROJECT_VARIANTS (VARIANT_ID), 
    CONSTRAINT T_RVW_QNAIRE_RESP_FK3 FOREIGN KEY (QNAIRE_VERS_ID)
        REFERENCES T_QUESTIONNAIRE_VERSION (QNAIRE_VERS_ID) 
);

----------------------------------------------------------------
--Only one response should exist against a questionnaire version 
-- for the given pidc variant or version(if variant not available)
----------------------------------------------------------------
ALTER TABLE T_RVW_QNAIRE_RESPONSE ADD 
    CONSTRAINT T_RVW_QNAIRE_RESPONSE_UK1 UNIQUE (PIDC_VERS_ID, VARIANT_ID, QNAIRE_VERS_ID);

CREATE INDEX T_RVW_QNAIRE_RESP_IDX1 ON T_RVW_QNAIRE_RESPONSE (PIDC_VERS_ID);

CREATE INDEX T_RVW_QNAIRE_RESP_IDX2 ON T_RVW_QNAIRE_RESPONSE (VARIANT_ID); 

----------------------------------------------------------------
--Add relationship between T_RVW_QNAIRE_RESPONSE and T_RVW_QUESTIONNAIRE
----------------------------------------------------------------
ALTER TABLE T_RVW_QUESTIONNAIRE ADD (QNAIRE_RESP_ID NUMBER);

ALTER TABLE T_RVW_QUESTIONNAIRE
ADD CONSTRAINT T_RVW_QUESTIONNAIRES_FK3 FOREIGN KEY(  QNAIRE_RESP_ID )
    REFERENCES T_RVW_QNAIRE_RESPONSE(  QNAIRE_RESP_ID );

--Drop existing unique constraint for  RESULT_ID, QNAIRE_VERS_ID
ALTER TABLE T_RVW_QUESTIONNAIRE
    DROP CONSTRAINT T_RVW_QUESTIONNAIRES_UK_1;
    
--Drop existing Foreign key constraint for QNAIRE_VERS_ID
ALTER TABLE T_RVW_QUESTIONNAIRE
    DROP CONSTRAINT T_RVW_QUESTIONNAIRES_FK2;

    
----------------------------------------------------------------
--Add relationship between T_RVW_QNAIRE_RESPONSE and T_RVW_QNAIRE_ANSWER.
--Now T_RVW_QNAIRE_ANSWER is child table of T_RVW_QNAIRE_RESPONSE
----------------------------------------------------------------
ALTER TABLE T_RVW_QNAIRE_ANSWER add QNAIRE_RESP_ID NUMBER;

ALTER TABLE T_RVW_QNAIRE_ANSWER ADD 
  CONSTRAINT T_RVW_QNAIRE_ANSWERS_FK_5 FOREIGN KEY (QNAIRE_RESP_ID) 
    REFERENCES T_RVW_QNAIRE_RESPONSE (QNAIRE_RESP_ID);
    
ALTER TABLE T_RVW_QNAIRE_ANSWER ADD 
    CONSTRAINT T_RVW_QNAIRE_ANSWER_UK1 UNIQUE (QNAIRE_RESP_ID, Q_ID);

----------------------------------------------------------------
-- DDL for Trigger T_RVW_QNAIRE_RESPONSE - Before Insert
----------------------------------------------------------------
CREATE OR REPLACE TRIGGER TRGRVW_QNAIRE_RESP_INS 
  BEFORE INSERT ON T_RVW_QNAIRE_RESPONSE
  FOR EACH ROW
Begin
    IF :new.QNAIRE_RESP_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.QNAIRE_RESP_ID FROM DUAL;
    END IF;

    IF :new.VERSION is null THEN
        :new.VERSION := 1;
    END IF;

    IF :new.CREATED_DATE is null THEN
        :new.CREATED_DATE := sys_extract_utc(systimestamp);
    END IF;

    IF :new.CREATED_USER is null THEN
        :new.CREATED_USER := user;
    END IF;

END;
/

ALTER TRIGGER TRGRVW_QNAIRE_RESP_INS ENABLE;

----------------------------------------------------------------
-- DDL for Trigger T_RVW_QNAIRE_RESPONSE - Before update
----------------------------------------------------------------
CREATE OR REPLACE TRIGGER TRGRVW_QNAIRE_RESP_UPDT
BEFORE UPDATE ON T_RVW_QNAIRE_RESPONSE
FOR EACH ROW
BEGIN
    IF :new.VERSION = :old.VERSION THEN
        :new.VERSION := :old.VERSION + 1;
    END IF;

    if :new.MODIFIED_DATE IS NULL or NOT UPDATING('Modified_Date') then
        :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
    end if;

    if :new.MODIFIED_USER IS NULL then
        :new.MODIFIED_USER := user;
    end if;
 
END;
/

ALTER TRIGGER TRGRVW_QNAIRE_RESP_UPDT ENABLE;


----------------------------------------------------------------
--Migrate Questionnaires linked to variants
----------------------------------------------------------------
insert into T_RVW_QNAIRE_RESPONSE 
    (pidc_vers_id, VARIANT_ID, QNAIRE_VERS_ID, CREATED_DATE, CREATED_USER)
select pidc_vers_id, VARIANT_ID, QNAIRE_VERS_ID, CREATED_DATE, CREATED_USER 
from 
    (
        select pidc_vers_id, variant_id, qnaire_vers_id, rvwqnare.result_id,rvwqnare.created_date, rvwqnare.created_user 
                , row_number() over (partition by variant_id, qnaire_vers_id order by rvwqnare.created_date) rn 
        from T_RVW_QUESTIONNAIRE rvwqnare,
            t_pidc_a2l pidca2l, 
            t_rvw_results results,
            (
                select * from (
                    select result_id, variant_id, 
                            row_number() over (partition by result_id order by created_date) rn  
                    from t_rvw_variants 
                ) 
                where rn=1
             ) varrvwresults
        where rvwqnare.result_id = varrvwresults.result_id
            and pidca2l.pidc_a2l_id = results.pidc_a2l_id
            and results.result_id = rvwqnare.result_id
    )
where rn=1;

----------------------------------------------------------------
--Migrate Questionnaires linked to pidc versions (no variant for versions)
----------------------------------------------------------------
insert into T_RVW_QNAIRE_RESPONSE 
        (PIDC_VERS_ID, QNAIRE_VERS_ID, CREATED_DATE, CREATED_USER)
select PIDC_VERS_ID, QNAIRE_VERS_ID, CREATED_DATE, CREATED_USER 
from (
        select pidc_vers_id, qnaire_vers_id, rvwqnare.result_id,rvwqnare.created_date, rvwqnare.created_user 
            , row_number() over (partition by pidc_vers_id, qnaire_vers_id order by rvwqnare.created_date) rn 
        from t_pidc_a2l pa2l, t_rvw_results results, T_RVW_QUESTIONNAIRE rvwqnare 
        where results.pidc_a2l_id = pa2l.pidc_a2l_id
            and rvwqnare.result_id = results.result_id 
            and results.result_id not in (select result_id from t_rvw_variants)
    ) 
where rn=1;

commit;

----------------------------------------------------------------
--Set the new new primary key as foreign keys to T_RVW_QNAIRE_ANSWER
--a) for pidc with variants
----------------------------------------------------------------
update T_RVW_QNAIRE_ANSWER answ 
set answ.QNAIRE_RESP_ID = 
    (
        select prjqnre.QNAIRE_RESP_ID 
        from T_RVW_QNAIRE_RESPONSE prjqnre 
        where answ.variant_id = prjqnre.variant_id 
            and answ.qnaire_vers_id = prjqnre.qnaire_vers_id
    )
where answ.variant_id is not null;

----------------------------------------------------------------
--b) for pidc without variants
----------------------------------------------------------------
update T_RVW_QNAIRE_ANSWER answ 
set answ.QNAIRE_RESP_ID = 
    (
        select prjqnre.QNAIRE_RESP_ID 
        from T_RVW_QNAIRE_RESPONSE prjqnre 
        where answ.pidc_vers_id = prjqnre.pidc_vers_id 
            and answ.qnaire_vers_id = prjqnre.qnaire_vers_id 
            and  prjqnre.variant_id is null)
where answ.variant_id is null;


commit;

--If there are records with QNAIRE_RESP_ID as null in T_RVW_QNAIRE_ANSWER, the review results for them are probably deleted now.
--Hence those records can be deleted

----------------------------------------------------------------
--Set the new new primary key as foreign keys to T_RVW_QUESTIONNAIRE
--a) for pidc with variants
----------------------------------------------------------------
update T_RVW_QUESTIONNAIRE qresp_res 
set qresp_res.QNAIRE_RESP_ID = 
	(
	   select QNAIRE_RESP_ID 
	   from (
	       select qresp.qnaire_resp_id,qresp.qnaire_vers_id, res.result_id, qresp.variant_id, qresp.pidc_vers_id 
	       from T_RVW_QNAIRE_RESPONSE qresp, t_rvw_variants rvw_var, t_rvw_results res
	       where rvw_var.variant_id = qresp.variant_id and res.result_id = rvw_var.result_id
	   ) qresp_vars
	   where qresp_vars.result_id = qresp_res.result_id and qresp_res.qnaire_vers_id = qresp_vars.qnaire_vers_id
	) 
where qresp_res.result_id in 
    (select distinct result_id from t_rvw_variants);

----------------------------------------------------------------
--b) for pidc without variants
----------------------------------------------------------------
update T_RVW_QUESTIONNAIRE qresp_res 
set qresp_res.QNAIRE_RESP_ID = 
	(
		select QNAIRE_RESP_ID from 
		(
			select qresp.qnaire_resp_id, qresp.qnaire_vers_id, res.result_id, qresp.pidc_vers_id 
			from T_RVW_QNAIRE_RESPONSE qresp, t_pidc_a2l pidca2l, t_rvw_results res
			where qresp.pidc_vers_id = pidca2l.pidc_vers_id and res.pidc_a2l_id = pidca2l.pidc_a2l_id
                and qresp.variant_id is null
		) qresp_pidcs
		where qresp_pidcs.result_id = qresp_res.result_id and qresp_res.qnaire_vers_id = qresp_pidcs.qnaire_vers_id
	) 
where qresp_res.result_id not in 
    (select distinct result_id from t_rvw_variants);

commit;

----------------------------------------------------------------
--Mark the new column QNAIRE_RESP_ID as mandatory
----------------------------------------------------------------
ALTER TABLE T_RVW_QUESTIONNAIRE modify QNAIRE_RESP_ID NUMBER not null;

ALTER TABLE T_RVW_QNAIRE_ANSWER modify QNAIRE_RESP_ID NUMBER not null;

--Unique constraint in T_RVW_QUESTIONNAIRE to ensure that 
--  one response is attached to a result only once
ALTER TABLE T_RVW_QUESTIONNAIRE 
    ADD CONSTRAINT T_RVW_QUESTIONNAIRE_UK1 UNIQUE (RESULT_ID, QNAIRE_RESP_ID);
    
----------------------------------------------------------------
--Only one response should exist in the questionnaire response for a question
----------------------------------------------------------------
ALTER TABLE T_RVW_QNAIRE_ANSWER ADD CONSTRAINT 
    T_RVW_QNAIRE_ANSWER_UK1 UNIQUE (QNAIRE_RESP_ID , Q_ID);


spool off