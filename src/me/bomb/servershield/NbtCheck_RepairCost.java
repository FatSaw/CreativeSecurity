package me.bomb.servershield;

class NbtCheck_RepairCost extends NbtCheck {

	protected NbtCheck_RepairCost() {
        super("RepairCost");
    }

    @Override
    protected NbtCheckResult check(NbtTagCompound tag, String itemName, ServerShield panilla) {
        return NbtCheckResult.PASS;
    }

}
