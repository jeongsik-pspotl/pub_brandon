package com.inswave.whive.common.member;

import java.util.List;

public interface MemberDao {

    void insert(Member user);

    void updateMember(long id, Member member);

    Member findById(Long id);

    void deleteById(Long id);

    List<Member> findAll();

    Member findByEmail(String email);



    void updateByLogoutAndID(MemberLogin memberLogin);

}
