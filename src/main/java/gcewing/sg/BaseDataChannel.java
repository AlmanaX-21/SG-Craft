package gcewing.sg;

import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.FMLOutboundHandler.OutboundTarget;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;
import java.io.DataInput;
import java.io.DataOutput;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;

public class BaseDataChannel {
   public String name;
   public List handlers = new ArrayList();
   protected EnumMap<Side, FMLEmbeddedChannel> pipes;

   public BaseDataChannel(String name, Object... handlers) {
      this.name = name;
      ChannelHandler handler = new DataHandler(this);
      this.pipes = NetworkRegistry.INSTANCE.newChannel(name, new ChannelHandler[]{handler});
      this.handlers.add(this);

      for(Object h : handlers) {
         this.handlers.add(h);
      }

   }

   protected ChannelOutput openTarget(String message, Side fromSide, FMLOutboundHandler.OutboundTarget target) {
      return this.openTarget(message, fromSide, target, (Object)null);
   }

   protected ChannelOutput openTarget(String message, Side fromSide, FMLOutboundHandler.OutboundTarget target, Object arg) {
      ChannelOutput out = new DataPacket(this, fromSide, target, arg);
      out.writeUTF(message);
      return out;
   }

   public ChannelOutput openServer(String message) {
      return this.openTarget(message, Side.CLIENT, OutboundTarget.TOSERVER);
   }

   public ChannelOutput openPlayer(EntityPlayer player, String message) {
      return this.openTarget(message, Side.SERVER, OutboundTarget.PLAYER, player);
   }

   public ChannelOutput openAllPlayers(String message) {
      return this.openTarget(message, Side.SERVER, OutboundTarget.ALL);
   }

   public ChannelOutput openAllAround(NetworkRegistry.TargetPoint point, String message) {
      return this.openTarget(message, Side.SERVER, OutboundTarget.ALLAROUNDPOINT, point);
   }

   public ChannelOutput openDimension(int dimensionId, String message) {
      return this.openTarget(message, Side.SERVER, OutboundTarget.DIMENSION, dimensionId);
   }

   public ChannelOutput openServerContainer(String message) {
      ChannelOutput out = this.openServer(".container.");
      out.writeUTF(message);
      return out;
   }

   public ChannelOutput openClientContainer(EntityPlayer player, String message) {
      ChannelOutput out = this.openPlayer(player, ".container.");
      out.writeUTF(message);
      return out;
   }

   @BaseDataChannel.ServerMessageHandler(".container.")
   public void onServerContainerMessage(EntityPlayer player, ChannelInput data) {
      String message = data.readUTF();
      doServerDispatch(player.openContainer, message, player, data);
   }

   @SideOnly(Side.CLIENT)
   @BaseDataChannel.ClientMessageHandler(".container.")
   public void onClientContainerMessage(ChannelInput data) {
      EntityPlayer player = Minecraft.getMinecraft().thePlayer;
      String message = data.readUTF();
      doClientDispatch(player.openContainer, message, data);
   }

   protected void onReceiveFromClient(EntityPlayer player, ChannelInput data) {
      String message = data.readUTF();

      for(Object h : this.handlers) {
         if (serverDispatch(h, message, player, data)) {
            return;
         }
      }

   }

   public static void doServerDispatch(Object handler, String message, EntityPlayer player, ChannelInput data) {
   }

   public static boolean serverDispatch(Object handler, String message, EntityPlayer player, ChannelInput data) {
      if (handler != null) {
         Method meth = BaseDataChannel.HandlerMap.SERVER.get(handler, message);
         if (meth != null) {
            try {
               meth.invoke(handler, player, data);
               return true;
            } catch (Exception e) {
               throw new RuntimeException(String.format("Exception while calling server-side handler %s.%s for message %s", handler.getClass().getName(), meth.getName(), message), e);
            }
         }
      }

      return false;
   }

   protected void onReceiveFromServer(ChannelInput data) {
      String message = data.readUTF();

      for(Object h : this.handlers) {
         if (clientDispatch(h, message, data)) {
            return;
         }
      }

   }

   public static void doClientDispatch(Object handler, String message, ChannelInput data) {
   }

   public static boolean clientDispatch(Object handler, String message, ChannelInput data) {
      if (handler != null) {
         Method meth = BaseDataChannel.HandlerMap.CLIENT.get(handler, message);
         if (meth != null) {
            try {
               meth.invoke(handler, data);
               return true;
            } catch (Exception e) {
               throw new RuntimeException(String.format("Exception while calling client-side handler %s.%s for message %s", handler.getClass().getName(), meth.getName(), message), e);
            }
         }
      }

      return false;
   }

   protected static enum HandlerMap {
      SERVER(ServerMessageHandler.class) {
         protected String annotationValue(Object a) {
            return ((ServerMessageHandler)a).value();
         }
      },
      CLIENT(ClientMessageHandler.class) {
         protected String annotationValue(Object a) {
            return ((ClientMessageHandler)a).value();
         }
      };

      protected Class type;
      protected ClassCache classCache;

      private HandlerMap(Class type) {
         this.classCache = new ClassCache();
         this.type = type;
      }

      protected abstract String annotationValue(Object var1);

      public Method get(Object handler, String message) {
         Class cls = handler.getClass();
         MethodCache cache = this.classCache.get(cls);
         Method meth = (Method)cache.get(message);
         if (meth == null) {
            for(Method m : cls.getMethods()) {
               Object a = m.getAnnotation(this.type);
               if (a != null && this.annotationValue(a).equals(message)) {
                  cache.put(message, m);
                  meth = m;
                  break;
               }
            }
         }

         return meth;
      }
   }

   protected static class MethodCache extends HashMap<String, Method> {
   }

   protected static class ClassCache extends HashMap<Class, MethodCache> {
      public MethodCache get(Class key) {
         MethodCache result = (MethodCache)super.get(key);
         if (result == null) {
            result = new MethodCache();
            this.put(key, result);
         }

         return result;
      }
   }

   static class ChannelInputStream extends ByteBufInputStream implements ChannelInput {
      public ChannelInputStream(ByteBuf buf) {
         super(buf);
      }

      public boolean readBoolean() {
         try {
            return super.readBoolean();
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      public byte readByte() {
         try {
            return super.readByte();
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      public char readChar() {
         try {
            return super.readChar();
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      public double readDouble() {
         try {
            return super.readDouble();
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      public float readFloat() {
         try {
            return super.readFloat();
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      public void readFully(byte[] b) {
         try {
            super.readFully(b);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      public void readFully(byte[] b, int off, int len) {
         try {
            super.readFully(b, off, len);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      public int readInt() {
         try {
            return super.readInt();
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      public String readLine() {
         try {
            return super.readLine();
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      public long readLong() {
         try {
            return super.readLong();
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      public short readShort() {
         try {
            return super.readShort();
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      public int readUnsignedByte() {
         try {
            return super.readUnsignedByte();
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      public int readUnsignedShort() {
         try {
            return super.readUnsignedShort();
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      public String readUTF() {
         try {
            return super.readUTF();
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      public int skipBytes(int n) {
         try {
            return super.skipBytes(n);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }
   }

   static class DataPacket implements ChannelOutput {
      ByteBufOutputStream out = new ByteBufOutputStream(Unpooled.buffer());
      BaseDataChannel channel;
      Side side;
      FMLOutboundHandler.OutboundTarget target;
      Object arg;

      DataPacket(BaseDataChannel channel, Side side, FMLOutboundHandler.OutboundTarget target, Object arg) {
         this.channel = channel;
         this.side = side;
         this.target = target;
         this.arg = arg;
      }

      public void write(byte[] b) {
         try {
            this.out.write(b);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      public void write(byte[] b, int off, int len) {
         try {
            this.out.write(b, off, len);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      public void write(int b) {
         try {
            this.out.write(b);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      public void writeBoolean(boolean v) {
         try {
            this.out.writeBoolean(v);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      public void writeByte(int v) {
         try {
            this.out.writeByte(v);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      public void writeBytes(String s) {
         try {
            this.out.writeBytes(s);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      public void writeChar(int v) {
         try {
            this.out.writeChar(v);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      public void writeChars(String s) {
         try {
            this.out.writeChars(s);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      public void writeDouble(double v) {
         try {
            this.out.writeDouble(v);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      public void writeFloat(float v) {
         try {
            this.out.writeFloat(v);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      public void writeInt(int v) {
         try {
            this.out.writeInt(v);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      public void writeLong(long v) {
         try {
            this.out.writeLong(v);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      public void writeShort(int v) {
         try {
            this.out.writeShort(v);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      public void writeUTF(String s) {
         try {
            this.out.writeUTF(s);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      public void close() {
         try {
            this.out.close();
         } catch (Exception e) {
            throw new RuntimeException(e);
         }

         ByteBuf payload = this.out.buffer();
         Packet pkt = new FMLProxyPacket(new PacketBuffer(payload), this.channel.name);
         FMLEmbeddedChannel pipe = (FMLEmbeddedChannel)this.channel.pipes.get(this.side);
         pipe.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(this.target);
         pipe.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(this.arg);
         pipe.writeAndFlush(pkt).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
      }
   }

   @Sharable
   protected static class DataHandler extends ChannelInboundHandlerAdapter {
      BaseDataChannel channel;

      DataHandler(BaseDataChannel channel) {
         this.channel = channel;
      }

      public void channelRead(ChannelHandlerContext ctx, Object obj) throws Exception {
         if (obj instanceof FMLProxyPacket) {
            this.handleProxyPacket(ctx, (FMLProxyPacket)obj);
         }

      }

      protected void handleProxyPacket(ChannelHandlerContext ctx, FMLProxyPacket msg) {
         ChannelInput data = new ChannelInputStream(msg.payload());
         if (ctx.channel() == this.channel.pipes.get(Side.SERVER)) {
            INetHandler net = (INetHandler)ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
            EntityPlayer player = ((NetHandlerPlayServer)net).playerEntity;
            this.channel.onReceiveFromClient(player, data);
         } else {
            this.channel.onReceiveFromServer(data);
         }

      }
   }

   public interface ChannelInput extends DataInput {
      boolean readBoolean();

      byte readByte();

      char readChar();

      double readDouble();

      float readFloat();

      void readFully(byte[] var1);

      void readFully(byte[] var1, int var2, int var3);

      int readInt();

      String readLine();

      long readLong();

      short readShort();

      int readUnsignedByte();

      int readUnsignedShort();

      String readUTF();

      int skipBytes(int var1);
   }

   public interface ChannelOutput extends DataOutput {
      void write(byte[] var1);

      void write(byte[] var1, int var2, int var3);

      void write(int var1);

      void writeBoolean(boolean var1);

      void writeByte(int var1);

      void writeBytes(String var1);

      void writeChar(int var1);

      void writeChars(String var1);

      void writeDouble(double var1);

      void writeFloat(float var1);

      void writeInt(int var1);

      void writeLong(long var1);

      void writeShort(int var1);

      void writeUTF(String var1);

      void close();
   }

   @Retention(RetentionPolicy.RUNTIME)
   public @interface ClientMessageHandler {
      String value();
   }

   @Retention(RetentionPolicy.RUNTIME)
   public @interface ServerMessageHandler {
      String value();
   }
}
