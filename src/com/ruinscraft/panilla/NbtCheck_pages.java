package com.ruinscraft.panilla;


import java.util.Arrays;

class NbtCheck_pages extends NbtCheck {

    // translations in the game which (intentionally) crash the user
    private static final String[] MOJANG_CRASH_TRANSLATIONS = new String[]{
            "translation.test.invalid",
            "translation.test.invalid2"
    };

    private static final int MINECRAFT_UNICODE_MAX = 65535;
    private static final short[] EMPTY_CHAR_MAP = new short[MINECRAFT_UNICODE_MAX];

    protected NbtCheck_pages() {
        super("pages");
    }

    private static short[] createCharMap(String string) {
        short[] charMap = Arrays.copyOf(EMPTY_CHAR_MAP, EMPTY_CHAR_MAP.length);

        for (char c : string.toCharArray()) {
            charMap[c]++;
        }

        return charMap;
    }

    @Override
    protected NbtCheckResult check(NbtTagCompound tag, String itemName, ServerShield panilla) {
        NbtTagList pages = tag.getList("pages", NbtDataType.STRING);

        if (pages.size() > 50) {
            return NbtCheckResult.CRITICAL; // too many pages
        }

        // iterate through book pages
        for (int i = 0; i < pages.size(); i++) {
            final String page = pages.getString(i);
            final String pageNoSpaces = page.replace(" ", "");

            // check char map
            short[] charMap = createCharMap(pageNoSpaces);
            int uniqueChars = 0;

            for (short count : charMap) {
                if (count > 0) {
                    uniqueChars++;
                }
            }

            if (uniqueChars > 180) { // 180 is a reasonable guess for max characters on a book page. It does depend on character size
                return NbtCheckResult.CRITICAL; // most likely a "crash" book
            }

            // check if contains mojang crash translations
            if (pageNoSpaces.startsWith("{\"translate\"")) {
                for (String crashTranslation : MOJANG_CRASH_TRANSLATIONS) {
                    String translationJson = String.format("{\"translate\":\"%s\"}", crashTranslation);

                    if (page.equals(translationJson)) {
                        return NbtCheckResult.CRITICAL;
                    }
                }
            }
        }

        return NbtCheckResult.PASS;
    }

    private static int getCharCountForItem(NbtTagCompound item) {
        int charCount = 0;

        if (item.hasKey("tag")) {
            NbtTagCompound tag = item.getCompound("tag");

            if (tag.hasKey("pages")) {
                NbtTagList pages = tag.getList("pages", NbtDataType.STRING);

                for (int i = 0; i < pages.size(); i++) {
                    final String page = pages.getString(i);
                    final String pageNoSpaces = page.replace(" ", "");
                    charCount += pageNoSpaces.length();
                }
            }
        }

        return charCount;
    }

    // Gets the amount of characters of books within in a list of items
    protected static int getCharCountForItems(NbtTagList items) {
        int charCount = 0;

        for (int i = 0; i < items.size(); i++) {
            charCount += getCharCountForItem(items.getCompound(i));
        }

        return charCount;
    }

}
