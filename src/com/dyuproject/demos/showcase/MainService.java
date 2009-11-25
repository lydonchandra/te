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

import java.io.IOException;

import javax.servlet.ServletException;

import org.codehaus.jra.Get;
import org.codehaus.jra.HttpResource;

import com.dyuproject.web.rest.RequestContext;
import com.dyuproject.web.rest.service.AbstractService;

/**
 * @author David Yu
 * @created Jun 17, 2009
 */

public class MainService extends AbstractService
{

    @Override
    protected void init()
    {
        
    }
    
    @HttpResource(location="/")
    @Get
    public void root(RequestContext rc) throws IOException, ServletException
    {
        rc.getResponse().setContentType("text/html");
        getWebContext().getJSPDispatcher().dispatch("index", rc.getRequest(), rc.getResponse());
    }
    
    @HttpResource(location="/oauth")
    @Get
    public void oauth(RequestContext rc) throws ServletException, IOException
    {
        rc.getResponse().setContentType("text/html");        
        //getWebContext().getJSPDispatcher().dispatch("oauth/index", rc.getRequest(), rc.getResponse());
        rc.getRequest().setAttribute("modules", GoogleModule.getAll());
        getWebContext().getJSPDispatcher().dispatch("oauth/google/index", rc.getRequest(), rc.getResponse());
    }
    
    @HttpResource(location="/oauth/google")
    @Get
    public void oauth_google(RequestContext rc) throws ServletException, IOException
    {
        rc.getResponse().setContentType("text/html");
        rc.getRequest().setAttribute("modules", GoogleModule.getAll());
        getWebContext().getJSPDispatcher().dispatch("oauth/google/index", rc.getRequest(), rc.getResponse());
    }
    
    @HttpResource(location="/hybrid")
    @Get
    public void hybrid(RequestContext rc) throws ServletException, IOException
    {
        rc.getResponse().setContentType("text/html");
        //getWebContext().getJSPDispatcher().dispatch("hybrid/index", rc.getRequest(), rc.getResponse());
        rc.getRequest().setAttribute("modules", GoogleModule.getAll());
        getWebContext().getJSPDispatcher().dispatch("hybrid/google/index", rc.getRequest(), rc.getResponse());        
    }
    
    @HttpResource(location="/hybrid/google")
    @Get
    public void hybrid_google(RequestContext rc) throws ServletException, IOException
    {
        rc.getResponse().setContentType("text/html");
        rc.getRequest().setAttribute("modules", GoogleModule.getAll());
        getWebContext().getJSPDispatcher().dispatch("hybrid/google/index", rc.getRequest(), rc.getResponse());
    }

}
