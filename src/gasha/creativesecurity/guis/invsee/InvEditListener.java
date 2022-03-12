package gasha.creativesecurity.guis.invsee;

import gasha.creativesecurity.AdditionalInventory;
import gasha.creativesecurity.CreativeSecurityPlugin;
import gasha.creativesecurity.DataListener;
import gasha.creativesecurity.Message;
import gasha.creativesecurity.PlayerData;
import gasha.creativesecurity.guis.GuiConfig;
import gasha.creativesecurity.guis.GuiUtil;
import gasha.creativesecurity.guis.invsee.EditSessionWrapper;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class InvEditListener
implements Listener {
    private GuiConfig guiConfig;
    private DataListener dataListener;
    public static Map<String, EditSessionWrapper> editing = new HashMap<String, EditSessionWrapper>();

    public InvEditListener(GuiConfig guiConfig, DataListener dataListener) {
        this.guiConfig = guiConfig;
        this.dataListener = dataListener;
    }

    @EventHandler(priority=EventPriority.LOW)
    public void onInvClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        final Player player = (Player)event.getWhoClicked();
        if (event.getClickedInventory() == null) {
            return;
        }
        Inventory clickedInventory = event.getClickedInventory();
        String clickedInvTitle = event.getView().getTitle();
        
        if (clickedInvTitle.startsWith(GuiUtil.INVSEE_TITLE)) {
            if (!player.hasPermission("creativesecurity.bypass.invedit")) {
                event.setCancelled(true);
                return;
            }
            final int clickedSlot = event.getRawSlot();
            if (GuiUtil.getInvseeUnusedSlots().contains(clickedSlot)) {
                event.setCancelled(true);
                return;
            }
            final EditSessionWrapper editSessionWrapper = editing.get(player.getName());
            final OfflinePlayer target = editSessionWrapper.getTargetOfflinePlayer();
            Material cursorType = event.getCursor().getType();
            Material currentItemType = event.getCurrentItem().getType();
            if (!this.isAir(cursorType) && !this.isAir(currentItemType) || this.isAir(cursorType) && !this.isAir(currentItemType) || !this.isAir(cursorType) && this.isAir(currentItemType)) {
                final AdditionalInventory additionalInventory = editSessionWrapper.getAdditionalInventory();
                new BukkitRunnable(){

                    public void run() {
                        Inventory editedInv = player.getOpenInventory().getTopInventory();
                        if (clickedSlot <= 3 && clickedSlot >= 0) {
                            InvEditListener.this.saveArmorChanges(editedInv, additionalInventory);
                        } else if (clickedSlot == 5) {
                            InvEditListener.this.saveExtraContent(editedInv, additionalInventory);
                        } else if (clickedSlot >= 18) {
                            InvEditListener.this.saveStorageContent(editedInv, additionalInventory);
                        } else {
                            return;
                        }
                        if (target.isOnline()) {
                            GameMode currentGamemode;
                            Player targetPlayer = target.getPlayer();
                            PlayerData targetData = InvEditListener.this.dataListener.getData(targetPlayer);
                            int currentIndex = targetData.getCurrentIndex();
                            GameMode gameMode = currentGamemode = targetData.isCreativeMode() ? GameMode.CREATIVE : GameMode.SURVIVAL;
                            if (editSessionWrapper.getGameMode() == currentGamemode && editSessionWrapper.getInvNumber() == currentIndex) {
                                editSessionWrapper.getAdditionalInventory().apply(targetPlayer.getInventory());
                            }
                        } else {
                            PlayerData targetData = editing.get(player.getName()).getPlayerData();
                            InvEditListener.this.dataListener.saveDataForOfflinePlayer(target.getUniqueId(), target.getName(), targetData);
                        }
                    }
                }.runTaskLater((Plugin)CreativeSecurityPlugin.getInstance(), 2L);
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory == null) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player)event.getWhoClicked();
        String title = event.getView().getTitle();
        if (title.startsWith(GuiUtil.INVSEE_TITLE)) {
            Message.INVEDIT_NO_OPERATION.sendDenial((CommandSender)player, new String[0][]);
            event.setCancelled(true);
        }
    }

    private boolean isAir(Material mat) {
        return mat == Material.AIR;
    }

    private void saveArmorChanges(Inventory invseeInventory, AdditionalInventory additionalInventory) {
        ItemStack[] gui = invseeInventory.getContents();
        ItemStack[] armor = new ItemStack[4];
        IntStream.rangeClosed(0, 3).forEach(index -> {
            ItemStack processed = gui[index];
            if (processed == null || processed.getType() == Material.AIR) {
                processed = null;
            }
            armor[index] = processed;
        });
        additionalInventory.setArmorContents(armor);
    }

    private void saveExtraContent(Inventory invseeInventory, AdditionalInventory additionalInventory) {
        ItemStack[] extra = Arrays.copyOfRange(invseeInventory.getContents(), 5, 6);
        ItemStack[] extraList = (ItemStack[])Arrays.stream(extra).filter(Objects::nonNull).filter(is -> is.getType() != Material.AIR).toArray(ItemStack[]::new);
        additionalInventory.setExtraContents(extraList);
    }

    private void saveStorageContent(Inventory invseeInventory, AdditionalInventory additionalInventory) {
        ItemStack[] content = invseeInventory.getContents();
        ItemStack[] storage = new ItemStack[content.length - 18];
        for (int i = 18; i < content.length; ++i) {
            int loc = i - 18;
            loc = i >= 45 ? (loc -= 27) : (loc += 9);
            ItemStack processed = content[i];
            if (processed != null && processed.getType() == Material.AIR) {
                processed = null;
            }
            storage[loc] = processed;
        }
        additionalInventory.setStorageContents(storage);
    }
}

