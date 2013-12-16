package org.cnio.appform.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.cnio.appform.util.dump.*;
import org.inb.dbtask.*;

public class DumpTest {

	public static void main (String[] args) {
		String prj = "PanGen-Eu";
		String intrv = "RecogidaMuestra_ES_PG";
		String grp = null;
		// Integer orderSec = 1;
		Integer sortOrder = 1;
		
//		String fileName = "/Users/bioinfo/Development/dbdumps/aliquotsnew-isblac.props";
		String fileName = "/Users/bioinfo/Development/deploy/appform/batch/Part_pangen.props";
		/*
		try {
			MainDump md = new MainDump ();
			md.publicBatch(fileName);
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		*/
		
		DataRetriever dr = new DataRetriever ();
		
		String prjCode = "157";
		String intrvId = "4150";
		String grpId = "";
		String orderSec = "1";
		
//		String dumpOut = dr.getAdminDump(prjCode, intrvId, "", Integer.parseInt(orderSec));
//		String dumpOut = dr.getAdminDump("157", "4150", null, 2);
//		System.out.println(dumpOut);
		
		DumpTest dt = new DumpTest();
		dt.delPatients();
	}
	
	public void delPatients () {
		String dbUrl = "jdbc:postgresql://localhost:4321/appform";
		String subjects = "188011009,157102002,15750101501,15750101504,15750101507,15750101508,15750101509,162131001";
		System.out.println("Deleting patients: "+subjects);			
		
		FixingTasksHub fth = new FixingTasksHub (dbUrl, "gcomesana", "appform");
		ArrayList<String> listCodsPatient = new ArrayList<String>();
		String patsArray[] = subjects.split(",");
		for (int i=0; i<patsArray.length; i++)
			listCodsPatient.add(patsArray[i]);
		
		HashMap<String, Object> deletions =
      (HashMap<String, Object>)fth.deletePatients("localhost", 
      		"gcomesana", 
      		"appform", 
      		true, listCodsPatient);
		ArrayList<String> noDeletions = fth.getNoDeletedPatients();
		String jsonOut = "";
		
		Integer deletedPats = (Integer)deletions.get("rows_affected");

    HashMap<String, HashMap<String, Integer>> patientSamples =
                          (HashMap)deletions.get("pats_with_samples");
    Iterator itOne = patientSamples.entrySet().iterator();
    String jsonPat = "";
    while (itOne.hasNext()) {
      Map.Entry pair = (Map.Entry)itOne.next();
      String patientCode = (String)pair.getKey();

      HashMap samples = (HashMap)pair.getValue();
      Iterator sampleIt = samples.entrySet().iterator();
      String jsonSamples = "\"samples\": [";
      while (sampleIt.hasNext()) {
        Map.Entry samplePair = (Map.Entry)sampleIt.next();
        jsonSamples += "{\"sample_code\":\""+samplePair.getKey()+"\",";
        jsonSamples += "\"num_of_answers\": "+samplePair.getValue()+"},";
      }
      jsonSamples = jsonSamples.substring(0, jsonSamples.length()-1)+"]";

      jsonPat += "{\"patient_code\": \""+patientCode+"\", ";
      jsonPat += jsonSamples+"},";
    }
    jsonPat = jsonPat.substring(0, jsonPat.length()-1);

    ArrayList<String> patients_affected = (ArrayList)deletions.get("pats_affected");
    String jsonPatsAff = "\"patients_deleted\": [";
    for (String patientCode: patients_affected)
      jsonPatsAff += "\""+patientCode+"\",";

    jsonPatsAff = jsonPatsAff.substring(0, jsonPatsAff.length()-1)+"]";

    jsonOut = "{\"deletions\": "+deletedPats+", " + jsonPatsAff+","+
      " \"pats_with_samples\":["+jsonPat+"]}";
    
    System.out.println(jsonOut);
	}
}
