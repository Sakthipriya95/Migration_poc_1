/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.compare;

import org.eclipse.nebula.widgets.nattable.edit.editor.AbstractCellEditor;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.MoveDirectionEnum;
import org.eclipse.nebula.widgets.nattable.widget.EditModeEnum;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


/**
 * @author jvi6cob
 */
public class PIDCCompareNatTableCheckBoxCellEditor extends AbstractCellEditor {

  /**
   * The editor control which paints the checkbox images.
   */
  private Canvas cnvsObj;
  /**
   * The current state of the checkbox having the corresponding value.
   */
  private boolean chkdState;


  /**
   * When the editor is activated, flip the current data value and commit it. The repaint will pick up the new value and
   * flip the image. This is only done if the mouse click is done within the rectangle of the painted checkbox image.
   */
  @Override
  protected Control activateCell(final Composite parnt, final Object originalCanonicalValue) {
    // if this editor was activated by clicking a letter or digit key, do nothing
    if (originalCanonicalValue instanceof Character) {
      return null;
    }

    setCanonicalValue(originalCanonicalValue);

    invertChkedState();

    getCanvasAndCommit(parnt);

    closeEditor();

    return this.cnvsObj;
  }

  /**
   * // Close editor so will react to subsequent clicks on the cell
   */
  private void closeEditor() {
    if ((this.editMode == EditModeEnum.INLINE) && (this.cnvsObj != null) && !this.cnvsObj.isDisposed()) {
      this.cnvsObj.getDisplay().asyncExec(new Runnable() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
          close();
        }
      });

    }
  }

  /**
   * @param parnt
   */
  private void getCanvasAndCommit(final Composite parnt) {
    this.cnvsObj = createEditorControl(parnt);

    commit(MoveDirectionEnum.NONE, false);
  }

  /**
   *
   */
  private void invertChkedState() {
    // Boolean inversion
    this.chkdState ^= true;
  }

  /**
   * Get the current state of check box
   * <p>
   * {@inheritDoc}
   */
  @Override
  public Boolean getEditorValue() {
    return getCurrentState();
  }

  /**
   * @return
   */
  private Boolean getCurrentState() {
    return Boolean.valueOf(this.chkdState);
  }

  /**
   * Sets the value to editor control. The only values accepted are null which is interpreted as false and Strings than
   * can be converted to Boolean directly. Every other object will result in setting the editor value to false
   * 
   * @param value The display value to set to the wrapped editor control.
   */
  @Override
  public void setEditorValue(final Object value) {
    if (value == null) {
      this.chkdState = false;
    }
    else {
      getCheckedState(value);
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Canvas getEditorControl() {
    return this.cnvsObj;
  }

  /**
   * Create the canvas
   * <p>
   * {@inheritDoc}
   */
  @Override
  public Canvas createEditorControl(final Composite parnt) {
    final Canvas cnvs = new Canvas(parnt, SWT.NONE);

    addMouseListener(cnvs);

    return cnvs;
  }


  /**
   * The below method creates a canvas for checkbox without inverting its state. </br>
   * This method is used when the pidcattribute is not modifiable.
   *
   * @param parnt composite
   * @return Canvas
   */
  public Canvas createEditorControlWithoutInversion(final Composite parnt) {
    final Canvas cnvs = new Canvas(parnt, SWT.NONE);

    cnvs.addMouseListener(new MouseAdapter() {

      /**
       * Invert the checkbox's state
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void mouseUp(final MouseEvent mouseEvent) {
        cnvs.redraw();
      }
    });

    return cnvs;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean openMultiEditDialog() {
    // checkbox multi editing is not supported
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean activateAtAnyPosition() {
    // as the checkbox should only change its value if the icon that represents the checkbox is
    // clicked, this method needs to return false so the IMouseEventMatcher can react on that.
    return false;
  }

  /**
   * @param value
   */
  private void getCheckedState(final Object value) {
    if (value instanceof Boolean) {
      this.chkdState = (Boolean) value;
    }
    else if (value instanceof String) {
      this.chkdState = Boolean.valueOf((String) value);
    }
    else {
      this.chkdState = false;
    }
  }

  /**
   * @param cnvs
   */
  private void addMouseListener(final Canvas cnvs) {
    cnvs.addMouseListener(new MouseAdapter() {

      /**
       * Invert the checkbox's state
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void mouseUp(final MouseEvent mouseEvent) {
        // Boolean inversion
        PIDCCompareNatTableCheckBoxCellEditor.this.chkdState ^= true;
        cnvs.redraw();
      }
    });
  }
}
