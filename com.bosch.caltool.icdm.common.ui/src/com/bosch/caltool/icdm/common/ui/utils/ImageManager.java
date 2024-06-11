/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.utils;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.util.CommonUtils;


/**
 * ICDM-948
 *
 * @author bru2cob
 */
public enum ImageManager {

                          /**
                           * Singleton implementation
                           */
                          INSTANCE;

  /**
   * Quadrant size for image descriptors
   */
  private static final int QUADRANT_SIZE = 5;

  /**
   * @return instance
   */
  public static ImageManager getInstance() {
    return INSTANCE;
  }

  /**
   * Registers the image
   *
   * @param key imageKey
   */
  private void register(final ImageKeys key) {
    URL url = FileLocator.find(Platform.getBundle(Activator.PLUGIN_ID), new Path(key.getPath()), null);
    ImageDescriptor imageDesc = ImageDescriptor.createFromURL(url);
    Activator.getDefault().getImageRegistry().put(key.toString(), imageDesc);
  }

  /**
   * Regsiter the image only if its not registered already else return the registered image
   *
   * @param key imageKey
   * @return image
   */
  public Image getRegisteredImage(final ImageKeys key) {
    if (!isRegistered(key)) {
      register(key);
    }
    return getRegistry().get(key.toString());
  }

  /**
   * Checks whether the image is registered in the registry
   *
   * @param key imageKey
   * @return true if image is registered
   */
  private boolean isRegistered(final ImageKeys key) {
    return getRegistry().get(key.getKeyAsString()) != null;
  }

  /**
   * Gets the image registry from the Activator class
   *
   * @return imgRegistry
   */
  private ImageRegistry getRegistry() {
    return Activator.getDefault().getImageRegistry();
  }

  /**
   * Gets the ImageDescriptor using imageKey
   *
   * @param key imgkey
   * @return img path
   */
  public static ImageDescriptor getImageDescriptor(final ImageKeys key) {
    return AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, key.getPath());
  }


  /**
   * @param image
   * @return
   */
  // ICDM-1998
  public static ImageDescriptor getImageDescriptor(final Image image) {
    return new ImageDescriptor() {

      /**
       * Method to get the image data and set in the review result link
       */
      @Override
      public ImageData getImageData() {
        return image.getImageData();
      }
    };
  }

  /**
   * ICDM-1091 Creates decorated image with a base image and an overlay image
   *
   * @param baseImgKey key of base image
   * @param overlayKeys Array of overlay keys
   * @return overlayed/base image
   */
  // ICDM-1091
  public static Image getMultipleDecoratedImage(final ImageKeys baseImgKey, final ImageKeys[] overlayKeys) {
    // decorated image key is created from base image and overlay image key's
    StringBuilder decImgkey = new StringBuilder(baseImgKey.getKeyAsString());
    for (ImageKeys imgKey : overlayKeys) {
      if (CommonUtils.isNotNull(imgKey)) {
        decImgkey.append(imgKey.getKeyAsString());
      }
    }

    Image decImg = Activator.getDefault().getImageRegistry().get(decImgkey.toString());
    // if decorated image is not present in registry it is created and added to registry else returned directly
    if (decImg == null) {
      ImageDescriptor[] imgDescriptors = new ImageDescriptor[QUADRANT_SIZE];
      int index = 0;
      for (ImageKeys imgKey : overlayKeys) {
        if (CommonUtils.isNotNull(imgKey)) {
          imgDescriptors[index] = ImageManager.getImageDescriptor(imgKey);
        }
        index++;
      }
      DecorationOverlayIcon decoratedImage =
          new DecorationOverlayIcon(ImageManager.getInstance().getRegisteredImage(baseImgKey), imgDescriptors);
      decImg = decoratedImage.createImage();
      Activator.getDefault().getImageRegistry().put(decImgkey.toString(), decImg);
    }
    return decImg;
  }

  /**
   * ICDM-931 Creates decorated image with a base image and an overlay image
   *
   * @param baseImgKey key of base image
   * @param overlayKey overlay image key
   * @param overlayImgPos position of overlay image on the base image
   * @return overlayed/base image
   */
  // ICDM-1021
  public static Image getDecoratedImage(final ImageKeys baseImgKey, final ImageKeys overlayKey,
      final int overlayImgPos) {
    // if base image is not available return the overlay image
    if (baseImgKey == null) {
      return ImageManager.getInstance().getRegisteredImage(overlayKey);
    }
    // decorated image key is created from base image and overlay image key's
    final String decImgkey = CommonUtils.concatenate(baseImgKey.getKeyAsString(), overlayKey.getKeyAsString());
    Image decImg = Activator.getDefault().getImageRegistry().get(decImgkey);
    // if decorated image is not present in registry it is created and added to registry else returned directly
    if (decImg == null) {
      DecorationOverlayIcon decoratedImage =
          new DecorationOverlayIcon(ImageManager.getInstance().getRegisteredImage(baseImgKey),
              ImageManager.getImageDescriptor(overlayKey), overlayImgPos);
      decImg = decoratedImage.createImage();
      Activator.getDefault().getImageRegistry().put(decImgkey, decImg);
    }
    return decImg;
  }

}
