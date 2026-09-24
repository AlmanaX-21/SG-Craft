package gcewing.sg.gui;

import gcewing.sg.BlockPos;
import gcewing.sg.SGChannel;
import gcewing.sg.SGCraft;
import gcewing.sg.SGPlayerAddresses;
import gcewing.sg.SGRemoteDialerContainer;
import gcewing.sg.SGRemoteDialerItem;
import gcewing.sg.SGScreen;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.lwjgl.input.Mouse;

public class SGRemoteDialerScreen extends SGScreen {
   private final EntityPlayer player;
   private GuiTextField addressField;
   private GuiTextField nameField;
   private List<SGPlayerAddresses.Entry> entries = new ArrayList<SGPlayerAddresses.Entry>();
   private String selectedAddress = "";
   private String status = "Loading address book";
   private int firstRow;

   public SGRemoteDialerScreen(EntityPlayer player, World world, BlockPos pos) {
      super(new SGRemoteDialerContainer(player), 300, 236);
      this.player = player;
   }

   public void initGui() {
      String address = addressField == null ? "" : addressField.getText();
      String name = nameField == null ? "" : nameField.getText();
      super.initGui();
      buttonList.clear();
      addressField = new GuiTextField(fontRendererObj, guiLeft + 78, guiTop + 29, 212, 19);
      addressField.setMaxStringLength(16);
      addressField.setText(address);
      addressField.setFocused(true);
      nameField = new GuiTextField(fontRendererObj, guiLeft + 78, guiTop + 54, 212, 19);
      nameField.setMaxStringLength(32);
      nameField.setText(name);
      buttonList.add(new GuiButton(0, guiLeft + 10, guiTop + 82, 62, 20, "Dial"));
      buttonList.add(new GuiButton(1, guiLeft + 76, guiTop + 82, 88, 20, "Disconnect"));
      buttonList.add(new GuiButton(2, guiLeft + 168, guiTop + 82, 122, 20, "Iris / Shield"));
      buttonList.add(new GuiButton(3, guiLeft + 10, guiTop + 198, 58, 20, "Add"));
      buttonList.add(new GuiButton(4, guiLeft + 72, guiTop + 198, 64, 20, "Rename"));
      buttonList.add(new GuiButton(5, guiLeft + 140, guiTop + 198, 58, 20, "Delete"));
      buttonList.add(new GuiButton(6, guiLeft + 202, guiTop + 198, 40, 20, "Up"));
      buttonList.add(new GuiButton(7, guiLeft + 246, guiTop + 198, 44, 20, "Down"));
      SGChannel.clearRemoteUpdates();
      SGChannel.sendRemoteAction("snapshot", "", "");
   }

   public void updateScreen() {
      super.updateScreen();
      addressField.updateCursorCounter();
      nameField.updateCursorCounter();
      SGChannel.RemoteUpdate update;
      while ((update = SGChannel.pollRemoteUpdate()) != null) {
         if (update.entries != null) {
            entries = update.entries;
            firstRow = Math.min(firstRow, Math.max(0, entries.size() - 5));
            status = "Saved addresses: " + entries.size();
         }
         if (update.status != null) {
            status = update.status;
         }
      }
   }

   protected void actionPerformed(GuiButton button) {
      switch (button.id) {
         case 0:
            SGChannel.sendRemoteAction("dial", addressField.getText(), "");
            break;
         case 1:
            SGChannel.sendRemoteAction("disconnect", "", "");
            break;
         case 2:
            SGChannel.sendRemoteAction("barrier", "", "");
            break;
         case 3:
            SGChannel.sendRemoteAction("save", addressField.getText(), nameField.getText());
            break;
         case 4:
         case 5:
            if (selectedAddress.isEmpty()) {
               status = "Select a saved address";
            } else {
               SGChannel.sendRemoteAction(button.id == 4 ? "rename" : "delete", selectedAddress, nameField.getText());
               if (button.id == 5) {
                  selectedAddress = "";
               }
            }
            break;
         case 6:
            firstRow = Math.max(0, firstRow - 1);
            break;
         case 7:
            firstRow = Math.min(Math.max(0, entries.size() - 5), firstRow + 1);
      }
   }

   public void mouseClicked(int x, int y, int button) {
      super.mouseClicked(x, y, button);
      addressField.mouseClicked(x, y, button);
      nameField.mouseClicked(x, y, button);
      int row = (y - guiTop - 110) / 17;
      if (button == 0 && x >= guiLeft + 10 && x < guiLeft + 290 && y >= guiTop + 110 && row < 5 && firstRow + row < entries.size()) {
         SGPlayerAddresses.Entry entry = entries.get(firstRow + row);
         selectedAddress = entry.address;
         addressField.setText(entry.address);
         nameField.setText(entry.name);
      }
   }

   public void handleMouseInput() {
      super.handleMouseInput();
      int wheel = Mouse.getEventDWheel();
      if (wheel != 0) {
         firstRow = Math.max(0, Math.min(Math.max(0, entries.size() - 5), firstRow - Integer.signum(wheel)));
      }
   }

   public void keyTyped(char character, int key) {
      if (key == 1) {
         close();
      } else if (key == 28 || key == 156) {
         SGChannel.sendRemoteAction("dial", addressField.getText(), "");
      } else if (key == 15) {
         boolean addressFocused = addressField.isFocused();
         addressField.setFocused(!addressFocused);
         nameField.setFocused(addressFocused);
      } else if (addressField.isFocused()) {
         addressField.textboxKeyTyped(character, key);
      } else if (nameField.isFocused()) {
         nameField.textboxKeyTyped(character, key);
      }
   }

   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      drawRect(guiLeft, guiTop, guiLeft + xSize, guiTop + ySize, 0xee171b25);
      drawRect(guiLeft + 8, guiTop + 107, guiLeft + 292, guiTop + 194, 0xff272e3a);
      for (int row = 0; row < 5 && firstRow + row < entries.size(); row++) {
         if (entries.get(firstRow + row).address.equals(selectedAddress)) {
            drawRect(guiLeft + 10, guiTop + 110 + row * 17, guiLeft + 290, guiTop + 127 + row * 17, 0xff425d79);
         }
      }
   }

   protected void drawForegroundLayer() {
      fontRendererObj.drawString("Remote Dialer", 10, 8, 0xffffff);
      fontRendererObj.drawString("Address", 10, 35, 0xd8e8ff);
      fontRendererObj.drawString("Name", 10, 60, 0xd8e8ff);
      for (int row = 0; row < 5 && firstRow + row < entries.size(); row++) {
         SGPlayerAddresses.Entry entry = entries.get(firstRow + row);
         fontRendererObj.drawString(fontRendererObj.trimStringToWidth(entry.name, 174), 14, 115 + row * 17, 0xffffff);
         fontRendererObj.drawString(entry.address, 225, 115 + row * 17, 0xaadfff);
      }
      fontRendererObj.drawString(fontRendererObj.trimStringToWidth(status, 280), 10, 223, 0xffd582);
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
      addressField.drawTextBox();
      nameField.drawTextBox();
   }

   public void close() {
      ItemStack held = player.getCurrentEquippedItem();
      if (held != null && held.getItem() == SGCraft.remoteDialer) {
         SGRemoteDialerItem.setHeld(held, false, player.ticksExisted);
      }
      super.close();
   }
}
