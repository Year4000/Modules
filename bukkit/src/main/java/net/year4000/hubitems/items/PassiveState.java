package net.year4000.hubitems.items;

public enum PassiveState {
    NONE,
    ALLWAYS_ON,
    ON,
    OFF,
    ;

    /** Is the instance of this enum passive or none */
    public boolean isPassive() {
        return this != PassiveState.NONE;
    }

    /** Is active to add glow effect */
    public boolean isActive() {
        return this == ALLWAYS_ON || this == ON;
    }
}
