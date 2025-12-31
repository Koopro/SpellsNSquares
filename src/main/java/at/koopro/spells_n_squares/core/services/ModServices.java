package at.koopro.spells_n_squares.core.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service locator for mod services.
 * Provides dependency injection-like functionality without heavy frameworks.
 */
public final class ModServices {
    private static final Map<Class<?>, Object> services = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Object> testOverrides = new ConcurrentHashMap<>();
    private static boolean testMode = false;
    
    /**
     * Registers a service.
     */
    public static <T> void register(Class<T> type, T instance) {
        if (instance == null) {
            throw new IllegalArgumentException("Service instance cannot be null");
        }
        if (!type.isInstance(instance)) {
            throw new IllegalArgumentException("Instance must be of type " + type.getName());
        }
        services.put(type, instance);
    }
    
    /**
     * Gets a service.
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> type) {
        if (testMode && testOverrides.containsKey(type)) {
            return (T) testOverrides.get(type);
        }
        T service = (T) services.get(type);
        if (service == null) {
            throw new IllegalStateException("Service not registered: " + type.getName());
        }
        return service;
    }
    
    /**
     * Gets a service, returning null if not registered.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getOrNull(Class<T> type) {
        if (testMode && testOverrides.containsKey(type)) {
            return (T) testOverrides.get(type);
        }
        return (T) services.get(type);
    }
    
    /**
     * Checks if a service is registered.
     */
    public static boolean isRegistered(Class<?> type) {
        return services.containsKey(type) || (testMode && testOverrides.containsKey(type));
    }
    
    /**
     * Overrides a service for testing.
     */
    public static <T> void overrideForTest(Class<T> type, T instance) {
        testOverrides.put(type, instance);
    }
    
    /**
     * Enables test mode.
     */
    public static void enableTestMode() {
        testMode = true;
    }
    
    /**
     * Clears all services (for testing).
     */
    public static void clear() {
        services.clear();
        testOverrides.clear();
        testMode = false;
    }
}


