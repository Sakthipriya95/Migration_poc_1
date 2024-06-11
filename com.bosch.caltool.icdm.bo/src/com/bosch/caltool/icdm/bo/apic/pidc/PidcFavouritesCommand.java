package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicUser;
import com.bosch.caltool.icdm.database.entity.apic.TabvPidFavorite;
import com.bosch.caltool.icdm.model.apic.pidc.PidcFavourite;


/**
 * Command class for Pidc Favourite
 *
 * @author dja7cob
 */
public class PidcFavouritesCommand extends AbstractCommand<PidcFavourite, PidcFavouriteLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public PidcFavouritesCommand(final ServiceData serviceData, final PidcFavourite input, final boolean isUpdate,
      final boolean isDelete) throws IcdmException {
    super(serviceData, input, new PidcFavouriteLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : (isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TabvPidFavorite dbPidcFav = new TabvPidFavorite();

    PidcLoader pidcLoader = new PidcLoader(getServiceData());
    dbPidcFav.setTabvProjectidcard(pidcLoader.getEntityObject(getInputData().getPidcId()));

    TabvApicUser tabvApicUser = new UserLoader(getServiceData()).getEntityObject(getInputData().getUserId());
    dbPidcFav.setTabvApicUser(tabvApicUser);
    Set<TabvPidFavorite> tabvPidFavorites = tabvApicUser.getTabvPidFavorites();
    if (null == tabvPidFavorites) {
      tabvPidFavorites = new HashSet<>();
      tabvApicUser.setTabvPidFavorites(tabvPidFavorites);
    }
    tabvPidFavorites.add(dbPidcFav);
    setUserDetails(COMMAND_MODE.CREATE, dbPidcFav);
    persistEntity(dbPidcFav);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    // NA
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    TabvPidFavorite dbPidcFav = new PidcFavouriteLoader(getServiceData()).getEntityObject(getInputData().getId());

    TabvApicUser tabvApicUser = new UserLoader(getServiceData()).getEntityObject(getInputData().getUserId());
    Set<TabvPidFavorite> tabvPidFavorites = tabvApicUser.getTabvPidFavorites();
    if (null != tabvPidFavorites) {
      tabvPidFavorites.remove(dbPidcFav);
    }

    getEm().remove(dbPidcFav);


  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // TODO Auto-generated method stub
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
    // TODO Auto-generated method stub
  }

}
