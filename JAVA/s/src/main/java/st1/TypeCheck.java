package st1;

import mb.RangePathsException;
import s.Node;
import st1.bean.*;
import st1.exp.*;

public class TypeCheck {


    Type eval(Exp exp,Node<Object> scope){
        if (exp instanceof StringExp){
            return StringType.getInstance();
        }else if(exp instanceof IntExp){
            return IntType.getInstance();
        }else if(exp instanceof FunctionExp){
        }else if(exp instanceof IdExp){
            return (Type)Node.kvs_find1st(scope,((IdExp)exp).value);
        }
        return null;
    }

    /*参数类型的计算*/
    Node<Type> evalList(Node<Exp> r_children, Node<Object> scope){
        Node<Type> r=null;
        Node<Exp> tmp=r_children;
        while (tmp!=null){
            r=Node.extend(eval(tmp.First(),scope),r);
        }
        return r;
    }

    public Type runPip(PipExp exp,Node<Object> scope) throws RangePathsException {
        Exp first=exp.children.First();
        Type o=eval(first,scope);
        Node<Exp> tmp=exp.children.Rest();
        while (tmp!=null){
            Exp child=tmp.First();
            if (child instanceof PointExp){
                /*访问小数点后，调用*/
                if (o instanceof StructType){
                    o=((StructType)o).get(((PointExp)child).value);
                }else{
                    throw child.exception("无法访问，不是结构体类型");
                }
            }else if (child instanceof SBracketExp){
                /*访问括号*/
                if (o instanceof FunctionType){
                    o=((FunctionType)o).match(evalList(((SBracketExp) child).r_children,scope));
                }else{
                    throw child.exception("不是函数类型，不能后接受调用()");
                }
            }else if(child instanceof ABracketExp){
                /*<>，即函数的泛型化*/
                if(o instanceof MarcoFunctionType){
                    /*
                        泛型的依赖类型，应该可以计算出来，而不是需要明确地指定，
                        未来应该可以根据参数类型具体具体，免泛型声明
                        如果支持柯西里化，很自然。
                    */
                    o=((MarcoFunctionType)o).downType(evalList(((ABracketExp) child).r_children,scope));
                }else{
                    throw child.exception("不是函数类型，不能后接受泛型具体化<>");
                }
            }else if (child instanceof MBracketExp){
                /*
                中括号的下标访问，比如字典、列表之类的，是否要处理？
                要么重载中括号
                要么中括号表示一个参数的特例
                可用函数替代
                */
                throw child.exception("暂时不支持中括号");
            }else{
                throw child.exception("不是期待的类型");
            }
            tmp=tmp.Rest();
        }
        return o;
    }
}
