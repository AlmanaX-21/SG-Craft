package gcewing.sg;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class BaseGui {
   public static final int defaultTextColor = 4210752;

   static boolean isFocused(IWidget widget) {
      if (widget == null) {
         return false;
      } else if (widget instanceof Root) {
         return true;
      } else {
         IWidgetContainer parent = widget.parent();
         return parent != null && parent.getFocus() == widget && isFocused(parent);
      }
   }

   static void tellFocusChanged(IWidget widget, boolean state) {
      if (widget != null) {
         widget.focusChanged(state);
         if (widget instanceof IWidgetContainer) {
            tellFocusChanged(((IWidgetContainer)widget).getFocus(), state);
         }
      }

   }

   static String name(Object obj) {
      return obj != null ? obj.getClass().getSimpleName() : "null";
   }

   public static Ref ref(Object target, String name) {
      return new FieldRef(target, name);
   }

   public static Ref ref(Object target, String getterName, String setterName) {
      return new PropertyRef(target, getterName, setterName);
   }

   public static Action action(Object target, String name) {
      return new MethodAction(target, name);
   }

   public static class Screen extends GuiContainer implements BaseMod.ISetMod {
      static final int defaultTextColor = 4210752;
      protected BaseMod mod;
      protected Root root;
      protected String title;
      protected IWidget mouseWidget;
      protected GState gstate;

      public Screen(Container container, int width, int height) {
         super(container);
         this.xSize = width;
         this.ySize = height;
         this.root = new Root(this);
         this.initGraphics();
      }

      public Screen(BaseContainer container) {
         this(container, container.xSize, container.ySize);
      }

      public Container getContainer() {
         return this.inventorySlots;
      }

      public int getWidth() {
         return this.xSize;
      }

      public int getHeight() {
         return this.ySize;
      }

      public void setMod(BaseMod mod) {
         this.mod = mod;
      }

      public void initGui() {
         super.initGui();
         this.root.layout();
      }

      protected void initGraphics() {
         this.gstate = new GState();
      }

      protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
         GL11.glPushMatrix();
         GL11.glTranslatef((float)this.guiLeft, (float)this.guiTop, 0.0F);
         this.initGraphics();
         this.drawBackgroundLayer();
         if (this.title != null) {
            this.drawTitle(this.title);
         }

         this.root.draw(this, mouseX - this.guiLeft, mouseY - this.guiTop);
         GL11.glPopMatrix();
      }

      protected void drawBackgroundLayer() {
         this.initGraphics();
         this.drawGuiBackground((double)0.0F, (double)0.0F, (double)this.xSize, (double)this.ySize);
      }

      protected void drawGuiContainerForegroundLayer(int par1, int par2) {
         this.drawForegroundLayer();
      }

      protected void drawForegroundLayer() {
      }

      public void close() {
         this.dispatchClosure(this.root);
         this.onClose();
         this.mc.thePlayer.closeScreen();
      }

      protected void onClose() {
      }

      public void bindTexture(String path) {
         this.bindTexture((String)path, 1, 1);
      }

      public void bindTexture(String path, int usize, int vsize) {
         this.bindTexture(this.mod.client.textureLocation(path), usize, vsize);
      }

      public void bindTexture(ResourceLocation rsrc) {
         this.bindTexture((ResourceLocation)rsrc, 1, 1);
      }

      public void bindTexture(ResourceLocation rsrc, int usize, int vsize) {
         this.gstate.texture = rsrc;
         this.mc.getTextureManager().bindTexture(rsrc);
         this.gstate.uscale = (double)1.0F / (double)usize;
         this.gstate.vscale = (double)1.0F / (double)vsize;
      }

      public void gSave() {
         this.gstate = new GState(this.gstate);
      }

      public void gRestore() {
         if (this.gstate.previous != null) {
            this.gstate = this.gstate.previous;
            this.mc.getTextureManager().bindTexture(this.gstate.texture);
         }

      }

      public void drawRect(double x, double y, double w, double h) {
         GL11.glDisable(3553);
         GL11.glColor3d((double)this.gstate.red, (double)this.gstate.green, (double)this.gstate.blue);
         GL11.glBegin(7);
         GL11.glVertex3d(x, y + h, (double)this.zLevel);
         GL11.glVertex3d(x + w, y + h, (double)this.zLevel);
         GL11.glVertex3d(x + w, y, (double)this.zLevel);
         GL11.glVertex3d(x, y, (double)this.zLevel);
         GL11.glEnd();
         GL11.glEnable(3553);
      }

      public void drawBorderedRect(double x, double y, double w, double h, double u, double v, double uSize, double vSize, double cornerWidth, double cornerHeight) {
         double sw = w - (double)2.0F * cornerWidth;
         double sh = h - (double)2.0F * cornerHeight;
         double usw = uSize - (double)2.0F * cornerWidth;
         double ush = vSize - (double)2.0F * cornerHeight;
         double x1 = x + cornerWidth;
         double x2 = w - cornerWidth;
         double y1 = y + cornerHeight;
         double y2 = h - cornerHeight;
         double u1 = u + cornerWidth;
         double u2 = u + uSize - cornerWidth;
         double v1 = v + cornerHeight;
         double v2 = v + vSize - cornerHeight;
         this.drawTexturedRect(x, y, cornerWidth, cornerHeight, u, v);
         this.drawTexturedRect(x2, y, cornerWidth, cornerHeight, u2, v);
         this.drawTexturedRect(x, y2, cornerWidth, cornerHeight, u, v2);
         this.drawTexturedRect(x2, y2, cornerWidth, cornerHeight, u2, v2);
         this.drawTexturedRect(x1, y, sw, cornerHeight, u1, v, usw, cornerHeight);
         this.drawTexturedRect(x1, y2, sw, cornerHeight, u1, v2, usw, cornerHeight);
         this.drawTexturedRect(x, y1, cornerWidth, sh, u, v1, cornerWidth, ush);
         this.drawTexturedRect(x2, y1, cornerWidth, sh, u2, v1, cornerWidth, ush);
         this.drawTexturedRect(x1, y1, sw, sh, u1, v1, usw, ush);
      }

      public void drawGuiBackground(double x, double y, double w, double h) {
         this.bindTexture((String)"gui/gui_background.png", 16, 16);
         this.setColor(16777215);
         this.drawBorderedRect(x, y, w, h, (double)0.0F, (double)0.0F, (double)16.0F, (double)16.0F, (double)4.0F, (double)4.0F);
      }

      public void drawTexturedRect(double x, double y, double w, double h) {
         this.drawTexturedRectUV(x, y, w, h, (double)0.0F, (double)0.0F, (double)1.0F, (double)1.0F);
      }

      public void drawTexturedRect(double x, double y, double w, double h, double u, double v) {
         this.drawTexturedRect(x, y, w, h, u, v, w, h);
      }

      public void drawTexturedRect(double x, double y, double w, double h, double u, double v, double us, double vs) {
         this.drawTexturedRectUV(x, y, w, h, u * this.gstate.uscale, v * this.gstate.vscale, us * this.gstate.uscale, vs * this.gstate.vscale);
      }

      public void drawTexturedRectUV(double x, double y, double w, double h, double u, double v, double us, double vs) {
         GL11.glBegin(7);
         GL11.glColor3f(this.gstate.red, this.gstate.green, this.gstate.blue);
         GL11.glTexCoord2d(u, v + vs);
         GL11.glVertex3d(x, y + h, (double)this.zLevel);
         GL11.glTexCoord2d(u + us, v + vs);
         GL11.glVertex3d(x + w, y + h, (double)this.zLevel);
         GL11.glTexCoord2d(u + us, v);
         GL11.glVertex3d(x + w, y, (double)this.zLevel);
         GL11.glTexCoord2d(u, v);
         GL11.glVertex3d(x, y, (double)this.zLevel);
         GL11.glEnd();
      }

      public void setColor(int hex) {
         this.setColor((double)(hex >> 16) / (double)255.0F, (double)(hex >> 8 & 255) / (double)255.0F, (double)(hex & 255) / (double)255.0F);
      }

      public void setColor(double r, double g, double b) {
         this.gstate.red = (float)r;
         this.gstate.green = (float)g;
         this.gstate.blue = (float)b;
      }

      public void resetColor() {
         this.setColor((double)1.0F, (double)1.0F, (double)1.0F);
      }

      public void setTextColor(int hex) {
         this.gstate.textColor = hex;
      }

      public void setTextColor(double red, double green, double blue) {
         this.setTextColor(BaseUtils.packedColor(red, green, blue));
      }

      public void setTextShadow(boolean state) {
         this.gstate.textShadow = state;
      }

      public void drawString(String s, int x, int y) {
         this.fontRendererObj.drawString(s, x, y, this.gstate.textColor, this.gstate.textShadow);
      }

      public void drawCenteredString(String s, int x, int y) {
         this.fontRendererObj.drawString(s, x - this.fontRendererObj.getStringWidth(s) / 2, y, this.gstate.textColor, this.gstate.textShadow);
      }

      public void drawRightAlignedString(String s, int x, int y) {
         this.fontRendererObj.drawString(s, x - this.fontRendererObj.getStringWidth(s), y, this.gstate.textColor, this.gstate.textShadow);
      }

      public void drawTitle(String s) {
         this.drawCenteredString(s, this.xSize / 2, 4);
      }

      public void drawInventoryName(IInventory inv, int x, int y) {
         this.drawString(inventoryName(inv), x, y);
      }

      public void drawPlayerInventoryName() {
         this.drawString(playerInventoryName(), 8, this.ySize - 96 + 2);
      }

      public static String inventoryName(IInventory inv) {
         String name = inv.getInventoryName();
         if (!inv.hasCustomInventoryName()) {
            name = StatCollector.translateToLocal(name);
         }

         return name;
      }

      public static String playerInventoryName() {
         return StatCollector.translateToLocal("container.inventory");
      }

      protected void mouseMovedOrUp(int x, int y, int button) {
         super.mouseMovedOrUp(x, y, button);
         if (this.mouseWidget != null) {
            MouseCoords m = new MouseCoords(this.mouseWidget, x, y);
            if (button == -1) {
               this.mouseWidget.mouseMoved(m);
            } else {
               this.mouseWidget.mouseReleased(m, button);
               this.mouseWidget = null;
            }
         }

      }

      public void mouseClicked(int x, int y, int button) {
         super.mouseClicked(x, y, button);
         this.mousePressed(x - this.guiLeft, y - this.guiTop, button);
      }

      protected void mousePressed(int x, int y, int button) {
         this.mouseWidget = this.root.dispatchMousePress(x, y, button);
         if (this.mouseWidget != null) {
            this.closeOldFocus(this.mouseWidget);
            this.focusOn(this.mouseWidget);
            this.mouseWidget.mousePressed(new MouseCoords(this.mouseWidget, x, y), button);
         }

      }

      void closeOldFocus(IWidget clickedWidget) {
         if (!BaseGui.isFocused(clickedWidget)) {
            IWidgetContainer parent;
            for(parent = clickedWidget.parent(); !BaseGui.isFocused(parent); parent = parent.parent()) {
            }

            this.dispatchClosure(parent.getFocus());
         }

      }

      void dispatchClosure(IWidget target) {
         while(target != null) {
            target.close();
            target = this.getFocusOf(target);
         }

      }

      IWidget getFocusOf(IWidget widget) {
         return widget instanceof IWidgetContainer ? ((IWidgetContainer)widget).getFocus() : null;
      }

      public void keyTyped(char c, int key) {
         if (!this.root.dispatchKeyPress(c, key)) {
            if (key != 1 && key != this.mc.gameSettings.keyBindInventory.getKeyCode()) {
               super.keyTyped(c, key);
            } else {
               this.close();
            }
         }

      }

      public void focusOn(IWidget newFocus) {
         IWidgetContainer parent = newFocus.parent();
         if (parent != null) {
            IWidget oldFocus = parent.getFocus();
            if (BaseGui.isFocused(parent)) {
               if (oldFocus != newFocus) {
                  BaseGui.tellFocusChanged(oldFocus, false);
                  parent.setFocus(newFocus);
                  BaseGui.tellFocusChanged(newFocus, true);
               }
            } else {
               parent.setFocus(newFocus);
               this.focusOn(parent);
            }
         }

      }

      public void focusChanged(boolean state) {
      }
   }

   public static class MouseCoords {
      int x;
      int y;

      public MouseCoords(IWidget widget, int x, int y) {
         while(widget != null) {
            x -= widget.left();
            y -= widget.top();
            widget = widget.parent();
         }

         this.x = x;
         this.y = y;
      }
   }

   public static class Widget implements IWidget {
      public IWidgetContainer parent;
      public int left;
      public int top;
      public int width;
      public int height;

      public Widget() {
      }

      public Widget(int width, int height) {
         this.width = width;
         this.height = height;
      }

      public IWidgetContainer parent() {
         return this.parent;
      }

      public void setParent(IWidgetContainer widget) {
         this.parent = widget;
      }

      public int left() {
         return this.left;
      }

      public int top() {
         return this.top;
      }

      public int width() {
         return this.width;
      }

      public int height() {
         return this.height;
      }

      public void setLeft(int x) {
         this.left = x;
      }

      public void setTop(int y) {
         this.top = y;
      }

      public void draw(Screen scr, int mouseX, int mouseY) {
      }

      public void mousePressed(MouseCoords m, int button) {
      }

      public void mouseMoved(MouseCoords m) {
      }

      public void mouseReleased(MouseCoords m, int button) {
      }

      public boolean keyPressed(char c, int key) {
         return false;
      }

      public void focusChanged(boolean state) {
      }

      public void close() {
      }

      public void layout() {
      }

      public IWidget dispatchMousePress(int x, int y, int button) {
         return this;
      }

      public boolean dispatchKeyPress(char c, int key) {
         return this.keyPressed(c, key);
      }

      public static int stringWidth(String s) {
         return Minecraft.getMinecraft().fontRenderer.getStringWidth(s);
      }

      public void addPopup(int x, int y, IWidget widget) {
         IWidget w;
         for(w = this; !(w instanceof Root); w = w.parent()) {
            x += w.left();
            y += w.top();
         }

         ((Root)w).addPopup(x, y, widget);
      }

      public void removePopup() {
         Root root = this.getRoot();
         root.remove(this);
      }

      public Root getRoot() {
         IWidget w;
         for(w = this; w != null && !(w instanceof Root); w = w.parent()) {
         }

         return (Root)w;
      }
   }

   public static class Group extends Widget implements IWidgetContainer {
      protected List<IWidget> widgets = new ArrayList();
      protected IWidget focus;

      public IWidget getFocus() {
         return this.focus;
      }

      public void setFocus(IWidget widget) {
         this.focus = widget;
      }

      public void add(int left, int top, IWidget widget) {
         widget.setLeft(left);
         widget.setTop(top);
         widget.setParent(this);
         this.widgets.add(widget);
      }

      public void remove(IWidget widget) {
         this.widgets.remove(widget);
         if (this.getFocus() == widget) {
            if (BaseGui.isFocused(this)) {
               BaseGui.tellFocusChanged(widget, false);
            }

            this.setFocus((IWidget)null);
         }

      }

      public void draw(Screen scr, int mouseX, int mouseY) {
         super.draw(scr, mouseX, mouseY);

         for(IWidget w : this.widgets) {
            int dx = w.left();
            int dy = w.top();
            GL11.glPushMatrix();
            GL11.glTranslated((double)dx, (double)dy, (double)0.0F);
            w.draw(scr, mouseX - dx, mouseY - dy);
            GL11.glPopMatrix();
         }

      }

      public IWidget dispatchMousePress(int x, int y, int button) {
         IWidget target = this.findWidget(x, y);
         return (IWidget)(target != null ? target.dispatchMousePress(x - target.left(), y - target.top(), button) : this);
      }

      public boolean dispatchKeyPress(char c, int key) {
         IWidget focus = this.getFocus();
         return focus != null && focus.dispatchKeyPress(c, key) ? true : super.dispatchKeyPress(c, key);
      }

      public IWidget findWidget(int x, int y) {
         for(int i = this.widgets.size() - 1; i >= 0; --i) {
            IWidget w = (IWidget)this.widgets.get(i);
            int l = w.left();
            int t = w.top();
            if (x >= l && y >= t && x < l + w.width() && y < t + w.height()) {
               return w;
            }
         }

         return null;
      }

      public void layout() {
         for(IWidget w : this.widgets) {
            w.layout();
         }

      }
   }

   public static class Root extends Group {
      public Screen screen;
      public List<IWidget> popupStack;

      public Root(Screen screen) {
         this.screen = screen;
         this.popupStack = new ArrayList();
      }

      public int width() {
         return this.screen.getWidth();
      }

      public int height() {
         return this.screen.getHeight();
      }

      public IWidget dispatchMousePress(int x, int y, int button) {
         IWidget w = this.topPopup();
         if (w == null) {
            w = super.dispatchMousePress(x, y, button);
         }

         return w;
      }

      public void addPopup(int x, int y, IWidget widget) {
         this.add(x, y, widget);
         this.popupStack.add(widget);
         this.screen.focusOn(widget);
      }

      public void remove(IWidget widget) {
         super.remove(widget);
         this.popupStack.remove(widget);
         this.focusTopPopup();
      }

      public IWidget topPopup() {
         int n = this.popupStack.size();
         return n > 0 ? (IWidget)this.popupStack.get(n - 1) : null;
      }

      void focusTopPopup() {
         IWidget w = this.topPopup();
         if (w != null) {
            this.screen.focusOn(w);
         }

      }
   }

   public static class FieldRef implements Ref {
      public Object target;
      public Field field;

      public FieldRef(Object target, String name) {
         try {
            this.target = target;
            this.field = target.getClass().getField(name);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      public Object get() {
         try {
            return this.field.get(this.target);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      public void set(Object value) {
         try {
            this.field.set(this.target, value);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }
   }

   public static class PropertyRef implements Ref {
      public Object target;
      public Method getter;
      public Method setter;

      public PropertyRef(Object target, String getterName, String setterName) {
         this.target = target;

         try {
            Class cls = target.getClass();
            this.getter = cls.getMethod(getterName);
            this.setter = cls.getMethod(setterName, this.getter.getReturnType());
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      public Object get() {
         try {
            return this.getter.invoke(this.target);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      public void set(Object value) {
         try {
            this.setter.invoke(this.target, value);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }
   }

   public static class MethodAction implements Action {
      Object target;
      Method method;

      public MethodAction(Object target, String name) {
         try {
            this.target = target;
            this.method = target.getClass().getMethod(name);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      public void perform() {
         try {
            this.method.invoke(this.target);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }
   }

   public static class GState {
      public GState previous;
      public double uscale;
      public double vscale;
      public float red;
      public float green;
      public float blue;
      public int textColor;
      public boolean textShadow;
      public ResourceLocation texture;

      public GState() {
         this.uscale = (double)1.0F;
         this.vscale = (double)1.0F;
         this.red = this.green = this.blue = 1.0F;
         this.textColor = 4210752;
         this.textShadow = false;
      }

      public GState(GState previous) {
         this.previous = previous;
         this.uscale = previous.uscale;
         this.vscale = previous.vscale;
         this.red = previous.red;
         this.green = previous.green;
         this.blue = previous.blue;
         this.textColor = previous.textColor;
         this.textShadow = previous.textShadow;
         this.texture = previous.texture;
      }
   }

   public interface Action {
      void perform();
   }

   public interface IWidget {
      IWidgetContainer parent();

      void setParent(IWidgetContainer var1);

      int left();

      int top();

      int width();

      int height();

      void setLeft(int var1);

      void setTop(int var1);

      void draw(Screen var1, int var2, int var3);

      IWidget dispatchMousePress(int var1, int var2, int var3);

      boolean dispatchKeyPress(char var1, int var2);

      void mousePressed(MouseCoords var1, int var2);

      void mouseMoved(MouseCoords var1);

      void mouseReleased(MouseCoords var1, int var2);

      boolean keyPressed(char var1, int var2);

      void focusChanged(boolean var1);

      void close();

      void layout();
   }

   public interface IWidgetContainer extends IWidget {
      IWidget getFocus();

      void setFocus(IWidget var1);
   }

   public interface Ref {
      Object get();

      void set(Object var1);
   }
}
