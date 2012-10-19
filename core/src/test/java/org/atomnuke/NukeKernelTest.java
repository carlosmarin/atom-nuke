package org.atomnuke;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.atomnuke.atom.model.Entry;
import org.atomnuke.atom.model.Feed;
import org.atomnuke.atom.model.builder.FeedBuilder;
import org.atomnuke.listener.AtomListener;
import org.atomnuke.plugin.local.LocalInstanceEnvironment;
import org.atomnuke.listener.AtomListenerException;
import org.atomnuke.listener.AtomListenerResult;
import org.atomnuke.listener.ListenerResult;
import org.atomnuke.listener.ReentrantAtomListener;
import org.atomnuke.plugin.InstanceContextImpl;
import org.atomnuke.source.AtomSource;
import org.atomnuke.source.AtomSourceException;
import org.atomnuke.source.result.AtomSourceResult;
import org.atomnuke.source.result.AtomSourceResultImpl;
import org.atomnuke.task.AtomTask;
import org.atomnuke.task.context.AtomTaskContext;
import org.atomnuke.util.lifecycle.InitializationException;
import org.atomnuke.util.TimeValue;
import org.junit.Test;

/**
 *
 * @author zinic
 */
public class NukeKernelTest {

   @Test
   public void nukeShakedownTest() throws Exception {
      final Nuke nukeKernel = new NukeKernel();
      final AtomicLong eventsProcessed = new AtomicLong(0);

      final AtomSource source = new AtomSource() {
         @Override
         public AtomSourceResult poll() throws AtomSourceException {
            return new AtomSourceResultImpl(new FeedBuilder().build());
         }

         @Override
         public void init(AtomTaskContext tc) throws InitializationException {
         }

         @Override
         public void destroy() {
         }
      };

      final AtomListener listener = new ReentrantAtomListener() {
         @Override
         public ListenerResult entry(Entry entry) throws AtomListenerException {
            eventsProcessed.incrementAndGet();

            return AtomListenerResult.ok();
         }

         @Override
         public ListenerResult feedPage(Feed page) throws AtomListenerException {
            eventsProcessed.incrementAndGet();

            return AtomListenerResult.ok();
         }

         @Override
         public void init(AtomTaskContext tc) throws InitializationException {
         }

         @Override
         public void destroy() {
         }
      };

      for (int taskId = 1; taskId <= 30; taskId++) {
         final AtomTask task = nukeKernel.follow(source, new TimeValue(10 * taskId, TimeUnit.MICROSECONDS));
         task.addListener(new InstanceContextImpl<AtomListener>(LocalInstanceEnvironment.getInstance(), listener));
      }

      nukeKernel.start();

      Thread.sleep(1000);

      nukeKernel.destroy();

      System.out.println("Processed " + eventsProcessed.get() + " feed events in one second.");
   }
}
