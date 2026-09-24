package gcewing.sg;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import gcewing.sg.client.renderer.RemoteDialerRenderer;
import gcewing.sg.client.renderer.RingBaseRenderer;
import gcewing.sg.client.renderer.RingControllerTESR;
import gcewing.sg.client.renderer.SGBaseTERenderer;
import gcewing.sg.gui.DHDFuelScreen;
import gcewing.sg.gui.DHDScreen;
import gcewing.sg.gui.SGRemoteDialerScreen;
import gcewing.sg.te.RingControllerTileEntity;
import gcewing.sg.te.RingTileEntity;
import gcewing.sg.te.RingTileEntityFlat;
import gcewing.sg.te.RingTileEntityUp;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;

public class SGCraftClient extends BaseModClient<SGCraft> {
   private int wormholeTicks;
   public SGCraftClient(SGCraft mod) {
      super(mod);
   }

   protected void registerScreens() {
      this.addScreen(SGGui.SGBase, SGBaseScreen.class);
      this.addScreen(SGGui.SGController, DHDScreen.class);
      this.addScreen(SGGui.DHDFuel, DHDFuelScreen.class);
      this.addScreen(SGGui.PowerUnit, PowerScreen.class);
      this.addScreen(SGGui.SG_REMOTE_DIALER, SGRemoteDialerScreen.class);
   }

   protected void registerTileEntityRenderers() {
      this.addTileEntityRenderer(SGBaseTE.class, new SGBaseTERenderer());
      this.addTileEntityRenderer(DHDTE.class, new DHDTERenderer());
      this.addTileEntityRenderer(RingTileEntity.class, new RingBaseRenderer());
      this.addTileEntityRenderer(RingTileEntityUp.class, new RingBaseRenderer());
      this.addTileEntityRenderer(RingTileEntityFlat.class, new RingBaseRenderer());
      this.addTileEntityRenderer(RingControllerTileEntity.class, new RingControllerTESR());
   }

   protected void registerEntityRenderers() {
      this.addEntityRenderer(IrisEntity.class, IrisRenderer.class);
   }

   protected void registerItemRenderers() {
      super.registerItemRenderers();
      MinecraftForgeClient.registerItemRenderer(SGCraft.remoteDialer, new RemoteDialerRenderer());
   }

   public float getTime() {
      return (float)Minecraft.getMinecraft().thePlayer.ticksExisted;
   }

   @SubscribeEvent
   public void onClientTick(TickEvent.ClientTickEvent event) {
      if (event.phase == TickEvent.Phase.END) {
         Integer start;
         while ((start = SGChannel.pollWormholeStart()) != null) {
            wormholeTicks = start;
         }
         if (Minecraft.getMinecraft().thePlayer == null) {
            wormholeTicks = 0;
         } else if (wormholeTicks > 0) {
            wormholeTicks--;
         }
      }
   }

   @SubscribeEvent
   public void onWormholeOverlay(RenderGameOverlayEvent.Post event) {
      if (event.type != RenderGameOverlayEvent.ElementType.ALL || wormholeTicks <= 0) {
         return;
      }
      int width = event.resolution.getScaledWidth();
      int height = event.resolution.getScaledHeight();
      float progress = (50.0F - wormholeTicks) / 50.0F;
      float crop = 0.5F / (1.0F + progress * 1.5F);
      float opacity = Math.min(1.0F, wormholeTicks / 6.0F);
      GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
      GL11.glDisable(GL11.GL_DEPTH_TEST);
      GL11.glDepthMask(false);
      GL11.glEnable(GL11.GL_BLEND);
      GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
      Minecraft.getMinecraft().renderEngine.bindTexture(base.resourceLocation("textures/gui/wormhole_tunnel.png"));
      Tessellator draw = Tessellator.instance;
      draw.startDrawingQuads();
      draw.setColorRGBA_F(1.0F, 1.0F, 1.0F, opacity);
      draw.addVertexWithUV(0, height, -90, 0.5F - crop, 0.5F + crop);
      draw.addVertexWithUV(width, height, -90, 0.5F + crop, 0.5F + crop);
      draw.addVertexWithUV(width, 0, -90, 0.5F + crop, 0.5F - crop);
      draw.addVertexWithUV(0, 0, -90, 0.5F - crop, 0.5F - crop);
      draw.draw();
      GL11.glPopAttrib();
   }
}
