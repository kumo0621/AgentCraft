package com.github.kumo0621.agentcraft;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.EulerAngle;
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
            int time = 0;

            @Override

            public void run() {
                //何かやりたいときはここに書き込む

                if (tick) {
                    double angle = Math.sin(Math.toRadians(time));

                    for (ArmorStand entity : map.values()) {
                        entity.setLeftLegPose(makeAngle(angle * 33f, 0f, 0f));
                        entity.setRightLegPose(makeAngle(angle * -33, 0f, 0f));
                        entity.setLeftArmPose(makeAngle(angle * -26, 0f, 0f));
                        entity.setRightArmPose(makeAngle(angle * 26f, 0f, 0f));
                    }
                    time += 10;
                }

            }
        }.

                runTaskTimer(this, 0L, 0L);

    }

    @NotNull
    private EulerAngle makeAngle(double x, double y, double z) {
        return new EulerAngle(Math.toRadians(x), Math.toRadians(y), Math.toRadians(z));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onPlayerchat(AsyncPlayerChatEvent e) {
        Bukkit.getScheduler().runTask(this, () -> {
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

                        Location own = moveFrontLocation(loc.clone(), 90);
                        if ((own.getBlock().getType().equals(Material.AIR))) {
                            moveFrontLocation(loc, 90);
                        } else {
                            Location previous = loc.clone();
                            Location vec = own.clone().subtract(previous);
                            vec.multiply(0.3);
                            vec.add(previous);
                            loc.zero().add(vec);
                            Bukkit.getScheduler().runTaskLater(this, () -> {
                                entity.teleport(previous);

                            }, 5);

                        }
                        break;
                    case "後ろ":
                        Location own2 = moveFrontLocation(loc.clone(), 270);
                        if ((own2.getBlock().getType().equals(Material.AIR))) {
                            moveFrontLocation(loc, 270);
                        } else {
                            Location previous = loc.clone();
                            Location vec = own2.clone().subtract(previous);
                            vec.multiply(0.3);
                            vec.add(previous);
                            loc.zero().add(vec);
                            Bukkit.getScheduler().runTaskLater(this, () -> {
                                entity.teleport(previous);

                            }, 5);

                        }
                        break;
                    case "左":
                        loc.setYaw(loc.getYaw() - 90);
                        break;
                    case "右":
                        loc.setYaw(loc.getYaw() + 90);
                        break;
                    case "壊す":
                        Location abc = moveFrontLocation(loc.clone(), 90);
                        if (abc.getBlock().getType().equals(Material.GRASS_BLOCK)) {
                            new BukkitRunnable() {
                                int time = 0;

                                @Override
                                public void run() {
                                    //何かやりたいときはここに書き込む
                                    time++;
                                    int hand = 0;
                                    if (time > 200) {
                                        abc.getBlock().setType(Material.AIR);
                                        abc.getWorld().playSound(abc, Sound.BLOCK_GRASS_BREAK, 1, 1);
                                        sendBlockDamage(abc, 0f);
                                        cancel();
                                    } else {
                                        //壊すとき腕振る処理途中
                                        sendBlockDamage(abc, time / 200.f);
                                        double angle = (Math.toRadians(time));
                                        for (ArmorStand entity : map.values()) {
                                            entity.setRightArmPose(makeAngle(angle * 100, 0f, 0f));
                                        }
                                        time += 10;
                                    }

                                }


                            }.runTaskTimer(this, 0L, 0L);
                        }
                        break;
                    default:
                        break;

                }
                entity.teleport(loc);
            }
        });
    }

    //ブロックの破壊エフェクト
    private void sendBlockDamage(Location location, float progress) {
        for (Player player : location.getNearbyPlayers(30)) {
            player.sendBlockDamage(location, progress);
        }
    }

    //関数化でやりたいこと
    //YAWの角度を与えたら新しい座標が返ってくる関数を用意する。
    private Location moveFrontLocation(Location loc, double dir) {
        ;
        dir += loc.getYaw();
        double x = Math.cos(Math.toRadians(dir));
        double z = Math.sin(Math.toRadians(dir));
        loc.setX(loc.getX() + x);
        loc.setZ(loc.getZ() + z);
        return loc;
    }

    private void adjustPosition(Location loc) {
        float a = loc.getYaw();
        a = (float) Math.floor(a / 90) * 90;
        loc.setYaw(a);

        double b = loc.getX();
        b = Math.floor(b);
        b = b + 0.5;
        loc.setX(b);

        double c = loc.getZ();
        c = Math.floor(c);
        c = c + 0.5;
        loc.setZ(c);

        double d = loc.getY();
        d = Math.floor(d);
        loc.setY(d);
    }


    boolean tick;
    Map<Team, ArmorStand> map = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equals("agentcraft")) {
            if (sender instanceof Player) {
                if (args.length == 0) {
                    sender.sendMessage("引数を指定してください。");
                } else {
                    switch (args[0]) {
                        case "time":
                            sender.sendMessage("テスト");
                            break;
                        case "summon":
                            Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
                            map.clear();
                            for (Player onlinePlayer : onlinePlayers) {
                                Location location = onlinePlayer.getLocation();
                                Team team = onlinePlayer.getScoreboard().getEntryTeam(onlinePlayer.getName());
                                if (team != null) {
                                    if (!map.containsKey(team)) {
                                        adjustPosition(location);
                                        @NotNull ArmorStand entity = location.getWorld().spawn(location, ArmorStand.class);
                                        entity.setGravity(false);
                                        entity.setSmall(true);
                                        entity.setArms(true);
                                        entity.setItemInHand(new ItemStack(Material.DIAMOND_PICKAXE));
                                        entity.addScoreboardTag(team.getName());
                                        map.put(team, entity);
                                        sender.sendMessage("アーマースタンドを召喚しました。");
                                    }
                                } else {
                                    sender.sendMessage("チームが存在しません");
                                }
                            }
                            break;
                        case "start":
                            tick = true;
                            System.out.println(tick);
                            break;
                        case "end":
                            tick = false;
                            System.out.println(tick);
                            break;
                        default:
                            sender.sendMessage("不明なコマンドです。");
                            break;
                    }
                }
            }
        }
        return super.onCommand(sender, command, label, args);
    }
}
