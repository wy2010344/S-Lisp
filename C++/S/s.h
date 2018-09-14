#pragma once
#include "./util/base.h"
#include "./util/location.h"
#include "./util/exception.h"
#include "./util/str.h"
#include "./util/file.h"

#include "./type/bool.h"
#include "./type/function.h"
#include "./type/int.h"
#include "./type/node.h"
#include "./type/string.h"

#include "./util/list.h"
#include "./util/kvs.h"

#include "./util/lib_function.h"
namespace s{
    void logException(Exception* e){
        cout<<"出现异常："<<e->Msg();
        if(e->type()==Exception::Exception_Location){
            LocationException* ex=static_cast<LocationException*>(e);
            cout<<"在位置"<<ex->Loc()->toString()<<endl;
        }else{
            cout<<endl;
        }
        delete e;
    }
};
