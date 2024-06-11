package com.bosch.caltool.icdm.bo.cdr.qnaire;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TQuestion;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionResultOption;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionResultOption;


/**
 * Command class for QuestionResult
 *
 * @author say8cob
 */
public class QuestionResultOptionCommand extends AbstractCommand<QuestionResultOption, QuestionResultOptionLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public QuestionResultOptionCommand(final ServiceData serviceData, final QuestionResultOption input,
      final boolean isUpdate, final boolean isDelete) throws IcdmException {
    super(serviceData, input, new QuestionResultOptionLoader(serviceData),
        (isDelete ? COMMAND_MODE.DELETE : (isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE)));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TQuestionResultOption entity = new TQuestionResultOption();

    TQuestion tQues = new QuestionLoader(getServiceData()).getEntityObject(getInputData().getQId());
    entity.setTQuestion(tQues);
    entity.setQResultName(getInputData().getQResultName());
    entity.setQResultType(getInputData().getQResultType());
    // setting allow to finsh WP
    entity.setqResultAlwFinishWP(booleanToYorN(getInputData().isqResultAlwFinishWP()));

    setUserDetails(COMMAND_MODE.CREATE, entity);

    tQues.addTQuestionResultOption(entity);

    persistEntity(entity);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    QuestionResultOptionLoader loader = new QuestionResultOptionLoader(getServiceData());
    TQuestionResultOption entity = loader.getEntityObject(getInputData().getId());

    TQuestion tQues = new QuestionLoader(getServiceData()).getEntityObject(getInputData().getQId());
    entity.setTQuestion(tQues);
    entity.setQResultName(getInputData().getQResultName());
    entity.setQResultType(getInputData().getQResultType());
    // setting allow to finsh WP
    entity.setqResultAlwFinishWP(booleanToYorN(getInputData().isqResultAlwFinishWP()));

    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    QuestionResultOptionLoader loader = new QuestionResultOptionLoader(getServiceData());
    TQuestionResultOption entity = loader.getEntityObject(getInputData().getId());

    entity.getTQuestion().removeTQuestionResultOption(entity);
    getEm().remove(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // NA
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    // NA
  }

}
