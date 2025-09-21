package io.github.paulem.attackthrough;

import com.google.common.primitives.Doubles;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class AttackThrough extends JavaPlugin implements Listener {
    private FileConfiguration config;
    private final int DEFAULT_REACH = 4;
    private final double DEFAULT_RAY_SIZE = 0.2;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        // Save the default config and get it
        saveDefaultConfig();
        config = getConfig();

        getLogger().info("Enabled !");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled !");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        // Get the block that the player clicked
        Block clickedBlock = event.getClickedBlock();
        if(clickedBlock == null) return;

        // Get the item in the player's hand, if there is none, set it as air
        @Nullable ItemStack itemInHand = event.getItem();
        if(itemInHand == null) itemInHand = new ItemStack(Material.AIR);

        // If the config is set to only allow the attack through for tools, and the item in hand is not a tool, return
        if(config.getBoolean("onlyForTools", false) && !EnchantmentTarget.TOOL.includes(itemInHand)) return;

        if (clickedBlock.isPassable() && !clickedBlock.getType().isInteractable())
        {
            Player player = event.getPlayer();

            Location clickedBlockLoc = clickedBlock.getLocation();
            Location playerEyeLoc = player.getEyeLocation();

            final RayTraceResult result = clickedBlock.getWorld().rayTraceEntities(clickedBlockLoc,
                    playerEyeLoc.getDirection(),
                    Doubles.constrainToRange(config.getInt("reach", DEFAULT_REACH) - clickedBlockLoc.distance(playerEyeLoc), 0, config.getInt("reach", DEFAULT_REACH)),
                    config.getDouble("raySize", DEFAULT_RAY_SIZE), entity -> entity instanceof Damageable);
            if(result == null) return;

            Entity hitEntity = result.getHitEntity();

            // If there is an entity behind the block, and it's not the player himself, attack it
            if (hitEntity != null && !hitEntity.equals(player))
            {
                // If the player is holding a sword, play the attack animation
                final boolean sweepin = isSword(itemInHand);
                if(sweepin) player.swingMainHand();

                // If the player doesn't have an active cooldown, attack the entity behind the block
                if(!player.hasCooldown(itemInHand.getType())) {
                    player.attack(hitEntity);
                }

                double itemAttackSpeed = 4; // Default item attack speed

                // Get all modifiers that are applied to the player to edit his attack speed (armor, tool...)
                AttributeInstance playerAttackSpeed = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
                if(playerAttackSpeed != null) {
                        itemAttackSpeed = playerAttackSpeed.getValue();
                }

                // Get the amount of ticks for the tool reload based on his attack speed, and put it as cooldown
                int ticks = getAttackSpeedTicks(itemAttackSpeed);
                player.setCooldown(itemInHand.getType(), ticks);

                // Cancel the block interaction (basically the break)
                event.setCancelled(true);
            }
        }
    }

    // Get the amount of ticks for the tool reload based on his attack speed
    public static int getAttackSpeedTicks(@NotNull Double attackSpeed) {
        return (int) (20 / attackSpeed);
    }

    // Check if the item is a sword
    public static boolean isSword(@Nullable ItemStack is){
        return is != null && is.getType().name().contains("SWORD");
    }
}
