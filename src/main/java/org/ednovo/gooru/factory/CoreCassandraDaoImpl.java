///////////////////////////////////////////////////////////////////////////////////////////////
 // CoreCassandraDaoImpl.java
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
package org.ednovo.gooru.factory;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import junit.framework.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.netflix.astyanax.ExceptionCallback;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.Row;
import com.netflix.astyanax.model.Rows;
import com.netflix.astyanax.query.IndexQuery;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.util.RangeBuilder;

public abstract class CoreCassandraDaoImpl implements CoreCassandraDao {

	private static final Logger log = LoggerFactory.getLogger(CoreCassandraDaoImpl.class);

	@Autowired(required = false)
	private CassandraFactory cassandraFactory;

	private Map<String,ColumnFamily<String, String>> map = new HashMap<String,ColumnFamily<String, String>>();
	
	@PostConstruct
	public void init() {
		
		if (cassandraFactory.getKeyspace() != null) {
			
			for(ColumnFamilyType columnFamilyType : ColumnFamilyType.values()){
				log.info("Type of ColumnFamilyName :" +columnFamilyType);
				if(columnFamilyType.getName()!=null){
				map.put(columnFamilyType.getName(), new ColumnFamily<String, String>(columnFamilyType.getName(), StringSerializer.get(), StringSerializer.get()));
				}
			}
		} 
	}

	public CassandraFactory getCassandraFactory() {
		return cassandraFactory;
	}

	public void setCassandraFactory(CassandraFactory cassandraFactory) {
		this.cassandraFactory = cassandraFactory;
	}

	@Override
	public void save(String rowKey, Map<String, String> columns, ColumnFamilyType columnFamilyType) {
		MutationBatch mutation = getCassandraFactory().getKeyspace().prepareMutationBatch();
		if(columns!=null){
		for (Map.Entry<String, String> entry : columns.entrySet()) {
			mutation.withRow(map.get(columnFamilyType.getName()), rowKey).putColumn(entry.getKey(), entry.getValue());
		}
		}
		try {
			mutation.execute();
		} catch (ConnectionException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ColumnList<String> read(String rowKey,ColumnFamilyType columnFamilyType){
		OperationResult<ColumnList<String>> result;
		try {
			result = getCassandraFactory().getKeyspace().prepareQuery(map.get(columnFamilyType.getName())).getKey(rowKey).execute();
			ColumnList<String> record = result.getResult();
			return record;
		} catch (ConnectionException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public ColumnList<String> getResourceByGooruOid(String gooruOid,ColumnFamilyType columnFamilyType){
		ColumnList<String> resource = null;
    	try {
    		resource = getCassandraFactory().getKeyspace().prepareQuery(map.get(columnFamilyType.getName())).getKey(gooruOid).execute().getResult();
		} catch (ConnectionException e) {
			
			log.info("Error while retieveing data : {}" ,e);
		}
    	return resource;
	}
	
	@Override
	public Rows<String, String>  getResourceFilterBy(String lastUpdated,String urlStatus,String resourceStatus,String indexingDone,ColumnFamilyType columnFamilyType){
		
		Rows<String, String> resources = null;
		
		try {
		 	IndexQuery<String , String > resourcesStatus =	getCassandraFactory().getKeyspace().prepareQuery(map.get(columnFamilyType.getName())).searchWithIndex();
			
		 	resourcesStatus.setRowLimit(Integer.MAX_VALUE);
		 	
		 	if(lastUpdated != null && !lastUpdated.isEmpty()){
				resourcesStatus.addExpression().whereColumn("last_updated").equals().value(lastUpdated);
			}
			if(urlStatus != null && !urlStatus.isEmpty()){
				resourcesStatus.addExpression().whereColumn("url_status").equals().value(urlStatus);
			}
			if(indexingDone != null && !indexingDone.isEmpty()){
				resourcesStatus.addExpression().whereColumn("indexing_done").equals().value(indexingDone);
			}
			if(resourceStatus != null && !resourceStatus.isEmpty()){
				resourcesStatus.addExpression().whereColumn("resource_status").equals().value(resourceStatus);
			}
			resources =	resourcesStatus.execute().getResult();
			
		} catch (ConnectionException e) {
			e.printStackTrace();
		}
		
		return resources;
		
	}
	
	@Override
	public void delete(String rowKey,ColumnFamilyType columnFamilyType) {
		MutationBatch mutation = getCassandraFactory().getKeyspace().prepareMutationBatch();
		mutation.withRow(map.get(columnFamilyType.getName()), rowKey).delete();
		try {
			mutation.execute();
		} catch (ConnectionException e) {
			throw new RuntimeException(e);
		}
	}

	
	@Override
	public Map<String,String> readKeys(String rowKeys,ColumnFamilyType columnFamilyType){
		
		OperationResult<Rows<String, String>> result;
		Map<String, String> rowValue = new HashMap<String, String>();
		try{
			result = (OperationResult<Rows<String, String>>) getCassandraFactory().getKeyspace().prepareQuery(map.get(columnFamilyType.getName())).getAllRows().setRowLimit(100).withColumnRange(new RangeBuilder().setMaxSize(10).build()).setExceptionCallback(new ExceptionCallback(){
	            @Override
	            public boolean onException(ConnectionException e) {
	            	try{
	            		Thread.sleep(1000);
	            	Assert.fail(e.getMessage());
	            	}
	            	catch(InterruptedException i){
	            	}
	                return true;
	            }
		}).execute();
			Rows<String, String> record = result.getResult();
			for (Row<String, String> entry : record) {
				log.info("Row :"+ entry.getKey()+ " " + entry.getColumns().size());
				rowValue.put(entry.getKey(), entry.getColumns().size()+"");
			}
			return rowValue;
		}
		catch (ConnectionException e) {
		    Assert.fail();
		}
		return null;
	}
	
	@Override
	public Integer getResourceStatusCount(String lastUpdated, String resourceStatus, ColumnFamilyType columnFamilyType) {
		
		Integer resourceCount = null;
		
		try {
			
		 	IndexQuery<String , String > resourcesStatus =	getCassandraFactory().getKeyspace().prepareQuery(map.get(columnFamilyType.getName())).searchWithIndex();
			
		 	resourcesStatus.setRowLimit(Integer.MAX_VALUE);
		 	
		 	if(lastUpdated != null && !lastUpdated.isEmpty()){
				resourcesStatus.addExpression().whereColumn("last_updated").equals().value(lastUpdated);
			}
		 	
			if(resourceStatus != null && !resourceStatus.isEmpty()){
				resourcesStatus.addExpression().whereColumn("resource_status").equals().value(resourceStatus);
			}
			
			resourceCount = resourcesStatus.execute().getResult().size();
		} catch (ConnectionException e) {
			e.printStackTrace();
		}
		
		return resourceCount;
	
	}
}
