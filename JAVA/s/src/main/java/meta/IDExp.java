package meta;

import mb.RangePathsException;

public class IDExp extends Exp {
    private final Token token;
    public final String value;
    public IDExp(Token token){
        this.token=token;
        this.value=token.value;
    }
    private Node<String> ids;
    public Node<String> getIds() throws RangePathsException {
        if (ids==null){
            ids=getIds(token,value);
        }
        return ids;
    }
    private static Node<String> getIds(Token block, String id_path) throws RangePathsException {
        if (id_path.charAt(0)=='.'||id_path.charAt(id_path.length()-1)=='.'||id_path.indexOf("..")!=-1){
            throw block.exception("不是合法的id类型");
        }else {
            String[] ids=id_path.split("\\.");
            return (Node.list(ids));
        }
    }

    @Override
    public RangePathsException exception(String msg) {
        return token.exception(msg);
    }

    @Override
    protected void toString(StringBuilder sb) {
        sb.append(token.value);
    }
    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        toString(sb);
        return sb.toString();
    }
}
