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

import java.io.DataInput;
import java.io.DataOutput;

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
}
