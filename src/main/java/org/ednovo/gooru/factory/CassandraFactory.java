///////////////////////////////////////////////////////////////////////////////////////////////
 // CassandraFactory.java
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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.ednovo.gooru.hibernate.configsettings.ConfigSettingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolType;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;

@Component
public final class CassandraFactory {
	
	private static CassandraFactory factory;

	private enum CassandraConstant {

		CQL_VERSION("cassandra.cqlversion", "3.0.0"),

		VERSION("cassandra.version", "1.2"),

		CLUSTER_NAME("cassandra.clustername", "Test Cluster"),

		SEED("cassandra.seed", "localhost:9160"),

		KEYSPACE_NAME("cassandra.keyspacename", "gooru");

		String defaultValue;

		String key;

		private CassandraConstant(String key, String defaultValue) {
			this.defaultValue = defaultValue;
			this.key = key;
		}

		/**
		 * @return the defaultValue
		 */
		public String getDefaultValue() {
			return defaultValue;
		}

		/**
		 * @return the key
		 */
		public String getKey() {
			return key;
		}

	}

	private static final Logger LOG = LoggerFactory.getLogger(CassandraFactory.class);

	private static final String CONNECTION_POOL = "MyConnectionPool";

	private Keyspace keyspace;

	private AstyanaxContext<Keyspace> astyanaxContext;

	@Autowired(required = false)
	private ConfigSettingRepository configSettingRepository;
	
	public CassandraFactory() {
		factory = this;
	}

	@PostConstruct
	public void init() {
		try {
			this.astyanaxContext = new AstyanaxContext.Builder()
					.forCluster(getSetting(CassandraConstant.CLUSTER_NAME))
					.forKeyspace(getSetting(CassandraConstant.KEYSPACE_NAME))
					.withAstyanaxConfiguration(new AstyanaxConfigurationImpl()
						.setDiscoveryType(NodeDiscoveryType.RING_DESCRIBE)
						.setConnectionPoolType(ConnectionPoolType.TOKEN_AWARE)
						.setCqlVersion(getSetting(CassandraConstant.CQL_VERSION))
						.setTargetCassandraVersion(getSetting(CassandraConstant.VERSION)))
					.withConnectionPoolConfiguration(new ConnectionPoolConfigurationImpl(CONNECTION_POOL)
						.setMaxConnsPerHost(20)
						.setInitConnsPerHost(10)
						.setSeeds(getSetting(CassandraConstant.SEED)))
					.withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
					.buildKeyspace(ThriftFamilyFactory.getInstance());
			this.astyanaxContext.start();
			this.keyspace = this.astyanaxContext.getClient();

			// test the connection
			this.keyspace.describeKeyspace();
		} catch (Throwable e) {
			LOG.error("Could not connect to cassandra : "+ e.getMessage());
		}
	}

	@PreDestroy
	public void cleanup() {
		this.astyanaxContext.shutdown();
	}
	
	public static boolean isInstantiated() {
		return factory != null && factory.getKeyspace() != null;
	}
	
	public Keyspace getKeyspace() {
		return keyspace;
	}

	private String getSetting(CassandraConstant constant) {
		String value = configSettingRepository != null ? configSettingRepository.getConfigSetting(constant.getKey()) : constant.getDefaultValue();
		return value != null && value.length() > 0 ? value : constant.getDefaultValue();
	}

}
