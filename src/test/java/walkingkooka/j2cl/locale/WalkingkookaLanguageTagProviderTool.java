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

import walkingkooka.collect.list.Lists;
import walkingkooka.text.CharSequences;
import walkingkooka.text.Indentation;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.Printers;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This tool prints to sysout, that prints a List holding all {@link WalkingkookaLanguageTag} with their data queried from the JDK classes.
 */
public final class WalkingkookaLanguageTagProviderTool {

    public static void main(final String[] args) throws Exception {
        new WalkingkookaLanguageTagProviderTool(Printers.sysOut().indenting(Indentation.with("  "))).print();
    }

    private WalkingkookaLanguageTagProviderTool(final IndentingPrinter printer) {
        super();
        this.printer = printer;
    }

    private void print() {
        final String encoded = encode(this.printer);

        this.line("public final static java.util.List<" + WalkingkookaLanguageTag.class.getName() + ">");
        this.line("  ALL=" + WalkingkookaLanguageTag.class.getName() + ".decode(");
        this.line("    " + CharSequences.quote(encoded));
        this.line("  );");
    }

    // @VisibleForTesting
    static String encode(final IndentingPrinter comments) {
        return WalkingkookaLanguageTag.all()
                .stream()
                .flatMap(e -> encode0(e, comments))
                .collect(Collectors.joining(WalkingkookaLanguageTag.LOCALE_SEPARATOR));
    }

    private void line(final CharSequence line) {
        this.printer.lineStart();
        this.printer.print(line);
    }

    private final IndentingPrinter printer;

    private static Stream<String> encode0(final String languageTag,
                                          final IndentingPrinter comments) {
        final Locale locale = Locale.forLanguageTag(languageTag);

        final String language = locale.getLanguage();
        final String country = locale.getCountry();
        final String variant = locale.getVariant();
        final String script = locale.getScript();

        String encoded = languageTag + WalkingkookaLanguageTag.LOCALE_COMPONENT_SEPARATOR +
                language + WalkingkookaLanguageTag.LOCALE_COMPONENT_SEPARATOR +
                country + WalkingkookaLanguageTag.LOCALE_COMPONENT_SEPARATOR +
                variant + WalkingkookaLanguageTag.LOCALE_COMPONENT_SEPARATOR +
                script;

        for (; ; ) {
            if (false == encoded.endsWith(WalkingkookaLanguageTag.LOCALE_COMPONENT_SEPARATOR)) {
                break;
            }
            encoded = CharSequences.subSequence(encoded, 0, -1).toString();
        }

        if (encoded.equals(languageTag + WalkingkookaLanguageTag.LOCALE_COMPONENT_SEPARATOR + languageTag)) {
            encoded = languageTag;
        }

        final List<String> encodeds = Lists.array();

        comments.lineStart();
        comments.print("// " +
                pad(languageTag) +
                pad("language=" + language) +
                pad("country=" + country) +
                pad("variant=" + variant) +
                pad("script=" + script) +
                "encoded=" + encoded);

        encodeds.add(encoded);

        if (languageTag.equals("nn-NO")) {
            comments.lineStart();
            comments.print("// " +
                    pad(languageTag) +
                    pad("language=nn-NO") +
                    pad("country=no") +
                    pad("variant=NO") +
                    pad("script=NY") +
                    "encoded=" + encoded);

            encodeds.add("nn-NO" + WalkingkookaLanguageTag.LOCALE_COMPONENT_SEPARATOR +
                    "nn-NO" + WalkingkookaLanguageTag.LOCALE_COMPONENT_SEPARATOR +
                    "no" + WalkingkookaLanguageTag.LOCALE_COMPONENT_SEPARATOR +
                    "NO" + WalkingkookaLanguageTag.LOCALE_COMPONENT_SEPARATOR +
                    "NY");
        }

        return encodeds.stream();
    }

    private static CharSequence pad(final String text) {
        return CharSequences.padRight(text, 20, ' ');
    }
}
