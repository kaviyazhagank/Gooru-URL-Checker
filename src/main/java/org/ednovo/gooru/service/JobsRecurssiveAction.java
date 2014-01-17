///////////////////////////////////////////////////////////////////////////////////////////////
 // JobsRecurssiveAction.java
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

package org.ednovo.gooru.service;

import java.io.IOException;
import java.util.concurrent.RecursiveAction;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.ednovo.gooru.controller.ResourceCassandraRestController;
import org.ednovo.gooru.model.DomainList;
import org.slf4j.LoggerFactory;



public class JobsRecurssiveAction extends RecursiveAction {
	
	private static final long serialVersionUID = 1L;

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ResourceCassandraRestController.class);
	
	private ResourceCassandraService resourceCassandraService;
	
    private final String[] domainsList;
    private final Integer low;
    private final Integer high;
    private static final int THRESHOLD = 3500;
    
    public JobsRecurssiveAction(String[] domainsList, int low, int high, ResourceCassandraService resourceCassandraService) {
        this.domainsList = domainsList;
        this.low = low;
        this.high= high;
        this.resourceCassandraService = resourceCassandraService;
    }
    
    void doLeft(int l, int h) throws JsonParseException, JsonMappingException, IOException {
    	ObjectMapper mapper =null;
    	for (int i = l; i < h; ++i) 
     		{
    			mapper = new ObjectMapper();
    			DomainList domainList = null;
    			domainList = mapper.readValue(resourceCassandraService.getRedisValue(domainsList[i]), DomainList.class);
    			if(domainList != null) {
    				resourceCassandraService.deleteKey(domainsList[i]);
    				int totalResources = domainList.getResourceCount();
    				int totalCheckedResources = domainList.getTotalChecked();
    				if(totalResources == totalCheckedResources){
    					resourceCassandraService.resetDomainList(domainList);
    				}
    				resourceCassandraService.stautsCheckerUrl(domainList);
			 	logger.info("domainList Domain :{} :: resourceCount:{}",domainList.getDomainName(),domainList.getResourceCount());
        	}
     	}
   }
    
   @Override
   protected void compute() {
   
         if (high - low < THRESHOLD) {
        	 ObjectMapper mapper = null;
     	  	 for (int i = low; i < high; ++i){
     	  		mapper = new ObjectMapper();
     	  		DomainList domainList = null;
     		    try {
     			 domainList = mapper.readValue(resourceCassandraService.getRedisValue(domainsList[i]), DomainList.class);
     			 if(domainList != null) {
     				 resourceCassandraService.deleteKey(domainsList[i]);
     				 int totalResources = domainList.getResourceCount();
     				 int totalCheckedResources = domainList.getTotalChecked();
     				 if(totalResources == totalCheckedResources){
     					 resourceCassandraService.resetDomainList(domainList);
     				 }
     				 resourceCassandraService.stautsCheckerUrl(domainList);
     				 logger.info("domainList Domain :{} :: resourceCount:{}",domainList.getDomainName(),domainList.getResourceCount());
     			 }
     		} catch (Exception ex ) {
     			ex.printStackTrace();
     		} 
           }
         } else {
           Integer middile = (low + high) >>> 1;
           JobsRecurssiveAction left = new JobsRecurssiveAction(domainsList, low, middile, resourceCassandraService);
           JobsRecurssiveAction right = new JobsRecurssiveAction(domainsList, middile, high, resourceCassandraService);
           left.fork();
           right.compute();
           left.join(); 
      }
   }
}

