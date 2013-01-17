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

jclass class_lua_Exception;

int jobject__gc(lua_State *lua)
{
	jobject obj = *(jobject *) lua_touserdata(L, 1);
	// env->DeleteGlobalRef(obj);

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

int java_setref(lua_State *lua)
{
	luaL_checkany(L, 1);
	luaL_checkany(L, 2);

	lua_getglobal(L, "java");
	lua_getfield(L, -1, "ref");
	
	lua_pushvalue(L, 1);
	lua_pushvalue(L, 2);

	lua_settable(L, -3);
	return 0;
}

int java_getref(lua_State *lua)
{
	luaL_checkany(L, 1);

	lua_getglobal(L, "java");
	lua_getfield(L, -1, "ref");
	
	lua_pushvalue(L, 1);

	lua_gettable(L, -2);
	return 1;
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
	method_lua_Function_invoke = env->GetMethodID(class_lua_Function, "invoke", "(II)I");
	
	class_lua_Exception = jclass_find_global("gmod/Lua$Exception");

	/* Register process async hook */
	lua_getglobal(L, "hook");
	lua_getfield(L, -1, "Add");
	lua_pushstring(L, "Think");
	lua_pushstring(L, "java.process_async");
	lua_pushcfunction(L, java_process_async);
	lua_call(L, 3, 0);

	/* Register java table */
	lua_newtable(L);
	
	lua_newtable(L);
	lua_setfield(L, -2, "ref");
	
	lua_pushcfunction(L, java_setref);
	lua_setfield(L, -2, "setref");

	lua_pushcfunction(L, java_getref);
	lua_setfield(L, -2, "getref");

	lua_setglobal(L, "java");

	return 0;
}

/* lua lock functionality for async operations */
bool lua_checklock(lua_State *L)
{
	if(!lua_haslock(L))
	{
		env->ThrowNew(class_lua_Exception, "No lock accuired.");
		return false;
	}

	return true;
}


EXPORT void JNICALL Java_gmod_Lua_lock(JNIEnv *env, jclass cls)
{
	lua_lock(L);
}

EXPORT void JNICALL Java_gmod_Lua_unlock(JNIEnv *env, jclass cls)
{
	lua_unlock(L);
}

/* lua_jcall() */
void lua_jcall(lua_State *L, int nargs, int nresults)
{
	int status = lua_pcall(L, nargs, nresults, 0);
	if(status != 0) {
		env->ThrowNew(class_lua_Exception, lua_tostring(L, -1));
	}
}

/* lua_absindex() */
#define lua_absindex(L, index) (index > 0 || index <= LUA_REGISTRYINDEX) ? (index) : (lua_gettop(L) + index + 1)

/* lua_getfield() */
const char *p_lua_getfield_k;
int p_lua_getfield(lua_State *L)
{
	lua_getfield(L, 1, p_lua_getfield_k);
	return 1;
}

EXPORT void JNICALL Java_gmod_Lua_getfield(JNIEnv *env, jclass cls, jint index, jstring name)
{
	if(!lua_checklock(L)) return;

	p_lua_getfield_k = env->GetStringUTFChars(name, JNI_FALSE);
	
	index = lua_absindex(L, index);
	lua_pushcfunction(L, p_lua_getfield);
	lua_pushvalue(L, index);
	lua_jcall(L, 1, 1);

	env->ReleaseStringUTFChars(name, p_lua_getfield_k);
}

EXPORT void JNICALL Java_gmod_Lua_setfield(JNIEnv *env, jclass cls, jint index, jstring name)
{
	lua_lock(L);
	{
		const char *name_utf = env->GetStringUTFChars(name, JNI_FALSE);
		lua_setfield(L, index, name_utf);
		env->ReleaseStringUTFChars(name, name_utf);
	}
	lua_unlock(L);
}

EXPORT void JNICALL Java_gmod_Lua_gettable(JNIEnv *env, jclass cls, jint index)
{
	if(!lua_checklock(L)) return;

	lua_gettable(L, index);
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

EXPORT void JNICALL Java_gmod_Lua_settop(JNIEnv *env, jclass cls, jint index)
{
	lua_lock(L);
	{
		lua_settop(L, index);
	}
	lua_unlock(L);
}

EXPORT void JNICALL Java_gmod_Lua_remove(JNIEnv *env, jclass cls, jint index)
{
	lua_lock(L);
	{
		lua_remove(L, index);
	}
	lua_unlock(L);
}

EXPORT void JNICALL Java_gmod_Lua_pushvalue(JNIEnv *env, jclass cls, jint index)
{
	lua_lock(L);
	{
		lua_pushvalue(L, index);
	}
	lua_unlock(L);
}

EXPORT void JNICALL Java_gmod_Lua_pushnil(JNIEnv *env, jclass cls)
{
	lua_lock(L);
	{
		lua_pushnil(L);
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

EXPORT void JNICALL Java_gmod_Lua_pushinteger(JNIEnv *env, jclass cls, jint i)
{
	lua_lock(L);
	{
		lua_pushinteger(L, i);
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
jint java_function_nargs;
int java_function_results;

int call_java_function_enclosed(lua_State *L)
{
	java_function_results = env->CallIntMethod(java_function_obj, method_lua_Function_invoke, java_function_nargs, -1);
	return java_function_results;
}

void stackdump_g(lua_State* l)
{
    int i;
    int top = lua_gettop(l);
 
    printf("total in stack %d\n",top);
 
    for (i = top; i > 0; i--)
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
				lua_getglobal(L, "tostring");
				lua_pushvalue(L, i);
				lua_call(L, 1, 1);
                printf("%s\n", lua_tostring(l, -1));
				lua_pop(L, 1);
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
	
	java_function_nargs = lua_tointeger(L, lua_upvalueindex(2));
	for(int i = 0; i < java_function_nargs; i++) 
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


	for(int i = 0; i < java_function_nargs; i++)
	{
		lua_pushvalue(L, lua_upvalueindex(3 + i));
	}
	lua_pushcclosure(L, call_java_function_enclosed, java_function_nargs);

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

EXPORT void JNICALL Java_gmod_Lua_call(JNIEnv *env, jclass cls, jint nargs, jint nresults)
{
	lua_lock(L);
	{
		lua_call(L, nargs, nresults);
	}
	lua_unlock(L);
}

EXPORT jboolean JNICALL Java_gmod_Lua_toboolean(JNIEnv *env, jclass cls, jint index)
{
	jboolean ret_value;

	lua_lock(L);
	{
		ret_value = lua_toboolean(L, index);
	}
	lua_unlock(L);

	return ret_value;
}

EXPORT jint JNICALL Java_gmod_Lua_tointeger(JNIEnv *env, jclass cls, jint index)
{
	jint ret_value;

	lua_lock(L);
	{
		ret_value = lua_tointeger(L, index);
	}
	lua_unlock(L);

	return ret_value;
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

EXPORT jint JNICALL Java_gmod_Lua_objlen(JNIEnv *env, jclass cls, jint index)
{
	if(!lua_checklock(L)) return -1;

	return lua_objlen(L, index);
}

EXPORT void JNICALL Java_gmod_Lua_dump_1stack(JNIEnv *env, jclass cls)
{
	lua_lock(L);
	{
		stackdump_g(L);
	}
	lua_unlock(L);
}