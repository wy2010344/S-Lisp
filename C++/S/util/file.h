#pragma once
#include <fstream>/*file*/
namespace s{
    namespace file{
        string read(string & path,const string lineSplits){
            ifstream myfile(path.c_str());
            string tmp;
            if (!myfile.is_open())
            {
                cout << "未成功打开文件:"<<path << endl;
                return NULL;
            }
            string sb;
            while(getline(myfile,tmp))
            {
                sb+=tmp;
                sb+=lineSplits;
            }
            myfile.close();
            return sb;
        }
        void write(string & path,string & content){
            ofstream f1(path.c_str());
            f1<<content;
            f1.close();
        }
    };
};
