package gasha.creativesecurity.guis;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import gasha.creativesecurity.CreativeSecurityPlugin;
import gasha.creativesecurity.XMaterial;
import gasha.creativesecurity.guis.GuiConfig;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class GuiUtil {
    private static CreativeSecurityPlugin main = CreativeSecurityPlugin.getInstance();
    static String GM_SELECTOR_TITLE;
    static String CREATIVE_INV_SELECTOR_TITLE;
    static String SURVIVAL_INV_SELECTOR_TITLE;
    public static String INVSEE_TITLE;
    public static String INVSEE_TITLE1;
    private static Map<GameMode, ItemStack> gamemodeSkulls;
    private static Map<Integer, ItemStack> numberedSkulls;
    private static ItemStack backArrow;
    private static ItemStack invseeSeparator;
    private static ItemStack invseeInfoItem;
    private static List<Integer> invseeUnusedSlots;

    static void loadStuff() {
        Material separatorMaterial;
        GM_SELECTOR_TITLE = main.getGuiConfig().getString("GmSelector.title");
        CREATIVE_INV_SELECTOR_TITLE = main.getGuiConfig().getString("InventorySelector.title-creative");
        SURVIVAL_INV_SELECTOR_TITLE = main.getGuiConfig().getString("InventorySelector.title-survival");
        INVSEE_TITLE = main.getGuiConfig().getString("Invsee.title");
        INVSEE_TITLE1 = main.getGuiConfig().getString("Invsee.title1");
        GuiConfig guiConfig = main.getGuiConfig();
        gamemodeSkulls = new HashMap<GameMode, ItemStack>();
        gamemodeSkulls.put(GameMode.SURVIVAL, GuiUtil.createGamemodeSkull(GameMode.SURVIVAL, guiConfig));
        gamemodeSkulls.put(GameMode.CREATIVE, GuiUtil.createGamemodeSkull(GameMode.CREATIVE, guiConfig));
        gamemodeSkulls.put(GameMode.SPECTATOR, GuiUtil.createGamemodeSkull(GameMode.SPECTATOR, guiConfig));
        gamemodeSkulls.put(GameMode.ADVENTURE, GuiUtil.createGamemodeSkull(GameMode.ADVENTURE, guiConfig));
        numberedSkulls = new HashMap<Integer, ItemStack>();
        String path = "InventorySelector.";
        for (int index = 0; index < 10; ++index) {
            numberedSkulls.put(index, GuiUtil.createSkull(guiConfig.getString(path + "skulls." + (index + 1)), guiConfig.getString(path + "name-format").replace("{index}", String.valueOf(index + 1)), guiConfig.getStringList(path + "lore-select")));
        }
        path = "InventorySelector.back.";
        backArrow = GuiUtil.createSkull(guiConfig.getString(path + "url"), guiConfig.getString(path + "name"), guiConfig.getStringList(path + "lore"));
        String configValue = guiConfig.getString("Invsee.separator.material");
        try {
            separatorMaterial = Material.valueOf((String)configValue);
        }
        catch (IllegalArgumentException ex) {
            main.getLogger().severe("Invalid material " + configValue + " in guis config! Using STAINED_GLASS_PANE by default");
            separatorMaterial = Material.valueOf((String)"STAINED_GLASS_PANE");
        }
        invseeSeparator = new ItemStack(separatorMaterial);
        invseeInfoItem = new ItemStack(Material.valueOf((String)guiConfig.getString("Invsee.info.material")));
        invseeUnusedSlots = new ArrayList<Integer>(Arrays.asList(4, 6, 7, 8));
        IntStream.rangeClosed(9, 17).forEach(invseeUnusedSlots::add);
    }

    private static ItemStack createGamemodeSkull(GameMode gamemode, GuiConfig config) {
        String path = "GmSelector." + gamemode.name().toLowerCase();
        return GuiUtil.createSkull(config.getString(path + ".url"), config.getString(path + ".name"), config.getStringList(path + ".lore"));
    }

    public static ItemStack getGamemodeSkull(GameMode gameMode) {
        return gamemodeSkulls.get((Object)gameMode);
    }

    public static ItemStack getNumberedSkull(int index) {
        return numberedSkulls.get(index);
    }

    public static ItemStack getBackArrowSkull() {
        return backArrow;
    }

    public static ItemStack createSkull(String url, String displayName, List<String> lore) {
        ItemStack item = GuiUtil.createSkullItemStack();
        SkullMeta meta = (SkullMeta)item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(lore);
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        try {
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set((Object)meta, (Object)profile);
        }
        catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException ex) {
            ex.printStackTrace();
        }
        item.setItemMeta((ItemMeta)meta);
        return item;
    }

    public static ItemStack createSkull(Player skullOwner, String displayName, List<String> lore) {
        ItemStack item = GuiUtil.createSkullItemStack();
        SkullMeta meta = (SkullMeta)item.getItemMeta();
        try {
            meta.setOwningPlayer((OfflinePlayer)skullOwner);
        }
        catch (NoSuchMethodError e) {
            meta.setOwner(skullOwner.getName());
        }
        meta.setDisplayName(displayName);
        meta.setLore(lore);
        item.setItemMeta((ItemMeta)meta);
        return item;
    }

    private static ItemStack createSkullItemStack() {
    	return new ItemStack(Material.PLAYER_HEAD);
    }

    public static ItemStack getInvseeSeparator() {
        return invseeSeparator;
    }

    public static ItemStack getInvseeInfoItem() {
        return invseeInfoItem;
    }

    public static List<Integer> getInvseeUnusedSlots() {
        return invseeUnusedSlots;
    }
}

