# coding=utf-8
from LocationException import LocationException
from Token import Token
from Exp import Exp
from QueueRun import QueueRun,UserFunction
from System import System
from Function import Function,FunctionType,LibFunction
from Util import Util
from Node import Node

import os

class S:
	"""docstring for S"""
	def __init__(self, lineSplit):
		self.lineSplit = lineSplit
		self.scope=System.library()
		lib_path=LibPath()
		self.scope=Node.kvs_extend("lib-path",lib_path,self.scope)
		self.loadLibKVS(lib_path.calculate("index.lisp"))
		
		
	def loadValue(self,path,delay,delay_args):
		value=Load.run_e(path,self.scope,self.lineSplit)
		if delay:
			value=value.exe(delay_args)
		return value
		
	def __LoadLibKV__(self,key,value):
		self.scope=Node.kvs_extend(key,value,self.scope)
		return self;
	
	def loadLibKV(self,path,key):
		return self.__LoadLibKV__(key,self.loadValue(path,false,None))
	
	def loadLibKV_delay(self,path,key,delay_args):
		return self.__LoadLibKV__(key,self.loadValue(path,true,delay_args))
	
	def __loadLibKVS__(self,kvs):
		tmp=kvs
		while tmp!=None:
			key=tmp.First()
			tmp=tmp.Rest()
			value=tmp.First()
			tmp=tmp.Rest()
			self.scope=Node.kvs_extend(key,value,self.scope)
		return self
	'''
	不延迟KVS
	'''
	def loadLibKVS(self,path):
		return self.__loadLibKVS__(self.loadValue(path, False, None))
	
	'''
	延迟kvs
	'''
	def loadLibKVS_delay(self,path,delay_args):
		return self.__loadLibKVS__(self.loadValue(path, True, delay_args))
	
	def addDef(self,key,value):
		self.scope=Node.kvs_extend(key,value,self.scope)
		return self
	
	def run(self,path):
		return Load.run_e(path, self.scope, self.lineSplit)
	
	def shell(self):
		tmp=""
		cache=""
		come=True
		qr=QueueRun(self.scope)
		while come:
			tmp=raw_input("<=")
			if tmp=="``":
				will=True
				while will:
					tmp=raw_input()
					if tmp=="``":
						will=False
					else:
						cache=cache+tmp+self.lineSplit
			else:
				cache=tmp
				
			if cache=="exit":
				come=False
			else:
				self.__shell__(qr, cache)
				'''
				try:
					self.__shell__(qr, cache);
				except LocationException,lex:
					print(str(lex))
				except BaseException,ex:
					print(ex)
				'''
				cache=""
	def __shell__(self,qr,cache):
		tokens=Token.run(cache,self.lineSplit)
		if tokens!=None:
			exp=Exp.Parse(tokens)
			r=qr.exe(exp)
			print("=>%s" % System.toString(r,True))
		
class LibPath(LibFunction):
	def __init__(self):
		try:
			self.lib_path=os.environ.get("S_LISP")
		except BaseException,be:
			print(be)
		finally:
			if (self.lib_path==None or self.lib_path==""):
				self.lib_path="D:/S_Lisp"
			else:
				self.lib_path=self.lib_path.replace('\\','/')
			if (not self.lib_path[len(self.lib_path)-1]=='/'):
				self.lib_path=self.lib_path+"/"
			#print(self.lib_path)
			
	def calculate(self,path):
		if (not path[0]=='.'):
			path="./"+path
		return Util.absolute_from_relative(self.lib_path,path)
	def exe(self,args):
		path=args.First()
		return self.calculate(path)
	def __str__(self):
		return "lib-path"
	
class Load(LibFunction):
	def __init__(self,baseScope,base_path,lineSplit):
		self.baseScope=baseScope
		self.base_path=base_path
		self.lineSplit=lineSplit
	def __str__(self):
		return "load"
	def exe(self,args):
		r_path=args.First()
		path=Util.absolute_from_relative(self.base_path,r_path)
		return Load.run_e(path,self.baseScope,self.lineSplit)
	
	onLoad=False
	core=None
	@staticmethod
	def run_e(path,scope,lineSplit):
		if Load.onLoad:
			raise BaseException("禁止在加载期间加载")
		else:
			x=Node.kvs_find1st(Load.core,path)
			if x!=None:
				return x.First()
			else:
				Load.onLoad=True
				sb=Util.readTxt(path,lineSplit)
				scope=Node.kvs_extend("load",Load(scope,path,lineSplit),scope)
				scope=Node.kvs_extend("pathOf",PathOf(path),scope)
				tokens=Token.run(sb,lineSplit)
				exp=Exp.Parse(tokens)
				f=UserFunction(exp,scope)
				b=f.exe(None)
				Load.core=Node.kvs_extend(path,Node.extend(b,None),Load.core)
				Load.onLoad=False
				return b
			
class PathOf(LibFunction):
	def __init__(self,basePath):
		self.basePath=basePath
	def __str__(self):
		return "pathOf"
	def exe(self,args):
		if args==None:
			return self.basePath
		else:
			r_path=args.First()
			return Util.absolute_from_relative(self.basePath,r_path)
	
class Parse(LibFunction):
	def __init__(self,defaultScope,lineSplit):
		self.defaultScope=defaultScope
		self.lineSplit=lineSplit
	def __str__(self):
		return "parse"
	def exe(self,args):
		the_str=args.First()
		args=args.Rest()
		scope=self.defaultScope if args==None else args.First()
		tokens=Token.run(str,self.lineSplit)
		exp=Exp.Parse(tokens)
		f=UserFunction(exp,scope)
		return f.exe(None)
	
class CacheValue(Function):
	def __str__(self):
		return "[]"
	def Function_type(self):
		return FunctionType.cache
	def exe(self,args):
		if args==None:
			return self.value
		else:
			self.value=args.First()
			return None
class Cache(LibFunction):
	def __str__(self):
		return "cache"
	def exe(self,args):
		cv=CacheValue()
		cv.exe(args)
		return cv
		
'''
殷富字符串
'''
class Py_Eval(LibFunction):
	def __str__(self):
		return "py-eval"
	def exe(self,args):
		return eval(args.First())
	
'''
调用实例方法
o
key
ps
'''
class Py_Call(LibFunction):
	def __str__(self):
		return "py-call"
	def exe(self,args):
		o=args.First()
		if isinstance(o, basestring):
			o=eval(o)
		args=args.Rest()
		key=args.First()#方法名
		args=args.Rest()
		
		build=Py_Call.run(args)
		ps=build[0]
		return eval("o."+key+build[1])
	@staticmethod
	def run(args):
		ps=[]
		avs=[]
		if args!=None:
			tmp=args.First()
			i=0
			while tmp!=None:
				ps.append(tmp.First())
				avs.append("ps["+str(i)+"]")
				tmp=tmp.Rest()
				i=i+1
			args=args.Rest()
			if args!=None:
				tmp=args.First()
				while tmp!=None:
					key=tmp.First()
					tmp=tmp.Rest()
					value=tmp.First()
					tmp=tmp.Rest()
					ps.append(value)
					avs.append(key+"=ps["+str(i)+"]")
					i=i+1
		return (ps,"("+",".join(avs)+")")
	
'''
调用函数
fun
ps
'''
class Py_Apply(LibFunction):
	def __str__(self):
		return "py-apply"
	def exe(self,args):
		o=args.First()
		if isinstance(o, baststring):
			o=eval(o)
		args=args.Rest()
		build=Py_Call.run(args);
		ps=build[0]
		return eval("o"+build[1]);
	
'''
访问实例属性o.key=value o.key
o
key
[value]如果存在，则设置，不存在，则返回
'''
class Py_Attr(LibFunction):
	def __str__(self):
		return "py-attr"
	def exe(self,args):
		o=args.First()
		if isinstance(o, basestring):
			o=eval(o)
		args=args.Rest()
		key=args.First()
		args=args.Rest()
		if args!=None:
			value=args.First()
			setattr(o,key,value)
			return None
		else:
			return getattr(o,key)
	
'''
设置字典方法o[key]=value o[key]
'''
class Py_Dict(LibFunction):
	def __str__(self):
		return "py-dict"
	def exe(self,args):
		o=args.First()
		if isinstance(o, basestring):
			o=eval(o)
		args=args.Rest()
		key=args.First()
		args=args.Rest()
		if args!=None:
			value=args.First()
			args=args.Rest()
			if args!=None and args.First():#强制更新
				o.update({key:value})
			else:
				o[key]=value
		else:
			return o.get(key,None)
		
class Py_FunctionValue:
	def __init__(self,fun):
		self.fun=fun
	def __call__(self,*args):
		vs=None
		for arg in args:
			vs=Node.extend(arg,vs)
		return self.fun.exe(vs)
'''
将S-Lisp函数转为python函数
'''
class Py_Function(LibFunction):
	def __str__(self):
		return "py-function"
	def exe(self,args):
		fun=args.First()
		return Py_FunctionValue(fun)
#S("\n").shell()		
			
