package com.bosch.caltool.apic.ui.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.jobs.rules.MutexRule;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVcdmTransferInput;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVcdmTransferServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author rvu1cob
 */
public class PidcVcdmTransferJob extends Job {


  /**
   * Instance of PIDCard
   */
  private final Pidc pidc;


  /**
   * Creates an instance of this class
   *
   * @param rule MutexRule
   * @param pidc PIDCard obj
   */
  public PidcVcdmTransferJob(final MutexRule rule, final Pidc pidc) {
    super("Transfering PIDC to vCDM ... ");
    setRule(rule);
    this.pidc = pidc;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final IStatus run(final IProgressMonitor monitor) {
    // begin the progress monitor
    monitor.beginTask("", 100);

    Pidc ret = null;

    try {
      // set the PIDC and encrypted password to the input details
      PidcVcdmTransferInput input = new PidcVcdmTransferInput();
      input.setPidc(this.pidc);
      input.setEncPwd(new CurrentUserBO().getEncPassword());

      // call the webservice to perfrom vcdm transfer
      ret = new PidcVcdmTransferServiceClient().transferPidc(input);

      CDMLogger.getInstance().infoDialog("Transfer to vCDM completed !", Activator.PLUGIN_ID);

      // end the progress monitor
      monitor.worked(100);
      monitor.subTask("Transfer to vCDM finished!");
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    // ret is null if vcdm transfer fails
    return (ret != null) ? Status.OK_STATUS : Status.CANCEL_STATUS;

  }
}
