package me.bomb.servershield;

class NbtCheck_BlockStateTag extends NbtCheck {

	protected NbtCheck_BlockStateTag() {
        super("BlockStateTag");
    }

    @Override
    protected NbtCheckResult check(NbtTagCompound tag, String itemName, ServerShield panilla) {
        return NbtCheckResult.FAIL;   // TODO: test variations of BlockStateTag to see what is potentially malicious
    }

}
