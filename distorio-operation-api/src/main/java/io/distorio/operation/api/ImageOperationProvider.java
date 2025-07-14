package io.distorio.operation.api;

public interface ImageOperationProvider {

  /**
   * @return a new instance of the operation provided by this plugin
   */
  ImageOperation create();
}
