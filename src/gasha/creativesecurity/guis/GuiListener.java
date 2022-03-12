package gasha.creativesecurity.guis;

import gasha.creativesecurity.CreativeSecurityPlugin;
import gasha.creativesecurity.DataListener;
import gasha.creativesecurity.Message;
import gasha.creativesecurity.guis.GuiUtil;
import gasha.creativesecurity.guis.inventories.GmSelectorGui;
import gasha.creativesecurity.guis.inventories.InventorySelectorGui;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.permissions.Permissible;

public class GuiListener
implements Listener {
    private CreativeSecurityPlugin main;
    private DataListener dataListener;

    public GuiListener(CreativeSecurityPlugin main, DataListener dataListener) {
        this.main = main;
        this.dataListener = dataListener;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player)event.getWhoClicked();
        if (event.getClickedInventory() == null) {
            return;
        }
        String title = event.getView().getTitle();
        if (title.equals(GuiUtil.GM_SELECTOR_TITLE)) {
            event.setCancelled(true);
            int clickedSlot = event.getRawSlot();
            switch (clickedSlot) {
                case 0: {
                    new InventorySelectorGui(player, "survival", this.dataListener).open();
                    break;
                }
                case 2: {
                    new InventorySelectorGui(player, "creative", this.dataListener).open();
                    break;
                }
                case 4: {
                    if (Message.hasPermission((Permissible)player, "creativesecurity.cmd.spectator", Message.NO_PERMISSION_SPECTATOR)) {
                        player.setGameMode(GameMode.SPECTATOR);
                        player.closeInventory();
                        Message.SPECTATOR_SWITCH.sendInfo((CommandSender)player);
                        break;
                    }
                    player.closeInventory();
                    break;
                }
                case 6: {
                    if (Message.hasPermission((Permissible)player, "creativesecurity.cmd.adventure", Message.NO_PERMISSION_ADVENTURE)) {
                        player.setGameMode(GameMode.ADVENTURE);
                        player.closeInventory();
                        Message.ADVENTURE_SWITCH.sendInfo((CommandSender)player);
                        break;
                    }
                    player.closeInventory();
                    break;
                }
            }
        } else if (title.equals(GuiUtil.CREATIVE_INV_SELECTOR_TITLE) || title.equals(GuiUtil.SURVIVAL_INV_SELECTOR_TITLE)) {
            event.setCancelled(true);
            if (event.getCurrentItem().getType() == Material.AIR) {
                return;
            }
            boolean creativeSelector = title.equals(GuiUtil.CREATIVE_INV_SELECTOR_TITLE);
            int clickedSlot = event.getRawSlot();
            if (clickedSlot == 17) {
                new GmSelectorGui(player, this.dataListener).open();
                return;
            }
            StringBuilder permission = new StringBuilder("creativesecurity.cmd.");
            if (creativeSelector) {
                permission.append("creative.gmc");
            } else {
                permission.append("survival.gms");
            }
            permission.append(clickedSlot + 1);
            if (!Message.hasPermission((Permissible)player, permission.toString(), Message.NO_PERMISSION_INVENTORY)) {
                player.closeInventory();
                return;
            }
            int result = this.dataListener.switchPlayerInventory(player, creativeSelector, clickedSlot);
            if (result == 2) {
                String message = ChatColor.translateAlternateColorCodes((char)'&', (String)this.main.messages.getString("keepinginventory"));
                if (!message.isEmpty()) {
                    player.sendMessage(message);
                }
            } else if (result == 1) {
                Message msg = creativeSelector ? Message.CREATIVE_SWITCH : Message.SURVIVAL_SWITCH;
                msg.sendInfo(player, new String[][]{{"number", Integer.toString(clickedSlot + 1)}});
            } else {
                Message.ERR_ALREADY.sendError((CommandSender)player);
                return;
            }
            player.closeInventory();
        }
    }
}

