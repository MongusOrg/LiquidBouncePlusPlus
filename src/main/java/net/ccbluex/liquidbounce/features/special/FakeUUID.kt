/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.special

import com.mojang.util.UUIDTypeAdapter
import java.util.UUID
import net.ccbluex.liquidbounce.utils.MinecraftInstance

object FakeUUID : MinecraftInstance() {
    var spoofedUUID = ""

    @JvmStatic
    fun getSpoofID(): String? = if (spoofedUUID.length <= 0) null else spoofedUUID
}