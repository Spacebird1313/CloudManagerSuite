package be.uantwerpen.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Thomas on 13/02/2016.
 */
@Controller
public class HomeController
{
    @RequestMapping({"/", "/home"})
    public String showHomepage(ModelMap model)
    {
        return "homepage";
    }
}
