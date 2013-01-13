#include <jni.h>
#include <stdlib.h> // debug

#include "gm_java.h"
#include "lua_lock.h"

#define JOBJECT_MT "java.object"

#ifdef __cplusplus
#define EXPORT extern "C" JNIEXPORT
#else
#define EXPORT JNIEXPORT
#endif

lua_State *L;

jclass class_lua_Function;
jmethodID method_lua_Function_invoke;

int jobject__gc(lua_State *lua)
{
	jobject obj = *(jobject *) lua_touserdata(L, 1);
	env->DeleteGlobalRef(obj);

	return 0;
}

void jobject_push(lua_State *lua, JNIEnv *env, jobject obj)
{
	jobject *user_data = (jobject*) lua_newuserdata(L, sizeof(jobject));
	*user_data = env->NewGlobalRef(obj);

	luaL_getmetatable(L, JOBJECT_MT);
	lua_setmetatable(L, -2);
}

jclass jclass_find_global(const char *name)
{
	jclass cls = env->FindClass(name);
	return (jclass)env->NewGlobalRef(cls);
}

int java_process_async(lua_State *lua)
{
	/* Unlock our lock and allow another thread to take it */
	lua_unlock(L);

	/* Wait for other thread to finish */
	lua_lock(L);

	return 0;
}

int gmod_lua_init(lua_State *lua)
{
	/* Store lua state */
	L = lua;

	/* Create jobject metatable */
	if(luaL_newmetatable(L, JOBJECT_MT))
	{
		lua_pushnil(L);
		lua_setfield(L, -2, "__metatable");

		lua_pushcfunction(L, jobject__gc);
		lua_setfield(L, -2, "__gc");
	}

	/* Lookup classes & methods */
	class_lua_Function = jclass_find_global("gmod/Lua$Function");
	method_lua_Function_invoke = env->GetMethodID(class_lua_Function, "invoke", "()I");

	/* Register process async hook */
	lua_getglobal(L, "hook");
	lua_getfield(L, -1, "Add");
	lua_pushstring(L, "Think");
	lua_pushstring(L, "java.process_async");
	lua_pushcfunction(L, java_process_async);
	lua_call(L, 3, 0);

	return 0;
}

EXPORT void JNICALL Java_gmod_Lua_lock(JNIEnv *env, jclass cls)
{
	lua_lock(L);
}

EXPORT void JNICALL Java_gmod_Lua_unlock(JNIEnv *env, jclass cls)
{
	lua_unlock(L);
}

EXPORT void JNICALL Java_gmod_Lua_getfield(JNIEnv *env, jclass cls, jint index, jstring name)
{
	lua_lock(L);
	{
		const char *name_utf = env->GetStringUTFChars(name, JNI_FALSE);
		lua_getfield(L, int(index), name_utf);
		env->ReleaseStringUTFChars(name, name_utf);
	}
	lua_unlock(L);

}

EXPORT void JNICALL Java_gmod_Lua_setfield(JNIEnv *env, jclass cls, jint index, jstring name)
{
	lua_lock(L);
	{
		const char *name_utf = env->GetStringUTFChars(name, JNI_FALSE);
		lua_setfield(L, int(index), name_utf);
		env->ReleaseStringUTFChars(name, name_utf);
	}
	lua_unlock(L);
}

EXPORT jint JNICALL Java_gmod_Lua_gettop(JNIEnv *env, jclass cls)
{
	jint ret_value;

	lua_lock(L);
	{
		ret_value = lua_gettop(L);
	}
	lua_unlock(L);

	return ret_value;
}

EXPORT void JNICALL Java_gmod_Lua_pushvalue(JNIEnv *env, jclass cls, jint index)
{
	lua_lock(L);
	{
		lua_pushvalue(L, index);
	}
	lua_unlock(L);
}

EXPORT void JNICALL Java_gmod_Lua_pushnumber(JNIEnv *env, jclass cls, jdouble n)
{
	lua_lock(L);
	{
		lua_pushnumber(L, n);
	}
	lua_unlock(L);
}

EXPORT void JNICALL Java_gmod_Lua_pushboolean(JNIEnv *env, jclass cls, jboolean b)
{
	lua_lock(L);
	{
		lua_pushboolean(L, b);
	}
	lua_unlock(L);
}

EXPORT void JNICALL Java_gmod_Lua_pushstring(JNIEnv *env, jclass cls, jstring str)
{
	lua_lock(L);
	{
		const char *str_utf = env->GetStringUTFChars(str, JNI_FALSE);
		jsize str_len = env->GetStringLength(str);
		lua_pushlstring(L, str_utf, str_len);
		env->ReleaseStringUTFChars(str, str_utf);
	}
	lua_unlock(L);
}

EXPORT void JNICALL Java_gmod_Lua_pushobject(JNIEnv *env, jclass cls, jobject obj)
{
	lua_lock(L);
	{
		jobject_push(L, env, obj);
	}
	lua_unlock(L);
}

jobject java_function_obj;
int java_function_results;

int call_java_function_enclosed(lua_State *L)
{
	java_function_results = env->CallIntMethod(java_function_obj, method_lua_Function_invoke);
	return java_function_results;
}

void stackdump_g(lua_State* l)
{
    int i;
    int top = lua_gettop(l);
 
    printf("total in stack %d\n",top);
 
    for (i = 1; i <= top; i++)
    {  /* repeat for each level */
        int t = lua_type(l, i);
        switch (t) {
            case LUA_TSTRING:  /* strings */
                printf("string: '%s'\n", lua_tostring(l, i));
                break;
            case LUA_TBOOLEAN:  /* booleans */
                printf("boolean %s\n",lua_toboolean(l, i) ? "true" : "false");
                break;
            case LUA_TNUMBER:  /* numbers */
                printf("number: %g\n", lua_tonumber(l, i));
                break;
            default:  /* other values */
                printf("%s\n", lua_typename(l, t));
                break;
        }
        printf("  ");  /* put a separator */
    }
    printf("\n");  /* end the listing */
}

int call_java_function(lua_State *L)
{
	luaL_checkudata(L, lua_upvalueindex(1), JOBJECT_MT);
	luaL_checkint(L, lua_upvalueindex(2));
	
	int nargs = lua_tointeger(L, lua_upvalueindex(2));
	for(int i = 0; i < nargs; i++) 
	{
		luaL_checkany(L, lua_upvalueindex(3 + i));
	}

	int nargs_direct = lua_gettop(L);

	/* parse our jobject and verify it */
	java_function_obj = *(jobject *) lua_touserdata(L, lua_upvalueindex(1));
	if(!env->IsInstanceOf(java_function_obj, class_lua_Function))
	{
		lua_pushstring(L, "Received invalid Java Object, gmod.Lua.Function expected.");
		lua_error(L);
	}

	for(int i = 0; i < nargs; i++)
	{
		lua_pushvalue(L, lua_upvalueindex(3 + i));
	}
	lua_pushcclosure(L, call_java_function_enclosed, nargs);

	for(int i = 0; i < nargs_direct; i++)
	{
		lua_pushvalue(L, 1);
		lua_remove(L, 1);
	}

	lua_call(L, nargs_direct, LUA_MULTRET);

	/* check for exceptions and report them */
	if(env->ExceptionCheck())
	{
		env->ExceptionDescribe();
		env->ExceptionClear();

		lua_pushstring(L, "Exception thrown while calling the java function.");
		lua_error(L);
	}

	return java_function_results;
}

EXPORT void JNICALL Java_gmod_Lua_pushfunction(JNIEnv *env, jclass cls, jobject func)
{
	lua_lock(L);
	{
		jobject_push(L, env, func);
		lua_pushinteger(L, 0);
		lua_pushcclosure(L, call_java_function, 2);
	}
	lua_unlock(L);
}

EXPORT void JNICALL Java_gmod_Lua_pushclosure(JNIEnv *env, jclass cls, jobject func, jint nargs)
{
	lua_lock(L);
	{
		jobject_push(L, env, func);
		lua_pushinteger(L, nargs);
		for(int i = 0; i < nargs; i++)
		{
			lua_pushvalue(L, -(nargs + 2));
			lua_remove(L, -(nargs + 3));
		}
		lua_pushcclosure(L, call_java_function, 2 + nargs);
	}
	lua_unlock(L);
}

EXPORT void JNICALL Java_gmod_Lua_settop(JNIEnv *env, jclass cls, jint index)
{
	lua_lock(L);
	{
		lua_settop(L, index);
	}
	lua_unlock(L);
}

EXPORT void JNICALL Java_gmod_Lua_call(JNIEnv *env, jclass cls, jint nargs, jint nresults)
{
	lua_lock(L);
	{
		lua_call(L, nargs, nresults);
	}
	lua_unlock(L);
}

EXPORT jdouble JNICALL Java_gmod_Lua_tonumber(JNIEnv *env, jclass cls, jint index)
{
	jdouble ret_value;

	lua_lock(L);
	{
		ret_value = lua_tonumber(L, index);
	}
	lua_unlock(L);

	return ret_value;
}

EXPORT jstring JNICALL Java_gmod_Lua_tostring(JNIEnv *env, jclass cls, jint index)
{
	jstring ret_value;

	lua_lock(L);
	{
		const char* str_utf = lua_tostring(L, index);
		ret_value = env->NewStringUTF(str_utf);
	}
	lua_unlock(L);

	return ret_value;
}

EXPORT jobject JNICALL Java_gmod_Lua_toobject(JNIEnv *env, jclass cls, jint index)
{
	jobject ret_value;

	lua_lock(L);
	{
		ret_value = *(jobject *) lua_touserdata(L, index);
	}
	lua_unlock(L);

	return ret_value;
}

EXPORT void JNICALL Java_gmod_Lua_dump_stack(JNIEnv *env, jclass cls)
{
	lua_lock(L);
	{
		stackdump_g(L);
	}
	lua_unlock(L);
}