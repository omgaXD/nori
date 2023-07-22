package com.omga.nori.init;

import com.omga.nori.NoriMod;
import com.omga.nori.content.NoriBucket;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

import static com.omga.nori.NoriMod.REGISTRATE;

@SuppressWarnings("unused")
public class ItemInit {

    public static final RegistryEntry<Item> NORI_INGOT = REGISTRATE.item("nori_ingot", Item::new)
            .tab(() -> CreativeModeTab.TAB_MATERIALS)
            .register();

    public static final RegistryEntry<Item> NORI_NUGGET = REGISTRATE.item("nori_nugget", Item::new)
            .tab(() -> CreativeModeTab.TAB_MATERIALS)
            .register();

    public static final ItemEntry<NoriBucket> NORI_BUCKET = REGISTRATE.item("nori_bucket", NoriBucket::new)
            .tab(() -> CreativeModeTab.TAB_MATERIALS)
            .register();


    public static void load() {
        NoriMod.LOGGER.info("Register items");
    }

}
