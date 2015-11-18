import cal.Schedule;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import database.DatabaseConnection;
import factory.CalendarFactory;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.eclipse.jetty.servlet.DefaultServlet;
import rest.Book;
import rest.OfficeHours;
import rest.User;

import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.util.HashMap;

/**
 * Guice module for the entire application.
 */
public class ApplicationModule extends ServletModule {
    @Override
    protected void configureServlets() {


        bind(DefaultServlet.class).in(Singleton.class);
        bind(Book.class).in(Singleton.class);
        bind(User.class).in(Singleton.class);
        bind(OfficeHours.class).in(Singleton.class);

        install(new FactoryModuleBuilder()
                .build(CalendarFactory.class));

        bind(DatabaseConnection.class).in(Singleton.class);

        bind(MessageBodyReader.class).to(JacksonJsonProvider.class);
        bind(MessageBodyWriter.class).to(JacksonJsonProvider.class);

        HashMap<String, String> options = new HashMap<>();
        options.put("com.sun.jersey.api.json.POJOMappingFeature", "true");
        serve("/*").with(GuiceContainer.class, options);
    }
}
