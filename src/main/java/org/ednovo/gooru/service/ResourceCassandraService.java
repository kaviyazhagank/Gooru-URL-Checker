///////////////////////////////////////////////////////////////////////////////////////////////
 // ResourceCassandraService.java
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

import java.util.List;
import java.util.Set;

import org.ednovo.gooru.factory.CoreCassandraDao;
import org.ednovo.gooru.model.ConfigSettings;
import org.ednovo.gooru.model.DomainList;


 public interface ResourceCassandraService extends CoreCassandraDao {
	
	 List<Object[]> getResourcesFromDb(int resourceSourceId,int maxRow);
	
	 Integer urlStatusChecker(String url);
	
	 List<DomainList> getValidDomain();
	
	 void saveDomailList(DomainList domainList,Integer statusCode);
	
	 void saveResourceStatus(String url,Integer statusCode,String gooruOid,boolean resourceInsdexStatus);
	
	 Double calculateConfidential(DomainList domainList);
	
	 void stautsCheckerUrl(DomainList domainList); 
	
	 boolean updateResourceStatusAndIndex(String gooruOid , Integer status,boolean frameBreaker);
	
	 void resetDomainList(DomainList domains);
	
	 void putRedisValue(String key, String value);
	
	 Set<String> getAllRedisKeys(String pattern);

	 String getRedisValue(String key);

	 void deleteKey(String key);
	
	 String getConfigSettings(ConfigSettings Key);
	
}
