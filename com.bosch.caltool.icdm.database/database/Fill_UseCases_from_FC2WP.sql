insert into tabv_use_case_sections
  (name_eng, desc_eng, use_case_id, created_user)
select distinct 
       wp_group
     , 'section for UseCase ' || wp_group
     , uc.use_case_id
     , 'HEF2FE'
from t_fc2wp  fc2wp
     , tabv_use_case_groups  grp
     , tabv_use_cases  uc
where fc2wp_type = 'FC2WP2'
  and wp_number like 'A%'
  and uc.group_id = grp.group_id
  and grp.name_eng = 'CAL WP'
  and wp_group like uc.name_eng || '%'
  and not exists (select 1 from tabv_use_case_Sections where name_eng = fc2wp.wp_group)
order by wp_group
;

insert into tabv_use_case_points
  (use_case_id
  , section_id
  , name_eng
  , desc_eng
--  , desc_ger
  , created_user)
select distinct 
       wp_group,
     sec.use_case_id
     , sec.section_id
     , wp_number
     , wp_name_E
--     , wp_name_G
     , 'HEF2FE'
from t_fc2wp  fc2wp
     , tabv_use_case_sections  sec
where fc2wp_type = 'FC2WP2'
  and wp_number like 'A%'
  and fc2wp.wp_group = sec.name_eng
  and not exists (select 1 from tabv_use_case_points where name_eng = fc2wp.wp_number)
order by wp_number
;

insert into tabv_ucp_attrs
  (point_id, use_case_id, section_id, created_user, attr_id)
select point_id
     , use_case_id
     , section_id
     , 'HEF2FE'
     , attr.attr_id
--     , mod(length(ucp.desc_eng) + attr.attr_id, 10)
 from tabv_use_case_points  ucp
    , tabv_attributes       attr
where use_case_id > 329414
--  and attr.attr_id = 85
  and mod(length(ucp.desc_eng) + attr.attr_id, 10) IN (3, 7, 9)
;

