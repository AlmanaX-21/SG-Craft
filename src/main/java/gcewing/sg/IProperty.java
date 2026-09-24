package gcewing.sg;

import java.util.Collection;

public interface IProperty<T extends Comparable> {
   String getName();

   Collection<T> getAllowedValues();

   Class<T> getValueClass();

   String getName(T var1);
}
