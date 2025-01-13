package com.pspotl.sidebranden.manager.controller;

import com.pspotl.sidebranden.common.error.WHiveInvliadRequestException;
import com.pspotl.sidebranden.common.member.MemberLogin;
import com.pspotl.sidebranden.common.member.MemberService;
import com.pspotl.sidebranden.common.menu.Menu;
import com.pspotl.sidebranden.common.menu.MenuService;
import com.pspotl.sidebranden.common.pricing.Pricing;
import com.pspotl.sidebranden.common.pricing.PricingService;
import com.pspotl.sidebranden.manager.util.ResponseUtility;
import com.pspotl.sidebranden.manager.util.common.Common;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * W-Hive 매니저에 표시되는 메뉴리스트 관련 컨트롤러
 *
 * @soorink
 */
@RestController
@RequiredArgsConstructor
public class MenuController {

    private final Common common;
    private final MemberService memberService;
    private final PricingService pricingService;

    private final MenuService menuService;
    private final ResponseUtility responseUtility;

    @Value("${spring.profiles}")
    private String profiles;

    // 새로운 메뉴를 만든다
    // 추후, 관리 페이지에서 사용할 예정
    @RequestMapping(value = "/manager/menu/create", method = RequestMethod.POST, produces = {"application/json"})
    public ResponseEntity<Object> createMenu(HttpServletRequest request, @RequestBody Menu menu) {
        if(menu.getMenu_code() == null || menu.getMenu_code().equals("")) {
            throw new WHiveInvliadRequestException("insert error");
        }
        menuService.insert(menu);

        return responseUtility.makeSuccessResponse();
    }

    //사용자 권한과 Profiles(Service, Onpremise)에 맞는 메뉴를 가져온다.
    @RequestMapping(value = "/manager/menu/list", method = RequestMethod.POST)
    public List<Menu> listMenu(HttpServletRequest request, @RequestBody Menu menu) {


        if(!StringUtils.hasText(menu.getMenu_role_type()) || !StringUtils.hasText(menu.getMenu_profile_type())) {
            throw new WHiveInvliadRequestException("list error");
        }

        if(profiles.equals("onpremiss")){
            return menuService.findByRoleAndProfile(menu);
        }else {
            String userId = common.getTokenToRealName(request);
            MemberLogin memberLogin = memberService.findByUserLoginID(userId);

            Pricing pricing = pricingService.findById(memberLogin.getUser_id());

            if(pricing != null ){

                if(memberLogin.getUser_role().equals("SUPERADMIN")){
                    menu.setMenu_pay_type(900);
                }

                if(pricing.getPay_change_yn().equals("Y")){
                    menu.setMenu_pay_type(300);
                }else if(pricing.getPay_change_yn().equals("N")){
                    menu.setMenu_pay_type(100);
                }

            }else {
                if(memberLogin.getUser_role().equals("SUPERADMIN")){
                    menu.setMenu_pay_type(900);
                }else {
                    menu.setMenu_pay_type(100);
                }

            }

            return menuService.findByPayAndProfile(menu);
        }

    }
}
