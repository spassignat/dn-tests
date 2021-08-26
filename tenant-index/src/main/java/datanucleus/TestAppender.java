package datanucleus;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.spi.LoggingEvent;

import static org.junit.Assert.fail;

public class TestAppender extends ConsoleAppender {
	String failMessage;

	public TestAppender() {
		setImmediateFlush(true);
	}

	public TestAppender(String failMessage) {
		this.failMessage = failMessage;
	}

	@Override
	public void doAppend(LoggingEvent loggingEvent) {
		super.doAppend(loggingEvent);
		if (loggingEvent.getMessage().equals(failMessage)) {
			fail(failMessage);
		}
	}
}
