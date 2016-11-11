package biz.paluch.logging.gelf.log4j;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import biz.paluch.logging.gelf.GelfMessageAssembler;
import biz.paluch.logging.gelf.intern.GelfMessage;
import biz.paluch.logging.gelf.intern.GelfSender;
import biz.paluch.logging.gelf.intern.GelfSenderConfiguration;
import biz.paluch.logging.gelf.intern.GelfSenderFactory;
import biz.paluch.logging.gelf.intern.GelfSenderProvider;

/**
 * @author Mark Paluch
 */
@RunWith(MockitoJUnitRunner.class)
public class GelfLogAppenderErrorsTest {

    public static final String THE_HOST = "the host";

    public static final LoggingEvent LOGGING_EVENT = new LoggingEvent("my.class", Logger.getLogger("my.class"),
            org.apache.log4j.Level.INFO, "message", null);

    @Mock
    private ErrorHandler errorHandler;

    @Mock
    private GelfSender sender;

    @Mock
    private GelfSenderProvider senderProvider;

    @Mock
    private GelfMessageAssembler assembler;

    private GelfLogAppender sut = new GelfLogAppender();

    @Before
    public void before() throws Exception {
        GelfSenderFactory.addGelfSenderProvider(senderProvider);

        when(assembler.getHost()).thenReturn(THE_HOST);
        when(senderProvider.supports(anyString())).thenReturn(true);
        sut.setErrorHandler(errorHandler);

    }

    @After
    public void after() throws Exception {
        GelfSenderFactory.removeGelfSenderProvider(senderProvider);
        GelfSenderFactory.removeAllAddedSenderProviders();
    }

    @Test
    public void testRuntimeExceptionOnCreateSender() throws Exception {
        sut.setGraylogHost(THE_HOST);
        when(assembler.getHost()).thenReturn(THE_HOST);
        when(senderProvider.create(any(GelfSenderConfiguration.class))).thenThrow(new IllegalStateException());

        sut.append(LOGGING_EVENT);

        verify(errorHandler, atLeast(1)).error(anyString(), any(IllegalStateException.class), anyInt());
    }

    @Test
    public void testInvalidMessage() throws Exception {

        when(senderProvider.create(any(GelfSenderConfiguration.class))).thenReturn(sender);
        when(sender.sendMessage(any(GelfMessage.class))).thenReturn(false);

        sut.append(LOGGING_EVENT);

        verify(errorHandler, atLeast(1)).error(anyString(), any(IllegalStateException.class), anyInt());
    }

    @Test
    public void testErrorOnSend() throws Exception {

        when(senderProvider.create(any(GelfSenderConfiguration.class))).thenReturn(sender);
        when(sender.sendMessage(any(GelfMessage.class))).thenThrow(new IllegalStateException());

        sut.append(LOGGING_EVENT);

        verify(errorHandler, atLeast(1)).error(anyString(), any(IllegalStateException.class), anyInt());
    }

    @Test(timeout = 500)
    public void gelfPortNotReachable() throws Exception {

        LogManager.getLoggerRepository().resetConfiguration();
        DOMConfigurator.configure(getClass().getResource("/log4j/log4j-gelf-not-reachable.xml"));

        Logger logger = Logger.getLogger(getClass());

        logger.info(LOGGING_EVENT);
    }
}
