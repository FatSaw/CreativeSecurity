package com.ruinscraft.panilla;

class NbtCheck_BucketVariantTag extends NbtCheck {

	protected NbtCheck_BucketVariantTag() {
        super("BucketVariantTag");
    }

    @Override
    protected NbtCheckResult check(NbtTagCompound tag, String itemName, ServerShield panilla) {
        return NbtCheckResult.PASS;
    }

}
