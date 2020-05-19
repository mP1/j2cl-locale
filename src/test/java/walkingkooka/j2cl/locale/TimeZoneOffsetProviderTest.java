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
import walkingkooka.collect.list.Lists;
import walkingkooka.j2cl.java.io.string.StringDataInputDataOutput;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.CharSequences;

import java.io.DataOutput;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class TimeZoneOffsetProviderTest implements ClassTesting2<TimeZoneOffsetProvider>,
        HashCodeEqualsDefinedTesting2<TimeZoneOffsetProvider>,
        ToStringTesting<TimeZoneOffsetProvider> {

    private final static int DEFAULT_OFFSET = (int) Duration.ofHours(10).toMillis();

    private final static long START = Date.UTC(2020 - TimeZoneOffsetProvider.YEAR_BIAS, 4, 16, 12, 58, 59);
    private final static long END = Date.UTC(2021 - TimeZoneOffsetProvider.YEAR_BIAS, 4, 16, 12, 58, 59);
    private final static int OFFSET = (int) Duration.ofHours(10).toMillis();

    @Test
    public void testHourToMillisConstant() {
        assertEquals(TimeZoneOffsetProvider.HOUR_TO_MILLIS, Duration.ofHours(1).toMillis(), "millis");
    }

    @Test
    public void testMinuteToMillisConstant() {
        assertEquals(TimeZoneOffsetProvider.MINUTE_TO_MILLIS, Duration.ofMinutes(1).toMillis(), "millis");
    }

    @Test
    public void testWith() {
        final TimeZoneOffsetProvider provider = this.createObject();
        assertEquals(Lists.of(this.offsets()), provider.offsets, "offsets");
    }

    @Test
    public void testWriteReadRoundtrip() throws IOException {
        final StringBuilder data = new StringBuilder();
        final DataOutput dataOutput = StringDataInputDataOutput.output(data::append);

        final TimeZoneOffsetProvider provider = this.createObject();
        provider.write(dataOutput);

        assertEquals(provider, TimeZoneOffsetProvider.read(DEFAULT_OFFSET, StringDataInputDataOutput.input(data.toString())));
    }

    @Test
    public void testDifferentDefaultOffset() {
        this.checkNotEquals(TimeZoneOffsetProvider.with(0,
                Lists.of(TimeZoneOffset.with(START,
                        END,
                        OFFSET))));
    }

    @Test
    public void testDifferentStartTime() {
        this.checkNotEquals(TimeZoneOffsetProvider.with(DEFAULT_OFFSET,
                Lists.of(TimeZoneOffset.with(START + 1 * TimeZoneOffsetProvider.HOUR_TO_MILLIS,
                        END,
                        OFFSET))));
    }

    @Test
    public void testDifferentOffset() {
        this.checkNotEquals(TimeZoneOffsetProvider.with(DEFAULT_OFFSET,
                Lists.of(TimeZoneOffset.with(START,
                        END,
                        1 * TimeZoneOffsetProvider.HOUR_TO_MILLIS + OFFSET))));
    }

    // getOffset........................................................................................................

    private final static String SYDNEY = "Australia/Sydney";

    @Test
    public void testGetOffsetSydney202001311920() {
        this.getOffsetAndCheck(SYDNEY,
                2020,
                Calendar.JANUARY,
                31,
                19,
                20);
    }

    @Test
    public void testGetOffsetSydney202004151920() {
        this.getOffsetAndCheck(SYDNEY,
                2020,
                Calendar.MAY,
                15,
                19,
                20);
    }

    @Test
    public void testGetOffsetSydney19700101() {
        this.getOffsetAndCheck(SYDNEY,
                1970,
                Calendar.JANUARY,
                1,
                0,
                0);
    }

    @Test
    public void testGetOffsetSydney19690620() {
        this.getOffsetAndCheck(SYDNEY,
                1969,
                Calendar.JULY,
                20,
                0,
                0);
    }

    @Test
    public void testGetOffsetSydney197001011259() {
        this.getOffsetAndCheck(SYDNEY,
                1970,
                Calendar.JANUARY,
                1,
                0,
                59);
    }

    @Test
    public void testGetOffset202004151920() {
        this.getOffsetAndCheck(2020,
                Calendar.MAY,
                15,
                19,
                20);
    }

    @Test
    public void testGetOffset19700101() {
        this.getOffsetAndCheck(1970,
                Calendar.JANUARY,
                1,
                0,
                0);
    }

    @Test
    public void testGetOffset19690620() {
        this.getOffsetAndCheck(1969,
                Calendar.JULY,
                20,
                0,
                0);
    }

    @Test
    public void testGetOffset1800To2000Jan1() {
        for (int year = 1800; year < 2000; year++) {
            this.getOffsetAndCheck(year,
                    Calendar.JANUARY,
                    1,
                    0,
                    0);
        }
    }

    @Test
    public void testGetOffset1800To2000July1() {
        for (int year = 1800; year < 2000; year++) {
            this.getOffsetAndCheck(year,
                    Calendar.JULY,
                    1,
                    0,
                    0);
        }
    }

    private void getOffsetAndCheck(final int year,
                                   final int month,
                                   final int day,
                                   final int hours,
                                   final int minutes) {
        for (final String zoneId : TimeZone.getAvailableIDs()) {
            getOffsetAndCheck(zoneId,
                    year,
                    month,
                    day,
                    hours,
                    minutes);
        }
    }

    private void getOffsetAndCheck(final String timeZoneId,
                                   final int year,
                                   final int month,
                                   final int day,
                                   final int hours,
                                   final int minutes) {
        final int era = GregorianCalendar.AD;
        final TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);

        final int dayOfWeek = LocalDate.of(year, 1 + month, day).getDayOfWeek().getValue();
        final int millis = hours * TimeZoneOffsetProvider.HOUR_TO_MILLIS + minutes * TimeZoneOffsetProvider.MINUTE_TO_MILLIS;

        final long jreRawOffset = timeZone.getRawOffset();
        final TimeZoneOffsetProvider provider = TimeZoneOffsetProvider.collect(timeZone);

        final Date date = new Date(millis);
        {
            final long jreOffset = timeZone.getOffset(era,
                    year - TimeZoneOffsetProvider.YEAR_BIAS,
                    month,
                    day,
                    dayOfWeek,
                    millis);
            final long emulatedOffset = provider.getOffset(era,
                    year - TimeZoneOffsetProvider.YEAR_BIAS,
                    month,
                    day,
                    dayOfWeek,
                    millis);

            final Supplier<String> message = () -> CharSequences.quoteAndEscape(timeZoneId) +
                    " jreRawOffset: " + duration(jreRawOffset).toString().substring(2) +
                    " getOffset AD, " + //
                    date;

            assertEquals(duration(jreOffset),
                    duration(emulatedOffset),
                    message);
        }
    }

    private static Duration duration(final long millis) {
        return Duration.ofMillis(millis);
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createObject(), "[16 May 2020 12:58:59-16 May 2021 12:58:59 offset=36000000]");
    }

    // HashCodeEquality..................................................................................................

    @Override
    public TimeZoneOffsetProvider createObject() {
        return TimeZoneOffsetProvider.with(DEFAULT_OFFSET, Lists.of(this.offsets()));
    }

    private TimeZoneOffset offsets() {
        return TimeZoneOffset.with(START,
                END,
                OFFSET);
    }

    // ClassTesting......................................................................................................

    @Override
    public Class<TimeZoneOffsetProvider> type() {
        return TimeZoneOffsetProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
