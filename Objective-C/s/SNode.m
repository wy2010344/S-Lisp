#import<Foundation/Foundation.h>
@interface SNode:NSObject{
	int length;
	NSObject * first;
	SNode *rest;
}

- (NSObject *) First;
- (SNode *) Rest;
- (int) Length;
@end

@implementation SNode

/*NSObject默认计数是1*/
- (id) initWith:(NSObject *) f andRest:(SNode *) r{
	self=[super init];
	if(r!=nil){
		length=[r Length]+1;
	}else{
		length=1;
	}
	NSLog(@"构造%@",f);
	first=f;
	rest=r;
	return self;
}
- (NSObject *) First{
	return first;
}

- (SNode *) Rest{
	return rest;
}

- (int) Length{
	return length;
}

- (void) dealloc{
	if(first!=nil){
		NSLog(@"销毁了%@",first);
		[first release];
	}else{
		NSLog(@"销毁了");
	}
	if(rest!=nil){
		[rest release];
	}
	[super dealloc];
}
@end