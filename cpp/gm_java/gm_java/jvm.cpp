#include <windows.h>
#include <shlwapi.h>

#include "jvm.h"

HMODULE hJVM;

typedef jint (JNICALL t_CreateJavaVM)(JavaVM **pvm, void **penv, void *args);
t_CreateJavaVM *JVM_CreateJavaVM;

typedef jint (JNICALL t_GetCreatedJavaVMs)(JavaVM **vmBuf, jsize bufLen, jsize *nVMs);
t_GetCreatedJavaVMs *JVM_GetCreatedJavaVMs;

int jvm_loadlib(const char* jre_path)
{
	/* Already loaded */
	if(hJVM)
	{
		return 1;
	}

	char dll_path[MAX_PATH];
	if(!PathCombineA(dll_path, jre_path, "bin\\client\\jvm.dll"))
	{
		return 0;
	}

	hJVM = LoadLibraryA(dll_path);
	if(!hJVM)
	{
		return 0;
	}

	JVM_CreateJavaVM = (t_CreateJavaVM*)GetProcAddress(hJVM, "JNI_CreateJavaVM");
	if(!JVM_CreateJavaVM)
	{
		return 0;
	}

	JVM_GetCreatedJavaVMs = (t_GetCreatedJavaVMs*)GetProcAddress(hJVM, "JNI_GetCreatedJavaVMs");
	if(!JVM_GetCreatedJavaVMs)
	{
		return 0;
	}

	return 1;
}

int jvm_create(JavaVM **jvm, JNIEnv **env, JavaVMInitArgs *vm_args)
{
	/* Library isn't loaded */
	if(!hJVM)
	{
		return -1;
	}

	return JVM_CreateJavaVM(jvm, (void**)env, vm_args);
}

int jvm_get(JavaVM **jvm)
{
	return JVM_GetCreatedJavaVMs(jvm, 1, NULL);
}