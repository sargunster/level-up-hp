package me.sargunvohra.svlib.capability

import net.minecraft.entity.player.EntityPlayer
import java.util.HashSet

abstract class PlayerCapability {
    private val listeners = HashSet<() -> Any>()

    open fun apply(target: EntityPlayer) {}

    open fun shouldPersist(wasDeath: Boolean) = !wasDeath

    fun addListener(listener: () -> Unit) = listeners.add(listener)

    fun notifyModified() = listeners.forEach { it() }
}
