# coding=utf-8
class Location:
	"""docstring for Location"""
	def __init__(self, row,col,i):
		self.row = row
		self.col = col
		self.i = i
		
	def Row(self):
		return self.row

	def Col(self):
		return self.col

	def Index(self):
		return self.i

	def __str__(self):
		return "位置"+str(self.row+1)+"行"+str(self.col+1)+"列，第"+str(self.i+1)+"个字符串"
	
'''
print(Location(1,2,3))
'''