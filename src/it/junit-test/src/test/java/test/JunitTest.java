/*
 * Copyright © 2020 Miroslav Pokorny
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
 */
package test;

import com.google.j2cl.junit.apt.J2clTestInput;
import org.junit.Assert;
import org.junit.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.j2cl.java.io.string.StringDataInputDataOutput;
import walkingkooka.j2cl.locale.Calendar;
import walkingkooka.j2cl.locale.GregorianCalendar;
import walkingkooka.j2cl.locale.TimeZoneCalendar;
import walkingkooka.j2cl.locale.TimeZoneDisplay;
import walkingkooka.j2cl.locale.TimeZoneOffset;
import walkingkooka.j2cl.locale.TimeZoneOffsetProvider;
import walkingkooka.j2cl.locale.WalkingkookaLanguageTag;
import walkingkooka.j2cl.locale.org.threeten.bp.LocalDate;
import walkingkooka.j2cl.locale.org.threeten.bp.LocalDateTime;
import walkingkooka.j2cl.locale.org.threeten.bp.LocalTime;
import walkingkooka.j2cl.locale.org.threeten.bp.ZoneOffset;
import walkingkooka.j2cl.locale.org.threeten.bp.zone.StandardZoneRules;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.Date;

@J2clTestInput(JunitTest.class)
public class JunitTest {

    @Test
    public void testCalendar() throws Exception {
        Assert.assertEquals("Calendar.JANUARY", 0, Calendar.JANUARY);
    }

    @Test
    public void testGregorianCalendar() throws Exception {
        Assert.assertEquals("GregorianCalendar.BC", 0, GregorianCalendar.BC);
    }

    @Test
    public void testTimeZoneCalendar() throws Exception {
        final StringBuilder data = new StringBuilder();
        final DataOutput dataOutput = StringDataInputDataOutput.output(data::append);

        final TimeZoneCalendar calendar = TimeZoneCalendar.with(1 , 2);
        calendar.write(dataOutput);

        final DataInput dataInput = StringDataInputDataOutput.input(data.toString());
        Assert.assertEquals(TimeZoneCalendar.read(dataInput), calendar);
    }

    @Test
    public void testTimeZoneDisplay() throws Exception {
        final StringBuilder data = new StringBuilder();

        final DataOutput dataOutput = StringDataInputDataOutput.output(data::append);
        dataOutput.writeUTF("short1");
        dataOutput.writeUTF("short2day");
        dataOutput.writeUTF("long3");
        dataOutput.writeUTF("long4day");

        final TimeZoneDisplay display = TimeZoneDisplay.read(StringDataInputDataOutput.input(data.toString()));

        Assert.assertEquals("shortDisplayName", "short1", display.shortDisplayName);
        Assert.assertEquals("shortDisplayNameDaylight", "short2day", display.shortDisplayNameDaylight);
        Assert.assertEquals("longDisplayName", "long3", display.longDisplayName);
        Assert.assertEquals("longDisplayNameDaylight", "long4day", display.longDisplayNameDaylight);
    }

    @Test
    public void testTimeZoneOffset() throws Exception {
        Assert.assertNotNull(TimeZoneOffset.with(0, 1, 2));
    }

    @Test
    public void testTimeZoneOffsetProvider() throws Exception {
        Assert.assertNotNull(TimeZoneOffsetProvider.with(0, Lists.of(TimeZoneOffset.with(0, 1, 2))));
    }

    @Test
    public void testWalkingkookaLanguageTag() throws Exception {
        final WalkingkookaLanguageTag wlt = WalkingkookaLanguageTag.parse("EN-AU");

        Assert.assertEquals("language", "en", wlt.language());
        Assert.assertEquals("script", "", wlt.script());
        Assert.assertEquals("country", "AU", wlt.country());
        Assert.assertEquals("variant", "", wlt.variant());
    }

    @Test
    public void testZoneRulesGetOffset() throws Exception {
        final LocalDateTime localDateTime = LocalDateTime.of(LocalDate.of(2000, 1, 1), LocalTime.of(0, 59));

        Assert.assertEquals("getOffset",
                ZoneOffset.ofTotalSeconds(11 * 60 * 60),
                this.standardZoneRules().getOffset(localDateTime));
    }

    @Test
    public void testZoneRulesInDaylightTimeSydney2000Jan1() throws Exception {
        this.inDaylightTimeAndCheck(new Date(Date.UTC(2000 - 1900, Calendar.JANUARY, 1, 0, 59, 0)), true);
    }

    @Test
    public void testZoneRulesInDaylightTimeSydney2000July() throws Exception {
        this.inDaylightTimeAndCheck(new Date(Date.UTC(2000 - 1900, Calendar.JULY, 1, 0, 59, 0)), false);
    }

    private void inDaylightTimeAndCheck(final Date date, final boolean expected) throws Exception {
        Assert.assertEquals("inDaylightTime " + date,
                expected,
                this.standardZoneRules().inDaylightTime(date));
    }

    private StandardZoneRules standardZoneRules() throws Exception {
        final String data = "1,-1,-2364113092,127,36292,40,86,-1,-2364113092,-1,-1672567140,49,87,-68,62,-104,-64,62,-71,92,62,-3,-96,63,65,-36,63,-120,-64,63,-54,92,78,-114,96,78,-69,0,79,22,-32,79,70,32,79,-97,96,79,-50,-96,80,39,-32,80,87,32,80,-80,96,80,-30,64,81,59,-128,81,106,-64,81,-60,0,81,-13,64,82,76,-128,82,123,-64,82,-43,0,83,4,64,83,93,-128,83,-116,-64,83,-26,0,84,34,96,84,113,32,84,-96,96,84,-7,-96,85,40,-32,85,-126,32,85,-79,96,86,10,-96,86,63,32,86,-112,-128,86,-57,-96,87,27,-96,87,82,-64,87,-90,-64,87,-37,64,88,47,64,88,94,-128,88,-73,-64,88,-25,0,89,64,64,89,111,-128,89,-56,-64,89,-6,-96,90,83,-32,90,-125,32,90,-36,96,91,11,-96,91,100,-32,91,-98,-96,91,-19,96,92,39,32,92,117,-32,92,-81,-96,92,-2,96,93,56,32,93,-119,-128,93,-64,-96,93,-6,96,94,73,32,94,-102,-128,94,-44,64,95,35,0,95,92,-64,95,-85,-128,95,-27,64,96,54,-96,96,109,-64,96,-65,32,96,-8,-32,97,71,-96,97,126,-64,97,-48,32,98,12,-128,98,80,-64,98,-107,0,98,-39,64,127,36292,40,44,40,44,40,44,40,44,40,44,40,44,40,44,40,44,40,44,40,44,40,44,40,44,40,44,40,44,40,44,40,44,40,44,40,44,40,44,40,44,40,44,40,44,40,44,40,44,40,44,40,44,40,44,40,44,40,44,40,44,40,44,40,44,40,44,40,44,40,44,40,44,40,44,40,44,40,44,40,44,40,44,40,44,40,44,2,1215867528,-1468487038";
        return StandardZoneRules.readExternal(StringDataInputDataOutput.input(data));
    }
}
