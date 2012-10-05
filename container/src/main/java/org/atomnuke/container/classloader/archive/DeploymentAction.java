package org.atomnuke.container.classloader.archive;

public enum DeploymentAction {

   UNPACK_ENTRY,
   DO_NOT_UNPACK_ENTRY;

   public static DeploymentAction defaultAction() {
      return UNPACK_ENTRY;
   }
}
