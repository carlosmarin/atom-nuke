package org.atomnuke.plugin.context;

import org.atomnuke.plugin.InstanceContext;
import org.atomnuke.plugin.Environment;
import org.atomnuke.plugin.operation.ComplexOperation;
import org.atomnuke.plugin.operation.OperationFailureException;
import org.atomnuke.plugin.operation.SimpleOperation;

/**
 *
 * @author zinic
 */
public class InstanceContextImpl<T> implements InstanceContext<T> {

   private final Environment environment;
   private final T instance;

   public InstanceContextImpl(Environment environment, T instance) {
      this.environment = environment;
      this.instance = instance;
   }

   @Override
   public Class<T> instanceClass() {
      return (Class<T>) instance.getClass();
   }

   @Override
   public T instance() {
      return instance;
   }

   @Override
   public void perform(SimpleOperation<T> requestedOperation) throws OperationFailureException {
      try {
         environment.stepInto();
         requestedOperation.perform(instance());
      } finally {
         environment.stepOut();
      }
   }

   @Override
   public <A> void perform(ComplexOperation<T, A> requestedOperation, A argument) throws OperationFailureException {
      try {
         environment.stepInto();
         requestedOperation.perform(instance(), argument);
      } finally {
         environment.stepOut();
      }
   }

   @Override
   public String toString() {
      return "InstanceContextImpl{" + "environment=" + environment + ", instance=" + instance + '}';
   }
}
