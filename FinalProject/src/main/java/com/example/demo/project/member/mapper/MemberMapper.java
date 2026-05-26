package com.example.demo.project.member.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.example.demo.project.member.service.*;

/** 구성원 MyBatis — USERS + MEMBER + GRP 조인 */
@Mapper
public interface MemberMapper {

    /** 프로젝트 구성원 목록 */
    List<ProjectMemberRowVO> selectProjectMemberList(MemberListCriteria criteria);

    String selectProjectNameByPrjId(@Param("prjId") Long prjId);

    /** 프로젝트 투입기간 제한용 — PROJECT.START_DATE / CLOSED_DATE */
    ProjectPeriodVO selectProjectPeriodByPrjId(@Param("prjId") Long prjId);

    /** 프로젝트 수행 기업(biz_no) 소속 사용자 목록 */
    List<CompanyMemberRowVO> selectCompanyMembersByPrjId(
            @Param("prjId") Long prjId,
            @Param("excludeRegistered") boolean excludeRegistered);

    /** 구성원 등록 모달 — 프로젝트 내 활성 그룹 */
    List<MemberGroupPickRowVO> selectProjectGroupsByPrjId(@Param("prjId") Long prjId);

    MemberDetailVO selectMemberDetail(
            @Param("prjId") Long prjId,
            @Param("userId") Long userId,
            @Param("grpId") Long grpId);

    List<MemberIssueRowVO> selectMemberIssues(
            @Param("prjId") Long prjId,
            @Param("userId") Long userId,
            @Param("grpId") Long grpId);

    /** (userId, grpId) 쌍에 해당하는 MEMBER 삭제 — prjId로 범위 제한 */
    int deleteMemberRows(
            @Param("prjId") Long prjId,
            @Param("rows") List<ProjectMemberRowVO> rows);

    int countGrpInProject(@Param("prjId") Long prjId, @Param("grpId") Long grpId);

    int countActiveMember(@Param("userId") Long userId, @Param("grpId") Long grpId);

    String selectUserHireDateYmd(@Param("userId") Long userId);

    int insertMember(MemberRegisterParam param);

    /** INSERT ALL + foreach 기반 일괄 등록 — 동일 그룹·기간으로 N건 INSERT */
    int insertMembers(
            @Param("userIds") List<Long> userIds,
            @Param("grpId") Long grpId,
            @Param("prjStartDate") String prjStartDate,
            @Param("prjEndDate") String prjEndDate);

    int updateMember(MemberUpdateParam param);
}
