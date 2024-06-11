spool c:\temp\4.pidcVersioning.log

--pidc_id && pro_rev_id
----------------------------
INSERT INTO T_PIDC_VERSION( PIDC_VERS_ID,PROJECT_ID, PRO_REV_ID,MODIFIED_DATE,MODIFIED_USER) 
             select SEQV_ATTRIBUTES.nextval,hist.PROJECT_ID,hist.PRO_REV_ID,hist.CREATED_DATE,hist.CREATED_USER 
                    from tabv_pid_history hist, (select PROJECT_ID, PRO_REV_ID, max(pid_status_id) stat from TABV_PID_HISTORY group by PROJECT_ID, PRO_REV_ID) latesthist 
                    where hist.project_id = latesthist.project_id and hist.pro_rev_id= latesthist.pro_rev_id and hist.PID_STATUS_ID=latesthist.stat;


--created_user && created_date
----------------------------
UPDATE T_PIDC_VERSION pidver SET ( CREATED_USER,CREATED_DATE) =
             (select hist.CREATED_USER,hist.CREATED_DATE
                    from tabv_pid_history hist, (select PROJECT_ID, PRO_REV_ID, min(pid_status_id) stat from TABV_PID_HISTORY group by PROJECT_ID, PRO_REV_ID) latesthist 
                    where hist.project_id = latesthist.project_id and hist.pro_rev_id= latesthist.pro_rev_id and hist.PID_STATUS_ID=latesthist.stat and pidver.PROJECT_ID = hist.PROJECT_ID and pidver.PRO_REV_ID=hist.PRO_REV_ID);  
                    
                    

--VERS_NAME && VERS_DESC_ENG
---------------------------
UPDATE T_PIDC_VERSION SET (VERS_NAME,VERS_DESC_ENG)=(select concat('Version ',to_char(PRO_REV_ID)),concat('Version ',to_char(PRO_REV_ID)) from dual);       


--PARENT_PIDC_VERS_ID
-----------------------
UPDATE T_PIDC_VERSION  this_ver set this_ver.PARENT_PIDC_VERS_ID = (select par_ver.PIDC_VERS_ID from T_PIDC_VERSION  par_ver where par_ver.pro_rev_id = this_ver.pro_rev_id - 1 and this_ver.PROJECT_ID = par_ver.PROJECT_ID and this_ver.pro_rev_id > 1);

--PID_STATUS
----------
UPDATE T_PIDC_VERSION pidver SET (PID_STATUS) =
             (select replace(upper(substr(sta.STATUS_TYPE,1,1)),'R','I')
                    from (select PROJECT_ID, PRO_REV_ID, max(pid_status_id) stat from TABV_PID_HISTORY group by PROJECT_ID, PRO_REV_ID) latesthist,tabv_pid_history hist inner join TABV_PID_STATUS sta on hist.PID_STATUS_ID = sta.PID_STATUS_ID 
                    where hist.project_id = latesthist.project_id and hist.pro_rev_id= latesthist.pro_rev_id and hist.PID_STATUS_ID=latesthist.stat and pidver.PROJECT_ID = hist.PROJECT_ID and pidver.PRO_REV_ID=hist.PRO_REV_ID);   

--VERSION
------------
UPDATE T_PIDC_VERSION SET VERSION = 1;  

commit;

spool off