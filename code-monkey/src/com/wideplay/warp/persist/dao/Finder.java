package com.wideplay.warp.persist.dao;

/**
 * Created with IntelliJ IDEA.
 * On: 3/06/2007
 *
 * @author Dhanji R. Prasanna
 * @since 1.0
 */
public @interface Finder {
    String name() default "";
    String query() default "";
}
