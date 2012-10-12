package org.atomnuke.service.context;

import java.util.Map;
import org.atomnuke.service.ServiceManager;

/**
 *
 * @author zinic
 */
public class ServiceContextImpl implements ServiceContext {

   private final Map<String, String> parameters;
   private final ServiceManager manager;

   public ServiceContextImpl(ServiceManager manager, Map<String, String> parameters) {
      this.manager = manager;
      this.parameters = parameters;
   }

   @Override
   public ServiceManager manager() {
      return manager;
   }

   @Override
   public Map<String, String> parameters() {
      return parameters;
   }
}
