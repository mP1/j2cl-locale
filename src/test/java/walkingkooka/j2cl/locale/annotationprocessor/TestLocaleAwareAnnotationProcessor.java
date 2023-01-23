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

import walkingkooka.text.printer.IndentingPrinter;

import java.io.DataOutput;
import java.util.Set;
import java.util.function.Function;

public final class TestLocaleAwareAnnotationProcessor extends LocaleAwareAnnotationProcessor{

    @Override
    protected Set<String> additionalArguments() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected String generate(final String localeFilter,
                              final Set<String> languageTags,
                              final Function<String, String> parameters,
                              final DataOutput data,
                              final IndentingPrinter comments) throws Exception {
        throw new UnsupportedOperationException();
    }
}
