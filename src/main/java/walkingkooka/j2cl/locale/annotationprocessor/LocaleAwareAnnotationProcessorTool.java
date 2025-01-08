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
import walkingkooka.collect.set.SortedSets;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.text.CharSequences;

import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class LocaleAwareAnnotationProcessorTool implements PublicStaticHelper {

    /**
     * A {@link Comparator} that uses the {@link Locale#toLanguageTag()} to compare.
     */
    public static final Comparator<Locale> LOCALE_COMPARATOR = LocaleAwareAnnotationProcessorTool::compareLocaleLanguageTag;

    private static int compareLocaleLanguageTag(final Locale left, final Locale right) {
        return left.toLanguageTag().compareTo(right.toLanguageTag());
    }

    /**
     * Converts the language tags to {@link Locale locales}.
     */
    public static Set<Locale> toLocales(final Set<String> languageTags) {
        return languageTags.stream()
            .map(Locale::forLanguageTag)
            .collect(Collectors.toCollection(() -> SortedSets.tree(LOCALE_COMPARATOR)));
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
                sharedLocales = SortedSets.tree(LOCALE_COMPARATOR);
                map.put(value, sharedLocales);
            }
            sharedLocales.add(locale);
        }

        return map;
    }

    /**
     * Finds the value with the most {@link Locale locales}. If multiple values have the same count, the first is returned.
     */
    public static <T> T findMostPopularLocaleKey(final Map<T, Set<Locale>> valueToLocales) {
        // find the most popular display
        int mostLocaleCounts = -1;
        T most = null;

        for (final Entry<T, Set<Locale>> valueAndLocales : valueToLocales.entrySet()) {
            final T value = valueAndLocales.getKey();
            final Set<Locale> locales = valueAndLocales.getValue();
            final int count = locales.size();

            if (count > mostLocaleCounts) {
                mostLocaleCounts = count;
                most = value;
            }
        }

        return most;
    }

    /**
     * Produces a message such as:
     * <pre>
     * 56 locale(s) selected by "EN-*"
     * </pre>
     */
    public static String extractSummary(final int count,
                                        final String label,
                                        final String filter) {
        return count + " " + label + "(s) selected by " + CharSequences.quoteAndEscape(filter);
    }

    /**
     * Stop creation
     */
    private LocaleAwareAnnotationProcessorTool() {
        throw new UnsupportedOperationException();
    }
}
