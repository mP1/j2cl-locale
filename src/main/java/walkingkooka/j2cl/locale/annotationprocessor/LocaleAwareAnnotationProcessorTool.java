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

import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.reflect.PublicStaticHelper;

import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public final class LocaleAwareAnnotationProcessorTool implements PublicStaticHelper {

    /**
     * A {@link Comparator} that uses the {@link Locale#toLanguageTag()} to compare.
     */
    public static final Comparator<Locale> LOCALE_COMPARATOR = LocaleAwareAnnotationProcessorTool::compareLocaleLanguageTag;

    private static int compareLocaleLanguageTag(final Locale left, final Locale right) {
        return left.toLanguageTag().compareTo(right.toLanguageTag());
    }

    public static <T extends Comparable<T>> Map<T, Set<Locale>> buildMultiLocaleMap(final Function<Locale, T> extractor,
                                                                                    final Set<Locale> locales) {
        return buildMultiLocaleMap(Comparator.naturalOrder(), extractor, locales);
    }

    /**
     * Builds a {@link Map} with values that share common {@link Locale locales}, think grouping but to a {@link Set}
     */
    public static <T> Map<T, Set<Locale>> buildMultiLocaleMap(final Comparator<T> comparator,
                                                              final Function<Locale, T> extractor,
                                                              final Set<Locale> locales) {
        final Map<T, Set<Locale>> map = Maps.sorted(comparator);

        for (final Locale locale : locales) {
            final T value = extractor.apply(locale);
            Set<Locale> sharedLocales = map.get(value);
            if (null == sharedLocales) {
                sharedLocales = Sets.sorted(LOCALE_COMPARATOR);
                map.put(value, sharedLocales);
            }
            sharedLocales.add(locale);
        }

        return map;
    }

    /**
     * Stop creation
     */
    private LocaleAwareAnnotationProcessorTool() {
        throw new UnsupportedOperationException();
    }
}
