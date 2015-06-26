package org.joshuacoles.extensions

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FirstParam
import org.codehaus.groovy.runtime.DefaultGroovyMethods
import org.joshuacoles.common.BiFunction
import org.joshuacoles.common.Function
import org.joshuacoles.extensions.support.IterableEnumeration

class CollectionsExtension {
    static <T> Iterator<T> iterator(Enumeration<T> self) { Collections.list(self).iterator() }

    static <T> Iterable<T> iterable(Enumeration<T> self) { new IterableEnumeration<T>(self) }

    static <T> List<T> asList(Enumeration<T> self) { Collections.list(self) }

    static <T> Iterable<T> each(Enumeration<T> self,
                                @ClosureParams(FirstParam.FirstGenericType.class) Closure closure) {
        DefaultGroovyMethods.each(self.iterator(), closure)
        return self.iterable()
    }

    static <T> Collection<T> union(Collection<T> self, Collection<T> x) { self + x }

    static <T> List<T> intersection(Collection<T> self, Collection<T> x) { self.intersect(x) }

    static <T, K, V> Map<K, V> map(final Collection<T> collection, Function<T, K> key, Function<T, V> value) {
        return collection.inject([:]) { LinkedHashMap<K, V> r, T c -> r[key(c)] = value(c); r }
    }

    static <U, T, K, V> Map<K, V> map(final Map<U, T> collection, BiFunction<U, T, K> key, BiFunction<U, T, V> value) {
        return collection.inject([:]) { LinkedHashMap<K, V> r, Map.Entry<U, T> c ->
            r[key(c.key, c.value)] = value(c.key, c.value); r
        }
    }

    static List collapse(Collection self) {
        def end = ((self.findAll({ it instanceof Collection }).sum() ?: []) as Collection)
                .append(self.findAll { !(it instanceof Collection) })
        return ((!end.any { it instanceof Collection }) ? end : ss(self))
    }


    static <T extends Collection> Collection append(Collection self, Collection collection) {
        self.addAll(collection)
        self
    }
}
