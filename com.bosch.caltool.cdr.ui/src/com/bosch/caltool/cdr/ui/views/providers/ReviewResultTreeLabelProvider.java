package com.bosch.caltool.cdr.ui.views.providers;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

import com.bosch.caltool.cdr.ui.editors.ReviewResultEditor;
import com.bosch.caltool.cdr.ui.util.CdrUIConstants;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.ParameterType;
import com.bosch.caltool.icdm.model.cdr.CDRResultFunction;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;


/**
 * @author dmo5cob
 */
public class ReviewResultTreeLabelProvider extends StyledCellLabelProvider implements ILabelProvider {


  /**
   * Constant string for functions
   */
  private static final String FUNCTIONS = "Functions";
  private final ReviewResultClientBO resultData;

  /**
   * @param editor Review Result Editor
   */
  public ReviewResultTreeLabelProvider(final ReviewResultEditor editor) {
    this.resultData = editor.getEditorInput().getResultData();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addListener(final ILabelProviderListener listener) {
    // Not required

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    // Not required

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isLabelProperty(final Object element, final String property) {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeListener(final ILabelProviderListener listener) {
    // Not required

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Image getImage(final Object element) {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getText(final Object element) {

    // Return text based on element type
    if ((element instanceof String) && ((String) element).equals(FUNCTIONS)) { // Level nodes
      return FUNCTIONS;
    }

    else if (element instanceof CDRResultFunction) { // ProjectID Card
      // ICDM-1333
      return this.resultData.getNameWithFuncVersion((CDRResultFunction) element);
    }
    else if (element instanceof CDRResultParameter) { // ProjectID Card
      return this.resultData.getFunctionParameter((CDRResultParameter) element).getName();
    }

    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void update(final ViewerCell cell) {


    // Get the element
    final Object element = cell.getElement();
    // Create a styled string
    final StyledString cellText = new StyledString();
    Image nodeImage = null;
    // Set text and image based on the element type
    if ((element instanceof String) && ((String) element).equals(FUNCTIONS)) {
      nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.FUNCTION_FOLDER_28X30);
      // set the image for root node
      cell.setImage(nodeImage);
      cellText.append(element.toString());
    }
    else if (element instanceof CDRResultFunction) {
      final CDRResultFunction func = (CDRResultFunction) element;
      nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.FUNCTION_28X30);
      // set the image
      cell.setImage(nodeImage);
      // ICDM-1333
      cellText.append(this.resultData.getNameWithFuncVersion(func));
    }

    else if (element instanceof CDRResultParameter) {
      final CDRResultParameter param = (CDRResultParameter) element;
      nodeImage = getImageForParameter(param);
      // set the image
      cell.setImage(nodeImage);
      cellText.append(this.resultData.getFunctionParameter(param).getName());
      // Icdm-619
      if (this.resultData.isReviewed(param)) {
        cell.setForeground(CdrUIConstants.GREEN);
      }
      else if (!this.resultData.isReviewed(param) &&
          !CommonUtils.isEqual(param.getResult(), CDRConstants.RESULT_FLAG.OK.getUiType())) {
        cell.setForeground(CdrUIConstants.RED);
      }
      else {
        cell.setForeground(CdrUIConstants.BLACK);
      }


    }

    cell.setText(cellText.toString());
    // set the image
    cell.setImage(nodeImage);
    super.update(cell);
  }

  private Image getImageForParameter(final CDRResultParameter param) {
    Image nodeImage = null;
    // Map Type image
    if (this.resultData.getFunctionParameter(param).getType().equalsIgnoreCase(ParameterType.MAP.getText())) {
      nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.MAP_16X16);
    }
    // curve type Image
    else if (this.resultData.getFunctionParameter(param).getType().equalsIgnoreCase(ParameterType.CURVE.getText())) {
      nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.CURVE_16X16);
    }
    // Value type Image
    else if (this.resultData.getFunctionParameter(param).getType().equalsIgnoreCase(ParameterType.VALUE.getText())) {
      nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.VALUE_16X16);
    }
    // ascii type image
    else if (this.resultData.getFunctionParameter(param).getType().equalsIgnoreCase(ParameterType.ASCII.getText())) {
      nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.ASCII_16X16);
    }
    // Value block type image
    else if (this.resultData.getFunctionParameter(param).getType().equalsIgnoreCase(ParameterType.VAL_BLK.getText())) {
      nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.VALBLK_16X16);
    }
    // Axis point type image
    else if (this.resultData.getFunctionParameter(param).getType().equalsIgnoreCase(ParameterType.AXIS_PTS.getText())) {
      nodeImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.AXIS_16X16);
    }
    return nodeImage;
  }


}
