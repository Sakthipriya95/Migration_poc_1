/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package stub;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lCompliParameterServiceClientTest;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lParameterPropsServiceTest;
import com.bosch.caltool.icdm.ws.rest.client.a2l.FC2WPDefinitionServiceClientTest;
import com.bosch.caltool.icdm.ws.rest.client.a2l.FC2WPMappingServiceClientTest;
import com.bosch.caltool.icdm.ws.rest.client.a2l.FC2WPVersionServiceClientTest;
import com.bosch.caltool.icdm.ws.rest.client.a2l.FunctionServiceClientTest;
import com.bosch.caltool.icdm.ws.rest.client.a2l.WorkPackageDivisionServiceClientTest;
import com.bosch.caltool.icdm.ws.rest.client.a2l.WorkPackageResourceServiceClientTest;
import com.bosch.caltool.icdm.ws.rest.client.a2l.WorkPackageServiceClientTest;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeServiceClientTest;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeSuperGroupServiceClientTest;
import com.bosch.caltool.icdm.ws.rest.client.apic.ConsolidatedRiskCatClientTest;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcRmDefClientTest;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcRmProjCharClientTest;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcRmProjCharExtClientTest;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcSearchRestTest;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcServiceClientTest;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionServiceClientTest;
import com.bosch.caltool.icdm.ws.rest.client.apic.RmMetaDataClientTest;
import com.bosch.caltool.icdm.ws.rest.client.apic.VcdmDataSetServiceClientTest;
import com.bosch.caltool.icdm.ws.rest.client.apic.VcdmDataSetWPStatsServiceClientTest;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CdrReportServiceClientTest;
import com.bosch.caltool.icdm.ws.rest.client.cdr.PreCalibrationDataServiceClientTest;
import com.bosch.caltool.icdm.ws.rest.client.general.DataModelRefreshServiceTest;
import com.bosch.caltool.icdm.ws.rest.client.general.NodeAccessServiceClientTest;

/**
 * @author ELM1COB
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({

    A2lCompliParameterServiceClientTest.class,
    A2lParameterPropsServiceTest.class,
    FC2WPDefinitionServiceClientTest.class,
    FC2WPMappingServiceClientTest.class,
    FC2WPVersionServiceClientTest.class,
    FunctionServiceClientTest.class,
    WorkPackageDivisionServiceClientTest.class,
    WorkPackageResourceServiceClientTest.class,
    WorkPackageServiceClientTest.class,
    AttributeSuperGroupServiceClientTest.class,
    AttributeServiceClientTest.class,
    ConsolidatedRiskCatClientTest.class,
    PidcRmDefClientTest.class,
    PidcRmProjCharClientTest.class,
    PidcRmProjCharExtClientTest.class,
    PidcSearchRestTest.class,
    PidcServiceClientTest.class,
    PidcVersionServiceClientTest.class,
    RmMetaDataClientTest.class,
    VcdmDataSetServiceClientTest.class,
    VcdmDataSetWPStatsServiceClientTest.class,
    CdrReportServiceClientTest.class,
    PreCalibrationDataServiceClientTest.class,
    DataModelRefreshServiceTest.class,
    NodeAccessServiceClientTest.class })
public class AllTests {

}
