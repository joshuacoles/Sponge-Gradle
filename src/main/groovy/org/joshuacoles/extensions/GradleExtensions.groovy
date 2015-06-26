package org.joshuacoles.extensions

import org.gradle.api.Project

/**
 * Created by joshuacoles on 28/05/2015.
 */
class GradleExtensions {
    static void remake(Project self, Object... things) {
        things.each(self.&delete)
        things.each(self.&mkdir)
    }
}
