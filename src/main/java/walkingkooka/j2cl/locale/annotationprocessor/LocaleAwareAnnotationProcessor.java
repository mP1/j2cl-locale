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
import walkingkooka.reflect.ClassName;
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
import javax.tools.Diagnostic.Kind;
import javax.tools.StandardLocation;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;

/**
 * An {@link AbstractProcessor} that has several (abstract) template method that probably generate a {@link Class} from
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
        this.logging = this.arguments.get(LOGGING_ANNOTATION_PROCESSOR_OPTION);
        this.messager = environment.getMessager();
    }

    private Map<String, String> arguments;

    @Override
    public final boolean process(final Set<? extends TypeElement> annotations,
                                 final RoundEnvironment environment) {
        final TypeElement exists = elements.getTypeElement(this.generatedClassName());

        // assume null means generated source does not exist...
        if (null == exists) {
            this.process0();
        }

        return false; // whether or not the set of annotation types are claimed by this processor
    }

    private Elements elements;

    public final static String ANNOTATION_PROCESSOR_LOCALES_FILTER = "$FILTERED_LOCALES";

    public final static String SELECTED_LOCALES = "$SELECTED_LOCALES";

    public final static String DATA_COMMENT = "$DATA_COMMENT";

    public final static String DATA = "$DATA";

    /**
     * Read the selected locales from an annotation processor argument, and generate replacements for various placeholders
     * in the template.
     */
    private void process0() {
        try {
            final Logging logging = this.logging();
            final String localeFilter = this.localeFilter();
            final String template = this.template();

            final Set<String> selectedLocales = WalkingkookaLanguageTag.all(localeFilter);

            final String merged = replace(template,
                    ANNOTATION_PROCESSOR_LOCALES_FILTER,
                    CharSequences.quoteAndEscape(localeFilter).toString());

            final String merged2 = replace(merged,
                    SELECTED_LOCALES,
                    CharSequences.quoteAndEscape(selectedLocales.stream()
                            .collect(Collectors.joining(",")))
                            .toString());

            final String data;
            final StringBuilder comments = new StringBuilder();

            try (final IndentingPrinter printer = logging.loggingDestination(comments, this)) {
                final StringBuilder dataStringBuilder = new StringBuilder();
                final String summary = this.generate(localeFilter,
                        selectedLocales,
                        this.arguments::get,
                        StringDataInputDataOutput.output(dataStringBuilder::append),
                        printer);
                printer.flush();

                data = dataStringBuilder.toString();
                this.printSummary(summary + ", " + rawAndCompressedSize(data));
            }

            final String merged3 = logging.replaceTemplatePlaceholder(merged2, comments);

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
                .indenting(INDENTATION);
    }

    final IndentingPrinter createLoggingTextFile() throws IOException {
        final ClassName type = ClassName.with(this.generatedClassName());

        final Writer writer = this.filer.createResource(StandardLocation.CLASS_OUTPUT,
                type.parentPackage().value(),
                type.nameWithoutPackage() + ".DATA.log").openWriter();
        return Printers.writer(writer, LineEnding.SYSTEM)
                .indenting(INDENTATION);
    }

    private final static Indentation INDENTATION = Indentation.SPACES2;

    // adds slash slash comments to the beginning of every line.
    private static void printedLineHandler(final CharSequence line,
                                           final LineEnding lineEnding,
                                           final Printer printer)
            throws PrinterException {
        printer.print("// " + line + lineEnding);
    }

    static CharSequence stringDeclaration(final String data, final int max) {
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

            // -10 just in case backslash is a part of a unicode sequence.
            int end = max - 1;
            final int slashIndex = left.lastIndexOf('\\', max - 10);
            if (-1 != slashIndex) {
                end = slashIndex - 1;
                do {
                    if (left.charAt(end) != '\\') {
                        end--;
                        break;
                    }
                    end--;
                    if (0 == end) {
                        end = max / 2 * 2;
                        break;
                    }
                } while (true);
            }

            statements.append(".append(")
                    .append(CharSequences.quote(left.substring(0, end)))
                    .append(')');
            left = left.substring(end);
        } while (false == left.isEmpty());

        statements.append(".toString()");
        return statements;
    }

    static String replace(final String template,
                          final String placeholder,
                          final String value) {
        final String after = template.replace(placeholder, value);
        if (template.equals(after)) {
            throw new IllegalStateException("Unable to find " + CharSequences.quoteAndEscape(placeholder) + " in " + CharSequences.quoteAndEscape(template));
        }
        return after;
    }

    /**
     * Takes the data string and returns a message such as
     * <pre>
     * data: 123 char(s), utf-8: 200 byte(s), gzipped 89 byte(s)
     * </pre>
     */
    private static String rawAndCompressedSize(final String data) throws IOException {
        final byte[] utf = data.getBytes(Charset.defaultCharset());

        try (final ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
            try (final GZIPOutputStream gzip = new GZIPOutputStream(bytes)) {
                gzip.write(utf);
                gzip.flush();
            }
            bytes.flush();

            return "data: " + data.length() + " char(s), utf-8: " + utf.length + " byte(s), gzipped " + bytes.toByteArray().length + " byte(s)";
        }
    }

    /**
     * Used to print a summary of the extracted locales and possibly other stuff like currencies, and details about the
     * extracted data such as its size both raw and compressed.
     */
    private void printSummary(final String message) {
        this.messager.printMessage(Kind.NOTE, // maven will show this as INFO
                this.getClass().getSimpleName() + " generated " + this.generatedClassName() + ".java, " + message);
    }

    // locale filter....................................................................................................

    @Override
    public Set<String> getSupportedOptions() {
        final Set<String> arguments = Sets.ordered();
        arguments.add(LOCALE_ANNOTATION_PROCESSOR_OPTION);
        arguments.add(LOGGING_ANNOTATION_PROCESSOR_OPTION);
        arguments.addAll(this.additionalArguments());
        return arguments;
    }

    /**
     * Additional annotation processor arguments in addition to {@link #LOCALE_ANNOTATION_PROCESSOR_OPTION}.
     */
    protected abstract Set<String> additionalArguments();

    // locale...........................................................................................................

    /**
     * Returns the annotation processor locale filter option or fails.
     */
    private String localeFilter() {
        final String localeFilter = this.localeFilter;
        if (null == localeFilter) {
            throw new IllegalStateException("Missing annotation processor argument " + CharSequences.quote(LOCALE_ANNOTATION_PROCESSOR_OPTION) + " with csv list of selected Locale(s) (https://github.com/mP1/j2cl-locale#locale-selection-javac-annotation-processor-argument");
        }
        return localeFilter;
    }

    private String localeFilter;

    /**
     * The annotation processor option that has the csv list of locale selectors.
     */
    private final static String LOCALE_ANNOTATION_PROCESSOR_OPTION = "walkingkooka.j2cl.java.util.Locale";

    // logging..........................................................................................................

    /**
     * Returns the annotation processor {@link Logging} fails if absent.
     */
    private Logging logging() {
        final String logging = this.logging;
        if (null == logging) {
            this.reportLoggingAnnotationProcessorArgumentFail("Missing annotation processor argument");
        }

        try {
            return Logging.valueOf(logging);
        } catch (final Exception ignore) {
            return this.reportLoggingAnnotationProcessorArgumentFail("Bad annotation processor argument");
        }
    }

    private Logging reportLoggingAnnotationProcessorArgumentFail(final String message) {
        return reportLoggingAnnotationProcessorArgumentFail(this.logging, message);
    }

    static Logging reportLoggingAnnotationProcessorArgumentFail(final String message,
                                                                final String logging) {
        throw new IllegalStateException(message + " " +
                CharSequences.quote(LOGGING_ANNOTATION_PROCESSOR_OPTION) +
                (null == logging ? "" : "=" + CharSequences.quoteIfChars(logging)) +
                ", expected one of " + Arrays.stream(Logging.values()).map(Logging::name).collect(Collectors.joining(", ")) + " (https://github.com/mP1/j2cl-locale#logging-javac-annotation-processor-argument)");
    }

    private String logging;

    /**
     * The annotation processor option that controls generated class logging destination.
     */
    private final static String LOGGING_ANNOTATION_PROCESSOR_OPTION = "walkingkooka.j2cl.locale.Logging";

    // generate merge replacement.......................................................................................

    /**
     * This method is invoked with one or more language tags and should return something like a field or method that will appear in the template.
     * It should return a summary that includes the items extracted.
     */
    protected abstract String generate(final String localeFilter,
                                       final Set<String> languageTags,
                                       final Function<String, String> parameters,
                                       final DataOutput data,
                                       final IndentingPrinter comments) throws Exception;

    // template.........................................................................................................

    /**
     * Reads the template that will be host the generated all() method.
     */
    private String template() throws IOException {
        final String templateResourceName = this.templateResourceName();

        try (final InputStream resource = this.getClass().getResourceAsStream(templateResourceName)) {
            if (null == resource) {
                throw new IllegalStateException("Unable to find template " + CharSequences.quoteAndEscape(templateResourceName));
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
                throw new IOException("Error reading template " + CharSequences.quoteAndEscape(templateResourceName));
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
