#include <windows.h>

#include "lua_lock.h"

CRITICAL_SECTION lock;
bool lock_exists = false;

void lua_lock(lua_State *L)
{
	if(lock_exists) EnterCriticalSection(&lock);
}

void lua_unlock(lua_State *L)
{
	if(lock_exists) LeaveCriticalSection(&lock);
}

void lua_createlock(lua_State *L)
{
	if(!lock_exists) 
	{
		InitializeCriticalSection(&lock);
		lock_exists = true;
	}
}

bool lua_haslock(lua_State *L)
{
	if(!lock_exists) return false;
	return TryEnterCriticalSection(&lock);
}