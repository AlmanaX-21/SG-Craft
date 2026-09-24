package gcewing.sg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseStringUtils {
   public static String[] split(String sep, String string) {
      List<String> list = new ArrayList();
      String[] result = new String[0];

      int j;
      for(int i = 0; i < string.length(); i = j + sep.length()) {
         j = string.indexOf(sep, i);
         if (j < 0) {
            j = string.length();
         }

         list.add(string.substring(i, j));
      }

      result = (String[])list.toArray(result);
      return result;
   }

   public static String join(String sep, String[] strings) {
      return join(sep, (Iterable)Arrays.asList(strings));
   }

   public static String join(String sep, Iterable<String> strings) {
      StringBuilder result = new StringBuilder();
      boolean first = true;

      for(String s : strings) {
         if (first) {
            first = false;
         } else {
            result.append(sep);
         }

         result.append(s);
      }

      return result.toString();
   }
}
