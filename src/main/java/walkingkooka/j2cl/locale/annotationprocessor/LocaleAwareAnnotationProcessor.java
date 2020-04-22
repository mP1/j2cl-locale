package walkingkooka.j2cl.locale.annotationprocessor;

import walkingkooka.collect.set.Sets;
import walkingkooka.j2cl.locale.WalkingkookaLanguageTag;
import walkingkooka.text.CharSequences;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Set;

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
        return Sets.of(this.annotationType());
    }

    /**
     * The fully qualified annotation type that appears on the provider type. The annotation and attributes are irrelevant
     * the only wish is to run this {@link javax.annotation.processing.Processor}.
     */
    protected abstract String annotationType();

    @Override
    public final synchronized void init(final ProcessingEnvironment environment) {
        super.init(environment);

        this.filer = environment.getFiler();
        this.localeFilter = environment.getOptions().get(LOCALE_ANNOTATION_PROCESSOR_OPTION);
        this.messager = environment.getMessager();
    }

    @Override
    public final boolean process(final Set<? extends TypeElement> annotations,
                                 final RoundEnvironment environment) {
        // without this check the generated class will be written multiple times resulting in an exception when attempting to create the file.
        if (environment.processingOver()) {
            this.process0();
        }

        return true; // whether or not the set of annotation types are claimed by this processor
    }

    /**
     * Read the selected locales from an annotation processor argument, f
     */
    private void process0() {
        try {
            final String localeFilter = this.localeFilter();
            final String replacement = this.generateTemplateMergeReplacement(WalkingkookaLanguageTag.all(localeFilter));
            final String template = this.providerTemplate();
            final String placeholder = this.placeholder();
            final String merged = template.replace(placeholder, replacement);

            this.writeGeneratedTypeSource(merged);
        } catch (final Exception cause) {
            this.error(cause.getMessage());
        }
    }

    // locale filter....................................................................................................

    @Override
    public final Set<String> getSupportedOptions() {
        return Sets.of(LOCALE_ANNOTATION_PROCESSOR_OPTION);
    }

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
     * This method is invoked with one or more language tags and should return a method that will appear in the template.
     */
    protected abstract String generateTemplateMergeReplacement(final Set<String> languageTags);

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

    // template merge...................................................................................................

    /**
     * The placeholder that appears in the template that will be replaced by the response of {@link #generateTemplateMergeReplacement(Set)}.
     */
    protected abstract String placeholder();

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
