
@interface SSystem : NSObject{
    
}
+ (NSString*)toString:(NSObject*)o trans:(BOOL)trans;
@end


@interface S_FirstFun : SLibFunction

@end

@implementation S_FirstFun

- (NSObject*)run:(SNode*)args{
	
                return [(SNode*)[args First] First];
            
}
- (SFunctionType)function_type{
	return SFunction_buildIn;
}
- (NSString*)description{
	return @"first";
}
@end
			            

@interface S_RestFun : SLibFunction

@end

@implementation S_RestFun

- (NSObject*)run:(SNode*)args{
	
                return [(SNode*)[args First] Rest];
            
}
- (SFunctionType)function_type{
	return SFunction_buildIn;
}
- (NSString*)description{
	return @"rest";
}
@end
			            

@interface S_ExtendFun : SLibFunction

@end

@implementation S_ExtendFun

- (NSObject*)run:(SNode*)args{
	
                return [SNode extend:[args First] with:(SNode*)[[args Rest] First]];
            
}
- (SFunctionType)function_type{
	return SFunction_buildIn;
}
- (NSString*)description{
	return @"extend";
}
@end
			            

@interface S_LengthFun : SLibFunction

@end

@implementation S_LengthFun

- (NSObject*)run:(SNode*)args{
	
                return [NSNumber numberWithInt:[(SNode*)[args First] Length]];
            
}
- (SFunctionType)function_type{
	return SFunction_buildIn;
}
- (NSString*)description{
	return @"length";
}
@end
			            

@interface S_Ref_countFun : SLibFunction

@end

@implementation S_Ref_countFun

- (NSObject*)run:(SNode*)args{
	
                return [NSNumber numberWithLong:[[args First] retainCount]-1];
            
}
- (SFunctionType)function_type{
	return SFunction_buildIn;
}
- (NSString*)description{
	return @"ref-count";
}
@end
			            

@interface S_IsemptyFun : SLibFunction

@end

@implementation S_IsemptyFun

- (NSObject*)run:(SNode*)args{
	
                return [SBool trans:[args First]==nil];
            
}
- (SFunctionType)function_type{
	return SFunction_buildIn;
}
- (NSString*)description{
	return @"empty?";
}
@end
			            

@interface S_IsexistFun : SLibFunction

@end

@implementation S_IsexistFun

- (NSObject*)run:(SNode*)args{
	
                return [SBool trans:[args First]!=nil];
            
}
- (SFunctionType)function_type{
	return SFunction_buildIn;
}
- (NSString*)description{
	return @"exist?";
}
@end
			            

@interface S_LogFun : SLibFunction

@end

@implementation S_LogFun

- (NSObject*)run:(SNode*)args{
	
                NSMutableString* str=[NSMutableString new];
                for (SNode* t=args; t!=nil; t=[t Rest]) {
                    [str appendString:[SSystem toString:[t First] trans:YES]];
                    [str appendString:@" "];
                }
                NSLog(@"%@",str);
                [SBase SEvalRelease:str];
                return nil;
            
}
- (SFunctionType)function_type{
	return SFunction_buildIn;
}
- (NSString*)description{
	return @"log";
}
@end
			            

@interface S_ToStringFun : SLibFunction

@end

@implementation S_ToStringFun

- (NSObject*)run:(SNode*)args{
	
                NSObject* b=[args First];
                return [SSystem toString:b trans:NO];
            
}
- (SFunctionType)function_type{
	return SFunction_buildIn;
}
- (NSString*)description{
	return @"toString";
}
@end
			            

@interface S_StringifyFun : SLibFunction

@end

@implementation S_StringifyFun

- (NSObject*)run:(SNode*)args{
	
                NSObject* b=[args First];
                return [SSystem toString:b trans:YES];
            
}
- (SFunctionType)function_type{
	return SFunction_buildIn;
}
- (NSString*)description{
	return @"stringify";
}
@end
			            

@interface S_IfFun : SLibFunction

@end

@implementation S_IfFun

            + (NSObject*)base_run:(SNode*)args{
                SBool* c=(SBool*)[args First];
                args=[args Rest];
                if([c Value]){
                    return [args First];
                }else{
                    args=[args Rest];
                    if(args!=nil){
                        return [args First];
                    }else{
                        return nil;
                    }
                }
            }
            
- (NSObject*)run:(SNode*)args{
	
                return [S_IfFun base_run:args];
            
}
- (SFunctionType)function_type{
	return SFunction_buildIn;
}
- (NSString*)description{
	return @"if";
}
@end
			            

@interface S_EqFun : SLibFunction

@end

@implementation S_EqFun

- (NSObject*)run:(SNode*)args{
	
                BOOL eq=YES;
                NSObject* old=[args First];
                SNode* t=[args Rest];
                while(eq && t!=nil){
                    eq=([t First]==old);
                    old=[t First];
                    t=[t Rest];
                }
                return [SBool trans:eq];
            
}
- (SFunctionType)function_type{
	return SFunction_buildIn;
}
- (NSString*)description{
	return @"eq";
}
@end
			            

@interface S_ApplyFun : SLibFunction

@end

@implementation S_ApplyFun

- (NSObject*)run:(SNode*)args{
	
                SFunction* f=(SFunction*)[args First];
                SNode* n_args=(SNode*)[[args Rest] First];
                NSObject* b=[f exec:n_args];
                if(b!=nil){
                    [SBase SEvalRelease:b];
                }
                return b;
            
}
- (SFunctionType)function_type{
	return SFunction_buildIn;
}
- (NSString*)description{
	return @"apply";
}
@end
			            

@interface S_TypeFun : SLibFunction

@end

@implementation S_TypeFun

            +(NSString*)base_run:(NSObject*)n{
                if(n==nil){
                    return @"list";
                }else{
                    if([n isKindOfClass:[SNode class]]){
                        return @"list";
                    }else
                    if([n isKindOfClass:[SFunction class]]){
                        return @"function";
                    }else
                    if([n isKindOfClass:[SBool class]]){
                        return @"bool";
                    }else
                    if([n isKindOfClass:[NSString class]]){
                        return @"string";
                    }else
                    if([n isKindOfClass:[NSNumber class]]){
                        return @"int";
                    }else{
                        return @"";
                    }
                }
            }
            
- (NSObject*)run:(SNode*)args{
	
                return [S_TypeFun base_run:[args First]];
            
}
- (SFunctionType)function_type{
	return SFunction_buildIn;
}
- (NSString*)description{
	return @"type";
}
@end
			            

@interface S_AndFun : SLibFunction

@end

@implementation S_AndFun

- (NSObject*)run:(SNode*)args{
	
                BOOL init=YES;
                SNode* t=args;
                while(t!=nil && init){
                    init=[(SBool*)[t First] Value];
                    t=[t Rest];
                }
                return [SBool trans:init];
            
}
- (SFunctionType)function_type{
	return SFunction_buildIn;
}
- (NSString*)description{
	return @"and";
}
@end
			            

@interface S_OrFun : SLibFunction

@end

@implementation S_OrFun

- (NSObject*)run:(SNode*)args{
	
                BOOL init=NO;
                SNode* t=args;
                while(t!=nil && (!init)){
                    init=[(SBool*)[t First] Value];
                    t=[t Rest];
                }
                return [SBool trans:init];
            
}
- (SFunctionType)function_type{
	return SFunction_buildIn;
}
- (NSString*)description{
	return @"or";
}
@end
			            

@interface S_NotFun : SLibFunction

@end

@implementation S_NotFun

- (NSObject*)run:(SNode*)args{
	
                return [SBool trans:![(SBool*)[args First] Value]];
            
}
- (SFunctionType)function_type{
	return SFunction_buildIn;
}
- (NSString*)description{
	return @"not";
}
@end
			            

@interface S_QuoteFun : SLibFunction

@end

@implementation S_QuoteFun

- (NSObject*)run:(SNode*)args{
	
                return [args First];
            
}
- (SFunctionType)function_type{
	return SFunction_user;
}
- (NSString*)description{
	return @"{(first args ) }";
}
@end
			            

@interface S_ListFun : SLibFunction

@end

@implementation S_ListFun

- (NSObject*)run:(SNode*)args{
	
                return args;
            
}
- (SFunctionType)function_type{
	return SFunction_user;
}
- (NSString*)description{
	return @"{args }";
}
@end
			            

@interface S_Kvs_find1stFun : SLibFunction

@end

@implementation S_Kvs_find1stFun

- (NSObject*)run:(SNode*)args{
	
                SNode* kvs=(SNode*)[args First];
                args=[args Rest];
                NSString* key=(NSString*)[args First];
                return [SNode kvs_find1stFrom:kvs of:key];
            
}
- (SFunctionType)function_type{
	return SFunction_user;
}
- (NSString*)description{
	return @"{(let (key kvs ) args find1st this ) (let (k v kvs ) args ) (if-run (str-eq k key ) {v } {(find1st key kvs ) } ) }";
}
@end
			            

@interface S_Kvs_extendFun : SLibFunction

@end

@implementation S_Kvs_extendFun

- (NSObject*)run:(SNode*)args{
	
                NSString* key=(NSString*)[args First];
                args=[args Rest];
                NSObject* value=[args First];
                args=[args Rest];
                SNode* kvs=(SNode*)[args First];
                return [SNode kvs_extendKey:key value:value kvs:kvs];
            
}
- (SFunctionType)function_type{
	return SFunction_user;
}
- (NSString*)description{
	return @"{(let (k v kvs ) args ) (extend k (extend v kvs ) ) }";
}
@end
			            
@implementation SSystem
+ (NSString*)toString:(NSObject*)o trans:(BOOL)trans{
    if (o==nil) {
        return @"[]";
    }else if (trans){
        if ([o isKindOfClass:[NSString class]]) {
            return [NSString stringWithFormat:@"\"%@\"",o];
        }else{
            return [o description];
        }
    }else{
        return [o description];
    }
}
+ (SNode*)library{
    SNode* m=nil;
    
	m=[SNode kvs_extendKey:@"first" value:[S_FirstFun new] kvs:m];
	m=[SNode kvs_extendKey:@"rest" value:[S_RestFun new] kvs:m];
	m=[SNode kvs_extendKey:@"extend" value:[S_ExtendFun new] kvs:m];
	m=[SNode kvs_extendKey:@"length" value:[S_LengthFun new] kvs:m];
	m=[SNode kvs_extendKey:@"ref-count" value:[S_Ref_countFun new] kvs:m];
	m=[SNode kvs_extendKey:@"empty?" value:[S_IsemptyFun new] kvs:m];
	m=[SNode kvs_extendKey:@"exist?" value:[S_IsexistFun new] kvs:m];
	m=[SNode kvs_extendKey:@"log" value:[S_LogFun new] kvs:m];
	m=[SNode kvs_extendKey:@"toString" value:[S_ToStringFun new] kvs:m];
	m=[SNode kvs_extendKey:@"stringify" value:[S_StringifyFun new] kvs:m];
	m=[SNode kvs_extendKey:@"if" value:[S_IfFun new] kvs:m];
	m=[SNode kvs_extendKey:@"eq" value:[S_EqFun new] kvs:m];
	m=[SNode kvs_extendKey:@"apply" value:[S_ApplyFun new] kvs:m];
	m=[SNode kvs_extendKey:@"type" value:[S_TypeFun new] kvs:m];
	m=[SNode kvs_extendKey:@"and" value:[S_AndFun new] kvs:m];
	m=[SNode kvs_extendKey:@"or" value:[S_OrFun new] kvs:m];
	m=[SNode kvs_extendKey:@"not" value:[S_NotFun new] kvs:m];
	m=[SNode kvs_extendKey:@"quote" value:[S_QuoteFun new] kvs:m];
	m=[SNode kvs_extendKey:@"list" value:[S_ListFun new] kvs:m];
	m=[SNode kvs_extendKey:@"kvs-find1st" value:[S_Kvs_find1stFun new] kvs:m];
	m=[SNode kvs_extendKey:@"kvs-extend" value:[S_Kvs_extendFun new] kvs:m];
    return m;
}

@end