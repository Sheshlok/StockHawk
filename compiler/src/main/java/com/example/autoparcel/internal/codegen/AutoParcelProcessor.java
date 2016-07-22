package com.example.autoparcel.internal.codegen;

import com.example.autoparcel.AutoParcel;
import com.example.autoparcel.ParcelAdapter;
import com.example.autoparcel.internal.common.MoreElements;
import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.NameAllocator;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;


/**
 * The custom processor extends from AbstractProcessor and is annotated with fully qualified names
 * of the annotations it will support. Few things to note here:
 *
 * 1. The processor extends from the AbstractProcessor class
 * 2. It shall be annotated with fully qualified names of the annotations it will process
 * 3. It needs to implement the 'process()' method. This method will be called during the build
 *    process to process the @AutoParcel annotation we created before. 'process()' is where all
 *    the magic is implemented. :)
 *
 * Source: {@linkplain "https://medium.com/@aitorvs/annotation-processing-in-android-studio-7042ccb83024#.gq0cpnln9"}
 *
 */
@SupportedAnnotationTypes("com.example.autoparcel.AutoParcel")
public final class AutoParcelProcessor extends AbstractProcessor{

    private ErrorReporter mErrorReporter;
    private Types mTypeUtils;

    static final class Property {
        final String fieldName;
        final VariableElement element;
        final TypeName typeName;
        final ImmutableSet<String> annotations;
        TypeMirror typeAdapter;

        Property(String fieldName, VariableElement element) {
            this.fieldName = fieldName;
            this.element = element;
            this.typeName = TypeName.get(element.asType());
            this.annotations = getAnnotations(element);

            ParcelAdapter parcelAdapter = element.getAnnotation(ParcelAdapter.class);
            if (parcelAdapter != null) {
                try {
                    parcelAdapter.value();
                } catch (MirroredTypeException e) {
                    this.typeAdapter = e.getTypeMirror();
                }
            }
        }

        public boolean isNullable() {
            return this.annotations.contains("Nullable");
        }

        private ImmutableSet<String> getAnnotations(VariableElement element) {
            ImmutableSet.Builder<String> builder = ImmutableSet.builder();
            for (AnnotationMirror annotationMirror: element.getAnnotationMirrors()) {
                builder.add(annotationMirror.getAnnotationType().asElement().getSimpleName().toString());
            }
            return builder.build();
        }
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mErrorReporter = new ErrorReporter(processingEnv);
        mTypeUtils = processingEnv.getTypeUtils();
    }

    /**
     * This method is called during build to process the @AutoParcel annotation we created before
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        Collection<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(AutoParcel.class);

        List<TypeElement> typeElementList = new ImmutableList.Builder<TypeElement>()
                .addAll(ElementFilter.typesIn(annotatedElements))
                .build();

        for (TypeElement typeElement: typeElementList) {
            processType(typeElement);
        }

        /**
         *  We are the only ones handling AutoParcel annotations
         */
        return true;
    }

    private void processType(TypeElement typeElement) {

        AutoParcel autoParcel = typeElement.getAnnotation(AutoParcel.class);

        if (autoParcel == null) {
            mErrorReporter.abortWithError("annotation processor for @AutoParcel was invoked with a" +
                    "type annotated differently; compiler bug? O_o", typeElement);
        }

        if (typeElement.getKind() != ElementKind.CLASS) {
            mErrorReporter.abortWithError("@" + AutoParcel.class.getName() + " only applies to classes", typeElement);

        }

        if (ancestorIsAutoParcel(typeElement)) {
            mErrorReporter.abortWithError("One @AutoParcel class shall not extend another", typeElement);
        }
        // get the fully qualified class name
        String fqClassName = generatedSubclassName(typeElement, 0);
        // class name
        String source = generateClass(typeElement, fqClassName, typeElement.getSimpleName().toString(), false);
        source = Reformatter.fixup(source);
        writeSourceFile(fqClassName, source, typeElement);
    }

    private void writeSourceFile(String className, String text, TypeElement originatingType) {
        try {
            JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(className, originatingType);
            Writer writer = sourceFile.openWriter();
            try {
                writer.write(text);
            } finally {
                writer.close();
            }
        } catch (IOException e) {
            // silent
            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                    "Could not write generated class" + className + ": " + e);
        }
    }

    private boolean ancestorIsAutoParcel(TypeElement typeElement) {
        while (true) {
            TypeMirror parentMirror = typeElement.getSuperclass();
            if (parentMirror.getKind() == TypeKind.NONE) {
                return false;
            }

            TypeElement parentElement = (TypeElement) mTypeUtils.asElement(parentMirror);
            if (MoreElements.isAnnotationPresent(parentElement, AutoParcel.class)) {
                return true;
            }

            typeElement = parentElement;
        }
    }


    //---------------------------- Helper methods for getting fully qualified className ------------
    private String generatedSubclassName(TypeElement typeElement, int depth) {
        return generatedClassName(typeElement, Strings.repeat("$", depth) + "AutoParcel_");
    }

    private String generatedClassName(TypeElement typeElement, String prefix) {
        String name = typeElement.getSimpleName().toString();
        while (typeElement.getEnclosingElement() instanceof TypeElement) {
            typeElement = (TypeElement) typeElement.getEnclosingElement();
            name = typeElement.getSimpleName() + "_" + name;
        }

        String pkg = TypeUtil.packageNameOf(typeElement);
        String dot = pkg.length() == 0 ? "" : ".";
        return  pkg + dot + prefix + name;
    }


    //---------------------------- Helper methods for getting fully qualified className ------------

    private String generateClass(TypeElement typeElement, String className, String classToExtend, boolean isFinal) {
        if (typeElement == null) {
            mErrorReporter.abortWithError("generateClass was invoked with null type", typeElement);
        }

        if (className == null) {
            mErrorReporter.abortWithError("generateClass was invoked with null class name", typeElement);
        }

        if (classToExtend == null) {
            mErrorReporter.abortWithError("generateClass was invoked with null parent class", typeElement);
        }

        List<VariableElement> nonPrivateFields = getNonPrivateFields(typeElement);
        if (nonPrivateFields.isEmpty()) {
            mErrorReporter.abortWithError("generateClass error, all fields are declared private", typeElement);
        }

        // get the properties
        ImmutableList<Property> properties = buildProperties(nonPrivateFields);

        // get the typeAdapters
        ImmutableMap<TypeMirror, FieldSpec> typeAdapters = getTypeAdapters(properties);

        // generate the AutoParcel_??? class
        String pkg = TypeUtil.packageNameOf(typeElement);
        TypeName classTypeName = ClassName.get(pkg, className);
        TypeSpec.Builder subClass = TypeSpec.classBuilder(className)
                // Class must always be final
                .addModifiers(Modifier.FINAL)
                // extends from original abstract class
                .superclass(ClassName.get(pkg, classToExtend))
                // Add the constructor
                .addMethod(generateConstructor(properties))
                // overrides describeContents
                .addMethod(generateDescribeContents())
                // static final CREATOR
                .addField(generateCreator(processingEnv, properties, classTypeName, typeAdapters))
                // overwrites writeToParcel
                .addMethod(generateWriteToParcel(processingEnv, properties, typeAdapters));

        if (!ancestorIsParcelable(processingEnv, typeElement)) {
            // Implement android.os.Parcelable if the ancestor does not do it
            subClass.addSuperinterface(ClassName.get("android.os", "Parcelable"));
        }

        if (!typeAdapters.isEmpty()) {
            typeAdapters.values().forEach(subClass::addField);
        }

        JavaFile javaFile = JavaFile.builder(pkg, subClass.build()).build();
        return javaFile.toString();

    }
    /** -----------------------------Helper Methods for generateClass-----------------------------*/

    //----------Helper methods for generating MethodSpecs, FieldSpecs for TypeSpec.Builder  --------

    private MethodSpec generateConstructor(ImmutableList<Property> properties) {
        List<ParameterSpec> parameterSpecList = Lists.newArrayListWithCapacity(properties.size());

        for (Property property: properties) {
            parameterSpecList.add(ParameterSpec.builder(property.typeName, property.fieldName).build());
        }

        MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                .addParameters(parameterSpecList);

        for (ParameterSpec parameterSpec: parameterSpecList) {
            builder.addStatement("this.$N = $N", parameterSpec.name, parameterSpec.name);
        }

        return builder.build();
     }

    private MethodSpec generateDescribeContents() {
        return MethodSpec.methodBuilder("describeContents")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(int.class)
                .addStatement("return 0")
                .build();
    }

    private FieldSpec generateCreator(ProcessingEnvironment processingEnvironment,
                                      ImmutableList<Property> propertyImmutableList,
                                      TypeName typeName,
                                      ImmutableMap<TypeMirror, FieldSpec> typeAdapters) {

        ClassName creator = ClassName.bestGuess("android.os.Parcelable.Creator");
        TypeName creatorOfClass = ParameterizedTypeName.get(creator, typeName);

//        Types typeUtils = processingEnvironment.getTypeUtils();
        CodeBlock.Builder ctorCall = CodeBlock.builder();
        ctorCall.add("return new $T(\n", typeName);
        ctorCall.indent().indent();

        boolean requiresSuppressWarnings = false;
        for (int i = 0, n = propertyImmutableList.size(); i < n; i++) {
            Property p = propertyImmutableList.get(i);

            if (p.typeAdapter != null && typeAdapters.containsKey(p.typeAdapter)) {
                Parcelables.readValueWithTypeAdapter(ctorCall, p, typeAdapters.get(p.typeAdapter));
            } else {
                requiresSuppressWarnings |= Parcelables.isTypeRequiresSuppressWarnings(p.typeName);
                TypeName parcelableType = Parcelables.getTypeNameFromProperty(p, processingEnvironment.getTypeUtils());
                Parcelables.readValue(ctorCall, p, parcelableType);
            }

            if (i < n-1) ctorCall.add(",");
            ctorCall.add("\n");
        }

        ctorCall.unindent().unindent();
        ctorCall.add(");\n");

        MethodSpec.Builder createFromParcel = MethodSpec.methodBuilder("createFromParcel")
                .addAnnotation(Override.class);

        if (requiresSuppressWarnings) {
            createFromParcel.addAnnotation(createSuppressUncheckedWarningAnnotation());
        }

        createFromParcel.addModifiers(Modifier.PUBLIC)
                .returns(typeName)
                .addParameter(ClassName.bestGuess("android.os.Parcel"), "in");

        createFromParcel.addCode(ctorCall.build());

        TypeSpec creatorImpl = TypeSpec.anonymousClassBuilder("")
                .superclass(creatorOfClass)
                .addMethod(createFromParcel.build())
                .addMethod(MethodSpec.methodBuilder("newArray")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ArrayTypeName.of(typeName))
                        .addParameter(int.class, "size")
                        .addStatement("return new $T[size]", typeName)
                        .build())
                .build();

        return FieldSpec
                .builder(creatorOfClass, "CREATOR", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$L", creatorImpl)
                .build();

    }

    private MethodSpec generateWriteToParcel(ProcessingEnvironment processingEnvironment,
                                             ImmutableList<Property> properties,
                                             ImmutableMap<TypeMirror, FieldSpec> typeAdapters) {

        ParameterSpec dest = ParameterSpec.builder(ClassName.get("android.os", "Parcel"), "dest").build();
        ParameterSpec flags = ParameterSpec.builder(ClassName.get(int.class), "flags").build();

        MethodSpec.Builder builder = MethodSpec.methodBuilder("writeToParcel")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(dest)
                .addParameter(flags);

        for (Property p: properties) {
            if (p.typeAdapter != null && typeAdapters.containsKey(p.typeAdapter)) {
                FieldSpec typeAdapter = typeAdapters.get(p.typeAdapter);
                builder.addCode(Parcelables.writeValueWithTypeAdapter(typeAdapter, p, dest));
            } else {
                builder.addCode(Parcelables.writeValue(p, dest, flags, processingEnvironment.getTypeUtils()));
            }
        }

        return builder.build();
    }

    private static AnnotationSpec createSuppressUncheckedWarningAnnotation() {
        return AnnotationSpec.builder(SuppressWarnings.class)
                .addMember("value", "\"unchecked\"")
                .build();
    }

    //----------Helper methods for generating MethodSpecs, FieldSpecs for TypeSpec.Builder  --------

    //----------------------------------Other helper methods ---------------------------------------

    private boolean ancestorIsParcelable(ProcessingEnvironment processingEnvironment, TypeElement typeElement) {
        TypeMirror classType = typeElement.asType();
        TypeMirror parcelable = processingEnvironment.getElementUtils().getTypeElement("android.os.Parcelable").asType();
        return TypeUtil.isClassOfType(processingEnvironment.getTypeUtils(), parcelable, classType);
    }

    private ImmutableMap<TypeMirror, FieldSpec> getTypeAdapters(ImmutableList<Property> properties) {
        Map<TypeMirror, FieldSpec> typeAdapters = new LinkedHashMap<>();
        NameAllocator nameAllocator = new NameAllocator();
        nameAllocator.newName("CREATOR");

        for (Property property: properties) {
            if (property.typeAdapter != null && !typeAdapters.containsKey(property.typeAdapter)) {
                ClassName typeName = (ClassName) TypeName.get(property.typeAdapter);
                String name = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, typeName.simpleName());
                name = nameAllocator.newName(name, typeName);

                typeAdapters.put(property.typeAdapter, FieldSpec.builder(typeName,
                        NameAllocator.toJavaIdentifier(name), Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                        .initializer("new $T()", typeName)
                        .build());
            }
        }

        return ImmutableMap.copyOf(typeAdapters);

    }

    private ImmutableList<Property> buildProperties(List<VariableElement> elements) {
        ImmutableList.Builder<Property> builder = ImmutableList.builder();
        for (VariableElement element: elements) {
            builder.add(new Property(element.getSimpleName().toString(), element));
        }
        return builder.build();
    }

    private List<VariableElement> getNonPrivateFields(TypeElement typeElement) {
        List<VariableElement> allFields = ElementFilter.fieldsIn(typeElement.getEnclosedElements());
        List<VariableElement> nonPrivateFields = new ArrayList<>();

        for (VariableElement field: allFields) {
            if (!field.getModifiers().contains(Modifier.PRIVATE)) {
                nonPrivateFields.add(field);
            }
        }

        return nonPrivateFields;
    }

    /** -----------------------------Helper Methods for generateClass-----------------------------*/

}
