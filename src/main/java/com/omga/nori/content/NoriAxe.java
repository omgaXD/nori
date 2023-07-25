package com.omga.nori.content;

import com.omga.nori.init.ItemInit;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class NoriAxe extends AxeItem {
    protected static Map<Block, Block> STRIPPABLES_REVERSED;
    public NoriAxe(Tier p_40521_, float p_40522_, float p_40523_, Properties p_40524_) {
        super(p_40521_, p_40522_, p_40523_, p_40524_);

        // weird fuckery copied from stackoverflow incoming
        STRIPPABLES_REVERSED = STRIPPABLES.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }

    @SuppressWarnings("All") // todo figure out what to supress precisely.
    @SubscribeEvent
    public static void onDestroyBlock(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        BlockPos breakPos = event.getPos();
        Level level = event.getPlayer().getLevel();
        BlockState state = event.getState();

        if (!player.getItemInHand(InteractionHand.MAIN_HAND).is(ItemInit.NORI_AXE.get())) return;

        // try to turn any logs on our way into stripped logs, then cancel event so it doesn't break.
        level.setBlock(breakPos, AxeItem.getAxeStrippingState(state), 3);
        event.setCanceled(true);
    }


    @Override
    public @NotNull InteractionResult useOn(UseOnContext p_40529_) {
        // vanilla code incoming, woohoo!
        Level level = p_40529_.getLevel();
        BlockPos blockpos = p_40529_.getClickedPos();
        Player player = p_40529_.getPlayer();
        BlockState blockstate = level.getBlockState(blockpos);

        Optional<BlockState> unstripped = Optional.empty();
        boolean is_unstripped = (STRIPPABLES_REVERSED.containsKey(blockstate.getBlock()));
        if (is_unstripped)
            unstripped = Optional.of(STRIPPABLES_REVERSED.get(blockstate.getBlock()).withPropertiesOf(blockstate));

        Optional<BlockState> optional_wax = is_unstripped ? Optional.empty() : Optional.ofNullable(blockstate.getToolModifiedState(p_40529_, net.minecraftforge.common.ToolActions.AXE_SCRAPE, false));
        Optional<BlockState> optional_another_wax = is_unstripped || optional_wax.isPresent() ? Optional.empty() : Optional.ofNullable(blockstate.getToolModifiedState(p_40529_, net.minecraftforge.common.ToolActions.AXE_WAX_OFF, false));
        ItemStack itemstack = p_40529_.getItemInHand();
        Optional<BlockState> result = Optional.empty();
        if (is_unstripped) {
            level.playSound(player, blockpos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
            result = unstripped;
        } else if (optional_wax.isPresent()) {
            level.playSound(player, blockpos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.levelEvent(player, 3005, blockpos, 0);
            result = optional_wax;
        } else if (optional_another_wax.isPresent()) {
            level.playSound(player, blockpos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.levelEvent(player, 3004, blockpos, 0);
            result = optional_another_wax;
        }

        if (result.isPresent()) {
            if (player instanceof ServerPlayer) {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, blockpos, itemstack);
            }

            level.setBlock(blockpos, result.get(), 11);
            if (player != null) {
                itemstack.hurtAndBreak(1, player, (p_150686_) -> {
                    p_150686_.broadcastBreakEvent(p_40529_.getHand());
                });
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }

    public static ToolAction UnscrapeToolAction = ToolAction.get("unscrape");


    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return toolAction == ToolActions.AXE_STRIP || toolAction == ToolActions.AXE_WAX_OFF || toolAction == ToolActions.AXE_DIG ||
                toolAction == UnscrapeToolAction;
    }
}
