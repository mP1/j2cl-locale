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

import walkingkooka.collect.set.Sets;
import walkingkooka.j2cl.java.io.string.StringDataInputDataOutput;
import walkingkooka.j2cl.locale.LocaleAware;
import walkingkooka.j2cl.locale.WalkingkookaLanguageTag;
import walkingkooka.text.CharSequences;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.Printer;
import walkingkooka.text.printer.PrinterException;
import walkingkooka.text.printer.Printers;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * An {@link AbstractProcessor} that has several (abstract) template method that probably generate a provider from
 * selected locales.
 */
public abstract class LocaleAwareAnnotationProcessor extends AbstractProcessor {

    protected LocaleAwareAnnotationProcessor() {
        super();
    }

    @Override
    public final SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public final Set<String> getSupportedAnnotationTypes() {
        return Sets.of(LocaleAware.class.getName());
    }

    @Override
    public final synchronized void init(final ProcessingEnvironment environment) {
        super.init(environment);

        this.elements = environment.getElementUtils();
        this.filer = environment.getFiler();
        this.arguments = environment.getOptions();
        this.localeFilter = this.arguments.get(LOCALE_ANNOTATION_PROCESSOR_OPTION);
        this.messager = environment.getMessager();
    }

    private Map<String, String> arguments;

    @Override
    public final boolean process(final Set<? extends TypeElement> annotations,
                                 final RoundEnvironment environment) {
        final TypeElement exists = elements.getTypeElement(this.generatedClassName());

        // assume null means generated source does not exist...
        if (null == exists) {
            // without this check the generated class will be written multiple times resulting in an exception when attempting to create the file.
            if (environment.processingOver()) {
                this.process0();
            }
        }

        return false; // whether or not the set of annotation types are claimed by this processor
    }

    private Elements elements;

    public final String ANNOTATION_PROCESSOR_LOCALES_FILTER = "$FILTERED_LOCALES";

    public final String SELECTED_LOCALES = "$SELECTED_LOCALES";

    public final String DATA_COMMENT = "$DATA_COMMENT";

    public final String DATA = "$DATA";

    /**
     * Read the selected locales from an annotation processor argument, and generate replacements for various placeholders
     * in the template.
     */
    private void process0() {
        try {
            final String localeFilter = this.localeFilter();
            final String template = this.providerTemplate();

            final Set<String> selectedLocales = WalkingkookaLanguageTag.all(localeFilter);

            final String merged = replace(template,
                    ANNOTATION_PROCESSOR_LOCALES_FILTER,
                    CharSequences.quoteAndEscape(localeFilter).toString());

            final String merged2 = replace(merged,
                    SELECTED_LOCALES,
                    CharSequences.quoteAndEscape(selectedLocales.stream()
                            .collect(Collectors.joining(",")))
                            .toString());

            final StringBuilder data = new StringBuilder();
            final StringBuilder comments = new StringBuilder();

            try (final IndentingPrinter printer = comments(Printers.stringBuilder(comments, LineEnding.SYSTEM))) {
                this.generate(selectedLocales,
                        this.arguments::get,
                        StringDataInputDataOutput.output(data::append),
                        printer);
                printer.flush();
            }

            final String merged3 = replace(merged2,
                    DATA_COMMENT,
                    "" + comments);

            final String merged4 = replace(merged3,
                    DATA,
                    "" + stringDeclaration(data, 256 * 64 - 1)); // 16k chars UTF8 encoded cant overflow 64k chars

            this.writeGeneratedTypeSource(merged4);
        } catch (final Exception cause) {
            this.error(cause.getMessage());
        }
    }

    public static IndentingPrinter comments(final Printer printer) {
        return printer.printedLine(LocaleAwareAnnotationProcessor::printedLineHandler)
                .indenting(Indentation.with("  "));
    }

    // adds slash slash comments to the beginning of every line.
    private static void printedLineHandler(final CharSequence line,
                                           final LineEnding lineEnding,
                                           final Printer printer)
            throws PrinterException {
        printer.print("// " + line + lineEnding);
    }

    static CharSequence stringDeclaration(final CharSequence data, final int max) {
        return data.length() < max ?
                CharSequences.quoteAndEscape(data) :
                stringBuilderSplit(CharSequences.escape(data).toString(), max);
    }

    private static CharSequence stringBuilderSplit(final String data, final int max) {
        final StringBuilder statements = new StringBuilder();
        statements.append("new java.lang.StringBuilder()");

        String left = data;

        do {
            if (left.length() < max) {
                statements.append(".append(").append(CharSequences.quote(left)).append(')');
                break;
            }

            int end = max - 1;
            final int slashIndex = left.lastIndexOf('\\', end);
            if (slashIndex > max - 10) {
                end = slashIndex;
            }

            statements.append(".append(")
                    .append(CharSequences.quote(left.substring(0, end)))
                    .append(')');
            left = left.substring(end);
        } while (false == left.isEmpty());

        statements.append(".toString()");
        return statements;
    }

    private static String replace(final String template,
                                  final String placeholder,
                                  final String value) {
        final String after = template.replace(placeholder, value);
        if (template.equals(after)) {
            throw new IllegalStateException("Unable to find " + CharSequences.quoteAndEscape(placeholder) + " in " + CharSequences.quoteAndEscape(template));
        }
        return after;
    }

    // locale filter....................................................................................................

    @Override
    public Set<String> getSupportedOptions() {
        final Set<String> arguments = Sets.ordered();
        arguments.add(LOCALE_ANNOTATION_PROCESSOR_OPTION);
        arguments.addAll(this.additionalArguments());
        return arguments;
    }

    /**
     * Additional annotation processor arguments in addition to {@link #LOCALE_ANNOTATION_PROCESSOR_OPTION}.
     */
    protected abstract Set<String> additionalArguments();

    /**
     * Returns the annotation processor locale filter option or fails.
     */
    private String localeFilter() {
        final String localeFilter = this.localeFilter;
        if (null == localeFilter) {
            throw new IllegalStateException("Missing annotation processor argument " + CharSequences.quote(LOCALE_ANNOTATION_PROCESSOR_OPTION) + " with csv list of selected Locale(s) https://github.com/mP1/j2cl-locale");
        }
        return localeFilter;
    }

    private String localeFilter;

    /**
     * The annotation processor option that has the csv list of locale selectors.
     */
    private final static String LOCALE_ANNOTATION_PROCESSOR_OPTION = "walkingkooka.j2cl.java.util.Locale";

    // generate merge replacement.......................................................................................

    /**
     * This method is invoked with one or more language tags and should return something like a field or method that will appear in the template.
     */
    protected abstract void generate(final Set<String> languageTags,
                                     final Function<String, String> parameters,
                                     final DataOutput data,
                                     final IndentingPrinter comments) throws Exception;

    // template.........................................................................................................

    /**
     * Reads the template that will be host the generated all() method.
     */
    private String providerTemplate() throws IOException {
        final String templateResourceName = this.templateResourceName();

        try (final InputStream resource = this.getClass().getResourceAsStream(templateResourceName)) {
            if (null == resource) {
                throw new IllegalStateException("Unable to find provider template " + CharSequences.quoteAndEscape(templateResourceName));
            }
            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(resource, Charset.defaultCharset()))) {
                final StringBuilder text = new StringBuilder();
                final char[] buffer = new char[4096];

                for (; ; ) {
                    final int count = reader.read(buffer);
                    if (-1 == count) {
                        break;
                    }
                    text.append(buffer, 0, count);
                }
                return text.toString();
            } catch (final IOException cause) {
                throw new IOException("Error reading provider template " + CharSequences.quoteAndEscape(templateResourceName));
            }
        }
    }

    /**
     * Uses the generated class name to create its template resource name.
     */
    private String templateResourceName() {
        final String typeName = this.generatedClassName();
        return typeName.substring(typeName.lastIndexOf('.') + 1) + ".java.txt";
    }

    // write source file................................................................................................

    /**
     * The fully qualified class name of the class source being generated.
     */
    protected abstract String generatedClassName();

    /**
     * Writes the result of merging the template with the generated method source.
     */
    private void writeGeneratedTypeSource(final String content) throws IOException {
        final String typeName = this.generatedClassName();
        try (final Writer writer = this.filer.createSourceFile(typeName).openWriter()) {
            writer.write(content);
            writer.flush();
        }
    }

    private Filer filer;

    // reporting........................................................................................................

    /**
     * Reports an error during the compile process.
     */
    private void error(final String message) {
        this.messager.printMessage(Diagnostic.Kind.ERROR, message);
    }

    private Messager messager;
}
