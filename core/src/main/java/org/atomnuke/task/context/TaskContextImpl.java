package org.atomnuke.task.context;

import java.util.Map;
import org.atomnuke.service.ServiceManager;
import org.atomnuke.task.Tasker;

/**
 *
 * @author zinic
 */
public class TaskContextImpl implements TaskContext {

   private final Map<String, String> parameters;
   private final ServiceManager services;
   private final Tasker submitter;

   public TaskContextImpl(Map<String, String> parameters, ServiceManager services, Tasker submitter) {
      this.parameters = parameters;
      this.services = services;
      this.submitter = submitter;
   }

   @Override
   public ServiceManager services() {
      return services;
   }

   @Override
   public Map<String, String> parameters() {
      return parameters;
   }

   @Override
   public Tasker submitter() {
      return submitter;
   }
}
