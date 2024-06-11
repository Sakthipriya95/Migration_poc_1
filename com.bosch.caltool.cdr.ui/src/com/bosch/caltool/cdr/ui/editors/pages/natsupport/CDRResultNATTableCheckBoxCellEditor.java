/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages.natsupport;

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
 * Check box cell editor for CDR Review Result <br>
 * <br>
 */
public class CDRResultNATTableCheckBoxCellEditor extends AbstractCellEditor {

  /**
   * The editor control that paints the checkbox images.
   */
  private Canvas cnvs;
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

    invertState();

    getCanvasAndCommit(parnt);

    closeEditor();

    return this.cnvs;
  }


  /**
   * @param parnt
   */
  private void getCanvasAndCommit(final Composite parnt) {
    this.cnvs = createEditorControl(parnt);

    commit(MoveDirectionEnum.NONE, false);
  }

  /**
   *
   */
  private void invertState() {
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
    return getChkdState();
  }


  /**
   * Sets the value to editor control. The only other values accepted in here are <code>null</code> which is interpreted
   * as <code>false</code> and Strings than can be converted to Boolean directly. Every other object will result in
   * setting the editor value to <code>false</code>.
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
    return this.cnvs;
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
   * {@inheritDoc}
   */
  @Override
  public boolean activateAtAnyPosition() {
    // as the checkbox should only change its value if the icon that represents the checkbox is
    // clicked, this method needs to return false so the IMouseEventMatcher can react on that.
    return false;
  }

  /**
  *
  */
  private void closeEditor() {
    // Close editor so will react to subsequent clicks on the cell
    if ((this.editMode == EditModeEnum.INLINE) && (this.cnvs != null) && !this.cnvs.isDisposed()) {
      this.cnvs.getDisplay().asyncExec(new Runnable() {

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
   * Create the canvas
   * <p>
   * {@inheritDoc}
   */
  @Override
  public Canvas createEditorControl(final Composite parnt) {
    final Canvas canvas = new Canvas(parnt, SWT.NONE);

    addMouseListener(canvas);

    return canvas;
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
   * @return
   */
  private Boolean getChkdState() {
    return Boolean.valueOf(this.chkdState);
  }

  /**
   * @param canvas
   */
  private void addMouseListener(final Canvas canvas) {
    canvas.addMouseListener(new MouseAdapter() {

      /**
       * Invert the checkbox's state
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void mouseUp(final MouseEvent mouseEvent) {
        // Boolean inversion
        CDRResultNATTableCheckBoxCellEditor.this.chkdState ^= true;
        canvas.redraw();
      }
    });
  }

}