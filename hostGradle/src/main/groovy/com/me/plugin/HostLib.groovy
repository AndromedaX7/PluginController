package com.me.plugin

import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class HostLib implements Plugin<Project>{

    @Override
    void apply(Project project) {
        if (!project.android){
            throw new IllegalStateException('Must apply \'com.android.application\' or \'com.android.library\' first!');
        }

        project.getExtensions().findByType(BaseExtension.class)
        .registerTransform(new HostTransform(project))
    }
}

