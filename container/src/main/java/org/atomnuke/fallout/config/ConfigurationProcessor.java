package org.atomnuke.fallout.config;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.atomnuke.Nuke;
import org.atomnuke.config.model.Binding;
import org.atomnuke.plugin.InstanceContext;
import org.atomnuke.config.model.Eventlet;
import org.atomnuke.config.model.LanguageType;
import org.atomnuke.config.model.Parameter;
import org.atomnuke.config.model.Parameters;
import org.atomnuke.config.model.Relay;
import org.atomnuke.config.model.Sink;
import org.atomnuke.config.model.Source;
import org.atomnuke.util.LanguageTypeUtil;
import org.atomnuke.fallout.config.server.ServerConfigurationHandler;
import org.atomnuke.fallout.context.ContainerContext;
import org.atomnuke.container.packaging.PackageContext;
import org.atomnuke.container.packaging.bindings.lang.BindingLanguage;
import org.atomnuke.plugin.env.NopInstanceEnvironment;
import org.atomnuke.listener.AtomListener;
import org.atomnuke.listener.eps.EventletRelay;
import org.atomnuke.listener.eps.eventlet.AtomEventlet;
import org.atomnuke.plugin.InstanceContextImpl;
import org.atomnuke.plugin.ReferenceInstantiationException;
import org.atomnuke.task.operation.TaskLifeCycleInitOperation;
import org.atomnuke.service.ServiceManager;
import org.atomnuke.source.AtomSource;
import org.atomnuke.task.AtomTask;
import org.atomnuke.task.manager.AtomTasker;
import org.atomnuke.task.context.AtomTaskContext;
import org.atomnuke.task.context.TaskContextImpl;
import org.atomnuke.util.TimeValueUtil;
import org.atomnuke.util.config.ConfigurationException;
import org.atomnuke.util.lifecycle.InitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zinic
 */
public class ConfigurationProcessor {

   private static final Logger LOG = LoggerFactory.getLogger(ConfigurationProcessor.class);

   private final Collection<PackageContext> loadedPackages;
   private final ServerConfigurationHandler cfgHandler;
   private final ContainerContext containerContext;
   private final ServiceManager services;
   private final AtomTasker tasker;

   public ConfigurationProcessor(AtomTasker tasker, ServiceManager services, ContainerContext containerContext, ServerConfigurationHandler cfgHandler, Collection<PackageContext> loadedPackages) {
      this.tasker = tasker;
      this.services = services;
      this.containerContext = containerContext;
      this.cfgHandler = cfgHandler;
      this.loadedPackages = loadedPackages;
   }

   private static Map<String, String> parametersToMap(Parameters parameters) {
      final Map<String, String> paramMap = new HashMap<String, String>();

      if (parameters != null) {
         for (Parameter param : parameters.getParam()) {
            paramMap.put(param.getName(), param.getValue());
         }
      }

      return paramMap;
   }

   public void merge(Nuke kernelBeingBuilt) throws ConfigurationException {
      processSources(kernelBeingBuilt);
      processRelays();
      processListeners();
      processEventlets();

      containerContext.process(cfgHandler.getBindings());
   }

   public InstanceContext<AtomEventlet> constructEventlet(LanguageType langType, String ref) throws ReferenceInstantiationException {
      final BindingLanguage bindingLanguage = LanguageTypeUtil.asBindingLanguage(langType);

      for (PackageContext packageContext : loadedPackages) {
         final InstanceContext<AtomEventlet> eventlet = packageContext.packageBindings().resolveReference(AtomEventlet.class, bindingLanguage, ref);

         if (eventlet != null) {
            return eventlet;
         }
      }

      throw new ReferenceInstantiationException("Unable to locate reference: " + ref);
   }

   public InstanceContext<AtomSource> constructSource(LanguageType langType, String ref) throws ReferenceInstantiationException {
      final BindingLanguage bindingLanguage = LanguageTypeUtil.asBindingLanguage(langType);

      for (PackageContext packageContext : loadedPackages) {
         final InstanceContext<AtomSource> source = packageContext.packageBindings().resolveReference(AtomSource.class, bindingLanguage, ref);

         if (source != null) {
            return source;
         }
      }

      throw new ReferenceInstantiationException("Unable to locate reference: " + ref);
   }

   public InstanceContext<AtomListener> constructListener(LanguageType langType, String ref) throws ReferenceInstantiationException {
      final BindingLanguage bindingLanguage = LanguageTypeUtil.asBindingLanguage(langType);

      for (PackageContext packageContext : loadedPackages) {
         final InstanceContext<AtomListener> listener = packageContext.packageBindings().resolveReference(AtomListener.class, bindingLanguage, ref);

         if (listener != null) {
            return listener;
         }
      }

      throw new ReferenceInstantiationException("Unable to locate reference: " + ref);
   }

   public boolean hasSourceBinding(String name) throws ConfigurationException {
      for (Binding binding : cfgHandler.getBindings()) {
         if (binding.getTarget().equals(name)) {
            return true;
         }
      }

      return false;
   }

   public boolean hasListenerBinding(String name) throws ConfigurationException {
      for (Binding binding : cfgHandler.getBindings()) {
         if (binding.getReceiver().equals(name)) {
            return true;
         }
      }

      return false;
   }

   public void processSources(Nuke kernelBeingBuilt) throws ConfigurationException {
      for (Source source : cfgHandler.getSources()) {
         final String sourceId = source.getId();

         if (hasSourceBinding(sourceId) && !containerContext.hasTask(sourceId)) {
            try {
               final InstanceContext<AtomSource> sourceContext = constructSource(source.getType(), source.getHref());
               sourceContext.perform(TaskLifeCycleInitOperation.<AtomSource>instance(), new TaskContextImpl(LoggerFactory.getLogger(sourceId), parametersToMap(source.getParameters()), services, tasker));

               final AtomTask newTask = kernelBeingBuilt.atomTasker().follow(sourceContext, TimeValueUtil.fromPollingInterval(source.getPollingInterval()));
               containerContext.registerSource(source.getId(), newTask);
            } catch (ReferenceInstantiationException bie) {
               LOG.error("Could not create source instance " + source.getId() + ". Reason: " + bie.getMessage(), bie);
            }
         }
      }
   }

   public void processRelays() throws ConfigurationException {
      for (Relay relay : cfgHandler.getRelays()) {
         final String relayId = relay.getId();

         if (hasListenerBinding(relayId) && !containerContext.hasRelay(relayId)) {
            final EventletRelay newRelay = new EventletRelay();

            try {
               newRelay.init(new TaskContextImpl(LoggerFactory.getLogger(EventletRelay.class), Collections.EMPTY_MAP, services, tasker));
               containerContext.registerRelay(relay.getId(), new InstanceContextImpl<EventletRelay>(NopInstanceEnvironment.getInstance(), newRelay));
            } catch (InitializationException ie) {
               LOG.error("Failed to create relay instance " + relay.getId() + ". Reason: " + ie.getMessage(), ie);
            }
         }
      }
   }

   public void processListeners() throws ConfigurationException {
      for (Sink sink : cfgHandler.getSinks()) {
         final String sinkId = sink.getId();

         if (hasListenerBinding(sinkId) && !containerContext.hasSink(sinkId)) {
            try {
               final InstanceContext<AtomListener> listenerCtx = constructListener(sink.getType(), sink.getHref());

               listenerCtx.perform(TaskLifeCycleInitOperation.<AtomListener>instance(), new TaskContextImpl(LoggerFactory.getLogger(sinkId), parametersToMap(sink.getParameters()), services, tasker));

               containerContext.registerSink(sink.getId(), listenerCtx);
            } catch (ReferenceInstantiationException bie) {
               LOG.error("Could not create sink instance " + sink.getId() + ". Reason: " + bie.getMessage(), bie);
               throw new ConfigurationException(bie);
            }
         }
      }
   }

   public void processEventlets() throws ConfigurationException {
      for (Eventlet eventlet : cfgHandler.getEventlets()) {
         final String eventletId = eventlet.getId();

         if (hasListenerBinding(eventletId) && !containerContext.hasEventlet(eventletId)) {
            try {
               final InstanceContext<AtomEventlet> eventletCtx = constructEventlet(eventlet.getType(), eventlet.getHref());
               final AtomTaskContext taskContext = new TaskContextImpl(LoggerFactory.getLogger(eventletId), parametersToMap(eventlet.getParameters()), services, tasker);

               eventletCtx.perform(TaskLifeCycleInitOperation.<AtomEventlet>instance(), taskContext);

               containerContext.registerEventlet(eventlet.getId(), eventletCtx);
            } catch (ReferenceInstantiationException bie) {
               LOG.error("Could not create eventlet instance " + eventlet.getId() + ". Reason: " + bie.getMessage(), bie);
               throw new ConfigurationException(bie);
            }
         }
      }
   }
}