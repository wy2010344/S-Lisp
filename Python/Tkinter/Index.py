# coding=utf-8

import sys
sys.path.append("../s/")
from Util import Util
from S import S,Py_Eval,Py_Call,Py_Apply,Py_Attr,Py_Dict,Py_Function
s=S("\n")
s.addDef("py-eval",Py_Eval())
s.addDef("py-call",Py_Call())
s.addDef("py-apply",Py_Apply())
s.addDef("py-attr",Py_Attr())
s.addDef("py-dict",Py_Dict())
s.addDef("py-function",Py_Function())
s.run(Util.exe_path("index.lisp")).exe(None)
#s.shell()