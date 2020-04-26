package me.sargunvohra.mcmods.leveluphp.item

import me.sargunvohra.mcmods.leveluphp.core.hpLeveller
import me.sargunvohra.mcmods.leveluphp.core.isMaxedOut
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.UseAction
import net.minecraft.stats.Stats
import net.minecraft.util.ActionResult
import net.minecraft.util.ActionResultType
import net.minecraft.util.Hand
import net.minecraft.world.World

class HeartContainerItem(props: Properties) : Item(props) {

    override fun hasEffect(stack: ItemStack) = true

    override fun getUseDuration(stack: ItemStack) = 32

    override fun getUseAction(stack: ItemStack) = UseAction.EAT

    override fun onItemRightClick(world: World, player: PlayerEntity, hand: Hand): ActionResult<ItemStack> {
        var ret = ActionResult(ActionResultType.FAIL, player.getHeldItem(hand))
        player.hpLeveller.let {
            if (!it.isMaxedOut) {
                player.activeHand = hand
                ret = ActionResult(ActionResultType.SUCCESS, player.getHeldItem(hand))
            }
        }
        return ret
    }

    override fun onItemUseFinish(
        stack: ItemStack,
        world: World,
        entity: LivingEntity
    ): ItemStack {
        (entity as? ServerPlayerEntity)?.hpLeveller?.let {
            it.level++
            entity.addStat(Stats.ITEM_USED.get(this))
            CriteriaTriggers.CONSUME_ITEM.trigger(entity, stack)
            stack.shrink(1)
        }
        return stack
    }
}
