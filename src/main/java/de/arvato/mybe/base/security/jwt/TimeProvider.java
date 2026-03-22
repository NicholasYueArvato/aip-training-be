package de.arvato.mybe.base.security.jwt;

import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by stephan on 04.07.17.
 */
@Component
public class TimeProvider implements Serializable
{

    private static final long serialVersionUID = -3301695478208950415L;

    public Date now()
    {
        return new Date();
    }
}
