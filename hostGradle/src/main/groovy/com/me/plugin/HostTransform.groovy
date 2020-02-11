package com.me.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.gradle.api.Project

class HostTransform extends Transform {
    private Project project

    HostTransform(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return "modify android manifest"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        def set = new HashSet<QualifiedContent.ContentType>()
        set.add(QualifiedContent.DefaultContentType.RESOURCES)
        return set
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        transformInvocation.inputs.each {
            TransformInput inputs ->

                input.jarInputs.each { JarInput jar ->
                    println(jar.file.absolutePath)
                    def jarName = jar.name
                    if (jarName.endsWith('.jar')) {
                        jarName = jarName.substring(0, jarName.length() - 4)
                    }

                    def dest = transformInvocation.outputProvider.getContentLocation(jarName + "-trans", jar.contentTypes, jar.scopes, Format.JAR)
                    if (jar.file.exists())
                        FileUtils.copyFile(jar.file, dest)
                }
                input.directoryInputs.each { DirectoryInput dinput ->
//                    if (dinput.file.isDirectory()) {
//                        classPool.insertClassPath(dinput.file.getAbsolutePath())
//                        loadLast(dinput.file.absolutePath, dinput.file)
//                    }

                    def dest = transformInvocation.outputProvider.getContentLocation(dinput.name, dinput.contentTypes, dinput.scopes, Format.DIRECTORY)
                    FileUtils.copyDirectory(dinput.file, dest)
                }
        }
    }
}