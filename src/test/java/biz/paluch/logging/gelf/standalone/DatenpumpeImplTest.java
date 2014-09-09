package biz.paluch.logging.gelf.standalone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import biz.paluch.logging.gelf.GelfTestSender;
import biz.paluch.logging.gelf.intern.GelfMessage;

public class DatenpumpeImplTest {
    @Before
    public void before() throws Exception {
        GelfTestSender.getMessages().clear();
    }

    @Test
    public void testBean() throws Exception {
        MyBean bean = new MyBean();

        DefaultGelfSenderConfiguration configuration = new DefaultGelfSenderConfiguration();
        configuration.setHost("test:static");

        DatenpumpeImpl datenpumpe = new DatenpumpeImpl(configuration);

        datenpumpe.submit(bean);

        assertEquals(1, GelfTestSender.getMessages().size());

        GelfMessage gelfMessage = GelfTestSender.getMessages().get(0);

        assertEquals("value field", gelfMessage.getField("value"));
        assertEquals("true", gelfMessage.getField("boolean"));
        assertNotNull(gelfMessage.getField("object"));

        assertEquals(3, gelfMessage.getAdditonalFields().size());

        datenpumpe.close();

        // additional check for NPE
        datenpumpe.close();

    }

    @Test
    public void testShoppingCart() throws Exception {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setCartId("the cart id");
        shoppingCart.setAmount(9.27);
        shoppingCart.setCustomerId("the customer id");

        DefaultGelfSenderConfiguration configuration = new DefaultGelfSenderConfiguration();
        configuration.setHost("test:static");

        DatenpumpeImpl datenpumpe = new DatenpumpeImpl(configuration);

        datenpumpe.submit(shoppingCart);

        assertEquals(1, GelfTestSender.getMessages().size());

        GelfMessage gelfMessage = GelfTestSender.getMessages().get(0);

        System.out.println(gelfMessage.toJson());

    }
}
