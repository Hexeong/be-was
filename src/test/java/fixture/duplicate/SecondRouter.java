package fixture.duplicate;

import annotation.RequestMapping;
import annotation.Router;

@Router
public class SecondRouter {
    @RequestMapping(path = "/duplicate")
    public void handle() { }
}
