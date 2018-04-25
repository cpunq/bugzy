# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/msharma/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

-dontwarn com.squareup.okhttp.**
-dontwarn okio.**
-dontwarn retrofit.**
-dontwarn rx.**
-dontwarn com.google.**

-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

# Preserve the line number information for
# debugging stack traces.
-keepattributes LineNumberTable,SourceFile


# For AboutLibraries proper functioning
-keep class .R
-keep class **.R$* {
    <fields>;
}
#-keepclasseswithmembers class **.R$* {
#    public static final int define_*;
#}

# keeping model classes
-keep class in.bugzy.data.model.** { <fields>; }
-keep class in.bugzy.data.remote.model.** { <fields>; }


-keep class com.google.gson.examples.android.model.** { *; }
-keep class com.hootsuite.nachos.** { *; }

-keep class com.flipboard.bottomsheet.*


# Retain generated class which implement Unbinder.
-keep public class * implements butterknife.Unbinder { public <init>(**, android.view.View); }

# Prevent obfuscation of types which use ButterKnife annotations since the simple name
# is used to reflectively look up the generated ViewBinding.
-keep class butterknife.*
-keepclasseswithmembernames class * { @butterknife.* <methods>; }
-keepclasseswithmembernames class * { @butterknife.* <fields>; }


##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }


# Enums to prevent Gson to throw unnecessary errors
-keepclassmembers enum * { *; }


# Keep the onEvent function to be used by EventBus
-keepclassmembers class ** {
    public void onEvent*(***);
}

##---------------End: proguard configuration for Gson  ----------