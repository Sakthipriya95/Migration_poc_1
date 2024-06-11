spool c:\temp\02_Alters_Data_Archival_Schema.log

----------------------------------------------------------
-- IMPORTANT : To be executed in DGS_ICDM_ARCHIVAL schema
----------------------------------------------------------


--------------------------------------------------------------------------------------------------------------------------------
--  ALM Task : 702122 - Imp : DB & Entity, Loader, Command, Basic Service, Service Client and Testcases Creation and to Store the Data Assessment Baseline data.
--  New tables are added related to Data Assessment.
-- Updated Tables with ALM Task : 711337: Impl: DB & Entity changes to add additional fields in t_da_parameters, t_da_data_assessment
-- Updated Tables with ALM Task : 698720: impl : Service to create a new baseline for Data Assessment Series Release and Development, create combined zip file in DB
-- Updated Tables with ALM Task : 718916: Impl: Performance improvement for the creation of compare HEX pdf report
--------------------------------------------------------------------------------------------------------------------------------

-- Table : t_da_data_assessment
CREATE TABLE t_da_data_assessment (
    data_assessment_id NUMBER NOT NULL,
    baseline_name      VARCHAR2(200 CHAR) NOT NULL,
    description        VARCHAR2(4000 CHAR),
    type_of_assignment VARCHAR2(1 CHAR) NOT NULL,
    pidc_vers_id       NUMBER NOT NULL,
    pidc_vers_fullname VARCHAR2(2000 CHAR) NOT NULL,
    variant_id         NUMBER,
    variant_name       VARCHAR2(2000 CHAR),
    pidc_a2l_id        NUMBER NOT NULL,
    a2l_filename       VARCHAR2(2000 CHAR) NOT NULL,
    hex_file_name      VARCHAR2(2000 CHAR),
    wp_defn_vers_id    NUMBER NOT NULL,
    wp_defn_vers_name  VARCHAR2(50 CHAR) NOT NULL, 
    vcdm_dst_source    VARCHAR2(2000 CHAR),
    vcdm_dst_vers_id   NUMBER,
    file_archival_status VARCHAR2(1 CHAR) DEFAULT 'N' NOT NULL,
    created_date       TIMESTAMP NOT NULL,
    created_user       VARCHAR2(30 CHAR) NOT NULL,
    modified_date      TIMESTAMP,
    modified_user      VARCHAR2(30 CHAR),
    version            NUMBER NOT NULL
    );

ALTER TABLE t_da_data_assessment 
    ADD CONSTRAINT t_data_assessment_pk PRIMARY KEY ( data_assessment_id );

-- Table : t_da_files
CREATE TABLE t_da_files (
    da_file_id         NUMBER NOT NULL,
    data_assessment_id NUMBER NOT NULL,
    file_name          VARCHAR2(200 CHAR) NOT NULL,
    file_data          BLOB NOT NULL,
    created_date       TIMESTAMP NOT NULL,
    created_user       VARCHAR2(30 CHAR) NOT NULL,
    modified_date      TIMESTAMP,
    modified_user      VARCHAR2(30 CHAR),
    version            NUMBER NOT NULL
);

ALTER TABLE t_da_files 
    ADD CONSTRAINT t_da_files_pk PRIMARY KEY ( da_file_id );

-- Table : t_da_parameters
CREATE TABLE t_da_parameters (
    da_param_id                   NUMBER NOT NULL,
    da_wp_resp_id                 NUMBER NOT NULL,
    parameter_id                  NUMBER NOT NULL,
    parameter_name                VARCHAR2(255 CHAR) NOT NULL,
    parameter_type                VARCHAR2(31 CHAR),
    compli_flag                   VARCHAR2(1 CHAR) DEFAULT 'N' NOT NULL,
    qssd_flag                     VARCHAR2(1 CHAR) DEFAULT 'N' NOT NULL,
    read_only_flag                VARCHAR2(1 CHAR) DEFAULT 'N' NOT NULL,
    dependent_characteristic_flag VARCHAR2(1 CHAR) DEFAULT 'N' NOT NULL,
    black_list_flag               VARCHAR2(1 CHAR) DEFAULT 'N' NOT NULL,
    function_name                 VARCHAR2(255 CHAR),
    function_version              VARCHAR2(255 CHAR),
    rvw_a2l_version               VARCHAR2(50 CHAR),
    rvw_func_version              VARCHAR2(200 CHAR),
    questionnaire_status          VARCHAR2(1 CHAR) NOT NULL,
    reviewed_flag                 VARCHAR2(1 CHAR) NOT NULL,
    never_reviewed_flag           VARCHAR2(1 CHAR) NOT NULL,
    equals_flag                   VARCHAR2(1 CHAR),
    hex_value                     BLOB,
    reviewed_value                BLOB,
    compli_result                 VARCHAR2(50 CHAR),
    qssd_result                   VARCHAR2(50 CHAR),
    compli_tooltip                VARCHAR2(50 CHAR),
    qssd_tooltip                  VARCHAR2(50 CHAR),
    review_score                  VARCHAR2(1 CHAR),
    review_remark                 VARCHAR2(4000 CHAR),
    result_id                     NUMBER,
    rvw_param_id                  NUMBER,
    rvw_result_name               VARCHAR2(4000 CHAR),
    created_date                  TIMESTAMP NOT NULL,
    created_user                  VARCHAR2(30 CHAR) NOT NULL,
    modified_date                 TIMESTAMP,
    modified_user                 VARCHAR2(30 CHAR),
    version                       NUMBER NOT NULL
);

COMMENT ON COLUMN t_da_parameters.result_id IS
    'The result_id out of t_rvw_results for the parameter.';

COMMENT ON COLUMN t_da_parameters.rvw_param_id IS
    'The rvw_param_id out of t_rvw_parameters for the parameter. To get the link between data assessment and the original record of the data review.';

ALTER TABLE t_da_parameters 
    ADD CONSTRAINT t_da_parameters_pk PRIMARY KEY ( da_param_id );

-- Table : t_da_qnaire_resp
CREATE TABLE t_da_qnaire_resp (
    da_qnaire_resp_id         NUMBER NOT NULL,
    da_wp_resp_id             NUMBER NOT NULL,
    qnaire_resp_id            NUMBER NOT NULL,
    qnaire_resp_name          VARCHAR2(200 CHAR) NOT NULL,
    qnaire_resp_vers_id       NUMBER,
    qnaire_resp_version_name  VARCHAR2(200 CHAR),
    ready_for_production_flag VARCHAR2(1 CHAR),
    baseline_existing_flag    VARCHAR2(1 CHAR) NOT NULL,
    num_positive_answers      NUMBER NOT NULL,
    num_neutral_answers       NUMBER NOT NULL,
    num_negative_answers      NUMBER NOT NULL,
    reviewed_date             TIMESTAMP,
    reviewed_user             VARCHAR2(30 CHAR),
    created_date              TIMESTAMP NOT NULL,
    created_user              VARCHAR2(30 CHAR) NOT NULL,
    modified_date             TIMESTAMP,
    modified_user             VARCHAR2(30 CHAR),
    version                   NUMBER NOT NULL
);

ALTER TABLE t_da_qnaire_resp 
    ADD CONSTRAINT t_da_qnaire_resp_pk PRIMARY KEY ( da_qnaire_resp_id );

-- Table : t_da_wp_resp
CREATE TABLE t_da_wp_resp (
    da_wp_resp_id                NUMBER NOT NULL,
    data_assessment_id           NUMBER NOT NULL,
    a2l_wp_id                    NUMBER NOT NULL,
    a2l_wp_name                  VARCHAR2(255 CHAR) NOT NULL,
    a2l_resp_id                  NUMBER NOT NULL,
    a2l_resp_alias_name          VARCHAR2(255 CHAR) NOT NULL,
    a2l_resp_name                VARCHAR2(255 CHAR) NOT NULL,
    a2l_resp_type                VARCHAR2(1 CHAR) NOT NULL,
    wp_ready_for_production_flag VARCHAR2(1 CHAR) NOT NULL,
    wp_finished_flag             VARCHAR2(1 CHAR) NOT NULL,
    qnaires_answered_flag        VARCHAR2(1 CHAR) NOT NULL,
    parameter_reviewed_flag      VARCHAR2(1 CHAR) NOT NULL,
    hex_rvw_equal_flag           VARCHAR2(1 CHAR) NOT NULL,
    created_date                 TIMESTAMP NOT NULL,
    created_user                 VARCHAR2(200 CHAR) NOT NULL,
    modified_date                TIMESTAMP,
    modified_user                VARCHAR2(30 CHAR),
    version                      NUMBER NOT NULL
);

ALTER TABLE t_da_wp_resp 
    ADD CONSTRAINT t_da_wp_resp_pk PRIMARY KEY ( da_wp_resp_id );

ALTER TABLE t_da_files
    ADD CONSTRAINT t_da_files_fk FOREIGN KEY ( data_assessment_id )
        REFERENCES t_da_data_assessment ( data_assessment_id );

ALTER TABLE t_da_parameters
    ADD CONSTRAINT t_da_parameters_fk FOREIGN KEY ( da_wp_resp_id )
        REFERENCES t_da_wp_resp ( da_wp_resp_id );

ALTER TABLE t_da_qnaire_resp
    ADD CONSTRAINT t_da_qnaire_resp_fk FOREIGN KEY ( da_wp_resp_id )
        REFERENCES t_da_wp_resp ( da_wp_resp_id );

ALTER TABLE t_da_wp_resp
    ADD CONSTRAINT t_da_wp_resp_fk FOREIGN KEY ( data_assessment_id )
        REFERENCES t_da_data_assessment ( data_assessment_id );

spool off
