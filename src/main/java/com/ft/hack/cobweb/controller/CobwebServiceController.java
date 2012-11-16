package com.ft.hack.cobweb.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.ft.hack.cobweb.service.RelationsQueryService;

@Controller
@RequestMapping("/")
public class CobwebServiceController {
	
	@RequestMapping(method = RequestMethod.GET, value = "/relations/{name}")
	public ModelAndView getRelations(@PathVariable String name) {
		RelationsQueryService queryService = new RelationsQueryService();
		List<List> dataNodes = queryService.getRelations(name);
		return new ModelAndView("relations", "results", dataNodes);
	}
}