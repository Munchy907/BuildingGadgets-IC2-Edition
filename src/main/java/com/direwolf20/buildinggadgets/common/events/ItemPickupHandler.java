package com.direwolf20.buildinggadgets.common.events;

import com.direwolf20.buildinggadgets.common.items.pastes.ConstructionPaste;
import com.direwolf20.buildinggadgets.common.tools.InventoryManipulation;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.PlaySoundSourceEvent;
import net.minecraftforge.client.event.sound.SoundEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/*@EventBusSubscriber
public class ItemPickupHandler {
    @SubscribeEvent
    public static void GetDrops(EntityItemPickupEvent event) {
        EntityItem entityItem = event.getItem();
        ItemStack itemStack = entityItem.getItem();
        EntityPlayer player = event.getEntityPlayer();
        World world = player.world;
        if (itemStack.getItem() instanceof ConstructionPaste) {
            itemStack = InventoryManipulation.addPasteToContainer(event.getEntityPlayer(), itemStack);
            entityItem.setItem(itemStack);

            event.getEntityPlayer().world.playSound(null, player.posX, player.posY + 0.5, player.posZ,
                    SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }
    }
}*/
