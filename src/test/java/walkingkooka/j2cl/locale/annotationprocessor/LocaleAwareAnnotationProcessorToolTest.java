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

package walkingkooka.j2cl.locale.annotationprocessor;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.PublicStaticHelperTesting;

import java.lang.reflect.Method;
import java.text.DateFormatSymbols;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class LocaleAwareAnnotationProcessorToolTest implements PublicStaticHelperTesting<LocaleAwareAnnotationProcessorTool> {

    @Test
    public void testToLocales() {
        assertEquals(Sets.of(Locale.forLanguageTag("EN-AU")),
            LocaleAwareAnnotationProcessorTool.toLocales(Sets.of("EN-AU")));
    }

    @Test
    public void testBuildMultiLocaleMapWithComparator() {
        final Locale EN_AU = Locale.forLanguageTag("EN-AU");
        final Locale FR_FR = Locale.forLanguageTag("FR-FR");
        final Locale ES_ES = Locale.forLanguageTag("ES-ES");

        final Map<String, Set<Locale>> sundayToLocales = Maps.sorted();
        sundayToLocales.put("Sunday", Sets.of(EN_AU));
        sundayToLocales.put("dimanche", Sets.of(FR_FR));
        sundayToLocales.put("domingo", Sets.of(ES_ES));

        assertEquals(sundayToLocales,
            LocaleAwareAnnotationProcessorTool.buildMultiLocaleMap(String.CASE_INSENSITIVE_ORDER,
                LocaleAwareAnnotationProcessorToolTest::firstDayOfWeek,
                Sets.of(EN_AU, FR_FR, ES_ES)));
    }

    @Test
    public void testBuildMultiLocaleMap() {
        final Locale EN_AU = Locale.forLanguageTag("EN-AU");
        final Locale FR_FR = Locale.forLanguageTag("FR-FR");
        final Locale ES_ES = Locale.forLanguageTag("ES-ES");

        final Map<String, Set<Locale>> sundayToLocales = Maps.sorted();
        sundayToLocales.put("Sunday", Sets.of(EN_AU));
        sundayToLocales.put("dimanche", Sets.of(FR_FR));
        sundayToLocales.put("domingo", Sets.of(ES_ES));

        assertEquals(sundayToLocales,
            LocaleAwareAnnotationProcessorTool.buildMultiLocaleMap(LocaleAwareAnnotationProcessorToolTest::firstDayOfWeek,
                Sets.of(EN_AU, FR_FR, ES_ES)));
    }

    @Test
    public void testBuildMultiLocaleMap2() {
        final Locale EN_AU = Locale.forLanguageTag("EN-AU");
        final Locale EN_NZ = Locale.forLanguageTag("EN-NZ");
        final Locale FR_FR = Locale.forLanguageTag("FR-FR");
        final Locale ES_ES = Locale.forLanguageTag("ES-ES");
        final Locale ES_MX = Locale.forLanguageTag("ES-MX");
        final Locale ES_AR = Locale.forLanguageTag("ES-AR");

        final Map<String, Set<Locale>> sundayToLocales = Maps.sorted();
        sundayToLocales.put("Sunday", Sets.of(EN_AU, EN_NZ));
        sundayToLocales.put("dimanche", Sets.of(FR_FR));
        sundayToLocales.put("domingo", Sets.of(ES_ES, ES_MX, ES_AR));

        assertEquals(sundayToLocales,
            LocaleAwareAnnotationProcessorTool.buildMultiLocaleMap(LocaleAwareAnnotationProcessorToolTest::firstDayOfWeek,
                Sets.of(EN_AU, EN_NZ, FR_FR, ES_ES, ES_MX, ES_AR)));
    }

    private static String firstDayOfWeek(final Locale locale) {
        return DateFormatSymbols.getInstance(locale).getWeekdays()[1];
    }

    @Test
    public void testFindMostPopularLocaleKey() {
        final Map<CharSequence, Set<Locale>> valueToLocales = Maps.ordered();
        valueToLocales.put("MOST", Sets.of(Locale.ENGLISH, Locale.FRANCE));
        valueToLocales.put(new StringBuilder("LEAST"), Sets.of(Locale.CANADA));

        this.findMostPopularLocaleKey(valueToLocales, "MOST");
    }

    @Test
    public void testFindMostPopularLocaleKey2() {
        final Map<CharSequence, Set<Locale>> valueToLocales = Maps.ordered();
        valueToLocales.put(new StringBuilder("LEAST"), Sets.of(Locale.CANADA));
        valueToLocales.put("MOST", Sets.of(Locale.ENGLISH, Locale.FRANCE));

        this.findMostPopularLocaleKey(valueToLocales, "MOST");
    }

    @Test
    public void testFindMostPopularLocaleKey3() {
        final Map<CharSequence, Set<Locale>> valueToLocales = Maps.ordered();
        valueToLocales.put("MOST", Sets.of(Locale.ENGLISH, Locale.FRANCE, Locale.CANADA));
        valueToLocales.put(new StringBuilder("1"), Sets.of(Locale.CHINA));
        valueToLocales.put(new StringBuilder("2"), Sets.of(Locale.GERMAN));

        this.findMostPopularLocaleKey(valueToLocales, "MOST");
    }

    @Test
    public void testFindMostPopularLocaleKeyFirst() {
        final Map<CharSequence, Set<Locale>> valueToLocales = Maps.ordered();
        valueToLocales.put("MOST", Sets.of(Locale.ENGLISH, Locale.FRANCE, Locale.CANADA));
        valueToLocales.put("MOST2", Sets.of(Locale.ENGLISH, Locale.FRANCE, Locale.CANADA));

        this.findMostPopularLocaleKey(valueToLocales, "MOST");
    }

    private void findMostPopularLocaleKey(final Map<CharSequence, Set<Locale>> valueToLocales,
                                          final CharSequence expected) {
        assertEquals(expected.toString(),
            LocaleAwareAnnotationProcessorTool.findMostPopularLocaleKey(valueToLocales).toString());
    }

    @Override
    public Class<LocaleAwareAnnotationProcessorTool> type() {
        return LocaleAwareAnnotationProcessorTool.class;
    }

    @Override
    public boolean canHavePublicTypes(final Method method) {
        return false;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
