///////////////////////////////////////////////////////////////////////////////////////////////
 // ResourceCassandraServiceImpl.java
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.ednovo.gooru.dao.ResourceJobsRepositoryDAO;
import org.ednovo.gooru.factory.ColumnFamilyType;
import org.ednovo.gooru.factory.CoreCassandraDaoImpl;
import org.ednovo.gooru.model.ConfigSettings;
import org.ednovo.gooru.model.DomainList;
import org.ednovo.gooru.util.DateUtils;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import com.netflix.astyanax.model.ColumnList;


@Service
public class ResourceCassandraServiceImpl extends CoreCassandraDaoImpl implements ResourceCassandraService, GooruJobsConstants {


	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ResourceCassandraServiceImpl.class);
	
	@Autowired(required=false)
	private RedisTemplate<String, String> redisStringTemplate;
	
	@Autowired
	private ResourceJobsRepositoryDAO resourceRepositoryDAO;
	

	

	
	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		
		String jsonKey = "http://gdata.youtube.com/feeds/api/videos?q=" +url.substring(31,42)+"&max-results=1&v=2&alt=jsonc";

		InputStream inputStream = (InputStream) new URL(jsonKey).openStream();
	    try {
	      BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
	      String jsonText = readAll(rd);
	      JSONObject json = null;
			json = new JSONObject(jsonText.toString());
	      return json;
	    }
	    finally {
	    	inputStream.close();
	    }
	}
	
	private static String readAll(Reader rd) throws IOException {
	    StringBuilder stringBuilder = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) {
	      stringBuilder.append((char) cp);
	    }
	    return stringBuilder.toString();
	  }
	
	
	private boolean isConfidenceDomain(String domainName, Map<String , String> domainResources,ColumnList<String> domainValues){
		 String successCount = domainValues.getStringValue(FILE_OK,null);
		 String totalCount = domainValues.getStringValue(TOTAL_COUNT,null);
		 double ConfidencePercentage = 0;
		 logger.info("Number of time resource has checked => " +totalCount);
		 
		 //Calculate percentage for confident
		 if(successCount != null && totalCount != null){
			 int success = Integer.parseInt(successCount);
			 int total = Integer.parseInt(totalCount);
			  ConfidencePercentage = (success/total)*100;
			 logger.info("ConfidencePercentage ===> "+ ConfidencePercentage);
			 
		 }
		 if(ConfidencePercentage > 50 ){
			 return true;
		 }else{
			 return false;
		 }
		 	
	}
	
	public List<Object[]> getResourcesFromDb(int resourceSourceId,int resourceFrom){
		return resourceRepositoryDAO.getResourcesFromDb(resourceSourceId, resourceFrom);
	}
	
	public Integer urlStatusChecker(String url){
		
		HttpURLConnection connection = null;
		int responseCode ;
		try {
			connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod ("GET"); 
			connection.setRequestProperty("User-Agent", "Mozilla/5.0");
			responseCode = connection.getResponseCode();
			connection.setConnectTimeout(180000);
		} catch (Exception e) {
			if(connection != null){ 
				try {
					return connection.getResponseCode();
				} catch (IOException e1) {
					return 404;
				}
			}
			return 404;
		}
		return responseCode;
	}

	public Double calculateConfidential(DomainList domains){
		
		double confidencePercent = 1.0;
		
		if(domains.getOkCount() != 0){
			confidencePercent = domains.getOkCount()/domains.getResourceCount()*1;
		}
		if(domains.getFailCount() !=0){
			confidencePercent = (confidencePercent) * (domains.getFailCount()/domains.getResourceCount() * (-1.5));
		}
		if(domains.getTransientCount() != 0){
			confidencePercent = (confidencePercent) * (domains.getTransientCount()/domains.getResourceCount() * (0.50));
		}
		
		return confidencePercent;		
	}
	@Override
	public  List<DomainList> getValidDomain() {
		return resourceRepositoryDAO.getValidDomain();
		
	}

	@Override
	public void saveDomailList(DomainList domains ,Integer statusCode) {
		
		domains.setResourceSourceId(domains.getResourceSourceId());
		domains.setDomainName(domains.getDomainName());
		domains.setTotalChecked(domains.getTotalChecked()+1);
		domains.setResourceCount(domains.getResourceCount()+1);
		if (statusCode == 200 || statusCode == 302 || statusCode == 301) {
			domains.setOkCount(domains.getOkCount() + 1);
		} else if (statusCode == 400 || statusCode == 404) {
			domains.setFailCount(domains.getOkCount() + 1);
		} else {
			domains.setTransientCount(domains.getTransientCount()+1);
		}
	   
		double confidencePerentage = calculateConfidential(domains);
		domains.setLastCheckedDate(new Date());
		domains.setConfidenceLevel(confidencePerentage);
		resourceRepositoryDAO.saveDomailList(domains);
	}	
	
	public void saveResourceStatus(String url,Integer statusCode,String gooruOid,boolean resourceInsdexStatus){
		
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		String indexing_done = "0";
		if(resourceInsdexStatus){
			indexing_done = "1";
		}
		dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		Map<String, String> resourceStatus = new HashMap<String, String>();
		resourceStatus.put(URL, url);
		resourceStatus.put(LAST_UPDATED,dateFormatter.format(new Date()).toString());
		resourceStatus.put(URL_STATUS,statusCode.toString());
		resourceStatus.put(INDEXING_DONE,indexing_done);
		if (statusCode == 200 || statusCode == 302 || statusCode == 301) {
			resourceStatus.put(RESOURCE_STATUS,"1");
		} else {
			resourceStatus.put(RESOURCE_STATUS, "0");
		}
		this.save(gooruOid,resourceStatus,ColumnFamilyType.RESOURCE_STATUS);
		
	}

	@Override
	public void resetDomainList(DomainList domains){
		resourceRepositoryDAO.resetDomainList(domains);
	}
	
	@Override
	public void stautsCheckerUrl(DomainList domains) {

		long start = System.currentTimeMillis();
		
		List<Object[]>  resources = resourceRepositoryDAO.getResourcesFromDb(domains.getResourceSourceId(), domains.getTotalChecked());
		if (resources != null && resources.size() > 0) {
			for (Object[] resource : resources) {	
				String urls = (String)resource[0];
				boolean has_framebreaker = (resource[1] != null) ?(Boolean) resource[1]:false;
				String gooruIds = (String) resource[2];  
				logger.info("URL :"+ urls +" - has_frame_breaker :"+has_framebreaker +" - GooruOid : "+gooruIds);
					ColumnList<String> gooruOids = this.read(gooruIds,ColumnFamilyType.RESOURCE_STATUS);
					Integer statusCode = null;
					if(gooruOids != null) {
						if(gooruOids.isEmpty()) {
							saveOrUpdateUrlStatusCheck(urls, statusCode, has_framebreaker, gooruIds, domains);
						} else {
							String lastUpdatedDate   = gooruOids.getColumnByName(LAST_UPDATED).getStringValue();
							Date updatedDate= DateUtils.stringDateToDate(lastUpdatedDate);
							Date currentDate = DateUtils.stringDateToDate(DateUtils.dateFormat(DateUtils.getCurrentDate()));
							
							Integer noOfDaysBetweenTwoDays = DateUtils.getDayDifference(updatedDate, currentDate);
							
							if (noOfDaysBetweenTwoDays >= 7 && isConfidenceDomain(domains.getDomainName(), null, gooruOids )) {
								saveOrUpdateUrlStatusCheck(urls, statusCode, has_framebreaker, gooruIds, domains);
							}
							else if (domains.getTransientCount() >= 0 || domains.getConfidenceLevel() < 0) {
								saveOrUpdateUrlStatusCheck(urls, statusCode, has_framebreaker, gooruIds, domains);
							} 
						}
                       
                     } 
				}
			}
		
		long end = System.currentTimeMillis();
	    logger.info("Time taken to Check resources from the DOMAIN: "+ domains.getDomainName() + " :  " + (end-start) +" ms STATUS \n");
	

	}
	
	private boolean getFrameBreakerStatus(Integer status){
		int hasFrameBreaker_new= 0;
		 if(status == 200 || status == 301 || status == 302){
			 hasFrameBreaker_new = 0;
		 } else if(status == 404 || status == 500 || status == 401){
			 hasFrameBreaker_new = 1;
		 }
		 if((hasFrameBreaker_new == 1)){
			 return true;
		 }
		 
		return false;
	}
	
	public boolean updateResourceStatusAndIndex(String gooruOid,Integer status,boolean hasFrameBreaker_new){
		
		boolean indexDone = false;
		resourceRepositoryDAO.updateResourceStatus(gooruOid, status, hasFrameBreaker_new);
		
		if((getConfigSettings(ConfigSettings.INDEX_REQUIRED)).equalsIgnoreCase("yes")){
			Representation representation = null;
			ClientResource indexResult = null;
			Form forms = new Form();
			
			indexResult = new ClientResource(getConfigSettings(ConfigSettings.REINDEXING_DOMAIN)+"gooruapi/rest/index/es-aca/resource/index");
			
			forms.add("sessionToken", getConfigSettings(ConfigSettings.SESSION_TOKEN));
			forms.add("ids", gooruOid);
			
			try{
				representation = indexResult.post(forms.getWebRepresentation());
			}catch(Exception e){
				logger.info("Could not post Index call: {}",e);
				return indexDone;
			}
			int indexStatus = indexResult.getResponse().getStatus().getCode();
		
			if(indexStatus == 200){
				indexDone = true;
			}
		}
		return indexDone;
	}
	
	@Override
	public String getConfigSettings(ConfigSettings Key){
		return resourceRepositoryDAO.getConfigSettings(Key);
	}

	@Override
	public void deleteKey(String key) {
		redisStringTemplate.delete(key);
	}
	
	@Override
	public void putRedisValue(String key, String value) {

		ValueOperations<String, String> valueOperations = getValueOperation();
		valueOperations.set(key, value);
		
	}
	@Override
	public String getRedisValue(String key) {
		ValueOperations<String, String> valueOperations = getValueOperation();
	    return valueOperations.get(key);
	}
	
	private ValueOperations<String, String> getValueOperation(){
		RedisTemplate<String, String> redisStringTemplate = getRedisStringTemplate();
		ValueOperations<String, String> valueOps = redisStringTemplate.opsForValue();
		return valueOps;
	}

	private RedisTemplate<String, String> getRedisStringTemplate(){
		
		  final StringRedisSerializer STRING_SERIALIZER = new StringRedisSerializer();
		  
		  redisStringTemplate.setKeySerializer(STRING_SERIALIZER);
		  
		  redisStringTemplate.setValueSerializer(STRING_SERIALIZER);

		  return redisStringTemplate;
	}
	
	private enum LongSerializer implements RedisSerializer<Long> {

		  INSTANCE;

		  @Override 
		  public byte[] serialize(Long aLong) throws SerializationException {
		    if (null != aLong) {
		      return aLong.toString().getBytes();
		    } 
		    else {
		      return new byte[0];
		    }
		  }

		  @Override 
		  public Long deserialize(byte[] bytes) throws SerializationException {
		    if (bytes != null && bytes.length > 0) {
		      return Long.parseLong(new String(bytes));
		    } 
		    else {
		      return null;
		    }
		  }
	}

	@Override
	public Set<String> getAllRedisKeys(String pattern) {
		return redisStringTemplate.keys(pattern);
	}

	private void saveOrUpdateUrlStatusCheck(String urls, Integer statusCode, boolean has_framebreaker, String gooruIds, DomainList domains) {
		boolean resourceIndexStatus = false;
		statusCode = urlStatusChecker(urls);
		System.out.println("statusCode-jobs ::" + statusCode );
		boolean value_new_framebreaker = getFrameBreakerStatus(statusCode);
		if(value_new_framebreaker != has_framebreaker)
		{
			 resourceIndexStatus= updateResourceStatusAndIndex(gooruIds,statusCode,value_new_framebreaker);
		}
		saveResourceStatus(urls,statusCode,gooruIds,resourceIndexStatus);
		saveDomailList(domains,statusCode);
	}
	
}
