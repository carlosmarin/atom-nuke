package org.atomnuke;

import java.util.UUID;
import org.atomnuke.kernel.KernelDelegate;
import org.atomnuke.kernel.resource.Destroyable;
import org.atomnuke.kernel.shutdown.ShutdownHook;
import org.atomnuke.plugin.InstanceContext;
import org.atomnuke.plugin.InstanceContextImpl;
import org.atomnuke.plugin.local.LocalInstanceEnvironment;
import org.atomnuke.source.AtomSource;
import org.atomnuke.task.Task;
import org.atomnuke.task.Tasker;
import org.atomnuke.util.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zinic
 */
public abstract class AbstractNukeImpl implements Nuke {

   private static final Logger LOG = LoggerFactory.getLogger(AbstractNukeImpl.class);

   protected static final long MAX_WAIT_TIME_FOR_SHUTDOWN = 15000;

   private final ShutdownHook kernelShutdownHook;
   private final KernelDelegate kernelDelegate;
   private final Thread controlThread;

   public AbstractNukeImpl(ShutdownHook kernelShutdownHook, KernelDelegate kernelDelegate) {
      this.kernelShutdownHook = kernelShutdownHook;
      this.kernelDelegate = kernelDelegate;

      this.controlThread = new Thread(kernelDelegate, "nuke-kernel-" + UUID.randomUUID().toString());
   }

   @Override
   public Tasker tasker() {
      return kernelDelegate.taskManager();
   }

   @Override
   public ShutdownHook shutdownHook() {
      return kernelShutdownHook;
   }

   @Override
   public Task follow(AtomSource source, TimeValue pollingInterval) {
      return follow(new InstanceContextImpl<AtomSource>(LocalInstanceEnvironment.getInstance(), source), pollingInterval);
   }

   @Override
   public Task follow(InstanceContext<AtomSource> source, TimeValue pollingInterval) {
      return tasker().follow(source, pollingInterval);
   }

   @Override
   public void start() {
      if (controlThread.getState() != Thread.State.NEW) {
         throw new IllegalStateException("Crawler already started or destroyed.");
      }

      kernelShutdownHook.enlist(new Destroyable() {
         @Override
         public void destroy() {
            kernelDelegate.cancellationRemote().cancel();
            kernelDelegate.taskManager().destroy();
            
            try {
               controlThread.join(MAX_WAIT_TIME_FOR_SHUTDOWN);
            } catch (InterruptedException ie) {
               LOG.info("Nuke kernel interrupted while shutting down. Killing thread now.", ie);

               controlThread.interrupt();
            }
         }
      });

      LOG.info("Nuke kernel: " + toString() + " starting.");
      controlThread.start();
   }

   @Override
   public void destroy() {
      kernelShutdownHook.shutdown();
   }
}
