# ColorBlindAssistant Application 

## Step by Step Implementation

Find the appropriate android project according to the requirement in GitHub. You may also go through this link to find a project on https://github.com/Ging-Wpp/FinalProject - automatic!
[GitHub](http://github.com) So for demonstration purposes let’s clone this project in this article. 

After redirect to the above web page click on the green-colored **Code** button then copy the **hyperlink** as shown in the below image. You may copy the link manually or by just click on the Copy icon. 

![image](https://user-images.githubusercontent.com/84076128/121774023-0f822b80-cbaa-11eb-97c1-50fc83e4d24b.png)

**Step 1:** Open your **Android Studio** then go to the **File > New > Project from Version Control** as shown in the below image. 

  ![image](https://user-images.githubusercontent.com/84076128/121774159-22e1c680-cbab-11eb-894b-ad63f995e5aa.png)

**Step 2:** After clicking on the **Project from Version Control** a pop-up screen will arise like below. In the **Version control** choose **Git** from the drop-down menu. 

  ![image](https://user-images.githubusercontent.com/84076128/121774384-77d20c80-cbac-11eb-9918-45cee75cdc8d.png)

**Step 3:** Then at last **paste the link in the URL** and choose your **Directory.** Click on the **Clone** button and you are done.
  
  ![image](https://user-images.githubusercontent.com/84076128/121774431-9f28d980-cbac-11eb-8c60-e809b8c358e7.png)

## Build an Application
  Get the APK link here: https://drive.google.com/file/d/1G5PJrsbAHZilk5hsDjJfTUw5zRFz3lRc/view?usp=sharing
  
  ![10_pkl-r2 แอปพลิเคชันช่วยเหลือคนตาบอดสี](https://user-images.githubusercontent.com/84076128/121774998-32630e80-cbaf-11eb-8be0-19cd246d11bc.png)
  
## Directory Structure
```bash
  FinalProject-master
    ├───.idea
    ├───app
    │   └───src
    │       ├───androidTest
    │       │   └───java
    │       │       └───com
    │       │           └───example
    │       │               └───finalproject
    │       ├───main
    │       │   ├───java
    │       │   │   └───com
    │       │   │       └───example
    │       │   │           └───finalproject
    │       │   ├───jniLibs
    │       │   │   ├───arm64-v8a
    │       │   │   ├───armeabi
    │       │   │   ├───armeabi-v7a
    │       │   │   ├───mips
    │       │   │   ├───mips64
    │       │   │   ├───x86
    │       │   │   └───x86_64
    │       │   └───res
    │       │       ├───drawable
    │       │       ├───drawable-v21
    │       │       ├───font
    │       │       ├───layout
    │       │       ├───mipmap-anydpi-v26
    │       │       ├───mipmap-hdpi
    │       │       ├───mipmap-mdpi
    │       │       ├───mipmap-xhdpi
    │       │       ├───mipmap-xxhdpi
    │       │       ├───mipmap-xxxhdpi
    │       │       ├───values
    │       │       └───values-night
    │       └───test
    │           └───java
    │               └───com
    │                   └───example
    │                       └───finalproject
    ├───gradle
    │   └───wrapper
    └───openCVLibrary343
        ├───build
        │   ├───generated
        │   │   ├───aidl_source_output_dir
        │   │   │   └───debug
        │   │   │       └───out
        │   │   │           └───org
        │   │   │               └───opencv
        │   │   │                   └───engine
        │   │   └───source
        │   │       └───buildConfig
        │   │           └───debug
        │   │               └───org
        │   │                   └───opencv
        │   ├───intermediates
        │   │   ├───aapt_friendly_merged_manifests
        │   │   │   └───debug
        │   │   │       └───aapt
        │   │   ├───aar_metadata
        │   │   │   └───debug
        │   │   ├───annotation_processor_list
        │   │   │   └───debug
        │   │   ├───compile_library_classes_jar
        │   │   │   └───debug
        │   │   ├───compile_r_class_jar
        │   │   │   └───debug
        │   │   ├───compile_symbol_list
        │   │   │   └───debug
        │   │   ├───incremental
        │   │   │   ├───mergeDebugJniLibFolders
        │   │   │   ├───mergeDebugShaders
        │   │   │   ├───packageDebugAssets
        │   │   │   └───packageDebugResources
        │   │   │       └───merged.dir
        │   │   │           └───values
        │   │   ├───javac
        │   │   │   └───debug
        │   │   │       └───classes
        │   │   │           └───org
        │   │   │               └───opencv
        │   │   │                   ├───android
        │   │   │                   ├───calib3d
        │   │   │                   ├───core
        │   │   │                   ├───dnn
        │   │   │                   ├───engine
        │   │   │                   ├───features2d
        │   │   │                   ├───imgcodecs
        │   │   │                   ├───imgproc
        │   │   │                   ├───ml
        │   │   │                   ├───objdetect
        │   │   │                   ├───osgi
        │   │   │                   ├───photo
        │   │   │                   ├───utils
        │   │   │                   ├───video
        │   │   │                   └───videoio
        │   │   ├───local_only_symbol_list
        │   │   │   └───debug
        │   │   ├───manifest_merge_blame_file
        │   │   │   └───debug
        │   │   ├───merged_manifest
        │   │   │   └───debug
        │   │   ├───navigation_json
        │   │   │   └───debug
        │   │   ├───packaged_manifests
        │   │   │   └───debug
        │   │   ├───packaged_res
        │   │   │   └───debug
        │   │   │       └───values
        │   │   └───symbol_list_with_package_name
        │   │       └───debug
        │   ├───outputs
        │   │   └───logs
        │   └───tmp
        │       └───compileDebugJavaWithJavac
        └───src
            └───main
                ├───aidl
                │   └───org
                │       └───opencv
                │           └───engine
                ├───java
                │   └───org
                │       └───opencv
                │           ├───android
                │           ├───calib3d
                │           ├───core
                │           ├───dnn
                │           ├───features2d
                │           ├───imgcodecs
                │           ├───imgproc
                │           ├───ml
                │           ├───objdetect
                │           ├───osgi
                │           ├───photo
                │           ├───utils
                │           ├───video
                │           └───videoio
                └───res
                    ├───mipmap-anydpi-v26
                    ├───mipmap-hdpi
                    ├───mipmap-mdpi
                    ├───mipmap-xhdpi
                    ├───mipmap-xxhdpi
                    ├───mipmap-xxxhdpi
                    └───values
```
