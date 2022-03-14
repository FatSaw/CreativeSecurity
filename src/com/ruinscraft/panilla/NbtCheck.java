package com.ruinscraft.panilla;


abstract class NbtCheck {

	private final String name;
	private final String[] aliases;

    protected NbtCheck(String name, String... aliases) {
        this.name = name;
        this.aliases = aliases;
    }

    protected String getName() {
        return name;
    }

    protected String[] getAliases() {
        return aliases;
    }

    protected abstract NbtCheckResult check(NbtTagCompound tag, String itemName, ServerShield panilla);

    protected enum NbtCheckResult {
        PASS,
        FAIL,
        CRITICAL
    }

}
