package me.bomb.servershield;

import java.util.Base64;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class NbtCheck_SkullOwner extends NbtCheck {

    private static final Pattern URL_MATCHER = Pattern.compile("url");

    private static UUID minecraftSerializableUuid(final int[] ints) {
        return new UUID((long) ints[0] << 32 | ((long) ints[1] & 0xFFFFFFFFL), (long) ints[2] << 32 | ((long) ints[3] & 0xFFFFFFFFL));
    }

    protected NbtCheck_SkullOwner() {
        super("SkullOwner");
    }

    @Override
    protected NbtCheckResult check(NbtTagCompound tag, String itemName, ServerShield panilla) {
        NbtTagCompound skullOwner = tag.getCompound("SkullOwner");

        if (skullOwner.hasKey("Name")) {
            String name = skullOwner.getString("Name");

            if (name.length() > 64) {
                return NbtCheckResult.CRITICAL;
            }
        }

        if (skullOwner.hasKey("UUID")) {
            String uuidString = skullOwner.getString("UUID");

            try {
                // Ensure valid UUID
                UUID.fromString(uuidString);
            } catch (Exception e) {
                return NbtCheckResult.CRITICAL;
            }
        }

        if (skullOwner.hasKeyOfType("Id", NbtDataType.STRING)) {
            String uuidString = skullOwner.getString("Id");

            try {
                // Ensure valid UUID
                UUID.fromString(uuidString);
            } catch (Exception e) {
                return NbtCheckResult.CRITICAL;
            }
        } else if (skullOwner.hasKeyOfType("Id", NbtDataType.INT_ARRAY)) {
            int[] ints = skullOwner.getIntArray("Id");

            try {
                UUID check = minecraftSerializableUuid(ints);
            } catch (Exception e) {
                return NbtCheckResult.CRITICAL;
            }
        }

        if (skullOwner.hasKey("Properties")) {
            NbtTagCompound properties = skullOwner.getCompound("Properties");

            if (properties.hasKey("textures")) {
                NbtTagList textures = properties.getList("textures", NbtDataType.COMPOUND);

                for (int i = 0; i < textures.size(); i++) {
                    NbtTagCompound entry = textures.getCompound(i);

                    if (entry.hasKey("Value")) {
                        String b64 = entry.getString("Value");
                        String decoded;

                        try {
                            decoded = new String(Base64.getDecoder().decode(b64));
                        } catch (IllegalArgumentException e) {
                            return NbtCheckResult.CRITICAL;
                        }

                        // all lowercase, no parentheses or spaces
                        decoded = decoded.trim()
                                .replace(" ", "")
                                .replace("\"", "")
                                .toLowerCase();

                        Matcher matcher = URL_MATCHER.matcher(decoded);

                        // example: {textures:{SKIN:{url:https://education.minecraft.net/wp-content/uploads/deezcord.png}}}
                        // may contain multiple url tags
                        while (matcher.find()) {
                            String url = decoded.substring(matcher.end() + 1);

                            if (url.startsWith("http://textures.minecraft.net") ||
                                    url.startsWith("https://textures.minecraft.net")) {
                                continue;
                            } else {
                                return NbtCheckResult.FAIL;
                            }
                        }
                    }
                }
            }
        }

        return NbtCheckResult.PASS;
    }

}
