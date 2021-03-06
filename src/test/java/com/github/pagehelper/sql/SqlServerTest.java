/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.github.pagehelper.sql;

import com.github.pagehelper.dialect.ReplaceSql;
import com.github.pagehelper.dialect.replace.RegexWithNolockReplaceSql;
import com.github.pagehelper.parser.SqlServerParser;
import net.sf.jsqlparser.JSQLParserException;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author liuzh
 */
public class SqlServerTest {
    public static final SqlServerParser sqlServer = new SqlServerParser();

    @Test
    @Ignore("暂时不支持")
    public void testSqlTestWithlock() throws JSQLParserException {
        String originalSql = "select * from Agency with (NOLOCK) where status=0 order by CreateTime";
        System.out.println(sqlServer.convertToPageSql(originalSql, 1, 10));
    }

    @Test
    public void testSqlTest() throws JSQLParserException {
        String originalSql = "Select * from country o where id > 10 order by id desc , countryname asc";
        System.out.println(sqlServer.convertToPageSql(originalSql, 1, 10));
    }

    @Test
    public void testSqlAlias() throws JSQLParserException {
        String originalSql = "Select o.* from country o where id > 10 order by id desc , countryname asc";
        System.out.println(sqlServer.convertToPageSql(originalSql, 1, 10));
    }

    @Test
    public void testSqlOrderByAlias() throws JSQLParserException {
        String originalSql = "select countrycode code from country order by code";
        System.out.println(sqlServer.convertToPageSql(originalSql, 1, 10));
    }

    @Test
    public void testSqlDistinct() throws JSQLParserException {
        String originalSql = "select distinct countrycode,countryname from country order by countrycode";
        System.out.println(sqlServer.convertToPageSql(originalSql, 1, 10));
    }

    @Test
    public void testSqlTableAll() throws JSQLParserException {
        String originalSql = "Select country.* from country where id > 10 order by id desc , countryname asc";
        System.out.println(sqlServer.convertToPageSql(originalSql, 1, 10));
    }

    @Test
    public void testSqlAs() throws JSQLParserException {
        //TODO 使用AS的时候，不要带单引号
        String originalSql = "Select id as id,countryname name,countrycode as code from country o where id > 10 order by id desc , countryname asc";
        System.out.println(sqlServer.convertToPageSql(originalSql, 1, 10));
    }

    @Test
    public void testSelectParameter() throws JSQLParserException {
        //TODO 这种情况会增加?的个数，需要实际在Mybatis中测试
        String originalSql = "Select id as id,? name,? from country o where id > 10 order by id desc , countryname asc";
        System.out.println(sqlServer.convertToPageSql(originalSql, 1, 10));
    }

    @Test
    public void testSqlWith() throws JSQLParserException {
        String originalSql = "with cr as " +
                " (select id " +
                "    from country " +
                "   where id > 100 " +
                "     and id < 120) " +
                "select id, countryname, countrycode " +
                "  from country " +
                " where id in (select * from cr) order by id";
        System.out.println(sqlServer.convertToPageSql(originalSql, 1, 10));
    }

    @Test
    public void testSqlLeftJoin() throws JSQLParserException {
        String originalSql = "select * " +
                "  from (select distinct A.USERID, " +
                "                        A.USERCODE, " +
                "                        A.USERNAME, " +
                "                        A.USERPWD, " +
                "                        A.CREATEDATE, " +
                "                        A.UPDATEDATE, " +
                "                        A.USERSTATE, " +
                "                        A.MEMO, " +
                "                        A.USERPHONE, " +
                "                        A.USEREMAIL, " +
                "                        A.IDCARD, " +
                "                        D.DEPTNAME, " +
                "                        D.DEPTID " +
                "          from BASE_SYS_USER A " +
                "          left JOIN BASE_SYS_ROLE_USER_REL B " +
                "            ON A.USERID = B.USERID " +
                "          left JOIN BASE_SYS_ROLE C " +
                "            on C.ROLEID = B.ROLEID " +
                "          left join BASE_SYS_DEPT_USER_REL REL " +
                "            on REL.USERID = A.USERID " +
                "          left join BASE_SYS_DEPT D " +
                "            on D.DEPTID = REL.DEPTID " +
                "         where 1 = 1 " +
                "           and C.ROLEID = ? " +
                "           and D.DEPTID = ? " +
                "           and A.USERNAME LIKE '%heh%' " +
                "           and A.USERSTATE = ? " +
                "         ) A Order by A.createDate desc";
        System.out.println(sqlServer.convertToPageSql(originalSql, 1, 10));
    }

    @Test
    public void testSqlUnion() throws JSQLParserException {
        String originalSql = "select countryname,countrycode code from country where id >170 " +
                "union all " +
                "select countryname,countrycode code from country where id < 10 order by code";
        System.out.println(sqlServer.convertToPageSql(originalSql, 1, 10));
    }

    @Test
    public void testSqlUnion2() throws JSQLParserException {
        String originalSql = "select countryname,code from ( " +
                "\tselect countryname,countrycode code from country where id >170 " +
                "\tunion all " +
                "\tselect countryname,countrycode code from country where id < 10 " +
                ") as temp " +
                "order by code";
        System.out.println(sqlServer.convertToPageSql(originalSql, 1, 10));
    }

    @Test
    public void testSqlOrderByFunctionAlias() throws JSQLParserException {
        String originalSql = "select countrycode code, func() func_alias from country order by func()";
        System.out.println(sqlServer.convertToPageSql(originalSql, 1, 10));
    }

    @Test
    public void testSqlOrderByUnknown() throws JSQLParserException {
        String originalSql = "select countryname from country order by countrycode";
        System.out.println(sqlServer.convertToPageSql(originalSql, 1, 10));
    }

    @Test
    public void testSqlOrderByTable() throws JSQLParserException {
        String originalSql = "select t.countrycode, t.countryname from country t order by t.countrycode";
        System.out.println(sqlServer.convertToPageSql(originalSql, 1, 10));
    }

    @Test
    public void testSqlStar() throws JSQLParserException {
        String originalSql = "select t.*, 1 alias from country t order by t.countrycode";
        System.out.println(sqlServer.convertToPageSql(originalSql, 1, 10));
    }

    @Test
    public void testSql377() throws JSQLParserException {
        String originalSql = "select distinct u.user_id, u.dept_id, u.login_name, u.user_name, u.email, u.phonenumber, u.status, u.create_time from sys_user u left join sys_dept d on u.dept_id = d.dept_id left join sys_user_role ur on u.user_id = ur.user_id left join sys_role r on r.role_id = ur.role_id where u.del_flag = '0' and (r.role_id != 1 or r.role_id IS NULL) and u.user_id not in (select u.user_id from sys_user u inner join sys_user_role ur on u.user_id = ur.user_id and ur.role_id = 1)";
        System.out.println(sqlServer.convertToPageSql(originalSql, 1, 10));
    }

    @Test
    public void testSql386() throws JSQLParserException {
        String originalSql = " select a.Guid,\n" +
            "               ProManager,\n" +
            "               WorkOrderType,\n" +
            "               a.Name,\n" +
            "               WorkNote,\n" +
            "               b.Name                             TeamName,\n" +
            "               ConstructionSite,\n" +
            "               iif(a.MaterialGuid is null, 0, 1) as IsMaterial,\n" +
            "               a.MaterialGuid,\n" +
            "               c.Code                          as MaterialCode,\n" +
            "               c.FullName                         MaterialName,\n" +
            "               d.FullName                         MatStdSortName\n" +
            "        from RMC_WorkOrder a\n" +
            "                 left join dbo.SYS_OrgFrame b on a.TeamGuid = b.Code and b.ParentGuid is null\n" +
            "                 left join dbo.BAS_Material c on a.MaterialGuid = c.Guid\n" +
            "                 left join BAS_MatStdSort d on a.MatStdSortGuid = d.Guid\n" +
            "        where a.ConfirmUser is null\n" +
            "          and b.Guid = 1\n" +
            "        order by a.ContractBillNO desc";
        System.out.println(sqlServer.convertToPageSql(originalSql, 1, 10));
    }

    @Test
    public void testSql345() throws JSQLParserException {
        String originalSql = "Select CC.ClinicID, CC.CaseHistoryNum, CC.CaseHistoryID, CC.DoctorID, CC.ClientRegisterID\n" +
            "From Client CC With(Nolock)\n" +
            "Left Outer Join Register CR With(Nolock) On CC.ClientRegisterID = CR.ClientRegisterID\n" +
            "Where CC.ClientID = 14374";
      ReplaceSql replaceSql = new RegexWithNolockReplaceSql();
      String replace = replaceSql.replace(originalSql);
      String pageSql = sqlServer.convertToPageSql(replace, 1, 10);
      String result = replaceSql.restore(pageSql);
      System.out.println(result);
    }
}
