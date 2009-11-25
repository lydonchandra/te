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
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.dyuproject.util.ClassLoaderUtil;
import com.dyuproject.util.Delim;

/**
 * GoogleModule
 * 
 * @author David Yu
 * @created Aug 18, 2009
 */

public final class GoogleModule
{
    
    static final String RESOURCE_LOCATION = "google_module.properties";
    static final Map<String,GoogleModule> __modules = new HashMap<String,GoogleModule>();
    
    static
    {
        URL resource = ClassLoaderUtil.getResource(RESOURCE_LOCATION, GoogleModule.class);
        if(resource==null)
            throw new IllegalStateException("resource: " + RESOURCE_LOCATION + " not found");

        Properties props = new Properties();
        try
        {
            props.load(resource.openStream());
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
        
        String[] modules = Delim.COMMA.split(props.getProperty("modules"));
        for(String m : modules)
        {
            m = m.trim();
            String url = props.getProperty(m + ".url");
            if(url==null)
                throw new IllegalStateException(m + " must have a url");
            
            String scope = props.getProperty(m + ".scope");
            if(scope==null)
                throw new IllegalStateException(m + " must have a scope");
            
            String name = props.getProperty(m + ".name");
            if(name==null)
                name = "google " + m;
            
            add(new GoogleModule(m, name, url, scope));
        }
    }
    
    static void add(GoogleModule module)
    {
        __modules.put(module.getId(), module);
    }
    
    public static GoogleModule get(String name)
    {
        return __modules.get(name);
    }
    
    public static Collection<GoogleModule> getAll()
    {
        return __modules.values();
    }
    
    private final String _id, _name, _url, _scope;
    
    public GoogleModule(String id, String name, String url, String scope)
    {
        _id = id;
        _name = name;
        _url = url;
        _scope = scope;
    }
    
    public String getId()
    {
        return _id;
    }
    
    public String getName()
    {
        return _name;
    }
    
    public String getUrl()
    {
        return _url;
    }
    
    public String getScope()
    {
        return _scope;
    }

}
