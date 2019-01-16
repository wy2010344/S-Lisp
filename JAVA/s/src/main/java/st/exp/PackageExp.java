package st.exp;

import java.util.ArrayList;
import java.util.List;

public abstract class PackageExp {
    private List<ImportExp> imports=new ArrayList<ImportExp>();
    public List<ImportExp> getImports(){
        return imports;
    }
}
