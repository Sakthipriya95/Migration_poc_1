spool c:\temp\31_Help_Links.log

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 594303: Update link for ImportA2lWPRespFromInputFileDialog
------------------------------------------------------------------------------------------------------------------
UPDATE T_LINKS SET NODE_TYPE = 'HELP_DIALOG_ImportA2lWpRespFromInputFileDialog' 
  WHERE NODE_TYPE = 'HELP_DIALOG_ImportA2lWpRespFromExcelDialog';

COMMIT;

spool off;