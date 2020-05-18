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

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.j2cl.java.io.string.StringDataInputDataOutput;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

import java.io.DataOutput;
import java.io.IOException;
import java.time.Duration;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class TimeZoneOffsetTest implements ClassTesting2<TimeZoneOffset>,
        HashCodeEqualsDefinedTesting2<TimeZoneOffset>,
        ToStringTesting<TimeZoneOffset> {

    @SuppressWarnings("deprecated")
    private final static long START = Date.UTC(2020 - 1900, 4, 16, 12, 58, 59);
    @SuppressWarnings("deprecated")
    private final static long END = Date.UTC(2021 - 1900, 4, 16, 12, 58, 59);
    private final static int OFFSET = (int) Duration.ofHours(10).toMillis();

    @Test
    public void testWith() {
        final TimeZoneOffset transition = this.createObject();
        assertEquals(START, transition.startTime, "startTime");
        assertEquals(END, transition.endTime, "endTime");
        assertEquals(OFFSET, transition.offset(), "offset");
    }

    @Test
    public void testWriteReadRoundtrip() throws IOException {
        final StringBuilder data = new StringBuilder();
        final DataOutput dataOutput = StringDataInputDataOutput.output(data::append);

        final TimeZoneOffset transition = this.createObject();
        transition.write(dataOutput);

        assertEquals(transition, TimeZoneOffset.read(StringDataInputDataOutput.input(data.toString())));
    }

    @Test
    @SuppressWarnings("deprecated")
    public void testDifferentStartTime() {
        this.checkNotEquals(TimeZoneOffset.with(Date.UTC(1999 - 1900, 11, 31, 12, 58, 59),
                END,
                OFFSET));
    }

    @Test
    @SuppressWarnings("deprecated")
    public void testDifferentEndTime() {
        this.checkNotEquals(TimeZoneOffset.with(START,
                Date.UTC(2050 - 1900, 11, 31, 12, 58, 59),
                OFFSET));
    }

    @Test
    public void testDifferentOffset() {
        this.checkNotEquals(TimeZoneOffset.with(START,
                END,
                OFFSET / 2));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createObject(), "16 May 2020, 10:58:59 pm-16 May 2021, 10:58:59 pm offset=36000000");
    }

    // HashCodeEqualityTesting..........................................................................................

    @Override
    public TimeZoneOffset createObject() {
        return TimeZoneOffset.with(START,
                END,
                OFFSET);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<TimeZoneOffset> type() {
        return TimeZoneOffset.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
