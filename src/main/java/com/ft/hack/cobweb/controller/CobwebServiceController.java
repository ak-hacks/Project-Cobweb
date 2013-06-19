package com.ft.hack.cobweb.controller;

import com.ft.hack.cobweb.dao.CobwebDAO;
import com.ft.hack.cobweb.service.DBPopulator;
import com.ft.hack.cobweb.service.RelationsQueryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/")
public class CobwebServiceController {

	@RequestMapping(method = RequestMethod.GET, value = "/relations/{name}")
	public ModelAndView getRelations(@PathVariable String name) {
		RelationsQueryService queryService = new RelationsQueryService();
		List<List> dataNodes = queryService.getRelations(name);
		return new ModelAndView("relations", "results", dataNodes);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/populatedb")
	public ModelAndView populatedb() {
		DBPopulator dbPopulator = new DBPopulator();

		String[] people = { "Larry Page", "Sergey Brin", "Glen Moreno", "Marjorie Scardino", "Robin Freestone",
				"William Ethridge", "Rona Fairhead", "Eric Schmidt", "Nikesh Arora", "Timothy Cook", "Philip Schiller",
				"Arthur Levinson", "Peter Oppenheimer", "Andrea Jung"};

		for (String string : people) {
			List<String[]> records = dbPopulator.searchCorporateAPI(string);
			CobwebDAO dao = new CobwebDAO();
			dao.insertRecords(records);
		}

        /*
        List<String[]> records = dbPopulator.constructMockData();
        CobwebDAO dao = new CobwebDAO();
        dao.insertRecords(records);
        */
		return new ModelAndView("populatedb", "populatedb", null);
	}
}