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
import walkingkooka.compare.ComparableTesting2;
import walkingkooka.j2cl.java.io.string.StringDataInputDataOutput;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class TimeZoneCalendarTest implements ClassTesting2<TimeZoneCalendar>,
    HashCodeEqualsDefinedTesting2<TimeZoneCalendar>,
    ComparableTesting2<TimeZoneCalendar>,
    ToStringTesting<TimeZoneCalendar> {

    private final static int FIRST = 1;
    private final static int MINIMAL = 5;

    @Test
    public void testWriteAndReadRoundtrip() throws IOException {
        final StringBuilder data = new StringBuilder();
        final DataOutput dataOutput = StringDataInputDataOutput.output(data::append);

        final TimeZoneCalendar calendar = TimeZoneCalendar.with(1, 2);
        calendar.write(dataOutput);

        final DataInput dataInput = StringDataInputDataOutput.input(data.toString());
        assertEquals(TimeZoneCalendar.read(dataInput), calendar);
        assertThrows(EOFException.class, () -> dataInput.readBoolean());
    }

    @Test
    public void testCompareLess() {
        this.compareToAndCheckLess(TimeZoneCalendar.with(FIRST + 1, MINIMAL));
    }

    @Test
    public void testCompareLess2() {
        this.compareToAndCheckLess(TimeZoneCalendar.with(FIRST, MINIMAL + 1));
    }

    @Test
    public void testCompareSorted() {
        final TimeZoneCalendar a = TimeZoneCalendar.with(FIRST, MINIMAL);
        final TimeZoneCalendar b = TimeZoneCalendar.with(FIRST, MINIMAL + 1);
        final TimeZoneCalendar c = TimeZoneCalendar.with(FIRST, MINIMAL + 5);
        final TimeZoneCalendar d = TimeZoneCalendar.with(FIRST + 1, MINIMAL);

        this.compareToArraySortAndCheck(d, c, a, b, a, b, c, d);
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createComparable().toString(),
            "firstDayOfWeek=1 minimalDaysInFirstWeek=5");
    }

    @Override
    public TimeZoneCalendar createComparable() {
        return TimeZoneCalendar.with(FIRST, MINIMAL);
    }

    @Override
    public Class<TimeZoneCalendar> type() {
        return TimeZoneCalendar.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
