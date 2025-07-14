package io.distorio.operation.api;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class OperationRegistry {

  public static List<ImageOperation> loadAllOperations() {
    List<ImageOperation> operations = new ArrayList<>();
    ServiceLoader<ImageOperationProvider> loader = ServiceLoader.load(ImageOperationProvider.class);
    for (ImageOperationProvider provider : loader) {
      operations.add(provider.create());
    }
    return operations;
  }

  /**
   * Load all operations with their provider's classloader information
   */
  public static List<OperationWithProvider> loadAllOperationsWithProviders() {
    List<OperationWithProvider> operations = new ArrayList<>();
    ServiceLoader<ImageOperationProvider> loader = ServiceLoader.load(ImageOperationProvider.class);
    for (ImageOperationProvider provider : loader) {
      ImageOperation operation = provider.create();
      ClassLoader providerClassLoader = provider.getClass().getClassLoader();
      operations.add(new OperationWithProvider(operation, providerClassLoader));
    }
    return operations;
  }

  /**
   * Wrapper class to hold an operation and its provider's classloader
   */
  public static class OperationWithProvider {
    private final ImageOperation operation;
    private final ClassLoader providerClassLoader;

    public OperationWithProvider(ImageOperation operation, ClassLoader providerClassLoader) {
      this.operation = operation;
      this.providerClassLoader = providerClassLoader;
    }

    public ImageOperation getOperation() {
      return operation;
    }

    public ClassLoader getProviderClassLoader() {
      return providerClassLoader;
    }
  }
}
