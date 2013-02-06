// mredkj.com
// created: 2001-07-11
// last updated: 2001-09-07
var NS4 = (navigator.appName == "Netscape" && parseInt(navigator.appVersion) < 5);


// THIS FUNCTION HAS TO BE UPDATED TO ACCOMODATE ids
function addOption(theSel, theText, theValue, theId) {
	var newOpt = new Option(theText, theValue);
	newOpt.id = theId;
	
	var selLength = theSel.length;
	theSel.options[selLength] = newOpt;
}


function deleteOption(theSel, theIndex) {	
	var selLength = theSel.length;
	if(selLength>0)
	{
		theSel.options[theIndex] = null;
	}
}

function moveOptions(theSelFrom, theSelTo) {
	
	var selLength = 0;
	if (theSelFrom)
	{
		selLength = theSelFrom.length;
	}

	var selectedText = new Array();
	var selectedValues = new Array();
	var selectedIds = new Array ();
	var selectedCount = 0;
	
	var i;
	
	// Find the selected Options in reverse order
	// and delete them from the 'from' Select.
	for(i=selLength-1; i>=0; i--)
	{
		if(theSelFrom.options[i].selected)
		{
			selectedText[selectedCount] = theSelFrom.options[i].text;
			selectedValues[selectedCount] = theSelFrom.options[i].value;
			if (theSelFrom.options[i].id != undefined)
				selectedIds[selectedCount] = theSelFrom.options[i].id;
				
			deleteOption(theSelFrom, i);
			selectedCount++;
		}
	}
	
	// Add the selected text/values in reverse order.
	// This will add the Options to the 'to' Select
	// in the same order as they were in the 'from' Select.
	for(i=selectedCount-1; i>=0; i--)
	{
		addOption(theSelTo, selectedText[i], selectedValues[i], selectedIds[i]);
	}
	
	if(NS4) history.go(0);
}
