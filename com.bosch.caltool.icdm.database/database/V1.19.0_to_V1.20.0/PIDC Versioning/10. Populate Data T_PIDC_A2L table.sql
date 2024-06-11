spool c:\temp\10.pidcVersioning.log
-- The below procedure iterates through TABV_PROJECT_ATTR and TABV_VARIANTS_ATTR
-- 
CREATE OR REPLACE PROCEDURE populate_pidc_a2l IS
    chk NUMBER := 0;
    attrID NUMBER :=0;
  BEGIN
     select attr.ATTR_ID into attrID from TABV_ATTRIBUTES attr where attr.ATTR_LEVEL = -30;
     FOR rec IN (select attrval.TEXTVALUE_ENG,pidcver.* from TABV_ATTR_VALUES attrval inner join TABV_PROJECT_ATTR pattr on attrval.VALUE_ID=pattr.VALUE_ID inner join T_PIDC_VERSION pidcver on pattr.PIDC_VERS_ID = pidcver.PIDC_VERS_ID inner join TABV_PROJECTIDCARD pidc on pidcver.PROJECT_ID=pidc.PROJECT_ID where
              pidcver.PRO_REV_ID = pidc.PRO_REV_ID and 
              pattr.ATTR_ID = attrID and 
              pattr.IS_VARIANT='N' and 
              pattr.VALUE_ID is not null)
     LOOP
        --DBMS_OUTPUT.put_line ('PIDC with ' || rec.PROJECT_ID || ';' || rec.PRO_REV_ID || 'has Pver ' || rec.TEXTVALUE_ENG);
        FOR inrec IN (select afile.* from TA2L_FILEINFO afile where afile.SDOM_PVER_NAME = rec.TEXTVALUE_ENG and afile.VCDM_A2LFILE_ID is not null and afile.SDOM_PVER_VARIANT is not null and afile.SDOM_PVER_VERSID is not null)
          LOOP
            BEGIN 
              select pa2l.PIDC_A2L_ID into chk from T_PIDC_A2L pa2l where pa2l.A2L_FILE_ID = inrec.ID and pa2l.PROJECT_ID=rec.PROJECT_ID;-- and pa2l.PIDC_VERS_ID=rec.PIDC_VERS_ID;
            EXCEPTION 
              WHEN no_data_found
            THEN
              chk:=0;
            END;
            IF chk=0 THEN 
              --DBMS_OUTPUT.put_line ('---A2l File found is ' || inrec.FILENAME);
              insert into T_PIDC_A2L(PIDC_A2L_ID,PROJECT_ID,PIDC_VERS_ID,A2L_FILE_ID,SDOM_PVER_NAME,CREATED_DATE,CREATED_USER,VERSION) VALUES(SEQV_ATTRIBUTES.nextval,rec.PROJECT_ID,rec.PIDC_VERS_ID,inrec.ID,inrec.SDOM_PVER_NAME,SYSDATE,'HFZ2SI',1);
            END IF;
            chk:=0;
          END LOOP;
     END LOOP;
     
     FOR rec1 IN (select attrval.TEXTVALUE_ENG,pidcver.* from TABV_ATTR_VALUES attrval inner join TABV_VARIANTS_ATTR vattr on attrval.VALUE_ID=vattr.VALUE_ID inner join T_PIDC_VERSION pidcver on vattr.PIDC_VERS_ID = pidcver.PIDC_VERS_ID inner join TABV_PROJECTIDCARD pidc on pidcver.PROJECT_ID=pidc.PROJECT_ID  where
              pidcver.PRO_REV_ID = pidc.PRO_REV_ID and 
              vattr.ATTR_ID = attrID and 
              vattr.IS_SUBVARIANT='N' and 
              vattr.VALUE_ID is not null)
     LOOP
        --DBMS_OUTPUT.put_line ('PIDC with ' || rec1.PROJECT_ID || ';' || rec1.PRO_REV_ID || 'has Pver ' || rec1.TEXTVALUE_ENG);
        FOR inrec1 IN (select afile.* from TA2L_FILEINFO afile where afile.SDOM_PVER_NAME = rec1.TEXTVALUE_ENG and afile.VCDM_A2LFILE_ID is not null and afile.SDOM_PVER_VARIANT is not null and afile.SDOM_PVER_VERSID is not null)
          LOOP
            BEGIN
            select pva2l.PIDC_A2L_ID into chk from T_PIDC_A2L pva2l where pva2l.A2L_FILE_ID = inrec1.ID and pva2l.PROJECT_ID=rec1.PROJECT_ID;-- and pva2l.PIDC_VERS_ID=rec1.PIDC_VERS_ID;
           EXCEPTION 
              WHEN no_data_found
            THEN
              chk:=0;
            END;
            IF chk=0 THEN 
              --DBMS_OUTPUT.put_line ('---A2l File found is ' || inrec1.FILENAME);
              insert into T_PIDC_A2L(PIDC_A2L_ID,PROJECT_ID,PIDC_VERS_ID,A2L_FILE_ID,SDOM_PVER_NAME,CREATED_DATE,CREATED_USER,VERSION) VALUES(SEQV_ATTRIBUTES.nextval,rec1.PROJECT_ID,rec1.PIDC_VERS_ID,inrec1.ID,inrec1.SDOM_PVER_NAME,SYSDATE,'HFZ2SI',1);
            END IF;
            chk:=0;
          END LOOP;
     END LOOP;

     DBMS_OUTPUT.put_line ('Procedure populate_pidc_a2l is done');
  END;
/     

exec populate_pidc_a2l();

--This procedure needs to be dropped as it is used only for populating data in T_PIDC_A2L table
drop procedure populate_pidc_a2l;

spool off;
