package org.nrg.dian.qc.http;

import groovy.util.XmlSlurper;

import org.nrg.dian.qc.model.SessionAssessment;
import org.nrg.dian.qc.exception.SessionNotFound;
import org.nrg.dian.qc.exception.ProjectListNotFound;

class ProjectDirectory {
	def http

	/**
	 * Look up a session's details via the REST API then modify the session in place
	 */
	def addProjectDetails(SessionAssessment session){
		def data = translate(query(session.session_id))
		session.project = data["project"] 
		session.subject_id = data["subject_ID"]
        session.system_session_id = data["ID"]
        
		return session
	}
	
	def query(session_id){
		def response = http.get("REST/experiments", 
                				[format: "xml", xsiType: "xnat:mrSessionData", 
                				 project:"DIAN_*", label:session_id, 
                				 column:"ID,subject_ID,label,project,date"])
		if (response.status != 200) {
			throw new ProjectListNotFound()
		}
		return response.data
	}
	
	def translate(xml){
		def results = xml.results
		// ensure we only found a single session (can not proceed if we did not 
		// find a record, unsure how to proceed if we receive more than one)
		if (results.rows.row.size() != 1) {
			throw new SessionNotFound()
		}
		def retVal = [:]
		results.columns.column.eachWithIndex {  column, i -> 
			retVal[column.text()] = results.rows.row.cell[i].text()
		}
		return retVal
	}
	
}