spool c:\temp\01_Archival_User_Creation.log

-- ********************************************************
--
-- IMPORTANT : a) To be executed in ADM_USR user
--             b) Verify and Set the correct user name
--             c) Set the correct password for the user
-- ********************************************************

---------------------------------------------------------------------  
--  ALM Task : 702122 - Create schema user for data Archival
---------------------------------------------------------------------

-- Create user
BEGIN 
	p_ADM.CreateUser(
        'DGS_ICDM_ARCHIVAL',
        <password>,
        'PW_180',
        'DGS_ICDM_TS',
        'TEMP2'
    );
END;
/

-- Grant user privileges
BEGIN
    p_ADM.GrantPrivToUser('DGS_ICDM_ARCHIVAL', 'CREATE SESSION');
    p_ADM.GrantPrivToUser('DGS_ICDM_ARCHIVAL', 'ALTER SESSION');
    p_ADM.GrantPrivToUser('DGS_ICDM_ARCHIVAL', 'CREATE TABLE');
    p_ADM.GrantPrivToUser('DGS_ICDM_ARCHIVAL', 'CREATE VIEW');
    p_ADM.GrantPrivToUser('DGS_ICDM_ARCHIVAL', 'CREATE SEQUENCE');
    p_ADM.GrantPrivToUser('DGS_ICDM_ARCHIVAL', 'CREATE TRIGGER');
    p_ADM.GrantPrivToUser('DGS_ICDM_ARCHIVAL', 'CREATE PROCEDURE');
    p_ADM.GrantPrivToUser('DGS_ICDM_ARCHIVAL', 'CREATE TYPE');
    p_ADM.GrantPrivToUser('DGS_ICDM_ARCHIVAL', 'DEBUG ANY PROCEDURE');
    p_ADM.GrantPrivToUser('DGS_ICDM_ARCHIVAL', 'DEBUG CONNECT SESSION');
END;
/

-- Add Quota in the table space
ALTER USER DGS_ICDM_ARCHIVAL quota unlimited on DGS_ICDM_TS;


spool off
