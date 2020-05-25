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

package walkingkooka.j2cl.locale.org.threeten.bp.zone;

import org.junit.jupiter.api.Test;
import walkingkooka.j2cl.locale.org.threeten.bp.Instant;
import walkingkooka.text.CharSequences;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class ZoneRulesTest {

    private final static String SYDNEY = "Australia/Sydney";
    private final static String LONDON = "Europe/London";
    private final static String NYC = "America/New_York";
    static final LocalDateTime DATE_2000_JAN_1 = LocalDateTime.of(2000, Month.JANUARY, 1, 0, 59);
    static final LocalDateTime DATE_2000_JULY_1 = LocalDateTime.of(2000, Month.JULY, 1, 0, 59);
    static final LocalDateTime DATE_2020_JAN_1 = LocalDateTime.of(2020, Month.JANUARY, 1, 0, 59);

    // getOffset........................................................................................................

    @Test
    public void testGetOffsetSydney200001011259() throws Exception {
        this.getOffsetAndCheck(SYDNEY,
                DATE_2000_JAN_1);
    }

    @Test
    public void testGetOffsetSydney200007011259() throws Exception {
        this.getOffsetAndCheck(SYDNEY,
                DATE_2000_JULY_1);
    }

    @Test
    public void testGetOffsetSydney202001011259() throws Exception {
        this.getOffsetAndCheck(SYDNEY,
                DATE_2020_JAN_1);
    }

    @Test
    public void testGetOffsetLondon200001011259() throws Exception {
        this.getOffsetAndCheck(LONDON,
                DATE_2000_JAN_1);
    }

    @Test
    public void testGetOffsetLondon200007011259() throws Exception {
        this.getOffsetAndCheck(LONDON,
                DATE_2000_JULY_1);
    }

    @Test
    public void testGetOffsetLondon202001011259() throws Exception {
        this.getOffsetAndCheck(LONDON,
                DATE_2020_JAN_1);
    }

    @Test
    public void testGetOffsetNewYork200001011259() throws Exception {
        this.getOffsetAndCheck(NYC,
                DATE_2000_JAN_1);
    }

    @Test
    public void testGetOffsetNewYork200007011259() throws Exception {
        this.getOffsetAndCheck(NYC,
                DATE_2000_JULY_1);
    }

    @Test
    public void testGetOffsetNewYork202001011259() throws Exception {
        this.getOffsetAndCheck(NYC,
                DATE_2020_JAN_1);
    }

    @Test
    public void testGetOffsetAllZoneIds200001010959() throws Exception {
        this.getOffsetAndCheck(DATE_2000_JAN_1);
    }

    @Test
    public void testGetOffsetAllZoneIds200007010959() throws Exception {
        this.getOffsetAndCheck(DATE_2000_JULY_1);
    }

    @Test
    public void testGetOffset1800To2000_Jan1() throws Exception {
        for (int year = 1800; year < 2000; year++) {
            this.getOffsetAndCheck(DATE_2000_JAN_1.withYear(year));
        }
    }

    @Test
    public void testGetOffset1800To2000_July1() throws Exception {
        for (int year = 1800; year < 2000; year++) {
            this.getOffsetAndCheck(DATE_2000_JULY_1.withYear(year));
        }
    }

    private void getOffsetAndCheck(final LocalDateTime dateTime) throws Exception {
        int i = 0;
        for (final String zoneId : TimeZone.getAvailableIDs()) {
            if (zoneId.contains("/")) {
                this.getOffsetAndCheck(java.time.ZoneId.of(zoneId),
                        dateTime);
                i++;
            }
        }

        assertTrue(i > 100, "not enough zoneIds " + i);
    }

    private void getOffsetAndCheck(final String zoneId,
                                   final LocalDateTime dateTime) throws Exception {
        this.getOffsetAndCheck(java.time.ZoneId.of(zoneId),
                dateTime);
    }

    private void getOffsetAndCheck(final java.time.ZoneId zoneId,
                                   final LocalDateTime dateTime) throws Exception {
        final java.time.zone.ZoneRules jreRules = zoneId.getRules();
        final java.time.ZoneOffset jreZoneOffset = jreRules.getOffset(dateTime);

        final ZoneRules emulatedRules = ZoneRules.of(zoneId);

        assertEquals(ZoneRules.transformZoneOffset(jreZoneOffset),
                emulatedRules.getOffset(ZoneRules.transformLocalDateTime(dateTime)),
                () -> zoneId + " getOffset " + dateTime);
    }

    // isDaylightSavings................................................................................................

    @Test
    public void testIsDaylightSavingsSydney200001011259() throws Exception {
        this.isDaylightSavingsAndCheck(SYDNEY,
                DATE_2000_JAN_1);
    }

    @Test
    public void testIsDaylightSavingsSydney200007011259() throws Exception {
        this.isDaylightSavingsAndCheck(SYDNEY,
                DATE_2000_JULY_1);
    }

    @Test
    public void testIsDaylightSavingsSydney202001011259() throws Exception {
        this.isDaylightSavingsAndCheck(SYDNEY,
                DATE_2020_JAN_1);
    }

    @Test
    public void testIsDaylightSavingsLondon200001011259() throws Exception {
        this.isDaylightSavingsAndCheck(LONDON,
                DATE_2000_JAN_1);
    }

    @Test
    public void testIsDaylightSavingsLondon200007011259() throws Exception {
        this.isDaylightSavingsAndCheck(LONDON,
                DATE_2000_JULY_1);
    }

    @Test
    public void testIsDaylightSavingsLondon202001011259() throws Exception {
        this.isDaylightSavingsAndCheck(LONDON,
                DATE_2020_JAN_1);
    }

    @Test
    public void testIsDaylightSavingsNewYork200001011259() throws Exception {
        this.isDaylightSavingsAndCheck(NYC,
                DATE_2000_JAN_1);
    }

    @Test
    public void testIsDaylightSavingsNewYork200007011259() throws Exception {
        this.isDaylightSavingsAndCheck(NYC,
                DATE_2000_JULY_1);
    }

    @Test
    public void testIsDaylightSavingsNewYork202001011259() throws Exception {
        this.isDaylightSavingsAndCheck(NYC,
                DATE_2020_JAN_1);
    }

    @Test
    public void testIsDaylightSavingsAllZoneIds200001010959() throws Exception {
        this.isDaylightSavingsAndCheck(DATE_2000_JAN_1);
    }

    @Test
    public void testIsDaylightSavingsAllZoneIds200007010959() throws Exception {
        this.isDaylightSavingsAndCheck(DATE_2000_JULY_1);
    }

    @Test
    public void testIsDaylightSavings1800To2000_Jan1() throws Exception {
        for (int year = 1800; year < 2000; year++) {
            this.isDaylightSavingsAndCheck(DATE_2000_JAN_1.withYear(year));
        }
    }

    @Test
    public void testIsDaylightSavings1800To2000_July() throws Exception {
        for (int year = 1800; year < 2000; year++) {
            this.isDaylightSavingsAndCheck(DATE_2000_JULY_1.withYear(year));
        }
    }

    private void isDaylightSavingsAndCheck(final LocalDateTime dateTime) throws Exception {
        int i = 0;
        for (final String zoneId : TimeZone.getAvailableIDs()) {
            if (zoneId.contains("/")) {
                this.isDaylightSavingsAndCheck(java.time.ZoneId.of(zoneId),
                        dateTime);
                i++;
            }
        }

        assertTrue(i > 100, "not enough zoneIds " + i);
    }

    private void isDaylightSavingsAndCheck(final String zoneId,
                                           final LocalDateTime dateTime) throws Exception {
        this.isDaylightSavingsAndCheck(java.time.ZoneId.of(zoneId),
                dateTime);
    }

    private void isDaylightSavingsAndCheck(final java.time.ZoneId zoneId,
                                           final LocalDateTime dateTime) throws Exception {

        final java.time.zone.ZoneRules jreRules = zoneId.getRules();
        final java.time.Instant jreInstant = dateTime.atZone(zoneId).toInstant();

        final ZoneRules emulatedRules = ZoneRules.of(zoneId);
        final Instant emulatedInstant = ZoneRules.transformInstant(jreInstant);

        final boolean inDaylightSavings = jreRules.isDaylightSavings(jreInstant);
        assertEquals(inDaylightSavings,
                emulatedRules.isDaylightSavings(emulatedInstant),
                () -> zoneId + " isDaylightSavings " + dateTime + "( jreInstant: " + jreInstant + ", emulatedInstant: " + emulatedInstant + ")");

        final Date date = java.util.Date
                .from(dateTime.atZone(zoneId)
                        .toInstant());
        assertEquals(inDaylightSavings,
                TimeZone.getTimeZone(zoneId).inDaylightTime(date),
                () -> zoneId + " isDaylightSavings " + dateTime + " vs TimeZone.getTimeZone.inDaylightTime " + date);
    }

    // inDaylightTime...................................................................................................

    @Test
    public void testInDaylightTimeSydney200001011259() throws Exception {
        this.inDaylightTimeAndCheck(SYDNEY,
                DATE_2000_JAN_1);
    }

    @Test
    public void testInDaylightTimeSydney200007011259() throws Exception {
        this.inDaylightTimeAndCheck(SYDNEY,
                DATE_2000_JULY_1);
    }

    @Test
    public void testInDaylightTimeSydney202001011259() throws Exception {
        this.inDaylightTimeAndCheck(SYDNEY,
                DATE_2020_JAN_1);
    }

    @Test
    public void testInDaylightTimeLondon200001011259() throws Exception {
        this.inDaylightTimeAndCheck(LONDON,
                DATE_2000_JAN_1);
    }

    @Test
    public void testInDaylightTimeLondon200007011259() throws Exception {
        this.inDaylightTimeAndCheck(LONDON,
                DATE_2000_JULY_1);
    }

    @Test
    public void testInDaylightTimeLondon202001011259() throws Exception {
        this.inDaylightTimeAndCheck(LONDON,
                DATE_2020_JAN_1);
    }

    @Test
    public void testInDaylightTimeNewYork200001011259() throws Exception {
        this.inDaylightTimeAndCheck(NYC,
                DATE_2000_JAN_1);
    }

    @Test
    public void testInDaylightTimeNewYork200007011259() throws Exception {
        this.inDaylightTimeAndCheck(NYC,
                DATE_2000_JULY_1);
    }

    @Test
    public void testInDaylightTimeNewYork202001011259() throws Exception {
        this.inDaylightTimeAndCheck(NYC,
                DATE_2020_JAN_1);
    }

    @Test
    public void testInDaylightTimeAllZoneIds200001010959() throws Exception {
        this.inDaylightTimeAndCheck(DATE_2000_JAN_1);
    }

    @Test
    public void testInDaylightTimeAllZoneIds200007010959() throws Exception {
        this.inDaylightTimeAndCheck(DATE_2000_JULY_1);
    }

    @Test
    public void testInDaylightTime1800To2000_Jan1() throws Exception {
        for (int year = 1800; year < 2000; year++) {
            this.inDaylightTimeAndCheck(DATE_2000_JAN_1.withYear(year));
        }
    }

    @Test
    public void testInDaylightTime1800To2000_July() throws Exception {
        for (int year = 1800; year < 2000; year++) {
            this.inDaylightTimeAndCheck(DATE_2000_JULY_1.withYear(year));
        }
    }

    private void inDaylightTimeAndCheck(final LocalDateTime dateTime) throws Exception {
        int i = 0;
        for (final String zoneId : TimeZone.getAvailableIDs()) {
            if (zoneId.contains("/")) {
                this.inDaylightTimeAndCheck(java.time.ZoneId.of(zoneId),
                        dateTime);
                i++;
            }
        }

        assertTrue(i > 100, "not enough zoneIds " + i);
    }

    private void inDaylightTimeAndCheck(final String zoneId,
                                        final LocalDateTime dateTime) throws Exception {
        this.inDaylightTimeAndCheck(java.time.ZoneId.of(zoneId),
                dateTime);
    }

    private void inDaylightTimeAndCheck(final java.time.ZoneId zoneId,
                                        final LocalDateTime dateTime) throws Exception {
        final ZoneRules emulatedRules = ZoneRules.of(zoneId);

        final Date date = java.util.Date
                .from(dateTime.atZone(zoneId)
                        .toInstant());
        assertEquals(TimeZone.getTimeZone(zoneId).inDaylightTime(date),
                emulatedRules.inDaylightTime(date),
                () -> zoneId + " inDaylightTime " + dateTime + " vs TimeZone.getTimeZone.inDaylightTime " + date);
    }

    // getOffsetLong........................................................................................................

    @Test
    public void testGetOffsetLongSydney200001011259() throws Exception {
        this.getOffsetLongAndCheck(SYDNEY,
                DATE_2000_JAN_1);
    }

    @Test
    public void testGetOffsetLongSydney200007011259() throws Exception {
        this.getOffsetLongAndCheck(SYDNEY,
                DATE_2000_JULY_1);
    }

    @Test
    public void testGetOffsetLongSydney202001011259() throws Exception {
        this.getOffsetLongAndCheck(SYDNEY,
                DATE_2020_JAN_1);
    }

    @Test
    public void testGetOffsetLongLondon200001011259() throws Exception {
        this.getOffsetLongAndCheck(LONDON,
                DATE_2000_JAN_1);
    }

    @Test
    public void testGetOffsetLongLondon200007011259() throws Exception {
        this.getOffsetLongAndCheck(LONDON,
                DATE_2000_JULY_1);
    }

    @Test
    public void testGetOffsetLongLondon202001011259() throws Exception {
        this.getOffsetLongAndCheck(LONDON,
                DATE_2020_JAN_1);
    }

    @Test
    public void testGetOffsetLongNewYork200001011259() throws Exception {
        this.getOffsetLongAndCheck(NYC,
                DATE_2000_JAN_1);
    }

    @Test
    public void testGetOffsetLongNewYork200007011259() throws Exception {
        this.getOffsetLongAndCheck(NYC,
                DATE_2000_JULY_1);
    }

    @Test
    public void testGetOffsetLongNewYork202001011259() throws Exception {
        this.getOffsetLongAndCheck(NYC,
                DATE_2020_JAN_1);
    }

    @Test
    public void testGetOffsetLongAllZoneIds200001010959() throws Exception {
        this.getOffsetLongAndCheck(DATE_2000_JAN_1);
    }

    @Test
    public void testGetOffsetLongAllZoneIds200007010959() throws Exception {
        this.getOffsetLongAndCheck(DATE_2000_JULY_1);
    }

    @Test
    public void testGetOffsetLong1950To2000_Jan1() throws Exception {
        for (int year = 1950; year < 2000; year++) {
            this.getOffsetLongAndCheck(DATE_2000_JAN_1.withYear(year));
        }
    }

    @Test
    public void testGetOffsetLong1950To2000_July1() throws Exception {
        for (int year = 1950; year < 2000; year++) {
            this.getOffsetLongAndCheck(DATE_2000_JULY_1.withYear(year));
        }
    }

    private void getOffsetLongAndCheck(final LocalDateTime dateTime) throws Exception {
        int i = 0;
        for (final String zoneId : TimeZone.getAvailableIDs()) {
            if (zoneId.contains("/")) {
                this.getOffsetLongAndCheck(java.time.ZoneId.of(zoneId),
                        dateTime);
                i++;
            }
        }

        assertTrue(i > 100, "not enough zoneIds " + i);
    }

    private void getOffsetLongAndCheck(final String zoneId,
                                           final LocalDateTime dateTime) throws Exception {
        this.getOffsetLongAndCheck(java.time.ZoneId.of(zoneId),
                dateTime);
    }

    private void getOffsetLongAndCheck(final java.time.ZoneId zoneId,
                                           final LocalDateTime dateTime) throws Exception {
        final java.time.zone.ZoneRules jreRules = zoneId.getRules();
        final java.time.ZoneOffset jreZoneOffset = jreRules.getOffset(dateTime);

        final ZoneRules emulatedRules = ZoneRules.of(zoneId);

        //final Date date = new Date(dateTime.toEpochSecond(jreZoneOffset));
        final Date date = Date.from(dateTime.toInstant(jreZoneOffset));

//        assertEquals(jreZoneOffset.getTotalSeconds() * 1000,
//                TimeZone.getTimeZone(zoneId).getOffset(date.getTime()),
//                () -> zoneId + " getOffsetLong " + dateTime + " vs TimeZone.getTimeZone.getOffset " + date);
        assertEquals(TimeZone.getTimeZone(zoneId).getOffset(date.getTime()),
                emulatedRules.getOffset(date.getTime()),
                () -> "TimeZone.getTimeZone.getOffset " + date + " " + zoneId + " getOffsetLong " + dateTime);
    }

    // getOffset5Int.....................................................................................................

    @Test
    public void testYearBiasConstant() {
        final String date = new Date(2000 - ZoneRules.YEAR_BIAS, 1, 1).toString();
        assertTrue(date.contains("2000"), date);
    }

    @Test
    public void testGetOffsetEraInvalidFails() {
        this.getOffsetFails(Integer.MAX_VALUE, 1999, 12, 31, 1, 9999);
    }

    @Test
    public void testGetOffsetEraBC() {
        this.getOffsetFails(walkingkooka.j2cl.locale.GregorianCalendar.BC, 1999, 12, 31, 1, 9999);
    }

    @Test
    public void testGetOffsetEraAD() {
        this.getOffsetFails(walkingkooka.j2cl.locale.GregorianCalendar.AD, 1999, 12, 31, 1, 9999);
    }

    @Test
    public void testGetOffsetMonthInvalid() {
        this.getOffsetFails(walkingkooka.j2cl.locale.GregorianCalendar.AD, 1999, -1, 31, 1, 9999);
    }

    @Test
    public void testGetOffsetMonthInvalid2() {
        this.getOffsetFails(walkingkooka.j2cl.locale.GregorianCalendar.AD, 1999, 13, 31, 1, 9999);
    }

    @Test
    public void testGetOffsetDayInvalid() {
        this.getOffsetFails(walkingkooka.j2cl.locale.GregorianCalendar.AD, 1999, 0, -1, 1, 9999);
    }

    @Test
    public void testGetOffsetDayInvalid2() {
        this.getOffsetFails(walkingkooka.j2cl.locale.GregorianCalendar.AD, 1999, 0, 32, 1, 9999);
    }

    private void getOffsetFails(final int era,
                                final int year,
                                final int month,
                                final int day,
                                final int dayOfWeek,
                                final int time) {
        assertThrows(IllegalArgumentException.class,
                () -> ZoneRules.of(ZoneId.of("Australia/Sydney")).getOffset(era, year, month, day, dayOfWeek, time));
    }

    @Test
    public void testGetOffset5IntSydney202001311920() throws Exception {
        this.getOffset5IntAndCheck(SYDNEY,
                2020,
                Calendar.JANUARY,
                31,
                19,
                20);
    }

    @Test
    public void testGetOffset5IntSydney202004151920() throws Exception {
        this.getOffset5IntAndCheck(SYDNEY,
                2020,
                Calendar.MAY,
                15,
                19,
                20);
    }

    @Test
    public void testGetOffset5IntSydney19700101() throws Exception {
        this.getOffset5IntAndCheck(SYDNEY,
                1970,
                Calendar.JANUARY,
                1,
                0,
                0);
    }

    @Test
    public void testGetOffset5IntSydney19690620() throws Exception {
        this.getOffset5IntAndCheck(SYDNEY,
                1969,
                Calendar.JULY,
                20,
                0,
                0);
    }

    @Test
    public void testGetOffset5IntSydney197001011259() throws Exception {
        this.getOffset5IntAndCheck(SYDNEY,
                1970,
                Calendar.JANUARY,
                1,
                0,
                59);
    }

    @Test
    public void testGetOffset5Int202004151920() throws Exception {
        this.getOffset5IntAndCheck(2020,
                Calendar.MAY,
                15,
                19,
                20);
    }

    @Test
    public void testGetOffset5Int19700101() throws Exception {
        this.getOffset5IntAndCheck(1970,
                Calendar.JANUARY,
                1,
                0,
                0);
    }

    @Test
    public void testGetOffset5Int19690620() throws Exception {
        this.getOffset5IntAndCheck(1969,
                Calendar.JULY,
                20,
                0,
                0);
    }

    private void getOffset5IntAndCheck(final int year,
                                       final int month,
                                       final int day,
                                       final int hours,
                                       final int minutes) throws Exception {
        for (final String zoneId : TimeZone.getAvailableIDs()) {
            if (isSupportedTimeZoneId(zoneId)) {
                getOffset5IntAndCheck(zoneId,
                        year,
                        month,
                        day,
                        hours,
                        minutes);
            }
        }
    }

    private void getOffset5IntAndCheck(final String timeZoneId,
                                       final int year,
                                       final int month,
                                       final int day,
                                       final int hours,
                                       final int minutes) throws Exception {
        final int era = GregorianCalendar.AD;
        final TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);

        final int dayOfWeek = LocalDate.of(year, 1 + month, day).getDayOfWeek().getValue();
        final int millis = LocalTime.of(hours, minutes).toSecondOfDay() * 1000;

        final long jreRawOffset = timeZone.getRawOffset();
        final ZoneRules emulatedRules = ZoneRules.of(ZoneId.of(timeZoneId));

        {
            final long jreOffset = timeZone.getOffset(era,
                    year,
                    month,
                    day,
                    dayOfWeek,
                    millis);
            final long emulatedOffset = emulatedRules.getOffset(era,
                    year,
                    month,
                    day,
                    dayOfWeek,
                    millis);

            final Supplier<String> message = () -> CharSequences.quoteAndEscape(timeZoneId) +
                    ", getOffset5Int: " + year + "/" + month + "/" + day + " " + hours + ":" + minutes +
                    ", jreRawOffset: " + duration(jreRawOffset).toString().substring(2);

            assertEquals(duration(jreOffset),
                    duration(emulatedOffset),
                    message);
        }
    }

    private static Duration duration(final long millis) {
        return Duration.ofMillis(millis);
    }

    // TimeZone.useDaylightTime.........................................................................................

    @Test
    public void testUseDaylightTimeAustraliaSydney() throws Exception {
        this.useDaylightTimeAndCheck(SYDNEY);
    }

    @Test
    public void testUseDaylightTimeAustraliaPerth() throws Exception {
        this.useDaylightTimeAndCheck("Australia/Perth");
    }

    @Test
    public void testUseDaylightTimeEuropeLondon() throws Exception {
        this.useDaylightTimeAndCheck(LONDON);
    }

    @Test
    public void testUseDaylightTimeAmericaNYC() throws Exception {
        this.useDaylightTimeAndCheck(NYC);
    }

    @Test
    public void testUseDaylightTimeAllTimezons() throws Exception {
        for (final String zoneId : TimeZone.getAvailableIDs()) {
            if (isSupportedTimeZoneId(zoneId)) {
                this.useDaylightTimeAndCheck(zoneId);
            }
        }
    }

    private void useDaylightTimeAndCheck(final String zoneId) throws Exception {
        assertEquals(TimeZone.getTimeZone(zoneId).useDaylightTime(),
                ZoneRules.of(ZoneId.of(zoneId)).useDaylightTime(),
                () -> "useDaylightTime for zoneId " + CharSequences.quoteAndEscape(zoneId));
    }

    private static boolean isSupportedTimeZoneId(final String zoneId) {
        return zoneId.contains("/"); // skip three letter non standard timezone ids
    }
}
