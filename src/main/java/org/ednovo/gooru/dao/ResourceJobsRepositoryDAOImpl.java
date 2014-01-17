///////////////////////////////////////////////////////////////////////////////////////////////
 // ResourceJobsRepositoryDAOImpl.java
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
package org.ednovo.gooru.dao;

import java.util.List;

import org.ednovo.gooru.model.ConfigSettings;
import org.ednovo.gooru.model.DomainList;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class ResourceJobsRepositoryDAOImpl implements ResourceJobsRepositoryDAO {
	
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ResourceJobsRepositoryDAOImpl.class);
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private SessionFactory sessionFactoryProd;

	@Override
	public List<DomainList> getValidDomain() {
		
		String hql = null;
		hql = "FROM DomainList ORDER BY total_checked ASC";
		Session session = sessionFactory.getCurrentSession();
		Transaction transaction = null;
		List<DomainList> domainList = null;
		try {
		transaction = session.beginTransaction();
		domainList =  session.createQuery(hql).list();
		transaction.commit();
		} 
		catch (RuntimeException ex) {
			transaction.rollback();
			logger.info(""+ex);
		}
		return   (List<DomainList>) domainList;	
	}

	@Override
	public void saveDomailList(DomainList domains) {
		
		Transaction transaction = null;
		try {
			Session session = sessionFactory.getCurrentSession();
			transaction = session.beginTransaction();
			session.saveOrUpdate(domains);	
			transaction.commit();
		} 
		catch (RuntimeException ex) {
			transaction.rollback();
			logger.info(""+ex);
		}
	}

	@Override
	public void updateResourceStatus(String gooruOid, Integer status, boolean hasFrameBreaker_new) {
		
		String query = "UPDATE resource r INNER JOIN content c ON c.content_id = r.content_id SET r.has_frame_breaker = "+ hasFrameBreaker_new +" WHERE c.gooru_oid = '"+ gooruOid + "'";
		Transaction transaction = null;
		try{
		Session session = sessionFactoryProd.getCurrentSession();;
		transaction = session.beginTransaction();
		SQLQuery sqlQuery = session.createSQLQuery(query);
		session.flush();
		sqlQuery.executeUpdate();
		transaction.commit();
		}catch(RuntimeException e){
			transaction.rollback();
			logger.info("Error in update resource : {}",e);
		}
	}

	@Cacheable("jobCache")
	@Override
	public String getConfigSettings(ConfigSettings Key){
		
		String sql = "SELECT constant_value FROM job_config_settings WHERE constant_name = '"+Key+"'";
		Session session = sessionFactory.getCurrentSession();
		Transaction transaction = null;
		List<String> constants = null;
		try {
			transaction = session.beginTransaction();
			constants = session.createSQLQuery(sql).list();
			transaction.commit();
		} catch(RuntimeException ex){
			transaction.rollback();
			logger.info("Error in update resource : {}",ex);
		}
		return constants.get(0);
	}

	@Override
	public List<Object[]> getResourcesFromDb(int resourceSourceId,int resourceFrom) {
		
		List<Object[]> resources = null;
		String sql = null;
		sql = "SELECT r.url,r.has_frame_breaker,c.gooru_oid FROM resource r INNER JOIN content c ON c.content_id = r.content_id WHERE r.resource_source_id = "+resourceSourceId+"  LIMIT "+resourceFrom+",10";
				
		Transaction transaction = null;
		try {
			Session session = sessionFactoryProd.getCurrentSession();
			transaction = session.beginTransaction();
			resources =  session.createSQLQuery(sql).list();
			transaction.commit();
		}catch(Exception e){
			transaction.rollback();
			logger.info(""+e);
		}
		return resources;
	}

	@Override
	public void resetDomainList(DomainList domains) {
		domains.setTotalChecked(0);
		domains.setOkCount(0);
		domains.setFailCount(0);
		domains.setTransientCount(0);
		domains.setConfidenceLevel(0.0);
		Transaction transaction = null;
		try {
		Session session = sessionFactory.getCurrentSession();
		transaction = session.beginTransaction();
		session.saveOrUpdate(domains);
		transaction.commit();
		} catch(RuntimeException ex) {
			transaction.rollback();
			logger.info(""+ex);
		}
		
	}

}
