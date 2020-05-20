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

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}