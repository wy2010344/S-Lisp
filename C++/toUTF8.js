var lib=require("./lib.js");
var fs=require("fs");
var iconv = require('iconv-lite');
lib.clone({
	fromPath:lib.vsPath,
	toPath:lib.gitPath,
	make:function(filedir,targetDir) {
		var txt=fs.readFileSync(filedir);
		txt=iconv.decode(txt,"gbk");
		txt=iconv.encode(txt,"utf8");
		fs.writeFileSync(targetDir,txt);
	}
});