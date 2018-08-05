var lib=require("./lib.js");
var fs=require("fs");
var iconv = require('iconv-lite');
lib.clone({
	fromPath:lib.gitPath,
	toPath:lib.vsPath,
	make:function(filedir,targetDir) {
		var txt=fs.readFileSync(filedir);
		txt=iconv.decode(txt,"utf8");
		txt=iconv.encode(txt,"gbk");
		fs.writeFileSync(targetDir,txt);
	}
});