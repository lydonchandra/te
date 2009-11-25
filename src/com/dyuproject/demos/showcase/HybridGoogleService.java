//========================================================================
//Copyright 2007-2008 David Yu dyuproject@gmail.com
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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jra.Get;
import org.codehaus.jra.HttpResource;

import com.dyuproject.oauth.Consumer;
import com.dyuproject.oauth.Endpoint;
import com.dyuproject.oauth.HttpAuthTransport;
import com.dyuproject.oauth.NonceAndTimestamp;
import com.dyuproject.oauth.Signature;
import com.dyuproject.oauth.SimpleNonceAndTimestamp;
import com.dyuproject.oauth.Token;
import com.dyuproject.oauth.TokenExchange;
import com.dyuproject.oauth.Transport;
import com.dyuproject.openid.OpenIdUser;
import com.dyuproject.openid.RelyingParty;
import com.dyuproject.openid.YadisDiscovery;
import com.dyuproject.util.http.HttpConnector;
import com.dyuproject.util.http.SimpleHttpConnector;
import com.dyuproject.util.http.UrlEncodedParameterMap;
import com.dyuproject.util.http.HttpConnector.Parameter;
import com.dyuproject.util.http.HttpConnector.Response;
import com.dyuproject.web.rest.RequestContext;
import com.dyuproject.web.rest.service.AbstractService;

/**
 * @author David Yu
 * @created Aug 10, 2009
 */

public final class HybridGoogleService extends AbstractService
{
    
    static final String GOOGLE_IDENTIFIER = "https://www.google.com/accounts/o8/id";
    static final String GOOGLE_OPENID_SERVER = "https://www.google.com/accounts/o8/ud";
    static final Endpoint __google = Consumer.getInstance().getEndpoint("www.google.com");
    
    static
    {
        RelyingParty.getInstance().addListener(new RelyingParty.Listener()
        {
            public void onDiscovery(OpenIdUser user, HttpServletRequest request)
            {
            }
            public void onPreAuthenticate(OpenIdUser user, HttpServletRequest request, UrlEncodedParameterMap params)
            {
                String scope = (String)user.getAttribute("google_scope");
                if(scope!=null)
                {
                    params.add("openid.ns.oauth", "http://specs.openid.net/extensions/oauth/1.0");
                    params.put("openid.oauth.consumer", __google.getConsumerKey());
                    params.put("openid.oauth.scope", scope);
                }
            }
            public void onAuthenticate(OpenIdUser user, HttpServletRequest request)
            {
                if(user.getAttribute("google_scope")!=null)
                {
                    String alias = user.getExtension("http://specs.openid.net/extensions/oauth/1.0");
                    if(alias!=null)
                    {
                        String requestToken = request.getParameter("openid." + alias + ".request_token");
                        Token token = new Token(__google.getConsumerKey(), requestToken, null, Token.AUTHORIZED);
                        UrlEncodedParameterMap accessTokenParams = new UrlEncodedParameterMap();
                        try
                        {
                            Response accessTokenResponse = fetchToken(TokenExchange.ACCESS_TOKEN, 
                                    accessTokenParams, __google, token);
                            if(accessTokenResponse.getStatus()==200 && token.getState()==Token.ACCESS_TOKEN)
                            {
                                user.setAttribute("token_k", token.getKey());
                                user.setAttribute("token_s", token.getSecret());
                            }
                        }
                        catch(IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
            public void onAccess(OpenIdUser user, HttpServletRequest request)
            {
                
            }
        });
    }
    
    static void invalidate(HttpServletRequest request, HttpServletResponse response) 
    throws IOException
    {
        RelyingParty.getInstance().getOpenIdUserManager().invalidate(request, response);
    }
    
    public static Response fetchToken(TokenExchange exchange, UrlEncodedParameterMap params,
            Endpoint endpoint, Token token) 
    throws IOException
    {
        // via GET, POST or Authorization
        Transport transport = endpoint.getTransport();
        
        // via HMAC-SHA1 or PLAINTEXT
        Signature sig = endpoint.getSignature();
        
        // nonce and timestamp generator
        NonceAndTimestamp nts = SimpleNonceAndTimestamp.getDefault();
        
        // http connector
        HttpConnector connector = SimpleHttpConnector.getDefault();
        
        // returns the http response
        return transport.send(params, endpoint, token, exchange, nts, sig, connector);
    }
    
    public static Response doGET(UrlEncodedParameterMap params, Endpoint endpoint, Token token) 
    throws IOException
    {
        // via HMAC-SHA1 or PLAINTEXT
        Signature sig = endpoint.getSignature();
        
        // nonce and timestamp generator
        NonceAndTimestamp nts = SimpleNonceAndTimestamp.getDefault();
        
        // http connector
        HttpConnector connector = SimpleHttpConnector.getDefault();
        
        // Authorization Header with the access_token
        Parameter authorizationHeader = new Parameter("Authorization", 
                HttpAuthTransport.getAuthHeaderValue(params, endpoint, token, nts,  sig));
        
        return connector.doGET(params.toStringRFC3986(), authorizationHeader);
    }
    
    private OpenIdInterceptor _interceptor;
    
    @Override
    protected void init()
    {
        _interceptor = (OpenIdInterceptor)getWebContext().getAttribute("openIdInterceptor");
    }
    
    @HttpResource(location="/hybrid/google/$")
    @Get
    public void service(RequestContext rc) throws IOException, ServletException
    {
        String type = rc.getPathElement(2);
        GoogleModule module = GoogleModule.get(type);
        if(module==null)
        {
            rc.getResponse().sendRedirect("/hybrid/google");
            return;
        }
        
        OpenIdUser user = RelyingParty.getInstance().getOpenIdUserManager().getUser(rc.getRequest());
        if(user==null || !type.equals(user.getAttribute("google_type")))
        {
            // we expect it to be google so skip discovery to speed up the openid process
            user = OpenIdUser.populate(GOOGLE_IDENTIFIER, YadisDiscovery.IDENTIFIER_SELECT, 
                    GOOGLE_OPENID_SERVER);
            user.setAttribute("google_scope", module.getScope());
            user.setAttribute("google_type", type);
        }
        
        rc.getRequest().setAttribute(OpenIdUser.ATTR_NAME, user);
        
        if(!_interceptor.preHandle(rc))
            return;
        
        System.err.println("info: " + user.getAttribute("info"));
        
        
        String key = (String)user.getAttribute("token_k");
        String secret = (String)user.getAttribute("token_s");
        Token token = new Token(__google.getConsumerKey(), key, secret, Token.ACCESS_TOKEN);
        UrlEncodedParameterMap serviceParams = new UrlEncodedParameterMap(module.getUrl());
        
        Response serviceResponse = doGET(serviceParams, __google, token);
        BufferedReader br = new BufferedReader(new InputStreamReader(serviceResponse.getInputStream(), "UTF-8"));
        rc.getResponse().setContentType("text/plain");
        PrintWriter pw = rc.getResponse().getWriter();
//        pw.append( rc.getResponse().get getParameter("openid.ext1.value.email") + " email: " + user.getClaimedId() + "\n identifier: " + user.getIdentifier() + "\n identity" + user.getIdentity());
        
        String responsestring = "";
        for(String line=null; (line=br.readLine())!=null;)
        	responsestring += line;
        
        String[] splitstring = responsestring.split("title");
        responsestring += splitstring[0];
        responsestring += splitstring[1];
        
//        pw.append(splitstring[1]);
        String[] emailsplit = splitstring[1].split(" |>|<");
        pw.append("Email: " + emailsplit[5]);
        pw.append("\n user claimid: " + user.getClaimedId());
        rc.getRequest().getSession().setAttribute("userid", user.getClaimedId());
        rc.getResponse().sendRedirect("/stock");
    }    

}
