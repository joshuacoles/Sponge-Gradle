package org.joshuacoles.extensions.support

class IterableEnumeration<T> implements Iterable<T> {
    private Enumeration<T> enumeration

    IterableEnumeration(Enumeration<T> enumeration) {
        this.enumeration = enumeration
    }

    @Override
    Iterator<T> iterator() { enumeration.iterator() }
}
