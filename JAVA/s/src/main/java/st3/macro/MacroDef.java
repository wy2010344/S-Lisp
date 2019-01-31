package st3.macro;

import mb.RangePathsException;
import s.Node;
import st3.*;

/**
 * (marcodef (args (a b c d e) this)
 *      ()
 *      ()
 *      ()
 * )
 *
 */
public class MacroDef extends ReadMacro {

    @Override
    public Object exec(Node<Object> scope, BracketExp bracketExp) throws RangePathsException {
        Node<Exp> args=bracketExp.children.Rest();
        if (args==null || args.Length()<1){
            throw bracketExp.exception("需要至少两个参数，一个是参数绑定，一个是定义体");
        }else {
            Exp arg_name_exp = args.First();
            if (arg_name_exp==null || arg_name_exp instanceof BracketExp){
                IDExp name_of_scope=null;
                Exp name_of_args=null;
                IDExp name_of_this=null;
                if (arg_name_exp!=null){
                    Node<Exp> arg_names= ((BracketExp) arg_name_exp).children;
                    if (arg_names!=null){
                        if (arg_names.Length()>4){
                            throw arg_name_exp.exception("需要最多3个参数，function,args,this");
                        }else{
                            Exp tmp=arg_names.First();
                            arg_names=arg_names.Rest();
                            if (tmp instanceof IDExp){
                                name_of_scope= (IDExp) tmp;
                            }else{
                                throw tmp.exception("scope必须是id类型");
                            }
                            if (arg_names!=null){
                                name_of_args=arg_names.First();
                                arg_names=arg_names.Rest();
                                if (arg_names!=null){
                                    tmp=arg_names.First();
                                    if (tmp instanceof IDExp){
                                        name_of_this= (IDExp) tmp;
                                    }else{
                                        throw name_of_this.exception("this必须是id类型");
                                    }
                                }
                            }
                        }
                    }
                }
                return new UserReadMacro(
                        bracketExp.children.First(),
                        scope,
                        name_of_scope,
                        name_of_args,
                        name_of_this,
                        args.Rest()
                );
            }else{
                throw arg_name_exp.exception("参数绑定必须是列表类型");
            }
        }
    }
}
