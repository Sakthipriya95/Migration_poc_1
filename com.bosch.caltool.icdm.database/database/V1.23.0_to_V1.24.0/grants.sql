spool c:\temp\grants.log

--------------------------------------------------------
--  2016-04-11
--------------------------------------------------------

-- ICDM-1950  provide grant in ICDM JPA

GRANT select, insert, update, delete ON T_QUESTIONNAIRE TO DGS_ICDM_JPA;
GRANT select, insert, update, delete ON T_QUESTIONNAIRE_VERSION TO DGS_ICDM_JPA;
GRANT select, insert, update, delete ON T_QUESTION TO DGS_ICDM_JPA;
GRANT select, insert, update, delete ON T_QUESTION_CONFIG TO DGS_ICDM_JPA;
GRANT select, insert, update, delete ON T_QUESTION_DEPEN_ATTRIBUTES TO DGS_ICDM_JPA;
GRANT select, insert, update, delete ON T_QUESTION_DEPEN_ATTR_VALUES TO DGS_ICDM_JPA;

--------------------------------------------------------
--  2016-05-09
--------------------------------------------------------

-- ICDM-1979  provide grant in ICDM JPA
GRANT select, insert, update, delete ON T_RVW_QUESTIONNAIRE TO DGS_ICDM_JPA;
GRANT select, insert, update, delete ON T_RVW_QNAIRE_ANSWER TO DGS_ICDM_JPA;
--------------------------------------------------------
--  2016-05-30
--------------------------------------------------------

-- ICDM-2084  provide grant in ICDM JPA
  
 GRANT select, insert, update, delete ON T_RVW_VARIANTS TO DGS_ICDM_JPA;

spool off