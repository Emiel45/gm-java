#ifndef     GLUA_H
#define     GLUA_H

extern "C" { 
#include "lua.h"
#include "lauxlib.h"
#include "lualib.h"
}

#ifdef _WIN32
    #define DLL_EXPORT extern "C" __declspec(dllexport)
#else
    #define DLL_EXPORT extern "C" __attribute__((visibility("default")))
#endif

#define GMOD_MODULE_OPEN()	DLL_EXPORT int gmod13_open	(lua_State *L)
#define GMOD_MODULE_CLOSE()	DLL_EXPORT int gmod13_close (lua_State *L)

#endif //   GLUA_H