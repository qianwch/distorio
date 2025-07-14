package io.distorio.app;

import io.distorio.operation.api.ImageOperation;
import java.util.Stack;

public class OperationHistory {

  private final Stack<ImageOperation> undoStack = new Stack<>();
  private final Stack<ImageOperation> redoStack = new Stack<>();

  public void push(ImageOperation op) {
    undoStack.push(op);
    redoStack.clear();
  }

  public boolean canUndo() {
    return !undoStack.isEmpty();
  }

  public boolean canRedo() {
    return !redoStack.isEmpty();
  }

  public ImageOperation undo() {
    if (!canUndo()) {
      return null;
    }
    ImageOperation op = undoStack.pop();
    redoStack.push(op);
    return op;
  }

  public ImageOperation redo() {
    if (!canRedo()) {
      return null;
    }
    ImageOperation op = redoStack.pop();
    undoStack.push(op);
    return op;
  }

  public void clear() {
    undoStack.clear();
    redoStack.clear();
  }
}
