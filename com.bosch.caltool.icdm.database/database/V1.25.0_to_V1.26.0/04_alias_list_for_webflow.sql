spool c:\temp\04_alias_list_for_webflow.log

declare 
 v_alias_list_id number;
begin 
  
  select seqv_attributes.nextval
    into :v_alias_list_id
    from dual;
 
  Insert into tabv_common_params 
    ( PARAM_ID
    , PARAM_DESC
    , PARAM_VALUE
    , VERSION ) 
  select 
      'WEBFLOW_ALIAS_ID'
    , 'Alias id for the web flow'
    , :v_alias_list_id
    , '1'
   from dual;
   
  insert into t_alias_definition
    ( ad_id
    , ad_name
    , version )
  select :v_alias_list_id
       , 'WebFlow'
       , 1
    from dual; 
    
  insert into t_alias_details 
    ( alias_details_id
    , ad_id
    , attr_id
    , value_id
    , alias_name
    , version)
  select seqv_attributes.nextval
       , :v_alias_list_id
       , attr_id
       , null
       , '(Dummy for Web Flow)' ||  attr_name_eng
       , 1
    from tabv_attributes 
   where attr_name_eng 
      in ( 'ECU Generation'
         , 'Customer/Brand'
         , 'ECU Type'
         , 'Customer Group'
         , 'Vehicle Model'
         , 'Rated Engine Power'
         , 'Displacement'
         , 'Model Year'
         , 'Engine Family'
         , 'Emission Standard Certification' 
         , 'Transmission' 
         , 'Region'
         , 'Cylinders (No.)'
         );    
         
insert into t_alias_details 
    ( alias_details_id
    , ad_id
    , attr_id
    , value_id
    , alias_name
    , version )
  select seqv_attributes.nextval
       , :v_alias_list_id
       , null
       , value_id
       , '(Dummy for Web Flow)' || textvalue_eng
       , 1
   from tabv_attr_values
  where attr_id in ( select attr_id
                      from tabv_attributes 
                     where attr_name_eng 
                        in ( 'ECU Generation'
                           , 'Customer/Brand'
                           , 'ECU Type'
                           , 'Customer Group'
                           , 'Vehicle Model'
                           , 'Rated Engine Power'
                           , 'Displacement'
                           , 'Model Year'
                           , 'Engine Family'
                           , 'Emission Standard Certification' 
                           , 'Transmission' 
                           , 'Region'
                           , 'Cylinders (No.)'
                           ) );            
end;
/

spool off