declare
  cursor curA2LPver is
    select   SDOM_PVER_NAME
           , SDOM_PVER_VARIANT
           , SDOM_PVER_REVISION
      from TA2L_FILEINFO
     where SDOM_PVER_NAME is not null 
  ;    
  
  cursor curNumElements is
    select ELEMENT_CLASS
         , count(*)      as NUM_ELEMENTS
      from GTT_EASEE_ELEMENTS
     group by ELEMENT_CLASS 
     order by ELEMENT_CLASS 
  ;    
  
  i NUMBER;
  vNumElementsString VARCHAR2(1024);

begin
  i := 0;
  
  for vA2LPver in curA2LPver loop
    i := i + 1;
    exit when i > 10;

    GET_PVER_BC (vA2LPver.SDOM_PVER_NAME, vA2LPver.SDOM_PVER_VARIANT, vA2LPver.SDOM_PVER_REVISION);

    vNumElementsString := '';
   
    for vNumElements in curNumElements loop
      vNumElementsString := vNumElementsString || ' - ' || vNumElements.ELEMENT_CLASS || ' : ' || vNumElements.NUM_ELEMENTS ;
    end loop;
   
    dbms_output.put_line(to_char(sysdate, 'hh24:mi:ss') || ' :: ' || vA2LPver.SDOM_PVER_NAME || ' / ' || vA2LPver.SDOM_PVER_VARIANT || ' ; ' || vA2LPver.SDOM_PVER_REVISION || vNumElementsString);
    
    -- clear the GTT table
    rollback;
  end loop;

end;
/