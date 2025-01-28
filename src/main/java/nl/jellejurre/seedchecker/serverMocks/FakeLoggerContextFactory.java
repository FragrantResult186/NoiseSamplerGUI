package nl.jellejurre.seedchecker.serverMocks;

import java.net.URI;

import nl.jellejurre.seedchecker.serverMocks.FakeLoggerContext;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.simple.SimpleLoggerContextFactory;

public class FakeLoggerContextFactory extends SimpleLoggerContextFactory {
    @Override
    public LoggerContext getContext(String fqcn, ClassLoader loader,
                                    Object externalContext, boolean currentContext) {
        return new nl.jellejurre.seedchecker.serverMocks.FakeLoggerContext();
    }

    @Override
    public LoggerContext getContext(String fqcn, ClassLoader loader,
                                    Object externalContext, boolean currentContext,
                                    URI configLocation, String name) {
        return new FakeLoggerContext();
    }
}
