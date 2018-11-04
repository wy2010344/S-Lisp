# coding=utf-8
from LocationException import LocationException
from Token import Token
from Exp import Exp
from QueueRun import QueueRun
from System import System

class S:
	"""docstring for S"""
	def __init__(self, lineSplit):
		self.lineSplit = lineSplit
		self.scope=System.library()
		
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
S("\n").shell()		
			