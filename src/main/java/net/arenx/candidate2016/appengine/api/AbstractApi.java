package net.arenx.candidate2016.appengine.api;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiNamespace;

@Api(name = "candidate2016Api",
version = "v1",
namespace = @ApiNamespace(ownerDomain = "appengine.candidate2016.arenx.net",
                           ownerName = "arenx.net",
                           packagePath=""))
public class AbstractApi {

}
