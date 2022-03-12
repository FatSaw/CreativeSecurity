package me.bomb.servershield;


class NbtCheck_AttributeModifiers extends NbtCheck {

	protected NbtCheck_AttributeModifiers() {
        super("AttributeModifiers");
    }

    @Override
    protected NbtCheckResult check(NbtTagCompound tag, String itemName, ServerShield panilla) {
        return NbtCheckResult.FAIL;
    }

}
