package net.jps.nuke.crawler.task;

import net.jps.nuke.crawler.remote.CancellationRemote;
import net.jps.nuke.listener.AtomListener;

/**
 *
 * @author zinic
 */
public class RegisteredListener {

   private final CancellationRemote cancellationRemote;
   private final AtomListener listener;

   public RegisteredListener(AtomListener listener, CancellationRemote cancellationRemote) {
      this.cancellationRemote = cancellationRemote;
      this.listener = listener;
   }

   public CancellationRemote cancellationRemote() {
      return cancellationRemote;
   }

   public AtomListener listener() {
      return listener;
   }
}
