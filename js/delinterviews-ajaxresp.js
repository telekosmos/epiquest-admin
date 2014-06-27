


/***************************************************************************
 *************************************************************************** 
 * AJAXRESPONSE INNERCLASS TO RESPONDE THE AJAX REQUESTS TO PERFORM
 * CRUD OPERATIONS
 */	

var DelIntrvsAjaxResponse = function () {




	var addOption = function (id, name) {
		$("#frmListIntrvs").append('<option value="'+id+'">'+name+'</option>');		
	}
	
	
	var onGetInterviews = function (o) {
		overlay.hide();
		$("#frmListIntrvs").empty();
		try {
			var jResponse = YAHOO.lang.JSON.parse(o.responseText);
			var arrayIntrvs = jResponse.interviews;
			var res = arrayIntrvs.length > 0;
			if (res) {
				// $("#frmListPats").append('<option value="-1">Choose...</option>');
				
				for (i=0; i<arrayIntrvs.length; i++) {
					var intrv = arrayIntrvs[i];
					var name = intrv.name;
					var id = intrv.id;
					
					addOption(id, name);
					console.log("addOption("+id+","+name+")");
				}
			}
		}
		catch (exp) {
			alert ("JSON Parse failed: "+exp);
			return;
		}
	}
	
	
	
	var onResponseDelete = function (o) {
		overlay.hide();
		try {
			var jResponse = YAHOO.lang.JSON.parse(o.responseText);
			console.log ("interviews deleted...:");
			
			var intrvsList = jResponse.interviews_deleted;
			var codPat = (intrvsList.length > 0)? intrvsList[0].codpat: "";
			var samples = jResponse.samples;
			var sim = jResponse.sim;
			
			
			var innerHtml = "<p>" + sim? "(<strong>Simulation</strong> update)": "(<strong>Live</strong> update)"; 
			innerHtml += (codPat == "")? "": " For patient <strong>"+codPat+"</strong></p>";
			innerHtml += "<ul><li>Interviews deleted: "+intrvsList.length;
      for (var i=0; i<intrvsList.length; i++)
        innerHtml += "'<i>"+intrvsList[i].intrv+"</i>', ";

				
			innerHtml = (intrvsList.length > 0)? innerHtml.substring(0, innerHtml.length-2): innerHtml;
			innerHtml += "</li><li>";
			if (samples.length == 0)
				innerHtml += "No samples for this patient";
				
			else {
				innerHtml += "Samples (when '<i>QES*</i>' questionnaire): ";
				for (var i=0; i<samples.length; i++)
					innerHtml += "'"+samples[i]+"', ";
					
				innerHtml = innerHtml.substring(0, innerHtml.length-2);
			}
			innerHtml += "</li></ul>";
			
			$("#responseDiv").empty();
			$("#responseDiv").append(innerHtml);	
			
		}
		catch (exp) {
			alert ("JSON Parse failed: "+exp);
			return;
		}
		
	}
	
	
	var onFail = function (o) {
		overlay.hide();
		console.log("Ajax request failed");
	}



	var test = function (testStr) {
		var msg = testStr || 'DelIntrvsAjaxResponse instanced';
		
		console.log(msg);
	}
	
	
	return {
		onGetInterviews: onGetInterviews,
		onResponseDelete: onResponseDelete,
		onFail: onFail,
		
		test: test
	}
	
};
/// AJAXRESPONSE /////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////
