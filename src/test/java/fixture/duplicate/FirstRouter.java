package fixture.duplicate;

import annotation.RequestMapping;
import annotation.Router;

@Router
public class FirstRouter {
    @RequestMapping(path = "/duplicate")
    public void handle() { }
}
