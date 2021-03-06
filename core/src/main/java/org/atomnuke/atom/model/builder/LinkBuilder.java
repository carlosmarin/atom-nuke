package org.atomnuke.atom.model.builder;

import org.atomnuke.atom.model.Link;

/**
 *
 * @author zinic
 */
public class LinkBuilder extends AtomConstructBuilderImpl<LinkBuilder, Link, LinkImpl> {

   public LinkBuilder() {
      super(LinkBuilder.class, new LinkImpl());
   }

   public LinkBuilder(Link copyConstruct) {
      super(LinkBuilder.class, new LinkImpl(), copyConstruct);

      if (copyConstruct != null) {
         if (copyConstruct.href() != null) {
            setHref(copyConstruct.href());
         }

         if (copyConstruct.hreflang() != null) {
            setHreflang(copyConstruct.hreflang());
         }

         if (copyConstruct.length() != null) {
            setLength(copyConstruct.length());
         }

         if (copyConstruct.rel() != null) {
            setRel(copyConstruct.rel());
         }

         if (copyConstruct.title() != null) {
            setTitle(copyConstruct.title());
         }

         if (copyConstruct.type() != null) {
            setType(copyConstruct.type());
         }
      }
   }

   public final LinkBuilder setHref(String href) {
      construct().setHref(href);
      return this;
   }

   public final LinkBuilder setRel(String rel) {
      construct().setRel(rel);
      return this;
   }

   public final LinkBuilder setHreflang(String hreflang) {
      construct().setHreflang(hreflang);
      return this;
   }

   public final LinkBuilder setTitle(String title) {
      construct().setTitle(title);
      return this;
   }

   public final LinkBuilder setLength(Integer length) {
      construct().setLength(length);
      return this;
   }

   public final LinkBuilder setType(String type) {
      construct().setType(type);
      return this;
   }
}
