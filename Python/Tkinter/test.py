#!/usr/bin/python
# -*- coding: UTF-8 -*-
 
import Tkinter
import tkMessageBox
 
def test():
    top = Tkinter.Tk()
     
    def helloCallBack():
        B["text"]="正常了"
     
    B = Tkinter.Button(top, text ="点我", command = helloCallBack)
     
    B.pack()
    top.mainloop()
#test()