declare
  cursor cur_DateValues is 
    select value_id, attr_id, datevalue
      from tabv_attr_values
     where datevalue is not null 
     order by attr_id, datevalue
  ; 
	
  cursor cur_DateValueExists(pAttrID IN NUMBER, pDate IN VARCHAR2) is 
    select value_id, attr_id
      from tabv_attr_values
     where datevalue = to_timestamp(pDate, 'YYYYMMDD')
       and attr_id = pAttrID
	; 
	
  vDateValueExists cur_DateValueExists%ROWTYPE;
begin

	for vDateValue in cur_DateValues loop
	
    -- find data values which are not the fist day of the month
    if (vDateValue.datevalue <> to_timestamp(to_char(vDateValue.datevalue, 'YYYYMM') || '01', 'YYYYMMDD')) then

      dbms_output.put_line(vDateValue.value_id 
        || ' ' || vDateValue.attr_id
        || ' ' || vDateValue.datevalue
      );
  
      -- check, if a data value for the first of the month exists for this attribute
      open cur_DateValueExists(vDateValue.attr_id, to_char(vDateValue.datevalue, 'YYYYMM') || '01');
      fetch cur_DateValueExists into vDateValueExists;
      
      if (cur_DateValueExists%FOUND) then
        dbms_output.put_line('date for first of the month exists');
        
        -- replace all usages of this value with the first of the month
        update TabV_Project_ATTR set value_id = vDateValueExists.value_id
          where value_id = vDateValue.value_id
        ;
        update TabV_Variants_ATTR set value_id = vDateValueExists.value_id
          where value_id = vDateValue.value_id
        ;
        update TabV_Attr_Dependencies set value_id = vDateValueExists.value_id
          where value_id = vDateValue.value_id
        ;
        update TabV_Attr_Dependencies set depen_value_id = vDateValueExists.value_id
          where depen_value_id = vDateValue.value_id
        ;
        
        -- delete this value
        delete from TabV_Attr_Values where value_id = vDateValue.value_id;
      else
        dbms_output.put_line('date for first of the month NOT existing');
        
        -- update the date value
        update TabV_Attr_Values 
          set datevalue = to_timestamp(to_char(datevalue, 'YYYYMM') || '01', 'YYYYMMDD')
          where value_id = vDateValue.value_id
        ;
        
      end if;
      
      close cur_DateValueExists;

    end if;
	
	end loop;

  update TabV_Attr_Values
    set Value_Desc_ENG = '-'
      , Value_Desc_GER = NULL
  where datevalue is not null
    and (Value_Desc_ENG <> '-' OR Value_Desc_GER is not NULL)
  ;
  
end;
/