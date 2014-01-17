///////////////////////////////////////////////////////////////////////////////////////////////
 // BaseComponent.java
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
package org.ednovo.gooru.util;

import java.util.Properties;

import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseComponent {

	@Autowired
	private Properties jobsConstants;

	protected final Logger logger = LoggerFactory.getLogger(BaseComponent.class);

	protected ClientResource createClientResource(String url) {
		ClientResource resource = new ClientResource(url);
		if (!getTomcatUsername().equals("NA")) {
			
			ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, getTomcatUsername(), getTomcatPassword());
			resource.setChallengeResponse(challengeResponse);

		}

		return resource;
	}
	
	protected void releaseClientResources(ClientResource resource, Representation representation){
		try {
			if (resource != null) {
				resource.release();
			}
			if (representation != null) {
				representation.release();
			}
			resource = null;
			representation = null;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public void createAndRunClientResource(String url) {
		executeClientResource(createClientResource(getApiPath() + url));
	}

	public void createAndRunClientResource(String url, Form form) {
		executeClientResource(createClientResource(getApiPath() + url), form);
	}
	
	public void createAndRunClientResource(String path,String url, Form form) {
		executeClientResource(createClientResource(path + url), form);
	}

	public void executeClientResource(ClientResource clientResource) {
		executeClientResource(clientResource, null);
	}

	public void executeClientResource(ClientResource clientResource, Form form) {

		Representation representation = null;
		try {
			if (form == null) {
				representation = clientResource.get();
			} else {
				representation = clientResource.post(form.getWebRepresentation());
			}

		} catch (Exception exception) {
			logger.error(exception.getMessage());
		} finally {
			releaseClientResources(clientResource, representation);
		}
	}

	public String getApiPath() {
		return getGooruHome() +"/"+ getJobsConstants().getProperty("restAPIEndPoint");
	}
	
	public String getSearchApiPath() {
		return getJobsConstants().getProperty("searchRestAPIEndPoint");
	}

	public String getTomcatUsername() {
		return getJobsConstants().getProperty("tomcatUsername");
	}

	public String getTomcatPassword() {
		return getJobsConstants().getProperty("tomcatPassword");
	}

	public String getPageTesterPath() {
		return getJobsConstants().getProperty("gooruHome") + getJobsConstants().getProperty("pageTesterUrl");
	}

	public String getGooruHome() {
		return getJobsConstants().getProperty("gooruHome");
	}

	public String getGlobalJobKey() {
		return getJobsConstants().getProperty("globalJobKey");
	}

	public Properties getJobsConstants() {
		return jobsConstants;
	}

	public void setJobsConstants(Properties jobsConstants) {
		this.jobsConstants = jobsConstants;
	}
	
	protected abstract class ClientResourceExecuter {
		
		ClientResource clientResource = null;
		
		Representation representation = null;
		
		protected ClientResourceExecuter(){
			try {
				run(clientResource, representation);
			} catch (Exception exception) {
				logger.error(exception.getMessage());
			} finally {
				releaseClientResources(clientResource, representation);
			}
		}
		
		public abstract void run(ClientResource clientResource,Representation representation) throws Exception ;
	}
}
