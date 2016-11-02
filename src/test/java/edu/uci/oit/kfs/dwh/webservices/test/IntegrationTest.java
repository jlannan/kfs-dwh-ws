package edu.uci.oit.kfs.dwh.webservices.test;

import java.util.Collections;

import javax.ws.rs.core.MediaType;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import edu.uci.oit.kfs.dwh.webservices.DWHRestServerImpl;
import edu.uci.oit.kfs.dwh.webservices.wsresult.KFSAccountingLineValidationResult;
import edu.uci.oit.kfs.dwh.webservices.wsresult.KfsToUcAccountingLineTranslationResult;

public class IntegrationTest extends Assert {

	private final static String ENDPOINT_ADDRESS = "http://localhost:8181/cxf/DWHRESTServer";
	private static Server server;
	private static ClassPathXmlApplicationContext springContext;

	@BeforeClass
	public static void initialize() throws Exception {
		startServer();
	}

	@AfterClass
	public static void destroy() throws Exception {
		springContext.close();
		server.stop();
		server.destroy();
	}

	private static void startServer() throws Exception {

		springContext = new ClassPathXmlApplicationContext("META-INF/spring/spring.xml");
		DWHRestServerImpl restService = (DWHRestServerImpl) springContext.getBean("restService");

		JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
		sf.setProvider(new JacksonJsonProvider());
		sf.setResourceClasses(DWHRestServerImpl.class);
		sf.setServiceBean(restService);
		sf.setAddress(ENDPOINT_ADDRESS);

		server = sf.create();
	}

	@Test
	public void testAccountingLineTranslation() {

		WebClient client = WebClient.create(ENDPOINT_ADDRESS + "/rest/coa/accountingLine/translate", Collections.singletonList(new JacksonJsonProvider()));
		client.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).query("kfsFiscalYear", "2017").query("kfsChartCode", "IR").query("kfsAccountNumber", "BF10001")
				.query("kfsObjectCode", "0000");

		KfsToUcAccountingLineTranslationResult result = client.get(KfsToUcAccountingLineTranslationResult.class);
		assertTrue(result.isValid());
		assertEquals("Success", result.getMessage());
		assertEquals("9", result.getUcLocationCode());
		assertEquals("899903", result.getUcAccountNumber());
		assertEquals("89990", result.getUcFundNumber());
		assertEquals("1000", result.getUcObjectCode());
		assertEquals("0", result.getUcSubCode());
		
	}

	@Test
	public void testAccountingLineValidation() {

		WebClient client = WebClient.create(ENDPOINT_ADDRESS + "/rest/coa/accountingLine/validate", Collections.singletonList(new JacksonJsonProvider()));
		client.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).query("kfsFiscalYear", "2017").query("kfsOriginationCode", "AR").query("kfsChartCode", "IR").query("kfsAccountNumber", "SS10571")
			.query("kfsSubAccountNumber", "LONGB").query("kfsObjectCode", "ASTS").query("kfsProjectCode", "123");

		KFSAccountingLineValidationResult result = client.get(KFSAccountingLineValidationResult.class);
		assertTrue(result.isValid());
		assertEquals("Success", result.getMessage());
	}

}
