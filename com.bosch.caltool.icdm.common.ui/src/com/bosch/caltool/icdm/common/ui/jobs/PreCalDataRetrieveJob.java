package com.bosch.caltool.icdm.common.ui.jobs;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.wizard.WizardPage;

import com.bosch.caltool.icdm.common.ui.wizards.PreCalDataExportWizard;
import com.bosch.caltool.icdm.common.ui.wizards.pages.PreCalRecommendedValuesPage;

/**
 * Job which is run in export to CDF wizard
 *
 * @author jvi6cob
 */
public abstract class PreCalDataRetrieveJob extends Job {

  /**
   * current system time in milliseconds
   */
  private final Long timeInMillisecs;
  /**
   * WizardPage
   */
  private final WizardPage wizardPage;

  /**
   * Constructor
   *
   * @param name String
   * @param timeInMillisecs Long
   * @param wizardPage WizardPage
   */
  public PreCalDataRetrieveJob(final String name, final Long timeInMillisecs, final WizardPage wizardPage) {
    super(name);
    this.wizardPage = wizardPage;
    this.timeInMillisecs = timeInMillisecs;
    final PreCalDataExportWizard wizard = (PreCalDataExportWizard) wizardPage.getWizard();
    final PreCalRecommendedValuesPage recomValPage = (PreCalRecommendedValuesPage) wizard.getNextPage(wizardPage);
    recomValPage.setAddedJob(false);
    recomValPage.setJobStartTime(timeInMillisecs);
  }

  /**
   * @return the timeInMillisecs
   */
  public Long getTimeInMillisecs() {
    return this.timeInMillisecs;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void canceling() {
    super.canceling();
    final PreCalDataExportWizard wizard = (PreCalDataExportWizard) this.wizardPage.getWizard();
    final PreCalRecommendedValuesPage recomValPage = (PreCalRecommendedValuesPage) wizard.getNextPage(this.wizardPage);
    recomValPage.setAddedJob(true);
    recomValPage.refreshJobUI();
  }

}