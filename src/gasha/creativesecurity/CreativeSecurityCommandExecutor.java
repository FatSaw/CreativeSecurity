package gasha.creativesecurity;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import gasha.creativesecurity.AdditionalInventory;
import gasha.creativesecurity.BlockPosition;
import gasha.creativesecurity.CreativeListener;
import gasha.creativesecurity.CreativeSecurityPlugin;
import gasha.creativesecurity.DataListener;
import gasha.creativesecurity.Message;
import gasha.creativesecurity.PlayerData;
import gasha.creativesecurity.RegionData;
import gasha.creativesecurity.guis.GuiUtil;
import gasha.creativesecurity.guis.invsee.EditSessionWrapper;
import gasha.creativesecurity.guis.invsee.InvEditListener;
import gasha.creativesecurity.guis.invsee.InvseeGui;
import gasha.creativesecurity.regionevent.EventManager;
import gasha.creativesecurity.regionevent.MessageUT;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;

public class CreativeSecurityCommandExecutor
implements CommandExecutor {
    private CreativeSecurityPlugin main;
    private CreativeListener creativeListener;
    private DataListener dataListener;
    private Map<Integer, List<Object>> helpMenu;
    private List<String> helpMenuCooldown;
    private Random random;
    private int maxCmdBlockRadius;

    CreativeSecurityCommandExecutor(CreativeSecurityPlugin main, CreativeListener creativeListener, DataListener dataListener) {
        this.main = main;
        this.creativeListener = creativeListener;
        this.dataListener = dataListener;
        this.helpMenuCooldown = new ArrayList<String>();
        this.random = new Random();
    }

    void reload() {
        this.maxCmdBlockRadius = this.main.getConfig().getInt("cmdblock-max-radius");
        this.helpMenu = new HashMap<Integer, List<Object>>();
        List<String> header = MessageUT.t(this.main.getConfig().getStringList("helpmenu.header"));
        List<String> footer = MessageUT.t(this.main.getConfig().getStringList("helpmenu.footer"));
        String previousStr = MessageUT.t(this.main.getConfig().getString("helpmenu.previous-page"));
        String nextStr = MessageUT.t(this.main.getConfig().getString("helpmenu.next-page"));
        String separator = MessageUT.t(this.main.getConfig().getString("helpmenu.buttons-spacer"));
        Set<String> pagesIds = this.main.getConfig().getConfigurationSection("helpmenu.pages").getKeys(false);
        int pagesAmount = pagesIds.stream().mapToInt(Integer::valueOf).max().getAsInt();
        for (String key : pagesIds) {
            ComponentBuilder builder;
            int pageNumber = Integer.valueOf(key);
            ArrayList<Object> page = new ArrayList<Object>(header);
            page.addAll(MessageUT.t(this.main.getConfig().getStringList("helpmenu.pages." + key)));
            if (pageNumber == 1) {
                builder = new ComponentBuilder(String.format("%1$" + previousStr.length() + "s", ""));
            } else {
                builder = new ComponentBuilder(previousStr);
                builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/creativesecurity help " + (pageNumber - 1)));
            }
            builder.append(separator.replace("{current}", key).replace("{total}", String.valueOf(pagesAmount)));
            builder.event(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, ""));
            if (pageNumber == pagesAmount) {
                builder.append(String.format("%1$" + nextStr.length() + "s", ""));
            } else {
                builder.append(nextStr);
                builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/creativesecurity help " + (pageNumber + 1)));
            }
            BaseComponent[] clickableMessage = builder.create();
            page.add(clickableMessage);
            page.addAll(footer);
            this.helpMenu.put(pageNumber, new ArrayList<Object>(page));
        }
    }

    private boolean isAPlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            Message.PLAYER_ONLY_CMD.sendInfo(sender);
            return false;
        }
        return true;
    }

    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (alias.equalsIgnoreCase("creativesecurity")) {
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("cmdblock")) {
                    if (!this.isAPlayer(sender)) {
                        return false;
                    }
                    Player player = (Player)sender;
                    if (!Message.hasPermission((Permissible)player, "creativesecurity.cmd.cmdblock")) {
                        return false;
                    }
                    if (args.length < 3) {
                        Message.CMDBLOCK_USAGE.sendInfo((CommandSender)player);
                        return false;
                    }
                    String radiusInput = args[1];
                    if (!radiusInput.matches("^(?:[1-9]|\\d\\d\\d*)$")) {
                        Message.CMDBLOCK_INVALID_RADIUS.sendDenial((CommandSender)player, new String[][]{{"input", radiusInput}});
                        return false;
                    }
                    int radius = Integer.valueOf(radiusInput);
                    if (radius > this.maxCmdBlockRadius) {
                        Message.CMDBLOCK_TOOBIG_RADIUS.sendDenial((CommandSender)player, new String[][]{{"max", String.valueOf(this.maxCmdBlockRadius)}});
                        return false;
                    }
                    Location radiusCenter = player.getLocation();
                    String radiusWorld = radiusCenter.getWorld().getName();
                    String playerName2 = player.getName();
                    List<Player> nearPlayers = this.main.getServer().getOnlinePlayers().stream().filter(Objects::nonNull).map(p -> p).filter(onlinePlayer -> !onlinePlayer.getName().equals(playerName2)).filter(onlinePlayer -> onlinePlayer.getWorld().getName().equals(radiusWorld)).filter(onlinePlayer -> onlinePlayer.getLocation().distance(radiusCenter) <= (double)radius).collect(Collectors.toList());
                    if (nearPlayers.isEmpty()) {
                        Message.CMDBLOCK_NO_TARGETS.sendInfo((CommandSender)player);
                    } else {
                        CharSequence[] cmdInput = Arrays.copyOfRange(args, 2, args.length);
                        String rawCmd = String.join((CharSequence)" ", cmdInput);
                        List<String> targetsNames = nearPlayers.stream().map(OfflinePlayer::getName).collect(Collectors.toList());
                        String replace = "";
                        if (rawCmd.contains("@a")) {
                            replace = "@a";
                        } else if (rawCmd.contains("@p")) {
                            replace = "@p";
                            Map<Player,Double> distances = nearPlayers.stream().collect(Collectors.toMap(Function.identity(), p -> p.getLocation().distance(radiusCenter)));
                            Double minDistance = distances.values().stream().mapToDouble(Double::doubleValue).min().getAsDouble();
                            Player nearestPlayer = distances.entrySet().stream().filter(entry -> Objects.equals(entry.getValue(), minDistance)).map(Map.Entry::getKey).findFirst().get();
                            targetsNames = Collections.singletonList(nearestPlayer.getName());
                        } else if (rawCmd.contains("@r")) {
                            replace = "@r";
                            int rnd = this.random.nextInt(nearPlayers.size());
                            targetsNames = Collections.singletonList(((Player)nearPlayers.get(rnd)).getName());
                        }
                        if (replace.isEmpty()) {
                            Message.CMDBLOCK_INVALID_CMD.sendError((CommandSender)player);
                        } else {
                            ArrayList<String> commands = new ArrayList<String>();
                            String finalReplace = replace;
                            targetsNames.forEach(target -> commands.add(rawCmd.replace(finalReplace, (CharSequence)target)));
                            commands.forEach(((Player)player)::performCommand);
                            Message.CMDBLOCK_SUCCESS.sendInfo((CommandSender)player, new String[][]{{"amount", String.valueOf(commands.size())}});
                        }
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("clear")) {
                    if (!this.isAPlayer(sender)) {
                        return false;
                    }
                    Player player = (Player)sender;
                    if (args.length == 1) {
                        if (!Message.hasPermission((Permissible)player, "creativesecurity.cmd.clear.own")) {
                            return false;
                        }
                        PlayerData playerData = this.dataListener.getData(player);
                        AdditionalInventory currentInventory = playerData.getCurrent();
                        currentInventory.clear();
                        currentInventory.apply(player.getInventory());
                        Message.CLEAR_SUCCESS_OWN.sendInfo((CommandSender)player, new String[]{"gamemode", player.getGameMode().name().toLowerCase()}, new String[]{"index", String.valueOf(playerData.getCurrentIndex() + 1)});
                    } else if (args.length == 2 || args.length == 4) {
                        int inventoryNumber;
                        GameMode gameMode;
                        if (!Message.hasPermission((Permissible)player, "creativesecurity.cmd.clear.others")) {
                            return false;
                        }
                        String targetName = args[1];
                        OfflinePlayer target2 = Bukkit.getOfflinePlayer(targetName);
                        Player targetPlayer = target2.getPlayer();
                        if (targetName.equals(player.getName())) {
                            Message.CLEAR_YOURSELF.sendDenial((CommandSender)player, new String[0][]);
                            return false;
                        }
                        Optional<PlayerData> optionalTargetData = target2.isOnline() ? Optional.of(this.dataListener.getData(targetPlayer)) : this.dataListener.loadPlayerData(target2.getUniqueId(), ((OfflinePlayer)target2)::getName);
                        if (!optionalTargetData.isPresent()) {
                            Message.TARGET_NEVER_JOINED.sendDenial((CommandSender)player, new String[0][]);
                            return false;
                        }
                        PlayerData targetData = optionalTargetData.get();
                        if (args.length == 2) {
                            gameMode = targetData.isCreativeMode() ? GameMode.CREATIVE : GameMode.SURVIVAL;
                            inventoryNumber = targetData.getCurrentIndex();
                        } else {
                            String inputGamemode = args[2].toUpperCase();
                            if (!this.validGamemode(inputGamemode, player)) {
                                return false;
                            }
                            gameMode = GameMode.valueOf((String)inputGamemode);
                            if (!this.validInventoryNumber(args[3], player)) {
                                return false;
                            }
                            inventoryNumber = Integer.valueOf(args[3]) - 1;
                        }
                        AdditionalInventory targetInventory = targetData.getAdditionalInventory(gameMode, inventoryNumber);
                        if (targetInventory == null) {
                            Message.CLEAR_TARGET_INVALID_GAMEMODE.sendDenial((CommandSender)player, new String[0][]);
                            return false;
                        }
                        targetInventory.clear();
                        String finalGamemode = gameMode.name().toLowerCase();
                        String finalInventoryNumber = String.valueOf(inventoryNumber + 1);
                        if (target2.isOnline()) {
                            if (args.length == 2 || inventoryNumber == targetData.getCurrentIndex() && gameMode == targetPlayer.getGameMode()) {
                                targetInventory.apply(targetPlayer.getInventory());
                            }
                            Message.CLEAR_SUCCESS_BY_OTHERS.sendInfo((CommandSender)targetPlayer, new String[]{"executor", player.getName()}, new String[]{"gamemode", finalGamemode}, new String[]{"index", finalInventoryNumber});
                        } else {
                            this.dataListener.saveDataForOfflinePlayer(target2.getUniqueId(), targetName, targetData);
                        }
                        Message.CLEAR_SUCCESS_OTHERS.sendInfo((CommandSender)player, new String[]{"target", targetName}, new String[]{"gamemode", finalGamemode}, new String[]{"index", finalInventoryNumber});
                    } else {
                        Message.CLEAR_USAGE.sendInfo((CommandSender)player);
                    }
                    return true;
                }
            }
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    if (sender instanceof Player && !Message.hasPermission((Permissible)((Player)sender), "creativesecurity.cmd.reload")) {
                        return false;
                    }
                    this.main.getServer().getWorlds().parallelStream().flatMap(world -> Arrays.stream(world.getLoadedChunks())).forEach(this.creativeListener::chunkUnloaded);
                    this.main.saveDefaultConfig();
                    this.main.reloadConfig();
                    EventManager.reload();
                    this.main.getServer().getWorlds().parallelStream().flatMap(world -> Arrays.stream(world.getLoadedChunks())).forEach(chunk -> this.creativeListener.chunkLoaded((Chunk)chunk, false));
                    sender.sendMessage(Message.RELOAD_SUCCESS.applyArgs(new StringBuilder(), new String[0][]).toString());
                } else if (args[0].equalsIgnoreCase("version")) {
                    Message.VERSION_INFO.sendInfo(sender, new String[]{"cs_ver", this.main.getDescription().getVersion()}, new String[]{"server_ver", Bukkit.getVersion()});
                } else if (args[0].equalsIgnoreCase("unmarkblocks")) {
                    if (!this.isAPlayer(sender)) {
                        return false;
                    }
                    Player player = (Player)sender;
                    if (!Message.hasPermission((Permissible)player, "creativesecurity.cmd.unmarkblocks")) {
                        return false;
                    }
                    if (!this.main.worldEditIntegrationLoaded()) {
                        Message.WORLD_EDIT_NOT_LOADED.sendInfo((CommandSender)player);
                        return false;
                    }
                    Plugin fawePlugin = this.main.getServer().getPluginManager().getPlugin("FastAsyncWorldEdit");
                    if (fawePlugin == null || !fawePlugin.isEnabled()) {
                        Message.FAWE_NOT_LOADED.sendDenial(player, new String[0][]);
                        return false;
                    }
                    Region selection = WorldEditPlugin.getInstance().getSession(player).getSelection();
                    //FawePlayer fawePlayer = FaweAPI.wrapPlayer(player);
                    //Region selection = fawePlayer.getSelection();
                    if (selection == null) {
                        Message.WORLD_EDIT_NO_SELECTION.sendInfo(player);
                        return false;
                    }
                    World selectionWorld = Bukkit.getWorld(selection.getWorld().getName());
                    int unmarkedAmount = 0;
                    for (BlockVector3 blockVector : selection) {
                        Block block = selectionWorld.getBlockAt(blockVector.getBlockX(), blockVector.getBlockY(), blockVector.getBlockZ());
                        RegionData regionData = CreativeListener.getRegionData(block);
                        if (regionData == null) {
                            Message.ERR_CHUNK_DATA_NOT_LOADED.sendError((CommandSender)player);
                            break;
                        }
                        BlockPosition blockPosition = new BlockPosition(block);
                        if (!regionData.isCreative(blockPosition)) continue;
                        regionData.unmark(blockPosition);
                        ++unmarkedAmount;
                    }
                    if (unmarkedAmount > 0) {
                        Message.WORLD_EDIT_UNMARKED_SUCCESS.sendInfo((CommandSender)player, new String[][]{{"amount", String.valueOf(unmarkedAmount)}});
                    } else {
                        Message.WORLD_EDIT_NO_MARKED.sendInfo((CommandSender)player);
                    }
                } else if (args[0].equalsIgnoreCase("invsee")) {
                    Message.INVSEE_USAGE.sendInfo(sender);
                } else if (args[0].equalsIgnoreCase("status")) {
                    Message.STATUS_USAGE.sendInfo(sender);
                } else {
                    this.sendHelpMenu(sender);
                }
                return true;
            }
            if (args.length >= 2) {
                if (args[0].equalsIgnoreCase("help") && args.length == 2) {
                    String senderName = sender.getName();
                    if (this.helpMenuCooldown.contains(senderName)) {
                        return false;
                    }
                    String pageNumberInput = args[1];
                    try {
                        this.sendHelpMenu(sender, Integer.valueOf(pageNumberInput));
                        this.helpMenuCooldown.add(senderName);
                        this.main.getServer().getScheduler().runTaskLater((Plugin)this.main, () -> this.helpMenuCooldown.remove(senderName), 5L);
                    }
                    catch (NumberFormatException ex) {
                        Message.INVALID_HELP_PAGE_MENU.sendError(sender);
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("invsee")) {
                    Optional<PlayerData> optionalTargetData;
                    Optional<Integer> inventoryNumberOptional;
                    Optional<GameMode> gameModeOptional;
                    if (!this.isAPlayer(sender)) {
                        return false;
                    }
                    Player player = (Player)sender;
                    if (!Message.hasPermission((Permissible)player, "creativesecurity.cmd.invsee")) {
                        return false;
                    }
                    String targetName = args[1];
                    if (targetName.equals(player.getName())) {
                        Message.INVSEE_YOURSELF.sendDenial((CommandSender)player, new String[0][]);
                        return false;
                    }
                    OfflinePlayer targetOfflinePlayer = Bukkit.getOfflinePlayer((String)targetName);
                    if (targetOfflinePlayer == null) {
                        Message.TARGET_NEVER_JOINED.sendDenial((CommandSender)player, new String[0][]);
                        return false;
                    }
                    Player targetOnlinePlayer = targetOfflinePlayer.getPlayer();
                    if (targetOnlinePlayer != null && targetOnlinePlayer.hasPermission("creativesecurity.bypass.cmd.invsee")) {
                        Message.INVSEE_TARGET_BYPASS.sendError((CommandSender)player);
                        return false;
                    }
                    if (args.length == 4) {
                        String chatInputGamemode = args[2].toUpperCase();
                        if (!this.validGamemode(chatInputGamemode, player)) {
                            return false;
                        }
                        gameModeOptional = Optional.of(GameMode.valueOf((String)chatInputGamemode));
                        if (!this.validInventoryNumber(args[3], player)) {
                            return false;
                        }
                        inventoryNumberOptional = Optional.of(Integer.valueOf(args[3]) - 1);
                    } else {
                        gameModeOptional = Optional.empty();
                        inventoryNumberOptional = Optional.empty();
                    }
                    if (targetOfflinePlayer.isOnline()) {
                        optionalTargetData = Optional.of(this.dataListener.getData(targetOnlinePlayer));
                    } else {
                        if (!gameModeOptional.isPresent()) {
                            Message.INVSEE_TARGET_OFFLINE.sendDenial((CommandSender)player, new String[0][]);
                            return false;
                        }
                        optionalTargetData = this.dataListener.loadPlayerData(targetOfflinePlayer.getUniqueId(), ((OfflinePlayer)targetOfflinePlayer)::getName);
                    }
                    if (!optionalTargetData.isPresent()) {
                        Message.TARGET_NEVER_JOINED.sendDenial((CommandSender)player, new String[0][]);
                        return false;
                    }
                    PlayerData targetData = optionalTargetData.get();
                    GameMode gameMode = gameModeOptional.orElseGet(() -> targetData.isCreativeMode() ? GameMode.CREATIVE : GameMode.SURVIVAL);
                    int inventoryNumber = inventoryNumberOptional.orElseGet(targetData::getCurrentIndex);
                    GameMode currentGamemode = targetData.isCreativeMode() ? GameMode.CREATIVE : GameMode.SURVIVAL;
                    int currentIndex = targetData.getCurrentIndex();
                    boolean currentlyUsedInv = targetOfflinePlayer.isOnline() && gameMode == currentGamemode && inventoryNumber == currentIndex;
                    AdditionalInventory selectedInventory = currentlyUsedInv ? new AdditionalInventory(targetOfflinePlayer.getPlayer()) : targetData.getAdditionalInventory(gameMode, inventoryNumber);
                    String title = GuiUtil.INVSEE_TITLE + GuiUtil.INVSEE_TITLE1.replace("{player}", targetName).replace("{gamemode}", PlayerData.getName(gameMode)).replace("{inv_number}", String.valueOf(inventoryNumber + 1));
                    new InvseeGui(this.main, selectedInventory, targetOfflinePlayer, inventoryNumber + 1, title).open(player);
                    InvEditListener.editing.put(player.getName(), new EditSessionWrapper(targetData, selectedInventory, inventoryNumber, gameMode, targetOfflinePlayer));
                } else if (args[0].equalsIgnoreCase("status")) {
                    if (args.length == 2) {
                        Optional<PlayerData> optionalTargetData;
                        if (!this.isAPlayer(sender)) {
                            return false;
                        }
                        Player player = (Player)sender;
                        if (!Message.hasPermission((Permissible)player, "creativesecurity.cmd.status")) {
                            return false;
                        }
                        String targetName = args[1];
                        if (targetName.equals(player.getName())) {
                            Message.STATUS_YOURSELF.sendDenial((CommandSender)player, new String[0][]);
                            return false;
                        }
                        OfflinePlayer target3 = Bukkit.getOfflinePlayer(targetName);
                        if (target3.isOnline()) {
                            Player targetPlayer = target3.getPlayer();
                            optionalTargetData = Optional.of(this.dataListener.getData(targetPlayer));
                        } else {
                            optionalTargetData = this.dataListener.loadPlayerData(target3.getUniqueId(), ((OfflinePlayer)target3)::getName);
                        }
                        if (!optionalTargetData.isPresent()) {
                            Message.TARGET_NEVER_JOINED.sendDenial((CommandSender)player, new String[0][]);
                            return false;
                        }
                        PlayerData targetData = optionalTargetData.get();
                        int currentIndex = targetData.getCurrentIndex();
                        GameMode currentGamemode = targetData.isCreativeMode() ? GameMode.CREATIVE : GameMode.SURVIVAL;
                        Message msg = target3.isOnline() ? Message.STATUS_INFO : Message.STATUS_INFO_OFFLINE;
                        msg.sendInfo((CommandSender)player, new String[]{"target", targetName}, new String[]{"gamemode", PlayerData.getName(currentGamemode)}, new String[]{"inv_number", String.valueOf(currentIndex + 1)});
                    } else {
                        Message.STATUS_USAGE.sendInfo(sender);
                    }
                } else {
                    this.sendHelpMenu(sender);
                }
                return true;
            }
            this.sendHelpMenu(sender);
            return true;
        }
        return false;
    }

    private void sendHelpMenu(CommandSender commandSender, int pageNumber) {
        if (!this.helpMenu.containsKey(pageNumber)) {
            Message.INVALID_HELP_PAGE_MENU.sendDenial(commandSender, new String[0][]);
        } else {
            this.helpMenu.get(pageNumber).forEach(line -> {
                if (line instanceof String) {
                    commandSender.sendMessage((String)line);
                } else if (line instanceof BaseComponent[] && commandSender instanceof Player) {
                    Player player = (Player)commandSender;
                    try {
                        player.spigot().sendMessage((BaseComponent[])line);
                    }
                    catch (NoSuchMethodError noSuchMethodError) {
                        // empty catch block
                    }
                }
            });
        }
    }

    private void sendHelpMenu(CommandSender commandSender) {
        this.sendHelpMenu(commandSender, 1);
    }

    private boolean validGamemode(String userInput, Player player) {
        Consumer<Player> sendInvalidGamemodeMessage = pl -> Message.INVALID_GAMEMODE.sendDenial((CommandSender)pl, new String[0][]);
        try {
            GameMode inputGamemode = GameMode.valueOf((String)userInput);
            if (inputGamemode != GameMode.CREATIVE && inputGamemode != GameMode.SURVIVAL) {
                sendInvalidGamemodeMessage.accept(player);
                return false;
            }
            return true;
        }
        catch (IllegalArgumentException ex) {
            sendInvalidGamemodeMessage.accept(player);
            return false;
        }
    }

    private boolean validInventoryNumber(String userInput, Player player) {
        int inputNumber;
        Consumer<Player> sendInvalidNumberMessage = pl -> Message.INVALID_INVENTORY_NUMBER.sendDenial((CommandSender)pl, new String[0][]);
        try {
            inputNumber = Integer.valueOf(userInput);
        }
        catch (NumberFormatException ex) {
            sendInvalidNumberMessage.accept(player);
            return false;
        }
        if (inputNumber < 1 || inputNumber > 10) {
            sendInvalidNumberMessage.accept(player);
            return false;
        }
        return true;
    }

    private void ride(Player player, Player target) {
        player.setPassenger((Entity)target);
    }

    private Optional<String> getDistance(Location location1, Location location2) {
        try {
            return Optional.of(String.format("%.2f", location1.distance(location2)));
        }
        catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    private void send(Player p, String msg) {
        p.sendMessage(ChatColor.translateAlternateColorCodes((char)'&', (String)msg));
    }
}

