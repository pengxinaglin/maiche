-injars bin\o2osdk_lib.jar
-outjars libs\o2osdk-lib.jar

-libraryjars libs\dspread_android_sdk_2.1.8_02.jar
-libraryjars libs\httpmime-4.1.1.jar
-libraryjars libs\iBridge.jar
-libraryjars libs\JBIG.jar
-libraryjars libs\reader_landicorp_1.1.5.jar
-libraryjars libs\yintong_o2o.jar
-libraryjars 'D:\android-sdk\platforms\android-8\android.jar'



-dontoptimize
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontpreverify
-verbose
-dontwarn




-keep class android.support.v4.** {
    <fields>;
    <methods>;
}

-keep interface  android.support.v4.app.** {
    <fields>;
    <methods>;
}

-keep public class * extends android.support.v4.**

-keep public class * extends android.app.Fragment

-keep public class * extends android.app.Application

-keep public class * extends android.app.Activity

-keep public class * extends android.app.Service


-keep public class com.dspread.xpos.** {
    <fields>;
    <methods>;
}
-keep public class com.landicorp.**{
	<fields>;
	<methods>;
}


-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}