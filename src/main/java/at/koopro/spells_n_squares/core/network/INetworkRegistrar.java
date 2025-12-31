package at.koopro.spells_n_squares.core.network;

import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Interface for modules to register their own network payloads.
 * Modules implementing this interface can self-register their network code.
 */
public interface INetworkRegistrar {
    /**
     * Registers all network payloads for this module.
     * @param registrar The payload registrar
     */
    void registerPayloads(PayloadRegistrar registrar);
}


