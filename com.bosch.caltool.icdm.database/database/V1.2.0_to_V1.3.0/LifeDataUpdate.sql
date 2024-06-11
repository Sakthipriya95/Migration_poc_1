--
-- change attribute TYPE to HYPERLINK
--
declare

  cursor curAttrs is 
    select *
	  from TabV_Attributes
	 where ATTR_DESC_ENG like 'LINK%'
  ;	 

begin 
  for vAttr in curAttrs loop
    dbms_output.put_line('Attr.: ' || vAttr.ATTR_NAME_ENG);
    
    update TabV_Attributes
      set VALUE_TYPE_ID = 5
     where ATTR_ID = vAttr.ATTR_ID
    ;
    
  end loop;
end;
/
