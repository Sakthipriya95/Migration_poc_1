/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.qnairemigration;

import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import org.junit.BeforeClass;
import org.junit.Test;

import com.bosch.caltool.dmframework.bo.AbstractCommand.COMMAND_MODE;
import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeModel;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.QuestionLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireAnswerCommand;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireAnswerOplCommand;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireAnswerOplLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireRespVersionLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireResponseLoader;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.bo.general.WsSystemLoader;
import com.bosch.caltool.icdm.bo.test.AbstractIcdmBOTest;
import com.bosch.caltool.icdm.cns.client.CnsClientConfiguration;
import com.bosch.caltool.icdm.cns.client.CnsServiceClientException;
import com.bosch.caltool.icdm.common.bo.qnaire.QnaireRespDepnEvaluator;
import com.bosch.caltool.icdm.common.bo.qnaire.QnaireRespVersDataResolver;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireRespVariant;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireRespVersion;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireResponse;
import com.bosch.caltool.icdm.logger.CnsClientLogger;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswerOpl;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponseModel;
import com.bosch.caltool.icdm.model.general.CommonParamKey;

/**
 * @author say8cob
 */
public class QnaireMigration extends AbstractIcdmBOTest {

  private static final String MIGRATED_VERSION_NAME = "Migrated version with both general and exisiting question";

  // Run commands only if flag is TRUE
  private static final boolean ENABLE_SAVE = true;

  // Key - Pidc Vers Id Value - PidcVersionAttributeModel
  private final Map<Long, PidcVersionAttributeModel> prjAttrModelMap = new HashMap<>();

  // Errors present
  // Key - resp vers ID, value - reason
  private final Map<Long, String> errRespVersMap = new HashMap<>();

  // Nothing to migrate
  private final Set<Long> noMigResqVersSet = new HashSet<>();

  private int totalRespVersCount;

  /**
   * Main migration method
   */
  @Test
  public void qnaireMigration() {
    try {
      doMigration();
    }
    catch (Exception e) {
      LOG.error("Error occured. " + e.getMessage(), e);
      fail("error occured - " + e.getMessage());
    }
  }

  /**
   * Do migration
   *
   * @throws IcdmException
   */
  private void doMigration() throws IcdmException {

//    // List (A) : Special Mig-Resp versions of a given PIDC Version ID
//    long ipPidcVersId = 773510965;
//    String respToMigQuery =
//        "SELECT distinct rspVers.qnaire_resp_vers_id                                                                " +
//            "FROM t_rvw_qnaire_resp_versions rspVers                                                                " +
//            "   , t_rvw_qnaire_response resp                                                                        " +
//            "WHERE rspVers.mig_state != 'C'                                                                         " +
//            "  and rspVers.name = '" + MIGRATED_VERSION_NAME + "'                                                   " +
//            "  and rspVers.qnaire_resp_id = resp.qnaire_resp_id                                                     " +
//            "  and resp.pidc_vers_id = " + ipPidcVersId;

    // List (B) : All Special Mig-Resp Versions in DB
    String respToMigQuery =
        "SELECT distinct qnaire_resp_vers_id                                                                        " +
            "FROM t_rvw_qnaire_resp_versions                                                                        " +
            "WHERE mig_state != 'C'                                                                                 " +
            "  and name = '" + MIGRATED_VERSION_NAME + "'                       ";


    // Run Query
    Query respVersQry = getServiceData().getEntMgr().createNativeQuery(respToMigQuery);
    List respVersIdList = respVersQry.getResultList();
    Set<Long> respVersIdSet = new HashSet<>();
    respVersIdList.forEach(o -> respVersIdSet.add(((BigDecimal) o).longValue()));


//    // List (C) : Defined qnaire resp version IDs
//    Set<Long> respVersIdSet = new HashSet<>(Arrays.asList(14747073777L));


    this.totalRespVersCount = respVersIdSet.size();
    LOG.info("Total Questionnaire Response Versions to migrate : {}", this.totalRespVersCount);

    RvwQnaireResponseLoader qnaireRespLdr = new RvwQnaireResponseLoader(getServiceData());
    RvwQnaireRespVersionLoader qnaireRespVersLdr = new RvwQnaireRespVersionLoader(getServiceData());
    int completedCount = 0;

    for (Long respVersId : respVersIdSet) {
      long startTime = System.currentTimeMillis();
      TRvwQnaireRespVersion dbQnaireRespVers = qnaireRespVersLdr.getEntityObject(respVersId);
      TRvwQnaireResponse rvwQnaireResp =
          qnaireRespLdr.getEntityObject(dbQnaireRespVers.getTRvwQnaireResponse().getQnaireRespId());
      List<TRvwQnaireRespVariant> tRvwQnaireRespVariants = new ArrayList<>(rvwQnaireResp.getTRvwQnaireRespVariants());
      long pidcVersId = tRvwQnaireRespVariants.get(0).getTPidcVersion().getPidcVersId();

      LOG.info("Qnaire Resp Vers ID : {}, Resp ID : {}, PIDC Version ID : {}", respVersId,
          rvwQnaireResp.getQnaireRespId(), pidcVersId);

      deleteNonReleventRvwAnswerForSpecialVers(pidcVersId, respVersId);
      completedCount++;

      long timeTaken = System.currentTimeMillis() - startTime;
      LOG.info("Completed count of Questionnaire Response Versions {}/{}. Time taken = {} ms", completedCount,
          this.totalRespVersCount, timeTaken);
    }

    summarize();
  }


  /**
   * Perform stuff on resp version
   *
   * @param pidcVersId PIDC version ID
   * @param qnaireRespVersId resp version ID
   * @throws IcdmException
   */

  private void deleteNonReleventRvwAnswerForSpecialVers(final Long pidcVersId, final Long qnaireRespVersId) {

    boolean hasErr = false;
    try {
      List<AbstractSimpleCommand> cmdList = createDelCommands(pidcVersId, qnaireRespVersId);
      if (cmdList.isEmpty()) {
        this.noMigResqVersSet.add(qnaireRespVersId);
      }
      LOG.info("    Total answers to be removed from Qnaire Resp Vers '{}' is : {}", qnaireRespVersId, cmdList.size());

      // Add command to update flag to mig COMPLETED
      cmdList.add(new RvwQnaireRespVrsionFlagMigUpdateCommand(getServiceData(), qnaireRespVersId, "C"));
      executeCommands(cmdList);
    }
    catch (Exception e) {
      hasErr = true;
      LOG.error("    Error while processing resp version ID : " + qnaireRespVersId, e);
      String err = e.getClass().getSimpleName() + ": " + e.getMessage();
      this.errRespVersMap.put(qnaireRespVersId, err);
    }

    if (hasErr) {
      try {
        // Add command to update flag to mig with ERROR
        RvwQnaireRespVrsionFlagMigUpdateCommand statusCmd =
            new RvwQnaireRespVrsionFlagMigUpdateCommand(getServiceData(), qnaireRespVersId, "E");
        executeCommands(Arrays.asList(statusCmd));
      }
      catch (Exception e) {
        LOG.error("    Error while running commands for Qnaire resp versi ID : " + qnaireRespVersId, e);
      }
    }
  }

  /**
   * @param pidcVersId
   * @param qnaireRespVersId
   * @return
   * @throws IcdmException
   */
  private List<AbstractSimpleCommand> createDelCommands(final Long pidcVersId, final Long qnaireRespVersId)
      throws IcdmException {

    PidcVersionAttributeModel prjAttrModel = this.prjAttrModelMap.get(pidcVersId);
    if (prjAttrModel == null) {
      LOG.debug("   Create PidcVersionAttributeModel");
      prjAttrModel = new ProjectAttributeLoader(getServiceData()).createModel(pidcVersId);
      this.prjAttrModelMap.put(pidcVersId, prjAttrModel);
    }

    // RvwQnaireResponseModel
    LOG.debug("   Create RvwQnaireResponseModel");
    RvwQnaireResponseModel qnaireRespModel =
        new RvwQnaireResponseLoader(getServiceData()).getQnaireResponseModel(qnaireRespVersId);

    // QNaire Resp Depn Evaluator
    LOG.debug("   Create QnaireRespDepnEvaluator");
    QnaireRespDepnEvaluator qnDepEvaluator =
        new QnaireRespDepnEvaluator(qnaireRespModel.getPidcVersion(), qnaireRespModel.getPidcVariant());

    LOG.debug("   Create QuestionRespDataProvider");
    QuestionRespDataProvider quesRespDataProvider = new QuestionRespDataProvider(getServiceData(),
        qnaireRespModel.getRvwQnrRespVersion(), qnaireRespModel, prjAttrModel);

    QnaireRespVersDataResolver quesRespEvaluator = new QnaireRespVersDataResolver(quesRespDataProvider);

    // loading questions
    LOG.debug("   loadMainQuestions");
    quesRespEvaluator.loadMainQuestions(qnaireRespModel);

    // list to store the command
    List<AbstractSimpleCommand> cmdList = new ArrayList<>();

    long totalAnswers = qnaireRespModel.getRvwQnrAnswrMap().size();
    LOG.debug("    totalAnswers = {}", totalAnswers);

    for (RvwQnaireAnswer rvwQnaireAns : qnaireRespModel.getRvwQnrAnswrMap().values()) {
      Question question = new QuestionLoader(getServiceData()).getDataObjectByID(rvwQnaireAns.getQuestionId());

      // MAIN Check : Check if this answer is to be deleted
      boolean toDelete = !(qnDepEvaluator.isQuestionApplicable(question, quesRespDataProvider) &&
          quesRespEvaluator.isQuestionVisible(rvwQnaireAns, quesRespDataProvider.isQuesDepChkNeeded()));
      if (toDelete) {
        LOG.info("    RvwAnswer to be removed ==> Id : {}, name : {}", rvwQnaireAns.getId(), rvwQnaireAns.getName());
        // Delete the child open points
        if (!rvwQnaireAns.getOplId().isEmpty()) {
          LOG.debug("    Delete the child open points");
          for (Long rvwAnswerOplId : rvwQnaireAns.getOplId()) {
            RvwQnaireAnswerOpl rvwQnaireAnswerOpl =
                new RvwQnaireAnswerOplLoader(getServiceData()).getDataObjectByID(rvwAnswerOplId);
            RvwQnaireAnswerOplCommand answerOplCommand =
                new RvwQnaireAnswerOplCommand(getServiceData(), rvwQnaireAnswerOpl, COMMAND_MODE.DELETE);
            cmdList.add(answerOplCommand);
          }
        }
        RvwQnaireAnswerCommand ansCmd = new RvwQnaireAnswerCommand(getServiceData(), rvwQnaireAns, COMMAND_MODE.DELETE);
        cmdList.add(ansCmd);
      }
    }

    return cmdList;
  }

  /**
   *
   */
  private void summarize() {
    // Finished. Log migration summary
    int pidcVersCount = this.prjAttrModelMap.size();
    int errorMigCount = this.errRespVersMap.size();
    int okCount = this.totalRespVersCount - errorMigCount;
    int noMigCount = this.noMigResqVersSet.size();

    StringBuilder summary = new StringBuilder("Summary:");

    summary.append("\n  ").append("Total         : ").append(this.totalRespVersCount);
    summary.append("\n  ").append("OK            : ").append(okCount);
    summary.append("\n  ").append("With Errors   : ").append(errorMigCount);
    summary.append("\n  ").append("No Mig Reqd   : ").append(noMigCount);
    summary.append("\n  ").append("PIDC Versions : ").append(pidcVersCount);

    if (errorMigCount != 0) {
      summary.append("\n\nErrors:\n\t------------\t-----\n\tResp Vers ID\tError\n\t------------\t-----");
      this.errRespVersMap.entrySet().forEach(e -> summary.append("\n\t").append(e.getKey()).append('\t')
          .append(e.getValue().replaceAll("\n", ". ").replaceAll("\r", ". ")));
    }

    LOG.info("Migration completed.\n\n{}\n\n", summary);
  }

  /**
   * @param cmdList
   * @throws IcdmException
   */
  private void executeCommands(final List<AbstractSimpleCommand> cmdList) throws IcdmException {
    // Run commands only if flag is TRUE
    if (ENABLE_SAVE) {
      getServiceData().getCommandExecutor().execute(cmdList);
    }
  }

  /**
   * cns initializer
   */
  @BeforeClass
  public static void checkCnsClientInitialization() {
    try (ServiceData serviceData = new ServiceData()) {
      serviceData.setUsername("BNE4COB");
      serviceData.setLanguage("English");
      // Set timezone to 'Asia/Calcutta', as jenkins test are executed in this timezone
      serviceData.setTimezone("Asia/Calcutta");

      CnsClientConfiguration config = new CnsClientConfiguration();

      config.setBaseUrl(new CommonParamLoader(serviceData).getValue(CommonParamKey.CNS_SERVER_URL));
      config.setLogger(CnsClientLogger.getInstance());
      config.setUser("DGS_ICDM");
      config.setPassword(new WsSystemLoader(serviceData).getTokenIcdm());

      config.setProducerPort(0);
      CnsClientConfiguration.initialize(config);
    }
    catch (CnsServiceClientException e) {
      LOG.error("Failed to initialize CNS client. " + e.getMessage(), e);
    }
  }
}
