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

import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.Printers;

import java.io.IOException;

enum Logging {

    /**
     * No logging will be output anywhere
     */
    NONE {
        @Override
        IndentingPrinter loggingDestination(final StringBuilder comments,
                                            final LocaleAwareAnnotationProcessor filer) {
            return Printers.sink(LineEnding.NONE)
                    .indenting(Indentation.EMPTY);
        }

        @Override
        String replaceTemplatePlaceholder(final String template, final CharSequence comment) {
            return replaceTemplatePlaceholder0(template, "");
        }
    },

    /**
     * Slash slash comments along with the logging messages are inserted into the generated class replacing the <code>$DATA_COMMENT</code> placeholder in templates.
     */
    SLASH_SLASH_COMMENTS {
        @Override
        IndentingPrinter loggingDestination(final StringBuilder comments,
                                            final LocaleAwareAnnotationProcessor filer) {
            return LocaleAwareAnnotationProcessor.comments(Printers.stringBuilder(comments, LineEnding.SYSTEM));
        }

        @Override
        String replaceTemplatePlaceholder(final String template, final CharSequence comment) {
            return replaceTemplatePlaceholder0(template, comment.toString());
        }
    },

    /**
     * Logging messages are written to a resource with the same name as the generated class + ".DATA.txt"
     */
    TXT_FILE {
        @Override
        IndentingPrinter loggingDestination(final StringBuilder comments,
                                            final LocaleAwareAnnotationProcessor filer) throws IOException {
            return filer.createLoggingTextFile();
        }

        @Override
        String replaceTemplatePlaceholder(final String template, final CharSequence comment) {
            return replaceTemplatePlaceholder0(template, "");
        }
    };

    abstract IndentingPrinter loggingDestination(final StringBuilder comments,
                                                 final LocaleAwareAnnotationProcessor filer) throws IOException;

    abstract String replaceTemplatePlaceholder(final String template, final CharSequence comment);

    static String replaceTemplatePlaceholder0(final String template,
                                              final String value) {
        return LocaleAwareAnnotationProcessor.replace(template,
                LocaleAwareAnnotationProcessor.DATA_COMMENT,
                value);
    }
}
