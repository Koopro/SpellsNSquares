package at.koopro.spells_n_squares.features.wand.registry;

/**
 * Enum representing wand wood types.
 * Different woods provide different characteristics.
 */
public enum WandWood {
    OAK("oak"),
    ASH("ash"),
    ELM("elm"),
    CHERRY("cherry"),
    WALNUT("walnut"),
    MAPLE("maple"),
    WILLOW("willow"),
    YEW("yew"),
    CYPRESS("cypress"),
    FIR("fir"),
    HOLLY("holly"),
    VINE("vine"),
    HAWTHORN("hawthorn"),
    CHESTNUT("chestnut"),
    CEDAR("cedar"),
    BEECH("beech"),
    BLACKTHORN("blackthorn"),
    DOGWOOD("dogwood"),
    ELDER("elder"),
    HORNBEAM("hornbeam"),
    IVY("ivy"),
    LAUREL("laurel"),
    MAHOGANY("mahogany"),
    PEAR("pear"),
    PINE("pine"),
    POPLAR("poplar"),
    REDWOOD("redwood"),
    ROWAN("rowan"),
    SILVER_LIME("silver_lime"),
    SPRUCE("spruce"),
    SYCAMORE("sycamore");
    
    private final String id;
    
    WandWood(String id) {
        this.id = id;
    }
    
    public String getId() {
        return id;
    }
    
    public static WandWood fromId(String id) {
        for (WandWood wood : values()) {
            if (wood.id.equals(id)) {
                return wood;
            }
        }
        return null;
    }
}

