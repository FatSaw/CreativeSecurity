package me.bomb.servershield;

class NbtCheck_BucketVariantTag extends NbtCheck {

	protected NbtCheck_BucketVariantTag() {
        super("BucketVariantTag");
    }

    @Override
    protected NbtCheckResult check(NbtTagCompound tag, String itemName, ServerShield panilla) {
        return NbtCheckResult.PASS;
    }

}
