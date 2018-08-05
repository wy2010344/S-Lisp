var fs=require("fs");
var path = require('path');  
var white_suffixs=[
	".h",
	".lisp",
	".txt",
	".js",
	".cpp"
];
module.exports={
	gitPath:"",
	vsPath:"",
	/*
	fromPath
	toPath
	make
	*/
	clone:function(p) {
		if(p.formPath=="")
		{
			console.log("源文件夹路径为空");
			return;
		}
		if(p.toPath=="")
		{
			console.log("目标文件夹路径为空");
			return;
		}
		var change=function(filePath,targetPath) {
			fs.readdir(filePath,function(err,files) {
				if(err){
					console.warn(err);
				}else{
					files.forEach(function(fileName) {
						var filedir=path.join(filePath,fileName);
						var targetDir=path.join(targetPath,fileName);
						fs.stat(filedir,function(err,stats) {
							if(err){
								console.warn(err);
							}else{
								if(stats.isFile()){
									//是文??
									var bool=false;
									white_suffixs.forEach(function(v){
										if(filedir.endsWith(v)){
											bool=true;
										}
									});
									if(bool){
										p.make(filedir,targetDir);
									}else{
										console.log(filedir);
									}
								}else{
									if(!fs.existsSync(targetDir)){
										fs.mkdirSync(targetDir);
									}
									change(filedir,targetDir);
								}
							}
						});
					});
				}
			});
		};
		change(p.fromPath,p.toPath);
	}
}