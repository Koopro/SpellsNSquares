package at.koopro.spells_n_squares.core.events;

import net.neoforged.bus.api.BusBuilder;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;

/**
 * Event bus for inter-module communication.
 * Modules publish events instead of calling each other directly.
 */
public final class ModuleEventBus {
    private static final ModuleEventBus INSTANCE = new ModuleEventBus();
    private final IEventBus bus;
    
    private ModuleEventBus() {
        this.bus = BusBuilder.builder().build();
    }
    
    /**
     * Gets the singleton instance of the module event bus.
     * @return The event bus instance
     */
    public static ModuleEventBus getInstance() {
        return INSTANCE;
    }
    
    /**
     * Posts an event to the bus.
     * @param event The event to post
     * @return The event (may be modified by handlers)
     */
    public <T extends Event> T post(T event) {
        bus.post(event);
        return event;
    }
    
    /**
     * Registers an event handler.
     * @param handler The handler object (methods annotated with @SubscribeEvent)
     */
    public void register(Object handler) {
        bus.register(handler);
    }
    
    /**
     * Unregisters an event handler.
     * @param handler The handler object
     */
    public void unregister(Object handler) {
        bus.unregister(handler);
    }
    
    /**
     * Gets the underlying NeoForge event bus.
     * @return The event bus
     */
    public IEventBus getBus() {
        return bus;
    }
}


