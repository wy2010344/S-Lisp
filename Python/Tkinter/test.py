#!/usr/bin/python
# -*- coding: UTF-8 -*-
 

from Tkinter import *
import tkMessageBox
 
def test():
    top = Tk()
     
    def helloCallBack():
        B["text"]="正常了"
     
    B = Button(top, text ="点我", command = helloCallBack)
     
    B.pack()
    top.mainloop()
#test()
def pytalk():
    import time
    
    t = Tk()
    t.title('与python聊天中')
           
    #创建frame容器
    frmLT = Frame(width=500, height=320, bg='white')
    frmLC = Frame(width=500, height=150, bg='red')
    frmLB = Frame(width=500, height=30)
    frmRT = Frame(width=200, height=500)
    
    frmLT.grid(row=0, column=0,padx=1,pady=3)
    frmLC.grid(row=1, column=0,padx=1,pady=3)
    frmLB.grid(row=2, column=0)
    frmRT.grid(row=0, column=1, rowspan=3,padx=2,pady=3)
    
    '''#固定容器大小
    frmLT.grid_propagate(0)
    frmLC.grid_propagate(0)
    frmLB.grid_propagate(0)
    frmRT.grid_propagate(0)'''
    
    #添加按钮
    btnSend = Button(frmLB, text='发 送', width = 8)#在frmLB容器中添加
    btnSend.grid(row=2,column=0)
    btnCancel = Button(frmLB, text='取消', width = 8)
    btnCancel.grid(row=2,column=1,sticky=E)
    
    #添加图片
    imgInfo = PhotoImage(file = "python_logo.gif")
    lblImage = Label(frmRT, image = imgInfo)
    lblImage.image = imgInfo
    lblImage.grid()
    
    #固定容器大小
    frmLT.grid_propagate(0)
    frmLC.grid_propagate(0)
    frmLB.grid_propagate(0)
    frmRT.grid_propagate(0)
    mainloop()
#pytalk()
def testLabelFrame():
    root = Tk()
    group = LabelFrame(root,text = '你最喜欢中国四大美女中的哪一位？',padx = 5,pady = 5)
    group.pack(padx = 10,pady = 10)
    
    girls = [('西施',1),('王昭君',2),('杨玉环',3),('貂蝉',4)]
    v = IntVar()
    v.set(1)
    for girl,num in girls:
        #调用父窗口是group而不是root，注意这个地方。
        Radiobutton(group,text = girl,variable = v,value = num).pack(anchor = W)
    mainloop()
    
testLabelFrame()