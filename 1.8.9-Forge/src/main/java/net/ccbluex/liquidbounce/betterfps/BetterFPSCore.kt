/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.betterfps

class BetterFPSCore {
    // Math
    var libGDX: LibGDXMath
    var rivens_full: RivensFullMath
    var rivens_half: RivensHalfMath
    var rivens: RivensMath
    var taylor: TaylorMath

    init {
        libGDX = LibGDXMath()
        rivens_full = RivensFullMath()
        rivens_half = RivensHalfMath()
        rivens = RivensMath()
        taylor = TaylorMath()
    }
}