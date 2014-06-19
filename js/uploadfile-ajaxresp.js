

var UploadFileAjaxResponse = function() {

  var onFail = function (o) {
// Access the response object's properties in the
// same manner as listed in responseSuccess( ).
// Please see the Failure Case section and
// Communication Error sub-section for more details on the
// response object's properties.
    var msg = o.responseText;

//		document.getElementById("body").appendChild(document.createTextNode(msg));
    console.error(msg);
  };


  /**
   * Display a message in the display messages area with the number of the codes
   * changed, the subject codes, filename and subjects with samples.
   *
   * @param o the server object response with all codes changed. The structure of the json object returned is:
   * {
   *  filename
   *  result
   *  rows_affected
   *  simulation
   *  subjects: [{old_code: new_code},...,{old_code: new_code}]
   *  subjects_unchanged
   *  subjs_with_samples
   * }
   */
  var displayMsg = function(o) {
    var innerContent = "", count=0;
    var subjectsChanged = o.subjects;
    $.each(subjectsChanged, function(i, pair) {
      $.each(pair, function(key, val) {
        innerContent += '<li>'+key+' -> '+val+'</li>';
        count++;
      });
    });

    var simString = o.sim == true? '<b>Simulation</b> mode<br/>': '<b>Live</b> mode<br/>';
    var date = new Date();
    var timestamp = '['+date.toLocaleTimeString()+", "+date.toLocaleDateString()+']';
    innerContent = timestamp +'<br/>'+ simString + count + " subject codes found in file:<br/><ul>"+innerContent+"</ul>";
    innerContent += "Subjects affected: "+ o.rows_affected+"<br/>";

    var subjectsWithSamples = '';
    $.each(o.subjs_with_samples, function(i, pair) {
      $.each(pair, function(key, val) {
        subjectsWithSamples += '<li>'+key+'</li>';
        count++;
      });
    });
    subjectsWithSamples = (subjectsWithSamples == "")? "None": subjectsWithSamples;
    innerContent += "Subjects with samples: "+ subjectsWithSamples+"<br/>";

    var subjectsUnchanged = '';
    $.each(o.subjects_unchanged, function(i, pair) {
      $.each(pair, function(key, val) {
        subjectsUnchanged += '<li>'+key+'</li>';
        count++;
      });
    })
    subjectsUnchanged = (subjectsUnchanged == "")? "None": subjectsUnchanged;
    innerContent += 'Subjects unchanged: <ul>' + subjectsUnchanged +'</ul><hr style="border-color: #000000">';

    // $("#responseDiv").empty();
    $("#responseDiv").append(innerContent);
    // $("#responseDiv").html(innerContent);
  }

  var onFileUpload = function(o) {
    console.log('File upload OK: '+o.filename);
    var re = /^[a-zA-Z0-9]{3}(\d){6,9}$/g;
    for (var key in o)
      if (key.match(re) != null)
        console.log(key+':'+o[key]);

    displayMsg(o);
    overlay.hide();
  }

  return {
    onFileUpload: onFileUpload,
    onFail: onFail
  }
}
