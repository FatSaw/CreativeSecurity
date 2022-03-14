package com.ruinscraft.panilla;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

class NbtCheck_display extends NbtCheck {

    private static final JsonParser PARSER = new JsonParser();

    protected NbtCheck_display() {
        super("display");
    }

    private static String createTextFromJsonArray(JsonArray jsonArray) {
        StringBuilder text = new StringBuilder();

        for (JsonElement jsonElement : jsonArray) {
            text.append(jsonElement.getAsJsonObject().get("text").getAsString());
        }

        return text.toString();
    }

    @Override
    protected NbtCheckResult check(NbtTagCompound tag, String itemName, ServerShield panilla) {
        NbtTagCompound display = tag.getCompound(getName());

        String name = display.getString("Name");

        // check for Json array
        if (name.startsWith("[{")) {
            try {
                JsonElement jsonElement = PARSER.parse(name);
                JsonArray jsonArray = jsonElement.getAsJsonArray();

                name = createTextFromJsonArray(jsonArray);
            } catch (Exception e) {
                // could not parse Json
            }
        }

        // check for Json object
        else if (name.startsWith("{")) {
            try {
                JsonElement jsonElement = PARSER.parse(name);
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                JsonArray jsonArray = jsonObject.getAsJsonArray("extra");

                name = createTextFromJsonArray(jsonArray);
            } catch (Exception e) {
                // could not parse Json
            }
        }



        if (name != null && name.length() > 35) {
            return NbtCheckResult.CRITICAL; // can cause crashes
        }

        NbtTagList lore = display.getList("Lore", NbtDataType.LIST);

        if (lore.size() > 48) {
            return NbtCheckResult.CRITICAL; // can cause crashes
        }

        for (int i = 0; i < lore.size(); i++) {
            String line = lore.getString(i);

            if (line.length() > 128) {
                return NbtCheckResult.CRITICAL; // can cause crashes
            }
        }

        return NbtCheckResult.PASS;
    }

}
