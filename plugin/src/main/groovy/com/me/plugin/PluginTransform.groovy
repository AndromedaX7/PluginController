package com.me.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import javassist.ClassPool
import javassist.CtConstructor
import javassist.CtMember
import javassist.CtMethod
import org.gradle.api.Project

class PluginTransform extends Transform {
    ClassPool classPool = ClassPool.default
    private Project project

    PluginTransform(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return "modify plugin context"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        def set = new HashSet<QualifiedContent.ContentType>()
        set.add(QualifiedContent.DefaultContentType.CLASSES)
        return set
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return true
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        classPool.appendClassPath(project.android.bootClasspath[0].toString())
        for (path in project.android.bootClasspath)
            println("BootClasspath" + path)
        transformInvocation.inputs.each { TransformInput input ->

            input.jarInputs.each { JarInput jar ->
                println(jar.file.absolutePath)
//                if (jar.name.startsWith("androidx.appcompat:appcompat:")) {
//                    classPool.appendClassPath(jar.file.absolutePath)
//                }
//                if (jar.name.startsWith("android.local.jars:pluginlib")) {
//                    classPool.appendClassPath(jar.file.absolutePath)
//                }
//                if (jar.name.startsWith("org.jetbrains.kotlin:kotlin-stdlib:")) {
//                    classPool.appendClassPath(jar.file.absolutePath)
//                }
//
//                if (jar.name.startsWith("org.jetbrains.kotlin:kotlin-stdlib-common:")) {
//                    classPool.appendClassPath(jar.file.absolutePath)
//                }
//                if (jar.name.startsWith("org.jetbrains.kotlin:kotlin-android-extensions-runtime:")) {
//                    classPool.appendClassPath(jar.file.absolutePath)
//                }
//                if (jar.name.startsWith(" org.jetbrains.kotlin:kotlin-stdlib-jdk7:")) {
//                    classPool.appendClassPath(jar.file.absolutePath)
//                }
//                if (jar.name.startsWith(" org.jetbrains.kotlin:kotlin-stdlib-jdk7:")) {
//                    classPool.appendClassPath(jar.file.absolutePath)
//                }
                classPool.insertClassPath(jar.file.absolutePath)
                def jarName = jar.name
                if (jarName.endsWith('.jar')) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }

                def dest = transformInvocation.outputProvider.getContentLocation(jarName + "-trans", jar.contentTypes, jar.scopes, Format.JAR)
                if (jar.file.exists())
                    FileUtils.copyFile(jar.file, dest)
            }
            input.directoryInputs.each { DirectoryInput dinput ->
                if (dinput.file.isDirectory()) {
                    classPool.insertClassPath(dinput.file.getAbsolutePath())
                    loadLast(dinput.file.absolutePath, dinput.file)
                }

                def dest = transformInvocation.outputProvider.getContentLocation(dinput.name, dinput.contentTypes, dinput.scopes, Format.DIRECTORY)

                FileUtils.copyDirectory(dinput.file, dest)
            }
        }

    }


    void loadLast(String parent, File file) {
        if (file.isDirectory()) {
            for (f in file.listFiles()) {
                loadLast(parent, f)
            }
        } else if (file.isFile() && file.name.endsWith(".class") && !file.name.contains("R\$") && !file.name.equals("R.class")) {
            println("===========================")
            println(parent)
            if (file.absolutePath.endsWith(".class")) {
                String fn = file.absolutePath.substring(parent.length() + 1)
                fn = fn.substring(0, fn.length() - 6).replace(File.separator, ".")
                println(fn)


                def clazz = classPool.getCtClass(fn)
                if ("com.me.pluginlib.activity.PluginAppCompatActivity" == clazz.name || "com.me.pluginlib.receiver.PluginReceiver" == clazz.name || clazz.name == "com.me.pluginlib.service.PluginService") {
                    println("lib class :" + clazz.name)
                    return
                }

                def superclass = clazz.getSuperclass()
                println("current class:" + clazz.name + "   superClass:" + superclass.name)
                if (superclass.name == "androidx.appcompat.app.AppCompatActivity") {
                    def find = classPool.getCtClass("com.me.pluginlib.activity.PluginAppCompatActivity")
                    clazz.setSuperclass(find)
                    def bytecode = clazz.toBytecode()
                    FileOutputStream out = new FileOutputStream(file)
                    out.write(bytecode)
                    out.flush()
                    out.close()
                    println("modify activity:" + clazz.name)
                    println clazz.superclass.name

                } else if (superclass.name == "android.content.BroadcastReceiver") {
                    def find = classPool.getCtClass("com.me.pluginlib.receiver.PluginReceiver")
                    clazz.setSuperclass(find)
                    println("modify receiver:" + clazz.name)
//
                    CtMethod method = clazz.getDeclaredMethod("onReceive", classPool.getCtClass("android.content.Context"), classPool.getCtClass("android.content.Intent"))
                    method.setName("_onReceive")
//                    CtMethod myContext = CtMethod.make("private android.content.Context myContext(android.content.Context ctx){" +
//                            "if(com.me.pluginlib.PluginManager.sApplicationContext!=null){" +
//                            "   return  com.me.pluginlib.PluginManager.sApplicationContext;" +
//                            "}" +
//                            "return  ctx;" +
//                            "}", clazz)
//                    clazz.addMethod(myContext)
//                    CtMethod onReceive = CtMethod.make(
//                            "public void onReceive(android.content.Context ctx,android.content.Intent intent){" +
//                                    "   _onReceive(myContext(ctx),intent);" +
//                                    "}", clazz)
//
//
//                    clazz.addMethod(onReceive)

                    def bytecode = clazz.toBytecode()
                    FileOutputStream out = new FileOutputStream(file)
                    out.write(bytecode)
                    out.flush()
                    out.close()
                } else if (superclass.name == "android.app.Service") {
                    def find = classPool.getCtClass("com.me.pluginlib.service.PluginService")
                    clazz.setSuperclass(find)
                    println("modify service:" + clazz.name)
                    def bytecode = clazz.toBytecode()
                    FileOutputStream out = new FileOutputStream(file)
                    out.write(bytecode)
                    out.flush()
                    out.close()
                }

            }
//                ClassParser cc =new ClassParser(file)
//                cc.parse()
            println("===========================")
        }
    }
}