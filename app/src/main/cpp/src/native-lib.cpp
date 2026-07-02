#include <jni.h>
#include <string>
#include <vector>
#include "../headers/setup.h"
#include "../headers/library.h"

#include <android/log.h>

#define LOG_TAG "native-lib"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT void JNICALL
Java_com_example_ordapp_Library_writeToFile(
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

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_ordapp_Library_printFile(
        JNIEnv* env,
        jobject,
        jstring fileNameJava) {
    const char* fileName = env->GetStringUTFChars(fileNameJava, nullptr);
    std::string fileNameParameter(fileName);

    env->ReleaseStringUTFChars(fileNameJava, fileName);
    std::string response = printFile(fileNameParameter);

    return env->NewStringUTF(response.c_str());
}

extern "C" JNIEXPORT int JNICALL
Java_com_example_ordapp_Library_readFile(
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
    return readFile(fileNameParameter, languageParameter);
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_ordapp_Library_pickWord(
        JNIEnv* env,
        jobject) {
    std::string word = pickWord();
    return env->NewStringUTF(word.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_ordapp_Library_compare(
        JNIEnv* env,
        jobject,
        jstring userInputJava) {
    const char* userInput = env->GetStringUTFChars(userInputJava, nullptr);
    std::string userInputParameter(userInput);

    env->ReleaseStringUTFChars(userInputJava, userInput);
    std::string response = compare(userInputParameter);

    return env->NewStringUTF(response.c_str());
}

extern "C" JNIEXPORT int JNICALL
Java_com_example_ordapp_Library_checkEmpty(
        JNIEnv* env,
        jobject) {
    return check_empty();
}

extern "C" JNIEXPORT int JNICALL
Java_com_example_ordapp_Library_checkSize(
        JNIEnv* env,
        jobject) {
    return check_size();
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_ordapp_Library_addAlternative(
        JNIEnv* env,
        jobject,
        jstring newAlternative) {
    const char* userInput = env->GetStringUTFChars(newAlternative, nullptr);
    std::string userInputParameter(userInput);

    env->ReleaseStringUTFChars(newAlternative, userInput);
    addAlternative(userInputParameter);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_ordapp_Library_rewriteFile(
        JNIEnv* env,
        jobject,
        jstring fileName) {
    const char* file_name = env->GetStringUTFChars(fileName, nullptr);
    std::string file_name_parameter(file_name);

    env->ReleaseStringUTFChars(fileName, file_name);
    return rewriteFile(file_name_parameter) ? JNI_TRUE : JNI_FALSE;
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_ordapp_Library_clean_1wrong_1lists(
        JNIEnv* env,
        jobject) {
    clean_wrong_lists();
}

extern "C" JNIEXPORT int JNICALL
Java_com_example_ordapp_Library_wordsLeft(
        JNIEnv* env,
        jobject) {
    return wordsLeft();
}