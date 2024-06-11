package com.bosch.caltool.apic.ws.client.serviceclient;

import java.io.File;

import org.apache.axis2.AxisFault;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.calcomp.caldataanalyzer.vo.LabelInfoVO;
import com.bosch.calcomp.caldataanalyzer.vo.LabelValueInfoVO;
import com.bosch.caltool.apic.ws.client.APICStub.Attribute;
import com.bosch.caltool.apic.ws.client.APICStub.AttributeValue;
import com.bosch.caltool.apic.ws.client.APICStub.AttributeWithValueType;
import com.bosch.caltool.apic.ws.client.APICStub.GetPidcDiffsResponseType;
import com.bosch.caltool.apic.ws.client.APICStub.GroupType;
import com.bosch.caltool.apic.ws.client.APICStub.LevelAttrInfo;
import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardChangedAttributeType;
import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardInfoType;
import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardType;
import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardVariantType;
import com.bosch.caltool.apic.ws.client.APICStub.SuperGroupType;
import com.bosch.caltool.apic.ws.client.APICStub.UseCaseType;
import com.bosch.caltool.apic.ws.client.APICStub.ValueList;


public class TestClient {

  private static final String LOG_FILE = "c:\\temp\\APIC_WS_TestClient.LOG";

  private static final String LOGGER_PATTERN = "%d [%-5t] %-5p %c:%L - %m%n";

  private ILoggerAdapter logger = null;

  public void start() throws Exception {

    File logFile = new File(LOG_FILE);
    if (logFile.exists()) {
      logFile.delete();
    }

    this.logger = new Log4JLoggerAdapterImpl(LOG_FILE, LOGGER_PATTERN);
    this.logger.setLogLevel(ILoggerAdapter.LEVEL_INFO);

    APICWebServiceClient apicWsClient = new APICWebServiceClient(APICWebServiceClient.APICWsServer.LOCAL_SERVER);

    int i;
    int i2;

    this.logger.info("========= Attribute Groups ===================");

    i = 0;

    SuperGroupType[] superGroups = apicWsClient.getAttrGroups();

    for (SuperGroupType superGroup : superGroups) {
      this.logger.info(++i + " : " + superGroup.getNameE() + " : DescrE = " + superGroup.getDescrE() + " : ID = " +
          superGroup.getId() + " : version = " + superGroup.getChangeNumber());

      i2 = 0;

      if (superGroup.getGroups() != null) {
        for (GroupType group : superGroup.getGroups()) {
          this.logger.info("   " + i + "." + i2++ + " : " + group.getNameE() + " : DescrE = " + group.getDescrE() +
              " : ID = " + group.getId() + " : version = " + group.getChangeNumber());

        }
      }

    }

    this.logger.info("========= Attributes =========================");

    i = 0;

    Attribute[] attributes = apicWsClient.getAllAttributes();

    for (Attribute attribute : attributes) {
      this.logger.info(++i + " : " + attribute.getNameE() + " : TypeID = " + attribute.getTypeID());
    }

    this.logger.info("======= Attributevalues ======================");

    long[] attrIDs;

    attrIDs = new long[2];
    attrIDs[0] = 36;
    attrIDs[1] = 84;

    ValueList[] values = apicWsClient.getAttrValues(attrIDs);

    for (ValueList value2 : values) {

      i = 0;

      this.logger.info("Attribute: " + value2.getAttribute().getNameE());

      if (value2.isValuesSpecified() && (value2.getValues().length > 0)) {

        for (AttributeValue value : value2.getValues()) {

          if (value.getIsDeleted()) {
            this.logger.info("  " + ++i + " : DELETED! : " + value.getValueE());
          }
          else {
            this.logger.info("  " + ++i + " : " + value.getValueE());
          }

        }

      }
      else {
        this.logger.info("  " + " no values found ");
      }

    }

    this.logger.info("========= UseCases =========================");

    i = 0;

    UseCaseType[] useCases = apicWsClient.getUseCases();

    for (UseCaseType useCase : useCases) {

      this.logger.info(
          ++i + " : " + useCase.getNameE() + " : " + useCase.getNameG() + " : Version = " + useCase.getChangeNumber());

      for (int k = 0; k < useCase.getUseCaseItems().length; k++) {
        this.logger.info(
            "  section: " + useCase.getUseCaseItems()[k].getNameE() + " : Version = " + useCase.getChangeNumber());

        long[] itemAttributes = useCase.getUseCaseItems()[k].getMappedAttributeIDs();

        if (itemAttributes != null) {

          this.logger.info("    attributes: " + itemAttributes.length);

          // for (int k2 = 0; k2 < itemAttributes.length; k2++) {
          //
          // this.logger.info(" attribute: " + itemAttributes[k2]);
          //
          // }

        }
        else {

          this.logger.info("    no attributes! ");
        }

      }

    }

    this.logger.info("=========== PIDCs ===========================");

    i = 0;

    ProjectIdCardInfoType[] projectIDCards = apicWsClient.getAllPidc();

    for (ProjectIdCardInfoType projectIDCard : projectIDCards) {
      this.logger.info(++i + " : " + projectIDCard.getName() + " (ID: " + projectIDCard.getId() + ")");
    }

    this.logger.info("======= PIDC details ========================");

    i = 0;

    // Long pidcID = new Long(699017);
    Long pidcID = new Long(759996267);
    // Long pidcID = new Long(699017);


    ProjectIdCardType projectCard = apicWsClient.getPidcDetails(pidcID);

    this.logger.info(projectCard.getProjectIdCardDetails().getName());

    getPidcLevelInfo(projectCard.getProjectIdCardDetails());

    AttributeWithValueType[] pidcAttrs = projectCard.getAttributes();

    printAttributes(pidcAttrs, "");

    this.logger.info("=========== PIDC Variants ===================");

    this.logger.info(projectCard.getProjectIdCardDetails().getName() + " (Rev.:" +
        projectCard.getProjectIdCardDetails().getChangeNumber() + ")");

    i = 0;

    if (projectCard.isVariantsSpecified()) {
      for (int k = 0; k < projectCard.getVariants().length; k++) {

        ProjectIdCardVariantType pidcVariant = projectCard.getVariants()[k];

        i++;

        this.logger.info(i + " : " + pidcVariant.getPIdCVariant().getName() + " (ATTR:" +
            pidcVariant.getAttributes().length + ")" + " (Rev.:" + pidcVariant.getPIdCVariant().getVersionNumber() +
            ")" + " (ID:" + pidcVariant.getPIdCVariant().getId() + " : Version = " +
            pidcVariant.getPIdCVariant().getChangeNumber() + ")");

        printAttributes(pidcVariant.getAttributes(), "  " + i + ".");

        if (pidcVariant.isSubVariantsSpecified()) {
          this.logger.info("  Sub-Variants");

          for (int l = 0; l < pidcVariant.getSubVariants().length; l++) {

            ProjectIdCardVariantType pidcSubVariant = pidcVariant.getSubVariants()[l];

            this.logger.info("   " + (l + 1) + " : " + pidcSubVariant.getPIdCVariant().getName() + " (ATTR:" +
                getAttributesLength(pidcSubVariant.getAttributes()) + ")" + " (Rev.:" +
                pidcSubVariant.getPIdCVariant().getVersionNumber() + ")" + " (ID:" +
                pidcSubVariant.getPIdCVariant().getId() + " : Version = " +
                pidcSubVariant.getPIdCVariant().getChangeNumber() + ")");

            printAttributes(pidcSubVariant.getAttributes(), "    " + (l + 1) + ".");

          }
        }
      }

    }
    else {
      this.logger.info("no Variants defined!");
    }


    this.logger.info("=========== PIDC Differences ===================");

    GetPidcDiffsResponseType pidcDiffs = apicWsClient.getPidcDiffs(pidcID, 140L, -1L).getGetPidcDiffsResponse();

    this.logger.info(pidcDiffs.getChangedAttributes().length + " attributes changed");

    this.logger.info("Differences for PIDC " + projectCard.getProjectIdCardDetails().getName());

    this.logger.info("  old changeNumber: " + pidcDiffs.getOldChangeNumber());
    this.logger.info("  new changeNumber: " + pidcDiffs.getNewChangeNumber());

    this.logger.info("  old version: " + pidcDiffs.getOldPidcVersionNumber());
    this.logger.info("  new version: " + pidcDiffs.getNewPidcVersionNumber());

    this.logger.info("  old status: " + pidcDiffs.getOldPidcStatus());
    this.logger.info("  new status: " + pidcDiffs.getNewPidcStatus());

    this.logger.info("  last modification date: " + pidcDiffs.getModifyDate().getTime());
    this.logger.info("  last modification user: " + pidcDiffs.getModifyUser());

    for (ProjectIdCardChangedAttributeType pidc : pidcDiffs.getChangedAttributes()) {
      System.out.println("Attr-ID" + pidc.getAttrID());
      System.out.println("Change Number: " + pidc.getChangeNumber());
      System.out.println("Old/New Value: " + pidc.getOldValueID() + "/" + pidc.getNewValueID());
    }

    this.logger.info("=========== Parameter Statistics ===================");

    // getParameterStatistics(apicWsClient, "ASMod_dmCOBasLM_MAP");
    //
    // getParameterStatistics(apicWsClient, "DATA_GEM_ExLp_tPipe0Ts.GEM_lPipe0TsExLp_C_VW[1]");
    //
    // getParameterStatistics(apicWsClient, "AC_dmClgACOff_CUR");

    this.logger.info("=============================================");
  }

  /**
   * @param array
   * @return
   */
  private int getAttributesLength(final AttributeWithValueType[] array) {
    if (array == null) {
      return 0;
    }
    else {
      return array.length;
    }
  }

  /**
   * @param pidcAttrs
   * @param preFix
   * @return
   */
  private int printAttributes(final AttributeWithValueType[] pidcAttrs, final String preFix) {

    int i = 0;

    if (pidcAttrs == null) {
      return i;
    }

    for (AttributeWithValueType pidcAttr : pidcAttrs) {
      i++;

      this.logger.info(preFix + i + " : " + pidcAttr.getAttribute().getNameE() + " (used = " + pidcAttr.getUsed() +
          ")" + " (value = " + getValue(pidcAttr) + ")" + " : Version = " + pidcAttr.getChangeNumber());

      if (pidcAttr.isPartNumberSpecified()) {
        this.logger.info("Partnumber : " + pidcAttr.getPartNumber());
      }
      if (pidcAttr.isSpecLinkSpecified()) {
        this.logger.info("Spec Link  : " + pidcAttr.getSpecLink());
      }
      if (pidcAttr.isDescriptionSpecified()) {
        this.logger.info("Description: " + pidcAttr.getDescription());
      }

    }
    return i;
  }

  /**
   * @param projectIDCard
   */
  private void getPidcLevelInfo(final ProjectIdCardInfoType projectIDCard) {
    for (int j = 0; j < projectIDCard.getLevelAttrInfoList().length; j++) {
      LevelAttrInfo levelAttrInfo = projectIDCard.getLevelAttrInfoList()[j];

      this.logger.info("    " + levelAttrInfo.getLevelNo() + " :: " + levelAttrInfo.getLevelName());


    }
  }


  /**
   * @param apicWsClient
   * @throws Exception
   */
  private void getParameterStatistics(final APICWebServiceClient apicWsClient, final String parameterName)
      throws Exception {

    this.logger.info("============================ Get ParameterStatistics ============================");

    LabelInfoVO labelInfoVO = null;

    try {
      labelInfoVO = apicWsClient.getParameterStatistics(parameterName, null /*
                                                                             * ICDM-218
                                                                             */);
      this.logger.info(labelInfoVO.getLabelName());
      this.logger.info("Label: " + labelInfoVO.getLabelName());
      this.logger.info("  number of values  : " + labelInfoVO.getNumberOfValues());
      this.logger.info("  number of datasets: " + labelInfoVO.getSumDataSets());
      this.logger.info("  min. value        : " + labelInfoVO.getMinValueSimpleDisplay());
      this.logger.info("  max. value        : " + labelInfoVO.getMaxValueSimpleDisplay());
      this.logger.info("  avg. value        : " + labelInfoVO.getAvgValueSimpleDisplay());
      this.logger.info("  median value      : " + labelInfoVO.getMedianValueSimpleDisplay());
      this.logger.info("  lower quartile    : " + labelInfoVO.getLowerQuartileValueSimpleDisplay());
      this.logger.info("  upper quartile    : " + labelInfoVO.getUpperQuartileValueSimpleDisplay());
      this.logger.info("  peak value        : " + labelInfoVO.getPeakValueSimpleDisplay());
      this.logger.info("  peak value pct.   : " + labelInfoVO.getPeakValuePercentage() + "%");

      this.logger.info("--------------------------------------------------");

      for (LabelValueInfoVO labelValueInfo : labelInfoVO.getValuesMap().values()) {
        this.logger.info("  value: " + labelValueInfo.getCalDataPhy().getSimpleDisplayValue() + " unit: " +
            labelValueInfo.getCalDataPhy().getUnit() + " datasets: " + labelValueInfo.getFileIDList().size());

      }

    }
    catch (AxisFault e) {
      this.logger.warn(e.getLocalizedMessage());
    }


    this.logger.info("========================== End get ParameterStatistics ==========================");
  }


  private String getValue(final AttributeWithValueType attrWithValue) {

    if (attrWithValue.getIsVariant()) {
      return "<VARIANT>";
    }

    if (attrWithValue.isValueSpecified()) {
      return attrWithValue.getValue().getValueE();
    }

    return "";

  }


  /**
   * @param args
   */
  public static void main(final String[] args) {
    try {
      new TestClient().start();
    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

}
