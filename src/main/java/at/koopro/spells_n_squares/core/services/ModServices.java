package at.koopro.spells_n_squares.core.services;

import at.koopro.spells_n_squares.core.util.dev.DevLogger;
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
        DevLogger.logMethodEntry(ModServices.class, "register", 
            "type=" + (type != null ? type.getSimpleName() : "null"));
        
        if (instance == null) {
            throw new IllegalArgumentException("Service instance cannot be null");
        }
        if (!type.isInstance(instance)) {
            throw new IllegalArgumentException("Instance must be of type " + type.getName());
        }
        services.put(type, instance);
        DevLogger.logStateChange(ModServices.class, "register", 
            "Service registered: " + type.getSimpleName());
        DevLogger.logMethodExit(ModServices.class, "register");
    }
    
    /**
     * Gets a service.
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> type) {
        DevLogger.logMethodEntry(ModServices.class, "get", 
            "type=" + (type != null ? type.getSimpleName() : "null"));
        
        if (testMode && testOverrides.containsKey(type)) {
            T service = (T) testOverrides.get(type);
            DevLogger.logMethodExit(ModServices.class, "get", "test override");
            return service;
        }
        T service = (T) services.get(type);
        if (service == null) {
            DevLogger.logError(ModServices.class, "get", 
                "Service not registered: " + type.getName(), null);
            throw new IllegalStateException("Service not registered: " + type.getName());
        }
        DevLogger.logMethodExit(ModServices.class, "get", "service");
        return service;
    }
    
    /**
     * Gets a service, returning null if not registered.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getOrNull(Class<T> type) {
        DevLogger.logMethodEntry(ModServices.class, "getOrNull", 
            "type=" + (type != null ? type.getSimpleName() : "null"));
        
        if (testMode && testOverrides.containsKey(type)) {
            T service = (T) testOverrides.get(type);
            DevLogger.logMethodExit(ModServices.class, "getOrNull", service != null ? "test override" : "null");
            return service;
        }
        T service = (T) services.get(type);
        DevLogger.logMethodExit(ModServices.class, "getOrNull", service != null ? "service" : "null");
        return service;
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




