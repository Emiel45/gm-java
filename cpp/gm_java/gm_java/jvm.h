#ifndef JVM_H
#define JVM_H

#include <jni.h>

int jvm_loadlib(const char* jre_path);

int jvm_create(JavaVM **jvm, JNIEnv **env, JavaVMInitArgs *vm_args);
int jvm_get(JavaVM **jvm);

#endif // JVM_H