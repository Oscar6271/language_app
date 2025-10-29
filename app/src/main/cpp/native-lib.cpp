#include <jni.h>
#include <string>
#include <vector>
#include "main.h"

#include <android/log.h>

#define LOG_TAG "native-lib"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)


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

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_ordapp_Practice_pickWord(
        JNIEnv* env,
        jobject
        ) {
    std::string word = pickWord();
    return env->NewStringUTF(word.c_str());
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_ordapp_Practice_compare(
        JNIEnv* env,
        jobject,
        jstring userInputJava) {
    const char* userInput = env->GetStringUTFChars(userInputJava, nullptr);
    std::string userInputParameter(userInput);

    env->ReleaseStringUTFChars(userInputJava, userInput);
    return compare(userInputParameter) ? JNI_TRUE : JNI_FALSE;
}
