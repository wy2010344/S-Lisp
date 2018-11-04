# coding=utf-8

from Location import Location

class Code:
	"""docstring for Code"""
	def __init__(self, txt,lineSplit):
		self.txt = txt
		self.maxLength=len(txt)
		self.lineSplit = lineSplit
		self.i=-1
		self.row=0
		self.col=0
		self.shift()
		
	def shift(self):
		self.i=self.i+1
		if self.i < self.maxLength:
			self.c=self.txt[self.i]
			if self.c==self.lineSplit:
				self.col=0
				self.row=self.row+1
			else:
				self.col=self.col+1
		else:
			c=' '

	def noEnd(self):
		return self.i < self.maxLength

	def current(self):
		return self.c

	def index(self):
		return self.i

	def substr(self,start,end):
		return self.txt[start:end]

	def currentLoc(self):
		return Location(self.row,self.col,self.i)