package com.ruinscraft.panilla;

class FailedNbt {

	protected static FailedNbt NO_FAIL = new FailedNbt(null, NbtCheck.NbtCheckResult.PASS);
    protected static FailedNbt FAIL_KEY_THRESHOLD = new FailedNbt(null, NbtCheck.NbtCheckResult.CRITICAL);

    protected final String key;
    protected final NbtCheck.NbtCheckResult result;

    protected FailedNbt(String key, NbtCheck.NbtCheckResult result) {
        this.key = key;
        this.result = result;
    }

    protected static boolean passes(FailedNbt failedNbt) {
        if (failedNbt == null) {
            return true;
        } else if (failedNbt.equals(NO_FAIL)) {
            return true;
        } else {
            return failedNbt.result == NbtCheck.NbtCheckResult.PASS;
        }
    }

    protected static boolean fails(FailedNbt failedNbt) {
        return !passes(failedNbt);
    }

    protected static boolean failsThreshold(FailedNbt failedNbt) {
        return failedNbt.equals(FAIL_KEY_THRESHOLD);
    }

}
