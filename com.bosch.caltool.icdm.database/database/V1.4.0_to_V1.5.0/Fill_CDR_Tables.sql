
--------------------------------------------------------
--  Fill T_PARAMETER
--------------------------------------------------------
insert into T_Parameter
  (  ID
   , Name
   , Longname
   , Ptype
   , Pclass
   , Iscodeword
   , Iscustprm
   , Created_Date
   , Created_User
   , Modifed_Date
   , Modifed_User
   , Version
  )
   select seqv_attributes.nextval as ID
        , t1.Name
        , t1.LONGNAME
        , t1.PTYPE
        , '-' as PCLASS
        , 'N' as isCodeword
        , 'N' as isCustPrm
        , t1.CREATED_DATE
        , user as CREATED_USER
        , null as MODIFED_DATE
        , null as MODIFED_USER
        , 1 as VERSION
     from (select name, ptype, LONGNAME, CREATED_DATE
             from T_Parameter@dgspro.world@k5esk_villa_ro
          ) t1   
;

commit;

--------------------------------------------------------
--  Fill T_FUNCTIONS
--------------------------------------------------------
insert into T_Functions
  (  ID
   , Name
   , Iscustfunc
   , Created_Date
   , Created_User
   , Modifed_Date
   , Modifed_User
   , Version
  )
   select seqv_attributes.nextval as ID
        , t1.Name
        , t1.Longname as LONGNAME
        , 'N' as isCustFunc
        , t1.CREATED_DATE
        , user as CREATED_USER
        , null as MODIFED_DATE
        , null as MODIFED_USER
        , 1 as VERSION
     from (select name, LONGNAME, CREATED_DATE
             from T_Functions@dgspro.world@k5esk_villa_ro t2
          ) t1   
;     

commit;


--------------------------------------------------------
--  Fill T_FUNCTIONVERSIONS
--------------------------------------------------------
insert into T_FunctionVersions
  (  ID
   , FuncName
   , Funcversion 
   , Defcharname
   , Version
   , Created_Date
  )
   select seqv_attributes.nextval as ID
        , FuncName
        , FuncVersion
        , DefCharName
        , 1 as VERSION
        , CREATED_DATE
     from (select
                  t1.FuncName
                , t1.FuncVersion
                , t1.DefCharName
                , t1.CREATED_DATE
             from T_FunctionVersions@dgspro.world@k5esk_villa_ro t1
          )
;     

commit;


--------------------------------------------------------
--  Fill T_UNITS
--------------------------------------------------------
insert into T_Units
  (  Unit
   , Created_Date
   , Version
  )
   select   t1.UNIT
          , t1.CREATED_DATE
          , 1              as VERSION
     from T_Units@dgspro.world@k5esk_villa_ro t1
;  

commit;

