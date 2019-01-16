package s.exp;

import s.Exp;
import s.Token;
import mb.RangePathsException;

public abstract class AtomExp extends Exp {
    private Token block;
    public AtomExp(Token block) {
        this.block=block;
    }
    public Token getBlock(){
        return block;
    }

    @Override
    public void buildString(StringBuilder sb) {
        sb.append(block.getContent());
    }

    @Override
    public RangePathsException exception(String msg) {
        return new RangePathsException(
                block.getBegin(),
                block.getBegin()+block.getContent().length(),
                toString()+"=>"+msg);
    }

    @Override
    public void warn(String msg) {
        System.out.println(
                "("+block.getBegin()+":"+block.getBegin()+block.getContent().length()+")"+
                        toString()+"=>"+msg
        );
    }
}
