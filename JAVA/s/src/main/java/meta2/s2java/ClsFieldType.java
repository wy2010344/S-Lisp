package meta2.s2java;

/*
未来应该增加联合类型
*/
public enum ClsFieldType {
    ID,/*未初始化字段*/
    IDDefault,/*已初始化字段*/
    IDStatic,/*静态字段，一定会初始化*/
    Fn,/*未初始化函数*/
    FnDefault,/*已初始化函数*/
    FnStatic,/*静态函数，一定会初始化*/
}
