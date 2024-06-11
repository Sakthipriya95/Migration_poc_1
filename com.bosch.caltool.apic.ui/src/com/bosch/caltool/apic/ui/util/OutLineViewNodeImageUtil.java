/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.swt.graphics.Image;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.client.bo.uc.FavUseCaseItemNode;
import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.LinkServiceClient;

/**
 * @author dmo5cob
 */
public class OutLineViewNodeImageUtil {


  /**
   * This set has the
   */
  private final Set<Long> links = new HashSet<>();


  /**
   * Get Instance method.
   *
   * @return instance of this class
   */
  public OutLineViewNodeImageUtil() {
    loadLinks();

  }


  /**
   *
   */
  private void loadLinks() {
    LinkServiceClient client = new LinkServiceClient();
    try {
      Map<String, Set<Long>> mapOfNodeIdsHavingLinks = client.getAllNodeIdByType();
      if (null != mapOfNodeIdsHavingLinks) {
        if (null != mapOfNodeIdsHavingLinks.get(MODEL_TYPE.GROUP.getTypeCode())) {
          this.links.addAll(mapOfNodeIdsHavingLinks.get(MODEL_TYPE.GROUP.getTypeCode()));
        }
        if (null != mapOfNodeIdsHavingLinks.get(MODEL_TYPE.SUPER_GROUP.getTypeCode())) {
          this.links.addAll(mapOfNodeIdsHavingLinks.get(MODEL_TYPE.SUPER_GROUP.getTypeCode()));
        }
        if (null != mapOfNodeIdsHavingLinks.get(MODEL_TYPE.USE_CASE.getTypeCode())) {
          this.links.addAll(mapOfNodeIdsHavingLinks.get(MODEL_TYPE.USE_CASE.getTypeCode()));
        }
        if (null != mapOfNodeIdsHavingLinks.get(MODEL_TYPE.USE_CASE_GROUP.getTypeCode())) {
          this.links.addAll(mapOfNodeIdsHavingLinks.get(MODEL_TYPE.USE_CASE_GROUP.getTypeCode()));
        }
        if (null != mapOfNodeIdsHavingLinks.get(MODEL_TYPE.USE_CASE_SECT.getTypeCode())) {
          this.links.addAll(mapOfNodeIdsHavingLinks.get(MODEL_TYPE.USE_CASE_SECT.getTypeCode()));
        }
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
    }

  }

  /**
   * Icon for usecase which are not up to date
   *
   * @param baseImgKey base image key
   * @param overlayKey overlay image key
   * @param element object
   * @return baseimage/decoratedImage
   */
  public Image getOutdatedOverLayedImage(final ImageKeys baseImgKey, final ImageKeys overlayKey, final Object element) {
    if (element instanceof UsecaseClientBO) {
      UsecaseClientBO uc = (UsecaseClientBO) element;

      if (!uc.isUpToDate()) {
        if ((baseImgKey != null) && (overlayKey != null)) {
          return ImageManager.getDecoratedImage(baseImgKey, overlayKey, IDecoration.BOTTOM_LEFT);
        }
      }
    }
    return ImageManager.getInstance().getRegisteredImage(baseImgKey);
  }


  /**
   * ICDM-931 Gives the deocrated image for objects which has links
   *
   * @param baseImgKey base image key
   * @param overlayKey overlay image key
   * @param element object
   * @return baseimage/decoratedImage
   */
  public Image getLinkOverLayedImage(final ImageKeys baseImgKey, final ImageKeys overlayKey, final IModel element) {
    // ICDM-959

    boolean hasLink = this.links.contains(element.getId());
    if (!hasLink) {
      if (baseImgKey != null) {
        return ImageManager.getInstance().getRegisteredImage(baseImgKey);
      }
      return null;
    }
    // ICDM-1021
    return ImageManager.getDecoratedImage(baseImgKey, overlayKey, IDecoration.TOP_RIGHT);
  }


  /**
   * ICDM-1091 Gives the decorated image for objects which are virtual nodes in fav nodes with decorator for link
   *
   * @param baseImgKey base image key
   * @param element object for which link is present
   * @return baseimage/decoratedImage
   */
  public Image getVirtualLinkOverLayedImage(final ImageKeys baseImgKey, final FavUseCaseItemNode element) {

    boolean hasLink = this.links.contains(element.getUseCaseItem().getID());
    if (!hasLink) {
      if (baseImgKey != null) {
        ImageKeys overlayKey = ImageKeys.VIRTUAL_DECORATOR_12X12;
        return ImageManager.getDecoratedImage(baseImgKey, overlayKey, IDecoration.BOTTOM_LEFT);
      }
      return null;
    }
    ImageKeys[] overlayKeys = { null, ImageKeys.LINK_DECORATOR_12X12, ImageKeys.VIRTUAL_DECORATOR_12X12, null };
    return ImageManager.getMultipleDecoratedImage(baseImgKey, overlayKeys);
  }

  /**
   * ICDM-1091 Gives the decorated image for objects which are virtual nodes in fav nodes with decorator for link
   *
   * @param baseImgKey base image key
   * @param element object for which link is present
   * @return baseimage/decoratedImage
   */
  public Image getVirtualLinkOverLayedImage(final ImageKeys baseImgKey, final IModel element) {

    boolean hasLink = this.links.contains(element.getId());
    if (!hasLink) {
      if (baseImgKey != null) {
        ImageKeys overlayKey = ImageKeys.VIRTUAL_DECORATOR_12X12;
        return ImageManager.getDecoratedImage(baseImgKey, overlayKey, IDecoration.BOTTOM_LEFT);
      }
      return null;
    }
    ImageKeys[] overlayKeys = { null, ImageKeys.LINK_DECORATOR_12X12, ImageKeys.VIRTUAL_DECORATOR_12X12, null };
    return ImageManager.getMultipleDecoratedImage(baseImgKey, overlayKeys);
  }

  /**
   * ICDM-1091 Gives the decorated image for objects which are fav nodes with decorator for link
   *
   * @param baseImgKey base image key
   * @param element object for which link is present
   * @return baseimage/decoratedImage
   */
  public Image getFavLinkOverLayedImage(final ImageKeys baseImgKey, final FavUseCaseItemNode element) {

    boolean hasLink = this.links.contains(element.getUseCaseItem().getID());
    if (!hasLink) {
      if (baseImgKey != null) {
        ImageKeys overlayKey = ImageKeys.FAV_NODE_DECORATOR_12X12;
        return ImageManager.getDecoratedImage(baseImgKey, overlayKey, IDecoration.BOTTOM_RIGHT);
      }
      return null;
    }
    ImageKeys[] overlayKeys = { null, ImageKeys.LINK_DECORATOR_12X12, null, ImageKeys.FAV_NODE_DECORATOR_12X12 };
    return ImageManager.getMultipleDecoratedImage(baseImgKey, overlayKeys);
  }

  /**
   * ICDM-1091 Gives the decorated image for objects which are fav nodes with decorator for link
   *
   * @param baseImgKey base image key
   * @param element object for which link is present
   * @return baseimage/decoratedImage
   */
  public Image getFavLinkOverLayedImage(final ImageKeys baseImgKey, final IModel element) {

    boolean hasLink = this.links.contains(element.getId());
    if (!hasLink) {
      if (baseImgKey != null) {
        ImageKeys overlayKey = ImageKeys.FAV_NODE_DECORATOR_12X12;
        return ImageManager.getDecoratedImage(baseImgKey, overlayKey, IDecoration.BOTTOM_RIGHT);
      }
      return null;
    }
    ImageKeys[] overlayKeys = { null, ImageKeys.LINK_DECORATOR_12X12, null, ImageKeys.FAV_NODE_DECORATOR_12X12 };
    return ImageManager.getMultipleDecoratedImage(baseImgKey, overlayKeys);
  }

  /**
   * @param uc28x30
   * @param element
   * @return
   */
  public Image getLinkOutdatedOverLayedImage(final ImageKeys baseImgKey, final IModel element) {

    boolean hasLink = this.links.contains(element.getId());
    if (!hasLink) {
      if (baseImgKey != null) {
        ImageKeys[] overlayKeys = { null, ImageKeys.OUTDATED, null, null };
        return ImageManager.getMultipleDecoratedImage(baseImgKey, overlayKeys);
      }
      return null;
    }
    ImageKeys[] overlayKeys =
        { null, ImageKeys.LINK_DECORATOR_12X12, ImageKeys.VIRTUAL_DECORATOR_12X12, ImageKeys.OUTDATED, null };
    return ImageManager.getMultipleDecoratedImage(baseImgKey, overlayKeys);
  }

  /**
   * @param uc28x30
   * @param element
   * @return
   */
  public Image getVirtualLinkOutdatedOverLayedImage(final ImageKeys baseImgKey, final IModel element) {

    boolean hasLink = this.links.contains(element.getId());
    if (!hasLink) {
      if (baseImgKey != null) {
        ImageKeys[] overlayKeys = { null, ImageKeys.OUTDATED, ImageKeys.VIRTUAL_DECORATOR_12X12, null };
        return ImageManager.getMultipleDecoratedImage(baseImgKey, overlayKeys);
      }
      return null;
    }
    ImageKeys[] overlayKeys =
        { null, ImageKeys.LINK_DECORATOR_12X12, ImageKeys.VIRTUAL_DECORATOR_12X12, ImageKeys.OUTDATED };
    return ImageManager.getMultipleDecoratedImage(baseImgKey, overlayKeys);
  }


}
