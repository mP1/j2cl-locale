/*
 * Copyright (c) 2007-present, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package walkingkooka.j2cl.locale.org.threeten.bp.zone;

import javaemul.internal.annotations.GwtIncompatible;
import walkingkooka.j2cl.java.io.string.StringDataInputDataOutput;
import walkingkooka.j2cl.locale.Calendar;
import walkingkooka.j2cl.locale.GregorianCalendar;
import walkingkooka.j2cl.locale.TimeZoneOffsetAndDaylightSavings;
import walkingkooka.j2cl.locale.org.threeten.bp.Duration;
import walkingkooka.j2cl.locale.org.threeten.bp.Instant;
import walkingkooka.j2cl.locale.org.threeten.bp.LocalDate;
import walkingkooka.j2cl.locale.org.threeten.bp.LocalDateTime;
import walkingkooka.j2cl.locale.org.threeten.bp.LocalTime;
import walkingkooka.j2cl.locale.org.threeten.bp.ZoneOffset;
import walkingkooka.j2cl.locale.org.threeten.bp.jdk8.Jdk8Methods;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * The rules defining how the zone offset varies for a single time-zone.
 * <p>
 * The rules model all the historic and future transitions for a time-zone.
 * {@link ZoneOffsetTransition} is used for known transitions, typically historic.
 * {@link ZoneOffsetTransitionRule} is used for future transitions that are based
 * on the result of an algorithm.
 * <p>
 * The rules are loaded via {@link ZoneRulesProvider} using a {@link ZoneId}.
 * The same rules may be shared internally between multiple zone IDs.
 * <p>
 * Serializing an instance of {@code ZoneRules} will store the entire set of rules.
 * It does not store the zone ID as it is not part of the state of this object.
 * <p>
 * A rule implementation may or may not store full information about historic
 * and future transitions, and the information stored is only as accurate as
 * that supplied to the implementation by the rules provider.
 * Applications should treat the data provided as representing the best information
 * available to the implementation of this rule.
 *
 * <h3>Specification for implementors</h3>
 * The supplied implementations of this class are immutable and thread-safe.
 */
public abstract class ZoneRules implements TimeZoneOffsetAndDaylightSavings {

    @GwtIncompatible
    public static void main(final String[] args) throws Exception {
        final StandardZoneRules rules = (StandardZoneRules)of(java.time.ZoneId.of("Australia/Sydney"));

        final StringBuilder data = new StringBuilder();
        rules.writeExternal(StringDataInputDataOutput.output(data::append));
        System.out.println(data);
    }

    /**
     * Intended to only be executed within a JVM building a {@link ZoneRules} from a JVM java.time source.
     * Currently the serialization format for java.time and backport appear to be completely compatible, and this class
     * also assumes a single {@link ZoneRules} implementation exists in both systems.
     */
    @GwtIncompatible
    public static ZoneRules of(final java.time.ZoneId zoneId) throws Exception {
        final java.time.zone.ZoneRules rules = zoneId.getRules();

        try (final ByteArrayOutputStream bytesOut = new ByteArrayOutputStream()) {
            try (final DataOutputStream dataOut = new DataOutputStream(bytesOut)) {
                final Method writeExternal = rules.getClass().getDeclaredMethod("writeExternal", DataOutput.class); // assumes a single ZoneRule class
                writeExternal.setAccessible(true);
                writeExternal.invoke(rules, dataOut);

                dataOut.flush();
            }

            bytesOut.flush();

            try (final ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytesOut.toByteArray())) {
                try (final DataInputStream dataIn = new DataInputStream(bytesIn)) {
                    return StandardZoneRules.readExternal(dataIn); // YUCK
                }
            }
        }
    }

    // Introduced to support annotation-processor writing selected zoneId data during build.
    @GwtIncompatible
    abstract public void writeExternal(final DataOutput out) throws IOException;

    // void writeExternal(DataOutput out) throws IOException {

//    /**
//     * Intended to only be executed within a JVM building a {@link ZoneRules} from a JVM java.time source.
//     */
//    @GwtIncompatible
//    public static ZoneRules of(final java.time.ZoneId zoneId) {
//        final java.time.zone.ZoneRules rules = zoneId.getRules();
//
//        final ZoneOffset baseStandardOffset = transformZoneOffset(rules.getTransitions().get(0).getOffsetAfter()); /// ????
//        final ZoneOffset baseWallOffset = transformZoneOffset(rules.getTransitions().get(0).getOffsetBefore());
//
//        return of(baseStandardOffset,
//                baseWallOffset,
//                transform(rules.getTransitions(), ZoneRules::transformZoneOffsetTransition),
//                transform(rules.getTransitions(), ZoneRules::transformZoneOffsetTransition),
//                transform(rules.getTransitionRules(), ZoneRules::transformZoneOffsetTransitionRule));
//    }
//
    @GwtIncompatible
    static ZoneOffset transformZoneOffset(final java.time.ZoneOffset zoneOffset) {
        return ZoneOffset.ofTotalSeconds(zoneOffset.getTotalSeconds());
    }
//
//    @GwtIncompatible
//    private static ZoneOffsetTransition transformZoneOffsetTransition(final java.time.zone.ZoneOffsetTransition transition) {
//        return ZoneOffsetTransition.of(transformLocalDateTime(null),
//                transformZoneOffset(transition.getOffsetBefore()),
//                transformZoneOffset(transition.getOffsetAfter()));
//    }
//
    @GwtIncompatible
    static LocalDateTime transformLocalDateTime(final java.time.LocalDateTime localDateTime) {
        return LocalDateTime.of(LocalDate.of(localDateTime.getYear(), localDateTime.getMonthValue(), localDateTime.getDayOfMonth()),
                transformTime(localDateTime.toLocalTime()));
    }
//
//    @GwtIncompatible
//    private static ZoneOffsetTransitionRule transformZoneOffsetTransitionRule(final java.time.zone.ZoneOffsetTransitionRule lastRules) {
//        return ZoneOffsetTransitionRule.of(transformMonth(lastRules.getMonth()),
//                lastRules.getDayOfMonthIndicator(),
//                transformDayOfWeek(lastRules.getDayOfWeek()),
//                transformTime(lastRules.getLocalTime()),
//                lastRules.isMidnightEndOfDay(),
//                transformTimeDefinition(lastRules.getTimeDefinition()),
//                transformZoneOffset(lastRules.getStandardOffset()),
//                transformZoneOffset(lastRules.getOffsetBefore()),
//                transformZoneOffset(lastRules.getOffsetAfter()));
//    }
//
//    @GwtIncompatible
//    private static DayOfWeek transformDayOfWeek(final java.time.DayOfWeek dayOfWeek) {
//        return DayOfWeek.of(dayOfWeek.getValue());
//    }

    @GwtIncompatible
    static Instant transformInstant(final java.time.Instant instant) {
        return Instant.ofEpochMilli(instant.toEpochMilli());
    }

//    @GwtIncompatible
//    private static Month transformMonth(final java.time.Month month) {
//        return Month.of(month.getValue());
//    }
//
    @GwtIncompatible
    static LocalTime transformTime(final java.time.LocalTime time) {
        return LocalTime.of(time.getHour(), time.getMinute(), time.getSecond(), time.getNano());
    }
//
//    @GwtIncompatible
//    private static TimeDefinition transformTimeDefinition(final java.time.zone.ZoneOffsetTransitionRule.TimeDefinition definition) {
//        return TimeDefinition.valueOf(definition.name());
//    }
//
//    /**
//     * Helper that transforms a {@link List} from the java.time class to the backport equivalent.
//     */
//    @GwtIncompatible
//    private static <S, D> List<D> transform(final List<S> list,
//                                            final Function<S, D> transformer) {
//        return list.stream()
//                .map(transformer)
//                .collect(Collectors.toList());
//    }

    /**
     * Obtains an instance of {@code ZoneRules} with full transition rules.
     *
     * @param baseStandardOffset           the standard offset to use before legal rules were set, not null
     * @param baseWallOffset               the wall offset to use before legal rules were set, not null
     * @param standardOffsetTransitionList the list of changes to the standard offset, not null
     * @param transitionList               the list of transitions, not null
     * @param lastRules                    the recurring last rules, size 16 or less, not null
     * @return the zone rules, not null
     */
    public static ZoneRules of(ZoneOffset baseStandardOffset,
                               ZoneOffset baseWallOffset,
                               List<ZoneOffsetTransition> standardOffsetTransitionList,
                               List<ZoneOffsetTransition> transitionList,
                               List<ZoneOffsetTransitionRule> lastRules) {
        Jdk8Methods.requireNonNull(baseStandardOffset, "baseStandardOffset");
        Jdk8Methods.requireNonNull(baseWallOffset, "baseWallOffset");
        Jdk8Methods.requireNonNull(standardOffsetTransitionList, "standardOffsetTransitionList");
        Jdk8Methods.requireNonNull(transitionList, "transitionList");
        Jdk8Methods.requireNonNull(lastRules, "lastRules");
        return new StandardZoneRules(baseStandardOffset, baseWallOffset,
                standardOffsetTransitionList, transitionList, lastRules);
    }

    /**
     * Obtains an instance of {@code ZoneRules} that always uses the same offset.
     * <p>
     * The returned rules always have the same offset.
     *
     * @param offset the offset, not null
     * @return the zone rules, not null
     */
    public static ZoneRules of(ZoneOffset offset) {
        Jdk8Methods.requireNonNull(offset, "offset");
        return new Fixed(offset);
    }

    /**
     * Restricted constructor.
     */
    ZoneRules() {
    }

    //-----------------------------------------------------------------------

    /**
     * Checks of the zone rules are fixed, such that the offset never varies.
     *
     * @return true if the time-zone is fixed and the offset never changes
     */
    public abstract boolean isFixedOffset();

    //-----------------------------------------------------------------------

    /**
     * Gets the offset applicable at the specified instant in these rules.
     * <p>
     * The mapping from an instant to an offset is simple, there is only
     * one valid offset for each instant.
     * This method returns that offset.
     *
     * @param instant the instant to find the offset for, not null, but null
     *                may be ignored if the rules have a single offset for all instants
     * @return the offset, not null
     */
    public abstract ZoneOffset getOffset(Instant instant);

    /**
     * Gets a suitable offset for the specified local date-time in these rules.
     * <p>
     * The mapping from a local date-time to an offset is not straightforward.
     * There are three cases:
     * <p><ul>
     * <li>Normal, with one valid offset. For the vast majority of the year, the normal
     * case applies, where there is a single valid offset for the local date-time.</li>
     * <li>Gap, with zero valid offsets. This is when clocks jump forward typically
     * due to the spring daylight savings change from "winter" to "summer".
     * In a gap there are local date-time values with no valid offset.</li>
     * <li>Overlap, with two valid offsets. This is when clocks are set back typically
     * due to the autumn daylight savings change from "summer" to "winter".
     * In an overlap there are local date-time values with two valid offsets.</li>
     * </ul><p>
     * Thus, for any given local date-time there can be zero, one or two valid offsets.
     * This method returns the single offset in the Normal case, and in the Gap or Overlap
     * case it returns the offset before the transition.
     * <p>
     * Since, in the case of Gap and Overlap, the offset returned is a "best" value, rather
     * than the "correct" value, it should be treated with care. Applications that care
     * about the correct offset should use a combination of this method,
     * {@link #getValidOffsets(LocalDateTime)} and {@link #getTransition(LocalDateTime)}.
     *
     * @param localDateTime the local date-time to query, not null, but null
     *                      may be ignored if the rules have a single offset for all instants
     * @return the best available offset for the local date-time, not null
     */
    public abstract ZoneOffset getOffset(LocalDateTime localDateTime);

    /**
     * Gets the offset applicable at the specified local date-time in these rules.
     * <p>
     * The mapping from a local date-time to an offset is not straightforward.
     * There are three cases:
     * <p><ul>
     * <li>Normal, with one valid offset. For the vast majority of the year, the normal
     * case applies, where there is a single valid offset for the local date-time.</li>
     * <li>Gap, with zero valid offsets. This is when clocks jump forward typically
     * due to the spring daylight savings change from "winter" to "summer".
     * In a gap there are local date-time values with no valid offset.</li>
     * <li>Overlap, with two valid offsets. This is when clocks are set back typically
     * due to the autumn daylight savings change from "summer" to "winter".
     * In an overlap there are local date-time values with two valid offsets.</li>
     * </ul><p>
     * Thus, for any given local date-time there can be zero, one or two valid offsets.
     * This method returns that list of valid offsets, which is a list of size 0, 1 or 2.
     * In the case where there are two offsets, the earlier offset is returned at index 0
     * and the later offset at index 1.
     * <p>
     * There are various ways to handle the conversion from a {@code LocalDateTime}.
     * One technique, using this method, would be:
     * <pre>
     *  List<ZoneOffset> validOffsets = rules.getOffset(localDT);
     *  if (validOffsets.size() == 1) {
     *    // Normal case: only one valid offset
     *    zoneOffset = validOffsets.get(0);
     *  } else {
     *    // Gap or Overlap: determine what to do from transition (which will be non-null)
     *    ZoneOffsetTransition trans = rules.getTransition(localDT);
     *  }
     * </pre>
     * <p>
     * In theory, it is possible for there to be more than two valid offsets.
     * This would happen if clocks to be put back more than once in quick succession.
     * This has never happened in the history of time-zones and thus has no special handling.
     * However, if it were to happen, then the list would return more than 2 entries.
     *
     * @param localDateTime the local date-time to query for valid offsets, not null, but null
     *                      may be ignored if the rules have a single offset for all instants
     * @return the list of valid offsets, may be immutable, not null
     */
    public abstract List<ZoneOffset> getValidOffsets(LocalDateTime localDateTime);

    /**
     * Gets the offset transition applicable at the specified local date-time in these rules.
     * <p>
     * The mapping from a local date-time to an offset is not straightforward.
     * There are three cases:
     * <p><ul>
     * <li>Normal, with one valid offset. For the vast majority of the year, the normal
     * case applies, where there is a single valid offset for the local date-time.</li>
     * <li>Gap, with zero valid offsets. This is when clocks jump forward typically
     * due to the spring daylight savings change from "winter" to "summer".
     * In a gap there are local date-time values with no valid offset.</li>
     * <li>Overlap, with two valid offsets. This is when clocks are set back typically
     * due to the autumn daylight savings change from "summer" to "winter".
     * In an overlap there are local date-time values with two valid offsets.</li>
     * </ul><p>
     * A transition is used to model the cases of a Gap or Overlap.
     * The Normal case will return null.
     * <p>
     * There are various ways to handle the conversion from a {@code LocalDateTime}.
     * One technique, using this method, would be:
     * <pre>
     *  ZoneOffsetTransition trans = rules.getTransition(localDT);
     *  if (trans == null) {
     *    // Gap or Overlap: determine what to do from transition
     *  } else {
     *    // Normal case: only one valid offset
     *    zoneOffset = rule.getOffset(localDT);
     *  }
     * </pre>
     *
     * @param localDateTime the local date-time to query for offset transition, not null, but null
     *                      may be ignored if the rules have a single offset for all instants
     * @return the offset transition, null if the local date-time is not in transition
     */
    public abstract ZoneOffsetTransition getTransition(LocalDateTime localDateTime);

    //-----------------------------------------------------------------------

    /**
     * Gets the standard offset for the specified instant in this zone.
     * <p>
     * This provides access to historic information on how the standard offset
     * has changed over time.
     * The standard offset is the offset before any daylight saving time is applied.
     * This is typically the offset applicable during winter.
     *
     * @param instant the instant to find the offset information for, not null, but null
     *                may be ignored if the rules have a single offset for all instants
     * @return the standard offset, not null
     */
    public abstract ZoneOffset getStandardOffset(Instant instant);

    /**
     * Gets the amount of daylight savings in use for the specified instant in this zone.
     * <p>
     * This provides access to historic information on how the amount of daylight
     * savings has changed over time.
     * This is the difference between the standard offset and the actual offset.
     * Typically the amount is zero during winter and one hour during summer.
     * Time-zones are second-based, so the nanosecond part of the duration will be zero.
     *
     * @param instant the instant to find the daylight savings for, not null, but null
     *                may be ignored if the rules have a single offset for all instants
     * @return the difference between the standard and actual offset, not null
     */
    public abstract Duration getDaylightSavings(Instant instant);
    //    default {
    //        ZoneOffset standardOffset = getStandardOffset(instant);
    //        ZoneOffset actualOffset = getOffset(instant);
    //        return actualOffset.toDuration().minus(standardOffset.toDuration()).normalized();
    //    }

    /**
     * Checks if the specified instant is in daylight savings.
     * <p>
     * This checks if the standard and actual offsets are the same at the specified instant.
     *
     * @param instant the instant to find the offset information for, not null, but null
     *                may be ignored if the rules have a single offset for all instants
     * @return the standard offset, not null
     */
    public abstract boolean isDaylightSavings(Instant instant);
    //    default {
    //        return (getStandardOffset(instant).equals(getOffset(instant)) == false);
    //    }

    /**
     * Checks if the offset date-time is valid for these rules.
     * <p>
     * To be valid, the local date-time must not be in a gap and the offset
     * must match the valid offsets.
     *
     * @param localDateTime the date-time to check, not null, but null
     *                      may be ignored if the rules have a single offset for all instants
     * @param offset        the offset to check, null returns false
     * @return true if the offset date-time is valid for these rules
     */
    public abstract boolean isValidOffset(LocalDateTime localDateTime, ZoneOffset offset);
    //    default {
    //        return getValidOffsets(dateTime).contains(offset);
    //    }

    //-----------------------------------------------------------------------

//    /**
//     * Gets the next transition after the specified instant.
//     * <p>
//     * This returns details of the next transition after the specified instant.
//     * For example, if the instant represents a point where "Summer" daylight savings time
//     * applies, then the method will return the transition to the next "Winter" time.
//     *
//     * @param instant the instant to get the next transition after, not null, but null
//     *                may be ignored if the rules have a single offset for all instants
//     * @return the next transition after the specified instant, null if this is after the last transition
//     */
//    public abstract ZoneOffsetTransition nextTransition(Instant instant);
//
//    /**
//     * Gets the previous transition before the specified instant.
//     * <p>
//     * This returns details of the previous transition after the specified instant.
//     * For example, if the instant represents a point where "summer" daylight saving time
//     * applies, then the method will return the transition from the previous "winter" time.
//     *
//     * @param instant the instant to get the previous transition after, not null, but null
//     *                may be ignored if the rules have a single offset for all instants
//     * @return the previous transition after the specified instant, null if this is before the first transition
//     */
//    public abstract ZoneOffsetTransition previousTransition(Instant instant);

    /**
     * Gets the complete list of fully defined transitions.
     * <p>
     * The complete set of transitions for this rules instance is defined by this method
     * and {@link #getTransitionRules()}. This method returns those transitions that have
     * been fully defined. These are typically historical, but may be in the future.
     * <p>
     * The list will be empty for fixed offset rules and for any time-zone where there has
     * only ever been a single offset. The list will also be empty if the transition rules are unknown.
     *
     * @return an immutable list of fully defined transitions, not null
     */
    public abstract List<ZoneOffsetTransition> getTransitions();

    /**
     * Gets the list of transition rules for years beyond those defined in the transition list.
     * <p>
     * The complete set of transitions for this rules instance is defined by this method
     * and {@link #getTransitions()}. This method returns instances of {@link ZoneOffsetTransitionRule}
     * that define an algorithm for when transitions will occur.
     * <p>
     * For any given {@code ZoneRules}, this list contains the transition rules for years
     * beyond those years that have been fully defined. These rules typically refer to future
     * daylight saving time rule changes.
     * <p>
     * If the zone defines daylight savings into the future, then the list will normally
     * be of size two and hold information about entering and exiting daylight savings.
     * If the zone does not have daylight savings, or information about future changes
     * is uncertain, then the list will be empty.
     * <p>
     * The list will be empty for fixed offset rules and for any time-zone where there is no
     * daylight saving time. The list will also be empty if the transition rules are unknown.
     *
     * @return an immutable list of transition rules, not null
     */
    public abstract List<ZoneOffsetTransitionRule> getTransitionRules();

    //-----------------------------------------------------------------------

    /**
     * Checks if this set of rules equals another.
     * <p>
     * Two rule sets are equal if they will always result in the same output
     * for any given input instant or local date-time.
     * Rules from two different groups may return false even if they are in fact the same.
     * <p>
     * This definition should result in implementations comparing their entire state.
     *
     * @param otherRules the other rules, null returns false
     * @return true if this rules is the same as that specified
     */
    @Override
    public abstract boolean equals(Object otherRules);

    /**
     * Returns a suitable hash code given the definition of {@code #equals}.
     *
     * @return the hash code
     */
    @Override
    public abstract int hashCode();

    //-----------------------------------------------------------------------

    /**
     * Fixed time-zone.
     */
    static final class Fixed extends ZoneRules implements Serializable {
        /**
         * A serialization identifier for this class.
         */
        @GwtIncompatible
        private static final long serialVersionUID = -8733721350312276297L;
        /**
         * The offset.
         */
        private final ZoneOffset offset;

        /**
         * Constructor.
         *
         * @param offset the offset, not null
         */
        Fixed(ZoneOffset offset) {
            this.offset = offset;
        }

        //-------------------------------------------------------------------------
        @Override
        public boolean isFixedOffset() {
            return true;
        }

        @Override
        public ZoneOffset getOffset(Instant instant) {
            return offset;
        }

        @Override
        public ZoneOffset getOffset(LocalDateTime localDateTime) {
            return offset;
        }

        @Override
        public List<ZoneOffset> getValidOffsets(LocalDateTime localDateTime) {
            return Collections.singletonList(offset);
        }

        @Override
        public ZoneOffsetTransition getTransition(LocalDateTime localDateTime) {
            return null;
        }

        @Override
        public boolean isValidOffset(LocalDateTime dateTime, ZoneOffset offset) {
            return this.offset.equals(offset);
        }

        //-------------------------------------------------------------------------
        @Override
        public ZoneOffset getStandardOffset(Instant instant) {
            return offset;
        }

        @Override
        public Duration getDaylightSavings(Instant instant) {
            return Duration.ZERO;
        }

        @Override
        public boolean isDaylightSavings(Instant instant) {
            return false;
        }

//        //-------------------------------------------------------------------------
//        @Override
//        public ZoneOffsetTransition nextTransition(Instant instant) {
//            return null;
//        }
//
//        @Override
//        public ZoneOffsetTransition previousTransition(Instant instant) {
//            return null;
//        }

        @Override
        public List<ZoneOffsetTransition> getTransitions() {
            return Collections.emptyList();
        }

        @Override
        public List<ZoneOffsetTransitionRule> getTransitionRules() {
            return Collections.emptyList();
        }

        //-----------------------------------------------------------------------
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof Fixed) {
                return offset.equals(((Fixed) obj).offset);
            }
            if (obj instanceof StandardZoneRules) {
                StandardZoneRules szr = (StandardZoneRules) obj;
                return szr.isFixedOffset() && offset.equals(szr.getOffset(Instant.EPOCH));
            }
            return false;
        }

        @Override
        public int hashCode() {
            return 1 ^
                    (31 + offset.hashCode()) ^
                    1 ^
                    (31 + offset.hashCode()) ^
                    1;
        }

        @Override
        public String toString() {
            return "FixedRules:" + offset;
        }

        @GwtIncompatible
        @Override
        public void writeExternal(final DataOutput out) {
            throw new UnsupportedOperationException();
        }
    }

    // implements TimeZoneOffsetAndDaylightSavings...........................................................................................

    @Override
    public boolean inDaylightTime(final Date time) {
        return this.isDaylightSavings(Instant.ofEpochMilli(time.getTime()));
    }

    /**
     * Gets the offset from GMT of this {@code TimeZone} for the specified date and
     * time. The offset includes daylight savings time if the specified date and
     * time are within the daylight savings time period.
     *
     * @param era       the {@code GregorianCalendar} era, either {@code GregorianCalendar.BC} or
     *                  {@code GregorianCalendar.AD}.
     * @param year      the year.
     * @param month     the {@code Calendar} month.
     * @param day       the day of the month.
     * @param dayOfWeek the {@code Calendar} day of the week.
     * @param time      the time of day in milliseconds.
     * @return the offset from GMT in milliseconds.
     */
    @Override
    public int getOffset(final int era,
                         final int year,
                         final int month,
                         final int day,
                         final int dayOfWeek,
                         final int time) {
        if (GregorianCalendar.AD != era) {
            throw new IllegalArgumentException("Invalid era " + era + " only AD supported"); // TODO add BC support.
        }
        // checkRange/checkDay/isLeapYear copied from ApacheHarmony's java.util.TimeZone.getOffset(int,int,int,int,int,int);
        checkRange(month, dayOfWeek, time);
        if (month != Calendar.FEBRUARY || day != 29 || !isLeapYear(year)) {
            checkDay(month, day);
        }

        final LocalTime localTime = LocalTime.ofNanoOfDay(time/ 1000);

        return this.getOffset(Instant.ofEpochMilli(new Date(year - YEAR_BIAS,
                month,
                day,
                localTime.getHour(),
                localTime.getMinute())
                .getTime())).getTotalSeconds() * 1000;
    }

    private void checkRange(int month, int dayOfWeek, int time) {
        if (month < Calendar.JANUARY || month > Calendar.DECEMBER) {
            //throw new IllegalArgumentException(Messages.getString("luni.3D", month)); //$NON-NLS-1$
            throw new IllegalArgumentException("Invalid month: " + month); //$NON-NLS-1$
        }
        if (dayOfWeek < Calendar.SUNDAY || dayOfWeek > Calendar.SATURDAY) {
//            throw new IllegalArgumentException(Messages
//                    .getString("luni.48", dayOfWeek)); //$NON-NLS-1$
            throw new IllegalArgumentException("Invalid dayOfWeek: " + dayOfWeek);
        }
        if (time < 0 || time >= 24 * 3600000) {
//            throw new IllegalArgumentException(Messages.getString("luni.3E", time)); //$NON-NLS-1$
            throw new IllegalArgumentException("Invalid time " + time);
        }
    }

    private void checkDay(int month, int day) {
        if (day <= 0 || day > GregorianCalendar.DaysInMonth[month]) {
            //throw new IllegalArgumentException(Messages.getString("luni.3F", day)); //$NON-NLS-1$
            throw new IllegalArgumentException("Invalid day " + day + " must be between 0 and " + GregorianCalendar.DaysInMonth[month]); //$NON-NLS-1$
        }
    }

    private boolean isLeapYear(int year) {
        if (year > 1582) {
            return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);
        }
        return year % 4 == 0;
    }

    final static int YEAR_BIAS = 1900;

    @Override
    public final int getOffset(final long time) {
        final int millisOnly = (int)(time % 1000);
        return this.getOffset(Instant.ofEpochMilli(time))
                .getTotalSeconds() * 1000 + millisOnly;
    }

    /**
     * Returns whether this {@code TimeZone} has a daylight savings time period.
     *
     * @return {@code true} if this {@code TimeZone} has a daylight savings time period, {@code false}
     * otherwise.
     */
    @Override
    public boolean useDaylightTime() {
        return /*this.useDaylightTimeTransition() ||*/
                this.useDaylightTimeTransitionRules();
    }

    private boolean useDaylightTimeTransition() {
        return this.getTransitions()
                .stream()
                .anyMatch(this::useDaylightTimeTransition0);
    }

    /**
     * If the transition is a gap or transition it is probably a daylight saving transition.
     */
    private boolean useDaylightTimeTransition0(final ZoneOffsetTransition transition) {
        return transition.isGap() || transition.isOverlap();
    }

    /**
     * If any rules exist daylight savings must be happening.
     */
    private boolean useDaylightTimeTransitionRules() {
        return this.getTransitionRules().size() >= 2;
    }
}
