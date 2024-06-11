/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.database.entity.apic;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.descriptors.OptimisticLockingPolicy.LockOnChange;

// import com.bosch.caltool.dmframework.common.ObjectStore;


/**
 * @author rgo7cob Icdm-652
 */
public class OptimisticLockCustomizer implements DescriptorCustomizer {


  /**
   * {@inheritDoc}
   */
  @Override
  public void customize(final ClassDescriptor descriptor) {

    // try {
    /*
     * causes any change to any relationship to increment version mainly done for use cases if the attr mapping is
     * deleted then the usecase or use case section is incremented.Can be extended for other Entities also.
     */
    descriptor.getOptimisticLockingPolicy().setLockOnChangeMode(LockOnChange.ALL);
    // }
    // catch (Exception ex) {
    // ObjectStore.getInstance().getLogger().error("Exception during the locking");
    // }
  }

}
