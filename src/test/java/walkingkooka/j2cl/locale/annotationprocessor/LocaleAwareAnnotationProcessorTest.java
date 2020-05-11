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
import walkingkooka.reflect.ClassAttributes;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.CharSequences;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class LocaleAwareAnnotationProcessorTest implements ClassTesting<LocaleAwareAnnotationProcessor> {

    @Test
    public void testAbstractClass() {
        assertEquals(true, ClassAttributes.ABSTRACT.is(LocaleAwareAnnotationProcessor.class));
    }

    @Test
    public void testProtectedDefaultConstructorPresent() throws Exception {
        assertEquals(JavaVisibility.PROTECTED, JavaVisibility.of(LocaleAwareAnnotationProcessor.class.getDeclaredConstructor()));
    }

    // stringDeclaration................................................................................................

    @Test
    public void testStringDeclarationShort() {
        this.stringDeclarationAndCheck("",
                1,
                CharSequences.quoteAndEscape("").toString());
    }

    @Test
    public void testStringDeclarationShort2() {
        this.stringDeclarationAndCheck("a",
                2,
                CharSequences.quoteAndEscape("a").toString());
    }

    @Test
    public void testStringDeclarationSplit() {
        this.stringDeclarationAndCheck("abcdefghijklmnopq",
                13,
                "new java.lang.StringBuilder().append(\"abcdefghijkl\").append(\"mnopq\").toString()"
        );
    }

    @Test
    public void testStringDeclarationSplitBackslash() {
        this.stringDeclarationAndCheck("abcdefghijk\\lmnopq",
                13,
                "new java.lang.StringBuilder().append(\"abcdefghijk\\\").append(\"\\lmnopq\").toString()"
        );
    }

    @Test
    public void testStringDeclarationSplitBackslash2() {
        this.stringDeclarationAndCheck("abcdefghij\\klmnopq",
                13,
                "new java.lang.StringBuilder().append(\"abcdefghij\\\\\").append(\"klmnopq\").toString()"
        );
    }

    @Test
    public void testStringDeclarationSplitBackslash3() {
        this.stringDeclarationAndCheck("a\\bcdefghijklmnopq",
                13,
                "new java.lang.StringBuilder().append(\"a\\\\bcdefghij\").append(\"klmnopq\").toString()"
        );
    }

    @Test
    public void testStringDeclarationSplitBackslash4() {
        this.stringDeclarationAndCheck("abcdefghijklmnopq\\",
                13,
                "new java.lang.StringBuilder().append(\"abcdefghijkl\").append(\"mnopq\\\\\").toString()"
        );
    }

    private void stringDeclarationAndCheck(final String text, final int max, final String expected) {
        final String string = LocaleAwareAnnotationProcessor.stringDeclaration(text, max).toString();
        System.out.println(string);

        assertEquals(CharSequences.escape(expected).toString(),
                CharSequences.escape(string).toString(),
                ()-> CharSequences.quoteAndEscape(text) + " max=" + max);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<LocaleAwareAnnotationProcessor> type() {
        return LocaleAwareAnnotationProcessor.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
