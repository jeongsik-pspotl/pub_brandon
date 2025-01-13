package com.pspotl.sidebranden.common.member;

import java.util.List;

public interface MemberLoginDao {

    void insert(MemberUserCreate memberUserCreate);

    MemberLogin findByLoginEmailAndPasswrod(MemberLogin memberLogin);

    List<MemberLogin> findByAll();

    MemberDetail findById(Long id);

    MemberDetail findByEmailBeforeCancel(String email);

    MemberLogin findByEmail(String email);

    MemberLogin findByOnlyEmail(String email);

    MemberLogin findByUserLoginID(String user_login_id);

    List<MemberAdminList> findByAdminList(Long doaminID);

    MemberLogin findByUserPhoneNumberOne(String phoneNumber);

    void update(MemberUserCreate memberUserCreate);

    void updateBuildYn(String email, String build_yn);

    void updatePasswordReset(MemberUserCreate memberUserCreate);

    void updatePasswordUserIDReset(MemberUserCreate memberUserCreate);

    void updateSecssionYn(String email, String secssion_yn);

    void updateUserHiveEventCheckYn(Long user_id, String eventYn, String comment);

    int payCheck(Long user_id);
    void updateUserAppIDJSON(Long user_id,String appIDJson);

}
