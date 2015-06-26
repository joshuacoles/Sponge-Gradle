package org.joshuacoles.common

/**
 * Created by joshuacoles on 24/05/2015.
 */
interface BiFunction<X, Y, R> {
    R call(X x, Y y)
}