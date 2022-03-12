package me.bomb.servershield;


class NbtCheck_author extends NbtCheck {

	protected NbtCheck_author() {
        super("author");
    }

    @Override
    protected NbtCheckResult check(NbtTagCompound tag, String itemName, ServerShield panilla) {
        int authorLength = tag.getString("author").length();

        if (authorLength > 16) {
            return NbtCheckResult.FAIL;
        }

        return NbtCheckResult.PASS;
    }

}
