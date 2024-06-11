package com.bosch.caltool.apic.ui.views;

import com.bosch.caltool.apic.ui.editors.PIDCEditorInput;
import com.bosch.caltool.icdm.common.ui.views.AbstractPage;
import com.bosch.caltool.icdm.common.ui.views.IPageCreator;

/**
 * PIDCPageCreator- this class is called during creation of pidc editor
 */

public class PIDCPageCreator implements IPageCreator {

  @SuppressWarnings("unused")
  private final PIDCEditorInput editorInput;
  private PIDCOutlinePage pidcOutlinePage;

  /**
   * Constructor
   *
   * @param dataHandler
   */
  public PIDCPageCreator(final PIDCEditorInput editorInput) {
    this.editorInput = editorInput;
  }

  /**
   * Creates a OUTLINE page for PIDC
   */
  public AbstractPage createPage() {
    this.pidcOutlinePage = new PIDCOutlinePage(this.editorInput.getSelectedPidcVersion(),
        this.editorInput.getUcItemId(), this.editorInput.getOutlineDataHandler(), this.editorInput.isProjUseCase());
    return this.pidcOutlinePage;
  }


  /**
   * @param useCaseItemId Usecase Item Id
   * @return the pidcOutlinePage
   */
  public final PIDCOutlinePage getPidcOutlinePage(final Long useCaseItemId) {
    this.pidcOutlinePage.setOutlineSelectionForUsecase(useCaseItemId);
    return this.pidcOutlinePage;
  }

}