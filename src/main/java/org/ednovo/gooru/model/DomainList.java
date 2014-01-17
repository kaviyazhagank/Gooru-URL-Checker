///////////////////////////////////////////////////////////////////////////////////////////////
 // DomainList.java
 // Gooru-URL Checker
 // Created by Gooru on 2014
 // Copyright (c) 2014 Gooru. All rights reserved.
 // http://www.goorulearning.org/
 // Permission is hereby granted, free of charge, to any person      obtaining
 // a copy of this software and associated documentation files (the
 // "Software"), to deal in the Software without restriction, including
 // without limitation the rights to use, copy, modify, merge, publish,
 // distribute, sublicense, and/or sell copies of the Software, and to
 // permit persons to whom the Software is furnished to do so,  subject to
 // the following conditions:
 // The above copyright notice and this permission notice shall be
 // included in all copies or substantial portions of the Software.
 // THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY  KIND,
 // EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE    WARRANTIES OF
 // MERCHANTABILITY, FITNESS FOR A PARTICULAR  PURPOSE     AND
 // NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR  COPYRIGHT HOLDERS BE
 // LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 // OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 // WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 //////////////////////////////////////////////////////////////////////////////////////////////
package org.ednovo.gooru.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.ednovo.gooru.service.DomainObject;

@Entity
@Table(name = "domain_list")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DomainList implements DomainObject {

	/**
	 * @author daniel
	 */
	
	
	private static final long serialVersionUID = 6707629200161474802L;
	
	public static final String OBJECT_KEY = "DOMAINLIST";
	
	@Id
	@Column(name = "resource_source_id")
	private Integer resourceSourceId;
	
	@Column(name = "domain_name")
	private String domainName;
	
	@Column(name = "resource_count")
	private Integer resourceCount;
	
	@Column(name = "last_checked_date")
	private Date lastCheckedDate;
	
	@Column(name = "total_checked")
	private Integer  totalChecked;
	
	@Column(name = "last_checked_count")
	private Integer  lastCheckedCount;
	
	@Column(name = "ok_count")
	private Integer  okCount;
	
	@Column(name = "fail_count")
	private Integer  failCount;
	
	@Column(name = "transient_count")
	private Integer  transientCount;
	
	@Column(name = "confidence_level")
	private Double  confidenceLevel;
	
	


	public Integer getResourceSourceId() {
		return resourceSourceId;
	}

	public void setResourceSourceId(Integer resourceSourceId) {
		this.resourceSourceId = resourceSourceId;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public Integer getResourceCount() {
		return resourceCount;
	}

	public void setResourceCount(Integer resourceCount) {
		this.resourceCount = resourceCount;
	}

	public Date getLastCheckedDate() {
		return lastCheckedDate;
	}

	public void setLastCheckedDate(Date lastCheckedDate) {
		this.lastCheckedDate = lastCheckedDate;
	}

	public Integer getTotalChecked() {
		return totalChecked;
	}

	public void setTotalChecked(Integer totalChecked) {
		this.totalChecked = totalChecked;
	}

	public Integer getLastCheckedCount() {
		return lastCheckedCount;
	}

	public void setLastCheckedCount(Integer lastCheckedCount) {
		this.lastCheckedCount = lastCheckedCount;
	}

	public Integer getOkCount() {
		return okCount;
	}

	public void setOkCount(Integer okCount) {
		this.okCount = okCount;
	}

	public Integer getFailCount() {
		return failCount;
	}

	public void setFailCount(Integer failCount) {
		this.failCount = failCount;
	}

	public Integer getTransientCount() {
		return transientCount;
	}

	public void setTransientCount(Integer transientCount) {
		this.transientCount = transientCount;
	}

	public Double getConfidenceLevel() {
		return confidenceLevel;
	}

	public void setConfidenceLevel(Double confidenceLevel) {
		this.confidenceLevel = confidenceLevel;
	}

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return getDomainName();
	}

	@Override
	public String getObjectKey() {
		// TODO Auto-generated method stub
		return OBJECT_KEY;
	}

	}
