package com.ruinscraft.panilla;

import java.util.UUID;

import net.minecraft.server.v1_15_R1.IChatBaseComponent;

class NbtCheck_EntityTag extends NbtCheck {

    private static final String[] ARMOR_STAND_TAGS = new String[]{"NoGravity", "ShowArms", "NoBasePlate", "Small", "Rotation", "Marker", "Pose", "Invisible"};

    protected NbtCheck_EntityTag() {
        super("EntityTag");
    }

    private static FailedNbt checkItems(NbtTagList items, String nmsItemClassName, ServerShield panilla) {
        for (int i = 0; i < items.size(); i++) {
            NbtTagCompound item = items.getCompound(i);

            if (item.hasKey("tag")) {
                FailedNbt failedNbt = NbtChecks.checkAll(item.getCompound("tag"), nmsItemClassName, panilla);

                if (FailedNbt.fails(failedNbt)) {
                    return failedNbt;
                }
            }
        }

        return FailedNbt.NO_FAIL;
    }

    @Override
    protected NbtCheckResult check(NbtTagCompound tag, String itemName, ServerShield panilla) {
        NbtTagCompound entityTag = tag.getCompound(getName());

        for (String armorStandTag : ARMOR_STAND_TAGS) {
            if (entityTag.hasKey(armorStandTag)) {
                return NbtCheckResult.FAIL;
            }
        }

        if (entityTag.hasKey("CustomName")) {
            String customName = entityTag.getString("CustomName");
            if (customName.length() > 64) {
                return NbtCheckResult.CRITICAL;
            }
            try {
            	IChatBaseComponent.ChatSerializer.a(customName);
            } catch (Exception e) {
                return NbtCheckResult.CRITICAL;
            }
        }

        if (entityTag.hasKey("UUID")) {
            String uuid = entityTag.getString("UUID");

            try {
                UUID.fromString(uuid);
            } catch (Exception e) {
                return NbtCheckResult.CRITICAL;
            }
        }

        if (entityTag.hasKey("ExplosionPower")) {
            return NbtCheckResult.FAIL;
        }

        if (entityTag.hasKey("Invulnerable")) {
            return NbtCheckResult.FAIL;
        }

        if (entityTag.hasKey("Motion")) {
            return NbtCheckResult.FAIL;
        }

        if (entityTag.hasKey("power")) {
            return NbtCheckResult.FAIL;
        }

        if (entityTag.hasKey("ArmorItems")) {
            NbtTagList items = entityTag.getList("ArmorItems", NbtDataType.COMPOUND);

            FailedNbt failedNbt = checkItems(items, itemName, panilla);

            if (FailedNbt.fails(failedNbt)) {
                return failedNbt.result;
            }
        }

        if (entityTag.hasKey("HandItems")) {
            NbtTagList items = entityTag.getList("HandItems", NbtDataType.COMPOUND);

            FailedNbt failedNbt = checkItems(items, itemName, panilla);

            if (FailedNbt.fails(failedNbt)) {
                return failedNbt.result;
            }
        }

        boolean hasIdTag = entityTag.hasKey("id");

        if (hasIdTag) {
            return NbtCheckResult.FAIL; //no eggs with id
            /*
            String id = entityTag.getString("id");

            // prevent lightning bolt eggs
            if (id.toLowerCase().contains("lightning")) {
                return NbtCheckResult.FAIL;
            }

            // check for massive slime spawn eggs
            if (entityTag.hasKey("Size")) {
                if (entityTag.getInt("Size") > 3) {
                    return NbtCheckResult.CRITICAL;
                }
            }

            // blow tags are mostly for EntityAreaEffectCloud
            // see nms.EntityAreaEffectCloud
            if (entityTag.hasKey("Age")) {
                return NbtCheckResult.FAIL;
            }

            if (entityTag.hasKey("Duration")) {
                return NbtCheckResult.FAIL;
            }

            if (entityTag.hasKey("WaitTime")) {
                return NbtCheckResult.FAIL;
            }

            if (entityTag.hasKey("ReapplicationDelay")) {
                return NbtCheckResult.FAIL;
            }

            if (entityTag.hasKey("DurationOnUse")) {
                return NbtCheckResult.FAIL;
            }

            if (entityTag.hasKey("RadiusOnUse")) {
                return NbtCheckResult.FAIL;
            }

            if (entityTag.hasKey("RadiusPerTick")) {
                return NbtCheckResult.FAIL;
            }

            if (entityTag.hasKey("Radius")) {
                return NbtCheckResult.FAIL;
            }

            if (entityTag.hasKey("Particle")) {
                return NbtCheckResult.FAIL;
            }

            if (entityTag.hasKey("Color")) {
                return NbtCheckResult.FAIL;
            }

            if (entityTag.hasKey("Potion")) {
                return NbtCheckResult.FAIL;
            }

            if (entityTag.hasKey("Effects")) {
                return NbtCheckResult.FAIL;
            }
            */
        }

        return NbtCheckResult.PASS;
    }

}
