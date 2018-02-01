package com.jd.fishbaby.controler;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
* Created with Eclipse.
* @author yuminghui3
* @version 创建时间：2018年2月1日 下午5:13:04
* $
*/
@Controller
@RequestMapping("/backstage")
public class BackstageControler extends BaseControler {
	
	@RequestMapping("/index")
	public String index(Model model) {
		
		return "/backstage/index";
	}
}
