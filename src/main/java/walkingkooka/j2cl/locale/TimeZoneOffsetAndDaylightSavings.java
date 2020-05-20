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

import java.util.Date;

/**
 * Defines an interface for {@link java.util.TimeZone} methods that require a tzdb to provide offsets, daylight saving
 * like data.
 */
public interface TimeZoneOffsetAndDaylightSavings {
    /**
     * Returns whether the specified {@code Date} is in the daylight savings time period for
     * this {@code TimeZone}.
     *
     * @param time a {@code Date}.
     * @return {@code true} when the {@code Date} is in the daylight savings time period, {@code false}
     * otherwise.
     */
    boolean inDaylightTime(Date time);
}
