#include <jni.h>
#include <string>
#include "main.h"

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

extern "C" JNIEXPORT void JNICALL
Java_com_example_ordapp_Practice_readFile(
        JNIEnv* env,
        jobject /* this */,
        jstring fileNameJava,
        jstring languageWriteJava) {
    const char* fileName = env->GetStringUTFChars(fileNameJava, nullptr);
    std::string fileNameParameter(fileName);

    const char* language_to_write_in = env->GetStringUTFChars(languageWriteJava, nullptr);
    std::string languageParameter(language_to_write_in);

    // frigör minnet som har allokerats
    env->ReleaseStringUTFChars(fileNameJava, fileName);
    env->ReleaseStringUTFChars(languageWriteJava, language_to_write_in);
    readFile(fileNameParameter, languageParameter);
}
