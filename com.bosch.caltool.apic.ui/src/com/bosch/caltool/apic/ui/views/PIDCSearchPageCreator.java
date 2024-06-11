/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.views;

import com.bosch.caltool.icdm.client.bo.uc.OutLineViewDataHandler;
import com.bosch.caltool.icdm.common.ui.views.AbstractPage;
import com.bosch.caltool.icdm.common.ui.views.IPageCreator;


/**
 * ICDM-1135
 *
 * @author bru2cob
 */
public class PIDCSearchPageCreator implements IPageCreator {

  private final OutLineViewDataHandler dataHandler;

  /**
   * @param dataHandler
   */
  public PIDCSearchPageCreator(final OutLineViewDataHandler dataHandler) {
    this.dataHandler = dataHandler;
  }

  @Override
  /**
   * Creates a OUTLINE page for PIDC Search
   */
  public AbstractPage createPage() {
    return new PIDCSearchOutlinePage(this.dataHandler);
  }

}