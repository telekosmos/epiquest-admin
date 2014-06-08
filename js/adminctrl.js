/*
 * Global Ajax Response from Register server
 */


var AdmFormCtrl = function () {
  
  var ajaxResp;
  var txtUsrName, txtPass1, txtPass2;
  var comboRole, listGrp, listPrj, listMainGrps;
// These two variables represents the left side project and group lists  
  var prjStore, grpStore;
  
  var listUsers;
  var disableField;
  
// This is a switch to checke if the user data is ok.
// It will be true if, when pushed register or update, the user has
// all minimun parameters; false otherwise
// It comes from the need of avoid resetting form after pushing send button
	var userDataOk = false;
	var currentUserId;
	
// This member holds the form state either for update or create a new user
// When updating, all fields can be changed EXCEPT the username
	var state;
	
	
  
/**
 * Makes an ajax call to reset the password for the currently selected user
 */
	var resetPasswd = function () {
		
		var postData = "act=resetpasswd&usr="+txtUsrName.value;
		var xReq = new AjaxReq();
		
    xReq.setUrl(APP_ROOT+"/servlet/mngusers");
    xReq.setMethod("POST");
    xReq.setPostdata(postData);
    xReq.setCallback(ajaxResp.onResetPasswd, ajaxResp.onFail, ajaxResp, null);
    xReq.startReq();
	}
	
	
	
/**
 * Modify the state member and several components when switching from creation mode
 * to update mode. 
 */
	var changeState = function (newState) {
		state = newState
		
		var guard = state == MODE_CREATE && txtUsrName != null;
		if (guard)
			txtUsrName.removeAttribute ('disable');
			
		else if (txtUsrName != null)
			txtUsrName.disabled = 'disabled';
		
	}
	
	
/**
 * This method run through all user fields to gather the data and send it to
 * the server to register the new user or update it
 * [or update an existing user]
 */
  var getUserParameters = function () {
		
		var usrId = $('#frmUsrId').val();
		userDataOk = true
		
		
    // Get username
    var sUsername = document.getElementById('username').value;
    if (sUsername == '') {
      alert("Please, fill the 'username' field");
      userDataOk = false;
    }
    sUsername = encodeURIComponent(sUsername);
    
    // Get passwords
    var sPassword = document.getElementById('password').value;
    var sRePassword = document.getElementById('re-password').value;
    if (sPassword == '') {
      alert("Please, fill the 'password' field");
      userDataOk = false;
    }
    else {
      if (sPassword != sRePassword) {
        alert("password are not equal. Write them again");
        userDataOk = false;
      }
    }
    sPassword = encodeURIComponent(sPassword);
		
		
		var firstname = document.getElementById('firstname').value;
    if (firstname == '') {
      alert("Please, fill the 'First name' field");
      userDataOk = false;
    }
    firstname = encodeURIComponent(firstname);
		
		var lastname = document.getElementById('lastname').value;
    if (lastname == '') {
      alert("Please, fill the 'Last name' field");
      userDataOk = false;
    }
    lastname = encodeURIComponent(lastname);
		
		
		var email = document.getElementById('email').value;
    if (email == '') {
      alert("Please, fill the 'Last name' field");
      userDataOk = false;
    }
		
		var emailRegEx = /^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/g;
		if (email.search(emailRegEx) == -1) {
			alert ("The email address is wrong. Only numbers, letters, _, - and . are allowed");
			userDataOk = false;
		}
    email = encodeURIComponent(email);
		
		
    // Get list of selected role
    var sRoles = '', nameRole = '';
    var aRoleList = document.getElementById('selected_role').options;
    for (var iIndex = 0; iIndex < aRoleList.length; iIndex++) {
			sRoles += aRoleList[iIndex].value + ";";/*
      if (aRoleList[iIndex].selected) {
        sRole += aRoleList[iIndex].value+';';
        nameRole = aRoleList[iIndex].text;
      }*/
    }
    if (sRoles == '') {
      alert("Please, at least one role must be selected");
      userDataOk = false;
    }
		sRoles = sRoles.substring(0, sRoles.length-1);
    
// Get list of selected groups
    var sGroupList = '';
    var aSelectedGroupList = document.getElementById('selected_group').options;
		var existPrimGrp = 0, existSecGrp = 0;
		var idGrp, idGrpParts;
    for (var iIndex = 0; iIndex < aSelectedGroupList.length; iIndex++) {
      sGroupList += aSelectedGroupList[iIndex].value + ";";
			
// check for primary group
			idGrp = aSelectedGroupList[iIndex].id;
			idGrpParts = idGrp.split ("-");
			if (idGrpParts.length == 3) {
				existPrimGrp = idGrpParts[2] == "1"? 1: existPrimGrp;
				existSecGrp = idGrpParts[2] == "2"? 1: existSecGrp;
			}
    }
		
    if (sGroupList == '' || existPrimGrp == 0 || existSecGrp == 0) {
      alert("Please, at least one country and one hospital must be selected");
			userDataOk = false
    }
		else
    	sGroupList = sGroupList.substring(0, sGroupList.length - 1);// Delete last ';'
    
		
// Get list of selected project
    var sProjectList = '';
    var aSelectedProjectList = document.getElementById('selected_project').options;
    for (var iIndex = 0; iIndex < aSelectedProjectList.length; iIndex++) {
      sProjectList += aSelectedProjectList[iIndex].value + ";";
    }
    if (sProjectList == '' && nameRole != 'admin') {
      alert("Please, at least one project should be selected");
      userDataOk = false;
    }
    sProjectList = sProjectList.substring(0, sProjectList.length - 1);// Delete last ';'
    
		var sUserField = "username=" + sUsername +  "&pwd=" +  sPassword +
    "&roles=" +  sRoles +  "&group=" +  sGroupList +  "&project=" +
    sProjectList+"&firstname="+firstname+"&lastname="+lastname+
		"&userId="+usrId+"&email="+email;
		
    
    return sUserField;
  };
	
  
	
	
/**
 * Method to make an ajax request in order to register a new role
 */
	var newRole = function () {
		if ($('#frmRoleName').val() == '') {
			alert ('A role name has to be introduce');
			$('#frmRoleName').css('border', '2px dashed red');
			return;
		}
		
		var postData = "frmRoleName="+encodeURIComponent(frmRoleName.value);
    postData += "&frmRoleDesc="+encodeURIComponent(frmRoleDesc.value)+"&what=ROL";
    
    var xReq = new AjaxReq();
    xReq.setUrl("addelem.jsp");
    xReq.setMethod("POST");
    xReq.setPostdata(postData);
    xReq.setCallback(ajaxResp.onNewRole, ajaxResp.onFail, ajaxResp, null);
    xReq.startReq();
	}




/**
 * Sends a request to register a new group
 */  
  var newGroup = function () {
    var txtGrp = document.getElementById('frmGrpName');
    var grpTypeSel = document.getElementById('frmGrpType');
		var txtGrpCode = document.getElementById('frmGrpCode');
		var grpParentId = document.getElementById('frmGrpParent');
		var parentGrp = document.getElementById('frmGrpParent');
    
    if (txtGrp.value.length == 0 || txtGrp.value == 'New group name') {
      alert ("A Group name has to be introduced");
			$('#frmGrpName').css('border', '2px dashed red');
      return;
    }
    
    if (grpTypeSel.selectedIndex == 0 || grpTypeSel.selectedIndex == undefined) {
      alert ("A Group Type has to be selected");
			$('#frmGrpType').css('border', '2px dashed red');
      return;
    }
		
		if (txtGrpCode.value.length == 0 || txtGrpCode.value == 'New group code') {
			alert ("A code for Group has to be entered");
			$('#frmGrpName').css('border', '2px dashed red');
			return;
		}
    
		if (grpTypeSel.selectedIndex == 2 && parentGrp.selectedIndex == 0) {
			alert ("A Parent group has to be selected when a Hospital group is to be created");
			$('#frmGrpParent').css('border', '2px dashed red');
			$('#frmGrpParent').change(function () {
				$(this).css('border', '1px solid black');
			});
      return;	
		}
		
    var postData = "frmGrpName="+encodeURIComponent(txtGrp.value)+"&frmGrpType=";
    postData += grpTypeSel.options[grpTypeSel.selectedIndex].value;
		postData += "&frmGrpCode="+encodeURIComponent(txtGrpCode.value);
    postData += "&what=GRP";
    postData += "&frmGrpParent="+grpParentId.options[grpParentId.selectedIndex].value;
		
    var xReq = new AjaxReq();
    xReq.setUrl("addelem.jsp");
    xReq.setMethod("POST");
    xReq.setPostdata(postData);
    xReq.setCallback(ajaxResp.onNewGrp, ajaxResp.onFail, ajaxResp, null);
//  alert (postData);
    xReq.startReq();
    
  };
  
  

/**
 * Sends a request to register a new project
 */  
  var newProject = function () {
    var txtPrj = document.getElementById('frmPrjName');
    var prjDesc = document.getElementById('frmPrjDesc');
		var prjCode = document.getElementById ('frmPrjCode');
    
    if (txtPrj.value.length == 0 || txtPrj.value == 'New project name') {
      alert ("A Project name has to be introduced");
			$('#frmPrjName').css('border', '3px dashed red');
      return;
    }
		
		if (prjCode.value.length == 0 || prjCode.value == 'New project code') {
      alert ("A Project code has to be introduced");
			$('#frmPrjCode').css('border', '3px dashed red');
      return;
    }
    
    var postData = "frmPrjName="+encodeURIComponent(txtPrj.value)+"&frmPrjDesc=";
    postData += encodeURIComponent(prjDesc.value);
		postData += "&frmPrjCode="+encodeURIComponent(prjCode.value);
    postData += "&what=PRJ";
    
    var xReq = new AjaxReq();
    xReq.setUrl("addelem.jsp");
    xReq.setMethod("POST");
    xReq.setPostdata(postData);
    xReq.setCallback(ajaxResp.onNewPrj, ajaxResp.onFail, ajaxResp, null);
    xReq.startReq();
    
  };
  
	
	
  
/**
 * This method submits all collected form data in order to register (or update)
 * a (new) user
 */
  var submitRegisterForm = function () {
    // Get user parameters
    var sUserField = getUserParameters();
    
		
		if (userDataOk == true) {
			var xReq = new AjaxReq();
      xReq.setUrl("admin.jsp");
      xReq.setMethod("POST");
      xReq.setPostdata(sUserField);
      xReq.setCallback(ajaxResp.onResponse, ajaxResp.onFail, ajaxResp, null);
      xReq.startReq();
		}
		
/*		
    if (sUserField == -1) {
      return
    }
		else if (sUserField == null)
			resetRegisterForm ();
			
		else {
			var xReq = new AjaxReq();
      xReq.setUrl("admin.jsp");
      xReq.setMethod("POST");
      xReq.setPostdata(sUserField);
      xReq.setCallback(ajaxResp.onResponse, ajaxResp.onFail, ajaxResp, null);
      xReq.startReq();
		}
*/			
  };
  
  
	
/**
 * Reset parameters from register form
 */
  var resetRegisterForm = function () { 
    document.getElementById('username').value = '';
    document.getElementById('password').value = '';
    document.getElementById('re-password').value = '';
    $("#firstname").val('');
		$("#lastname").val('');
		$("#frmUsrId").val('');
		$("#email").val('');
		
		$("#password").removeAttr("disabled");
		$("#re-password").removeAttr("disabled");
		
    
//		document.getElementById('registered_role').options[0].selected = true;
		
// reset group lists    
    var aSelectedGroupList = document.getElementById('selected_group').options;
    for (var iIndex = 0; iIndex < aSelectedGroupList.length; iIndex++) {
      aSelectedGroupList[iIndex].selected = true;
    }
    moveOptions(document.getElementById('selected_group'), 
                document.getElementById('registered_group'));
    
// reset project lists
    var aSelectedProjectList = document.getElementById('selected_project').options;
    for (var iIndex = 0; iIndex < aSelectedProjectList.length; iIndex++) {
      aSelectedProjectList[iIndex].selected = true;
    }
    moveOptions(document.getElementById('selected_project'), 
                document.getElementById('registered_project'));
                
// reset role lists
		var aSelectedRoleList = document.getElementById('selected_role').options;
    for (var iIndex = 0; iIndex < aSelectedRoleList.length; iIndex++) {
      aSelectedRoleList[iIndex].selected = true;
    }
    moveOptions(document.getElementById('selected_role'), 
                document.getElementById('registered_role'));
								
    document.getElementById('frmGrpName').value = 'New group name';
		
    document.getElementById('frmPrjName').value = 'New project name';
		document.getElementById('frmPrjCode').value = 'New project code';
		document.getElementById('frmPrjDesc').value = 'Project description';
		
		$('input#btnSend').val("register");
		changeState (MODE_CREATE)
  };


  /**
   * Disable (if parameter is evaluated to false) or enable (if param is true) the
   * currently selected user
   * @param enable {boolean} true to enable the user; false to disable
   */
  var disableUser = function(disable) {
    var postdata = 'what=user&disable='+(disable? 1: 0)+'&usrid='+currentUserId;

    var xReqPrjs = new AjaxReq ();
    xReqPrjs.setUrl(APP_ROOT+"/servlet/AjaxUtilServlet");
    xReqPrjs.setMethod("POST");
    xReqPrjs.setPostdata(postdata);
    xReqPrjs.setCallback(ajaxResp.onUserDisable, ajaxResp.onFail, ajaxResp, null);
    xReqPrjs.startReq();
    console.log('/servlet/AjaxUtilServlet:POST?'+postdata);
  }
	
	
  
/**
 * This method send a request to get data for a new user just after selecting an
 * element from the users list
 * @param {opt} this is the option (checkbox) to know if the user is disabled or 
 * enabled
 */  
  var displayUsr = function (opt) {
    var postData;
		var usrId = opt.value;
    currentUserId = opt.value;
    postData = "what=usr&frmid="+usrId;
    
		$('#listUsrs > option').each (function () {
			$(this).css('font-weight', 'normal');
		})
		
// Disable some components if the user is disabled. This can be changed
//		var disabled = $("#listUsrs > option['value="+usrId+"']").attr("disabled");

		var xReqPrjs = new AjaxReq ();
		xReqPrjs.setUrl(APP_ROOT+"/servlet/AjaxUtilServlet");
    xReqPrjs.setMethod("GET");
    xReqPrjs.setPostdata("what=prj");
    xReqPrjs.setCallback(ajaxResp.onGetPrjs, ajaxResp.onFail, ajaxResp, null);
    xReqPrjs.startReq();
		
		var xReqRoles = new AjaxReq ();
		xReqRoles.setUrl(APP_ROOT+"/servlet/AjaxUtilServlet");
    xReqRoles.setMethod("GET");
    xReqRoles.setPostdata("what=rol");
    xReqRoles.setCallback(ajaxResp.onGetRols, ajaxResp.onFail, ajaxResp, null);
    xReqRoles.startReq();

		var xReqGrps = new AjaxReq ();
		xReqGrps.setUrl (APP_ROOT+"/servlet/AjaxUtilServlet");
    xReqGrps.setMethod("GET");
    xReqGrps.setPostdata("what=grp");
    xReqGrps.setCallback(ajaxResp.onGetGrps, ajaxResp.onFail, ajaxResp, null);
    xReqGrps.startReq();
		
    var xReq = new AjaxReq();
    xReq.setUrl("getdata.jsp");
    xReq.setMethod("GET");
    xReq.setPostdata(postData);
    xReq.setCallback(ajaxResp.onGetUserData, ajaxResp.onFail, ajaxResp, null);
    xReq.startReq();
// alert (postData);

		changeState (MODE_UPDATE)
  }
  
  
  
	
/**
 * This method sends a request to switch the state of an user from enable to disable
 * or viceversa
 */
  var switchUsr = function () {
    var postData;

    if ($(listUsers).attr('selectedIndex') == -1) {
      alert ("An user has to be selected from the list of Regsitered Users");
      return;
    }
    
		var myListUsers = $(listUsers)[0];
    postData = "what=usr&frmid="+myListUsers.options[myListUsers.selectedIndex].value;
    postData += "&frmname="+myListUsers.options[myListUsers.selectedIndex].text;
    
    var xReq = new AjaxReq();
    xReq.setUrl("switchdata.jsp");
    xReq.setMethod("POST");
    xReq.setPostdata(postData);
    xReq.setCallback(ajaxResp.onSwitchUser, ajaxResp.onFail, ajaxResp, null);
    xReq.startReq();
  }
  
  
	
/*	
	var rmvActiveUsrs = function () {
		var users = $('input[type=checkbox]');
		var postData = "what=rmv&users=";
		
		$.each(users, function (index) {
			postData += $(this).attr('name')+",";
		});
		postData = postData.substr(0, postData.length-1);
		
alert ("postData: "+postData);
		var xReq = new AjaxReq();
    xReq.setUrl("/epiadmin/servlet/AjaxUtilServlet");
    xReq.setMethod("POST");
    xReq.setPostdata(postData);
    xReq.setCallback(ajaxResp.onRmvUser, ajaxResp.onFail, ajaxResp, null);
    xReq.startReq();
	}
*/	
	
	
/**
 * This method is triggered when a new group is being created and the type is 
 * secondary (HOSPITAL). It is oriented to get primary groups to set a relationship
 * @param {Object} combo
 */
	var watchType = function (combo) {
		
		if (combo.selectedIndex == 2) {
			var postData = "what=pg";
			
			var xReq = new AjaxReq();
	    xReq.setUrl(APP_ROOT+"/servlet/MngGroupsServlet");
	    xReq.setMethod("GET");
	    xReq.setPostdata(postData);
	    xReq.setCallback(ajaxResp.onPrimaryGrps, ajaxResp.onFail, ajaxResp, null);
	    xReq.startReq();
		}
	}
	
	
	
	
  var init = function () {
    ajaxResp = new AjaxResponse ();

    txtUsrName = document.getElementById('username');
    txtPass1 = document.getElementById('password');
    txtPass2 = document.getElementById('re-password');
    
    listGrp = document.getElementById('selected_group');
		listMainGrp = document.getElementById('frmGrpParent');
		
    listPrj = document.getElementById('selected_project');
    comboRole = document.getElementById('registered_role'); 
    
    prjStore = document.getElementById('retistered_project');
    grpStore = document.getElementById('registered_group');
    
//    listUsers = document.getElementById('listUsrs');
		listUsers = $('#listUsrs');
		userOpts = $('#listUsrs > option');
		$(userOpts).click (function (ev) {
			var opt = ev.target;
//			switchBtn ();
			displayUsr(opt);
//			sayHello ($(opt)[0].text);
		});

    var me = this;
    disableField = $('#userRemove');
    disableField.bind('click', function(ev) {
      if ($(this).text().toLowerCase().indexOf('disable') != -1)
        me.disableUsr(true); // enable the user
      else
        me.disableUsr(false); // disable the user
    });
		
		/*
		$('input#btnSend').click(function(ev) {
			if (userDataOk == true) {
				$(this).val("register");
				resetRegisterForm();
			}
		})
		*/
		
		$('input#frmPrjName').focus(function (ev) {
			$(this).css('border', '1px solid darkblue');
		})
		
		changeState (MODE_CREATE)
	
  }
  

	
	
	
	var sayHello = function (str) {
		if (!str)
			alert ("sayHello");
			
		else
			alert (str);
	}
  
  
  return {
    getUserParameters: getUserParameters,
    submitRegisterForm: submitRegisterForm,
		newRole: newRole,
    newGroup: newGroup,
    newProject: newProject,
    resetRegisterForm: resetRegisterForm,
    displayUsr: displayUsr,
    disableUsr: disableUser,
    switchUsr: switchUsr,
		resetPasswd: resetPasswd,
		changeState: changeState,
//		rmvActiveUsrs: rmvActiveUsrs,
		
		watchType: watchType,
    
    init:init
  }
  
}



var overlay, admCtrl;
$(document).ready(function () {
	overlay = new Overlay ();
	admCtrl = new AdmFormCtrl();
	admCtrl.init ();
});


/*
var overlay;
var admCtrl;
function onReady () {
  overlay = new Overlay();
  admCtrl = new AdmFormCtrl ();
  admCtrl.init();
  
//  ajaxResp = new AjaxResponse();
}
*/


