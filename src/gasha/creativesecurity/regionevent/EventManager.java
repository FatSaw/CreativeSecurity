package gasha.creativesecurity.regionevent;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.RegionResultSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gasha.creativesecurity.CreativeSecurityPlugin;
import gasha.creativesecurity.consolefilter.ConsoleFilter;
import gasha.creativesecurity.regionevent.ActionBar;
import gasha.creativesecurity.regionevent.EventConfig;
import gasha.creativesecurity.regionevent.EventEntities;
import gasha.creativesecurity.regionevent.MessageUT;
import net.minecraft.server.v1_15_R1.Packet;
import net.minecraft.server.v1_15_R1.PacketPlayOutMount;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

public class EventManager
implements Listener {
    private static CreativeSecurityPlugin main;
    public static EventConfig eventconfig;
    private static EventConfig chatconfig;
    private static List<EventEntities> regionsLoaded;
    private static List<EventEntities> residenceLoaded;
    private static List<EventEntities> worldsLoaded;
    private static List<EventEntities> chatLoaded;
    private static HashMap<UUID, String> info;

    public EventManager() {
        main = CreativeSecurityPlugin.getInstance();
        ActionBar.setup();
        eventconfig = new EventConfig(main, "region_event");
        chatconfig = new EventConfig(main, "chat-event");
    }

    public static void reload() {
        EventEntities ee;
        worldsLoaded.clear();
        regionsLoaded.clear();
        chatLoaded.clear();
        eventconfig.reload();
        chatconfig.reload();
        ConfigurationSection configSection = EventManager.eventconfig.config.getConfigurationSection("worlds");
        for (String key : configSection.getKeys(false)) {
            ee = new EventEntities("world", key);
            ee.setEnter(eventconfig.getStrList("worlds." + key + ".onEnter"));
            ee.setLeave(eventconfig.getStrList("worlds." + key + ".onLeave"));
            worldsLoaded.add(ee);
        }
        configSection = EventManager.eventconfig.config.getConfigurationSection("regions");
        for (String key : configSection.getKeys(false)) {
            ee = new EventEntities("region", key);
            ee.setEnter(eventconfig.getStrList("regions." + key + ".onEnter"));
            ee.setLeave(eventconfig.getStrList("regions." + key + ".onLeave"));
            regionsLoaded.add(ee);
        }
        configSection = EventManager.eventconfig.config.getConfigurationSection("residence");
        for (String key : configSection.getKeys(false)) {
            ee = new EventEntities("residence", key);
            ee.setEnter(eventconfig.getStrList("residence." + key + ".onEnter"));
            ee.setLeave(eventconfig.getStrList("residence." + key + ".onLeave"));
            residenceLoaded.add(ee);
        }
        configSection = EventManager.chatconfig.config.getConfigurationSection("chats");
        for (String key : configSection.getKeys(false)) {
            ee = new EventEntities("chat", key);
            ee.setEnter(chatconfig.getStrList("chats." + key + ".onChat"));
            ee.setLeave(chatconfig.getStrList("chats." + key + ".words"));
            ee.setCased(chatconfig.getBool("chats." + key + ".case"));
            ee.setCancel(chatconfig.getBool("chats." + key + ".cancel"));
            ee.setContains(chatconfig.getBool("chats." + key + ".contains"));
            chatLoaded.add(ee);
        }
        configSection = EventManager.chatconfig.config.getConfigurationSection("replacements");
        for (String wordToReplace : configSection.getKeys(false)) {
            ee = new EventEntities("replacements", wordToReplace);
            ee.setContains(chatconfig.getBool("replacements." + wordToReplace + ".contains"));
            ee.setCaseInsensitive(chatconfig.getBool("replacements." + wordToReplace + ".case-insensitive"));
            String replacement = chatconfig.getStr("replacements." + wordToReplace + ".replacement");
            if (replacement.charAt(0) == '\\') {
                replacement = StringEscapeUtils.unescapeJava((String)replacement);
            }
            ee.setReplacement(replacement);
            chatLoaded.add(ee);
        }
        try {
            new BukkitRunnable(){

                public void run() {
                    for (Player host : Bukkit.getOnlinePlayers()) {
                        try {
                        	PacketPlayOutMount packet = new PacketPlayOutMount(((CraftEntity)host).getHandle());
                            if (host.getPassenger() != null) {
                                if (!(host.getPassenger() instanceof Player)) continue;
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    EventManager.sendPacket(p, packet);
                                }
                                continue;
                            }
                            EventManager.sendPacket(host, packet);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.runTaskTimer((Plugin)main, 0L, 20L);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void sendPacket(Player p, Packet<?> packet) {
        ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
    }

    public static ApplicableRegionSet getWGRegions(Location loc) {
        RegionManager manager = main.getWorldGuardHook().getRegionManager(loc.getWorld());
        try {
            BlockVector3 ve = BlockVector3.at(loc.getX(), loc.getY(), loc.getZ());
            return manager.getApplicableRegions(ve);
        }
        catch (NoClassDefFoundError post7Beta2version) {
            BlockVector3 blockVector = BlockVector3.at((double)loc.getX(), (double)loc.getY(), (double)loc.getZ());
            try {
                Method method = manager.getClass().getMethod("getApplicableRegions", BlockVector3.class);
                Object invokeValue = method.invoke((Object)manager, new Object[]{blockVector});
                return (ApplicableRegionSet)invokeValue;
            }
            catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
                main.getServer().getLogger().severe("Your WorldGuard region seems to be incompatible! Please, use a supported version");
                return new RegionResultSet(Collections.emptyList(), null);
            }
        }
    }

    private List<String> getRawWGRegions(Location loc) {
        ArrayList<String> result = new ArrayList<String>();
        EventManager.getWGRegions(loc).forEach(each -> result.add(each.getId()));
        return result;
    }

    public static String getResidenceRegion(Location loc) {
        ClaimedResidence claimedResidence = null;
        try {
            claimedResidence = Residence.getInstance().getResidenceManager().getByLoc(loc);
        }
        catch (NoClassDefFoundError noClassDefFoundError) {
            // empty catch block
        }
        return claimedResidence == null ? null : claimedResidence.getName();
    }

    public boolean isWithinRegion(Location loc, String region) {
        ApplicableRegionSet set = EventManager.getWGRegions(loc);
        for (ProtectedRegion each : set) {
            if (!each.getId().equalsIgnoreCase(region)) continue;
            return true;
        }
        return false;
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onCommand(ServerCommandEvent e) {
        String cmd = e.getCommand();
        if (cmd.equalsIgnoreCase("list") && ConsoleFilter.fc.getBool("prevent-list").booleanValue()) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onConsoleAndCommandBlockCommand(ServerCommandEvent event) {
        String command = event.getCommand().replace("/", "").toLowerCase();
        if (event.getSender() instanceof ConsoleCommandSender && this.isCommandBlocked(main.getConfig().getStringList("blocked-commands-console"), command)) {
            main.getLogger().info("This command is blocked!");
        } else if (!(event.getSender() instanceof BlockCommandSender) || !this.isCommandBlocked(main.getConfig().getStringList("blocked-commands-cmd-blocks"), command)) {
            return;
        }
        event.setCancelled(true);
    }

    private boolean isCommandBlocked(List<String> blockedCommandsList, String command) {
        return blockedCommandsList.stream().filter(blockedCmd -> blockedCmd.split(" ")[0].equals(command.split(" ")[0])).map(String::toLowerCase).anyMatch(command::startsWith);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String msg = event.getMessage();
        block0: for (EventEntities ee : chatLoaded) {
            if (ee.getType().equals("chat")) {
                if (!ee.isCased()) {
                    msg = msg.toLowerCase();
                }
                for (String target : ee.getLeave()) {
                    if (!ee.isCased()) {
                        target = target.toLowerCase();
                    }
                    if (ee.isContains()) {
                        if (!msg.contains(target)) continue;
                        if (ee.isCancel()) {
                            event.setCancelled(true);
                        }
                        EventManager.schedule(event.getPlayer(), ee.getEnter(), null);
                        continue block0;
                    }
                    if (!msg.equals(target)) continue;
                    if (ee.isCancel()) {
                        event.setCancelled(true);
                    }
                    EventManager.schedule(event.getPlayer(), ee.getEnter(), null);
                    continue block0;
                }
                continue;
            }
            if (!ee.getType().equals("replacements")) continue;
            String key = ee.getName();
            msg = event.getMessage();
            String finalMessage = event.getMessage();
            String enteredWord = "";
            boolean replace = false;
            if (ee.isCaseInsensitive()) {
                if (msg.equalsIgnoreCase(key) || ee.isContains() && msg.toLowerCase().contains(key.toLowerCase())) {
                    replace = true;
                    Pattern pattern = Pattern.compile("(?i)" + Pattern.quote(key));
                    Matcher matcher = pattern.matcher(msg);
                    while (matcher.find()) {
                        enteredWord = matcher.group();
                    }
                }
            } else if (msg.equals(key) || ee.isContains() && msg.contains(key)) {
                replace = true;
                enteredWord = key;
            }
            if (replace) {
                finalMessage = finalMessage.replaceAll("(?i)" + Pattern.quote(key), ee.getReplacement()).replace("{entered-word}", enteredWord);
            }
            if (finalMessage.isEmpty() || event.getMessage().equals(finalMessage)) continue;
            event.setMessage(finalMessage);
        }
    }

    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Team team = player.getScoreboard().getTeam(player.getName());
        if (team != null) {
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
            team.removePlayer((OfflinePlayer)player);
            team.unregister();
        }
        this.performeJoinRespawnChecks(player);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        main.getServer().getScheduler().runTaskLater((Plugin)main, () -> this.performeJoinRespawnChecks(event.getPlayer()), 5L);
    }

    private void performeJoinRespawnChecks(Player player) {
        String residenceName;
        Location location = player.getLocation();
        List<String> regions = this.getRawWGRegions(location);
        for (EventEntities ee : regionsLoaded) {
            if (!regions.contains(ee.getName())) continue;
            EventManager.schedule(player, ee.getEnter(), ee.getName());
            break;
        }
        if (main.isResidenceIntegrationLoaded() && (residenceName = EventManager.getResidenceRegion(location)) != null) {
            for (EventEntities ee : residenceLoaded) {
                if (!residenceName.equals(ee.getName())) continue;
                EventManager.schedule(player, ee.getEnter(), ee.getName());
                break;
            }
        }
    }

    @EventHandler
    public void teleport(PlayerTeleportEvent event) {
        if (!event.isCancelled() && event.getPlayer().getGameMode() != GameMode.SPECTATOR) {
            this.performeMoveChecks(event.getPlayer(), event.getFrom(), event.getTo());
        }
    }

    @EventHandler
    public void move(PlayerMoveEvent event) {
        ArrayList<Object> entities = new ArrayList<Object>();
        try {
            entities.addAll(event.getPlayer().getPassengers());
        }
        catch (NoSuchMethodError ex) {
            entities.add((Object)event.getPlayer().getPassenger());
        }
        entities.add((Object)event.getPlayer());
        entities.stream().filter(entity -> entity instanceof Player).map(entity -> (Player)entity).forEach(player -> this.performeMoveChecks((Player)player, event.getFrom(), event.getTo()));
    }

    @EventHandler
    public void vehicleMove(VehicleMoveEvent event) {
        Vehicle vehicle = event.getVehicle();
        if (vehicle.getPassenger() == null || !(vehicle.getPassenger() instanceof Player)) {
            return;
        }
        Player player = (Player)vehicle.getPassenger();
        if (player.hasPermission("creativesecurity.region.vehicle")) {
            this.performeMoveChecks(player, event.getFrom(), event.getTo());
        }
    }

    private void performeMoveChecks(Player player, Location locationFrom, Location locationTo) {
        int x = locationFrom.getBlockX();
        int y = locationFrom.getBlockY();
        int z = locationFrom.getBlockZ();
        int xx = locationTo.getBlockX();
        int yy = locationTo.getBlockY();
        int zz = locationTo.getBlockZ();
        if (x == xx && y == yy && z == zz) {
            return;
        }
        List<String> regionsFrom = this.getRawWGRegions(locationFrom);
        List<String> regionsTo = this.getRawWGRegions(locationTo);
        for (EventEntities loadedRegion : regionsLoaded) {
            if (regionsTo.contains(loadedRegion.getName())) {
                if (regionsFrom.contains(loadedRegion.getName())) continue;
                EventManager.schedule(player, loadedRegion.getEnter(), loadedRegion.getName());
                continue;
            }
            if (!regionsFrom.contains(loadedRegion.getName())) continue;
            EventManager.schedule(player, loadedRegion.getLeave(), loadedRegion.getName());
        }
        if (main.isResidenceIntegrationLoaded()) {
            String from = EventManager.getResidenceRegion(locationFrom);
            String to = EventManager.getResidenceRegion(locationTo);
            if (from == null && to == null || from == to) {
                return;
            }
            for (EventEntities loadedResidence : residenceLoaded) {
                String loadedResidenceName = loadedResidence.getName();
                if (loadedResidenceName.equals(from)) {
                    EventManager.schedule(player, loadedResidence.getLeave(), loadedResidenceName);
                    continue;
                }
                if (!loadedResidenceName.equals(to)) continue;
                EventManager.schedule(player, loadedResidence.getEnter(), loadedResidenceName);
            }
        }
    }

    @EventHandler
    public void onWorld(PlayerChangedWorldEvent e) {
        Player p = e.getPlayer();
        String world = p.getWorld().getName();
        for (EventEntities ee : worldsLoaded) {
            if (ee.getName().equals(world)) {
                EventManager.schedule(p, ee.getEnter(), null);
                continue;
            }
            if (!ee.getName().equals(e.getFrom().getName())) continue;
            EventManager.schedule(p, ee.getLeave(), null);
        }
    }

    public static void schedule(Player p, List<String> fcmds, String extra) {
        ArrayList<String> cmds = new ArrayList<String>(fcmds);
        for (String cmd : new ArrayList<String>(cmds)) {
            cmds.remove(cmd);
            if (cmd.contains("Wait")) {
                String waitStringValue = cmd.split(":")[1].replace("]", "");
                int wait = Integer.parseInt(waitStringValue);
                Bukkit.getScheduler().runTaskLater((Plugin)main, () -> EventManager.schedule(p, cmds, extra), (long)wait);
                return;
            }
            Bukkit.getScheduler().runTask((Plugin)main, () -> EventManager.process(p, cmd, extra));
        }
    }

    private static void process(final Player p, String cmd, String extra) {
        try {
            String identifier = cmd.split("]")[0].split(":")[0].replace("[", "");
            String type = cmd.split("]")[0].split(":")[1];
            String data = cmd.split("] ")[1].replace("%player%", p.getName());
            data = extra != null ? data.replace("%regionname%", extra) : data.replace("%worldname%", p.getWorld().getName());
            info.put(p.getUniqueId(), MessageUT.t(data));
            switch (identifier.toLowerCase()) {
                case "message": {
                    switch (type) {
                        case "player": {
                            MessageUT.plmessage(p, data);
                            return;
                        }
                        case "global": {
                            for (Player pl : Bukkit.getOnlinePlayers()) {
                                MessageUT.plmessage(pl, data);
                            }
                            return;
                        }
                    }
                }
                case "vanilla": {
                    switch (type) {
                        case "heal": {
                            if (p.getHealth() + (double)Integer.parseInt(data) >= p.getMaxHealth()) {
                                p.setHealth(p.getMaxHealth());
                            } else {
                                p.setHealth(p.getHealth() + (double)Integer.parseInt(data));
                            }
                            return;
                        }
                        case "damage": {
                            p.damage((double)Integer.parseInt(data));
                            return;
                        }
                        case "potion": {
                            String potionname = data.split(" ")[0].toUpperCase();
                            int amplifier = Integer.parseInt(data.split(" ")[1]);
                            int duration = Integer.parseInt(data.split(" ")[2]);
                            PotionEffect pe = new PotionEffect(PotionEffectType.getByName((String)potionname), duration, amplifier);
                            p.addPotionEffect(pe, true);
                            return;
                        }
                        case "removepotion": {
                            if (data.equalsIgnoreCase("all")) {
                                for (PotionEffect pe2 : p.getActivePotionEffects()) {
                                    p.removePotionEffect(pe2.getType());
                                }
                                return;
                            }
                            String potionname2 = data.split(" ")[0].toUpperCase();
                            if (p.hasPotionEffect(PotionEffectType.getByName((String)potionname2))) {
                                p.removePotionEffect(PotionEffectType.getByName((String)potionname2));
                            }
                            return;
                        }
                    }
                }
                case "command": {
                    switch (type) {
                        case "console": {
                            Bukkit.dispatchCommand((CommandSender)Bukkit.getServer().getConsoleSender(), (String)data);
                            return;
                        }
                        case "player": {
                            Bukkit.dispatchCommand((CommandSender)p, (String)data);
                            return;
                        }
                        case "opplayer": {
                            if (p.isOp()) {
                                Bukkit.dispatchCommand((CommandSender)p, (String)data);
                            } else {
                                p.setOp(true);
                                new BukkitRunnable(){

                                    public void run() {
                                        Bukkit.dispatchCommand((CommandSender)p, (String)((String)info.get(p.getUniqueId())));
                                        Bukkit.getScheduler().runTaskLater((Plugin)main, () -> p.setOp(false), 1L);
                                    }
                                }.runTaskLater((Plugin)main, 1L);
                            }
                            return;
                        }
                    }
                }
                case "titlesubtitle": {
                    switch (type) {
                        case "player": {
                            MessageUT.plmessage(p, "<title>" + data.replace("<>", "<subtitle>"));
                            return;
                        }
                        case "global": {
                            for (Player pl : Bukkit.getOnlinePlayers()) {
                                MessageUT.plmessage(pl, "<title>" + data.replace("<>", "<subtitle>"));
                            }
                            return;
                        }
                    }
                }
                case "actionbar": {
                    switch (type) {
                        case "player": {
                            MessageUT.plmessage(p, "<action>" + data);
                            return;
                        }
                        case "global": {
                            for (Player pl : Bukkit.getOnlinePlayers()) {
                                MessageUT.plmessage(pl, "<action>" + data);
                            }
                            return;
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
            if (cmd == null || cmd.length() < 1) {
                return;
            }
            ex.printStackTrace();
            MessageUT.debug("Region Event: " + cmd + " Is Invalid Event");
        }
    }

    static {
        eventconfig = null;
        chatconfig = null;
        regionsLoaded = new ArrayList<EventEntities>();
        residenceLoaded = new ArrayList<EventEntities>();
        worldsLoaded = new ArrayList<EventEntities>();
        chatLoaded = new ArrayList<EventEntities>();
        info = new HashMap();
    }
}

