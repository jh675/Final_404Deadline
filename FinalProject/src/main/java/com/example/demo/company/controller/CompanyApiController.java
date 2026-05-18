package com.example.demo.company.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.company.service.CompanyService;
import com.example.demo.company.service.CompanyVO;
import com.github.pagehelper.PageInfo;

@RestController
@RequestMapping("/api/company")
public class CompanyApiController {

    private final CompanyService companyService;

    public CompanyApiController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/list")
    public Map<String, Object> list(
            @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
            CompanyVO company
    ) {

        PageInfo<CompanyVO> pageInfo = companyService.selectAll(company, pageNum);

        Map<String, Object> result = new HashMap<>();
        result.put("list", pageInfo.getList());
        result.put("total", pageInfo.getTotal());

        return result;
    }
}