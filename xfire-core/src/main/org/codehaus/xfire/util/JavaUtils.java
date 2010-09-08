/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.xfire.util;

import java.text.Collator;
import java.util.Arrays;
import java.util.Locale;

public class JavaUtils
{

    /** Collator for comparing the strings */
    static final Collator englishCollator = Collator.getInstance(Locale.ENGLISH);

    /** Use this character as suffix */
    static final char keywordPrefix = '_';
    
    /**
     * These are java keywords as specified at the following URL (sorted alphabetically).
     * http://java.sun.com/docs/books/jls/second_edition/html/lexical.doc.html#229308
     * Note that false, true, and null are not strictly keywords; they are literal values,
     * but for the purposes of this array, they can be treated as literals.
     *    ****** PLEASE KEEP THIS LIST SORTED IN ASCENDING ORDER ******
     */
    static final String keywords[] =
    {
        "abstract",  "assert",       "boolean",    "break",      "byte",      "case",
        "catch",     "char",         "class",      "const",     "continue",
        "default",   "do",           "double",     "else",      "extends",
        "false",     "final",        "finally",    "float",     "for",
        "goto",      "if",           "implements", "import",    "instanceof",
        "int",       "interface",    "long",       "native",    "new",
        "null",      "package",      "private",    "protected", "public",
        "return",    "short",        "static",     "strictfp",  "super",
        "switch",    "synchronized", "this",       "throw",     "throws",
        "transient", "true",         "try",        "void",      "volatile",
        "while"
    };
    
    /**
     * checks if the input string is a valid java keyword.
     * @return boolean true/false
     */
    public static boolean isJavaKeyword(String keyword) {
      return (Arrays.binarySearch(keywords, keyword, englishCollator) >= 0);
    }

    /**
     * Turn a java keyword string into a non-Java keyword string.  (Right now
     * this simply means appending an underscore.)
     */
    public static String makeNonJavaKeyword(String keyword){
        return  keywordPrefix + keyword;
     }
    
}
