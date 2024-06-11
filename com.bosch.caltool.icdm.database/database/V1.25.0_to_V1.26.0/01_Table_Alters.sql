spool c:\temp\01_Table_Alters.log


-------------------------------------------------
--iCDM-2237
----------------------------------------------------------
--Alter t_functions to add virtual  column function_name_upper to store the upper name of the function
alter table t_functions add (function_name_upper varchar2(255) GENERATED ALWAYS AS (upper(name)) VIRTUAL);

--Alter t_functions to add   column relevant_name to store if the function is relevant default false
ALTER TABLE t_functions ADD relevant_name  varchar2(1) default 'N';

--Alter t_functionversions to add virtual column function_name_upper to store the upper name of the functione
alter table t_functionversions add (function_name_upper varchar2(255) GENERATED ALWAYS AS (upper(funcname)) VIRTUAL);



--Alter T_FUNCVERS_UNIQUE to add virtual column function_name_upper to store the upper name of the functione
alter table T_FUNCVERS_UNIQUE add (function_name_upper varchar2(255) GENERATED ALWAYS AS (upper(funcname)) VIRTUAL);



-- Temp table for grouing the functions based on count
create table temp_params_per_function_case
as
select funcname,upper(funcname) funcname_upper_case,count(*) number_of_params
          from t_functionversions 
         group by funcname;
         

  
 ---- Initial update to 'N'
update t_functions
   set relevant_name = 'N';

-- Update the relevant flag to yes for the case with the most associated parameters   
update t_functions
   set relevant_name = 'Y'
  where exists( Select 1
                  from ( SELECT distinct FIRST_VALUE(funcname)
                           OVER (partition by funcname_upper_case ORDER BY number_of_params desc ROWS UNBOUNDED PRECEDING) AS most_params
                           FROM temp_params_per_function_case 
                        ) rel_functions
                 where rel_functions.most_params = t_functions.name)
 ;  
 
commit;

drop table temp_params_per_function_case;
    
-- For all functions without assigned parameters              
update t_functions
   set relevant_name = 'Y'
  where exists( Select 1
                  from ( select distinct FIRST_VALUE(name)
                                         OVER (partition by function_name_upper 
                                         ORDER BY created_date desc ROWS UNBOUNDED PRECEDING) AS first_created 
                           from t_functions all_functions
                          where not exists (Select 1
                                              from t_functions rel_func
                                             where rel_func.relevant_name = 'Y'
                                               and all_functions.function_name_upper = rel_func.function_name_upper ) 
                                            ) rel_functions
                where rel_functions.first_created = t_functions.name)
 ;   
commit;

-------------------------------------------------
--iCDM-2189
----------------------------------------------------------
--Alter T_QUESTIONNAIRE_VERSION to add three new columns

alter table T_QUESTIONNAIRE_VERSION add ( MEASURE_RELAVENT_FLAG VARCHAR2(1),
                                          MEASURE_HIDDEN_FLAG VARCHAR2(1),
                                          RESPONSIBLE_RELAVENT_FLAG VARCHAR2(1),
                                          RESPONSIBLE_HIDDEN_FLAG VARCHAR2(1),
                                          COMPLETION_DATE_RELAVENT_FLAG  VARCHAR2(1),
                                          COMPLETION_DATE_HIDDEN_FLAG  VARCHAR2(1));


-- create new table T_QNAIRE_ANS_OPEN_POINTS to store open points and relavent feilds

create table T_QNAIRE_ANS_OPEN_POINTS (OPEN_POINTS_ID NUMBER NOT NULL,
                            OPEN_POINTS VARCHAR2(4000),
                            MEASURE VARCHAR2(4000),
                            RESPONSIBLE NUMBER,
                            COMPLETION_DATE TIMESTAMP(6),
                            RESULT VARCHAR2(1),
                            RVW_ANSWER_ID NUMBER,
                            CREATED_USER VARCHAR2(100),
                            MODIFIED_USER VARCHAR2(100),
                            CREATED_DATE TIMESTAMP(6),
                            MODIFIED_DATE TIMESTAMP(6),
                            VERSION NUMBER NOT NULL,
CONSTRAINT T_QNAIRE_ANS_OPEN_POINTS_PK PRIMARY KEY (OPEN_POINTS_ID),
CONSTRAINT T_QNAIRE_ANS_OPEN_POINTS_FK1 FOREIGN KEY (RVW_ANSWER_ID) REFERENCES T_RVW_QNAIRE_ANSWER (RVW_ANSWER_ID));

ALTER TABLE T_QNAIRE_ANS_OPEN_POINTS ADD CONSTRAINT T_QNAIRE_ANS_OPEN_POINTS_FK2
FOREIGN KEY (RESPONSIBLE) REFERENCES TABV_APIC_USERS(USER_ID);

-- alter table T_QUESTION_CONFIG to add three new columns

alter table T_QUESTION_CONFIG add  ( MEASURE VARCHAR2(1),                                       
                                     RESPONSIBLE VARCHAR2(1),                                       
                                     COMPLETION_DATE  VARCHAR2(1));

-- alter table T_RVW_QNAIRE_ANSWER to rename temp_open_points
alter table T_RVW_QNAIRE_ANSWER add TEMP_OPEN_POINTS VARCHAR2(4000);                                     
                                     
---------------------------------------------------------
--iCDM-2234
--Alter TABV_PROJECT_ATTR to add TRNSFR_VCDM_YN column
----------------------------------------------------------
alter table TABV_PROJECT_ATTR
add TRNSFR_VCDM_YN varchar2(1);

---------------------------------------------------------
--ICDM-2241
--Focus matrix applicable flag for project attribute
----------------------------------------------------------

alter table TABV_PROJECT_ATTR add (FOCUS_MATRIX_YN VARCHAR2(1));

---------------------------------------------------------
--ICDM-2295
--Coulmn added for group attr flag
----------------------------------------------------------

 ALTER TABLE tabv_attributes add group_attr_flag varchar2(1);
 
 ---------------------------------------------------------
--ICDM-2295
--Table for Group Attr Values
----------------------------------------------------------
 
 CREATE TABLE T_GROUP_ATTR_VALUES(
   GAVL_ID NUMBER(15) PRIMARY KEY, 
   GRP_ATTR_VAL_ID NUMBER(15),
   PREDEFINED_VALUE_ID NUMBER(15), 
   CREATED_USER VARCHAR2(30) NOT NULL, 
    CREATED_DATE TIMESTAMP (6) NOT NULL, 
    MODIFIED_DATE TIMESTAMP (6), 
    MODIFIED_USER VARCHAR2(30), 
    VERSION NUMBER NOT NULL, 
    CONSTRAINT  T_GROUP_ATTR_VALUES_FK1 FOREIGN KEY (GRP_ATTR_VAL_ID) REFERENCES TABV_ATTR_VALUES (VALUE_ID),
    CONSTRAINT T_GROUP_ATTR_VALUES_FK2 FOREIGN KEY (PREDEFINED_VALUE_ID) REFERENCES TABV_ATTR_VALUES (VALUE_ID)
   );
  
---------------------------------------------------------
--ICDM-2295
--Table for Group Attr validity
----------------------------------------------------------
 CREATE TABLE T_GROUP_ATTR_VALIDITY(
   
    GAVD_ID NUMBER(15) PRIMARY KEY, 
    GAVL_ID NUMBER(15) Not null ,
    VALIDITY_VALUE_ID NUMBER(15) Not null,
    CREATED_USER VARCHAR2(30) NOT NULL, 
    CREATED_DATE TIMESTAMP (6) NOT NULL, 
    MODIFIED_DATE TIMESTAMP (6), 
    MODIFIED_USER VARCHAR2(30), 
    VERSION NUMBER NOT NULL, 

    CONSTRAINT  T_GROUP_ATTR_VALIDITY_FK1 FOREIGN KEY (GAVL_ID) REFERENCES T_GROUP_ATTR_VALUES (GAVL_ID),
    CONSTRAINT T_GROUP_ATTR_VALIDITY_FK2 FOREIGN KEY (VALIDITY_VALUE_ID) REFERENCES TABV_ATTR_VALUES (VALUE_ID)
   );
   
---------------------------------------------------------
--ICDM-2295
--Table for Group Attr validity
----------------------------------------------------------
   
   ALTER TABLE T_GROUP_ATTR_VALUES ADD PREDEFINED_ATTR_ID  number(15) not null;
   
   ALTER TABLE  T_GROUP_ATTR_VALUES ADD  CONSTRAINT T_GROUP_ATTR_VALUES_FK3  FOREIGN KEY (PREDEFINED_ATTR_ID) REFERENCES TABV_ATTRIBUTES(ATTR_ID);
   
---------------------------------------------------------
--ICDM-2305
--New Column for review score
----------------------------------------------------------
   
   ALTER TABLE T_RVW_PARAMETERS ADD review_score varchar2(1);
   
---------------------------------------------------------
--ICDM-1659
--New Columns for focus matrix related info in Change History
----------------------------------------------------------

   ALTER TABLE T_PIDC_CHANGE_HISTORY
   ADD OLD_FOCUS_MATRIX_YN VARCHAR2 (1 BYTE);

   ALTER TABLE T_PIDC_CHANGE_HISTORY
   ADD NEW_FOCUS_MATRIX_YN VARCHAR2 (1 BYTE);
   
---------------------------------------------------------
--ICDM-2278
--New Columns for Tranfer Vcdm related info in Change History
----------------------------------------------------------

   ALTER TABLE T_PIDC_CHANGE_HISTORY
   ADD OLD_TRANSFER_VCDM_YN VARCHAR2 (1 BYTE);

   ALTER TABLE T_PIDC_CHANGE_HISTORY
   ADD NEW_TRANSFER_VCDM_YN VARCHAR2 (1 BYTE);
   

spool off
