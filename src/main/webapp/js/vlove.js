$(document).ready(function() {
	document.getElementsByTagName('body')[0].onclick = clickFunc;
	Wicket.Ajax.registerPreCallHandler(showBusySign);
	Wicket.Ajax.registerPostCallHandler(hideBusySign);
	Wicket.Ajax.registerFailureHandler(hideBusySign);
});

function clickFunc(eventData) {
	var clickedElement = (window.event) ? event.srcElement : eventData.target;
	if (clickedElement.tagName.toUpperCase() == 'BUTTON'
			|| clickedElement.tagName.toUpperCase() == 'A'
			|| clickedElement.parentNode.tagName.toUpperCase() == 'A'
			|| (clickedElement.tagName.toUpperCase() == 'INPUT' && (clickedElement.type
					.toUpperCase() == 'BUTTON' || clickedElement.type
					.toUpperCase() == 'SUBMIT'))) {
		showBusySign();
	}
}

function hideBusySign() {
	document.getElementById('busy_indicator').style.display = 'none';
}

function showBusySign() {
	document.getElementById('busy_indicator').style.display = 'inline';
}

function groupRequired(id, message) {
	$('#'+id).find(':radio').each(function() {
		$(this).addClass('required');
		$('#'+id+'err').remove();
		$(this).after('<div class="calloutContainer" id="'+id+'err"><div class="calloutContent">'+message+'</div><div class="calloutLeft"><div class="calloutLeft2"></div></div>');
		var o = $(this).offset();
		$('#'+id+'err').css({'left':o.left,'top':o.top-15});
	});
}

function inputRequired(id, message) {
	clearError(id);
	
	var i = $('#'+id);
	var e = $('#'+id+'err');
	e.remove();
	
	i.removeClass('valid');
	i.addClass('required');
	
	if (message != null && !message == '') {
		var sibs = i.siblings(':input');
		if (sibs.length == 0) {
			i.after('<div class="calloutContainer" id="'+id+'err"><div class="calloutContent">'+message+'</div><div class="calloutLeft"><div class="calloutLeft2"></div></div>');
		} else {
			i.after('<div class="calloutTopContainer" id="'+id+'err"><div class="calloutTopContent">'+message+'</div><div class="calloutTop"><div class="calloutTop2"></div></div>');
		}
		
		var e = $('#'+id+'err');
		if (sibs.length == 0) {
			var o = i.offset();
			e.css({'left':o.left+220,'top':o.top});
		} else {
			var o = i.position();
			e.css({'left':o.left-i.width(),'top':o.top+35});
		}
		e.fadeIn(1000);
	} else {
		$('#'+id+'.ui-checkbox').addClass('ui-checkbox-state-error');
	}
}

function clearError(id) {
	$('#'+id).removeClass('required');
	$('#'+id).addClass('valid');
	$('#'+id+'err').remove();
	$('#'+id+'.ui-checkbox').removeClass('ui-checkbox-state-error');
}