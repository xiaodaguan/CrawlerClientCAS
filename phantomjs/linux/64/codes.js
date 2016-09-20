system = require('system')
address = system.args[1];//获得命令行第二个参数 接下来会用到
//console.log('Loading a web page');
var page = require('webpage').create();
var url = address;
//console.log(url);
page.open(url, function (status) {
    //Page is loaded!
    if (status !== 'success') {
        console.log('Unable to post!');
    } else {
        //console.log(page.content);
		//var title = page.evaluate(function() {
		//	return document.title;//示范下如何使用页面的jsapi去操作页面的
		//	});
		//console.log(title);

		console.log(page.content);
	}
    phantom.exit();
});