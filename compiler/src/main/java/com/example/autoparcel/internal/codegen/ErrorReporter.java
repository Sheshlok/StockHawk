package com.example.autoparcel.internal.codegen;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

/**
 * Created by sheshloksamal on 19/07/16.
 * Handle Error Reporting for an annotation processor
 *
 * @author Éamonn McManus
 */
class ErrorReporter {
    /**
     * A {@code Messager} provides the way for an annotation processor to
     * report error messages, warnings, and other notices.
     */
    private final Messager messager;

    ErrorReporter(ProcessingEnvironment processingEnv) {
        this.messager = processingEnv.getMessager();
    }

    /**
     * Issue a compilation note.
     *
     * @param msg the text of the note
     * @param e   the element to which it pertains
     */
    void reportNote(String msg, Element e) {
        messager.printMessage(Diagnostic.Kind.NOTE, msg, e);
    }

    /**
     * Issue a compilation warning.
     *
     * @param msg the text of the warning
     * @param e   the element to which it pertains
     */
    void reportWarning(String msg, Element e) {
        messager.printMessage(Diagnostic.Kind.WARNING, msg, e);
    }

    /**
     * Issue a compilation error. This method does not throw an exception, since we want to continue
     * processing and perhaps report other errors. It is a good idea to introduce a test case in
     * CompilationTest for any new call to reportError(...) to ensure that we continue correctly after
     * an error.
     *
     * @param msg the text of the warning
     * @param e   the element to which it pertains
     */
    void reportError(String msg, Element e) {
        messager.printMessage(Diagnostic.Kind.ERROR, msg, e);
    }

    /**
     * Issue a compilation error and abandon the processing of this class. This does not prevent the
     * processing of other classes.
     *
     * @param msg the text of the error
     * @param e   the element to which it pertains
     */
    void abortWithError(String msg, Element e) {
        reportError(msg, e);
        throw new AbortProcessingException();
    }
}

