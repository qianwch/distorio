package io.distorio.operation.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

public class OperationRegistry {

  // Cache for operation providers by operation ID
  private static final Map<String, ImageOperationProvider> providerCache = new HashMap<>();
  private static volatile boolean cacheInitialized = false;

  // Initialize the cache with all available providers
  private static void initializeCache() {
    if (!cacheInitialized) {
      synchronized (OperationRegistry.class) {
        if (!cacheInitialized) {
          ServiceLoader<ImageOperationProvider> loader = ServiceLoader.load(ImageOperationProvider.class);
          for (ImageOperationProvider provider : loader) {
            ImageOperation operation = provider.create();
            String operationId = operation.getMetadata().getId();
            providerCache.put(operationId, provider);
          }
          cacheInitialized = true;
        }
      }
    }
  }

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
   * Create a new instance of an operation by its ID using the provider system
   */
  public static ImageOperation createOperationById(String operationId) {
    // Initialize cache if not already done
    initializeCache();
    
    // Get provider from cache
    ImageOperationProvider provider = providerCache.get(operationId);
    if (provider != null) {
      return provider.create();
    }
    return null; // Operation not found
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
