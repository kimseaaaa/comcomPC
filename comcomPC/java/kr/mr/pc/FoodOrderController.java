package kr.mr.pc;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.mr.model.ClientDTO;
import kr.mr.model.FoodOrderDTO;
import kr.mr.service.FoodOrderService;

@Controller
public class FoodOrderController {
	
	@Autowired
	private FoodOrderService foservice;
	
	@RequestMapping(value = "/ajaxcartadd.do", method = RequestMethod.POST, produces = "application/json; charset=utf8" )
	@ResponseBody
	public Map<Integer, FoodOrderDTO> ajaxcartadd(String orderfoodcode, HttpSession session) {
		
		ClientDTO cldto = (ClientDTO)session.getAttribute("cllogdto");
		int fcode = Integer.parseInt(orderfoodcode);
		foservice.cartadd(fcode, cldto);
		System.out.println(foservice.getfocart());
		return foservice.getfocart();
	
	}
	
	@RequestMapping(value = "/ajaxCartMinus.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<Integer, FoodOrderDTO> ajaxCartMinus(String orderfoodcode, HttpSession session) {
		ClientDTO cldto = (ClientDTO)session.getAttribute("cllogdto");
		int fcode = Integer.parseInt(orderfoodcode);
		foservice.cartminus(fcode, cldto);
	
		return foservice.getfocart();
	} 
	
	@RequestMapping(value = "/ajaxCartDelete.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<Integer, FoodOrderDTO> ajaxCartDelete(String orderfoodcode) {
		int fcode = Integer.parseInt(orderfoodcode);
		foservice.cartdelete(fcode);
	
		return foservice.getfocart();
	}
	
	@RequestMapping("/clientFoodPay.do")
	public String clientFoodPay(RedirectAttributes redirect) {
		String viewpage=null;
		String msg=null;
		if(foservice.getfocart()==null || foservice.getfocart().size()==0) {
			viewpage="redirect:/clientFoodMain.do";
			msg="??????????????? ????????? ????????????.";
			redirect.addAttribute("msg", msg);
		}else {
			viewpage = "client/food/clientFoodPay";
		}
		return viewpage;
	}
	
	@RequestMapping("/clientFoodPayOk.do")
	public String clientFoodPayOk (HttpSession session) {
		
		ClientDTO cldto = (ClientDTO)session.getAttribute("cllogdto");
		
		//db??????
		foservice.cartInsert(cldto);
		
		////?????? ?????????
		foservice.newcart();
		
		return "redirect:/clientFoodMain.do";
	}
	
	//////////////?????????//////////////////////
	
	//????????? ????????? ??????
	@RequestMapping(value = "/ajaxFoodOrderCnt.do")
	@ResponseBody
	public String ajaxFoodOrderCnt() {
		int fcode =foservice.folist().size();
		String cnt =String.valueOf(fcode);
		return cnt;
	} 
	
	//?????? ??????????????????
	@RequestMapping("/adminOrderOk.do")
	public String adminOrderOk(String fodcode,String l) {
		System.out.println("fodcode : "+fodcode);
		int okfodcode = Integer.parseInt(fodcode);
		foservice.orderOk(okfodcode);
		String viewpage = null;
		if(l==null) {
			viewpage="redirect:/adminDashboard.do";
			
		}else {
			viewpage= "redirect:/adminFoodOrder.do";
		}
		return viewpage;
	}
	
	@RequestMapping("/adminFoodOrder.do")
	public String adminFoodOrderlist(Model model) {
		
		///????????? ??????
		List<FoodOrderDTO> folist = foservice.folist();
		model.addAttribute("folist",folist);
		
		/////??????????????? ??????
		List<FoodOrderDTO> endfolist =  foservice.endfolist();
		model.addAttribute("endfolist",endfolist);
		
		return "admin/food/adminFoodOrde";
	}
	
	//?????? ???????????? ?????? ????????? ??????
	@RequestMapping("/adminOrderSearch.do")
	public String adminOrderSearch(String ordersearch, Model model) {
		List<FoodOrderDTO> searchlist =new ArrayList<FoodOrderDTO>();
		try {
			//??????????????? ??????
			int seatnum = Integer.parseInt(ordersearch);
			searchlist = foservice.seatnumSFOList(seatnum);
			
		} catch (NumberFormatException e) {
			//???????????? ??????
			searchlist = foservice.idSFOList(ordersearch);
		}
		
		model.addAttribute("endfolist",searchlist);
		
		return "admin/food/adminFoodOrde";
	}
	
	
	

}
