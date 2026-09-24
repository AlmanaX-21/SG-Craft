package gcewing.sg;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class SGPlayerAddresses {
   static final String tagName = "SGCraftAddresses";

   static NBTTagCompound forPlayer(EntityPlayer player) {
      NBTTagCompound data = player.getEntityData();
      NBTTagCompound persisted = data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
      data.setTag(EntityPlayer.PERSISTED_NBT_TAG, persisted);
      return persisted;
   }

   public static List<Entry> list(NBTTagCompound persisted) {
      NBTTagList tags = persisted.getTagList(tagName, 10);
      List<Entry> entries = new ArrayList();
      for(int i = 0; i < tags.tagCount(); i++) {
         NBTTagCompound tag = tags.getCompoundTagAt(i);
         entries.add(new Entry(tag.getString("name"), tag.getString("address")));
      }
      return entries;
   }

   static void save(NBTTagCompound persisted, String name, String address) {
      name = validName(name);
      address = validAddress(address);
      NBTTagList tags = persisted.getTagList(tagName, 10);
      for(int i = 0; i < tags.tagCount(); i++) {
         NBTTagCompound tag = tags.getCompoundTagAt(i);
         if (address.equals(tag.getString("address"))) {
            tag.setString("name", name);
            persisted.setTag(tagName, tags);
            return;
         }
      }
      if (tags.tagCount() >= 100) {
         throw new IllegalArgumentException("Address book is full");
      }
      NBTTagCompound tag = new NBTTagCompound();
      tag.setString("name", name);
      tag.setString("address", address);
      tags.appendTag(tag);
      persisted.setTag(tagName, tags);
   }

   static void rename(NBTTagCompound persisted, String address, String name) {
      address = validAddress(address);
      name = validName(name);
      NBTTagList tags = persisted.getTagList(tagName, 10);
      for(int i = 0; i < tags.tagCount(); i++) {
         NBTTagCompound tag = tags.getCompoundTagAt(i);
         if (address.equals(tag.getString("address"))) {
            tag.setString("name", name);
            persisted.setTag(tagName, tags);
            return;
         }
      }
      throw new IllegalArgumentException("Address is not saved");
   }

   static void delete(NBTTagCompound persisted, String address) {
      address = validAddress(address);
      NBTTagList tags = persisted.getTagList(tagName, 10);
      for(int i = 0; i < tags.tagCount(); i++) {
         if (address.equals(tags.getCompoundTagAt(i).getString("address"))) {
            tags.removeTag(i);
            persisted.setTag(tagName, tags);
            return;
         }
      }
      throw new IllegalArgumentException("Address is not saved");
   }

   static String validName(String name) {
      String value = name.trim();
      if (value.isEmpty() || value.length() > 32) {
         throw new IllegalArgumentException("Name must contain 1 to 32 characters");
      }
      for (int i = 0; i < value.length(); i++) {
         if (Character.isISOControl(value.charAt(i)) || value.charAt(i) == '\u00a7') {
            throw new IllegalArgumentException("Name contains unsupported characters");
         }
      }
      return value;
   }

   static String validAddress(String address) {
      String value = SGAddressing.normalizeAddress(address);
      try {
         SGAddressing.validateAddress(value);
      } catch (SGAddressing.AddressingError e) {
         throw new IllegalArgumentException(e.getMessage());
      }
      return value;
   }

   public static class Entry {
      public final String name;
      public final String address;

      public Entry(String name, String address) {
         this.name = name;
         this.address = address;
      }
   }
}
