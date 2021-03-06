package org.atomnuke.cli.command.source;

import java.util.Iterator;
import org.atomnuke.atombus.config.model.MessageSource;
import org.atomnuke.cli.CliConfigurationHandler;
import org.atomnuke.cli.command.AbstractNukeCommand;
import org.atomnuke.util.cli.command.result.CommandFailure;
import org.atomnuke.util.cli.command.result.CommandResult;
import org.atomnuke.util.cli.command.result.CommandSuccess;

/**
 *
 * @author zinic
 */
public class DeleteSource extends AbstractNukeCommand {

   private static final int SOURCE_ID = 0;

   public DeleteSource(CliConfigurationHandler configurationHandler) {
      super(configurationHandler);
   }

   @Override
   public String getCommandToken() {
      return "rm";
   }

   @Override
   public String getCommandDescription() {
      return "Removes a message actor's source definition. This will unbind any recievers tied to the source definition being deleted.";
   }

   @Override
   public CommandResult perform(String[] arguments) throws Exception {
      if (arguments.length != 1) {
         return new CommandFailure("Deleting a source requires one arguments: <sink-id>");
      }

      final CliConfigurationHandler cfgHandler = getConfigHandler();

      for (Iterator<MessageSource> sourceItr = cfgHandler.getMessageSources().iterator(); sourceItr.hasNext();) {
         if (sourceItr.next().getActorRef().equals(arguments[SOURCE_ID])) {
            sourceItr.remove();
            unbindSource(cfgHandler, arguments[SOURCE_ID]);

            cfgHandler.write();
            return new CommandSuccess();
         }
      }

      return new CommandFailure("No source with an id matching, \"" + arguments[SOURCE_ID] + "\" seems to exist.");
   }
}
