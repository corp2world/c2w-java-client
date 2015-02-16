package com.c2w.client.core;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.c2w.client.core.message.MessageDeliveryTest;
import com.c2w.client.core.message.MessageResponseTest;
import com.c2w.client.core.message.MessageTest;
import com.c2w.client.core.message.PropertiesModelTest;
import com.c2w.client.core.service.ResultTest;
import com.c2w.client.core.service.ServiceFactoryTest;
import com.c2w.client.core.service.http.HttpServiceTest;

@RunWith(Suite.class)
@SuiteClasses({
	MessageTest.class,
	MessageResponseTest.class,
	MessageDeliveryTest.class,
	PropertiesModelTest.class,
	ResultTest.class,
	ServiceFactoryTest.class,
	HttpServiceTest.class
})
public class AllTests {

}
