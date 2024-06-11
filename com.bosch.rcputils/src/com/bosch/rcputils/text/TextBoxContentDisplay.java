/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.rcputils.text;


/**
 * @author svj7cob
 */

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


/**
 * This class is used to count the content inside the text and display the number of characters left in the text box and
 * returned as a composite
 * 
 * @author svj7cob
 */
// ICDM-2006 (Parent task : ICDM-1774)
public class TextBoxContentDisplay extends Composite {

  /**
   * the text field where the content to be listened
   */
  private Text text;

  /**
   * the exact text limit in the text box, given in the constructor
   */
  private int maxLength;

  /**
   * the user defined style
   */
  private final int style;

  /**
   * gridData For Text
   */
  private final GridData gridDataForText;

  /**
   * gridData For Composite
   */
  private final GridData gridDataForComposite;

  /**
   * color of default text for character left
   */
  private Color colorGrey;

  /**
   * Constant to be shown in message label while showing the limitation of the text box
   */
  public static final String CHAR_LEFT = " characters left of ";

  /**
   * flag to display the label
   */
  private boolean canDisplayTheLabel;

  /**
   * message label to show the left out characters in the text box
   */
  private Label messageLabel;

  /**
   * This constructor is to be used if the message label below the Text to be displayed
   * 
   * @param composite the given composite
   * @param style the user defined style
   * @param maxLength the maximum character a user can enter in the Text box
   * @param gridDataForText the user defined grid data
   */
  public TextBoxContentDisplay(final Composite composite, final int style, final int maxLength,
      final GridData gridDataForText) {
    this(composite, style, gridDataForText, true, maxLength);
  }

  /**
   * This constructor is to be used if the message label below the Text not to be displayed
   * 
   * @param composite the given composite
   * @param style the user defined style
   * @param gridDataForText the user defined grid data
   * @param maxLength the maximum length that the text can afford
   * @param canDisplayTheLabel can display the message label
   */
  public TextBoxContentDisplay(final Composite composite, final int style, final GridData gridDataForText,
      final boolean canDisplayTheLabel, final int maxLength) {
    super(composite, SWT.NONE);
    this.style = style;
    this.gridDataForText = gridDataForText;
    this.gridDataForComposite = getGridDataForComposite();
    this.canDisplayTheLabel = canDisplayTheLabel;
    if (canDisplayTheLabel) {
      this.maxLength = maxLength;
      initializeColor();
    }
    createObject();
  }

  /**
   * this method initialize the color for the message label
   */
  private void initializeColor() {
    Display display = Display.getCurrent() == null ? Display.getDefault() : Display.getCurrent();
    colorGrey = new Color(display, 138, 128, 126);
  }

  /**
   * This method gives the text box with the feature of having a ModifyListener, to show the no fo characters left
   */
  private void createObject() {
    // setting layout for composite
    this.setLayout(getGridLayoutForComposite());

    // setting layout data for text field
    text = new Text(this, this.style);
    text.setLayoutData(this.gridDataForText);


    if (this.canDisplayTheLabel) {
      messageLabel = new Label(this, SWT.NONE);

      // setting layout data for message label
      messageLabel.setLayoutData(getGridDataForLabel());

      messageLabel.setText(this.maxLength + CHAR_LEFT + this.maxLength);

      // initially the text to be shown as grey color
      messageLabel.setForeground(colorGrey);
    }
    else {
      text.setEditable(false);
    }
    TextBoxContentListener boxContentListener =
        new TextBoxContentListener(this.canDisplayTheLabel, messageLabel, this.maxLength, colorGrey, this);
    // adding modify listener to the text
    text.addModifyListener(boxContentListener);

    // setting layout data for composite
    setLayoutData(this.gridDataForComposite);
  }


  /**
   * This method defines grid data for Composite
   * 
   * @return GridData
   */
  private GridData getGridDataForComposite() {
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.verticalAlignment = GridData.FILL;
    gridData.verticalSpan = 2;
    gridData.grabExcessVerticalSpace = true;
    return gridData;
  }

  /**
   * This method defines grid layout for composite
   * 
   * @return GridLayout
   */
  private GridLayout getGridLayoutForComposite() {
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 1;
    gridLayout.marginWidth = 0;
    gridLayout.marginHeight = 0;
    return gridLayout;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    if (colorGrey != null && !colorGrey.isDisposed()) {
      colorGrey.dispose();
      colorGrey = null;
    }
    super.dispose();
  }

  /**
   * This method defines grid data for Label
   * 
   * @return GridData
   */
  private GridData getGridDataForLabel() {
    GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.END;
    gridData.verticalAlignment = GridData.BEGINNING;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = false;
    return gridData;
  }

  /**
   * This method return the customized Text having the modify listener
   * 
   * @return the text
   */
  public Text getText() {
    return text;
  }

  /**
   * This method should be used This method is used <br>
   * <ul>
   * 1. to make the text un-editable for unauthorized ones, still they haves permission to copy the text content in the
   * text box <br>
   * </ul>
   * <ul>
   * 2. to make the message label string as empty, so that 'x characters left of x' will not be displayed
   * </ul>
   * 
   * @param canDisplayTheLabel the flag make the text un-editable and message label as empty
   */
  public void setEditable(final boolean canDisplayTheLabel) {
    this.canDisplayTheLabel = canDisplayTheLabel;
    if (!canDisplayTheLabel) {
      getText().setEditable(false);
      messageLabel.setText("");
    }
  }

}