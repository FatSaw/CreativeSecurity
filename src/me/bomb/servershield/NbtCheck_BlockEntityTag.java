package me.bomb.servershield;

class NbtCheck_BlockEntityTag extends NbtCheck {

	protected NbtCheck_BlockEntityTag() {
        super("BlockEntityTag");
    }

    private FailedNbt checkItems(NbtTagList items, String itemName, ServerShield panilla) {
        int charCount = NbtCheck_pages.getCharCountForItems(items);

        if (charCount > 100_000) {
            return new FailedNbt(getName(), NbtCheck.NbtCheckResult.CRITICAL);
        }

        for (int i = 0; i < items.size(); i++) {
            FailedNbt failedNbt = checkItem(items.getCompound(i), itemName, panilla);

            if (FailedNbt.fails(failedNbt)) {
                return failedNbt;
            }
        }

        return FailedNbt.NO_FAIL;
    }

    private FailedNbt checkItem(NbtTagCompound item, String itemName, ServerShield panilla) {
        if (item.hasKey("tag")) {
            FailedNbt failedNbt = NbtChecks.checkAll(item.getCompound("tag"), itemName, panilla);

            if (FailedNbt.fails(failedNbt)) {
                return failedNbt;
            }
        }

        return FailedNbt.NO_FAIL;
    }

    @Override
    protected NbtCheckResult check(NbtTagCompound tag, String itemName, ServerShield panilla) {
        NbtTagCompound blockEntityTag = tag.getCompound(getName());

        int sizeBytes = blockEntityTag.getStringSizeBytes();

        // ensure BlockEntityTag isn't huge
        if (sizeBytes > 262140) {
            return NbtCheckResult.CRITICAL;
        }

        if (blockEntityTag.hasKey("LootTable")) {
            String lootTable = blockEntityTag.getString("LootTable");

            lootTable = lootTable.trim();

            if (lootTable.isEmpty()) {
                return NbtCheckResult.CRITICAL;
            }

            if (lootTable.contains(":")) {
                String[] keySplit = lootTable.split(":");

                if (keySplit.length < 2) {
                    return NbtCheckResult.CRITICAL;
                }

                String namespace = keySplit[0];
                String key = keySplit[1];

                if (namespace.isEmpty() || key.isEmpty()) {
                    return NbtCheckResult.CRITICAL;
                }
            }
        }

            // locked chests
            if (blockEntityTag.hasKey("Lock")) {
                return NbtCheckResult.FAIL;
            }

            // signs with text
            if (blockEntityTag.hasKey("Text1")
                    || blockEntityTag.hasKey("Text2")
                    || blockEntityTag.hasKey("Text3")
                    || blockEntityTag.hasKey("Text4")) {
                return NbtCheckResult.FAIL;
            }

        // tiles with items/containers (chests, hoppers, shulkerboxes, etc)
        if (blockEntityTag.hasKey("Items")) {
            // only ItemShulkerBoxes should have "Items" NBT tag in survival
            itemName = itemName.toLowerCase();

                if (!(itemName.contains("shulker") || itemName.contains("itemstack") || itemName.contains("itemblock"))) {
                    return NbtCheckResult.FAIL;
                }
            

            // Campfires should not have BlockEntityTag
            if (itemName.contains("campfire")) {
                return NbtCheckResult.FAIL;
            }

            NbtTagList items = blockEntityTag.getList("Items", NbtDataType.COMPOUND);
            FailedNbt failedNbt = checkItems(items, itemName, panilla);

            if (FailedNbt.fails(failedNbt)) {
                return failedNbt.result;
            }
        }

        // check the item within a JukeBox
        if (blockEntityTag.hasKey("RecordItem")) {
            NbtTagCompound item = blockEntityTag.getCompound("RecordItem");

            FailedNbt failedNbt = checkItem(item, itemName, panilla);

            if (FailedNbt.fails(failedNbt)) {
                return failedNbt.result;
            }
        }

        return NbtCheckResult.PASS;
    }

}
