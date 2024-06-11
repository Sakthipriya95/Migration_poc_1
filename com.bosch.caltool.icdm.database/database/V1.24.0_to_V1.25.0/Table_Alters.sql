spool c:\temp\table_alters.log


-------------------------------------------------
--iCDM-2183
----------------------------------------------------------
--Alter t_rvw_parameters to add column parent_param_id to store the param of parent review
ALTER TABLE T_RVW_PARAMETERS ADD (PARENT_PARAM_ID NUMBER);

ALTER TABLE T_RVW_PARAMETERS ADD CONSTRAINT T_RVW_PARAMETERS_FK5
    FOREIGN KEY (PARENT_PARAM_ID) REFERENCES T_RVW_PARAMETERS(RVW_PARAM_ID);

-------------------------------------------------
--iCDM-2183
----------------------------------------------------------
--Alter t_rvw_results to add column to identify the type of delta review
ALTER TABLE T_RVW_RESULTS ADD (DELTA_REVIEW_TYPE VARCHAR2(1));

--Set the delta review type for existing reviews
UPDATE T_RVW_RESULTS SET DELTA_REVIEW_TYPE='D' where ORG_RESULT_ID is not null;


commit;


spool off
