///////////////////////////////////////////////////////////////////////////////////////////////
 // ResourceCassandraRestController.java
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
package org.ednovo.gooru.controller;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.ednovo.gooru.factory.ColumnFamilyType;
import org.ednovo.gooru.model.ConfigSettings;
import org.ednovo.gooru.model.DomainList;
import org.ednovo.gooru.service.JobsRecurssiveAction;
import org.ednovo.gooru.service.ResourceCassandraService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.Row;
import com.netflix.astyanax.model.Rows;


@Controller
public class ResourceCassandraRestController  {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ResourceCassandraRestController.class);

	@Autowired
	private ResourceCassandraService resourceCassandraService;

	@RequestMapping(method = RequestMethod.GET, value = "/resource/status")
	public ModelAndView getUrlStatusByGooruOid(HttpServletRequest request,@RequestParam(value = "gooruOid",required = true) String gooruOid,
			HttpServletResponse response) throws Exception {
				ColumnList<String> resources = this.getResourceCassandraService().getResourceByGooruOid(gooruOid, ColumnFamilyType.RESOURCE_STATUS);
				
				ModelAndView jsonmodel = new ModelAndView("model");
				JSONObject jsonObject = new JSONObject();
				if(resources != null && resources.size() > 0){
					jsonObject.put("gooruOid", gooruOid);
					jsonObject.put("lastUpdated",resources.getStringValue("last_updated", null));
					jsonObject.put("urlStatus",resources.getStringValue("url_status", null));
					jsonObject.put("isIndexingDone",resources.getStringValue("indexing_done", null));
					jsonObject.put("resourceStatus",resources.getStringValue("resource_status", null));
					jsonObject.put("Url",resources.getStringValue("url", null));
				}
				jsonmodel.addObject("model", jsonObject);
				return jsonmodel;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/resource/status/filterby")
	public ModelAndView getUrlStatusByFilter(HttpServletRequest request,@RequestParam(value = "lastUpdated" ,required = false) String lastUpdated,
			@RequestParam(value = "urlStatus" , required = false) String urlStatus,
			@RequestParam(value = "resourceStatus" , required = false) String resourceStatus,
			@RequestParam(value = "indexingDone" , required = false) String indexingDone,
			HttpServletResponse response) throws Exception {
			
			ModelAndView jsonmodel = new ModelAndView("model");
			Rows<String, String> resource = this.getResourceCassandraService().getResourceFilterBy(lastUpdated,urlStatus,resourceStatus,indexingDone, ColumnFamilyType.RESOURCE_STATUS);
					JSONArray jsonArray = new JSONArray();			
				if(resource != null && resource.size()> 0){
				for(Row<String, String> resources : resource){
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("gooruOid",resources.getKey());
					jsonObject.put("lastUpdated",resources.getColumns().getStringValue("last_updated", null));
					jsonObject.put("urlStatus",resources.getColumns().getStringValue("url_status", null));
					jsonObject.put("isIndexingDone",resources.getColumns().getStringValue("indexing_done", null));
					jsonObject.put("resourceStatus",resources.getColumns().getStringValue("resource_status", null));
					jsonObject.put("Url",resources.getColumns().getStringValue("url", null));
					jsonArray.put(jsonObject);
				 }
				}
				jsonmodel.addObject("model",jsonArray.toString());
				
				return jsonmodel;
	}
	
	public void executeEveryThirtyMins() throws Exception {
		logger.info("scheduling Every 30 minutes started ...");
		ObjectMapper mapper = null;
		List<DomainList> domainCheck = resourceCassandraService.getValidDomain();
		for (final DomainList domains : domainCheck)
		{
			mapper = new ObjectMapper();
			resourceCassandraService.putRedisValue(resourceCassandraService.getConfigSettings(ConfigSettings.DOMAIN_KEY) + domains.getDomainName(), mapper.writeValueAsString(domains));
		}
	}
	
	 @RequestMapping(value="/resource/counts", method = RequestMethod.GET)
	 public ModelAndView getDomainCheckCount(HttpServletRequest request, @RequestParam(value = "resourceStatus", required = false, defaultValue = "1") String resourceStatus, 
	    		@RequestParam(value = "lastUpdated" ,required = false) String lastUpdated, HttpServletResponse response) throws Exception
	    {
			 ModelAndView jsonmodel = new ModelAndView("model");
			 Integer count = this.getResourceCassandraService().getResourceStatusCount(lastUpdated, resourceStatus, ColumnFamilyType.RESOURCE_STATUS);
			 JSONObject jsonObject = new JSONObject();
			 jsonObject.put("lastUpdated", lastUpdated);
			 jsonObject.put("statusCount",count);
			 jsonObject.put("flag", resourceStatus.equals("1") ? "successResources" : "failureResources");
			 jsonmodel.addObject("model",jsonObject);
			 return jsonmodel;
	    }
	 
	public void executeEveryTenSecs() throws Exception{
		
		Set <String> domainKeysSet = resourceCassandraService.getAllRedisKeys(resourceCassandraService.getConfigSettings(ConfigSettings.DOMAIN_KEY) + "*");
		if(domainKeysSet != null && domainKeysSet.size()>0) {
			String[] domainKeys = domainKeysSet.toArray(new String[0]);
			ForkJoinPool fjpool = new ForkJoinPool(64); 
			logger.info("Start Time for RecurssiveAction : "+(System.currentTimeMillis()));
	        JobsRecurssiveAction task = new JobsRecurssiveAction(domainKeys, 1, domainKeys.length, resourceCassandraService);
	        long start = System.currentTimeMillis();
	        fjpool.invoke(task);
	        logger.info("End Time for RecurssiveAction : "+(System.currentTimeMillis()));
	        logger.info("Parallel processing time: "    + (System.currentTimeMillis() - start)+ " ms");
		}
	}
	
	public ResourceCassandraService getResourceCassandraService() {
		return resourceCassandraService;
	}
}
