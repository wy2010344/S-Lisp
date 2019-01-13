package mb;

public interface Logger {
    void debug(String msg, Object... args);
    void info(String msg, Object... args);
    void warn(String msg, Object... args);
    void error(String msg, Object... args);
    public static class DefaultLogger implements Logger {
        private final String name;
        private DefaultLogger(String name){
            this.name=name;
        }
        public static DefaultLogger getLogger(String name){
            return new DefaultLogger(name);
        }
        private String buildString(String type,String msg,Object ...args){
            return type+":"+msg+args;
        }
        @Override
        public void info(String msg,Object ...args){
            System.out.println(buildString("info",msg,args));
        }
        @Override
        public void error(String msg,Object ...args){
            System.err.println(buildString("error",msg,args));
        }
        @Override
        public void warn(String msg,Object ...args){
            System.out.println(buildString("warn",msg,args));
        }
        @Override
        public void debug(String msg,Object ...args){
            System.out.println(buildString("debug",msg,args));
        }
    }
}
