package org.atomnuke.bindings.ear;

import com.rackspace.papi.commons.util.classloader.ear.EarClassLoaderContext;
import java.io.File;
import org.atomnuke.bindings.BindingContext;
import org.atomnuke.bindings.BindingInstantiationException;
import org.atomnuke.bindings.context.ClassLoaderContext;
import org.atomnuke.bindings.lang.LanguageDescriptor;
import org.atomnuke.bindings.lang.LanguageDescriptorImpl;
import org.atomnuke.bindings.loader.Loader;
import org.atomnuke.config.model.LanguageType;
import org.atomnuke.context.InstanceContext;

/**
 *
 * @author zinic
 */
public class EarBindingContext implements BindingContext {

   private static final LanguageDescriptor LANGUAGE_DESCRIPTOR = new LanguageDescriptorImpl(LanguageType.JAVA, ".ear");
   
   private final EarLoader loader;

   public EarBindingContext(File deploymentDir) {
      loader = new EarLoader(deploymentDir);
   }

   @Override
   public LanguageDescriptor language() {
      return LANGUAGE_DESCRIPTOR;
   }

   @Override
   public Loader loader() {
      return loader;
   }

   @Override
   public boolean hasRef(String ref) {
      return findCtxFor(ref) != null;
   }

   private EarClassLoaderContext findCtxFor(String ref) {
      for (EarClassLoaderContext ctx : loader.getLoadedPackages().values()) {
         try {
            ctx.getClassLoader().loadClass(ref);
            return ctx;
         } catch (ClassNotFoundException cnfe) {
         }
      }

      return null;
   }

   @Override
   public <T> InstanceContext<T> instantiate(Class<T> interfaceType, String href) throws BindingInstantiationException {
      final EarClassLoaderContext ctx = findCtxFor(href);

      if (ctx == null) {
         return null;
      }

      final Thread currentThread = Thread.currentThread();
      final ClassLoader earCtxClassLoader = ctx.getClassLoader();
      final ClassLoader threadCtxClassLoader = currentThread.getContextClassLoader();

      currentThread.setContextClassLoader(earCtxClassLoader);

      try {
         final Class instanceClass = earCtxClassLoader.loadClass(href);
         final Object instance = instanceClass.newInstance();

         return new ClassLoaderContext<T>(ctx.getClassLoader(), interfaceType.cast(instance));
      } catch (Exception ex) {
         throw new BindingInstantiationException(ex.getMessage(), ex.getCause());
      } finally {
         currentThread.setContextClassLoader(threadCtxClassLoader);
      }
   }
}