CREATE GLOBAL TEMPORARY TABLE temp_labels (
seq_id number,
node_id     number,
label      VARCHAR2(255),
upper_label varchar2(255),
unit   VARCHAR2(30))
ON COMMIT DELETE ROWS;

create public synonym temp_labels for k5esk_ldb2.temp_labels;

grant select,insert,delete,update on temp_labels to ldb2_user;


CREATE OR REPLACE PROCEDURE CREATE_UPDATED_LBLLIST 
(
  P_NODE_ID IN NUMBER  
, P_USERNAME IN VARCHAR2  
, P_RESULT OUT NUMBER  
) AS 
cursor cur_label is
select * from temp_labels;

cursor cur_label_list(p_pro_rev_id  number) is
select * from v_ldb2_project_labels where pro_rev_id=p_pro_rev_id;

n_count number:=0;
n_pro_rev_id number:=0;
n_vers_id number:=0;
n_max_rev_id number:=0;
n_new_pro_rev_id number:=0;
n_scope number:=0;

BEGIN

select scope into n_scope from v_ldb2_object_tree where node_id=P_NODE_ID;

-- labellist cannot be created on other scopes
if(n_scope<>8) then
  P_RESULT:=-2;
  return;
end if;

SELECT vers_id into n_vers_id FROM V_Ldb2_Sw_Vers where 
         VILLA_SWVERS_ID = (select object_id from v_ldb2_object_tree where node_id= P_NODE_ID);

select pro_rev_id into n_pro_rev_id from V_LDB2_PROJECT_REVISION where vers_id=n_vers_id 
and rev_id=(select max(rev_id) from V_LDB2_PROJECT_REVISION  where vers_id=n_vers_id );

select count(1) into n_count from temp_labels where upper_label not in (select upper_label from v_ldb2_project_labels where pro_rev_id=n_pro_rev_id);

IF (n_count>0) then
-- to create a label list as new label is found
    SELECT COALESCE(MAX(rev_Id), 0) into n_max_rev_id from V_LDB2_PROJECT_REVISION
      where vers_id=n_vers_id;

    n_max_rev_id:= n_max_rev_id+1;

    IF (pkg_wrapper.insert_ssd_lock(n_vers_id,n_max_rev_id)=0) then
        -- lock not acquired
        P_RESULT:= -1;
        return;
    end if;

-- to mark any previous valid list as invalid
    update v_ldb2_project_revision set valid_list='N' where vers_id=n_vers_id;
    
-- select sequence value for pro_rev_id
  select seq_ldb2_all.nextval into n_new_pro_rev_id from dual;
-- insert a new revision
    insert into V_LDB2_PROJECT_REVISION values(n_new_pro_rev_id,n_vers_id,n_max_rev_id,'Rule import via icdm','New rule created by' || P_USERNAME,'Y','N');

-- insert labels in a new labellist
    insert into v_ldb2_project_labels(pro_rev_id,label,upper_label,unit,ctrl_fld) 
     select n_new_pro_rev_id, label,upper_label,unit,ctrl_fld from v_ldb2_project_labels where pro_rev_id=n_pro_rev_id;

-- insert new labels in the labellist
      insert into v_ldb2_project_labels(pro_rev_id,label,upper_label,unit,ctrl_fld) 
        select n_new_pro_rev_id,label,upper_label,unit,'N' from temp_labels where upper_label not in 
        (select upper_label from v_ldb2_project_labels where pro_rev_id=n_pro_rev_id);

else
    p_result:=0;
    return;
end if;
  
  P_RESULT:=1;
  
EXCEPTION
  WHEN OTHERS THEN
    P_RESULT:= -3;
END CREATE_UPDATED_LBLLIST;

create public synonym CREATE_UPDATED_LBLLIST for k5esk_ldb2.CREATE_UPDATED_LBLLIST;
grant execute on CREATE_UPDATED_LBLLIST to ldb2_user;





CREATE OR REPLACE PACKAGE pkg_villa_interface_ssdtest AS
  -- Global Variable to hold the Release ID
  G_ReleaseID NUMBER;

  -- If the Pl/sql table has been updated for the Global Release ID
  /*************************************************************************
  Purpose: For Villa to create the SSD file based on the given release id
  History: Sp,6-Aug-03,For Villa
         : FEDab33593, Add paramenters for filtering and Global bypass check
           PK, 09.03.2005, FEDab40788 CheckSSD file to have automatic comments generated
           SP,RCMS00047178 ,V4.5, for header information of full scope
           RB,23.08/07 RCMS00447599 Increase of SSD-Release-Config Levels from 3 to 5 levels
           SK RCMS00457889 Name of .ssd-file depends on selected usecases MFT, SSD, APP
  *****************************************************************************/
  /*To store the output in PL/SQL table*/
  TYPE Villa_SSD_File IS TABLE OF VARCHAR2(32767) INDEX BY BINARY_INTEGER;

  TYPE fea_val_array IS TABLE OF VARCHAR2(32000) INDEX BY BINARY_INTEGER;

  c_villa_data Villa_SSD_File;

  FUNCTION Data_Split(p_in_string VARCHAR2, p_delim VARCHAR2)
    RETURN fea_val_array;

  FUNCTION Get_Node_ID(pn_pro_rel_id NUMBER) RETURN NUMBER;

  FUNCTION Get_Rev_Description(pn_pro_rel_id NUMBER) RETURN VARCHAR2;

  FUNCTION Fnc_Get_Revision_Desc(pn_pro_rel_id NUMBER, pn_level NUMBER)
    RETURN VARCHAR2;

  FUNCTION Fnc_Get_Config_Details(pn_pro_rel_id NUMBER, pn_level NUMBER)
    RETURN VARCHAR2;

  FUNCTION Fnc_Level_Cnt(pn_pro_rel_id NUMBER, pn_level NUMBER) RETURN NUMBER;

  FUNCTION Fnc_Excel_Config_Desc(pn_pro_rel_id NUMBER,
                                 pn_level      NUMBER,
                                 pn_swposition NUMBER) RETURN VARCHAR2;

  FUNCTION Fnc_Excel_Rev_Desc(pn_pro_rel_id NUMBER,
                              pn_level      NUMBER,
                              pn_swposition NUMBER) RETURN VARCHAR2;

  FUNCTION CONFIG_RELEASE_CNT(Pn_pro_rel_id NUMBER) RETURN NUMBER;

  /*
    PK, 09.03.2005, FEDab40788
    Added parameter pb_comment to identify if advance formula should have comment
  */
  PROCEDURE SSD_File(pn_pro_rel_id NUMBER,
                     pc_ssd_data   OUT c_villa_data%TYPE,
                     pc_mft        VARCHAR2 DEFAULT 'MFT',
                     pc_ssd        VARCHAR2 DEFAULT 'SSD',
                     pc_appchK     VARCHAR2 DEFAULT 'App-Chk',
                     pc_comp       varchar2 default 'Cal4Comp',
                     pb_refresh    BOOLEAN DEFAULT TRUE,
                     pb_comment    BOOLEAN DEFAULT FALSE);

  -- Procedures for Test, Shankar
  PROCEDURE SetReleaseID(A_ReleaseID IN NUMBER);

  -- Procedures for Test, Shankar
  PROCEDURE CreateSSDFile(A_ReleaseID IN NUMBER,
                          A_SSDData   OUT c_villa_data%TYPE);

  FUNCTION GetSSDCase(pn_pro_rel_id IN NUMBER, pc_label VARCHAR2)
    RETURN VARCHAR2;

  FUNCTION GetECUType(pn_pro_rel_id NUMBER) RETURN VARCHAR2;

  FUNCTION SSD_File_Label(pn_pro_rel_id NUMBER,
                          pc_ssd_data   OUT c_villa_data%TYPE,
                          pc_mft        VARCHAR2 DEFAULT 'MFT',
                          pc_ssd        VARCHAR2 DEFAULT 'SSD',
                          pc_appchK     VARCHAR2 DEFAULT 'App-Chk',
                          pc_comp       VARCHAR2 DEFAULT 'Cal4Comp',
                          pb_temporary  BOOLEAN DEFAULT FALSE,
                          pb_refresh    BOOLEAN DEFAULT TRUE,
                          pb_comment    BOOLEAN DEFAULT FALSE) RETURN BOOLEAN;

  FUNCTION get_feature_values(pn_pro_rel_id NUMBER) RETURN VARCHAR2;

  /*  SK RCMS00457889 Added parameters for naming of .ssd-file on selected usecases MFT, SSD, APP*/
  FUNCTION GetSSDFilename(pn_pro_rel_id NUMBER,
                          p_MFT         VARCHAR2 DEFAULT 'MFT',
                          p_SSD         VARCHAR2 DEFAULT 'SSD',
                          p_APP_CHK     VARCHAR2 DEFAULT 'App-Chk')
    RETURN VARCHAR2;

  PROCEDURE SSD_File_ICDM(pc_ssd_data OUT c_villa_data%TYPE,
                          pb_node_id  number,
                          pb_comment  BOOLEAN DEFAULT FALSE);

  PROCEDURE SSD_File_curve_map(pc_ssd_data OUT c_villa_data%TYPE,
                               pb_node_id  number,
                               pb_comment  BOOLEAN DEFAULT FALSE);

  /*
    PK, 09.03.2005, FEDab40788
    Added to create ssd file with node details
  */
  PROCEDURE SSD_File_Node(pn_pro_rel_id NUMBER,
                          pc_ssd_data   OUT c_villa_data%TYPE,
                          pc_mft        VARCHAR2 DEFAULT 'MFT',
                          pc_ssd        VARCHAR2 DEFAULT 'SSD',
                          pc_appchK     VARCHAR2 DEFAULT 'App-Chk',
                          pc_comp       varchar2 default 'Cal4Comp',
                          pb_refresh    BOOLEAN DEFAULT TRUE,
                          pb_comment    BOOLEAN DEFAULT FALSE);

  FUNCTION SSD_File_Label_Node(pn_pro_rel_id NUMBER,
                               pc_ssd_data   OUT c_villa_data%TYPE,
                               pc_mft        VARCHAR2 DEFAULT 'MFT',
                               pc_ssd        VARCHAR2 DEFAULT 'SSD',
                               pc_appchK     VARCHAR2 DEFAULT 'App-Chk',
                               pc_comp       VARCHAR2 DEFAULT 'Cal4Comp',
                               pb_temporary  BOOLEAN DEFAULT FALSE,
                               pb_refresh    BOOLEAN DEFAULT TRUE,
                               pb_comment    BOOLEAN DEFAULT FALSE)
    RETURN BOOLEAN;
  PROCEDURE SSD_File_icdm_dependency(pc_ssd_data OUT c_villa_data%TYPE,
                               pb_node_id  number,
                               pb_comment  BOOLEAN DEFAULT FALSE);

END pkg_villa_interface_ssdtest;


/


CREATE OR REPLACE PACKAGE BODY pkg_villa_interface_ssdtest AS
  /**************************************************************************************************/
  FUNCTION Fnc_Get_Revision_Desc(pn_pro_rel_id NUMBER, pn_level NUMBER)
    RETURN VARCHAR2 IS
    /************************************************************************
    Purpose: To fetch the revision details based on the release id
    Parameters: In:Pn_pro_rel_id---Release id
                    Pn_level--------Level of the configuration
                  OUT:None
    Returns: Revision details
    History: Sp,6-Aug-03,For Villa
    *****************************************************************************/
    CURSOR Cur_Get_Revision IS
      SELECT rev_id || ' ' || description descr
        FROM v_ldb2_project_revision rev, v_ldb2_config_release con
       WHERE rev.pro_rev_id = con.sw_pro_rev_id
         AND con.pro_rel_id = pn_pro_rel_id
         AND con.sw_level = pn_level
       ORDER BY con.sw_level, con.sw_position;
    c_level_desc VARCHAR2(10000);
  BEGIN
    /*To concatinate the details from the cursor*/
    FOR val_rel_descr IN cur_get_revision LOOP
      IF c_level_desc IS NULL THEN
        c_level_desc := 'Level ' || pn_level || ': ' ||
                        replace(val_rel_descr.descr, chr(10), '');
      ELSE
        c_level_desc := c_level_desc || chr(10) || '!' || chr(9) || chr(9) ||
                        chr(9) || '  ' ||
                        replace(val_rel_descr.descr, chr(10), '');
      END IF;
    END LOOP;
    RETURN c_level_desc;
    /*to handle exceptions n to login to the error file.*/
  EXCEPTION
    WHEN OTHERS THEN
      Pkg_Error_Logging.RegisterError('Error occurred  from the function Fnc_Get_Revision_Desc',
                                      SQLERRM,
                                      USER,
                                      SYSDATE,
                                      'Pkg_Villa_Interface');
      Pkg_Error_Logging.SyncMessage;
      Raise_Application_Error(-20003,
                              'Unable to fetch the release details for pro_rel_id(' ||
                              pn_pro_rel_id || ') and level(' || pn_level ||
                              '). ' || sqlerrm);
  END Fnc_Get_Revision_Desc;
  FUNCTION Get_Node_ID(pn_pro_rel_id NUMBER) RETURN NUMBER IS
    /*************************************************************
    Purpose: To get the node id based on the release id
    Parameters: In:Pn_Pro_Rel_id--Release id
    Return: Node_id
    History: SP,25-9-2005,V4.5,RCMS00047178
    ***************************************************************/
    n_node V_Ldb2_Object_Tree.Node_id%TYPE;
  BEGIN
    SELECT node_id
      INTO n_node
      FROM v_ldb2_object_tree tree
     WHERE object_id = (SELECT vers.villa_swvers_id
                          FROM v_ldb2_sw_vers          vers,
                               v_ldb2_project_revision rev,
                               v_ldb2_project_release  rel
                         WHERE vers.vers_id = rev.vers_id
                           AND rev.pro_rev_id = rel.pro_rev_id
                           AND rel.pro_rel_id = pn_pro_rel_id);
    RETURN n_node;
  EXCEPTION
    WHEN Others THEN
      Pkg_Error_Logging.RegisterError('Error occurred  from the function Get_Node_Id',
                                      SQLERRM,
                                      USER,
                                      SYSDATE,
                                      'Pkg_Villa');
      Raise_Application_Error(-20002,
                              'Unable to fetch node_id for pro_rel_id(' ||
                              pn_pro_rel_id || ') ' || sqlerrm);
  END Get_Node_Id;
  /********************************************************************************************************/
  FUNCTION Get_Rev_Description(pn_pro_rel_id NUMBER) RETURN VARCHAR2 IS
    /*************************************************************
    Purpose: To get the revision description based on the release id
    Parameters: In:Pn_Pro_Rel_id--Release id
    Return: Rev id and revision description
    History: SP,25-9-2005,V4.5,RCMS00047178
    ***************************************************************/
    c_rev_description1 VARCHAR2(10000);
  BEGIN
    SELECT rev_id || ' ' || rev.description descr
      INTO c_rev_description1
      FROM v_ldb2_project_revision rev, v_ldb2_project_release con
     WHERE rev.pro_rev_id = con.pro_rev_id
       AND con.pro_rel_id = pn_pro_rel_id;
    RETURN c_rev_description1;
  EXCEPTION
    WHEN others THEN
      Pkg_Error_Logging.RegisterError('Error occurred  from the function Get_Rev_Description',
                                      SQLERRM,
                                      USER,
                                      SYSDATE,
                                      'Pkg_Villa');
      Raise_Application_Error(-20002,
                              'Unable to fetch revision description for pro_rel_id(' ||
                              pn_pro_rel_id || ') ' || sqlerrm);
  END Get_Rev_Description;
  /**********************************************************************************************************/
  FUNCTION Fnc_Get_Config_Details(pn_pro_rel_id NUMBER, pn_level NUMBER)
    RETURN VARCHAR2 IS
    /************************************************************************
    Purpose: To fetch the scope based on the release id
    Parameters:
      In:Pn_pro_rel_id---Release id
                   Pn_level--------Level of the configuration
      OUT:None
      Returns: Scope of the release
      History: Sp,6-Aug-03,For Villa
       RB,23.08/07 RCMS00447599 Increase of SSD-Release-Config Levels from 3 to 5 levels
    *****************************************************************************/
    /*To fetch the configured details*/
    CURSOR cur_config_des IS
      SELECT prj.name || '/' || sw.description descr,
             con.sw_level,
             con.sw_position
        FROM v_ldb2_villa_projects prj,
             v_ldb2_villa_swvers   sw,
             v_ldb2_object_tree    tree,
             v_ldb2_config_release con
       WHERE con.pro_rel_id = pn_pro_rel_id
         AND tree.node_id = con.sw_version_id
         AND tree.object_id = sw.id
         AND sw.fk_projectid = prj.id
         AND con.sw_level = pn_level
       ORDER BY con.sw_level, con.sw_position;
    c_level_desc VARCHAR2(10000);
    n_rel_cnt    NUMBER := 0;
  BEGIN
    /*Sp, to chk whether the release is a configured release or not*/
    SELECT count(*)
      INTO n_rel_cnt
      FROM v_ldb2_config_release
     WHERE pro_rel_id = pn_pro_rel_id;
    IF n_rel_cnt > 0 THEN
      /*To combine the values of release details from the cursor*/
      FOR val_config_des in cur_config_des LOOP
        IF c_level_desc IS NULL THEN
          c_level_desc := 'Level' || pn_level || ': ' ||
                          replace(val_config_des.descr, chr(10), '');
        ELSE
          c_level_desc := c_level_desc || chr(10) || '!' || chr(9) ||
                          chr(9) || chr(9) || '  ' ||
                          replace(val_config_des.descr, chr(10), '');
        END IF;
      END LOOP;
      IF c_level_desc IS NULL THEN
        c_level_desc := 'Level' || pn_level || ':';
      END IF;
    ELSIF n_rel_cnt = 0 AND pn_level = 1 THEN
      SELECT 'Level1' || ': ' || PROJ.name || '/' || VERS.name descr
        INTO c_level_desc
        FROM v_ldb2_project_release  REL,
             v_ldb2_project_revision REV,
             v_ldb2_sw_vers          VERS,
             v_ldb2_projects         PROJ
       WHERE rel.pro_rel_id = pn_pro_rel_id
         AND rel.pro_rev_id = rev.pro_rev_id
         AND rev.vers_id = vers.vers_id
         AND vers.id = proj.id;
    ELSIF n_rel_cnt = 0 AND pn_level != 1 THEN
      RETURN 'Level' || pn_level || ':';
    END IF;
    return c_level_desc;
    /*to handle exceptions n to login to the error file.*/
  EXCEPTION
    WHEN OTHERS THEN
      Pkg_Error_Logging.RegisterError('Error occurred  from the function Fnc_Get_Config_Details',
                                      SQLERRM,
                                      USER,
                                      SYSDATE,
                                      'Pkg_Villa');
      Raise_Application_Error(-20002,
                              'Unable to fetch the release details for pro_rel_id(' ||
                              pn_pro_rel_id || ') and level(' || pn_level ||
                              '). ' || sqlerrm);
  END Fnc_Get_Config_Details;
  /**************************************************************************************************/
  FUNCTION Fnc_Level_Cnt(pn_pro_rel_id NUMBER, pn_level NUMBER) RETURN NUMBER IS
    /********************************************************************************
    Purpose: To fetch the count of sw-versions used for the given level in the configured release
    Parameter:Pn_Pro_rel_id: Release id
              Pn_level: Level
    Return: Count of sw-versions for the given level
    History:Sp, 25-Sep-05,V4.5,RCMS00047178
    *********************************************************************************************/
    n_cnt NUMBER := 0;
  BEGIN
    SELECT count(sw_version_id)
      INTO n_cnt
      FROM v_ldb2_config_release
     WHERE pro_rel_id = pn_pro_rel_id
       AND sw_level = pn_level;
    RETURN n_cnt;
  EXCEPTION
    WHEN no_Data_found THEN
      RETURN 0;
    WHEN Others THEN
      Pkg_Error_Logging.RegisterError('Error occurred  from the function Fnc_Level_Cnt',
                                      SQLERRM,
                                      USER,
                                      SYSDATE,
                                      'Pkg_Villa_Interface');
      Raise_Application_Error(-20003,
                              'Unable to fetch the count ,release details for pro_rel_id(' ||
                              pn_pro_rel_id || ') and level(' || pn_level ||
                              '). ' || sqlerrm);
      RETURN 0;
  END;
  /********************************************************************************************************************************/
  FUNCTION Fnc_Excel_Config_Desc(pn_pro_rel_id number,
                                 pn_level      number,
                                 pn_swposition NUMBER) RETURN VARCHAR2 IS
    /********************************************************************************
    Purpose: To create configured details
    Parameter:Pn_Pro_rel_id: Release id
              Pn_level: Level
              Pn_Swposition: Sw-Position
    Return: Count of sw-versions for the given level
    History:Sp, 25-Sep-05,V4.5,RCMS00047178
    **********************************************************************************************/
    /*Cursor to fetch the configured details*/
    CURSOR cur_config_des is
      SELECT con.sw_level, con.sw_position, con.sw_version_id
        FROM v_ldb2_config_release con
       WHERE con.pro_rel_id = pn_pro_rel_id
         AND con.sw_level = pn_level
         AND con.sw_position = pn_swposition
       ORDER BY con.sw_level, con.sw_position;
    c_level_desc VARCHAR2(5000);
    n_retcode    NUMBER;
  BEGIN
    /*Looping through the data*/
    FOR val_config_des in cur_config_des LOOP
      c_level_desc := Ldb2_Full_Scope(val_config_des.sw_version_id, 'Y');
    END LOOP;
    c_level_desc := replace(c_level_desc, chr(10), ' ');
    return c_level_desc;
    --sp,14.03.03,fedab29711,V3.1,to handle exceptions n to login to the error file.
  EXCEPTION
    WHEN no_data_found THEN
      RETURN null;
    WHEN OTHERS THEN
      Rollback;
      Pkg_Error_Logging.RegisterError('Error occurred  from the function Fnc_Get_Config_Desc',
                                      SQLERRM,
                                      USER,
                                      SYSDATE,
                                      'Pkg_Villa_Interface');
      Raise_Application_Error(-20003,
                              'Unable to fetch the configured details for pro_rel_id(' ||
                              pn_pro_rel_id || ') and level(' || pn_level ||
                              '). ' || sqlerrm);
  END;
  /**********************************************************************************************************************/
  FUNCTION Fnc_Excel_Rev_Desc(pn_pro_rel_id NUMBER,
                              pn_level      NUMBER,
                              pn_swposition NUMBER) RETURN VARCHAR2 IS
    /*Cursor to fetch the revision details*/
    CURSOR cur_get_revision IS
      SELECT rev_id || ' ' || description descr
        FROM v_ldb2_project_revision rev, v_ldb2_config_release con
       WHERE rev.pro_rev_id = con.sw_pro_rev_id
         AND con.pro_rel_id = pn_pro_rel_id
         AND con.sw_level = pn_level
         AND con.sw_position = pn_swposition;
    c_level_desc VARCHAR2(5000);
    n_retcode    NUMBER;
  BEGIN
    /*Looping through the data*/
    FOR val_rel_descr in cur_get_revision LOOP
      c_level_desc := val_rel_descr.descr;
    END LOOP;
    c_level_desc := replace(c_level_desc, chr(10), ' ');
    Return c_level_desc;
  EXCEPTION
    WHEN no_data_found THEN
      RETURN NULL;
    WHEN OTHERS THEN
      Rollback;
      Pkg_Error_Logging.RegisterError('Error occurred  from the function Fnc_Excel_Rev_Desc',
                                      SQLERRM,
                                      USER,
                                      SYSDATE,
                                      'Pkg_Villa_Interface');
      Raise_Application_Error(-20003,
                              'Unable to fetch the configured revision details for pro_rel_id(' ||
                              pn_pro_rel_id || ') and level(' || pn_level ||
                              '). ' || sqlerrm);
  END Fnc_Excel_Rev_Desc;
  /******************************************************************************************************************/
  FUNCTION CONFIG_RELEASE_CNT(Pn_pro_rel_id NUMBER) RETURN NUMBER IS
    n_cnt NUMBER := 0;
  BEGIN
    SELECT COUNT(sw_version_id)
      INTO n_cnt
      FROM v_ldb2_config_release
     WHERE pro_rel_id = pn_pro_rel_id;
    RETURN n_cnt;
  EXCEPTION
    when others then
      Pkg_Error_Logging.RegisterError('Error occurred  from the function Config_Release_Cnt',
                                      SQLERRM,
                                      USER,
                                      SYSDATE,
                                      'Pkg_Villa_Interface');
      Raise_Application_Error(-20003,
                              'Unable to fetch the configured count for pro_rel_id(' ||
                              pn_pro_rel_id || ') ' || sqlerrm);
      RETURN 0;
  END;
  /******************************************************************************************************/
  PROCEDURE SSD_File(pn_pro_rel_id NUMBER,
                     pc_ssd_data   OUT c_villa_data%TYPE,
                     pc_mft        VARCHAR2 DEFAULT 'MFT',
                     pc_ssd        VARCHAR2 DEFAULT 'SSD',
                     pc_appchK     VARCHAR2 DEFAULT 'App-Chk',
                     pc_comp       VARCHAR2 DEFAULT 'Cal4Comp',
                     pb_refresh    BOOLEAN DEFAULT TRUE,
                     pb_comment    BOOLEAN DEFAULT FALSE) IS
    /***************************************************************************
    Purpose: To save the SSD output to PL/SQl table for the provided release id
    Parameters: In:Pn_pro_rel_id---Release id
                  OUT:None
    Returns:  True , incase the function is success else returns false
    History: Sp,6-Aug-03,For Villa
             PK,16.10.03, FEDab33593 Global Bypass Check added for labels with *Vector
             and *Channel, Changed Header format, added parameters for filter
             PK,16.03.04, FEDab33935 Global SSD Header for check SSD file
             PK,09.03.05, FEDab40788 Automatic comments for Advance formula
             PK,19.08.2005 RCMS00046790 Convert DCM2SSD for the labels
             RB,23.08/07 RCMS00447599 Increase of SSD-Release-Config Levels from 3 to 5 levels
             SK RCMS00457889 Name of .ssd-file depends on selected usecases MFT, SSD, APP
    ***************************************************************************/
    /*To fetch the SSD detail for the given release id*/
    /*
        PK RCMS00046790 added dcm2ssd
    */
    CURSOR c_ssd IS
      SELECT distinct lab_obj_id,
                      typ,
                      max_wert,
                      min_wert,
                      formula formula,
                      ssd_grp_label,
                      func_label,
                      label,
                      lab_rev_id,
                      dcm2ssd,
                      cases,
                      contact
        FROM v_ldb2_dcm_ssd
       WHERE pro_rel_id = pn_pro_rel_id
         AND cases IN (pc_mft, pc_ssd, pc_appchk, pc_comp)
       ORDER BY ssd_grp_label, func_label, label;
    /*cursor to fetch the components for the release*/
    CURSOR cur_rel_comp IS
      SELECT feature_text, value_text
        FROM v_ldb2_prj_rel_comp
       WHERE pro_rel_id = pn_pro_rel_id
       ORDER BY feature_text, value_text;
    CURSOR Cur_Rel IS
      SELECT DECODE(rel_typ, 'e', 'External release', 'Internal Release') rel_type,
             cre_date rel_date,
             cre_user rel_user,
             rel_id rel_id,
             description rel_description,
             global_chk global_bypass,
             global_ssdheader
        FROM v_ldb2_project_release
       WHERE pro_rel_id = pn_pro_rel_id;
    CURSOR LCur_Errors is
      SELECT 1
        FROM DUAL
       WHERE EXISTS (SELECT PRO_REL_ID
                FROM v_ldb2_pro_errors
               WHERE pro_rel_id = pn_pro_rel_id);
    LI_Pro_Rel_ID V_LDB2_PRO_ERRORS.PRO_REL_ID % TYPE;
    /*Local Variable declaration*/
    c_rel_typ           VARCHAR2(1);
    b_errors            BOOLEAN := FALSE;
    c_releasetyp        VARCHAR2(500) := 'Only internal Release';
    c_ssd_grp_label_old v_ldb2_ssd_grp.label%TYPE := '__xxxxx__';
    c_func_label_old    v_ldb2_pavast.function%TYPE := '__xxxxx__';
    c_nachlabel         VARCHAR2(1010);
    c_nachlabel_frm     VARCHAR2(1010);
    n_count_grp         NUMBER := 0;
    n_count_func        NUMBER := 0;
    c_comp              VARCHAR2(20000);
    c_feature           v_ldb2_features.feature_text%TYPE;
    c_value             v_ldb2_values.value_text%TYPE;
    c_default           CHAR;
    c_rev_data1         VARCHAR2(4000);
    c_rev_data2         VARCHAR2(4000);
    c_rev_data3         VARCHAR2(4000);
    c_rev_data          VARCHAR2(4000);
    c_new_formula_desc  VARCHAR2(32767);
    i                   BINARY_INTEGER := 1;
    c_cases             VARCHAR2(200);
    LI_Counter          NUMBER;
    n_rev_cnt           NUMBER := 0;
    c_global_chk        CHAR;
    c_global_type       VARCHAR2(10);
    c_ecutype           VARCHAR2(20);
    c_global_ssdheader  v_ldb2_project_release.global_ssdheader%TYPE;
    dcm2ssd_data        pkg_dcm2ssd.ssd_file;
    n_config_cnt        NUMBER;
    n_level_cnt         NUMBER;
    c_description1      VARCHAR2(32000);
    c_rev_description1  VARCHAR2(32000);
    n_node              v_ldb2_object_tree.node_id%type;
    c_desc_substr1      VARCHAR2(10000);
    c_desc_substr2      VARCHAR2(10000);
  BEGIN
    /* Initialize c_villa_data!!
    For Villa the PL/SQL-Table is not cleared incase the query is re-run for same pro_rel_id but
    for labeldatabase every time the package is invoke the PL/SQL-Table is cleared */
    IF pb_refresh THEN
      IF G_ReleaseID <> pn_pro_rel_id THEN
        c_villa_data.DELETE;
      END IF;
    ELSE
      c_villa_data.DELETE;
    END IF;
    /************************************************************************************************************/
    BEGIN
      /*To fetch the components detail for the given release id*/
      FOR Rec_Release_Component IN cur_rel_comp LOOP
        IF c_comp IS NULL THEN
          c_comp := Rec_Release_Component.Feature_text || '{' ||
                    Rec_Release_Component.Value_Text || '}';
        ELSE
          c_comp := c_comp || chr(10) || '!' || chr(9) || chr(9) || ' ' ||
                    Rec_Release_Component.Feature_text || '{' ||
                    Rec_Release_Component.Value_Text || '}';
        END IF;
      END LOOP;
    EXCEPTION
      WHEN OTHERS THEN
        Pkg_Error_Logging.RegisterError('Error occurred  while fetching the components detail from the function SSD_File',
                                        SQLERRM,
                                        USER,
                                        SYSDATE,
                                        'Pkg_Villa_Interface');
        Raise_Application_Error(-20003,
                                'Error occurred while creating SSD File for pro_rel_id(' ||
                                pn_pro_rel_id || ') ' || sqlerrm);
    END;
    /************************************************************************************************************/
    BEGIN
      /*To fecth release detail*/
      -- $$$
      /*
      If external release, then no errors??
      if internal release, then check if errors exist for the release!!
      */
      -- $$$
      -- PK V4.0 To handle global bypass check based on ECU-Types
      c_ecutype := getECUType(pn_pro_rel_id);
      IF c_ecutype IS NULL THEN
        Pkg_Error_Logging.RegisterError('Error occurred  while finding the ECU-Type for the release',
                                        SQLERRM,
                                        USER,
                                        SYSDATE,
                                        'Pkg_Villa_Interface');
      END IF;
      FOR C_Rel IN Cur_Rel LOOP
        IF C_Rel.rel_type = 'Internal Release' THEN
          BEGIN
            OPEN LCur_Errors;
            FETCH LCur_Errors
              INTO LI_Pro_Rel_ID;
            b_Errors := LCur_Errors%FOUND;
            CLOSE LCur_Errors;
          EXCEPTION
            WHEN OTHERS THEN
              IF LCur_Errors%ISOPEN THEN
                CLOSE LCur_Errors;
              END IF;
              Pkg_Error_Logging.RegisterError('Error occurred  while finding the release type from the function SSD_File',
                                              SQLERRM,
                                              USER,
                                              SYSDATE,
                                              'Pkg_Villa_Interface');
          END;
          IF b_errors THEN
            c_releasetyp := 'Only internal Release (with errors)';
          END IF;
        ELSE
          c_releasetyp := 'External release';
        END IF;
        SELECT DECODE(pc_mft, 'MFT', 'MFT', '') ||
               DECODE(pc_ssd, 'SSD', ' SSD', '') ||
               DECODE(pc_appchk, 'App-Chk', ' App-Chk', '') ||
               DECODE(pc_comp, 'Cal4Comp', 'Cal4Comp', '')
          INTO c_cases
          FROM DUAL;
        c_cases := REPLACE(TRIM(c_cases), ' ', ',');
        /*Inserting the values to PL/Sql table*/
        c_villa_data(i) := '!  SSD-Report for ' || c_ecutype ||
                           ' Software, SSD-Labeldatabase, DS/ESQ3';
        i := i + 1;
        c_villa_data(i) := '!  ============================================================================================';
        i := i + 1;
        c_villa_data(i) := ' ';
        i := i + 1;
        c_villa_data(i) := '!  System-spezifische Software-Daten';
        /*Fetching the count for the release*/
        n_config_cnt := CONFIG_RELEASE_CNT(Pn_pro_rel_id);
        /*If it is a configured release then the release detail for all levels are displayed*/
        IF n_config_cnt > 1 THEN
          i := i + 1;
          c_villa_data(i) := '!  Scope    Level1                                                Labellist';
          /*Sp, V4.5*/
          /*Finding count for each level and looping through the data*/
          n_level_cnt := Pkg_Villa_Interface.Fnc_Level_Cnt(pn_pro_rel_id, 1);
          FOR j IN 1 .. n_level_cnt LOOP
            c_description1     := Pkg_Villa_Interface.fnc_excel_config_desc(pn_pro_rel_id,
                                                                            1,
                                                                            j);
            c_rev_description1 := Pkg_Villa_Interface.Fnc_Excel_Rev_Desc(pn_pro_rel_id,
                                                                         1,
                                                                         j);
            IF Length(c_description1) < 100 THEN
              c_description1 := rpad(c_description1, 100, ' ');
              i := i + 1;
              c_villa_data(i) := '!             ' || c_description1 || '' ||
                                 c_rev_description1;
            ELSIF Length(c_description1) > 100 THEN
              c_desc_substr1 := Substr(c_description1, 1, 90);
              FOR n in 90 .. 100 LOOP
                c_desc_substr1 := c_desc_substr1 || ' ';
              END LOOP;
              c_desc_substr2 := Substr(c_description1,
                                       91,
                                       Length(c_desc_substr1));
              i := i + 1;
              c_villa_data(i) := '!            ' || chr(9) ||
                                 c_desc_substr1 || '' || c_rev_description1;
              i := i + 1;
              c_villa_data(i) := '!            ' || chr(9) ||
                                 c_desc_substr2;
            ELSE
              i := i + 1;
              c_villa_data(i) := '!             ' || c_description1 || '' ||
                                 c_rev_description1;
            END IF;
          END LOOP;
          i := i + 1;
          c_villa_data(i) := '! ';
          /*For level 2*/
          i := i + 1;
          c_villa_data(i) := '!           Level2                                                Labellist';
          /*Resetting the cnt to 0*/
          n_level_cnt := 0;
          /*Finding the cnt for level2 */
          /*Finding count for each level and looping through the data*/
          n_level_cnt := Pkg_Villa_Interface.Fnc_Level_Cnt(pn_pro_rel_id, 2);
          FOR j IN 1 .. n_level_cnt LOOP
            c_description1     := Pkg_Villa_Interface.fnc_excel_config_desc(pn_pro_rel_id,
                                                                            2,
                                                                            j);
            c_rev_description1 := Pkg_Villa_Interface.Fnc_Excel_Rev_Desc(pn_pro_rel_id,
                                                                         2,
                                                                         j);
            IF Length(c_description1) < 100 THEN
              c_description1 := rpad(c_description1, 100, ' ');
              i := i + 1;
              c_villa_data(i) := '!             ' || c_description1 || '' ||
                                 c_rev_description1;
            ELSIF Length(c_description1) > 100 THEN
              c_desc_substr1 := Substr(c_description1, 1, 90);
              FOR n in 90 .. 100 LOOP
                c_desc_substr1 := c_desc_substr1 || ' ';
              END LOOP;
              c_desc_substr2 := Substr(c_description1,
                                       91,
                                       Length(c_desc_substr1));
              i := i + 1;
              c_villa_data(i) := '!            ' || chr(9) ||
                                 c_desc_substr1 || '' || c_rev_description1;
              i := i + 1;
              c_villa_data(i) := '!            ' || chr(9) ||
                                 c_desc_substr2;
            ELSE
              i := i + 1;
              c_villa_data(i) := '!             ' || c_description1 || '' ||
                                 c_rev_description1;
            END IF;
          END LOOP;
          i := i + 1;
          c_villa_data(i) := '! ';
          /*For level 3*/
          i := i + 1;
          c_villa_data(i) := '!           Level3                                                Labellist';
          n_level_cnt := 0;
          /*Fetching the count for level 3*/
          n_level_cnt := Pkg_Villa_Interface.Fnc_Level_Cnt(pn_pro_rel_id, 3);
          FOR j IN 1 .. n_level_cnt LOOP
            c_description1     := Pkg_Villa_Interface.fnc_excel_config_desc(pn_pro_rel_id,
                                                                            3,
                                                                            j);
            c_rev_description1 := Pkg_Villa_Interface.Fnc_Excel_Rev_Desc(pn_pro_rel_id,
                                                                         3,
                                                                         j);
            IF Length(c_description1) < 100 THEN
              c_description1 := rpad(c_description1, 100, ' ');
              i := i + 1;
              c_villa_data(i) := '!             ' || c_description1 || '' ||
                                 c_rev_description1;
            ELSIF Length(c_description1) > 100 THEN
              c_desc_substr1 := Substr(c_description1, 1, 90);
              FOR n in 90 .. 100 LOOP
                c_desc_substr1 := c_desc_substr1 || ' ';
              END LOOP;
              c_desc_substr2 := Substr(c_description1,
                                       91,
                                       Length(c_desc_substr1));
              i := i + 1;
              c_villa_data(i) := '!            ' || chr(9) ||
                                 c_desc_substr1 || '' || c_rev_description1;
              i := i + 1;
              c_villa_data(i) := '!            ' || chr(9) ||
                                 c_desc_substr2;
            ELSE
              i := i + 1;
              c_villa_data(i) := '!             ' || c_description1 || '' ||
                                 c_rev_description1;
            END IF;
          END LOOP;
          --RB,23.08/07 RCMS00447599 Increase of SSD-Release-Config Levels from 3 to 5 levels
          i := i + 1;
          c_villa_data(i) := '! ';
          /*For level 4*/
          i := i + 1;
          c_villa_data(i) := '!           Level4                                                Labellist';
          n_level_cnt := 0;
          /*Fetching the count for level 4*/
          n_level_cnt := Pkg_Villa_Interface.Fnc_Level_Cnt(pn_pro_rel_id, 4);
          FOR j IN 1 .. n_level_cnt LOOP
            c_description1     := Pkg_Villa_Interface.fnc_excel_config_desc(pn_pro_rel_id,
                                                                            4,
                                                                            j);
            c_rev_description1 := Pkg_Villa_Interface.Fnc_Excel_Rev_Desc(pn_pro_rel_id,
                                                                         4,
                                                                         j);
            IF Length(c_description1) < 100 THEN
              c_description1 := rpad(c_description1, 100, ' ');
              i := i + 1;
              c_villa_data(i) := '!             ' || c_description1 || '' ||
                                 c_rev_description1;
            ELSIF Length(c_description1) > 100 THEN
              c_desc_substr1 := Substr(c_description1, 1, 90);
              FOR n in 90 .. 100 LOOP
                c_desc_substr1 := c_desc_substr1 || ' ';
              END LOOP;
              c_desc_substr2 := Substr(c_description1,
                                       91,
                                       Length(c_desc_substr1));
              i := i + 1;
              c_villa_data(i) := '!            ' || chr(9) ||
                                 c_desc_substr1 || '' || c_rev_description1;
              i := i + 1;
              c_villa_data(i) := '!            ' || chr(9) ||
                                 c_desc_substr2;
            ELSE
              i := i + 1;
              c_villa_data(i) := '!             ' || c_description1 || '' ||
                                 c_rev_description1;
            END IF;
          END LOOP;
          i := i + 1;
          c_villa_data(i) := '! ';
          /*For level 3*/
          i := i + 1;
          c_villa_data(i) := '!           Level5                                                Labellist';
          n_level_cnt := 0;
          /*Fetching the count for level 5*/
          n_level_cnt := Pkg_Villa_Interface.Fnc_Level_Cnt(pn_pro_rel_id, 5);
          FOR j IN 1 .. n_level_cnt LOOP
            c_description1     := Pkg_Villa_Interface.fnc_excel_config_desc(pn_pro_rel_id,
                                                                            5,
                                                                            j);
            c_rev_description1 := Pkg_Villa_Interface.Fnc_Excel_Rev_Desc(pn_pro_rel_id,
                                                                         5,
                                                                         j);
            IF Length(c_description1) < 100 THEN
              c_description1 := rpad(c_description1, 100, ' ');
              i := i + 1;
              c_villa_data(i) := '!             ' || c_description1 || '' ||
                                 c_rev_description1;
            ELSIF Length(c_description1) > 100 THEN
              c_desc_substr1 := Substr(c_description1, 1, 90);
              FOR n in 90 .. 100 LOOP
                c_desc_substr1 := c_desc_substr1 || ' ';
              END LOOP;
              c_desc_substr2 := Substr(c_description1,
                                       91,
                                       Length(c_desc_substr1));
              i := i + 1;
              c_villa_data(i) := '!            ' || chr(9) ||
                                 c_desc_substr1 || '' || c_rev_description1;
              i := i + 1;
              c_villa_data(i) := '!            ' || chr(9) ||
                                 c_desc_substr2;
            ELSE
              i := i + 1;
              c_villa_data(i) := '!             ' || c_description1 || '' ||
                                 c_rev_description1;
            END IF;
          END LOOP;
        ELSE
          /*If the release is not a configured release then fetch the data based on release id*/
          /*To fetch the node id for the selected release*/
          n_node := Get_Node_Id(pn_pro_rel_id);
          /*To find the full description based on node_id*/
          c_description1 := Ldb2_Full_Scope(n_node);
          /*To fetch the revision description for the selected release*/
          c_rev_description1 := Get_Rev_Description(pn_pro_rel_id);
          i := i + 1;
          c_villa_data(i) := '!  Scope' || chr(9) || ':' || 'Level1' ||
                             chr(9) || chr(9) || chr(9) || chr(9) || chr(9) ||
                             chr(9) || chr(9) || chr(9) || chr(9) || chr(9) ||
                             chr(9) || chr(9) || 'Labellist';
          IF Length(c_description1) < 100 THEN
            c_description1 := rpad(c_description1, 100, ' ');
            i := i + 1;
            c_villa_data(i) := '!            ' || chr(9) || c_description1 || '' ||
                               c_rev_description1;
          ELSIF Length(c_description1) > 100 THEN
            c_desc_substr1 := Substr(c_description1, 1, 90);
            c_desc_substr1 := rpad(c_desc_substr1, 100, ' ');
            c_desc_substr2 := Substr(c_description1,
                                     91,
                                     Length(c_desc_substr1));
            i := i + 1;
            c_villa_data(i) := '!            ' || chr(9) || c_desc_substr1 || '' ||
                               c_rev_description1;
            i := i + 1;
            c_villa_data(i) := '!             ' || chr(9) || c_desc_substr2;
          ELSE
            i := i + 1;
            c_villa_data(i) := '!            ' || chr(9) || c_description1 || '' ||
                               c_rev_description1;
          END IF;
          i := i + 1;
          c_villa_data(i) := '! ';
          i := i + 1;
          c_villa_data(i) := '!       ' || chr(9) || ':' || 'Level2' ||
                             chr(9) || chr(9) || chr(9) || chr(9) || chr(9) ||
                             chr(9) || chr(9) || chr(9) || chr(9) || chr(9) ||
                             chr(9) || chr(9) || 'Labellist';
          i := i + 1;
          c_villa_data(i) := '! ';
          i := i + 1;
          c_villa_data(i) := '!       ' || chr(9) || ':' || 'Level3' ||
                             chr(9) || chr(9) || chr(9) || chr(9) || chr(9) ||
                             chr(9) || chr(9) || chr(9) || chr(9) || chr(9) ||
                             chr(9) || chr(9) || 'Labellist';
          i := i + 1;
          c_villa_data(i) := '! ';
          i := i + 1;
          c_villa_data(i) := '!       ' || chr(9) || ':' || 'Level4' ||
                             chr(9) || chr(9) || chr(9) || chr(9) || chr(9) ||
                             chr(9) || chr(9) || chr(9) || chr(9) || chr(9) ||
                             chr(9) || chr(9) || 'Labellist';
          i := i + 1;
          c_villa_data(i) := '! ';
          i := i + 1;
          c_villa_data(i) := '!       ' || chr(9) || ':' || 'Level5' ||
                             chr(9) || chr(9) || chr(9) || chr(9) || chr(9) ||
                             chr(9) || chr(9) || chr(9) || chr(9) || chr(9) ||
                             chr(9) || chr(9) || 'Labellist';
        END IF;
        i := i + 1;
        c_villa_data(i) := '! ';
        i := i + 1;
        c_villa_data(i) := '!  Release' || chr(9) || ':' || c_rel.rel_id || ' ' ||
                           replace(c_rel.rel_description, chr(10));
        i := i + 1;
        c_villa_data(i) := '!  ';
        i := i + 1;
        c_villa_data(i) := '!  Cases' || chr(9) || ':' || c_cases;
        i := i + 1;
        c_villa_data(i) := '!  ';
        i := i + 1;
        c_villa_data(i) := '!  Created On' || chr(9) || ':' || sysdate;
        i := i + 1;
        c_villa_data(i) := '! ';
        i := i + 1;
        c_villa_data(i) := '!  Created By' || chr(9) || ':' || user;
        i := i + 1;
        c_villa_data(i) := '! ';
        i := i + 1;
        c_villa_data(i) := '!  ECU-Type' || chr(9) || ':' || c_ecutype;
        i := i + 1;
        c_villa_data(i) := '! ';
        i := i + 1;
        c_villa_data(i) := '!  ' || c_releasetyp;
        i := i + 1;
        c_villa_data(i) := '! ';
        i := i + 1;
        c_villa_data(i) := '!  Features' || chr(9) || ':' || c_comp;
        i := i + 1;
        c_villa_data(i) := '!  ============================================================================================';
        i := i + 1;
        c_villa_data(i) := ' ';
        i := i + 1;
        c_villa_data(i) := ' ';
        --V4.1 Global Check SSD header to be added
        IF c_rel.global_ssdheader IS NOT NULL THEN
          i := i + 1;
          c_villa_data(i) := '   ' || c_rel.global_ssdheader;
          i := i + 1;
          c_villa_data(i) := ' ';
        END IF;
        IF c_rel.global_bypass = 'Y' THEN
          -- V4.0 to insert respective global bypass check type;
          IF c_ecutype = 'EDC16' THEN
            c_global_type := '*_';
          ELSE
            c_global_type := '*';
          END IF;
          i := i + 1;
          c_villa_data(i) := '! 0. - Global Check ';
          i := i + 1;
          c_villa_data(i) := '! =========================================================';
          i := i + 1;
          c_villa_data(i) := ' ';
          i := i + 1;
          c_villa_data(i) := '! 0.1 Global Bypass Check ';
          i := i + 1;
          c_villa_data(i) := '! ---------------------------------------------------------';
          i := i + 1;
          c_villa_data(i) := ' ';
          i := i + 1;
          c_villa_data(i) := '   ' || c_global_type || 'Channel, >=0, <=0';
          i := i + 1;
          c_villa_data(i) := ' ';
          i := i + 1;
          c_villa_data(i) := '   ' || c_global_type || 'Vector, >=0, <=0';
          i := i + 1;
          c_villa_data(i) := ' ';
        END IF;
      END LOOP;
    END;
    /*************************************************************************************************************/
    /*To include SSD data*/
    FOR c_ssd_rec IN c_ssd LOOP
      /*To fetch advanced formula as it is clob field and cannot be queried directly from the select statement*/
      c_new_formula_Desc := pkg_clob.Get_Formdesc(c_ssd_rec.lab_obj_id,
                                                  c_ssd_rec.lab_rev_id);
      IF c_new_formula_desc = '-2' THEN
        NULL;
      ELSE
        c_default := 'N';
        IF c_ssd_rec.ssd_grp_label != c_ssd_grp_label_old THEN
          n_count_grp := n_count_grp + 1;
          c_ssd_grp_label_old := c_ssd_rec.ssd_grp_label;
          c_func_label_old := '__xxxxxx__';
          n_count_func := 0;
          i := i + 1;
          c_villa_data(i) := ' ';
          i := i + 1;
          c_villa_data(i) := '! ' || to_char(n_count_grp) || '. ' ||
                             c_ssd_rec.ssd_grp_label;
          i := i + 1;
          c_villa_data(i) := '! =========================================================';
          i := i + 1;
          c_villa_data(i) := ' ';
        END IF;
        IF c_ssd_rec.func_label != c_func_label_old THEN
          n_count_func := n_count_func + 1;
          c_func_label_old := c_ssd_rec.func_label;
          i := i + 1;
          c_villa_data(i) := '! ' || to_char(n_count_grp) || '.' ||
                             to_char(n_count_func) || ' ' ||
                             c_ssd_rec.func_label;
          i := i + 1;
          c_villa_data(i) := '! ---------------------------------------------------------';
          i := i + 1;
          c_villa_data(i) := ' ';
        END IF;
        i := i + 1;
        c_villa_data(i) := '#usecase ' || c_ssd_rec.cases;
        i := i + 1;
        c_villa_data(i) := '#contact "' || c_ssd_rec.contact || '"';
        i := i + 1;
        c_villa_data(i) := ' ';
        IF c_ssd_rec.dcm2ssd = 'Y' THEN
          dcm2ssd_data := pkg_dcm2ssd.convert_dcm2ssd(c_ssd_rec.lab_obj_id,
                                                      c_ssd_rec.lab_rev_id);
          FOR j IN 1 .. dcm2ssd_data.count LOOP
            i := i + 1;
            c_villa_data(i) := dcm2ssd_data(j);
          END LOOP;
        END IF;
        c_nachlabel_frm := ', ' || c_ssd_rec.formula;
        c_nachlabel     := null;
        IF c_ssd_rec.min_wert IS NOT NULL THEN
          c_nachlabel := ', >=' ||
                         replace(to_char(c_ssd_rec.min_wert), ',', '.');
        END IF;
        IF c_ssd_rec.max_wert IS NOT NULL THEN
          c_nachlabel := c_nachlabel || ', <=' ||
                         replace(to_char(c_ssd_rec.max_wert), ',', '.');
        END IF;
        IF (c_ssd_rec.min_wert IS NULL AND c_ssd_rec.max_wert IS NULL AND
           c_ssd_rec.typ IS NOT NULL) THEN
          c_default   := 'Y';
          c_nachlabel := ', =' || replace(to_char(c_ssd_rec.typ), ',', '.');
        END IF;
        /* if min ,max and formula and advanced formula are null then only default is considered in the file */
        IF c_default = 'Y' THEN
          IF c_ssd_rec.formula IS NULL AND c_new_formula_desc IS NULL AND
             c_nachlabel IS NOT NULL THEN
            i := i + 1;
            c_villa_data(i) := '   ' || c_ssd_rec.label || c_nachlabel;
            i := i + 1;
            c_villa_data(i) := ' ';
          END IF;
        ELSE
          IF c_nachlabel IS NOT NULL THEN
            i := i + 1;
            c_villa_data(i) := '   ' || c_ssd_rec.label || c_nachlabel;
            i := i + 1;
            c_villa_data(i) := ' ';
          END IF;
        END IF;
        IF c_ssd_rec.formula IS NOT NULL THEN
          i := i + 1;
          c_villa_data(i) := '   ' || c_ssd_rec.label || ', ' ||
                             c_ssd_rec.formula;
          i := i + 1;
          c_villa_data(i) := ' ';
        END IF;
        IF c_new_formula_desc IS NOT NULL THEN
          i := i + 1;
          /*
                  PK 09.03.2005, FEDab40788, pb_comment is used to generate autocomments for advance formula
          */
          IF pb_comment THEN
            c_villa_data(i) := '!##################### Begin of ' ||
                               c_ssd_rec.label ||
                               ' #######################';
            i := i + 1;
            c_villa_data(i) := ' ';
            i := i + 1;
          END IF;
          c_villa_data(i) := '   ' || c_new_formula_desc;
          /*
                  PK 09.03.2005, FEDab40788, pb_comment is used to generate autocomments for advance formula
          */
          IF pb_comment THEN
            i := i + 1;
            c_villa_data(i) := ' ';
            i := i + 1;
            c_villa_data(i) := '!##################### End of ' ||
                               c_ssd_rec.label ||
                               ' #######################';
          END IF;
          i := i + 1;
          c_villa_data(i) := ' ';
        END IF;
        i := i + 1;
        c_villa_data(i) := '#endcontact ';
        i := i + 1;
        c_villa_data(i) := '#endusecase';
        i := i + 1;
        c_villa_data(i) := ' ';
      END IF;
    END LOOP;
    pc_ssd_data := c_villa_data;
    -- Shankar 18.08.2003
    SetReleaseID(pn_pro_rel_id);
  EXCEPTION
    WHEN OTHERS THEN
      Pkg_Error_Logging.RegisterError('Error occurred  from the function SSD_File',
                                      SQLERRM,
                                      USER,
                                      SYSDATE,
                                      'Pkg_Villa_Interface');
      Raise_Application_Error(-20003,
                              'Error occurred while creating SSD file for pro_rel_id(' ||
                              pn_pro_rel_id || ') ' || sqlerrm);
  END SSD_File;
  /******************************************************************************************************/
  FUNCTION SSD_File_Label(pn_pro_rel_id NUMBER,
                          pc_ssd_data   OUT c_villa_data%TYPE,
                          pc_mft        VARCHAR2 DEFAULT 'MFT',
                          pc_ssd        VARCHAR2 DEFAULT 'SSD',
                          pc_appchK     VARCHAR2 DEFAULT 'App-Chk',
                          pc_comp       VARCHAR2 DEFAULT 'Cal4Comp',
                          pb_temporary  BOOLEAN DEFAULT FALSE,
                          pb_refresh    BOOLEAN DEFAULT TRUE,
                          pb_comment    BOOLEAN DEFAULT FALSE) RETURN BOOLEAN IS
    /*****************************************************************************************************
    Purpose            : To save the SSD output to PL/SQl table for the provided release id ordered by label
    Parameters INPUT :    pn_pro_rel_id            Release id
                                            pc_ssd_data                Out put will be a PL/SQL table containing SSD data
                                            pc_mft                        MFT    case
                                            pc_ssd                        SSD case
                                            pc_appchk                    App-Chk case
                                            pb_temporary            If its a temporary release
                                            pb_refresh                Flag for villa incase the data is same
                                            pb_comment                Advanced formula comments if required
                  OUT     :        TRUE on success and FALSE on failure
                  History     :     PK,22-08-05 FEDab00046790 V4.5
                                             Moved existing forms function to database package so that it can be used across many forms
                                             RB,23.08/07 RCMS00447599 Increase of SSD-Release-Config Levels from 3 to 5 levels
                        SK RCMS00457889 Name of .ssd-file depends on selected usecases MFT, SSD, APP
    *********************************************************************************************************/
    /*To fetch the SSD detail for the given release id*/
    /*
        PK RCMS00046790 added dcm2ssd
    */
    CURSOR c_ssd IS
      SELECT distinct lab_obj_id,
                      typ,
                      max_wert,
                      min_wert,
                      formula formula,
                      ssd_grp_label,
                      func_label,
                      label,
                      lab_rev_id,
                      dcm2ssd,
                      cases,
                      contact
        FROM v_ldb2_dcm_ssd
       WHERE pro_rel_id = pn_pro_rel_id
         AND cases IN (pc_mft, pc_ssd, pc_appchk, pc_comp)
       ORDER BY label;
    CURSOR Cur_Rel IS
      SELECT DECODE(rel_typ, 'e', 'External release', 'Internal Release') rel_type,
             cre_date rel_date,
             cre_user rel_user,
             rel_id rel_id,
             description rel_description,
             global_chk global_bypass,
             global_ssdheader
        FROM v_ldb2_project_release
       WHERE pro_rel_id = pn_pro_rel_id;
    n_Pro_Rel_ID V_LDB2_PRO_ERRORS.PRO_REL_ID % TYPE;
    /*Local Variable declaration*/
    dcm2ssd_data       pkg_dcm2ssd.ssd_file;
    c_nachlabel_frm    VARCHAR2(1010);
    c_nachlabel        VARCHAR2(1010);
    c_default          CHAR;
    c_new_formula_desc VARCHAR2(32767);
    c_global_type      VARCHAR2(10);
    i                  BINARY_INTEGER := 1;
    c_releasetyp       VARCHAR2(500) := 'Only internal Release';
    n_count            NUMBER;
    c_ecutype          VARCHAR2(20);
    c_rev_data         VARCHAR2(4000);
    c_rev_data1        VARCHAR2(4000);
    c_rev_data2        VARCHAR2(4000);
    c_rev_data3        VARCHAR2(4000);
    n_rev_cnt          NUMBER := 0;
    c_error_text       VARCHAR2(2000);
    except_file EXCEPTION;
    c_comp             VARCHAR2(20000);
    c_cases            VARCHAR2(200);
    n_config_cnt       NUMBER;
    n_level_cnt        NUMBER;
    c_description1     VARCHAR2(32000);
    c_rev_description1 VARCHAR2(10000);
    n_node             V_Ldb2_Object_Tree.Node_id%TYPE;
    c_desc_substr1     VARCHAR2(10000);
    c_desc_substr2     VARCHAR2(10000);
  BEGIN
    /*
         Initialize c_villa_data!!
         For Villa the PL/SQL-Table is not cleared incase the query is re-run for same pro_rel_id but
         for labeldatabase every time the package is invoke the PL/SQL-Table is cleared
    */
    IF pb_refresh THEN
      IF G_ReleaseID <> pn_pro_rel_id THEN
        c_villa_data.DELETE;
      END IF;
    ELSE
      c_villa_data.DELETE;
    END IF;
    c_comp    := get_feature_values(pn_pro_rel_id);
    c_ecutype := getECUType(pn_pro_rel_id);
    IF c_ecutype IS NULL THEN
      c_error_text := 'Error occurred  while finding the ECU-Type for the release for pro_rel_id(' ||
                      pn_pro_rel_id || ')';
      RAISE except_file;
    END IF;
    /*To fecth release detail*/
    -- $$$
    /*
    If external release, then no errors??
    if internal release, then check if errors exist for the release!!
    */
    FOR c_rel IN cur_rel LOOP
      IF C_Rel.rel_type = 'Internal Release' THEN
        SELECT COUNT(pro_rel_id)
          INTO n_count
          FROM v_ldb2_pro_errors
         WHERE pro_rel_id = pn_pro_rel_id
           AND error_nr <> 6;
        IF n_count > 0 THEN
          IF pb_temporary THEN
            c_releasetyp := 'Temporary Release for CheckSSD(with errors)';
          ELSE
            c_releasetyp := 'Only internal Release (with errors)';
          END IF;
        ELSE
          IF pb_temporary THEN
            c_releasetyp := 'Temporary Release for CheckSSD';
          ELSE
            c_releasetyp := 'Only internal Release';
          END IF;
        END IF;
      ELSE
        c_releasetyp := 'External release';
      END IF;
      SELECT DECODE(pc_mft, 'MFT', 'MFT', '') ||
             DECODE(pc_ssd, 'SSD', ' SSD', '') ||
             DECODE(pc_appchk, 'App-Chk', ' App-Chk', '') ||
             DECODE(pc_comp, 'Cal4Comp', ' Cal4Comp', '')
        INTO c_cases
        FROM DUAL;
      c_cases := REPLACE(TRIM(c_cases), ' ', ',');
      /*Inserting the values to PL/Sql table*/
      c_villa_data(i) := '!  SSD-Report for ' || c_ecutype ||
                         ' Software, SSD-Labeldatabase, DS/ESQ3';
      i := i + 1;
      c_villa_data(i) := '!  ============================================================================================';
      i := i + 1;
      c_villa_data(i) := ' ';
      i := i + 1;
      c_villa_data(i) := '!  System-spezifische Software-Daten';
      /*To find whether the release is a configured release*/
      n_config_cnt := CONFIG_RELEASE_CNT(Pn_pro_rel_id);
      /*FEtch configured release data*/
      IF n_config_cnt > 1 THEN
        i := i + 1;
        c_villa_data(i) := '!  Scope    Level1                                                Labellist';
        /*Sp, V4.5, find the count for level and release*/
        n_level_cnt := Pkg_Villa_Interface.Fnc_Level_Cnt(pn_pro_rel_id, 1);
        FOR j IN 1 .. n_level_cnt LOOP
          c_description1     := Pkg_Villa_Interface.fnc_excel_config_desc(pn_pro_rel_id,
                                                                          1,
                                                                          j);
          c_rev_description1 := Pkg_Villa_Interface.Fnc_Excel_Rev_Desc(pn_pro_rel_id,
                                                                       1,
                                                                       j);
          IF Length(c_description1) < 100 THEN
            c_description1 := rpad(c_description1, 100, ' ');
            i := i + 1;
            c_villa_data(i) := '!            ' || chr(9) || c_description1 || '' ||
                               c_rev_description1;
          ELSIF Length(c_description1) > 100 THEN
            c_desc_substr1 := Substr(c_description1, 1, 90);
            c_desc_substr1 := rpad(c_desc_substr1, 100, ' ');
            c_desc_substr2 := Substr(c_description1,
                                     91,
                                     Length(c_desc_substr1));
            i := i + 1;
            c_villa_data(i) := '!            ' || chr(9) || c_desc_substr1 || '' ||
                               c_rev_description1;
            i := i + 1;
            c_villa_data(i) := '!             ' || chr(9) || c_desc_substr2;
          ELSE
            i := i + 1;
            c_villa_data(i) := '!            ' || chr(9) || c_description1 || '' ||
                               c_rev_description1;
          END IF;
        END LOOP;
        /*For level 2*/
        i := i + 1;
        c_villa_data(i) := '!     ';
        i := i + 1;
        c_villa_data(i) := '!           Level2                                                Labellist';
        n_level_cnt := 0;
        n_level_cnt := Pkg_Villa_Interface.Fnc_Level_Cnt(pn_pro_rel_id, 2);
        FOR j IN 1 .. n_level_cnt LOOP
          c_description1     := Pkg_Villa_Interface.fnc_excel_config_desc(pn_pro_rel_id,
                                                                          2,
                                                                          j);
          c_rev_description1 := Pkg_Villa_Interface.Fnc_Excel_Rev_Desc(pn_pro_rel_id,
                                                                       2,
                                                                       j);
          IF Length(c_description1) < 100 THEN
            c_description1 := rpad(c_description1, 100, ' ');
            i := i + 1;
            c_villa_data(i) := '!            ' || chr(9) || c_description1 || '' ||
                               c_rev_description1;
          ELSIF Length(c_description1) > 100 THEN
            c_desc_substr1 := Substr(c_description1, 1, 90);
            c_desc_substr1 := rpad(c_desc_substr1, 100, ' ');
            c_desc_substr2 := Substr(c_description1,
                                     91,
                                     Length(c_desc_substr1));
            i := i + 1;
            c_villa_data(i) := '!            ' || chr(9) || c_desc_substr1 || '' ||
                               c_rev_description1;
            i := i + 1;
            c_villa_data(i) := '!             ' || chr(9) || c_desc_substr2;
          ELSE
            i := i + 1;
            c_villa_data(i) := '!            ' || chr(9) || c_description1 || '' ||
                               c_rev_description1;
          END IF;
        END LOOP;
        i := i + 1;
        c_villa_data(i) := '!     ';
        /*For level 3*/
        i := i + 1;
        c_villa_data(i) := '!              Level3                                                Labellist';
        n_level_cnt := 0;
        n_level_cnt := Pkg_Villa_Interface.Fnc_Level_Cnt(pn_pro_rel_id, 3);
        FOR j IN 1 .. n_level_cnt LOOP
          c_description1     := Pkg_Villa_Interface.fnc_excel_config_desc(pn_pro_rel_id,
                                                                          3,
                                                                          j);
          c_rev_description1 := Pkg_Villa_Interface.Fnc_Excel_Rev_Desc(pn_pro_rel_id,
                                                                       3,
                                                                       j);
          IF Length(c_description1) < 100 THEN
            c_description1 := rpad(c_description1, 100, ' ');
            i := i + 1;
            c_villa_data(i) := '!            ' || chr(9) || c_description1 || '' ||
                               c_rev_description1;
          ELSIF Length(c_description1) > 100 THEN
            c_desc_substr1 := Substr(c_description1, 1, 90);
            c_desc_substr1 := rpad(c_desc_substr1, 100, ' ');
            c_desc_substr2 := Substr(c_description1,
                                     91,
                                     Length(c_desc_substr1));
            i := i + 1;
            c_villa_data(i) := '!            ' || chr(9) || c_desc_substr1 || '' ||
                               c_rev_description1;
            i := i + 1;
            c_villa_data(i) := '!             ' || chr(9) || c_desc_substr2;
          ELSE
            i := i + 1;
            c_villa_data(i) := '!            ' || chr(9) || c_description1 || '' ||
                               c_rev_description1;
          END IF;
        END LOOP;
        i := i + 1;
        c_villa_data(i) := '!     ';
        /*For level 4*/
        i := i + 1;
        c_villa_data(i) := '!              Level4                                                Labellist';
        n_level_cnt := 0;
        n_level_cnt := Pkg_Villa_Interface.Fnc_Level_Cnt(pn_pro_rel_id, 4);
        FOR j IN 1 .. n_level_cnt LOOP
          c_description1     := Pkg_Villa_Interface.fnc_excel_config_desc(pn_pro_rel_id,
                                                                          4,
                                                                          j);
          c_rev_description1 := Pkg_Villa_Interface.Fnc_Excel_Rev_Desc(pn_pro_rel_id,
                                                                       4,
                                                                       j);
          IF Length(c_description1) < 100 THEN
            c_description1 := rpad(c_description1, 100, ' ');
            i := i + 1;
            c_villa_data(i) := '!            ' || chr(9) || c_description1 || '' ||
                               c_rev_description1;
          ELSIF Length(c_description1) > 100 THEN
            c_desc_substr1 := Substr(c_description1, 1, 90);
            c_desc_substr1 := rpad(c_desc_substr1, 100, ' ');
            c_desc_substr2 := Substr(c_description1,
                                     91,
                                     Length(c_desc_substr1));
            i := i + 1;
            c_villa_data(i) := '!            ' || chr(9) || c_desc_substr1 || '' ||
                               c_rev_description1;
            i := i + 1;
            c_villa_data(i) := '!             ' || chr(9) || c_desc_substr2;
          ELSE
            i := i + 1;
            c_villa_data(i) := '!            ' || chr(9) || c_description1 || '' ||
                               c_rev_description1;
          END IF;
        END LOOP;
        i := i + 1;
        c_villa_data(i) := '!     ';
        /*For level 5*/
        i := i + 1;
        c_villa_data(i) := '!              Level5                                                Labellist';
        n_level_cnt := 0;
        n_level_cnt := Pkg_Villa_Interface.Fnc_Level_Cnt(pn_pro_rel_id, 5);
        FOR j IN 1 .. n_level_cnt LOOP
          c_description1     := Pkg_Villa_Interface.fnc_excel_config_desc(pn_pro_rel_id,
                                                                          5,
                                                                          j);
          c_rev_description1 := Pkg_Villa_Interface.Fnc_Excel_Rev_Desc(pn_pro_rel_id,
                                                                       5,
                                                                       j);
          IF Length(c_description1) < 100 THEN
            c_description1 := rpad(c_description1, 100, ' ');
            i := i + 1;
            c_villa_data(i) := '!            ' || chr(9) || c_description1 || '' ||
                               c_rev_description1;
          ELSIF Length(c_description1) > 100 THEN
            c_desc_substr1 := Substr(c_description1, 1, 90);
            c_desc_substr1 := rpad(c_desc_substr1, 100, ' ');
            c_desc_substr2 := Substr(c_description1,
                                     91,
                                     Length(c_desc_substr1));
            i := i + 1;
            c_villa_data(i) := '!            ' || chr(9) || c_desc_substr1 || '' ||
                               c_rev_description1;
            i := i + 1;
            c_villa_data(i) := '!             ' || chr(9) || c_desc_substr2;
          ELSE
            i := i + 1;
            c_villa_data(i) := '!            ' || chr(9) || c_description1 || '' ||
                               c_rev_description1;
          END IF;
        END LOOP;
      ELSE
        /*If release is not a configured release then fetch individual data*/
        /*To find the node id*/
        n_node := Get_Node_Id(pn_pro_rel_id);
        /*To find the path*/
        c_description1 := Ldb2_Full_Scope(n_node);
        /*To fidn the description*/
        c_rev_description1 := Get_Rev_Description(pn_pro_rel_id);
        /*Displaying the data*/
        i := i + 1;
        c_villa_data(i) := '!  Scope' || chr(9) || ':' || 'Level1' ||
                           chr(9) || chr(9) || chr(9) || chr(9) || chr(9) ||
                           chr(9) || chr(9) || chr(9) || chr(9) ||
                           'Labellist';
        i := i + 1;
        c_villa_data(i) := '!            ' || chr(9) || c_description1 ||
                           '        ' || '   ' || c_rev_description1;
        i := i + 1;
        c_villa_data(i) := '!       ';
        i := i + 1;
        c_villa_data(i) := '!       ' || chr(9) || ':' || 'Level2' ||
                           chr(9) || chr(9) || chr(9) || chr(9) || chr(9) ||
                           chr(9) || chr(9) || chr(9) || chr(9) ||
                           'Labellist';
        i := i + 1;
        c_villa_data(i) := '!       ';
        i := i + 1;
        c_villa_data(i) := '!       ' || chr(9) || ':' || 'Level3' ||
                           chr(9) || chr(9) || chr(9) || chr(9) || chr(9) ||
                           chr(9) || chr(9) || chr(9) || chr(9) ||
                           'Labellist';
        i := i + 1;
        c_villa_data(i) := '!       ';
        i := i + 1;
        c_villa_data(i) := '!       ' || chr(9) || ':' || 'Level4' ||
                           chr(9) || chr(9) || chr(9) || chr(9) || chr(9) ||
                           chr(9) || chr(9) || chr(9) || chr(9) ||
                           'Labellist';
        i := i + 1;
        c_villa_data(i) := '!       ';
        i := i + 1;
        c_villa_data(i) := '!       ' || chr(9) || ':' || 'Level5' ||
                           chr(9) || chr(9) || chr(9) || chr(9) || chr(9) ||
                           chr(9) || chr(9) || chr(9) || chr(9) ||
                           'Labellist';
      END IF;
      IF NOT pb_temporary THEN
        i := i + 1;
        c_villa_data(i) := '! ';
        i := i + 1;
        c_villa_data(i) := '!  Release' || chr(9) || ':' || c_rel.rel_id || ' ' ||
                           replace(c_rel.rel_description, chr(10));
        i := i + 1;
        c_villa_data(i) := '! ';
        i := i + 1;
        c_villa_data(i) := '!  Cases' || chr(9) || ':' || c_cases;
      END IF;
      i := i + 1;
      c_villa_data(i) := '!  ';
      i := i + 1;
      c_villa_data(i) := '!  Created On' || chr(9) || ':' || sysdate;
      i := i + 1;
      c_villa_data(i) := '! ';
      i := i + 1;
      c_villa_data(i) := '!  Created By' || chr(9) || ':' || user;
      i := i + 1;
      c_villa_data(i) := '! ';
      i := i + 1;
      c_villa_data(i) := '!  ECU-Type' || chr(9) || ':' || c_ecutype;
      i := i + 1;
      c_villa_data(i) := '! ';
      i := i + 1;
      c_villa_data(i) := '!  ' || c_releasetyp;
      i := i + 1;
      c_villa_data(i) := '! ';
      i := i + 1;
      c_villa_data(i) := '!  Features' || chr(9) || ':' || c_comp;
      i := i + 1;
      c_villa_data(i) := '!  ============================================================================================';
      i := i + 1;
      c_villa_data(i) := ' ';
      i := i + 1;
      c_villa_data(i) := ' ';
      IF c_rel.global_ssdheader IS NOT NULL THEN
        i := i + 1;
        c_villa_data(i) := '   ' || c_rel.global_ssdheader;
        i := i + 1;
        c_villa_data(i) := ' ';
      END IF;
      IF c_rel.global_bypass = 'Y' THEN
        -- V4.0 to insert respective global bypass check type;
        IF c_ecutype = 'EDC16' THEN
          c_global_type := '*_';
        ELSE
          c_global_type := '*';
        END IF;
        i := i + 1;
        c_villa_data(i) := '   ' || c_global_type || 'Channel, >=0, <=0';
        i := i + 1;
        c_villa_data(i) := ' ';
        i := i + 1;
        c_villa_data(i) := '   ' || c_global_type || 'Vector, >=0, <=0';
        i := i + 1;
        c_villa_data(i) := ' ';
      END IF;
    END LOOP;
    /*************************************************************************************************************/
    /*To include SSD data*/
    FOR c_ssd_rec IN c_ssd LOOP
      /*To fetch advanced formula as it is clob field and cannot be queried directly from the select statement*/
      c_new_formula_Desc := pkg_clob.Get_Formdesc(c_ssd_rec.lab_obj_id,
                                                  c_ssd_rec.lab_rev_id);
      IF c_new_formula_desc = '-2' THEN
        c_error_text := 'Unable to fetch the advanced formula for ' ||
                        'obj_id(' || c_ssd_rec.lab_obj_id ||
                        ') and RevisionID(' || c_ssd_rec.lab_rev_id || ')';
        RAISE except_file;
      ELSE
        c_default := 'N';
      END IF;
      c_nachlabel_frm := ', ' || c_ssd_rec.formula;
      c_nachlabel := null;
      i := i + 1;
      c_villa_data(i) := '#usecase ' || c_ssd_rec.cases;
      i := i + 1;
      c_villa_data(i) := '#contact "' || c_ssd_rec.contact || '"';
      i := i + 1;
      c_villa_data(i) := ' ';
      /*
              Convert the DCM data to SSD data if the checkbox is selected
      */
      IF c_ssd_rec.dcm2ssd = 'Y' THEN
        dcm2ssd_data := pkg_dcm2ssd.convert_dcm2ssd(c_ssd_rec.lab_obj_id,
                                                    c_ssd_rec.lab_rev_id);
        FOR j IN 1 .. dcm2ssd_data.count LOOP
          i := i + 1;
          c_villa_data(i) := dcm2ssd_data(j);
        END LOOP;
      END IF;
      IF c_ssd_rec.min_wert IS NOT NULL THEN
        c_nachlabel := ', >=' ||
                       replace(to_char(c_ssd_rec.min_wert), ',', '.');
      END IF;
      IF c_ssd_rec.max_wert IS NOT NULL THEN
        c_nachlabel := c_nachlabel || ', <=' ||
                       replace(to_char(c_ssd_rec.max_wert), ',', '.');
      END IF;
      IF (c_ssd_rec.min_wert IS NULL AND c_ssd_rec.max_wert IS NULL AND
         c_ssd_rec.typ IS NOT NULL) THEN
        c_default   := 'Y';
        c_nachlabel := ', =' || replace(to_char(c_ssd_rec.typ), ',', '.');
      END IF;
      /* if min ,max and formula and advanced formula are null then only default is considered in the file */
      IF c_default = 'Y' THEN
        IF c_ssd_rec.formula IS NULL AND c_new_formula_desc IS NULL AND
           c_nachlabel IS NOT NULL THEN
          i := i + 1;
          c_villa_data(i) := '   ' || c_ssd_rec.label || c_nachlabel;
          i := i + 1;
          c_villa_data(i) := ' ';
        END IF;
      ELSE
        IF c_nachlabel IS NOT NULL THEN
          i := i + 1;
          c_villa_data(i) := '   ' || c_ssd_rec.label || c_nachlabel;
          i := i + 1;
          c_villa_data(i) := ' ';
        END IF;
      END IF;
      IF c_ssd_rec.formula IS NOT NULL THEN
        i := i + 1;
        c_villa_data(i) := '   ' || c_ssd_rec.label || ', ' ||
                           c_ssd_rec.formula;
        i := i + 1;
        c_villa_data(i) := ' ';
      END IF;
      IF c_new_formula_desc IS NOT NULL THEN
        i := i + 1;
        /*
                PK 09.03.2005, FEDab40788, pb_comment is used to generate autocomments for advance formula
        */
        IF pb_comment THEN
          c_villa_data(i) := '!##################### Begin of ' ||
                             c_ssd_rec.label || ' #######################';
          i := i + 1;
          c_villa_data(i) := ' ';
          i := i + 1;
          c_villa_data(i) := '   ' || 'BEGINLINE' || c_new_formula_desc;
          i := i + 1;
          c_villa_data(i) := ' ';
          i := i + 1;
          c_villa_data(i) := '!##################### End of ' ||
                             c_ssd_rec.label || ' #######################';
        ELSE
          c_villa_data(i) := '   ' || c_new_formula_desc;
        END IF;
        i := i + 1;
        c_villa_data(i) := ' ';
      END IF;
      i := i + 1;
      c_villa_data(i) := '#endcontact ';
      i := i + 1;
      c_villa_data(i) := '#endusecase';
      i := i + 1;
      c_villa_data(i) := ' ';
    END LOOP;
    pc_ssd_data := c_villa_data;
    SetReleaseID(pn_pro_rel_id);
    RETURN TRUE;
  EXCEPTION
    WHEN except_file THEN
      Pkg_Error_Logging.RegisterError(c_error_text,
                                      SQLERRM,
                                      USER,
                                      SYSDATE,
                                      'Pkg_Villa_Interface');
      RETURN FALSE;
    WHEN OTHERS THEN
      Pkg_Error_Logging.RegisterError('Error occurred  from the function SSD_File',
                                      SQLERRM,
                                      USER,
                                      SYSDATE,
                                      'Pkg_Villa_Interface');
      RETURN FALSE;
  END SSD_File_Label;
  /************************************************************************************************************/
  -- Shankar 18.08.2003
  PROCEDURE SetReleaseID(A_ReleaseID IN NUMBER) IS
  BEGIN
    G_ReleaseID := A_ReleaseID;
  END;
  -- Shankar 18.08.2003
  PROCEDURE CreateSSDFile(A_ReleaseID IN NUMBER,
                          A_SSDData   OUT c_villa_data%TYPE) IS
  BEGIN
    IF G_ReleaseID = A_ReleaseID THEN
      A_SSDData := c_villa_data;
    ELSE
      SSD_File(A_ReleaseID, A_SSDData);
    END IF;
  END;
  /****************************************************************************************************************/
  FUNCTION GetSSDCase(pn_pro_rel_id IN NUMBER, pc_label VARCHAR2)
    RETURN VARCHAR2 IS
    /***************************************************************************
    Purpose: To fecth the case for the giben release and label
    Parameters: In:Pn_pro_rel_id---Release id
                   Pc_label--------Label
                      OUT:Pc_case--------Case of the label
    History: Sp,26-Aug-03,For Villa
    ***************************************************************************/
    c_case VARCHAR2(32) := null;
    cursor cur_getCase is
      SELECT ssd_cases
        FROM v_villa_rel_labels
       WHERE pro_rel_id = pn_pro_rel_id
         AND upper_label = upper(pc_label);
    cursor cur_getClass is
      SELECT nvl(ssd_class, '???')
        FROM v_ldb2_common_label
       WHERE upper_label = upper(pc_label);
  BEGIN
    open cur_getCase;
    fetch cur_getCase
      into c_case;
    if cur_getCase%notfound then
      open cur_getClass;
      fetch cur_getClass
        into c_case;
      if cur_getClass%notfound then
        Pkg_Error_Logging.RegisterError('Error occurred  from the procedure SSD_Case. ' ||
                                        'Unable to fetch the case for the given label (' ||
                                        pc_label || ')' || ', Pro rel id ' ||
                                        pn_pro_rel_id,
                                        SQLERRM,
                                        USER,
                                        SYSDATE,
                                        'Pkg_Villa_Interface');
        RETURN 'ERR';
      end if;
      close cur_getClass;
    end if;
    close cur_getCase;
    RETURN c_case;
  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      Pkg_Error_Logging.RegisterError('Error occurred  from the procedure SSD_Case. ' ||
                                      'Unable to fetch the case for the given label (' ||
                                      pc_label || ')' || ', Pro rel id ' ||
                                      pn_pro_rel_id,
                                      SQLERRM,
                                      USER,
                                      SYSDATE,
                                      'Pkg_Villa_Interface');
      Pkg_Error_Logging.SyncMessage;
      RETURN 'ERR';
    WHEN OTHERS THEN
      Pkg_Error_Logging.RegisterError('Error occurred  from the procedure SSD_Case',
                                      SQLERRM,
                                      USER,
                                      SYSDATE,
                                      'Pkg_Villa_Interface');
      Raise_Application_Error(-20003,
                              'Error occurred while fetching SSD case from GetSSDCase for pro_rel_id(' ||
                              pn_pro_rel_id || ') ' || sqlerrm);
  END GetSSDCase;
  /****************************************************************************************************************/
  FUNCTION GetECUType(pn_pro_rel_id NUMBER) RETURN VARCHAR2 IS
    /****************************************************************************************************************
            Purpose    : To fecth the case for the giben release and label
        Parameters : In:Pn_pro_rel_id---Release id
                    OUT:ECU Type
        History    : PK V4.0, To generate SSD File based on the ECU Type either EDC16 or EDC17
    ***************************************************************************************************************/
    n_node_id NUMBER;
  BEGIN
    SELECT node_id
      INTO n_node_id
      FROM v_ldb2_object_tree      tree,
           v_ldb2_sw_vers          vers,
           v_ldb2_project_revision rev,
           v_ldb2_project_release  rel
     WHERE rel.pro_rel_id = pn_pro_rel_id
       AND rel.pro_rev_id = rev.pro_rev_id
       AND rev.vers_id = vers.vers_id
       AND vers.villa_swvers_id = tree.object_id;
    RETURN ldb2_get_ecutype(n_node_id, 8);
  EXCEPTION
    WHEN OTHERS THEN
      RETURN NULL;
  END;
  /****************************************************************************************************************/
  FUNCTION GetSSDFilename(pn_pro_rel_id NUMBER,
                          p_MFT         VARCHAR2 DEFAULT 'MFT',
                          p_SSD         VARCHAR2 DEFAULT 'SSD',
                          p_APP_CHK     VARCHAR2 DEFAULT 'App-Chk')
    RETURN VARCHAR2 IS
    /****************************************************************************************************************
            Purpose    : To get the SSD filename for respective release
        Parameters : In:Pn_pro_rel_id---Release id
                    OUT:SSD filename
        History    : PK V4.4, To get the SSD Filename for respective release
                     PK V4.7, RCMS00308576 Standardise filename for the Release outputs
                              Removed the description fields and replaced with SW-Ver No and Project Code
                     SK V5.1  RCMS00457889 Name of .ssd-file depends on selected usecases MFT, SSD, APP
    ***************************************************************************************************************/
    c_sw_decr  V_LDB2_VILLA_SWVERS.VERSIONNUMBER%TYPE;
    c_prj_decr V_LDB2_VILLA_PROJECTS.CODE%TYPE;
    n_rel_id   V_LDB2_PROJECT_RELEASE.REL_ID%TYPE;
    n_rev_id   V_LDB2_PROJECT_REVISION.REV_ID%TYPE;
    c_filename VARCHAR2(500);
    lv_case    VARCHAR2(50);
  BEGIN
    SELECT VPRJ.CODE, VSWVER.VERSIONNUMBER, REV.REV_ID, REL.REL_ID
      INTO c_prj_decr, c_sw_decr, n_rev_id, n_rel_id
      FROM V_LDB2_PROJECT_RELEASE  REL,
           V_LDB2_PROJECT_REVISION REV,
           V_LDB2_SW_VERS          SWVER,
           V_LDB2_VILLA_PROJECTS   VPRJ,
           V_LDB2_VILLA_SWVERS     VSWVER
     WHERE REL.PRO_REV_ID = REV.PRO_REV_ID
       AND REV.VERS_ID = SWVER.VERS_ID
       AND SWVER.VILLA_SWVERS_ID = VSWVER.ID
       AND VSWVER.FK_PROJECTID = VPRJ.ID
       AND REL.PRO_REL_ID = pn_pro_rel_id;
    IF p_MFT = 'MFT' THEN
      lv_case := '_MFT';
    END IF;
    IF p_SSD = 'SSD' THEN
      lv_case := lv_case || '_SSD';
    END IF;
    IF p_APP_CHK = 'App-Chk' THEN
      lv_case := lv_case || '_APP';
    END IF;
    IF p_MFT IS NULL AND p_SSD IS NULL AND p_APP_CHK IS NULL THEN
      lv_case := '_MFT_SSD_APP';
    END IF;
    c_filename := REPLACE('SSD_' ||
                          replace(replace(c_prj_decr, chr(10)), ' ', '_') || '_' ||
                          replace(replace(c_sw_decr, chr(10)), ' ', '_') || '_' ||
                          to_char(n_rev_id) || '_' || to_char(n_rel_id) ||
                          lv_case || '.ssd',
                          '/',
                          '_');
    c_filename := Translate(c_filename,
                            'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_:,.+ÄËÖÜäëöüß',
                            'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_:,.+');
    RETURN c_filename;
  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      RETURN 'SSD_invalid_filename_for_release.SSD';
    WHEN OTHERS THEN
      RETURN 'SSD_invalid_filename_for_release.SSD';
  END;
  /****************************************************************************************************************/
  FUNCTION get_feature_values(pn_pro_rel_id NUMBER) RETURN VARCHAR2 is
    /****************************************************************************************************************
            Purpose    : To get the feature and values for respective release as a string
        Parameters      In : Pn_pro_rel_id    ---Release id
                    OUT : Feature and Values description for a release
        History    : PK V4.5, RCMS00046790
                                 To get feature and Values for respective release
    ****************************************************************************************************************/
    /*cursor to fetch the components for the release*/
    CURSOR cur_rel_comp IS
      SELECT feature_text, value_text
        FROM v_ldb2_prj_rel_comp
       WHERE pro_rel_id = pn_pro_rel_id
       ORDER BY feature_text, value_text;
    c_comp VARCHAR2(20000);
  BEGIN
    /*To fetch the components detail for the given release id*/
    FOR Rec_Release_Component IN cur_rel_comp LOOP
      IF c_comp IS NULL THEN
        c_comp := Rec_Release_Component.Feature_text || '{' ||
                  Rec_Release_Component.Value_Text || '}';
      ELSE
        c_comp := c_comp || chr(10) || '!' || chr(9) || chr(9) || ' ' ||
                  Rec_Release_Component.Feature_text || '{' ||
                  Rec_Release_Component.Value_Text || '}';
      END IF;
    END LOOP;
    RETURN c_comp;
  EXCEPTION
    WHEN OTHERS THEN
      Pkg_Error_Logging.RegisterError('Error occurred  while fetching the components detail from the function get_feature_values',
                                      SQLERRM,
                                      USER,
                                      SYSDATE,
                                      'Pkg_Villa_Interface');
      RETURN 'Error fetching Feature and Values';
  END;

  FUNCTION Data_Split(p_in_string VARCHAR2, p_delim VARCHAR2)
    RETURN fea_val_array IS
    i      number := 0;
    pos    number := 0;
    lv_str varchar2(32000) := p_in_string;

    strings fea_val_array;

  BEGIN

    -- determine first chuck of string
    pos := instr(lv_str, p_delim, 1, 1);

    -- while there are chunks left, loop
    WHILE (pos != 0) LOOP

      -- increment counter
      i := i + 1;

      -- create array element for chuck of string
      strings(i) := substr(lv_str, 1, pos);

      -- remove chunk from string
      lv_str := substr(lv_str, pos + 1, length(lv_str));

      -- determine next chunk
      pos := instr(lv_str, p_delim, 1, 1);

      -- no last chunk, add to array
      IF pos = 0 THEN

        strings(i + 1) := lv_str;

      END IF;

    END LOOP;

    -- return array
    RETURN strings;

  END Data_Split;
  /***************************************************************************
  Purpose: To save the SSD output to PL/SQl table for the provided node id
           and list of labels
  Parameters: In:  pb_node_id---nodeId of Review(SSD)
              OUT: pb_comment--- to get the comments true or false
  Returns:  Procedure which returns the data which contains the rules
  History: Created for SSD-Icdm Interface. to create .ssd file for the
           list of labels given as and input and stored in temporary table
  ***************************************************************************/
  PROCEDURE SSD_File_ICDM(pc_ssd_data OUT c_villa_data%TYPE,
                          pb_node_id  number,
                          pb_comment  BOOLEAN DEFAULT FALSE) IS

    /*labels inserted in temp table.To get the lab id of the labels from v_ldb2_pavast*/
    CURSOR c_label IS

      SELECT *
        FROM V_LDB2_PAVAST
       WHERE UPPER_LABEL IN (SELECT LABEL FROM TEMP_LABELLIST_INTERFACE);
    /*to get the ssd data for the labels present in temp table and node_id */
    CURSOR c_ssd(pn_lab_id number) IS
      SELECT distinct lab_obj_id,
                      typ,
                      max_wert,
                      min_wert,
                      formula formula,
                      ssd_grp_label,
                      func_label,
                      label,
                      lab_rev_id,
                      dcm2ssd,
                      cases,
                      contact
        FROM V_LDB2_ICDM_SSD
       WHERE node_id = pb_node_id
         and lab_lab_id = pn_lab_id
         and cases IN ('Review')
       ORDER BY ssd_grp_label, func_label, label;

    c_rel_typ           VARCHAR2(1);
    b_errors            BOOLEAN := FALSE;
    c_releasetyp        VARCHAR2(500) := 'Only internal Release';
    c_ssd_grp_label_old v_ldb2_ssd_grp.label%TYPE := '__xxxxx__';
    c_func_label_old    v_ldb2_pavast.function%TYPE := '__xxxxx__';
    c_nachlabel         VARCHAR2(1010);
    c_nachlabel_frm     VARCHAR2(1010);
    n_count_grp         NUMBER := 0;
    n_count_func        NUMBER := 0;
    c_comp              VARCHAR2(20000);
    c_feature           v_ldb2_features.feature_text%TYPE;
    c_value             v_ldb2_values.value_text%TYPE;
    c_default           CHAR;
    c_rev_data1         VARCHAR2(4000);
    c_rev_data2         VARCHAR2(4000);
    c_rev_data3         VARCHAR2(4000);
    c_rev_data          VARCHAR2(4000);
    c_new_formula_desc  VARCHAR2(32767);
    i                   BINARY_INTEGER := 1;
    c_cases             VARCHAR2(200);
    LI_Counter          NUMBER;
    n_rev_cnt           NUMBER := 0;
    c_global_chk        CHAR;
    c_global_type       VARCHAR2(10);
    c_ecutype           VARCHAR2(20);
    c_global_ssdheader  v_ldb2_project_release.global_ssdheader%TYPE;
    dcm2ssd_data        pkg_dcm2ssd.ssd_file;
    n_config_cnt        NUMBER;
    n_level_cnt         NUMBER;
    c_description1      VARCHAR2(32000);
    c_rev_description1  VARCHAR2(32000);
    n_node              v_ldb2_object_tree.node_id%type;
    c_desc_substr1      VARCHAR2(10000);
    c_desc_substr2      VARCHAR2(10000);

  BEGIN
    c_villa_data.DELETE;
    c_villa_data(i) := '!  SSD-Report for ' || c_ecutype ||
                       ' Software, SSD-Labeldatabase, for ICDM';
    i := i + 1;
    c_villa_data(i) := '!  ============================================================================================';
    i := i + 1;
    c_villa_data(i) := ' ';
    i := i + 1;
    c_villa_data(i) := '!  System-spezifische Software-Daten';
    /*To include SSD data*/
    FOR c_label_rec IN c_label LOOP
      dbms_output.put_line('ssss');
      FOR c_ssd_rec IN c_ssd(c_label_rec.LAB_ID) LOOP
        dbms_output.put_line(c_label_rec.LAB_ID || 'labId');
        dbms_output.put_line(pb_node_id || '   node id');
        /*To fetch advanced formula as it is clob field and cannot be queried directly from the select statement*/
        c_new_formula_Desc := pkg_clob.Get_Formdesc(c_ssd_rec.lab_obj_id,
                                                    c_ssd_rec.lab_rev_id);

        DBMS_OUTPUT.PUT_LINE('c_ssd_rec' || c_ssd_rec.lab_obj_id);
        IF c_new_formula_desc = '-2' THEN
          NULL;
        ELSE
          c_default := 'N';
          IF c_ssd_rec.ssd_grp_label != c_ssd_grp_label_old THEN
            n_count_grp := n_count_grp + 1;
            c_ssd_grp_label_old := c_ssd_rec.ssd_grp_label;
            c_func_label_old := '__xxxxxx__';
            n_count_func := 0;
            i := i + 1;
            c_villa_data(i) := ' ';
            i := i + 1;
            c_villa_data(i) := '! ' || to_char(n_count_grp) || '. ' ||
                               c_ssd_rec.ssd_grp_label;
            i := i + 1;
            c_villa_data(i) := '! =========================================================';
            i := i + 1;
            c_villa_data(i) := ' ';
          END IF;
          IF c_ssd_rec.func_label != c_func_label_old THEN
            n_count_func := n_count_func + 1;
            c_func_label_old := c_ssd_rec.func_label;
            i := i + 1;
            c_villa_data(i) := '! ' || to_char(n_count_grp) || '.' ||
                               to_char(n_count_func) || ' ' ||
                               c_ssd_rec.func_label;
            i := i + 1;
            c_villa_data(i) := '! ---------------------------------------------------------';
            i := i + 1;
            c_villa_data(i) := ' ';
          END IF;

          i := i + 1;

          c_villa_data(i) := '#usecase ' || c_ssd_rec.cases;

          i := i + 1;
          c_villa_data(i) := '#contact "' || c_ssd_rec.contact || '"';
          i := i + 1;
          c_villa_data(i) := ' ';

          IF c_ssd_rec.dcm2ssd = 'Y' THEN
            dcm2ssd_data := pkg_dcm2ssd.convert_dcm2ssd(c_ssd_rec.lab_obj_id,
                                                        c_ssd_rec.lab_rev_id);
            FOR j IN 1 .. dcm2ssd_data.count LOOP
              i := i + 1;
              c_villa_data(i) := dcm2ssd_data(j);
            END LOOP;
          END IF;
          c_nachlabel_frm := ', ' || c_ssd_rec.formula;
          c_nachlabel     := null;
          IF c_ssd_rec.min_wert IS NOT NULL THEN
            c_nachlabel := ', >=' ||
                           replace(to_char(c_ssd_rec.min_wert), ',', '.');
          END IF;
          IF c_ssd_rec.max_wert IS NOT NULL THEN
            c_nachlabel := c_nachlabel || ', <=' ||
                           replace(to_char(c_ssd_rec.max_wert), ',', '.');
          END IF;
          IF (c_ssd_rec.min_wert IS NULL AND c_ssd_rec.max_wert IS NULL AND
             c_ssd_rec.typ IS NOT NULL) THEN
            c_default   := 'Y';
            c_nachlabel := ', =' ||
                           replace(to_char(c_ssd_rec.typ), ',', '.');
          END IF;
          /* if min ,max and formula and advanced formula are null then only default is considered in the file */
          IF c_default = 'Y' THEN
            IF c_ssd_rec.formula IS NULL AND c_new_formula_desc IS NULL AND
               c_nachlabel IS NOT NULL THEN
              i := i + 1;
              c_villa_data(i) := '   ' || c_ssd_rec.label || c_nachlabel;
              i := i + 1;
              c_villa_data(i) := ' ';
            END IF;
          ELSE
            IF c_nachlabel IS NOT NULL THEN
              i := i + 1;
              c_villa_data(i) := '   ' || c_ssd_rec.label || c_nachlabel;
              i := i + 1;
              c_villa_data(i) := ' ';
            END IF;
          END IF;
          IF c_ssd_rec.formula IS NOT NULL THEN
            i := i + 1;
            c_villa_data(i) := '   ' || c_ssd_rec.label || ', ' ||
                               c_ssd_rec.formula;
            i := i + 1;
            c_villa_data(i) := ' ';
          END IF;
          IF c_new_formula_desc IS NOT NULL THEN
            i := i + 1;
            /*
                    PK 09.03.2005, FEDab40788, pb_comment is used to generate autocomments for advance formula
            */
            IF pb_comment THEN
              c_villa_data(i) := '!##################### Begin of ' ||
                                 c_ssd_rec.label ||
                                 ' #######################';
              i := i + 1;
              c_villa_data(i) := ' ';
              i := i + 1;
            END IF;
            c_villa_data(i) := '   ' || c_new_formula_desc;
            /*
                    PK 09.03.2005, FEDab40788, pb_comment is used to generate autocomments for advance formula
            */
            IF pb_comment THEN
              i := i + 1;
              c_villa_data(i) := ' ';
              i := i + 1;
              c_villa_data(i) := '!##################### End of ' ||
                                 c_ssd_rec.label ||
                                 ' #######################';
            END IF;
            i := i + 1;
            c_villa_data(i) := ' ';
          END IF;
          i := i + 1;
          c_villa_data(i) := '#endcontact ';
          i := i + 1;
          c_villa_data(i) := '#endusecase';
          i := i + 1;
          c_villa_data(i) := ' ';
        END IF;
      END LOOP;
    END LOOP;
    pc_ssd_data := c_villa_data;

  END SSD_File_ICDM;

  PROCEDURE SSD_File_Node(pn_pro_rel_id NUMBER,
                          pc_ssd_data   OUT c_villa_data%TYPE,
                          pc_mft        VARCHAR2 DEFAULT 'MFT',
                          pc_ssd        VARCHAR2 DEFAULT 'SSD',
                          pc_appchK     VARCHAR2 DEFAULT 'App-Chk',
                          pc_comp       VARCHAR2 DEFAULT 'Cal4Comp',
                          pb_refresh    BOOLEAN DEFAULT TRUE,
                          pb_comment    BOOLEAN DEFAULT FALSE) IS
    /***************************************************************************
    Purpose: To save the SSD output to PL/SQl table for the provided release id
    Parameters: In:Pn_pro_rel_id---Release id
                  OUT:None
    Returns:  True , incase the function is success else returns false
    History: Sp,6-Aug-03,For Villa
             PK,16.10.03, FEDab33593 Global Bypass Check added for labels with *Vector
             and *Channel, Changed Header format, added parameters for filter
             PK,16.03.04, FEDab33935 Global SSD Header for check SSD file
             PK,09.03.05, FEDab40788 Automatic comments for Advance formula
             PK,19.08.2005 RCMS00046790 Convert DCM2SSD for the labels
             RB,23.08/07 RCMS00447599 Increase of SSD-Release-Config Levels from 3 to 5 levels
             SK RCMS00457889 Name of .ssd-file depends on selected usecases MFT, SSD, APP
    ***************************************************************************/
    /*To fetch the SSD detail for the given release id*/
    /*
        PK RCMS00046790 added dcm2ssd
    */
    CURSOR c_ssd IS
      SELECT distinct lab_obj_id,
                      typ,
                      max_wert,
                      min_wert,
                      formula formula,
                      ssd_grp_label,
                      func_label,
                      label,
                      lab_rev_id,
                      dcm2ssd,
                      cases,
                      contact,
                      Node
        FROM V_LDB2_DCM_SSD_MITNODE
       WHERE pro_rel_id = pn_pro_rel_id
         AND cases IN (pc_mft, pc_ssd, pc_appchk, pc_comp)
       ORDER BY ssd_grp_label, func_label, label;
    /*cursor to fetch the components for the release*/
    CURSOR cur_rel_comp IS
      SELECT feature_text, value_text
        FROM v_ldb2_prj_rel_comp
       WHERE pro_rel_id = pn_pro_rel_id
       ORDER BY feature_text, value_text;
    CURSOR Cur_Rel IS
      SELECT DECODE(rel_typ, 'e', 'External release', 'Internal Release') rel_type,
             cre_date rel_date,
             cre_user rel_user,
             rel_id rel_id,
             description rel_description,
             global_chk global_bypass,
             global_ssdheader
        FROM v_ldb2_project_release
       WHERE pro_rel_id = pn_pro_rel_id;
    CURSOR LCur_Errors is
      SELECT 1
        FROM DUAL
       WHERE EXISTS (SELECT PRO_REL_ID
                FROM v_ldb2_pro_errors
               WHERE pro_rel_id = pn_pro_rel_id);
    LI_Pro_Rel_ID V_LDB2_PRO_ERRORS.PRO_REL_ID % TYPE;
    /*Local Variable declaration*/
    c_rel_typ           VARCHAR2(1);
    b_errors            BOOLEAN := FALSE;
    c_releasetyp        VARCHAR2(500) := 'Only internal Release';
    c_ssd_grp_label_old v_ldb2_ssd_grp.label%TYPE := '__xxxxx__';
    c_func_label_old    v_ldb2_pavast.function%TYPE := '__xxxxx__';
    c_nachlabel         VARCHAR2(1010);
    c_nachlabel_frm     VARCHAR2(1010);
    n_count_grp         NUMBER := 0;
    n_count_func        NUMBER := 0;
    c_comp              VARCHAR2(20000);
    c_feature           v_ldb2_features.feature_text%TYPE;
    c_value             v_ldb2_values.value_text%TYPE;
    c_default           CHAR;
    c_rev_data1         VARCHAR2(4000);
    c_rev_data2         VARCHAR2(4000);
    c_rev_data3         VARCHAR2(4000);
    c_rev_data          VARCHAR2(4000);
    c_new_formula_desc  VARCHAR2(32767);
    i                   BINARY_INTEGER := 1;
    c_cases             VARCHAR2(200);
    LI_Counter          NUMBER;
    n_rev_cnt           NUMBER := 0;
    c_global_chk        CHAR;
    c_global_type       VARCHAR2(10);
    c_ecutype           VARCHAR2(20);
    c_global_ssdheader  v_ldb2_project_release.global_ssdheader%TYPE;
    dcm2ssd_data        pkg_dcm2ssd.ssd_file;
    n_config_cnt        NUMBER;
    n_level_cnt         NUMBER;
    c_description1      VARCHAR2(32000);
    c_rev_description1  VARCHAR2(32000);
    n_node              v_ldb2_object_tree.node_id%type;
    c_desc_substr1      VARCHAR2(10000);
    c_desc_substr2      VARCHAR2(10000);
  BEGIN
    /* Initialize c_villa_data!!
    For Villa the PL/SQL-Table is not cleared incase the query is re-run for same pro_rel_id but
    for labeldatabase every time the package is invoke the PL/SQL-Table is cleared */
    IF pb_refresh THEN
      IF G_ReleaseID <> pn_pro_rel_id THEN
        c_villa_data.DELETE;
      END IF;
    ELSE
      c_villa_data.DELETE;
    END IF;
    /************************************************************************************************************/
    BEGIN
      /*To fetch the components detail for the given release id*/
      FOR Rec_Release_Component IN cur_rel_comp LOOP
        IF c_comp IS NULL THEN
          c_comp := Rec_Release_Component.Feature_text || '{' ||
                    Rec_Release_Component.Value_Text || '}';
        ELSE
          c_comp := c_comp || chr(10) || '!' || chr(9) || chr(9) || ' ' ||
                    Rec_Release_Component.Feature_text || '{' ||
                    Rec_Release_Component.Value_Text || '}';
        END IF;
      END LOOP;
    EXCEPTION
      WHEN OTHERS THEN
        Pkg_Error_Logging.RegisterError('Error occurred  while fetching the components detail from the function SSD_File',
                                        SQLERRM,
                                        USER,
                                        SYSDATE,
                                        'Pkg_Villa_Interface');
        Raise_Application_Error(-20003,
                                'Error occurred while creating SSD File for pro_rel_id(' ||
                                pn_pro_rel_id || ') ' || sqlerrm);
    END;
    /************************************************************************************************************/
    BEGIN
      /*To fecth release detail*/
      -- $$$
      /*
      If external release, then no errors??
      if internal release, then check if errors exist for the release!!
      */
      -- $$$
      -- PK V4.0 To handle global bypass check based on ECU-Types
      c_ecutype := getECUType(pn_pro_rel_id);
      IF c_ecutype IS NULL THEN
        Pkg_Error_Logging.RegisterError('Error occurred  while finding the ECU-Type for the release',
                                        SQLERRM,
                                        USER,
                                        SYSDATE,
                                        'Pkg_Villa_Interface');
      END IF;
      FOR C_Rel IN Cur_Rel LOOP
        IF C_Rel.rel_type = 'Internal Release' THEN
          BEGIN
            OPEN LCur_Errors;
            FETCH LCur_Errors
              INTO LI_Pro_Rel_ID;
            b_Errors := LCur_Errors%FOUND;
            CLOSE LCur_Errors;
          EXCEPTION
            WHEN OTHERS THEN
              IF LCur_Errors%ISOPEN THEN
                CLOSE LCur_Errors;
              END IF;
              Pkg_Error_Logging.RegisterError('Error occurred  while finding the release type from the function SSD_File',
                                              SQLERRM,
                                              USER,
                                              SYSDATE,
                                              'Pkg_Villa_Interface');
          END;
          IF b_errors THEN
            c_releasetyp := 'Only internal Release (with errors)';
          END IF;
        ELSE
          c_releasetyp := 'External release';
        END IF;
        SELECT DECODE(pc_mft, 'MFT', 'MFT', '') ||
               DECODE(pc_ssd, 'SSD', ' SSD', '') ||
               DECODE(pc_appchk, 'App-Chk', ' App-Chk', '') ||
               DECODE(pc_comp, 'Cal4Comp', 'Cal4Comp', '')
          INTO c_cases
          FROM DUAL;
        c_cases := REPLACE(TRIM(c_cases), ' ', ',');
        /*Inserting the values to PL/Sql table*/
        c_villa_data(i) := '!  SSD-Report for ' || c_ecutype ||
                           ' Software, SSD-Labeldatabase, DS/ESQ3';
        i := i + 1;
        c_villa_data(i) := '!  ============================================================================================';
        i := i + 1;
        c_villa_data(i) := ' ';
        i := i + 1;
        c_villa_data(i) := '!  System-spezifische Software-Daten';
        /*Fetching the count for the release*/
        n_config_cnt := CONFIG_RELEASE_CNT(Pn_pro_rel_id);
        /*If it is a configured release then the release detail for all levels are displayed*/
        IF n_config_cnt > 1 THEN
          i := i + 1;
          c_villa_data(i) := '!  Scope    Level1                                                Labellist';
          /*Sp, V4.5*/
          /*Finding count for each level and looping through the data*/
          n_level_cnt := Pkg_Villa_Interface.Fnc_Level_Cnt(pn_pro_rel_id, 1);
          FOR j IN 1 .. n_level_cnt LOOP
            c_description1     := Pkg_Villa_Interface.fnc_excel_config_desc(pn_pro_rel_id,
                                                                            1,
                                                                            j);
            c_rev_description1 := Pkg_Villa_Interface.Fnc_Excel_Rev_Desc(pn_pro_rel_id,
                                                                         1,
                                                                         j);
            IF Length(c_description1) < 100 THEN
              c_description1 := rpad(c_description1, 100, ' ');
              i := i + 1;
              c_villa_data(i) := '!             ' || c_description1 || '' ||
                                 c_rev_description1;
            ELSIF Length(c_description1) > 100 THEN
              c_desc_substr1 := Substr(c_description1, 1, 90);
              FOR n in 90 .. 100 LOOP
                c_desc_substr1 := c_desc_substr1 || ' ';
              END LOOP;
              c_desc_substr2 := Substr(c_description1,
                                       91,
                                       Length(c_desc_substr1));
              i := i + 1;
              c_villa_data(i) := '!            ' || chr(9) ||
                                 c_desc_substr1 || '' || c_rev_description1;
              i := i + 1;
              c_villa_data(i) := '!            ' || chr(9) ||
                                 c_desc_substr2;
            ELSE
              i := i + 1;
              c_villa_data(i) := '!             ' || c_description1 || '' ||
                                 c_rev_description1;
            END IF;
          END LOOP;
          i := i + 1;
          c_villa_data(i) := '! ';
          /*For level 2*/
          i := i + 1;
          c_villa_data(i) := '!           Level2                                                Labellist';
          /*Resetting the cnt to 0*/
          n_level_cnt := 0;
          /*Finding the cnt for level2 */
          /*Finding count for each level and looping through the data*/
          n_level_cnt := Pkg_Villa_Interface.Fnc_Level_Cnt(pn_pro_rel_id, 2);
          FOR j IN 1 .. n_level_cnt LOOP
            c_description1     := Pkg_Villa_Interface.fnc_excel_config_desc(pn_pro_rel_id,
                                                                            2,
                                                                            j);
            c_rev_description1 := Pkg_Villa_Interface.Fnc_Excel_Rev_Desc(pn_pro_rel_id,
                                                                         2,
                                                                         j);
            IF Length(c_description1) < 100 THEN
              c_description1 := rpad(c_description1, 100, ' ');
              i := i + 1;
              c_villa_data(i) := '!             ' || c_description1 || '' ||
                                 c_rev_description1;
            ELSIF Length(c_description1) > 100 THEN
              c_desc_substr1 := Substr(c_description1, 1, 90);
              FOR n in 90 .. 100 LOOP
                c_desc_substr1 := c_desc_substr1 || ' ';
              END LOOP;
              c_desc_substr2 := Substr(c_description1,
                                       91,
                                       Length(c_desc_substr1));
              i := i + 1;
              c_villa_data(i) := '!            ' || chr(9) ||
                                 c_desc_substr1 || '' || c_rev_description1;
              i := i + 1;
              c_villa_data(i) := '!            ' || chr(9) ||
                                 c_desc_substr2;
            ELSE
              i := i + 1;
              c_villa_data(i) := '!             ' || c_description1 || '' ||
                                 c_rev_description1;
            END IF;
          END LOOP;
          i := i + 1;
          c_villa_data(i) := '! ';
          /*For level 3*/
          i := i + 1;
          c_villa_data(i) := '!           Level3                                                Labellist';
          n_level_cnt := 0;
          /*Fetching the count for level 3*/
          n_level_cnt := Pkg_Villa_Interface.Fnc_Level_Cnt(pn_pro_rel_id, 3);
          FOR j IN 1 .. n_level_cnt LOOP
            c_description1     := Pkg_Villa_Interface.fnc_excel_config_desc(pn_pro_rel_id,
                                                                            3,
                                                                            j);
            c_rev_description1 := Pkg_Villa_Interface.Fnc_Excel_Rev_Desc(pn_pro_rel_id,
                                                                         3,
                                                                         j);
            IF Length(c_description1) < 100 THEN
              c_description1 := rpad(c_description1, 100, ' ');
              i := i + 1;
              c_villa_data(i) := '!             ' || c_description1 || '' ||
                                 c_rev_description1;
            ELSIF Length(c_description1) > 100 THEN
              c_desc_substr1 := Substr(c_description1, 1, 90);
              FOR n in 90 .. 100 LOOP
                c_desc_substr1 := c_desc_substr1 || ' ';
              END LOOP;
              c_desc_substr2 := Substr(c_description1,
                                       91,
                                       Length(c_desc_substr1));
              i := i + 1;
              c_villa_data(i) := '!            ' || chr(9) ||
                                 c_desc_substr1 || '' || c_rev_description1;
              i := i + 1;
              c_villa_data(i) := '!            ' || chr(9) ||
                                 c_desc_substr2;
            ELSE
              i := i + 1;
              c_villa_data(i) := '!             ' || c_description1 || '' ||
                                 c_rev_description1;
            END IF;
          END LOOP;
          --RB,23.08/07 RCMS00447599 Increase of SSD-Release-Config Levels from 3 to 5 levels
          i := i + 1;
          c_villa_data(i) := '! ';
          /*For level 4*/
          i := i + 1;
          c_villa_data(i) := '!           Level4                                                Labellist';
          n_level_cnt := 0;
          /*Fetching the count for level 4*/
          n_level_cnt := Pkg_Villa_Interface.Fnc_Level_Cnt(pn_pro_rel_id, 4);
          FOR j IN 1 .. n_level_cnt LOOP
            c_description1     := Pkg_Villa_Interface.fnc_excel_config_desc(pn_pro_rel_id,
                                                                            4,
                                                                            j);
            c_rev_description1 := Pkg_Villa_Interface.Fnc_Excel_Rev_Desc(pn_pro_rel_id,
                                                                         4,
                                                                         j);
            IF Length(c_description1) < 100 THEN
              c_description1 := rpad(c_description1, 100, ' ');
              i := i + 1;
              c_villa_data(i) := '!             ' || c_description1 || '' ||
                                 c_rev_description1;
            ELSIF Length(c_description1) > 100 THEN
              c_desc_substr1 := Substr(c_description1, 1, 90);
              FOR n in 90 .. 100 LOOP
                c_desc_substr1 := c_desc_substr1 || ' ';
              END LOOP;
              c_desc_substr2 := Substr(c_description1,
                                       91,
                                       Length(c_desc_substr1));
              i := i + 1;
              c_villa_data(i) := '!            ' || chr(9) ||
                                 c_desc_substr1 || '' || c_rev_description1;
              i := i + 1;
              c_villa_data(i) := '!            ' || chr(9) ||
                                 c_desc_substr2;
            ELSE
              i := i + 1;
              c_villa_data(i) := '!             ' || c_description1 || '' ||
                                 c_rev_description1;
            END IF;
          END LOOP;
          i := i + 1;
          c_villa_data(i) := '! ';
          /*For level 3*/
          i := i + 1;
          c_villa_data(i) := '!           Level5                                                Labellist';
          n_level_cnt := 0;
          /*Fetching the count for level 5*/
          n_level_cnt := Pkg_Villa_Interface.Fnc_Level_Cnt(pn_pro_rel_id, 5);
          FOR j IN 1 .. n_level_cnt LOOP
            c_description1     := Pkg_Villa_Interface.fnc_excel_config_desc(pn_pro_rel_id,
                                                                            5,
                                                                            j);
            c_rev_description1 := Pkg_Villa_Interface.Fnc_Excel_Rev_Desc(pn_pro_rel_id,
                                                                         5,
                                                                         j);
            IF Length(c_description1) < 100 THEN
              c_description1 := rpad(c_description1, 100, ' ');
              i := i + 1;
              c_villa_data(i) := '!             ' || c_description1 || '' ||
                                 c_rev_description1;
            ELSIF Length(c_description1) > 100 THEN
              c_desc_substr1 := Substr(c_description1, 1, 90);
              FOR n in 90 .. 100 LOOP
                c_desc_substr1 := c_desc_substr1 || ' ';
              END LOOP;
              c_desc_substr2 := Substr(c_description1,
                                       91,
                                       Length(c_desc_substr1));
              i := i + 1;
              c_villa_data(i) := '!            ' || chr(9) ||
                                 c_desc_substr1 || '' || c_rev_description1;
              i := i + 1;
              c_villa_data(i) := '!            ' || chr(9) ||
                                 c_desc_substr2;
            ELSE
              i := i + 1;
              c_villa_data(i) := '!             ' || c_description1 || '' ||
                                 c_rev_description1;
            END IF;
          END LOOP;
        ELSE
          /*If the release is not a configured release then fetch the data based on release id*/
          /*To fetch the node id for the selected release*/
          n_node := Get_Node_Id(pn_pro_rel_id);
          /*To find the full description based on node_id*/
          c_description1 := Ldb2_Full_Scope(n_node);
          /*To fetch the revision description for the selected release*/
          c_rev_description1 := Get_Rev_Description(pn_pro_rel_id);
          i := i + 1;
          c_villa_data(i) := '!  Scope' || chr(9) || ':' || 'Level1' ||
                             chr(9) || chr(9) || chr(9) || chr(9) || chr(9) ||
                             chr(9) || chr(9) || chr(9) || chr(9) || chr(9) ||
                             chr(9) || chr(9) || 'Labellist';
          IF Length(c_description1) < 100 THEN
            c_description1 := rpad(c_description1, 100, ' ');
            i := i + 1;
            c_villa_data(i) := '!            ' || chr(9) || c_description1 || '' ||
                               c_rev_description1;
          ELSIF Length(c_description1) > 100 THEN
            c_desc_substr1 := Substr(c_description1, 1, 90);
            c_desc_substr1 := rpad(c_desc_substr1, 100, ' ');
            c_desc_substr2 := Substr(c_description1,
                                     91,
                                     Length(c_desc_substr1));
            i := i + 1;
            c_villa_data(i) := '!            ' || chr(9) || c_desc_substr1 || '' ||
                               c_rev_description1;
            i := i + 1;
            c_villa_data(i) := '!             ' || chr(9) || c_desc_substr2;
          ELSE
            i := i + 1;
            c_villa_data(i) := '!            ' || chr(9) || c_description1 || '' ||
                               c_rev_description1;
          END IF;
          i := i + 1;
          c_villa_data(i) := '! ';
          i := i + 1;
          c_villa_data(i) := '!       ' || chr(9) || ':' || 'Level2' ||
                             chr(9) || chr(9) || chr(9) || chr(9) || chr(9) ||
                             chr(9) || chr(9) || chr(9) || chr(9) || chr(9) ||
                             chr(9) || chr(9) || 'Labellist';
          i := i + 1;
          c_villa_data(i) := '! ';
          i := i + 1;
          c_villa_data(i) := '!       ' || chr(9) || ':' || 'Level3' ||
                             chr(9) || chr(9) || chr(9) || chr(9) || chr(9) ||
                             chr(9) || chr(9) || chr(9) || chr(9) || chr(9) ||
                             chr(9) || chr(9) || 'Labellist';
          i := i + 1;
          c_villa_data(i) := '! ';
          i := i + 1;
          c_villa_data(i) := '!       ' || chr(9) || ':' || 'Level4' ||
                             chr(9) || chr(9) || chr(9) || chr(9) || chr(9) ||
                             chr(9) || chr(9) || chr(9) || chr(9) || chr(9) ||
                             chr(9) || chr(9) || 'Labellist';
          i := i + 1;
          c_villa_data(i) := '! ';
          i := i + 1;
          c_villa_data(i) := '!       ' || chr(9) || ':' || 'Level5' ||
                             chr(9) || chr(9) || chr(9) || chr(9) || chr(9) ||
                             chr(9) || chr(9) || chr(9) || chr(9) || chr(9) ||
                             chr(9) || chr(9) || 'Labellist';
        END IF;
        i := i + 1;
        c_villa_data(i) := '! ';
        i := i + 1;
        c_villa_data(i) := '!  Release' || chr(9) || ':' || c_rel.rel_id || ' ' ||
                           replace(c_rel.rel_description, chr(10));
        i := i + 1;
        c_villa_data(i) := '!  ';
        i := i + 1;
        c_villa_data(i) := '!  Cases' || chr(9) || ':' || c_cases;
        i := i + 1;
        c_villa_data(i) := '!  ';
        i := i + 1;
        c_villa_data(i) := '!  Created On' || chr(9) || ':' || sysdate;
        i := i + 1;
        c_villa_data(i) := '! ';
        i := i + 1;
        c_villa_data(i) := '!  Created By' || chr(9) || ':' || user;
        i := i + 1;
        c_villa_data(i) := '! ';
        i := i + 1;
        c_villa_data(i) := '!  ECU-Type' || chr(9) || ':' || c_ecutype;
        i := i + 1;
        c_villa_data(i) := '! ';
        i := i + 1;
        c_villa_data(i) := '!  ' || c_releasetyp;
        i := i + 1;
        c_villa_data(i) := '! ';
        i := i + 1;
        c_villa_data(i) := '!  Features' || chr(9) || ':' || c_comp;
        i := i + 1;
        c_villa_data(i) := '!  ============================================================================================';
        i := i + 1;
        c_villa_data(i) := ' ';
        i := i + 1;
        c_villa_data(i) := ' ';
        --V4.1 Global Check SSD header to be added
        IF c_rel.global_ssdheader IS NOT NULL THEN
          i := i + 1;
          c_villa_data(i) := '   ' || c_rel.global_ssdheader;
          i := i + 1;
          c_villa_data(i) := ' ';
        END IF;
        IF c_rel.global_bypass = 'Y' THEN
          -- V4.0 to insert respective global bypass check type;
          IF c_ecutype = 'EDC16' THEN
            c_global_type := '*_';
          ELSE
            c_global_type := '*';
          END IF;
          i := i + 1;
          c_villa_data(i) := '! 0. - Global Check ';
          i := i + 1;
          c_villa_data(i) := '! =========================================================';
          i := i + 1;
          c_villa_data(i) := ' ';
          i := i + 1;
          c_villa_data(i) := '! 0.1 Global Bypass Check ';
          i := i + 1;
          c_villa_data(i) := '! ---------------------------------------------------------';
          i := i + 1;
          c_villa_data(i) := ' ';
          i := i + 1;
          c_villa_data(i) := '   ' || c_global_type || 'Channel, >=0, <=0';
          i := i + 1;
          c_villa_data(i) := ' ';
          i := i + 1;
          c_villa_data(i) := '   ' || c_global_type || 'Vector, >=0, <=0';
          i := i + 1;
          c_villa_data(i) := ' ';
        END IF;
      END LOOP;
    END;
    /*************************************************************************************************************/
    /*To include SSD data*/
    FOR c_ssd_rec IN c_ssd LOOP
      /*To fetch advanced formula as it is clob field and cannot be queried directly from the select statement*/
      c_new_formula_Desc := pkg_clob.Get_Formdesc(c_ssd_rec.lab_obj_id,
                                                  c_ssd_rec.lab_rev_id);
      IF c_new_formula_desc = '-2' THEN
        NULL;
      ELSE
        c_default := 'N';
        IF c_ssd_rec.ssd_grp_label != c_ssd_grp_label_old THEN
          n_count_grp := n_count_grp + 1;
          c_ssd_grp_label_old := c_ssd_rec.ssd_grp_label;
          c_func_label_old := '__xxxxxx__';
          n_count_func := 0;
          i := i + 1;
          c_villa_data(i) := ' ';
          i := i + 1;
          c_villa_data(i) := '! ' || to_char(n_count_grp) || '. ' ||
                             c_ssd_rec.ssd_grp_label;
          i := i + 1;
          c_villa_data(i) := '! =========================================================';
          i := i + 1;
          c_villa_data(i) := ' ';
        END IF;
        IF c_ssd_rec.func_label != c_func_label_old THEN
          n_count_func := n_count_func + 1;
          c_func_label_old := c_ssd_rec.func_label;
          i := i + 1;
          c_villa_data(i) := '! ' || to_char(n_count_grp) || '.' ||
                             to_char(n_count_func) || ' ' ||
                             c_ssd_rec.func_label;
          i := i + 1;
          c_villa_data(i) := '! ---------------------------------------------------------';
          i := i + 1;
          c_villa_data(i) := ' ';
        END IF;
        i := i + 1;
        c_villa_data(i) := '#usecase ' || c_ssd_rec.cases;
        i := i + 1;
        c_villa_data(i) := '#contact "' || c_ssd_rec.contact || '"';
        i := i + 1;
        c_villa_data(i) := '! SSD node: ' || c_ssd_rec.node;
        i := i + 1;
        c_villa_data(i) := ' ';
        IF c_ssd_rec.dcm2ssd = 'Y' THEN
          dcm2ssd_data := pkg_dcm2ssd.convert_dcm2ssd(c_ssd_rec.lab_obj_id,
                                                      c_ssd_rec.lab_rev_id);
          FOR j IN 1 .. dcm2ssd_data.count LOOP
            i := i + 1;
            c_villa_data(i) := dcm2ssd_data(j);
          END LOOP;
        END IF;
        c_nachlabel_frm := ', ' || c_ssd_rec.formula;
        c_nachlabel     := null;
        IF c_ssd_rec.min_wert IS NOT NULL THEN
          c_nachlabel := ', >=' ||
                         replace(to_char(c_ssd_rec.min_wert), ',', '.');
        END IF;
        IF c_ssd_rec.max_wert IS NOT NULL THEN
          c_nachlabel := c_nachlabel || ', <=' ||
                         replace(to_char(c_ssd_rec.max_wert), ',', '.');
        END IF;
        IF (c_ssd_rec.min_wert IS NULL AND c_ssd_rec.max_wert IS NULL AND
           c_ssd_rec.typ IS NOT NULL) THEN
          c_default   := 'Y';
          c_nachlabel := ', =' || replace(to_char(c_ssd_rec.typ), ',', '.');
        END IF;
        /* if min ,max and formula and advanced formula are null then only default is considered in the file */
        IF c_default = 'Y' THEN
          IF c_ssd_rec.formula IS NULL AND c_new_formula_desc IS NULL AND
             c_nachlabel IS NOT NULL THEN
            i := i + 1;
            c_villa_data(i) := '   ' || c_ssd_rec.label || c_nachlabel;
            i := i + 1;
            c_villa_data(i) := ' ';
          END IF;
        ELSE
          IF c_nachlabel IS NOT NULL THEN
            i := i + 1;
            c_villa_data(i) := '   ' || c_ssd_rec.label || c_nachlabel;
            i := i + 1;
            c_villa_data(i) := ' ';
          END IF;
        END IF;
        IF c_ssd_rec.formula IS NOT NULL THEN
          i := i + 1;
          c_villa_data(i) := '   ' || c_ssd_rec.label || ', ' ||
                             c_ssd_rec.formula;
          i := i + 1;
          c_villa_data(i) := ' ';
        END IF;
        IF c_new_formula_desc IS NOT NULL THEN
          i := i + 1;
          /*
                  PK 09.03.2005, FEDab40788, pb_comment is used to generate autocomments for advance formula
          */
          IF pb_comment THEN
            c_villa_data(i) := '!##################### Begin of ' ||
                               c_ssd_rec.label ||
                               ' #######################';
            i := i + 1;
            c_villa_data(i) := ' ';
            i := i + 1;
          END IF;
          c_villa_data(i) := '   ' || c_new_formula_desc;
          /*
                  PK 09.03.2005, FEDab40788, pb_comment is used to generate autocomments for advance formula
          */
          IF pb_comment THEN
            i := i + 1;
            c_villa_data(i) := ' ';
            i := i + 1;
            c_villa_data(i) := '!##################### End of ' ||
                               c_ssd_rec.label ||
                               ' #######################';
          END IF;
          i := i + 1;
          c_villa_data(i) := ' ';
        END IF;
        i := i + 1;
        c_villa_data(i) := '#endcontact ';
        i := i + 1;
        c_villa_data(i) := '#endusecase';
        i := i + 1;
        c_villa_data(i) := ' ';
      END IF;
    END LOOP;
    pc_ssd_data := c_villa_data;
    -- Shankar 18.08.2003
    SetReleaseID(pn_pro_rel_id);
  EXCEPTION
    WHEN OTHERS THEN
      Pkg_Error_Logging.RegisterError('Error occurred  from the function SSD_File',
                                      SQLERRM,
                                      USER,
                                      SYSDATE,
                                      'Pkg_Villa_Interface');
      Raise_Application_Error(-20003,
                              'Error occurred while creating SSD file for pro_rel_id(' ||
                              pn_pro_rel_id || ') ' || sqlerrm);
  END SSD_File_Node;

  FUNCTION SSD_File_Label_Node(pn_pro_rel_id NUMBER,
                               pc_ssd_data   OUT c_villa_data%TYPE,
                               pc_mft        VARCHAR2 DEFAULT 'MFT',
                               pc_ssd        VARCHAR2 DEFAULT 'SSD',
                               pc_appchK     VARCHAR2 DEFAULT 'App-Chk',
                               pc_comp       VARCHAR2 DEFAULT 'Cal4Comp',
                               pb_temporary  BOOLEAN DEFAULT FALSE,
                               pb_refresh    BOOLEAN DEFAULT TRUE,
                               pb_comment    BOOLEAN DEFAULT FALSE)
    RETURN BOOLEAN IS
    /*****************************************************************************************************
    Purpose            : To save the SSD output to PL/SQl table for the provided release id ordered by label
    Parameters INPUT :    pn_pro_rel_id            Release id
                                            pc_ssd_data                Out put will be a PL/SQL table containing SSD data
                                            pc_mft                        MFT    case
                                            pc_ssd                        SSD case
                                            pc_appchk                    App-Chk case
                                            pb_temporary            If its a temporary release
                                            pb_refresh                Flag for villa incase the data is same
                                            pb_comment                Advanced formula comments if required
                  OUT     :        TRUE on success and FALSE on failure
                  History     :     PK,22-08-05 FEDab00046790 V4.5
                                             Moved existing forms function to database package so that it can be used across many forms
                                             RB,23.08/07 RCMS00447599 Increase of SSD-Release-Config Levels from 3 to 5 levels
                        SK RCMS00457889 Name of .ssd-file depends on selected usecases MFT, SSD, APP
    *********************************************************************************************************/
    /*To fetch the SSD detail for the given release id*/
    /*
        PK RCMS00046790 added dcm2ssd
    */
    CURSOR c_ssd IS
      SELECT distinct lab_obj_id,
                      typ,
                      max_wert,
                      min_wert,
                      formula formula,
                      ssd_grp_label,
                      func_label,
                      label,
                      lab_rev_id,
                      dcm2ssd,
                      cases,
                      contact,
                      Node
        FROM V_LDB2_DCM_SSD_MITNODE
       WHERE pro_rel_id = pn_pro_rel_id
         AND cases IN (pc_mft, pc_ssd, pc_appchk, pc_comp)
       ORDER BY label;
    CURSOR Cur_Rel IS
      SELECT DECODE(rel_typ, 'e', 'External release', 'Internal Release') rel_type,
             cre_date rel_date,
             cre_user rel_user,
             rel_id rel_id,
             description rel_description,
             global_chk global_bypass,
             global_ssdheader
        FROM v_ldb2_project_release
       WHERE pro_rel_id = pn_pro_rel_id;
    n_Pro_Rel_ID V_LDB2_PRO_ERRORS.PRO_REL_ID % TYPE;
    /*Local Variable declaration*/
    dcm2ssd_data       pkg_dcm2ssd.ssd_file;
    c_nachlabel_frm    VARCHAR2(1010);
    c_nachlabel        VARCHAR2(1010);
    c_default          CHAR;
    c_new_formula_desc VARCHAR2(32767);
    c_global_type      VARCHAR2(10);
    i                  BINARY_INTEGER := 1;
    c_releasetyp       VARCHAR2(500) := 'Only internal Release';
    n_count            NUMBER;
    c_ecutype          VARCHAR2(20);
    c_rev_data         VARCHAR2(4000);
    c_rev_data1        VARCHAR2(4000);
    c_rev_data2        VARCHAR2(4000);
    c_rev_data3        VARCHAR2(4000);
    n_rev_cnt          NUMBER := 0;
    c_error_text       VARCHAR2(2000);
    except_file EXCEPTION;
    c_comp             VARCHAR2(20000);
    c_cases            VARCHAR2(200);
    n_config_cnt       NUMBER;
    n_level_cnt        NUMBER;
    c_description1     VARCHAR2(32000);
    c_rev_description1 VARCHAR2(10000);
    n_node             V_Ldb2_Object_Tree.Node_id%TYPE;
    c_desc_substr1     VARCHAR2(10000);
    c_desc_substr2     VARCHAR2(10000);
  BEGIN
    /*
         Initialize c_villa_data!!
         For Villa the PL/SQL-Table is not cleared incase the query is re-run for same pro_rel_id but
         for labeldatabase every time the package is invoke the PL/SQL-Table is cleared
    */
    EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_SORT=GERMAN';
    IF pb_refresh THEN
      IF G_ReleaseID <> pn_pro_rel_id THEN
        c_villa_data.DELETE;
      END IF;
    ELSE
      c_villa_data.DELETE;
    END IF;
    c_comp    := get_feature_values(pn_pro_rel_id);
    c_ecutype := getECUType(pn_pro_rel_id);
    IF c_ecutype IS NULL THEN
      c_error_text := 'Error occurred  while finding the ECU-Type for the release for pro_rel_id(' ||
                      pn_pro_rel_id || ')';
      RAISE except_file;
    END IF;
    /*To fecth release detail*/
    -- $$$
    /*
    If external release, then no errors??
    if internal release, then check if errors exist for the release!!
    */
    FOR c_rel IN cur_rel LOOP
      IF C_Rel.rel_type = 'Internal Release' THEN
        SELECT COUNT(pro_rel_id)
          INTO n_count
          FROM v_ldb2_pro_errors
         WHERE pro_rel_id = pn_pro_rel_id
           AND error_nr <> 6;
        IF n_count > 0 THEN
          IF pb_temporary THEN
            c_releasetyp := 'Temporary Release for CheckSSD(with errors)';
          ELSE
            c_releasetyp := 'Only internal Release (with errors)';
          END IF;
        ELSE
          IF pb_temporary THEN
            c_releasetyp := 'Temporary Release for CheckSSD';
          ELSE
            c_releasetyp := 'Only internal Release';
          END IF;
        END IF;
      ELSE
        c_releasetyp := 'External release';
      END IF;
      SELECT DECODE(pc_mft, 'MFT', 'MFT', '') ||
             DECODE(pc_ssd, 'SSD', ' SSD', '') ||
             DECODE(pc_appchk, 'App-Chk', ' App-Chk', '') ||
             DECODE(pc_comp, 'Cal4Comp', ' Cal4Comp', '')
        INTO c_cases
        FROM DUAL;
      c_cases := REPLACE(TRIM(c_cases), ' ', ',');
      /*Inserting the values to PL/Sql table*/
      c_villa_data(i) := '!  SSD-Report for ' || c_ecutype ||
                         ' Software, SSD-Labeldatabase, DS/ESQ3';
      i := i + 1;
      c_villa_data(i) := '!  ============================================================================================';
      i := i + 1;
      c_villa_data(i) := ' ';
      i := i + 1;
      c_villa_data(i) := '!  System-spezifische Software-Daten';
      /*To find whether the release is a configured release*/
      n_config_cnt := CONFIG_RELEASE_CNT(Pn_pro_rel_id);
      /*FEtch configured release data*/
      IF n_config_cnt > 1 THEN
        i := i + 1;
        c_villa_data(i) := '!  Scope    Level1                                                Labellist';
        /*Sp, V4.5, find the count for level and release*/
        n_level_cnt := Pkg_Villa_Interface.Fnc_Level_Cnt(pn_pro_rel_id, 1);
        FOR j IN 1 .. n_level_cnt LOOP
          c_description1     := Pkg_Villa_Interface.fnc_excel_config_desc(pn_pro_rel_id,
                                                                          1,
                                                                          j);
          c_rev_description1 := Pkg_Villa_Interface.Fnc_Excel_Rev_Desc(pn_pro_rel_id,
                                                                       1,
                                                                       j);
          IF Length(c_description1) < 100 THEN
            c_description1 := rpad(c_description1, 100, ' ');
            i := i + 1;
            c_villa_data(i) := '!            ' || chr(9) || c_description1 || '' ||
                               c_rev_description1;
          ELSIF Length(c_description1) > 100 THEN
            c_desc_substr1 := Substr(c_description1, 1, 90);
            c_desc_substr1 := rpad(c_desc_substr1, 100, ' ');
            c_desc_substr2 := Substr(c_description1,
                                     91,
                                     Length(c_desc_substr1));
            i := i + 1;
            c_villa_data(i) := '!            ' || chr(9) || c_desc_substr1 || '' ||
                               c_rev_description1;
            i := i + 1;
            c_villa_data(i) := '!             ' || chr(9) || c_desc_substr2;
          ELSE
            i := i + 1;
            c_villa_data(i) := '!            ' || chr(9) || c_description1 || '' ||
                               c_rev_description1;
          END IF;
        END LOOP;
        /*For level 2*/
        i := i + 1;
        c_villa_data(i) := '!     ';
        i := i + 1;
        c_villa_data(i) := '!           Level2                                                Labellist';
        n_level_cnt := 0;
        n_level_cnt := Pkg_Villa_Interface.Fnc_Level_Cnt(pn_pro_rel_id, 2);
        FOR j IN 1 .. n_level_cnt LOOP
          c_description1     := Pkg_Villa_Interface.fnc_excel_config_desc(pn_pro_rel_id,
                                                                          2,
                                                                          j);
          c_rev_description1 := Pkg_Villa_Interface.Fnc_Excel_Rev_Desc(pn_pro_rel_id,
                                                                       2,
                                                                       j);
          IF Length(c_description1) < 100 THEN
            c_description1 := rpad(c_description1, 100, ' ');
            i := i + 1;
            c_villa_data(i) := '!            ' || chr(9) || c_description1 || '' ||
                               c_rev_description1;
          ELSIF Length(c_description1) > 100 THEN
            c_desc_substr1 := Substr(c_description1, 1, 90);
            c_desc_substr1 := rpad(c_desc_substr1, 100, ' ');
            c_desc_substr2 := Substr(c_description1,
                                     91,
                                     Length(c_desc_substr1));
            i := i + 1;
            c_villa_data(i) := '!            ' || chr(9) || c_desc_substr1 || '' ||
                               c_rev_description1;
            i := i + 1;
            c_villa_data(i) := '!             ' || chr(9) || c_desc_substr2;
          ELSE
            i := i + 1;
            c_villa_data(i) := '!            ' || chr(9) || c_description1 || '' ||
                               c_rev_description1;
          END IF;
        END LOOP;
        i := i + 1;
        c_villa_data(i) := '!     ';
        /*For level 3*/
        i := i + 1;
        c_villa_data(i) := '!              Level3                                                Labellist';
        n_level_cnt := 0;
        n_level_cnt := Pkg_Villa_Interface.Fnc_Level_Cnt(pn_pro_rel_id, 3);
        FOR j IN 1 .. n_level_cnt LOOP
          c_description1     := Pkg_Villa_Interface.fnc_excel_config_desc(pn_pro_rel_id,
                                                                          3,
                                                                          j);
          c_rev_description1 := Pkg_Villa_Interface.Fnc_Excel_Rev_Desc(pn_pro_rel_id,
                                                                       3,
                                                                       j);
          IF Length(c_description1) < 100 THEN
            c_description1 := rpad(c_description1, 100, ' ');
            i := i + 1;
            c_villa_data(i) := '!            ' || chr(9) || c_description1 || '' ||
                               c_rev_description1;
          ELSIF Length(c_description1) > 100 THEN
            c_desc_substr1 := Substr(c_description1, 1, 90);
            c_desc_substr1 := rpad(c_desc_substr1, 100, ' ');
            c_desc_substr2 := Substr(c_description1,
                                     91,
                                     Length(c_desc_substr1));
            i := i + 1;
            c_villa_data(i) := '!            ' || chr(9) || c_desc_substr1 || '' ||
                               c_rev_description1;
            i := i + 1;
            c_villa_data(i) := '!             ' || chr(9) || c_desc_substr2;
          ELSE
            i := i + 1;
            c_villa_data(i) := '!            ' || chr(9) || c_description1 || '' ||
                               c_rev_description1;
          END IF;
        END LOOP;
        i := i + 1;
        c_villa_data(i) := '!     ';
        /*For level 4*/
        i := i + 1;
        c_villa_data(i) := '!              Level4                                                Labellist';
        n_level_cnt := 0;
        n_level_cnt := Pkg_Villa_Interface.Fnc_Level_Cnt(pn_pro_rel_id, 4);
        FOR j IN 1 .. n_level_cnt LOOP
          c_description1     := Pkg_Villa_Interface.fnc_excel_config_desc(pn_pro_rel_id,
                                                                          4,
                                                                          j);
          c_rev_description1 := Pkg_Villa_Interface.Fnc_Excel_Rev_Desc(pn_pro_rel_id,
                                                                       4,
                                                                       j);
          IF Length(c_description1) < 100 THEN
            c_description1 := rpad(c_description1, 100, ' ');
            i := i + 1;
            c_villa_data(i) := '!            ' || chr(9) || c_description1 || '' ||
                               c_rev_description1;
          ELSIF Length(c_description1) > 100 THEN
            c_desc_substr1 := Substr(c_description1, 1, 90);
            c_desc_substr1 := rpad(c_desc_substr1, 100, ' ');
            c_desc_substr2 := Substr(c_description1,
                                     91,
                                     Length(c_desc_substr1));
            i := i + 1;
            c_villa_data(i) := '!            ' || chr(9) || c_desc_substr1 || '' ||
                               c_rev_description1;
            i := i + 1;
            c_villa_data(i) := '!             ' || chr(9) || c_desc_substr2;
          ELSE
            i := i + 1;
            c_villa_data(i) := '!            ' || chr(9) || c_description1 || '' ||
                               c_rev_description1;
          END IF;
        END LOOP;
        i := i + 1;
        c_villa_data(i) := '!     ';
        /*For level 5*/
        i := i + 1;
        c_villa_data(i) := '!              Level5                                                Labellist';
        n_level_cnt := 0;
        n_level_cnt := Pkg_Villa_Interface.Fnc_Level_Cnt(pn_pro_rel_id, 5);
        FOR j IN 1 .. n_level_cnt LOOP
          c_description1     := Pkg_Villa_Interface.fnc_excel_config_desc(pn_pro_rel_id,
                                                                          5,
                                                                          j);
          c_rev_description1 := Pkg_Villa_Interface.Fnc_Excel_Rev_Desc(pn_pro_rel_id,
                                                                       5,
                                                                       j);
          IF Length(c_description1) < 100 THEN
            c_description1 := rpad(c_description1, 100, ' ');
            i := i + 1;
            c_villa_data(i) := '!            ' || chr(9) || c_description1 || '' ||
                               c_rev_description1;
          ELSIF Length(c_description1) > 100 THEN
            c_desc_substr1 := Substr(c_description1, 1, 90);
            c_desc_substr1 := rpad(c_desc_substr1, 100, ' ');
            c_desc_substr2 := Substr(c_description1,
                                     91,
                                     Length(c_desc_substr1));
            i := i + 1;
            c_villa_data(i) := '!            ' || chr(9) || c_desc_substr1 || '' ||
                               c_rev_description1;
            i := i + 1;
            c_villa_data(i) := '!             ' || chr(9) || c_desc_substr2;
          ELSE
            i := i + 1;
            c_villa_data(i) := '!            ' || chr(9) || c_description1 || '' ||
                               c_rev_description1;
          END IF;
        END LOOP;
      ELSE
        /*If release is not a configured release then fetch individual data*/
        /*To find the node id*/
        n_node := Get_Node_Id(pn_pro_rel_id);
        /*To find the path*/
        c_description1 := Ldb2_Full_Scope(n_node);
        /*To fidn the description*/
        c_rev_description1 := Get_Rev_Description(pn_pro_rel_id);
        /*Displaying the data*/
        i := i + 1;
        c_villa_data(i) := '!  Scope' || chr(9) || ':' || 'Level1' ||
                           chr(9) || chr(9) || chr(9) || chr(9) || chr(9) ||
                           chr(9) || chr(9) || chr(9) || chr(9) ||
                           'Labellist';
        i := i + 1;
        c_villa_data(i) := '!            ' || chr(9) || c_description1 ||
                           '        ' || '   ' || c_rev_description1;
        i := i + 1;
        c_villa_data(i) := '!       ';
        i := i + 1;
        c_villa_data(i) := '!       ' || chr(9) || ':' || 'Level2' ||
                           chr(9) || chr(9) || chr(9) || chr(9) || chr(9) ||
                           chr(9) || chr(9) || chr(9) || chr(9) ||
                           'Labellist';
        i := i + 1;
        c_villa_data(i) := '!       ';
        i := i + 1;
        c_villa_data(i) := '!       ' || chr(9) || ':' || 'Level3' ||
                           chr(9) || chr(9) || chr(9) || chr(9) || chr(9) ||
                           chr(9) || chr(9) || chr(9) || chr(9) ||
                           'Labellist';
        i := i + 1;
        c_villa_data(i) := '!       ';
        i := i + 1;
        c_villa_data(i) := '!       ' || chr(9) || ':' || 'Level4' ||
                           chr(9) || chr(9) || chr(9) || chr(9) || chr(9) ||
                           chr(9) || chr(9) || chr(9) || chr(9) ||
                           'Labellist';
        i := i + 1;
        c_villa_data(i) := '!       ';
        i := i + 1;
        c_villa_data(i) := '!       ' || chr(9) || ':' || 'Level5' ||
                           chr(9) || chr(9) || chr(9) || chr(9) || chr(9) ||
                           chr(9) || chr(9) || chr(9) || chr(9) ||
                           'Labellist';
      END IF;
      IF NOT pb_temporary THEN
        i := i + 1;
        c_villa_data(i) := '! ';
        i := i + 1;
        c_villa_data(i) := '!  Release' || chr(9) || ':' || c_rel.rel_id || ' ' ||
                           replace(c_rel.rel_description, chr(10));
        i := i + 1;
        c_villa_data(i) := '! ';
        i := i + 1;
        c_villa_data(i) := '!  Cases' || chr(9) || ':' || c_cases;
      END IF;
      i := i + 1;
      c_villa_data(i) := '!  ';
      i := i + 1;
      c_villa_data(i) := '!  Created On' || chr(9) || ':' || sysdate;
      i := i + 1;
      c_villa_data(i) := '! ';
      i := i + 1;
      c_villa_data(i) := '!  Created By' || chr(9) || ':' || user;
      i := i + 1;
      c_villa_data(i) := '! ';
      i := i + 1;
      c_villa_data(i) := '!  ECU-Type' || chr(9) || ':' || c_ecutype;
      i := i + 1;
      c_villa_data(i) := '! ';
      i := i + 1;
      c_villa_data(i) := '!  ' || c_releasetyp;
      i := i + 1;
      c_villa_data(i) := '! ';
      i := i + 1;
      c_villa_data(i) := '!  Features' || chr(9) || ':' || c_comp;
      i := i + 1;
      c_villa_data(i) := '!  ============================================================================================';
      i := i + 1;
      c_villa_data(i) := ' ';
      i := i + 1;
      c_villa_data(i) := ' ';
      IF c_rel.global_ssdheader IS NOT NULL THEN
        i := i + 1;
        c_villa_data(i) := '   ' || c_rel.global_ssdheader;
        i := i + 1;
        c_villa_data(i) := ' ';
      END IF;
      IF c_rel.global_bypass = 'Y' THEN
        -- V4.0 to insert respective global bypass check type;
        IF c_ecutype = 'EDC16' THEN
          c_global_type := '*_';
        ELSE
          c_global_type := '*';
        END IF;
        i := i + 1;
        c_villa_data(i) := '   ' || c_global_type || 'Channel, >=0, <=0';
        i := i + 1;
        c_villa_data(i) := ' ';
        i := i + 1;
        c_villa_data(i) := '   ' || c_global_type || 'Vector, >=0, <=0';
        i := i + 1;
        c_villa_data(i) := ' ';
      END IF;
    END LOOP;
    /*************************************************************************************************************/
    /*To include SSD data*/
    FOR c_ssd_rec IN c_ssd LOOP
      /*To fetch advanced formula as it is clob field and cannot be queried directly from the select statement*/
      c_new_formula_Desc := pkg_clob.Get_Formdesc(c_ssd_rec.lab_obj_id,
                                                  c_ssd_rec.lab_rev_id);
      IF c_new_formula_desc = '-2' THEN
        c_error_text := 'Unable to fetch the advanced formula for ' ||
                        'obj_id(' || c_ssd_rec.lab_obj_id ||
                        ') and RevisionID(' || c_ssd_rec.lab_rev_id || ')';
        RAISE except_file;
      ELSE
        c_default := 'N';
      END IF;
      c_nachlabel_frm := ', ' || c_ssd_rec.formula;
      c_nachlabel := null;
      i := i + 1;
      c_villa_data(i) := '#usecase ' || c_ssd_rec.cases;
      i := i + 1;
      c_villa_data(i) := '#contact "' || c_ssd_rec.contact || '"';
      i := i + 1;
      c_villa_data(i) := '! SSD node: ' || c_ssd_rec.Node;
      i := i + 1;
      c_villa_data(i) := ' ';
      /*
              Convert the DCM data to SSD data if the checkbox is selected
      */
      IF c_ssd_rec.dcm2ssd = 'Y' THEN
        dcm2ssd_data := pkg_dcm2ssd.convert_dcm2ssd(c_ssd_rec.lab_obj_id,
                                                    c_ssd_rec.lab_rev_id);
        FOR j IN 1 .. dcm2ssd_data.count LOOP
          i := i + 1;
          c_villa_data(i) := dcm2ssd_data(j);
        END LOOP;
      END IF;
      IF c_ssd_rec.min_wert IS NOT NULL THEN
        c_nachlabel := ', >=' ||
                       replace(to_char(c_ssd_rec.min_wert), ',', '.');
      END IF;
      IF c_ssd_rec.max_wert IS NOT NULL THEN
        c_nachlabel := c_nachlabel || ', <=' ||
                       replace(to_char(c_ssd_rec.max_wert), ',', '.');
      END IF;
      IF (c_ssd_rec.min_wert IS NULL AND c_ssd_rec.max_wert IS NULL AND
         c_ssd_rec.typ IS NOT NULL) THEN
        c_default   := 'Y';
        c_nachlabel := ', =' || replace(to_char(c_ssd_rec.typ), ',', '.');
      END IF;
      /* if min ,max and formula and advanced formula are null then only default is considered in the file */
      IF c_default = 'Y' THEN
        IF c_ssd_rec.formula IS NULL AND c_new_formula_desc IS NULL AND
           c_nachlabel IS NOT NULL THEN
          i := i + 1;
          c_villa_data(i) := '   ' || c_ssd_rec.label || c_nachlabel;
          i := i + 1;
          c_villa_data(i) := ' ';
        END IF;
      ELSE
        IF c_nachlabel IS NOT NULL THEN
          i := i + 1;
          c_villa_data(i) := '   ' || c_ssd_rec.label || c_nachlabel;
          i := i + 1;
          c_villa_data(i) := ' ';
        END IF;
      END IF;
      IF c_ssd_rec.formula IS NOT NULL THEN
        i := i + 1;
        c_villa_data(i) := '   ' || c_ssd_rec.label || ', ' ||
                           c_ssd_rec.formula;
        i := i + 1;
        c_villa_data(i) := ' ';
      END IF;
      IF c_new_formula_desc IS NOT NULL THEN
        i := i + 1;
        /*
                PK 09.03.2005, FEDab40788, pb_comment is used to generate autocomments for advance formula
        */
        IF pb_comment THEN
          c_villa_data(i) := '!##################### Begin of ' ||
                             c_ssd_rec.label || ' #######################';
          i := i + 1;
          c_villa_data(i) := ' ';
          i := i + 1;
          c_villa_data(i) := '   ' || 'BEGINLINE' || c_new_formula_desc;
          i := i + 1;
          c_villa_data(i) := ' ';
          i := i + 1;
          c_villa_data(i) := '!##################### End of ' ||
                             c_ssd_rec.label || ' #######################';
        ELSE
          c_villa_data(i) := '   ' || c_new_formula_desc;
        END IF;
        i := i + 1;
        c_villa_data(i) := ' ';
      END IF;
      i := i + 1;
      c_villa_data(i) := '#endcontact ';
      i := i + 1;
      c_villa_data(i) := '#endusecase';
      i := i + 1;
      c_villa_data(i) := ' ';
    END LOOP;
    pc_ssd_data := c_villa_data;
    SetReleaseID(pn_pro_rel_id);
    RETURN TRUE;
  EXCEPTION
    WHEN except_file THEN
      Pkg_Error_Logging.RegisterError(c_error_text,
                                      SQLERRM,
                                      USER,
                                      SYSDATE,
                                      'Pkg_Villa_Interface');
      RETURN FALSE;
    WHEN OTHERS THEN
      Pkg_Error_Logging.RegisterError('Error occurred  from the function SSD_File',
                                      SQLERRM,
                                      USER,
                                      SYSDATE,
                                      'Pkg_Villa_Interface');
      RETURN FALSE;
  END SSD_File_Label_Node;

  PROCEDURE SSD_File_curve_map(pc_ssd_data OUT c_villa_data%TYPE,
                               pb_node_id  number,
                               pb_comment  BOOLEAN DEFAULT FALSE) IS

    CURSOR c_label IS

      SELECT *
        FROM V_LDB2_PAVAST
       WHERE UPPER_LABEL IN (SELECT UPPER(LABEL) FROM TEMP_LABELLIST_INTERFACE);

    /*to get the ssd data for the labels present in temp table and node_id */
    CURSOR c_ssd(pn_lab_id number) IS
      SELECT distinct a.lab_obj_id,
                      a.typ,
                      a.max_wert,
                      a.min_wert,
                      a.formula formula,
                      a.ssd_grp_label,
                      a.func_label,
                      b.label,
                      a.lab_rev_id,
                      a.dcm2ssd,
                      a.cases,
                      a.contact
        FROM V_LDB2_ICDM_SSD a, TEMP_LABELLIST_INTERFACE b
       WHERE a.node_id = pb_node_id
         and a.lab_lab_id = pn_lab_id
         and a.cases IN ('Review')
         and upper(a.label)=upper(b.label)
       ORDER BY ssd_grp_label, func_label, label;

    c_rel_typ           VARCHAR2(1);
    b_errors            BOOLEAN := FALSE;
    c_releasetyp        VARCHAR2(500) := 'Only internal Release';
    c_ssd_grp_label_old v_ldb2_ssd_grp.label%TYPE := '__xxxxx__';
    c_func_label_old    v_ldb2_pavast.function%TYPE := '__xxxxx__';
    c_nachlabel         VARCHAR2(1010);
    c_nachlabel_frm     VARCHAR2(1010);
    n_count_grp         NUMBER := 0;
    n_count_func        NUMBER := 0;
    c_comp              VARCHAR2(20000);
    c_feature           v_ldb2_features.feature_text%TYPE;
    c_value             v_ldb2_values.value_text%TYPE;
    c_default           CHAR;
    c_rev_data1         VARCHAR2(4000);
    c_rev_data2         VARCHAR2(4000);
    c_rev_data3         VARCHAR2(4000);
    c_rev_data          VARCHAR2(4000);
    c_new_formula_desc  VARCHAR2(32767);
    i                   BINARY_INTEGER := 1;
    c_cases             VARCHAR2(200);
    LI_Counter          NUMBER;
    n_rev_cnt           NUMBER := 0;
    c_global_chk        CHAR;
    c_global_type       VARCHAR2(10);
    c_ecutype           VARCHAR2(20);
    c_global_ssdheader  v_ldb2_project_release.global_ssdheader%TYPE;
    dcm2ssd_data        pkg_dcm2ssd.ssd_file;
    n_config_cnt        NUMBER;
    n_level_cnt         NUMBER;
    c_description1      VARCHAR2(32000);
    c_rev_description1  VARCHAR2(32000);
    n_node              v_ldb2_object_tree.node_id%type;
    c_desc_substr1      VARCHAR2(10000);
    c_desc_substr2      VARCHAR2(10000);

  BEGIN
    c_villa_data.DELETE;
    c_villa_data(i) := '!  SSD-Report for ' || c_ecutype ||
                       ' Software, SSD-Labeldatabase, for ICDM';
    i := i + 1;
    c_villa_data(i) := '!  ============================================================================================';
    i := i + 1;
    c_villa_data(i) := ' ';
    i := i + 1;
    c_villa_data(i) := '!  System-spezifische Software-Daten';

    /*To include SSD data*/
    FOR c_label_rec IN c_label LOOP

      FOR c_ssd_rec IN c_ssd(c_label_rec.LAB_ID) LOOP

        /*To fetch advanced formula as it is clob field and cannot be queried directly from the select statement*/
        c_new_formula_Desc := pkg_clob.Get_Formdesc(c_ssd_rec.lab_obj_id,
                                                    c_ssd_rec.lab_rev_id);

        IF c_new_formula_desc = '-2' THEN
          NULL;
        ELSE
          c_default := 'N';
          IF c_ssd_rec.ssd_grp_label != c_ssd_grp_label_old THEN
            n_count_grp := n_count_grp + 1;
            c_ssd_grp_label_old := c_ssd_rec.ssd_grp_label;
            c_func_label_old := '__xxxxxx__';
            n_count_func := 0;
            i := i + 1;
            c_villa_data(i) := ' ';
            i := i + 1;
            c_villa_data(i) := '! ' || to_char(n_count_grp) || '. ' ||
                               c_ssd_rec.ssd_grp_label;
            i := i + 1;
            c_villa_data(i) := '! =========================================================';
            i := i + 1;
            c_villa_data(i) := ' ';
          END IF;

          IF c_ssd_rec.func_label != c_func_label_old THEN
            n_count_func := n_count_func + 1;
            c_func_label_old := c_ssd_rec.func_label;
            i := i + 1;
            c_villa_data(i) := '! ' || to_char(n_count_grp) || '.' ||
                               to_char(n_count_func) || ' ' ||
                               c_ssd_rec.func_label;
            i := i + 1;
            c_villa_data(i) := '! ---------------------------------------------------------';
            i := i + 1;
            c_villa_data(i) := ' ';
          END IF;

          i := i + 1;
          --------------------------------------------------------------------------------------------
          c_villa_data(i) := '#usecase ' || c_ssd_rec.cases;

          i := i + 1;
          c_villa_data(i) := '#contact "' || c_ssd_rec.contact || '"';
          i := i + 1;
          c_villa_data(i) := ' ';
          -------------------------------------------------------------------------------
          IF c_ssd_rec.dcm2ssd = 'Y' THEN
            dcm2ssd_data := pkg_dcm2ssd.convert_dcm2ssd(c_ssd_rec.lab_obj_id,
                                                        c_ssd_rec.lab_rev_id);
            FOR j IN 1 .. dcm2ssd_data.count LOOP
              i := i + 1;
              c_villa_data(i) := dcm2ssd_data(j);
            END LOOP;
          END IF;
          -----------------------------------------
          c_nachlabel_frm := ', ' || c_ssd_rec.formula;
          c_nachlabel     := null;

          -----------------------------------------
          /* zuwAD_Inj, MAP ( <= 8000, ALL, ALL ) ! map
          zuwAD_Inj, MAP ( >= 5000 && <=8000, ALL, ALL ) ! map
          mrwBDB_KL, MAP ( 0.0, ALL ) ! curve*/

          -------------------------------------
          IF c_ssd_rec.min_wert IS NOT NULL and c_ssd_rec.max_wert is NULL and
             c_label_rec.category like 'MAP%' THEN
            c_nachlabel := ',MAP(>=' ||
                           replace(to_char(c_ssd_rec.min_wert), ',', '.') ||
                           ',ALL,ALL)';

          ELSIF c_ssd_rec.min_wert IS NULL and
                c_ssd_rec.max_wert is NOT NULL and
                c_label_rec.category like 'MAP%' THEN
            c_nachlabel := ',MAP(<=' ||
                           replace(to_char(c_ssd_rec.max_wert), ',', '.') ||
                           ',ALL,ALL)';

          ELSIF c_ssd_rec.min_wert IS NOT NULL and
                c_ssd_rec.max_wert is NOT NULL and
                c_label_rec.category like 'MAP%' THEN
            c_nachlabel := ',MAP(>=' ||
                           replace(to_char(c_ssd_rec.min_wert), ',', '.') || ' ' ||
                           chr(38) || chr(38) || ' <=' ||
                           replace(to_char(c_ssd_rec.max_wert), ',', '.') ||
                           ',ALL,ALL)';

          ELSIF c_ssd_rec.min_wert IS NOT NULL and
                c_ssd_rec.max_wert is NULL and
                c_label_rec.category like 'CURVE%' THEN
            c_nachlabel := ',MAP(>=' ||
                           replace(to_char(c_ssd_rec.min_wert), ',', '.') ||
                           ',ALL)';

          ELSIF c_ssd_rec.min_wert IS NULL and
                c_ssd_rec.max_wert is NOT NULL and
                c_label_rec.category like 'CURVE%' THEN
            c_nachlabel := ',MAP(<=' ||
                           replace(to_char(c_ssd_rec.max_wert), ',', '.') ||
                           ',ALL)';

          ELSIF c_ssd_rec.min_wert IS NOT NULL and
                c_ssd_rec.max_wert is NOT NULL and
                c_label_rec.category like 'CURVE%' THEN
            c_nachlabel := ',MAP(>=' ||
                           replace(to_char(c_ssd_rec.min_wert), ',', '.') || ' ' ||
                           chr(38) || chr(38) || ' <=' ||
                           replace(to_char(c_ssd_rec.max_wert), ',', '.') ||
                           ',ALL)';

          ELSIF c_ssd_rec.min_wert IS NOT NULL and
                c_ssd_rec.max_wert is NOT NULL and
                (c_label_rec.category not like 'CURVE%' or
                c_label_rec.category not like 'MAP') THEN
            c_nachlabel := ', >=' ||
                           replace(to_char(c_ssd_rec.min_wert), ',', '.');

            c_nachlabel := c_nachlabel || ', <=' ||
                           replace(to_char(c_ssd_rec.max_wert), ',', '.');

          ELSIF c_ssd_rec.min_wert IS NOT NULL and
                c_ssd_rec.max_wert is NULL and
                (c_label_rec.category not like 'CURVE%' or
                c_label_rec.category not like 'MAP') THEN
            c_nachlabel := ', >=' ||
                           replace(to_char(c_ssd_rec.min_wert), ',', '.');

          ELSIF c_ssd_rec.min_wert IS NULL and
                c_ssd_rec.max_wert is NOT NULL and
                (c_label_rec.category not like 'CURVE%' or
                c_label_rec.category not like 'MAP') THEN

            c_nachlabel := c_nachlabel || ', <=' ||
                           replace(to_char(c_ssd_rec.max_wert), ',', '.');

          ELSIF (c_ssd_rec.min_wert IS NULL AND c_ssd_rec.max_wert IS NULL AND
                c_ssd_rec.typ IS NOT NULL) THEN

            c_default   := 'Y';
            c_nachlabel := ', =' ||
                           replace(to_char(c_ssd_rec.typ), ',', '.');
          END IF;

          /*IF c_ssd_rec.min_wert IS NOT NULL THEN
                 c_nachlabel := ', >=' ||
                           replace(to_char(c_ssd_rec.min_wert), ',', '.');
              END IF;



          IF c_ssd_rec.max_wert IS NOT NULL THEN
            c_nachlabel := c_nachlabel || ', <=' ||
                           replace(to_char(c_ssd_rec.max_wert), ',', '.');
          END IF;

          IF (c_ssd_rec.min_wert IS NULL AND c_ssd_rec.max_wert IS NULL AND
             c_ssd_rec.typ IS NOT NULL) THEN
            c_default   := 'Y';
            c_nachlabel := ', =' || replace(to_char(c_ssd_rec.typ), ',', '.');
          END IF;*/

          /* if min ,max and formula and advanced formula are null then only default is considered in the file */
          IF c_default = 'Y' THEN
            IF c_ssd_rec.formula IS NULL AND c_new_formula_desc IS NULL AND
               c_nachlabel IS NOT NULL THEN
              i := i + 1;
              c_villa_data(i) := '   ' || c_ssd_rec.label || c_nachlabel;
              i := i + 1;
              c_villa_data(i) := ' ';
            END IF;
          ELSE
            IF c_nachlabel IS NOT NULL THEN
              i := i + 1;
              c_villa_data(i) := '   ' || c_ssd_rec.label || c_nachlabel;
              i := i + 1;
              c_villa_data(i) := ' ';
            END IF;
          END IF;
          IF c_ssd_rec.formula IS NOT NULL THEN
            i := i + 1;
            c_villa_data(i) := '   ' || c_ssd_rec.label || ', ' ||
                               c_ssd_rec.formula;
            i := i + 1;
            c_villa_data(i) := ' ';
          END IF;
          IF c_new_formula_desc IS NOT NULL THEN
            i := i + 1;
            /*
                    PK 09.03.2005, FEDab40788, pb_comment is used to generate autocomments for advance formula
            */
            IF pb_comment THEN
              c_villa_data(i) := '!##################### Begin of ' ||
                                 c_ssd_rec.label ||
                                 ' #######################';
              i := i + 1;
              c_villa_data(i) := ' ';
              i := i + 1;
            END IF;
            c_villa_data(i) := '   ' || c_new_formula_desc;
            /*
                    PK 09.03.2005, FEDab40788, pb_comment is used to generate autocomments for advance formula
            */
            IF pb_comment THEN
              i := i + 1;
              c_villa_data(i) := ' ';
              i := i + 1;
              c_villa_data(i) := '!##################### End of ' ||
                                 c_ssd_rec.label ||
                                 ' #######################';
            END IF;
            i := i + 1;
            c_villa_data(i) := ' ';
          END IF;
          i := i + 1;
          c_villa_data(i) := '#endcontact ';
          i := i + 1;
          c_villa_data(i) := '#endusecase';
          i := i + 1;
          c_villa_data(i) := ' ';
        END IF;
      END LOOP;
    END LOOP;
    pc_ssd_data := c_villa_data;

  END SSD_File_curve_map;
  PROCEDURE SSD_File_icdm_dependency(pc_ssd_data OUT c_villa_data%TYPE,
                               pb_node_id  number,
                               pb_comment  BOOLEAN DEFAULT FALSE) IS

    CURSOR c_label IS

      SELECT *
        FROM V_LDB2_PAVAST
       WHERE UPPER_LABEL IN (SELECT upper(LABEL) FROM TEMP_LABELLIST_INTERFACE);

    /*to get the ssd data for the labels present in temp table and node_id */
    CURSOR c_ssd(pn_lab_id number,pn_lab_obj_id number) IS
      SELECT distinct a.lab_obj_id,
                      a.typ,
                      a.max_wert,
                      a.min_wert,
                      a.formula formula,
                      a.ssd_grp_label,
                      a.func_label,
                      b.label,
                      a.lab_rev_id,
                      a.dcm2ssd,
                      a.cases,
                      a.contact
        FROM V_LDB2_ICDM_SSD a, TEMP_LABELLIST_INTERFACE b
       WHERE a.node_id = pb_node_id
         and a.lab_lab_id = pn_lab_id
         and a.lab_obj_id = pn_lab_obj_id
         and a.cases IN ('Review')
         and upper(a.label)=upper(b.label)
       ORDER BY ssd_grp_label, func_label, label;

      CURSOR c_ssd_fea_val is
         SELECT * FROM TEMP_SSD2 ;

    c_rel_typ           VARCHAR2(1);
    b_errors            BOOLEAN := FALSE;
    c_releasetyp        VARCHAR2(500) := 'Only internal Release';
    c_ssd_grp_label_old v_ldb2_ssd_grp.label%TYPE := '__xxxxx__';
    c_func_label_old    v_ldb2_pavast.function%TYPE := '__xxxxx__';
    c_nachlabel         VARCHAR2(1010);
    c_nachlabel_frm     VARCHAR2(1010);
    n_count_grp         NUMBER := 0;
    n_count_func        NUMBER := 0;
    c_comp              VARCHAR2(20000);
    c_feature           v_ldb2_features.feature_text%TYPE;
    c_value             v_ldb2_values.value_text%TYPE;
    c_default           CHAR;
    c_rev_data1         VARCHAR2(4000);
    c_rev_data2         VARCHAR2(4000);
    c_rev_data3         VARCHAR2(4000);
    c_rev_data          VARCHAR2(4000);
    c_new_formula_desc  VARCHAR2(32767);
    i                   BINARY_INTEGER := 1;
    c_cases             VARCHAR2(200);
    LI_Counter          NUMBER;
    n_rev_cnt           NUMBER := 0;
    c_global_chk        CHAR;
    c_global_type       VARCHAR2(10);
    c_ecutype           VARCHAR2(20);
    c_global_ssdheader  v_ldb2_project_release.global_ssdheader%TYPE;
    dcm2ssd_data        pkg_dcm2ssd.ssd_file;
    n_config_cnt        NUMBER;
    n_level_cnt         NUMBER;
    c_description1      VARCHAR2(32000);
    c_rev_description1  VARCHAR2(32000);
    n_node              v_ldb2_object_tree.node_id%type;
    c_desc_substr1      VARCHAR2(10000);
    c_desc_substr2      VARCHAR2(10000);

  BEGIN
    c_villa_data.DELETE;
    c_villa_data(i) := '!  SSD-Report for ' || c_ecutype ||
                       ' Software, SSD-Labeldatabase, for ICDM';
    i := i + 1;
    c_villa_data(i) := '!  ============================================================================================';
    i := i + 1;
    c_villa_data(i) := ' ';
    i := i + 1;
    c_villa_data(i) := '!  System-spezifische Software-Daten';

    /*To include SSD data*/
    FOR c_label_rec IN c_label LOOP

     FOR c_fea_val IN c_ssd_fea_val LOOP


      FOR c_ssd_rec IN c_ssd(c_label_rec.LAB_ID,c_fea_val.LAB_OBJ_ID) LOOP
          dbms_output.put_line('lab_obj_id    ' || c_fea_val.LAB_OBJ_ID  );

        /*To fetch advanced formula as it is clob field and cannot be queried directly from the select statement*/
        c_new_formula_Desc := pkg_clob.Get_Formdesc(c_ssd_rec.lab_obj_id,
                                                    c_ssd_rec.lab_rev_id);

                                                    dbms_output.put_line('c_new_formula_Desc     ' || c_new_formula_Desc  );

        IF c_new_formula_desc = '-2' THEN
          NULL;
        ELSE
          c_default := 'N';
          IF c_ssd_rec.ssd_grp_label != c_ssd_grp_label_old THEN
            n_count_grp := n_count_grp + 1;
            c_ssd_grp_label_old := c_ssd_rec.ssd_grp_label;
            c_func_label_old := '__xxxxxx__';
            n_count_func := 0;
            i := i + 1;
            c_villa_data(i) := ' ';
            i := i + 1;
            c_villa_data(i) := '! ' || to_char(n_count_grp) || '. ' ||
                               c_ssd_rec.ssd_grp_label;
            i := i + 1;
            c_villa_data(i) := '! =========================================================';
            i := i + 1;
            c_villa_data(i) := ' ';
          END IF;

          IF c_ssd_rec.func_label != c_func_label_old THEN
            n_count_func := n_count_func + 1;
            c_func_label_old := c_ssd_rec.func_label;
            i := i + 1;
            c_villa_data(i) := '! ' || to_char(n_count_grp) || '.' ||
                               to_char(n_count_func) || ' ' ||
                               c_ssd_rec.func_label;
            i := i + 1;
            c_villa_data(i) := '! ---------------------------------------------------------';
            i := i + 1;
            c_villa_data(i) := ' ';
          END IF;

          i := i + 1;
          --------------------------------------------------------------------------------------------
          c_villa_data(i) := '#usecase ' || c_ssd_rec.cases;

          i := i + 1;
          c_villa_data(i) := '#contact "' || c_ssd_rec.contact || '"';
          i := i + 1;
          c_villa_data(i) := ' ';
          -------------------------------------------------------------------------------
          IF c_ssd_rec.dcm2ssd = 'Y' THEN
            dcm2ssd_data := pkg_dcm2ssd.convert_dcm2ssd(c_ssd_rec.lab_obj_id,
                                                        c_ssd_rec.lab_rev_id);
            FOR j IN 1 .. dcm2ssd_data.count LOOP
              i := i + 1;
              c_villa_data(i) := dcm2ssd_data(j);
            END LOOP;
          END IF;
          -----------------------------------------
          c_nachlabel_frm := ', ' || c_ssd_rec.formula;
          c_nachlabel     := null;

          -----------------------------------------
          /* zuwAD_Inj, MAP ( <= 8000, ALL, ALL ) ! map
          zuwAD_Inj, MAP ( >= 5000 && <=8000, ALL, ALL ) ! map
          mrwBDB_KL, MAP ( 0.0, ALL ) ! curve*/

          -------------------------------------
          IF c_ssd_rec.min_wert IS NOT NULL and c_ssd_rec.max_wert is NULL and
             c_label_rec.category like 'MAP%' THEN
            c_nachlabel := ',MAP(>=' ||
                           replace(to_char(c_ssd_rec.min_wert), ',', '.') ||
                           ',ALL,ALL)';

          ELSIF c_ssd_rec.min_wert IS NULL and
                c_ssd_rec.max_wert is NOT NULL and
                c_label_rec.category like 'MAP%' THEN
            c_nachlabel := ',MAP(<=' ||
                           replace(to_char(c_ssd_rec.max_wert), ',', '.') ||
                           ',ALL,ALL)';

          ELSIF c_ssd_rec.min_wert IS NOT NULL and
                c_ssd_rec.max_wert is NOT NULL and
                c_label_rec.category like 'MAP%' THEN
            c_nachlabel := ',MAP(>=' ||
                           replace(to_char(c_ssd_rec.min_wert), ',', '.') || ' ' ||
                           chr(38) || chr(38) || ' <=' ||
                           replace(to_char(c_ssd_rec.max_wert), ',', '.') ||
                           ',ALL,ALL)';

          ELSIF c_ssd_rec.min_wert IS NOT NULL and
                c_ssd_rec.max_wert is NULL and
                c_label_rec.category like 'CURVE%' THEN
            c_nachlabel := ',MAP(>=' ||
                           replace(to_char(c_ssd_rec.min_wert), ',', '.') ||
                           ',ALL)';

          ELSIF c_ssd_rec.min_wert IS NULL and
                c_ssd_rec.max_wert is NOT NULL and
                c_label_rec.category like 'CURVE%' THEN
            c_nachlabel := ',MAP(<=' ||
                           replace(to_char(c_ssd_rec.max_wert), ',', '.') ||
                           ',ALL)';

          ELSIF c_ssd_rec.min_wert IS NOT NULL and
                c_ssd_rec.max_wert is NOT NULL and
                c_label_rec.category like 'CURVE%' THEN
            c_nachlabel := ',MAP(>=' ||
                           replace(to_char(c_ssd_rec.min_wert), ',', '.') || ' ' ||
                           chr(38) || chr(38) || ' <=' ||
                           replace(to_char(c_ssd_rec.max_wert), ',', '.') ||
                           ',ALL)';

          ELSIF c_ssd_rec.min_wert IS NOT NULL and
                c_ssd_rec.max_wert is NOT NULL and
                (c_label_rec.category not like 'CURVE%' or
                c_label_rec.category not like 'MAP') THEN
            c_nachlabel := ', >=' ||
                           replace(to_char(c_ssd_rec.min_wert), ',', '.');

            c_nachlabel := c_nachlabel || ', <=' ||
                           replace(to_char(c_ssd_rec.max_wert), ',', '.');

          ELSIF c_ssd_rec.min_wert IS NOT NULL and
                c_ssd_rec.max_wert is NULL and
                (c_label_rec.category not like 'CURVE%' or
                c_label_rec.category not like 'MAP') THEN
            c_nachlabel := ', >=' ||
                           replace(to_char(c_ssd_rec.min_wert), ',', '.');

          ELSIF c_ssd_rec.min_wert IS NULL and
                c_ssd_rec.max_wert is NOT NULL and
                (c_label_rec.category not like 'CURVE%' or
                c_label_rec.category not like 'MAP') THEN

            c_nachlabel := c_nachlabel || ', <=' ||
                           replace(to_char(c_ssd_rec.max_wert), ',', '.');

          ELSIF (c_ssd_rec.min_wert IS NULL AND c_ssd_rec.max_wert IS NULL AND
                c_ssd_rec.typ IS NOT NULL) THEN

            c_default   := 'Y';
            c_nachlabel := ', =' ||
                           replace(to_char(c_ssd_rec.typ), ',', '.');
          END IF;

          /*IF c_ssd_rec.min_wert IS NOT NULL THEN
                 c_nachlabel := ', >=' ||
                           replace(to_char(c_ssd_rec.min_wert), ',', '.');
              END IF;



          IF c_ssd_rec.max_wert IS NOT NULL THEN
            c_nachlabel := c_nachlabel || ', <=' ||
                           replace(to_char(c_ssd_rec.max_wert), ',', '.');
          END IF;

          IF (c_ssd_rec.min_wert IS NULL AND c_ssd_rec.max_wert IS NULL AND
             c_ssd_rec.typ IS NOT NULL) THEN
            c_default   := 'Y';
            c_nachlabel := ', =' || replace(to_char(c_ssd_rec.typ), ',', '.');
          END IF;*/

          /* if min ,max and formula and advanced formula are null then only default is considered in the file */
          IF c_default = 'Y' THEN
            IF c_ssd_rec.formula IS NULL AND c_new_formula_desc IS NULL AND
               c_nachlabel IS NOT NULL THEN
              i := i + 1;
              c_villa_data(i) := '   ' || c_ssd_rec.label || c_nachlabel;
              i := i + 1;
              c_villa_data(i) := ' ';
            END IF;
          ELSE
            IF c_nachlabel IS NOT NULL THEN
              i := i + 1;
              c_villa_data(i) := '   ' || c_ssd_rec.label || c_nachlabel;
              i := i + 1;
              c_villa_data(i) := ' ';
            END IF;
          END IF;
          IF c_ssd_rec.formula IS NOT NULL THEN
            i := i + 1;
            c_villa_data(i) := '   ' || c_ssd_rec.label || ', ' ||
                               c_ssd_rec.formula;
            i := i + 1;
            c_villa_data(i) := ' ';
          END IF;
          IF c_new_formula_desc IS NOT NULL THEN
            i := i + 1;
            /*
                    PK 09.03.2005, FEDab40788, pb_comment is used to generate autocomments for advance formula
            */
            IF pb_comment THEN
              c_villa_data(i) := '!##################### Begin of ' ||
                                 c_ssd_rec.label ||
                                 ' #######################';
              i := i + 1;
              c_villa_data(i) := ' ';
              i := i + 1;
            END IF;
            c_villa_data(i) := '   ' || c_new_formula_desc;
            /*
                    PK 09.03.2005, FEDab40788, pb_comment is used to generate autocomments for advance formula
            */
            IF pb_comment THEN
              i := i + 1;
              c_villa_data(i) := ' ';
              i := i + 1;
              c_villa_data(i) := '!##################### End of ' ||
                                 c_ssd_rec.label ||
                                 ' #######################';
            END IF;
            i := i + 1;
            c_villa_data(i) := ' ';
          END IF;
          i := i + 1;
          c_villa_data(i) := '#endcontact ';
          i := i + 1;
          c_villa_data(i) := '#endusecase';
          i := i + 1;
          c_villa_data(i) := ' ';
        END IF;
      END LOOP;
     END LOOP;
    END LOOP;
    pc_ssd_data := c_villa_data;

  END SSD_File_icdm_dependency;

END pkg_villa_interface_ssdtest;
/

grant execute on pkg_villa_interface_ssdtest to ldb2_user;


  CREATE OR REPLACE PROCEDURE "K5ESK_LDB2"."POPULATE_TEMP_SSD" 
(N_RESULT OUT NUMBER,
P_OBJ_ID_1 IN NUMBER )AS 

Cursor cur_ssd is
select distinct a.lab_obj_id        
          from v_ldb2_ssd2 a, v_ldb2_pavast b 
          where a.lab_lab_id=b.lab_id and 
          a.obj_id_1= P_OBJ_ID_1 
          and a.historie='N' and a.lab_lab_id in 
         (select lab_Id from V_Ldb2_Pavast where upper_Label in 
         (SELECT DISTINCT(upper(label)) from TEMP_LABELLIST_INTERFACE ));

cursor cur_ssd_feaval (n_lab_obj_id number) is
select * from v_ldb2_comp where lab_obj_id=n_lab_obj_id and historie='N';

cursor cur_temp_features is
select * from T_SSD2_TEMP_FEAVAL; 

p_count number:=0;
p_chk boolean := false;
p_default boolean := false;

BEGIN

N_RESULT:=0;
  FOR val_ssd in cur_ssd loop
    p_chk:=true;
    p_default:=true; -- to eliminate deafult record
    for val_feaVal in cur_ssd_feaval(val_ssd.lab_obj_id) loop
    p_default:=false;
      select count(1) into p_count from T_SSD2_TEMP_FEAVAL where feature_id=val_feaVal.feature_id and
      value_id=val_feaVal.value_id;
      if p_count <1 then
        p_chk:=false;
        EXIT;
      end if;
    
    end loop;
    
    if p_chk=true and p_default=false then
    insert into TEMP_SSD2 values (val_ssd.lab_obj_id);
    end if;
  
  end loop;
 
   insert into temp_ssd2 (select distinct a.lab_obj_id        
          from v_ldb2_ssd2 a, v_ldb2_pavast b 
          where a.lab_lab_id=b.lab_id and 
          a.obj_id_1= P_OBJ_ID_1 
          and a.historie='N' and a.lab_lab_id in 
         (select lab_Id from V_Ldb2_Pavast where upper_Label in 
         (SELECT DISTINCT(upper(label)) from TEMP_LABELLIST_INTERFACE ))
         and a.lab_lab_id not in (select lab_lab_id from v_ldb2_ssd2 where lab_obj_id in (select lab_obj_id from temp_ssd2))
         and a.lab_obj_id not in (select lab_obj_id from temp_ssd2)
         and a.lab_obj_id not in (select lab_obj_id from v_ldb2_ssd_comp));
  
END POPULATE_TEMP_SSD;

/

grant execute on POPULATE_TEMP_SSD to ldb2_user;


 CREATE OR REPLACE PROCEDURE "K5ESK_LDB2"."SP_WRITECDF_V1" (pn_pro_rel_id  NUMBER,
                                                        cdfminMaxCheck VARCHAR2,
                                                        chk_ssd        VARCHAR2,
                                                        chk_mft        VARCHAR2,
                                                        app_hint       VARCHAR2,
                                                        chk_cmpPck     VARCHAR2,
                                                        ecutype        VARCHAR2,
                                                        userId         VARCHAR2,
                                                        primary_id     OUT VARCHAR2) IS

  /*this procedure is used to write the cdf related information in temporary files
  - its basically dcm data along with state and comments defined
  */

  CURSOR c_dcm IS
    SELECT typ,
           max_wert,
           min_wert,
           formula formula,
           label,
           dcm_row_number,
           dcm_historie,
           dcm_data,
           ssd_grp_label,
           func_label,
           dim,
           lab_lab_id,
           obj_id_1,
           rev_id,
           lab_obj_id,
           maturity,
           data_desc,
           cases
      FROM V_LDB2_DCM_SSDNODE_MATURITY a /*V_LDB2_DCM_SSDNODE_MATURITY created to add maturity and data description*/
     WHERE pro_rel_id = pn_pro_rel_id
       AND CASES IN (chk_mft, chk_ssd, app_hint, chk_cmpPck)
     ORDER BY label, dcm_row_number;

  CURSOR cur_lab_info(pn_lab_id NUMBER) IS
    SELECT typ, subtyp FROM v_ldb2_common_label WHERE lab_id = pn_lab_id;

  n_pro_rev_id NUMBER;
  l_rel_typ    VARCHAR2(1);
  l_rel_desc   VARCHAR2(4000);
  k            NUMBER := 0;
  rec_lab_info cur_lab_info%rowtype;
  l_label_old  VARCHAR2(100) := '__xxyxx__';
  temp_data    VARCHAR2(4000);
  temp_comment VARCHAR2(4000);
  maturity_comment varchar2(100);
  data_comment varchar2(4000);
  feature_value varchar2(1000);
  node_inf varchar2(1000);

BEGIN

  primary_id := pn_pro_rel_id || '_CDFReport_' ||
                nvl(userId, 'Invalid User');

  BEGIN
    SELECT rel_typ, description
      INTO l_rel_typ, l_rel_desc
      FROM v_ldb2_project_release
     WHERE pro_rel_id = pn_pro_rel_id;

  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
  END;

  BEGIN
    SELECT pro_rev_id
      INTO n_pro_rev_id
      FROM v_ldb2_project_release
     WHERE pro_rel_id = pn_pro_rel_id;

  EXCEPTION
    WHEN no_data_Found THEN
      NULL;
  END;

  k := 0;

  FOR c_dcm_rec IN c_dcm LOOP
    -- comment variable to include node where rule is defined and feature value combination
    temp_comment := 'Rule Defined at : ' ||
                    ldb2_full_scope(c_dcm_rec.obj_id_1) || chr(10) ||
                    'For Feature-value : ' ||
                    pkg_common.get_features_values(c_dcm_rec.lab_obj_id,
                                                   c_dcm_rec.rev_id);
    feature_value:=pkg_common.get_features_values(c_dcm_rec.lab_obj_id,
                                                   c_dcm_rec.rev_id);
    data_comment :=  c_dcm_rec.data_desc ;
    node_inf:=ldb2_full_scope(c_dcm_rec.obj_id_1);
    maturity_comment :=chr(10) || 'SSD maturity level : '  ;

    if c_dcm_rec.dcm_data is null then

      OPEN cur_lab_info(c_dcm_rec.lab_lab_id);
      FETCH cur_lab_info
        into rec_lab_info;
      CLOSE cur_lab_info;

      -- added Cint and Kw typ and subtyp
      if (c_dcm_rec.typ is not null and (rec_lab_info.typ = 'Kgs' OR rec_lab_info.typ = 'Cint') and rec_lab_info.subtyp = 'Kw') then
        k := k + 1;
        insert into temp_dcmDataList values (k, primary_id, ' ');
        temp_data := '** ' || c_dcm_rec.label || '  ' ||
                     ldb2_longname(c_dcm_rec.lab_lab_id, ecutype, 'E');
        k         := k + 1;
        insert into temp_dcmDataList values (k, primary_id, temp_data);
        k := k + 1;
        insert into temp_dcmDataList values (k, primary_id, ' ');
        temp_data := 'FESTWERT         ' || c_dcm_rec.label;
        k         := k + 1;
        insert into temp_dcmDataList values (k, primary_id, temp_data);
        temp_data := 'EINHEIT_W "' || c_dcm_rec.dim || '"';
        k         := k + 1;
        insert into temp_dcmDataList values (k, primary_id, temp_data);
        temp_data := ' WERT ' || replace(c_dcm_rec.typ, ',', '.');
        k         := k + 1;
        insert into temp_dcmDataList values (k, primary_id, temp_data);
        k := k + 1;
        insert into temp_dcmDataList values (k, primary_id, 'END');
        k := k + 1;
        insert into temp_dcmDataList values (k, primary_id, ' ');

        if (c_dcm_rec.min_wert = c_dcm_rec.max_wert and c_dcm_rec.maturity is null) then

            insert into TEMP_CDFDATALIST_COMMENTS(LABEL,UNI_ID,STATE,MATURITY,FEATUREVALUE,USECASE,NODE_INF,DATA_DESC) values
            (c_dcm_rec.label,primary_id, 'checked',nvl2(c_dcm_rec.maturity,c_dcm_rec.maturity,'None'),feature_value,c_dcm_rec.cases ,node_inf,data_comment);

        elsif ((c_dcm_rec.min_wert = c_dcm_rec.max_wert or
              c_dcm_rec.typ is not null) and upper(c_dcm_rec.maturity) = upper('Fixed')) then

          insert into TEMP_CDFDATALIST_COMMENTS(LABEL,UNI_ID,STATE,MATURITY,FEATUREVALUE,USECASE,NODE_INF,DATA_DESC) values
            (c_dcm_rec.label, primary_id, 'checked',nvl2(c_dcm_rec.maturity,c_dcm_rec.maturity,'None'),feature_value,c_dcm_rec.cases ,node_inf,data_comment);

        elsif ((c_dcm_rec.min_wert = c_dcm_rec.max_wert or c_dcm_rec.typ is not null) and upper(c_dcm_rec.maturity) = upper('Standard')) then

          insert into TEMP_CDFDATALIST_COMMENTS(LABEL,UNI_ID,STATE,MATURITY,FEATUREVALUE,USECASE,NODE_INF,DATA_DESC)  values
            (c_dcm_rec.label, primary_id, 'calibrated',nvl2(c_dcm_rec.maturity,c_dcm_rec.maturity,'None'),feature_value,c_dcm_rec.cases ,node_inf,data_comment);

        elsif ((c_dcm_rec.min_wert = c_dcm_rec.max_wert or c_dcm_rec.typ is not null) and upper(c_dcm_rec.maturity) = upper('Start')) then

          insert into TEMP_CDFDATALIST_COMMENTS(LABEL,UNI_ID,STATE,MATURITY,FEATUREVALUE,USECASE,NODE_INF,DATA_DESC)  values
            (c_dcm_rec.label, primary_id,'prelimCalibrated',nvl2(c_dcm_rec.maturity,c_dcm_rec.maturity,'None'),feature_value,c_dcm_rec.cases ,node_inf,data_comment);

        elsif (c_dcm_rec.typ is not null and c_dcm_rec.maturity is null) then

          insert into TEMP_CDFDATALIST_COMMENTS(LABEL,UNI_ID,STATE,MATURITY,FEATUREVALUE,USECASE,NODE_INF,DATA_DESC)  values
            (c_dcm_rec.label, primary_id,'prelimCalibrated',nvl2(c_dcm_rec.maturity,c_dcm_rec.maturity,'None'),feature_value,c_dcm_rec.cases ,node_inf,data_comment);

        else
          -- if min!=max then it is prelimcalibrated
          insert into TEMP_CDFDATALIST_COMMENTS(LABEL,UNI_ID,STATE,MATURITY,FEATUREVALUE,USECASE,NODE_INF,DATA_DESC)  values
            (c_dcm_rec.label, primary_id,'prelimCalibrated',nvl2(c_dcm_rec.maturity,c_dcm_rec.maturity,'None'),feature_value,c_dcm_rec.cases ,node_inf,data_comment);
        end if;

      elsif (c_dcm_rec.min_wert = c_dcm_rec.max_wert and   c_dcm_rec.typ is null and  (rec_lab_info.typ = 'Kgs' OR rec_lab_info.typ = 'Cint') and
            rec_lab_info.subtyp = 'Kw' and cdfminMaxCheck = 'true') then

        k := k + 1;
        insert into temp_dcmDataList values (k, primary_id, ' ');
        temp_data := '** ' || c_dcm_rec.label || '  ' ||
                     ldb2_longname(c_dcm_rec.lab_lab_id, ecutype, 'E');
        k         := k + 1;
        insert into temp_dcmDataList values (k, primary_id, temp_data);
        k := k + 1;
        insert into temp_dcmDataList values (k, primary_id, ' ');
        temp_data := 'FESTWERT         ' || c_dcm_rec.label;
        k         := k + 1;
        insert into temp_dcmDataList values (k, primary_id, temp_data);
        temp_data := 'EINHEIT_W "' || c_dcm_rec.dim || '"';
        k         := k + 1;
        insert into temp_dcmDataList values (k, primary_id, temp_data);
        --- temp_data := ' WERT '||replace(c_dcm_rec.typ, ',', '.');
        temp_data := ' WERT ' || replace(c_dcm_rec.min_wert, ',', '.');

        k := k + 1;
        insert into temp_dcmDataList values (k, primary_id, temp_data);
        k := k + 1;
        insert into temp_dcmDataList values (k, primary_id, 'END');
        k := k + 1;
        insert into temp_dcmDataList values (k, primary_id, ' ');
        -- for state and comment fields in cdf file (if recommended is null)
        if (c_dcm_rec.min_wert = c_dcm_rec.max_wert and  c_dcm_rec.maturity is null) then

          insert into TEMP_CDFDATALIST_COMMENTS(LABEL,UNI_ID,STATE,MATURITY,FEATUREVALUE,USECASE,NODE_INF,DATA_DESC)    values
            (c_dcm_rec.label,  primary_id,  'checked',nvl2(c_dcm_rec.maturity,c_dcm_rec.maturity,'None'),feature_value,c_dcm_rec.cases ,node_inf,data_comment);

        elsif ((c_dcm_rec.min_wert = c_dcm_rec.max_wert or c_dcm_rec.typ is not null) and upper(c_dcm_rec.maturity) = upper('Fixed')) then

          insert into TEMP_CDFDATALIST_COMMENTS(LABEL,UNI_ID,STATE,MATURITY,FEATUREVALUE,USECASE,NODE_INF,DATA_DESC)   values
            (c_dcm_rec.label,  primary_id, 'checked',nvl2(c_dcm_rec.maturity,c_dcm_rec.maturity,'None'),feature_value,c_dcm_rec.cases ,node_inf,data_comment);

        elsif ((c_dcm_rec.min_wert = c_dcm_rec.max_wert or  c_dcm_rec.typ is not null) and  upper(c_dcm_rec.maturity) = upper('Standard')) then

          insert into TEMP_CDFDATALIST_COMMENTS(LABEL,UNI_ID,STATE,MATURITY,FEATUREVALUE,USECASE,NODE_INF,DATA_DESC)  values
            (c_dcm_rec.label, primary_id, 'calibrated',nvl2(c_dcm_rec.maturity,c_dcm_rec.maturity,'None'),feature_value,c_dcm_rec.cases ,node_inf,data_comment);

        elsif ((c_dcm_rec.min_wert = c_dcm_rec.max_wert or c_dcm_rec.typ is not null) and upper(c_dcm_rec.maturity) = upper('Start')) then

           insert into TEMP_CDFDATALIST_COMMENTS(LABEL,UNI_ID,STATE,MATURITY,FEATUREVALUE,USECASE,NODE_INF,DATA_DESC)  values
              (c_dcm_rec.label, primary_id,'prelimCalibrated',nvl2(c_dcm_rec.maturity,c_dcm_rec.maturity,'None'),feature_value,c_dcm_rec.cases ,node_inf,data_comment);

        elsif (c_dcm_rec.typ is not null and c_dcm_rec.maturity is null) then

          insert into TEMP_CDFDATALIST_COMMENTS(LABEL,UNI_ID,STATE,MATURITY,FEATUREVALUE,USECASE,NODE_INF,DATA_DESC)   values
            (c_dcm_rec.label, primary_id,'prelimCalibrated',nvl2(c_dcm_rec.maturity,c_dcm_rec.maturity,'None'),feature_value,c_dcm_rec.cases ,node_inf,data_comment);

        else

          insert into TEMP_CDFDATALIST_COMMENTS(LABEL,UNI_ID,STATE,MATURITY,FEATUREVALUE,USECASE,NODE_INF,DATA_DESC)   values
            (c_dcm_rec.label,  primary_id,'calibrated',nvl2(c_dcm_rec.maturity,c_dcm_rec.maturity,'None'),feature_value,c_dcm_rec.cases ,node_inf,data_comment);

        end if;
      end if;

    else
      if c_dcm_rec.label != l_label_old then
        k := k + 1;
        insert into temp_dcmDataList values (k, primary_id, ' ');
        temp_data := '** ' || c_dcm_rec.label || '  ' ||
                     ldb2_longname(c_dcm_rec.lab_lab_id, ecutype, 'E');
        k         := k + 1;
        insert into temp_dcmDataList values (k, primary_id, temp_data);
        k := k + 1;
        insert into temp_dcmDataList values (k, primary_id, ' ');
        l_label_old := c_dcm_rec.label;
      end if;
      k := k + 1;
      insert into temp_dcmDataList  values  (k, primary_id, c_dcm_rec.dcm_data);
      -- for state and comment fields in cdf file (when dcm data is present for a rule)
      if (c_dcm_rec.min_wert = c_dcm_rec.max_wert and   c_dcm_rec.maturity is null) then

        insert into TEMP_CDFDATALIST_COMMENTS(LABEL,UNI_ID,STATE,MATURITY,FEATUREVALUE,USECASE,NODE_INF,DATA_DESC) values
          (c_dcm_rec.label,  primary_id,'checked',nvl2(c_dcm_rec.maturity,c_dcm_rec.maturity,'None'),feature_value,c_dcm_rec.cases ,node_inf,data_comment);

      elsif ((c_dcm_rec.min_wert = c_dcm_rec.max_wert or  c_dcm_rec.dcm_data is not null or c_dcm_rec.typ is not null) and
            upper(c_dcm_rec.maturity) = upper('Fixed')) then

        insert into TEMP_CDFDATALIST_COMMENTS(LABEL,UNI_ID,STATE,MATURITY,FEATUREVALUE,USECASE,NODE_INF,DATA_DESC) values
          (c_dcm_rec.label,  primary_id, 'checked',nvl2(c_dcm_rec.maturity,c_dcm_rec.maturity,'None'),feature_value,c_dcm_rec.cases ,node_inf,data_comment);

      elsif ((c_dcm_rec.min_wert = c_dcm_rec.max_wert or  c_dcm_rec.dcm_data is not null or c_dcm_rec.typ is not null) and
            upper(c_dcm_rec.maturity) = upper('Standard')) then

        insert into TEMP_CDFDATALIST_COMMENTS(LABEL,UNI_ID,STATE,MATURITY,FEATUREVALUE,USECASE,NODE_INF,DATA_DESC)  values
          (c_dcm_rec.label, primary_id, 'calibrated',nvl2(c_dcm_rec.maturity,c_dcm_rec.maturity,'None'),feature_value,c_dcm_rec.cases ,node_inf,data_comment);

      elsif ((c_dcm_rec.min_wert = c_dcm_rec.max_wert or  c_dcm_rec.dcm_data is not null or c_dcm_rec.typ is not null) and
            upper(c_dcm_rec.maturity) = upper('Start')) then

        insert into TEMP_CDFDATALIST_COMMENTS(LABEL,UNI_ID,STATE,MATURITY,FEATUREVALUE,USECASE,NODE_INF,DATA_DESC)  values
          (c_dcm_rec.label, primary_id,'prelimCalibrated',nvl2(c_dcm_rec.maturity,c_dcm_rec.maturity,'None'),feature_value,c_dcm_rec.cases ,node_inf,data_comment);

      elsif ((c_dcm_rec.dcm_data is not null or c_dcm_rec.typ is not null) and  c_dcm_rec.maturity is null) then

         insert into TEMP_CDFDATALIST_COMMENTS(LABEL,UNI_ID,STATE,MATURITY,FEATUREVALUE,USECASE,NODE_INF,DATA_DESC)   values
          (c_dcm_rec.label,  primary_id,'prelimCalibrated',nvl2(c_dcm_rec.maturity,c_dcm_rec.maturity,'None'),feature_value,c_dcm_rec.cases ,node_inf,data_comment);

      else

        insert into TEMP_CDFDATALIST_COMMENTS(LABEL,UNI_ID,STATE,MATURITY,FEATUREVALUE,USECASE,NODE_INF,DATA_DESC)    values
          (c_dcm_rec.label,    primary_id,  'prelimCalibrated',nvl2(c_dcm_rec.maturity,c_dcm_rec.maturity,'None'),feature_value,c_dcm_rec.cases ,node_inf,data_comment);

     end if;
    end if;

  END LOOP;

  commit;

END SP_WRITECDF_V1;

/

grant execute on SP_WRITECDF_V1 to ldb2_user;



  CREATE OR REPLACE TRIGGER "K5ESK_LDB2"."TRG_LDB2_SSD_U" 
BEFORE  UPDATE  ON T_LDB2_SSD2  FOR EACH ROW
DECLARE
    anzahl NUMBER;
BEGIN
    IF (:NEW.lab_obj_id != :OLD.lab_obj_id)
       --or (:new.rev_id != :old.rev_id)
       THEN
      RAISE_APPLICATION_ERROR(-20021,'Update of key-Value not permitted');
 END IF;
    SELECT COUNT(*) INTO anzahl FROM T_LDB2_LABEL WHERE lab_id = :NEW.lab_lab_id
;
 IF anzahl = 0 THEN
      SELECT COUNT(*) INTO anzahl FROM T_LDB2_PAVAST WHERE lab_id = :NEW.lab_lab_id;
    IF anzahl = 0 THEN
      RAISE_APPLICATION_ERROR(-20020,'No valid lab_lab_id');
    END IF;
 END IF;
 SELECT COUNT(*) INTO anzahl FROM T_LDB2_SCOPE WHERE scope_id = :NEW.scope;
 IF anzahl = 0 THEN
      RAISE_APPLICATION_ERROR(-20021,'No valid scope');
    END IF;
 anzahl := 0;

/*Ra.27.06.2001.Check for the existence of the obj_id_1 in the object tree  table.*/
             IF (:NEW.scope=1 OR :NEW.scope=3 OR :NEW.scope=5 OR :NEW.scope=7 OR :NEW.scope=8) THEN
                         SELECT COUNT(*) INTO anzahl
                                          FROM T_LDB2_OBJECT_TREE
                                         WHERE node_id=:NEW.obj_id_1;
                              IF anzahl=0 THEN
                      RAISE_APPLICATION_ERROR(-20027,'Invalid Node id');
                              END IF;
             END IF;
              -- RB V4.8  18/12/06 RCMS00318577 New states for SSD Finished flag
    IF :NEW.state != 0 AND :NEW.state != 5 AND :NEW.state != 1 AND :NEW.state != 2 THEN
      RAISE_APPLICATION_ERROR(-20023,'No valid state');
      END IF;
    IF :NEW.ssd_relevance != 'Y' AND :NEW.ssd_relevance != 'N' THEN
      RAISE_APPLICATION_ERROR(-20024,'No valid SSD-Relevance');
 END IF;
 IF :NEW.historie != 'Y' AND :NEW.historie != 'N' THEN
   RAISE_APPLICATION_ERROR(-20025,'No valid Historie');
    END IF;
    IF :NEW.MIN > :NEW.MAX THEN
    RAISE_APPLICATION_ERROR(-20026,'Min grosser als Max');
 END IF;

--     :new.historie:='Y';
--     :new.rev_id:=:old.rev_id+1;
  :NEW.mod_date := SYSDATE;
  If(:NEW.mod_user is null) then
     :NEW.mod_user := USER;
  End if;
  
  END;
/
ALTER TRIGGER "K5ESK_LDB2"."TRG_LDB2_SSD_U" ENABLE;

  CREATE OR REPLACE TRIGGER "K5ESK_LDB2"."TRG_LDB2_SSD_C" 
BEFORE  INSERT  ON T_LDB2_SSD2  FOR EACH ROW
DECLARE
    anzahl NUMBER;
  BEGIN
    SELECT COUNT(*) INTO anzahl FROM T_LDB2_LABEL WHERE lab_id = :NEW.lab_lab_id
;
 IF anzahl = 0 THEN
	SELECT COUNT(*) INTO anzahl FROM T_LDB2_PAVAST WHERE lab_id = :NEW.lab_lab_id;
	 IF anzahl = 0 THEN
           RAISE_APPLICATION_ERROR(-20020,'No valid lab_lab_id');
	 END IF;
        END IF;
 SELECT COUNT(*) INTO anzahl FROM T_LDB2_SCOPE WHERE scope_id = :NEW.scope;
 IF anzahl = 0 THEN
           RAISE_APPLICATION_ERROR(-20021,'No valid scope');
        END IF;
 anzahl := 0;


--Ra.27.06.2001.Check for the existence of the obj_id_1 in the objec tree table.


             IF (:NEW.scope=1 OR :NEW.scope=3 OR :NEW.scope=5 OR :NEW.scope
=7 OR :NEW.scope=8) THEN
                              SELECT COUNT(*
) INTO anzahl FROM T_LDB2_OBJECT_TREE WHERE node_id=:NEW.obj_id_1;
                              IF anzahl=0 THEN
                    RAISE_APPLICATION_ERROR(-20027,'Invalid Object');
                              END IF;
             END IF;

                -- RB V4.8  18/12/06 RCMS00318577 New states for SSD Finished flag
    IF :NEW.state != 0 AND :NEW.state != 5 AND :NEW.state != 1 AND :NEW.state != 2 THEN
      RAISE_APPLICATION_ERROR(-20023,'No valid state');
      END IF;
        IF :NEW.ssd_relevance != 'Y' AND :NEW.ssd_relevance != 'N' THEN
           RAISE_APPLICATION_ERROR(-20024,'No valid SSD-Relevance');
 END IF;
 IF :NEW.historie != 'Y' AND :NEW.historie != 'N' THEN
    RAISE_APPLICATION_ERROR(-20025,'No valid Historie');
        END IF;
        IF :NEW.MIN > :NEW.MAX THEN
    RAISE_APPLICATION_ERROR(-20026,'Min grosser als Max');
 END IF;
        -- SSD_GRP_ID, DEPT_DEPT_ID und Typ noch zu prufen
        IF (:NEW.Lab_obj_id IS NULL) THEN
            SELECT SEQ_LDB2_ALL.NEXTVAL INTO :NEW.lab_obj_id FROM dual;
 END IF;
        :NEW.cre_date := SYSDATE;
        
       If(:NEW.cre_user is null) then
          :NEW.cre_user := USER; 
       end if;
     
      If(:NEW.mod_user is null) then
     :NEW.mod_user := USER;
  End if;
     
        :NEW.mod_date := SYSDATE;        

  END;
/
ALTER TRIGGER "K5ESK_LDB2"."TRG_LDB2_SSD_C" ENABLE;