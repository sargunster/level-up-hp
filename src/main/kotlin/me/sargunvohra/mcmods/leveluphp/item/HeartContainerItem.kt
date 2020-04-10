package me.sargunvohra.mcmods.leveluphp.item

import me.sargunvohra.mcmods.leveluphp.hpLevelHandler
import net.minecraft.advancement.criterion.Criterions
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.stat.Stats
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.UseAction
import net.minecraft.world.World

class HeartContainerItem : Item(Settings().group(ItemGroup.MISC).maxCount(1)) {

    override fun hasEnchantmentGlint(stack: ItemStack) = true

    override fun getMaxUseTime(stack: ItemStack) = 32

    override fun getUseAction(stack: ItemStack) = UseAction.EAT

    override fun use(world: World, player: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        var ret = TypedActionResult(ActionResult.FAIL, player.getStackInHand(hand))
        player.hpLevelHandler.let {
            if (!it.isMaxedOut) {
                player.setCurrentHand(hand)
                ret = TypedActionResult(ActionResult.SUCCESS, player.getStackInHand(hand))
            }
        }
        return ret
    }

    override fun finishUsing(
        stack: ItemStack,
        world: World,
        entity: LivingEntity
    ): ItemStack {
        (entity as? ServerPlayerEntity)?.hpLevelHandler?.let {
            it.level++
            entity.incrementStat(Stats.USED.getOrCreateStat(this))
            Criterions.CONSUME_ITEM.trigger(entity, stack)
            stack.decrement(1)
        }
        return stack
    }
}
