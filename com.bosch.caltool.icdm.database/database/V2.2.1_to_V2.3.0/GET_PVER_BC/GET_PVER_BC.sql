CREATE GLOBAL TEMPORARY TABLE GTT_EASEE_ELEMENTS
   (  ID NUMBER
	, ELEMENT_CLASS VARCHAR2(50 BYTE)
	, ELEMENT_NAME VARCHAR2(255 BYTE)
	, ELEMENT_VARIANT VARCHAR2(255 BYTE)
	, ELEMENT_REVISION NUMBER
	, CONTAINER_ID NUMBER
	, PRIMARY KEY (ID) ENABLE
   ) ON COMMIT DELETE ROWS ;


--create or replace FUNCTION Get_PVER_BC (  pPverName     IN VARCHAR2
--                                        , pPverVariant  IN VARCHAR2
--                                        , pPverRevision IN NUMBER
--                                       ) return NUMBER IS
create or replace procedure Get_PVER_BC (  pPverName     IN VARCHAR2
                                         , pPverVariant  IN VARCHAR2
                                         , pPverRevision IN NUMBER
                                        ) IS
-- declare  

  -- get the SDOM PVER ID for an A2L file
  cursor curSdomPverIdForPVER(  pPverName     IN VARCHAR2
                              , pPverVariant  IN VARCHAR2
                              , pPverRevision IN NUMBER
                             ) is
    select VERS_NUMMER
      from MV_SDOM_PVER
     where EL_NAME  = upper(pPverName)
       and VARIANTE = pPverVariant
       and REVISION = pPverRevision
  ;  

  -- get the elements of an PVER
  -- the PVER ID must have been set before using RB_WEB_P_CORE_F.SET_ROOT(pverID)
  cursor curPverElements is
    select 
            TREE_LEVEL
          , CLASS 
          , NAME
          , VARIANT
          , VERSION_ID
          , REVISION
          , ELEMENT_DESCRIPTION
          , LIFECYCLE_STATE
          , USE_STATE
      from RB_WEB_V_VERSION_USAGE_TREE@SCMPRO.WORLD@HEF2FE_RO
     where CLASS IN ('BC', 'BC-MO', 'BX', 'FC', 'FX')
       and USE_STATE = 'ENABLED'
     order by POS
  ;

  -- the PVER ID
  pverID number;

  -- the current BC ID
  vBcID number;

  -- number of BCs in the PVER  
  numBC number := 0;

  -- number of FCs in the PVER  
  numFC number := 0;

  -- number of other classes in the PVER  
  numOther number := 0;

  -- a temporary return code for function calls
  returnCode number;

  -- FOR TESTING ONLY
  -- pPverName VARCHAR2(255) := 'D17525V71C001';
  -- pPverVariant VARCHAR2(255) := 'M29D63';
  -- pPverRevision NUMBER := 0;

begin

  -- get the SDOM PVER ID
  open curSdomPverIdForPVER(pPverName, pPverVariant, pPverRevision);
  fetch curSdomPverIdForPVER into pverID;
  close curSdomPverIdForPVER; 

  -- dbms_output.put_line ('PVER Name    : ' || pPverName);
  -- dbms_output.put_line ('PVER Variant : ' || pPverVariant);
  -- dbms_output.put_line ('PVER Revision: ' || pPverRevision);
  -- dbms_output.put_line ('PVER ID      : ' || pverID);

  if pverID IS NULL then
    RAISE_APPLICATION_ERROR(-20100, 'Error message: '|| 'PVER not found in SDOM');
  end if;

  -- initialize SDOM session
  returnCode := RB_WEB_P_CORE_F.INIT_SESSION@SCMPRO.WORLD@HEF2FE_RO('ICDM_GetBCInfo', '', 1, 0, 0, 0);
  if returnCode != 1 then
    RAISE_APPLICATION_ERROR(-20100, 'Error message: '|| RB_WEB_P_CORE_F.GET_ERROR_MESSAGE@SCMPRO.WORLD@HEF2FE_RO(returnCode));
  end if;

  -- set the root object ID
  returnCode := RB_WEB_P_CORE_F.SET_ROOT@SCMPRO.WORLD@HEF2FE_RO(pverID);
  if returnCode != 1 then
    RAISE_APPLICATION_ERROR(-20100, 'Error message: '|| RB_WEB_P_CORE_F.GET_ERROR_MESSAGE@SCMPRO.WORLD@HEF2FE_RO(returnCode));
  end if;

  insert into GTT_EASEE_ELEMENTS
    (ID, ELEMENT_CLASS, ELEMENT_NAME, ELEMENT_VARIANT, ELEMENT_REVISION, CONTAINER_ID)
  values
    (  pverID
     , 'PVER'
     , pPverName
     , pPverVariant
     , pPverRevision
     , null
    )
  ;  

  vBcID := null;

  -- loop over all FCs (including Fx) and BCs (including Bxx)
  for pverElement in curPverElements loop
    -- dbms_output.put_line (pverElement.TREE_LEVEL || ' ' || pverElement.CLASS || ' '  || pverElement.NAME || ' '  || pverElement.VARIANT || ' '  || pverElement.REVISION || ' ' || pverElement.USE_STATE);

    if (pverElement.CLASS like 'B%') then
      numBC := numBC + 1;

      vBcID := pverElement.VERSION_ID;

      insert into GTT_EASEE_ELEMENTS
        (ID, ELEMENT_CLASS, ELEMENT_NAME, ELEMENT_VARIANT, ELEMENT_REVISION, CONTAINER_ID)
      values
        (  pverElement.VERSION_ID
         , pverElement.CLASS
         , pverElement.NAME
         , pverElement.VARIANT
         , pverElement.REVISION
         , pverID
        )
      ;  

    elsif ((pverElement.CLASS like 'F%') AND (vBCID is not NULL)) then
      numFC := numFC + 1;

      insert into GTT_EASEE_ELEMENTS
        (ID, ELEMENT_CLASS, ELEMENT_NAME, ELEMENT_VARIANT, ELEMENT_REVISION, CONTAINER_ID)
      values
        (  pverElement.VERSION_ID
         , pverElement.CLASS
         , pverElement.NAME
         , pverElement.VARIANT
         , pverElement.REVISION
         , vBcID
        )
      ;  
    else
      numOther := numOther + 1;
    end if;

  end loop;

  -- dbms_output.put_line ('BCs: ' || numBC || '  FCs: '  || numFC || '  other classes: '  || numOther);

  -- return numBC;
end;
/
