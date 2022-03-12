package gasha.creativesecurity;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class DataLogFormatter
extends Formatter {
    private static final String format = "%1$tb %1$td, %1$tY %1$tl:%1$tM:%1$tS %1$Tp %2$s%n%4$s: <%7$s> %5$s%6$s%n";
    private final Date dat = new Date();

    @Override
    public synchronized String format(LogRecord record) {
        String source;
        this.dat.setTime(record.getMillis());
        if (record.getSourceClassName() != null) {
            source = record.getSourceClassName();
            if (record.getSourceMethodName() != null) {
                source = source + " " + record.getSourceMethodName();
            }
        } else {
            source = record.getLoggerName();
        }
        String message = this.formatMessage(record);
        String throwable = "";
        if (record.getThrown() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println();
            record.getThrown().printStackTrace(pw);
            pw.close();
            throwable = sw.toString();
        }
        return String.format(format, this.dat, source, record.getLoggerName(), record.getLevel().getLocalizedName(), message, throwable, record.getThreadID());
    }
}

