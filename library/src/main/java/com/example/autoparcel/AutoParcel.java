package com.example.autoparcel;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Tells the framework that our annotation can only be used in classes */
@Target(ElementType.TYPE)

/** Tells the framework that the annotation is to be discarded by the compiler after it is processed
 * by our custom annotation processor */
@Retention(RetentionPolicy.SOURCE)

public @interface AutoParcel {
}
