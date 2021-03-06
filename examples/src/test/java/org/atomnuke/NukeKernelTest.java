package org.atomnuke;

import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.atomnuke.examples.eventlets.CounterEventlet;
import org.atomnuke.examples.source.EventGenerator;
import org.atomnuke.fallout.service.gc.FalloutReclamationService;
import org.atomnuke.sink.eps.EventletChainSink;
import org.atomnuke.plugin.context.NopInstanceContext;
import org.atomnuke.plugin.proxy.japi.JapiProxyFactory;
import org.atomnuke.service.RuntimeServiceManager;
import org.atomnuke.service.Service;
import org.atomnuke.service.ServiceManager;
import org.atomnuke.service.introspection.ServicesInterrogatorImpl;
import org.atomnuke.task.atom.AtomTask;
import org.atomnuke.task.context.TaskContextImpl;
import org.atomnuke.util.TimeValue;
import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zinic
 */
public class NukeKernelTest {

   @Test
   public void nukeShakedownTest() throws Exception {
      final NukeKernel nukeKernel = new NukeKernel();
      final AtomicLong eventsProcessed = new AtomicLong(0);

      for (int taskId = 1; taskId <= 30; taskId++) {
         final AtomTask task = nukeKernel.follow(new EventGenerator("Task " + taskId, true), new TimeValue(1000 * taskId, TimeUnit.NANOSECONDS));

         final EventletChainSink relay = new EventletChainSink();

         final ServiceManager svcManager = new RuntimeServiceManager(StaticNukeEnvironment.get(), new JapiProxyFactory());

         svcManager.submit(new NopInstanceContext<Service>(new FalloutReclamationService()));
         svcManager.resolve();

         relay.init(new TaskContextImpl(StaticNukeEnvironment.get(), LoggerFactory.getLogger(NukeKernelTest.class), Collections.EMPTY_MAP, new ServicesInterrogatorImpl(svcManager), null, "test"));

         relay.enlistHandler(new CounterEventlet(eventsProcessed, false));
         relay.enlistHandler(new CounterEventlet(eventsProcessed, false));
         relay.enlistHandler(new CounterEventlet(eventsProcessed, false));
         relay.enlistHandler(new CounterEventlet(eventsProcessed, false));
         relay.enlistHandler(new CounterEventlet(eventsProcessed, false));

         task.addSink(relay);
      }

      nukeKernel.start();

      Thread.sleep(1000);

      nukeKernel.destroy();

      System.out.println("Processed " + eventsProcessed.get() + " entry events in one second.");
   }
}
