$(function (){
	$(".valider").on("click", function(e){
		e.preventDefault();
		var mdp = $(".mdp").val();
		var mdp2 = $(".mdp2").val();
		var log = $(".login").val();
		var mail = $(".mail").val();
		var regexMail =/[a-zA-Z0-9]+@[a-zA-Z0-9]+\.[a-zA-Z0-9]+/.test(mail);
		var regex = /[a-z]{1,}[A-Z]{1,}[0-9]{1,}/.test(mdp);
		if(!regexMail){
			$(".messageMail").text("mail invalide");
		}
		if(log.length<1){
			$(".messageLogin").text("login invalide");
		}
		if(regex){
			if(mdp.length>=8){
				if(mdp=== mdp2){
					$(".submit-jsf").trigger("click");
				} else{
					$(".messageMdp").text("erreur dans le mot de passe");
				}
			}else{
				$(".messageMdp").text("mot de passe doit comporter au minimum 8 caractère");
			}

		}else{
			$(".messageMdp").text("mot de passe invalide");
		}
	

	});
});
