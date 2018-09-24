package s.exp;
public abstract class Exp {
	public static enum Exp_Type{
		ID,
		String,
		Int,
		Call,//()
		List,//[]
		Function,//{}
		Let,//(let a b c d)
		Let_ID,//a
		Let_Bra,//(a b ...c)
		Let_Rest_ID//...x
	}
	public abstract boolean isBracket();
	public abstract s.Location Loc();
	public abstract Exp_Type xtype();
    public static void repeat(StringBuilder sb,int indent){
        int i=0;
        while(i<indent){
            sb.append("  ");
            i++;
        }
    }
    public BracketsExp parent;
    //不换行，组合
    protected abstract void toString(StringBuilder sb);
    //换行
    public abstract String toString(int indent);
    //换行，组合
	protected abstract void toString(StringBuilder sb,int indent);
	public abstract String to_value();
}
