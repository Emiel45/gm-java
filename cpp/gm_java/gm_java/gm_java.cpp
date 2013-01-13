#include <windows.h>
#include <stdio.h>
#include <io.h>
#include <fcntl.h>
#include <string.h>

#include "gm_java.h"
#include "lua_lock.h"
#include "gmod_lua.h"
#include "jvm.h"

#define MODULE_NAME "java"

lua_State *lua;

JavaVM *jvm;
JNIEnv *env;

int java_load(lua_State *L)
{
	/* 1st argument must be a table */
	luaL_checktype(L, 1, LUA_TTABLE);

	/* check jre_path */
	lua_getfield(L, 1, "jre_path");
	if(!lua_isstring(L, -1)) 
	{
		lua_pushstring(L, "Invalid jre_path, string expected.");
		lua_error(L);
	}

	/* load jvm library */
	const char* jre_path = lua_tostring(L, -1);
	if(!jvm_loadlib(jre_path))
	{
		lua_pushstring(L, "Couldn't load JVM library.");
		lua_error(L);
	}

	/* get existing jvm */
	if(jvm_get(&jvm) != JNI_OK)
	{
		lua_pushstring(L, "Couldn't check for existing JVMs.");
		lua_error(L);
	}

	if(jvm)
	{
		lua_getglobal(L, "print");
		lua_pushstring(L, "Loaded existing JVM.");
		lua_call(L, 1, 0);

		return 0;
	}

	/* check options */
	lua_getfield(L, 1, "options");
	if(!lua_istable(L, -1))
	{
		lua_pushstring(L, "Invalid options, table expected.");
		lua_error(L);
	}

	/* set options */
	int options_len = lua_objlen(L, -1);
	JavaVMOption* options = new JavaVMOption[options_len];

	for(int i = 0; i < options_len; i++)
	{
		lua_rawgeti(L, -1, (i + 1));
		{
			if(lua_isstring(L, -1))
			{
				const char* value = lua_tostring(L, -1);

				char* storedValue = new char[512];
				strcpy(storedValue, value);

				options[i].optionString = storedValue;
			}
		}
		lua_pop(L, 1);
	}

	JavaVMInitArgs vm_args;
	vm_args.version = JNI_VERSION_1_6;
	vm_args.nOptions = options_len;
	vm_args.options = options;
	vm_args.ignoreUnrecognized = JNI_FALSE;
	
	/* create jvm */
	int res = jvm_create(&jvm, &env, &vm_args);

	/* cleanup options */
	for(int i = 0; i < options_len; i++)
	{
		delete options[i].optionString;
	}
	delete options;

	if(res != JNI_OK)
	{
		lua_pushstring(L, "Couldn't create JVM.");
		lua_error(L);
	}

	/* no results */
	return 0;
}

int java_invoke_main(lua_State *L)
{
	/* 1st argument must be a string */
	luaL_checkstring(L, 1);
	const char* class_name = lua_tostring(L, 1);

	/* check if jvm is loaded */
	if(!jvm)
	{
		lua_pushstring(L, "Java not loaded.");
		lua_error(L);
	}

	/* look up the class */
	jclass cls = env->FindClass(class_name);
	if(!cls)
	{
		lua_pushstring(L, "Class not found.");
		lua_error(L);
	}
	
	/* look up the method */
	jmethodID method = env->GetStaticMethodID(cls, "main", "([Ljava/lang/String;)V");
	if(!method)
	{
		lua_pushstring(L, "Main method not found.");
		lua_error(L);
	}
	
	/* if first call */
	if(!lua_haslock(L))
	{
		/* lock our lua_state */
		lua_createlock(L);
		lua_lock(L);

		/* init gmod bindings */
		lua_pushcfunction(L, gmod_lua_init);
		lua_call(L, 0, 0);
	}

	/* invoke the method */
	env->CallStaticVoidMethod(cls, method);

	/* handle exception */
	if(env->ExceptionCheck())
	{
		env->ExceptionDescribe();
		env->ExceptionClear();
	}

	/* no results */
	return 0;
}

static const luaL_Reg module_methods[] = 
{
	{"load", java_load},
	{"invoke_main", java_invoke_main},

	{NULL, NULL}
};

void create_console()
{
	AllocConsole();

    HANDLE handle_out = GetStdHandle(STD_OUTPUT_HANDLE);
    int hCrt = _open_osfhandle((long) handle_out, _O_TEXT);
    FILE* hf_out = _fdopen(hCrt, "w");
    setvbuf(hf_out, NULL, _IONBF, 1);
    *stdout = *hf_out;

    HANDLE handle_in = GetStdHandle(STD_INPUT_HANDLE);
    hCrt = _open_osfhandle((long) handle_in, _O_TEXT);
    FILE* hf_in = _fdopen(hCrt, "r");
    setvbuf(hf_in, NULL, _IONBF, 128);
    *stdin = *hf_in;
}

GMOD_MODULE_OPEN()
{
	/* Allocate a debug console */
	create_console();

	/* Store lua state */
	lua = L;

	/* Register java module */
	luaL_register(L, MODULE_NAME, module_methods);

	/* Push java module to stack */
	lua_getglobal(L, MODULE_NAME);

	/* foo = require("java") */
	return 1;
}

GMOD_MODULE_CLOSE()
{

	/* no results */
	return 0;
}

lua_State *get_lua()
{
	return lua;
}

JavaVM *get_jvm()
{
	return jvm;
}