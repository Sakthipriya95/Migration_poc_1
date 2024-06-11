--------------------------------------------------------
-- Copyright (c) Robert Bosch GmbH. All rights reserved.
--
--
--  DDL for Package Body PK_GROUP2PAL
--------------------------------------------------------

create or replace PACKAGE BODY PK_GROUP2PAL AS

  --
  -- Get a new ID from iCDM general IDs Sequence
  --
  function getID return number is
      cursor curGetID is
      select SeqV_Attributes.nextval
       from dual
    ;
      
    vID number;
  begin
    open curGetID();
    fetch curGetID into vID;
    close curGetID;

    return vID;
  end ;

  function getPidcA2L(pPidcA2LID in number) return t_pidc_a2l%ROWTYPE is

    -- get PIDC A2L information
    cursor curGetPidcA2L(pPidcA2LID IN NUMBER) is
      select *
        from t_pidc_a2l
       where pidc_a2l_id = pPidcA2LID
    ;
    
    vPidcA2L curGetPidcA2L%ROWTYPE;
  begin
    open curGetPidcA2L(pPidcA2LID);
    fetch curGetPidcA2L into vPidcA2L;
    close curGetPidcA2L;
  
    return vPidcA2L;
  end getPidcA2L;

  --
  -- Get the A2L_RESP_ID for a particular ALIAS_NAME (pRespAlias) having a particular RESP_TYPE (pRespType)
  -- If it's not existing, it will be created.
  --
  -- If pRespAlias is null, alias_name will be null
  --
  function getA2lRespID(pPidcID in number, pRespType in varchar2, pRespAlias in varchar2, pCreUser in varchar2) return number is
  
    cursor getA2lRespID (pPidcID in number, pRespType in varchar2,pRespAlias in varchar2) is
      select a2l_resp_id
        from t_a2l_responsibility
       where project_id = pPidcID
         and resp_type = pRespType
         -- if pRespAlias is null, alias name should be null
         and alias_name = pRespAlias
         -- of pRespAlias is null, user_id should be null
         and ((nvl(pRespAlias, 0) = nvl(user_id, 0)) or pRespAlias IS NOT NULL)
    ;     

    vPidcWpRespID number := 0;
  begin
  
    open getA2lRespID(pPidcID, pRespType, pRespAlias);
    fetch getA2lRespID into vPidcWpRespID;
    
    if (getA2lRespID%NOTFOUND) then
      -- create PIDC Resp
      vPidcWpRespID := getID();
      
      dbms_output.put_line('vPidcWpRespID: ' || vPidcWpRespID || ' - ' || pRespType || ' - ' || pRespAlias);

      insert into t_a2l_responsibility
        (A2L_RESP_ID, PROJECT_ID, ALIAS_NAME, RESP_TYPE, CREATED_USER)
      values
        (vPidcWpRespID, pPidcID, pRespAlias, pRespType, pCreUser)
      ;  
      
    end if;
    
    close getA2lRespID;
  
    return vPidcWpRespID;
  end getA2lRespID;

  function getDefaultA2lRespID(pPidcID in number, pCreUser in varchar2) return number is
  
  begin
    return getA2lRespID(pPidcID, 'R', 'RB', pCreUser);
  end getDefaultA2lRespID;

  --
  -- Transfer A2L GROUP workpackes and responsibilities to PAL workpackages
  --
  procedure TransferGroup2PalWP(pPidcA2LID IN NUMBER, pCreUser VARCHAR2) AS
  begin
    -- transfer Workpackages
    pk_group2pal.CreatePalWP(pPidcA2LID, pCreUser);
    -- transfer responsibilities
    pk_group2pal.CreatePalRESP(pPidcA2LID, pCreUser);
    -- merge parameter responsibilities to WP responsibilities if they are unique for the complete WP
    pk_group2pal.MoveUniqueResp(pPidcA2LID, pCreUser);
  end TransferGroup2PalWP;

  --
  -- Delete existing PAL workpackages and parameter mappings
  -- - all versions and variant group specific settings will be deleted
  -- - delete will be done only if there is a single WP with a single RESP (default situation)
  --
  procedure DeletePalWP(pPidcA2LID IN NUMBER) AS
  
    -- get the WpDefinitionVersion 
    cursor curGetWpDefnVers(pPidcA2LID IN NUMBER) is
      select *
        from T_A2L_WP_DEFN_VERSIONS
       where pidc_a2l_id = pPidcA2LID
    ;
    
    vPidcA2L t_pidc_a2l%ROWTYPE;
  begin

    -- get the PIDC_A2L details
    vPidcA2L := getPidcA2L(pPidcA2LID);
    
    for vWpDefnVers in curGetWpDefnVers(pPidcA2LID) loop
      dbms_output.put_line('Deleting PAL WP version number ' || vWpDefnVers.version_number || ' ...');
      dbms_output.put_line('  WP Definition ID: ' || vWpDefnVers.wp_defn_vers_id);
    
      delete from t_a2l_wp_param_mapping
       where wp_resp_id in (select wp_resp_id from t_a2l_wp_responsibility where wp_defn_vers_id = vWpDefnVers.wp_defn_vers_id)
      ;
        
      delete from t_a2l_wp_responsibility
       where wp_defn_vers_id = vWpDefnVers.wp_defn_vers_id
      ;
        
      delete from t_a2l_wp_defn_versions
       where wp_defn_vers_id = vWpDefnVers.wp_defn_vers_id
      ;
    
    end loop;
    
    update t_pidc_a2l
       set wp_param_present_flag = 'N',
       active_wp_param_present_flag = 'N'
     where pidc_a2l_id = vPidcA2L.pidc_a2l_id
    ; 
    
    dbms_output.put_line('PAL WP deleted!');

   end DeletePalWP;

  --
  -- Create new _DEFAULT_WP_
  -- - map all parameter based on A2L file info to _DEFAULT_WP_
  --
  procedure CreateDefaultPalWP(pPidcA2LID IN NUMBER, pCreUser VARCHAR2) AS
  
    cursor curGetWpDefnVersID (pPidcA2lID in number) is
      select wp_defn_vers_id
        from t_a2l_wp_defn_versions
       where pidc_a2l_id = pPidcA2lID
         and version_number = 0
    ;     

    cursor curPidcWpRespID (pPidcID in number, pAliasName in varchar2) is
      select a2l_resp_id
        from t_a2l_responsibility
       where project_id = pPidcID
         and alias_name = pAliasName
    ;     

    vPidcA2L t_pidc_a2l%ROWTYPE;
    vWpDefnVersID number;
    vDefaultWpRespID number;
    vDefaultWpID number;
    vBoschRespID number;
    vWorkpackageId number;
    
    
  begin 
    -- get WP-Definition
    open curGetWpDefnVersID (pPidcA2LID);
    fetch curGetWpDefnVersID into vWpDefnVersID;
    if (curGetWpDefnVersID%NOTFOUND) then
      dbms_output.put_line('WP Definition not found.');
    else  
      dbms_output.put_line('WP Definition still existing - ID: ' || vWpDefnVersID);
      close curGetWpDefnVersID;
      return;
    end if;
    close curGetWpDefnVersID;
    vPidcA2L := getPidcA2L(pPidcA2LID);
    
    vWpDefnVersID := getID();
    vDefaultWpID := getID();
    vDefaultWpRespID := getID();
    
    -- get / create default responsibility for Robert Bosch without ALIAS_NAME
    vBoschRespID := getDefaultA2lRespID(vPidcA2L.Project_ID, pCreUser);
    
    dbms_output.put_line('            PIDC_ID: ' || vPidcA2L.Project_ID);
    dbms_output.put_line('    WP_DEFN_VERS_ID: ' || vWpDefnVersID);
    dbms_output.put_line('      DEFAULT_WP_ID: ' || vDefaultWpID);
    dbms_output.put_line('      BOSCH_RESP_ID: ' || vBoschRespID);
    dbms_output.put_line(' DEFAULT_WP_RESP_ID: ' || vDefaultWpRespID);
    
    INSERT INTO T_A2L_WP_DEFN_VERSIONS 
      (  WP_DEFN_VERS_ID
       , CREATED_USER
       , IS_ACTIVE
       , PARAM_LVL_CHG_ALLOWD_FLAG
       , VERSION_DESC
       , VERSION_NAME
       , VERSION_NUMBER
       , PIDC_A2L_ID
      ) 
      VALUES 
        (  vWpDefnVersID
         , pCreUser
         , 'N'
         , 'N'
         , 'Working Set'
         , 'Working Set'
         , 0
         , pPidcA2LID
        )
    ;
    SELECT A2L_WP_ID INTO vWorkpackageId from t_a2l_work_packages 
                                        where pidc_vers_id = vPidcA2L.PIDC_VERS_ID
                                          and WP_NAME = cDefaultWpName
                                        ;
                                            EXCEPTION
      WHEN NO_DATA_FOUND THEN
        vWorkpackageId := NULL;
     DBMS_OUTPUT.PUT_LINE(vWorkpackageId);
    if vWorkpackageId is null then
      INSERT INTO T_A2L_WORK_PACKAGES(A2L_WP_ID, WP_NAME, WP_DESC, PIDC_VERS_ID,CREATED_USER) 
      VALUES
      (vDefaultWpID, cDefaultWpName, 'Default Workpackage created by iCDM'
         ,vPidcA2L.PIDC_VERS_ID,pCreUser);
    ELSE
      vDefaultWpID := vWorkpackageId;
      dbms_output.put_line('    DEFAULT_WP_ID: ' || vDefaultWpID);
    END IF;  

    INSERT INTO T_A2L_WP_RESPONSIBILITY 
      (  WP_RESP_ID
       , CREATED_USER
       , A2L_WP_ID
       , A2L_RESP_ID
       , WP_DEFN_VERS_ID
      ) 
      VALUES 
        (  vDefaultWpRespID
         , pCreUser
         , vDefaultWpID
         , vBoschRespID
         , vWpDefnVersID
        )
     ;
    INSERT into T_A2L_WP_PARAM_MAPPING 
      (  PARAM_ID
       , WP_RESP_ID
       , CREATED_USER
      )           
        SELECT param.id as PARAM_ID
             , vDefaultWpRespID as WP_RESP_ID
             , pCreUser as CREATED_USER
          FROM T_PARAMETER param
             , TA2L_FILEINFO a2lfile
             , TA2L_MODULES a2lmodule
             , TA2L_CHARACTERISTICS a2lparam
         WHERE a2lfile.id = a2lmodule.FILE_ID
           AND a2lmodule.MODULE_ID = a2lparam.MODULE_ID
           AND a2lparam.name = param.name
           AND a2lparam.DTYPE = param.PTYPE
           AND a2lfile.id = vPidcA2L.A2L_FILE_ID
    ;
    update t_pidc_a2l
       set WP_PARAM_PRESENT_FLAG = 'Y'
         , IS_WORKING_SET_MODIFIED = 'Y'
     where pidc_a2l_id = vPidcA2L.pidc_a2l_id
    ; 
  end CreateDefaultPalWP;

  --
  -- create PAL WP based on GROUP WP
  --
  procedure CreatePalWP(pPidcA2LID IN NUMBER, pCreUser VARCHAR2) AS

      cursor curGetWpDefnVersID (pPidcA2lID in number) is
        select wp_defn_vers_id
          from t_a2l_wp_defn_versions
         where pidc_a2l_id = pPidcA2lID
           and version_number = 0
      ;     
      
      cursor curGetDefaultWpRespID (pDefnVersID in number) is
        select WP_RESP.wp_resp_id
          from T_A2L_WP_RESPONSIBILITY WP_RESP
             , T_A2L_WORK_PACKAGES     WP
         where WP_RESP.A2L_WP_ID = WP.A2L_WP_ID 
           and WP_RESP.wp_defn_vers_id = pDefnVersID 
           and WP.wp_name = cDefaultWpName
           and WP_RESP.A2L_VAR_GRP_ID IS NULL
      ;     
      
      cursor curGetNumWp (pDefnVersID in number) is
        select count(wp_resp_id)
          from T_A2L_WP_RESPONSIBILITY WP_RESP
         where WP_RESP.wp_defn_vers_id = pDefnVersID 
      ;     
      
      -- get the GROUP structure for a particular root (WP or RESP)
      cursor curGetParamMappings (pPidcA2lID IN number, pWpRootID IN number) is
        select gpar.param_id
             , agrp.grp_name
             , agrp.grp_long_name
             , gresp.resp_id
             , wresp.resp_name
             , pa2l.project_id
          from t_pidc_a2l            pa2l
             , t_a2l_group           agrp
             , t_a2l_grp_param       gpar
             , t_a2l_wp_resp         gresp
             , t_wp_resp             wresp
             , t_a2l_resp            aresp
         where pa2l.pidc_a2l_id      = pPidcA2lID
           and pa2l.a2l_file_id      = agrp.a2l_id
           and agrp.wp_root_id       = pWpRootID
           and agrp.group_id         = gpar.group_id
           and gresp.a2l_group_id    = agrp.group_id
           and gresp.resp_id         = wresp.resp_id
           and gresp.a2l_resp_id     = aresp.a2l_resp_id
           and aresp.pidc_a2l_id     = pPidcA2lID
      ;
      
      vDefnVersID number;
      vWpRespID number;
      vA2lRespID number;
      vDefaultWpRespID number;
      
      vNumParameter number := 0;
      vNumWP number := 0;
      vPidcA2l t_pidc_a2l%ROWTYPE;
      vWorkpackageId number;
      vParamMappings curGetParamMappings%ROWTYPE;
      
      --
      -- get or create the WP (T_A2L_WP_RESPONSIBILITY)
      --
      function getWpRespID(pDefnVersID in number, pGrpName in varchar2, pGrpLongName in varchar2, pPidcWpRespID in number) return number is
      
        cursor curGetWpRespID (pDefnVersID in number, pGrpName in varchar2) is
          select wp_resp_id
            from t_a2l_wp_responsibility wp_resp, t_a2l_work_packages wp
           where wp_resp.wp_defn_vers_id = pDefnVersID 
             and wp_resp.a2l_wp_id = wp.a2l_wp_id 
             and wp.wp_name = pGrpName
        ;     
    
        cursor curGetPidcWp (pPidcVersId in number, pWpName in Varchar2) is
          SELECT A2L_WP_ID 
            from t_a2l_work_packages 
           where pidc_vers_id = vPidcA2L.PIDC_VERS_ID
             and wp_name = pGrpName
        ;
        
        vWpRespID number := 0;
        
      begin
        vPidcA2L := getPidcA2L(pPidcA2LID);
        open curGetWpRespID(pDefnVersID, pGrpName);
        fetch curGetWpRespID into vWpRespID;
        
        if (curGetWpRespID%NOTFOUND) then
          -- create WP
          vWpRespID := getID();
          
          open curGetPidcWp (vPidcA2L.PIDC_VERS_ID, pGrpName);
          fetch curGetPidcWp into vWorkpackageId;

          if (curGetPidcWp%NOTFOUND) then
            -- get a new WP ID
            vWorkpackageId := getID();
              
            INSERT INTO T_A2L_WORK_PACKAGES(A2L_WP_ID,WP_NAME, WP_DESC,PIDC_VERS_ID,CREATED_USER) 
              VALUES (vWorkpackageId, pGrpName, pGrpLongName, vPidcA2l.pidc_vers_id, pCreUser);
          end if;
          
          close curGetPidcWp;
          
          insert into T_A2L_WP_RESPONSIBILITY
            (WP_RESP_ID, WP_DEFN_VERS_ID, A2L_WP_ID, A2L_RESP_ID, CREATED_USER)
          values
            (vWpRespID, pDefnVersID, vWorkpackageId, pPidcWpRespID, pCreUser)
          ;  
          
          vNumWP := vNumWP + 1;
        
        end if;
        
        close curGetWpRespID;
      
        return vWpRespID;
      end;
      
    begin
      -- get WP-Definition
      open curGetWpDefnVersID (pPidcA2LID);
      fetch curGetWpDefnVersID into vDefnVersID;
      if (curGetWpDefnVersID%NOTFOUND) then
        dbms_output.put_line('WP Definition not found!');
        close curGetWpDefnVersID;
        return;
      else  
        dbms_output.put_line('WP Definition ID: ' || vDefnVersID);
      end if;
      close curGetWpDefnVersID;
    
      -- get default WP
      open curGetDefaultWpRespID(vDefnVersID);
      fetch curGetDefaultWpRespID into vDefaultWpRespID;
      if (curGetDefaultWpRespID%NOTFOUND) then
        dbms_output.put_line('no ' || cDefaultWpName || ' found!');
        close curGetDefaultWpRespID;
        return;
      end if;
      close curGetDefaultWpRespID;
    
      -- get number of WP, if more than one existing (the default), don't transfer WP
      open curGetNumWp(vDefnVersID);
      fetch curGetNumWp into vNumWP;
      if (curGetNumWp%NOTFOUND) then
        dbms_output.put_line('no ' || cDefaultWpName || ' found!');
        close curGetNumWp;
        return;
      elsif (vNumWP > 1)then  
        dbms_output.put_line(vNumWP || ' WP still defined, transfer aborted!');
        close curGetNumWp;
        return;
      end if;
      close curGetNumWp;
      
      vNumWP := 0;
     
      -- create WP and paramter
      for vParamMappings in curGetParamMappings(pPidcA2LID, cWpRootWP_ID) loop
   
        -- A2L_GRP tables are using only default responsibilities (R, C, O), alias name for them needs to be null
        vA2lRespID := getA2lRespID(vParamMappings.project_id, vParamMappings.resp_name, 'RB', pCreUser);
  
        vWpRespID := getWpRespID(vDefnVersID, vParamMappings.grp_name, vParamMappings.grp_long_name, vA2lRespID);
      
        update t_a2l_wp_param_mapping
           set WP_RESP_ID = vWpRespID
             , modified_user = pCreUser
         where param_id = vParamMappings.param_ID   
           and wp_resp_id in (select wp_resp_id from t_a2l_wp_responsibility where WP_DEFN_VERS_ID = vDefnVersID)
           and WP_RESP_ID = vDefaultWpRespID
        ;   
      
        vNumParameter := vNumParameter + 1;
      end loop;
      
      -- set the WP definition (the working set) as modified      
      if (vNumParameter > 0) then
        update t_pidc_a2l
         set IS_WORKING_SET_MODIFIED = 'Y'
         where pidc_a2l_id = pPidcA2LID
           and IS_WORKING_SET_MODIFIED = 'N'
         ;
      end if;

      dbms_output.put_line('       WP_DEFN_VERS_ID: ' || vDefnVersID);
      dbms_output.put_line('num Parameter assinged: ' || vNumParameter);
      dbms_output.put_line('        num WP created: ' || vNumWP);
  END CreatePalWP;

  --
  -- Transfer responsibilities from GROUPs to PAL WP
  --
  -- Responsibilities will be set per parameter since it's defined per Parameter in GROUPs
  -- Responsibility will only be set if the parameter has default responsibility or no responsibility
  --
  procedure CreatePalRESP(pPidcA2LID IN NUMBER, pCreUser VARCHAR2) AS

      cursor curGetRespGroups (pPidcA2lID IN number, pWpRootID IN number) is
        select agrp.grp_name
             , agrp.grp_long_name
             , pa2l.project_ID
          from t_pidc_a2l            pa2l
             , t_a2l_group           agrp
         where pa2l.pidc_a2l_id      = pPidcA2lID
           and pa2l.a2l_file_id      = agrp.a2l_id
           and agrp.wp_root_id       = pWpRootID
      ;
      
      cursor curGetPidcResp (pAliasName in varchar2, pPidcA2lID in number) is
        select wpresp.a2l_resp_id
             , pa2l.project_id
          from t_a2l_responsibility   wpresp
             , t_pidc_a2l       pa2l
         where alias_name       = pAliasName
           and wpresp.project_id          = pa2l.project_id
           and pa2l.pidc_a2l_id = pPidcA2lID
      ;
      
      cursor curGetWpDefnVersID (pPidcA2lID in number) is
        select wp_defn_vers_id
          from t_a2l_wp_defn_versions
         where pidc_a2l_id = pPidcA2lID
           and version_number = 0
      ;     
      
      cursor curGetParamMappings (pPidcA2lID IN number, pWpRootID IN number) is
        select gpar.param_id
             , agrp.grp_name
             , agrp.grp_long_name
             , gresp.l_last_name
             , gresp.l_first_name
             , gresp.l_department
             , gresp.user_id
             , gresp.resp_type
             , gresp.a2l_resp_id
          from t_pidc_a2l            pa2l
             , t_a2l_group           agrp
             , t_a2l_grp_param       gpar
             , t_a2l_responsibility  gresp
         where pa2l.pidc_a2l_id      = pPidcA2lID
           and pa2l.a2l_file_id      = agrp.a2l_id
           and agrp.wp_root_id       = pWpRootID
           and agrp.group_id         = gpar.group_id
           and gresp.alias_Name      = agrp.grp_name
           and gresp.project_id      = pa2l.project_id
      ;
      
      cursor curGetParamA2lRespID (pWpDefnVersID in number, pParamID in number) is
        select pmap.wp_param_map_id
             , pmap.param_id
             , pmap.wp_resp_id
             , nvl(pmap.par_a2l_resp_id, 0) nvl_par_a2l_resp_id
          from t_a2l_wp_param_mapping  pmap
             , t_a2l_wp_responsibility wpr
         where wpr.wp_defn_vers_id = pWpDefnVersID
           and pmap.wp_resp_id     = wpr.wp_resp_id
           and pmap.param_id       = pParamID
      ;     
      
      vDefnVersID number;
      vPidcID number;
      vDefaultA2LRespID number;
      
      vNumResponsibilities number := 0;
      vNumNewResponsibilities number := 0;
      vNumChangedResp number := 0;
      vNumNotChangedResp number := 0;
      
      vParamMappings curGetParamMappings%ROWTYPE;
      vParamResp curGetParamA2lRespID%ROWTYPE;
      vRespGroup curGetRespGroups%ROWTYPE;
      vPidcResp curGetPidcResp%ROWTYPE;
      vPidcA2L t_pidc_a2l%ROWTYPE;
      
    begin
      open curGetWpDefnVersID (pPidcA2LID);
      fetch curGetWpDefnVersID into vDefnVersID;
      if (curGetWpDefnVersID%NOTFOUND) then
        dbms_output.put_line('WP Definition not found!');
        close curGetWpDefnVersID;
        return;
      else  
        dbms_output.put_line('WP Definition ID: ' || vDefnVersID);
      end if;
      close curGetWpDefnVersID;

      vPidcA2L := getPidcA2L(pPidcA2LID);
      vPidcID := vPidcA2L.PROJECT_ID;
    
      -- create Responsibilities
      for vRespGroup in curGetRespGroups(pPidcA2LID, cWpRootWP_ID) loop
      
        open curGetPidcResp (vRespGroup.grp_name, pPidcA2LID);
        fetch curGetPidcResp into vPidcResp;
        if (curGetPidcResp%NOTFOUND) then
          insert into t_a2l_responsibility
            (PROJECT_ID, ALIAS_NAME, RESP_TYPE, CREATED_USER)
          values
            (vRespGroup.project_ID, vRespGroup.grp_name, cRespTypeBoschID, pCreUser)
          ;  
          
          vNumNewResponsibilities := vNumNewResponsibilities + 1;
        end if;
        close curGetPidcResp;
        
        vNumResponsibilities := vNumResponsibilities + 1;
      end loop;
    
      vDefaultA2LRespID := getDefaultA2lRespID(vPidcID, pCreUser);
      -- map Responsibility to parameter
      for vParamMappings in curGetParamMappings(pPidcA2LID, cWpRootRESP_ID) loop
        open curGetParamA2lRespID(vDefnVersID, vParamMappings.param_id);
        fetch curGetParamA2lRespID into vParamResp;
        
        if (((vParamResp.nvl_par_a2l_resp_id = vDefaultA2LRespID) OR (vParamResp.nvl_par_a2l_resp_id = 0))
            AND (curGetParamA2lRespID%ROWCOUNT = 1)) then
          -- current resp is default
          update t_a2l_wp_param_mapping
             set par_a2l_resp_id = vParamMappings.a2l_resp_id
               , WP_RESP_INHERIT_FLAG = 'N'
               , modified_user = pCreUser
           where wp_resp_id in (select wp_resp_id from t_a2l_wp_responsibility where WP_DEFN_VERS_ID = vDefnVersID)
             and param_id = vParamMappings.param_id
          ; 

          vNumChangedResp := vNumChangedResp + 1;
        else
          if (curGetParamA2lRespID%ROWCOUNT != 1) then
            dbms_output.put_line('parameter has multiple responsibilities: PARAMETER_ID = ' || vParamMappings.param_id || ' : ' || curGetParamA2lRespID%ROWCOUNT);
          end if;
        
          -- current resp is not default, don't change it
          vNumNotChangedResp := vNumNotChangedResp + 1;
        end if;
        
        close curGetParamA2lRespID;
        
      end loop;

      -- set the WP definition (the working set) as modified      
      if (vNumChangedResp > 0) then
        update t_pidc_a2l
         set IS_WORKING_SET_MODIFIED = 'Y'
         where pidc_a2l_id = pPidcA2LID
           and IS_WORKING_SET_MODIFIED = 'N'
         ;   
      end if;
    
      dbms_output.put_line('        WP found, WP_DEFN_VERS_ID: ' || vDefnVersID);
      dbms_output.put_line('                          PIDC_ID: ' || vPidcID);
      dbms_output.put_line('             num Responsibilities: ' || vNumResponsibilities);
      dbms_output.put_line('         num New Responsibilities: ' || vNumNewResponsibilities);
      dbms_output.put_line('     num changed Responsibilities: ' || vNumChangedResp);
      dbms_output.put_line(' num NOT changed Responsibilities: ' || vNumNotChangedResp);
  END CreatePalRESP;

  --
  --
  --
  procedure MoveUniqueRESP(pPidcA2LID IN NUMBER, pModUser VARCHAR2) AS
      cursor curUniqueResp (pDefnVersID in number) is
        select wp_resp_id
             , a2l_wp_id
             , count(par_a2l_resp_id)
          from (    
                select wppal.wp_resp_id
                     , wppal.a2l_wp_id
                     , wppar.par_a2l_resp_id
                     , count(wppar.WP_PARAM_MAP_ID)  as NUM_PARAMETER
                  from t_a2l_wp_responsibility          wppal
                     , t_a2l_wp_param_mapping     wppar
                     , t_a2l_work_packages wp
                 where wppal.wp_defn_vers_id     = pDefnVersID
                 and wp.a2l_wp_id = wppal.a2l_wp_id
                   and wppal.wp_resp_id          = wppar.wp_resp_id
                 group by wppal.wp_resp_id
                        , wppal.a2l_wp_id  
                        , wppar.par_a2l_resp_id
                )        
         group by wp_resp_id
                , a2l_wp_id
         having count(par_a2l_resp_id) = 1
      ;
      
      cursor curParResp (pWpID IN NUMBER) is
        select par_a2l_resp_id
             , presp.alias_name
             , presp.resp_type
          from t_a2l_wp_param_mapping  pmap
             , t_a2l_responsibility          presp
         where wp_resp_id = pWpID
           and pmap.par_a2l_resp_id = presp.a2l_resp_id
           and rownum = 1
      ;     
    
      cursor curGetWpDefnVersID (pPidcA2lID in number) is
        select wp_defn_vers_id
          from t_a2l_wp_defn_versions
         where pidc_a2l_id = pPidcA2lID
           and version_number = 0
      ;     
      
      uniqueRespWP curUniqueResp%ROWTYPE;
      
      parResp curParResp%ROWTYPE;
      
      vDefnVersID number;
      
      -- number of moved responsibilities
      vNumMovedResp number := 0;
    begin
    
      open curGetWpDefnVersID (pPidcA2LID);
      fetch curGetWpDefnVersID into vDefnVersID;
      if (curGetWpDefnVersID%NOTFOUND) then
        dbms_output.put_line('WP Definition not found!');
        return;
      end if;
        dbms_output.put_line('WP Definition found: '|| vDefnVersID);
      close curGetWpDefnVersID;
    
    
      for uniqueRespWP in curUniqueResp(vDefnVersID) loop
        open curParResp(uniqueRespWP.wp_resp_id);
        fetch curParResp into parResp;
        close curParResp;
      
        update T_A2L_WP_RESPONSIBILITY
           set A2L_RESP_ID = parResp.par_a2l_resp_id
             , modified_user = pModUser
         where wp_resp_id = uniqueRespWP.wp_resp_id
        ; 
      
        update t_a2l_wp_param_mapping
           set WP_RESP_INHERIT_FLAG = 'Y'
             , modified_user = pModUser
         where wp_resp_id = uniqueRespWP.wp_resp_id
        ; 
      
        vNumMovedResp := vNumMovedResp + 1;
      
        dbms_output.put_line (uniqueRespWP.a2l_wp_id || ' - ' || parResp.resp_type || ' - ' || parResp.alias_name);
        
      end loop;
      
      -- set the WP definition (the working set) as modified      
      if (vNumMovedResp > 0) then
        update t_pidc_a2l
         set IS_WORKING_SET_MODIFIED = 'Y'
         where pidc_a2l_id = pPidcA2LID
           and IS_WORKING_SET_MODIFIED = 'N'
         ;   
      end if;

      dbms_output.put_line('    WP found, WP_DEFN_VERS_ID: ' || vDefnVersID);
      dbms_output.put_line('   num moved Responsibilities: ' || vNumMovedResp);

  END MoveUniqueRESP;

END PK_GROUP2PAL;

/
