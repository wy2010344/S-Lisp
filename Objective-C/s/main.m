#import <Foundation/Foundation.h>
#import "./SNode.m"
int main (int argc , const char *argv[]) {
    NSAutoreleasePool *pool = [[NSAutoreleasePool alloc ] init];
    SNode * node=[[SNode alloc] initWith:@"我尝试" andRest:nil];
    node=[[SNode alloc] initWith:@"我相信" andRest:node];
    NSLog(@"retain_count%@",[node valueForKey:@"retainCount"]);
    /*所有的OC对象默认是1，看来必须用自定义的retain和release，在检查到retainCount为1的时候，就再release一下销毁掉*/
    [node release];
    NSLog(@"Hello World!");

    //编不过呢
    NSString *newStr = @"abdcdddccdd00我是中文";
 
    int i=0;
    while(i<[newStr length])
    {   
      NSLog(@"第%d个字符是:%@",i, [newStr characterAtIndex:i]);
      i++;
    } 

    i=0;
    while(i<[newStr length]){
       NSLog(@"第%d个字是:%@",i,[newStr substringWithRange:NSMakeRange(i, 1)]);
       i++;
    } 

    [pool drain];
 
    return 0;
}
