package gcewing.sg;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import gcewing.sg.client.renderer.RemoteDialerRenderer;
import gcewing.sg.client.renderer.RingBaseRenderer;
import gcewing.sg.client.renderer.RingControllerTESR;
import gcewing.sg.client.renderer.SGBaseTERenderer;
import gcewing.sg.client.renderer.WormholeVideo;
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
   private WormholeVideo video;
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
         if (wormholeTicks > 0) {
            wormholeTicks--;
         }
         Integer start;
         while ((start = SGChannel.pollWormholeStart()) != null) {
            if (video != null) {
               video.stop();
            }
            wormholeTicks = start;
            video = start > 0 ? WormholeVideo.play() : null;
         }
         if (Minecraft.getMinecraft().thePlayer == null) {
            wormholeTicks = 0;
         }
         if (wormholeTicks == 0 && video != null) {
            video.stop();
            video = null;
         }
      }
   }

   @SubscribeEvent
   public void onWormholeOverlay(RenderGameOverlayEvent.Post event) {
      if (event.type != RenderGameOverlayEvent.ElementType.ALL || video == null) {
         return;
      }
      if (!video.bind((SGWormholeTravel.TICKS - wormholeTicks + event.partialTicks) / 20.0)) {
         return;
      }
      int width = event.resolution.getScaledWidth();
      int height = event.resolution.getScaledHeight();
      float opacity = Math.min(1.0F, wormholeTicks / 6.0F);
      GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
      GL11.glDisable(GL11.GL_DEPTH_TEST);
      GL11.glDepthMask(false);
      GL11.glEnable(GL11.GL_BLEND);
      GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
      Tessellator draw = Tessellator.instance;
      draw.startDrawingQuads();
      draw.setColorRGBA_F(1.0F, 1.0F, 1.0F, opacity);
      draw.addVertexWithUV(0, height, -90, 0, 1);
      draw.addVertexWithUV(width, height, -90, 1, 1);
      draw.addVertexWithUV(width, 0, -90, 1, 0);
      draw.addVertexWithUV(0, 0, -90, 0, 0);
      draw.draw();
      GL11.glPopAttrib();
   }
}
