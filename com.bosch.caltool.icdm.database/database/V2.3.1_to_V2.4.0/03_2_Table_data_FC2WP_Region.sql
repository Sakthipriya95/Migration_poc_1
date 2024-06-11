spool c:\temp\03_2_Table_data_FC2WP_Region.log

---------------------------------------------------------------------
-- ALM ID : 333284
--  Insert query for T_REGION
---------------------------------------------------------------------  
insert into t_region(REGION_ID,REGION_CODE,REGION_NAME_ENG,REGION_NAME_GER)
select seqv_attributes.nextval
     , 'FR'
     , 'France'
     , 'Frankreich'
  from dual;     
  
insert into t_region(REGION_ID,REGION_CODE,REGION_NAME_ENG,REGION_NAME_GER)
select seqv_attributes.nextval
     , 'NA'
     , 'North America'
     , 'Nordamerika'
  from dual;       
  
insert into t_region(REGION_ID,REGION_CODE,REGION_NAME_ENG,REGION_NAME_GER)
select seqv_attributes.nextval
     , 'CN'
     , 'China'
     , 'China'
  from dual; 
  
insert into t_region(REGION_ID,REGION_CODE,REGION_NAME_ENG,REGION_NAME_GER)
select seqv_attributes.nextval
     , 'GB'
     , 'Great Britain'
     , 'Groﬂbritannien'
  from dual;   
  
insert into t_region(REGION_ID,REGION_CODE,REGION_NAME_ENG,REGION_NAME_GER)
select seqv_attributes.nextval
     , 'JP'
     , 'Japan'
     , 'Japan'
  from dual;   

commit;

spool off