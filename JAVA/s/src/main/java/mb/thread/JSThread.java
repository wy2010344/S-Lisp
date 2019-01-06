package mb.thread;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 模拟js的单线程
 * 事件循环
 *
 */
public class JSThread {
    /*
    *服务端异步完成事件
    */
    static abstract class TAsyncCall<T>{
        /**
         * 如果真允许异步，这个可能要锁，误多写?
         */
        private volatile int time=0;
        /*
        执行结束，将自己写入队列，如果队列为空，则重启主线程
        需要纯异步，如果没有写回，就一直不写回
        * */
        public void write(T v){
            time++;
            if (time==1){
                write_once(v);
            }else{
                onwritemore(time);
            }
        };
        protected abstract void onwritemore(int time);

        /**
         * 是否已经写了
         * @return
         */
        public boolean isWrite(){
            return time!=0;
        }
        protected abstract void write_once(T v);
    }


    static interface TEvent{
        void success();
    }

    private LinkedBlockingQueue<TEvent> chain=new LinkedBlockingQueue<TEvent>();/*事件队列*/

    /**
     * 追加事件，外部线程处理的
     * @param event
     */
    public void addEvent(TEvent event){
        chain.add(event);
        if (!on){
            /*主事件循环停止了，从本线程启动*/
            circle();
        }
    }
    /**
     * 主事件循环
     */
    private void circle(){
        on=true;
        while (!chain.isEmpty()){
            /*
            不空的时候，将取队列顺序执行
            */
            chain.remove().success();
        }
        on=false;
    }
    private volatile boolean on=true;/*会被多线程读，保证实时性*/

    public JSThread(Start r){
        final Executor executor=new Executor(this);
        r.run(executor);
        circle();
    }

    public static abstract class Param_Async<T>{

        public static class MulTimeException extends Exception{
            public MulTimeException(String msg){
                super(msg);
            }
        }
        /**
         * 在另一个线程里执行
         * 这个线程执行完后如果发现主线程没工作，就充当主线程。
         * @param executor
         * @param call
         */
        public abstract void run(Executor executor,TAsyncCall<T> call) throws Throwable;

        /***
         * 主线程轮循到时执行
         * @param executor
         * @param value
         */
        public abstract void success(Executor executor,T value);

        /**
         * 异常，默认空
         * @param cause
         */
        protected void exception(Throwable cause){

        }

        /**
         * 在写成功之后的所有异常
         * @param cause
         */
        protected void exception_after(Throwable cause){

        }
    }

    public static abstract class Param_Sync<T>{
        public abstract T run(Executor executor)throws Throwable;
        public abstract void success(Executor executor,T value);
        public void exception(Throwable cause){}
    }
    /**
     * 启动器
     */
    public static class Executor{
        public Executor(JSThread thread){
            this.thread=thread;
        }
        private JSThread thread;
        private volatile int i=0;

        /**
         * 执行异步的处理
         * @param t
         * @param <T>
         */
        public <T> void run(final Param_Async<T> t){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    TAsyncCall<T> call=new TAsyncCall<T>(){
                        @Override
                        protected void write_once(final T v) {
                            thread.addEvent(new TEvent() {
                                @Override
                                public void success() {
                                    t.success(Executor.this,v);
                                }
                            });
                        }

                        @Override
                        protected void onwritemore(int time) {
                            t.exception_after(new Param_Async.MulTimeException("写了第"+time+"次"));
                        }
                    };
                    try {
                        t.run(Executor.this, call);
                    }catch (Throwable throwable){
                        if (call.isWrite()){
                            /*在成功之后的异常*/
                            t.exception_after(throwable);
                        }else{
                            /*在成功之前的异常*/
                            t.exception(throwable);
                        }
                    }
                }
            },"JSThread-"+(i++)).start();/*立即执行*/
        }

        /**
         * 执行同步的处理
         * @param t
         * @param <T>
         */
        public <T> void run(final Param_Sync<T> t){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        final T v=t.run(Executor.this);
                        thread.addEvent(new TEvent() {
                            @Override
                            public void success() {
                                t.success(Executor.this,v);
                            }
                        });
                    }catch (Throwable cause){
                        t.exception(cause);
                    }
                }
            },"JSThread-"+(i++)).start();
        }
    }
    public static interface Start{
        void run(Executor exe);
    }
    public static void log(String msg){
        System.out.println(msg+":"+Thread.currentThread().getId()+":"+Thread.currentThread().getName());
    }
    public static void main(String[] args){
        final Param_Async ap=new Param_Async<String>(){
            @Override
            public void run(Executor executor1,JSThread.TAsyncCall<String> call) {
                log("我是第一次输出");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log("线程1执行完成");
                call.write("我是第一次输出");
            }

            @Override
            public void success(Executor executor1,final String v1) {
                executor1.run(new Param_Async<String>(){
                    @Override
                    public void run(Executor executor2,JSThread.TAsyncCall<String> call) {
                        log("进入第二个线程");
                        call.write(v1+"->我是第二次输出");
                    }

                    @Override
                    public void success(Executor executor2,String v2) {
                        log(v2+"->不再向下执行");
                    }
                });
            }
        };
        new JSThread(new Start() {
            @Override
            public void run(Executor executor) {
                log("主线程");
                for (int i=0;i<10;i++) {
                    log("执行任务"+i);
                    executor.run(ap);
                }
            }
        });
    }
}
