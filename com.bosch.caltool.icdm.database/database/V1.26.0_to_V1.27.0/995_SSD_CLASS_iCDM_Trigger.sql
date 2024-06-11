spool c:\temp\995_SSD_CLASS_iCDM_Trigger.log

CREATE OR REPLACE TRIGGER TRG_PARAM_INS 
BEFORE INSERT ON T_PARAMETER FOR EACH ROW

DECLARE
  cursor cur_GetSSDClass (p_Label VARCHAR2) is
    select ssd_class
      from k5esk_ldb2.T_ldb2_pavast
     where LABEL = p_Label 
  ;    

  v_SSDClass VARCHAR2(255);
BEGIN
  open cur_GetSSDClass (:NEW.name);
  fetch cur_GetSSDClass into v_SSDClass;
  
  if (cur_GetSSDClass%FOUND) then
    :NEW.SSD_CLASS := v_SSDClass;
  end if;
  
  close cur_GetSSDClass;
END;

spool off

