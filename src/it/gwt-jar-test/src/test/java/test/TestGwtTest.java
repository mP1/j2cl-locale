/*
 * Copyright 2023 Miroslav Pokorny (github.com/mP1)
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
package test;

import com.google.gwt.junit.client.GWTTestCase;

import walkingkooka.j2cl.java.io.string.StringDataInputDataOutput;
import walkingkooka.j2cl.locale.Calendar;
import walkingkooka.j2cl.locale.GregorianCalendar;
import walkingkooka.j2cl.locale.TimeZoneCalendar;
import walkingkooka.j2cl.locale.TimeZoneDisplay;
import walkingkooka.j2cl.locale.WalkingkookaLanguageTag;

import java.io.DataInput;
import java.io.DataOutput;

public class TestGwtTest extends GWTTestCase {
    @Override
    public String getModuleName() {
        return "test.Test";
    }

    public void testAssertEquals() {
        assertEquals(
            1,
            1
        );
    }

    public void testTimeZoneCalendar() throws Exception {
        final StringBuilder data = new StringBuilder();
        final DataOutput dataOutput = StringDataInputDataOutput.output(data::append);

        final TimeZoneCalendar calendar = TimeZoneCalendar.with(1, 2);
        calendar.write(dataOutput);

        final DataInput dataInput = StringDataInputDataOutput.input(data.toString());
        assertEquals(TimeZoneCalendar.read(dataInput), calendar);
    }

    public void testTimeZoneDisplay() throws Exception {
        final StringBuilder data = new StringBuilder();

        final DataOutput dataOutput = StringDataInputDataOutput.output(data::append);
        dataOutput.writeUTF("short1");
        dataOutput.writeUTF("short2day");
        dataOutput.writeUTF("long3");
        dataOutput.writeUTF("long4day");

        final TimeZoneDisplay display = TimeZoneDisplay.read(StringDataInputDataOutput.input(data.toString()));

        assertEquals("shortDisplayName", "short1", display.shortDisplayName);
        assertEquals("shortDisplayNameDaylight", "short2day", display.shortDisplayNameDaylight);
        assertEquals("longDisplayName", "long3", display.longDisplayName);
        assertEquals("longDisplayNameDaylight", "long4day", display.longDisplayNameDaylight);
    }

    public void testWalkingkookaLanguageTag() throws Exception {
        final WalkingkookaLanguageTag wlt = WalkingkookaLanguageTag.parse("EN-AU");

        assertEquals("language", "en", wlt.language());
        assertEquals("script", "", wlt.script());
        assertEquals("country", "AU", wlt.country());
        assertEquals("variant", "", wlt.variant());
    }
}
