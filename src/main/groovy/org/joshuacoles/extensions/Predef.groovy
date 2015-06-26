package org.joshuacoles.extensions

/**
 * Created by joshuacoles on 24/06/2015.
 */
class Predef {
    static void remove(String self, CharSequence target) {
        self.replace(target, "")
    }

    static void removeAll(String self, CharSequence target) {
        self.replaceAll(target, "")
    }
}
