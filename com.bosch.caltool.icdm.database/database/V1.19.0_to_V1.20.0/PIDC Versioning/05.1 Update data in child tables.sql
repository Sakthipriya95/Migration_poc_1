spool c:\temp\5.1pidcVersioning.log
--TABV_PROJECTIDCARD Check if this is required  Note: null entry found for PIDCID - 769052017 in PIDC_history table and hence in T_PIDC_VERSION --table
--TABV_PROJECTIDCARD Check if this is required  Note: null entry found for PIDCID - 772790017 in PIDC_history table and hence in T_PIDC_VERSION --table
UPDATE TABV_PROJECTIDCARD pidc SET(PIDC_VERS_ID) = (select pidver.PIDC_VERS_ID from T_PIDC_VERSION pidver where pidc.PROJECT_ID=pidver.PROJECT_ID and pidc.PRO_REV_ID=pidver.PRO_REV_ID);

--TABV_PIDC_DET_STRUCTURE IMPORTANT: To be done after update of TABV_PROJECTIDCARD
UPDATE TABV_PIDC_DET_STRUCTURE pdet SET(PIDC_VERS_ID) = (select pidc.PIDC_VERS_ID from TABV_PROJECTIDCARD pidc where pdet.PIDC_ID=pidc.PROJECT_ID);

commit;

spool off