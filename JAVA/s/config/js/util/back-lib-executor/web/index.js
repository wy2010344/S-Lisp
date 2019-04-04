({
    success:function(prun){
	    var parseJSON=function(str){
	        return JSON.parse(str.replace(/&quot;/g,"\""));
	    };
	    var webPackageDeal=function(pkg){
	        if(pkg.body.out){
	            return pkg.body.success;
	        }else{
	            return null;
	        }
	    };
	    return function(map){
	        /**
	         * 新式的调用JS
	         */
	        var servlet=prun(map);
	        var url_prefix=""+map.get("url_prefix");
	        
	        var log_prefix="js";
	        if(url_prefix!="/"){
	            log_prefix=url_prefix.substring(0,url_prefix.indexOf("/"));
	        }
	        var log=ini.get("me").getLogger(log_prefix);
	        
	        var request=(function(){
	            var url=decodeURI((""+servlet.getUrl()));
	            var act=url.substring(url.indexOf(url_prefix)+url_prefix.length,url.length);
	            map.put("log",log);
	            var me={
	                getAct:function(){
	                    return act;
	                },
	                original:servlet.original,
	                session:servlet.session //不同项目不同实现
	            };
	            me.p_str=function(key){
	                var v=servlet.p_str(key);
	                if(v){
	                    return ""+v;
	                }else{
	                    return null;
	                }
	            };
	            me.p_strs=function(key){
	                //返回多维数组
	                var vs=servlet.p_strs(key);
	                var ret=[];
	                for(var i=0;i<vs.length;i++){
	                    ret.push(""+vs[i]);
	                }
	                return ret;
	            };
	            me.p_json=function(key){
	                var v=me.p_str(key);
	                if(v==null){
	                    return v;
	                }else{
	                    return parseJSON(v);
	                }
	            };
	            me.p_jsons=function(key){
	                var vs=me.p_strs(key);
	                for(var i=0;i<vs.length;i++){
	                    vs[i]=parseJSON(vs[i]);
	                }
	                return vs;
	            };
	            servlet.request(me);//扩展实现
	            return me;
	        })();
	        
	        var response=(function(){
	            var isWrite=false;
	            var write=function(txt){
	                isWrite=true;
	                servlet.send(txt);
	            };
	            var writeJSON=function(obj){
	                write(JSON.stringify(obj));
	            };
	            var me={
	                json:function(o){
	                    writeJSON({
	                        code:0,
	                        description:"操作成功",
	                        data:o
	                    });
	                },
	                success:function(msg){
	                    writeJSON({
	                        code:0,
	                        description:msg||"操作成功"
	                    });
	                },
	                error:function(msg,code){
	                    writeJSON({
	                        code:code||-1,
	                        description:msg
	                    });
	                },
	                text:function(txt){
	                    write(txt);
	                },
	                isWrite:function(){
	                    return isWrite;
	                }
	            };
	            //扩展实现
	            servlet.response(me,function(v){
	                isWrite=v;
	            })
	            return me;
	        })();
	        /**
	         * 为什么要将request/response作为单独的参数传递而不是全局的常量？
	         * 未来可能做到js不是处理单线程，而是web服务本身的多线程。
	         * request还带有session，可以查询操作员、权限
	         * web本身是无状态的
	         */
	        var doServlet=function(route,str){
	            var will=true;
	            route(request,response);
	            if(response.isWrite()){
	                will=false;
	            }
	            return will;
	        };
	        var path=request.getAct();
	        var nodes=path.split("/");
	        var substr="";
	        var will=true;
	        var has=false;
	        var already=[];
	        var work_base_path=map.get("work_base_path");
	        if(work_base_path!=""){
	            work_base_path="/"+work_base_path;
	        }
	        already.push("act/web"+work_base_path);
	        var log=map.get("log")||mb.log;
	        try{
	            while(nodes.length!=0 && will){
	                already.push(nodes.shift());
	                var substr=already.join("/");
	                /**
	                 * //先查找代表目录的文件，如果有，一般都不用继续查找，因为它是文件夹不是叶子文件。
	                 */
	                var route=mb.getLib(substr+"/_.js",webPackageDeal);
	                if(route){
	                    /**
	                     * 可能是过滤器，作为枝节点，但并无处理，转由下一个处理。
	                     */
	                    will=doServlet(route);
	                }else{
	                    /**
	                     * 叶子结点，如果有与文件夹同名的怎么办？有点该着被同名文件拦截了的味道，本来同名文件和文件夹并不冲突，因为有js后缀，同外文件更应该作为根结点。
	                     */
	                    route=mb.getLib(substr+".js",webPackageDeal);
	                    if(route){
	                        has=true;
	                        will=doServlet(route);
	                    }
	                }
	            }
	            if(will){
	                //没有找到
	                if(has){
	                    log.error("经过具体页面，但并未处理");
	                }
	                //有过默认的处理方法，可能是根处理器，还是不返回吧，报个404，当未处理。
	                response.error("未找到对应处理的方法",404);
	            }
	        }catch(ex){
	            log.error(ex);
	            response.error(""+ex,500);
	        }
	    }
    }
})