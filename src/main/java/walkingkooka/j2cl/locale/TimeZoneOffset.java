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

import walkingkooka.ToStringBuilder;
import walkingkooka.ToStringBuilderOption;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

/**
 * Represents the details of a time limited offset.
 */
public final class TimeZoneOffset {

    /**
     * Reads a single {@link TimeZoneOffset}
     */
    public static TimeZoneOffset read(final DataInput data) throws IOException {
        return with(data.readLong(),
                data.readLong(),
                data.readInt());
    }

    /**
     * Factory that creates a {@link TimeZoneOffset}.
     */
    public static TimeZoneOffset with(final long startTime,
                                      final long endTime,
                                      final int offset) {
        return new TimeZoneOffset(startTime,
                endTime,
                offset);
    }

    /**
     * Private ctor
     */
    private TimeZoneOffset(final long startTime,
                           final long endTime,
                           final int offset) {
        super();
        this.startTime = startTime;
        this.endTime = endTime;
        this.offset = offset;
    }

    /**
     * Writes this to the given {@link DataOutput}.
     */
    public void write(final DataOutput data) throws IOException {
        data.writeLong(this.startTime);
        data.writeLong(this.endTime);
        data.writeInt(this.offset);
    }

    /**
     * The start time in ticks.
     */
    public final long startTime;

    /**
     * The end time in ticks.
     */
    public final long endTime;

    public final int offset() {
        return this.offset;
    }

    /**
     * The offset for this transition, typically one hour.
     */
    private final int offset;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(this.startTime, this.endTime, this.offset);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other || other instanceof TimeZoneOffset && this.equals0((TimeZoneOffset) other);
    }

    private boolean equals0(final TimeZoneOffset other) {
        return this.startTime == other.startTime &&
                this.endTime == other.endTime &&
                this.offset == other.offset;
    }

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .disable(ToStringBuilderOption.QUOTE)
                .enable(ToStringBuilderOption.SKIP_IF_DEFAULT_VALUE)
                .value(date(this.startTime))
                .separator("-")
                .value(date(this.endTime))
                .separator(" ")
                .label("offset")
                .value(this.offset)
                .build();
    }

    private static String date(final long ticks) {
        return new Date(ticks).toLocaleString();
    }
}
