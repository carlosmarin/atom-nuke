package org.atomnuke.atom.model.builder;

import org.atomnuke.atom.model.PersonConstruct;
import org.atomnuke.atom.model.impl.PersonConstructImpl;

/**
 *
 * @author zinic
 */
public class PersonConstructBuilder<T extends PersonConstructBuilder, B extends PersonConstruct> extends AtomConstructBuilderImpl<T, B, PersonConstructImpl> {

   protected PersonConstructBuilder(Class<T> builderClass) {
      super(builderClass, new PersonConstructImpl());
   }

   protected PersonConstructBuilder(Class<T> builderClass, B copyConstruct) {
      super(builderClass, new PersonConstructImpl(), copyConstruct);

      setName(copyConstruct.name());
      setEmail(copyConstruct.email());
      setUri(copyConstruct.uri());
   }

   public final T setName(String name) {
      construct().setName(name);
      return builder();
   }

   public final T setEmail(String email) {
      construct().setEmail(email);
      return builder();
   }

   public final T setUri(String uri) {
      construct().setUri(uri);
      return builder();
   }
}
