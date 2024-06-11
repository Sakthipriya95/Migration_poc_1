spool c:\temp\5.pidcVersioning.log

--TABV_PROJECT_SUB_VARIANTS                    
UPDATE TABV_PROJECT_SUB_VARIANTS subvar SET(PIDC_VERS_ID) = (select pidver.PIDC_VERS_ID from T_PIDC_VERSION pidver where subvar.PROJECT_ID=pidver.PROJECT_ID and subvar.PRO_REV_ID=pidver.PRO_REV_ID);

--TABV_PROJ_SUB_VARIANTS_ATTR                    
UPDATE TABV_PROJ_SUB_VARIANTS_ATTR subvarattr SET(PIDC_VERS_ID) = (select pidver.PIDC_VERS_ID from T_PIDC_VERSION pidver where subvarattr.PROJECT_ID=pidver.PROJECT_ID and subvarattr.PRO_REV_ID=pidver.PRO_REV_ID);

--TABV_PROJECT_VARIANTS                    
UPDATE TABV_PROJECT_VARIANTS var SET(PIDC_VERS_ID) = (select pidver.PIDC_VERS_ID from T_PIDC_VERSION pidver where var.PROJECT_ID=pidver.PROJECT_ID and var.PRO_REV_ID=pidver.PRO_REV_ID);

--TABV_VARIANTS_ATTR                    
UPDATE TABV_VARIANTS_ATTR varattr SET(PIDC_VERS_ID) = (select pidver.PIDC_VERS_ID from T_PIDC_VERSION pidver where varattr.PROJECT_ID=pidver.PROJECT_ID and varattr.PRO_REV_ID=pidver.PRO_REV_ID);

--TABV_PROJECT_ATTR                    
UPDATE TABV_PROJECT_ATTR pattr SET(PIDC_VERS_ID) = (select pidver.PIDC_VERS_ID from T_PIDC_VERSION pidver where pattr.PROJECT_ID=pidver.PROJECT_ID and pattr.PRO_REV_ID=pidver.PRO_REV_ID);
                    
commit;

spool off