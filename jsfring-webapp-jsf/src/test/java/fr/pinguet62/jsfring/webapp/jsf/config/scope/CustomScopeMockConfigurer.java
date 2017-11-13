package fr.pinguet62.jsfring.webapp.jsf.config.scope;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.beans.factory.config.Scope;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.web.context.WebApplicationContext.SCOPE_SESSION;

/**
 * Mock for webapp session {@link Scope}.<br>
 * Permit to {@link Autowired} {@link Bean} into JUnit tests.
 *
 * @see WebApplicationContext#SCOPE_SESSION
 */
@Component
public class CustomScopeMockConfigurer extends CustomScopeConfigurer {

    public CustomScopeMockConfigurer() {
        addScope(SCOPE_SESSION, new StaticScope());
    }

}

/**
 * Workaround for <b>Singleton</b> {@link Scope}.
 */
@Component
class StaticScope implements Scope {

    private Object instance;

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        if (instance == null)
            instance = objectFactory.getObject();
        return instance;
    }

    @Override
    public String getConversationId() {
        return null;
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
    }

    @Override
    public Object remove(String name) {
        return null;
    }

    @Override
    public Object resolveContextualObject(String key) {
        return null;
    }

}