/***************************************************************************
 *************************************************************************** 
 * AJAXRESPONSE INNERCLASS TO RESPONDE THE AJAX REQUESTS TO PERFORM
 * CRUD OPERATIONS
 */	

var AjaxResponse = function () {


var groupPrimary = 1
var groupSecondary = 2

/**
 * Response method on password reset for one user. This method just get the
 * json response and raise an alert dialog to show the result message
 */
	var onResetPasswd = function (o) {
		overlay.hide();
    try {
			var jResponse = YAHOO.lang.JSON.parse(o.responseText);
      
			if (jResponse) {
        var res = jResponse.res;
        var msg = jResponse.msg;
        
        alert (msg);
			}
		}
		catch (exp) {
			alert("JSON Parse failed!: "+o.responseText); 
	    return;
		}
	};




/**
 * Reset parameters from register form. This is a convinient private method to
 * be run after an user registration
 */
  var resetForm = function () { 
    document.getElementById('username').value = '';
    document.getElementById('password').value = '';
		document.getElementById('firstname').value = '';
		document.getElementById('lastname').value = '';
    document.getElementById('re-password').value = '';
    document.getElementById('email').value = '';
//    document.getElementById('registered_role').options[0].selected = true;


		var aSelectedRoleList = document.getElementById('selected_role').options;
    for (var iIndex = 0; iIndex < aSelectedRoleList.length; iIndex++) {
      aSelectedRoleList[iIndex].selected = true;
    }
    moveOptions(document.getElementById('selected_role'), 
                document.getElementById('registered_role'));
    
    var aSelectedGroupList = document.getElementById('selected_group').options;
    for (var iIndex = 0; iIndex < aSelectedGroupList.length; iIndex++) {
      aSelectedGroupList[iIndex].selected = true;
    }
    moveOptions(document.getElementById('selected_group'), 
                document.getElementById('registered_group'));
    
    var aSelectedProjectList = document.getElementById('selected_project').options;
    for (var iIndex = 0; iIndex < aSelectedProjectList.length; iIndex++) {
      aSelectedProjectList[iIndex].selected = true;
    }
    moveOptions(document.getElementById('selected_project'), 
                document.getElementById('registered_project'));
                
    document.getElementById('frmGrpName').value = 'New group name';
    document.getElementById('frmPrjName').value = 'New project name';
		
		document.getElementById('frmGrpParent').disabled = "disabled";
  }
	
  
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
 * Ajax response handler for adding and updating an user 
 */	
	var onResponse = function (o) {
    overlay.hide ();
		try {
			var jResponse = YAHOO.lang.JSON.parse(o.responseText);
      var res = jResponse.type;
      
      if (res == "error") {
        alert (jResponse.message);
        return;
      }
      else {
        var name = jResponse.name;
        var idUsr = jResponse.id;
        var roles = jResponse.roles;
        var optTxt = name + " ("+roles.toUpperCase()+")";
				var optExists = false;
				
// This is the "loop" to add/update one user from list
				$("#listUsrs > option").each (function () {
					var txt = $(this).text();
					var parenthesis = txt.indexOf("(");
					var thisName = txt.substring(0, parenthesis-1);
					if (thisName == name) {
						$(this).text(optTxt);
						optExists = true;
						return false;
					}
/*					
					else {
						var newUsrOpt = new Option(optTxt, idUsr);
						var lUsrs = document.getElementById("listUsrs");
						
						lUsrs.add(newUsrOpt, null);
					}
*/					
				});
				if (!optExists) {
					var newUsrOpt = new Option(optTxt, idUsr);
					var lUsrs = document.getElementById("listUsrs");
					lUsrs.add(newUsrOpt, null);
					/*
					var jqUserOpts = $('#listUsrs > option');
					$(jqUserOpts).click (function (ev) {
						var opt = ev.target;
			//			switchBtn ();
						displayUsr(opt);
			//			sayHello ($(opt)[0].text);
					}) */
				}
        
				$('#listUsrs > option[value='+idUsr+']').css('font-weight', 'bold');
        
        alert (jResponse.message);
        resetForm();
      }
      			
		}
		catch (exp) {
			alert("JSON Parse failed!"); 
	    return;
		}
	};
  
	
	
/**
 * Callback method on responding to the addition of a new role.
 * It has to add the new role to the rolelist and open an alert
 * @param {Object} o
 */  
  var onNewRole  = function (o) {
    overlay.hide();
    try {
			var jResponse = YAHOO.lang.JSON.parse(o.responseText);
      
			if (jResponse) {
        var roleName = jResponse.name;
        var roleVal = jResponse.id;
        var res = jResponse.res;
        var msg = jResponse.msg;
        
        if (res == 1) {
  			  var listRole = document.getElementById("registered_group");
          var newRole = new Option (roleName, roleVal);
          listRole.add(newRole, null);
        }
        
        alert (msg);
			}
		}
		catch (exp) {
			alert("JSON Parse failed!: "+o.responseText); 
	    return;
		}
	};

  
  
/**
 * Callback method on responding to the addition of a new group.
 * It has to add the new group to the list of groups and open an alert
 * @param {Object} o
 */  
  var onNewGrp  = function (o) {
    overlay.hide();
    try {
			var jResponse = YAHOO.lang.JSON.parse(o.responseText);
      
			if (jResponse) {
        var grpName = jResponse.name;
        var grpVal = jResponse.id;
        var res = jResponse.res;
        var msg = jResponse.msg;
        
        if (res == 1) {
  			  var listGrp = document.getElementById("registered_group");
          var newGrp = new Option (grpName, grpVal);
          listGrp.add(newGrp, null);
					
//					var listParents = document.getElementById('frmGrpParent');
//					listParents.disable();
        }
        alert (msg);
				
				document.getElementById('frmGrpParent').disabled = "disabled";
			}
		}
		catch (exp) {
			alert("JSON Parse failed!: "+o.responseText); 
	    return;
		}
	};


  /**
   * Callback method on responding to the enabling or disabling of an user.
   * @param {Object} o
   */
  var onUserDisable = function(o) {
    overlay.hide();
    try {
      var jResponse = YAHOO.lang.JSON.parse(o.responseText);

      if (jResponse) {
        var msg = jResponse.msg;
        var success = jResponse.res == 1;
        var action = jResponse.action;

        if (action.toLowerCase().indexOf('disable') != -1) {
          alert(msg);
          $('#userRemove').text('Enable');
          $('#userRemove').css('color', 'green');
        }
        else { // enable
          alert(msg);
          $('#userRemove').text('Disable');
          $('#userRemove').css('color', 'red');
        }
      }
    }
    catch (exp) {
      alert("JSON Parse failed!: "+o.responseText);
      return;
    }
  }
  
/**
 * Callback method on responding to the addition of a new project.
 * It has to add the new group to the list of groups and open an alert
 * @param {Object} o
 */  
  var onNewPrj  = function (o) {
    overlay.hide();
    try {
			var jResponse = YAHOO.lang.JSON.parse(o.responseText);
      
			if (jResponse) {
        var res = jResponse.res;
        var msg = jResponse.msg;
        var prjName = jResponse.name;
        var prjVal = jResponse.id;
        
        if (res == 1) {
  			  var listPrj = document.getElementById("registered_project");
          var newPrj = new Option (prjName, prjVal);
          listPrj.add(newPrj, null);
        }
        alert (msg);
			}
		}
		catch (exp) {
			alert("JSON Parse failed!: "+o.responseText); 
	    return;
		}
	};  
  




  var onSwitchUser = function (o) {
    overlay.hide();
    try {
			var jResponse = YAHOO.lang.JSON.parse(o.responseText);
      
			if (jResponse) {
        var res = jResponse.res;
        var msg = jResponse.msg;
        var usrId = jResponse.id;
        var usrOpt;
        if (res == 1) {
  			  var listUsrs = document.getElementById("listUsrs");
					/*
          for (i=0; i<listUsrs.length; i++) {
            if (listUsrs.options[i].value == usrId) {
              usrOpt = i;
              break;
            }
          }
          listUsrs.remove (usrOpt);
          */
// This piece of code is to "reset" the form fields
// This and some other code can be suitable to be in any other common file but
// here and adminutil.js...
          document.getElementById('username').value = '';
          document.getElementById('password').value = '';
          document.getElementById('re-password').value = '';
          
          document.getElementById('registered_role').options[0].selected = true;
          
          var aSelectedGroupList = document.getElementById('selected_group').options;
          for (var iIndex = 0; iIndex < aSelectedGroupList.length; iIndex++) {
            aSelectedGroupList[iIndex].selected = true;
          }
          moveOptions(document.getElementById('selected_group'), 
                      document.getElementById('registered_group'));
          
          var aSelectedProjectList = document.getElementById('selected_project').options;
          for (var iIndex = 0; iIndex < aSelectedProjectList.length; iIndex++) {
            aSelectedProjectList[iIndex].selected = true;
          }
          moveOptions(document.getElementById('selected_project'), 
                      document.getElementById('registered_project'));
                      
          document.getElementById('frmGrpName').value = 'New group name';
          document.getElementById('frmPrjName').value = 'New project name';
        }
        alert (msg);
			}
		}
		catch (exp) {
			alert("JSON Parse failed!: "+o.responseText); 
	    return;
		}
  }
  
	
  
/**
 * This method is the callback method to retrieve the data from an user. These
 * data is a json object with the form:
 * {"res":1,"msg":"","user":"username","passwd":"passwd","roles":[r1,r2,...,rn],
 * "grps":[g1,g2,...,gn],"prjs":[p1,p2,...,pn],"disable":0|1}
 * where ri, gi and pi are ids (numbers)
 * @param {Object} o
 */  
  var onGetUserData = function (o) {
    overlay.hide();
    try {
/*
 * 
{"res":1,"msg":"","username":"geruser","passwd":"germany","roles":[100],"groups":[4, 300],"prjs":[50]}
user: geruser, pass:germany, roles:[100], groups:[4,300], prjs:[50]
 */
			var jResp = YAHOO.lang.JSON.parse(o.responseText);
      
			if (jResp) {
        var res = jResp.res;
        var msg = jResp.msg;
        if (res == 1) {
					$("#frmUsrId").val(jResp.id);
					
  			  var jsonOut = "user: "+jResp.username+", pass:"+jResp.passwd+", roles:";
          for (i=0; i<jResp.roles.length; i++)
            jsonOut += jResp.roles[i]+",";
          
          jsonOut += " groups:";
          for (i=0; i<jResp.groups.length; i++)
            jsonOut += jResp.groups[i]+",";
            
          jsonOut += " prjs:";
          for (i=0; i<jResp.prjs.length; i++)
            jsonOut += jResp.prjs[i]+",";
            
/*
          var txtUsrName = document.getElementById('username');
          txtUsrName.value = jResp.username;
          
          var txtPass1 = document.getElementById('password');
          txtPass1.value = jResp.passwd;
          var txtPass2 = document.getElementById('re-password');
          txtPass2.value = jResp.passwd;
          
          var listGrp = document.getElementById('selected_group');
          var grpStore = document.getElementById('registered_group');
*/
					$('#username').val(jResp.username);
					$('#firstname').val(jResp.firstName);
					$('#lastname').val(jResp.lastName);
					$('#email').val(jResp.email);
					
					$('#password').val(jResp.passwd);
					$('#re-password').val(jResp.passwd);
					$('#password').attr("disabled", "disabled");
					$('#re-password').attr("disabled", "disabled");
					
					
// falta poner en rojo el user si es disabled...
					var selIndex = $("#listUsrs").attr('selectedIndex');
					selIndex = (selIndex == -1)? selIndex: selIndex - 1;
					if (jResp.disable == 1) {
            $('#userRemove').text('Enable').css('color', 'green');
					}
					else {
            $('#userRemove').text('Disable').css('color', 'red');
					}
          /*
          var disabled = opt.getAttribute('disabled');
          var strDis = "";
          if (disabled == null || disabled == 0) { // user is enabled
            // $("#btnSwitchUsr").val(" Enable ");
            disableField.text('Disable').css('color', 'red');
          }
          else {
            // $("#btnSwitchUsr").val(" Disable ");
            disableField.text('Enable').css('color', 'green');
          }
          */
	
// UPDATE THE ROLES LIST
					var roleStore = $('#registered_role');
					var listRole = $('#selected_role');
					$(listRole).empty();
					
					for (var i=0; i<jResp.roles.length; i++) {
						var roleId = jResp.roles[i];
						var roleOpt = $('#registered_role > option[value='+roleId+']');
						var optSelector = 'option[value='+roleId+']';
						
						$(roleStore).remove(optSelector);
						$(roleOpt).appendTo(listRole);
					}	
	
// UPDATE THE GROUPS LIST
// alert ("onGetUserData: managing groups");
					var grpStore = $('#registered_group');
					var listGrp = $('#selected_group');
					$(listGrp).empty();
					
					for (var i=0; i<jResp.groups.length; i++) {
						var grpId = jResp.groups[i];
						var grpOpt = $('#registered_group > option[value='+grpId+']');
						var optSelector = 'option[value='+grpId+']';
						
						$(grpStore).remove(optSelector);
						$(grpOpt).appendTo(listGrp);
					}
					
// UPDATE THE PROJECTS LIST
					var prjStore = $('#registered_project');					
					var listPrj = $('#selected_project');
					$(listPrj).empty();
					
					for (var j=0; j < jResp.prjs.length; j++) {
						var prjId = jResp.prjs[j];
						var prjOpt = $('#registered_project > option[value='+prjId+']');
						var optSelector = 'option[value='+prjId+']';
						
						$(prjStore).remove (optSelector);
						$(prjOpt).appendTo(listPrj);
					}
					
					$('input#btnSend').val('update');
					
/*				          
/ remove the options from the previous user, if so          
          for (i=0; i<listGrp.lengh; i++)
            listGrp.remove(0);
            
/ first get the index of the group for this user from the registered groups
/ and then remove it from the registered and add it to the selected            
          for (i=0; i<jResp.groups.length; i++) {
            var currId = jResp.groups[i];
            var theGrpOpt, theGrpIdx;
            for (j=0; j<grpStore.length; j++) {
              if (grpStore.options[j].value == currId)
                theGrpIdx = j;
            }
            theGrpOpt = new Option (grpStore.options[theGrpIdx].text, currId);
            grpStore.remove(theGrpIdx);
            listGrp.add(theGrpOpt,null);
          }
          
          
          var listPrj = document.getElementById('selected_project');
          var comboRole = document.getElementById('registered_role'); 
          
          var prjStore = document.getElementById('retistered_project');
*/
        }
        else {
          alert (msg);
          return;
        }
			}
		}
		catch (exp) {
			
			alert("JSON Parse failed!: "+exp.message); 
	    return;
		}
  }
	
  
  
	
	var onGetPrjs = function (o) {
		overlay.hide();
    try {
			var jResponse = YAHOO.lang.JSON.parse(o.responseText);
      
			if (jResponse) {
				$('#registered_project').empty();
				
				var projects = jResponse.prjs;
				for (var i=0; i<projects.length; i++) {
					var opt = "<option value=\""+projects[i].id+"\">" + projects[i].name +
								"</option>";
					$('#registered_project').append(opt);
				}
			}
		}
		catch (exp) {
			alert ("JSON err: "+o.responseText);
			return;
		}
	}
	
	
	
	
	var onGetGrps = function (o) {
		overlay.hide();
    try {
			var jResponse = YAHOO.lang.JSON.parse(o.responseText);
      
			if (jResponse) {
				$('#registered_group').empty();
				
				var groups = jResponse.groups;
				for (var i=0; i<groups.length; i++) {
					var idOpt = "g-"+groups[i].id+"-"+groups[i].type;
					var opt = "<option id=\""+ idOpt +"\" value=\""+groups[i].id+"\" onmouseover=\"Tip('"+
							decodeURI(groups[i].name).replace(/\+/g, ' ').replace (/\%2F/g, '/') + 
							"');\" onmouseout=\"UnTip();\">" + 
							decodeURI(groups[i].name).replace(/\+/g, ' ').replace(/\%2F/g, '/') +
								"</option>";

					$('#registered_group').append(opt);
				}
			}
		}
		catch (exp) {
			alert ("JSON err: "+o.responseText);
			return;
		}
	}
	
	
	
/**
 * Response method on getting roles for a user
 * @param {Object} o
 */
	var onGetRols = function (o) {
		overlay.hide();
    try {
			var jResponse = YAHOO.lang.JSON.parse(o.responseText);
      
			if (jResponse) {
				$('#registered_role').empty();
				
				var roles = jResponse['roles'];
				for (var i=0; i<roles.length; i++) {
					var opt = "<option value=\""+roles[i].id+"\">" + 
							decodeURI(roles[i].name).replace(/\+/g, ' ') +
								"</option>";
					$('#registered_role').append(opt);
				} 
			}
		}
		catch (exp) {
			alert ("JSON err: "+o.responseText);
			return;
		}
	}
	
	
	
	var onRmvUser = function (o) {
		try {
			var jResponse = YAHOO.lang.JSON.parse(o.responseText);
      
			if (jResponse) {
				 
			}
		}
		catch (exp) {
			alert ("JSON err: "+o.responseText);
			return;
		}
	}	
	
	
	
	
/**
 * Response method when dynamically requesting primary groups to set it as
 * secondary groups parent
 * @param {Object} o
 */
	var onPrimaryGrps = function (o) {
		overlay.hide();
		var jResponse;
    try {
			jResponse = YAHOO.lang.JSON.parse(o.responseText);
      
			if (jResponse) {
				$('#frmGrpParent').removeAttr("disabled");
				if ($('#frmGrpParent')[0].options.length > 1)
					$('#frmGrpParent').empty();
				
				var groups = jResponse.groups;
				for (var i=0; i<groups.length; i++) {
					var opt = "<option value=\""+groups[i].id+"\" onmouseover=\"Tip('"+
							decodeURI(groups[i].name).replace(/\+/g, ' ').replace (/\%2F/g, '/') + 
							"');\" onmouseout=\"UnTip();\">" + 
							decodeURI(groups[i].name).replace(/\+/g, ' ').replace(/\%2F/g, '/') +
								"</option>";
					$('#frmGrpParent').append(opt);
				}
			}
		}
		catch (exp) {
			alert ("JSON ERR: "+o.responseText+"; "+exp);
			return;
		}
	}
	

	return {
		onResponse: onResponse,
		onFail: onFail,
    
    onNewGrp: onNewGrp,
    onNewPrj: onNewPrj,
    onGetUserData: onGetUserData,
    onSwitchUser: onSwitchUser,
		onNewRole: onNewRole,
		onResetPasswd: onResetPasswd,
    onUserDisable: onUserDisable,
		
		onRmvUser: onRmvUser,
		
		onGetPrjs: onGetPrjs,
		onGetGrps: onGetGrps,
		onGetRols: onGetRols,
		
		onPrimaryGrps: onPrimaryGrps
	}
	
};
/// AJAXRESPONSE /////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////
