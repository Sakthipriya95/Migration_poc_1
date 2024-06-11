


Table_Data
- change the tool version


insert into T_RVW_VARIANTS
(RESULT_ID, CREATED_USER, CREATED_DATE, VARIANT_ID)
select rres.RESULT_ID
     , rres.CREATED_USER
     , rres.CREATED_DATE
     , min(pvar.VARIANT_ID) as VARIANT_ID
  from T_RVW_RESULTS rres
     , TABV_PROJECT_VARIANTS pvar
     , T_PIDC_A2L pa2l
 where rres.PIDC_A2L_ID = pa2l.PIDC_A2L_ID
   and pa2l.PIDC_VERS_ID = pvar.PIDC_VERS_ID
   and pvar.DELETED_FLAG = 'N'
 group by rres.RESULT_ID
     , rres.CREATED_USER
     , rres.CREATED_DATE
 order by rres.RESULT_ID
     , rres.CREATED_USER
     , rres.CREATED_DATE
;  