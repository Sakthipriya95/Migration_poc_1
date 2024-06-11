set list=com.bosch.calcomp.externallink	com.bosch.caltool.apic.vcdminterface	com.bosch.caltool.apic.vcdminterface.test  com.bosch.caltool.apic.ws.client   com.bosch.caltool.apic.ws.client.test   com.bosch.caltool.apic.ws.common	com.bosch.caltool.comparator	com.bosch.caltool.datamodel.core	com.bosch.caltool.icdm.client.bo  com.bosch.caltool.icdm.client.bo.test    com.bosch.caltool.icdm.cns.client	com.bosch.caltool.icdm.cns.client.test	com.bosch.caltool.icdm.cns.common	com.bosch.caltool.icdm.common.bo	com.bosch.caltool.icdm.common.util	com.bosch.caltool.icdm.logger	com.bosch.caltool.icdm.model	com.bosch.caltool.icdm.ws.rest.client	com.bosch.caltool.icdm.ws.rest.client.test	com.bosch.rcputils
set jenkinswkp=%1
(for %%A in (%list%) do ( 
    echo %%A
    xcopy /s /i /Y "%CD%\%%A" "%jenkinswkp%\%%A\"
)) 