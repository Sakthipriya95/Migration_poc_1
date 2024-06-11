spool c:\temp\31_Table_Data_Qnaire.log

--------------------------------------------------------------------------------------------------------------------
--584263: Only one qnaire response should be linked to destination variant
--------------------------------------------------------------------------------------------------------------------

INSERT INTO T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT)
    VALUES ('RVW_QNAIRE_RESP', 'LINK_VAR_NOT_ALLOWED', 'The selected questionnaire response {0} is already linked to a variant. Only one variant linking for questionnaire response is allowed.');

COMMIT;

spool off
