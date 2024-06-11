spool c:\temp\02_Alters_Data_Archival_Schema.log

----------------------------------------------------------
-- IMPORTANT : To be executed in DGS_ICDM_ARCHIVAL schema
----------------------------------------------------------


--------------------------------------------------------------------------------------------------------------------------------
--  ALM Task : 781459 - mpl : WP Archival: Create DB tables and models for WP Archives
--  New tables are added for storing WP Archival data
--------------------------------------------------------------------------------------------------------------------------------

-- Table t_wp_archival
CREATE TABLE t_wp_archival (
    wp_archival_id     NUMBER NOT NULL,
    baseline_name      VARCHAR2(100 CHAR) NOT NULL,
    pidc_vers_id       NUMBER NOT NULL,
    pidc_vers_fullname VARCHAR2(100 CHAR) NOT NULL,
    pidc_a2l_id        NUMBER NOT NULL,
    a2l_filename       VARCHAR2(100 CHAR) NOT NULL,
    variant_id         NUMBER,
    variant_name       VARCHAR2(100 CHAR),
    resp_id            NUMBER NOT NULL,
    resp_name          VARCHAR2(100 CHAR) NOT NULL,
    wp_id              NUMBER NOT NULL,
    wp_name            VARCHAR2(100 CHAR) NOT NULL,
    wp_defn_vers_id    NUMBER NOT NULL,
    wp_defn_vers_name  VARCHAR2(100 CHAR) NOT NULL,
    file_archival_status VARCHAR2(1 CHAR) DEFAULT 'N' NOT NULL,
    created_date       TIMESTAMP(6) NOT NULL,
    created_user       VARCHAR2(30 CHAR) NOT NULL,
    modified_date      TIMESTAMP (6),
    modified_user      VARCHAR2(30 CHAR),
    version            NUMBER NOT NULL
);

ALTER TABLE t_wp_archival ADD CONSTRAINT t_wp_archival_pk PRIMARY KEY ( wp_archival_id );

-- Table t_wp_files
CREATE TABLE t_wp_files (
    wp_file_id     NUMBER NOT NULL,
    wp_archival_id NUMBER NOT NULL,
    file_name      VARCHAR2(100 CHAR) NOT NULL,
    file_data      BLOB NOT NULL,
    created_date   TIMESTAMP(6) NOT NULL,
    created_user   VARCHAR2(30 CHAR) NOT NULL,
    modified_date  TIMESTAMP(6),
    modified_user  VARCHAR2(30 CHAR),
    version        NUMBER NOT NULL
);

ALTER TABLE t_wp_files ADD CONSTRAINT t_wp_files_pk PRIMARY KEY ( wp_file_id );

ALTER TABLE t_wp_files
    ADD CONSTRAINT t_wp_files_fk FOREIGN KEY ( wp_archival_id )
        REFERENCES t_wp_archival ( wp_archival_id ); 
    
-- Altering the table column size as 1 CHAR instead of 1 BYTE
ALTER TABLE T_DA_DATA_ASSESSMENT  
MODIFY (FILE_ARCHIVAL_STATUS VARCHAR2(1 CHAR) );

--------------------------------------------------------------------------------------------------------------------------------
--  ALM Task : 795910 - Compliance labels count mismatch between data assessment report and baseline of data assessment report
--  New columns are added for storing compli count related to fields
--------------------------------------------------------------------------------------------------------------------------------
ALTER TABLE DGS_ICDM_ARCHIVAL.T_DA_DATA_ASSESSMENT
	ADD (COMPLI_PARAM_IN_A2L NUMBER,
	     COMPLI_PARAM_PASSED NUMBER,
	     COMPLI_PARAM_CSSD_FAIL NUMBER,
	     COMPLI_PARAM_NO_RULE_FAIL NUMBER,
	     COMPLI_PARAM_SSD2RV_FAIL NUMBER,
	     QSSD_PARAM_FAIL NUMBER);

spool off
