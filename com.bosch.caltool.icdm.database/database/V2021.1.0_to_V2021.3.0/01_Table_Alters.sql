spool c:\temp\01_Table_Alters.log

--567657 - Added column for quotation relevance flag
ALTER TABLE TABV_UCP_ATTRS ADD (MAPPING_FLAGS NUMBER(6));

--------------------------------------------------------------------------------------------------------------------
--569577: Dev: Changes in Question Dialog to support selection of specific result options during dependency selection
--------------------------------------------------------------------------------------------------------------------
--Added new column to store dependency result option id----------

ALTER TABLE T_QUESTION
	ADD DEP_Q_RESULT_OPT_ID NUMBER ;

ALTER TABLE T_QUESTION 
	ADD CONSTRAINT T_QUESTION_FK3 FOREIGN KEY (DEP_Q_RESULT_OPT_ID) REFERENCES T_QUESTION_RESULT_OPTIONS(Q_RESULT_OPT_ID);
	
--Modified check constraint for question dependency and response--

ALTER TABLE T_QUESTION 
	DROP CONSTRAINT T_QUES_DEP_CK1 ;
	
--Check constraint -if dep ques id is null , dep ques resp and q result opt id should also be null ,
-----------		else either of dep ques resp or q result opt id should be null

ALTER TABLE T_QUESTION 
	ADD CONSTRAINT T_QUES_DEP_CK1 
		CHECK((DEP_QUES_RESP IS NOT NULL AND DEP_Q_RESULT_OPT_ID IS NULL AND DEP_QUES_ID IS NOT NULL) 
			OR (DEP_QUES_RESP IS NULL AND DEP_Q_RESULT_OPT_ID IS NOT NULL AND DEP_QUES_ID IS NOT NULL)
			OR (DEP_QUES_RESP IS NULL AND DEP_Q_RESULT_OPT_ID IS NULL AND DEP_QUES_ID IS NULL));
			

spool off
