package org.atomnuke.syslog.netty;

import org.atomnuke.source.AtomSource;
import org.atomnuke.source.AtomSourceException;
import org.atomnuke.source.result.AtomSourceResult;
import org.atomnuke.fallout.source.queue.QueueSource;
import org.atomnuke.fallout.source.queue.EntryQueueImpl;
import org.atomnuke.task.context.AtomTaskContext;
import org.atomnuke.lifecycle.InitializationException;
import org.atomnuke.service.ServiceUnavailableException;
import org.atomnuke.service.netty.server.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zinic
 */
public class NettySyslogSource implements AtomSource {

   private static final Logger LOG = LoggerFactory.getLogger(NettySyslogSource.class);
   private final QueueSource queueSource;

   public NettySyslogSource() {
      queueSource = new EntryQueueImpl();
   }

   @Override
   public AtomSourceResult poll() throws AtomSourceException {
      return queueSource.poll();
   }

   @Override
   public void init(AtomTaskContext tc) throws InitializationException {
      try {
         final NettyServer server = tc.services().firstAvailable(NettyServer.class);
         server.open(5025);
         
         
      } catch (ServiceUnavailableException sue) {
      }
   }

   @Override
   public void destroy() {
   }
}