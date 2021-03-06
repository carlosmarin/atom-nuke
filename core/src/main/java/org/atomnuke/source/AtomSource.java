package org.atomnuke.source;

import org.atomnuke.task.context.AtomTaskContext;
import org.atomnuke.lifecycle.ResourceLifeCycle;
import org.atomnuke.source.result.AtomSourceResult;

/**
 * An AtomSource represents an object that may be polled for ATOM data.
 *
 * @author zinic
 */
public interface AtomSource extends ResourceLifeCycle<AtomTaskContext> {

   /**
    * Polls the source for ATOM data. This data may be an Entry object or a Feed
    * object.
    *
    * @return result containing the necessary information to identify the result
    * and extract it.
    * @throws AtomSourceException thrown when the underlying system this source
    * abstracts encounters a failure and is unable to return ATOM data.
    */
   AtomSourceResult poll() throws AtomSourceException;
}
