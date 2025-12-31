package at.koopro.spells_n_squares.features.economy.system;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * System for managing Gringotts bank vaults.
 */
public final class GringottsSystem {
    private static final Map<UUID, VaultData> PLAYER_VAULTS = new HashMap<>();
    
    private GringottsSystem() {
    }
    
    /**
     * Gets vault data for a player.
     */
    public static VaultData getVault(Player player) {
        return PLAYER_VAULTS.getOrDefault(player.getUUID(), new VaultData(player.getUUID()));
    }
    
    /**
     * Deposits currency into a player's vault.
     */
    public static boolean deposit(Player player, int galleons, int sickles, int knuts) {
        if (!CurrencySystem.removeCurrency(player, galleons, sickles, knuts)) {
            return false;
        }
        
        VaultData vault = getVault(player);
        VaultData newVault = vault.add(galleons, sickles, knuts);
        PLAYER_VAULTS.put(player.getUUID(), newVault);
        return true;
    }
    
    /**
     * Withdraws currency from a player's vault.
     */
    public static boolean withdraw(Player player, int galleons, int sickles, int knuts) {
        VaultData vault = getVault(player);
        if (!vault.hasEnough(galleons, sickles, knuts)) {
            return false;
        }
        
        VaultData newVault = vault.remove(galleons, sickles, knuts);
        PLAYER_VAULTS.put(player.getUUID(), newVault);
        CurrencySystem.addCurrency(player, galleons, sickles, knuts);
        return true;
    }
    
    /**
     * Represents vault data for a player.
     */
    public static class VaultData {
        private final UUID ownerId;
        private int galleons = 0;
        private int sickles = 0;
        private int knuts = 0;
        private BlockPos vaultLocation;
        
        public VaultData(UUID ownerId) {
            this.ownerId = ownerId;
        }
        
        public UUID getOwnerId() {
            return ownerId;
        }
        
        public int getGalleons() {
            return galleons;
        }
        
        public int getSickles() {
            return sickles;
        }
        
        public int getKnuts() {
            return knuts;
        }
        
        public BlockPos getVaultLocation() {
            return vaultLocation;
        }
        
        public void setVaultLocation(BlockPos pos) {
            this.vaultLocation = pos;
        }
        
        public VaultData add(int galleons, int sickles, int knuts) {
            int totalKnuts = CurrencySystem.toKnuts(this.galleons + galleons, this.sickles + sickles, this.knuts + knuts);
            CurrencySystem.CurrencyAmount amount = CurrencySystem.fromKnuts(totalKnuts);
            VaultData newVault = new VaultData(ownerId);
            newVault.galleons = amount.galleons();
            newVault.sickles = amount.sickles();
            newVault.knuts = amount.knuts();
            newVault.vaultLocation = this.vaultLocation;
            return newVault;
        }
        
        public VaultData remove(int galleons, int sickles, int knuts) {
            int totalKnuts = CurrencySystem.toKnuts(this.galleons, this.sickles, this.knuts);
            int removeKnuts = CurrencySystem.toKnuts(galleons, sickles, knuts);
            CurrencySystem.CurrencyAmount amount = CurrencySystem.fromKnuts(Math.max(0, totalKnuts - removeKnuts));
            VaultData newVault = new VaultData(ownerId);
            newVault.galleons = amount.galleons();
            newVault.sickles = amount.sickles();
            newVault.knuts = amount.knuts();
            newVault.vaultLocation = this.vaultLocation;
            return newVault;
        }
        
        public boolean hasEnough(int galleons, int sickles, int knuts) {
            int totalKnuts = CurrencySystem.toKnuts(this.galleons, this.sickles, this.knuts);
            int requiredKnuts = CurrencySystem.toKnuts(galleons, sickles, knuts);
            return totalKnuts >= requiredKnuts;
        }
    }
}












