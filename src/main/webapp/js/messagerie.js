$(function (){
	
	
	setInterval(function(){
		console.log('fx timeout')
		$(".btn-rafraichir").trigger("click");
	
	},2800);
	
	
});
