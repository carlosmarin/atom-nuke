package net.jps.nuke.examples.listener.test;

import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicLong;
import net.jps.nuke.atom.model.Entry;
import net.jps.nuke.atom.model.Feed;
import net.jps.nuke.listener.AtomListener;
import net.jps.nuke.listener.AtomListenerException;
import net.jps.nuke.listener.ListenerResultImpl;

/**
 *
 * @author zinic
 */
public class PrintStreamOutputListener implements AtomListener {

    private final PrintStream out;
    private String msg;
    private long creationTime;
    private AtomicLong atomicLong;

    public PrintStreamOutputListener(PrintStream out, String msg) {
        this.msg = msg;
        this.out = out;
        this.creationTime = System.currentTimeMillis();
        this.atomicLong = new AtomicLong(0);
    }

    @Override
    public void init() throws AtomListenerException {
        out.println("PrintStreamOutputListener(" + toString() + ") initalized.");
    }

    @Override
    public void destroy() throws AtomListenerException {
        out.println("PrintStreamOutputListener(" + toString() + ") destroyed.");
    }

    private void newEvent() {
        final long events = atomicLong.incrementAndGet();
        final long nowInMillis = System.currentTimeMillis();

        if (events % 10000 == 0) {
            out.println((nowInMillis - creationTime) + "ms elapsed. Events received: " + events + " - Events per 10ms: " + (events / ((nowInMillis - creationTime) / 10)) + " - (" + msg + ")");
        }

        try {
            Thread.sleep(20);
        } catch (InterruptedException ie) {
        }
    }

    @Override
    public ListenerResultImpl entry(Entry entry) throws AtomListenerException {
        newEvent();

        return ListenerResultImpl.ok();
    }

    @Override
    public ListenerResultImpl feedPage(Feed page) throws AtomListenerException {
        newEvent();

        return ListenerResultImpl.ok();
    }
}
