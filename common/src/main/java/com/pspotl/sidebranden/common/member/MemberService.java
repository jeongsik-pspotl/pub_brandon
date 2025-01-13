package com.pspotl.sidebranden.common.member;


import com.pspotl.sidebranden.common.enums.MessageString;
import com.pspotl.sidebranden.common.error.WHiveInvliadRequestException;
import com.pspotl.sidebranden.common.pricing.Pricing;
import com.pspotl.sidebranden.common.pricing.PricingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class MemberService {

    @Autowired
    private MemberDaoImpl memberDaoImpl;

    @Autowired
    private MemberLoginDaoImpl memberLoginDaoImpl;

    @Autowired
    private PricingService pricingService;

    public PasswordEncoder getPasswordEncode(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Transactional
    public Member findById(Long id) {
        return memberDaoImpl.findById(id);
    }

    @Transactional
    public Member findByEmail(String email) {
        return memberDaoImpl.findByEmail(email);
    }

    @Transactional(readOnly=true)
    public MemberLogin findByUserLoginID(String user_login_id){
        return memberLoginDaoImpl.findByUserLoginID(user_login_id);
    }

    @Transactional
    public MemberLogin findByEmailDetail(String email){
        return memberLoginDaoImpl.findByEmail(email);
    }

    @Transactional(readOnly=true)
    public MemberLogin findByLoginEmailAndPassword(MemberLogin memberLogin) {

        MemberLogin memberLogintemp = null;
        boolean isPasswordMatching = false;
        // 내부에서 암호화 체크 기능 추가..
        String rawPassword = memberLogin.getPassword();
        try {
            memberLogintemp = memberLoginDaoImpl.findByOnlyEmail(memberLogin.getUser_login_id());
        }catch (Exception e){
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_USER_ID_OR_PASSWORD.getMessage());
        }

//        try {
//           // memberLogintemp = memberLoginDaoImpl.findByEmail(memberLogin.getEmail());
//        }catch (Exception e){
//            e.printStackTrace();
//        }


        BCryptEncoder encoder = new BCryptEncoder();
        try{
            isPasswordMatching = encoder.matches(rawPassword, memberLogintemp.getPassword());
        }catch(Exception e){
            e.printStackTrace();
            log.error("password error ", e);
        }


        if(!isPasswordMatching){
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_USER_ID_OR_PASSWORD.getMessage());
        }

        return memberLogintemp;
    }

    @Transactional
    public boolean findByAuthCheckUserAndPassword(String user_login_id, String password){

        MemberLogin memberLoginCheck = null;
        boolean isPasswordMatching = false;

        String rawPassword = password;
        try {
            memberLoginCheck = memberLoginDaoImpl.findByUserLoginID(user_login_id);
        } catch (Exception e) {
            return false;
        }

        BCryptEncoder encoder = new BCryptEncoder();
        try {
            isPasswordMatching = encoder.matches(rawPassword, memberLoginCheck.getPassword());
        }catch (Exception e) {
            return false;
        }


        if(!isPasswordMatching){
            return false;
        }

        return true;
    }


    @Transactional
    public List<Member> findAll() {
        return memberDaoImpl.findAll();
    }

    @Transactional
    public void createHiveUser(MemberUserCreate memberUserCreate){
        // password bcrypt encode 변환
        memberUserCreate.setPassword(new BCryptPasswordEncoder().encode(memberUserCreate.getPassword()));
        memberUserCreate.setCreated_date(LocalDateTime.now());
        memberUserCreate.setUpdated_date(LocalDateTime.now());
        memberUserCreate.setBuild_yn("N"); // build yn flag 추가
        memberLoginDaoImpl.insert(memberUserCreate);
    }

    @Transactional
    public void updateHiveUser(MemberUserCreate memberUserCreate){
         memberLoginDaoImpl.update(memberUserCreate);
    }

    @Transactional
    public String updateUserPasswordReset(MemberUserCreate memberUserCreate){

        String uuid = UUID.randomUUID().toString().replaceAll("-", ""); // -를 제거해 주었다.
        uuid = uuid.substring(0, 10); //uuid를 앞에서부터 10자리 잘라줌.
        log.info("uuid " +uuid);
        memberUserCreate.setPassword(new BCryptPasswordEncoder().encode(uuid));
        memberUserCreate.setUpdated_date(LocalDateTime.now());

        memberLoginDaoImpl.updatePasswordReset(memberUserCreate);


        return uuid;
    }

    @Transactional
    public void updateUserHiveEventCheckYn(Long user_id, String eventYn, String comment){
        memberLoginDaoImpl.updateUserHiveEventCheckYn(user_id, eventYn, comment);
    }

    @Transactional
    public List<MemberLogin> findByAll(){
        return memberLoginDaoImpl.findByAll();
    }

    @Transactional
    public MemberLogin findByEmailOne(String email) { return memberLoginDaoImpl.findByEmail(email); }

    @Transactional
    public MemberDetail findByIdDetail(Long id){
        return memberLoginDaoImpl.findById(id);
    }

    @Transactional
    public List<MemberAdminList> findByAdminList(Long domainID){ return memberLoginDaoImpl.findByAdminList(domainID); }

    @Transactional
    public MemberDetail findByDetailAndPricing(Long id){

        MemberDetail memberDetail = memberLoginDaoImpl.findById(id);

        Pricing pricing = pricingService.findById(memberDetail.getUser_id());



        memberDetail.setPricing(pricing);

        return memberDetail;

    }

    @Transactional
    public MemberDetail findByEmailDetailAndPricing(String email){

        MemberDetail memberDetail = memberLoginDaoImpl.findByEmailBeforeCancel(email);

        Pricing pricing = pricingService.findById(memberDetail.getUser_id());



        memberDetail.setPricing(pricing);

        return memberDetail;

    }

    @Transactional
    public void insert(Member member) {
        member.setCreatedDateNow();
        member.setPassword(getPasswordEncode().encode(member.getPassword()));
        memberDaoImpl.insert(member);
    }

    @Transactional
    public void updateMember(Long id, Member member) {
        memberDaoImpl.updateMember(id, member);
    }

    @Transactional
    public void updateMemberBuildYn(String email, String build_yn) { memberLoginDaoImpl.updateBuildYn(email, build_yn); }

    @Transactional
    public void updateMemberSecssionYn(String email){

        memberLoginDaoImpl.updateSecssionYn(email,"NOUSER");
    }

    @Transactional
    public void delete(Long id) {
        memberDaoImpl.deleteById(id);
    }

    @Transactional
    public void updateLogout(Long id) {
        MemberLogin memberLogin = new MemberLogin();
        memberLogin.setUser_id(id);
        memberLogin.setLast_login_date(LocalDateTime.now());
        memberDaoImpl.updateByLogoutAndID(memberLogin);
    }

    @Transactional
    public void updateConfirmPassword(Map<String, Object> payload){

        // old password 체크 기능 추가
        MemberLogin memberLogintemp = null;
        MemberUserCreate memberUserCreate = new MemberUserCreate();
        boolean isPasswordMatching = false;
        // 내부에서 암호화 체크 기능 추가..
        String rawPassword = payload.get("old_password").toString();
        try {
            memberLogintemp = memberLoginDaoImpl.findByUserLoginID(payload.get("user_login_id").toString());
        }catch (Exception e){
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_USER_OLD_PASSWORD.getMessage());
        }

//        try {
//           // memberLogintemp = memberLoginDaoImpl.findByEmail(memberLogin.getEmail());
//        }catch (Exception e){
//            e.printStackTrace();
//        }


        BCryptEncoder encoder = new BCryptEncoder();
        try{
            isPasswordMatching = encoder.matches(rawPassword, memberLogintemp.getPassword());
        }catch(Exception e){
            e.printStackTrace();
            log.error("password error ", e);
        }


        if(!isPasswordMatching){
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_USER_OLD_PASSWORD.getMessage());
        }else {

            // new password update 처리 추가
            memberUserCreate.setUser_id(Long.valueOf(payload.get("user_id").toString()));
            // memberUserCreate.setEmail(payload.get("email").toString());
            memberUserCreate.setPassword(new BCryptPasswordEncoder().encode(payload.get("new_password").toString()));

            memberLoginDaoImpl.updatePasswordUserIDReset(memberUserCreate);

        }

    }

    @Transactional
    public MemberLogin userPhoneNumberCheck(String phoneNumber){
        return memberLoginDaoImpl.findByUserPhoneNumberOne(phoneNumber);
    }

    @Transactional
    public int payCheck(Long user_id) {
        return memberLoginDaoImpl.payCheck(user_id);
    }

    @Transactional
    public void userDetailAppIDUpdate(Long user_id, String appid_json){
        memberLoginDaoImpl.updateUserAppIDJSON(user_id, appid_json);
    }

}
