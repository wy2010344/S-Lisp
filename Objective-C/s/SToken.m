#import<Foundation/Foundation.h>

enum STokenTypes{
    BracketLeft,
    BracketRight,
    Comment,
    Prevent,
    Str,
    Id,
    Num
}
@interface SToken:NSObject{
    NSString * value;
    STokenTypes type;
    int index;
}
- (NSString *)Value;
- (STokenTypes)Type;
- (int)Index;

+ (Node*)tokenize:(NSString *)txt 
           length:(int)len 
             flag:(int)flag 
             rest:(Node*)rest;
+ (Node*)tokenize_split:(NSString *)txt 
                 length:(int)len 
                   flag:(int)flag 
                  split:(char)split 
                   type:(STokenTypes) type 
                   rest:(SNode*)rest 
                   Node:(SNode*)*cache;

@end

@implementation SToken

- (id)initWithValue:(NSString*) v type:(STokenTypes) t index:(index) i{
    self=[super init];
    
    value=v;
    type=t;
    index=i;

    return self;
}
-(NSString *)Value{
    return value;
}

-(STokenTypes)Type{
    return type;
}

- (int)Index{
    return index;
}

/*字符串遍历，用ASCII码对比，不用char*/
+ (Node*)tokenize:(NSString *)txt 
           length:(int)len 
             flag:(int)flag 
             rest:(Node*)rest{

}
+ (Node*)tokenize_split:(NSString *)txt 
                 length:(int)len 
                   flag:(int)flag 
                  split:(char)split 
                   type:(STokenTypes) type 
                   rest:(SNode*)rest 
                   Node:(SNode*)*cache{
    if(flag<len){

    }else{
        NSLog(@"超出范围");
    }
}
@end;