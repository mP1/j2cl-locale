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
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.j2cl.java.io.string.StringDataInputDataOutput;
import walkingkooka.predicate.PredicateTesting;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class WalkingkookaLanguageTagTest implements ClassTesting<WalkingkookaLanguageTag>,
    HashCodeEqualsDefinedTesting2<WalkingkookaLanguageTag>,
    PredicateTesting,
    ToStringTesting<WalkingkookaLanguageTag> {

    // filter...........................................................................................................

    @Test
    public void testFilterNullFails() {
        assertThrows(NullPointerException.class, () -> WalkingkookaLanguageTag.filter(null));
    }

    @Test
    public void testFilterEmptyFails() {
        assertThrows(IllegalArgumentException.class, () -> WalkingkookaLanguageTag.filter(""));
    }

    @Test
    public void testFilterWildcard() {
        this.testTrue("*", "A");
    }

    @Test
    public void testFilterWildcard2() {
        this.testTrue("*", "a");
    }

    @Test
    public void testFilterWildcard3() {
        this.testTrue("A,B,C,*", "XYZ");
    }

    @Test
    public void testFilterExact() {
        this.testTrue("A,B", "A");
    }

    @Test
    public void testFilterExact2() {
        this.testTrue("A,B", "B");
    }

    @Test
    public void testFilterExact3() {
        this.testFalse("A,B", "AB");
    }

    @Test
    public void testFilterEndsWildcard() {
        this.testFalse("A*,B", "Z");
    }

    @Test
    public void testFilterEndsWildcard2() {
        this.testTrue("A*,B", "A");
    }

    @Test
    public void testFilterEndsWildcard3() {
        this.testTrue("A*,B*", "B");
    }

    @Test
    public void testFilterEndsWildcard4() {
        this.testTrue("A*,B*", "B2");
    }

    private void testTrue(final String filter, final String value) {
        this.testTrue(WalkingkookaLanguageTag.filter(filter), value);
    }

    private void testFalse(final String filter, final String value) {
        this.testFalse(WalkingkookaLanguageTag.filter(filter), value);
    }

    @Test
    public void testFilterToStringWildcard() {
        this.toStringAndCheck(WalkingkookaLanguageTag.filter("*"), "*");
    }

    @Test
    public void testFilterToStringIncludesWildcard() {
        this.toStringAndCheck(WalkingkookaLanguageTag.filter("A,*"), "*");
    }

    @Test
    public void testFilterToStringMany() {
        this.toStringAndCheck(WalkingkookaLanguageTag.filter("A,B*,C*"), "A,B*,C*");
    }

    // .................................................................................................................

    private final static WalkingkookaLanguageTag ROOT = WalkingkookaLanguageTag.with("", "", "", "");

    private final static String TAG = "en-AU-NSW";
    private final static String LANGUAGE = "en";
    private final static String COUNTRY = "AU";
    private final static String VARIANT = "NSW";

    // decode...........................................................................................................

    @Test
    public void testDecode0Components() throws IOException {
        final StringBuilder text = new StringBuilder();
        final DataOutput out = StringDataInputDataOutput.output(text::append);
        out.writeInt(1);
        out.writeUTF("");

        this.decodeAndCheck(text,
            ROOT);
    }

    @Test
    public void testDecode1Components() throws IOException {
        final StringBuilder text = new StringBuilder();
        final DataOutput out = StringDataInputDataOutput.output(text::append);
        out.writeInt(1);
        out.writeUTF("EN");

        this.decodeAndCheck(text,
            WalkingkookaLanguageTag.with("EN", "EN", "", ""));
    }

    @Test
    public void testDecode2Components() throws IOException {
        final StringBuilder text = new StringBuilder();
        final DataOutput out = StringDataInputDataOutput.output(text::append);
        out.writeInt(1);
        out.writeUTF("af-NA,af,NA");

        this.decodeAndCheck(text,
            WalkingkookaLanguageTag.with("af-NA", "af", "NA", ""));
    }

    @Test
    public void testDecode3Components() throws IOException {
        final StringBuilder text = new StringBuilder();
        final DataOutput out = StringDataInputDataOutput.output(text::append);
        out.writeInt(1);
        out.writeUTF("af-NA,af,NA");

        this.decodeAndCheck(text,
            WalkingkookaLanguageTag.with("af-NA", "af", "NA", ""));
    }

    @Test
    public void testDecode4Components() throws IOException {
        final StringBuilder text = new StringBuilder();
        final DataOutput out = StringDataInputDataOutput.output(text::append);
        out.writeInt(1);
        out.writeUTF("tag,lang,country");

        this.decodeAndCheck(text,
            WalkingkookaLanguageTag.with("tag", "lang", "country", ""));
    }

    @Test
    public void testDecode5Components() throws IOException {
        final StringBuilder text = new StringBuilder();
        final DataOutput out = StringDataInputDataOutput.output(text::append);
        out.writeInt(1);
        out.writeUTF("az-Cyrl-AZ,az,AZ,,Cyrl");

        this.decodeAndCheck(text,
            WalkingkookaLanguageTag.with("az-Cyrl-AZ", "az", "AZ", "", "Cyrl"));
    }

    @Test
    public void testSeveral1() throws IOException {
        final StringBuilder text = new StringBuilder();
        final DataOutput out = StringDataInputDataOutput.output(text::append);
        out.writeInt(3);
        out.writeUTF("");
        out.writeUTF("EN");
        out.writeUTF("az-Cyrl-AZ,az,AZ,,Cyrl");

        this.decodeAndCheck(text,
            ROOT,
            WalkingkookaLanguageTag.with("EN", "EN", "", ""),
            WalkingkookaLanguageTag.with("az-Cyrl-AZ", "az", "AZ", "", "Cyrl"));
    }

    @Test
    public void testSeveral2() throws IOException {
        final StringBuilder text = new StringBuilder();
        final DataOutput out = StringDataInputDataOutput.output(text::append);
        out.writeInt(3);
        out.writeUTF("EN");
        out.writeUTF("CA-FR,CA,FR");
        out.writeUTF("DE");

        this.decodeAndCheck(text,
            WalkingkookaLanguageTag.with("EN", "EN", "", ""),
            WalkingkookaLanguageTag.with("CA-FR", "ca", "FR", ""),
            WalkingkookaLanguageTag.with("DE", "DE", "", ""));
    }

    @Test
    public void testSeveral3() throws IOException {
        final StringBuilder text = new StringBuilder();
        final DataOutput out = StringDataInputDataOutput.output(text::append);
        out.writeInt(2);
        out.writeUTF("EN");
        out.writeUTF("az-Cyrl-AZ,az,AZ,,Cyrl");

        this.decodeAndCheck(text,
            WalkingkookaLanguageTag.with("EN", "EN", "", ""),
            WalkingkookaLanguageTag.with("az-Cyrl-AZ", "az", "AZ", "", "Cyrl"));
    }

    private void decodeAndCheck(final CharSequence encoded,
                                final WalkingkookaLanguageTag... expected) throws IOException {
        this.decodeAndCheck0(encoded, Lists.of(expected));
    }

    private void decodeAndCheck0(final CharSequence encoded,
                                 final List<WalkingkookaLanguageTag> expected) throws IOException {

        assertEquals(toString(expected),
            toString(WalkingkookaLanguageTag.decode(StringDataInputDataOutput.input(encoded.toString()))),
            "decode " + encoded);
    }

    private static String toString(final List<WalkingkookaLanguageTag> tags) {
        return tags.stream()
            .map(WalkingkookaLanguageTag::toString)
            .collect(Collectors.joining("\n"));
    }

    // all..............................................................................................................

    @Test
    public void testAllWithNullFilterFails() {
        assertThrows(NullPointerException.class, () -> WalkingkookaLanguageTag.all(null));
    }

    @Test
    public void testAllWithEmptyFilterFails() {
        assertThrows(IllegalArgumentException.class, () -> WalkingkookaLanguageTag.all(""));
    }

    @Test
    public void testAllInvalidFilterFails() {
        assertThrows(IllegalArgumentException.class, () -> WalkingkookaLanguageTag.all("EN,*FR"));
    }

    @Test
    public void testAllInvalidFilterFails2() {
        assertThrows(IllegalArgumentException.class, () -> WalkingkookaLanguageTag.all("EN,FR*-*"));
    }

    @Test
    public void testAllWildcard() {
        this.allAndCheck("*", WalkingkookaLanguageTag.all());
    }

    @Test
    public void testAllEn() {
        this.allAndCheck("EN", "en");
    }

    @Test
    public void testAllEnWildcard2() {
        this.allAndCheck("en", "en");
    }

    @Test
    public void testAllMultpleExact() {
        this.allAndCheck("EN,FR", "en", "fr");
    }

    @Test
    public void testAllEnWildcard() {
        this.allAndCheck("EN*",
            WalkingkookaLanguageTag.all()
                .stream()
                .filter(t -> CaseSensitivity.INSENSITIVE.equals(t, "EN") || CaseSensitivity.INSENSITIVE.startsWith(t, "EN"))
                .collect(Collectors.toCollection(() -> SortedSets.tree(String.CASE_INSENSITIVE_ORDER))));
    }

    @Test
    public void testAllEnWildcardFr() {
        this.allAndCheck("EN*,FR",
            WalkingkookaLanguageTag.all()
                .stream()
                .filter(t -> CaseSensitivity.INSENSITIVE.startsWith(t, "EN") || t.equalsIgnoreCase("FR"))
                .collect(Collectors.toCollection(() -> SortedSets.tree(String.CASE_INSENSITIVE_ORDER))));
    }

    private void allAndCheck(final String filter,
                             final String... expected) {
        this.allAndCheck(filter, Sets.of(expected));
    }

    private void allAndCheck(final String filter,
                             final Set<String> expected) {
        assertEquals(expected, WalkingkookaLanguageTag.all(filter), () -> " filter " + CharSequences.quoteAndEscape(filter));
    }

    @Test
    public void testAllWithoutAlternatives() {
        // filter out the "duplicate" locales with two forms before comparing.
        assertEquals(Arrays.stream(Locale.getAvailableLocales())
                .sorted(new Comparator<Locale>() {
                    public int compare(final Locale l, final Locale r) {
                        return l.toLanguageTag().compareTo(r.toLanguageTag());
                    }
                })
                .map(Locale::toLanguageTag)
                .filter(t -> false == WalkingkookaLanguageTag.isUnsupported(t))
                .sorted()
                .distinct()
                .collect(Collectors.joining("\n")),
            WalkingkookaLanguageTag.all()
                .stream()
                .filter(t -> {
                    final int index = t.indexOf("-");
                    final String language = -1 != index ? t.substring(0, index) : t;
                    return WalkingkookaLanguageTag.oldToNewLanguage(language).equals(language);
                })
                .collect(Collectors.joining("\n")));
    }

    @Test
    public void testHe() {
        final Set<String> all = WalkingkookaLanguageTag.all();
        assertTrue(all.contains("he"), () -> "" + all);
    }

    @Test
    public void testIw() {
        final Set<String> all = WalkingkookaLanguageTag.all();
        assertTrue(all.contains("iw"), () -> "" + all);
    }

    @Test
    public void testEn() {
        final Set<String> all = WalkingkookaLanguageTag.all();
        assertTrue(all.contains("en"), () -> "" + all);
    }

    @Test
    public void testLocalesFiltered() {
        final String filter = "EN*";
        assertEquals(WalkingkookaLanguageTag.all(filter),
            WalkingkookaLanguageTag.locales(filter)
                .stream()
                .map(Locale::toLanguageTag)
                .collect(Collectors.toCollection(Sets::ordered)));
    }

    // parse............................................................................................................

    @Test
    public void testParseEmpty() {
        this.parseAndCheck("", "", "", "", "");
    }

    @Test
    public void testParseLanguageUpperCase() {
        this.parseAndCheck("EN", "en", "", "", "");
    }

    @Test
    public void testParseLanguageMixedCase() {
        this.parseAndCheck("En", "en", "", "", "");
    }

    @Test
    public void testParseLanguageLowerCase() {
        this.parseAndCheck("en", "en", "", "", "");
    }

    @Test
    public void testParseLanguageCountryUpperCase() {
        this.parseAndCheck("en-GB", "en", "GB", "", "");
    }

    @Test
    public void testParseLanguageCountryLowerCase() {
        this.parseAndCheck("en-gb", "en", "GB", "", "");
    }

    @Test
    public void testParseLanguageScript() {
        this.parseAndCheck("bs-Latn", "bs", "", "", "Latn");
    }

    @Test
    public void testParseLanguageScriptCountry() {
        this.parseAndCheck("bs-Latn-BA", "bs", "BA", "", "Latn");
    }

    @Test
    public void testParseLanguageCountryVariant() {
        this.parseAndCheck("ca-ES-VALENCIA", "ca", "ES", "VALENCIA", "");
    }

    @Test
    public void testParseLanguageCountryVariant2() {
        // ignore the unicode variant complexity simply down to just the trailing JP
        this.parseAndCheck("ja-JP-u-ca-japanese-x-lvariant-JP", "ja", "JP", "JP", "");
    }

    @Test
    public void testParseHeLanguage() {
        this.parseAndCheck("he", "he", "", "", "");
    }

    @Test
    public void testParseIwLanguage() {
        this.parseAndCheck("iw", "iw", "", "", "");
    }

    @Test
    public void testParseYiLanguage() {
        this.parseAndCheck("yi", "yi", "", "", "");
    }

    @Test
    public void testParseJiLanguage() {
        this.parseAndCheck("ji", "ji", "", "", "");
    }

    @Test
    public void testParseIdLanguage() {
        this.parseAndCheck("id", "id", "", "", "");
    }

    @Test
    public void testParseInLanguage() {
        this.parseAndCheck("in", "in", "", "", "");
    }

    @Test
    public void testParseUndLanguage() {
        this.parseAndCheck("und", "und", "", "", "");
    }

    @Test
    public void testParseNnNoLanguage() {
        this.parseAndCheck("nn-NO", "nn", "NO", "", "");
    }

    private void parseAndCheck(final String parse,
                               final String language,
                               final String country,
                               final String variant,
                               final String script) {
        final WalkingkookaLanguageTag wlt = WalkingkookaLanguageTag.parse(parse);

        assertEquals(language, wlt.language(), "language");
        assertEquals(script, wlt.script(), "script");
        assertEquals(country, wlt.country(), "country");
        assertEquals(variant, wlt.variant(), "variant");
    }

    // lang: no      coun: NO s:  var: NY
    @Test
    public void testWithNoNoNy() {
        final WalkingkookaLanguageTag wlt = WalkingkookaLanguageTag.with(null, "no", "NO", "NY");

        assertEquals("no", wlt.language(), "language");
        assertEquals("", wlt.script(), "script");
        assertEquals("NO", wlt.country(), "country");
        assertEquals("NY", wlt.variant(), "variant");
    }

    @Test
    public void testWithNoNoNyWithTag() {
        final WalkingkookaLanguageTag wlt = WalkingkookaLanguageTag.with("nn-NO", "no", "NO", "NY");

        assertEquals("no", wlt.language(), "language");
        assertEquals("", wlt.script(), "script");
        assertEquals("NO", wlt.country(), "country");
        assertEquals("NY", wlt.variant(), "variant");
    }

    // lookup............................................................................................................

    @Test
    public void testLookupExact() {
        this.lookupAndCheck(Maps.of("en", 1), "en", 1);
    }

    @Test
    public void testLookupMatchAfterDroppingCountry() {
        this.lookupAndCheck(Maps.of("en", 1), "en-AU", 1);
    }

    @Test
    public void testLookupMatchAfterDroppingCountry2() {
        this.lookupAndCheck(Maps.of("en", 1, "fr", 2), "en-AU", 1);
    }

    @Test
    public void testLookupMatchAfterDroppingVariant() {
        this.lookupAndCheck(Maps.of("ca-ES", 1), "ca-ES-VALENCIA", 1);
    }

    @Test
    public void testLookupMatchAfterDroppingVariant2() {
        this.lookupAndCheck(Maps.of("ca-ES", 1, "fr", 2), "ca-ES-VALENCIA", 1);
    }

    @Test
    public void testLookupMatchAfterDroppingVariant3() {
        this.lookupAndCheck(Maps.of("ca-ES", 1, "ca", 2), "ca-ES-VALENCIA", 1);
    }

    @Test
    public void testLookupMatchAfterDroppingVariantAndCountry() {
        this.lookupAndCheck(Maps.of("ca", 1), "ca-ES-VALENCIA", 1);
    }

    @Test
    public void testLookupMatchAfterDroppingVariantAndCountry2() {
        this.lookupAndCheck(Maps.of("ca", 1, "fr", 2), "ca-ES-VALENCIA", 1);
    }

    @Test
    public void testLookupMatchAfterDroppingVariantAndCountryFail() {
        this.lookupAndCheck(Maps.of("en", 1), "ca-ES-VALENCIA", null);
    }

    @Test
    public void testLookupFails() {
        this.lookupAndCheck(Maps.of("en", 1), "fr", null);
    }


    private void lookupAndCheck(final Map<String, Integer> source,
                                final String tag,
                                final Integer expected) {
        assertEquals(Optional.ofNullable(expected),
            WalkingkookaLanguageTag.parse(tag).tryLookup(source::get),
            () -> "tryLookup " + tag + " with " + source);
    }

    // Object...........................................................................................................

    @Test
    public void testDifferentTag() {
        this.checkNotEquals(WalkingkookaLanguageTag.with("EN", "EN", "", ""));
    }

    @Test
    public void testDifferentLanguage() {
        this.checkNotEquals(WalkingkookaLanguageTag.with("fr-" + COUNTRY + "-" + VARIANT, "fr", COUNTRY, VARIANT));
    }

    @Test
    public void testDifferentCountry() {
        this.checkNotEquals(WalkingkookaLanguageTag.with(LANGUAGE + "-NZ-" + VARIANT, LANGUAGE, "NZ", VARIANT));
    }

    @Test
    public void testDifferentVariant() {
        this.checkNotEquals(WalkingkookaLanguageTag.with(LANGUAGE + "-" + COUNTRY + "-qld", LANGUAGE, COUNTRY, "qld"));
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final String tag = "en-AU";
        this.toStringAndCheck(WalkingkookaLanguageTag.parse(tag), tag.replace('-', '_'));
    }

    @Test
    public void testToStringNoNoNy() {
        final String tag = "no-NO-NY";
        this.toStringAndCheck(WalkingkookaLanguageTag.parse(tag), tag.replace('-', '_'));
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<WalkingkookaLanguageTag> type() {
        return WalkingkookaLanguageTag.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // HashCodeEqualsDefinedTesting2....................................................................................

    @Override
    public WalkingkookaLanguageTag createObject() {
        return WalkingkookaLanguageTag.with(TAG, LANGUAGE, COUNTRY, VARIANT);
    }
}
