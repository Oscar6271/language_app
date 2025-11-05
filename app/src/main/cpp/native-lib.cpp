#include <jni.h>
#include <string>
#include <vector>
#include "main.h"

#include <android/log.h>

#define LOG_TAG "native-lib"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)


extern "C" JNIEXPORT void JNICALL
Java_com_example_ordapp_SimpleInput_writeToFile(
        JNIEnv* env,
        jobject,
        jstring fileNameFromJava,
        jstring contentToWriteFromJava,
        jboolean append) {
    const char* fileName = env->GetStringUTFChars(fileNameFromJava, nullptr);
    std::string fileNameParameter(fileName);

    const char* contentToWrite = env->GetStringUTFChars(contentToWriteFromJava, nullptr);
    std::string contentToWriteParameter(contentToWrite);

    bool appendParameter = (append == JNI_TRUE);

    // frigör minnet som har allokerats
    env->ReleaseStringUTFChars(fileNameFromJava, fileName);
    env->ReleaseStringUTFChars(contentToWriteFromJava, contentToWrite);
    writeToFile(fileNameParameter, contentToWriteParameter, appendParameter);
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

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_ordapp_Practice_compare(
        JNIEnv* env,
        jobject,
        jstring userInputJava) {
    const char* userInput = env->GetStringUTFChars(userInputJava, nullptr);
    std::string userInputParameter(userInput);

    env->ReleaseStringUTFChars(userInputJava, userInput);
    std::string response = compare(userInputParameter);

    return env->NewStringUTF(response.c_str());
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_ordapp_Practice_checkEmpty(
        JNIEnv* env,
        jobject) {
    return check_empty() ? JNI_TRUE : JNI_FALSE;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_ordapp_ChooseFileMode_printFile(
        JNIEnv* env,
        jobject,
        jstring fileNameJava) {
    const char* fileName = env->GetStringUTFChars(fileNameJava, nullptr);
    std::string fileNameParameter(fileName);

    env->ReleaseStringUTFChars(fileNameJava, fileName);
    std::string response = printFile(fileNameParameter);

    return env->NewStringUTF(response.c_str());
}