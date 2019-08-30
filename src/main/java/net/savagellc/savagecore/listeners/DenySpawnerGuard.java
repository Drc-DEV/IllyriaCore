package net.savagellc.savagecore.listeners;

import net.savagellc.savagecore.persist.Conf;
import net.prosavage.baseplugin.XMaterial;
import net.savagellc.savagecore.persist.Messages;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DenySpawnerGuard implements Listener {

    private final List<BlockFace> blockFaces = Arrays.asList(BlockFace.WEST, BlockFace.EAST, BlockFace.SOUTH, BlockFace.NORTH);
    private final Material spawner = XMaterial.SPAWNER.parseMaterial();

    @SuppressWarnings("deprecation")
    @EventHandler
    public void spawnerPlacement(PlayerInteractEvent e) {

        if (!Conf.denySpawnerProtection) return;

        if (e.isCancelled()) return;

        Player p = e.getPlayer();
        ItemStack inHand = p.getItemInHand();

        if (inHand == null) return;

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (!inHand.getType().equals(spawner)) {
                if (e.getBlockFace().equals(BlockFace.UP) || e.getBlockFace().equals(BlockFace.DOWN)) return;

                if (!Objects.requireNonNull(e.getClickedBlock()).getType().equals(spawner)) return;

                e.setCancelled(true);
                p.sendMessage(Messages.prefix + Messages.noSpawnerProtection.toString());
            } else if (inHand.getType().equals(spawner)) {
                if (Objects.requireNonNull(e.getClickedBlock()).getType().equals(spawner)) return;

                if (e.getBlockFace().equals(BlockFace.UP) || e.getBlockFace().equals(BlockFace.DOWN)) return;
                e.setCancelled(true);
                p.sendMessage(Messages.prefix + Messages.noSpawnerProtection.toString());
            }
        }
    }

    @EventHandler
    public void spawnerProtectionCheck(BlockPlaceEvent e) {

        if (!Conf.denySpawnerProtection) return;

        if (e.isCancelled()) return;

        Block blockPlaced = e.getBlockPlaced();
        Player p = e.getPlayer();

        if (blockPlaced == null) return;

        if (blockPlaced.getType() == spawner) {
            for (BlockFace blockFace : blockFaces) {
                if (e.getBlockPlaced().getRelative(blockFace).getType() != spawner) {
                    if (e.getBlockPlaced().getRelative(blockFace).getType() != Material.AIR) {
                        e.setCancelled(true);
                        p.sendMessage(Messages.prefix + Messages.noSpawnerProtection.toString());
                        return;
                    }
                }
            }
        } else if (blockPlaced.getType() != spawner) {
            for (BlockFace blockFace : blockFaces) {
                if (e.getBlockPlaced().getRelative(blockFace).getType() == spawner) {
                    e.setCancelled(true);
                    p.sendMessage(Messages.prefix + Messages.noSpawnerProtection.toString());
                    return;
                }
            }
        }
    }
}