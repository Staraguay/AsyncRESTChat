package com.chat.services;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/")
public class ChatServiceDeployApplication extends Application
{
   private Set<Object> singletons = new HashSet<Object>();

   public ChatServiceDeployApplication()
   {
     // singletons.add(new CdiDbProxyBean());
      singletons.add(new ChatServiceResource());
   }

   @Override
   public Set<Object> getSingletons()
   {
      return singletons;
   }
}
