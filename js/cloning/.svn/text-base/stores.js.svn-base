var grpJsonSt = new Ext.data.JsonStore ({
// json reader config
	root: 'groups',
	fields: ['id', 'name', 'cod'],
	idProperty: 'id',

// this store config
//	data: grpData,
	storeId: 'grpStore',
	proxy: new Ext.data.HttpProxy({
		url: APP_ROOT+'/servlet/MngGroupsServlet',
		method: 'GET',
	}),
	
	baseParams: {'what':'p'},
	autoDestroy: true,
//	autoLoad: true
});