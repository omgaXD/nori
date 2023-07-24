package com.omga.nori.content;

import com.mojang.datafixers.util.Pair;
import com.omga.nori.NoriMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;


@Mod.EventBusSubscriber
public class NoriHoe extends DiggerItem {
    // todo choose the tag that a hoe item would be mostly cursed to dig (can be any tag even Anvil)
    private static final TagKey<Block> most_cursed_mineable_tag = BlockTags.MINEABLE_WITH_HOE;
    public NoriHoe(Tier p_41336_, int p_41337_, float p_41338_, Item.Properties p_41339_) {
        super((float)p_41337_, p_41338_, p_41336_, most_cursed_mineable_tag, p_41339_);
    }

    public static ToolAction NoriHoeToolAction = ToolAction.get("norihoe");

    // vanilla code
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockState toolModifiedState = level.getBlockState(blockpos).getToolModifiedState(context, NoriHoeToolAction, false);
        Pair<Predicate<UseOnContext>, Consumer<UseOnContext>> pair = toolModifiedState == null ? null : Pair.of(ctx -> true, changeIntoState(toolModifiedState));
        if (pair == null) {
            return InteractionResult.PASS;
        } else {
            Predicate<UseOnContext> predicate = pair.getFirst();
            Consumer<UseOnContext> consumer = pair.getSecond();
            if (predicate.test(context)) {
                Player player = context.getPlayer();
                level.playSound(player, blockpos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
                if (!level.isClientSide) {
                    consumer.accept(context);
                    if (player != null) {
                        context.getItemInHand().hurtAndBreak(1, player, (p_150845_) -> {
                            p_150845_.broadcastBreakEvent(context.getHand());
                        });
                    }
                }

                return InteractionResult.sidedSuccess(level.isClientSide);
            } else {
                return InteractionResult.PASS;
            }
        }
    }

    private static final TagKey<Block> allowed_blocks = Objects.requireNonNull(ForgeRegistries.BLOCKS.tags()).createTagKey(NoriMod.RL("nori_hoe_convertable"));
    private static final BlockState nori_hoe_converts_to = Blocks.DIRT.defaultBlockState();

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void OnNoriHoeEvent(BlockEvent.BlockToolModificationEvent event) {
        if (!event.getToolAction().equals(NoriHoeToolAction)) {
            return;
        }
        if (event.getState().is(allowed_blocks))
            event.setFinalState(nori_hoe_converts_to);
    }

    public static Consumer<UseOnContext> changeIntoState(BlockState bs) {
        return (context) -> {
            context.getLevel().setBlock(context.getClickedPos(), bs, 11);
        };
    }

    @Override
    public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction) {
        return toolAction == NoriHoeToolAction;
    }
}
