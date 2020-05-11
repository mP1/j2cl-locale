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
import walkingkooka.compare.ComparableTesting;
import walkingkooka.j2cl.java.io.string.StringDataInputDataOutput;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

import java.io.DataOutput;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class TimeZoneDisplayTest implements ClassTesting2<TimeZoneDisplay>,
        ComparableTesting,
        HashCodeEqualsDefinedTesting2<TimeZoneDisplay>,
        ToStringTesting<TimeZoneDisplay> {

    private final static String SHORT = "short-1";
    private final static String SHORT_DAY = "short-day-2";
    private final static String LONG = "long-3";
    private final static String LONG_DAY = "long-day-4";

    @Test
    public void testWith() {
        final TimeZoneDisplay display = this.createObject();
        assertEquals(SHORT, display.shortDisplayName, "shortDisplayName");
        assertEquals(SHORT_DAY, display.shortDisplayNameDaylight, "shortDisplayNameDaylight");
        assertEquals(LONG, display.longDisplayName, "longDisplayName");
        assertEquals(LONG, display.longDisplayName, "longDisplayNameDaylight");
    }

    @Test
    public void testRead() throws IOException {
        final StringBuilder text = new StringBuilder();
        final DataOutput data = StringDataInputDataOutput.output(text::append);
        data.writeUTF(SHORT);
        data.writeUTF(SHORT_DAY);
        data.writeUTF(LONG);
        data.writeUTF(LONG_DAY);

        assertEquals(this.createObject(), TimeZoneDisplay.read(StringDataInputDataOutput.input(text.toString())));
    }

    // ComparableTesting....................................................................................

    @Test
    public void testCompareLess() {
        this.compareToAndCheckLess(this.createObject(),
                TimeZoneDisplay.with("zzzz", SHORT_DAY, LONG, LONG_DAY));
    }

    // HashCodeEqualsDefinedTesting2....................................................................................

    @Test
    public void testDifferentShort() {
        this.checkNotEquals(TimeZoneDisplay.with("different", SHORT_DAY, LONG, LONG_DAY));
    }

    @Test
    public void testDifferentShortDaylightSaving() {
        this.checkNotEquals(TimeZoneDisplay.with(SHORT, "different", LONG, LONG_DAY));
    }

    @Test
    public void testDifferentLong() {
        this.checkNotEquals(TimeZoneDisplay.with(SHORT, SHORT_DAY, "different", LONG_DAY));
    }

    @Test
    public void testDifferentLongDaylightSaving() {
        this.checkNotEquals(TimeZoneDisplay.with(SHORT, SHORT_DAY, LONG, "different"));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createObject(),
                "\"short-1\" \"short-day-2\" \"long-3\" \"long-day-4\"");
    }

    // ClassTesting....................................................................................................

    @Override
    public Class<TimeZoneDisplay> type() {
        return TimeZoneDisplay.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // HashCodeEqualsDefinedTesting2....................................................................................

    @Override
    public TimeZoneDisplay createObject() {
        return TimeZoneDisplay.with(SHORT, SHORT_DAY, LONG, LONG_DAY);
    }
}
