package net.jps.nuke.util;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author zinic
 */
public class TimeValue implements Comparable<TimeValue> {

   public static TimeValue now() {
      return new TimeValue(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
   }

   public static TimeValue now(TimeUnit unit) {
      return now().convert(unit);
   }
   
   private final TimeUnit unit;
   private final long value;

   public TimeValue(long value, TimeUnit unit) {
      this.value = value;
      this.unit = unit;
   }

   public TimeUnit unit() {
      return unit;
   }

   public long value() {
      return value;
   }

   public long value(TimeUnit otherTimeUnit) {
      return unit.equals(otherTimeUnit)
              ? value
              : otherTimeUnit.convert(value, unit);
   }

   public TimeValue convert(TimeUnit otherTimeUnit) {
      return new TimeValue(value(otherTimeUnit), otherTimeUnit);
   }
   
   /**
    * Adds the time value to this value and returns the result. This method does
    * not mutate object state.
    * 
    * @param otherTimeValue
    * @return 
    */
   public TimeValue add(TimeValue otherTimeValue) {
      return new TimeValue(otherTimeValue.value(unit()) + value(), unit());
   }
   
   public boolean isGreatherThan(TimeValue otherTimeValue) {
      return compareTo(otherTimeValue) > 0;
   }
   
   public boolean isLessThan(TimeValue otherTimeValue) {
      return compareTo(otherTimeValue) < 0;
   }

   public int compareTo(TimeValue otherTimeValue) {
      final long compareValue = value(otherTimeValue.unit()) - otherTimeValue.value();
      
      return compareValue > 0 ? 1 : compareValue < 0 ? -1 : 0;
   }

   @Override
   public int hashCode() {
      int hash = 7;
      hash = 71 * hash + (this.unit != null ? this.unit.hashCode() : 0);
      hash = 71 * hash + (int) (this.value ^ (this.value >>> 32));
      return hash;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj != null && getClass() == obj.getClass()) {
         final TimeValue otherTimeValue = (TimeValue) obj;
         
         return compareTo(otherTimeValue) == 0;
      }

      return false;
   }
}
