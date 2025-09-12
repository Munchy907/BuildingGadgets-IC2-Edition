package com.direwolf20.buildinggadgets.common.items.gadgets;

import com.direwolf20.buildinggadgets.common.config.SyncedConfig;
import com.direwolf20.buildinggadgets.common.items.ItemModBase;
import com.direwolf20.buildinggadgets.common.items.capability.CapabilityProviderBlockProvider;
import com.direwolf20.buildinggadgets.common.tools.NBTTool;
import ic2.api.classic.item.IDamagelessElectricItem;
import ic2.api.classic.item.IElectricTool;
import ic2.api.item.ElectricItem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.BlockEvent;

import javax.annotation.Nullable;
import java.util.List;

public abstract class GadgetGeneric extends ItemModBase implements IDamagelessElectricItem, IElectricTool {
    protected int tier = 2; // Tier in IC2 Terms, same tier as Energy Crystal
    protected final int TRANSFER_RATE = 2048; // Transfer rate for IC2, same as Lapotron Crystal
    protected final boolean PROVIDE_ENERGY = false; // Can provide energy to machines, same as a normal IC2 Tool

    public GadgetGeneric(String name) {
        super(name);
        this.setNoRepair();
        setMaxStackSize(1);
    }

    public int getMaxEnergy() {
        return SyncedConfig.maxEnergy;
    }


    @Override
    @Nullable
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound tag) {
        return new CapabilityProviderBlockProvider(stack);
    }

    @Override
    public boolean isDamageable() {
        return true;
    }

    @Override
    public boolean isRepairable() {
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book){return false;}

    @Override
    public boolean isEnchantable(ItemStack stack) {return false;}
    @Override
    public boolean isExcluded(ItemStack item, Enchantment ench) {
        return ench == Enchantments.MENDING;
    }
    @Override
    public EnumEnchantmentType getType(ItemStack item) {
        return EnumEnchantmentType.DIGGER;
    }
    @Override
    public boolean isSpecialSupported(ItemStack item, Enchantment ench) {
        return ench == Enchantments.UNBREAKING;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1D - ElectricItem.manager.getCharge(stack) / this.getMaxCharge(stack);
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return MathHelper.hsvToRGB(Math.max(0.0F, (float) ElectricItem.manager.getCharge(stack) / (float) this.getMaxCharge(stack)) / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public boolean isDamaged(ItemStack stack) {
        return ElectricItem.manager.getCharge(stack) != this.getMaxEnergy();
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
        // IC2 Doesn't hide the durability bar when full for its tools
        // ElectricItem.manager.getCharge(stack) != getMaxCharge();
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return false;
    }

    public static ItemStack getGadget(EntityPlayer player) {
        ItemStack heldItem = player.getHeldItemMainhand();
        if (!(heldItem.getItem() instanceof GadgetGeneric)) {
            heldItem = player.getHeldItemOffhand();
            if (!(heldItem.getItem() instanceof GadgetGeneric)) {
                return ItemStack.EMPTY;
            }
        }
        return heldItem;
    }

    public abstract int getEnergyCost(ItemStack tool);

    public abstract int getDamageCost(ItemStack tool);

    public boolean canUse(ItemStack tool, EntityPlayer player) {
        // You can always use in creative or when max energy is set to 0
        if (player.capabilities.isCreativeMode || getMaxEnergy() == 0)
            return true;

        return ElectricItem.manager.canUse(tool, getEnergyCost(tool));
    }

    public void applyDamage(ItemStack tool, EntityPlayer player) {
        // don't apply damage in creative or if there is no power to be had
        if (player.capabilities.isCreativeMode || getMaxEnergy() == 0)
            return;

        ElectricItem.manager.use(tool, getEnergyCost(tool), player);
    }

    protected void addEnergyInformation(List<String> list, ItemStack stack) {
        // Don't display energy for gadgets that can't accept it.
/*        if( getMaxCharge() == 0 ) return;
        if (stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
            IEnergyStorage energy = CapabilityProviderEnergy.getCap(stack);
            list.add(TextFormatting.WHITE + I18n.format("tooltip.gadget.energy") + ": " + withSuffix(energy.getEnergyStored()) + "/" + withSuffix(energy.getMaxEnergyStored()));
        }*/
    }

    public static boolean getFuzzy(ItemStack stack) {
        return NBTTool.getOrNewTag(stack).getBoolean("fuzzy");
    }

    public static void toggleFuzzy(EntityPlayer player, ItemStack stack) {
        NBTTool.getOrNewTag(stack).setBoolean("fuzzy", !getFuzzy(stack));
        player.sendStatusMessage(new TextComponentString(TextFormatting.AQUA + new TextComponentTranslation("message.gadget.fuzzymode").getUnformattedComponentText() + ": " + getFuzzy(stack)), true);
    }

    public static boolean getConnectedArea(ItemStack stack) {
        return !NBTTool.getOrNewTag(stack).getBoolean("unconnectedarea");
    }

    public static void toggleConnectedArea(EntityPlayer player, ItemStack stack) {
        NBTTool.getOrNewTag(stack).setBoolean("unconnectedarea", getConnectedArea(stack));
        String suffix = stack.getItem() instanceof GadgetDestruction ? "area" : "surface";
        player.sendStatusMessage(new TextComponentString(TextFormatting.AQUA + new TextComponentTranslation("message.gadget.connected" + suffix).getUnformattedComponentText() + ": " + getConnectedArea(stack)), true);
    }

    public static boolean shouldRayTraceFluid(ItemStack stack) {
        return NBTTool.getOrNewTag(stack).getBoolean("raytrace_fluid");
    }

    public static void toggleRayTraceFluid(EntityPlayer player, ItemStack stack) {
        NBTTool.getOrNewTag(stack).setBoolean("raytrace_fluid", !shouldRayTraceFluid(stack));
        player.sendStatusMessage(new TextComponentString(TextFormatting.AQUA + new TextComponentTranslation("message.gadget.raytrace_fluid").getUnformattedComponentText() + ": " + shouldRayTraceFluid(stack)), true);
    }

    public static void addInformationRayTraceFluid(List<String> tooltip, ItemStack stack) {
        tooltip.add(TextFormatting.BLUE + I18n.format("tooltip.gadget.raytrace_fluid") + ": " + shouldRayTraceFluid(stack));
    }

    public static class EmitEvent {
        protected static boolean breakBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
            BlockEvent.BreakEvent breakEvent = new BlockEvent.BreakEvent(world, pos, state, player);
            MinecraftForge.EVENT_BUS.post(breakEvent);

            return !breakEvent.isCanceled();
        }

        protected static boolean placeBlock(EntityPlayer player, BlockSnapshot snapshot, EnumFacing facing, EnumHand hand) {
            BlockEvent.PlaceEvent event = ForgeEventFactory.onPlayerBlockPlace(
                    player,
                    snapshot,
                    facing,
                    hand
            );

            return !event.isCanceled();
        }
    }
}
