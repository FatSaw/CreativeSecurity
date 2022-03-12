package me.bomb.servershield;

class NbtCheck_title extends NbtCheck {

	protected NbtCheck_title() {
        super("title");
    }

    @Override
    protected NbtCheckResult check(NbtTagCompound tag, String itemName, ServerShield panilla) {
        final int titleLength = tag.getString("title").length();

        if (titleLength > 16) {
            return NbtCheckResult.CRITICAL;
        }

        return NbtCheckResult.PASS;
    }

}
