#include "DecodeWlt.h"
#include <dlfcn.h>
#include <stdio.h>

#ifndef LOG_TAG
#define LOG_TAG "MY_DEFAULT"
#endif
#include <android/log.h>
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#define NULL 0
#define TRUE 1
#define FALSE 0

//extern "C" int unpack(const char* pszInFile, const char* pszBmpPath, int bSaveBmp);
typedef int (*def_unpack)(const char*, const char*, int);


JNIEXPORT jint JNICALL Java_com_ntga_card_DecodeWlt_Wlt2Bmp
  (JNIEnv * env, jobject obj, jstring wltPath, jstring bmpPath)
{
	const char* pszWltPath=NULL;
	const char* pszBmpPath=NULL;
	jboolean isCopy=JNI_FALSE;
	jint result;
	void *hso=NULL;
	def_unpack pfn_unpack;
	
	if(NULL==wltPath || NULL==bmpPath)
		return -11;
	do
	{
		hso=dlopen("/data/data/com.ntga.jwt/lib/libWltRS.so",RTLD_NOW);
		if(NULL==hso)
		{
			result=-12;
			const char* errmsg=dlerror();
			if(NULL==errmsg)
				LOGI("No error message.\n");
			else
				LOGE("%s\n",errmsg);
			break;
		}
		else
		{
			LOGI("Load libWltRS.so success\n");
		}
		pfn_unpack=(def_unpack)dlsym(hso,"unpack");
		if(NULL==pfn_unpack)
		{
			result=-13;
			LOGE("Get \"unpack\" function failed");
			break;
		}
		else
		{
			LOGI("Get \"unpack\" function success");
		}
		pszWltPath=env->GetStringUTFChars(wltPath,&isCopy);
		if(NULL==pszWltPath)
		{
			result=-14;
			break;
		}
		pszBmpPath=env->GetStringUTFChars(bmpPath,&isCopy);
		if(NULL==pszBmpPath)
		{
			result=-14;
			break;
		}
		result=pfn_unpack(pszWltPath,pszBmpPath,1);
	}while(FALSE);
	if(NULL!=pszWltPath)
		env->ReleaseStringUTFChars(wltPath,pszWltPath);
	if(NULL!=pszBmpPath)
		env->ReleaseStringUTFChars(bmpPath,pszBmpPath);
	if(NULL!=hso)
		dlclose(hso);
	return result;
}
