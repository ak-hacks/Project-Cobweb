package com.ft.hack.cobweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class CobwebServiceController {

	@RequestMapping(method = RequestMethod.GET, value = "/relations/{name}")
	public ModelAndView getRelations(@PathVariable String name) {
		String relations = "Relations" + name;
		return new ModelAndView("relations", "relations", relations);
	}
}
