package me.sargunvohra.leveluphp.item

import me.sargunvohra.leveluphp.level.leveller
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.EnumAction
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.stats.StatList
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.world.World

class HeartContainer : Item(Properties().group(ItemGroup.MISC)) {

    override fun hasEffect(stack: ItemStack) = true

    override fun getItemStackLimit(stack: ItemStack) = 1

    override fun getUseDuration(stack: ItemStack) = 32

    override fun getUseAction(stack: ItemStack) = EnumAction.EAT

    override fun onItemRightClick(
        world: World,
        player: EntityPlayer,
        hand: EnumHand
    ): ActionResult<ItemStack> {
        var ret = ActionResult(EnumActionResult.FAIL, player.getHeldItem(hand))
        player.leveller.ifPresent {
            if (!it.maxedOut) {
                player.activeHand = hand
                ret = ActionResult(EnumActionResult.SUCCESS, player.getHeldItem(hand))
            }
        }
        return ret
    }

    override fun onItemUseFinish(
        stack: ItemStack,
        world: World,
        entity: EntityLivingBase
    ): ItemStack {
        (entity as? EntityPlayerMP)?.leveller?.ifPresent {
            it.level++
            entity.addStat(StatList.ITEM_USED.get(this))
            CriteriaTriggers.CONSUME_ITEM.trigger(entity, stack)
            stack.shrink(1)
        }
        return stack
    }
}
