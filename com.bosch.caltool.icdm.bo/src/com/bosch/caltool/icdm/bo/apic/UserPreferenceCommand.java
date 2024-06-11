package com.bosch.caltool.icdm.bo.apic;

import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TUserPreference;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicUser;
import com.bosch.caltool.icdm.model.apic.UserPreference;


/**
 * Command class for tUserPreference
 *
 * @author EKIR1KOR
 */
public class UserPreferenceCommand extends AbstractCommand<UserPreference, UserPreferenceLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public UserPreferenceCommand(final ServiceData serviceData, final UserPreference input, final boolean isUpdate)
      throws IcdmException {
    super(serviceData, input, new UserPreferenceLoader(serviceData),
        isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TUserPreference entity = new TUserPreference();
    TabvApicUser tabvApicUser = new UserLoader(getServiceData()).getEntityObject(getInputData().getUserId());

    entity.setTabvApicUser(tabvApicUser);
    entity.setUserPrefKey(getInputData().getUserPrefKey());
    entity.setUserPrefVal(getInputData().getUserPrefVal());

    Set<TUserPreference> tUserPreference = tabvApicUser.getTUserPreferences();
    if (null == tUserPreference) {
      tUserPreference = new HashSet<>();
      tabvApicUser.setTUserPreferences(tUserPreference);
    }

    tUserPreference.add(entity);
    setUserDetails(COMMAND_MODE.CREATE, entity);
    persistEntity(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    UserPreferenceLoader loader = new UserPreferenceLoader(getServiceData());
    TUserPreference entity = loader.getEntityObject(getInputData().getId());

    entity.setUserPrefKey(getInputData().getUserPrefKey());
    entity.setUserPrefVal(getInputData().getUserPrefVal());

    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // not applicable
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
    // not applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    // not applicable
  }

}
