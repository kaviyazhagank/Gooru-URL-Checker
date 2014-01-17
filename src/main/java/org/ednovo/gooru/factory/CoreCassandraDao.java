///////////////////////////////////////////////////////////////////////////////////////////////
 // CoreCassandraDao.java
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

import java.util.Map;

import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.Rows;

public interface CoreCassandraDao {

	void save(String rowKey, Map<String, String> columns,ColumnFamilyType columnFamilyType);

	ColumnList<String> read(String rowKey,ColumnFamilyType columnFamilyType);
	
	void delete(String rowKey,ColumnFamilyType columnFamilyType);
	
	Map<String, String> readKeys(String rowKeys,ColumnFamilyType columnFamilyType);

	ColumnList<String> getResourceByGooruOid(String gooruOid,ColumnFamilyType columnFamilyType);

	Rows<String, String> getResourceFilterBy(String lastUpdated,String urlStatus,String resourceStatus, String indexingDone,ColumnFamilyType columnFamilyType);

	Integer getResourceStatusCount(String lastUpdated, String resourceStatus, ColumnFamilyType columnFamilyType);
		
}
