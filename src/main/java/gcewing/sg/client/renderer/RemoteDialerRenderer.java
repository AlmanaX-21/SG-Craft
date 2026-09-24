package gcewing.sg.client.renderer;

import gcewing.sg.SGCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

public class RemoteDialerRenderer implements IItemRenderer {
   protected static final IModelCustom remoteDialerModel;

   public boolean handleRenderType(ItemStack item, IItemRenderer.ItemRenderType type) {
      return true;
   }

   public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack item, IItemRenderer.ItemRendererHelper helper) {
      return true;
   }

   public void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data) {
      if (type == ItemRenderType.EQUIPPED) {
         GL11.glPushMatrix();
         GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
         GL11.glTranslatef(-0.5F, 0.45F, 0.8F);
         GL11.glScalef(0.4F, 0.4F, 0.4F);
         Minecraft.getMinecraft().renderEngine.bindTexture(SGCraft.mod.resourceLocation("textures/tileentity/stargate3/universe_dialer.jpg"));
         remoteDialerModel.renderAll();
         GL11.glPopMatrix();
      } else if (type != ItemRenderType.INVENTORY && type != ItemRenderType.ENTITY) {
         if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            this.renderEquipped(item);
         } else if (type == ItemRenderType.FIRST_PERSON_MAP) {
            GL11.glPushMatrix();
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(-0.5F, 0.45F, 0.8F);
            GL11.glScalef(0.4F, 0.4F, 0.4F);
            Minecraft.getMinecraft().renderEngine.bindTexture(SGCraft.mod.resourceLocation("textures/tileentity/stargate3/universe_dialer.jpg"));
            remoteDialerModel.renderAll();
            GL11.glPopMatrix();
         }
      } else {
         GL11.glPushMatrix();
         GL11.glScalef(0.4F, 0.4F, 0.4F);
         Minecraft.getMinecraft().renderEngine.bindTexture(SGCraft.mod.resourceLocation("textures/tileentity/stargate3/universe_dialer.jpg"));
         remoteDialerModel.renderAll();
         GL11.glPopMatrix();
      }

   }

   protected void renderEquipped(ItemStack item) {
      boolean isHeld = this.isHeld(item);
      if (!isHeld) {
         GL11.glPushMatrix();
         GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);
         GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
         GL11.glTranslatef(0.0F, 0.5F, -0.5F);
         GL11.glScalef(0.4F, 0.4F, 0.4F);
         Minecraft.getMinecraft().renderEngine.bindTexture(SGCraft.mod.resourceLocation("textures/tileentity/stargate3/universe_dialer.jpg"));
         remoteDialerModel.renderAll();
         GL11.glPopMatrix();
      } else {
         float progress = this.getProgress(item.getTagCompound().getBoolean("held"), item);
         GL11.glPushMatrix();
         GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);
         GL11.glRotatef(45.0F + progress * 45.0F, 1.0F, 0.0F, 0.0F);
         GL11.glTranslatef(-0.75F * progress, 0.5F + 0.3F * progress, -0.5F - 1.3F * progress);
         GL11.glScalef(0.4F, 0.4F, 0.4F);
         Minecraft.getMinecraft().renderEngine.bindTexture(SGCraft.mod.resourceLocation("textures/tileentity/stargate3/universe_dialer.jpg"));
         remoteDialerModel.renderAll();
         GL11.glPopMatrix();
      }

   }

   protected float getProgress(boolean held, ItemStack item) {
      NBTTagCompound nbt = item.getTagCompound();
      long startTime = nbt.getLong("time");
      float currentTime = (float)Minecraft.getMinecraft().thePlayer.ticksExisted;
      if (currentTime - (float)startTime > 15.0F && !held) {
         item.getTagCompound().removeTag("time");
      }

      float progress = Math.max(0.0F, Math.min(1.0F, (currentTime - (float)startTime) / 15.0F));
      if (!held) {
         progress = 1.0F - progress;
      }

      return progress;
   }

   protected boolean isHeld(ItemStack item) {
      if (item.getTagCompound() != null) {
         NBTTagCompound nbt = item.getTagCompound();
         return nbt.getBoolean("held") || nbt.hasKey("time");
      }

      return false;
   }

   static {
      remoteDialerModel = AdvancedModelLoader.loadModel(SGCraft.mod.resourceLocation("models/item/universe_dialer.obj"));
   }
}
