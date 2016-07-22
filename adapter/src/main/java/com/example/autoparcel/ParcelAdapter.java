package com.example.autoparcel;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by sheshloksamal on 19/07/16.
 * An annotation that indicates the auto-parcel {@link ParcelTypeAdapter} to use to parcel and
 * unparcel the filed. The value must be set to a valid {@link ParcelTypeAdapter} class.
 *
 * <pre>
 *     <code>
 *         {@literal @} AutoParcel public abstract class Foo {
 *             {@literal @} ParcelAdapter(DateTypeAdapter.class) public abstract Date date;
 *         }
 *     </code>
 * </pre>
 *
 * The generated code will instantiate and use the {@code DateTypeAdapter}  class to parcel and
 * unparcel the {@code date()} property. In order for the generated code to instantiate the
 * {@link ParcelTypeAdapter}, it needs a public no-arg constructor.
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface ParcelAdapter {
    Class<? extends ParcelTypeAdapter<?>> value();
}
