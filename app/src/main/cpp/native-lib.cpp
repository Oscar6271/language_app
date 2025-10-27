#include <jni.h>
#include <string>
#include "main.h"

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_ordapp_Practice_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_ordapp_SimpleInput_writeToFile(
        JNIEnv* env,
        jobject,
        jstring fileNameFromJava,
        jstring contentToWriteFromJava
        ) {
    const char* fileName = env->GetStringUTFChars(fileNameFromJava, nullptr);
    std::string fileNameParameter(fileName);

    const char* contentToWrite = env->GetStringUTFChars(contentToWriteFromJava, nullptr);
    std::string contentToWriteParameter(contentToWrite);

    // frigör minnet som har allokerats
    env->ReleaseStringUTFChars(fileNameFromJava, fileName);
    env->ReleaseStringUTFChars(contentToWriteFromJava, contentToWrite);

    return writeToFile(fileNameParameter, contentToWriteParameter) ? JNI_TRUE : JNI_FALSE;
}
