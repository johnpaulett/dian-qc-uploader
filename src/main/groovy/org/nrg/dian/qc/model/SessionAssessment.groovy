package org.nrg.dian.qc.model;

import java.text.SimpleDateFormat;

import org.nrg.dian.qc.util.DateUtil;

import groovy.xml.StreamingMarkupBuilder;

/** 
 * Simple POGO for holding information about the session and set of scans.
 */
class SessionAssessment {
	static final String PASS = "1"
	static final String FAIL = "0"
	
	def id
	Date date
	def project
	def session_id
	def rater
	def stereotacticMarker
	def incidentalFindings
	def comments
	def pass
	def payable
	
	List<MrScanAssessment> scans = [];
	
	String toXml() {
		def xml = new StreamingMarkupBuilder().bind{
			mkp.xmlDeclaration()
			"xnat:QCManualAssessment" ("ID":id, "project":project, 
			"xmlns:prov":"http://www.nbirn.net/prov", 
			"xmlns:xnat":"http://nrg.wustl.edu/xnat",
			"xmlns:xsi":"http://www.w3.org/2001/XMLSchema-instance", 
			"xsi:schemaLocation":"http://nrg.wustl.edu/xnat plugin-resources/project-skeletons/xnat/src/schemas/xnat/xnat.xsd"){ 
				"xnat:imageSession_ID" session_id
				if (rater){
					"xnat:rater" rater
				}
				if (date){
					"xnat:date" DateUtil.dateFormat(date)
					"xnat:time" DateUtil.timeFormat(date)
				}
				if (stereotacticMarker){
					"xnat:stereotacticMarker" stereotacticMarker
				}
				if (incidentalFindings){
					"xnat:incidentalFindings" incidentalFindings
				}
				"xnat:scans" {
					for (scan in scans) {
						unescaped << scan.toXml()
					}
				}
				if (comments){
					"xnat:comments" comments
				}
				"xnat:pass" pass
				if (payable){
					"xnat:payable" payable
				}
			}
		}
		return xml.toString()
	}
}