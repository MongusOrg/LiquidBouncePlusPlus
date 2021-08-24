package net.ccbluex.liquidbounce.features.module.modules.player;

import net.ccbluex.liquidbounce.event.*;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.MovementUtils;
import net.ccbluex.liquidbounce.utils.InventoryUtils;
import net.ccbluex.liquidbounce.utils.timer.MSTimer;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.util.DamageSource;

@ModuleInfo(name = "InvManager", description = "yep, another badly skidded invcleaner module.", category = ModuleCategory.PLAYER)
public class InvManager extends Module {

    public final BoolValue noMoveValue = new BoolValue("NoMove", false);
    public final BoolValue smartValue = new BoolValue("SmartClean", false);
    public final IntegerValue delay = new IntegerValue("Delay", 1, 0, 500);

    private boolean cleaning, equipping = false;

    private MSTimer timer = new MSTimer();

    @Override
    public void onEnable() {
        cleaning = equipping = false;
    }

    @EventTarget
    public void onMotion(MotionEvent e) {
        int slotID = -1;
        double maxProt = -1.0D;
        int switchArmor = -1;
        if (e.getEventState() == EventState.PRE && (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory) && (!noMoveValue.get() || !MovementUtils.isMoving())) {
            for (int i = 0; i < 45; ++i) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                    ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                    if ((isTrash(is) || (smartValue.get() && hasBetterItem(i, is))) && timer.hasTimePassed(delay.get())) {
                        if (!cleaning) {
                            cleaning = true;
                            mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                        }
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 0, mc.thePlayer);
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, -999, 0, 0, mc.thePlayer);
                        timer.reset();
                        break;
                    }
                }

                if (i == 44 && cleaning) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(0));
                    cleaning = false;
                }


                ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (stack != null && (this.canEquip(stack) || isBestArmor(stack) && !this.canEquip(stack))) {
                    if (isBestArmor(stack) && switchArmor == -1) {
                        switchArmor = getSlotToSwap(stack);
                    }

                    double protValue = this.getProtectionValue(stack);
                    if (protValue >= maxProt) {
                        slotID = i;
                        maxProt = protValue;
                    }
                }
            }

            if (slotID != -1) {
                if (this.timer.hasTimePassed(delay.get())) {
                    if (!this.equipping) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                        this.equipping = true;
                    }

                    if (switchArmor != -1) {
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 4 + switchArmor, 0, 4, mc.thePlayer);
                    } else {
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slotID, 0, 1, mc.thePlayer);
                    }
                    this.timer.reset();
                }
            } else if (this.equipping) {
                mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(0));
                this.equipping = false;
            }
        }
    }
/*
    public static void swap(int slot, int hotbarNum) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, hotbarNum, 2, mc.thePlayer);
    }
*/
    public static float getSwordDamage(final ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof ItemSword)) return 0;
        float damage = ((ItemSword) itemStack.getItem()).getDamageVsEntity();
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack) * 1.25f;
        return damage;
    }

    public static boolean hasBetterItem(int slot, ItemStack stack) {
        return findBetterItem(slot, stack) != -1 && findBetterItem(slot, stack) != slot;
    }

    public static int findBetterItem(int slot, ItemStack stack) {
        int finalSlot = -1;
        if (stack.getItem() instanceof ItemTool) {
            finalSlot = slot;
            int index = 0;
            for (ItemStack itemStack : mc.thePlayer.inventory.mainInventory) {
                if (itemStack == null || itemStack.getItem() == null) {
                    index++;
                    continue;
                }
                if (itemStack != stack && itemStack.getItem() instanceof ItemTool) {
                    double currentDamage = (itemStack.getAttributeModifiers().get("generic.attackDamage").stream().findFirst().orElse(null) == null 
                    ? 0D : itemStack.getAttributeModifiers().get("generic.attackDamage").stream().findFirst().orElse(null).getAmount()) + 1.25 * getEnchantment(itemStack, Enchantment.sharpness);
                    ItemStack bestStack = mc.thePlayer.inventory.getStackInSlot(finalSlot);
                    if (bestStack == null) {
                        index++;
                        continue;
                    }
                    double bestDamage = (bestStack.getAttributeModifiers().get("generic.attackDamage").stream().findFirst().orElse(null) == null 
                    ? 0D : bestStack.getAttributeModifiers().get("generic.attackDamage").stream().findFirst().orElse(null).getAmount()) + 1.25 * getEnchantment(bestStack, Enchantment.sharpness);
                    if (bestDamage < currentDamage)
                        finalSlot = index;
                }
                index++;
            }
        }

        if (stack.getItem() instanceof ItemSword) {
            finalSlot = slot;
            int index = 0;
            for (ItemStack itemStack : mc.thePlayer.inventory.mainInventory) {
                if (itemStack == null || itemStack.getItem() == null) {
                    index++;
                    continue;
                }
                if (itemStack != stack && itemStack.getItem() instanceof ItemSword) {
                    float currDamage = getSwordDamage(itemStack);
                    ItemStack bestStack = mc.thePlayer.inventory.getStackInSlot(finalSlot);
                    if (bestStack == null) {
                        index++;
                        continue;
                    }
                    float bestDamage = getSwordDamage(bestStack);
                    if (bestDamage < currDamage)
                        finalSlot = index;
                }
                index++;
            }
        }

        if (stack.getItem() instanceof ItemBow) {
            finalSlot = slot;
            double bestPower = getEnchantment(stack, Enchantment.power);
            int index = 0;
            for (ItemStack itemStack : mc.thePlayer.inventory.mainInventory) {
                if (itemStack == null || itemStack.getItem() == null) {
                    index++;
                    continue;
                }
                if (itemStack != stack && itemStack.getItem() instanceof ItemBow) {
                    double currentPower = getEnchantment(itemStack, Enchantment.power);
                    if (currentPower > bestPower) {
                        finalSlot = index;
                        bestPower = currentPower;
                    }
                }
                index++;
            }
        }

        return finalSlot;
    }

    public static int getEnchantment(ItemStack itemStack, Enchantment enchantment) {
        if (itemStack == null || itemStack.getEnchantmentTagList() == null || itemStack.getEnchantmentTagList().hasNoTags())
            return 0;

        for (int i = 0; i < itemStack.getEnchantmentTagList().tagCount(); i++) {
            final NBTTagCompound tagCompound = itemStack.getEnchantmentTagList().getCompoundTagAt(i);

            if ((tagCompound.hasKey("ench") && tagCompound.getShort("ench") == enchantment.effectId) || (tagCompound.hasKey("id") && tagCompound.getShort("id") == enchantment.effectId))
                return tagCompound.getShort("lvl");
        }

        return 0;
    }

    public static boolean isBadPotion(final ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            final ItemPotion potion = (ItemPotion) stack.getItem();
            if (ItemPotion.isSplash(stack.getItemDamage())) {
                for (final Object o : potion.getEffects(stack)) {
                    final PotionEffect effect = (PotionEffect) o;
                    if (effect.getPotionID() == Potion.poison.getId() || effect.getPotionID() == Potion.harm.getId() || effect.getPotionID() == Potion.moveSlowdown.getId() || effect.getPotionID() == Potion.weakness.getId()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isBestArmor(ItemStack stack) {
        try {
            if (stack.getItem() instanceof ItemArmor) {
                if (mc.thePlayer.getEquipmentInSlot(1) != null && stack.getUnlocalizedName().contains("boots")) {
                    assert mc.thePlayer.getEquipmentInSlot(1).getItem() instanceof ItemArmor;

                    if (getProtectionValue(stack) + (double) ((ItemArmor) stack.getItem()).damageReduceAmount > getProtectionValue(mc.thePlayer.getEquipmentInSlot(1)) + (double) ((ItemArmor) mc.thePlayer.getEquipmentInSlot(1).getItem()).damageReduceAmount) {
                        return true;
                    }
                }

                if (mc.thePlayer.getEquipmentInSlot(2) != null && stack.getUnlocalizedName().contains("leggings")) {
                    assert mc.thePlayer.getEquipmentInSlot(2).getItem() instanceof ItemArmor;

                    if (getProtectionValue(stack) + (double) ((ItemArmor) stack.getItem()).damageReduceAmount > getProtectionValue(mc.thePlayer.getEquipmentInSlot(2)) + (double) ((ItemArmor) mc.thePlayer.getEquipmentInSlot(2).getItem()).damageReduceAmount) {
                        return true;
                    }
                }

                if (mc.thePlayer.getEquipmentInSlot(3) != null && stack.getUnlocalizedName().contains("chestplate")) {
                    assert mc.thePlayer.getEquipmentInSlot(3).getItem() instanceof ItemArmor;

                    if (getProtectionValue(stack) + (double) ((ItemArmor) stack.getItem()).damageReduceAmount > getProtectionValue(mc.thePlayer.getEquipmentInSlot(3)) + (double) ((ItemArmor) mc.thePlayer.getEquipmentInSlot(3).getItem()).damageReduceAmount) {
                        return true;
                    }
                }

                if (mc.thePlayer.getEquipmentInSlot(4) != null && stack.getUnlocalizedName().contains("helmet")) {
                    assert mc.thePlayer.getEquipmentInSlot(4).getItem() instanceof ItemArmor;

                    return getProtectionValue(stack) + (double) ((ItemArmor) stack.getItem()).damageReduceAmount > getProtectionValue(mc.thePlayer.getEquipmentInSlot(4)) + (double) ((ItemArmor) mc.thePlayer.getEquipmentInSlot(4).getItem()).damageReduceAmount;
                }
            }

            return false;
        } catch (Exception var3) {
            return false;
        }
    }

    public static int getSlotToSwap(ItemStack stack) {
        if (stack.getItem() instanceof ItemArmor) {
            if (mc.thePlayer.getEquipmentInSlot(1) == null) return 4;
            if (mc.thePlayer.getEquipmentInSlot(1) != null && stack.getUnlocalizedName().contains("boots")) {
                assert mc.thePlayer.getEquipmentInSlot(1).getItem() instanceof ItemArmor;

                if (getProtectionValue(stack) + (double) ((ItemArmor) stack.getItem()).damageReduceAmount > getProtectionValue(mc.thePlayer.getEquipmentInSlot(1)) + (double) ((ItemArmor) mc.thePlayer.getEquipmentInSlot(1).getItem()).damageReduceAmount) {
                    return 4;
                }
            }

            if (mc.thePlayer.getEquipmentInSlot(1) == null) return 3;

            if (mc.thePlayer.getEquipmentInSlot(2) != null && stack.getUnlocalizedName().contains("leggings")) {
                assert mc.thePlayer.getEquipmentInSlot(2).getItem() instanceof ItemArmor;

                if (getProtectionValue(stack) + (double) ((ItemArmor) stack.getItem()).damageReduceAmount > getProtectionValue(mc.thePlayer.getEquipmentInSlot(2)) + (double) ((ItemArmor) mc.thePlayer.getEquipmentInSlot(2).getItem()).damageReduceAmount) {
                    return 3;
                }
            }
            if (mc.thePlayer.getEquipmentInSlot(1) == null) return 2;


            if (mc.thePlayer.getEquipmentInSlot(3) != null && stack.getUnlocalizedName().contains("chestplate")) {

                if (getProtectionValue(stack) + (double) ((ItemArmor) stack.getItem()).damageReduceAmount > getProtectionValue(mc.thePlayer.getEquipmentInSlot(3)) + (double) ((ItemArmor) mc.thePlayer.getEquipmentInSlot(3).getItem()).damageReduceAmount) {
                    return 2;
                }
            }
            if (mc.thePlayer.getEquipmentInSlot(1) == null) return 1;


            if (mc.thePlayer.getEquipmentInSlot(4) != null && stack.getUnlocalizedName().contains("helmet")) {
                assert mc.thePlayer.getEquipmentInSlot(1).getItem() instanceof ItemArmor;

                if (getProtectionValue(stack) + (double) ((ItemArmor) stack.getItem()).damageReduceAmount > getProtectionValue(mc.thePlayer.getEquipmentInSlot(4)) + (double) ((ItemArmor) mc.thePlayer.getEquipmentInSlot(4).getItem()).damageReduceAmount) {
                    return 1;
                }
            }
        }

        return -1;
    }

    public static boolean isTrash(ItemStack item) {
        return ((item.getItem().getUnlocalizedName().contains("tnt")) || item.getDisplayName().contains("frog") ||
                (item.getItem().getUnlocalizedName().contains("stick"))||
                (item.getItem().getUnlocalizedName().contains("string")) || (item.getItem().getUnlocalizedName().contains("flint")) ||
                (item.getItem().getUnlocalizedName().contains("feather")) || (item.getItem().getUnlocalizedName().contains("bucket")) ||
                (item.getItem().getUnlocalizedName().contains("snow")) || (item.getItem().getUnlocalizedName().contains("enchant")) ||
                (item.getItem().getUnlocalizedName().contains("exp")) || (item.getItem().getUnlocalizedName().contains("shears")) ||
                (item.getItem().getUnlocalizedName().contains("anvil")) ||
                (item.getItem().getUnlocalizedName().contains("torch")) || (item.getItem().getUnlocalizedName().contains("seeds")) ||
                (item.getItem().getUnlocalizedName().contains("leather")) || (item.getItem().getUnlocalizedName().contains("boat")) ||
                (item.getItem().getUnlocalizedName().contains("fishing")) || (item.getItem().getUnlocalizedName().contains("wheat")) ||
                (item.getItem().getUnlocalizedName().contains("flower")) || (item.getItem().getUnlocalizedName().contains("record")) ||
                (item.getItem().getUnlocalizedName().contains("note")) || (item.getItem().getUnlocalizedName().contains("sugar")) ||
                (item.getItem().getUnlocalizedName().contains("wire")) || (item.getItem().getUnlocalizedName().contains("trip")) ||
                (item.getItem().getUnlocalizedName().contains("slime")) || (item.getItem().getUnlocalizedName().contains("web")) ||
                ((item.getItem() instanceof ItemGlassBottle)) || (item.getItem().getUnlocalizedName().contains("piston")) ||
                (item.getItem().getUnlocalizedName().contains("potion") && (isBadPotion(item))) ||
                (item.getItem() instanceof ItemBlock && InventoryUtils.BLOCK_BLACKLIST.contains(((ItemBlock)item.getItem()).getBlock())) ||
             //   ((item.getItem() instanceof ItemArmor) && isBestArmor(item)) ||
             //   (item.getItem() instanceof ItemEgg || (item.getItem().getUnlocalizedName().contains("bow")) && !item.getDisplayName().contains("Kit")) ||
             //   ((item.getItem() instanceof ItemSword) && !isBestSword(item)) ||
                (item.getItem().getUnlocalizedName().contains("Raw")));
    }

    private static boolean canEquip(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemArmor)) return false;

        return mc.thePlayer.getEquipmentInSlot(1) == null && stack.getUnlocalizedName().contains("boots") || mc.thePlayer.getEquipmentInSlot(2) == null && stack.getUnlocalizedName().contains("leggings") || mc.thePlayer.getEquipmentInSlot(3) == null && stack.getUnlocalizedName().contains("chestplate") || mc.thePlayer.getEquipmentInSlot(4) == null && stack.getUnlocalizedName().contains("helmet");
    }

    private static double getProtectionValue(ItemStack stack) {
        return stack.getItem() instanceof ItemArmor ? (double) ((ItemArmor) stack.getItem()).damageReduceAmount + (double) ((100 - ((ItemArmor) stack.getItem()).damageReduceAmount) * EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack)) * 0.0075D : 0.0D;
    }
}
