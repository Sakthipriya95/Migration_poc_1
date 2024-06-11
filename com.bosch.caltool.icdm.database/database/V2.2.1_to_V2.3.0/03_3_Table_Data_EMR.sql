--------------------------------------------------------
--  DDL for Package PK_EMISSION_ROBUSTNESS
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE PK_EMISSION_ROBUSTNESS AS 

  procedure add_text_value(a_text varchar2, for_a_column_name varchar2);
  
  procedure add_val_to_mapping_table(a_value_in_excel varchar2,a_col_val_id number, a_col_id number);
  
  procedure add_column(a_column_name varchar2, a_numeric_flag varchar2, a_normalized_flag varchar2);
  
  procedure add_emission_standard(a_emission_standard_name varchar2, a_emission_standard_flag varchar2 default 'N', a_testcase_flag varchar2 default 'N', a_measure_flag varchar2 default 'N');
  
  procedure set_emission_standard_parent(a_emis_child varchar2, a_emis_parent varchar2);
  
  procedure add_ems_to_mapping_table(a_value_in_excel varchar2,a_ems_id number);
  
  procedure add_col_to_mapping_table(a_value_in_excel varchar2,a_col_id number);
  
  procedure add_measure_unit(a_measure_unit varchar2);
   
  procedure add_mu_to_mapping_table(a_value_in_excel varchar2,a_mu_id number);

END PK_EMISSION_ROBUSTNESS;

/


--------------------------------------------------------
--  DDL for Package Body PK_EMISSION_ROBUSTNESS
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE BODY PK_EMISSION_ROBUSTNESS AS

  procedure check_column_name_existing(a_column_name varchar2);
  procedure check_normalized(a_column_name varchar2); 
  function get_emis_id_or_error(a_emission_standard_name varchar2) return varchar2;

  procedure add_emission_standard(a_emission_standard_name varchar2, a_emission_standard_flag varchar2 default 'N', a_testcase_flag varchar2 default 'N', a_measure_flag varchar2 default 'N') 
  AS
    l_ems_id t_emr_emission_standard.ems_id%type := seqv_attributes.nextval;
     
  BEGIN
    insert into t_emr_emission_standard(ems_id,emission_standard_name,emission_standard_flag,testcase_flag,measures_flag)
    values (l_ems_id,a_emission_standard_name,a_emission_standard_flag,a_testcase_flag,a_measure_flag);
    
    add_ems_to_mapping_table(a_emission_standard_name,l_ems_id);
  exception
    when DUP_VAL_ON_INDEX then
      rollback;
      raise_application_error(-20995,'The emission standard "' || a_emission_standard_name || '" already exists. A Rollback has been performed.');
  END;
  
  procedure set_emission_standard_parent(a_emis_child varchar2, a_emis_parent varchar2) 
  AS
    l_id_of_child t_emr_emission_standard.ems_id%type := get_emis_id_or_error(a_emis_child);
    l_id_of_parent t_emr_emission_standard.ems_id%type := get_emis_id_or_error(a_emis_parent);
  BEGIN
    update t_emr_emission_standard
       set parent_ems_id = l_id_of_parent
     where ems_id = l_id_of_child;
  END;
  
  function get_emis_id_or_error(a_emission_standard_name varchar2) return varchar2
  is
    l_emission_standard_id t_emr_emission_standard.ems_id%type;
  begin
    select ems_id
      into l_emission_standard_id
      from t_emr_emission_standard
     where emission_standard_name = a_emission_standard_name;
     
     return l_emission_standard_id;
  exception
    when no_data_found then
      raise_application_error(-20990,'Emission Standard with the name ' || a_emission_standard_name || ' is not existing.');
  end;
  
  procedure add_ems_to_mapping_table(a_value_in_excel varchar2,a_ems_id number)
  as
  begin
    insert into t_emr_excel_mapping(ems_id, value_in_excel)
    values(a_ems_id,a_value_in_excel);
  end;

  procedure add_measure_unit(a_measure_unit varchar2) 
  AS
    l_mu_id t_emr_measure_unit.mu_id%type := seqv_attributes.nextval;
     
  BEGIN
    insert into t_emr_measure_unit(mu_id,measure_unit_name)
    values (l_mu_id,a_measure_unit);
    
    add_mu_to_mapping_table(a_measure_unit,l_mu_id);
  exception
    when DUP_VAL_ON_INDEX then
      rollback;
      raise_application_error(-20995,'The measure unit "' || a_measure_unit || '" already exists. A Rollback has been performed.');
  END;
  
  procedure add_mu_to_mapping_table(a_value_in_excel varchar2,a_mu_id number)
  as
  begin
    insert into t_emr_excel_mapping(mu_id, value_in_excel)
    values(a_mu_id,a_value_in_excel);
  end;

  procedure add_column(a_column_name varchar2, a_numeric_flag varchar2, a_normalized_flag varchar2) 
  AS
    l_col_id t_emr_column.col_id%type := seqv_attributes.nextval;
     
  BEGIN
    insert into t_emr_column(col_id,column_name, numeric_flag, nomalized_flag)
    values (l_col_id,a_column_name,a_numeric_flag,a_normalized_flag);
    
    add_col_to_mapping_table(a_column_name,l_col_id);
    
  END;

  procedure add_text_value(a_text varchar2, for_a_column_name varchar2) 
  AS
    l_col_id t_emr_column.col_id%type;
    l_col_value_id t_emr_column_value.col_value_id%type := seqv_attributes.nextval;
  BEGIN
    check_column_name_existing(for_a_column_name);
    check_normalized(for_a_column_name);
  
    select col_id
      into l_col_id
      from t_emr_column
     where column_name = for_a_column_name
       and nomalized_flag = 'Y';
       
    insert into t_emr_column_value(col_value_id,col_id, col_value)
    values (l_col_value_id,l_col_id,a_text);
    
    add_val_to_mapping_table(a_text,l_col_value_id,l_col_id);
    
  END;
  
  procedure add_val_to_mapping_table(a_value_in_excel varchar2,a_col_val_id number, a_col_id number)
  as
  begin
    insert into t_emr_excel_mapping(col_id, col_value_id, value_in_excel)
    values(a_col_id,a_col_val_id,a_value_in_excel);
  end;
  
  procedure add_col_to_mapping_table(a_value_in_excel varchar2,a_col_id number)
  as
  begin
    insert into t_emr_excel_mapping(col_id, value_in_excel)
    values(a_col_id,a_value_in_excel);
  end;
  
  procedure check_column_name_existing(a_column_name varchar2) 
  as
    lo_exists number;
  begin
    select count(*)
      into lo_exists
      from t_emr_column
     where column_name = a_column_name;
     
    if lo_exists = 0 then
        raise_application_error(-20990,'Column ' || a_column_name || ' is not existing.');
    end if;
  end;
  
  procedure check_normalized(a_column_name varchar2) 
  as
    lo_exists number;
  begin
    select count(*)
      into lo_exists
      from t_emr_column
     where column_name = a_column_name
       and nomalized_flag = 'Y';
     
    if lo_exists = 0 then
        raise_application_error(-20990,'Column ' || a_column_name || ' is not normalized.');
    end if;
  end;
  
  procedure check_pk_col_name_existing(a_pk_column_name varchar2) 
  as
    lo_exists number;
  begin
    if (a_pk_column_name not in ('EMS_ID','MU_ID','COL_ID','COL_VALUE_ID')) then
      raise_application_error(-20990,'Parameter a_pk_column_name must be one of these values: EMS_ID,MU_ID,COL_ID,COL_VALUE_ID');
    end if;
  end;
  
END PK_EMISSION_ROBUSTNESS;
/


begin
  PK_EMISSION_ROBUSTNESS.add_text_value('listed','Measures');
end;
/

begin
    PK_EMISSION_ROBUSTNESS.add_column('RoMP Check','N','Y');
    PK_EMISSION_ROBUSTNESS.add_column('RoMP Remark','N','N');
    PK_EMISSION_ROBUSTNESS.add_column('RoR Target [IEF]','Y','N');
    PK_EMISSION_ROBUSTNESS.add_column('RoR Result[IEF]','Y','N');
    PK_EMISSION_ROBUSTNESS.add_column('RoR Result Fuel2 [IEF]','Y','N');
    PK_EMISSION_ROBUSTNESS.add_column('RoR Status','N','Y');
    PK_EMISSION_ROBUSTNESS.add_column('RoR Remark','N','N');
    PK_EMISSION_ROBUSTNESS.add_column('RoR Flex Fuel Project','N','Y');
    PK_EMISSION_ROBUSTNESS.add_column('RoR Approval for project start Text','N','Y');
    PK_EMISSION_ROBUSTNESS.add_column('RoR Approval for project start','N','Y');
    PK_EMISSION_ROBUSTNESS.add_column('RoR Customer','N','N');
    PK_EMISSION_ROBUSTNESS.add_column('RoR Chassis denomination','N','N');
    PK_EMISSION_ROBUSTNESS.add_column('RoR Engine','N','N');
    PK_EMISSION_ROBUSTNESS.add_column('RoR Market','N','N');
    PK_EMISSION_ROBUSTNESS.add_column('RoR Lead Project','N','N');
    PK_EMISSION_ROBUSTNESS.add_column('RoR Software','N','N');
    PK_EMISSION_ROBUSTNESS.add_column('RoR Dataset','N','N');
    PK_EMISSION_ROBUSTNESS.add_column('Emission legislation limit 20°C','Y','N');
    PK_EMISSION_ROBUSTNESS.add_column('Emission legislation limit -7°C','Y','N');
    PK_EMISSION_ROBUSTNESS.add_column('User defined name of N1','N','N');
    PK_EMISSION_ROBUSTNESS.add_column('User defined name of N2','N','N');
    PK_EMISSION_ROBUSTNESS.add_column('CET','N','N');
    PK_EMISSION_ROBUSTNESS.add_column('N1','Y','N');
    PK_EMISSION_ROBUSTNESS.add_column('N2','Y','N');
    PK_EMISSION_ROBUSTNESS.add_column('Average','Y','N');
    PK_EMISSION_ROBUSTNESS.add_column('Result','Y','N');
    PK_EMISSION_ROBUSTNESS.add_column('Status','N','Y');
    PK_EMISSION_ROBUSTNESS.add_column('Result [IEF]','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_column('Target [IEF]','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_column('Total Status','N','Y');
    PK_EMISSION_ROBUSTNESS.add_column('Measures','N','Y');
    PK_EMISSION_ROBUSTNESS.add_column('Emission legislation limit 10°C','Y','N');
end;
/

begin
    PK_EMISSION_ROBUSTNESS.add_text_value('Yes','RoMP Check');
    PK_EMISSION_ROBUSTNESS.add_text_value('No','RoMP Check');
    PK_EMISSION_ROBUSTNESS.add_text_value('n.a.','RoR Status');
    PK_EMISSION_ROBUSTNESS.add_text_value('C','RoR Status');
    PK_EMISSION_ROBUSTNESS.add_text_value('B','RoR Status');
    PK_EMISSION_ROBUSTNESS.add_text_value('A','RoR Status');
    PK_EMISSION_ROBUSTNESS.add_text_value('2nd','RoR Status');
    PK_EMISSION_ROBUSTNESS.add_text_value('#','RoR Status');
    PK_EMISSION_ROBUSTNESS.add_text_value('Yes, 2nd Fuel','RoR Flex Fuel Project');
    PK_EMISSION_ROBUSTNESS.add_text_value('No','RoR Flex Fuel Project');
    PK_EMISSION_ROBUSTNESS.add_text_value('[C - IEF target not achieved, reason not fully explained]','RoR Approval for project start Text');
    PK_EMISSION_ROBUSTNESS.add_text_value('[B - IEF target not achieved, due to technical rasons]','RoR Approval for project start Text');
    PK_EMISSION_ROBUSTNESS.add_text_value('[A - IEF target achieved]','RoR Approval for project start Text');
    PK_EMISSION_ROBUSTNESS.add_text_value('[2nd - According to Codex: 2nd measurement is needed]','RoR Approval for project start Text');
    PK_EMISSION_ROBUSTNESS.add_text_value('Yes','RoR Approval for project start');
    PK_EMISSION_ROBUSTNESS.add_text_value('No','RoR Approval for project start');
    PK_EMISSION_ROBUSTNESS.add_text_value('refused','Status');
    PK_EMISSION_ROBUSTNESS.add_text_value('passed','Status');
    PK_EMISSION_ROBUSTNESS.add_text_value('refused','Total Status');
    PK_EMISSION_ROBUSTNESS.add_text_value('passed','Total Status');
    PK_EMISSION_ROBUSTNESS.add_text_value('listed','Measures');
end;
/

begin
    PK_EMISSION_ROBUSTNESS.add_measure_unit('NMHC [g/km]');
    PK_EMISSION_ROBUSTNESS.add_measure_unit('THC [g/km]');
    PK_EMISSION_ROBUSTNESS.add_measure_unit('CO [g/mi]');
    PK_EMISSION_ROBUSTNESS.add_measure_unit('NOx [g/mi]');
    PK_EMISSION_ROBUSTNESS.add_measure_unit('PM [mg/mi]');
    PK_EMISSION_ROBUSTNESS.add_measure_unit('PN [#/mi]');
end;
/

begin
    PK_EMISSION_ROBUSTNESS.add_text_value('No','RoMP Check');
    PK_EMISSION_ROBUSTNESS.add_text_value('Yes','RoMP Check');
    PK_EMISSION_ROBUSTNESS.add_text_value('A','RoR Status');
    PK_EMISSION_ROBUSTNESS.add_text_value('B','RoR Status');
    PK_EMISSION_ROBUSTNESS.add_text_value('C','RoR Status');
    PK_EMISSION_ROBUSTNESS.add_text_value('#','RoR Status');
    PK_EMISSION_ROBUSTNESS.add_text_value('2nd','RoR Status');
    PK_EMISSION_ROBUSTNESS.add_text_value('n.a.','RoR Status');
    PK_EMISSION_ROBUSTNESS.add_text_value('Yes, 2nd Fuel','RoR Flex Fuel Project');
    PK_EMISSION_ROBUSTNESS.add_text_value('No','RoR Flex Fuel Project');
    PK_EMISSION_ROBUSTNESS.add_text_value('[A - IEF target achieved]','RoR Approval for project start Text');
    PK_EMISSION_ROBUSTNESS.add_text_value('[B - IEF target not achieved, due to technical rasons]','RoR Approval for project start Text');
    PK_EMISSION_ROBUSTNESS.add_text_value('[C - IEF target not achieved, reason not fully explained]','RoR Approval for project start Text');
    PK_EMISSION_ROBUSTNESS.add_text_value('[2nd - According to Codex: 2nd measurement is needed]','RoR Approval for project start Text');
    PK_EMISSION_ROBUSTNESS.add_text_value('Yes','RoR Approval for project start');
    PK_EMISSION_ROBUSTNESS.add_text_value('No','RoR Approval for project start');
    PK_EMISSION_ROBUSTNESS.add_text_value('passed','Status');
    PK_EMISSION_ROBUSTNESS.add_text_value('refused','Status');
    PK_EMISSION_ROBUSTNESS.add_text_value('passed','Total Status');
    PK_EMISSION_ROBUSTNESS.add_text_value('refused','Total Status');
    PK_EMISSION_ROBUSTNESS.add_text_value('listed','Measures');
end;
/

begin
    PK_EMISSION_ROBUSTNESS.add_emission_standard(a_emission_standard_name => 'Euro 1',a_emission_standard_flag => 'Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard(a_emission_standard_name => 'Euro 2',a_emission_standard_flag => 'Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard(a_emission_standard_name => 'Euro 3',a_emission_standard_flag => 'Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard(a_emission_standard_name => 'Euro 4',a_emission_standard_flag => 'Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard(a_emission_standard_name => 'Euro 5a',a_emission_standard_flag => 'Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard(a_emission_standard_name => 'Euro 5b',a_emission_standard_flag => 'Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard(a_emission_standard_name => 'Euro 6b',a_emission_standard_flag => 'Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard(a_emission_standard_name => 'Euro 6c',a_emission_standard_flag => 'Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard(a_emission_standard_name => 'Euro 6d- TEMP',a_emission_standard_flag => 'Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard(a_emission_standard_name => 'Euro 6d',a_emission_standard_flag => 'Y');    
    
    PK_EMISSION_ROBUSTNESS.add_emission_standard(a_emission_standard_name => 'US EPA Tier 1',a_emission_standard_flag => 'Y');  
    PK_EMISSION_ROBUSTNESS.add_emission_standard(a_emission_standard_name => 'US EPA Tier 2 Bin 5',a_emission_standard_flag => 'Y');  
    PK_EMISSION_ROBUSTNESS.add_emission_standard(a_emission_standard_name => 'US EPA Tier 2 Bin 6',a_emission_standard_flag => 'Y');  
    PK_EMISSION_ROBUSTNESS.add_emission_standard(a_emission_standard_name => 'US EPA Tier 2 Bin 7',a_emission_standard_flag => 'Y');  
    PK_EMISSION_ROBUSTNESS.add_emission_standard(a_emission_standard_name => 'US EPA Tier 2 Bin 8',a_emission_standard_flag => 'Y');  
    PK_EMISSION_ROBUSTNESS.add_emission_standard(a_emission_standard_name => 'US EPA Tier 2 Bin 9',a_emission_standard_flag => 'Y');  
    PK_EMISSION_ROBUSTNESS.add_emission_standard(a_emission_standard_name => 'US EPA Tier 2 Bin 10',a_emission_standard_flag => 'Y');  
    PK_EMISSION_ROBUSTNESS.add_emission_standard(a_emission_standard_name => 'US EPA Tier 2 Bin 11',a_emission_standard_flag => 'Y');
    
    
    PK_EMISSION_ROBUSTNESS.add_emission_standard(a_emission_standard_name => 'PROCONVE L4',a_emission_standard_flag => 'Y');  
    PK_EMISSION_ROBUSTNESS.add_emission_standard(a_emission_standard_name => 'PROCONVE L5',a_emission_standard_flag => 'Y');  
    PK_EMISSION_ROBUSTNESS.add_emission_standard(a_emission_standard_name => 'PROCONVE L6',a_emission_standard_flag => 'Y');
    
    PK_EMISSION_ROBUSTNESS.add_emission_standard(a_emission_standard_name => 'New Short Term',a_emission_standard_flag => 'Y');  
    PK_EMISSION_ROBUSTNESS.add_emission_standard(a_emission_standard_name => 'New Long Term',a_emission_standard_flag => 'Y');  
    PK_EMISSION_ROBUSTNESS.add_emission_standard(a_emission_standard_name => 'Post New Long Term',a_emission_standard_flag => 'Y');
    
    PK_EMISSION_ROBUSTNESS.add_emission_standard(a_emission_standard_name => 'India 2000',a_emission_standard_flag => 'Y');  
    PK_EMISSION_ROBUSTNESS.add_emission_standard(a_emission_standard_name => 'BS 2',a_emission_standard_flag => 'Y');  
    PK_EMISSION_ROBUSTNESS.add_emission_standard(a_emission_standard_name => 'BS 3',a_emission_standard_flag => 'Y');  
    PK_EMISSION_ROBUSTNESS.add_emission_standard(a_emission_standard_name => 'BS 4',a_emission_standard_flag => 'Y');  
    PK_EMISSION_ROBUSTNESS.add_emission_standard(a_emission_standard_name => 'BS 5',a_emission_standard_flag => 'Y');  
    PK_EMISSION_ROBUSTNESS.add_emission_standard(a_emission_standard_name => 'BS 6',a_emission_standard_flag => 'Y');  
    PK_EMISSION_ROBUSTNESS.add_emission_standard(a_emission_standard_name => 'BS 1 2-Wheeler',a_emission_standard_flag => 'Y');  
    PK_EMISSION_ROBUSTNESS.add_emission_standard(a_emission_standard_name => 'BS 2 2-Wheeler',a_emission_standard_flag => 'Y');  
    PK_EMISSION_ROBUSTNESS.add_emission_standard(a_emission_standard_name => 'BS 3 2-Wheeler',a_emission_standard_flag => 'Y');  
    PK_EMISSION_ROBUSTNESS.add_emission_standard(a_emission_standard_name => 'BS 1 3-Wheeler',a_emission_standard_flag => 'Y');  
    PK_EMISSION_ROBUSTNESS.add_emission_standard(a_emission_standard_name => 'BS 2 3-Wheeler',a_emission_standard_flag => 'Y');  
    PK_EMISSION_ROBUSTNESS.add_emission_standard(a_emission_standard_name => 'BS 3 3-Wheeler',a_emission_standard_flag => 'Y');
end;
/

begin
    /* Emission, Testcase, Measure */
    --PK_EMISSION_ROBUSTNESS.add_emission_standard('Emission Robustness Evaluation_V1.4 - Variant 1 - NEDC','N','N','N');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 1 - 1. NEDC - 4 tests in a row','N','Y','N');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 1 - 1.1 NEDC 20°C Sequence cold','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 1 - 1.2 NEDC Sequence warm','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 1 - 1.3 1st NEDC Sequence hot','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 1 - 1.4 2nd NEDC Sequence hot','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 1 - 2. NEDC 14°C','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 1 - 3. NEDC 20°C in all drive modes','N','Y','N');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 1 - 3.1 NEDC 20°C Drive Mode 1','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 1 - 3.2 NEDC 20°C Drive Mode 2','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 1 - 3.3 NEDC 20°C Drive Mode 3','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 1 - 3.4 NEDC 20°C Drive Mode 4','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 1 - 3.5 NEDC 20°C Drive Mode 5','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 1 - 3.6 NEDC 20°C Drive Mode 6','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 1 - 4. WLTC 20°C','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 1 - 5. ECE -7°C','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 1 - 6. Steady state emissions','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 1 - 7. RDESi normal / aggressive','N','Y','N');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 1 - 7.1 RDESi normal','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 1 - 7.2 RDESi aggressive','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 1 - 8. RDE with PEMS','N','Y','N');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 1 - 8.1 City (unweighted)','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 1 - 8.2 City (EMROAD)','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 1 - 8.3 City (SPF)','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 1 - 8.4 Total (unweighted)','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 1 - 8.5 Total (EMROAD)','N','Y','Y');
    
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 1 - 1. NEDC - 4 tests in a row','Emission Robustness Evaluation_V1.4 - Variant 1 - NEDC');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 1 - 2. NEDC 14°C','Emission Robustness Evaluation_V1.4 - Variant 1 - NEDC');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 1 - 3. NEDC 20°C in all drive modes','Emission Robustness Evaluation_V1.4 - Variant 1 - NEDC');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 1 - 4. WLTC 20°C','Emission Robustness Evaluation_V1.4 - Variant 1 - NEDC');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 1 - 5. ECE -7°C','Emission Robustness Evaluation_V1.4 - Variant 1 - NEDC');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 1 - 6. Steady state emissions','Emission Robustness Evaluation_V1.4 - Variant 1 - NEDC');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 1 - 7. RDESi normal / aggressive','Emission Robustness Evaluation_V1.4 - Variant 1 - NEDC');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 1 - 8. RDE with PEMS','Emission Robustness Evaluation_V1.4 - Variant 1 - NEDC');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 1 - 1.1 NEDC 20°C Sequence cold','Variant 1 - 1. NEDC - 4 tests in a row');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 1 - 1.2 NEDC Sequence warm','Variant 1 - 1. NEDC - 4 tests in a row');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 1 - 1.3 1st NEDC Sequence hot','Variant 1 - 1. NEDC - 4 tests in a row');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 1 - 1.4 2nd NEDC Sequence hot','Variant 1 - 1. NEDC - 4 tests in a row');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 1 - 3.1 NEDC 20°C Drive Mode 1','Variant 1 - 3. NEDC 20°C in all drive modes');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 1 - 3.2 NEDC 20°C Drive Mode 2','Variant 1 - 3. NEDC 20°C in all drive modes');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 1 - 3.3 NEDC 20°C Drive Mode 3','Variant 1 - 3. NEDC 20°C in all drive modes');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 1 - 3.4 NEDC 20°C Drive Mode 4','Variant 1 - 3. NEDC 20°C in all drive modes');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 1 - 3.5 NEDC 20°C Drive Mode 5','Variant 1 - 3. NEDC 20°C in all drive modes');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 1 - 3.6 NEDC 20°C Drive Mode 6','Variant 1 - 3. NEDC 20°C in all drive modes');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 1 - 7.1 RDESi normal','Variant 1 - 7. RDESi normal / aggressive');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 1 - 7.2 RDESi aggressive','Variant 1 - 7. RDESi normal / aggressive');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 1 - 8.1 City (unweighted)','Variant 1 - 8. RDE with PEMS');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 1 - 8.2 City (EMROAD)','Variant 1 - 8. RDE with PEMS');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 1 - 8.3 City (SPF)','Variant 1 - 8. RDE with PEMS');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 1 - 8.4 Total (unweighted)','Variant 1 - 8. RDE with PEMS');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 1 - 8.5 Total (EMROAD)','Variant 1 - 8. RDE with PEMS');
end;
/

begin
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Emission Robustness Evaluation_V1.4 - Variant 2 - FTP75','N','N','N');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 2 - 1. FTP75 - 4 tests in a row','N','Y','N');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 2 - 1.1 FTP75 20°C Sequence cold','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 2 - 1.2 FTP75 Sequence warm','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 2 - 1.3 1st FTP72 Sequence hot','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 2 - 1.4 2nd FTP72 Sequence hot','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 2 - 2. FTP75 10°C','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 2 - 3. FTP75 20°C in all drive modes','N','Y','N');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 2 - 3.1 FTP75 20°C Drive Mode 1','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 2 - 3.2 FTP75 20°C Drive Mode 2','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 2 - 3.3 FTP75 20°C Drive Mode 3','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 2 - 3.4 FTP75 20°C Drive Mode 4','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 2 - 3.5 FTP75 20°C Drive Mode 5','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 2 - 3.6 FTP75 20°C Drive Mode 6','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 2 - 4. US06, HWY','N','Y','N');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 2 - 4.1 US06','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 2 - 4.2 HWY','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 2 - 5. FTP75 -7°C','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 2 - 6. Steady state emissions','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 2 - 7. RDESi normal / aggressive','N','Y','N');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 2 - 7.1 RDESi normal','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 2 - 7.2 RDESi aggressive','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 2 - 8. RDE with PEMS','N','Y','N');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 2 - 8.1 City (unweighted)','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 2 - 8.2 City (EMROAD)','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 2 - 8.3 City (SPF)','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 2 - 8.4 Total (unweighted)','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 2 - 8.5 Total (EMROAD)','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 2 - 8.6 Total (SPF)','N','Y','Y');
    
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 2 - 1. FTP75 - 4 tests in a row','Emission Robustness Evaluation_V1.4 - Variant 2 - FTP75');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 2 - 2. FTP75 10°C','Emission Robustness Evaluation_V1.4 - Variant 2 - FTP75');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 2 - 3. FTP75 20°C in all drive modes','Emission Robustness Evaluation_V1.4 - Variant 2 - FTP75');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 2 - 4. US06, HWY','Emission Robustness Evaluation_V1.4 - Variant 2 - FTP75');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 2 - 5. FTP75 -7°C','Emission Robustness Evaluation_V1.4 - Variant 2 - FTP75');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 2 - 6. Steady state emissions','Emission Robustness Evaluation_V1.4 - Variant 2 - FTP75');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 2 - 7. RDESi normal / aggressive','Emission Robustness Evaluation_V1.4 - Variant 2 - FTP75');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 2 - 8. RDE with PEMS','Emission Robustness Evaluation_V1.4 - Variant 2 - FTP75');
    
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 2 - 1.1 FTP75 20°C Sequence cold','Variant 2 - 1. FTP75 - 4 tests in a row');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 2 - 1.2 FTP75 Sequence warm','Variant 2 - 1. FTP75 - 4 tests in a row');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 2 - 1.3 1st FTP72 Sequence hot','Variant 2 - 1. FTP75 - 4 tests in a row');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 2 - 1.4 2nd FTP72 Sequence hot','Variant 2 - 1. FTP75 - 4 tests in a row');
    
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 2 - 3.1 FTP75 20°C Drive Mode 1','Variant 2 - 3. FTP75 20°C in all drive modes');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 2 - 3.2 FTP75 20°C Drive Mode 2','Variant 2 - 3. FTP75 20°C in all drive modes');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 2 - 3.3 FTP75 20°C Drive Mode 3','Variant 2 - 3. FTP75 20°C in all drive modes');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 2 - 3.4 FTP75 20°C Drive Mode 4','Variant 2 - 3. FTP75 20°C in all drive modes');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 2 - 3.5 FTP75 20°C Drive Mode 5','Variant 2 - 3. FTP75 20°C in all drive modes');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 2 - 3.6 FTP75 20°C Drive Mode 6','Variant 2 - 3. FTP75 20°C in all drive modes');
    
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 2 - 4.1 US06','Variant 2 - 4. US06, HWY');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 2 - 4.2 HWY','Variant 2 - 4. US06, HWY');
    
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 2 - 7.1 RDESi normal','Variant 2 - 7. RDESi normal / aggressive');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 2 - 7.2 RDESi aggressive','Variant 2 - 7. RDESi normal / aggressive');
    
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 2 - 8.1 City (unweighted)','Variant 2 - 8. RDE with PEMS');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 2 - 8.2 City (EMROAD)','Variant 2 - 8. RDE with PEMS');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 2 - 8.3 City (SPF)','Variant 2 - 8. RDE with PEMS');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 2 - 8.4 Total (unweighted)','Variant 2 - 8. RDE with PEMS');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 2 - 8.5 Total (EMROAD)','Variant 2 - 8. RDE with PEMS');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 2 - 8.6 Total (SPF)','Variant 2 - 8. RDE with PEMS');
end;
/

begin
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Emission Robustness Evaluation_V1.4 - Variant 3 - JC08','N','N','N');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 3 - 1. MIDC - 4 tests in a row','N','Y','N');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 3 - 1.1 MIDC 20°C Sequence cold','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 3 - 1.2 MIDC Sequence warm','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 3 - 1.3 1st MIDC Sequence hot','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 3 - 1.4 2nd MIDC Sequence hot','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 3 - 2. MIDC 20°C in all drive modes','N','Y','N');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 3 - 2.1 MIDC 20°C Drive Mode 1','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 3 - 2.2 MIDC 20°C Drive Mode 2','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 3 - 2.3 MIDC 20°C Drive Mode 3','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 3 - 2.4 MIDC 20°C Drive Mode 4','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 3 - 2.5 MIDC 20°C Drive Mode 5','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 3 - 2.6 MIDC 20°C Drive Mode 6','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 3 - 3. WLTC 20°C','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 3 - 4. Steady state emissions','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 3 - 5. RDESi normal / aggressive','N','Y','N');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 3 - 5.1 RDESi normal','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 3 - 5.2 RDESi aggressive','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 3 - 6. RDE with PEMS','N','Y','N');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 3 - 6.1 City (unweighted)','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 3 - 6.2 City (EMROAD)','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 3 - 6.3 City (SPF)','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 3 - 6.4 Total (unweighted)','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 3 - 6.5 Total (EMROAD)','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 3 - 6.6 Total (SPF)','N','Y','Y');
    
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 3 - 1. MIDC - 4 tests in a row','Emission Robustness Evaluation_V1.4 - Variant 3 - JC08');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 3 - 2. MIDC 20°C in all drive modes','Emission Robustness Evaluation_V1.4 - Variant 3 - JC08');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 3 - 3. WLTC 20°C','Emission Robustness Evaluation_V1.4 - Variant 3 - JC08');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 3 - 4. Steady state emissions','Emission Robustness Evaluation_V1.4 - Variant 3 - JC08');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 3 - 5. RDESi normal / aggressive','Emission Robustness Evaluation_V1.4 - Variant 3 - JC08');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 3 - 6. RDE with PEMS','Emission Robustness Evaluation_V1.4 - Variant 3 - JC08');
    
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 3 - 1.1 MIDC 20°C Sequence cold','Variant 3 - 1. MIDC - 4 tests in a row');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 3 - 1.2 MIDC Sequence warm','Variant 3 - 1. MIDC - 4 tests in a row');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 3 - 1.3 1st MIDC Sequence hot','Variant 3 - 1. MIDC - 4 tests in a row');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 3 - 1.4 2nd MIDC Sequence hot','Variant 3 - 1. MIDC - 4 tests in a row');
    
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 3 - 2.1 MIDC 20°C Drive Mode 1','Variant 3 - 2. MIDC 20°C in all drive modes');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 3 - 2.2 MIDC 20°C Drive Mode 2','Variant 3 - 2. MIDC 20°C in all drive modes');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 3 - 2.3 MIDC 20°C Drive Mode 3','Variant 3 - 2. MIDC 20°C in all drive modes');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 3 - 2.4 MIDC 20°C Drive Mode 4','Variant 3 - 2. MIDC 20°C in all drive modes');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 3 - 2.5 MIDC 20°C Drive Mode 5','Variant 3 - 2. MIDC 20°C in all drive modes');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 3 - 2.6 MIDC 20°C Drive Mode 6','Variant 3 - 2. MIDC 20°C in all drive modes');
    
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 3 - 5.1 RDESi normal','Variant 3 - 5. RDESi normal / aggressive');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 3 - 5.2 RDESi aggressive','Variant 3 - 5. RDESi normal / aggressive');
    
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 3 - 6.1 City (unweighted)','Variant 3 - 6. RDE with PEMS');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 3 - 6.2 City (EMROAD)','Variant 3 - 6. RDE with PEMS');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 3 - 6.3 City (SPF)','Variant 3 - 6. RDE with PEMS');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 3 - 6.4 Total (unweighted)','Variant 3 - 6. RDE with PEMS');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 3 - 6.5 Total (EMROAD)','Variant 3 - 6. RDE with PEMS');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 3 - 6.6 Total (SPF)','Variant 3 - 6. RDE with PEMS');
end;
/

begin
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Emission Robustness Evaluation_V1.4 - Variant 4 - MIDC','N','N','N');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 4 - 1. MIDC - 4 tests in a row','N','Y','N');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 4 - 1.1 MIDC 20°C Sequence cold','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 4 - 1.2 MIDC Sequence warm','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 4 - 1.3 1st MIDC Sequence hot','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 4 - 1.4 2nd MIDC Sequence hot','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 4 - 2. MIDC 20°C in all drive modes','N','Y','N');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 4 - 2.1 MIDC 20°C Drive Mode 1','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 4 - 2.2 MIDC 20°C Drive Mode 2','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 4 - 2.3 MIDC 20°C Drive Mode 3','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 4 - 2.4 MIDC 20°C Drive Mode 4','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 4 - 2.5 MIDC 20°C Drive Mode 5','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 4 - 2.6 MIDC 20°C Drive Mode 6','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 4 - 3. WLTC 20°C','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 4 - 4. Steady state emissions','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 4 - 5. RDESi normal / aggressive','N','Y','N');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 4 - 5.1 RDESi normal','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 4 - 5.2 RDESi aggressive','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 4 - 6. RDE with PEMS','N','Y','N');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 4 - 6.1 City (unweighted)','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 4 - 6.2 City (EMROAD)','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 4 - 6.3 City (SPF)','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 4 - 6.4 Total (unweighted)','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 4 - 6.5 Total (EMROAD)','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 4 - 6.6 Total (SPF)','N','Y','Y');
    
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 4 - 1. MIDC - 4 tests in a row','Emission Robustness Evaluation_V1.4 - Variant 4 - MIDC');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 4 - 2. MIDC 20°C in all drive modes','Emission Robustness Evaluation_V1.4 - Variant 4 - MIDC');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 4 - 3. WLTC 20°C','Emission Robustness Evaluation_V1.4 - Variant 4 - MIDC');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 4 - 4. Steady state emissions','Emission Robustness Evaluation_V1.4 - Variant 4 - MIDC');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 4 - 5. RDESi normal / aggressive','Emission Robustness Evaluation_V1.4 - Variant 4 - MIDC');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 4 - 6. RDE with PEMS','Emission Robustness Evaluation_V1.4 - Variant 4 - MIDC');
    
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 4 - 1.1 MIDC 20°C Sequence cold','Variant 4 - 1. MIDC - 4 tests in a row');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 4 - 1.2 MIDC Sequence warm','Variant 4 - 1. MIDC - 4 tests in a row');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 4 - 1.3 1st MIDC Sequence hot','Variant 4 - 1. MIDC - 4 tests in a row');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 4 - 1.4 2nd MIDC Sequence hot','Variant 4 - 1. MIDC - 4 tests in a row');
    
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 4 - 2.1 MIDC 20°C Drive Mode 1','Variant 4 - 2. MIDC 20°C in all drive modes');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 4 - 2.2 MIDC 20°C Drive Mode 2','Variant 4 - 2. MIDC 20°C in all drive modes');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 4 - 2.3 MIDC 20°C Drive Mode 3','Variant 4 - 2. MIDC 20°C in all drive modes');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 4 - 2.4 MIDC 20°C Drive Mode 4','Variant 4 - 2. MIDC 20°C in all drive modes');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 4 - 2.5 MIDC 20°C Drive Mode 5','Variant 4 - 2. MIDC 20°C in all drive modes');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 4 - 2.6 MIDC 20°C Drive Mode 6','Variant 4 - 2. MIDC 20°C in all drive modes');
    
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 4 - 5.1 RDESi normal','Variant 4 - 5. RDESi normal / aggressive');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 4 - 5.2 RDESi aggressive','Variant 4 - 5. RDESi normal / aggressive');
    
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 4 - 6.1 City (unweighted)','Variant 4 - 6. RDE with PEMS');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 4 - 6.2 City (EMROAD)','Variant 4 - 6. RDE with PEMS');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 4 - 6.3 City (SPF)','Variant 4 - 6. RDE with PEMS');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 4 - 6.4 Total (unweighted)','Variant 4 - 6. RDE with PEMS');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 4 - 6.5 Total (EMROAD)','Variant 4 - 6. RDE with PEMS');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 4 - 6.6 Total (SPF)','Variant 4 - 6. RDE with PEMS');
end;
/

begin
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Emission Robustness Evaluation_V1.4 - Variant 5 - WMTC','N','N','N');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 5 - 1. WMTC - 4 tests in a row','N','Y','N');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 5 - 1.1 WMTC 20°C Sequence cold','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 5 - 1.2 WMTC Sequence warm','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 5 - 1.3 1st WMTC Sequence hot','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 5 - 1.4 2nd WMTC Sequence hot','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 5 - 2. WMTC 5°C','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 5 - 3. WMTC 20°C in all drive modes','N','Y','N');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 5 - 3.1 WMTC 20°C Drive Mode 1','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 5 - 3.2 WMTC 20°C Drive Mode 2','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 5 - 3.3 WMTC 20°C Drive Mode 3','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 5 - 3.4 WMTC 20°C Drive Mode 4','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 5 - 3.5 WMTC 20°C Drive Mode 5','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 5 - 3.6 WMTC 20°C Drive Mode 6','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 5 - 4. Steady state emissions','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 5 - 5. RDE-RC-AGGR','N','Y','Y');
    
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 5 - 1. WMTC - 4 tests in a row','Emission Robustness Evaluation_V1.4 - Variant 5 - WMTC');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 5 - 2. WMTC 5°C','Emission Robustness Evaluation_V1.4 - Variant 5 - WMTC');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 5 - 3. WMTC 20°C in all drive modes','Emission Robustness Evaluation_V1.4 - Variant 5 - WMTC');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 5 - 4. Steady state emissions','Emission Robustness Evaluation_V1.4 - Variant 5 - WMTC');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 5 - 5. RDE-RC-AGGR','Emission Robustness Evaluation_V1.4 - Variant 5 - WMTC');
    
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 5 - 1.1 WMTC 20°C Sequence cold','Variant 5 - 1. WMTC - 4 tests in a row');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 5 - 1.2 WMTC Sequence warm','Variant 5 - 1. WMTC - 4 tests in a row');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 5 - 1.3 1st WMTC Sequence hot','Variant 5 - 1. WMTC - 4 tests in a row');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 5 - 1.4 2nd WMTC Sequence hot','Variant 5 - 1. WMTC - 4 tests in a row');
    
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 5 - 3.1 WMTC 20°C Drive Mode 1','Variant 5 - 3. WMTC 20°C in all drive modes');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 5 - 3.2 WMTC 20°C Drive Mode 2','Variant 5 - 3. WMTC 20°C in all drive modes');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 5 - 3.3 WMTC 20°C Drive Mode 3','Variant 5 - 3. WMTC 20°C in all drive modes');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 5 - 3.4 WMTC 20°C Drive Mode 4','Variant 5 - 3. WMTC 20°C in all drive modes');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 5 - 3.5 WMTC 20°C Drive Mode 5','Variant 5 - 3. WMTC 20°C in all drive modes');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 5 - 3.6 WMTC 20°C Drive Mode 6','Variant 5 - 3. WMTC 20°C in all drive modes');
end;
/

begin
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Emission Robustness Evaluation_V1.4 - Variant 6 - WLTC','N','N','N');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 6 - 1. WLTC - 4 tests in a row','N','Y','N');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 6 - 1.1 WLTC 20°C Sequence cold','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 6 - 1.2 WLTC Sequence warm','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 6 - 1.3 1st WLTC Sequence hot','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 6 - 1.4 2nd WLTC Sequence hot','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 6 - 2. WLTC 14°C','N','Y','N');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 6 - 3. WLTC 20°C in all drive modes','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 6 - 2.1 WLTC 20°C Drive Mode 1','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 6 - 2.2 WLTC 20°C Drive Mode 2','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 6 - 2.3 WLTC 20°C Drive Mode 3','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 6 - 2.4 WLTC 20°C Drive Mode 4','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 6 - 2.5 WLTC 20°C Drive Mode 5','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 6 - 2.6 WLTC 20°C Drive Mode 6','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 6 - 4. ECE -7°C','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 6 - 5. RDE on chassis dyno','N','Y','N');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 6 - 5.1 RDESi aggressive@minimum moderate temperature limit','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 6 - 5.2 RDES aggressive@maximum moderate temperature limit','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 6 - 5.3 RDESi aggressive@maximum moderate temperature limit and extended altitude','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 6 - 6. Steady state emissions','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 6 - 7. RDE with PEMS','N','Y','N');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 6 - 7.1 City (unweighted)','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 6 - 7.2 City (EMROAD)','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 6 - 7.3 City (SPF)','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 6 - 7.4 Total (unweighted)','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 6 - 7.5 Total (EMROAD)','N','Y','Y');
    PK_EMISSION_ROBUSTNESS.add_emission_standard('Variant 6 - 7.6 Total (SPF)','N','Y','Y');
    
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 6 - 1. WLTC - 4 tests in a row','Emission Robustness Evaluation_V1.4 - Variant 6 - WLTC');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 6 - 2. WLTC 14°C','Emission Robustness Evaluation_V1.4 - Variant 6 - WLTC');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 6 - 3. WLTC 20°C in all drive modes','Emission Robustness Evaluation_V1.4 - Variant 6 - WLTC');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 6 - 4. ECE -7°C','Emission Robustness Evaluation_V1.4 - Variant 6 - WLTC');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 6 - 5. RDE on chassis dyno','Emission Robustness Evaluation_V1.4 - Variant 6 - WLTC');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 6 - 6. Steady state emissions','Emission Robustness Evaluation_V1.4 - Variant 6 - WLTC');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 6 - 7. RDE with PEMS','Emission Robustness Evaluation_V1.4 - Variant 6 - WLTC');
    
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 6 - 1.1 WLTC 20°C Sequence cold','Variant 6 - 1. WLTC - 4 tests in a row');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 6 - 1.2 WLTC Sequence warm','Variant 6 - 1. WLTC - 4 tests in a row');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 6 - 1.3 1st WLTC Sequence hot','Variant 6 - 1. WLTC - 4 tests in a row');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 6 - 1.4 2nd WLTC Sequence hot','Variant 6 - 1. WLTC - 4 tests in a row');
    
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 6 - 2.1 WLTC 20°C Drive Mode 1','Variant 6 - 3. WLTC 20°C in all drive modes');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 6 - 2.2 WLTC 20°C Drive Mode 2','Variant 6 - 3. WLTC 20°C in all drive modes');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 6 - 2.3 WLTC 20°C Drive Mode 3','Variant 6 - 3. WLTC 20°C in all drive modes');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 6 - 2.4 WLTC 20°C Drive Mode 4','Variant 6 - 3. WLTC 20°C in all drive modes');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 6 - 2.5 WLTC 20°C Drive Mode 5','Variant 6 - 3. WLTC 20°C in all drive modes');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 6 - 2.6 WLTC 20°C Drive Mode 6','Variant 6 - 3. WLTC 20°C in all drive modes');
    
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 6 - 5.1 RDESi aggressive@minimum moderate temperature limit','Variant 6 - 5. RDE on chassis dyno');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 6 - 5.2 RDES aggressive@maximum moderate temperature limit','Variant 6 - 5. RDE on chassis dyno');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 6 - 5.3 RDESi aggressive@maximum moderate temperature limit and extended altitude','Variant 6 - 5. RDE on chassis dyno');
    
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 6 - 7.1 City (unweighted)','Variant 6 - 7. RDE with PEMS');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 6 - 7.2 City (EMROAD)','Variant 6 - 7. RDE with PEMS');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 6 - 7.3 City (SPF)','Variant 6 - 7. RDE with PEMS');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 6 - 7.4 Total (unweighted)','Variant 6 - 7. RDE with PEMS');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 6 - 7.5 Total (EMROAD)','Variant 6 - 7. RDE with PEMS');
    PK_EMISSION_ROBUSTNESS.set_emission_standard_parent('Variant 6 - 7.6 Total (SPF)','Variant 6 - 7. RDE with PEMS');
end;
/