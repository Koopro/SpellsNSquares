package at.koopro.spells_n_squares.core.api.addon.events;

import net.neoforged.bus.api.BusBuilder;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;

/**
 * Event bus for addon-specific events.
 * Allows addons to subscribe to and publish custom events.
 */
public final class AddonEventBus {
    private static final AddonEventBus INSTANCE = new AddonEventBus();
    private final IEventBus bus;
    
    private AddonEventBus() {
        this.bus = BusBuilder.builder().build();
    }
    
    /**
     * Gets the singleton instance of the addon event bus.
     * @return The event bus instance
     */
    public static AddonEventBus getInstance() {
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









