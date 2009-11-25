//========================================================================
//Copyright 2007-2009 David Yu dyuproject@gmail.com
//------------------------------------------------------------------------
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at 
//http://www.apache.org/licenses/LICENSE-2.0
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//========================================================================

package com.dyuproject.demos.showcase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jra.Get;
import org.codehaus.jra.HttpResource;

import com.dyuproject.oauth.Constants;
import com.dyuproject.oauth.Consumer;
import com.dyuproject.oauth.ConsumerContext;
import com.dyuproject.oauth.Endpoint;
import com.dyuproject.oauth.HttpAuthTransport;
import com.dyuproject.oauth.Token;
import com.dyuproject.oauth.TokenExchange;
import com.dyuproject.oauth.Transport;
import com.dyuproject.util.http.HttpConnector;
import com.dyuproject.util.http.UrlEncodedParameterMap;
import com.dyuproject.util.http.HttpConnector.Parameter;
import com.dyuproject.util.http.HttpConnector.Response;
import com.dyuproject.web.rest.RequestContext;
import com.dyuproject.web.rest.service.AbstractService;

/**
 * @author David Yu
 * @created Jun 17, 2009
 */

public final class OAuthGoogleService extends AbstractService
{
    
    static final Consumer __consumer = Consumer.getInstance();
    static final Endpoint __googleEndpoint = __consumer.getEndpoint("www.google.com");
    
    static void invalidate(HttpServletRequest request, HttpServletResponse response) 
    throws IOException
    {
        __consumer.invalidate(__googleEndpoint.getConsumerKey(), request, response);
    }

    @Override
    protected void init()
    {
        
    }
    
    @HttpResource(location="/oauth/google/$")
    @Get
    public void service(RequestContext rc) throws IOException
    {
        HttpServletRequest request = rc.getRequest();
        HttpServletResponse response = rc.getResponse();
        String type = rc.getPathElement(2);
        GoogleModule module = GoogleModule.get(type);
        if(module==null)
        {            
            response.sendRedirect("/oauth/google");
            return;
        }
        
        Token token = __consumer.getToken(__googleEndpoint.getConsumerKey()+type, request);
        switch(token.getState())
        {
            case Token.UNITIALIZED:
                UrlEncodedParameterMap params = new UrlEncodedParameterMap()
                    .add("scope", module.getScope())
                    .add(Constants.OAUTH_CALLBACK, request.getRequestURL().toString());
                    
                Response r = __consumer.fetchToken(__googleEndpoint, params, TokenExchange.REQUEST_TOKEN, 
                        token);
                if(r.getStatus()==200 && token.getState()==Token.UNAUTHORIZED)
                {
                    // unauthorized request token
                    __consumer.saveToken(token, request, response);
                    StringBuilder urlBuffer = Transport.buildAuthUrl(__googleEndpoint.getAuthorizationUrl(), 
                            token, null);
                    Transport.appendToUrl("hd", "default", urlBuffer);
                    response.sendRedirect(urlBuffer.toString());
                }
                break;
                
            case Token.UNAUTHORIZED:
                if(token.authorize(request.getParameter(Constants.OAUTH_TOKEN), 
                        request.getParameter(Constants.OAUTH_VERIFIER)))
                {
                    if(fetchAccessToken(token, request, response))
                        query(module.getUrl(), token, request, response);
                    else
                        __consumer.saveToken(token, request, response);
                }
                break;
                
            case Token.AUTHORIZED:
                if(fetchAccessToken(token, request, response))
                    query(module.getUrl(), token, request, response);
                break;
                
            case Token.ACCESS_TOKEN:
                query(module.getUrl(), token, request, response);
                break;
                
            default:
                response.sendRedirect(request.getContextPath() + "/index.html");
        }
    }
    
    public boolean fetchAccessToken(Token token, HttpServletRequest request, 
            HttpServletResponse response) throws IOException
    {
        // authorized request token
        UrlEncodedParameterMap params = new UrlEncodedParameterMap();
        
        Response r = __consumer.fetchToken(__googleEndpoint, params, TokenExchange.ACCESS_TOKEN, token);
        if(r.getStatus()==200 && token.getState()==Token.ACCESS_TOKEN)
        {
            // access token
            __consumer.saveToken(token, request, response);
            return true;
        }
        return false;
    }
    
    void query(String url, Token token, HttpServletRequest request, 
            HttpServletResponse response) throws IOException
    {
        Response r = serviceGET(url, __consumer.getConsumerContext(), __googleEndpoint, 
                token, request, response);
        
        BufferedReader br = new BufferedReader(new InputStreamReader(r.getInputStream(), "UTF-8"));
        response.setContentType("text/xml");
        PrintWriter pw = response.getWriter();
        for(String line=null; (line=br.readLine())!=null;)
            pw.append(line);
    }
    
    public static Response serviceGET(String serviceUrl, ConsumerContext context, Endpoint ep, 
            Token token, HttpServletRequest request, HttpServletResponse response) 
            throws IOException
    {
        HttpConnector connector = context.getHttpConnector();
        UrlEncodedParameterMap params = new UrlEncodedParameterMap(serviceUrl);
        context.getNonceAndTimestamp().put(params, token.getCk());
        Parameter authorization = new Parameter("Authorization", 
                HttpAuthTransport.getAuthHeaderValue(params, ep, token, 
                context.getNonceAndTimestamp(),  ep.getSignature()));
        return connector.doGET(params.getUrl(), authorization);
        
    }

}
