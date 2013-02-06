
Ext.BLANK_IMAGE_URL = '../lib/ext/resources/images/default/s.gif';
Ext.ns ('Cloning')


Cloning.Panel = Ext.extend (Ext.FormPanel, {
	
	constructor: function (config) {
		var myCfg = {
			title: 'Interview cloning',
			renderTo: 'divCloning',
			// height: '100',
			// width: '90%'
			autoHeight: true,
			autoWidth: true,
			header: true,
//			id: 'cloningForm',
//			name: 'cloningForm',
			labelAlign: 'top'

/* items
 * 		[intrvSource, // combo
 *     prjTarget,  // combo
 * 		 groupTarget, // combo (primary groups)
 * 		 targetName]  // textfield
 */
			
			
		}
		
	}
	
});






/**
 * This is the preconfigured class for the combo for groups which is used to 
 * select the groups corresponding to a user
 * @param {Object} config
 */
Sampledlg.MyCombo = Ext.extend (Ext.form.ComboBox, {
	
	constructor: function (config) {
		var myCfg = {
			allowBlank: false,
			tpl: '<tpl for="."><div ext:qtip="{name:htmlEncode}" class="x-combo-list-item">{name:htmlDecode}</div></tpl>',
			fieldLabel: 'Groups',
			emptyText: 'Select a hospital...',
			labelSeparator: '',
//			labelStyle: "font-weight:bold; color:#15428b; margin-left: 10px;",
			
    	store: new Sampledlg.GrpStore ({
				storeId:'grpStore', 
				usrid: IntroDlg.appCfg.usrid
			}),
			
			displayField: 'name',
			valueField: 'cod',
			mode: 'local',
			
			id: 'comboGrp',
			name: 'comboGrp',
			 
			forceSelection: true,
			triggerAction: 'all',
			typeAhead: true,
			selectOnFocus: true,
			listWidth: 250,
			disabled: false,
			
			style: {
				marginLeft:'5px'
			},
			labelWidth: 110,
			
			listeners: {
				select: IntroDlg.onChangeGrp,
				valid: IntroDlg.subjectValid,
			} 
    }; // eo config object
     
		Sampledlg.MyCombo.superclass.constructor.call (this, Ext.apply (myCfg, config));
		
		
	}
})