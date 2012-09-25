package org.atomnuke.listener.eps;

import org.atomnuke.listener.eps.eventlet.AtomEventletException;
import org.atomnuke.atom.model.Entry;
import org.atomnuke.atom.model.Feed;
import org.atomnuke.plugin.InstanceEnvironment;
import org.atomnuke.listener.eps.eventlet.AtomEventlet;
import org.atomnuke.listener.eps.selector.Selector;
import org.atomnuke.listener.eps.selector.SelectorResult;
import org.atomnuke.task.lifecycle.DestructionException;
import org.atomnuke.util.remote.AtomicCancellationRemote;
import org.atomnuke.util.remote.CancellationRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zinic
 */
public class HandlerConduit {

   private static final Logger LOG = LoggerFactory.getLogger(HandlerConduit.class);

   private final InstanceEnvironment<? extends AtomEventlet> eventHandler;
   private final CancellationRemote cancellationRemote;
   private final Selector selector;

   public HandlerConduit(InstanceEnvironment<? extends AtomEventlet> eventHandler, Selector selector) {
      this.eventHandler = eventHandler;
      this.selector = selector;

      cancellationRemote = new AtomicCancellationRemote();
   }

   public void destroy() throws DestructionException {
      eventHandler.stepInto();

      try {
         eventHandler.getInstance().destroy();
      } finally {
         eventHandler.stepOut();
      }
   }

   public CancellationRemote cancellationRemote() {
      return cancellationRemote;
   }

   public SelectorResult select(Feed page) {
      final SelectorResult result = selector.select(page);

      if (result == SelectorResult.PROCESS) {
         for (Entry entry : page.entries()) {
            select(entry);
         }
      }

      return result;
   }

   public SelectorResult select(Entry entry) {
      final SelectorResult result = selector.select(entry);

      if (result == SelectorResult.PROCESS) {
         eventHandler.stepInto();

         try {
            eventHandler.getInstance().entry(entry);
         } catch (AtomEventletException epe) {
            LOG.error(epe.getMessage(), epe);
         } finally {
            eventHandler.stepOut();
         }
      }

      return result;
   }
}
