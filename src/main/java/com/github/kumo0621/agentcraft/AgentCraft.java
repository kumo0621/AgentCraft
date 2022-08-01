package com.github.kumo0621.agentcraft;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class AgentCraft extends JavaPlugin implements org.bukkit.event.Listener {
    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);
        new BukkitRunnable() {
            @Override
            public void run() {
                //何かやりたいときはここに書き込む

                if (tick) {


                    for (ArmorStand entity : map.values()) {
                        Location loc = entity.getLocation();
                        loc.setX(loc.getX() + 1);
                        entity.teleport(loc);


                    }
                }
            }
        }.runTaskTimer(this, 0L, 0L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onPlayerchat(AsyncPlayerChatEvent e) {
        Bukkit.getScheduler().runTask(this, new Runnable() {
            @Override
            public void run() {
                Player player = e.getPlayer();
                Team team = player.getScoreboard().getEntryTeam(player.getName());
                ArmorStand entity = map.get(team);
                String chat;
                chat = e.getMessage();
                System.out.println(chat);
                if (entity != null) {
                    Location loc = entity.getLocation();
                    switch (chat) {
                        case "上":
                            loc.setY(loc.getY() + 1);
                            break;
                        case "下":
                            loc.setY(loc.getY() - 1);
                            break;
                        case "前":
                            loc.setX(loc.getX() + 1);
                            break;
                        case "後ろ":
                            loc.setX(loc.getX() - 1);
                            break;
                        case "右":
                            loc.setZ(loc.getZ() + 1);
                            break;
                        case "左":
                            loc.setZ(loc.getZ() - 1);
                            break;
                    }
                    entity.teleport(loc);
                }
            }
        });
    }


    boolean tick;
    Map<Team, ArmorStand> map = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equals("agentcraft")) {
            if (sender instanceof Player) {
                if (args.length == 0) {
                    sender.sendMessage("引数を指定してください。");
                } else if (args[0].equals("time")) {
                    sender.sendMessage("テスト");
                } else if (args[0].equals("summon")) {
                    Player player = (Player) sender;


                    Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
                    map.clear();
                    for (Player onlinePlayer : onlinePlayers) {
                        Location location = onlinePlayer.getLocation();
                        Team team = onlinePlayer.getScoreboard().getEntryTeam(onlinePlayer.getName());
                        if (team != null && !map.containsKey(team)) {
                            @NotNull ArmorStand entity = location.getWorld().spawn(location, ArmorStand.class);
                            entity.setGravity(false);
                            map.put(team, entity);
                            sender.sendMessage("アーマースタンドを召喚しました。");
                        } else sender.sendMessage("チームが存在しません");

                    }

                } else if (args[0].equals("start")) {
                    tick = true;
                    System.out.println(tick);
                } else if (args[0].equals("end")) {
                    tick = false;
                    System.out.println(tick);
                } else {
                    sender.sendMessage("不明なコマンドです。");
                }
            }
        }
        return super.onCommand(sender, command, label, args);
    }
}