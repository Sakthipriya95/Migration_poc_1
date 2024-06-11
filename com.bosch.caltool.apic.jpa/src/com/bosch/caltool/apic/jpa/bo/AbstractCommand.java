package com.bosch.caltool.apic.jpa.bo;

import java.util.Map;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractDataCommand;
import com.bosch.caltool.dmframework.bo.AbstractDataProvider;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.MailHotline;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * Abstract class as base for all commands
 *
 * @author hef2fe
 */
public abstract class AbstractCommand extends AbstractDataCommand { // NOPMD by bne4cob on

  // 6/26/13 12:33 PM

  /**
   * Constructor
   *
   * @param dataProvider the apic data provider
   */
  protected AbstractCommand(final AbstractDataProvider dataProvider) {
    super(dataProvider);
  }

  /**
   * @return the dataProvider
   */
  @Override
  protected final ApicDataProvider getDataProvider() {
    return (ApicDataProvider) super.getDataProvider();
  }

  /**
   * @return the dataLoader
   */
  @Override
  protected final DataLoader getDataLoader() {
    return getDataProvider().getDataLoader();
  }

  /**
   * @return the dataCache
   */
  @Override
  protected final DataCache getDataCache() {
    return getDataProvider().getDataCache();
  }

  /**
   * @return the entityProvider
   */
  @Override
  protected final EntityProvider getEntityProvider() {
    return getDataProvider().getEntityProvider();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Map<Long, ChangedData> getChangedData() { // NOPMD by BNE4COB on 12/12/13 9:43 PM
    return super.getChangedData();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setStaleDataValidate(final boolean validate) { // NOPMD by BNE4COB on 12/12/13 9:44 PM
    super.setStaleDataValidate(validate);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected abstract void rollBackDataModel();


  /**
   * iCDM-834 Gets the sender object with receipient as iCDM Hotline
   *
   * @return MailHotline
   */
  protected MailHotline getHotlineNotifier() {
    // old framework code , removing the reference

    // get the HOTLINE address from table
    String toAddr = getDataProvider().getParameterValue(ApicConstants.ICDM_HOTLINE_TO);
    // get notification status // icdm-946
    String status = getDataProvider().getParameterValue(ApicConstants.MAIL_NOTIFICATION_ENABLED);
    if (ApicUtil.compare(status, ApicConstants.YES) == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      return new MailHotline(null, toAddr, true/* automatic notification enabled */);
    }
    // Set details and send mail
    return new MailHotline(null, toAddr, false/* automatic notification disabled */);
  }


  /**
   * iCDM-889 Gets the sender object with receipient as iCDM User(s)
   *
   * @param toAddr to addresses to be notified by hotline
   * @return MailHotline
   */
  protected MailHotline getUserNotifier(final Set<String> toAddr) {
    // get the HOTLINE address from table
    String fromAddr = getDataProvider().getParameterValue(ApicConstants.ICDM_HOTLINE_TO);
    // get notification status // icdm-946
    String status = getDataProvider().getParameterValue(ApicConstants.MAIL_NOTIFICATION_ENABLED);
    if (ApicUtil.compare(status, ApicConstants.YES) == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      return new MailHotline(fromAddr, toAddr, true/* automatic notification enabled */);
    }
    // Create the mail sender with from and to addresses
    return new MailHotline(fromAddr, toAddr, false/* automatic notification disabled */);
  }

}
