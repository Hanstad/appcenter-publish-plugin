package com.ins.gradle.plugin.android.appcenter

import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.dsl.ProductFlavor
import com.android.build.gradle.internal.tasks.featuresplit.getVariant
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.logging.Logging
import java.io.File

class AppCenterPlugin : Plugin<Project> {


    val _log = Logging.getLogger(AppCenterPlugin::class.java.simpleName)

    override fun apply(project: Project) {

        project.plugins.withType(AppCenterPlugin::class.java) {
            applyInternal(project)
        }
    }

    private fun applyInternal(project: Project) {

        val android = project.extensions.findByName("android") as AppExtension

        val extension =  project.extensions.create(APP_CENTER_EXTENSION, AppCenterPluginExtension::class.java, project)

        extension.productFlavors =  project.container(FlavorExtension::class.java)

        val uploadAllVariant = project.tasks.create("uploadAppCenter") // all apk upload task

        android.applicationVariants.whenObjectAdded { appVariant ->
            val variantName = appVariant.name.capitalize()

            _log.quiet("Variant {}, signing ready  {}", appVariant.name, appVariant.buildType.signingConfig.isSigningReady)
            if(extension.defaultConfig == null) throw GradleException("Default config is undefined")

            val extensionForVariant =   getConfigForVariant(variantName, extension, android)
            _log.quiet("App Center extension for {}  is : ", variantName)
            _log.quiet("{}", extensionForVariant.toString())

            val uploadVariant = project.tasks.create("upload${variantName}AppCenter", UploadVariant::class.java) { it ->
                it.variant = appVariant
            }

            if(uploadVariant.inputApks.size == 1) {
                uploadVariant.inputApks.forEach { file ->
                    val apkToCenterTask =getFileTask(project, file, extensionForVariant, uploadVariant.name)
                    uploadVariant.dependsOn(apkToCenterTask)
                }
            }
            else {

                val filteredFiles = uploadVariant.inputApks.filter { file -> file.nameWithoutExtension.contains("universal") }

                if(filteredFiles.isNotEmpty()) {
                    val apkToCenterTask =getFileTask(project, filteredFiles[0], extensionForVariant, uploadVariant.name)
                    uploadVariant.dependsOn(apkToCenterTask)
                }
            }
            // upload task depends on assemble
            try {
                appVariant.assembleProvider
            } catch (e: NoSuchMethodError) {
                @Suppress("DEPRECATION")
                appVariant.assemble
            }?.let { uploadVariant.dependsOn(it) }
                    ?: _log.warn("Assemble task not found. Publishing APKs may not work.")

            uploadAllVariant.dependsOn(uploadVariant)



        }
    }


    fun getFileTask(project : Project, file : File, extension: FlavorExtension, variantName : String) : Task {
        return project.tasks
                .create("upload${file.nameWithoutExtension.replace("-", "")}ApkAppCenter",
                        AppCenterUploadTask::class.java) {
                    it.artifact = file
                    it.extension = extension

                    _log.quiet("Uploading variant {} with file  {}  ", variantName, it.artifact.absolutePath)
                }

    }

    /**
     * Get all flavors associated to variant (one or several dimensions)
     */
    fun getFlavors(variantName : String, android: AppExtension) : List<ProductFlavor>{
        return ArrayList<ProductFlavor>(android.productFlavors.filter { rF ->
            variantName.toLowerCase().contains(rF.name.toLowerCase())
        })
    }

    /**
     * Get App Center Flavor Extension by name
     */
    fun getExtensionByFlavorName(flavorName : String, extension : AppCenterPluginExtension) : FlavorExtension?{
        return extension.productFlavors?.findByName(flavorName)
    }


    /**
     * Get Flavor Extension for a given variant name
     */
    fun getConfigForVariant(variantName : String, appCenterExtension : AppCenterPluginExtension, android: AppExtension) : FlavorExtension{

        if(appCenterExtension.productFlavors?.isEmpty() != false) return appCenterExtension.defaultConfig!!


        var resultExtension : FlavorExtension =  appCenterExtension.defaultConfig!!
        // ordering flavor by dimension
        val orderBy = android.flavorDimensionList.withIndex().associate {
            it.value to it.index
        }

        val sortedFlavorsByDim = getFlavors(variantName, android).sortedBy {
            orderBy[it.dimension]
        }
        sortedFlavorsByDim.forEach { flavor ->

            val flavorExtension = getExtensionByFlavorName(flavor.name, appCenterExtension )
            flavorExtension?.let { fe ->
                resultExtension = fe.mergeWith(resultExtension!!)
            }
        }
        return resultExtension
    }
}

