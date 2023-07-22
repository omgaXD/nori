package com.omga.nori.content;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class NoriBucket extends BucketItem {

    public NoriBucket(Properties buider) {
        super(() -> Fluids.EMPTY, buider);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        BlockHitResult blockhitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        InteractionResultHolder<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onBucketUse(player, level, itemstack, blockhitresult);
        if (ret != null) return ret;
        if (blockhitresult.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(itemstack);
        } else if (blockhitresult.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(itemstack);
        } else {
            BlockPos blockpos = blockhitresult.getBlockPos();
            Direction direction = blockhitresult.getDirection();
            BlockPos blockpos1 = blockpos.relative(direction);
            if (level.mayInteract(player, blockpos) && player.mayUseItemAt(blockpos1, direction, itemstack)) {
                BlockState blockstate1 = level.getBlockState(blockpos);
                if (blockstate1.getBlock() instanceof BucketPickup bucketpickup) {
                    ItemStack itemstack1 = bucketpickup.pickupBlock(level, blockpos, blockstate1);
                    if (!itemstack1.isEmpty()) {
                        player.awardStat(Stats.ITEM_USED.get(this));
                        bucketpickup.getPickupSound(blockstate1).ifPresent((p_150709_) -> {
                            player.playSound(p_150709_, 1.0F, 1.0F);
                        });
                        level.gameEvent(player, GameEvent.FLUID_PICKUP, blockpos);
                        if (!level.isClientSide) {
                            CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer)player, itemstack1);
                        }

                        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
                    }
                }

                return InteractionResultHolder.fail(itemstack);
            } else {
                return InteractionResultHolder.fail(itemstack);
            }
        }
    }
}
