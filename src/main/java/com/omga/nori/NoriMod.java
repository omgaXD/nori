package com.omga.nori;

import com.mojang.logging.LogUtils;
import com.omga.nori.init.ItemInit;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;


// The value here should match an entry in the META-INF/mods.toml file
@Mod("nori")
@SuppressWarnings("unused")
public class NoriMod
{
    public static final String MOD_ID = "nori";
    public static ResourceLocation RL(String resource) {
        return new ResourceLocation(MOD_ID, resource);
    }

    // Registrate object
    public static Registrate REGISTRATE;



    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();


    public NoriMod()
    {

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        REGISTRATE = Registrate.create(MOD_ID);

        ItemInit.load();
    }
}
