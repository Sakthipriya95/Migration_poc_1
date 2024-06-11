/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.views;

import com.bosch.caltool.icdm.client.bo.uc.OutLineViewDataHandler;
import com.bosch.caltool.icdm.common.ui.views.AbstractPage;
import com.bosch.caltool.icdm.common.ui.views.IPageCreator;


/**
 * Outline page creator for Attributes page
 *
 * @author adn1cob
 */
public class AttributesPageCreator implements IPageCreator {

  private final OutLineViewDataHandler dataHandler;

  /**
   * @param dataHandler Attributes OutLine View Data Handler
   */
  public AttributesPageCreator(final OutLineViewDataHandler dataHandler) {
    this.dataHandler = dataHandler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AbstractPage createPage() {
    return new AttributesOutlinePage(this.dataHandler);
  }

}
