--
-- Insert only features, which are used in rules
-- use the latest date when the feature was used in a release or a rule
--
insert into T_SSD_Features
(  Feature_ID
 , Feature_Text
 , ICDM_Only
 , Created_User
 , Created_Date
 , Version
 , last_used_ssd
)
SELECT distinct comp.feature_id
     , fea.feature_text
	 , 'N'
     , user
	 , sysdate
     , 1
	 , lastused.last_used
from v_ldb2_comp@DGSPRO.WORLD@K5ESK_LDB2 comp
   , v_ldb2_features@DGSPRO.WORLD@K5ESK_LDB2 fea
   , (
		select max(cre_date) as last_used
			 , feature_id
		--     , value_id
		 from
		 (
		select max(cre_date) as cre_date
			 , feature_id
			 , value_id 
		 from 
		(
		 select max(cre_date) as cre_date
			  , feature_id
			  , value_id
			  , 'Release' as inf 
		   from v_ldb2_prj_rel_comp@DGSPRO.WORLD@K5ESK_LDB2 fea
			  , v_ldb2_project_release@DGSPRO.WORLD@K5ESK_LDB2 rel
		  where rel.pro_rel_id = fea.pro_rel_id
		  group by feature_id
				 , value_id 
		 union
		 select max(cre_date) as cre_date
			  , feature_id
			  , value_id
			  , 'Rule' as inf 
		   from v_ldb2_ssd2@DGSPRO.WORLD@K5ESK_LDB2 ssd 
			  , v_ldb2_comp@DGSPRO.WORLD@K5ESK_LDB2 comp
		  where comp.lab_obj_id = ssd.lab_obj_id 
			and comp.rev_id = ssd.rev_id
		  group by feature_id
				 , value_id
		)a
		group by feature_id
			   , value_id
		order by feature_id
			   , value_id
		 )
		group by feature_id
		--       , value_id
     ) lastused
where comp.feature_id = fea.feature_id 
  and lastused.feature_id = fea.feature_id
; 

--
-- Insert only feature values, which are used in rules
-- use the latest date when the feature value was used in a release or a rule
--
insert into T_SSD_Values
(  Value_ID
 , Feature_ID
 , Value_Text
 , ICDM_Only
 , Created_User
 , Created_Date
 , Version
 , last_used_ssd
)
SELECT comp.value_id
     , comp.feature_id
     , val.value_text 
     , 'N'
     , user
     , sysdate
     , 1
	 , lastused.last_used
from v_ldb2_comp@DGSPRO.WORLD@K5ESK_LDB2 comp
   , v_ldb2_features@DGSPRO.WORLD@K5ESK_LDB2 fea
   , v_ldb2_values@DGSPRO.WORLD@K5ESK_LDB2 val
   , (
		select max(cre_date) as last_used
		     , value_id
		 from
		 (
		select max(cre_date) as cre_date
			 , feature_id
			 , value_id 
		 from 
		(
		 select max(cre_date) as cre_date
			  , feature_id
			  , value_id
			  , 'Release' as inf 
		   from v_ldb2_prj_rel_comp@DGSPRO.WORLD@K5ESK_LDB2 fea
			  , v_ldb2_project_release@DGSPRO.WORLD@K5ESK_LDB2 rel
		  where rel.pro_rel_id = fea.pro_rel_id
		  group by feature_id
				 , value_id 
		 union
		 select max(cre_date) as cre_date
			  , feature_id
			  , value_id
			  , 'Rule' as inf 
		   from v_ldb2_ssd2@DGSPRO.WORLD@K5ESK_LDB2 ssd 
			  , v_ldb2_comp@DGSPRO.WORLD@K5ESK_LDB2 comp
		  where comp.lab_obj_id = ssd.lab_obj_id 
			and comp.rev_id = ssd.rev_id
		  group by feature_id
				 , value_id
		)a
		group by feature_id
			   , value_id
		order by feature_id
			   , value_id
		 )
		group by value_id
     ) lastused
where comp.feature_id = fea.feature_id 
  and comp.value_id = val.value_id 
  and lastused.value_id = val.value_id 
group by comp.feature_id
       , fea.feature_text
       , comp.value_id
       , val.value_text
       , lastused.last_used
order by comp.feature_id
       , comp.value_id
;       


--
-- some check statements
--
select * from V_LDB2_FEATURES
minus 
select * from V_LDB2_FEATURES@DGSPRO.WORLD@K5ESK_VILLA_RO
;

select * from V_LDB2_FEATURES@DGSPRO.WORLD@K5ESK_VILLA_RO
minus 
select * from V_LDB2_FEATURES
;

select * from V_LDB2_VALUES
minus 
select * from V_LDB2_VALUES@DGSPRO.WORLD@K5ESK_VILLA_RO
;

select * from V_LDB2_VALUES@DGSPRO.WORLD@K5ESK_VILLA_RO
minus 
select * from V_LDB2_VALUES
;
