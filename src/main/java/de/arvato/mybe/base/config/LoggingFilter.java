package de.arvato.mybe.base.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoggingFilter extends OncePerRequestFilter
{
    private static final Logger LOG = LoggerFactory.getLogger(LoggingFilter.class);

    private boolean logCall = true;
    private static boolean logIp = true;

    public LoggingFilter logCall(boolean logCall)
    {
        this.logCall = logCall;
        return this;
    }

    public LoggingFilter logIp(boolean logIp)
    {
        LoggingFilter.logIp = logIp;
        return this;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        boolean isFirstRequest = !this.isAsyncDispatch(request);

        if (isFirstRequest)
        {
            beforeRequest(request);
        }

        try
        {
            filterChain.doFilter(request, response);
        }
        finally
        {
            if (!this.isAsyncStarted(request))
            {
                afterRequest(request);
            }

        }

    }

    @Override
    public void destroy()
    {
        LOG.info("*** Destroy MDC Filter");
    }


    public void beforeRequest(HttpServletRequest request)
    {
        registerLogger(request);
        if (logCall && LOG.isInfoEnabled())
        {
            LOG.info("Service called for request: {} {}", request.getMethod(), getFullURL(request));
        }
    }

    public static void registerLogger(HttpServletRequest request)
    {
//        if (MDC.get("requestId") == null)
//        {
//            MDC.put("requestId", getRequestId(request));
//        }

        if (MDC.get("sessionId") == null)
        {
            MDC.put("sessionId", getSessionId(request));
        }
    }

//    public static String getRequestId(HttpServletRequest request)
//    {
//        if (request.getHeader("X-Request-Id") != null)
//        {
//            return request.getHeader("X-Request-Id");
//        }
//        else
//        {
//            return String.valueOf(System.currentTimeMillis());
//        }
//    }

    public static String getSessionId(HttpServletRequest request)
    {
        if (request.getHeader("X-Session-Id") != null)
        {
            return request.getHeader("X-Session-Id");
        }
        else if (request.getRemoteUser() != null)
        {
            StringBuilder sb = new StringBuilder(150);

            sb.append(request.getRemoteUser());

            appendSuffix(request, sb);

            return sb.toString();
        }
        else if (request.getAttribute("Cust-Session-Id") != null)
        {
            StringBuilder sb = new StringBuilder(150);

            sb.append(request.getAttribute("Cust-Session-Id"));

            appendSuffix(request, sb);

            return sb.toString();
        }
        else
        {
            StringBuilder sb = new StringBuilder(150);

            appendSuffix(request, sb);

            return sb.toString();
        }
    }

    public static void setCustomIdentifier(HttpServletRequest request, String identifier)
    {
        request.setAttribute("Cust-Session-Id", identifier);
        MDC.put("sessionId", getSessionId(request));
    }

    public static void afterRequest(HttpServletRequest request)
    {
//        MDC.remove("requestId");
        MDC.remove("sessionId");
    }

    private static String getFullURL(HttpServletRequest request)
    {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();

        if (queryString == null)
        {
            return requestURL.toString();
        }
        else
        {
            return requestURL.append('?').append(queryString).toString();
        }
    }

    private static void appendSuffix(HttpServletRequest request, StringBuilder sb)
    {
        if (request.getContentLengthLong() > -1)
        {
            sb.append(request.getContentLengthLong());
        }

        if (logIp)
        {
            sb.append("-").append(request.getRemoteAddr());
        }
    }


}
