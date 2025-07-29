# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Regras para ignorar avisos de classes opcionais do OkHttp e suas dependências
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn org.bouncycastle.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**

# Regras do Dynatrace OpenKit para não quebrar o SDK com ofuscação
-keep class com.dynatrace.openkit.** { *; }
-keep class com.dynatrace.openkit.api.** { *; }
-keep class com.dynatrace.openkit.protocol.** { *; }
-keep class com.dynatrace.openkit.core.** { *; }
-keep class com.dynatrace.openkit.core.caching.** { *; }
-keep class com.dynatrace.openkit.core.configuration.** { *; }
-keep class com.dynatrace.openkit.core.objects.** { *; }
-keep class com.dynatrace.openkit.providers.** { *; }
-keep class com.dynatrace.openkit.util.json.** { *; }