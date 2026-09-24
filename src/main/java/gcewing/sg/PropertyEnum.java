package gcewing.sg;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;

public class PropertyEnum<T extends Enum> extends PropertyHelper<T> {
   private final ImmutableSet<T> allowedValues;
   private final Map<String, T> nameToValue = Maps.newHashMap();

   protected PropertyEnum(String name, Class<T> valueClass, Collection<T> allowedValues) {
      super(name, valueClass);
      this.allowedValues = ImmutableSet.copyOf(allowedValues);

      for(T t : allowedValues) {
         String s = this.getName(t);
         if (this.nameToValue.containsKey(s)) {
            throw new IllegalArgumentException("Multiple values have the same name '" + s + "'");
         }

         this.nameToValue.put(s, t);
      }

   }

   public Collection<T> getAllowedValues() {
      return this.allowedValues;
   }

   public String getName(T value) {
      return value.toString().toLowerCase();
   }

   public static <T extends Enum<T>> PropertyEnum<T> create(String name, Class<T> clazz) {
      return create(name, clazz, Predicates.alwaysTrue());
   }

   public static <T extends Enum<T>> PropertyEnum<T> create(String name, Class<T> clazz, Predicate<T> filter) {
      return create(name, clazz, Collections2.filter(Lists.newArrayList(clazz.getEnumConstants()), filter));
   }

   public static <T extends Enum<T>> PropertyEnum<T> create(String name, Class<T> clazz, T... values) {
      return create(name, clazz, Lists.newArrayList(values));
   }

   public static <T extends Enum<T>> PropertyEnum<T> create(String name, Class<T> clazz, Collection<T> values) {
      return new PropertyEnum<T>(name, clazz, values);
   }
}
