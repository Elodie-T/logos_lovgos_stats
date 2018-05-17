("use strict");

$(function(){
	
	
	$(".zone-drag-drop").each(function(){
		
		var phrase = $(this).find(".phrase").text();
		let tmp = "";
		let decoupe = phrase.split("((trou))");
		for(let i = 0 ; i < decoupe.length ; i++) {
			if(i == decoupe.length - 1) {
				tmp +="<span>" + decoupe[i] + "</span>";	
			} else {
				tmp +="<span>" + decoupe[i] + "</span>((trou))";
			}
			
		}
		phrase = tmp;
		$(this).find(".inputs-drop .drop").each(function(){
			console.log( $(this).html() );
			
			phrase = phrase.replace("((trou))",$(this).html());
			$(this).remove();
			
			
			
		});
		
		
			
		$(this).find(".phrase").html(phrase);	
		$(this).find(".wrap-input-drag").draggable({
			revert:true
		});
		$(".wrap-input-drop").droppable({
			drop:function(event,ui){
				$(this).append(ui.draggable[0]);
				$(this).find('input').val(ui.draggable.text());
		
				$(ui.draggable[0]).css({position: "static"});	
			}
		});
	
		
	});
	
	
	
});