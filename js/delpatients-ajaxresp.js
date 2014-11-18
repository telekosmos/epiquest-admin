


/***************************************************************************
 *************************************************************************** 
 * AJAXRESPONSE INNERCLASS TO RESPONDE THE AJAX REQUESTS TO PERFORM
 * CRUD OPERATIONS
 */	

var DelPatsAjaxResponse = function () {

  var clearIntrvCombo = function () {
    var intrvSel = $("#frmListPats");

    $(intrvSel).empty();
  }


  var addOption = function (id, name) {
    var intrvSel = $("#frmListPats");

    $(intrvSel).append("<option value=\""+id+"\">"+name+"</option>");
  }


  var remarkDiv = function(msgDiv) {
    msgDiv.addClass('operation-done');
    setTimeout(function() {
      msgDiv.removeClass('operation-done');
    }, 500);
  };

  var displayMsg = function(jResponse) {
    var numOfDeleted = jResponse.deletions;
    var patsDeleted = jResponse.patients_deleted; // an array!!
    var sim = jResponse.sim;

    var patsWithSamples = jResponse.pats_with_samples;
    var date = new Date();
    var timestamp = '['+date.toLocaleTimeString()+", "+date.toLocaleDateString()+']';
    var innerContent = "<p>" + timestamp +"<br/>";
    innerContent += sim? "(<strong>Simulation</strong> update) ": "(<strong>Live</strong> update) ";
    innerContent += numOfDeleted + " subjects where removed<br/>";
    innerContent += patsWithSamples.length + " patients with samples were found and not deleted<br/>";
    innerContent += "<ul>";
    for (var i=0; i<patsWithSamples.length; i++) {
      var patInfo = patsWithSamples[i];
      var samples = patInfo.samples;

      innerContent += "<li><strong>"+patInfo.patient_code+"</strong> (samples: ";
      for (var j=0; j<samples.length; j++)
        innerContent += samples[j].sample_code+",";

      innerContent = innerContent.substring(0, innerContent.length-1);
      innerContent += ")</li>"
    }
    innerContent += '</ul><hr style="border-color: #000000">';

    var responseDiv = $("#responseDiv");
    responseDiv.append(innerContent);
    responseDiv.animate({
      scrollTop: responseDiv[0].scrollHeight
    }, "fast");
    remarkDiv(responseDiv);

    return jResponse;
  }


/**
 * Get the interviews for the current group and the current project and fills
 * the source questionnaire combobox
 * @param {Object} o, the response json object
 */
	var onGetSubjects = function (o) {
		
		// $("#frmQuestionnaire").find('option').remove();
		overlay.hide ();
		try {
			var jResponse = YAHOO.lang.JSON.parse(o.responseText);
			var arrSubjects = jResponse.subjects;
			var res = arrSubjects.length > 0;
			clearIntrvCombo ();
			if (res) {
				// $("#frmListPats").append('<option value="-1">Choose...</option>');
				
				for (i=0; i<arrSubjects.length; i++) {
					var pat = arrSubjects[i];
					var name = pat.codpatient;
					var id = pat.id;
					
					addOption(name, name);
				}
			}
		}
		catch (exp) {
			alert ("JSON Parse failed: "+exp);
			return;
		}
		
	}
	
	
	
/**
 * Get the sections for the current interview and fills the sections combobox. 
 * @param {Object} o, the response json object
 */
	var onGetHospitals = function (o) {
		
		overlay.hide ();
		try {
			var jResponse = YAHOO.lang.JSON.parse(o.responseText);
      
			var arrGroups = jResponse.groups;
			var res = arrGroups.length > 0;
			var hospCombo = $("#frmHospital");
			$(hospCombo).empty();
			
			if (res) {
				$(hospCombo).append('<option value="-1">Choose...</option>');
				// addOption (-1, "Choose a questionnaire");
				for (i=0; i<arrGroups.length; i++) {
					var hosp = arrGroups[i];
					var name = hosp.name;
					var id = hosp.id;
					var code = hosp.code;
					
					$(hospCombo).append("<option value=\""+code+"\">"+decodeURIComponent(name)+"</option>");
				}
			}
		}
		catch (exp) {
			alert ("JSON Parse failed: "+exp);
			return;
		}
		
	}
	
	
	
	/**
	 * Callback function on delete patients operation is complete. Refresh a div
	 * on the page customizing the following piece of html:
	 * 23 subjects where removed<br/>
	 * 	2 patients with samples were found and not deleted<br/>
	 * 	<ul>
	 * 	<li><strong>188011009</strong> (samples 18801100900, 18801100901)</li>
	 * 	<li><strong>188011320</strong> (samples 18801132000, 18801132001, 18801132002, 18801132005)</li>
	 *	</ul>
	 * @param {Object} o
	 */
	var onDelPatients = function (o) {
		overlay.hide ();
		try {
			var jResponse = YAHOO.lang.JSON.parse(o.responseText);
      /*
			var numOfDeleted = jResponse.deletions;
			var patsDeleted = jResponse.patients_deleted; // an array!!
			var sim = jResponse.sim;
			
			var patsWithSamples = jResponse.pats_with_samples;
			var innerContent = sim? "(<strong>Simulation</strong> update) ": "(<strong>Live</strong> update) ";
			innerContent += numOfDeleted + " subjects where removed<br/>";
	  	innerContent += patsWithSamples.length + " patients with samples were found and not deleted<br/>";
			innerContent += "<ul>";
			for (i=0; i<patsWithSamples.length; i++) {
				var patInfo = patsWithSamples[i];
				var samples = patInfo.samples;
				
				innerContent += "<li><strong>"+patInfo.patient_code+"</strong> (samples: ";
				for (j=0; j<samples.length; j++)
					innerContent += samples[j].sample_code+",";
					
				innerContent = innerContent.substring(0, innerContent.length-1);
				innerContent += ")</li>"
			}
			innerContent += "</ul>"; 
	  	
			$("#responseDiv").empty();
			$("#responseDiv").append(innerContent);
			*/
      displayMsg(jResponse);
		
		}
		catch (exp) {
			alert ("JSON Parse failed: "+exp);
			return;
		}
		
	}
	
	
	
	return {
		onGetSubjects: onGetSubjects,
		onGetHospitals: onGetHospitals,
		onDelPatients: onDelPatients		
	}
	
};
/// AJAXRESPONSE /////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////
