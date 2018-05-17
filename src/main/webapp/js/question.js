("use strict");

$(function(){
	$(".zone-phrase-trous").each(function(){
		var phrase = $(this).find(".phrase").text();
		$(this).find(".inputs .wrap-input").each(function(){
			console.log( $(this).html() );
			phrase = phrase.replace("((trou))",$(this).html());
			$(this).remove();
		});
		$(this).find(".phrase").html(phrase);
	});
	
	
});