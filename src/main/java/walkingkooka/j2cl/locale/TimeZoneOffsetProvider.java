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

package walkingkooka.j2cl.locale;

import javaemul.internal.annotations.GwtIncompatible;
import walkingkooka.collect.list.Lists;
import walkingkooka.text.printer.IndentingPrinter;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.time.zone.ZoneOffsetTransition;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

/**
 * Provides a collection of all the time zone offset transitions but ignoring rules for a single timezone.
 * This provides the implementation for {@link TimeZone#getOffset(int, int, int, int, int, int)} but not
 * {@link TimeZone#getOffset(long)} with the later requiring {@link TimeZone#inDaylightTime(Date)}.
 */
public final class TimeZoneOffsetProvider {

    /**
     * Collects all the time zone data for the given {@link TimeZone}.
     */
    @GwtIncompatible
    public static TimeZoneOffsetProvider collect(final TimeZone timeZone) {
        return with(timeZone.getRawOffset(),
                timeZone.toZoneId()
                        .getRules()
                        .getTransitions()
                        .stream()
                        .map(t -> TimeZoneOffsetProvider.from(t, timeZone))
                        .collect(Collectors.toList()));
    }

    @GwtIncompatible
    private static TimeZoneOffset from(final ZoneOffsetTransition transition,
                                       final TimeZone timeZone) {
        final long start = transition.getDateTimeBefore().toEpochSecond(transition.getOffsetBefore());
        final long end = transition.getDateTimeAfter().toEpochSecond(transition.getOffsetAfter());

        return TimeZoneOffset.with(start,
                end,
                timeZone.getRawOffset());
    }

    public static TimeZoneOffsetProvider with(final int defaultOffset,
                                              final List<TimeZoneOffset> offsets) {
        return new TimeZoneOffsetProvider(defaultOffset,
                offsets);
    }

    private TimeZoneOffsetProvider(final int defaultOffset,
                                   final List<TimeZoneOffset> offsets) {
        super();
        this.defaultOffset = defaultOffset;
        this.offsets = offsets;
    }

    final static int AD = 1; // 1=Calendar.AD

    /**
     * Provides the implementation for {@link TimeZone#getOffset(int, int, int, int, int, int)} without the timezone offset.
     */
    public int getOffset(final int era,
                         final int year,
                         final int month,
                         final int day,
                         final int dayOfWeek,
                         final int millis) {
        if (AD != era) {
            throw new IllegalArgumentException("Invalid era " + era + " only AD supported"); // TODO add BC support.
        }
        if (dayOfWeek < GregorianCalendar.SUNDAY || dayOfWeek > GregorianCalendar.SATURDAY) {
            throw new IllegalArgumentException();
        }

        final int hours = millis / HOUR_TO_MILLIS;
        final int minutes = millis - (hours * HOUR_TO_MILLIS) - millis / MINUTE_TO_MILLIS;

        return this.findOrDefault(new Date(year - YEAR_BIAS,
                month,
                day,
                hours,
                minutes)
                .getTime());
    }

    public final static int YEAR_BIAS = 1900;
    public final static int MINUTE_TO_MILLIS = 60 * 1000;
    public final static int HOUR_TO_MILLIS = 60 * MINUTE_TO_MILLIS;

    /**
     * Almost provides the implementation for {@link TimeZone#getOffset(long)} but ignores daylight saving timezone offset.
     * To implement would require honouring the zone rules that describe future day light transitions which requires more DATA.
     */
    private int findOrDefault(final long ticks) {
        return this.find(ticks)
                .map(TimeZoneOffset::offset)
                .orElse(this.defaultOffset);
    }

    private final int defaultOffset;

    /**
     * For the given time as ticks finds the {@link TimeZoneOffset} holding that time.
     */
    private Optional<TimeZoneOffset> find(final long ticks) {
        TimeZoneOffset result = null;

        int i = 0;

        final List<TimeZoneOffset> offsets = this.offsets;
        final int count = offsets.size();
        if (count > 0) {

            TimeZoneOffset offset = offsets.get(0);


            for (; ; ) {
                i++;

                if (ticks >= offset.startTime) {
                    if (count == i) {
                        result = offset;
                        break;
                    }
                    final TimeZoneOffset next = offsets.get(i);
                    if (ticks <= offset.endTime) {
                        result = offset;
                        break;
                    }
                    offset = next;
                    continue;
                }

                if (count == i) {
                    result = offset;
                    break;
                }
                offset = offsets.get(i);
            }
        }

        return Optional.ofNullable(result);
    }

    /**
     * Reads a {@link TimeZoneOffsetProvider} from the given {@link DataOutput}.
     */
    public static TimeZoneOffsetProvider read(final int defaultOffset,
                                              final DataInput data) throws IOException {
        final int count = data.readInt();
        final List<TimeZoneOffset> offsets = Lists.array();
        for (int i = 0; i < count; i++) {
            offsets.add(TimeZoneOffset.read(data));
        }
        return TimeZoneOffsetProvider.with(defaultOffset, offsets);
    }

    /**
     * Generates all the offsets for the given {@link TimeZone} with comments
     */
    @GwtIncompatible
    public void generate(final DataOutput data,
                         final IndentingPrinter comments) throws IOException {
        this.offsets.forEach(o -> {
            comments.lineStart();
            comments.print(o.toString());
        });

        this.write(data);
    }

    /**
     * Writes a {@link List} of {@link TimeZoneOffset} to the given {@link DataOutput}.
     */
    @GwtIncompatible
    public void write(final DataOutput data) throws IOException {
        final List<TimeZoneOffset> offsets = this.offsets;
        data.writeInt(offsets.size());

        for (final TimeZoneOffset offset : offsets) {
            offset.write(data);
        }
    }

    public final List<TimeZoneOffset> offsets;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(this.defaultOffset, this.offsets);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other || (other instanceof TimeZoneOffsetProvider && this.equals0((TimeZoneOffsetProvider) other));
    }

    private boolean equals0(final TimeZoneOffsetProvider other) {
        return this.defaultOffset == other.defaultOffset &&
                this.offsets.equals(other.offsets);
    }

    @Override
    public String toString() {
        return this.offsets.toString();
    }
}
