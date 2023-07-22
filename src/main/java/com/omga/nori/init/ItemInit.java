package com.omga.nori.init;

import com.omga.nori.NoriMod;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import static com.omga.nori.NoriMod.REGISTRATE;

@SuppressWarnings("unused")
public class ItemInit {

    public static final RegistryEntry<Item> NORI_INGOT = REGISTRATE.item("nori_ingot", Item::new)
            .tab(() -> CreativeModeTab.TAB_MATERIALS)
            .register();

    public static final RegistryEntry<Item> NORI_NUGGET= REGISTRATE.item("nori_nugget", Item::new)
            .tab(() -> CreativeModeTab.TAB_MATERIALS)
            .register();




    public static void load() {
        NoriMod.LOGGER.info("Register items");
    }

}
