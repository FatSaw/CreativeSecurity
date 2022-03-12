package me.bomb.servershield;


class NbtCheck_Fireworks extends NbtCheck {

	protected NbtCheck_Fireworks() {
        super("Fireworks");
    }

    @Override
    protected NbtCheckResult check(NbtTagCompound tag, String itemName, ServerShield panilla) {
        NbtTagCompound fireworks = tag.getCompound("Fireworks");

        int flight = fireworks.getInt("Flight");

        if (flight > 3 || flight < 1) {
            return NbtCheckResult.FAIL;
        }

        NbtTagList explosions = fireworks.getList("Explosions", NbtDataType.COMPOUND);

        if (explosions != null
                && explosions.size() > 8) {
            return NbtCheckResult.FAIL;
        }

        return NbtCheckResult.PASS;
    }

}
