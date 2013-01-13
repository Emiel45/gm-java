#ifndef GM_JAVA_H
#define GM_JAVA_H

#include <glua.h>
#include <jni.h>

lua_State *get_lua();
JavaVM *get_jvm();

extern JNIEnv* env;

#endif // GM_JAVA_H