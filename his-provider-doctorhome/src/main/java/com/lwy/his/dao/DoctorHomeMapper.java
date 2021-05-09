package com.lwy.his.dao;


import com.lwy.his.entity.assets.*;
import com.lwy.his.entity.doctor.DocotrInspectionrecord;
import com.lwy.his.entity.doctor.DoctorDrugrecord;
import com.lwy.his.entity.doctor.DoctorNodrugrecord;
import com.lwy.his.entity.doctor.DoctorTestrecode;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public interface DoctorHomeMapper {

    /**
     * 根据医生姓名 日期 读取就诊还是没就诊
     *  map.put("today",today);   日期
     *         map.put("tomorrow",tomorrow);   下一天
     *         map.put("patientusername",patientusername);   患者名字(账号)
     *       map.put("doctorusername",doctorusername);   医生名字(账号)
     */
    CopyOnWriteArrayList<Patient_Registration_record> selectallPRrecode(ConcurrentMap map);
    /**
     * 根据患者挂号记录的id 查找挂号记录，和病历表
     */
    Patient_Registration_record selectPRRandMRbyid(int id);
    /**
     * 根据挂号id设置已经看诊
     */
    @Update("update patient_registration_record set prralready = 1 where prrid = #{prrid}")
    void updatePrralready(int prrid);


    /**
     * 根据挂号id修改病历里相关信息，主诉等信息
     */
    void updatePRRinf(ConcurrentMap map);
    /**
     * 查找所有疾病信息
     */
    @Select("select * from diagnosis")
     CopyOnWriteArrayList<Diagnosis> selectallDiagnosis();
    /**
     * 返回所有检查项目
     */
     @Select("select * from Inspection_items")
    CopyOnWriteArrayList<Inspection_items> selectallInspection();
    /**
     * 返回所有检验项目
     */
    @Select("select * from Test_items")
    CopyOnWriteArrayList<Test_items> selectalltest();
    /**
     * 把提交得检验项目提交到检验表中   传入 挂号历史积极  t
     */
    @Insert("insert into Inspection(inid,inmrid,indo,inend,innum,intime,inalive,ingivemoney,inused,inwater)" +
            "values(#{inid},#{inmrid},#{indo},#{inend},#{innum},#{intime},#{inalive},#{ingivemoney},#{inused},#{inwater})")
     void insertInspection(Inspection inspection);
    /**
     * 把提交得检查项目提交到检验表中
     */
     @Insert("insert into Test(tid,tmrid,tdo,tend,tnum,ttime,talive,tused,tgivemoney,twater)" +
             "values(#{tid},#{tmrid},#{tdo},#{tend},#{tnum},#{ttime},#{talive},#{tused},#{tgivemoney},#{twater})")
    void insertTest(Test test);

    /**
     * 根据挂号id查找病历id
     */
    @Select("select  mr.mrid  from Patient_Registration_record prr,Medical_record mr " +
            "where prr.prrmrid = mr.mrid and prr.prrid = #{prrid}")
    int selectMRid(int prrid);
    /**
     * 查找检验表有多少条数据
     */
    @Select("select count(*) from Inspection")
     int countInspection();
    /**
     * 查找检查表有多少条数据
     */
    @Select("select count(*) from Test")
    int countTest();
    /**
     * 统计检验医生记录表条数 需要自定义
     */
    @Select("select count(*) from DoctorTestrecord")
     int countDoctorTestrecord();
    /**
     * 插入数据到检验医生记录表
     */
    @Insert("insert into DoctorTestrecord(dtrid,dtrmrid,dtrtid) values" +
            "(#{dtrid},#{dtrmrid},#{dtrtid})")
    void insertDoctorTestrecord(DoctorTestrecode doctorTestrecode);
    /**
     * 统计检查医生记录表得条数
     */
    @Select("select count(*) from DoctorInspectionrecord")
    int countDoctorInspectionrecord();
    /**
     * 插入检查医生记录表中
     */
    @Insert("insert into DoctorInspectionrecord(dirid,dirmrid,diriid) values(#{dirid},#{dirmrid},#{diriid})")
    void insertDoctorInspectionrecord(DocotrInspectionrecord docotrInspectionrecord);
    /**
     * 根据病历id查找 检查，检验，缴费得信息
     */
    CopyOnWriteArrayList<Medical_record> selectallTestAndInsprction(int mrid);
    /**
     * 将检验表相关信息设置为停用
     */
    @Update("update test set talive = 1 where tmrid = #{mrid} and tid = #{id} limit 1")
      void updateTestalive(ConcurrentMap map);
    /**
     * 将检查表相关信息设置为停用
     */
    @Update("update inspection set inalive = 1 where inmrid = #{mrid} and inid = #{id}  limit 1")
    void updateInspectionalive(ConcurrentMap map);


    /**
     * 获取Handle表得个数
     */
    @Select("select count(*) from handle")
    int countHandle();


    /**
     * 提交到Handle表
     */
    @Insert("insert into Handle(hid,hmrid,hdo,hnum,htime,halive,hgivemoney,hused,hwater)" +
            "values(#{hid},#{hmrid},#{hdo},#{hnum},#{htime},#{halive},#{hgivemoney},#{hused},#{hwater})")
    void insertHandle(Handle handle);

    /**
     * 根据mr 病历表查找医生Id
     */
    @Select("select mrdid from medical_record where mrid = #{mrid}")
    int selectDridByMR(int mrid);

    /**
     * 向 Handle Drug Pay 表插入对应记录
     */
    @Insert("insert into DrugOrHandleWithPayRecord(pid,dhid) values(#{pid},#{dhid})")
    void insertDrugOrHandleWithPayRecord(DrugOrHandleWithPayRecord drugOrHandleWithPayRecord);



}
