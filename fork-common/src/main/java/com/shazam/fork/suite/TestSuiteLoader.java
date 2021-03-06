/*
 * Copyright 2016 Shazam Entertainment Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.shazam.fork.suite;

import com.shazam.fork.io.DexFileExtractor;
import com.shazam.fork.model.TestCaseEvent;
import com.shazam.fork.model.TestCaseEventFactory;
import org.jf.dexlib.*;
import org.jf.dexlib.EncodedValue.AnnotationEncodedSubValue;
import org.jf.dexlib.EncodedValue.ArrayEncodedValue;
import org.jf.dexlib.EncodedValue.EncodedValue;
import org.jf.dexlib.EncodedValue.StringEncodedValue;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.*;
import java.util.stream.Stream;

import static com.shazam.fork.suite.AnnotationParser.parseAnnotation;
import static java.lang.Math.min;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public class TestSuiteLoader {
    private static final String TEST_ANNOTATION = "Lorg/junit/Test;";
    private static final String IGNORE_ANNOTATION = "Lorg/junit/Ignore;";
    private static final String REVOKE_PERMISSION_ANNOTATION = "Lcom/shazam/fork/RevokePermission;";
    private static final String TEST_PROPERTIES_ANNOTATION = "Lcom/shazam/fork/TestProperties;";
    public static final String JAVA_LANG_OBJECT_SINGATURE = "Ljava/lang/Object;";

    private final File instrumentationApkFile;
    private final DexFileExtractor dexFileExtractor;
    private final TestClassMatcher testClassMatcher;
    private final List<String> includedAnnotations;
    private final List<String> excludedAnnotations;
    private final TestCaseEventFactory factory;

    public TestSuiteLoader(File instrumentationApkFile,
                           DexFileExtractor dexFileExtractor,
                           TestClassMatcher testClassMatcher,
                           String includedAnnotation,
                           String excludedAnnotation,
                           TestCaseEventFactory testCaseEventFactory) {
        this.instrumentationApkFile = instrumentationApkFile;
        this.dexFileExtractor = dexFileExtractor;
        this.testClassMatcher = testClassMatcher;
        this.includedAnnotations = parseAnnotation().apply(includedAnnotation);
        this.excludedAnnotations = parseAnnotation().apply(excludedAnnotation);
        this.factory = testCaseEventFactory;
    }

    public Collection<TestCaseEvent> loadTestSuite() throws NoTestCasesFoundException {
        Collection<DexFile> dexFiles = dexFileExtractor.getDexFiles(instrumentationApkFile);
        List<TestCaseEvent> testCaseEvents = extractTests(dexFiles);

        if (testCaseEvents.isEmpty()) {
            throw new NoTestCasesFoundException("No tests cases were found in the test APK: " + instrumentationApkFile.getAbsolutePath());
        }
        return testCaseEvents;
    }

    private List<TestCaseEvent> extractTests(Collection<DexFile> dexFiles) {
        List<ClassDefItem> classDefItems = dexFiles.stream()
                .map(dexFile -> dexFile.ClassDefsSection.getItems())
                .flatMap(Collection::stream)
                .collect(toList());

        return classDefItems.stream()
                .filter(c -> testClassMatcher.matchesPatterns(c.getClassType().getTypeDescriptor()))
                .map(item -> convertClassToTestCaseEvents(item, classDefItems))
                .flatMap(Collection::stream)
                .collect(toList());
    }

    @Nonnull
    private List<TestCaseEvent> convertClassToTestCaseEvents(ClassDefItem classDefItem, List<ClassDefItem> classDefItems) {
        boolean classIncluded = false;
        AnnotationDirectoryItem annotationDirectoryItem = classDefItem.getAnnotations();
        if (annotationDirectoryItem == null) {
            return emptyList();
        }

        AnnotationSetItem classSet = annotationDirectoryItem.getClassAnnotations();
        if (classSet != null) {
            AnnotationItem[] annotations = classSet.getAnnotations();
            if (isClassExcluded(annotations)) {
                return emptyList();
            } else {
                classIncluded = true;
            }
        }

        return parseMethods(classDefItem, annotationDirectoryItem, classIncluded, classDefItems);
    }

    private List<TestCaseEvent> parseMethods(ClassDefItem classDefItem,
                                             AnnotationDirectoryItem annotationDirectory,
                                             boolean classIncluded,
                                             List<ClassDefItem> classDefItems) {
        return recursiveSearch(classDefItem, annotationDirectory, classDefItems).stream()
                .flatMap(method -> parseTestCaseEvents(classDefItem, annotationDirectory, method, classIncluded))
                .collect(toList());
    }

    private List<AnnotationDirectoryItem.MethodAnnotation> recursiveSearch(ClassDefItem classDefItem,
                                                                           AnnotationDirectoryItem directoryItem,
                                                                           List<ClassDefItem> classDefItems) {
        TypeIdItem superClassIdItem = classDefItem.getSuperclass();
        String superClassDescriptor = superClassIdItem.getTypeDescriptor();
        List<AnnotationDirectoryItem.MethodAnnotation> list = new ArrayList<>();
        if (directoryItem != null) {
            list.addAll(directoryItem.getMethodAnnotations());
        }
        if (!JAVA_LANG_OBJECT_SINGATURE.equals(superClassDescriptor)) {
            findClassDef(classDefItems, superClassDescriptor)
                    .ifPresent(a -> list.addAll(recursiveSearch(a, a.getAnnotations(), classDefItems)));
        }
        return list;
    }

    private Optional<ClassDefItem> findClassDef(List<ClassDefItem> classDefItems, String identity) {
        return classDefItems.stream()
                .filter(a -> a.getClassType().getTypeDescriptor().equals(identity))
                .findFirst();
    }

    private Stream<TestCaseEvent> parseTestCaseEvents(ClassDefItem classDefItem,
                                                      AnnotationDirectoryItem annotationDirectoryItem,
                                                      AnnotationDirectoryItem.MethodAnnotation methodAnnotation,
                                                      boolean classIncluded) {
        return stream(methodAnnotation.annotationSet.getAnnotations())
                .filter(annotation -> TEST_ANNOTATION.equals(stringType(annotation)))
                .filter(item -> isMethodIncluded(item, classIncluded))
                .map(annotation -> convertToTestCaseEvent(classDefItem, annotationDirectoryItem, methodAnnotation));
    }

    private boolean isMethodIncluded(AnnotationItem a, boolean classIncluded) {
        return classIncluded || included(a) && !excluded(a);
    }

    private boolean isClassExcluded(AnnotationItem... annotations) {
        return !included(annotations) || excluded(annotations);
    }

    @Nonnull
    private TestCaseEvent convertToTestCaseEvent(ClassDefItem classDefItem,
                                                 AnnotationDirectoryItem annotationDirectoryItem,
                                                 AnnotationDirectoryItem.MethodAnnotation method) {
        String testMethod = method.method.getMethodName().getStringValue();
        AnnotationItem[] annotations = method.annotationSet.getAnnotations();
        String testClass = getClassName(classDefItem);
        boolean ignored = isClassIgnored(annotationDirectoryItem) || isMethodIgnored(annotations);
        List<String> permissionsToRevoke = getPermissionsToRevoke(annotations);
        Map<String, String> properties = getTestProperties(annotations);
        return factory.newTestCase(testMethod, testClass, ignored, permissionsToRevoke, properties);
    }

    private String getClassName(ClassDefItem classDefItem) {
        String typeDescriptor = classDefItem.getClassType().getTypeDescriptor();
        return typeDescriptor.substring(1, typeDescriptor.length() - 1).replace('/', '.');
    }

    private boolean isMethodIgnored(AnnotationItem... annotationItems) {
        return containsAnnotation(IGNORE_ANNOTATION, annotationItems);
    }

    private List<String> getPermissionsToRevoke(AnnotationItem[] annotations) {
        return stream(annotations)
                .filter(annotationItem -> REVOKE_PERMISSION_ANNOTATION.equals(stringType(annotationItem)))
                .map(annotationItem -> annotationItem.getEncodedAnnotation().values)
                .flatMap(encodedValues -> stream(encodedValues)
                        .flatMap(encodedValue -> stream(((ArrayEncodedValue) encodedValue).values)
                                .map(stringEncoded -> ((StringEncodedValue) stringEncoded).value.getStringValue())))
                .collect(toList());
    }

    private Map<String, String> getTestProperties(AnnotationItem[] annotations) {
        Map<String, String> properties = new HashMap<>();
        stream(annotations)
                .filter(annotationItem -> TEST_PROPERTIES_ANNOTATION.equals(stringType(annotationItem)))
                .map(AnnotationItem::getEncodedAnnotation)
                .forEach(an -> {
                    List<String> keys = getAnnotationProperty(an, "keys");
                    List<String> values = getAnnotationProperty(an, "values");

                    for (int i = 0; i < min(values.size(), keys.size()); i++) {
                        properties.put(keys.get(i), values.get(i));
                    }
                });
        return properties;
    }

    private List<String> getAnnotationProperty(AnnotationEncodedSubValue an, String propertyName) {
        int propValueIndex = indexOfName(an, propertyName);
        if (propValueIndex >= 0) {
            EncodedValue[] values = ((ArrayEncodedValue) an.values[propValueIndex]).values;
            return stream(values)
                    .map(stringEncodedValue -> ((StringEncodedValue) stringEncodedValue).value.getStringValue())
                    .collect(toList());
        } else {
            return emptyList();
        }
    }

    private int indexOfName(AnnotationEncodedSubValue p, String key) {
        int index = -1;
        StringIdItem[] names = p.names;
        for (int i = 0; i < names.length; i++) {
            if (names[i].getStringValue().equals(key)) return i;
        }
        return index;
    }

    private boolean isClassIgnored(AnnotationDirectoryItem annotationDirectoryItem) {
        AnnotationSetItem classAnnotations = annotationDirectoryItem.getClassAnnotations();
        return classAnnotations != null && containsAnnotation(IGNORE_ANNOTATION, classAnnotations.getAnnotations());
    }


    private boolean included(AnnotationItem... annotations) {
        return includedAnnotations.isEmpty() ||
                containsAnnotation(annotations, includedAnnotations.stream());
    }

    private boolean containsAnnotation(AnnotationItem[] annotations, Stream<String> stream) {
        return stream.anyMatch(included -> containsAnnotation(included, annotations));
    }

    private boolean excluded(AnnotationItem... annotations) {
        return !excludedAnnotations.isEmpty()
                && containsAnnotation(annotations, excludedAnnotations.stream());
    }

    private boolean containsAnnotation(String comparisonAnnotation, AnnotationItem... annotations) {
        return stream(annotations).anyMatch(annotation -> comparisonAnnotation.equals(stringType(annotation)));
    }

    private String stringType(AnnotationItem annotation) {
        return annotation.getEncodedAnnotation().annotationType.getTypeDescriptor();
    }
}
