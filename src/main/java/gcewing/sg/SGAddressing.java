package gcewing.sg;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class SGAddressing {
   static AddressingError malformedAddressError = new AddressingError("Malformed stargate address");
   static AddressingError coordRangeError = new AddressingError("Coordinates out of stargate range");
   static AddressingError dimensionRangeError = new AddressingError("Dimension not reachable by stargate");
   public static final String symbolChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
   public static final int numSymbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".length();
   public static final int numCoordSymbols = 7;
   public static final int numDimensionSymbols = 2;
   public static final int maxAddressLength = 9;
   public static final int maxCoord = 139967;
   public static final int minCoord = -139967;
   public static final int coordRange = 279935;
   public static final int minDirectDimension = -648;
   public static final int maxDimensionIndex = 1295;
   public static final int dimensionRange = 1296;
   static final String padding = "---------";
   static final long mc = 279937L;
   static final long pc = 93563L;
   static final long qc = 153742L;
   static final long md = 1297L;
   static final long pd = 953L;
   static final long qd = 788L;
   static final long mdOld = 1298L;
   static final long qdOld = 459L;

   protected static boolean isValidSymbolChar(char c) {
      return isValidSymbolChar(String.valueOf(c));
   }

   public static boolean isValidSymbolChar(String c) {
      return "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".indexOf(c) >= 0;
   }

   protected static char symbolToChar(int i) {
      return "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".charAt(i);
   }

   protected static int charToSymbol(char c) {
      return charToSymbol(String.valueOf(c));
   }

   protected static int charToSymbol(String c) {
      return "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".indexOf(c);
   }

   protected static boolean validSymbols(String s) {
      for(int i = 0; i < s.length(); ++i) {
         if (charToSymbol(s.charAt(i)) < 0) {
            return false;
         }
      }

      return true;
   }

   protected static void validateAddress(String s) throws AddressingError {
      int l = s.length();
      if (l != 9 || !validSymbols(s)) {
         throw malformedAddressError;
      }
   }

   public static String normalizeAddress(String address) {
      return address.replace("-", "").toUpperCase();
   }

   protected static String coordSymbolsOf(String address) {
      return address.substring(0, 7);
   }

   protected static String dimensionSymbolsOf(String address) {
      return address.substring(7);
   }

   public static String addressForLocation(SGLocation loc) throws AddressingError {
      SGBaseTE gate = SGBaseTE.at(loc);
      if (gate == null) {
         throw new AddressingError("No stargate at location");
      }
      return gate.getHomeAddress();
   }

   public static SGBaseTE findAddressedStargate(String address, World fromWorld) throws AddressingError {
      validateAddress(address);
      try {
         return SGGateRegistry.resolve(address);
      } catch (IllegalStateException e) {
         throw new AddressingError(e.getMessage());
      }
   }

   protected static int permuteDimension(long c, int d) {
      return (int)(((long)d + c) % 1296L);
   }

   protected static int unpermuteDimension(long c, int d) {
      int i = (int)(((long)d - c) % 1296L);
      if (i < 0) {
         i += 1296;
      }

      return i;
   }

   protected static long interleaveCoords(int x, int z) {
      long p6 = 1L;

      long c;
      for(c = 0L; x > 0 || z > 0; p6 *= 6L) {
         c += p6 * (long)(x % 6);
         x /= 6;
         p6 *= 6L;
         c += p6 * (long)(z % 6);
         z /= 6;
      }

      return c;
   }

   protected static int[] uninterleaveCoords(long c) {
      int p6 = 1;

      int[] xy;
      for(xy = new int[]{0, 0}; c > 0L; p6 *= 6) {
         xy[0] = (int)((long)xy[0] + (long)p6 * (c % 6L));
         c /= 6L;
         xy[1] = (int)((long)xy[1] + (long)p6 * (c % 6L));
         c /= 6L;
      }

      return xy;
   }

   protected static int hash(int i, long f, long m) {
      int h = (int)((long)(i + 1) * f % m) - 1;
      return h;
   }

   public static WorldServer getWorld(int dimension) {
      return BaseUtils.getWorldForDimension(dimension);
   }

   protected static boolean inCoordRange(int i) {
      return i >= -139967 && i <= 139967;
   }

   protected static String intToSymbols(int i, int n) {
      return longToSymbols((long)i, n);
   }

   protected static String longToSymbols(long i, int n) {
      String s;
      for(s = ""; n-- > 0; i /= (long)numSymbols) {
         s = symbolToChar((int)(i % (long)numSymbols)) + s;
      }

      return s;
   }

   protected static int intFromSymbols(String s) {
      return (int)longFromSymbols(s);
   }

   protected static long longFromSymbols(String s) {
      long i = 0L;
      int n = s.length();

      for(int j = 0; j < n; ++j) {
         char c = s.charAt(j);
         i = i * (long)numSymbols + (long)charToSymbol(c);
      }

      return i;
   }

   public static String padAddress(String address, String caret, int maxLength) {
      if (maxLength < 7) {
         maxLength = 7;
      }

      return formatAddress(address + "---------".substring(address.length(), maxLength), " ", " ");
   }

   public static String formatAddress(String address, String sep1, String sep2) {
      String coord = address.substring(0, 7);
      String dimen = address.substring(7);
      int i = 4;
      String result = coord.substring(0, i) + sep1 + coord.substring(i);
      if (dimen.length() > 0) {
         result = result + sep2 + dimen;
      }

      return result;
   }

   public static String localAddress(String address) {
      return address.substring(0, 7);
   }

   static class AddressingError extends Exception {
      AddressingError(String s) {
         super(s);
      }
   }
}
