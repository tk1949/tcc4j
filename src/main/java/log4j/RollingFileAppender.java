package log4j;

import org.apache.log4j.Priority;

/**
 * Custom log appender
 */
public class RollingFileAppender extends org.apache.log4j.RollingFileAppender
{
    public boolean isAsSevereAsThreshold(Priority priority)
    {
        return this.getThreshold().equals(priority);
    }
}