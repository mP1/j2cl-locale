/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.j2cl.locale.org.threeten.bp;

import javaemul.internal.annotations.GwtIncompatible;
import walkingkooka.j2cl.locale.org.threeten.bp.jdk8.Jdk8Methods;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.time.chrono.ChronoLocalDateTime;

import static walkingkooka.j2cl.locale.org.threeten.bp.LocalTime.HOURS_PER_DAY;
import static walkingkooka.j2cl.locale.org.threeten.bp.LocalTime.MINUTES_PER_DAY;
import static walkingkooka.j2cl.locale.org.threeten.bp.LocalTime.NANOS_PER_DAY;
import static walkingkooka.j2cl.locale.org.threeten.bp.LocalTime.NANOS_PER_HOUR;
import static walkingkooka.j2cl.locale.org.threeten.bp.LocalTime.NANOS_PER_MINUTE;
import static walkingkooka.j2cl.locale.org.threeten.bp.LocalTime.NANOS_PER_SECOND;
import static walkingkooka.j2cl.locale.org.threeten.bp.LocalTime.SECONDS_PER_DAY;

/**
 * A date-time without a time-zone in the ISO-8601 calendar system,
 * such as {@code 2007-12-23T10:15:30}.
 * <p>
 * {@code LocalDateTime} is an immutable date-time object that represents a date-time,
 * often viewed as year-month-day-hour-minute-second. Other date and time fields,
 * such as day-of-year, day-of-week and week-of-year, can also be accessed.
 * Time is represented to nanosecond precision.
 * For example, the value "2nd October 2007 at 13:45.30.123456789" can be
 * stored in a {@code LocalDateTime}.
 * <p>
 * This class does not store or represent a time-zone.
 * Instead, it is a description of the date, as used for birthdays, combined with
 * the local time as seen on a wall clock.
 * It cannot represent an instant on the time-line without additional information
 * such as an offset or time-zone.
 * <p>
 * The ISO-8601 calendar system is the modern civil calendar system used today
 * in most of the world. It is equivalent to the proleptic Gregorian calendar
 * system, in which today's rules for leap years are applied for all time.
 * For most applications written today, the ISO-8601 rules are entirely suitable.
 * However, any application that makes use of historical dates, and requires them
 * to be accurate will find the ISO-8601 approach unsuitable.
 *
 * <h3>Specification for implementors</h3>
 * This class is immutable and thread-safe.
 */
public final class LocalDateTime
        /*extends ChronoLocalDateTime<LocalDate>
        implements Temporal, TemporalAdjuster, Serializable*/
        implements Comparable<LocalDateTime> {

    /**
     * The minimum supported {@code LocalDateTime}, '-999999999-01-01T00:00:00'.
     * This is the local date-time of midnight at the start of the minimum date.
     * This combines {@link LocalDate#MIN} and {@link LocalTime#MIN}.
     * This could be used by an application as a "far past" date-time.
     */
    public static final LocalDateTime MIN = LocalDateTime.of(LocalDate.MIN, LocalTime.MIN);
    /**
     * The maximum supported {@code LocalDateTime}, '+999999999-12-31T23:59:59.999999999'.
     * This is the local date-time just before midnight at the end of the maximum date.
     * This combines {@link LocalDate#MAX} and {@link LocalTime#MAX}.
     * This could be used by an application as a "far future" date-time.
     */
    public static final LocalDateTime MAX = LocalDateTime.of(LocalDate.MAX, LocalTime.MAX);
//    /**
//     * Simulate JDK 8 method reference LocalDateTime::from.
//     */
//    public static final TemporalQuery<LocalDateTime> FROM = new TemporalQuery<LocalDateTime>() {
//        @Override
//        public LocalDateTime queryFrom(TemporalAccessor temporal) {
//            return LocalDateTime.from(temporal);
//        }
//    };

    /**
     * Serialization version.
     */
    @GwtIncompatible
    private static final long serialVersionUID = 6207766400415563566L;

    /**
     * The date part.
     */
    private final LocalDate date;
    /**
     * The time part.
     */
    private final LocalTime time;

    //-----------------------------------------------------------------------
//    /**
//     * Obtains the current date-time from the system clock in the default time-zone.
//     * <p>
//     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
//     * time-zone to obtain the current date-time.
//     * <p>
//     * Using this method will prevent the ability to use an alternate clock for testing
//     * because the clock is hard-coded.
//     *
//     * @return the current date-time using the system clock and default time-zone, not null
//     */
//    public static LocalDateTime now() {
//        return now(Clock.systemDefaultZone());
//    }
//
//    /**
//     * Obtains the current date-time from the system clock in the specified time-zone.
//     * <p>
//     * This will query the {@link Clock#system(ZoneId) system clock} to obtain the current date-time.
//     * Specifying the time-zone avoids dependence on the default time-zone.
//     * <p>
//     * Using this method will prevent the ability to use an alternate clock for testing
//     * because the clock is hard-coded.
//     *
//     * @param zone  the zone ID to use, not null
//     * @return the current date-time using the system clock, not null
//     */
//    public static LocalDateTime now(ZoneId zone) {
//        return now(Clock.system(zone));
//    }
//
//    /**
//     * Obtains the current date-time from the specified clock.
//     * <p>
//     * This will query the specified clock to obtain the current date-time.
//     * Using this method allows the use of an alternate clock for testing.
//     * The alternate clock may be introduced using {@link Clock dependency injection}.
//     *
//     * @param clock  the clock to use, not null
//     * @return the current date-time, not null
//     */
//    public static LocalDateTime now(Clock clock) {
//        Jdk8Methods.requireNonNull(clock, "clock");
//        final Instant now = clock.instant();  // called once
//        ZoneOffset offset = clock.getZone().getRules().getOffset(now);
//        return ofEpochSecond(now.getEpochSecond(), now.getNano(), offset);
//    }
//
//    //-----------------------------------------------------------------------
//    /**
//     * Obtains an instance of {@code LocalDateTime} from year, month,
//     * day, hour and minute, setting the second and nanosecond to zero.
//     * <p>
//     * The day must be valid for the year and month, otherwise an exception will be thrown.
//     * The second and nanosecond fields will be set to zero.
//     *
//     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
//     * @param month  the month-of-year to represent, not null
//     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
//     * @param hour  the hour-of-day to represent, from 0 to 23
//     * @param minute  the minute-of-hour to represent, from 0 to 59
//     * @return the local date-time, not null
//     * @throws DateTimeException if the value of any field is out of range
//     * @throws DateTimeException if the day-of-month is invalid for the month-year
//     */
//    public static LocalDateTime of(int year, Month month, int dayOfMonth, int hour, int minute) {
//        LocalDate date = LocalDate.of(year, month, dayOfMonth);
//        LocalTime time = LocalTime.of(hour, minute);
//        return new LocalDateTime(date, time);
//    }
//
//    /**
//     * Obtains an instance of {@code LocalDateTime} from year, month,
//     * day, hour, minute and second, setting the nanosecond to zero.
//     * <p>
//     * The day must be valid for the year and month, otherwise an exception will be thrown.
//     * The nanosecond field will be set to zero.
//     *
//     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
//     * @param month  the month-of-year to represent, not null
//     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
//     * @param hour  the hour-of-day to represent, from 0 to 23
//     * @param minute  the minute-of-hour to represent, from 0 to 59
//     * @param second  the second-of-minute to represent, from 0 to 59
//     * @return the local date-time, not null
//     * @throws DateTimeException if the value of any field is out of range
//     * @throws DateTimeException if the day-of-month is invalid for the month-year
//     */
//    public static LocalDateTime of(int year, Month month, int dayOfMonth, int hour, int minute, int second) {
//        LocalDate date = LocalDate.of(year, month, dayOfMonth);
//        LocalTime time = LocalTime.of(hour, minute, second);
//        return new LocalDateTime(date, time);
//    }
//
//    /**
//     * Obtains an instance of {@code LocalDateTime} from year, month,
//     * day, hour, minute, second and nanosecond.
//     * <p>
//     * The day must be valid for the year and month, otherwise an exception will be thrown.
//     *
//     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
//     * @param month  the month-of-year to represent, not null
//     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
//     * @param hour  the hour-of-day to represent, from 0 to 23
//     * @param minute  the minute-of-hour to represent, from 0 to 59
//     * @param second  the second-of-minute to represent, from 0 to 59
//     * @param nanoOfSecond  the nano-of-second to represent, from 0 to 999,999,999
//     * @return the local date-time, not null
//     * @throws DateTimeException if the value of any field is out of range
//     * @throws DateTimeException if the day-of-month is invalid for the month-year
//     */
//    public static LocalDateTime of(int year, Month month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond) {
//        LocalDate date = LocalDate.of(year, month, dayOfMonth);
//        LocalTime time = LocalTime.of(hour, minute, second, nanoOfSecond);
//        return new LocalDateTime(date, time);
//    }
//
//    //-----------------------------------------------------------------------
//    /**
//     * Obtains an instance of {@code LocalDateTime} from year, month,
//     * day, hour and minute, setting the second and nanosecond to zero.
//     * <p>
//     * The day must be valid for the year and month, otherwise an exception will be thrown.
//     * The second and nanosecond fields will be set to zero.
//     *
//     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
//     * @param month  the month-of-year to represent, from 1 (January) to 12 (December)
//     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
//     * @param hour  the hour-of-day to represent, from 0 to 23
//     * @param minute  the minute-of-hour to represent, from 0 to 59
//     * @return the local date-time, not null
//     * @throws DateTimeException if the value of any field is out of range
//     * @throws DateTimeException if the day-of-month is invalid for the month-year
//     */
//    public static LocalDateTime of(int year, int month, int dayOfMonth, int hour, int minute) {
//        LocalDate date = LocalDate.of(year, month, dayOfMonth);
//        LocalTime time = LocalTime.of(hour, minute);
//        return new LocalDateTime(date, time);
//    }
//
//    /**
//     * Obtains an instance of {@code LocalDateTime} from year, month,
//     * day, hour, minute and second, setting the nanosecond to zero.
//     * <p>
//     * The day must be valid for the year and month, otherwise an exception will be thrown.
//     * The nanosecond field will be set to zero.
//     *
//     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
//     * @param month  the month-of-year to represent, from 1 (January) to 12 (December)
//     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
//     * @param hour  the hour-of-day to represent, from 0 to 23
//     * @param minute  the minute-of-hour to represent, from 0 to 59
//     * @param second  the second-of-minute to represent, from 0 to 59
//     * @return the local date-time, not null
//     * @throws DateTimeException if the value of any field is out of range
//     * @throws DateTimeException if the day-of-month is invalid for the month-year
//     */
//    public static LocalDateTime of(int year, int month, int dayOfMonth, int hour, int minute, int second) {
//        LocalDate date = LocalDate.of(year, month, dayOfMonth);
//        LocalTime time = LocalTime.of(hour, minute, second);
//        return new LocalDateTime(date, time);
//    }
//
//    /**
//     * Obtains an instance of {@code LocalDateTime} from year, month,
//     * day, hour, minute, second and nanosecond.
//     * <p>
//     * The day must be valid for the year and month, otherwise an exception will be thrown.
//     *
//     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
//     * @param month  the month-of-year to represent, from 1 (January) to 12 (December)
//     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
//     * @param hour  the hour-of-day to represent, from 0 to 23
//     * @param minute  the minute-of-hour to represent, from 0 to 59
//     * @param second  the second-of-minute to represent, from 0 to 59
//     * @param nanoOfSecond  the nano-of-second to represent, from 0 to 999,999,999
//     * @return the local date-time, not null
//     * @throws DateTimeException if the value of any field is out of range
//     * @throws DateTimeException if the day-of-month is invalid for the month-year
//     */
//    public static LocalDateTime of(int year, int month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond) {
//        LocalDate date = LocalDate.of(year, month, dayOfMonth);
//        LocalTime time = LocalTime.of(hour, minute, second, nanoOfSecond);
//        return new LocalDateTime(date, time);
//    }

    /**
     * Obtains an instance of {@code LocalDateTime} from a date and time.
     *
     * @param date  the local date, not null
     * @param time  the local time, not null
     * @return the local date-time, not null
     */
    public static LocalDateTime of(LocalDate date, LocalTime time) {
        Jdk8Methods.requireNonNull(date, "date");
        Jdk8Methods.requireNonNull(time, "time");
        return new LocalDateTime(date, time);
    }

    //-------------------------------------------------------------------------
//    /**
//     * Obtains an instance of {@code LocalDateTime} from an {@code Instant} and zone ID.
//     * <p>
//     * This creates a local date-time based on the specified instant.
//     * First, the offset from UTC/Greenwich is obtained using the zone ID and instant,
//     * which is simple as there is only one valid offset for each instant.
//     * Then, the instant and offset are used to calculate the local date-time.
//     *
//     * @param instant  the instant to create the date-time from, not null
//     * @param zone  the time-zone, which may be an offset, not null
//     * @return the local date-time, not null
//     * @throws DateTimeException if the result exceeds the supported range
//     */
//    public static LocalDateTime ofInstant(Instant instant, ZoneId zone) {
//        Jdk8Methods.requireNonNull(instant, "instant");
//        Jdk8Methods.requireNonNull(zone, "zone");
//        ZoneRules rules = zone.getRules();
//        ZoneOffset offset = rules.getOffset(instant);
//        return ofEpochSecond(instant.getEpochSecond(), instant.getNano(), offset);
//    }

    /**
     * Obtains an instance of {@code LocalDateTime} using seconds from the
     * epoch of 1970-01-01T00:00:00Z.
     * <p>
     * This allows the {@link ChronoField#INSTANT_SECONDS epoch-second} field
     * to be converted to a local date-time. This is primarily intended for
     * low-level conversions rather than general application usage.
     *
     * @param epochSecond  the number of seconds from the epoch of 1970-01-01T00:00:00Z
     * @param nanoOfSecond  the nanosecond within the second, from 0 to 999,999,999
     * @param offset  the zone offset, not null
     * @return the local date-time, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    public static LocalDateTime ofEpochSecond(long epochSecond, int nanoOfSecond, ZoneOffset offset) {
        Jdk8Methods.requireNonNull(offset, "offset");
        long localSecond = epochSecond + offset.getTotalSeconds();  // overflow caught later
        long localEpochDay = Jdk8Methods.floorDiv(localSecond, SECONDS_PER_DAY);
        int secsOfDay = Jdk8Methods.floorMod(localSecond, SECONDS_PER_DAY);
        LocalDate date = LocalDate.ofEpochDay(localEpochDay);
        LocalTime time = LocalTime.ofSecondOfDay(secsOfDay, nanoOfSecond);
        return new LocalDateTime(date, time);
    }

//    //-----------------------------------------------------------------------
//    /**
//     * Obtains an instance of {@code LocalDateTime} from a temporal object.
//     * <p>
//     * A {@code TemporalAccessor} represents some form of date and time information.
//     * This factory converts the arbitrary temporal object to an instance of {@code LocalDateTime}.
//     * <p>
//     * The conversion extracts and combines {@code LocalDate} and {@code LocalTime}.
//     * <p>
//     * This method matches the signature of the functional interface {@link TemporalQuery}
//     * allowing it to be used as a query via method reference, {@code LocalDateTime::from}.
//     *
//     * @param temporal  the temporal object to convert, not null
//     * @return the local date-time, not null
//     * @throws DateTimeException if unable to convert to a {@code LocalDateTime}
//     */
//    public static LocalDateTime from(TemporalAccessor temporal) {
//        if (temporal instanceof LocalDateTime) {
//            return (LocalDateTime) temporal;
//        } else if (temporal instanceof ZonedDateTime) {
//            return ((ZonedDateTime) temporal).toLocalDateTime();
//        }
//        try {
//            LocalDate date = LocalDate.from(temporal);
//            LocalTime time = LocalTime.from(temporal);
//            return new LocalDateTime(date, time);
//        } catch (DateTimeException ex) {
//            throw new DateTimeException("Unable to obtain LocalDateTime from TemporalAccessor: " +
//                    temporal + ", type " + temporal.getClass().getName());
//        }
//    }
//
//    //-----------------------------------------------------------------------
//    /**
//     * Obtains an instance of {@code LocalDateTime} from a text string such as {@code 2007-12-23T10:15:30}.
//     * <p>
//     * The string must represent a valid date-time and is parsed using
//     * {@link org.threeten.bp.format.DateTimeFormatter#ISO_LOCAL_DATE_TIME}.
//     *
//     * @param text  the text to parse such as "2007-12-23T10:15:30", not null
//     * @return the parsed local date-time, not null
//     * @throws DateTimeParseException if the text cannot be parsed
//     */
//    public static LocalDateTime parse(CharSequence text) {
//        return parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
//    }
//
//    /**
//     * Obtains an instance of {@code LocalDateTime} from a text string using a specific formatter.
//     * <p>
//     * The text is parsed using the formatter, returning a date-time.
//     *
//     * @param text  the text to parse, not null
//     * @param formatter  the formatter to use, not null
//     * @return the parsed local date-time, not null
//     * @throws DateTimeParseException if the text cannot be parsed
//     */
//    public static LocalDateTime parse(CharSequence text, DateTimeFormatter formatter) {
//        Jdk8Methods.requireNonNull(formatter, "formatter");
//        return formatter.parse(text, LocalDateTime.FROM);
//    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param date  the date part of the date-time, validated not null
     * @param time  the time part of the date-time, validated not null
     */
    private LocalDateTime(LocalDate date, LocalTime time) {
        this.date = date;
        this.time = time;
    }

    /**
     * Returns a copy of this date-time with the new date and time, checking
     * to see if a new object is in fact required.
     *
     * @param newDate  the date of the new date-time, not null
     * @param newTime  the time of the new date-time, not null
     * @return the date-time, not null
     */
    private LocalDateTime with(LocalDate newDate, LocalTime newTime) {
        if (date == newDate && time == newTime) {
            return this;
        }
        return new LocalDateTime(newDate, newTime);
    }

//    //-----------------------------------------------------------------------
//    /**
//     * Checks if the specified field is supported.
//     * <p>
//     * This checks if this date-time can be queried for the specified field.
//     * If false, then calling the {@link #range(TemporalField) range} and
//     * {@link #get(TemporalField) get} methods will throw an exception.
//     * <p>
//     * If the field is a {@link ChronoField} then the query is implemented here.
//     * The supported fields are:
//     * <ul>
//     * <li>{@code NANO_OF_SECOND}
//     * <li>{@code NANO_OF_DAY}
//     * <li>{@code MICRO_OF_SECOND}
//     * <li>{@code MICRO_OF_DAY}
//     * <li>{@code MILLI_OF_SECOND}
//     * <li>{@code MILLI_OF_DAY}
//     * <li>{@code SECOND_OF_MINUTE}
//     * <li>{@code SECOND_OF_DAY}
//     * <li>{@code MINUTE_OF_HOUR}
//     * <li>{@code MINUTE_OF_DAY}
//     * <li>{@code HOUR_OF_AMPM}
//     * <li>{@code CLOCK_HOUR_OF_AMPM}
//     * <li>{@code HOUR_OF_DAY}
//     * <li>{@code CLOCK_HOUR_OF_DAY}
//     * <li>{@code AMPM_OF_DAY}
//     * <li>{@code DAY_OF_WEEK}
//     * <li>{@code ALIGNED_DAY_OF_WEEK_IN_MONTH}
//     * <li>{@code ALIGNED_DAY_OF_WEEK_IN_YEAR}
//     * <li>{@code DAY_OF_MONTH}
//     * <li>{@code DAY_OF_YEAR}
//     * <li>{@code EPOCH_DAY}
//     * <li>{@code ALIGNED_WEEK_OF_MONTH}
//     * <li>{@code ALIGNED_WEEK_OF_YEAR}
//     * <li>{@code MONTH_OF_YEAR}
//     * <li>{@code EPOCH_MONTH}
//     * <li>{@code YEAR_OF_ERA}
//     * <li>{@code YEAR}
//     * <li>{@code ERA}
//     * </ul>
//     * All other {@code ChronoField} instances will return false.
//     * <p>
//     * If the field is not a {@code ChronoField}, then the result of this method
//     * is obtained by invoking {@code TemporalField.isSupportedBy(TemporalAccessor)}
//     * passing {@code this} as the argument.
//     * Whether the field is supported is determined by the field.
//     *
//     * @param field  the field to check, null returns false
//     * @return true if the field is supported on this date-time, false if not
//     */
//    @Override
//    public boolean isSupported(TemporalField field) {
//        if (field instanceof ChronoField) {
//            return field.isDateBased() || field.isTimeBased();
//        }
//        return field != null && field.isSupportedBy(this);
//    }
//
//    @Override
//    public boolean isSupported(TemporalUnit unit) {
//        if (unit instanceof ChronoUnit) {
//            return unit.isDateBased() || unit.isTimeBased();
//        }
//        return unit != null && unit.isSupportedBy(this);
//    }
//
//    /**
//     * Gets the range of valid values for the specified field.
//     * <p>
//     * The range object expresses the minimum and maximum valid values for a field.
//     * This date-time is used to enhance the accuracy of the returned range.
//     * If it is not possible to return the range, because the field is not supported
//     * or for some other reason, an exception is thrown.
//     * <p>
//     * If the field is a {@link ChronoField} then the query is implemented here.
//     * The {@link #isSupported(TemporalField) supported fields} will return
//     * appropriate range instances.
//     * All other {@code ChronoField} instances will throw a {@code DateTimeException}.
//     * <p>
//     * If the field is not a {@code ChronoField}, then the result of this method
//     * is obtained by invoking {@code TemporalField.rangeRefinedBy(TemporalAccessor)}
//     * passing {@code this} as the argument.
//     * Whether the range can be obtained is determined by the field.
//     *
//     * @param field  the field to query the range for, not null
//     * @return the range of valid values for the field, not null
//     * @throws DateTimeException if the range for the field cannot be obtained
//     */
//    @Override
//    public ValueRange range(TemporalField field) {
//        if (field instanceof ChronoField) {
//            return (field.isTimeBased() ? time.range(field) : date.range(field));
//        }
//        return field.rangeRefinedBy(this);
//    }
//
//    /**
//     * Gets the value of the specified field from this date-time as an {@code int}.
//     * <p>
//     * This queries this date-time for the value for the specified field.
//     * The returned value will always be within the valid range of values for the field.
//     * If it is not possible to return the value, because the field is not supported
//     * or for some other reason, an exception is thrown.
//     * <p>
//     * If the field is a {@link ChronoField} then the query is implemented here.
//     * The {@link #isSupported(TemporalField) supported fields} will return valid
//     * values based on this date-time, except {@code NANO_OF_DAY}, {@code MICRO_OF_DAY},
//     * {@code EPOCH_DAY} and {@code EPOCH_MONTH} which are too large to fit in
//     * an {@code int} and throw a {@code DateTimeException}.
//     * All other {@code ChronoField} instances will throw a {@code DateTimeException}.
//     * <p>
//     * If the field is not a {@code ChronoField}, then the result of this method
//     * is obtained by invoking {@code TemporalField.getFrom(TemporalAccessor)}
//     * passing {@code this} as the argument. Whether the value can be obtained,
//     * and what the value represents, is determined by the field.
//     *
//     * @param field  the field to get, not null
//     * @return the value for the field
//     * @throws DateTimeException if a value for the field cannot be obtained
//     * @throws ArithmeticException if numeric overflow occurs
//     */
//    @Override
//    public int get(TemporalField field) {
//        if (field instanceof ChronoField) {
//            return (field.isTimeBased() ? time.get(field) : date.get(field));
//        }
//        return super.get(field);
//    }
//
//    /**
//     * Gets the value of the specified field from this date-time as a {@code long}.
//     * <p>
//     * This queries this date-time for the value for the specified field.
//     * If it is not possible to return the value, because the field is not supported
//     * or for some other reason, an exception is thrown.
//     * <p>
//     * If the field is a {@link ChronoField} then the query is implemented here.
//     * The {@link #isSupported(TemporalField) supported fields} will return valid
//     * values based on this date-time.
//     * All other {@code ChronoField} instances will throw a {@code DateTimeException}.
//     * <p>
//     * If the field is not a {@code ChronoField}, then the result of this method
//     * is obtained by invoking {@code TemporalField.getFrom(TemporalAccessor)}
//     * passing {@code this} as the argument. Whether the value can be obtained,
//     * and what the value represents, is determined by the field.
//     *
//     * @param field  the field to get, not null
//     * @return the value for the field
//     * @throws DateTimeException if a value for the field cannot be obtained
//     * @throws ArithmeticException if numeric overflow occurs
//     */
//    @Override
//    public long getLong(TemporalField field) {
//        if (field instanceof ChronoField) {
//            return (field.isTimeBased() ? time.getLong(field) : date.getLong(field));
//        }
//        return field.getFrom(this);
//    }

    //-----------------------------------------------------------------------
    /**
     * Gets the year field.
     * <p>
     * This method returns the primitive {@code int} value for the year.
     * <p>
     * The year returned by this method is proleptic as per {@code get(YEAR)}.
     * To obtain the year-of-era, use {@code get(YEAR_OF_ERA}.
     *
     * @return the year, from MIN_YEAR to MAX_YEAR
     */
    public int getYear() {
        return date.getYear();
    }

//    /**
//     * Gets the month-of-year field from 1 to 12.
//     * <p>
//     * This method returns the month as an {@code int} from 1 to 12.
//     * Application code is frequently clearer if the enum {@link Month}
//     * is used by calling {@link #getMonth()}.
//     *
//     * @return the month-of-year, from 1 to 12
//     * @see #getMonth()
//     */
//    public int getMonthValue() {
//        return date.getMonthValue();
//    }
//
//    /**
//     * Gets the month-of-year field using the {@code Month} enum.
//     * <p>
//     * This method returns the enum {@link Month} for the month.
//     * This avoids confusion as to what {@code int} values mean.
//     * If you need access to the primitive {@code int} value then the enum
//     * provides the {@link Month#getValue() int value}.
//     *
//     * @return the month-of-year, not null
//     * @see #getMonthValue()
//     */
//    public Month getMonth() {
//        return date.getMonth();
//    }
//
//    /**
//     * Gets the day-of-month field.
//     * <p>
//     * This method returns the primitive {@code int} value for the day-of-month.
//     *
//     * @return the day-of-month, from 1 to 31
//     */
//    public int getDayOfMonth() {
//        return date.getDayOfMonth();
//    }
//
//    /**
//     * Gets the day-of-year field.
//     * <p>
//     * This method returns the primitive {@code int} value for the day-of-year.
//     *
//     * @return the day-of-year, from 1 to 365, or 366 in a leap year
//     */
//    public int getDayOfYear() {
//        return date.getDayOfYear();
//    }
//
//    /**
//     * Gets the day-of-week field, which is an enum {@code DayOfWeek}.
//     * <p>
//     * This method returns the enum {@link DayOfWeek} for the day-of-week.
//     * This avoids confusion as to what {@code int} values mean.
//     * If you need access to the primitive {@code int} value then the enum
//     * provides the {@link DayOfWeek#getValue() int value}.
//     * <p>
//     * Additional information can be obtained from the {@code DayOfWeek}.
//     * This includes textual names of the values.
//     *
//     * @return the day-of-week, not null
//     */
//    public DayOfWeek getDayOfWeek() {
//        return date.getDayOfWeek();
//    }

    //-----------------------------------------------------------------------
    /**
     * Gets the hour-of-day field.
     *
     * @return the hour-of-day, from 0 to 23
     */
    public int getHour() {
        return time.getHour();
    }

    /**
     * Gets the minute-of-hour field.
     *
     * @return the minute-of-hour, from 0 to 59
     */
    public int getMinute() {
        return time.getMinute();
    }

    /**
     * Gets the second-of-minute field.
     *
     * @return the second-of-minute, from 0 to 59
     */
    public int getSecond() {
        return time.getSecond();
    }

    /**
     * Gets the nano-of-second field.
     *
     * @return the nano-of-second, from 0 to 999,999,999
     */
    public int getNano() {
        return time.getNano();
    }

//    //-----------------------------------------------------------------------
//    /**
//     * Returns an adjusted copy of this date-time.
//     * <p>
//     * This returns a new {@code LocalDateTime}, based on this one, with the date-time adjusted.
//     * The adjustment takes place using the specified adjuster strategy object.
//     * Read the documentation of the adjuster to understand what adjustment will be made.
//     * <p>
//     * A simple adjuster might simply set the one of the fields, such as the year field.
//     * A more complex adjuster might set the date to the last day of the month.
//     * A selection of common adjustments is provided in {@link TemporalAdjusters}.
//     * These include finding the "last day of the month" and "next Wednesday".
//     * Key date-time classes also implement the {@code TemporalAdjuster} interface,
//     * such as {@link Month} and {@link MonthDay MonthDay}.
//     * The adjuster is responsible for handling special cases, such as the varying
//     * lengths of month and leap years.
//     * <p>
//     * For example this code returns a date on the last day of July:
//     * <pre>
//     *  import static org.threeten.bp.Month.*;
//     *  import static org.threeten.bp.temporal.Adjusters.*;
//     *
//     *  result = localDateTime.with(JULY).with(lastDayOfMonth());
//     * </pre>
//     * <p>
//     * The classes {@link LocalDate} and {@link LocalTime} implement {@code TemporalAdjuster},
//     * thus this method can be used to change the date, time or offset:
//     * <pre>
//     *  result = localDateTime.with(date);
//     *  result = localDateTime.with(time);
//     * </pre>
//     * <p>
//     * The result of this method is obtained by invoking the
//     * {@link TemporalAdjuster#adjustInto(Temporal)} method on the
//     * specified adjuster passing {@code this} as the argument.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param adjuster the adjuster to use, not null
//     * @return a {@code LocalDateTime} based on {@code this} with the adjustment made, not null
//     * @throws DateTimeException if the adjustment cannot be made
//     * @throws ArithmeticException if numeric overflow occurs
//     */
//    @Override
//    public LocalDateTime with(TemporalAdjuster adjuster) {
//        // optimizations
//        if (adjuster instanceof LocalDate) {
//            return with((LocalDate) adjuster, time);
//        } else if (adjuster instanceof LocalTime) {
//            return with(date, (LocalTime) adjuster);
//        } else if (adjuster instanceof LocalDateTime) {
//            return (LocalDateTime) adjuster;
//        }
//        return (LocalDateTime) adjuster.adjustInto(this);
//    }
//
//    /**
//     * Returns a copy of this date-time with the specified field set to a new value.
//     * <p>
//     * This returns a new {@code LocalDateTime}, based on this one, with the value
//     * for the specified field changed.
//     * This can be used to change any supported field, such as the year, month or day-of-month.
//     * If it is not possible to set the value, because the field is not supported or for
//     * some other reason, an exception is thrown.
//     * <p>
//     * In some cases, changing the specified field can cause the resulting date-time to become invalid,
//     * such as changing the month from 31st January to February would make the day-of-month invalid.
//     * In cases like this, the field is responsible for resolving the date. Typically it will choose
//     * the previous valid date, which would be the last valid day of February in this example.
//     * <p>
//     * If the field is a {@link ChronoField} then the adjustment is implemented here.
//     * The {@link #isSupported(TemporalField) supported fields} will behave as per
//     * the matching method on {@link LocalDate#with(TemporalField, long) LocalDate}
//     * or {@link LocalTime#with(TemporalField, long) LocalTime}.
//     * All other {@code ChronoField} instances will throw a {@code DateTimeException}.
//     * <p>
//     * If the field is not a {@code ChronoField}, then the result of this method
//     * is obtained by invoking {@code TemporalField.adjustInto(Temporal, long)}
//     * passing {@code this} as the argument. In this case, the field determines
//     * whether and how to adjust the instant.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param field  the field to set in the result, not null
//     * @param newValue  the new value of the field in the result
//     * @return a {@code LocalDateTime} based on {@code this} with the specified field set, not null
//     * @throws DateTimeException if the field cannot be set
//     * @throws ArithmeticException if numeric overflow occurs
//     */
//    @Override
//    public LocalDateTime with(TemporalField field, long newValue) {
//        if (field instanceof ChronoField) {
//            if (field.isTimeBased()) {
//                return with(date, time.with(field, newValue));
//            } else {
//                return with(date.with(field, newValue), time);
//            }
//        }
//        return field.adjustInto(this, newValue);
//    }
//
//    //-----------------------------------------------------------------------
//    /**
//     * Returns a copy of this {@code LocalDateTime} with the year altered.
//     * The time does not affect the calculation and will be the same in the result.
//     * If the day-of-month is invalid for the year, it will be changed to the last valid day of the month.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param year  the year to set in the result, from MIN_YEAR to MAX_YEAR
//     * @return a {@code LocalDateTime} based on this date-time with the requested year, not null
//     * @throws DateTimeException if the year value is invalid
//     */
//    public LocalDateTime withYear(int year) {
//        return with(date.withYear(year), time);
//    }
//
//    /**
//     * Returns a copy of this {@code LocalDateTime} with the month-of-year altered.
//     * The time does not affect the calculation and will be the same in the result.
//     * If the day-of-month is invalid for the year, it will be changed to the last valid day of the month.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param month  the month-of-year to set in the result, from 1 (January) to 12 (December)
//     * @return a {@code LocalDateTime} based on this date-time with the requested month, not null
//     * @throws DateTimeException if the month-of-year value is invalid
//     */
//    public LocalDateTime withMonth(int month) {
//        return with(date.withMonth(month), time);
//    }
//
//    /**
//     * Returns a copy of this {@code LocalDateTime} with the day-of-month altered.
//     * If the resulting {@code LocalDateTime} is invalid, an exception is thrown.
//     * The time does not affect the calculation and will be the same in the result.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param dayOfMonth  the day-of-month to set in the result, from 1 to 28-31
//     * @return a {@code LocalDateTime} based on this date-time with the requested day, not null
//     * @throws DateTimeException if the day-of-month value is invalid
//     * @throws DateTimeException if the day-of-month is invalid for the month-year
//     */
//    public LocalDateTime withDayOfMonth(int dayOfMonth) {
//        return with(date.withDayOfMonth(dayOfMonth), time);
//    }
//
//    /**
//     * Returns a copy of this {@code LocalDateTime} with the day-of-year altered.
//     * If the resulting {@code LocalDateTime} is invalid, an exception is thrown.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param dayOfYear  the day-of-year to set in the result, from 1 to 365-366
//     * @return a {@code LocalDateTime} based on this date with the requested day, not null
//     * @throws DateTimeException if the day-of-year value is invalid
//     * @throws DateTimeException if the day-of-year is invalid for the year
//     */
//    public LocalDateTime withDayOfYear(int dayOfYear) {
//        return with(date.withDayOfYear(dayOfYear), time);
//    }
//
//    //-----------------------------------------------------------------------
//    /**
//     * Returns a copy of this {@code LocalDateTime} with the hour-of-day value altered.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param hour  the hour-of-day to set in the result, from 0 to 23
//     * @return a {@code LocalDateTime} based on this date-time with the requested hour, not null
//     * @throws DateTimeException if the hour value is invalid
//     */
//    public LocalDateTime withHour(int hour) {
//        LocalTime newTime = time.withHour(hour);
//        return with(date, newTime);
//    }
//
//    /**
//     * Returns a copy of this {@code LocalDateTime} with the minute-of-hour value altered.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param minute  the minute-of-hour to set in the result, from 0 to 59
//     * @return a {@code LocalDateTime} based on this date-time with the requested minute, not null
//     * @throws DateTimeException if the minute value is invalid
//     */
//    public LocalDateTime withMinute(int minute) {
//        LocalTime newTime = time.withMinute(minute);
//        return with(date, newTime);
//    }
//
//    /**
//     * Returns a copy of this {@code LocalDateTime} with the second-of-minute value altered.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param second  the second-of-minute to set in the result, from 0 to 59
//     * @return a {@code LocalDateTime} based on this date-time with the requested second, not null
//     * @throws DateTimeException if the second value is invalid
//     */
//    public LocalDateTime withSecond(int second) {
//        LocalTime newTime = time.withSecond(second);
//        return with(date, newTime);
//    }
//
//    /**
//     * Returns a copy of this {@code LocalDateTime} with the nano-of-second value altered.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param nanoOfSecond  the nano-of-second to set in the result, from 0 to 999,999,999
//     * @return a {@code LocalDateTime} based on this date-time with the requested nanosecond, not null
//     * @throws DateTimeException if the nano value is invalid
//     */
//    public LocalDateTime withNano(int nanoOfSecond) {
//        LocalTime newTime = time.withNano(nanoOfSecond);
//        return with(date, newTime);
//    }
//
//    //-----------------------------------------------------------------------
//    /**
//     * Returns a copy of this {@code LocalDateTime} with the time truncated.
//     * <p>
//     * Truncation returns a copy of the original date-time with fields
//     * smaller than the specified unit set to zero.
//     * For example, truncating with the {@link ChronoUnit#MINUTES minutes} unit
//     * will set the second-of-minute and nano-of-second field to zero.
//     * <p>
//     * The unit must have a {@linkplain TemporalUnit#getDuration() duration}
//     * that divides into the length of a standard day without remainder.
//     * This includes all supplied time units on {@link ChronoUnit} and
//     * {@link ChronoUnit#DAYS DAYS}. Other units throw an exception.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param unit  the unit to truncate to, not null
//     * @return a {@code LocalDateTime} based on this date-time with the time truncated, not null
//     * @throws DateTimeException if unable to truncate
//     */
//    public LocalDateTime truncatedTo(TemporalUnit unit) {
//        return with(date, time.truncatedTo(unit));
//    }
//
//    //-----------------------------------------------------------------------
//    /**
//     * Returns a copy of this date-time with the specified period added.
//     * <p>
//     * This method returns a new date-time based on this time with the specified period added.
//     * The amount is typically {@link Period} but may be any other type implementing
//     * the {@link TemporalAmount} interface.
//     * The calculation is delegated to the specified adjuster, which typically calls
//     * back to {@link #plus(long, TemporalUnit)}.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param amount  the amount to add, not null
//     * @return a {@code LocalDateTime} based on this date-time with the addition made, not null
//     * @throws DateTimeException if the addition cannot be made
//     * @throws ArithmeticException if numeric overflow occurs
//     */
//    @Override
//    public LocalDateTime plus(TemporalAmount amount) {
//        return (LocalDateTime) amount.addTo(this);
//    }
//
//    /**
//     * Returns a copy of this date-time with the specified period added.
//     * <p>
//     * This method returns a new date-time based on this date-time with the specified period added.
//     * This can be used to add any period that is defined by a unit, for example to add years, months or days.
//     * The unit is responsible for the details of the calculation, including the resolution
//     * of any edge cases in the calculation.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param amountToAdd  the amount of the unit to add to the result, may be negative
//     * @param unit  the unit of the period to add, not null
//     * @return a {@code LocalDateTime} based on this date-time with the specified period added, not null
//     * @throws DateTimeException if the unit cannot be added to this type
//     */
//    @Override
//    public LocalDateTime plus(long amountToAdd, TemporalUnit unit) {
//        if (unit instanceof ChronoUnit) {
//            ChronoUnit f = (ChronoUnit) unit;
//            switch (f) {
//                case NANOS: return plusNanos(amountToAdd);
//                case MICROS: return plusDays(amountToAdd / MICROS_PER_DAY).plusNanos((amountToAdd % MICROS_PER_DAY) * 1000);
//                case MILLIS: return plusDays(amountToAdd / MILLIS_PER_DAY).plusNanos((amountToAdd % MILLIS_PER_DAY) * 1000000);
//                case SECONDS: return plusSeconds(amountToAdd);
//                case MINUTES: return plusMinutes(amountToAdd);
//                case HOURS: return plusHours(amountToAdd);
//                case HALF_DAYS: return plusDays(amountToAdd / 256).plusHours((amountToAdd % 256) * 12);  // no overflow (256 is multiple of 2)
//            }
//            return with(date.plus(amountToAdd, unit), time);
//        }
//        return unit.addTo(this, amountToAdd);
//    }
//
//    //-----------------------------------------------------------------------
//    /**
//     * Returns a copy of this {@code LocalDateTime} with the specified period in years added.
//     * <p>
//     * This method adds the specified amount to the years field in three steps:
//     * <ol>
//     * <li>Add the input years to the year field</li>
//     * <li>Check if the resulting date would be invalid</li>
//     * <li>Adjust the day-of-month to the last valid day if necessary</li>
//     * </ol>
//     * <p>
//     * For example, 2008-02-29 (leap year) plus one year would result in the
//     * invalid date 2009-02-29 (standard year). Instead of returning an invalid
//     * result, the last valid day of the month, 2009-02-28, is selected instead.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param years  the years to add, may be negative
//     * @return a {@code LocalDateTime} based on this date-time with the years added, not null
//     * @throws DateTimeException if the result exceeds the supported date range
//     */
//    public LocalDateTime plusYears(long years) {
//        LocalDate newDate = date.plusYears(years);
//        return with(newDate, time);
//    }
//
//    /**
//     * Returns a copy of this {@code LocalDateTime} with the specified period in months added.
//     * <p>
//     * This method adds the specified amount to the months field in three steps:
//     * <ol>
//     * <li>Add the input months to the month-of-year field</li>
//     * <li>Check if the resulting date would be invalid</li>
//     * <li>Adjust the day-of-month to the last valid day if necessary</li>
//     * </ol>
//     * <p>
//     * For example, 2007-03-31 plus one month would result in the invalid date
//     * 2007-04-31. Instead of returning an invalid result, the last valid day
//     * of the month, 2007-04-30, is selected instead.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param months  the months to add, may be negative
//     * @return a {@code LocalDateTime} based on this date-time with the months added, not null
//     * @throws DateTimeException if the result exceeds the supported date range
//     */
//    public LocalDateTime plusMonths(long months) {
//        LocalDate newDate = date.plusMonths(months);
//        return with(newDate, time);
//    }
//
//    /**
//     * Returns a copy of this {@code LocalDateTime} with the specified period in weeks added.
//     * <p>
//     * This method adds the specified amount in weeks to the days field incrementing
//     * the month and year fields as necessary to ensure the result remains valid.
//     * The result is only invalid if the maximum/minimum year is exceeded.
//     * <p>
//     * For example, 2008-12-31 plus one week would result in 2009-01-07.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param weeks  the weeks to add, may be negative
//     * @return a {@code LocalDateTime} based on this date-time with the weeks added, not null
//     * @throws DateTimeException if the result exceeds the supported date range
//     */
//    public LocalDateTime plusWeeks(long weeks) {
//        LocalDate newDate = date.plusWeeks(weeks);
//        return with(newDate, time);
//    }
//
//    /**
//     * Returns a copy of this {@code LocalDateTime} with the specified period in days added.
//     * <p>
//     * This method adds the specified amount to the days field incrementing the
//     * month and year fields as necessary to ensure the result remains valid.
//     * The result is only invalid if the maximum/minimum year is exceeded.
//     * <p>
//     * For example, 2008-12-31 plus one day would result in 2009-01-01.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param days  the days to add, may be negative
//     * @return a {@code LocalDateTime} based on this date-time with the days added, not null
//     * @throws DateTimeException if the result exceeds the supported date range
//     */
//    public LocalDateTime plusDays(long days) {
//        LocalDate newDate = date.plusDays(days);
//        return with(newDate, time);
//    }
//
//    //-----------------------------------------------------------------------
//    /**
//     * Returns a copy of this {@code LocalDateTime} with the specified period in hours added.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param hours  the hours to add, may be negative
//     * @return a {@code LocalDateTime} based on this date-time with the hours added, not null
//     * @throws DateTimeException if the result exceeds the supported date range
//     */
//    public LocalDateTime plusHours(long hours) {
//        return plusWithOverflow(date, hours, 0, 0, 0, 1);
//    }
//
//    /**
//     * Returns a copy of this {@code LocalDateTime} with the specified period in minutes added.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param minutes  the minutes to add, may be negative
//     * @return a {@code LocalDateTime} based on this date-time with the minutes added, not null
//     * @throws DateTimeException if the result exceeds the supported date range
//     */
//    public LocalDateTime plusMinutes(long minutes) {
//        return plusWithOverflow(date, 0, minutes, 0, 0, 1);
//    }
//
    /**
     * Returns a copy of this {@code LocalDateTime} with the specified period in seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to add, may be negative
     * @return a {@code LocalDateTime} based on this date-time with the seconds added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public LocalDateTime plusSeconds(long seconds) {
        return plusWithOverflow(date, 0, 0, seconds, 0, 1);
    }
//
//    /**
//     * Returns a copy of this {@code LocalDateTime} with the specified period in nanoseconds added.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param nanos  the nanos to add, may be negative
//     * @return a {@code LocalDateTime} based on this date-time with the nanoseconds added, not null
//     * @throws DateTimeException if the result exceeds the supported date range
//     */
//    public LocalDateTime plusNanos(long nanos) {
//        return plusWithOverflow(date, 0, 0, 0, nanos, 1);
//    }
//
//    //-----------------------------------------------------------------------
//    /**
//     * Returns a copy of this date-time with the specified period subtracted.
//     * <p>
//     * This method returns a new date-time based on this time with the specified period subtracted.
//     * The amount is typically {@link Period} but may be any other type implementing
//     * the {@link TemporalAmount} interface.
//     * The calculation is delegated to the specified adjuster, which typically calls
//     * back to {@link #minus(long, TemporalUnit)}.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param amount  the amount to subtract, not null
//     * @return a {@code LocalDateTime} based on this date-time with the subtraction made, not null
//     * @throws DateTimeException if the subtraction cannot be made
//     * @throws ArithmeticException if numeric overflow occurs
//     */
//    @Override
//    public LocalDateTime minus(TemporalAmount amount) {
//        return (LocalDateTime) amount.subtractFrom(this);
//    }
//
//    /**
//     * Returns a copy of this date-time with the specified period subtracted.
//     * <p>
//     * This method returns a new date-time based on this date-time with the specified period subtracted.
//     * This can be used to subtract any period that is defined by a unit, for example to subtract years, months or days.
//     * The unit is responsible for the details of the calculation, including the resolution
//     * of any edge cases in the calculation.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param amountToSubtract  the amount of the unit to subtract from the result, may be negative
//     * @param unit  the unit of the period to subtract, not null
//     * @return a {@code LocalDateTime} based on this date-time with the specified period subtracted, not null
//     * @throws DateTimeException if the unit cannot be added to this type
//     */
//    @Override
//    public LocalDateTime minus(long amountToSubtract, TemporalUnit unit) {
//        return (amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1, unit) : plus(-amountToSubtract, unit));
//    }
//
//    //-----------------------------------------------------------------------
//    /**
//     * Returns a copy of this {@code LocalDateTime} with the specified period in years subtracted.
//     * <p>
//     * This method subtracts the specified amount from the years field in three steps:
//     * <ol>
//     * <li>Subtract the input years from the year field</li>
//     * <li>Check if the resulting date would be invalid</li>
//     * <li>Adjust the day-of-month to the last valid day if necessary</li>
//     * </ol>
//     * <p>
//     * For example, 2008-02-29 (leap year) minus one year would result in the
//     * invalid date 2009-02-29 (standard year). Instead of returning an invalid
//     * result, the last valid day of the month, 2009-02-28, is selected instead.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param years  the years to subtract, may be negative
//     * @return a {@code LocalDateTime} based on this date-time with the years subtracted, not null
//     * @throws DateTimeException if the result exceeds the supported date range
//     */
//    public LocalDateTime minusYears(long years) {
//        return (years == Long.MIN_VALUE ? plusYears(Long.MAX_VALUE).plusYears(1) : plusYears(-years));
//    }
//
//    /**
//     * Returns a copy of this {@code LocalDateTime} with the specified period in months subtracted.
//     * <p>
//     * This method subtracts the specified amount from the months field in three steps:
//     * <ol>
//     * <li>Subtract the input months from the month-of-year field</li>
//     * <li>Check if the resulting date would be invalid</li>
//     * <li>Adjust the day-of-month to the last valid day if necessary</li>
//     * </ol>
//     * <p>
//     * For example, 2007-03-31 minus one month would result in the invalid date
//     * 2007-04-31. Instead of returning an invalid result, the last valid day
//     * of the month, 2007-04-30, is selected instead.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param months  the months to subtract, may be negative
//     * @return a {@code LocalDateTime} based on this date-time with the months subtracted, not null
//     * @throws DateTimeException if the result exceeds the supported date range
//     */
//    public LocalDateTime minusMonths(long months) {
//        return (months == Long.MIN_VALUE ? plusMonths(Long.MAX_VALUE).plusMonths(1) : plusMonths(-months));
//    }
//
//    /**
//     * Returns a copy of this {@code LocalDateTime} with the specified period in weeks subtracted.
//     * <p>
//     * This method subtracts the specified amount in weeks from the days field decrementing
//     * the month and year fields as necessary to ensure the result remains valid.
//     * The result is only invalid if the maximum/minimum year is exceeded.
//     * <p>
//     * For example, 2009-01-07 minus one week would result in 2008-12-31.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param weeks  the weeks to subtract, may be negative
//     * @return a {@code LocalDateTime} based on this date-time with the weeks subtracted, not null
//     * @throws DateTimeException if the result exceeds the supported date range
//     */
//    public LocalDateTime minusWeeks(long weeks) {
//        return (weeks == Long.MIN_VALUE ? plusWeeks(Long.MAX_VALUE).plusWeeks(1) : plusWeeks(-weeks));
//    }
//
//    /**
//     * Returns a copy of this {@code LocalDateTime} with the specified period in days subtracted.
//     * <p>
//     * This method subtracts the specified amount from the days field incrementing the
//     * month and year fields as necessary to ensure the result remains valid.
//     * The result is only invalid if the maximum/minimum year is exceeded.
//     * <p>
//     * For example, 2009-01-01 minus one day would result in 2008-12-31.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param days  the days to subtract, may be negative
//     * @return a {@code LocalDateTime} based on this date-time with the days subtracted, not null
//     * @throws DateTimeException if the result exceeds the supported date range
//     */
//    public LocalDateTime minusDays(long days) {
//        return (days == Long.MIN_VALUE ? plusDays(Long.MAX_VALUE).plusDays(1) : plusDays(-days));
//    }
//
//    //-----------------------------------------------------------------------
//    /**
//     * Returns a copy of this {@code LocalDateTime} with the specified period in hours subtracted.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param hours  the hours to subtract, may be negative
//     * @return a {@code LocalDateTime} based on this date-time with the hours subtracted, not null
//     * @throws DateTimeException if the result exceeds the supported date range
//     */
//    public LocalDateTime minusHours(long hours) {
//        return plusWithOverflow(date, hours, 0, 0, 0, -1);
//    }
//
//    /**
//     * Returns a copy of this {@code LocalDateTime} with the specified period in minutes subtracted.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param minutes  the minutes to subtract, may be negative
//     * @return a {@code LocalDateTime} based on this date-time with the minutes subtracted, not null
//     * @throws DateTimeException if the result exceeds the supported date range
//     */
//    public LocalDateTime minusMinutes(long minutes) {
//        return plusWithOverflow(date, 0, minutes, 0, 0, -1);
//    }
//
//    /**
//     * Returns a copy of this {@code LocalDateTime} with the specified period in seconds subtracted.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param seconds  the seconds to subtract, may be negative
//     * @return a {@code LocalDateTime} based on this date-time with the seconds subtracted, not null
//     * @throws DateTimeException if the result exceeds the supported date range
//     */
//    public LocalDateTime minusSeconds(long seconds) {
//        return plusWithOverflow(date, 0, 0, seconds, 0, -1);
//    }
//
//    /**
//     * Returns a copy of this {@code LocalDateTime} with the specified period in nanoseconds subtracted.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param nanos  the nanos to subtract, may be negative
//     * @return a {@code LocalDateTime} based on this date-time with the nanoseconds subtracted, not null
//     * @throws DateTimeException if the result exceeds the supported date range
//     */
//    public LocalDateTime minusNanos(long nanos) {
//        return plusWithOverflow(date, 0, 0, 0, nanos, -1);
//    }
//
//    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code LocalDateTime} with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param newDate  the new date to base the calculation on, not null
     * @param hours  the hours to add, may be negative
     * @param minutes the minutes to add, may be negative
     * @param seconds the seconds to add, may be negative
     * @param nanos the nanos to add, may be negative
     * @param sign  the sign to determine add or subtract
     * @return the combined result, not null
     */
    private LocalDateTime plusWithOverflow(LocalDate newDate, long hours, long minutes, long seconds, long nanos, int sign) {
        // 9223372036854775808 long, 2147483648 int
        if ((hours | minutes | seconds | nanos) == 0) {
            return with(newDate, time);
        }
        long totDays = nanos / NANOS_PER_DAY +             //   max/24*60*60*1B
                seconds / SECONDS_PER_DAY +                //   max/24*60*60
                minutes / MINUTES_PER_DAY +                //   max/24*60
                hours / HOURS_PER_DAY;                     //   max/24
        totDays *= sign;                                   // total max*0.4237...
        long totNanos = nanos % NANOS_PER_DAY +                    //   max  86400000000000
                (seconds % SECONDS_PER_DAY) * NANOS_PER_SECOND +   //   max  86400000000000
                (minutes % MINUTES_PER_DAY) * NANOS_PER_MINUTE +   //   max  86400000000000
                (hours % HOURS_PER_DAY) * NANOS_PER_HOUR;          //   max  86400000000000
        long curNoD = time.toNanoOfDay();                       //   max  86400000000000
        totNanos = totNanos * sign + curNoD;                    // total 432000000000000
        totDays += Jdk8Methods.floorDiv(totNanos, NANOS_PER_DAY);
        long newNoD = Jdk8Methods.floorMod(totNanos, NANOS_PER_DAY);
        LocalTime newTime = (newNoD == curNoD ? time : LocalTime.ofNanoOfDay(newNoD));
        return with(newDate.plusDays(totDays), newTime);
    }
//
//    //-----------------------------------------------------------------------
//    /**
//     * Queries this date-time using the specified query.
//     * <p>
//     * This queries this date-time using the specified query strategy object.
//     * The {@code TemporalQuery} object defines the logic to be used to
//     * obtain the result. Read the documentation of the query to understand
//     * what the result of this method will be.
//     * <p>
//     * The result of this method is obtained by invoking the
//     * {@link TemporalQuery#queryFrom(TemporalAccessor)} method on the
//     * specified query passing {@code this} as the argument.
//     *
//     * @param <R> the type of the result
//     * @param query  the query to invoke, not null
//     * @return the query result, null may be returned (defined by the query)
//     * @throws DateTimeException if unable to query (defined by the query)
//     * @throws ArithmeticException if numeric overflow occurs (defined by the query)
//     */
//    @SuppressWarnings("unchecked")
//    @Override  // override for Javadoc
//    public <R> R query(TemporalQuery<R> query) {
//        if (query == TemporalQueries.localDate()) {
//            return (R) toLocalDate();
//        }
//        return super.query(query);
//    }
//
//    /**
//     * Adjusts the specified temporal object to have the same date and time as this object.
//     * <p>
//     * This returns a temporal object of the same observable type as the input
//     * with the date and time changed to be the same as this.
//     * <p>
//     * The adjustment is equivalent to using {@link Temporal#with(TemporalField, long)}
//     * twice, passing {@link ChronoField#EPOCH_DAY} and
//     * {@link ChronoField#NANO_OF_DAY} as the fields.
//     * <p>
//     * In most cases, it is clearer to reverse the calling pattern by using
//     * {@link Temporal#with(TemporalAdjuster)}:
//     * <pre>
//     *   // these two lines are equivalent, but the second approach is recommended
//     *   temporal = thisLocalDateTime.adjustInto(temporal);
//     *   temporal = temporal.with(thisLocalDateTime);
//     * </pre>
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param temporal  the target object to be adjusted, not null
//     * @return the adjusted object, not null
//     * @throws DateTimeException if unable to make the adjustment
//     * @throws ArithmeticException if numeric overflow occurs
//     */
//    @Override  // override for Javadoc
//    public Temporal adjustInto(Temporal temporal) {
//        return super.adjustInto(temporal);
//    }
//
//    /**
//     * Calculates the period between this date-time and another date-time in
//     * terms of the specified unit.
//     * <p>
//     * This calculates the period between two date-times in terms of a single unit.
//     * The start and end points are {@code this} and the specified date-time.
//     * The result will be negative if the end is before the start.
//     * The {@code Temporal} passed to this method must be a {@code LocalDateTime}.
//     * For example, the period in days between two date-times can be calculated
//     * using {@code startDateTime.until(endDateTime, DAYS)}.
//     * <p>
//     * The calculation returns a whole number, representing the number of
//     * complete units between the two date-times.
//     * For example, the period in months between 2012-06-15T00:00 and 2012-08-14T23:59
//     * will only be one month as it is one minute short of two months.
//     * <p>
//     * This method operates in association with {@link TemporalUnit#between}.
//     * The result of this method is a {@code long} representing the amount of
//     * the specified unit. By contrast, the result of {@code between} is an
//     * object that can be used directly in addition/subtraction:
//     * <pre>
//     *   long period = start.until(end, MONTHS);   // this method
//     *   dateTime.plus(MONTHS.between(start, end));      // use in plus/minus
//     * </pre>
//     * <p>
//     * The calculation is implemented in this method for {@link ChronoUnit}.
//     * The units {@code NANOS}, {@code MICROS}, {@code MILLIS}, {@code SECONDS},
//     * {@code MINUTES}, {@code HOURS} and {@code HALF_DAYS}, {@code DAYS},
//     * {@code WEEKS}, {@code MONTHS}, {@code YEARS}, {@code DECADES},
//     * {@code CENTURIES}, {@code MILLENNIA} and {@code ERAS} are supported.
//     * Other {@code ChronoUnit} values will throw an exception.
//     * <p>
//     * If the unit is not a {@code ChronoUnit}, then the result of this method
//     * is obtained by invoking {@code TemporalUnit.between(Temporal, Temporal)}
//     * passing {@code this} as the first argument and the input temporal as
//     * the second argument.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param endExclusive  the end date-time, which is converted to a {@code LocalDateTime}, not null
//     * @param unit  the unit to measure the period in, not null
//     * @return the amount of the period between this date-time and the end date-time
//     * @throws DateTimeException if the period cannot be calculated
//     * @throws ArithmeticException if numeric overflow occurs
//     */
//    @Override
//    public long until(Temporal endExclusive, TemporalUnit unit) {
//        LocalDateTime end = LocalDateTime.from(endExclusive);
//        if (unit instanceof ChronoUnit) {
//            ChronoUnit f = (ChronoUnit) unit;
//            if (f.isTimeBased()) {
//                long daysUntil = date.daysUntil(end.date);
//                long timeUntil = end.time.toNanoOfDay() - time.toNanoOfDay();
//                if (daysUntil > 0 && timeUntil < 0) {
//                    daysUntil--;
//                    timeUntil += NANOS_PER_DAY;
//                } else if (daysUntil < 0 && timeUntil > 0) {
//                    daysUntil++;
//                    timeUntil -= NANOS_PER_DAY;
//                }
//                long amount = daysUntil;
//                switch (f) {
//                    case NANOS:
//                        amount = Jdk8Methods.safeMultiply(amount, NANOS_PER_DAY);
//                        return Jdk8Methods.safeAdd(amount, timeUntil);
//                    case MICROS:
//                        amount = Jdk8Methods.safeMultiply(amount, MICROS_PER_DAY);
//                        return Jdk8Methods.safeAdd(amount, timeUntil / 1000);
//                    case MILLIS:
//                        amount = Jdk8Methods.safeMultiply(amount, MILLIS_PER_DAY);
//                        return Jdk8Methods.safeAdd(amount, timeUntil / 1000000);
//                    case SECONDS:
//                        amount = Jdk8Methods.safeMultiply(amount, SECONDS_PER_DAY);
//                        return Jdk8Methods.safeAdd(amount, timeUntil / NANOS_PER_SECOND);
//                    case MINUTES:
//                        amount = Jdk8Methods.safeMultiply(amount, MINUTES_PER_DAY);
//                        return Jdk8Methods.safeAdd(amount, timeUntil / NANOS_PER_MINUTE);
//                    case HOURS:
//                        amount = Jdk8Methods.safeMultiply(amount, HOURS_PER_DAY);
//                        return Jdk8Methods.safeAdd(amount, timeUntil / NANOS_PER_HOUR);
//                    case HALF_DAYS:
//                        amount = Jdk8Methods.safeMultiply(amount, 2);
//                        return Jdk8Methods.safeAdd(amount, timeUntil / (NANOS_PER_HOUR * 12));
//                }
//                throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
//            }
//            LocalDate endDate = end.date;
//            if (endDate.isAfter(date) && end.time.isBefore(time)) {
//                endDate = endDate.minusDays(1);
//            } else if (endDate.isBefore(date) && end.time.isAfter(time)) {
//                endDate = endDate.plusDays(1);
//            }
//            return date.until(endDate, unit);
//        }
//        return unit.between(this, end);
//    }
//
//    //-----------------------------------------------------------------------
//    /**
//     * Combines this date-time with an offset to create an {@code OffsetDateTime}.
//     * <p>
//     * This returns an {@code OffsetDateTime} formed from this date-time at the specified offset.
//     * All possible combinations of date-time and offset are valid.
//     *
//     * @param offset  the offset to combine with, not null
//     * @return the offset date-time formed from this date-time and the specified offset, not null
//     */
//    public OffsetDateTime atOffset(ZoneOffset offset) {
//        return OffsetDateTime.of(this, offset);
//    }
//
//    /**
//     * Combines this date-time with a time-zone to create a {@code ZonedDateTime}.
//     * <p>
//     * This returns a {@code ZonedDateTime} formed from this date-time at the
//     * specified time-zone. The result will match this date-time as closely as possible.
//     * Time-zone rules, such as daylight savings, mean that not every local date-time
//     * is valid for the specified zone, thus the local date-time may be adjusted.
//     * <p>
//     * The local date-time is resolved to a single instant on the time-line.
//     * This is achieved by finding a valid offset from UTC/Greenwich for the local
//     * date-time as defined by the {@link ZoneRules rules} of the zone ID.
//     *<p>
//     * In most cases, there is only one valid offset for a local date-time.
//     * In the case of an overlap, where clocks are set back, there are two valid offsets.
//     * This method uses the earlier offset typically corresponding to "summer".
//     * <p>
//     * In the case of a gap, where clocks jump forward, there is no valid offset.
//     * Instead, the local date-time is adjusted to be later by the length of the gap.
//     * For a typical one hour daylight savings change, the local date-time will be
//     * moved one hour later into the offset typically corresponding to "summer".
//     * <p>
//     * To obtain the later offset during an overlap, call
//     * {@link ZonedDateTime#withLaterOffsetAtOverlap()} on the result of this method.
//     * To throw an exception when there is a gap or overlap, use
//     * {@link ZonedDateTime#ofStrict(LocalDateTime, ZoneOffset, ZoneId)}.
//     *
//     * @param zone  the time-zone to use, not null
//     * @return the zoned date-time formed from this date-time, not null
//     */
//    @Override
//    public ZonedDateTime atZone(ZoneId zone) {
//        return ZonedDateTime.of(this, zone);
//    }

    //-----------------------------------------------------------------------
    /**
     * Gets the {@code LocalDate} part of this date-time.
     * <p>
     * This returns a {@code LocalDate} with the same year, month and day
     * as this date-time.
     *
     * @return the date part of this date-time, not null
     */
    //@Override
    public LocalDate toLocalDate() {
        return date;
    }

    /**
     * Gets the {@code LocalTime} part of this date-time.
     * <p>
     * This returns a {@code LocalTime} with the same hour, minute, second and
     * nanosecond as this date-time.
     *
     * @return the time part of this date-time, not null
     */
    //@Override
    public LocalTime toLocalTime() {
        return time;
    }

//    //-----------------------------------------------------------------------
//    /**
//     * Compares this date-time to another date-time.
//     * <p>
//     * The comparison is primarily based on the date-time, from earliest to latest.
//     * It is "consistent with equals", as defined by {@link Comparable}.
//     * <p>
//     * If all the date-times being compared are instances of {@code LocalDateTime},
//     * then the comparison will be entirely based on the date-time.
//     * If some dates being compared are in different chronologies, then the
//     * chronology is also considered, see {@link ChronoLocalDateTime#compareTo}.
//     *
//     * @param other  the other date-time to compare to, not null
//     * @return the comparator value, negative if less, positive if greater
//     */
//    @Override  // override for Javadoc and performance
//    public int compareTo(ChronoLocalDateTime<?> other) {
//        if (other instanceof LocalDateTime) {
//            return compareTo0((LocalDateTime) other);
//        }
//        return super.compareTo(other);
//    }
//
    @Override
    public int compareTo(final LocalDateTime other) {
        int cmp = date.compareTo0(other.toLocalDate());
        if (cmp == 0) {
            cmp = time.compareTo(other.toLocalTime());
        }
        return cmp;
    }

    /**
     * Checks if this date-time is after the specified date-time.
     * <p>
     * This checks to see if this date-time represents a point on the
     * local time-line after the other date-time.
     * <pre>
     *   LocalDate a = LocalDateTime.of(2012, 6, 30, 12, 00);
     *   LocalDate b = LocalDateTime.of(2012, 7, 1, 12, 00);
     *   a.isAfter(b) == false
     *   a.isAfter(a) == false
     *   b.isAfter(a) == true
     * </pre>
     * <p>
     * This method only considers the position of the two date-times on the local time-line.
     * It does not take into account the chronology, or calendar system.
     * This is different from the comparison in {@link #compareTo(ChronoLocalDateTime)},
     * but is the same approach as {@link #DATE_TIME_COMPARATOR}.
     *
     * @param other  the other date-time to compare to, not null
     * @return true if this date-time is after the specified date-time
     */
    //@Override  // override for Javadoc and performance
//    public boolean isAfter(ChronoLocalDateTime<?> other) {
//        if (other instanceof LocalDateTime) {
//            return compareTo0((LocalDateTime) other) > 0;
//        }
//        return super.isAfter(other);
//    }
    public boolean isAfter(final LocalDateTime other) {
        return compareTo((LocalDateTime) other) > 0;
    }

    /**
     * Checks if this date-time is before the specified date-time.
     * <p>
     * This checks to see if this date-time represents a point on the
     * local time-line before the other date-time.
     * <pre>
     *   LocalDate a = LocalDateTime.of(2012, 6, 30, 12, 00);
     *   LocalDate b = LocalDateTime.of(2012, 7, 1, 12, 00);
     *   a.isBefore(b) == true
     *   a.isBefore(a) == false
     *   b.isBefore(a) == false
     * </pre>
     * <p>
     * This method only considers the position of the two date-times on the local time-line.
     * It does not take into account the chronology, or calendar system.
     * This is different from the comparison in {@link #compareTo(ChronoLocalDateTime)},
     * but is the same approach as {@link #DATE_TIME_COMPARATOR}.
     *
     * @param other  the other date-time to compare to, not null
     * @return true if this date-time is before the specified date-time
     */
    //@Override  // override for Javadoc and performance
//    public boolean isBefore(ChronoLocalDateTime<?> other) {
//        if (other instanceof LocalDateTime) {
//            return compareTo0((LocalDateTime) other) < 0;
//        }
//        return super.isBefore(other);
//    }
    public boolean isBefore(final LocalDateTime other) {
        return compareTo(other) < 0;
    }

//    /**
//     * Checks if this date-time is equal to the specified date-time.
//     * <p>
//     * This checks to see if this date-time represents the same point on the
//     * local time-line as the other date-time.
//     * <pre>
//     *   LocalDate a = LocalDateTime.of(2012, 6, 30, 12, 00);
//     *   LocalDate b = LocalDateTime.of(2012, 7, 1, 12, 00);
//     *   a.isEqual(b) == false
//     *   a.isEqual(a) == true
//     *   b.isEqual(a) == false
//     * </pre>
//     * <p>
//     * This method only considers the position of the two date-times on the local time-line.
//     * It does not take into account the chronology, or calendar system.
//     * This is different from the comparison in {@link #compareTo(ChronoLocalDateTime)},
//     * but is the same approach as {@link #DATE_TIME_COMPARATOR}.
//     *
//     * @param other  the other date-time to compare to, not null
//     * @return true if this date-time is equal to the specified date-time
//     */
//    @Override  // override for Javadoc and performance
//    public boolean isEqual(ChronoLocalDateTime<?> other) {
//        if (other instanceof LocalDateTime) {
//            return compareTo0((LocalDateTime) other) == 0;
//        }
//        return super.isEqual(other);
//    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this date-time is equal to another date-time.
     * <p>
     * Compares this {@code LocalDateTime} with another ensuring that the date-time is the same.
     * Only objects of type {@code LocalDateTime} are compared, other types return false.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other date-time
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof LocalDateTime) {
            LocalDateTime other = (LocalDateTime) obj;
            return date.equals(other.date) && time.equals(other.time);
        }
        return false;
    }

    /**
     * A hash code for this date-time.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return date.hashCode() ^ time.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this date-time as a {@code String}, such as {@code 2007-12-23T10:15:30}.
     * <p>
     * The output will be one of the following ISO-8601 formats:
     * <p><ul>
     * <li>{@code yyyy-MM-dd'T'HH:mm}</li>
     * <li>{@code yyyy-MM-dd'T'HH:mm:ss}</li>
     * <li>{@code yyyy-MM-dd'T'HH:mm:ss.SSS}</li>
     * <li>{@code yyyy-MM-dd'T'HH:mm:ss.SSSSSS}</li>
     * <li>{@code yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS}</li>
     * </ul><p>
     * The format used will be the shortest that outputs the full value of
     * the time where the omitted parts are implied to be zero.
     *
     * @return a string representation of this date-time, not null
     */
    @Override
    public String toString() {
        return date.toString() + 'T' + time.toString();
    }

//    /**
//     * Outputs this date-time as a {@code String} using the formatter.
//     * <p>
//     * This date-time will be passed to the formatter
//     * {@link DateTimeFormatter#format(TemporalAccessor) print method}.
//     *
//     * @param formatter  the formatter to use, not null
//     * @return the formatted date-time string, not null
//     * @throws DateTimeException if an error occurs during printing
//     */
//    @Override  // override for Javadoc
//    public String format(DateTimeFormatter formatter) {
//        return super.format(formatter);
//    }
//
//    //-----------------------------------------------------------------------
//    @GwtIncompatible
//    private Object writeReplace() {
//        return new Ser(Ser.LOCAL_DATE_TIME_TYPE, this);
//    }
//
//    /**
//     * Defend against malicious streams.
//     * @return never
//     * @throws InvalidObjectException always
//     */
//    @GwtIncompatible
//    private Object readResolve() throws ObjectStreamException {
//        throw new InvalidObjectException("Deserialization via serialization delegate");
//    }

    @GwtIncompatible
    void writeExternal(DataOutput out) throws IOException {
        date.writeExternal(out);
        time.writeExternal(out);
    }

    static LocalDateTime readExternal(DataInput in) throws IOException {
        LocalDate date = LocalDate.readExternal(in);
        LocalTime time = LocalTime.readExternal(in);
        return LocalDateTime.of(date, time);
    }

    //-----------------------------------------------------------------------inlined from ChronoLocalDateTime
    /**
     * Converts this date-time to an {@code Instant}.
     * <p>
     * This combines this local date-time and the specified offset to form
     * an {@code Instant}.
     *
     * @param offset  the offset to use for the conversion, not null
     * @return an {@code Instant} representing the same instant, not null
     */
    public Instant toInstant(ZoneOffset offset) {
        return Instant.ofEpochSecond(toEpochSecond(offset), toLocalTime().getNano());
    }

    /**
     * Converts this date-time to the number of seconds from the epoch
     * of 1970-01-01T00:00:00Z.
     * <p>
     * This combines this local date-time and the specified offset to calculate the
     * epoch-second value, which is the number of elapsed seconds from 1970-01-01T00:00:00Z.
     * Instants on the time-line after the epoch are positive, earlier are negative.
     *
     * @param offset  the offset to use for the conversion, not null
     * @return the number of seconds from the epoch of 1970-01-01T00:00:00Z
     */
    public long toEpochSecond(ZoneOffset offset) {
        Jdk8Methods.requireNonNull(offset, "offset");
        long epochDay = toLocalDate().toEpochDay();
        long secs = epochDay * 86400 + toLocalTime().toSecondOfDay();
        secs -= offset.getTotalSeconds();
        return secs;
    }

}

